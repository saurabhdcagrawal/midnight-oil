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
