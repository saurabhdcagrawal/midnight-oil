# Apache Kafka Handbook

### Complete Interview + System Design + Production Engineering Notes

### Author: Saurabh Agrawal

---

# Table of Contents

## PART I - STREAMING FUNDAMENTALS

### Page 1

1. Batch Processing
2. Stream Processing
3. Polling vs Notifications
4. Message Brokers
5. Messaging Patterns

   * Load Balancing
   * Fan Out
   * Hybrid Pattern

---

## PART II - KAFKA FUNDAMENTALS

### Page 2

6. What is Apache Kafka?
7. Topics
8. Partitions
9. Brokers
10. Consumer Groups
11. Offsets
12. Ordering
13. Replication
14. Fault Tolerance

---

## PART III - KAFKA ARCHITECTURE

### Page 3

15. Log Based Architecture
16. Append Only Log
17. Partitioning Strategies
18. Key Based Partitioning
19. Round Robin Partitioning
20. Custom Partitioning

---

## PART IV - PARALLELISM & ORDERING

### Page 4

21. Why Partitions Exist
22. Maximum Parallelism
23. Consumers vs Partitions
24. Ordering Guarantees
25. Global Ordering Problem
26. Real World Ordering Examples

---

## PART V - RELIABILITY

### Page 5

27. Producer Acknowledgments
28. Consumer Acknowledgments
29. Offset Commit Strategies
30. Replication Factor
31. Min In Sync Replicas
32. Durability Tradeoffs

---

## PART VI - IDEMPOTENCY

### Page 6

33. Why Idempotency Exists
34. Producer Sequence Numbers
35. Duplicate Prevention
36. Ordering Protection
37. Failure Scenarios

---

## PART VII - DLQ

### Page 7

38. Dead Letter Queues
39. Poison Pill Messages
40. Retry Strategies
41. Spring Kafka DLQ

---

## PART VIII - REPLAY

### Page 8

42. Replay Fundamentals
43. Offset Reset
44. New Consumer Groups
45. Seek API
46. Replay Use Cases

---

## PART IX - SPRING KAFKA

### Page 9

47. Manual Commits
48. Error Handlers
49. DLQ Integration
50. Production Best Practices

---

## PART X - INTERVIEW QUESTIONS

### Page 10

51. Kafka Interview Questions
52. Sound Bites
53. Production Stories

---

## PART XI - REAL WORLD FINANCIAL CRIMES ARCHITECTURES

### Page 11

54. 50 Million Record Screening Architecture
55. Kafka Design Decisions
56. Lessons Learned

### Page 12

57. Production Pitfalls
58. Consumer Lag
59. Hot Partitions
60. Rebalancing Storms
61. Replay Failures

### Page 13

123. Enterprise Screening Architecture

### Page 14

134. My 50M Client Screening Platform

### Page 15

151. Staff Engineer & VP Discussions

# PAGE 1

# Streaming Fundamentals

---

# 1. Batch Processing

## Definition

Batch processing accumulates data over a period of time and processes it as a bounded dataset.

The data is collected first and processed later.

Examples:

* End of Day Reports
* Monthly Statements
* Rolling 30 Day Credit Card Reports
* Historical Analytics

Characteristics:

* Input and output are files
* Data is bounded
* Processing starts after data collection completes
* Results available after processing finishes

---

## MapReduce Example

MapReduce is a classic batch processing framework.

Flow:

Input File
↓
Map
↓
Shuffle
↓
Reduce
↓
Output File

---

## Reliability Guarantees

One of the strongest properties of batch systems is reliability.

Failed tasks are:

* Automatically retried
* Partial outputs discarded
* Re-executed safely

Result:

Output remains identical to a failure-free execution.

This greatly simplifies application development.

---

# Interview Sound Bite

Batch processing operates on a bounded dataset that is collected over time and processed as a unit. Frameworks such as MapReduce provide strong reliability guarantees through retries and deterministic re-execution.

---

# 2. Stream Processing

## Definition

Stream processing removes the concept of fixed processing windows and processes data continuously as events arrive.

Instead of waiting for an entire file:

Process Event
↓
Immediately

---

## What is a Stream?

A stream is data incrementally made available over time.

Each piece of data is called an Event.

An event is:

* Immutable
* Timestamped
* Represents something that happened at a point in time

Examples:

* Credit card transaction
* User login
* Order creation
* Screening request

---

## Event Encoding

Events may be encoded as:

* Text
* JSON
* Binary

This allows them to be:

* Stored in files
* Stored in databases
* Stored in document stores
* Sent over networks

---

## Batch vs Stream

### Batch

File Written Once
↓
Many Jobs Read File

### Stream

Producer Generates Event
↓
Multiple Consumers Process Event

---

# Interview Sound Bite

Stream processing continuously processes immutable timestamped events as they arrive, enabling near real-time analytics and decision making.

---

# 3. Polling vs Notifications

## Polling Model

Producer writes data to a datastore.

Consumer repeatedly checks for new records.

Flow:

Producer
↓
Database
↑
Consumer Polls

---

## Problems With Polling

For low-latency systems:

* Frequent polling becomes expensive
* Most polls return no new data
* Increased database load
* Increased network traffic

---

## Notification Model

Instead of polling:

Producer
↓
Notify Consumer

Consumer reacts immediately.

Benefits:

* Lower latency
* Lower overhead
* Better scalability

---

## Traditional Databases

Traditional databases generally do not provide efficient notification mechanisms.

Examples:

* Database Triggers
* Change Notifications

These are often limited and not designed for large-scale streaming.

---

## Financial Industry Example

UDP Multicast

Widely used for:

* Stock Market Data
* Market Feeds

Characteristics:

* Extremely low latency
* Application responsible for packet recovery

---

# Interview Sound Bite

Polling introduces unnecessary overhead because consumers repeatedly check for new data. Streaming systems prefer notification-based models where consumers react immediately to new events.

---

# 4. Message Brokers

## Definition

A message broker is a specialized system designed to handle streams of messages between producers and consumers.

Examples:

* Kafka
* RabbitMQ
* ActiveMQ

---

## Why Message Brokers Exist

Traditional databases are optimized for:

* Queries
* Transactions

Message brokers are optimized for:

* Event distribution
* Low latency
* High throughput

---

## Components

Producer
↓
Broker
↓
Consumer

---

## Durability

Some brokers:

Memory Only

Others:

Disk Based

Kafka is Disk Based.

---

## Producer Behavior

Producer waits only for broker acknowledgment.

Producer does NOT wait for consumer processing.

This decouples producers from consumers.

---

## Consumer Handling

Message brokers can tolerate:

* Consumer crashes
* Disconnects
* Slow consumers

Messages remain queued.

---

## Limitations

Most traditional brokers:

* Delete messages after successful delivery
* Not designed for long-term storage
* Large queues reduce throughput

Kafka later solves many of these limitations.

---

# Interview Sound Bite

Message brokers decouple producers from consumers and provide low-latency event distribution while handling failures, retries, and consumer scalability.

---

# 5. Messaging Patterns

## A. Load Balancing Pattern

Each message goes to exactly one consumer.

Example:

Consumer Group

Partition 0 → Consumer A

Partition 1 → Consumer B

Partition 2 → Consumer C

Benefits:

* Horizontal scaling
* Increased throughput

---

## B. Fan Out Pattern

Every consumer receives every message.

Example:

Fraud System
AML System
Analytics System

All consume same event.

Benefits:

* Independent processing
* Event reuse

---

## C. Hybrid Pattern

Multiple Consumer Groups

Within each group:

One consumer processes each message.

Across groups:

All groups receive all messages.

This is Kafka's Consumer Group model.

---

# End of Page 1

Next Page:
Kafka Fundamentals


# PAGE 2

# Kafka Fundamentals

---

# 6. What is Apache Kafka?

## Definition

Apache Kafka is a distributed event streaming platform used for:

* Real-time data pipelines
* Event-driven architectures
* Stream processing
* Event distribution
* Data integration

Kafka combines:

* Database durability
* Message broker semantics
* Distributed system scalability

---

## Why Kafka Was Created

Traditional message brokers struggled with:

* High throughput
* Long-term retention
* Replay
* Horizontal scalability

Kafka solved these problems using an append-only log architecture.

---

## Core Features

### High Throughput

Millions of messages per second.

### Fault Tolerance

Data replicated across brokers.

### Replayability

Consumers can replay old events.

### Ordering

Guaranteed within a partition.

### Horizontal Scalability

Scale using partitions and consumer groups.

---

## Kafka Architecture

```text
Producer
    ↓
Topic
    ↓
Partition
    ↓
Broker
    ↓
Consumer Group
    ↓
Consumer
```

---

### 💡 My Screening Architecture Example

Kafka was used to distribute 50 million screening records across multiple worker services.

```text
Spring Batch
    ↓
Kafka Topic (screening.jobs)
    ↓
10 Partitions
    ↓
Consumer Group
(screening-workers)
    ↓
10 Worker Services
    ↓
Screening Engine
    ↓
Actimize
```

Benefits:

* Parallelism
* Fault Tolerance
* Replayability
* Ordering by Client ID
* 40% reduction in processing time

---

### 🎯 Interview Sound Bite

Kafka is a distributed event streaming platform that combines the durability of a database with the low-latency distribution model of a message broker. It provides ordering, replayability, fault tolerance, and horizontal scalability.

---

# 7. Topics

## Definition

A Topic is a logical category of messages.

Examples:

```text
screening.jobs
screening.results
alerts.generated
payments
transactions
```

---

## Analogy

Think of a topic as a database table.

Difference:

### Database Table

```text
Query Driven
```

### Kafka Topic

```text
Event Driven
```

---

## Example From My Project

```text
Topic:
screening.jobs
```

Contained:

* Client records
* Screening requests
* Batch partition payloads

Produced by:

```text
Spring Batch Producer
```

Consumed by:

```text
screening-workers
```

---

### 🎯 Interview Sound Bite

A topic is a logical stream of related events. Producers write events to a topic and consumers subscribe to that topic.

---

# 8. Brokers

## Definition

A Kafka Broker is a Kafka server.

Responsibilities:

* Store messages
* Serve producers
* Serve consumers
* Replicate data
* Manage partitions

---

## Example Cluster

```text
Broker 1
Broker 2
Broker 3
```

Each broker stores partitions.

---

## Why Multiple Brokers?

### Fault Tolerance

```text
Broker Fails
      ↓
Replica Takes Over
```

### Scalability

```text
More Brokers
      ↓
More Partitions
      ↓
Higher Throughput
```

---

### 🎯 Interview Sound Bite

A broker is a Kafka server responsible for storing topic partitions and serving producer and consumer requests.

---

# 9. Partitions

## Definition

A topic is divided into partitions.

Partitions are the unit of:

* Parallelism
* Ordering
* Scalability

---

## Example

Topic:

```text
screening.jobs
```

Partitions:

```text
P0
P1
P2
P3
P4
```

---

## Why Partitions Exist

Without partitions:

```text
Single Consumer
Single Throughput Limit
```

With partitions:

```text
Multiple Consumers
Parallel Processing
```

---

## Example

50 Million Records

Without Partitions:

```text
One Worker
```

With Partitions:

```text
10 Partitions
10 Workers
```

Each worker handles a subset of records.

---

## Ordering Guarantee

Kafka guarantees ordering:

```text
Within A Partition
```

Kafka does NOT guarantee ordering:

```text
Across Partitions
```

---

## Example

Partition 0

```text
A
B
C
```

Guaranteed:

```text
A → B → C
```

Partition 1

```text
D
E
F
```

Guaranteed:

```text
D → E → F
```

Combined Order:

```text
A B C D E F
```

NOT guaranteed.

---

### ⚠️ Production Pitfall

Many engineers incorrectly assume ordering exists across an entire topic.

It does not.

Ordering exists only within a partition.

---

### 💡 My Screening Architecture Example

We used:

```text
Key = clientId
```

This ensured:

```text
All screening events
for the same client
remain ordered.
```

---

### 🎯 Interview Sound Bite

Partitions are Kafka's unit of parallelism. Ordering is guaranteed within a partition but not across partitions.

---

# 10. Consumer Groups

## Definition

A Consumer Group is a collection of consumers working together to process a topic.

---

## Why Consumer Groups Exist

Without consumer groups:

```text
Every Consumer
Gets Every Message
```

With consumer groups:

```text
Work Is Shared
```

---

## Example

Topic:

```text
screening.jobs
```

Partitions:

```text
P0
P1
P2
P3
```

Consumers:

```text
C1
C2
C3
C4
```

Assignments:

```text
P0 → C1
P1 → C2
P2 → C3
P3 → C4
```

---

## Rule

Within a consumer group:

```text
One Partition
=
One Active Consumer
```

---

## Why?

Prevents duplicate processing.

---

## Fan-Out Example

Group A:

```text
AML
```

Group B:

```text
Analytics
```

Group C:

```text
Fraud
```

Each group receives all messages.

Within each group:

Messages are load balanced.

---

### 💡 My Screening Architecture Example

```text
Topic:
screening.jobs

Partitions:
10

Consumer Group:
screening-workers

Consumers:
10
```

Result:

```text
Maximum Parallelism
```

---

### 🎯 Interview Sound Bite

Consumer groups enable horizontal scaling. Within a consumer group, each partition is assigned to exactly one consumer at a time.

---

# 11. Offsets

## Definition

Offset = Position inside a partition.

Think of it as a bookmark.

---

## Example

Partition 0

```text
Offset 0 → Msg1
Offset 1 → Msg2
Offset 2 → Msg3
Offset 3 → Msg4
```

---

## Why Offsets Matter

After a crash:

Consumer resumes from:

```text
Last Committed Offset
```

instead of:

```text
Beginning Of Topic
```

---

## Important Interview Point

Offsets are NOT stored:

```text
Per Consumer
```

Offsets are NOT stored:

```text
Per Partition
```

Offsets are stored:

```text
Per Consumer Group + Partition
```

✅ This is a favorite senior-level interview question.

---

## Example

Group AML

```text
Offset = 100
```

Group Analytics

```text
Offset = 40
```

Same topic.

Different progress.

---

## Kafka Internal Topic

Kafka stores offsets inside:

```text
__consumer_offsets
```

---

### 💡 My Screening Architecture Example

If a worker failed after processing:

```text
Record 20,000
```

Kafka restarted processing from:

```text
Last Committed Offset
```

instead of replaying the entire partition.

---

### 🎯 Interview Sound Bite

Offsets represent consumer progress and are tracked per consumer group and partition, allowing consumers to resume processing after failures.

---

# Cross References

See Also:

* Page 4 → Consumer Groups & Parallelism
* Page 5 → Reliability & Replication
* Page 6 → Offset Commit Strategies
* Page 8 → Replay & Reprocessing

---

# End of Page 2

## Next Page

### Page 3 – Kafka Architecture

Topics Covered:

* Log-Based Message Brokers
* Append-Only Log Architecture
* Producer Flow
* Consumer Flow
* Partitioning Strategies
* Key-Based Partitioning
* Round Robin Partitioning
* Custom Partitioning
* Replication
* Fault Tolerance
* Why Kafka Achieves Millions of Messages per Second

# PAGE 3

# Kafka Architecture

---

# 12. Log-Based Message Brokers

## Traditional Message Brokers

Traditional message brokers generally:

* Deliver a message
* Wait for acknowledgment
* Delete the message

Examples:

* RabbitMQ
* ActiveMQ

Limitations:

* Limited replay capability
* Large queues reduce performance
* Not optimized for long-term retention

---

## Kafka's Approach

Kafka combines:

```text
Database Durability
+
Message Broker Semantics
+
Distributed Scalability
```

Instead of deleting messages immediately:

Kafka stores messages in an append-only log.

---

## Why This Matters

Benefits:

* Replay
* Durability
* High Throughput
* Fault Tolerance

---

### 🎯 Interview Sound Bite

Kafka is called a log-based message broker because messages are stored in an append-only log rather than being deleted immediately after consumption.

---

# 13. Append-Only Log

## Definition

A log is a sequential, append-only collection of records stored on disk.

Example:

```text
Offset 0 → Msg1
Offset 1 → Msg2
Offset 2 → Msg3
Offset 3 → Msg4
```
When a producer sends message to broker, the broker appends it to a specific partition log
and assigns it a unique sequential Offset to verify it was successfully stored.
New records are always added at the end.

---

## Why Append Only?

Appending is extremely efficient.

Kafka avoids:

* Random writes
* Frequent updates
* Expensive disk seeks

Result:

```text
Very High Throughput
```

---

## Producer Flow

Producer

↓

Append Message

↓

Partition Log

↓

Offset Assigned

---
## Consumer Flow

Consumer

↓

Reads Sequentially

↓

Tracks Offset

↓

Continues Reading

---

## Unix Analogy

Kafka behaves similarly to:

```bash
tail -f logfile
```

Consumer keeps reading new entries as they arrive.

---

### 🎯 Interview Sound Bite

Kafka achieves high throughput because producers append messages sequentially to disk instead of performing random writes.

---

# 14. Producer Flow

## Message Lifecycle

```text
Producer
    ↓
Topic
    ↓
Partition
    ↓
Broker
    ↓
Disk Log
```

---

## Producer Responsibilities

* Serialize events
* Choose partition
* Send records
* Handle retries
* Wait for acknowledgments

---

## Example

```java
ProducerRecord<String, ClientRecord>
```

Key:

```text
clientId
```

Value:

```text
Client Screening Record
```

---

### 💡 My Screening Architecture Example

Producer:

```text
Spring Batch Worker
```

Published:

```text
Client Screening Records
```

Topic:

```text
screening.jobs
```

---

# 15. Consumer Flow

## Message Lifecycle

```text
Partition
    ↓
Consumer Group
    ↓
Consumer
    ↓
Business Logic
    ↓
Offset Commit
```

---

## Consumer Responsibilities

* Read messages
* Process messages
* Commit offsets
* Handle retries

---

## Important

Kafka does NOT know:

```text
Message Successfully Processed
```

Kafka only knows:

```text
Offset Committed
```

Huge interview point.

---

### ⚠️ Production Pitfall

Many engineers assume:

```text
Consumed = Processed
```

Wrong.

Kafka only tracks offsets.

Your application is responsible for successful processing.

---

### 🎯 Interview Sound Bite

Kafka guarantees delivery to consumers, but successful business processing is the application's responsibility.

---

# 16. Partitioning Strategies

## Why Partitioning Exists

Partitioning enables:

* Parallelism
* Scalability
* Load Distribution

Without partitioning:

```text
One Topic
One Consumer
One Throughput Limit
```

With partitioning:

```text
One Topic
Many Partitions
Many Consumers
```

---

# 17. Key-Based Partitioning

## Default Strategy

If a key exists:

Kafka hashes the key.

```text
Partition = hash(key) % partitionCount
```

---

## Example

Key:

```text
customerId = 123
```

Always goes to:

```text
Partition 5
```

---

## Why?

Guarantees ordering.

All messages for:

```text
customerId=123
```

stay in the same partition.

---

### Typical Keys

Financial Systems:

```text
accountId
customerId
clientId
```

Inventory:

```text
productId
```

User Activity:

```text
userId
```

IoT:

```text
deviceId
```

---

### 💡 My Screening Architecture Example

Key:

```text
clientId
```

Reason:

All screening events for a client must remain ordered.

---

### 🎯 Interview Sound Bite

Key-based partitioning ensures that related events are routed to the same partition, preserving ordering for that entity.

---

# 18. Round Robin Partitioning

## Definition

Used when no key is supplied.

Kafka distributes messages evenly.

Example:

```text
Msg1 → P0
Msg2 → P1
Msg3 → P2
Msg4 → P0
Msg5 → P1
```

---

## Benefits

* Balanced load
* Better throughput

---

## Drawback

No ordering guarantees.

---

### Use Cases

* Metrics
* Logging
* Analytics
* Independent Events

---

### 🎯 Interview Sound Bite

Round-robin partitioning maximizes distribution but sacrifices ordering guarantees.

---

# 19. Custom Partitioning

## Definition

Applications can implement custom partitioning logic.

Example:

```java
public class ScreeningPartitioner
```

---

## Use Cases

Region-Based Routing

```text
US → Partition 0
EU → Partition 1
APAC → Partition 2
```

---

Compliance Routing

```text
Retail → Partition 0
Wealth → Partition 1
Institutional → Partition 2
```

---

### Benefits

* Better locality
* Better workload isolation
* Business-specific routing

---

### 🎯 Interview Sound Bite

Custom partitioners allow organizations to align Kafka partitioning with business or operational boundaries.

---

# 20. Replication

## Why Replication Exists

Without replication:

```text
Broker Failure
=
Data Loss
```

---

## Replication Factor

Example:

```text
Replication Factor = 3
```

Partition 0 exists on:

```text
Broker 1 (Leader)
Broker 2 (Follower)
Broker 3 (Follower)
```

---

## Leader

Handles:

* Producer Writes
* Consumer Reads

---

## Followers

Replicate data from leader.

---

## Failure Example

```text
Leader Crashes
      ↓
Follower Promoted
      ↓
Service Continues
```

---

### 🎯 Interview Sound Bite

Replication protects Kafka from broker failures by maintaining multiple copies of partition data across brokers.

---

# 21. Fault Tolerance

## Definition

Kafka remains operational despite failures.

Examples:

### Broker Failure

```text
Leader Dies
    ↓
Follower Becomes Leader
```

---

### Consumer Failure

```text
Consumer Dies
    ↓
Rebalance
    ↓
Partition Assigned To New Consumer
```

---

### Network Failure

```text
Retry
Replication
Leader Election
```

---

### 💡 My Screening Architecture Example

If a worker service crashed:

```text
screening-worker-3
```

Kafka automatically reassigned partitions to remaining workers.

Processing resumed from committed offsets.

No manual intervention required.

---

# Why Kafka Can Scale To Millions Of Messages Per Second

Kafka combines:

### Sequential Disk Writes

```text
Append Only Logs
```

### Partitioning

```text
Parallel Processing
```

### Replication

```text
Fault Tolerance
```

### Consumer Groups

```text
Horizontal Scaling
```

### Pull-Based Consumption

```text
Efficient Reads
```

---

# Quick Revision Sheet

Kafka Architecture

```text
Producer
    ↓
Topic
    ↓
Partition
    ↓
Broker
    ↓
Consumer Group
    ↓
Consumer
```

Partitioning Options:

```text
Key Based
Round Robin
Custom
```

Replication:

```text
Leader
Follower
Leader Election
```

Scalability:

```text
Partitioning
Consumer Groups
```

---

# Cross References

See Also:

* Page 4 → Parallelism & Ordering
* Page 5 → Reliability & Acknowledgments
* Page 6 → Offsets & Idempotency
* Page 8 → Replay

---

# End of Page 3

## Next Page

### Page 4 – Partitions, Consumer Groups & Parallelism

Topics Covered:

* Maximum Parallelism
* Consumers vs Partitions
* Load Balancing
* Fan-Out
* Ordering Tradeoffs
* Global Ordering Problem
* Real-World Ordering Examples
* Consumer Rebalancing

# PAGE 4

# Partitions, Consumer Groups & Parallelism

---

# 22. Why Partitions Exist

## The Core Problem

Suppose we have:

```text
50 Million Client Records
```

And only:

```text
1 Consumer
```

Then:

```text
Throughput
=
Limited By One Machine
```

No matter how powerful Kafka is, a single consumer becomes the bottleneck.

---

## Kafka Solution

Split the topic into partitions.

Example:

```text
screening.jobs

P0
P1
P2
P3
P4
P5
P6
P7
P8
P9
```

Now multiple consumers can process records simultaneously.

---

## Benefits

### Parallel Processing

```text
1 Topic
10 Partitions
10 Consumers
```

---

### Scalability

Add more partitions.

Add more consumers.

Increase throughput.

---

### Fault Isolation

A slow consumer only impacts its assigned partitions.

---

### 🎯 Interview Sound Bite

Partitions are Kafka's unit of scalability and parallelism.

---

# 23. Maximum Parallelism

## Most Important Kafka Rule

### Maximum Parallelism = Number Of Partitions

If a topic has:

```text
10 Partitions
```

Maximum parallelism is:

```text
10
```

Not:

```text
20
50
100
```

---

## Why?

Within a consumer group:

```text
One Partition
=
One Active Consumer
```

---

## Example

Topic:

```text
screening.jobs
```

Partitions:

```text
10
```

Consumers:

```text
10
```

Result:

```text
Perfect Utilization
```

---

### 🎯 Interview Favorite

Question:

```text
Can 20 consumers process a topic with 10 partitions?
```

Answer:

```text
No.

Only 10 consumers can be active.

The other 10 remain idle.
```

---

# 24. Consumer Count vs Partition Count

## Scenario 1

Consumers = Partitions

```text
10 Partitions
10 Consumers
```

Assignment:

```text
P0 -> C1
P1 -> C2
P2 -> C3
...
P9 -> C10
```

Result:

```text
Maximum Parallelism
Balanced Load
```

---

## Scenario 2

Consumers < Partitions

```text
10 Partitions
5 Consumers
```

Assignment:

```text
C1 -> P0,P1
C2 -> P2,P3
C3 -> P4,P5
C4 -> P6,P7
C5 -> P8,P9
```

Result:

```text
Consumers Handle Multiple Partitions
```

Works fine but throughput may be limited.

---

## Scenario 3

Consumers > Partitions

```text
10 Partitions
15 Consumers
```

Result:

```text
10 Active Consumers
5 Idle Consumers
```

No performance gain.

---

### 🎯 Interview Sound Bite

To maximize Kafka throughput, consumer count should generally match partition count.

---

# 25. Load Balancing

## Definition

Kafka distributes partitions among consumers in a consumer group.

---

## Example

Before Rebalance

```text
P0 -> C1
P1 -> C2
P2 -> C3
P3 -> C4
```

---

Consumer C2 Crashes

Kafka Rebalances

---

After Rebalance

```text
P0 -> C1
P1 -> C3
P2 -> C4
P3 -> C1
```

---

## Goal

Distribute work evenly.

---

### 💡 My Screening Architecture Example

```text
screening-workers
```

If one worker crashes:

Kafka automatically redistributes partitions.

Processing resumes from committed offsets.

No manual intervention required.

---

# 26. Fan-Out Pattern

## Definition

Every consumer group receives every message.

---

## Example

Topic:

```text
client.events
```

Consumer Groups:

```text
AML
Fraud
Analytics
```

Each group receives:

```text
All Messages
```

---

## Why Useful?

One event can power multiple systems.

Example:

```text
Client Onboarded
```

Triggers:

```text
AML Screening
Fraud Analysis
Reporting
Audit
```

All independently.

---

### 🎯 Interview Sound Bite

Fan-out allows multiple independent consumer groups to process the same event stream.

---

# 27. Combined Pattern

## Real World Architecture

Kafka often uses:

```text
Fan-Out
+
Load Balancing
```

---

Example:

```text
Topic
```

Consumer Groups:

```text
AML
Fraud
Analytics
```

Within AML:

```text
AML Worker 1
AML Worker 2
AML Worker 3
```

Messages are:

```text
Broadcast Across Groups
Load Balanced Within Group
```

---

### 🎯 Senior Interview Point

Consumer Groups provide horizontal scaling.

Multiple Consumer Groups provide fan-out.

Kafka supports both simultaneously.

---

# 28. Ordering Guarantees

## Important Rule

Kafka guarantees ordering:

```text
Within A Partition
```

Kafka does NOT guarantee ordering:

```text
Across Partitions
```

---

## Example

Partition 0

```text
A
B
C
```

Guaranteed:

```text
A -> B -> C
```

---

Partition 1

```text
D
E
F
```

Guaranteed:

```text
D -> E -> F
```

---

Combined:

```text
A B C D E F
```

No guarantee.

---

### Why?

Partitions operate independently.

Each partition has:

```text
Independent Producer Writes
Independent Consumers
Independent Replication
```

---

# 29. Real World Ordering Examples

## Financial Transactions

Example:

```text
Debit $100
Credit $50
Debit $25
```

Wrong ordering causes:

```text
Incorrect Balance
```

Key:

```text
accountId
```

---

## Inventory Systems

Events:

```text
Add Stock
Reserve Stock
Cancel Reservation
```

Wrong ordering causes:

```text
Overselling
```

Key:

```text
productId
```

---

## User Timelines

Events:

```text
Post
Like
Comment
```

Key:

```text
userId
```

---

## IoT Sensors

Events:

```text
Temperature Readings
```

Key:

```text
deviceId
```

---

### 💡 My Screening Architecture Example

Key:

```text
clientId
```

Reason:

```text
Client Screening Events
Must Remain Ordered
```

---

# 30. Cases Where Ordering Is Not Required

Examples:

```text
Daily Metrics
Analytics
Monitoring
Search Indexing
Log Aggregation
```

Ordering adds cost.

Avoid it when unnecessary.

---

### 🎯 Interview Sound Bite

Ordering should only be enforced when business correctness depends on event sequence.

---

# 31. Global Ordering Problem

## What If We Need Ordering Across All Messages?

Kafka would need:

```text
Global Sequence Number
```

or

```text
Single Partition
```

---

## Example

Partition 0

```text
T1
T4
T7
```

Partition 1

```text
T2
T3
T6
```

Partition 2

```text
T5
T8
```

Required:

```text
T1 T2 T3 T4 T5 T6 T7 T8
```

---

## Consumer Challenge

Consumer must:

```text
Merge Streams
Buffer Messages
Wait For Missing Events
```

---

## Example

Partition 0 delivers:

```text
T1
T4
```

Partition 1 is slow.

Consumer cannot emit:

```text
T4
```

Until:

```text
T2
T3
```

arrive.

---

## Consequences

### Higher Latency

Messages wait.

---

### More Memory

Buffers grow.

---

### Reduced Throughput

Consumers idle.

---

### Loss Of Parallelism

Kafka's biggest advantage disappears.

---

### 🎯 Senior Interview Question

Why doesn't Kafka support global ordering?

Answer:

Because enforcing global ordering would require coordination, buffering, and synchronization across partitions, eliminating Kafka's scalability advantages.
A way to do that would be just have a single partition but thats precisely what we said , we wouldnt be able to take advantage of kafka's parallelism
So clearly there is a trade-off between strict ordering and scalability. Kafka achieves the best balance by providing ordering guarantees within a partition instead of global ordering

---

# Best Practice

Use:

```text
Partition-Level Ordering
```

With:

```text
accountId
clientId
productId
deviceId
```

Avoid:

```text
Global Ordering
```

Unless absolutely necessary.

---

# Quick Revision Sheet

```text
Maximum Parallelism
=
Number Of Partitions

One Partition
=
One Active Consumer

Ordering
=
Within Partition Only

Fan-Out
=
Multiple Consumer Groups

Load Balancing
=
Within Consumer Group

Best Practice
=
Key Based Partitioning
```

---

# Cross References

See Also:

* Page 3 → Partitioning Strategies
* Page 5 → Reliability & Replication
* Page 6 → Offsets & Idempotency
* Page 8 → Replay

---

# End Of Page 4

## Next Page

### Page 5 – Reliability, Replication & Acknowledgments

Topics Covered:

* Producer Acks
* Consumer Acks
* Replication Factor
* Min In Sync Replicas
* acks=0
* acks=1
* acks=all
* Leader Failures
* Data Loss Scenarios
* Production Best Practices

# PAGE 5

# Reliability, Replication & Acknowledgments

---

# 32. Reliability In Distributed Systems

## Core Problem

In a distributed system, failures are inevitable.

Examples:

* Broker crashes
* Consumer crashes
* Producer crashes
* Network failures
* Leader failures
* Slow consumers

Kafka's reliability model is built around:

```text
Replication
Acknowledgments
Offset Management
Retries
Idempotency
```

---

## Reliability Goals

Kafka attempts to provide:

### Durability

Data should survive broker failures.

---

### Availability

System should continue operating after failures.

---

### Recoverability

Consumers should resume from where they left off.

---

### Scalability

Reliability should not destroy throughput.

---

### 🎯 Interview Sound Bite

Kafka achieves reliability through replication, acknowledgments, retries, offset management, and idempotent producers.

---

# 33. Replication

## Why Replication Exists

Without replication:

```text
Broker Failure
=
Data Loss
```

---

## Replication Factor

Example:

```text
Replication Factor = 3
```

Partition:

```text
P0
```

Stored On:

```text
Broker 1 (Leader)
Broker 2 (Follower)
Broker 3 (Follower)
```

---

## Leader

Responsible For:

* Producer writes
* Consumer reads
* Replica coordination

---

## Followers

Responsible For:

* Replicating leader data
* Taking over after leader failure

---

## Example

```text
Leader (Broker 1)
      ↓
Follower (Broker 2)
      ↓
Follower (Broker 3)
```

---

### 🎯 Interview Sound Bite

Replication protects Kafka from broker failures by maintaining multiple copies of partition data across brokers.

---

# 34. In Sync Replicas (ISR)

## Definition

ISR = In-Sync Replicas

Replicas that are caught up with the leader.

Example:

```text
Leader (B1)
Follower (B2)
Follower (B3)
```

All caught up:

```text
ISR = {B1, B2, B3}
```

---

## Why ISR Matters

Kafka only trusts replicas that are fully synchronized.

---

### 🎯 Interview Sound Bite

ISR represents the set of replicas that are fully caught up with the leader and eligible for leadership election.

---

# 35. min.insync.replicas

## Definition

Minimum number of replicas that must acknowledge a write.

Example:

```text
Replication Factor = 3
min.insync.replicas = 2
```

Meaning:

```text
Leader
+
1 Follower
```

Must acknowledge.

---

## Why Important?

Prevents:

```text
Leader ACK
Leader Crash
Data Loss
```

Scenario.

---

## Typical Configuration

```bash
--replication-factor 3
--config min.insync.replicas=2
```

---

### Production Sound Bite

We typically use replication factor 3 with min.insync.replicas=2 so the platform can tolerate one broker failure while maintaining durability guarantees.

---

# 36. Producer Acknowledgments

## Definition

Acknowledgments determine when Kafka confirms a write.

Producer:

```text
Send Message
      ↓
Wait For ACK
      ↓
Continue
```

Configuration:

```properties
acks=0
acks=1
acks=all
```

---

# 37. acks=0

## Fire And Forget

Producer:

```text
Send Message
```

Does NOT wait.

---

## Flow

```text
Producer
      ↓
Message Sent
      ↓
Continue
```

---

## Benefits

* Fastest
* Lowest latency

---

## Risks

Kafka may never receive the message.

---

## Failure Scenario

```text
Producer Sends Msg57
Network Failure
Broker Never Receives Message
Producer Thinks Success
```

Result:

```text
Data Loss
```

---

### ⚠️ Production Pitfall

Never use:

```text
acks=0
```

For compliance workloads.

---

### 🎯 Interview Sound Bite

acks=0 provides maximum throughput but offers no durability guarantees because the producer never waits for broker confirmation.

---

# 38. acks=1

## Leader Acknowledgment

Producer waits for leader only.

---

## Flow

```text
Producer
      ↓
Leader Writes Message
      ↓
Leader Sends ACK
      ↓
Producer Continues
```

---

## Benefits

* Faster than acks=all
* Common default

---

## Failure Scenario

Timeline:

```text
T1 Producer Sends Message
T2 Leader Writes Message
T3 Leader Sends ACK
T4 Followers Have NOT Replicated Yet
T5 Leader Crashes
```

New Leader:

```text
Follower Without Message
```

Result:

```text
Data Loss
```

---

### Important

The producer already received ACK.

But the message is gone.

Why acks=1 can still lose data
- With acks=1 the leader sends an ACK to the producer as soon as it has written the record to its local log — it does not wait for followers to replicate that write.
- If the leader crashes (software crash, hardware failure, or disk loss) before followers have appended the record, the new leader may not have that record and the write is lost — even though the producer already received an ACK.

Failure timeline (example)
- T1: Producer sends message
- T2: Leader writes message to its log and sends ACK to the producer
- T3: Followers have not finished replication
- T4: Leader crashes before replication completes
- Result: The message is missing from the new leader → data loss

Different perspectives
- Cluster: The message only existed on a single physical disk that is now unavailable.
- Producer: It received an ACK for a write the cluster later “forgot.”
- Consumer: No consumer could have read the message anyway, because Kafka’s high watermark prevents reading uncommitted (not-yet-replicated) data.

Takeaway / Best practice
- Acknowledging a write before it is replicated to a majority (or to the configured in-sync replicas) is unsafe for systems that require zero data loss (RPO = 0).
- For stronger durability use acks=all combined with an appropriate min.insync.replicas (e.g., replication.factor=3 and min.insync.replicas=2). In that configuration, the leader will only ACK after the required followers have appended the record, preventing the scenario above.
---

### 🎯 Interview Favorite

Question:

Why can data still be lost with acks=1?

Answer:

Because the leader may acknowledge before followers replicate. If the leader crashes before replication completes, the acknowledged message can be lost.


---

# 39. acks=all

## Strongest Durability

Producer waits for:

```text
Leader
+
All Required ISR Replicas
```

---

## Flow

```text
Producer
      ↓
Leader Write
      ↓
Followers Replicate
      ↓
Followers ACK Leader
      ↓
Leader ACK Producer
```

---

## Benefits

* Strongest durability
* Compliance friendly

---

## Example

```text
Replication Factor = 3
min.insync.replicas = 2
acks=all
```

Kafka waits for:

```text
Leader
+
One Follower
```

Before acknowledgment.

---

### 🎯 Interview Sound Bite

acks=all provides the strongest durability because Kafka waits for all required in-sync replicas before acknowledging the write.

---

# 40. Consumer Acknowledgments

## Important Distinction

Consumers do NOT send acknowledgments like producers.

Instead:

Consumers commit offsets.

---

## Producer Side

```text
ACK
=
Message Written
```

---

## Consumer Side

```text
Offset Commit
=
Message Processed
```

---

### Interview Favorite

Question:

How does a consumer acknowledge processing?

Answer:

By committing offsets.

---

# 41. Auto Commit

## Default Mode

```properties
enable.auto.commit=true
```

Default:

```properties
auto.commit.interval.ms=5000
```

---

## Problem

Auto-commit is:

```text
Time Based
```

NOT:

```text
Processing Based
```
---
The consumer automatically sends the commit request to the broker based on a time interval rather than your code's processing success (logic).

## Failure Scenario

Timeline:

```text
T0 Read Msg57
T1 Auto Commit Offset 57
T2 Processing Still Running
T3 Application Crashes
T4 Restart
T5 Starts From Msg58
```

Result:

```text
Data Loss
```

Msg57 never completed.

---

### ⚠️ Production Pitfall

Offset committed before business processing completed.

---

### 🎯 Interview Sound Bite

Auto commit can cause data loss because offsets may be committed before processing completes.

---

# 42. Manual Commit

## Recommended Approach

Disable:

```properties
enable.auto.commit=false
```

Commit only after successful processing.

---

## Flow

```text
Read Message
      ↓
Process Message
      ↓
Save Business Result
      ↓
Commit Offset
```

---

## Benefits

Avoids data loss.

---

### Spring Kafka Example

```java
@KafkaListener(...)
public void consume(ClientRecord record,
                    Acknowledgment ack) {

    process(record);

    ack.acknowledge();
}
```

---

### 🎯 Interview Sound Bite

Manual commit ensures a record is marked processed only after business processing succeeds.

---

# 43. At-Least-Once Delivery

## What Happens?

Scenario:

```text
Msg57 Processed
Alert Saved
Application Crashes
Offset Not Committed
```

Restart:

```text
Kafka Sends Msg57 Again
```

---

## Result

Duplicate Processing.

---

## Why?

Kafka prefers:

```text
Duplicate Processing
```

over

```text
Data Loss
```

---

### Interview Sound Bite

Kafka provides at-least-once delivery because messages may be retried if offsets were not committed successfully.

---

# 44. Idempotent Consumers

## Why Needed?

Manual commit can cause retries.

Retries can create:

```text
Duplicate Alerts
Duplicate Emails
Duplicate Database Writes
```

---

## Solution

Consumers must be idempotent.

---

## Example

Use:

```text
Transaction ID
Alert ID
Client ID + Screening Date
```

As unique identifiers.

---

## Goal

Processing the same message twice should produce the same outcome.

---

### Production Sound Bite

We design consumers to be idempotent because Kafka provides at-least-once delivery semantics.

---

# Reliability Best Practices

## Producer Side

```text
acks=all
enable.idempotence=true
replication.factor=3
min.insync.replicas=2
```

---

## Consumer Side

```text
Manual Offset Commit
Idempotent Consumers
DLQ
Retries
```

---

### 🎯 Senior Engineer Answer

We use acks=all with idempotent producers to ensure durability and ordering. On the consumer side, we disable auto-commit and commit offsets only after successful processing. Since Kafka provides at-least-once delivery, consumers are designed to be idempotent and failed messages are routed to a DLQ after retries.

---

# Quick Revision Sheet

```text
Replication Factor = Number Of Copies

ISR = In Sync Replicas

acks=0
=
Fastest
Can Lose Data

acks=1
=
Leader Only
Can Lose Data

acks=all
=
Most Reliable

Auto Commit
=
Possible Data Loss

Manual Commit
=
Recommended

At Least Once
=
Duplicates Possible

Idempotent Consumer
=
Required
```

---

# Cross References

See Also:

* Page 6 → Idempotency
* Page 7 → Dead Letter Queues
* Page 8 → Replay
* Page 9 → Spring Kafka

---

# End Of Page 5

## Next Page

### Page 6 – Idempotency & Producer Reliability

Topics Covered:

* enable.idempotence=true
* Producer IDs
* Sequence Numbers
* Retry Scenarios
* Ordering Protection
* Duplicate Prevention
* acks + Idempotency Matrix
* Failure Analysis

# PAGE 6

# Idempotency & Producer Reliability

---

# 45. Why Idempotency Exists

## The Problem

Distributed systems fail.

Failures include:

* Broker crashes
* Producer crashes
* Network failures
* Timeouts
* Retries

---

## Example

Producer sends:

```text
Msg57
```

Timeline:

```text
T1 Producer Sends Message
T2 Broker Receives Message
T3 Broker Writes Message
T4 ACK Lost
T5 Producer Times Out
T6 Producer Retries
```

Result:

```text
Msg57
Msg57
```

Duplicate message.

---

## Why Is This Dangerous?

Examples:

```text
Transfer $100
Send Email
Generate Alert
Create Case
```

Running twice can cause:

```text
Duplicate Transactions
Duplicate Alerts
Duplicate Cases
```

---

### 🎯 Interview Sound Bite

Retries improve reliability but can introduce duplicate messages. Idempotency solves this problem.

---

# 46. What Is Idempotency?

## Definition

An operation is idempotent if:

```text
Executing It Multiple Times
Produces Same Result
```

---

## Example

Not Idempotent

```java
balance += 100;
```

Run twice:

```text
100
200
```

Different result.

---

## Idempotent

```java
balance = 100;
```

Run twice:

```text
100
100
```

Same result.

---

## Kafka Goal

If producer retries:

```text
Msg57
```

Kafka should store:

```text
One Copy Only
```

---

### 🎯 Interview Sound Bite

Idempotency guarantees retries do not create duplicate records.

---

# 47. Producer Idempotency

## Enable Idempotency

```properties
enable.idempotence=true
```

---

## What Kafka Does

Kafka assigns:

```text
Producer ID (PID)
```

to every producer.

---
This means multiple concurrent producers can write to the same partition
## Example

```text
Producer A
PID = 101
```

---

Each message receives:

```text
Sequence Number . A sequence number is assigned per partition. The producer client library assigns the sequence number, not the broker.
```

---

Example:

```text
Msg1 -> Seq 0
Msg2 -> Seq 1
Msg3 -> Seq 2
Msg4 -> Seq 3
```

---

### 🎯 Interview Sound Bite

Kafka uses Producer IDs and Sequence Numbers to detect duplicate retries.

---

# 48. Sequence Numbers

## Example

Producer:

```text
PID = 101
```

Messages:

```text
Seq 0
Seq 1
Seq 2
```

---

Broker remembers:

```text
Highest Sequence Seen
```

---

### Retry Scenario

Broker already has:

```text
PID 101
Seq 2
```

Producer retries:

```text
PID 101
Seq 2
```

Broker detects duplicate.

---

Result:

```text
Ignored
```

---

### Important

Duplicate is removed before reaching consumers.

---

### 🎯 Interview Favorite

Question:

How does Kafka detect duplicate producer retries?

Answer:

Kafka tracks Producer IDs and Sequence Numbers. If a retry contains a previously processed sequence number, Kafka discards it.

---
# 49. Idempotency And Ordering

## The Intuitive (But Incomplete) Mental Model

If a distributed network operated strictly under a **Synchronous Stop-and-Wait** model:

```text
Send Packet 1
↓
Wait For ACK
↓
Send Packet 2
↓
Wait For ACK
↓
Send Packet 3
```

ordering could never break.

If Packet 2 failed, the producer would simply retry Packet 2 before Packet 3 was ever created.

However, high-performance distributed systems intentionally break this simple model to maximize throughput.

---

## Sequential vs Pipelined Producer Model

| Characteristic               | Strict Sequential Model                   | Pipelined Model                           |
| ---------------------------- | ----------------------------------------- | ----------------------------------------- |
| Configuration                | `max.in.flight.requests.per.connection=1` | `max.in.flight.requests.per.connection=5` |
| Producer Behavior            | Send one batch and wait for ACK           | Send multiple batches without waiting     |
| Throughput                   | Lower                                     | Higher                                    |
| Network Utilization          | Poor                                      | Excellent                                 |
| Ordering Risk During Retries | None                                      | Possible                                  |
| Typical Usage                | Rare                                      | Common Production Configuration           |

---

## 1. The Strict Sequential Model (Synchronous)

**Configuration**

```text
max.in.flight.requests.per.connection = 1
```

### How It Works

1. Producer sends Packet 1.
2. Producer completely halts and waits.
3. Broker writes Packet 1 and sends an ACK.
4. Producer receives the ACK and only then sends Packet 2.

### Why Ordering Never Breaks

There is only ever **one packet** on the wire.

Example:

```text
Packet1
Packet2
Packet3
```

If Packet2 fails:

```text
Packet1 ACK
↓
Packet2 Failed
↓
Retry Packet2
↓
Packet2 ACK
↓
Send Packet3
```

Packet3 cannot leapfrog Packet2 because Packet3 was never transmitted.

Ordering remains intact.

### Drawback

```text
Poor Throughput

Poor Network Utilization

Higher Latency
```

The producer spends most of its time waiting.

---

## 2. The Pipelined Model (Asynchronous Reality)

**Configuration**

```text
max.in.flight.requests.per.connection = 5
```

### How It Works

To avoid wasting time waiting for network round trips, the producer continuously streams requests.

```text
Packet1
Packet2
Packet3
Packet4
Packet5
```

are transmitted immediately.

All packets are moving through the network simultaneously.

### Why?

Instead of:

```text
Send
Wait

Send
Wait

Send
Wait
```

Kafka performs:

```text
Send
Send
Send
Send
Send
```

This dramatically increases throughput.

---

## ⚠️ The Great Kafka Ordering Myth

The famous statement:

```text
Kafka Guarantees Ordering Within A Partition
```

comes with an important caveat.

Without setting idempotence to true (or strictly limiting in-flight requests to 1), Kafka **does NOT** guarantee ordering if network errors or retries occur. 
The baseline ordering guarantee *only* applies to flawless, uninterrupted network flights.

---

## Scenario: Order Scrambling Without Idempotency

### Intended Sequence

```text
Msg1
↓
Msg2
↓
Msg3
```

### Network Incident

```text
Msg2 Delayed
```

### Broker Receives

```text
Msg1
↓
Msg3
```

### Producer Retries Msg2

Final Log:

```text
Offset 0 → Msg1

Offset 1 → Msg3

Offset 2 → Msg2
```

❌ Ordering Broken

---

## 🛡️ Hidden Benefit: Idempotency Preserves Ordering

Most engineers think idempotency only prevents duplicates.

In reality, it also protects ordering.

When:

```text
enable.idempotence=true
```

Kafka assigns:

```text
Producer ID (PID)

Sequence Numbers
```

to every message.

---

## How Sequence Numbers Repair The Pipeline

1. Producer sends Msg1 (Seq 0), Msg2 (Seq 1), and Msg3 (Seq 2).
2. Msg2 is delayed due to a network issue.
3. Msg3 reaches the broker before Msg2.
4. Broker expects Seq 1 but receives Seq 2.
5. Broker rejects Msg3 because a sequence gap exists.
6. Producer resends Msg2, then Msg3.

Final Result:

```text
Offset 0 → Msg1 (Seq 0)

Offset 1 → Msg2 (Seq 1)

Offset 2 → Msg3 (Seq 2)
```

✅ Ordering Preserved

---

## Why Kafka Needs Sequence Numbers

Kafka idempotency helps detect:

```text
Duplicate Messages

Out-Of-Order Messages

Missing Sequence Numbers (Sequence Gaps)
```

A sequence gap may indicate:

```text
Message Delayed

Message Retry In Progress

Network Issue

Broker Issue

Message Loss
```

---

## Interview Sound Bite

> Idempotency is not just duplicate prevention. Kafka uses Producer IDs and Sequence Numbers to detect duplicate messages, identify out-of-order records, and detect sequence gaps during retry scenarios.

---

# 50. Idempotency Requirements

When idempotency is enabled:

```text
enable.idempotence=true
```

Kafka automatically enforces:

```text
acks=all

retries=Integer.MAX_VALUE

max.in.flight.requests.per.connection<=5
```

---

## Why These Settings?

To preserve:

```text
Ordering

Durability

Retry Safety
```

---

### acks=all

Provides:

```text
Maximum Durability
```

The producer receives acknowledgement only after all ISR replicas have persisted the record.

---

### retries=Integer.MAX_VALUE

Provides:

```text
Reliable Delivery
```

Kafka can safely retry transient failures.

---

### max.in.flight.requests.per.connection<=5

Provides:

```text
High Throughput

Ordering Protection
```

Kafka can pipeline requests while remaining compatible with broker-side sequence validation.

---

## The Key Realization

Modern producers do not operate as:

```text
Send
Wait

Send
Wait

Send
Wait
```

They operate as:

```text
Send
Send
Send
Send
Send
```

using request pipelining.

This improves:

```text
Throughput

Network Utilization

Latency
```

but introduces ordering risks during retry scenarios.

---

## How Kafka Fixes The Problem

Kafka combines:

```text
Producer ID (PID)

Sequence Numbers

Broker Validation
```

to ensure retries do not introduce duplicates or ordering violations.

---

### 🙋‍♂️ Interview Favorite Question

**Question:** Can idempotency work with `acks=0`?

**Answer:** **No.** 

* **Fire-and-Forget:** `acks=0` means the producer sends data blindly and never waits for a reply.
* **No Feedback Loop:** Idempotence requires a two-way conversation. Without an ACK path, the broker cannot send back errors like `OutOfOrderSequenceException` [DEV Community Kafka Producer Acks Guide].
* **The Result:** The producer can never detect duplicates or gaps, completely disabling the idempotency engine [DEV Community Kafka Producer Acks Guide].

# 51. Failure Scenario #1

## Without Idempotency

Timeline:

```text
Producer Sends Msg57
Broker Writes Msg57
ACK Lost
Producer Retries
Broker Writes Msg57 Again
```

Result:

```text
Duplicate Message
```

---

# Failure Scenario #2

## With Idempotency

Timeline:

```text
Producer Sends Msg57
Broker Writes Msg57
ACK Lost
Producer Retries
Broker Detects Duplicate Sequence Number
```

Result:

```text
One Copy Stored
```

---

### 🎯 Interview Sound Bite

Idempotency eliminates duplicates caused by producer retries.

---

# 52. Exactly Once Semantics (EOS)

## Interview Trap

Many candidates say:

```text
Kafka Guarantees Exactly Once
```

This is incomplete.

---

## Reality

Kafka guarantees:

```text
Exactly Once Producer Writes
```

when:

```properties
enable.idempotence=true
```

---

Consumer processing still requires:

```text
Idempotent Consumers
Transactions
```

---

### Better Answer

Kafka can provide exactly-once semantics when idempotent producers, transactions, and properly designed consumers are used together.

---

# 53. Idempotent Consumers

## Important

Producer idempotency protects:

```text
Kafka Log
```

It does NOT protect:

```text
Database
Email
External APIs
```

---

Example:

Consumer receives:

```text
Msg57
```

Processes:

```text
Generate Alert
```

Application crashes before offset commit.

---

Kafka retries:

```text
Msg57
```

Again.

---

Without protection:

```text
Alert Generated Twice
```

---

### Solution

Use unique business keys.

Examples:

```text
alertId
transactionId
clientId + date
```

---

### 🎯 Interview Sound Bite

Producer idempotency protects Kafka. Consumer idempotency protects downstream systems.

---

# 54. Idempotency Matrix

| Configuration          | Duplicate Risk | Durability |
| ---------------------- | -------------- | ---------- |
| acks=0                 | Very High      | Very Low   |
| acks=1                 | Medium         | Medium     |
| acks=all               | Low            | High       |
| acks=all + idempotence | Lowest         | Highest    |

---

## Production Recommendation

```properties
acks=all
enable.idempotence=true
retries=Integer.MAX_VALUE
```

---

### Production Sound Bite

For compliance workloads we use acks=all, replication factor 3, min.insync.replicas=2, and idempotent producers to maximize durability and eliminate duplicate writes.

---

# My Screening Architecture Example

Producer:

```text
Spring Batch Screening Producer
```

Topic:

```text
screening.jobs
```

Requirements:

```text
No Duplicate Screening Requests
No Lost Records
Ordered By Client
```

Configuration:

```properties
acks=all
enable.idempotence=true
replication.factor=3
min.insync.replicas=2
```

Benefits:

```text
Durable
Replayable
Retry Safe
Fault Tolerant
```

---

# Quick Revision Sheet

```text
Idempotency
=
Safe Retries

Producer ID
=
PID

Sequence Number
=
Message Ordering Identifier

Duplicate Retry
=
Discarded

enable.idempotence=true
=
Recommended

Producer Idempotency
=
Protects Kafka

Consumer Idempotency
=
Protects Database

Best Practice
=
acks=all + idempotence=true
```

---

# Cross References

See Also:

* Page 5 → Reliability & Acknowledgments
* Page 7 → Dead Letter Queues
* Page 8 → Replay
* Page 9 → Spring Kafka Transactions

---

# End Of Page 6

## Next Page

### Page 7 – Dead Letter Queues (DLQ)

Topics Covered:

* Poison Pill Messages
* Retry Strategies
* DLQ Architecture
* Spring Kafka DLQ
* Error Handlers
* Production Patterns
* Interview Questions

# PAGE 7

# Dead Letter Queues (DLQ)

---

# 55. Why Dead Letter Queues Exist

## The Problem

Not all failures are transient.

Some messages will never succeed.

Examples:

```text id="jlwm0d"
Invalid JSON
Missing Required Fields
Corrupt Payload
Unknown Schema Version
Bad Reference Data
Poison Pill Messages
```

---

## Without DLQ

Consumer:

```text id="d07f5o"
Reads Message
      ↓
Fails
      ↓
Retries
      ↓
Fails
      ↓
Retries Forever
```

---

Result:

```text id="4hqqg6"
Consumer Stuck
Partition Blocked
Throughput Drops
Operational Impact
```

---

## Kafka Principle

Bad messages should not stop healthy message processing.

---

### 🎯 Interview Sound Bite

Dead Letter Queues isolate permanently failing messages so healthy traffic can continue processing.

---

# 56. What Is A Dead Letter Queue?

## Definition

A Dead Letter Queue (DLQ) is a separate Kafka topic used to store messages that cannot be processed successfully.

---

## Example

Main Topic:

```text id="4r7sqh"
screening.jobs
```

DLQ Topic:

```text id="v52zpp"
screening.jobs.dlq
```

---

## Flow

```text id="h7m5d0"
Producer
      ↓
screening.jobs
      ↓
Consumer
      ↓
Failure
      ↓
Retry
      ↓
Retry
      ↓
Retry
      ↓
screening.jobs.dlq
```

---

### Benefits

* Main pipeline continues
* Failed records preserved
* Easier investigation
* Easier replay

---

### 🎯 Interview Sound Bite

A DLQ preserves failed messages while preventing them from blocking the main processing pipeline.

---

# 57. Poison Pill Messages

## Definition

A Poison Pill is a message that consistently fails processing.

---

## Example

Expected:

```json id="htg3ot"
{
  "clientId": "123",
  "country": "US"
}
```

Received:

```json id="n0xjxf"
{
  "country": "US"
}
```

Missing:

```text id="nfyfhu"
clientId
```

---

## Result

Consumer fails every time.

---

## Problem

Without DLQ:

```text id="i0qhq5"
Read
Fail
Retry
Read
Fail
Retry
```

Forever.

---

### Production Impact

One bad message can block an entire partition.

---

### 🎯 Interview Sound Bite

Poison pill messages repeatedly fail processing and are a common reason for DLQ adoption.

---

# 58. Retry Strategies

## Strategy 1

Immediate Retry

```text id="tx5zaj"
Fail
Retry Immediately
```

---

### Problem

Usually ineffective.

System may still be unavailable.

---

# Strategy 2

Fixed Delay Retry

Example:

```text id="uqgq1w"
Retry After 5 Seconds
```

---

### Benefits

Allows transient issues to recover.

---

# Strategy 3

Exponential Backoff

Example:

```text id="40c0v5"
Retry 1 = 1 Second

Retry 2 = 2 Seconds

Retry 3 = 4 Seconds

Retry 4 = 8 Seconds
```

---

### Benefits

Reduces pressure on failing systems.

---

### Production Recommendation

Use:

```text id="l6oj05"
Exponential Backoff
```

for external dependencies.

---

### 🎯 Interview Sound Bite

Retries should be limited and typically use exponential backoff to avoid overwhelming downstream systems.

---

# 59. Retry Topic Pattern

## Advanced Pattern

Instead of:

```text id="mcbjta"
Main Topic
      ↓
DLQ
```

Use:

```text id="1kzq5k"
Main Topic
      ↓
Retry Topic
      ↓
Retry Topic
      ↓
Retry Topic
      ↓
DLQ
```

---

## Example

```text id="eq4gb0"
screening.jobs
```

↓

```text id="ckm1ok"
screening.jobs.retry.1
```

↓

```text id="8em9v4"
screening.jobs.retry.2
```

↓

```text id="2tm3xh"
screening.jobs.retry.3
```

↓

```text id="8nl9w6"
screening.jobs.dlq
```

---

### Benefits

Separates:

```text id="l7o1np"
Temporary Failures
Permanent Failures
```

---

### Senior Interview Point

Large-scale systems often use retry topics instead of repeated processing within the same consumer.

---

# 60. DLQ Architecture

## Standard Flow

```text id="qg5p8o"
Producer
      ↓
Main Topic
      ↓
Consumer
      ↓
Success ───────────────► Done

Failure
      ↓
Retry
      ↓
Retry
      ↓
DLQ Topic
```

---

## Why Preserve Messages?

Compliance teams often require:

```text id="96gqnt"
Auditability
Traceability
Recoverability
```

---

### Example

Financial Crimes Platform

Cannot simply discard:

```text id="uwktjw"
Sanctions Screening Request
```

Need:

```text id="hkh8dk"
Investigation
Replay
Audit Trail
```

---

### 🎯 Interview Sound Bite

DLQs provide recoverability and auditability for failed records.

---

# 61. Spring Kafka Error Handling

## Common Pattern

```java id="8p4xpd"
@KafkaListener(...)
public void consume(ClientRecord record) {

    process(record);

}
```

---

Failure:

```text id="w8tlm7"
Exception Thrown
```

---

Error Handler decides:

```text id="ztlvr7"
Retry?
Send To DLQ?
Ignore?
```

---

### Spring Components

```java id="tqrr48"
DefaultErrorHandler

DeadLetterPublishingRecoverer
```

---

### 🎯 Interview Sound Bite

Spring Kafka uses error handlers and recoverers to route failed records to retry topics or DLQs.

---

# 62. Spring Kafka DLQ Example

## DLQ Publisher

```java id="ly0d3v"
DeadLetterPublishingRecoverer recoverer =
    new DeadLetterPublishingRecoverer(kafkaTemplate);
```

---

## Error Handler

```java id="tcc8d4"
DefaultErrorHandler handler =
    new DefaultErrorHandler(
        recoverer,
        new FixedBackOff(1000, 3)
    );
```

---

Meaning:

```text id="z3d4qj"
Retry 3 Times
Then Send To DLQ
```

---

### Interview Sound Bite

A common Spring Kafka pattern is retrying a message a fixed number of times before publishing it to a DLQ topic.

---

# 63. Replaying DLQ Messages

## Why Replay?

Issue fixed.

Need to reprocess failed records.

---

Example:

```text id="p3g7k7"
Bad Reference Data Fixed
```

Now:

```text id="z1jd9h"
Replay DLQ
```

---

## Flow

```text id="0k2d0o"
DLQ Topic
      ↓
Replay Job
      ↓
Main Topic
      ↓
Normal Processing
```

---

### Benefits

Failed records are not lost.

---

### Production Sound Bite

DLQ messages should be replayable once the root cause has been resolved.

---

# 64. My Screening Architecture Example

Topic:

```text id="7zkdfg"
screening.jobs
```

Potential Failures:

```text id="0drd2v"
Invalid Client Payload
Reference Data Issues
Vendor Connectivity Problems
Schema Mismatches
```

---

Pattern:

```text id="d2zz86"
Retry
Retry
Retry
DLQ
```

---

Benefits:

```text id="s6twr7"
No Lost Records
Operational Visibility
Replay Capability
Audit Compliance
```

---

# Common Interview Questions

## Question

Why not simply discard failed messages?

Answer:

```text id="2edb7w"
Because compliance and business-critical systems require auditability, recoverability, and root cause analysis.
```

---

## Question

When should a message go to DLQ?

Answer:

```text id="r5o7j4"
After retries are exhausted or when a failure is determined to be permanent.
```

---

## Question

What is a poison pill message?

Answer:

```text id="c3owpr"
A message that consistently fails processing and cannot be successfully consumed.
```

---

## Question

Can DLQ messages be replayed?

Answer:

```text id="o7jvmu"
Yes. Replay is one of the primary reasons DLQs exist.
```

---

# Quick Revision Sheet

```text id="pm2kkl"
DLQ
=
Dead Letter Queue

Purpose
=
Store Failed Messages

Poison Pill
=
Always Fails

Retries
=
Before DLQ

Best Practice
=
Exponential Backoff

Spring Kafka
=
DefaultErrorHandler

Recovery
=
Replay DLQ Messages
```

---

# Cross References

See Also:

* Page 5 → Reliability
* Page 6 → Idempotency
* Page 8 → Replay
* Page 9 → Spring Kafka

---

# End Of Page 7

## Next Page

### Page 8 – Replay & Reprocessing

Topics Covered:

* Why Replay Is Possible
* Offset Management
* Offset Reset
* Seek API
* New Consumer Groups
* Replay Strategies
* Backfills
* Recovery Scenarios
* Real-World Use Cases

# PAGE 8

# Replay & Reprocessing

---

# 65. Why Replay Is Possible

## Traditional Message Brokers

Most traditional message brokers:

```text id="7w5z6j"
Deliver Message
      ↓
Consumer ACK
      ↓
Delete Message
```

---

Result:

```text id="iqptfp"
Replay Not Possible
```

---

## Kafka Approach

Kafka stores messages for a configurable retention period.

Messages remain available even after consumption.

---

## Example

Consumer reads:

```text id="rjl3xu"
Offset 100
```

Message still exists.

---

Consumer can later read:

```text id="08ht5f"
Offset 100
```

Again.

---

### Why?

Kafka separates:

```text id="6n9b5e"
Storage
```

from

```text id="4n2v3q"
Consumption
```

---

### 🎯 Interview Sound Bite

Replay is possible because Kafka retains messages independently of consumer progress.

---

# 66. Message Retention

## Definition

Kafka stores messages for a configured retention period.

Examples:

```properties id="i5ow7j"
retention.ms
retention.hours
retention.bytes
```

---

## Example

```properties id="n8xj98"
retention.hours=168
```

Means:

```text id="jg8e9g"
7 Days
```

of data retained.

---

## Important

Message consumption does NOT delete data.

---

Consumer may read:

```text id="u9eh5h"
Offset 100
```

Kafka still retains:

```text id="5r6lml"
Offset 100
```

Until retention expires.

---

### 🎯 Interview Sound Bite

Kafka retention is time or size based, not consumption based.

---

# 67. Replay Fundamentals

## Definition

Replay means:

```text id="m46y3w"
Reprocessing Previously Consumed Messages
```

---

## Common Reasons

### Bug Fix

Application incorrectly processed messages.

---

### New Feature

Need to derive new business data.

---

### Data Recovery

Consumer failure.

---

### Audit Requirements

Reconstruct historical state.

---

### ML / AI Use Cases

Retrain models from historical events.

---

### 🎯 Interview Sound Bite

Replay allows organizations to derive new business value from historical event streams.

---

# 68. Offset Reset

## Simplest Replay Method

Move consumer offsets backward.

---

## Example

Current Offset:

```text id="d4q3bz"
500
```

Reset To:

```text id="c4hb67"
400
```

---

Consumer processes:

```text id="0tb5iv"
400
401
402
...
500
```

Again.

---

### Benefits

Simple.

Fast.

---

### Risks

Duplicates.

Must use:

```text id="pw78kg"
Idempotent Consumers
```

---

### 🎯 Interview Sound Bite

Offset resets are the simplest replay mechanism and require idempotent consumers to avoid duplicate side effects.

---

# 69. Earliest Offset Replay

## Full Replay

Consumer starts from:

```text id="4xawul"
Offset 0
```

---

Configuration:

```properties id="9hf68r"
auto.offset.reset=earliest
```

---

Result:

```text id="4qk7sn"
Entire Topic Reprocessed
```

---

### Common Use Cases

* New Services
* Historical Backfills
* Analytics Pipelines

---

### Example

Analytics team launches:

```text id="vz4uz3"
Consumer Group:
analytics-v2
```

Starts from:

```text id="38i93y"
Beginning Of Topic
```

---

### Benefits

No impact on existing consumers.

---

# 70. New Consumer Group Replay

## Favorite Production Pattern

Create a new consumer group.

---

Existing Group:

```text id="sgl7rj"
screening-workers
```

---

New Group:

```text id="d5ubq8"
screening-workers-replay
```

---

Result:

```text id="3d7nqx"
Both Can Read Same Messages
```

---

### Benefits

Safe.

No offset modification.

No production disruption.

---

### Production Sound Bite

The safest replay strategy is often creating a new consumer group rather than modifying production offsets.

---

# 71. Kafka Seek API

## Advanced Replay

Kafka allows consumers to move directly to specific offsets.

---

Example:

```java id="4pcrbw"
consumer.seek(partition, offset);
```

---

Move to:

```text id="8p8lwf"
Offset 2500
```

Immediately.

---

### Use Cases

Replay:

```text id="ehjnn0"
Single Client
Single Batch
Specific Incident Window
```

---

### Benefits

Highly targeted recovery.

---

### 🎯 Interview Sound Bite

Seek allows consumers to reposition themselves to any offset for targeted replay.

---

# 72. Replay Architecture

## Generic Flow

```text id="6o3m4l"
Kafka Topic
      ↓
Consumer
      ↓
Business Logic
```

---

Replay:

```text id="vf7k1f"
Kafka Topic
      ↓
Replay Consumer
      ↓
Business Logic
```

---

Same data.

Different consumer.

---

### Benefits

No production disruption.

---

# 73. Recovery Scenario

## Example

Consumer bug.

Timeline:

```text id="9ncbvq"
Day 1
Bug Introduced

Day 5
Bug Detected

Day 6
Fix Released
```

---

Need to reprocess:

```text id="2aj4cn"
Days 1-5
```

Data.

---

Replay:

```text id="c7ew6m"
Historical Events
```

Using:

```text id="d8eqif"
New Consumer Group
```

---

Result:

```text id="yl6k3t"
Corrected Results
```

Without rebuilding source systems.

---

### 🎯 Interview Sound Bite

Replay dramatically reduces recovery costs because source systems do not need to regenerate historical data.

---

# 74. Backfill Processing

## Definition

Reprocessing historical data to create new outputs.

---

Example

New Requirement:

```text id="cb4e3k"
Generate Risk Score
```

for all historical events.

---

Replay:

```text id="ndjz0v"
2 Years Of Events
```

---

Generate:

```text id="gmb74u"
Risk Scores
```

---

### Benefits

Historical data becomes reusable.

---

# 75. Replay + Idempotency

## Important Relationship

Replay inherently creates duplicates.

---

Example

Original Processing:

```text id="4y1rpn"
Msg57
```

Processed.

---

Replay:

```text id="o71d7u"
Msg57
```

Processed again.

---

Without idempotency:

```text id="dfnpjr"
Duplicate Alerts
Duplicate Emails
Duplicate Cases
```

---

With idempotency:

```text id="wvlc7u"
Safe Replay
```

---

### 🎯 Interview Favorite

Question:

Why are idempotent consumers required for replay?

Answer:

Because replay intentionally reprocesses previously consumed messages, which would otherwise create duplicate side effects.

---

# 76. Replay In My Screening Architecture

## Example

Screening Rules Updated

Need:

```text id="bwlm9j"
Historical Re-Screening
```

---

Approach:

```text id="bubwz3"
Kafka Topic
      ↓
Replay Consumer Group
      ↓
Updated Screening Logic
      ↓
New Alerts
```

---

Benefits:

```text id="7dcl71"
No Source System Rebuild
No Data Reload
Fast Recovery
```

---

### Production Sound Bite

Kafka replay allows historical re-screening when sanctions lists, business rules, or screening logic change.

---

# Common Interview Questions

## Question

Why can Kafka replay messages?

Answer:

```text id="jlwmg7"
Because messages are retained independently of consumer progress.
```

---

## Question

How do you replay data?

Answer:

```text id="u4cxx2"
Offset Reset
New Consumer Group
Seek API
```

---

## Question

Safest replay strategy?

Answer:

```text id="1j2slo"
Create A New Consumer Group
```

---

## Question

Why is replay valuable?

Answer:

```text id="jrzc35"
Recovery
Backfills
Analytics
Auditing
Machine Learning
```

---

# Quick Revision Sheet

```text id="n8cfhf"
Replay
=
Reprocess Historical Events

Retention
=
Independent Of Consumption

Replay Methods
=
Offset Reset
New Consumer Group
Seek API

Safest Method
=
New Consumer Group

Replay Requires
=
Idempotent Consumers

Benefits
=
Recovery
Backfill
Auditing
Analytics
```

---

# Cross References

See Also:

* Page 5 → Reliability
* Page 6 → Idempotency
* Page 7 → DLQ
* Page 9 → Spring Kafka

---

# End Of Page 8

## Next Page

### Page 9 – Spring Kafka

Topics Covered:

* KafkaTemplate
* KafkaListener
* Consumer Factory
* Producer Factory
* Manual Acknowledgments
* Error Handling
* Retry Topics
* DLQ Integration
* Transactions
* Production Best Practices

# PAGE 9

# Spring Kafka

---

# 77. Why Spring Kafka?

## Problem

Using the Kafka Java API directly requires handling:

* Producer creation
* Consumer creation
* Serialization
* Offset management
* Error handling
* Threading
* Retries

---

## Spring Kafka Solution

Spring Kafka provides:

```text
Abstractions
Configuration
Dependency Injection
Retry Handling
DLQ Support
```

---

## Core Components

```text
KafkaTemplate
@KafkaListener
ProducerFactory
ConsumerFactory
ContainerFactory
ErrorHandlers
```

---

### 🎯 Interview Sound Bite

Spring Kafka simplifies Kafka development by providing declarative producers, consumers, retries, offset management, and DLQ integration.

---

# 78. KafkaTemplate

## Definition

Primary producer abstraction.

Used to publish messages.

---

## Example

```java
@Autowired
private KafkaTemplate<String, ClientRecord> kafkaTemplate;
```

---

## Send Message

```java
kafkaTemplate.send(
    "screening.jobs",
    clientId,
    clientRecord
);
```

---

## What Happens?

```text
Application
      ↓
KafkaTemplate
      ↓
Producer
      ↓
Kafka Topic
```

---

### My Screening Architecture Example

Producer:

```text
Spring Batch Worker
```

Publishes:

```text
Client Screening Requests
```

Topic:

```text
screening.jobs
```

---

### 🎯 Interview Sound Bite

KafkaTemplate is Spring Kafka's producer abstraction used to publish events to Kafka topics.

---

# 79. @KafkaListener

## Definition

Primary consumer abstraction.

---

## Example

```java
@KafkaListener(
    topics = "screening.jobs",
    groupId = "screening-workers"
)
public void consume(ClientRecord record) {

}
```

---

## What Happens?

```text
Kafka Topic
      ↓
Consumer Group
      ↓
@KafkaListener
```

---

### Benefits

* Automatic polling
* Thread management
* Serialization support
* Offset management

---

### 🎯 Interview Sound Bite

@KafkaListener allows applications to consume Kafka messages declaratively without manually managing Kafka consumer APIs.

---

# 80. ProducerFactory

## Purpose

Creates Kafka producers.

---

## Example

```java
@Bean
public ProducerFactory<String, ClientRecord>
producerFactory() {

}
```

---

## Responsibilities

* Producer configuration
* Serializer setup
* Connection settings

---

### Common Configurations

```properties
bootstrap.servers
acks=all
retries
enable.idempotence=true
```

---

# 81. ConsumerFactory

## Purpose

Creates Kafka consumers.

---

## Responsibilities

* Deserializers
* Group IDs
* Offset configuration

---

### Common Configurations

```properties
group.id
auto.offset.reset
enable.auto.commit
```

---

# 82. Serialization

## Why Needed?

Kafka stores:

```text
Bytes
```

Applications use:

```text
Java Objects
```

---

## Producer

Object

↓

JSON

↓

Kafka

---

## Consumer

Kafka

↓

JSON

↓

Object

---

## Example

```java
JsonSerializer
JsonDeserializer
```

---

### Interview Sound Bite

Kafka stores bytes. Serializers and deserializers convert application objects into transportable formats.

---

# 83. Manual Acknowledgments

## Recommended Configuration

Disable:

```properties
enable.auto.commit=false
```

---

## Listener Example

```java
@KafkaListener(...)
public void consume(
    ClientRecord record,
    Acknowledgment ack
) {

    process(record);

    ack.acknowledge();
}
```

---

## Why?

Ensures:

```text
Process First
Commit Later
```

---

### Benefits

* No data loss
* Better recovery
* At-least-once delivery

---

### ⚠️ Production Pitfall

Never commit before business processing completes.

---

### 🎯 Interview Sound Bite

Manual acknowledgments ensure offsets are committed only after successful business processing.

---

# 84. Error Handling

## Problem

Consumer throws exception.

Example:

```java
throw new RuntimeException();
```

---

Question:

```text
Retry?
DLQ?
Ignore?
Stop Processing?
```

---

## Spring Kafka Solution

```java
DefaultErrorHandler
```

---

### Responsibilities

* Retry logic
* Backoff handling
* DLQ routing

---

### 🎯 Interview Sound Bite

Spring Kafka centralizes retry and failure handling using configurable error handlers.

---

# 85. Retry Configuration

## Fixed Retry

```java
new FixedBackOff(
    1000,
    3
);
```

Meaning:

```text
Retry Every 1 Second
Maximum 3 Retries
```

---

## Exponential Backoff

Example:

```text
1s
2s
4s
8s
```

---

### Benefits

Protects downstream systems.

---

### Recommended

External APIs:

```text
Exponential Backoff
```

---

# 86. DLQ Integration

## DeadLetterPublishingRecoverer

Example:

```java
DeadLetterPublishingRecoverer recoverer =
        new DeadLetterPublishingRecoverer(
            kafkaTemplate
        );
```

---

## Flow

```text
Main Topic
      ↓
Consumer
      ↓
Retry
      ↓
Retry
      ↓
Retry
      ↓
DLQ
```

---

## Example

```text
screening.jobs
```

↓

```text
screening.jobs.dlq
```

---

### Interview Sound Bite

DeadLetterPublishingRecoverer automatically routes failed records to DLQ topics after retries are exhausted.

---

# 87. Retry Topics

## Advanced Pattern

Instead of retrying immediately:

```text
Main Topic
      ↓
Retry Topic
      ↓
Retry Topic
      ↓
DLQ
```

---

## Example

```text
screening.jobs
```

↓

```text
screening.jobs.retry.1
```

↓

```text
screening.jobs.retry.2
```

↓

```text
screening.jobs.dlq
```

---

### Benefits

* Better isolation
* Better observability
* Better scaling

---

# 88. Kafka Transactions

## Problem

Need atomic processing.

Example:

```text
Consume Message
Update Database
Publish Event
Commit Offset
```

---

Failure in the middle:

```text
Partial State
```

---

## Kafka Transactions

Allow:

```text
All Success
or
All Rollback
```

---

### Example

```java
@Transactional
```

with Kafka transactional producer.

---

### Interview Sound Bite

Kafka transactions help implement exactly-once processing across Kafka operations.

---

# 89. Concurrency

## Single Thread

```java
@KafkaListener(...)
```

Consumes using one thread.

---

## Multi Thread

```java
factory.setConcurrency(10);
```

---

### Example

```text
10 Partitions
10 Threads
```

Maximum utilization.

---

### Interview Favorite

Question:

Should concurrency exceed partition count?

Answer:

```text
No.

Extra threads remain idle.
```

---

# 90. Observability

## Metrics To Monitor

Consumer Lag

```text
Latest Offset
-
Committed Offset
```

---

Retries

```text
Retry Count
```

---

DLQ Volume

```text
Messages Routed To DLQ
```

---

Processing Time

```text
Message Processing Latency
```

---

### Why Important?

Large lag may indicate:

```text
Slow Consumers
Partition Hotspots
Infrastructure Problems
```

---

### Production Sound Bite

Consumer lag is one of the most important Kafka metrics because it indicates whether consumers are keeping up with incoming traffic.

---

# 91. My Screening Architecture Example

Producer:

```text
Spring Batch Workers
```

Topic:

```text
screening.jobs
```

Consumer Group:

```text
screening-workers
```

Processing:

```text
Screening Logic
```

Reliability:

```text
acks=all
enable.idempotence=true
manual commits
```

Failure Handling:

```text
retry
retry
retry
DLQ
```

Observability:

```text
consumer lag
processing time
DLQ count
```

---

# Production Best Practices

## Producer

```properties
acks=all
enable.idempotence=true
retries=Integer.MAX_VALUE
```

---

## Consumer

```properties
enable.auto.commit=false
```

---

## Processing

```text
Idempotent Consumers
```

---

## Failure Handling

```text
Retries
DLQ
```

---

## Monitoring

```text
Consumer Lag
DLQ Volume
Retry Count
```

---

# Quick Revision Sheet

```text
KafkaTemplate
=
Producer

@KafkaListener
=
Consumer

Manual Ack
=
Recommended

DefaultErrorHandler
=
Retries

DeadLetterPublishingRecoverer
=
DLQ

Concurrency
=
Should Match Partitions

Consumer Lag
=
Key Metric
```

---

# Cross References

See Also:

* Page 5 → Reliability
* Page 6 → Idempotency
* Page 7 → DLQ
* Page 8 → Replay

---

# End Of Page 9

## Next Page

### Page 10 – Kafka Interview Questions & Staff Engineer Discussion

Topics Covered:

* Top 50 Kafka Interview Questions
* Senior Engineer Answers
* Staff Engineer Answers
* Architecture Tradeoffs
* Real Production Scenarios
* My 50M Screening Story


# PAGE 10

# Kafka Interview Questions & Staff Engineer Discussion

---

# Top Kafka Interview Questions

---

# 92. What Is Kafka?

## Junior Engineer Answer

Kafka is a distributed event streaming platform used to publish and consume messages.

---

## Senior Engineer Answer

Kafka is a distributed log-based event streaming platform that provides:

* High Throughput
* Fault Tolerance
* Replayability
* Ordering
* Horizontal Scalability

through topics, partitions, consumer groups, and replication.

---

## Staff Engineer Answer

Kafka acts as the backbone of event-driven architectures by decoupling producers and consumers while providing durable, replayable event streams that can scale horizontally across multiple systems.

---

### 🎯 Interview Sound Bite

Kafka combines the durability of a database with the distribution capabilities of a message broker.

---

# 93. What Is A Topic?

## Answer

A topic is a logical category of related events.

Examples:

```text id="rzpfr5"
payments
transactions
screening.jobs
alerts
```

Producers publish to topics.

Consumers subscribe to topics.

---

### Follow Up

Question:

Can topics provide parallelism?

Answer:

No.

Partitions provide parallelism.

---

# 94. What Is A Partition?

## Answer

Partitions are the unit of:

```text id="pr6o9z"
Parallelism
Scalability
Ordering
```

Kafka guarantees ordering within a partition.

---

### Follow Up

Question:

Why not one giant partition?

Answer:

Because throughput would be limited by one consumer.

---

# 95. What Is A Consumer Group?

## Answer

A consumer group is a collection of consumers working together to process a topic.

---

### Key Rule

```text id="is4b4j"
One Partition
=
One Active Consumer
```

---

### Follow Up

Question:

Why do consumer groups exist?

Answer:

To enable horizontal scaling without duplicate processing.

---

# 96. How Does Kafka Guarantee Ordering?

## Answer

Kafka guarantees ordering only within a partition.

---

### Example

Key:

```text id="1nznlu"
clientId
```

All records for that client go to the same partition.

---

### Staff Engineer Discussion

Ordering is expensive.

Only require ordering when business correctness depends on event sequence.

---

### 🎯 Interview Favorite

Question:

Does Kafka guarantee ordering across partitions?

Answer:

No.

---

# 97. Why Doesn't Kafka Support Global Ordering?

## Answer

Global ordering would require:

```text id="0j4a48"
Coordination
Synchronization
Buffering
```

Across partitions.

---

### Result

```text id="d8iw9x"
Higher Latency
Reduced Throughput
Less Scalability
```

---

### Staff Engineer Discussion

Kafka intentionally sacrifices global ordering to achieve horizontal scalability.

---

# 98. What Is An Offset?

## Answer

Offset = Position inside a partition.

Think of it as a bookmark.

---

### Important

Offsets are tracked:

```text id="9xvzjk"
Per Consumer Group
+
Per Partition
```

---

### Interview Favorite

Question:

Where are offsets stored?

Answer:

```text id="3c5c8r"
__consumer_offsets
```

---

# 99. What Happens When A Consumer Crashes?

## Answer

Kafka triggers:

```text id="d0s8lq"
Rebalance
```

Partitions are reassigned to remaining consumers.

Processing resumes from committed offsets.

---

### My Screening Example

If:

```text id="jlwm1k"
screening-worker-3
```

fails,

Kafka automatically redistributes partitions.

No manual intervention required.

---

# 100. What Is Consumer Lag?

## Answer

Consumer lag measures:

```text id="ejhlmo"
Latest Offset
-
Committed Offset
```

---

### Why Important?

Large lag indicates:

```text id="j7j0pk"
Slow Consumers
Hot Partitions
Infrastructure Problems
```

---

### Production Sound Bite

Consumer lag is one of the most important Kafka operational metrics.

---

# 101. Explain acks=0, acks=1, acks=all

## acks=0

Fastest.

No durability.

Possible data loss.

---

## acks=1

Leader acknowledgment only.

Possible data loss after leader failure.

---

## acks=all

Leader + ISR acknowledgments.

Strongest durability.

---

### Interview Favorite

Question:

Can data still be lost with acks=1?

Answer:

Yes.

Leader may crash before followers replicate.

---

# 102. What Is ISR?

## Definition

ISR = In Sync Replicas.

Replicas fully caught up with the leader.

---

### Why Important?

Kafka trusts ISR members during leader election.

---

### Production Example

```text id="8nuv34"
Replication Factor = 3
ISR = 3
```

System can tolerate broker failures.

---

# 103. What Is Producer Idempotency?

## Answer

Idempotency prevents duplicate messages caused by retries.

Enabled using:

```properties id="ntzjlwm"
enable.idempotence=true
```

---

### How?

Kafka assigns:

```text id="kzw4cg"
Producer ID
Sequence Number
```

to each message.

---

### Interview Favorite

Question:

How does Kafka detect duplicate retries?

Answer:

Producer IDs and Sequence Numbers.

---

# 104. What Is A Poison Pill Message?

## Answer

A message that consistently fails processing.

Examples:

```text id="hx0xrz"
Bad Schema
Corrupt JSON
Missing Required Fields
```

---

### Solution

```text id="g1tmbk"
DLQ
```

---

# 105. What Is A DLQ?

## Answer

A Dead Letter Queue stores permanently failing messages.

---

### Why?

Prevent one bad message from blocking an entire partition.

---

### Benefits

```text id="4i7ng9"
Auditability
Replayability
Recoverability
```

---

# 106. Why Is Replay Possible In Kafka?

## Answer

Kafka separates:

```text id="ltbnpp"
Storage
```

from

```text id="e9kr3s"
Consumption
```

Messages remain in Kafka after being consumed.

---

### Replay Methods

```text id="l33gf5"
Offset Reset
New Consumer Group
Seek API
```

---

### Safest Method

```text id="a4xtyl"
New Consumer Group
```

---

# 107. Explain At-Least-Once Delivery

## Answer

Consumer may process a message.

Application crashes.

Offset not committed.

Kafka redelivers the message.

---

### Result

```text id="a1quh5"
Duplicate Processing
```

Possible.

---

### Why?

Kafka prefers:

```text id="twv4u7"
Duplicates
```

over

```text id="w9r5y5"
Data Loss
```

---

# 108. Why Do Consumers Need To Be Idempotent?

## Answer

Because retries and replay create duplicate processing opportunities.

---

### Examples

```text id="7e9mzw"
Duplicate Alerts
Duplicate Emails
Duplicate Cases
```

---

### Solution

Business keys:

```text id="m1hcmv"
alertId
transactionId
caseId
```

---

# 109. How Would You Design A Reliable Kafka Pipeline?

## Staff Engineer Answer

Producer:

```properties id="zdrg2x"
acks=all
enable.idempotence=true
replication.factor=3
min.insync.replicas=2
```

Consumer:

```properties id="jbgyd6"
enable.auto.commit=false
```

Processing:

```text id="5if23g"
Manual Commit
Idempotent Consumer
Retries
DLQ
```

Observability:

```text id="4h4s7j"
Consumer Lag
Retry Counts
DLQ Volume
```

---

# 110. Tell Me About A Kafka Project

## My Screening Architecture Story

Problem:

```text id="zsdg2k"
50 Million Client Records
```

Needed:

```text id="5fxo4v"
Parallel Processing
Fault Tolerance
Replayability
Ordering
```

---

Solution:

```text id="wp3m3r"
Spring Batch
      ↓
Kafka Topic
(screening.jobs)
      ↓
10 Partitions
      ↓
Consumer Group
(screening-workers)
      ↓
10 Worker Services
      ↓
Screening Engine
      ↓
Actimize
```

---

Partition Key:

```text id="gfpk4x"
clientId
```

---

Benefits:

```text id="pb0v39"
40% Faster Processing
Fault Tolerant
Replay Capable
Scalable
```

---

# Staff Engineer Discussion Topics

## Topic 1

How many partitions should a topic have?

Answer:

Balance:

```text id="x8h7n4"
Throughput
Future Growth
Operational Complexity
```

Avoid excessive partition counts.

---

## Topic 2

When should ordering be enforced?

Only when business correctness requires it.

---

## Topic 3

How do you replay billions of messages safely?

Answer:

```text id="zyf9c8"
New Consumer Group
Idempotent Consumers
Dedicated Replay Pipeline
```

---

## Topic 4

What metrics would you monitor?

```text id="xzwdpj"
Consumer Lag
Producer Errors
DLQ Volume
Broker Health
ISR Count
Processing Latency
```

---

# Kafka Sound Bites

```text id="yj42vz"
Kafka is for propagation, not querying.

Maximum parallelism equals number of partitions.

Ordering exists within partitions only.

Offsets are tracked per consumer group and partition.

Replay is possible because Kafka retains messages.

Idempotent producers prevent duplicate writes.

Idempotent consumers prevent duplicate business effects.

Consumer lag is the most important operational metric.

DLQs isolate poison-pill messages.

Kafka trades global ordering for scalability.
```

---

# Quick Revision Sheet

```text id="9vlhwy"
Topic
=
Logical Stream

Partition
=
Unit Of Parallelism

Consumer Group
=
Unit Of Scaling

Offset
=
Bookmark

Replay
=
Possible

DLQ
=
Failed Messages

Ordering
=
Within Partition

Best Producer Setup
=
acks=all + idempotence

Best Consumer Setup
=
Manual Commit + Idempotent Consumer
```

---

# End Of Page 10

## Next Page

### Page 11 – My 50 Million Record Screening Architecture

Topics Covered:

* End-to-End Design
* Spring Batch Partitioning
* Kafka Architecture
* Consumer Scaling
* Fault Tolerance
* Replay Strategy
* Actimize Integration
* Interview Story

# PAGE 11

# My 50 Million Record Screening Architecture

---

# System Overview

## Business Problem

The platform needed to screen:

```text
50+ Million Client Records
```

against:

* Sanctions Lists
* PEP Lists
* Adverse Media Sources
* Internal Watchlists

while meeting:

* Regulatory Requirements
* SLA Requirements
* Fault Tolerance Requirements
* Replay Requirements

---

## High Level Architecture

```text
50M Client Records
        │
        ▼
Spring Batch Partitioner
        │
        ▼
Kafka Topic
(screening.jobs)
        │
        ▼
10 Kafka Partitions
        │
        ▼
Consumer Group
(screening-workers)
        │
        ▼
10 Worker Services
        │
        ▼
Screening Vendors
(RDC / Bridger)
        │
        ▼
MQ
        │
        ▼
Actimize
        │
        ▼
Investigators
```

---

# Why The Existing Architecture Was Not Enough

Original platform characteristics:

```text
Large Batch Processing
Limited Parallelism
Long Processing Windows
Scaling Challenges
```

---

## Main Challenges

### Scale

```text
50M+ Records
```

---

### Throughput

Need to process millions of screening requests efficiently.

---

### Reliability

Cannot lose screening requests.

---

### Replayability

Need ability to rescreen clients when:

* Watchlists change
* Vendor logic changes
* Business rules change

---

### Ordering

Client-related screening events must remain ordered.

---

# Spring Batch Partitioning

## Why Partitioning?

Without partitioning:

```text
One Reader
One Processor
One Writer
```

Throughput becomes limited.

---

## Solution

Split workload into partitions.

---

### ClientIdRangePartitioner

Example:

```text
ClientId 1       -> 10,000,000
ClientId 10M     -> 20,000,000
ClientId 20M     -> 30,000,000
ClientId 30M     -> 40,000,000
ClientId 40M     -> 50,000,000
```

---

Result:

```text
5 Partitions
```

Each partition processes:

```text
10 Million Records
```

---

## Benefits

```text
Parallel Execution
Independent Processing
Higher Throughput
```

---

# Reader Design

## JdbcPagingItemReader

Used to avoid loading massive datasets into memory.

---

Configuration:

```java
pageSize = 10000
chunkSize = 1000
```

---

### Why Paging?

Bad:

```text
Load 50 Million Records
Into Memory
```

Impossible.

---

Good:

```text
Read Small Pages
Process
Continue
```

---

### Benefits

```text
Low Memory Usage
Stable Throughput
Scalable
```

---

# Kafka Producer Layer

## Why Kafka?

Needed:

```text
Parallelism
Replayability
Durability
Fault Tolerance
```

---

### Producer Flow

```text
Spring Batch Worker
        │
        ▼
KafkaTemplate
        │
        ▼
screening.jobs
```

---

## Partition Key

```text
clientId
```

---

### Why clientId?

Guarantees:

```text
All Events For Same Client
Remain Ordered
```

---

# Kafka Topic Design

## Topic

```text
screening.jobs
```

---

## Partition Count

```text
10 Partitions
```

---

### Why 10?

Provides:

```text
10x Parallelism
Future Growth
Fault Isolation
```

---

## Replication

```text
Replication Factor = 3
```

---

## Reliability

```properties
acks=all
enable.idempotence=true
min.insync.replicas=2
```

---

# Consumer Architecture

## Consumer Group

```text
screening-workers
```

---

## Worker Services

```text
Worker 1
Worker 2
Worker 3
...
Worker 10
```

---

## Assignment

```text
P0 -> Worker 1
P1 -> Worker 2
P2 -> Worker 3
...
P9 -> Worker 10
```

---

## Key Kafka Principle

```text
Maximum Parallelism
=
Number Of Partitions
```

---

### Interview Favorite

Question:

Why not 50 workers?

Answer:

```text
Only 10 partitions exist.

Only 10 workers can be active.
```

---

# Screening Engine Layer

## Responsibilities

Worker receives:

```text
Client Screening Request
```

---

Worker performs:

```text
Data Enrichment
Request Transformation
Vendor Submission
Response Processing
```

---

## Vendors

```text
RDC
Bridger
```

---

# Reliability Strategy

## Producer Side

```properties
acks=all
enable.idempotence=true
retries=Integer.MAX_VALUE
```

---

## Consumer Side

```properties
enable.auto.commit=false
```

---

## Processing

```text
Process
Persist Result
Commit Offset
```

---

### Why?

Avoid:

```text
Offset Committed
Before Processing Complete
```

---

# Failure Handling

## Worker Crash

Example:

```text
Worker 5 Crashes
```

---

Kafka triggers:

```text
Consumer Rebalance
```

---

Partitions reassigned:

```text
Worker 6
Worker 7
Worker 8
```

---

Processing resumes from:

```text
Last Committed Offset
```

---

### Result

```text
No Data Loss
No Manual Recovery
```

---

# Replay Strategy

## Why Replay Matters

Need re-screening when:

```text
Sanctions Lists Change
PEP Lists Change
Business Rules Change
Vendor Logic Changes
```

---

## Replay Flow

```text
Historical Kafka Topic
        │
        ▼
Replay Consumer Group
        │
        ▼
Updated Screening Logic
        │
        ▼
New Results
```

---

## Benefit

No need to rebuild source systems.

---

# DLQ Strategy

## Failure Types

```text
Bad Payload
Schema Mismatch
Vendor Error
Reference Data Error
```

---

## Flow

```text
Main Topic
      ↓
Retry
      ↓
Retry
      ↓
Retry
      ↓
DLQ
```

---

## Benefits

```text
Pipeline Continues
Auditability
Recoverability
Replayability
```

---

# Observability

## Metrics Monitored

### Consumer Lag

```text
Latest Offset
-
Committed Offset
```

---

### Processing Time

```text
End To End Latency
```

---

### DLQ Volume

```text
Failed Messages
```

---

### Retry Counts

```text
Operational Health
```

---

# Results

## Throughput Improvement

```text
~40% Faster Processing
```

---

## Scalability

```text
50M+ Records
```

processed reliably.

---

## Reliability

```text
No Lost Screening Requests
```

---

## Replayability

```text
Historical Re-Screening
Supported
```

---

## Fault Tolerance

```text
Worker Failure Recovery
Automatic
```

---

# Staff Engineer Discussion

## Why Kafka Instead Of Direct Vendor Calls?

Kafka provides:

```text
Buffering
Replayability
Fault Tolerance
Backpressure Handling
Independent Scaling
```

---

## Why Key By clientId?

Maintains:

```text
Ordering
```

for all client-related screening events.

---

## Why Manual Commit?

Prevents:

```text
Data Loss
```

when processing succeeds but offsets are committed prematurely.

---

## Why Replication Factor 3?

Provides:

```text
High Availability
Broker Failure Tolerance
```

without excessive storage overhead.

---

# Interview Sound Bite

I designed a Kafka-based screening platform that processed over 50 million client records using Spring Batch partitioning, Kafka consumer groups, and distributed worker services. By partitioning workload by client ranges and distributing processing through Kafka, we improved throughput by approximately 40% while maintaining ordering, replayability, fault tolerance, and regulatory-grade reliability.

---

# Quick Revision Sheet

```text
50M Records

Spring Batch
      ↓
Kafka
      ↓
10 Partitions
      ↓
10 Workers
      ↓
Screening Vendors
      ↓
Actimize

Partition Key = clientId

acks=all

enable.idempotence=true

manual commit

DLQ

Replay

40% Faster Processing
```

---

# End Of Page 11

## Next Page

### Page 12 – Production Pitfalls & Real World Failure Scenarios

Topics Covered:

* Consumer Lag Explosions
* Hot Partitions
* Rebalancing Storms
* Offset Mistakes
* Auto Commit Failures
* Duplicate Processing
* Data Loss Scenarios
* Kafka War Stories

# PAGE 12

# Production Pitfalls & Real World Failure Scenarios

---

# Why This Chapter Matters

Most engineers understand:

```text
Topic
Partition
Consumer Group
Offset
```

Very few engineers understand:

```text
Production Failures
Recovery
Operational Tradeoffs
```

This section covers the kinds of issues Staff Engineers and Architects deal with in production.

---

# 111. Consumer Lag Explosion

## What Is Consumer Lag?

```text
Consumer Lag
=
Latest Offset
-
Committed Offset
```

---

## Example

Partition Latest Offset:

```text
1,000,000
```

Consumer Offset:

```text
900,000
```

Lag:

```text
100,000
```

---

## Why Lag Happens

### Slow Consumers

```text
Incoming Rate
>
Processing Rate
```

---

### Expensive Business Logic

Example:

```text
External API Calls
Database Queries
Complex Calculations
```

---

### Consumer Failure

Consumer crashes.

Remaining consumers inherit work.

Lag grows.

---

### Traffic Spike

Producer throughput suddenly increases.

Consumers cannot keep up.

---

## Production Impact

```text
Delayed Processing
Missed SLAs
Increased Memory Pressure
Business Delays
```

---

## Solutions

### Increase Partitions

Allows more parallelism.

---

### Increase Consumers

Only works if partitions exist.

---

### Optimize Processing

Reduce downstream bottlenecks.

---

### Scale Infrastructure

More CPU.

More Memory.

---

### 🎯 Interview Sound Bite

Consumer lag indicates consumers are processing data slower than producers are generating it.

---

# 112. Hot Partitions

## Problem

Not all partition keys are equal.

---

Example:

```text
clientId = 123
```

Generates:

```text
10 Million Events
```

---

Other clients:

```text
100 Events
```

---

Result

```text
Partition 4
=
Extremely Busy

All Other Partitions
=
Mostly Idle
```

---

## Why This Is Bad

Kafka parallelism becomes:

```text
One Busy Partition
```

instead of:

```text
Evenly Distributed Load
```

---

## Symptoms

```text
High Lag
Uneven CPU Usage
Slow Throughput
```

---

## Solutions

### Better Partition Keys

Bad:

```text
country
```

Good:

```text
clientId
```

---

### Composite Keys

```text
clientId-region
```

---

### More Partitions

May reduce hotspots.

---

### 🎯 Interview Favorite

Question:

Can Kafka still have bottlenecks with many partitions?

Answer:

Yes.

Hot partitions can become throughput bottlenecks.

---

# 113. Rebalancing Storms

## What Is Rebalancing?

Kafka redistributes partitions when:

```text
Consumer Joins
Consumer Leaves
Consumer Crashes
```

---

## Example

Before:

```text
P0 -> C1
P1 -> C2
P2 -> C3
```

---

Consumer C2 Crashes.

Kafka rebalances.

---

After:

```text
P0 -> C1
P1 -> C3
P2 -> C1
```

---

## Problem

Rebalancing pauses processing.

---

## Rebalancing Storm

Repeated:

```text
Join
Leave
Join
Leave
Join
Leave
```

---

Results:

```text
Constant Rebalancing
No Useful Work
```

---

## Causes

### Unstable Infrastructure

Containers repeatedly restarting.

---

### Bad Health Checks

Consumers constantly killed.

---

### Long GC Pauses

Consumer appears dead.

---

## Production Impact

```text
High Latency
Throughput Drops
Consumer Churn
```

---

### 🎯 Interview Sound Bite

Excessive consumer churn can create rebalancing storms that significantly reduce Kafka throughput.

---

# 114. Auto Commit Disaster

## Common Mistake

Configuration:

```properties
enable.auto.commit=true
```

---

Timeline:

```text
Read Msg57
Offset Committed
Processing Begins
Application Crashes
```

---

Restart:

```text
Starts At Msg58
```

---

Result:

```text
Msg57 Lost Forever
```

---

### Why?

Kafka thinks processing succeeded.

---

### Solution

```properties
enable.auto.commit=false
```

Use:

```text
Manual Commit
```

---

### 🎯 Interview Favorite

Question:

Can Kafka lose data with auto commit?

Answer:

Yes.

If offsets are committed before processing completes.

---

# 115. Duplicate Processing

## Opposite Problem

Timeline:

```text
Read Msg57
Process Msg57
Application Crashes
Offset Not Committed
```

---

Restart:

```text
Read Msg57 Again
```

---

Result:

```text
Duplicate Processing
```

---

### Important

This is expected.

Kafka prefers:

```text
Duplicates
```

over

```text
Data Loss
```

---

## Solution

Idempotent Consumers.

---

### 🎯 Interview Sound Bite

At-least-once delivery trades duplicate processing for reliability.

---

# 116. Large Message Problems

## Example

Message Size:

```text
100 MB
```

---

Problems:

```text
Slow Replication
Network Pressure
GC Pressure
Disk Pressure
```

---

## Best Practice

Store:

```text
Metadata
```

in Kafka.

Store:

```text
Large Files
```

elsewhere.

---

Example:

```text
S3
Database
Object Store
```

---

### Interview Sound Bite

Kafka performs best with relatively small messages and should not be used as a file storage system.

---

# 117. Infinite Retry Loops

## Example

Consumer:

```text
Read Message
Fail
Retry
Fail
Retry
Fail
Retry
```

Forever.

---

## Problem

One bad message blocks the partition.

---

## Solution

```text
Retries
+
DLQ
```

---

### Best Practice

```text
Retry
Retry
Retry
DLQ
```

---

# 118. Misunderstanding Ordering

## Common Incorrect Assumption

```text
Kafka Guarantees Topic Ordering
```

False.

---

## Actual Guarantee

```text
Partition Ordering
```

Only.

---

### Example

Partition 0:

```text
A
B
C
```

Partition 1:

```text
D
E
F
```

---

Kafka guarantees:

```text
A -> B -> C
```

and

```text
D -> E -> F
```

---

Kafka does NOT guarantee:

```text
A B C D E F
```

---

### 🎯 Interview Favorite

This is one of the most commonly misunderstood Kafka concepts.

---

# 119. Broker Failure Scenario

## Without Replication

```text
Broker Dies
=
Data Lost
```

---

## With Replication

```text
Leader Dies
      ↓
Follower Promoted
      ↓
Continue Processing
```

---

## Best Practice

```text
Replication Factor = 3
min.insync.replicas = 2
acks = all
```

---

# 120. Replay Disaster

## Scenario

Replay:

```text
100 Million Events
```

---

Consumer:

```text
Non-Idempotent
```

---

Results:

```text
Duplicate Alerts
Duplicate Emails
Duplicate Cases
Duplicate Payments
```

---

## Lesson

Replay requires:

```text
Idempotent Consumers
```

---

# 121. Vendor Dependency Failure

## Real World Example

Consumer:

```text
Receives Screening Request
```

Calls:

```text
RDC
Bridger
```

---

Vendor Down.

---

Result:

```text
Retries
Lag Growth
Backpressure
```

---

## Solution

```text
Retry Topics
DLQ
Circuit Breakers
Backpressure Controls
```

---

### 💡 My Screening Architecture Example

External screening vendors are often the slowest component.

Kafka acts as a buffer between:

```text
Internal Systems
```

and

```text
Vendor Systems
```

allowing each side to scale independently.

---

# 122. Monitoring Checklist

## Broker Metrics

```text
Broker Health
ISR Count
Disk Usage
Network Throughput
```

---

## Consumer Metrics

```text
Consumer Lag
Processing Time
Rebalance Count
Retry Count
```

---

## DLQ Metrics

```text
DLQ Volume
DLQ Growth Rate
```

---

## Producer Metrics

```text
Producer Errors
Retry Count
Latency
```

---

# Staff Engineer Discussion

## Question

What is the first Kafka metric you check?

Answer:

```text
Consumer Lag
```

because it quickly indicates whether consumers are keeping up.

---

## Question

What is the most dangerous Kafka configuration?

Answer:

```properties
enable.auto.commit=true
```

for business-critical systems.

---

## Question

What causes most Kafka production incidents?

Answer:

```text
Consumer Failures
Bad Partition Keys
Offset Mismanagement
External Dependency Failures
```

---

# Production War Stories Summary

```text
Consumer Lag Explosion
Hot Partitions
Rebalancing Storms
Auto Commit Data Loss
Duplicate Processing
Poison Pill Messages
Replay Disasters
Vendor Outages
```

These are significantly more valuable in senior interviews than simply explaining what a topic or partition is.

---

# Quick Revision Sheet

```text
Lag
=
Consumers Too Slow

Hot Partition
=
Uneven Key Distribution

Rebalancing Storm
=
Consumer Churn

Auto Commit
=
Potential Data Loss

Manual Commit
=
Recommended

Replay
=
Requires Idempotency

DLQ
=
Failed Messages

Most Important Metric
=
Consumer Lag
```

---

# End Of Page 12

---

# From Kafka Concepts To Real Production Systems

Everything covered so far:

- Topics
- Partitions
- Consumer Groups
- Reliability
- Idempotency
- DLQ
- Replay

was applied directly in the Financial Crimes screening platform described in the following chapters.

The next sections demonstrate how Kafka was used to process over 50 million client records, integrate with RDC and Bridger, generate approximately 35,000 monthly alerts, support sanctions, PEP and adverse media screening, and provide replayable, fault-tolerant processing for regulatory workloads.

The goal is to connect Kafka theory with real-world architecture decisions.

---


## Next Page

### Page 13 – Kafka Interview Sound Bites & Executive-Level Discussion

Topics Covered:

* 30 Second Answers
* 60 Second Answers
* Staff Engineer Talking Points
* Architecture Tradeoffs
* VP-Level Discussion Topics
* Screening Platform Executive Narrative


# PAGE 13

# Kafka Interview Sound Bites & Executive-Level Discussion

---

# Why This Chapter Exists

Most candidates know:

```text
Topic
Partition
Consumer Group
Offset
```

Very few candidates can explain:

```text
Why Kafka?
Why not RabbitMQ?
Why 10 partitions?
Why manual commits?
Why replay matters?
Why ordering is expensive?
```

This chapter focuses on concise, high-impact answers suitable for:

* Senior Engineer Interviews
* Staff Engineer Interviews
* Principal Engineer Interviews
* VP Promotion Discussions
* Architecture Reviews

---

# 123. 30-Second Kafka Explanation

## Interview Version

Kafka is a distributed event streaming platform that enables systems to publish and consume events at scale. It provides durability through replication, scalability through partitioning, ordering within partitions, and replayability through retained event logs.

---

# 124. 60-Second Kafka Explanation

Kafka is a distributed log-based event streaming platform designed for high-throughput, fault-tolerant event processing.

Producers publish events to topics.

Topics are divided into partitions.

Partitions provide scalability and ordering.

Consumer groups provide horizontal scaling.

Replication protects against broker failures.

Offsets track consumer progress.

Unlike traditional message brokers, Kafka retains messages after consumption, enabling replay and recovery.

---

# 125. Why Kafka Instead Of RabbitMQ?

## RabbitMQ

Optimized for:

```text
Task Queues
Work Distribution
Short-Lived Messages
```

---

## Kafka

Optimized for:

```text
High Throughput
Replay
Long-Term Retention
Streaming
```

---

## Staff Engineer Answer

RabbitMQ is excellent for request processing and work queues, while Kafka excels at building scalable event streams that multiple systems can consume independently.

---

### Interview Sound Bite

RabbitMQ focuses on message delivery. Kafka focuses on event streams.

---

# 126. Why Kafka Instead Of Direct API Calls?

## Direct API Calls

```text
System A
     ↓
System B
```

Problems:

```text
Tight Coupling
Backpressure
Retry Complexity
Availability Dependencies
```

---

## Kafka

```text
System A
     ↓
Kafka
     ↓
System B
```

Benefits:

```text
Decoupling
Buffering
Replay
Independent Scaling
```

---

### My Screening Example

Kafka separates:

```text
Batch Screening Pipeline
```

from:

```text
Vendor Processing Systems
```

allowing both systems to scale independently.

---

# 127. Why Use Partitions?

Without partitions:

```text
One Consumer
One Throughput Limit
```

---

With partitions:

```text
Multiple Consumers
Parallel Processing
```

---

### Sound Bite

Partitions are Kafka's unit of scalability.

---

# 128. Why Use Consumer Groups?

Consumer groups provide:

```text
Load Balancing
Fault Tolerance
Horizontal Scaling
```

---

Without consumer groups:

Every consumer processes every message.

---

With consumer groups:

Work is distributed.

---

### Sound Bite

Consumer groups allow Kafka to scale horizontally without duplicate processing.

---

# 129. Why Does Kafka Not Support Global Ordering?

Because global ordering requires:

```text
Synchronization
Coordination
Buffering
```

across partitions.

---

Result:

```text
Higher Latency
Reduced Throughput
Reduced Scalability
```

---

### Executive Sound Bite

Kafka intentionally sacrifices global ordering to achieve horizontal scalability.

---

# 130. Why Is Replay Valuable?

Replay allows:

```text
Recovery
Backfills
Reprocessing
Auditing
Machine Learning
```

without rebuilding source systems.

---

### My Screening Example

When screening logic changes:

```text
Replay Historical Events
```

instead of:

```text
Reload 50M Records
```

---

# 131. Why Is Consumer Lag Important?

Consumer lag measures:

```text
Latest Offset
-
Committed Offset
```

---

Lag indicates:

```text
Consumer Health
Throughput Capacity
Operational Risk
```

---

### Sound Bite

Consumer lag is the single most important Kafka operational metric.

---

# 132. Why Use Manual Commit?

## Auto Commit

Can cause:

```text
Data Loss
```

---

## Manual Commit

Ensures:

```text
Process First
Commit Later
```

---

### Sound Bite

Manual commits trade duplicate processing risk for stronger reliability guarantees.

---

# 133. Why Do Consumers Need To Be Idempotent?

Kafka provides:

```text
At Least Once Delivery
```

---

Meaning:

```text
Duplicates Can Occur
```

---

Consumers must tolerate:

```text
Retries
Replay
Recovery
```

---

### Sound Bite

Producer idempotency protects Kafka. Consumer idempotency protects business systems.

---

# 134. What Is The Best Kafka Configuration?

## Producer

```properties
acks=all
enable.idempotence=true
retries=Integer.MAX_VALUE
```

---

## Topic

```properties
replication.factor=3
min.insync.replicas=2
```

---

## Consumer

```properties
enable.auto.commit=false
```

---

## Processing

```text
Manual Commit
Idempotent Consumer
Retries
DLQ
```

---

# 135. Explain Kafka Reliability In 60 Seconds

Kafka reliability comes from:

```text
Replication
ISR
Acknowledgments
Retries
Idempotency
Offset Management
```

Producers use:

```text
acks=all
```

to ensure durability.

Consumers use:

```text
Manual Commits
```

to avoid data loss.

DLQs isolate permanently failing messages.

Replay provides recovery capabilities.

---

# 136. Staff Engineer Discussion

## How Many Partitions Should We Create?

Depends on:

```text
Throughput
Future Growth
Consumer Count
Operational Complexity
```

---

Too Few:

```text
Limited Parallelism
```

---

Too Many:

```text
Operational Overhead
```

---

### Sound Bite

Partition count is both a throughput decision and a long-term operational decision.

---

# 137. Staff Engineer Discussion

## What Happens If Traffic Doubles?

Possible Actions:

```text
Increase Partitions
Increase Consumers
Optimize Processing
Scale Infrastructure
```

---

Need to determine:

```text
Producer Bottleneck?
Kafka Bottleneck?
Consumer Bottleneck?
Vendor Bottleneck?
```

---

# 138. Staff Engineer Discussion

## What Is Usually The Bottleneck?

Rarely Kafka.

Usually:

```text
Databases
External APIs
Vendor Systems
Business Logic
```

---

### My Screening Example

The slowest component was typically:

```text
Vendor Screening Systems
```

not Kafka itself.

---

# 139. VP-Level Discussion

## Why Is Kafka Strategically Important?

Kafka enables:

```text
Loose Coupling
Event-Driven Architecture
Independent Team Scaling
Operational Resilience
```

---

Organizations use Kafka because:

```text
Systems Evolve
Requirements Change
Consumers Multiply
```

---

Kafka allows those changes without rewriting upstream systems.

---

# 140. VP-Level Discussion

## What Business Value Does Kafka Deliver?

Technical Benefits:

```text
Scalability
Reliability
Replayability
```

---

Business Benefits:

```text
Faster Delivery
Lower Operational Risk
Better Recovery
Regulatory Compliance
```

---

### Executive Sound Bite

Kafka transforms data into a reusable enterprise asset rather than a one-time integration.

---

# My Executive Screening Platform Narrative

## Situation

Needed to process:

```text
50+ Million Client Records
```

for:

```text
Sanctions
PEP
Adverse Media
```

screening.

---

## Challenges

```text
Scale
Reliability
Ordering
Replayability
```

---

## Solution

```text
Spring Batch Partitioning
       ↓
Kafka
       ↓
10 Partitions
       ↓
10 Worker Services
       ↓
Vendor Screening Systems
       ↓
Actimize
```

---

## Design Decisions

Partition Key:

```text
clientId
```

to preserve ordering.

---

Reliability:

```text
acks=all
idempotence=true
manual commits
```

---

Failure Handling:

```text
Retries
DLQ
Replay
```

---

## Outcome

```text
40% Faster Processing

50M+ Records Scaled

Fault Tolerant

Replay Capable

Operationally Resilient
```

---

# Final Kafka Revision Sheet

```text
Topic
=
Logical Stream

Partition
=
Unit Of Parallelism

Consumer Group
=
Unit Of Scaling

Offset
=
Bookmark

Ordering
=
Within Partition Only

Replay
=
Possible

DLQ
=
Failed Records

Lag
=
Health Metric

Best Producer
=
acks=all + idempotence

Best Consumer
=
Manual Commit + Idempotency

Maximum Parallelism
=
Number Of Partitions

Most Important Metric
=
Consumer Lag
```

---

# End Of Kafka Handbook

# From Kafka Concepts To Real Production Systems

Everything covered so far:

- Topics
- Partitions
- Consumer Groups
- DLQ
- Replay
- Idempotency
- Reliability

was applied directly in the Financial Crimes screening platform described in the next chapters.

The following chapters demonstrate how these Kafka concepts were used to process 50M+ client records, integrate with RDC and Bridger, generate 35K monthly alerts, and support regulatory screening workloads.

# PART XII – ENTERPRISE SCREENING ARCHITECTURE

# PAGE 13

---

# 123. Financial Crimes Technology Overview

## Purpose

Financial Crimes Technology systems help financial institutions comply with regulatory requirements by screening clients, transactions, and counterparties against various risk datasets.

Primary objectives:

* Prevent onboarding sanctioned entities
* Identify Politically Exposed Persons (PEPs)
* Detect adverse media risk
* Support Anti-Money Laundering (AML) investigations
* Maintain regulatory compliance

---

# 124. Screening Types

## Sanctions Screening

Compares clients against government watchlists:

* OFAC
* HMT
* EU
* UN

Purpose:

```text
Prevent prohibited entities from entering the financial system.
```

---

## PEP Screening

Identifies:

```text
Politically Exposed Persons
```

Examples:

* Government officials
* Military leaders
* State-owned enterprise executives

Purpose:

```text
Enhanced Due Diligence (EDD)
```

---

## Adverse Media Screening

Searches news and public sources for:

* Fraud
* Corruption
* Terrorism
* Human trafficking
* Financial crime

Purpose:

```text
Identify reputational and financial crime risk.
```

---

# 125. Screening Lifecycle

```text
Client Onboarding
       ↓
Client Screening
       ↓
Potential Match
       ↓
Alert Generation
       ↓
Actimize Case Creation
       ↓
Investigator Review
       ↓
Disposition
```

---

# 126. NNS Screening Platform

NNS serves as the centralized screening platform supporting:

* Wealth Management
* E*TRADE
* Institutional Securities Group
* Corporate Clients

Capabilities:

```text
Sanctions Screening
PEP Screening
Adverse Media Screening
Private List Screening
```

---

# 127. RDC Integration

RDC provides:

```text
PEP Data
Adverse Media Data
Risk Intelligence
```

Flow:

```text
Client Population
       ↓
NNS
       ↓
RDC
       ↓
Match Results
       ↓
Actimize
```

---

# 128. Bridger Integration

Bridger provides:

```text
Sanctions Screening
Watchlist Matching
Entity Resolution
```

Supports:

```text
OFAC
HMT
EU
UN
```

---

# 129. Actimize Integration

Actimize serves as:

```text
Alert Repository
Case Management Platform
Investigation Platform
```

Flow:

```text
Screening Match
       ↓
Actimize Alert
       ↓
Investigator Review
       ↓
Disposition
```

---

# 130. Alert Generation

Monthly Volume:

```text
~35,000 Alerts
```

Generated from:

```text
Sanctions Matches
PEP Matches
Adverse Media Matches
```

---

# 131. False Positive Reduction

One major initiative involved:

```text
65,185 Inactive Screening Records
```

being identified and removed after diagnosing legacy E*TRADE scoping defects.

Benefits:

* Reduced operational workload
* Reduced false positives
* Increased investigator capacity

---

# 132. Documentation Initiative

Created first comprehensive documentation covering:

```text
NNS Screening Filters
Business Scoping Logic
Party Role Logic
Portfolio Coverage
```

Supported:

```text
Wealth
E*TRADE
ISG
```

Result:

Reduced dependency on tribal knowledge.

---

# 133. AI Documentation Chatbot

Created AI-powered documentation assistant capable of answering:

```text
Screening Logic Questions
Filter Logic Questions
System Design Questions
Business Rule Questions
```

Benefits:

* Faster onboarding
* Reduced SME dependency
* Improved engineering productivity

Recognition:

```text
Global Technology Expo

Town Hall Recognition
```

---

# End of Page 13

# PART XIII – MY 50M CLIENT SCREENING PLATFORM

# PAGE 14

---

# 134. Business Problem

Need to process:

```text
50+ Million Client Records
```

for:

* Sanctions Screening
* PEP Screening
* Adverse Media Screening

Requirements:

```text
Scalable
Fault Tolerant
Replayable
Ordered
```

---

# 135. High-Level Architecture

```text
DB2 Client Repository
         ↓
Spring Batch
(ClientIdRangePartitioner)
         ↓
Kafka Topic
(screening.jobs)
         ↓
10 Kafka Partitions
         ↓
Consumer Group
(screening-workers)
         ↓
RDC / Bridger
         ↓
MQ
         ↓
Actimize
```

---

# 136. ClientIdRangePartitioner

Partitioned:

```text
50M Records
```

into:

```text
5 Partitions
```

Each processing:

```text
10M Records
```

Benefits:

```text
Parallelism
Scalability
Isolation
```

---

# 137. JdbcPagingItemReader

Configuration:

```java
pageSize = 10000
chunkSize = 1000
```

Benefits:

```text
Low Memory Usage
Stable Throughput
Large Dataset Support
```

---

# 138. Kafka Design

Topic:

```text
screening.jobs
```

Partitions:

```text
10
```

Replication:

```text
3
```

Producer Configuration:

```properties
acks=all
enable.idempotence=true
```

---

# 139. Partition Key

```text
clientId
```

Reason:

```text
Maintain Ordering
For Client Events
```

---

# 140. Consumer Architecture

Consumer Group:

```text
screening-workers
```

Workers:

```text
10
```

Parallelism:

```text
10 Partitions
10 Workers
```

---

# 141. Reliability

Producer:

```text
acks=all
idempotence=true
```

Consumer:

```text
manual commit
```

Result:

```text
No Data Loss
Replay Support
```

---

# 142. Failure Recovery

Worker Crash:

```text
Consumer Rebalance
```

Processing resumes from:

```text
Last Committed Offset
```

No manual intervention required.

---

# 143. Replay Strategy

Used for:

```text
Watchlist Changes
Rule Changes
Vendor Changes
```

Approach:

```text
New Consumer Group
Replay Historical Events
```

---

# 144. Vendor Migration

Led planning for:

```text
Bridger Migration
```

Activities:

* Architecture planning
* Data mapping
* Interface definition
* Risk assessment

---

# 145. 1M Record Vendor POC

Worked with:

```text
LexisNexis
```

to execute:

```text
1 Million Record Validation
```

Objectives:

```text
Accuracy
Performance
Coverage
Match Quality
```

---

# 146. EDD Transformation

Served as:

```text
Technical Lead
De Facto Project Manager
```

Responsibilities:

* API contracts
* Data contracts
* Process design
* End-to-end integration

Delivered:

```text
60%+
of required functionality
```

---

# 147. Observability

Metrics:

```text
Consumer Lag
Processing Time
Retry Counts
DLQ Volume
```

Tools:

```text
Grafana
Prometheus
```

---

# 148. Results

```text
50M+ Records Processed

40% Faster Processing

35K Monthly Alerts

8M Screening Population

Highly Available

Replay Capable
```

---

# 149. Interview Story (5 Minute Version)

Use this chapter as your complete architecture walkthrough.

---

# 150. Interview Story (2 Minute Version)

"I redesigned a large-scale screening platform that processes over 50 million client records through Spring Batch partitioning and Kafka-based distributed processing. By partitioning work, leveraging Kafka consumer groups, and implementing fault-tolerant replayable workflows, we improved throughput by approximately 40% while maintaining regulatory-grade reliability."

---

# End of Page 14

# PART XIV – STAFF ENGINEER & VP DISCUSSIONS

# PAGE 15

---

# 151. Architecture Leadership

Examples:

```text
NNS Architecture Direction

Bridger Migration

EDD Transformation

Postgres Adoption

API Modernization
```

---

# 152. Postgres Adoption Leadership

Championed:

```text
Postgres Adoption
```

within Financial Crimes Technology.

Contributions:

* Liquibase Design
* Access Model
* Environment Setup
* Approval Process

Result:

```text
Infrastructure Ready
Ahead Of Schedule
```

---

# 153. Team Enablement

Created:

```text
Kafka Documentation
Architecture Decision Records
API Specifications
Technical Guides
```

Benefits:

```text
Reduced Knowledge Silos
Improved Onboarding
```

---

# 154. Mentorship

Supported engineers through:

```text
Design Reviews
Architecture Discussions
Production Troubleshooting
Knowledge Transfer
```

---

# 155. AI Innovation

Developed:

```text
AI Documentation Chatbot
```

Capabilities:

```text
Answer Screening Questions
Answer Architecture Questions
Answer Business Logic Questions
```

Impact:

```text
Reduced SME Dependency
Accelerated Onboarding
```

---

# 156. Promotion Narrative

Focus areas:

```text
Scale
Influence
Execution
Technical Leadership
Business Impact
```

---

# 157. Business Impact Examples

### False Positive Reduction

```text
65,185 Inactive Records Removed
```

### Alert Processing

```text
35,000 Alerts Monthly
```

### Screening Population

```text
8 Million Clients
```

### Vendor Validation

```text
1M Record POC
```

---

# 158. Staff Engineer Discussion Questions

### How many partitions should a topic have?

Depends on:

```text
Throughput
Growth
Consumer Count
Operational Complexity
```

---

### How do you replay 1B events?

```text
New Consumer Group

Dedicated Replay Pipeline

Idempotent Consumers
```

---

### How do you design for reliability?

```text
Replication

Idempotency

Manual Commits

DLQ

Replay
```

---

# 159. Executive Narrative

"My primary focus has been scaling and modernizing Financial Crimes screening platforms that support sanctions, PEP, and adverse media screening across millions of client records. Beyond implementation, I have driven architecture decisions, vendor migrations, documentation standards, onboarding improvements, AI-driven knowledge management, and operational efficiency initiatives that directly reduced risk and increased platform scalability."

---

# 160. Key Leadership Examples

```text
AI Documentation Chatbot

NNS Documentation Initiative

Bridger Migration

Postgres Adoption

EDD Transformation

1M Record Vendor POC

65K Record Cleanup

Team Mentorship

Architecture Leadership
```

---

# End of Page 15

# End Of Enterprise Screening Architecture Section
