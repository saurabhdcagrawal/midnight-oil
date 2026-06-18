	# System Design Interview Playbook

	## 1. Interview Framework

	### 1.1 Requirement Gathering
	- Functional requirements
	- Non-functional requirements
	- Scope clarification

	### 1.2 Capacity Estimation
	- Users
	- QPS
	- Read/Write ratio
	- Storage estimation
	- Bandwidth

	### 1.3 API Design

	### 1.4 Data Modeling

	### 1.5 High-Level Architecture

	### 1.6 Scaling Strategy
	- Horizontal scaling
	- Load balancing
	- Partitioning/sharding
	- Replication

	### 1.7 Reliability & Resilience
	- Retries
	- Timeouts
	- Circuit breakers
	- Backpressure
	- DLQ
	- Idempotency

	### 1.8 Observability
	- Metrics
	- Logging
	- Distributed tracing
	- Alerting

	### 1.9 Trade-offs
	- CAP theorem
	- Consistency models
	- Latency vs durability
	- Availability vs correctness


	---

	## 2. Common Architecture Building Blocks

	### 2.1 API Gateway

	### 2.2 Load Balancer

	### 2.3 Caching
	- Cache-aside
	- Write-through
	- Write-behind
	- Cache invalidation
	- Cache eviction

	### 2.4 Database Patterns
	- SQL vs NoSQL
	- Indexing
	- Partitioning
	- Replication

	### 2.5 Asynchronous Processing
	- Message queues
	- Kafka
	- Event-driven architecture
	- Consumer groups
	- Ordering
	- Retry strategy
	- Dead Letter Queue

	### 2.6 Distributed Coordination
	- Locks
	- Optimistic locking
	- Versioning
	- Leader election

	### 2.7 Rate Limiting & Traffic Control
	- Token bucket
	- Leaky bucket
	- Fixed window
	- Sliding window

	### 2.8 Concurrency Control
	- Thread pools
	- Semaphores
	- Bounded queues
	- Backpressure

	### 2.9 Security
	- Authentication
	- Authorization
	- Encryption
	- Secrets management
	- Auditing


	---

	## 3. High-Level System Design Case Studies

	### 3.1 URL Shortener

	### 3.2 Notification System

	### 3.3 Fraud Monitoring System

	### 3.4 Financial Aggregation Platform (Plaid / QuickBooks)

	### 3.5 Audiobook Progress Synchronization

	#### Quick Revision

	Problem:
	Sync audiobook position.

	Core Data:
	(userId, bookId) → position

	Scale Reads:
	Redis.

	Scale Writes:
	Kafka.

	Conflict:
	Versioning.

	Partition:
	userId.

	Source of truth:
	Database.


	#### Key Bottlenecks

	Reads:
	Millions of devices requesting progress.

	Solution:
	Redis.

	---

	Writes:
	Millions of heartbeats.

	Solution:
	Kafka.

	---

	Consistency:
	Multiple devices update same book.

	Solution:
	Versioning / optimistic locking.

	#### Problem

	Design a system that allows users to synchronize audiobook playback progress across devices.

	Example:

	Phone pauses at 01:23:45

	Tablet opens app

	Resume from 01:23:45

	#### Part A — Interview Evolution Flow ⭐⭐⭐

	#### Functional Requirements

	- Update progress.
	- Retrieve latest progress.
	- Support multiple devices.
	- Near real-time synchronization.
	- Support millions of users.


	---

	#### Non-Functional Requirements

	- Low latency reads.
	- High write throughput.
	- High availability.
	- Durability.
	- Horizontal scalability.


	---

	#### Step 1: Start Simple

	Architecture:

	Device
	 |
	Load Balancer
	 |
	Progress Sync Service
	 |
	Database


	Data Model:

	Progress
	---------
	userId
	bookId
	position
	updatedAt
	version (optional)


	Primary Key:

	(userId, bookId)


	Access Pattern:

	GET progress by userId + bookId

	UPDATE progress by userId + bookId


	Database Choices:

	- DynamoDB
	- Cassandra
	- SQL with proper indexing


	Interview Note:

	Always start with the simplest working system.
	Do not introduce Redis or Kafka immediately.


	---

	#### Step 2: Scale Reads — Introduce Redis


	Problem:

	Millions of users may frequently request their latest playback position.

	Reading from the database for every request increases:
	- Latency.
	- Database load.


	Solution:

	Use Redis as a low-latency cache.


	Architecture:

	Device
	 |
	Progress Service
	 |
	Redis
	 |
	Database


	Redis Key:

	progress:user123:bookABC


	Cache Miss:

	Redis does not have the key due to:
	- TTL expiration.
	- Memory eviction.
	- Redis restart.

	Flow:

	Request
	 |
	Redis MISS
	 |
	Database
	 |
	Return data
	 |
	Populate Redis


	---

	#### Step 3: Keep Redis Updated


	Write-Through Style:

	The application manages both writes.


	Flow:

	Device
	 |
	Progress Service
	 |
	Database Update
	 |
	Redis Update
	 |
	Return Success


	Database is the source of truth.


	---

	#### Step 4: What If Redis Update Fails?


	Scenario:

	Database:
	1:23:45

	Redis:
	1:20:00 (stale)


	Option 1: Retry Redis update

	Retry 1
	Retry 2
	Retry 3


	Problem:

	A retry could overwrite a newer update.


	Example:

	Old retry:
	1:23:45

	New update:
	1:25:00


	If old retry executes later:

	Redis becomes:
	1:23:45 ❌


	Solution:

	Use:
	- Version numbers.
	- Timestamps.


	Option 2: Invalidate only that key


	DELETE progress:user123:bookABC


	Next read:

	Redis MISS
	 |
	Database
	 |
	Rebuild Redis


	---

	#### Step 5: Scale Writes — Introduce Kafka


	Problem:

	Millions of devices sending heartbeat updates can create huge write traffic.


	Architecture:


	Device
	 |
	Progress Service
	 |
	Kafka
	 |
	Consumers
	 |
	Database


	Benefits:

	- Absorbs traffic spikes.
	- Provides buffering.
	- Allows asynchronous processing.
	- Handles backpressure.
	- Supports retries.


	---

	#### Step 6: Hybrid Large Scale Design


	Progress Update
		   |
		   v
	Progress Service
		   |
	----------------------
	|                    |
	v                    v
	Redis            Kafka
	|                    |
	Fast Reads       Consumers
						  |
						  v
					  Database


	Redis:

	- Stores latest playback state.
	- Provides low-latency reads.


	Kafka:

	- Handles high-frequency heartbeat writes.
	- Smooths traffic spikes.
	- Allows asynchronous persistence.


	---

	#### Step 7: Multiple Device Conflict (L6 Deep Dive)


	Problem:

	Phone:
	1:30:00


	Tablet:
	1:25:00


	Both send updates.


	Possible strategies:


	1. Last Write Wins

	Use timestamps.

	Problem:
	Device clocks can be incorrect.


	2. Server Generated Version

	Server maintains:

	version = 100

	Next successful update:

	version = 101

	Why server generated?

	Multiple devices may send concurrent updates.
	Devices cannot reliably generate globally ordered versions.

	Benefits:

	- Detect stale updates.
	- Prevent old retries from overwriting newer state.


	3. Optimistic Locking


	UPDATE progress
	SET position=?, version=version+1
	WHERE userId=?
	AND bookId=?
	AND version=?


	If no rows are updated:
	- Another update happened.
	- Reload latest state.
	- Apply conflict resolution.


	---

	#### Reliability

	Discuss:

	- Retry with exponential backoff.
	- Idempotency.
	- Duplicate requests.
	- Cache invalidation.
	- DLQ.
	- Monitoring Kafka lag.
	- API latency.
	- Cache hit ratio.


	---

	#### Partitioning


	Partition by userId.


	Reason:

	Most access patterns are:

	(userId, bookId)


	This keeps a user's data together and distributes load.


	---

	#### L6 Interview Flow


	Do NOT start with everything.


	Evolve the design:


	Simple Database
			|
			v
	Add Redis for read scaling
			|
			v
	Handle cache failures
			|
			v
	Add Kafka for massive writes
			|
			v
	Handle multi-device conflicts
			|
			v
	Versioning / Optimistic Locking


	---

	#### Key L6 Soundbite


	"I would start with a simple progress service backed by a database. As read traffic grows, I would add Redis for low-latency access. If heartbeat writes become very high, I would introduce Kafka to absorb write spikes and process them asynchronously. Finally, I would handle multi-device conflicts using server-controlled versions and optimistic locking."

					Device
					   |
				 Load Balancer
					   |
			  Progress Sync Service
				   /          \
				  /            \
			   Redis          Kafka
				 |              |
		 Low latency reads  Consumers
								 |
							  Database

	Read Path:
	Device → Service → Redis → Database on miss

	Write Path:
	Device → Service → Redis (latest state) + Kafka → Consumer → Database

	### Part B — Detailed Design Reference

	#### 1. Problem Statement

	#### 2. Functional Requirements

	#### Core Features

	* Update audiobook playback progress.
	* Retrieve latest playback progress.
	* Synchronize progress across multiple devices.
	* Support near real-time synchronization.
	* Handle millions of listeners.

	---

	#### 3. Non-Functional Requirements

	* Low latency reads.
	* High write throughput due to frequent heartbeat updates.
	* High availability.
	* Durable storage of playback progress.
	* Horizontal scalability.
	* Reliable handling of failures.

	---

	#### 4. Capacity Estimation

	Example assumptions:

	* 100 million active users.
	* A heartbeat every 5 seconds.

	Theoretical write volume:

	```
	100M users / 5 seconds = 20M updates per second
	```

	This may be too high, so clients can optimize by:

	* Sending heartbeat every 15–30 seconds.
	* Sending immediate updates when:

	  * User pauses.
	  * Application moves to background.
	  * User closes the app.
	  * Chapter changes.

	---

	#### 5. API Design

	####
	```
	POST /progress
	```

	Request:

	```json
	{
	  "userId": "123",
	  "bookId": "ABC",
	  "deviceId": "PHONE",
	  "position": "01:23:45"
	}
	```

	---

	#### Get Latest Progress

	```
	GET /progress?userId=123&bookId=ABC
	```

	Response:

	```json
	{
	  "position": "01:23:45",
	  "updatedAt": "2026-01-01T10:30:00"
	}
	```

	---

	#### 6. Data Model

	Access pattern:

	* Retrieve progress by `userId + bookId`.
	* Update progress by `userId + bookId`.

	Progress table:

	```
	Progress
	---------
	userId
	bookId
	position
	updatedAt
	version (optional)
	```

	Primary key:

	```
	(userId, bookId)
	```

	Possible storage choices:

	* DynamoDB
	* Cassandra
	* Relational database with proper indexing

	---

	#### 7. Initial Simple Architecture

	Start with the simplest design:

	```
	Device
	   |
	Load Balancer
	   |
	Progress Sync Service
	   |
	Database
	```

	Flow:

	1. Device sends current playback position.
	2. Service updates database.
	3. Another device requests latest position.
	4. Service reads database and returns the value.

	Always start the interview with the simplest working design.

	---

	#### 8. Scale Reads with Redis Cache

	Problem:

	Millions of devices frequently request the latest playback position. Reading the database for every request increases latency and database load.

	Add Redis:

	```
	Device
	   |
	Progress Sync Service
	   |
	Redis Cache
	   |
	Database
	```

	Redis stores:

	```
	Key:
	progress:user123:bookABC

	Value:
	{
	  position: 01:23:45,
	  updatedAt: timestamp
	}
	```

	#### Cache Miss

	A cache miss occurs when Redis does not have the key because of:

	* Expiration (TTL).
	* Eviction due to memory pressure.
	* Redis restart/failure.

	Flow:

	```
	Request
	   |
	Redis MISS
	   |
	Database
	   |
	Return data
	   |
	Populate Redis
	```

	---

	#### 9. Keeping Cache Updated (Write-Through)

	The application manages both database and cache writes.

	Flow:

	```
	Device
	   |
	Progress Sync Service
	   |
	Database Update
	   |
	Redis Update
	   |
	Return Success
	```

	Database remains the source of truth.

	---

	#### 10. Cache Update Failure Handling

	Scenario:

	```
	Database update succeeds.
	Redis update fails.
	```

	Database:

	```
	1:23:45
	```

	Redis:

	```
	1:20:00 (stale)
	```

	Solutions:

	#### Retry Redis Update

	Useful for temporary failures.

	Example:

	```
	Retry 1
	Retry 2
	Retry 3
	```

	#### Invalidate Only the Affected Cache Key

	Example:

	```
	DELETE progress:user123:bookABC
	```

	The next read will cause a cache miss and reload the latest value from the database.

	---

	#### 11. Scale Writes with Kafka

	Problem:

	Millions of devices sending heartbeat updates can create very high write throughput.

	Introduce Kafka:

	```
	Device
	   |
	Progress Sync Service
	   |
	Kafka
	   |
	Consumers
	   |
	Database
	```

	Benefits:

	* Handles traffic spikes.
	* Decouples producers from consumers.
	* Provides buffering and backpressure.
	* Supports retries and asynchronous processing.

	---

	#### 12. Hybrid Large-Scale Architecture

	For very large systems:

	```
					   Progress Update
							  |
							  v
					 Progress Sync Service
							  |
				------------------------------
				|                            |
				v                            v
	   Redis (Latest State)               Kafka
				|                            |
		 Low Latency Reads                  |
											 |
									  Persistence Consumer
											 |
											 v
										 Database
	```

	Redis provides immediate access to the latest playback position.

	Kafka handles massive write throughput and asynchronously persists updates.

	---

	#### 13. Multiple Device Synchronization (L6 Discussion)

	Multiple devices may update the same audiobook:

	Example:

	```
	Phone:
	1:30:00

	Tablet:
	1:25:00
	```

	Possible conflict resolution strategies:

	#### Last Write Wins

	Use timestamps.

	The latest update replaces the older one.

	Limitation:

	* Device clocks may not be reliable.

	---

	#### Server-Generated Version Numbers

	Maintain:

	```
	version
	```

	The server increments the version for each successful update.

	Benefits:

	* Detect stale updates.
	* Prevent old retries from overwriting newer progress.

	---

	#### Optimistic Locking

	Example:

	```sql
	UPDATE progress
	SET position = ?, version = version + 1
	WHERE userId = ?
	AND bookId = ?
	AND version = ?
	```

	If no rows are updated, another request modified the record and the conflict must be handled.

	---

	#### 14. Reliability Considerations

	Discuss:

	* Retry with exponential backoff.
	* Idempotent updates.
	* Handling duplicate requests.
	* Cache invalidation.
	* Dead Letter Queue (DLQ) for failed events.
	* Monitoring Kafka consumer lag.
	* API latency metrics.
	* Cache hit ratio.

	---

	#### 15. Partitioning Strategy

	Partition by:

	```
	userId
	```

	Reason:

	Most access patterns are:

	```
	(userId, bookId)
	```

	This distributes users across partitions and keeps related data together.

	---

	#### 16. Interview Evolution Strategy

	Do not start with Kafka, Redis, versioning, and concurrency.

	Evolve the design:

	```
	1. Simple Database Design
				|
				v
	2. Add Redis for Low-Latency Reads
				|
				v
	3. Handle Cache Failures
				|
				v
	4. Add Kafka for Massive Write Throughput
				|
				v
	5. Discuss Multi-Device Conflicts
				|
				v
	6. Add Versioning and Optimistic Locking
	```

	---

	#### L6 Interview Soundbite

	"I would start with a simple Progress Sync Service backed by a database. As read traffic grows, I would introduce Redis to provide low-latency access to the latest playback position. If heartbeat writes become very high, I would introduce Kafka to absorb the write stream and asynchronously persist updates. Finally, I would handle multiple device updates using conflict resolution techniques such as server-generated versions and optimistic locking."

	### 3.6 Content Delivery & Audio Streaming

	### 3.7 Recommendation System

	### 3.8 Chat System

	### 3.9 News Feed System

	### 3.10 Search System

	### 3.11 Distributed Cache

	### 3.12 Rate Limiter

	### 3.13 Ride Sharing System


	---

	## 4. Design Evolution Patterns

	### 4.1 Start Simple
	- Single service
	- Single database

	### 4.2 Scale Reads
	- Add caching
	- Add replicas
	- Use CDN

	### 4.3 Scale Writes
	- Introduce Kafka
	- Batch processing
	- Asynchronous workflows

	### 4.4 Improve Reliability
	- Retry
	- Timeouts
	- Circuit breakers
	- Idempotency

	### 4.5 Handle Distributed Data Challenges
	- Eventual consistency
	- Conflict resolution
	- Versioning
	- Optimistic locking


	---

	## 5. L6 Deep Dive Questions

	### Caching
	- Why do we need cache?
	- What is the source of truth?
	- How is cache populated?
	- What happens on cache miss?
	- What happens when cache update fails?

	### Kafka / Async Processing
	- Why use Kafka?
	- How do you handle retries?
	- How do you ensure ordering?
	- How do you handle duplicate messages?
	- What happens if consumers are slower than producers?

	### Database
	- Why SQL vs NoSQL?
	- What is the partition key?
	- How do you handle hot partitions?
	- How do you scale reads and writes?

	### Consistency
	- What happens when two clients update the same data?
	- How do you handle conflicts?
	- Should we use timestamps or versions?

	### Reliability
	- What happens when a dependency fails?
	- How do you prevent cascading failures?
	- How do you handle traffic spikes?

	### Operations
	- What metrics would you monitor?
	- What would trigger an alert?
	- How would you debug production failures?

	---


# Scale-Up System Design: Appointment Scheduling Service (1K RPS → 10K RPS)

# Interview Question

You are responsible for an Appointment Scheduling Service.

Current traffic is:

```text
1,000 requests/sec
```

Business forecasts indicate traffic will grow to:

```text
10,000 requests/sec
```

The service must maintain:

```text
Same SLA
Same latency requirements
No double booking
High availability
```

Current architecture:

```text
Client
   |
   v
Appointment Service
   |
   +---- Provider Availability Service
   |
   +---- Insurance Verification Service
   |
   +---- Database
   |
   +---- Cache
```

Known production issues:

```text
1. Database connection pools get exhausted

2. Cache occasionally causes double booking

3. Provider availability service retries create spikes

4. Insurance verification failures cascade into
   appointment scheduling failures

5. Traffic expected to increase by 10x
```

How would you scale the system?

---

# Understanding The Problem

This is not a greenfield system design problem.

The system already exists.

The challenge is:

```text
1,000 RPS
        ↓
10,000 RPS
```

while maintaining:

```text
Correctness
Latency
Availability
```

The most important business invariant is:

```text
A provider slot must never be double booked.
```

Therefore:

```text
Correctness > Performance
```

Cache can improve performance.

Database must guarantee correctness.

---

# Challenge #1 - Application Layer Saturation

Current deployment:

```text
3 application instances
```

At 10x traffic:

```text
CPU utilization increases
Thread pools become exhausted
Connection pools become exhausted
```

Symptoms:

```text
Higher latency
Request timeouts
Reduced throughput
```

---

## Solution

Scale horizontally.

```text
3 instances
      ↓
10-15 instances
```

behind a load balancer.

Because the application is stateless:

```text
Request
   |
   v
Any Instance
```

can process the request.

This allows throughput to scale linearly.

---

# Challenge #2 - Database Bottleneck

Current flow:

```text
Read Provider Slot
       |
       v
Create Booking
       |
       v
Update Availability
```

At 10x traffic:

```text
Database CPU increases
IOPS increases
Locks increase
Connection exhaustion occurs
```

Simply adding more application instances will not help if the database becomes the bottleneck.

---

## Solution - Read Replicas

Separate reads from writes.

Architecture:

```text
               Primary
                  |
      ------------------------
      |                      |
      v                      v
  Replica-1              Replica-2
```

---

### Read Traffic

```text
Check Availability
Provider Search
View Schedule
Appointment Lookup
```

goes to replicas.

---

### Write Traffic

```text
Create Booking
Cancel Booking
Reschedule Booking
```

goes to primary.

This significantly reduces load on the write database.

---

# Challenge #3 - Database Connection Pool Exhaustion

Every request requires:

```text
Application Thread
        |
        v
Database Connection
```

At 10,000 RPS:

```text
Connection pool becomes exhausted.
```

New requests wait for connections.

Latency increases dramatically.

---

## Solution

Increase:

```text
Connection Pool Size
```

based on database capacity.

Monitor:

```text
Pool utilization
Wait time
Timeout count
```

Additionally:

```text
Scale application instances
```

so database load is distributed.

---

# Challenge #4 - Double Booking

This is the most important problem.

Example:

```text
Provider Slot:
10:00 AM
```

Two users:

```text
User A
User B
```

arrive simultaneously.

---

## Without Protection

```text
A reads slot available

B reads slot available

A books slot

B books slot
```

Result:

```text
Double Booking
```

---

# Why Cache Can Make It Worse

Suppose cache contains:

```text
Slot Available
```

User A books successfully.

Database updated.

Cache update delayed.

User B reads stale cache.

System incorrectly believes:

```text
Slot Available
```

Result:

```text
Duplicate Reservation
```

---

# Correct Solution

Database must be source of truth.

Cache is optimization only.

Never rely on cache for booking correctness.

---

## Enforce Uniqueness

Example:

```sql
UNIQUE(provider_id, slot_id)
```

or

```sql
UNIQUE(provider_id, appointment_time)
```

Now:

```text
First booking succeeds
Second booking fails
```

regardless of cache state.

---

## Idempotency

Use:

```text
provider_id
appointment_slot
location_id
```

to generate an idempotency key.

Example:

```text
provider123_10am_nyc
```

If duplicate requests arrive:

```text
Return existing booking
```

instead of creating a second booking.

---

## Cache Strategy

Write database first.

```text
Request
   |
   v
Database Commit
   |
   v
Cache Update
```

Database guarantees correctness.

Cache accelerates reads.

---

# Challenge #5 - Provider Availability Retries

Provider Availability Service occasionally times out.

Current behavior:

```text
Retry
Retry
Retry
```

At 10,000 RPS:

```text
10,000 requests

becomes

30,000+ requests
```

due to retries.

This creates:

```text
Retry Storm
```

---

# Retry Storm Effects

```text
Higher latency
More failures
More retries
Even higher latency
```

Positive feedback loop.

---

# Solution

Limit retries.

Example:

```text
Maximum Retries = 3
```

Use:

```text
Exponential Backoff
```

Example:

```text
Retry 1 = 100 ms

Retry 2 = 250 ms

Retry 3 = 700 ms
```

Add:

```text
Jitter
```

to randomize retry timing.

This prevents synchronized spikes.

---

# Challenge #6 - Cascading Failures

Current flow:

```text
Appointment Service
        |
        v
Insurance Verification Service
```

Suppose Insurance Service becomes slow.

Appointment threads wait.

Eventually:

```text
Thread pool exhausted
```

Then:

```text
Queue builds
```

Then:

```text
Requests timeout
```

Eventually:

```text
Entire appointment service fails
```

even though appointment service itself is healthy.

---

# Solution - Circuit Breaker

Circuit Breaker States:

```text
Closed
Open
Half Open
```

---

### Closed

Normal traffic.

```text
Appointment
      |
      v
Insurance
```

---

### Open

Failures exceed threshold.

```text
Appointment
      |
      X
Insurance
```

Requests fail immediately.

No waiting.

No thread exhaustion.

---

### Half Open

Small number of requests allowed.

If successful:

```text
Close Circuit
```

Otherwise:

```text
Open Circuit Again
```

---

## Benefits

```text
Fail Fast
Protect Appointment Service
Reduce load on Insurance Service
Allow recovery
```

---

# Challenge #7 - Thread Pool Exhaustion

Every request consumes:

```text
Application Thread
```

Slow downstream services cause:

```text
Blocked Threads
```

Eventually:

```text
No threads available
```

System stops serving requests.

---

# Solution

Use:

```text
Bounded Thread Pool
```

Example:

```text
30 worker threads
```

and:

```text
Queue Size = 10,000
```

The queue provides:

```text
Buffering
Backpressure
Controlled resource usage
```

If queue exceeds capacity:

```text
Return HTTP 429
```

rather than crashing the service.

---

# Challenge #8 - Timeouts

Never allow requests to wait forever.

Every dependency should have:

```text
Connection Timeout
Read Timeout
```

Example:

```text
500 ms
```

Benefits:

```text
Fail Fast
Prevent Resource Exhaustion
Protect Thread Pools
```

---

# Challenge #9 - Observability

Before scaling, measure.

Monitor:

```text
Throughput
P50 Latency
P95 Latency
P99 Latency
Error Rate
Retry Count
Circuit Breaker State
```

Infrastructure metrics:

```text
CPU
Memory
Connection Pool Usage
Database Utilization
Cache Hit Rate
```

Without observability:

```text
Cannot identify bottlenecks
Cannot validate scaling improvements
```

---

# Load Testing

Before production rollout:

```text
Simulate 10,000 requests/sec
```

Validate:

```text
P99 latency
Database throughput
Connection pool behavior
Retry behavior
Circuit breaker activation
```

Tune system based on results.

---

# Final Architecture

```text
Client
   |
   v
Load Balancer
   |
   v
Appointment Service (10+ Instances)
   |
   +--------------------------+
   |                          |
   v                          v
Provider Service      Insurance Service
                              |
                       Circuit Breaker
   |
   v
Primary Database
   |
   +-----------+
   |           |
   v           v
Replica-1   Replica-2

Cache

Retries + Exponential Backoff + Jitter

Observability + Monitoring
```

---

# Interview Summary

A strong answer should emphasize:

```text
1. Horizontal scaling of application tier

2. Read replicas for database scaling

3. Connection pool management

4. Database as source of truth

5. Unique constraints and idempotency
   to prevent double booking

6. Retry limits with backoff and jitter

7. Circuit breakers to prevent cascading failures

8. Bounded resources and backpressure

9. Timeouts

10. Observability and load testing
```

The most important takeaway is:

```text
Appointment Scheduling is fundamentally
a correctness and concurrency problem.

The database must guarantee correctness.

Everything else exists to improve
performance and resiliency.
```
# Additional Deep Dive: Database Thread Pool Exhaustion at 1000 RPS

## Important Observation

The original proposal suggested:

```text
Primary Database
+
Read Replica
```

However, the problem statement explicitly states:

```text
Database thread pools are already exhausting
at 1000 requests/sec.
```

This changes the discussion significantly.

The problem is no longer:

```text
How do we scale from 1000 RPS to 10000 RPS?
```

The problem becomes:

```text
Why is the database already struggling
at current traffic?
```

Before adding infrastructure, we must identify the bottleneck.

---

# Potential Root Causes

## 1. Too Many Database Calls Per Request

Example:

```text
Check Provider
Check Schedule
Check Slot
Insert Booking
Update Availability
Create Audit Record
```

Total:

```text
6 database operations/request
```

At:

```text
1000 RPS
```

This becomes:

```text
6000 DB operations/sec
```

At:

```text
10000 RPS
```

This becomes:

```text
60000 DB operations/sec
```

Simply adding application instances will not solve this.

---

## Solution

Reduce database round trips.

Example:

Instead of:

```text
Read Provider
Read Schedule
Read Slot
Insert Booking
Update Slot
```

Use:

```text
Single Transaction
Stored Procedure
Optimized Query
```

Goal:

```text
5 DB calls
      ↓
2 DB calls
```

Reducing database round trips directly reduces connection usage and database load.

---

# 2. Slow Queries

Database pool exhaustion is often caused by slow queries rather than insufficient pool size.

Suppose:

```text
Query Time = 20 ms
```

At:

```text
1000 RPS
```

Approximate concurrent connections:

```text
20
```

Now suppose:

```text
Query Time = 200 ms
```

At:

```text
1000 RPS
```

Approximate concurrent connections:

```text
200
```

The database pool may become exhausted even though traffic has not increased.

---

## Solution

Review:

```text
Execution Plans
Indexes
Slow Query Logs
```

Identify:

```text
Table Scans
Missing Indexes
Inefficient Joins
```

Optimize the slowest queries first.

This often provides larger gains than adding hardware.

---

# 3. Reads Still Hitting Primary

Even with a read replica, traffic may still be hitting the primary database.

Example:

```text
Check Availability
Provider Search
Schedule Lookup
```

may still be routed to primary.

This creates unnecessary pressure on the write database.

---

## Solution

Ensure all read-only operations are routed to replicas.

Example:

```text
Availability Search
Provider Lookup
Appointment Search
```

↓

```text
Read Replica
```

while:

```text
Create Booking
Cancel Booking
Reschedule Booking
```

↓

```text
Primary Database
```

---

# 4. Cache More Aggressively

Most appointment systems are heavily read dominated.

Example:

```text
95% Availability Checks
5% Booking Requests
```

Users repeatedly search for available appointments.

Those requests often generate significant database traffic.

---

## Solution

Cache:

```text
Provider Availability
Provider Schedule
Location Availability
```

for a short TTL.

Example:

```text
30 seconds
60 seconds
```

This removes a large percentage of database reads.

---

## Important

Booking operations must still use the database.

Cache should never become the source of truth.

Correct flow:

```text
Availability Search
      ↓
Cache

Booking
      ↓
Database
```

---

# 5. Lock Contention

Appointment systems frequently experience lock contention.

Example:

```text
Provider A
10:00 AM Slot
```

Thousands of users attempt to reserve the same slot.

Typical flow:

```text
Read Slot
Update Slot
Commit
```

This creates contention.

---

## Solution

Use database-enforced correctness.

Examples:

```sql
UNIQUE(provider_id, appointment_time)
```

or

```text
Optimistic Locking
```

The database should prevent double booking.

Avoid holding locks for long periods.

---

# 6. Connection Leaks

Another common cause of pool exhaustion.

Symptoms:

```text
Pool Utilization Near 100%
Traffic Not Increasing
```

Connections are acquired but not released.

---

## Solution

Monitor:

```text
Connection Pool Usage
Connection Wait Time
Active Connections
Idle Connections
```

Verify connections are properly closed.

Use:

```text
Connection Leak Detection
```

available in most pool implementations.

---

# Capacity Planning Questions

Before scaling to 10000 RPS, I would ask:

```text
How many DB calls per request?

What is average query latency?

What is P95 query latency?

What percentage of traffic is read vs write?

How many active DB connections exist?

What is pool utilization?

Are reads already using replicas?

What are the slowest queries?
```

Without these answers, increasing infrastructure may simply move the bottleneck.

---

# Interview Takeaway

When the interviewer says:

```text
Database thread pools are already exhausting
at 1000 requests/sec.
```

A strong response is:

```text
Before scaling horizontally, I would identify
why the database is already saturated.

Potential causes include:

- Excessive database round trips
- Slow queries
- Missing indexes
- Reads hitting primary
- Lock contention
- Connection leaks

The goal is not simply to add more servers.
The goal is to reduce database pressure and
eliminate the existing bottleneck before
attempting a 10x scale increase.
```
# Production Incident: Duplicate Provider Bookings Due to Multi-Instance Deployment

# Interview Question

A healthcare appointment scheduling platform experienced duplicate provider bookings after a production hotfix was deployed.

Normally, a scheduling job is expected to run on only one application instance.

A hotfix bypassed staging validation and was deployed directly to production.

As a result:

```text
Scheduler Instance 1
Scheduler Instance 2
Scheduler Instance 3
```

all started processing the same provider availability records simultaneously.

This caused:

```text
Duplicate provider bookings
Duplicate notifications
Duplicate downstream processing
```

The interviewer asks:

```text
How would you prevent this from happening?
```

Expected topics:

```text
Distributed Locking
Idempotency
```

---

# Understanding The Problem

This is not a cache consistency problem.

This is not a database scaling problem.

This is a:

```text
Distributed Coordination Problem
```

Multiple application instances are performing work that should only be executed once.

---

# Original Design

Expected:

```text
Scheduler Service

Instance-1
```

Single scheduler processes:

```text
Provider A
10:00 AM Slot
```

one time.

---

# What Happened

After deployment:

```text
Scheduler Service

Instance-1
Instance-2
Instance-3
```

all became active.

Now all instances process:

```text
Provider A
10:00 AM Slot
```

simultaneously.

---

# Failure Sequence

Instance-1:

```text
Read Provider Slot
Create Booking
```

Instance-2:

```text
Read Provider Slot
Create Booking
```

Instance-3:

```text
Read Provider Slot
Create Booking
```

Result:

```text
Multiple bookings created
```

for the same provider slot.

---

# Why This Happens

Every instance believes:

```text
I am responsible for processing this slot.
```

There is no coordination mechanism.

Nothing prevents:

```text
Instance-1
Instance-2
Instance-3
```

from executing the same workflow.

---

# Solution #1 - Distributed Lock

Before processing a provider slot:

```text
Acquire Lock
```

Key:

```text
providerId:slotId
```

Example:

```text
provider123:10AM
```

Only one instance should be able to acquire the lock.

---

# Lock Acquisition Flow

Instance-1:

```text
Acquire Lock
```

Success.

```text
Process Booking
```

---

Instance-2:

```text
Acquire Lock
```

Fails.

```text
Do Not Process
```

---

Instance-3:

```text
Acquire Lock
```

Fails.

```text
Do Not Process
```

---

# Redis Implementation Example

```text
SET provider123:10AM
    instance1
    NX
    EX 30
```

Meaning:

```text
NX = Set Only If Key Does Not Exist

EX = Expire After 30 Seconds
```

If lock already exists:

```text
Another instance is processing.
```

---

# Architecture

```text
Instance-1
      |
Acquire Lock
      |
      v
 Process Booking
      |
 Release Lock
```

---

```text
Instance-2
      |
Acquire Lock
      |
      X
Lock Exists
      |
Exit
```

---

# Why Distributed Lock Helps

Distributed lock prevents:

```text
Concurrent Execution
```

of the same booking workflow.

Only one instance can process a provider slot at a given time.

---

# Why Distributed Lock Is Not Enough

This is the critical interview insight.

Consider:

```text
Instance-1
```

acquires lock.

Processes booking.

Crashes before lock cleanup.

Lock expires.

---

Later:

```text
Instance-2
```

acquires lock.

Processes same booking again.

Duplicate booking still occurs.

---

# Other Failure Scenarios

```text
Application Restart

Message Replay

Retry Logic

Duplicate Requests

Deployment Issues

Network Partition
```

All can result in:

```text
Same operation executed again
```

even when locks exist.

---

# Solution #2 - Idempotency

Idempotency guarantees:

```text
Executing the same operation multiple times
produces the same result.
```

---

# Idempotency Key

Generate:

```text
providerId
appointmentSlot
locationId
```

Example:

```text
provider123_10AM_NYC
```

This uniquely identifies:

```text
One logical booking request
```

---

# Database Design

```sql
Booking
-------
booking_id
provider_id
appointment_slot
location_id
idempotency_key UNIQUE
```

---

# Booking Flow

Instance-1:

```text
INSERT booking
```

Success.

---

Instance-2:

```text
INSERT booking
```

Same idempotency key.

Database returns:

```text
Already Exists
```

Return existing booking.

No duplicate booking created.

---

# Why Idempotency Helps

Even if:

```text
Retries happen

Messages replay

Instances restart

Deployments fail

Locks expire
```

the same booking request can never create multiple records.

---

# Distributed Lock vs Idempotency

These solve different problems.

---

## Distributed Lock

Prevents:

```text
Concurrent Processing
```

Example:

```text
Instance-1
Instance-2
Instance-3
```

trying to execute the same workflow simultaneously.

---

## Idempotency

Prevents:

```text
Duplicate Processing
```

even if execution happens multiple times over time.

Example:

```text
Retry

Replay

Crash Recovery

Deployment Bug
```

---

# Why We Need Both

Distributed Lock:

```text
Protects NOW
```

Idempotency:

```text
Protects FOREVER
```

Distributed Lock reduces:

```text
Database contention
Duplicate notifications
Duplicate external calls
```

Idempotency guarantees:

```text
Data correctness
```

---

# Final Architecture

```text
Scheduler Instance
        |
Acquire Distributed Lock
        |
        v
Generate Idempotency Key
        |
        v
Insert Booking
(Unique Constraint)
        |
        v
Success
```

---

# Interview Answer

A deployment bug allowed multiple scheduler instances to process the same provider slot concurrently. I would introduce a distributed lock keyed by providerId and slotId so only one instance can process a slot at a time. However, distributed locks alone are insufficient because retries, lock expiration, crashes, and replayed messages can still result in duplicate execution. Therefore I would also implement idempotency using a unique booking key persisted in the database. The distributed lock prevents concurrent processing, while idempotency guarantees correctness even if the same operation is executed multiple times.

# Database Connection Pool Exhaustion - Complete Deep Dive

# What Is A Database Connection?

A database connection is an active session between your application and the database.

Example:

```text
Application
      |
      v
Database Connection
      |
      v
Database
```

Creating connections is expensive because it involves:

```text
TCP Handshake

Authentication

Session Creation

Resource Allocation
```

Therefore applications do not create a new connection for every request.

Instead they maintain a:

```text
Database Connection Pool
```

---

# What Is A Database Connection Pool?

Example:

```text
Pool Size = 50
```

Meaning:

```text
50 database connections
already exist and are ready to use.
```

Popular implementations:

```text
HikariCP

Apache DBCP

C3P0
```

---

# Request Flow

Every request typically requires:

```text
Application Thread
+
Database Connection
```

Example:

```text
HTTP Request
      |
      v
Worker Thread
      |
      v
Acquire DB Connection
      |
      v
Execute Queries
      |
      v
Commit Transaction
      |
      v
Release Connection
```

The connection is occupied during the entire operation.

---

# Application Thread Pool vs Database Connection Pool

These are different resources.

---

## Application Thread Pool

Example:

```java
Executors.newFixedThreadPool(30)
```

Meaning:

```text
30 requests can execute simultaneously.
```

---

## Database Connection Pool

Example:

```text
Pool Size = 20
```

Meaning:

```text
Only 20 database operations
can execute simultaneously.
```

---

# Mental Model

Think of:

```text
Application Threads
```

as:

```text
Restaurant Workers
```

and:

```text
Database Connections
```

as:

```text
Cash Registers
```

Example:

```text
100 Workers
20 Registers
```

Even though:

```text
100 workers are available
```

only:

```text
20 customers
```

can check out simultaneously.

Everyone else waits.

---

# What Is Database Pool Exhaustion?

Pool exhaustion means:

```text
All available database connections
are busy.
```

Example:

```text
Pool Size = 50

50 connections in use
```

Request 51:

```text
Needs connection
```

but:

```text
No connection available
```

So it waits.

---

Request 52:

```text
Wait
```

Request 53:

```text
Wait
```

Eventually:

```text
Connection acquisition timeout
```

occurs.

---

# Symptoms Of Pool Exhaustion

```text
Increasing latency

Connection timeout exceptions

Large request queues

Reduced throughput

Database wait times increasing
```

---

# Important Senior Engineering Insight

When someone says:

```text
Database pool exhausted
```

do NOT immediately think:

```text
Need bigger pool
```

Pool exhaustion is usually:

```text
A symptom

Not the root cause
```

---

# Root Cause #1 - Too Many Database Calls Per Request

Suppose booking an appointment performs:

```text
1. Read Provider

2. Read Schedule

3. Read Slot

4. Insert Booking

5. Update Slot Availability

6. Create Audit Record
```

Total:

```text
6 database operations
```

per request.

---

At:

```text
1000 requests/sec
```

Database performs:

```text
6000 database operations/sec
```

---

At:

```text
10000 requests/sec
```

Database performs:

```text
60000 database operations/sec
```

before we even discuss scaling.

---

# Why This Causes Pool Exhaustion

Suppose:

```text
Pool Size = 100
```

and each request occupies a connection for:

```text
200ms
```

because of multiple database operations.

Connections remain busy longer.

Eventually:

```text
100 connections busy
```

New requests cannot acquire connections.

Result:

```text
Wait Time Increases

Latency Increases

Timeouts Increase
```

---

# Senior Question To Ask

Before adding infrastructure:

```text
Why does a single request
need so many database round trips?
```

---

# Optimization - Reduce Database Round Trips

Current Flow:

```text
Read Provider

Read Schedule

Read Slot

Insert Booking

Update Slot
```

Five database calls.

---

Potential Improvement:

```text
Single Transaction

Stored Procedure

Optimized Query
```

Example:

```text
5 DB Calls
      ↓
2 DB Calls
```

---

# Why Fewer Round Trips Matter

Every database call requires:

```text
Acquire Connection

Network Round Trip

Database Processing

Return Result
```

Reducing:

```text
5 calls
```

to:

```text
2 calls
```

reduces:

```text
Connection Occupancy Time

Latency

Pool Utilization
```

without adding hardware.

---

# Stored Procedure Example

Instead of:

```text
Application
   |
Read Provider

Application
   |
Read Slot

Application
   |
Insert Booking

Application
   |
Update Slot
```

multiple client-server round trips occur.

---

Instead:

```sql
BookAppointment(
    providerId,
    slotId,
    patientId
)
```

Application makes:

```text
1 database call
```

instead of:

```text
4-5 database calls
```

---

# Root Cause #2 - Slow Queries

Suppose:

```text
Query Time = 20ms
```

At:

```text
1000 RPS
```

few concurrent connections are required.

---

Now suppose:

```text
Query Time = 200ms
```

Connections stay occupied:

```text
10x longer
```

Pool fills much faster.

---

# Solution

Review:

```text
Execution Plans

Slow Query Logs

Indexes
```

Look for:

```text
Table Scans

Missing Indexes

Inefficient Joins
```

---

# Root Cause #3 - Lock Contention

Appointment systems are fundamentally concurrency problems.

Example:

```text
Provider A
10:00 AM
```

Many users attempt to book simultaneously.

Flow:

```text
Read Slot

Update Slot

Commit
```

creates contention.

---

Effects:

```text
Transactions wait

Connections remain occupied

Pool utilization increases
```

---

# Solution

Use database-enforced correctness.

Example:

```sql
UNIQUE(provider_id, slot_id)
```

or:

```text
Optimistic Locking
```

Avoid long-running locks.

---

# Root Cause #4 - Connection Leaks

Application acquires:

```java
connection =
    datasource.getConnection();
```

but never releases it.

---

Pool slowly fills.

Eventually:

```text
Pool Utilization = 100%
```

even though traffic remains stable.

---

# Solution

Monitor:

```text
Active Connections

Idle Connections

Connection Wait Time

Leak Detection
```

---

# Root Cause #5 - Reads Hitting Primary

Suppose architecture already has:

```text
Primary
+
Read Replica
```

but reads still hit:

```text
Primary
```

Examples:

```text
Availability Search

Provider Lookup

Schedule Search
```

Now:

```text
Primary becomes overloaded.
```

---

# Solution

Route read traffic:

```text
Availability Search
Provider Search
Appointment Lookup
```

to:

```text
Read Replicas
```

while:

```text
Create Booking

Cancel Booking

Reschedule Booking
```

continue hitting:

```text
Primary
```

---

# Can Adding Replicas Increase Pool Capacity?

Yes.

Example:

```text
Primary Pool = 100

Replica Pool = 100

Replica Pool = 100
```

Total:

```text
300 available connections
```

across all databases.

---

# Important Limitation

Replicas only help:

```text
Read Traffic
```

If exhaustion comes from:

```text
INSERT

UPDATE

DELETE
```

then:

```text
Additional replicas provide little benefit.
```

because writes still go to primary.

---

# Capacity Planning Questions

If interviewer says:

```text
Database pool exhausted at 1000 RPS
```

ask:

```text
How many DB calls per request?

Average query latency?

P95 query latency?

Read/write ratio?

Pool size?

Are reads already using replicas?

Any lock contention?

Any slow queries?

Any connection leaks?
```

---

# Senior Engineering Takeaway

When I hear:

```text
Database pool exhaustion
```

I do NOT immediately think:

```text
Increase pool size
```

I think:

```text
How long is each connection occupied?

How many database round trips occur?

Can I reduce DB calls?

Can I optimize slow queries?

Can I reduce lock contention?

Can I move reads to replicas?
```

Pool exhaustion is usually the symptom.

The goal is to identify and eliminate the reason connections remain occupied for so long before adding additional infrastructure.
