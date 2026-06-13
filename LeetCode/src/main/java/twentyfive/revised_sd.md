# L6 Backend Engineering & System Design Handbook

## Table of Contents

- Part I — [L6 System Design Interview Mastery](#part-i--l6-system-design-interview-mastery)
  - [Chapter 1. System Design Interview Framework](#chapter-1-system-design-interview-framework)
  - [Chapter 2. System Design Fundamentals](#chapter-2-system-design-fundamentals)
  - [Chapter 3. Load Balancing and Traffic Distribution](#chapter-3-load-balancing-and-traffic-distribution)
  - [Chapter 4. Caching Systems](#chapter-4-caching-systems)
  - [Chapter 5. Database Design and Storage Systems](#chapter-5-database-design-and-storage-systems)
  - [Chapter 6. Distributed Systems Fundamentals](#chapter-6-distributed-systems-fundamentals)
  - [Chapter 7. Data Consistency and Distributed Transactions](#chapter-7-data-consistency-and-distributed-transactions)
  - [Chapter 8. Traffic Control and Resilience Engineering](#chapter-8-traffic-control-and-resilience-engineering)
  - [Chapter 9. Messaging and Event-Driven Architecture](#chapter-9-messaging-and-event-driven-architecture)
  - [Chapter 10. API Design and Communication Patterns](#chapter-10-api-design-and-communication-patterns)
  - [Chapter 11. Reliability and Production Engineering](#chapter-11-reliability-and-production-engineering)
  - [Chapter 12. Performance Engineering](#chapter-12-performance-engineering)

- Part II — [Advanced System Architecture](#part-ii--advanced-system-architecture)
  - [Chapter 13. Search Systems](#chapter-13-search-systems)
  - [Chapter 14. CDN and Object Storage](#chapter-14-cdn-and-object-storage)
  - [Chapter 15. Microservices Architecture](#chapter-15-microservices-architecture)
  - [Chapter 16. Cloud Native Architecture](#chapter-16-cloud-native-architecture)
  - [Chapter 17. AI and LLM System Architecture](#chapter-17-ai-and-llm-system-architecture)

- Part III — [Senior Java and Spring Backend Engineering](#part-iii--senior-java-and-spring-backend-engineering)
  - [Chapter 18. Java Concurrency and JVM Fundamentals](#chapter-18-java-concurrency-and-jvm-fundamentals)
  - [Chapter 19. Spring Core](#chapter-19-spring-core)
  - [Chapter 20. Spring Boot and Production APIs](#chapter-20-spring-boot-and-production-apis)
  - [Chapter 21. Spring Data JPA and Hibernate](#chapter-21-spring-data-jpa-and-hibernate)
  - [Chapter 22. Spring Security](#chapter-22-spring-security)
  - [Chapter 23. Resilience4j and Microservice Resilience](#chapter-23-resilience4j-and-microservice-resilience)

---

# Part I — L6 System Design Interview Mastery

## Chapter 1. System Design Interview Framework

- Understanding and Clarifying Requirements
- Scale Estimation
- High-Level Architecture
- API and Data Modeling
- Deep Dive into Critical Components
- Bottlenecks and System Improvements
- Engineering Trade-offs
- Reliability and Failure Handling
- Security and Privacy
- Observability and Operations
- Multi-Region Design
- How an L6 Engineer Thinks
- Interview Time Management
- Common L6 Follow-Up Questions
- Final System Design Checklist

---

## Chapter 2. System Design Fundamentals

- Scalability
  - Horizontal vs Vertical Scaling
- Reliability
- Availability
- Efficiency
  - Latency
  - Throughput
- Maintainability

---

## Chapter 3. Load Balancing and Traffic Distribution

- Why Load Balancers Are Needed
- Load Balancing Algorithms
  - Round Robin
  - Weighted Round Robin
  - Least Connections
  - Least Response Time
  - IP Hash
- Health Checks
- Layer 4 vs Layer 7 Load Balancing
- Reverse Proxy
- Active-Active vs Active-Passive
- Global Load Balancing
- Sticky Sessions

---

## Chapter 4. Caching Systems

- Why Caching Is Needed
- Principle of Locality
- Cache Types
  - Browser Cache
  - CDN Cache
  - Local Application Cache
  - Distributed Cache
- Cache Read Patterns
  - Cache Aside
  - Read Through
- Cache Write Patterns
  - Write Through
  - Write Back
  - Write Around
- TTL and Cache Invalidation
- Cache Stampede
- Hot Keys
- Redis Replication and Sharding
- Cache Failure Handling

---

## Chapter 5. Database Design and Storage Systems

- Choosing the Right Database
- Relational Databases (SQL)
- ACID Transactions
- NoSQL Databases
  - Key-Value Databases
  - Document Databases
  - Column Family Databases
  - Graph Databases
- SQL vs NoSQL Trade-offs
- Normalization vs Denormalization
- Indexing
  - B-Tree Index
  - Hash Index
  - Composite Index
- Replication
- Partitioning and Sharding
- Partition Key Selection
- Hot Partitions
- Database Selection Examples

---

## Chapter 6. Distributed Systems Fundamentals

- What Is a Distributed System?
- Distributed System Challenges
- CAP Theorem
- CP vs AP Systems
- Consistency Models
  - Strong Consistency
  - Eventual Consistency
  - Read-Your-Writes
  - Monotonic Reads
  - Causal Consistency
- PACELC Theorem
- Replication
- Quorum-Based Replication
- Leader-Follower Architecture
- Leader Election
- Split Brain Problem
- Heartbeats and Failure Detection
- Checksums

---

## Chapter 7. Data Consistency and Distributed Transactions

- Why Distributed Transactions Are Difficult
- Two-Phase Commit (2PC)
- Saga Pattern
  - Choreography
  - Orchestration
- Transactional Outbox Pattern
- Change Data Capture (CDC)
- Idempotent Consumers
- Exactly Once Processing
- Event Ordering

---

## Chapter 8. Traffic Control and Resilience Engineering

- Controlled Concurrency
  - Thread Pools
  - Semaphores
- Rate Limiting
  - Fixed Window
  - Sliding Window
  - Token Bucket
  - Leaky Bucket
- Backpressure
- Queue Management
- Retry Storm Prevention
- Timeouts
- Circuit Breakers
- Graceful Degradation

---

## Chapter 9. Messaging and Event-Driven Architecture

- Why Asynchronous Communication
- Message Queue vs Event Streaming
- Kafka Architecture
- Topics and Partitions
- Producers
- Consumers and Consumer Groups
- Consumer Offsets
- Delivery Semantics
  - At Most Once
  - At Least Once
  - Exactly Once
- Idempotent Producers
- Kafka Transactions
- Ordering Guarantees
- Consumer Scaling
- Dead Letter Queues (DLQ)

---

## Chapter 10. API Design and Communication Patterns

- REST Principles
- HTTP Methods and Status Codes
- Resource Modeling
- Request and Response Design
- API Versioning
- Pagination
- Idempotency
- Authentication and Authorization
- REST vs gRPC
- Synchronous vs Asynchronous Communication

---

## Chapter 11. Reliability and Production Engineering

- Failure Modes
- Timeouts
- Retries
  - Exponential Backoff
  - Jitter
- Circuit Breakers
- Bulkheads
- Load Shedding
- Graceful Degradation
- Health Checks
- Observability
  - Metrics
  - Logs
  - Distributed Tracing
- SLI, SLO, and SLA

---

## Chapter 12. Performance Engineering

- Latency vs Throughput
- Capacity Planning
- Little's Law
- CPU-Bound vs I/O-Bound Systems
- Thread Pools
- Connection Pools
- Batching
- Caching and Performance
- Load Testing
  - Load Testing
  - Stress Testing
  - Spike Testing
  - Soak Testing
- Bottleneck Identification
  - CPU
  - Memory
  - Database
  - Network
  - External Dependencies
- Queue Backlogs and Consumer Lag
- Performance Optimization Process

---

# Part II — Advanced System Architecture

## Chapter 13. Search Systems

- Search Architecture
- Inverted Index
- Tokenization
- Normalization
- Stop Words
- Stemming
- Ranking and Relevance
- Indexing Pipeline
- Query Processing
- Elasticsearch Architecture

---

## Chapter 14. CDN and Object Storage

- Why CDNs Are Needed
- CDN Architecture
- Edge Caching
- Cache Invalidation
- Content Delivery Flow
- Object Storage Architecture
- Storage Classes
- Replication and Durability
- Pre-Signed URLs

---

## Chapter 15. Microservices Architecture

- Monolith vs Microservices
- Service Boundaries
- Domain Driven Design (DDD)
- Database Per Service
- Synchronous Communication
  - REST
  - gRPC
- Asynchronous Communication
  - Kafka
  - Messaging
- Distributed Transactions
- API Gateway and Backend-for-Frontend (BFF)
- Service Discovery
- Configuration and Secret Management
- Service Mesh
- Failure Scenarios and Resilience

---

## Chapter 16. Cloud Native Architecture

- Containers
- Virtual Machines vs Containers
- Docker
- Container Registries
- Kubernetes
  - Pods
  - Deployments
  - Services
  - Autoscaling
- Configuration Management
  - ConfigMaps
  - Secrets
- CI/CD Pipelines
- Deployment Strategies
  - Rolling Deployments
  - Blue-Green Deployments
  - Canary Deployments
- Infrastructure as Code (IaC)

---

## Chapter 17. AI and LLM System Architecture

- AI-Augmented vs AI-Native Systems
- LLM Limitations
- Embeddings
- Vector Databases
- Retrieval-Augmented Generation (RAG)
- Prompt Engineering
- Guardrails and Validation
- Human-in-the-Loop Systems
- AI System Monitoring

---

# Part III — Senior Java and Spring Backend Engineering

## Chapter 18. Java Concurrency and JVM Fundamentals

- Threads and Processes
- ExecutorService and Thread Pools
- CompletableFuture
- Locks and Synchronization
- Java Memory Model
- Garbage Collection
- JVM Performance Tuning

---

## Chapter 19. Spring Core

- Inversion of Control (IoC)
- Dependency Injection (DI)
- Bean Lifecycle
- Bean Scopes
- Application Context
- Spring AOP
- Spring Proxies
- Transaction Management Internals

---

## Chapter 20. Spring Boot and Production APIs

- Spring Boot Auto Configuration
- Starter Dependencies
- Embedded Web Server
- DispatcherServlet
- REST Controllers
- DTO vs Entity Design
- Validation
- Global Exception Handling
- Profiles and Configuration
- Spring Boot Actuator

---

## Chapter 21. Spring Data JPA and Hibernate

- JPA Architecture
- Entity Lifecycle
- Persistence Context
- Dirty Checking
- Lazy vs Eager Loading
- N+1 Query Problem
- Transactions
- Locking Strategies
- Batch Operations

---

## Chapter 22. Spring Security

- Spring Security Filter Chain
- Authentication
- Authorization
- JWT
- OAuth2
- OpenID Connect (OIDC)
- Keycloak and Okta Integration
- Method-Level Security

---

## Chapter 23. Resilience4j and Microservice Resilience

- Circuit Breaker
- Retry
- Rate Limiter
- Bulkhead
- Timeout
- Fallback Strategies


---
# Chapter 1. System Design Interview Framework
---

# 1. Understand and Clarify Requirements

Never start designing immediately.

Spend the first 5–10 minutes understanding the problem.

Ask:

- What are the functional requirements?
- What are the non-functional requirements?
- What are the constraints?

---

## Functional Requirements

What should the system do?

Examples:

Twitter:

- Create tweets.
- Follow users.
- View timelines.
- Like posts.
- Search content.


Uber:

- Request a ride.
- Match riders with drivers.
- Track locations.
- Process payments.

---

## Non-Functional Requirements

How should the system behave?

Examples:

- Scalability
- Availability
- Reliability
- Low latency
- Consistency
- Durability
- Security

---

## Prioritize Requirements

Large systems cannot optimize every property.

Example:

Banking:

Prioritize:

- Consistency
- Durability

Less important:

- Millisecond latency


Social media:

Prioritize:

- Availability
- Scalability
- Low latency

Eventual consistency is acceptable.

---

# 2. Estimate Scale

Before choosing technologies, estimate the magnitude of the system.

Ask:

- How many users?
- Daily active users?
- Requests per second?
- Data size?
- Read/write ratio?

---

## Example: Twitter Scale Estimation

Users:

```
500 million users
```

Daily active:

```
100 million
```

Tweets per day:

```
500 million
```

Average:

```
~6000 writes/second
```

Read traffic may be much higher.

---

## Why Estimate?

Because design changes based on scale.

Example:

100 requests/sec:

- Single database may be sufficient.


100 million requests/sec:

- Caching.
- Sharding.
- Multiple regions.
- Distributed systems.

---

# 3. High-Level Design

Draw the major components first.

Do not start with database tables.

Example:

```
Users
  |
Load Balancer
  |
API Gateway
  |
Application Services
  |
----------------------
|          |          |
Cache     Database   Kafka
             |
          Storage
```

Explain responsibilities.

Example:

API Gateway:

- Authentication.
- Rate limiting.
- Routing.

Cache:

- Reduce database load.
- Improve latency.

Kafka:

- Asynchronous processing.
- Decoupling.
- Replayability.

---

# 4. Design Data Models and APIs

Once architecture is clear, define:

- Data entities.
- Database schema.
- APIs.

---

## API Example

Create Tweet:

```
POST /tweets
```

Request:

```json
{
 "userId": 123,
 "content": "Hello world"
}
```

Response:

```json
{
 "tweetId": 456,
 "status": "SUCCESS"
}
```

---

## Data Model Example

Tweet:

```
tweetId
userId
content
createdTime
visibility
```

---

# 5. Deep Dive Into Critical Components

This is where L6 interviews are won.

Identify the bottlenecks.

Examples:

---

## Database Scaling

Questions:

- Will one database handle the traffic?
- Do we need replication?
- Do we need sharding?
- What is the partition key?

---

## Caching

Questions:

- What data should be cached?
- What is the TTL?
- How is cache invalidated?
- What happens when cache fails?

---

## Asynchronous Processing

Questions:

- What operations do not need synchronous responses?
- Can Kafka improve throughput?
- How do consumers handle failures?
- Is ordering required?

---

## Media Systems

Questions:

- How are files uploaded?
- Do we use pre-signed URLs?
- How are videos processed?
- Do we need a CDN?

---

# 6. Identify Bottlenecks and Improve

Good system design is iterative.

Start simple:

```
User
 |
Service
 |
Database
```

Find problems:

- Database overloaded.
- High latency.
- Too many writes.
- Large storage requirements.

Then improve:

```
Users
 |
Load Balancer
 |
Application Servers
 |
Redis Cache
 |
Database Replicas
 |
Sharded Databases
 |
Kafka for async processing
```

---

# 7. Discuss Trade-Offs

Senior engineers always discuss trade-offs.

---

## SQL vs NoSQL

SQL:

Advantages:

- ACID transactions.
- Joins.
- Strong consistency.

Disadvantages:

- More difficult horizontal scaling.


NoSQL:

Advantages:

- High scalability.
- Flexible schema.

Disadvantages:

- Usually weaker consistency.
- Data duplication.

---

## Synchronous vs Asynchronous

Synchronous:

Advantages:

- Immediate response.
- Easier consistency.

Disadvantages:

- Higher latency.
- Tighter coupling.


Asynchronous:

Advantages:

- Decoupling.
- Better throughput.
- Retries and replayability.

Disadvantages:

- Eventual consistency.
- More operational complexity.

---

## Cache vs Database

Cache:

Advantages:

- Low latency.
- Reduced database load.

Disadvantages:

- Stale data.
- Cache invalidation complexity.

---

# 8. Reliability and Failure Handling

An L6 candidate should always discuss failure scenarios.

Ask:

What happens if:

- A server dies?
- The database is unavailable?
- Redis fails?
- Kafka consumer falls behind?
- A dependency becomes slow?
- Traffic spikes 100x?

---

Solutions:

- Replication.
- Timeouts.
- Retries with backoff and jitter.
- Circuit breakers.
- Rate limiting.
- Backpressure.
- Graceful degradation.

---

# 9. Security and Privacy

Depending on the system, discuss:

Authentication:

- OAuth
- JWT

Authorization:

- Roles and permissions.

Data protection:

- Encryption at rest.
- Encryption in transit.

Other considerations:

- API rate limiting.
- Audit logging.
- PII protection.

---

# 10. Observability and Operations

A production-ready system requires visibility.

Monitor:

- Request latency.
- Throughput.
- Error rates.
- Resource saturation.

Use:

- Metrics.
- Logs.
- Distributed tracing.

Examples:

- Prometheus.
- Grafana.
- ELK stack.

---

# 11. Multi-Region Design (If Required)

For global systems consider:

- Data replication.
- Geo-routing.
- CDN.
- Disaster recovery.

Trade-offs:

Higher availability.

versus

Replication lag and consistency challenges.

---

# 12. How an L6 Engineer Thinks

A junior engineer says:

"Let's use Redis."

A senior engineer says:

"We have a read-heavy workload with strict latency requirements, so I would introduce Redis using a cache-aside pattern. For consistency, I would use cache invalidation on writes and TTL as a saf[...]

---

A junior engineer says:

"Let's use Kafka."

A senior engineer says:

"The operation does not require immediate user feedback, so I would move it to an asynchronous Kafka pipeline. This provides decoupling, replayability, and allows consumers to scale independently[...]

---

A junior engineer says:

"We should add more servers."

A senior engineer says:

"Before scaling, I want to understand whether the bottleneck is CPU, database capacity, network bandwidth, or a slow dependency."

---

# 13. Time Management During a 60 Minute Interview

Recommended breakdown:

First 5–10 minutes:

- Clarify requirements.
- Estimate scale.

---

Next 10 minutes:

- Draw high-level architecture.

---

Next 25–30 minutes:

Deep dive:

- Data model.
- Database.
- Cache.
- Scaling.
- Failure handling.

---

Final 10 minutes:

Discuss:

- Trade-offs.
- Bottlenecks.
- Future improvements.

---

# 14. Common L6 Follow-Up Questions

## Scaling

- What happens when users increase 100x?
- How would you shard the database?
- How do you avoid hot partitions?

---

## Reliability

- What happens if Redis goes down?
- How do you handle Kafka failures?
- How do you prevent cascading failures?

---

## Consistency

- Is eventual consistency acceptable?
- How do you prevent stale reads?
- Where do you need transactions?

---

## Performance

- What are your latency goals?
- What are your bottlenecks?
- How would you improve throughput?

---

## Operations

- What metrics would you monitor?
- How would you debug a production outage?

---

# 15. Final L6 System Design Checklist

Before ending the interview, make sure you covered:

☐ Functional requirements

☐ Non-functional requirements

☐ Scale estimation

☐ APIs and data model

☐ High-level architecture

☐ Database choice

☐ Partitioning strategy

☐ Caching strategy

☐ Asynchronous processing

☐ Reliability and failures

☐ Security

☐ Observability

☐ Trade-offs

☐ Future scaling concerns

---

# The Golden Rule

Do not try to show every technology you know.

An good system design interview is not about saying:

"Redis + Kafka + Cassandra + Kubernetes."

It is about explaining:

- Why a component is needed.
- What problem it solves.
- What new complexity it introduces.
- What trade-offs you accept.

The best L6 engineers are not judged by the number of technologies they mention.

They are judged by the quality of their engineering decisions.

---
# Chapter 2. System Design Fundamentals
---

## 1. What is System Design?

System design is the process of defining the architecture, components, modules, interfaces, and data flow of a system to satisfy both functional and non-functional requirements.

A good system should be able to:

- Handle increasing traffic and data growth.
- Remain available during failures.
- Provide low latency responses.
- Be easy to maintain and evolve.
- Handle failures gracefully.
- Optimize cost while meeting business goals.

---

# (rest of document unchanged)
