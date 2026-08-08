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

# Apple Sports System Design

# Leaderboard Service – Failure Handling (Part 1)

---

# Failure Scenario 1

## Duplicate Kafka Messages

Kafka provides **at-least-once delivery**.

Therefore, the same event may be delivered multiple times.

Example:

```json
{
  "playerId": 30,
  "leagueId": "NBA",
  "seasonId": "2026",
  "metric": "POINTS",
  "value": 2504,
  "eventId": "evt-123"
}
```

Suppose the Leaderboard Service receives this event twice.

---

# Will the Leaderboard Become Incorrect?

No.

Our design is naturally **idempotent**.

Why?

Because the Statistics Service publishes the **latest cumulative score**, not the score delta.

Example:

```
Current Score

2501
```

Statistics Service publishes:

```
Season Points = 2504
```

Leaderboard updates:

```
Player 30 → 2504
```

If the same event is delivered again:

```
Player 30 → 2504
```

Nothing changes.

The leaderboard remains correct.

---

# Why This Works

Good event:

```json
{
    "playerId":30,
    "value":2504
}
```

Bad event:

```json
{
    "playerId":30,
    "pointsScored":3
}
```

If the Leaderboard Service processed:

```
+3
```

twice:

```
2501

↓

2504

↓

2507 ❌
```

The ranking would become incorrect.

Publishing the **final cumulative value** makes updates idempotent.

---

# Why Do We Still Include eventId?

Although eventId is not required for leaderboard correctness, it is still useful for:

* Logging
* Tracing
* Auditing
* Debugging
* Future non-idempotent workflows

---

# Failure Scenario 2

## Out-of-Order Events

Example:

Event A

```
Score = 2504

Sequence = 105
```

Event B

```
Score = 2501

Sequence = 103
```

Due to retries or network delays, Event A arrives first.

Later, Event B arrives.

If the service blindly updates Redis:

```
2504

↓

2501 ❌
```

The leaderboard becomes stale.

---

# Solution

Each event should include an ordering field.

Preferred:

```
sequenceNumber
```

Alternative:

```
updatedTimestamp
```

A sequence number is generally preferred because:

* It provides deterministic ordering.
* It does not rely on synchronized clocks.
* It avoids clock-skew issues across distributed systems.

---

# Processing Logic

Current Redis state:

```
Player 30

Score = 2504

Sequence = 105
```

Incoming event:

```
Score = 2501

Sequence = 103
```

Comparison:

```
103 < 105
```

↓

Discard the event.

The leaderboard remains correct.

---

# Interview Answer

> Since the Statistics Service publishes cumulative values, stale events should not overwrite newer data. Each event includes an ordering field such as a sequence number. Before updating the leaderboard, the Leaderboard Service compares the incoming sequence number with the latest processed sequence for that player. If the event is older, it is ignored.

---

# Where Should We Store the Latest Sequence?

A natural choice is **Redis**, because the Leaderboard Service already uses Redis on every update.

Advantages:

* Low latency
* No additional database lookup
* Fast validation before updating the leaderboard

---

# Possible Redis Design

## Structure 1

Leaderboard

```
Key

NBA:2026:POINTS
```

Contents:

```
PlayerId        Score

30              2504

7               2400

15              2100
```

---

## Structure 2

Latest Sequence

```
Key

NBA:2026:POINTS:SEQUENCE
```

Contents:

```
PlayerId        Sequence

30              105

7               98

15              110
```

Processing flow:

```
Receive Event

↓

Read latest sequence

↓

Incoming Sequence > Stored Sequence ?

        YES
         │
         ▼
Update leaderboard

Update stored sequence

        NO
         │
         ▼
Discard event
```

---

# Can We Store the Sequence in the Same Redis Sorted Set?

This was an important design discussion.

A Redis Sorted Set conceptually stores:

```
Member

↓

Score
```

Example:

```
Player30 → 2504
```

The score determines the ordering.

However, we also want to store:

```
Player30

Score = 2504

Sequence = 105
```

A Sorted Set provides only **one score per member**.

If we replaced the score with the sequence number:

```
Player30 → 105
```

the leaderboard ordering would become incorrect.

Similarly, combining both values into one numeric field would break ranking semantics.

Therefore, the sequence should be stored separately.

---

# Better Design

Use two Redis data structures:

### Redis Sorted Set

Responsible only for rankings.

```
Player30 → 2504
```

---

### Redis Hash (or another metadata structure)

Responsible for player metadata.

```
Player30

Sequence = 105
```

This cleanly separates:

* Ranking data
* Metadata

---

# New Consistency Question

Using two Redis structures introduces another challenge.

Suppose we:

1. Update the leaderboard.
2. Crash before updating the sequence.

Now:

Leaderboard:

```
Player30 → 2504
```

Sequence:

```
Player30 → 104
```

The next delivery of the same event may be processed incorrectly.

This introduces the need for **atomic updates** across both Redis structures.

Possible solutions (to be covered during Redis deep dive):

* Redis transactions
* Lua scripts
* Other atomic update mechanisms

---

# Key Takeaways

* Design events to be **idempotent** by publishing cumulative values instead of deltas.
* Use **sequence numbers** to detect stale or out-of-order events.
* Store the latest processed sequence in Redis for fast validation.
* Keep ranking data and metadata separate.
* Multiple Redis structures may require **atomic updates** to maintain consistency.

# Apple Sports System Design

# Leaderboard Service – Failure Recovery & Scaling

---

# Failure Scenario 3

## Redis Crash

Assume the Redis cluster crashes and all in-memory leaderboard data is lost.

Remaining systems are still available:

* PostgreSQL (Statistics Service)
* Kafka
* Cassandra (Leaderboard Snapshots)

---

# Recovery Strategy

## Option 1 (Preferred)

### Redis Replica

```text
          Primary Redis
                ❌
                 │
         Automatic Failover
                 │
                 ▼
            Redis Replica
```

Promote a healthy replica to the new primary.

Advantages:

* Fast recovery
* Minimal downtime
* No replay required
* Preferred for single-node failures

---

## Option 2

### Full Redis Cluster Loss

If the entire Redis cluster is lost:

### Step 1

Restore the latest leaderboard snapshot from Cassandra.

Example:

```text
Snapshot Time

10:00 AM
```

---

### Step 2

Replay Kafka events that occurred after the snapshot.

```text
10:00 Snapshot

↓

10:01

10:02

10:03
```

Replay only the missing events.

Eventually Redis catches up to the latest leaderboard state.

---

# Why Not Replay the Entire Kafka Topic?

Suppose a season contains:

```text
50 Million Events
```

Replaying everything would be very slow.

Snapshots significantly reduce recovery time.

---

# Snapshot Frequency Trade-off

Suppose snapshots are taken every:

```text
30 Minutes
```

Worst case:

Redis crashes after 29 minutes.

Need to replay:

```text
29 Minutes of Kafka Events
```

Trade-off:

Frequent snapshots:

* Faster recovery
* More writes to Cassandra

Less frequent snapshots:

* Lower storage/write cost
* Longer replay during recovery

---

# Snapshot Metadata

When storing a snapshot, also persist Kafka checkpoint information.

Example:

```text
Snapshot

LeagueId      : NBA

SeasonId      : 2026

Metric        : POINTS

SnapshotTime  : 10:00 AM

KafkaPartition : 7

KafkaOffset    : 4523118
```

---

# Why Store Kafka Offset?

During recovery:

1. Restore leaderboard snapshot.
2. Resume Kafka consumption from the stored offset.

Example:

```text
Restore Snapshot

↓

Kafka Offset

4523118

↓

Resume Replay

4523119
```

This avoids replaying already processed events.

---

# Why Offset Instead of Timestamp?

Kafka guarantees ordering using:

```text
Partition

↓

Offset
```

Timestamps may:

* Be identical
* Have clock skew
* Not guarantee ordering

Offsets provide deterministic replay.

---

# Multiple Kafka Partitions

If the topic has multiple partitions:

Store an offset for each partition.

Example:

```text
Partition 0 → Offset 1051

Partition 1 → Offset 8842

Partition 2 → Offset 12771

Partition 3 → Offset 592
```

Recovery resumes independently for each partition.

---

# Scaling

Suppose tonight is the NBA Finals.

Traffic suddenly becomes:

```text
Reads

100K/sec

↓

5 Million/sec
```

---

# Scaling Stateless Services

Both the API layer and Leaderboard Service are stateless.

Therefore they can scale horizontally behind a Load Balancer.

```text
               Load Balancer
                     │
          ┌──────────┼──────────┐
          ▼          ▼          ▼
        API        API        API

          ┌──────────┼──────────┐
          ▼          ▼          ▼
    Leaderboard  Leaderboard  Leaderboard
      Service      Service      Service
```

---

# Redis Scaling

Redis supports two different scaling strategies.

These solve different problems.

---

# 1. Redis Replicas

Purpose:

**Read Scaling**

Example:

```text
                 Primary
                     │
          ┌──────────┴──────────┐
          ▼                     ▼
      Replica 1            Replica 2
```

Characteristics:

* Primary receives writes.
* Replicas serve read traffic.
* Every replica contains the complete dataset.

Benefits:

* Higher read throughput.
* High availability.
* Faster failover.

---

# 2. Redis Cluster

Purpose:

**Capacity Scaling**

Instead of storing every leaderboard on one Redis node:

```text
             Redis Cluster

      ┌────────┬────────┬────────┐
      ▼        ▼        ▼
    Node A   Node B   Node C
```

Example:

Node A

```text
NBA:2026:POINTS

NBA:2026:ASSISTS
```

Node B

```text
NFL:2026:YARDS
```

Node C

```text
ATP:2026:ACES
```

Each node stores only part of the dataset.

Benefits:

* Increased storage capacity.
* Higher write throughput.
* Horizontal scaling.

---

# Redis Replicas vs Redis Cluster

| Redis Replicas                | Redis Cluster                            |
| ----------------------------- | ---------------------------------------- |
| Same dataset on every node    | Dataset partitioned across nodes         |
| Solves read scaling           | Solves memory/capacity scaling           |
| Primary handles writes        | Each shard handles writes for its keys   |
| Reads distributed to replicas | Requests routed to the appropriate shard |

---

# Can We Use Both?

Yes.

Production systems often combine both.

```text
               Redis Cluster

         Shard A (Primary)
                 │
              Replica

         Shard B (Primary)
                 │
              Replica

         Shard C (Primary)
                 │
              Replica
```

The cluster partitions data across shards.

Each shard has replicas for:

* Read scaling
* High availability

---

# Hot Leaderboards

Example:

```text
NBA:2026:POINTS
```

During the NBA Finals, this single leaderboard may receive millions of read requests.

Read traffic can be distributed across the replicas of the shard that owns this leaderboard.

---

# Are Leaderboards Already Precomputed?

Yes.

Every scoring event updates Redis immediately.

```text
PLAYER_STATS_UPDATED

↓

Leaderboard Service

↓

Redis Sorted Set
```

When users request:

```http
GET /leaderboards/POINTS
```

The service simply retrieves an already maintained ranking.

It does **not** compute rankings on demand.

This is one of the primary reasons for using Redis Sorted Sets.

---

# Interview Summary

A strong interview answer:

> "The API layer and Leaderboard Service are stateless, so I would scale them horizontally behind a load balancer. I would use a Redis Cluster to partition leaderboard data across multiple nodes as the dataset grows, and Redis replicas to scale read traffic and provide high availability. For Redis recovery, I would first fail over to a replica if available. If the entire Redis cluster is lost, I would restore the latest leaderboard snapshot from Cassandra and replay Kafka events starting from the stored offsets associated with that snapshot."

# Apple Sports System Design

# Leaderboard Service – Observability & Monitoring

---

# Goal

Suppose users report:

> **"The leaderboard is not updating."**

As the on-call engineer, the objective is to quickly identify **where** in the event pipeline the failure or latency is occurring.

The most effective approach is to follow the **entire request path**.

```text id="9pb8z6"
Sports Provider
        │
        ▼
Ingestion Service
        │
        ▼
Kafka
        │
        ▼
Statistics Service
        │
        ▼
Leaderboard Service
        │
        ▼
Redis
        │
        ▼
Leaderboard API
        │
        ▼
Apple Sports App
```

---

# Step 1 – Sports Provider

First verify that upstream providers are sending events correctly.

Monitor:

* Incoming event rate
* Stale timestamps
* Failed webhook deliveries
* Authentication failures
* Provider-side outages

Questions to ask:

* Are we receiving events?
* Are events delayed?
* Has the event rate suddenly dropped?

---

# Step 2 – Ingestion Service

Ensure events are being accepted and published successfully.

Monitor:

* Requests/sec
* P95 / P99 latency
* HTTP 5xx errors
* Request timeouts
* CPU utilization
* Memory utilization
* Rate-limited requests

Questions:

* Is the service overloaded?
* Are requests timing out?
* Is event validation failing?

---

# Step 3 – Kafka

Kafka is the event backbone.

The most important metric is:

## Consumer Lag

High consumer lag indicates downstream services cannot keep up.

Monitor:

* Consumer lag
* Messages/sec
* Consumer health
* Broker health
* Producer errors

---

## Scaling During High Lag

First response:

Scale the Leaderboard Service consumers horizontally (up to the current number of Kafka partitions).

Adding Kafka partitions is typically a **capacity-planning decision**, not the immediate response during an incident.

---

# Step 4 – Statistics Service

Verify player statistics are being updated correctly.

Monitor:

* Processing latency
* Error rate
* CPU
* Memory
* Database latency
* Connection pool utilization

Questions:

* Are statistics being computed?
* Is PostgreSQL healthy?
* Is the service falling behind?

---

# Step 5 – Redis

Redis serves live leaderboard queries.

Monitor:

* Read latency
* Write latency
* Memory usage
* Evictions
* Replication lag
* Failovers
* Cluster health

Questions:

* Is Redis responding?
* Are replicas synchronized?
* Is memory exhausted?

---

# Step 6 – Leaderboard API

Even if Redis is healthy, users may still experience failures if the API layer is unhealthy.

Monitor:

* Requests/sec
* Error rate
* HTTP 4xx / 5xx
* P95 latency
* P99 latency
* Load balancer health

Questions:

* Can clients reach the service?
* Is API latency increasing?
* Are instances healthy?

---

# Why P95 and P99?

Average latency can hide problems.

Example:

| Requests |   Latency |
| -------- | --------: |
| 95%      |     20 ms |
| 5%       | 2 seconds |

Average latency still appears acceptable.

P95 and P99 reveal how the slowest requests are performing.

For user-facing systems like Apple Sports, tail latency is often more important than the average.

---

# Dashboard Strategy

Create dashboards for each stage:

## Sports Provider

* Incoming event rate
* Provider latency
* Authentication failures

---

## Ingestion Service

* Throughput
* Error rate
* P95/P99 latency
* CPU
* Memory

---

## Kafka

* Consumer lag
* Broker health
* Messages/sec
* Failed publishes

---

## Statistics Service

* Processing latency
* Error rate
* Database latency
* Connection pool usage

---

## Redis

* Read latency
* Write latency
* Memory usage
* Replication lag
* Cluster health

---

## Leaderboard API

* Request rate
* Error rate
* P95/P99 latency
* Load balancer health

---

# Alerting

Examples of production alerts:

* Kafka consumer lag exceeds threshold.
* Redis memory exceeds 80–90%.
* Redis replication lag increases.
* API P99 latency exceeds SLA.
* Ingestion Service error rate increases.
* Statistics Service processing latency spikes.
* Sports Provider event rate suddenly drops.

These alerts allow engineers to respond before users notice stale or delayed leaderboards.

---

# Interview Answer

> "When troubleshooting stale leaderboards, I would trace the entire event pipeline rather than focusing on a single component. I'd first verify that the Sports Provider is delivering fresh events, then check the Ingestion Service's throughput, latency, and error rates. Next I'd inspect Kafka consumer lag to determine whether downstream processing is delayed. I'd then validate the Statistics Service and PostgreSQL health, followed by Redis latency, memory usage, and replication status. Finally, I'd examine the Leaderboard API's request rate, error rate, and P95/P99 latency. Monitoring every stage of the pipeline helps isolate the bottleneck quickly and reduces mean time to recovery (MTTR)."

---

# Key Takeaways

* Always debug the **entire event pipeline**.
* Kafka consumer lag is one of the most valuable health indicators.
* Monitor **P95/P99 latency**, not just averages.
* Scale stateless services horizontally behind a load balancer.
* Instrument every service with metrics, dashboards, and alerts to quickly identify production issues.

# Fantasy Leaderboard Service

## Functional Requirements

1. Users can draft fantasy teams by selecting real-world athletes participating in a sport (e.g., NBA). Multiple users may own the same athlete.

2. Users can create private fantasy leagues or join platform-managed public leagues.

3. Each fantasy league maintains an independent leaderboard for its participating fantasy teams.

4. As real-world games progress, athlete scoring events should update the corresponding fantasy team scores based on predefined fantasy scoring rules.

5. The Fantasy Leaderboard Service should maintain a near real-time ranking of fantasy teams within each fantasy league.

6. The service should support the following operations:

   * Retrieve the Top **N** fantasy teams within a league.
   * Retrieve the current rank of a user's fantasy team within a league.
   * Retrieve the neighboring fantasy teams (users immediately above and below a given user) within a league.

---

# Non-Functional Requirements

1. Fantasy leaderboards should reflect real-world scoring events within **1 second**.

2. Support approximately **50 million registered users**.

3. Support approximately **10 million fantasy leagues** (private and public).

4. A user may participate in up to **100 fantasy leagues**.

5. Handle approximately **1 million leaderboard read requests per second** during peak sporting events.

6. Handle approximately **10,0000 fantasy score updates per second** during peak live games. Peak lives games 10000 events/sec QPS(queries per second)

7. The system should be highly available and fault tolerant.

8. The system should scale horizontally to support increasing numbers of users, leagues, and live sporting events.

9. Ensure leaderboard rankings remain consistent despite duplicate or out-of-order event delivery.


# Fantasy Leaderboard Service

## Requirements, Scale and APIs

---

# Problem Statement

Design a **Fantasy Leaderboard Service** that maintains real-time rankings of users participating in fantasy sports leagues based on the live performance of real-world athletes.

Unlike the professional athlete leaderboard, this service ranks **fantasy teams (users)** instead of athletes.

---

# Functional Requirements

1. Users can draft fantasy teams by selecting real-world athletes participating in a sport (e.g., NBA). Multiple users may own the same athlete.

2. Users can create private fantasy leagues or join platform-managed public leagues.

3. Each fantasy league maintains an independent leaderboard for its participating fantasy teams.

4. As real-world games progress, athlete scoring events should update the corresponding fantasy team scores based on predefined fantasy scoring rules.

5. The Fantasy Leaderboard Service should maintain a near real-time ranking of fantasy teams within each fantasy league.

6. Support the following operations:

* Retrieve the Top **N** fantasy teams within a league.
* Retrieve the current rank of a user's fantasy team.
* Retrieve neighboring fantasy teams immediately above and below a given user.

---

# Non-Functional Requirements

1. Fantasy leaderboards should reflect real-world scoring events within **1 second**.

2. Support approximately **50 million registered users**.

3. Support approximately **10 million fantasy leagues**.

4. A user may participate in up to **100 fantasy leagues**.

5. Handle approximately **1 million read requests/sec** during peak sporting events.

6. Handle approximately **100,000 fantasy leaderboard updates/sec** during live games.

7. High availability.

8. Fault tolerance.

9. Horizontal scalability.

10. Ensure leaderboard correctness despite duplicate or out-of-order event delivery.

---

# Capacity Estimation

Registered Users

```text
50 Million
```

Fantasy Leagues

```text
10 Million
```

Maximum leagues per user

```text
100
```

Peak Read Throughput

```text
1 Million Requests/sec
```

Peak Write Throughput

```text
100,000 Fantasy Score Updates/sec
```

---

# Why Is the Write Rate Higher Than the Athlete Leaderboard?

Professional Athlete Leaderboard

```text
Player Scores

↓

Update One Athlete
```

Fantasy Leaderboard

```text
Player Scores

↓

Many Fantasy Users Own That Player

↓

Update Thousands of Fantasy Teams

↓

Update Thousands of Leaderboard Entries
```

One real-world scoring event can update many fantasy teams.

This **fan-out** is the primary scaling challenge of the Fantasy Leaderboard Service.

---

# Service Boundary

This interview focuses on the **Fantasy Leaderboard Service**, not the entire Fantasy Sports Platform.

Assume the following already exist:

* Fantasy League Service
* User Service
* Draft/Roster Management Service

These services are responsible for:

* Creating fantasy leagues
* Joining leagues
* Drafting fantasy teams
* Managing league membership

The Leaderboard Service only maintains and serves rankings.

---

# Leaderboard APIs

## Get Top N Teams

```http
GET /v1/fantasy-leagues/{leagueId}/leaderboard?limit=100
```

Returns the highest-ranked fantasy teams within a league.

---

## Get User Rank

```http
GET /v1/fantasy-leagues/{leagueId}/users/{userId}/rank
```

Returns the current ranking of a user's fantasy team.

---

## Get Neighboring Teams

```http
GET /v1/fantasy-leagues/{leagueId}/users/{userId}/neighbors?before=10&after=10
```

Returns users immediately above and below the specified user.

---

# APIs Not Owned By This Service

These belong to the Fantasy League Service and are considered out of scope:

```http
POST /v1/fantasy-leagues
```

Create a fantasy league.

---

```http
POST /v1/fantasy-leagues/{leagueId}/teams
```

Create a fantasy team and draft players.

These operations are assumed to already exist and are not part of the Leaderboard Service.

---

# Key Difference From the Athlete Leaderboard

Professional Athlete Leaderboard

```text
Sports Event

↓

Statistics Service

↓

Athlete Leaderboard
```

Fantasy Leaderboard

```text
Sports Event

↓

Statistics Service

↓

Fantasy Scoring Service

↓

Fantasy Leaderboard
```

The additional **Fantasy Scoring Service** maps athlete performance to fantasy team scores before updating the leaderboard.

---

# Key Takeaways

* The service ranks **fantasy teams**, not athletes.
* Every fantasy league has an independent leaderboard.
* One real-world scoring event may update thousands of fantasy teams.
* The Leaderboard Service focuses only on maintaining and querying rankings.
* League creation, drafting, and roster management belong to separate services.

# Fantasy Leaderboard Service

## High-Level Architecture Discussion

---

# High-Level Architecture

Unlike the professional athlete leaderboard, the Fantasy Leaderboard Service ranks **fantasy teams (users)** instead of athletes.

A real-world sporting event must first be translated into fantasy points before the leaderboard can be updated.

---

# Simplified Architecture (Preferred)

For a system whose primary responsibility is fantasy sports:

```text
Sports Provider
        │
        ▼
Ingestion Service
        │
        ▼
Kafka (RAW_GAME_EVENT)
        │
        ▼
Fantasy Scoring Service
        │
        ▼
Kafka (FANTASY_SCORE_UPDATED)
        │
        ▼
Fantasy Leaderboard Service
        │
        ▼
Redis
```

This keeps the architecture focused and avoids introducing unnecessary services.

---

# Step-by-Step Flow

Suppose Steph Curry hits a three-pointer.

The Sports Provider sends:

```json
{
    "matchId":100,
    "playerId":30,
    "eventType":"THREE_POINTER"
}
```

---

## Step 1

The Ingestion Service

Responsibilities:

* Authenticate sports provider
* Validate payload
* Normalize vendor-specific payloads
* Publish a `RAW_GAME_EVENT` to Kafka
* Return HTTP 202

---

## Step 2

Fantasy Scoring Service

Consumes:

```text
RAW_GAME_EVENT
```

Responsibilities:

* Determine fantasy scoring rule

Example:

```text
THREE_POINTER

↓

+3 Fantasy Points
```

* Determine which fantasy teams own the player
* Update each affected fantasy team's cumulative score
* Persist updated fantasy scores
* Publish a new event:

```text
FANTASY_SCORE_UPDATED
```

Example:

```json
{
    "leagueId":123,
    "userId":456,
    "fantasyScore":108
}
```

---

## Step 3

Fantasy Leaderboard Service

Consumes:

```text
FANTASY_SCORE_UPDATED
```

Responsibilities:

* Update the corresponding Redis Sorted Set
* Re-rank the fantasy teams
* Serve leaderboard queries

Redis key example:

```text
FantasyLeague:123
```

Members:

```text
UserId

↓

Fantasy Score
```

---

# Why Two Kafka Event Types?

The system intentionally uses two event types.

## Event 1

Produced by the Ingestion Service.

```text
RAW_GAME_EVENT
```

Example:

```json
{
    "playerId":30,
    "eventType":"THREE_POINTER"
}
```

This represents a real-world sporting event.

---

## Event 2

Produced by the Fantasy Scoring Service.

```text
FANTASY_SCORE_UPDATED
```

Example:

```json
{
    "leagueId":123,
    "userId":456,
    "fantasyScore":108
}
```

This represents an updated fantasy team score.

The Leaderboard Service only consumes fantasy score updates.

---

# Why Not Update Redis Directly?

Separating scoring from leaderboard maintenance provides clear service boundaries.

Fantasy Scoring Service:

* Applies fantasy scoring rules
* Updates fantasy team scores
* Publishes score updates

Fantasy Leaderboard Service:

* Maintains rankings
* Updates Redis
* Serves leaderboard APIs

Each service has a single responsibility.

---

# Statistics Service Discussion

An alternative architecture introduces a dedicated Statistics Service.

```text
RAW_GAME_EVENT

↓

Statistics Service

↓

PLAYER_STATS_UPDATED

↓

Fantasy Scoring Service

↓

FANTASY_SCORE_UPDATED

↓

Leaderboard Service
```

In this design, the Statistics Service maintains authoritative player statistics and publishes `PLAYER_STATS_UPDATED` events.

---

# Do We Need a Statistics Service?

Not necessarily.

It depends on the scope.

---

## Option A (Preferred)

No Statistics Service.

Use:

```text
RAW_GAME_EVENT

↓

Fantasy Scoring Service
```

Advantages:

* Simpler architecture
* Lower latency
* Fewer services
* Fewer Kafka topics
* Easier to explain in an interview

Recommended when designing only the Fantasy Leaderboard Service.

---

## Option B

Dedicated Statistics Service.

Advantages:

* Single source of truth for player statistics
* Reusable by:

  * Athlete Leaderboards
  * Player Profiles
  * Team Statistics
  * Live Box Scores
  * Fantasy Sports
  * Analytics

Recommended when designing an entire Sports Platform instead of only the Fantasy Leaderboard Service.

---

# Key Design Decision

For the Fantasy Leaderboard interview problem, prefer the simpler architecture:

```text
Sports Provider
        │
        ▼
Ingestion Service
        │
        ▼
Kafka (RAW_GAME_EVENT)
        │
        ▼
Fantasy Scoring Service
        │
        ▼
Kafka (FANTASY_SCORE_UPDATED)
        │
        ▼
Fantasy Leaderboard Service
        │
        ▼
Redis
```

This design satisfies all requirements while keeping service responsibilities clear and avoiding unnecessary complexity.

---

# Key Takeaways

* Two Kafka event types are sufficient:

  * `RAW_GAME_EVENT`
  * `FANTASY_SCORE_UPDATED`
* The Fantasy Scoring Service converts real-world sports events into fantasy team scores.
* The Fantasy Leaderboard Service only maintains rankings and serves leaderboard queries.
* Introduce a dedicated Statistics Service only if the platform contains multiple products that require centralized player statistics.


# Fantasy Leaderboard Service

## Data Model, Persistence & Fantasy Score Update Pipeline

---

# Core Entities

## User

```text
User
-----
userId
```

---

## FantasyLeague

```text
FantasyLeague
-------------
leagueId
name
sport
seasonId
```

---

## FantasyTeam

One fantasy team belongs to one user within one fantasy league.

```text
FantasyTeam
-----------
teamId
userId
leagueId
```

---

## FantasyRoster

Stores the athletes drafted by each fantasy team.

```text
FantasyRoster
-------------
teamId
playerId
```

Example:

| Team  | Player |
| ----- | ------ |
| Team1 | Curry  |
| Team1 | Jokic  |
| Team2 | Curry  |
| Team3 | Tatum  |

This table answers:

> **Which fantasy teams own a given player?**

---

## FantasyTeamScore

Stores the durable cumulative fantasy score for every fantasy team.

```text
FantasyTeamScore
----------------
teamId
leagueId
score
lastUpdated
```

---

# Why Not Store a FantasyLeaderboard Table?

The leaderboard is **not** the source of truth.

The source of truth is:

```text
FantasyTeamScore
```

The leaderboard is simply a sorted representation of those scores.

Redis Sorted Sets maintain the ranking efficiently.

Therefore, a separate relational `FantasyLeaderboard` table is unnecessary.

---

# Persistence

## PostgreSQL

System of Record

Stores:

* FantasyLeague
* FantasyTeam
* FantasyRoster
* FantasyTeamScore

Reasons:

* Highly relational data
* ACID transactions
* Durable storage
* Millions of rows are well within PostgreSQL's capabilities

---

## Redis

Used for two purposes.

### 1. Player Ownership Cache

Fantasy rosters change infrequently but are read for every scoring event.

Instead of querying PostgreSQL repeatedly:

```sql
SELECT teamId
FROM FantasyRoster
WHERE playerId = 30;
```

cache the mapping in Redis.

Example:

```text
Key

player:30

Value

Team1
Team2
Team5
Team18
...
```

When Curry scores:

```text
Redis

↓

Return all fantasy teams owning Curry
```

This significantly reduces database reads.

---

### 2. Leaderboard

Redis Sorted Sets maintain rankings.

Example:

```text
Key

FantasyLeague:123

Member

teamId

Score

fantasyScore
```

Redis automatically maintains ordering as scores change.

---

# Fantasy Score Update Flow

Suppose Steph Curry hits a three-pointer.

Sports Provider publishes:

```text
RAW_GAME_EVENT
```

Example:

```json
{
    "playerId":30,
    "eventType":"THREE_POINTER"
}
```

---

## Step 1

Fantasy Scoring Service consumes the event.

Lookup:

```text
player:30
```

Redis returns:

```text
Team1

Team2

Team3

...
```

These are all fantasy teams that own Curry.

---

## Step 2

For every affected fantasy team:

Read current score:

```text
100
```

Apply fantasy scoring rule:

```text
+3
```

New score:

```text
103
```

Persist:

```text
FantasyTeamScore

103
```

---

## Step 3

Immediately publish:

```text
FANTASY_SCORE_UPDATED
```

Example:

```json
{
    "leagueId":10,
    "teamId":1,
    "score":103
}
```

The event carries the **new cumulative fantasy score**.

The Leaderboard Service does **not** need to query PostgreSQL again.

---

## Step 4

Fantasy Leaderboard Service consumes:

```text
FANTASY_SCORE_UPDATED
```

Updates Redis:

```text
ZADD

Key

FantasyLeague:10

Member

Team1

Score

103
```

Redis automatically reorders the leaderboard.

---

# Why Doesn't the Leaderboard Service Read PostgreSQL?

The Fantasy Scoring Service has already:

1. Calculated the new score.
2. Persisted it.
3. Published the updated score.

Therefore the Leaderboard Service can directly update Redis using the event.

This avoids an unnecessary database read.

---

# Comparison With Athlete Leaderboard

## Athlete Leaderboard

One sports event updates one leaderboard entry.

```text
Player Scores

↓

Leaderboard Service

↓

Redis
```

Simple one-to-one update.

---

## Fantasy Leaderboard

One sports event affects many fantasy teams.

```text
Player Scores

↓

Find Teams Owning Player

↓

Update Fantasy Scores

↓

Leaderboard Updates
```

This introduces a **fan-out** problem.

---

# Architecture Option 1 (Preferred Initial Design)

Keep the architecture simple.

```text
Sports Provider
        │
        ▼
Ingestion Service
        │
        ▼
Kafka (RAW_GAME_EVENT)
        │
        ▼
Fantasy Scoring Service
        │
        ▼
Thread Pool (Parallel Updates)
        │
        ▼
PostgreSQL
        │
        ▼
Kafka (FANTASY_SCORE_UPDATED)
        │
        ▼
Fantasy Leaderboard Service
        │
        ▼
Redis
```

The Fantasy Scoring Service uses an internal thread pool to process multiple team score updates in parallel.

Advantages:

* Simpler architecture
* Fewer Kafka topics
* Lower latency
* Easier to explain

---

# Architecture Option 2 (Scaling Fan-Out)

If a single scoring event affects an extremely large number of fantasy teams, introduce another Kafka stage.

```text
RAW_GAME_EVENT

↓

Fantasy Scoring Service

↓

Kafka (TEAM_SCORE_UPDATE)

↓

Score Update Workers

↓

PostgreSQL

↓

Kafka (FANTASY_SCORE_UPDATED)

↓

Fantasy Leaderboard Service

↓

Redis
```

Benefits:

* Independent scaling of score-update workers
* Better backpressure handling
* Durable work queue
* Suitable for extremely large fan-out scenarios

---

# Which Architecture Should Be Presented?

Start with **Option 1**.

It satisfies the current requirements with minimal complexity.

If the interviewer later increases the scale (e.g., millions of fantasy teams affected by a single player event), evolve the design to **Option 2**.

This demonstrates sound engineering judgment by introducing complexity only when justified.

---

# Key Takeaways

* PostgreSQL is the source of truth for fantasy team scores.
* Redis caches player ownership mappings to avoid repeated database lookups.
* Redis Sorted Sets maintain fantasy leaderboard rankings.
* The Leaderboard Service never recalculates scores—it simply updates Redis from `FANTASY_SCORE_UPDATED` events.
* Unlike the athlete leaderboard, the fantasy leaderboard introduces a **fan-out** problem because one player event can affect many fantasy teams.
* Prefer a simpler thread-pool-based design initially, and introduce an additional Kafka stage only if fan-out becomes a bottleneck.

# Fantasy Leaderboard Service

## Reliability, Idempotency & Transactional Outbox

---

# Idempotency

Kafka provides **at-least-once delivery**, so the same event may be delivered multiple times.

Example:

```text
eventId = abc123

Team1

+3 Fantasy Points
```

If processed twice:

```text
100

↓

103

↓

106 ❌
```

The same fantasy score should never be applied twice.

---

# Solution

Every event carries a unique **eventId**.

Example:

```json
{
    "eventId":"abc123",
    "teamId":1,
    "leagueId":10,
    "fantasyPoints":3
}
```

Before processing an event, the consumer performs an idempotency check.

---

# Consumer-Side Deduplication

Store processed event IDs in Redis.

Example:

```text
processed:abc123
```

Processing flow:

1. Check whether the eventId already exists.
2. If it exists, ignore the event.
3. Otherwise:

```text
SETNX processed:abc123
TTL = 24 hours
```

Then process the event normally.

Redis is a good choice because:

* Extremely fast lookup
* Automatic expiration using TTL
* No need to store processed IDs permanently

---

# Why the Leaderboard Service Is Naturally Idempotent

The Fantasy Leaderboard Service does **not** receive:

```text
+3
```

Instead it receives the final cumulative score.

Example:

```json
{
    "teamId":1,
    "leagueId":10,
    "score":103
}
```

It executes:

```text
ZADD FantasyLeague:10 Team1 103
```

If the same event is delivered twice:

```text
ZADD FantasyLeague:10 Team1 103
```

Redis simply overwrites the existing value.

The ranking remains unchanged.

Therefore the Leaderboard Service is naturally idempotent.

The critical place where deduplication is required is **before updating PostgreSQL**.

---

# Failure Scenario

Suppose the Fantasy Score Worker performs:

```text
Update PostgreSQL
```

John's score changes:

```text
100

↓

103
```

The database transaction commits successfully.

Immediately afterwards the worker crashes before publishing:

```text
FANTASY_SCORE_UPDATED
```

Result:

PostgreSQL

```text
John = 103
```

Redis

```text
John = 100
```

The database and leaderboard become inconsistent.

---

# Solution — Transactional Outbox Pattern

Instead of only updating the score, perform both operations within the same database transaction.

```sql
BEGIN;

UPDATE FantasyTeamScore
SET score = 103
WHERE teamId = 1;

INSERT INTO Outbox
(
    eventType,
    payload,
    status
)
VALUES
(
    'FANTASY_SCORE_UPDATED',
    ...,
    'PENDING'
);

COMMIT;
```

Now both operations succeed or fail together.

Either:

* Score update commits.
* Outbox record commits.

Or neither commits.

This guarantees consistency.

---

# Outbox Publisher

A separate Outbox Publisher continuously reads pending records.

```text
FantasyTeamScore

↓

Outbox

↓

Outbox Publisher

↓

Kafka

↓

Fantasy Leaderboard Service
```

After successful publication, the outbox record is marked as published.

---

# Crash Recovery

Suppose the worker crashes immediately after the database transaction commits.

The Outbox table already contains:

```text
FANTASY_SCORE_UPDATED
```

The Outbox Publisher eventually publishes the event to Kafka.

The Fantasy Leaderboard Service receives it and updates Redis.

No score updates are lost.

---

# Why Not Publish Directly to Kafka?

PostgreSQL and Kafka do not participate in the same distributed transaction.

This creates the classic failure scenario:

```text
UPDATE PostgreSQL

↓

Crash

↓

Kafka Event Never Published
```

The Transactional Outbox Pattern solves this problem by ensuring the database update and event creation occur atomically.

---

# CDC Alternative

Instead of polling the Outbox table, many production systems use:

```text
PostgreSQL

↓

Transaction Log

↓

Debezium (CDC)

↓

Kafka
```

Debezium monitors the database transaction log.

Whenever a new Outbox record is committed, it automatically publishes the corresponding Kafka event.

Benefits:

* No polling
* Lower latency
* Better scalability
* Widely adopted in event-driven architectures

---

# Reliability Summary

## Idempotency

* Every event carries a unique `eventId`.
* Consumers perform deduplication using Redis (`SETNX` + TTL).
* Prevents duplicate score updates.

---

## Leaderboard Updates

* Events contain the **final cumulative score**, not score deltas.
* Updating Redis with the same score multiple times is naturally idempotent.

---

## Reliable Event Publishing

* Use the Transactional Outbox Pattern.
* Update `FantasyTeamScore` and insert an Outbox record in the same database transaction.
* A dedicated Outbox Publisher (or CDC using Debezium) reliably publishes events to Kafka.

This guarantees that whenever a fantasy score is successfully committed to PostgreSQL, the corresponding leaderboard update event will eventually be delivered, even if the application crashes immediately after the transaction commits.


# Fantasy Leaderboard Service

## PostgreSQL Scaling & Redis Recovery

---

# Scaling PostgreSQL

The Fantasy Scoring Service performs frequent updates to the `FantasyTeamScore` table.

As traffic grows, PostgreSQL should be scaled incrementally before introducing distributed complexity.

---

## 1. Vertical Scaling

Initially scale a single PostgreSQL instance by increasing:

* CPU
* Memory
* Faster NVMe SSDs

This is the simplest scaling strategy and should always be considered before sharding.

---

## 2. Connection Pooling

Do not allow thousands of workers to create thousands of database connections.

Use a connection pool such as **HikariCP** (Spring Boot's default connection pool).

```text id="s8y8kc"
Fantasy Score Workers
          │
          ▼
       HikariCP
          │
          ▼
     PostgreSQL
```

Benefits:

* Reuses existing database connections.
* Reduces connection establishment overhead.
* Prevents exhausting PostgreSQL's maximum connection limit.

---

## 3. Batch Updates

Instead of executing one SQL update per fantasy team, process updates in batches.

Example:

Instead of:

```sql id="3pgkh5"
UPDATE Team1;
UPDATE Team2;
...
UPDATE Team500;
```

Execute batched updates within a transaction.

Benefits:

* Fewer network round trips.
* Lower transaction overhead.
* Better write throughput.

---

## 4. Table Partitioning

Partition large tables such as:

```text id="3ecyr0"
FantasyTeamScore
```

by:

```text id="laj0dk"
leagueId
```

Benefits:

* Smaller indexes.
* Faster index lookups.
* Faster maintenance operations.
* Improved write performance.

This is **table partitioning inside PostgreSQL**, not distributed sharding.

---

## 5. Database Sharding

When a single PostgreSQL instance becomes insufficient, shard by:

```text id="zpmw1x"
leagueId
```

Example:

```text id="2jizqh"
leagueId % N
```

Each PostgreSQL instance stores a subset of fantasy leagues.

Advantages:

* Even distribution of write load.
* No cross-shard score updates because each fantasy team belongs to one league.
* Natural shard key aligned with the domain model.

---

## 6. Archive Old Leagues

Completed seasons and inactive fantasy leagues no longer receive writes.

Move them to colder storage to reduce the size of the active database.

Examples:

* Cold PostgreSQL instance
* Object storage
* Data warehouse

---

# Scaling Strategy

```text id="f7cbzs"
Vertical Scaling
        ↓
Connection Pooling
        ↓
Batch Updates
        ↓
Table Partitioning
        ↓
Database Sharding
```

This progression introduces distributed complexity only when necessary.

---

# Redis Recovery

Redis maintains the in-memory leaderboard rankings.

If Redis is lost, the system must rebuild the leaderboard.

---

## High Availability

Deploy Redis using primary-replica replication.

```text id="cvljlwm"
Redis Primary
        │
        ▼
Redis Replica
```

If the primary fails, promote a replica with minimal interruption.

---

## Catastrophic Failure

If the entire Redis cluster is lost:

Periodically persist leaderboard snapshots.

Each snapshot stores:

* `leagueId`
* Leaderboard state
* Corresponding Kafka offset

Example:

```text id="5cmfrs"
League 123

John 103

Mike 98

Sarah 95

Kafka Offset = 9,582,341
```

---

## Recovery Process

### Step 1

Restore the latest leaderboard snapshot into Redis.

### Step 2

Resume Kafka consumption from the stored offset.

```text id="mjlwm5"
Snapshot Offset

↓

Replay Remaining Events

↓

Latest State
```

Only events after the snapshot need to be replayed.

---

# Why Store the Kafka Offset?

Without the offset, the system cannot determine where replay should begin.

The stored offset identifies exactly which events are already reflected in the snapshot.

---

# Active vs Inactive Leagues

Fantasy leagues are user-created.

Not all leagues are active simultaneously.

Examples:

* Office League
* Friends League
* Family League
* Public League

Many leagues may receive little or no traffic.

Therefore, avoid assuming every league must always reside in Redis.

---

# Redis Cluster

A single Redis instance is insufficient to store millions of active leaderboards.

Deploy a Redis Cluster.

```text id="jlwm6"
                Redis Cluster

      ┌────────┬────────┬────────┐
      │ Node 1 │ Node 2 │ Node 3 │
      ├────────┼────────┼────────┤
      │League1 │League4 │League7 │
      │League2 │League5 │League8 │
      │League3 │League6 │League9 │
      └────────┴────────┴────────┘
```

Leaderboards are distributed across nodes using the Redis key:

```text id="jlwm8"
FantasyLeague:<leagueId>
```

Redis Cluster hashes the key and routes requests to the appropriate shard.

---

# Cache Only Active Leaderboards

Instead of storing every leaderboard in Redis:

* Keep active/hot leagues in Redis.
* Store inactive leagues durably in PostgreSQL.
* Load inactive leaderboards into Redis on demand.

Example:

```text id="jlwm9"
GET Leaderboard

↓

Redis Hit

↓

Return Immediately
```

If not found:

```text id="jlwma"
PostgreSQL

↓

Rebuild Leaderboard

↓

Store in Redis

↓

Return Response
```

This cache-aside approach optimizes memory usage while preserving low-latency access for active leagues.

---

# Key Takeaways

* PostgreSQL remains the durable source of truth.
* Scale PostgreSQL gradually: vertical scaling → connection pooling → batch updates → partitioning → sharding.
* `leagueId` is the natural partitioning and sharding key because every fantasy team belongs to exactly one league.
* Redis replication provides high availability for node failures.
* Periodic leaderboard snapshots plus Kafka offsets enable fast recovery after catastrophic Redis failures.
* Use a Redis Cluster to horizontally scale memory and throughput.
* Keep only active leaderboards in Redis; inactive user-created leagues can be reconstructed from PostgreSQL when needed.


# Fantasy Leaderboard Service

## PostgreSQL Scaling & Redis Recovery

---

# Scaling PostgreSQL

The Fantasy Scoring Service performs frequent updates to the `FantasyTeamScore` table.

As traffic grows, PostgreSQL should be scaled incrementally before introducing distributed complexity.

---

## 1. Vertical Scaling

Initially scale a single PostgreSQL instance by increasing:

* CPU
* Memory
* Faster NVMe SSDs

This is the simplest scaling strategy and should always be considered before sharding.

---

## 2. Connection Pooling

Do not allow thousands of workers to create thousands of database connections.

Use a connection pool such as **HikariCP** (Spring Boot's default connection pool).

```text id="s8y8kc"
Fantasy Score Workers
          │
          ▼
       HikariCP
          │
          ▼
     PostgreSQL
```

Benefits:

* Reuses existing database connections.
* Reduces connection establishment overhead.
* Prevents exhausting PostgreSQL's maximum connection limit.

---

## 3. Batch Updates

Instead of executing one SQL update per fantasy team, process updates in batches.

Example:

Instead of:

```sql id="3pgkh5"
UPDATE Team1;
UPDATE Team2;
...
UPDATE Team500;
```

Execute batched updates within a transaction.

Benefits:

* Fewer network round trips.
* Lower transaction overhead.
* Better write throughput.

---

## 4. Table Partitioning

Partition large tables such as:

```text id="3ecyr0"
FantasyTeamScore
```

by:

```text id="laj0dk"
leagueId
```

Benefits:

* Smaller indexes.
* Faster index lookups.
* Faster maintenance operations.
* Improved write performance.

This is **table partitioning inside PostgreSQL**, not distributed sharding.

---

## 5. Database Sharding

When a single PostgreSQL instance becomes insufficient, shard by:

```text id="zpmw1x"
leagueId
```

Example:

```text id="2jizqh"
leagueId % N
```

Each PostgreSQL instance stores a subset of fantasy leagues.

Advantages:

* Even distribution of write load.
* No cross-shard score updates because each fantasy team belongs to one league.
* Natural shard key aligned with the domain model.

---

## 6. Archive Old Leagues

Completed seasons and inactive fantasy leagues no longer receive writes.

Move them to colder storage to reduce the size of the active database.

Examples:

* Cold PostgreSQL instance
* Object storage
* Data warehouse

---

# Scaling Strategy

```text id="f7cbzs"
Vertical Scaling
        ↓
Connection Pooling
        ↓
Batch Updates
        ↓
Table Partitioning
        ↓
Database Sharding
```

This progression introduces distributed complexity only when necessary.

---

# Redis Recovery

Redis maintains the in-memory leaderboard rankings.

If Redis is lost, the system must rebuild the leaderboard.

---

## High Availability

Deploy Redis using primary-replica replication.

```text id="cvljlwm"
Redis Primary
        │
        ▼
Redis Replica
```

If the primary fails, promote a replica with minimal interruption.

---

## Catastrophic Failure

If the entire Redis cluster is lost:

Periodically persist leaderboard snapshots.

Each snapshot stores:

* `leagueId`
* Leaderboard state
* Corresponding Kafka offset

Example:

```text id="5cmfrs"
League 123

John 103

Mike 98

Sarah 95

Kafka Offset = 9,582,341
```

---

## Recovery Process

### Step 1

Restore the latest leaderboard snapshot into Redis.

### Step 2

Resume Kafka consumption from the stored offset.

```text id="mjlwm5"
Snapshot Offset

↓

Replay Remaining Events

↓

Latest State
```

Only events after the snapshot need to be replayed.

---

# Why Store the Kafka Offset?

Without the offset, the system cannot determine where replay should begin.

The stored offset identifies exactly which events are already reflected in the snapshot.

---

# Active vs Inactive Leagues

Fantasy leagues are user-created.

Not all leagues are active simultaneously.

Examples:

* Office League
* Friends League
* Family League
* Public League

Many leagues may receive little or no traffic.

Therefore, avoid assuming every league must always reside in Redis.

---

# Redis Cluster

A single Redis instance is insufficient to store millions of active leaderboards.

Deploy a Redis Cluster.

```text id="jlwm6"
                Redis Cluster

      ┌────────┬────────┬────────┐
      │ Node 1 │ Node 2 │ Node 3 │
      ├────────┼────────┼────────┤
      │League1 │League4 │League7 │
      │League2 │League5 │League8 │
      │League3 │League6 │League9 │
      └────────┴────────┴────────┘
```

Leaderboards are distributed across nodes using the Redis key:

```text id="jlwm8"
FantasyLeague:<leagueId>
```

Redis Cluster hashes the key and routes requests to the appropriate shard.

---

# Cache Only Active Leaderboards

Instead of storing every leaderboard in Redis:

* Keep active/hot leagues in Redis.
* Store inactive leagues durably in PostgreSQL.
* Load inactive leaderboards into Redis on demand.

Example:

```text id="jlwm9"
GET Leaderboard

↓

Redis Hit

↓

Return Immediately
```

If not found:

```text id="jlwma"
PostgreSQL

↓

Rebuild Leaderboard

↓

Store in Redis

↓

Return Response
```

This cache-aside approach optimizes memory usage while preserving low-latency access for active leagues.

---

# Key Takeaways

* PostgreSQL remains the durable source of truth.
* Scale PostgreSQL gradually: vertical scaling → connection pooling → batch updates → partitioning → sharding.
* `leagueId` is the natural partitioning and sharding key because every fantasy team belongs to exactly one league.
* Redis replication provides high availability for node failures.
* Periodic leaderboard snapshots plus Kafka offsets enable fast recovery after catastrophic Redis failures.
* Use a Redis Cluster to horizontally scale memory and throughput.
* Keep only active leaderboards in Redis; inactive user-created leagues can be reconstructed from PostgreSQL when needed.



**Why Apache Flink instead of plain Kafka consumers?**

Kafka consumers are well suited for simple stateless event processing. However, once we need **stateful stream processing**—such as computing rolling 5-minute averages, ball possession, distance covered, heat maps, or other live match statistics—we need much more than simply consuming events.

Without a stream processing framework, we would have to implement:

* Window management
* Stateful storage
* Timer-based window eviction
* Handling out-of-order and late-arriving events
* Checkpointing
* Failure recovery
* Stateful scaling across multiple instances
* Exactly-once processing

Implementing these capabilities ourselves significantly increases the complexity of the application.

Apache Flink provides these capabilities out of the box. It manages state, event-time windows, watermarks, checkpointing, recovery, and parallel execution, allowing developers to focus on implementing the business logic rather than building the underlying stream-processing infrastructure.


**Why Apache Flink instead of plain Kafka consumers?**

Kafka consumers are well suited for simple stateless event processing. However, once we need **stateful stream processing**—such as computing rolling 5-minute averages, ball possession, distance covered, heat maps, or other live match statistics—we need much more than simply consuming events.

Without a stream processing framework, we would have to implement:

* Window management
* Stateful storage
* Timer-based window eviction
* Handling out-of-order and late-arriving events
* Checkpointing
* Failure recovery
* Stateful scaling across multiple instances
* Exactly-once processing


# Apache Flink - High-Level Interview Notes

## Why Apache Flink?

Kafka consumers are sufficient for **stateless** event processing.

However, once we need **stateful stream processing**, such as:

* Rolling 5-minute averages
* Distance covered
* Ball possession
* Heat maps
* Live player statistics

Kafka consumers become difficult to manage because we would have to implement:

* Window management
* Stateful storage
* Timer-based window eviction
* Handling out-of-order events
* Checkpointing
* Failure recovery
* Stateful scaling
* Exactly-once processing

Apache Flink provides these capabilities out of the box, allowing developers to focus on the business logic instead of building stream-processing infrastructure.

---

# Stateful Stream Processing

A computation is **stateful** when processing the current event requires information from previously processed events.

Examples:

* Average speed over the last 5 minutes
* Distance covered
* Ball possession
* Heat maps

Example:

```text
Current Event
+
Previous Events
↓
New Result
```

Without remembering previous events, these calculations are impossible.

---

# High-Level Flink Architecture

```text
Sports Provider
        │
        ▼
Telemetry Ingestion Service
        │
        ▼
Kafka
(match-telemetry-events)
        │
        ▼
Apache Flink
        │
        ▼
Live Match Statistics
        │
        ▼
Leaderboard / Analytics / AI / Replay
```

Flink continuously consumes telemetry events from Kafka and computes live statistics.

---

# Event Time vs Processing Time

Every telemetry event contains two important timestamps.

## Event Time

The time when the event actually occurred on the field.

Example:

```text
Player scores at 12:05:10
```

The sports provider assigns this timestamp before sending the event.

Example:

```json
{
  "eventTimestamp": "12:05:10"
}
```

Event Time is used for all business calculations because it accurately represents when the event happened.

---

## Processing Time

The time when Flink receives the event.

Example:

```text
Event occurs      : 12:05:10
Received by Flink : 12:05:12
```

Processing time can vary because of:

* Network latency
* Congestion
* Retries
* Temporary outages

Therefore, processing time should not be used for sports statistics.

---

# Watermarks

Since events may arrive late or out of order, Flink cannot immediately close an event-time window.

A **watermark** represents Flink's estimate that it has received almost all events up to a particular event timestamp.

Example:

Window:

```text
12:00:00 → 12:05:00
```

Allowed lateness:

```text
5 seconds
```

Flink waits until approximately:

```text
12:05:05
```

before finalizing the window.

Events arriving before the watermark advances are still included.

Events arriving after the window has been finalized are considered late.

---

## Who decides the watermark?

The application developer configures a **watermark strategy**, for example:

* Maximum expected out-of-order delay = 5 seconds.

Flink then automatically generates moving watermarks using the event timestamps from the incoming stream.

Important distinction:

* **Allowed lateness** is a configuration.
* **Watermark** is a continuously advancing timestamp generated by Flink.

---

# Checkpointing

State is typically maintained in memory while Flink processes the stream.

If a TaskManager crashes, all in-memory state would normally be lost.

Checkpointing solves this problem.

At configurable intervals, Flink asynchronously creates snapshots of:

* Operator state
* Window state
* Aggregation state
* Kafka offsets

These snapshots are stored in durable storage such as:

* Amazon S3
* HDFS
* Cloud object storage

If a failure occurs:

1. Flink restores the latest checkpoint.
2. Restores the saved operator state.
3. Resumes consuming Kafka from the saved offsets.
4. Continues processing without rebuilding all state from scratch.

---

# Why Event Time Matters

Example:

```text
12:05:10  Player scores
12:05:12  Event reaches Flink
```

Although Flink receives the event two seconds later, it still processes it as an event that occurred at **12:05:10**, ensuring accurate statistics.

---

# Key Interview Takeaways

## Why Flink?

Use Flink whenever the application requires **stateful stream processing**, including rolling windows, continuous aggregations, event-time processing, and fault-tolerant state management.

---

## What is Stateful Processing?

The computation depends on both the current event and previously processed events.

---

## Why Event Time?

Business calculations should be based on when an event actually occurred, not when it was received over the network.

---

## What is a Watermark?

A watermark is Flink's indication that it has likely received all events up to a specific event timestamp, allowing event-time windows to be safely closed while still tolerating a configurable amount of late-arriving data.

---

## What is Checkpointing?

Checkpointing periodically snapshots operator state and Kafka offsets to durable storage so Flink can recover from failures without losing stateful computations.


Implementing these capabilities ourselves significantly increases the complexity of the application.

Apache Flink provides these capabilities out of the box. It manages state, event-time windows, watermarks, checkpointing, recovery, and parallel execution, allowing developers to focus on implementing the business logic rather than building the underlying stream-processing infrastructure.


# Sports Telemetry Ingestion Pipeline

## Problem Statement

Design a highly scalable telemetry ingestion platform capable of ingesting real-time sports telemetry from multiple sports (NBA, NFL, MLB, MLS, Formula 1) and making it available to downstream analytics systems.

---

# Functional Requirements

1. Ingest real-time telemetry events from external sports providers.
2. Support multiple sports with a generic ingestion pipeline.
3. Validate and normalize incoming telemetry into a canonical event format.
4. Publish telemetry for downstream consumers.
5. Persist telemetry for historical analysis, replay, and machine learning.

---

# Non-Functional Requirements

* Support approximately **500,000 telemetry events/second**.
* Support approximately **2,000 concurrent live games**.
* P99 ingestion latency **< 100 ms**.
* Horizontally scalable.
* Highly available.
* Durable (no event loss).
* Preserve ordering where required.
* Support replay after failures.

---

# Capacity Estimation

Assumptions:

* 500,000 telemetry events/sec
* Approximately 1 KB per event

Approximate throughput:

* **500 MB/sec**
* **~43 TB/day**

This immediately rules out using a traditional OLTP database such as PostgreSQL for raw telemetry storage.

---

# Telemetry Ingestion API

```http
POST /v1/matches/{matchId}/events
```

Example payload:

```json
{
  "eventId": "evt-12345",
  "providerId": "stats-perform",
  "matchId": "match-1001",
  "sportId": "NBA",
  "playerId": "player-30",
  "teamId": "team-12",
  "eventTimestamp": 1754523012345,
  "eventType": "PLAYER_POSITION",
  "payload": {
    "x": 52.34,
    "y": 18.92,
    "speed": 7.4,
    "heartRate": 152
  }
}
```

---

# Why expose a REST endpoint?

Instead of allowing providers to publish directly to Kafka, the ingestion endpoint provides a controlled entry point for the platform.

Responsibilities include:

* Authenticate and authorize providers.
* Rate limit incoming requests.
* Validate payloads.
* Perform request deduplication.
* Transform provider-specific payloads into a canonical event model.
* Publish validated events to Kafka.

This keeps downstream systems provider-agnostic.

---

# High-Level Architecture

```text
Sports Provider
        │
        ▼
Load Balancer
        │
        ▼
Telemetry Ingestion Service
        │
        ▼
Kafka
(match-telemetry-events)
        │
        ├───────────────┐
        ▼               ▼
Telemetry         Live Match
Persistence       Statistics (Flink)
Service
        │
        ▼
Cassandra
```

---

# Why Kafka?

Kafka satisfies all major requirements:

* High throughput
* Low latency (well within the 100 ms SLA)
* Durable event storage
* Replay capability
* Multiple independent consumers
* Horizontal scalability

Kafka becomes the central event bus for the telemetry platform.

---

# Why Cassandra?

Raw telemetry is an append-heavy workload.

Cassandra is a good fit because it offers:

* Extremely high write throughput
* Horizontal scalability
* High availability
* Efficient storage of large volumes of time-series data

The Telemetry Persistence Service asynchronously consumes telemetry events from Kafka and stores them in Cassandra.

---

# Kafka Partitioning

The initial recommendation is to partition by:

```text
matchId
```

Benefits:

* Preserves ordering within a match.
* All telemetry for a game is processed together.
* Simplifies downstream processing.

Potential challenge:

Large events (for example, an NBA Finals game) can create a **hot partition**.

Possible future optimization:

Use a composite partition key such as:

```text
matchId + entityId
```

to distribute the load while preserving ordering for each tracked entity.

---

# Why Kafka Consumers Are Not Enough

Simple Kafka consumers work well for stateless event processing.

However, many sports analytics require **stateful stream processing**, such as:

* Rolling 5-minute average speed
* Distance covered
* Ball possession
* Heat maps
* Sprint detection
* Live player statistics

Without a stream processing framework, developers would need to implement:

* Stateful storage
* Window management
* Timer-based eviction
* Handling out-of-order events
* Checkpointing
* Failure recovery
* Stateful scaling
* Exactly-once processing

---

# Why Apache Flink?

Apache Flink provides these capabilities out of the box.

Instead of building stream-processing infrastructure yourself, Flink offers:

* Stateful stream processing
* Event-time windows
* Watermarks
* Automatic state management
* Checkpointing
* Failure recovery
* Parallel execution
* Exactly-once processing

This allows developers to focus on business logic rather than stream-processing infrastructure.

---

# High-Level Flink Architecture

```text
Sports Provider
        │
        ▼
Telemetry Ingestion Service
        │
        ▼
Kafka
(match-telemetry-events)
        │
        ▼
Apache Flink
        │
        ▼
Live Match Statistics
        │
        ▼
Leaderboard / Analytics / AI / Replay
```

---

# Interview Summary

**When would I introduce Apache Flink?**

Once the system needs **stateful stream processing**, such as rolling-window analytics or continuous aggregations, Kafka consumers alone become insufficient because they require custom implementations for state management, windows, checkpointing, recovery, and out-of-order event handling.

Apache Flink provides these capabilities natively, enabling scalable, fault-tolerant, real-time stream processing while allowing developers to focus on the business logic.

	
# Apache Flink - High-Level Interview Notes

## Why Apache Flink?

Kafka consumers are sufficient for **stateless** event processing.

However, once we need **stateful stream processing**, such as:

* Rolling 5-minute averages
* Distance covered
* Ball possession
* Heat maps
* Live player statistics

Kafka consumers become difficult to manage because we would have to implement:

* Window management
* Stateful storage
* Timer-based window eviction
* Handling out-of-order events
* Checkpointing
* Failure recovery
* Stateful scaling
* Exactly-once processing

Apache Flink provides these capabilities out of the box, allowing developers to focus on the business logic instead of building stream-processing infrastructure.

---

# Stateful Stream Processing

A computation is **stateful** when processing the current event requires information from previously processed events.

Examples:

* Average speed over the last 5 minutes
* Distance covered
* Ball possession
* Heat maps

Example:

```text
Current Event
+
Previous Events
↓
New Result
```

Without remembering previous events, these calculations are impossible.

---

# High-Level Flink Architecture

```text
Sports Provider
        │
        ▼
Telemetry Ingestion Service
        │
        ▼
Kafka
(match-telemetry-events)
        │
        ▼
Apache Flink
        │
        ▼
Live Match Statistics
        │
        ▼
Leaderboard / Analytics / AI / Replay
```

Flink continuously consumes telemetry events from Kafka and computes live statistics.

---

# Event Time vs Processing Time

Every telemetry event contains two important timestamps.

## Event Time

The time when the event actually occurred on the field.

Example:

```text
Player scores at 12:05:10
```

The sports provider assigns this timestamp before sending the event.

Example:

```json
{
  "eventTimestamp": "12:05:10"
}
```

Event Time is used for all business calculations because it accurately represents when the event happened.

---

## Processing Time

The time when Flink receives the event.

Example:

```text
Event occurs      : 12:05:10
Received by Flink : 12:05:12
```

Processing time can vary because of:

* Network latency
* Congestion
* Retries
* Temporary outages

Therefore, processing time should not be used for sports statistics.

---

# Watermarks

Since events may arrive late or out of order, Flink cannot immediately close an event-time window.

A **watermark** represents Flink's estimate that it has received almost all events up to a particular event timestamp.

Example:

Window:

```text
12:00:00 → 12:05:00
```

Allowed lateness:

```text
5 seconds
```

Flink waits until approximately:

```text
12:05:05
```

before finalizing the window.

Events arriving before the watermark advances are still included.

Events arriving after the window has been finalized are considered late.

---

## Who decides the watermark?

The application developer configures a **watermark strategy**, for example:

* Maximum expected out-of-order delay = 5 seconds.

Flink then automatically generates moving watermarks using the event timestamps from the incoming stream.

Important distinction:

* **Allowed lateness** is a configuration.
* **Watermark** is a continuously advancing timestamp generated by Flink.

---

# Checkpointing

State is typically maintained in memory while Flink processes the stream.

If a TaskManager crashes, all in-memory state would normally be lost.

Checkpointing solves this problem.

At configurable intervals, Flink asynchronously creates snapshots of:

* Operator state
* Window state
* Aggregation state
* Kafka offsets

These snapshots are stored in durable storage such as:

* Amazon S3
* HDFS
* Cloud object storage

If a failure occurs:

1. Flink restores the latest checkpoint.
2. Restores the saved operator state.
3. Resumes consuming Kafka from the saved offsets.
4. Continues processing without rebuilding all state from scratch.

---

# Why Event Time Matters

Example:

```text
12:05:10  Player scores
12:05:12  Event reaches Flink
```

Although Flink receives the event two seconds later, it still processes it as an event that occurred at **12:05:10**, ensuring accurate statistics.

---

# Key Interview Takeaways

## Why Flink?

Use Flink whenever the application requires **stateful stream processing**, including rolling windows, continuous aggregations, event-time processing, and fault-tolerant state management.

---

## What is Stateful Processing?

The computation depends on both the current event and previously processed events.

---

## Why Event Time?

Business calculations should be based on when an event actually occurred, not when it was received over the network.

---

## What is a Watermark?

A watermark is Flink's indication that it has likely received all events up to a specific event timestamp, allowing event-time windows to be safely closed while still tolerating a configurable amount of late-arriving data.

---

## What is Checkpointing?

Checkpointing periodically snapshots operator state and Kafka offsets to durable storage so Flink can recover from failures without losing stateful computations.

## Kafka Consumer vs Apache Flink

A Kafka consumer is sufficient for **simple event processing** and **simple keyed aggregations**, such as updating points, rebounds, assists, or team scores as events arrive. These computations maintain simple state (for example, incrementing a counter) and can be implemented with a Kafka consumer updating Redis or a database.

However, when we need **advanced stateful stream processing**, such as:

* Rolling 5-minute averages
* Sliding or tumbling window aggregations
* Ball possession over time
* Distance covered
* Heat maps
* Event-time processing
* Out-of-order event handling

the complexity increases significantly. We now need capabilities such as:

* Window management
* Event-time processing
* Watermarks for late-arriving events
* Time-based state eviction
* Checkpointing
* Failure recovery
* Exactly-once processing
* Scalable state management

Apache Flink provides these capabilities out of the box, allowing us to focus on implementing the business logic rather than building and maintaining the stream-processing infrastructure ourselves.

**Interview Summary**

> A Kafka consumer is ideal for simple event processing and straightforward keyed aggregations. When the application requires advanced stateful stream processing with event-time windows, rolling aggregations, out-of-order event handling, and fault-tolerant state management, Apache Flink is the appropriate choice.

# Live Match Statistics Service

## Problem Statement

Design a service that computes and serves **live sports statistics** from telemetry events in real time.

The service should support multiple sports (NBA, NFL, MLB, MLS, etc.) and provide low-latency APIs for applications such as Apple Sports.

---

# Clarifying Questions

### 1. Is this for one sport or multiple sports?

Assume **multiple sports**.

The ingestion and streaming pipeline remains generic, while the business logic for computing statistics is sport-specific.

Examples:

* NBA Rules Engine
* NFL Rules Engine
* Soccer Rules Engine

---

### 2. What statistics are we computing?

#### Player Statistics

* Points
* Rebounds
* Assists
* Distance Covered
* Average Speed (Rolling 5 Minutes)
* Sprint Count
* Minutes Played

#### Team Statistics

* Team Score
* Ball Possession %
* Field Goal %
* Fouls
* Turnovers

#### Match Statistics

* Quarter Score
* Match Score
* Time Remaining
* Timeouts
* Heat Maps

---

# Functional Requirements

1. Consume telemetry events from Kafka.
2. Compute player, team, and match statistics in real time.
3. Continuously update live statistics.
4. Expose APIs to retrieve statistics.
5. Persist finalized statistics after the match ends.

---

# Non-Functional Requirements

* Statistics visible within approximately **1 second**.
* Support approximately **500K telemetry events/sec**.
* Support approximately **2 million concurrent viewers**.
* Highly available.
* Horizontally scalable.
* Fault tolerant.
* Event-time processing.
* Replay capability.

---

# Kafka Consumer vs Apache Flink

A Kafka consumer is sufficient for **simple event processing** and **simple keyed aggregations**, such as:

* Points
* Rebounds
* Assists
* Team Score

These computations maintain simple state (for example, incrementing a counter) and can be implemented using a Kafka consumer updating Redis.

However, when the application requires **advanced stateful stream processing**, such as:

* Rolling averages
* Sliding/Tumbling windows
* Distance covered
* Ball possession
* Heat maps
* Event-time processing
* Out-of-order event handling

the complexity increases significantly.

These workloads require:

* Window management
* Event-time processing
* Watermarks
* Time-based state eviction
* Checkpointing
* Failure recovery
* Exactly-once processing
* Scalable state management

Apache Flink provides these capabilities out of the box, allowing developers to focus on the business logic instead of building stream-processing infrastructure.

---

# REST APIs

## Match Statistics

```http
GET /v1/matches/{matchId}/statistics
```

Returns:

* Match score
* Quarter
* Time remaining
* Team statistics

---

## Player Statistics

```http
GET /v1/matches/{matchId}/players/{playerId}/statistics
```

Returns:

* Points
* Rebounds
* Assists
* Distance covered
* Average speed
* Minutes played

---

## Team Statistics

```http
GET /v1/matches/{matchId}/teams/{teamId}/statistics
```

Returns:

* Score
* Ball possession
* Field goal percentage
* Fouls
* Turnovers

---

## Why only GET APIs?

This is a read-only service.

Clients never update statistics directly.

Statistics are continuously computed by Flink consuming telemetry events from Kafka.

---

# Data Model

## MatchStatistics

```text
matchId (PK)

homeTeamId
awayTeamId

homeScore
awayScore

currentQuarter

timeRemaining

status (LIVE / FINISHED)

lastUpdated
```

---

## PlayerStatistics

```text
(matchId, playerId)

points
rebounds
assists

distanceCovered

avgSpeedLast5Min

minutesPlayed

lastUpdated
```

---

## TeamStatistics

```text
(matchId, teamId)

score

ballPossession

fieldGoalPercentage

turnovers

timeoutsRemaining

lastUpdated
```

---

# High-Level Architecture

```text
Sports Provider
        │
        ▼
Telemetry Ingestion Service
        │
        ▼
Kafka
(match-telemetry-events)
        │
        ▼
Apache Flink
        │
        ├──────────────┐
        ▼              ▼
Redis        PostgreSQL (Final Statistics)
        │
        ▼
Statistics API
        │
        ▼
Apple Sports App
```

---

# Component Responsibilities

## Telemetry Ingestion Service

* Authenticate providers
* Validate requests
* Deduplicate events
* Convert to canonical event format
* Publish to Kafka
* Return HTTP 202

---

## Kafka

Acts as the central event bus.

Benefits:

* High throughput
* Durable storage
* Replay capability
* Decouples ingestion from computation
* Multiple downstream consumers

---

## Apache Flink

Consumes telemetry events and computes live statistics.

Examples:

### Simple Keyed Aggregations

* Points
* Rebounds
* Assists
* Team Score

### Advanced Stateful Stream Processing

* Rolling average speed
* Distance covered
* Ball possession
* Heat maps
* Sprint detection

Flink also provides:

* Event-time processing
* Window management
* Watermarks
* Checkpointing
* Failure recovery

---

## Redis

Stores the latest live statistics.

Benefits:

* Sub-millisecond reads
* Extremely high throughput
* Ideal for millions of concurrent users refreshing live scores

The Statistics API simply reads from Redis.

---

## PostgreSQL

Stores finalized statistics once a match is complete.

Example workflow:

```text
MATCH_ENDED Event

↓

Persist Final Match Statistics

↓

PostgreSQL
```

Why PostgreSQL?

* Data volume is relatively small (millions of rows, not billions).
* ACID guarantees.
* Easy historical queries.
* Career statistics.
* Reporting and analytics.

Redis stores temporary live data, while PostgreSQL becomes the permanent system of record after the match concludes.

---

# Interview Summary

* Use Kafka as the event bus.
* Use Apache Flink for the statistics computation pipeline.
* Use Redis as the low-latency serving layer for live statistics.
* Use PostgreSQL to persist finalized statistics after the match ends.
* Expose read-only REST APIs that serve data directly from Redis.
* Separate the concerns of ingestion, computation, serving, and historical storage to achieve scalability and maintainability.


# Live Match Statistics Service - Architecture & Scaling

## High-Level Architecture

```text
                 Sports Provider
                        │
                        ▼
           Telemetry Ingestion Service
                        │
                        ▼
                     Kafka
        (match-telemetry-events Topic)
                        │
                        ▼
                  Apache Flink
          (Source → Operators → Sink)
              │                    │
              │                    ▼
              │               Redis Sink
              │                    │
              ▼                    ▼
      Checkpoints (S3)         Redis Cluster
              │                    │
              │                    ▼
              │             Statistics API
              │                    │
              ▼                    ▼
        Failure Recovery      Apple Sports App

                After MATCH_ENDED
                       │
                       ▼
          Persist Final Statistics
                       │
                       ▼
                  PostgreSQL
```

---

# Component Responsibilities

## Telemetry Ingestion Service

Responsibilities:

* Authenticate providers
* Validate requests
* Deduplicate events
* Convert provider payload into a canonical format
* Publish telemetry events to Kafka
* Return HTTP 202 Accepted

The service is stateless and can be horizontally scaled behind a load balancer.

---

## Kafka

Kafka acts as the central event bus.

Benefits:

* High throughput
* Durable event log
* Replay capability
* Decouples ingestion from downstream consumers
* Supports multiple consumers

Partitioning Strategy:

```
Partition Key = matchId
```

This preserves ordering for events belonging to the same match while enabling parallel processing across matches.

---

## Apache Flink

Consumes telemetry events from Kafka and computes live statistics.

Examples of simple keyed aggregations:

* Points
* Rebounds
* Assists
* Team score

Examples of advanced stateful stream processing:

* Rolling average speed
* Distance covered
* Ball possession
* Heat maps
* Sprint detection

Flink provides:

* Event-time processing
* Window management
* Watermarks
* Checkpointing
* Failure recovery
* Exactly-once processing

---

# Flink Sink

Every Flink pipeline consists of:

```
Source
    ↓
Operators
    ↓
Sink
```

For this system:

```
Kafka Source
      ↓
Flink Operators
      ↓
Redis Sink
```

Redis is the serving layer for the application and therefore acts as the primary sink for live statistics.

---

# Redis

Redis stores the latest computed statistics.

Benefits:

* Sub-millisecond reads
* Extremely high throughput
* Ideal for millions of concurrent users refreshing live scores

The Statistics API simply reads the latest values from Redis.

Redis is **not** the system of record.

---

# PostgreSQL

PostgreSQL stores finalized statistics after the match ends.

Example workflow:

```
MATCH_ENDED Event
        ↓
Persist Final Statistics
        ↓
PostgreSQL
```

Reasons:

* Historical match statistics
* Career statistics
* Reporting
* Analytics
* Permanent storage

Live updates remain in Redis during the match.

---

# Flink Checkpointing

Checkpointing is Flink's fault-tolerance mechanism.

At configurable intervals, Flink snapshots:

* Operator state
* Window state
* Aggregation state
* Kafka offsets

These checkpoints are stored in durable object storage such as:

* Amazon S3
* HDFS
* Azure Blob Storage

Checkpoints are **not** stored in Redis or PostgreSQL.

Purpose:

If a TaskManager fails:

1. Restore the latest checkpoint.
2. Restore operator state.
3. Resume consuming Kafka from the saved offsets.
4. Continue processing without rebuilding all state.

---

# Separation of Responsibilities

| Component        | Responsibility                               |
| ---------------- | -------------------------------------------- |
| Kafka            | Durable event log and replay                 |
| Apache Flink     | Stream processing and statistics computation |
| Redis            | Serve low-latency live statistics            |
| PostgreSQL       | Store finalized historical statistics        |
| Amazon S3 / HDFS | Store Flink checkpoints for recovery         |

---

# Scaling Strategy

## Telemetry Ingestion Service

Stateless.

Scale horizontally behind a load balancer.

---

## Kafka

Scale by:

* Adding brokers
* Increasing partitions

Partition by:

```
matchId
```

---

## Apache Flink

Scale by:

* Increasing parallelism
* Adding TaskManagers

Flink automatically distributes processing across the cluster.

---

## Redis

A single Redis node is insufficient for millions of concurrent viewers.

Use:

```
Redis Cluster
```

Shard by:

```
matchId
```

Each live match resides on a different Redis shard.

---

## Statistics API

Stateless.

Scale horizontally behind a load balancer.

---

# Hot Match Scenario

Example:

NBA Finals receives 5 million concurrent viewers.

Potential issue:

One Redis key becomes extremely hot.

Possible optimizations:

* Redis read replicas
* Small API-side cache (for example, 100–250 ms TTL)
* Horizontal API scaling

A CDN is generally not useful because statistics change continuously during a live match.

---

# Interview Summary

* Kafka provides durable, scalable event ingestion.
* Apache Flink computes both simple keyed aggregations and advanced event-time analytics.
* Redis acts as the Flink sink and serves live statistics with sub-millisecond latency.
* PostgreSQL stores finalized historical statistics after the match ends.
* Flink checkpoints state and Kafka offsets to Amazon S3 (or HDFS) for fault recovery.
* Every layer can scale independently, enabling support for hundreds of thousands of telemetry events per second and millions of concurrent readers.

# Live Match Statistics Service - Sport-Specific Business Logic

## Supporting Multiple Sports

The streaming infrastructure should remain **generic**, while the business rules should be **sport-specific**.

The same pipeline can support:

* NBA
* NFL
* Soccer
* MLB

without changing Kafka, Flink, Redis, or the APIs.

Only the business logic changes.

---

# High-Level Architecture

```text
                    Kafka
                      │
                      ▼
                 Apache Flink
                      │
             Statistics Operator
                      │
          StatisticsProcessorFactory
                      │
      ┌───────────────┼───────────────┐
      ▼               ▼               ▼
 NBAStatistics   SoccerStatistics   NFLStatistics
   Processor        Processor         Processor
      │               │               │
      └───────────────┼───────────────┘
                      ▼
                 Redis Sink
```

The Flink pipeline remains identical for every sport.

Only the processor implementation changes.

---

# Strategy Pattern

Common interface:

```java
public interface StatisticsProcessor {

    SportType getSport();

    void process(TelemetryEvent event,
                 StatisticsContext context);
}
```

Each sport implements this interface.

Examples:

* NBAStatisticsProcessor
* SoccerStatisticsProcessor
* NFLStatisticsProcessor

---

# Processor Factory

A factory maps each sport to its corresponding processor.

Example:

```text
NBA     → NBAStatisticsProcessor

Soccer  → SoccerStatisticsProcessor

NFL     → NFLStatisticsProcessor
```

When an event arrives, the Flink operator selects the correct processor based on the sport.

---

# Example

Incoming telemetry:

```json
{
  "sport": "NBA",
  "eventType": "REBOUND"
}
```

Routing:

```text
NBAStatisticsProcessor
        │
        ▼
Increment Player Rebounds
Increment Team Statistics (if applicable)
```

Another example:

```json
{
  "sport": "SOCCER",
  "eventType": "GOAL"
}
```

Routing:

```text
SoccerStatisticsProcessor
        │
        ▼
Increment Player Goals
Increment Team Score
```

---

# Responsibilities

## Apache Flink

Provides the streaming infrastructure:

* Kafka consumption
* Event-time processing
* Window management
* Watermarks
* Checkpointing
* Failure recovery
* State management
* Redis sink

Flink **does not contain the business rules**.

---

## Statistics Processor

Contains the sport-specific business logic.

Examples:

### NBA

* 2-point shot → +2 points
* 3-point shot → +3 points
* Rebound → Increment rebounds
* Foul → Increment personal and team fouls

### Soccer

* Goal → Increment player goals and team score
* Yellow Card
* Red Card
* Possession calculations

Each sport encapsulates its own scoring and statistics rules.

---

# Separation of Concerns

```text
Apache Flink
│
├── Streaming Infrastructure
│     • Kafka Source
│     • Event Time
│     • Watermarks
│     • Windows
│     • Checkpointing
│     • Redis Sink
│
└── Statistics Operator
       │
       ▼
StatisticsProcessorFactory
       │
       ├── NBA Processor
       ├── Soccer Processor
       └── NFL Processor
```

This cleanly separates infrastructure from business logic.

---

# Design Benefits

* Supports multiple sports without changing the streaming pipeline.
* New sports can be added by implementing another `StatisticsProcessor`.
* Business rules remain isolated and maintainable.
* Follows the **Strategy Pattern**.
* Adheres to the **Open/Closed Principle**:

  * Open for extension (add new sports).
  * Closed for modification (no changes to the Flink pipeline).

---

# Interview Summary

When supporting multiple sports, keep the ingestion, streaming, checkpointing, and serving infrastructure generic. Encapsulate sport-specific rules behind a `StatisticsProcessor` interface using the Strategy Pattern. The Flink job invokes the appropriate processor based on the event's sport, allowing the platform to support new sports without modifying the underlying streaming architecture.

# Framework-Independent Business Logic

One important design principle is that the **business logic should be independent of the streaming framework**.

Whether the application uses:

* Apache Flink
* A Spring Boot Kafka Consumer
* Another stream processing framework

the business rules should remain unchanged.

The streaming framework is responsible for **executing** the business logic, not implementing it.

---

# Without Apache Flink

Architecture:

```text id="0wryuj"
Kafka

    │

    ▼

Statistics Consumer
(Spring Boot)

    │

    ▼

StatisticsProcessorFactory

    │

 ┌──┴──────────────┐
 ▼                 ▼

NBAProcessor   SoccerProcessor

    │

    ▼

Redis
```

The Kafka consumer receives events and delegates processing to the appropriate sport-specific processor.

Example:

```java id="uikwhu"
@KafkaListener(topics = "match-telemetry-events")
public void consume(TelemetryEvent event) {

    StatisticsProcessor processor =
            factory.getProcessor(event.getSport());

    StatisticsContext context =
            loadFromRedis(event);

    processor.process(event, context);

    saveToRedis(context);
}
```

---

# With Apache Flink

Architecture:

```text id="gjlwm5"
Kafka Source

      │

      ▼

Apache Flink

      │

      ▼

Statistics Operator

      │

      ▼

StatisticsProcessorFactory

      │

 ┌────┴─────────────┐
 ▼                  ▼

NBAProcessor   SoccerProcessor

      │

      ▼

Redis Sink
```

The Flink operator invokes exactly the same business logic.

Example:

```java id="jlwm51"
public void processElement(
        TelemetryEvent event,
        Context ctx,
        Collector<StatisticsResult> out) {

    StatisticsProcessor processor =
            factory.getProcessor(event.getSport());

    StatisticsContext context = getState();

    processor.process(event, context);

    updateState(context);

    out.collect(buildStatistics(context));
}
```

The only difference is how the state is managed.

---

# State Management Comparison

## Spring Boot Kafka Consumer

The application is responsible for managing state.

Typical flow:

```text id="jlwm52"
Receive Event

↓

Load Current Statistics (Redis)

↓

Process Event

↓

Update Statistics

↓

Save Back to Redis
```

The application must implement:

* State storage
* Window management
* Timers
* Late-event handling
* Checkpointing
* Failure recovery

---

## Apache Flink

Flink manages state internally.

Typical flow:

```text id="jlwm53"
Receive Event

↓

Read Flink State

↓

Process Event

↓

Update Flink State

↓

Write Result to Redis
```

Flink automatically provides:

* Event-time processing
* Window management
* Watermarks
* Stateful operators
* Checkpointing
* Failure recovery
* Offset coordination

The developer focuses primarily on implementing the business logic.

---

# Separation of Responsibilities

## Streaming Framework

Responsible for:

* Reading from Kafka
* State management
* Event-time processing
* Window management
* Watermarks
* Checkpointing
* Recovery
* Writing to Redis

---

## Statistics Processors

Responsible only for business rules.

Examples:

### NBA

* 2-point shot → Add 2 points
* 3-point shot → Add 3 points
* Rebound → Increment rebound count
* Personal foul → Update player and team fouls

### Soccer

* Goal → Update player goals and team score
* Yellow card
* Red card
* Possession calculations

---

# Key Design Principle

The business logic should be **framework-independent**.

Changing the execution engine (for example, replacing a Spring Boot Kafka consumer with Apache Flink) should not require rewriting the sport-specific business rules.

Only the surrounding infrastructure changes.

---

# Interview Summary

Apache Flink is an execution engine for stream processing. It provides state management, event-time processing, windowing, checkpointing, and recovery. The actual scoring and statistics logic should be encapsulated in reusable `StatisticsProcessor` implementations that are independent of Flink. This separation of concerns keeps the business logic reusable, maintainable, and easy to extend as new sports are added.


# Live Match Statistics Service - Failure Handling

Failure handling is a critical part of a production-ready streaming system. The architecture should ensure that no single component failure causes data loss or prolonged downtime.

---

# 1. Telemetry Ingestion Service Failure

The Telemetry Ingestion Service is stateless.

Architecture:

```text
               Load Balancer
                     │
          ┌──────────┼──────────┐
          ▼          ▼          ▼
     Ingestion   Ingestion   Ingestion
      Service      Service     Service
```

If one instance crashes:

* Traffic is routed to healthy instances.
* No state is lost.
* The service scales horizontally.

---

# 2. Kafka Broker Failure

Kafka provides durability through replication.

Configuration:

```text
Replication Factor = 3
```

Example:

```text
Broker 1 (Leader)

Broker 2 (Follower)

Broker 3 (Follower)
```

If the leader broker fails:

* Kafka elects a new leader.
* Producers and consumers continue.
* No telemetry events are lost.

---

# 3. Apache Flink Failure

If a TaskManager crashes:

```text
Checkpoint #120

        ↓

TaskManager Failure

        ↓

Restart TaskManager

        ↓

Restore Checkpoint

        ↓

Resume from Kafka Offsets
```

Checkpoint contains:

* Operator state
* Window state
* Aggregation state
* Kafka offsets

Benefits:

* No recomputation from the beginning.
* No loss of stateful computations.
* Fast recovery.

---

# 4. Redis Failure

Redis acts as the **serving layer**, not the system of record.

If Redis crashes:

```text
Restart Redis

       ↓

Flink restores checkpoint

       ↓

Resume processing

       ↓

Repopulate Redis
```

Important distinction:

Redis does **not** contain Flink checkpoints.

Flink restores its internal state from checkpoint storage (for example, Amazon S3), resumes consuming Kafka from the saved offsets, and writes the latest computed statistics back to Redis.

---

# 5. PostgreSQL Failure

When the match ends:

```text
MATCH_ENDED

      ↓

Persist Final Statistics

      ↓

PostgreSQL
```

If PostgreSQL is unavailable:

```text
MATCH_ENDED

      ↓

Persistence Queue / Kafka Topic

      ↓

Retry Worker

      ↓

PostgreSQL
```

This prevents temporary database outages from blocking the live statistics pipeline.

---

# 6. Duplicate Telemetry Events

Providers may retry requests.

Example:

```text
SHOT_MADE

eventId = 1001
```

received twice.

Solution:

Implement idempotency using:

* eventId
* provider sequence number (if available)

Duplicate events are ignored.

---

# 7. Out-of-Order Events

Example:

```text
12:00:06 arrives first

12:00:05 arrives later
```

Solution:

* Event-time processing
* Watermarks
* Configurable allowed lateness

This ensures rolling windows and time-based statistics remain accurate.

---

# 8. Hot Match Scenario

Example:

NBA Finals receives millions of concurrent viewers.

Potential issue:

A single Redis key becomes extremely hot.

Possible optimizations:

* Redis Cluster
* Redis read replicas
* Small API-side cache (100–250 ms)
* Horizontal API scaling

A CDN is generally not effective because live statistics change continuously.

---

# Failure Handling Summary

| Failure                     | Solution                                              |
| --------------------------- | ----------------------------------------------------- |
| Telemetry Ingestion Service | Stateless service behind a load balancer              |
| Kafka Broker                | Replication and leader election                       |
| Apache Flink                | Checkpoint recovery and Kafka offset restoration      |
| Redis                       | Repopulate from Flink after checkpoint recovery       |
| PostgreSQL                  | Asynchronous persistence with retries                 |
| Duplicate Events            | Idempotency using eventId or provider sequence number |
| Out-of-Order Events         | Event-time processing with watermarks                 |
| Hot Matches                 | Redis Cluster, replicas, API cache                    |

---

# Key Interview Takeaways

* Stateless services are horizontally scalable and easy to recover.
* Kafka ensures durable event storage and replay.
* Flink checkpoints operator state and Kafka offsets to durable storage (such as Amazon S3) for fault recovery.
* Redis serves low-latency live statistics but is not the source of truth.
* PostgreSQL stores finalized historical statistics after the match ends.
* Event-time processing and idempotency ensure correctness despite duplicate or late-arriving telemetry events.
* Each layer has a clearly defined responsibility, making the system resilient and scalable.

# Live Match Statistics Service - Optimizations & Trade-offs

## 1. Reduce Redis Write Throughput

Telemetry may arrive at hundreds of thousands of events per second.

Example:

```text id="v0yjlwm"
500,000 telemetry events/sec
```

Updating Redis for every telemetry event may generate unnecessary write traffic.

Example:

```text id="ufshmx"
12:00:01.001

Player Position

↓

Redis Update

12:00:01.020

Player Position

↓

Redis Update

12:00:01.040

Player Position

↓

Redis Update
```

The client cannot perceive updates every few milliseconds.

### Optimization

Continue processing every telemetry event in Flink, but publish aggregated statistics to Redis at small intervals (for example, every **100–250 ms**).

Benefits:

* Significantly fewer Redis writes.
* Reduced network traffic.
* Lower Redis CPU utilization.
* No noticeable impact on user experience.

This technique is commonly referred to as **write coalescing** or **micro-batching**.

---

# 2. Redis Pipelining

Instead of sending multiple commands individually:

```text id="f0jlwm"
SET PlayerStatistics

SET TeamStatistics

SET MatchStatistics
```

Pipeline multiple commands together.

Benefits:

* Fewer network round trips.
* Higher throughput.
* Lower latency under heavy write load.

---

# 3. Persist Only Final Statistics

Avoid updating PostgreSQL for every telemetry event.

Instead:

```text id="y0jlwm"
Live Match

↓

Redis

↓

MATCH_ENDED Event

↓

Persist Final Statistics

↓

PostgreSQL
```

Benefits:

* Lower database write load.
* Reduced transaction overhead.
* PostgreSQL stores only finalized historical data.

---

# 4. Kafka Compression

Telemetry events are repetitive.

Enable Kafka message compression:

* Snappy
* LZ4
* Zstandard (Zstd)

Benefits:

* Lower network bandwidth.
* Higher producer throughput.
* Reduced storage consumption.

---

# 5. Hot Match Optimization

Example:

NBA Finals receives millions of concurrent viewers.

Potential issue:

A single match becomes a hot key in Redis.

Possible optimizations:

* Redis Cluster
* Redis read replicas
* Small API-side cache (100–250 ms TTL)
* Horizontal API scaling

A CDN is generally not useful because live statistics change continuously.

---

# Technology Trade-offs

## Why Redis instead of PostgreSQL?

| Redis                      | PostgreSQL                       |
| -------------------------- | -------------------------------- |
| In-memory                  | Disk-based                       |
| Sub-millisecond reads      | Higher read latency              |
| Optimized for live serving | Optimized for historical storage |
| Temporary live statistics  | Permanent system of record       |

Redis serves live statistics.

PostgreSQL stores finalized historical statistics.

---

## Why Apache Flink instead of Kafka Consumers?

| Kafka Consumer             | Apache Flink                        |
| -------------------------- | ----------------------------------- |
| Simple event processing    | Advanced stateful stream processing |
| Simple keyed aggregations  | Rolling/windowed aggregations       |
| Manual event-time handling | Built-in event-time processing      |
| Manual window management   | Built-in window operators           |
| Manual checkpointing       | Automatic checkpointing             |
| Manual failure recovery    | Built-in recovery                   |

A Kafka consumer is sufficient for simple event processing and straightforward keyed aggregations.

Apache Flink becomes the preferred choice when the application requires rolling windows, event-time processing, watermarks, and fault-tolerant state management.

---

## Why Kafka?

Kafka provides:

* High throughput.
* Durable event storage.
* Replay capability.
* Loose coupling between producers and consumers.
* Support for multiple downstream consumers.

---

# Final Interview Summary

### Architecture

Sports Provider

↓

Telemetry Ingestion Service

↓

Kafka

↓

Apache Flink

↓

Redis

↓

Statistics API

↓

Apple Sports App

↓

MATCH_ENDED

↓

PostgreSQL

---

### Responsibilities

| Component                   | Responsibility                                           |
| --------------------------- | -------------------------------------------------------- |
| Telemetry Ingestion Service | Validate, authenticate, deduplicate, publish events      |
| Kafka                       | Durable event bus and replay                             |
| Apache Flink                | Compute live statistics using stateful stream processing |
| Redis                       | Serve low-latency live statistics                        |
| Statistics API              | Expose read-only APIs                                    |
| PostgreSQL                  | Store finalized historical statistics                    |
| Amazon S3 / HDFS            | Store Flink checkpoints                                  |

---

### Key Design Principles

* Separate streaming infrastructure from sport-specific business logic.
* Keep business logic framework-independent using the Strategy Pattern.
* Use Kafka for durable event streaming.
* Use Apache Flink for advanced stream processing.
* Use Redis for low-latency live reads.
* Persist finalized statistics to PostgreSQL.
* Recover Flink state using checkpoints stored in durable object storage.
* Design each component to scale independently.
* Optimize Redis writes through write coalescing and pipelining while maintaining near real-time user experience.

# Standings Service

## Problem Statement

Design a service that maintains **league standings** as matches complete.

Examples:

* NBA Eastern Conference
* NFL AFC East
* MLB AL East

Unlike the Live Statistics Service, this service **does not process telemetry**. It only updates league standings after a game has officially finished.

---

# Functional Requirements

1. Consume completed match events.
2. Update league standings.
3. Compute:

   * Wins
   * Losses
   * Draws (if applicable)
   * Winning Percentage
   * Rank
4. Expose APIs to retrieve standings.
5. Persist standings for historical reporting.

---

# Non-Functional Requirements

* Highly available.
* Horizontally scalable.
* Fault tolerant.
* Low-latency reads.
* Eventual consistency is acceptable.

---

# High-Level Architecture

```text
                Sports Provider
                       │
                       ▼
          Telemetry Ingestion Service
                       │
                       ▼
                    Kafka
               (match-events)
                       │
                       ▼
              Standings Consumer
                       │
              Load Current Standings
                       │
                       ▼
            StandingsProcessorFactory
                       │
        ┌──────────────┴──────────────┐
        ▼                             ▼
NBAStandingsProcessor       SoccerStandingsProcessor
        │                             │
        └──────────────┬──────────────┘
                       ▼
           Updated League Standings
                ┌────────┴────────┐
                ▼                 ▼
             PostgreSQL         Redis
                │                 │
                └────────┬────────┘
                         ▼
                  Standings API
                         │
                         ▼
                  Apple Sports App
```

---

# Why does it start with the Sports Provider?

The Sports Provider is the source of all sports events.

The Telemetry Ingestion Service publishes events to Kafka.

Examples of events:

* MATCH_STARTED
* SHOT_MADE
* GOAL
* REBOUND
* MATCH_ENDED

The Standings Service only cares about:

```text
MATCH_COMPLETED
```

or

```text
MATCH_ENDED
```

---

# Kafka Event

Example:

```json
{
  "matchId": 123,
  "leagueId": "NBA",
  "winnerTeamId": "LAL",
  "loserTeamId": "GSW",
  "completedAt": "2026-08-07T20:15:00Z"
}
```

---

# Processing Flow

1. Receive MATCH_COMPLETED event.
2. Load current standings.
3. Apply league-specific business rules.
4. Persist updated standings.
5. Update Redis cache.
6. Commit Kafka offset.

---

# Standings Business Logic

The consumer itself should not contain NBA or Soccer rules.

Instead, use the Strategy Pattern.

---

## Interface

```java
public interface StandingsProcessor {

    LeagueType getLeague();

    void updateStandings(
            MatchCompletedEvent event,
            LeagueStandings standings);
}
```

---

## NBA Implementation

Example logic:

* Winner:

  * Wins++
  * Games Played++
* Loser:

  * Losses++
  * Games Played++
* Recalculate Winning %
* Recalculate Rankings

---

## Soccer Implementation

Soccer may support draws.

Example:

```text
If Draw

↓

TeamA Draw++

TeamB Draw++

↓

Recompute Rankings
```

Different league.

Different business rules.

Same interface.

---

# Consumer Flow

```text
Receive Kafka Event

↓

Load League Standings

↓

ProcessorFactory

↓

NBAProcessor / SoccerProcessor

↓

Save Standings

↓

Update Redis

↓

Commit Kafka Offset
```

The consumer never knows NBA-specific rules.

It simply delegates to the appropriate processor.

---

# Data Model

```text
LeagueStanding

leagueId

season

teamId

wins

losses

draws

gamesPlayed

winningPercentage

rank

lastUpdated
```

---

# REST API

```http
GET /v1/leagues/{leagueId}/standings
```

Example:

```json
[
  {
    "rank":1,
    "team":"Lakers",
    "wins":53,
    "losses":20
  }
]
```

---

# Redis Caching

Standings change infrequently but are read extremely often.

Store one cache entry per league.

Example:

```text
standings:NBA

standings:NFL

standings:MLS
```

Each key stores the complete ordered standings table.

API flow:

```text
Client

↓

Standings API

↓

Redis
```

This avoids millions of database reads.

---

# PostgreSQL

PostgreSQL is the permanent system of record.

Stores:

* Season standings
* Historical standings
* Reporting
* Analytics

Redis is only the serving cache.

---

# Transaction Strategy

Do **not** use distributed transactions between PostgreSQL and Redis.

Recommended processing order:

```text
Receive Kafka Message

↓

Update PostgreSQL

↓

COMMIT PostgreSQL

↓

Update Redis

↓

Commit Kafka Offset
```

---

# Why commit the Kafka offset last?

The Kafka offset indicates that processing has completed successfully.

If Redis update fails:

```text
Update PostgreSQL

↓

COMMIT

↓

Update Redis

↓

FAIL
```

Do **not** commit the Kafka offset.

Kafka will redeliver the message.

---

# Idempotency

Since a message may be replayed before the Kafka offset is committed, processing must be idempotent.

Possible approaches:

* Track processed `matchId`
* Track processed `eventId`
* Use a unique database constraint
* Ignore already-processed matches

This prevents duplicate standings updates.

---

# Failure Handling

## Consumer Failure

Kafka redelivers the message.

---

## PostgreSQL Failure

Do not commit the Kafka offset.

Retry processing.

---

## Redis Failure

Do not commit the Kafka offset.

Replay the message after restart.

---

## Duplicate Events

Ignore duplicate `matchId` or `eventId`.

---

# Scaling

## Kafka

Partition by:

```text
leagueId
```

Benefits:

* Ordering within a league.
* Parallel processing across leagues.

---

## Consumer

Stateless.

Scale horizontally using Kafka consumer groups.

---

## Redis

Cache latest standings.

One key per league.

---

## PostgreSQL

Stores durable historical standings.

---

# Why Kafka Consumer instead of Flink?

A simple Kafka consumer is sufficient because:

* Updates only occur when a match finishes.
* No rolling windows.
* No event-time processing.
* No watermarks.
* No checkpointing.
* No advanced stateful stream processing.

Apache Flink would add unnecessary complexity.

---

# Design Patterns

* Event-Driven Architecture
* Strategy Pattern
* Factory Pattern
* Cache-Aside Serving Pattern
* Idempotent Consumer Pattern

---

# Interview Summary

* Sports Provider publishes match lifecycle events through the Telemetry Ingestion Service.
* Kafka delivers `MATCH_COMPLETED` events to the Standings Service.
* The consumer delegates league-specific business rules to a `StandingsProcessor` using the Strategy Pattern.
* PostgreSQL is the system of record.
* Redis caches the latest standings for low-latency reads.
* Kafka offsets are committed only after PostgreSQL and Redis updates succeed.
* Idempotency protects against message reprocessing.
* Partition Kafka by `leagueId` to preserve ordering while enabling horizontal scaling.


# Live Sports Notification Service

## Problem Statement

Design a service that delivers live sports notifications to users of the Apple Sports app.

Example notifications:

* Goal scored
* Three-pointer
* Touchdown
* Match started
* Match ended
* Lead changed

Users can subscribe to:

* Teams
* Players
* Leagues
* Individual matches

Notifications should be delivered through Apple Push Notification service (APNs).

---

# Clarifying Questions

## Scope

Support multiple sports:

* NBA
* NFL
* MLB
* Soccer
* NHL

---

## Event Source

Events are received from an external Sports Provider through a webhook.

---

## Notification Types

Support notifications for:

* Player scored
* Lead changed
* Match started
* Match ended
* Overtime started
* Breaking news (future)

---

## Delivery Latency

Target:

* 2–5 seconds

---

## Reliability

* At-least-once delivery
* Minimize duplicate notifications

---

## User Preferences

Users can subscribe to:

* Teams
* Players
* Leagues
* Matches

Users can also configure notification preferences such as:

* Score updates
* Final score only
* DND (Do Not Disturb) hours

---

# Functional Requirements

* Subscribe to teams, players, leagues, and matches.
* Unsubscribe.
* Deliver push notifications.
* Respect user notification preferences.
* Retry failed notifications.
* Support millions of users.

---

# Non-Functional Requirements

* Low latency
* Highly available
* Horizontally scalable
* Fault tolerant
* Millions of concurrent users
* At-least-once delivery

---

# High-Level Architecture

```text
                  Sports Provider
                         │
                         ▼
            Telemetry Ingestion Service
                         │
                         ▼
                 Kafka (match-events)
                         │
                         ▼
          Match Event Processing Service
        (Sport-specific Business Logic)
                         │
                         ▼
                Kafka (domain-events)
                         │
                         ▼
               Notification Service
                         │
          Lookup Followers (Redis)
                         │
                         ▼
                     Fan-out
                         │
                         ▼
            Kafka (notification-jobs)
                         │
                         ▼
              Notification Workers
                         │
                         ▼
                        APNs
                         │
                         ▼
                  Apple Sports App
```

---

# Why Three Kafka Topics?

## Topic 1 - match-events

Published by the Telemetry Ingestion Service.

Contains raw provider events.

Examples:

* GOAL
* SHOT_MADE
* REBOUND
* FOUL
* MATCH_STARTED
* MATCH_ENDED
* PLAYER_POSITION_CHANGED
* HEARTBEAT

These events are provider-specific.

---

## Topic 2 - domain-events

Published by the Match Event Processing Service.

Contains business events.

Examples:

* PLAYER_SCORED
* LEAD_CHANGED
* MATCH_STARTED
* MATCH_COMPLETED
* OVERTIME_STARTED

This creates a **canonical event model** for all downstream consumers.

Benefits:

* Removes provider-specific details.
* Centralizes business logic.
* Downstream services remain independent of the sports provider.
* Makes switching providers much easier.

---

## Topic 3 - notification-jobs

Created after fan-out.

Each message represents a notification for one user.

Example:

```json
{
  "userId": 101,
  "deviceToken": "...",
  "title": "Lakers scored!",
  "matchId": 123
}
```

Notification Workers consume this topic and send notifications through APNs.

---

# Why Match Event Processing?

Without this layer, every downstream service must filter raw provider events.

Example:

Raw provider events:

* PLAYER_POSITION_CHANGED
* HEARTBEAT
* PLAYER_SPEED_UPDATED
* GOAL

Notification Service would need logic like:

```java
if(event == GOAL)
    notify();

else if(event == HEARTBEAT)
    ignore();

else if(event == PLAYER_POSITION_CHANGED)
    ignore();
```

Every downstream consumer would duplicate this filtering.

Instead:

```text
Raw Events

↓

Match Event Processing

↓

Canonical Domain Events

↓

Consumers
```

This centralizes business rules and simplifies downstream services.

---

# Notification Service Responsibilities

* Consume domain events.
* Lookup interested users.
* Apply notification preferences.
* Respect DND hours.
* Perform fan-out.
* Publish notification jobs.

The Notification Service does **not** send push notifications directly.

---

# Subscription Model

Do not model subscriptions as:

```text
User

↓

Favorite Team
```

Instead, create an inverted index.

Redis:

```text
followers:team:Lakers

↓

{101,205,340,...}
```

```text
followers:player:LeBron

↓

{101,455,901,...}
```

```text
followers:league:NBA

↓

{100,200,300,...}
```

```text
followers:match:123

↓

{10,45,89,...}
```

Benefits:

* O(1) lookup
* No user scanning
* Very scalable

---

# Fan-out

Input:

One business event.

```text
PLAYER_SCORED
```

Output:

Millions of notification jobs.

```text
PLAYER_SCORED

↓

Notification Service

↓

Lookup Followers

↓

User101

User102

User103

...

User2000000
```

Each user receives an independent notification job.

This is the **fan-out** stage.

---

# Notification Workers

Workers consume:

```text
notification-jobs
```

Responsibilities:

* Build APNs request
* Call APNs
* Retry failures
* Send failed requests to DLQ
* Update notification status (if persistence is required)

Workers contain no business logic.

They only deliver notifications.

---

# Redis

Redis stores:

## Subscription Index

```text
followers:team:Lakers
```

## User Preferences

```text
user:101

deviceToken

notificationPreferences

DND Hours
```

Redis acts as a cache.

---

# PostgreSQL

Stores:

* User subscriptions
* User notification preferences
* Device registrations
* Notification history (optional)

PostgreSQL is the system of record.

Redis caches frequently accessed data.

---

# Fan-out Partitioning

Do **not** partition notification jobs by:

```text
matchId
```

Reason:

A popular match could create millions of notifications that all go to one Kafka partition, creating a hot partition.

Instead:

```text
Partition Key = userId
```

Benefits:

* Even load distribution.
* Preserves notification ordering for each user.
* Scales horizontally.

---

# Processing Flow

```text
Receive Domain Event

↓

Lookup Followers

↓

Merge Followers
(team/player/league/match)

↓

Remove Duplicate Users

↓

Apply User Preferences

↓

Create Notification Jobs

↓

Publish notification-jobs Topic
```

---

# Notification Delivery Flow

```text
Notification Worker

↓

Read Notification Job

↓

Call APNs

↓

Success

↓

ACK Kafka Offset
```

If delivery fails:

```text
Retry

↓

DLQ

↓

Manual Investigation
```

---

# Design Patterns

* Event-Driven Architecture
* Strategy Pattern (sport-specific business rules in Match Event Processing)
* Publish-Subscribe
* Fan-out
* Inverted Index
* Worker Queue
* Retry with DLQ

---

# Key Design Decisions

* Separate raw provider events from business events using a Match Event Processing Service.
* Use a canonical event model so downstream services are independent of the sports provider.
* Store follower mappings as inverted indexes in Redis.
* Perform fan-out by creating one notification job per user.
* Use a dedicated Kafka topic for notification jobs.
* Partition notification jobs by `userId`, not `matchId`, to avoid hot partitions and preserve per-user notification ordering.
* Isolate APNs integration inside Notification Workers.
* Use PostgreSQL as the source of truth and Redis as a high-performance cache.


# Live Sports Notification Service - Duplicate Notifications & Delivery Semantics

## Problem

The Notification Workers consume notification jobs from Kafka and deliver them to Apple Push Notification service (APNs).

Kafka provides **at-least-once delivery**, which means a notification job may be delivered to a worker more than once.

Example:

```text
Notification Worker

↓

Call APNs

↓

APNs returns SUCCESS

↓

Worker crashes

↓

Kafka Offset NOT committed

↓

Kafka redelivers message

↓

Duplicate notification
```

Without additional safeguards, users may receive duplicate push notifications.

---

# Why Exactly-Once Is Difficult

Three independent systems are involved:

```text
Kafka

Redis

APNs
```

There is no distributed transaction spanning all three systems.

Because APNs is an external service, we cannot guarantee true exactly-once notification delivery.

The practical goal is:

* At-least-once delivery
* Minimize duplicate notifications
* Provide recovery for failures

---

# Notification ID

During fan-out, generate a deterministic notification identifier.

Example:

```text
notificationId =
hash(userId + eventId + notificationType)
```

Every notification job carries this identifier.

Example:

```json
{
  "notificationId": "abc123",
  "userId": 101,
  "eventId": 56789,
  "title": "Lakers scored!"
}
```

---

# Idempotency

Workers perform an idempotency check before processing.

Example using Redis:

```text
processed-notification:{notificationId}
```

Use an atomic operation such as:

```text
SETNX(notificationId)
```

If the key already exists:

* The notification has already been claimed.
* Skip processing.

Apply a TTL (for example, 24–48 hours) so processed IDs eventually expire.

---

# Recommended Worker Flow

```text
Receive Notification Job

↓

SETNX(notificationId)

│
├── Key Exists
│       ↓
│     Skip Processing
│
└── Success
        ↓

Call APNs

↓

Commit Kafka Offset
```

This minimizes duplicate notifications while allowing workers to scale horizontally.

---

# Remaining Edge Cases

Consider this sequence:

```text
SETNX(notificationId)

↓

Worker crashes

↓

Kafka redelivers message
```

The next worker observes the Redis key and skips processing.

However, the first worker may have crashed:

* Before calling APNs
* During the APNs request
* After APNs accepted the request

Because APNs is an external system, the application cannot determine exactly where the failure occurred.

This is one reason true exactly-once delivery cannot be guaranteed.

---

# Improving Reliability

Instead of storing only "processed", maintain a processing status.

Example:

```text
notificationId

Status = PENDING
```

Worker flow:

```text
Receive Job

↓

Create/Claim notificationId

Status = PENDING

↓

Call APNs

↓

Status = SENT

↓

Commit Kafka Offset
```

If a worker crashes, notification records remaining in the **PENDING** state can be identified by a background recovery process and retried or investigated.

---

# Design Trade-off

It is generally preferable to design for:

* At-least-once delivery
* Idempotent processing
* Recovery of incomplete work

rather than attempting distributed transactions across Kafka, Redis, and APNs.

---

# Interview Summary

* Kafka provides at-least-once message delivery.
* Notification jobs should include a deterministic `notificationId`.
* Workers use an atomic idempotency check (for example, Redis `SETNX`) to reduce duplicate processing.
* Kafka offsets are committed only after notification processing completes.
* True exactly-once delivery is not achievable because APNs is an external system outside the transaction boundary.
* Tracking notification state (such as **PENDING** and **SENT**) provides better observability and enables recovery for interrupted deliveries.


# Live Sports Notification Service - Duplicate Notifications & Delivery Semantics

## Problem

The Notification Workers consume notification jobs from Kafka and deliver them to Apple Push Notification service (APNs).

Kafka provides **at-least-once delivery**, which means a notification job may be delivered to a worker more than once.

Example:

```text
Notification Worker

↓

Call APNs

↓

APNs returns SUCCESS

↓

Worker crashes

↓

Kafka Offset NOT committed

↓

Kafka redelivers message

↓

Duplicate notification
```

Without additional safeguards, users may receive duplicate push notifications.

---

# Why Exactly-Once Is Difficult

Three independent systems are involved:

```text
Kafka

Redis

APNs
```

There is no distributed transaction spanning all three systems.

Because APNs is an external service, we cannot guarantee true exactly-once notification delivery.

The practical goal is:

* At-least-once delivery
* Minimize duplicate notifications
* Provide recovery for failures

---

# Notification ID

During fan-out, generate a deterministic notification identifier.

Example:

```text
notificationId =
hash(userId + eventId + notificationType)
```

Every notification job carries this identifier.

Example:

```json
{
  "notificationId": "abc123",
  "userId": 101,
  "eventId": 56789,
  "title": "Lakers scored!"
}
```

---

# Idempotency

Workers perform an idempotency check before processing.

Example using Redis:

```text
processed-notification:{notificationId}
```

Use an atomic operation such as:

```text
SETNX(notificationId)
```

If the key already exists:

* The notification has already been claimed.
* Skip processing.

Apply a TTL (for example, 24–48 hours) so processed IDs eventually expire.

---

# Recommended Worker Flow

```text
Receive Notification Job

↓

SETNX(notificationId)

│
├── Key Exists
│       ↓
│     Skip Processing
│
└── Success
        ↓

Call APNs

↓

Commit Kafka Offset
```

This minimizes duplicate notifications while allowing workers to scale horizontally.

---

# Remaining Edge Cases

Consider this sequence:

```text
SETNX(notificationId)

↓

Worker crashes

↓

Kafka redelivers message
```

The next worker observes the Redis key and skips processing.

However, the first worker may have crashed:

* Before calling APNs
* During the APNs request
* After APNs accepted the request

Because APNs is an external system, the application cannot determine exactly where the failure occurred.

This is one reason true exactly-once delivery cannot be guaranteed.

---

# Improving Reliability

Instead of storing only "processed", maintain a processing status.

Example:

```text
notificationId

Status = PENDING
```

Worker flow:

```text
Receive Job

↓

Create/Claim notificationId

Status = PENDING

↓

Call APNs

↓

Status = SENT

↓

Commit Kafka Offset
```

If a worker crashes, notification records remaining in the **PENDING** state can be identified by a background recovery process and retried or investigated.

---

# Design Trade-off

It is generally preferable to design for:

* At-least-once delivery
* Idempotent processing
* Recovery of incomplete work

rather than attempting distributed transactions across Kafka, Redis, and APNs.

---

# Interview Summary

* Kafka provides at-least-once message delivery.
* Notification jobs should include a deterministic `notificationId`.
* Workers use an atomic idempotency check (for example, Redis `SETNX`) to reduce duplicate processing.
* Kafka offsets are committed only after notification processing completes.
* True exactly-once delivery is not achievable because APNs is an external system outside the transaction boundary.
* Tracking notification state (such as **PENDING** and **SENT**) provides better observability and enables recovery for interrupted deliveries.


# Live Sports Notification Service - Idempotency & Duplicate Handling

## Problem

Kafka provides **at-least-once delivery**.

If a consumer crashes before committing the Kafka offset, Kafka redelivers the same message.

Without idempotency, duplicate notifications may be created or delivered.

---

# Duplicate Domain Event Example

Suppose the Match Event Processing Service publishes:

```text
PLAYER_SCORED

eventId = 500
```

Notification Service consumes the event.

It looks up followers.

```text
Followers

↓

User101

User102
```

It creates two notification jobs.

```text
Notification A

user101

eventId=500
```

```text
Notification B

user102

eventId=500
```

Now suppose the Notification Service crashes **before committing the Kafka offset**.

Kafka redelivers:

```text
PLAYER_SCORED

eventId=500
```

Without idempotency, the service performs fan-out again and creates duplicate notification jobs.

---

# Why Auto-Increment IDs Do Not Work

Suppose the first fan-out creates:

```text
NotificationId = 1001
```

After replay:

```text
NotificationId = 1002
```

Although they represent the same logical notification, the IDs are different.

Redis cannot detect duplicates.

---

# Deterministic Notification ID

Instead, generate a deterministic identifier.

Example:

```text
notificationId =
userId:eventId:notificationType
```

For example:

```text
101:500:PLAYER_SCORED
```

If the event is replayed, the same notification generates the same identifier.

This allows duplicate detection.

---

# EventId vs NotificationId

This is an important distinction.

## Notification Service

Unit of work:

```text
One Domain Event
```

Example:

```text
PLAYER_SCORED

eventId=500
```

Here, **eventId** alone is sufficient.

If Kafka redelivers the same domain event, the Notification Service can detect that `eventId=500` has already been processed and skip fan-out.

---

## Notification Worker

Unit of work:

```text
One Notification Job
```

After fan-out:

```text
eventId=500

↓

User101

User102

User103
```

All notification jobs share the same eventId.

If the worker used only `eventId` for idempotency:

```text
User101

eventId=500

↓

Sent Successfully
```

Then:

```text
User102

eventId=500
```

would incorrectly be considered a duplicate.

User102 would never receive the notification.

Therefore the worker requires a unique identifier per user.

Example:

```text
notificationId =
userId:eventId
```

or

```text
userId:eventId:notificationType
```

---

# Two Layers of Idempotency

## Layer 1 – Notification Service

Consumes:

```text
Domain Event
```

Uses:

```text
eventId
```

Purpose:

Prevent duplicate fan-out.

This avoids creating millions of duplicate notification jobs.

---

## Layer 2 – Notification Worker

Consumes:

```text
Notification Job
```

Uses:

```text
notificationId

(userId + eventId + notificationType)
```

Purpose:

Prevent duplicate delivery to APNs.

This provides a second safety net in case duplicate notification jobs are produced.

---

# Worker Processing Flow

```text
Receive Notification Job

↓

notificationId already processed?

        │
   ┌────┴────┐
   │         │
  Yes       No
   │         │
   ▼         ▼
 Skip     Call APNs

              ↓

      Commit Kafka Offset
```

---

# Why Two Layers?

Notification Service idempotency:

* Prevents duplicate fan-out.
* Saves Kafka bandwidth.
* Reduces unnecessary worker load.

Worker idempotency:

* Protects against duplicate notification jobs.
* Prevents duplicate push notifications reaching users.

Together they provide a robust solution for Kafka's at-least-once delivery model.

---

# Interview Summary

* Kafka may redeliver messages if offsets are not committed.
* The Notification Service should use `eventId` to prevent duplicate fan-out of the same domain event.
* Notification Workers process user-specific notification jobs, so `eventId` alone is insufficient.
* Workers should use a deterministic per-user identifier such as `userId + eventId + notificationType`.
* Auto-increment IDs or random UUIDs are unsuitable for idempotency because retries generate different values.
* Applying idempotency at both the Notification Service and the Notification Worker minimizes duplicate work and duplicate notifications while maintaining at-least-once delivery.

# Live Sports Notification Service - Subscription Management

## Goal

Allow users to follow and unfollow:

* Teams
* Players
* Leagues
* Matches

These subscriptions determine who receives notifications during fan-out.

---

# REST APIs

Follow a team:

```http
POST /v1/teams/{teamId}/follow
Authorization: Bearer <JWT>
```

The authenticated user's identity is obtained from the JWT or session.

Do **not** pass `userId` in the request body.

---

Unfollow:

```http
DELETE /v1/teams/{teamId}/follow
```

Similarly:

```http
POST /v1/players/{playerId}/follow

POST /v1/leagues/{leagueId}/follow

POST /v1/matches/{matchId}/follow
```

---

# PostgreSQL Schema

## UserTeamSubscription

```text
userId

teamId

createdAt
```

Composite Primary Key:

```text
(userId, teamId)
```

Benefits:

* Prevents duplicate subscriptions.
* Fast lookup.
* Enforces data integrity.

Similarly:

```text
UserPlayerSubscription

(userId, playerId)
```

```text
UserLeagueSubscription

(userId, leagueId)
```

```text
UserMatchSubscription

(userId, matchId)
```

---

# Redis Data Structures

## Inverted Index (used during fan-out)

```text
followers:team:Lakers

↓

{101,205,340,...}
```

```text
followers:player:LeBron

↓

{101,455,901,...}
```

```text
followers:league:NBA

↓

{100,200,300,...}
```

```text
followers:match:123

↓

{10,45,89,...}
```

Purpose:

Quickly identify all users interested in an event without scanning every user.

---

## Forward Index

```text
subscriptions:user:101

↓

Lakers

NBA

LeBron

Match123
```

Purpose:

Support APIs such as:

```http
GET /v1/users/me/subscriptions
```

without querying PostgreSQL.

---

# Cache Synchronization Approaches

## Option 1 - Synchronous Update (Simple)

```text
Follow API

↓

Insert PostgreSQL

(COMMIT)

↓

Update Redis

↓

Return 200
```

Advantages:

* Very simple.
* Redis updated immediately.
* Easy to implement.

Disadvantages:

* API depends on Redis.
* Redis failures require retries or error handling.
* API has two responsibilities:

  * Persist data
  * Maintain cache

This is an excellent solution for small and medium-sized systems.

---

## Option 2 - CDC with Debezium + Kafka (Recommended for Large Systems)

```text
Follow API

↓

PostgreSQL

(COMMIT)

↓

Return 200

↓

Debezium (CDC)

↓

Kafka

(subscription-events)

↓

Subscription Cache Service

↓

Redis
```

Flow:

1. API inserts into PostgreSQL.
2. PostgreSQL commits.
3. Debezium captures the database change from the Write-Ahead Log (WAL).
4. Debezium publishes a subscription event to Kafka.
5. Subscription Cache Service consumes the event.
6. Redis indexes are updated.

Benefits:

* PostgreSQL remains the source of truth.
* API is completely decoupled from Redis.
* Durable event stream.
* Replay capability.
* Additional services can consume subscription events in the future.
* Better scalability and maintainability.

Trade-off:

Redis becomes eventually consistent with PostgreSQL.

---

## Option 3 - CDC Directly to Redis

```text
Follow API

↓

PostgreSQL

↓

Debezium

↓

Redis
```

Benefits:

* Fewer components.
* Lower latency.

Limitations:

* No Kafka replay.
* Harder to support additional downstream consumers.
* Less resilient if Redis is unavailable.

---

# Which Approach to Choose?

## Small / Medium Systems

```text
PostgreSQL

↓

Redis
```

Simple and perfectly acceptable.

---

## Enterprise Systems (Apple Scale)

```text
PostgreSQL

↓

Debezium

↓

Kafka

↓

Subscription Cache Service

↓

Redis
```

This provides loose coupling, replay, durability, and allows future consumers (analytics, recommendations, auditing, etc.) to subscribe without changing the Follow API.

---

# Interview Summary

* Authenticate users via JWT/session instead of accepting `userId` in the request.
* Store subscriptions in PostgreSQL using composite primary keys to prevent duplicates.
* Maintain both:

  * A **forward index** (`subscriptions:user:{userId}`) for user-facing APIs.
  * An **inverted index** (`followers:team:{teamId}`) for efficient fan-out.
* For simpler systems, update Redis synchronously after committing PostgreSQL.
* For large-scale systems, use **Debezium + Kafka + Subscription Cache Service** to synchronize Redis asynchronously while keeping PostgreSQL as the source of truth.


# Live Sports Notification Service - APNs Integration, Device Registration & Failure Handling

# APNs Overview

Apple Push Notification service (APNs) does **not** recognize application user IDs.

APNs routes notifications using **device tokens**.

The Notification Worker must translate:

```text
userId

↓

deviceToken(s)

↓

APNs
```

---

# Device Registration Flow

When the Apple Sports app launches for the first time:

```text
Apple Sports App

↓

Register with APNs

↓

Receive Device Token

↓

POST /v1/devices

↓

Notification Backend
```

The application authenticates using the user's JWT/session.

The backend associates the authenticated user with the device token.

---

# Device Registration API

```http
POST /v1/devices
Authorization: Bearer <JWT>
```

Example payload:

```json
{
  "deviceToken": "abc123xyz...",
  "platform": "iOS"
}
```

The backend extracts the authenticated user from the JWT and stores the mapping.

---

# PostgreSQL Schema

```text
UserDevice

------------------------

userId

deviceToken

platform

isActive

lastSeen

createdAt
```

Composite Primary Key:

```text
(userId, deviceToken)
```

---

# Redis Cache

Cache active devices for fast lookup.

```text
devices:user:101

↓

deviceToken1

deviceToken2

deviceToken3
```

This avoids querying PostgreSQL for every notification.

---

# Multiple Devices

One user may own several Apple devices.

Example:

```text
User101

├── iPhone
├── iPad
└── MacBook
```

Each device has its own APNs device token.

The Notification Worker sends the same notification to every active device.

---

# Notification Worker Flow

```text
Receive Notification Job

↓

userId

↓

Lookup Active Device Tokens
(Redis)

↓

deviceToken1

deviceToken2

deviceToken3

↓

Call APNs
```

---

# Why Store userId Instead of deviceToken?

## Preferred Design

Notification Job:

```json
{
  "notificationId": "...",
  "userId": 101,
  "eventId": 500,
  "payload": { ... }
}
```

Worker performs device lookup at processing time.

Benefits:

* Always uses the latest device tokens.
* Handles users with multiple devices naturally.
* Supports token rotation without recreating queued jobs.
* Keeps notification jobs smaller.

---

## Alternative

Store the device token inside every notification job.

Example:

```json
{
  "deviceToken": "...",
  "payload": { ... }
}
```

Disadvantages:

* Device tokens may become stale.
* Multiple jobs are required for users with multiple devices.
* Harder to handle token updates.

---

# APNs Failure Handling

## Successful Response

```text
Notification Worker

↓

Call APNs

↓

2xx Success

↓

Commit Kafka Offset
```

---

## Transient Failures

Examples:

* Timeout
* Network failure
* HTTP 429
* HTTP 500
* HTTP 503

Handling:

* Retry
* Exponential Backoff
* Random Jitter

Example:

```text
Retry 1

1 second

↓

Retry 2

2 seconds

↓

Retry 3

4 seconds

↓

DLQ
```

Random jitter prevents thousands of workers from retrying simultaneously.

---

# Circuit Breaker

Wrap APNs calls with a circuit breaker.

```text
Notification Worker

↓

Circuit Closed

↓

Call APNs
```

If failure rate exceeds the configured threshold:

```text
Circuit Opens

↓

Fail Fast

↓

Do not call APNs
```

After a cool-down period:

```text
Half Open

↓

Try a small number of requests

↓

Success?

↓

Close Circuit

Else

Open Again
```

Benefits:

* Prevents overwhelming APNs during outages.
* Conserves worker resources.
* Improves overall system stability.

---

# Dead Letter Queue

If retries are exhausted:

```text
Notification Worker

↓

Retry 3 Times

↓

notification-dlq
```

The DLQ enables:

* Manual investigation
* Replay
* Alerting
* Operational visibility

---

# Permanent Failures

Some APNs errors should **not** be retried.

Examples:

* Invalid device token
* Unregistered device
* Malformed payload
* Authentication errors caused by invalid requests

Handling:

* Mark notification as failed.
* Mark device token as inactive in PostgreSQL.
* Remove the device token from Redis.
* Commit the Kafka offset.

Retrying permanent failures only wastes resources.

---

# Final Notification Delivery Flow

```text
Notification Worker

↓

Receive Notification Job

↓

Lookup Device Tokens

↓

Circuit Breaker

↓

Call APNs

        │
        │
   Success
        │
        ▼
Commit Kafka Offset

        │
        │
Transient Failure
        │
        ▼
Retry + Exponential Backoff + Jitter

        │
        ▼
Retry Limit Reached

↓

notification-dlq

        │
        │
Permanent Failure
        │
        ▼
Deactivate Device Token

↓

Commit Kafka Offset
```

---

# Interview Summary

* APNs delivers notifications using **device tokens**, not application user IDs.
* Users register their device tokens through a Device Registration API after obtaining a token from APNs.
* Notification jobs should contain `userId`; the worker resolves the latest active device tokens at delivery time.
* Support multiple devices per user by storing multiple active device tokens.
* Protect APNs calls with a circuit breaker.
* Retry transient failures using exponential backoff with jitter.
* Send jobs to a DLQ after the retry limit.
* Do not retry permanent APNs errors such as invalid or unregistered device tokens. Instead, deactivate the token and remove it from the cache.

The One Sentence to Remember

RabbitMQ = "Who should do this work?" (Task Queue)

Kafka = "Who wants to know this happened?" (Event Log)
	