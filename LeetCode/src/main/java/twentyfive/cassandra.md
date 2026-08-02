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