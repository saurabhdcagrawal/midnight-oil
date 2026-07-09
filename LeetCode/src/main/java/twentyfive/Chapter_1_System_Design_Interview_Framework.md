# Audible Senior Backend Interview Playbook

# Chapter 1 -- System Design Interview Framework

------------------------------------------------------------------------

## 1. What is the interviewer evaluating?

A system design interview is **not** about producing the perfect
architecture.

The interviewer wants to evaluate whether you think like a senior
engineer.

They are looking for your ability to:

-   Handle ambiguity
-   Gather requirements before proposing a solution
-   Break large problems into smaller components
-   Make reasonable assumptions
-   Justify technical decisions
-   Discuss trade-offs
-   Design scalable and reliable systems
-   Communicate clearly

A good interview is a structured engineering discussion rather than a
race to draw boxes.

------------------------------------------------------------------------

## 2. The Biggest Mistake

Most candidates hear:

> Design WhatsApp.

and immediately start drawing architecture.

``` text
Client
↓

Load Balancer

↓

Kafka

↓

Redis

↓

Database
```

The interviewer has no idea **why** you selected those components.

Instead, let the requirements drive the architecture.

Think of system design like building a house.

You don't begin by choosing the windows.

You first understand:

-   Who will use it?
-   What problem are you solving?
-   How large is it?
-   What constraints exist?

Only then should you begin designing.

------------------------------------------------------------------------

## 3. The Interview Roadmap

Use the same framework for every design problem.

``` text
Clarify
    ↓
Requirements
    ↓
Scale
    ↓
API Design
    ↓
Data Model
    ↓
High-Level Architecture
    ↓
Deep Dive
    ↓
Scalability
    ↓
Reliability
    ↓
Trade-offs
    ↓
Future Improvements
```

This roadmap keeps the conversation organized and ensures you never
wonder what comes next.

------------------------------------------------------------------------

## 4. Step 1 -- Clarify the Problem

Never begin drawing immediately.

Spend the first few minutes asking questions.

Example (Design a Notification Service):

-   What notification channels are supported?
-   Is real-time delivery required?
-   Should retries be supported?
-   Are duplicate notifications acceptable?
-   Should users manage notification preferences?
-   How many notifications are expected per day?

Clarifying questions reduce ambiguity and demonstrate strong engineering
habits.

------------------------------------------------------------------------

## 5. Step 2 -- Define Requirements

Separate requirements into two categories.

### Functional Requirements

Describe what the system should do.

Example:

-   Send notifications
-   Retry failed deliveries
-   Track delivery status
-   Manage user preferences

### Non-Functional Requirements

Describe how the system should behave.

Typical examples:

-   High availability
-   Low latency
-   Scalability
-   Fault tolerance
-   Durability
-   Security
-   Strong or eventual consistency

------------------------------------------------------------------------

## 6. Step 3 -- Estimate Scale

Estimate enough to justify architectural choices.

Consider:

-   Daily active users
-   Requests per second
-   Read/write ratio
-   Storage growth
-   Network bandwidth

Example:

-   10 million users
-   100 million requests/day
-   80% reads
-   20% writes
-   5 TB/year

The goal is not perfect arithmetic but sound engineering reasoning.

------------------------------------------------------------------------

## 7. Step 4 -- Design APIs

Define the important external interfaces.

``` http
POST /notifications

GET /notifications/{id}

DELETE /notifications/{id}
```

Only define the APIs that are essential to the design.

------------------------------------------------------------------------

## 8. Step 5 -- Data Model

Identify the primary entities.

Example:

``` text
User

Notification

Template

DeliveryStatus
```

Focus on the entities that influence the architecture.

------------------------------------------------------------------------

## 9. Step 6 -- High-Level Architecture

Now begin drawing.

Start with the major components.

``` text
Client

↓

API Gateway

↓

Notification Service

↓

Message Queue

↓

Workers

↓

Email / SMS Providers

↓

Database
```

Explain the request flow from beginning to end before discussing
implementation details.

------------------------------------------------------------------------

## 10. Step 7 -- Deep Dive

Choose the most interesting components.

Possible topics:

-   Message Queue
-   Retry Strategy
-   Idempotency
-   Cache
-   Database Choice
-   Partitioning
-   Replication
-   Search

Avoid deep diving into every component.

------------------------------------------------------------------------

## 11. Step 8 -- Scalability & Reliability

Discuss how the system behaves under load.

Topics include:

-   Horizontal Scaling
-   Read Replicas
-   Database Sharding
-   Rate Limiting
-   Retries
-   Circuit Breakers
-   Dead Letter Queues
-   Monitoring
-   Auto Scaling

------------------------------------------------------------------------

## 12. Step 9 -- Trade-offs

Every design decision has advantages and disadvantages.

Example:

### Message Queue

Advantages

-   Decouples services
-   Improves scalability
-   Handles traffic spikes

Disadvantages

-   Eventual consistency
-   Operational complexity

The interviewer is often more interested in your reasoning than your
technology choices.

------------------------------------------------------------------------

## 13. Step 10 -- Future Improvements

End by discussing how the system could evolve.

Examples:

-   Multi-region deployment
-   Read replicas
-   Database sharding
-   Better caching
-   Search indexing
-   Disaster recovery
-   Machine learning enhancements

------------------------------------------------------------------------

# Key Takeaway

When presented with any design problem, think:

1.  Clarify requirements.
2.  Define functional and non-functional requirements.
3.  Estimate scale.
4.  Design APIs.
5.  Design the data model.
6.  Draw the high-level architecture.
7.  Deep dive into important components.
8.  Discuss scalability and reliability.
9.  Explain trade-offs.
10. Suggest future improvements.

Following this framework consistently is what makes senior engineers
appear organized and confident during system design interviews.

------------------------------------------------------------------------

# Next Chapter

The next chapter covers the complete request lifecycle:

**Client → DNS → Load Balancer → API Gateway → Services → Cache →
Database → Message Queue**

Understanding this journey makes every subsequent system design topic
much easier to reason about.


# Chapter 2 – The Request Lifecycle

---

# Why This Chapter Matters

Every backend system, regardless of whether you're designing WhatsApp, Uber, Dropbox, or Amazon, follows the same fundamental request lifecycle.

Instead of memorizing technologies individually, first understand **how a request travels through the system.**

Once you understand this journey, every building block (Redis, Kafka, SQL, Load Balancer, API Gateway, etc.) naturally falls into place.

Think of this chapter as learning the anatomy of a distributed system.

---

# The Complete Request Journey

```
                 User
                   │
                   ▼
              Mobile/Web App
                   │
                   ▼
                 DNS Lookup
                   │
                   ▼
             Load Balancer
                   │
                   ▼
              API Gateway
                   │
                   ▼
          Authentication Service
                   │
                   ▼
          Business Service Layer
          (Order, Payment, User...)
           │              │
           │              │
           ▼              ▼
         Redis         Message Queue
           │              │
           ▼              ▼
        Database       Background Workers
                           │
                           ▼
                     Email/SMS/Push
```

Every major backend architecture is some variation of this diagram.

---

# Step 1 – Client

The request begins from a client.

Examples:

- Mobile App
- Web Browser
- Smart TV
- IoT Device
- Another Backend Service

Example:

```
POST /orders
```

The client doesn't know where the server is.

It only knows a domain name.

Example:

```
api.amazon.com
```

---

# Step 2 – DNS

## What is DNS?

DNS (Domain Name System) converts a domain name into an IP address.

Instead of remembering:

```
54.183.25.9
```

users type

```
amazon.com
```

DNS translates that into an IP address.

---

## Why do we need DNS?

Because IP addresses change.

Servers move.

Cloud infrastructure changes.

The domain remains the same.

---

## Interview Takeaway

DNS is **not** involved in business logic.

Its job is simply:

```
Domain Name

↓

IP Address
```

---

# Step 3 – Load Balancer

Now the request reaches the Load Balancer.

```
           Client

              │

              ▼

        Load Balancer

         │        │

         ▼        ▼

     Server A   Server B

```

---

## Why do we need it?

Without one:

```
1000 requests

↓

One server

↓

Crash
```

With one:

```
1000 requests

↓

Load Balancer

↓

500

↓

Server A

+

500

↓

Server B
```

The load balancer distributes traffic.

---

## Responsibilities

- Distribute requests
- Remove unhealthy servers
- Improve availability
- Enable horizontal scaling

---

## Common Algorithms

- Round Robin
- Least Connections
- Weighted Round Robin
- IP Hash

Don't memorize all of them.

Know why they exist.

---

# Step 4 – API Gateway

After the Load Balancer comes the API Gateway.

```
Client

↓

Load Balancer

↓

API Gateway

↓

Microservices
```

---

## Why not call services directly?

Imagine 30 microservices.

The mobile app would need to know:

- User Service
- Order Service
- Payment Service
- Search Service
- Notification Service

This creates tight coupling.

Instead

```
Client

↓

Gateway

↓

Everything else
```

---

## Responsibilities

- Authentication
- Authorization
- Routing
- Rate Limiting
- Logging
- Request Aggregation
- API Versioning

Think of it as the front door of your backend.

---

# Step 5 – Authentication

Before reaching business logic, the system usually verifies identity.

Example:

```
JWT Token

↓

Validate

↓

Continue
```

If authentication fails:

```
401 Unauthorized
```

Business logic is never executed.

---

# Step 6 – Business Service

Now the request reaches the service responsible for the business problem.

Examples:

- Order Service
- Payment Service
- Cart Service
- User Service
- Inventory Service

Example:

```
POST /orders
```

The Order Service may need to:

- Validate inventory
- Calculate total
- Reserve stock
- Save order
- Publish event

This is where business rules live.

---

# Step 7 – Cache (Redis)

Before reading the database:

```
Service

↓

Redis
```

If data exists:

```
Return immediately.
```

Otherwise:

```
Redis Miss

↓

Database

↓

Store in Redis

↓

Return Response
```

This is called the **Cache-Aside Pattern**.
Note that storing in redis is in critical path. it is done synchronously before returning response back to the client

---

## Why cache?

Because memory is much faster than disk.

Typical latency:

| Storage | Approx Latency |
|----------|---------------:|
| CPU Cache | Nanoseconds |
| RAM (Redis) | Microseconds |
| SSD Database | Milliseconds |

Millions of reads become much cheaper.

---

# Step 8 – Database

Eventually, persistent storage is required.

Examples:

- PostgreSQL
- MySQL
- DynamoDB
- Cassandra
- MongoDB

The database stores durable business data.

Examples:

- Orders
- Users
- Products
- Payments

---

# Step 9 – Message Queue

Some work should not happen while the user waits.

Example:

Customer places an order.

Should we wait while:

- Email is sent?
- SMS is sent?
- Analytics updated?
- Recommendations refreshed?

No.

Instead:

```
Order Created

↓

Publish Event

↓

Kafka / SQS

↓

Background Workers
```

The user receives a response immediately.

Everything else happens asynchronously.

---

# Step 10 – Background Workers

Workers consume messages.

Examples:

- Send Email
- Send SMS
- Push Notification
- Generate Invoice
- Update Analytics

Workers improve scalability because they process tasks independently of user requests.

---

# Synchronous vs Asynchronous

## Synchronous

```
Client

↓

Server

↓

Database

↓

Response
```

The client waits.

---

## Asynchronous

```
Client

↓

Server

↓

Queue

↓

Response

Later...

↓

Worker

↓

Email
```

The client does **not** wait.

---

# Where Do Common Technologies Fit?

| Component | Common Technologies |
|------------|---------------------|
| DNS | Route53, Cloud DNS |
| Load Balancer | ALB, NLB, Nginx, HAProxy |
| API Gateway | Kong, AWS API Gateway, Apigee |
| Cache | Redis, Memcached |
| Database | PostgreSQL, MySQL, MongoDB, Cassandra |
| Message Queue | Kafka, RabbitMQ, Amazon SQS |
| Search | Elasticsearch |
| Object Storage | Amazon S3 |

Don't memorize products.

Understand the role each component plays.

---

# Complete Request Flow Example

Customer places an order.

```
Client

↓

DNS

↓

Load Balancer

↓

API Gateway

↓

Authentication

↓

Order Service

↓

Redis (Cache Miss)

↓

Database

↓

Save Order

↓

Publish Event

↓

Kafka

↓

Return Success Response

↓

Email Worker

↓

Send Confirmation Email
```

Notice something important.

The customer receives the response **before** the email is sent.

That is a hallmark of scalable backend systems.

---

# Interview Tips

When explaining a design:

1. Walk through the request from beginning to end.
2. Explain why each component exists.
3. Introduce new technologies only when they solve a problem.
4. Discuss trade-offs.
5. Keep the explanation structured.

A good interviewer should always know where the request is and what happens next.

---

# Key Takeaways

- Every distributed system follows a request lifecycle.
- Requirements determine which components are needed.
- Load Balancers improve availability and scalability.
- API Gateways centralize cross-cutting concerns.
- Business services implement domain logic.
- Redis reduces database load.
- Databases provide durable storage.
- Message queues enable asynchronous processing.
- Background workers handle long-running tasks.
- Explain the request flow before discussing optimizations.

---

# Next Chapter

**Chapter 3 – Databases**

We'll cover:

- SQL vs NoSQL
- Query-driven schema design
- Normalization vs Denormalization
- Indexing
- Replication
- Read Replicas
- Sharding
- Partitioning
- Transactions
- Locking

# Chapter 3 – Database Design

---

# Why This Chapter Matters

The database is the heart of almost every backend system.

No matter what system you're designing:

- Uber
- WhatsApp
- Amazon
- Dropbox
- Netflix
- Ticketmaster

Eventually you'll have to answer:

> **Where does the data live?**

Choosing the right database is one of the most important architectural decisions.

---

# The First Rule

Never start by saying

> "I'll use MongoDB."

or

> "I'll use PostgreSQL."

Instead ask yourself

> **What problem am I trying to solve?**

The requirements determine the database.

---

# Types of Databases

Broadly speaking there are two categories.

```
                Databases

           /                 \

        SQL                 NoSQL

```

Both are excellent.

They solve different problems.

---

# SQL Databases

Examples

- PostgreSQL
- MySQL
- Oracle
- SQL Server

SQL databases store data in tables.

Example

```
Users

+---------+---------+

| Id | Name |

+---------+---------+

| 1 | John |

| 2 | Alice |

+---------+---------+
```

Relationships between tables are first-class citizens.

---

# Why Choose SQL?

Use SQL when you need

- Transactions
- ACID guarantees
- Strong consistency
- Complex joins
- Referential integrity
- Structured data

Examples

- Banking
- Payments
- Orders
- Inventory
- Payroll

---

# SQL Strengths

Advantages

- ACID transactions
- Mature ecosystem
- Rich querying
- Joins
- Referential integrity
- Excellent reporting

---

# SQL Weaknesses

Disadvantages

- Harder horizontal scaling
- Schema migrations
- Joins become expensive at massive scale

---

# NoSQL Databases

Examples

- MongoDB
- Cassandra
- DynamoDB
- Couchbase

NoSQL databases usually store documents or key-value pairs.

Example

```
{

"userId":123,

"name":"John",

"orders":[...]

}
```

The entire object can be stored together.

---

# Why Choose NoSQL?

Use NoSQL when

- Massive scale
- Flexible schema
- Huge write throughput
- Horizontal scaling
- Denormalized data

Examples

- Social Media
- Activity Feed
- Logging
- IoT
- Analytics

---

# SQL vs NoSQL

| SQL | NoSQL |
|------|--------|
| Fixed Schema | Flexible Schema |
| ACID | BASE/Eventual Consistency |
| Joins | Denormalized |
| Strong Consistency | Eventual Consistency |
| Vertical Scaling | Horizontal Scaling |
| Rich Queries | Simpler Queries |

---

# Query-Driven Schema Design

This is one of the most important concepts in modern backend engineering.

Traditional database design starts with entities.

```
Customer

Order

Product

Invoice
```

Modern distributed systems often start with

> **How will the application query the data?**

Example

Suppose every request asks

```
Show me all notifications for User X
```

Instead of normalizing everything,

store notifications partitioned by User ID.

Now one query returns everything.

This reduces joins and improves performance.

Always design for the access patterns.

---

# Normalization

Normalization reduces redundancy.

Example

Instead of

```
Order

CustomerName

CustomerAddress
```

Store

```
Customer

Order

```

linked by CustomerId.

Advantages

- No duplicated data
- Easier updates
- Better consistency

Disadvantages

- More joins

---

# Denormalization

Denormalization intentionally duplicates data.

Example

```
Order

CustomerName

CustomerEmail

CustomerPhone
```

Why?

Because every order page needs customer details.

Advantages

- Faster reads
- Fewer joins
- Better performance

Disadvantages

- Duplicate data
- Harder updates

Modern distributed systems often favor denormalization.

---

# Indexes

Without an index

```
Find User

↓

Scan every row
```

With an index

```
Find User

↓

Jump directly to location
```

Indexes dramatically improve read performance.

---

# Primary Index

Every table has one primary key.

Example

```
UserId
```

Searching by primary key is extremely fast.

---

# Secondary Index

Suppose users search by email.

Without an index

```
Full table scan
```

With an index

```
Email Index

↓

User
```

Much faster.

---

# Composite Index

Suppose this query happens frequently.

```
WHERE UserId = ?

AND Status='ACTIVE'
```

Instead of two indexes

Create one

```
(UserId, Status)
```

This avoids unnecessary scans.

---

# Covering Index

Sometimes the index contains every column required by the query.

Then the database never needs to access the table.

This is called a Covering Index.

Very efficient for read-heavy systems.

---

# Replication

One database receives writes.

Other databases copy the data.

```
          Primary

          /     \

Replica   Replica
```

Benefits

- Higher availability
- More read throughput
- Disaster recovery

---

# Replication Lag

Replication is not instantaneous.

Example

```
Write

↓

Primary

↓

(200ms)

↓

Replica
```

If a user immediately reads from the replica,

they may see stale data.

This is replication lag.

---

# Read Replicas

Read-heavy systems often use replicas.

```
Writes

↓

Primary

Reads

↓

Replica A

Replica B

Replica C
```

Benefits

- Better scalability
- Lower load

Trade-off

Eventual consistency.

---

# Sharding

Sometimes one database is not enough.

Split data across multiple databases.

Example

```
UserId

1-1M

↓

Shard 1

1M-2M

↓

Shard 2

2M-3M

↓

Shard 3
```

Each shard stores only part of the data.

Benefits

- Massive horizontal scaling

Challenges

- Cross-shard joins
- Rebalancing
- Operational complexity

---

# Partitioning vs Sharding

Partitioning

One database server.

Multiple partitions.

Sharding

Multiple database servers.

Each stores part of the data.

Think of sharding as distributed partitioning.

---

# Transactions

A transaction groups multiple operations.

Either

Everything succeeds

or

Everything rolls back.

Example

Transfer money.

```
Debit Account A

Credit Account B
```

You cannot allow only one operation.

Transactions ensure consistency.

---

# Optimistic Locking

Assume conflicts are rare.

Store

```
Version Number
```

When updating

```
UPDATE

WHERE Version = 5
```

If another transaction already changed it,

the update fails.

Very scalable.

---

# Pessimistic Locking

Lock the row before modifying it.

```
SELECT ...

FOR UPDATE
```

Other transactions wait.

Safer.

Lower concurrency.

---

# Choosing the Right Database

Ask yourself

1. Do I need ACID?

↓

Yes

↓

SQL

---

Need massive horizontal scale?

↓

Yes

↓

NoSQL

---

Read heavy?

↓

Add Cache

↓

Read Replicas

---

Write heavy?

↓

Sharding

↓

Partitioning

---

# Interview Tips

Don't say

> "I'll use MongoDB."

Say

> "Since the workload is read-heavy with flexible document structures and we don't require complex joins or multi-row transactions, a document database is a good fit."

Always justify the choice.

---

# Key Takeaways

- Requirements drive database selection.
- SQL provides strong consistency and transactions.
- NoSQL favors scalability and flexibility.
- Design schemas around query patterns.
- Normalize for consistency.
- Denormalize for performance.
- Indexes accelerate reads.
- Replication improves availability.
- Read replicas scale reads.
- Sharding scales writes.
- Understand the trade-offs before choosing a database.

---

# Next Chapter

**Caching**

We'll answer:

- Why Redis?
- Why not cache everything?
- Cache-Aside
- Write-Through
- Write-Behind
- Cache Invalidation
- TTL
- Distributed Caching
- Hot Keys
- Cache Stampede


# Chapter 4 – Caching

---

# Why This Chapter Matters

Databases are relatively slow.

Even a well-indexed SQL query takes milliseconds.

Memory access takes microseconds.

That difference becomes enormous when your system handles millions of requests.

Caching exists to avoid repeatedly asking the database for the same information.

Think of caching as:

> **"Remembering the answer instead of solving the same problem repeatedly."**

---

# What is a Cache?

A cache is a temporary, high-speed storage layer that stores frequently accessed data.

Instead of:

```
Client

↓

Service

↓

Database
```

We insert a cache.

```
Client

↓

Service

↓

Cache

↓

Database
```

Now the service checks memory first.

---

# Why Do We Need Caching?

Imagine one product page receives

```
100,000 requests/minute
```

Without a cache

```
100,000 database queries
```

The database becomes the bottleneck.

With a cache

```
100,000 cache reads

↓

500 database reads
```

The database load drops dramatically.

---

# Cache Hit vs Cache Miss

## Cache Hit

The requested data already exists.

```
Service

↓

Redis

↓

Return Data
```

The database is never contacted.

---

## Cache Miss

The requested data is absent.

```
Service

↓

Redis

↓

Miss

↓

Database

↓

Redis

↓

Return Response
```

The cache is updated before returning.

---

# Why Redis?

Redis is an in-memory key-value store.

Unlike SQL databases, Redis stores data entirely in RAM.

Memory access is significantly faster than disk access.

Advantages

- Extremely fast
- Simple key-value lookup
- Supports expiration
- Distributed
- Highly scalable
- Rich data structures

---

# Why Not Store Everything in Redis?

Memory is expensive.

Example

```
Redis RAM

64 GB

Database Storage

20 TB
```

Keeping every record in memory is usually impractical.

Cache only the data that is frequently accessed.

---

# Cache-Aside Pattern (Lazy Loading)

This is the most common caching strategy.

Flow

```
Service

↓

Redis

↓

Hit?

↓

Yes

↓

Return

↓

No

↓

Database

↓

Store in Redis

↓

Return
```

Advantages

- Simple
- Efficient
- Loads data only when needed

Disadvantages

- First request is slower
- Possible cache misses

Most interview questions assume this pattern.

---

# Write-Through Cache

Every write updates both Redis and the database.

```
Write

↓

Redis

↓

Database
```

Advantages

- Cache is always current
- Faster future reads

Disadvantages

- Higher write latency
- Extra memory usage

---

# Write-Behind (Write-Back)

Writes go to Redis first.

Database updates later.

```
Write

↓

Redis

↓

Queue

↓

Database
```

Advantages

- Very fast writes
- Good for analytics

Disadvantages

- Risk of data loss
- More operational complexity

---

# Read-Through Cache

The application never talks directly to the database.

```
Application

↓

Cache

↓

Database
```

The cache automatically loads missing data.

Less common in interviews but worth knowing.

---

# TTL (Time-To-Live)

Cached data should not live forever.

Example

```
Product Price

TTL = 10 minutes
```

After expiration

```
Redis removes it automatically.
```

TTL prevents stale data from remaining indefinitely.

---

# Cache Invalidation

The hardest problem in caching is not storing data.

It is removing outdated data.

Example

```
User changes profile picture.
```

Should Redis still return the old image?

No.

The cache must be updated or removed.

---

## Common Cache Invalidation Strategies

### Time-Based

```
Expire after 10 minutes.
```

Simple.

May serve stale data.

---

### Write Invalidation

Whenever data changes

```
Update Database

↓

Delete Redis Key
```

Next read reloads fresh data.

Very common.

---

### Write Update

Update both

```
Database

+

Redis
```

Useful when stale reads are unacceptable.

---

# Distributed Cache

Large systems often run multiple Redis servers.

```
           Redis Cluster

        /       |       \

Node A  Node B  Node C
```

Data is distributed across nodes.

Benefits

- Horizontal scaling
- Higher availability
- Better throughput

---

# Cache Eviction

Memory is limited.

Redis eventually removes data.

Common policies

### LRU

Least Recently Used

Remove the oldest unused data.

Most common.

---

### LFU

Least Frequently Used

Remove rarely accessed data.

Useful for recommendation systems.

---

### FIFO

First In First Out

Oldest entries leave first.

Simple.

---

# Cache Stampede

Suppose

```
One popular product expires.
```

Suddenly

```
10,000 requests

↓

Database
```

The database becomes overloaded.

This is called a Cache Stampede.

---

## Solutions

- Randomized TTL
- Request Coalescing
- Distributed Locks
- Background Refresh

---

# Hot Keys

Sometimes one key becomes extremely popular.

Example

```
Product #123
```

Millions of requests hit the same Redis node.

One node becomes overloaded.

Solutions

- Replicate hot keys
- Partition traffic
- Local application cache

---

# When Should You Cache?

Good candidates

- Product Catalog
- User Profiles
- Session Data
- Configuration
- Search Results
- Frequently Accessed Metadata

Poor candidates

- Rapidly changing financial transactions
- Real-time account balances
- Highly sensitive data requiring strict consistency

---

# Where Does Redis Fit?

```
Client

↓

Load Balancer

↓

API Gateway

↓

Service

↓

Redis

↓

Database
```

Redis sits between the application and the database.

---

# Interview Example

Question

```
Database CPU reaches 100%.

What do you do?
```

Strong answer

- Identify read-heavy workload
- Introduce Redis
- Use Cache-Aside
- Add TTL
- Monitor cache hit ratio
- Add read replicas if necessary

Notice

Caching is not the only solution.

It is one tool among many.

---

# Interview Tips

Don't say

> "I'll add Redis."

Say

> "Most requests repeatedly read the same relatively static data. Introducing Redis using the Cache-Aside pattern reduces database load while keeping the implementation simple."

Always explain **why**.

---

# Key Takeaways

- Cache reduces database load.
- Redis is memory-based and extremely fast.
- Cache Hit avoids database access.
- Cache Miss loads data from the database.
- Cache-Aside is the most common strategy.
- TTL prevents indefinitely stale data.
- Cache invalidation keeps data consistent.
- Distributed caches scale horizontally.
- Watch for cache stampedes and hot keys.
- Caching improves performance but introduces consistency trade-offs.

---

# Appendix – Cache Stampede (Thundering Herd Problem)

---

# Why This Matters

One of the most common follow-up questions in a System Design interview is:

> **"What happens if your cache expires?"**

If you simply answer:

> "The application reads from the database."

The interviewer will usually ask:

> **"What if 10,000 users request the same data at exactly the same time?"**

This scenario is known as a **Cache Stampede** (also called the **Thundering Herd Problem**).

Understanding this problem and its solutions demonstrates production-level system design knowledge.

---

# Normal Request Flow

Suppose a popular product is already cached.

```
Redis

product:123

↓

iPhone 17
```

Now imagine 10,000 users request the product.

```
10,000 Requests

↓

Redis

↓

Return Product
```

Notice something important.

The database is never contacted.

Redis serves every request.

The system performs extremely well.

---

# Cache Expiration

Assume the cache entry has a TTL (Time-To-Live).

```
TTL = 10 minutes
```

At exactly 10:00 AM

Redis removes the key.

```
Redis

product:123

❌ Missing
```

The cache is now empty.

---

# What Happens Next?

Now suppose 10,000 users request the same product immediately after the cache expires.

Every request performs the following logic.

```
Service

↓

Redis

↓

Cache Miss
```

Since the cache no longer contains the product,

every request falls back to the database.

```
Request 1

↓

Database

Request 2

↓

Database

Request 3

↓

Database

...

Request 10,000

↓

Database
```

Instead of executing

```
1 database query
```

the application suddenly executes

```
10,000 database queries
```

almost simultaneously.

This sudden burst of database traffic is called a **Cache Stampede**.

---

# Why is this Dangerous?

Suppose a database query normally takes

```
20 milliseconds
```

Normally

```
1 Query

↓

20 ms
```

During a cache stampede

```
10,000 Queries

↓

Database Queue

↓

High CPU

↓

Long Response Times

↓

Timeouts

↓

Application Errors
```

A single expired cache key can overload the entire database.

---

# Timeline

## Before Cache Expiration

```
Redis

↓

Product

↓

10,000 Users

↓

No Database Traffic
```

Everything is fast.

---

## Cache Expires

```
Redis

↓

Key Removed
```

---

## Immediately Afterwards

```
10,000 Requests

↓

Redis

↓

Cache Miss

↓

Database

↓

10,000 Queries
```

The database becomes overwhelmed.

---

# How Do We Prevent a Cache Stampede?

Several techniques are commonly used.

---

# Solution 1 – Request Coalescing (Single Flight)

This is one of the best solutions.

Only **one request** is allowed to query the database.

Everyone else waits briefly.

```
10,000 Requests

↓

Redis Miss

↓

Acquire Lock

↓

One Request

↓

Database

↓

Update Redis

↓

Remaining Requests

↓

Redis Hit
```

Instead of

```
10,000 database queries
```

the system performs only

```
1 database query.
```

---

# Solution 2 – Distributed Lock

Use a distributed lock (often implemented with Redis).

```
Redis Miss

↓

Acquire Lock

↓

Winner

↓

Database

↓

Update Cache

↓

Release Lock
```

All other requests wait until the cache is refreshed.

---

# Solution 3 – Randomized TTL

Suppose every cache entry expires after exactly 10 minutes.

```
Product A

10 min

Product B

10 min

Product C

10 min
```

All keys expire together.

This creates traffic spikes.

Instead, randomize the expiration.

```
Product A

9 min

Product B

11 min

Product C

13 min

Product D

10 min
```

Now cache entries expire gradually instead of simultaneously.

This significantly reduces the likelihood of a cache stampede.

---

# Solution 4 – Background Refresh

Instead of waiting until the cache expires,

refresh it before expiration.

```
Redis

↓

9 Minutes

↓

Background Refresh

↓

TTL Reset
```

Users never experience a cache miss.

This works well for hot data.

---

# Solution 5 – Serve Stale Data

Instead of deleting expired data immediately,

temporarily serve slightly stale data.

```
Redis

↓

Expired

↓

Return Existing Data

↓

Refresh Cache in Background
```

For many applications,

returning data that is a few seconds old is preferable to overloading the database.

Examples include:

- Product catalog
- News articles
- User profiles
- Recommendations

This strategy is generally **not** appropriate for:

- Bank balances
- Stock prices
- Real-time financial transactions

---

# Comparing the Solutions

| Solution | Advantages | Disadvantages |
|-----------|------------|---------------|
| Request Coalescing | Only one database query | Slight waiting time for other requests |
| Distributed Lock | Prevents duplicate cache refresh | Lock management complexity |
| Randomized TTL | Prevents synchronized expiration | Doesn't eliminate all cache misses |
| Background Refresh | Users rarely see cache misses | Requires additional background jobs |
| Serve Stale Data | Excellent user experience | Temporary stale data |

---

# Real-World Example

Imagine there is only one water cooler in an office.

Normally

```
Employees

↓

Water Cooler
```

Everything works smoothly.

Now the water cooler becomes empty.

Instead of one employee refilling it,

everyone rushes to the basement to get water.

```
100 Employees

↓

Basement

↓

Chaos
```

This is exactly what happens during a cache stampede.

Redis is the water cooler.

The database is the basement.

---

# Interview Example

**Question**

> Your product page receives 500,000 requests per minute. The cache entry expires. What happens?

**Strong Answer**

> When the cache entry expires, many requests may simultaneously miss the cache and hit the database. This creates a cache stampede, which can overwhelm the database. To mitigate this, I would use request coalescing or a distributed lock so that only one request refreshes the cache while the others wait or temporarily receive stale data. I would also randomize TTL values and proactively refresh hot keys in the background.

---

# Common Interview Mistakes

❌ Assuming cache misses are harmless.

❌ Ignoring hot keys.

❌ Using the same TTL for every cache entry.

❌ Allowing every request to query the database after expiration.

❌ Forgetting to discuss mitigation strategies.

---

# Key Takeaways

- A Cache Stampede occurs when many requests simultaneously miss the cache and overload the database.
- It is also known as the **Thundering Herd Problem**.
- The problem usually occurs immediately after a popular cache entry expires.
- Request Coalescing (Single Flight) is one of the most effective solutions.
- Distributed Locks ensure only one request refreshes the cache.
- Randomized TTL prevents synchronized expiration.
- Background Refresh proactively updates popular cache entries.
- Serving stale data is acceptable for many read-heavy workloads.
- Hot keys require special attention because they are requested far more frequently than average data.

---

# Interview Tip

Whenever you mention **Redis** or **TTL**, be prepared for the interviewer to ask:

> **"What happens when a hot cache entry expires?"**

A strong answer should immediately mention:

1. Cache Stampede (Thundering Herd Problem)
2. Request Coalescing / Distributed Lock
3. Randomized TTL
4. Background Refresh
5. Serve Stale Data (when acceptable)

Explaining both the problem and multiple mitigation strategies demonstrates strong production system design experience.

---


# Next Chapter

**Messaging & Event-Driven Architecture**

We'll answer:

- Why Kafka?
- Why RabbitMQ?
- Why SQS?
- Why asynchronous processing?
- What is Pub/Sub?
- Ordering guarantees
- Consumer groups
- Retry strategies
- Dead Letter Queues
- Idempotency


# Chapter 5 – Messaging & Event-Driven Architecture

---

# Why This Chapter Matters

Not every task should happen immediately.

Consider an e-commerce website.

When a customer places an order, should the user wait while the system:

- Sends an email
- Sends an SMS
- Updates analytics
- Generates recommendations
- Creates invoices
- Updates inventory reports

No.

The user only cares that:

> **The order was successfully placed.**

Everything else can happen later.

This is where messaging systems become essential.

---

# Synchronous vs Asynchronous Communication

## Synchronous

```
Client

↓

Order Service

↓

Payment Service

↓

Inventory Service

↓

Notification Service

↓

Response
```

Every service waits for the previous one.

Problems

- Slow
- Fragile
- High latency
- Tight coupling

If one service is down,

everything fails.

---

## Asynchronous

```
Client

↓

Order Service

↓

Kafka

↓

Response

↓

Inventory Worker

↓

Notification Worker

↓

Analytics Worker
```

The user receives a response immediately.

Other services work independently.

---

# What is a Message Queue?

A message queue temporarily stores work.

Instead of

```
Service A

↓

Service B
```

we introduce a queue.

```
Service A

↓

Queue

↓

Service B
```

The queue decouples producers from consumers.

---

# Why Use Messaging?

Messaging solves several problems.

- Decoupling
- Scalability
- Reliability
- Retry
- Burst traffic
- Background processing

---

# Producer

The producer creates events.

Example

```
Order Created
```

Producer

```
Order Service

↓

Publish Event
```

The producer doesn't know

who will consume the event.

---

# Consumer

Consumers process events.

Example

```
Inventory Worker

Notification Worker

Analytics Worker
```

Each consumes the same event independently.

---

# Event

An event represents something that happened.

Examples

```
OrderCreated

PaymentSucceeded

UserRegistered

ProductUpdated

ShipmentDelivered
```

Notice

Events describe facts.

Not commands.

---

# Queue

Imagine

```
100,000 orders

↓

Queue

↓

Workers
```

Instead of overwhelming the workers,

the queue smooths traffic.

This is called **load leveling**.

---

# Pub/Sub

Sometimes multiple systems need the same event.

Example

```
Order Created

↓

Notification

Inventory

Analytics

Recommendation
```

Instead of calling four services,

publish one event.

Every subscriber receives it.

---

# Queue vs Pub/Sub

Queue

```
One Message

↓

One Consumer
```

Pub/Sub

```
One Message

↓

Many Consumers
```

---

# Apache Kafka

Kafka is a distributed event streaming platform.

Think of Kafka as

```
Distributed Commit Log
```

Messages are stored on disk

and can be replayed.

Kafka is excellent for

- Streaming
- Analytics
- High throughput
- Event sourcing

---

# Kafka Concepts

---

## Topic

A topic is like a category.

Example

```
Orders

Payments

Users

Notifications
```

Messages of the same type go into the same topic.

---

## Partition

Topics are divided into partitions.

```
Orders

Partition 1

Partition 2

Partition 3
```

Partitions enable horizontal scaling.

---

## Offset

Every message has an offset.

```
0

1

2

3

4
```

Consumers remember offsets.

This allows replaying messages.

---

## Consumer Group

Suppose

```
Orders Topic
```

has

```
4 partitions
```

and

```
4 consumers
```

Each consumer handles one partition.

More throughput.

---

# Kafka Ordering

Kafka guarantees ordering

within a partition.

Not across partitions.

Interview tip:

If ordering matters,

ensure related events go to the same partition.

---

# RabbitMQ

RabbitMQ is a traditional message broker.

Strengths

- Routing
- Reliable delivery
- Work queues
- Complex messaging patterns

Better suited for

- Task queues
- Background jobs
- Enterprise integration

---

# Amazon SQS

Amazon SQS is a managed queue.

Advantages

- Fully managed
- Highly reliable
- No infrastructure management

Great for

AWS-native applications.

---

# Kafka vs RabbitMQ vs SQS

| Feature | Kafka | RabbitMQ | Amazon SQS |
|----------|--------|-----------|------------|
| Throughput | Very High | Medium | High |
| Replay Messages | Yes | No | Limited |
| Ordering | Per Partition | Queue Order | FIFO Queue Only |
| Best For | Event Streaming | Work Queues | Cloud Messaging |
| Persistence | Excellent | Good | Managed |

---

# Event-Driven Architecture

Instead of services calling each other directly,

they communicate using events.

Traditional

```
Order

↓

Inventory

↓

Notification

↓

Analytics
```

Event Driven

```
Order

↓

Kafka

↓

Inventory

Notification

Analytics
```

Loose coupling.

Better scalability.

---

# Retry

Sometimes consumers fail.

Instead of losing the message,

retry.

Typical strategy

```
Retry

↓

1 second

↓

2 seconds

↓

4 seconds

↓

8 seconds
```

This is

**Exponential Backoff.**

---

# Dead Letter Queue (DLQ)

Some messages will never succeed.

Don't retry forever.

Instead

```
Message

↓

Retry

↓

Retry

↓

Retry

↓

DLQ
```

Later

an engineer investigates.

---

# Idempotency

Suppose

```
PaymentProcessed
```

arrives twice.

Without idempotency

```
Charge Customer Twice
```

Bad.

Instead

```
Check Payment ID

Already Processed?

↓

Ignore
```

Consumers should be idempotent.

---

# Exactly Once?

Interview trick question.

Three delivery guarantees.

---

## At Most Once

```
Never duplicate

May lose messages
```

---

## At Least Once

```
Never lose messages

Duplicates possible
```

Most common.

---

## Exactly Once

Very difficult.

Usually achieved using

- Transactions
- Idempotency
- Deduplication

---

# Event Versioning

Events evolve.

Instead of changing

```
OrderCreated
```

Create

```
OrderCreatedV2
```

Avoid breaking existing consumers.

---

# Monitoring

Monitor

- Queue depth
- Consumer lag
- Retry count
- DLQ size
- Processing latency
- Throughput

---

# Interview Example

Question

```
Email service is down.

What happens?
```

Good answer

```
Order Service

↓

Kafka

↓

Queue grows

↓

Emails delayed

↓

Order still succeeds
```

The system remains available.

---

# Common Mistakes

❌ Calling every service synchronously

❌ No retry strategy

❌ No DLQ

❌ No idempotency

❌ Ignoring ordering

❌ One giant topic for everything

---

# Interview Tips

Don't say

> "I'll use Kafka."

Say

> "Since multiple downstream services independently consume order events and high throughput is expected, an event-driven architecture with Kafka decouples producers from consumers while improving scalability and resilience."

Explain the problem first.

Then introduce the technology.

---

# Key Takeaways

- Use asynchronous processing whenever the user does not need an immediate result.
- Queues decouple producers and consumers.
- Pub/Sub allows multiple subscribers.
- Kafka excels at event streaming.
- RabbitMQ is strong for task queues.
- SQS is ideal for AWS-managed messaging.
- Retries should use exponential backoff.
- Failed messages belong in a Dead Letter Queue.
- Consumers should be idempotent.
- Monitor consumer lag and queue depth.

---

# Next Chapter

**Scalability & Reliability**

We'll cover

- Horizontal Scaling
- Vertical Scaling
- Auto Scaling
- Read Replicas
- Sharding
- Rate Limiting
- Circuit Breakers
- Health Checks
- Timeouts
- Monitoring
- Distributed Tracing
- Logging
- High Availability

# Chapter 6 – Scalability & Reliability

---

# Why This Chapter Matters

Building a system that works for 100 users is easy.

Building one that works for

- 10 million users
- Millions of requests per second
- Multiple data centers
- Hardware failures
- Traffic spikes

is the real challenge.

System Design interviews are largely about answering one question:

> **"How does your system behave when demand grows or things fail?"**

---

# Scalability vs Reliability

These are different concepts.

## Scalability

Can the system handle **more traffic**?

Example

```
Today

10,000 requests/sec

↓

Tomorrow

1,000,000 requests/sec
```

Can the system still work?

---

## Reliability

Can the system continue working **when failures occur?**

Example

```
Database crashes

↓

System still works
```

A good architecture focuses on both.

---

# Vertical Scaling

Also called

> Scale Up

Increase the power of one server.

Example

```
Old Server

8 CPU

32 GB RAM

↓

New Server

64 CPU

512 GB RAM
```

Advantages

- Simple
- No code changes
- Fast to implement

Disadvantages

- Hardware limit
- Expensive
- Single point of failure

---

# Horizontal Scaling

Also called

> Scale Out

Instead of buying a bigger machine

buy more machines.

```
Server

↓

Server

Server

Server

Server
```

Advantages

- Nearly unlimited scaling
- Better fault tolerance
- Higher availability

Disadvantages

- More complexity
- Distributed systems problems

Most internet-scale companies scale horizontally.

---

# Auto Scaling

Traffic changes.

Example

```
Night

5 servers

↓

Black Friday

200 servers
```

Cloud providers automatically add or remove servers.

Benefits

- Lower cost
- Better availability
- Handles traffic spikes

---

# Stateless Services

Horizontal scaling works best when services are stateless.

Bad

```
Server remembers user session
```

If the server crashes,

the session is lost.

Better

```
Session stored in Redis
```

Now any server can handle the request.

---

# Single Point of Failure (SPOF)

Imagine

```
Client

↓

One Database
```

If that database fails,

everything stops.

This is called a

Single Point of Failure.

Always identify SPOFs during interviews.

---

# High Availability (HA)

High Availability means the service continues operating despite failures.

Typical approach

```
Primary

↓

Replica

↓

Replica
```

If one instance fails,

another takes over.

---

# Load Balancer

Load balancers improve both

- Scalability
- Availability

```
Load Balancer

↓

Server A

↓

Server B

↓

Server C
```

If Server B fails,

traffic automatically shifts.

---

# Health Checks

How does the Load Balancer know a server failed?

It performs health checks.

```
Are you alive?

↓

200 OK

↓

Healthy
```

If no response

```
Remove from rotation
```

---

# Rate Limiting

Suppose

one user sends

```
1 million requests
```

Your system may crash.

Rate limiting protects the system.

Example

```
100 requests/minute
```

After that

```
HTTP 429

Too Many Requests
```

Common algorithms

- Token Bucket
- Leaky Bucket
- Fixed Window
- Sliding Window

Know the idea, not every implementation.

---

# Circuit Breaker

Imagine

Payment Service is down.

Without a circuit breaker

```
Retry

Retry

Retry

Retry
```

Everything slows down.

Instead

```
Circuit Opens

↓

Fail Fast
```

Once the dependency recovers,

the circuit closes.

Benefits

- Prevent cascading failures
- Faster recovery
- Protect downstream services

---

# Timeout

Never wait forever.

Instead

```
Wait

2 seconds

↓

Timeout
```

Then

- Retry
- Return cached data
- Show fallback

---

# Retry

Many failures are temporary.

Retry

```
1 sec

↓

2 sec

↓

4 sec

↓

8 sec
```

This is

Exponential Backoff.

---

# Bulkhead Pattern

Suppose

Email Service crashes.

Should Payment Service also crash?

No.

Separate resource pools.

```
Payment Threads

Inventory Threads

Notification Threads
```

One failure stays isolated.

---

# Read Replicas

Reads usually outnumber writes.

Instead of

```
One Database
```

Use

```
Primary

↓

Replica

↓

Replica

↓

Replica
```

Writes

↓

Primary

Reads

↓

Replicas

Much better scalability.

---

# Sharding

Eventually

one database becomes too large.

Split the data.

```
Shard 1

Users 1-1M

Shard 2

Users 1M-2M

Shard 3

Users 2M-3M
```

Each shard handles part of the workload.

---

# Monitoring

You cannot fix what you cannot observe.

Monitor

- CPU
- Memory
- Latency
- QPS
- Queue Depth
- Cache Hit Ratio
- DB Connections
- Error Rate

Monitoring should be discussed in every design interview.

---

# Logging

Logs answer

> What happened?

Example

```
Request Received

↓

Payment Started

↓

Inventory Reserved

↓

Order Completed
```

Without logs

production debugging becomes extremely difficult.

---

# Metrics

Metrics answer

> How healthy is the system?

Examples

- Average Latency
- Error Rate
- Success Rate
- Throughput
- Active Users

---

# Distributed Tracing

One request may travel through

```
Gateway

↓

Order

↓

Payment

↓

Inventory

↓

Notification
```

Tracing connects all these calls.

Popular tools

- Jaeger
- Zipkin
- AWS X-Ray

Useful for debugging latency.

---

# Graceful Degradation

Suppose

Recommendation Service fails.

Should checkout fail?

No.

Disable recommendations.

Checkout still succeeds.

Always identify

critical

vs

non-critical

services.

---

# Disaster Recovery

Prepare for

- Region failure
- Database corruption
- Cloud outage

Common strategies

- Multi-region deployment
- Automated backups
- Cross-region replication
- Failover

---

# Interview Example

Question

```
Traffic increases by 100x.

What changes?
```

Strong answer

- Horizontal scaling
- Auto Scaling
- Load Balancers
- Redis
- Read Replicas
- Sharding
- Kafka
- CDN
- Monitoring

Notice

No single technology solves scaling.

Scaling is a combination of techniques.

---

# Common Mistakes

❌ One huge database

❌ No health checks

❌ No monitoring

❌ Infinite retries

❌ Stateful services

❌ Ignoring failures

❌ No rate limiting

---

# Interview Tips

Don't say

> "I'll add more servers."

Say

> "Since the service is stateless, we can horizontally scale behind a load balancer. To reduce database pressure, we'll introduce Redis for caching, use read replicas for read-heavy traffic, and autoscale application servers based on CPU and request rate."

Always explain the reasoning behind each decision.

---

# Key Takeaways

- Scalability is handling growth.
- Reliability is handling failure.
- Horizontal scaling is preferred for distributed systems.
- Keep services stateless.
- Eliminate single points of failure.
- Use health checks and load balancers.
- Protect systems with rate limiting.
- Use circuit breakers and retries carefully.
- Monitor everything.
- Design for graceful degradation.
- Always assume failures will happen.

---

# Next Chapter

**Chapter 7 – Architecture Patterns**

We'll cover:

- Monolith vs Microservices
- Event-Driven Architecture
- CQRS
- Saga Pattern
- Event Sourcing
- API Gateway Pattern
- Backend for Frontend (BFF)
- Fan-Out / Fan-In
- Orchestration vs Choreography

This chapter ties together everything you've learned so far into reusable architecture patterns used in real-world distributed systems.

# Chapter 7 – Architecture Patterns

---

# Why This Chapter Matters

By now you've learned:

- Databases
- Caching
- Messaging
- Scalability
- Reliability

This chapter focuses on **how to combine these building blocks into real architectures.**

Think of architecture patterns as reusable blueprints.

Just like software design patterns (Factory, Strategy, Observer), architecture patterns solve recurring system-level problems.

---

# Pattern 1 – Monolith

A monolith is a single deployable application.

```
+-------------------------------------+
|                                     |
|   User Management                   |
|   Orders                            |
|   Payments                          |
|   Inventory                         |
|   Notifications                     |
|                                     |
+-------------------------------------+
                 │
                 ▼
            One Database
```

Everything runs inside one application.

---

## Advantages

- Simple to build
- Easy deployment
- Easy debugging
- Simple transactions
- Fewer distributed system problems

---

## Disadvantages

- Difficult to scale independently
- Large codebase
- Slow deployments
- Entire application must be redeployed
- One bug can affect everything

---

## When to Use

- Small teams
- Startups
- Early-stage products
- Internal tools

---

# Pattern 2 – Microservices

Instead of one application,

split the system into independent services.

```
                 API Gateway
                      │
 ┌──────────┬─────────┼─────────┬──────────┐
 ▼          ▼         ▼         ▼          ▼
User     Order    Payment   Inventory   Notification
Service  Service  Service   Service     Service
 │          │         │          │           │
 ▼          ▼         ▼          ▼           ▼
DB        DB        DB         DB          DB
```

Each service owns its data.

---

## Advantages

- Independent deployment
- Independent scaling
- Better fault isolation
- Smaller codebases
- Technology flexibility

---

## Disadvantages

- Operational complexity
- Network latency
- Distributed transactions
- Service discovery
- Monitoring becomes harder

---

## Interview Tip

Don't recommend microservices automatically.

For small systems,

a monolith is often the better choice.

---

# Pattern 3 – Event-Driven Architecture

Instead of direct communication,

services communicate through events.

Traditional

```
Order

↓

Inventory

↓

Notification

↓

Analytics
```

Event Driven

```
Order

↓

Kafka

↓

Inventory

Notification

Analytics

Recommendation
```

Services become loosely coupled.

---

## Advantages

- Highly scalable
- Independent services
- Easy to add consumers
- Better resilience

---

## Disadvantages

- Eventual consistency
- Debugging is harder
- Ordering challenges

---

# Pattern 4 – CQRS

CQRS stands for

Command Query Responsibility Segregation.

Separate

```
Writes
```

from

```
Reads
```

Architecture

```
Client

↓

Command Service

↓

Database

↓

Events

↓

Read Database

↓

Query Service
```

---

## Why?

Read traffic often exceeds write traffic.

Instead of optimizing one database for both,

optimize each independently.

---

## Good Use Cases

- Banking
- Analytics
- Reporting
- Large-scale dashboards

---

# Pattern 5 – Event Sourcing

Instead of storing current state,

store every event.

Traditional

```
Balance = $500
```

Event Sourcing

```
Account Created

↓

Deposit $200

↓

Withdraw $50

↓

Deposit $350
```

Current state is reconstructed from events.

---

## Advantages

- Complete audit history
- Easy replay
- Temporal queries

---

## Disadvantages

- More storage
- Complex implementation
- Event versioning

---

# Pattern 6 – Saga Pattern

Distributed transactions are difficult.

Instead of one large transaction,

use multiple local transactions.

Example

```
Order

↓

Payment

↓

Inventory

↓

Shipping
```

If Inventory fails,

execute compensating actions.

```
Cancel Payment

↓

Cancel Order
```

---

## Types of Saga

### Choreography

Services react to events.

```
Order

↓

Payment

↓

Inventory

↓

Shipping
```

No central coordinator.

---

### Orchestration

One service controls everything.

```
Saga Coordinator

↓

Payment

↓

Inventory

↓

Shipping
```

Easier to understand.

---

# Pattern 7 – API Gateway

Instead of exposing every service,

provide one entry point.

```
Client

↓

API Gateway

↓

Microservices
```

Responsibilities

- Authentication
- Authorization
- Routing
- Rate Limiting
- Logging

---

# Pattern 8 – Backend For Frontend (BFF)

Different clients have different needs.

Instead of one API,

provide specialized APIs.

```
Mobile

↓

Mobile BFF

↓

Services

Desktop

↓

Desktop BFF

↓

Services
```

Benefits

- Optimized responses
- Less over-fetching
- Better performance

---

# Pattern 9 – Fan-Out

One request produces many tasks.

```
Upload Photo

↓

Generate Thumbnail

↓

Virus Scan

↓

Face Detection

↓

AI Caption
```

Perfect for Kafka.

---

# Pattern 10 – Fan-In

Combine multiple independent results.

Example

Homepage

```
User

↓

Orders

↓

Recommendations

↓

Notifications

↓

Combine

↓

Response
```

---

# Pattern 11 – Database Per Service

Each microservice owns its database.

```
Order Service

↓

Order DB

Payment Service

↓

Payment DB
```

Advantages

- Independent scaling
- Loose coupling

Disadvantages

- Joins become difficult
- Distributed transactions

---

# Pattern 12 – Shared Database

```
Service A

↓

Shared Database

↑

Service B
```

Easy initially.

Becomes tightly coupled later.

Generally discouraged for large microservice architectures.

---

# Choosing the Right Pattern

| Situation | Recommended Pattern |
|-----------|----------------------|
| Startup MVP | Monolith |
| Large Enterprise | Microservices |
| Analytics | Event-Driven |
| Read Heavy | CQRS |
| Audit Requirements | Event Sourcing |
| Distributed Transactions | Saga |
| Multiple Client Types | Backend for Frontend |
| Async Work | Fan-Out |

---

# Common Interview Questions

### Why microservices?

Independent deployment and scaling.

---

### Why not microservices?

Operational complexity may outweigh benefits.

---

### Why CQRS?

Optimize reads and writes separately.

---

### Why Event-Driven?

Loose coupling and asynchronous processing.

---

### Why Saga?

Maintain consistency across distributed services.

---

# Common Mistakes

❌ Choosing microservices too early

❌ Sharing databases across services

❌ Using distributed transactions everywhere

❌ Ignoring eventual consistency

❌ No compensating actions

---

# Interview Example

Question

```
Design Amazon Order Processing
```

Strong answer

- API Gateway
- Order Microservice
- Payment Microservice
- Inventory Microservice
- Kafka for events
- Saga Pattern for consistency
- Redis for caching
- CQRS for reporting
- Read Replicas for scaling

Notice

Multiple patterns work together.

---

# Interview Tips

Don't memorize patterns.

Instead ask:

> **What problem am I solving?**

Then choose the simplest pattern that solves it.

Architecture patterns are tools, not goals.

---

# Key Takeaways

- Monoliths are simple and effective for small systems.
- Microservices improve scalability but add complexity.
- Event-Driven Architecture enables loose coupling.
- CQRS separates read and write workloads.
- Event Sourcing stores events instead of state.
- Saga replaces distributed transactions.
- API Gateways centralize client access.
- Backend for Frontend optimizes APIs for different clients.
- Fan-Out and Fan-In enable scalable asynchronous workflows.
- Every pattern involves trade-offs.

---

# Next Chapter

# System Design Building Blocks Cheat Sheet

Before jumping into full interview questions, we'll build a **one-stop reference chapter** that summarizes:

- When to use SQL vs NoSQL
- When to use Redis
- When to use Kafka
- When to shard
- When to use read replicas
- When to use CQRS
- When to use Saga
- When to scale horizontally
- Common trade-offs

This will become your **10-minute revision guide** before any system design interview.

# Chapter 8 – System Design Decision Matrix

---

# Why This Chapter Matters

One of the biggest mistakes candidates make is choosing technologies first.

Example:

❌ "I'll use Kafka."

Instead think:

> What problem am I trying to solve?

This chapter helps you map problems to technologies.

Think of it as your interview cheat sheet.

---

# System Design Decision Flow

```
Need Persistent Storage?

↓

Yes

↓

Need Transactions?

↓

Yes

↓

SQL

↓

No

↓

NoSQL

↓

Read Heavy?

↓

Redis

↓

More Reads?

↓

Read Replicas

↓

More Writes?

↓

Sharding

↓

Long Running Tasks?

↓

Kafka / Queue

↓

Millions of Static Assets?

↓

CDN

↓

Need Search?

↓

Elasticsearch
```

---

# 1. When Should I Use SQL?

Choose SQL when you need

- ACID Transactions
- Strong Consistency
- Complex Queries
- Joins
- Referential Integrity
- Structured Data

Examples

- Banking
- Payments
- Inventory
- Orders
- Payroll

Interview phrase

> "Data consistency is critical, therefore a relational database is appropriate."

---

# 2. When Should I Use NoSQL?

Choose NoSQL when

- Massive Scale
- Flexible Schema
- High Write Throughput
- Denormalized Data
- Simple Queries

Examples

- Social Media
- Logging
- Activity Feeds
- IoT
- Product Catalog

Interview phrase

> "Since the access patterns are simple and horizontal scalability is more important than joins, a NoSQL database is a better fit."

---

# 3. When Should I Use Redis?

Use Redis when

- Frequent Reads
- Hot Data
- Session Storage
- Leaderboards
- User Profiles
- Product Details
- Configuration

Don't use Redis for

- Permanent Storage
- Large Historical Data
- Frequently Changing Financial Data

Interview phrase

> "Redis reduces database load by serving frequently accessed data directly from memory."

---

# 4. When Should I Use Kafka?

Use Kafka when

- Event Streaming
- High Throughput
- Multiple Consumers
- Async Processing
- Event Replay
- Audit Logs

Examples

- Notifications
- Analytics
- Order Events
- Recommendation Systems

Don't use Kafka for

- Simple synchronous communication
- Small request-response interactions

---

# 5. When Should I Use RabbitMQ?

Use RabbitMQ when

- Background Jobs
- Reliable Task Processing
- Complex Routing
- Enterprise Integration

Examples

- PDF Generation
- Invoice Creation
- Image Processing

---

# 6. When Should I Use Amazon SQS?

Choose SQS when

- Entire application is on AWS
- Managed infrastructure is preferred
- Simple queues are sufficient

Avoid if

- Message replay
- Event streaming
- Complex event processing

---

# 7. When Should I Cache?

Cache when

- Reads greatly exceed writes
- Data changes infrequently
- Low latency matters

Examples

- User Profiles
- Product Catalog
- Search Results

Avoid caching

- Real-time account balances
- Highly volatile financial data

---

# 8. When Should I Add Read Replicas?

Use Read Replicas when

```
Reads >> Writes
```

Examples

- News Feed
- Product Pages
- Search
- User Profiles

Benefits

- Scale reads
- Reduce primary database load

Trade-off

Replication lag.

---

# 9. When Should I Shard?

Use Sharding when

One database

can no longer handle

- Storage
- Write throughput
- CPU

Example

```
Users

↓

Shard by UserId
```

Avoid sharding early.

It adds operational complexity.

---

# 10. When Should I Partition?

Partition tables when

One table becomes extremely large.

Examples

- Orders
- Logs
- Transactions

Often partition by

- Date
- UserId
- Region

---

# 11. When Should I Use CDN?

Use CDN for

- Images
- Videos
- JavaScript
- CSS
- Downloads

Benefits

- Lower latency
- Reduced backend traffic

Do not store

Dynamic user data.

---

# 12. When Should I Use Elasticsearch?

Need

```
LIKE '%apple%'
```

across

millions of products?

Use Elasticsearch.

Examples

- Product Search
- News Search
- Log Search

Avoid

Simple primary key lookups.

---

# 13. When Should I Use WebSockets?

Use WebSockets when

- Chat
- Gaming
- Live Stock Prices
- Live Sports
- Collaborative Editing

Avoid when

Simple request-response is enough.

---

# 14. Polling vs WebSockets

Polling

```
Client

↓

Server

↓

Every 5 seconds
```

Simple.

Wasteful.

---

WebSockets

```
Client

⇅

Server
```

Real-time.

Bi-directional.

---

# 15. When Should I Scale Horizontally?

Choose Horizontal Scaling when

- Cloud Deployment
- High Availability
- Large Traffic
- Internet Scale

Preferred for

Almost every modern distributed system.

---

# 16. When Should I Scale Vertically?

Choose Vertical Scaling when

- Legacy Systems
- Small Applications
- Temporary Scaling

Eventually

hardware becomes the limit.

---

# 17. When Should I Use Microservices?

Use when

- Large Engineering Teams
- Independent Deployments
- Independent Scaling
- Different Release Cycles

Avoid for

Small applications.

---

# 18. When Should I Stay Monolithic?

Good choice when

- Startup
- MVP
- Small Team
- Simple Product

A monolith is not bad.

Premature microservices are.

---

# 19. When Should I Use CQRS?

Use CQRS when

Reads and writes

have different scaling requirements.

Examples

- Reporting
- Analytics
- Dashboards

Avoid

Simple CRUD applications.

---

# 20. When Should I Use Saga?

Use Saga when

Multiple services

must complete

one business transaction.

Example

```
Order

↓

Payment

↓

Inventory

↓

Shipping
```

Distributed transactions become difficult.

Saga provides compensation.

---

# 21. When Should I Use Event-Driven Architecture?

Choose Event-Driven when

Multiple downstream systems

need the same event.

Examples

- Notifications
- Analytics
- Recommendations
- Inventory

Avoid

Simple request-response workflows.

---

# 22. Common Interview Decision Tree

```
Need Transactions?

↓

SQL

Need Scale?

↓

NoSQL

Too Many Reads?

↓

Redis

Database Overloaded?

↓

Read Replicas

Write Bottleneck?

↓

Sharding

Long Running Tasks?

↓

Kafka

Need Search?

↓

Elasticsearch

Static Files?

↓

CDN

Real-Time?

↓

WebSockets

Distributed Transaction?

↓

Saga

Different Read/Write Workloads?

↓

CQRS
```

---

# Top 20 Interview Questions

| Problem | Technology |
|----------|------------|
| Database overloaded | Redis |
| Read-heavy traffic | Read Replicas |
| Write-heavy traffic | Sharding |
| Millions of images | CDN |
| Full-text search | Elasticsearch |
| Chat | WebSockets |
| Notifications | Kafka |
| Reports | CQRS |
| Distributed transactions | Saga |
| Startup MVP | Monolith |
| Large enterprise | Microservices |
| User sessions | Redis |
| Product catalog | Cache |
| Analytics | Event Streaming |
| High availability | Load Balancer |
| Traffic spikes | Auto Scaling |
| Slow APIs | Cache |
| Background jobs | Queue |
| Hot objects | Redis |
| Multiple consumers | Pub/Sub |

---

# Golden Rule

Never begin with the technology.

Always begin with the problem.

Instead of saying

> "I'll use Redis."

Say

> "Since product information is read far more often than it changes, introducing Redis reduces database load and significantly improves response latency."

Interviewers evaluate your reasoning,

not your vocabulary.

---

# Final Checklist

Before choosing any technology ask:

✅ What problem am I solving?

✅ What are my functional requirements?

✅ What are my non-functional requirements?

✅ What are the trade-offs?

✅ Is there a simpler solution?

If you can answer these five questions, you'll make better architectural decisions than most candidates.

---

# Next Chapter

# End-to-End Interview Problems

We'll start applying everything you've learned.

Beginning with:

**Design a Notification Service**

because it naturally combines:

- SQL
- Redis
- Kafka
- Retry
- DLQ
- Idempotency
- Scaling
- Monitoring
- APIs
- Trade-offs

It is one of the best interview questions for senior backend engineers.

# Appendix – Synchronous vs Asynchronous Processing & The Transactional Outbox Pattern

---

# Why This Matters

One of the most common architecture interview questions is:

> **Which parts of your request are synchronous, and which are asynchronous?**

Understanding this distinction helps you design systems that are both responsive and reliable.

---

# Example Request Flow

```
Client

↓

DNS

↓

Load Balancer

↓

API Gateway

↓

Authentication

↓

Order Service

↓

Redis (Cache Miss)

↓

Database

↓

Save Order

↓

Publish Event

↓

Kafka

↓

Return Success Response

↓

Email Worker

↓

Send Confirmation Email
```

At first glance, this entire flow looks sequential.

However, not every step belongs in the user's critical path.

---

# What is the Critical Path?

The critical path consists of everything that **must complete before the user receives a response.**

For an order system, the user only cares about one thing:

> **Was my order successfully placed?**

The user does **not** need to wait for:

- Confirmation email
- Analytics updates
- Recommendation engine
- Inventory reporting
- Audit logging

These tasks can happen later.

---

# Which Steps are Synchronous?

Everything below is synchronous because the client is still waiting.

```
Client
   │
   ▼
DNS
   │
   ▼
Load Balancer
   │
   ▼
API Gateway
   │
   ▼
Authentication
   │
   ▼
Order Service
   │
   ▼
Redis
   │
   ▼
Database
   │
   ▼
Save Order
```

At this point, the order has been successfully persisted.

---

# Is Publishing to Kafka Synchronous?

This is where many candidates get confused.

Consider this flow.

```
Order Service

↓

Publish Event

↓

Kafka

↓

ACK

↓

Return Response
```

From the **Order Service's perspective**, publishing is still synchronous.

Why?

Because the service typically waits for Kafka to acknowledge that the message has been successfully stored.

Only then does it return success to the client.

This usually takes only a few milliseconds.

---

# Why Wait for Kafka?

Imagine this scenario.

```
Save Order ✓

↓

Kafka unavailable ❌
```

Now you have

- Order stored in the database
- No Kafka event

Consequences

- No email
- No inventory update
- No analytics
- No downstream processing

This is known as the **Dual Write Problem**.

The application successfully wrote to one system but failed to write to another.

---

# Which Steps are Asynchronous?

After Kafka acknowledges the event, the client no longer needs to wait.

```
Client receives HTTP 200

──────────────────────────────

Kafka

↓

Email Worker

↓

Send Email

↓

Analytics Worker

↓

Update Dashboard

↓

Recommendation Worker

↓

Generate Recommendations
```

Everything after the response is asynchronous.

These tasks execute independently of the user's request.

---

# Real-World Example

Think about Amazon.

You click

```
Place Order
```

Within a second you see

```
Order Placed Successfully
```

A few seconds later you receive

```
Order Confirmation Email
```

Amazon does not delay your order confirmation while waiting for the email service.

The email is asynchronous.

---

# Can We Remove Kafka from the Critical Path?

A natural question is:

> **Why not return immediately after saving the order?**

For example

```
Client

↓

Order Service

↓

Database

↓

Return Success
```

Then a background service reads the Orders table.

```
Orders Table

↓

Background Service

↓

Kafka
```

This shortens the critical path.

However, it introduces new problems.

---

# Problems with Polling the Orders Table

How does the background service know

- Which orders are new?
- Which orders have already been published?
- What if it crashes halfway?
- What if multiple publishers are running?

Scanning the Orders table continuously is inefficient and error-prone.

Business data and integration events become tightly coupled.

---

# The Industry Standard Solution

The preferred production approach is the **Transactional Outbox Pattern**.

Instead of writing only the order,

the application writes

- the Order
- an Outbox Event

inside the same database transaction.

```
Transaction

----------------------------

Insert Order

Insert Outbox Event

----------------------------

Commit
```

Both records are committed atomically.

If the transaction succeeds,

both exist.

If it fails,

neither exists.

---

# Outbox Table

Example

Orders

```
+---------+

| Order1 |

+---------+
```

Outbox

```
+--------------------------+

| Event: OrderCreated |

| Published = false |

+--------------------------+
```

The client immediately receives success.

---

# Outbox Publisher

A separate service continuously processes the Outbox table.

```
Outbox

↓

Publisher

↓

Kafka

↓

Mark Published = true
```

The publisher retries failed messages until Kafka accepts them.

The user never waits.

---

# Complete Flow

```
Client

↓

Order Service

↓

Database Transaction

│

├── Insert Order

└── Insert Outbox Event

↓

Commit

↓

Return Success

──────────────────────────────

Background Publisher

↓

Read Outbox

↓

Publish to Kafka

↓

Mark Published

↓

Consumers

↓

Email

Inventory

Analytics

Recommendations
```

This is one of the most common production architectures for reliable event publishing.

---

# Why is the Outbox Pattern Better?

Suppose Kafka is unavailable.

```
Insert Order ✓

Insert Outbox Event ✓

Kafka ❌
```

No problem.

The Outbox Publisher retries later.

Nothing is lost.

No manual recovery is required.

---

# Comparing the Three Approaches

## Option 1 – Publish Directly to Kafka

```
Save Order

↓

Publish Kafka

↓

Return Success
```

Advantages

- Simple
- Immediate event publication

Disadvantages

- Dual write problem
- Request latency includes Kafka acknowledgement

---

## Option 2 – Poll Orders Table

```
Save Order

↓

Return Success

↓

Poll Orders

↓

Publish Kafka
```

Advantages

- Short critical path

Disadvantages

- Inefficient polling
- Difficult to identify unpublished records
- Poor separation of concerns

---

## Option 3 – Transactional Outbox (Recommended)

```
Transaction

↓

Insert Order

+

Insert Outbox Event

↓

Commit

↓

Return Success

↓

Background Publisher

↓

Kafka
```

Advantages

- Atomic writes
- Reliable event publication
- Retries supported
- No distributed transaction
- Better separation of concerns

Disadvantages

- Requires an Outbox table
- Additional publisher service
- Slightly higher implementation complexity

---

# Interview Tips

If the interviewer asks

> **"Should the Order Service publish directly to Kafka?"**

A strong answer is:

> "Publishing directly to Kafka is simple but introduces the dual-write problem because the database and Kafka are separate systems. In production, I would prefer the Transactional Outbox Pattern. The Order Service writes both the Order and an Outbox Event within the same database transaction, returns success to the client immediately, and a background publisher asynchronously publishes the event to Kafka with retries and idempotency."

This answer demonstrates knowledge of production-grade distributed systems.

---

# Key Takeaways

- The user should wait only for work required to complete the request.
- Everything else should be asynchronous whenever possible.
- Directly writing to both the database and Kafka introduces the dual-write problem.
- Polling the Orders table is generally inefficient.
- The Transactional Outbox Pattern is a widely adopted production solution.
- Write the Order and Outbox Event in the same transaction.
- A background publisher reliably publishes events to Kafka.
- Consumers process events independently using retries and idempotency.
- Keep the critical path as short as possible while preserving reliability.
