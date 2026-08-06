# Apple Mock #1 – API Design (Favorites API)

## Problem Statement

Design REST APIs for the Apple Sports Favorites feature.

Users should be able to:

* Follow a team
* Unfollow a team
* View favorite teams
* Receive notifications for their favorite teams (handled by a downstream Notification Service)

---

# Step 1 – Clarifying Questions

Before designing the APIs, ask clarifying questions.

### Questions

* Is this API for a specific sport or should it support multiple sports?
* Are **Follow** and **Favorite** the same concept?
* What types of events should generate notifications?
* Is the user already authenticated?

### Assumptions

* Supports multiple sports.
* Follow and Favorite mean the same thing.
* Users receive notifications for major events:

  * Game start
  * Score updates
  * Lead changes
  * Game end
* User authentication is already handled by the API Gateway using JWT.

---

# Key REST Design Principle

The resource being created is **not the Team**.

The resource is the relationship:

```text
User
    ↓
Favorite Team
```

Instead of designing APIs around actions:

```http
POST /v1/team/{teamId}/follow
POST /v1/team/{teamId}/unfollow
```

Model the API around the resource:

```text
User
    ↓
Favorite Teams
```

This follows REST principles more naturally.

---

# API 1 – Follow Team

```http
POST /v1/users/me/favorite-teams
```

### Request

```json
{
  "teamId": "LAL"
}
```

### Why `/me`?

The user is already authenticated.

The API Gateway validates the JWT and extracts:

```text
userId = JWT.userId
```

Therefore, the client does **not** send `userId` in the request body.

Avoid sending:

```json
{
  "userId": "123",
  "teamId": "LAL"
}
```

because the authenticated user should always come from the JWT.

---

### Successful Response

**First time following**

```http
201 Created
```

Optionally return the created resource.

---

### If the user already follows the team

Two acceptable approaches:

**Preferred**

```http
200 OK
```

Reason:

Following a team is naturally idempotent.

Repeated requests produce the same final state.

Alternative:

```http
409 Conflict
```

indicating the favorite already exists.

For a consumer application like Apple Sports, returning **200 OK** provides a better user experience.

---

# API 2 – Unfollow Team

```http
DELETE /v1/users/me/favorite-teams/{teamId}
```

Example:

```http
DELETE /v1/users/me/favorite-teams/LAL
```

No request body is required.

The server extracts:

* userId → JWT
* teamId → Path Variable

---

### Successful Response

```http
204 No Content
```

---

### What if the team isn't already followed?

Still return:

```http
204 No Content
```

DELETE is naturally idempotent.

Regardless of whether the favorite existed previously, the final state is:

> User is not following the team.

---

# API 3 – Get Favorite Teams

```http
GET /v1/users/me/favorite-teams
```

Example:

```http
GET /v1/users/me/favorite-teams?page=1&size=20
```

Example Response:

```json
{
  "items": [
    {
      "teamId": "LAL",
      "teamName": "Los Angeles Lakers",
      "sport": "NBA"
    },
    {
      "teamId": "NYY",
      "teamName": "New York Yankees",
      "sport": "MLB"
    }
  ],
  "page": 1,
  "size": 20,
  "totalItems": 2
}
```

---

# Pagination

Two options:

## Offset Pagination

Example:

```http
GET /v1/users/me/favorite-teams?page=1&size=20
```

Advantages:

* Simple
* Easy to implement
* Suitable for relatively small and stable datasets

For a Favorites API, offset pagination is usually sufficient.

---

## Cursor Pagination

Better suited for:

* Live feeds
* Frequently changing data
* Infinite scrolling

Since favorite teams are relatively stable, offset pagination is a reasonable choice.

---

# Request Validation

Before creating a favorite:

* Validate the team exists.
* Validate the team is active (optional).
* Check whether the user already follows the team.
* Validate the maximum number of favorite teams has not been exceeded.
* User authentication and authorization are already handled through the JWT.

---

# Error Handling

Suppose PostgreSQL is temporarily unavailable.

Recommended approach:

* Retry a small number of times for transient failures using exponential backoff.
* If the dependency remains unavailable, return:

```http
503 Service Unavailable
```

instead of:

```http
500 Internal Server Error
```

Reason:

* **500** indicates an unexpected application failure.
* **503** indicates that a downstream dependency is temporarily unavailable and the client may retry later.

---

# Notification API?

No additional notification API is required.

Following a team simply records the user's preference.

Later, when a game event occurs:

```text
Game Event
      ↓
Notification Service
      ↓
Find followers of Team
      ↓
Send Push Notification
```

The Notification Service is a downstream consumer and is independent of the Favorites API.

---

# Versioning

Expose APIs using versioned endpoints:

```text
/v1/users/me/favorite-teams
```

This allows future API evolution without breaking existing clients.

---

# Consistent Error Response

Use a consistent error format.

Example:

```json
{
  "code": "TEAM_NOT_FOUND",
  "message": "The specified team does not exist.",
  "requestId": "abc123",
  "timestamp": "2026-08-05T18:30:00Z"
}
```

This simplifies client integration and troubleshooting.

---

# Key Interview Takeaways

* Model APIs around **resources**, not actions.
* Use the authenticated user from the JWT rather than passing `userId` in the request body.
* Prefer RESTful endpoints such as:

  * `POST /v1/users/me/favorite-teams`
  * `DELETE /v1/users/me/favorite-teams/{teamId}`
  * `GET /v1/users/me/favorite-teams`
* Treat follow and unfollow operations as idempotent where appropriate.
* Validate business rules before creating resources.
* Return `503 Service Unavailable` for temporary downstream dependency failures.
* Keep notifications as a downstream concern rather than invoking them directly from the Favorites API.


# Apple Mock #1 - Coding Round

# Live Match Manager

## Problem Statement

Design and implement an in-memory **Live Match Manager**.

Each match has:

* `matchId`
* `homeTeamId`
* `awayTeamId`
* `homeScore`
* `awayScore`
* `status`

The manager should support:

```java
createMatch(matchId, homeTeamId, awayTeamId)

startMatch(matchId)

updateScore(matchId, homeDelta, awayDelta)

finishMatch(matchId)

getMatch(matchId)
```

---

# Functional Requirements

* Store matches in memory.
* Support concurrent access.
* Multiple threads may update different matches simultaneously.
* Multiple threads may update the same match simultaneously.
* Reads are much more frequent than writes.

---

# High-Level Design

```text
                    LiveMatchManager
                           │
                           │
          ConcurrentHashMap<Long, Match>
                           │
          ┌────────────────┴───────────────┐
          │                                │
      Match #1                        Match #2
   (Own ReadWriteLock)            (Own ReadWriteLock)
```

Each match owns its own synchronization.

---

# Class Design

```java
enum MatchStatus {
    NOT_STARTED,
    LIVE,
    FINISHED
}
```

---

```java
public class Match {

    private final long matchId;
    private final long homeTeamId;
    private final long awayTeamId;

    private int homeScore;
    private int awayScore;

    private MatchStatus status;

    private final ReadWriteLock lock =
            new ReentrantReadWriteLock();

    public Match(long matchId,
                 long homeTeamId,
                 long awayTeamId) {

        this.matchId = matchId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;

        this.homeScore = 0;
        this.awayScore = 0;

        this.status = MatchStatus.NOT_STARTED;
    }

    public void startMatch() {

        lock.writeLock().lock();

        try {

            if (status != MatchStatus.NOT_STARTED) {
                throw new IllegalStateException(
                        "Match already started.");
            }

            status = MatchStatus.LIVE;

        } finally {

            lock.writeLock().unlock();
        }
    }

    public void finishMatch() {

        lock.writeLock().lock();

        try {

            if (status != MatchStatus.LIVE) {
                throw new IllegalStateException(
                        "Match is not live.");
            }

            status = MatchStatus.FINISHED;

        } finally {

            lock.writeLock().unlock();
        }
    }

    public void updateScore(int homeDelta,
                            int awayDelta) {

        lock.writeLock().lock();

        try {

            if (status != MatchStatus.LIVE) {
                throw new IllegalStateException(
                        "Match is not live.");
            }

            homeScore += homeDelta;
            awayScore += awayDelta;

        } finally {

            lock.writeLock().unlock();
        }
    }

    public MatchSnapshot snapshot() {

        lock.readLock().lock();

        try {

            return new MatchSnapshot(
                    matchId,
                    homeTeamId,
                    awayTeamId,
                    homeScore,
                    awayScore,
                    status);

        } finally {

            lock.readLock().unlock();
        }
    }
}
```

---

```java
public record MatchSnapshot(
        long matchId,
        long homeTeamId,
        long awayTeamId,
        int homeScore,
        int awayScore,
        MatchStatus status) {
}
```

---

```java
public class LiveMatchManager {

    private final ConcurrentHashMap<Long, Match> matches =
            new ConcurrentHashMap<>();

    public void createMatch(long matchId,
                            long homeTeamId,
                            long awayTeamId) {

        Match match =
                new Match(matchId,
                          homeTeamId,
                          awayTeamId);

        Match existing =
                matches.putIfAbsent(matchId, match);

        if (existing != null) {
            throw new IllegalArgumentException(
                    "Match already exists.");
        }
    }

    public void startMatch(long matchId) {

        Match match = getInternalMatch(matchId);

        match.startMatch();
    }

    public void finishMatch(long matchId) {

        Match match = getInternalMatch(matchId);

        match.finishMatch();
    }

    public void updateScore(long matchId,
                            int homeDelta,
                            int awayDelta) {

        Match match = getInternalMatch(matchId);

        match.updateScore(homeDelta, awayDelta);
    }

    public MatchSnapshot getMatch(long matchId) {

        Match match = getInternalMatch(matchId);

        return match.snapshot();
    }

    private Match getInternalMatch(long matchId) {

        Match match = matches.get(matchId);

        if (match == null) {
            throw new IllegalArgumentException(
                    "Match not found.");
        }

        return match;
    }
}
```

---

# Why ConcurrentHashMap?

Multiple threads may create and retrieve matches concurrently.

A normal HashMap is not thread-safe.

Synchronizing the entire map would introduce coarse-grained locking and reduce throughput.

ConcurrentHashMap allows concurrent access while maintaining thread safety.

---

# Why ReadWriteLock?

Requirements specify:

* Reads are much more frequent than writes.

A ReadWriteLock allows:

* Multiple concurrent readers.
* Exclusive writers.

This improves throughput compared to synchronizing every read.

---

# Why Put the Lock Inside Match?

Each Match owns its own synchronization.

Benefits:

* Fine-grained locking
* Better encapsulation
* Different matches can be updated concurrently
* LiveMatchManager does not need to know synchronization details

---

# Why Return MatchSnapshot Instead of Match?

Returning the mutable Match object exposes internal state.

Callers could modify:

```java
match.setHomeScore(999);
```

without:

* acquiring locks
* validating business rules

Instead, return an immutable MatchSnapshot.

This preserves encapsulation and thread safety.

---

# Complexity

| Operation   | Time |
| ----------- | ---- |
| createMatch | O(1) |
| getMatch    | O(1) |
| updateScore | O(1) |
| startMatch  | O(1) |
| finishMatch | O(1) |

Space Complexity:

```text
O(number of matches)
```

---

# Key Interview Takeaways

* Prefer ConcurrentHashMap for concurrent access.
* Use fine-grained locking.
* Keep synchronization inside the domain object.
* Protect object invariants with locks.
* Return immutable snapshots instead of mutable internal objects.
* Keep critical sections as small as possible.
* Never perform network or I/O operations while holding a lock.


# Apple Mock #1 - Coding Follow-up Questions

These are realistic Apple ICT4 follow-up questions after implementing the Live Match Manager.

---

# 1. Why ConcurrentHashMap instead of HashMap?

**Answer**

HashMap is not thread-safe and may become corrupted under concurrent modifications.

Synchronizing a HashMap would introduce coarse-grained locking.

ConcurrentHashMap provides thread-safe concurrent access with much higher throughput.

---

# 2. Why not synchronize the entire LiveMatchManager?

**Answer**

A global synchronized lock would serialize every operation.

Updates to Match A should not block updates to Match B.

Using a lock inside each Match provides fine-grained locking and allows different matches to be updated concurrently.

---

# 3. Why ReadWriteLock instead of synchronized?

**Answer**

The workload is read-heavy.

ReadWriteLock allows:

* Multiple concurrent readers.
* One exclusive writer.

If both read and write methods were synchronized, every read would wait behind a write and vice versa.

---

# 4. What happens without a lock?

**Answer**

```java
homeScore += 1;
```

is not atomic.

Internally it performs:

```text
Read

↓

Increment

↓

Write
```

Two threads may both read the same value and overwrite each other's result.

This is called a **Lost Update Race Condition**.

---

# 5. Why not use AtomicInteger?

**Answer**

AtomicInteger guarantees atomic updates for a single variable.

A Match consists of multiple related fields:

* homeScore
* awayScore
* status
* lastEventId
* lastUpdatedTime

The business operation updates the Match as a single unit.

ReadWriteLock protects the consistency of the entire object rather than individual variables.

---

# 6. Why return MatchSnapshot instead of Match?

**Answer**

Returning Match exposes mutable internal state.

Callers could bypass:

* synchronization
* validation
* business rules

Returning an immutable MatchSnapshot preserves encapsulation and provides a consistent view of the Match.

---

# 7. Why not call Notification Service while holding the lock?

**Answer**

Keep the critical section as small as possible.

Network calls may:

* Block for hundreds of milliseconds
* Timeout
* Increase lock contention

Update the Match, release the lock, then perform external work.

---

# 8. How would this work in production?

Instead of:

```text
updateScore()

↓

Notification Service

↓

Analytics

↓

Audit
```

Use an event-driven architecture:

```text
updateScore()

↓

Publish SCORE_UPDATED

↓

Kafka

      │
      ├────────► Notification Service
      │
      ├────────► Analytics Service
      │
      └────────► Audit Service
```

Benefits:

* Loose coupling
* Independent scaling
* Failure isolation
* Better resiliency

---

# 9. When would CompletableFuture be appropriate?

Use CompletableFuture when tasks execute:

* Inside the same JVM
* Independently
* Without requiring separate services

Examples:

* Image generation
* Cache refresh
* Parallel API calls
* Independent calculations

Do **not** use CompletableFuture for communication between microservices.

Prefer Kafka or another event bus.

---

# 10. Why keep the critical section small?

Locks reduce concurrency.

Only protect shared mutable state.

Do not include:

* HTTP calls
* Database calls
* Kafka publishing
* Notification sending

inside the locked region.

---

# 11. What if two threads update different matches?

Because each Match owns its own lock:

```text
Thread A
    │
    ▼
Match 1 Lock

----------------------

Thread B
    │
    ▼
Match 2 Lock
```

Both updates proceed concurrently.

---

# 12. What if two threads update the same match?

Both contend for the same write lock.

One thread updates first.

The second waits until the lock is released.

This guarantees atomic score updates.

---

# 13. Interview Keywords

Use these naturally during discussion:

* Fine-grained locking
* Coarse-grained locking
* Thread safety
* Lost update
* Atomic operation
* Read-heavy workload
* Encapsulation
* Immutable snapshot
* Critical section
* Lock contention
* High throughput
* Independent scaling
* Event-driven architecture

---

# Golden Interview Principles

1. Start with the simplest correct solution.
2. Use fine-grained locking where appropriate.
3. Protect object invariants, not just variables.
4. Keep critical sections small.
5. Never perform I/O while holding a lock.
6. Return immutable objects to callers.
7. Prefer event-driven communication across services.
8. Use CompletableFuture only for asynchronous work within the same JVM.
	
	
# Apple Mock #1 – Production Follow-ups

After completing the coding exercise, Apple interviewers often extend the discussion into production system design. These questions test whether you can evolve an in-memory solution into a scalable distributed system.

---

# Question 1

## We now deploy 10 application instances behind a Load Balancer.

```text
                Load Balancer
                     │
      ┌──────────────┼──────────────┐
      │              │              │
      ▼              ▼              ▼
   Instance 1    Instance 2    Instance 3
      │              │              │
  In-Memory      In-Memory      In-Memory
   Match Map      Match Map      Match Map
```

A client sends:

```java
updateScore(matchId = 100)
```

to Instance 1.

A second later another client sends:

```java
getMatch(matchId = 100)
```

to Instance 2.

### What's wrong?

Each application instance maintains its own in-memory state.

Example:

```text
Instance 1

Match 100
Score = 10-8

--------------------------

Instance 2

Match 100
Doesn't exist
```

The read request may return stale or missing data.

The application is no longer consistent.

---

## Important Observation

`ConcurrentHashMap`

and

`ReadWriteLock`

only protect data **inside a single JVM**.

They do **not** synchronize state across multiple application instances.

This is one of the biggest differences between application-level concurrency and distributed systems.

---

## Production Solution

Externalize the authoritative game state.

```text
Client

↓

Load Balancer

↓

API Service (Stateless)

↓

Redis
```

Each API instance reads and writes to the same shared Redis cluster.

The API instances become stateless and can scale horizontally.

---

## Even Better Production Architecture

Instead of allowing every API instance to update Redis:

```text
Provider

↓

Ingestion API

↓

Kafka

↓

Game State Consumer

↓

Redis

↓

API Service
```

Only the Game State Consumer updates Redis.

The API Service only performs reads.

This creates a clear separation of responsibilities.

---

# Question 2

Suppose Redis stores the game state.

Two API instances simultaneously execute:

```text
updateScore(+1)
```

for the same match.

How do we prevent a lost update?

---

# Option 1 – Distributed Lock

Each application instance acquires a distributed lock before updating Redis.

```text
API Instance

↓

Acquire Lock(matchId)

↓

Update Redis

↓

Release Lock
```

Advantages:

* Prevents concurrent updates
* Guarantees exclusive access

Disadvantages:

* Extra network round trips
* Lock acquisition latency
* Lock expiration handling
* Increased operational complexity

Suitable when multiple writers are unavoidable.

---

# Option 2 – Preferred Solution (Single Writer)

Rather than allowing every application instance to update Redis:

```text
API

↓

Kafka

↓

Partition by MatchId

↓

Single Game State Consumer

↓

Redis
```

Because Kafka partitions events by `matchId`:

```text
Match 100

↓

Partition 5

↓

One Consumer

↓

Redis Update
```

Only one consumer processes events for a given match.

Benefits:

* Preserves ordering
* Eliminates lost updates
* No distributed locks required
* Simpler architecture
* Better scalability

This is the preferred production design.

---

# Distributed Lock vs Single Writer

| Distributed Lock                          | Kafka Single Writer                |
| ----------------------------------------- | ---------------------------------- |
| Multiple writers coordinate               | One logical writer per match       |
| Additional lock management                | No lock management                 |
| Higher latency                            | Lower latency                      |
| More operational complexity               | Simpler architecture               |
| Useful when multiple writers are required | Preferred for event-driven systems |

---

# Interview Answer

> "If multiple application instances update Redis directly, we need a coordination mechanism to prevent lost updates. One option is a distributed lock keyed by `matchId`. However, for a high-throughput event-driven platform like Apple Sports, I would avoid multiple writers altogether. I'd partition Kafka by `matchId` so that a single Game State Consumer owns updates for each match and writes the latest state to Redis. This preserves ordering, avoids distributed locks, and scales more naturally."

---

# Key Interview Takeaways

### Single JVM

Use:

* `synchronized`
* `ReadWriteLock`
* `AtomicInteger`

These solve concurrency **within one process**.

---

### Multiple JVMs

Application-level locks no longer work.

Options:

* Distributed lock
* Shared database
* Shared Redis
* Better architecture

---

### Large Distributed Systems

Prefer **single ownership** over distributed locking.

Examples:

```text
Kafka Partition

↓

Single Consumer

↓

Redis
```

Instead of coordinating many concurrent writers.

This reduces complexity, improves scalability, and preserves event ordering.

---

# Golden Rule

> **When moving from one JVM to many JVMs, stop thinking about Java locks and start thinking about ownership, coordination, and architecture.**


# Apple Sports System Design

# Professional Athlete Leaderboard Service

---

# Problem Statement

Design a **Leaderboard Service** for Apple Sports that ranks **professional athletes**.

The service should support multiple sports and maintain separate leaderboards for different leagues and seasons.

Examples:

* NBA 2026 Points Leaderboard
* Premier League 2026 Goals Leaderboard
* ATP Rankings
* Wimbledon Aces Leaderboard

---

# Requirement Clarification

## Clarifying Questions

### 1. Is the leaderboard generic or sport-specific?

The system should support **multiple sports**, but each leaderboard belongs to a specific league and season.

Examples:

* NBA Season 2026
* Premier League 2026
* Wimbledon 2026

---

### 2. How quickly should rankings update?

Leaderboards should be updated in **near real time**.

Target:

* Within **1 second** of a score/statistics update.

---

### 3. How are athletes ranked?

Ranking is maintained **per leaderboard**.

Examples:

* NBA 2026 Top Scorers
* Premier League Goal Scorers
* ATP Rankings

Each leaderboard ranks athletes using a numeric score.

---

# Functional Requirements

The system should:

1. Rank professional athletes.
2. Support multiple sports, leagues and seasons.
3. Maintain independent leaderboards for each league and season.
4. Allow users to:

   * View Top N athletes.
   * View an athlete's current rank.
   * View athletes surrounding a given athlete (±N positions).
5. Automatically update rankings whenever athlete statistics change.

---

# Non-Functional Requirements

* Near real-time updates (within 1 second)
* Read-heavy workload
* Low read latency (<100 ms)
* High write throughput
* Highly available
* Fault tolerant
* Horizontally scalable

---

# Capacity Estimates

Assumptions:

```text
20 Sports

1000 Leagues

4000 Seasons

20,000 Professional Athletes

100,000 Read Requests/sec

10,000 Leaderboard Updates/sec (Peak)
```

These numbers are not exact.

Their purpose is to justify technology choices later in the design.

Important observation:

```text
Reads (100K/sec)

>>

Writes (10K/sec)
```

This indicates:

* Cache aggressively
* Optimize read latency
* Precompute rankings

---

# REST APIs

## 1. Get Top N Athletes

```http
GET /v1/leagues/{leagueId}/seasons/{seasonId}/leaderboard?limit=100
```

Optional:

```http
GET /v1/leagues/{leagueId}/seasons/{seasonId}/leaderboard?startRank=1&limit=100
```

Example Response

```json
[
  {
    "rank": 1,
    "athleteId": 101,
    "name": "Player A",
    "score": 2150
  }
]
```

---

## 2. Get Athlete Rank

```http
GET /v1/leagues/{leagueId}/seasons/{seasonId}/athletes/{athleteId}/rank
```

Example Response

```json
{
  "athleteId": 101,
  "rank": 17,
  "score": 2150
}
```

---

## 3. Get Athletes Around an Athlete

```http
GET /v1/leagues/{leagueId}/seasons/{seasonId}/athletes/{athleteId}/neighbors?before=10&after=10
```

Example

If athlete rank is 250:

```text
240
241
242
...
249

250 ← Athlete

251
252
...
260
```

Using query parameters makes the API flexible.

Examples:

```http
?before=5&after=20

?before=0&after=50
```

---

# Why Query Parameters?

The resource is:

```text
Neighbors of Athlete 123
```

Parameters such as:

* before
* after
* limit
* sort

modify **how much** of the resource is returned.

Therefore they belong as **query parameters**, not path parameters.

---

# Why Not Include Sport in the URL?

Preferred API:

```http
GET /v1/leagues/{leagueId}/seasons/{seasonId}/leaderboard
```

Reason:

A globally unique `leagueId` already determines the sport.

Example:

```text
NBA

↓

Basketball
```

Including:

```http
/sports/{sportId}/...
```

would be redundant.

If `leagueId` were only unique within a sport, then including `sportId` would make sense.

---

# Interview Tips

## Functional Requirements

Think in terms of user actions:

* View
* Retrieve
* Rank
* Update

---

## Non-Functional Requirements

Think in terms of system qualities:

* Latency
* Throughput
* Scalability
* Availability
* Fault Tolerance

---

## REST API Design

### Path Parameters

Identify the resource.

Example:

```http
/leagues/{leagueId}

/athletes/{athleteId}
```

### Query Parameters

Modify how the resource is returned.

Examples:

```http
?limit=100

?before=10&after=10

?sort=desc
```

---

# Overall Progress

Completed:

* ✅ Requirement Clarification
* ✅ Functional Requirements
* ✅ Non-Functional Requirements
* ✅ Capacity Estimation
* ✅ REST API Design

Next:

* High-Level Architecture
* Data Model
* Redis Data Structures
* Kafka Event Flow
* Scaling
* Failure Handling
* Trade-offs


# Apple Sports System Design

# Professional Athlete Leaderboard Service (Part 2)

# High-Level Architecture

## End-to-End Flow

```text
Sports Provider
        │
        ▼
Webhook API
        │
        ▼
Ingestion Service
 ├─ Authenticate Provider
 ├─ Validate Request
 ├─ Rate Limit
 ├─ Normalize Payload
 └─ Publish Event to Kafka
        │
        ▼
      Kafka
        │
        ▼
Leaderboard Service
        │
        ├──────────────► Redis Sorted Sets
        │
        └──────────────► Snapshot Service
                              │
                              ▼
                          Cassandra

                 ▲
                 │
           Leaderboard API
                 │
                 ▼
              Mobile App
```

---

# Sports Provider Payload

The provider sends **raw game events**, not leaderboard updates.

Example:

```json
{
  "eventId": "e123",
  "matchId": 100,
  "leagueId": "NBA",
  "seasonId": "2026",
  "playerId": 30,
  "eventType": "THREE_POINTER",
  "points": 3,
  "timestamp": "..."
}
```

Possible fields include:

* eventId
* matchId
* leagueId
* seasonId
* playerId
* eventType
* eventDescription
* timestamp / sequenceNumber

---

# Ingestion Service Responsibilities

The ingestion service should remain lightweight.

Responsibilities:

* Authenticate sports provider
* Validate mandatory fields
* Rate limiting
* Normalize vendor-specific payloads into a canonical format
* Publish events to Kafka
* Return **202 Accepted**

Returning immediately keeps the ingestion path lightweight while Kafka absorbs traffic spikes.

---

# Why Kafka?

Kafka serves as the event backbone.

Benefits:

* Decouples ingestion from downstream processing.
* Buffers traffic spikes.
* Supports replay.
* Provides durability.
* Allows independent scaling of downstream consumers.

---

# What Events Should Kafka Contain?

There are two valid architectures.

---

## Option A (Simpler)

```text
Sports Provider

↓

GAME_EVENTS

↓

Leaderboard Service
```

The Leaderboard Service interprets game events itself.

Example:

```text
THREE_POINTER

↓

+3 season points

↓

Update leaderboard
```

Advantages:

* Simpler architecture.
* Suitable for interviews or smaller systems.

Disadvantages:

* Leaderboard Service must understand sport-specific rules.

---

## Option B (Preferred Production Design)

```text
Sports Provider

↓

GAME_EVENTS

↓

Statistics Service

↓

PLAYER_STATS_UPDATED

↓

Leaderboard Service
```

Advantages:

* Clear separation of responsibilities.
* Leaderboard Service no longer understands basketball, football, cricket, etc.
* Easier to add new sports.

Preferred for production systems.

---

# GAME_EVENTS vs PLAYER_STATS_UPDATED

## GAME_EVENTS

Represents what happened during a match.

Example:

```text
Steph Curry hit a three-pointer.
```

Payload:

```json
{
  "eventType": "THREE_POINTER",
  "playerId": 30,
  "points": 3
}
```

---

## PLAYER_STATS_UPDATED

Represents the athlete's latest cumulative statistics.

Example:

```json
{
  "playerId": 30,
  "leagueId": "NBA",
  "seasonId": "2026",
  "metric": "POINTS",
  "seasonPoints": 2501
}
```

Notice:

Game Event:

```text
+3
```

Statistics Event:

```text
Season Points = 2501
```

The Leaderboard Service only needs the second event.

---

# Why Separate Statistics from Leaderboards?

Statistics Service owns:

* Points
* Goals
* Assists
* Rebounds
* Steals
* Blocks
* Games Played

Leaderboard Service owns:

* Rankings

Each service has a single responsibility.

This keeps business logic isolated from ranking logic.

# Apple Sports System Design

# Professional Athlete Leaderboard Service (Part 3)

# Statistics Service

The Statistics Service is the authoritative owner of player statistics.

Example:

```text
Steph Curry

Season Points : 2501
Assists       : 642
Rebounds      : 301
```

Its responsibility is to process raw game events and maintain cumulative statistics.

---

# Backend Database

A relational database such as PostgreSQL is a good fit.

Example schema:

```sql
PlayerSeasonStats
-----------------------------------------
leagueId
seasonId
playerId

points
assists
rebounds
steals
blocks

updatedTimestamp
```

Primary Key:

```text
(leagueId, seasonId, playerId)
```

---

# Why PostgreSQL?

Player statistics are relational.

Each athlete has multiple attributes:

* points
* assists
* rebounds
* steals
* blocks

Updating statistics is straightforward.

Example:

```sql
UPDATE PlayerSeasonStats
SET points = points + 3
WHERE playerId = 30
AND seasonId = 2026;
```

---

# Leaderboard Service

The Leaderboard Service consumes:

```text
PLAYER_STATS_UPDATED
```

It does **not** understand:

* Three-pointers
* Goals
* Penalties
* Free throws

It simply receives:

```text
Player 30

Season Points = 2501
```

and updates the leaderboard.

---

# Redis Sorted Sets

Each leaderboard is represented by its own Redis Sorted Set.

Examples:

```text
NBA_2026_POINTS

NBA_2026_ASSISTS

PremierLeague_2026_GOALS

ATP_2026_RANKINGS
```

Each Sorted Set stores:

```text
PlayerId

↓

Score
```

Example:

```text
NBA_2026_POINTS

Curry     → 2501

Durant    → 2400

Jokic     → 2100
```

Redis automatically keeps the members ordered by score.

---

# What is ZADD?

`ZADD` inserts or updates a member in a Redis Sorted Set.

Example:

```text
ZADD NBA_2026_POINTS

2501 Curry
```

If Curry later reaches:

```text
2504
```

we simply execute:

```text
ZADD NBA_2026_POINTS

2504 Curry
```

Redis updates the score and automatically reorders the leaderboard.

No application-side sorting is required.

---

# Why Redis Sorted Set?

A leaderboard fundamentally requires four operations:

## 1. Update Score

Example:

```text
2501 → 2504
```

Redis efficiently updates the member's score.

---

## 2. Retrieve Top N

Example:

Top 100 athletes.

Redis retrieves the highest-ranked members directly.

---

## 3. Retrieve Athlete Rank

Example:

Player 30

↓

Rank 17

Redis provides rank lookup efficiently.

---

## 4. Retrieve Neighbors

Example:

Show athletes ranked ±10 around Player 30.

Redis supports range queries by rank, making this operation efficient.

---

# Why Not Redis Hash?

A Redis Hash stores:

```text
PlayerId

↓

Score
```

but does **not** maintain ordering.

To retrieve the Top 100:

1. Read all players.
2. Sort them in application memory.
3. Return the first 100.

This becomes inefficient for frequent leaderboard queries.

---

# Why Not PostgreSQL?

PostgreSQL can execute:

```sql
ORDER BY score DESC
LIMIT 100;
```

However, under very high read traffic (for example, 100K reads/sec), repeatedly sorting and querying a relational database places unnecessary load on it.

Redis is optimized for serving these read-heavy ranking queries.

---

# Why Not Cassandra?

Cassandra excels at:

* High write throughput
* Low write latency
* Horizontal scalability

However, it is not optimized for maintaining globally sorted rankings.

It is better suited for snapshots and historical persistence than serving live leaderboards.

---

# Snapshot Strategy

Redis should not be the only copy of the leaderboard.

Periodically:

* Every X minutes, or
* Every N updates

persist leaderboard snapshots to Cassandra.

Benefits:

* Fast recovery after Redis failures.
* No need to replay an entire season of events.

---

# Final Architecture

```text
Sports Provider
        │
        ▼
GAME_EVENTS
        │
        ▼
Statistics Service
        │
 Update PostgreSQL
        │
        ▼
PLAYER_STATS_UPDATED
        │
        ▼
Leaderboard Service
        │
        ├────────► Redis Sorted Sets
        │
        └────────► Cassandra Snapshots
        │
        ▼
Leaderboard API
        │
        ▼
Mobile Applications
```

---

# Interview Notes

For a 45-minute Apple system design interview, you do **not** need to design the Statistics Service unless the interviewer asks.

A reasonable assumption is:

> "Assume another service publishes `PLAYER_STATS_UPDATED` events. My Leaderboard Service consumes those events and maintains the rankings."

This keeps the discussion focused on the leaderboard system itself while still demonstrating clean separation of responsibilities.

# Apple Sports Interview Notes

# Remembering Sports Events & Architecture

---

# The Key Principle

**Do not memorize every event for every sport.**

Apple is **not** testing sports knowledge.

They are testing whether you can design a system that processes **generic game events**.

Think in **three layers**:

```text
Raw Game Events
        │
        ▼
Statistics Service
        │
PLAYER_STATS_UPDATED
        │
        ▼
Leaderboard Service
```

---

# Layer 1 — Raw Game Events

These are provider-specific events.

## Basketball

### Scoring Events

Mnemonic:

> **Three Tall Friends**

```text
Three Pointer
Two Pointer
Free Throw
```

---

### Player Statistics

Mnemonic:

> **RASB ("RazBee")**

```text
Rebound
Assist
Steal
Block
```

---

### Match Events

Mnemonic:

> **Foxes Take Quarters Gently**

```text
Foul
Timeout
Quarter End
Game End
```

---

## Soccer

### Scoring

```text
Goal
Penalty
```

---

### Player Statistics

Mnemonic:

> **Always Yell Red**

```text
Assist
Yellow Card
Red Card
```

---

### Match Events

Mnemonic:

> **Half Full**

```text
Half Time
Full Time
```

---

## Tennis

### Scoring

Mnemonic:

> **Ace Plays Daily**

```text
Ace
Point Won
Double Fault
```

---

### Match Events

Mnemonic:

> **Good Sets Matter**

```text
Game Won
Set Won
Match Won
```

---

## Cricket

### Scoring

Mnemonic:

> **Runs Fly With Sixes**

```text
Run
Four
Wide
Six
```

---

### Player Statistics

Mnemonic:

> **We Catch Runs**

```text
Wicket
Catch
Run Out
```

---

### Match Events

```text
Over End
Innings End
Match End
```

---

# What Does the Sports Provider Send?

The provider sends **raw game events**, not leaderboard updates.

Example:

```json
{
  "eventId": "e123",
  "matchId": 100,
  "leagueId": "NBA",
  "seasonId": "2026",
  "playerId": 30,
  "eventType": "THREE_POINTER",
  "eventValue": 3,
  "timestamp": "...",
  "sequenceNumber": 12345
}
```

Generic fields:

* eventId
* matchId
* leagueId
* seasonId
* playerId
* eventType
* eventValue
* timestamp
* sequenceNumber

---

# Statistics Service

The Statistics Service owns **cumulative player statistics**.

Example:

```text
Steph Curry

Season Points : 2501
Assists       : 642
Rebounds      : 301
```

Responsibilities:

* Consume GAME_EVENTS
* Apply sport-specific business rules
* Update player statistics
* Publish PLAYER_STATS_UPDATED

Example:

```json
{
  "playerId": 30,
  "leagueId": "NBA",
  "seasonId": "2026",
  "metric": "POINTS",
  "seasonScore": 2501
}
```

Notice:

Game Event

```text
THREE_POINTER (+3)
```

becomes

Statistics Event

```text
Season Points = 2501
```

---

# Leaderboard Service

The Leaderboard Service **does not understand sports rules**.

It simply receives:

```text
Player 30

Season Points = 2501
```

and updates the leaderboard.

---

# Redis Sorted Sets

One Sorted Set per leaderboard.

Examples:

```text
NBA_2026_POINTS

NBA_2026_ASSISTS

PremierLeague_2026_GOALS

ATP_2026_RANKINGS
```

Each Sorted Set stores:

```text
PlayerId  →  Score
```

Example:

```text
Curry   → 2501

Durant  → 2400

Jokic   → 2100
```

Updating:

```text
ZADD NBA_2026_POINTS

2504 Curry
```

Redis automatically updates Curry's score and reorders the leaderboard.

---

# Architecture Summary

```text
Sports Provider
        │
        ▼
GAME_EVENTS
        │
        ▼
Statistics Service
        │
(Update PostgreSQL)
        │
        ▼
PLAYER_STATS_UPDATED
        │
        ▼
Leaderboard Service
        │
        ▼
Redis Sorted Sets
        │
        ▼
Leaderboard API
        │
        ▼
Apple Sports App
```

---

# Interview Takeaways

### Think in Layers

```text
Game Event

↓

Statistics

↓

Leaderboard
```

---

### Responsibilities

| Component           | Responsibility                            |
| ------------------- | ----------------------------------------- |
| Sports Provider     | Sends raw game events                     |
| Ingestion Service   | Authentication, validation, normalization |
| Kafka               | Event backbone and decoupling             |
| Statistics Service  | Computes cumulative player statistics     |
| Leaderboard Service | Maintains rankings                        |
| Redis               | Low-latency leaderboard reads             |
| Cassandra           | Periodic leaderboard snapshots            |
| API Service         | Serves leaderboard requests               |

---

# Golden Rule

During the interview, you only need to say something like:

> "The provider publishes sport-specific game events such as scoring events, player statistics events, and match lifecycle events. These are normalized into a canonical event format and published to Kafka. A Statistics Service computes cumulative player statistics and publishes `PLAYER_STATS_UPDATED` events. The Leaderboard Service consumes those events and updates Redis Sorted Sets, which are used to serve low-latency leaderboard queries."

This answer demonstrates an understanding of **clean service boundaries**, **event-driven architecture**, and **technology selection**, which is what Apple interviewers are evaluating—not your knowledge of basketball or soccer rules.

# Apple Sports System Design

# Statistics Service Data Model

---

# Question

How should the Statistics Service store player statistics?

Should it use:

* One table?
* Multiple tables?
* One table per sport?

---

# What is the Statistics Service?

The Statistics Service is the **source of truth** for cumulative player statistics.

Examples:

```text id="lfx2jv"
Steph Curry

Season Points : 2501
Assists       : 642
Rebounds      : 301
```

It consumes:

```text id="u5d4oq"
GAME_EVENTS
```

and publishes:

```text id="0mk1d2"
PLAYER_STATS_UPDATED
```

---

# Option 1 — Single Table (Simple)

```sql id="7wyhkj"
PlayerSeasonStats
---------------------------------------------------------
leagueId
seasonId
playerId

points
assists
rebounds
steals
blocks

gamesPlayed

updatedTimestamp
```

Primary Key

```text id="jl3qqd"
(leagueId, seasonId, playerId)
```

Example:

| League | Season | Player | Points | Assists | Rebounds |
| ------ | ------ | ------ | ------ | ------- | -------- |
| NBA    | 2026   | Curry  | 2501   | 642     | 301      |

Updating after a three-pointer:

```sql id="tqvt1o"
UPDATE PlayerSeasonStats
SET points = points + 3
WHERE playerId = 30
AND leagueId = 'NBA'
AND seasonId = 2026;
```

### Advantages

* Very simple.
* Easy to query.
* Good when designing only one sport.

---

# Problem with Multiple Sports

Basketball statistics:

```text id="24df6r"
Points

Rebounds

Blocks
```

Soccer statistics:

```text id="6pfem4"
Goals

Yellow Cards

Red Cards
```

Tennis statistics:

```text id="ax3tkr"
Aces

Double Faults
```

A single relational table quickly fills with many columns that are irrelevant for most sports.

---

# Option 2 — Separate Table Per Sport

```text id="obcpr8"
BasketballSeasonStats

SoccerSeasonStats

TennisSeasonStats

CricketSeasonStats
```

Each table contains only the statistics relevant to that sport.

Advantages:

* Cleaner schema.
* No unnecessary NULL columns.
* Easy to evolve sport-specific metrics.

---

# Option 3 — Generic Metric Model (Flexible)

Instead of storing one column per statistic:

```sql id="03h08r"
PlayerSeasonStats

leagueId
seasonId
playerId

metricType
metricValue
```

Example:

| Player  | Metric   | Value |
| ------- | -------- | ----: |
| Curry   | POINTS   |  2501 |
| Curry   | ASSISTS  |   642 |
| Curry   | REBOUNDS |   301 |
| Haaland | GOALS    |    27 |
| Alcaraz | ACES     |   184 |

Advantages:

* Works for every sport.
* Easy to introduce new metrics.
* No schema changes for new statistics.

---

# Which Option Should I Choose?

For a real production platform supporting many sports:

The internal implementation depends on product requirements.

Any of the above approaches could be appropriate.

---

# For an Apple Leaderboard Interview

**Do not spend interview time designing the Statistics Service schema.**

The Statistics Service is an **upstream dependency**.

Assume it already exists and publishes cumulative player statistics.

Example event:

```json id="17whs8"
{
  "playerId": 30,
  "leagueId": "NBA",
  "seasonId": "2026",
  "metric": "POINTS",
  "value": 2501
}
```

The Leaderboard Service simply consumes this event.

---

# Separation of Responsibilities

```text id="84xby2"
Sports Provider
        │
        ▼
GAME_EVENTS
        │
        ▼
Statistics Service
```

Responsibilities:

* Understand sport-specific rules.
* Compute cumulative statistics.
* Persist player statistics.
* Publish PLAYER_STATS_UPDATED.

---

```text id="ec3vva"
Leaderboard Service
```

Responsibilities:

* Consume PLAYER_STATS_UPDATED.
* Update Redis Sorted Sets.
* Serve leaderboard APIs.

It **does not** understand:

* Three Pointers
* Goals
* Aces
* Penalties

It only receives:

```text id="yajrzl"
Player 30

POINTS = 2501
```

and updates the ranking.

---

# Interview Answer

If asked:

> **How does the Statistics Service store data?**

A good answer is:

> "Since the Leaderboard Service only depends on cumulative player statistics, I would treat the Statistics Service as an upstream system. Internally it can use a schema appropriate for each sport or a generic metric model. Rather than designing that service in detail, I assume it publishes `PLAYER_STATS_UPDATED` events containing the player ID, league, season, metric, and updated value."

---

# Key Interview Lesson

Do **not** design every upstream service.

Instead, clearly define service boundaries.

The Leaderboard interview is evaluating your ability to design:

* Ranking
* Event processing
* Redis data structures
* Scalability
* Low-latency reads

—not the complete sports analytics platform.


# Apple Sports System Design

# Leaderboard Service (Redis Data Model)

---

# Goal

Design the Redis data model for storing leaderboards.

Assume we have one leaderboard:

```text
NBA 2026 Top Scorers
```

---

# Redis Data Model

Every leaderboard is represented by a single logical collection.

It consists of:

```text
Key
Member
Score
```

---

## Key

The key uniquely identifies a leaderboard.

Format:

```text
leagueId:seasonId:metric
```

Examples:

```text
NBA:2026:POINTS

NBA:2026:ASSISTS

NBA:2026:REBOUNDS

PREMIER_LEAGUE:2026:GOALS

ATP:2026:ACES
```

The key tells us **which leaderboard** we are updating or querying.

---

## Member

Each member represents one athlete.

```text
Member = playerId
```

Example:

```text
30

7

15
```

The member is simply the athlete's unique identifier.

Player profile information (name, team, image, etc.) can be fetched from another service if required.

---

## Score

The score represents the cumulative statistic used for ranking.

Examples:

```text
2501 Points

642 Assists

27 Goals

184 Aces
```

The score determines the athlete's position in the leaderboard.

---

# Mental Model

Think of every leaderboard as:

```text
Key

NBA:2026:POINTS

────────────────────────────

Member          Score

Curry           2501

Durant          2400

Jokic           2100

...
```

The leaderboard maintains one entry for every athlete.

---

# Updating the Leaderboard

Assume the Leaderboard Service receives:

```json
{
    "playerId":30,
    "leagueId":"NBA",
    "seasonId":"2026",
    "metric":"POINTS",
    "value":2504
}
```

The update flow is:

### Step 1

Construct the leaderboard key.

```text
NBA:2026:POINTS
```

---

### Step 2

Locate the corresponding leaderboard.

---

### Step 3

Locate the athlete within that leaderboard.

```text
Member = playerId
```

---

### Step 4

Update the athlete's cumulative score.

```text
Score = 2504
```

Redis automatically maintains the ordering after the score changes.

The application does **not** need to re-sort the leaderboard.

---

# Why Include Metric?

Initially, we considered:

```text
Key

leagueId:seasonId
```

Example:

```text
NBA:2026
```

Problem:

The same league contains multiple leaderboards.

Example:

```text
NBA 2026

↓

Points

Assists

Rebounds

Steals
```

Using only:

```text
NBA:2026
```

would mix all these leaderboards together.

Therefore the metric must be included.

Final key:

```text
leagueId:seasonId:metric
```

---

# API Design Improvement

Original API

```http
GET /v1/leagues/{leagueId}/seasons/{seasonId}/leaderboard?limit=100
```

Question:

Top 100 **what**?

* Points?
* Assists?
* Rebounds?

The API does not specify the leaderboard.

---

## Improved API

Option 1

```http
GET /v1/leagues/{leagueId}/seasons/{seasonId}/leaderboard?metric=POINTS&limit=100
```

Option 2 (Preferred)

```http
GET /v1/leagues/{leagueId}/seasons/{seasonId}/leaderboards/POINTS?limit=100
```

Why?

The metric becomes part of the resource being requested.

The API explicitly identifies the leaderboard.

---

# Supporting Multiple Sports

The same API works across all sports.

Basketball

```text
NBA:2026:POINTS
```

Soccer

```text
PREMIER_LEAGUE:2026:GOALS
```

Tennis

```text
ATP:2026:ACES
```

The Leaderboard Service does **not** contain sport-specific logic.

It simply uses the metric supplied in the request or event.

---

# Why Not Default to Points?

Using a default such as:

```text
POINTS
```

creates ambiguity.

For example:

```text
NBA Leaderboard
```

Could mean:

* Points
* Assists
* Rebounds

Similarly, in soccer, while **Goals** may be the most common leaderboard, there can also be:

* Goals
* Assists
* Clean Sheets
* Saves
* Yellow Cards

Making the metric explicit keeps the API generic and extensible.

---

# End-to-End Read Flow

User request:

```http
GET /v1/leagues/NBA/seasons/2026/leaderboards/POINTS?limit=100
```

↓

Construct key:

```text
NBA:2026:POINTS
```

↓

Locate the leaderboard.

↓

Retrieve the Top N athletes.

↓

Return the response to the client.

---

# Key Takeaways

Think of a leaderboard using three concepts:

```text
Key

↓

Identifies the leaderboard

----------------------------

Member

↓

Identifies the athlete

----------------------------

Score

↓

Determines the ranking
```

This simple model supports:

* Multiple sports
* Multiple leagues
* Multiple seasons
* Multiple leaderboard metrics

without changing the overall architecture.


GET /v1/leagues/{leagueId}/seasons/{seasonId}/leaderboards/{metric}?limit=100

where metric is an enum such as:

POINTS
GOALS
ASSISTS
REBOUNDS
ACES
SAVES
CLEAN_SHEETS