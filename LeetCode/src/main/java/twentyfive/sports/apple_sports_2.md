# Apple Sports System Design Interview
## Design: Apple Sports Live Game Service (Part 1)

---

# Problem Statement

Design the backend for **Apple Sports Live Game Service**.

The service powers:

- Apple Sports App
- Apple TV
- Live Activities
- Widgets

It should support multiple sports:

- NBA
- NFL
- MLB
- NHL
- Soccer

Users should be able to:

- View live games
- View game details
- Follow favorite teams
- Receive live notifications

---

# Step 1 - Clarify Requirements

Never jump into the architecture immediately.

A Senior (ICT4) engineer starts by asking clarifying questions.

## Functional Questions

- Is this for one sport or multiple sports?
- Is the service responsible only for serving APIs or also ingesting provider events?
- Do we support:
  - Live scores?
  - Play-by-play events?
  - Player statistics?
  - Standings?
  - Team schedules?
- Do users receive notifications?
- Do we store historical events?

---

## Scale Questions

Ask about:

- Number of external data providers
- Peak events/sec
- Peak concurrent users
- Monthly Active Users
- Read vs Write ratio
- Latency requirements

Example:

> Could you share the expected scale in terms of concurrent users, event throughput, QPS, and latency requirements?

---

## Consistency Questions

- Is eventual consistency acceptable?
- How fresh should scores be?
- Can clients briefly see stale data?

---

## Example Interview Assumptions

For this design assume:

### Functional

- Multiple sports
- 3 external providers
- Historical events retained
- Users can follow favorite teams
- Notifications supported

### Scale

- 20 Million Monthly Active Users
- 2 Million concurrent users
- 10,000 events/sec during peak
- Read heavy (~100:1)
- Target latency < 1 second

---

# Step 2 - High Level Architecture

Instead of thinking:

Client -> API -> Database

Think in terms of independent responsibilities.

```
                   Sports Providers
                          │
                          ▼
                 Ingestion Service
                          │
                Normalize Events
                          │
                          ▼
                     Kafka Topics
        ┌─────────────┼─────────────┐
        ▼             ▼             ▼
 Game State     Notifications    Analytics
   Consumer        Consumer       Consumer
        ▼             ▼             ▼
     Cassandra      Push        Cassandra
        │
        ▼
      Redis
        │
        ▼
-----------------------------------------------
                Serving API Layer
-----------------------------------------------
        │          │            │
        ▼          ▼            ▼
 Apple Sports   Apple TV   Live Activities
```

---

# Responsibilities

## Ingestion Layer

Responsible for:

- Receiving events from providers
- Validation
- Authentication
- Schema validation
- Payload normalization

Should remain lightweight.

---

## Canonical Event Model

Each provider sends different payloads.

Instead of allowing downstream systems to understand every provider format:

```
Provider A

Goal:
{
  scoreHome
  scoreAway
}

Provider B

{
  home_points
  away_points
}
```

Normalize once into:

```
SportsEvent
```

Every downstream service understands only one schema.

Benefits:

- Loose coupling
- Easier onboarding of new providers
- Cleaner downstream services

---

# Kafka

Kafka becomes the central event backbone.

Instead of:

```
Provider

↓

Database
```

Use:

```
Provider

↓

Ingestion

↓

Kafka

↓

Consumers
```

---

# Why Kafka?

## 1. Decoupling

The ingestion service is no longer tightly coupled to downstream systems.

---

## 2. Lower Ingestion Latency

The ingestion service:

- validates
- normalizes
- publishes to Kafka
- returns

```
202 Accepted
```

The provider does not wait for:

- Cassandra
- Notifications
- Analytics

---

## 3. Independent Scaling

Each consumer group scales independently.

Examples:

- Game State Consumer
- Notification Consumer
- Analytics Consumer
- Search Consumer
- Statistics Consumer

---

## 4. Fault Isolation

If Analytics is slow:

Game State still works.

Notifications still work.

No cascading failures.

---

## 5. Buffering Traffic Spikes

Kafka absorbs bursts.

Example:

NBA Finals

or

Super Bowl

Event rate spikes dramatically.

Kafka buffers while consumers process independently.

---

## 6. Replay

One of Kafka's biggest advantages.

Example:

A bug corrupts standings.

Fix consumer.

Replay Kafka events.

Rebuild state.

No provider replay required.

---

# Consumer Groups

Example:

```
                     Kafka
                        │
      ┌──────────┬───────────────┬─────────────┐
      ▼          ▼               ▼             ▼
 Game State   Notifications   Analytics   Search Index
   Consumer      Consumer      Consumer      Consumer
```

Each consumer owns a single responsibility.

---

# Storage

## Cassandra

Stores:

- Current game state
- Historical game events
- Standings
- Team schedules

Chosen because:

- High write throughput
- Horizontal scalability
- Time-series friendly
- High availability

---

## Redis

Used for:

- Live game
- Live games list
- Team schedule
- Standings
- Frequently accessed metadata

Purpose:

Sub-millisecond reads.

---

# Serving Layer

Clients never access Kafka.

Clients call REST APIs.

Example:

```
GET /v1/games/{gameId}

GET /v1/games/live

GET /v1/leagues/{leagueId}/standings

GET /v1/teams/{teamId}/schedule

POST /v1/users/{userId}/subscriptions
```

Serving Layer reads primarily from:

Redis

Falls back to Cassandra when necessary.

---

# Kafka Partitioning

## Partition Key

Partition by:

```
matchId
```

Why?

All events for the same game remain in one partition.

Kafka guarantees ordering **within a partition**.

---

Example

```
Match 123

↓

Partition 6

GOAL

FOUL

YELLOW CARD

RED CARD

FULL TIME
```

Ordering is preserved.

---

# Why Not TeamId?

Example:

```
Lakers vs Warriors
```

If partitioned by TeamId:

```
Lakers

↓

Partition 2

Warriors

↓

Partition 9
```

Now game events can arrive out of order.

Ordering is broken.

---

# Why Ordering Matters

Sports events are stateful.

Example:

```
Score

100-99

↓

102-99

↓

Correction

101-99
```

Or

```
Goal

↓

VAR Review

↓

Goal Cancelled
```

Processing events out of order leads to an incorrect game state.

Keeping all events for a match in one partition preserves correctness.

---

# Trade-off

One very popular game (e.g. Super Bowl, World Cup Final, NBA Finals) may become a hot partition.

Accepting this trade-off is reasonable because:

- Ordering for a single game is critical.
- Different games naturally distribute across many partitions.
- Consumer groups can scale horizontally across partitions.
- Multiple matches share partitions using Kafka's hash-based partitioning.

Kafka determines the partition as:

```
hash(matchId) % numberOfPartitions
```

---

# Key Interview Takeaways

- Start with clarifying questions.
- Discuss scale before architecture.
- Separate ingestion and serving paths.
- Normalize provider payloads into a canonical event model.
- Use Kafka to decouple producers from consumers.
- Explain *why* each component exists.
- Partition Kafka by `matchId` to preserve ordering.
- Mention replay, buffering, fault isolation, and independent scaling.
- Connect infrastructure decisions back to the sports domain (ordering, score corrections, play-by-play events).


# Apple Sports System Design Interview
## Part 2 - Game State Consumer, State Management & Storage Design

---

# Interview Question

Now that we have Kafka as the event backbone...

How does the **Game State Consumer** process incoming events?

Example:

```
GAME_STARTED

↓

HOME +2

↓

AWAY +3

↓

TIMEOUT

↓

HOME +3

↓

GAME_ENDED
```

---

# Responsibilities of the Game State Consumer

The Game State Consumer is responsible for maintaining the **latest state** of a game.

It is **NOT** responsible for storing historical events.

Responsibilities:

- Consume events from Kafka
- Maintain current game state
- Update Redis
- Persist latest game snapshot
- Ensure ordering
- Handle duplicate events (later discussion)

---

# State Management

Question:

When an event arrives...

```
HOME +3
```

How does the consumer know the current score?

Current score might be:

```
95 - 92
```

It needs existing state before applying the new event.

---

# Option 1 - Read Redis (Recommended)

```
Kafka

↓

Game State Consumer

↓

Read Current Game State

(Redis)

↓

Apply Event

↓

Updated Game State

↓

Write Back to Redis

↓

Persist Snapshot to Cassandra
```

Example

Redis contains

```json
{
  "matchId":"123",
  "homeScore":95,
  "awayScore":92,
  "period":4
}
```

Incoming Event

```
HOME +3
```

Updated State

```
98 - 92
```

Redis is updated immediately.

This is the preferred approach because Redis provides extremely low latency.

---

# Option 2 - Keep Everything In Memory

Example

```java
ConcurrentHashMap<MatchId, GameState>
```

Advantages

- Fastest possible

Problems

- Lost on restart
- Consumer rebalance moves partitions
- Requires rebuilding state
- Difficult recovery

Generally not recommended as the primary source of state.

---

# Option 3 - Read Cassandra Every Event

```
Kafka

↓

Consumer

↓

Read Cassandra

↓

Apply Event

↓

Write Cassandra
```

Works correctly but performs poorly.

At

```
10,000 events/sec
```

reading Cassandra before every update becomes expensive.

Too much read amplification.

---

# Recommended Design

Maintain current state in Redis.

Persist snapshots to Cassandra.

```
Kafka

↓

Game State Consumer

↓

Read Redis

↓

Apply Event

↓

Update Redis

↓

Persist Game Snapshot

↓

Cassandra
```

---

# Historical Events

A separate consumer stores immutable event history.

```
Kafka

↓

Event Storage Consumer

↓

Append Event

↓

GameEvent Table
```

Notice the separation of responsibilities.

Game State Consumer

Maintains latest state.

Event Storage Consumer

Maintains historical events.

---

# Why Separate Consumers?

Single Responsibility Principle.

Different workloads.

Game State

- latency sensitive
- updates current score

Event Storage

- append only
- analytics
- replay
- audit

Each consumer group scales independently.

---

# Storage Design

We do NOT store only one representation.

Instead we maintain three layers.

---

## Layer 1 - Redis

Stores

Latest game state.

Example

```
Match 123

105 - 101

Q4

02:35

LIVE
```

Purpose

- Fastest reads
- Apple Sports API
- Apple TV
- Live Activities

---

## Layer 2 - Cassandra GameState

Stores durable snapshots.

Example schema

```
GameState

matchId

homeScore

awayScore

period

clock

status

lastEventId

lastUpdated
```

Purpose

- Durable current state
- Recovery after Redis restart
- Direct database queries
- Faster than replaying events

---

## Layer 3 - Cassandra GameEvent

Stores immutable event history.

Example schema

```
GameEvent

matchId

eventId

sequenceNumber

eventType

payload

timestamp
```

Example

```
GAME_STARTED

HOME +2

AWAY +3

TIMEOUT

HOME +3

GAME_ENDED
```

Purpose

- Replay
- Audit
- Analytics
- Play-by-play
- Historical reporting

---

# Why Not Store Only Raw Events?

Suppose the API receives

```
GET /games/123
```

If only raw events exist...

The service must replay

```
GAME_STARTED

↓

HOME +2

↓

AWAY +3

↓

TIMEOUT

↓

HOME +3
```

Potentially thousands of events.

Too slow for a live sports application.

---

# Why Not Store Only Snapshots?

Fast reads.

But you lose:

- Replay
- Audit
- Analytics
- Play-by-play
- Ability to rebuild downstream systems

Not acceptable for production systems.

---

# Recommended Approach

Store BOTH.

Current snapshot

+

Immutable history

This combines

- fast serving
- durability
- replay capability
- analytics
- operational recovery

---

# Redis Recovery

Question

What happens if Redis crashes?

Bad approach

Replay every event from Kafka.

This may involve replaying millions of historical events.

Better approach

```
Redis Restart

↓

Load GameState Snapshots

↓

Warm Redis

↓

Resume Processing
```

Snapshots dramatically reduce recovery time.

---

# Benefits of This Design

Redis

- Sub-millisecond reads

GameState Snapshot

- Durable current state
- Recovery
- Fast lookup

GameEvent History

- Replay
- Audit
- Analytics
- Historical queries

Each storage layer exists because it optimizes a different access pattern.

---

# Interview Takeaways

- The Game State Consumer owns the latest game state.
- Redis stores the latest in-memory state.
- Cassandra GameState stores durable snapshots.
- Cassandra GameEvent stores immutable history.
- Separate consumers improve scalability and maintainability.
- Design storage around access patterns, not around the database technology.
- Explain WHY each representation exists instead of simply listing technologies.


# Apple Sports System Design Interview
## Part 3 - Updated Architecture, State Management & Stream Processing

---

# Updated High-Level Architecture

```
                                   +----------------------+
                                   |  Sports Providers    |
                                   | (Opta, Sportradar..) |
                                   +----------+-----------+
                                              |
                                              |
                                   POST /v1/events
                                              |
                                              v
                               +---------------------------+
                               |     Ingestion Service     |
                               |---------------------------|
                               | Authentication            |
                               | Validation                |
                               | Rate Limiting             |
                               | Payload Normalization     |
                               | Canonical Event Model     |
                               +------------+--------------+
                                            |
                                            |
                                     Publish Event
                                            |
                                            v
                              +----------------------------+
                              |        Kafka Topic         |
                              |    Key = matchId           |
                              +-------------+--------------+
                                            |
        -------------------------------------------------------------------------
        |                          |                          |                  |
        |                          |                          |                  |
        v                          v                          v                  v
+-------------------+     +-------------------+     +----------------+    +----------------+
| Game State        |     | Event Storage     |     | Notification   |    | Analytics      |
| Consumer          |     | Consumer          |     | Consumer       |    | Consumer       |
+---------+---------+     +---------+---------+     +--------+-------+    +----------------+
          |                         |                         |
          |                         |                         |
          |                  Append Event                     |
          |                         |                         |
          |                         v                         |
          |             +------------------------+            |
          |             | Cassandra             |            |
          |             | GameEvent Table       |            |
          |             +------------------------+            |
          |
          |
Read Current State
          |
          v
+-------------------------+
| Redis                   |
| Current Game State      |
+------------+------------+
             |
      Apply Event
             |
             v
+-------------------------+
| Updated Game State      |
+------------+------------+
             |
             |
             v
+-------------------------+
| Cassandra               |
| GameState Snapshot      |
+------------+------------+
             |
             |
             v
+--------------------------------------+
|         Serving API Layer            |
|--------------------------------------|
| GET /games/{id}                      |
| GET /games/live                      |
| GET /teams/{id}/schedule             |
| GET /standings                       |
| POST /subscriptions                  |
+------------------+-------------------+
                   |
      ----------------------------------------
      |                |                    |
      v                v                    v
 Apple Sports      Apple TV         Live Activities
```

---

# Responsibilities

## Ingestion Service

Responsible for

- Authentication
- Validation
- Rate Limiting
- Payload normalization
- Converting provider payloads into a canonical event model

It should remain lightweight.

After publishing to Kafka it immediately returns

```
202 Accepted
```

---

# Game State Consumer

This consumer owns the **current state** of every game.

Responsibilities

- Consume events from Kafka
- Read latest game state
- Apply business logic
- Update current score
- Update Redis
- Persist latest GameState snapshot

It is **NOT** responsible for storing history.

---

# Event Storage Consumer

This consumer owns the immutable history.

Responsibilities

- Consume every Kafka event
- Append raw event to Cassandra
- Preserve replay capability
- Support play-by-play
- Support audit
- Support analytics

This table is append-only.

---

# Notification Consumer

Responsible for

- Favorite team subscriptions
- Push notifications
- Live Activities updates
- Fan alerts

Independent scaling.

---

# Analytics Consumer

Responsible for

- Historical statistics
- Team statistics
- Leaderboards
- Reporting
- Advanced analytics

---

# State Management

Question

How does the Game State Consumer know the current score?

Example

```
Current

95-92

↓

Incoming Event

HOME +3

↓

Updated

98-92
```

The consumer needs current state before applying the event.

---

# Three Possible Designs

## Option 1 (Recommended)

Read Redis.

```
Kafka

↓

Game State Consumer

↓

Read Redis

↓

Apply Event

↓

Update Redis

↓

Persist Snapshot
```

Advantages

- Very low latency
- Simple
- Easy to reason about
- Easy recovery

---

## Option 2

Keep everything in memory.

Example

```java
ConcurrentHashMap<MatchId, GameState>
```

Advantages

- Extremely fast

Problems

- Lost on restart
- Hard recovery
- Difficult partition rebalance
- Not durable

---

## Option 3

Read Cassandra every event.

```
Kafka

↓

Consumer

↓

Read Cassandra

↓

Apply Event

↓

Write Cassandra
```

Works correctly but causes excessive reads.

Not suitable for

```
10,000 events/sec
```

---

# Recommended State Flow

```
Kafka Event

↓

Read Redis

↓

Apply Business Logic

↓

Update Current Game State

↓

Update Redis

↓

Persist Snapshot

↓

Done
```

---

# Storage Model

Instead of storing one representation...

Maintain three.

---

## Redis

Stores

Latest game state.

Example

```
Game 123

Lakers 105

Warriors 101

Q4

02:35
```

Purpose

- Lowest latency
- Apple Sports
- Apple TV
- Live Activities

---

## Cassandra GameState

Stores

Durable snapshot.

Example

```
matchId

homeScore

awayScore

period

clock

status

lastEventId

lastUpdated
```

Purpose

- Durable current state
- Recovery
- Direct lookup
- Cache warm-up

---

## Cassandra GameEvent

Stores immutable history.

Example

```
matchId

eventId

sequenceNumber

eventType

payload

timestamp
```

Purpose

- Replay
- Audit
- Analytics
- Play-by-play
- Historical reporting

---

# Why Store Both?

## Only Raw Events

Problem

Every API request requires replaying thousands of events.

Too slow.

---

## Only Snapshots

Problem

Lose

- Replay
- Audit
- Analytics
- Historical events

---

## Best Design

Maintain

- Current Snapshot
- Immutable Event History

This optimizes

- Reads
- Recovery
- Replay
- Analytics

---

# Redis Recovery

Suppose Redis crashes.

Bad approach

Replay every event.

```
Millions of Events
```

Better approach

```
Redis Restart

↓

Load GameState snapshots

↓

Warm Cache

↓

Resume
```

Recovery becomes much faster.

---

# Can We Optimize Further?

Yes.

Notice the current flow.

```
Redis Read

↓

Update

↓

Redis Write
```

Every event performs

- One network read
- One network write

At

```
10,000 events/sec
```

this becomes expensive.

---

# Kafka Streams

Instead of reading Redis every event...

Maintain state locally.

```
Kafka

↓

Kafka Streams

↓

Local State Store

↓

Redis

↓

Cassandra
```

The local state store already knows

```
Game 123

95-92
```

Incoming

```
HOME +3
```

Immediately becomes

```
98-92
```

No Redis read required.

Only the updated state is written to Redis.

---

# Kafka Streams State Store

Think of it as

```java
Map<MatchId, GameState>
```

except

Kafka Streams manages it.

Benefits

- Local memory
- Durable
- Automatically restored
- Fault tolerant
- Backed by Kafka changelog topics

---

# Kafka Streams Recovery

If the service crashes

```
Restart

↓

Replay Changelog Topic

↓

Restore State Store

↓

Continue Processing
```

No custom recovery logic required.

---

# Apache Flink

Flink is also a stateful stream processor.

Unlike Kafka Streams

it runs as a distributed processing engine.

---

# Flink Strengths

Excellent for

- Windowed computations
- Rolling statistics
- Live leaderboards
- Fantasy scoring
- Average possession
- Win probability
- Multi-stream joins
- Complex event processing

Example

```
Game Events

↓

5-minute Window

↓

Compute Team Statistics

↓

Publish Results
```

---

# Flink State

Flink also maintains local state.

Example

```
Match

↓

Current Score

↓

Possession

↓

Shots

↓

Corners
```

without requiring Redis reads.

---

# Flink Checkpointing

Periodically

```
Current State

↓

Checkpoint

↓

Durable Storage
```

If a node crashes

```
Restore Checkpoint

↓

Continue Processing
```

Supports exactly-once processing.

---

# Kafka Streams vs Flink

| Kafka Streams | Apache Flink |
|---------------|--------------|
| Java library | Distributed stream engine |
| Embedded in application | Separate cluster |
| Simpler operations | More operational overhead |
| Local state store | Distributed state backend |
| Changelog recovery | Checkpoint recovery |
| Great for maintaining game state | Great for advanced analytics |

---

# What Would I Recommend?

For maintaining **current game state**

➡️ Kafka Streams

Reason

- Simpler
- Lower latency
- Local state
- Eliminates Redis reads
- Natural fit for keyed-by-matchId processing

For advanced analytics

➡️ Apache Flink

Reason

- Windowing
- Aggregations
- Joins
- Streaming analytics
- Leaderboards
- Fantasy scoring

---

# Interview Guidance

Start with the simpler architecture.

```
Kafka

↓

Redis

↓

Cassandra
```

Only introduce Kafka Streams or Flink if the interviewer asks

- Can this be optimized?
- Can we eliminate Redis reads?
- How would you support windowed analytics?

Senior engineers start with the simplest design that satisfies the requirements, then evolve it based on concrete bottlenecks rather than introducing additional complexity prematurely.

# Apple Sports System Design Interview
## Part 4 - Reliability, Idempotency & Out-of-Order Event Processing

---

# Updated Architecture

```
                                   +----------------------+
                                   |  Sports Providers    |
                                   | (Opta, Sportradar..) |
                                   +----------+-----------+
                                              |
                                              |
                                   POST /v1/events
                                              |
                                              v
                               +---------------------------+
                               |     Ingestion Service     |
                               |---------------------------|
                               | Authentication            |
                               | Validation                |
                               | Rate Limiting             |
                               | Canonical Model           |
                               +------------+--------------+
                                            |
                                            |
                                    Publish Event
                                            |
                                            v
                              +----------------------------+
                              |        Kafka Topic         |
                              |    Key = matchId           |
                              +-------------+--------------+
                                            |
       -------------------------------------------------------------------------
       |                         |                         |                    |
       |                         |                         |                    |
       v                         v                         v                    v
+-------------------+   +-------------------+    +----------------+    +----------------+
| Game State        |   | Event Storage     |    | Notification   |    | Analytics      |
| Consumer          |   | Consumer          |    | Consumer       |    | Consumer       |
+---------+---------+   +---------+---------+    +--------+-------+    +----------------+
          |                        |                         |
          |                        |                         |
          |                 Append Event                     |
          |                        |                         |
          |                        v                         |
          |            +------------------------+            |
          |            | Cassandra             |            |
          |            | GameEvent             |            |
          |            +------------------------+            |
          |
          |
          |   Read Current State
          |
          v
+-------------------------+
| Redis                   |
| Current Game State      |
+------------+------------+
             |
             |
             | Apply Event
             |
             v
+-------------------------+
| Updated Game State      |
+------------+------------+
             |
             |
             v
+-------------------------+
| Cassandra               |
| GameState Snapshot      |
+------------+------------+
             |
             |
             v
+--------------------------------------+
|        Serving API Layer             |
|--------------------------------------|
| GET /games/{id}                      |
| GET /games/live                      |
| GET /standings                       |
| GET /schedule                        |
| POST /subscriptions                  |
+------------------+-------------------+
                   |
      ---------------------------------------
      |                 |                   |
      v                 v                   v
 Apple Sports      Apple TV        Live Activities
```

---

# Reliability

Distributed systems must handle:

- Duplicate events
- Consumer retries
- Provider retries
- Out-of-order events
- Consumer crashes

---

# Duplicate Events

Example

```
EventId = E123

HOME +3

↓

Provider Timeout

↓

Provider Retries

↓

HOME +3
```

Without protection

```
95-92

↓

98-92

↓

101-92 ❌
```

---

# Where Should We Handle Idempotency?

There are two possible places.

---

## Option 1

Deduplicate during ingestion.

```
Provider

↓

Ingestion API

↓

Redis(eventId)

↓

Kafka
```

If

```
eventId
```

already exists

Ignore request.

### Advantages

- Less Kafka traffic
- Downstream consumers do less work

### Problem

This only protects against

- Provider retries

It does NOT protect against

- Kafka redelivery
- Consumer restart
- Consumer retry

Therefore this should NOT be the only protection.

---

## Option 2 (Recommended)

Consumer-side idempotency.

```
Kafka

↓

Consumer

↓

Has Event Already Been Processed?

↓

No

↓

Apply Event

↓

Persist State

↓

Mark Event Processed
```

If the same event arrives again

```
Duplicate

↓

Ignore
```

This protects against

- Provider retries
- Kafka at-least-once delivery
- Consumer restart
- Consumer retry

---

# Where Store Processed Event IDs?

Several options exist.

---

## Redis

Store

```
eventId
```

with

```
TTL = 24 hours
```

Advantages

- Very fast
- Suitable because duplicate events usually occur shortly after the original
- Memory usage remains bounded

Disadvantages

- Requires persistence or backup if Redis restarts

---

## Cassandra

Table

```
ProcessedEvent

eventId (PK)

processedTimestamp
```

Consumer performs

```
INSERT ... IF NOT EXISTS
```

If insert succeeds

Process event.

Otherwise

Ignore duplicate.

Advantages

- Durable
- Survives restart

Disadvantages

- Conditional writes (LWT) are slower
- Adds latency

---

## Production Recommendation

Use both.

```
Receive Event

↓

Redis Lookup

↓

Duplicate?

↓

Yes

↓

Ignore

↓

No

↓

Process Event

↓

Persist GameState

↓

Persist Raw Event

↓

Store EventId in Redis

↓

Persist EventId in Cassandra
```

Redis keeps the hot path fast.

Cassandra provides durability.

---

# Kafka Producer Settings

Producer should use

```
acks=all

enable.idempotence=true
```

Benefits

## acks=all

Leader waits for all in-sync replicas.

Improves durability.

---

## enable.idempotence=true

Prevents duplicate messages caused by producer retries.

If the producer retries

Kafka stores only one copy.

---

Important

These settings **do not** solve

- Out-of-order events from the provider.

They only protect communication between

Producer

↓

Kafka Broker.

---

# Out-of-Order Events

Suppose the actual game events are

```
Sequence 10

↓

Sequence 11

↓

Sequence 12
```

But because of provider delays

Kafka receives

```
10

↓

12

↓

11
```

Kafka preserves the order it receives.

It does NOT reorder events.

---

# What Information Should A Provider Send?

Every event should include

```
matchId

eventId

sequenceNumber

eventTimestamp
```

The important field is

```
sequenceNumber
```

It represents the authoritative ordering.

The timestamp is useful for

- Display
- Analytics
- Debugging

It is NOT sufficient for ordering because

- Multiple events may share a timestamp
- Clock precision varies
- Network delays exist

---

# Consumer Processing

Consumer maintains

```
lastProcessedSequence
```

Suppose

```
lastProcessedSequence = 10
```

Incoming event

```
Sequence = 12
```

Expected

```
11
```

Consumer detects a gap.

Instead of processing immediately

Buffer

```
12
```

until

```
11
```

arrives.

Then process

```
11

↓

12
```

Ordering is preserved.

---

# Missing Events

What if

```
11
```

never arrives?

Do not wait forever.

Introduce a timeout.

```
Receive 12

↓

Wait 2 seconds

↓

11 arrives?

YES

↓

Process 11

↓

Process 12

NO

↓

Raise Alert

or

Request Replay

or

Apply Business Policy
```

The exact timeout depends on product requirements.

---

# Kafka Streams

Kafka Streams naturally supports

- Stateful processing
- Local state stores
- Ordered processing by key

It can maintain

```
lastProcessedSequence

Current Score

Current Quarter
```

locally without requiring Redis reads.

---

# Apache Flink

Flink is well suited for

- Event-time processing
- Watermarks
- Late arriving events
- Windowed aggregations
- Complex stream processing

Instead of processing by

```
Arrival Time
```

Flink processes by

```
Event Time
```

allowing more accurate handling of delayed events.

---

# Watermarks

A watermark represents

> "I believe all events up to this event time have arrived."

Late events can still arrive after the watermark, but Flink provides configurable strategies to handle them.

---

# Interview Discussion

One important architectural question is:

> Should Apple implement complex buffering logic, or require providers to deliver ordered events per match?

For commercial providers such as Sportradar or Stats Perform, it is reasonable to require:

- Monotonically increasing sequence numbers.
- Ordered delivery per match whenever possible.

The ingestion pipeline can still handle occasional late events, but should avoid unnecessary complexity if provider guarantees are sufficient.

---

# Key Interview Takeaways

- Handle idempotency primarily at the consumer.
- Ingestion deduplication is an optimization, not the primary defense.
- `acks=all` and `enable.idempotence=true` protect producer-to-Kafka communication only.
- Kafka preserves arrival order, not business order.
- Prefer sequence numbers over timestamps for ordering.
- Buffer future events until missing sequence numbers arrive.
- Use timeouts to prevent indefinite waiting.
- Kafka Streams simplifies stateful processing.
- Flink excels at event-time processing, watermarks, and late-event handling.
- Always ask whether the complexity belongs in your system or should be guaranteed by upstream providers.


# Why Event Timestamps Are NOT Enough

A common interview question is:

> Why can't we simply use the event timestamp to determine ordering?

The short answer is:

> **Timestamps tell us *when* an event occurred, but they are not always sufficient to determine the correct business order.**

The preferred solution is to use:

- **Sequence Number** → Authoritative ordering
- **Timestamp** → Display, analytics, debugging

---

# Problem 1 - Multiple Events Can Share The Same Timestamp

Example

```
10:01:05.123

GOAL

10:01:05.123

YELLOW CARD
```

Both events have the exact same timestamp.

Question:

Which happened first?

The timestamp cannot answer that.

A sequence number can.

```
Sequence 101

↓

Sequence 102
```

---

# Problem 2 - Timestamp Precision

Suppose the provider records timestamps only to the nearest second.

```
10:01:05

GOAL

10:01:05

PENALTY

10:01:05

RED CARD
```

Three events.

One timestamp.

Ordering is impossible.

---

# Problem 3 - Clock Skew

Different systems rarely have perfectly synchronized clocks.

Example

```
Referee System

10:01:05.200

↓

Statistics System

10:01:05.180
```

Which event actually happened first?

You cannot rely solely on timestamps because clocks can drift.

---

# Problem 4 - Timestamp != Business Order

This is the most important reason.

Example

```
GOAL

↓

VAR Review

↓

GOAL CANCELLED
```

Provider data

```
GOAL

timestamp = 10:01:05

sequence = 150

↓

GOAL CANCELLED

timestamp = 10:01:08

sequence = 151
```

Business logic depends on the **sequence**.

The game state must apply events in the provider-defined order.

Another example

```
Basket

↓

Timeout

↓

Official Score Correction
```

The correction may arrive later but must still be applied after the original score event.

---

# Problem 5 - Network Delays

Suppose

```
Event A

timestamp = 10:01:05
```

is delayed on the network.

Meanwhile

```
Event B

timestamp = 10:01:06
```

arrives first.

Arrival order becomes

```
B

↓

A
```

Arrival order is different from event time.

Kafka preserves **arrival order**, not necessarily the order in which events actually occurred.

---

# Why Sequence Numbers Are Better

Provider sends

```
Sequence 101

↓

Sequence 102

↓

Sequence 103
```

Consumer maintains

```
lastProcessedSequence = 101
```

If

```
103
```

arrives

the consumer immediately knows

```
102 is missing.
```

It can

- Buffer event 103
- Wait for 102
- Process 102
- Then process 103

Ordering is preserved.

---

# Why Keep The Timestamp?

Timestamps are still extremely valuable.

Use them for

- Display ("Goal scored at 72:15")
- Analytics
- Historical reporting
- Measuring end-to-end latency
- Event-time processing (Apache Flink)
- Auditing

Do **not** use timestamps as the authoritative ordering mechanism.

---

# Recommended Event Schema

```
SportsEvent

matchId

eventId

sequenceNumber

eventTimestamp

eventType

payload
```

Where

- **sequenceNumber** → Ordering
- **eventTimestamp** → Display, analytics, debugging

---

# Interview Answer

> I would ask the provider to include both a monotonically increasing **sequence number** and an **event timestamp**. The sequence number becomes the authoritative ordering key because timestamps may have limited precision, clock skew, or multiple events occurring at the same instant. The timestamp remains valuable for analytics, user display, and event-time processing, but I would not rely on it alone to determine the order in which game state should be updated.

---

# Key Takeaways

- Timestamps are not guaranteed to uniquely identify event order.
- Multiple events can share the same timestamp.
- Distributed systems experience clock skew.
- Arrival order may differ from event time.
- Kafka preserves arrival order within a partition, not business order.
- Sequence numbers provide deterministic ordering.
- Timestamps should be used for analytics, display, debugging, and event-time processing—not as the primary ordering mechanism.


# Apple Sports System Design Interview
## Part 5 - Missing Events, Replay & Recovery

---

# Missing Event Scenario

Provider generates

```
Sequence 10

↓

Sequence 11

↓

Sequence 12
```

Our system receives

```
10

↓

12
```

The Game State Consumer detects

```
Expected = 11

Received = 12
```

Sequence **11** is missing.

---

# Consumer Behaviour

The consumer maintains

```
lastProcessedSequence
```

Suppose

```
lastProcessedSequence = 10
```

Incoming event

```
Sequence = 12
```

Instead of immediately processing

```
12
```

it buffers the event.

```
Buffer

↓

12
```

Then waits for

```
11
```

for a configurable timeout.

---

# Why Not Wait Forever?

Waiting forever blocks all future updates for that match.

Example

```
10

↓

12 (Buffered)

↓

13 (Buffered)

↓

14 (Buffered)
```

The game stops updating.

For a live sports application this is unacceptable.

---

# Recommended Flow

```
Receive Event

↓

Expected Sequence?

↓

YES

↓

Process Event

↓

NO

↓

Buffer Event

↓

Wait

↓

Missing Event Arrives?

↓

YES

↓

Process Missing Event

↓

Process Buffered Events

↓

NO

↓

Recovery Strategy
```

---

# Recovery Strategies

Several options exist.

---

## Option 1 (Preferred)

Request missing event(s) from provider.

Example

```
GET /matches/123/events?fromSequence=11&toSequence=11
```

Provider returns

```
Sequence 11
```

Consumer processes

```
11

↓

12
```

Advantages

- Complete history preserved
- Correct current state
- Replay remains possible

---

## Option 2

Request all events since the last processed sequence.

Example

```
GET /matches/123/events?fromSequence=11
```

Provider returns

```
11

12

13
```

Our consumer is idempotent.

Already processed events are ignored.

Advantages

- Simpler provider API
- Naturally recovers missing events
- Works well with idempotent consumers

---

## Option 3

Request latest game snapshot.

Example

```
GET /matches/123/current-state
```

Response

```json
{
  "homeScore":105,
  "awayScore":103,
  "period":4,
  "clock":"03:12"
}
```

Current state becomes correct.

---

# Drawback Of Snapshot Recovery

Suppose

Provider generated

```
10

11

12
```

We only received

```
10

12
```

Snapshot recovery fixes

```
GameState

✓ Correct
```

But

GameEvent history becomes

```
10

12
```

Sequence

```
11
```

is permanently missing.

---

# Why Does This Matter?

Several downstream features depend on complete history.

Examples

- Play-by-play
- Analytics
- Historical reports
- Audit
- Debugging
- Rebuilding downstream systems

Snapshot recovery restores the current score.

It does **not** restore historical fidelity.

---

# Current State vs Event History

Current Game State

```
Lakers 105

Warriors 103

Q4

02:35
```

History

```
GOAL

↓

FOUL

↓

TIMEOUT

↓

SCORE

↓

GAME_END
```

These are two different datasets.

Current state answers

```
What is happening now?
```

Event history answers

```
How did we get here?
```

---

# Preferred Recovery Order

```
Missing Sequence

↓

Replay Missing Event

↓

Replay Since Last Sequence

↓

Latest Snapshot

↓

Operational Alert
```

The earlier the recovery succeeds, the more complete the history remains.

---

# Two Types Of Replay

These are often confused.

---

## Provider Replay

Problem

Provider feed is incomplete.

Solution

Ask provider to resend events.

Example

```
GET /matches/123/events?fromSequence=11
```

Purpose

Recover missing provider events.

---

## Kafka Replay

Problem

Our consumer failed after Kafka already stored the event.

Solution

Reset Kafka offset.

Replay retained events.

Purpose

Recover from consumer failures.

Kafka replay **cannot** recover events that never reached Kafka.

---

# Source Of Truth

Failure determines the recovery source.

| Failure | Recovery Source |
|----------|-----------------|
| Consumer crash after Kafka received event | Replay from Kafka offset |
| Provider never delivered event | Request replay from provider |
| Redis lost current state | Reload GameState snapshots from Cassandra |
| Cassandra GameState corrupted | Rebuild from Kafka GameEvent history |

---

# Interview Discussion

One important design question is:

> Does Apple Sports require complete event history, or only the latest game state?

If historical fidelity is required

Preferred recovery is

```
Replay Missing Events
```

If only the current score matters

Latest snapshot recovery may be sufficient.

---

# Key Takeaways

- Never wait forever for missing events.
- Buffer out-of-order events for a configurable timeout.
- Distinguish between provider replay and Kafka replay.
- Provider replay recovers events that never reached Kafka.
- Kafka replay recovers events already stored in Kafka.
- Latest snapshots restore current state but may leave gaps in historical event data.
- Current state and immutable event history are different data products with different correctness requirements.
- Always clarify whether complete event history is a business requirement before designing recovery logic.


# Apple Sports System Design Interview
## Part 6 - Apache Flink, Event Time & Watermarks

---

# Where Does Flink Fit?

Flink is **not** intended to maintain the live game scoreboard.

Instead, Flink is best suited for downstream stream analytics such as:

- Rolling team statistics
- Top scorers
- Fantasy scoring
- Win probability
- Leaderboards
- Last 5-minute metrics
- Historical aggregations

The Game State Consumer and Flink serve different purposes.

---

# Live Game State vs Stream Analytics

## Game State Consumer

Goal

Maintain the latest game state.

Requirements

- Lowest possible latency
- Immediate score updates
- Deterministic ordering
- No intentional delays

Uses

- Kafka
- Sequence numbers
- Small reorder buffer (if needed)

Does NOT use watermarks.

---

## Flink

Goal

Compute aggregations over streams.

Examples

- Goals scored in the last 5 minutes
- Team possession percentage
- Top scorers this quarter
- Rolling player statistics
- Fantasy points

Uses

- Event Time
- Windows
- Watermarks
- Stateful processing

---

# Event Time vs Processing Time

## Event Time

When the event actually occurred.

Example

```
GOAL

Occurred

10:01:05
```

This timestamp comes from the sports provider.

---

## Processing Time

When our system received it.

Example

```
GOAL

Occurred

10:01:05

↓

Received

10:01:09
```

Network delays can make these different.

For analytics, Flink prefers **Event Time**.

---

# What Is A Watermark?

A watermark is a **progress indicator** for event time.

It tells Flink:

> "I believe I've now received all events up to this event timestamp."

It is **not a guarantee**.

It is a heuristic used to determine when it is safe to close event-time windows.

---

# Watermarks Do NOT Determine Ordering

Watermarks answer

```
How long should I wait for late events?
```

They do **not** answer

```
Which event happened first?
```

Business ordering still comes from

```
sequenceNumber
```

---

# Window Example

Suppose we want to compute

```
Goals Scored

Last 5 Minutes
```

Window

```
10:00:00

↓

10:05:00
```

This defines **which events belong to the aggregation**.

It does **not** define when Flink publishes the result.

---

# Allowed Lateness

Suppose we configure

```
Allowed Lateness = 30 seconds
```

Timeline

```
Window End

10:05:00

↓

Wait For Late Events

30 seconds

↓

Watermark Passes

↓

Close Window

↓

Aggregate Results

↓

Publish
```

The **window length** and the **waiting period** are different concepts.

- Window = Events included
- Allowed lateness = How long Flink waits before finalizing

---

# Late Events

Suppose an event arrives after the watermark has closed the window.

Several strategies exist.

---

## Option 1 (Most Common)

Discard the event.

```
Late Event

↓

Too Late

↓

Drop
```

Suitable when slightly stale analytics are acceptable.

---

## Option 2

Update the aggregation.

Example

Initial result

```
LeBron

32 Points
```

Late basket arrives.

Updated result

```
LeBron

34 Points
```

Flink can emit a corrected aggregation.

---

## Option 3

Side Output

```
Late Event

↓

Side Output Stream

↓

Audit

↓

Alert

↓

Offline Processing
```

Useful when data should never be silently lost.

---

# Why Doesn't The Game State Consumer Use Watermarks?

Suppose a user is watching a live game.

Goal scored.

If the scoreboard waits

```
30 seconds
```

before updating

the user experience becomes unacceptable.

The Game State Consumer prioritizes

- Low latency
- Correct ordering
- Immediate updates

It should not intentionally delay processing waiting for late events.

---

# Sequence Numbers Are Still Required

Even when using Flink.

Suppose two events have the same timestamp.

```
GOAL

10:01:05.123

↓

YELLOW CARD

10:01:05.123
```

Which came first?

The timestamp cannot determine this.

The provider should supply

```
sequenceNumber
```

as the authoritative ordering field.

Flink does not replace business ordering.

---

# Flink Responsibilities

Excellent use cases

- Rolling team statistics
- Top scorers
- Fantasy scoring
- Win probability
- Average possession
- Rolling averages
- Historical analytics
- Windowed computations

---

# Game State Consumer Responsibilities

Excellent use cases

- Maintain current score
- Current quarter
- Match status
- Current clock
- Live game state

Optimized for

- Low latency
- Deterministic updates
- Immediate user-facing responses

---

# Mental Model

| Component | Goal | Uses Watermarks? |
|-----------|------|------------------|
| Game State Consumer | Maintain latest score | ❌ No |
| Notification Consumer | Immediate push notifications | ❌ No |
| Analytics Consumer | Rolling aggregations | ✅ Yes |
| Fantasy Scoring | Aggregate player statistics | ✅ Yes |
| Leaderboards | Compute rankings | ✅ Yes |

---

# Interview Answer

> I would not use Flink to maintain the live scoreboard because that path prioritizes minimal latency and deterministic ordering using provider sequence numbers. Instead, I would use Flink downstream for stream analytics such as rolling team statistics, top scorers, leaderboards, and fantasy scoring. Flink's event-time processing, windows, and watermarks allow me to wait briefly for late-arriving events before finalizing aggregations, improving analytical accuracy while keeping latency bounded.

---

# Key Takeaways

- Flink is primarily a stream processing engine for analytics, not live state management.
- Event Time represents when the event occurred; Processing Time represents when the system received it.
- Watermarks indicate progress in event time and determine when event-time windows can be closed.
- Watermarks do **not** determine business ordering.
- Sequence numbers remain the authoritative ordering mechanism.
- Window duration defines which events are aggregated.
- Allowed lateness defines how long Flink waits for delayed events before finalizing results.
- Late events can be dropped, used to update previous results, or routed to a side output stream.
- Separate low-latency operational workloads (Game State Consumer) from analytical workloads (Flink).

# Apple Sports System Design Interview
## Part 7 - Read Path, API Design & Real-Time Client Updates

---

# Read Path

Until now we designed the write path.

```
Sports Provider

↓

Kafka

↓

Consumers

↓

Redis / Cassandra
```

Now we design the read path.

```
Client

↓

REST API

↓

Redis

↓

Response
```

Apple Sports is a read-heavy application.

Example

```
Writes

10,000 events/sec

↓

Reads

500,000 requests/sec
```

The read path must be optimized.

---

# REST APIs

## List Live Games

```
GET /v1/games/live
```

Purpose

Return all currently live games.

Example query parameters

```
GET /v1/games/live?league=NBA

GET /v1/games/live?sport=basketball

GET /v1/games/live?favoriteOnly=true
```

Pagination is generally unnecessary because the number of simultaneously live games is relatively small.

---

## Game Details

```
GET /v1/games/{gameId}
```

Purpose

Return the latest state of a single game.

Future APIs

```
GET /v1/games/{gameId}/events
```

Play-by-play

```
GET /v1/games/{gameId}/stats
```

Player and team statistics

Keep APIs focused.

Do not overload a single endpoint.

---

# API Response

Example

```json
[
  {
    "gameId": "123",
    "homeTeam": "Lakers",
    "awayTeam": "Warriors",
    "homeScore": 105,
    "awayScore": 101,
    "period": 4,
    "clock": "02:31",
    "status": "LIVE"
  }
]
```

Avoid returning

- Player statistics
- Rosters
- Play-by-play

These belong to dedicated APIs.

Keep payloads small.

---

# Serving Path

The Serving API does not read Kafka.

It simply reads the latest game state.

```
Client

↓

Game Query Service

↓

Redis

↓

Response
```

The game state has already been computed by the Game State Consumer.

The serving layer should not recompute anything.

---

# Updated Architecture

```
Sports Provider

↓

Ingestion Service

↓

Kafka

↓

Game State Consumer

↓

Redis Cluster

↓

Game Query Service

↓

REST APIs

↓

Apple Sports App
```

Redis contains the latest game state for every match.

---

# Why Redis?

Redis stores

```
Current Game State
```

Examples

```
Game 123

Lakers 105

Warriors 101

Q4

02:31
```

Benefits

- Sub-millisecond reads
- Low latency
- Perfect for read-heavy workloads

Cassandra remains the durable backing store.

---

# Scaling The Read Path

Suppose

```
2 Million Concurrent Users
```

If users poll every

```
5 seconds
```

The system receives approximately

```
400,000 requests/sec
```

Most requests return identical data.

Polling wastes resources.

---

# Polling

Client repeatedly calls

```
GET /v1/games/{gameId}
```

every few seconds.

Advantages

- Simple
- Easy to implement

Disadvantages

- High request volume
- Unnecessary network traffic
- Updates are delayed until the next poll

---

# Long Polling

Client sends

```
GET /v1/games/{gameId}
```

Server waits until data changes.

Then returns a response.

Client immediately reconnects.

Advantages

- Better than polling

Disadvantages

- Frequent HTTP reconnects
- More server overhead
- Still not ideal for live sports

---

# Server-Sent Events (SSE)

Client establishes one HTTP connection.

Example

```
GET /v1/games/{gameId}/stream
```

Server keeps the connection open.

Whenever the score changes

```
Server

↓

Push Update

↓

Client UI Refresh
```

Example event

```
event: score

data:
{
    "homeScore":104,
    "awayScore":101
}
```

---

# Why SSE?

Apple Sports is primarily

```
Server

↓

Client
```

communication.

Users mostly consume live updates.

Advantages

- Persistent HTTP connection
- Lower overhead than polling
- Automatic browser reconnection
- Simpler than WebSockets
- Excellent for one-way streaming

---

# WebSockets

Provide full duplex communication.

```
Client

⇄

Server
```

Best suited for

- Chat
- Multiplayer games
- Collaborative editing
- Real-time betting
- Interactive fantasy sports

Apple Sports mainly pushes updates.

Bidirectional communication is generally unnecessary.

---

# Recommended Approach

Use

REST

for initial state.

Then

SSE

for live updates.

Workflow

```
User Opens App

↓

GET /v1/games/live

↓

Receive Current State

↓

Open SSE Connection

↓

Receive Incremental Updates
```

This avoids repeated polling.

---

# How Does SSE Receive Updates?

The Game State Consumer updates Redis.

At the same time it publishes an internal update event.

```
Game State Consumer

↓

Update Redis

↓

Publish GameUpdated(matchId)
```

The SSE Service subscribes to these notifications.

Possible mechanisms

- Kafka
- Redis Pub/Sub
- Internal Event Bus

Whenever it receives

```
GameUpdated
```

it pushes updates to connected clients.

```
GameUpdated

↓

SSE Service

↓

Connected Clients
```

Notice

The SSE Service does **not** poll Redis continuously.

It reacts to change notifications.

---

# Complete Read Architecture

```
                    Game State Consumer
                              │
                              ▼
                       Redis Cluster
                              │
                              ├──────────────┐
                              │              │
                              ▼              ▼
                    Game Query Service   SSE Service
                              │              │
                    REST APIs │              │ Live Updates
                              ▼              ▼
                      Apple Sports Clients (iPhone, Apple TV, Live Activities)
```

---

# Why Not Push Directly From Kafka?

Clients should never connect directly to Kafka.

Reasons

- Security
- Protocol mismatch
- Authentication
- Authorization
- Fan-out management
- Client lifecycle management

Kafka remains an internal event backbone.

---

# Interview Answer

> I would expose REST APIs to retrieve the initial state of games, such as `GET /v1/games/live` and `GET /v1/games/{gameId}`. These APIs read from Redis, which maintains the latest game state computed by the Game State Consumer. After the initial load, clients establish a Server-Sent Events connection to receive incremental score updates. The Game State Consumer updates Redis and publishes lightweight internal update events, which the SSE service consumes to push changes to connected clients. This approach minimizes polling, reduces latency, and scales efficiently for millions of users.

---

# Key Takeaways

- Separate write path from read path.
- Redis serves as the low-latency read store.
- Keep REST payloads lightweight.
- Use REST for initial state retrieval.
- Use SSE for real-time server-to-client updates.
- Reserve WebSockets for bidirectional communication.
- The SSE service reacts to internal events rather than polling Redis.
- Kafka is an internal messaging system and is never exposed directly to clients.

# Apple Sports System Design Interview
## Part 8 - Kafka Consumer Failure, Recovery & Rebalancing

---

# Failure Scenario

Suppose the system is operating normally.

```
Sports Provider

↓

Kafka

↓

Game State Consumer

↓

Redis
```

Suddenly

```
Game State Consumer

↓

Crash
```

Meanwhile Kafka continues receiving

```
GOAL

↓

FOUL

↓

TIMEOUT

↓

SCORE

↓

...
```

Question

What happens now?

---

# Step 1 - Failure Detection

Every consumer periodically sends heartbeats to the Kafka Group Coordinator.

```
Consumer

↓

Heartbeat

↓

Group Coordinator
```

If heartbeats stop

```
session.timeout.ms

expires
```

The Group Coordinator assumes the consumer has failed.

It removes the consumer from the consumer group.

---

# Step 2 - Rebalancing

Suppose

```
Consumer A

Partitions

P0 P1

Consumer B

Partitions

P2 P3

Consumer C

Partitions

P4 P5
```

Consumer B crashes.

The Group Coordinator performs a rebalance.

After rebalance

```
Consumer A

P0 P1 P2

Consumer C

P3 P4 P5
```

The remaining consumers continue processing.

---

# Important Distinction

Consumer crash

≠

Broker crash

A consumer failure does **not** trigger leader election.

ISR is **not** involved.

Leader election only occurs if a Kafka broker fails.

---

# Kafka Durability

Events remain safely stored in Kafka.

Kafka persists records to its append-only log.

Example

```
100

101

102

103

104
```

Even if all consumers stop,

the events remain on disk.

No data is lost.

---

# Offset Recovery

Suppose Consumer B processed

```
100

101

102
```

and committed

```
Offset = 102
```

Consumer crashes.

Kafka receives

```
103

104

105
```

After rebalance

Consumer A starts reading

```
103
```

because Kafka remembers the committed offset.

---

# What If Offset Wasn't Committed?

Suppose

Consumer processed

```
100

101

102
```

but only committed

```
101
```

Crash occurs.

After recovery

Consumer starts from

```
102
```

again.

Therefore

```
102
```

is processed twice.

This is expected under

```
At-Least-Once Delivery
```

---

# Why Idempotency Matters

Duplicate processing is possible.

Example

```
HOME +3
```

processed twice

would produce

```
101

↓

104
```

instead of

```
101
```

The Game State Consumer must therefore be idempotent.

Duplicate events should be safely ignored.

---

# Consumer Lag

While the consumer is unavailable

Kafka continues accepting events.

```
Producer

↓

Kafka

↓

Consumer (Stopped)
```

Consumer lag increases.

Definition

```
Consumer Lag

=

Latest Offset

-

Committed Offset
```

After recovery

```
Consumer

↓

Processes backlog

↓

Lag decreases

↓

Eventually

Lag = 0
```

---

# What Do Users Experience?

During rebalance

Consumers briefly pause.

```
Consumer Crash

↓

Rebalance

↓

Partition Assignment

↓

Resume Processing
```

Users may observe

- Slight delay in live score updates
- Increasing consumer lag

They should **not** experience permanent data loss.

Once consumers catch up

Redis reflects the latest game state.

---

# Broker Failure vs Consumer Failure

These are different scenarios.

---

## Consumer Failure

```
Consumer

↓

Crash
```

Recovery

- Group Coordinator detects missing heartbeats
- Rebalance
- Resume from committed offsets

ISR is **not** involved.

---

## Broker Failure

```
Leader Broker

↓

Crash
```

Kafka promotes one of the

```
In-Sync Replicas (ISR)
```

to become the new leader.

Producer and consumers continue communicating with the new leader.

This is where

```
acks=all
```

helps ensure durability.

---

# Production Optimization

Kafka supports

```
Cooperative Sticky Rebalancing
```

Instead of stopping all consumers,

only the necessary partitions move.

Benefits

- Faster recovery
- Reduced downtime
- Smaller pauses
- Better throughput during rebalancing

---

# Updated Failure Flow

```
Game State Consumer

↓

Crash

↓

Heartbeats Stop

↓

Group Coordinator Detects Failure

↓

Consumer Removed

↓

Rebalance

↓

Partitions Reassigned

↓

Resume From Committed Offset

↓

Catch Up

↓

Update Redis

↓

Consumer Lag Returns To Zero
```

---

# Interview Answer

> If a Game State Consumer crashes, the Kafka Group Coordinator detects the missing heartbeats after the configured session timeout and removes it from the consumer group. Kafka then performs a rebalance, assigning the failed consumer's partitions to the remaining consumers. Since Kafka durably stores events on disk, no data is lost. The new consumer resumes processing from the last committed offset. If the previous consumer crashed after processing an event but before committing its offset, that event may be processed again, which is why the Game State Consumer must be idempotent. During the rebalance, consumer lag temporarily increases and users may experience a brief delay in live score updates, but once the consumer catches up, Redis is updated with the latest state and normal operation resumes.

---

# Key Takeaways

- Consumer crashes are handled by the Kafka Group Coordinator.
- Missing heartbeats trigger consumer removal and rebalancing.
- Consumer failure does **not** involve ISR or leader election.
- Kafka retains events on disk until they expire based on retention policies.
- Consumers restart from the last committed offset.
- At-least-once delivery can result in duplicate processing.
- Idempotent consumers prevent duplicate state updates.
- Consumer lag is an important production metric during failures.
- Broker failures and consumer failures are separate failure scenarios with different recovery mechanisms.	

# Apple Sports System Design Interview
## Part 9 - Cassandra Data Modeling (GameEvent Table)

---

# Cassandra Modeling Principle

Unlike relational databases, Cassandra tables are designed around **query patterns**, not entities.

Golden Rule

> Model your tables based on the queries you need to support.

Do **not** start by designing a `Game` entity.

Instead ask:

```
What queries am I optimizing?
```

---

# Primary Query

```
GET /v1/games/{gameId}/events
```

Requirements

- Retrieve all events for a game
- Return events in business order
- Low latency
- Efficient partition scan

---

# Recommended Schema

```sql
CREATE TABLE GameEvent (
    gameId text,
    sequenceNumber bigint,
    eventId uuid,
    eventTimestamp timestamp,
    eventType text,
    teamId text,
    playerId text,
    payload text,
    PRIMARY KEY ((gameId), sequenceNumber)
);
```

---

# Primary Key

```
PRIMARY KEY

((gameId), sequenceNumber)
```

---

## Partition Key

```
gameId
```

Purpose

All events belonging to the same game are stored in the same partition.

Advantages

- Efficient retrieval of an entire game's play-by-play
- Single partition read
- Matches the primary query pattern

Example

```
Game 123

↓

Event 1

↓

Event 2

↓

Event 3

↓

...
```

---

## Clustering Column

```
sequenceNumber
```

Purpose

Store events in provider-defined business order.

Cassandra automatically returns rows ordered by the clustering column.

Example

```
Sequence 1

↓

Sequence 2

↓

Sequence 3

↓

Sequence 4
```

No application-side sorting required.

---

# Why Not Use eventTimestamp?

Earlier we established that timestamps are not always sufficient for ordering.

Problems

- Multiple events may share the same timestamp.
- Timestamp precision may be limited.
- Distributed systems experience clock skew.
- Arrival order may differ from event time.

Example

```
GOAL

10:01:05.123

↓

YELLOW CARD

10:01:05.123
```

Which happened first?

The timestamp cannot answer that.

A monotonically increasing

```
sequenceNumber
```

provides deterministic ordering.

---

# Recommended Columns

| Column | Purpose |
|---------|----------|
| gameId | Partition key |
| sequenceNumber | Business ordering |
| eventId | Unique identifier |
| eventTimestamp | Display, analytics, auditing |
| eventType | GOAL, FOUL, TIMEOUT, etc. |
| teamId | Team associated with the event |
| playerId | Player associated with the event |
| payload | Flexible event-specific details |

---

# Why Use a Payload?

Different event types contain different attributes.

Example

GOAL

```json
{
  "scorer":"23",
  "assist":"11"
}
```

FOUL

```json
{
  "player":"15",
  "foulType":"Technical"
}
```

TIMEOUT

```json
{
  "team":"Lakers"
}
```

A flexible payload avoids creating many nullable columns.

---

# Wide Partition Discussion

Suppose a cricket match generates

```
50,000 events
```

One partition may become very large.

For most sports

- NBA
- NFL
- Soccer
- NHL

a single partition per game is typically acceptable because event counts are relatively modest.

For extremely high-volume sports or telemetry-style workloads, introduce bucketing.

---

# Bucketing Strategies

Example

Partition by

```
(gameId, period)
```

or

```
(gameId, inning)
```

or

```
(gameId, day)
```

Example Primary Key

```sql
PRIMARY KEY ((gameId, period), sequenceNumber)
```

Benefits

- Smaller partitions
- Better compaction
- More predictable read performance

Trade-off

Fetching the entire game now requires reading multiple partitions.

Choose bucketing only when partition size becomes a real concern.

---

# Example Rows

| gameId | sequenceNumber | eventType | playerId |
|---------|---------------:|-----------|----------|
| G123 | 1 | GAME_START | NULL |
| G123 | 2 | TIP_OFF | NULL |
| G123 | 3 | SCORE | P23 |
| G123 | 4 | FOUL | P15 |
| G123 | 5 | TIMEOUT | NULL |

Rows are naturally returned in sequence order.

---

# Query Example

```
GET /v1/games/G123/events
```

Cassandra performs

- Partition lookup using `gameId`
- Sequential scan ordered by `sequenceNumber`

No sorting is required in the application.

---

# Trade-offs

### Advantages

- Query-first design
- Efficient partition reads
- Natural event ordering
- Good fit for append-only event storage
- Scales horizontally across nodes

### Trade-offs

- Not optimized for querying by player or team
- Denormalization is expected in Cassandra
- Bucketing may be required for exceptionally large partitions

Additional tables should be created for different access patterns rather than trying to satisfy every query with a single table.

---

# Interview Answer

> Since my primary query is retrieving the play-by-play for a single game, I would partition the table by `gameId`. I would use `sequenceNumber` as the clustering column because it represents the provider's authoritative business ordering and allows Cassandra to store and return events in the correct order without application-side sorting. I would also store `eventId`, `eventTimestamp`, `eventType`, `teamId`, `playerId`, and a flexible payload for event-specific details. If a particular sport generated extremely large numbers of events, I would introduce bucketing—for example by period or inning—to keep partition sizes manageable while accepting the trade-off of reading multiple partitions for a full game history.

---

# Key Takeaways

- Cassandra models tables around query patterns.
- Use `gameId` as the partition key for play-by-play queries.
- Use `sequenceNumber` as the clustering column for deterministic ordering.
- Prefer `sequenceNumber` over `eventTimestamp` for business ordering.
- Store timestamps for display, analytics, and auditing.
- Use a flexible payload because different event types have different attributes.
- Consider bucketing only when partitions become too large.
- Create separate denormalized tables for different query patterns rather than overloading one table.

# Apple Sports System Design Interview
## Part 9 - Cassandra Data Modeling (GameEvent Table)

---

# Cassandra Modeling Principle

Unlike relational databases, Cassandra tables are designed around **query patterns**, not entities.

Golden Rule

> Model your tables based on the queries you need to support.

Do **not** start by designing a `Game` entity.

Instead ask:

```
What queries am I optimizing?
```

---

# Primary Query

```
GET /v1/games/{gameId}/events
```

Requirements

- Retrieve all events for a game
- Return events in business order
- Low latency
- Efficient partition scan

---

# Recommended Schema

```sql
CREATE TABLE GameEvent (
    gameId text,
    sequenceNumber bigint,
    eventId uuid,
    eventTimestamp timestamp,
    eventType text,
    teamId text,
    playerId text,
    payload text,
    PRIMARY KEY ((gameId), sequenceNumber)
);
```

---

# Primary Key

```
PRIMARY KEY

((gameId), sequenceNumber)
```

---

## Partition Key

```
gameId
```

Purpose

All events belonging to the same game are stored in the same partition.

Advantages

- Efficient retrieval of an entire game's play-by-play
- Single partition read
- Matches the primary query pattern

Example

```
Game 123

↓

Event 1

↓

Event 2

↓

Event 3

↓

...
```

---

## Clustering Column

```
sequenceNumber
```

Purpose

Store events in provider-defined business order.

Cassandra automatically returns rows ordered by the clustering column.

Example

```
Sequence 1

↓

Sequence 2

↓

Sequence 3

↓

Sequence 4
```

No application-side sorting required.

---

# Why Not Use eventTimestamp?

Earlier we established that timestamps are not always sufficient for ordering.

Problems

- Multiple events may share the same timestamp.
- Timestamp precision may be limited.
- Distributed systems experience clock skew.
- Arrival order may differ from event time.

Example

```
GOAL

10:01:05.123

↓

YELLOW CARD

10:01:05.123
```

Which happened first?

The timestamp cannot answer that.

A monotonically increasing

```
sequenceNumber
```

provides deterministic ordering.

---

# Recommended Columns

| Column | Purpose |
|---------|----------|
| gameId | Partition key |
| sequenceNumber | Business ordering |
| eventId | Unique identifier |
| eventTimestamp | Display, analytics, auditing |
| eventType | GOAL, FOUL, TIMEOUT, etc. |
| teamId | Team associated with the event |
| playerId | Player associated with the event |
| payload | Flexible event-specific details |

---

# Why Use a Payload?

Different event types contain different attributes.

Example

GOAL

```json
{
  "scorer":"23",
  "assist":"11"
}
```

FOUL

```json
{
  "player":"15",
  "foulType":"Technical"
}
```

TIMEOUT

```json
{
  "team":"Lakers"
}
```

A flexible payload avoids creating many nullable columns.

---

# Wide Partition Discussion

Suppose a cricket match generates

```
50,000 events
```

One partition may become very large.

For most sports

- NBA
- NFL
- Soccer
- NHL

a single partition per game is typically acceptable because event counts are relatively modest.

For extremely high-volume sports or telemetry-style workloads, introduce bucketing.

---

# Bucketing Strategies

Example

Partition by

```
(gameId, period)
```

or

```
(gameId, inning)
```

or

```
(gameId, day)
```

Example Primary Key

```sql
PRIMARY KEY ((gameId, period), sequenceNumber)
```

Benefits

- Smaller partitions
- Better compaction
- More predictable read performance

Trade-off

Fetching the entire game now requires reading multiple partitions.

Choose bucketing only when partition size becomes a real concern.

---

# Example Rows

| gameId | sequenceNumber | eventType | playerId |
|---------|---------------:|-----------|----------|
| G123 | 1 | GAME_START | NULL |
| G123 | 2 | TIP_OFF | NULL |
| G123 | 3 | SCORE | P23 |
| G123 | 4 | FOUL | P15 |
| G123 | 5 | TIMEOUT | NULL |

Rows are naturally returned in sequence order.

---

# Query Example

```
GET /v1/games/G123/events
```

Cassandra performs

- Partition lookup using `gameId`
- Sequential scan ordered by `sequenceNumber`

No sorting is required in the application.

---

# Trade-offs

### Advantages

- Query-first design
- Efficient partition reads
- Natural event ordering
- Good fit for append-only event storage
- Scales horizontally across nodes

### Trade-offs

- Not optimized for querying by player or team
- Denormalization is expected in Cassandra
- Bucketing may be required for exceptionally large partitions

Additional tables should be created for different access patterns rather than trying to satisfy every query with a single table.

---

# Interview Answer

> Since my primary query is retrieving the play-by-play for a single game, I would partition the table by `gameId`. I would use `sequenceNumber` as the clustering column because it represents the provider's authoritative business ordering and allows Cassandra to store and return events in the correct order without application-side sorting. I would also store `eventId`, `eventTimestamp`, `eventType`, `teamId`, `playerId`, and a flexible payload for event-specific details. If a particular sport generated extremely large numbers of events, I would introduce bucketing—for example by period or inning—to keep partition sizes manageable while accepting the trade-off of reading multiple partitions for a full game history.

---

# Key Takeaways

- Cassandra models tables around query patterns.
- Use `gameId` as the partition key for play-by-play queries.
- Use `sequenceNumber` as the clustering column for deterministic ordering.
- Prefer `sequenceNumber` over `eventTimestamp` for business ordering.
- Store timestamps for display, analytics, and auditing.
- Use a flexible payload because different event types have different attributes.
- Consider bucketing only when partitions become too large.
- Create separate denormalized tables for different query patterns rather than overloading one table.

# Apple Sports System Design Interview
## Part 10 - Redis Data Modeling, TTL & Cache Strategy

---

# Why Redis?

Redis is the serving layer for the latest game state.

Read path

```
Client

↓

Game Query Service

↓

Redis

↓

Response
```

Redis provides

- Sub-millisecond reads
- Low latency
- High throughput
- Excellent fit for read-heavy workloads

---

# Redis Data Structure

Recommended

```
Redis Hash
```

Key

```
game:{matchId}
```

Example

```
game:123
```

---

# Why Redis Hash?

Each game contains multiple independently changing fields.

Example

```
homeScore

awayScore

period

clock

status
```

A Hash allows updating individual fields without rewriting the entire object.

Example

```
HSET game:123 homeScore 105
```

Only one field changes.

No serialization required.

---

# Why Not Store A JSON String?

Suppose Redis stored

```json
{
  "homeScore":104,
  "awayScore":101,
  "period":4,
  "clock":"02:31"
}
```

When the score changes

Application must

- Read JSON
- Deserialize
- Modify object
- Serialize
- Write the entire object

This creates unnecessary overhead for high-frequency updates.

---

# Example Redis Operations

Store

```redis
HSET game:123 \
homeScore 105 \
awayScore 101 \
period 4 \
clock "02:31" \
status LIVE \
homeTeamId LAL \
awayTeamId GSW
```

Read

```redis
HGETALL game:123
```

Update

```redis
HSET game:123 homeScore 107
```

---

# Recommended Fields

Key

```
game:{matchId}
```

Type

```
Hash
```

Fields

```
homeScore

awayScore

period

clock

status

homeTeamId

awayTeamId

lastSequenceNumber

lastUpdated
```

---

# Why Store lastSequenceNumber?

Example

```
game:123

lastSequenceNumber = 381
```

Benefits

- Helps detect duplicate or stale events
- Useful for debugging
- Can support idempotency checks
- Makes recovery easier after consumer restarts

---

# Why Store lastUpdated?

Example

```
lastUpdated

2026-08-03T18:22:11Z
```

Useful for

- Monitoring
- Cache freshness
- Operational debugging
- Health checks

---

# Team IDs vs Team Names

Store

```
homeTeamId

awayTeamId
```

instead of

```
Lakers

Warriors
```

Reason

Game state changes frequently.

Team metadata

- Team names
- Logos
- Branding

changes infrequently and belongs in a separate reference cache or service.

---

# Cache Consistency

Our architecture

```
Sports Provider

↓

Kafka

↓

Game State Consumer

↓

Redis
```

Redis is updated immediately after every processed event.

Therefore

Clients almost always read the latest game state.

This is **not** a traditional Cache-Aside pattern.

The write pipeline keeps Redis synchronized.

---

# Should Redis Use TTL?

Question

Should

```
game:123
```

expire?

---

## While The Game Is LIVE

Recommendation

```
No TTL
```

Reason

Game duration is unpredictable.

Example

Basketball game enters overtime.

If the key expires

the live scoreboard disappears.

Avoid expiration during active games.

---

## When The Game Ends

Suppose

```
status = FINAL
```

Now set

```
TTL = 24 hours
```

Workflow

```
LIVE

↓

No TTL

↓

FINAL

↓

Expire After 24 Hours

↓

Automatic Removal
```

Benefits

- Users can still view recently completed games
- Avoids unnecessary Cassandra reads
- Redis memory is automatically reclaimed

---

# Why Not Delete Immediately?

```
DEL game:123
```

would force all post-game requests to hit Cassandra.

Keeping recently completed games in Redis improves performance.

---

# Why Not Keep Forever?

Eventually Redis fills with years of completed games.

TTL provides automatic lifecycle management.

---

# Redis Failure

Suppose Redis crashes.

Recovery

```
Redis Restart

↓

Read GameState Snapshots

↓

Warm Redis

↓

Resume Traffic
```

Current state is rebuilt from

```
Cassandra GameState
```

Redis is **not** the source of truth.

---

# Redis vs Cassandra

Redis

Stores

```
Current Game State
```

Purpose

- Fast serving
- Low latency
- High throughput

---

Cassandra

Stores

```
Durable GameState Snapshot
```

Purpose

- Recovery
- Durability
- Long-term storage

---

# Cache Strategy

Traditional Cache-Aside

```
Read

↓

Cache Miss

↓

Database

↓

Populate Cache
```

This is **not** our architecture.

---

Our Design

```
Sports Provider

↓

Kafka

↓

Game State Consumer

↓

Update Redis

↓

Persist Cassandra

↓

Clients Read Redis
```

Redis is updated as part of the write pipeline.

This behaves more like a **write-through serving cache** than a traditional cache-aside cache.

---

# Complete Read Path

```
Sports Provider

↓

Kafka

↓

Game State Consumer

↓

Redis Hash (Current Game State)

↓

Game Query Service

↓

REST API / SSE

↓

Apple Sports Clients
```

---

# Interview Answer

> I would model each game's current state as a Redis Hash using a key such as `game:{matchId}`. A Hash allows me to update individual fields like `homeScore`, `awayScore`, `clock`, or `status` without rewriting the entire object. I would also store metadata such as `lastSequenceNumber` and `lastUpdated`. While a game is live, I would not configure a TTL because game durations are unpredictable. Once the game reaches a terminal state, I would set a TTL—such as 24 hours—to keep recently completed games available while automatically reclaiming memory later. Redis serves as the low-latency serving layer, while Cassandra remains the durable source used to rebuild Redis after failures.

---

# Key Takeaways

- Use a Redis Hash for each game.
- Store game state under `game:{matchId}`.
- Update individual fields using `HSET`.
- Prefer team IDs over team names in the game-state cache.
- Store `lastSequenceNumber` and `lastUpdated` for operational purposes.
- Do not use TTL while a game is live.
- Apply a TTL after the game reaches a final state.
- Redis is updated as part of the write path, minimizing stale reads.
- Redis is the serving layer; Cassandra is the durable recovery store.
- This architecture resembles a write-through serving cache rather than a traditional cache-aside design.

# Apple Sports System Design Interview
## Part 11 - SSE Architecture & Real-Time Client Updates

---

# Do We Need A Registry Of SSE Servers?

Question

Do we need a distributed registry to know

```
Game 123

↓

SSE Server 2
```

Answer

**No.**

A distributed registry introduces unnecessary complexity.

Instead, every SSE server maintains its own local subscriptions.

---

# Mental Model

An SSE server serves

```
Many Clients
```

not

```
One Game
```

Example

```
                Load Balancer
                      │
        ┌─────────────┼─────────────┐
        ▼             ▼             ▼
    SSE Server 1  SSE Server 2  SSE Server 3
```

Clients connect

```
Server 1

User A → Game123

User B → Game555

User C → Game123

User D → Game888
```

```
Server 2

User E → Game123

User F → Game999

User G → Game555
```

```
Server 3

User H → Game777

User I → Game123
```

Notice

Game123 has users connected to multiple SSE servers.

This is completely normal.

---

# Local Subscription Map

Each SSE server keeps an in-memory structure.

Example

```java
Map<GameId, Set<ClientConnection>>
```

Example

```
Game123

↓

Client A

↓

Client C

↓

Client H
```

This map exists only on that server.

No distributed registry is required.

---

# How Updates Flow

The Game State Consumer processes a new event.

```
Sports Provider

↓

Kafka

↓

Game State Consumer
```

The consumer

- Updates Redis
- Persists Cassandra
- Publishes

```
GameUpdated(game123)
```

---

# Every SSE Server Receives The Event

Each SSE server subscribes to the internal update stream.

Possible mechanisms

- Kafka
- Redis Pub/Sub
- Internal Event Bus

Suppose

```
GameUpdated(game123)
```

arrives.

Server 1

```
subscriptions.get(game123)

↓

Clients Found

↓

Push Update
```

Server 2

```
subscriptions.get(game123)

↓

Clients Found

↓

Push Update
```

Server 3

```
subscriptions.get(game123)

↓

No Clients

↓

Ignore
```

The lookup is an in-memory hash map lookup (`O(1)`), making it very inexpensive.

---

# What Does The SSE Server Actually Store?

The SSE server does **not** store game state.

It only stores

```
GameId

↓

Connected Clients
```

The latest game state remains in Redis.

---

# Client Connection Lifecycle

When a user opens a game

```
GET /v1/games/123/stream
```

The SSE server

```
Accept Connection

↓

subscriptions.get(123).add(client)
```

When the user disconnects

```
subscriptions.get(123).remove(client)
```

Everything is maintained in memory.

---

# Complete End-To-End Flow

## Step 1

Client requests current state.

```
GET /v1/games/123
```

Response comes from

```
Redis
```

The user immediately sees the current score.

---

## Step 2

Client opens a persistent SSE connection.

```
GET /v1/games/123/stream
```

The connection remains open.

---

## Step 3

A new game event occurs.

```
Sports Provider

↓

GOAL
```

---

## Step 4

Internal processing.

```
Sports Provider

↓

Ingestion Service

↓

Kafka

↓

Game State Consumer
```

The Game State Consumer

- Updates Redis
- Persists Cassandra
- Publishes

```
GameUpdated(game123)
```

---

## Step 5

SSE Server receives

```
GameUpdated(game123)
```

Looks up

```
subscriptions.get(game123)
```

Finds

```
Client A

Client B

Client C
```

Pushes an SSE message over the existing HTTP connection.

Example

```json
event: score

data:
{
    "homeScore":106,
    "awayScore":101,
    "clock":"02:15"
}
```

The SSE server is **not** sending Kafka messages to clients.

It is sending **Server-Sent Events (SSE)** over HTTP.

---

## Step 6

The Apple Sports application receives the SSE event.

The client

- Parses the message
- Updates its in-memory model
- Refreshes the UI

No additional REST request is required.

---

# Delta Updates

The SSE server does not need to send the entire game state every time.

Instead of

```json
{
    "homeScore":106,
    "awayScore":101,
    "period":4,
    "clock":"02:15",
    "status":"LIVE",
    "homeTeam":"Lakers",
    "awayTeam":"Warriors"
}
```

it can send only the changed fields.

Example

```json
{
    "gameId":"123",
    "eventType":"SCORE_UPDATE",
    "homeScore":106,
    "awayScore":101,
    "clock":"02:15"
}
```

Benefits

- Smaller payloads
- Lower bandwidth
- Faster updates
- Better scalability

The client merges the update into its local state.

---

# Why Not Poll Redis?

The SSE server should **not** poll Redis continuously.

Instead

```
Game State Consumer

↓

GameUpdated Event

↓

SSE Server

↓

Push To Clients
```

This event-driven approach avoids unnecessary Redis reads and provides lower latency.

---

# Complete Architecture

```
                 User Opens App
                        │
                        ▼
             GET /v1/games/{gameId}
                        │
                        ▼
                     Redis
                        │
                        ▼
             Current Game State
                        │
                        ▼
          Open SSE Connection
                        │
────────────────────────┼────────────────────────
                        │
                 Sports Provider
                        │
                        ▼
                 Ingestion Service
                        │
                        ▼
                      Kafka
                        │
                        ▼
             Game State Consumer
                │              │
                │              ▼
                │         Cassandra
                ▼
           Update Redis
                │
                ▼
      Publish GameUpdated Event
                │
                ▼
          Multiple SSE Servers
                │
                ▼
     Push Updates To Interested Clients
                │
                ▼
        Apple Sports App Updates UI
```

---

# Interview Answer

> Each SSE server maintains a local in-memory mapping of `gameId` to connected client sessions. All SSE servers subscribe to the internal `GameUpdated` event stream. When an update arrives, each server checks whether it has clients interested in that game and pushes an SSE message only to those clients. The client application listens on the persistent SSE connection, updates its local model, and refreshes the UI without making another REST request. This avoids polling, keeps latency low, and eliminates the need for a distributed registry of SSE servers.

---

# Key Takeaways

- One SSE server serves many clients across many games.
- A single game may have connected users on multiple SSE servers.
- No distributed registry is required.
- Each server maintains an in-memory `Map<GameId, Set<ClientConnection>>`.
- All SSE servers subscribe to internal game update events.
- The SSE server pushes updates only to interested clients.
- Clients consume SSE events, not Kafka messages.
- Clients update their local state and refresh the UI without additional REST calls.
- Send delta updates whenever possible to reduce bandwidth.
- The architecture is fully event-driven from the sports provider to the user's device.

# Apple Sports System Design Interview
## Part 12 - Notification Service & User Favorites

---

# New Requirement

Users can follow their favorite teams.

Example

```
User123

↓

Lakers

Bills

Yankees
```

When an important event occurs

- Team scores
- Lead changes
- Game starts
- Game ends
- Overtime starts

the user should receive a push notification within a few seconds.

---

# Where Should Notifications Be Generated?

There are two possible designs.

---

## Option 1

Notification Service consumes the raw game-events topic.

```
                 Kafka
                    │
        ┌───────────┴────────────┐
        ▼                        ▼
Game State Consumer     Notification Service
```

Advantages

- Independent consumer group
- Notification failures do not impact score processing
- Better service isolation

Disadvantages

- Notification Service must understand game rules
- It must determine
    - Lead changes
    - Game end
    - Overtime
- May need to read current state from Redis

---

## Option 2 (Preferred)

Game State Consumer publishes higher-level domain events.

```
Sports Provider

↓

Kafka (Raw Events)

↓

Game State Consumer

↓

Redis

↓

Cassandra

↓

Game State Events Topic

↓

Notification Service
```

Advantages

- Business logic exists in one place
- Downstream services remain simple
- No duplicated game-state logic

---

# Raw Events vs Domain Events

## Raw Provider Event

```json
{
    "eventType":"SHOT_MADE",
    "team":"Lakers",
    "points":2
}
```

This is simply a fact.

---

## Domain Event

After updating the score

```
Lakers 100

Warriors 99
```

The Game State Consumer detects

```
Lead Changed
```

It publishes

```json
{
    "eventType":"LEAD_CHANGED",
    "gameId":"123",
    "leadingTeam":"Lakers",
    "score":"100-99"
}
```

The Notification Service simply reacts.

```
LEAD_CHANGED

↓

Send Push Notification
```

---

# More Domain Event Examples

## Game Finished

Provider

```
Clock = 00:00
```

Game State Consumer determines

```
Quarter = 4

Clock = 00:00

↓

GAME_FINAL
```

Publishes

```json
{
    "eventType":"GAME_FINAL",
    "winner":"Lakers",
    "score":"115-110"
}
```

---

## Overtime Started

Provider

```
Clock = 00:00

Score = 101-101
```

Game State Consumer determines

```
OVERTIME_STARTED
```

Publishes

```json
{
    "eventType":"OVERTIME_STARTED",
    "gameId":"123"
}
```

---

## Team Reaches 100 Points

Provider

```
SHOT_MADE
```

Game State Consumer updates score

```
98

↓

100
```

Publishes

```json
{
    "eventType":"TEAM_REACHED_100_POINTS",
    "team":"Lakers"
}
```

---

# Why Publish Domain Events?

Without domain events

Every downstream service must implement

- Score calculation
- Lead detection
- Overtime rules
- Game completion rules

This duplicates business logic.

Instead

```
Raw Events

↓

Game State Consumer

↓

Business Events

↓

Notification Service

↓

Live Activities

↓

Analytics

↓

Other Consumers
```

One service owns the game rules.

Everyone else simply reacts.

---

# SSE Mapping

SSE servers maintain

```
GameId

↓

Connected Clients
```

This mapping is

- In memory
- Temporary
- Rebuilt automatically when clients reconnect

Example

```java
Map<GameId, Set<ClientConnection>>
```

No database is required.

---

# Notification Mapping

Notifications require

```
Team

↓

Followers
```

Example

```
Lakers

↓

User1

↓

User8

↓

User25
```

This is persistent business data.

It belongs in a database.

---

# PostgreSQL vs Cassandra

## Option 1 - PostgreSQL (Recommended Initial Choice)

Store

```sql
UserFavorites

userId

teamId

notificationPreference
```

Advantages

- ACID transactions
- Simple schema
- Easy CRUD
- Strong consistency
- Excellent indexing
- Operational simplicity

Typical query

```sql
SELECT *
FROM UserFavorites
WHERE userId = ?
```

Excellent fit.

---

## Notification Query

When the Lakers score

```
Who follows Lakers?
```

```sql
SELECT userId
FROM UserFavorites
WHERE teamId='Lakers'
```

This works well with proper indexing at moderate scale.

---

## Option 2 - Cassandra

If Apple Sports grows to hundreds of millions of users and notification fan-out becomes extremely large, create a denormalized Cassandra table.

Example

```sql
CREATE TABLE TeamFollowers (
    teamId text,
    userId uuid,
    notificationsEnabled boolean,
    PRIMARY KEY ((teamId), userId)
);
```

Optimized for

```
Team

↓

Followers
```

---

# Large Partition Problem

Suppose

```
Lakers

↓

15 Million Followers
```

One Cassandra partition becomes too large.

Introduce bucketing.

Example

```sql
PRIMARY KEY ((teamId, bucketId), userId)
```

Where

```
bucketId = hash(userId) % 100
```

Now

```
Lakers Bucket 0

↓

150,000 users

Lakers Bucket 1

↓

150,000 users

...

Lakers Bucket 99

↓

150,000 users
```

Notification workers can process buckets in parallel.

---

# Recommendation

For an initial production system

Use

```
PostgreSQL
```

because

- Favorites are relational business data
- Simple CRUD
- Strong consistency
- Easier operations

If notification fan-out becomes a bottleneck

Introduce

```
Cassandra
```

for a denormalized

```
Team → Followers
```

lookup table.

Do not introduce Cassandra unless the scale requires it.

---

# Complete Notification Architecture

```
Sports Provider
        │
        ▼
Kafka (Raw Events)
        │
        ▼
Game State Consumer
        │
        ├────────► Redis
        │
        ├────────► Cassandra
        │
        ▼
Game State Events Topic
        │
        ▼
Notification Service
        │
        ▼
PostgreSQL (User Favorites)
        │
        ▼
Apple Push Notification Service (APNs)
        │
        ▼
Apple Sports App
```

---

# Interview Answer

> I would keep the Game State Consumer focused on maintaining the authoritative game state. As it processes raw provider events, it would publish higher-level domain events such as `LEAD_CHANGED`, `GAME_FINAL`, or `OVERTIME_STARTED`. The Notification Service would subscribe to those domain events rather than raw provider events, avoiding duplication of game logic. User favorites and notification preferences are relational business data, so I would initially store them in PostgreSQL. If notification fan-out grows to hundreds of millions of users, I would introduce a denormalized Cassandra table optimized for `teamId → followers` lookups while keeping PostgreSQL as the system of record.

---

# Key Takeaways

- Separate raw provider events from business/domain events.
- The Game State Consumer should own game-state business logic.
- Downstream services should react to domain events instead of recalculating state.
- SSE connection mappings are temporary and maintained in memory.
- User favorites are persistent business data.
- PostgreSQL is an excellent initial choice for user preferences.
- Cassandra becomes valuable only when notification fan-out requires massive horizontal scaling.
- Choose the simplest solution that satisfies current scale and evolve when justified.


# Apple Sports Backend - Final System Design

## High-Level Architecture
# Apple Sports Backend Architecture (Final)

```text
                                              APPLE SPORTS BACKEND

┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                        Sports Data Providers                                                               │
│                                                                                                                            │
│  Opta • Stats Perform • Genius Sports • SportRadar                                                                         │
│                                                                                                                            │
│  Raw Events: GOAL, SHOT_MADE, ASSIST, REBOUND, FOUL, TIMEOUT, SUBSTITUTION, PERIOD_END...                                 │
└────────────────────────────────────────────┬───────────────────────────────────────────────────────────────────────────────┘
                                             │
                                             │ POST /v1/games/{gameId}/events
                                             ▼
┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                          Ingestion Service                                                                 │
│----------------------------------------------------------------------------------------------------------------------------│
│ Responsibilities                                                                                                           │
│ • Authenticate provider                                                                                                    │
│ • Validate payload                                                                                                         │
│ • Normalize provider format → Canonical Event                                                                              │
│ • Return HTTP 202 Accepted                                                                                                 │
│ • Publish Raw Event to Kafka                                                                                                │
└────────────────────────────────────────────┬───────────────────────────────────────────────────────────────────────────────┘
                                             │
                                             ▼
┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                          Kafka : game-events                                                               │
│----------------------------------------------------------------------------------------------------------------------------│
│ Raw Immutable Provider Events                                                                                              │
│ Partition Key = gameId                                                                                                     │
│ Ordering Guaranteed Per Game                                                                                                │
└───────────────┬──────────────────────────────┬──────────────────────────────┬──────────────────────────────┐
                │                              │                              │                              │
                ▼                              ▼                              ▼                              ▼

┌─────────────────────────────┐    ┌────────────────────────────┐    ┌─────────────────────────────┐    ┌──────────────────────────────┐
│ Game State Consumer         │    │ Event Storage Service      │    │ Statistics Consumer         │    │ Real-Time Analytics Engine  │
│                             │    │                            │    │                             │    │ (Apache Flink / Streams)    │
├─────────────────────────────┤    ├────────────────────────────┤    ├─────────────────────────────┤    ├──────────────────────────────┤
│ Responsibilities            │    │ Responsibilities           │    │ Responsibilities            │    │ Responsibilities            │
│ • Validate ordering         │    │ • Persist raw events       │    │ • Match Statistics          │    │ • Rolling Aggregations      │
│ • Idempotency               │    │ • Immutable history        │    │ • Player Statistics         │    │ • Leaderboards             │
│ • Compute score             │    │ • Replay support           │    │ • Materialized Views        │    │ • Win Probability          │
│ • Compute game state        │    │ • Auditing                 │    │ • Persist Statistics        │    │ • Fantasy Points           │
│ • Update Redis              │    │                            │    │                             │    │ • Player Rankings          │
│ • Persist Game Snapshot     │    │                            │    │                             │    │                            │
│ • Publish Business Events   │    │                            │    │                             │    │                            │
└──────────────┬──────────────┘    └──────────────┬─────────────┘    └──────────────┬──────────────┘    └──────────────┬───────────────┘
               │                                  │                                 │                                  │
               ▼                                  ▼                                 ▼                                  ▼

      ┌────────────────────┐          ┌──────────────────────────┐        ┌──────────────────────────┐       ┌────────────────────────────┐
      │ Redis Cluster      │          │ Cassandra                │        │ Cassandra               │       │ Redis Sorted Sets         │
      ├────────────────────┤          ├──────────────────────────┤        ├─────────────────────────┤       ├────────────────────────────┤
      │ game:{gameId}      │          │ match_events             │        │ match_statistics        │       │ leaderboard:points        │
      │ Current Game State │          │ Raw Event History        │        │ player_statistics       │       │ leaderboard:assists       │
      │ Score              │          │                          │        │                         │       │ leaderboard:rebounds      │
      │ Clock              │          └──────────────────────────┘        └─────────────────────────┘       │ leaderboard:fantasy       │
      │ Quarter            │                                                                                 └────────────────────────────┘
      │ Possession         │
      │ Status             │
      └──────────┬─────────┘
                 │
                 ▼
┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                        Kafka : game-state-events                                                           │
├────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ Business (Domain) Events                                                                                                   │
│                                                                                                                            │
│ • SCORE_UPDATED                                                                                                            │
│ • LEAD_CHANGED                                                                                                             │
│ • PERIOD_STARTED                                                                                                           │
│ • PERIOD_ENDED                                                                                                             │
│ • GAME_FINAL                                                                                                               │
│ • OVERTIME_STARTED                                                                                                         │
└───────────────┬──────────────────────────────┬──────────────────────────────┬──────────────────────────────────────────────┘
                │                              │                              │
                ▼                              ▼                              ▼

┌─────────────────────────────┐    ┌─────────────────────────────┐    ┌────────────────────────────────┐
│ SSE Service                 │    │ Notification Service        │    │ Future Consumers              │
│                             │    │                             │    │                                │
├─────────────────────────────┤    ├─────────────────────────────┤    ├────────────────────────────────┤
│ Responsibilities            │    │ Responsibilities            │    │ • Live Activities             │
│ • Subscribe state events    │    │ • Subscribe state events    │    │ • Widgets                     │
│ • Maintain SSE sessions     │    │ • Read Followers Cache      │    │ • ML Models                   │
│ • Push live updates         │    │ • Send APNs                 │    │ • Search                      │
│                             │    │                             │    │ • Additional Services         │
└─────────────────────────────┘    └──────────────┬──────────────┘    └────────────────────────────────┘
                                                   │
                                                   ▼

                                      ┌──────────────────────────────────┐
                                      │ PostgreSQL                      │
                                      ├──────────────────────────────────┤
                                      │ Users                           │
                                      │ Favorite Teams                  │
                                      │ Notification Preferences        │
                                      └──────────────────────────────────┘


═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════════

                                                  CLIENT FACING LAYER

                                                     Apple Sports App

                                                            │
                                 ┌──────────────────────────┴──────────────────────────┐
                                 │                                                     │
                                 ▼                                                     ▼

                              REST APIs                                           SSE Stream

                                 │                                                     ▲
                                 ▼                                                     │

┌────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                              API / Query Service                                                           │
├────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│ Responsibilities                                                                                                           │
│ • Stateless REST APIs                                                                                                      │
│ • Horizontally Scalable                                                                                                    │
│ • Read Redis for current game state                                                                                        │
│ • Read Cassandra for history & statistics                                                                                  │
│ • Read Redis Sorted Sets for leaderboards                                                                                  │
│                                                                                                                            │
│ APIs                                                                                                                       │
│ GET /v1/games/live                                                                                                         │
│ GET /v1/games/{gameId}                    → Redis (Current Game State)                                                     │
│ GET /v1/games/{gameId}/events             → Cassandra (match_events)                                                       │
│ GET /v1/games/{gameId}/stats              → Cassandra (match_statistics)                                                   │
│ GET /v1/players/{playerId}/stats          → Cassandra (player_statistics)                                                  │
│ GET /v1/leaderboards/points               → Redis Sorted Sets                                                              │
│ GET /v1/leaderboards/fantasy              → Redis Sorted Sets                                                              │
│ GET /v1/teams/{teamId}/schedule                                                                              │
│ GET /v1/standings                                                                                                          │
│ GET /v1/games/{gameId}/stream             → SSE Endpoint                                                                   │
└───────────────────────────────────────┬───────────────────────────────┬────────────────────────────────────────────────────┘
                                        │                               │
                                        ▼                               ▼

                                 Redis Cluster                   Cassandra Cluster

                           Current Game State              Events + Statistics
```
---

# Responsibilities of Each Service

## 1. Ingestion Service

Consumes requests from external sports providers.

Responsibilities

- Authenticate provider
- Validate payload
- Normalize payload into internal canonical format
- Return **202 Accepted**
- Publish raw event to **game-events**

---

## 2. Game State Consumer

Consumes **game-events**.

Responsibilities

- Validate event ordering
- Handle duplicate events (idempotency)
- Compute latest game score
- Compute current game state
- Update Redis
- Persist latest game snapshot to Cassandra
- Publish domain events to **game-state-events** *(only after state has been successfully persisted)*

This service owns the authoritative game state.

---

## 3. Storage Service

Consumes **game-events**.

Responsibilities

- Store every raw provider event
- Maintain immutable event history
- Support replay
- Support auditing/debugging

Acts as the **System of Record** for all provider events.

It does **not** compute business logic.

---

## 4. Statistics Consumer

Consumes **game-events**.

Responsibilities

- Compute match statistics
- Compute player statistics
- Build materialized views
- Persist statistics to Cassandra

This service owns all statistical data and operates independently of game-state processing.

---

## 5. Flink / Kafka Streams

Consumes **game-events**.

Responsibilities

- Rolling aggregations
- Leaderboards
- Fantasy scoring
- Win probability
- Player rankings
- Real-time analytics

Analytics are completely independent from the critical game-state path.

---

## 6. API / Query Service

Client-facing REST service.

Reads

- Redis → Live game state
- Cassandra → Historical events
- Cassandra → Statistics
- Redis Sorted Sets → Leaderboards

Example APIs

```http
GET /v1/games/live

GET /v1/games/{gameId}

GET /v1/games/{gameId}/events

GET /v1/games/{gameId}/stats

GET /v1/players/{playerId}/stats

GET /v1/leaderboards/points

GET /v1/leaderboards/fantasy

GET /v1/teams/{teamId}/schedule

GET /v1/standings
```

---

## 7. SSE Service

Consumes **game-state-events**.

Responsibilities

- Maintain persistent HTTP connections
- Track connected clients
- Push live score updates
- Push game state updates

The SSE service **does not calculate game state**.

It simply forwards domain events.

---

## 8. Notification Service

Consumes **game-state-events**.

Responsibilities

- Read user favorites
- Read notification preferences
- Send Apple Push Notifications (APNs)

Example notifications

- Team scored
- Lead changed
- Game started
- Overtime
- Final score

---

# Database Responsibilities

## Redis

Stores

- Current game state
- Current score
- Clock
- Period
- Match status

Optimized for low-latency reads.

---

## Cassandra

Stores

### GameEvent

Immutable raw event history.

Used for

- Replay
- Auditing
- Historical event queries

---

### GameSnapshot

Latest computed game state.

Used for

- Fast recovery
- Redis rebuild
- Historical game state

---

### Statistics

Stores

- Match statistics
- Player statistics

---

## Redis Sorted Sets

Stores

- Leaderboards
- Fantasy rankings
- Player rankings

Optimized for fast ranking queries.

---

## PostgreSQL

Stores relational user data.

Example

- Users
- Favorite teams
- Notification preferences

---

# Kafka Topics

## game-events

Raw provider events.

Examples

- GOAL
- SHOT_MADE
- FOUL
- TIMEOUT
- SUBSTITUTION

Consumers

- Game State Consumer
- Storage Service
- Statistics Consumer
- Flink

---

## game-state-events

Business/domain events.

Examples

- SCORE_UPDATED
- LEAD_CHANGED
- GAME_FINAL
- PERIOD_STARTED
- OVERTIME_STARTED

Consumers

- SSE Service
- Notification Service
- Future consumers

---

# End-to-End Flow

1. Sports provider sends an event.
2. Ingestion Service authenticates, validates and normalizes it.
3. Event is published to **game-events**.
4. Storage Service stores the immutable raw event.
5. Game State Consumer validates ordering and idempotency.
6. Game State Consumer computes the latest game state.
7. Redis is updated with the latest state.
8. Latest game snapshot is persisted to Cassandra.
9. Domain event is published to **game-state-events**.
10. SSE Service pushes live updates to connected clients.
11. Notification Service sends push notifications based on user preferences.
12. Statistics Consumer updates match and player statistics.
13. Flink continuously computes leaderboards, fantasy points, rankings and analytics.

---

# Why This Architecture?

- Clear separation of responsibilities.
- Event-driven and loosely coupled.
- Redis serves live requests with very low latency.
- Cassandra provides durable historical storage.
- Immutable event history enables replay and auditing.
- Game snapshots enable fast recovery without replaying the entire event history.
- PostgreSQL stores relational user preferences.
- Kafka enables independent scaling of consumers.
- SSE delivers real-time updates without polling.
- Notification processing is isolated from the critical game-state path.
- Analytics are completely independent of score computation.

This architecture is simple enough to explain in an interview while still demonstrating production-ready design, scalability, and clear service ownership.

---
# Webhooks vs Polling vs Streaming

## How Does the Sports Provider Send Data?

For this design, I assume the sports data provider integrates with our system using an **HTTP webhook**.

The provider sends an HTTP `POST` request whenever a game event occurs.

Example

```http
POST /v1/matches/{matchId}/events
Content-Type: application/json

{
    "eventId":"evt-123",
    "matchId":"match-456",
    "eventType":"SHOT_MADE",
    "team":"Lakers",
    "points":3,
    "timestamp":"2026-08-03T20:15:22Z"
}
```

Our **Ingestion Service** receives the request, performs lightweight validation, publishes the event to Kafka, and immediately returns **202 Accepted**.

---

# Why Return 202 Accepted?

The provider should not wait while we:

- Compute game state
- Update Redis
- Persist Cassandra
- Compute analytics
- Send notifications
- Push SSE updates

Instead

```
Sports Provider

↓

POST /events

↓

Ingestion Service

↓

Validate

↓

Publish to Kafka

↓

HTTP 202 Accepted
```

Everything after Kafka happens asynchronously.

Kafka becomes the asynchronous boundary for the system.

---

# Why Webhooks?

Sports events only occur when something happens.

Examples

- Goal
- Shot made
- Foul
- Timeout
- Period end
- Game end

Instead of repeatedly asking the provider whether anything changed, the provider pushes updates to us only when events occur.

This provides

- Lower latency
- Lower network traffic
- Better scalability
- Simpler integration

---

# Polling

Instead of the provider calling us, we repeatedly call the provider.

Example

```http
GET /provider/matches/{matchId}/events
```

Every second

```
Our Service

↓

GET /events

↓

No Events

↓

GET /events

↓

No Events

↓

GET /events

↓

Goal!
```

Problems

- Lots of unnecessary requests
- Higher latency
- Wasted bandwidth
- Not ideal for real-time sports

---

# Streaming

Some providers expose

- Kafka
- WebSocket
- gRPC Streaming

Example

```
Sports Provider

↓

Kafka Topic

↓

Our Consumer
```

or

```
Sports Provider

↓

WebSocket

↓

Our Consumer
```

This provides even lower latency than webhooks.

If the provider offered a streaming interface, only the ingestion layer would change.

The rest of the architecture would remain exactly the same.

---

# Comparison

| Integration Method | Who Initiates? | Communication | Best For |
|-------------------|----------------|--------------|----------|
| Polling | Our system | Repeated HTTP GET | Simple systems, not ideal for live sports |
| Webhook | Sports Provider | HTTP POST | Real-time event delivery |
| Kafka / Streaming | Sports Provider | Persistent stream | Highest throughput and lowest latency |

---

# REST vs Webhook

A webhook is **not a different protocol**.

It is simply a REST/HTTP endpoint that another system calls automatically when an event occurs.

Our endpoint

```http
POST /v1/matches/{matchId}/events
```

is a normal HTTP REST endpoint.

Because the provider calls it automatically whenever an event occurs, it is acting as a **webhook receiver**.

---

# Synchronous vs Asynchronous

The HTTP request itself is synchronous.

```
Sports Provider

↓

POST /events

↓

Ingestion Service

↓

HTTP 202 Accepted
```

Everything after publishing to Kafka is asynchronous.

```
Kafka

↓

Game State Consumer

↓

Storage Service

↓

Analytics

↓

Notification Service

↓

SSE Service
```

The provider receives a response in a few milliseconds while the rest of the platform continues processing independently.

---

# Why This Fits Our Architecture

```
Sports Provider

↓

Webhook (HTTP POST)

↓

Ingestion Service

↓

Kafka (game-events)

↓

Game State Consumer

↓

Redis

↓

Kafka (game-state-events)

↓

SSE / Notifications / Future Consumers
```

The webhook is simply the entry point into our event-driven architecture.

---

# Interview Answer

> I assume the sports provider integrates with us using an HTTP webhook. The provider POSTs live game events to the Ingestion Service whenever something happens in a match. The Ingestion Service performs lightweight authentication, validation, and normalization, publishes the event to Kafka, and immediately returns **202 Accepted**. Kafka acts as the asynchronous boundary, allowing downstream services such as Game State, Storage, Analytics, Notifications, and SSE to process the event independently. If the provider instead exposed Kafka or another streaming interface, I would replace only the ingestion layer while keeping the rest of the architecture unchanged.

> The game-events topic contains raw immutable provider events that represent facts occurring during the game. The Game State Consumer processes these events, applies business rules, updates the current game state, and publishes higher-level domain events such as SCORE_UPDATED, LEAD_CHANGED, or GAME_FINAL to a separate game-state-events topic. This keeps downstream services like Notifications and SSE simple because they consume meaningful business events instead of implementing sports-specific logic themselves.

# Apple Sports System Design
# Raw Events vs Business (Domain) Events

In an event-driven architecture, it is a good practice to separate **raw provider events** from **business (domain) events**.

This keeps the system loosely coupled and prevents downstream services from implementing business logic.

---

# Two Kafka Topics

```
game-events
```

Contains **raw provider events** exactly as they occur.

Examples:

- GOAL
- SHOT_MADE
- SHOT_ATTEMPT
- FOUL
- TIMEOUT
- SUBSTITUTION
- FREE_THROW
- PERIOD_END

---

```
game-state-events
```

Contains **business/domain events** produced after applying business rules.

Examples:

- SCORE_UPDATED
- LEAD_CHANGED
- PERIOD_STARTED
- PERIOD_ENDED
- GAME_FINAL
- OVERTIME_STARTED

These events represent meaningful state changes rather than low-level provider actions.

---

# High-Level Flow

```
Sports Provider

↓

Ingestion Service

↓

Kafka : game-events

↓

Game State Consumer

↓

Apply Business Rules

↓

Update Redis

↓

Persist Snapshot

↓

Kafka : game-state-events

↓

SSE Service

Notification Service

Future Consumers
```

---

# Example 1 - Basketball Score

## Raw Provider Event

Provider sends:

```json
{
    "matchId":"NBA123",
    "player":"LeBron James",
    "team":"LAL",
    "type":"SHOT_MADE",
    "points":2
}
```

This is simply a fact that occurred.

It does **not** contain the latest score.

---

## Game State Consumer

The Game State Consumer processes the event.

Responsibilities:

- Validate ordering
- Check idempotency
- Compute latest score
- Update game state

Current score:

```
102 - 100
```

After processing:

```
104 - 100
```

---

## Business Event

The Game State Consumer publishes:

```json
{
    "type":"SCORE_UPDATED",
    "matchId":"NBA123",
    "homeScore":104,
    "awayScore":100
}
```

Notice that the provider never sent this event.

It was derived from business logic.

---

# Example 2 - Lead Change

Current score:

```
102 - 103
```

Raw provider event:

```json
{
    "type":"SHOT_MADE",
    "team":"LAL",
    "points":2
}
```

New score:

```
104 - 103
```

The Lakers now take the lead.

The Game State Consumer publishes:

```json
{
    "type":"LEAD_CHANGED",
    "matchId":"NBA123",
    "newLeader":"LAL"
}
```

Again, this is a business event computed by the application.

---

# Example 3 - End Of Quarter

Raw provider events:

```
CLOCK = 00:00

↓

PERIOD_END
```

Business event:

```json
{
    "type":"PERIOD_ENDED",
    "period":3
}
```

---

# Example 4 - Game Finished

Raw provider event:

```
FINAL_WHISTLE
```

Business event:

```json
{
    "type":"GAME_FINAL",
    "winner":"LAL",
    "finalScore":"110-104"
}
```

---

# Why Separate The Two?

Suppose the Notification Service consumed raw provider events.

It would receive:

```
SHOT_ATTEMPT

↓

SHOT_MADE

↓

FREE_THROW

↓

FOUL

↓

TIMEOUT

↓

SUBSTITUTION
```

The Notification Service would need to understand:

- Basketball rules
- Scoring rules
- Lead changes
- Quarter transitions

This tightly couples the Notification Service to sports logic.

---

Instead, it consumes business events:

```
SCORE_UPDATED

↓

LEAD_CHANGED

↓

GAME_FINAL

↓

OVERTIME_STARTED
```

Now it simply reacts to meaningful events.

No sports-specific calculations are required.

---

# SSE Service

Similarly, the SSE Service should not calculate scores.

Instead of consuming:

```
SHOT_MADE
```

it consumes:

```json
{
    "type":"SCORE_UPDATED",
    "homeScore":104,
    "awayScore":100
}
```

Its responsibility is simply to push updates to connected clients.

---

# Benefits

## Separation of Concerns

Provider events represent facts.

Business events represent business decisions.

---

## Simpler Consumers

Downstream services consume meaningful state changes instead of implementing game rules.

---

## Loose Coupling

If provider payloads change, only the Game State Consumer needs to change.

Notification Service, SSE Service, Widgets, and other consumers remain unaffected.

---

## Easier Extensibility

New consumers can subscribe to business events without understanding provider-specific event formats.

Examples:

- Live Activities
- Widgets
- Analytics
- Machine Learning
- Recommendation Systems

---

# Raw Events vs Business Events

| Raw Provider Events | Business (Domain) Events |
|---------------------|--------------------------|
| GOAL | SCORE_UPDATED |
| SHOT_MADE | LEAD_CHANGED |
| SHOT_ATTEMPT | PERIOD_STARTED |
| FREE_THROW | PERIOD_ENDED |
| FOUL | GAME_FINAL |
| TIMEOUT | OVERTIME_STARTED |
| SUBSTITUTION | MATCH_DELAYED |

---

# Interview Questions

### Q: Why have two Kafka topics?

> The `game-events` topic stores immutable raw provider events that describe what happened during a game. The Game State Consumer processes these events, applies business rules, updates the current game state, and publishes higher-level domain events to the `game-state-events` topic. Downstream services consume these business events instead of implementing sports-specific logic.

---

### Q: Why shouldn't Notification Service consume raw events?

> Notification Service should focus on sending notifications, not interpreting sports rules. By consuming business events such as `SCORE_UPDATED` or `GAME_FINAL`, it remains simple, loosely coupled, and independent of provider-specific event formats.

---

### Q: Who owns the business logic?

> The Game State Consumer owns the business logic. It validates events, ensures idempotency, computes the latest game state, updates Redis and Cassandra, and publishes business events for downstream consumers.

---

# Key Takeaways

- `game-events` contains raw immutable provider events.
- `game-state-events` contains higher-level business events derived from those raw events.
- The Game State Consumer is responsible for transforming raw events into business events.
- Downstream services consume business events, keeping them simple and loosely coupled.
- This separation improves maintainability, extensibility, and scalability in an event-driven architecture.


# CompletableFuture in the Game State Consumer

One question that often comes up during system design interviews is:

> **Should we use `CompletableFuture` inside the Game State Consumer to improve performance?**

The answer is:

> **Yes, but only for truly independent operations.**

---

# What Should Remain Sequential?

The Game State Consumer owns the authoritative game state.

Its core business logic must execute in order.

```text
Receive Event
        ↓
Validate Ordering
        ↓
Validate Idempotency
        ↓
Compute Latest Game State
```

These steps are dependent on each other and **must remain sequential**.

---

# What Can Be Parallelized?

Once the latest game state has been computed, some I/O operations become independent.

For example:

```text
Update Redis

Persist Snapshot to Cassandra
```

These two operations do not depend on each other.

Therefore they can execute concurrently.

---

# Recommended Flow

```text
Receive Event
        ↓
Validate Ordering
        ↓
Validate Idempotency
        ↓
Compute Latest Game State
        ↓
 ┌───────────────┬────────────────┐
 │               │                │
 ▼               ▼                │
Update Redis   Persist Snapshot   │
 │               │                │
 └─────── Wait For Both ──────────┘
                 ↓
Publish SCORE_UPDATED Event
```

---

# Why Publish Last?

The domain event should only be published **after** Redis and Cassandra have been successfully updated.

Otherwise:

```text
Publish SCORE_UPDATED
        ↓
Client Immediately Reads Redis
        ↓
Redis Still Contains Old Score
```

Clients would observe inconsistent state.

Publishing after persistence ensures downstream consumers only see committed state.

---

# Sample Implementation

```java
public void process(GameEvent event) {

    GameState latestState = computeLatestState(event);

    CompletableFuture<Void> redisFuture =
            CompletableFuture.runAsync(() ->
                    redisRepository.update(latestState));

    CompletableFuture<Void> cassandraFuture =
            CompletableFuture.runAsync(() ->
                    cassandraRepository.saveSnapshot(latestState));

    CompletableFuture
            .allOf(redisFuture, cassandraFuture)
            .join();

    kafkaProducer.publish(
            new ScoreUpdatedEvent(latestState));
}
```

---

# Where CompletableFuture Makes Sense

Good use cases include independent I/O operations.

Examples:

```text
Update Redis

Persist Cassandra Snapshot

Call Multiple Independent Services

Fetch Data From Multiple Services

Send Multiple Notifications
```

---

## Notification Service Example

```text
Goal Scored
      │
      ├── Send APNs
      ├── Send Email
      ├── Send SMS
      └── Update Analytics
```

Each task is independent and can execute concurrently.

```java
CompletableFuture<Void> push =
        CompletableFuture.runAsync(this::sendPushNotification);

CompletableFuture<Void> email =
        CompletableFuture.runAsync(this::sendEmail);

CompletableFuture<Void> sms =
        CompletableFuture.runAsync(this::sendSms);

CompletableFuture<Void> analytics =
        CompletableFuture.runAsync(this::updateAnalytics);

CompletableFuture
        .allOf(push, email, sms, analytics)
        .join();
```

---

# Query Service Example

Instead of making sequential service calls:

```java
GameState game = gameService.get(gameId);
PlayerStats stats = statsService.get(gameId);
Standings standings = standingsService.get();
```

Use parallel requests:

```java
CompletableFuture<GameState> gameFuture =
        CompletableFuture.supplyAsync(() -> gameService.get(gameId));

CompletableFuture<PlayerStats> statsFuture =
        CompletableFuture.supplyAsync(() -> statsService.get(gameId));

CompletableFuture<Standings> standingsFuture =
        CompletableFuture.supplyAsync(() -> standingsService.get());

CompletableFuture
        .allOf(gameFuture, statsFuture, standingsFuture)
        .join();

GameState game = gameFuture.join();
PlayerStats stats = statsFuture.join();
Standings standings = standingsFuture.join();
```

This reduces overall response latency because all service calls execute concurrently.

---

# Where NOT to Use CompletableFuture

Do **not** parallelize operations that depend on each other.

For example:

```text
Validate Ordering
        ↓
Validate Idempotency
        ↓
Compute Game State
```

These steps must execute sequentially to preserve correctness.

---

# Interview Sound Bite

> Use `CompletableFuture` only for independent work. In the Game State Consumer, ordering validation, idempotency checks, and game-state computation must remain sequential. After the new state has been computed, independent I/O operations such as updating Redis and persisting a Cassandra snapshot can execute concurrently. Only after both complete should the domain event be published, ensuring downstream consumers observe a consistent and durable state.


```text
Sports Provider
      ↓
Ingestion Service
      ↓
Kafka (game-events)
      ↓
Game State Consumer
      ↓
Redis + Cassandra
      ↓
Kafka (game-state-events)
      ↓
SSE
      ↓
Apple Sports App
```


# Apple Sports System Design - Multi-Region Architecture

## Problem Statement

Apple Sports is expanding globally and now serves users in:

* North America
* Europe
* Asia-Pacific

Goals:

* Low latency for users worldwide
* High availability
* Regional fault isolation
* Fast disaster recovery
* Continue serving users even if one region fails

---

# Clarifying Questions

Before designing a multi-region system, I would clarify:

* Should users always connect to the nearest region?
* Are sports providers regional or global?
* What is the acceptable latency between regions?
* What are the RTO (Recovery Time Objective) and RPO (Recovery Point Objective)?
* Should every region continue operating independently if another region fails?

For this design, I assume:

* Users connect to their nearest region.
* Sports providers send events to the US region.
* A global latency of less than one second is acceptable.
* Every region should continue operating independently.

---

# High-Level Architecture

```text
                     Global DNS / Global Load Balancer
                                |
        ---------------------------------------------------
        |                     |                          |
        ▼                     ▼                          ▼
     US-East              Europe                 Asia-Pacific
        |                     |                          |
        |                     |                          |
   API Gateway          API Gateway              API Gateway
        |                     |                          |
   Ingestion API        API Service              API Service
        |                     |                          |
   US Kafka Cluster   Europe Kafka Cluster   Asia Kafka Cluster
        |                     |                          |
 MirrorMaker 2  ------------> | <------------ MirrorMaker 2
        |                     |                          |
        ▼                     ▼                          ▼
 Game State Consumer   Game State Consumer    Game State Consumer
        |                     |                          |
        ▼                     ▼                          ▼
 Redis Cluster        Redis Cluster         Redis Cluster
        |                     |                          |
        ▼                     ▼                          ▼
 SSE Servers          SSE Servers           SSE Servers
 Notification         Notification          Notification
        |                     |                          |
        ▼                     ▼                          ▼
 Cassandra           Cassandra             Cassandra
```

---

# Why Multiple Regions?

Keeping traffic local reduces latency.

Instead of every European user calling the US region:

```text
Europe User

↓

Europe API

↓

Europe Redis

↓

Europe SSE
```

Users receive responses from nearby infrastructure.

---

# Regional Components

Each region owns its own:

* API Service
* Kafka Cluster
* Game State Consumer
* Redis Cluster
* SSE Servers
* Notification Service
* Cassandra Cluster

This allows each region to scale and recover independently.

---

# Kafka Architecture

## Should there be one global Kafka cluster?

No.

Each region should have its own Kafka cluster.

Example:

```text
US Kafka

Europe Kafka

Asia Kafka
```

Benefits:

* Regional independence
* Lower latency
* Better fault isolation
* Easier operations

---

# Cross-Region Event Replication

Sports providers send events only to the US region.

```text
Sports Provider

↓

US Ingestion Service

↓

US Kafka (game-events)

↓

MirrorMaker 2

↓

Europe Kafka

↓

Asia Kafka
```

MirrorMaker 2 replicates Kafka topics between regional Kafka clusters.

---

# Why Replicate Kafka Events Instead of Redis?

Redis contains **derived state**.

Kafka contains the **source of truth**.

If we replicate Redis globally:

* Replication lag
* Cache consistency issues
* Write conflicts
* More operational complexity

Instead:

Each region independently computes the game state from the replicated event stream.

```text
Europe Kafka

↓

Game State Consumer

↓

Europe Redis
```

This is much simpler and more scalable.

---

# Regional Game State Computation

Every region independently performs:

```text
Consume game-events

↓

Validate ordering

↓

Check idempotency

↓

Compute new game state

↓

Update Redis

↓

Persist GameState Snapshot

↓

Publish game-state-events
```

Because every region consumes the same ordered event stream, every region computes the same final game state.

---

# Redis

Each region has its own Redis Cluster.

```text
US Redis

Europe Redis

Asia Redis
```

Purpose:

* Serve live scores with very low latency
* Keep traffic local
* Avoid cross-region cache synchronization

---

# Why Not Replicate Redis?

We replicate immutable events rather than cache.

Redis is rebuilt locally by replaying Kafka events.

Advantages:

* Simpler architecture
* No distributed cache consistency
* Independent regional recovery

---

# Cassandra

Each region has its own Cassandra cluster.

Stores:

* Raw Game Events
* Latest GameState Snapshot

Cross-region replication provides:

* Disaster recovery
* Historical durability
* Regional independence

---

# SSE

Each region runs its own SSE servers.

```text
Europe Users

↓

Europe SSE

↓

Europe Redis
```

Users never stream scores from another continent.

---

# Notification Service

Each region has its own Notification Service.

```text
Europe Notification Service

↓

APNs

↓

European Users
```

Notifications remain local.

---

# Regional Failure

Suppose:

```text
US-East

↓

Crash
```

Recovery:

* Users reconnect.
* Global Load Balancer routes requests to a healthy region if appropriate.
* Kafka events already exist in replicated Kafka clusters.
* Regional Game State Consumers continue processing.
* Redis can be restored from local GameState snapshots if necessary.

---

# Benefits

* Lower latency
* Independent scaling
* Regional fault isolation
* Easier disaster recovery
* Local Redis
* Local SSE
* Local Notifications

---

# Trade-Off

Replicating Kafka introduces small cross-region propagation delay.

Example:

```text
US Event

↓

MirrorMaker

↓

Europe Kafka

↓

Europe Users
```

A delay of approximately 100–200 ms is acceptable since our end-to-end requirement is less than one second.

---

# Interview Summary

If asked how to support multiple regions, I would answer:

> "I would deploy independent stacks in each region, including API Services, Kafka, Redis, Cassandra, SSE, and Notification Services. Sports providers publish events to the US Kafka cluster, and Kafka MirrorMaker 2 replicates the immutable event stream to regional Kafka clusters. Each region independently computes game state, updates its local Redis cache, and serves users locally. I prefer replicating events rather than Redis because Kafka is the source of truth while Redis is a derived cache, which avoids the complexity of cross-region cache synchronization."

---

# Key Interview Takeaways

* Keep user traffic local.
* Deploy one Kafka cluster per region.
* Replicate Kafka events using MirrorMaker 2.
* Do **not** replicate Redis globally.
* Redis is derived state.
* Kafka is the source of truth.
* Each region independently computes game state.
* Each region serves its own users using local Redis, SSE, and Notification Services.
* Replicate the source of truth, derive the cache locally.


> I'd debug this systematically by tracing the event through each stage of the pipeline to identify where latency is introduced. First, I'd verify whether the provider is sending events on time by comparing the provider's event timestamp with the ingestion timestamp. If provider latency is normal, I'd inspect the Ingestion Service metrics, including request rate, P99 latency, and error rate. Next, I'd check Kafka consumer lag to determine whether consumers are keeping up with incoming events. If lag is increasing, I'd investigate the Game State Consumer by looking at CPU utilization, processing latency, and exception rates. Since Redis is on the critical path for serving live scores, I'd then examine Redis read/write latency, memory usage, evictions, and replication lag. Finally, I'd inspect the SSE layer by checking active connections, disconnect rates, and end-to-endevent delivery latency. This approach isolates the stage introducing the delay rather than guessing at individual components.

> In this system, I primarily use Redis replicas for high availability rather than read scaling. Live scores require the freshest possible data, so I'd prefer the API Service to read from the primary. The replicas provide automatic failover, support maintenance operations with minimal downtime, and act as a recovery target if the primary fails. I still monitor replication lag because it determines how up-to-date the failover replica will be." 

# P50, P95 and P99 Latency (Apple System Design Notes)

## What is Latency?

Latency is the time taken to process a request.

Example:

```http
GET /v1/matches/{matchId}/livescore
```

Response time:

```text
12 ms
```

That is the latency for a single request.

In production, millions of requests are processed, and each request can have a different response time.

---

# Example

Suppose the API receives **100 requests**.

Response times are:

```text
95 requests → 10 ms

3 requests → 50 ms

1 request → 200 ms

1 request → 1000 ms
```

---

# P50 (Median Latency)

P50 means:

> **50% of requests completed in this time or less.**

For the above example:

```text
P50 = 10 ms
```

Half of all requests completed within 10 ms.

P50 represents the **typical user experience**.

---

# P95 Latency

P95 means:

> **95% of requests completed in this time or less.**

In our example:

```text
95 requests

↓

10 ms
```

Therefore:

```text
P95 = 10 ms
```

Only the slowest 5% of requests took longer.

P95 is commonly used for Service Level Objectives (SLOs).

---

# P99 Latency

P99 means:

> **99% of requests completed in this time or less.**

Continuing the example:

```text
95 requests → 10 ms

3 requests → 50 ms

1 request → 200 ms
```

The first 99 requests complete within **200 ms**.

Therefore:

```text
P99 = 200 ms
```

The remaining 1% of requests took longer (1000 ms in this example).

P99 captures **tail latency**, which has the biggest impact on user experience.

---

# Why Not Use Average Latency?

Let's calculate the average.

```text
95 × 10 = 950

3 × 50 = 150

1 × 200 = 200

1 × 1000 = 1000

Total = 2300 ms

Average = 23 ms
```

Average latency is only **23 ms**.

However, one user waited:

```text
1000 ms
```

The average hides this poor user experience.

---

# Why Monitor P99?

Suppose Apple Sports users complain:

> "Sometimes live scores take too long to update."

Metrics show:

```text
Average = 23 ms
```

Looks healthy.

However:

```text
P99 = 1000 ms
```

Now we know that a small percentage of users are experiencing significant delays.

P99 helps identify these tail-latency issues.

---

# Visual Representation

```text
Latency Distribution

10 ms    **************************************************

50 ms    ***

200 ms   *

1000 ms  *
```

Most requests are very fast.

A small number of requests are extremely slow.

P99 captures these slow requests.

---

# Example in Apple Sports

Suppose the API metrics are:

```text
P50 = 8 ms

P95 = 15 ms

P99 = 120 ms
```

Interpretation:

* 50% of requests complete within **8 ms**
* 95% complete within **15 ms**
* 99% complete within **120 ms**
* Only the slowest 1% take longer than 120 ms

---

# Where Should We Monitor P99?

## API Service

* Request latency
* Response latency

---

## Redis

* Read latency
* Write latency

---

## Kafka

* Produce latency
* Consumer processing latency

---

## SSE Service

* Event delivery latency

---

## End-to-End Pipeline

Measure the total time from:

```text
Provider

↓

Ingestion Service

↓

Kafka

↓

Game State Consumer

↓

Redis

↓

SSE

↓

User receives updated score
```

Monitoring end-to-end P99 latency provides the best measure of the user experience.

---

# Interview Answer

**Question: Why do you monitor P99 instead of average latency?**

**Answer:**

> Average latency can hide the experience of users who encounter unusually slow requests. P99 measures the latency experienced by nearly all users and helps identify tail-latency issues caused by resource contention, garbage collection, network delays, or overloaded downstream services. Since live sports updates are latency-sensitive, P99 is a much better indicator of user experience than the average latency.

---

# Key Takeaways

| Metric  | Meaning                                   | Use Case                                     |
| ------- | ----------------------------------------- | -------------------------------------------- |
| **P50** | 50% of requests complete within this time | Typical user experience                      |
| **P95** | 95% of requests complete within this time | Service Level Objectives (SLOs)              |
| **P99** | 99% of requests complete within this time | Detecting tail latency and production issues |

---

# Interview Tip

Whenever discussing production monitoring, mention **P99 latency**.

For example:

* P99 API latency
* P99 Redis read latency
* P99 Kafka processing latency
* P99 end-to-end event delivery latency

Mentioning P99 demonstrates that you are thinking about the **worst-case user experience**, not just the average performance, which is an important mindset for senior distributed systems engineers.



# Apple Sports System Design - Technology Trade-offs (Interview Q&A)

## 1. Why Redis? Why not read live scores directly from Cassandra?

**Answer**

Redis stores the current game state entirely in memory, providing sub-millisecond read latency. During a live sporting event, millions of users may continuously request the latest score, making Redis an ideal choice for serving high-volume, low-latency reads.

Cassandra, while extremely fast for writes, is optimized for durable storage of historical data rather than serving frequently changing live state.

Redis and Cassandra serve different purposes:

* **Redis** → Current game state (live score, clock, period, status)
* **Cassandra** → Durable historical storage (Game Events and GameState snapshots)

This separation allows Redis to optimize reads while Cassandra optimizes writes.

---

## 2. Why Cassandra? Why not PostgreSQL?

**Answer**

The Game Events table is an append-only event store that receives a very high volume of writes.

Cassandra is a good fit because it provides:

* High write throughput
* Low write latency
* Horizontal scalability
* Partition-based data distribution
* Query-driven schema design

The system does not require complex joins or strong ACID transactions for event storage, making Cassandra a better fit than a relational database.

Additionally, immutable event data maps naturally to Cassandra's strengths.

---

## 3. Why PostgreSQL for User Favorites?

**Answer**

User favorites are relational data.

Example:

```text
User
   ↓
Favorite Teams
```

Operations include:

* Creating favorites
* Removing favorites
* Querying favorite teams
* Enforcing referential integrity

The expected scale (millions of users) is well within PostgreSQL's capabilities.

PostgreSQL provides:

* ACID transactions
* Relational modeling
* Indexes
* Constraints
* Easy querying

A relational database is a better fit than Cassandra for this use case.

---

## 4. Why Two Kafka Topics?

```text
game-events

game-state-events
```

**Answer**

The two topics have different responsibilities.

### game-events

Contains raw provider events.

Examples:

* SHOT_MADE
* GOAL
* FOUL
* TIMEOUT

These events are immutable.

---

### game-state-events

Contains business domain events generated after applying business logic.

Examples:

* SCORE_UPDATED
* GAME_STARTED
* GAME_ENDED

Downstream consumers such as SSE and Notification Services consume these business events instead of raw provider events.

Benefits:

* Separation of concerns
* Easier downstream processing
* Independent scaling
* Better decoupling

---

## 5. Why Store GameState Snapshots?

We already have GameEvent history.

Why keep GameState?

**Answer**

Snapshots significantly reduce recovery time.

If Redis crashes, replaying millions of historical events could take a long time.

Instead:

```text
Redis Lost

↓

Load Latest GameState Snapshot

↓

Resume Processing
```

Benefits:

* Faster Redis recovery
* Lower Recovery Time Objective (RTO)
* Avoid replaying the full event history

Game Events remain the source of truth, while snapshots optimize recovery.

---

## 6. Why Redis Hash Instead of Redis String?

**Answer**

Redis Hash allows updating individual fields without rewriting the entire object.

Example:

```text
game:123

homeScore
awayScore
clock
status
```

Instead of serializing and storing the whole object after every update, the Game State Consumer updates only the affected fields.

Example:

```text
HSET game:123 homeScore 104
```

Benefits:

* Smaller writes
* Lower network overhead
* Faster updates
* Cleaner data model

---

## 7. Why Server-Sent Events (SSE)?

Instead of:

* WebSockets
* Long Polling
* Short Polling

**Answer**

The communication pattern is one-way:

Server → Client

Clients never send live updates back.

SSE provides:

* Simpler implementation
* Built on HTTP
* Automatic reconnection
* Lower resource usage than polling
* Lower complexity than WebSockets

Since Apple Sports only pushes live score updates, SSE is the best fit.

---

## 8. Why Webhooks Instead of Polling the Provider?

**Answer**

With polling:

```text
GET every second

↓

Most requests return "No change"
```

This wastes:

* Network bandwidth
* CPU
* Provider resources

With webhooks:

```text
Event occurs

↓

Provider immediately sends POST request
```

Benefits:

* Lower latency
* Lower infrastructure cost
* Event-driven communication
* No unnecessary requests

---

## 9. Why Normalize Provider Payloads?

**Answer**

Different providers expose different payload formats.

Example:

Provider A

```json
{
  "matchId": "123",
  "goal": true
}
```

Provider B

```json
{
  "fixtureId": "123",
  "event": "GOAL"
}
```

Internally we normalize them into a canonical event model:

```json
{
  "gameId": "123",
  "eventType": "GOAL"
}
```

Benefits:

* Simplifies downstream services
* Supports multiple providers
* Easier maintenance
* Consistent internal contracts

---

## 10. Why Kafka Instead of RabbitMQ?

**Answer**

Kafka provides:

* High throughput
* Durable event storage
* Replay capability
* Partition ordering
* Independent consumer groups
* Horizontal scalability

Replay is particularly valuable for:

* Redis recovery
* Consumer recovery
* Historical reprocessing

These capabilities make Kafka a better fit for event-driven sports processing.

---

## 11. Why Event-Driven Instead of Synchronous REST?

**Answer**

A synchronous chain such as:

```text
Provider

↓

Storage

↓

Redis

↓

Notifications

↓

SSE
```

places every component on the critical path.

Failures in one service delay the entire pipeline.

An event-driven architecture:

* Decouples services
* Isolates failures
* Allows independent scaling
* Reduces end-to-end latency
* Improves resilience

Kafka acts as the event backbone between services.

---

## 12. Why a Dedicated Game State Consumer?

Why not compute scores inside the API Service?

**Answer**

The API Service should remain stateless and focus on serving requests.

Computing scores on every API request would be expensive because millions of users may request the same game simultaneously.

Instead:

* Compute the state once
* Store it in Redis
* Serve it many times

Responsibilities become clear:

* **Game State Consumer** → Computes current state
* **Redis** → Stores current state
* **API Service** → Reads and serves current state

---

## 13. Why a Separate Storage Service?

Why not let the Game State Consumer persist raw events?

**Answer**

Separating Storage from Game State processing keeps the critical path lightweight.

The Game State Consumer focuses on:

* Computing current state
* Updating Redis
* Publishing business events

The Storage Service independently persists raw events to Cassandra.

Benefits:

* Lower latency
* Independent scaling
* Better fault isolation
* Cleaner service ownership

---

## 14. Why Partition Kafka by gameId?

**Answer**

Ordering is required for events belonging to the same game.

Partitioning by `gameId` guarantees:

```text
Game 123

↓

Partition 5

↓

Ordered Processing
```

This allows the Game State Consumer to process events sequentially for each game.

Partitioning by:

* eventId
* providerId
* teamId

would not preserve per-game ordering.

---

## 15. Why Keep Raw Events Forever?

We already know the final score.

**Answer**

Raw events are the immutable source of truth.

They enable:

* Replay
* Recovery
* Auditing
* Play-by-play history
* Analytics
* Future feature development

Current state can always be reconstructed from the event stream.

---

## 16. What Would You Improve if You Had Six More Months?

**Answer**

My priority would be improving the platform before adding new product features.

Examples:

* Multi-region active-active deployment
* Schema Registry for event evolution
* Dead Letter Queue (DLQ) for poison messages
* Better observability with distributed tracing and SLOs
* Autoscaling policies
* Chaos engineering and disaster recovery testing
* Operational dashboards and alerting

Once the platform is mature and resilient, I would focus on product enhancements such as:

* Personalized notifications
* ML-based recommendations
* User engagement analytics
* Personalized sports content

---

# Key Interview Takeaways

When discussing technology choices:

1. Start with the **business requirement**.
2. Explain the **responsibility** of the component.
3. Justify **why** the technology fits.
4. Mention the **trade-off** if appropriate.

Example structure:

> **Business Requirement → Responsibility → Technology Choice → Trade-off**

This communication style demonstrates senior engineering thinking and is well suited for Apple ICT4 system design interviews.

# Apple Sports System Design - Production Hardening

Production hardening focuses on operating a distributed system reliably in production. The goal is to ensure the platform remains resilient, recoverable, and scalable when failures occur.

---

# 1. Retry Strategy

## Interview Question

Redis times out while the Game State Consumer is updating the current score.

Should we retry?

## First Principle

Not every failure should be retried.

Classify failures into two categories:

### Transient Failures (Retry)

Examples:

* Network timeout
* Temporary Redis unavailability
* Short CPU spike
* Temporary Cassandra timeout

These failures may succeed if retried.

---

### Permanent Failures (Do Not Retry)

Examples:

* Invalid payload
* Missing required fields
* Unknown event type
* Corrupt message
* Business validation failure

Retries will never fix these.

These events should be moved to a Dead Letter Queue (DLQ).

---

## Retry Strategy

Use exponential backoff with jitter.

Example:

```text
100 ms

↓

200 ms

↓

400 ms

↓

800 ms
```

Maximum:

* 3–5 retries

Adding **jitter** randomizes retry intervals and prevents all consumers from retrying simultaneously, avoiding a **retry storm**.

---

## Why not retry forever?

Infinite retries:

* Block Kafka partitions
* Increase consumer lag
* Waste resources
* Delay processing of subsequent events

After the retry limit is reached, send the message to the Dead Letter Queue.

---

# 2. Circuit Breaker

## Problem

Suppose Redis becomes unavailable.

Without a circuit breaker:

```text
Consumer

↓

Redis Timeout

↓

Retry

↓

Redis Timeout

↓

Retry
```

Thousands of consumers continue sending requests, making recovery even harder.

---

## Solution

Use a Circuit Breaker.

When failures exceed a threshold:

```text
Circuit Opens

↓

Fail Fast

↓

Stop Sending Requests
```

Redis is given time to recover.

---

## Circuit Breaker States

```text
Closed

↓

Open

↓

Half Open

↓

Closed
```

The Half-Open state allows a few test requests to determine whether the downstream dependency has recovered.

---

## Where would I use Circuit Breakers?

* Redis
* Cassandra
* APNs (Notification Service)
* External Provider APIs

Any downstream dependency that can become temporarily unavailable.

---

# 3. Dead Letter Queue (DLQ)

## Example

Provider sends:

```json
{
  "gameId":123
}
```

Missing:

* eventType
* timestamp

Retrying will never fix the payload.

Instead:

```text
Kafka

↓

Game State Consumer

↓

Dead Letter Queue
```

Operations teams can inspect and replay corrected events later.

---

## Messages suitable for DLQ

* Invalid payload
* Unknown schema
* Corrupt event
* Unsupported event type
* Business validation failures

---

## Messages NOT suitable for DLQ

Temporary infrastructure failures:

* Redis timeout
* Cassandra timeout
* Network timeout

Retry these first.

---

# 4. Graceful Shutdown

Suppose Kubernetes terminates a Game State Consumer.

Without graceful shutdown:

```text
Event Processing

↓

Pod Terminated

↓

Offset Not Committed

↓

Duplicate Processing
```

---

## Correct Shutdown Sequence

```text
SIGTERM

↓

Stop Polling Kafka

↓

Finish Current Event

↓

Commit Offset

↓

Shutdown
```

This prevents duplicate processing and ensures a clean consumer shutdown.

---

# 5. Autoscaling

Scale services using business metrics rather than infrastructure metrics whenever possible.

---

## Game State Consumer

Scale based on:

* Kafka Consumer Lag
* Events processed per second

Consumer lag directly measures whether consumers are keeping up with incoming traffic.

---

## API Service

Scale based on:

* CPU
* Memory
* Request latency

---

## SSE Service

Scale based on:

* Active SSE Connections
* Event delivery latency

CPU alone is not a good scaling metric because SSE servers mainly maintain long-lived network connections.

---

## Notification Service

Scale based on:

* Queue depth
* Notifications per second
* APNs response latency

---

## Why not scale everything on CPU?

CPU may remain low while Kafka lag grows significantly.

Business metrics provide a better indication of system health than infrastructure metrics alone.

---

# 6. Rolling Deployment

Deploy new versions gradually.

```text
Old Consumer

↓

New Consumer Starts

↓

Traffic Shifts

↓

Old Consumer Stops
```

Benefits:

* Zero downtime
* Gradual rollout
* Safe Kafka consumer rebalance

---

# 7. Blue-Green Deployment

Maintain two complete environments.

```text
Blue (Current)

Green (New)

↓

Switch Traffic
```

Benefits:

* Instant rollback
* Minimal deployment risk
* Good for API services

---

# 8. Feature Flags

Instead of enabling new functionality for every user immediately:

```text
New Provider

↓

5%

↓

20%

↓

50%

↓

100%
```

Benefits:

* Safer rollout
* Easy rollback
* Canary testing

---

# 9. Chaos Engineering

Intentionally introduce failures to validate system resilience.

Examples:

* Kill Redis
* Kill Kafka Broker
* Kill Game State Consumer
* Kill SSE Server
* Increase Redis latency
* Drop network packets
* Simulate provider outage

The objective is to verify that recovery mechanisms actually work in production.

---

# 10. Rate Limiting

Suppose a provider bug generates:

```text
2 Million Events / Second
```

Protect the Ingestion API using rate limiting.

Possible response:

```http
429 Too Many Requests
```

Benefits:

* Prevent overload
* Protect downstream services
* Maintain system stability

---

# Production Readiness Checklist

Before deploying a distributed system, I verify:

* Retry strategy with exponential backoff and jitter
* Circuit breakers for downstream services
* Dead Letter Queue for poison messages
* Graceful shutdown with Kafka offset commits
* Autoscaling using business metrics
* Rolling or Blue-Green deployments
* Feature flags for gradual rollouts
* Chaos engineering tests
* Monitoring and alerting

---

# Interview Summary

If asked how I would harden this system for production, I would answer:

> "Beyond the core architecture, I would focus on operational resilience. I would implement retries with exponential backoff and jitter for transient failures, circuit breakers to prevent cascading failures, Dead Letter Queues for permanently invalid events, graceful shutdown to avoid duplicate processing during deployments, autoscaling based on business metrics such as Kafka consumer lag and SSE connection count, rolling or blue-green deployments for safe releases, feature flags for gradual rollouts, and chaos engineering to continuously validate the system's recovery mechanisms."

---

# Key Interview Takeaways

* Retry only transient failures.
* Never retry invalid data indefinitely.
* Use DLQs for poison messages.
* Protect downstream services with circuit breakers.
* Scale on business metrics, not just CPU.
* Always support graceful shutdown.
* Deploy safely using rolling or blue-green strategies.
* Validate resilience through chaos engineering.
* Production readiness is as important as system design.


# Apple ICT4 System Design - Communication Framework

One of the biggest differences between a good senior engineer and a great senior engineer is **how they communicate** their design.

The goal is not to sound more technical—it is to communicate architectural thinking clearly.

---

# Rule 1 - Responsibility First

Always begin by explaining **what the component is responsible for**, before explaining how it works.

### Instead of

> Redis stores the score.

### Say

> **The responsibility of Redis is to serve the latest game state with sub-millisecond latency.**

Then explain how:

> We store the game state as a Redis Hash so individual fields can be updated efficiently without rewriting the entire object.

---

## Communication Pattern

```text
Responsibility

↓

Technology Choice

↓

Implementation
```

Avoid:

```text
Technology

↓

Technology

↓

Technology
```

---

# Rule 2 - Explain "Why" Before "How"

Interviewers care more about the reasoning than the implementation.

### Instead of

> Redis Hash

> HSET

> Fields

Explain:

> The game state changes frequently, but only a few fields change for each event. Using a Redis Hash allows us to update only those fields instead of rewriting the entire object.

Always answer:

* Why?
* Then How?

---

# Rule 3 - Tell a Story

Avoid explaining services independently.

Instead, explain the lifecycle of an event.

Example:

> When a provider sends an event, the Ingestion Service authenticates and normalizes the payload before publishing it to Kafka. The Game State Consumer owns the authoritative game state, validates ordering and idempotency, computes the updated score, updates Redis, persists a GameState snapshot for recovery, and publishes a business event. SSE and Notification Services independently consume those business events to update users in real time.

A story is much easier for interviewers to follow than isolated components.

---

# Rule 4 - Explicitly Identify the Critical Path

Use this phrase naturally:

> **The critical path is...**

Example:

```text
Provider

↓

Kafka

↓

Game State Consumer

↓

Redis

↓

SSE

↓

Client
```

Then immediately mention:

> Everything else is intentionally kept **off the critical path**.

Examples:

* Storage Service
* Analytics
* Notification Service

This demonstrates architectural thinking.

---

# Rule 5 - Always Mention the Trade-off

Every important design decision has a trade-off.

Example:

> I partition Kafka by gameId to preserve ordering. The trade-off is that a single hot game cannot be processed in parallel, but correctness is more important than maximizing throughput.

Trade-offs demonstrate mature engineering judgment.

---

# Rule 6 - Think in Terms of Ownership

Instead of saying:

> Storage Service stores events.

Say:

> Storage Service owns immutable event history.

Instead of:

> Redis stores the score.

Say:

> Redis owns the current game state cache.

Instead of:

> SSE pushes updates.

Say:

> SSE owns real-time delivery to connected clients.

Ownership is a hallmark of senior architectural thinking.

---

# Rule 7 - Be Decisive

Avoid uncertain language.

### Instead of

> Maybe Redis...

> Redis Set?

Say

> I'd use a Redis Hash.

Confidence inspires confidence.

If the interviewer wants alternatives, they will ask.

---

# Rule 8 - Finish the Story

Don't stop after one step.

Instead of:

```text
Update Redis
```

Finish the entire flow:

```text
Update Redis

↓

Persist GameState Snapshot

↓

Publish SCORE_UPDATED

↓

SSE

↓

Client
```

Complete flows demonstrate a full understanding of the system.

---

# Rule 9 - Use Concrete Numbers

Numbers make your design more believable.

Instead of:

> High traffic

Say:

> Two million concurrent users

Instead of:

> Low latency

Say:

> Sub-millisecond Redis reads

Whenever possible, quantify scale and performance.

---

# Rule 10 - Use Senior Engineering Language

These phrases naturally communicate senior-level thinking.

## Responsibilities

> The responsibility of this service is...

---

## Critical Path

> This component is on the critical path.

> This service is intentionally off the critical path.

---

## Trade-offs

> The trade-off here is...

---

## Scalability

> This allows the service to scale independently.

---

## Failure Isolation

> Failure is isolated to this component.

---

## Recovery

> The source of truth remains the event stream.

---

## Ownership

> This service owns...

---

## Latency

> This is part of the latency-sensitive path.

---

## Consistency

> Eventual consistency is acceptable for this use case.

---

## Business Requirement

> The business requirement drives this design decision.

---

# Example Transformation

## Before

```text
Kafka

↓

Redis

↓

Consumer

↓

Cassandra
```

---

## After

> The Game State Consumer owns the authoritative game state. It consumes raw events from Kafka, validates ordering and idempotency, applies business rules, updates Redis so the API Service can serve live scores with sub-millisecond latency, persists a GameState snapshot for fast recovery, and publishes business events for downstream consumers such as SSE and Notification Services. This keeps the critical path lightweight while allowing downstream services to scale independently.

Same architecture.

Much stronger communication.

---

# A Simple Framework for Every "Why?" Question

Whenever an interviewer asks:

> Why did you choose this technology?

Use this structure:

## 1. Business Requirement

What problem are we solving?

Example:

> Millions of users need live scores with very low latency.

---

## 2. Responsibility

What responsibility does this component have?

Example:

> Redis serves the latest game state.

---

## 3. Technology Choice

Explain the implementation.

Example:

> I use an in-memory Redis Hash because only a few fields change for each event.

---

## 4. Trade-off

Every decision has one.

Example:

> This introduces another infrastructure component, but dramatically improves read latency while allowing Cassandra to focus on durable event storage.

---

# Final Takeaways

Throughout the interview:

* Lead with **responsibility**, not technology.
* Explain **why** before **how**.
* Tell the story of the request or event.
* Identify the **critical path**.
* Mention important **trade-offs**.
* Speak in terms of **ownership**.
* Use **numbers** whenever possible.
* Finish the complete end-to-end flow.
* Sound decisive and confident.

## Golden Formula

For almost every architecture question:

```text
Business Requirement

↓

Responsibility

↓

Technology Choice

↓

Implementation

↓

Trade-off
```

This communication style consistently presents you as a strong ICT4-level engineer because it demonstrates structured thinking, clear ownership, and sound architectural judgment rather than simply listing technologies.
