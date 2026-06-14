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

# L6 Backend Engineering & System Design Handbook

# Table of Contents

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

"We have a read-heavy workload with strict latency requirements, so I would introduce Redis using a cache-aside pattern. For consistency, I would use cache invalidation on writes and TTL as a safety mechanism."

---

A junior engineer says:

"Let's use Kafka."

A senior engineer says:

"The operation does not require immediate user feedback, so I would move it to an asynchronous Kafka pipeline. This provides decoupling, replayability, and allows consumers to scale independently."

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

A good system design interview is not about saying:

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

# 2. Scalability

Scalability is the ability of a system to handle increasing workload by adding resources.

A scalable system should continue to provide acceptable performance when:

- Number of users increases.
- Request volume increases.
- Data size grows.

There are two primary ways to scale a system:

- Horizontal Scaling (Scale Out)
- Vertical Scaling (Scale Up)

---

# 2.1 Horizontal Scaling (Scale Out)

Horizontal scaling means adding more machines or instances to an existing pool of resources.

Example:


Before:

        Application Server


After:

     App Server 1
          |
     App Server 2
          |
     App Server 3


Advantages:

- Can be scaled dynamically.
- Provides better fault tolerance because failure of one node does not bring down the entire system.
- Allows handling very large traffic and data volume.
- Usually preferred for internet-scale systems.

Examples:

- Application server clusters.
- Cassandra clusters.
- Kafka brokers.
- MongoDB clusters.
- Redis clusters.

---

# 2.2 Vertical Scaling (Scale Up)

Vertical scaling means increasing the capacity of a single machine.

Examples:

- Add more CPU.
- Add more memory.
- Add faster SSD storage.
- Increase network capacity.


Example:


Before:
8 CPU, 32 GB RAM

After:
64 CPU, 512 GB RAM


Advantages:

- Simpler architecture.
- No need to deal with distributed system complexity.
- Easier to implement initially.

Limitations:

- Hardware has an upper limit.
- May require downtime.
- Creates a larger single point of failure.
- Can become very expensive.

---

# 2.3 Horizontal vs Vertical Scaling Trade-Off

| Horizontal Scaling | Vertical Scaling |
|---|---|
| Add more machines | Increase machine capacity |
| Better fault tolerance | Simpler architecture |
| Supports massive scale | Limited by hardware |
| More complex | Easier to implement |
| Common for distributed systems | Common for smaller systems |

---

## L6 Interview Discussion

A common interview mistake is saying:

"MySQL scales only vertically."

This is not completely accurate.

Traditional relational databases relied heavily on vertical scaling, but modern systems also scale using:

- Read replicas.
- Sharding.
- Partitioning.
- Distributed SQL systems such as Google Spanner or CockroachDB.

Real-world systems usually combine both horizontal and vertical scaling.

Example:

- Scale stateless application servers horizontally.
- Use caching to reduce database load.
- Add read replicas for heavy read traffic.
- Partition databases when data grows significantly.

---

# 3. Reliability

Reliability is the ability of a system to continue operating correctly over a period of time, even in the presence of failures.

A reliable system should:

- Continue working despite hardware failures.
- Recover from software crashes.
- Avoid data corruption or loss.
- Produce correct results.

---

## Improving Reliability

Common techniques include:

### Redundancy

Avoid having a single point of failure.

Example:

Instead of:

Client
 |
Server


Use:

                Load Balancer
                      |
              -----------------
              |               |
           Server A        Server B


If Server A fails, traffic can be routed to Server B.

---

### Replication

Maintain multiple copies of data.

Example:

              Primary Database
                    |
          --------------------
          |                  |
     Replica A          Replica B


Benefits:

- Failure recovery.
- Improved availability.
- Read scaling.

---

### Failure Detection

Systems use mechanisms such as:

- Health checks.
- Heartbeats.
- Monitoring alerts.

---

## L6 Interview Discussion

Reliability is not only about preventing failures.

Failures are inevitable in distributed systems.

The goal is to:

- Detect failures quickly.
- Isolate failures.
- Recover automatically.
- Minimize customer impact.

---

# 4. Availability

Availability measures the percentage of time a system is operational and accessible to users.

Formula:

Availability = (Total Time - Downtime) / Total Time

Example:

99.99% availability means the service is unavailable for approximately:

~52 minutes per year.

---

## Availability Levels

| Availability | Downtime per Year |
|---|---|
| 99% | ~3.65 days |
| 99.9% | ~8.76 hours |
| 99.99% | ~52 minutes |
| 99.999% | ~5 minutes |

---

## Availability vs Reliability

These concepts are related but different.

### Highly Available but Not Reliable

Example:

A website responds to every request but returns incorrect account balances.

It is available but not reliable.

---

### Reliable but Not Highly Available

Example:

A system is frequently taken offline for maintenance to guarantee data correctness.

It may be reliable but not highly available.

---

## L6 Interview Discussion

A highly available system is not automatically reliable.

The ideal system is both:

- Highly available.
- Highly reliable.

The required trade-off depends on business requirements.

Examples:

Banking systems:
- Prioritize consistency and correctness.

Social media:
- Prioritize availability and responsiveness.

---

# 5. Efficiency

Efficiency measures how effectively a system uses resources to provide performance.

The two most important measurements are:

## Latency

The time required to complete a single request.

Example:

A user clicks "Load Timeline".

The system responds in:

150 milliseconds.

Latency measures that delay.

---

## Throughput

The amount of work completed per unit of time.

Examples:

- Requests per second.
- Transactions per second.
- Messages processed per second.

Example:

A Kafka consumer processing:

100,000 messages per second.

---

## L6 Interview Discussion

Improving throughput does not always reduce latency.

Example:

Batch processing can increase throughput by processing many requests together, but individual requests may wait longer.

---

# 6. Maintainability

Maintainability is the ability of a system to be:

- Debugged.
- Modified.
- Tested.
- Extended.
- Operated by engineers.

A maintainable system reduces long-term engineering cost.

---

## Characteristics of Maintainable Systems

### Modular Design

Separate responsibilities into independent components.

Example:

Instead of a giant application:

User Service
Order Service
Payment Service


Each service can evolve independently.

---

### Observability

A system should provide enough information to understand its internal state.

Includes:

- Metrics.
- Logs.
- Distributed tracing.

---

### Simplicity

The simplest solution that satisfies requirements is often the best solution.

Avoid unnecessary complexity.

---

## L6 Interview Discussion

Senior engineers optimize for the lifetime of the system.

A design that works today but becomes impossible to operate in one year is not a good design.

Good engineers consider:

- Operational complexity.
- Deployment strategies.
- Monitoring.
- Debuggability.
- Future growth.

---

# Key Takeaways

1. Scalability allows a system to handle growth.
    - Horizontal scaling adds machines.
    - Vertical scaling increases machine capacity.

2. Reliability ensures the system behaves correctly despite failures.

3. Availability measures whether the system is operational.

4. Efficiency focuses on latency and throughput.

5. Maintainability ensures the system can evolve and be operated over many years.

---

# Common L6 Interview Follow-Up Questions

### Scalability

- Why not simply keep adding bigger machines?
- When would you choose vertical scaling?
- How would you scale a database?

---

### Reliability

- What happens when a server fails?
- How do you remove single points of failure?
- How do you recover from a database outage?

---

### Availability

- Is a highly available system always reliable?
- How much downtime does 99.99% availability allow?

---

### Performance

- How would you reduce latency?
- How would you improve throughput?

---

### Maintainability

- How do you make a large system easier to operate?
- What tools help you debug production issues?

When you choose Redis, you are optimizing latency.
When you choose Kafka, you are improving scalability and decoupling.
When you add replicas, you are improving availability and reliability.
When you use sharding, you are improving scalability.
When you add monitoring, you improve maintainability.


---
# Chapter 3. Load Balancing and Traffic Distribution
---

# 1. Why Do We Need Load Balancers?

As a system grows, a single server becomes insufficient because of:

- CPU limitations
- Memory limitations
- Network bandwidth limitations
- Single point of failure

Example:

Without load balancing:


               Client
                  |
              Web Server


Problems:

- Limited capacity
- If the server crashes, the entire application is unavailable


Solution: Add multiple servers.


               Client
                  |
           Load Balancer
                  |
        --------------------
        |         |         |
     Server A  Server B  Server C


The Load Balancer distributes incoming traffic across multiple servers.

---

# 2. Benefits of Load Balancing

## Scalability

New servers can be added as traffic increases.

Example:

Black Friday traffic increases.

Before:

3 application servers

After:

30 application servers


---

## High Availability

If a server fails, traffic is routed to healthy servers.

Example:


Request arrives:

              Load Balancer
                    |
           -------------------
           |                 |
      Server A            Server B (healthy)
       (down)

Request automatically goes to Server B.

---

## Better Resource Utilization

Without load balancing:

Server A:
- 90% CPU utilization

Server B:
- 10% CPU utilization


With proper load balancing:

Server A:
- 50%

Server B:
- 50%

---

# 3. Load Balancer Placement

Typical architecture:


               Users
                 |
                 DNS
                 |
          Load Balancer
                 |
          API Gateway
                 |
       ------------------
       |                |
 Service A          Service B


DNS resolves the domain name and directs users to the load balancer IP address.

The load balancer then decides which backend instance should handle the request.

---

# 4. Load Balancing Algorithms


## 4.1 Round Robin

Requests are distributed sequentially.


Example:


Request 1 → Server A

Request 2 → Server B

Request 3 → Server C

Request 4 → Server A


Advantages:

- Simple
- Easy to implement


Disadvantages:

- Assumes all servers have equal capacity
- Does not consider current load


---

## 4.2 Weighted Round Robin


Some servers are more powerful.


Example:


Server A:
16 CPUs

Server B:
8 CPUs


Weights:

Server A = 2

Server B = 1


Traffic:

2 requests → Server A

1 request → Server B


Advantages:

- Handles different hardware capacities


---

## 4.3 Least Connections


Send the request to the server with the fewest active connections.


Example:


Server A:
200 active connections


Server B:
50 active connections


New request goes to Server B.


Advantages:

- Better for long-running requests


Examples:

- WebSockets
- Streaming connections


---

## 4.4 Least Response Time


Send traffic to the server that responds fastest.


Factors:

- Current load
- CPU usage
- Network latency


Useful for:

- Large distributed systems


---

## 4.5 IP Hash


The same client IP is mapped to the same server.


Example:


User 1 IP → Server A

User 2 IP → Server B


Advantages:

- Provides session affinity (sticky sessions)


Disadvantages:

- Uneven distribution if some users are very active


---

# 5. Health Checks

A load balancer must determine whether a server is healthy.

---

## Active Health Checks


The load balancer periodically sends requests:

Example:

GET /health


Server response:

HTTP 200 OK


The server remains in the rotation.


If the server does not respond:

- Mark as unhealthy
- Stop sending traffic


---

## Passive Health Checks


The load balancer observes real traffic.

Example:

- Connection failures
- HTTP 500 errors
- Timeouts


A server with repeated failures can be removed from rotation.

---

# 6. Layer 4 vs Layer 7 Load Balancing


## Layer 4 (Transport Layer)

Works at the TCP/UDP level.


Decisions are based on:

- Source IP
- Destination IP
- Port


Example:

Client
  |
TCP Connection
  |
Load Balancer
  |
Server


Advantages:

- Very fast
- Lower overhead


Disadvantages:

- Does not understand HTTP requests


Examples:

- AWS Network Load Balancer


---

## Layer 7 (Application Layer)

Works at the HTTP/HTTPS level.


Can inspect:

- URL paths
- Headers
- Cookies
- HTTP methods


Example:


/api/users    → User Service

/api/orders   → Order Service


Advantages:

- Intelligent routing
- Authentication integration
- Request rewriting
- Rate limiting


Disadvantages:

- More CPU overhead


Examples:

- NGINX
- HAProxy
- AWS Application Load Balancer


---

# 7. Reverse Proxy vs Load Balancer


## Reverse Proxy

A server that sits in front of backend servers and receives requests on their behalf.


Client
  |
Reverse Proxy
  |
Backend Servers


Responsibilities may include:

- Routing requests
- SSL termination
- Authentication
- Compression
- Caching


Examples:

- NGINX
- HAProxy


---

## Load Balancer


Primary responsibility:

Distribute traffic across multiple servers.


In modern systems, a reverse proxy often also acts as a load balancer.


Example:

NGINX can act as:

- Reverse proxy
- Load balancer
- Cache
- SSL terminator


---

# 8. Active-Active vs Active-Passive


## Active-Active


Multiple servers simultaneously handle traffic.


Example:


          Load Balancer
                 |
       -------------------
       |                 |
    Server A          Server B


Advantages:

- Better resource utilization
- Higher throughput
- Faster failover


Disadvantages:

- More complex synchronization


---

## Active-Passive


One server handles traffic.

The other waits as a backup.


Example:


            Load Balancer
                   |
             Primary Server
                   |
              Backup Server


If the primary fails:

Backup becomes active.


Advantages:

- Simpler design


Disadvantages:

- Backup resources remain unused


---

# 9. Global Load Balancing


Large companies have multiple data centers.


Example:


              Users
                 |
             Geo DNS
                 |
      ----------------------
      |                    |
  US Data Center      Europe Data Center


Routing decisions can consider:

- User location
- Latency
- Data center health
- Capacity


Examples:

- Route53 latency-based routing
- Anycast routing


---

# 10. L6 Interview Discussion


## Why not simply add more application servers?

Without a load balancer, users do not know which server to choose.

A load balancer distributes traffic and handles failures.


---

## What happens when a server dies?

Health checks detect the failure.

The load balancer removes the unhealthy server and redirects traffic to healthy instances.


---

## What happens if the load balancer itself fails?

A load balancer can become a single point of failure.

Solutions:

- Multiple load balancers
- DNS failover
- Active-active load balancer clusters


---

## When would you use Layer 7 instead of Layer 4?

Use Layer 7 when you need:

- Path-based routing
- Header-based routing
- Authentication
- HTTP-aware policies


Use Layer 4 when you need:

- Maximum performance
- Simple TCP/UDP routing


---

## Why avoid sticky sessions?

Sticky sessions tie a user to a particular server.

Problems:

- Uneven load distribution
- Harder auto-scaling
- Difficult failover


Prefer stateless services.

Store shared session data in:

- Redis
- Database
- Distributed session store


---

# Key Takeaways


1. Load balancers enable horizontal scaling and high availability.

2. Common algorithms:
   - Round robin
   - Weighted round robin
   - Least connections
   - Least response time
   - IP hash

3. Health checks remove failed servers.

4. Layer 4 is faster; Layer 7 provides smarter routing.

5. Reverse proxies can provide routing, caching, SSL termination, and load balancing.

6. Avoid sticky sessions by designing stateless services.

7. Global systems use DNS and geo-routing to direct users to the closest healthy data center.



---
# Chapter 4. Caching Systems
---

# 1. Why Do We Need Caching?

Caching stores frequently accessed data in a faster storage layer to reduce:

- Database load
- Network calls
- Disk access
- Response latency

The goal is to serve repeated requests from a fast memory-based layer instead of repeatedly computing or retrieving the same data.

---

Example:

Without cache:

User Request
      |
 Application Server
      |
 Database
      |
 Response


Every request hits the database.

Problems:

- Higher latency
- More database CPU usage
- Limited scalability


---

With cache:


User Request
      |
 Application Server
      |
     Cache
      |
 Cache Hit?
   /      \
 Yes       No
 |          |
Return   Database
 Data       |
             |
          Store in Cache
             |
          Return Data


Benefits:

- Lower latency
- Higher throughput
- Reduced database pressure

---

# 2. Principle of Locality

Caching works because of the principle of locality.

## Temporal Locality

Data accessed recently is likely to be accessed again soon.

Example:

A breaking news article receives millions of requests within minutes.


---

## Spatial Locality

Data close to recently accessed data is also likely to be accessed.

Example:

When a user watches episode 5 of a series, they are likely to watch episode 6.

---

# 3. Types of Caches


## 3.1 Browser Cache

Located on the user's device.

Stores:

- Images
- CSS
- JavaScript
- Static files


Benefits:

- Zero network latency
- Reduces server traffic


---

## 3.2 CDN Cache

CDN stores content closer to users at edge locations.

Example:

User in India
      |
 CDN Edge Server (India)
      |
 Origin Server (US)


Benefits:

- Lower latency
- Lower origin traffic
- Better global performance


Examples:

- Images
- Videos
- Static assets


---

## 3.3 Application Local Cache

Stored inside the application process.

Example:

Java application:

```
HashMap<Key, Value>
```

or libraries like:

- Caffeine
- Guava Cache


Advantages:

- Extremely fast
- No network calls


Disadvantages:

- Each application instance has its own copy
- Difficult to keep data consistent


Example:


Server A:
User 123 → Gold

Server B:
User 123 → Silver


---

## 3.4 Distributed Cache

A separate shared cache service.

Examples:

- Redis
- Memcached


Architecture:


Application Server A
          |
Application Server B
          |
Application Server C
          |
        Redis


Advantages:

- Shared across servers
- Supports horizontal scaling
- Easier consistency management


Disadvantages:

- Additional network hop
- Cache service can become a bottleneck

---

# 4. Cache Read Strategies


## 4.1 Cache-Aside (Lazy Loading)

The most common pattern with Redis.


Flow:


Read Request
      |
 Application
      |
 Check Cache
      |
 Cache Miss?
      |
 Database
      |
 Update Cache
      |
 Return Data


Example:

GET /users/123


Redis:

user:123


If missing:

- Query MySQL
- Store result in Redis
- Return response


Advantages:

- Cache stores only frequently accessed data
- Lower memory usage


Disadvantages:

- First request has higher latency
- Possibility of stale data


---

## 4.2 Read-Through Cache

The application reads directly from cache.

The cache itself fetches data from the database on a miss.


Application
      |
 Cache
      |
 Database


Advantages:

- Simpler application code


Disadvantages:

- Cache layer becomes more complex


---

# 5. Cache Write Strategies


## 5.1 Write-Through Cache


Write Request
      |
 Application
      |
 Cache
      |
 Database


Data is written to cache and database together.


Advantages:

- Cache always contains fresh data


Disadvantages:

- Increased write latency
- Unnecessary cache entries for data that may never be read


---

## 5.2 Write-Back (Write-Behind)


Write Request
      |
 Application
      |
 Cache
      |
 Async Database Update


Advantages:

- Very low write latency
- High write throughput


Examples:

- Analytics counters
- High-volume metrics


Disadvantages:

- Data can be lost if cache fails before flushing


---

## 5.3 Write-Around


Write Request
      |
 Application
      |
 Database


Cache is not updated.

On the next read:

Cache miss → Database → Cache


Advantages:

- Avoids polluting cache with infrequently read data


Disadvantages:

- First read is slower


---

# 6. Cache Eviction Policies


## LRU (Least Recently Used)

Evict data that has not been used recently.

Based on temporal locality.


Example:

Cache:

A B C D


Access:

A


Evict:

B


---

## LFU (Least Frequently Used)

Evict the least frequently accessed items.


Good for:

- Frequently accessed long-lived data


Example:

Popular product pages.


---

## FIFO (First In First Out)

Remove the oldest inserted item.


Simple but does not consider usage patterns.


---

# 7. Time-To-Live (TTL)


TTL defines how long a cache entry remains valid.


Example:

Stock price:

TTL = 5 seconds


User profile:

TTL = 1 hour


Benefits:

- Automatic cleanup
- Prevents extremely stale data


Trade-off:

Short TTL:
- More database reads
- More fresh data


Long TTL:
- Lower database load
- More stale data


---

# 8. Cache Invalidation

One of the hardest problems in distributed systems.


Problem:

Database:
User 123 = Premium


Cache:
User 123 = Basic


User receives stale data.


---

## Common Strategies


### TTL-based invalidation

Allow cache entries to expire.


Simple but data can be stale.


---

### Explicit invalidation

When database updates:

Delete cache entry.


Example:

Update User
      |
 Update Database
      |
 Delete Redis Key


Next read:

Cache miss → Database → Cache


This is very common with cache-aside.


---

### Event-driven invalidation


Database Update
      |
 Kafka Event
      |
 Cache Consumer
      |
 Remove/Update Cache


Useful when many services cache the same data.


---

# 9. Cache Stampede (Thundering Herd)

Problem:

A popular key expires.


Example:

Celebrity profile expires.

Millions of users request it simultaneously.


Without protection:


1 million requests
       |
 Redis Miss
       |
 Database


Database becomes overwhelmed.


---

Solutions:

## Request Coalescing

Allow only one request to refresh the cache.

Other requests wait for the result.


---

## Randomized TTL

Instead of all keys expiring together:

Bad:

Every key expires at 12:00


Better:

Expire between 11:55 - 12:05


---

## Background Refresh

Refresh popular keys before they expire.


---

# 10. Hot Keys

A small number of keys receive enormous traffic.


Example:

World Cup final score.


Problem:

A single Redis node becomes overloaded.


Solutions:

- Replicate hot data
- Add local application cache
- Split hot keys
- Use CDN for static content


---

# 11. Redis High Availability


## Replication


           Primary
              |
       ----------------
       |              |
    Replica        Replica


Benefits:

- Read scaling
- Failover


---

## Sharding


Split keys across multiple nodes.


Example:


Shard 1:
Users 1-1M


Shard 2:
Users 1M-2M


Benefits:

- More memory
- More throughput


Challenges:

- Rebalancing
- Hot partitions


---

# 12. What Happens If Redis Goes Down?


Possible strategies:


## Fall back to Database

Application bypasses cache.

Risk:

Database overload.


---

## Use Circuit Breakers

Temporarily stop sending requests to unhealthy cache nodes.


---

## Graceful Degradation


Example:

Social media:

Return older feed data.

Disable less important features.


---

## Replication and Automatic Failover

Redis Sentinel or Redis Cluster can promote replicas.

---

# 13. L6 Interview Discussion


## Why not cache everything?

Because:

- Memory is expensive
- Some data is rarely used
- Data may become stale


---

## When should you use cache-aside?

Most read-heavy applications:

- User profiles
- Product information
- Configuration


---

## When should you use write-back?

When throughput matters more than immediate durability.

Examples:

- Analytics
- Counters


---

## How do you handle stale data?

Options:

- TTL
- Explicit invalidation
- Event-driven updates


---

## How do you protect the database when cache fails?

- Rate limiting
- Circuit breakers
- Request throttling
- Graceful degradation


---

# Key Takeaways

1. Caching improves latency and reduces backend load.

2. Cache-aside is the most common Redis pattern.

3. Different write strategies have different consistency and performance trade-offs.

4. TTL and invalidation strategies are required to prevent stale data.

5. Protect against cache stampedes and hot keys.

6. Redis uses replication and sharding to scale.

7. A cache failure should not cause a complete system outage.

---
# Chapter 5. Database Design and Storage Systems
---

# 1. Choosing the Right Database

There is no single best database.

Database selection depends on:

- Data structure
- Access patterns
- Read/write ratio
- Data volume
- Latency requirements
- Consistency requirements
- Availability requirements
- Scaling needs

The most important interview question is:

"What are my query patterns?"

A good database design starts with the queries the application needs to support.

---

# 2. Relational Databases (SQL)

Examples:

- MySQL
- PostgreSQL
- Oracle
- SQL Server

SQL databases store data in tables with predefined schemas.

Example:


User Table

UserId | Name | Email
----------------------
1      | John | john@gmail.com


Order Table

OrderId | UserId | Amount
--------------------------
100     | 1      | $50


Relationships are represented using foreign keys.

---

# 3. ACID Properties

Relational databases prioritize correctness using transactions.

## Atomicity

A transaction either completes entirely or not at all.

Example:

Money transfer:

Withdraw $100 from Account A.

Deposit $100 into Account B.

Both operations succeed or both fail.

---

## Consistency

A transaction moves the database from one valid state to another.

Example:

Total money in the system remains the same after a transfer.

---

## Isolation

Concurrent transactions should not interfere with each other.

Example:

Two users trying to buy the last product should not both succeed.

---

## Durability

Once a transaction is committed, the data survives failures.

Example:

Even if the database crashes after commit, the transaction is recovered.

---

# 4. Advantages of SQL Databases

- Strong consistency.
- Support complex joins.
- Transactions.
- Mature ecosystem.
- Powerful query language.

---

# 5. Limitations of SQL Databases

Traditional challenges:

- Vertical scaling limitations.
- Sharding is complex.
- Cross-shard joins are difficult.

However, modern relational databases can scale horizontally using:

- Read replicas.
- Partitioning.
- Sharding.
- Distributed SQL databases.

---

# 5.1 Read Replicas

## Introduction

As applications scale, database workloads often become heavily read-oriented. Examples include:

* User profile views.
* Product catalog searches.
* Dashboards and reports.
* Analytics queries.

A single primary database may become a bottleneck because it must handle both read and write traffic.

**Read replicas** are a common strategy to horizontally scale read capacity by creating additional read-only copies of the primary database.

---

# How Read Replicas Work

## Primary-Replica Architecture

The database architecture separates responsibilities:

* The **primary database** handles all write operations:

  * INSERT
  * UPDATE
  * DELETE

* One or more **read replicas** handle read-only queries:

  * SELECT statements

Changes made to the primary are replicated to the replicas.

Example:

```
                     Application
                           |
            --------------------------------
            |                              |
        Write Requests                 Read Requests
            |                              |
        Primary Database        -----------------------
                                 |                     |
                           Read Replica 1        Read Replica 2
```

---

## Replication Process

Most relational databases implement replication asynchronously.

Example:

```
User updates profile
          |
          v
     Primary Database
          |
          v
     Replication Log
          |
          v
     Read Replica
```

The primary commits the transaction immediately and sends the changes to replicas afterward.

This approach provides:

* Low write latency.
* Improved read scalability.

---

# Advantages of Read Replicas

## Horizontal Read Scaling

Additional replicas can be added to handle increasing read traffic.

Example:

```
Primary:
10,000 writes/sec

Single Primary:
20,000 reads/sec capacity


With 5 replicas:

Primary:
20,000 reads/sec

Replicas:
5 × 20,000 reads/sec

Total:
120,000 reads/sec
```

Read throughput can scale by adding more replicas.

---

## Offloading Expensive Queries

Long-running queries can be routed to replicas.

Examples:

* Reporting.
* Analytics.
* Dashboard generation.
* Large aggregations.

This prevents analytical workloads from affecting transactional operations on the primary.

---

## Native Database Support

Most relational databases support read replicas natively.

Examples:

* PostgreSQL streaming replication.
* MySQL replication.
* Amazon RDS Read Replicas.

Managed cloud databases can automatically provision and manage replicas.

---

# Challenges and Trade-Offs

## Replication Lag

Because replication is usually asynchronous, replicas may temporarily fall behind the primary.

Example:

```
Time T0:
User updates address

Time T1:
Application reads from replica

Result:
Old address returned
```

This is known as **stale reads**.

---

## Handling Read-After-Write Consistency

Some applications require users to immediately see their own changes.

Possible solutions:

### Read from Primary After a Write

Example:

```
Update Profile
      |
      |
Read from Primary
```

Ensures the latest data is returned.

---

### Sticky Reads

After a user performs a write operation, route that user's subsequent reads to the primary for a short period.

Example:

```
User writes data

Next 5 seconds:
Read from Primary

After replication catches up:
Read from Replica
```

---

## Write Scalability Limitation

Read replicas increase **read capacity**, but they do not improve write scalability.

All writes still go through a single primary database.

Example:

```
           Writes
             |
             v
        Primary Database
             |
        ----------------
        |              |
   Replica 1      Replica 2
```

If the primary reaches its CPU, memory, or storage limits, additional solutions are needed:

* Vertical scaling.
* Partitioning.
* Sharding.
* Database redesign.

---

# Read Routing Strategies

Applications must decide where to send database queries.

---

## Application-Level Routing

The application manages separate connections:

Example:

```
Application

Write Connection
       |
    Primary Database


Read Connection
       |
Randomly Selected Replica
```

Advantages:

* Full control over routing decisions.
* Can apply custom consistency rules.

Disadvantages:

* More complex application logic.
* Requires maintaining multiple connection pools.

---

## Database Proxy Routing

A proxy sits between the application and databases.

Example:

```
Application
      |
Database Proxy
      |
------------------------
|                      |
Primary            Read Replicas
```

The proxy automatically routes:

* INSERT, UPDATE, DELETE → Primary
* SELECT → Replicas

Examples:

* ProxySQL for MySQL.
* PgBouncer or similar connection routing solutions for PostgreSQL.

Advantages:

* Simplifies application code.
* Centralizes routing policies.

Disadvantages:

* Additional infrastructure component.
* Can become a bottleneck if not scaled properly.

---

# Failure Scenarios

## Replica Failure

If a replica becomes unavailable:

```
Replica 1 fails
       |
Traffic moves to other replicas
```

The system loses some read capacity but remains available.

---

## Primary Failure

If the primary database fails:

* A replica may be promoted to become the new primary.
* Applications must redirect write traffic to the new primary.

This process is called **failover**.

---

# L6 Interview Discussion

## When would you introduce read replicas?

Use read replicas when:

* Read traffic is significantly higher than write traffic.
* Analytical queries impact transactional workloads.
* A single database cannot handle read throughput.

---

## Why not always read from replicas?

Because replicas may lag behind the primary.

For workflows requiring strong consistency, reads may need to go to the primary.

Examples:

Strong consistency needed:

* Account balances.
* Recent profile updates.
* Payment confirmations.

Eventual consistency acceptable:

* News feeds.
* Product catalogs.
* Analytics dashboards.

---

## Do read replicas solve database scaling completely?

No.

Read replicas only scale **read throughput**.

The primary database remains a bottleneck for:

* Writes.
* Transaction processing.
* Schema changes.

At very large scale, additional techniques such as partitioning and sharding are required.

---

# Key Takeaways

1. Read replicas provide horizontal scalability for read-heavy workloads.

2. The primary handles writes, while replicas handle SELECT queries.

3. Replication is often asynchronous, which can lead to stale reads.

4. Read-after-write consistency can be achieved by temporarily routing reads to the primary.

5. Read replicas improve read scalability but do not solve write scalability.

6. Applications can route reads using application logic or database proxies.

7. At massive scale, read replicas are often combined with partitioning and sharding.


# 5.2 Partitioning vs Sharding

## Introduction

As databases grow to billions of records, storing all data in a single large table becomes inefficient. Two common techniques used to manage large datasets are **partitioning** and **sharding**.

Both techniques split data into smaller chunks, but they solve different problems:

* **Partitioning** improves data organization and query performance within a single database server.
* **Sharding** distributes data across multiple independent database servers to achieve horizontal scalability.

---

## Partitioning vs Sharding Comparison

| Feature                  | Partitioning                                                                          | Sharding                                                                                           |
| ------------------------ | ------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| **Location**             | Partitions are stored within the same database server.                                | Shards are distributed across multiple independent database servers.                               |
| **Primary Goal**         | Improve maintenance efficiency and query performance.                                 | Scale horizontally to handle massive data volume and traffic.                                      |
| **Architecture**         | Uses the resources (CPU, memory, disk) of a single machine.                           | Uses a shared-nothing architecture where each shard has its own CPU, memory, and storage.          |
| **Scalability**          | Limited by the capacity of a single server.                                           | Can scale by adding more database nodes.                                                           |
| **Complexity**           | Low. Usually supported natively by relational databases.                              | High. Requires shard routing, data distribution, rebalancing, and handling cross-shard operations. |
| **Joins & Transactions** | Normal joins and ACID transactions are easier because all data resides on one server. | Cross-shard joins and distributed transactions are difficult and expensive.                        |

---

# Partitioning

## What is Partitioning?

Partitioning divides a large database table into smaller physical pieces called **partitions** based on a partitioning strategy.

Common partitioning strategies include:

### Range Partitioning

Data is divided based on a range of values.

Example:

```
Orders Table

Partition 1:
Orders from 2022

Partition 2:
Orders from 2023

Partition 3:
Orders from 2024
```

---

### List Partitioning

Data is divided based on predefined categories.

Example:

```
Users Table

US Users
EU Users
Asia Users
```

---

### Hash Partitioning

A hash function determines which partition stores a record.

Example:

```
hash(UserId) % 4

0 → Partition A
1 → Partition B
2 → Partition C
3 → Partition D
```

---

## Advantages of Partitioning

### Partition Pruning

The database engine can ignore irrelevant partitions when executing a query.

Example:

A query asks:

```
SELECT * FROM Orders
WHERE OrderDate BETWEEN Jan 1, 2024 and Jan 31, 2024
```

The database only scans the 2024 partition instead of scanning years of historical data.

---

### Easier Maintenance

Operations such as:

* Archiving old data.
* Dropping old records.
* Backing up specific ranges.

become much easier.

---

## Limitations of Partitioning

Because all partitions remain on the same database server, they still share:

* CPU.
* Memory.
* Disk I/O.

Partitioning improves performance and manageability but **does not provide unlimited scalability**.

---

# Sharding

## What is Sharding?

Sharding is a form of horizontal partitioning where data is distributed across multiple independent database servers.

Each shard owns a subset of the total data.

Example:

```
User Database

Shard 1:
Users 1 - 10 Million

Shard 2:
Users 10 Million - 20 Million

Shard 3:
Users 20 Million - 30 Million
```

---

## How Requests Are Routed

A routing layer determines which shard contains the requested data.

Example:

```
Application
      |
Shard Router
      |
------------------------
|            |          |
Shard 1    Shard 2    Shard 3
```

Routing may be based on:

* User ID.
* Customer ID.
* Geographic region.
* Hash of a key.

---

## Advantages of Sharding

### Horizontal Scalability

Additional shards can be added as data volume and traffic grow.

Example:

A social media platform with billions of users can distribute user data across hundreds of database servers.

---

### Improved Throughput

Multiple shards can process requests in parallel.

Example:

```
1000 writes/sec on one server

becomes

10 shards × 1000 writes/sec = 10,000 writes/sec
```

---

### Fault Isolation

A failure in one shard affects only the data stored on that shard rather than the entire database.

---

## Challenges of Sharding

### Cross-Shard Queries

A query such as:

```
Find all users who purchased a product
```

may require querying many shards and combining the results.

---

### Data Rebalancing

As shards grow unevenly, data may need to be migrated between servers.

This process can be expensive and requires careful planning.

---

### Hot Shards

Poor shard key selection can create uneven traffic.

Example:

```
User ID 1 - 1000 receives 90% of traffic
```

That shard becomes overloaded while others remain underutilized.

---

### Distributed Transactions

Transactions involving multiple shards are complex and often require techniques such as:

* Saga patterns.
* Eventual consistency.
* Application-level coordination.

---

# How Partitioning and Sharding Work Together

Large-scale systems often combine both techniques.

Example:

```
Global Application

             |
        Sharding Layer
             |
--------------------------------
|                              |
US Database                 EU Database
|                              |
Local Partitions           Local Partitions
(by date)                  (by date)
```

A system may:

1. **Shard** data across multiple servers to achieve horizontal scalability.
2. **Partition** data within each shard to improve query performance and maintenance.

---

# L6 Interview Discussion

## When would you use partitioning?

Use partitioning when:

* A single database server can handle the workload.
* Tables are very large.
* Queries frequently access a subset of data.
* You want easier data lifecycle management.

---

## When would you use sharding?

Use sharding when:

* Data volume exceeds the capacity of a single machine.
* Read/write throughput exceeds what one server can handle.
* You need horizontal scaling.

---

## Why is sharding more difficult?

Because it introduces distributed system challenges:

* Request routing.
* Data rebalancing.
* Cross-shard queries.
* Distributed transactions.
* Hot partitions.

---

# Key Takeaways

1. Partitioning splits data within a single database server to improve performance and maintenance.

2. Sharding distributes data across multiple servers to achieve horizontal scalability.

3. Partitioning is simpler but limited by a single machine's resources.

4. Sharding provides massive scale but introduces operational and consistency challenges.

5. Large-scale systems often combine sharding and partitioning.


# 6. NoSQL Databases

NoSQL databases trade some relational features for:

- Horizontal scalability.
- Flexible schemas.
- High availability.
- Massive throughput.

Common categories:

1. Key-Value Databases
2. Document Databases
3. Column Family Databases
4. Graph Databases

---

# 7. Key-Value Databases

Examples:

- Redis
- DynamoDB

Data model:

Key → Value


Example:

user:123 → {
 name: "John",
 membership: "Gold"
}


Advantages:

- Extremely fast lookup.
- Simple horizontal scaling.

Use cases:

- Sessions.
- Caching.
- User preferences.
- Feature flags.

---

# 8. Document Databases

Examples:

- MongoDB.
- CouchDB.

Store JSON-like documents.

Example:


{
 userId: 123,
 name: "John",
 addresses: [
   "New York",
   "Boston"
 ]
}


Advantages:

- Flexible schema.
- Good for hierarchical data.
- Easy application mapping.

Use cases:

- Product catalogs.
- User profiles.
- Content management.

---

# 9. Column Family Databases

Examples:

- Cassandra.
- HBase.

Designed for:

- Massive scale.
- Very high write throughput.
- Distributed storage.

Example:

Twitter Tweet Table:

Partition Key:
UserId

Clustering Key:
Timestamp


User 123:

10:01 Tweet A

10:05 Tweet B

10:10 Tweet C


Advantages:

- Horizontal scaling.
- High write throughput.
- High availability.

Trade-offs:

- Limited joins.
- Data duplication is common.
- Query-driven schema design.

---

# 10. Graph Databases

Examples:

- Neo4j.
- Amazon Neptune.

Designed for relationships.

Example:

User A → follows → User B

Advantages:

- Efficient graph traversal.
- Relationship-heavy queries.

Use cases:

- Social networks.
- Recommendation systems.
- Fraud detection.

---

# 11. SQL vs NoSQL Trade-Off

| Feature | SQL | NoSQL |
|---|---|---|
| Schema | Fixed | Flexible |
| Transactions | Strong ACID | Usually limited |
| Joins | Excellent | Usually avoided |
| Scaling | More complex | Designed for horizontal scaling |
| Consistency | Strong | Often eventual consistency |
| Data model | Normalized | Often denormalized |
| Best for | Complex relationships | Massive scale |

---

# 12. Normalization vs Denormalization

## Normalization

Data is split into multiple related tables.

Example:

User Table:

UserId | Name


Order Table:

OrderId | UserId


Advantages:

- Less duplication.
- Easier updates.
- Better consistency.

Disadvantages:

- Requires joins.
- More expensive reads.

---

## Denormalization

Duplicate data to optimize reads.

Example:

Order:

{
 orderId: 100,
 userName: "John",
 address: "NY"
}


Advantages:

- Faster reads.
- Fewer joins.

Disadvantages:

- More storage.
- Data consistency challenges.

---
# 5.3 Query-Driven Schema Design (Query-First Data Modeling)

## Introduction

Traditional relational database design focuses on modeling real-world entities and minimizing data duplication through normalization.

However, at large scale, the primary challenge is often not storing data efficiently—it is retrieving the right data with very low latency.

Query-driven schema design (also known as **query-first** or **demand-driven design**) is a methodology where the schema is designed around the application's exact read and write access patterns.

Instead of asking:

```
What entities exist in the system?
```

we start with:

```
What queries must the system execute?
```

This approach is the foundation of many NoSQL databases such as:

* MongoDB
* Cassandra
* ScyllaDB

and modern API layers such as GraphQL.

---

# Traditional Design vs Query-Driven Design

| Feature               | Traditional Relational Design                       | Query-Driven Design                                  |
| --------------------- | --------------------------------------------------- | ---------------------------------------------------- |
| Primary Goal          | Minimize redundancy and maintain strict consistency | Maximize read/write efficiency and minimize latency  |
| Modeling Strategy     | Normalize entities into separate tables             | Denormalize data around access patterns              |
| Structure Orientation | Designed around business entities                   | Designed around application workflows and UI needs   |
| Joins                 | Common and optimized by relational databases        | Avoided because they may be expensive or unsupported |
| Storage Cost          | Lower storage, higher query complexity              | Higher storage, simpler and faster queries           |
| Schema Evolution      | Generally more stable                               | Changes as access patterns evolve                    |

---

# The Query-First Design Process

## Step 1: Identify Access Patterns

Before designing the schema, list all critical application queries.

Examples:

```
Load a user's profile page

Fetch the latest 20 posts from followed users

Retrieve an order and its line items

Display the top 10 comments for a post
```

For each query, understand:

* Query frequency.
* Required response latency.
* Filtering conditions.
* Sorting requirements.
* Read-to-write ratio.

A common L6 principle:

> Your database schema should serve your application's most important queries.

---

## Step 2: Choose the Right Primary Keys and Indexes

The primary key determines how data is distributed and retrieved.

In distributed databases, selecting a good partition key is critical.

A good partition key:

* Distributes traffic evenly.
* Avoids hot partitions.
* Supports common lookup patterns.

Examples:

Good:

```
UserId hash → Partition
```

Bad:

```
All events from today's date → Single partition
```

because all writes target the same location.

Additional clustering or sort keys can be used to organize data within a partition.

Example:

```
Partition Key:
UserId

Sort Key:
Timestamp DESC
```

This allows efficient queries such as:

```
Get the most recent 50 messages for a user
```

---

## Step 3: Apply Embedding and Denormalization

Data that is frequently read together should often be stored together.

### Traditional Relational Model

```
Post Table
     |
     |
Comments Table
     |
     |
Users Table
```

Loading a post page may require multiple joins.

---

### Query-Driven NoSQL Model

```
Post Document

{
  postId: 123,
  authorName: "Alice",
  content: "Hello World",
  topComments: [
    {
      user: "Bob",
      text: "Great post"
    }
  ]
}
```

A single database lookup can retrieve everything needed for the page.

---

# Query-Driven Design in APIs

The same philosophy applies to API design.

A good API should be designed around client needs rather than internal data models.

Example:

A mobile dashboard requires:

* User profile.
* Recent orders.
* Recommendations.
* Account metrics.

Instead of requiring four separate API calls:

```
Client
 |
 |-- User Service
 |
 |-- Orders Service
 |
 |-- Recommendation Service
 |
 |-- Metrics Service
```

A query-driven API layer such as GraphQL or a Backend-for-Frontend (BFF) can expose:

```
Dashboard Response
{
  profile,
  orders,
  recommendations,
  metrics
}
```

This reduces network round trips and improves client performance.

---

# Trade-Offs and Challenges

## Data Duplication

Denormalization means the same information may exist in multiple places.

Example:

```
User Name

Stored in:
- User document
- Post document
- Comment document
```

When the user changes their name, all copies must eventually be updated.

---

## More Complex Writes

Optimizing reads may require multiple write operations.

Example:

```
Update User Name

      |
      |
Update User Profile

      +
Update All Posts

      +
Update All Comments
```

This may require:

* Asynchronous updates.
* Event-driven architecture.
* Background jobs.

---

## Schema Evolution

If application workflows change, the schema may need to evolve.

Examples:

* New UI screens.
* Different sorting requirements.
* New query patterns.

Large systems often use:

* Backward-compatible schema changes.
* Lazy migrations.
* Background migration jobs.

---

# When Should You Use Query-Driven Design?

Use it when:

* You have well-known access patterns.
* Read latency is a top priority.
* The system operates at massive scale.
* Joins become expensive.
* You are using NoSQL or distributed databases.

---

# When Is Normalization Better?

Normalization is often preferred when:

* Data consistency is critical.
* Data relationships are complex.
* Write correctness is more important than read performance.
* The dataset comfortably fits within a relational database.

Examples:

* Banking systems.
* Payment ledgers.
* Inventory management.

---

# L6 Interview Discussion

## Why do large-scale systems denormalize data?

Because network calls and joins become expensive at scale.

Storage is relatively cheap, but latency and distributed coordination are expensive.

---

## What is the first step when designing a database?

Do not start with tables.

Start with:

```
Access Patterns
        |
        ↓
Primary Keys
        |
        ↓
Indexes
        |
        ↓
Schema Design
```

---

## Is denormalization always better?

No.

It improves read performance but introduces:

* Data duplication.
* More complex writes.
* Eventual consistency challenges.

Choose the approach based on business requirements.

---

# Key Takeaways

1. Query-driven design starts with application access patterns rather than entities.

2. Denormalization trades additional storage for lower query latency.

3. Partition keys and indexes should be chosen based on how data is retrieved.

4. Data that is frequently read together should often be stored together.

5. Modern NoSQL databases heavily rely on query-driven schema design.

6. The best database design is driven by workload and trade-offs, not a single universal rule.

# 13. Database Indexing

Indexes improve query performance.

Without an index:

Query:

SELECT * FROM users WHERE email='abc@gmail.com';


Database performs a full table scan.


With an index:

Email → Row Location


The database can directly locate the row.

---

## B-Tree Index

Most common index structure.

Advantages:

- Efficient range queries.
- Supports sorting.

Examples:

WHERE age > 30

ORDER BY createdTime

---

## Hash Index

Optimized for exact lookups.

Example:

WHERE userId = 100


Not suitable for range queries.

---

## Composite Index

Index on multiple columns.

Example:

(userId, timestamp)


Useful query:

WHERE userId = 10
AND timestamp > yesterday


Important rule:

Indexes are ordered.

(userId, timestamp)

supports:

✔ userId

✔ userId + timestamp


but not efficiently:

❌ timestamp only

---

# 14. Index Trade-Offs

Advantages:

- Faster reads.
- Faster filtering.

Disadvantages:

- Additional storage.
- Slower writes because indexes must be updated.

---

# 15. Replication

Replication creates copies of data across multiple servers.

---

## Primary-Replica Model


          Write
            |
         Primary
        /       \
   Replica    Replica


Reads can be distributed to replicas.

---

Benefits:

- Higher availability.
- Read scaling.
- Disaster recovery.

---

Challenges:

## Replication Lag

A replica may be behind the primary.

Example:

Write:

User status = Premium


Immediately read from replica:

User status = Basic


The system experiences eventual consistency.

---

# 16. Partitioning and Sharding

A single database cannot grow forever.

Partitioning splits data into smaller pieces.

---

## Horizontal Partitioning (Sharding)

Split rows.

Example:

Server A:

UserId 1 - 1M


Server B:

UserId 1M - 2M


Advantages:

- More storage.
- More throughput.

---

Challenges:

- Rebalancing.
- Cross-shard joins.
- Hot partitions.

---

## Vertical Partitioning

Split columns or services.

Example:

User Service Database:

UserId, Name, Email


Profile Service Database:

UserId, Preferences, Avatar

---

# 17. Choosing a Partition Key

One of the most important distributed database decisions.

A good partition key should:

- Distribute traffic evenly.
- Avoid hot partitions.
- Match query patterns.

---

Bad example:

Partition by country.

USA → 90% of traffic.


Good example:

Partition by userId.

Traffic spreads evenly.

---

# 18. Hot Partitions

Problem:

One partition receives significantly more traffic.

Example:

Celebrity user with 100 million followers.

All requests hit the same partition.

---

Solutions:

- Better partition keys.
- Add random suffixes.
- Caching.
- Replication.

---

# 19. Database Selection Examples

## Banking System

Requirements:

- Strong consistency.
- Transactions.

Choice:

MySQL / PostgreSQL.

---

## Twitter Tweets

Requirements:

- Massive writes.
- Horizontal scaling.

Choice:

Cassandra.

---

## User Sessions

Requirements:

- Extremely low latency.

Choice:

Redis.

---

## Product Catalog

Requirements:

- Flexible schema.

Choice:

MongoDB.

---

## Search Engine

Requirements:

- Full-text search.
- Ranking.

Choice:

Elasticsearch.

---

## Social Graph

Requirements:

- Relationship traversal.

Choice:

Graph Database.

---

# 20. L6 Interview Discussion

## Why Cassandra instead of MySQL?

Because:

- Write throughput is massive.
- Data can be partitioned naturally.
- Eventual consistency is acceptable.

Trade-off:

- Less flexible queries.
- Data duplication.

---

## Why not use one database for everything?

Because every database optimizes for different problems.

Example:

Twitter:

MySQL:
- User metadata.

Cassandra:
- Tweets.

Redis:
- Timeline cache.

Elasticsearch:
- Search.

S3:
- Media.

---

## How do you scale a database?

Techniques:

- Indexing.
- Read replicas.
- Caching.
- Sharding.
- Archival of old data.

---

## What happens when a shard becomes too large?

Solutions:

- Split the shard.
- Rebalance data.
- Use consistent hashing.
- Archive old data.

---

# Key Takeaways

1. Start with access patterns, not technology choices.

2. SQL databases are excellent for:
   - Transactions.
   - Relationships.
   - Strong consistency.

3. NoSQL databases are designed for:
   - Horizontal scaling.
   - Massive throughput.
   - Flexible data models.

4. Indexes improve reads but make writes more expensive.

5. Replication improves availability and read scaling.

6. Sharding increases capacity but introduces operational complexity.

7. The best systems usually use multiple databases, choosing each one based on its strengths.

# Chapter 6. Distributed Systems Fundamentals

# 1. What is a Distributed System?

A distributed system is a collection of independent computers that work together and appear to users as a single system.

Examples:

- Kafka clusters
- Cassandra clusters
- Redis clusters
- Kubernetes clusters
- Global databases

---

## Why Use Distributed Systems?

A single machine has limitations:

- CPU
- Memory
- Storage
- Network bandwidth
- Reliability

Distributed systems solve these limitations by using multiple machines.

---

## Advantages

### Scalability

Add more machines as traffic grows.

Example:

1 database server

becomes

100 database nodes


---

### High Availability

If one machine fails, other machines continue serving requests.

Example:

Multiple database replicas.

---

### Fault Tolerance

The system can continue operating even when components fail.

Example:

One Kafka broker fails but the cluster continues processing.

---

### Geographic Distribution

Data can be placed closer to users.

Example:

US users → US region

Europe users → Europe region


---

# 2. Challenges of Distributed Systems

Distributed systems introduce new challenges:

- Network failures
- Partial failures
- Replication lag
- Data consistency
- Clock synchronization
- Node failures
- Increased operational complexity

---

# 3. CAP Theorem

CAP states that during a network partition, a distributed system can guarantee at most two of the following:

- Consistency (C)
- Availability (A)
- Partition Tolerance (P)

---

## Consistency

Every client sees the same data at the same time.

Example:

A bank account balance update:

User A deposits $100.

Immediately afterwards:

User B reads the account.

They must see the updated balance.

---

## Availability

Every request receives a response.

The response may or may not contain the latest data.

Example:

A social media feed still loads even if some likes are slightly outdated.

---

## Partition Tolerance

The system continues operating even when communication between nodes fails.

Example:

Two database nodes cannot communicate due to a network outage.

The system must decide:

- Stop serving requests to maintain consistency.
- Continue serving requests and accept stale data.

---

# 4. Why Partition Tolerance Is Mandatory

In real distributed systems, network failures are unavoidable.

Therefore:

A distributed system must tolerate partitions.

The real CAP trade-off is:

CP vs AP

---

# 5. CP Systems

Prioritize:

Consistency + Partition Tolerance


During a partition:

The system rejects requests that cannot guarantee correctness.


Examples:

- ZooKeeper
- HBase
- Google Spanner (with additional mechanisms)


Good for:

- Banking
- Financial transactions
- Inventory systems


Example:

Two users attempt to buy the last airplane seat.

It is better to reject one request than oversell the seat.

---

# 6. AP Systems

Prioritize:

Availability + Partition Tolerance


During a partition:

The system continues accepting requests.

Data is synchronized later.

---

Examples:

- Cassandra
- DynamoDB


Good for:

- Social media feeds
- Likes
- Analytics data


Example:

A tweet like count may be temporarily inconsistent.

Eventually, all replicas converge.

---

# 7. Eventual Consistency

Data may be temporarily inconsistent but will eventually become consistent if no new updates occur.


Example:


Time 0:

Replica A:
Likes = 100


Replica B:
Likes = 95


After synchronization:


Replica A:
Likes = 100


Replica B:
Likes = 100


---

# 8. Strong Consistency

A read always returns the latest successful write.

Example:

ATM withdrawal.

After withdrawing money:

All ATMs should show the updated balance.

---

# 9. Other Consistency Models

## Read-Your-Writes Consistency

A user sees their own updates immediately.

Example:

You update your profile picture.

Refreshing your profile should show the new image.

---

## Monotonic Reads

Once a client sees newer data, they never see older data.

Example:

You read:

Version 5

Later reads should not return:

Version 4


---

## Causal Consistency

Related operations are observed in the same order.

Example:

A comment should not appear before the post it replies to.

---

# 10. PACELC Theorem

CAP discusses behavior during a partition.

PACELC extends CAP.

It asks:

Even when there is no partition, should the system choose:

Latency or Consistency?


---

P:
Partition occurs

Choose:

Availability or Consistency


ELSE:

Choose:

Latency or Consistency


---

Examples:


Cassandra:

AP during partitions

EL:

Low latency over strong consistency


---

Spanner:

CP during partitions

EC:

Consistency over lower latency


---

# 11. Replication

Replication maintains copies of data on multiple nodes.

---

Example:


               Primary
                  |
        --------------------
        |                  |
    Replica A          Replica B


---

## Advantages

- High availability
- Disaster recovery
- Read scaling


---

## Challenges

### Replication Lag

A replica may not immediately receive the latest update.

Example:

Primary:

Premium User = true


Replica:

Premium User = false


A read from the replica returns stale data.

---

# 12. Quorum-Based Replication

Many distributed databases use quorum.

Definitions:

N = Number of replicas

W = Number of replicas that must acknowledge a write

R = Number of replicas queried during a read


---

## Quorum Rule


R + W > N


This guarantees that reads and writes overlap.

---

## Example

N = 3


Choose:

W = 2

R = 2


Write:

Node A ✓

Node B ✓

Node C (down)


The write succeeds.

---

Read:

Read from A and B.

At least one replica has the latest value.

---

## Trade-offs


Higher W:

Advantages:
- More consistent writes

Disadvantages:
- Higher write latency


Higher R:

Advantages:
- More consistent reads

Disadvantages:
- Higher read latency

---

# 13. Leader-Follower Architecture

One node acts as the leader.

It receives writes.

Followers replicate data.


Example:


           Leader
              |
       ----------------
       |              |
   Follower        Follower


---

Advantages:

- Simpler conflict resolution.
- Strong consistency is easier.

Examples:

- MySQL replication
- PostgreSQL replication

---

# 14. Leader Election

If the leader fails, a new leader must be selected.

This prevents the system from becoming unavailable.


Examples:

- ZooKeeper
- Raft-based systems


---

## Split Brain Problem

A dangerous scenario where multiple nodes believe they are the leader.

Example:


Node A thinks:
"I am the leader."


Node B thinks:
"I am also the leader."


Both accept writes.

Result:

Conflicting data.


---

Solutions:

- Consensus protocols
- Majority voting
- Quorum


---

# 15. Heartbeats and Failure Detection

Nodes periodically send heartbeat messages.


Example:


Node A → "I am alive"

Node B → "I am alive"


If a heartbeat is missed repeatedly:

The node may be considered failed.


---

Used in:

- Kafka clusters
- Kubernetes
- Distributed databases


---

# 16. Checksums

Checksums detect data corruption during transmission or storage.


Example:


Data:

Hello


Checksum:

ABCD1234


When received:

Recalculate checksum.

If it differs:

The data is corrupted.


---

Used in:

- File systems
- Network protocols
- Distributed storage


---

# 17. L6 Interview Discussion

## Why can't a distributed system have CA?

Because network partitions can always occur.

When a partition happens, the system must choose:

Consistency or Availability.

---

## Why would you choose AP?

When availability is more important than perfectly fresh data.

Examples:

- Social feeds
- Analytics
- Counters

---

## Why would you choose CP?

When correctness is more important.

Examples:

- Banking
- Payments
- Inventory

---

## What is the downside of replication?

- More storage
- Replication lag
- Conflict resolution complexity


---

## How do you detect a failed node?

Using:

- Heartbeats
- Health checks
- Timeouts


---

## Why is quorum useful?

It allows tuning between:

- Consistency
- Latency
- Availability


---

# Key Takeaways

1. Distributed systems provide scalability and fault tolerance but introduce consistency challenges.

2. CAP only applies during network partitions.

3. Real systems usually choose between CP and AP.

4. PACELC explains the latency vs consistency trade-off when the network is healthy.

5. Replication improves availability but introduces synchronization challenges.

6. Quorum (R + W > N) provides a tunable consistency mechanism.

7. Leader election prevents systems from becoming unavailable after failures.

8. Heartbeats and checksums help maintain system health and integrity.

---
# Chapter 7. Data Consistency and Distributed Transactions
---

# 1. Why Do We Need Data Consistency Patterns?

In a monolithic application, maintaining consistency is relatively simple.

Example:

```
Create Order

BEGIN TRANSACTION

Insert Order
Update Inventory
Charge Payment

COMMIT
```

If any step fails:

```
ROLLBACK
```

The database guarantees ACID properties.

---

In distributed systems, the data may be spread across:

```
Order Service
      |
 Order Database


Inventory Service
      |
 Inventory Database


Payment Service
      |
 Payment Database
```

A single database transaction cannot easily span multiple services.

Therefore, distributed systems often trade strict consistency for:

- Availability
- Scalability
- Fault tolerance

---

# 2. Consistency Models

Different systems require different consistency guarantees.

---

## Strong Consistency

After a successful write, every read receives the latest value.

Example:

```
Account Balance = $1000

Withdraw $100

Immediately read balance

Result:
$900
```

---

Advantages:

- Simple mental model.
- No stale reads.

---

Disadvantages:

- Higher latency.
- Lower availability during failures.

---

Examples:

- Banking transactions.
- Payment systems.

---

## Eventual Consistency

Updates propagate over time.

For a short period:

```
Replica A: $900

Replica B: $1000
```

Eventually:

```
Replica A: $900
Replica B: $900
```

---

Advantages:

- High availability.
- Better scalability.
- Lower latency.

---

Examples:

- Social media feeds.
- Product recommendations.
- Analytics.

---

# 3. Session Consistency Models

Sometimes full strong consistency is unnecessary.

A system can provide guarantees for a single user.

---

## Read-Your-Writes Consistency

A user should immediately see their own updates.

Example:

User updates profile picture.

Expected:

```
Upload new picture

Refresh profile

See new picture
```

Not:

```
See old picture for 30 seconds
```

---

## Monotonic Reads

A user should not observe time moving backwards.

Example:

Reads:

```
Version 5
```

Later:

```
Version 4
```

This should never happen.

---

## Causal Consistency

Related operations should be observed in the correct order.

Example:

User posts:

```
I got promoted!
```

Then comments:

```
Thank you everyone!
```

It would be confusing to see:

```
Thank you everyone!
```

before the original post.

---

# 4. Distributed Transactions

The challenge:

```
Create Order

Reserve Inventory

Charge Payment

Create Shipment
```

What happens if payment fails after inventory was reserved?

---

Traditional solution:

```
Two-Phase Commit (2PC)
```

---

# 5. Two-Phase Commit (2PC)

A coordinator manages all participants.

---

## Phase 1: Prepare

Coordinator asks:

```
Can you commit?
```

Services respond:

```
YES
```

or:

```
NO
```

---

## Phase 2: Commit

If all services agree:

```
COMMIT
```

Otherwise:

```
ROLLBACK
```

---

## Problems with 2PC

Although it provides strong consistency, it has drawbacks:

- Slow due to coordination.
- A failed coordinator can block progress.
- Reduced availability.
- Difficult to scale.

Because of these trade-offs, large-scale systems usually avoid 2PC.

---

# 6. Saga Pattern

A Saga breaks a large transaction into a sequence of smaller local transactions.

Example:

```
Order Service

Create Order
      |
      v
Inventory Service

Reserve Inventory
      |
      v
Payment Service

Charge Payment
```

Each service commits its own database transaction.

---

## Failure Handling

Example:

```
Order Created ✔

Inventory Reserved ✔

Payment Failed ✘
```

The system executes compensating actions:

```
Release Inventory

Cancel Order
```

---

# 7. Saga Choreography

Services communicate through events.

Example:

```
Order Created Event
          |
          v
Inventory Service
          |
Inventory Reserved Event
          |
          v
Payment Service
```

---

## Advantages

- No central coordinator.
- Services remain loosely coupled.

---

## Disadvantages

- Event flow can become difficult to understand.
- Debugging complex workflows is harder.

---

# 8. Saga Orchestration

A dedicated service controls the workflow.

Example:

```
                Saga Orchestrator
                         |
 ------------------------------------------------
 |                      |                       |
Order Service      Inventory Service      Payment Service
```

The orchestrator decides:

- Which service to call.
- What to do on failures.
- Which compensation actions to execute.

---

## Advantages

- Centralized workflow.
- Easier monitoring.
- Easier debugging.

---

## Disadvantages

- Additional component to maintain.
- Orchestrator can become a bottleneck if poorly designed.

---

# 9. Transactional Outbox Pattern

A very common problem in event-driven systems.

Example:

```
Order Service

Step 1:
Update Order Database ✔

Step 2:
Publish Kafka Event ✘
```

Result:

Database says:

```
Order Created
```

But other services never receive the event.

The system becomes inconsistent.

---

# 10. Outbox Solution

Write both the business data and the event in the same database transaction.

Example:

```
Database Transaction

-------------------------
Order Table
-------------------------
Order 123 CREATED


-------------------------
Outbox Table
-------------------------
OrderCreated Event
-------------------------

COMMIT
```

Now either:

- Both succeed.
- Both fail.

---

# 11. Publishing Outbox Events

A separate process reads the outbox table.

Example:

```
Outbox Table
      |
      |
 Outbox Publisher
      |
      |
 Kafka Topic
```

---

The publisher:

- Reads new events.
- Publishes them to Kafka.
- Marks them as processed.

---

# 12. Change Data Capture (CDC)

Instead of polling the outbox table, many systems read database transaction logs.

Example:

```
Database
     |
Transaction Log
     |
Debezium
     |
Kafka
```

---

Benefits:

- Lower database load.
- Near real-time streaming.
- Reliable event propagation.

---

Common use cases:

- Event publishing.
- Search indexing.
- Data replication.
- Analytics pipelines.

---

# 13. Idempotent Consumers

Messaging systems often provide at-least-once delivery.

A consumer may receive:

```
OrderCreated Event
```

multiple times.

Example:

```
Message Processed

Consumer crashes before offset commit

Kafka sends message again
```

---

Consumers must handle duplicates.

---

## Techniques

### Store Processed Message IDs

Example:

```
processed_messages

message_id
----------
12345
67890
```

If a duplicate arrives:

```
Ignore it.
```

---

### Use Idempotent Updates

Good:

```
Set Order Status = SHIPPED
```

Running multiple times has the same effect.

---

Bad:

```
Increase Account Balance by $100
```

Running multiple times causes incorrect results.

---

# 14. Exactly Once Processing

Exactly-once is difficult in distributed systems.

It generally requires coordination between:

- Message processing.
- State changes.
- Offset commits.

---

Kafka provides features such as:

- Idempotent producers.
- Transactions.

However, exactly-once guarantees usually apply within Kafka workflows and still require careful application design.

---

# 15. Event Ordering

Some workflows require ordering.

Example:

```
Account Created

Account Updated

Account Deleted
```

Processing:

```
Delete
Update
Create
```

would be incorrect.

---

Solutions:

- Use the same partition key.
- Process a partition sequentially.
- Use version numbers.

---

# 16. L6 Interview Discussion

## Why not use 2PC?

Because it introduces:

- High latency.
- Blocking behavior.
- Lower availability.

Modern large-scale systems often prefer Saga patterns.

---

## How do you keep database and Kafka consistent?

Use the Transactional Outbox Pattern:

```
Database Transaction

Business Data
+
Outbox Event
```

Then publish asynchronously using CDC.

---

## Why must Kafka consumers be idempotent?

Because failures can cause messages to be delivered more than once.

---

## When is eventual consistency acceptable?

Examples:

Acceptable:

- Social feeds.
- Recommendations.
- Analytics.

Less acceptable:

- Financial transactions.
- Account balances.

---

# Key Takeaways

1. Distributed systems often choose eventual consistency to achieve higher scalability and availability.

2. Strong consistency is necessary only for critical workflows.

3. Sagas manage distributed transactions using local transactions and compensation.

4. Choreography is decentralized; orchestration provides centralized control.

5. The Transactional Outbox Pattern keeps databases and Kafka events consistent.

6. CDC tools such as Debezium stream database changes reliably.

7. Consumers must be idempotent because duplicate messages can occur.

8. Exactly-once processing is possible only with careful coordination.

9. Ordering guarantees depend on partitioning strategy.

# Chapter 8: Traffic Control and Resilience Engineering
# Part 1: Why Traffic Control & Controlled Concurrency

---

# 1. Why Do We Need Traffic Control?

Large distributed systems depend on many downstream components:

- Databases
- External vendor APIs
- Internal microservices
- Message queues

Every dependency has a finite capacity.

Example:

Vendor API capacity:

```
500 requests/sec
```

Our service receives:

```
5000 requests/sec
```

Without protection:

```
Incoming Requests (5000/sec)
             |
          Service
             |
     Vendor API (500/sec capacity)
```

Result:

- Increased latency
- Request timeouts
- Thread exhaustion
- Retry storms
- Cascading failures

Traffic control prevents one overloaded dependency from causing the entire system to fail.

---

# 2. Controlled Concurrency

## The Problem

Rate is not the only problem.

A dependency may not have a strict requests-per-second limit, but it can only process a certain number of requests simultaneously.

Example:

Vendor can handle:

```
50 concurrent requests
```

Our application can create:

```
200 Tomcat threads
```

Without concurrency control:

```
200 Application Threads
            |
       200 Vendor Calls
            |
      Vendor Overloaded
```

Even if the overall request rate is reasonable, too many parallel requests can overwhelm the dependency.

---

# 3. Solution: Limit Concurrent Requests

The goal is:

> Match the number of concurrent requests to the downstream system's actual capacity.

Common approaches:

- Thread pools
- Semaphores
- Bulkheads

---

# 4. Thread Pool

A thread pool provides execution capacity.

It maintains a fixed number of worker threads that execute tasks.

Example:

```
Incoming Work
       |
Bounded Queue
       |
Worker Thread Pool (100 threads)
       |
External Dependency
```

Only 100 tasks can execute at the same time.

Additional tasks:

- Wait in the queue.
- Are rejected when the queue reaches capacity.

---

## Java Example

```java
ExecutorService executor =
    Executors.newFixedThreadPool(100);
```

---

## Advantages

- Prevents creating unlimited threads.
- Controls CPU and memory usage.
- Provides predictable throughput.
- Allows asynchronous processing.

---

# 5. Semaphore / Bulkhead

A semaphore does not create threads.

A semaphore controls access to a constrained resource.

Example:

Vendor capacity:

```
50 concurrent calls
```

Semaphore:

```
50 permits
```

Flow:

```
Application Threads
        |
Acquire Semaphore Permit
        |
     Available?
     /        \
   Yes        No
    |          |
Call Vendor  Wait / Timeout / Reject
    |
Release Permit
```

Only 50 calls can be active at a time.

---

## Java Example

```java
Semaphore semaphore = new Semaphore(50);

if (semaphore.tryAcquire(500, TimeUnit.MILLISECONDS)) {
    try {
        callVendor();
    } finally {
        semaphore.release();
    }
} else {
    throw new VendorBusyException();
}
```

---

# 6. Thread Pool vs Semaphore

Although both limit concurrency, they solve different problems.

| Thread Pool | Semaphore |
|---|---|
| Provides execution capacity | Controls access to a resource |
| Creates and manages worker threads | Does not create threads |
| Protects application resources | Protects downstream dependencies |
| Often uses a bounded queue | Requests wait, timeout, or fail when permits are unavailable |
| Useful for asynchronous processing | Useful for external APIs, databases, and limited resources |

---

# 7. Execution Model Matters: Where Do Threads Come From?

A semaphore does not provide threads.

The existing execution model provides the threads.

---

## Synchronous REST API

Spring Boot with Tomcat:

```
HTTP Request
      |
Tomcat Thread Pool
      |
Controller
      |
Service
      |
Semaphore (50 permits)
      |
External Vendor API
```

Tomcat already provides request threads.

Example:

```
Tomcat Threads:
200

Vendor Capacity:
50 concurrent calls
```

Without semaphore:

```
200 Tomcat Threads
        |
200 Vendor Calls
        |
Vendor Overloaded
```

With semaphore:

```
200 Tomcat Threads
        |
Semaphore (50)
        |
50 Vendor Calls
```

The remaining requests may:

- Wait for a permit.
- Timeout and fail fast.
- Return HTTP 429 or HTTP 503.

---

## Kafka Consumer Processing

Kafka consumers also provide execution threads.

Example:

```
Kafka Topic
      |
Consumer Threads
      |
Business Processing
      |
Semaphore (50)
      |
External Vendor API
```

The semaphore protects the vendor while allowing consumers to process messages concurrently.

---

# 8. When Should We Create Worker Threads?

A separate worker pool is useful when we want to decouple accepting work from processing.

Example:

```
Kafka Consumer
      |
Bounded Queue
      |
Worker Thread Pool
      |
External Vendor API
```

Reasons:

- Keep Kafka polling responsive.
- Apply different concurrency limits to different stages.
- Absorb short traffic spikes.
- Isolate slow operations.

Example:

```
Kafka Consumers:
10 threads

Processing Workers:
100 threads

Vendor Capacity:
20 concurrent calls
```

Each stage has an independent concurrency boundary.

---

# 9. What Happens When the Concurrency Limit Is Reached?

This is an important design decision.

## Synchronous APIs

Possible choices:

- Wait briefly for a permit.
- Timeout and fail fast.
- Return HTTP 429 or HTTP 503.

Example:

```
Request #51 arrives
        |
No semaphore permit available
        |
Wait 500ms
        |
No permit?
        |
Return 429/503
```

---

## Asynchronous Systems

Examples:

- Kafka
- Batch processing

Usually we do not reject work immediately.

Options:

```
Kafka Topic
      |
Consumer
      |
Bounded Queue
      |
Worker Threads
      |
Semaphore
      |
Vendor
```

The work can remain buffered and processed later at a controlled rate.

---

# 10. Why Not Simply Add More Threads?

More threads only help if the downstream dependency has additional capacity.

Example:

```
Application:
500 threads

Vendor:
50 concurrent requests
```

Adding more threads leads to:

- More waiting.
- More memory usage.
- More context switching.
- More timeouts.
- More pressure on the dependency.

The correct approach is to match concurrency to the actual capacity of the downstream system.

---

# L6 Interview Soundbites

## How did you implement concurrency control?

> "It depends on the execution model. In a synchronous Spring Boot API, I use existing Tomcat request threads and place a semaphore or bulkhead around the downstream call. In Kafka or batch systems, consumer or worker threads provide execution capacity while semaphores limit the pressure applied to external dependencies."

---

## Why use a semaphore instead of a thread pool?

> "A thread pool controls how much work my application can execute. A semaphore controls how much pressure my application applies to a constrained dependency. They solve different bottlenecks."

---

## What was your production experience?

> "In our screening platform, the service could process more requests than the external vendor could handle. We introduced a concurrency limit using a semaphore-based bulkhead to ensure only a fixed number of requests were in flight. This protected the vendor and improved overall system stability."

---

# Key Takeaways

1. Downstream systems have limited concurrent capacity.

2. Concurrency control matches system throughput to dependency capacity.

3. Thread pools provide execution capacity.

4. Semaphores and bulkheads protect downstream resources.

5. The source of threads depends on the execution model:
   - Tomcat request threads.
   - Kafka consumer threads.
   - Batch worker threads.

6. The most important design decision is what happens when capacity is exhausted:
   - Wait.
   - Timeout.
   - Reject.
   - Queue for later.

7. More threads are not always better. The bottleneck usually moves to the downstream dependency.

---

# Chapter 8: Traffic Control and Resilience Engineering
# Part 2: Rate Limiting

---

# 1. What Is Rate Limiting?

Rate limiting controls how many requests are allowed during a period of time.

The key question is:

> "How many requests per second/minute am I willing to accept?"

It protects a system from excessive traffic and prevents overload.

---

## Example

A vendor contract allows:

```
500 requests/second
```

Our service receives:

```
5,000 requests/second
```

Without rate limiting:

```
Incoming Requests
        |
      Service
        |
Vendor API (500 req/sec capacity)
```

Result:

- Vendor throttling.
- Increased latency.
- Timeouts.
- Cascading failures.

---

With rate limiting:

```
Incoming Requests (5000/sec)
             |
       Rate Limiter (500/sec)
             |
          Service
             |
       Vendor API
```

The service allows only the permitted rate.

Excess requests may:

- Receive HTTP 429 (Too Many Requests).
- Be queued temporarily.
- Retry later with exponential backoff.

---

# 2. Rate Limiting vs Concurrency Control

These are related but solve different problems.

## Concurrency Control

Question:

> "How many requests can be active at the same time?"

Example:

```
Vendor supports:
50 simultaneous requests
```

Solution:

```
Semaphore:
50 permits
```

---

## Rate Limiting

Question:

> "How many requests can occur over a period of time?"

Example:

```
500 requests/second
```

Even if every request completes instantly, we should not exceed that rate.

---

## Comparison

| Aspect | Rate Limiting | Concurrency Control |
|---|---|---|
| Controls | Requests over time | Active simultaneous requests |
| Example | 500 requests/sec | 50 concurrent requests |
| Common tools | Token bucket, sliding window | Semaphore, bulkhead |
| Protects | Service capacity or contractual limits | Downstream connection capacity |

---

# 3. Where Is Rate Limiting Applied?

Rate limiting can exist at multiple layers.

---

## API Gateway

Most common location.

Example:

```
Client
  |
API Gateway
  |
Rate Limiter
  |
Microservices
```

Use cases:

- Protect backend services.
- Prevent abuse.
- Enforce tenant quotas.

---

## Application Level

Example:

```
Service
   |
Rate Limiter
   |
External Vendor API
```

Use cases:

- Vendor contracts.
- Third-party API quotas.
- Downstream protection.

---

## User-Level Rate Limiting

Example:

```
Free Users:
100 requests/hour

Premium Users:
10,000 requests/hour
```

This enables different service tiers.

---

# 4. Fixed Window Algorithm

The simplest approach.

Example:

```
Limit:
500 requests per second
```

Window:

```
12:00:00 - 12:00:01
```

Allow:

```
500 requests
```

---

## Problem: Burst at Window Boundaries

Example:

```
12:00:00.999
500 requests accepted

12:00:01.001
500 more requests accepted
```

Effectively:

```
1000 requests in a few milliseconds
```

Disadvantage:

- Traffic spikes at window boundaries.

---

# 5. Sliding Window Algorithm

Instead of fixed boundaries, maintain a moving time window.

Example:

```
Current time:
12:00:30.500

Count requests between:

12:00:29.500
        |
12:00:30.500
```

The system always looks at the previous one second.

---

## Advantages

- More accurate.
- Prevents boundary bursts.
- Provides smoother limiting.

---

## Disadvantages

- More memory.
- More computation.

---

# 6. Token Bucket Algorithm

The most common rate limiting strategy.

---

## Request Flow

```
Request
   |
Token available?
    |
  Yes ----> Allow request
    |
  No
    |
Reject / Delay / Queue
```

---

## Token Bucket Algorithm

Token Bucket is the most commonly used rate limiting strategy.

The system maintains a bucket containing tokens.

Each request consumes one token.

Two parameters control its behavior:

```
Bucket Capacity:
Maximum number of tokens that can be stored.

Refill Rate:
Number of new tokens added per second.
```

---

### Example Configuration

```
Bucket Capacity: 1000 tokens

Refill Rate: 200 tokens/second

Each Request:
Consumes 1 token
```

---

## Normal Traffic

Assume normal traffic is:

```
100 requests/second
```

The bucket receives:

```
+200 tokens/second
```

The application consumes:

```
-100 tokens/second
```

Unused capacity accumulates:

```
+100 tokens/second saved
```

Example:

```
Second 1: 300 tokens
Second 2: 400 tokens
Second 3: 500 tokens
...
Maximum: 1000 tokens
```

The bucket never grows beyond its configured capacity.

---

## Handling a Sudden Burst

Assume the bucket has accumulated:

```
1000 tokens
```

A user suddenly sends:

```
800 requests instantly
```

Result:

```
800 tokens consumed

200 tokens remain
```

All 800 requests are allowed immediately.

---

## Another Burst Immediately After

Another:

```
500 requests arrive
```

Available tokens:

```
200
```

Result:

```
200 requests allowed

300 requests rejected, delayed, or asked to retry later
```

---

## Why Token Bucket Is Popular

It separates two important concepts:

### 1. Refill Rate → Long-Term Average Throughput

Example:

```
200 tokens/second
```

Meaning:

> Over a long period, the client can sustain approximately 200 requests per second.

---

### 2. Bucket Capacity → Maximum Burst Capacity

Example:

```
1000 tokens
```

Meaning:

> The system can tolerate a short burst of up to 1000 requests if enough tokens have accumulated.

---

## Trade-off: Why Not Make the Bucket Very Large?

A larger bucket allows bigger bursts.

Example:

```
Bucket Capacity: 100,000 tokens
```

A client may suddenly send:

```
100,000 requests instantly
```

This could overwhelm:

* Databases.
* External APIs.
* Microservices.

Therefore, the bucket size should be chosen based on the amount of burst traffic the downstream systems can safely tolerate.

---

## L6 Interview Soundbite

"Token Bucket separates average throughput from burst capacity. The refill rate controls the long-term request rate, while the bucket capacity controls how much short-term burst traffic the system can absorb."


---

Token bucket allows controlled bursts by accumulating unused tokens up to a configured bucket capacity. The refill rate controls the long-term average throughput, while the bucket size controls the maximum burst that the system can tolerate.

## Advantages

- Handles normal traffic efficiently.
- Allows short bursts.
- Simple to implement.

---

## Disadvantages

- Long bursts can still exceed downstream capacity.
- Often combined with concurrency limits.

---

# 7. Leaky Bucket Algorithm

Leaky bucket smooths outgoing traffic.

Imagine a bucket with a small hole.

Requests enter quickly:

```
Incoming Traffic
        |
      Bucket
        |
 Constant Output Rate
```

Requests leave at a fixed speed.

---

## Example

Incoming:

```
5000 requests instantly
```

Output:

```
500 requests/sec
```

---

## Advantages

- Provides smooth traffic.
- Protects fragile downstream systems.

---

## Disadvantages

- Does not allow bursts.
- Requests may wait in the queue.

---

# 8. Token Bucket vs Leaky Bucket

| Aspect | Token Bucket | Leaky Bucket |
|---|---|---|
| Traffic behavior | Allows bursts | Smooths traffic |
| Token accumulation | Yes | No |
| Queueing | Usually not required | Usually uses a queue |
| Best for | User APIs, gateways | Protecting strict downstream capacity |

---

# 9. Choosing a Rate Limiting Algorithm

## Fixed Window

Use when:

- Simplicity matters.
- Minor bursts are acceptable.

---

## Sliding Window

Use when:

- Accurate limits are important.
- Bursts must be minimized.

---

## Token Bucket

Use when:

- Traffic is usually steady.
- Short bursts are acceptable.

Examples:

- Public APIs.
- API gateways.
- User quotas.

---

## Leaky Bucket

Use when:

- Downstream systems need a steady flow.
- Sudden bursts are dangerous.

Examples:

- Payment processors.
- Legacy systems.
- Slow external vendors.

---

# 10. Combining Rate Limiting with Other Controls

Rate limiting is only one layer of protection.

A production system may combine:

```
Incoming Requests
        |
Rate Limiter
        |
Application Threads
        |
Semaphore/Bulkhead
        |
External Dependency
```

Example:

Vendor contract:

```
500 requests/sec
```

Vendor capacity:

```
50 concurrent requests
```

We may enforce both:

```
Rate Limiter:
500 req/sec

Semaphore:
50 concurrent calls
```

This ensures we respect both:

- Throughput limits.
- Concurrent connection limits.

---

# 11. L6 Interview Soundbites

## Why do we need rate limiting?

"Rate limiting protects services from excessive traffic, prevents abuse, enforces quotas, and helps maintain predictable system behavior."

---

## Why not only use a semaphore?

"A semaphore controls how many requests are active simultaneously, but it does not enforce requests per second. If a vendor allows only 500 requests per second, a concurrency limit alone is not sufficient."

---

## Which algorithm would you choose?

"Token bucket is my default choice because it is simple and allows controlled bursts. For strict traffic smoothing, I would choose a leaky bucket."

---

## Can rate limiting replace backpressure?

"No. Rate limiting controls incoming traffic, while backpressure handles situations where consumers cannot keep up with producers."

---

# Key Takeaways

1. Rate limiting controls request volume over time.

2. Concurrency control limits active simultaneous operations.

3. Token bucket is the most commonly used algorithm.

4. Sliding window provides more accurate limits.

5. Leaky bucket provides a constant outgoing rate.

6. Production systems often combine rate limiting with concurrency limits and other resilience mechanisms.

# Chapter 8: Traffic Control and Resilience Engineering
# Part 3: Backpressure, Bounded Queues & Overload Management

---

# 1. What is Backpressure?

Backpressure is a mechanism that prevents a fast producer from overwhelming a slower consumer.

The consumer or system effectively signals:

> "I cannot process work at your current rate. Slow down, buffer temporarily, or stop sending more work."

---

## Example: Kafka Producer vs Consumer

Producer rate:

```
100,000 messages/sec
```

Consumer capacity:

```
10,000 messages/sec
```

Architecture:

```
Producer
    |
Kafka Topic
    |
Consumer
```

The consumer cannot keep up.

---

Without any control:

```
Producer:
100,000 messages/sec

Consumer:
10,000 messages/sec
```

Difference:

```
90,000 messages/sec accumulate
```

Result:

```
Consumer lag continuously increases
```

Messages wait longer before being processed.

---

# 2. Why Is Backpressure Needed?

Every system has finite resources:

- CPU
- Memory
- Threads
- Database connections
- Network bandwidth

If incoming work exceeds processing capacity:

- Queues grow.
- Latency increases.
- Memory consumption increases.
- Timeouts occur.
- Failures cascade to other systems.

The goal is not to process unlimited traffic.

The goal is:

> Keep the system stable under overload.

---

# 3. Bounded vs Unbounded Queues

Queues are a common way to absorb traffic spikes.

However, queues are not free.

---

## Unbounded Queue Problem

Example:

```
Producer:
100,000 requests/sec

Consumer:
10,000 requests/sec
```

Accumulation:

```
90,000 requests/sec waiting
```

After:

### 1 second

```
90,000 requests waiting
```

### 10 seconds

```
900,000 requests waiting
```

### 1 minute

```
5.4 million requests waiting
```

If each request consumes:

```
1 KB memory
```

Then:

```
5.4 million × 1 KB ≈ 5.4 GB memory
```

Result:

```
Queue growth
      |
Memory exhaustion
      |
OutOfMemoryError
      |
Service crash
```

---

## Bounded Queue

A bounded queue has a maximum capacity.

Example:

```
Queue capacity:
10,000 requests
```

Architecture:

```
Producer
    |
Bounded Queue
    |
Consumer
```

---

When the queue becomes full:

Possible strategies:

- Reject new requests.
- Return HTTP 429.
- Retry later.
- Drop lower-priority work.
- Apply load shedding.

---

## Why Not Create a Very Large Queue?

A huge queue does not remove overload.

It only hides it.

Example:

```
Queue size:
10 million requests
```

Result:

```
Request enters queue
        |
Waits 5 minutes
        |
Gets processed
```

The system appears available but has unacceptable latency.

This problem is called:

```
Bufferbloat
```

---

# 4. Common Backpressure Strategies

## Strategy 1: Reject Requests

Fail fast when the system has no capacity.

Example:

```
HTTP 429 Too Many Requests
```

Advantages:

- Protects memory.
- Protects worker threads.
- Provides predictable behavior.

---

## Strategy 2: Slow Down Producers

The producer reduces the amount of work it sends.

Examples:

- Reduce Kafka producer throughput.
- Use flow control.
- Apply rate limiting.

Example:

```
Consumer:
I can process 1,000 messages.

Producer:
Sends only 1,000 messages.
```

---

## Strategy 3: Scale Consumers

Increase processing capacity.

Example:

Kafka consumer group:

```
Partition 1 → Consumer A
Partition 2 → Consumer B
Partition 3 → Consumer C
```

Advantages:

- Reduces consumer lag.
- Increases throughput.

---

## Limitation

Scaling consumers has limits.

Example:

```
Kafka Partitions:
3

Consumers:
10
```

Only:

```
3 consumers are active.
```

The remaining consumers are idle.

Maximum parallelism is limited by the number of partitions.

---

## Strategy 4: Load Shedding

Intentionally discard less important work.

Examples:

Drop:

- Analytics events.
- Debug logs.
- Recommendation updates.

Preserve:

- Payments.
- Orders.
- Critical transactions.

---

# 5. Kafka vs In-Memory Queues

This is a common interview discussion.

---

## Kafka

Architecture:

```
Producer
    |
Kafka Topic
    |
Consumer
```

When consumers are slow:

```
Consumer lag increases.
```

Messages remain:

- Durable.
- Stored on disk.
- Available for replay.

Kafka can absorb large spikes over longer periods.

---

## In-Memory Queue

Example:

```
Application
    |
In-memory Queue
    |
Worker Threads
```

Characteristics:

- Very fast.
- Low latency.
- Uses application memory.
- Must be bounded.

It is suitable for short-term buffering.

---

# 6. Combining Kafka with Internal Backpressure

A common production design:

```
Kafka Topic
      |
Consumer Threads
      |
Bounded Processing Queue
      |
Worker Thread Pool
      |
Semaphore/Bulkhead
      |
External Dependency
```

Purpose of each layer:

| Component | Purpose |
|---|---|
| Kafka | Durable long-term buffer |
| Consumer Threads | Read messages continuously |
| Bounded Queue | Absorb short spikes and protect memory |
| Worker Pool | Control processing capacity |
| Semaphore/Bulkhead | Protect downstream dependency |

---

# Advanced Design Consideration: Do We Always Need Another Queue?

A queue should not be added automatically.

The key question is:

> "What two stages am I trying to decouple?"

---

## Kafka Already Provides Buffering

Example:

```
Producer
    |
Kafka Topic
    |
Consumer
    |
External Vendor
```

If the vendor is slow:

```
Consumer throughput decreases
           |
Kafka consumer lag increases
```

Kafka already acts as:

- A durable queue.
- Long-term buffering.
- A mechanism to absorb traffic spikes.

Therefore, an additional in-memory queue may not be necessary.

---

## When Is an Internal Bounded Queue Useful?

An internal queue is useful when we need to decouple Kafka polling from processing.

Example:

```
Kafka Consumer Thread
          |
      Bounded Queue
          |
     Worker Threads
          |
       Vendor API
```

Advantages:

- Consumers can continue calling `poll()` regularly.
- Avoids consumer group rebalancing due to long processing.
- Allows different concurrency levels for each stage.
- Protects application memory with a bounded capacity.

Example:

```
Kafka Consumers:
10 threads

Processing Workers:
100 threads

Vendor Capacity:
20 concurrent requests
```

Architecture:

```
Kafka Consumers
        |
Bounded Queue
        |
Worker Pool
        |
Semaphore (20)
        |
Vendor
```

Each stage has a separate concurrency boundary.

---

## Senior Design Principle

Do not add queues unless they solve a specific problem.

Additional queues introduce:

- More memory usage.
- Additional latency.
- More operational complexity.

Kafka already provides durable buffering, so a second queue should only be introduced when we need to decouple consumption from processing or apply different concurrency controls.

# 7. Backpressure vs Rate Limiting vs Concurrency Control

These concepts are related but solve different problems.

| Concept | Question Answered | Example |
|---|---|---|
| Rate Limiting | How many requests per second are allowed? | 500 requests/sec |
| Concurrency Control | How many operations can execute simultaneously? | 50 vendor calls |
| Backpressure | What happens when the system cannot keep up? | Queue, reject, slow producer |

---

# 8. Real Production Thinking

A senior engineer does not ask:

> "How do I process every request no matter what?"

A senior engineer asks:

> "What should my system do when capacity is exceeded?"

Possible answers:

- Queue temporarily.
- Reject and ask the caller to retry.
- Slow down producers.
- Scale consumers.
- Drop non-critical work.

The correct choice depends on:

- Business criticality.
- Latency requirements.
- Available resources.

---

# L6 Interview Soundbites

## What is backpressure?

"Backpressure prevents a fast producer from overwhelming a slower consumer. Instead of allowing queues to grow indefinitely, the system slows producers, buffers work, rejects requests, or sheds load."

---

## Why not use an unbounded queue?

"An unbounded queue simply moves the overload problem into application memory. It increases latency and can eventually cause OutOfMemoryError."

---

## How is Kafka different from a queue in your application?

"Kafka stores messages durably on disk and can absorb long-term traffic spikes. In-memory queues are faster but consume application memory and therefore must be bounded."

---

## How do you handle a consumer that cannot keep up?

"I first identify the bottleneck. Depending on the situation, I may increase consumer parallelism, add partitions, apply backpressure, reduce producer throughput, or temporarily buffer work."

---

# Key Takeaways

1. Backpressure keeps systems stable when demand exceeds capacity.

2. Unbounded queues are dangerous because they consume unlimited memory.

3. Bounded queues provide controlled buffering.

4. Kafka provides durable buffering but consumer lag must still be monitored.

5. Scaling consumers increases throughput but is limited by partition count.

6. Backpressure, rate limiting, and concurrency control solve different problems.

7. A well-designed system has a clear overload policy.

# Chapter 8: Traffic Control and Resilience Engineering
# Part 4: End-to-End Resilience Pipeline

---

# 1. The Big Picture

In a production distributed system, a single mechanism is usually not enough.

Different failures require different protection mechanisms:

- Traffic spikes.
- Slow dependencies.
- Downstream outages.
- Resource exhaustion.
- Retry storms.

The design principle is:

> Protect your own service, protect downstream dependencies, and fail gracefully when problems occur.

---

# 2. End-to-End Traffic Control Architecture

A production system may combine multiple layers of protection:

```
Incoming Requests / Messages
              |
        Rate Limiter
              |
    Bounded Queue (Optional)
              |
 Application Threads
(Tomcat / Kafka Workers)
              |
   Bulkhead / Semaphore
(Max Concurrent Calls)
              |
          Timeout
              |
 Retry (Exponential Backoff
          + Jitter)
              |
      Circuit Breaker
              |
      External Dependency
```

Each layer solves a different problem.

---

# 3. Rate Limiter - Protect the System Entrance

Question:

> How much traffic am I willing to accept?

Example:

```
Incoming Traffic:
100,000 requests/sec

Allowed:
10,000 requests/sec
```

Excess requests may:

- Receive HTTP 429 Too Many Requests.
- Be delayed.
- Be retried later.

The goal is to prevent the service from becoming overloaded.

---

# 4. Bounded Queue - Absorb Short Traffic Spikes

Traffic is often bursty.

Example:

Normal traffic:

```
500 requests/sec
```

Temporary spike:

```
5,000 requests/sec
```

Instead of rejecting immediately:

```
Incoming Requests
        |
Bounded Queue
        |
Workers
```

The queue absorbs short-term spikes.

---

## When the Queue Becomes Full

The system needs a clear overload policy:

- Reject requests.
- Return HTTP 429.
- Retry later.
- Drop low-priority work.

A queue should never grow indefinitely.

---

# 5. Application Threads - Execute the Work

These are the threads that perform the business operation.

Examples:

Synchronous API:

```
Tomcat Request Threads
```

Kafka Processing:

```
Kafka Consumer Threads
```

In many systems, these already provide the required execution capacity.

Additional worker pools are only created when we need to:

- Decouple receiving work from processing.
- Isolate slow operations.
- Apply different concurrency boundaries.

---

# 6. Bulkhead / Semaphore - Protect the Downstream Dependency

This was the primary solution in our vendor screening system.

Example:

```
Application Capacity:
200 concurrent threads

Vendor Capacity:
50 concurrent requests
```

Without protection:

```
200 Threads
      |
200 Vendor Calls
      |
Vendor Overloaded
```

With bulkhead:

```
200 Threads
      |
Semaphore (50 permits)
      |
50 Vendor Calls
```

The remaining requests:

- Wait for a permit.
- Timeout.
- Fail fast.

The goal is not maximum throughput.

The goal is to match the request pressure to the downstream system's actual capacity.

---

# 7. Timeout - Prevent Resource Exhaustion

A slow dependency can consume threads and connections.

Example:

Normal response:

```
100ms
```

Failure scenario:

```
10 seconds
```

Without a timeout:

```
Thread waits indefinitely
        |
Thread pool becomes exhausted
        |
System becomes unavailable
```

With a timeout:

```
Timeout = 500ms

Request fails quickly.
```

Benefits:

- Releases threads sooner.
- Protects resources.
- Prevents cascading failures.

---

# 8. Retry with Exponential Backoff and Jitter

Some failures are temporary.

Examples:

- Network glitches.
- Temporary service unavailability.
- Short spikes in latency.

---

## Retry Example

```
Attempt 1:
Timeout

Wait:
100ms + random jitter

Attempt 2:
Timeout

Wait:
200ms + random jitter

Attempt 3:
Success
```

---

## Why Exponential Backoff?

Bad:

```
10,000 clients fail

All retry after 100ms

Another traffic spike occurs.
```

Good:

```
Client 1 retries after 120ms

Client 2 retries after 170ms

Client 3 retries after 250ms
```

Benefits:

- Gives the dependency time to recover.
- Prevents synchronized retry storms.

---

## Retry Should Be Used Carefully

Good candidates:

- Timeouts.
- HTTP 503 Service Unavailable.
- Temporary network failures.

Bad candidates:

- HTTP 400 Bad Request.
- HTTP 401 Unauthorized.
- Business validation failures.

---

# 9. Circuit Breaker - Fail Fast During Outages

What happens when the dependency is completely unhealthy?

Without a circuit breaker:

```
Request
   |
Vendor Call
   |
Timeout
   |
Retry
   |
Failure
```

Every request continues to hit the failing dependency.

Problems:

- Wasted threads.
- Increased latency.
- More pressure on the dependency.

---

## Circuit Breaker States

### Closed

Normal operation.

```
Requests → Vendor
```

---

### Open

Failure threshold exceeded.

```
Request
   |
Circuit Open
   |
Fail immediately
```

No call reaches the dependency.

---

### Half-Open

After a cooling period:

```
Allow a few test requests.
```

If they succeed:

```
Half Open → Closed
```

If they fail:

```
Half Open → Open
```

---

# 10. How the Layers Work Together

Imagine a sudden traffic spike:

```
100,000 requests arrive.
```

### Step 1: Rate Limiter

Accepts only the allowed rate.

```
10,000 requests/sec allowed.
```

---

### Step 2: Queue

Absorbs a temporary burst.

---

### Step 3: Bulkhead

Ensures only the allowed number of vendor calls occur.

Example:

```
50 concurrent calls.
```

---

### Step 4: Timeout

If the vendor becomes slow, requests do not wait forever.

---

### Step 5: Retry

Transient failures are retried with controlled backoff.

---

### Step 6: Circuit Breaker

If the vendor is continuously failing:

```
Circuit opens.
```

Requests fail immediately until recovery.

---

# 11. Important Design Principle

Do not blindly add every resilience mechanism.

A junior engineer might say:

> "Let's add retries, queues, rate limiting, and circuit breakers everywhere."

A senior engineer asks:

> "What is the actual bottleneck and failure mode?"

Examples:

### Vendor has limited concurrency

Use:

```
Semaphore / Bulkhead
```

---

### API contract allows only 500 requests/sec

Use:

```
Rate Limiter
```

---

### Temporary network failures

Use:

```
Retry with Backoff
```

---

### Dependency is completely unavailable

Use:

```
Circuit Breaker
```

---

### Traffic spikes temporarily

Use:

```
Bounded Queue
```

---

# 12. Real Production Example - Client Screening Vendor

In our screening platform, the service could process a much higher level of parallel requests than the external vendor could handle.

Symptoms:

- Increased latency.
- Request timeouts.
- Vendor saturation.

Root cause:

```
Application throughput
        >
Vendor capacity
```

---

Solution:

```
Application Threads
        |
Bulkhead / Semaphore
        |
External Vendor
```

Additional protections:

- Timeouts to prevent blocked threads.
- Controlled retries with exponential backoff.
- Circuit breakers to fail fast during outages.

---

## Key Lesson

The goal was not to maximize throughput.

The goal was:

> Match our request pressure to the downstream capacity while keeping our own system healthy.

---

# L6 Interview Soundbites

## How do you protect a slow dependency?

"I identify the failure mode first. I use concurrency limits to control pressure, timeouts to release resources quickly, retries with backoff for transient failures, and circuit breakers to fail fast during prolonged outages."

---

## Why are retries dangerous?

"Retries can amplify failures. During an outage, uncontrolled retries can create a retry storm that prevents the dependency from recovering."

---

## What was your biggest production lesson?

"Throughput alone is not the goal. A distributed system must respect the capacity of downstream dependencies. Controlled throughput and graceful degradation create a more reliable system."

---

# Key Takeaways

1. Resilience requires multiple layers of protection.

2. Rate limiting controls incoming traffic.

3. Bounded queues absorb short-term bursts.

4. Bulkheads protect constrained dependencies.

5. Timeouts prevent resource exhaustion.

6. Retries should use exponential backoff and jitter.

7. Circuit breakers prevent repeated calls to unhealthy dependencies.

8. The correct mechanism depends on the actual bottleneck.

# Chapter 8: Traffic Control and Resilience Engineering
# Part 5: Resilience4j Implementation with Spring Boot

---

# 1. Why Do We Need Resilience4j?

The resilience patterns discussed earlier:

- Bulkhead
- Timeout
- Retry
- Circuit Breaker
- Rate Limiting

are architectural concepts.

In production, we need a framework that implements these patterns reliably.

Resilience4j is a lightweight fault tolerance library for Java applications that provides production-ready implementations of these mechanisms.

---

# 2. How Resilience4j Works

Resilience4j follows the Decorator pattern.

It wraps your external call with multiple layers of protection.

Conceptually:

```
Business Service
        |
Resilience4j Decorators
        |
----------------------------
| Rate Limiter              |
| Bulkhead                  |
| Time Limiter              |
| Retry                     |
| Circuit Breaker           |
----------------------------
        |
HTTP Client
(WebClient / RestTemplate)
        |
External Dependency
```

---

## Important Note About Ordering

The exact order of these decorators depends on the implementation and desired behavior.

A senior engineer should not memorize a single ordering.

The important idea is:

- Limit incoming traffic.
- Limit concurrent access to dependencies.
- Fail slow operations quickly.
- Retry only transient failures.
- Stop calling dependencies that are unhealthy.

---

# 3. Bulkhead Implementation

## The Problem

Example:

```
Our service:
200 concurrent requests

Vendor capacity:
50 concurrent requests
```

Without a bulkhead:

```
200 threads
      |
200 vendor calls
      |
Vendor overloaded
```

---

## Using Resilience4j Bulkhead

Configuration:

```yaml
resilience4j:
  bulkhead:
    instances:
      vendorService:
        maxConcurrentCalls: 50
        maxWaitDuration: 500ms
```

Behavior:

```
Request
   |
Bulkhead
   |
----------------
|              |
Permit       No Permit
Available    Available
|              |
Call        Wait 500ms
Vendor          |
             Reject
```

The bulkhead acts similarly to a semaphore.

---

## Spring Annotation Example

```java
@Bulkhead(
    name = "vendorService",
    type = Bulkhead.Type.SEMAPHORE
)
public Response callVendor() {
    return vendorClient.call();
}
```

---

# 4. Timeout Implementation

## The Problem

A slow dependency can block resources.

Example:

```
Vendor normally:
100ms

Failure:
10 seconds
```

Without timeout:

```
Thread waits indefinitely.
```

---

## Configuration

```yaml
resilience4j:
  timelimiter:
    instances:
      vendorService:
        timeoutDuration: 500ms
```

---

## Behavior

```
Request
   |
Call Vendor
   |
Response within 500ms?
      |
   Yes      No
    |        |
Return    Timeout Exception
```

---

# 5. Retry with Exponential Backoff

## Why Retry?

Many failures are temporary.

Examples:

- Network glitches.
- Temporary connection failures.
- Short service interruptions.

---

## Configuration

```yaml
resilience4j:
  retry:
    instances:
      vendorService:
        maxAttempts: 3
        waitDuration: 100ms
```

Example:

```
Attempt 1:
Failure

Wait:
100ms

Attempt 2:
Failure

Wait:
200ms

Attempt 3:
Success
```

---

## Adding Jitter

Retries should not happen at exactly the same time.

Bad:

```
10,000 clients
      |
Retry after exactly 100ms
      |
Traffic spike
```

Better:

```
Client A: 120ms

Client B: 170ms

Client C: 250ms
```

Jitter spreads retries over time and prevents retry storms.

---

# 6. Circuit Breaker Implementation

## The Problem

If a dependency is down, continuously calling it wastes resources.

Without a circuit breaker:

```
Request
   |
Vendor Call
   |
Timeout
   |
Retry
   |
Failure
```

The system keeps applying pressure to a failing dependency.

---

## Circuit Breaker States

### CLOSED

Normal operation.

```
Requests
   |
Vendor
```

---

### OPEN

Failure threshold exceeded.

```
Request
   |
Circuit Open
   |
Fail Immediately
```

No calls reach the dependency.

---

### HALF OPEN

After a waiting period:

```
Allow small number of test requests
```

If successful:

```
HALF OPEN -> CLOSED
```

If failures continue:

```
HALF OPEN -> OPEN
```

---

## Configuration

```yaml
resilience4j:
  circuitbreaker:
    instances:
      vendorService:
        failureRateThreshold: 50
        slidingWindowSize: 100
        waitDurationInOpenState: 30s
```

Meaning:

```
Last 100 calls:
Failure rate > 50%
```

Then:

```
Circuit opens for 30 seconds.
```

---

# 7. Rate Limiter Implementation

Rate limiting controls requests over time.

Example:

```
Vendor contract:
500 requests/second
```

Configuration:

```yaml
resilience4j:
  ratelimiter:
    instances:
      vendorService:
        limitForPeriod: 500
        limitRefreshPeriod: 1s
        timeoutDuration: 0
```

Behavior:

```
Request
   |
Rate Limiter
   |
Token Available?
      |
   Yes       No
    |         |
Proceed   Reject
```

---

# 8. Combining Multiple Patterns

A production call may use several protections.

Example:

```
Application Thread
        |
Rate Limiter
        |
Bulkhead
        |
Time Limiter
        |
Retry
        |
Circuit Breaker
        |
External Vendor
```

Each solves a different problem:

| Pattern | Protects Against |
|----------|-----------------|
| Rate Limiter | Too much traffic over time |
| Bulkhead | Too many concurrent calls |
| Time Limiter | Slow dependencies |
| Retry | Temporary failures |
| Circuit Breaker | Long outages |

---

# 9. Spring Boot Annotation Example

A real service method may combine multiple patterns.

```java
@RateLimiter(name = "vendorService")
@Bulkhead(
    name = "vendorService",
    type = Bulkhead.Type.SEMAPHORE
)
@TimeLimiter(name = "vendorService")
@Retry(name = "vendorService")
@CircuitBreaker(
    name = "vendorService",
    fallbackMethod = "fallback"
)
public Response callVendor(Request request) {
    return vendorClient.call(request);
}
```

---

## Fallback Method

A fallback provides an alternative response when a call fails.

Examples:

- Return cached data.
- Return a default response.
- Queue work for later processing.
- Display degraded functionality.

Example:

```java
public Response fallback(
        Request request,
        Exception ex) {

    return Response.cachedValue();
}
```

---

# 10. Monitoring and Observability

Resilience mechanisms must be monitored.

Examples:

## Bulkhead

Monitor:

- Concurrent calls.
- Waiting requests.
- Rejected requests.

---

## Retry

Monitor:

- Retry count.
- Retry success rate.

---

## Circuit Breaker

Monitor:

- Number of times opened.
- Failure rate.
- Slow call rate.

---

## Rate Limiter

Monitor:

- Allowed requests.
- Rejected requests.

---

Metrics are commonly exported using:

```
Micrometer
       |
Prometheus
       |
Grafana
```

---

# 11. Real Production Thinking

A junior engineer says:

> "I added retry and circuit breaker."

A senior engineer asks:

- What failure are you handling?
- Is the dependency slow or unavailable?
- Will retries increase load?
- What should happen when capacity is exhausted?
- What metrics prove that the solution works?

---

# L6 Interview Soundbites

## How would you protect an external vendor API?

"I would identify the failure modes first. If the vendor has limited capacity, I would use a bulkhead to control concurrency. I would add timeouts to prevent resource exhaustion, retries with exponential backoff for transient failures, and a circuit breaker to fail fast during outages."

---

## Why are retries dangerous?

"Retries can amplify failures. During an outage, uncontrolled retries can create retry storms that increase load and delay recovery."

---

## Should every API use all Resilience4j patterns?

"No. Resilience patterns should be chosen based on the bottleneck and failure mode. Adding unnecessary retries or queues can make systems more complex and sometimes worsen failures."

---

# Key Takeaways

1. Resilience4j implements production-ready resilience patterns.

2. Bulkheads protect dependencies from excessive concurrency.

3. Timeouts prevent blocked threads and resource exhaustion.

4. Retries should be limited and use exponential backoff with jitter.

5. Circuit breakers fail fast when dependencies are unhealthy.

6. Rate limiting controls request volume over time.

7. Observability is essential to tune resilience configurations.

# Chapter 8: Traffic Control and Resilience Engineering
# Part 6: Real Production Scenarios & L6 Interview Deep Dive

---

# 1. Real Production Scenario: External Vendor Capacity Problem

This was a common real-world problem in our screening platform.

Architecture:

```
Screening Service
        |
External Screening Vendor
```

The system could process a much higher level of parallel requests than the vendor could handle.

---

## Symptoms Observed in Production

We started seeing:

- Increased response latency.
- Vendor API timeouts.
- Higher failure rates.
- Increased retry traffic.
- Thread exhaustion risk.

---

## Root Cause Analysis

The issue was not the overall throughput of our system.

The bottleneck was the downstream vendor's ability to process concurrent requests.

Example:

```
Our Service Capacity:
200 concurrent threads

Vendor Capacity:
50 concurrent requests
```

Without protection:

```
200 Threads
      |
200 Vendor Calls
      |
Vendor Overloaded
```

The more threads we added, the worse the problem became.

---

# 2. Solution: Controlled Concurrency

The goal was not:

```
Maximize throughput at any cost.
```

The goal was:

```
Match the pressure we apply to the vendor's actual capacity.
```

---

## Semaphore-Based Bulkhead

We introduced a concurrency limiter.

Example:

```
Semaphore:
50 permits
```

Architecture:

```
Application Threads
        |
Semaphore / Bulkhead
        |
External Vendor
```

Behavior:

```
First 50 requests
        |
Vendor API

Request 51
        |
No permit available
        |
Wait / Timeout / Fail Fast
```

This prevented the service from overwhelming the dependency.

---

# 3. Why Not Simply Increase Thread Count?

A common junior-level reaction:

> "The system is slow. Let's increase the thread pool."

This may actually make the situation worse.

Example:

```
Before:
200 Threads
       |
Vendor (50 capacity)

After:
1000 Threads
       |
Vendor (still 50 capacity)
```

Problems:

- More blocked threads.
- More memory consumption.
- More context switching.
- More timeouts.
- More retries.
- More pressure on the vendor.

---

## Senior Engineering Principle

Increasing concurrency only helps when the downstream system has additional capacity.

Otherwise, it only moves the bottleneck and amplifies failures.

---

# 4. What Happens When Capacity Is Exhausted?

This depends on the execution model.

---

## Synchronous REST APIs

Example:

```
Client
  |
Tomcat Thread
  |
Semaphore
  |
Vendor
```

When permits are exhausted:

Possible strategies:

### Wait

```
Request waits until a permit is released.
```

Pros:

- No immediate failure.

Cons:

- Higher latency.
- Thread exhaustion risk.

---

### Wait With Timeout (Preferred)

Example:

```
Try to acquire permit for 500ms.
```

If unavailable:

```
Return HTTP 429 or HTTP 503.
```

Benefits:

- Fail fast.
- Protect thread pools.
- Maintain predictable latency.

---

## Asynchronous Systems (Kafka / Batch)

Example:

```
Kafka Topic
      |
Consumer
      |
Bounded Queue
      |
Worker Threads
      |
Semaphore
      |
Vendor
```

The work is not necessarily rejected immediately.

Possible strategies:

- Leave messages in Kafka.
- Pause consumption.
- Buffer in a bounded queue.
- Scale consumers.

---

# 5. Handling a Slow Dependency

Scenario:

```
Vendor normally:
100ms

Suddenly:
10 seconds
```

Without protection:

```
Request
   |
Vendor Call
   |
Thread blocked for 10 seconds
```

After enough requests:

```
Thread pool exhausted
        |
Service becomes unavailable
```

---

## Solution

Use timeouts.

Example:

```
Timeout:
500ms
```

The request fails quickly and resources are released.

---

# 6. Handling Temporary Failures

Examples:

- Network glitches.
- Temporary service issues.
- Short spikes in latency.

---

## Solution: Retry with Exponential Backoff

Bad:

```
10,000 clients fail

Retry after exactly 100ms

Another traffic spike occurs.
```

---

Good:

```
Attempt 1:
Failure

Wait:
100ms + jitter

Attempt 2:
Failure

Wait:
200ms + jitter

Attempt 3:
Success
```

---

## Why Jitter?

Without randomness:

```
10,000 clients retry together
        |
Traffic spike
```

With jitter:

```
Clients retry at different times
        |
Traffic is spread out
```

---

# 7. Handling Complete Dependency Outages

Scenario:

```
Vendor is completely down.
```

Without a circuit breaker:

```
Every request
      |
Timeout
      |
Retry
      |
Failure
```

The system continues wasting:

- Threads.
- CPU.
- Network resources.

---

## Solution: Circuit Breaker

The circuit monitors:

- Failure rate.
- Slow call rate.

Example:

```
Failure rate > 50%
over the last 100 requests
```

The circuit opens:

```
Request
    |
Circuit OPEN
    |
Fail immediately
```

No calls reach the vendor.

After a cooling period:

```
HALF OPEN
        |
Small number of test requests
```

If successful:

```
HALF OPEN → CLOSED
```

Otherwise:

```
HALF OPEN → OPEN
```

---

# 8. How a Senior Engineer Thinks About Reliability

A junior engineer asks:

> "How do I make the system process more requests?"

A senior engineer asks:

> "What is the actual bottleneck, and how do I keep the system healthy under failure?"

---

## Different Problems Require Different Solutions

| Problem | Solution |
|---|---|
| Too many requests per second | Rate Limiter |
| Too many concurrent vendor calls | Bulkhead / Semaphore |
| Short traffic spikes | Bounded Queue |
| Slow dependency | Timeout |
| Temporary failures | Retry + Backoff + Jitter |
| Long outages | Circuit Breaker |
| Non-critical traffic during overload | Load Shedding |

---

# 9. L6 Interview Questions & Answers

## Q: How did you implement concurrency control?

**Answer:**

"It depended on the execution model. In a synchronous Spring Boot API, we already had Tomcat request threads, so we introduced a semaphore-based bulkhead around the vendor call. The semaphore did not create threads; it limited how many existing threads could access the vendor simultaneously."

---

## Q: Why not use a larger thread pool?

**Answer:**

"More threads only help when the downstream system has additional capacity. In our case, the vendor was the bottleneck, so increasing threads only increased waiting, memory usage, and timeouts."

---

## Q: What happened when the 51st request arrived?

**Answer:**

"That depended on the workload. For synchronous APIs, we could wait briefly for a permit and then fail fast with HTTP 429 or 503. For asynchronous systems, we could keep the work in Kafka or a bounded queue and process it later."

---

## Q: How do you protect a slow external API?

**Answer:**

"I identify the failure mode first. I use concurrency limits to protect the dependency, timeouts to avoid blocked resources, controlled retries for transient failures, and circuit breakers to fail fast when the dependency becomes unhealthy."

---

# Key Takeaways

1. The goal of traffic control is not maximum throughput; it is controlled, predictable throughput.

2. Always identify the true bottleneck before choosing a solution.

3. More threads do not solve a dependency capacity problem.

4. Bulkheads protect limited downstream resources.

5. Timeouts prevent resource exhaustion.

6. Retries should be controlled using exponential backoff and jitter.

7. Circuit breakers prevent repeated calls to unhealthy dependencies.

8. Senior engineers design systems that degrade gracefully under failure.



# Chapter 9. Messaging and Event-drivern Architectur
---
Seperate Kafka Chapter
---
# Chapter 10. API Design and Communication Patterns
---

# REST Principles, Resource Modeling, and Statelessness

# 1. What Makes a Well-Designed API?

A well-designed API should be:

- Reliable
- Scalable
- Secure
- Easy for consumers to understand and integrate

Good APIs are not just technically correct; they provide a predictable contract that client applications can rely on for years.

Key principles:

- Consistent resource modeling
- Stateless communication
- Strong authentication and authorization
- Clear error handling
- Backward compatibility
- Good documentation
- Performance and scalability
- Observability

---

# 2. RESTful Resource Modeling and Naming Conventions

A good API models **resources**, not actions.

The URI identifies the resource, while the HTTP method defines the action performed on it.

---

## Good Examples

Get a client:

```
GET /clients/123
```

Create a client:

```
POST /clients
```

Get a client's account:

```
GET /clients/123/accounts/456
```

Filter active clients:

```
GET /clients?status=active&limit=50
```

---

## Bad Examples

Action-oriented APIs:

```
GET /getClient?id=123

POST /createClient
```

Problems:

- Less intuitive
- Inconsistent with REST principles
- Harder to maintain as APIs grow

---

## Naming Best Practices

Use:

- Plural nouns
- Lowercase URLs
- Hyphens for readability
- Hierarchical resources

Examples:

Good:

```
/clients
/client-risk-profiles
/clients/123/accounts
```

Avoid:

```
/GetClient
/clientDetails
/createNewAccount
```

---

## Why This Matters

Consistent APIs reduce the learning curve for client developers.

A developer should be able to predict how a new endpoint works based on existing endpoints.

Example:

If:

```
GET /clients/123
```

returns a client, then naturally:

```
DELETE /clients/123
```

should remove that client.

Consistency reduces documentation requirements and minimizes integration mistakes.

---

# 3. Statelessness

## Definition

In a stateless API, the server does not remember client-specific information between requests.

Every request contains all the information needed to process it.

Example:

```
GET /clients/123/analytics

Authorization: Bearer <JWT>
```

The JWT carries the identity and authorization information required to process the request.

---

## Why Statelessness Matters

Stateless APIs are easier to scale horizontally.

Example architecture:

```
                Load Balancer
                      |
          ---------------------------
          |             |             |
       API-1          API-2         API-3
```

Any server can process any request because no user session is stored locally.

Benefits:

- No sticky sessions required
- Easier auto-scaling
- Better fault tolerance
- Simpler multi-region deployments

If API-1 fails:

```
Request 1 → API-1 (fails)

Request 2 → API-3 (continues normally)
```

The user experience is not affected because API-3 has all required context inside the request.

---

# 4. Traditional Stateful Sessions

Before JWT-based authentication became common, many web applications used server-side sessions.

---

## How Session Authentication Works

### Step 1: User Logs In

User sends credentials:

```
POST /login

username = john
password = ****** 
```

The server validates the credentials.

---

### Step 2: Server Creates Session

The server stores session information:

```
Session ID: abc123

Data:
{
    userId: 45,
    role: "ADMIN"
}
```

The server returns:

```
Cookie:
sessionId=abc123
```

---

### Step 3: Future Requests

Every request sends the session ID:

```
GET /clients/123

Cookie:
sessionId=abc123
```

The server looks up:

```
abc123 → userId=45, role=ADMIN
```

and performs authorization checks.

---

# 5. Problems with In-Memory Sessions

Imagine a load-balanced system:

```
               Load Balancer
                      |
              ----------------
              |              |
            API-1          API-2
          Session X       No Session
```

Request 1:

```
User → API-1
```

API-1 creates:

```
Session X123
```

Request 2:

```
User → API-2
```

API-2 does not recognize the session.

---

## Possible Solutions

### Sticky Sessions

Force the load balancer to always route the same user to the same server.

Problems:

- Poor load balancing.
- Harder scaling.
- Server failures cause session loss.

---

### Shared Session Store

Store sessions in a centralized system such as Redis:

```
          API Servers
               |
            Redis
               |
        Session Data
```

Now every server can retrieve the session.

---

## But There Are Trade-offs

Although Redis solves the scalability problem, every request now requires:

```
API Server
     |
Redis lookup
     |
Authorization decision
```

This introduces:

- Additional network calls
- More operational complexity
- A dependency on the session store

---

# 6. Why JWT Became Popular

The main reason sessions exist is to avoid sending usernames and passwords with every request.

Without sessions:

Every request would require:

```
username + password
```

This is insecure and inconvenient.

---

## JWT Solves This Differently

After login:

1. User provides credentials once.
2. Authentication service verifies them.
3. Authentication service issues a signed JWT.
4. Client sends the JWT with every request.

Example:

```
Authorization: Bearer eyJhbGciOi...
```

The API validates the token and trusts the claims inside it.

---

## Benefits of JWT-Based Authentication

### Statelessness

No server-side session storage.

---

### Scalability

Any API instance can validate the token.

---

### Resilience

If one server fails, another can immediately handle the request.

---

### Cloud-Friendly Architecture

Works naturally with:

- Load balancers
- Kubernetes
- Auto-scaling groups
- Multi-region deployments

---

# 7. Interview Soundbite

"The main reason we use sessions is so users do not have to send credentials with every request. Traditionally, the server stored session data and the client sent back a session ID. However, this creates scaling challenges in distributed systems.

With JWT-based authentication, the token itself carries signed identity claims, allowing any API server to authenticate the request without maintaining server-side session state. This makes APIs easier to scale horizontally across cloud environments."

---

# Key Takeaways

1. APIs should model resources rather than actions.

2. Consistent URI design makes APIs easier to understand and maintain.

3. Stateless APIs allow any server instance to process any request.

4. Traditional sessions store state on the server and can create scaling challenges.

5. Shared session stores like Redis improve scalability but add network dependencies.

6. JWT enables stateless authentication by carrying signed identity claims.

7. Stateless architectures work naturally with modern cloud environments.

# API Design - Part 2
# Authentication, Authorization, OAuth, JWT and API Security

---

# 1. Authentication vs Authorization

These two concepts are often confused.

---

## Authentication: "Who are you?"

Authentication verifies the identity of a user or system.

Examples:

- Username and password
- Multi-factor authentication (MFA)
- OAuth login
- JWT access token

Example:

User logs in:

```
username: john@example.com
password: ********
```

The authentication service verifies the credentials and establishes the user's identity.

---

## Authorization: "What are you allowed to do?"

Authorization determines whether an authenticated user has permission to perform a specific action.

Example:

```
GET /admin/users
```

User:

```
Role: CUSTOMER
```

Result:

```
403 Forbidden
```

because the user does not have the ADMIN role.

---

## Common Authorization Models

### Role-Based Access Control (RBAC)

Users are assigned roles.

Example:

```
ADMIN:
- Create users
- Delete users
- View reports


CUSTOMER:
- View own profile
- Update own settings
```

Simple and commonly used.

---

### Attribute-Based Access Control (ABAC)

Decisions are based on attributes.

Examples:

- User department
- Location
- Time of access
- Resource ownership

Example:

```
Allow access if:

user.department == resource.department
```

ABAC provides more flexibility but is more complex.

---

# 2. OAuth 2.0

## The Problem OAuth Solves

Suppose a third-party application wants access to your Google calendar.

Bad design:

```
Third-party app
        |
Ask for your Google password
```

Problems:

- The app knows your password.
- You cannot grant limited access.
- You cannot easily revoke permissions.

---

## OAuth Solution

OAuth allows a user to grant limited access to another application without sharing credentials.

Example:

```
User
 |
Third-party App
 |
Google Authorization Server
 |
Access Token
 |
Google APIs
```

---

# OAuth Roles

## Resource Owner

The user who owns the data.

Example:

You own your Google Calendar.

---

## Client Application

The application requesting access.

Example:

Calendar management app.

---

## Authorization Server

Verifies identity and issues tokens.

Examples:

- Okta
- Auth0
- Google Identity Platform

---

## Resource Server

The API hosting protected data.

Example:

Google Calendar API.

---

# OAuth Flow (Simplified)

1. User clicks:

```
Login with Google
```

---

2. Browser redirects to Google's login page.

---

3. User authenticates with Google.

---

4. User grants permission:

Example:

```
Allow this app to:
- Read your calendar
- Create events
```

---

5. Authorization server issues an access token.

---

6. Client uses the token:

```
Authorization:
Bearer access_token
```

to call the API.

---

# 3. JWT (JSON Web Token)

A JWT is a compact signed token containing claims about a user.

Typical format:

```
header.payload.signature
```

Example:

```
xxxxx.yyyyy.zzzzz
```

---

# JWT Header

Contains metadata.

Example:

```json
{
 "alg": "RS256",
 "typ": "JWT"
}
```

Meaning:

- Algorithm used for signing.
- Token type.

---

# JWT Payload (Claims)

Contains information about the user.

Example:

```json
{
 "sub": "12345",
 "email": "john@example.com",
 "role": "ADMIN",
 "exp": 1750000000
}
```

Common claims:

- sub → Subject (user ID)
- exp → Expiration time
- iat → Issued time
- iss → Issuer

---

# JWT Signature

Generated by signing:

```
Base64(header) + Base64(payload)
```

using a cryptographic key.

The signature guarantees:

- The token was issued by a trusted authority.
- The payload has not been modified.

---

# 4. JWT Validation Flow

Example:

```
Client
  |
JWT Token
  |
API Gateway / Service
  |
Validate Signature
  |
Extract Claims
  |
Authorization Check
  |
Process Request
```

The service verifies:

- Is the signature valid?
- Has the token expired?
- Was it issued by a trusted issuer?
- Does the user have required permissions?

---

# 5. Symmetric vs Asymmetric Signing

## Symmetric Key (HS256)

Uses the same secret for:

- Creating the signature.
- Verifying the signature.

Example:

```
Auth Server
     |
Shared Secret
     |
API Services
```

---

### Advantages

- Simple.
- Fast.

---

### Disadvantages

Every service needs the secret.

If one service is compromised, attackers could create fake tokens.

---

# Asymmetric Key (RS256)

Uses:

- Private key → Sign tokens.
- Public key → Verify tokens.

Example:

```
          Private Key
              |
     Authorization Server
              |
           JWT Token
              |
       -----------------
       |               |
 API Service       API Gateway
   Public Key       Public Key
```

---

### Advantages

The private key is only held by the authorization server.

API services only require the public key.

Better for large microservice environments.

---

### Why Enterprises Prefer RS256

Example:

A company has:

```
200 microservices
```

With HS256:

```
All 200 services store the same secret.
```

A compromise is dangerous.

With RS256:

```
Only the authentication server has the private key.
```

All services safely verify using public keys.

---

# 6. Token Expiration and Refresh Tokens

## Why Tokens Expire

If an access token is stolen, a long-lived token gives attackers a long period of access.

Example:

```
Access Token:
Valid for 15 minutes
```

---

## Refresh Token

A longer-lived token used to request new access tokens.

Flow:

```
User Login
     |
Access Token (15 mins)
Refresh Token (30 days)
```

When access token expires:

```
Client
 |
Refresh Token
 |
Authorization Server
 |
New Access Token
```

---

# 7. HTTPS / TLS

Never send tokens or credentials over plain HTTP.

Without encryption:

```
Client
 |
Network
 |
Server
```

An attacker could capture:

- Passwords
- JWTs
- Session IDs

---

## TLS Provides

### Encryption

Data cannot be read by attackers.

---

### Integrity

Data cannot be modified in transit.

---

### Authentication

The client can verify it is communicating with the legitimate server.

---

# 8. Input Validation

Never trust data coming from clients.

Examples:

```
POST /payments

amount = -1000000
```

Without validation, this could cause security issues.

---

# Server-Side Validation

Validation must always happen on the server.

Do not rely only on frontend validation because clients can bypass it.

---

# Spring Boot Bean Validation

Example:

```java
public class UserRequest {

    @NotNull
    private String name;

    @Email
    private String email;

    @Min(18)
    private int age;
}
```

The framework automatically validates requests.

---

# JSON Schema Validation

Useful when validating complex JSON payloads.

Example:

Rules:

```
userId must be integer

email must match email format

age must be >= 18
```

---

# 9. Additional API Security Best Practices

---

## Principle of Least Privilege

Give users only the permissions they require.

Example:

A reporting service should have:

```
READ_REPORTS
```

not:

```
ADMIN
```

---

## Rate Limiting

Protect APIs against:

- Abuse
- Bots
- Denial-of-service attacks

Example:

```
100 requests per minute per API key
```

---

## Replay Protection

Attackers may capture a valid request and resend it.

Solutions:

- Nonces
- Timestamps
- Short-lived tokens
- Idempotency keys

---

## Audit Logging

Record sensitive operations.

Examples:

- Money transfers
- Permission changes
- Data access

Logs should contain:

```
Who performed the action?

What action occurred?

When did it occur?

Was it successful?
```

---

# 10. L6 Interview Discussion

## Why use JWT instead of server sessions?

Because JWT enables stateless APIs that scale horizontally without requiring shared session storage.

---

## Why is RS256 preferred in microservices?

Because only the authentication server owns the private key. Services verify using public keys and cannot create fake tokens.

---

## Is authentication enough?

No.

Authentication answers:

"Who are you?"

Authorization answers:

"What are you allowed to do?"

---

## Why validate input on the server?

Clients cannot be trusted. Frontend validation can be bypassed.

---

## How would you secure a public API?

Use:

- HTTPS/TLS
- OAuth 2.0
- JWT validation
- Rate limiting
- Input validation
- Audit logging
- Least privilege

---

# Key Takeaways

1. Authentication verifies identity; authorization controls permissions.

2. OAuth allows delegated access without sharing credentials.

3. JWT enables stateless authentication.

4. JWT signatures protect against token tampering.

5. RS256 is preferred for large distributed systems because services only need public keys.

6. Access tokens should be short-lived and combined with refresh tokens.

7. All APIs should use HTTPS.

8. Always perform server-side validation.

9. Security requires multiple layers, including authentication, authorization, validation, rate limiting, and auditing.

# API Design - Part 3
# HTTP Methods, Idempotency, Error Handling, and Versioning

---

# 1. HTTP Methods Overview

HTTP methods define the action performed on a resource.

A good REST API uses HTTP verbs consistently.

---

# 2. GET

## Purpose

Retrieve a resource.

Example:

```
GET /customers/123
```

Response:

```json
{
  "customerId": 123,
  "name": "John Smith",
  "status": "ACTIVE"
}
```

---

## Characteristics

### Safe

GET should not modify server-side data.

Calling:

```
GET /customers/123
```

one time or one thousand times should not change the customer.

---

### Idempotent

Multiple identical GET requests produce the same effect.

---

## Common Use Cases

- Get customer details.
- Search resources.
- Retrieve reports.

---

# 3. POST

## Purpose

Create a new resource or trigger a server-side operation.

Example:

```
POST /customers
```

Request:

```json
{
  "name": "John Smith",
  "email": "john@gmail.com"
}
```

Response:

```
201 Created
```

---

## Characteristics

### Not naturally idempotent

Example:

Request:

```
POST /payments
{
  amount: $100
}
```

Client times out.

The client retries.

Without protection:

```
Payment 1: $100
Payment 2: $100
```

The customer is charged twice.

---

## Making POST Idempotent

Use an Idempotency-Key.

Example:

Request:

```
POST /payments

Headers:
Idempotency-Key: abc123
```

The server stores:

```
abc123 → Previous response
```

If the same request arrives again:

```
Return existing result.
Do not execute payment again.
```

---

# 4. PUT

## Purpose

Create or completely replace a resource at a known URI.

Example:

```
PUT /users/123
```

Request:

```json
{
  "name": "John",
  "email": "john@example.com"
}
```

---

## Idempotent

Calling the same PUT request repeatedly results in the same final state.

Example:

```
PUT /users/123
status = ACTIVE
```

First call:

```
User becomes ACTIVE
```

Second call:

```
User remains ACTIVE
```

No additional side effects occur.

---

# 5. PATCH

## Purpose

Partially update a resource.

Example:

Current customer:

```json
{
  "name": "John",
  "email": "john@example.com",
  "status": "ACTIVE"
}
```

Request:

```
PATCH /customers/123
```

Body:

```json
{
  "status": "SUSPENDED"
}
```

Only the status changes.

---

## PATCH and Idempotency

PATCH can be idempotent depending on the operation.

Example:

```
PATCH status = ACTIVE
```

Idempotent.

---

Example:

```
PATCH increment balance by $10
```

Not idempotent.

---

# 6. DELETE

## Purpose

Remove a resource.

Example:

```
DELETE /customers/123
```

---

## Idempotent

The first call removes the customer.

Repeated calls leave the system in the same state.

---

# 7. HTTP Method Summary

| Method | Purpose | Safe | Idempotent |
|---|---|---|---|
| GET | Retrieve data | Yes | Yes |
| POST | Create/Action | No | No (usually) |
| PUT | Replace resource | No | Yes |
| PATCH | Partial update | No | Depends |
| DELETE | Remove resource | No | Yes |

---

# 8. HTTP Status Codes

HTTP status codes communicate the result of an operation.

---

# 2xx Success

## 200 OK

Request completed successfully.

Example:

```
GET /customers/123
```

---

## 201 Created

A new resource was created.

Example:

```
POST /customers
```

---

## 202 Accepted

The request was accepted but processing happens asynchronously.

---

Example:

Large report generation.

```
POST /reports
```

Response:

```
202 Accepted

{
 "jobId": "abc123",
 "status": "PROCESSING"
}
```

The client checks later:

```
GET /jobs/abc123
```

---

## 204 No Content

Successful request with no response body.

Common with:

```
DELETE /customers/123
```

---

# 4xx Client Errors

The client sent an invalid request.

---

## 400 Bad Request

Malformed request.

Examples:

- Invalid JSON.
- Missing required fields.
- Validation failure.

---

## 401 Unauthorized

The user is not authenticated.

Examples:

- Missing JWT.
- Expired token.
- Invalid token.

---

## 403 Forbidden

The user is authenticated but lacks permission.

Example:

```
User role: CUSTOMER

Endpoint:
DELETE /admin/users/123
```

Response:

```
403 Forbidden
```

---

## 404 Not Found

Requested resource does not exist.

---

## 409 Conflict

Request conflicts with current state.

Examples:

- Duplicate username.
- Concurrent update conflict.
- Duplicate resource creation.

---

## 429 Too Many Requests

The client exceeded rate limits.

Example:

```
100 requests/minute exceeded.
```

---

# 5xx Server Errors

Indicate a problem on the server.

---

## 500 Internal Server Error

Unexpected server failure.

---

## 502 Bad Gateway

The server received an invalid response from a downstream service.

Example:

```
API Gateway
     |
Downstream Service returns invalid response
```

---

## 503 Service Unavailable

The service is temporarily unavailable.

Examples:

- Maintenance.
- Dependency outage.
- Circuit breaker open.

---

## 504 Gateway Timeout

A downstream service failed to respond in time.

---

# 9. Error Response Design

Avoid returning:

```
Error occurred.
```

This provides little information.

---

A good error response should be structured.

Example:

```json
{
  "timestamp": "2025-01-01T10:00:00Z",
  "status": 400,
  "errorCode": "INVALID_EMAIL",
  "message": "Email format is invalid",
  "traceId": "abc-123"
}
```

---

## Why Use Error Codes?

Messages can change.

Applications should rely on stable error codes.

Example:

Bad:

```
"User not found"
```

A client may parse this text.

---

Good:

```
USER_NOT_FOUND
```

Clients can implement logic:

```
If USER_NOT_FOUND:
    Show "Create Account" option.
```

---

# 10. Correlation IDs and Trace IDs

In distributed systems, one request may travel through many services.

Example:

```
Client
 |
API Gateway
 |
User Service
 |
Account Service
 |
Database
```

---

A unique ID follows the request.

Example:

```
X-Correlation-ID: 123abc
```

Every service logs:

```
[123abc] UserService request started

[123abc] Database query failed
```

---

Benefits:

- Easier debugging.
- Trace requests across services.
- Faster incident resolution.

---

# 11. API Versioning

APIs often support clients for years.

Breaking changes must be managed carefully.

---

## URI Versioning

Example:

```
/api/v1/customers
/api/v2/customers
```

---

Advantages:

- Simple.
- Easy for clients to understand.

---

Disadvantages:

- Multiple versions may need to be maintained.

---

## Header Versioning

Example:

```
API-Version: 2
```

Advantages:

- Cleaner URLs.

---

Disadvantages:

- Less visible and harder to test manually.

---

# 12. Backward Compatibility

A good API should avoid breaking existing clients.

---

## Safe Changes

Examples:

Adding a new optional field:

Before:

```json
{
  "name": "John"
}
```

After:

```json
{
  "name": "John",
  "nickname": "Johnny"
}
```

Old clients continue to work.

---

## Breaking Changes

Examples:

Changing:

```
customerId (integer)
```

to:

```
customerId (string)
```

or removing fields.

---

Approaches:

- Introduce a new API version.
- Provide migration period.
- Deprecate old endpoints gradually.

---

# 13. OpenAPI / Swagger

APIs are contracts between producers and consumers.

Documentation should be generated from an API specification.

---

OpenAPI describes:

- Endpoints.
- Request schema.
- Response schema.
- Authentication.
- Error responses.

---

Swagger provides:

- Interactive documentation.
- API testing.
- Client SDK generation.

---

# 14. L6 Interview Discussion

## How would you design an API for a long-running operation?

Do not keep an HTTP request open for several minutes.

Instead:

```
POST /reports
```

Return:

```
202 Accepted
jobId: 123
```

Client polls:

```
GET /jobs/123
```

or receives a webhook notification.

---

## How do you prevent duplicate payments?

Use:

- Idempotency keys.
- Persistent request records.
- Unique constraints.

---

## Why should error responses include trace IDs?

Because customers can provide the trace ID to support engineers, allowing them to find the exact request across distributed logs.

---

## How do you evolve APIs safely?

- Version APIs.
- Maintain backward compatibility.
- Deprecate gradually.
- Communicate changes to consumers.

---

# Key Takeaways

1. HTTP methods should match resource semantics.

2. GET is safe and idempotent.

3. POST is usually not idempotent but can be made idempotent using Idempotency-Key.

4. PUT replaces resources; PATCH modifies partial data.

5. Use appropriate HTTP status codes.

6. Error responses should be structured and include stable error codes.

7. Correlation IDs are essential for debugging distributed systems.

8. APIs must evolve carefully through versioning and backward compatibility.

9. OpenAPI acts as a contract between API producers and consumers.

# API Design - Part 4
# Pagination, Performance, Scalability, and Production Readiness

---

# 1. Pagination

Returning large datasets in a single response is dangerous.

Example:

```
GET /transactions
```

Imagine a customer has:

```
100 million transactions
```

Returning everything causes:

- Large response sizes
- High memory usage
- Increased database load
- Long response times
- Network overhead

---

The solution is pagination.

---

# 2. Offset Pagination

The client specifies:

- Offset
- Limit

Example:

```
GET /transactions?offset=100&limit=50
```

Meaning:

```
Skip first 100 records.
Return next 50.
```

---

Database query:

```sql
SELECT *
FROM transactions
ORDER BY created_time DESC
LIMIT 50 OFFSET 100;
```

---

## Advantages

- Simple.
- Easy to understand.
- Allows jumping directly to a page.

Example:

```
Page 20
```

---

## Problems at Scale

The database still has to scan and skip previous records.

Example:

```
OFFSET 1,000,000
```

The database may process one million rows before returning the next 50.

---

Another issue is inconsistent results.

Example:

Page 1:

```
Transaction A
Transaction B
Transaction C
```

A new transaction arrives.

Now Page 2 may:

- Contain duplicates.
- Miss records.

---

# 3. Cursor Pagination

Instead of saying:

```
Skip N records.
```

The client says:

```
Give me records after this position.
```

---

Example:

First request:

```
GET /transactions?limit=50
```

Response:

```json
{
 "transactions": [...],
 "nextCursor": "tx_98345"
}
```

---

Next request:

```
GET /transactions?cursor=tx_98345&limit=50
```

---

Database query:

```sql
SELECT *
FROM transactions
WHERE transaction_id < 98345
ORDER BY transaction_id DESC
LIMIT 50;
```

---

## Advantages

- Faster for very large datasets.
- Uses indexes efficiently.
- More stable when new data arrives.

---

## Disadvantages

- Cannot easily jump to page 100.
- Cursor generation is more complex.

---

# 4. Offset vs Cursor Pagination

| Feature | Offset | Cursor |
|---|---|---|
| Simplicity | Easy | More complex |
| Jump to page | Yes | No |
| Large datasets | Poor | Excellent |
| Performance | Degrades | Consistent |
| Real-time feeds | Poor | Excellent |

---

## Interview Recommendation

For:

- Twitter feed
- Facebook feed
- Transaction history
- Event logs

Prefer:

```
Cursor pagination
```

---

For:

- Admin dashboards
- Small datasets

Offset pagination may be acceptable.

---

# 5. Filtering and Sorting

APIs should allow clients to retrieve only relevant data.

---

## Filtering

Example:

```
GET /transactions?status=FAILED
```

---

Multiple filters:

```
GET /transactions?
status=FAILED&
country=US
```

---

## Sorting

Example:

```
GET /transactions?sortBy=createdTime&order=desc
```

---

## Database Considerations

Design indexes around common queries.

Example:

Query:

```
WHERE userId = 123
ORDER BY createdTime DESC
```

A useful index:

```
(userId, createdTime)
```

---

# 6. API Response Optimization

Avoid sending unnecessary data.

---

## Field Selection

Example:

Default response:

```json
{
 "id":123,
 "name":"John",
 "email":"john@test.com",
 "address":"NY"
}
```

Client only needs:

```
name
```

Request:

```
GET /users/123?fields=name
```

Response:

```json
{
 "name":"John"
}
```

---

Benefits:

- Smaller payloads.
- Lower bandwidth.
- Faster responses.

---

# 7. API Caching

Frequently accessed data should be cached.

Examples:

- User profiles.
- Product information.
- Configuration data.

---

Architecture:

```
Client
 |
API Server
 |
Redis Cache
 |
Database
```

---

## Cache-Aside Pattern

Flow:

```
Request
 |
Check Redis
 |
Hit --> Return data

Miss
 |
Database
 |
Populate cache
 |
Return response
```

---

Benefits:

- Lower latency.
- Reduced database load.

---

Challenges:

- Stale data.
- Cache invalidation.

---

# 8. Synchronous vs Asynchronous APIs

---

## Synchronous API

The client waits until processing completes.

Example:

```
POST /payment
```

Flow:

```
Client
 |
API
 |
Payment Service
 |
Response
```

---

Good for:

- Fast operations.
- Immediate confirmation.

---

Problems:

- Client waits.
- Long-running tasks can timeout.

---

# 9. Asynchronous APIs

Used for expensive operations.

Examples:

- Report generation.
- Video processing.
- Bulk client screening.
- Data exports.

---

Flow:

```
POST /reports

Response:

202 Accepted

{
 jobId: 123,
 status: "PROCESSING"
}
```

---

Backend:

```
API
 |
Kafka Queue
 |
Worker Service
 |
Database/S3
```

---

Client checks status:

```
GET /jobs/123
```

---

Benefits:

- Better scalability.
- Protects application threads.
- Allows retries and replay.

---

# 10. Webhooks

Polling is inefficient when the server knows when an event completes.

Example:

Without webhook:

```
Client
 |
GET /jobs/123
 |
"Still processing"
```

Repeated every few seconds.

---

With webhook:

Client registers:

```
POST /webhooks

{
 callbackUrl:
 "https://client.com/result"
}
```

---

When complete:

```
Server
 |
POST callbackUrl
 |
Result delivered
```

---

Benefits:

- Lower API traffic.
- Faster notifications.
- Better user experience.

---

# 11. API Gateway Responsibilities

A gateway sits between clients and backend services.

Architecture:

```
Client
 |
API Gateway
 |
Microservices
```

---

Common responsibilities:

Authentication:

- Validate JWT.

---

Authorization:

- Enforce access policies.

---

Traffic control:

- Rate limiting.
- Throttling.

---

Routing:

- Direct requests to correct services.

---

Security:

- DDoS protection.
- Request validation.

---

Observability:

- Logging.
- Metrics.
- Trace IDs.

---

# 12. Rate Limiting at API Layer

Protect APIs from abuse.

Examples:

```
Free tier:
100 requests/minute


Premium:
10000 requests/minute
```

---

Common algorithms:

- Token bucket.
- Sliding window.
- Fixed window.

---

When limit exceeded:

```
429 Too Many Requests
```

---

# 13. API Observability

Monitor:

Performance:

- Request latency.
- P95/P99 response times.

---

Traffic:

- Requests per second.

---

Errors:

- 4xx/5xx rates.

---

Resources:

- CPU.
- Memory.
- Thread pools.

---

Dependency metrics:

- Database latency.
- Redis latency.
- External API latency.

---

Tools:

- Prometheus.
- Grafana.
- Distributed tracing.

---

# 14. Designing a Production-Ready API

A production API should consider:

Functionality:

- Correct endpoints.
- Good resource models.

---

Scalability:

- Stateless services.
- Horizontal scaling.
- Caching.
- Async processing.

---

Reliability:

- Timeouts.
- Retries.
- Circuit breakers.

---

Security:

- OAuth.
- JWT.
- TLS.
- Input validation.

---

Observability:

- Logs.
- Metrics.
- Tracing.

---

# 15. L6 Interview Discussion

## How would you design an API handling millions of requests per minute?

Discuss:

- Stateless services behind a load balancer.
- Horizontal scaling.
- Redis caching.
- API gateway.
- Rate limiting.
- Observability.

---

## Why use cursor pagination for a feed?

Because offset pagination becomes expensive and inconsistent when new records are continuously inserted.

---

## How would you process a request taking 30 minutes?

Do not keep the HTTP connection open.

Use:

```
202 Accepted
```

Create a job.

Process asynchronously using:

- Kafka.
- Worker services.

Return status through:

- Polling.
- Webhooks.

---

## How do you protect downstream services?

Use:

- Rate limiting.
- Controlled concurrency.
- Backpressure.
- Circuit breakers.
- Timeouts.

---

# Key Takeaways

1. Pagination prevents large responses from overwhelming systems.

2. Cursor pagination is preferred for large, continuously changing datasets.

3. APIs should expose filtering and sorting that align with database indexes.

4. Cache frequently accessed data to reduce latency.

5. Long-running operations should be asynchronous.

6. Webhooks reduce unnecessary polling.

7. API gateways centralize security, routing, and traffic management.

8. Production APIs require observability, security, and resilience.


---
# Chapter 13. Search Systems
---

# 1. Why Do We Need a Dedicated Search System?

Traditional databases are optimized for:

- Primary key lookups
- Range queries
- Joins
- Transactions


Example:

```sql
SELECT * FROM users WHERE userId = 123;
```

This is efficient because of indexes.


---

However, full-text search has different requirements:

Examples:

Search:

```
"best wireless headphones"
```

Need to find:

```
Document 1:
"The best Bluetooth headphones for travel"

Document 2:
"Top wireless audio devices"
```

The system must understand text, relevance, and ranking.

---

# 2. Search System Architecture

Typical architecture:

```
             User Query
                  |
            Search Service
                  |
            Elasticsearch
                  |
            Inverted Index


Content Creation
       |
     Database
       |
      Kafka
       |
 Search Index Consumer
       |
 Elasticsearch
```

The database remains the source of truth.

Elasticsearch maintains a searchable index.

---

# 3. Inverted Index

The fundamental data structure behind search engines.

Instead of:

```
Document -> Words
```

We store:

```
Word -> List of Documents
```

Example:

Documents:

```
Doc1:
"apple iphone case"

Doc2:
"iphone charger"

Doc3:
"apple watch"
```


Inverted index:

```
apple  -> Doc1, Doc3

iphone -> Doc1, Doc2

charger -> Doc2
```

---

# 4. Tokenization

Breaking text into searchable terms.

Example:

```
"Apple iPhone 16 Pro"
```

Tokens:

```
apple
iphone
16
pro
```

---

# 5. Normalization

Convert words into a standard format.

Examples:

Lowercasing:

```
Apple
apple
APPLE
```

becomes:

```
apple
```

---

Removing punctuation:

```
iPhone-16
```

becomes:

```
iphone 16
```

---

# 6. Stop Words

Common words with little meaning are often removed.

Examples:

```
a
the
is
of
and
```

Example:

```
"The history of computers"
```

Stored as:

```
history computers
```

---

# 7. Stemming

Reduce words to a common root.

Examples:

```
running
runs
runner
```

may become:

```
run
```

Benefits:

A search for:

```
run shoes
```

can also match:

```
running shoes
```

---

# 8. Ranking Results

Search results are not simply matching documents.

They are ranked by relevance.

Factors:

- Keyword frequency
- Term importance
- Popularity
- Freshness
- User behavior

---

## TF-IDF

Term Frequency:

How often a word appears in a document.


Inverse Document Frequency:

How unique the word is across all documents.


Example:

The word:

```
the
```

has low value.

The word:

```
kubernetes
```

has higher value.

---

Modern search engines often use BM25, an improvement over TF-IDF.

---

# 9. Elasticsearch Architecture

Elasticsearch distributes data using:

## Shards

A large index is split into smaller pieces.

Example:

```
Users Index

Shard 1
Shard 2
Shard 3
```

Benefits:

- Parallel searches
- Horizontal scaling

---

## Replicas

Copies of shards.

Benefits:

- High availability
- Increased read throughput


Example:

```
Shard 1
 |
Replica 1
```

---

# 10. Search Query Flow

Example:

User searches:

```
"wireless headphones"
```


Flow:

```
User
 |
API Gateway
 |
Search Service
 |
Elasticsearch
 |
Relevant document IDs
 |
Database
 |
Full document details
 |
User
```

Often Elasticsearch stores only searchable fields.

Detailed information may be fetched from the primary database.

---

# 11. Keeping Search Index Updated

Database is usually the source of truth.

Update flow:

```
Write Request
      |
 Database
      |
 Kafka Event
      |
 Indexing Consumer
      |
 Elasticsearch
```

---

## Eventual Consistency

A newly created document may not appear immediately in search.

Example:

User uploads a new photo.

Database:

Updated instantly.

Search index:

Updated a few seconds later.

This is often acceptable.

---

# 12. Autocomplete

Autocomplete requires very low latency.

Example:

User types:

```
iph
```

Suggestions:

```
iphone
iphone case
iphone charger
```

Techniques:

- Prefix trees (Tries)
- Elasticsearch completion suggester
- Cached popular queries

---

# 13. Search Caching

Common searches can be cached.

Examples:

```
weather today

world cup score

iphone 16
```

Caching reduces load on Elasticsearch.

---

# 14. Common L6 Interview Questions

## Why not search directly in MySQL?

Because relational indexes are not designed for:

- Tokenization
- Relevance ranking
- Full-text ranking at massive scale


---

## Why is Elasticsearch not the source of truth?

Because indexes can be rebuilt.

The database contains the authoritative data.

---

## Why use Kafka between database and Elasticsearch?

Benefits:

- Decouples systems
- Handles spikes
- Provides replayability
- Allows multiple consumers

---

## What happens if Elasticsearch goes down?

Possible approaches:

- Return cached results
- Degrade search functionality
- Queue indexing events and replay later

---

## How do you scale search?

Use:

- More shards
- More replicas
- Caching
- Distributed clusters

---

# Key Takeaways

1. Search systems use inverted indexes rather than traditional database indexes.

2. Elasticsearch provides distributed full-text search.

3. The database is the source of truth; search indexes are derived data.

4. Kafka is commonly used to synchronize updates asynchronously.

5. Search is eventually consistent.

6. Sharding and replication provide scalability and availability.

7. Ranking determines the most relevant results.

---
# Chapter 14. CDN and Object Storage
---

# 1. Why Not Store Large Files in a Database?

Databases are optimized for:

- Transactions
- Queries
- Indexes
- Relationships

They are not ideal for storing large binary objects.

Examples:

- Videos
- Images
- Audio files
- PDFs

Problems:

- Expensive storage
- Increased database backup size
- Slower database performance
- Difficult horizontal scaling

---

# 2. Object Storage

Object storage is designed to store massive amounts of unstructured data.

Examples:

- Amazon S3
- Google Cloud Storage
- Azure Blob Storage

Data is stored as objects:

```
Object
  |
  |-- Data (video/image/audio)
  |
  |-- Metadata
        |
        |-- Object ID
        |-- Size
        |-- Content type
        |-- Creation timestamp
```

---

# 3. Object Storage Architecture

Example:

A photo upload system.

```
User Upload
      |
API Service
      |
Metadata Database
      |
Object Storage (S3)
```

The database stores metadata:

Example:

Photo table:

```
photoId
userId
s3ObjectKey
createdTimestamp
visibility
```

The actual image bytes are stored in S3.

---

# 4. Why Use Object Storage?

Advantages:

- Virtually unlimited storage.
- High durability.
- Lower cost than database storage.
- Designed for large objects.
- Easy integration with CDNs.

---

# 5. Durability vs Availability

Object storage systems typically replicate data across multiple machines and availability zones.

Example:

```
Object
 |
--------------------
|        |          |
Copy 1  Copy 2    Copy 3
```

Benefits:

- Hardware failures do not cause data loss.
- Data can survive entire server failures.

---

# 6. Content Delivery Network (CDN)

A CDN is a geographically distributed network of cache servers that stores copies of content closer to users.

---

Without CDN:

```
User (India)
       |
       |
US Data Center
       |
S3 Storage
```

Problems:

- High latency.
- Increased network cost.
- High load on origin servers.

---

With CDN:

```
User (India)
       |
CDN Edge Server (India)
       |
S3 Origin (US)
```

Benefits:

- Lower latency.
- Reduced origin traffic.
- Better user experience.

---

# 7. CDN Cache Flow

## Cache Hit

The requested file already exists at the edge.

```
User
 |
CDN Edge Cache
 |
Response
```

Very low latency.

---

## Cache Miss

The file is not cached.

```
User
 |
CDN Edge
 |
Origin Storage (S3)
 |
Return File
 |
Store in CDN Cache
```

Future requests become cache hits.

---

# 8. Cache Invalidation

Problem:

A user updates their profile image.

Old image is still cached worldwide.

---

Solutions:

## TTL Expiration

Example:

```
profileImage.jpg

TTL = 1 hour
```

After expiration:

CDN fetches the latest version.

---

## Explicit Invalidation

Send a request to the CDN to remove cached objects.

Example:

```
Invalidate:
user123/profile.jpg
```

---

## Versioned URLs (Common Approach)

Instead of:

```
profile.jpg
```

Use:

```
profile_v2.jpg
```

or:

```
profile.jpg?v=2
```

Benefits:

- Avoids waiting for cache expiration.
- Users immediately receive the new file.

---

# 9. Upload Flow (Production Design)

A naive design:

```
Client
 |
Application Server
 |
S3
```

Problem:

Large files consume application bandwidth.

---

## Better Design: Pre-Signed URLs

Flow:

```
Client
 |
Request Upload URL
 |
Application Server
 |
Generate Signed S3 URL
 |
Client uploads directly to S3
```

Benefits:

- Application servers do not handle large files.
- Lower infrastructure cost.
- Better scalability.

---

# 10. Media Processing Pipeline

Large media often requires asynchronous processing.

Example:

Video upload.

```
User Upload
      |
Object Storage
      |
Kafka Event
      |
Media Processing Service
      |
-------------------------
|            |            |
1080p       720p        480p
versions
      |
Store processed files
      |
CDN
```

---

# 11. Why Multiple Video Resolutions?

Different users have different:

- Network speeds.
- Devices.
- Screen sizes.

Examples:

Mobile user:

```
480p
```

High-speed Wi-Fi:

```
1080p
```

Benefits:

- Lower bandwidth usage.
- Better user experience.

---

# 12. Handling Viral Content

Example:

A video suddenly receives 100 million views.

Without CDN:

```
100 million requests
       |
Origin Storage
```

Problems:

- Massive bandwidth cost.
- Potential overload.

---

With CDN:

```
100 million requests
       |
Global Edge Caches
       |
Small percentage reaches origin
```

---

# 13. Security Considerations

## Signed URLs

Prevent unauthorized access.

Example:

```
https://storage.com/file.mp4?token=abc123&expires=10PM
```

After expiration:

Access is denied.

---

## Access Control

Examples:

Private files:

- User-specific documents.
- Paid content.
- Internal videos.

The application verifies permissions before generating access URLs.

---

# 14. L6 Interview Discussion

## Why not store videos in MySQL?

Because databases are optimized for structured queries, not large binary files.

Use:

- Database → Metadata.
- Object storage → Actual media.

---

## Why use a CDN?

Because it:

- Reduces latency.
- Reduces origin traffic.
- Handles global scale.

---

## How do you update a file already cached by CDN?

Options:

- TTL expiration.
- Explicit cache invalidation.
- Versioned URLs (most common).

---

## How would you design YouTube/Instagram/Audible media delivery?

Architecture:

```
Metadata DB
      |
Object Storage
      |
CDN
      |
Users
```

Add:

- Async transcoding.
- Multiple quality formats.
- Access control.
- Analytics pipelines.

---

## Why use pre-signed URLs?

Because application servers should not become a bottleneck for large uploads.

---

# Key Takeaways

1. Databases store metadata; object storage stores large binary files.

2. Object storage provides durable, low-cost, massively scalable storage.

3. CDNs cache content close to users and reduce latency.

4. Cache invalidation can be handled through TTL, explicit invalidation, or versioned URLs.

5. Large uploads should bypass application servers using pre-signed URLs.

6. Media processing is usually asynchronous using events and workers.

7. CDNs are essential for handling viral global traffic.

# Communication Patterns: Polling, Long Polling, WebSockets, SSE, REST and gRPC

---

# 1. Why Do We Need Different Communication Models?

Different applications have different communication requirements.

Examples:

Simple API request:
- Get user profile
- Create an order

Works well with:

HTTP Request/Response.

---

Real-time applications:

- Chat messages
- Stock prices
- Sports scores
- Notifications
- Collaborative editing

Require the server to push updates to clients.

---

# 2. Traditional Request-Response Model (REST)

Most web applications follow a request-response pattern.

Example:

```
Client
   |
HTTP GET /users/123
   |
Server
   |
JSON Response
```

The connection closes after the response is sent.

---

Advantages:

- Simple.
- Stateless.
- Easy to scale.
- Supported everywhere.

---

Disadvantages:

- Server cannot send data unless the client requests it.
- Not suitable for real-time updates.

---

Use Cases:

- CRUD APIs.
- Microservice communication.
- Mobile applications.

---

# 3. Polling

## What is Polling?

The client repeatedly asks the server for updates.

Example:

Every 5 seconds:

```
Client
   |
GET /notifications
   |
Server
   |
No updates
```

5 seconds later:

```
Client
   |
GET /notifications
```

---

Advantages:

- Very simple to implement.
- Works with standard HTTP infrastructure.

---

Disadvantages:

- Generates many unnecessary requests.
- Higher server load.
- Higher network usage.
- Updates are delayed until the next poll interval.

---

Example:

Polling every 10 seconds:

A message arrives at second 1.

User receives it at second 10.

Latency = 9 seconds.

---

# 4. Short Polling vs Long Polling

Short polling:

```
Client
   |
Request
   |
Immediate response
```

Client retries after a fixed interval.

---

Long polling:

The server keeps the HTTP connection open until:

- New data becomes available.
- A timeout occurs.

Example:

```
Client
   |
Request
   |
Server waits...
   |
New message arrives
   |
Response returned
```

The client immediately creates another request.

---

Advantages:

- Lower latency than polling.
- Fewer unnecessary requests.

---

Disadvantages:

- Each update still requires creating a new HTTP connection.
- Large numbers of waiting connections consume server resources.

---

# 5. WebSockets

## What is WebSocket?

WebSocket provides a persistent, full-duplex connection between client and server.

Once established:

```
Client
   ⇅
WebSocket Connection
   ⇅
Server
```

Both sides can send messages at any time.

---

Advantages:

- Real-time communication.
- Very low latency.
- Lower overhead after connection establishment.
- Supports bidirectional communication.

---

Examples:

- WhatsApp
- Slack
- Online gaming
- Live collaboration
- Trading applications

---

# 6. WebSocket Architecture at Scale

A simple architecture:

```
Clients
    |
WebSocket Servers
    |
Message Broker
    |
Backend Services
```

---

Example:

User A sends a message.

```
User A
   |
WebSocket Server A
   |
Kafka / Redis PubSub
   |
WebSocket Server B
   |
User B
```

---

Why use Kafka or Redis Pub/Sub?

Because:

User A and User B may be connected to different WebSocket servers.

A message bus allows servers to share events.

---

# 7. Server-Sent Events (SSE)

SSE is a one-way streaming protocol over HTTP.

Communication:

```
Server
   |
Continuous stream
   |
Client
```

---

Characteristics:

- Server → Client only.
- Uses HTTP.
- Automatically reconnects.
- Lightweight.

---

Examples:

- Live notifications.
- News feeds.
- Monitoring dashboards.
- Progress updates.

---

# 8. WebSocket vs SSE

| Feature | WebSocket | SSE |
|---|---|---|
| Communication | Bidirectional | Server → Client |
| Protocol | WebSocket | HTTP |
| Connection | Persistent | Persistent |
| Complexity | Higher | Lower |
| Binary data | Supported | Not ideal |
| Auto reconnect | Application handles | Built-in |
| Best for | Chat, games | Notifications, updates |

---

# 9. REST vs gRPC

## REST

Uses:

- HTTP/HTTPS
- JSON payloads

Example:

```
GET /users/123
```

---

Advantages:

- Human readable.
- Browser friendly.
- Widely adopted.

---

Disadvantages:

- Larger payloads.
- Slower serialization.

---

## gRPC

Uses:

- HTTP/2.
- Protocol Buffers (binary format).

Example:

```
Client
   |
gRPC
   |
Service
```

---

Advantages:

- Smaller messages.
- Faster serialization.
- Supports streaming.
- Strongly typed contracts.

---

Disadvantages:

- Less browser friendly.
- Harder to inspect manually.

---

Use Cases:

REST:
- Public APIs.
- Mobile applications.
- External integrations.

gRPC:
- Internal microservice communication.
- High throughput services.
- Low latency systems.

---

# 10. Communication Pattern Selection

## User Profile API

Choice:

REST

Reason:

Simple request-response.

---

## Chat Application

Choice:

WebSocket

Reason:

Bidirectional real-time communication.

---

## Live Notification System

Choice:

SSE or WebSocket

Reason:

Server pushes events to clients.

---

## Microservice Communication

Choice:

REST or gRPC.

REST:
- Simpler.
- More common.

gRPC:
- Better performance.
- Streaming support.

---

## Real-Time Dashboard

Choice:

SSE.

Reason:

Server only sends updates.

---

# 11. Scaling Real-Time Connections

A single server cannot maintain millions of WebSocket connections.

Solutions:

## Horizontal Scaling

```
Clients
   |
Load Balancer
   |
----------------
|              |
WS Server A   WS Server B
```

---

## Connection Routing

Users are assigned to WebSocket servers.

Example:

```
User 123
      |
Hash(UserId)
      |
WebSocket Server 5
```

---

## Shared Message Bus

Servers communicate using:

- Kafka
- Redis Pub/Sub

---

# 12. Reliability Considerations

## Connection Failure

Client should reconnect automatically.

---

## Message Loss

Use:

- Sequence numbers.
- Message acknowledgements.
- Persistent storage.

Example:

Chat systems store messages in a database.

---

## Backpressure

A slow client can cause memory buildup.

Solutions:

- Buffer limits.
- Drop old messages.
- Disconnect slow clients.

---

# 13. L6 Interview Discussion

## Why not use polling for chat?

Because millions of clients polling every few seconds create enormous unnecessary traffic.

---

## When would you choose WebSockets over REST?

When both client and server need to exchange data in real time.

Examples:

- Chat.
- Gaming.
- Collaboration.

---

## Why use SSE instead of WebSocket?

When only the server sends updates.

Examples:

- Notifications.
- Live dashboards.

SSE is simpler and works over HTTP.

---

## Why use Kafka with WebSocket servers?

WebSocket servers are usually stateless.

Users are connected to different servers.

Kafka distributes events between servers.

---

## REST vs gRPC?

REST:

- Easier integration.
- Human-readable.
- Good for external APIs.

gRPC:

- Better performance.
- Smaller payloads.
- Streaming support.

---

# Key Takeaways

1. REST is best for simple request-response APIs.

2. Polling is simple but inefficient for real-time systems.

3. Long polling reduces unnecessary requests but still creates repeated HTTP connections.

4. WebSockets provide persistent bidirectional communication.

5. SSE provides simple server-to-client streaming.

6. gRPC provides high-performance communication between internal services.

7. Real-time systems usually combine WebSockets with a message broker like Kafka or Redis Pub/Sub.

8. At large scale, connection management, message routing, and backpressure become major challenges.

# System Design - Observability

---

# 1. What is Observability?

Observability is the ability to understand the internal state of a system by analyzing its external outputs.

The three pillars of observability are:

1. Metrics
2. Logs
3. Distributed Traces

Together they answer:

- Is the system healthy?
- What failed?
- Where did the failure occur?
- Why did it happen?
- Which users are impacted?

---

# 2. Why Is Observability Important?

Large distributed systems contain many components:

Example:

```
Client
   |
API Gateway
   |
User Service
   |
Order Service
   |
Database
```

If a request takes 10 seconds, the question is:

- Is the API slow?
- Is the database overloaded?
- Is a downstream service failing?
- Is the network experiencing latency?

Observability helps identify the bottleneck quickly.

---

# 3. Metrics

Metrics are numerical measurements collected over time.

Examples:

- Requests per second (RPS)
- CPU utilization
- Memory usage
- Disk usage
- Database connections
- Queue length
- Kafka consumer lag
- Cache hit ratio
- Error rate
- Response latency

---

# 4. Types of Metrics

## Counter

A value that only increases.

Examples:

```
Total requests = 10,000,000
Total errors = 5,000
```

Examples in Prometheus:

```
http_requests_total
kafka_messages_processed_total
```

---

## Gauge

A value that can increase or decrease.

Examples:

```
Current CPU = 60%
Active connections = 200
Queue size = 500
```

---

## Histogram

Measures the distribution of values.

Example:

Request latency:

```
10ms
20ms
100ms
500ms
```

Useful for calculating:

- Average latency
- Percentiles (P50, P95, P99)

---

## Why Percentiles Matter

Average latency can be misleading.

Example:

100 requests:

```
99 requests = 10ms
1 request = 5000ms
```

Average:

```
~60ms
```

Looks healthy.

But one user waited 5 seconds.

Therefore, production systems monitor:

- P50 (median)
- P95
- P99

---

# 5. The Four Golden Signals

Google identifies four key metrics for distributed systems.

---

## Latency

How long requests take.

Example:

```
P95 latency > 500ms
```

may indicate a problem.

---

## Traffic

Amount of work the system receives.

Examples:

- Requests per second
- Messages per second

---

## Errors

Requests that fail.

Examples:

- HTTP 5xx
- Failed database calls
- Kafka processing failures

---

## Saturation

How close the system is to its limit.

Examples:

- CPU at 95%
- Thread pool exhaustion
- Database connection pool full
- Disk almost full

---

# 6. Logging

Logs capture discrete events that occurred in the system.

Examples:

```
User 123 logged in

Payment failed for order 456

Kafka message processing failed
```

---

# 7. Log Levels

## DEBUG

Very detailed information.

Example:

```
Entering calculatePrice()
Input value = 123
```

Usually disabled in production.

---

## INFO

Normal application events.

Examples:

- Application started.
- Scheduled job completed.

---

## WARN

Unexpected events that the system can recover from.

Example:

```
Vendor API took 3 seconds to respond.
```

---

## ERROR

Failures requiring investigation.

Example:

```
Database connection failed.
```

---

# 8. Structured Logging

Avoid:

```
User failed login
```

Better:

```json
{
  "timestamp": "2025-01-01T10:00:00",
  "userId": 123,
  "action": "LOGIN",
  "status": "FAILED"
}
```

Benefits:

- Searchable
- Filterable
- Easy analytics

---

# 9. Centralized Logging

In distributed systems, logs from thousands of servers must be aggregated.

Architecture:

```
Application Servers
        |
 Log Collector
        |
 Log Storage
        |
 Search Dashboard
```

Examples:

- ELK Stack
  - Elasticsearch
  - Logstash
  - Kibana

- Splunk

- Cloud logging services

---

# 10. Distributed Tracing

Tracing follows a request as it travels through multiple services.

Example:

```
User Request
     |
API Gateway
     |
User Service
     |
Order Service
     |
Database
```

A trace shows:

- Total request duration
- Time spent in each service
- Failed dependency calls

---

# 11. Trace ID and Span ID

## Trace ID

A unique identifier for the entire request.

Example:

```
Trace ID: abc123
```

It follows the request across every service.

---

## Span

A single unit of work within a trace.

Example:

```
Trace abc123

API Gateway:
50ms

User Service:
100ms

Database:
800ms
```

The database is identified as the bottleneck.

---

# 12. OpenTelemetry

OpenTelemetry is the industry standard for collecting:

- Metrics
- Logs
- Traces

It provides a common instrumentation framework.

---

# 13. Prometheus and Grafana

## Prometheus

A metrics collection system.

Responsibilities:

- Scrapes metrics from applications.
- Stores time-series data.
- Supports querying with PromQL.

Example:

```
http_requests_total
```

---

## Grafana

Visualization and alerting platform.

Used to build dashboards.

Examples:

- API latency dashboard
- Kafka consumer lag dashboard
- JVM memory dashboard

---

# 14. Alerting

Monitoring without alerts is not enough.

Examples:

Trigger an alert when:

```
P95 latency > 500ms for 5 minutes
```

or:

```
Error rate > 5%
```

or:

```
Kafka consumer lag > 100,000 messages
```

---

# 15. SLIs, SLOs, and SLAs

## SLI (Service Level Indicator)

A measured metric.

Examples:

- Availability
- Latency
- Error rate

---

## SLO (Service Level Objective)

A target for an SLI.

Example:

```
99.9% of requests complete in under 200ms.
```

---

## SLA (Service Level Agreement)

A contractual commitment.

Example:

```
99.9% uptime guaranteed to customers.
```

Violations may have financial penalties.

---

# 16. Observability for Kafka Systems

Important metrics:

Producer:

- Publish rate
- Failed sends
- Retry count

Broker:

- CPU
- Disk usage
- Network throughput

Consumer:

- Consumer lag
- Processing latency
- Failed messages
- DLQ count

---

# 17. Observability for APIs

Monitor:

- Request volume
- P95/P99 latency
- HTTP status codes
- Dependency latency
- Thread pool utilization
- Database connection pool usage

---

# 18. L6 Interview Discussion

## A service suddenly becomes slow. How do you debug?

Start with metrics.

Questions:

- Did traffic increase?
- Did latency increase?
- Did error rates increase?
- Is CPU or memory saturated?
- Is the database slow?
- Is an external API slow?

Then use traces.

Find:

- Which service is consuming the most time.

Then use logs.

Find:

- The exact failure or exception.

---

## Why not rely only on logs?

Logs explain individual events.

But they do not easily answer:

- Is the system gradually slowing?
- What percentage of users are impacted?
- Is latency increasing over time?

Metrics provide trends.

---

## Why are traces needed?

Because a single user request may travel through many services.

Traces reveal where time is spent.

---

## What would you monitor for your vendor integration?

Metrics:

- Request rate
- P95 latency
- Timeout count
- Error percentage
- Retry count
- Circuit breaker open events

Also monitor:

- Thread pool usage
- Queue size
- Rate limiter rejections

---

# Key Takeaways

1. Observability is built on metrics, logs, and traces.

2. Metrics show trends and system health.

3. Logs provide detailed event information.

4. Traces identify latency across service boundaries.

5. Monitor the four golden signals:
   - Latency
   - Traffic
   - Errors
   - Saturation

6. Prometheus collects metrics; Grafana visualizes them.

7. Good alerts focus on customer impact, not just machine health.

8. An L6 engineer uses observability to reduce mean time to detection (MTTD) and mean time to recovery (MTTR).

# Performance Engineering

---

# 1. What is Performance Engineering?

Performance engineering is the practice of designing, measuring, and optimizing systems to meet performance goals.

Key goals:

- Low latency
- High throughput
- Efficient resource utilization
- Predictable behavior under load

---

## Important Metrics

### Latency

How long an individual request takes.

Example:

```
User clicks Play
        |
Audio starts in 50 ms
```

Latency = 50 ms

---

### Throughput

The amount of work completed per unit time.

Examples:

- 10,000 requests/sec
- 1 million messages/hour
- 100 GB processed/hour

---

## Latency vs Throughput Trade-Off

Increasing throughput may increase latency.

Example:

A worker consumes Kafka messages.

Option A:

Process one message at a time:

```
Low throughput
Low latency
```

---

Option B:

Process messages in batches:

```
Higher throughput
Higher latency
```

---

The correct choice depends on business requirements.

---

# 2. Capacity Planning

Before designing a system, estimate expected load.

Questions:

- How many users?
- Requests per second?
- Read/write ratio?
- Peak traffic?
- Data growth?

---

Example:

System receives:

```
10,000 requests/sec
```

Average request time:

```
200 ms
```

How many concurrent requests are being processed?

---

## Little's Law

```
Concurrency = Throughput × Latency
```

Example:

```
Throughput = 10,000 requests/sec

Latency = 0.2 seconds
```

Therefore:

```
Concurrency = 10,000 × 0.2

= 2,000 requests
```

The system must support roughly 2,000 concurrent requests.

---

# 3. CPU Bound vs I/O Bound Workloads

Understanding the workload determines scaling strategy.

---

## CPU Bound

The application spends most of its time performing computations.

Examples:

- Image processing
- Video encoding
- Machine learning inference
- Encryption

---

Characteristics:

- High CPU usage.
- Low waiting time.

Optimization:

- Increase CPU resources.
- Use parallel processing.
- Optimize algorithms.

---

## I/O Bound

The application spends most of its time waiting.

Examples:

- Database queries.
- External API calls.
- Disk operations.
- Network communication.

---

Characteristics:

```
Thread
 |
CPU work (5 ms)
 |
Waiting on DB (100 ms)
```

Most of the time is spent waiting.

---

Optimization:

- Asynchronous processing.
- More concurrent workers.
- Connection pooling.
- Caching.

---

# 4. Thread Pools

Creating a new thread for every request is expensive.

Problems:

- Memory overhead.
- Context switching.
- Thread exhaustion.

---

Use a thread pool.

```
Request Queue
      |
 Thread Pool
      |
 Worker Threads
```

---

## CPU Bound Thread Pools

Usually:

```
Number of threads ≈ Number of CPU cores
```

Reason:

More threads cause excessive context switching.

---

## I/O Bound Thread Pools

More threads can be useful.

Example:

```
100 threads
 |
90 waiting for network calls
 |
10 actively using CPU
```

---

However, unlimited threads are dangerous.

---

## Thread Pool Exhaustion

Example:

```
Thread Pool Size = 100

Incoming requests = 10,000
```

Requests accumulate.

Problems:

- Increased latency.
- Memory growth.
- Application crashes.

---

Solutions:

- Bounded queues.
- Backpressure.
- Request rejection.
- Rate limiting.

---

# 5. Connection Pools

Connections are expensive to create.

Examples:

- Database connections.
- HTTP connections.

---

Without pooling:

```
Request
 |
Open TCP connection
 |
Execute query
 |
Close connection
```

This creates unnecessary overhead.

---

With a pool:

```
Connection Pool

Connection 1
Connection 2
Connection 3
```

Connections are reused.

---

## Why Not Create Infinite Connections?

Because downstream systems have limits.

Example:

Database supports:

```
500 connections
```

Application opens:

```
10,000 connections
```

The database becomes overloaded.

---

Therefore connection pools also provide controlled concurrency.

---

# 6. Batching

Processing many small operations individually can be inefficient.

---

Example:

Bad:

```
Insert 1 row

Insert 1 row

Insert 1 row
```

---

Better:

```
Batch Insert:

1000 rows
```

---

Benefits:

- Fewer network round trips.
- Higher throughput.
- Better resource utilization.

---

Trade-Off:

Large batches may increase latency.

---

# 7. Caching and Performance

Caching improves performance by avoiding expensive operations.

Examples:

- Database queries.
- API responses.
- Computation results.

---

Benefits:

- Lower latency.
- Higher throughput.
- Reduced load on dependencies.

---

Trade-Offs:

- Stale data.
- Cache invalidation complexity.
- Memory cost.

---

# 8. Load Testing

Performance should be tested before production.

---

## Load Testing

Tests expected traffic.

Example:

```
Expected:
10,000 requests/sec
```

---

## Stress Testing

Increase traffic until the system breaks.

Purpose:

Find maximum capacity.

---

## Spike Testing

Sudden traffic increase.

Example:

```
1,000 requests/sec
        ↓
100,000 requests/sec
```

Tests:

- Auto-scaling.
- Rate limiting.
- Backpressure.

---

## Soak Testing

Run traffic for long periods.

Finds:

- Memory leaks.
- Resource exhaustion.
- Connection leaks.

---

# 9. Identifying Bottlenecks

A slow system can fail for many reasons.

---

## CPU Bottleneck

Symptoms:

- High CPU usage.
- Increased response times.

Solutions:

- More CPU.
- Better algorithms.
- Horizontal scaling.

---

## Memory Bottleneck

Symptoms:

- Frequent garbage collection.
- Out of memory errors.

Solutions:

- Reduce object creation.
- Increase memory.
- Tune garbage collection.

---

## Database Bottleneck

Symptoms:

- Slow queries.
- Connection pool exhaustion.

Solutions:

- Indexing.
- Caching.
- Query optimization.
- Read replicas.
- Partitioning.

---

## Network Bottleneck

Symptoms:

- High latency.
- Timeouts.

Solutions:

- Compression.
- CDN.
- Reduce payload size.
- Improve network architecture.

---

## External Dependency Bottleneck

Example:

Vendor API takes:

```
5 seconds
```

Problems:

- Threads become blocked.
- Thread pools exhaust.
- System latency increases.

---

Solutions:

- Timeouts.
- Circuit breakers.
- Controlled concurrency.
- Rate limiting.
- Asynchronous processing.

---

# 10. Queue-Based Systems and Backlogs

Example:

Kafka consumers process:

```
5,000 messages/sec
```

Producers send:

```
20,000 messages/sec
```

Result:

```
Consumer Lag Increases
```

---

Solutions:

- Add more consumers.
- Increase partitions.
- Improve processing efficiency.
- Apply backpressure upstream.

---

# 11. Performance Optimization Process

A common mistake:

```
System is slow
      |
Add more servers
```

---

Better approach:

```
Measure
   |
Identify bottleneck
   |
Optimize the bottleneck
   |
Scale if necessary
```

---

Example:

A slow API:

```
Request
 |
API Service
 |
Database
```

Metrics show:

```
Database latency = 2 seconds
API CPU = 20%
```

Adding API servers will not help.

The database is the bottleneck.

---

# 12. L6 Interview Discussion

## How do you improve a slow system?

My process:

1. Examine metrics.

Look at:

- Latency.
- Throughput.
- Error rates.
- CPU.
- Memory.
- Dependency latency.

---

2. Identify the bottleneck.

Examples:

- Database.
- Network.
- External API.
- Thread pool.
- Disk.

---

3. Apply targeted solutions.

Examples:

Database:

- Indexing.
- Caching.
- Replication.

External dependency:

- Timeouts.
- Controlled concurrency.
- Circuit breakers.

---

4. Validate improvements using load testing.

---

## Why not increase thread pool size infinitely?

More threads cause:

- More memory usage.
- More context switching.
- Higher pressure on downstream systems.

---

## How do you determine thread pool size?

Depends on workload:

CPU-bound:

Approximately CPU core count.

I/O-bound:

More threads are acceptable because many wait for I/O.

---

## How do you handle traffic spikes?

Use:

- Auto-scaling.
- Caching.
- Queue buffering.
- Rate limiting.
- Backpressure.

---

# Key Takeaways

1. Latency measures response time; throughput measures work completed.

2. Use Little's Law for capacity estimation.

3. CPU-bound and I/O-bound systems require different optimization strategies.

4. Thread pools and connection pools provide controlled concurrency.

5. Batching improves throughput but can increase latency.

6. Performance tuning begins with measurement, not guessing.

7. Load testing reveals system limits before production.

8. The bottleneck determines the scaling strategy.

9. More hardware does not solve every performance problem.


# Machine Learning Basics for System Design (Low Priority)

---

# 1. Why Should Backend Engineers Understand ML?

Most large-scale systems include ML components.

Examples:

Recommendation systems:

```
User
 |
Recommendation Service
 |
ML Model
 |
Ranked Results
```

Examples:

- "Customers who bought this also bought..."
- "Recommended for you"
- Personalized home pages

---

# 2. High-Level ML Pipeline

A typical ML system has two major phases:

1. Training
2. Inference

---

# 3. Training Phase (Offline)

Training creates the model.

Flow:

```
User Events
     |
Kafka
     |
Data Lake (S3)
     |
Feature Engineering
     |
Training Jobs (Spark)
     |
ML Model
     |
Model Registry
```

Examples of user events:

- Clicks
- Purchases
- Searches
- Likes
- Listening history

---

## Characteristics

Usually:

- Batch processing
- Runs hourly or daily
- Processes massive datasets

Requirements:

- High throughput
- Large storage
- Distributed processing

---

# 4. Model Deployment

After training:

```
Model Registry
       |
Model Serving Platform
       |
Inference API
```

The model is loaded into servers that can answer prediction requests.

---

# 5. Inference (Online Prediction)

Inference is the real-time use of a model.

Example:

User opens Audible.

Flow:

```
User
 |
API Gateway
 |
Recommendation Service
 |
Feature Store
 |
ML Model Service
 |
Top 10 Recommendations
 |
User
```

---

## Characteristics

Requires:

- Low latency
- High availability
- Fast feature lookup

Typical latency goals:

- Tens of milliseconds

---

# 6. Feature Store

Features are the inputs to an ML model.

Examples:

User features:

```
User age
Country
Subscription type
Favorite genres
```

Behavior features:

```
Books listened in last 7 days
Average listening time
Recent searches
```

---

## Why Use a Feature Store?

Benefits:

- Reuse features across models
- Keep training and inference consistent
- Low-latency access

---

# 7. Batch vs Real-Time Features

## Batch Features

Updated periodically.

Example:

```
Average books listened per month
```

Generated using:

- Spark
- Batch jobs

---

## Streaming Features

Updated in real time.

Example:

```
Last book listened to
Current session activity
```

Pipeline:

```
Events
 |
Kafka
 |
Stream Processing
 |
Feature Store
```

---

# 8. Recommendation System Architecture

Example:

```
                 User
                   |
              API Gateway
                   |
          Recommendation Service
                   |
          -----------------------
          |                     |
    Feature Store           ML Model
          |                     |
          -----------Prediction--
                   |
             Ranking Service
                   |
             Top Results
```

---

# 9. Candidate Generation vs Ranking

Large systems do not score every item.

Example:

Amazon has hundreds of millions of products.

You cannot score every product in real time.

---

## Candidate Generation

Quickly retrieves a smaller set.

Example:

```
100 million books
        |
 Candidate Generator
        |
 1000 candidates
```

---

## Ranking

ML model evaluates candidates.

Example:

```
1000 books
     |
Ranking Model
     |
Top 20 books
```

---

# 10. Batch Recommendation vs Real-Time Recommendation

## Batch Recommendation

Example:

Netflix daily recommendations.

Flow:

```
Offline Job
      |
Generate recommendations
      |
Store in Redis
      |
Serve quickly
```

Advantages:

- Very low latency
- Cheap online serving

Disadvantages:

- Not personalized to recent activity

---

## Real-Time Recommendation

Example:

You listened to a mystery audiobook 30 seconds ago.

The next recommendations should change.

Flow:

```
Recent Event
      |
Kafka
      |
Feature Update
      |
Model Inference
```

Advantages:

- More personalized

Disadvantages:

- Higher complexity
- Higher infrastructure cost

---

# 11. Model Versioning

Models change over time.

Example:

```
Model V1
Model V2
Model V3
```

Need the ability to:

- Deploy new versions
- Roll back bad models
- Compare performance

---

# 12. A/B Testing

Different users see different model versions.

Example:

```
10% Users → Model A
90% Users → Model B
```

Measure:

- Click-through rate
- Listening time
- Purchases
- Conversion

---

# 13. Model Monitoring

ML systems can degrade over time.

---

## Data Drift

Input data changes.

Example:

A recommendation model trained on old user behavior no longer matches current trends.

---

## Performance Monitoring

Track:

- Prediction latency
- Error rate
- Resource usage
- Business metrics

Examples:

- Click-through rate
- Engagement
- Conversion

---

# 14. ML System Challenges

## Cold Start Problem

New users have no history.

Solutions:

- Popular content
- Demographic signals
- Contextual recommendations

---

## Scalability

Millions of users may request recommendations simultaneously.

Solutions:

- Cache popular recommendations
- Pre-compute results
- Scale model servers horizontally

---

## Latency

Models may be expensive.

Solutions:

- Simpler models for online inference
- Cache predictions
- Use batch recommendations

---

# 15. L6 Interview Discussion

## Why not compute recommendations every request?

Because:

- Models may be expensive.
- Millions of users create huge load.

Instead:

- Pre-compute recommendations.
- Cache results.
- Use real-time inference only where necessary.

---

## Why use Kafka in ML pipelines?

Because Kafka provides:

- Decoupling
- Buffering during traffic spikes
- Replayability for retraining
- Multiple consumers

Examples:

Events can feed:

- Data lake
- Real-time features
- Analytics systems

---

## Why separate training and inference?

Training:

- Compute intensive
- Can run offline

Inference:

- Requires low latency and high availability

---

## How do you handle a new user with no history?

Cold start strategies:

- Popular items
- Trending content
- User demographics
- Context signals

---

# Key Takeaways

1. ML systems have two major phases:
   - Offline training
   - Online inference

2. Kafka often connects event generation with data pipelines.

3. Feature stores provide consistent low-latency access to model features.

4. Recommendation systems often use:
   - Candidate generation
   - Ranking

5. Many recommendations are precomputed and cached.

6. Real-time personalization provides better user experience but increases complexity.

7. Models require monitoring, versioning, and A/B testing.



# Chapter 15 Microservices Architecture

---

# 1. What Are Microservices?

A microservice architecture is a design approach where a large application is divided into multiple small, independent services.

Each service:

- Owns a specific business capability.
- Has its own deployment lifecycle.
- Can scale independently.
- Communicates with other services using network protocols.

Example: E-commerce system

```
                 Client
                    |
               API Gateway
                    |
   -----------------------------------
   |                |                |
User Service   Order Service   Payment Service
   |                |                |
 User DB        Order DB        Payment DB
```

Each team can develop, deploy, and scale its service independently.

---

# 2. Monolith vs Microservices

## Monolithic Architecture

A monolith contains all application modules inside a single deployable unit.

Example:

```
                 Application
                      |
    --------------------------------
    |              |               |
 Users         Orders          Payments
                      |
                 Single Database
```

---

## Advantages of Monoliths

- Simpler development.
- Easier debugging.
- No network communication overhead.
- Easier local testing.
- Simple deployments.

---

## Problems as Systems Grow

A large monolith may experience:

- Long deployment cycles.
- Difficult scaling.
- Tight coupling between teams.
- Large code bases.
- Higher risk deployments.

---

## Advantages of Microservices

### Independent Deployment

A payment service can be deployed without redeploying user services.

---

### Independent Scaling

Example:

An e-commerce system receives:

```
10,000 requests/sec
```

Most traffic goes to product search.

Instead of scaling the entire application:

```
10 User Servers
10 Payment Servers
100 Search Servers
```

Each service scales based on demand.

---

### Technology Flexibility

Different services may choose technologies based on requirements.

Example:

```
User Service       -> PostgreSQL
Search Service     -> Elasticsearch
Caching Layer      -> Redis
Analytics          -> Kafka
```

---

### Fault Isolation

A failure in one service does not necessarily bring down the entire system.

Example:

```
Recommendation Service fails
              |
              |
Product page still loads
without recommendations.
```

This is called graceful degradation.

---

# 3. Challenges of Microservices

Microservices are not free.

They introduce distributed system complexity.

---

## Network Latency

In a monolith:

```
OrderService.callPayment()
```

is a simple method call.

In microservices:

```
Order Service
      |
 HTTP/gRPC
      |
Payment Service
```

Network calls have:

- Latency.
- Timeouts.
- Partial failures.

---

## Distributed Failures

Example:

```
Client
 |
Order Service
 |
Payment Service
 |
Database
```

Questions:

- What happens if Payment Service is slow?
- What if the network fails?
- What if the database is unavailable?

Solutions:

- Timeouts.
- Retries.
- Circuit breakers.
- Fallback mechanisms.

---

## Operational Complexity

More services mean:

- More deployments.
- More monitoring.
- More logs.
- More configuration management.

---

# 4. Service Boundaries and Domain Driven Design

One of the most important decisions in microservices is determining service boundaries.

A bad split creates excessive communication between services.

---

## Domain Driven Design (DDD)

DDD recommends organizing services around business domains.

Examples:

```
User Domain
     |
User Service


Order Domain
     |
Order Service


Payment Domain
     |
Payment Service
```

Each service owns its business logic and data.

---

## Avoid a Distributed Monolith

A common mistake:

```
Request

User Service
      |
Order Service
      |
Inventory Service
      |
Payment Service
      |
Shipping Service
```

Problems:

- High latency.
- More failure points.
- Complex debugging.
- Cascading failures.

Microservices should minimize unnecessary synchronous dependencies.

---

# 5. Database Per Service Pattern

A fundamental microservice principle is that each service owns its data.

Example:

```
User Service
      |
 User Database


Order Service
      |
 Order Database


Payment Service
      |
 Payment Database
```

---

## Why Not Use a Shared Database?

Shared databases create tight coupling.

Example:

Payment team changes a table schema.

Order service unexpectedly breaks.

---

Benefits of database ownership:

- Independent deployments.
- Independent schema evolution.
- Clear ownership.

---

# 6. Service Communication

Microservices communicate either synchronously or asynchronously.

---

## Synchronous Communication

Examples:

- REST APIs.
- gRPC.

Architecture:

```
Order Service
      |
      REST
      |
Payment Service
```

Advantages:

- Simple request-response model.
- Immediate result.

Disadvantages:

- Runtime dependency.
- Increased latency.
- Cascading failures.

---

## Asynchronous Communication

Examples:

- Kafka.
- RabbitMQ.
- Amazon SQS.

Architecture:

```
Order Service
      |
   Event
      |
    Kafka
      |
Payment Service
```

Advantages:

- Loose coupling.
- Better traffic handling.
- Independent scaling.
- Improved resilience.

Disadvantages:

- Eventual consistency.
- More difficult debugging.
- Additional operational complexity.

---

# 7. Distributed Transactions

In a monolith, a single database transaction can update multiple tables.

Example:

```
BEGIN TRANSACTION

Update Order Table

Update Payment Table

COMMIT
```

---

In microservices:

```
Order Database

Payment Database

Inventory Database
```

A single ACID transaction across services becomes difficult.

---

## Why Not Use Two-Phase Commit (2PC)?

Although possible, 2PC introduces:

- Higher latency.
- Coordinator failures.
- Reduced availability.
- Poor scalability.

It is uncommon in large internet-scale systems.

---

## Saga Pattern

A saga breaks a large transaction into multiple local transactions.

Example:

```
Create Order

      |
Reserve Inventory

      |
Charge Payment

      |
Create Shipment
```

If a later step fails, previous operations are compensated.

Example:

```
Payment failed

      |
Release Inventory

      |
Cancel Order
```

---

## Choreography

Services communicate using events.

Example:

```
Order Created Event
          |
Inventory Service
          |
Payment Service
```

Advantages:

- Looser coupling.

Disadvantages:

- Complex workflows can become difficult to understand.

---

## Orchestration

A central coordinator manages the workflow.

Example:

```
Saga Orchestrator

        |
--------------------------------
|              |              |
Order       Inventory      Payment
```

Advantages:

- Easier visibility.
- Centralized workflow.

Disadvantages:

- Orchestrator becomes a critical component.

---

# 8. API Composition

Sometimes a client requires data from multiple services.

Example:

Order Details Page requires:

- Order information.
- Customer information.
- Product information.

---

## Option 1: Client Composition

```
Mobile App
   |
-----------------------
|          |          |
Order    User      Product
Service  Service   Service
```

Problem:

Many network calls from the client.

---

## Option 2: API Gateway Aggregation

```
Client
  |
API Gateway
  |
-----------------------
|          |          |
Order    User      Product
```

The gateway combines responses.

---

## Option 3: Backend For Frontend (BFF)

Create a backend specifically optimized for a client.

Example:

```
Mobile BFF

Web BFF

Smart TV BFF
```

Each exposes APIs optimized for that client.

---

# 9. Service Discovery

In dynamic environments, service locations change.

Example:

```
Payment Service

Instance A: 10.0.1.5
Instance B: 10.0.1.8
```

Instances may be created or removed automatically.

---

## Client-Side Discovery

The client asks a registry:

```
Service Registry
        |
Client chooses instance
```

Example:

- Netflix Eureka.

---

## Server-Side Discovery

A load balancer or proxy performs discovery.

Example:

```
Client
   |
Load Balancer
   |
Payment Service
```

Examples:

- Kubernetes Services.
- API Gateways.

---

# 10. Configuration and Secret Management

Configuration should be separated from application code.

Examples:

- Database URLs.
- Feature flags.
- Environment settings.

---

## Secrets

Sensitive information:

- Passwords.
- API keys.
- Certificates.

Should be stored in secret managers rather than source code.

Examples:

- AWS Secrets Manager.
- Kubernetes Secrets.
- HashiCorp Vault.

---

# 11. Service Mesh (High Level)

A service mesh handles communication concerns between services.

Example responsibilities:

- Service-to-service authentication.
- mTLS encryption.
- Traffic routing.
- Retries.
- Load balancing.
- Observability.

Examples:

- Istio.
- Linkerd.

---

# 12. Failure Scenarios

## What happens if a service becomes slow?

Problems:

- Threads become blocked.
- Requests accumulate.
- Latency increases.

Solutions:

- Timeouts.
- Circuit breakers.
- Bulkheads.
- Backpressure.

---

## What happens if a dependency fails?

Possible strategies:

- Return cached data.
- Use default responses.
- Disable non-critical features.
- Retry with exponential backoff.

---

# 13. L6 Interview Discussion

## When should you choose microservices?

Use microservices when:

- Different components have different scaling needs.
- Multiple teams need independent ownership.
- Independent deployment provides significant value.
- The system has become too large for a monolith.

---

## When is a monolith better?

For:

- Small teams.
- Simple domains.
- Early-stage products.

A well-designed monolith is often simpler and cheaper to operate.

---

## How do you avoid a distributed monolith?

Avoid:

- Excessive synchronous service calls.
- Shared databases.
- Tight coupling between services.

Prefer:

- Clear ownership.
- Asynchronous communication where appropriate.
- Independent deployments.

---

## How would you design communication between services?

Use:

- REST/gRPC for low-latency request-response.
- Kafka or messaging systems for asynchronous workflows and events.

---

# Key Takeaways

1. Microservices provide independent deployment, scaling, and ownership.

2. They introduce distributed system challenges such as network failures and eventual consistency.

3. Service boundaries should follow business domains.

4. Each service should own its database.

5. Synchronous communication is simple but creates runtime coupling.

6. Asynchronous communication improves resilience but introduces eventual consistency.

7. Distributed transactions are typically handled using Saga patterns.

8. Microservices require strong observability, resilience, and automation.

9. Microservices are not always better; choose them only when the benefits outweigh the operational cost.

# Chapter 16 Cloud Native Basics

---

# 1. What Does Cloud Native Mean?

Cloud native applications are designed to take advantage of cloud infrastructure.

Characteristics:

- Containerized applications.
- Automated deployments.
- Horizontal scaling.
- Fault tolerance.
- Infrastructure automation.
- Observability.

Traditional applications were often deployed on fixed servers.

Example:

```
Application
     |
Physical Server
```

Scaling often required purchasing and provisioning new hardware.

---

Cloud-native systems use dynamic infrastructure.

Example:

```
Application
     |
Containers
     |
Container Orchestration Platform
     |
Cloud Infrastructure
```

---

# 2. Containers

A container is a lightweight package containing:

- Application code.
- Runtime.
- Libraries.
- Dependencies.
- Configuration.

The goal:

```
Works on my machine
          |
          v
Runs the same in every environment
```

---

# 3. Containers vs Virtual Machines

## Virtual Machine

```
Physical Machine
        |
 Hypervisor
        |
----------------
|      |       |
VM1    VM2    VM3
|      |       |
Guest OS
Application
```

Each VM includes a complete operating system.

---

Advantages:

- Strong isolation.

---

Disadvantages:

- Larger memory footprint.
- Slower startup time.

---

## Containers

```
Physical Machine
       |
Operating System
       |
Container Runtime
       |
----------------
|      |       |
C1     C2      C3
```

Each container shares the host operating system kernel.

---

Advantages:

- Lightweight.
- Fast startup.
- Higher density.

---

Disadvantages:

- Weaker isolation compared to VMs.

---

# 4. Docker

Docker is a popular container platform.

A Docker image is an immutable blueprint.

Example:

```
Java 17 Runtime
        +
Application JAR
        +
Dependencies
        |
 Docker Image
```

---

A container is a running instance of an image.

Example:

```
Docker Image
       |
    Run
       |
Docker Container
```

---

Benefits:

- Consistent environments.
- Easier deployment.
- Simplified dependency management.

---

# 5. Container Registry

Images are stored in a registry.

Example:

```
Developer
    |
Build Docker Image
    |
Container Registry
    |
Kubernetes pulls image
```

Examples:

- Docker Hub.
- Amazon ECR.
- Google Artifact Registry.

---

# 6. Kubernetes Overview

Kubernetes is a container orchestration platform.

It manages:

- Deployment.
- Scaling.
- Networking.
- Service discovery.
- Self-healing.

---

# 7. Kubernetes Pods

The smallest deployable unit in Kubernetes is a Pod.

Example:

```
Pod
 |
------------------
|                |
Application     Sidecar
Container       Container
```

Usually a pod contains one application container.

---

# 8. Deployments

A Deployment manages a desired number of pods.

Example:

```
Desired state:

Order Service:
5 Pods
```

Kubernetes continuously ensures this state.

---

If a pod crashes:

```
Pod Failure
      |
Kubernetes creates a replacement
```

This provides self-healing.

---

# 9. Kubernetes Services

Pods are temporary.

Example:

```
Pod A: 10.1.1.5

Pod B: 10.1.1.8
```

IPs can change.

A Kubernetes Service provides a stable endpoint.

Example:

```
Order Service
       |
-----------------
|               |
Pod A         Pod B
```

---

Responsibilities:

- Stable DNS name.
- Load balancing.
- Service discovery.

---

# 10. Horizontal Pod Autoscaling (HPA)

Kubernetes can automatically adjust the number of pods.

Example:

Normal traffic:

```
Order Service
3 Pods
```

---

Traffic spike:

```
CPU increases

Kubernetes

3 Pods → 10 Pods
```

---

Benefits:

- Handles variable load.
- Optimizes infrastructure cost.

---

# 11. Configuration Management

Applications should not hardcode environment-specific values.

Examples:

- Database URLs.
- Feature flags.
- External service endpoints.

---

Kubernetes provides:

## ConfigMaps

For non-sensitive configuration.

Examples:

```
Database host

Application settings
```

---

## Secrets

For sensitive data.

Examples:

- API keys.
- Passwords.
- Certificates.

---

# 12. CI/CD Pipeline

Continuous Integration (CI):

Automatically validates code changes.

Typical steps:

```
Code Commit
      |
Build
      |
Unit Tests
      |
Static Analysis
      |
Artifact Creation
```

---

Continuous Deployment (CD):

Automatically moves software to environments.

Example:

```
Artifact
    |
Deploy to Staging
    |
Integration Tests
    |
Production Deployment
```

---

# 13. Deployment Strategies

## Rolling Deployment

Replace instances gradually.

Example:

```
Version 1

Pod 1
Pod 2
Pod 3


Replace one by one


Version 2

Pod 1
Pod 2
Pod 3
```

---

Advantages:

- Minimal downtime.

---

## Blue-Green Deployment

Maintain two environments.

Example:

```
Blue:
Current Production


Green:
New Version
```

Switch traffic when ready.

---

Advantages:

- Easy rollback.

---

Disadvantages:

- Requires more infrastructure.

---

## Canary Deployment

Release to a small percentage of users.

Example:

```
5% traffic → New Version

95% traffic → Existing Version
```

Monitor metrics before increasing rollout.

---

# 14. Infrastructure as Code (IaC)

Infrastructure should be defined using code.

Examples:

- Servers.
- Networks.
- Databases.
- Kubernetes clusters.

Tools:

- Terraform.
- CloudFormation.

---

Benefits:

- Version control.
- Repeatability.
- Reduced manual errors.

---

# 15. Cloud Native Observability

Cloud systems require monitoring.

Monitor:

- Pod CPU.
- Memory usage.
- Restarts.
- Request latency.
- Error rates.

Common tools:

- Prometheus.
- Grafana.
- ELK stack.

---

# 16. L6 Interview Discussion

## Why use containers?

Containers provide:

- Consistent environments.
- Faster deployments.
- Better resource utilization.

---

## Why Kubernetes instead of manually managing servers?

Because Kubernetes provides:

- Automated scaling.
- Self-healing.
- Service discovery.
- Deployment management.

---

## What happens when a pod crashes?

Kubernetes detects the failure and creates a replacement pod.

---

## How do you deploy a new version without downtime?

Strategies:

- Rolling deployments.
- Blue-green deployments.
- Canary deployments.

---

## How does Kubernetes help microservices?

It provides:

- Service discovery.
- Load balancing.
- Autoscaling.
- Configuration management.
- Failure recovery.

---

# Key Takeaways

1. Containers package applications with dependencies.

2. Docker images are immutable blueprints; containers are running instances.

3. Kubernetes orchestrates container deployment, scaling, and recovery.

4. Pods are the smallest deployable unit in Kubernetes.

5. Services provide stable networking and load balancing.

6. Autoscaling adjusts capacity based on demand.

7. CI/CD enables reliable automated deployments.

8. Infrastructure as Code makes environments repeatable.



# Chapter 17 - AI & LLM System Architecture

## What Is An LLM?

An LLM (Large Language Model) is a transformer-based deep learning model trained on massive text corpora to predict the next token in a sequence.

Through large-scale training, it learns language structure and can perform tasks such as:

* Summarization
* Classification
* Information Extraction
* Question Answering
* Reasoning

### Interview Answer

An LLM is a transformer-based deep learning model trained on large text corpora to predict the next token in a sequence. In production systems, I treat an LLM as an external probabilistic service rather than a deterministic system component.

---

## Deterministic vs Probabilistic Systems

Traditional Software:

Input
↓
Logic
↓
Output

Same Input
→ Same Output

Deterministic

LLM Systems:

Prompt
↓
Model
↓
Response

Same Prompt
→ Different Outputs Possible

Probabilistic

### Interview Sound Bite

"I treat LLMs as external services that generate probabilistic outputs."

---

## AI Native vs AI Augmented Systems

### AI Native

AI is the core engine of the product.

Examples:

* ChatGPT
* Grammarly
* AI Coding Assistants

Characteristics:

* AI is in the critical path
* Product depends on models
* Architecture revolves around prompts, embeddings, vector search, and retrieval pipelines

Removing AI breaks the product.

---

### AI Augmented

AI is an enhancement layer.

Examples:

* Banking app with chatbot
* Compliance platform with AI summaries
* Fraud system with AI explanations

Removing AI does not break the core product.

### Interview Sound Bite

"An AI-native application is designed around models from the ground up, whereas AI augmentation adds intelligence to an existing deterministic system."

Our screening platform is AI-augmented, not AI-native.

---

## LLM Limitations

* Hallucinations
* Inconsistency
* Latency
* Cost
* Prompt Sensitivity

### Interview Sound Bite

"I design assuming AI can be wrong and add validation and fallback mechanisms."

---

# Chapter 15 - AI Architecture Patterns

## Standard AI Integration Architecture

Client
↓
Backend
↓
Prompt Builder
↓
LLM API
↓
Validation Layer
↓
Response + Fallback

### Components

* Prompt Builder
* LLM API
* Validation Layer
* Logging
* Monitoring
* Caching
* Fallback Logic

### Interview Sound Bite

"I treat AI as an external service that enriches the system rather than being in the critical path."

---

## Pattern 1 - AI As Enrichment Layer

Examples:

* Transaction Categorization
* Adverse Media Summaries
* Risk Summaries
* Merchant Normalization

### Sound Bite

"AI should enrich systems rather than own correctness-sensitive decisions."

---

## Pattern 2 - AI Plus Rules

Rules:

* Deterministic
* Explainable
* Auditable

AI:

* Handles ambiguity
* Handles fuzzy matching
* Handles unstructured information

### Sound Bite

"Rules provide explainability. AI handles ambiguity."

---

## Pattern 3 - Human In The Loop

AI Output
↓
Confidence Check
↓
Human Review
↓
Decision

Used In:

* AML
* Fraud
* Compliance
* Healthcare
* Legal

---

## Pattern 4 - AI Plus Validation

Input
↓
LLM
↓
Validation Layer
↓
Approved Output

Validation Techniques:

* Schema Validation
* Structured Outputs
* Confidence Thresholds
* Business Rule Verification
* Human Review

---

# Chapter 16 - Retrieval Augmented Generation (RAG)

## What Is RAG?

RAG = Retrieval Augmented Generation

RAG combines:

Retrieval
+
Generation

Retrieval:
Find relevant data

Generation:
Generate an answer using that data

RAG = LLM + External Knowledge Retrieval

---

## Why RAG Exists

Without RAG:

User Query
↓
Prompt
↓
LLM
↓
Answer

Problems:

* Hallucinations
* Outdated Knowledge
* No Enterprise Data
* No Customer Context

---

With RAG:

User Query
↓
Embedding
↓
Vector Search
↓
Retrieved Context
↓
Prompt Construction
↓
LLM
↓
Grounded Answer

### Interview Sound Bite

"Without RAG, the model relies entirely on training data. With RAG, we retrieve relevant context at query time and ground the response."

---

## Why RAG Is Powerful

* Reduces Hallucinations
* Uses Up-To-Date Data
* Supports Enterprise Knowledge
* Supports Private Data
* Avoids Frequent Retraining

---

## RAG Pipeline

Raw Documents
↓
Chunking
↓
Embedding Model
↓
Vector Creation
↓
Vector Database
↓
Retrieval
↓
Prompt Construction
↓
LLM
↓
Answer

---

## Embeddings

Embeddings convert text into vectors.

Purpose:

* Semantic Search
* Similarity Matching
* Retrieval

Example:

"wire transfer"

and

"bank transfer"

have similar embeddings despite different wording.

---

## RAG In Compliance Systems

Data Sources:

* Transactions
* Alerts
* Adverse Media
* Watchlists

Without RAG:

The model relies on generic knowledge.

With RAG:

The model receives actual enterprise data.

### Interview Sound Bite

"In compliance systems, decisions must be based on actual data rather than model memory."

---

## RAG vs Fine Tuning

### Use RAG When

* Knowledge changes frequently
* Enterprise documents
* Customer-specific information
* Compliance data

### Use Fine Tuning When

* Behavior changes
* Consistent output style
* Domain-specific reasoning
* Structured output optimization

### Interview Sound Bite

"RAG is for knowledge. Fine-tuning is for behavior."

---

# Chapter 17 - Prompt Engineering

## What Prompt Engineering Really Means

Prompt engineering is the process of designing structured inputs to guide LLM behavior.

This includes:

* Roles
* Context
* Instructions
* Constraints
* Output Formats
* Examples

### Prompt Lifecycle

Design
↓
Test
↓
Evaluate
↓
Refine
↓
Repeat

### Interview Sound Bites

"Prompt engineering is programming in natural language."

"Prompts are the control plane for LLM behavior."

"We don't just ask questions. We define contracts."

---

## Production Prompt Structure

Role
+
Context
+
Instructions
+
Constraints
+
Output Format

---

## Prompting Techniques

### Role Prompting

You are a financial compliance analyst.

---

### Few Shot Prompting

Input A → Output A

Input B → Output B

---

### Context Injection

Retrieved enterprise data inserted into prompt.

---

### Structured Outputs

{
"risk_level":"HIGH",
"confidence":0.92
}

---

## Production AML Prompt Example

SYSTEM:

You are a financial compliance analyst specializing in AML and sanctions screening.

Analyze only the supplied data.

Do not make assumptions.

Return UNKNOWN if information is insufficient.

OUTPUT:

{
"risk_level":"",
"reason":"",
"key_factors":[],
"confidence":0.0
}

### Benefits

* Reduced Hallucination
* Machine Readable Output
* Easier Validation
* Production Ready Integration

### Interview Sound Bite

"We treat prompts as contracts rather than free-form text."

---

# Chapter 18 - AI Reliability

## Hallucination Prevention

Techniques:

* RAG
* Grounding
* Validation Layers
* Structured Outputs
* Confidence Thresholds
* Human Review
* Fallback Responses

### Interview Answer

To reduce hallucinations, I ground the model using retrieved context, enforce structured outputs, validate outputs against source data, add fallback mechanisms, and route high-risk decisions to human review.

---

## AI Evaluation Framework

Metrics:

* Accuracy
* Precision
* Recall
* Latency (P95/P99)
* Cost Per Request
* Human Feedback

### Interview Sound Bite

"AI systems require evaluation pipelines, not just prompts."

---

# Chapter 20 - AI In Compliance, AML And Screening

## AI Use Cases

### Adverse Media Summarization

Input:
News Articles

Output:
Risk Summary

---

### Entity Matching

Improve fuzzy matching between:

* Client Names
* Watchlists
* Adverse Media Entities

---

### Risk Summaries

Generate explainable summaries for analysts.

---

### Alert Prioritization

Rank alerts by risk.

---

### Case Investigation Assistants

Provide conversational access to:

* Alerts
* Transactions
* Media
* Historical Cases

### Interview Sound Bite

"AI assists analysts. It does not replace regulatory controls."

---

# Chapter 21 - AI Assisted SQL Modernization Project

## Problem

Legacy screening population logic existed inside SQL procedures and database objects.

Challenges:

* Years of accumulated logic
* Poor documentation
* Complex joins
* Inclusion / Exclusion rules
* 401k retirement exclusions
* Role-based eligibility rules

---

## Architecture

Legacy SQL
↓
LLM Semantic Extraction Layer
↓
Structured Knowledge Layer
↓
AI Agent
↓
User Questions

---

## Semantic Extraction Layer

Using an LLM, we extracted:

* Table Lineage
* Relationship Mapping
* Join Dependencies
* Inclusion Rules
* Exclusion Rules
* Business Semantics

Key Concepts:

* Semantic Extraction
* Code Interpretation
* Knowledge Abstraction Layer

---

## Agent Layer

Users could ask:

* Why are 401k accounts excluded?
* Which roles participate in screening?
* How is eligibility determined?

The agent answered using extracted knowledge rather than requiring SQL expertise.

---

## Why Not Traditional Parsing?

Traditional parsers provide syntax.

The LLM extracted business intent and semantic meaning.

### Interview Answer

"Parsing gives syntax structure, but it doesn't capture business semantics. The LLM helped interpret intent and business rules."

---

## Hallucination Controls

* Structured Outputs
* Validation Against SQL
* Restricted Scope
* Agent Operates On Extracted Knowledge
* Human Review For Critical Cases

---

## Scaling Strategy

Batch SQL Analysis
↓
Structured Knowledge Cache
↓
Independent Agent Layer

The extraction layer runs offline.

The reasoning layer scales independently.

---

## 90 Second Interview Story

I worked on an AI-assisted modernization initiative for a legacy screening system where business logic was embedded in complex SQL stored procedures and database objects.

The logic had evolved over time, was poorly documented, and difficult for engineers and business users to interpret.

To solve this, we introduced an AI-assisted analysis layer using an LLM to extract structured representations of:

* Table relationships
* Join dependencies
* Business rules
* Inclusion and exclusion logic

This created a knowledge abstraction layer over the legacy system.

On top of that, we built an AI agent that allowed users to query this logic conversationally.

Architecturally, the solution used a two-layer design:

1. Semantic extraction layer
2. Agent reasoning layer

We also implemented guardrails and validation against source SQL to reduce hallucination risk.

The result was significantly improved transparency and reduced dependency on SQL experts for understanding complex screening rules.

---

## Strong Interview Phrases

* Semantic Extraction
* Knowledge Abstraction Layer
* RAG-Style Pattern
* Separation Of Model Responsibilities
* Guardrails To Reduce Hallucination
* AI Assisted Modernization
* Deterministic Validation
* Layered AI Architecture
* Conversational Access To Business Logic
* AI As An Enrichment Layer

```
```

## PART V - INTERVIEW SOUND BITES

### Scalability

"Scale reads and writes independently."

"Partition by access pattern."

"External dependencies are usually the limiting factor."

---

### Reliability

"Failures are inevitable. Recovery is designed."

"DLQs isolate bad data while keeping the pipeline healthy."

"We design for retries rather than trying to prevent them."

---

### Data Engineering

"Kafka is for propagation, not querying."

"Databases are optimized for querying and correctness."

"Event streams are often the source of truth."

---

### AI

"AI should not be in the critical path for correctness-sensitive decisions."

"RAG is for knowledge. Fine tuning is for behavior."

"Validation is more important than generation."

"AI systems require evaluation pipelines, not just prompts."

"Structured outputs make AI production-ready."

# Chapter 18 Microservices & Spring Boot Architecture
# Microservices Fundamentals & Architecture

---

# 1. What are Microservices?

Microservices are an architectural style where a software system is composed of multiple small, autonomous services that communicate over a network using well-defined APIs.

Each microservice:

- Runs as an independent process.
- Owns a specific business capability.
- Can be developed, deployed, and scaled independently.
- Communicates with other services through network calls.

Example:

```
                  E-Commerce Platform

      Product Service      Order Service      Payment Service
              |                  |                  |
              ----------------------------------------
                                 |
                            API Contracts
                                 |
                              Network
```

Microservices may run as separate processes, containers, or cloud-native workloads managed by platforms like Kubernetes.

---

# 2. Why Microservices?

Microservices allow organizations to:

- Deliver features faster.
- Scale individual parts of the system independently.
- Enable independent teams to work autonomously.
- Improve resilience by isolating failures.
- Adopt new technologies more safely.

The goal is not to make services as small as possible.

A very large service creates a mini-monolith.

Too many tiny services create unnecessary operational complexity.

The objective is to find the right balance where services remain independent, cohesive, and manageable.

---

# 3. Monolith vs Microservices

## Monolithic Architecture

A monolith is a single deployable application where all modules run inside the same process.

Example:

```
                 Monolithic Application

          ---------------------------------
          | User | Order | Payment | Search |
          ---------------------------------
                         |
                      Database
```

### Advantages

- Simple development and deployment.
- Easier local testing.
- No network latency between modules.
- Simpler transactions.

### Challenges

- Large codebases become difficult to maintain.
- A change requires redeploying the entire application.
- Scaling requires replicating the entire application.
- Technology choices are constrained.

Example:

A checkout component receives 100x more traffic during a sale.

In a monolith:

```
Scale the entire application
```

even though only checkout requires more capacity.

---

## Microservice Architecture

Example:

```
            API Gateway
                 |
     --------------------------------
     |              |               |
 Product Service  Order Service  Payment Service
     |              |               |
 Product DB      Order DB       Payment DB
```

Each service can:

- Scale independently.
- Have independent release cycles.
- Choose different technologies.
- Fail independently.

Example:

If the payment service fails:

```
User can still:
- Browse products.
- Search items.
- Add products to cart.
```

instead of the entire website failing.

---

# 4. Defining Service Boundaries

One of the hardest problems in microservice design is deciding where service boundaries should exist.

This is not a one-time decision.

Service boundaries evolve as the business evolves.

A good boundary is based on a business capability.

Examples:

```
User Service
- Registration
- Authentication
- Profile Management


Order Service
- Order creation
- Order tracking
- Order history


Inventory Service
- Stock availability
- Inventory updates
```

---

# 5. Single Responsibility Principle (SRP) Applied to Services

A service should have one reason to change.

The core idea:

> Gather together things that change for the same reason. Separate those things that change for different reasons.

Example:

## Good Boundary

Order Service:

- Validate orders.
- Apply discounts.
- Calculate totals.

Why?

Pricing rules and order processing logic often change together.

---

## Bad Boundary

Combining:

```
Authentication
+
Order Processing
```

in the same service.

Reason:

Authentication changes because of security requirements.

Orders change because of business workflows.

They have different reasons to evolve.

---

# 6. Independent Deployment and Loose Coupling

Microservices should be able to change and deploy independently without forcing consumers to change.

Services expose contracts through APIs.

Example:

```
Client Service
       |
       |
  REST API
       |
Order Service
```

The consumer should depend on the contract, not internal implementation details.

---

## Avoid Excessive Sharing

Sharing internal models across services creates tight coupling.

Bad:

```
Shared Java library containing OrderEntity
         |
 Product Service
         |
 Payment Service
```

Now every change requires coordination.

---

Better:

```
Order Service
      |
   API DTO
      |
 Consumers
```

Each service controls its own internal model.

---

# 7. Database Ownership

A common microservice principle:

```
Database per Service
```

Example:

```
Order Service
      |
 Order Database


Inventory Service
      |
 Inventory Database
```

Advantages:

- Independent schema changes.
- Independent scaling.
- Better service ownership.

---

## Why Shared Databases are Dangerous

Bad:

```
Order Service
        |
        |
 Shared Database
        |
        |
Inventory Service
```

Problems:

- Tight coupling.
- Cross-service dependencies.
- Difficult schema evolution.
- Coordinated deployments.

---

# 8. Technology Heterogeneity

Microservices allow teams to choose the best technology for each problem.

Example:

A social media platform may use:

```
User Relationships
       |
 Graph Database


User Posts
       |
 Document Database


Payments
       |
 Relational Database
```

Different workloads have different requirements.

---

## Advantages

- Use the best database for each use case.
- Adopt new programming languages gradually.
- Experiment with new frameworks with lower risk.

Example:

A team can rewrite a small service in a new language in weeks, rather than rewriting an entire monolith.

---

# 9. Independent Scaling

In a monolith:

```
High Traffic in Checkout
           |
 Scale Entire Application
```

This wastes resources.

---

With microservices:

```
Checkout Service
       |
 Add more instances


Profile Service
       |
 Keep existing capacity
```

This allows better resource utilization.

---

## Cloud-Native Scaling

Container orchestration platforms such as Kubernetes enable:

- Automatic scaling based on traffic.
- Dynamic provisioning of instances.
- Health monitoring.
- Automatic replacement of failed instances.

---

# 10. Configuration Management Challenges

A microservice ecosystem may have:

- Many services.
- Multiple instances of each service.
- Multiple environments (dev, QA, prod).

Managing configuration manually becomes difficult.

---

## Centralized Configuration

Solutions:

- Spring Cloud Config.
- Consul.
- etcd.

Example:

```
             Git Repository
                   |
          Spring Cloud Config
                   |
       ---------------------------
       |             |            |
   Product       Order       Inventory
   Service       Service     Service
```

Benefits:

- Centralized configuration.
- Environment-specific properties.
- Easier operational management.

---

# 11. Visibility, Monitoring, and Logging

As the number of services increases, debugging becomes more difficult.

A single user request may flow through many services.

Example:

```
Client
  |
API Gateway
  |
Order Service
  |
Inventory Service
  |
Database
```

Requirements:

## Centralized Logging

Aggregate logs from all services.

Examples:

- ELK stack.
- Splunk.

---

## Metrics and Monitoring

Monitor:

- Request latency.
- Error rates.
- CPU and memory.
- Service health.

Tools:

- Prometheus.
- Grafana.

---

# 12. Fault Tolerance and Resilience

Distributed systems fail.

A service may fail because of:

- Network issues.
- Timeouts.
- Dependency outages.
- Resource exhaustion.

Failures should be isolated.

Example:

```
Order Service
       |
       X
Inventory Service Down
```

Order Service should degrade gracefully instead of causing a complete system outage.

---

## Common Resilience Techniques

### Timeouts

Avoid waiting indefinitely for slow services.

---

### Retries

Retry transient failures.

Use exponential backoff with jitter to avoid retry storms.

---

### Circuit Breakers

Stop sending requests to an unhealthy dependency.

States:

```
Closed
  |
Failure threshold reached
  |
Open
  |
After timeout
  |
Half Open
```

---

### Fallbacks

Return:

- Cached data.
- Default responses.
- Partial functionality.

---

# 13. Challenges of Microservices

Microservices are not free.

Benefits come with additional complexity.

Challenges include:

- Network latency.
- Partial failures.
- Distributed debugging.
- Data consistency challenges.
- Deployment complexity.
- Monitoring many moving parts.
- Increased operational overhead.

---

# 14. L6 Interview Discussion

## How do you decide service boundaries?

I align services around business capabilities and the principle:

"Things that change together should stay together. Things that change for different reasons should be separated."

---

## Why not make services extremely small?

Very small services increase:

- Network calls.
- Operational complexity.
- Deployment overhead.

The goal is cohesive, independently deployable services.

---

## Why does a microservice own its database?

Because shared databases create tight coupling and prevent independent evolution.

---

## What is the biggest downside of microservices?

Operational complexity.

You trade simple in-process communication for:

- Network calls.
- Failure handling.
- Monitoring.
- Distributed data management.

---

# Key Takeaways

1. Microservices organize systems around business capabilities.

2. Service boundaries are more important than the number of services.

3. Services should be independently deployable.

4. Avoid shared databases and shared internal models.

5. Microservices allow independent scaling and technology choices.

6. Distributed systems require resilience, observability, and automation.

7. Microservices provide flexibility but introduce operational complexity.

# L6 Interview Soundbites

## When would you choose a monolith over microservices?

"Microservices provide independent scaling, deployment, and team autonomy, but they introduce distributed system complexity such as network failures, distributed tracing, data consistency challenges, and operational overhead. For a small team or a simple product, a well-designed modular monolith is often the better choice."

---

## How do you decide microservice boundaries?

"I do not split services based on technical layers. I align service boundaries around business capabilities and the principle that things which change together should stay together, while things that evolve independently should be separated."

---

## Why avoid a shared database?

"Sharing a database creates tight coupling between services because schema changes require coordination between teams. Database-per-service preserves ownership, allows independent evolution, and enables each service to choose storage technology that best fits its workload."

# Microservices & Spring Boot - Part 2
# Web Services, SOAP, REST & HTTP Fundamentals

---

# 1. What is a Web Service?

A web service is a software component that allows applications running on different platforms and technologies to communicate over a network using a standardized interface.

Example:

```
Java Application
       |
       | HTTP / Messaging
       |
Python Application
       |
       | HTTP / Messaging
       |
.NET Application
```

The goal is interoperability.

A Java service should be able to communicate with a Python service or a .NET service without needing to know their internal implementation.

---

# 2. Characteristics of a Good Web Service

A good web service should provide:

- A well-defined contract.
- Technology independence.
- Platform independence.
- Loose coupling.
- Standard communication protocols.
- Security.
- Reliability.

---

# 3. Service Contract

A service contract defines:

- What operations are available.
- What inputs are required.
- What outputs are returned.
- What errors may occur.
- How consumers interact with the service.

---

Examples:

REST:

```
OpenAPI / Swagger specification
```

SOAP:

```
WSDL (Web Services Description Language)
```

A good contract allows client applications to integrate without knowing the service internals.

---

# 4. Communication Protocols

Web services communicate using standard protocols.

Examples:

### HTTP/HTTPS

Most common for modern APIs.

Example:

```
Client
  |
HTTP Request
  |
Service
  |
HTTP Response
  |
Client
```

---

### Message-Based Communication

Examples:

- Kafka
- JMS
- RabbitMQ

Useful for asynchronous communication.

Example:

```
Producer
   |
 Message Broker
   |
 Consumer
```

Benefits:

- Decoupling.
- Buffering spikes.
- Independent scaling.
- Event-driven architectures.

---

# 5. SOAP Architecture

SOAP (Simple Object Access Protocol) is a protocol for exchanging structured messages between applications.

SOAP messages are typically XML based.

---

## SOAP Message Structure

```
SOAP Envelope
     |
     |--- Header
     |
     |--- Body
```

---

### Envelope

The outer container defining the SOAP message.

---

### Header

Contains metadata.

Examples:

- Authentication information.
- Security tokens.
- Transaction information.
- Routing details.

---

### Body

Contains the actual request or response payload.

Example:

```
<SOAP-Body>
    <GetCustomerRequest>
        <customerId>123</customerId>
    </GetCustomerRequest>
</SOAP-Body>
```

---

# 6. SOAP Characteristics

## Advantages

### Strong Contract

WSDL defines exactly:

- Available operations.
- Request schema.
- Response schema.

---

### Built-in Standards

SOAP supports standards such as:

- WS-Security
- Reliable messaging
- Transactions

---

### Language Independent

A Java service can communicate with a .NET service using the same contract.

---

## Disadvantages

- XML payloads are large.
- More bandwidth consumption.
- More parsing overhead.
- More complex development.

---

# 7. REST Architecture

REST (Representational State Transfer) is an architectural style rather than a strict protocol.

REST models the system around resources.

Examples:

```
GET /customers/123

POST /customers

DELETE /customers/123
```

---

The HTTP method represents the action:

```
GET    -> Retrieve
POST   -> Create
PUT    -> Replace
PATCH  -> Partial update
DELETE -> Remove
```

---

# 8. REST Principles

## Stateless Communication

Every request contains all information required to process it.

Example:

```
GET /accounts/123

Authorization:
Bearer JWT_TOKEN
```

The server does not store client session state.

---

## Resource-Oriented Design

Good:

```
/customers/123/orders
```

Bad:

```
/getCustomerOrders?id=123
```

---

## Representation

A resource can have different representations.

Examples:

- JSON
- XML
- Protocol Buffers

Modern REST APIs commonly use JSON.

---

# 9. SOAP vs REST

| Feature | SOAP | REST |
|---|---|---|
| Type | Protocol | Architectural style |
| Data Format | Usually XML | JSON, XML, etc. |
| Contract | WSDL | OpenAPI |
| Complexity | Higher | Lower |
| Payload Size | Larger | Smaller |
| Performance | Slower | Faster |
| State | Can be stateful | Usually stateless |
| Flexibility | Lower | Higher |

---

# 10. When to Use SOAP

SOAP is often used in:

- Banking.
- Insurance.
- Government systems.
- Legacy enterprise integrations.

Reasons:

- Strict contracts.
- Strong security standards.
- Reliable messaging.
- Transaction support.

---

# 11. When to Use REST

REST is preferred for:

- Microservices.
- Mobile applications.
- Web applications.
- Public APIs.
- Cloud-native systems.

Reasons:

- Lightweight.
- Easier development.
- Better scalability.
- Better browser support.

---

# 12. Synchronous vs Asynchronous Communication

## Synchronous Communication

The caller waits for a response.

Example:

```
Order Service
       |
       | HTTP
       |
Inventory Service
       |
       |
Response
```

---

### Advantages

- Simple.
- Immediate response.
- Easier debugging.

---

### Disadvantages

- Higher coupling.
- Increased latency.
- Dependency failures propagate.

---

# Example Problem

Order Service calls:

```
Inventory Service
       |
       |
Slow response (10 seconds)
```

Order Service threads become blocked.

This may cause:

- Thread pool exhaustion.
- Increased latency.
- Cascading failures.

---

# 13. Asynchronous Communication

The caller sends an event and continues processing.

Example:

```
Order Created Event
        |
      Kafka
        |
-----------------
|               |
Inventory     Notification
Service       Service
```

---

## Advantages

- Loose coupling.
- Better scalability.
- Failure isolation.
- Independent consumer scaling.
- Replay capability.

---

## Disadvantages

- Eventual consistency.
- More operational complexity.
- Harder debugging.

---

# 14. Choosing Communication Style

Use synchronous communication when:

- Immediate response is required.
- Data consistency is needed at request time.

Examples:

- Authentication.
- Payment authorization.
- Inventory availability check.

---

Use asynchronous communication when:

- The operation does not affect the user response immediately.

Examples:

- Sending emails.
- Analytics.
- Notifications.
- Generating reports.

---

# 15. L6 Interview Soundbites

## Why is REST preferred for microservices?

"REST provides a lightweight, stateless, and simple communication model that scales well in distributed systems. It works naturally with HTTP infrastructure such as load balancers, gateways, and caching layers."

---

## When would you choose SOAP over REST?

"SOAP is valuable in enterprise environments where strict contracts, advanced security standards, reliable messaging, or transactional guarantees are required. REST is usually preferred for modern cloud-native services because of its simplicity and lower overhead."

---

## When should you use asynchronous communication?

"I use asynchronous communication when the operation does not need to be completed in the user-facing request path. Moving it to an event-driven pipeline reduces latency, improves resilience, and allows independent scaling of downstream consumers."

---

## Why can synchronous communication become dangerous at scale?

"Each synchronous dependency increases latency and creates a failure path. A slow downstream service can exhaust threads, increase response times, and cause cascading failures. We mitigate this using timeouts, circuit breakers, retries, and sometimes asynchronous patterns."

---

# Key Takeaways

1. Web services enable communication between independent systems.

2. Service contracts define how consumers interact with services.

3. SOAP provides strong contracts and enterprise features but has higher complexity.

4. REST is lightweight and widely used in modern microservices.

5. Synchronous calls are simple but increase coupling.

6. Asynchronous messaging improves scalability and resilience but introduces eventual consistency.

7. The communication model should be chosen based on business requirements and trade-offs.

# Microservices & Spring Boot - Part 3
# Spring Framework Core: IoC, Dependency Injection, Beans & AOP

---

# 1. What Problem Did Spring Solve?

Before Spring, enterprise Java applications had several challenges:

- Objects manually created their own dependencies.
- Tight coupling between components.
- Difficult unit testing.
- Complex XML-based configuration.
- Boilerplate infrastructure code.

Example without dependency injection:

```java
public class OrderService {

    private PaymentService paymentService =
            new PaymentService();

    public void placeOrder() {
        paymentService.processPayment();
    }
}
```

Problems:

- OrderService is tightly coupled to PaymentService.
- Difficult to replace PaymentService with another implementation.
- Hard to mock during testing.

---

# 2. Inversion of Control (IoC)

## Traditional Approach

The application controls object creation.

```
Application
     |
new PaymentService()
```

The object manages its own dependencies.

---

## IoC Approach

Control of object creation is delegated to the Spring container.

```
Application
      |
Spring Container
      |
Creates Objects
      |
Injects Dependencies
```

This inversion of responsibility is called **Inversion of Control (IoC)**.

---

# 3. Dependency Injection (DI)

Dependency Injection is the primary mechanism Spring uses to implement IoC.

Instead of a class creating its dependencies:

Bad:

```java
class OrderService {

 private PaymentService paymentService =
       new PaymentService();

}
```

Spring injects the dependency:

Good:

```java
@Service
public class OrderService {

 private final PaymentService paymentService;

 public OrderService(PaymentService paymentService) {
    this.paymentService = paymentService;
 }

}
```

The Spring container creates the PaymentService object and injects it into OrderService.

---

# 4. Why Dependency Injection Is Important

Benefits:

## Loose Coupling

OrderService depends on an abstraction, not the concrete implementation.

Example:

```
PaymentService interface
           |
 ------------------------
 |                      |
CreditCardService   PaypalService
```

The implementation can change without modifying the consumer.

---

## Easier Testing

Example:

```
OrderService
      |
Mock PaymentService
```

Unit tests can replace real dependencies with mocks.

---

## Better Maintainability

Different implementations can be selected using:

- Profiles
- Conditional beans
- Configuration

---

# 5. Types of Dependency Injection

## Constructor Injection (Recommended)

Example:

```java
public class UserService {

 private final UserRepository repository;

 public UserService(UserRepository repository) {
      this.repository = repository;
 }
}
```

Advantages:

- Makes dependencies explicit.
- Allows immutable fields.
- Easier testing.
- Guarantees the object is created in a valid state.

---

## Setter Injection

Example:

```java
public class UserService {

 private UserRepository repository;

 public void setRepository(UserRepository repository) {
      this.repository = repository;
 }
}
```

Useful when a dependency is optional.

---

Disadvantages:

- Object may exist without required dependencies.
- Mutable state.

---

## Field Injection

Example:

```java
@Autowired
private UserRepository repository;
```

---

Why it is discouraged:

- Hidden dependencies.
- Difficult unit testing.
- Cannot create immutable objects.

---

# 6. Spring Container

The Spring container is responsible for:

- Creating beans.
- Managing their lifecycle.
- Resolving dependencies.
- Applying configuration.

The two main containers are:

## BeanFactory

Basic IoC container.

Features:

- Lightweight.
- Lazy initialization.

---

## ApplicationContext

The commonly used Spring container.

Adds:

- Event support.
- Internationalization.
- AOP integration.
- Automatic bean post-processing.

---

# 7. What Is a Bean?

A bean is an object whose lifecycle is managed by the Spring container.

Examples:

```java
@Service
class OrderService {}
```

```java
@Repository
class OrderRepository {}
```

```java
@Controller
class UserController {}
```

Spring discovers these classes and creates objects called beans.

---

# 8. How Does Spring Discover Beans?

## Component Scanning

Spring scans packages for annotations:

```
@Component
@Service
@Repository
@Controller
@RestController
```

Example:

```
com.company.app
        |
     Spring scans
        |
Creates beans
```

---

# 9. @Component vs @Service vs @Repository

Technically they all create beans.

The difference is semantic.

---

## @Component

Generic Spring-managed bean.

Example:

```
Utility classes
Helper components
```

---

## @Service

Represents business logic.

Example:

```
OrderService
PaymentService
```

---

## @Repository

Represents the persistence layer.

Additional benefit:

Spring translates database-specific exceptions into Spring's DataAccessException hierarchy.

---

## @Controller / @RestController

Handle HTTP requests.

---

# 10. Bean Lifecycle

A typical lifecycle:

```
1. Spring starts

2. Bean definition loaded

3. Bean instance created

4. Dependencies injected

5. BeanPostProcessor executes

6. Initialization methods execute

7. Bean ready for use

Application running...

8. Shutdown event

9. Destroy methods execute
```

---

## Initialization Methods

Examples:

```java
@PostConstruct
public void init() {
    // initialize resources
}
```

---

## Destruction Methods

Examples:

```java
@PreDestroy
public void cleanup() {
    // release resources
}
```

---

# 11. Bean Scopes

## Singleton (Default)

One instance per Spring container.

Example:

```
ApplicationContext
        |
    OrderService
        |
    Single Instance
```

Most services use this scope.

---

## Prototype

A new bean instance is created every time it is requested.

Example:

```
Request bean #1

Request bean #2
```

---

## Request Scope

One bean per HTTP request.

Useful for request-specific data.

---

## Session Scope

One bean per HTTP session.

Common in traditional web applications.

---

# 12. Aspect-Oriented Programming (AOP)

AOP separates cross-cutting concerns from business logic.

Examples of cross-cutting concerns:

- Logging
- Security
- Transactions
- Metrics
- Auditing

---

Without AOP:

```
OrderService

Start log

Validate permissions

Execute business logic

Write metrics
```

The business logic becomes cluttered.

---

With AOP:

```
Logging Aspect

Security Aspect

Transaction Aspect

      |
      V

OrderService
```

---

# 13. Core AOP Concepts

## Aspect

A class containing cross-cutting logic.

Example:

```
LoggingAspect
```

---

## Join Point

A point during execution where an aspect can run.

Example:

```
Method execution
```

---

## Pointcut

A rule defining where advice applies.

Example:

```
All methods inside service package.
```

---

## Advice

The action performed.

Types:

- Before
- After
- Around
- After returning
- After throwing

---

# 14. Dynamic Proxies in Spring AOP

Spring does not modify your classes directly.

It creates proxy objects.

Example:

```
Client
 |
Proxy
 |
OrderService
```

The proxy can execute extra behavior before or after the method call.

---

Example:

```
Transaction Proxy

Start Transaction

Call Service Method

Commit/Rollback
```

---

This is how features like `@Transactional` work internally.

---

# 15. L6 Interview Soundbites

## What is IoC?

"Instead of application code controlling object creation, the responsibility is delegated to the Spring container. The container creates objects, manages their lifecycle, and injects dependencies."

---

## Why is constructor injection preferred?

"Constructor injection makes dependencies explicit, allows immutable objects, and makes classes easier to test. It ensures a bean cannot be created without its required dependencies."

---

## Why is field injection discouraged?

"Field injection hides dependencies, makes unit testing harder, and prevents immutable design."

---

## Why do we need AOP?

"AOP allows us to separate cross-cutting concerns such as transactions, logging, security, and metrics from business logic, resulting in cleaner and more maintainable code."

---

## How does @Transactional work internally?

"Spring creates a proxy around the bean. The proxy starts a transaction before invoking the method, and then commits or rolls back the transaction based on the outcome."

---

# Key Takeaways

1. IoC means Spring controls object creation and lifecycle.

2. Dependency Injection promotes loose coupling and testability.

3. Constructor injection is the preferred approach.

4. Beans are objects managed by the Spring container.

5. Component scanning discovers beans automatically.

6. ApplicationContext is the primary Spring container.

7. Bean lifecycle includes creation, dependency injection, initialization, and destruction.

8. AOP handles cross-cutting concerns using proxies.

9. Spring features like transactions, security, and logging rely heavily on AOP.

# Microservices & Spring Boot - Part 4
# Spring Boot Internals & Production REST APIs

---

# 1. Why Was Spring Boot Created?

Traditional Spring applications required significant configuration.

Challenges included:

- Managing dependency versions.
- XML configuration.
- Manual servlet container setup.
- Boilerplate configuration.
- Additional effort to make applications production-ready.

Example:

Before Spring Boot:

```
Application
    |
Configure Spring Context
    |
Configure DispatcherServlet
    |
Configure Tomcat Server
    |
Manage dependency versions
```

This resulted in a lot of setup code before developers could focus on business logic.

---

# 2. Spring Framework vs Spring Boot

## Spring Framework

Provides the core capabilities:

- IoC Container.
- Dependency Injection.
- AOP.
- Transaction Management.
- Spring MVC.
- Data Access.
- Security.

Spring provides the foundation.

---

## Spring Boot

Spring Boot builds on top of Spring and provides:

- Auto-configuration.
- Opinionated defaults.
- Starter dependencies.
- Embedded servers.
- Externalized configuration.
- Production-ready features.

---

## Interview Soundbite

"Spring Framework provides the core programming model such as IoC, dependency injection, and AOP. Spring Boot reduces the amount of configuration required by providing auto-configuration, sensible defaults, and production-ready capabilities."

---

# 3. Spring Boot Starters

A starter is a curated set of dependencies for a specific functionality.

Examples:

## Web Applications

```
spring-boot-starter-web
```

Includes:

- Spring MVC.
- Embedded Tomcat.
- Jackson.
- Validation libraries.

---

## Database Applications

```
spring-boot-starter-data-jpa
```

Includes:

- Spring Data JPA.
- Hibernate.
- Transaction management.

---

## Security

```
spring-boot-starter-security
```

Includes:

- Spring Security.
- Authentication filters.
- Authorization mechanisms.

---

## Why Starters Matter

Without starters:

```
Developer manually chooses:

Jackson version
Tomcat version
Spring MVC version
Logging libraries
```

Potential issues:

- Dependency conflicts.
- Incompatible versions.

---

With starters:

Spring Boot manages compatible versions.

---

# 4. Auto-Configuration

## The Problem

Different applications require different infrastructure components.

Example:

A REST application needs:

- DispatcherServlet.
- Jackson ObjectMapper.
- HTTP message converters.
- Embedded web server.

A database application needs:

- DataSource.
- EntityManager.
- TransactionManager.

---

## How Auto-Configuration Works

At startup Spring Boot:

```
Read classpath
       |
Detect available libraries
       |
Evaluate conditions
       |
Create required beans
```

Example:

If:

```
spring-boot-starter-web
```

is present:

Spring Boot configures:

- DispatcherServlet.
- Jackson.
- Default error handling.
- Embedded Tomcat.

---

## Conditional Configuration

Auto-configuration uses conditions.

Examples:

```
@ConditionalOnClass

@ConditionalOnMissingBean

@ConditionalOnProperty
```

---

Example:

```
If no custom ObjectMapper exists:

Create default ObjectMapper.
```

---

## Interview Soundbite

"Spring Boot auto-configuration uses conditional bean creation. It examines the classpath and application configuration and automatically creates infrastructure beans when the required dependencies are present."

---

# 5. Embedded Server

Traditional applications:

```
Application
      |
WAR File
      |
External Tomcat
```

Deployment team installs and manages the server.

---

Spring Boot approach:

```
Application
      |
Embedded Tomcat
      |
Executable JAR
```

---

Advantages:

- Simple deployment.
- Same runtime in all environments.
- Easier containerization with Docker.
- Faster startup.

---

# 6. Spring Boot Application Startup Lifecycle

Example:

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

---

## Startup Steps

### 1. Bootstrap Application

Spring creates an ApplicationContext.

---

### 2. Component Scanning

Searches for:

```
@Component
@Service
@Repository
@Controller
@Configuration
```

---

### 3. Process Auto-Configuration

Spring Boot creates infrastructure beans.

Examples:

- DataSource.
- DispatcherServlet.
- ObjectMapper.
- Transaction Manager.

---

### 4. Create and Wire Beans

Dependencies are injected.

---

### 5. Execute Initialization

Examples:

```
@PostConstruct

CommandLineRunner
```

---

### 6. Application Ready

The application starts accepting requests.

---

# 7. Spring MVC Request Lifecycle

When an HTTP request arrives:

```
Client
  |
Embedded Tomcat
  |
DispatcherServlet
  |
Handler Mapping
  |
Controller
  |
Service
  |
Repository
  |
Database
```

---

The response follows the reverse path.

---

# 8. DispatcherServlet

The DispatcherServlet is the front controller of Spring MVC.

Responsibilities:

- Receives incoming requests.
- Determines the correct controller.
- Invokes controller methods.
- Coordinates request processing.
- Returns the response.

---

## Example

Request:

```
GET /customers/123
```

Spring maps it to:

```java
@GetMapping("/customers/{id}")
public CustomerDTO getCustomer() {
}
```

---

# 9. HTTP Message Conversion

Controllers work with Java objects.

Example:

```java
public CustomerDTO getCustomer()
```

But HTTP communicates using JSON.

---

Jackson converts:

```
Java Object
     |
 JSON
```

This is serialization.

---

Incoming requests:

```
JSON
 |
Java Object
```

This is deserialization.

---

# 10. DTO vs Entity

## Entity

Represents the database model.

Example:

```
CustomerEntity
```

Contains:

- Database mappings.
- Persistence annotations.

---

## DTO

Represents data exchanged over APIs.

Example:

```
CustomerResponseDTO
```

---

## Why Not Expose Entities?

Problems:

### Tight Coupling

Changing database schema can break API clients.

---

### Security Risks

Entity may contain fields such as:

```
passwordHash
internalFlags
```

---

### Over-fetching

Clients may receive unnecessary data.

---

## Best Practice

```
Controller
     |
 DTO
     |
Service
     |
Entity
     |
Repository
```

---

## Interview Soundbite

"Entities represent persistence concerns, while DTOs represent API contracts. Separating them prevents database changes from leaking into external APIs and allows independent evolution."

---

# 11. Validation

Never trust client input.

Example:

```java
public class UserRequest {

    @NotBlank
    private String name;

    @Email
    private String email;
}
```

Controller:

```java
@PostMapping("/users")
public void createUser(
        @Valid @RequestBody UserRequest request) {
}
```

Spring validates the request before executing business logic.

---

# 12. Exception Handling

Avoid:

```
try {
}
catch(Exception e) {
}
```

in every controller.

---

Use centralized exception handling.

Example:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

Benefits:

- Consistent error responses.
- Less duplicate code.
- Centralized logging.

---

# 13. Externalized Configuration & Profiles

Different environments require different configurations.

Examples:

- Database URLs.
- API endpoints.
- Feature flags.

---

Profiles:

```
application.properties

application-dev.properties

application-prod.properties
```

Example:

```
spring.profiles.active=prod
```

---

Benefits:

- Same application artifact.
- Different runtime configuration.

---

# 14. Spring Boot Actuator

Actuator provides production endpoints.

Examples:

Health:

```
/actuator/health
```

Metrics:

```
/actuator/metrics
```

Mappings:

```
/actuator/mappings
```

---

Used for:

- Kubernetes health probes.
- Monitoring.
- Troubleshooting.

---

# 15. Production Readiness Considerations

A production service requires more than working code.

Consider:

## Configuration

- Externalized properties.
- Secrets management.

---

## Observability

- Structured logging.
- Metrics.
- Distributed tracing.

---

## Security

- HTTPS.
- Authentication.
- Authorization.

---

## Reliability

- Timeouts.
- Retries.
- Circuit breakers.

---

## Performance

- Connection pooling.
- Thread pool tuning.
- Caching.

---

# 16. L6 Interview Soundbites

## What happens when a Spring Boot application starts?

"Spring Boot creates the ApplicationContext, scans for components, processes auto-configurations, creates and wires beans, executes initialization callbacks, and finally starts the embedded server to accept requests."

---

## What happens when a REST request reaches Spring Boot?

"The embedded Tomcat receives the HTTP request and passes it to DispatcherServlet. DispatcherServlet uses handler mappings to find the appropriate controller, executes the request through the service and repository layers, and uses message converters like Jackson to serialize the response."

---

## Why use DTOs instead of entities?

"Entities represent database models and may change due to persistence requirements. DTOs represent stable API contracts and prevent internal database details from leaking to external clients."

---

## Why is Spring Boot popular?

"Spring Boot reduces configuration complexity through opinionated defaults, starter dependencies, auto-configuration, and production-ready features, allowing developers to focus on business functionality."

---

# Key Takeaways

1. Spring Boot simplifies traditional Spring configuration.
2. Auto-configuration creates infrastructure beans based on classpath and conditions.
3. Embedded servers enable self-contained deployments.
4. DispatcherServlet is the front controller of Spring MVC.
5. Jackson handles JSON serialization and deserialization.
6. DTOs should be separated from entities.
7. Validation and centralized exception handling improve API quality.
8. Actuator provides operational visibility.
9. Production readiness requires observability, security, resilience, and performance considerations.

# Microservices & Spring Boot - Part 5
# Spring Data JPA, Hibernate, Transactions & Database Performance

---

# 1. What is JPA?

JPA (Java Persistence API) is a specification that defines how Java objects are mapped to relational databases.

It is not an implementation.

Popular implementations include:

- Hibernate
- EclipseLink

---

## Why Do We Need JPA?

Without an ORM:

```
Application
     |
 JDBC
     |
 SQL Queries
     |
 Database
```

Developers manually handle:

- SQL creation
- ResultSet mapping
- Connection management
- Object conversion

---

With JPA:

```
Application
     |
 Entity Object
     |
 JPA
     |
 Hibernate
     |
 JDBC
     |
 Database
```

The ORM converts Java objects into SQL operations.

---

# 2. What is Hibernate?

Hibernate is the most popular implementation of JPA.

Responsibilities:

- Object-relational mapping (ORM)
- SQL generation
- Entity lifecycle management
- Caching
- Dirty checking
- Lazy loading
- Transaction coordination

---

# 3. Entity Mapping

An entity represents a table in the database.

Example:

```java
@Entity
@Table(name="customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
```

---

## Common ID Generation Strategies

### IDENTITY

Database generates the primary key.

Example:

```
AUTO_INCREMENT
```

---

### SEQUENCE

Uses a database sequence.

Example:

```
customer_sequence.nextVal()
```

Often performs better for databases that support sequences.

---

### AUTO

Hibernate chooses an appropriate strategy.

---

### TABLE

Stores generated IDs in a separate table.

Less commonly used due to performance overhead.

---

# 4. Spring Data Repository

Example:

```java
public interface CustomerRepository
       extends JpaRepository<Customer, Long> {

}
```

Spring automatically provides methods like:

```
save()
findById()
findAll()
delete()
```

---

## Derived Query Methods

Example:

```java
List<Customer>
findByStatus(String status);
```

Spring generates SQL based on the method name.

---

## JPQL

JPQL works with entities rather than tables.

Example:

```java
@Query(
"SELECT c FROM Customer c WHERE c.status = :status"
)
List<Customer> findActive();
```

---

# 5. Persistence Context

This is one of the most important JPA concepts.

The persistence context is a first-level cache managed by Hibernate.

It tracks entities during a transaction.

Example:

```
Transaction
     |
Persistence Context
     |
Managed Entities
```

---

## Example

```java
Customer customer =
 repository.findById(1L);

Customer customer2 =
 repository.findById(1L);
```

Only one database query is executed.

The second request returns the entity from the persistence context.

---

## Entity States

### Transient

Object exists in memory but is not managed.

Example:

```java
Customer c = new Customer();
```

---

### Persistent

Entity is attached to the persistence context.

Example:

```java
repository.findById(1L);
```

---

### Detached

Previously managed entity no longer attached.

Example:

After transaction ends.

---

### Removed

Marked for deletion.

Example:

```java
repository.delete(customer);
```

---

# 6. Dirty Checking

Hibernate automatically detects changes to managed entities.

Example:

```java
@Transactional
public void updateCustomer() {

 Customer c =
  repository.findById(1L);

 c.setName("John");

}
```

Notice:

```
No save()
```

is called.

---

At transaction commit:

```
Hibernate compares:
Old state vs Current state
```

Generates:

```sql
UPDATE customer
SET name = 'John'
WHERE id = 1;
```

---

## Why Is This Useful?

Advantages:

- Less boilerplate.
- Automatic updates.
- Better domain-driven design.

---

# 7. Fetch Strategies

## Eager Loading

Related entities are loaded immediately.

Example:

```
Order
 |
Customer loaded automatically
```

---

Advantages:

- Simple.

Disadvantages:

- Can fetch unnecessary data.
- Larger queries.
- Performance issues.

---

## Lazy Loading

Related entities are loaded only when accessed.

Example:

```
Order loaded

Later:
order.getCustomer()
     |
Database query executed
```

---

Advantages:

- Better performance when relationships are not always needed.

---

Disadvantages:

Can cause:

```
LazyInitializationException
```

if accessed outside an active persistence context.

---

# 8. N+1 Query Problem

A very common performance issue.

Example:

Get 100 orders.

Query 1:

```sql
SELECT * FROM orders;
```

---

Then for each order:

```sql
SELECT * FROM customer
WHERE id = ?
```

100 additional queries.

---

Total:

```
1 + 100 = 101 queries
```

---

## Solutions

### Fetch Join

Example:

```jpql
SELECT o
FROM Order o
JOIN FETCH o.customer
```

---

### Entity Graph

Allows specifying relationships to fetch.

---

### DTO Projection

Fetch only required columns.

---

# 9. Transactions

A transaction is a unit of work that executes completely or not at all.

---

## ACID Properties

### Atomicity

All operations succeed or rollback.

---

### Consistency

Database remains in a valid state.

---

### Isolation

Concurrent transactions do not interfere improperly.

---

### Durability

Committed changes survive failures.

---

# 10. @Transactional

Spring uses AOP proxies to manage transactions.

Flow:

```
Client
 |
Transaction Proxy
 |
Service Method
 |
Database
```

---

Before method execution:

```
Begin transaction
```

---

Success:

```
Commit
```

---

Exception:

```
Rollback
```

---

# 11. Transaction Propagation

Defines behavior when one transaction calls another.

---

## REQUIRED (Default)

Join existing transaction or create a new one.

Example:

```
Service A Transaction
        |
        |
Service B joins same transaction
```

---

## REQUIRES_NEW

Suspend current transaction and start a new transaction.

Example:

```
Transaction A

Suspended

Transaction B starts
```

Useful for:

- Audit logging
- Independent operations

---

## MANDATORY

Requires an existing transaction.

Throws exception if none exists.

---

# 12. Isolation Levels

Controls visibility of concurrent changes.

---

## READ_UNCOMMITTED

Allows dirty reads.

Highest concurrency.

Lowest consistency.

---

## READ_COMMITTED

Prevents dirty reads.

Most commonly used.

---

## REPEATABLE_READ

Prevents non-repeatable reads.

---

## SERIALIZABLE

Highest consistency.

Transactions behave as if executed one at a time.

Lowest concurrency.

---

# 13. Optimistic Locking

Assumes conflicts are rare.

Uses a version column.

Example:

```java
@Version
private Long version;
```

---

Flow:

User A reads:

```
version = 5
```

User B reads:

```
version = 5
```

---

User A updates:

```
version becomes 6
```

---

User B tries update:

```
WHERE version = 5
```

No rows updated.

Conflict detected.

---

## Good For

- Read-heavy systems.
- Rare conflicts.

Examples:

- User profiles
- Product catalogs

---

# 14. Pessimistic Locking

Locks the database row.

Example:

```
SELECT FOR UPDATE
```

Other transactions must wait.

---

## Good For

High-conflict scenarios.

Examples:

- Inventory updates
- Financial transactions

---

## Trade-Off

More consistency.

Less concurrency.

---

# 15. Connection Pooling

Opening database connections is expensive.

Instead:

```
Application
     |
Connection Pool
     |
Database
```

---

Spring Boot commonly uses:

```
HikariCP
```

---

Benefits:

- Reuse connections.
- Lower latency.
- Better throughput.

---

# 16. Common JPA Performance Issues

## Problem: Too Many Queries

Solution:

- Use fetch joins.
- Use DTO projections.

---

## Problem: Large Result Sets

Solution:

- Pagination.
- Streaming.

---

## Problem: Long Transactions

Solution:

- Keep transaction boundaries small.
- Avoid network calls inside transactions.

---

## Problem: Eager Loading

Solution:

Use lazy loading where appropriate.

---

# 17. L6 Interview Soundbites

## What is the persistence context?

"The persistence context is Hibernate's first-level cache that manages entity lifecycle within a transaction. It tracks entities and enables features like dirty checking."

---

## Why does Hibernate update an entity without save()?

"Because managed entities are tracked by the persistence context. At transaction commit, Hibernate performs dirty checking and automatically generates the required SQL update."

---

## Why avoid long transactions?

"Long transactions hold database connections and locks for a longer time, reducing concurrency and increasing the probability of contention."

---

## When do you use optimistic vs pessimistic locking?

"Optimistic locking is preferred when conflicts are rare because it provides better concurrency. Pessimistic locking is useful when conflicts are frequent and data consistency is critical."

---

## Why should transactions be in the service layer?

"Transactions usually represent a business operation that may involve multiple repositories. Keeping transaction boundaries in the service layer aligns transaction scope with business logic."

---

# Key Takeaways

1. JPA is a specification; Hibernate is a common implementation.

2. The persistence context manages entity lifecycle and acts as a first-level cache.

3. Dirty checking automatically persists changes to managed entities.

4. Lazy loading improves performance but can cause LazyInitializationException.

5. N+1 queries are a common ORM performance problem.

6. @Transactional works using Spring AOP proxies.

7. Choose transaction propagation and isolation levels based on business requirements.

8. Optimistic locking favors concurrency; pessimistic locking favors strict consistency.

9. Keep transactions short and use connection pooling for scalability.

# Microservices & Spring Boot - Part 5
# Spring Data JPA, Hibernate, Transactions & Database Performance

---

# 1. What is JPA?

JPA (Java Persistence API) is a specification that defines how Java objects are mapped to relational databases.

It is not an implementation.

Popular implementations include:

- Hibernate
- EclipseLink

---

## Why Do We Need JPA?

Without an ORM:

```
Application
     |
 JDBC
     |
 SQL Queries
     |
 Database
```

Developers manually handle:

- SQL creation
- ResultSet mapping
- Connection management
- Object conversion

---

With JPA:

```
Application
     |
 Entity Object
     |
 JPA
     |
 Hibernate
     |
 JDBC
     |
 Database
```

The ORM converts Java objects into SQL operations.

---

# 2. What is Hibernate?

Hibernate is the most popular implementation of JPA.

Responsibilities:

- Object-relational mapping (ORM)
- SQL generation
- Entity lifecycle management
- Caching
- Dirty checking
- Lazy loading
- Transaction coordination

---

# 3. Entity Mapping

An entity represents a table in the database.

Example:

```java
@Entity
@Table(name="customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
```

---

## Common ID Generation Strategies

### IDENTITY

Database generates the primary key.

Example:

```
AUTO_INCREMENT
```

---

### SEQUENCE

Uses a database sequence.

Example:

```
customer_sequence.nextVal()
```

Often performs better for databases that support sequences.

---

### AUTO

Hibernate chooses an appropriate strategy.

---

### TABLE

Stores generated IDs in a separate table.

Less commonly used due to performance overhead.

---

# 4. Spring Data Repository

Example:

```java
public interface CustomerRepository
       extends JpaRepository<Customer, Long> {

}
```

Spring automatically provides methods like:

```
save()
findById()
findAll()
delete()
```

---

## Derived Query Methods

Example:

```java
List<Customer>
findByStatus(String status);
```

Spring generates SQL based on the method name.

---

## JPQL

JPQL works with entities rather than tables.

Example:

```java
@Query(
"SELECT c FROM Customer c WHERE c.status = :status"
)
List<Customer> findActive();
```

---

# 5. Persistence Context

This is one of the most important JPA concepts.

The persistence context is a first-level cache managed by Hibernate.

It tracks entities during a transaction.

Example:

```
Transaction
     |
Persistence Context
     |
Managed Entities
```

---

## Example

```java
Customer customer =
 repository.findById(1L);

Customer customer2 =
 repository.findById(1L);
```

Only one database query is executed.

The second request returns the entity from the persistence context.

---

## Entity States

### Transient

Object exists in memory but is not managed.

Example:

```java
Customer c = new Customer();
```

---

### Persistent

Entity is attached to the persistence context.

Example:

```java
repository.findById(1L);
```

---

### Detached

Previously managed entity no longer attached.

Example:

After transaction ends.

---

### Removed

Marked for deletion.

Example:

```java
repository.delete(customer);
```

---

# 6. Dirty Checking

Hibernate automatically detects changes to managed entities.

Example:

```java
@Transactional
public void updateCustomer() {

 Customer c =
  repository.findById(1L);

 c.setName("John");

}
```

Notice:

```
No save()
```

is called.

---

At transaction commit:

```
Hibernate compares:
Old state vs Current state
```

Generates:

```sql
UPDATE customer
SET name = 'John'
WHERE id = 1;
```

---

## Why Is This Useful?

Advantages:

- Less boilerplate.
- Automatic updates.
- Better domain-driven design.

---

# 7. Fetch Strategies

## Eager Loading

Related entities are loaded immediately.

Example:

```
Order
 |
Customer loaded automatically
```

---

Advantages:

- Simple.

Disadvantages:

- Can fetch unnecessary data.
- Larger queries.
- Performance issues.

---

## Lazy Loading

Related entities are loaded only when accessed.

Example:

```
Order loaded

Later:
order.getCustomer()
     |
Database query executed
```

---

Advantages:

- Better performance when relationships are not always needed.

---

Disadvantages:

Can cause:

```
LazyInitializationException
```

if accessed outside an active persistence context.

---

# 8. N+1 Query Problem

A very common performance issue.

Example:

Get 100 orders.

Query 1:

```sql
SELECT * FROM orders;
```

---

Then for each order:

```sql
SELECT * FROM customer
WHERE id = ?
```

100 additional queries.

---

Total:

```
1 + 100 = 101 queries
```

---

## Solutions

### Fetch Join

Example:

```jpql
SELECT o
FROM Order o
JOIN FETCH o.customer
```

---

### Entity Graph

Allows specifying relationships to fetch.

---

### DTO Projection

Fetch only required columns.

---

# 9. Transactions

A transaction is a unit of work that executes completely or not at all.

---

## ACID Properties

### Atomicity

All operations succeed or rollback.

---

### Consistency

Database remains in a valid state.

---

### Isolation

Concurrent transactions do not interfere improperly.

---

### Durability

Committed changes survive failures.

---

# 10. @Transactional

Spring uses AOP proxies to manage transactions.

Flow:

```
Client
 |
Transaction Proxy
 |
Service Method
 |
Database
```

---

Before method execution:

```
Begin transaction
```

---

Success:

```
Commit
```

---

Exception:

```
Rollback
```

---

# 11. Transaction Propagation

Defines behavior when one transaction calls another.

---

## REQUIRED (Default)

Join existing transaction or create a new one.

Example:

```
Service A Transaction
        |
        |
Service B joins same transaction
```

---

## REQUIRES_NEW

Suspend current transaction and start a new transaction.

Example:

```
Transaction A

Suspended

Transaction B starts
```

Useful for:

- Audit logging
- Independent operations

---

## MANDATORY

Requires an existing transaction.

Throws exception if none exists.

---

# 12. Isolation Levels

Controls visibility of concurrent changes.

---

## READ_UNCOMMITTED

Allows dirty reads.

Highest concurrency.

Lowest consistency.

---

## READ_COMMITTED

Prevents dirty reads.

Most commonly used.

---

## REPEATABLE_READ

Prevents non-repeatable reads.

---

## SERIALIZABLE

Highest consistency.

Transactions behave as if executed one at a time.

Lowest concurrency.

---

# 13. Optimistic Locking

Assumes conflicts are rare.

Uses a version column.

Example:

```java
@Version
private Long version;
```

---

Flow:

User A reads:

```
version = 5
```

User B reads:

```
version = 5
```

---

User A updates:

```
version becomes 6
```

---

User B tries update:

```
WHERE version = 5
```

No rows updated.

Conflict detected.

---

## Good For

- Read-heavy systems.
- Rare conflicts.

Examples:

- User profiles
- Product catalogs

---

# 14. Pessimistic Locking

Locks the database row.

Example:

```
SELECT FOR UPDATE
```

Other transactions must wait.

---

## Good For

High-conflict scenarios.

Examples:

- Inventory updates
- Financial transactions

---

## Trade-Off

More consistency.

Less concurrency.

---

# 15. Connection Pooling

Opening database connections is expensive.

Instead:

```
Application
     |
Connection Pool
     |
Database
```

---

Spring Boot commonly uses:

```
HikariCP
```

---

Benefits:

- Reuse connections.
- Lower latency.
- Better throughput.

---

# 16. Common JPA Performance Issues

## Problem: Too Many Queries

Solution:

- Use fetch joins.
- Use DTO projections.

---

## Problem: Large Result Sets

Solution:

- Pagination.
- Streaming.

---

## Problem: Long Transactions

Solution:

- Keep transaction boundaries small.
- Avoid network calls inside transactions.

---

## Problem: Eager Loading

Solution:

Use lazy loading where appropriate.

---

# 17. L6 Interview Soundbites

## What is the persistence context?

"The persistence context is Hibernate's first-level cache that manages entity lifecycle within a transaction. It tracks entities and enables features like dirty checking."

---

## Why does Hibernate update an entity without save()?

"Because managed entities are tracked by the persistence context. At transaction commit, Hibernate performs dirty checking and automatically generates the required SQL update."

---

## Why avoid long transactions?

"Long transactions hold database connections and locks for a longer time, reducing concurrency and increasing the probability of contention."

---

## When do you use optimistic vs pessimistic locking?

"Optimistic locking is preferred when conflicts are rare because it provides better concurrency. Pessimistic locking is useful when conflicts are frequent and data consistency is critical."

---

## Why should transactions be in the service layer?

"Transactions usually represent a business operation that may involve multiple repositories. Keeping transaction boundaries in the service layer aligns transaction scope with business logic."

---

# Key Takeaways

1. JPA is a specification; Hibernate is a common implementation.

2. The persistence context manages entity lifecycle and acts as a first-level cache.

3. Dirty checking automatically persists changes to managed entities.

4. Lazy loading improves performance but can cause LazyInitializationException.

5. N+1 queries are a common ORM performance problem.

6. @Transactional works using Spring AOP proxies.

7. Choose transaction propagation and isolation levels based on business requirements.

8. Optimistic locking favors concurrency; pessimistic locking favors strict consistency.

9. Keep transactions short and use connection pooling for scalability.

# Microservices & Spring Boot - Part 7
# Spring Cloud Infrastructure

---

# 1. The Problem with Distributed Microservices

A microservice architecture may contain:

```
Order Service
Inventory Service
Payment Service
User Service
Notification Service
```

Each service may have:

- Multiple instances.
- Different environments (dev, QA, prod).
- Dynamic scaling.
- Frequent deployments.

Questions arise:

- How does Order Service find Inventory Service?
- Where do services get their configuration?
- How do clients know which service instance to call?
- How do we apply authentication and rate limiting consistently?

Spring Cloud provides solutions to these distributed system challenges.

---

# 2. Configuration Management Problem

In small applications, configuration may live inside:

```
application.properties
```

Example:

```properties
database.url=jdbc:mysql://localhost:3306/order
inventory.url=http://localhost:8080
```

---

This becomes difficult when there are:

- Hundreds of services.
- Multiple environments.
- Frequent configuration changes.

Problems:

- Configuration duplication.
- Manual updates.
- Configuration drift.
- Difficult rollbacks.

---

# 3. Centralized Configuration with Spring Cloud Config

The idea:

Store configuration in a central location.

Example architecture:

```
                 Git Repository
                        |
                Spring Cloud Config Server
                        |
       ---------------------------------------
       |                    |                 |
 Order Service      Inventory Service   Payment Service
```

---

## How It Works

At startup:

```
Microservice
      |
Requests configuration
      |
Config Server
      |
Git Repository
```

The service receives environment-specific properties.

Example:

```
order-service-dev.properties

order-service-prod.properties
```

---

## Advantages

- Centralized configuration management.
- Version history using Git.
- Easy rollback.
- Environment separation.
- No need to rebuild applications for config changes.

---

## Security Considerations

Sensitive information should not be stored as plain text.

Examples:

- Database passwords.
- API keys.
- Certificates.

Use:

- HashiCorp Vault.
- AWS Secrets Manager.
- Kubernetes Secrets.

---

# 4. The Service Discovery Problem

In a dynamic environment:

```
Inventory Service

Instance 1:
10.0.1.20

Instance 2:
10.0.1.21

Instance 3:
10.0.1.22
```

IPs may change because:

- Auto-scaling.
- Container restarts.
- Failures.
- New deployments.

Hardcoding:

```
inventory-service = 10.0.1.20
```

is not practical.

---

# 5. Service Registry

A service registry maintains a list of available service instances.

Example:

```
                Service Registry
                      |
          --------------------------
          |                        |
   Inventory Instance 1      Inventory Instance 2
```

Each service:

- Registers itself.
- Sends periodic heartbeats.
- Deregisters when shutting down.

---

# 6. Eureka Service Discovery

Eureka is Netflix's service registry.

Example:

```
              Eureka Server
                     |
        --------------------------
        |                        |
 Inventory Service          Order Service
    registers                   queries
```

---

## Registration Flow

Inventory Service starts:

```
Inventory Service
       |
Register:
inventory-service
IP: 10.0.1.20
Port: 8080
       |
Eureka Server
```

---

Order Service needs Inventory:

```
Order Service
       |
Ask Eureka:
"Give me inventory-service instances"
       |
Receives:
10.0.1.20
10.0.1.21
```

---

# 7. Client-Side Load Balancing

The client receives a list of available instances.

Example:

```
Order Service

Inventory Instances:

10.0.1.20
10.0.1.21
10.0.1.22
```

The client chooses one.

---

Example algorithms:

- Round robin.
- Random.
- Weighted selection.

---

## Spring Cloud LoadBalancer

Modern Spring applications use:

```
Spring Cloud LoadBalancer
```

Example:

```
Order Service
      |
Spring Cloud LoadBalancer
      |
Inventory Service Instance
```

---

## Advantages

- No central load balancer bottleneck.
- Service has knowledge of available instances.
- Works well in internal microservice communication.

---

# 8. Server-Side vs Client-Side Load Balancing

## Server-Side

Example:

```
Client
  |
Load Balancer
  |
----------------
|              |
Service A    Service B
```

Examples:

- NGINX
- AWS Application Load Balancer

---

Advantages:

- Simple clients.
- Centralized routing.

Disadvantages:

- Additional network hop.
- Load balancer becomes another component to manage.

---

## Client-Side

Example:

```
Client
 |
Service Discovery
 |
Choose Instance
 |
Service Instance
```

---

Advantages:

- Reduced extra hop.
- Better service awareness.

Disadvantages:

- More complex client libraries.

---

# 9. API Gateway

In a microservice architecture, clients should generally not communicate directly with every service.

Bad:

```
Mobile App
 |
----------------------------
|            |              |
User       Order        Payment
Service    Service      Service
```

Problems:

- Client needs to know all services.
- Authentication logic duplicated.
- Difficult to apply rate limiting.
- API changes affect clients.

---

# 10. API Gateway Architecture

Better:

```
             Client
                |
           API Gateway
                |
--------------------------------
|              |               |
User        Order          Payment
Service     Service        Service
```

---

# 11. Responsibilities of API Gateway

## Routing

Example:

```
/users/*     -> User Service
/orders/*    -> Order Service
/payments/*  -> Payment Service
```

---

## Authentication

Validate:

- OAuth tokens.
- JWT tokens.

---

## Authorization

Enforce:

- Roles.
- Permissions.

---

## Rate Limiting

Protect backend services.

Examples:

```
Free User:
100 requests/hour

Premium User:
10000 requests/hour
```

---

## SSL Termination

The gateway handles HTTPS connections.

Internal services may communicate over private networks.

---

## Request Transformation

Examples:

- Add headers.
- Remove sensitive information.
- Transform payloads.

---

## Observability

Central place for:

- Logging.
- Metrics.
- Distributed tracing.

---

# 12. Backend for Frontend (BFF)

Different clients may have different needs.

Example:

```
          Backend Systems
                 |
        -------------------
        |                 |
 Mobile BFF          Web BFF
        |                 |
     Mobile App       Browser
```

---

Mobile may need:

- Smaller responses.
- Fewer network calls.

Web may need:

- More detailed data.

---

Benefits:

- Client-specific optimization.
- Reduced over-fetching.
- Independent frontend evolution.

---

# 13. Service Discovery in Kubernetes

Modern cloud environments often do not use Eureka.

Kubernetes provides built-in service discovery.

Example:

```
Order Service
      |
inventory-service.default.svc.cluster.local
      |
Kubernetes Service
      |
Pods
```

---

Responsibilities:

- DNS-based discovery.
- Load balancing.
- Health checking.

---

## Interview Soundbite

"Eureka solved service discovery before Kubernetes became widely adopted. In modern cloud-native environments, Kubernetes Services and DNS often replace Eureka, but the underlying problem remains the same: services need a dynamic way to locate healthy instances."

---

# 14. Failure Scenarios

## Config Server is Down

Existing services continue running using already-loaded configuration.

However:

- New instances may fail to start.
- Configuration updates may be unavailable.

---

## Eureka Registry is Down

Existing instances can continue using cached service information.

However:

- New service registrations may fail.
- Discovery information may become stale.

---

## API Gateway Failure

Because it is a critical entry point:

Solutions:

- Run multiple gateway instances.
- Place behind a load balancer.
- Monitor health.
- Enable autoscaling.

---

# 15. L6 Interview Soundbites

## Why use centralized configuration?

"Centralized configuration separates configuration from application binaries. It allows environment-specific settings, versioning, easier rollbacks, and avoids rebuilding services when configuration changes."

---

## Why do we need service discovery?

"In dynamic environments, service instances constantly change due to scaling, failures, and deployments. Service discovery provides a reliable mechanism for locating healthy instances."

---

## Why use an API Gateway?

"An API Gateway provides a single entry point for clients and centralizes cross-cutting concerns such as authentication, routing, rate limiting, SSL termination, and observability."

---

## When would you choose BFF?

"I would introduce a Backend for Frontend when different clients have significantly different data requirements. It allows mobile and web applications to evolve independently without overloading the clients with unnecessary data."

---

## Is Eureka still relevant?

"The concept of service discovery is absolutely relevant. While many modern Kubernetes-based systems rely on DNS-based discovery instead of Eureka, the architectural problem being solved remains the same."

---

# Key Takeaways

1. Microservices require infrastructure for configuration, discovery, routing, and security.

2. Spring Cloud Config centralizes environment-specific configuration.

3. Service discovery removes hardcoded network locations.

4. Eureka uses registration and heartbeats to maintain available instances.

5. Client-side load balancing allows services to choose healthy instances.

6. API Gateways centralize authentication, routing, security, and traffic management.

7. BFF provides client-specific APIs.

8. Kubernetes provides built-in service discovery and often replaces Eureka.

# Microservices & Spring Boot - Part 8
# Spring Security, OAuth2, OpenID Connect & API Security

---

# 1. Why Do We Need Spring Security?

A production API must answer two questions:

### Authentication

"Who are you?"

Example:

```
User Alice logs in.

System verifies:
- Username/password
- MFA
- OAuth login
```

---

### Authorization

"What are you allowed to do?"

Example:

```
Endpoint:

DELETE /admin/users/123
```

User:

```
ROLE_USER
```

Result:

```
403 Forbidden
```

because the user lacks the required permission.

---

# 2. Spring Security Architecture

Spring Security is built around a chain of filters.

A request does not directly reach the controller.

Flow:

```
HTTP Request
      |
Embedded Tomcat
      |
Security Filter Chain
      |
DispatcherServlet
      |
Controller
      |
Service
```

---

## Why Filters?

Filters can intercept every request and apply cross-cutting security concerns:

- Authentication
- Authorization
- CSRF protection
- Session management
- Exception handling

---

# 3. Security Filter Chain

The Security Filter Chain contains multiple filters executed in order.

Examples:

```
Request
 |
Authentication Filter
 |
Authorization Filter
 |
Exception Filter
 |
Controller
```

---

## Interview Soundbite

"Spring Security works as a filter chain in front of Spring MVC. Every incoming request passes through security filters before reaching the DispatcherServlet."

---

# 4. Authentication Flow with Username and Password

Traditional flow:

```
Client
 |
POST /login
 |
UsernamePasswordAuthenticationFilter
 |
AuthenticationManager
 |
AuthenticationProvider
 |
UserDetailsService
 |
Database
```

---

## Step 1: Authentication Filter

Extracts credentials from the request.

Example:

```
username=alice
password=****** 
```

---

## Step 2: AuthenticationManager

Coordinates authentication.

It decides which AuthenticationProvider should process the request.

---

## Step 3: AuthenticationProvider

Validates credentials.

Examples:

- Database authentication
- LDAP
- External identity providers

---

## Step 4: UserDetailsService

Loads user information.

Example:

```
User:
Alice

Roles:
ADMIN
USER
```

---

# 5. Password Storage

Passwords should never be stored as plain text.

Bad:

```
password = mySecret123
```

---

Good:

```
password = BCrypt hash
```

---

## Why Hash Passwords?

If the database is compromised:

```
Attacker sees:
$2a$10$K7H.....
```

instead of the real password.

---

# 6. JWT Authentication in Spring Security

Modern microservices commonly use stateless authentication.

Flow:

```
Client
 |
Authorization: Bearer JWT
 |
JWT Authentication Filter
 |
Validate Token
 |
Create Authentication Object
 |
Security Context
 |
Controller
```

---

## What Happens Internally?

### Step 1

The filter extracts the JWT.

Example:

```
Authorization:
Bearer eyJhbGci...
```

---

### Step 2

Validate:

- Signature
- Expiration
- Issuer
- Audience

---

### Step 3

Extract claims:

Example:

```
userId = 123

roles = ADMIN
```

---

### Step 4

Create an Authentication object.

Example:

```
UsernamePasswordAuthenticationToken
```

---

### Step 5

Store it in:

```
SecurityContextHolder
```

The rest of the application can access the authenticated user.

---

# 7. SecurityContext

SecurityContext stores information about the current authenticated request.

Example:

```
SecurityContext
      |
Authentication
      |
Principal + Roles
```

---

Example in code:

```java
Authentication auth =
 SecurityContextHolder
    .getContext()
    .getAuthentication();
```

---

# 8. OAuth2 Roles

In OAuth2:

```
User
 |
Client Application
 |
Authorization Server
 |
Resource Server
```

---

## Authorization Server

Responsible for:

- Authenticating users.
- Issuing access tokens.

Examples:

- Keycloak
- Okta
- Auth0

---

## Resource Server

The API receiving the token.

Example:

```
Order Service
```

Responsibilities:

- Validate JWT.
- Enforce authorization.

---

# 9. OpenID Connect (OIDC)

OAuth2 answers:

```
Can this application access a resource?
```

---

OpenID Connect answers:

```
Who is the user?
```

---

OIDC is an identity layer built on top of OAuth2.

It introduces an ID Token.

Example:

```
OAuth2:
Access Token

OIDC:
Access Token
+
ID Token
```

---

## Common Example

```
Login with Google
```

The application receives identity information about the user through OIDC.

---

# 10. Keycloak Architecture

Example:

```
           Keycloak
               |
      ----------------
      |              |
 User Login      Token Issuance
               |
             JWT
               |
      ----------------
      |              |
 Order API      Payment API
```

---

Keycloak manages:

- Users
- Roles
- Groups
- Password policies
- MFA
- OAuth2 flows
- Token issuance

---

# 11. Authorization in Spring

## URL-Based Authorization

Example:

```java
http
.authorizeHttpRequests(auth ->
 auth
 .requestMatchers("/admin/**")
 .hasRole("ADMIN")
 .anyRequest()
 .authenticated()
);
```

---

## Method-Level Authorization

Example:

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser() {
}
```

---

Advantages:

- Authorization close to business logic.
- Fine-grained access control.

---

# 12. CSRF Protection

CSRF is an attack where a malicious website causes a user's browser to send unintended requests.

Example:

```
User logged into bank.com

Visits malicious website

Hidden request:
Transfer $1000
```

---

CSRF is especially relevant for:

- Browser applications using cookies.

---

For stateless JWT APIs:

```
Authorization:
Bearer token
```

CSRF is generally disabled because the browser does not automatically attach JWT tokens like cookies.

---

# 13. CORS

CORS controls which domains can call your API.

Example:

Allowed:

```
https://myapp.com
```

Blocked:

```
https://evil.com
```

---

Common configuration:

- Allowed origins
- HTTP methods
- Headers

---

# 14. Stateless vs Stateful Security

## Session-Based

```
Client
 |
Session ID Cookie
 |
Server Session Store
```

Problems:

- Sticky sessions.
- Shared session stores.
- Harder horizontal scaling.

---

## JWT-Based

```
Client
 |
JWT Token
 |
Any API Instance
```

Benefits:

- Stateless.
- Cloud friendly.
- Easier scaling.

---

# 15. Security Best Practices

Use:

- HTTPS everywhere.
- Short-lived access tokens.
- Refresh tokens.
- RBAC or ABAC.
- Least privilege.
- Secure password hashing.
- Input validation.
- Audit logging.
- Rate limiting.

---

# 16. L6 Interview Soundbites

## What happens when a secured request reaches Spring Boot?

"The request first enters the Security Filter Chain. Authentication filters validate the user's identity and populate the SecurityContext. Authorization filters verify permissions before allowing the request to reach the controller."

---

## Why use JWT in microservices?

"JWT allows stateless authentication. Any service instance can validate the token without maintaining server-side session state, making horizontal scaling easier."

---

## What is the role of Keycloak or Okta?

"They act as authorization servers responsible for authenticating users and issuing signed tokens. Microservices act as resource servers that validate tokens and enforce access policies."

---

## OAuth2 vs OpenID Connect?

"OAuth2 is a delegation framework that allows applications to access resources on behalf of a user. OpenID Connect extends OAuth2 by adding identity information through ID tokens."

---

## Why use method-level security?

"URL-level rules provide coarse-grained protection, but business operations often require finer authorization checks. Method-level security keeps authorization rules close to the business logic."

---

# Key Takeaways

1. Spring Security protects applications through a Security Filter Chain.

2. Authentication verifies identity; authorization controls access.

3. JWT authentication stores user identity inside a signed token and uses SecurityContext to make it available during the request.

4. OAuth2 manages delegated access; OIDC adds identity.

5. Keycloak and Okta are common authorization servers.

6. Method-level security provides fine-grained authorization.

7. Stateless JWT security scales well for cloud-native microservices.

8. Security requires multiple layers including HTTPS, validation, least privilege, and auditing.

# Microservices & Spring Boot - Part 9
# Resilience, Fault Tolerance & Resilience4j

---

# 1. Why Do Distributed Calls Fail?

In a monolithic application:

```
Order Module
     |
Inventory Module
```

Communication happens inside the same process.

---

In microservices:

```
Order Service
      |
 HTTP/Kafka
      |
Inventory Service
```

Every network call introduces failure possibilities.

---

Common failures:

- Network latency.
- Temporary outages.
- Dependency failures.
- Thread exhaustion.
- Database slowdowns.
- Resource exhaustion.
- Traffic spikes.

---

## Fundamental Principle

Distributed systems are designed assuming failures will happen.

The goal is not to eliminate failures.

The goal is to:
- Detect failures quickly.
- Contain the impact.
- Recover gracefully.

---

# 2. Timeouts

Never wait indefinitely for a dependency.

Bad:

```
Order Service
      |
Inventory Service
      |
Wait forever...
```

---

Good:

```
Order Service
      |
Inventory Service
      |
Wait 500ms
      |
Timeout
      |
Return fallback or error
```

---

## Why Timeouts Matter

Without timeouts:

- Threads remain blocked.
- Thread pools become exhausted.
- New requests cannot be processed.
- Failure spreads to healthy services.

---

## Choosing Timeout Values

Timeouts should be based on:

- Normal dependency latency.
- P95/P99 response times.
- Business requirements.

Example:

```
Inventory API:
P99 latency = 200ms

Timeout = 300-500ms
```

---

## L6 Interview Soundbite

"I choose timeout values using actual production latency metrics rather than arbitrary numbers. The timeout should be slightly above normal high-percentile latency while still protecting the caller."

---

# 3. Retries

Retries handle transient failures.

Example:

```
Request fails due to temporary network issue.

Retry after a short delay.

Request succeeds.
```

---

## Good Candidates for Retry

- Network timeouts.
- Temporary dependency unavailability.
- HTTP 503 responses.
- Rate-limited requests where a retry is allowed.

---

## Bad Candidates for Retry

Do not retry:

- Validation errors.
- Authentication failures.
- Permanent failures.

Examples:

```
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
```

---

# 4. Retry Storm Problem

Retries can make outages worse.

Example:

Normal traffic:

```
1,000 requests/second
```

Dependency becomes slow.

Every request retries 3 times:

```
Original:
1,000 RPS

Retries:
3,000 additional RPS
```

The failing dependency receives:

```
4,000 RPS
```

The outage becomes worse.

---

# 5. Exponential Backoff

Do not retry immediately.

Bad:

```
Retry after:
100ms
100ms
100ms
```

---

Good:

```
Retry 1:
100ms

Retry 2:
200ms

Retry 3:
400ms
```

---

This gives the dependency time to recover.

---

# 6. Jitter

If thousands of clients retry at the same time:

```
10,000 clients
        |
Retry exactly at 500ms
        |
Traffic spike
```

---

Add randomness:

```
Retry 1:
100-200ms

Retry 2:
300-500ms
```

---

Benefits:

- Avoids synchronized retry spikes.
- Smooths traffic.
- Reduces pressure on dependencies.

---

# 7. Circuit Breaker Pattern

A circuit breaker prevents repeatedly calling a failing dependency.

---

## Closed State

Normal operation.

```
Request
   |
Dependency
   |
Success
```

Requests are allowed.

---

## Open State

Failure threshold exceeded.

```
Request
   |
Circuit Open
   |
Fail immediately
```

No calls go to the dependency.

---

Benefits:

- Protects the dependency.
- Protects caller resources.
- Enables faster failure.

---

## Half-Open State

After a wait period:

```
Allow limited test requests.
```

If successful:

```
Half-Open -> Closed
```

If failures continue:

```
Half-Open -> Open
```

---

# 8. Circuit Breaker Example

Scenario:

```
Payment Service
        |
External Bank API
```

Bank API is down.

Without circuit breaker:

```
Every payment request waits 5 seconds.

Threads accumulate.

Application becomes unavailable.
```

---

With circuit breaker:

```
Failure threshold reached.

Circuit opens.

Requests fail immediately.
```

The system remains healthy.

---

# 9. Fallback Strategies

When a dependency fails, provide an alternative.

Examples:

## Cached Data

```
Product Service unavailable.

Return cached product information.
```

---

## Default Response

Example:

```
Recommendation service unavailable.

Return popular products.
```

---

## Queue For Later

Example:

```
Email service unavailable.

Publish event to Kafka.
```

---

## Graceful Degradation

Example:

```
E-commerce site:

Browsing works.

Recommendations temporarily disabled.
```

---

# 10. Bulkhead Pattern

The idea comes from ship compartments.

A hole in one compartment should not sink the entire ship.

---

Example:

```
Application Threads

-----------------
Inventory Pool
-----------------
Payment Pool
-----------------
Notification Pool
-----------------
```

---

If Notification Service becomes slow:

```
Notification threads exhausted.
```

Inventory and Payment continue working.

---

## Implementation Approaches

- Separate thread pools.
- Connection pool limits.
- Semaphore limits.

---

# 11. Rate Limiting

Rate limiting protects services from excessive traffic.

Examples:

```
Free users:
100 requests/minute

Premium users:
10,000 requests/minute
```

---

Algorithms:

- Token bucket.
- Leaky bucket.
- Sliding window.
- Fixed window.

---

# 12. Controlled Concurrency

This directly applies to external dependency protection.

Example:

External vendor allows:

```
Maximum 50 concurrent requests.
```

Bad:

```
500 threads call vendor simultaneously.
```

Results:

- Vendor overload.
- Timeouts.
- Failures.

---

Better:

```
Semaphore permits: 50

Request arrives
       |
Acquire permit
       |
Call vendor
       |
Release permit
```

---

Benefits:

- Protects downstream services.
- Prevents resource exhaustion.
- Provides predictable load.

---

## Real Production Example

```
Client Screening Service
             |
             |
     External Screening Vendor
```

We observed:

- Increased latency.
- Timeout errors.
- Vendor saturation.

Solution:

- Introduced concurrency limits.
- Controlled parallel requests.
- Reduced downstream pressure.
- Improved overall system stability.

---

# 13. Resilience4j Overview

Resilience4j is a lightweight fault-tolerance library.

It provides:

- Circuit Breaker.
- Retry.
- Rate Limiter.
- Bulkhead.
- Time Limiter.

---

## Spring Boot Integration

Example:

```java
@CircuitBreaker(
    name = "paymentService",
    fallbackMethod = "fallback"
)
public PaymentResponse pay() {
    return client.call();
}
```

---

Retry example:

```java
@Retry(name="inventoryService")
public Inventory getInventory() {
    return client.get();
}
```

---

Bulkhead example:

```java
@Bulkhead(name="vendorService")
public Response callVendor() {
    return client.call();
}
```

---

# 14. Combining Patterns

A typical production call may use:

```
Client Request
       |
Timeout
       |
Retry with Backoff + Jitter
       |
Circuit Breaker
       |
Bulkhead Limit
       |
External Dependency
```

---

The order and configuration must be chosen carefully.

For example:

- Retry only transient failures.
- Do not retry forever.
- Ensure retries do not bypass rate limits.

---

# 15. Observability of Resilience Patterns

Monitor:

Circuit Breaker:
- Open count.
- Failure rate.
- Slow calls.

Retries:
- Retry count.
- Success after retry.

Bulkheads:
- Queue size.
- Rejected requests.

Timeouts:
- Timeout frequency.

---

# 16. L6 Interview Soundbites

## Why are retries dangerous?

"Retries can amplify failures. During an outage, uncontrolled retries may create a retry storm that increases traffic and prevents the dependency from recovering."

---

## Why use a circuit breaker?

"A circuit breaker allows the system to fail fast when a dependency is unhealthy, protecting threads and reducing unnecessary pressure on downstream services."

---

## How do you protect an external API?

"I combine timeout limits, controlled concurrency, rate limiting, and circuit breakers. The goal is to make sure my service remains healthy even when the dependency is slow or unavailable."

---

## When do you use a bulkhead?

"I use bulkheads when different dependencies have different reliability characteristics. Isolating resources prevents one failing dependency from consuming all available threads or connections."

---

## What is the biggest mistake with resilience?

"Adding retries without understanding the failure mode. Retries can be beneficial for transient failures but harmful during overload scenarios."

---

# Key Takeaways

1. Failures are normal in distributed systems.

2. Timeouts prevent resources from being blocked indefinitely.

3. Retries should target transient failures and use backoff with jitter.

4. Circuit breakers fail fast and prevent cascading failures.

5. Bulkheads isolate failures.

6. Controlled concurrency protects downstream systems.

7. Rate limiting protects services from abuse.

8. Resilience4j provides production-ready implementations of these patterns.

9. Resilience patterns must be observable and tuned using real production metrics.

# Microservices & Spring Boot - Part 9
# Resilience, Fault Tolerance & Resilience4j

---

# 1. Why Do Distributed Calls Fail?

In a monolithic application:

```
Order Module
     |
Inventory Module
```

Communication happens inside the same process.

---

In microservices:

```
Order Service
      |
 HTTP/Kafka
      |
Inventory Service
```

Every network call introduces failure possibilities.

---

Common failures:

- Network latency.
- Temporary outages.
- Dependency failures.
- Thread exhaustion.
- Database slowdowns.
- Resource exhaustion.
- Traffic spikes.

---

## Fundamental Principle

Distributed systems are designed assuming failures will happen.

The goal is not to eliminate failures.

The goal is to:
- Detect failures quickly.
- Contain the impact.
- Recover gracefully.

---

# 2. Timeouts

Never wait indefinitely for a dependency.

Bad:

```
Order Service
      |
Inventory Service
      |
Wait forever...
```

---

Good:

```
Order Service
      |
Inventory Service
      |
Wait 500ms
      |
Timeout
      |
Return fallback or error
```

---

## Why Timeouts Matter

Without timeouts:

- Threads remain blocked.
- Thread pools become exhausted.
- New requests cannot be processed.
- Failure spreads to healthy services.

---

## Choosing Timeout Values

Timeouts should be based on:

- Normal dependency latency.
- P95/P99 response times.
- Business requirements.

Example:

```
Inventory API:
P99 latency = 200ms

Timeout = 300-500ms
```

---

## L6 Interview Soundbite

"I choose timeout values using actual production latency metrics rather than arbitrary numbers. The timeout should be slightly above normal high-percentile latency while still protecting the caller."

---

# 3. Retries

Retries handle transient failures.

Example:

```
Request fails due to temporary network issue.

Retry after a short delay.

Request succeeds.
```

---

## Good Candidates for Retry

- Network timeouts.
- Temporary dependency unavailability.
- HTTP 503 responses.
- Rate-limited requests where a retry is allowed.

---

## Bad Candidates for Retry

Do not retry:

- Validation errors.
- Authentication failures.
- Permanent failures.

Examples:

```
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
```

---

# 4. Retry Storm Problem

Retries can make outages worse.

Example:

Normal traffic:

```
1,000 requests/second
```

Dependency becomes slow.

Every request retries 3 times:

```
Original:
1,000 RPS

Retries:
3,000 additional RPS
```

The failing dependency receives:

```
4,000 RPS
```

The outage becomes worse.

---

# 5. Exponential Backoff

Do not retry immediately.

Bad:

```
Retry after:
100ms
100ms
100ms
```

---

Good:

```
Retry 1:
100ms

Retry 2:
200ms

Retry 3:
400ms
```

---

This gives the dependency time to recover.

---

# 6. Jitter

If thousands of clients retry at the same time:

```
10,000 clients
        |
Retry exactly at 500ms
        |
Traffic spike
```

---

Add randomness:

```
Retry 1:
100-200ms

Retry 2:
300-500ms
```

---

Benefits:

- Avoids synchronized retry spikes.
- Smooths traffic.
- Reduces pressure on dependencies.

---

# 7. Circuit Breaker Pattern

A circuit breaker prevents repeatedly calling a failing dependency.

---

## Closed State

Normal operation.

```
Request
   |
Dependency
   |
Success
```

Requests are allowed.

---

## Open State

Failure threshold exceeded.

```
Request
   |
Circuit Open
   |
Fail immediately
```

No calls go to the dependency.

---

Benefits:

- Protects the dependency.
- Protects caller resources.
- Enables faster failure.

---

## Half-Open State

After a wait period:

```
Allow limited test requests.
```

If successful:

```
Half-Open -> Closed
```

If failures continue:

```
Half-Open -> Open
```

---

# 8. Circuit Breaker Example

Scenario:

```
Payment Service
        |
External Bank API
```

Bank API is down.

Without circuit breaker:

```
Every payment request waits 5 seconds.

Threads accumulate.

Application becomes unavailable.
```

---

With circuit breaker:

```
Failure threshold reached.

Circuit opens.

Requests fail immediately.
```

The system remains healthy.

---

# 9. Fallback Strategies

When a dependency fails, provide an alternative.

Examples:

## Cached Data

```
Product Service unavailable.

Return cached product information.
```

---

## Default Response

Example:

```
Recommendation service unavailable.

Return popular products.
```

---

## Queue For Later

Example:

```
Email service unavailable.

Publish event to Kafka.
```

---

## Graceful Degradation

Example:

```
E-commerce site:

Browsing works.

Recommendations temporarily disabled.
```

---

# 10. Bulkhead Pattern

The idea comes from ship compartments.

A hole in one compartment should not sink the entire ship.

---

Example:

```
Application Threads

-----------------
Inventory Pool
-----------------
Payment Pool
-----------------
Notification Pool
-----------------
```

---

If Notification Service becomes slow:

```
Notification threads exhausted.
```

Inventory and Payment continue working.

---

## Implementation Approaches

- Separate thread pools.
- Connection pool limits.
- Semaphore limits.

---

# 11. Rate Limiting

Rate limiting protects services from excessive traffic.

Examples:

```
Free users:
100 requests/minute

Premium users:
10,000 requests/minute
```

---

Algorithms:

- Token bucket.
- Leaky bucket.
- Sliding window.
- Fixed window.

---

# 12. Controlled Concurrency

This directly applies to external dependency protection.

Example:

External vendor allows:

```
Maximum 50 concurrent requests.
```

Bad:

```
500 threads call vendor simultaneously.
```

Results:

- Vendor overload.
- Timeouts.
- Failures.

---

Better:

```
Semaphore permits: 50

Request arrives
       |
Acquire permit
       |
Call vendor
       |
Release permit
```

---

Benefits:

- Protects downstream services.
- Prevents resource exhaustion.
- Provides predictable load.

---

## Real Production Example

```
Client Screening Service
             |
             |
     External Screening Vendor
```

We observed:

- Increased latency.
- Timeout errors.
- Vendor saturation.

Solution:

- Introduced concurrency limits.
- Controlled parallel requests.
- Reduced downstream pressure.
- Improved overall system stability.

---

# 13. Resilience4j Overview

Resilience4j is a lightweight fault-tolerance library.

It provides:

- Circuit Breaker.
- Retry.
- Rate Limiter.
- Bulkhead.
- Time Limiter.

---

## Spring Boot Integration

Example:

```java
@CircuitBreaker(
    name = "paymentService",
    fallbackMethod = "fallback"
)
public PaymentResponse pay() {
    return client.call();
}
```

---

Retry example:

```java
@Retry(name="inventoryService")
public Inventory getInventory() {
    return client.get();
}
```

---

Bulkhead example:

```java
@Bulkhead(name="vendorService")
public Response callVendor() {
    return client.call();
}
```

---

# 14. Combining Patterns

A typical production call may use:

```
Client Request
       |
Timeout
       |
Retry with Backoff + Jitter
       |
Circuit Breaker
       |
Bulkhead Limit
       |
External Dependency
```

---

The order and configuration must be chosen carefully.

For example:

- Retry only transient failures.
- Do not retry forever.
- Ensure retries do not bypass rate limits.

---

# 15. Observability of Resilience Patterns

Monitor:

Circuit Breaker:
- Open count.
- Failure rate.
- Slow calls.

Retries:
- Retry count.
- Success after retry.

Bulkheads:
- Queue size.
- Rejected requests.

Timeouts:
- Timeout frequency.

---

# 16. L6 Interview Soundbites

## Why are retries dangerous?

"Retries can amplify failures. During an outage, uncontrolled retries may create a retry storm that increases traffic and prevents the dependency from recovering."

---

## Why use a circuit breaker?

"A circuit breaker allows the system to fail fast when a dependency is unhealthy, protecting threads and reducing unnecessary pressure on downstream services."

---

## How do you protect an external API?

"I combine timeout limits, controlled concurrency, rate limiting, and circuit breakers. The goal is to make sure my service remains healthy even when the dependency is slow or unavailable."

---

## When do you use a bulkhead?

"I use bulkheads when different dependencies have different reliability characteristics. Isolating resources prevents one failing dependency from consuming all available threads or connections."

---

## What is the biggest mistake with resilience?

"Adding retries without understanding the failure mode. Retries can be beneficial for transient failures but harmful during overload scenarios."

---

# Key Takeaways

1. Failures are normal in distributed systems.

2. Timeouts prevent resources from being blocked indefinitely.

3. Retries should target transient failures and use backoff with jitter.

4. Circuit breakers fail fast and prevent cascading failures.

5. Bulkheads isolate failures.

6. Controlled concurrency protects downstream systems.

7. Rate limiting protects services from abuse.

8. Resilience4j provides production-ready implementations of these patterns.

9. Resilience patterns must be observable and tuned using real production metrics.
