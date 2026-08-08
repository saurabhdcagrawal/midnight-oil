# Redis Interview Notes (Part 1) – Data Structures

# What is Redis?

Redis is an **in-memory key-value data store** that supports rich data structures such as Strings, Hashes, Sets, Sorted Sets, Lists, and Streams.

Because data resides primarily in memory, Redis provides **sub-millisecond latency**, making it ideal for caching, leaderboards, session storage, counters, distributed locks, and pub/sub.

Key characteristics:

* In-memory
* Extremely fast
* Rich data structures
* Optional persistence (RDB/AOF)
* Supports horizontal scaling using Redis Cluster

---

# Why Redis?

Redis is **not** a replacement for PostgreSQL.

PostgreSQL remains the **source of truth**.

Redis serves as a **low-latency cache** and read optimization layer.

Architecture:

```text
                PostgreSQL
          (Source of Truth)
                  │
                  │
         Frequently Accessed Data
                  │
                  ▼
               Redis
                  │
                  ▼
             APIs / Services
```

Benefits:

* Reduces database load
* Sub-millisecond reads
* Handles very high read throughput
* Improves API latency

---

# Apple Sports Use Cases

| Data                | Redis Structure |
| ------------------- | --------------- |
| Current Match State | Hash            |
| Match Status        | String          |
| Followers           | Set             |
| Device Tokens       | Set             |
| Leaderboards        | Sorted Set      |
| Counters            | String + INCR   |
| Idempotency         | String + SETNX  |

---

# Redis Key Naming Convention

Redis does **not** interpret `:` specially.

It is simply a naming convention used to organize keys.

Examples:

```text
match:123:status

user:101

followers:team:Lakers

devices:user:101
```

The colon improves readability and groups related keys logically.

---

# String

## Use Case

Store a single value.

Examples:

```redis
SET match:123:status LIVE

GET match:123:status
```

Result:

```text
LIVE
```

---

## Counters

```redis
SET requestCounter 100

INCR requestCounter
```

Result:

```text
101
```

Another increment:

```redis
INCR requestCounter
```

Result:

```text
102
```

Redis stores the value as a **String**.

`INCR` works only if the String contains a valid integer.

If the key does not exist:

```redis
INCR requestCounter
```

Redis treats the value as `0` and creates:

```text
requestCounter = 1
```

---

## Atomic Operations

`INCR` is atomic.

Instead of:

```text
GET

↓

Increment

↓

SET
```

being executed separately (causing race conditions),

Redis performs the entire increment as one indivisible operation.

Benefits:

* No lost updates
* Thread-safe across concurrent clients
* Ideal for counters

---

## Time Complexity

| Operation | Complexity |
| --------- | ---------- |
| SET       | O(1)       |
| GET       | O(1)       |
| INCR      | O(1)       |

---

# Hash

## Use Case

Store one object with multiple attributes.

Instead of multiple Redis keys:

```text
match:123:status

match:123:homeScore

match:123:awayScore
```

Use one Redis key:

```text
match:123
```

Fields:

```text
status      -> LIVE

homeScore   -> 100

awayScore   -> 98

quarter     -> 4

venue        -> Chase Center
```

---

## Commands

Create / Update fields:

```redis
HSET match:123 status LIVE

HSET match:123 homeScore 100

HSET match:123 awayScore 98
```

Read one field:

```redis
HGET match:123 homeScore
```

Read another field:

```redis
HGET match:123 status
```

Read the entire object:

```redis
HGETALL match:123
```

Redis also supports setting multiple fields in one command:

```redis
HSET match:123 status LIVE homeScore 100 awayScore 98
```

---

## Time Complexity

Most operations are:

```text
O(1)
```

---

## When to Use

Use a Hash when storing a logical object with multiple related fields.

Examples:

* Match State
* User Profile
* Device Metadata

---

# Set

## Use Case

Store a collection of **unique** values.

Order is **not guaranteed**.

Apple Sports example:

```text
followers:team:Lakers

↓

101

205

301
```

Perfect for storing follower IDs.

---

## Commands

Add:

```redis
SADD followers:team:Lakers 101
```

Get all members:

```redis
SMEMBERS followers:team:Lakers
```

Remove:

```redis
SREM followers:team:Lakers 101
```

Membership check:

```redis
SISMEMBER followers:team:Lakers 101
```

---

## Characteristics

* Unique elements
* No duplicates
* Unordered
* Fast membership checks

---

## Time Complexity

| Command   | Complexity |
| --------- | ---------- |
| SADD      | O(1)       |
| SREM      | O(1)       |
| SISMEMBER | O(1)       |
| SMEMBERS  | O(N)       |

---

## Apple Sports Examples

Followers:

```text
followers:team:Lakers

↓

{101,205,301}
```

Device Tokens:

```text
devices:user:101

↓

token1

token2

token3
```

User Subscriptions:

```text
subscriptions:user:101

↓

Lakers

NBA

LeBron
```

---

# Sorted Set (ZSET)

## Use Case

Store **unique members** along with a **numeric score**.

Redis automatically keeps members sorted by score.

Perfect for:

* Leaderboards
* Rankings
* Trending Content
* Top Players

---

## General Syntax

```redis
ZADD key score member
```

Score comes **before** the member.

Example:

```redis
ZADD leaderboard_NFL_2026 1000 Saurabh 950 Rahul 900 John
```

Conceptually:

```text
leaderboard_NFL_2026

↓

Saurabh -> 1000

Rahul -> 950

John -> 900
```

Redis automatically maintains the ordering.

---

## Increase Score

Example:

```redis
ZINCRBY leaderboard_NFL_2026 3 LeBron
```

Redis updates LeBron's score and reorders the leaderboard if necessary.

---

## Get Top Players

Top 3:

```redis
ZREVRANGE leaderboard_NFL_2026 0 2
```

`REV` returns the highest scores first.

---

## Get Player Score

```redis
ZSCORE leaderboard_NFL_2026 LeBron
```

---

## Remove Player

```redis
ZREM leaderboard_NFL_2026 LeBron
```

---

## Time Complexity

| Command   | Complexity   |
| --------- | ------------ |
| ZADD      | O(log N)     |
| ZINCRBY   | O(log N)     |
| ZREM      | O(log N)     |
| ZSCORE    | O(1)         |
| ZREVRANGE | O(log N + M) |

---

## Apple Sports Examples

Fantasy Leaderboard

```text
User101 -> 250

User205 -> 240

User301 -> 225
```

NBA Scoring Leaderboard

```text
LeBron -> 35

Curry -> 28

Luka -> 24
```

Trending Teams

```text
Lakers -> 120000 searches
```

Most Viewed Matches

```text
Match123 -> 2M views
```

---

# Mental Model

## String

One value

```text
match:123:status

↓

LIVE
```

---

## Hash

One object

```text
match:123

↓

status

homeScore

awayScore

quarter
```

---

## Set

Unique collection

```text
followers:team:Lakers

↓

101

205

301
```

---

## Sorted Set

Unique collection with ranking

```text
leaderboard

↓

35  LeBron

28  Curry

24  Luka
```

---

# Interview Summary

| Data Structure | Use Case                                                              |
| -------------- | --------------------------------------------------------------------- |
| String         | One value, counters, sessions                                         |
| Hash           | Objects with multiple fields                                          |
| Set            | Unique collections (followers, subscriptions, device tokens)          |
| Sorted Set     | Ranked collections (leaderboards, trending content, fantasy rankings) |

## Key Interview Rule

* **String** → One value
* **Hash** → One object
* **Set** → Unique collection
* **Sorted Set** → Unique collection with ranking


# Redis Data Structures — Interview Cheat Sheet

## 1. String

### Structure

Key → Value

### Commands

    SET match:123:status LIVE
    GET match:123:status
    DEL match:123:status

    INCR match:123:views
    INCRBY match:123:views 10
    DECR match:123:views

    EXPIRE match:123:status 60

### Example

    match:123:status → LIVE

### Use Cases

- Match status
- Counters
- Cache values
- Idempotency keys

### Important

Redis Strings can contain numeric values, but Redis does not have a separate Integer data type.

    SET requestCounter 100
    INCR requestCounter

Result:

    101

`INCR` is atomic.

---

# 2. Hash

### Structure

Key → Field → Value

Use when representing **one object with multiple attributes**.

### Example

    match:123

        status      → LIVE
        homeScore   → 100
        awayScore   → 98
        quarter     → 4

### Commands

    HSET match:123 status LIVE
    HSET match:123 homeScore 100
    HSET match:123 awayScore 98

    HGET match:123 homeScore

    HGETALL match:123

    HDEL match:123 venue

Multiple fields:

    HSET match:123 status LIVE homeScore 100 awayScore 98

### Use Cases

- Current match state
- User profile
- Device metadata
- Frequently accessed objects

---

# 3. Set

### Structure

Key → Members

A Set contains **unique members** and has **no guaranteed ordering**.

Think:

    Set<Integer>

### Example

    followers:team:Lakers

        {101, 205, 301, 450}

### Commands

    SADD followers:team:Lakers 101

    SREM followers:team:Lakers 101

    SISMEMBER followers:team:Lakers 101

    SMEMBERS followers:team:Lakers

    SCARD followers:team:Lakers

### Use Cases

#### Team Followers

    SADD followers:team:Lakers 101

#### Device Tokens

    devices:user:101

        {tokenA, tokenB, tokenC}

#### User Subscriptions

    subscriptions:user:101

        {Lakers, NBA, LeBron}

### Why Set?

Use a Set when:

- Values must be unique
- Ordering does not matter
- You need fast membership checks

---

# 4. Sorted Set (ZSET)

### Structure

Key → (Score, Member)

A Sorted Set contains **unique members**, and every member has a numeric score.

Redis automatically maintains ordering based on the score.

### Example

    leaderboard

        Saurabh → 1000
        Rahul   → 950
        John    → 900

### Add Members

Syntax:

    ZADD key score member

Example:

    ZADD leaderboard 1000 Saurabh
    ZADD leaderboard 950 Rahul
    ZADD leaderboard 900 John

Multiple members:

    ZADD leaderboard 1000 Saurabh 950 Rahul 900 John

### Update Score

    ZINCRBY leaderboard 10 Saurabh

Saurabh's score becomes:

    1010

Redis automatically adjusts the ranking.

### Get Score

    ZSCORE leaderboard Saurabh

### Remove Member

    ZREM leaderboard Saurabh

### Get Rankings

Lowest → highest:

    ZRANGE leaderboard 0 9

Highest → lowest:

    ZREVRANGE leaderboard 0 9

For a leaderboard, normally use:

    ZREVRANGE leaderboard 0 9

because highest scores should appear first.

### Count Members

    ZCARD leaderboard

---

# Apple Sports Use Cases

## Player Leaderboard

    playerLeaderboard

        LeBron → 35
        Curry  → 28
        Luka   → 24

## Fantasy Leaderboard

    fantasyLeaderboard

        user101 → 250
        user205 → 240
        user301 → 225

## Trending Teams

    trendingTeams

        Lakers → 120000
        Knicks → 100000
        Celtics → 95000

## Most Viewed Matches

    mostViewedMatches

        match123 → 2,000,000
        match456 → 1,500,000

---

# Set vs Sorted Set

## Set

    Unique members
    No ordering

Example:

    followers:team:Lakers

    {101, 205, 301}

## Sorted Set

    Unique members
    + Numeric score
    + Ranking

Example:

    leaderboard

    Saurabh → 1000
    Rahul   → 950
    John    → 900

### Mental Shortcut

    Need uniqueness?
        → SET

    Need uniqueness + ranking?
        → SORTED SET

---

# Redis Data Structure Mental Model

## String

    Key → Value

    match:123:status → LIVE

## Hash

    Key → Field → Value

    match:123
        status    → LIVE
        homeScore → 100
        awayScore → 98

## Set

    Key → Members

    followers:team:Lakers
        {101, 205, 301}

## Sorted Set

    Key → (Score, Member)

    leaderboard
        Saurabh → 1000
        Rahul   → 950
        John    → 900

---

# Interview Decision Tree

    What are you storing?
            │
            ├── One value
            │      ↓
            │    STRING
            │
            ├── One object with multiple fields
            │      ↓
            │    HASH
            │
            ├── Collection of unique values
            │      ↓
            │    SET
            │
            └── Collection of unique values + ranking
                   ↓
              SORTED SET

# The Four to Memorize

    String      → Key → Value

    Hash        → Key → Field → Value

    Set         → Key → Members

    Sorted Set  → Key → (Score, Member)

---

# Command + Structure Reference

| Data Structure | Structure | Command | What it does | Apple Sports Example |
|---|---|---|---|---|
| **String** | `Key → Value` | `SET` | Store value | `SET match:123:status LIVE` |
| | | `GET` | Read value | `GET match:123:status` |
| | | `DEL` | Delete key | `DEL match:123:status` |
| | | `INCR` | Atomic +1 | `INCR match:123:views` |
| | | `INCRBY` | Atomic +N | `INCRBY match:123:views 10` |
| | | `DECR` | Atomic -1 | `DECR inventory:123` |
| | | `EXPIRE` | Set TTL | `EXPIRE match:123:status 60` |
| **Hash** | `Key → Field → Value` | `HSET` | Set field(s) | `HSET match:123 status LIVE` |
| | | `HGET` | Get one field | `HGET match:123 status` |
| | | `HGETALL` | Get all fields | `HGETALL match:123` |
| | | `HDEL` | Delete field | `HDEL match:123 venue` |
| **Set** | `Key → Members` | `SADD` | Add unique member | `SADD followers:team:Lakers 101` |
| | | `SREM` | Remove member | `SREM followers:team:Lakers 101` |
| | | `SISMEMBER` | Check membership | `SISMEMBER followers:team:Lakers 101` |
| | | `SMEMBERS` | Get all members | `SMEMBERS followers:team:Lakers` |
| | | `SCARD` | Count members | `SCARD followers:team:Lakers` |
| **Sorted Set** | `Key → (Score, Member)` | `ZADD` | Add member + score | `ZADD leaderboard 100 Saurabh` |
| | | `ZINCRBY` | Increment score | `ZINCRBY leaderboard 10 Saurabh` |
| | | `ZSCORE` | Get member score | `ZSCORE leaderboard Saurabh` |
| | | `ZREM` | Remove member | `ZREM leaderboard Saurabh` |
| | | `ZRANGE` | Lowest → highest | `ZRANGE leaderboard 0 9` |
| | | `ZREVRANGE` | Highest → lowest | `ZREVRANGE leaderboard 0 9` |
| | | `ZCARD` | Count members | `ZCARD leaderboard` |

---

# Quick Command List

## String

    SET
    GET
    DEL
    INCR
    INCRBY
    DECR
    EXPIRE

## Hash

    HSET
    HGET
    HGETALL
    HDEL

## Set

    SADD
    SREM
    SISMEMBER
    SMEMBERS
    SCARD

## Sorted Set

    ZADD
    ZINCRBY
    ZSCORE
    ZREM
    ZRANGE
    ZREVRANGE
    ZCARD

## Highest-Priority Commands to Memorize

    SET / GET
    INCR
    EXPIRE

    HSET / HGET / HGETALL

    SADD / SREM / SISMEMBER / SMEMBERS

    ZADD / ZINCRBY / ZSCORE / ZREVRANGE / ZREM
	
	
	
	# Redis TTL / Expiration

## Commands

    EXPIRE match:123:status 100
    TTL match:123:status
    SET match:123:status LIVE EX 60

- `EXPIRE` → sets TTL on an existing key.
- `TTL` → returns remaining seconds.
- `SET ... EX 60` → sets the value and TTL together.

## Why TTL?

1. Prevent **unlimited cache growth**.
2. Automatically remove **stale data**.
3. Act as a **safety mechanism** if cache updates stop.

## Apple Sports

We have an authoritative event stream, so we can update Redis directly when an event arrives:

    Authoritative Game Event
            ↓
    Game State Consumer
            ↓
    Update Redis
            ↓
    match:123
    status = LIVE
    score = 100:98

TTL is still useful as a safety net.

## When TTL Reaches 0

    TTL → 0
       ↓
    Key expires
       ↓
    GET → MISS

The key is no longer available.

## Interview Takeaway

> **TTL prevents stale data and unlimited cache growth. For live sports, authoritative events directly update Redis; TTL provides an additional safety net.**

# Redis SETNX — Idempotency

Use the deterministic `eventId` as the Redis key:

    event:event-123 → processed

## SETNX Redis Command

This is the actual Redis command. You can run it directly in the Redis CLI / console:

    SETNX event:event-123 processed

First time:

    (integer) 1

→ Key was created → process the event.

Duplicate:

    (integer) 0

→ Key already exists → skip the event.

## Java / Spring Application

Our Java application normally does NOT execute `SETNX` by typing it into a console.

Instead, it uses a Redis client/library such as Spring Data Redis.

### Java Code

    boolean firstTime =
        redisTemplate.opsForValue()
            .setIfAbsent(
                "event:" + event.getEventId(),
                "processed"
            );

    if (Boolean.TRUE.equals(firstTime)) {
        notificationService.send(event);
    } else {
        // Duplicate → skip
    }

The library communicates with Redis:

    Java Application
          ↓
    Spring Data Redis
          ↓
    Redis Client
          ↓
    Redis Server

## Better Pattern — SET + NX + TTL

Redis command:

    SET event:event-123 processed NX EX <TTL>

- `NX` → only create if key doesn't exist
- `EX` → automatically expire the key
- Prevents deduplication keys from staying in Redis forever

### Java Code With TTL

    boolean firstTime =
        redisTemplate.opsForValue()
            .setIfAbsent(
                "event:" + event.getEventId(),
                "processed",
                Duration.ofHours(<TTL>)
            );

    if (Boolean.TRUE.equals(firstTime)) {
        notificationService.send(event);
    } else {
        // Duplicate → skip
    }

## Flow

    Event
      ↓
    SET event:eventId processed NX EX <TTL>
      ↓
    Result
      ↓
    1 → First time → Process
    0 → Duplicate   → Skip

## Interview Takeaway

> `SETNX` atomically creates a key only if it doesn't exist. In Java, we normally use a Redis client/library API rather than typing Redis commands directly. `SET NX + TTL` provides atomic deduplication plus automatic cleanup.

# Redis Lua + Distributed Lock

## Why Lua?

Individual Redis commands such as `INCR` and `SET NX EX` are already atomic.

We need Lua when we have **multiple dependent Redis operations that must behave as one atomic operation**.

Example:

    GET lock
    ↓
    Check owner
    ↓
    DEL lock

The problem is that another instance could acquire the lock between the `GET` and `DEL`.

Lua allows us to perform the check + delete atomically inside Redis.

---

# Distributed Lock

Suppose multiple instances are trying to process the same resource:

    Service A
    Service B
    Service C
         ↓
       Redis

## Acquire Lock

    SET lock:match:123 <uniqueInstanceId> NX EX 30

- `NX` → acquire only if lock doesn't already exist
- `EX 30` → automatically release after 30 seconds
- `<uniqueInstanceId>` → identifies who owns the lock

If Service A gets:

    1 → Lock acquired

If Service B gets:

    0 → Lock already exists

---

# Why Do We Need a Unique ID?

Suppose:

    lock:match:123 → Service-A

Service A crashes.

After the TTL expires:

    lock expires

Service B acquires it:

    lock:match:123 → Service-B

Service A could later wake up.

It must NOT delete Service B's lock.

So the value identifies the owner:

    lock:match:123 → Service-B

---

# Important: Redis Does Not Block Other Instances

A distributed lock is a **logical application-level lock**.

Redis does not prevent Service A from reading the key just because Service B owns it.

Service A can do:

    GET lock:match:123

and receive:

    Service-B

Service A compares:

    Service-B == Service-A

    false

Therefore Service A must NOT delete the lock.

---

# Why Lua Is Needed for Release

Doing this separately is unsafe:

    GET lock:match:123
    ↓
    Check value == myInstanceId
    ↓
    DEL lock:match:123

Another service could acquire the lock between the check and delete.

Instead, Lua performs:

    Check owner
        ↓
    If owner == myInstanceId
        ↓
    Delete lock

Redis executes the Lua script atomically.

Conceptually:

    if Redis.get(lockKey) == myInstanceId:
        Redis.delete(lockKey)

---

# Scenario

    Service A
        ↓
    SET lock:match:123 A NX EX 30
        ↓
    1 → A owns lock
        ↓
    A crashes
        ↓
    TTL expires
        ↓
    Service B acquires lock
        ↓
    lock:match:123 → B
        ↓
    A wakes up
        ↓
    A cannot safely delete B's lock

Lua ensures:

    If value == A
        → DELETE

    If value != A
        → DO NOTHING

---

# Idempotency vs Distributed Lock

## Idempotency

Question:

    "Have I already processed this event?"

    SET event:event-123 processed NX EX <TTL>

    1 → First time → Process
    0 → Duplicate → Skip

## Distributed Lock

Question:

    "Can I be the instance processing this resource right now?"

    SET lock:match:123 <uniqueId> NX EX 30

    1 → Lock acquired
    0 → Someone else owns it

---

# Interview Takeaway

> `SET NX EX` is enough to acquire a distributed lock. We use a unique owner ID so a stale/crashed instance cannot delete another instance's lock. Lua is useful when releasing the lock because checking ownership and deleting the lock must happen atomically.


# Redis Pub/Sub

Redis Pub/Sub is a **real-time broadcast mechanism**, not a durable event backbone like Kafka.

## Commands

Publisher:

    PUBLISH match:123 score-update

Subscriber:

    SUBSCRIBE match:123

## Mini Diagram

    Game State Service
           │
           │ PUBLISH
           ↓
         Redis
           │
           ├────────→ Notification Service
           │
           ├────────→ WebSocket Service
           │
           └────────→ Other subscribers

## Key Points

- Redis broadcasts the message to **currently connected subscribers**.
- No persistent storage or replay.
- Disconnected subscribers miss messages.
- If Redis goes down, messages can be lost.
- It is **not publishing to other Redis layers**; Redis delivers the message to application services that subscribed to the channel.
- For our Apple Sports design, **Kafka handles durable event distribution; Redis handles fast state/cache access.**

# Redis Replication + Failure Handling

Redis can have:

        Redis Primary
             │
       replication
        ┌────┴────┐
        ↓         ↓
    Replica 1  Replica 2

The primary handles writes, while replicas maintain copies of the data and can be used for reads.

## Why do we care?

If the primary dies:

        Primary 💥
            ↓
    Replica can be promoted
            ↓
       New Primary

This gives us **high availability**.

## Replication Lag

Replication is generally asynchronous, so replicas can briefly be behind the primary.

        Write
          ↓
    Primary = 101
          ↓
    replication lag
          ↓
    Replica = 100 → 101

Therefore, don't blindly send every read to replicas.

## Sports Examples

### Primary — freshness matters

- Current score
- Current possession
- Game clock
- Current play
- Live game state
For live score / current game state:Primary for reads where freshness is critical

### Replicas — slight staleness is acceptable

- Historical player stats
- Historical game data
- Rankings/leaderboards where tiny lag is acceptable

### Rule

> Freshest state required → **Primary**  
> Slight staleness acceptable → **Replica**

## For Our Apple Sports Architecture

Redis is mainly our **fast serving/state layer**:

        Kafka
          ↓
        Flink
          ↓
    Redis Primary
          ↓
       Replicas
          ↓
    API / WebSocket Services

We don't want Redis to be our source of truth.

If Redis loses data, we can rebuild the state from **Kafka/events or the authoritative sports data source**.

## Why Replicas?

- **Read scalability** → distribute read-heavy workloads.
- **High availability** → a replica can be promoted if the primary fails.
- **Lower load on primary** → replicas can handle reads where slight staleness is acceptable.

## Interview Phrase

> Redis improves latency and scalability, but Kafka/authoritative systems provide durability. Redis can be rebuilt if necessary.


# Redis Cluster / Sharding

## What is Redis Cluster?

Redis Cluster allows Redis data to be **distributed across multiple Redis primary nodes** instead of keeping everything on one Redis server.

This provides:

* **Horizontal scalability** → distribute data and traffic across nodes.
* **Higher throughput** → multiple primaries can handle requests.
* **High availability** → replicas can be used for failover.

  Redis Cluster
  │
  ┌────┼────┐
  ↓    ↓    ↓
  P1   P2   P3
  │    │    │
  R1   R2   R3

P = Primary
R = Replica

## How Does Redis Know Which Node Has a Key?

The application creates a normal Redis key:

```
SET match:123:status LIVE
```

Here:

```
Key   = match:123:status
Value = LIVE
```

Redis Cluster takes the **key** and hashes it:

```
match:123:status
        ↓
      Hash
        ↓
    Hash Slot
        ↓
Redis Primary / Shard
```

The application does **not** directly tell Redis which physical shard to use.

Redis Cluster determines the shard based on the key.

For example:

```
match:123:status → Primary 1
match:456:status → Primary 2
```

The exact shard depends on the hash result and cluster slot mapping.

## Hash Tags `{}`

Sometimes we want related keys to be stored on the **same shard**.

Example:

```
SET match:{123}:status LIVE
SET match:{123}:score 100
SET match:{123}:clock 02:31
```

Redis uses the value inside `{}` for hashing:

```
match:{123}:status
        ↓
      {123}
        ↓
      Hash
        ↓
    Hash Slot
        ↓
    Same Shard
```

Therefore:

```
match:{123}:status
match:{123}:score
match:{123}:clock

        ↓

    Same Shard
```

Without `{}`:

```
match:123:status
match:123:score
match:123:clock
```

Redis hashes the keys normally, so they **could end up on different shards**.

## Sports Example

For a live match, we may want related state together:

```
SET match:{123}:status LIVE
SET match:{123}:score 100
SET match:{123}:clock 02:31
```

All use `{123}`, so Redis Cluster routes them to the **same hash slot and therefore the same shard**.

## Redis Hash vs Redis Cluster

### Redis Hash

A Redis Hash is a **data structure** that stores multiple field-value pairs under one Redis key.

Example:

```
HSET match:123 status LIVE score 100
```

Structure:

```
match:123
    ├── status → LIVE
    └── score  → 100
```

### Redis Cluster / Sharding

Redis Cluster is about **distributing Redis keys across multiple Redis primaries**.

```
Key
 ↓
Hash
 ↓
Hash Slot
 ↓
Redis Primary / Shard
```

## Important Distinction

```
Redis Hash
→ Data structure
→ Stores field-value pairs under one key

Redis Cluster
→ Distributes Redis keys across multiple primary nodes

Hash Tag { }
→ Allows related keys to be routed to the same hash slot
```

## Interview Takeaway

> **Redis Cluster distributes keys across multiple primary nodes for horizontal scalability. The application designs the key, Redis hashes it to determine the hash slot and shard. Hash tags `{}` can be used to keep related keys on the same shard.**


# Redis Cluster: Sharding vs Replication

## Redis Cluster / Sharding

**Sharding provides horizontal scalability for data and traffic.**

```text
Redis Cluster
     │
 ┌───┼───┐
 ↓   ↓   ↓
P1  P2  P3
A   B   C
```

Different data is distributed across multiple primary nodes.

> **Sharding → scale capacity and read/write throughput by distributing data.**

## Replication

**Replication provides high availability and can also scale reads.**

```text
Primary
   ↓
Replica
   ↓
Replica
```

Replicas contain **copies of the same data**.

> **Replication → high availability + optional read scaling.**

## Using Both Together

Redis Cluster can use **sharding + replication**:

```text
             Redis Cluster
                  │
        ┌─────────┼─────────┐
        ↓         ↓         ↓
     Primary   Primary   Primary
        │         │         │
     Replica   Replica   Replica
```

Each primary owns a different portion of the data, while its replica maintains a copy for failover.

## Interview Takeaway

> **Sharding → horizontally scale the dataset and traffic across primaries.**
>
> **Replication → provide high availability and, where appropriate, scale reads.**
