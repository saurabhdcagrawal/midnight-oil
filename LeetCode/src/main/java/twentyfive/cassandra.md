# Cassandra Fundamentals – Apple Sports Interview Notes

## Goal

Be able to confidently answer:

> **Why would Apple Sports choose Cassandra instead of PostgreSQL?**

---

# 1. The Problem

Imagine Apple Sports receiving live events from providers such as:

- Opta
- Stats Perform
- Sportradar

Example events:

- Goal
- Yellow Card
- Red Card
- Shot
- Possession Update
- Player Statistics
- Match Clock Update

Peak traffic:

```
40,000–50,000 events/second
```

These events must be ingested with very low latency.

---

# 2. Recommended High-Level Architecture

Instead of writing directly to the database:

```
Provider
    ↓
PostgreSQL
```

Use an event-driven architecture:

```
Sports Data Provider
        ↓
      Kafka
        ↓
Stream Processing
        ↓
    Database
        ↓
      Clients
```

Kafka decouples producers from consumers and absorbs traffic spikes.

---

# 3. Why Kafka?

A single published event can be consumed by multiple downstream services.

```
                 Kafka
        /          |           \
       /           |            \
      ▼            ▼             ▼

Score Service   Stats Service   Notification Service
      │             │                  │
      ▼             ▼                  ▼
 Cassandra       Redis               APNs
```

Benefits:

- Decouples producers and consumers
- Buffers traffic spikes
- Multiple consumers process the same event
- Supports replay
- Enables independent scaling of services

---

# 4. Why Not PostgreSQL?

PostgreSQL is optimized for:

- ACID transactions
- Joins
- Foreign keys
- Complex SQL
- Referential integrity

Excellent for:

- User accounts
- Payments
- Banking
- Airline reservations

Challenges for massive sports event ingestion:

- Every insert updates indexes
- Transaction overhead
- Concurrency coordination (MVCC)
- Primarily scales vertically
- Less suitable for extremely high write throughput

---

# 5. Cassandra Philosophy

> **Writes are king.**

Cassandra is optimized for:

- Massive write throughput
- Horizontal scalability
- High availability
- Time-series/event data

Typical workloads:

- Sports events
- Logs
- IoT
- Metrics
- Messaging

---

# 6. Horizontal Scaling

Instead of one giant database server:

```
Node A
Node B
Node C
Node D
...
Node N
```

When traffic grows:

> Add more nodes.

This provides near-linear scalability.

---

# 7. Peer-to-Peer Architecture

Unlike many databases:

**There is NO master node.**

Every node can:

- Accept reads
- Accept writes

If one node fails:

```
Node A ❌

↓

Client communicates with

Node B
Node C
Node D
```

This provides high availability.

---

# 8. Partitioning

Data is distributed across multiple nodes.

Example:

```
Node 1
Matches 1–20M

Node 2
Matches 20–40M

Node 3
Matches 40–60M
```

A hash of the partition key determines which node stores the data.

---

# 9. Consistent Hashing

```
Partition Key
      ↓
Hash
      ↓
Token
      ↓
Node
```

When a new node is added:

Only a small amount of data moves.

Benefits:

- Efficient horizontal scaling
- Minimal data redistribution

---

# 10. Replication

Example:

Replication Factor = 3

```
Node A
   ↓
Node B
   ↓
Node C
```

Benefits:

- Fault tolerance
- High availability
- No single point of failure

---

# 11. Cassandra Write Path

When an event arrives:

```
Goal Event
      ↓
Coordinator Node
      ↓
Commit Log
      ↓
Memtable (Memory)
      ↓
ACK Returned
```

Later:

```
Memtable
      ↓
SSTables (Disk)
```

Why is this fast?

- Write is acknowledged quickly.
- Disk organization happens asynchronously.

This makes Cassandra highly write-optimized.

---

# 12. Cassandra Read Path

```
Client
   ↓
Coordinator
   ↓
Replica Nodes
   ↓
Merge Results
   ↓
Return Response
```

Reads are more expensive than writes because data may need to be retrieved from multiple replicas.

---

# 13. Which Database for Which Workload?

## PostgreSQL

Store:

- User Accounts
- Authentication
- Payments
- Subscriptions

Reason:

- ACID transactions
- Strong consistency
- Constraints
- Relational data

---

## Cassandra

Store:

- Live Match Events
- Live Scores
- Player Timelines
- Historical Match Events
- Time-series sports data

Reason:

- Massive writes
- Predictable query patterns
- Horizontal scalability
- High availability

---

## Redis

Store:

- Current Score
- Live Standings
- Leaderboards
- Hot Matches

Reason:

- Extremely fast in-memory reads

---

## Kafka

Use as the transport layer for events.

---

# 14. Historical Statistics

It depends on the workload.

### Cassandra is a good fit for:

- Match history
- Season statistics
- Player timelines
- Last 100 matches

These are predictable, time-series queries.

### PostgreSQL is better for:

- Complex joins
- Multi-table reporting
- Strong transactional workloads

---

# 15. Golden Rule

Never ask:

> Which database is better?

Instead ask:

> Which database is better for this workload?

---

# 16. Interview Tip ⭐⭐⭐⭐⭐

If asked:

> Why Cassandra?

Don't immediately answer.

Instead ask:

> What are the query patterns?

This demonstrates senior-level thinking because Cassandra uses **query-first** data modeling.

---

# 17. Key Takeaways

Choose Cassandra when you need:

- Massive write throughput
- Horizontal scalability
- High availability
- Time-series/event storage
- Predictable query patterns

Avoid Cassandra when you need:

- Complex joins
- Multi-row transactions
- Foreign keys
- Strong ACID guarantees
- Arbitrary SQL queries

---

# Sample Interview Answer

**Q: Why would Apple Sports choose Cassandra instead of PostgreSQL?**

> Apple Sports ingests a continuous stream of high-volume sports events such as goals, substitutions, player statistics, and match updates. This workload is append-heavy with predictable access patterns and requires horizontal scalability and high availability. Cassandra is optimized for these workloads because it distributes writes across many nodes, provides near-linear scalability, and delivers very high write throughput. PostgreSQL is excellent for transactional systems with complex relationships and joins, but for massive real-time event ingestion, Cassandra is generally a better fit.

---

# Next Session

- Query-First Data Modeling
- Primary Keys
- Partition Keys
- Clustering Columns
- Composite Primary Keys
- Denormalization
- Time-Series Table Design
- Apple Sports Schema Examples


# Cassandra Data Modeling (Query-First Design)

## The Biggest Difference Between SQL and Cassandra

### SQL

```
Entities
    ↓
Normalize
    ↓
Relationships
    ↓
Queries
```

Example:

```
User
Order
Product
Payment
```

Then write SQL using JOINs.

---

### Cassandra

```
Queries
    ↓
Tables
    ↓
Data Model
```

Cassandra tables are designed **for specific query patterns**, not for normalization.

---

# First Question to Ask

Before designing any Cassandra table, ask:

> **"What are the query access patterns?"**

Example interview response:

> **Before I design the schema, I'd like to understand the query access patterns because Cassandra data modeling is driven by how the data will be queried rather than by normalization.**

This is a senior-level answer.

---

# Why Query Patterns Matter

Suppose Apple Sports needs to support the following queries.

### Query 1

```
Show all events for Match 123
ordered by event time.
```

### Query 2

```
Show all matches for Liverpool.
```

### Query 3

```
Show all goals scored by Messi.
```

Each query may require a **different Cassandra table**.

---

# Query 1

## Requirement

Show all events for a match in chronological order.

### Table

```
MatchEventsByMatch
```

### Partition Key

```
matchId
```

Why?

All events for a match should be stored together.

### Clustering Column

```
eventTimestamp
```

Why?

Cassandra stores rows **already sorted** inside a partition.

### Columns

```
eventId
eventType
eventDescription
playerId
teamId
```

---

# Query 2

## Requirement

Show all matches for a team.

### Table

```
MatchesByTeam
```

### Partition Key

```
teamId
```

### Clustering Column

```
matchDate DESC
```

Why DESC?

Sports apps typically display:

- Latest match
- Previous match
- Older matches

### Columns

```
matchId
opponentTeamId
homeAway
leagueId
status
score
```

---

# Query 3

## Requirement

Show all goals scored by a player.

### Table

```
GoalsByPlayer
```

### Partition Key

```
playerId
```

### Clustering Column

```
goalTimestamp DESC
```

### Columns

```
matchId
againstTeamId
goalType
minute
leagueId
seasonId
```

---

# Denormalization

Notice the same goal is stored in multiple tables.

```
MatchEventsByMatch

AND

GoalsByPlayer
```

This is intentional.

---

## SQL Philosophy

```
Normalize
Reduce Duplication
Use JOINs
```

---

## Cassandra Philosophy

```
Duplicate Data
Optimize Queries
Avoid JOINs
```

Storage is cheap.

Latency is expensive.

---

# Another Interview Question

Suppose Apple asks:

```
Show me every match Messi played during Season 2025.
```

Can the existing tables answer this?

No.

Why?

- GoalsByPlayer → only goals
- MatchesByTeam → by team
- MatchEventsByMatch → by match

None are optimized for this query.

Create another table.

### Table

```
MatchesByPlayerSeason
```

### Partition Key

```
playerId
```

### Clustering Columns

```
seasonId
matchDate DESC
```

### Columns

```
matchId
teamId
opponentTeamId
minutesPlayed
result
```

Again, the table exists solely to support a query.

---

# Golden Rule

In SQL:

> Number of tables ≈ Number of entities.

In Cassandra:

> Number of tables ≈ Number of query patterns.

This is the biggest mindset shift.

---

# Why IDs Instead of Names?

Prefer:

```
matchId
teamId
playerId
leagueId
```

Instead of:

```
matchName
playerName
teamName
```

Reasons:

- IDs are stable.
- Names can change.
- IDs are smaller.
- Other services can enrich the response.

---

# Interview Tips ⭐⭐⭐⭐⭐

Always start by asking:

> What are the query access patterns?

Then identify:

- Partition Key
- Clustering Column
- Remaining columns

Finally explain:

> "This table is optimized specifically for this query."

---

# Key Takeaways

- Cassandra is **query-first**, not entity-first.
- Design one table per major query pattern.
- Duplicate data when necessary.
- Avoid joins.
- Partition Key determines **where** data is stored.
- Clustering Column determines **how data is ordered within a partition**.
- Think in IDs rather than names.
- Always justify your design using the expected query pattern.

# Cassandra Data Modeling - Additional Concepts

## Why Use IDs Instead of Names?

When designing Cassandra tables, prefer storing IDs instead of names.

Example:

Instead of:

```
matchName
teamName
leagueName
playerName
```

Store:

```
matchId
teamId
leagueId
playerId
```

### Why?

- IDs are immutable.
- Names may change.
- IDs consume less storage.
- IDs are easier to reference across services.
- Avoids inconsistencies when names change.

---

# Where Do Team Names and League Names Come From?

There are multiple approaches.

---

## Approach 1 - Separate Metadata Service (Normalized)

Store only IDs in Cassandra.

```
MatchesByTeam

teamId
matchId
opponentTeamId
leagueId
```

The application enriches the response by querying another service.

```
Client
    ↓
Match Service
    ↓
Cassandra
    ↓
teamId = 20
leagueId = 5
    ↓
Team Service
League Service
    ↓
PostgreSQL
    ↓
Liverpool
Premier League
```

### Advantages

- Single source of truth
- Easy to update names
- No duplicated metadata

### Disadvantages

- Additional network call
- Slightly higher latency

---

## Approach 2 - Denormalize into Cassandra

Store names directly inside the table.

```
MatchesByTeam

Partition Key
-------------
teamId

Clustering Column
-----------------
matchDate DESC

Columns
-------
matchId
opponentTeamId
opponentTeamName
leagueId
leagueName
homeAway
score
```

### Advantages

- One database read
- Lowest latency
- No additional service lookup

### Disadvantages

- Duplicate data
- Team or league name updates require updating multiple tables

This approach is perfectly acceptable in Cassandra because Cassandra favors denormalization.

---

## Approach 3 - Redis Cache (Most Common)

Store IDs in Cassandra.

Store metadata in Redis.

```
Client
    ↓
Match Service
    ↓
Cassandra

teamId = 20
leagueId = 5

    ↓

Redis

20 → Liverpool
5  → Premier League
```

Advantages:

- Very low latency
- Small metadata fits well in memory
- Minimal duplication
- Easy to update cache

This is a very common production architecture.

---

# Which Approach Would Apple Likely Use?

A realistic architecture is:

```
                Cassandra

matchId
teamId
leagueId
playerId

        ↓

      Redis

teamId → Liverpool

leagueId → Premier League

playerId → Messi
```

This provides:

- Compact storage
- Fast lookups
- Low latency
- Easy metadata updates

---

# What if Redis Goes Down?

Redis is **NOT** the source of truth.

Redis is only a cache.

The request should still succeed.

Normal flow:

```
Client
    ↓
Match Service
    ↓
Cassandra
    ↓
Redis
    ↓
Response
```

Redis unavailable:

```
Client
    ↓
Match Service
    ↓
Cassandra
    ↓
Team Service
    ↓
PostgreSQL
    ↓
Response
```

The request is slower but still succeeds.

---

# Cache-Aside Pattern

This is the most common caching strategy.

### Cache Hit

```
Request
    ↓
Redis
    ↓
Response
```

### Cache Miss

```
Request
    ↓
Redis (Miss)
    ↓
Database
    ↓
Populate Redis
    ↓
Response
```

Redis is repopulated so future requests are fast again.

---

# Source of Truth

```
PostgreSQL

or

Cassandra
```

Source of truth depends on the data.

For example:

### PostgreSQL

- Teams
- Players
- Leagues
- Users
- Subscriptions

### Cassandra

- Match Events
- Live Scores
- Historical Sports Events
- Player Timelines

### Redis

- Frequently accessed metadata
- Current score
- Leaderboards
- Hot matches

Redis should never be the only copy of the data.

---

# What Does homeAway Mean?

Consider the table:

```
MatchesByTeam
```

Partition Key:

```
teamId
```

Suppose the partition is:

```
teamId = Liverpool
```

Rows:

| matchDate | opponentTeamId | homeAway | score |
|-----------|----------------|----------|-------|
| Aug 1 | Arsenal | HOME | 2-1 |
| Aug 8 | Chelsea | AWAY | 1-1 |
| Aug 15 | Everton | HOME | 3-0 |

Since the partition already represents Liverpool:

```
HOME
```

means

> Liverpool is the home team.

```
AWAY
```

means

> Liverpool is the away team.

Therefore we only need:

```
opponentTeamId
homeAway
```

instead of storing both:

```
homeTeamId
awayTeamId
```

This keeps each row compact while still supporting the query efficiently.

---

# Why Not Store Both Team IDs?

Both approaches are valid.

### Option 1

```
homeTeamId
awayTeamId
```

More explicit.

### Option 2

```
opponentTeamId
homeAway
```

Smaller rows because the partition key already identifies the team.

For a table partitioned by `teamId`, Option 2 is usually sufficient.

---

# Senior Interview Answer

If asked:

> Why didn't you store homeTeamId and awayTeamId?

A strong answer is:

> Since this table is partitioned by `teamId`, every row already belongs to a specific team. Therefore, storing only the `opponentTeamId` along with a `homeAway` flag is sufficient to reconstruct the fixture while keeping each row compact. If future query patterns require direct access to both team IDs, I can denormalize further and include both IDs.

---

# Key Takeaways

- Prefer IDs over names.
- Names can be retrieved through:
  - Metadata Service
  - Redis Cache
  - Denormalized Cassandra tables
- Redis is a cache, not the source of truth.
- Use the Cache-Aside pattern for cache failures.
- When partitioning by `teamId`, storing `opponentTeamId` and `homeAway` is usually enough.
- Cassandra encourages denormalization when it improves read performance.

# Cassandra Primary Keys, Partition Keys & Clustering Columns

## Cassandra PRIMARY KEY

Unlike SQL, the PRIMARY KEY in Cassandra defines **three things**:

- Primary Key
- Partition Key
- Clustering Column(s)

---

## Example 1

```sql
PRIMARY KEY(matchId)
```

### Primary Key

```
matchId
```

### Partition Key

```
matchId
```

### Clustering Columns

None

---

## Example 2

```sql
PRIMARY KEY(matchId, eventTimestamp)
```

### Primary Key

```
(matchId, eventTimestamp)
```

### Partition Key

```
matchId
```

### Clustering Column

```
eventTimestamp
```

---

### What does the Partition Key do?

The Partition Key determines:

- Which node stores the data
- Which partition the row belongs to

Think of it as:

```
Hash(matchId)

↓

Node B

↓

Partition
```

Every event belonging to the same match goes into the same partition.

---

### What does the Clustering Column do?

The Clustering Column determines the ordering **within** a partition.

Suppose:

```
matchId = 123
```

Rows are physically stored as:

```
Match 123

75'

76'

77'

78'

79'

80'
```

No sorting is required during reads.

---

## Easy Way to Remember

Imagine a filing cabinet.

### Partition Key

Chooses the drawer.

```
Drawer

Match 123
```

### Clustering Column

Determines how papers are arranged inside the drawer.

```
Match 123

↓

75'

76'

77'

78'

79'
```

---

# Composite Partition Keys

Example:

```sql
PRIMARY KEY(
    (leagueId, seasonId),
    matchDate,
    matchId
)
```

Notice the extra parentheses.

Everything inside the first parentheses becomes the Partition Key.

---

## Primary Key

```
(
 (leagueId, seasonId),
 matchDate,
 matchId
)
```

---

## Composite Partition Key

```
leagueId
seasonId
```

Both values are hashed together.

```
Hash(leagueId + seasonId)

↓

Partition
```

---

## Clustering Columns

```
matchDate

matchId
```

Rows are sorted:

1. By matchDate
2. If dates are equal, by matchId

---

## Example

Partition:

```
leagueId = EPL

seasonId = 2025
```

Rows:

| matchDate | matchId |
|-----------|----------|
| Aug 1 | M101 |
| Aug 1 | M102 |
| Aug 5 | M103 |
| Aug 10 | M104 |

Sorted first by date, then by matchId.

---

# Why Not Use Only seasonId?

Suppose the partition key is:

```
seasonId
```

Then:

```
Season 2025

↓

EPL Matches

NBA Games

NFL Games

MLB Games

...

100,000+ rows
```

Everything for the 2025 season ends up in one partition.

Problems:

- Large partition
- Poor data distribution
- More filtering during queries

---

# Why Not Use Only leagueId?

Suppose:

```
leagueId = EPL
```

Partition:

```
1992

1993

1994

...

2026
```

All seasons for the league end up in one partition.

Again, the partition continues growing over time.

---

# Why Use (leagueId, seasonId)?

Each league-season combination gets its own partition.

Examples:

```
(EPL, 2025)

↓

380 Matches
```

```
(EPL, 2026)

↓

380 Matches
```

```
(NBA, 2025)

↓

1230 Games
```

```
(MLB, 2025)

↓

2430 Games
```

Benefits:

- Better data distribution
- Smaller partitions
- Supports common query patterns

---

# Important Principle

Partition Keys should:

- Match the primary query pattern
- Evenly distribute data
- Avoid creating huge partitions

---

# Interview Question

**Why did you choose `(leagueId, seasonId)` instead of only `seasonId`?**

A strong answer:

> I chose a composite partition key because queries are typically scoped to both a league and a season. Using only `seasonId` would mix data from many leagues into one partition, while using only `leagueId` would accumulate multiple seasons in one partition. Combining both provides better data distribution and aligns with the primary access pattern.

---

# Summary

### Example 1

```sql
PRIMARY KEY(matchId)
```

Partition Key:

```
matchId
```

No Clustering Columns.

---

### Example 2

```sql
PRIMARY KEY(matchId, eventTimestamp)
```

Partition Key:

```
matchId
```

Clustering Column:

```
eventTimestamp
```

---

### Example 3

```sql
PRIMARY KEY(
    (leagueId, seasonId),
    matchDate,
    matchId
)
```

Composite Partition Key:

```
leagueId
seasonId
```

Clustering Columns:

```
matchDate
matchId
```

---

# Golden Rules

- The **Primary Key** consists of the **Partition Key** plus the **Clustering Column(s)**.
- The **Partition Key** determines **where** data is stored.
- The **Clustering Columns** determine **how rows are ordered within a partition**.
- Use a **Composite Partition Key** when a single column would create very large partitions or does not align with the primary query pattern.
- Always choose the partition key based on **query access patterns** and **data distribution**, not just the entities in your model.

# Cassandra Nodes vs Partitions

One of the most common interview questions is:

> **What is the difference between a Node and a Partition?**

Many people think they are the same.

They are not.

---

# Cassandra Cluster

A Cassandra cluster consists of multiple nodes.

```
Cluster
    │
    ├── Node A
    ├── Node B
    ├── Node C
    └── Node D
```

Each **Node** is a physical machine (or VM/container) running Cassandra.

---

# What is a Partition?

A partition is a **logical grouping of rows** that share the same Partition Key.

Example:

```sql
PRIMARY KEY(matchId, eventTimestamp)
```

Partition Key:

```
matchId
```

Suppose we have:

```
matchId = 100
```

Then every row belongs to the same partition.

```
Partition

matchId = 100

75'

76'

77'

78'

79'

80'
```

Another match creates another partition.

```
Partition

matchId = 101

5'

20'

45'

68'

90'
```

Every unique partition key creates a new partition.

---

# How Does Cassandra Know Where to Store a Partition?

Cassandra hashes the Partition Key.

```
Partition Key

↓

Hash()

↓

Token

↓

Node
```

Example:

```
matchId = 100

↓

Hash()

↓

Node A
```

```
matchId = 101

↓

Hash()

↓

Node C
```

```
matchId = 102

↓

Hash()

↓

Node B
```

---

# Important Difference

A node stores **many partitions**.

Example:

```
Node A

Partition(matchId = 100)

Partition(matchId = 245)

Partition(matchId = 350)

Partition(matchId = 1001)

...

Thousands of partitions
```

Similarly,

```
Node B

Partition(matchId = 101)

Partition(matchId = 102)

Partition(matchId = 500)

...
```

Therefore:

```
Node ≠ Partition
```

One node contains many partitions.

---

# Apple Sports Example

Suppose Apple stores data for:

```
10 Million Matches
```

Apple is **NOT** going to have:

```
10 Million Nodes
```

Instead they might have:

```
100 Cassandra Nodes
```

Each node stores hundreds of thousands of partitions.

```
Cluster

Node A
    Match100
    Match101
    Match250
    Match999
    ...

-----------------------------

Node B
    Match102
    Match300
    Match700
    ...

-----------------------------

Node C
    ...
```

---

# Replication

Earlier we discussed Replication Factor.

Suppose:

```
Replication Factor = 3
```

Partition:

```
matchId = 100
```

Primary copy:

```
Node A
```

Replica copies:

```
Node C

Node D
```

The partition exists on multiple nodes.

This provides:

- High Availability
- Fault Tolerance

---

# Visual Summary

```
Cluster
    │
    ├── Node A
    │      ├── Partition(matchId=100)
    │      ├── Partition(matchId=245)
    │      └── Partition(matchId=350)
    │
    ├── Node B
    │      ├── Partition(matchId=101)
    │      ├── Partition(matchId=500)
    │      └── Partition(matchId=700)
    │
    └── Node C
           ├── Partition(matchId=102)
           ├── Partition(matchId=999)
           └── Partition(matchId=1500)
```

---

# Interview Analogy

Think of a library.

```
Library

↓

Bookshelves

↓

Folders

↓

Documents
```

Equivalent Cassandra concepts:

```
Cluster

↓

Nodes (Bookshelves)

↓

Partitions (Folders)

↓

Rows (Documents)
```

A bookshelf contains many folders.

A node contains many partitions.

---

# Interview Answer

**Q: What is the difference between a Node and a Partition?**

> A **Node** is a physical machine (or VM/container) that is part of the Cassandra cluster. A **Partition** is a logical grouping of rows that share the same Partition Key. Cassandra hashes the Partition Key to determine which node stores that partition. A single node stores many partitions, and each partition may be replicated to multiple nodes depending on the configured replication factor.

---

# Key Takeaways

- A **Cluster** contains multiple **Nodes**.
- A **Node** is a physical Cassandra server.
- A **Partition** is a logical collection of rows sharing the same Partition Key.
- Cassandra hashes the Partition Key to determine which node stores the partition.
- One node stores many partitions.
- One partition can exist on multiple nodes because of replication.
- **Node ≠ Partition**.


# Wide Partitions in Cassandra

One of the biggest challenges in Cassandra is avoiding **wide partitions**.

A **wide partition** is a partition that becomes too large because too much data shares the same Partition Key.

Large partitions lead to:

- Slower reads
- Longer compactions
- Longer repairs
- More disk I/O
- Uneven data distribution

---

# Example

Suppose we create:

```sql
CREATE TABLE PlayerEventsByPlayer (

    playerId UUID,
    eventTimestamp TIMESTAMP,
    eventType TEXT,

    PRIMARY KEY(playerId, eventTimestamp)
);
```

Partition Key:

```
playerId
```

Suppose:

```
playerId = Messi
```

Now imagine storing:

- Every goal
- Every assist
- Every pass
- Every shot
- Every foul
- Every touch

for 20 years.

Everything goes into ONE partition.

```
Messi

↓

2006

2007

2008

...

2026
```

This partition continuously grows over time.

This is called a **Wide Partition**.

---

# Why is this a Problem?

A partition should remain reasonably sized.

Very large partitions cause:

- Large SSTables
- Expensive compactions
- Longer repair operations
- Slower reads
- Poor cache utilization

---

# Solution - Bucketing

Instead of:

```sql
PRIMARY KEY(playerId, eventTimestamp)
```

Bucket the data.

Example:

```sql
PRIMARY KEY(
    (playerId, seasonId),
    eventTimestamp
)
```

Now each season becomes its own partition.

```
Messi

Season 2023

↓

Partition
```

```
Messi

Season 2024

↓

Partition
```

```
Messi

Season 2025

↓

Partition
```

Each partition remains much smaller.

---

# Another Option

If queries are based on event type:

```
Show me every goal scored by Messi.
```

We can partition by:

```sql
PRIMARY KEY(
    (playerId, eventType),
    eventTimestamp
)
```

Partitions become:

```
Messi

GOAL
```

```
Messi

ASSIST
```

```
Messi

FOUL
```

Again, partitions remain smaller.

---

# Bucketing Trade-Off

Suppose we choose:

```sql
PRIMARY KEY(
    (playerId, seasonId),
    eventTimestamp
)
```

Now a query asks:

```
Show me every event from 2023 to 2025.
```

The data now exists in multiple partitions.

```
(Messi,2023)

(Messi,2024)

(Messi,2025)
```

One query can no longer retrieve everything.

---

# Does Cassandra Merge Multiple Partitions?

No.

Cassandra is optimized for fast lookups of individual partitions.

It does not efficiently perform range queries across multiple partition keys.

For example:

```sql
SELECT *
FROM PlayerEventsBySeason
WHERE playerId = 10
AND seasonId BETWEEN 2023 AND 2025;
```

This is **not** an efficient query because `seasonId` is part of the Partition Key.

---

# How Is This Usually Implemented?

The service layer performs multiple partition lookups.

```
Client

↓

Player Service

↓

Query (2023)

↓

Query (2024)

↓

Query (2025)

↓

Merge Results

↓

Sort (if required)

↓

Return Response
```

---

# Service Layer Example

```java
List<Event> events = new ArrayList<>();

for (int season = 2023; season <= 2025; season++) {

    events.addAll(
        repository.findByPlayerAndSeason(playerId, season)
    );
}

events.sort(byTimestamp);

return events;
```

---

# Can These Queries Run in Parallel?

Yes.

This is the preferred approach.

```
                 Player Service

                 /      |      \

             2023     2024     2025

                 \      |      /

                  Merge Results

                        ↓

                 Return Response
```

Using Java:

- CompletableFuture
- Virtual Threads
- Reactive Programming (WebFlux)

Instead of executing sequentially:

```
20ms

+

20ms

+

20ms
```

Parallel execution gives approximately:

```
20ms
```

(plus a small amount of merge overhead)

---

# What if Career Queries Become Common?

Suppose the application frequently asks:

```
Show me every event in Messi's career.
```

Now repeatedly querying many season partitions may become inefficient.

Instead, create another table optimized for that query.

Example:

```
CareerEventsByPlayer
```

This illustrates Cassandra's philosophy:

> One table per important query pattern.

---

# Interview Question

**Q: Why choose `(playerId, seasonId)` instead of just `playerId`?**

A strong answer:

> Using only `playerId` would create a continuously growing partition throughout the player's career, resulting in a wide partition. Since most application queries are season-specific, adding `seasonId` naturally buckets the data, keeps partitions manageable, and aligns with the primary query pattern.

---

# Interview Question

**Q: How would you retrieve data across multiple seasons?**

A strong answer:

> Since the data is bucketed by `(playerId, seasonId)`, each season resides in a separate partition. Cassandra does not efficiently query across multiple partition keys, so the service layer issues parallel queries—one per season—and merges the results before returning them. If multi-season or career-wide queries become a dominant access pattern, I would introduce another Cassandra table optimized specifically for those queries.

---

# Key Takeaways

- Avoid wide partitions.
- Bucket data using additional fields such as `seasonId`, `year`, or `month`.
- Cassandra optimizes for **fast partition lookups**, not cross-partition joins or scans.
- Multi-partition queries are typically coordinated by the application/service layer.
- Execute independent partition queries in parallel when possible.
- Design tables around **query access patterns**, even if it means maintaining multiple denormalized tables.

# Cassandra Write Path

One of the most common senior interview questions is:

> **What happens when a client writes data into Cassandra?**

Suppose the application receives a new sports event:

```json
{
    "matchId": 123,
    "playerId": 10,
    "eventType": "GOAL",
    "timestamp": "78:12"
}
```

The application executes:

```java
repository.save(event);
```

The write follows these steps.

---

# Step 1 - Coordinator Node

The client can send the request to **any Cassandra node**.

```
Application

↓

Node B
```

Node B becomes the **Coordinator Node**.

The Coordinator Node is responsible for:

- Receiving the request
- Calculating which nodes own the partition
- Forwarding the write
- Waiting for the configured consistency level
- Returning success to the client

The Coordinator Node does **not** necessarily store the data.

---

# Step 2 - Calculate the Partition

Suppose:

```sql
PRIMARY KEY(matchId, eventTimestamp)
```

Partition Key:

```
matchId
```

Cassandra hashes the partition key.

```
matchId

↓

Hash()

↓

Token

↓

Replica Nodes
```

Example:

```
matchId = 123

↓

Node C

Node D

Node E
```

The coordinator forwards the write to these replica nodes.

---

# Step 3 - Commit Log

Each replica first writes the data to the **Commit Log**.

```
Replica

↓

Commit Log (Disk)
```

The Commit Log is:

- Persistent
- Append-only
- Sequentially written

### Why?

Suppose power fails immediately after the write.

Without the Commit Log:

```
Memory

↓

Power Failure

↓

Data Lost
```

The Commit Log provides durability and crash recovery.

---

# Step 4 - MemTable

Immediately after writing to the Commit Log, Cassandra inserts the row into the **MemTable**.

```
Commit Log

↓

MemTable (RAM)
```

The MemTable is:

- In memory
- Sorted
- Mutable

The client write is now complete.

### Why not write directly to disk?

Random disk updates are slow.

Instead Cassandra accumulates many writes in memory before flushing.

---

# Step 5 - Flush

Eventually the MemTable reaches a configured threshold.

Example:

```
256 MB
```

The MemTable is flushed to disk.

```
MemTable

↓

Disk

↓

SSTable
```

The MemTable is cleared and a new empty MemTable is created.

---

# Step 6 - SSTables

An SSTable is an **immutable sorted data file** stored on disk.

Important characteristics:

- Immutable
- Sorted
- Persistent
- Stored on disk

Cassandra **never modifies an existing SSTable**.

---

# Why are SSTables Immutable?

Suppose:

```
Goal @ 10'

Goal @ 35'

Goal @ 75'
```

are already stored in an SSTable.

A new goal arrives:

```
Goal @ 82'
```

Cassandra does **NOT**:

```
Open SSTable

↓

Insert Row

↓

Save SSTable
```

Instead:

```
Commit Log

↓

MemTable

↓

Flush

↓

New SSTable
```

Random disk writes are avoided.

This is one reason Cassandra achieves extremely high write throughput.

---

# Step 7 - Compaction

Over time multiple SSTables accumulate.

```
SSTable 1

SSTable 2

SSTable 3

SSTable 4
```

If Cassandra had to read every SSTable, reads would become slower.

Therefore Cassandra performs **Compaction**.

```
SSTable 1

+

SSTable 2

+

SSTable 3

↓

Compaction

↓

New SSTable
```

Old SSTables are deleted after compaction.

Compaction is a background process.

Clients do not wait for it.

---

# Complete Write Path

```
Application

↓

Coordinator Node

↓

Replica Nodes

↓

Commit Log (Disk)

↓

MemTable (RAM)

↓

Flush

↓

Immutable SSTable

↓

Background Compaction
```

---

# Why Cassandra Writes are Fast

- Sequential disk writes
- Append-only Commit Log
- In-memory MemTable
- Immutable SSTables
- No random disk updates
- Background Compaction

---

# Interview Question

**Q: Why are SSTables immutable?**

A strong answer:

> SSTables are immutable to avoid expensive random disk updates. Every write is appended to the Commit Log and stored in the MemTable. When the MemTable is flushed, Cassandra creates a new SSTable rather than modifying an existing one. This enables high write throughput and simplifies concurrency. Background compaction later merges multiple SSTables to improve read performance.

---

# Partitions vs SSTables

This is one of the most confusing Cassandra concepts.

## Partition

A partition is a **logical grouping of rows** that share the same Partition Key.

Example:

```
Partition

matchId = 123

10'

35'

75'
```

Partitions are defined by the data model.

---

## SSTable

An SSTable is a **physical file stored on disk**.

It stores many partitions.

Example:

```
SSTable 1

----------------

Partition Messi

10'

20'

----------------

Partition Ronaldo

15'

----------------

Partition Mbappe

8'
```

An SSTable is simply Cassandra's on-disk storage format.

---

# One SSTable Contains Many Partitions

Suppose the MemTable contains:

```
Messi

10'

20'

---------------

Ronaldo

15'

---------------

Mbappe

8'
```

When Cassandra flushes:

```
MemTable

↓

SSTable 1
```

SSTable 1 now contains:

```
Partition Messi

10'

20'

----------------

Partition Ronaldo

15'

----------------

Partition Mbappe

8'
```

One SSTable stores many partitions.

---

# Later Writes

More events arrive.

```
Messi

35'

---------------

Ronaldo

60'

---------------

Mbappe

55'
```

These go into a new MemTable.

When that MemTable is flushed:

```
SSTable 2
```

Disk now contains:

```
SSTable 1

Messi

10'

20'

Ronaldo

15'

----------------------

SSTable 2

Messi

35'

Ronaldo

60'
```

Notice:

Messi appears in **both SSTables**.

This is expected.

---

# Is the Partition Split?

Logically:

No.

Messi still has one partition.

```
Messi

10'

20'

35'
```

Physically:

The rows are temporarily stored in multiple SSTables.

```
SSTable 1

10'

20'

------------------

SSTable 2

35'
```

This is completely normal.

---

# What Happens Later?

Background Compaction merges the SSTables.

```
SSTable 1

+

SSTable 2

↓

SSTable 3
```

Now:

```
SSTable 3

Messi

10'

20'

35'

----------------

Ronaldo

15'

60'
```

The old SSTables are deleted.

---

# Is an SSTable Temporary?

No.

An SSTable is a permanent on-disk storage file.

It remains on disk until Cassandra replaces it during compaction.

The **MemTable** is temporary.

The **SSTable** is persistent.

---

# Logical View vs Physical View

## Logical View

How developers think about the data.

```
Partition Messi

10'

20'

35'

----------------

Partition Ronaldo

15'

60'
```

---

## Physical View

How Cassandra stores the data.

```
SSTable 1

Messi

10'

20'

Ronaldo

15'

----------------------

SSTable 2

Messi

35'

Ronaldo

60'
```

The partition is a logical concept.

The SSTable is a physical storage file.

---

# Hierarchy

```
Cluster

↓

Node

↓

SSTables

↓

Partitions

↓

Rows
```

Example:

```
Cluster

│

├── Node A

│     ├── SSTable 1

│     │      ├── Partition (Messi)

│     │      ├── Partition (Ronaldo)

│     │      └── Partition (Mbappe)

│     │

│     ├── SSTable 2

│     │      ├── Partition (Messi)

│     │      ├── Partition (Ronaldo)

│     │      └── Partition (Mbappe)

│     │

│     └── SSTable 3
```

---

# Key Takeaways

- A **Partition** is a logical grouping of rows with the same Partition Key.
- An **SSTable** is an immutable, sorted, on-disk storage file.
- One SSTable contains many partitions.
- A partition may temporarily exist across multiple SSTables as new data is written.
- Cassandra never updates an SSTable in place.
- Background compaction merges SSTables to improve read performance.
- **Partition ≠ SSTable**.
- **Logical model ≠ Physical storage**.	

# Cassandra Read Path

One of the first questions after understanding the write path is:

> **If data is spread across multiple SSTables, how does Cassandra read efficiently?**

Suppose the application executes:

```java
repository.findByMatchId(matchId);
```

---

# Naive Approach

Imagine Cassandra has:

```
SSTable 1

SSTable 2

SSTable 3

SSTable 4

SSTable 5
```

A naive approach would be:

```
Open SSTable 1

↓

Search

↓

Open SSTable 2

↓

Search

↓

Open SSTable 3

↓

...
```

This becomes expensive when many SSTables exist.

Cassandra instead maintains several metadata structures to quickly locate data.

---

# Read Path Overview

```
Client

↓

Coordinator Node

↓

Bloom Filter

↓

Partition Summary

↓

Partition Index

↓

SSTable

↓

Rows

↓

Merge Results (if required)

↓

Return Response
```

Most of these metadata structures are kept in memory.

Disk is accessed only when necessary.

---

# Bloom Filter

Every SSTable has its own Bloom Filter.

The Bloom Filter answers one question:

> **Could this SSTable contain the requested partition?**

Example:

```
Lookup

Match123

↓

Bloom Filter
```

Possible responses:

```
Probably YES
```

or

```
Definitely NO
```

Important properties:

- No False Negatives
- Possible False Positives

---

## No False Negatives

If the Bloom Filter says:

```
Definitely NO
```

The partition absolutely does not exist in that SSTable.

Cassandra skips that SSTable completely.

---

## False Positives

Sometimes the Bloom Filter says:

```
Probably YES
```

even though the partition is not present.

Worst case:

Cassandra opens the SSTable unnecessarily.

This is acceptable because Bloom Filters use very little memory while eliminating most unnecessary disk reads.

---

# Partition Index

Suppose the Bloom Filter says:

```
Probably YES
```

Now Cassandra consults the Partition Index.

Example:

```
Partition Index

Match123

↓

Byte Offset 4521

----------------

Match124

↓

Byte Offset 9310

----------------

Match125

↓

Byte Offset 14012
```

Instead of scanning the entire SSTable,

Cassandra jumps directly to the correct byte offset.

---

# Partition Summary

Large SSTables may contain millions of partitions.

The Partition Index itself can become large.

Therefore Cassandra keeps a Partition Summary in memory.

Think of it like the table of contents of a book.

```
Book

↓

Table of Contents

↓

Chapter

↓

Page
```

The Partition Summary narrows down where in the Partition Index Cassandra should search.

---

# Relationship

```
Bloom Filter

↓

Can this SSTable contain the partition?

↓

Partition Summary

↓

Locate the approximate position in the index

↓

Partition Index

↓

Locate the exact byte offset

↓

SSTable

↓

Read the partition
```

---

# Partitions and SSTables

One of the most confusing Cassandra concepts is the relationship between partitions and SSTables.

---

## Partition

A partition is a **logical grouping of rows** that share the same Partition Key.

Example:

```
Partition Match123

10'

35'

75'
```

The partition is defined by the data model.

---

## SSTable

An SSTable is a **physical immutable file stored on disk**.

An SSTable contains many partitions.

Example:

```
SSTable 1

----------------

Partition Match123

10'

35'

75'

----------------

Partition Match124

15'

60'

----------------

Partition Match125

20'
```

One SSTable stores many partitions.

---

# One Partition is Stored Together

Within a single SSTable,

rows belonging to the same partition are stored contiguously.

Example:

```
SSTable 1

Partition Match123

10'

35'

75'

----------------

Partition Match124

15'

60'
```

The partition is **not split** inside an SSTable.

---

# Multiple SSTables

Later another flush occurs.

```
SSTable 2

----------------

Partition Match123

82'

----------------

Partition Match124

90'
```

Now Match123 exists in both SSTables.

```
SSTable 1

Match123

10'

35'

75'

----------------

SSTable 2

Match123

82'
```

This is expected.

New writes create new SSTables.

Existing SSTables are never modified.

---

# During Reads

Suppose someone requests:

```
Match123
```

Cassandra may read:

```
SSTable 1

↓

10'

35'

75'

+

SSTable 2

↓

82'

↓

Merge Results

↓

Return Response
```

Eventually Background Compaction merges the SSTables.

```
SSTable 1

+

SSTable 2

↓

SSTable 3
```

Result:

```
SSTable 3

Match123

10'

35'

75'

82'
```

Old SSTables are deleted.

---

# Is an SSTable Temporary?

No.

An SSTable is a permanent on-disk storage file.

It remains on disk until it is replaced during Compaction.

The MemTable is temporary.

The SSTable is persistent.

---

# Hierarchy

```
Cluster

↓

Node

↓

SSTables

↓

Partitions

↓

Rows
```

Example:

```
Cluster

│

├── Node A

│     ├── SSTable 1

│     │      ├── Partition Match123

│     │      ├── Partition Match124

│     │      └── Partition Match125

│     │

│     ├── SSTable 2

│     │      ├── Partition Match123

│     │      ├── Partition Match126

│     │      └── Partition Match130

│     │

│     └── SSTable 3
```

---

# Important Distinction

A Partition is **not** an SSTable.

```
Partition

↓

Logical grouping of rows
```

```
SSTable

↓

Physical storage file
```

One SSTable contains many partitions.

A partition may temporarily exist across multiple SSTables until Compaction merges them.

---

# Interview Question

**Q: Why doesn't Cassandra scan every SSTable?**

A strong answer:

> Every SSTable maintains a Bloom Filter. If the Bloom Filter reports "Definitely No," Cassandra skips that SSTable entirely. If it reports "Probably Yes," Cassandra consults the Partition Summary and Partition Index to jump directly to the partition within the SSTable rather than scanning the file sequentially.

---

# Key Takeaways

- Cassandra optimizes reads using metadata structures before touching disk.
- Every SSTable has its own Bloom Filter.
- Bloom Filters return:
  - Probably YES
  - Definitely NO
- Bloom Filters never produce false negatives.
- Every SSTable has a Partition Index.
- The Partition Index stores one entry per partition, not one entry per row.
- A Partition Summary helps locate the relevant portion of the Partition Index efficiently.
- Within a single SSTable, all rows belonging to a partition are stored together.
- A partition may temporarily exist across multiple SSTables because new writes create new SSTables.
- Background Compaction merges SSTables and consolidates partition data.

# Commit Log vs MemTable

One of the easiest ways to get confused in Cassandra is mixing up the Commit Log and the MemTable.

They have completely different responsibilities.

---

# Commit Log

The Commit Log provides **durability**.

Every write is appended sequentially.

Example:

```
Write 1

↓

Write 2

↓

Write 3

↓

Write 4
```

Suppose writes arrive in this order:

```
Match123 10'

Match124 15'

Match123 35'

Match124 60'

Match123 75'
```

The Commit Log stores them exactly in arrival order.

```
Commit Log

----------------------

Match123 10'

Match124 15'

Match123 35'

Match124 60'

Match123 75'
```

The Commit Log is:

- Append-only
- Sequentially written
- Used for crash recovery
- Not optimized for reads

Think of it as a recovery journal.

---

# MemTable

The MemTable is **not** append-only.

It is a mutable, sorted in-memory data structure.

As writes arrive:

```
Match123 10'

Match124 15'

Match123 35'

Match124 60'

Match123 75'
```

The MemTable continuously organizes the data by:

1. Partition Key
2. Clustering Column

Internally it becomes:

```
Match123

10'

35'

75'

--------------------

Match124

15'

60'
```

Notice that although the writes arrived interleaved, the MemTable groups rows belonging to the same partition together.

---

# Why Doesn't Cassandra Simply Append to the MemTable?

Suppose the MemTable were append-only.

```
Match123 10'

Match124 15'

Match123 35'

Match124 60'

Match123 75'
```

Now imagine flushing this data to an SSTable.

Cassandra would first need to:

- Group rows by Partition Key
- Sort rows by Clustering Column
- Build Partition Indexes

That would make every flush expensive.

Instead, Cassandra continuously maintains a sorted MemTable.

---

# Flushing the MemTable

When the MemTable reaches its configured threshold:

```
MemTable

↓

Flush

↓

SSTable
```

Since the MemTable is already sorted, Cassandra simply writes it to disk.

Example:

```
MemTable

Match123

10'

35'

75'

--------------------

Match124

15'

60'
```

becomes

```
SSTable

--------------------

Partition Match123

10'

35'

75'

--------------------

Partition Match124

15'

60'
```

No additional sorting is required during the flush.

---

# Who Groups the Partition?

The **MemTable** does.

The SSTable does not reorganize the data.

The SSTable is simply the on-disk representation of the already sorted MemTable.

Think of the flow as:

```
Commit Log

↓

Recovery Journal

----------------------

MemTable

↓

Sorted In-Memory Structure

----------------------

SSTable

↓

Sorted Immutable Disk File
```

---

# Responsibilities

## Commit Log

Responsible for:

- Durability
- Crash Recovery
- Sequential Writes

Not responsible for:

- Sorting
- Grouping partitions
- Query performance

---

## MemTable

Responsible for:

- Grouping rows by Partition Key
- Ordering rows by Clustering Column
- Preparing data for efficient flushing
- Fast in-memory writes

---

## SSTable

Responsible for:

- Persisting the MemTable to disk
- Immutable storage
- Efficient sequential reads

---

# Interview Question

**Q: Who groups rows belonging to the same partition?**

A strong answer:

> The MemTable maintains data in a sorted in-memory structure organized first by the Partition Key and then by the Clustering Columns. The Commit Log simply records writes sequentially for durability. When the MemTable is flushed, Cassandra writes its already sorted contents directly into a new SSTable, so no expensive sorting is required during the flush.

---

# Key Takeaways

- The **Commit Log** is append-only.
- The **MemTable** is **not** append-only.
- The MemTable continuously organizes data by:
  - Partition Key
  - Clustering Columns
- The MemTable groups rows belonging to the same partition together.
- Flushing does not reorganize the data; it simply serializes the sorted MemTable into an immutable SSTable.
- SSTables preserve the organization established by the MemTable.

# Partition Summary vs Partition Index

One of the most common Cassandra interview questions is:

> **What is the difference between the Partition Summary and the Partition Index?**

Both help Cassandra locate a partition efficiently, but they serve different purposes.

---

# Read Path

```
Client

↓

Bloom Filter

↓

Partition Summary

↓

Partition Index

↓

SSTable Data

↓

Return Rows
```

Each step narrows the search.

---

# Partition Index

Every SSTable contains a **Partition Index**.

The Partition Index has **one entry for every partition** in that SSTable.

Example:

```
Partition Index

Match0001  ---> Byte Offset 100

Match0002  ---> Byte Offset 450

Match0003  ---> Byte Offset 820

...

Match0999  ---> Byte Offset 412100

Match1000  ---> Byte Offset 412950

Match1001  ---> Byte Offset 413600

...

Match10000 ---> Byte Offset 4320000
```

Notice:

Every partition has an index entry.

The index tells Cassandra exactly where the partition begins inside the SSTable.

---

# Why Not Search the Entire Partition Index?

Suppose an SSTable contains:

```
50 Million Partitions
```

The Partition Index itself becomes very large.

Searching the entire index for every read would be inefficient.

---

# Partition Summary

The Partition Summary is a **sampled version of the Partition Index**.

Instead of storing every partition, it stores periodic entries.

Example:

```
Partition Summary

Match0001  ---> Partition Index Offset A

Match1000  ---> Partition Index Offset B

Match2000  ---> Partition Index Offset C

Match3000  ---> Partition Index Offset D

...

Match10000 ---> Partition Index Offset J
```

Notice:

The Partition Summary **does not point to the SSTable data**.

It points into the **Partition Index**.

Its purpose is to quickly narrow down where to search in the Partition Index.

---

# Read Example

Suppose the application requests:

```
Match1450
```

### Step 1

Bloom Filter:

```
Probably YES
```

Proceed.

---

### Step 2

Partition Summary

Search:

```
Match1450
```

Closest sampled entry:

```
Match1000

↓

Partition Index Offset B
```

Now Cassandra knows approximately where to begin searching in the Partition Index.

---

### Step 3

Partition Index

Instead of searching from the beginning:

```
Match0001

↓

Match0002

↓

Match0003

↓

...
```

Cassandra begins near:

```
Match1000

↓

Match1001

↓

Match1002

↓

...

↓

Match1450
```

Eventually it finds:

```
Match1450

↓

Byte Offset 598220
```

---

### Step 4

SSTable Data

Jump directly to:

```
Byte Offset 598220
```

Read:

```
Partition Match1450

10'

22'

45'

67'

90'
```

Return the rows.

---

# Memory vs Disk

| Component | Stored In | Purpose |
|-----------|-----------|---------|
| Bloom Filter | Memory | Skip SSTables that definitely don't contain the partition |
| Partition Summary | Memory | Quickly locate the relevant region of the Partition Index |
| Partition Index | Disk (frequently cached) | Find the exact byte offset of the partition in the SSTable |
| SSTable Data | Disk | Store the actual partition rows |

---

# Why Keep the Summary in Memory?

Suppose:

```
100 SSTables
```

Each has:

```
20 Million Partitions
```

Loading every Partition Index completely into memory would consume enormous RAM.

Instead Cassandra keeps:

```
Memory

Bloom Filter

✓

Partition Summary

✓
```

The much larger Partition Index remains on disk, although frequently accessed portions are often cached by the operating system.

---

# Phone Book Analogy

Imagine searching for "Messi" in a huge phone book.

## Partition Summary

```
A → Page 1

B → Page 60

C → Page 120

...

M → Page 520

N → Page 610
```

This tells you where to begin searching.

---

## Partition Index

Starting near page 520:

```
Martinez

Marshall

Mason

Messi

Meyer
```

Now you find the exact entry.

---

## SSTable

Finally, you jump to the exact location in the data file and read the partition.

---

# Responsibilities

## Bloom Filter

Answers:

> Should I even open this SSTable?

Responses:

- Probably YES
- Definitely NO

---

## Partition Summary

Answers:

> Where should I begin searching inside the Partition Index?

---

## Partition Index

Answers:

> What is the exact byte offset of this partition inside the SSTable?

---

## SSTable

Contains the actual partition rows.

---

# Interview Question

**Q: What is the difference between the Partition Summary and the Partition Index?**

A strong answer:

> The Partition Index contains one entry for every partition and maps each partition key to its byte offset within the SSTable. The Partition Summary is a sampled version of the Partition Index that is kept in memory. It allows Cassandra to quickly narrow the search to a small portion of the Partition Index, reducing lookup time and memory usage. The Partition Index then provides the exact byte offset of the partition in the SSTable.

---

# Key Takeaways

- Every SSTable has its own Bloom Filter, Partition Summary, and Partition Index.
- The Partition Index stores one entry for every partition.
- The Partition Summary stores periodic samples of the Partition Index.
- The Partition Summary points into the Partition Index, **not** directly to the SSTable data.
- The Partition Index points to the exact byte offset of the partition within the SSTable.
- Bloom Filters and Partition Summaries are memory-resident for fast lookups.
- Partition Indexes are stored on disk but are frequently cached by the operating system.
- Cassandra touches the SSTable data only after locating the exact partition offset.

# Cassandra Read Path

```text
Client
   │
   ▼
Coordinator Node
   │
   ▼
Bloom Filter
   │
   ├── Definitely NO → Skip SSTable
   │
   └── Probably YES
           │
           ▼
   Partition Summary
           │
           ▼
   Partition Index
           │
           ▼
   Byte Offset in SSTable
           │
           ▼
   SSTable Data
           │
           ▼
Merge Results (if partition exists in multiple SSTables)
           │
           ▼
Return Response
```

# Cassandra Replication & Consistency

## Why Replication?

Suppose we have a Cassandra cluster:

```
        Cassandra Cluster

      ┌────────┐
      │ Node A │
      └────────┘

      ┌────────┐
      │ Node B │
      └────────┘

      ┌────────┐
      │ Node C │
      └────────┘

      ┌────────┐
      │ Node D │
      └────────┘
```

Suppose:

```
Match123

↓

Hash()

↓

Node B
```

Should Cassandra store the data only on Node B?

No.

If Node B fails, the partition becomes unavailable.

Instead, Cassandra replicates the partition across multiple nodes.

```
Node A

Match123

----------------

Node B

Match123

----------------

Node C

Match123
```

This provides:

- High Availability
- Fault Tolerance
- Better Read Scalability

---

# Replication Factor (RF)

The **Replication Factor (RF)** specifies how many copies of each partition Cassandra stores.

Example:

```
RF = 3
```

means:

```
Match123

↓

Node A

Node B

Node C
```

Three copies of the same partition exist.

Example:

```
RF = 1
```

```
Match123

↓

Node B
```

Only one copy exists.

If Node B fails, the data becomes unavailable.

---

# Trade-offs

Higher Replication Factor provides:

- Better Availability
- Better Fault Tolerance

But also increases:

- Storage Usage
- Network Traffic
- Write Latency

Choosing RF is a trade-off between resilience and cost.

---

# Consistency Levels

Once data is replicated, Cassandra must decide:

> **How many replicas must acknowledge a write before returning success?**

This is controlled by the **Consistency Level**.

---

# Consistency Level = ONE

```
Client

↓

Coordinator

↓

Node A ✓

↓

Return Success
```

Characteristics:

- Lowest latency
- Highest availability

Trade-off:

Other replicas may not yet have the latest data.

---

# Consistency Level = ALL

```
Client

↓

Coordinator

↓

Node A ✓

Node B ✓

Node C ✓

↓

Return Success
```

Characteristics:

- Strongest consistency

Trade-offs:

- Highest latency
- If one replica is unavailable, the write fails

---

# Consistency Level = QUORUM

For:

```
RF = 3
```

Quorum is calculated as:

```
⌊ RF / 2 ⌋ + 1

⌊3/2⌋ + 1

= 2
```

Therefore Cassandra waits for:

```
Node A ✓

Node B ✓

↓

Return Success
```

The third replica can respond later.

---

# Why QUORUM?

QUORUM provides a balance between:

- Consistency
- Availability
- Latency

It is one of the most commonly used consistency levels.

---

# Read and Write Quorums

Suppose:

```
RF = 3

Write Consistency = QUORUM (2)

Read Consistency = QUORUM (2)
```

Then:

```
R + W > RF

2 + 2 > 3
```

Because the read quorum and write quorum overlap by at least one replica, at least one replica participating in the read has acknowledged the latest successful write. This greatly reduces the chance of stale reads.

---

# Consistency Level Comparison

| Consistency Level | Replicas Required | Advantages | Trade-offs |
|-------------------|-------------------|------------|------------|
| ONE | 1 | Lowest latency, highest availability | Possible stale reads |
| QUORUM | Majority of replicas | Good balance of consistency and latency | Slightly higher latency than ONE |
| ALL | All replicas | Strongest consistency | Highest latency, lowest availability |

---

# Apple Sports Example

Suppose a goal is scored.

Millions of users immediately refresh the score.

Possible choices:

- **ONE** → Fastest, but some users may briefly see stale data.
- **ALL** → Strong consistency, but slower and vulnerable to replica failures.
- **QUORUM / LOCAL_QUORUM** → Typically the best balance for live sports applications.

---

# Interview Question

**Q: Why not always use Consistency Level = ALL?**

A strong answer:

> ALL provides the strongest consistency but increases write latency and reduces availability because every replica must respond. If even one replica is unavailable, the write fails. For most production workloads, QUORUM provides a better balance between consistency, latency, and fault tolerance.

---

# Interview Question

**Q: Why choose QUORUM over ONE?**

A strong answer:

> ONE offers the lowest latency but can return stale data because only one replica acknowledges the write. QUORUM requires a majority of replicas to participate, providing much stronger consistency while still maintaining good availability and reasonable latency.

---

# Key Takeaways

- Cassandra replicates partitions across multiple nodes for high availability.
- Replication Factor (RF) defines how many copies of each partition are stored.
- Consistency Levels determine how many replicas must acknowledge a read or write.
- **ONE** favors latency and availability.
- **ALL** favors consistency but increases latency and reduces availability.
- **QUORUM** provides a practical balance between consistency, latency, and fault tolerance.
- Using **Read QUORUM** and **Write QUORUM** with **R + W > RF** ensures that read and write operations overlap on at least one replica, significantly reducing stale reads.

# Cassandra Replication & Consistency

## Why Replication?

Suppose we have a Cassandra cluster:

```
        Cassandra Cluster

      ┌────────┐
      │ Node A │
      └────────┘

      ┌────────┐
      │ Node B │
      └────────┘

      ┌────────┐
      │ Node C │
      └────────┘

      ┌────────┐
      │ Node D │
      └────────┘
```

Suppose:

```
Match123

↓

Hash()

↓

Node B
```

Should Cassandra store the data only on Node B?

No.

If Node B fails, the partition becomes unavailable.

Instead, Cassandra replicates the partition across multiple nodes.

```
Node A

Match123

----------------

Node B

Match123

----------------

Node C

Match123
```

This provides:

- High Availability
- Fault Tolerance
- Better Read Scalability

---

# Replication Factor (RF)

The **Replication Factor (RF)** specifies how many copies of each partition Cassandra stores.

Example:

```
RF = 3
```

means:

```
Match123

↓

Node A

Node B

Node C
```

Three copies of the same partition exist.

Example:

```
RF = 1
```

```
Match123

↓

Node B
```

Only one copy exists.

If Node B fails, the data becomes unavailable.

---

# Trade-offs

Higher Replication Factor provides:

- Better Availability
- Better Fault Tolerance

But also increases:

- Storage Usage
- Network Traffic
- Write Latency

Choosing RF is a trade-off between resilience and cost.

---

# Consistency Levels

Once data is replicated, Cassandra must decide:

> **How many replicas must acknowledge a write before returning success?**

This is controlled by the **Consistency Level**.

---

# Consistency Level = ONE

```
Client

↓

Coordinator

↓

Node A ✓

↓

Return Success
```

Characteristics:

- Lowest latency
- Highest availability

Trade-off:

Other replicas may not yet have the latest data.

---

# Consistency Level = ALL

```
Client

↓

Coordinator

↓

Node A ✓

Node B ✓

Node C ✓

↓

Return Success
```

Characteristics:

- Strongest consistency

Trade-offs:

- Highest latency
- If one replica is unavailable, the write fails

---

# Consistency Level = QUORUM

For:

```
RF = 3
```

Quorum is calculated as:

```
⌊ RF / 2 ⌋ + 1

⌊3/2⌋ + 1

= 2
```

Therefore Cassandra waits for:

```
Node A ✓

Node B ✓

↓

Return Success
```

The third replica can respond later.

---

# Why QUORUM?

QUORUM provides a balance between:

- Consistency
- Availability
- Latency

It is one of the most commonly used consistency levels.

---

# Read and Write Quorums

Suppose:

```
RF = 3

Write Consistency = QUORUM (2)

Read Consistency = QUORUM (2)
```

Then:

```
R + W > RF

2 + 2 > 3
```

Because the read quorum and write quorum overlap by at least one replica, at least one replica participating in the read has acknowledged the latest successful write. This greatly reduces the chance of stale reads.

---

# Consistency Level Comparison

| Consistency Level | Replicas Required | Advantages | Trade-offs |
|-------------------|-------------------|------------|------------|
| ONE | 1 | Lowest latency, highest availability | Possible stale reads |
| QUORUM | Majority of replicas | Good balance of consistency and latency | Slightly higher latency than ONE |
| ALL | All replicas | Strongest consistency | Highest latency, lowest availability |

---

# Apple Sports Example

Suppose a goal is scored.

Millions of users immediately refresh the score.

Possible choices:

- **ONE** → Fastest, but some users may briefly see stale data.
- **ALL** → Strong consistency, but slower and vulnerable to replica failures.
- **QUORUM / LOCAL_QUORUM** → Typically the best balance for live sports applications.

---

# Interview Question

**Q: Why not always use Consistency Level = ALL?**

A strong answer:

> ALL provides the strongest consistency but increases write latency and reduces availability because every replica must respond. If even one replica is unavailable, the write fails. For most production workloads, QUORUM provides a better balance between consistency, latency, and fault tolerance.

---

# Interview Question

**Q: Why choose QUORUM over ONE?**

A strong answer:

> ONE offers the lowest latency but can return stale data because only one replica acknowledges the write. QUORUM requires a majority of replicas to participate, providing much stronger consistency while still maintaining good availability and reasonable latency.

---

# Key Takeaways

- Cassandra replicates partitions across multiple nodes for high availability.
- Replication Factor (RF) defines how many copies of each partition are stored.
- Consistency Levels determine how many replicas must acknowledge a read or write.
- **ONE** favors latency and availability.
- **ALL** favors consistency but increases latency and reduces availability.
- **QUORUM** provides a practical balance between consistency, latency, and fault tolerance.
- Using **Read QUORUM** and **Write QUORUM** with **R + W > RF** ensures that read and write operations overlap on at least one replica, significantly reducing stale reads.

# Cassandra Replication & Consistency - Q&A

## What is Replication Factor (RF)?

Replication Factor (RF) defines **how many copies of each partition Cassandra stores**.

Example:

```
RF = 3
```

Suppose:

```
Match123
```

Cassandra stores it on three replica nodes.

```
Node A

Match123

----------------

Node B

Match123

----------------

Node C

Match123
```

Every partition has three copies.

---

## Is RF the same as the number of nodes?

No.

These are completely different concepts.

Example:

```
Cluster Size = 10 Nodes

RF = 3
```

The cluster has ten nodes, but every partition is stored on only three of them.

Example:

```
Match123

↓

Node3

Node4

Node5
```

Another partition:

```
Match456

↓

Node8

Node9

Node10
```

Another partition:

```
Match789

↓

Node10

Node1

Node2
```

Different partitions are distributed across different nodes.

---

## Rule

```
Cluster Size

↓

How many Cassandra nodes exist.
```

```
Replication Factor

↓

How many copies of EACH partition exist.
```

---

## Who Configures RF?

RF is configured when the Keyspace is created.

Example:

```sql
CREATE KEYSPACE sports
WITH replication = {
'class':'NetworkTopologyStrategy',
'DC1':3
};
```

Normally the DBA or infrastructure team configures this.

Applications usually do not change RF.

---

# What is QUORUM?

QUORUM is a **Consistency Level**.

It is **not manually configured as a number**.

Cassandra calculates it using:

```
QUORUM = floor(RF / 2) + 1
```

Examples:

| RF | QUORUM |
|----|---------|
| 1 | 1 |
| 2 | 2 |
| 3 | 2 |
| 4 | 3 |
| 5 | 3 |
| 6 | 4 |

Example:

```
RF = 5

↓

QUORUM

↓

floor(5/2)+1

↓

3
```

Applications specify:

```java
ConsistencyLevel.QUORUM
```

Cassandra converts that into the required number of replicas.

---

# What are R and W?

R and W are **not Cassandra configuration parameters.**

They simply represent the consistency level used for:

```
R

↓

Read Operations
```

```
W

↓

Write Operations
```

Examples:

```
Write

↓

ConsistencyLevel.ONE

↓

W = 1
```

```
Read

↓

ConsistencyLevel.QUORUM

↓

R = 2
```

---

# Why are R and W Separate?

Because reads and writes often have different business requirements.

Example:

```
POST /goal
```

A write operation.

Only **Write Consistency** matters.

Example:

```
W = QUORUM
```

---

```
GET /liveScore
```

A read operation.

Only **Read Consistency** matters.

Example:

```
R = ONE
```

The same application may choose different consistency levels for different APIs.

---

# Does a POST use both R and W?

No.

Example:

```
POST /goal

↓

Write

↓

W = QUORUM
```

Only Write Consistency is used.

---

```
GET /score

↓

Read

↓

R = ONE
```

Only Read Consistency is used.

---

# What does ConsistencyLevel.QUORUM actually mean?

Suppose:

```
RF = 3
```

Application executes:

```java
statement.setConsistencyLevel(ConsistencyLevel.QUORUM);
```

Cassandra computes:

```
QUORUM = floor(3/2)+1

↓

2
```

Coordinator waits for two replicas.

For a write:

```
Coordinator

↓

Node A ✓

Node B ✓

↓

Return Success
```

For a read:

```
Coordinator

↓

Read Node A

Read Node B

↓

Return Result
```

The same consistency level applies differently depending on whether the operation is a read or write.

---

# What is R + W > RF?

This is **not a Cassandra configuration.**

It is a design rule.

Suppose:

```
RF = 3

Write = QUORUM

Read = QUORUM
```

Then

```
W = 2

R = 2
```

Therefore

```
R + W > RF

2 + 2 > 3
```

The read quorum and write quorum overlap on at least one replica.

This greatly reduces the chance of stale reads.

---

Suppose instead:

```
Write = QUORUM

↓

2

Read = ONE

↓

1
```

Now:

```
R + W = 3

NOT > RF
```

No overlap is guaranteed.

A stale read becomes possible.

---

# Can any node be the Coordinator?

Yes.

Every Cassandra node is equal.

Whichever node receives the client request becomes the Coordinator.

Example:

```
Application

↓

Node7

↓

Coordinator
```

Tomorrow:

```
Application

↓

Node2

↓

Coordinator
```

There is no master node.

Cassandra uses a peer-to-peer architecture.

---

# Responsibilities

| Component | Responsibility |
|------------|----------------|
| Replication Factor (RF) | Number of copies of each partition |
| Consistency Level | Number of replicas participating in a read or write |
| QUORUM | Majority of replicas (calculated from RF) |
| Coordinator | Node that receives the client request and coordinates the operation |

# Cassandra Consistency Levels in Production

## Who Configures Read and Write Consistency?

The application configures the consistency level.

Typically this is done through the Cassandra Java Driver.

Example:

```java
statement.setConsistencyLevel(DefaultConsistencyLevel.QUORUM);
```

Consistency Level is configured **per operation (per query)**.

It is **not** a cluster-wide setting.

---

# Is There a Default?

Yes.

Most applications configure a default consistency level.

Example (Driver Configuration):

```yaml
datastax-java-driver:
  basic:
    request:
      consistency: LOCAL_QUORUM
```

Now every query automatically uses:

```
LOCAL_QUORUM
```

unless overridden.

---

# Default Behavior

Suppose the application has:

```
Default Consistency = LOCAL_QUORUM
```

Then

```
POST /goal

↓

LOCAL_QUORUM
```

```
GET /score

↓

LOCAL_QUORUM
```

No extra code is required.

---

# Overriding a Write

Suppose analytics events are less critical.

Default:

```
LOCAL_QUORUM
```

Only this write overrides the consistency level.

```java
SimpleStatement stmt =
    SimpleStatement.builder(
        "INSERT INTO analytics (...) VALUES (...)")
        .setConsistencyLevel(DefaultConsistencyLevel.ONE)
        .build();

session.execute(stmt);
```

Result:

```
Analytics Write

↓

Consistency = ONE
```

Everything else continues using the default.

---

# Overriding a Read

Suppose live score latency is critical.

```java
SimpleStatement stmt =
    SimpleStatement.builder(
        "SELECT * FROM scores WHERE match_id=?")
        .addPositionalValue(matchId)
        .setConsistencyLevel(DefaultConsistencyLevel.ONE)
        .build();

session.execute(stmt);
```

Now only this API becomes:

```
GET /liveScore

↓

Consistency = ONE
```

Other APIs continue using the default.

---

# Another Read Example

Official match result.

```java
SimpleStatement stmt =
    SimpleStatement.builder(
        "SELECT * FROM scores WHERE match_id=?")
        .addPositionalValue(matchId)
        .setConsistencyLevel(DefaultConsistencyLevel.QUORUM)
        .build();

session.execute(stmt);
```

Now:

```
GET /officialScore

↓

Consistency = QUORUM
```

---

# Production Example

Suppose:

```
Default Consistency

↓

LOCAL_QUORUM
```

Application:

```
POST /goal

↓

LOCAL_QUORUM

(Default)
```

```
GET /match

↓

LOCAL_QUORUM

(Default)
```

```
POST /analytics

↓

ONE

(Override)
```

```
GET /liveScore

↓

ONE

(Override)
```

```
GET /officialScore

↓

QUORUM

(Override)
```

---

# Why Override?

Different APIs have different business requirements.

Example:

| API | Consistency | Reason |
|------|------------|--------|
| POST /goal | QUORUM or LOCAL_QUORUM | Important write |
| GET /liveScore | ONE | Lowest latency |
| GET /officialScore | QUORUM | Strong consistency |
| POST /analytics | ONE | Throughput more important than consistency |

---

# Important Clarification

A request uses **only one consistency level**.

Example:

```
POST /goal

↓

Write

↓

Write Consistency Only
```

There is no read consistency involved.

---

Similarly:

```
GET /score

↓

Read

↓

Read Consistency Only
```

There is no write consistency involved.

---

# Then Why Do We Talk About R + W > RF?

Because architects choose consistency levels for both operations.

Example:

```
POST /goal

↓

QUORUM

(W = 2)
```

```
GET /officialScore

↓

QUORUM

(R = 2)
```

Now

```
R + W > RF

2 + 2 > 3
```

The read quorum overlaps the write quorum, greatly reducing the chance of stale reads.

Notice:

These are **two separate API calls**, not one request.

---

# Responsibilities

| Who? | Responsibility |
|------|----------------|
| DBA / Infrastructure | Configure Replication Factor (RF) |
| Cassandra | Calculates QUORUM using `floor(RF/2)+1` |
| Application | Chooses consistency level (ONE, QUORUM, ALL, etc.) for each query |
| Coordinator | Waits for the required number of replicas before returning the response |

---

# Interview Answer

**Q: How is consistency level configured in production?**

A strong answer:

> Replication Factor is configured at the keyspace level by the infrastructure team. The application typically configures a default consistency level through the Cassandra driver (for example, LOCAL_QUORUM). Most queries use that default, while specific APIs override it when they have different consistency or latency requirements. Each read or write request carries its own consistency level, and the coordinator calculates the required number of replicas based on the configured Replication Factor.

---

# Key Takeaways

- Replication Factor (RF) is configured once for the keyspace.
- QUORUM is calculated as `floor(RF/2)+1`.
- Applications choose the consistency level for each query.
- Most applications configure a default consistency level.
- Individual queries can override the default.
- POST requests use only **Write Consistency**.
- GET requests use only **Read Consistency**.
- `R + W > RF` is a design principle that ensures read and write quorums overlap, reducing stale reads.

# What Happens if One Replica Has Stale Data?

Suppose:

```
RF = 3

Read Consistency = QUORUM
```

Replica state:

```
Node A

Score = 2

Timestamp = 10:15:32

--------------------

Node B

Score = 2

Timestamp = 10:15:32

--------------------

Node C

Score = 1

Timestamp = 10:15:20   ← Stale
```

---

## Coordinator Sends Read Requests

```
Coordinator

↓

Node A

Node B

Node C
```

For QUORUM, Cassandra needs responses from **2 replicas**.

---

## Scenario 1

Suppose Node A and Node B respond first.

```
Node A

Score = 2

✓

----------------

Node B

Score = 2

✓
```

The coordinator immediately returns:

```
Score = 2
```

Node C being stale does not affect the response.

---

## Scenario 2

Suppose Node A and Node C respond first.

```
Node A

Score = 2

Timestamp = 10:15:32

----------------

Node C

Score = 1

Timestamp = 10:15:20
```

The coordinator compares the timestamps.

```
10:15:32

>

10:15:20
```

Node A contains the latest version.

The coordinator returns:

```
Score = 2
```

to the client.

---

## How Does Cassandra Know Which Value Is Correct?

Every write in Cassandra carries a **write timestamp**.

During a read, if replicas return different versions of the same data, the coordinator compares the timestamps and selects the latest version.

This is known as **Last Write Wins**.

---

## What Happens to the Stale Replica?

If Cassandra detects that a replica is stale, it may perform a **Read Repair**, updating the stale replica with the latest value in the background.

Example:

Before Read Repair:

```
Node A

Score = 2

----------------

Node C

Score = 1
```

After Read Repair:

```
Node A

Score = 2

----------------

Node C

Score = 2
```

Future reads become consistent.

---

# Why QUORUM Helps

Suppose:

```
RF = 3

Write Consistency = QUORUM

Read Consistency = QUORUM
```

The write succeeds on:

```
Node A ✓

Node B ✓

Node C ✗
```

Later a read uses QUORUM.

Possible read combinations:

```
A + B

A + C

B + C
```

Every possible read overlaps with at least one replica that acknowledged the latest write.

This is why:

```
R + W > RF
```

provides strong consistency.

---

# Interview Question

**Q: What happens if one replica returns stale data during a QUORUM read?**

A strong answer:

> The coordinator compares the versions returned by the replicas using write timestamps. It returns the latest version to the client (Last Write Wins). If it detects that another replica is stale, Cassandra may perform a Read Repair to synchronize the stale replica in the background.

# Cassandra Replica Synchronization

Cassandra uses three mechanisms to keep replicas synchronized.

| Mechanism | Trigger | Purpose |
|-----------|---------|---------|
| **Hinted Handoff** | Replica is down during a write | Replay missed writes when the replica comes back |
| **Read Repair** | Replica is stale during a read | Return the latest value and repair stale replicas |
| **Anti-Entropy Repair** | Scheduled background process | Compare replicas and repair inconsistencies even if no reads occur |

---

# 1. Hinted Handoff

## Problem

Suppose:

```
RF = 3

Write = QUORUM
```

Replicas:

```
Node A

Node B

Node C
```

Node C crashes.

```
Node C

↓

DOWN
```

A client sends:

```
POST /goal
```

The coordinator writes to:

```
Node A ✓

Node B ✓

Node C ✗
```

Since QUORUM is satisfied:

```
W = 2
```

the write succeeds.

The client immediately receives:

```
200 OK
```

---

## What About Node C?

Node C missed the write.

Current state:

```
Node A

Goal = 2

----------------

Node B

Goal = 2

----------------

Node C

Goal = 1
```

Instead of failing the write, the coordinator stores a **Hint**.

Think of it as:

```
Hint

↓

"Node C missed:

Goal = 2"
```

Later, when Node C comes back online:

```
Coordinator

↓

Replay Hint

↓

Node C

Goal = 2
```

Now all replicas are synchronized again.

---

## Timeline

```
Replica Down

↓

Write Arrives

↓

Coordinator Writes Available Replicas

↓

Store Hint

↓

Client Gets Success

↓

Replica Recovers

↓

Replay Hint

↓

Replica Updated
```

---

# Interview Question

**Q: What happens if one replica is unavailable during a write?**

A strong answer:

> If the requested consistency level can still be satisfied (for example, QUORUM with RF=3), the coordinator writes to the available replicas, returns success to the client, and stores a Hint for the unavailable replica. When that replica comes back online, the coordinator replays the missed writes using Hinted Handoff.

---

# 2. Read Repair

Suppose:

```
RF = 3

Read = QUORUM
```

Replica state:

```
Node A

Score = 2

Timestamp = 10:15:32

--------------------

Node B

Score = 2

Timestamp = 10:15:32

--------------------

Node C

Score = 1

Timestamp = 10:15:20   ← Stale
```

---

## Coordinator Sends Read Requests

```
Coordinator

↓

Node A

Node B

Node C
```

Suppose Node A and Node C respond first.

```
Node A

Score = 2

Timestamp = 10:15:32

----------------

Node C

Score = 1

Timestamp = 10:15:20
```

The coordinator compares timestamps.

```
10:15:32

>

10:15:20
```

The latest version is returned to the client.

```
Score = 2
```

---

## How Does Cassandra Know Which Version Is Latest?

Every write carries a **write timestamp**.

If replicas return different versions, Cassandra uses **Last Write Wins**.

The version with the newest timestamp is returned.

---

## What Happens to the Stale Replica?

Cassandra may initiate a **Read Repair**.

```
Before

Node C

Score = 1

↓

Read Repair

↓

After

Node C

Score = 2
```

The repair occurs after the latest value has been determined.

The goal is to synchronize replicas for future reads.

---

## Critical Path

The client should not wait for replica repair.

Critical path:

```
Client

↓

Coordinator

↓

Read Replicas

↓

Choose Latest Version

↓

Return Response
```

After the response:

```
Repair Stale Replica

↓

Background
```

Keeping repair outside the critical path minimizes read latency.

---

# Interview Question

**Q: What happens if one replica returns stale data during a QUORUM read?**

A strong answer:

> The coordinator compares the versions returned by the replicas using write timestamps. It returns the latest version to the client (Last Write Wins). If another replica is stale, Cassandra may initiate a Read Repair to synchronize that replica without delaying the client response.

---

# 3. Anti-Entropy Repair

## Problem

Suppose:

```
Node A

Score = 2

----------------

Node B

Score = 2

----------------

Node C

Score = 1
```

Nobody reads this partition.

Read Repair never occurs.

Node C remains stale forever.

---

## Solution

Cassandra periodically runs **Anti-Entropy Repair**.

Its job is to compare replicas and synchronize differences.

---

# Does Cassandra Compare Entire Nodes?

No.

Different nodes store different partitions.

Only replicas responsible for the **same token range** are compared.

---

## Example

Cluster:

```
10 Nodes

RF = 3
```

Suppose token range:

```
250 - 500
```

is replicated on:

```
Node2

Node3

Node4
```

These three replicas should contain identical data for that token range.

Node8 is not involved because it stores different token ranges.

---

## Token Range

A token range contains many partitions.

Example:

```
Token Range

250 - 500

↓

Match123

Match124

Match125

...

Match80000
```

Notice:

- Larger than a partition
- Smaller than an entire node

This is the unit Cassandra compares.

---

# Merkle Trees

Each replica builds a Merkle Tree for its token range.

```
Node2

Token Range 250-500

↓

Merkle Tree
```

```
Node3

Token Range 250-500

↓

Merkle Tree
```

If root hashes match:

```
ABCD123

=

ABCD123
```

No repair is required.

---

If hashes differ:

```
ABCD123

≠

XYZ789
```

Cassandra recursively compares the Merkle Tree branches until it identifies the specific partitions that differ.

Only those partitions are synchronized.

---

# Why Merkle Trees?

Without Merkle Trees:

```
Compare

Millions of Partitions
```

With Merkle Trees:

```
Compare

One Root Hash

↓

Equal?

Done.

↓

Different?

Compare Child Hashes

↓

Eventually Find Only the Differing Partitions
```

This makes Anti-Entropy Repair efficient.

---

# Timeline

```
Replica Becomes Stale

↓

No Reads Occur

↓

Read Repair Never Runs

↓

Scheduled Anti-Entropy Repair

↓

Build Merkle Trees

↓

Compare Token Ranges

↓

Identify Differences

↓

Synchronize Replicas
```

---

# Comparison

| Mechanism | Trigger | Repairs |
|-----------|---------|----------|
| Hinted Handoff | Replica unavailable during write | Missed writes |
| Read Repair | Replica stale during read | Replica participating in the read |
| Anti-Entropy Repair | Scheduled maintenance | Any inconsistent replica, even if never read |

---

# Important Distinction

Hinted Handoff:

```
Replica Missed A Write

↓

Replay Missed Writes
```

Read Repair:

```
Replica Returned Stale Data

↓

Repair During/After Read
```

Anti-Entropy Repair:

```
Replica Drifted Over Time

↓

Background Comparison

↓

Synchronize Replicas
```

---

# Mental Model

```
Cluster
    │
    ▼
Node
    │
    ▼
Replicated Token Range   ← Anti-Entropy compares here
    │
    ▼
Partitions
    │
    ▼
Rows
```

A **replicated token range** is:

- Smaller than an entire node's data
- Larger than a single partition
- Expected to be identical across all replicas for that range

This is the unit Cassandra compares using Merkle Trees.

---

# Interview Question

**Q: Do Merkle Trees compare entire Cassandra nodes?**

A strong answer:

> No. Cassandra builds Merkle Trees for replicated token ranges, not entire nodes. Only replicas responsible for the same token range compare their Merkle Trees. If the hashes differ, Cassandra recursively narrows the comparison to identify and synchronize only the partitions that are inconsistent.

---

# Key Takeaways

- Hinted Handoff repairs replicas that were unavailable during writes.
- Read Repair fixes stale replicas detected during reads.
- Anti-Entropy Repair repairs replicas even if the data is never read.
- Cassandra compares **replicated token ranges**, not entire nodes.
- A replicated token range is larger than a partition but smaller than an entire node's data.
- Merkle Trees allow Cassandra to efficiently detect differences without comparing every partition.
- Only partitions that actually differ are synchronized.

# Gossip Protocol & Seed Nodes

## Why Does Cassandra Need Gossip?

Suppose we have a Cassandra cluster:

```
Node A

Node B

Node C

Node D
```

Questions:

- How does Node A know Node C is alive?
- How does Node B know Node D crashed?
- How does a new node join the cluster?
- How do nodes learn token ownership?
- How do nodes learn schema changes?

Cassandra solves this using the **Gossip Protocol**.

---

# Gossip Protocol

Cassandra has **no master node**.

Instead, every node periodically exchanges cluster metadata with another node.

Example:

```
Node A

↓

Talks to Node C
```

Node A shares information such as:

- Nodes that are alive
- Nodes that are down
- Token ownership
- Schema version
- Data center
- Rack
- Load information

Node C shares its own knowledge.

Both nodes update their cluster state.

---

## Gossip Continues Forever

Every node periodically gossips with another node.

Example:

```
Node A

↓

Node C

----------------

Node B

↓

Node D

----------------

Node C

↓

Node A
```

Eventually cluster information spreads to every node.

No master node is required.

---

# What Information is Shared?

Gossip exchanges **cluster metadata**, not application data.

Examples:

- Node status (UP / DOWN)
- Token ownership
- Schema version
- Data center
- Rack
- Load information
- Cluster membership

Application data (such as match scores or events) is **not** exchanged through Gossip.

---

# Failure Detection

Suppose:

```
Node C

↓

Crash
```

Node B attempts to gossip.

No response.

Node B marks:

```
Node C

↓

SUSPECT
```

Other nodes also fail to contact Node C.

Eventually the cluster agrees:

```
Node C

↓

DOWN
```

Requests are no longer routed to that node.

---

# Why Not Mark a Node Down Immediately?

Temporary network delays can occur because of:

- Network congestion
- Garbage Collection (GC) pauses
- High CPU usage
- Temporary packet loss

Immediately declaring a node dead would produce false failures.

Instead Cassandra uses an **Accrual Failure Detector**.

It considers:

- How late the heartbeat is
- The node's normal response pattern

Nodes transition:

```
Healthy

↓

Suspect

↓

Down
```

This avoids unnecessary failovers caused by temporary delays.

---

# Seed Nodes

A Seed Node is **not** a master node.

Its only responsibility is to help a new node discover the cluster.

---

## Existing Cluster

```
Node A

Node B

Node C

Node D
```

Now a new node starts.

```
Node E
```

Initially it knows nothing.

---

## Step 1

Node E is configured with one or more Seed Nodes.

Example:

```yaml
seed_nodes:
  - NodeA
  - NodeC
```

This simply means:

> "When starting, contact one of these nodes."

---

## Step 2

Node E contacts a Seed Node.

```
Node E

↓

Node A
```

Node E asks:

> "How do I join the cluster?"

---

## Step 3

The Seed Node replies with cluster membership information.

Example:

```
Current Cluster

↓

Node A

Node B

Node C

Node D
```

Node E now knows about the cluster.

---

## Step 4

Node E begins participating in Gossip.

```
Node E

↓

Node B

↓

Node C

↓

Node D
```

Within a short time,

every node knows about Node E,

and Node E knows about every node.

The Seed Node has no further special responsibility.

---

# If the Seed Node Fails

Suppose:

```
Node A

↓

DOWN
```

Does the cluster stop?

No.

Existing nodes continue gossiping normally.

Only a brand-new node attempting to join may have difficulty if **all configured Seed Nodes** are unavailable.

For this reason, production clusters typically configure multiple Seed Nodes.

Example:

```yaml
seed_nodes:
  - NodeA
  - NodeC
  - NodeF
```

A new node can contact any available Seed Node.

---

# Seed Node is NOT

A Seed Node is **not**:

- Master
- Leader
- Coordinator
- Metadata Server
- Single Point of Failure

Its only purpose is **cluster bootstrapping**.

---

# Timeline

```
Cluster Exists

↓

New Node Starts

↓

Contact Seed Node

↓

Receive Cluster Membership

↓

Start Gossip

↓

Become Normal Cluster Member
```

---

# Responsibilities

| Component | Responsibility |
|-----------|----------------|
| Gossip Protocol | Exchange cluster metadata between nodes |
| Failure Detector | Determine whether a node is healthy, suspect, or down |
| Seed Node | Help new nodes discover the cluster during startup |

---

# Interview Questions

### Q: Why doesn't Cassandra need a master node?

A strong answer:

> Cassandra uses a peer-to-peer architecture. Every node exchanges cluster metadata through the Gossip Protocol. Since cluster state is distributed across all nodes rather than managed by a central server, Cassandra does not require a master node.

---

### Q: What is a Seed Node?

A strong answer:

> A Seed Node is simply a well-known contact point used by a new Cassandra node to discover the cluster. After learning the existing cluster membership, the new node begins gossiping directly with all other nodes. The Seed Node has no special role after cluster discovery.

---

### Q: What happens if a Seed Node crashes?

A strong answer:

> Existing nodes continue operating normally because Gossip is peer-to-peer. Only new nodes attempting to join may be affected if all configured Seed Nodes are unavailable. This is why production clusters typically configure multiple Seed Nodes.

---

# Key Takeaways

- Cassandra has **no master node**.
- Nodes exchange **cluster metadata** using the Gossip Protocol.
- Gossip shares metadata, not application data.
- Gossip allows nodes to learn about:
  - Cluster membership
  - Node status
  - Token ownership
  - Schema changes
- Cassandra uses an **Accrual Failure Detector** to distinguish temporary delays from actual node failures.
- Nodes transition through **Healthy → Suspect → Down** rather than being marked down immediately.
- A **Seed Node** is only used to bootstrap a new node into the cluster.
- After joining, all nodes participate equally in Gossip.
- Production clusters typically configure multiple Seed Nodes for reliability.

# Consistent Hashing & Virtual Nodes (vnodes)

## How Does Cassandra Decide Which Node Stores a Partition?

Cassandra does not use:

- A lookup table
- A master node
- A metadata database

Instead it uses **Consistent Hashing**.

---

# Step 1 - Hash the Partition Key

Suppose:

```
Partition Key

↓

Match123
```

Cassandra hashes the partition key.

```
Match123

↓

Hash()

↓

582
```

The hash value becomes the **token**.

---

# Step 2 - Nodes Also Have Tokens

Each Cassandra node is assigned one (or more) tokens on a logical ring.

Example:

```
                    0
                     │
          Node A (100)
               ●
      ┌──────────────────┐
      │                  │
Node D(850) ●            ● Node B (350)
      │                  │
      └──────────────────┘
               ●
         Node C (650)
```

Node positions define ownership on the ring.

---

# Step 3 - Find the First Node Clockwise

Suppose:

```
Partition Key

↓

Match123

↓

Hash()

↓

582
```

Move clockwise around the ring.

```
582

↓

650

↓

Node C
```

Node C becomes the **Primary Replica**.

---

# Replication

Suppose:

```
RF = 3
```

Primary owner:

```
Node C
```

Replicas are the next nodes clockwise.

```
Node C

↓

Node D

↓

Node A
```

Result:

```
Match123

↓

Node C (Primary)

Node D (Replica)

Node A (Replica)
```

---

# Another Example

```
Hash = 920
```

Move clockwise.

```
920

↓

Wrap Around

↓

100

↓

Node A
```

The ring wraps around continuously.

---

# What Happens When a New Node Joins?

Suppose Node E joins between Node B and Node C.

Before:

```
Node B (350)

↓

Node C (650)
```

After:

```
Node B (350)

↓

Node E (500)

↓

Node C (650)
```

Originally Node C owned:

```
(350,650]
```

Now ownership becomes:

```
(350,500]

↓

Node E

-------------------

(500,650]

↓

Node C
```

Only partitions whose hashes fall within the new token range move.

Example:

```
Match123

↓

Hash = 582

↓

Still Node C
```

```
MatchABC

↓

Hash = 430

↓

Moves From Node C

↓

Node E
```

Only the affected token range is redistributed.

---

# Why Consistent Hashing?

Without Consistent Hashing:

Adding one server may require moving almost every partition.

With Consistent Hashing:

Only partitions belonging to the affected token ranges move.

This minimizes data movement and allows Cassandra to scale horizontally.

---

# Problem with One Token Per Node

Suppose each node owns only one token.

```
Node A

50 GB

----------------

Node B

500 GB

----------------

Node C

80 GB

----------------

Node D

450 GB
```

Data may become unevenly distributed.

---

Another problem:

Suppose Node B fails.

Node C inherits all of Node B's token range.

```
Node B

↓

DOWN

↓

Node C inherits everything
```

Node C becomes overloaded.

---

# Virtual Nodes (vnodes)

Modern Cassandra assigns **many tokens** to each physical node.

Example:

```
Node A

100

420

760

910

----------------

Node B

40

310

690

980

----------------

Node C

150

520

830

950

----------------

Node D

220

600

870

990
```

Each physical node now appears multiple times around the ring.

---

# Ring with Virtual Nodes

Instead of:

```
A

B

C

D
```

The ring becomes:

```
A

B

C

D

A

C

D

B

A

C

...
```

Ownership is spread across many small token ranges.

---

# Benefits of Virtual Nodes

- Better load balancing
- Reduced hotspots
- Faster cluster rebalancing
- Better fault tolerance
- Simpler node addition and removal

---

# Node Failure

Without Virtual Nodes:

```
Node B

↓

DOWN

↓

Node C inherits one huge range
```

With Virtual Nodes:

Node B owns many small token ranges.

Example:

```
40

↓

Node A

----------------

310

↓

Node D

----------------

690

↓

Node C

----------------

980

↓

Node A
```

Different nodes inherit different token ranges.

The load is naturally distributed.

---

# Adding a New Node

Suppose Node E joins.

Instead of taking one huge token range,

it receives many small token ranges from different nodes.

This results in much smoother rebalancing.

---

# What Does "256 Virtual Nodes" Mean?

It does **not** mean:

- 256 copies of the data
- 256 physical nodes

It means:

```
One Physical Node

↓

Owns 256 Virtual Tokens

↓

Each Virtual Token Owns One Token Range
```

Example:

```
Node A

↓

0-10

35-42

80-95

150-158

220-240

...

256 token ranges
```

---

# Hierarchy

```
Cluster
    │
    ▼
Physical Node
    │
    ▼
Virtual Nodes (Tokens)
    │
    ▼
Token Ranges
    │
    ▼
Partitions
    │
    ▼
Rows
```

Each Virtual Node owns a token range.

Each token range contains many partitions.

---

# Interview Questions

### Q: How does Cassandra determine which node owns a partition?

A strong answer:

> Cassandra hashes the partition key to generate a token. Each node owns one or more tokens on a logical ring. The partition belongs to the first node encountered when moving clockwise from the partition's token. Additional replicas are assigned to subsequent nodes clockwise according to the configured Replication Factor.

---

### Q: What happens when a new node joins the cluster?

A strong answer:

> Only the token ranges that the new node takes ownership of are redistributed. The remaining data stays where it is. This minimizes data movement and allows Cassandra to scale horizontally.

---

### Q: Why are Virtual Nodes better?

A strong answer:

> Virtual Nodes assign multiple token ranges to each physical node instead of a single large range. This improves load balancing, distributes ownership more evenly, reduces hotspots, and spreads data movement across many nodes during node addition, removal, or failure.

---

### Q: Does a node with 256 Virtual Nodes store 256 copies of the data?

A strong answer:

> No. Virtual Nodes do not duplicate data. They divide ownership into many small token ranges distributed around the ring. Each physical node owns multiple token ranges, and together those ranges determine which partitions the node stores.

---

# Key Takeaways

- Cassandra uses **Consistent Hashing** to distribute partitions.
- The partition key is hashed into a token.
- The partition belongs to the **first node encountered clockwise** on the token ring.
- Additional replicas are assigned to the next nodes clockwise according to the Replication Factor.
- Only affected token ranges move when nodes are added or removed.
- Modern Cassandra uses **Virtual Nodes (vnodes)**.
- A vnode represents ownership of a token range, not a physical node.
- Each physical node owns many virtual tokens.
- Virtual Nodes improve load balancing and simplify cluster scaling and recovery.

# Cassandra Compaction Strategies

## Why Do We Need Compaction?

Recall the Cassandra write path:

```
Write

↓

Commit Log

↓

MemTable

↓

Flush

↓

SSTable
```

Every MemTable flush creates a **new immutable SSTable**.

Over time:

```
SSTable1

↓

SSTable2

↓

SSTable3

↓

...

↓

Hundreds of SSTables
```

---

## Why is That a Problem?

Suppose we need to read:

```
Match123
```

Cassandra may need to check multiple SSTables.

```
Bloom Filter

↓

SSTable1 ?

↓

SSTable2 ?

↓

SSTable3 ?

↓

...

↓

SSTable500 ?
```

Although Bloom Filters help eliminate unnecessary disk reads, having many SSTables increases:

- Read latency
- Metadata overhead
- Disk space
- Duplicate versions of the same data

---

# Solution: Compaction

Compaction merges multiple SSTables into fewer, larger SSTables.

Example:

Before:

```
SSTable1

SSTable2

SSTable3

SSTable4
```

↓

Compaction

↓

After:

```
SSTable5
```

Old SSTables are deleted after the new SSTable is successfully written.

---

# What Happens During Compaction?

During compaction Cassandra:

- Reads multiple SSTables
- Merges rows
- Keeps the latest version of each row (Last Write Wins)
- Removes obsolete versions
- Removes expired tombstones (when safe)
- Writes a brand-new SSTable
- Deletes the old SSTables
- Compaction never modifies an existing SSTable. Instead, Cassandra reads multiple immutable SSTables, merges their contents into a brand-new SSTable, and once that new SSTable is fully written, it atomically replaces the old SSTables, which are then deleted. The original SSTables are never updated in place.

---

# Compaction Strategies

Different workloads require different compaction strategies.

| Strategy | Best For |
|-----------|----------|
| STCS | General-purpose / Write-heavy workloads |
| LCS | Read-heavy workloads |
| TWCS | Time-series workloads |

---

# 1. STCS (Size-Tiered Compaction Strategy)

STCS groups SSTables of **similar size**.

Example:

```
10 MB

12 MB

11 MB

↓

Merge

↓

33 MB
```

Later:

```
33 MB

35 MB

34 MB

↓

Merge

↓

102 MB
```

### Advantages

- Excellent write throughput
- Simple implementation
- Lower compaction overhead

### Disadvantages

- A partition may exist in multiple SSTables
- Reads may touch several SSTables
- Higher read amplification

---

# 2. LCS (Leveled Compaction Strategy)

Instead of grouping by size,

LCS organizes SSTables into **levels**.

```
Level 0

↓

Level 1

↓

Level 2

↓

Level 3
```

Within a level:

- SSTables have non-overlapping key ranges
- Reads usually examine only one SSTable per level

### Advantages

- Very fast reads
- Low read amplification
- Good for read-heavy applications

### Disadvantages

- More compaction work
- Higher write amplification
- Increased disk I/O

---

# 3. TWCS (Time Window Compaction Strategy)

TWCS groups SSTables by **time windows**.

Example:

```
10:00 - 11:00

↓

SSTables

↓

Compact Together
```

```
11:00 - 12:00

↓

SSTables

↓

Compact Together
```

```
12:00 - 1:00

↓

SSTables

↓

Compact Together
```

Each time window is compacted independently.

Older windows are rarely compacted again.

---

# Why is TWCS Ideal for Time-Series Data?

Example:

```
Football Match Events

Goal Events

Yellow Cards

Red Cards

Substitutions

VAR Events
```

During a live match:

- Events are continuously written

After the match:

- Data becomes mostly read-only

TWCS naturally groups writes for each time period and avoids repeatedly compacting historical data.

---

# TWCS Benefits

- Efficient writes
- Efficient compaction
- Minimal rewriting of historical data
- Works well with TTL-based expiration
- Excellent for logs, metrics, IoT, and sports events

---

# Which Strategy Would You Choose?

| Workload | Strategy |
|-----------|----------|
| General-purpose application | STCS |
| Read-heavy application | LCS |
| Time-series / Event data | TWCS |

---

# Apple Sports Example

Suppose we are storing:

```
Match Events

Goal Events

Player Statistics

Live Score Updates
```

These events are naturally ordered by time.

Most writes happen while the match is in progress.

Once the match finishes, the data becomes mostly immutable.

TWCS is an ideal choice because it compacts data within time windows and minimizes unnecessary rewrites of older match data.

---

# Comparison

| Feature | STCS | LCS | TWCS |
|----------|------|-----|------|
| Organizes By | SSTable Size | Levels | Time Windows |
| Read Performance | Medium | Excellent | Good |
| Write Performance | Excellent | Medium | Excellent |
| Write Amplification | Low | High | Low |
| Read Amplification | High | Low | Low (recent windows) |
| Best Use Case | General-purpose | Read-heavy | Time-series |

---

# Interview Questions

### Q: Why does Cassandra need compaction?

A strong answer:

> Every MemTable flush creates a new immutable SSTable. Without compaction, the number of SSTables would continue growing, increasing read latency and storage overhead. Compaction merges SSTables, removes obsolete row versions and expired tombstones, and produces fewer, larger SSTables.

---

### Q: Which compaction strategy would you choose for a live sports application?

A strong answer:

> I would choose Time Window Compaction Strategy (TWCS). Sports events are naturally time-series data. Writes occur continuously while a match is in progress, and historical match data becomes mostly immutable afterward. TWCS compacts SSTables within time windows, reducing unnecessary rewrites and providing efficient write performance.
> Because SSTables are grouped by time windows. Once a window is complete, its data becomes mostly immutable and is compacted within that window only. Unlike STCS, historical data is not repeatedly rewritten during future compactions, reducing write amplification and making TWCS ideal for append-only workloads like live sports events.

---

### Q: Why is LCS faster for reads?

A strong answer:

> LCS organizes SSTables into levels with non-overlapping key ranges. As a result, a read typically needs to examine at most one SSTable per level, significantly reducing read amplification compared to STCS.

---

### Q: Why is STCS good for writes?

A strong answer:

> STCS merges SSTables of similar size, resulting in fewer compaction operations and lower write amplification. This makes it a good default choice for write-heavy workloads.

---

# Key Takeaways

- Every MemTable flush creates a new immutable SSTable.
- Too many SSTables increase read latency and storage overhead.
- Compaction merges SSTables and removes obsolete data.
- STCS groups SSTables by size.
- LCS organizes SSTables into non-overlapping levels for fast reads.
- TWCS groups SSTables by time windows and is ideal for time-series workloads.
- For a live sports platform storing match events, **TWCS is typically the best choice** because the data is naturally time-ordered and becomes mostly immutable after the event.


# Secondary Indexes in Cassandra

## What is a Secondary Index?

A Secondary Index allows queries on non-primary key columns.

Example:

```sql
SELECT *
FROM MatchEvents
WHERE playerId = 'Messi';
```

without making `playerId` part of the primary key.

---

## Why are Secondary Indexes Discouraged?

Since data is distributed across many nodes, Cassandra may need to contact multiple nodes to satisfy an indexed query.

This can lead to higher latency and poor scalability.

---

## Preferred Cassandra Approach

Instead of creating a Secondary Index, Cassandra prefers **query-driven data modeling**.

Create another table optimized for the query.

Example:

```
MatchEvents
PK: (matchId, eventTimestamp)

↓

PlayerEvents
PK: (playerId, eventTimestamp)
```

This is called **denormalization**.

---

## Materialized Views

Materialized Views automatically maintain alternate query tables.

However, many production systems prefer maintaining denormalized tables in the application for greater control and predictable performance.

---

## Interview Takeaway

> Cassandra favors **creating new tables for new query patterns** rather than relying on Secondary Indexes, especially in large distributed systems.

# Choosing the Right Datastore - Apple Live Sports

The most important design principle is:

> **Choose the database based on the characteristics of the data and access patterns, not because one database can store everything.**

---

# 1. League Metadata

Example:

- Premier League
- La Liga
- Bundesliga

Characteristics:

- Small dataset
- Strong consistency
- Rare updates
- Relational

Typical Queries:

- Get League
- Update League
- List All Leagues

### Database

**PostgreSQL**

Reason:

- ACID transactions
- Rich querying
- Easy updates
- Relational data

---

# 2. Match Metadata

Example:

- MatchId
- Home Team
- Away Team
- Stadium
- Referee
- Kickoff Time
- Match Status

Characteristics:

- Relational
- Frequently updated before kickoff
- Multiple query patterns

Typical Queries:

- Today's matches
- Matches by league
- Matches by team
- Match details

### Database

**PostgreSQL**

Reason:

- Flexible indexes
- Multiple filtering options
- Strong consistency
- Relational model

---

# 3. Teams

Example:

- Team
- Coach
- City
- League
- Logo

Characteristics:

- Relational
- Low write volume

### Database

**PostgreSQL**

---

# 4. Players

Example:

- Player
- Team
- Position
- Nationality
- Age

Characteristics:

- Relational
- Frequently searched
- Low write volume

### Database

**PostgreSQL**

---

# 5. Match Events

Example:

- Goals
- Yellow Cards
- Red Cards
- VAR Events
- Substitutions

Characteristics:

- Extremely high write throughput
- Append-only
- Time-series
- Ordered by timestamp

Typical Queries:

- Match timeline
- All events for a match

### Database

**Cassandra**

Reason:

- High write throughput
- Horizontal scalability
- Excellent for append-only time-series data
- TWCS is ideal

---

# 6. Live Score

Example:

- Current Score
- Match Clock
- Possession
- Shots
- Corners

Characteristics:

- Millions of reads
- Frequent updates
- Very low latency required

### Database

**Redis**

Reason:

- In-memory storage
- Sub-millisecond to low-millisecond reads
- Ideal cache for live data

---

# 7. Leaderboards

Example:

- Top Scorers
- Top Assists
- Top Goalkeepers

Characteristics:

- Frequently updated
- Frequently read
- Ranking operations

### Database

**Redis (Sorted Sets)**

Reason:

- Efficient ranking
- Fast reads
- Built-in sorted data structures

---

# 8. User Data / Favorites

Example:

- Favorite Teams
- Favorite Players
- User Preferences

Characteristics:

- Transactional
- Relational
- Strong consistency

### Database

**PostgreSQL**

---

# 9. Notifications

Example:

- Goal Scored
- Match Started
- Red Card

Characteristics:

- Event-driven
- Asynchronous
- Fan-out to multiple consumers

### Technology

**Kafka**

Reason:

- Reliable event streaming
- Decouples producers and consumers
- Supports multiple downstream services

---

# 10. Analytics

Example:

- API Calls
- User Clicks
- App Usage
- Viewing Statistics

Characteristics:

- Massive volume
- Append-only
- Offline analysis

### Technology

```
Kafka

↓

Data Lake / Data Warehouse
```

---

# High-Level Architecture

```
                Feed Providers
                       │
                       ▼
                    Kafka
                       │
      ┌────────────────┼────────────────┐
      ▼                ▼                ▼
 Event Service    Score Service   Notification Service
      │                │                │
      ▼                ▼                ▼
 Cassandra         Redis          APNs / FCM
      │
      ▼
Historical Match Events

--------------------------------------------

Reference APIs

      │
      ▼

PostgreSQL

Leagues
Teams
Players
Match Metadata
Users
Favorites
```

---

# Interview Summary

| Data | Best Choice | Why? |
|------|-------------|------|
| League Metadata | PostgreSQL | Relational, ACID, small dataset |
| Match Metadata | PostgreSQL | Rich querying, multiple filters |
| Teams | PostgreSQL | Relational reference data |
| Players | PostgreSQL | Relational reference data |
| Match Events | Cassandra | High write throughput, time-series |
| Live Score | Redis | Extremely low-latency reads |
| Leaderboards | Redis | Sorted Sets for rankings |
| User Data | PostgreSQL | Transactional, relational |
| Notifications | Kafka | Event streaming and fan-out |
| Analytics | Kafka → Data Lake | Massive event ingestion |

---

# Key Takeaway

A senior system designer chooses the datastore based on:

- Data characteristics
- Access patterns
- Read/write ratio
- Consistency requirements
- Scalability requirements

**Not because one database can store every type of data.**

# Cassandra vs PostgreSQL

## PostgreSQL

Best for:

- Relational data
- ACID transactions
- Strong consistency
- Flexible querying
- Joins
- Foreign keys

Examples:

- Users
- Payments
- Orders
- Teams
- Players
- Match Metadata

---

## Cassandra

Best for:

- Massive write throughput
- Time-series data
- Append-only workloads
- Horizontal scalability
- Predictable query patterns
- High availability

Examples:

- Match Events
- Logs
- Metrics
- IoT Data
- Clickstream Events

---

# Data Model

### PostgreSQL

- Relational
- Supports joins
- Normalized schema

### Cassandra

- Query-driven
- Denormalized
- No joins
- One table per access pattern

---

# Consistency

### PostgreSQL

- Full ACID transactions
- Strong consistency
- Ideal for financial and transactional workflows

### Cassandra

- Tunable consistency
- Eventual consistency
- Limited transactional support (LWT)

---

# Querying

### PostgreSQL

Excellent for ad-hoc queries.

Example:

```sql
SELECT *
FROM Matches
WHERE league='Premier League'
AND city='London'
AND goals >= 3;
```

### Cassandra

Queries should be known in advance.

Schema is designed around query patterns.

New query patterns often require new tables.

---

# Scaling

### PostgreSQL

- Vertical scaling
- Read replicas for scaling reads
- Horizontal write scaling is more complex (typically requires sharding)

### Cassandra

- Peer-to-peer architecture
- Add more nodes to increase capacity
- Excellent horizontal scaling
- No master node

---

# Write Performance

### PostgreSQL

- Optimized for transactional consistency
- Updates indexes and transaction log

### Cassandra

Write path:

```
Commit Log

↓

MemTable

↓

Flush

↓

SSTable
```

Sequential writes make Cassandra ideal for high write throughput.

---

# Apple Sports Example

### Match Metadata

Use **PostgreSQL**

Reason:

- Relational
- Multiple query patterns
- Strong consistency
- Rich querying

---

### Match Events

Use **Cassandra**

Reason:

- High write throughput
- Append-only
- Time-series
- Horizontal scalability

---

# Interview Question

### Why PostgreSQL for Subscription Purchases?

> Subscription purchases require ACID transactions and strong consistency. We cannot risk charging a customer without recording the purchase or vice versa. PostgreSQL provides atomic transactions and immediate consistency, making it the correct choice.

---

### Why Cassandra for Match Events?

> Match events are append-only, time-series data generated at very high volume. Cassandra provides excellent write throughput, horizontal scalability, and predictable low-latency writes, making it a much better fit than a relational database.

---

# Quick Comparison

| Feature | PostgreSQL | Cassandra |
|----------|------------|-----------|
| Data Model | Relational | Wide-column / Denormalized |
| Transactions | Full ACID | Tunable/Eventual Consistency |
| Joins | ✅ | ❌ |
| Ad-hoc Queries | ✅ Excellent | ❌ Limited |
| Horizontal Scaling | Moderate | Excellent |
| Write Throughput | High | Extremely High |
| Time-Series | Good | Excellent |
| Best For | Transactional Systems | Event & Time-Series Systems |

---

# Key Takeaway

**PostgreSQL** is chosen when correctness, relationships, and flexible querying are the priority.

**Cassandra** is chosen when scalability, write throughput, availability, and time-series/event data are the priority.

# Request-Driven vs Event-Driven Architecture

One of the most important architectural concepts is understanding **what starts the work**.

---

# 1. Request-Driven Architecture

A client sends an API request.

Example:

```http
POST /purchase
```

Flow:

```
Client

↓

Purchase API

↓

Purchase Service

↓

Database
```

The HTTP request initiates the entire workflow.

Typical examples:

- Purchase Order
- User Registration
- Payment
- Login

---

# 2. Event-Driven Architecture

The application reacts to an event instead of an API request.

Example:

```
Goal Scored
```

Apple does not generate the goal.

Instead, an external sports provider sends the event.

```
Sports Provider

↓

Goal Event

↓

Our System
```

---

# How Do Events Enter the System?

## Option 1 (Most Common)

Sports provider calls an HTTP endpoint.

```
Sports Provider

↓

POST /feed/events

↓

Ingestion Service

↓

Kafka
```

The Ingestion Service:

- Validates the payload
- Performs authentication
- Publishes the event to Kafka

Very little business logic is performed here.

---

## Option 2

Sports provider publishes directly to Kafka.

```
Sports Provider

↓

Kafka

↓

Consumers
```

Common for internal enterprise integrations.

---

## Option 3

Sports provider streams data using WebSockets.

```
Sports Provider

↓

WebSocket

↓

Ingestion Service

↓

Kafka
```

Common for real-time feeds.

---

# Where Does Event-Driven Begin?

Before Kafka:

```
Sports Provider

↓

HTTP / WebSocket

↓

Ingestion Service
```

This is request/stream driven.

After Kafka:

```
Kafka

↓

Event Persistence Service

↓

Live Score Service

↓

Notification Service
```

Everything reacts to events.

No service directly calls another service's API.

---

# Apple Sports Architecture

```
              Sports Provider
                     │
             HTTP / WebSocket
                     │
                     ▼
             Ingestion Service
                     │
                     ▼
                   Kafka
      ┌──────────────┼──────────────┐
      ▼              ▼              ▼
Event Persistence  Live Score   Notification
     Service         Service       Service
      │              │              │
      ▼              ▼              ▼
 Cassandra         Redis         APNs / FCM
```

---

# Why Kafka?

Kafka decouples producers from consumers.

The Ingestion Service publishes one event.

Multiple services consume it independently.

Benefits:

- Independent scaling
- Loose coupling
- Easy to add new consumers
- Event replay
- Fault tolerance

---

# Redis vs Cassandra

When a goal is received:

```
Goal Event

↓

Kafka

↓

Score Service
```

The Score Service performs two writes.

### Redis

Updates the current state.

Example:

```
Score = 3-1

Minute = 80

Possession = 61%
```

Optimized for:

- Extremely fast reads
- Active matches

---

### Cassandra

Persists the event.

Example:

```
80' Goal - Saka

82' Yellow Card

85' Substitution
```

Optimized for:

- Durable storage
- Historical timeline
- High write throughput

---

# Why is Redis "Derived" from Cassandra?

Redis stores only the latest snapshot.

```
Score = 3-1
```

Cassandra stores the history.

```
15' Goal

42' Goal

80' Goal
```

Replaying the historical events reconstructs the latest score.

Therefore:

> **The current state stored in Redis is derived (computed) from the historical events persisted in Cassandra.**

---

# Redis Recovery

If Redis crashes during a live match:

### Option 1

Replay recent events from Kafka (if still retained).

### Option 2

Replay the current match's events from Cassandra.

Only the events for the active match need to be replayed—not months of historical data.

---

# Key Takeaways

- External providers usually send events via HTTP or WebSockets.
- The Ingestion Service publishes those events to Kafka.
- Kafka marks the beginning of the event-driven architecture.
- Multiple downstream services independently consume the same Kafka events.
- Redis stores the current computed state for fast reads.
- Cassandra stores the durable event history.
- Redis can always be rebuilt by replaying the match events from Kafka (within retention) or Cassandra.


# Why Kafka Instead of Direct Service Calls?

Without Kafka:

```
Sports Feed

↓

Ingestion Service

├──► Event Persistence Service
├──► Live Score Service
└──► Notification Service
```

Problems:

- Tight coupling between producer and consumers
- Higher request latency
- Failure in one downstream service can affect the entire request
- Difficult to add new consumers
- Producer must handle retries for every consumer
- Services cannot scale independently

---

# Kafka-Based Architecture

```
Sports Feed

↓

Ingestion Service

↓

Kafka

├──► Event Persistence Service
├──► Live Score Service
├──► Notification Service
└──► Analytics Service
```

Benefits:

- Producer only publishes events
- Consumers are decoupled
- Independent scaling
- Fault isolation
- Easy to add new consumers
- Event replay

---

# Kafka Hierarchy

```
Kafka Cluster
      │
      ▼
Brokers
      │
      ▼
Topics
      │
      ▼
Partitions
      │
      ▼
Messages (Events)
```

A message is:

- Published to a **Topic**
- Appended to a **Partition**
- That partition is physically stored on a **Broker**

---

# Apple Sports Example

```
Topic

match-events

├── Partition 0
├── Partition 1
└── Partition 2
```

Example:

```
Goal Event

↓

match-events

↓

Partition 1

↓

Broker 2
```

---

# Why Use matchId as the Partition Key?

Example:

```
Match123

10' Goal

20' Yellow Card

35' Red Card

60' Substitution
```

All events for the same match should remain in order.

Producer:

```java
new ProducerRecord<>(
    "match-events",
    matchId,
    event
);
```

Kafka hashes the `matchId`.

```
Hash(matchId)

↓

Partition 1
```

Every event for Match123 is appended to the same partition.

Kafka guarantees:

> **Ordering is preserved within a partition.**

---

# Different Matches Scale Horizontally

```
Partition 1

Match123 Events

-------------------

Partition 2

Match456 Events

-------------------

Partition 3

Match789 Events
```

Each match can be processed independently while preserving ordering within each match.

---

# Can We Split One Match Across Multiple Partitions?

Generally, **no**.

If events for the same match are written to different partitions:

```
Goal

↓

Partition 0

Yellow Card

↓

Partition 2

Red Card

↓

Partition 1
```

Different consumers process different partitions independently.

Kafka provides **no ordering guarantee across partitions**.

---

# Why Not Buffer and Reorder?

Possible idea:

- Hold events temporarily
- Sort by timestamp
- Process in order

Problems:

- How long should we wait?
- Late-arriving events become difficult to handle.
- Increases processing latency.
- Adds significant complexity.

---

# What If One Match Becomes a Hotspot?

For a normal football match, timeline events are relatively low.

Examples:

- Goals
- Cards
- VAR
- Substitutions

One partition is typically sufficient.

If processing much higher-frequency data (e.g., player tracking or telemetry), partition by a more granular key such as:

- `playerId`
- `sensorId`
- `cameraId`

This increases parallelism but changes the ordering guarantee from **per match** to **per player/sensor**.

---

# Interview Questions

### Q: What partition key would you choose for live sports events?

> I would partition by `matchId` so that all events for a match are routed to the same partition, preserving event order. Different matches are distributed across different partitions, enabling parallel processing across matches.

---

### Q: Why not partition by timestamp?

> Kafka guarantees ordering only within a partition. Partitioning by timestamp would distribute events from the same match across multiple partitions, breaking the event timeline.

---

### Q: What if one partition becomes a bottleneck?

> For match timelines, event volume per match is usually low enough that one partition is sufficient. If processing much higher-frequency data, I would partition by a more granular key (such as `playerId` or `sensorId`), accepting a different ordering guarantee while increasing parallelism.

---

# Key Takeaways

- Kafka decouples producers from consumers.
- Messages are written to **Topics**.
- Topics consist of **Partitions**.
- Partitions are physically stored on **Brokers**.
- Ordering is guaranteed **within a partition**, not across partitions.
- For live sports, **`matchId` is the ideal partition key** because it preserves the match timeline while allowing different matches to be processed in parallel.

# Kafka Producer

A **Producer** is the application that publishes messages to Kafka.

Apple Sports example:

```
Sports Feed

↓

Ingestion Service

↓

Kafka
```

The Ingestion Service acts as the Kafka Producer.

---

# What Does a Producer Send?

A producer sends:

- Topic
- Key
- Value

Example:

```java
ProducerRecord<String, MatchEvent>

Topic = "match-events"

Key = matchId

Value = Goal Event
```

---

# Topic

Logical category of messages.

Example:

```
match-events
```

---

# Key

Used for partitioning.

Example:

```
matchId
```

---

# Value

The actual event payload.

Example:

```json
{
  "minute": 80,
  "event": "GOAL",
  "player": "Saka"
}
```

---

# How Does Kafka Choose a Partition?

## Case 1 - Key Present (Most Common)

```
matchId

↓

Hash(matchId)

↓

Partition 2
```

Every event with the same key is routed to the same partition.

Ordering is preserved.

---

## Case 2 - No Key

Kafka distributes messages across partitions for load balancing.

Ordering between related messages is not guaranteed.

---

## Case 3 - Custom Partitioner

Developers can implement custom partitioning logic.

Example:

```
Premier League

↓

Partition 0

La Liga

↓

Partition 1
```

Less common than the default hash partitioner.

---

# Why Use matchId as the Partition Key?

Example:

```
Match123

10' Goal

20' Yellow Card

35' Red Card

60' Substitution
```

All events for the same match should remain in order.

Producer:

```java
new ProducerRecord<>(
    "match-events",
    matchId,
    event
);
```

Kafka hashes `matchId`.

```
Hash(matchId)

↓

Partition 1
```

All Match123 events go to Partition 1.

---

# Does the Producer Remember the Partition?

No.

The producer simply recomputes the hash every time.

Example:

```
Hash(Match123)

↓

582934

↓

582934 % 3

↓

Partition 1
```

Since hashing is deterministic, every producer computes the same partition for the same key.

No lookup table is required.

---

# What Happens If We Increase the Number of Partitions?

Example:

Initially:

```
3 partitions

Hash % 3

↓

Partition 1
```

Later:

```
6 partitions

Hash % 6

↓

Partition 4
```

Future messages for the same key may be routed to a different partition.

Existing messages remain in their original partition.

For topics requiring strict per-key ordering, partition counts should be planned carefully.

---

# Producer Application vs Kafka Producer Client

These are different concepts.

### Producer Application

Your business application.

Example:

```
Ingestion Service
```

---

### Kafka Producer Client

A library running **inside** the producer application.

Responsibilities:

- Serialize messages
- Compute partition
- Maintain broker metadata
- Find the leader broker
- Batch requests
- Retry failures
- Handle acknowledgements
- Send messages over the network

---

# Flow Inside the Producer

```
Your Application

↓

Kafka Producer Client

↓

Partitioner

↓

Hash(matchId)

↓

Partition

↓

Metadata Cache

↓

Leader Broker

↓

Append Message
```

---

# Does the Producer Send to a Broker or a Partition?

Logically:

The producer publishes to a **Topic**.

The Kafka Producer Client:

1. Computes the partition using the partitioner.
2. Looks up which broker is the leader for that partition.
3. Sends the message directly to that broker.

Flow:

```
Producer

↓

Topic

↓

Partition (chosen by Producer Client)

↓

Leader Broker

↓

Message Appended
```

---

# Kafka Hierarchy

```
Kafka Cluster
      │
      ▼
Brokers
      │
      ▼
Topics
      │
      ▼
Partitions
      │
      ▼
Messages
```

Messages are:

- Published to a Topic
- Assigned to a Partition
- Physically stored on the leader Broker for that partition

---

# Interview Questions

### Q: Who decides which partition a message goes to?

> The Kafka Producer Client (using the partitioner) determines the partition. By default, it hashes the message key (e.g., `matchId`) and maps it to a partition. It then uses cached cluster metadata to identify the leader broker and sends the message directly there.

---

### Q: Does the producer remember which partition a key belongs to?

> No. The producer recomputes the deterministic hash for every message. The same key always maps to the same partition (unless the number of partitions changes).

---

### Q: Where does the Kafka Producer Client run?

> The Kafka Producer Client is a library embedded inside the producer application. It handles serialization, partition selection, metadata lookup, batching, retries, acknowledgements, and communication with the Kafka cluster.

---

# Key Takeaways

- The producer publishes messages to a **Topic**.
- The **Kafka Producer Client** (library) decides the partition.
- Partition selection is typically based on `hash(key)`.
- The Producer Client uses cached metadata to determine the leader broker.
- The broker simply appends the message to the partition log.
- Using `matchId` as the partition key preserves event ordering for each match.

# Example - Producer Application Using Kafka

```java
@Service
public class IngestionService {

    private final KafkaTemplate<String, MatchEvent> kafkaTemplate;

    public void processEvent(MatchEvent event) {

        kafkaTemplate.send(
            "match-events",
            event.getMatchId(),
            event
        );
    }
}
```

In this example:

- `IngestionService` is the **Producer Application**.
- `KafkaTemplate` is Spring's wrapper around the Kafka Producer Client.
- Calling `kafkaTemplate.send()` ultimately invokes the **Kafka Producer Client**, which publishes the message to Kafka.

---

# What Happens Internally?

```
Your Application

↓

KafkaTemplate (Spring)

↓

Kafka Producer Client

↓

Serializer

↓

Partitioner

↓

Hash(matchId)

↓

Partition

↓

Metadata Cache

↓

Leader Broker

↓

Append Message
```

---

# Responsibilities of the Kafka Producer Client

The **Kafka Producer Client** is a library embedded inside the producer application.

It is responsible for:

- Serializing the message
- Selecting the partition (using the configured partitioner)
- Discovering brokers using cached cluster metadata
- Determining the leader broker for the selected partition
- Batching messages for higher throughput
- Compressing messages (if enabled)
- Retrying transient failures
- Waiting for acknowledgements (`acks`)
- Sending messages over the network to the Kafka cluster

The application simply invokes:

```java
kafkaTemplate.send("match-events", event.getMatchId(), event);
```

(or directly `producer.send(...)` when using the Kafka Java client).

Everything else is handled automatically by the **Kafka Producer Client**.

---

# Interview Summary

> The Kafka Producer Client is a library embedded inside the producer application. The application simply calls `producer.send()` (or Spring's `KafkaTemplate.send()`), while the client library handles serialization, partition selection, broker discovery using cached metadata, batching, retries, acknowledgements, and network communication with the Kafka cluster.


# Kafka Offsets

One of the biggest differences between Kafka and traditional message queues is how messages are consumed.

---

# Traditional Queue (RabbitMQ)

```
Producer

↓

Queue

↓

Consumer

↓

ACK

↓

Message Removed
```

Once a consumer successfully acknowledges (ACKs) a message, RabbitMQ removes it from the queue.

The message lifecycle is tied to **consumption**.

---

# Kafka

```
Producer

↓

Topic

↓

Partition (Append-Only Log)

↓

Consumer Reads

↓

Consumer Commits Offset

↓

Message Remains on Disk
```

Messages are **not deleted** after consumption.

Kafka retains them until its retention policy removes them.

---

# Example

Partition 1

```
Offset 0   Goal

Offset 1   Yellow Card

Offset 2   Red Card

Offset 3   Substitution
```

Consumer starts at:

```
Current Offset = 0
```

Processes:

```
Offset 0

Goal
```

Then commits:

```
Current Offset = 1
```

The next poll starts at Offset 1.

Notice:

```
Offset 0

Goal
```

still exists in Kafka.

Nothing is deleted.

---

# Who Updates the Offset?

The **consumer** controls the offset.

Kafka stores the committed offset for each consumer group.

There are two common modes.

---

## Auto Commit

The Kafka Consumer Client periodically commits offsets automatically.

---

## Manual Commit (Preferred)

The application processes the message first.

After successful processing:

```java
consumer.commitSync();
```

Only then is the offset committed.

This provides better control over retries and failure handling.

---

# Why Keep Messages?

Because multiple consumer groups may read the same event.

Example:

```
Goal Event
```

Consumer Group A:

```
Live Score Service
```

Consumer Group B:

```
Analytics Service
```

Consumer Group C:

```
Notification Service
```

Each maintains its own offset.

Example:

```
Partition

Offset 25

Goal

-----------------------

Live Score Offset = 26

Analytics Offset = 10

Notification Offset = 25
```

The message remains available until Kafka's retention policy removes it.

---

# When Are Messages Deleted?

Kafka does **not** delete messages after they are consumed.

Messages are removed only when:

- The configured retention time expires (e.g., 7 days)
- The configured retention size limit is exceeded

Message deletion depends on **retention**, not **consumption**.

---

# Kafka vs RabbitMQ

| Feature | Kafka | RabbitMQ |
|----------|--------|----------|
| Architecture | Distributed append-only log | Message queue |
| Storage | Disk-based | Queue (messages may be stored in memory and/or on disk) |
| Message after consumption | Retained until retention policy | Removed after successful ACK |
| Consumer progress | Offsets | ACK / Queue position |
| Replay messages | Easy | Not available after ACK (without additional mechanisms) |
| Multiple independent consumers | Excellent (Consumer Groups) | Requires exchanges/bindings; acknowledged messages are not replayed |
| Best For | Event streaming, analytics, logs | Task queues, work distribution, request-response |

---

# Key Difference

### Kafka

```
Producer

↓

Partition

↓

Consumer Reads

↓

Commit Offset

↓

Message Stays on Disk

↓

Retention Policy Deletes Later
```

---

### RabbitMQ

```
Producer

↓

Queue

↓

Consumer Reads

↓

ACK

↓

Message Removed
```

---

# Interview Questions

### Q: Does Kafka delete a message after it is consumed?

> No. Kafka retains messages independently of consumption. Consumers track their progress using offsets, and messages remain in the partition until Kafka's retention policy removes them.

---

### Q: Who updates the offset?

> The consumer controls when offsets are committed. After successfully processing a message, it commits the offset (either automatically or manually), and Kafka stores that position for the consumer group.

---

### Q: What is the biggest difference between Kafka and RabbitMQ?

> Kafka is a distributed append-only log where messages are retained independently of consumption and consumers track their progress using offsets. RabbitMQ is a message queue where messages are typically removed after they are successfully acknowledged by a consumer. Kafka is optimized for event streaming and replay, while RabbitMQ is optimized for reliable work distribution and task processing.