# Apple Round 1 Mock Interview
## Problem: Compare Two Players API

---

## Problem Statement

Apple Sports wants to introduce a **Player Comparison** feature.

Users should be able to compare two players and view their season statistics side-by-side.

Design the API and implement the backend in Java.

---

# Step 1 - Clarify Requirements

Before jumping into coding, clarify the problem.

### Question 1

**Candidate**

> Are both players guaranteed to belong to the same sport?

**Interviewer**

Yes.

Assume both players belong to the **NBA**.

---

### Question 2

**Candidate**

> Are we designing a generic API that supports multiple sports, or should we optimize for basketball?

**Interviewer**

Optimize for **basketball**.

Don't over-engineer for multiple sports.

---

### Question 3

**Candidate**

> What exactly are we comparing?
>
> Current season statistics?
> Career statistics?
> Live game statistics?

**Interviewer**

Current season statistics.

---

### Question 4

**Candidate**

> Are the statistics already stored in a datastore, or do I need to calculate them from raw game events?

**Interviewer**

Assume they already exist.

This API only retrieves them.

---

### Question 5

**Candidate**

> Should the API simply return both players' statistics, or should it determine who performed better?

**Interviewer**

Return both players' statistics.

Do **not** calculate an overall winner.

Different users may value different statistics differently.

---

# Final Requirements

- NBA players only
- Same sport
- Compare exactly two players
- Compare current season statistics
- Statistics already exist in storage
- Read-only API
- Return both players' statistics
- Do not calculate a winner

---

# Requirement Summary

Before designing the API, summarize the requirements.

Example:

> We're designing a read-only REST API for Apple Sports that compares two NBA players. The API accepts two player IDs, retrieves their current season statistics from the backend datastore, and returns those statistics side-by-side. It does not calculate statistics or determine an overall winner.

This confirms your understanding before moving into design.

---

# Step 2 - REST API Design

Use **GET** since this is a read-only operation.

```http
GET /v1/players/compare?player1Id=23&player2Id=30
```

## Why GET?

Because

- the API retrieves data
- it does not modify server state
- therefore GET is the correct HTTP verb

---

## Why IDs Instead Of Names?

Use

```text
playerId
```

instead of

```text
LeBron James
Stephen Curry
```

because

- IDs are unique
- Names are not guaranteed to be unique
- Internal databases typically use primary keys

---

# Step 3 - Response DTO

```json
{
  "player1": {
    "id": 23,
    "name": "LeBron James",
    "pointsPerGame": 28.1,
    "reboundsPerGame": 8.2,
    "assistsPerGame": 7.4
  },
  "player2": {
    "id": 30,
    "name": "Stephen Curry",
    "pointsPerGame": 30.3,
    "reboundsPerGame": 5.2,
    "assistsPerGame": 6.8
  }
}
```

Simple.

Readable.

Returns exactly what the client needs.

---

# Possible Error Responses

## Player Not Found

HTTP

```text
404 Not Found
```

Response

```json
{
  "errorCode": "PLAYER_NOT_FOUND",
  "message": "Player 30 does not exist"
}
```

---

## Invalid Request

```text
400 Bad Request
```

Examples

- Missing playerId
- Same player selected twice
- Invalid playerId

---

# What Comes Next?

Many candidates think the interview is finished after designing the API.

It is **not**.

The interviewer will likely say:

> Great.
>
> Now implement the backend.

This is where the coding portion begins.

---

# Next Steps

The next phase is typically:

```text
REST API
        │
        ▼
PlayerController
        │
        ▼
PlayerComparisonService
        │
        ▼
PlayerRepository
        │
        ▼
Redis / Cassandra
```

The interviewer will then ask you to implement the Java code.

---

# Interview Tips

Do **not** jump directly into coding.

Instead say something like:

> Before I start coding, I'd like to outline the backend components and their responsibilities.

This demonstrates that you're thinking like a backend engineer building a production service, rather than simply writing a method.

# Step 4 - Backend Design

After discussing the API, the interviewer says:

> Great.
>
> Now let's implement the backend.

Before writing code, briefly outline the backend components.

```
                GET /v1/players/compare
                          │
                          ▼
                 PlayerController
                          │
                          ▼
             PlayerComparisonService
                          │
                          ▼
              PlayerStatsRepository
                          │
                          ▼
                Redis / Cassandra
```

---

# Responsibilities

## PlayerController

Responsibilities

- Accept HTTP request
- Validate request parameters
- Delegate to service
- Return HTTP response

The controller should contain **minimal business logic**.

---

## PlayerComparisonService

Responsibilities

- Validate business rules
- Retrieve both players
- Handle missing players
- Build comparison response
- Return DTO

This layer owns the business logic.

---

## PlayerStatsRepository

Responsibilities

- Retrieve player statistics
- Hide datastore implementation
- Return domain objects

Example implementation

- Redis
- Cassandra
- PostgreSQL

The service should not know where data comes from.

---

# Class Design

## PlayerController

```java
@RestController
@RequestMapping("/v1/players")
public class PlayerController {

    private final PlayerComparisonService service;

    @GetMapping("/compare")
    public PlayerComparisonResponse comparePlayers(
            @RequestParam Long player1Id,
            @RequestParam Long player2Id) {

        return service.comparePlayers(player1Id, player2Id);
    }
}
```

Notice

The controller simply delegates.

No business logic.

---

## Service

```java
public interface PlayerComparisonService {

    PlayerComparisonResponse comparePlayers(
            Long player1Id,
            Long player2Id);

}
```

---

## Repository

```java
public interface PlayerStatsRepository {

    Optional<PlayerStats> findByPlayerId(Long playerId);

}
```

---

# Domain Model

```java
class PlayerStats {

    Long playerId;

    String playerName;

    double pointsPerGame;

    double reboundsPerGame;

    double assistsPerGame;

}
```

---

# Response DTO

```java
class PlayerComparisonResponse {

    PlayerStats player1;

    PlayerStats player2;

}
```

---

# Step 5 - Implement Business Logic

The interviewer now asks:

> Please implement comparePlayers().

Example

```java
public PlayerComparisonResponse comparePlayers(
        Long player1Id,
        Long player2Id) {

    if(player1Id.equals(player2Id)){
        throw new IllegalArgumentException(
            "Players must be different");
    }

    PlayerStats player1 =
        repository.findByPlayerId(player1Id)
                  .orElseThrow(() ->
                      new PlayerNotFoundException(player1Id));

    PlayerStats player2 =
        repository.findByPlayerId(player2Id)
                  .orElseThrow(() ->
                      new PlayerNotFoundException(player2Id));

    return new PlayerComparisonResponse(player1, player2);
}
```

---

# What The Interviewer Is Evaluating

Notice that this code is intentionally simple.

The interviewer is evaluating

- Clean code
- Separation of responsibilities
- OOP principles
- Exception handling
- Readability
- Naming conventions

This is **not** an algorithms problem.

---

# Discussion Points

While coding, explain your choices.

Examples

> "I'm keeping the controller thin and placing the business logic inside the service."

> "The repository hides the persistence layer so that we can switch databases without changing the service."

> "I'm returning Optional from the repository to explicitly handle missing players."

These explanations demonstrate senior-level design thinking.

---

# Possible Follow-Up Questions

The interviewer may now ask:

- What if one player doesn't exist?
- What if millions of users call this API?
- How would you cache the results?
- Where would Redis fit?
- Can we fetch both players concurrently?
- How would you unit test this service?

These follow-up questions often lead into discussions about concurrency, caching, and scalability.

# Step 6 - Edge Cases, Concurrency & Scalability

After implementing the service, the interviewer begins asking follow-up questions.

This is where the discussion shifts from coding to production engineering.

---

# Follow-up 1

## What if one player does not exist?

Return

```http
404 Not Found
```

Example

```json
{
  "errorCode":"PLAYER_NOT_FOUND",
  "message":"Player 23 does not exist."
}
```

---

# Follow-up 2

## What if both player IDs are identical?

Example

```http
GET /players/compare?player1Id=23&player2Id=23
```

Return

```http
400 Bad Request
```

Reason

A player cannot be compared against themselves.

Validate this in the service layer before querying the database.

---

# Follow-up 3

## Can we retrieve both players concurrently?

Suppose fetching each player takes

```
100 ms
```

Sequential execution

```
Player1 100 ms

↓

Player2 100 ms

↓

Total ≈ 200 ms
```

Instead, fetch both simultaneously.

Example

```java
CompletableFuture<PlayerStats> player1Future =
    CompletableFuture.supplyAsync(() ->
        repository.findByPlayerId(player1Id)
                  .orElseThrow(() ->
                      new PlayerNotFoundException(player1Id)));

CompletableFuture<PlayerStats> player2Future =
    CompletableFuture.supplyAsync(() ->
        repository.findByPlayerId(player2Id)
                  .orElseThrow(() ->
                      new PlayerNotFoundException(player2Id)));

CompletableFuture.allOf(player1Future, player2Future).join();

return new PlayerComparisonResponse(
        player1Future.join(),
        player2Future.join());
```

### Why CompletableFuture?

- Both lookups are independent.
- They can execute in parallel.
- Overall latency is reduced.

---

# Follow-up 4

## How would you improve performance?

Use Redis.

```
API

↓

Redis

↓

Cache Miss

↓

Database

↓

Update Cache

↓

Return Response
```

Frequently accessed player statistics can be cached.

Benefits

- Lower latency
- Reduced database load
- Higher throughput

---

# Follow-up 5

## What if statistics are updated during a live game?

The API should **not** calculate statistics.

Instead

```
Sports Provider

↓

Kafka

↓

Statistics Consumer

↓

Redis

↓

Cassandra

↓

Compare Player API
```

The Compare Player API becomes a simple read service.

---

# Follow-up 6

## What if Redis is unavailable?

Fallback

```
Redis

↓

Cache Miss / Failure

↓

Cassandra

↓

Return Response
```

Optionally

```
↓

Refresh Redis
```

This ensures high availability.

---

# Follow-up 7

## How would you scale this API?

The service is stateless.

Deploy multiple instances behind a load balancer.

```
                Load Balancer

        /            |            \

Instance 1    Instance 2    Instance 3

          ↓

       Shared Redis

          ↓

      Cassandra
```

Since no session state is stored locally, requests can be routed to any instance.

---

# Follow-up 8

## How would you authenticate users?

Example

```
Client

↓

API Gateway

↓

JWT Validation

↓

Player Service
```

The Compare Player API trusts the authenticated identity from the gateway.

---

# Follow-up 9

## How would you unit test this service?

Mock

```
PlayerRepository
```

Verify

- Player exists
- Player missing
- Same player selected
- Repository throws exception
- Successful response

The service can be tested independently without requiring a database.

---

# Interview Tip

Notice the interview has evolved naturally.

```
Clarify Requirements

↓

REST API

↓

DTO

↓

Controller

↓

Service

↓

Repository

↓

Business Logic

↓

Concurrency

↓

Caching

↓

Scalability
```

The interviewer is evaluating far more than your ability to write Java.

They're looking for production-ready engineering decisions and your ability to explain trade-offs while collaborating.

# Step 7 - Evolving Requirements

One common senior-level interview technique is to change the requirements after the initial solution.

The interviewer wants to evaluate:

- Flexibility
- Extensibility
- Ability to discuss trade-offs
- Communication

---

# Scenario 1

## Requirement Change

> We now want to compare **three players** instead of two.

---

## Initial Design

```http
GET /v1/players/compare?player1Id=23&player2Id=30
```

Problem

The API only supports two players.

---

## Improved Design

Instead of two query parameters

```http
GET /v1/players/compare?playerIds=23,30,45
```

or

```http
GET /v1/players/compare?playerIds=23&playerIds=30&playerIds=45
```

Now the API naturally scales to

- 2 players
- 3 players
- N players

without introducing a new endpoint.

---

## Service Method

Instead of

```java
comparePlayers(Long player1Id,
               Long player2Id)
```

Use

```java
comparePlayers(List<Long> playerIds)
```

This is easier to extend.

---

## Response

```json
{
  "players": [
    {
      "id": 23,
      "name": "LeBron James"
    },
    {
      "id": 30,
      "name": "Stephen Curry"
    },
    {
      "id": 77,
      "name": "Luka Doncic"
    }
  ]
}
```

---

# Scenario 2

## Requirement Change

> Support multiple sports.

Instead of

```java
BasketballPlayer
```

consider

```java
Player
```

Add

```java
sportType
```

Example

```java
enum SportType {

    NBA,
    NFL,
    MLB,
    NHL

}
```

The API becomes

```http
GET /v1/players/compare?playerIds=23,30&sport=NBA
```

or

```http
GET /v1/nba/players/compare?playerIds=23,30
```

---

# Scenario 3

## Requirement Change

> Compare live statistics.

Previously

```
API

↓

Database
```

Now

```
Sports Provider

↓

Kafka

↓

Statistics Consumer

↓

Redis

↓

Compare API
```

The Compare API remains unchanged.

Only the data source changes.

This is an example of good separation of concerns.

---

# Scenario 4

## Requirement Change

> Response must be under 50 ms.

Possible optimizations

- Redis cache
- Precomputed statistics
- Parallel lookups using CompletableFuture
- Read replicas
- Database indexes
- CDN (if applicable)

Trade-off

Higher memory usage in exchange for lower latency.

---

# Scenario 5

## Requirement Change

> Thousands of comparison requests per second.

Scale horizontally.

```
                Load Balancer

        /            |            \

Instance 1    Instance 2    Instance 3

          ↓

         Redis

          ↓

      Cassandra
```

The service remains stateless.

Any instance can process any request.

---

# Scenario 6

## Requirement Change

> Add player rankings later.

Avoid returning

```json
{
    "winner":"LeBron"
}
```

Instead return raw statistics.

The UI can decide

- highest scorer
- best rebounder
- best assister

Keeping the backend generic makes future enhancements easier.

---

# Senior Engineering Tip

When requirements change, avoid saying

> "I need to redesign everything."

Instead say

> "My current design already supports most of this. I'd make the following changes..."

This demonstrates that your design is extensible rather than rigid.

---

# Key Interview Takeaways

A strong senior engineer should naturally progress through the following flow:

```text
Clarify Requirements
        │
        ▼
Design REST API
        │
        ▼
Design Request / Response
        │
        ▼
Outline Backend Components
        │
        ▼
Implement Java Classes
        │
        ▼
Handle Validation & Errors
        │
        ▼
Discuss Concurrency
        │
        ▼
Discuss Caching
        │
        ▼
Discuss Scalability
        │
        ▼
Adapt To Changing Requirements
```

---

# Interview Sound Bite

> "I like to start by clarifying the requirements before coding. Once the requirements are clear, I design the API contract, outline the backend responsibilities, implement the core business logic, and finally discuss scalability and future extensibility. This helps ensure we're solving the right problem before optimizing the solution."

# Apple Sports Mock Interview #1
# Live Game Backend Service

---

# Problem Statement

Apple Sports is introducing a **Live Game Service**.

Design an **in-memory backend service** that manages live games.

Assume:

- No database
- Everything stored in memory
- Focus on clean Java, OOP, and concurrency

---

# Functional Requirements

Support the following operations:

- Add Game
- Get Game
- Update Score
- Delete Game

Each game contains:

- gameId
- homeTeam
- awayTeam
- homeScore
- awayScore
- MatchStatus
- scheduledStart
- actualStart
- actualEnd

---

# Step 1 - Clarify Requirements

Before writing any code, clarify the requirements.

## Candidate Questions

### Q1

Are we designing for one sport or multiple sports?

**Interviewer**

Optimize for **NBA** only.

No need for a generic sports platform.

---

### Q2

How many games should the service support?

**Interviewer**

Assume

- 5,000 concurrent live games
- 100,000 reads/sec
- 5,000 writes/sec

This is a **read-heavy system**.

---

### Q3

Can I assume this is an in-memory implementation only?

**Interviewer**

Yes.

No Redis.

No Cassandra.

No Kafka.

No persistence.

---

# Requirements Summary

- NBA only
- In-memory service
- Read-heavy workload
- Thread-safe implementation
- CRUD operations
- No persistence

---

# Step 2 - Domain Modeling

## Initial Thought

Game

↓

Team

↓

HashMap<GameId, Game>

---

## Discussion

Do we really need a Team class?

Current requirements only require

- home team
- away team

Therefore

```java
String homeTeam;

String awayTeam;
```

is sufficient.

Avoid unnecessary abstraction.

If future requirements include

- logo
- city
- coach
- roster

then introduce

```java
class Team
```

This follows the **YAGNI (You Aren't Gonna Need It)** principle.

---

# Thread-Safe Repository

Instead of

```java
HashMap<Long, Game>
```

Use

```java
ConcurrentHashMap<Long, Game>
```

Reason

- Multiple concurrent readers
- Concurrent score updates
- Thread-safe map operations

---

# Game Entity

```java
public class Game {

    private final Long gameId;

    private final String homeTeam;

    private final String awayTeam;

    private final int homeScore;

    private final int awayScore;

    private final MatchStatus status;

    private final LocalDateTime scheduledStart;

    private final LocalDateTime actualStart;

    private final LocalDateTime actualEnd;

}
```

For interview purposes, getters can be omitted or Lombok may be mentioned.

---

# Design Principles

- Keep entities lightweight.
- Avoid premature abstraction.
- Model only current requirements.
- Evolve the model when requirements change.

# Step 3 - Service Design

After defining the domain model, design the service interface.

---

# Service Interface

```java
public interface GameService {

    Game addGame(CreateGameRequest request);

    Game getGame(Long gameId);

    Game updateScore(UpdateScoreRequest request);

    void deleteGame(Long gameId);

}
```

---

# Why Not editGame()?

Instead of

```java
editGame(GameRequest request)
```

Prefer

```java
updateScore(UpdateScoreRequest request)
```

Reason

The current business requirement is only to update the score.

Using a generic `editGame()` exposes unnecessary fields that clients should not modify, such as:

- homeTeam
- awayTeam
- scheduledStart

A narrowly scoped API is:

- easier to validate
- easier to secure
- expresses business intent clearly

---

# Request Objects

## CreateGameRequest

```java
class CreateGameRequest {

    Long gameId;

    String homeTeam;

    String awayTeam;

    LocalDateTime scheduledStart;

}
```

---

## UpdateScoreRequest

```java
class UpdateScoreRequest {

    Long gameId;

    int homeScore;

    int awayScore;

    MatchStatus status;

}
```

Using separate request objects follows the Single Responsibility Principle and prevents accidental modification of unrelated fields.

---

# Step 4 - GameServiceImpl

The service implementation maintains an in-memory repository.

```java
public class GameServiceImpl implements GameService {

    private final ConcurrentHashMap<Long, Game> games =
            new ConcurrentHashMap<>();

}
```

---

# Why ConcurrentHashMap?

Requirements

- 100K reads/sec
- 5K writes/sec
- Multiple concurrent readers
- Concurrent score updates

`ConcurrentHashMap` provides thread-safe access to the map without requiring explicit synchronization for common operations.

---

# Step 5 - Implement addGame()

## Approach

Before writing code, explain the implementation.

Example:

> The implementation consists of five steps.
>
> 1. Validate the request.
> 2. Validate business rules.
> 3. Create the Game object.
> 4. Store it atomically.
> 5. Return the newly created Game.

---

## Validation

Validate:

- request is not null
- gameId is present
- homeTeam is present
- awayTeam is present
- homeTeam != awayTeam
- scheduledStart is present

---

## Business Rule

Each `gameId` uniquely identifies a game.

Duplicate game IDs are not allowed.

---

## Why NOT This?

```java
if (!games.containsKey(gameId)) {
    games.put(gameId, game);
}
```

This is **not thread-safe**.

Reason

These are two separate operations.

```
containsKey()

↓

put()
```

Another thread may insert the same game between these operations.

This is called a **check-then-act race condition**.

---

## Correct Solution

Use

```java
Game existing =
        games.putIfAbsent(game.getGameId(), game);

if (existing != null) {
    throw new GameAlreadyExistsException(game.getGameId());
}
```

`putIfAbsent()` performs:

- check
- insert

as one **atomic operation**.

No other thread can insert the same key in between.

---

# Custom Exceptions

Create domain-specific exceptions.

```java
GameAlreadyExistsException
```

```java
GameNotFoundException
```

Avoid throwing generic exceptions.

---

# Interview Tip

When asked to implement a method:

Don't immediately start coding.

Instead explain:

- overall approach
- validation
- business rules
- implementation steps

Then write the code.

Interviewers evaluate communication just as much as the implementation.

# Step 6 - Implement getGame()

## Approach

Before writing code, explain the implementation.

Example:

> The implementation consists of three steps.
>
> 1. Validate the request.
> 2. Retrieve the Game from the repository.
> 3. Return the Game or throw an exception if it does not exist.

---

# Validation

Validate

```java
gameId != null
```

If not

Throw

```java
IllegalArgumentException
```

(or a custom InvalidRequestException)

---

# Retrieve Game

```java
Game game = games.get(gameId);
```

---

# If Game Does Not Exist

Throw

```java
GameNotFoundException
```

---

# Return Game

```java
return game;
```

---

# Step 7 - deleteGame()

Implementation flow

```
Validate Request

↓

Remove Game

↓

If null

↓

Throw GameNotFoundException

↓

Return
```

Implementation

```java
Game removed = games.remove(gameId);

if (removed == null) {
    throw new GameNotFoundException(gameId);
}
```

---

# Why remove() Instead Of

```java
containsKey()

↓

remove()
```

Exactly the same reason discussed earlier.

```
containsKey()

↓

remove()
```

is not atomic.

Another thread could remove the game between these operations.

Using

```java
remove()
```

directly is simpler and atomic.

---

# Step 8 - updateScore()

Implementation flow

```
Validate Request

↓

Retrieve Game

↓

Game Exists?

↓

Update Score

↓

Return Updated Game
```

Request

```java
UpdateScoreRequest

gameId

homeScore

awayScore

status
```

Business Rules

- Game must exist
- Scores cannot be negative
- Game must not already be FINISHED
- Status transition should be valid

---

# Concurrency Discussion

Suppose two score updates arrive simultaneously.

Example

```
Update A

20-18

↓

Update B

22-20
```

How do we ensure one update does not overwrite another?

This leads to an important concurrency discussion.

---

# Thread Safety Discussion

Many candidates think

```
ConcurrentHashMap
```

solves all concurrency problems.

It does not.

It only protects the **map**.

Example

```
ConcurrentHashMap

↓

Game Object
```

Two threads can still access the same mutable Game object simultaneously.

```
Thread A

updateScore()

↓

Game

↑

Thread B

getGame()
```

The map is thread-safe.

The Game object is not.

---

# Why?

ConcurrentHashMap guarantees operations such as

```java
put()

get()

remove()

putIfAbsent()
```

are thread-safe.

It does **not** automatically make the objects stored inside the map thread-safe.

---

# Possible Solutions

Several valid approaches exist.

## Option 1

Synchronize updates.

Example

```java
synchronized updateScore(...)
```

Pros

- Easy
- Safe

Cons

- Readers block writers
- Writers block readers

---

## Option 2 (Preferred)

Immutable Game Object

Instead of modifying

```java
game.setHomeScore(...)
```

Create a new Game object.

Example

```java
Game updatedGame =
        new Game(
                gameId,
                homeTeam,
                awayTeam,
                newHomeScore,
                newAwayScore,
                status,
                scheduledStart,
                actualStart,
                actualEnd
        );
```

Then

```java
games.replace(gameId, updatedGame);
```

No existing object is ever modified.

---

# Why Is This Better?

Readers never observe partially updated state.

Old readers continue using the previous immutable Game object.

New readers automatically receive the updated Game object.

No object is modified after creation.

---

# Important Trade-Off

Suppose

Thread A

already retrieved

```
Game A

20-18
```

Thread B creates

```
Game B

22-20
```

and replaces the reference.

Thread A still returns

```
20-18
```

This is acceptable.

Thread A sees a **consistent snapshot**.

Future readers receive the latest Game object.

Immutability guarantees consistency, not that every reader always sees the newest value.

---

# Interview Question

Q:

Why not simply use ReentrantLock?

Answer

For a single map operation, explicit locking is unnecessary because ConcurrentHashMap already provides atomic operations such as

```java
putIfAbsent()
```

Locks become useful when multiple dependent operations must remain consistent.

Example

```
Update Game

↓

Update Leaderboard

↓

Update Standings

↓

Publish Notification
```

These cannot be protected by a single map operation.

---

# REST API

Once the service implementation is complete, exposing it through REST is straightforward.

```http
POST /v1/games
```

```http
GET /v1/games/{gameId}
```

```http
PUT /v1/games/{gameId}/score
```

```http
DELETE /v1/games/{gameId}
```

The controller should remain thin.

Business logic belongs inside the service.

---

# Why Service Layer?

The controller handles

- HTTP requests
- request parsing
- response generation

The service handles

- business rules
- validation
- concurrency
- domain logic

This separation allows future callers, such as Kafka consumers, to reuse the same service without duplicating logic.

---

# Key Interview Takeaways

The interviewer evaluated much more than coding ability.

Topics covered

- Requirement clarification
- Domain modeling
- OOP
- Service design
- Repository pattern
- Validation
- Custom exceptions
- ConcurrentHashMap
- Atomic operations
- Race conditions
- Mutable vs Immutable objects
- Thread safety
- REST API design
- Separation of concerns

---

# Biggest Learning

There are two separate concurrency concerns.

## 1. Protecting the container

Use

```java
ConcurrentHashMap
```

to make map operations thread-safe.

---

## 2. Protecting the objects inside the container

ConcurrentHashMap does **not** make the stored Game objects thread-safe.

Solutions include

- immutable objects
- synchronized updates
- ReadWriteLock
- atomic fields

The interview discussion emphasized understanding the trade-offs rather than memorizing a single solution.