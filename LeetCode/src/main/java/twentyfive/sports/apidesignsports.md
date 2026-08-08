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


Here is the consolidated version so far, including the correction that **association objects represent relationships and don't own the add/remove operations**.

# Sports League Management System — LLD

## 1. Goal

Design a Sports League Management System that allows users to:

* Browse sports and leagues
* View seasons
* View teams and players
* View upcoming and completed matches
* View match statistics
* View player and team statistics
* View league standings

The system should support multiple sports such as basketball, football, and soccer.

---

# 2. Domain Classes

## Sport

### Attributes

* `sportId`
* `sportName`

### Behavior

Currently no major domain behavior.

---

## League

### Attributes

* `leagueId`
* `leagueName`
* `sport`

### Behavior

Currently no major domain behavior.

### Relationship

```text
Sport 1 ───────── * League
```

A sport can have many leagues.

A league belongs to one sport.

---

## Season

### Attributes

* `seasonId`
* `seasonTitle`
* `league`

### Behavior

* `addTeam()`
* `removeTeam()`

### Relationship

```text
League 1 ───────── * Season
```

A league can have multiple seasons.

---

## Team

### Attributes

* `teamId`
* `teamTitle`
* `logo`

### Behavior

* `addPlayer()`
* `removePlayer()`

---

## SeasonTeam

Association entity between `Season` and `Team`.

### Attributes

* `season`
* `team`

### Behavior

None for now.

### Relationship

```text
Season * ───────── * Team
          │
          ↓
      SeasonTeam
```

A team can participate in multiple seasons.

A season can contain multiple teams.

### Important

`SeasonTeam` represents the **relationship**.

It should not own:

```text
addTeam()
removeTeam()
```

Those operations belong naturally to `Season` (or eventually a service).

---

## Player

### Attributes

* `playerId`
* `playerName`
* `position`

### Behavior

No major behavior identified yet.

---

## PlayerTeam

Association entity between `Player` and `Team`.

### Attributes

* `player`
* `team`
* `startDate`
* `endDate`

### Behavior

None for now.

### Relationship

```text
Player * ───────── * Team
          │
          ↓
      PlayerTeam
```

A player can play for multiple teams during their career.

A team has multiple players.

`startDate` and `endDate` allow us to track the player's history.

### Important

`PlayerTeam` represents the **relationship**.

It should not own:

```text
addPlayer()
removePlayer()
```

Those operations belong naturally to `Team` (or eventually a service).

---

# 3. Match

### Attributes

* `matchId`
* `matchDate`
* `homeTeam`
* `awayTeam`
* `venue`
* `homeScore`
* `awayScore`
* `matchStatus`
* `scheduledStart`
* `actualStart`
* `actualEnd`
* `season`

### Behavior

* `schedule()`
* `start()`
* `updateScore()`
* `updateStatus()`
* `end()`

### Relationship

```text
Season 1 ───────── * Match

Match
 ├── homeTeam → Team
 ├── awayTeam → Team
 └── season → Season
```

We do **not** need `league` directly inside `Match`.

We can derive:

```text
Match
  ↓
Season
  ↓
League
  ↓
Sport
```

---

# 4. MatchStatistics

Statistics associated with a match.

### Attributes

* `match`
* `totalPoints`
* `totalAssists`
* `totalRebounds`
* sport-specific statistics

### Behavior

* `updateStatistics()`

The exact statistics will depend on the sport.

For example, basketball may have:

```text
points
assists
rebounds
steals
blocks
```

Football/soccer may have:

```text
shots
shotsOnTarget
possession
corners
fouls
```

We should not force every sport's statistics into one giant class.

---

# 5. PlayerStatistics

Statistics for a player during a match.

### Attributes

* `player`
* `match`
* `points`
* `assists`
* `rebounds`
* sport-specific statistics

### Behavior

* `updateStatistics()`

Conceptually:

```text
Player
   +
Match
   ↓
PlayerStatistics
```

This allows us to answer:

> How did a particular player perform in a particular match?

---

# 6. TeamStatistics

Statistics for a team during a match.

### Attributes

* `team`
* `match`
* `fouls`
* `substitutions`
* `possession`
* sport-specific statistics

### Behavior

* `updateStatistics()`

---

# 7. LeagueStandings

Candidate entity — **not finalized yet**.

We need to decide whether standings should be:

```text
Persisted as an object/entity
```

or

```text
Calculated dynamically from match results
```

We'll decide this when we discuss behavior and services.

---

# 8. Current Relationship Model

```text
Sport
  │
  │ 1
  ↓
League
  │
  │ 1
  ↓
Season
  │
  │ *
  ↓
SeasonTeam
  ↑
  │ *
  │
Team
```

Player relationship:

```text
Player
  │
  │ 1
  ↓
PlayerTeam
  ↑
  │ *
  │
Team
```

Match:

```text
Season
  │
  │ 1
  ↓
Match
 ├── homeTeam → Team
 ├── awayTeam → Team
 └── season → Season
```

Statistics:

```text
Match
 ├── MatchStatistics
 ├── PlayerStatistics
 └── TeamStatistics
```

---

# 9. Important LLD Mental Model

Up to this point, the process is very similar to database modeling:

```text
Database Modeling          LLD
-----------------          ----------------
Table                  →   Class
Column                 →   Attribute
Foreign Key            →   Object reference
1-to-many              →   Collection/reference
Many-to-many            →   Association entity
```

But LLD eventually goes beyond the data model:

```text
Entities
   ↓
Relationships
   ↓
Behavior
   ↓
Interfaces
   ↓
Polymorphism
   ↓
Design Patterns
   ↓
Services
```

We are currently at:

```text
Entities + Relationships + Initial Behavior
```


# Sports League Management System — Classes & Attributes

```text
Sport
- sportId
- sportName

League
- leagueId
- leagueName
- sport

Season
- seasonId
- seasonTitle
- league

Team
- teamId
- teamTitle
- logo

SeasonTeam
- season
- team

Player
- playerId
- playerName
- position

PlayerTeam
- player
- team
- startDate
- endDate

Match
- matchId
- matchDate
- homeTeam
- awayTeam
- venue
- homeScore
- awayScore
- matchStatus
- scheduledStart
- actualStart
- actualEnd
- season

MatchStatistics
- match
- totalPoints
- totalAssists
- totalRebounds
- sportSpecificStatistics

PlayerStatistics
- player
- match
- points
- assists
- rebounds
- sportSpecificStatistics

TeamStatistics
- team
- match
- fouls
- substitutions
- possession
- sportSpecificStatistics

LeagueStandings
- TBD
```

## Compact View

```text
Sport
  sportId, sportName

League
  leagueId, leagueName, sport

Season
  seasonId, seasonTitle, league

Team
  teamId, teamTitle, logo

SeasonTeam
  season, team

Player
  playerId, playerName, position

PlayerTeam
  player, team, startDate, endDate

Match
  matchId, matchDate, homeTeam, awayTeam, venue,
  homeScore, awayScore, matchStatus,
  scheduledStart, actualStart, actualEnd, season

MatchStatistics
  match, totalPoints, totalAssists, totalRebounds,
  sportSpecificStatistics

PlayerStatistics
  player, match, points, assists, rebounds,
  sportSpecificStatistics

TeamStatistics
  team, match, fouls, substitutions, possession,
  sportSpecificStatistics

LeagueStandings
  TBD
```

## Relationships

```text
Sport 1 ─────── * League 1 ─────── * Season
                                      │
                                      │
                                      * 
                                      ↓
                                  SeasonTeam
                                      ↑
                                      *
                                      │
                                      Team

Player * ─────── * Team
         │
         ↓
     PlayerTeam

Season 1 ─────── * Match
                    │
          ┌─────────┼─────────┐
          ↓         ↓         ↓
      MatchStats PlayerStats TeamStats
```

Absolutely. Here is the **single pasteable Saurabh's format** covering everything we've designed **up through `StatisticsService`**.

# Sports League Management System — LLD

## Classes, Attributes, Behavior & Design So Far

---

# 1. Goal

Design a Sports League Management System that allows users to:

* Browse sports and leagues
* View seasons
* View teams and players
* View matches
* View live match updates
* View match statistics
* View player/team statistics
* View league standings

The system should support multiple sports such as:

```text
Basketball
Football
Soccer
```

---

# 2. Domain Classes

## Sport

```text
Sport
- sportId
- sportName
```

---

## League

```text
League
- leagueId
- leagueName
- sport
```

Relationship:

```text
Sport 1 ─────── * League
```

A sport can have many leagues.

---

## Season

```text
Season
- seasonId
- seasonTitle
- league
```

Relationship:

```text
League 1 ─────── * Season
```

---

## Team

```text
Team
- teamId
- teamTitle
- logo
```

Behavior:

```text
+ addPlayer()
+ removePlayer()
```

---

## SeasonTeam

Association between Season and Team.

```text
SeasonTeam
- season
- team
```

Relationship:

```text
Season * ─────── * Team
          │
          ↓
      SeasonTeam
```

A team can participate in multiple seasons.

A season can contain multiple teams.

### Important

`SeasonTeam` represents the relationship.

It does NOT need to own:

```text
addTeam()
removeTeam()
```

Those operations belong to `Season` or a service such as `SeasonService`.

---

## Player

```text
Player
- playerId
- playerName
- position
```

A player can play for multiple teams over their career.

---

## PlayerTeam

Association between Player and Team.

```text
PlayerTeam
- player
- team
- startDate
- endDate
```

Relationship:

```text
Player * ─────── * Team
          │
          ↓
      PlayerTeam
```

`startDate` and `endDate` allow us to track team history.

### Important

`PlayerTeam` represents the relationship.

It does NOT need to own:

```text
addPlayer()
removePlayer()
```

Those operations belong to `Team` or a service such as `RosterService`.

---

# 3. Match

```text
Match
- matchId
- matchDate
- homeTeam
- awayTeam
- venue
- homeScore
- awayScore
- matchStatus
- scheduledStart
- actualStart
- actualEnd
- season
```

Behavior:

```text
+ schedule()
+ start()
+ updateScore()
+ updateStatus()
+ end()
```

Relationship:

```text
Season 1 ─────── * Match

Match
 ├── homeTeam → Team
 ├── awayTeam → Team
 └── season → Season
```

We don't need `league` directly in Match.

We can derive:

```text
Match
  ↓
Season
  ↓
League
  ↓
Sport
```

---

# 4. Statistics Classes

## MatchStatistics

```text
MatchStatistics
- match
- totalPoints
- totalAssists
- totalRebounds
- sportSpecificStatistics
```

Behavior:

```text
+ updateStatistics()
```

---

## PlayerStatistics

```text
PlayerStatistics
- player
- match
- points
- assists
- rebounds
- sportSpecificStatistics
```

Behavior:

```text
+ updateStatistics()
```

These are statistics for a specific player in a specific match.

---

## TeamStatistics

```text
TeamStatistics
- team
- match
- fouls
- substitutions
- possession
- sportSpecificStatistics
```

Behavior:

```text
+ updateStatistics()
```

These are statistics for a specific team in a specific match.

---

## LeagueStandings

Potential domain concept:

```text
LeagueStandings
- season
- team
- wins
- losses
- draws
- points
- rank
```

This represents accumulated season state.

It may be persisted in the database:

```text
LEAGUE_STANDINGS
----------------
season_id
team_id
wins
losses
draws
points
rank
```

Important:

A database table does NOT automatically require a Java class.

But `LeagueStandings` is useful as a domain concept because the application explicitly needs to serve current standings.

---

# 5. MatchResult

We decided we don't necessarily need a persistent `MatchResult` entity.

The result can be calculated from:

```text
homeScore
awayScore
```

For example:

```text
homeScore > awayScore → HOME_WIN
homeScore < awayScore → AWAY_WIN
homeScore == awayScore → DRAW
```

It can simply be a temporary value:

```text
MatchResult
- winnerTeam
- loserTeam
- resultType
```

or even:

```text
enum MatchResult {
    HOME_WIN,
    AWAY_WIN,
    DRAW
}
```

No separate database table is necessarily required.

---

# 6. GameEvent

Represents something that happened during a match.

```text
GameEvent
- eventType
- timestamp
- team
- player
```

Example:

```text
GameEvent
eventType = THREE_POINT_SHOT
team = Lakers
player = Player123
```

The important idea:

```text
GameEvent
→ describes WHAT happened

SportRuleEngine
→ decides WHAT that event means
```

We don't put `sportName` in `GameEvent`.

The sport can be derived from:

```text
Match
  ↓
Season
  ↓
League
  ↓
Sport
```

---

# 7. SportRuleEngine

Purpose:

Keep `Match` generic and put sport-specific rules behind an interface.

```text
SportRuleEngine
```

No fields initially.

Behavior:

```text
+ applyEvent(match, event)
+ calculateResult(match)
+ calculateStandingsPoints(result)
```

---

# 8. Sport Rule Implementations

```text
SportRuleEngine
        │
        ├── BasketballRuleEngine
        ├── FootballRuleEngine
        └── SoccerRuleEngine
```

Each implementation follows the same interface but has different rules.

### Basketball

```text
BasketballRuleEngine

applyEvent()
    THREE_POINT_SHOT → +3
    FREE_THROW       → +1

calculateResult()
    higher score → winner

calculateStandingsPoints()
    WIN  → +1
    LOSS → +0
```

### Soccer

```text
SoccerRuleEngine

applyEvent()
    GOAL → +1

calculateResult()
    higher score → winner
    equal score  → draw

calculateStandingsPoints()
    WIN  → +3
    DRAW → +1
    LOSS → +0
```

### Football

```text
FootballRuleEngine

applyEvent()
    TOUCHDOWN  → +6
    FIELD_GOAL → +3

calculateResult()
    higher score → winner

calculateStandingsPoints()
    sport-specific rules
```

---

# 9. Strategy Pattern

The `SportRuleEngine` is the Strategy interface.

```text
              SportRuleEngine
                     │
        ┌────────────┼────────────┐
        ↓            ↓            ↓
 Basketball      Football      Soccer
 RuleEngine      RuleEngine    RuleEngine
```

The `Match` doesn't need to know the details of each sport.

Instead:

```text
Match
  ↓
SportRuleEngine
```

### Why?

Without Strategy:

```text
if basketball
    ...
else if football
    ...
else if soccer
    ...
```

With Strategy:

```text
SportRuleEngine
      ↓
appropriate implementation
```

Adding a new sport does not require rewriting `Match`.

---

# 10. SportRuleEngineFactory

The Factory decides which rule engine to provide.

```text
SportRuleEngineFactory

+ getRuleEngine(sport)
```

Example:

```text
Basketball → BasketballRuleEngine
Football   → FootballRuleEngine
Soccer     → SoccerRuleEngine
```

Flow:

```text
Match
  ↓
get Sport
  ↓
SportRuleEngineFactory
  ↓
SportRuleEngine
```

### Strategy + Factory

Strategy answers:

```text
How do we implement different sport rules?
```

Factory answers:

```text
Which rule implementation should we use?
```

---

# 11. Services

Current candidate services:

```text
MatchService
TeamService
PlayerService
RosterService
SeasonService
StandingService
LeagueService
StatisticsService
```

Important:

We don't create a service simply because a class exists.

A service should represent a meaningful application workflow.

---

# 12. MatchService

Primary responsibility:

Coordinate match-related workflows.

```text
MatchService

+ recordEvent(matchId, event)
+ getMatch(matchId)
```

Main workflow:

```text
recordEvent(matchId, event)

1. match = getMatch(matchId)

2. ruleEngine =
       factory.getRuleEngine(match.getSport())

3. ruleEngine.applyEvent(match, event)

4. statisticsService.update(...)

5. matchRepository.save(match)
```

Full flow:

```text
API
 ↓
MatchService
 ↓
getMatch(matchId)
 ↓
Match
 ↓
SportRuleEngineFactory
 ↓
SportRuleEngine
 ↓
applyEvent(match, event)
 ↓
StatisticsService
 ↓
Repository
 ↓
Database
```

---

# 13. StatisticsService

We decided that `MatchService` should not directly update every type of statistic.

Instead:

```text
MatchService
      ↓
StatisticsService
      ↓
 ┌───────────────┬────────────────┬────────────────┐
 ↓               ↓                ↓
MatchStats     TeamStats       PlayerStats
```

Candidate behavior:

```text
StatisticsService

+ updateMatchStatistics(match, event)

+ updateTeamStatistics(team, match, event)

+ updatePlayerStatistics(player, match, event)
```

Example:

```text
GameEvent
team = Lakers
player = LeBron
event = THREE_POINT_SHOT

        ↓

MatchService
        ↓
BasketballRuleEngine
        ↓
Match score updated
        ↓
StatisticsService
        ├── MatchStatistics → update
        ├── TeamStatistics  → update
        └── PlayerStatistics → update
```

Important separation:

```text
SportRuleEngine
→ decides what the event means

StatisticsService
→ maintains statistics
```

---

# 14. Current Overall Design

```text
                         API
                          │
                          ↓
                    MatchService
                          │
              ┌───────────┴───────────┐
              ↓                       ↓
      SportRuleEngineFactory    StatisticsService
              │                       │
              ↓               ┌───────┼────────┐
       SportRuleEngine         ↓       ↓        ↓
              │              Match   Team    Player
       ┌──────┼──────┐       Stats   Stats    Stats
       ↓      ↓      ↓
 Basketball Football Soccer
   Engine     Engine  Engine
              │
              ↓
            Match
              │
              ↓
          Repository
              │
              ↓
           Database
```

---

# 15. Important LLD Principle We've Established

Database model and domain model **do not need to be identical**.

```text
Database
    ≠
Java Domain Model
```

A database table does not automatically require a Java class.

For example:

```text
SEASON_TEAM
PLAYER_TEAM
LEAGUE_STANDINGS
```

may exist in the database, while the Java domain model can choose which concepts deserve classes.

Similarly, a calculated value does not automatically need a database table.

Example:

```text
MatchResult
```

can simply be calculated from the Match.

---

# Current Classes / Interfaces

```text
Sport
League
Season
Team
SeasonTeam
Player
PlayerTeam
Match

MatchStatistics
PlayerStatistics
TeamStatistics
LeagueStandings

GameEvent

SportRuleEngine
BasketballRuleEngine
FootballRuleEngine
SoccerRuleEngine

SportRuleEngineFactory
```

# Current Services

```text
MatchService
StatisticsService

TeamService
PlayerService
RosterService
SeasonService
StandingService
LeagueService

(Still to be refined)
```

# Patterns Used So Far

```text
Strategy Pattern
    → SportRuleEngine

Factory Pattern
    → SportRuleEngineFactory
```

# Next

Next we should refine the remaining **services and their responsibilities**, then move to:

```text
Repositories / DAO
        ↓
REST APIs
        ↓
Database Design
        ↓
Complete Java Implementation
        ↓
SOLID + Interview Follow-ups
```
Absolutely. I'll keep **all the code**, but organize it in the same **Saurabh's format** you've been using: **purpose → class → attributes → behavior → relationships → code → interview takeaway**. This is the version I'd keep as your master LLD note.

# Sports League Management System — Complete LLD

## Saurabh's Format

---

# 0. What Are We Designing?

A Sports League Management System that allows users to:

* View sports
* View leagues
* View seasons
* View teams and players
* View matches
* Record live match events
* View match statistics
* View player/team statistics
* View league standings

The system should support multiple sports:

```text
Basketball
Football
Soccer
```

The key design goal is:

> **Different sports have different rules, but Match should remain generic.**

This is where the **Strategy Pattern** becomes useful.

---

# 1. Domain Model

## Overall Relationship

```text
Sport
  │
  └────── 1 : N ──────> League
                           │
                           └────── 1 : N ──────> Season
                                                    │
                                                    ├──── N : N ──── Team
                                                    │                 │
                                                    │                 │
                                                    │                 └── N : N ── Player
                                                    │
                                                    └──── 1 : N ────── Match
```

---

# 2. Sport

## Purpose

Represents a sport supported by the system.

## Class

```text
Sport
```

## Attributes

```text
sportId
sportName
```

## Behavior

No significant behavior for now.

## Java

```java
class Sport {

    private String sportId;
    private String sportName;

    public Sport(String sportId, String sportName) {
        this.sportId = sportId;
        this.sportName = sportName;
    }
}
```

---

# 3. League

## Purpose

Represents a league belonging to a sport.

Examples:

```text
NBA
Premier League
NFL
```

## Attributes

```text
leagueId
leagueName
sport
```

## Relationship

```text
Sport 1 ───────── N League
```

## Java

```java
class League {

    private String leagueId;
    private String leagueName;
    private Sport sport;

    public League(
            String leagueId,
            String leagueName,
            Sport sport) {

        this.leagueId = leagueId;
        this.leagueName = leagueName;
        this.sport = sport;
    }
}
```

---

# 4. Season

## Purpose

Represents a particular season of a league.

Example:

```text
NBA
 ├── 2025 Season
 ├── 2026 Season
 └── 2027 Season
```

## Attributes

```text
seasonId
seasonTitle
league
```

## Relationship

```text
League 1 ───────── N Season
```

## Java

```java
class Season {

    private String seasonId;
    private String seasonTitle;
    private League league;

    public Season(
            String seasonId,
            String seasonTitle,
            League league) {

        this.seasonId = seasonId;
        this.seasonTitle = seasonTitle;
        this.league = league;
    }
}
```

---

# 5. Team

## Purpose

Represents a sports team.

## Attributes

```text
teamId
teamTitle
logo
```

## Behavior

Conceptually:

```text
addPlayer()
removePlayer()
```

But we do not necessarily put these relationship operations directly inside the entity.

They can be handled through `RosterService`.

## Java

```java
class Team {

    private String teamId;
    private String teamTitle;
    private String logo;

    public Team(
            String teamId,
            String teamTitle,
            String logo) {

        this.teamId = teamId;
        this.teamTitle = teamTitle;
        this.logo = logo;
    }
}
```

---

# 6. SeasonTeam

## Purpose

Represents the many-to-many relationship between Season and Team.

```text
Season * ───────── * Team
          │
          ↓
     SeasonTeam
```

A team can participate in multiple seasons.

A season contains multiple teams.

## Attributes

```text
season
team
```

## Java

```java
class SeasonTeam {

    private Season season;
    private Team team;

    public SeasonTeam(
            Season season,
            Team team) {

        this.season = season;
        this.team = team;
    }
}
```

### Important

`SeasonTeam` represents the **association**.

We don't need:

```text
addTeam()
removeTeam()
```

inside the association object.

That behavior belongs in a service such as:

```text
SeasonService
```

---

# 7. Player

## Purpose

Represents a player.

A player can play for different teams over time.

## Attributes

```text
playerId
playerName
position
```

## Java

```java
class Player {

    private String playerId;
    private String playerName;
    private String position;

    public Player(
            String playerId,
            String playerName,
            String position) {

        this.playerId = playerId;
        this.playerName = playerName;
        this.position = position;
    }
}
```

---

# 8. PlayerTeam

## Purpose

Represents the many-to-many relationship between Player and Team.

```text
Player * ───────── * Team
          │
          ↓
      PlayerTeam
```

We use this because a player can belong to multiple teams over their career.

## Attributes

```text
player
team
startDate
endDate
```

The dates allow us to model team history.

## Java

```java
class PlayerTeam {

    private Player player;
    private Team team;

    private LocalDate startDate;
    private LocalDate endDate;

    public PlayerTeam(
            Player player,
            Team team,
            LocalDate startDate,
            LocalDate endDate) {

        this.player = player;
        this.team = team;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
```

---

# 9. Match

## Purpose

Represents a game between two teams.

## Attributes

```text
matchId

homeTeam
awayTeam

season
venue

homeScore
awayScore

status

scheduledStart
actualStart
actualEnd
```

We don't need `league` directly.

We can derive:

```text
Match
  ↓
Season
  ↓
League
  ↓
Sport
```

## Behavior

```text
start()
updateScore()
end()
```

## Java

```java
class Match {

    private String matchId;

    private Team homeTeam;
    private Team awayTeam;

    private Season season;

    private String venue;

    private int homeScore;
    private int awayScore;

    private MatchStatus status;

    private LocalDateTime scheduledStart;
    private LocalDateTime actualStart;
    private LocalDateTime actualEnd;


    public Match(
            String matchId,
            Team homeTeam,
            Team awayTeam,
            Season season,
            String venue,
            LocalDateTime scheduledStart) {

        this.matchId = matchId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.season = season;
        this.venue = venue;
        this.scheduledStart = scheduledStart;

        this.homeScore = 0;
        this.awayScore = 0;

        this.status = MatchStatus.SCHEDULED;
    }


    public void start() {

        if (status != MatchStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Match cannot be started");
        }

        status = MatchStatus.LIVE;
        actualStart = LocalDateTime.now();
    }


    public void updateScore(
            Team team,
            int points) {

        if (status != MatchStatus.LIVE) {
            throw new IllegalStateException(
                    "Match is not live");
        }

        if (team.equals(homeTeam)) {

            homeScore += points;

        } else if (team.equals(awayTeam)) {

            awayScore += points;

        } else {

            throw new IllegalArgumentException(
                    "Team does not belong to this match");
        }
    }


    public void end() {

        if (status != MatchStatus.LIVE) {
            throw new IllegalStateException(
                    "Match is not live");
        }

        status = MatchStatus.COMPLETED;
        actualEnd = LocalDateTime.now();
    }


    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public Season getSeason() {
        return season;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public MatchStatus getStatus() {
        return status;
    }
}
```

---

# 10. Match Status

```java
enum MatchStatus {

    SCHEDULED,
    LIVE,
    COMPLETED,
    CANCELLED
}
```

---

# 11. GameEvent

## Purpose

Represents something that happens during a match.

Examples:

```text
GOAL
THREE_POINT_SHOT
FREE_THROW
TOUCHDOWN
FIELD_GOAL
```

## Attributes

```text
eventType
timestamp
team
player
```

## Java

```java
class GameEvent {

    private GameEventType eventType;
    private LocalDateTime timestamp;

    private Team team;
    private Player player;


    public GameEvent(
            GameEventType eventType,
            LocalDateTime timestamp,
            Team team,
            Player player) {

        this.eventType = eventType;
        this.timestamp = timestamp;
        this.team = team;
        this.player = player;
    }


    public GameEventType getEventType() {
        return eventType;
    }

    public Team getTeam() {
        return team;
    }

    public Player getPlayer() {
        return player;
    }
}
```

```java
enum GameEventType {

    GOAL,
    THREE_POINT_SHOT,
    FREE_THROW,
    TOUCHDOWN,
    FIELD_GOAL
}
```

---

# 12. Statistics

We have three levels of statistics.

```text
MatchStatistics
PlayerStatistics
TeamStatistics
```

---

## MatchStatistics

```text
MatchStatistics
- match
- totalPoints
- totalAssists
- totalRebounds
```

```java
class MatchStatistics {

    private Match match;

    private int totalPoints;
    private int totalAssists;
    private int totalRebounds;

    public void updateStatistics(
            GameEvent event) {

        // Update match-level statistics
    }
}
```

---

## PlayerStatistics

```text
PlayerStatistics
- player
- match
- points
- assists
- rebounds
```

```java
class PlayerStatistics {

    private Player player;
    private Match match;

    private int points;
    private int assists;
    private int rebounds;

    public void updateStatistics(
            GameEvent event) {

        // Update player-level statistics
    }
}
```

---

## TeamStatistics

```text
TeamStatistics
- team
- match
- fouls
- substitutions
- possession
```

```java
class TeamStatistics {

    private Team team;
    private Match match;

    private int fouls;
    private int substitutions;
    private double possession;

    public void updateStatistics(
            GameEvent event) {

        // Update team-level statistics
    }
}
```

---

# 13. LeagueStandings

## Purpose

Represents accumulated standings for a team in a particular season.

## Attributes

```text
season
team

wins
losses
draws
points
rank
```

We do NOT need `league`.

Why?

```text
LeagueStandings
      ↓
    Season
      ↓
    League
```

## Java

```java
class LeagueStandings {

    private Season season;
    private Team team;

    private int wins;
    private int losses;
    private int draws;

    private int points;
    private int rank;


    public void recordWin(
            int pointsEarned) {

        wins++;
        points += pointsEarned;
    }


    public void recordLoss(
            int pointsEarned) {

        losses++;
        points += pointsEarned;
    }


    public void recordDraw(
            int pointsEarned) {

        draws++;
        points += pointsEarned;
    }


    public int getPoints() {
        return points;
    }
}
```

---

# 14. MatchResult

## Purpose

Represents the result calculated after a match.

It does not necessarily need to be persisted.

```text
HOME_WIN
AWAY_WIN
DRAW
```

## Java

```java
enum MatchResultType {

    HOME_WIN,
    AWAY_WIN,
    DRAW
}
```

```java
class MatchResult {

    private MatchResultType resultType;

    private Team winner;
    private Team loser;


    public MatchResult(
            MatchResultType resultType,
            Team winner,
            Team loser) {

        this.resultType = resultType;
        this.winner = winner;
        this.loser = loser;
    }


    public MatchResultType getResultType() {
        return resultType;
    }

    public Team getWinner() {
        return winner;
    }

    public Team getLoser() {
        return loser;
    }
}
```

---

# 15. Strategy Pattern — SportRuleEngine

## Problem

Different sports have different rules.

For example:

```text
Basketball
3-point shot → +3

Soccer
Goal → +1

Football
Touchdown → +6
```

We don't want:

```text
if basketball
else if soccer
else if football
```

inside `Match`.

## Solution

Create an interface:

```java
interface SportRuleEngine {

    void applyEvent(
            Match match,
            GameEvent event);

    MatchResult calculateResult(
            Match match);

    int calculateStandingsPoints(
            MatchResult result);
}
```

---

# 16. BasketballRuleEngine

```java
class BasketballRuleEngine
        implements SportRuleEngine {


    @Override
    public void applyEvent(
            Match match,
            GameEvent event) {

        int points;

        switch (event.getEventType()) {

            case THREE_POINT_SHOT:
                points = 3;
                break;

            case FREE_THROW:
                points = 1;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported basketball event");
        }

        match.updateScore(
                event.getTeam(),
                points);
    }


    @Override
    public MatchResult calculateResult(
            Match match) {

        if (match.getHomeScore()
                > match.getAwayScore()) {

            return new MatchResult(
                    MatchResultType.HOME_WIN,
                    match.getHomeTeam(),
                    match.getAwayTeam());
        }

        if (match.getAwayScore()
                > match.getHomeScore()) {

            return new MatchResult(
                    MatchResultType.AWAY_WIN,
                    match.getAwayTeam(),
                    match.getHomeTeam());
        }

        return new MatchResult(
                MatchResultType.DRAW,
                null,
                null);
    }


    @Override
    public int calculateStandingsPoints(
            MatchResult result) {

        if (result.getResultType()
                == MatchResultType.HOME_WIN
                ||
            result.getResultType()
                == MatchResultType.AWAY_WIN) {

            return 1;
        }

        return 0;
    }
}
```

---

# 17. SoccerRuleEngine

```java
class SoccerRuleEngine
        implements SportRuleEngine {


    @Override
    public void applyEvent(
            Match match,
            GameEvent event) {

        if (event.getEventType()
                != GameEventType.GOAL) {

            throw new IllegalArgumentException(
                    "Unsupported soccer event");
        }

        match.updateScore(
                event.getTeam(),
                1);
    }


    @Override
    public MatchResult calculateResult(
            Match match) {

        if (match.getHomeScore()
                > match.getAwayScore()) {

            return new MatchResult(
                    MatchResultType.HOME_WIN,
                    match.getHomeTeam(),
                    match.getAwayTeam());
        }

        if (match.getAwayScore()
                > match.getHomeScore()) {

            return new MatchResult(
                    MatchResultType.AWAY_WIN,
                    match.getAwayTeam(),
                    match.getHomeTeam());
        }

        return new MatchResult(
                MatchResultType.DRAW,
                null,
                null);
    }


    @Override
    public int calculateStandingsPoints(
            MatchResult result) {

        switch (result.getResultType()) {

            case HOME_WIN:
            case AWAY_WIN:
                return 3;

            case DRAW:
                return 1;

            default:
                return 0;
        }
    }
}
```

---

# 18. FootballRuleEngine

```java
class FootballRuleEngine
        implements SportRuleEngine {


    @Override
    public void applyEvent(
            Match match,
            GameEvent event) {

        int points;

        switch (event.getEventType()) {

            case TOUCHDOWN:
                points = 6;
                break;

            case FIELD_GOAL:
                points = 3;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported football event");
        }

        match.updateScore(
                event.getTeam(),
                points);
    }


    @Override
    public MatchResult calculateResult(
            Match match) {

        if (match.getHomeScore()
                > match.getAwayScore()) {

            return new MatchResult(
                    MatchResultType.HOME_WIN,
                    match.getHomeTeam(),
                    match.getAwayTeam());
        }

        if (match.getAwayScore()
                > match.getHomeScore()) {

            return new MatchResult(
                    MatchResultType.AWAY_WIN,
                    match.getAwayTeam(),
                    match.getHomeTeam());
        }

        return new MatchResult(
                MatchResultType.DRAW,
                null,
                null);
    }


    @Override
    public int calculateStandingsPoints(
            MatchResult result) {

        return result.getResultType()
                == MatchResultType.HOME_WIN
                ||
               result.getResultType()
                == MatchResultType.AWAY_WIN
                ? 1
                : 0;
    }
}
```

---

# 19. Factory Pattern

## Problem

Who decides which `SportRuleEngine` to use?

We don't want:

```text
MatchService

if basketball
    new BasketballRuleEngine()

if soccer
    new SoccerRuleEngine()
```

Instead, use a Factory.

## Java

```java
class SportRuleEngineFactory {

    public SportRuleEngine getRuleEngine(
            SportType sport) {

        switch (sport) {

            case BASKETBALL:
                return new BasketballRuleEngine();

            case SOCCER:
                return new SoccerRuleEngine();

            case FOOTBALL:
                return new FootballRuleEngine();

            default:
                throw new IllegalArgumentException(
                        "Unsupported sport");
        }
    }
}
```

```java
enum SportType {

    BASKETBALL,
    FOOTBALL,
    SOCCER
}
```

---

# 20. Strategy + Factory

## Strategy answers

> How do we support different sport rules?

```text
SportRuleEngine
       │
 ┌─────┼────────┐
 ↓     ↓        ↓
Basket Soccer Football
```

## Factory answers

> Which implementation should I use?

```text
SportRuleEngineFactory
        │
        ├── BasketballRuleEngine
        ├── SoccerRuleEngine
        └── FootballRuleEngine
```

---

# 21. Repositories

## Purpose

Repositories handle persistence.

Services handle business logic.

```text
Service
   ↓
Repository
   ↓
Database
```

---

# 22. MatchRepository

```java
interface MatchRepository {

    Match findById(
            String matchId);

    void save(
            Match match);
}
```

---

# 23. StandingRepository

```java
interface StandingRepository {

    List<LeagueStandings> findBySeason(
            String seasonId);

    LeagueStandings findBySeasonAndTeam(
            String seasonId,
            String teamId);

    void save(
            LeagueStandings standings);

    void update(
            LeagueStandings standings);
}
```

---

# 24. StatisticsRepository

```java
interface StatisticsRepository {

    MatchStatistics getMatchStatistics(
            String matchId);

    PlayerStatistics getPlayerStatistics(
            String playerId,
            String matchId);

    TeamStatistics getTeamStatistics(
            String teamId,
            String matchId);


    void saveMatchStatistics(
            MatchStatistics statistics);

    void savePlayerStatistics(
            PlayerStatistics statistics);

    void saveTeamStatistics(
            TeamStatistics statistics);
}
```

---

# 25. StatisticsService

## Responsibility

Maintains:

```text
MatchStatistics
TeamStatistics
PlayerStatistics
```

## Behavior

```text
updateStatistics()
updateMatchStatistics()
updateTeamStatistics()
updatePlayerStatistics()
```

## Java

```java
class StatisticsService {

    private StatisticsRepository statisticsRepository;


    public StatisticsService(
            StatisticsRepository statisticsRepository) {

        this.statisticsRepository =
                statisticsRepository;
    }


    public void updateStatistics(
            Match match,
            GameEvent event) {

        updateMatchStatistics(
                match,
                event);

        updateTeamStatistics(
                event.getTeam(),
                match,
                event);

        updatePlayerStatistics(
                event.getPlayer(),
                match,
                event);
    }


    private void updateMatchStatistics(
            Match match,
            GameEvent event) {

        MatchStatistics statistics =
                statisticsRepository
                        .getMatchStatistics(
                                "matchId");

        statistics.updateStatistics(event);

        statisticsRepository
                .saveMatchStatistics(statistics);
    }


    private void updateTeamStatistics(
            Team team,
            Match match,
            GameEvent event) {

        TeamStatistics statistics =
                statisticsRepository
                        .getTeamStatistics(
                                "teamId",
                                "matchId");

        statistics.updateStatistics(event);

        statisticsRepository
                .saveTeamStatistics(statistics);
    }


    private void updatePlayerStatistics(
            Player player,
            Match match,
            GameEvent event) {

        if (player == null) {
            return;
        }

        PlayerStatistics statistics =
                statisticsRepository
                        .getPlayerStatistics(
                                "playerId",
                                "matchId");

        statistics.updateStatistics(event);

        statisticsRepository
                .savePlayerStatistics(statistics);
    }
}
```

---

# 26. StandingService

## Responsibility

Maintains accumulated season standings.

```text
StandingService
        ↓
LeagueStandings
```

## Behavior

```text
updateStandings()
getStandings()
```

## Java

```java
class StandingService {

    private StandingRepository standingRepository;


    public StandingService(
            StandingRepository standingRepository) {

        this.standingRepository =
                standingRepository;
    }


    public void updateStandings(
            Season season,
            MatchResult result,
            int points) {

        if (result.getWinner() != null) {

            LeagueStandings winnerStanding =
                    standingRepository
                            .findBySeasonAndTeam(
                                    "seasonId",
                                    "winnerTeamId");

            winnerStanding.recordWin(points);

            standingRepository
                    .update(winnerStanding);
        }


        if (result.getLoser() != null) {

            LeagueStandings loserStanding =
                    standingRepository
                            .findBySeasonAndTeam(
                                    "seasonId",
                                    "loserTeamId");

            loserStanding.recordLoss(0);

            standingRepository
                    .update(loserStanding);
        }
    }


    public List<LeagueStandings> getStandings(
            String seasonId) {

        return standingRepository
                .findBySeason(seasonId);
    }
}
```

---

# 27. MatchService

## Responsibility

`MatchService` is the **orchestrator**.

It does not implement basketball/soccer/football rules.

It coordinates:

```text
Match
RuleEngine
StatisticsService
StandingService
Repositories
```

---

# 28. Record Event Flow

```text
recordEvent(matchId, event)

        ↓

1. Load Match

        ↓

2. Get SportRuleEngine

        ↓

3. Apply Event

        ↓

4. Update Statistics

        ↓

5. Persist Match
```

## Java

```java
class MatchService {

    private MatchRepository matchRepository;

    private SportRuleEngineFactory ruleEngineFactory;

    private StatisticsService statisticsService;

    private StandingService standingService;


    public MatchService(
            MatchRepository matchRepository,
            SportRuleEngineFactory ruleEngineFactory,
            StatisticsService statisticsService,
            StandingService standingService) {

        this.matchRepository =
                matchRepository;

        this.ruleEngineFactory =
                ruleEngineFactory;

        this.statisticsService =
                statisticsService;

        this.standingService =
                standingService;
    }


    public Match getMatch(
            String matchId) {

        return matchRepository
                .findById(matchId);
    }


    public void recordEvent(
            String matchId,
            GameEvent event) {

        // 1. Get Match
        Match match =
                matchRepository
                        .findById(matchId);


        // 2. Get appropriate rule engine
        SportType sport =
                getSportType(match);

        SportRuleEngine ruleEngine =
                ruleEngineFactory
                        .getRuleEngine(sport);


        // 3. Apply sport-specific behavior
        ruleEngine.applyEvent(
                match,
                event);


        // 4. Update statistics
        statisticsService
                .updateStatistics(
                        match,
                        event);


        // 5. Persist match
        matchRepository
                .save(match);
    }


    public void startMatch(
            String matchId) {

        Match match =
                matchRepository
                        .findById(matchId);

        match.start();

        matchRepository.save(match);
    }


    public void endMatch(
            String matchId) {

        Match match =
                matchRepository
                        .findById(matchId);


        SportRuleEngine ruleEngine =
                ruleEngineFactory
                        .getRuleEngine(
                                getSportType(match));


        // Calculate result
        MatchResult result =
                ruleEngine
                        .calculateResult(match);


        // Calculate league points
        int points =
                ruleEngine
                        .calculateStandingsPoints(
                                result);


        // Complete match
        match.end();

        matchRepository
                .save(match);


        // Update standings
        standingService
                .updateStandings(
                        match.getSeason(),
                        result,
                        points);
    }


    private SportType getSportType(
            Match match) {

        /*
         * match
         *   ↓
         * season
         *   ↓
         * league
         *   ↓
         * sport
         */

        return SportType.SOCCER;
    }
}
```

---

# 29. Complete Match Event Flow

This is the most important flow to understand.

```text
Client
   │
   │ POST /matches/{matchId}/events
   ↓
MatchController
   │
   ↓
MatchService
   │
   ├── MatchRepository.findById()
   │
   ├── SportRuleEngineFactory
   │          ↓
   │     SportRuleEngine
   │
   ├── ruleEngine.applyEvent()
   │          ↓
   │       Match
   │
   ├── StatisticsService
   │          ↓
   │       Statistics
   │
   └── MatchRepository.save()
              ↓
           Database
```

---

# 30. Match Completion Flow

```text
Client
   │
   │ POST /matches/{matchId}/end
   ↓
MatchController
   ↓
MatchService
   ↓
SportRuleEngine
   │
   ├── calculateResult()
   │
   └── calculateStandingsPoints()
             │
             ↓
      StandingService
             ↓
       LeagueStandings
             ↓
       StandingRepository
             ↓
          Database
```

---

# 31. Controllers / REST APIs

Controllers should be **thin**.

They should not contain business logic.

Correct:

```text
Controller
    ↓
Service
    ↓
Repository
```

Incorrect:

```text
Controller
    ↓
Business Logic
    ↓
SQL
```

---

# 32. MatchController

## APIs

```text
GET  /matches/{matchId}

POST /matches/{matchId}/events

POST /matches/{matchId}/start

POST /matches/{matchId}/end
```

## Java

```java
class MatchController {

    private MatchService matchService;


    public MatchController(
            MatchService matchService) {

        this.matchService =
                matchService;
    }


    public Match getMatch(
            String matchId) {

        return matchService
                .getMatch(matchId);
    }


    public void recordEvent(
            String matchId,
            GameEvent event) {

        matchService
                .recordEvent(
                        matchId,
                        event);
    }


    public void startMatch(
            String matchId) {

        matchService
                .startMatch(matchId);
    }


    public void endMatch(
            String matchId) {

        matchService
                .endMatch(matchId);
    }
}
```

---

# 33. StandingController

## API

```text
GET /seasons/{seasonId}/standings
```

## Java

```java
class StandingController {

    private StandingService standingService;


    public StandingController(
            StandingService standingService) {

        this.standingService =
                standingService;
    }


    public List<LeagueStandings> getStandings(
            String seasonId) {

        return standingService
                .getStandings(seasonId);
    }
}
```

---

# 34. Statistics APIs

```text
GET /matches/{matchId}/statistics

GET /matches/{matchId}/teams/{teamId}/statistics

GET /matches/{matchId}/players/{playerId}/statistics
```

---

# 35. Complete Service Layer

```text
                    Services
                       │
       ┌───────────────┼────────────────┐
       ↓               ↓                ↓
 MatchService   StatisticsService  StandingService
       │               │                │
       ↓               ↓                ↓
 RuleEngine      StatisticsRepo    StandingRepo
```

We intentionally keep this small.

We don't create a service just because we have a class.

---

# 36. Complete Architecture

```text
                         REST API
                            │
                            ↓
                      Controllers
                            │
                            ↓
                         Services
                            │
             ┌──────────────┼──────────────┐
             ↓              ↓              ↓
       MatchService   StatisticsService  StandingService
             │
             ↓
   SportRuleEngineFactory
             │
             ↓
      SportRuleEngine
             │
      ┌──────┼─────────┐
      ↓      ↓         ↓
 Basketball Soccer   Football
  Engine    Engine    Engine
      │
      ↓
    Match
      │
      ↓
 Repositories
      │
      ↓
   Database
```

---

# 37. All Classes So Far

## Domain

```text
Sport
League
Season
Team
SeasonTeam
Player
PlayerTeam
Match
GameEvent
MatchResult

MatchStatistics
PlayerStatistics
TeamStatistics
LeagueStandings
```

## Strategy

```text
SportRuleEngine
BasketballRuleEngine
SoccerRuleEngine
FootballRuleEngine
```

## Factory

```text
SportRuleEngineFactory
```

## Services

```text
MatchService
StatisticsService
StandingService
```

## Repositories

```text
MatchRepository
StatisticsRepository
StandingRepository
```

## Controllers

```text
MatchController
StandingController
```

---

# 38. Patterns Used

## Strategy Pattern

```text
SportRuleEngine
      │
 ┌────┼─────┐
 ↓    ↓     ↓
NBA Soccer Football
```

Used because sport-specific behavior varies.

---

## Factory Pattern

```text
SportRuleEngineFactory
        ↓
correct RuleEngine
```

Used to hide object creation/selection from `MatchService`.

---

# 39. SOLID

## S — Single Responsibility

```text
Match
→ owns match state

SportRuleEngine
→ owns sport-specific rules

MatchService
→ orchestrates match workflow

StatisticsService
→ manages statistics

StandingService
→ manages standings

Repository
→ persistence
```

Each has a clear responsibility.

---

## O — Open/Closed

To add:

```text
Tennis
```

we create:

```text
TennisRuleEngine
```

without changing the existing rule-engine implementations.

---

## L — Liskov Substitution

Any:

```text
BasketballRuleEngine
SoccerRuleEngine
FootballRuleEngine
```

can be used wherever:

```text
SportRuleEngine
```

is expected.

---

## I — Interface Segregation

Repositories and interfaces expose only the operations needed by their consumers.

---

## D — Dependency Inversion

Services depend on:

```text
MatchRepository
StandingRepository
StatisticsRepository
```

interfaces rather than concrete database implementations.

```text
MatchService
      ↓
MatchRepository
   interface
      ↑
PostgresMatchRepository
```

---

# 40. Important Interview Explanations

## Why not put sport rules inside Match?

Because Match would become:

```text
if basketball...
if soccer...
if football...
```

and violate SRP / become difficult to extend.

Instead:

```text
Match
  ↓
SportRuleEngine
```

---

## Why Factory?

The service shouldn't have to know:

```text
new SoccerRuleEngine()
new BasketballRuleEngine()
```

The Factory handles selection.

---

## Why is MatchService needed?

The entity represents the match state.

The service coordinates the workflow:

```text
load
→ apply rule
→ update statistics
→ persist
```

---

## Why StatisticsService?

Because `MatchService` shouldn't know how every statistic is calculated or persisted.

```text
MatchService
    ↓
StatisticsService
    ↓
MatchStats
TeamStats
PlayerStats
```

---

## Why StandingService?

Standings are accumulated season state.

After a match:

```text
MatchResult
    ↓
StandingService
    ↓
LeagueStandings
```

---

## Why Season in LeagueStandings instead of League?

Because:

```text
League
 ├── 2025 Season
 └── 2026 Season
```

Standings are meaningful for a **specific season**.

Therefore:

```text
LeagueStandings
- season
- team
```

and:

```text
season → league
```

allows us to derive the league.

---

# 41. DB vs Object Model

Important LLD distinction:

```text
Database Model
        ≠
Java Domain Model
```

For example:

```text
PLAYER_TEAM
SEASON_TEAM
```

are association tables in the DB.

In Java, we can represent them as:

```text
PlayerTeam
SeasonTeam
```

when they have meaningful domain information.

But a database table does not automatically require a Java class.

Likewise:

```text
MatchResult
```

can be a temporary calculated object without requiring a database table.

---

# 42. Final Interview Mental Model

When the interviewer gives you:

> "Design a sports league management system."

Think:

```text
                 REQUIREMENTS
                      ↓
                  Nouns
                      ↓
             Entities + Relationships
                      ↓
                   Behavior
                      ↓
             What varies by sport?
                      ↓
                Strategy Pattern
                      ↓
             How select the strategy?
                      ↓
                 Factory
                      ↓
                  Services
                      ↓
                Repositories
                      ↓
                  REST APIs
                      ↓
                 Database
```

The most important flow to remember:

```text
GameEvent
    ↓
MatchService
    ↓
SportRuleEngineFactory
    ↓
SportRuleEngine
    ↓
Match updated
    ↓
StatisticsService
    ↓
Repository
```

When match ends:

```text
MatchService
    ↓
RuleEngine.calculateResult()
    ↓
RuleEngine.calculateStandingsPoints()
    ↓
StandingService
    ↓
LeagueStandings
    ↓
Repository
```

---

# 43. The Interview Answer in One Minute

> "I modeled Sport, League, Season, Team, Player and Match as the core domain entities. Since a player can move between teams and teams participate across seasons, I modeled those many-to-many relationships through PlayerTeam and SeasonTeam.
>
> Match behavior is generic, while sport-specific rules vary, so I use a Strategy Pattern through SportRuleEngine, with Basketball, Soccer and Football implementations. A Factory selects the appropriate rule engine based on the sport.
>
> MatchService orchestrates match workflows. StatisticsService manages match, team and player statistics, while StandingService manages accumulated season standings. Persistence is isolated behind repository interfaces.
>
> The REST controllers remain thin and delegate to services. This gives us clear separation of concerns and makes adding another sport straightforward without changing Match or the existing rule implementations."

---

# 44. What We Have Completed

```text
Requirements                    ✅
Entities                        ✅
Attributes                      ✅
Relationships                   ✅
Entity Behavior                 ✅
GameEvent                       ✅
Strategy Pattern                ✅
Factory Pattern                 ✅
Services                        ✅
Repositories / DAO              ✅
REST APIs                       ✅
Architecture                    ✅
SOLID                           ✅
Interview Explanation           ✅
```

## Remaining

```text
Database Schema
↓
Spring Boot implementation
↓
Walk through complete use cases
↓
Interview follow-up questions
```

The next useful step is to **implement this as a realistic Spring Boot LLD**, rather than adding more design abstractions.

# Match Event → Statistics → Standings

## 1. API Request

```text
POST /matches/{matchId}/events
```

```text
GameEvent
- eventType
- team
- player
- timestamp
```

---

## 2. Event Processing

```text
MatchController
      ↓
MatchService
```

### MatchService

```text
1. match = matchRepository.findById(matchId)

2. ruleEngine =
   ruleEngineFactory.getRuleEngine(match's sport)

3. ruleEngine.applyEvent(match, event)
```

Example:

```text
GOAL
 ↓
SoccerRuleEngine
 ↓
match.updateScore(team, 1)
```

**RuleEngine handles sport-specific scoring rules.**

---

## 3. Update Statistics

After applying the event:

```text
StatisticsService.updateStatistics(match, event)
```

Updates:

```text
PlayerStatistics
    → player's relevant stat

TeamStatistics
    → team's relevant stat

MatchStatistics
    → match aggregate statistics
```

Then repositories persist the changes.

---

## 4. Match Ends

```text
MatchService.endMatch()
        ↓
ruleEngine.calculateResult(match)
```

Example:

```text
Team A = 3
Team B = 1

→ Team A wins
```

Then:

```text
ruleEngine.calculateStandingsPoints(result)
```

For soccer:

```text
Win  → 3
Draw → 1
Loss → 0
```

---

## 5. Update Standings

```text
StandingService
      ↓
LeagueStandings
      ↓
Repository
      ↓
Database
```

Updates:

```text
Team A
- wins++
- points += 3

Team B
- losses++
```

---

## 6. Complete Flow

```text
EVENT
  ↓
MatchController
  ↓
MatchService
  ↓
RuleEngine
  ↓
Match Score
  ↓
StatisticsService
  ↓
Player / Team / Match Statistics


MATCH ENDS
  ↓
RuleEngine
  ↓
Result + Standings Points
  ↓
StandingService
  ↓
LeagueStandings
```

## Key Responsibilities

```text
MatchService
→ orchestrates

RuleEngine
→ sport-specific rules

Match
→ owns score/state

StatisticsService
→ updates statistics

StandingService
→ updates standings

Repository
→ persistence
```


# Player Comparison — API + Coding

## 1. Problem

Build an API to compare 2 soccer players based on a statistic.

Example:

GET /v1/players/{playerId1}/compare/{playerId2}?metric=GOALS

Player 1 → 20 Goals
Player 2 → 15 Goals

Result → Player 1 wins

---

# 2. Classes

## Player

### Attributes
- playerId
- playerName

### Behavior
- getPlayerId()
- getPlayerName()


## PlayerStatistic

### Attributes
- playerId
- Map<StatisticType, Integer> statistics

Example:

Player 101:
    GOALS   → 20
    ASSISTS → 10

### Behavior
- getValue(StatisticType)


## StatisticType

enum StatisticType {
    GOALS,
    ASSISTS,
    CORNERS,
    YELLOW_CARD,
    RED_CARD
}


## ComparisonResult

### Attributes
- player1
- player2
- statisticType
- player1Value
- player2Value
- winner

### Purpose
Holds the result of comparing two players.


## PlayerStatisticsRepository

### Attributes
- Map<PlayerId, PlayerStatistic>

### Behavior
- getStatistics(playerId)
- save(PlayerStatistic)

### Responsibility
Gets/stores player statistics.

Repository hides whether data comes from:
- HashMap
- PostgreSQL
- Cassandra
- etc.


## PlayerComparisonService

### Behavior

comparePlayer(player1, player2, statisticType)

### Responsibility

- Get statistics for both players
- Get requested metric
- Compare values
- Create ComparisonResult

---

# 3. Relationships

Player
    1
    |
    | has statistics
    |
    *
PlayerStatistic


PlayerComparisonService
        |
        ↓
PlayerStatisticsRepository
        |
        ↓
PlayerStatistic


PlayerComparisonService
        |
        ↓
ComparisonResult

---

# 4. Basic Flow

GET /v1/players/101/compare/202?metric=GOALS

        ↓

Controller

        ↓

PlayerComparisonService

        ↓

Repository
    ↙       ↘
Player 101  Player 202

GOALS=20    GOALS=15

        ↓

Compare

        ↓

ComparisonResult

        ↓

API Response


---

# 5. Basic Code

enum StatisticType {
    GOALS,
    ASSISTS,
    CORNERS,
    YELLOW_CARD,
    RED_CARD
}


class Player {

    private final int playerId;
    private final String playerName;

    public Player(int playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }
}


class PlayerStatistic {

    private final int playerId;
    private final Map<StatisticType, Integer> statistics;

    public PlayerStatistic(
        int playerId,
        Map<StatisticType, Integer> statistics
    ) {
        this.playerId = playerId;
        this.statistics = statistics;
    }

    public int getValue(StatisticType type) {

        Integer value = statistics.get(type);

        if (value == null) {
            throw new StatisticNotFoundException(
                "Statistic not found: " + type
            );
        }

        return value;
    }

    public int getPlayerId() {
        return playerId;
    }
}


class PlayerStatisticsRepository {

    private final Map<Integer, PlayerStatistic> data =
        new HashMap<>();

    public PlayerStatistic getStatistics(int playerId) {
        return data.get(playerId);
    }

    public void save(PlayerStatistic statistic) {
        data.put(statistic.getPlayerId(), statistic);
    }
}


class PlayerComparisonService {

    private final PlayerStatisticsRepository repository;

    public PlayerComparisonService(
        PlayerStatisticsRepository repository
    ) {
        this.repository = repository;
    }

    public ComparisonResult comparePlayer(
        Player player1,
        Player player2,
        StatisticType type
    ) {

        PlayerStatistic stats1 =
            repository.getStatistics(player1.getPlayerId());

        PlayerStatistic stats2 =
            repository.getStatistics(player2.getPlayerId());

        int value1 = stats1.getValue(type);
        int value2 = stats2.getValue(type);

        Player winner = null;

        if (value1 > value2) {
            winner = player1;
        } else if (value2 > value1) {
            winner = player2;
        }

        return new ComparisonResult(
            player1,
            player2,
            type,
            value1,
            value2,
            winner
        );
    }
}


class StatisticNotFoundException extends RuntimeException {

    public StatisticNotFoundException(String message) {
        super(message);
    }
}

---

# 6. Why Map?

Map<StatisticType, Integer>

Because our lookup is:

player → statistic → value

Example:

statistics.get(GOALS)

Average lookup:

O(1)

Better than:

List<PlayerStatistic>

where we would need to search through the list.

---

# 7. Why Repository?

Service should not know how data is stored.

Today:

PlayerComparisonService
        ↓
HashMap

Tomorrow:

PlayerComparisonService
        ↓
PostgreSQL / Cassandra

Service remains unchanged.

Repository abstracts data access.

---

# 8. Error Cases

Player doesn't exist
    → 404

Invalid metric
    → 400

Statistic missing
    → StatisticNotFoundException

Same player
    → 400

Tie
    → winner = null / TIE

---

# 9. Important Interview Approach

DO NOT over-engineer initially.

Start:

Controller
    ↓
Service
    ↓
Repository
    ↓
Compare
    ↓
Result

Then evolve only when interviewer adds requirements.

Potential future requirements:

- Multiple sports
- Different comparison rules
- Multiple metrics
- Ranking many players
- Concurrent statistic updates
- Real-time statistics

Only then introduce:

Strategy / Factory / concurrency / caching etc.

---

# 10. Key Interview Explanation

"I'll start with a simple implementation for one sport and a basic comparison. I'll keep the comparison logic in the service, data access behind a repository, and use a map for O(1) statistic lookup. If the interviewer adds requirements such as multiple sports or different comparison rules, I'll evolve the design rather than over-engineering upfront."




# Player Comparison — LLD + API

## 1. Problem

Compare 2 players based on one statistic.

### Assumptions

* Start with **one sport**
* Compare **2 players**
* One statistic at a time
* Career / overall statistics
* Handle missing players and statistics

---

# 2. API

```text
GET /v1/players/{playerId1}/compare/{playerId2}?statistic=GOALS
```

Example:

```text
GET /v1/players/101/compare/202?statistic=GOALS
```

Controller receives:

```text
playerId1
playerId2
statistic
```

Then calls:

```java
comparePlayer(101, 202, Statistic.GOALS)
```

---

# 3. Classes

## Player

```text
Player
- playerId
- playerName
```

Represents player identity.

---

## PlayerStatistics

```text
PlayerStatistics
- playerId
- Map<Statistic, Integer> playerStats
```

Example:

```text
Player 101

GOALS   → 20
ASSISTS → 10
CORNERS → 5
```

### Responsibility

Return a statistic value.

If statistic doesn't exist:

```text
StatisticNotFoundException
```

---

## Statistic

```java
enum Statistic {
    GOALS,
    ASSISTS,
    CORNERS,
    RED_CARD,
    YELLOW_CARD
}
```

---

## PlayerStatisticsRepository

```text
Map<Integer, PlayerStatistics>
```

### Responsibility

Find statistics for a player using `playerId`.

```text
getPlayerStatistics(playerId)
```

If player doesn't exist:

```text
PlayerNotFoundException
```

### Important

Repository uses IDs for lookup:

```text
Map<playerId, PlayerStatistics>
```

while the LLD/domain model can use object relationships.

---

## PlayerComparisonService

### Responsibility

1. Get Player 1 statistics
2. Get Player 2 statistics
3. Get requested statistic
4. Compare values
5. Determine winner
6. Return result

The service should not duplicate validation owned by Repository / PlayerStatistics.

---

## PlayerComparisonResult

DTO returned from the service.

```text
PlayerComparisonResult
- player1Id
- player2Id
- statistic
- player1StatValue
- player2StatValue
- winner
```

Use `final` fields because this is an immutable result.

---

# 4. Responsibility Split

```text
PlayerStatisticsRepository
        ↓
"Do statistics exist for this player?"
        ↓
PlayerNotFoundException


PlayerStatistics
        ↓
"Does this statistic exist?"
        ↓
StatisticNotFoundException


PlayerComparisonService
        ↓
"Which player has the higher value?"
        ↓
PlayerComparisonResult
```

---

# 5. Flow

```text
GET /v1/players/101/compare/202?statistic=GOALS
                    ↓
                Controller
                    ↓
comparePlayer(101, 202, Statistic.GOALS)
                    ↓
        PlayerComparisonService
                    ↓
        PlayerStatisticsRepository
             ↙              ↘
       Player 101        Player 202
       GOALS = 20        GOALS = 15
             ↘              ↙
                Compare
                   ↓
         PlayerComparisonResult
                   ↓
              API Response
```

---

# 6. Code

```java
import java.util.HashMap;
import java.util.Map;


// --------------------
// Statistic
// --------------------

enum Statistic {
    GOALS,
    ASSISTS,
    CORNERS,
    RED_CARD,
    YELLOW_CARD
}


// --------------------
// Player
// --------------------

class Player {

    private final int playerId;
    private final String playerName;

    public Player(int playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }
}


// --------------------
// PlayerStatistics
// --------------------

class PlayerStatistics {

    private final int playerId;
    private final Map<Statistic, Integer> playerStats;

    public PlayerStatistics(
            int playerId,
            Map<Statistic, Integer> playerStats) {

        this.playerId = playerId;
        this.playerStats = playerStats;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getPlayerStatValue(Statistic statistic) {

        Integer value = playerStats.get(statistic);

        if (value == null) {
            throw new StatisticNotFoundException(
                    "Statistic not found: " + statistic
            );
        }

        return value;
    }
}


// --------------------
// Repository
// --------------------

class PlayerStatisticsRepository {

    private final Map<Integer, PlayerStatistics> playerStatData =
            new HashMap<>();

    public void savePlayerStatistics(
            int playerId,
            PlayerStatistics playerStatistics) {

        playerStatData.put(playerId, playerStatistics);
    }

    public PlayerStatistics getPlayerStatistics(int playerId) {

        PlayerStatistics statistics =
                playerStatData.get(playerId);

        if (statistics == null) {
            throw new PlayerNotFoundException(
                    "Player not found: " + playerId
            );
        }

        return statistics;
    }
}


// --------------------
// DTO
// --------------------

class PlayerComparisonResult {

    private final int player1Id;
    private final int player2Id;

    private final Statistic statistic;

    private final int player1StatValue;
    private final int player2StatValue;

    // -1 = tie
    private final int winner;

    public PlayerComparisonResult(
            int player1Id,
            int player2Id,
            Statistic statistic,
            int player1StatValue,
            int player2StatValue,
            int winner) {

        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.statistic = statistic;
        this.player1StatValue = player1StatValue;
        this.player2StatValue = player2StatValue;
        this.winner = winner;
    }
}


// --------------------
// Service
// --------------------

class PlayerComparisonService {

    private final PlayerStatisticsRepository repository;

    public PlayerComparisonService(
            PlayerStatisticsRepository repository) {

        this.repository = repository;
    }

    public PlayerComparisonResult comparePlayer(
            int player1Id,
            int player2Id,
            Statistic statistic) {

        // Repository handles player existence
        PlayerStatistics player1Statistics =
                repository.getPlayerStatistics(player1Id);

        PlayerStatistics player2Statistics =
                repository.getPlayerStatistics(player2Id);

        // PlayerStatistics handles statistic existence
        int player1Value =
                player1Statistics.getPlayerStatValue(statistic);

        int player2Value =
                player2Statistics.getPlayerStatValue(statistic);

        // Business logic
        int winner;

        if (player1Value > player2Value) {
            winner = player1Id;

        } else if (player2Value > player1Value) {
            winner = player2Id;

        } else {
            winner = -1; // tie
        }

        return new PlayerComparisonResult(
                player1Id,
                player2Id,
                statistic,
                player1Value,
                player2Value,
                winner
        );
    }
}


// --------------------
// Exceptions
// --------------------

class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(String message) {
        super(message);
    }
}


class StatisticNotFoundException extends RuntimeException {

    public StatisticNotFoundException(String message) {
        super(message);
    }
}


// --------------------
// Main
// --------------------

public class Main {

    public static void main(String[] args) {

        Map<Statistic, Integer> player1Stats =
                new HashMap<>();

        player1Stats.put(Statistic.GOALS, 20);
        player1Stats.put(Statistic.ASSISTS, 10);


        Map<Statistic, Integer> player2Stats =
                new HashMap<>();

        player2Stats.put(Statistic.GOALS, 15);
        player2Stats.put(Statistic.ASSISTS, 12);


        PlayerStatisticsRepository repository =
                new PlayerStatisticsRepository();

        repository.savePlayerStatistics(
                1,
                new PlayerStatistics(1, player1Stats)
        );

        repository.savePlayerStatistics(
                2,
                new PlayerStatistics(2, player2Stats)
        );


        PlayerComparisonService service =
                new PlayerComparisonService(repository);


        PlayerComparisonResult result =
                service.comparePlayer(
                        1,
                        2,
                        Statistic.GOALS
                );
    }
}
```

---

# 7. Saurabh's Interview Explanation

> "The controller receives the two player IDs and the statistic. The comparison service uses the repository to retrieve statistics for both players. The repository handles player existence, while PlayerStatistics handles whether the requested statistic exists. The service then performs the comparison and returns an immutable result DTO."

---

# 8. Common Bugs to Watch

1. **API gives IDs** → service receives `playerId`, not Player objects.
2. **LLD vs Repository** → objects can reference objects; repositories use IDs/keys.
3. **Constructor name** must exactly match the class name.
4. **Keep types consistent** → `int playerId` ↔ `Map<Integer, ...>`.
5. **Repository** handles `PlayerNotFoundException`.
6. **PlayerStatistics** handles `StatisticNotFoundException`.
7. **Tie handling** → don't use `>=` accidentally.
8. **Result DTO** → use `final` fields for immutability.

---

# 9. Core Mental Model

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PlayerStatistics
    ↓
Compare
    ↓
Result DTO
```

### Responsibilities

```text
Repository
→ Find player statistics

PlayerStatistics
→ Find statistic value

Service
→ Compare

DTO
→ Return result
```

### Keep it simple

Don't introduce Factory, Strategy, Kafka, Redis, concurrency, or multiple sports unless the interviewer introduces a requirement that needs them.


# Concurrency — Player Statistics

## 1. HashMap — Not Thread Safe

**What:** `HashMap` is not thread-safe for concurrent reads/writes.

**Example:**

Thread A → GOALS +1  
Thread B → GOALS +1

Both can read `GOALS = 10` and both write `11` → lost update / race condition.

**Takeaway:** Don't use `HashMap` for concurrent updates.

---

## 2. synchronized — Thread Safe but Coarse-Grained

**Solution:**

    public synchronized void incrementPlayerStat(Statistic statistic) {
        Integer current = playerStats.get(statistic);
        playerStats.put(statistic, current + 1);
    }

**Why:** Makes the entire method atomic.

**Problem:** Coarse-grained locking.

**Example:**

Thread A → GOALS +1  
Thread B → ASSISTS +1

Even though they update different statistics, B waits for A because both need the same lock.

**Takeaway:** Correct but can unnecessarily reduce concurrency.

---

## 3. ConcurrentHashMap

**Solution:**

    Map<Statistic, Integer> playerStats =
        new ConcurrentHashMap<>();

At repository level:

    Map<Integer, PlayerStatistics> playerStatisticsData =
        new ConcurrentHashMap<>();

**Example:**

Thread A → Player 1  
Thread B → Player 2

Both can access different entries concurrently.

**Takeaway:** Thread-safe concurrent access without one global lock.

---

## 4. compute() — Atomic Read → Modify → Write

### Problem

This is NOT atomic as a whole:

    Integer current = playerStats.get(Statistic.GOALS);
    playerStats.put(Statistic.GOALS, current + 1);

Because:

    GET → MODIFY → PUT

are separate operations.

Two threads can both read the same value and cause a lost update.

### Solution

    playerStats.compute(
        Statistic.GOALS,
        (key, current) -> current == null ? 1 : current + 1
    );

### What does compute() give us?

    compute(key, (key, currentValue) -> newValue)

    key          → key we passed
    currentValue → current value stored for that key
    return       → new value

**Example:**

    GOALS = 10

    Thread A → compute(GOALS) → 11
    Thread B → compute(GOALS) → 12

The read → modify → write operation is atomic for that key.

**Takeaway:**

For our `GOALS +1` use case:

    ConcurrentHashMap + compute()

is enough.

---

## 5. ReentrantReadWriteLock — When Do We Need It?

### Current Problem

We DON'T need it for:

    GOALS +1

`ConcurrentHashMap + compute()` is sufficient.

### When Would We Need It?

When ONE business operation needs to update **multiple related pieces of state** and they must remain consistent.

**Example:**

    GOAL EVENT

    GOALS         +1
    fantasyPoints +3
    lastUpdated   = now

**Requirement:**

Either ALL updates happen OR NONE should be visible.

Now we may need one larger critical section:

    GOAL EVENT
         ↓
    ┌──────────────────┐
    │ GOALS +1         │
    │ points +3        │
    │ lastUpdated      │
    └──────────────────┘
         ↑
      ONE LOCK

### Mental Model

    ONE MAP ENTRY
    → ConcurrentHashMap + compute()

    MULTIPLE RELATED STATE
    → Larger critical section
    → Consider ReentrantReadWriteLock

### Interview Answer

"I first identify what needs to be atomic. For a single statistic update, ConcurrentHashMap with compute() is sufficient. If one business operation needs to update multiple related pieces of state consistently, then I would consider a broader lock."

# LLD — Player Comparison System
## Multi-Sport Design

---

## 1. Requirement

Compare two players based on a selected statistic.

Example:

GET /v1/players/101/compare/202?statistic=GOALS

Initially we assumed one sport.

Now we want to support multiple sports.

Examples:

SOCCER:
- GOALS
- ASSISTS
- CORNERS
- FOULS
- YELLOW_CARD
- RED_CARD

BASKETBALL:
- POINTS
- REBOUNDS
- ASSISTS

TENNIS:
- ACES
- DOUBLE_FAULTS

---

## 2. Important Design Decision

`Sport` belongs to `Player`, not to `PlayerStatistics`.

Player:

    playerId
    playerName
    sport

PlayerStatistics:

    playerId
    Map<Statistic, Integer>

Reason:

A player's sport is an attribute of the player.

Statistics represent the player's performance within that sport.

---

## 3. Classes

    Player
        ↓
    playerId
    playerName
    sport

    PlayerStatistics
        ↓
    playerId
    Map<Statistic, Integer>

    PlayerRepository
        ↓
    getPlayer(playerId)

    PlayerStatisticsRepository
        ↓
    getPlayerStatistics(playerId)

    SportStatisticsRegistry
        ↓
    Map<Sport, Set<Statistic>>

    PlayerComparisonService
        ↓
    comparePlayers()

    PlayerComparisonResult
        ↓
    DTO returned to controller

---

## 4. Sport

    enum Sport {
        SOCCER,
        BASKETBALL,
        TENNIS
    }

---

## 5. Statistic

We can initially keep one enum.

    enum Statistic {
        GOALS,
        ASSISTS,
        CORNERS,
        FOULS,
        PENALTY,
        YELLOW_CARD,
        RED_CARD,

        POINTS,
        REBOUNDS,

        ACES,
        DOUBLE_FAULTS
    }

This is acceptable initially.

If the interviewer says each sport has very different statistic behavior,
we can later introduce Strategy / sport-specific statistic providers.

Don't over-engineer before the requirement demands it.

---

## 6. Player

    class Player {

        private final int playerId;
        private final String playerName;
        private final Sport sport;

        public Player(
                int playerId,
                String playerName,
                Sport sport) {

            this.playerId = playerId;
            this.playerName = playerName;
            this.sport = sport;
        }

        public Sport getSport() {
            return sport;
        }
    }

---

## 7. PlayerStatistics

    class PlayerStatistics {

        private final int playerId;

        private final Map<Statistic, Integer> playerStats =
            new HashMap<>();

        public PlayerStatistics(
                int playerId,
                Map<Statistic, Integer> playerStats) {

            this.playerId = playerId;
            this.playerStats.putAll(playerStats);
        }

        public Integer getPlayerStat(Statistic statistic) {

            Integer value = playerStats.get(statistic);

            if (value == null) {
                throw new StatisticNotFoundException(
                    "Statistic not found: " + statistic
                );
            }

            return value;
        }
    }

---

# 8. Why do we need PlayerRepository?

If `sport` belongs to `Player`, then the comparison service needs
PlayerRepository to retrieve the sport.

    PlayerRepository
            ↓
         Player
            ↓
          sport

And separately:

    PlayerStatisticsRepository
            ↓
      PlayerStatistics
            ↓
      GOALS / ASSISTS / etc.

Therefore:

    PlayerComparisonService
            │
            ├── PlayerRepository
            │      ↓
            │    Player
            │      ↓
            │    Sport
            │
            └── PlayerStatisticsRepository
                   ↓
              PlayerStatistics
                   ↓
                Statistics

If `PlayerStatistics` also stored `sport`, then PlayerRepository would not
be necessary for this particular use case.

However, that would duplicate domain information.

Cleaner model:

    Player
    → owns sport

    PlayerStatistics
    → owns statistics

Therefore, if `Player` owns the sport, the service needs
`PlayerRepository` to retrieve it.

---

# 9. SportStatisticsRegistry

We need to know which statistics are valid for each sport.

Use:

    Map<Sport, Set<Statistic>>

Example:

    class SportStatisticsRegistry {

        private final Map<Sport, Set<Statistic>> supportedStatistics =
            Map.of(
                Sport.SOCCER, Set.of(
                    Statistic.GOALS,
                    Statistic.ASSISTS,
                    Statistic.CORNERS,
                    Statistic.FOULS,
                    Statistic.PENALTY,
                    Statistic.YELLOW_CARD,
                    Statistic.RED_CARD
                ),

                Sport.BASKETBALL, Set.of(
                    Statistic.POINTS,
                    Statistic.REBOUNDS,
                    Statistic.ASSISTS
                ),

                Sport.TENNIS, Set.of(
                    Statistic.ACES,
                    Statistic.DOUBLE_FAULTS
                )
            );

        public boolean isSupported(
                Sport sport,
                Statistic statistic) {

            return supportedStatistics
                .getOrDefault(sport, Set.of())
                .contains(statistic);
        }
    }

---

# 10. Why Map<Sport, Set<Statistic>>?

We need to answer:

> Is this statistic valid for this sport?

Example:

    SOCCER
        ↓
    {GOALS, ASSISTS, CORNERS, ...}

    BASKETBALL
        ↓
    {POINTS, REBOUNDS, ASSISTS}

Then:

    supportedStatistics
        .get(sport)
        .contains(statistic);

gives approximately O(1) lookup.

This is cleaner than writing:

    if (sport == SOCCER && statistic == GOALS)
        ...

    else if (sport == BASKETBALL && statistic == POINTS)
        ...

---

# 11. PlayerRepository

    class PlayerRepository {

        private final Map<Integer, Player> players =
            new ConcurrentHashMap<>();

        public Player getPlayer(int playerId) {

            Player player = players.get(playerId);

            if (player == null) {
                throw new PlayerNotFoundException(
                    "Player " + playerId + " not found"
                );
            }

            return player;
        }

        public void savePlayer(Player player) {
            players.putIfAbsent(
                player.getPlayerId(),
                player
            );
        }
    }

---

# 12. PlayerStatisticsRepository

    class PlayerStatisticsRepository {

        private final Map<Integer, PlayerStatistics>
            playerStatisticsData =
            new ConcurrentHashMap<>();

        public PlayerStatistics getPlayerStatistics(int playerId) {

            PlayerStatistics statistics =
                playerStatisticsData.get(playerId);

            if (statistics == null) {
                throw new PlayerNotFoundException(
                    "Statistics not found for player " + playerId
                );
            }

            return statistics;
        }

        public void savePlayerStatistics(
                int playerId,
                PlayerStatistics playerStatistics) {

            playerStatisticsData.putIfAbsent(
                playerId,
                playerStatistics
            );
        }
    }

---

# 13. Comparison Service

    class PlayerComparisonService {

        private final PlayerRepository playerRepository;

        private final PlayerStatisticsRepository
            playerStatisticsRepository;

        private final SportStatisticsRegistry
            sportStatisticsRegistry;

        public PlayerComparisonService(
                PlayerRepository playerRepository,
                PlayerStatisticsRepository playerStatisticsRepository,
                SportStatisticsRegistry sportStatisticsRegistry) {

            this.playerRepository = playerRepository;
            this.playerStatisticsRepository =
                playerStatisticsRepository;
            this.sportStatisticsRegistry =
                sportStatisticsRegistry;
        }

        public PlayerComparisonResult comparePlayers(
                int playerId1,
                int playerId2,
                Statistic statistic) {

            Player player1 =
                playerRepository.getPlayer(playerId1);

            Player player2 =
                playerRepository.getPlayer(playerId2);

            if (player1.getSport() != player2.getSport()) {

                throw new IllegalArgumentException(
                    "Players must belong to the same sport"
                );
            }

            if (!sportStatisticsRegistry.isSupported(
                    player1.getSport(),
                    statistic)) {

                throw new IllegalArgumentException(
                    "Statistic " + statistic +
                    " is not supported for " +
                    player1.getSport()
                );
            }

            PlayerStatistics player1Statistics =
                playerStatisticsRepository
                    .getPlayerStatistics(playerId1);

            PlayerStatistics player2Statistics =
                playerStatisticsRepository
                    .getPlayerStatistics(playerId2);

            int player1Value =
                player1Statistics.getPlayerStat(statistic);

            int player2Value =
                player2Statistics.getPlayerStat(statistic);

            int winner = -1;

            if (player1Value != player2Value) {

                winner = player1Value > player2Value
                    ? playerId1
                    : playerId2;
            }

            return new PlayerComparisonResult(
                playerId1,
                playerId2,
                statistic,
                player1Value,
                player2Value,
                winner
            );
        }
    }

---

# 14. Validation Flow

    Request
       ↓
    Get Player 1
    Get Player 2
       ↓
    Same sport?
       │
       ├── NO
       │     ↓
       │ IllegalArgumentException
       │
       └── YES
             ↓
    Is statistic valid for sport?
             │
             ├── NO
             │     ↓
             │ IllegalArgumentException
             │
             └── YES
                   ↓
           Get PlayerStatistics
                   ↓
           Get requested statistic
                   ↓
              Compare values
                   ↓
           PlayerComparisonResult

---

# 15. REST API

    GET /v1/players/{playerId1}/compare/{playerId2}?statistic=GOALS

Example:

    GET /v1/players/101/compare/202?statistic=GOALS

Controller:

    @RestController
    @RequestMapping("/v1/players")
    class PlayerComparisonController {

        private final PlayerComparisonService comparisonService;

        public PlayerComparisonController(
                PlayerComparisonService comparisonService) {

            this.comparisonService = comparisonService;
        }

        @GetMapping("/{playerId1}/compare/{playerId2}")
        public PlayerComparisonResult comparePlayers(
                @PathVariable int playerId1,
                @PathVariable int playerId2,
                @RequestParam Statistic statistic) {

            return comparisonService.comparePlayers(
                playerId1,
                playerId2,
                statistic
            );
        }
    }

---

# 16. Key Design Decisions

1. `Player` owns `Sport` because sport is a player attribute.

2. `PlayerStatistics` owns the player's actual statistics.

3. If `sport` exists only on `Player`, we need `PlayerRepository`
   to determine the sport.

4. `PlayerStatisticsRepository` retrieves performance data.

5. `SportStatisticsRegistry` owns the mapping:
   `Map<Sport, Set<Statistic>>`.

6. Validate that both players belong to the same sport.

7. Validate that the requested statistic is supported for that sport.

8. `ConcurrentHashMap` is appropriate for shared repository state.

9. Don't introduce `PlayerRepository` unless the use case actually
   needs player information.

10. Don't introduce Strategy Pattern until different sports actually
    require different comparison behavior.

---

# 17. Interview Mental Model

    Player
    → Who is the player?
    → What sport do they play?

    PlayerStatistics
    → What are the player's numbers?

    PlayerRepository
    → Find Player

    PlayerStatisticsRepository
    → Find Statistics

    SportStatisticsRegistry
    → What statistics are valid for this sport?

    PlayerComparisonService
    → Validate + compare

    Controller
    → HTTP → Service

    DTO
    → Service → HTTP response

---

# 18. Main Takeaway

The important LLD principle here:

> Put data and responsibility where they naturally belong.

    Player
    → identity + sport

    PlayerStatistics
    → performance data

    Registry
    → sport rules/configuration

    Repository
    → persistence/retrieval

    Service
    → business logic

    Controller
    → HTTP handling

Don't add a repository, strategy, lock, or abstraction simply because
it is a common design pattern.

Add it when the requirement gives us a reason to need it.