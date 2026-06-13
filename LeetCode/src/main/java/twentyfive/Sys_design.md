# L6 Backend Engineering & System Design Handbook

## ⭐⭐⭐⭐⭐ Core System Design Foundations

* [01. System Design Fundamentals](#01-system-design-fundamentals)
* [02. Load Balancing](#02-load-balancing)
* [03. Caching](#03-caching)
* [04. Database Design & Storage](#04-database-design--storage)
* [05. Distributed Systems Fundamentals](#05-distributed-systems-fundamentals)
* [06. Consistent Hashing](#06-consistent-hashing)
* [07. Kafka & Event-Driven Architecture](#07-kafka--event-driven-architecture)
* [08. Traffic Control & Downstream Protection](#08-traffic-control--downstream-protection)
* [09. Reliability & Resilience Patterns](#09-reliability--resilience-patterns)
* [10. Communication Patterns](#10-communication-patterns)
* [11. Observability & Production Operations](#11-observability--production-operations)

---

## ⭐⭐⭐⭐⭐ API Engineering

* [12. API Design & Architecture](#12-api-design--architecture)

  * Part 1: REST Principles & Stateless Architecture
  * Part 2: Authentication, Authorization, OAuth, JWT & Security
  * Part 3: HTTP Methods, Idempotency, Error Handling & Versioning
  * Part 4: Pagination, Performance, Scalability & Production Readiness

---

## ⭐⭐⭐⭐⭐ Interview Execution

* [13. System Design Interview Framework](#13-system-design-interview-framework)

---

## ⭐⭐⭐ Lower Priority but Useful Topics

* [14. Search Systems](#14-search-systems)
* [15. CDN & Object Storage](#15-cdn--object-storage)
* [16. Machine Learning System Basics](#16-machine-learning-system-basics)

---

## ⭐⭐⭐⭐ Modern AI Systems

* [17. AI Architecture & LLM Systems](#17-ai-architecture--llm-systems)

  * LLM Architecture
  * Retrieval-Augmented Generation (RAG)
  * Embeddings & Vector Databases
  * Prompt Engineering
  * Model Serving
  * AI Agents
  * Context Management
  * Semantic Search
  * GPU Inference Pipelines
  * AI Caching Strategies
  * Evaluation, Monitoring & Guardrails

---

# Priority Ranking

## ⭐⭐⭐⭐⭐ Master Completely

* API Design
* Distributed Systems
* Database Design
* Caching
* Kafka & Event Processing
* Reliability & Resilience
* Traffic Control
* Observability
* System Design Interview Framework

---

## ⭐⭐⭐⭐ Very Important

* Load Balancing
* Consistent Hashing
* Communication Patterns
* AI Architecture

---

## ⭐⭐⭐ Useful but Lower Priority

* Search Systems
* CDN & Object Storage
* Machine Learning Fundamentals

---

# Recommended L6 Preparation Strategy

1. Master the core foundations.
2. Practice 10–15 complete system designs under time constraints.
3. Focus on explaining trade-offs rather than listing technologies.
4. Use production examples from your experience:

   * Kafka 50M client screening pipeline
   * Vendor API protection using rate limiting and controlled concurrency
   * Microservice resilience and observability
   * API design and compliance workflows

---

> **L6 engineering is not about knowing every technology. It is about making the right architectural decisions, understanding trade-offs, and communicating them clearly.**

# System Design Fundamentals

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

# Load Balancing and Reverse Proxy

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

# System Design - Caching

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

# System Design - Database Design and Storage

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

# Distributed Systems Fundamentals

---

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

# Traffic Control: Concurrency, Rate Limiting and Backpressure

---

# 1. Why Do We Need Traffic Control?

Large distributed systems often depend on downstream services:

- Databases
- External vendor APIs
- Internal microservices
- Message queues

Every dependency has a finite capacity.

Example:

A vendor API allows:

500 requests per second.

Our service suddenly receives:

5000 requests per second.

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


Traffic control protects downstream systems and keeps the overall system stable.

---

# 2. Controlled Concurrency

## The Problem

Even if the downstream does not have a rate limit, it can only process a certain number of requests simultaneously.

Example:

Vendor can handle:

100 concurrent connections.


If we create:

10000 threads

sending requests simultaneously, the downstream may fail.

---

## Solution: Limit Concurrent Requests

Use:

- Thread pools
- Semaphores
- Bounded queues


Example:

```
Incoming Requests
        |
 Request Queue
        |
 Thread Pool (100 threads)
        |
 Vendor API
```

Only 100 requests are active at a time.

Remaining requests wait in the queue.

---

## Thread Pool

A thread pool maintains a fixed number of worker threads.

Advantages:

- Prevents creating unlimited threads.
- Controls CPU and memory usage.
- Provides predictable throughput.


Example:

Java:

```java
ExecutorService executor =
    Executors.newFixedThreadPool(100);
```

---

## Semaphore

A semaphore controls access to a limited resource.

Example:

Vendor allows only 50 simultaneous calls.

```
Semaphore permits = 50
```

Before calling vendor:

- Acquire permit.
- Call API.
- Release permit.

---

Java:

```java
Semaphore semaphore = new Semaphore(50);

semaphore.acquire();

try {
    callVendor();
} finally {
    semaphore.release();
}
```

---

# 3. Rate Limiting

Concurrency controls how many requests are active at the same time.

Rate limiting controls how many requests are allowed over time.

Example:

Vendor policy:

Maximum:

500 requests per second.


Even if each request completes instantly, we should not exceed:

500 requests every second.

---

## Common Algorithms

### Fixed Window

Example:

500 requests per second.

Time window:

12:00:00 - 12:00:01

Allow:

500 requests.


Simple but has a burst problem.

Example:

500 requests at:

12:00:00.999

and another 500 at:

12:00:01.001


Effectively:

1000 requests within milliseconds.

---

### Sliding Window

Maintains a moving time window.

Example:

Always count the previous 1 second.

Advantages:

- More accurate.
- Prevents burst spikes.

Disadvantages:

- More memory and computation.

---

### Token Bucket

The most commonly used approach.

A bucket contains tokens.

Example:

Capacity:

500 tokens.

Refill:

500 tokens per second.

Each request consumes one token.


If tokens are available:

Request proceeds.

If empty:

- Reject request.
- Queue request.
- Delay request.


Advantages:

- Allows short bursts.
- Smooths traffic.

---

### Leaky Bucket

Requests enter a queue and leave at a constant rate.

Example:

```
Incoming Traffic
       |
    Bucket
       |
Constant Output Rate
```

Advantages:

- Very smooth traffic.

Disadvantages:

- Bursts are not allowed.

---

# 4. Backpressure

## What is Backpressure?

Backpressure means slowing down the producer when the consumer cannot keep up.

---

Example:

Kafka:

Producer:

100,000 messages/sec


Consumer:

10,000 messages/sec


Without control:

Consumer lag grows indefinitely.

---

# Backpressure Strategies

## 1. Bounded Queue

Do not allow infinite memory growth.

Example:

```
Queue capacity = 10,000 messages
```

When full:

- Reject new requests.
- Slow producers.
- Apply retry policies.

---

## 2. Return Errors

Example:

HTTP:

```
429 Too Many Requests
```

Client can retry later.

---

## 3. Slow Down Producers

Examples:

- Reduce Kafka producer rate.
- Apply flow control.
- Use rate limiters.

---

## 4. Scale Consumers

Example:

Kafka:

Increase consumer instances.

```
Partition 1 -> Consumer A
Partition 2 -> Consumer B
Partition 3 -> Consumer C
```

---

# 5. Concurrency vs Rate Limiting vs Backpressure

| Concept | Controls | Example |
|---|---|---|
| Concurrency Control | Number of active requests | 100 threads calling vendor |
| Rate Limiting | Requests per unit time | 500 requests/sec |
| Backpressure | System overload | Queue full, return 429 |

---

# 6. Real World Example: External Vendor Protection

Scenario:

AML screening system calls a third-party vendor.

Vendor limits:

- 500 requests/second
- 100 concurrent connections


Solution:

```
Incoming Requests
        |
 Rate Limiter (500/sec)
        |
 Bounded Queue
        |
 Thread Pool / Semaphore (100 concurrent)
        |
 Vendor API
```

---

Benefits:

- Vendor is protected.
- Latency remains predictable.
- Threads do not grow infinitely.
- Failures are isolated.

---

# 7. Failure Scenarios

## What if the vendor becomes slow?

Problem:

Threads become blocked waiting for responses.

Solutions:

- Timeouts
- Circuit breakers
- Reduce concurrency
- Fail fast

---

## What if traffic suddenly spikes?

Solutions:

- Token bucket allows short bursts.
- Queue requests temporarily.
- Return 429 when capacity is exceeded.

---

## What if retries create more load?

This creates a retry storm.

Solutions:

- Exponential backoff.
- Jitter.
- Retry limits.
- Circuit breakers.

---

# 8. L6 Interview Discussion

## Why not simply increase the thread pool?

More threads increase:

- CPU context switching.
- Memory usage.
- Downstream pressure.

The bottleneck usually moves to the dependency.

---

## Thread Pool vs Semaphore

Thread Pool:

Controls how many workers execute tasks.

Good for:
- Processing internal tasks.

Semaphore:

Controls access to an external resource.

Good for:
- Limiting API calls.
- Database connections.

They can be used together.

---

## Which rate limiter would you choose?

Token Bucket.

Because:

- It handles steady traffic.
- Allows controlled bursts.
- Simple to implement.

---

## How do you protect a slow dependency?

Use a combination of:

- Timeouts.
- Controlled concurrency.
- Rate limiting.
- Circuit breakers.
- Backpressure.

---

# Key Takeaways

1. Downstream systems have limited capacity.

2. Controlled concurrency limits active requests.

3. Rate limiting controls requests over time.

4. Token bucket is the most common rate limiting algorithm.

5. Backpressure prevents overloaded systems from collapsing.

6. Thread pools and semaphores are complementary.

7. Good systems fail gracefully instead of allowing cascading failures.

# Traffic Control: Concurrency, Rate Limiting and Backpressure

---

# 1. Why Do We Need Traffic Control?

Large distributed systems often depend on downstream services:

- Databases
- External vendor APIs
- Internal microservices
- Message queues

Every dependency has a finite capacity.

Example:

A vendor API allows:

500 requests per second.

Our service suddenly receives:

5000 requests per second.

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


Traffic control protects downstream systems and keeps the overall system stable.

---

# 2. Controlled Concurrency

## The Problem

Even if the downstream does not have a rate limit, it can only process a certain number of requests simultaneously.

Example:

Vendor can handle:

100 concurrent connections.


If we create:

10000 threads

sending requests simultaneously, the downstream may fail.

---

## Solution: Limit Concurrent Requests

Use:

- Thread pools
- Semaphores
- Bounded queues


Example:

```
Incoming Requests
        |
 Request Queue
        |
 Thread Pool (100 threads)
        |
 Vendor API
```

Only 100 requests are active at a time.

Remaining requests wait in the queue.

---

## Thread Pool

A thread pool maintains a fixed number of worker threads.

Advantages:

- Prevents creating unlimited threads.
- Controls CPU and memory usage.
- Provides predictable throughput.


Example:

Java:

```java
ExecutorService executor =
    Executors.newFixedThreadPool(100);
```

---

## Semaphore

A semaphore controls access to a limited resource.

Example:

Vendor allows only 50 simultaneous calls.

```
Semaphore permits = 50
```

Before calling vendor:

- Acquire permit.
- Call API.
- Release permit.

---

Java:

```java
Semaphore semaphore = new Semaphore(50);

semaphore.acquire();

try {
    callVendor();
} finally {
    semaphore.release();
}
```

---

# 3. Rate Limiting

Concurrency controls how many requests are active at the same time.

Rate limiting controls how many requests are allowed over time.

Example:

Vendor policy:

Maximum:

500 requests per second.


Even if each request completes instantly, we should not exceed:

500 requests every second.

---

## Common Algorithms

### Fixed Window

Example:

500 requests per second.

Time window:

12:00:00 - 12:00:01

Allow:

500 requests.


Simple but has a burst problem.

Example:

500 requests at:

12:00:00.999

and another 500 at:

12:00:01.001


Effectively:

1000 requests within milliseconds.

---

### Sliding Window

Maintains a moving time window.

Example:

Always count the previous 1 second.

Advantages:

- More accurate.
- Prevents burst spikes.

Disadvantages:

- More memory and computation.

---

### Token Bucket

The most commonly used approach.

A bucket contains tokens.

Example:

Capacity:

500 tokens.

Refill:

500 tokens per second.

Each request consumes one token.


If tokens are available:

Request proceeds.

If empty:

- Reject request.
- Queue request.
- Delay request.


Advantages:

- Allows short bursts.
- Smooths traffic.

---

### Leaky Bucket

Requests enter a queue and leave at a constant rate.

Example:

```
Incoming Traffic
       |
    Bucket
       |
Constant Output Rate
```

Advantages:

- Very smooth traffic.

Disadvantages:

- Bursts are not allowed.

---

# 4. Backpressure

## What is Backpressure?

Backpressure means slowing down the producer when the consumer cannot keep up.

---

Example:

Kafka:

Producer:

100,000 messages/sec


Consumer:

10,000 messages/sec


Without control:

Consumer lag grows indefinitely.

---

# Backpressure Strategies

## 1. Bounded Queue

Do not allow infinite memory growth.

Example:

```
Queue capacity = 10,000 messages
```

When full:

- Reject new requests.
- Slow producers.
- Apply retry policies.

---

## 2. Return Errors

Example:

HTTP:

```
429 Too Many Requests
```

Client can retry later.

---

## 3. Slow Down Producers

Examples:

- Reduce Kafka producer rate.
- Apply flow control.
- Use rate limiters.

---

## 4. Scale Consumers

Example:

Kafka:

Increase consumer instances.

```
Partition 1 -> Consumer A
Partition 2 -> Consumer B
Partition 3 -> Consumer C
```

---

# 5. Concurrency vs Rate Limiting vs Backpressure

| Concept | Controls | Example |
|---|---|---|
| Concurrency Control | Number of active requests | 100 threads calling vendor |
| Rate Limiting | Requests per unit time | 500 requests/sec |
| Backpressure | System overload | Queue full, return 429 |

---

# 6. Real World Example: External Vendor Protection

Scenario:

AML screening system calls a third-party vendor.

Vendor limits:

- 500 requests/second
- 100 concurrent connections


Solution:

```
Incoming Requests
        |
 Rate Limiter (500/sec)
        |
 Bounded Queue
        |
 Thread Pool / Semaphore (100 concurrent)
        |
 Vendor API
```

---

Benefits:

- Vendor is protected.
- Latency remains predictable.
- Threads do not grow infinitely.
- Failures are isolated.

---

# 7. Failure Scenarios

## What if the vendor becomes slow?

Problem:

Threads become blocked waiting for responses.

Solutions:

- Timeouts
- Circuit breakers
- Reduce concurrency
- Fail fast

---

## What if traffic suddenly spikes?

Solutions:

- Token bucket allows short bursts.
- Queue requests temporarily.
- Return 429 when capacity is exceeded.

---

## What if retries create more load?

This creates a retry storm.

Solutions:

- Exponential backoff.
- Jitter.
- Retry limits.
- Circuit breakers.

---

# 8. L6 Interview Discussion

## Why not simply increase the thread pool?

More threads increase:

- CPU context switching.
- Memory usage.
- Downstream pressure.

The bottleneck usually moves to the dependency.

---

## Thread Pool vs Semaphore

Thread Pool:

Controls how many workers execute tasks.

Good for:
- Processing internal tasks.

Semaphore:

Controls access to an external resource.

Good for:
- Limiting API calls.
- Database connections.

They can be used together.

---

## Which rate limiter would you choose?

Token Bucket.

Because:

- It handles steady traffic.
- Allows controlled bursts.
- Simple to implement.

---

## How do you protect a slow dependency?

Use a combination of:

- Timeouts.
- Controlled concurrency.
- Rate limiting.
- Circuit breakers.
- Backpressure.

---

# Key Takeaways

1. Downstream systems have limited capacity.

2. Controlled concurrency limits active requests.

3. Rate limiting controls requests over time.

4. Token bucket is the most common rate limiting algorithm.

5. Backpressure prevents overloaded systems from collapsing.

6. Thread pools and semaphores are complementary.

7. Good systems fail gracefully instead of allowing cascading failures.

# Search Systems (Lower Priority)

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

# CDN and Object Storage (Lower Priority)

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

# System Design Interview Framework (High Priority)

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

# API Design - Part 1
# REST Principles, Resource Modeling, and Statelessness

---

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

# AI Systems Design Handbook 

---

# Chapter 14 - AI Fundamentals

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
