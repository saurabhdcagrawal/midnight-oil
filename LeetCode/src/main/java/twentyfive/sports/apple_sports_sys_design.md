# Apple Sports System Design - Part 1
# Requirements, Scale & High-Level Architecture

## Problem Statement

Design a highly scalable Live Sports platform similar to Apple Sports, ESPN, or Yahoo Sports that delivers real-time sports updates to millions of users.

The system receives live sports events from official sports data providers and serves current scores, play-by-play events, statistics, and notifications to users with very low latency.

---

# Functional Requirements

The system should support:

- Display live scores
- Display play-by-play events
- Display live match statistics
- Store historical match data
- Allow users to follow favorite teams
- Send push notifications for important events

Examples:

Current Score

```
Lakers 102

Warriors 100

Q4 01:35
```

Play-by-Play

```
8:31 PM

🏀 Curry made 3PT

Warriors 98-96

-------------------

8:33 PM

🏀 James Dunk

Lakers 100-98
```

---

# Non-Functional Requirements

- Score updates should appear within one second.
- Highly available system.
- Horizontally scalable.
- Support millions of concurrent users.
- Preserve ordering of events for each match.
- Never lose scoring events.

---

# Scale Estimation

| Metric | Estimate |
|---------|----------|
| Concurrent Users | ~5 Million |
| Sports Events | ~10,000 events/sec |
| Read Requests | 1–5 Million requests/sec |
| Push Notifications | Millions during major events |
| Historical Events | Billions |

## Observation

This is a highly read-heavy system.

Although only about 10,000 sports events per second are ingested, millions of users continuously read the latest scores.

This observation drives the architecture:

- Kafka for scalable event ingestion.
- Redis for low-latency reads.
- Cassandra for durable historical storage.

---

# APIs

## Current Match

```http
GET /matches/{matchId}
```

Response

```json
{
  "matchId":"NBA123",
  "homeTeam":"Lakers",
  "awayTeam":"Warriors",
  "score":"102-100",
  "quarter":"Q4",
  "clock":"01:35"
}
```

---

## Match Timeline

```http
GET /matches/{matchId}/events
```

---

## Match Statistics

```http
GET /matches/{matchId}/stats
```

---

## Subscribe To Team

```http
POST /users/{userId}/subscriptions
```

---

# High-Level Architecture

```
                     Sports Data Providers
               (NBA, NFL, FIFA, MLB, etc.)
                            │
                            ▼
                  Live Sports Feed API
                            │
                            ▼
                   Ingestion Service
          (Validation + Normalization)
                            │
                            ▼
                         Kafka
                  (match-events topic)
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
        ▼                   ▼                   ▼
 Score Processor   Statistics Processor   Notification Processor
        │                   │                   │
        │                   │                   ▼
        │                   │           Push Notification
        │                   │               Service
        │                   │
        ├─────────────┐      │
        ▼             ▼      ▼
     Redis      Cassandra  Cassandra
(Current Score) (Timeline) (Statistics)
        │             │
        └──────┬──────┘
               ▼
          API Service
               │
       ┌───────┴────────┐
       ▼                ▼
   Mobile App       Web Client
```

---

# End-to-End Flow

1. Sports providers send real-time match events.
2. Ingestion Service authenticates, validates, and normalizes events.
3. Canonical events are published to Kafka.
4. Multiple consumer groups independently process the same event.
5. Score Processor updates Redis and Cassandra.
6. Statistics Processor computes live statistics.
7. Notification Processor sends push notifications.
8. API Service serves live data to users.

---

# Why This Architecture?

The architecture separates responsibilities:

- Kafka decouples producers from consumers.
- Redis serves the latest match state with very low latency.
- Cassandra stores durable historical data.
- Independent processors scale separately.
- New consumers can be added without changing the ingestion pipeline.

---

# Key Design Principles

- Event-driven architecture.
- Loose coupling through Kafka.
- Independent consumer groups.
- Low-latency read path using Redis.
- Durable historical storage using Cassandra.
- Horizontal scalability.
- High availability.


# Apple Sports System Design - Part 2
# Ingestion Service & Kafka Design

## Ingestion Service

The Ingestion Service acts as the entry point into the system.

```
Sports Data Providers

↓

HTTPS Request

↓

Ingestion Service

↓

Kafka
```

The service is responsible for:

- Authenticating sports data providers.
- Validating incoming events.
- Normalizing provider-specific payloads into a canonical event model.
- Performing basic deduplication.
- Publishing standardized events to Kafka.
- Returning acknowledgements to the provider.

---

# Why Do We Need an Ingestion Service?

A common interview question is:

> Why not let sports providers publish directly to Kafka?

The Ingestion Service isolates external providers from the internal event-driven architecture.

Benefits:

- Authentication
- Request validation
- Rate limiting
- Canonical event model
- Easier onboarding of new providers
- Internal services remain independent of provider-specific schemas

This acts as an Anti-Corruption Layer between external systems and internal services.

---

# Event Normalization

Different providers expose different payload formats.

NBA

```json
{
   "points":3
}
```

Soccer

```json
{
   "goal":true
}
```

NFL

```json
{
   "touchdown":true,
   "points":7
}
```

Rather than exposing these differences to downstream services, the Ingestion Service converts them into a canonical event.

Example:

```json
{
   "eventId":"evt-123",
   "matchId":"nba-456",
   "eventType":"SCORE",
   "teamId":"LAL",
   "points":3,
   "eventTime":"2026-08-02T20:15:00Z"
}
```

Now every downstream consumer processes the same schema regardless of the original provider.

---

# Validation

Before publishing to Kafka, invalid events are rejected.

Examples:

- Missing matchId
- Invalid team
- Negative score
- Invalid timestamp
- Unknown event type

Only valid events enter the event pipeline.

---

# Deduplication

Sports providers may retry sending events due to network failures.

Example:

```
Goal Event

↓

Network Timeout

↓

Provider Retries
```

If the provider supplies a unique:

```
eventId
```

the Ingestion Service can check whether the event has already been processed.

```
Already Processed?

↓

Ignore Duplicate
```

This becomes the first layer of duplicate protection before Kafka.

---

# Kafka

The Ingestion Service publishes all canonical events to Kafka.

Topic:

```
match-events
```

Kafka becomes the event backbone of the system.

Every downstream service independently consumes the same events.

```
match-events

        │
        ├───────────────┐
        │               │
        ▼               ▼
 Score Processor   Statistics Processor
        │
        ▼
Notification Processor
```

Kafka fan-out allows each consumer group to scale independently.

---

# Why One Topic?

Recommended approach:

```
match-events
```

containing all event types.

Examples:

- Score
- Goal
- Foul
- Timeout
- Substitution
- Quarter Start
- Quarter End

Consumers filter only the events relevant to them.

Advantages:

- Simpler producer
- Easier event replay
- Easier onboarding of new consumers
- Single source of sports events

---

# Why Not Multiple Topics?

Alternative:

```
score-events

statistics-events

notification-events
```

Problem:

The Ingestion Service must decide which topic(s) each event belongs to.

Business logic starts leaking into the producer.

Using a single canonical topic keeps producers simple and downstream consumers independent.

---

# Kafka Partitioning

Partition Key:

```
matchId
```

Example:

```
Lakers vs Warriors

↓

Partition 7
```

Kafka guarantees ordering only within a partition.

Since all events for the same match share the same partition key, event ordering is preserved.

Example:

Correct ordering:

```
Score

↓

Foul

↓

Timeout

↓

Score
```

Ordering must never become:

```
Timeout

↓

Score

↓

Foul
```

Partitioning by `matchId` guarantees sequential processing for a single match while allowing different matches to be processed in parallel.

---

# Number of Partitions

Partitions should be selected based on throughput and desired consumer parallelism.

Example assumptions:

Peak traffic:

```
10,000 events/sec
```

If one partition comfortably handles:

```
500 events/sec
```

Required partitions:

```
10000 / 500

≈20
```

Provisioning approximately **32 partitions** provides additional capacity for future growth and allows more consumer parallelism.

---

# Consumer Groups

Suppose there are 32 partitions.

Score Processor Consumer Group

```
Consumer 1

Consumer 2

...

Consumer 32
```

Statistics Processor Consumer Group

```
Consumer 1

Consumer 2

...

Consumer 32
```

Each consumer group independently processes every event.

Kafka automatically assigns partitions within each consumer group.

---

# Hot Matches

A common interview follow-up is:

> What if the Super Bowl becomes extremely hot?

Since partitioning uses:

```
matchId
```

all events for that match go to one partition.

Although this limits processing for a single match to one consumer, this is acceptable because:

- Sports matches generate relatively low event rates compared to Kafka's partition capacity.
- Preserving event ordering is more important than distributing a single match across multiple partitions.

Different matches continue to scale horizontally across the remaining partitions.

---

# Interview Questions

### Q: Why have an Ingestion Service instead of letting providers publish directly to Kafka?

> The Ingestion Service authenticates providers, validates requests, normalizes different provider payloads into a canonical event model, performs basic deduplication, and decouples external provider contracts from internal services.

---

### Q: Why use a single Kafka topic?

> A single canonical `match-events` topic keeps producers simple, allows downstream services to independently consume and filter events, simplifies replay, and makes it easy to add new consumers without changing the ingestion pipeline.

---

### Q: Why partition by `matchId`?

> All events for a match must remain ordered. Kafka guarantees ordering only within a partition, so using `matchId` as the partition key ensures sequential processing for a match while allowing different matches to be processed in parallel.

---

### Q: How do you determine the number of partitions?

> Partitions are determined by expected throughput and required consumer parallelism. The goal is to provide sufficient capacity while allowing horizontal scaling of consumer groups.

---

# Key Takeaways

- The Ingestion Service acts as an Anti-Corruption Layer.
- External provider payloads are normalized into a canonical event model.
- Invalid events are rejected before entering Kafka.
- Basic deduplication can be performed using provider event IDs.
- A single `match-events` topic simplifies the architecture.
- Partitioning by `matchId` preserves per-match ordering.
- Consumer groups allow independent horizontal scaling.
- The number of partitions is driven by throughput and desired parallelism.

# Apple Sports System Design - Part 3
# Score Processor, Redis & Cassandra

## Score Processor

The Score Processor is a Kafka consumer responsible for processing live sports events and updating the current match state and historical records.

Architecture:

```
                 Kafka

                   │

                   ▼

            Score Processor

             │            │

             ▼            ▼

         Redis      Cassandra

(Current State)   (Historical Data)
```

Every event published to Kafka is independently processed by the Score Processor.

---

# Responsibilities

The Score Processor:

- Consumes events from Kafka.
- Updates the latest match state in Redis.
- Stores historical events in Cassandra.
- Commits the Kafka offset only after all required processing completes successfully.

---

# Why Update Both Redis and Cassandra?

Redis and Cassandra serve different purposes.

## Redis

Stores only the latest match state.

Example:

```
Key

match:NBA123
```

Value

```json
{
   "score":"102-100",
   "quarter":"Q4",
   "clock":"01:35"
}
```

Purpose:

- Extremely low-latency reads.
- Millions of API requests.
- Current score only.

---

## Cassandra

Stores historical information.

Examples:

- Every scoring event.
- Match timeline.
- Historical statistics.
- Completed matches.

Purpose:

- Durable persistence.
- Historical queries.
- Analytics.

---

# Source of Truth

One of the most common interview questions is:

> What is the source of truth?

In an event-driven architecture there are two concepts:

## Event Source of Truth

Kafka is the source of truth for the sequence of business events.

Every downstream system derives its state from these events.

```
Kafka

↓

Score Processor

↓

Redis

↓

Cassandra
```

---

## Durable Data Store

Cassandra is the durable persistent store for historical match data.

Redis is **not** the source of truth.

Redis is a materialized cache of the latest match state.

---

# Should Redis Update Cassandra?

No.

Redis should never update Cassandra.

Both databases are independently updated by processing the same Kafka event.

```
                Kafka

                  │

                  ▼

           Score Processor

            │           │

            ▼           ▼

        Redis      Cassandra
```

Neither database depends on the other.

---

# Processing Flow

For every Kafka event:

```
Consume Event

↓

Update Redis

↓

Update Cassandra

↓

Commit Kafka Offset
```

Conceptually these are part of the same processing step.

The Kafka consumer offset is committed only after both updates succeed.

---

# Parallel Writes

Although the processing flow is often shown sequentially, Redis and Cassandra updates are independent.

A production implementation can execute them concurrently.

Example:

```
             Kafka Event

                  │

                  ▼

           Score Processor

            │           │

            ▼           ▼

      Update Redis  Update Cassandra

            │           │

            └─────┬─────┘

                  ▼

            Commit Offset
```

This reduces processing latency while maintaining correctness.

---

# Why Commit Offset Last?

Suppose:

```
Redis ✓

↓

Commit Offset ✓

↓

Cassandra ✗
```

The event would be permanently lost for Cassandra.

Instead:

```
Redis ✓

↓

Cassandra ✗

↓

Offset NOT Committed
```

Kafka redelivers the event.

No data is lost.

---

# Failure Scenario

Suppose:

```
Kafka Event

↓

Update Redis ✓

↓

Update Cassandra ✗

↓

Crash
```

Since the offset was never committed:

```
Kafka

↓

Replay Event
```

Redis executes again.

Example:

```
SET score = "102-100"
```

Running this operation twice produces the same state.

Cassandra stores the missing historical record.

The system converges to the correct state.

---

# Idempotency

Kafka provides at-least-once delivery.

Therefore every external side effect should be idempotent.

## Redis

Good:

```
SET score = "102-100"
```

Running multiple times produces the same state.

Not idempotent:

```
INCR homeScore
```

Running multiple times incorrectly increments the score.

---

## Cassandra

Historical events should be written using a unique business identifier.

Example:

```
eventId = EVT-123
```

If the same event is replayed:

```
Already Exists

↓

Ignore Duplicate
```

This prevents duplicate historical records.

---

# Why Not Cassandra First?

A common interview suggestion is:

```
Update Cassandra

↓

Update Redis
```

This unnecessarily delays updates to the cache.

The API primarily serves current scores from Redis.

Since Redis and Cassandra are independent destinations, neither needs to wait for the other.

Running both updates concurrently minimizes latency.

---

# Read Path

## Current Score

```
Client

↓

API

↓

Redis

↓

Response
```

Redis serves millions of read requests with sub-millisecond latency.

---

## Historical Timeline

```
Client

↓

API

↓

Cassandra

↓

Response
```

Historical data is read directly from Cassandra.

Redis stores only hot, frequently accessed data.

---

# What Happens If Redis Crashes?

Redis is only a cache.

No business data is lost.

Redis can be rebuilt by replaying Kafka events or reconstructing the latest state from Cassandra.

Because Redis is derived state, it should never be considered the system of record.

---

# Interview Questions

### Q: Why use Redis if Cassandra already contains the latest score?

> Redis is optimized for sub-millisecond reads and serves millions of live score requests. Cassandra is optimized for durable storage and historical queries. Keeping current state in Redis significantly reduces latency and database load.

---

### Q: Should Redis update Cassandra?

> No. Redis is a cache, not a system of record. Both Redis and Cassandra are independently updated from the same Kafka event.

---

### Q: When should the Kafka offset be committed?

> Only after all required processing completes successfully. If any required write fails, the offset should not be committed so Kafka can safely redeliver the event.

---

### Q: Why are Redis and Cassandra writes idempotent?

> Kafka provides at-least-once delivery. Events may be replayed after failures, so updates to Redis and Cassandra must be idempotent to ensure retries produce the correct final state.

---

# Key Takeaways

- Kafka is the source of truth for the event stream.
- Cassandra is the durable historical data store.
- Redis is a derived cache containing the latest match state.
- Redis never updates Cassandra.
- Both Redis and Cassandra are updated independently from the same Kafka event.
- Kafka offsets are committed only after all required processing succeeds.
- Every external side effect should be idempotent because Kafka may redeliver events.
- Redis serves current state; Cassandra serves historical data.

# Apple Sports System Design - Part 4
# API Layer, WebSockets & Notification Service

## API Layer

The API Layer is the entry point for all client requests.

Responsibilities:

- Serve current match scores.
- Serve historical match timelines.
- Serve match statistics.
- Handle user subscriptions.
- Authenticate requests.
- Route requests to the appropriate backend services.

---

# Read Path

## Current Score

Current score is always served from Redis.

```
Client

↓

API Service

↓

Redis

↓

Response
```

Why?

- Sub-millisecond latency
- Millions of reads per second
- Keeps Cassandra isolated from read-heavy traffic

---

## Historical Timeline

Timeline data is served from Cassandra.

```
Client

↓

API Service

↓

Cassandra

↓

Response
```

Historical data is much larger and is queried less frequently.

Redis should only store hot, frequently accessed data.

---

# How Do Live Updates Reach Users?

A common interview question is:

> How does the score automatically update on a user's phone?

Three approaches are possible.

---

# Option 1 — Polling

Client:

```
Every 2 seconds

↓

GET /matches/NBA123
```

Problems:

- Huge number of unnecessary requests.
- High API load.
- Increased latency.
- Battery consumption.

Millions of users polling every few seconds quickly overwhelm the backend.

---

# Option 2 — Long Polling

Client:

```
GET /matches/NBA123

↓

Server waits

↓

New event occurs

↓

Response returned

↓

Client reconnects
```

Better than polling.

Still requires frequent reconnects.

Higher server overhead.

---

# Option 3 — WebSockets (Recommended)

Client establishes a persistent connection.

```
Client

⇄

WebSocket Server
```

Whenever a score changes:

```
Score Processor

↓

Redis Updated

↓

Publish Update

↓

WebSocket Server

↓

Push Update

↓

Client
```

Advantages:

- Near real-time updates.
- Very low latency.
- Minimal network overhead.
- No repeated HTTP requests.

This is the preferred solution for live sports.

---

# WebSocket Flow

```
Sports Provider

↓

Kafka

↓

Score Processor

↓

Redis

↓

WebSocket Gateway

↓

Millions of Clients
```

The API handles request-response operations.

The WebSocket Gateway handles live streaming updates.

---

# Notification Service

The Notification Service is another Kafka consumer.

Architecture:

```
Kafka

↓

Notification Processor

↓

Notification Service

↓

Apple Push Notification Service (APNs)

↓

User Device
```

---

# Responsibilities

The Notification Service:

- Consumes sports events.
- Determines whether users subscribed to the event.
- Builds notification payloads.
- Sends push notifications.

Example:

```
Lakers Score

↓

Find Followers

↓

Generate Notification

↓

Send Push
```

---

# User Subscriptions

Users can follow:

- Teams
- Players
- Entire leagues

Example:

```
User 123

↓

Favorite Team

↓

Lakers
```

Stored in a Subscription Store.

When an event arrives:

```
Goal Event

↓

Lookup Subscribers

↓

Generate Notifications
```

---

# Fan-Out

Suppose:

```
Lakers Score
```

Followers:

```
2 Million Users
```

The Notification Service should **not** send all notifications from a single server.

Instead:

```
Kafka Event

↓

Notification Processor

↓

Notification Queue

↓

Worker Pool

↓

Push Notification Service
```

Multiple workers independently send notifications.

Workers can be scaled horizontally.

---

# Why Separate Notification Processing?

Sending push notifications is relatively slow.

The Score Processor should not wait for notification delivery.

Instead:

```
Kafka Event

        │

        ├─────────────► Score Processor

        │

        └─────────────► Notification Processor
```

Both process the same event independently.

This keeps the critical path for updating live scores fast.

---

# Failure Handling

Suppose APNs becomes temporarily unavailable.

```
Notification Worker

↓

Retry

↓

Dead Letter Queue

↓

Retry Later
```

Live score processing is completely unaffected.

Loose coupling ensures one downstream service does not block others.

---

# Why Kafka Fan-Out?

Kafka allows multiple independent consumer groups.

```
match-events

        │

        ├──────────► Score Processor

        │

        ├──────────► Statistics Processor

        │

        └──────────► Notification Processor
```

Each service:

- Scales independently.
- Processes events at its own speed.
- Can be deployed independently.

This is one of the biggest advantages of an event-driven architecture.

---

# WebSocket Scaling

One WebSocket server cannot support millions of persistent connections.

Architecture:

```
               Load Balancer

                     │

      ┌──────────────┼──────────────┐

      ▼              ▼              ▼

 WebSocket 1    WebSocket 2    WebSocket 3
```

Clients maintain persistent connections with one WebSocket server.

When a score changes:

```
Redis Updated

↓

Publish Internal Event

↓

Relevant WebSocket Server

↓

Connected Clients
```

Each WebSocket server only pushes updates to the clients connected to it.

---

# Interview Questions

### Q: Why not use polling?

> Polling generates unnecessary requests even when no scores change, increasing API load, network traffic, and latency. Live sports require near real-time updates, making WebSockets a much better fit.

---

### Q: Why separate the Notification Service?

> Sending push notifications is slow and may involve external services such as APNs. Separating notification processing keeps the live score update path fast and allows notifications to scale independently.

---

### Q: Why use Kafka fan-out?

> Kafka allows multiple consumer groups to independently consume the same event stream. Score processing, statistics computation, and notifications remain loosely coupled and can scale independently.

---

### Q: How do millions of users receive live updates?

> Clients establish persistent WebSocket connections with a fleet of WebSocket servers. Whenever Redis is updated with a new score, the corresponding update is pushed over WebSockets to connected clients, providing sub-second latency without continuous polling.

---

# Key Takeaways

- Current scores are served from Redis.
- Historical timelines are served from Cassandra.
- WebSockets are preferred over polling for live sports.
- Kafka fan-out enables multiple independent downstream services.
- Notification processing is decoupled from score processing.
- Worker pools allow notification delivery to scale horizontally.
- WebSocket servers maintain persistent client connections and push score updates in real time.


# Apple Sports System Design - Part 5
# Failure Scenarios, Reliability & Trade-offs

One of the key qualities of a senior system design is demonstrating how the system behaves when components fail.

The goal is not to eliminate failures but to build a resilient system that can recover automatically while minimizing user impact.

---

# Failure Scenario 1 – Redis Failure

Question:

> What happens if Redis crashes?

Redis is a cache, **not** the system of record.

Architecture:

```
                Kafka

                  │

                  ▼

           Score Processor

            │           │

            ▼           ▼

        Redis      Cassandra
```

If Redis crashes, the latest match state is temporarily unavailable.

However:

- Kafka still contains the event stream.
- Cassandra still contains historical match data.

No business data is lost.

---

## Recovery Option 1 – Replay Kafka

If Kafka still retains the events:

```
Kafka

↓

Replay Events

↓

Rebuild Redis
```

The Score Processor reprocesses historical events and reconstructs the latest match state.

---

## Recovery Option 2 – Warm Redis From Cassandra

Alternatively:

```
Cassandra

↓

Latest Match State

↓

Populate Redis
```

This is usually faster than replaying an entire event stream.

---

## API Behavior

During Redis recovery:

```
Client

↓

API

↓

Redis

↓

Cache Miss
```

The API can temporarily read from Cassandra.

Advantages:

- Higher latency.
- Service remains available.

---

# Failure Scenario 2 – Cassandra Failure

Question:

Can live scores still be served?

Yes.

Users primarily read:

```
Current Score

↓

Redis
```

Redis continues serving current match state.

Problem:

Historical writes fail.

Processing becomes:

```
Kafka Event

↓

Redis ✓

↓

Cassandra ✗

↓

Offset NOT Committed
```

Kafka retains the event.

When Cassandra recovers:

```
Replay Event

↓

Persist History
```

No historical events are lost.

---

# Failure Scenario 3 – Kafka Broker Failure

Suppose:

```
Broker 1 (Leader)

Broker 2 (ISR)

Broker 3 (ISR)
```

Leader crashes.

Kafka KRaft elects a new leader from the In-Sync Replica (ISR).

Producer retries automatically.

Consumers continue consuming from the new leader.

Minimal interruption.

---

# Failure Scenario 4 – Score Processor Failure

Suppose processing fails after Redis is updated.

```
Kafka Event

↓

Redis Updated ✓

↓

Crash

↓

Offset NOT Committed
```

Kafka redelivers the event.

Redis executes:

```
SET score = "104-100"
```

again.

Since the operation is idempotent, the final state remains correct.

---

# Failure Scenario 5 – Notification Service Failure

Architecture:

```
Kafka

        │

        ├────────► Score Processor

        │

        └────────► Notification Processor
```

If the Notification Service fails:

- Live score updates continue.
- Statistics continue.
- Only notifications are delayed.

This demonstrates the advantage of loosely coupled consumer groups.

---

# Failure Scenario 6 – WebSocket Server Failure

Suppose:

```
WS7

↓

Crash
```

Clients detect the broken connection.

They automatically reconnect through the load balancer.

Example:

```
Client

↓

Load Balancer

↓

WS3
```

WS3 registers the client's match subscriptions and continues pushing live updates.

---

# Failure Scenario 7 – Duplicate Events

Duplicate events may occur because:

- Provider retries.
- Kafka replay.
- Consumer crashes before committing offsets.

Consumers should process events idempotently.

Example:

Redis

Good:

```
SET score = "104-100"
```

Not Good:

```
INCR score
```

Historical events should use unique event identifiers.

```
eventId = EVT-123
```

If the same event is replayed:

```
Already Exists

↓

Ignore
```

---

# Event Ordering

A common interview question is:

> What if score updates arrive out of order?

Example:

Correct order:

```
Score

↓

Timeout

↓

Score
```

Incorrect:

```
Timeout

↓

Score
```

Kafka preserves ordering within a partition.

By partitioning using:

```
matchId
```

all events for the same match are processed sequentially.

---

# Hot Matches

Question:

> What if the Super Bowl becomes extremely hot?

Since partitioning uses:

```
matchId
```

all events for that match are processed by one partition.

Although this limits processing for one match to a single consumer, sports events generate relatively low event rates.

Maintaining correct event ordering is more important than distributing a single match across multiple partitions.

Different matches continue to scale horizontally across other partitions.

---

# Reliability Principles

The design follows several important distributed system principles.

### Kafka provides reliable event delivery.

- Events are retained on disk.
- Consumers commit offsets only after successful processing.
- Failed processing results in replay.

---

### Redis is a cache.

- Fast reads.
- Rebuildable.
- Not the system of record.

---

### Cassandra provides durable persistence.

- Historical timeline.
- Match statistics.
- Long-term storage.

---

### External operations are idempotent.

Kafka provides at-least-once delivery.

Every external write must therefore be safe to execute multiple times.

---

# Interview Questions

### Q: What happens if Redis crashes?

> Redis is only a cache. The latest state can be reconstructed either by replaying Kafka events or by rebuilding the cache from Cassandra.

---

### Q: Why doesn't a Notification Service failure stop score updates?

> Notification processing is an independent Kafka consumer group. Kafka fan-out allows each downstream service to process events independently without affecting other services.

---

### Q: Why is idempotency important?

> Kafka guarantees at-least-once delivery. Events may be replayed after failures, so every external write must be idempotent to ensure retries produce the same final state.

---

### Q: How is event ordering preserved?

> Kafka guarantees ordering within a partition. By partitioning using `matchId`, all events for the same match are processed sequentially.

---

# Key Takeaways

- Redis failures do not cause data loss because Redis is a cache.
- Cassandra failures delay persistence but do not lose events because Kafka retains them until processing succeeds.
- Kafka broker failures are handled through leader election.
- Consumer crashes are handled through offset replay.
- Notification failures are isolated from score processing.
- Event ordering is preserved using `matchId` as the partition key.
- Idempotent processing ensures retries converge to the correct final state.


> The system uses an event-driven architecture where sports providers send canonical events through an Ingestion Service into Kafka. Kafka acts as the backbone, allowing multiple independent consumer groups such as Score Processing, Statistics, and Notifications to process the same events. The Score Processor updates Redis for low-latency reads and Cassandra for durable historical storage. APIs serve current scores from Redis and historical data from Cassandra. Live updates are delivered through WebSockets, while Kafka's at-least-once delivery and idempotent processing ensure reliable event handling. The architecture scales horizontally, preserves per-match ordering using matchId partitioning, and remains resilient to failures because each downstream service is independently scalable and recoverable.

# Apple Sports System Design
# Redis Data Model

Before designing the database schema, always identify the application's access patterns.

Database schemas should be driven by **how the application reads and writes data**, not by the entities themselves.

---

# Access Patterns

## 1. Current Match Score

```http
GET /matches/{matchId}
```

Required information:

- Home Team
- Away Team
- Current Score
- Quarter / Period
- Game Clock
- Match Status

---

## 2. Match Timeline

```http
GET /matches/{matchId}/events
```

Required information:

- Goals / Scores
- Fouls
- Timeouts
- Substitutions
- Chronological ordering

---

## 3. Match Statistics

```http
GET /matches/{matchId}/stats
```

Required information:

- Possession
- Rebounds
- Assists
- Fouls
- Other live statistics

---

## Observation

Nearly every request begins with:

```
matchId
```

This naturally becomes the primary lookup key.

---

# Redis Design

Redis stores only the **current live match state**.

Historical data is stored in Cassandra.

---

# Option 1 – Store JSON as a Redis String

Key:

```
match:NBA123
```

Value:

```json
{
    "homeScore":102,
    "awayScore":100,
    "quarter":"Q4",
    "clock":"01:35"
}
```

Updating the score requires:

```
Read JSON

↓

Deserialize

↓

Modify

↓

Serialize

↓

Write Entire JSON
```

Problems:

- Entire object rewritten for every update.
- Additional serialization/deserialization overhead.
- Inefficient partial updates.

---

# Option 2 – Redis Hash (Recommended)

Key:

```
match:NBA123
```

Fields:

```
homeTeam     → Lakers
awayTeam     → Warriors
homeScore    → 102
awayScore    → 100
quarter      → Q4
clock        → 01:35
status       → LIVE
lastUpdated  → 2026-08-02T20:05:30Z
```

Example commands:

```redis
HSET match:NBA123 homeScore 104

HSET match:NBA123 clock "01:12"

HMGET match:NBA123 homeScore awayScore clock
```

Advantages:

- Update only the changed field.
- No JSON serialization.
- Efficient memory utilization.
- Natural representation of an object.
- Excellent performance for partial updates.

This is the preferred Redis data structure.

---

# Option 3 – Multiple Redis Keys

Example:

```
score:NBA123

quarter:NBA123

clock:NBA123

status:NBA123
```

Problems:

Every API request requires multiple Redis lookups.

```
GET

↓

GET

↓

GET

↓

GET
```

Higher network overhead and increased latency.

Not recommended.

---

# Idempotent Updates

Kafka provides **at-least-once delivery**, meaning events may be replayed.

Therefore Redis updates must be idempotent.

### Good

The Score Processor computes the latest state and writes:

```redis
HSET match:NBA123 homeScore 104
```

If the event is replayed:

```
104

↓

104
```

Final state remains correct.

---

### Not Recommended

```redis
HINCRBY match:NBA123 homeScore 2
```

If the event is replayed:

```
102

↓

104

↓

106
```

Incorrect result.

For event-driven architectures, prefer writing the **computed state** (`HSET`) rather than incrementing values (`HINCRBY`).

---

# Key Expiration

Redis stores only hot, live data.

While the match is active:

```
No Expiration
```

After the match finishes:

```
FINAL

↓

EXPIRE 24 hours
```

or

```
Delete Key
```

Historical information remains permanently stored in Cassandra.

---

# Final Redis Schema

Key:

```
match:{matchId}
```

Data Structure:

```
Redis Hash
```

Example:

```
match:NBA123

homeTeam     -> Lakers
awayTeam     -> Warriors
homeScore    -> 104
awayScore    -> 100
quarter      -> Q4
clock         -> 01:12
status        -> LIVE
lastUpdated   -> 2026-08-02T20:05:30Z
```

---

# Interview Questions

### Q: Why use a Redis Hash instead of storing JSON?

> Redis Hashes allow efficient field-level updates without rewriting the entire object. They avoid serialization overhead and are a natural fit for representing mutable objects such as live match state.

---

### Q: Why not store every attribute as a separate Redis key?

> Multiple keys require multiple network round trips and increase latency. A single Redis Hash keeps all match state together and can be retrieved efficiently.

---

### Q: Why use `HSET` instead of `HINCRBY`?

> Kafka delivers messages using at-least-once semantics, so events may be replayed. `HSET` writes the computed state and is idempotent, while `HINCRBY` would incorrectly increment the score again during replay.

---

# Key Takeaways

- Design the schema based on access patterns.
- Redis stores only the latest live match state.
- Use one Redis Hash per match.
- Avoid storing JSON strings or multiple independent keys.
- Use `HSET` with computed values to support idempotent event replay.
- Redis remains a cache; historical data belongs in Cassandra.

# Apple Sports System Design
# Cassandra Data Model - Match Events

Unlike relational databases, Cassandra schemas are designed around **query patterns**, not entities.

A common interview principle is:

> **Model your tables based on how the application reads the data.**

---

# Step 1: Identify Access Patterns

Before designing the schema, identify the application's read queries.

---

## Current Match

```http
GET /matches/{matchId}
```

This request is served entirely from Redis.

No Cassandra lookup is required.

---

## Match Timeline

```http
GET /matches/{matchId}/events
```

Need:

- Goals
- Fouls
- Timeouts
- Substitutions
- Play-by-play events

All ordered chronologically.

---

## Match Statistics

```http
GET /matches/{matchId}/stats
```

Need the latest statistics for a match.

(Designed in a separate table.)

---

## Historical Timeline

```http
GET /matches/{matchId}/events?page=2
```

Need efficient pagination through historical events.

---

# Observation

Every timeline query begins with:

```
matchId
```

This naturally becomes the Cassandra partition key.

---

# Match Events Table

```sql
CREATE TABLE match_events (

    match_id text,

    event_time timestamp,

    event_id text,

    event_type text,

    team_id text,

    player_id text,

    payload text,

    PRIMARY KEY ((match_id), event_time)
);
```

---

# Partition Key

```
match_id
```

Why?

Our primary query is:

```
Return every event
for Match NBA123
```

Using `match_id` as the partition key stores all events for a match together.

Advantages:

- Efficient partition lookup.
- No cross-partition scan.
- Excellent locality for timeline queries.

---

# Clustering Key

```
event_time
```

Within each partition, Cassandra stores rows ordered by the clustering key.

Example:

```
Partition

NBA123

↓

20:01 Goal

↓

20:04 Timeout

↓

20:07 Goal

↓

20:48 Final
```

The timeline is naturally stored in chronological order.

No additional sorting is required.

---

# Descending Order

If the application usually shows the latest event first, Cassandra can store rows in descending order.

Example:

```sql
WITH CLUSTERING ORDER BY (event_time DESC);
```

Now the newest events appear first.

Useful for:

- Live timelines
- Infinite scrolling
- Latest match activity

---

# Why Not Partition By Team?

Example:

```
Partition

Lakers
```

Problem:

A single team participates in thousands of matches.

Partitions become very large.

Most queries are not:

```
Show every Lakers event
```

They are:

```
Show events for Match NBA123
```

Therefore `match_id` is the better partition key.

---

# Why Not Use eventId As The Clustering Key?

The primary query is:

```
Show timeline
```

not

```
Find Event EVT-123
```

Using `event_time` naturally supports timeline ordering.

---

# Partition Size

Typical sports matches generate:

- Hundreds of events
- Occasionally a few thousand events

This creates well-sized Cassandra partitions.

There is no hotspot concern because each match is stored independently.

---

# Event Payload

Fields such as:

- Event Type
- Team
- Player
- Additional metadata

can either be stored as separate columns or as a serialized payload depending on query requirements.

Example:

```json
{
   "eventType":"GOAL",
   "player":"LeBron James",
   "points":2
}
```

---

# Idempotency

Kafka provides at-least-once delivery.

The same event may be replayed.

Every event therefore contains a unique:

```
eventId
```

If the application receives the same event again:

```
Already Processed?

↓

Ignore Duplicate
```

This prevents duplicate historical records.

---

# Example Query

```sql
SELECT *

FROM match_events

WHERE match_id='NBA123';
```

Returns:

```
Goal

↓

Timeout

↓

Goal

↓

Final
```

Already ordered by time.

No joins.

No sorting.

Exactly what Cassandra is optimized for.

---

# Interview Questions

### Q: Why use `matchId` as the partition key?

> The primary access pattern is retrieving all events for a specific match. Using `matchId` as the partition key stores all events for that match together, allowing efficient reads without scanning multiple partitions.

---

### Q: Why use `eventTime` as the clustering key?

> Timeline queries require events in chronological order. Cassandra automatically orders rows within a partition by the clustering key, allowing efficient sequential reads without additional sorting.

---

### Q: Why not use `eventId` as the clustering key?

> The application retrieves complete timelines rather than individual events. Ordering by `eventTime` directly supports the primary query pattern.

---

### Q: Why doesn't this create oversized partitions?

> A sports match generates only hundreds or a few thousand events, making `matchId` an excellent partition key with well-balanced partition sizes.

---

# Key Takeaways

- Cassandra schemas are designed around query patterns.
- `matchId` is the partition key because nearly every timeline query begins with a match.
- `eventTime` is the clustering key to naturally order events chronologically.
- Timeline queries require no joins or sorting.
- Each match creates a well-sized Cassandra partition.
- Kafka replay requires idempotent writes using a unique business `eventId`.

# Apple Sports System Design
# Cassandra Data Model - Match Statistics

Unlike the `match_events` table, which stores every event, the `match_statistics` table stores the **latest aggregated statistics** for each match.

This table is a **materialized view** maintained by the Statistics Processor.

---

# Access Pattern

Primary query:

```http
GET /matches/{matchId}/stats
```

The API needs the current statistics:

- Home Score
- Away Score
- Rebounds
- Assists
- Fouls
- Possession
- Other live statistics

The API does **not** need every historical event.

---

# Two Possible Designs

## Option A – Aggregate On Read ❌

Store every statistics event.

Example:

```
Match123

↓

Rebound

↓

Assist

↓

Rebound

↓

Foul

↓

Timeout
```

When the API receives:

```http
GET /matches/123/stats
```

it must:

```
Read all events

↓

Aggregate

↓

Return Response
```

Example:

```java
rebounds = count(eventType == REBOUND);
assists  = count(eventType == ASSIST);
fouls    = count(eventType == FOUL);
```

Problems:

- Every request scans historical events.
- Higher latency.
- Expensive for millions of read requests.
- Poor scalability.

---

## Option B – Aggregate On Write (Recommended)

Instead of calculating statistics during every API request, calculate them once as events arrive.

```
Kafka Event

↓

Statistics Processor

↓

Update match_statistics

↓

API Reads Snapshot
```

Example:

After processing a rebound event:

```
match_statistics

Match123

homeRebounds = 11

awayRebounds = 9

homeAssists = 15

awayAssists = 12
```

Now the API simply executes:

```sql
SELECT *

FROM match_statistics

WHERE match_id='NBA123';
```

One row.

Response returned immediately.

---

# Important Clarification

The aggregation **still happens**.

The difference is **when** it happens.

### Aggregate On Read

```
Every API Request

↓

Read Historical Events

↓

Compute Statistics
```

---

### Aggregate On Write

```
Every Kafka Event

↓

Update Running Statistics

↓

Store Latest Snapshot
```

The API only performs a simple lookup.

---

# Why Aggregate On Write?

Suppose:

```
1000 Match Events

5 Million API Requests
```

### Aggregate On Read

Every request scans:

```
1000 Events

↓

Aggregate

↓

Return
```

This results in billions of event inspections.

---

### Aggregate On Write

Each event updates statistics exactly once.

```
1000 Events

↓

1000 Updates
```

Then:

```
5 Million Reads

↓

Read One Row
```

Much more efficient.

This shifts computation from the **read path** to the **write path**, which is a common pattern for read-heavy systems.

---

# Materialized View

The `match_statistics` table is a materialized view of the immutable event stream.

```
Kafka Events

↓

Statistics Processor

↓

Materialized View

(match_statistics)

↓

API Reads
```

Historical events remain in `match_events`.

Current statistics are stored in `match_statistics`.

---

# Schema

```sql
CREATE TABLE match_statistics (

    match_id text,

    home_score int,

    away_score int,

    home_rebounds int,

    away_rebounds int,

    home_assists int,

    away_assists int,

    home_fouls int,

    away_fouls int,

    possession_home decimal,

    possession_away decimal,

    last_updated timestamp,

    PRIMARY KEY (match_id)
);
```

There is no clustering key because each match has exactly one statistics row.

---

# Updating Statistics

Every Kafka event updates the materialized view.

Example:

```
Rebound Event

↓

Statistics Processor

↓

Update match_statistics
```

Latest values overwrite previous values.

---

# Idempotent Updates

Kafka provides at-least-once delivery.

The same event may be replayed.

Avoid increment operations such as:

```sql
UPDATE match_statistics

SET home_rebounds = home_rebounds + 1;
```

Replay would produce:

```
10

↓

11

↓

12
```

Incorrect.

Instead, the Statistics Processor computes the latest state and writes:

```sql
UPDATE match_statistics

SET home_rebounds = 11;
```

Replay becomes:

```
11

↓

11
```

The operation is idempotent.

---

# Statistics Processor

For this design, a dedicated Kafka consumer is sufficient.

```
Kafka

↓

Statistics Processor

↓

match_statistics
```

The processor consumes events ordered by `matchId` and continuously updates the latest statistics.

This approach works well because:

- Statistics are simple incremental calculations.
- Events for a match are processed sequentially.
- No complex windowing or stream joins are required.

---

# When Would You Use Apache Flink?

A stream-processing framework such as Apache Flink becomes useful when processing requires:

- Event-time processing
- Sliding or tumbling windows
- Watermarks
- Late event handling
- Stream joins
- Complex real-time analytics

Examples:

- Average possession over the last five minutes.
- Rolling shooting percentage.
- Top scorer during the current quarter.
- Live leaderboards across multiple matches.

These use cases are beyond the needs of this system.

For maintaining current match statistics, a Kafka consumer is simpler and more appropriate.

---

# Interview Questions

### Q: Why not calculate statistics for every API request?

> Because the system is highly read-heavy. Computing statistics once as Kafka events arrive and storing a materialized view makes reads extremely fast while avoiding repeated aggregation of historical events.

---

### Q: Is the system still aggregating statistics?

> Yes. The aggregation happens incrementally during event processing (write time), not during every API request (read time).

---

### Q: Why not use Apache Flink?

> The required calculations are straightforward and keyed by `matchId`. A Kafka consumer can efficiently maintain the latest statistics. Flink would be introduced only if future requirements included event-time windows, stream joins, or more advanced analytics.

---

# Key Takeaways

- Store current statistics as a materialized view.
- Perform aggregation during event processing rather than API requests.
- The API performs a simple lookup instead of scanning historical events.
- Maintain idempotent updates because Kafka provides at-least-once delivery.
- A Kafka consumer is sufficient for simple per-match aggregations; Apache Flink is better suited for complex stream-processing workloads.

# Apple Sports System Design
# Cassandra Data Model - Match Statistics

Unlike the `match_events` table, which stores every event, the `match_statistics` table stores the **latest aggregated statistics** for each match.

This table is a **materialized view** maintained by the Statistics Processor.

---

# Access Pattern

Primary query:

```http
GET /matches/{matchId}/stats
```

The API needs the current statistics:

- Home Score
- Away Score
- Rebounds
- Assists
- Fouls
- Possession
- Other live statistics

The API does **not** need every historical event.

---

# Two Possible Designs

## Option A – Aggregate On Read ❌

Store every statistics event.

Example:

```
Match123

↓

Rebound

↓

Assist

↓

Rebound

↓

Foul

↓

Timeout
```

When the API receives:

```http
GET /matches/123/stats
```

it must:

```
Read all events

↓

Aggregate

↓

Return Response
```

Example:

```java
rebounds = count(eventType == REBOUND);
assists  = count(eventType == ASSIST);
fouls    = count(eventType == FOUL);
```

Problems:

- Every request scans historical events.
- Higher latency.
- Expensive for millions of read requests.
- Poor scalability.

---

## Option B – Aggregate On Write (Recommended)

Instead of calculating statistics during every API request, calculate them once as events arrive.

```
Kafka Event

↓

Statistics Processor

↓

Update match_statistics

↓

API Reads Snapshot
```

Example:

After processing a rebound event:

```
match_statistics

Match123

homeRebounds = 11

awayRebounds = 9

homeAssists = 15

awayAssists = 12
```

Now the API simply executes:

```sql
SELECT *

FROM match_statistics

WHERE match_id='NBA123';
```

One row.

Response returned immediately.

---

# Important Clarification

The aggregation **still happens**.

The difference is **when** it happens.

### Aggregate On Read

```
Every API Request

↓

Read Historical Events

↓

Compute Statistics
```

---

### Aggregate On Write

```
Every Kafka Event

↓

Update Running Statistics

↓

Store Latest Snapshot
```

The API only performs a simple lookup.

---

# Why Aggregate On Write?

Suppose:

```
1000 Match Events

5 Million API Requests
```

### Aggregate On Read

Every request scans:

```
1000 Events

↓

Aggregate

↓

Return
```

This results in billions of event inspections.

---

### Aggregate On Write

Each event updates statistics exactly once.

```
1000 Events

↓

1000 Updates
```

Then:

```
5 Million Reads

↓

Read One Row
```

Much more efficient.

This shifts computation from the **read path** to the **write path**, which is a common pattern for read-heavy systems.

---

# Materialized View

The `match_statistics` table is a materialized view of the immutable event stream.

```
Kafka Events

↓

Statistics Processor

↓

Materialized View

(match_statistics)

↓

API Reads
```

Historical events remain in `match_events`.

Current statistics are stored in `match_statistics`.

---

# Schema

```sql
CREATE TABLE match_statistics (

    match_id text,

    home_score int,

    away_score int,

    home_rebounds int,

    away_rebounds int,

    home_assists int,

    away_assists int,

    home_fouls int,

    away_fouls int,

    possession_home decimal,

    possession_away decimal,

    last_updated timestamp,

    PRIMARY KEY (match_id)
);
```

There is no clustering key because each match has exactly one statistics row.

---

# Updating Statistics

Every Kafka event updates the materialized view.

Example:

```
Rebound Event

↓

Statistics Processor

↓

Update match_statistics
```

Latest values overwrite previous values.

---

# Idempotent Updates

Kafka provides at-least-once delivery.

The same event may be replayed.

Avoid increment operations such as:

```sql
UPDATE match_statistics

SET home_rebounds = home_rebounds + 1;
```

Replay would produce:

```
10

↓

11

↓

12
```

Incorrect.

Instead, the Statistics Processor computes the latest state and writes:

```sql
UPDATE match_statistics

SET home_rebounds = 11;
```

Replay becomes:

```
11

↓

11
```

The operation is idempotent.

---

# Statistics Processor

For this design, a dedicated Kafka consumer is sufficient.

```
Kafka

↓

Statistics Processor

↓

match_statistics
```

The processor consumes events ordered by `matchId` and continuously updates the latest statistics.

This approach works well because:

- Statistics are simple incremental calculations.
- Events for a match are processed sequentially.
- No complex windowing or stream joins are required.

---

# When Would You Use Apache Flink?

A stream-processing framework such as Apache Flink becomes useful when processing requires:

- Event-time processing
- Sliding or tumbling windows
- Watermarks
- Late event handling
- Stream joins
- Complex real-time analytics

Examples:

- Average possession over the last five minutes.
- Rolling shooting percentage.
- Top scorer during the current quarter.
- Live leaderboards across multiple matches.

These use cases are beyond the needs of this system.

For maintaining current match statistics, a Kafka consumer is simpler and more appropriate.

---

# Interview Questions

### Q: Why not calculate statistics for every API request?

> Because the system is highly read-heavy. Computing statistics once as Kafka events arrive and storing a materialized view makes reads extremely fast while avoiding repeated aggregation of historical events.

---

### Q: Is the system still aggregating statistics?

> Yes. The aggregation happens incrementally during event processing (write time), not during every API request (read time).

---

### Q: Why not use Apache Flink?

> The required calculations are straightforward and keyed by `matchId`. A Kafka consumer can efficiently maintain the latest statistics. Flink would be introduced only if future requirements included event-time windows, stream joins, or more advanced analytics.

---

# Key Takeaways

- Store current statistics as a materialized view.
- Perform aggregation during event processing rather than API requests.
- The API performs a simple lookup instead of scanning historical events.
- Maintain idempotent updates because Kafka provides at-least-once delivery.
- A Kafka consumer is sufficient for simple per-match aggregations; Apache Flink is better suited for complex stream-processing workloads.

# Apple Sports System Design
# Cassandra Data Model - User Subscriptions

One of the most common interview scenarios is designing user subscriptions for notifications.

Unlike relational databases, Cassandra does **not** support efficient joins.

Instead, Cassandra schemas are designed around **query patterns**, often resulting in multiple denormalized tables.

---

# Requirements

Users should be able to:

- Follow favorite teams.
- Unfollow teams.
- View all teams they follow.

The Notification Service should be able to:

- Find every user following a team when a score update occurs.

---

# Access Patterns

## Query 1

Retrieve all teams followed by a user.

```http
GET /users/{userId}/subscriptions
```

Example response:

```
Lakers

Chiefs

Yankees
```

---

## Query 2

A score update occurs.

```
Lakers scored.
```

Need to determine:

```
Which users follow the Lakers?
```

This is a completely different lookup.

---

# Why Two Tables?

A relational database might use:

```
Users

Teams

Subscriptions
```

and perform joins.

Cassandra does not support joins efficiently.

Instead, each access pattern gets its own table.

This is called **query-driven denormalization**.

---

# Table 1 - User Subscriptions

Purpose:

Retrieve all teams followed by a user.

Schema:

```sql
CREATE TABLE user_subscriptions (

    user_id text,

    team_id text,

    subscribed_at timestamp,

    PRIMARY KEY ((user_id), team_id)

);
```

---

# Partition Key

```
user_id
```

Reason:

Primary query:

```
Return all teams followed by User123.
```

All subscriptions for a user are stored together.

---

# Clustering Key

```
team_id
```

Allows multiple teams to exist within the user's partition.

Example:

```
Partition

User123

↓

Chiefs

↓

Lakers

↓

Yankees
```

---

# Query

```sql
SELECT *

FROM user_subscriptions

WHERE user_id='User123';
```

Returns:

```
Chiefs

Lakers

Yankees
```

---

# Problem

The Notification Service asks:

```
Who follows the Lakers?
```

The `user_subscriptions` table cannot answer this efficiently.

We need another table optimized for the reverse lookup.

---

# Table 2 - Team Followers

Purpose:

Retrieve all users following a particular team.

Schema:

```sql
CREATE TABLE team_followers (

    team_id text,

    user_id text,

    subscribed_at timestamp,

    PRIMARY KEY ((team_id), user_id)

);
```

---

# Partition Key

```
team_id
```

Reason:

Primary query:

```
Return every follower of the Lakers.
```

All followers for a team are colocated within the same partition.

---

# Clustering Key

```
user_id
```

Allows multiple followers to exist within the team's partition.

Example:

```
Partition

Lakers

↓

User1

↓

User2

↓

User3

↓

User4
```

---

# Query

```sql
SELECT *

FROM team_followers

WHERE team_id='Lakers';
```

Returns every follower of the Lakers.

This directly supports notification fan-out.

---

# Notification Flow

When a score update occurs:

```
Goal Event

↓

Notification Processor

↓

Query team_followers

↓

Retrieve User IDs

↓

Push Notifications
```

No joins are required.

---

# Duplicate Data

Notice that subscription information exists in two tables.

```
user_subscriptions

team_followers
```

This duplication is intentional.

Cassandra favors **denormalization** to optimize reads.

Storage is inexpensive compared to performing expensive joins or secondary lookups.

---

# Writing Data

When a user follows a team:

```
User123

↓

Follow Lakers
```

The application writes to both tables.

```
user_subscriptions

AND

team_followers
```

This allows both read paths to remain efficient.

---

# Large Teams

Suppose:

```
Lakers

↓

5 Million Followers
```

A single Cassandra partition could become very large.

A common optimization is bucketed partitioning.

Example:

Partition Key:

```
(team_id, bucket)
```

Result:

```
Lakers

Bucket 1

↓

100,000 Followers

----------------

Bucket 2

↓

100,000 Followers

----------------

Bucket 3

↓

100,000 Followers
```

Multiple notification workers can process different buckets in parallel.

For the initial system design, a single partition per team is acceptable.

Bucketing can be introduced later if extremely popular teams create oversized partitions.

---

# Redis Optimization

At very large scale, notification fan-out can be optimized further.

Instead of querying Cassandra for every notification:

```
Goal Event

↓

Notification Processor

↓

Redis

↓

Follower List

↓

Worker Pool

↓

Apple Push Notification Service (APNs)
```

Redis caches follower lists for hot teams.

Cassandra remains the durable source of truth.

Redis reduces lookup latency during major sporting events.

This optimization is usually discussed only if the interviewer asks how the system could scale further.

---

# Interview Questions

### Q: Why use two tables instead of one?

> Cassandra schemas are designed around query patterns rather than normalization. Since the application needs both **User → Teams** and **Team → Users** lookups, maintaining two denormalized tables provides efficient reads for both access patterns.

---

### Q: Isn't this duplicate data?

> Yes. Cassandra intentionally favors denormalization. Duplicate data is maintained to optimize read performance because joins are not efficient.

---

### Q: Why partition `user_subscriptions` by `userId`?

> The primary query retrieves all subscriptions for a user. Partitioning by `userId` colocates all of a user's subscriptions within a single partition.

---

### Q: Why partition `team_followers` by `teamId`?

> The Notification Service needs to retrieve all followers of a team when an event occurs. Partitioning by `teamId` makes this lookup efficient.

---

### Q: What happens if a team has millions of followers?

> A single partition may become too large. A common optimization is bucketing, where the partition key becomes `(teamId, bucket)` and followers are distributed across multiple partitions, allowing notification workers to process buckets in parallel.

---

# Final Cassandra Tables

```
match_events
```

Stores immutable historical event data.

---

```
match_statistics
```

Stores the latest materialized statistics for each match.

---

```
user_subscriptions
```

Supports:

```
User

↓

Teams
```

---

```
team_followers
```

Supports:

```
Team

↓

Followers
```

---

# Key Takeaways

- Cassandra schemas are designed around access patterns.
- Denormalization is preferred over joins.
- `user_subscriptions` supports **User → Teams** lookups.
- `team_followers` supports **Team → Users** lookups.
- Duplicate data is intentional to optimize reads.
- Bucketing can be introduced later to avoid oversized partitions for extremely popular teams.
- Redis can be used as a cache for hot follower lists to further reduce notification latency.

# Apple Sports System Design
# User Subscriptions - PostgreSQL & Redis

Initially, it may seem reasonable to store user subscriptions in Cassandra.

However, after evaluating the workload, PostgreSQL is a better choice.

---

# Why PostgreSQL?

User subscriptions are transactional CRUD data.

Typical operations:

- Follow a team
- Unfollow a team
- View followed teams

Example:

```
User

↓

Follow Lakers

↓

INSERT Subscription
```

Later:

```
User

↓

Unfollow Lakers

↓

DELETE Subscription
```

These are simple transactional operations that fit naturally into a relational database.

---

# Expected Scale

Example:

```
5 Million Users

↓

Average 10 Teams Followed

↓

≈ 50 Million Subscription Rows
```

PostgreSQL can comfortably handle this scale with proper indexing.

This workload does not justify introducing Cassandra.

---

# PostgreSQL Schema

```sql
CREATE TABLE subscriptions (

    user_id BIGINT,

    team_id VARCHAR(50),

    created_at TIMESTAMP,

    PRIMARY KEY (user_id, team_id)

);
```

Indexes:

```sql
CREATE INDEX idx_user
ON subscriptions(user_id);

CREATE INDEX idx_team
ON subscriptions(team_id);
```

These indexes efficiently support both access patterns.

---

# Access Patterns

## Query 1

Retrieve teams followed by a user.

```sql
SELECT *

FROM subscriptions

WHERE user_id = 123;
```

---

## Query 2

Retrieve followers of a team.

```sql
SELECT *

FROM subscriptions

WHERE team_id = 'LAL';
```

The secondary index supports this lookup.

---

# Why Not Cassandra?

Cassandra is optimized for:

- Massive write throughput
- Immutable event data
- Time-series workloads
- Horizontal scalability

Subscriptions are different.

Characteristics:

- Moderate data volume
- Simple CRUD operations
- Frequent inserts and deletes
- Transactional consistency

PostgreSQL is a better fit.

---

# Why Redis?

Although PostgreSQL is the source of truth, the Notification Service should not query PostgreSQL every time a score changes.

Example:

```
Lakers Score

↓

Notification Service

↓

SELECT Followers

↓

Millions of Rows
```

Repeated database lookups create unnecessary load.

Instead, Redis maintains a cache of followers for hot teams.

---

# Redis Data Model

Example:

```
Key

team:LAL
```

Value:

```
Redis Set

↓

User1

User2

User3

...
```

Redis Sets naturally support adding and removing followers.

Commands:

```redis
SADD team:LAL user123

SREM team:LAL user123

SMEMBERS team:LAL
```

---

# Keeping Redis In Sync

When a user follows a team:

```
User

↓

Subscription Service

↓

PostgreSQL

↓

Publish Subscription Event

↓

Subscription Consumer

↓

Redis Updated
```

Example event:

```json
{
    "userId":123,
    "teamId":"LAL",
    "action":"FOLLOW"
}
```

Redis:

```redis
SADD team:LAL user123
```

---

# Unfollow Flow

```
User

↓

Subscription Service

↓

PostgreSQL

↓

Publish Event

↓

Redis
```

Redis executes:

```redis
SREM team:LAL user123
```

The cache remains synchronized with PostgreSQL.

---

# Why Publish An Event Instead Of Using CDC?

Since the Subscription Service owns all writes, it can publish an event immediately after successfully committing the database transaction.

Architecture:

```
Subscription Service

↓

PostgreSQL

↓

Kafka Event

↓

Redis
```

This is simpler than introducing Change Data Capture (CDC).

CDC tools such as Debezium become useful when:

- Legacy applications write directly to the database.
- Multiple systems modify the same tables.
- Database changes must be propagated without changing application code.

For this design, publishing an event directly from the service is simpler and easier to explain.

---

# Notification Flow

When a score update occurs:

```
Goal Event

↓

Notification Processor

↓

Redis

↓

Follower List

↓

Worker Pool

↓

Apple Push Notification Service (APNs)
```

No PostgreSQL query is required during notification fan-out.

PostgreSQL remains the source of truth.

Redis provides fast access to frequently used follower lists.

---

# Interview Questions

### Q: Why choose PostgreSQL instead of Cassandra?

> User subscriptions are transactional CRUD data with moderate scale. PostgreSQL provides ACID transactions, efficient indexing, and a simpler data model. Cassandra is better suited for high-volume immutable event data such as match history.

---

### Q: Why cache subscriptions in Redis?

> During major sporting events, the Notification Service must quickly identify followers for a team. Redis stores follower lists in memory, avoiding repeated database queries and significantly reducing notification latency.

---

### Q: Why publish subscription events instead of using CDC?

> Since the Subscription Service owns the write path, it can publish a Kafka event immediately after a successful database transaction. CDC is more appropriate when database changes originate from external or legacy systems that cannot publish events themselves.

---

# Final Design

| Component | Technology | Purpose |
|-----------|------------|---------|
| Subscriptions | PostgreSQL | Source of truth for user subscriptions |
| Subscription Events | Kafka | Propagate follow/unfollow changes |
| Follower Cache | Redis | Fast lookup for notification fan-out |
| Notification Service | Worker Pool | Sends push notifications through APNs |

---

# Key Takeaways

- PostgreSQL is the preferred database for subscription data.
- Redis caches follower lists for low-latency notification fan-out.
- The Subscription Service publishes Kafka events after successful database commits.
- Redis is updated asynchronously by consuming subscription events.
- PostgreSQL remains the source of truth, while Redis is a derived cache optimized for reads.


# Apple Sports System Design
# Backpressure, Consumer Lag & Scaling

One of the most common production challenges in an event-driven architecture is handling situations where producers generate events faster than consumers can process them.

This is known as **backpressure**.

---

# What Is Backpressure?

Backpressure occurs when:

```
Producer Rate

>

Consumer Processing Rate
```

Example:

```
Producer

100,000 events/sec

Consumer

10,000 events/sec
```

Every second:

```
90,000 Events
```

accumulate in Kafka.

---

# Why Kafka Handles Bursts Well

Unlike an in-memory queue, Kafka persists events to disk.

```
Producer

↓

Kafka
(Durable Log)

↓

Consumer
```

Kafka acts as a durable buffer between producers and consumers.

This allows producers and consumers to operate independently.

Temporary spikes in traffic do not immediately cause failures.

---

# Consumer Lag

The primary metric for detecting backpressure is **Consumer Lag**.

Consumer Lag is defined as:

> The difference between the latest offset written to a partition and the latest offset committed by the consumer.

Example:

Latest Kafka Offset:

```
5000
```

Consumer Offset:

```
4700
```

Consumer Lag:

```
5000 - 4700 = 300 Messages
```

The larger the lag, the farther the consumer is behind.

---

# Temporary Backpressure

Example:

```
Producer

12,000 events/sec

Consumer

11,500 events/sec
```

Consumer lag increases briefly.

Traffic eventually decreases.

Consumers process the backlog and catch up.

No manual intervention is required.

---

# Sustained Backpressure

Suppose:

```
Producer

100,000 events/sec

Consumer

10,000 events/sec
```

Consumer lag grows continuously:

```
0

↓

90K

↓

180K

↓

270K

↓

...
```

The consumer will never catch up.

Eventually:

- Live scores become stale.
- Notifications are delayed.
- User experience degrades.

At this point, the system must be scaled or optimized.

---

# Scaling Consumers

Kafka parallelism is determined by the number of partitions.

Example:

```
8 Partitions

↓

8 Consumers
```

To increase throughput:

```
16 Partitions

↓

16 Consumers
```

Now twice as many events can be processed in parallel.

---

# Important

Adding more consumers than partitions does **not** increase throughput.

Example:

```
8 Partitions

20 Consumers
```

Only:

```
8 Consumers
```

actively process data.

The remaining consumers remain idle.

Maximum consumer parallelism equals the number of partitions.

---

# Optimizing Consumer Processing

Sometimes the bottleneck is not Kafka—it is the consumer itself.

Example processing flow:

```
Consume Event

↓

Update Redis

↓

Update Cassandra

↓

Call External Service
```

If processing each event takes too long, throughput decreases.

Possible optimizations:

- Batch database writes.
- Reduce synchronous external calls.
- Cache frequently accessed data.
- Execute independent work in parallel.
- Optimize database queries.

---

# Independent Consumer Groups

Our architecture contains multiple consumer groups.

```
Kafka

        │

        ├────────► Score Processor

        │

        ├────────► Statistics Processor

        │

        └────────► Notification Processor
```

Each consumer group maintains its own offsets.

If the Notification Processor falls behind:

- Notifications are delayed.
- Live scores continue updating.
- Statistics continue processing.

Backpressure in one consumer group does not affect the others.

This is one of the major advantages of Kafka's consumer group model.

---

# Extreme Backpressure

Suppose consumer lag continues growing despite scaling.

Possible actions:

- Increase Kafka partitions.
- Add additional consumer instances.
- Optimize processing latency.
- Investigate downstream bottlenecks (Redis, Cassandra, external APIs).
- Temporarily deprioritize non-critical workloads such as notifications while keeping score processing real-time.

---

# Metrics To Monitor

Important production metrics include:

### Kafka

- Consumer Lag ⭐⭐⭐⭐⭐
- Producer Throughput
- Consumer Throughput
- Offset Commit Rate
- Consumer Rebalance Frequency

### Consumer

- Processing Latency
- Retry Rate
- Dead Letter Queue (DLQ) Rate

### Infrastructure

- Kafka Broker Disk Usage
- CPU Utilization
- Memory Utilization
- Network Throughput

Consumer Lag is typically the first metric that indicates the system is falling behind.

---

# Interview Questions

### Q: What is backpressure?

> Backpressure occurs when producers generate events faster than consumers can process them, causing Kafka to accumulate messages and consumer lag to increase.

---

### Q: How does Kafka handle backpressure?

> Kafka persists events to disk, allowing it to act as a durable buffer between producers and consumers. Temporary traffic spikes increase consumer lag but do not immediately cause message loss.

---

### Q: What is consumer lag?

> Consumer lag is the difference between the latest produced offset and the latest committed consumer offset. It indicates how far behind a consumer is in processing events.

---

### Q: What if consumer lag keeps increasing?

> Sustained growth in consumer lag indicates that consumers cannot keep up. Solutions include increasing partitions, adding consumer instances, optimizing processing logic, or removing downstream bottlenecks.

---

### Q: Why doesn't notification lag affect score processing?

> Kafka consumer groups maintain independent offsets. Each service consumes the same event stream independently, so delays in one consumer group do not impact others.

---

# Key Takeaways

- Backpressure occurs when producers outpace consumers.
- Kafka absorbs temporary traffic spikes by buffering events on disk.
- Consumer lag is the primary metric used to detect backpressure.
- Maximum consumer parallelism is limited by the number of Kafka partitions.
- Scaling consumers, increasing partitions, and optimizing processing are common ways to reduce lag.
- Independent Kafka consumer groups isolate failures and prevent one downstream service from blocking another.
- Monitoring consumer lag is critical for maintaining a healthy event-driven system.