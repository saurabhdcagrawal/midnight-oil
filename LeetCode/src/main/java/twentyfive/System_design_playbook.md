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

	# Step 2: Scale Reads — Introduce Redis


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

	# Step 3: Keep Redis Updated


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

	# Step 4: What If Redis Update Fails?


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

	# Step 5: Scale Writes — Introduce Kafka


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

	# Step 6: Hybrid Large Scale Design


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

	# Step 7: Multiple Device Conflict (L6 Deep Dive)


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

	# Reliability

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

	# Partitioning


	Partition by userId.


	Reason:

	Most access patterns are:

	(userId, bookId)


	This keeps a user's data together and distributes load.


	---

	# L6 Interview Flow


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

	# Key L6 Soundbite


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

	#### Part B — Detailed Design Reference

	## 1. Problem Statement

	## 2. Functional Requirements

	### Core Features

	* Update audiobook playback progress.
	* Retrieve latest playback progress.
	* Synchronize progress across multiple devices.
	* Support near real-time synchronization.
	* Handle millions of listeners.

	---

	## 3. Non-Functional Requirements

	* Low latency reads.
	* High write throughput due to frequent heartbeat updates.
	* High availability.
	* Durable storage of playback progress.
	* Horizontal scalability.
	* Reliable handling of failures.

	---

	## 4. Capacity Estimation

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

	## 5. API Design

	### Update Progress

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

	### Get Latest Progress

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

	## 6. Data Model

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

	## 7. Initial Simple Architecture

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

	## 8. Scale Reads with Redis Cache

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

	### Cache Miss

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

	## 9. Keeping Cache Updated (Write-Through)

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

	## 10. Cache Update Failure Handling

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

	### Retry Redis Update

	Useful for temporary failures.

	Example:

	```
	Retry 1
	Retry 2
	Retry 3
	```

	### Invalidate Only the Affected Cache Key

	Example:

	```
	DELETE progress:user123:bookABC
	```

	The next read will cause a cache miss and reload the latest value from the database.

	---

	## 11. Scale Writes with Kafka

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

	## 12. Hybrid Large-Scale Architecture

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

	## 13. Multiple Device Synchronization (L6 Discussion)

	Multiple devices may update the same audiobook:

	Example:

	```
	Phone:
	1:30:00

	Tablet:
	1:25:00
	```

	Possible conflict resolution strategies:

	### Last Write Wins

	Use timestamps.

	The latest update replaces the older one.

	Limitation:

	* Device clocks may not be reliable.

	---

	### Server-Generated Version Numbers

	Maintain:

	```
	version
	```

	The server increments the version for each successful update.

	Benefits:

	* Detect stale updates.
	* Prevent old retries from overwriting newer progress.

	---

	### Optimistic Locking

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

	## 14. Reliability Considerations

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

	## 15. Partitioning Strategy

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

	## 16. Interview Evolution Strategy

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

	## L6 Interview Soundbite

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


