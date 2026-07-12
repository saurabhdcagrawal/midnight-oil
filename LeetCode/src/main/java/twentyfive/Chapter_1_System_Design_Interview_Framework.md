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

# Optimistic Locking - Complete Guide

---

# What Problem Does Optimistic Locking Solve?

Optimistic locking prevents the **Lost Update Problem**, where two concurrent transactions overwrite each other's changes.

Suppose we have an account.

```
Account

Balance = $100
```

Two users make requests simultaneously.

```
Thread A

Withdraw $30
```

```
Thread B

Withdraw $40
```

Without proper concurrency control, one update may overwrite the other.

---

# Lost Update Problem

Both threads execute

```java
balance = SELECT balance;
```

Thread A reads

```
100
```

Thread B also reads

```
100
```

Both perform their calculations.

Thread A

```
100 - 30 = 70
```

Thread B

```
100 - 40 = 60
```

Now both update the database.

Thread A

```
UPDATE balance = 70
```

Thread B

```
UPDATE balance = 60
```

Final balance

```
$60
```

Correct balance should be

```
$30
```

Thread A's update was overwritten.

This is called the **Lost Update Problem**.

---

# Two Ways to Handle Concurrency

There are two major approaches.

```
Concurrency Control

↓

Optimistic Locking

OR

Pessimistic Locking
```

---

# Pessimistic Locking

Assumption

> Someone else WILL modify this row.

Immediately lock the row.

Example

```sql
SELECT *
FROM account
WHERE id = 1
FOR UPDATE;
```

Other transactions must wait.

Advantages

- No conflicting updates
- Guaranteed serialization

Disadvantages

- Lower throughput
- Waiting/blocking
- Possible deadlocks

---

# Optimistic Locking

Assumption

> Conflicts are rare.

Do NOT lock the row.

Instead,

allow everyone to read and modify independently.

When saving,

check whether another transaction already modified the row.

If yes,

reject the update.

---

# How Does It Work?

Add a version column.

```
Account

-------------------------

id

balance

version
```

Initial data

```
Balance = 100

Version = 1
```

---

# Example

Thread A reads

```
Balance = 100

Version = 1
```

Thread B also reads

```
Balance = 100

Version = 1
```

Both think

```
Version = 1
```

---

# Thread A Updates

Instead of

```sql
UPDATE account
SET balance = 70;
```

execute

```sql
UPDATE account
SET
    balance = 70,
    version = 2
WHERE
    id = 1
AND
    version = 1;
```

Notice

```
WHERE version = 1
```

Database checks

```
Version still 1?

↓

YES
```

Update succeeds.

Database now contains

```
Balance = 70

Version = 2
```

---

# Thread B Updates

Thread B still believes

```
Version = 1
```

It executes

```sql
UPDATE account
SET
    balance = 60,
    version = 2
WHERE
    id = 1
AND
    version = 1;
```

Database checks

```
Current Version = 2

Version = 1 ?

↓

NO
```

Rows updated

```
0
```

The application immediately knows

> Someone modified this row.

Conflict detected.

---

# Retry Flow

Thread B now retries.

```
Read Again

↓

Balance = 70

Version = 2

↓

Withdraw 40

↓

Balance = 30

↓

UPDATE
WHERE version = 2

↓

Success
```

Final balance

```
$30
```

Correct.

---

# Visual Timeline

```
Thread A                     Thread B

Read V1                      Read V1

↓

Compute                      Compute

↓

Update Success

Version = 2

                              ↓

                     Update Fails

                     Version Mismatch

                              ↓

                           Retry

                              ↓

                        Update Success
```

---

# Why Version Column?

The version column acts like a fingerprint.

Every successful update changes it.

Example

```
Version

1

↓

2

↓

3

↓

4
```

If your version is outdated,

your update is rejected.

---

# Example 2 – Product Inventory

Current Product

```
Stock = 10

Version = 5
```

Customer A buys

```
2
```

Customer B buys

```
3
```

Both read

```
Stock = 10

Version = 5
```

---

Customer A

```sql
UPDATE product

SET
stock = 8,
version = 6

WHERE
id = 1
AND version = 5;
```

Success.

---

Customer B

```sql
UPDATE product

SET
stock = 7,
version = 6

WHERE
id = 1
AND version = 5;
```

Fails.

Rows Updated

```
0
```

Application retries.

Reads

```
Stock = 8

Version = 6
```

Then updates again.

---

# Example 3 – Editing User Profile

Suppose

```
User Profile

Name = John

Version = 12
```

Laptop

```
Reads Version = 12
```

Phone

```
Reads Version = 12
```

Laptop changes

```
John

↓

John Smith
```

Version becomes

```
13
```

Phone later tries

```
John

↓

Johnny
```

Database rejects it.

Otherwise

Laptop's update would be lost.

---

# Example 4 – Google Docs

Google Docs uses a more sophisticated approach,

but the idea is similar.

Two users edit simultaneously.

Instead of locking the document,

Google detects conflicts

and merges or resolves them.

Optimistic locking follows the same philosophy.

---

# Java Example (Spring Boot / Hibernate)

Entity

```java
@Entity
public class Product {

    @Id
    private Long id;

    private Integer stock;

    @Version
    private Long version;
}
```

Now

```java
repository.save(product);
```

Hibernate automatically generates SQL similar to

```sql
UPDATE product

SET
stock=?,
version=?

WHERE
id=?
AND version=?;
```

You don't write the version check manually.

---

# Advantages

No locks.

High concurrency.

No waiting.

High throughput.

Excellent for

- User Profiles
- Product Details
- Blog Posts
- Employee Records
- Configuration Data

---

# Disadvantages

Conflicting updates fail.

Application must retry.

If conflicts are frequent,

many retries occur.

This wastes work.

---

# When Should You Use Optimistic Locking?

Use when

- Reads >> Writes
- Conflicts are rare
- High throughput matters
- Locking would reduce performance

Examples

- Editing profile
- Updating product description
- Editing blog
- Updating configuration

---

# When Should You Avoid It?

Suppose

```
Flash Sale

↓

100,000 Buyers

↓

Same Product
```

Everyone updates

```
Stock
```

Most updates fail.

Thousands of retries.

Poor performance.

Better choices

- Atomic SQL Update
- Pessimistic Locking
- Redis-based reservation
- Queue-based inventory reservation

---

# Optimistic vs Pessimistic Locking

| Optimistic Locking | Pessimistic Locking |
|--------------------|---------------------|
| Assumes conflicts are rare | Assumes conflicts are common |
| No lock while processing | Locks row immediately |
| Detects conflicts | Prevents conflicts |
| Retry on conflict | Wait for lock |
| High throughput | Lower throughput |
| No blocking | Blocking possible |
| No deadlocks caused by row locks | Deadlocks possible |

---

# Common Interview Questions

## Why do we need a version column?

To detect whether another transaction modified the row after we read it.

---

## Why does UPDATE affect zero rows?

Because the version no longer matches.

Another transaction updated the row first.

---

## Does optimistic locking prevent conflicts?

No.

It detects conflicts.

The application decides how to resolve them.

---

## What happens after conflict detection?

Possible strategies

- Retry automatically
- Return HTTP 409 Conflict
- Ask the user to refresh
- Merge changes

Depends on business requirements.

---

## Is optimistic locking faster?

Usually yes.

Because

- No locks
- No waiting
- Better concurrency

---

## Does it work across multiple application servers?

Yes.

Suppose

```
Server A

Server B

Server C
```

All servers update the same database.

The version check happens inside the database.

Therefore

it works correctly regardless of which server processes the request.

---

# Real-World Examples

## Amazon

Editing product details.

Conflicts are uncommon.

Optimistic locking is a good fit.

---

## Banking

Money transfer.

Conflicts cannot be tolerated.

Usually use

- Pessimistic locking
- Serializable transactions
- Carefully designed transactional logic

---

## Airline Seat Booking

One seat.

Thousands of buyers.

Optimistic locking leads to excessive retries.

Better options

- Pessimistic locking
- Reservation service
- Queue-based allocation

---

# Key Takeaways

- Optimistic locking assumes conflicts are rare.
- Rows are not locked while being read or processed.
- A version column detects concurrent updates.
- The UPDATE statement succeeds only if the version still matches.
- If another transaction updated the row first, the UPDATE affects zero rows.
- The application can retry, reject the request, or ask the user to refresh.
- Optimistic locking provides high throughput because transactions do not block each other.
- It is best suited for systems where reads greatly outnumber writes and update conflicts are infrequent.
- It detects conflicts rather than preventing them.

---

# Optimistic Locking and Database Concurrency Control

---

# Common Misconception

Many engineers believe that optimistic locking means

> "No locking happens at all."

This is **not true**.

Optimistic locking means

- The **application does not explicitly lock the row while reading and processing it.**
- The **database still uses its normal concurrency control mechanisms** (such as row locks and MVCC) for a very short period while executing the `UPDATE`.

The database guarantees that the `UPDATE` statement executes atomically.

---

# How Optimistic Locking Works

Suppose the table contains

```
Product

-------------------------

id = 1

stock = 10

version = 5
```

Two users read the same row.

```
Thread A

Stock = 10

Version = 5
```

```
Thread B

Stock = 10

Version = 5
```

Both perform business logic independently.

No locks are held during this time.

---

# Business Logic Happens Outside the Lock

Suppose the application performs

```
Read Product

↓

Calculate Discount

↓

Call Pricing Service

↓

Call Tax Service

↓

Validate Rules

↓

Prepare Update
```

This processing might take

```
500 ms
```

During these 500 ms

```
NO DATABASE ROW IS LOCKED
```

Other transactions are free to read and update the row.

---

# The Final Update

When saving,

the application executes

```sql
UPDATE product
SET
    stock = 9,
    version = 6
WHERE
    id = 1
AND
    version = 5;
```

This single SQL statement is atomic.

The database internally

- checks the WHERE clause
- updates the row
- increments the version

as one indivisible operation.

---

# What Happens If Two Updates Arrive Together?

Suppose both threads execute

```sql
UPDATE product
SET
    stock = ?,
    version = 6
WHERE
    id = 1
AND
    version = 5;
```

at almost the same time.

Database timeline

```
Thread A

Acquire Internal Row Lock

↓

Version == 5 ?

↓

YES

↓

Update Row

↓

Version = 6

↓

Commit

↓

Release Lock
```

Only after Thread A finishes

does Thread B continue.

Thread B now checks

```
Version == 5 ?

↓

NO

Current Version = 6
```

Rows Updated

```
0
```

Conflict detected.

No data is overwritten.

---

# Why Doesn't Both UPDATE Statements Succeed?

Because the database executes each UPDATE atomically.

Conceptually,

the database performs

```
Check WHERE Clause

+

Modify Row

+

Commit
```

as one protected operation.

Another transaction cannot modify the same row in the middle of this process.

The application's optimistic locking relies on the database's concurrency control to guarantee this behavior.

---

# Timeline

```
Thread A

Read Version = 5

↓

Business Logic (500 ms)

↓

UPDATE

↓

Internal DB Lock (2-5 ms)

↓

Commit

----------------------------

Thread B

Read Version = 5

↓

Business Logic (500 ms)

↓

UPDATE

↓

Wait Briefly

↓

Version Mismatch

↓

0 Rows Updated
```

Notice

The row is only protected during the UPDATE,

not during the business logic.

---

# Compare With Pessimistic Locking

## Pessimistic Locking

```
BEGIN

↓

SELECT ... FOR UPDATE

↓

🔒 Row Locked

↓

Business Logic (500 ms)

↓

UPDATE

↓

COMMIT

↓

Unlock
```

The row remains locked during the entire transaction.

---

## Optimistic Locking

```
SELECT

↓

Business Logic (500 ms)

↓

UPDATE ... WHERE version = ?

↓

Database Locks Row

(typically only a few milliseconds)

↓

Commit
```

The lock exists only during the UPDATE.

This allows much higher concurrency.

---

# Why Is Optimistic Locking Faster?

Suppose

100 users

read the same row.

With pessimistic locking

```
User 1

↓

Lock

↓

500 ms

↓

Unlock

↓

User 2

↓

500 ms

↓

Unlock

...
```

Everyone waits.

---

With optimistic locking

```
100 Users

↓

Read Simultaneously

↓

Process Simultaneously

↓

Attempt UPDATE

↓

Only conflicting updates retry
```

No long-lived locks.

Better throughput.

---

# Important Interview Point

Optimistic locking does **not** eliminate database locking.

It eliminates **long-lived application locks**.

The database still uses its normal concurrency mechanisms during the UPDATE statement to ensure atomicity and consistency.

Without the database's atomic UPDATE behavior,

optimistic locking would not work.

---

# Key Takeaways

- Optimistic locking does **not** lock rows while the application performs business logic.
- The application performs processing without holding database locks.
- The final `UPDATE ... WHERE version = ?` executes atomically.
- During the UPDATE, the database briefly uses its normal concurrency control (such as row locks and/or MVCC) to protect the row.
- Only one transaction can successfully update a given version.
- If another transaction has already modified the row, the UPDATE affects **0 rows**, indicating a version conflict.
- Optimistic locking depends on the database's atomic execution of UPDATE statements to detect concurrent modifications correctly.
- The major advantage is that locks are held only for a few milliseconds instead of throughout the entire business operation, allowing much higher concurrency.


# Database Concurrency - Reads vs Writes (MVCC & Row Locking)

---

# A Common Interview Question

Many developers assume

> "If one transaction is updating a row, nobody else can even read it."

This is **not true** for modern relational databases such as

- PostgreSQL
- MySQL InnoDB
- Oracle

These databases use **MVCC (Multi-Version Concurrency Control)**.

MVCC allows most reads to happen concurrently without blocking writes.

---

# Scenario 1 – SELECT + SELECT

Current row

```
Product

Stock = 10

Version = 5
```

Two users execute

```sql
SELECT *
FROM product
WHERE id = 1;
```

at the same time.

Timeline

```
Thread A

SELECT

↓

Reads Version 5

----------------------------

Thread B

SELECT

↓

Reads Version 5
```

Result

✅ Both execute simultaneously.

No waiting.

No locking.

---

# Scenario 2 – SELECT + UPDATE

Current row

```
Stock = 10

Version = 5
```

Thread A

```sql
SELECT *
FROM product
WHERE id = 1;
```

Thread B

```sql
UPDATE product
SET stock = 9
WHERE id = 1;
```

Can they run together?

Yes.

With MVCC,

the SELECT reads a consistent snapshot while the UPDATE creates a newer version of the row.

The reader is not blocked.

The writer is not blocked.

---

# How MVCC Works

Think of the database as keeping multiple versions of the same row.

Initially

```
Version 5

Stock = 10
```

Thread B updates it.

Database internally creates

```
Version 6

Stock = 9
```

Thread A, which started earlier,

continues reading

```
Version 5
```

New transactions read

```
Version 6
```

Readers and writers do not block each other.

---

# Scenario 3 – UPDATE + UPDATE

Current row

```
Stock = 10

Version = 5
```

Thread A

```sql
UPDATE ...
```

Thread B

```sql
UPDATE ...
```

Both want to modify

the same row.

This cannot happen simultaneously.

Database behavior

```
Thread A

Acquire Row Lock

↓

Modify Row

↓

Commit

↓

Release Lock

------------------------

Thread B

Wait

↓

Acquire Lock

↓

Update
```

Conflicting writes are serialized.

---

# Scenario 4 – UPDATE Different Rows

Thread A

```sql
UPDATE product
SET stock = 5
WHERE id = 1;
```

Thread B

```sql
UPDATE product
SET stock = 8
WHERE id = 2;
```

Different rows.

No conflict.

Both execute simultaneously.

---

# Scenario 5 – SELECT FOR UPDATE

Normal SELECT

```sql
SELECT *
FROM product
WHERE id = 1;
```

Does NOT lock the row.

Other users can

- Read
- Update

normally.

---

Now consider

```sql
SELECT *
FROM product
WHERE id = 1
FOR UPDATE;
```

This is different.

Timeline

```
Transaction A

SELECT FOR UPDATE

↓

Row Locked

↓

Business Logic

↓

UPDATE

↓

COMMIT

↓

Unlock
```

Now another transaction trying to update the same row

must wait.

This is **Pessimistic Locking**.

---

# Why Allow Concurrent Reads?

Imagine Amazon.

```
1 Million Customers

↓

Viewing Product
```

Should everyone wait

because one customer edits the product?

No.

That would destroy performance.

Instead

```
Readers

↓

Concurrent

--------------------

Writers

↓

Serialized
```

---

# Think of a Library

Normal SELECT

```
100 People

↓

Read Same Book
```

Everyone can read.

No problem.

---

UPDATE

```
Editor A

↓

Modify Book

↓

Save

↓

Editor B
```

Only one editor modifies the book at a time.

Readers continue reading the last published edition.

This is similar to MVCC.

---

# Relationship with Optimistic Locking

Optimistic locking depends on this behavior.

Timeline

```
Thread A

Read Version 5

↓

Business Logic (500 ms)

↓

UPDATE WHERE version = 5

↓

Success

----------------------------

Thread B

Read Version 5

↓

Business Logic (500 ms)

↓

UPDATE WHERE version = 5

↓

0 Rows Updated

↓

Retry
```

Notice

The reads happen concurrently.

Only the UPDATE is serialized by the database.

---

# Summary Table

| Operation | Concurrent? | Reason |
|------------|------------|--------|
| SELECT + SELECT | ✅ Yes | Readers do not block readers |
| SELECT + UPDATE | ✅ Usually Yes | MVCC provides consistent snapshots |
| UPDATE + UPDATE (same row) | ❌ No | Conflicting writes are serialized |
| UPDATE + UPDATE (different rows) | ✅ Yes | Different rows, no conflict |
| SELECT FOR UPDATE + UPDATE | ❌ No | Row lock forces waiting |

---

# Important Interview Point

It is not entirely correct to say

> "Only updates are serialized."

A better statement is

> **Conflicting writes to the same row are serialized by the database. Reads are usually served concurrently using MVCC, allowing them to see a consistent snapshot without blocking writers. Updates to different rows can also proceed concurrently because they do not conflict.**

This is the expected explanation in a Senior Backend interview.

---

# Key Takeaways

- Modern relational databases use **MVCC (Multi-Version Concurrency Control)** to improve concurrency.
- Multiple SELECT queries on the same row execute concurrently.
- SELECT queries usually do not block UPDATE operations, and UPDATE operations usually do not block SELECT queries.
- Multiple UPDATE statements on the **same row** cannot modify the row simultaneously; the database serializes conflicting writes.
- UPDATE statements on **different rows** can execute concurrently.
- `SELECT ... FOR UPDATE` acquires a row lock and is commonly used for pessimistic locking.
- Optimistic locking relies on the database's atomic UPDATE execution and MVCC to detect version conflicts while allowing concurrent reads.


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

# Appendix – Request Coalescing vs Distributed Lock

---

# Why This Matters

A very common follow-up question in System Design interviews is:

> **"How would you prevent a Cache Stampede?"**

Many candidates immediately answer:

> "I'd use a distributed lock."

While that is not wrong, it's actually incomplete.

The better answer is:

> **"I'd use Request Coalescing. In a distributed environment, I'd implement it using a Distributed Lock."**

Understanding the difference demonstrates senior-level system design knowledge.

---

# The Big Picture

Think of it this way:

```
Request Coalescing

↓

Goal / Strategy

↓

Distributed Lock

↓

One possible implementation
```

Request Coalescing is the **idea**.

Distributed Lock is **one technique** used to achieve that idea.

---

# What is Request Coalescing?

Request Coalescing means:

> **If multiple requests need the same expensive data, only one request should perform the work.**

All other requests wait for that result.

Instead of

```
10,000 Requests

↓

10,000 Database Queries
```

we want

```
10,000 Requests

↓

ONE Database Query

↓

Cache Updated

↓

9,999 Requests Read Cache
```

The goal is to eliminate duplicate work.

---

# Example

Suppose the cache entry for

```
product:123
```

expires.

Immediately afterwards

```
10,000 users

↓

Request Product
```

Without Request Coalescing

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

The database receives

```
10,000 identical queries.
```

This is the Cache Stampede.

---

# With Request Coalescing

```
10,000 Requests

↓

One Request

↓

Database

↓

Redis Updated

↓

Remaining Requests

↓

Redis Hit
```

Only one request performs the expensive work.

Everyone else benefits from it.

---

# How Do We Implement Request Coalescing?

There are two common scenarios.

---

# Scenario 1 – Single Application (One JVM)

Imagine one Spring Boot application.

```
           Spring Boot

        ┌───────────────┐

Request 1

Request 2

Request 3

Request 4

        └───────────────┘
```

Since every request runs inside the same process,

they can coordinate using memory.

No Redis lock is required.

---

# In-Memory Request Coalescing

A common implementation uses

```
ConcurrentHashMap<String, CompletableFuture<T>>
```

The key is the cache key.

Example

```
product:123
```

The value is a Future representing the ongoing database query.

---

# Request Flow

Suppose

```
Request 1
```

arrives first.

```
Request 1

↓

No Future Exists

↓

Create Future

↓

Query Database
```

Now

```
Request 2
```

arrives.

Instead of querying the database,

it simply waits for the existing Future.

```
Request 2

↓

Future Exists

↓

Wait
```

Same for

```
Request 3

↓

Future Exists

↓

Wait
```

Eventually

```
Database Returns Product

↓

Complete Future

↓

Every Waiting Request Receives Result
```

Only **one** database query occurs.

---

# Java Example (Conceptual)

```java
ConcurrentHashMap<String, CompletableFuture<Product>> inFlight = new ConcurrentHashMap<>();

public Product getProduct(String productId) {

    CompletableFuture<Product> future =
        inFlight.computeIfAbsent(productId,
            id -> CompletableFuture.supplyAsync(() -> loadFromDatabase(id)));

    try {
        return future.get();
    } finally {
        inFlight.remove(productId);
    }
}
```

This pattern is often called **Single Flight**.

No distributed lock.

No Redis.

No duplicate database queries.

---

# Advantages

- Extremely fast
- No network calls
- Simple implementation
- Perfect for a single JVM

---

# Limitation

This only works inside **one application instance**.

Now imagine

```
Load Balancer

↓

Server A

Server B

Server C
```

Each server has its own memory.

Server A cannot see Server B's ConcurrentHashMap.

---

# Scenario 2 – Distributed System

Suppose

```
100 Spring Boot Servers
```

receive the same request simultaneously.

```
Load Balancer

↓

Server A

Server B

Server C

...

Server Z
```

Each server sees

```
Cache Miss
```

Without coordination

every server queries the database.

```
Server A

↓

Database

Server B

↓

Database

Server C

↓

Database
```

The cache stampede still occurs.

---

# Distributed Lock

Instead of using local memory,

every server coordinates through a shared locking system.

Usually

- Redis
- ZooKeeper
- etcd
- Consul

Example

```
Server A

↓

Acquire Lock

↓

Success

↓

Database

↓

Update Redis

↓

Release Lock
```

Meanwhile

```
Server B

↓

Acquire Lock

↓

Lock Busy

↓

Wait
```

```
Server C

↓

Acquire Lock

↓

Lock Busy

↓

Wait
```

Once Server A refreshes the cache,

Servers B and C simply read from Redis.

---

# Why is it called "Distributed"?

Because the lock works across

multiple machines.

Unlike ConcurrentHashMap,

the lock is shared by every application instance.

---

# Visual Comparison

## Single Server

```
                One JVM

      Request 1

      Request 2

      Request 3

↓

ConcurrentHashMap

↓

One Database Query
```

---

## Multiple Servers

```
Load Balancer

↓

Server A

Server B

Server C

↓

Redis Lock

↓

One Database Query
```

---

# Request Coalescing vs Distributed Lock

| Request Coalescing | Distributed Lock |
|--------------------|------------------|
| Strategy | Implementation |
| Goal is to avoid duplicate work | Guarantees only one server performs the work |
| Can work in one JVM | Works across many servers |
| May use Futures, Promises, Mutexes | Usually implemented using Redis, ZooKeeper, etcd |
| No network coordination required | Shared coordination required |

---

# Which One Should I Use?

## Single Application

Use

- ConcurrentHashMap
- CompletableFuture
- Mutex
- Semaphore

No distributed lock required.

---

## Multiple Application Servers

Use

- Redis Lock
- ZooKeeper
- etcd
- Consul

to coordinate cache refreshes.

---

# Interview Example

**Question**

> How would you prevent a Cache Stampede?

**Strong Answer**

> "I'd use Request Coalescing so that only one request refreshes the cache while all other requests wait for the result. If the application is running as a single JVM, I can implement this efficiently using a ConcurrentHashMap with CompletableFuture, allowing all requests for the same cache key to share a single in-flight database query. If the application is distributed across multiple servers behind a load balancer, local memory is no longer sufficient because each server has its own state. In that case, I'd use a distributed lock (for example, Redis) so that only one server refreshes the cache while the others wait and subsequently read the refreshed value."

This answer demonstrates an understanding of both the architectural goal and the implementation details.

---

# Common Interview Mistakes

❌ Assuming Request Coalescing and Distributed Lock are identical.

❌ Using Redis locks inside a single JVM application.

❌ Forgetting that each application server has its own memory.

❌ Allowing every server to query the database after a cache miss.

❌ Ignoring duplicate work across application instances.

---

# Key Takeaways

- Request Coalescing is a **strategy** to eliminate duplicate work.
- Distributed Lock is one **implementation** of that strategy.
- In a single JVM, use in-memory coordination such as `ConcurrentHashMap` with `CompletableFuture`.
- In a distributed system, use a shared coordination mechanism like Redis, ZooKeeper, etcd, or Consul.
- Always choose the simplest solution that matches your deployment architecture.
- Interviewers care more about **why** you chose the mechanism than the mechanism itself.

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

# Appendix – Queue vs Publish/Subscribe (Pub/Sub)

---

# Why This Matters

One of the most common interview questions is:

> **"Should this use a Queue or a Pub/Sub model?"**

The answer depends on **how many consumers should process the message.**

Think about one question:

> **"How many systems need this message?"**

That determines the pattern.

---

# Queue (Point-to-Point Messaging)

In a Queue,

one message is processed by **exactly one consumer**.

```
Producer

↓

Queue

↓

Worker A

Worker B

Worker C
```

Suppose one message arrives.

```
Generate Invoice
```

Only one worker processes it.

```
Producer

↓

Queue

↓

Worker B
```

Workers A and C never see the message.

---

# Queue Analogy

Imagine a restaurant kitchen.

Customers place orders.

```
Burger Order

↓

Kitchen Queue
```

Three chefs are available.

```
Chef A

Chef B

Chef C
```

Only **one chef** cooks the burger.

Having all three chefs cook the same burger would be wasteful.

---

# Characteristics of a Queue

- One producer
- One queue
- One message
- One consumer processes each message

This is called **competing consumers**.

Workers compete to process messages.

---

# Typical Queue Use Cases

Queues are ideal when work should happen exactly once.

Examples

- Send Email
- Generate PDF
- Resize Images
- Process Payments
- Create Invoice
- Video Encoding
- Thumbnail Generation

Each task should only be executed once.

---

# Queue Example

```
Upload Image

↓

Queue

↓

Image Worker

↓

Thumbnail Created
```

No other worker should create another thumbnail.

---

# Publish/Subscribe (Pub/Sub)

Pub/Sub is different.

One published event is delivered to **multiple independent consumers**.

```
Producer

↓

Topic

↓

Inventory

↓

Notification

↓

Analytics

↓

Recommendation
```

Every subscriber receives the event.

---

# Pub/Sub Analogy

Think about YouTube.

One creator uploads a video.

```
New Video

↓

YouTube

↓

Subscriber A

Subscriber B

Subscriber C
```

Every subscriber receives the notification.

That is Pub/Sub.

---

# Pub/Sub Example

Customer places an order.

```
Order Created
```

Many services care.

```
Order Event

↓

Inventory

↓

Email

↓

Analytics

↓

Recommendations

↓

Fraud Detection
```

Each service independently processes the event.

---

# Characteristics of Pub/Sub

- One producer
- One topic
- Many subscribers
- Every subscriber receives the message

Subscribers are independent.

One service failing does not stop the others.

---

# Queue vs Pub/Sub

## Queue

```
Message

↓

Queue

↓

Worker A
```

Only one worker receives it.

---

## Pub/Sub

```
Message

↓

Topic

↓

Service A

Service B

Service C

Service D
```

Every service receives the message.

---

# Visual Comparison

## Queue

```
             Queue

Producer

↓

Queue

↓

Worker 1

Worker 2

Worker 3

↓

Only ONE Worker Processes Message
```

---

## Pub/Sub

```
Producer

↓

Topic

↓

Inventory

Notification

Analytics

Fraud Detection

Recommendation

↓

ALL Consumers Receive Event
```

---

# Real Example – Queue

Customer uploads a video.

Should

```
Thumbnail Worker
```

run once

or

five times?

Only once.

Queue.

---

# Real Example – Pub/Sub

Customer places an order.

Should

Inventory update?

Yes.

Should

Analytics update?

Yes.

Should

Email send?

Yes.

Should

Recommendation update?

Yes.

Every service needs the event.

Pub/Sub.

---

# Which Technologies Support Which?

| Technology | Queue | Pub/Sub |
|------------|-------|----------|
| RabbitMQ | ✅ | ✅ |
| Kafka | ⚠️ (via consumer groups) | ✅ |
| Amazon SQS | ✅ | ❌ |
| Amazon SNS | ❌ | ✅ |
| Google Pub/Sub | ❌ | ✅ |

---

# Kafka is Interesting

Kafka is usually used as Pub/Sub.

```
Orders Topic

↓

Inventory

↓

Email

↓

Analytics
```

Each consumer group gets every event.

However,

inside one consumer group,

Kafka behaves like a Queue.

Example

```
Inventory Consumer Group

Worker A

Worker B

Worker C
```

Only one worker processes each partition's message.

This allows Kafka to support both patterns.

---

# Kafka Consumer Groups

Suppose

```
Orders Topic
```

Three consumer groups exist.

```
Inventory Group

Notification Group

Analytics Group
```

Each group receives every message.

Inside

```
Inventory Group
```

there may be

```
Worker 1

Worker 2

Worker 3
```

Only one worker processes a given message.

So Kafka provides

```
Topic

↓

Consumer Groups

↓

Workers
```

Pub/Sub between groups.

Queue inside each group.

---

# Interview Decision Tree

Ask yourself

> **Should multiple independent systems process this event?**

If YES

↓

Pub/Sub

---

Should only one worker perform this task?

↓

Queue

---

# Queue Examples

- Email Sending
- Invoice Generation
- Payment Processing
- Thumbnail Creation
- PDF Generation
- Image Compression
- Video Encoding

One worker should complete the task.

---

# Pub/Sub Examples

- Order Created
- User Registered
- Payment Completed
- Product Updated
- Shipment Delivered

Multiple systems react independently.

---

# Common Interview Questions

### Why Queue?

Only one worker should perform the task.

Avoid duplicate work.

---

### Why Pub/Sub?

Multiple independent services need the same event.

Maintain loose coupling.

---

### Can Kafka behave like both?

Yes.

Kafka Topics provide Pub/Sub.

Kafka Consumer Groups provide Queue-like processing within each subscriber.

---

# Common Mistakes

❌ Using Queue when multiple systems require the same event.

❌ Using Pub/Sub when only one worker should process the task.

❌ Thinking Kafka is "just a queue."

❌ Forgetting Consumer Groups.

---

# Key Takeaways

- Queue = One Message → One Consumer.
- Pub/Sub = One Message → Many Independent Consumers.
- Queue is ideal for background jobs.
- Pub/Sub is ideal for event-driven architectures.
- Kafka supports both using Topics and Consumer Groups.
- RabbitMQ supports both messaging patterns.
- Amazon SQS is primarily a Queue service.
- Amazon SNS is a Pub/Sub service.

---

# Interview Tip

A simple rule to remember:

Ask yourself:

> **"How many systems should process this message?"**

If the answer is:

**One**

→ Queue

If the answer is:

**Many**

→ Pub/Sub

This single question is usually enough to choose the correct messaging pattern during a system design interview.

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

---

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

# Chapter 0 – System Design Building Blocks & Decision Framework

---

# Why This Chapter Matters

After designing a few systems, you'll notice something interesting.

Most large-scale distributed systems are built using the same set of building blocks.

Examples

- Redis
- SQL
- NoSQL
- Kafka
- Load Balancer
- API Gateway
- Cache
- Workers
- Read Replicas
- Sharding

The real interview challenge is **not knowing these technologies**.

It is knowing **when to use each one and why**.

Interviewers are constantly asking questions like:

- Why Redis instead of the database?
- Why Kafka instead of synchronous APIs?
- Why SQL instead of NoSQL?
- Why Read Replicas?
- Why Sharding?

This chapter provides a decision framework that can be applied to almost every system design interview.

---

# The System Design Thought Process

Whenever a request enters the system, mentally walk through the request path.

```
Client

↓

Load Balancer?

↓

API Gateway?

↓

Application Service?

↓

Cache?

↓

Database?

↓

Queue?

↓

Workers?

↓

External Services?
```

Every component should exist because it solves a specific problem.

---

# 1. Choosing the Right Storage

Ask yourself:

> What kind of data am I storing?

| Requirement | Recommended Choice | Why |
|-------------|--------------------|-----|
| ACID Transactions | SQL | Strong consistency and transactions |
| Flexible Schema | NoSQL | Schema evolution and horizontal scalability |
| Fast Reads | Redis | In-memory performance |
| Large Files | Object Storage (S3, GCS, Azure Blob) | Optimized for blobs |
| Full-Text Search | Elasticsearch / OpenSearch | Inverted indexes |
| Analytics | Data Warehouse | Optimized for OLAP queries |

---

# 2. Cache Decision Framework

Ask

```
Is this data read frequently?

↓

YES

↓

Can slightly stale data be tolerated?

↓

YES

↓

Use Redis
```

Typical Redis use cases

- User Profiles
- Product Catalog
- URL Mapping
- Sessions
- Leaderboards
- Feature Flags
- Frequently Accessed Configuration

Avoid caching

- Bank Balances
- Payment Authorization
- Inventory Counts (unless carefully designed)
- Critical transactional data

Redis improves performance.

It should not be the source of truth.

---

# 3. Queue Decision Framework

Ask

```
Does the user need to wait for this operation?

↓

YES

↓

Keep it synchronous

NO

↓

Use Kafka / Queue
```

Examples

Asynchronous

- Email
- SMS
- Push Notifications
- Analytics
- Image Processing
- Video Encoding
- Audit Logging

Synchronous

- Login
- Payment Authorization
- OTP Validation
- Inventory Check
- User Registration

---

# 4. SQL vs NoSQL Decision Framework

Instead of memorizing databases,

ask these questions.

```
Need Transactions?

↓

SQL
```

```
Need JOINs?

↓

SQL
```

```
Need Flexible Schema?

↓

NoSQL
```

```
Need Massive Horizontal Scaling?

↓

NoSQL
```

General guideline

SQL

- Orders
- Payments
- Banking
- Users

NoSQL

- Product Catalog
- Activity Feed
- Shopping Cart
- User Preferences
- Event Storage

---

# 5. Redis Decision Framework

Redis is commonly used for

- Caching
- Sessions
- Rate Limiting
- Counters
- Leaderboards
- Distributed Locks
- Pub/Sub
- Temporary Data

Redis is ideal when

- Low latency is required
- Data can be regenerated
- Data changes less frequently than it is read

---

# 6. Kafka Decision Framework

Ask

```
Does this work have to happen immediately?

↓

NO

↓

Kafka
```

Kafka is commonly used for

- Notifications
- Audit Logs
- Analytics
- Recommendation Systems
- Search Index Updates
- ETL Pipelines
- Event Streaming

Kafka should not be used for

- Immediate user responses
- Payment Authorization
- Login Authentication

---

# 7. Read Replica Decision Framework

Ask

```
Does the system receive significantly more reads than writes?

↓

YES

↓

Use Read Replicas
```

Good examples

- URL Shortener
- Product Catalog
- News Feed
- Search Results

Writes

↓

Primary Database

Reads

↓

Read Replicas

---

# 8. Sharding Decision Framework

Ask

```
Will a single database eventually become too large?

↓

YES

↓

Shard the data
```

Good shard keys

- UserId
- TenantId
- CustomerId
- OrderId
- URL Hash

Avoid

- Timestamp
- Sequential IDs (can create hotspots)

---

# 9. CDN Decision Framework

Ask

```
Is this static content?

↓

YES

↓

CDN
```

Examples

- Images
- CSS
- JavaScript
- Videos
- Documents
- Downloads

Some URL redirects can also be cached at the CDN layer.

---

# 10. Object Storage Decision Framework

Ask

```
Am I storing large files?

↓

YES

↓

Object Storage
```

Examples

- Images
- Videos
- PDFs
- Audio
- Backups

Best Practice

Store

- Metadata → SQL

Store

- Actual File → Object Storage

---

# 11. Worker Pattern

Whenever you encounter a long-running operation,

think

```
Queue

↓

Workers
```

Examples

- Email Sending
- Image Resizing
- Video Transcoding
- Report Generation
- Machine Learning Inference
- Thumbnail Generation

Workers allow the API to respond quickly while processing continues in the background.

---

# 12. Event-Driven Architecture

Whenever multiple systems need to react to the same business event,

consider publishing an event.

Example

```
Order Created

↓

Payment Service

↓

Inventory Service

↓

Notification Service

↓

Analytics Service

↓

Recommendation Service
```

Instead of

```
Order Service

↓

Calls Every Service Directly
```

Benefits

- Loose coupling
- Independent scaling
- Easier extensibility

---

# 13. Reliability Checklist

Every distributed system should consider

- Retries
- Exponential Backoff
- Timeouts
- Circuit Breakers
- Idempotency
- Dead Letter Queue (DLQ)
- Monitoring
- Alerting

Ask yourself

```
What happens if this component fails?
```

---

# 14. Scalability Checklist

Can each layer scale independently?

```
API Servers

↓

Cache

↓

Database

↓

Kafka

↓

Workers

↓

Search

↓

Storage
```

Avoid tightly coupling scaling decisions.

---

# 15. Monitoring Checklist

Every production system should monitor

API

- Request Rate
- Latency
- Error Rate

Database

- Query Latency
- Connection Pool
- Slow Queries

Redis

- Cache Hit Ratio
- Memory Usage

Kafka

- Consumer Lag
- Queue Depth

Workers

- Success Rate
- Retry Count
- DLQ Size

Infrastructure

- CPU
- Memory
- Disk
- Network

---

# The Golden Questions

Almost every system design interview can be approached by asking these questions.

### 1. Is the workload read-heavy or write-heavy?

This determines

- Caching
- Read Replicas
- Database strategy

---

### 2. Does the user need to wait?

If not,

move the work to

- Kafka
- Queue
- Workers

---

### 3. Do I need strong consistency?

If yes

↓

SQL

If eventual consistency is acceptable

↓

Asynchronous processing

---

### 4. Can I cache this?

Frequently read data usually belongs in Redis.

---

### 5. Can this be asynchronous?

Background processing improves responsiveness and scalability.

---

### 6. What happens if this component fails?

Always discuss

- Retry
- Fallback
- Circuit Breaker
- Monitoring

---

### 7. How will this scale?

Think independently about

- API Servers
- Cache
- Database
- Workers
- Messaging

---

### 8. What are the trade-offs?

Every design decision has advantages and disadvantages.

Interviewers value engineers who can explain both.

---

# Decision Cheat Sheet

| Requirement | Recommended Building Block |
|-------------|----------------------------|
| Fast repeated reads | Redis |
| Persistent transactional data | SQL |
| Flexible schema | NoSQL |
| Background processing | Kafka / Queue |
| Large files | Object Storage |
| Full-text search | Elasticsearch |
| Static content | CDN |
| High read traffic | Read Replicas |
| Massive datasets | Sharding |
| Long-running work | Workers |
| Reliable event publishing | Transactional Outbox |
| Distributed consistency | Saga |
| Strong distributed transaction | Two-Phase Commit (2PC) |
| Rate limiting | Redis + Token Bucket |
| Analytics | Kafka + Consumers |

---

# Final Takeaways

System design is not about memorizing architectures.

It is about making informed engineering decisions based on the system's requirements.

For every component you introduce, ask yourself:

- Why do I need it?
- What problem does it solve?
- What trade-offs does it introduce?
- How does it scale?
- What happens if it fails?

If you can answer these questions confidently, you'll be able to design and defend almost any distributed system in a senior backend interview.

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

# Appendix – Saga Pattern

---

# Why Does the Saga Pattern Exist?

Suppose you're building Amazon.

A customer places an order.

What happens?

```
Place Order

↓

Payment

↓

Inventory

↓

Shipping

↓

Notification
```

Looks simple.

But here's the problem.

Each step belongs to a different microservice.

```
Order Service

Payment Service

Inventory Service

Shipping Service
```

Each service owns **its own database**.

---

# Why Can't We Use One Transaction?

In a monolith we simply write

```sql
BEGIN TRANSACTION

Insert Order

Update Inventory

Charge Payment

Commit
```

Easy.

Everything succeeds or everything rolls back.

---

In microservices

```
Order DB

Payment DB

Inventory DB

Shipping DB
```

There is **no single database transaction**.

Each service has its own database.

---

# The Problem

Suppose

```
Order Created ✅

↓

Payment Successful ✅

↓

Inventory Failed ❌
```

Now what?

Customer already paid.

Order already exists.

Inventory doesn't.

The system is inconsistent.

---

# Distributed Transactions?

One solution is

```
Two Phase Commit (2PC)
```

We'll discuss it briefly.

Coordinator

↓

Payment

↓

Inventory

↓

Shipping

↓

Commit

Sounds nice.

Reality?

- Slow
- Blocking
- Poor scalability
- Difficult across cloud services
- Rarely used in internet-scale systems

Most modern architectures avoid 2PC.

---

# Enter Saga

Instead of one giant transaction,

split the workflow into

multiple **local transactions**.

```
Order

↓

Payment

↓

Inventory

↓

Shipping
```

Each service commits independently.

---

# But What if Something Fails?

Suppose

```
Order Created ✅

↓

Payment Charged ✅

↓

Inventory Failed ❌
```

Instead of rolling back a database transaction,

we perform

**Compensating Transactions.**

---

# Compensating Transaction

Think of it as

> Undoing business actions.

Instead of

```
ROLLBACK
```

we execute

```
Refund Payment

↓

Cancel Order
```

Business consistency is restored.

---

# Example

Customer places an order.

```
Order Service

↓

Create Order

↓

SUCCESS
```

Payment

```
Charge Credit Card

↓

SUCCESS
```

Inventory

```
Reserve Stock

↓

FAILED
```

Now execute

```
Refund Payment

↓

Cancel Order
```

Customer gets money back.

System becomes consistent.

---

# Saga Flow

```
Order

↓

Payment

↓

Inventory

↓

Shipping

↓

Complete
```

Failure

```
Order

↓

Payment

↓

Inventory ❌

↓

Refund Payment

↓

Cancel Order
```

Notice

We don't rollback databases.

We perform business actions.

---

# Why Is This Better?

Each service

- owns its database
- commits independently

No distributed locking.

No coordinator locking resources.

Better scalability.

---

# Two Types of Saga

There are two implementations.

---

# 1. Choreography

No central coordinator.

Each service publishes events.

Example

```
Order Created

↓

Payment Service

↓

Payment Completed

↓

Inventory Service

↓

Inventory Reserved

↓

Shipping Service
```

Each service reacts independently.

---

# Choreography Diagram

```
Order Service

↓

OrderCreated Event

↓

Payment Service

↓

PaymentCompleted Event

↓

Inventory Service

↓

InventoryReserved Event

↓

Shipping Service
```

Nobody controls the workflow.

Events drive everything.

---

# Advantages

- Loose coupling
- Easy to add new services
- No central coordinator

---

# Disadvantages

Eventually

everyone talks to everyone.

```
Order

↓

Payment

↓

Inventory

↓

Shipping

↓

Notification

↓

Fraud

↓

Analytics

↓

Recommendation
```

The event graph becomes difficult to understand.

This is called

**Event Spaghetti.**

---

# 2. Orchestration

One service coordinates everything.

Usually called

Saga Orchestrator.

```
Saga

↓

Payment

↓

Inventory

↓

Shipping
```

The Saga tells each service

what to do next.

---

# Orchestration Example

```
Customer Places Order

↓

Saga

↓

Charge Payment

↓

SUCCESS

↓

Reserve Inventory

↓

FAILED

↓

Refund Payment

↓

Cancel Order

↓

Finish
```

The Saga owns the workflow.

---

# Advantages

- Easy to understand
- Centralized workflow
- Easier debugging
- Easier monitoring

---

# Disadvantages

The Saga becomes another service

that must be maintained.

---

# Choreography vs Orchestration

| Choreography | Orchestration |
|--------------|---------------|
| Event-driven | Central coordinator |
| Services react to events | Saga tells services what to do |
| Loosely coupled | Easier to understand |
| Harder debugging | Easier monitoring |
| No central point | Coordinator becomes critical |

---

# Which One Should I Use?

Small workflow

↓

Choreography

Large business process

↓

Orchestration

Most enterprises eventually adopt orchestration.

---

# Example – Flight Booking

Customer books vacation.

Need

- Flight
- Hotel
- Rental Car

Workflow

```
Reserve Flight

↓

Reserve Hotel

↓

Reserve Car
```

Suppose

Car Reservation fails.

Compensation

```
Cancel Hotel

↓

Cancel Flight
```

Everything returns to original state.

---

# Another Example – Amazon

Customer places order.

```
Create Order

↓

Charge Card

↓

Reserve Inventory

↓

Book Shipment
```

Shipping fails.

Compensation

```
Release Inventory

↓

Refund Card

↓

Cancel Order
```

---

# Is Saga ACID?

No.

Saga provides

**Eventual Consistency.**

For a short period

```
Order Exists

Payment Charged

Inventory Missing
```

Eventually

compensation restores consistency.

---

# Idempotency

Compensating actions

must be idempotent.

Example

```
Refund Payment
```

Suppose retry occurs.

Do not refund twice.

Always use

```
Transaction ID

Order ID
```

to detect duplicates.

---

# Retry

Suppose

Inventory Service

times out.

Retry

```
1 sec

↓

2 sec

↓

4 sec
```

Only compensate

after retries are exhausted.

---

# Monitoring

Track

- Saga ID
- Current Step
- Completed Steps
- Failed Step
- Compensation Status

Otherwise

debugging becomes difficult.

---

# Interview Example

Question

Design an e-commerce checkout.

Strong answer

```
Order Service

↓

Saga

↓

Payment

↓

Inventory

↓

Shipping

↓

Notification
```

If Shipping fails

```
Release Inventory

↓

Refund Payment

↓

Cancel Order
```

This maintains business consistency.

---

# Common Interview Mistakes

❌ Using distributed transactions everywhere

❌ Forgetting compensating actions

❌ No retries

❌ No idempotency

❌ Confusing database rollback with business rollback

---

# Interview Tips

Don't say

> "Saga rolls back the transaction."

Instead say

> "Saga performs compensating transactions that logically undo previously completed business operations."

That distinction is extremely important.

---

# Key Takeaways

- Microservices cannot easily share one database transaction.
- Saga replaces distributed transactions with a sequence of local transactions.
- Every successful step commits independently.
- Failures are handled through compensating transactions.
- Saga provides eventual consistency.
- Choreography uses events.
- Orchestration uses a central coordinator.
- Compensation is a business operation, not a database rollback.
- Retries and idempotency are essential.
- Saga is widely used in payment, booking, logistics, and e-commerce systems.


# Appendix – Understanding Two-Phase Commit (2PC) vs Saga Using an Amazon Checkout Example

---

# Why This Matters

One of the most common senior backend interview questions is:

> **"Why don't companies like Amazon simply use one transaction across all microservices?"**

The answer lies in understanding why **Two-Phase Commit (2PC)** doesn't scale well for distributed systems and why modern architectures prefer the **Saga Pattern**.

This appendix explains both using a realistic Amazon checkout example.

---

# The Scenario

A customer clicks

```
Place Order
```

Behind the scenes, multiple services are involved.

```
Customer

↓

Order Service

↓

Payment Service

↓

Inventory Service

↓

Shipping Service
```

Each service owns **its own database**.

```
Order Service
     ↓
 OrderDB

Payment Service
     ↓
 PaymentDB

Inventory Service
     ↓
 InventoryDB

Shipping Service
     ↓
 ShippingDB
```

This is a typical microservice architecture.

---

# The Business Requirement

The customer expects one thing:

> Either everything succeeds

or

> Nothing happens.

In other words,

- Order should exist
- Payment should be charged
- Inventory should be reserved
- Shipment should be created

OR

None of them should happen.

---

# How Would Two-Phase Commit (2PC) Solve This?

A central coordinator manages every participant.

```
                Coordinator

         /        |        \

     Order    Payment   Inventory

                     \

                  Shipping
```

The coordinator is responsible for making sure everyone commits or everyone rolls back.

---

# Phase 1 – Prepare Phase

The coordinator asks every service:

> **"Can you commit?"**

Notice something important.

Nobody commits yet.

Everyone simply prepares.

---

## Step 1 – Order Service

```
BEGIN TRANSACTION

Insert Order

DO NOT COMMIT
```

Order Service replies

```
READY
```

The transaction remains open.

---

## Step 2 – Payment Service

```
BEGIN TRANSACTION

Charge Credit Card

DO NOT COMMIT
```

Payment Service replies

```
READY
```

Again,

the transaction remains open.

---

## Step 3 – Inventory Service

```
BEGIN TRANSACTION

Reserve iPhone

DO NOT COMMIT
```

Inventory replies

```
READY
```

Still no commit.

---

## Step 4 – Shipping Service

```
BEGIN TRANSACTION

Create Shipment

DO NOT COMMIT
```

Shipping replies

```
READY
```

---

# At This Point

Every service has completed its work,

but **nothing has committed yet.**

```
Order

Prepared

↓

Payment

Prepared

↓

Inventory

Prepared

↓

Shipping

Prepared
```

Everything is waiting.

---

# What is Actually Locked?

Suppose Inventory contains

```
iPhone

Quantity = 5
```

Inventory executes

```sql
UPDATE Inventory
SET quantity = 4
WHERE product = 'iPhone';
```

The database locks that row.

Normally,

locks are released after

```
COMMIT
```

or

```
ROLLBACK
```

But neither has happened yet.

The transaction is still waiting.

Therefore,

the row remains locked.

---

# Another Customer Arrives

A second customer now wants to buy the same iPhone.

```
Customer 2

↓

Buy iPhone

↓

Inventory Database
```

The database replies

```
Row is locked.

Please wait.
```

Customer 2 is blocked.

---

A third customer arrives.

```
Customer 3

↓

Buy iPhone
```

Blocked again.

Now imagine

10,000 customers.

Thousands of rows remain locked while waiting for the coordinator.

---

# Payment is Also Waiting

Payment Service has already charged the customer's card.

However,

the transaction is still open.

Suppose customer support checks payment status.

```
Payment Status?

↓

Transaction Still Open
```

Again,

everyone waits.

---

# Then the Worst Case Happens

Suppose the coordinator crashes.

```
Coordinator

💥
```

Now every service asks

```
Should I Commit?

Should I Rollback?

I don't know.
```

Since nobody knows the final decision,

they cannot release their transactions.

Everything remains locked.

---

# Why Can't Services Decide Independently?

Suppose Inventory decides

```
I'll Commit.
```

Later,

Payment decides

```
I'll Rollback.
```

Now

```
Inventory Reserved

Payment Failed
```

Incorrect.

Now suppose Payment commits

but Inventory rolls back.

```
Customer Charged

No Inventory Reserved
```

Also incorrect.

Therefore,

every participant must wait.

---

# Why is Two-Phase Commit Called a Blocking Protocol?

Because everyone waits until the coordinator makes the final decision.

```
Order

Waiting

↓

Payment

Waiting

↓

Inventory

Waiting

↓

Shipping

Waiting
```

No one can continue.

---

# Why Does This Hurt Performance?

Imagine Shipping takes

```
5 seconds
```

During those five seconds,

Order Database

↓

holds its transaction open.

Payment Database

↓

holds its transaction open.

Inventory Database

↓

holds its transaction open.

Shipping Database

↓

holds its transaction open.

Now imagine Black Friday.

```
100,000 Orders

↓

Prepared

↓

Waiting

↓

Prepared

↓

Waiting
```

Soon,

the databases contain

- Thousands of open transactions
- Thousands of locked rows
- Long-running queries
- Timeouts
- Slow applications

This is why 2PC does not scale well for internet-scale systems.

---

# How Saga Solves the Same Problem

Instead of one distributed transaction,

Saga performs a sequence of **local transactions**.

Each service commits immediately.

---

## Order Service

```
Create Order

↓

COMMIT
```

Done.

No waiting.

No lock held.

---

## Payment Service

```
Charge Card

↓

COMMIT
```

Done.

Again,

no waiting.

---

## Inventory Service

```
Reserve Stock

↓

FAILED
```

Oops.

Now the Saga executes compensating actions.

---

# Compensation

Instead of rolling back SQL,

Saga performs business operations.

```
Refund Card

↓

Cancel Order
```

Notice

Payment was already committed.

We are not rolling back the database.

We are performing a new business operation that logically undoes the previous one.

---

# Timeline Comparison

## Two-Phase Commit

```
Order

↓

WAIT

↓

Payment

↓

WAIT

↓

Inventory

↓

WAIT

↓

Shipping

↓

WAIT

↓

Commit Everything
```

Every participant waits.

---

## Saga

```
Order

↓

Commit

↓

Payment

↓

Commit

↓

Inventory

↓

Fails

↓

Refund Payment

↓

Cancel Order
```

Every service commits immediately.

No long-running distributed transaction exists.

---

# What About Temporary Inconsistency?

Suppose the customer checks their credit card immediately.

They may briefly see

```
Card Charged
```

A few seconds later,

Saga performs

```
Refund Payment
```

Eventually,

everything becomes consistent.

This is called

**Eventual Consistency.**

For a short period,

the system may be temporarily inconsistent,

but it eventually reaches the correct state.

---

# Why Modern Companies Prefer Saga

Large distributed systems prioritize

- Scalability
- High Availability
- Throughput
- Independent Deployments

Holding database transactions open across multiple services, networks, and regions simply does not scale well.

Saga accepts temporary inconsistency in exchange for much better scalability.

---

# Two-Phase Commit vs Saga

| Two-Phase Commit (2PC) | Saga |
|-------------------------|------|
| One distributed transaction | Multiple local transactions |
| Coordinator required | Coordinator optional (orchestrator) |
| Participants wait before committing | Each service commits immediately |
| Long-running database locks | No long-running distributed locks |
| Strong consistency | Eventual consistency |
| Blocking protocol | Non-blocking approach |
| Difficult to scale | Designed for microservices |
| Rollback database transaction | Execute compensating business actions |

---

# Real-World Analogy

Imagine four people signing a contract.

## Two-Phase Commit

Everyone signs,

but nobody is allowed to submit the paperwork until every single person agrees.

If one person disappears,

everyone else waits indefinitely with signed papers in hand.

---

## Saga

Each person submits their paperwork immediately.

Later,

if someone discovers a problem,

the previously completed steps are reversed through agreed-upon corrective actions.

Nobody waits.

---

# Interview Tips

If the interviewer asks

> **"Why doesn't Amazon use Two-Phase Commit?"**

A strong answer is:

> "Two-Phase Commit requires every participating service to keep its local transaction open during the prepare phase until the coordinator decides whether to commit or abort. That means database rows and resources remain locked, reducing concurrency and making the system vulnerable to blocking if the coordinator or network fails. Modern distributed systems generally prefer the Saga Pattern, where each service commits its local transaction immediately and compensating transactions restore business consistency if a later step fails."

---

# Key Takeaways

- Every microservice owns its own database.
- There is no single transaction spanning multiple databases.
- Two-Phase Commit keeps transactions open until every participant agrees.
- Open transactions hold locks and reduce concurrency.
- If the coordinator fails, participants remain blocked.
- Saga replaces one distributed transaction with multiple local transactions.
- Each service commits independently.
- Failures are handled using compensating business actions rather than SQL rollbacks.
- Saga provides eventual consistency instead of immediate consistency.
- Modern internet-scale companies typically prefer Saga over Two-Phase Commit for distributed workflows.


# Appendix – Saga vs Two-Phase Commit (2PC)

---

# Why This Matters

One of the most common senior backend interview questions is:

> **"What's the difference between Saga and Two-Phase Commit (2PC)?"**

Many engineers answer:

> "Saga is another distributed transaction."

While this is a common way to describe it, it is not the most precise explanation.

A better way to think about it is:

> **Both Saga and 2PC solve the same business problem—maintaining consistency across multiple services—but they solve it in completely different ways.**

---

# The Problem They Solve

Suppose you're building Amazon.

A customer places an order.

Several services participate:

```
Order Service

↓

Payment Service

↓

Inventory Service

↓

Shipping Service
```

Each service owns its own database.

```
Order Service
    ↓
 OrderDB

Payment Service
    ↓
 PaymentDB

Inventory Service
    ↓
 InventoryDB

Shipping Service
    ↓
 ShippingDB
```

The business requirement is simple:

Either

- Order is created
- Payment is charged
- Inventory is reserved
- Shipment is created

OR

None of them should happen.

The challenge is:

There is no single database transaction spanning all these databases.

---

# Three Ways to Maintain Consistency

There are three common approaches.

---

# 1. Local Transaction

Everything lives in one database.

```
Order

↓

Payment

↓

Inventory

↓

COMMIT
```

Characteristics

- One database
- One transaction
- ACID guarantees
- Simple rollback

This is the easiest scenario.

---

# 2. Distributed Transaction (Two-Phase Commit)

Now imagine every service owns its own database.

Instead of one database,

we coordinate multiple databases using one global transaction.

```
Coordinator

↓

Order DB

↓

Payment DB

↓

Inventory DB

↓

Shipping DB
```

All databases participate in one distributed transaction.

---

## How It Works

Each participant

- Begins a local transaction
- Performs its work
- Waits for the coordinator

```
Prepare

↓

WAIT

↓

Commit
```

Nobody commits until everyone agrees.

If everyone says "Ready"

↓

Commit.

If anyone fails

↓

Rollback everyone.

---

## Characteristics

- One global transaction
- Strong consistency
- Coordinator required
- Long-running locks
- Blocking protocol
- Poor scalability

---

# 3. Saga Pattern

Saga takes a completely different approach.

Instead of one large transaction,

it performs multiple **local transactions**.

```
Order

↓

COMMIT

↓

Payment

↓

COMMIT

↓

Inventory

↓

COMMIT
```

Each service commits immediately.

There is no global transaction.

---

# What Happens If Something Fails?

Suppose

```
Order Created

✓

↓

Payment Charged

✓

↓

Inventory Failed

✗
```

Instead of rolling back SQL,

Saga executes

**Compensating Transactions.**

```
Refund Payment

↓

Cancel Order
```

Notice

We are not rolling back the database.

We are performing new business operations that logically undo previous work.

---

# Are Both Distributed Transaction Patterns?

This is where terminology becomes important.

Many blogs say

> "Saga is a distributed transaction pattern."

While this is commonly accepted,

the more precise explanation is:

> **Saga is a distributed consistency pattern that replaces traditional distributed transactions.**

Why?

Because Saga does **not** maintain one distributed transaction.

Instead,

it executes

- Multiple local transactions
- Compensation on failure

There is no global commit.

---

# The Key Difference

## Two-Phase Commit

```
One Global Transaction

↓

Every Service Waits

↓

Commit Together
```

---

## Saga

```
Many Local Transactions

↓

Each Commits Immediately

↓

Failure?

↓

Compensate Previous Steps
```

---

# Comparison

| Feature | Two-Phase Commit (2PC) | Saga |
|----------|-------------------------|-------|
| Goal | Maintain consistency across services | Maintain consistency across services |
| Transaction Type | One distributed transaction | Multiple local transactions |
| Commit | All services commit together | Each service commits independently |
| Rollback | Database rollback | Compensating business actions |
| Consistency | Strong consistency | Eventual consistency |
| Coordinator | Required | Optional (depends on implementation) |
| Long-running locks | Yes | No |
| Scalability | Lower | Higher |
| Availability | Lower during failures | Higher |
| Typical Use | Traditional enterprise systems | Modern microservices |

---

# Real-World Analogy

Imagine four friends are moving furniture.

---

## Two-Phase Commit

Everyone is carrying one large table.

```
Person A

Person B

Person C

Person D
```

Nobody can put the table down until everyone is ready.

If one person slips,

everyone must continue holding the table.

Everything waits.

---

## Saga

Instead,

each person carries their own chair.

```
Person A

✓ Finished

↓

Person B

✓ Finished

↓

Person C

✗ Failed
```

Now simply undo the previous work.

```
Take Chair Back

↓

Return Chair

↓

Done
```

Nobody had to wait.

---

# Which One Do Modern Companies Prefer?

Large internet companies like

- Amazon
- Netflix
- Uber
- Airbnb

typically prefer

**Saga**

because they optimize for

- Scalability
- High Availability
- Independent Deployments
- Loose Coupling

Strong consistency across dozens of microservices is often too expensive.

Temporary inconsistency with compensation is usually an acceptable trade-off.

---

# Interview Question

**Question**

> Is Saga a distributed transaction?

A strong answer is:

> "Both Saga and Two-Phase Commit address consistency across multiple services. Two-Phase Commit achieves this using a single distributed transaction coordinated across all participants, providing strong consistency but introducing blocking and long-lived locks. Saga takes a different approach by executing a sequence of local transactions that commit independently. If a later step fails, compensating business actions restore consistency. So rather than being a traditional distributed transaction, Saga is better described as a distributed consistency pattern that replaces distributed transactions in modern microservice architectures."

---

# Key Takeaways

- Both Saga and Two-Phase Commit solve the same business problem: maintaining consistency across multiple services.
- Two-Phase Commit uses one distributed transaction coordinated across all participants.
- Saga uses multiple independent local transactions.
- Two-Phase Commit relies on database rollback.
- Saga relies on compensating business actions.
- Two-Phase Commit provides strong consistency.
- Saga provides eventual consistency.
- Two-Phase Commit scales poorly because participants hold transactions open.
- Saga scales well because every service commits immediately.
- Modern distributed systems generally prefer Saga over Two-Phase Commit.

---

# Rule to Remember

Think about the difference like this:

**Two-Phase Commit**

> "Nobody commits until everyone agrees."

**Saga**

> "Everyone commits independently. If something later fails, undo the previous business operations."

This single mental model is often enough to answer most interview questions on the topic.

Saga is not a distributed transaction pattern in a traditional sense. Two-Phase Commit is a distributed transaction protocol because it coordinates one logical transaction across multiple participants. Saga does not create a global transaction. Instead, it coordinates a sequence of independent local transactions and uses compensating transactions to restore business consistency if a later step fails. So Saga is better described as a distributed consistency or workflow pattern rather than a distributed transaction.


# Notification Service – High-Level Design (HLD)




---

# Objective

Design a highly scalable Notification Service capable of sending

- Email
- SMS
- Push Notifications

Requirements

- High Availability
- Low Latency
- Horizontal Scalability
- Retry Support
- Delivery Tracking
- User Preferences
- Idempotency
- Fault Tolerance

---


# Notification Service – Interview Preparation (Before High-Level Design)

---

# Problem Statement

Design a highly scalable Notification Service that supports

- Email
- SMS
- Push Notifications

The system should be reliable, scalable, and fault tolerant.

Before drawing the architecture, gather requirements and establish the design assumptions.

---

# Step 1 – Clarifying Questions

Before jumping into the solution, ask clarifying questions.

This demonstrates structured thinking and prevents incorrect assumptions.

---

## Functional Questions

### 1. Which notification channels should be supported?

- Email
- SMS
- Push Notifications
- In-App Notifications

Assumption

We'll support

- Email
- SMS
- Push

---

### 2. Should notifications be sent immediately?

Possible answers

- Immediate
- Scheduled
- Both

Assumption

Support both real-time and scheduled notifications.

---

### 3. Should failed notifications be retried?

Assumption

Yes.

Retries are required.

---

### 4. Should duplicate notifications be prevented?

Assumption

Yes.

The system should be idempotent.

---

### 5. Should users be able to configure notification preferences?

Examples

- Disable marketing emails
- Disable SMS
- Quiet hours

Assumption

Yes.

---

### 6. Should delivery status be tracked?

Possible statuses

- QUEUED
- PROCESSING
- SENT
- FAILED

Assumption

Yes.

---

### 7. Should notification templates be supported?

Example

```
Hi {{name}}

Your order {{orderId}} has shipped.
```

Assumption

Yes.

---

## Non-Functional Questions

### Expected traffic?

Assumption

100 Million Notifications / Day

---

### Latency?

Assumption

Notification should be accepted within a few hundred milliseconds.

Actual delivery may occur asynchronously.

---

### Availability?

Assumption

High Availability (99.9% or higher)

---

### Scalability?

The system should support horizontal scaling.

---

### Reliability?

No notification should be lost.

---

### Consistency?

Eventual consistency is acceptable.

---

# Step 2 – Functional Requirements

The system should support

- Send Email
- Send SMS
- Send Push Notifications
- Schedule Notifications
- Retry Failed Notifications
- Notification Templates
- User Preferences
- Delivery Tracking
- Notification History

---

# Step 3 – Non-Functional Requirements

The system should provide

- High Availability
- Low Latency
- Horizontal Scalability
- Fault Tolerance
- Reliable Delivery
- Idempotent Processing
- Monitoring & Observability
- Eventual Consistency

---

# Step 4 – Capacity Estimation

Assume

```
100 Million Notifications / Day
```

Average requests per second

```
100,000,000

÷

86,400

≈ 1,160 notifications/sec
```

Design for peak traffic.

Assume

```
10×

Peak Load

≈12,000 notifications/sec
```

This immediately tells us

- We need asynchronous processing.
- We need horizontal scaling.
- A single server is insufficient.
- Background workers are required.

---

# Step 5 – API Design

## Send Notification

```
POST /notifications
```

Request

```json
{
    "userId": 123,
    "channel": "EMAIL",
    "templateId": "ORDER_CONFIRMED",
    "data": {
        "orderId": "A12345",
        "amount": 250
    }
}
```

Response

```json
{
    "notificationId": "N123456",
    "status": "QUEUED"
}
```

Notice

The API returns

```
QUEUED
```

instead of

```
DELIVERED
```

because notification delivery is asynchronous.

---

## Retrieve Notification Status

```
GET /notifications/{notificationId}
```

Example Response

```json
{
    "notificationId": "N123456",
    "status": "SENT",
    "deliveredAt": "2026-07-09T15:00:00Z"
}
```

---

# Step 6 – Data Model

## Notification

```
Notification

------------------------

notificationId

userId

channel

templateId

status

createdAt

updatedAt
```

Possible statuses

- QUEUED
- PROCESSING
- SENT
- FAILED
- RETRYING
- DEAD_LETTER

---

## User Preference

```
UserPreference

------------------------

userId

emailEnabled

smsEnabled

pushEnabled

marketingOptIn

quietHours
```

---

## Notification Template

```
Template

------------------------

templateId

channel

subject

body

version
```

Templates contain placeholders.

Example

```
Subject

Order {{orderId}} Confirmed

Body

Hi {{name}},

Your order {{orderId}} has been confirmed.
```

---

## Delivery Log

```
DeliveryLog

------------------------

notificationId

provider

attemptNumber

status

errorMessage

timestamp
```

Used for

- Auditing
- Monitoring
- Retries
- Customer Support

---

# Database Choice

A relational database (PostgreSQL or MySQL) is a good fit because we require

- Reliable persistence
- Transactions
- Delivery history
- User preferences
- Reporting

Redis is used as a cache, not as the source of truth.

---

# Transition to High-Level Design

At this point we have

✅ Requirements

✅ Scale

✅ APIs

✅ Data Model

Now we can confidently design the High-Level Architecture because we understand

- What we're building
- How much traffic we expect
- What data we store
- How clients interact with the system
- The major constraints and trade-offs

The next step is the High-Level Design (HLD), where we identify the major components, explain how they interact, and walk through the end-to-end request flow.

# High-Level Architecture

```
                                Client
                                   │
                                   ▼
                           Load Balancer
                                   │
                                   ▼
                             API Gateway
                                   │
                                   ▼
                      Notification Service
                                   │
              ┌────────────────────┼─────────────────────┐
              │                    │                     │
              ▼                    ▼                     ▼
        Redis Cache         Notification DB        User Preference DB
              │                    │
              │                    ▼
              │           Transactional Outbox
              │                    │
              │                    ▼
              │             Outbox Publisher
              │                    │
              └────────────────────┤
                                   ▼
                               Kafka Topics
          ┌────────────────────────┼────────────────────────┐
          ▼                        ▼                        ▼
     Email Topic              SMS Topic               Push Topic
          │                        │                        │
          ▼                        ▼                        ▼
    Email Workers            SMS Workers            Push Workers
          │                        │                        │
          ▼                        ▼                        ▼
 Email Provider             SMS Provider            Push Provider
 (SES/SendGrid)             (Twilio)              (FCM/APNS)
```

---

# Request Flow

The system processes a notification request in the following steps.

---

## Step 1 – Client Sends Request

```
POST /notifications
```

Example

```json
{
  "userId": 123,
  "channel": "EMAIL",
  "templateId": "ORDER_CONFIRMED",
  "data": {
      "orderId": "A12345"
  }
}
```
The client expects a response indicating whether the notification has been accepted.


---

## Step 2 – Load Balancer

The Load Balancer distributes incoming requests across multiple Notification Service instances.

Purpose

- High Availability
- Horizontal Scaling
- Fault Tolerance

---

## Step 3 – API Gateway

The API Gateway performs

- Authentication
- Authorization
- Rate Limiting
- Request Validation
- Request Routing
- Logging

Only valid requests are forwarded.

---

## Step 4 – Notification Service

This is the core business service.

Responsibilities

- Validate request
- Validate template
- Check user preferences
- Generate Notification ID
- Persist notification
- Write Outbox event
- Return response

The Notification Service remains stateless so that it can scale horizontally.

---

## Step 5 – Redis Cache

Frequently accessed information is stored in Redis.

Examples

- Notification Templates
- User Preferences
- Configuration
- Rate Limit Counters

If Redis misses,

the service loads data from the database and refreshes the cache.

Redis improves performance but is **not** the source of truth.

---

## Step 6 – Database

The Notification Service stores

```
Notification

notificationId

userId

channel

status

createdAt
```

Delivery status is initially

```
QUEUED
```

---

## Step 7 – Transactional Outbox

Inside the same database transaction

```
Insert Notification

+

Insert Outbox Event
```

Both operations either succeed together or fail together.

This prevents the Dual Write Problem.

The API now immediately returns

```
HTTP 202 Accepted
```

Notice

The notification has only been accepted.

It has not yet been delivered.

---

## Step 8 – Outbox Publisher

A background publisher continuously polls the Outbox table.

```
Outbox

↓

Publisher

↓

Kafka
```

Once Kafka acknowledges the message,

the Outbox record is marked as published.

---

## Step 9 – Kafka

Kafka decouples request processing from notification delivery.

Separate topics exist for each notification channel.

```
Email Topic

SMS Topic

Push Topic
```

This prevents one notification channel from blocking another.

---

## Step 10 – Workers

Dedicated workers consume messages independently.

```
Email Topic

↓

Email Worker
```

The worker performs

- Load Template
- Replace Variables
- Call Provider
- Update Delivery Status

The same architecture applies to SMS and Push notifications.

Workers can scale independently.

---

## Step 11 – External Providers

Workers call external providers.

Examples

Email

- Amazon SES
- SendGrid

SMS

- Twilio

Push

- Firebase Cloud Messaging (FCM)
- Apple Push Notification Service (APNS)

External providers are treated as unreliable systems.

Workers implement

- Retry
- Exponential Backoff
- Timeout
- Circuit Breaker

---

## Step 12 – Delivery Status

Workers update the Notification Database.

Possible states

```
QUEUED

PROCESSING

SENT

FAILED

RETRYING

DEAD_LETTER
```

Clients can retrieve delivery status later.

```
GET /notifications/{notificationId}
```

---

# Synchronous vs Asynchronous Flow

## Synchronous Path (Critical Path)

Everything the client waits for.

```
Client

↓

Load Balancer

↓

API Gateway

↓

Notification Service

↓

Redis

↓

Database

↓

Transactional Outbox

↓

HTTP 202 Accepted
```

The client request ends here.

---

## Asynchronous Path

Background processing begins after the response.

```
Outbox Publisher

↓

Kafka

↓

Workers

↓

Email Provider

↓

Delivery Status Updated
```

The client does not wait for these operations.

---

# Failure Handling

## Kafka Down

Notification is still safely stored.

```
Notification DB

↓

Outbox

↓

Retry Publishing
```

No notifications are lost.

---

## Redis Down

Fallback

```
Redis

↓

Database
```

Higher latency,

but functionality remains intact.

---

## Email Provider Down

Workers retry with exponential backoff.

```
1 second

↓

2 seconds

↓

4 seconds

↓

8 seconds
```

Eventually

↓

Dead Letter Queue

---

# Scalability

Every component scales independently.

```
API Servers

↓

Horizontal Scaling

Kafka

↓

More Partitions

Workers

↓

More Consumers

Redis

↓

Redis Cluster

Database

↓

Read Replicas

↓

Partitioning
```

---

# Why This Architecture?

| Component | Purpose |
|-----------|---------|
| Load Balancer | Distribute traffic |
| API Gateway | Authentication, Rate Limiting |
| Notification Service | Business Logic |
| Redis | Low-latency reads |
| Database | Persistent storage |
| Transactional Outbox | Reliable event publishing |
| Kafka | Asynchronous processing |
| Workers | Notification delivery |
| External Providers | Send Email/SMS/Push |

---

# Trade-offs

Advantages

- Low API latency
- Highly scalable
- Fault tolerant
- Supports retries
- Independent scaling
- No message loss
- Decoupled architecture

Trade-offs

- Eventual consistency
- Additional infrastructure
- Operational complexity
- Duplicate message handling
- Requires idempotent workers

---

# Interview Summary

A notification request first reaches the Load Balancer and API Gateway, where it is authenticated, rate-limited, and routed to the Notification Service. The service validates the request, checks Redis for templates and user preferences, stores the notification and an Outbox event in the database within a single transaction, and immediately returns **HTTP 202 Accepted**. A background Outbox Publisher reliably publishes events to Kafka. Dedicated Email, SMS, and Push workers consume messages independently, invoke external providers, retry transient failures, and update delivery status in the database. This architecture keeps the critical request path short while providing reliable, scalable, and fault-tolerant asynchronous notification delivery.

# Notification Service – HLD Walkthrough (Step-by-Step Request Flow)

---

# Why This Section Matters

Drawing the architecture is only half of the interview.

The interviewer now wants you to explain

> **"Walk me through what happens when a notification request arrives."**

Think of this as narrating the journey of a single request through the system.

---

# Step 1 – Client Sends Request

The client (Web Application, Mobile App, or another Microservice) sends a request.

```
POST /notifications
```

Example

```json
{
    "userId": 123,
    "channel": "EMAIL",
    "templateId": "ORDER_CONFIRMED",
    "data": {
        "orderId": "A12345"
    }
}
```

The client expects a response indicating whether the notification has been accepted.

---

# Step 2 – Load Balancer

The request first reaches the Load Balancer.

Responsibilities

- Distribute traffic across multiple Notification Service instances
- Route traffic away from unhealthy instances
- Improve availability
- Enable horizontal scaling

```
Client

↓

Load Balancer

↓

Notification Service 1

Notification Service 2

Notification Service 3
```

---

# Step 3 – API Gateway

The request is forwarded to the API Gateway.

Responsibilities

- Authentication
- Authorization
- Rate Limiting
- Request Validation
- API Versioning
- Logging
- Routing

Only authenticated and valid requests continue.

---

# Step 4 – Notification Service

The Notification Service contains the core business logic.

Responsibilities

- Validate request payload
- Generate Notification ID
- Validate notification channel
- Validate template
- Check user preferences
- Determine notification priority

The service is stateless, allowing it to scale horizontally.

---

# Step 5 – Read Frequently Used Data from Redis

Before accessing the database, the service checks Redis.

Typical cached data

- Notification Templates
- User Preferences
- Configuration
- Rate Limit Counters

```
Notification Service

↓

Redis
```

### Cache Hit

The required information is immediately returned.

### Cache Miss

Load the data from the database and populate Redis for future requests.

Redis is an optimization layer, not the source of truth.

---

# Step 6 – Persist Notification

The Notification Service creates a new notification record.

Example

```
Notification

notificationId

userId

channel

status = QUEUED

createdAt
```

This provides a durable record before attempting delivery.

---

# Step 7 – Transactional Outbox

Within the same database transaction,

the service writes

```
Notification Record

+

Outbox Event
```

Example

```
BEGIN TRANSACTION

Insert Notification

Insert Outbox Event

COMMIT
```

This ensures the notification and the event are either both saved or neither is saved.

It eliminates the Dual Write Problem.

---

# Step 8 – Return Response to the Client

At this point,

the notification has been safely stored.

The API immediately returns

```
HTTP 202 Accepted
```

Example

```json
{
    "notificationId": "N123456",
    "status": "QUEUED"
}
```

Notice

The notification has **not** been delivered yet.

It has only been accepted for processing.

The user's request ends here.

Everything after this point is asynchronous.

---

# Step 9 – Outbox Publisher

A background publisher continuously scans the Outbox table.

```
Outbox

↓

Publisher
```

Whenever unpublished events are found,

they are published to Kafka.

After Kafka acknowledges the message,

the Outbox entry is marked as published.

This guarantees reliable event delivery.

---

# Step 10 – Kafka

Kafka acts as the messaging backbone.

Instead of sending notifications directly,

events are published to Kafka topics.

Example

```
Email Topic

SMS Topic

Push Topic
```

Separating channels allows each to scale independently.

---

# Step 11 – Notification Workers

Dedicated workers consume events.

```
Email Topic

↓

Email Worker
```

Worker responsibilities

- Read notification
- Load template
- Replace placeholders
- Call external provider
- Update delivery status

The same architecture applies to

- SMS Workers
- Push Workers

Each worker group scales independently.

---

# Step 12 – External Notification Providers

Workers invoke external providers.

Examples

Email

- Amazon SES
- SendGrid

SMS

- Twilio

Push

- Firebase Cloud Messaging (FCM)
- Apple Push Notification Service (APNS)

Since these providers are external,

workers implement

- Retry
- Exponential Backoff
- Timeout
- Circuit Breaker

to improve resilience.

---

# Step 13 – Update Delivery Status

After attempting delivery,

workers update the notification record.

Possible states

```
QUEUED

PROCESSING

SENT

FAILED

RETRYING

DEAD_LETTER
```

Clients can retrieve status later.

```
GET /notifications/{notificationId}
```

---

# Failure Handling

## Kafka Unavailable

The notification remains safe.

```
Notification DB

↓

Outbox

↓

Retry Publisher
```

No notifications are lost.

---

## Redis Unavailable

Fallback

```
Redis

↓

Database
```

Higher latency,

but requests continue successfully.

---

## Email Provider Failure

Workers retry.

```
1 second

↓

2 seconds

↓

4 seconds

↓

8 seconds
```

If retries are exhausted,

the message moves to the Dead Letter Queue.

---

# End-to-End Request Flow

```
Client

↓

Load Balancer

↓

API Gateway

↓

Notification Service

↓

Redis (Templates / Preferences)

↓

Notification Database

↓

Transactional Outbox

↓

HTTP 202 Accepted

---------------------------------------------------

Outbox Publisher

↓

Kafka

↓

Email Topic

↓

Email Worker

↓

Email Provider

↓

Update Notification Status
```

---

# Interview Walkthrough (30–60 Seconds)

> The client sends a notification request through the Load Balancer and API Gateway. The Notification Service validates the request, checks Redis for templates and user preferences, stores the notification and an Outbox event within a single database transaction, and immediately returns **HTTP 202 Accepted**. A background Outbox Publisher reliably publishes events to Kafka. Dedicated Email, SMS, and Push workers consume the events, invoke external providers with retries and exponential backoff, and update the notification status in the database. This keeps the critical request path short while ensuring reliable, scalable, and fault-tolerant notification delivery.

---

# Key Takeaways

- Keep the synchronous request path short.
- Persist the notification before publishing events.
- Use the Transactional Outbox Pattern for reliable event publishing.
- Kafka decouples API processing from notification delivery.
- Dedicated workers process each notification channel independently.
- External providers are treated as unreliable systems.
- Retries, Dead Letter Queues, and idempotent workers ensure reliable delivery.
- Clients receive a fast response while notification delivery continues asynchronously.


# Appendix – Notification Service Deep Dive & Interview Follow-up Questions

---

# Why This Chapter Matters

Most candidates can draw the high-level architecture.

```
Client

↓

Notification Service

↓

Kafka

↓

Email Worker

↓

Email Provider
```

However, senior backend interviews are usually decided by the follow-up questions.

The interviewer wants to understand:

- Can your system survive failures?
- Can it scale?
- Can it recover automatically?
- Can it avoid duplicate notifications?
- How does it behave under edge cases?

This appendix covers those production-level concerns.

---

# Question 1 – What if Kafka Goes Down?

Current Flow

```
Notification Service

↓

Kafka

↓

Email Worker
```

Suppose Kafka becomes unavailable.

```
POST /notifications

↓

Publish to Kafka

↓

FAIL
```

Should the API return HTTP 500?

Maybe.

But production systems usually try very hard not to lose requests.

---

## Better Solution – Transactional Outbox Pattern

Instead of writing directly to Kafka,

write both the notification and an Outbox event inside one database transaction.

```
Database Transaction

----------------------------------

Insert Notification

Insert Outbox Event

----------------------------------

COMMIT
```

Return immediately.

```
HTTP 202 Accepted
```

A background publisher continuously reads the Outbox table.

```
Outbox Table

↓

Publisher

↓

Kafka
```

Suppose Kafka is unavailable.

```
Notification Saved ✓

Outbox Saved ✓

Kafka Down ❌
```

No problem.

The publisher retries until Kafka becomes available.

Nothing is lost.

---

# Question 2 – What if Redis Goes Down?

Redis is used for

- Notification Templates
- User Preferences
- Frequently Accessed Metadata

Suppose Redis becomes unavailable.

```
Notification Service

↓

Redis

↓

Unavailable
```

Fallback

```
Notification Service

↓

SQL Database
```

The request becomes slower,

but it still succeeds.

Redis improves performance.

It should never be the source of truth.

---

# Question 3 – What if the Email Provider is Slow?

Without Kafka

```
Notification Service

↓

Email Provider

↓

20-second delay
```

Your API becomes slow.

With Kafka

```
Notification Service

↓

Kafka

↓

Return HTTP 202

↓

Worker

↓

Email Provider
```

Only the worker waits.

The client never waits.

---

# Question 4 – What if the Email Provider is Completely Down?

Suppose the Email Provider returns

```
503 Service Unavailable
```

The worker retries.

Example

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

This is **Exponential Backoff**.

If retries continue failing,

move the message to a Dead Letter Queue.

```
Email Worker

↓

Retry

↓

Retry

↓

Retry

↓

Dead Letter Queue
```

The message is preserved for later investigation.

---

# Question 5 – How Do You Prevent Duplicate Emails?

Kafka provides

**At-Least-Once Delivery.**

Therefore,

duplicate messages are possible.

Example

```
Order Confirmed

↓

Email Sent

↓

Worker Crashes

↓

Kafka Redelivers

↓

Email Sent Again
```

Customer receives two emails.

Bad.

---

## Solution – Idempotency

Assign every notification a unique identifier.

```
notificationId
```

Before sending

```
Already Processed?

↓

YES

↓

Ignore
```

Workers should always be idempotent.

---

# Question 6 – What if One Customer Generates Millions of Notifications?

Protect the system with Rate Limiting.

Example

```
100 Notifications

↓

Per Minute

↓

Per User
```

After exceeding the limit

```
HTTP 429

Too Many Requests
```

---

# Question 7 – How Do You Schedule Notifications?

Suppose a user wants

```
Send Tomorrow

9:00 AM
```

Don't publish immediately.

Instead

```
Notification Database

↓

status = SCHEDULED

↓

scheduledTime = Tomorrow 9:00 AM
```

A scheduler continuously checks for due notifications.

```
Scheduler

↓

Find Scheduled Notifications

↓

Publish to Kafka
```

Workers remain unchanged.

---

# Question 8 – How Do You Support Multiple Email Providers?

Suppose AWS SES fails.

```
Email Worker

↓

SES

↓

Failure
```

Fallback

```
SendGrid

or

Mailgun
```

Abstract providers behind an interface.

```
EmailProvider

↓

SES

↓

SendGrid

↓

Mailgun
```

This is an excellent use case for the Strategy Pattern.

---

# Question 9 – How Do You Prevent Spam?

Users should have notification preferences.

Example

```
Marketing Emails

Disabled
```

Before sending

```
Check User Preferences

↓

Allowed?

↓

YES

↓

Send
```

---

# Question 10 – How Do You Support Multiple Notification Channels?

Avoid one giant worker.

Bad

```
Worker

↓

Email Logic

↓

SMS Logic

↓

Push Logic
```

Better

```
Notification Worker

↓

Notification Strategy

↓

Email Strategy

SMS Strategy

Push Strategy
```

Each channel has its own implementation.

Open for extension.

Closed for modification.

---

# Question 11 – What if Workers Become Overloaded?

Scale workers horizontally.

```
Kafka

↓

Email Worker 1

↓

Email Worker 2

↓

Email Worker 3

↓

Email Worker 4
```

Kafka partitions distribute work across workers.

---

# Question 12 – What if One Email Takes 10 Seconds?

Don't process one email at a time.

Use concurrency.

```
Worker

↓

Thread Pool

↓

20 Concurrent Email Requests
```

Or simply increase worker instances.

---

# Question 13 – How Do You Monitor the System?

Monitor every layer.

API

- Request Rate
- Error Rate
- Latency

Kafka

- Queue Depth
- Consumer Lag

Workers

- Success Rate
- Retry Count
- Processing Time

Providers

- Delivery Success Rate
- Provider Latency

Dead Letter Queue

- Failed Message Count

---

# Question 14 – How Do You Support One Billion Notifications Per Day?

Scale every layer.

```
API Servers

↓

Horizontal Scaling

↓

Kafka

↓

More Partitions

↓

Workers

↓

More Consumers

↓

Database

↓

Read Replicas

↓

Partitioning

↓

Redis

↓

Redis Cluster
```

---

# Question 15 – How Do You Prevent One Slow Channel from Affecting Others?

Use separate Kafka topics.

```
Email Topic

SMS Topic

Push Topic
```

Each topic has dedicated workers.

```
Email Workers

SMS Workers

Push Workers
```

Failure in SMS should not impact Email.

---

# Question 16 – What if the Notification Database Becomes a Bottleneck?

Reads usually dominate.

Move read traffic to Read Replicas.

```
Primary Database

↓

Read Replica 1

↓

Read Replica 2
```

As the table grows

- Partition
- Archive old notifications

---

# Question 17 – Should Delivery Status Be Synchronous?

No.

Return

```
HTTP 202 Accepted
```

The client can later request status.

```
GET /notifications/{notificationId}
```

Possible responses

```
QUEUED

PROCESSING

DELIVERED

FAILED
```

---

# Interview Tips

If asked

> How would you improve this design?

Strong answers include

- Transactional Outbox
- Redis Cluster
- Multiple Email Providers
- Kafka Partitioning
- Dead Letter Queue
- Idempotent Workers
- Rate Limiting
- Circuit Breakers
- Multi-Region Deployment
- Comprehensive Monitoring

---

# Common Interview Mistakes

❌ Calling Email Providers synchronously

❌ No retry strategy

❌ No Dead Letter Queue

❌ No idempotency

❌ Returning HTTP 200 before processing completes

❌ Using Redis as the source of truth

❌ One worker for every notification type

❌ No monitoring

---

# Final Production Architecture

```
Client

↓

API Gateway

↓

Notification Service

↓

Notification Database

↓

Transactional Outbox

↓

Outbox Publisher

↓

Kafka

↓

Email Topic

SMS Topic

Push Topic

↓

Dedicated Workers

↓

Provider Strategy

↓

SES

SendGrid

Mailgun

↓

Delivery Status Updated
```

---

# Final Takeaways

A production-ready Notification Service should include

- Asynchronous processing
- Transactional Outbox
- Kafka
- Retry with Exponential Backoff
- Dead Letter Queue
- Idempotent Workers
- User Preferences
- Scheduling Support
- Provider Abstraction
- Rate Limiting
- Horizontal Scaling
- Monitoring and Observability

This is the level of depth expected in Senior Backend and Staff-level System Design interviews.

# URL Shortener – Part 1
# Before High-Level Design

---

# Problem Statement

Design a URL Shortener service similar to TinyURL or Bitly.

The service should convert long URLs into short URLs and redirect users efficiently.

Example

Long URL

```
https://www.example.com/products/mobile/apple/iphone16?color=black
```

↓

Short URL

```
https://tiny.ly/Ab3Kd9
```

When the short URL is opened,

the user should be redirected to the original URL.

---

# Step 1 – Clarifying Questions

Before designing the system, clarify the requirements.

---

## Functional Questions

### 1. Should users be able to create short URLs?

Assumption

Yes.

---

### 2. Should custom aliases be supported?

Example

```
tiny.ly/chatgpt
```

instead of

```
tiny.ly/A8dkL2
```

Assumption

Yes.

---

### 3. Should URLs expire?

Assumption

Support optional expiration.

---

### 4. Should click analytics be supported?

Examples

- Click Count
- Country
- Browser
- Device
- Referrer

Assumption

Yes.

---

### 5. Should authenticated users manage URLs?

Assumption

Optional.

Not required for the initial design.

---

### 6. Should duplicate long URLs generate the same short URL?

Example

```
google.com

↓

abc123
```

If another user shortens the same URL,

should we reuse

```
abc123
```

or create

```
xyz987
```

Assumption

Create a new short URL each time.

This simplifies ownership and analytics.

---

### 7. Should users edit or delete URLs?

Assumption

No.

Read-only after creation.

---

# Non-Functional Questions

---

### Availability

High Availability

---

### Latency

Redirects should complete within

```
<100 ms
```

---

### Scalability

Support billions of URLs.

---

### Reliability

URLs should never be lost.

---

### Read vs Write Ratio

Very read-heavy.

Millions of redirects.

Relatively few URL creations.

---

### Consistency

Eventual consistency is acceptable for analytics.

Strong consistency required for URL mapping.

---

# Step 2 – Functional Requirements

The system should support

- Create Short URL
- Redirect to Original URL
- Optional Custom Alias
- Optional Expiration
- Click Analytics
- URL Metadata

---

# Step 3 – Non-Functional Requirements

The system should provide

- High Availability
- Low Latency
- Horizontal Scalability
- Fault Tolerance
- High Read Throughput
- Reliable URL Storage

---

# Step 4 – Capacity Estimation

Assume

```
100 Million New URLs / Month
```

Average writes

```
≈40 URLs/sec
```

Assume

```
1000×

More Reads Than Writes
```

Average redirects

```
≈40,000 redirects/sec
```

Peak traffic

```
400,000 redirects/sec
```

This tells us

- The system is read-heavy.
- Caching is essential.
- Read replicas will likely be required.
- Redirect latency should be minimal.

---

# Step 5 – API Design

---

## Create Short URL

```
POST /urls
```

Request

```json
{
    "longUrl": "https://www.example.com/product/12345",
    "customAlias": null,
    "expiryDate": null
}
```

Response

```json
{
    "shortUrl": "https://tiny.ly/Ab3Kd9"
}
```

---

## Redirect

```
GET /{shortCode}
```

Example

```
GET /Ab3Kd9
```

Response

```
HTTP 302 Redirect

Location:
https://www.example.com/product/12345
```

---

## Retrieve Analytics

```
GET /analytics/{shortCode}
```

Example Response

```json
{
    "clickCount": 125430,
    "createdAt": "2026-07-01",
    "expiresAt": null
}
```

---

# Step 6 – Data Model

---

## URL Table

```
URL

-------------------------

id

shortCode

longUrl

createdAt

expiresAt

createdBy

isActive
```

---

## Click Analytics

```
ClickEvent

-------------------------

eventId

shortCode

timestamp

country

device

browser

referrer
```

Analytics data is append-only.

It can later be processed asynchronously.

---

# Database Choice

A relational database is a good choice because

- URL mapping is structured
- Lookups are by primary key
- Strong consistency is required
- Relationships are simple

Possible choices

- PostgreSQL
- MySQL

Analytics can later be moved to a separate analytical store if needed.

---

# Design Decisions So Far

We now know

- The system is extremely read-heavy.
- Redirect latency is critical.
- URL mappings require strong consistency.
- Analytics can be processed asynchronously.
- Redis will likely play an important role.
- Read replicas will help scale redirect traffic.

At this point we have enough information to design the High-Level Architecture.

The next step is to identify the major components, define the request flow, and explain how URL creation and redirection work end-to-end.

# URL Shortener – Part 2
# Generating Short URLs

---

# Why This Matters

The primary responsibility of a URL Shortener is simple.

```
Long URL

↓

Short URL
```

The difficult question is

> **How do we generate a short URL that is unique, scalable, and efficient?**

There are several approaches.

Each has advantages and trade-offs.

---

# Requirements

A good short URL should be

- Unique
- Short
- Easy to generate
- Fast to lookup
- Scalable
- Difficult to guess (optional)

---

# Option 1 – Auto Increment ID

Suppose the database generates

```
1

2

3

4

5
```

Example

```
id = 123456
```

Instead of exposing

```
123456
```

convert it to Base62.

```
123456

↓

W7E
```

Store

```
Long URL

↓

ID

↓

Base62

↓

Short URL
```

---

## Advantages

- Very simple
- Guaranteed uniqueness
- No collisions
- Sequential IDs
- Excellent performance

---

## Disadvantages

- Easy to guess URLs

```
abc123

↓

abc124

↓

abc125
```

Competitors can enumerate links.

---

# Option 2 – UUID

Generate

```
550e8400-e29b-41d4-a716-446655440000
```

Then hash or encode.

---

Advantages

- Globally unique
- Distributed generation
- No central database

---

Disadvantages

UUIDs are

```
128 bits
```

Much longer than necessary.

Need hashing or truncation.

---

# Option 3 – Random String

Generate

```
aB93Kd
```

Randomly.

Check

```
Already Exists?

↓

No

↓

Use
```

Otherwise

Generate another.

---

Advantages

- Difficult to guess
- No sequential pattern

---

Disadvantages

Must check collisions.

As the database grows,

collision probability increases.

---

# Option 4 – Hash Long URL

Input

```
https://google.com
```

Hash

```
SHA-256

↓

f4dce81...

↓

Take first 7 characters
```

Advantages

Same URL

↓

Same Short URL

---

Disadvantages

Collisions are still possible.

Need collision handling.

Hash computation is more expensive than using an ID.

---

# Option 5 – Snowflake IDs

Distributed ID generators like Twitter Snowflake.

Generate

```
64-bit ID

↓

Base62

↓

Short URL
```

Advantages

- Globally unique
- Ordered
- Distributed
- No database coordination

Very common in large distributed systems.

---

# Comparing the Options

| Approach | Unique | Distributed | Collision | Easy |
|----------|---------|-------------|-----------|------|
| Auto Increment | ✅ | ❌ | None | ⭐⭐⭐⭐⭐ |
| UUID | ✅ | ✅ | None | ⭐⭐ |
| Random | Usually | ✅ | Possible | ⭐⭐⭐ |
| Hash | Usually | ✅ | Possible | ⭐⭐⭐ |
| Snowflake | ✅ | ✅ | None | ⭐⭐⭐⭐ |

---

# Which Approach Should We Choose?

For most interview solutions,

Auto Increment + Base62

is an excellent choice.

Why?

Because

- IDs are unique.
- Base62 produces compact URLs.
- No collision handling required.
- Very simple architecture.

For very large distributed systems,

Snowflake + Base62

is often preferred because IDs can be generated without relying on a single database.

---

# What is Base62?

Suppose our database generated

```
125
```

Instead of exposing

```
125
```

we encode it.

Base62 uses

```
0-9

A-Z

a-z
```

Total

```
62 characters
```

Example

```
125

↓

21

↓

Z

(example only)
```

Large decimal numbers become much shorter.

---

# Why Base62?

Suppose we used Base10.

```
999999999
```

Still long.

Suppose we use Base62.

```
999999999

↓

15ftG7
```

Much shorter.

This is why URL shorteners almost always use Base62.

---

# URL Generation Flow

```
Long URL

↓

Generate Unique ID

↓

Convert to Base62

↓

Store Mapping

↓

Return

https://tiny.ly/W7E3fK
```

---

# What About Custom Aliases?

Suppose the user wants

```
tiny.ly/chatgpt
```

instead of

```
tiny.ly/A91fKs
```

Flow

```
Custom Alias?

↓

YES

↓

Already Exists?

↓

No

↓

Use It

↓

Return
```

If already taken

↓

Return

```
HTTP 409 Conflict
```

Ask the user to choose another alias.

---

# What About Collisions?

If using

Auto Increment

↓

No collisions.

If using

UUID

↓

Practically impossible.

If using

Random IDs

↓

Possible.

Need

```
Generate

↓

Check Database

↓

Collision?

↓

Generate Again
```

---

# Should We Hash the Long URL?

Example

```
google.com

↓

SHA-256

↓

Take First 7 Characters
```

Advantages

Same URL

↓

Same Short URL

Disadvantages

- Collision handling
- Longer computation
- Harder to support multiple users shortening the same URL independently

Most production systems prefer generating unique IDs rather than hashing the URL.

---

# Interview Follow-up Questions

### Why Base62 instead of Base64?

Base64 includes characters like

```
+

/

=
```

These require escaping in URLs.

Base62 uses only

```
0-9

A-Z

a-z
```

making it URL-safe.

---

### Why not use UUID directly?

UUIDs are too long.

Example

```
550e8400-e29b-41d4-a716-446655440000
```

Not user-friendly.

---

### Why not use random strings?

Random generation eventually creates collisions.

Collision detection adds additional database lookups.

---

### Why is Auto Increment difficult in distributed systems?

Suppose you have

```
Server A

Server B

Server C
```

Who generates

```
ID = 123457
```

Only one database can guarantee uniqueness.

This becomes a bottleneck.

Distributed ID generators such as Snowflake solve this problem.

---

# Key Takeaways

- Every URL shortener needs a strategy for generating unique short codes.
- Auto Increment + Base62 is the simplest and most common interview solution.
- UUIDs provide uniqueness but are too long for direct use.
- Random IDs require collision detection.
- Hashing the long URL can produce deterministic short URLs but introduces collision handling and flexibility trade-offs.
- Snowflake IDs are a good choice for large distributed systems because they generate globally unique IDs without centralized coordination.
- Base62 is preferred because it creates compact, URL-safe identifiers.

# URL Shortener – Part 3
# Base62 Encoding Deep Dive

---

# Why Do We Need Base62?

Suppose our database generates an ID.

```
123456789
```

We could use this directly.

```
https://tiny.ly/123456789
```

But that's

- Long
- Easy to guess
- Not user friendly

Instead we encode the number into another numbering system.

```
123456789

↓

Base62

↓

8M0kX
```

Much shorter.

---

# What is a Number System?

We're familiar with Decimal (Base10).

```
0 1 2 3 4 5 6 7 8 9
```

Ten symbols.

Hence

```
Base10
```

---

Computers use Binary.

```
0 1
```

Two symbols.

Hence

```
Base2
```

---

Hexadecimal

```
0-9

A-F
```

16 symbols.

```
Base16
```

---

The more symbols we have,

the fewer digits we need.

---

# Base36

Uses

```
0-9

A-Z
```

Total

```
36 characters
```

---

# Base62

Uses

```
0-9

A-Z

a-z
```

Total

```
10

+

26

+

26

=

62 characters
```

This gives us many more possible values per character.

---

# Character Mapping

```
0  -> 0
1  -> 1
...
9  -> 9

10 -> A
11 -> B
...
35 -> Z

36 -> a
37 -> b
...
61 -> z
```

This mapping is arbitrary,

but it is the one commonly used.

---

# Why More Characters Matter

Suppose we have

```
999999999
```

In decimal

```
999999999
```

Nine digits.

Now convert it to Base62.

```
15ftG7
```

Only

```
6 characters
```

Same number.

Much shorter.

---

# How Base Conversion Works

Think about decimal.

```
123
```

Actually means

```
1 × 10²

+

2 × 10¹

+

3 × 10⁰
```

Similarly,

Base62 means

```
digit × 62⁰

digit × 62¹

digit × 62²
```

Same mathematics.

Different base.

---

# Manual Example

Convert

```
125
```

to Base62.

Step 1

Divide by 62.

```
125 ÷ 62

=

2

remainder

1
```

Store

```
1
```

---

Now divide

```
2 ÷ 62

=

0

remainder

2
```

Store

```
2
```

Now read remainders backwards.

```
2

1
```

Result

```
21
```

Check

```
2 × 62

+

1

=

125
```

Correct.

---

# Another Example

Convert

```
3844
```

Notice

```
62²

=

3844
```

Therefore

```
100
```

in Base62.

Because

```
1 × 62²

+

0 × 62¹

+

0 × 62⁰
```

equals

```
3844
```

---

# General Algorithm

```
Number

↓

Divide by 62

↓

Store Remainder

↓

Repeat Until Quotient = 0

↓

Reverse Remainders
```

Very similar to decimal-to-binary conversion.

---

# Java Pseudocode

```java
String chars =
"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

while(id > 0){

    int remainder = id % 62;

    result.append(chars.charAt(remainder));

    id /= 62;
}

return result.reverse();
```

Simple.

Efficient.

Time Complexity

```
O(log₆₂ N)
```

---

# Why Not Base64?

Base64 uses

```
A-Z

a-z

0-9

+

/

=
```

Problem

```
+

/

=
```

are special characters in URLs.

They often require

URL encoding.

Example

```
abc+

↓

abc%2B
```

Longer URLs.

More complexity.

---

Base62 contains only

```
A-Z

a-z

0-9
```

Every character is already URL-safe.

---

# Capacity Comparison

| Characters | Possible Values |
|------------|----------------:|
| 1 | 62 |
| 2 | 62² = 3,844 |
| 3 | 62³ = 238,328 |
| 4 | 62⁴ = 14.7 Million |
| 5 | 62⁵ = 916 Million |
| 6 | 62⁶ = 56.8 Billion |
| 7 | 62⁷ = 3.5 Trillion |
| 8 | 62⁸ = 218 Trillion |

Notice

A

```
7-character
```

Base62 code can represent

```
3.5 Trillion
```

unique URLs.

More than enough for most systems.

---

# Why Not Use Decimal?

Suppose IDs reach

```
8,239,145,127
```

The URL becomes

```
tiny.ly/8239145127
```

Ten digits.

Now encode using Base62.

```
tiny.ly/G4mX9K
```

Only six characters.

Much easier to type.

Much easier to share.

---

# Interview Questions

### Why Base62?

Because

- Compact
- URL-safe
- Human-friendly
- Easy to encode/decode
- Huge address space

---

### Why not Base64?

Base64 contains

```
+

/

=
```

which require escaping in URLs.

Base62 avoids these characters.

---

### Is Base62 Encryption?

No.

Base62 is simply an encoding.

Anyone can decode it.

It provides

No security.

---

### Is Base62 Hashing?

No.

Hashing

```
Input

↓

Hash

↓

Fixed Output
```

Base62

```
Number

↓

Shorter Representation
```

The mapping is reversible.

---

# Common Misconceptions

❌ Base62 compresses data.

It does not.

It simply represents the same number using a larger alphabet.

---

❌ Base62 provides security.

It does not.

It is completely reversible.

---

❌ Base62 prevents collisions.

It does not.

Collision prevention comes from how IDs are generated,

not from the encoding.

---

# Key Takeaways

- Base62 is a number encoding system using 62 URL-safe characters.
- It converts large numeric IDs into much shorter strings.
- Encoding is reversible.
- It is not encryption.
- It is not hashing.
- It does not prevent collisions.
- Auto Increment IDs + Base62 is one of the simplest and most common URL shortener implementations.
- Seven Base62 characters can represent approximately **3.5 trillion** unique values, making it suitable for very large systems.

# URL Shortener – Part 4
# High-Level Design (HLD)

---

# Objective

Design a highly scalable URL Shortener capable of

- Creating short URLs
- Redirecting users with very low latency
- Supporting billions of URLs
- Supporting analytics
- Handling extremely read-heavy traffic

---

# High-Level Architecture

```
                          Client
                             │
                             ▼
                     DNS / Load Balancer
                             │
                             ▼
                       API Gateway
                             │
          ┌──────────────────┴─────────────────┐
          │                                    │
          ▼                                    ▼
 URL Shortening Service              Redirect Service
          │                                    │
          ▼                                    ▼
      Redis Cache                      Redis Cache
          │                                    │
          ▼                                    ▼
        Database <-----------------------------┘
          │
          ▼
   Analytics Publisher
          │
          ▼
        Kafka
          │
          ▼
   Analytics Workers
          │
          ▼
 Analytics Database
```

---

# Major Components

## Client

The client may be

- Web Browser
- Mobile App
- Another Service

It either

- Creates a short URL
- Opens an existing short URL

---

## Load Balancer

Responsibilities

- Distribute requests
- Health checks
- High availability
- Horizontal scalability

---

## API Gateway

Responsibilities

- Authentication
- Authorization
- Rate Limiting
- Logging
- Routing

---

## URL Shortening Service

Responsible for

- Validating URLs
- Generating IDs
- Base62 Encoding
- Supporting Custom Aliases
- Persisting mappings

---

## Redirect Service

Responsible for

- Receiving short URLs
- Looking up long URLs
- Returning HTTP Redirect

This service is extremely read-heavy.

---

## Redis

Caches

```
shortCode

↓

longUrl
```

Most redirects should never reach the database.

---

## Database

Stores

```
shortCode

↓

longUrl
```

This is the source of truth.

---

## Kafka

Redirects should be extremely fast.

Analytics should not slow them down.

Instead

```
Redirect

↓

Kafka

↓

Analytics
```

---

## Analytics Workers

Consume Kafka events

Compute

- Click Count
- Browser
- Device
- Country
- Referrer

Store analytics separately.

---

# Write Flow (Create Short URL)

The client creates a new URL.

```
Client

↓

POST /urls

↓

Load Balancer

↓

API Gateway

↓

URL Shortening Service
```

The service

- Validates URL
- Generates Unique ID
- Converts ID to Base62
- Stores Mapping

```
Database

↓

Return Short URL
```

Example

```
https://google.com

↓

ID = 983412

↓

Base62

↓

A9kLm2

↓

Store

↓

Return

tiny.ly/A9kLm2
```

---

# Read Flow (Redirect)

A user opens

```
tiny.ly/A9kLm2
```

The request flows

```
Browser

↓

Load Balancer

↓

Redirect Service

↓

Redis
```

---

## Cache Hit

```
Redis

↓

Long URL

↓

HTTP 302 Redirect
```

No database call.

Fast.

---

## Cache Miss

```
Redis

↓

MISS

↓

Database

↓

Redis

↓

HTTP 302
```

The cache is populated for future requests.

This is the **Cache-Aside Pattern**.

---

# Why Separate URL Creation and Redirect Services?

Traffic characteristics are different.

Creation

```
Few Writes
```

Redirect

```
Millions of Reads
```

Independent services allow

- Independent scaling
- Better resource utilization
- Easier deployment

---

# Synchronous vs Asynchronous

## Synchronous Path

```
Client

↓

Gateway

↓

Redirect Service

↓

Redis

↓

Database (only on cache miss)

↓

HTTP 302 Redirect
```

The user waits only for the redirect.

---

## Asynchronous Path

After returning the redirect

```
Redirect Service

↓

Kafka

↓

Analytics Worker

↓

Analytics DB
```

Analytics should never slow down redirects.

---

# Why HTTP 302?

The Redirect Service returns

```
HTTP 302 Found
```

with

```
Location:
https://www.example.com
```

The browser automatically requests the destination URL.

---

# Scaling Strategy

Each layer scales independently.

```
Load Balancer

↓

More Redirect Services

↓

Redis Cluster

↓

Read Replicas

↓

Kafka Partitions

↓

Analytics Workers
```

---

# Failure Handling

## Redis Down

Fallback

```
Redis

↓

Database
```

Higher latency

No downtime.

---

## Database Down

New URL creation fails.

Existing redirects may continue if Redis still contains mappings.

---

## Kafka Down

Redirects should continue.

Analytics events can be retried or temporarily buffered.

Analytics is not part of the critical path.

---

# Why This Architecture?

| Component | Responsibility |
|------------|----------------|
| Load Balancer | Distribute traffic |
| API Gateway | Authentication, Rate Limiting |
| URL Shortening Service | Generate short URLs |
| Redirect Service | Resolve short URLs |
| Redis | Fast lookups |
| Database | Source of truth |
| Kafka | Asynchronous analytics |
| Analytics Workers | Process click events |

---

# Trade-offs

Advantages

- Extremely fast redirects
- Read-heavy optimization
- Horizontal scalability
- Low latency
- Decoupled analytics
- Independent service scaling

Trade-offs

- Eventual consistency for analytics
- Cache invalidation complexity
- Additional operational components
- Cache misses increase latency

---

# Interview Summary

A client creates a short URL by sending a request through the Load Balancer and API Gateway to the URL Shortening Service. The service validates the URL, generates a unique ID, converts it to Base62, stores the mapping in the database, and returns the short URL.

When a user accesses the short URL, the request is routed to the Redirect Service, which first checks Redis for the mapping. On a cache hit, the service immediately returns an HTTP 302 redirect. On a cache miss, it retrieves the mapping from the database, updates Redis, and then returns the redirect. Analytics events are published asynchronously to Kafka and processed by background workers, ensuring that click tracking never impacts redirect latency.

---

# Key Takeaways

- Separate the write path from the read path.
- Optimize redirects using Redis.
- Use the Cache-Aside pattern for lookups.
- Keep analytics asynchronous using Kafka.
- Scale redirect services independently from URL creation services.
- Design the redirect path to be as short and fast as possible.

# URL Shortener – Part 5
# Deep Dive, Scaling & Interview Follow-ups

---

# Why This Chapter Matters

Drawing the architecture is only the beginning.

A senior interviewer will now ask questions like

- What if Redis goes down?
- What if one URL receives 100 million clicks?
- How do you shard the database?
- How do you prevent cache stampede?
- Why HTTP 301 vs 302?
- How do you support analytics?

This chapter answers those questions.

---

# 1. Cache-Aside Pattern

Most redirect requests should never hit the database.

Request

```
GET /Ab3Kd9
```

Flow

```
Redirect Service

↓

Redis

↓

Cache Hit?

↓

YES

↓

Return Long URL
```

Very fast.

---

## Cache Miss

If Redis doesn't contain the URL

```
Redirect Service

↓

Redis

↓

MISS

↓

Database

↓

Redis

↓

Return Long URL
```

This is called the **Cache-Aside Pattern**.

Advantages

- Simple
- Reduces database load
- Easy to implement

Trade-offs

- First request is slower
- Cache invalidation required

---

# 2. Hot Keys

Suppose a celebrity tweets

```
tiny.ly/chatgpt
```

The URL suddenly receives

```
100 Million Requests
```

All requests hit the same Redis key.

```
Redis

↓

One Hot Key

↓

CPU Spike
```

Even Redis can become overloaded.

---

## Possible Solutions

### Redis Replication

```
Redis Primary

↓

Replica 1

Replica 2

Replica 3
```

Read requests are distributed.

---

### Local In-Memory Cache

Each Redirect Service stores

```
Most Popular URLs
```

inside local memory.

```
Redirect Service

↓

Local Cache

↓

Redis

↓

Database
```

Most requests never reach Redis.

---

### CDN

Popular redirects can even be cached at the edge.

```
Browser

↓

CDN

↓

Redirect
```

No request reaches your servers.

---

# 3. Cache Stampede

Suppose a very popular URL expires from Redis.

```
Redis

↓

Key Removed
```

Immediately

```
100,000 Requests

↓

Database
```

The database becomes overloaded.

This is called a **Cache Stampede**.

---

## Solutions

### Request Coalescing

The first request reloads the cache.

Everyone else waits.

```
100 Requests

↓

One Database Query

↓

Redis Updated

↓

All Requests Return
```

---

### Distributed Lock

```
Acquire Lock

↓

One Server Reloads Cache

↓

Release Lock
```

Other servers wait until the cache is rebuilt.

---

### Refresh Before Expiry

Instead of waiting for expiration

Refresh the cache early.

```
Expires In

↓

30 Seconds

↓

Refresh
```

Users never experience a cache miss.

---

# 4. Database Read Replicas

Redirects are read-heavy.

```
Reads

>>

Writes
```

Instead of one database

```
Primary

↓

Replica 1

↓

Replica 2

↓

Replica 3
```

Reads

↓

Replicas

Writes

↓

Primary

---

# 5. Database Sharding

Suppose we store

```
20 Billion URLs
```

One database becomes too large.

Partition by

```
shortCode Hash
```

Example

```
Shard 1

A-F

Shard 2

G-L

Shard 3

M-R

Shard 4

S-Z
```

Or more commonly

```
hash(shortCode) % NumberOfShards
```

This distributes data evenly.

---

# 6. URL Expiration

Some URLs expire.

Example

```
Expires

July 31
```

Redirect Flow

```
Lookup

↓

Expired?

↓

YES

↓

HTTP 410 Gone
```

Instead of

```
302 Redirect
```

---

# 7. Custom Aliases

Example

```
tiny.ly/chatgpt
```

Flow

```
Alias Exists?

↓

YES

↓

HTTP 409 Conflict

↓

Choose Another Alias
```

---

# 8. HTTP 301 vs HTTP 302

Interviewers ask this surprisingly often.

---

## HTTP 301

```
Moved Permanently
```

Browser caches the redirect.

Future requests may bypass your service.

Advantages

- Faster
- Less traffic

Disadvantages

Hard to change later.

---

## HTTP 302

```
Temporary Redirect
```

Browser asks your server every time.

Advantages

- Track every click
- Destination can change

Most URL shorteners prefer

```
302
```

because analytics are important.

---

# 9. Analytics

Should redirects wait?

No.

Bad

```
Redirect

↓

Store Analytics

↓

Return
```

Analytics slows every request.

---

Better

```
Redirect

↓

Return 302

↓

Publish Event

↓

Kafka

↓

Analytics Worker

↓

Analytics DB
```

Redirect remains fast.

---

# 10. Redis Failure

Redis becomes unavailable.

Fallback

```
Redirect Service

↓

Database
```

Higher latency

No downtime.

---

# 11. Database Failure

If Redis already contains the URL

Redirects continue.

```
Redis

↓

302
```

New URLs cannot be created.

Cache gradually expires.

Eventually redirects fail.

---

# 12. Kafka Failure

Redirects should continue.

Analytics is non-critical.

Possible approaches

- Retry publishing
- Temporary local buffer
- Outbox Pattern (if analytics must never be lost)

---

# 13. Duplicate URLs

Suppose two users shorten

```
google.com
```

Possible strategies

Option 1

Create two different short URLs.

Simpler.

Supports independent ownership and analytics.

---

Option 2

Return the same short URL.

Saves storage.

Requires deduplication logic.

---

# 14. Monitoring

Important metrics

API

- Request Rate
- Error Rate
- Latency

Redis

- Cache Hit Ratio
- Memory Usage

Database

- Query Latency
- Connections

Kafka

- Consumer Lag

Analytics

- Processing Delay

---

# 15. Scaling Strategy

Scale every layer independently.

```
Load Balancer

↓

Redirect Services

↓

Redis Cluster

↓

Database Read Replicas

↓

Kafka

↓

Analytics Workers
```

---

# Common Interview Questions

## Q. Why use Redis?

To reduce database reads and provide low-latency redirects.

---

## Q. Why Cache-Aside?

Simple.

Only cache frequently accessed data.

---

## Q. What if Redis crashes?

Fallback to the database.

---

## Q. What if one URL becomes extremely popular?

Use

- Local Cache
- Redis Replicas
- CDN

---

## Q. Why use Kafka?

Analytics should never slow down redirects.

---

## Q. Why HTTP 302 instead of 301?

302 allows every request to reach the redirect service, enabling click tracking and destination updates.

---

## Q. Why not hash the long URL?

Hash collisions.

Difficult ownership.

Harder analytics.

Generated IDs are simpler.

---

# Final Architecture

```
Client

↓

Load Balancer

↓

API Gateway

↓

Redirect Service

↓

Redis

↓

Database

↓

302 Redirect

----------------------

Analytics Event

↓

Kafka

↓

Analytics Workers

↓

Analytics Database
```

---

# Key Takeaways

- URL Shorteners are highly read-heavy systems.
- Cache-Aside keeps most reads in Redis.
- Read Replicas reduce database load.
- Sharding supports billions of URLs.
- Hot keys require replication, local caching, or CDNs.
- Cache Stampede should be mitigated with request coalescing, distributed locks, or proactive refresh.
- Analytics should always be asynchronous.
- HTTP 302 is generally preferred over HTTP 301 for URL shorteners because it supports analytics and flexible redirects.
- Every layer should scale independently.

If URL creation scales to hundreds of millions of new URLs per day, the database sequence could become a bottleneck. At that point, I'd replace the centralized ID generator with a distributed scheme such as Snowflake IDs, allowing each application instance to generate globally unique IDs independently

I'd use a distributed ID generator such as Snowflake. Each application instance generates a 64-bit ID locally using a timestamp, a unique machine ID, and a per-millisecond sequence number. This avoids a centralized bottleneck, guarantees uniqueness as long as machine IDs are unique and clocks are managed correctly, and produces IDs that are roughly ordered by creation time.

"What happens if a server's clock moves backward?"

A strong answer is:

"That's one of the key challenges with Snowflake. The generator keeps track of the last timestamp it used. If the current system time is earlier than the last timestamp, it detects clock rollback. Depending on the implementation, it can either pause until the clock catches up, reject ID generation temporarily, or use a logical timestamp to ensure time never appears to move backwards. This prevents duplicate IDs."

# Distributed Rate Limiter – Part 1
# Before High-Level Design

---

# Problem Statement

Design a distributed Rate Limiter for an API platform.

The system should protect backend services from excessive traffic by limiting the number of requests a client can make within a specified time period.

Examples

```
100 requests/minute/user

1000 requests/minute/API Key

10 login attempts/hour/IP
```

The Rate Limiter should work correctly even when the application is running on many servers.

---

# Why Do We Need Rate Limiting?

Without rate limiting,

one client could send

```
1,000,000 Requests

↓

API

↓

CPU

↓

Memory

↓

Database

↓

Crash
```

Rate limiting protects

- APIs
- Databases
- Authentication Services
- Payment Systems
- Search APIs
- Third-party integrations

It also helps prevent abuse and denial-of-service attacks.

---

# Step 1 – Clarifying Questions

Before designing the system, gather requirements.

---

## Functional Questions

### 1. What should be rate limited?

Possible options

- User
- IP Address
- API Key
- OAuth Client
- Organization
- Endpoint

Assumption

Support all of the above.

---

### 2. Should different APIs have different limits?

Example

```
Login

↓

10/hour

Search

↓

1000/minute

Payment

↓

20/minute
```

Assumption

Yes.

---

### 3. Should rejected requests receive an error?

Assumption

Yes.

Return

```
HTTP 429

Too Many Requests
```

---

### 4. Should burst traffic be allowed?

Some systems allow temporary bursts.

Others enforce a constant rate.

Assumption

Allow controlled bursts.

---

### 5. Should limits be configurable?

Assumption

Yes.

Policies should be configurable without code changes.

---

## Non-Functional Questions

### Latency

The Rate Limiter should add

```
<5 ms
```

to request processing.

---

### Availability

High Availability.

The Rate Limiter should not become a single point of failure.

---

### Scalability

Support

Millions of users

Millions of requests per second

---

### Reliability

The Rate Limiter should behave consistently across multiple application servers.

---

# Step 2 – Functional Requirements

The system should support

- Limit requests by User
- Limit requests by IP
- Limit requests by API Key
- Configurable policies
- Return HTTP 429 when limits are exceeded
- Allow policy updates

---

# Step 3 – Non-Functional Requirements

The system should provide

- Low latency
- High throughput
- Horizontal scalability
- Fault tolerance
- Distributed consistency
- High availability

---

# Step 4 – Capacity Estimation

Assume

```
10 Million Active Users
```

Average

```
100 Requests/User/Day
```

Total

```
1 Billion Requests/Day
```

Average Requests/Second

```
≈11,500 requests/sec
```

Peak traffic

```
10×

≈115,000 requests/sec
```

This tells us

- We need an in-memory solution.
- A relational database is too slow for every request.
- Redis is a strong candidate.

---

# Step 5 – API Design

The Rate Limiter is usually internal.

Example

```
allowRequest(
    clientId,
    apiName
)
```

Returns

```
true

or

false
```

Example

```
allowRequest(
    "user123",
    "/payments"
)

↓

true
```

---

# Step 6 – Data Model

Redis Key

```
rate_limit:user123:/payments
```

Value

```
Current Request Count
```

Example

```
Key

rate_limit:user123:/payments

↓

Value

57
```

An expiration time is associated with the key.

Example

```
TTL

60 seconds
```

When the window expires,

Redis automatically removes the key.

---

# Database Choice

Redis is the preferred choice because

- In-memory
- Extremely fast
- Atomic operations
- Built-in expiration
- Distributed deployment support

A relational database would introduce unnecessary latency and contention.

---

# Transition to the Next Chapter

We now know

- What we're limiting
- Expected traffic
- Why Redis is a good fit
- How requests are identified

The next step is choosing the rate limiting algorithm.

Several algorithms exist, each with different trade-offs.

The next chapter compares

- Fixed Window
- Sliding Window
- Sliding Log
- Token Bucket
- Leaky Bucket

and explains when to use each.

# Distributed Rate Limiter – Part 2
# Rate Limiting Algorithms

---

# Why This Chapter Matters

There is no single "best" rate limiting algorithm.

Different systems have different requirements.

Some prioritize

- Simplicity

Others prioritize

- Fairness

Others prioritize

- Burst traffic

A senior engineer should understand the strengths and weaknesses of each algorithm.

---

# Overview

The five most common algorithms are

1. Fixed Window Counter
2. Sliding Window Counter
3. Sliding Log
4. Token Bucket
5. Leaky Bucket

---

# 1. Fixed Window Counter

The simplest algorithm.

Suppose the limit is

```
100 Requests

Per Minute
```

Create a counter.

```
Minute 10:00

↓

Counter = 0
```

Every request increments the counter.

```
Request

↓

Counter++

↓

Counter <= 100 ?

↓

YES

↓

Allow
```

At

```
10:01
```

Reset the counter.

---

## Example

```
Limit

100 Requests / Minute
```

User sends

```
100 Requests

10:00:59
```

Allowed.

One second later

```
10:01:00
```

Counter resets.

User sends

```
100 More Requests
```

Allowed again.

Total

```
200 Requests

Within 2 Seconds
```

Even though the limit is

```
100/minute
```

---

# Problem

This is called the **Boundary Problem**.

Traffic spikes occur at window boundaries.

---

## Advantages

- Very simple
- Fast
- Low memory

---

## Disadvantages

- Allows burst traffic
- Unfair around window boundaries

---

# 2. Sliding Window Counter

Instead of fixed one-minute buckets,

consider the previous

```
60 Seconds
```

at any point in time.

Suppose current time is

```
10:00:45
```

Count requests between

```
09:59:45

↓

10:00:45
```

This removes boundary spikes.

---

## Advantages

- Fairer
- Smooth traffic
- Lower burst probability

---

## Disadvantages

- More complex
- Requires additional bookkeeping

---

# 3. Sliding Log

Store the timestamp of every request.

Example

```
10:00:01

10:00:05

10:00:10

10:00:15
```

For every new request

Remove timestamps older than

```
60 seconds
```

Count remaining timestamps.

If

```
Count < Limit
```

Allow.

Otherwise

Reject.

---

## Advantages

Most accurate algorithm.

Perfect sliding window.

---

## Disadvantages

High memory usage.

Suppose

```
1 Million Users

×

100 Requests

↓

100 Million Timestamps
```

Expensive.

Rarely used for very large systems.

---

# 4. Token Bucket

One of the most popular algorithms.

Imagine a bucket.

```
Capacity

100 Tokens
```

Every request consumes

```
1 Token
```

If tokens remain

↓

Allow.

If bucket becomes empty

↓

Reject.

---

## Token Refill

Tokens refill continuously.

Example

```
10 Tokens

Every Second
```

Suppose the bucket size is

```
100
```

Initially

```
100 Tokens
```

User sends

```
40 Requests
```

Bucket now contains

```
60 Tokens
```

After

```
4 Seconds
```

Tokens refill.

```
100 Tokens
```

again.

---

## Advantages

- Allows controlled bursts
- Smooth traffic
- Very efficient
- Widely used

---

## Disadvantages

Slightly more complex than Fixed Window.

---

# 5. Leaky Bucket

Imagine a bucket with a hole.

Requests enter quickly.

```
100 Requests

↓

Bucket
```

Water leaves

```
10 Requests/Second
```

at a constant rate.

Even if

```
1000 Requests
```

arrive together,

the output remains steady.

---

## Advantages

Very smooth traffic.

Excellent for protecting downstream systems.

---

## Disadvantages

Burst requests experience delay.

---

# Visual Comparison

## Fixed Window

```
████████████████████

Reset

████████████████████
```

Traffic spikes at boundaries.

---

## Sliding Window

```
██████████████████
██████████████████
██████████████████
```

Smooth.

---

## Token Bucket

```
Tokens

██████████

↓

Consume

↓

Refill
```

Allows bursts.

---

## Leaky Bucket

```
Requests

██████████████

↓

Constant Output

████████
```

Smooth output.

---

# Algorithm Comparison

| Algorithm | Burst Support | Accuracy | Memory | Complexity |
|------------|--------------|-----------|----------|-------------|
| Fixed Window | Poor | Low | Low | Very Low |
| Sliding Window | Good | Medium | Medium | Medium |
| Sliding Log | Excellent | High | High | High |
| Token Bucket | Excellent | Medium | Low | Medium |
| Leaky Bucket | No | High | Low | Medium |

---

# Which Algorithm Should I Choose?

## Fixed Window

Choose when

- Simplicity matters
- Small systems

---

## Sliding Window

Choose when

- Fairness matters
- API Rate Limiting

---

## Sliding Log

Choose when

- Accuracy is critical
- Traffic volume is manageable

---

## Token Bucket

Choose when

- Burst traffic should be allowed
- High throughput
- Cloud APIs

Examples

- AWS
- Stripe
- API Gateways

---

## Leaky Bucket

Choose when

- Constant request rate is required

Examples

- Network routers
- Traffic shaping
- Streaming systems

---

# Which One Is Asked Most Often?

For backend interviews

Most common answer

```
Token Bucket
```

because

- Efficient
- Allows bursts
- Low memory
- Easy to distribute

---

# Interview Questions

## Why not Fixed Window?

Boundary problem.

Allows sudden bursts.

---

## Why not Sliding Log?

Very memory intensive.

Stores every request timestamp.

---

## Why Token Bucket?

Allows short bursts while enforcing a long-term average request rate.

---

## Why Leaky Bucket?

Protects downstream services by producing a constant output rate.

---

## Can Redis implement all of them?

Yes.

Redis supports

- Atomic Counters
- Sorted Sets
- Expiration
- Lua Scripts

making it suitable for every algorithm.

---

# Key Takeaways

- Fixed Window is simple but suffers from burst traffic at window boundaries.
- Sliding Window improves fairness by evaluating the previous rolling time interval.
- Sliding Log provides the most accurate rate limiting but consumes significantly more memory.
- Token Bucket allows controlled bursts while enforcing a long-term average rate and is the most common choice for API rate limiting.
- Leaky Bucket smooths traffic by processing requests at a constant rate, making it useful for protecting downstream services.
- There is no universally best algorithm; the right choice depends on the application's requirements.

# Distributed Rate Limiter – Part 3
# High-Level Design (HLD)

---

# Objective

Design a distributed Rate Limiter that

- Supports millions of users
- Works across multiple application servers
- Adds minimal latency
- Prevents abuse
- Is highly available
- Scales horizontally

---

# High-Level Architecture

```
                          Client
                             │
                             ▼
                     DNS / Load Balancer
                             │
                             ▼
                       API Gateway
                             │
                             ▼
                     Rate Limiter Service
                             │
                     Check Request Limit
                             │
                             ▼
                        Redis Cluster
                             │
                Allow?                 Reject?
                  │                      │
                  ▼                      ▼
          Backend Service        HTTP 429 Too Many Requests
```

---

# Why Place the Rate Limiter Before the Backend?

Suppose a malicious client sends

```
1 Million Requests
```

If requests reach the backend first

```
Client

↓

Backend

↓

Database

↓

Reject
```

The backend still wastes CPU, memory, and database connections.

Instead

```
Client

↓

Rate Limiter

↓

Reject Immediately
```

The backend never sees invalid requests.

---

# Major Components

## Client

The client could be

- Browser
- Mobile App
- API Consumer
- Another Microservice

---

## Load Balancer

Responsibilities

- Distribute traffic
- Health checks
- High availability

---

## API Gateway

Responsibilities

- Authentication
- Authorization
- Routing
- Rate Limiting (sometimes integrated)

Many API gateways (Kong, Envoy, NGINX, AWS API Gateway) provide built-in rate limiting.

---

## Rate Limiter Service

Responsibilities

- Identify the client
- Determine the applicable policy
- Check request count
- Allow or reject request
- Return HTTP 429 when the limit is exceeded

---

## Redis Cluster

Redis stores request counters.

Example

```
rate_limit:user123:/payments
```

Value

```
57
```

TTL

```
60 seconds
```

Redis automatically removes expired counters.

---

# Request Flow

Suppose

```
Limit

100 Requests / Minute
```

Current count

```
99
```

Client sends another request.

```
Client

↓

Gateway

↓

Rate Limiter

↓

Redis

↓

Counter = 99

↓

Increment

↓

100

↓

Allow

↓

Backend
```

The next request

```
Counter

101

↓

Reject

↓

HTTP 429
```

---

# Redis Operations

The Rate Limiter needs three operations.

1.

```
Read Counter
```

2.

```
Increment Counter
```

3.

```
Set Expiration
```

These must happen atomically.

---

# Why Atomic Operations?

Suppose two requests arrive simultaneously.

Server 1

```
Counter = 99
```

Server 2

```
Counter = 99
```

Both increment.

Both think

```
100
```

Both allow.

Now

```
101 Requests
```

were allowed.

Incorrect.

Atomic operations prevent race conditions.

---

# Redis Atomic Commands

Redis provides

```
INCR
```

which is atomic.

Example

```
INCR rate_limit:user123
```

No matter how many servers execute this,

Redis guarantees correctness.

---

# Why Not Store Counters in SQL?

Imagine

```
100,000 Requests / Second
```

Every request would perform

```
SELECT

UPDATE
```

Database contention would become severe.

Redis performs these operations in memory and is significantly faster.

---

# Distributed Deployment

Suppose we have

```
Load Balancer

↓

Server 1

Server 2

Server 3
```

If each server stores its own counter

```
Server 1

20 Requests

Server 2

20 Requests

Server 3

20 Requests
```

User effectively sends

```
60 Requests
```

while the limit is

```
20
```

Incorrect.

Instead

All servers use

```
One Shared Redis Cluster
```

Every request updates the same counter.

---

# Redis Key Design

Key format

```
rate_limit:{clientId}:{api}
```

Example

```
rate_limit:user123:/payments
```

Value

```
57
```

TTL

```
60 seconds
```

When TTL expires,

Redis automatically removes the key.

---

# Why TTL?

Without TTL

```
Counter

↓

Forever
```

The user would eventually be blocked permanently.

TTL resets the window automatically.

---

# Horizontal Scaling

Every component scales independently.

```
Load Balancer

↓

Gateway Instances

↓

Rate Limiter Instances

↓

Redis Cluster

↓

Backend Services
```

The Rate Limiter is stateless.

State lives in Redis.

This allows unlimited horizontal scaling.

---

# Failure Handling

## Redis Unavailable

Two possible strategies.

### Fail Open

```
Redis Down

↓

Allow Request
```

Advantages

- High availability
- Better user experience

Disadvantages

- Abuse possible

Suitable for

- Public APIs
- Low-risk systems

---

### Fail Closed

```
Redis Down

↓

Reject Request
```

Advantages

Protects backend.

Disadvantages

Legitimate users are rejected.

Suitable for

- Banking
- Payments
- Security-sensitive APIs

---

# Monitoring

Monitor

API

- Request Rate
- Rejection Rate
- Latency

Redis

- Memory Usage
- CPU
- Command Latency

Rate Limiter

- Allowed Requests
- Rejected Requests
- Redis Errors

---

# Why Redis?

Redis provides

- In-memory performance
- Atomic operations
- Built-in expiration (TTL)
- Horizontal scaling
- High throughput
- Low latency

This makes it ideal for distributed rate limiting.

---

# Trade-offs

Advantages

- Fast
- Horizontally scalable
- Distributed
- Simple architecture
- Shared counters across servers

Trade-offs

- Additional infrastructure
- Redis becomes a critical dependency
- Need replication and failover
- Multi-region deployments require additional design

---

# Interview Summary

A request first reaches the Load Balancer and API Gateway before entering the Rate Limiter Service. The service identifies the client and the applicable rate limit policy, then checks a shared Redis Cluster. Redis atomically increments a request counter with an expiration time. If the updated count remains within the configured limit, the request is forwarded to the backend service. Otherwise, the Rate Limiter immediately returns **HTTP 429 Too Many Requests**. Using Redis ensures low latency, atomic updates, automatic window expiration through TTL, and consistent enforcement across multiple application servers.

---

# Key Takeaways

- Place the Rate Limiter before the backend service.
- Keep the Rate Limiter stateless.
- Store counters in Redis.
- Use atomic operations to avoid race conditions.
- Use TTL to automatically reset rate limits.
- Share Redis across all application servers.
- Decide between Fail Open and Fail Closed based on business requirements.
- Scale API servers, Rate Limiter instances, and Redis independently.


# Distributed Rate Limiter – Part 4
# Redis Internals, Atomicity & Lua Scripts

---

# Why This Chapter Matters

Most candidates stop after saying

> "I'll store counters in Redis."

A senior interviewer immediately asks

> "How do you avoid race conditions?"

or

> "Why use Lua Scripts?"

This chapter answers those questions.

---

# The Problem

Suppose our limit is

```
100 Requests / Minute
```

Redis stores

```
Key

rate_limit:user123

↓

Value

57
```

Every request should

1. Increment the counter
2. Check the limit
3. Set expiration (if first request)

Sounds simple.

Unfortunately,

it's easy to implement incorrectly.

---

# Naive Implementation

```
GET Counter

↓

Increment

↓

Save

↓

Set Expiry
```

Example

```
counter = redis.get(key)

counter++

redis.set(key,counter)

redis.expire(key,60)
```

Looks correct.

It isn't.

---

# Race Condition

Suppose two requests arrive simultaneously.

```
Counter = 99
```

Server A

```
Reads 99
```

Server B

```
Reads 99
```

Both increment.

```
100
```

Both save

```
100
```

Both allow.

Actual requests

```
101
```

Allowed requests

```
100
```

One request was effectively lost.

---

# Solution

Never use

```
GET

↓

Increment

↓

SET
```

Instead

use

```
INCR
```

Redis executes

```
INCR
```

atomically.

---

# Why is INCR Atomic?

Redis processes commands on a single thread.

Example

```
Request A

↓

INCR

↓

Done

↓

Request B

↓

INCR

↓

Done
```

No two INCR operations execute simultaneously.

No race condition.

---

# Is INCR Enough?

Not quite.

We still need

```
TTL
```

Example

```
INCR

↓

EXPIRE
```

---

# The Hidden Bug

Suppose

Request A

```
INCR

↓

Counter = 1
```

Immediately after

Redis crashes

before

```
EXPIRE
```

executes.

Now

```
Counter

↓

Never Expires
```

The user eventually becomes permanently blocked.

---

# Another Race Condition

Suppose

Two requests arrive simultaneously.

Server A

```
INCR

↓

Counter = 1
```

Server B

```
INCR

↓

Counter = 2
```

Now both execute

```
EXPIRE 60
```

The expiration timer gets reset multiple times.

Depending on timing, the effective rate limit window can drift.

---

# Why MULTI / EXEC Isn't Ideal

Redis transactions

```
MULTI

↓

INCR

↓

EXPIRE

↓

EXEC
```

execute atomically as a group.

However,

they still execute every command,

even if the counter already exists.

Ideally,

we only want to set the TTL when the counter is created.

---

# Better Solution — Lua Script

Execute everything inside Redis.

```
Read Counter

↓

Increment

↓

If Counter == 1

↓

Set Expiry

↓

Return Count
```

All inside

one atomic script.

---

# Lua Script (Conceptual)

```lua
count = redis.call("INCR", key)

if count == 1 then
    redis.call("EXPIRE", key, 60)
end

return count
```

Everything runs as

one atomic operation.

No other Redis command runs in between.

---

# Why Lua Scripts?

Advantages

- Atomic
- Faster
- One network round trip
- Business logic executes inside Redis
- Prevents race conditions

---

# Request Flow

```
Client

↓

Rate Limiter

↓

Lua Script

↓

Redis

↓

Return Current Count

↓

Allowed?

↓

YES

↓

Backend

NO

↓

HTTP 429
```

---

# Token Bucket in Redis

Instead of

```
Counter
```

store

```
Current Tokens

Last Refill Time
```

Each request

```
Calculate New Tokens

↓

Subtract One Token

↓

Save Updated Values
```

Again,

all calculations should happen inside

one Lua Script.

---

# Sliding Window in Redis

Use

```
Sorted Set
```

Each request

```
Current Timestamp

↓

Add To Sorted Set

↓

Remove Old Entries

↓

Count Remaining Entries
```

If

```
Count < Limit
```

Allow.

Otherwise

Reject.

---

# Why Sorted Set?

Redis Sorted Sets store

```
Timestamp

↓

Request
```

efficiently.

Operations

```
ZADD

ZREMRANGEBYSCORE

ZCARD
```

make Sliding Window implementation straightforward.

---

# Hot Keys

Suppose one API key belongs to a large enterprise.

```
Google API

↓

100 Million Requests
```

One Redis key becomes

extremely hot.

---

## Possible Solutions

### Redis Cluster

Distribute keys across nodes.

---

### Local Cache

Cache policy,

not counters.

---

### Hierarchical Limits

Instead of

```
Company

↓

One Counter
```

Use

```
Company

↓

Department

↓

User
```

Spread load.

---

# Multi-Region Deployment

Suppose

```
US-East

US-West

Europe
```

Should all regions share one Redis?

Usually

No.

Latency becomes too high.

Instead

Regional rate limiting

or

Global quotas with asynchronous synchronization,

depending on business requirements.

---

# Monitoring

Monitor

Redis

- Command Latency
- Memory Usage
- CPU
- Evictions

Rate Limiter

- Allowed Requests
- Rejected Requests
- Lua Script Errors
- Redis Failures

---

# Interview Questions

## Why not use SQL?

Too slow.

High contention.

Poor scalability.

---

## Why not GET + SET?

Race conditions.

Lost updates.

---

## Why Redis INCR?

Atomic.

Fast.

Thread-safe.

---

## Why Lua Scripts?

Need multiple Redis operations

to execute atomically.

---

## Why Sorted Sets?

Efficient Sliding Window implementation.

---

## Why Redis TTL?

Automatically resets rate limit windows.

No cleanup jobs required.

---

## What if Redis fails?

Two strategies

### Fail Open

```
Allow Requests
```

Higher availability.

Possible abuse.

---

### Fail Closed

```
Reject Requests
```

Protects backend.

May reject legitimate users.

Choose based on business requirements.

---

# Common Interview Mistakes

❌ Using SQL counters

❌ Using GET → Increment → SET

❌ Forgetting TTL

❌ Ignoring race conditions

❌ Assuming Redis automatically solves every concurrency issue

❌ Not discussing Fail Open vs Fail Closed

❌ Ignoring multi-region deployments

---

# Key Takeaways

- Redis `INCR` is atomic and avoids lost updates.
- `INCR` followed by `EXPIRE` can introduce subtle bugs if implemented naively.
- Lua Scripts allow multiple Redis operations to execute atomically.
- Sliding Window implementations commonly use Redis Sorted Sets.
- Token Bucket implementations typically store the token count and last refill timestamp in Redis.
- Redis TTL automatically resets counters, eliminating manual cleanup.
- Choose between Fail Open and Fail Closed based on the application's risk profile.
- A distributed Rate Limiter should remain stateless, with Redis acting as the shared state store.

# Distributed Rate Limiter – Part 5
# Deep Dive, Scaling & Interview Follow-ups

---

# Why This Chapter Matters

The interviewer now assumes the Rate Limiter works.

The remaining discussion is about

- Scalability
- Failure handling
- Edge cases
- Trade-offs
- Production considerations

This is where senior candidates differentiate themselves.

---

# 1. Where Should Rate Limiting Happen?

Possible locations

```
Client
```

↓

Easy to bypass.

---

```
Application Server
```

↓

Backend resources already consumed.

---

```
API Gateway
```

↓

Best choice.

Reject requests before they reach backend services.

---

```
Load Balancer
```

↓

Possible for very simple IP-based rules,

but usually lacks business context.

---

# Recommended Architecture

```
Client

↓

Load Balancer

↓

API Gateway

↓

Rate Limiter

↓

Backend Services
```

---

# 2. Hierarchical Rate Limiting

Sometimes one limit is not enough.

Example

```
Organization

↓

100,000 requests/minute

↓

Users

↓

1,000 requests/minute
```

Now both limits are enforced.

Example

```
Company Limit

AND

User Limit
```

Both must succeed.

---

# 3. Endpoint Specific Limits

Different APIs require different protection.

Example

```
GET /search

↓

1000/minute
```

```
POST /payment

↓

20/minute
```

```
POST /login

↓

10/hour
```

Policy becomes

```
User

+

Endpoint
```

instead of

```
User Only
```

---

# 4. Burst Handling

Suppose

```
Limit

100 Requests/Minute
```

Should users be allowed

```
100 Requests

Immediately?
```

Depends.

Fixed Window

↓

Yes.

Token Bucket

↓

Controlled burst.

Leaky Bucket

↓

No.

---

# 5. Dynamic Policies

Different customers may have different plans.

Example

```
Free

↓

100 Requests/Minute
```

```
Premium

↓

1000 Requests/Minute
```

```
Enterprise

↓

Unlimited
```

Policies should come from configuration,

not hardcoded values.

---

# 6. Distributed Redis Cluster

Single Redis server

↓

Eventually becomes a bottleneck.

Use

```
Redis Cluster

Node 1

Node 2

Node 3
```

Keys are distributed across nodes.

Example

```
hash(userId)

↓

Redis Node
```

---

# 7. Hot Keys

Suppose one enterprise customer generates

```
50 Million Requests/Minute
```

One Redis key becomes extremely hot.

Possible solutions

- Redis Cluster
- Separate rate limits per department
- Local policy cache
- Increase Redis capacity

---

# 8. Multi-Region Deployments

Example

```
US-East

US-West

Europe
```

Question

Should all regions share one Redis?

Usually

No.

Reasons

- High latency
- Cross-region failures
- Expensive network traffic

Instead

Regional Redis clusters

with

Regional rate limiting.

Global quotas require additional synchronization.

---

# 9. Redis Replication

Redis Primary

↓

Replica

↓

Replica

Reads

↓

Replicas

Writes

↓

Primary

Remember

Rate limiting writes on every request.

Therefore

Most traffic still reaches the primary.

Replication mainly improves

- Availability
- Disaster recovery

not write scalability.

---

# 10. Memory Management

Millions of users

↓

Millions of Redis keys.

Fortunately

TTL automatically removes inactive counters.

Memory usage remains manageable.

---

# 11. Observability

Monitor

API

- Requests/sec
- Rejected requests
- Latency

Redis

- Memory
- CPU
- Command latency
- Evictions

Rate Limiter

- Allowed Requests
- Blocked Requests
- Redis Failures
- Lua Script Errors

Infrastructure

- Network
- Disk
- Availability

---

# 12. Security

Prevent attackers from bypassing limits.

Examples

- Trust authenticated user IDs over client-supplied identifiers.
- Validate API keys before applying limits.
- Apply additional IP-based limits for anonymous traffic.

---

# 13. Common Failure Scenarios

## Redis Down

Two options

### Fail Open

```
Allow Requests
```

Advantages

- High availability

Disadvantages

- Possible abuse

---

### Fail Closed

```
Reject Requests
```

Advantages

- Backend protected

Disadvantages

- Legitimate users blocked

Choose based on business requirements.

---

## Redis Slow

Use

- Timeouts
- Circuit Breakers
- Monitoring
- Fallback strategy

---

## Clock Synchronization

Sliding Window and Token Bucket depend on time.

Use a consistent time source.

Avoid relying on unsynchronized application server clocks.

---

# 14. Trade-offs

| Decision | Advantage | Trade-off |
|----------|-----------|-----------|
| Redis | Very fast | Extra infrastructure |
| Token Bucket | Supports bursts | Slightly more complex |
| Lua Scripts | Atomic | Harder to debug |
| API Gateway | Protects backend | Gateway becomes critical |
| Distributed Redis | Scalable | Operational complexity |

---

# 15. Interview Questions

### Why Redis instead of SQL?

Redis is in-memory, supports atomic operations, TTL, and much higher throughput.

---

### Why Token Bucket?

Allows controlled bursts while enforcing a long-term average rate.

---

### Why Lua Scripts?

Need conditional logic and multiple Redis operations to execute atomically.

---

### Why API Gateway?

Reject requests before they consume backend resources.

---

### What if Redis crashes?

Choose

- Fail Open
- Fail Closed

based on business requirements.

---

### How do you support different subscription plans?

Store policies separately and load them dynamically.

---

### How do you support millions of users?

- Stateless Rate Limiter
- Redis Cluster
- Horizontal scaling
- TTL cleanup
- Monitoring

---

# Final Architecture

```
                    Client
                       │
                       ▼
               Load Balancer
                       │
                       ▼
                 API Gateway
                       │
                       ▼
             Distributed Rate Limiter
                       │
                       ▼
                 Redis Cluster
                       │
          Allow?              Reject?
             │                  │
             ▼                  ▼
      Backend Services     HTTP 429
```

---

# Final Takeaways

- Place the Rate Limiter before backend services.
- Keep it stateless and store counters in Redis.
- Use atomic Redis operations or Lua Scripts for correctness.
- Token Bucket is the most common choice for API rate limiting.
- TTL automatically resets rate limit windows.
- Use Redis Cluster for scalability and high availability.
- Decide between Fail Open and Fail Closed based on risk.
- Monitor latency, rejection rates, Redis health, and memory usage.
- Design policies to be configurable and support different users, APIs, and subscription plans.

This is the level of depth typically expected in Senior Backend and Staff-level system design interviews.


# Real-Time Ad Click Analytics System – Part 2
# High-Level Design (HLD)

---

# Problem Statement

Design a system that tracks every ad click and provides

- Real-time click counts
- Live dashboards
- Historical reporting
- Fraud detection
- High throughput
- Low latency

---

# Clarifying Questions

Before starting, ask the interviewer:

### 1. How fresh should analytics be?

- Real-time (< 2 seconds)?
- Near real-time (30-60 seconds)?
- Batch (hourly/daily)?

This determines whether we need Kafka consumers or Flink.

---

### 2. Is every click equally important?

For example

- Marketing dashboard
- Billing advertisers
- Fraud detection

Billing systems usually require much stronger correctness guarantees.

---

### 3. What scale are we designing for?

Example

- 50K clicks/sec
- 500K clicks/sec

This affects Kafka partition count and storage.

---

### 4. Do we need historical reports?

If yes,

we'll store raw click events.

---

# Functional Requirements

- Track every click
- Live dashboard
- Historical reports
- Campaign analytics
- Country/device/browser breakdown
- Fraud detection

---

# Non Functional Requirements

- High Availability
- Low Latency
- Horizontally Scalable
- Fault Tolerant
- Event Driven

---

# High-Level Architecture

```
                        Users
                          │
                          ▼
                  Ad Redirect Service
                          │
           Return HTTP 302 Immediately
                          │
                          ▼
                 Publish Click Event
                          │
                          ▼
                        Kafka
            ┌─────────────┴─────────────┐
            │                           │
            ▼                           ▼
     Analytics Pipeline         Raw Event Storage
            │                           │
            ▼                           ▼
      Redis (Live Stats)        Analytics Database
            │
            ▼
     Dashboard / APIs
```

---

# Why Return Redirect Immediately?

Never delay the user.

Bad Design

```
Click

↓

Store Analytics

↓

Update DB

↓

Generate Metrics

↓

Redirect
```

Redirect becomes slow.

Good Design

```
Click

↓

HTTP 302

↓

Publish Event

↓

Background Processing
```

User experiences minimal latency.

---

# Components

## Ad Redirect Service

Responsibilities

- Validate click
- Generate Click Event
- Publish to Kafka
- Redirect user immediately

Should remain stateless.

---

## Kafka

Acts as the event backbone.

Every click becomes an immutable event.

Example

```
Click

↓

Kafka Topic

↓

Multiple Consumers
```

Advantages

- Decouples producers and consumers
- Durable
- Scalable
- Supports replay

---

## Analytics Pipeline

Consumes click events.

Initially

Simple Kafka Consumers.

Later

Can evolve into Flink for advanced stream processing.

Responsibilities

- Aggregate clicks
- Compute metrics
- Detect fraud
- Update dashboard cache

---

## Redis

Stores

```
Campaign123

↓

Clicks = 58,231
```

Optimized for

- Live dashboards
- Fast reads

---

## Analytics Database

Stores

Every click event.

Used for

- Historical reports
- Billing
- Auditing
- Reprocessing

Unlike Redis,

this is the source of truth for historical analytics.

---

# Request Flow

```
User Clicks Ad

↓

Redirect Service

↓

Publish Event

↓

Kafka

↓

Return HTTP 302

↓

Analytics Consumer

↓

Update Redis

↓

Store Raw Event

↓

Dashboard Refresh
```

Notice

Redirect and analytics are completely decoupled.

---

# Click Event Schema

Example

```json
{
  "clickId": "UUID",
  "campaignId": "CMP123",
  "adId": "AD987",
  "userId": "USR456",
  "timestamp": "2026-07-10T20:30:15Z",
  "country": "US",
  "device": "Mobile",
  "browser": "Chrome",
  "cost": 0.75
}
```

Important

Click events are immutable.

Never update them.

Always append new events.

---

# Why Kafka?

Without Kafka

```
Redirect Service

↓

Analytics DB

↓

Redis

↓

Fraud Detection
```

Redirect service becomes tightly coupled to every downstream system.

With Kafka

```
Redirect Service

↓

Kafka

├── Analytics
├── Billing
├── Fraud Detection
├── ML Pipeline
└── Reporting
```

Each consumer scales independently.

---

# Why Redis?

Dashboard queries should never scan billions of click records.

Instead

```
Dashboard

↓

Redis

↓

Current Metrics
```

Redis stores pre-computed aggregates for fast reads.

---

# Why Store Raw Events?

Suppose tomorrow the business asks

```
Show

Clicks

Grouped by Browser

For Last Month
```

If we only stored counters,

this query is impossible.

Raw events allow

- New reports
- Auditing
- Replay
- Backfilling metrics

---

# Scaling

Each component scales independently.

```
Load Balancer

↓

Redirect Service (Multiple Instances)

↓

Kafka Cluster

↓

Analytics Consumers

↓

Redis Cluster

↓

Analytics Database
```

---

# Failure Handling

If Redis fails

- Dashboard may show stale data
- Raw events remain safely stored

If Analytics Consumer crashes

- Kafka retains events
- Consumer resumes from the last committed offset

If Dashboard fails

- Analytics pipeline continues
- Metrics continue updating

---

# Trade-offs

Advantages

- Fast redirect
- Decoupled architecture
- Horizontally scalable
- Easy to add new consumers
- Durable event storage

Trade-offs

- Eventual consistency
- Additional infrastructure
- Kafka operational complexity
- Dashboard may briefly lag behind reality

---

# Interview Summary

The Redirect Service immediately returns the HTTP redirect to keep user latency low. Every click is published as an immutable event to Kafka, which acts as the central event bus. Independent consumers process these events asynchronously. Aggregated metrics are stored in Redis for low-latency dashboards, while raw click events are persisted in an Analytics Database for historical reporting, auditing, and replay. This architecture is scalable, fault tolerant, and naturally supports adding new consumers such as fraud detection, billing, and machine learning without modifying the Redirect Service.


I'd have the Redirect Service publish the click event because it's the first component that knows a click occurred and already has all the necessary metadata. This keeps the architecture simple and avoids introducing another service whose only responsibility would be forwarding events to Kafka. If click loss is unacceptable, I'd enhance the design using the Outbox Pattern so events are durably stored before being published asynchronously to Kafka.


---

# Key Takeaways

- Never block the redirect path for analytics.
- Publish click events asynchronously to Kafka.
- Treat click events as immutable.
- Store aggregated metrics in Redis.
- Store raw events for historical reporting and replay.
- Keep producers and consumers decoupled.
- Scale each component independently.

# Real-Time Ad Click Analytics System – Part 3
# Why Flink? Kafka Consumers vs Stream Processing

---

# The Problem

So far our architecture is

```
Users

↓

Redirect Service

↓

Kafka

↓

Analytics Consumer

↓

Redis

↓

Dashboard
```

Looks good.

Question

**Why isn't this enough?**

Why do companies use Flink?

---

# Scenario 1 – Simple Click Counter

Suppose Marketing only wants

```
Campaign A

↓

Total Clicks
```

Kafka Consumer is enough.

```
Kafka

↓

Consumer

↓

Redis INCR
```

Every click

```
INCR campaign123
```

Done.

No Flink required.

---

# Scenario 2 – Clicks Per Minute

Marketing asks

```
Clicks

Last Minute
```

Now things become harder.

Consumer needs to remember

```
Current Minute

↓

Current Count
```

Example

```
10:01

↓

523 Clicks

------------

10:02

↓

684 Clicks
```

Still manageable.

---

# Scenario 3 – Last 5 Minutes

Now Marketing changes requirement.

```
Show

Clicks

Last 5 Minutes
```

NOT

```
Current Minute
```

But

```
Rolling

5 Minute Window
```

Every second

the answer changes.

Now the consumer must

- remember old events
- remove expired events
- compute rolling counts

Logic becomes complicated.

---

# Scenario 4 – Top 10 Campaigns

Now they ask

```
Top Campaigns

Last 10 Minutes
```

Consumer now needs

- maintain counts
- continuously sort
- expire old clicks

Much harder.

---

# Scenario 5 – CTR

CTR

```
Clicks

/

Impressions
```

Problem

Clicks

and

Impressions

arrive as

different Kafka streams.

```
Impression Topic

↓

Kafka

------------

Click Topic

↓

Kafka
```

Need to join them.

Consumers become complicated.

---

# Scenario 6 – Fraud Detection

Business asks

```
If

IP

makes

100 clicks

within

10 seconds

↓

Raise Alert
```

Consumer now needs

- remember timestamps
- sliding windows
- expiration
- timers

Again,

complex.

---

# Kafka Consumer Starts Growing

```
Kafka

↓

Consumer

↓

Count Clicks

↓

Remember Time

↓

Sliding Window

↓

Top Campaigns

↓

CTR

↓

Fraud Detection

↓

Revenue

↓

Session Tracking
```

One consumer

becomes huge.

Hard to maintain.

---

# This Is Why Flink Exists

Flink specializes in

```
Continuous

Event Processing
```

Instead of writing all the logic yourself,

Flink provides

- Windows
- State
- Timers
- Joins
- Aggregations
- Checkpointing

out of the box.

---

# New Architecture

```
Users

↓

Redirect Service

↓

Kafka

↓

Flink

↓

Redis

↓

Dashboard
```

Instead of

```
Kafka Consumer

↓

Business Logic
```

we now have

```
Kafka

↓

Flink

↓

Business Logic
```

---

# What Does Flink Actually Do?

Imagine

```
Click

↓

Campaign 123
```

Flink immediately updates

```
Campaign123

Clicks++

```

Suppose another click arrives

```
Campaign123

↓

Clicks++
```

State is always current.

---

# Stateful Processing

Unlike a normal Kafka consumer,

Flink remembers things.

Example

```
Campaign123

↓

58231 Clicks
```

Flink keeps this in memory.

No need to query the database.

---

# Windowing

Suppose we want

```
Clicks

Last 5 Minutes
```

Flink automatically

- adds new clicks
- removes old clicks
- recomputes totals

No custom cleanup logic required.

---

# Stream Joins

CTR requires

```
Impressions

+

Clicks
```

Flink joins these streams continuously.

Consumer code becomes much simpler.

---

# Timers

Fraud Detection

```
100 Clicks

↓

10 Seconds

↓

Alert
```

Flink has built-in timers.

Consumers don't need to implement scheduling.

---

# Checkpointing

Suppose Flink crashes.

Without Flink

Consumer may lose in-memory state.

With Flink

```
Checkpoint

↓

Recover

↓

Continue
```

Processing resumes from the last checkpoint.

---

# Exactly Once

Kafka usually provides

```
At Least Once
```

delivery.

Meaning

duplicates are possible.

Flink supports

```
Exactly Once

Processing
```

through checkpointing and coordinated state management.

Very useful for

- Billing
- Revenue
- Financial Analytics

---

# Comparison

| Feature | Kafka Consumer | Apache Flink |
|----------|----------------|--------------|
| Simple Counter | ✅ | ✅ |
| Rolling Windows | Manual | Built-in |
| Sliding Windows | Manual | Built-in |
| Session Windows | Manual | Built-in |
| Stream Joins | Manual | Built-in |
| Stateful Processing | Manual | Built-in |
| Timers | Manual | Built-in |
| Checkpointing | Manual | Built-in |
| Exactly Once | Difficult | Built-in Support |

---

# When Is Kafka Consumer Enough?

Use simple consumers when

- Increment counters
- Store events
- Send notifications
- Simple ETL

Examples

```
Kafka

↓

Consumer

↓

Database
```

No Flink required.

---

# When Should You Introduce Flink?

Use Flink when you need

- Rolling windows
- Sliding windows
- Session windows
- Stream joins
- Stateful aggregations
- Real-time dashboards
- Fraud detection
- Event-time processing
- Exactly-once processing

---

# Interview Example

Interviewer

"Do we need Flink?"

Good Answer

> "Not initially. If the requirement is simply to count clicks and store events, Kafka consumers are sufficient. As requirements evolve to include rolling time windows, CTR calculations, fraud detection, sessionization, or real-time dashboards, I'd introduce Apache Flink because it provides built-in stream processing primitives such as state management, event-time windows, timers, joins, checkpointing, and exactly-once processing."

This demonstrates incremental system design rather than introducing unnecessary complexity.

---

# Key Takeaways

- Kafka transports events; it does not perform stream processing.
- Simple Kafka consumers are sufficient for straightforward event processing.
- As analytics become more complex, custom consumer logic becomes difficult to maintain.
- Flink provides built-in support for stateful stream processing, windowing, joins, timers, and checkpointing.
- Introduce Flink only when business requirements justify the additional operational complexity.

# Real-Time Ad Click Analytics System – Part 4
# Event Time, Processing Time, Watermarks & Windows

---

# Why This Chapter Matters

Suppose a user clicks an advertisement at

```
10:00:00
```

Due to network delay,

Kafka receives the event at

```
10:00:03
```

Flink processes it at

```
10:00:05
```

Question:

Which timestamp should analytics use?

This is one of the most important concepts in stream processing.

---

# Three Different Times

Every event can have three timestamps.

```
Event Time

↓

When the click actually happened

----------------------------

Ingestion Time

↓

When Kafka received it

----------------------------

Processing Time

↓

When Flink processed it
```

Example

```
User Clicks

10:00:00

↓

Kafka

10:00:03

↓

Flink

10:00:05
```

---

# Which Time Should Analytics Use?

Suppose Marketing asks

```
Clicks

Between

10:00

and

10:01
```

Should this click count?

Yes.

It happened at

```
10:00:00
```

Even though Flink processed it later.

Therefore

analytics should use

```
Event Time
```

not

Processing Time.

---

# Why Processing Time Is Wrong

Suppose

Network delay

```
20 seconds
```

Click

```
10:00:50
```

Processed

```
10:01:10
```

If we use processing time,

this click is counted in

```
10:01
```

Wrong.

The user actually clicked during

```
10:00
```

---

# Event Time

Every click event contains

```json
{
    "clickId":"123",
    "campaignId":"A1",
    "eventTime":"10:00:50"
}
```

Notice

the timestamp travels with the event.

Flink performs analytics using

```
eventTime
```

---

# Problem

Events Do Not Always Arrive In Order

Suppose

```
Click A

10:00:01

↓

Arrives Immediately
```

```
Click B

10:00:02

↓

Network Delay

↓

Arrives 20 Seconds Later
```

Arrival order becomes

```
Click A

↓

Click C

↓

Click D

↓

Click B
```

Out of order.

---

# Without Special Handling

Suppose Flink closes

```
10:00 Window
```

before Click B arrives.

Click B gets ignored.

Analytics become incorrect.

---

# Watermarks

Watermarks solve this problem.

Think of a watermark as

```
"I'm willing to wait

a little longer

for late events."
```

Example

```
Allowed Delay

30 Seconds
```

Now

```
10:00 Window

↓

Wait Until

10:00:30

↓

Close Window
```

Late events arriving within

30 seconds

are still counted.

---

# Example

Clicks

```
10:00:01

10:00:05

10:00:10

10:00:15
```

One click arrives late

```
Event Time

10:00:12

Arrival Time

10:00:25
```

Watermark

```
30 Seconds
```

The event is still accepted.

Correct analytics.

---

# What Happens If It Arrives Too Late?

Suppose

Watermark

```
30 Seconds
```

Late click

```
2 Minutes Late
```

Window already closed.

Choices

- Ignore
- Send to late-event stream
- Reprocess later

Depends on business requirements.

---

# Windowing

Instead of computing

all clicks forever,

Flink groups events into windows.

Several types exist.

---

# 1. Tumbling Window

Fixed-size,

non-overlapping windows.

Example

```
10:00-10:01

↓

523 Clicks

----------------

10:01-10:02

↓

610 Clicks
```

Good for

- Reports
- Billing
- Minute statistics

---

# Visualization

```
|-----1 Min-----|

|-----1 Min-----|

|-----1 Min-----|
```

No overlap.

---

# 2. Sliding Window

Suppose Marketing asks

```
Clicks

Last

5 Minutes
```

Updated

Every Minute.

Windows become

```
10:00-10:05

10:01-10:06

10:02-10:07
```

Notice

they overlap.

---

# Visualization

```
|---------5 Min---------|

      |---------5 Min---------|

            |---------5 Min---------|
```

---

# Good For

- Live dashboards
- Trending campaigns
- Real-time analytics

---

# 3. Session Window

Instead of

fixed time,

group activity

by user behavior.

Example

User clicks

```
10:00

10:01

10:03
```

Then

no activity

for

```
30 Minutes
```

New click

```
10:35
```

Starts

a new session.

---

# Visualization

```
Clicks

● ● ●

-----------30 min------------

● ●
```

Useful for

- User sessions
- Shopping carts
- Website analytics

---

# Which Window Should I Use?

| Requirement | Window |
|------------|---------|
| Hourly Report | Tumbling |
| Last 5 Minutes | Sliding |
| User Session | Session |

---

# Example Dashboard

Marketing wants

```
Campaign A

↓

Clicks

Last 5 Minutes
```

Every second,

the number changes.

Sliding Window

is perfect.

---

# Example Billing

Advertiser pays

```
Every Hour
```

Use

Tumbling Window.

No overlap.

Simple billing.

---

# Example Fraud

Detect

```
100 Clicks

Within

10 Seconds
```

Sliding Window

works well.

---

# Complete Flow

```
User Click

↓

Redirect Service

↓

Kafka

↓

Flink

↓

Event Time

↓

Watermark

↓

Sliding Window

↓

Aggregate

↓

Redis

↓

Dashboard
```

---

# Interview Questions

## Why Event Time?

Because network delays should not change analytics.

---

## Why Watermarks?

To tolerate late-arriving events while still producing timely results.

---

## Why Tumbling Window?

Fixed reporting intervals.

No overlap.

---

## Why Sliding Window?

Continuous rolling analytics.

---

## Why Session Window?

Groups user activity instead of fixed time intervals.

---

## What if events arrive too late?

Options

- Ignore
- Send to late-event processing
- Recompute historical reports

Depends on business requirements.

---

# Key Takeaways

- Event Time represents when the event actually occurred.
- Processing Time represents when the stream processor handled the event.
- Analytics almost always use Event Time.
- Watermarks allow Flink to wait for late-arriving events before closing a window.
- Tumbling Windows are fixed and non-overlapping.
- Sliding Windows overlap and are ideal for rolling metrics.
- Session Windows group events based on user inactivity rather than fixed time intervals.
- Choosing the correct window type depends on the business requirement rather than the technology.

# Real-Time Ad Click Analytics System – Part 5
# Exactly Once, Checkpointing, Scaling & Fault Tolerance

---

# Why This Chapter Matters

Suppose a user clicks an advertisement.

```
Click

↓

Kafka

↓

Flink

↓

Redis

↓

Dashboard
```

Now imagine

Flink crashes.

Questions

- Is the click lost?
- Is it counted twice?
- Can processing resume?

These are the problems this chapter solves.

---

# Delivery Guarantees

There are three common delivery guarantees.

```
At Most Once

↓

At Least Once

↓

Exactly Once
```

---

# 1. At Most Once

Event is processed

```
0

or

1 Time
```

If failure occurs

↓

Event may be lost.

Example

```
Kafka

↓

Consumer

↓

Crash

↓

Lost Event
```

Suitable for

- Logs
- Metrics

Not suitable for billing.

---

# 2. At Least Once

Kafka guarantees

```
Event

↓

One Or More Times
```

If consumer crashes

Kafka redelivers.

Example

```
Click

↓

Processed

↓

Crash

↓

Processed Again
```

Problem

Duplicate processing.

---

# Example

Campaign

```
100 Clicks
```

One click processed twice.

Dashboard

```
101

↓

102
```

Actual

```
101
```

Incorrect.

---

# 3. Exactly Once

Ideal behavior.

Every event affects the system

```
Exactly One Time
```

No loss.

No duplicates.

This is important for

- Billing
- Revenue
- Financial reports

---

# How Flink Achieves Exactly Once

Flink uses

```
Checkpointing
```

Think of it as

saving the current progress.

---

# Example

Suppose Flink has processed

```
1 Million Events
```

Checkpoint

```
Offset = 1,000,000

State Saved
```

If Flink crashes

```
Restart

↓

Restore Checkpoint

↓

Continue
```

No need to start over.

---

# What Is Checkpointing?

Checkpoint stores

- Current Kafka offsets
- Current Flink state
- Window state
- Counters
- Timers

Everything required to continue processing.

---

# Example

Campaign

```
Campaign123

↓

Clicks

58231
```

Checkpoint

```
Campaign123

↓

58231
```

Crash.

Restart.

Restore

```
58231
```

Continue

```
58232

58233
```

---

# Kafka Offset Management

Kafka stores

```
Offset

0

1

2

3

4
```

Suppose Flink processed

```
Offset

1000
```

Checkpoint records

```
Offset = 1000
```

Crash.

Restart.

Kafka resumes from

```
1001
```

No duplicate processing.

---

# Scaling Kafka

Suppose

```
500,000 Clicks/sec
```

One Kafka partition

isn't enough.

Create

```
Kafka Topic

↓

Partition 1

Partition 2

Partition 3

Partition 4
```

---

# Partitioning Strategy

Partition by

```
Campaign ID
```

Example

```
Campaign A

↓

Partition 3
```

Every click for Campaign A

goes to the same partition.

Advantages

- Ordering preserved
- Stateful processing simplified

---

# Scaling Flink

Each Kafka partition

can be processed by

one Flink task.

```
Kafka

Partition1

↓

Flink Task1

----------------

Partition2

↓

Flink Task2

----------------

Partition3

↓

Flink Task3
```

Scale horizontally.

---

# Why Partition by Campaign?

Suppose

Campaign A

receives

```
1 Million Clicks
```

All clicks for that campaign

go to the same Flink task.

State remains consistent.

---

# Redis Updates

Suppose Flink computes

```
Campaign123

↓

58231 Clicks
```

Redis stores

```
campaign123

↓

58231
```

Dashboard reads Redis.

Never computes analytics itself.

---

# Historical Database

Redis stores

Current aggregates.

Analytics Database stores

Every click.

Example

```
Click

↓

Analytics DB
```

Useful for

- Historical reports
- Auditing
- Replay
- Backfilling metrics

---

# What If Redis Crashes?

Redis is only a cache.

Dashboard

may temporarily lose live metrics.

Raw events remain safe in Kafka and the Analytics Database.

Rebuild Redis by replaying events.

---

# What If Kafka Crashes?

Kafka replicates partitions.

Example

```
Partition 1

↓

Leader

↓

Follower

↓

Follower
```

If leader fails

Follower becomes

new leader.

Processing continues.

---

# What If Flink Crashes?

```
Crash

↓

Restart

↓

Restore Checkpoint

↓

Continue
```

No manual intervention.

---

# Hot Campaign Problem

Suppose

```
World Cup Final

↓

Campaign A

↓

10 Million Clicks
```

One partition becomes hot.

Possible solutions

- Increase partitions
- Use campaign + region as partition key
- Hierarchical aggregation
- Additional aggregation stage

---

# Monitoring

Monitor

Kafka

- Consumer Lag
- Throughput
- Partition Health

Flink

- Checkpoint Duration
- Checkpoint Failures
- Processing Latency
- Backpressure

Redis

- Memory
- Latency
- Hit Rate

Dashboard

- Refresh Latency

---

# Trade-offs

Advantages

- Near real-time analytics
- Fault tolerant
- Horizontally scalable
- Exactly-once processing
- Durable event storage

Trade-offs

- Operational complexity
- Multiple distributed systems
- Checkpoint storage overhead
- Eventual consistency

---

# Final Architecture

```
                 Users
                   │
                   ▼
          Ad Redirect Service
                   │
          HTTP 302 Immediately
                   │
                   ▼
                 Kafka
      ┌────────────┴────────────┐
      │                         │
      ▼                         ▼
   Apache Flink          Analytics DB
      │
      ▼
    Redis
      │
      ▼
 Live Dashboard
```

---

# Interview Questions

## Why Kafka?

Reliable event streaming and decoupling between producers and consumers.

---

## Why Flink?

Real-time stateful stream processing, windowing, joins, timers, and exactly-once support.

---

## Why Redis?

Serve pre-computed metrics with very low latency.

---

## Why Analytics Database?

Historical reporting, replay, auditing, and billing.

---

## What happens if Flink crashes?

Restore from the latest checkpoint and resume processing from the corresponding Kafka offsets.

---

## Why partition by Campaign ID?

Preserves ordering and ensures all events for a campaign are processed by the same Flink task.

---

## What if Redis crashes?

Redis can be rebuilt because Kafka and the Analytics Database contain the source data.

---

# Key Takeaways

- Kafka provides durable event storage and scalable event distribution.
- Flink performs stateful stream processing on top of Kafka.
- Checkpoints capture both processing state and Kafka offsets.
- Exactly-once processing prevents duplicate analytics and is essential for financial use cases.
- Redis stores live aggregates for dashboards, while the Analytics Database stores the complete event history.
- Partitioning by Campaign ID preserves ordering and simplifies stateful processing.
- A production system should monitor consumer lag, checkpoint health, processing latency, and backpressure.
We can rebuild the in-memory state by replaying events from Kafka starting from the appropriate offset. However, replaying a large volume of events after every restart is expensive and increases recovery time. Stream processors like Flink periodically checkpoint both the processing state and the Kafka offsets, allowing them to restore state quickly and resume processing from the checkpoint rather than replaying the entire event history."

# Apache Flink - System Design Interview Summary

## Why not just use Kafka Consumers?

Simple Kafka consumers work well for straightforward tasks such as

- Incrementing counters
- Writing events to a database
- Sending notifications

However, as analytics requirements grow, the consumer must implement increasingly complex logic like:

- Sliding/Tumbling windows
- Stateful aggregations
- Stream joins
- Timers
- Fault recovery
- Checkpointing

Instead of building and maintaining all of this ourselves, we use Apache Flink.

---

## What is Flink?

Apache Flink is a distributed **stream processing engine**.

Kafka is responsible for **transporting events**, while Flink is responsible for **processing those events in real time**.

```
Kafka
    ↓
Flink
    ↓
Redis / Database / Dashboard
```

---

## Why use Flink?

Flink provides built-in support for:

- Stateful stream processing
- Time-based windows (Sliding, Tumbling, Session)
- Stream joins (e.g., Clicks + Impressions for CTR)
- Event-time processing and Watermarks
- Checkpointing and fault recovery
- Exactly-once processing (when supported by the sink)

This allows us to build complex real-time analytics without implementing these mechanisms ourselves.

---

## When would I introduce Flink?

I would **not** introduce Flink initially.

If the requirements are simply:

- Count clicks
- Store events
- Update Redis

then Kafka consumers are sufficient.

As the system evolves to require:

- "Clicks in the last 5 minutes"
- Rolling windows
- Top campaigns
- CTR calculations
- Fraud detection
- Real-time dashboards

I would replace simple Kafka consumers with Flink.

This keeps the initial design simple while allowing the system to evolve as business requirements become more complex.

---

## Interview Answer (30 seconds)

"I'd start with Kafka consumers because they're sufficient for simple event processing. As analytics requirements evolve to include rolling time windows, stateful aggregations, stream joins, fraud detection, and real-time dashboards, I'd introduce Apache Flink. Flink is a distributed stream processing engine that provides built-in support for state management, windowing, event-time processing, checkpointing, and fault recovery, allowing us to implement complex real-time analytics without writing all of that logic ourselves."

"Can dashboards query Flink directly?"

A strong answer is:

"Technically yes, because Flink maintains processing state, but I wouldn't design it that way. Flink is optimized for stream processing, not serving high volumes of interactive queries. I'd have Flink continuously compute aggregates and publish them to a serving layer such as Redis. The dashboard would query Redis for low-latency reads while Flink focuses on processing the event stream."

Our live dashboard is backed by Redis, which is a cache. If Flink crashes, it restores its checkpointed state and replays events from Kafka starting at the checkpoint offset. During replay, Redis may receive duplicate updates because it doesn't participate in Flink's checkpoint protocol. This is acceptable because Redis is eventually consistent and can always be rebuilt from the source of truth. For systems requiring strong consistency, such as billing, raw events are persisted in an analytics database (or another exactly-once sink), and billing is computed from that durable, correct event history rather than from Redis.

                    Users
                      │
                      ▼
              Redirect Service
                      │
                Publish Event
                      │
                      ▼
                    Kafka
                ┌─────────────┐
                ▼             ▼
          Analytics DB      Flink
       (Source of Truth)      │
                │             ▼
                │          Redis
                │             │
                ▼             ▼
          Billing Jobs    Live Dashboard
		  
Notice the separation:

Analytics DB → correctness, history, replay, billing.
Redis → speed, live dashboard.
Flink → real-time computation.

That's a very common production architecture and is exactly how I'd present it in a senior system design interview.

If billing must be real-time,

then instead of Redis,

Flink writes to an exactly-once transactional sink (or another sink that supports exactly-once semantics).

Kafka

↓

Flink

↓

Transactional DB

↓

Billing API

"Yes, dashboards can read directly from the analytics database. If the database is designed for analytical queries—such as ClickHouse, Pinot, or Druid—it may even eliminate the need for Redis. However, if we're using a traditional relational database or expect very high dashboard query volume, I'd introduce Redis to offload reads, reduce database load, and provide sub-millisecond response times. The choice depends on the expected read traffic and the capabilities of the serving database."

The key takeaway is: Redis is not mandatory. It's a performance optimization that becomes valuable when dashboard read traffic is high or when your primary database isn't optimized for serving those interactive queries.

# Apache Flink for Real-Time Analytics - System Design Interview Notes

---

# When Should I Introduce Flink?

I would **not** introduce Flink initially.

If the requirements are simple, such as:

- Count clicks
- Store events
- Update Redis
- Send notifications

then simple Kafka consumers are sufficient.

As the business requirements evolve to include:

- Rolling time windows
- Stateful aggregations
- Stream joins
- Fraud detection
- Real-time dashboards
- Session tracking

I would introduce Apache Flink.

---

# 30-Second Interview Answer

> I'd start with Kafka consumers because they're sufficient for simple event processing. As analytics requirements evolve to include rolling time windows, stateful aggregations, stream joins, fraud detection, and real-time dashboards, I'd introduce Apache Flink. Flink is a distributed stream processing engine that provides built-in support for state management, windowing, event-time processing, checkpointing, and fault recovery, allowing us to implement complex real-time analytics without writing all of that logic ourselves.

---

# Why Flink?

Kafka is responsible for transporting events.

Flink is responsible for processing events.

```
Kafka
    ↓
Flink
    ↓
Redis / Database / Dashboard
```

Flink provides built-in support for:

- Stateful stream processing
- Sliding, Tumbling and Session windows
- Stream joins (e.g., Clicks + Impressions → CTR)
- Event-time processing
- Watermarks
- Checkpointing
- Fault recovery
- Exactly-once processing (when supported by the sink)

Instead of implementing all these features manually inside Kafka consumers, Flink provides them out of the box.

---

# Can Dashboards Query Flink Directly?

Technically **yes**, because Flink maintains processing state.

However, I would **not** design the system that way.

Flink is optimized for **stream processing**, not for serving thousands of concurrent dashboard requests.

Instead, Flink continuously computes aggregates and writes them to a serving layer such as Redis.

The dashboard simply reads Redis.

---

## Interview Answer

> Technically yes, because Flink maintains processing state, but I wouldn't design it that way. Flink is optimized for stream processing, not serving high volumes of interactive queries. I'd have Flink continuously compute aggregates and publish them to a serving layer such as Redis. The dashboard would query Redis for low-latency reads while Flink focuses on processing the event stream.

---

# Typical Production Architecture

```
                    Users
                      │
                      ▼
              Redirect Service
                      │
                Publish Event
                      │
                      ▼
                    Kafka
                ┌─────────────┐
                ▼             ▼
          Analytics DB      Flink
       (Source of Truth)      │
                │             ▼
                │          Redis
                │             │
                ▼             ▼
          Billing Jobs    Live Dashboard
```

---

# Responsibilities of Each Component

### Kafka

- Durable event log
- Decouples producers from consumers
- Allows replay after failures

---

### Flink

- Real-time stream processing
- Stateful aggregations
- Windowing
- Event-time processing
- Watermarks
- Checkpointing
- Fault recovery

Flink is the **compute layer**.

---

### Redis

- Dashboard cache
- Low-latency reads
- Stores pre-computed aggregates

Redis is the **serving layer**.

---

### Analytics Database

- Source of truth
- Stores every raw click event
- Historical reporting
- Replay
- Auditing
- Billing

Analytics DB is the **correctness layer**.

---

# Why Separate Analytics DB and Redis?

Analytics DB and Redis serve completely different purposes.

| Analytics DB | Redis |
|--------------|--------|
| Source of truth | Cache |
| Historical data | Live metrics |
| Billing | Dashboard |
| Replay | Fast reads |
| Correctness | Performance |

---

# What Happens If Flink Crashes?

Current checkpoint

```
Offset = 1000

Campaign A Count = 100
```

Suppose Flink processes events

```
1001

↓

1002

↓

...

↓

1015
```

before crashing.

After restart

- Flink restores the checkpoint.
- Processing state is restored.
- Kafka replay starts from Offset 1000.
- Events 1001-1015 are processed again

		  
# Final Architecture - Real-Time Ad Click Analytics System

```text
                          Users
                            │
                            ▼
                    Ad Redirect Service
                            │
            Publish Click Event + HTTP 302 Redirect
                            │
                            ▼
                          Kafka
                            │
        ┌───────────────────┴───────────────────┐
        │                                       │
        ▼                                       ▼
   Apache Flink                        Click Event Store
(Real-Time Processing)               (Source of Truth)
        │
        ├──────────────┐
        │              │
        ▼              ▼
   Redis Cache    Transactional DB*
 (Live Dashboard) (Optional - Real-time Billing)
        │              │
        ▼              ▼
   Live Dashboard   Billing API

* Only required if billing must be real-time with exactly-once guarantees.
```

---

# Responsibilities

## Ad Redirect Service

- Receives user click
- Publishes immutable Click Event to Kafka
- Immediately returns HTTP 302 redirect
- Does **not** perform analytics

---

## Kafka

Acts as the central event bus.

Responsibilities:

- Durable event log
- Decouples producers and consumers
- Allows replay after failures
- Multiple downstream consumers

---

## Apache Flink

Acts as the **real-time compute engine**.

Responsibilities:

- Stateful processing
- Sliding/Tumbling/Session windows
- CTR calculations
- Fraud detection
- Aggregations
- Checkpointing
- Fault recovery

Flink continuously computes metrics.

It is **not** a serving layer.

---

## Redis

Acts as the **dashboard cache**.

Stores:

- Click counts
- Revenue
- Top campaigns
- CTR
- Trending ads

Purpose:

- Very low latency reads
- Dashboard queries

Redis is **not** the source of truth.

---

## Click Event Store (Analytics DB)

Stores every raw click event.

Example:

```json
{
  "clickId":"123",
  "campaignId":"A1",
  "userId":"U10",
  "timestamp":"2026-07-11T10:01:05Z",
  "device":"Mobile",
  "country":"US"
}
```

Purpose:

- Historical reports
- Replay
- Auditing
- Machine Learning
- Data warehouse
- Source of truth

---

## Billing (Optional)

If billing must be real-time,

Flink writes to a transactional database that supports exactly-once semantics.

```
Kafka

↓

Flink

↓

Transactional DB

↓

Billing API
```

This avoids duplicate billing.

---

# Failure Handling

## If Flink crashes

```
Checkpoint

↓

Restore State

↓

Replay Kafka Events

↓

Continue Processing
```

No events are lost.

---

## What happens to Redis?

Redis may receive duplicate updates during replay because it is **not** coordinated with Flink checkpoints.

This is acceptable because:

- Redis is only a cache.
- Dashboards are eventually consistent.
- Redis can always be rebuilt from the Click Event Store.

---

## Why not serve dashboards directly from Flink?

Technically possible.

Not recommended.

Reason:

Flink is optimized for **processing streams**, not serving thousands of concurrent dashboard queries.

Instead:

```
Flink

↓

Redis

↓

Dashboard
```

---

## Why not serve dashboards directly from the Click Event Store?

Possible for:

- Small systems
- Low dashboard traffic
- Analytics databases like ClickHouse, Pinot or Druid

For high dashboard traffic,

Redis significantly reduces read load and provides sub-millisecond lookups.

---

# Separation of Responsibilities

| Component | Responsibility |
|------------|----------------|
| Redirect Service | Capture click and publish event |
| Kafka | Durable event bus |
| Flink | Real-time computation |
| Redis | Live dashboard cache |
| Click Event Store | Source of truth, history, replay |
| Transactional DB (optional) | Exactly-once billing |

---

# Interview Summary

> The Redirect Service immediately publishes click events to Kafka and returns an HTTP 302 response to keep user latency low. Kafka acts as the durable event bus. Apache Flink performs real-time stream processing such as aggregations, windowing, CTR calculations, and fraud detection. Live metrics are written to Redis for low-latency dashboards, while every raw click event is stored in the Click Event Store, which serves as the source of truth for historical reporting, replay, auditing, and billing. If Flink crashes, it restores its checkpointed state and replays events from Kafka. Redis may temporarily contain duplicate values during replay because it is a cache, but it can always be rebuilt from the source of truth. If billing requires strong consistency, Flink writes to a transactional database supporting exactly-once semantics instead of relying on Redis.


# Real-Time Ad Click Analytics - Final Production Architecture

---

# Final Architecture

```text
                          Users
                            │
                            ▼
                    Ad Redirect Service
                            │
            Publish Click Event + HTTP 302 Redirect
                            │
                            ▼
                          Kafka
                 ┌──────────┴──────────┐
                 ▼                     ▼
         Apache Flink          Storage Consumer
      (Real-time Compute)     (Persist Raw Events)
                 │                     │
                 ▼                     ▼
             Redis Cache       Click Event Store
                 │
                 ▼
          Live Dashboard

Batch Billing

Click Event Store
        │
        ▼
 Billing Batch Job
        │
        ▼
   Billing DB / Invoice
```

---

# Responsibilities

## 1. Ad Redirect Service

Responsibilities

- Receives user click
- Publishes Click Event to Kafka
- Immediately returns HTTP 302 Redirect
- Never performs analytics

---

## 2. Kafka

Acts as the central event bus.

Responsibilities

- Durable event log
- Decouples producers and consumers
- Allows replay after failures
- Supports multiple independent consumers

---

## 3. Storage Consumer

Consumes click events from Kafka.

Responsibilities

- Persist every raw click event
- Insert into Click Event Store
- No analytics
- No aggregation

Pseudo Code

```java
while(true){

    ClickEvent event = kafka.read();

    clickRepository.save(event);

}
```

This service has a single responsibility:

**Persist raw events.**

---

## 4. Click Event Store

This is the **Source of Truth**.

Stores every click.

Example

```json
{
  "clickId":"123",
  "campaignId":"CMP-10",
  "userId":"USR-25",
  "timestamp":"2026-07-11T10:15:00Z",
  "country":"US",
  "device":"Mobile"
}
```

Used for

- Historical reports
- Auditing
- Replay
- Machine Learning
- Billing
- Data Warehouse

Unlike Redis,

the Click Event Store contains **raw immutable events**.

---

## 5. Apache Flink

Acts as the **Real-Time Compute Engine**.

Responsibilities

- Stateful processing
- Sliding/Tumbling/Session windows
- CTR calculations
- Fraud detection
- Real-time aggregations
- Checkpointing
- Fault recovery

Produces

```
Campaign A

↓

58231 Clicks

CTR

Revenue

Top Campaigns
```

Flink computes metrics.

It does **not** serve dashboards.

---

## 6. Redis

Acts as the **Serving Layer**.

Stores

- Live Click Counts
- Revenue
- CTR
- Trending Campaigns

Dashboard reads only Redis.

Advantages

- Extremely fast reads
- Low latency
- Offloads dashboard traffic from databases

Redis is **not** the source of truth.

---

## 7. Live Dashboard

Reads only from Redis.

Never queries Kafka.

Never queries Flink.

Never scans billions of click events.

---

# Billing

## Batch Billing (Most Common)

Billing usually does **not** need to be real-time.

Instead,

all click events are already stored in the Click Event Store.

A scheduled batch job computes invoices.

```
Click Event Store

↓

Billing Batch Job

↓

Billing DB

↓

Invoice
```

Example SQL

```sql
SELECT campaignId,
       COUNT(*) AS clicks
FROM ClickEvents
WHERE click_time BETWEEN '2026-07-01' AND '2026-07-31'
GROUP BY campaignId;
```

Advantages

- Simple
- Accurate
- Easy to audit
- Easy to verify
- No dependency on Flink

This is how many production billing systems work.

---

## Real-Time Billing (Optional)

If business requires invoices to update immediately,

Flink can write to a transactional database supporting exactly-once semantics.

```
Kafka

↓

Apache Flink

↓

Transactional Database

↓

Billing API
```

Unlike Redis,

this sink must support exactly-once guarantees.

---

# Failure Handling

## If Flink crashes

```
Restore Checkpoint

↓

Replay Kafka Events

↓

Continue Processing
```

No events are lost.

---

## What happens to Redis?

Redis may temporarily receive duplicate updates during replay because it is **not** coordinated with Flink checkpoints.

This is acceptable because

- Redis is only a cache.
- Dashboards are eventually consistent.
- Redis can always be rebuilt from the Click Event Store.

---

## If Storage Consumer crashes

Only the Storage Consumer stops.

Flink continues processing.

Kafka retains events.

After restart,

the Storage Consumer resumes from its last committed offset.

No raw events are lost.

---

## Why Separate Storage Consumer and Flink?

Instead of

```
Kafka

↓

Flink

├── Redis
└── Click Event Store
```

prefer

```
Kafka

├── Apache Flink
│      │
│      ▼
│   Redis
│
└── Storage Consumer
       │
       ▼
 Click Event Store
```

Reasons

- Single Responsibility Principle
- Independent scaling
- Storage continues even if Flink is unavailable
- Easier to maintain
- Typical event-driven architecture

---

# Why Not Store Events Through Flink?

If Flink is responsible for persistence,

then a Flink outage may delay event storage.

Keeping storage independent ensures

- Raw events are always persisted
- Replay is always possible
- Billing is unaffected by Flink failures

---

# Final Responsibilities

| Component | Responsibility |
|------------|----------------|
| Ad Redirect Service | Capture clicks and publish events |
| Kafka | Durable event bus |
| Storage Consumer | Persist raw events |
| Click Event Store | Source of truth |
| Apache Flink | Real-time analytics |
| Redis | Live dashboard cache |
| Billing Batch Job | Generate invoices |
| Billing DB | Store invoices |

---

# Interview Summary

> The Redirect Service immediately publishes click events to Kafka and returns the HTTP 302 redirect. Kafka acts as the central event bus. A dedicated Storage Consumer persists every raw click event into the Click Event Store, which serves as the source of truth for historical reporting, replay, auditing, and billing. Independently, Apache Flink consumes the same Kafka topic to perform real-time stream processing such as windowing, aggregations, CTR calculations, and fraud detection. Flink continuously updates Redis, which serves as the low-latency cache for live dashboards. Batch billing jobs compute invoices from the Click Event Store, while real-time billing—if required—would use a transactional exactly-once sink instead of Redis. This architecture cleanly separates storage, computation, and serving, making the system scalable, fault tolerant, and easy to evolve.


# Facebook (Meta) Post Search System Design

---

# 1. Problem Statement

Design Facebook's Post Search system that allows users to search billions of posts using keywords while supporting:

- Keyword search
- Hashtag search
- Search by user/page/group
- Filters (date, language, media type, visibility)
- Relevance ranking
- Permission-aware search
- Near real-time indexing
- Horizontal scalability

The system should return the most relevant posts in under 100 ms while handling millions of searches per second.

---

# 2. Functional Requirements

Users should be able to:

- Search using free-text keywords.
- Search hashtags (#travel).
- Search posts from a specific user/page/group.
- Filter results by:
  - Date
  - Language
  - Images/Videos
  - Public/Friends/Groups
- Sort by relevance.
- Sort by newest (optional).
- View only posts they are authorized to access.

---

# 3. Non-Functional Requirements

### Low Latency

Search should complete within approximately 100 milliseconds.

Reason:
Users expect search to feel instantaneous.

---

### High Throughput

The system should support millions of search requests per second.

Facebook has billions of users generating enormous search traffic.

---

### High Availability

Search should remain operational even if some servers fail.

Users should never lose the ability to search because one node crashes.

---

### Horizontal Scalability

As the number of posts grows from billions to tens of billions, we should simply add more search nodes.

---

### Near Real-Time Search

A newly created post should become searchable within a few seconds.

Immediate consistency is usually unnecessary.

---

### Permission-Aware

Users should never see posts they are not authorized to access.

This is arguably the most important requirement after correctness.

---

# 4. Estimation (Back-of-the-Envelope)

Suppose:

Daily active users:
2 Billion

Average posts/day:
500 Million

Average searchable words/post:
30

Total indexed terms/day:

500M × 30
=
15 Billion indexed terms/day

Storage:

If one posting consumes roughly 20 bytes,

15B × 20
≈ 300 GB/day

Across years, this grows into petabytes.

Conclusion:

The search index must be distributed.

---

# 5. High-Level Design

```text
                           +----------------+
                           |      User      |
                           +-------+--------+
                                   |
                                   v
                           +----------------+
                           |   Search API   |
                           +-------+--------+
                                   |
                                   v
                           +----------------+
                           | Query Service  |
                           +---+--------+---+
                               |        |
                Autocomplete   |        | Search Query
                               |        |
                               v        v
                    +----------------+  +----------------+
                    | Autocomplete   |  | Search Service |
                    +----------------+  +--------+-------+
                                                |
                                                v
                                       +------------------+
                                       | Inverted Index   |
                                       +--------+---------+
                                                |
                                                v
                                       +------------------+
                                       | Ranking Service  |
                                       +--------+---------+
                                                |
                                                v
                                       +------------------+
                                       | Permission Check |
                                       +--------+---------+
                                                |
                                                v
                                       +---------------------------+
                                       | Search Results to return  |
                                       +---------------------------+
```
Let's understand each component.

---

## Search API

Responsibilities:

- Receives search requests.
- Authenticates users.
- Applies rate limiting.
- Sends requests to Query Service.

Example:

GET /search?q=hiking alaska

---

## Query Service

This service understands what the user wants.

Responsibilities:

- Tokenization
- Query parsing
- Spell correction
- Synonym expansion
- Language detection
- Stop-word removal

Example:

User types:

"hiking in alaska"

After parsing:

["hiking", "alaska"]

---

## Search Service

This service performs the actual search.

Responsibilities:

- Looks up the inverted index.
- Retrieves candidate Post IDs.
- Sends candidates to Ranking Service.

Notice:

The Search Service does NOT directly return results.

It only retrieves matching candidates.

Ranking happens later.

---

## Ranking Service

Responsibilities:

- BM25 / TF-IDF scoring
- Freshness boost
- Like count
- Comment count
- Share count
- Friend affinity
- Author popularity
- Click history

Final Score =

BM25
+
Freshness
+
Likes
+
Comments
+
Shares
+
Friend Affinity

The highest-scoring documents are returned.

---

## Permission Filter

Even if a post matches perfectly,

the user may not be allowed to see it.

Example:

Matching posts:

P1 Public

P2 Friends

P3 Private

P4 Group Only

Suppose Alice searches.

She may receive only

P1
P2 (if friend)
P4 (if group member)

P3 is filtered out.

Permission filtering is one of the biggest challenges in Facebook Search.

---

## Why Not Search the Database?

Suppose we have

3 billion posts.

Searching

LIKE '%football%'

would require scanning billions of rows.

Even with indexes,

substring matching is extremely expensive.

Instead,

we search an inverted index,

which converts

Keyword → List of matching Post IDs.

This reduces search time from minutes to milliseconds.

---

# Key Interview Takeaways

Interviewer often asks:

"Why can't we search MySQL directly?"

Expected answer:

Because relational databases are optimized for transactional lookups using primary keys or indexed columns.

Full-text search across billions of documents is much more efficient using an inverted index maintained by a dedicated search engine.

---

# 6. Inverted Index

The core data structure behind almost every large-scale search engine is the **Inverted Index**.

Instead of storing:

```
Post
    ↓
Words
```

we store the reverse mapping:

```
Word
    ↓
Matching Posts
```

Hence the name **Inverted Index**.

Without an inverted index, searching billions of posts would require scanning every document.

With an inverted index, searching becomes almost an index lookup.

---

# Why Do We Need an Inverted Index?

Suppose Facebook stores 5 billion posts.

A user searches:

```
hiking alaska
```

If posts are stored only in a database:

```
PostID      Text

P1          I love hiking in Alaska
P2          Football is amazing
P3          Alaska is beautiful
...
```

The database would have to examine every row looking for:

```
LIKE '%hiking%'
```

and

```
LIKE '%alaska%'
```

This is essentially a table scan and is far too expensive at Facebook scale.

Instead, we preprocess every post once during indexing.

---

# Building the Index

Suppose a user creates a post:

```
"I love hiking in Alaska"
```

---

## Step 1: Tokenization

Tokenizer splits the sentence into words.

```
I
love
hiking
in
Alaska
```

---

## Step 2: Normalize

Convert to lowercase.

```
i
love
hiking
in
alaska
```

---

## Step 3: Remove Stop Words

Words like

```
the
is
in
of
an
a
```

appear in almost every sentence.

Searching them provides little value.

After removing stop words:

```
love
hiking
alaska
```

---

## Step 4: Stemming (Optional)

Reduce words to their root.

Example:

```
hiking
hiked
hikes
```

↓

```
hike
```

Similarly,

```
running

runner

runs
```

↓

```
run
```

This allows searches like

```
run
```

to match

```
running
```

without storing separate entries.

Some systems use stemming, while others use lemmatization for more accurate linguistic processing.

---

# Creating the Inverted Index

Instead of storing:

```
Post 101

love
hiking
alaska
```

we build:

| Word | Posting List |
|------|--------------|
| love | P101 |
| hiking | P101 |
| alaska | P101 |

Suppose another post arrives.

```
P205

Alaska is beautiful
```

After processing:

```
alaska
beautiful
```

Index becomes:

| Word | Posting List |
|------|--------------|
| love | P101 |
| hiking | P101 |
| alaska | P101, P205 |
| beautiful | P205 |

Every new document simply updates the posting lists.

---

# Posting List

Each keyword maps to a list of matching documents.

Example:

```
hiking

↓

P101
P350
P721
P981
```

This list is called the **Posting List**.

Searching becomes extremely fast because we only examine documents containing the searched word.

---

# What Does a Posting Actually Store?

A beginner may think it stores only:

```
hiking

↓

P101
P350
P721
```

Real search engines store much richer information.

Example:

```
hiking

↓

[
    {
        PostId: P101,
        TermFrequency: 3,
        Timestamp: 2026-07-10,
        Visibility: Public,
        Language: English,
        LikeCount: 420,
        Positions: [2, 8, 14]
    },

    {
        PostId: P350,
        TermFrequency: 1,
        Timestamp: 2026-06-20,
        Visibility: Friends,
        Language: English,
        LikeCount: 80,
        Positions: [5]
    }
]
```

Notice that every posting contains metadata that helps later during filtering and ranking.

---

# Why Store Positions?

Consider two searches:

```
new york city
```

and

```
new
```

Suppose a document is:

```
I visited New York City last year.
```

Position information is stored:

```
new      -> position 2

york     -> position 3

city     -> position 4
```

This enables **phrase search**.

Without positions, we only know the words exist.

We cannot determine whether they appear together.

---

# Why Store Term Frequency?

Suppose:

Document A

```
hiking hiking hiking alaska
```

Document B

```
hiking alaska
```

Document A is probably more relevant for "hiking".

Term Frequency (TF) allows the ranking algorithm (BM25/TF-IDF) to score A higher than B.

---

# Why Store Timestamp?

Timestamp enables:

- Recent-first sorting
- Date filtering
- Freshness boosting during ranking

Instead of fetching timestamps from the primary database, the search engine can use the timestamp already stored with the indexed document.

---

# Why Store Visibility?

Instead of retrieving every matching post and then asking another service:

```
Is this post public?
```

the search engine already knows:

```
Visibility = Public

Visibility = Friends

Visibility = Private
```

Permission filtering becomes much faster.

---

# Why Store Like Count?

Likes are useful for ranking.

A post with:

```
10,000 likes
```

may deserve a higher score than one with:

```
5 likes
```

However, like counts change frequently.

Later we'll discuss whether these should live in the index or be fetched from a separate engagement service.

---

# Write Path (Indexing Pipeline)

Whenever a new post is created:

```
User

↓

Post Service

↓

Primary Database

↓

Kafka

↓

Indexing Service

↓

Tokenizer

↓

Normalizer

↓

Inverted Index
```

Notice something important.

The user does **NOT** wait for indexing to complete.

The post is successfully written once the database commit succeeds.

Indexing happens asynchronously.

---

# Why Asynchronous Indexing?

Imagine indexing took 2 seconds.

If we waited:

```
User clicks Post

↓

Wait...

↓

Tokenizer

↓

Index Update

↓

Success
```

Posting would feel extremely slow.

Instead:

```
User clicks Post

↓

Database Commit

↓

Return Success

↓

Kafka Event

↓

Indexer updates search engine
```

The post becomes searchable a few seconds later.

This is called **Near Real-Time Indexing**.

---

# Advantages of Asynchronous Indexing

✅ Fast user writes

✅ Decouples search from database

✅ Retry failed indexing

✅ Can scale indexers independently

✅ Smooth handling of traffic spikes using Kafka buffering

---

# What Happens if the Indexer Crashes?

Kafka retains events.

When the indexer comes back:

```
Kafka

↓

Replay missed events

↓

Rebuild index
```

No posts are permanently lost from the search index.

---

# Complexity

Building the index:

O(total number of words)

Searching a keyword:

Approximately O(1) lookup to find the posting list (using hash structures/B-trees internally), followed by processing only the matching postings instead of scanning every document.

This is why search engines scale to billions of documents.

---

# Interview Takeaways

✔ The inverted index is the heart of the search engine.

✔ Documents are preprocessed once during indexing.

✔ Queries never scan the database.

✔ Posting lists contain much more than Post IDs—they also store metadata like term frequency, timestamps, visibility, and positions.

✔ Kafka enables asynchronous indexing, allowing fast writes and near real-time search.

✔ Position information enables phrase search.

✔ Term frequency enables relevance ranking.

✔ Metadata stored with postings reduces expensive database lookups during query execution.


---

# 7. Query Execution (How Search Actually Works)

Once the inverted index has been built, the next question is:

> How does a search query actually retrieve results?

Suppose the user searches:

```
hiking alaska
```

The search engine does **not** scan posts.

Instead, it performs a sequence of operations on the inverted index.

The overall pipeline looks like this:

```
Search Request
      |
Search API
      |
Query Parser
      |
Tokenizer
      |
Index Lookup
      |
Candidate Retrieval
      |
Filtering
      |
Ranking
      |
Top-K Selection
      |
Permission Filter
      |
Return Results
```

Let's examine each stage.

---

# Step 1: Query Parsing

User enters:

```
Hiking in Alaska
```

The Query Parser performs preprocessing similar to indexing.

### Tokenization

```
Hiking
in
Alaska
```

### Lowercasing

```
hiking
in
alaska
```

### Remove Stop Words

```
hiking
alaska
```

Now the search engine only searches meaningful terms.

---

# Step 2: Index Lookup

Suppose the inverted index contains:

```
hiking →

P1
P2
P5
P10
```

```
alaska →

P2
P5
P8
P20
```

Looking up each word is extremely fast because the search engine stores the dictionary efficiently (typically using hash tables, B-trees, or finite state transducers depending on the implementation).

---

# Step 3: Candidate Retrieval

Now we have two posting lists.

```
hiking

↓

P1
P2
P5
P10
```

```
alaska

↓

P2
P5
P8
P20
```

The search engine must combine them.

---

## AND Query

If the query means:

```
hiking AND alaska
```

Intersection:

```
P2
P5
```

Only documents containing both words remain.

---

## OR Query

If the query means:

```
hiking OR alaska
```

Union:

```
P1
P2
P5
P8
P10
P20
```

Now every matching document becomes a candidate.

---

## Which Does Facebook Use?

Real systems use neither a pure AND nor a pure OR.

Instead they perform **ranked retrieval**.

Example:

```
hiking alaska waterfalls
```

A post matching all three terms ranks higher than one matching only one term.

The ranking algorithm determines relevance.

---

# Step 4: Filtering

Now suppose candidate posts are:

```
P2
P5
P8
P20
```

The user selected:

```
Last 30 days
```

The search engine now filters candidates using their timestamps.

```
P2
Timestamp = July 10

Keep
```

```
P5
Timestamp = March 12

Discard
```

```
P8
Timestamp = July 5

Keep
```

Result:

```
P2
P8
```

Notice something important.

The search engine **did not search by date**.

It searched by keywords first.

Then filtered using timestamp metadata.

This distinction is very important in interviews.

---

# Interview Question

**Should timestamp be part of retrieval or filtering?**

The answer is:

Usually **filtering**.

Reason:

The inverted index retrieves documents matching keywords.

Timestamp is metadata associated with those documents.

Filtering is very efficient because it operates only on candidate documents instead of the entire corpus.

---

# Alternative Design: Separate Date Index

Instead of storing timestamps with each posting, we could maintain another index.

Example:

```
2026-07-10

↓

P2
P7
P9
```

```
2026-07-09

↓

P15
P18
```

Now perform:

```
Keyword Results

INTERSECT

Date Results
```

Advantages:

- Efficient for analytics.
- Useful for purely date-driven searches.

Disadvantages:

- More indexes to maintain.
- More intersections.
- Increased storage.
- Additional write overhead.

For general-purpose search engines like Facebook, storing timestamps with indexed documents and filtering candidates is usually simpler and faster.

---

# Retrieval by Date vs Filtering by Date

Many candidates mistakenly say:

> "Search using timestamp."

This is not how most search engines work.

Correct approach:

```
Keyword Retrieval

↓

Candidate Documents

↓

Timestamp Filter

↓

Ranking
```

This minimizes the number of documents examined.

---

# Step 5: Ranking

Suppose remaining candidates are:

```
P2
P8
```

Now the Ranking Service computes a score.

Example:

```
Score =
BM25
+ Freshness
+ Likes
+ Comments
+ Shares
+ Friend Affinity
+ Author Quality
```

Example:

```
P2

BM25 = 12
Freshness = 4
Likes = 3

Total = 19
```

```
P8

BM25 = 10
Freshness = 6
Likes = 5

Total = 21
```

Ranking:

```
P8
P2
```

---

# Freshness

Recent posts often deserve a boost.

Example:

```
Yesterday

Score +4
```

```
Two years ago

Score +0
```

This helps users see newer content without completely ignoring older highly relevant posts.

---

# Likes as a Ranking Signal

Interview Question:

> Should likes be used during retrieval?

Usually **No**.

Likes are primarily a **ranking feature**.

Suppose:

```
P2

Likes = 5000
```

```
P8

Likes = 20
```

Both documents already matched the keywords.

Likes simply influence their final ordering.

---

# Sort by Most Liked

Suppose the user explicitly selects:

```
Sort by Most Liked
```

The flow becomes:

```
Keyword Retrieval

↓

Candidate Documents

↓

Sort by LikeCount

↓

Top Results
```

Notice:

Likes still do not determine which documents match.

They determine how the matching documents are ordered.

---

# Should Like Count Be Stored in the Index?

This is a common interview follow-up.

Option 1 (Most Common)

Store lightweight metadata.

```
PostId

Timestamp

LikeCount
```

Advantages:

- No extra database lookup.
- Faster ranking.
- Simple implementation.

Disadvantage:

Like counts change frequently.

---

Option 2

Keep engagement metrics in a separate store (e.g., Redis or a key-value database).

Flow:

```
Retrieve Candidate PostIds

↓

Fetch Latest Likes

↓

Final Ranking
```

Advantages:

- Always uses fresh engagement metrics.
- Avoids frequent index updates.

Disadvantages:

- Additional network call.
- Slightly higher latency.
- More operational complexity.

---

Option 3 (Hybrid)

This is the most common production approach.

Store:

```
Approximate LikeCount
```

inside the index.

During final ranking:

```
Fetch latest engagement
```

for only the top candidate documents.

Benefits:

- Fast retrieval.
- Accurate ranking.
- Minimal additional network traffic.

---

# Why Not Update the Index Every Time Someone Likes a Post?

Imagine a viral post.

```
100,000 likes/minute
```

Updating the inverted index for every like would generate enormous write amplification.

Instead:

- Periodically refresh indexed values.
- Use a separate engagement service for real-time counts.
- Or combine both (hybrid approach).

---

# Step 6: Top-K Retrieval

Interview Question:

"What if 'football' matches 80 million posts?"

Do we retrieve all 80 million?

No.

Each shard computes only its local Top-K.

Example:

```
Shard 1

Top 100
```

```
Shard 2

Top 100
```

```
Shard 3

Top 100
```

Coordinator merges:

```
100
+
100
+
100
```

instead of millions.

Finally returns:

```
Top 20
```

This dramatically reduces memory, CPU, and network usage.

---

# Why Top-K Matters

Without Top-K:

```
80 million posts

↓

Send across network

↓

Sort

↓

Return 20
```

Impossible at scale.

With Top-K:

```
Each shard

↓

Top 100

↓

Coordinator Merge

↓

Top 20
```

Efficient and scalable.

---

# Step 7: Permission Filtering

Now suppose Top-K results are:

```
P2 Public

P8 Friends

P10 Private
```

Alice is friends with the author of P8.

Returned:

```
P2
P8
```

Discard:

```
P10
```

Some search engines integrate permission checks during retrieval to avoid scoring inaccessible documents, while others apply permission filtering after candidate retrieval but before final results.

The exact strategy depends on the system's access control model and performance requirements.

---

# Complete Query Flow

```
User Query
      |
Query Parsing
      |
Index Lookup
      |
Candidate Retrieval
      |
Date / Language Filters
      |
Ranking
      |
Top-K Selection
      |
Permission Filter
      |
Return Results
```

---

# Interview Summary

**Retrieval**
- Uses the inverted index.
- Finds documents matching the query terms.

**Filtering**
- Applies constraints like date, language, media type, and visibility.
- Operates on candidate documents.

**Ranking**
- Computes a relevance score using BM25, freshness, likes, comments, shares, and personalization.

**Top-K**
- Each shard returns only its best candidates.
- The coordinator merges local Top-K lists into the global Top results.

**Key Insight**

A search engine does **not** retrieve documents using likes or timestamps.

It retrieves documents using **keywords**.

Metadata such as timestamps and engagement metrics are then used for **filtering** and **ranking**, which is what makes the final results both relevant and personalized.

---

# 8. Distributed Search (Scaling the Inverted Index)

So far, we've assumed the inverted index exists on a single machine.

That works for a few million documents.

Facebook, however, stores **billions of posts**, resulting in petabytes of index data.

A single machine cannot:

- Store the entire index
- Handle millions of searches per second
- Process all indexing requests
- Survive failures

Therefore, the search index must be distributed across many machines.

---

# High-Level Distributed Architecture

                        User
                          |
                    Search API
                          |
                  Search Coordinator
                          |
        +---------+--------+---------+
        |         |        |         |
     Shard 1   Shard 2   Shard 3   Shard 4
        |         |        |         |
    Local Top-K Local Top-K Local Top-K Local Top-K
        \         |        |         /
         \--------+--------+--------/
                  Merge Results
                       |
                Return Top Results

Every shard contains **only part of the index**.

---

# What is a Shard?

A shard is simply a **partition** of the search index.

Instead of storing:

```
Entire Index
```

on one machine,

we split it into multiple smaller indexes.

Example:

```
Shard 1
```

contains

```
Posts 1 - 1 Billion
```

```
Shard 2
```

contains

```
Posts 1B - 2B
```

etc.

Each shard can be searched independently.

---

# Two Common Sharding Strategies

There are two major approaches.

## Option 1: Shard by Term

Example:

```
A-F

Server 1

------------------

G-L

Server 2

------------------

M-R

Server 3

------------------

S-Z

Server 4
```

Searching:

```
hiking alaska
```

requires:

```
alaska

↓

Server 1
```

```
hiking

↓

Server 2
```

Then the coordinator combines the posting lists.

---

## Advantages

Very small posting lists.

Good compression.

---

## Disadvantages

One query may need to contact many shards.

A query with five words could hit five different servers.

Common terms become hotspots.

Example:

```
the

facebook

love
```

These shards receive much more traffic.

Rebalancing is difficult.

---

# Option 2: Shard by Document ID (Most Common)

Instead of splitting terms,

split documents.

Example:

```
Shard 1

Posts

1 - 1 Billion
```

```
Shard 2

Posts

1B - 2B
```

Each shard contains a **complete inverted index for only its own documents**.

Example:

Shard 1

```
hiking →

P10

P22

P99
```

Shard 2

```
hiking →

P1.2B

P1.8B
```

Every shard can answer the complete query independently.

---

# Why Document-Based Sharding Wins

Suppose the query is:

```
hiking alaska waterfalls
```

Coordinator sends the **entire query** to every shard.

```
Search Coordinator

↓

Shard 1

↓

Top 100
```

```
↓

Shard 2

↓

Top 100
```

```
↓

Shard 3

↓

Top 100
```

Coordinator merges only:

```
100

+

100

+

100
```

instead of millions.

This is exactly how Elasticsearch and OpenSearch distribute searches.

---

# Why Not Shard by User?

Someone might suggest:

```
User A

↓

Shard 1
```

```
User B

↓

Shard 2
```

Problem:

Searching

```
football
```

would require knowing which users wrote football posts.

The search engine wants to search documents,

not users.

Therefore document-based sharding is preferred.

---

# Query Fan-Out

Suppose we have

8 shards.

The coordinator receives:

```
travel alaska
```

Instead of searching itself,

it broadcasts the query.

```
Coordinator

↓

Shard 1

↓

Top 100
```

```
↓

Shard 2

↓

Top 100
```

...

```
↓

Shard 8

↓

Top 100
```

Every shard searches locally.

This is called **Query Fan-Out**.

---

# Local Ranking

Notice something important.

Each shard performs:

```
Retrieval

↓

Filtering

↓

Ranking

↓

Top-K
```

The coordinator never ranks millions of documents.

It only merges already-ranked local results.

---

# Global Merge

Suppose

Shard 1 returns

```
P1

Score = 92
```

```
P2

Score = 88
```

Shard 2 returns

```
P500

Score = 95
```

```
P700

Score = 81
```

Coordinator merges:

```
95

92

88

81
```

Returns:

```
P500

P1

P2

P700
```

Only a few hundred documents need sorting.

---

# Why Top-K Merge Is Efficient

Without Top-K:

```
Shard 1

10 million posts
```

```
Shard 2

12 million posts
```

```
Shard 3

8 million posts
```

Network traffic:

```
30 million documents
```

Impossible.

Instead:

Each shard returns only

```
Top 100
```

Coordinator merges

```
300 documents
```

Huge performance improvement.

---

# Replication

What if

Shard 3 crashes?

We would lose part of the search index.

Instead every shard has replicas.

Example:

```
Primary

↓

Replica 1

↓

Replica 2
```

If the primary fails,

traffic automatically switches to a replica.

Benefits:

- High availability
- Fault tolerance
- Read scaling

---

# Read Scaling

Replicas are not only for failures.

Suppose:

```
Shard 1

Primary
```

receives

50,000 queries/sec.

Instead,

load balancer distributes requests:

```
Primary

Replica 1

Replica 2
```

Each handles part of the traffic.

---

# Index Updates

When a new post arrives:

```
Indexer

↓

Primary Shard

↓

Replica 1

↓

Replica 2
```

Replicas eventually receive the same updates.

Search systems generally tolerate slight replication lag.

---

# Hot Shards

One major production challenge is **hot shards**.

Example:

Trending searches:

```
World Cup

Taylor Swift

Election

Olympics
```

Certain shards may receive significantly more traffic.

This creates uneven CPU and memory utilization.

Possible solutions:

- More replicas
- Query caching
- Better shard balancing
- Adaptive routing
- Splitting overloaded shards

---

# Query Cache

Many users search the same topics.

Example:

```
Taylor Swift

NBA Finals

World Cup
```

Instead of repeating the same search,

cache:

```
Query

↓

Top Results
```

Common implementation:

```
Redis
```

or built-in search engine caches.

Benefits:

- Lower latency
- Reduced CPU
- Lower disk access

---

# Document Cache

Instead of caching only queries,

we can cache frequently accessed documents.

Example:

```
Post ID

↓

Rendered Post
```

Avoids repeated database fetches after ranking.

---

# Adding More Shards

Suppose we originally have:

```
4 shards
```

The index grows beyond capacity.

Can we simply create

```
8 shards
```

Not easily.

Existing documents must be redistributed.

This process is called **Rebalancing**.

Rebalancing is expensive because:

- Large data movement
- Network utilization
- Temporary performance degradation

Many search clusters add capacity gradually and rebalance in the background.

---

# Consistent Hashing?

Interview Question:

Should we use Consistent Hashing?

Usually **No**.

Consistent hashing is excellent for:

- Distributed caches
- Key-value stores

Search engines typically use **fixed shard allocation** because:

- Queries must fan out to all shards.
- Entire index partitions need careful balancing.
- Large-scale data movement is already managed by shard allocation strategies.

---

# Failure Handling

Suppose:

```
Shard 5

↓

Down
```

Coordinator can:

1. Query a replica.
2. Return partial results (if acceptable).
3. Retry another replica.
4. Fail the request (rare).

Facebook generally prefers degraded search over complete outage.

---

# Complete Distributed Search Flow

User Query

↓

Coordinator

↓

Broadcast to all shards

↓

Each shard:

- Retrieve
- Filter
- Rank
- Compute Top-K

↓

Coordinator merges Top-K

↓

Permission Filter (if not already applied)

↓

Return Results

---

# Interview Trade-Offs

| Design Choice | Advantages | Disadvantages |
|---------------|------------|---------------|
| **Shard by Term** | Smaller posting lists, efficient compression | Complex queries touch many shards, hot terms create hotspots, difficult rebalancing |
| **Shard by Document ID** | Simple query fan-out, independent shards, easy Top-K merging | Every query must reach every shard |
| **Replicas** | High availability, increased read throughput | More storage, replication overhead |
| **Query Cache** | Very low latency for popular searches | Cache invalidation and memory usage |
| **Top-K Merge** | Minimal network traffic and coordinator work | Requires good local ranking quality |

---

# Key Interview Takeaways

✔ Large search indexes are partitioned into **shards**.

✔ Most production search engines (Elasticsearch/OpenSearch) shard by **document**, not by term.

✔ A coordinator broadcasts the query to all shards (**query fan-out**).

✔ Each shard performs **retrieval → filtering → ranking → local Top-K** independently.

✔ The coordinator merges only the **Top-K** results from each shard.

✔ Replicas provide **fault tolerance** and **read scalability**.

✔ Query caching dramatically improves performance for popular searches.

✔ Shard rebalancing and hot shards are important operational challenges in large-scale search systems.

---

# 9. Ranking (How Search Decides Which Results Come First)

After retrieving candidate posts, the search engine must decide:

> **Which matching posts should appear first?**

This is called **Ranking**.

Suppose a user searches:

```
hiking alaska
```

Assume the search engine retrieves these candidate posts:

```
P1
P2
P3
P4
P5
```

All five contain the keywords.

However, they are **not equally relevant**.

The ranking service computes a score for every candidate and sorts them.

---

# Ranking Pipeline

```
Candidate Documents
        |
Retrieve Metadata
        |
Compute Scores
        |
Sort by Score
        |
Return Top-K
```

Notice that **ranking happens only after retrieval**.

The search engine is **not ranking billions of posts**.

It is ranking only the candidate documents returned by the retrieval phase.

---

# What Makes a Post Relevant?

A modern search engine combines many signals.

```
Final Score =
Text Relevance
+ Freshness
+ Engagement
+ Personalization
+ Quality Signals
```

Each signal contributes to the final ranking.

---

# Signal 1: Text Relevance (BM25)

The most important signal is:

**How well does the document match the query?**

Modern search engines usually use **BM25**.

Earlier search engines used **TF-IDF**.

---

# What is BM25?

BM25 is a ranking algorithm that estimates how relevant a document is to a query.

It considers:

- Frequency of the keyword
- Rarity of the keyword
- Document length

Example:

Query:

```
hiking alaska
```

Document A

```
I love hiking in Alaska.
```

Document B

```
Alaska has mountains.

...

(hiking appears once in 5000 words)
```

Document A should rank higher because it focuses on the topic.

BM25 automatically rewards that.

---

# Why Not Just Count Keyword Matches?

Suppose two posts:

```
P1

hiking hiking hiking hiking hiking
```

```
P2

I recently went hiking in Alaska and loved every trail.
```

Simple keyword counting might rank P1 higher.

BM25 recognizes that excessive repetition should not dominate the ranking.

This prevents keyword stuffing.

---

# BM25 vs TF-IDF

Earlier search engines used TF-IDF.

```
TF

Term Frequency
```

```
IDF

Inverse Document Frequency
```

TF-IDF works well but has limitations.

BM25 improves upon it by:

- Penalizing excessively long documents
- Preventing repeated words from dominating
- Producing better relevance scores

Today, BM25 is the default ranking algorithm in Elasticsearch and OpenSearch.

---

# Signal 2: Freshness

Facebook contains a lot of time-sensitive content.

Suppose someone searches:

```
Olympics
```

Should the first result be:

```
Olympics 2012
```

Probably not.

Recent posts are often more useful.

Therefore:

```
Yesterday

+5 points
```

```
Last week

+3 points
```

```
Five years ago

+0 points
```

Freshness is usually implemented as a **boost**, not as a replacement for relevance.

---

# Why Freshness is a Boost

Consider two posts.

Post A

```
Very relevant

Yesterday
```

Post B

```
Very relevant

Five years ago
```

Freshness helps Post A rank slightly higher.

However, an irrelevant new post should not beat a highly relevant older one.

This is why freshness is just one signal among many.

---

# Signal 3: Engagement

Popular posts often provide useful content.

Signals include:

- Likes
- Comments
- Shares
- Saves
- Reactions

Example:

```
Post A

Likes = 20
```

```
Post B

Likes = 50,000
```

All else being equal, Post B probably deserves a higher ranking.

---

# Should Likes Dominate Ranking?

No.

Imagine:

```
Old Meme

2 million likes
```

versus

```
Breaking News

200 likes
```

If likes were the only signal, users would never discover new content.

Instead, likes are combined with freshness and relevance.

---

# Signal 4: Personalization

Facebook is highly personalized.

Suppose Alice searches:

```
birthday
```

She is more interested in:

- Friends' birthday posts
- Family posts
- People she interacts with

than random public posts.

Therefore the search engine boosts documents based on:

- Friend relationship
- Follow relationship
- Group membership
- Page follows
- Interaction history

---

# Friend Affinity

Suppose Alice interacts with Bob every day.

She rarely interacts with Charlie.

Both write posts containing:

```
hiking alaska
```

Bob's post should likely appear first.

This is called **Affinity Ranking**.

The search engine estimates:

```
How likely is this user to care about this author's content?
```

---

# Signal 5: Click History

If many users search:

```
iphone review
```

and consistently click one post,

that post is probably useful.

Historical click-through rate (CTR) becomes another ranking signal.

This is similar to web search engines.

---

# Signal 6: Author Quality

Not all authors have equal credibility.

Signals may include:

- Trusted page
- Verified account
- Low spam score
- Consistent quality history

Higher-quality authors may receive a modest ranking boost.

---

# Signal 7: Spam Detection

Some posts try to manipulate ranking.

Example:

```
FREE FREE FREE FREE FREE
```

or

```
Click here!!!!

Win Money!!!!

FREE!!!!
```

Spam detection models can reduce the ranking score or remove such posts entirely.

---

# Combining Signals

A simplified scoring formula might look like:

```
Final Score =

0.45 × BM25
+0.20 × Freshness
+0.15 × Engagement
+0.10 × Affinity
+0.05 × Click History
+0.05 × Author Quality
```

The exact weights are learned and tuned over time.

Production systems often use machine learning models instead of fixed formulas.

---

# Example Ranking

Suppose we have three candidate posts.

### Post A

```
Excellent keyword match

Old

High likes
```

Score

```
BM25 = 10
Freshness = 1
Likes = 5

Total = 16
```

---

### Post B

```
Good keyword match

Very recent

Moderate likes
```

```
BM25 = 9
Freshness = 4
Likes = 3

Total = 16
```

---

### Post C

```
Perfect keyword match

Recent

High engagement

Friend posted it
```

```
BM25 = 10
Freshness = 4
Likes = 5
Affinity = 4

Total = 23
```

Ranking becomes:

```
P3
P1
P2
```

---

# Multi-Stage Ranking

Facebook does **not** apply expensive ML models to every candidate.

Instead, ranking happens in stages.

### Stage 1: Retrieval

```
80 Million Posts

↓

50,000 Candidates
```

---

### Stage 2: Lightweight Ranking

Use inexpensive signals.

```
BM25

Freshness

Basic Engagement
```

↓

Top 500

---

### Stage 3: Heavy ML Ranking

Now apply expensive features.

Examples:

- Friend affinity
- Click prediction
- Deep learning models
- User interests
- Graph features

↓

Top 20

This saves enormous CPU time.

---

# Why Multi-Stage Ranking?

Imagine running a deep neural network on:

```
80 million posts
```

for every search.

Impossible.

Instead:

```
Retrieve

↓

Filter

↓

Cheap Ranking

↓

Top 500

↓

ML Ranking

↓

Top 20
```

Only a tiny number of documents require expensive computation.

---

# Most Relevant vs Most Recent

Interviewers often ask:

> **Should users always see the newest posts first?**

Not necessarily.

Example:

Search:

```
Java Spring Boot
```

Newest post:

```
"Learning Spring today!"
```

Older post:

```
Comprehensive Spring Boot production guide with thousands of likes.
```

If sorted only by time, the older but much more useful post disappears.

Therefore many systems default to **Most Relevant**, while still offering a **Sort by Newest** option.

---

# How "Sort by Newest" Works

If the user explicitly chooses:

```
Sort by Newest
```

The ranking stage changes.

Instead of:

```
BM25
+ Freshness
+ Likes
```

the search engine simply sorts the filtered candidate documents by:

```
Timestamp DESC
```

Notice:

The retrieval stage **does not change**.

Only the final ranking changes.

---

# Machine Learning Ranking

Large search systems increasingly replace manually tuned formulas with ML models.

The model may consider hundreds of features, including:

- Query terms
- Text relevance
- Likes
- Comments
- Shares
- Friend affinity
- Group membership
- Previous clicks
- User interests
- Language
- Device type
- Geographic location

The model predicts:

> **How likely is the user to engage with this post?**

Posts with higher predicted engagement receive higher rankings.

---

# Interview Summary

Ranking answers the question:

> **Among all matching documents, which ones are most useful?**

A production ranking system typically combines:

- **BM25** for textual relevance
- **Freshness** for recent content
- **Engagement** (likes, comments, shares)
- **Personalization** (friend affinity, interaction history)
- **Quality signals** (author trust, spam score)
- **Machine learning models** for final ordering

Most large-scale systems use **multi-stage ranking**:

1. Retrieve candidates
2. Apply lightweight scoring
3. Keep only the top few hundred
4. Run expensive ML ranking
5. Return the final Top-K results

This provides high-quality search results while keeping latency low.

---

# 10. Index Maintenance & Near Real-Time Search

The inverted index is **not static**.

Facebook users continuously:

- Create posts
- Edit posts
- Delete posts
- Change visibility
- Add hashtags
- Receive likes/comments/shares

The search index must remain synchronized with the primary database.

The database remains the **Source of Truth**, while the search index is a **derived view** optimized for fast search.

---

# Why Not Search the Database Directly?

The database is optimized for:

- Transactions
- ACID guarantees
- Primary key lookups

The search engine is optimized for:

- Full-text search
- Ranking
- Phrase search
- Boolean queries

Instead of querying the database directly, we continuously build and maintain a search index.

---

# Overall Indexing Pipeline

```
             User
               |
         Create Post
               |
          Post Service
               |
      Primary Database
               |
      Commit Successful
               |
        Publish Event
             Kafka
               |
        Indexing Service
               |
   Tokenize / Normalize
               |
      Update Search Index
```

Notice an important point.

The user receives success immediately after the database commit.

The search index is updated asynchronously.

---

# Why Asynchronous Indexing?

Imagine updating the search index took:

```
2 seconds
```

If the user had to wait:

```
Click Post

↓

Update Database

↓

Update Search Index

↓

Success
```

Posting would feel very slow.

Instead:

```
Click Post

↓

Database Commit

↓

Return Success

↓

Kafka

↓

Indexer

↓

Search Engine Updated
```

The post becomes searchable a few seconds later.

This is called:

**Near Real-Time Search**

---

# Near Real-Time (NRT)

Interview Question:

> Why isn't search immediately consistent?

Because:

Updating a distributed search index is expensive.

Instead,

systems typically allow:

```
2–10 seconds
```

of indexing delay.

This is acceptable because users generally don't expect a post to appear in search instantly.

---

# Create Post Flow

Suppose Alice creates:

```
"I love hiking in Alaska."
```

### Step 1

Database stores:

```
PostId = P500

Text = ...

Timestamp = ...
```

---

### Step 2

Publish Kafka Event

```
PostCreated

{
    PostId: P500
}
```

---

### Step 3

Indexer consumes the event.

---

### Step 4

Indexer retrieves the document.

```
Post Service

↓

Full Post
```

---

### Step 5

Tokenizer

```
love

hiking

alaska
```

---

### Step 6

Update Posting Lists

```
love

↓

P500
```

```
hiking

↓

P500
```

```
alaska

↓

P500
```

The document is now searchable.

---

# Why Send Only the PostId?

Interview Question:

Should Kafka contain the full post?

Option 1

Publish:

```
Entire Post
```

Advantages

- Faster indexing
- No additional lookup

Disadvantages

- Large Kafka messages
- Duplicate data
- Higher network usage

---

Option 2 (Common)

Publish only:

```
PostId
```

Indexer fetches the latest version.

Advantages

- Smaller events
- Latest data
- Easier retries

Disadvantages

- One additional service call

Most production systems prefer this approach.

---

# Editing a Post

Suppose Alice edits:

Original

```
I love hiking.
```

Updated

```
I love hiking in Alaska.
```

The index must change.

Flow:

```
Database Updated

↓

Kafka

↓

Indexer

↓

Remove Old Terms

↓

Add New Terms
```

Old index:

```
hiking

↓

P500
```

New index:

```
hiking

↓

P500
```

```
alaska

↓

P500
```

---

# Delete Post

Suppose Alice deletes:

```
P500
```

Should we immediately rebuild the index?

No.

Instead,

publish:

```
PostDeleted

↓

P500
```

Indexer removes the posting references.

```
hiking

↓

P500 ❌
```

---

# Soft Delete vs Hard Delete

Many systems perform:

Soft Delete

```
Deleted = true
```

Search ignores deleted posts.

Physical cleanup occurs later.

Advantages:

- Easy recovery
- Better auditing
- Faster delete operations

---

# Visibility Changes

Suppose:

Public

↓

Friends Only

The document itself hasn't changed.

Only permissions changed.

Indexer updates:

```
Visibility Metadata
```

instead of rebuilding the entire document.

---

# Likes Keep Changing

Suppose:

```
500 likes
```

↓

```
501 likes
```

Should the search index update?

Probably not.

Imagine:

```
100,000 likes/minute
```

Updating the index every time would create enormous write traffic.

---

# Option 1

Periodic refresh.

Every few minutes:

```
Latest Like Count

↓

Update Index
```

---

# Option 2

Store likes elsewhere.

```
Search Index

↓

Post IDs

↓

Engagement Service

↓

Latest Likes
```

---

# Option 3 (Most Common)

Hybrid.

Index stores:

```
Approximate Likes
```

Ranking service fetches:

```
Latest Likes
```

only for the top candidates.

---

# What Happens if Kafka Goes Down?

Good interview question.

If Kafka is unavailable,

the Post Service should **not** lose the event.

Common approaches:

### Transactional Outbox Pattern

Instead of:

```
Database Commit

↓

Kafka
```

Write both the post and an "outbox" event in the same database transaction.

```
Database

↓

Posts Table

Outbox Table
```

A background process publishes outbox events to Kafka.

Benefits:

- No lost events
- Atomicity between database and messaging

---

# What if the Indexer Crashes?

Kafka retains messages.

When the indexer comes back:

```
Kafka

↓

Replay Events

↓

Update Index
```

No indexing events are lost.

---

# What if the Search Index Becomes Corrupted?

Because the database is the source of truth,

the index can be rebuilt.

```
Database

↓

Scan All Posts

↓

Tokenizer

↓

Rebuild Entire Index
```

This process is expensive but reliable.

Many production systems periodically validate index consistency.

---

# Index Refresh

One subtle concept.

The indexer may write documents immediately,

but searchers don't necessarily see them instantly.

Search engines periodically perform a:

```
Refresh
```

which makes newly indexed documents searchable.

Example timeline:

```
10:00:00

Post Created
```

```
10:00:01

Indexed
```

```
10:00:03

Index Refreshed
```

```
10:00:03

Searchable
```

This is why search is called:

**Near Real-Time**

instead of

**Real-Time**

---

# Event Types

Typical Kafka events:

```
PostCreated
```

```
PostUpdated
```

```
PostDeleted
```

```
VisibilityChanged
```

Each event updates the search index appropriately.

---

# Keeping Database and Index Consistent

Remember:

The database is authoritative.

The search index is eventually consistent.

If inconsistencies occur,

the index can always be rebuilt from the database.

This separation makes the system more resilient.

---

# Interview Trade-Offs

| Design Choice | Advantages | Disadvantages |
|---------------|------------|---------------|
| **Synchronous Indexing** | Immediately searchable | Slow writes, higher user latency |
| **Asynchronous Indexing** | Fast writes, scalable | Small indexing delay |
| **Publish Full Post** | Faster indexing | Larger Kafka messages |
| **Publish PostId Only** | Smaller events, latest data | Extra lookup |
| **Immediate Like Updates** | Accurate ranking | Extremely high write amplification |
| **Periodic Like Refresh** | Efficient | Slightly stale counts |
| **Hybrid Engagement Fetch** | Fast + accurate | More system complexity |
| **Outbox Pattern** | No lost events | Additional table and background publisher |

---

# Key Interview Takeaways

✔ The **database is the source of truth**.

✔ The **search index is a derived, eventually consistent view** optimized for retrieval.

✔ Updates flow asynchronously through **Kafka**, enabling fast user writes.

✔ New posts become searchable within a few seconds (**Near Real-Time Search**).

✔ Likes and other frequently changing metrics are **not usually re-indexed on every update**; they are refreshed periodically or fetched from a separate engagement service.

✔ If the indexer fails, **Kafka replay** ensures events are not lost.

✔ If the index is corrupted, it can always be **rebuilt from the database**.

---

# 11. Advanced Search Features

So far, we've designed a scalable full-text search engine.

Modern search systems also provide:

- Autocomplete
- Hashtag search
- Phrase search
- Typo tolerance
- Synonym search
- Emoji search
- Language-aware search

Let's examine each feature.

---

# 1. Autocomplete

Suppose the user types:

```
tr
```

Even before pressing Enter,

Facebook suggests:

```
travel

tree

trip

trek
```

This is called **Autocomplete**.

Notice:

Autocomplete does **not** search posts.

It searches possible query terms.

---

# Why Not Use the Inverted Index?

Suppose our inverted index contains:

```
travel

↓

millions of posts
```

To answer:

```
tr
```

we would need to scan every word beginning with "tr".

This is inefficient.

Instead,

autocomplete maintains a separate data structure.

---

# Trie (Prefix Tree)

The most common solution is a Trie.

Example words:

```
travel

tree

trip

truck
```

Trie:

```
          root
         /   \
        t
        |
        r
      / | \
     a  e  i
     |  |  |
   travel tree trip
```

Searching:

```
tr
```

takes time proportional to the length of the prefix.

---

# Complexity

Searching:

```
travel
```

Time complexity:

```
O(length of prefix)
```

Instead of:

```
O(number of words)
```

This makes autocomplete extremely fast.

---

# Ranking Autocomplete Suggestions

Suppose:

```
travel
```

```
tree
```

```
trip
```

All match:

```
tr
```

Which should appear first?

Rank using:

- Search frequency
- Trending topics
- User history
- Geographic location
- Language

Example:

Alice searches travel frequently.

Suggestions become:

```
travel

trip

tree
```

Another user may see a different order.

---

# Updating the Trie

When new words become popular:

```
AI Agent
```

```
ChatGPT
```

```
World Cup
```

The autocomplete index is updated asynchronously,

similar to the search index.

---

# 2. Hashtag Search

Example:

```
#travel
```

Should hashtags be stored separately?

There are two approaches.

---

## Option 1

Treat hashtags as normal tokens.

Example post:

```
I love #travel
```

Tokenizer produces:

```
love

travel

#travel
```

Index:

```
travel

↓

P100
```

```
#travel

↓

P100
```

Simple and effective.

---

## Option 2

Maintain a dedicated hashtag index.

```
#travel

↓

P100

P200

P500
```

Advantages:

- Trending calculations
- Hashtag analytics
- Recommendations

Disadvantages:

- Additional storage
- Extra maintenance

Many production systems do both.

---

# Trending Hashtags

Interview Question:

How would you show:

```
Trending Today
```

Maintain counters.

Example:

```
#travel

1,200,000
```

```
#olympics

8,500,000
```

A streaming system continuously updates counts.

The top hashtags become trending.

---

# 3. Phrase Search

Suppose the user searches:

```
"New York City"
```

This is different from:

```
new

york

city
```

appearing separately.

---

# Why Posting Positions Matter

During indexing,

store word positions.

Example document:

```
I visited New York City.
```

Store:

```
new

↓

Position 2
```

```
york

↓

Position 3
```

```
city

↓

Position 4
```

Now verify:

```
2

↓

3

↓

4
```

The phrase exists.

Without positions,

phrase search would not be possible.

---

# 4. Typo Tolerance

Suppose the user searches:

```
hikng
```

instead of:

```
hiking
```

Should search fail?

No.

Users make mistakes.

---

# Spell Correction

Possible approaches:

### Edit Distance (Levenshtein Distance)

Example:

```
hikng

↓

hiking
```

Distance = 1

The search engine suggests:

```
Did you mean:

hiking?
```

---

# N-Grams

Break words into smaller pieces.

Example:

```
hiking
```

Trigrams:

```
hik

iki

kin

ing
```

Now:

```
hikng
```

shares many trigrams.

This enables fuzzy matching.

---

# Which One is Better?

Levenshtein:

- Accurate
- Expensive

N-Grams:

- Faster
- Scalable

Production systems often combine both.

---

# 5. Synonym Search

Suppose the user searches:

```
car
```

Relevant documents may contain:

```
automobile
```

```
vehicle
```

Instead of searching only:

```
car
```

Expand the query.

```
car

↓

car

automobile

vehicle
```

This increases recall.

---

# Advantages

Users find more relevant documents.

---

# Disadvantages

Too many synonyms may introduce irrelevant results.

Example:

```
apple
```

Could mean:

- Fruit
- Company

Context becomes important.

---

# 6. Language Detection

Facebook stores posts in many languages.

Suppose:

```
Bonjour
```

Tokenizer should apply French language rules,

not English.

Each indexed document stores:

```
Language = French
```

Queries can be restricted to:

```
English

Spanish

Hindi

etc.
```

---

# 7. Emoji Search

Suppose users search:

```
🏕️
```

Tokenizer converts emojis into searchable tokens.

Example:

```
🏕️

↓

camping
```

Similarly,

```
❤️

↓

love
```

This enables emoji-aware search.

---

# 8. Query Suggestions

Different from autocomplete.

Autocomplete:

```
tra

↓

travel
```

Query Suggestions:

After searching:

```
travel
```

suggest:

```
travel alaska

travel europe

travel tips
```

Suggestions are generated using:

- Search logs
- Trending queries
- User behavior

---

# 9. Search Analytics

Search logs are valuable.

Track:

- Most searched queries
- No-result searches
- Average latency
- Click-through rate
- Abandoned searches

Benefits:

- Improve ranking
- Detect missing content
- Optimize autocomplete
- Discover trending topics

---

# 10. Search History

Users often repeat searches.

Example:

Alice searches:

```
spring boot

kafka

system design
```

Frequently.

Search box can suggest these first.

History is user-specific.

---

# Advanced Query Processing Pipeline

```
User Query
      |
Language Detection
      |
Spell Correction
      |
Synonym Expansion
      |
Tokenizer
      |
Search Index
      |
Ranking
      |
Suggestions
```

Notice:

Many preprocessing steps happen **before** querying the index.

---

# Interview Trade-Offs

| Feature | Common Implementation | Trade-Off |
|----------|-----------------------|----------|
| **Autocomplete** | Trie / Prefix Index | Extra memory, extremely fast prefix lookup |
| **Hashtags** | Treat as tokens + optional hashtag index | Additional index maintenance for analytics |
| **Phrase Search** | Store word positions | Larger index size |
| **Spell Correction** | Levenshtein + N-Grams | Accuracy vs performance |
| **Synonyms** | Query expansion | Better recall but potential loss of precision |
| **Language Detection** | Language-specific analyzers | Additional indexing complexity |
| **Emoji Search** | Normalize emojis into tokens | Requires custom mappings |
| **Search History** | User profile service | Personalization vs storage/privacy considerations |

---

# Key Interview Takeaways

✔ Autocomplete is **not** built from the inverted index; it typically uses a **Trie** or specialized prefix index.

✔ Hashtags are often indexed as **regular tokens**, with an optional dedicated hashtag index for trending and analytics.

✔ Phrase search requires storing **word positions** in the posting list.

✔ Typo tolerance is commonly implemented using **Levenshtein distance**, **N-Grams**, or a combination.

✔ Synonym expansion improves recall by broadening the query before retrieval.

✔ Language-specific tokenization and normalization improve relevance across multilingual content.

✔ Advanced search features are layered **on top of** the core retrieval pipeline rather than replacing it.

---

# 12. End-to-End Flow, Bottlenecks & Interview Discussion

In this chapter, we'll walk through:

1. Creating a new post
2. Searching for a post
3. How ranking works
4. How failures are handled
5. Performance bottlenecks
6. Common interview follow-up questions

---

# End-to-End Flow: Creating a New Post

Suppose Alice creates a post:

```
"I love hiking in Alaska."
```

The complete write flow is:

```
                    User
                      |
                 Post Service
                      |
              Primary Database
                      |
                 Commit Success
                      |
                 Return Success
                      |
             Transactional Outbox
                      |
              Outbox Publisher
                      |
                    Kafka
                      |
              Indexing Service
                      |
      Tokenizer / Normalizer / Stemmer
                      |
             Update Inverted Index
                      |
            Search Index Refreshed
                      |
           Post Becomes Searchable
```

Notice:

The user does **not** wait for indexing.

The search index is updated asynchronously.

---

# End-to-End Flow: Search Request

Now Bob searches:

```
hiking alaska
```

The request flows through:

```
                    User
                      |
                 Search API
                      |
               Query Parser
                      |
      Spell Correction (optional)
                      |
        Synonym Expansion (optional)
                      |
                 Tokenizer
                      |
             Search Coordinator
                      |
      +---------+---------+---------+
      |         |         |         |
   Shard 1   Shard 2   Shard 3   Shard 4
      |         |         |         |
 Retrieve   Retrieve   Retrieve   Retrieve
      |         |         |         |
 Filter     Filter     Filter     Filter
      |         |         |         |
 Rank       Rank       Rank       Rank
      |         |         |         |
 Top-100   Top-100   Top-100   Top-100
      +---------+---------+---------+
                      |
              Merge Top-K Results
                      |
             Permission Filtering
                      |
             Fetch Latest Engagement
                      |
               Final ML Ranking
                      |
                Return Top Results
```

Every component has a specific responsibility.

---

# What Happens Inside Each Shard?

Suppose Shard 2 receives:

```
hiking alaska
```

The local flow is:

```
Query
   |
Lookup "hiking"
   |
Lookup "alaska"
   |
Intersect Posting Lists
   |
Apply Date Filter
   |
Apply Language Filter
   |
Compute BM25
   |
Apply Freshness Boost
   |
Return Local Top-100
```

Each shard performs the same work independently.

---

# Where Does Ranking Happen?

Many people assume ranking happens only once.

Actually there are multiple ranking stages.

```
Retrieve Candidates
        |
Cheap Ranking
(BM25 + Freshness)
        |
Top 500
        |
Fetch Additional Features
        |
ML Ranking
        |
Top 20
```

This keeps latency low.

---

# Why Not Run ML on Every Candidate?

Suppose:

```
Query:

football
```

Matches:

```
80 million posts
```

Running a neural network on:

```
80 million posts
```

would be impossible.

Instead:

```
80M

↓

50K Candidates

↓

Top 500

↓

ML Ranking

↓

Top 20
```

Only a few hundred documents require expensive computation.

---

# Example Walkthrough

Suppose Bob searches:

```
spring boot
```

Candidate documents:

```
P1
```

```
Spring Boot Guide
```

```
100 Likes
```

```
Yesterday
```

---

```
P2
```

```
Spring Boot Tutorial
```

```
50,000 Likes
```

```
5 Years Old
```

---

```
P3
```

```
Spring Boot at Morgan Stanley
```

```
200 Likes
```

```
Today
```

Bob frequently reads Morgan Stanley engineering content.

Ranking signals:

| Post | BM25 | Freshness | Likes | Affinity | Total |
|------|------|-----------|-------|----------|-------|
| P1 | 10 | 4 | 2 | 1 | 17 |
| P2 | 9 | 0 | 5 | 0 | 14 |
| P3 | 10 | 5 | 3 | 4 | 22 |

Final ranking:

```
P3

↓

P1

↓

P2
```

Notice:

The most-liked post is **not** necessarily ranked first.

---

# Common Bottlenecks

Large search systems encounter several bottlenecks.

---

## 1. Hot Queries

Popular searches:

```
Taylor Swift
```

```
World Cup
```

```
Election
```

may generate millions of requests.

Solutions:

- Query cache
- More replicas
- Load balancing
- CDN (for static assets)
- Adaptive routing

---

## 2. Large Posting Lists

Suppose:

```
love
```

matches:

```
500 million posts
```

Retrieving every posting is wasteful.

Solutions:

- Skip lists
- Block-max indexes
- Early termination
- Dynamic pruning
- Top-K retrieval

These techniques avoid scanning every matching document.

---

## 3. Slow Ranking

Ranking is CPU intensive.

Solutions:

- Multi-stage ranking
- Feature caching
- ML inference optimization
- Only rank top candidates

---

## 4. Frequent Index Updates

Facebook receives millions of new posts every hour.

Updating the search index continuously creates heavy write traffic.

Solutions:

- Kafka buffering
- Multiple indexing workers
- Batch indexing
- Near real-time refresh

---

## 5. Hot Shards

Some shards receive much more traffic.

Example:

If one shard contains many trending posts,

it becomes overloaded.

Solutions:

- More replicas
- Better shard balancing
- Split oversized shards
- Cache popular queries

---

## 6. Permission Checks

Permission filtering can become expensive.

Every candidate may require checking:

- Public
- Friends
- Groups
- Block lists
- Privacy settings

Solutions:

- Store visibility metadata in the index
- Cache ACLs (Access Control Lists)
- Push filtering down to each shard
- Precompute common permissions where possible

---

## 7. Stale Engagement Metrics

Like counts change continuously.

Using only indexed values may produce stale rankings.

Solutions:

- Periodic index refresh
- Separate engagement service
- Hybrid ranking

---

# Failure Scenarios

---

## Search Node Failure

```
Shard 3

↓

Down
```

Coordinator routes the query to a replica.

Users usually notice no interruption.

---

## Kafka Failure

Use:

```
Transactional Outbox
```

Events are retried until delivered.

No posts are lost.

---

## Indexer Failure

Kafka retains events.

After restart:

```
Replay Events

↓

Update Index
```

---

## Search Index Corruption

Rebuild from the database.

```
Database

↓

Indexer

↓

New Search Index
```

The database remains the source of truth.

---

# Monitoring

A production search system continuously tracks:

### Search Latency

```
P50

P95

P99
```

---

### Indexing Delay

How long before a post becomes searchable?

Target:

```
2–10 seconds
```

---

### Query Throughput

Queries per second.

Helps determine when more search nodes are required.

---

### Cache Hit Rate

Higher cache hit rate means:

- Lower latency
- Less CPU
- Fewer disk reads

---

### Failed Queries

Monitor:

- Timeouts
- Exceptions
- Empty results
- Slow shards

---

# Security Considerations

Never expose:

- Private posts
- Deleted posts
- Blocked users' content
- Hidden group content

Permission filtering must be enforced consistently.

Even cached results must respect user permissions.

---

# Scaling Summary

As Facebook grows:

```
More Users

↓

More Posts

↓

More Shards

↓

More Replicas

↓

More Indexers

↓

More Kafka Partitions

↓

More Coordinators
```

Every component can scale horizontally.

---

# Final Architecture

```
                          User
                            |
                      Search API
                            |
                     Query Service
                            |
                 Search Coordinator
                            |
        +----------+----------+----------+
        |          |          |          |
     Shard 1    Shard 2    Shard 3    Shard 4
        |          |          |          |
 Retrieval   Retrieval   Retrieval   Retrieval
        |          |          |          |
 Filtering  Filtering  Filtering  Filtering
        |          |          |          |
 Ranking    Ranking    Ranking    Ranking
        |          |          |          |
 Top-100    Top-100    Top-100    Top-100
        +----------+----------+----------+
                            |
                     Merge Results
                            |
                  Permission Filter
                            |
                  ML Ranking (optional)
                            |
                     Return Results
```

---

# Interview Recap

A senior-level explanation should emphasize the separation of responsibilities:

### Retrieval

- Uses the inverted index.
- Finds documents matching the query terms.

### Filtering

- Applies date, language, media type, and permission constraints.
- Removes documents that should not be considered.

### Ranking

- Orders the remaining candidates using relevance, freshness, engagement, personalization, and quality signals.

### Distribution

- Each shard performs retrieval, filtering, and local ranking independently.
- The coordinator merges only the Top-K results from each shard.

### Reliability

- Database is the source of truth.
- Kafka enables asynchronous indexing.
- Replicas provide high availability.
- Transactional Outbox prevents lost indexing events.
- The index can always be rebuilt from the database.

---

# Common Interview Questions

### Q1. Why not search MySQL directly?

Because relational databases are optimized for transactional lookups, not full-text search over billions of documents.

---

### Q2. Why asynchronous indexing?

To keep write latency low while allowing the search index to update in the background.

---

### Q3. Why are likes not used for retrieval?

Retrieval is keyword-based using the inverted index.

Likes are ranking signals applied after candidate retrieval.

---

### Q4. Why store timestamps in the index?

To efficiently support filtering, freshness boosting, and sorting without querying the primary database.

---

### Q5. Why does every shard return only Top-K?

To minimize network traffic and coordinator work while still producing globally ranked results.

---

### Q6. Why is the database the source of truth?

Because the search index is a derived view optimized for search. If the index becomes inconsistent or corrupted, it can always be rebuilt from the authoritative database.

---

# Final Takeaway

A Facebook-scale post search system is much more than an inverted index.

It combines:

- Distributed indexing
- Near real-time event processing
- Sharded search clusters
- Multi-stage ranking
- Permission-aware filtering
- Personalization
- Fault-tolerant distributed systems

The key design principle is to separate the problem into distinct stages:

**Retrieve → Filter → Rank → Merge → Return**

This separation keeps the system scalable, maintainable, and capable of serving billions of documents with low latency.


---

# 12. End-to-End Flow, Bottlenecks & Interview Discussion

In this chapter, we'll walk through:

1. Creating a new post
2. Searching for a post
3. How ranking works
4. How failures are handled
5. Performance bottlenecks
6. Common interview follow-up questions

---

# End-to-End Flow: Creating a New Post

Suppose Alice creates a post:

```
"I love hiking in Alaska."
```

The complete write flow is:

```
                    User
                      |
                 Post Service
                      |
              Primary Database
                      |
                 Commit Success
                      |
                 Return Success
                      |
             Transactional Outbox
                      |
              Outbox Publisher
                      |
                    Kafka
                      |
              Indexing Service
                      |
      Tokenizer / Normalizer / Stemmer
                      |
             Update Inverted Index
                      |
            Search Index Refreshed
                      |
           Post Becomes Searchable
```

Notice:

The user does **not** wait for indexing.

The search index is updated asynchronously.

---

# End-to-End Flow: Search Request

Now Bob searches:

```
hiking alaska
```

The request flows through:

```
                    User
                      |
                 Search API
                      |
               Query Parser
                      |
      Spell Correction (optional)
                      |
        Synonym Expansion (optional)
                      |
                 Tokenizer
                      |
             Search Coordinator
                      |
      +---------+---------+---------+
      |         |         |         |
   Shard 1   Shard 2   Shard 3   Shard 4
      |         |         |         |
 Retrieve   Retrieve   Retrieve   Retrieve
      |         |         |         |
 Filter     Filter     Filter     Filter
      |         |         |         |
 Rank       Rank       Rank       Rank
      |         |         |         |
 Top-100   Top-100   Top-100   Top-100
      +---------+---------+---------+
                      |
              Merge Top-K Results
                      |
             Permission Filtering
                      |
             Fetch Latest Engagement
                      |
               Final ML Ranking
                      |
                Return Top Results
```

Every component has a specific responsibility.

---

# What Happens Inside Each Shard?

Suppose Shard 2 receives:

```
hiking alaska
```

The local flow is:

```
Query
   |
Lookup "hiking"
   |
Lookup "alaska"
   |
Intersect Posting Lists
   |
Apply Date Filter
   |
Apply Language Filter
   |
Compute BM25
   |
Apply Freshness Boost
   |
Return Local Top-100
```

Each shard performs the same work independently.

---

# Where Does Ranking Happen?

Many people assume ranking happens only once.

Actually there are multiple ranking stages.

```
Retrieve Candidates
        |
Cheap Ranking
(BM25 + Freshness)
        |
Top 500
        |
Fetch Additional Features
        |
ML Ranking
        |
Top 20
```

This keeps latency low.

---

# Why Not Run ML on Every Candidate?

Suppose:

```
Query:

football
```

Matches:

```
80 million posts
```

Running a neural network on:

```
80 million posts
```

would be impossible.

Instead:

```
80M

↓

50K Candidates

↓

Top 500

↓

ML Ranking

↓

Top 20
```

Only a few hundred documents require expensive computation.

---

# Example Walkthrough

Suppose Bob searches:

```
spring boot
```

Candidate documents:

```
P1
```

```
Spring Boot Guide
```

```
100 Likes
```

```
Yesterday
```

---

```
P2
```

```
Spring Boot Tutorial
```

```
50,000 Likes
```

```
5 Years Old
```

---

```
P3
```

```
Spring Boot at Morgan Stanley
```

```
200 Likes
```

```
Today
```

Bob frequently reads Morgan Stanley engineering content.

Ranking signals:

| Post | BM25 | Freshness | Likes | Affinity | Total |
|------|------|-----------|-------|----------|-------|
| P1 | 10 | 4 | 2 | 1 | 17 |
| P2 | 9 | 0 | 5 | 0 | 14 |
| P3 | 10 | 5 | 3 | 4 | 22 |

Final ranking:

```
P3

↓

P1

↓

P2
```

Notice:

The most-liked post is **not** necessarily ranked first.

---

# Common Bottlenecks

Large search systems encounter several bottlenecks.

---

## 1. Hot Queries

Popular searches:

```
Taylor Swift
```

```
World Cup
```

```
Election
```

may generate millions of requests.

Solutions:

- Query cache
- More replicas
- Load balancing
- CDN (for static assets)
- Adaptive routing

---

## 2. Large Posting Lists

Suppose:

```
love
```

matches:

```
500 million posts
```

Retrieving every posting is wasteful.

Solutions:

- Skip lists
- Block-max indexes
- Early termination
- Dynamic pruning
- Top-K retrieval

These techniques avoid scanning every matching document.

---

## 3. Slow Ranking

Ranking is CPU intensive.

Solutions:

- Multi-stage ranking
- Feature caching
- ML inference optimization
- Only rank top candidates

---

## 4. Frequent Index Updates

Facebook receives millions of new posts every hour.

Updating the search index continuously creates heavy write traffic.

Solutions:

- Kafka buffering
- Multiple indexing workers
- Batch indexing
- Near real-time refresh

---

## 5. Hot Shards

Some shards receive much more traffic.

Example:

If one shard contains many trending posts,

it becomes overloaded.

Solutions:

- More replicas
- Better shard balancing
- Split oversized shards
- Cache popular queries

---

## 6. Permission Checks

Permission filtering can become expensive.

Every candidate may require checking:

- Public
- Friends
- Groups
- Block lists
- Privacy settings

Solutions:

- Store visibility metadata in the index
- Cache ACLs (Access Control Lists)
- Push filtering down to each shard
- Precompute common permissions where possible

---

## 7. Stale Engagement Metrics

Like counts change continuously.

Using only indexed values may produce stale rankings.

Solutions:

- Periodic index refresh
- Separate engagement service
- Hybrid ranking

---

# Failure Scenarios

---

## Search Node Failure

```
Shard 3

↓

Down
```

Coordinator routes the query to a replica.

Users usually notice no interruption.

---

## Kafka Failure

Use:

```
Transactional Outbox
```

Events are retried until delivered.

No posts are lost.

---

## Indexer Failure

Kafka retains events.

After restart:

```
Replay Events

↓

Update Index
```

---

## Search Index Corruption

Rebuild from the database.

```
Database

↓

Indexer

↓

New Search Index
```

The database remains the source of truth.

---

# Monitoring

A production search system continuously tracks:

### Search Latency

```
P50

P95

P99
```

---

### Indexing Delay

How long before a post becomes searchable?

Target:

```
2–10 seconds
```

---

### Query Throughput

Queries per second.

Helps determine when more search nodes are required.

---

### Cache Hit Rate

Higher cache hit rate means:

- Lower latency
- Less CPU
- Fewer disk reads

---

### Failed Queries

Monitor:

- Timeouts
- Exceptions
- Empty results
- Slow shards

---

# Security Considerations

Never expose:

- Private posts
- Deleted posts
- Blocked users' content
- Hidden group content

Permission filtering must be enforced consistently.

Even cached results must respect user permissions.

---

# Scaling Summary

As Facebook grows:

```
More Users

↓

More Posts

↓

More Shards

↓

More Replicas

↓

More Indexers

↓

More Kafka Partitions

↓

More Coordinators
```

Every component can scale horizontally.

---

# Final Architecture

```
                          User
                            |
                      Search API
                            |
                     Query Service
                            |
                 Search Coordinator
                            |
        +----------+----------+----------+
        |          |          |          |
     Shard 1    Shard 2    Shard 3    Shard 4
        |          |          |          |
 Retrieval   Retrieval   Retrieval   Retrieval
        |          |          |          |
 Filtering  Filtering  Filtering  Filtering
        |          |          |          |
 Ranking    Ranking    Ranking    Ranking
        |          |          |          |
 Top-100    Top-100    Top-100    Top-100
        +----------+----------+----------+
                            |
                     Merge Results
                            |
                  Permission Filter
                            |
                  ML Ranking (optional)
                            |
                     Return Results
```

---

# Interview Recap

A senior-level explanation should emphasize the separation of responsibilities:

### Retrieval

- Uses the inverted index.
- Finds documents matching the query terms.

### Filtering

- Applies date, language, media type, and permission constraints.
- Removes documents that should not be considered.

### Ranking

- Orders the remaining candidates using relevance, freshness, engagement, personalization, and quality signals.

### Distribution

- Each shard performs retrieval, filtering, and local ranking independently.
- The coordinator merges only the Top-K results from each shard.

### Reliability

- Database is the source of truth.
- Kafka enables asynchronous indexing.
- Replicas provide high availability.
- Transactional Outbox prevents lost indexing events.
- The index can always be rebuilt from the database.

---

# Common Interview Questions

### Q1. Why not search MySQL directly?

Because relational databases are optimized for transactional lookups, not full-text search over billions of documents.

---

### Q2. Why asynchronous indexing?

To keep write latency low while allowing the search index to update in the background.

---

### Q3. Why are likes not used for retrieval?

Retrieval is keyword-based using the inverted index.

Likes are ranking signals applied after candidate retrieval.

---

### Q4. Why store timestamps in the index?

To efficiently support filtering, freshness boosting, and sorting without querying the primary database.

---

### Q5. Why does every shard return only Top-K?

To minimize network traffic and coordinator work while still producing globally ranked results.

---

### Q6. Why is the database the source of truth?

Because the search index is a derived view optimized for search. If the index becomes inconsistent or corrupted, it can always be rebuilt from the authoritative database.

---

# Final Takeaway

A Facebook-scale post search system is much more than an inverted index.

It combines:

- Distributed indexing
- Near real-time event processing
- Sharded search clusters
- Multi-stage ranking
- Permission-aware filtering
- Personalization
- Fault-tolerant distributed systems

The key design principle is to separate the problem into distinct stages:

**Retrieve → Filter → Rank → Merge → Return**

This separation keeps the system scalable, maintainable, and capable of serving billions of documents with low latency.

# Chat System - WebSockets Explained

---

# One Common Misconception

Many people think Alice and Bob share one WebSocket connection.

```
Alice  <=======>  Bob
```

❌ Incorrect.

There is **never** a direct WebSocket between two users.

Every client establishes its own WebSocket connection to the server.

---

# Correct Architecture

```
Alice Phone
      │
      │ WebSocket Connection
      ▼
WebSocket Server
      ▲
      │ WebSocket Connection
      │
Bob Phone
```

Actually, Bob is connected using **his own independent WebSocket connection**.

A more realistic view is:

```
Alice Phone
      │
      ▼
 WebSocket Connection A
      │
      ▼
 WebSocket Server
      ▲
      │
 WebSocket Connection B
      │
      ▼
 Bob Phone
```

Alice and Bob are **not connected to each other**.

Both are connected to the server.

---

# WebSocket vs WebSocket Server

This terminology is important.

## WebSocket

A WebSocket is a **single persistent connection** between one client and one server.

Example

```
Alice Phone

↓

WebSocket Connection

↓

WebSocket Server
```

One client.

One connection.

---

## WebSocket Server

A WebSocket Server manages thousands (or millions) of WebSocket connections.

Example

```
                 WebSocket Server

        Alice Connection

        Bob Connection

        Charlie Connection

        David Connection

        ...
```

Think of it as

```java
ConcurrentHashMap<UserId, WebSocketSession> connections;
```

Every connected user has a WebSocketSession stored in memory.

---

# Production Architecture

Large systems run multiple WebSocket servers.

```
                 Load Balancer

             /       |       \

          WS1       WS2      WS3
```

Each WebSocket Server manages thousands of users.

Example

```
WS1

Alice
Charlie
Mike
Sarah
```

```
WS2

Bob
Tom
Lisa
```

```
WS3

David
Emma
```

---

# Why Multiple WebSocket Servers?

A single server cannot handle millions of users.

Instead,

connections are distributed across many servers.

Each server manages only the users connected to it.

---

# Where Is Connection Information Stored?

Each WebSocket Server keeps active connections in memory.

Example

```java
ConcurrentHashMap<UserId, WebSocketSession> connections;
```

Example

```
connections

Alice -> SessionA
Charlie -> SessionB
Mike -> SessionC
```

This lookup is extremely fast.

---

# How Does The System Find Bob?

Suppose Alice sends

```
Hi Bob
```

Alice is connected to

```
WS1
```

Bob is connected to

```
WS2
```

The Chat Service must determine

```
Where is Bob?
```

---

# Redis Mapping

Redis stores

```
Alice -> WS1

Bob -> WS2

Charlie -> WS1
```

Notice

Redis stores the **WebSocket Server**, not the socket object itself.

The actual WebSocketSession lives only inside that server's memory.

---

# Message Flow

```
Alice Phone

↓

Alice's WebSocket Connection

↓

WS1

↓

Chat Service

↓

Redis Lookup

↓

Bob -> WS2

↓

WS2

↓

connections.get("Bob")

↓

Bob's WebSocket Connection

↓

Bob Phone
```

---

# Why Doesn't Redis Store Socket IDs?

Because socket objects exist only in the memory of the WebSocket Server.

Example

```
WS2

connections

Bob -> WebSocketSession
```

Redis simply tells us

```
Bob

↓

WS2
```

Then WS2 performs

```java
connections.get("Bob")
```

and sends the message over Bob's existing WebSocket connection.

---

# Multiple Devices

Suppose Bob uses

- iPhone
- Laptop
- iPad

Each device creates its own WebSocket connection.

```
Bob iPhone

↓

WebSocket Connection 1

↓

WS2


Bob Laptop

↓

WebSocket Connection 2

↓

WS3


Bob iPad

↓

WebSocket Connection 3

↓

WS5
```

Redis now stores

```
Bob

↓

WS2 (iPhone)

WS3 (Laptop)

WS5 (iPad)
```

or conceptually

```java
userId -> List<Connection>
```

When Alice sends a message,

the Chat Service forwards it to all WebSocket servers that currently have one of Bob's active connections.

---

# Responsibilities

## WebSocket Server

Responsible for

- Accepting WebSocket connections
- Keeping connections alive
- Managing active sessions in memory
- Sending messages over existing connections
- Detecting disconnects

It is essentially a **Connection Manager**.

---

## Chat Service

Responsible for business logic

- Validate sender
- Store message
- Generate Message ID
- Publish Kafka events
- Determine recipients
- Read receipts
- Delivery status
- Group messaging

The Chat Service does **not** manage socket connections.

---

# Final Architecture

```
                    Load Balancer
                           │
        ┌──────────────────┼──────────────────┐
        ▼                  ▼                  ▼
   WebSocket S1      WebSocket S2      WebSocket S3
        │                  │                  │
        │                  │                  │
 Alice Connection     Bob Connection    Charlie Connection
        │                  │
        └──────────────┬───┘
                       ▼
                  Chat Service
                       │
                  Redis Mapping

             Alice -> WS1
             Bob   -> WS2
```

---

# Key Takeaways

- Every client/device has its own WebSocket connection.
- There is never a direct WebSocket between Alice and Bob.
- A WebSocket Server manages thousands of WebSocket connections.
- Active WebSocket sessions are stored in the WebSocket Server's memory.
- Redis stores which WebSocket Server a user is connected to.
- The Chat Service uses Redis to locate the correct WebSocket Server and forwards the message there.
- The WebSocket Server retrieves the in-memory WebSocketSession and pushes the message to the client.
- WebSocket Servers are responsible only for managing connections, while the Chat Service contains the messaging business logic.

---

# Interview Summary

> Every client establishes its own persistent WebSocket connection to a WebSocket Server. A WebSocket Server manages thousands of active connections in memory and acts as a connection manager. Redis maintains a lightweight mapping of `userId -> WebSocketServer`, allowing the Chat Service to quickly determine where a recipient is connected. When Alice sends a message, it reaches the Chat Service through her WebSocket Server. The Chat Service looks up Bob's WebSocket Server in Redis, forwards the message to that server, and the server uses its in-memory WebSocket session to push the message to Bob. This architecture cleanly separates connection management from chat business logic and scales horizontally by adding more WebSocket servers.		  


# Fan-out on Write vs Fan-out on Read (Group Chat)

---

# The Problem

Suppose Alice sends a message to a group.

```
Family Group

Members

- Alice
- Bob
- Charlie
- David
```

Alice sends

> Happy New Year

Question:

How should we deliver this message to the group members?

There are two common approaches:

1. Fan-out on Write
2. Fan-out on Read

---

# Common Database Tables

These tables exist in **both** designs.

---

## Groups

Stores basic group information.

| GroupId | GroupName | CreatedBy |
|---------|-----------|-----------|
|100|Family|Alice|
|200|Office|Bob|

---

## GroupMembers

Stores which users belong to which groups.

| GroupId | UserId |
|----------|--------|
|100|Alice|
|100|Bob|
|100|Charlie|
|100|David|

When Alice sends a message,

the system first determines the members.

```sql
SELECT UserId
FROM GroupMembers
WHERE GroupId = 100;
```

Returns

```
Alice
Bob
Charlie
David
```

The difference between Fan-out on Write and Fan-out on Read begins **after this query**.

---

# Fan-out on Write

The system proactively creates delivery records for every recipient.

---

## Step 1

Insert one row into Messages.

### Messages

| MessageId | GroupId | Sender | Message |
|-----------|----------|--------|----------|
|101|100|Alice|Happy New Year|

---

## Step 2

Read GroupMembers

```
Alice
Bob
Charlie
David
```

---

## Step 3

Create recipient records.

### MessageRecipient

| MessageId | Recipient | Status |
|-----------|-----------|--------|
|101|Bob|DELIVERED|
|101|Charlie|UNDELIVERED|
|101|David|DELIVERED|

Notice:

Alice is the sender, so we typically don't create a recipient record for her.

---

Suppose Charlie later receives the message.

### MessageRecipient

| MessageId | Recipient | Status |
|-----------|-----------|--------|
|101|Bob|DELIVERED|
|101|Charlie|DELIVERED|
|101|David|DELIVERED|

---

Charlie opens the chat.

### MessageRecipient

| MessageId | Recipient | Status |
|-----------|-----------|--------|
|101|Bob|DELIVERED|
|101|Charlie|READ|
|101|David|DELIVERED|

Every recipient maintains an independent status.

---

# Fan-out on Write Flow

```
Message

↓

Read GroupMembers

↓

Bob

Charlie

David

↓

Create Recipient Row

Create Recipient Row

Create Recipient Row
```

Every message creates

```
(Number of Group Members - 1)
```

new database rows.

---

# Fan-out on Read

Instead of creating recipient rows,

store the message only once.

---

## Step 1

Insert into Messages.

### Messages

| MessageId | GroupId | Sender | Message |
|-----------|----------|--------|----------|
|101|100|Alice|Happy New Year|
|102|100|Bob|Meeting at 5 PM|
|103|100|Charlie|See you tomorrow|

Done.

No MessageRecipient table.

---

## UserGroupState

Instead of storing every pending message,

store only each user's progress.

| User | Group | LastReadMessageId |
|------|--------|------------------|
|Alice|100|103|
|Bob|100|102|
|Charlie|100|101|
|David|100|103|

Interpretation

Alice has read

```
101
102
103
```

Bob has read

```
101
102
```

Charlie has read

```
101
```

David has read everything.

---

## Charlie Opens The Group

Chat Service executes

```sql
SELECT *
FROM Messages
WHERE GroupId = 100
AND MessageId > 101
ORDER BY MessageId;
```

Returns

| MessageId | Sender | Message |
|-----------|--------|----------|
|102|Bob|Meeting at 5 PM|
|103|Charlie|See you tomorrow|

Immediately after delivery,

update

### UserGroupState

| User | Group | LastReadMessageId |
|------|--------|------------------|
|Charlie|100|103|

Done.

No recipient rows were ever created.

---

# Fan-out on Read Flow

```
Message

↓

Store Once

↓

User Opens Group

↓

Fetch Messages

WHERE MessageId > LastReadMessageId

↓

Update LastReadMessageId
```

---

# Storage Comparison

Suppose

```
1000 members

100 messages
```

---

## Fan-out on Write

Messages table

```
100 rows
```

Recipient table

```
100 × 999

=

99,900 rows
```

Total

```
≈100,000 rows
```

---

## Fan-out on Read

Messages table

```
100 rows
```

UserGroupState

```
1000 rows
```

Total

```
1100 rows
```

Huge reduction in storage.

---

# Why Fan-out on Read Scales Better

Instead of storing

```
Bob needs Message101

Bob needs Message102

Bob needs Message103
```

we simply store

```
Bob has already read Message100.
```

Everything newer is automatically unread.

This changes storage complexity from

```
O(Messages × Users)
```

to approximately

```
O(Messages + Users)
```

which is significantly smaller for very large groups.

---

# Visual Comparison

## Fan-out on Write

```
Groups

↓

GroupMembers

↓

Messages

↓

MessageRecipient

↓

Bob

Charlie

David
```

Each new message generates recipient records.

---

## Fan-out on Read

```
Groups

↓

GroupMembers

↓

Messages

+

UserGroupState
```

Messages are stored once.

Only each user's progress is tracked.

---

# Which Systems Use Which?

## Fan-out on Write

Best for

- WhatsApp family groups
- Slack channels
- Microsoft Teams
- Small and medium groups

Advantages

- Fast reads
- Instant delivery
- Easy to track per-recipient status

Disadvantages

- High storage cost
- Expensive writes

---

## Fan-out on Read

Best for

- Large communities
- Broadcast channels
- Twitter/X timelines
- Large social platforms

Advantages

- Cheap writes
- Much lower storage
- Better scalability

Disadvantages

- More expensive reads
- Client fetches messages when opening the group

---

# Comparison Table

| Feature | Fan-out on Write | Fan-out on Read |
|---------|------------------|-----------------|
| Message stored | Once | Once |
| Recipient records | Yes | No |
| User progress | Optional | Required (`UserGroupState`) |
| Write Cost | High | Low |
| Read Cost | Low | Higher |
| Storage | O(Messages × Users) | O(Messages + Users) |
| Best For | Small/Medium Groups | Very Large Groups |

---

# Interview Summary

> Both approaches maintain the same `Groups` and `GroupMembers` tables. The difference is what happens after the group members are identified. In **Fan-out on Write**, the system stores the message once and immediately creates a `MessageRecipient` record for every recipient to track delivery and read status. This provides fast reads but results in high write amplification and storage costs. In **Fan-out on Read**, the system stores the message only once and does not create recipient records. Instead, it maintains a lightweight `UserGroupState` table (for example, `LastReadMessageId` or `LastSeenTimestamp`) for each user. When a user opens the group, unread messages are fetched by querying for messages newer than the user's last read position. This dramatically reduces storage requirements and is the preferred approach for very large groups or broadcast channels.

# WhatsApp System Design - Interview Flow (10-15 Minutes)

---

# How to Think

Don't explain every component individually.

Instead, explain the journey of **one message** from sender to receiver.

Imagine Alice sends

> "Hi Bob"

The interviewer should naturally discover every component as you walk through the flow.

---

# Step 1 - High-Level Architecture (1 minute)

Start with a simple diagram.

```
                Clients
                   │
           WebSocket Connection
                   │
            Load Balancer
                   │
        WebSocket Servers
                   │
             Chat Services
        ┌──────────┼──────────┐
        ▼          ▼          ▼
     Message DB   Redis     Kafka
```

Then say:

> Clients establish persistent WebSocket connections through a load balancer. WebSocket servers manage long-lived client connections, while stateless Chat Services contain the messaging business logic. Messages are persisted in the Message Database, Redis stores temporary connection and presence information, and Kafka publishes events for asynchronous processing.

Stop.

Do NOT explain every box.

---

# Step 2 - Alice Sends a Message (2 minutes)

Now tell the story.

```
Alice

↓

WebSocket Server

↓

Chat Service
```

Chat Service

1. Authenticates Alice.
2. Generates a Snowflake Message ID.
3. Stores the message in the Message Database.
4. Marks status as SENT.
5. Publishes a MessageCreated event to Kafka.

Explain:

> The database is the source of truth. Every message is persisted before delivery.

---

# Step 3 - Deliver To Bob (2 minutes)

Chat Service now asks

```
Where is Bob?
```

Redis contains

```
Bob → WS2
```

Chat Service forwards the message.

```
Chat Service

↓

WS2

↓

Bob
```

Bob immediately sends back

```
DELIVERED
```

Chat Service updates

```
Message Status = DELIVERED
```

and notifies Alice.

Later,

Bob opens the chat.

```
READ
```

Database becomes

```
READ
```

Alice now sees

```
Blue ✓✓
```

---

# Step 4 - Offline Users (1 minute)

Suppose Bob isn't connected.

Redis says

```
Bob Offline
```

No problem.

The message is already safely stored in the database.

Kafka triggers the Notification Service.

Notification Service sends

- APNS
- FCM

Bob opens the app.

A WebSocket reconnects.

Chat Service fetches

```
UNDELIVERED
```

messages from the database.

---

# Step 5 - Media (1 minute)

Large files are never stored inside the database.

Instead

```
Upload

↓

Object Storage

↓

Store URL

↓

Database
```

Recipients download directly from a CDN backed by object storage.

---

# Step 6 - Group Chat (2 minutes)

Small groups

```
Store Message

↓

Kafka

↓

Fan-out Service

↓

Deliver To Members
```

Large groups

```
Store Once

↓

User Opens Group

↓

Fetch Messages

WHERE MessageId > LastReadMessageId
```

Explain the tradeoff.

Fan-out on Write

- Faster reads
- Expensive writes

Fan-out on Read

- Cheap writes
- Better for huge communities

---

# Step 7 - Scalability (2 minutes)

Explain why the system scales.

WebSocket Servers

- Stateful
- Maintain active connections

Chat Services

- Stateless
- Can scale horizontally

Redis

- Online users
- Presence
- Routing

Kafka

- Decouples notifications, analytics and search

Message DB

- Durable source of truth

Object Storage

- Large media

---

# If Time Permits

Mention

- Typing indicators → WebSocket only
- Presence → Redis + Heartbeats
- Ordering → Kafka partition by ConversationId
- Snowflake IDs
- Push notifications

These are enhancements, not the core flow.

---

# Final 2-Minute Summary

> Clients establish persistent WebSocket connections to WebSocket Servers, which act as connection managers. When Alice sends a message, the Chat Service authenticates the request, generates a Snowflake ID, persists the message in the Message Database, and publishes a MessageCreated event to Kafka. The Chat Service uses Redis to determine which WebSocket Server currently owns Bob's connection and forwards the message there for real-time delivery. If Bob is offline, the message remains safely stored in the database while a Notification Service sends a push notification through APNS or FCM. Media files are stored separately in object storage with only metadata in the database. For group messaging, small groups use fan-out on write for low latency, while very large groups use fan-out on read to reduce storage and write amplification. The architecture scales by separating stateful WebSocket Servers from stateless Chat Services, with Redis providing fast routing and Kafka enabling asynchronous event processing.

---

# Interview Tip

Don't try to explain every box.

Tell the story of **one message**.

Everything else naturally appears:

- WebSocket → connection
- Chat Service → business logic
- DB → persistence
- Redis → routing
- Kafka → asynchronous processing
- Notifications → offline users
- Object Storage → media
- Fan-out → groups

Interviewers remember a coherent flow much better than a checklist of technologies.

# WhatsApp System Design

# Chapter 1 - Requirements, Scale Estimation & APIs

---

# Problem Statement

Design a messaging platform similar to WhatsApp that supports:

- One-to-one messaging
- Group messaging
- Media sharing
- Online/offline users
- Read receipts
- Push notifications
- Low latency communication
- Massive scalability

---

# Functional Requirements

## Must Have

- User authentication
- One-to-one chat
- Group chat
- Real-time messaging
- Offline message delivery
- Read receipts
- Delivery receipts
- Message history
- Media sharing
- Push notifications

---

## Nice To Have

- Typing indicators
- Last Seen
- Online Presence
- Message Search
- Reactions
- Voice Notes
- Video Calls

---

# Non Functional Requirements

## Availability

The system should remain available even if some servers fail.

Target

```
99.99%
```

---

## Scalability

Should support

- Hundreds of millions of users
- Millions of concurrent WebSocket connections
- Millions of messages per second

---

## Low Latency

Users expect messages to appear almost instantly.

Target

```
<100 ms
```

for online users.

---

## Durability

Messages must never be lost after the sender receives acknowledgement.

---

## Ordering

Messages inside the same conversation should appear in order.

---

## Reliability

Offline users should receive messages after reconnecting.

---

# Capacity Estimation (Interview Level)

Assume

```
500 Million Daily Active Users
```

Average

```
40 messages/day/user
```

Total

```
20 Billion messages/day
```

Messages per second

```
20,000,000,000

/

86,400

≈231,000 messages/sec
```

Peak traffic may be

```
2-5×

Average
```

Design for

```
~1 Million messages/sec
```

---

# APIs

## Send Message

```
POST /messages
```

Request

```json
{
  "senderId":"Alice",
  "receiverId":"Bob",
  "text":"Hello"
}
```

Response

```json
{
  "messageId":"123456",
  "status":"SENT"
}
```

---

## Get Conversation

```
GET /conversations/{conversationId}/messages
```

Optional

```
?after=messageId
```

or

```
?limit=50
```

---

## Create Group

```
POST /groups
```

Request

```json
{
   "name":"Family",
   "members":[
      "Alice",
      "Bob",
      "Charlie"
   ]
}
```

---

## Send Group Message

```
POST /groups/{groupId}/messages
```

---

## Upload Media

```
POST /media
```

Returns

```
Media URL
```

which is later stored in the Messages table.

---

## Mark Delivered

```
POST /messages/{messageId}/delivered
```

---

## Mark Read

```
POST /messages/{messageId}/read
```

---

## WebSocket Events

Instead of REST APIs, some operations use the existing WebSocket connection.

Examples

```
SEND_MESSAGE

DELIVERED

READ

TYPING_START

TYPING_STOP
```

---

# APIs vs WebSockets

REST APIs are used for

- Login
- Fetch History
- Create Group
- Upload Media

WebSockets are used for

- Real-time Messages
- Read Receipts
- Delivery Receipts
- Typing Indicators
- Presence Updates

---

# Design Decisions

| Requirement | Design Decision |
|-------------|-----------------|
| Real-time messaging | WebSockets |
| Offline delivery | Message Database |
| Low latency routing | Redis |
| Asynchronous processing | Kafka |
| Large media | Object Storage |
| Global uniqueness | Snowflake IDs |

---

# What Comes Next

Now that we understand the requirements, APIs, and expected scale, the next step is designing the high-level architecture that satisfies these requirements.

The following chapter introduces the major building blocks:

- WebSocket Servers
- Chat Service
- Message Database
- Redis
- Kafka
- Object Storage
- Notification Service

and explains how they interact to process a message from sender to receiver.

# WhatsApp System Design

# Chapter 2 - High-Level Architecture (HLD)

---

# Goal

Before discussing message flow, we first need to identify the major components of the system.

A common mistake in interviews is immediately jumping into Kafka, Redis, or databases.

Instead, first identify the building blocks and clearly explain the responsibility of each component.

---

# High-Level Architecture

```
                                 Clients
                    (Android / iOS / Web / Desktop)
                                         │
                                 WebSocket Connection
                                         │
                                  Load Balancer
                                         │
      ┌──────────────────────────────────┼──────────────────────────────────┐
      ▼                                  ▼                                  ▼
 WebSocket Server 1                WebSocket Server 2                WebSocket Server 3
(Connection Manager)              (Connection Manager)              (Connection Manager)
      │                                  │                                  │
      └──────────────────────────────────┼──────────────────────────────────┘
                                         │
                                  Load Balancer
                                         │
            ┌────────────────────────────┼────────────────────────────┐
            ▼                            ▼                            ▼
      Chat Service 1               Chat Service 2               Chat Service 3
      (Stateless)                  (Stateless)                 (Stateless)
            │                            │                            │
            └───────────────┬────────────┴────────────┬──────────────┘
                            │                         │
          ┌─────────────────┼──────────────┬──────────┼───────────────┐
          ▼                 ▼              ▼          ▼               ▼
     Message DB          Redis          Kafka     Object Storage   Group Service
                                           │
                                ┌──────────┼────────────┐
                                ▼          ▼            ▼
                        Notification   Analytics    Search
                           Service
```

---

# Why So Many Components?

A messaging system performs many different responsibilities.

Trying to solve everything inside one application quickly becomes difficult to scale.

Instead, every component has a well-defined responsibility.

---

# Component Responsibilities

---

## 1. Clients

Clients include

- Android
- iPhone
- Desktop
- Web

Responsibilities

- Display messages
- Send messages
- Upload media
- Read receipts
- Delivery receipts
- Typing indicators

Clients maintain a persistent WebSocket connection while the app is active.

---

## 2. Load Balancer

The Load Balancer distributes incoming WebSocket connections across multiple WebSocket Servers.

```
Clients

↓

Load Balancer

↓

WS1
WS2
WS3
```

Benefits

- High availability
- Horizontal scaling
- No single point of failure

---

## 3. WebSocket Servers

The WebSocket Server is responsible only for connection management.

Responsibilities

- Accept WebSocket connections
- Authenticate users
- Maintain active WebSocket sessions
- Heartbeats
- Push messages to clients
- Detect disconnects

Example

```java
ConcurrentHashMap<UserId, WebSocketSession> sessions;
```

Notice

WebSocket Servers do **not** contain messaging business logic.

They simply manage active network connections.

---

# Stateful Component

WebSocket Servers are **stateful**.

Why?

Because active WebSocket sessions exist only in memory.

Example

```
Bob

↓

WebSocket Session
```

If a WebSocket Server crashes,

clients reconnect and establish new WebSocket sessions.

---

## 4. Chat Service

The Chat Service contains all messaging business logic.

Responsibilities

- Authenticate sender
- Validate requests
- Generate Message IDs
- Store messages
- Update message status
- Read receipts
- Delivery receipts
- Group routing
- Offline delivery
- Publish Kafka events

Unlike WebSocket Servers,

Chat Services are completely stateless.

---

# Why Stateless?

A Chat Service processes one request.

Example

```
Receive Message

↓

Store Message

↓

Publish Kafka Event

↓

Return Response
```

After processing,

no request-specific state remains in memory.

Therefore,

multiple Chat Service instances can be added or removed easily.

---

## 5. Message Database

The Message Database is the **source of truth**.

Stores

- Messages
- Conversation IDs
- Status
- Sender
- Receiver
- Timestamp

Every message is persisted before delivery.

This guarantees durability.

---

## 6. Redis

Redis stores temporary information.

Examples

```
Alice → WS1

Bob → WS3
```

It also stores

- Online users
- Heartbeats
- Presence
- Optional typing state

Redis does **not** store permanent chat history.

---

# Why Redis?

Connection mappings change frequently.

Example

```
Bob

↓

WS3

↓

Disconnect

↓

Reconnect

↓

WS1
```

Databases are not ideal for this type of frequently changing data.

Redis provides extremely fast lookups.

---

## 7. Kafka

Kafka is the event backbone.

After successfully storing a message,

the Chat Service publishes

```
MessageCreated
```

Kafka consumers include

- Notification Service
- Analytics
- Search Indexing
- Audit Logging

This keeps the Chat Service lightweight.

---

## 8. Notification Service

Responsible for offline users.

If Redis indicates the recipient is offline,

the Notification Service sends push notifications using

- Firebase Cloud Messaging (FCM)
- Apple Push Notification Service (APNS)

---

## 9. Object Storage

Large media files are never stored inside the database.

Examples

- Images
- Videos
- Documents
- Voice Notes

Instead,

media is uploaded to object storage such as Amazon S3.

The Message Database stores only a reference (URL).

---

## 10. Group Service

Responsible for

- Group creation
- Membership
- Permissions
- Group metadata

Typical tables

```
Groups

GroupMembers
```

---

# Component Interaction

The architecture intentionally separates concerns.

| Component | Responsibility |
|------------|----------------|
| Client | User Interface |
| Load Balancer | Distribute traffic |
| WebSocket Server | Manage persistent connections |
| Chat Service | Business logic |
| Message DB | Source of truth |
| Redis | Fast temporary state |
| Kafka | Asynchronous event distribution |
| Notification Service | Offline notifications |
| Object Storage | Large media files |
| Group Service | Group metadata |

---

# Stateful vs Stateless

Understanding this distinction is extremely important.

## Stateful Components

```
WebSocket Servers

Redis

Database
```

These components maintain state.

---

## Stateless Components

```
Chat Services

Notification Service

Analytics Service
```

These services process requests and do not retain request-specific information after completion.

Stateless services scale horizontally with ease.

---

# Why Separate WebSocket Server From Chat Service?

A common interview question.

WebSocket Servers maintain millions of long-lived TCP connections.

Chat Services perform CPU-intensive business logic.

Separating them allows independent scaling.

Example

Suppose

```
10 million users online
```

We may require

```
200 WebSocket Servers
```

but only

```
30 Chat Service instances
```

because processing messages is much cheaper than maintaining millions of open network connections.

---

# Design Principles

The architecture follows several important principles.

### Separation of Concerns

Every component has one responsibility.

---

### Stateless Business Logic

Business logic scales independently.

---

### Event-Driven Architecture

Kafka decouples downstream services.

---

### Durable Storage

The Message Database is always the source of truth.

---

### In-Memory Routing

Redis provides extremely fast routing to connected users.

---

### Horizontal Scalability

Every major component can scale independently.

---

# What Comes Next

Now that we understand the overall architecture and the role of every component,

the next chapter focuses on one of the most important technologies used in modern chat systems:

# Chapter 3

**WebSockets Deep Dive**

Topics include

- Polling
- Long Polling
- Server-Sent Events (SSE)
- WebSockets
- Why WebSockets are ideal for chat
- WebSocket lifecycle
- One connection per device
- WebSocket Server vs WebSocket Connection
- Managing millions of persistent connections

# WhatsApp System Design

# Chapter 3 - WebSockets Deep Dive

---

# Why Do We Need WebSockets?

Imagine Alice sends a message to Bob.

Bob should see

```
Hi
```

almost instantly.

Question:

How does Bob's phone know a new message has arrived?

There are four possible approaches:

1. Polling
2. Long Polling
3. Server Sent Events (SSE)
4. WebSockets

Let's understand each.

---

# 1. Polling

The client repeatedly asks the server

```
Do I have any new messages?
```

Example

```
Bob

↓

GET /messages

↓

No

↓

1 second later

↓

GET /messages

↓

No

↓

1 second later

↓

GET /messages

↓

Yes

↓

Hello
```

---

## Advantages

- Very easy to implement
- Works everywhere

---

## Problems

Suppose Bob receives

```
1 message/minute
```

but polls every second.

```
60 requests

↓

59 useless
```

Problems

- Network overhead
- CPU overhead
- Database load
- Increased latency

Worst case latency equals

```
Polling Interval
```

---

# 2. Long Polling

Instead of responding immediately,

the server waits until

- a message arrives
- timeout occurs

Flow

```
Bob

↓

GET /messages

↓

(wait)

↓

Alice sends message

↓

Server responds

↓

Bob immediately opens another request
```

---

## Advantages

- Fewer requests than polling
- Better latency

---

## Problems

The server must keep thousands or millions of HTTP requests open.

Still requires repeatedly creating new HTTP requests.

---

# 3. Server Sent Events (SSE)

The client opens one HTTP connection.

The server continuously pushes updates.

```
Server

↓

Message 1

↓

Message 2

↓

Message 3
```

---

## Advantages

- Efficient
- Low latency
- Simple

---

## Limitation

Communication is

```
Server

↓

Client
```

only.

If the client wants to send data,

it must still use HTTP.

Not suitable for chat.

---

# 4. WebSockets

WebSockets upgrade an HTTP connection into a persistent bidirectional connection.

Connection

```
HTTP

↓

Upgrade

↓

WebSocket
```

Now both client and server can send data at any time.

```
Client

⇅

Server
```

---

# Why WebSockets?

Chat applications require

- Low latency
- Continuous communication
- Bidirectional communication
- Very low overhead

WebSockets satisfy all four.

---

# HTTP vs WebSocket

## HTTP

Every request creates a new conversation.

```
Request

↓

Response

↓

Connection Closed
```

---

## WebSocket

One connection stays open.

```
Connect

↓

Message

↓

Message

↓

Message

↓

Disconnect
```

---

# Real World Analogy

## Polling

Every minute,

you ask

```
Do I have mail?
```

---

## Long Polling

You tell the mailman

```
Don't answer until you actually have mail.
```

---

## SSE

Watching television.

Only the broadcaster talks.

---

## WebSocket

Talking on the phone.

Both people can talk whenever they want.

---

# WebSocket Lifecycle

```
Connect

↓

Authenticate

↓

Store Session

↓

Heartbeat

↓

Exchange Messages

↓

Disconnect

↓

Remove Session
```

---

# WebSocket Connection vs WebSocket Server

This is one of the most misunderstood topics.

---

## WebSocket Connection

One client

↓

One server

```
Alice Phone

↓

WebSocket Connection

↓

WebSocket Server
```

A connection belongs to exactly one client.

---

## WebSocket Server

A WebSocket Server manages thousands of WebSocket connections.

Example

```
                 WebSocket Server

       Alice Connection

       Bob Connection

       Charlie Connection

       David Connection
```

Internally

```java
ConcurrentHashMap<UserId, WebSocketSession> sessions;
```

---

# One Connection Per Device

Suppose Bob uses

- iPhone
- Laptop
- Tablet

Bob now has

```
iPhone

↓

Connection 1

↓

WS2


Laptop

↓

Connection 2

↓

WS3


Tablet

↓

Connection 3

↓

WS5
```

One connection per device.

---

# Multiple WebSocket Servers

Millions of users cannot connect to one server.

Production systems use

```
             Load Balancer

        /       |        \

      WS1      WS2      WS3
```

Each server manages only the users connected to it.

---

# Where Are Connections Stored?

Connections are stored in memory.

Example

```java
ConcurrentHashMap<UserId, WebSocketSession> sessions;
```

Example

```
Alice

↓

Session A

Bob

↓

Session B

Charlie

↓

Session C
```

This lookup is O(1).

---

# How Does The System Find Bob?

Suppose

Alice sends

```
Hi Bob
```

WS1 does not know where Bob is.

Redis stores

```
Bob

↓

WS3
```

Chat Service forwards

```
WS3

↓

sessions.get("Bob")

↓

WebSocketSession

↓

send(message)
```

Bob receives the message.

---

# Why Not Store Socket IDs In Redis?

Socket objects exist only inside a WebSocket Server.

Redis stores only

```
User

↓

WebSocket Server
```

Each WebSocket Server already knows the actual session object.

---

# Heartbeats

Suppose Bob loses internet.

The server may not immediately know.

Every

```
30 seconds
```

the client sends

```
PING
```

Server replies

```
PONG
```

If no heartbeat is received for

```
90 seconds
```

the user is considered offline.

Redis removes

```
Bob

↓

ONLINE
```

---

# Scaling

WebSocket Servers are stateful.

Each server stores active connections in memory.

Scaling is done by adding more WebSocket Servers.

```
Load Balancer

↓

WS1

WS2

WS3

WS4

WS5
```

Clients reconnect automatically if one server fails.

---

# Advantages Of WebSockets

- Full duplex communication
- Very low latency
- Low protocol overhead
- Persistent connection
- Perfect for real-time messaging
- Supports millions of concurrent users

---

# Comparison

| Feature | Polling | Long Polling | SSE | WebSocket |
|----------|----------|--------------|-----|-----------|
| Client → Server | Yes | Yes | HTTP | Yes |
| Server → Client | No | Yes | Yes | Yes |
| Bidirectional | No | No | No | Yes |
| Persistent Connection | No | Temporary | Yes | Yes |
| Overhead | High | Medium | Low | Very Low |
| Best Use Case | Simple Apps | Legacy Notifications | Live Dashboards | Chat Applications |

---

# Interview Questions

## Why not Polling?

Too many unnecessary HTTP requests.

---

## Why not Long Polling?

Still requires repeatedly opening HTTP requests and keeping many requests waiting.

---

## Why not SSE?

Only supports server-to-client communication.

Chat requires both directions.

---

## Why WebSockets?

Persistent bidirectional communication with very low latency and minimal overhead.

---

# Interview Summary

> A chat application requires persistent, low-latency, bidirectional communication between clients and the server. Polling and Long Polling generate unnecessary HTTP overhead, while Server-Sent Events only support one-way communication. WebSockets establish a single long-lived connection that allows both the client and server to exchange messages at any time. Each client device maintains its own WebSocket connection to a WebSocket Server, which manages thousands of active sessions in memory. Multiple WebSocket Servers are deployed behind a load balancer to support millions of concurrent users. Redis maintains a lightweight mapping from `userId` to `WebSocketServer`, enabling the Chat Service to efficiently route messages to the correct server for delivery.

# WhatsApp System Design

# Chapter 4 - Chat Service Deep Dive

---

# What Is The Chat Service?

The Chat Service is the **brain** of the messaging system.

Unlike the WebSocket Server, which only manages network connections, the Chat Service contains all the business logic.

Think of the WebSocket Server as the **connection manager** and the Chat Service as the **application server**.

---

# Why Separate Chat Service From WebSocket Server?

Many candidates initially design

```
Client

↓

WebSocket Server

↓

Database
```

This works.

But now imagine adding

- Read Receipts
- Group Chat
- Notifications
- Media
- Search
- Analytics
- Audit Logs

The WebSocket Server becomes huge.

Instead we separate responsibilities.

```
Client

↓

WebSocket Server

↓

Chat Service
```

---

# Responsibilities

The Chat Service is responsible for

- Authenticate sender
- Validate conversation
- Generate Message ID
- Persist messages
- Update message status
- Read receipts
- Delivery receipts
- Group routing
- Offline delivery
- Publish Kafka events
- Determine recipient
- Redis lookup

Everything related to messaging lives here.

---

# Stateless Design

One of the most important interview concepts.

Chat Service is **Stateless**.

Suppose Alice sends

```
Hi Bob
```

The Chat Service

```
Receive Request

↓

Validate

↓

Generate Message ID

↓

Store Database

↓

Publish Kafka

↓

Return Response
```

After returning,

nothing remains in memory.

Every future request can go to another Chat Service instance.

---

# Why Stateless?

Stateless services

- Scale easily
- Recover easily
- Load balance easily

If Chat Service 2 crashes,

the Load Balancer simply routes requests to

```
Chat Service 1

or

Chat Service 3
```

Nothing is lost because all important state is stored outside.

---

# Multiple Chat Services

Production architecture

```
                 Load Balancer

           /         |          \

      Chat S1    Chat S2     Chat S3
```

Each request may reach a different instance.

Example

Alice

↓

Chat S2

Bob

↓

Chat S1

Charlie

↓

Chat S3

This works because every Chat Service shares

- Database
- Redis
- Kafka

---

# Message Lifecycle

Suppose Alice sends

```
Hi Bob
```

The complete flow is

```
Alice

↓

WebSocket Server

↓

Chat Service

↓

Generate Snowflake ID

↓

Persist Message

↓

Publish Kafka Event

↓

Redis Lookup

↓

Forward To Bob
```

Let's examine every step.

---

# Step 1 - Authenticate

Before processing,

verify

- JWT
- Session
- Sender

Never trust client input.

---

# Step 2 - Validate Conversation

Questions

- Does Bob exist?
- Is Alice blocked?
- Does Alice belong to the group?
- Is message size valid?

Reject invalid requests.

---

# Step 3 - Generate Message ID

Every message receives a globally unique ID.

Typically

Snowflake ID

Example

```
MessageID

98765432112345
```

Benefits

- Globally unique
- Roughly time ordered
- No database sequence bottleneck

---

# Step 4 - Persist Message

The Chat Service writes

```
Messages
```

table.

Example

| MessageId | Sender | Receiver | Status |
|-----------|----------|------------|----------|
|101|Alice|Bob|SENT|

This is the source of truth.

---

# Why Store Before Delivery?

Imagine

Deliver first

↓

Database crashes

Bob saw the message

↓

Message lost forever

Bad.

Therefore

Always

```
Persist First

↓

Deliver Later
```

---

# Step 5 - Publish Kafka Event

After persistence,

publish

```
MessageCreated
```

Kafka Event

```
MessageCreated

MessageId

Conversation

Sender

Receiver
```

---

# Why Kafka?

Many services care about a new message.

Examples

- Notification Service
- Analytics
- Search
- Audit
- Fraud Detection

Without Kafka

Chat Service directly calls

every downstream service.

Terrible coupling.

Kafka decouples them.

---

# Step 6 - Locate Recipient

Question

Where is Bob?

Redis contains

```
Bob

↓

WS2
```

Chat Service asks Redis

```
GET Bob
```

Returns

```
WS2
```

---

# Step 7 - Forward Message

Chat Service sends

```
Deliver

↓

WS2
```

WS2 performs

```java
sessions.get("Bob")
```

Returns

```
WebSocketSession
```

Then

```java
session.send(message);
```

Bob receives

```
Hi
```

---

# Step 8 - Delivery Receipt

Bob's client immediately responds

```
DELIVERED
```

Flow

```
Bob

↓

WS2

↓

Chat Service

↓

Database

↓

Status = DELIVERED
```

Then

Alice is notified.

```
✓✓
```

---

# Step 9 - Read Receipt

Later

Bob opens the conversation.

Bob sends

```
READ
```

Flow

```
Bob

↓

Chat Service

↓

Database

↓

Status = READ

↓

Notify Alice
```

Alice now sees

```
Blue ✓✓
```

---

# Online User Flow

Redis

```
Bob

↓

WS2
```

Chat Service immediately forwards.

---

# Offline User Flow

Redis

```
Bob

↓

OFFLINE
```

Chat Service

```
Persist

↓

Kafka

↓

Notification Service

↓

Push Notification
```

When Bob reconnects

```
SELECT *

FROM Messages

WHERE

Status = UNDELIVERED
```

Messages are delivered.

---

# Why Doesn't Chat Service Keep Sessions?

Suppose Chat Service stored

```
Bob

↓

Socket Object
```

Now

100 Chat Services

would each maintain connections.

Scaling becomes difficult.

Instead

Connection Management

↓

WebSocket Servers

Business Logic

↓

Chat Service

Clean separation.

---

# Chat Service Responsibilities Summary

| Responsibility | Why? |
|---------------|------|
| Authenticate | Security |
| Validate | Data Integrity |
| Generate Snowflake ID | Unique Ordering |
| Persist Message | Durability |
| Publish Kafka | Asynchronous Processing |
| Redis Lookup | Find Recipient |
| Route Message | Real-time Delivery |
| Update Status | Read/Delivery Receipts |
| Offline Handling | Reliable Delivery |

---

# Design Principles

The Chat Service follows

## Single Responsibility

Only business logic.

---

## Stateless Design

No in-memory request state.

---

## Event Driven

Uses Kafka.

---

## Durable First

Persist before delivery.

---

## Fast Routing

Uses Redis.

---

# Interview Questions

## Why multiple Chat Services?

Stateless services scale horizontally and eliminate single points of failure.

---

## Why not merge Chat Service and WebSocket Server?

WebSocket Servers manage long-lived connections.

Chat Services execute business logic.

Separating them allows each tier to scale independently.

---

## Why store before delivering?

To guarantee durability.

A delivered-but-not-persisted message is unacceptable.

---

## Why publish Kafka after storing?

The database is the source of truth.

Kafka distributes events to downstream services but should not replace durable storage.

---

# Complete Flow

```
Alice

↓

WebSocket Server

↓

Chat Service

↓

Authenticate

↓

Validate

↓

Generate Snowflake ID

↓

Persist Message

↓

Publish Kafka Event

↓

Redis Lookup

↓

Forward To Recipient WS Server

↓

Recipient

↓

Delivery Receipt

↓

Database Update

↓

Read Receipt

↓

Database Update
```

---

# Interview Summary

> The Chat Service is the stateless business layer of the messaging platform. It authenticates requests, validates conversations, generates globally unique message IDs using Snowflake, persists every message before delivery, publishes a `MessageCreated` event to Kafka, and uses Redis to locate the recipient's WebSocket Server for real-time delivery. Delivery and read acknowledgements follow the same path back through the Chat Service, which updates the message status in the database and notifies the sender. Because all durable state is stored in external systems such as the database, Redis, and Kafka, multiple Chat Service instances can be added or removed without affecting correctness, allowing the system to scale horizontally.

# WhatsApp System Design

# Chapter 5 - Redis Deep Dive

---

# Why Do We Need Redis?

One of the most common interview questions is:

> Why can't we simply use the database?

Suppose Alice sends a message.

The Chat Service needs to answer one question immediately:

```
Where is Bob currently connected?
```

This lookup happens for **every single message**.

Doing a database query for every message would become a bottleneck.

Instead, we keep this frequently changing information in Redis.

---

# What Does Redis Store?

Redis stores **temporary, fast-changing state**, not permanent chat history.

Examples include

- User → WebSocket Server mapping
- Online/Offline presence
- Last heartbeat
- Typing indicators (optional)
- Session metadata

Redis **does not** store

- Chat history
- Group messages
- Media
- Read receipts (permanent)
- User profile

Those belong in the database.

---

# Why Not Database?

Imagine

```
100 Million online users
```

Every user

- connects
- disconnects
- reconnects
- switches WiFi
- switches LTE
- changes WebSocket server

That's millions of updates every minute.

Databases are optimized for

- durability
- consistency
- complex queries

Not for extremely frequent state changes.

Redis is memory-based and designed exactly for this.

---

# User → WebSocket Mapping

This is Redis's most important responsibility.

Example

```
Alice

↓

WS1

Bob

↓

WS3

Charlie

↓

WS2
```

Conceptually

```
Alice -> WS1

Bob -> WS3

Charlie -> WS2
```

---

# Why Do We Need This Mapping?

Suppose Alice sends

```
Hi Bob
```

Flow

```
Chat Service

↓

Redis

↓

Bob

↓

WS3
```

Now Chat Service knows exactly which WebSocket Server should receive the message.

Without Redis,

the Chat Service would have to ask every WebSocket Server

```
Do you have Bob?
```

Terrible.

---

# Redis Lookup

Conceptually

```
GET user:Bob
```

Returns

```
WS3
```

Chat Service forwards

```
Deliver

↓

WS3
```

WS3 performs

```java
sessions.get("Bob")
```

Bob receives the message.

---

# Presence

Redis also stores online users.

Example

```
user:Alice

status = ONLINE
```

or conceptually

```
Alice

↓

ONLINE
```

---

# Online Flow

Bob opens WhatsApp.

```
Bob

↓

Authenticate

↓

Connect WebSocket

↓

Redis

↓

ONLINE
```

Now everyone immediately sees

```
Online
```

---

# Offline Flow

Bob closes the app.

WebSocket disconnects.

Redis updates

```
Bob

↓

OFFLINE
```

or simply removes the mapping.

---

# Unexpected Disconnects

Suppose

- Battery dies
- Internet disappears
- Phone crashes

The server doesn't immediately know.

---

# Heartbeats

Every

```
30 seconds
```

the client sends

```
PING
```

Server replies

```
PONG
```

If no heartbeat arrives for

```
90 seconds
```

the server assumes

```
OFFLINE
```

Redis removes

```
Bob

↓

ONLINE
```

---

# Why Heartbeats?

TCP connections don't always close gracefully.

Heartbeats allow us to detect

- Network failures
- Device crashes
- Lost connections

---

# Last Seen

Online status changes constantly.

Last Seen should be permanent.

Therefore

Redis

```
ONLINE
```

Database

```
Last Seen = 10:42 AM
```

When Bob disconnects

```
Update DB

↓

Last Seen
```

Then remove

```
ONLINE
```

from Redis.

---

# Typing Indicator

Suppose Alice starts typing.

Redis may optionally store

```
Conversation123

↓

Alice Typing
```

with

```
TTL = 3 seconds
```

If Alice stops typing,

or no events arrive,

Redis automatically expires the key.

No cleanup required.

---

# Why TTL?

Typing indicators are temporary.

If Alice loses internet,

the typing indicator disappears automatically.

No stale state.

---

# Redis Data Structures

Although Redis supports many structures,

a messaging system mostly uses

## String

```
user:Bob

↓

WS3
```

---

## Hash

```
user:Bob

status

ONLINE

lastHeartbeat

10:30
```

---

## Set

Useful for

```
Online Users
```

Example

```
SADD onlineUsers Alice

SADD onlineUsers Bob
```

---

## Sorted Set (Optional)

Useful for

- ranking
- recent activity
- leaderboards

Not commonly required for core messaging.

---

# Redis Cluster

A single Redis server cannot handle millions of users.

Production systems use

```
Redis Cluster

      /     |      \

Node1 Node2 Node3
```

Keys are automatically distributed.

Example

```
Alice

↓

Node1

Bob

↓

Node3
```

Scaling is horizontal.

---

# Failure Scenario

Suppose Redis crashes.

Do we lose chat history?

No.

Redis never stores permanent messages.

Worst case

- users reconnect
- mappings are rebuilt
- presence is restored

Chat history remains safe in the Message Database.

---

# Redis vs Database

| Redis | Database |
|---------|----------|
| Memory | Disk |
| Very Fast | Slower |
| Temporary State | Permanent State |
| Presence | Messages |
| Routing | Chat History |
| Heartbeats | Read Receipts |
| Typing | Media Metadata |

---

# Redis vs WebSocket Server Memory

This is another common interview question.

WebSocket Server

```
Bob

↓

WebSocketSession
```

Redis

```
Bob

↓

WS3
```

Notice the difference.

Redis knows

```
Which server?
```

The WebSocket Server knows

```
Which socket?
```

Redis never stores actual socket objects.

---

# Complete Routing Flow

```
Alice

↓

Chat Service

↓

Redis

↓

Bob → WS3

↓

WS3

↓

sessions.get("Bob")

↓

WebSocketSession

↓

Bob
```

---

# Why Redis Instead of Calling Every WS Server?

Without Redis

```
Chat Service

↓

WS1?

↓

WS2?

↓

WS3?

↓

WS4?
```

Every message would require broadcasting a lookup.

Complexity

```
O(Number of WS Servers)
```

With Redis

```
Chat Service

↓

Redis

↓

WS3
```

Complexity

```
O(1)
```

Huge improvement.

---

# Design Principles

Redis stores

- Frequently changing data
- Temporary state
- Fast lookups

Databases store

- Durable business data

This separation allows each technology to do what it does best.

---

# Interview Questions

## Why Redis?

Because connection mappings and presence change extremely frequently and require low-latency lookups.

---

## Why not store chat history in Redis?

Redis is in-memory and optimized for temporary state.

Messages require durable storage.

---

## Why not store WebSocket sessions in Redis?

WebSocket sessions are in-memory objects owned by a specific WebSocket Server.

Redis stores only which server owns the connection.

---

## What happens if Redis crashes?

Presence information and routing mappings are temporarily lost.

Users reconnect, and mappings are rebuilt.

No chat history is lost because messages are stored in the Message Database.

---

# Interview Summary

> Redis acts as the distributed in-memory state store for the messaging system. It maintains lightweight, fast-changing information such as `userId → WebSocketServer` mappings, online presence, heartbeats, and optionally typing indicators. Whenever the Chat Service needs to deliver a message, it performs an O(1) Redis lookup to determine which WebSocket Server currently owns the recipient's connection. Redis complements the Message Database rather than replacing it—the database stores durable chat history, while Redis stores only transient routing and presence information. This separation enables low-latency message delivery while keeping permanent business data safely persisted.

# WhatsApp System Design

# Chapter 6 - Kafka Deep Dive

---

# Why Do We Need Kafka?

One of the biggest mistakes candidates make is trying to make the Chat Service do everything.

Suppose Alice sends

```
Hi Bob
```

The Chat Service now needs to

- Deliver to Bob
- Send Push Notification
- Update Analytics
- Index Search
- Audit Logging
- Fraud Detection
- Recommendation Engine

If Chat Service directly calls every service,

```
                Chat Service

      /      |      |      |      \

 Notification Analytics Search Audit Fraud
```

Problems

- Tight coupling
- Slow response
- Difficult to add new services
- One service failure impacts messaging

Instead,

the Chat Service should do only two critical things

1. Persist the message
2. Publish an event

Everything else becomes asynchronous.

---

# Event-Driven Architecture

Instead of directly calling services,

Chat Service publishes

```
MessageCreated
```

event.

```
Chat Service

↓

Kafka

↓

Consumers
```

Now each consumer works independently.

---

# High Level Architecture

```
Alice

↓

WebSocket Server

↓

Chat Service

↓

Message Database

↓

Kafka

      │
      │
 ┌────┼──────────────┬─────────────┐
 ▼    ▼              ▼             ▼
Notification    Analytics     Search     Audit
 Service
```

The Chat Service is now completely decoupled.

---

# What Does Kafka Store?

Kafka stores

```
Events
```

Example

```json
{
   "event":"MessageCreated",
   "messageId":101,
   "sender":"Alice",
   "receiver":"Bob",
   "conversationId":200,
   "timestamp":"10:30"
}
```

Notice

Kafka stores

**events**

not

database rows.

---

# Why Publish After Database?

Question

Should we publish first?

```
Chat Service

↓

Kafka

↓

Database
```

No.

Imagine

```
Kafka Success

↓

Database Failure
```

Consumers think

the message exists,

but it was never saved.

Wrong.

Correct order

```
Persist

↓

Publish Event
```

The Message Database is the source of truth.

Kafka distributes changes.

---

# Producers

The Chat Service is the producer.

```
Chat Service

↓

Kafka Topic
```

One request

↓

One event.

---

# Consumers

Many services consume independently.

```
                 Kafka

        /      |      |      \

Notification Analytics Search Audit
```

Each service has its own responsibility.

---

# Notification Service

Consumes

```
MessageCreated
```

Checks Redis.

If receiver offline

↓

Send Push Notification

---

# Analytics Service

Consumes

```
MessageCreated
```

Updates

- Messages/day
- Active users
- Traffic graphs

---

# Search Service

Consumes

```
MessageCreated
```

Indexes messages.

Users can later search

```
Vacation

Passport

Tickets
```

---

# Audit Service

Stores immutable audit logs.

Useful for

- Compliance
- Security
- Investigations

---

# Why Kafka Instead Of REST Calls?

Suppose Analytics is slow.

REST

```
Chat Service

↓

Analytics

↓

Timeout
```

Alice waits longer.

With Kafka

```
Chat Service

↓

Kafka

↓

Return Response
```

Analytics processes later.

Messaging remains fast.

---

# Kafka Topics

Common topics

```
message-created

message-read

message-delivered

group-created

media-uploaded
```

Different consumers subscribe to different topics.

---

# Consumer Groups

Suppose Analytics receives

```
1 Million Events/sec
```

One consumer isn't enough.

Kafka allows

```
Analytics Group

Analytics1

Analytics2

Analytics3
```

Messages are shared across consumers.

Horizontal scaling.

---

# Partitions

Topics are divided into partitions.

```
Message Topic

──────────────

Partition 0

Partition 1

Partition 2
```

Multiple consumers process partitions simultaneously.

---

# Why Partition By ConversationId?

Suppose

Conversation

Alice ↔ Bob

Message1

Message2

Message3

If all messages go to the same partition,

Kafka guarantees

```
1

↓

2

↓

3
```

Ordering is preserved.

If messages were spread across partitions,

ordering could break.

Therefore

```
Partition Key

=

ConversationId
```

---

# Kafka Delivery Semantics

Kafka supports

### At Most Once

Message may be lost.

Never duplicated.

---

### At Least Once

Message never lost.

Duplicates possible.

Default behavior.

---

### Exactly Once

Neither lost nor duplicated.

Requires

- Idempotent Producers
- Transactions
- Transaction-aware Consumers/Sinks

Higher complexity.

---

# What Do We Use?

For chat systems,

the Message Database is already the source of truth.

Kafka mainly distributes events.

Therefore

```
At Least Once
```

is usually sufficient.

Duplicate analytics or notifications can be handled using idempotency.

---

# What Happens If Consumer Crashes?

Suppose

Analytics

```
Processed

Message 1

Message 2

Crash
```

Kafka remembers

```
Last Committed Offset
```

Analytics restarts

↓

Continues

from

```
Message 3
```

No events are lost.

---

# Replay

One huge advantage.

Suppose

Search Service had a bug.

Fix deployed.

Simply replay

```
MessageCreated
```

events.

Search index is rebuilt.

Without touching production.

---

# Why Kafka Is Not The Source Of Truth

Kafka has retention.

Messages eventually expire.

Therefore

```
Database

↓

Source of Truth

Kafka

↓

Event Distribution
```

Never the opposite.

---

# Advantages

- Loose coupling
- Horizontal scalability
- Replay capability
- Fault tolerance
- Asynchronous processing
- Multiple consumers
- Ordered processing within partitions

---

# Kafka vs Database

| Database | Kafka |
|------------|---------|
| Stores business data | Stores events |
| Durable source of truth | Event stream |
| Query history | Replay events |
| Supports updates | Immutable log |
| Read by APIs | Read by consumers |

---

# Common Interview Questions

## Why Kafka?

To decouple downstream services and process non-critical work asynchronously.

---

## Why publish after storing?

Because the database is the source of truth.

---

## Why not REST?

REST tightly couples services and increases latency.

---

## Why ConversationId partitioning?

Kafka guarantees ordering within a partition.

Putting all messages of one conversation in the same partition preserves message order.

---

## What if Kafka crashes?

Messages remain safely stored in the database.

Kafka recovers from replicated logs.

Consumers resume from committed offsets.

---

# Interview Summary

> Kafka acts as the event backbone of the messaging platform. After a message is successfully persisted in the Message Database, the Chat Service publishes a `MessageCreated` event to Kafka. Downstream services such as Notification, Analytics, Search, and Audit consume these events independently without increasing the latency of message delivery. Kafka enables loose coupling, horizontal scalability, replay capability, and ordered processing within a conversation by partitioning events using the `conversationId`. The Message Database remains the durable source of truth, while Kafka is responsible for reliable event distribution.

# WhatsApp System Design

# Chapter 7 - End-to-End Message Flow

---

# Goal

This chapter follows **one message** from the moment Alice presses **Send** until Bob reads it.

By the end of this chapter, you should understand how every major component works together.

We will cover

- Message sending
- Database persistence
- Kafka publishing
- Redis routing
- WebSocket delivery
- Delivery receipts
- Read receipts
- Offline delivery
- Push notifications

---

# Scenario

Alice sends

```
Hi Bob
```

Assume

- Alice is connected to WS1
- Bob is connected to WS3

Redis contains

```
Alice -> WS1

Bob -> WS3
```

---

# Complete Architecture

```
                    Alice

                      │

              WebSocket Connection

                      │

                     WS1

                      │

                Chat Service

      ┌──────────┬───────────────┬─────────────┐
      ▼          ▼               ▼             ▼

 Message DB   Redis Lookup     Kafka      Notification

                      │

                      ▼

                     WS3

                      │

                      ▼

                    Bob
```

---

# Step 1 - Alice Presses Send

Alice types

```
Hi Bob
```

The client sends

```json
{
   "sender":"Alice",
   "receiver":"Bob",
   "message":"Hi Bob"
}
```

over the existing WebSocket connection.

No HTTP request is created.

---

# Step 2 - WebSocket Server

Alice's request reaches

```
WS1
```

Responsibilities

- Validate WebSocket session
- Verify authentication
- Forward request

WS1 performs no business logic.

It forwards the request to the Chat Service.

---

# Step 3 - Chat Service

The Chat Service receives

```
Hi Bob
```

It performs

- Authentication
- Validation
- Conversation checks
- Block checks
- Group membership (if applicable)

---

# Step 4 - Generate Message ID

The Chat Service generates

```
Snowflake ID

98765432112345
```

Benefits

- Globally unique
- Roughly time ordered
- No database bottleneck

---

# Step 5 - Persist Message

The message is written to the database.

Messages

| MessageId | Sender | Receiver | Message | Status |
|-----------|----------|-----------|----------|---------|
|101|Alice|Bob|Hi Bob|SENT|

Status

```
SENT
```

means

The server has safely stored the message.

Not that Bob has received it.

---

# Why Persist First?

Suppose we deliver first.

```
Bob Receives

↓

Database Crash
```

Now

Bob saw

```
Hi Bob
```

but the message doesn't exist.

Impossible to recover.

Therefore

Always

```
Persist

↓

Deliver
```

---

# Step 6 - Publish Kafka Event

The Chat Service publishes

```
MessageCreated
```

Example

```json
{
   "event":"MessageCreated",
   "messageId":101,
   "sender":"Alice",
   "receiver":"Bob"
}
```

Kafka consumers

- Notification
- Analytics
- Search
- Audit

This happens asynchronously.

Alice doesn't wait.

---

# Step 7 - Find Bob

Question

Where is Bob?

Redis

```
Bob

↓

WS3
```

The Chat Service performs

```
GET user:Bob
```

Returns

```
WS3
```

---

# Step 8 - Forward To Bob

Chat Service forwards

```
Deliver

↓

WS3
```

WS3 executes

```java
sessions.get("Bob")
```

Returns

```
WebSocketSession
```

WS3 pushes

```
Hi Bob
```

Bob receives the message instantly.

---

# Step 9 - Delivery Receipt

Bob's client immediately sends

```json
{
   "messageId":101,
   "event":"DELIVERED"
}
```

Flow

```
Bob

↓

WS3

↓

Chat Service
```

---

# Step 10 - Update Database

Database

Before

| MessageId | Status |
|-----------|---------|
|101|SENT|

After

| MessageId | Status |
|-----------|---------|
|101|DELIVERED|

---

# Step 11 - Notify Alice

Redis

```
Alice

↓

WS1
```

Chat Service forwards

```
DELIVERED

↓

WS1

↓

Alice
```

Alice now sees

```
✓✓
```

---

# Step 12 - Read Receipt

Bob opens the conversation.

Client sends

```json
{
   "messageId":101,
   "event":"READ"
}
```

Flow

```
Bob

↓

WS3

↓

Chat Service

↓

Database
```

Database

| MessageId | Status |
|-----------|---------|
|101|READ|

---

# Step 13 - Notify Alice

Redis

```
Alice

↓

WS1
```

Forward

```
READ

↓

Alice
```

Alice now sees

```
Blue ✓✓
```

---

# Online Message Flow

```
Alice

↓

WS1

↓

Chat Service

↓

Generate Snowflake ID

↓

Persist Database

↓

Publish Kafka Event

↓

Redis Lookup

↓

WS3

↓

Bob

↓

Delivered

↓

Database Update

↓

Alice

↓

Read

↓

Database Update

↓

Alice
```

---

# Offline User Flow

Suppose Bob isn't online.

Redis

```
Bob

↓

OFFLINE
```

Now

the Chat Service cannot push the message.

Instead

```
Persist Message

↓

Publish Kafka

↓

Notification Service

↓

Push Notification
```

The message remains safely stored.

---

# Notification Flow

Notification Service consumes

```
MessageCreated
```

Checks Redis

```
Bob

↓

OFFLINE
```

Sends

- APNS
- FCM

Example

```
Alice sent you a message
```

---

# Bob Opens WhatsApp

Bob taps notification.

The app launches.

A new WebSocket connection is established.

Redis updates

```
Bob

↓

WS2
```

---

# Fetch Undelivered Messages

The Chat Service executes

```sql
SELECT *
FROM Messages
WHERE Receiver='Bob'
AND Status='UNDELIVERED'
ORDER BY MessageId;
```

Returns

```
Hi Bob
```

The message is delivered.

Database becomes

```
DELIVERED
```

---

# Complete Offline Flow

```
Alice

↓

WS1

↓

Chat Service

↓

Persist Database

↓

Kafka

↓

Notification Service

↓

Push Notification

↓

Bob Opens App

↓

Reconnect

↓

Fetch Undelivered

↓

Deliver

↓

DELIVERED

↓

READ
```

---

# Why Is This Architecture Reliable?

Even if

- WS crashes
- Notification Service crashes
- Kafka is temporarily unavailable

the message is already persisted in the Message Database.

Nothing is lost.

The database is always the source of truth.

---

# Responsibilities During Message Flow

| Component | Responsibility |
|-----------|----------------|
| Client | Send/Receive messages |
| WebSocket Server | Manage connection |
| Chat Service | Business logic |
| Message Database | Durable storage |
| Kafka | Event distribution |
| Redis | Recipient routing |
| Notification Service | Offline notifications |

---

# Sequence Diagram

```
Alice
 │
 │ Send Message
 ▼
WS1
 │
 ▼
Chat Service
 │
 ├──────────────► Generate Snowflake ID
 │
 ├──────────────► Store in Message DB
 │
 ├──────────────► Publish MessageCreated to Kafka
 │
 ├──────────────► Lookup Bob in Redis
 │
 ▼
WS3
 │
 ▼
Bob
 │
 │ DELIVERED
 ▼
Chat Service
 │
 ▼
Message DB
 │
 ▼
WS1
 │
 ▼
Alice (✓✓)
 │
 │ READ
 ▼
Chat Service
 │
 ▼
Message DB
 │
 ▼
WS1
 │
 ▼
Alice (Blue ✓✓)
```

---

# Interview Questions

## Why persist before delivery?

To guarantee durability.

---

## Why Redis lookup?

To quickly determine which WebSocket Server owns the recipient's connection.

---

## Why Kafka after persistence?

The database is the source of truth.

Kafka distributes events asynchronously.

---

## Why WebSocket instead of HTTP?

Persistent bidirectional communication with low latency.

---

## Why update message status?

To support

- SENT
- DELIVERED
- READ

and display the correct UI.

---

# Interview Summary

> When Alice sends a message, the request travels over an existing WebSocket connection to a WebSocket Server, which forwards it to the Chat Service. The Chat Service authenticates the request, validates the conversation, generates a Snowflake ID, and persists the message in the Message Database with a `SENT` status. After persistence, it publishes a `MessageCreated` event to Kafka for downstream services and performs a Redis lookup to determine which WebSocket Server currently manages Bob's connection. If Bob is online, the message is immediately forwarded to his WebSocket Server and delivered. Bob's client sends `DELIVERED` and later `READ` acknowledgements, allowing the Chat Service to update the database and notify Alice so her UI progresses from ✓ to ✓✓ to blue ✓✓. If Bob is offline, the message remains safely stored in the database while the Notification Service sends a push notification. When Bob reconnects, the Chat Service retrieves undelivered messages from the database and delivers them over the newly established WebSocket connection.

# WhatsApp System Design

# Chapter 8 - Database Design Deep Dive

---

# Goal

A messaging application is fundamentally a **data-intensive system**.

Everything eventually ends up in a database.

A good database design should support

- Fast message writes
- Fast conversation retrieval
- Read receipts
- Group chats
- Offline delivery
- Scalability

In this chapter we'll design the database from scratch.

---

# High Level Database Schema

```
Users

↓

Conversations

↓

Messages

↓

Groups

↓

GroupMembers

↓

MessageRecipient (Fan-out on Write)

OR

↓

UserGroupState (Fan-out on Read)
```

---

# 1. Users Table

Stores user information.

| Column | Type | Description |
|---------|------|-------------|
| userId | BIGINT | Primary Key |
| username | VARCHAR | Display Name |
| phone | VARCHAR | Login |
| profilePic | VARCHAR | URL |
| lastSeen | TIMESTAMP | Last active |
| createdAt | TIMESTAMP | Account creation |

Example

| userId | username |
|----------|-----------|
|1|Alice|
|2|Bob|
|3|Charlie|

---

# 2. Conversations Table

Every chat has one Conversation ID.

This simplifies routing.

Instead of searching

```
Alice

Bob
```

we use

```
ConversationId
```

---

## One-to-One

| conversationId | type |
|----------------|------|
|101|DIRECT|

---

## Group

| conversationId | type |
|----------------|------|
|500|GROUP|

---

# Why Conversation Table?

Without it,

every query becomes

```
WHERE

(sender=Alice AND receiver=Bob)

OR

(sender=Bob AND receiver=Alice)
```

Instead

```
WHERE conversationId=101
```

Much cleaner.

---

# 3. ConversationMembers

Maps users to conversations.

For Direct Chat

| conversationId | userId |
|----------------|---------|
|101|Alice|
|101|Bob|

---

For Group

| conversationId | userId |
|----------------|---------|
|500|Alice|
|500|Bob|
|500|Charlie|
|500|David|

Notice

This table works for

- Direct chats
- Groups

Many companies don't keep separate

```
Groups

GroupMembers
```

Instead

everything is represented as a conversation.

---

# 4. Messages Table

This is the most important table.

| Column | Description |
|---------|-------------|
| messageId | Snowflake ID |
| conversationId | Chat |
| senderId | Sender |
| messageType | TEXT / IMAGE / VIDEO |
| content | Text OR Media URL |
| status | SENT / DELIVERED / READ |
| createdAt | Timestamp |

---

Example

| MessageId | Conversation | Sender | Content |
|------------|--------------|---------|----------|
|101|500|Alice|Hello|
|102|500|Bob|Hi|
|103|500|Charlie|Welcome|

---

# Why Store ConversationId?

Instead of

```
Alice

Bob
```

Every query becomes

```
SELECT *

WHERE conversationId=500

ORDER BY messageId
```

Simple.

---

# 5. Message Status

One-to-one chats

| MessageId | Status |
|------------|----------|
|101|SENT|

↓

|101|DELIVERED|

↓

|101|READ|

Simple.

---

# Group Chats

Things become interesting.

Should one message have

```
READ
```

or

```
DELIVERED
```

No.

Every recipient has a different state.

Therefore

two approaches.

---

# Fan-out on Write

Tables

Messages

+

MessageRecipient

---

Messages

| MessageId | Conversation | Sender |
|------------|-------------|---------|
|101|500|Alice|

---

MessageRecipient

| MessageId | User | Status |
|------------|------|----------|
|101|Bob|READ|
|101|Charlie|DELIVERED|
|101|David|UNDELIVERED|

Every recipient has an independent lifecycle.

---

Advantages

Easy

Fast Reads

---

Disadvantages

Huge storage.

---

# Storage Example

1000 Members

100 Messages

Rows

```
100

+

100 × 999

=

99,900
```

Huge.

---

# Fan-out on Read

Instead

Store only

Messages

---

Messages

| MessageId | Conversation | Sender |
|------------|-------------|---------|
|101|500|Alice|
|102|500|Bob|
|103|500|Charlie|

---

UserConversationState

| User | Conversation | LastReadMessage |
|------|---------------|----------------|
|Alice|500|103|
|Bob|500|102|
|Charlie|500|101|

Now Charlie opens the chat.

Query

```sql
SELECT *
FROM Messages
WHERE ConversationId=500
AND MessageId >101
ORDER BY MessageId;
```

Returns

```
102

103
```

Done.

---

Advantages

Tiny storage.

---

Disadvantages

More expensive reads.

---

# Why LastReadMessage Instead Of Unread List?

Bad

```
Bob needs

101

102

103

104
```

Good

```
Bob has read

100
```

Everything after

100

is unread.

Storage drops dramatically.

---

# Media Messages

Don't store

```
100 MB Video
```

Instead

Messages

| MessageId | Type | Content |
|------------|------|----------|
|101|VIDEO|https://cdn/video123.mp4|

Only metadata.

---

# Conversation Query

Opening a chat

```sql
SELECT *
FROM Messages
WHERE ConversationId=500
ORDER BY MessageId DESC
LIMIT 50;
```

This becomes the most common query.

---

# Indexes

## Messages

Primary Key

```
messageId
```

Secondary

```
(conversationId, messageId)
```

Supports

```
Latest 50 Messages
```

very efficiently.

---

ConversationMembers

Index

```
userId
```

Useful for

```
Show My Conversations
```

---

Users

Index

```
phone
```

for login.

---

# Database Partitioning

Suppose

```
20 Billion Messages

Per Day
```

One database isn't enough.

Partition

```
ConversationId
```

Example

```
Conversation 1

↓

Shard A

Conversation 2

↓

Shard B

Conversation 3

↓

Shard C
```

All messages of one conversation stay together.

Ordering preserved.

---

# Why Partition By Conversation?

Bad

```
Partition

Sender
```

Now

Alice→Bob

Bob→Alice

could land on different shards.

Conversation queries become expensive.

---

Good

```
ConversationId
```

All conversation history remains together.

---

# Soft Delete

Never physically delete immediately.

Instead

```
isDeleted=true
```

Allows

- Recovery
- Compliance
- Audit

Background jobs clean old data.

---

# Database Summary

| Table | Purpose |
|---------|----------|
| Users | User information |
| Conversations | Chat metadata |
| ConversationMembers | Users in conversation |
| Messages | Chat history |
| MessageRecipient | Fan-out on Write |
| UserConversationState | Fan-out on Read |

---

# Interview Questions

## Why Conversation Table?

Simplifies querying and routing.

---

## Why ConversationId instead of Sender/Receiver?

One query works for both direct and group chats.

---

## Why Snowflake IDs?

Globally unique IDs without a centralized sequence and roughly time ordered.

---

## Why MessageRecipient?

Tracks individual delivery/read status for each recipient in fan-out on write.

---

## Why UserConversationState?

Tracks only the user's progress (`LastReadMessage`) in fan-out on read, dramatically reducing storage.

---

## Why Store Media URL Instead of Media?

Databases are optimized for structured data.

Large binaries belong in object storage.

---

## Why Partition By ConversationId?

Keeps all messages for a conversation together, enabling efficient retrieval and preserving ordering.

---

# Interview Summary

> The database is the durable source of truth for the messaging system. Users and Conversations model participants and chats, while the Messages table stores the complete message history using globally unique Snowflake IDs. ConversationMembers maps users to conversations and supports both direct and group chats. For small groups, fan-out on write creates MessageRecipient records to track delivery and read status for every recipient. For very large groups, fan-out on read stores each message only once and maintains a lightweight UserConversationState table with the user's LastReadMessageId. Messages are partitioned by ConversationId to keep all conversation history together, media is stored externally in object storage with only URLs in the database, and appropriate indexes support efficient retrieval of recent conversation history.

# WhatsApp System Design

# Chapter 9 - Scaling the System

---

# Goal

A basic chat application works well for a few thousand users.

WhatsApp supports

- Billions of users
- Hundreds of millions of daily active users
- Millions of concurrent WebSocket connections
- Millions of messages per second

The system must scale horizontally at every layer.

The design principle is

```
Scale each component independently.
```

---

# High-Level Scaling

```
Clients

↓

Load Balancer

↓

Multiple WebSocket Servers

↓

Load Balancer

↓

Multiple Chat Services

↓

Redis Cluster

Kafka Cluster

Database Shards

Object Storage
```

Notice

There is **no single server**.

Everything scales horizontally.

---

# Scaling WebSocket Servers

WebSocket Servers are stateful.

Each server keeps

```java
ConcurrentHashMap<UserId, WebSocketSession>
```

Suppose one server can maintain

```
100,000 WebSocket connections
```

If

```
10 Million users
```

are online

We need

```
10,000,000

/

100,000

=

100 WebSocket Servers
```

A Load Balancer distributes new connections.

```
Clients

↓

LB

↓

WS1

WS2

WS3

...

WS100
```

---

# Why Can't We Use One Huge WebSocket Server?

Problems

- Memory limits
- CPU limits
- File descriptor limits
- Network bandwidth
- Single point of failure

Horizontal scaling is much safer.

---

# Scaling Chat Services

Unlike WebSocket Servers,

Chat Services are stateless.

```
Request

↓

Process

↓

Return
```

Nothing remains in memory.

Suppose

one Chat Service handles

```
20,000 requests/sec
```

If traffic grows

```
200,000 requests/sec
```

Simply add more instances.

```
LB

↓

Chat1

Chat2

Chat3

...

Chat10
```

Very easy.

---

# Why Separate Scaling?

Suppose

```
20 Million users online

but

very few messages
```

Need

```
Many WebSocket Servers

Few Chat Services
```

Another day

```
Same online users

Holiday

Huge message traffic
```

Need

```
Same WS Servers

More Chat Services
```

Independent scaling saves money.

---

# Scaling Redis

Redis stores

```
User

↓

WS Server
```

and

```
Presence
```

One Redis server cannot store everything.

Use

```
Redis Cluster

      /     |      \

Node1 Node2 Node3
```

Keys are automatically distributed.

Example

```
Alice

↓

Node1

Bob

↓

Node3
```

---

# Scaling Kafka

One Kafka broker is insufficient.

Kafka uses

```
Topic

↓

Partitions
```

Example

```
Message Topic

Partition 0

Partition 1

Partition 2

Partition 3
```

Producers write

Consumers read

Many consumers can process in parallel.

---

# Why ConversationId Partition?

Suppose

Alice sends

```
Hi
```

Then

```
How are you?
```

If both go to

```
Partition 2
```

Kafka guarantees

```
Hi

↓

How are you?
```

Ordering preserved.

---

# Scaling The Database

Messages grow forever.

Eventually

one database cannot store all messages.

Shard

by

```
ConversationId
```

Example

```
Conversation A

↓

Shard1

Conversation B

↓

Shard2

Conversation C

↓

Shard3
```

Benefits

- Smaller databases
- Parallel writes
- Parallel reads

---

# Why Not Shard By User?

Suppose

Alice

↓

Shard1

Bob

↓

Shard2

Conversation queries become

```
Read

Shard1

+

Shard2
```

Much slower.

ConversationId keeps related messages together.

---

# Scaling Object Storage

Media

- Images
- Videos
- Voice Notes

are stored in

Object Storage

Examples

- Amazon S3
- Google Cloud Storage
- Azure Blob Storage

These systems are already horizontally scalable.

No custom scaling required.

---

# CDN

Suppose

one viral video

gets

```
5 Million downloads
```

Without CDN

```
Everyone

↓

Object Storage
```

Expensive.

With CDN

```
Users

↓

Nearest CDN Edge

↓

Object Storage (only on cache miss)
```

Much lower latency.

---

# Scaling Notification Service

Notification Service is stateless.

```
Kafka

↓

Notification1

Notification2

Notification3
```

Consumer Group

More traffic?

Add more consumers.

---

# Scaling Analytics

Analytics

consumes

```
MessageCreated
```

Millions of events/sec.

Again

```
Kafka

↓

Analytics1

Analytics2

Analytics3

Analytics4
```

Horizontal scaling.

---

# Stateless vs Stateful

This is an important interview topic.

## Stateful

- WebSocket Servers
- Redis
- Database

Need careful management.

---

## Stateless

- Chat Service
- Notification Service
- Analytics Service
- Search Service

Simply add more instances.

---

# Capacity Planning Example

Suppose

```
100 Million Online Users
```

Assume

```
100,000 WebSocket Connections

per WS Server
```

Need

```
1000 WS Servers
```

Suppose

```
1 Million Messages/sec
```

One Chat Service

```
25,000 Requests/sec
```

Need

```
40 Chat Service instances
```

Notice

1000 WS Servers

40 Chat Services

Different scaling requirements.

---

# Failure Isolation

Suppose

Analytics crashes.

```
Messaging

↓

Still Works
```

Suppose

Notification crashes.

```
Chat

↓

Still Works
```

Kafka isolates failures.

---

# Design Principles

Every tier scales independently.

```
Connections

↓

WebSocket Layer

Business Logic

↓

Chat Layer

Storage

↓

Database

Routing

↓

Redis

Events

↓

Kafka
```

No single bottleneck.

---

# Interview Questions

## Why multiple WebSocket Servers?

Millions of persistent TCP connections cannot fit on one machine.

---

## Why multiple Chat Services?

Stateless business logic scales horizontally.

---

## Why separate WS Servers from Chat Services?

Connection count and message throughput grow independently.

---

## Why shard the database?

One database eventually becomes too large.

---

## Why partition Kafka?

Parallel processing while preserving ordering.

---

## Why Redis Cluster?

One Redis node cannot handle all routing and presence data.

---

# Interview Summary

> The architecture scales horizontally by treating every major component as an independent tier. WebSocket Servers scale based on the number of concurrent connections, while stateless Chat Services scale based on message throughput. Redis is deployed as a cluster to distribute presence and routing information, Kafka uses partitions and consumer groups for parallel event processing, and the Message Database is sharded by ConversationId so that all messages of a conversation remain together. Object storage and CDNs handle media at internet scale. This separation of concerns allows the platform to support hundreds of millions of users while avoiding single points of failure and enabling each layer to scale according to its own workload.

# WhatsApp System Design

# Chapter 10 - Fault Tolerance & Failure Handling

---

# Goal

Distributed systems fail.

Servers crash.

Networks fail.

Databases become unavailable.

Redis restarts.

Kafka brokers go down.

A production system must continue operating despite these failures.

This chapter discusses common failure scenarios and how to recover from them.

---

# High-Level Principle

Always identify

1. What failed?
2. What state was already persisted?
3. How do we recover?
4. Could duplicate work happen?
5. Could data be lost?

A good design minimizes

- Data loss
- Downtime
- Duplicate processing

---

# Failure Scenario 1

## WebSocket Server Crashes

Suppose

```
Bob

↓

WS3
```

WS3 suddenly crashes.

---

# What Happens?

The WebSocket sessions existed only in memory.

```
Bob

↓

Disconnected
```

Redis still contains

```
Bob -> WS3
```

which is now stale.

---

# Recovery

Bob's client automatically reconnects.

```
Bob

↓

Load Balancer

↓

WS5
```

Redis updates

```
Bob

↓

WS5
```

The stale entry is replaced.

---

# Are Messages Lost?

No.

Messages were already stored in

```
Message Database
```

After reconnecting,

Chat Service queries

```sql
SELECT *
FROM Messages
WHERE Receiver='Bob'
AND Status='UNDELIVERED';
```

Missed messages are delivered.

---

# Failure Scenario 2

## Chat Service Crashes Before Writing To Database

```
Alice

↓

Chat Service

↓

CRASH
```

before persistence.

---

Result

Message never entered the system.

Client receives

```
Failure

or

Timeout
```

Alice retries.

No inconsistency exists because nothing was stored.

---

# Failure Scenario 3

## Chat Service Crashes After Database Write But Before Kafka Publish

Flow

```
Persist

↓

CRASH

↓

Kafka Publish Never Happens
```

Problem

Message exists

Notification doesn't.

Analytics doesn't.

Search doesn't.

---

# Solution

Use the

## Transactional Outbox Pattern

Instead of

```
DB

↓

Kafka
```

Chat Service writes

```
Messages

+

Outbox Event
```

inside one database transaction.

---

Outbox

| EventId | Event | Published |
|----------|--------|-----------|
|500|MessageCreated|No|

---

A background publisher reads

```
Published = No
```

publishes to Kafka,

then marks

```
Published = Yes
```

No events are lost.

---

# Failure Scenario 4

## Kafka Broker Crashes

Kafka replicates partitions.

```
Broker1

Broker2

Broker3
```

One broker is leader.

Others are followers.

If leader crashes

```
Follower

↓

Leader
```

Clients continue.

---

# Failure Scenario 5

## Kafka Consumer Crashes

Suppose

Analytics consumed

```
Event1

Event2

Crash
```

Offset committed

after Event2.

When Analytics restarts

Kafka resumes

```
Event3
```

No events lost.

---

# Failure Scenario 6

## Redis Crashes

Redis stores

- Presence
- Routing
- Typing
- Heartbeats

Redis does NOT store messages.

Worst case

```
Nobody appears online
```

until reconnect.

Users reconnect.

Mappings rebuild automatically.

Chat history remains safe.

---

# Failure Scenario 7

## Database Crashes

This is serious.

The Message Database is the source of truth.

Production databases use

- Primary
- Replicas

```
Primary

↓

Replica1

Replica2
```

If Primary fails

Replica is promoted.

Writes continue.

---

# Failure Scenario 8

## Notification Service Crashes

Notification events accumulate in Kafka.

```
Kafka

↓

Notification

↓

Crash
```

No problem.

After restart,

Notification resumes from the last committed offset.

Push notifications continue.

---

# Failure Scenario 9

## Duplicate Kafka Events

Kafka default delivery is

```
At Least Once
```

Duplicate events are possible.

Example

```
MessageCreated

↓

Notification

↓

Crash

↓

Replay

↓

Notification Again
```

---

# Solution

Idempotency.

Store

```
Processed Event IDs
```

Example

```
Event123

Already Processed
```

Ignore duplicate.

---

# Failure Scenario 10

## Duplicate Message Submission

Alice presses

```
Send
```

Network timeout.

Alice presses again.

Two identical requests arrive.

---

# Solution

Client generates

```
ClientMessageId
```

Example

```
UUID
```

Chat Service checks

```
Already Exists?
```

If yes

Return previous response.

No duplicate message.

---

# Failure Scenario 11

## Lost Delivery Receipt

Bob receives

```
Hi
```

but

```
DELIVERED
```

never reaches server.

Database still says

```
SENT
```

---

Recovery

Client periodically resends

```
DELIVERED
```

until acknowledged.

Because updates are idempotent,

setting

```
Status = DELIVERED
```

multiple times is harmless.

---

# Failure Scenario 12

## Lost Read Receipt

Exactly the same.

READ updates are idempotent.

```
READ

↓

READ

↓

READ
```

No issue.

---

# Failure Scenario 13

## Object Storage Failure

Upload fails.

Message should NOT reference a missing file.

Correct flow

```
Upload Media

↓

Success

↓

Create Message
```

Never

```
Create Message

↓

Upload Later
```

---

# Failure Scenario 14

## Load Balancer Failure

Production deployments use

multiple

Load Balancers

or

managed cloud load balancing.

Avoid single points of failure.

---

# Idempotency

One of the most important interview topics.

Operations that should be idempotent

- Read Receipt
- Delivery Receipt
- Notification
- Media Upload Completion
- Message Send (using ClientMessageId)

Running them twice

produces the same final state.

---

# Exactly Once?

Can we guarantee exactly-once delivery?

Not across the entire distributed system.

Realistically

- Database guarantees persistence.
- Kafka provides at-least-once by default.
- Consumers use idempotency.
- The application achieves effectively-once behavior.

---

# Why The Database Is The Source Of Truth

Every failure scenario eventually comes back to one rule

```
Persist First
```

Everything else

- Notifications
- Analytics
- Search
- Push
- Presence

can be rebuilt.

Messages cannot.

---

# Common Recovery Mechanisms

| Failure | Recovery |
|----------|----------|
| WebSocket Server | Client reconnects |
| Chat Service | Retry request |
| Kafka Consumer | Resume from committed offset |
| Kafka Broker | Replica becomes leader |
| Redis | Rebuild from reconnects |
| Notification | Replay Kafka events |
| Duplicate Events | Idempotency |
| Lost Receipts | Retry safely |
| DB Primary Failure | Promote replica |

---

# Design Principles

A resilient messaging system follows these rules

- Persist before delivery
- Keep business logic stateless
- Make consumers idempotent
- Retry transient failures
- Replicate critical infrastructure
- Never trust in-memory state
- Design for replay

---

# Interview Questions

## What if Chat Service crashes after DB write?

Use the Transactional Outbox Pattern so events can still be published later.

---

## What if Kafka duplicates events?

Consumers must be idempotent.

---

## What if Redis is lost?

Presence is rebuilt from client reconnects.

Messages remain safe.

---

## What if WebSocket Server crashes?

Clients reconnect automatically.

Redis mapping is updated.

Undelivered messages are fetched from the database.

---

## Why Persist First?

Because persistence guarantees durability.

Everything else can be reconstructed.

---

# Interview Summary

> Distributed systems are designed with the assumption that failures are inevitable. WebSocket Servers recover through automatic client reconnections, Redis routing information is rebuilt from those reconnects, Kafka brokers replicate partitions for high availability, and consumers resume processing from committed offsets after failures. To avoid losing events between database writes and Kafka publishing, a Transactional Outbox Pattern is commonly used. Since Kafka provides at-least-once delivery by default, downstream services are designed to be idempotent. The Message Database remains the durable source of truth, while other components such as Redis, Kafka, Notification Services, and Search can always rebuild their state from persisted message history.

# WhatsApp System Design

# Chapter 11 - Media Sharing Deep Dive

---

# Goal

Text messages are small.

Media files are not.

Examples

- Images
- Videos
- Voice Notes
- Documents
- PDFs

These files can range from a few KB to several GB.

A messaging system must support media efficiently without slowing down the messaging pipeline.

---

# Functional Requirements

Support

- Image sharing
- Video sharing
- Audio sharing
- Documents
- Voice notes

Recipients should

- Receive messages quickly
- Download media efficiently
- Resume interrupted downloads

---

# Why Can't We Store Media In The Database?

Suppose Alice uploads

```
100 MB Video
```

If we store

```
Messages

MessageID

Sender

Receiver

Video (100 MB)
```

Problems

- Huge database
- Slow backups
- Slow replication
- Expensive storage
- Poor query performance

Databases are optimized for

```
Structured Data
```

not

```
Large Binary Objects
```

---

# Better Architecture

Store

Metadata

↓

Database

Actual File

↓

Object Storage

---

# High-Level Flow

```
Alice

↓

Upload Media

↓

Object Storage

↓

Returns Media URL

↓

Chat Service

↓

Store Metadata

↓

Message Database

↓

Notify Bob
```

Notice

The Chat Service never stores the actual video.

Only a reference.

---

# Messages Table

Instead of

```
100 MB Binary
```

store

| MessageId | Type | MediaURL |
|------------|------|----------------|
|101|VIDEO|https://cdn/video123.mp4|

The database remains small.

---

# Object Storage

Examples

- Amazon S3
- Google Cloud Storage
- Azure Blob Storage

Responsibilities

- Store files
- High durability
- Automatic replication
- Cheap storage
- Massive scalability

---

# Upload Flow

Suppose Alice selects

```
Vacation.mp4
```

Question

Should Alice upload through Chat Service?

---

# Option 1

```
Alice

↓

Chat Service

↓

Object Storage
```

Problems

Every byte flows through Chat Service.

Suppose

```
500 MB Video
```

Millions of uploads

↓

Chat Service becomes bottleneck.

---

# Better Solution

Use

Pre-Signed URLs.

---

# What Is A Pre-Signed URL?

The Chat Service requests

```
Object Storage

↓

Generate Upload URL
```

Example

```
https://storage/upload/abc123
```

Valid for

```
5 minutes
```

Only Alice can upload.

---

# Upload Flow

```
Alice

↓

Chat Service

↓

Request Upload URL

↓

Object Storage

↓

Pre-Signed URL

↓

Alice uploads directly

↓

Object Storage

↓

Upload Success

↓

Chat Service

↓

Store Media Metadata

↓

Notify Bob
```

Notice

The Chat Service never transfers the file.

Huge scalability improvement.

---

# Why Pre-Signed URLs?

Benefits

- Lower server load
- Faster uploads
- Better scalability
- Secure
- Temporary access

---

# Download Flow

Bob opens chat.

Messages table contains

```
Media URL
```

Question

Should Chat Service download the video?

No.

Instead

```
Bob

↓

CDN

↓

Object Storage
```

---

# Why CDN?

Suppose

```
5 Million Downloads
```

Without CDN

```
Everyone

↓

Object Storage
```

Very expensive.

High latency.

---

With CDN

```
Users

↓

Nearest CDN Edge

↓

Object Storage

(Cache Miss Only)
```

Popular media is cached near users.

Benefits

- Faster downloads
- Lower latency
- Reduced object storage traffic

---

# Complete Download Flow

```
Bob

↓

Open Chat

↓

Chat Service

↓

Messages

↓

Media URL

↓

CDN

↓

Object Storage
```

---

# Voice Notes

Voice Notes are treated exactly like videos.

Store

```
Media URL
```

Example

| MessageId | Type | URL |
|------------|------|----------------|
|201|VOICE|https://cdn/audio123.mp3|

---

# Image Thumbnails

Large images

```
8 MB
```

Don't download immediately.

Generate

Thumbnail

```
200 KB
```

Messages table

| MessageId | ThumbnailURL | OriginalURL |
|------------|---------------|--------------|
|101|thumb.jpg|image.jpg|

Chat loads

```
Thumbnail
```

User taps

↓

Original Image.

---

# Video Thumbnails

Same idea.

Generate

```
Preview Image
```

Display instantly.

Download video only when played.

---

# Media Compression

Client often compresses

- Images
- Videos

before upload.

Benefits

- Smaller uploads
- Faster transfer
- Less storage

---

# Chunked Upload

Suppose

```
2 GB Video
```

Uploading in one request is risky.

Instead

Split

```
Chunk1

Chunk2

Chunk3

...
```

If

Chunk5 fails,

resume

from Chunk5.

Not from beginning.

---

# Virus Scanning

Documents

may contain malware.

Flow

```
Upload

↓

Object Storage

↓

Virus Scan

↓

Available
```

Only after successful scan

should recipients access the file.

---

# Access Control

Object Storage should never expose public files.

Instead

Downloads use

Temporary Signed URLs.

Example

Valid for

```
2 minutes
```

After expiration

Download fails.

---

# Media Metadata

Messages table stores

| Column | Purpose |
|----------|-----------|
| Media URL | Download |
| Thumbnail URL | Preview |
| Media Type | Image/Video |
| File Size | UI |
| MIME Type | Rendering |

---

# Failure Scenario

Upload fails.

Should Chat Service create message?

No.

Correct sequence

```
Upload

↓

Success

↓

Store Metadata

↓

Create Message
```

Never

```
Create Message

↓

Upload Later
```

Otherwise

Bob receives

Broken Link.

---

# Why Object Storage?

Advantages

- Virtually unlimited storage
- Cheap
- Highly durable
- Automatic replication
- CDN integration

---

# Why Not Database?

Databases

- expensive
- slower
- replication overhead
- backup overhead

Better suited for

metadata only.

---

# Sequence Diagram

```
Alice

↓

Chat Service

↓

Generate Upload URL

↓

Object Storage

↓

Upload

↓

Store Media Metadata

↓

Database

↓

Kafka

↓

Bob

↓

CDN

↓

Object Storage
```

---

# Common Interview Questions

## Why Store URL Instead Of File?

Large binary files degrade database performance.

---

## Why Pre-Signed URL?

Allows clients to upload directly to object storage without routing data through Chat Service.

---

## Why CDN?

Reduces latency and origin traffic by caching media close to users.

---

## Why Thumbnail?

Avoid downloading large images and videos just to display chat history.

---

## Why Chunk Upload?

Supports resumable uploads for large files.

---

## Why Temporary Download URLs?

Improves security by preventing permanent public access to media.

---

# Interview Summary

> Media files are stored outside the Message Database in highly durable object storage such as Amazon S3. The Chat Service stores only metadata, including the media URL, type, and thumbnail information. Clients upload directly to object storage using pre-signed URLs, avoiding the Chat Service becoming a bandwidth bottleneck. Recipients download media through a CDN, which caches popular content close to users and reduces latency. Additional optimizations such as thumbnails, chunked uploads, compression, virus scanning, and temporary signed download URLs improve performance, reliability, and security while keeping the messaging pipeline lightweight.

# WhatsApp System Design

# Chapter 12 - Group Chat Deep Dive

---

# Goal

Supporting one-to-one messaging is relatively straightforward.

Group messaging introduces new challenges:

- One sender
- Hundreds or thousands of recipients
- Different delivery status per recipient
- Different read status per recipient
- Offline users
- Large communities
- Scalability

This chapter explains how production systems design group messaging.

---

# Functional Requirements

Support

- Create groups
- Add/remove members
- Send group messages
- Read receipts
- Delivery receipts
- Offline members
- Large communities

---

# High-Level Architecture

```
                    Alice

                      │

                WebSocket

                      │

                 Chat Service

                      │

               Store Message

                      │

                 Message DB

                      │

                   Kafka

                      │

              Fan-out Service

         ┌────────┼────────┬────────┐

         ▼        ▼        ▼        ▼

       Bob     Charlie    David    Emma
```

---

# Database Design

Groups

| GroupId | Name |
|----------|------|
|100|Family|

---

GroupMembers

| GroupId | User |
|----------|------|
|100|Alice|
|100|Bob|
|100|Charlie|
|100|David|

Whenever Alice sends a message,

the system first determines

```
Who belongs to this group?
```

using

```sql
SELECT UserId
FROM GroupMembers
WHERE GroupId=100;
```

---

# Message Storage

Messages table

| MessageId | GroupId | Sender | Message |
|------------|---------|---------|----------|
|101|100|Alice|Happy New Year|

Notice

The message itself is stored only once.

---

# Problem

Suppose

```
1000 Members
```

How should delivery work?

There are two approaches.

---

# Option 1 - Fan-out On Write

Immediately create delivery records.

```
Message

↓

Bob

Charlie

David

Emma

↓

Recipient Records
```

---

# MessageRecipient Table

| MessageId | Recipient | Status |
|------------|-----------|----------|
|101|Bob|READ|
|101|Charlie|DELIVERED|
|101|David|UNDELIVERED|

Every member has an independent status.

---

# Delivery Flow

Alice sends

↓

Store message

↓

Read GroupMembers

↓

Create MessageRecipient rows

↓

Publish Kafka

↓

Fan-out Service

↓

Deliver to online users

---

# Advantages

- Very fast reads
- Easy unread calculation
- Easy delivery tracking
- Easy read receipts

---

# Disadvantages

Storage grows rapidly.

Example

```
1000 Members

100 Messages
```

Recipient rows

```
100 × 999

=

99,900
```

---

# Option 2 - Fan-out On Read

Instead of creating recipient rows,

store only

Messages

---

Messages

| MessageId | GroupId | Sender | Message |
|------------|----------|--------|----------|
|101|100|Alice|Hello|
|102|100|Bob|Hi|
|103|100|Charlie|Welcome|

---

UserGroupState

| User | Group | LastReadMessageId |
|------|--------|------------------|
|Alice|100|103|
|Bob|100|102|
|Charlie|100|101|
|David|100|103|

---

# Reading Messages

Charlie opens the group.

Execute

```sql
SELECT *
FROM Messages
WHERE GroupId=100
AND MessageId >101
ORDER BY MessageId;
```

Returns

```
102

103
```

Update

```
LastReadMessageId

↓

103
```

Done.

---

# Why LastReadMessage?

Bad

```
Bob needs

102

103

104

105
```

Good

```
Bob has read

101
```

Everything newer

↓

Unread.

Much smaller storage.

---

# Storage Comparison

Suppose

```
1000 Members

100 Messages
```

Fan-out on Write

```
Messages

100

Recipient Rows

99,900

Total

100,000
```

Fan-out on Read

```
Messages

100

UserGroupState

1000

Total

1100
```

Huge reduction.

---

# Which Should We Use?

Small groups

```
Family

Friends

Office Teams
```

Fan-out on Write

Better user experience.

---

Large communities

```
100,000 Members

500,000 Members

1 Million Members
```

Fan-out on Read

Storage remains manageable.

---

# Fan-out Service

Question

Should Chat Service deliver

```
1000 Messages
```

itself?

No.

Instead

```
Store

↓

Kafka

↓

Fan-out Service
```

The Fan-out Service

- Reads group members
- Checks Redis
- Delivers to online users
- Leaves offline users in the database

This keeps Chat Service lightweight.

---

# Online Member

Redis

```
Bob

↓

WS2
```

Fan-out Service

↓

WS2

↓

Bob

Immediate delivery.

---

# Offline Member

Redis

```
Charlie

↓

OFFLINE
```

No WebSocket delivery.

Nothing else required.

The message is already stored.

When Charlie reconnects

↓

Fetch unread messages.

---

# Read Receipts

Suppose

Bob reads

Message101.

Fan-out on Write

Update

```
MessageRecipient

↓

READ
```

Fan-out on Read

Update

```
UserGroupState

↓

LastReadMessageId
```

Both achieve the same goal.

---

# Ordering

Messages are ordered using

Snowflake IDs.

Kafka partitions by

```
ConversationId (GroupId)
```

All group messages remain in order.

---

# Scaling

Small groups

↓

Fan-out on Write

Large groups

↓

Fan-out on Read

Fan-out Service

↓

Kafka Consumer Group

↓

Multiple Fan-out Workers

```
Kafka

↓

Fanout1

Fanout2

Fanout3
```

Horizontal scaling.

---

# Database Summary

## Fan-out on Write

```
Groups

↓

GroupMembers

↓

Messages

↓

MessageRecipient
```

---

## Fan-out on Read

```
Groups

↓

GroupMembers

↓

Messages

+

UserGroupState
```

---

# Design Principles

Store the message only once.

Never duplicate message text.

Only duplicate metadata when required.

Separate

```
Storage

↓

Delivery
```

Storage happens immediately.

Delivery can happen later.

---

# Interview Questions

## Why store the message once?

Avoid duplicating large message content.

Only metadata is duplicated when necessary.

---

## Why Fan-out Service?

Chat Service remains lightweight and stateless.

Fan-out scales independently.

---

## Why Fan-out on Write?

Fast reads and easy status tracking.

Ideal for small and medium groups.

---

## Why Fan-out on Read?

Avoids massive write amplification and storage costs.

Ideal for very large communities.

---

## Why GroupMembers table?

Maps users to groups.

Used to determine recipients whenever a group message is sent.

---

## Why UserGroupState?

Tracks each user's progress through the conversation rather than storing one row per recipient per message.

---

# Interview Summary

> Group messaging introduces the challenge of delivering one message to many recipients while tracking different delivery and read states. The message itself is stored only once in the Messages table, while group membership is maintained in the GroupMembers table. For small and medium groups, a Fan-out Service consumes Kafka events and creates per-recipient delivery records (fan-out on write), providing fast reads and simple status tracking. For very large communities, the system stores each message only once and maintains a lightweight UserGroupState table containing each user's LastReadMessageId (fan-out on read). Online users receive messages immediately through WebSocket routing, while offline users retrieve unread messages from the database after reconnecting. This hybrid approach balances latency, storage efficiency, and scalability.

# WhatsApp System Design

# Chapter 13 - Complete Interview Walkthrough (15 Minutes)

---

# Interview Strategy

A common mistake candidates make is explaining every component one by one.

Don't do this.

Instead,

tell the story of **one message**.

Imagine Alice sends

```
Hi Bob
```

Everything else naturally appears.

---

# Minute 1

## Clarify Requirements

I'd first clarify the requirements with the interviewer.

Functional Requirements

- One-to-one messaging
- Group messaging
- Offline delivery
- Read receipts
- Delivery receipts
- Media sharing

Non-functional Requirements

- Low latency
- High availability
- Scalability
- Durability
- Message ordering

I'd assume hundreds of millions of users and millions of concurrent connections.

---

# Minute 2

## APIs

The major APIs are

```
POST /messages

GET /conversations/{id}

POST /groups

POST /media
```

Real-time communication happens over WebSockets rather than REST.

---

# Minute 3

## High Level Architecture

```
Clients

↓

Load Balancer

↓

WebSocket Servers

↓

Chat Services

↓

Message DB
Redis
Kafka
Object Storage
Notification Service
```

Explain each component briefly.

### WebSocket Server

Maintains persistent client connections.

### Chat Service

Contains messaging business logic.

### Database

Stores all messages.

### Redis

Stores connection routing and online presence.

### Kafka

Publishes events to downstream services.

### Object Storage

Stores media.

---

# Minute 4-7

## Walk Through One Message

Alice sends

```
Hi Bob
```

### Step 1

Alice already has an existing WebSocket connection.

```
Alice

↓

WS1
```

---

### Step 2

WS1 forwards the request to Chat Service.

WS Server contains

no business logic.

---

### Step 3

Chat Service

- authenticates
- validates
- generates Snowflake ID

---

### Step 4

Persist message

```
Messages
```

Status

```
SENT
```

I always persist before delivery because the database is my source of truth.

---

### Step 5

Publish

```
MessageCreated
```

to Kafka.

Notification

Analytics

Search

Audit

all consume asynchronously.

---

### Step 6

Find Bob.

Redis

```
Bob

↓

WS3
```

---

### Step 7

Forward

```
WS3

↓

Bob
```

Bob receives

```
Hi Bob
```

---

### Step 8

Bob sends

```
DELIVERED
```

Database becomes

```
DELIVERED
```

Alice sees

```
✓✓
```

---

### Step 9

Bob opens chat.

```
READ
```

Database

```
READ
```

Alice sees

```
Blue ✓✓
```

---

# Minute 8

## Offline Users

If Bob is offline,

Redis says

```
OFFLINE
```

The Chat Service still stores the message.

Kafka triggers Notification Service.

Push notification is sent using

- APNS
- FCM

When Bob reconnects,

the Chat Service queries

```
UNDELIVERED
```

messages.

---

# Minute 9

## Media

Large media never goes into the database.

Flow

```
Client

↓

Pre-Signed URL

↓

Object Storage

↓

Store URL

↓

Message DB
```

Recipients download via CDN.

---

# Minute 10

## Group Chat

Small Groups

Fan-out on Write.

```
Message

↓

Recipients
```

Large Communities

Fan-out on Read.

```
Store Once

↓

LastReadMessageId
```

Storage remains manageable.

---

# Minute 11

## Scaling

WebSocket Servers

Stateful.

Scale based on

```
Connections
```

Chat Services

Stateless.

Scale based on

```
Requests/sec
```

Redis

Cluster.

Kafka

Partitions.

Database

Shard by

```
ConversationId
```

---

# Minute 12

## Ordering

Messages use

Snowflake IDs.

Kafka partitions by

```
ConversationId
```

Ordering is preserved within a conversation.

---

# Minute 13

## Failure Handling

WebSocket crash

↓

Reconnect

Redis rebuilds.

Chat Service crash

↓

Retry.

Kafka consumer crash

↓

Resume from committed offset.

Redis crash

↓

Presence rebuilt.

Database

↓

Source of Truth.

---

# Minute 14

## Tradeoffs

Explain

Why Redis?

Fast routing.

Why Kafka?

Decouple downstream services.

Why Object Storage?

Large files.

Why Stateless Chat Service?

Horizontal scalability.

Why separate WS Server?

Independent scaling.

---

# Minute 15

## Final Summary

"My design separates connection management from business logic.

Persistent WebSocket connections provide low-latency communication.

Chat Services remain stateless and persist every message before delivery.

Redis performs O(1) routing of users to WebSocket Servers.

Kafka decouples notifications, analytics, search, and audit processing.

Media is stored separately in object storage.

Small groups use fan-out on write while very large communities use fan-out on read.

The database remains the durable source of truth, while Redis and Kafka optimize routing and asynchronous processing.

Each layer scales independently, allowing the system to support hundreds of millions of users."

---

# Whiteboard Diagram

```
                          Clients

                             │

                      WebSocket

                             │

                      Load Balancer

                             │

            ┌────────────────┼────────────────┐

            ▼                ▼                ▼

          WS1              WS2              WS3

            │                │                │

            └────────────────┼────────────────┘

                             │

                      Load Balancer

                             │

        ┌────────────────────┼────────────────────┐

        ▼                    ▼                    ▼

     Chat S1             Chat S2             Chat S3

        │                    │                    │

        └──────────────┬─────┴──────────────┬─────┘

                       │                    │

      ┌────────────────┼────────────┬──────┼──────────────┐

      ▼                ▼            ▼      ▼              ▼

 Message DB        Redis         Kafka   Object Store   Group DB

                                     │

                      ┌──────────────┼──────────────┐

                      ▼              ▼              ▼

                Notification     Analytics      Search

```

---

# Tips That Impress Interviewers

✅ Start with requirements.

✅ Explain one message end-to-end.

✅ Explain *why* each component exists.

✅ Mention tradeoffs instead of claiming one perfect solution.

✅ Keep the Message Database as the source of truth.

✅ Separate stateful WebSocket Servers from stateless Chat Services.

✅ Use Redis only for transient routing/presence.

✅ Use Kafka only for asynchronous processing.

---

# Common Mistakes

❌ Storing media in the database.

❌ Making Chat Service stateful.

❌ Using polling instead of WebSockets.

❌ Querying the database to locate online users.

❌ Letting Chat Service call Analytics/Search synchronously.

❌ Forgetting offline users.

❌ Ignoring ordering within a conversation.

❌ Mixing permanent state (DB) with temporary state (Redis).

---

# Final Interview Advice

Don't try to memorize the architecture.

Memorize **the journey of one message**.

Every major component naturally appears during that journey:

- WebSocket → Connection
- Chat Service → Business Logic
- Database → Durability
- Redis → Routing
- Kafka → Async Processing
- Object Storage → Media
- Notification Service → Offline Delivery
- Fan-out → Group Messaging
- Snowflake IDs → Ordering
- Read/Delivery Receipts → Message Lifecycle

If you can confidently explain the life of a single message from sender to receiver, you've effectively explained the entire WhatsApp architecture.

# WhatsApp System Design

# Chapter 14 - Common Interview Questions & Trade-offs

---

# 1. Why WebSockets instead of HTTP?

HTTP is request-response.

For every message,

```
Client

↓

HTTP Request

↓

Server

↓

HTTP Response

↓

Connection Closed
```

A chat application requires persistent bidirectional communication.

WebSockets establish one connection that stays open.

```
Client

⇅

Server
```

Benefits

- Lower latency
- Lower network overhead
- Real-time communication

---

# 2. Why not Polling?

Polling repeatedly asks

```
Any new message?
```

Example

Every second

↓

60 requests

↓

59 useless

Waste of

- CPU
- Network
- Database

---

# 3. Why not Server Sent Events (SSE)?

SSE only supports

```
Server

↓

Client
```

Chat requires

```
Client

⇅

Server
```

Only WebSockets support true bidirectional communication.

---

# 4. Why separate WebSocket Server and Chat Service?

WebSocket Servers manage

- TCP connections
- Heartbeats
- Session objects

Chat Service performs

- Validation
- Persistence
- Routing
- Business logic

Connection count and request throughput scale differently.

---

# 5. Why is Chat Service stateless?

Every request is independent.

```
Receive Request

↓

Process

↓

Return
```

No request-specific data remains in memory.

Stateless services

- Scale horizontally
- Recover easily
- Simplify load balancing

---

# 6. Why use Redis?

Chat Service needs

```
Where is Bob?
```

Millions of times per second.

Redis stores

```
Bob

↓

WS3
```

O(1) lookup.

Databases are slower and optimized for durability.

---

# 7. Why doesn't Redis store socket objects?

Socket objects exist only inside the WebSocket Server process.

Redis stores only

```
Bob

↓

WS3
```

WS3 stores

```java
ConcurrentHashMap<UserId, WebSocketSession>
```

---

# 8. Why Kafka?

Without Kafka

```
Chat Service

↓

Notification

↓

Analytics

↓

Search

↓

Audit
```

Every downstream dependency increases latency.

Kafka decouples producers from consumers.

---

# 9. Why publish after database write?

Correct order

```
Persist

↓

Publish Event
```

Never

```
Publish

↓

Persist
```

Otherwise consumers process events for messages that were never saved.

---

# 10. Why is the database the source of truth?

Messages are business data.

Redis

↓

Temporary

Kafka

↓

Event Stream

Database

↓

Permanent

Everything can be rebuilt from the database.

---

# 11. Why store media outside the database?

Databases are optimized for structured data.

Large binaries

- Slow backups
- Slow replication
- Higher storage costs

Object Storage is built for this use case.

---

# 12. Why use Pre-Signed URLs?

Without them

```
Client

↓

Chat Service

↓

S3
```

Chat Service transfers gigabytes of data.

With pre-signed URLs

```
Client

↓

S3
```

Chat Service only coordinates.

---

# 13. Why CDN?

Millions of downloads

↓

CDN caches media near users

↓

Lower latency

↓

Lower storage traffic

---

# 14. Why ConversationId instead of Sender/Receiver?

One query works for

- Direct Chat
- Group Chat

```
SELECT *

WHERE conversationId=123
```

Much simpler than

```
sender=A

receiver=B

OR

sender=B

receiver=A
```

---

# 15. Why Snowflake IDs?

Advantages

- Globally unique
- Roughly time ordered
- No database bottleneck
- Scalable ID generation

---

# 16. Why partition Kafka by ConversationId?

Kafka guarantees ordering

within a partition.

All messages for

```
Conversation123
```

go to one partition.

Ordering preserved.

---

# 17. Why shard database by ConversationId?

All messages remain together.

Conversation queries stay local.

Avoids cross-shard joins.

---

# 18. Why not shard by User?

Alice

↓

Shard1

Bob

↓

Shard2

Conversation

↓

Multiple shards

Poor performance.

---

# 19. Why persist before delivery?

Suppose

Deliver

↓

Database Crash

Bob saw message

↓

Message lost forever

Persisting first guarantees durability.

---

# 20. Why Redis instead of Database for Presence?

Presence changes constantly.

Users

- Connect
- Disconnect
- Reconnect

Millions of updates every minute.

Redis is optimized for this.

---

# 21. Why Heartbeats?

Network failures don't always close TCP connections.

Heartbeat

```
PING

↓

PONG
```

helps detect dead connections.

---

# 22. Why Fan-out on Write?

Advantages

- Fast reads
- Easy delivery tracking
- Easy read receipts

Best for

Small groups.

---

# 23. Why Fan-out on Read?

Advantages

- Tiny storage
- Small write amplification

Best for

Large communities.

---

# 24. Why store LastReadMessageId?

Instead of storing

Unread Messages

Store

```
Last Read

↓

105
```

Everything after

105

is unread.

Very efficient.

---

# 25. Why Object Storage?

Optimized for

- Large files
- Cheap storage
- Massive scalability
- High durability

---

# 26. What happens if WebSocket Server crashes?

Client reconnects.

Redis mapping updates.

Undelivered messages are fetched from the database.

---

# 27. What happens if Redis crashes?

Presence is temporarily lost.

Users reconnect.

Mappings rebuild.

Messages remain safe.

---

# 28. What happens if Kafka crashes?

Kafka replicates partitions.

Leader fails

↓

Follower becomes leader.

---

# 29. What happens if Chat Service crashes after DB write?

Use

```
Transactional Outbox Pattern
```

Outbox Publisher later publishes to Kafka.

No events lost.

---

# 30. What delivery guarantee does Kafka provide?

Default

```
At Least Once
```

Duplicates are possible.

Consumers should be idempotent.

---

# 31. How do you prevent duplicate messages?

Client generates

```
ClientMessageId (UUID)
```

Server checks whether it has already processed that ID.

If yes,

return the previous result.

---

# 32. How do you preserve ordering?

- Snowflake IDs
- Kafka partition by ConversationId
- Database queries ordered by MessageId

---

# 33. Why not use Kafka as the database?

Kafka stores an event log.

It is not optimized for

- random queries
- conversation history
- indexes
- pagination

Use Kafka for event streaming, not as the primary data store.

---

# 34. Why not query Redis for message history?

Redis is an in-memory cache.

Message history is durable business data.

Store it in the database.

---

# 35. What is the most important design principle?

Always remember

```
Persist

↓

Then Deliver
```

Everything else

- Notifications
- Analytics
- Search
- Presence

can be rebuilt.

Messages cannot.

---

# Final Interview Cheat Sheet

| Question | Short Answer |
|----------|--------------|
| Why WebSockets? | Persistent bidirectional communication |
| Why Redis? | Fast routing & presence |
| Why Kafka? | Decouple asynchronous work |
| Why DB first? | Durability |
| Why Object Storage? | Large media |
| Why CDN? | Fast downloads |
| Why Snowflake? | Unique ordered IDs |
| Why ConversationId? | Efficient queries & ordering |
| Why Chat Service stateless? | Horizontal scaling |
| Why separate WS Server? | Independent scaling |
| Why Fan-out on Write? | Fast reads |
| Why Fan-out on Read? | Lower storage |
| Why LastReadMessageId? | Efficient unread tracking |
| Why Transactional Outbox? | Reliable DB → Kafka publishing |
| Why Idempotency? | Safe retries and duplicate handling |

---

# Final Advice

A senior system design interview is not about naming technologies.

Interviewers care about **trade-offs**.

For every major component, be prepared to answer:

- Why did you choose it?
- What problem does it solve?
- What are the alternatives?
- What are the trade-offs?
- What happens when it fails?

If you can confidently answer those five questions for every component in your architecture, you'll handle the vast majority of follow-up questions in a senior backend system design interview.


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
	
	
# Design Twitter / X - Part 1
## Requirements, Capacity Estimation, High-Level Architecture, Core Data Services

---

# 1. Problem Statement

Design a Twitter-like social networking system where users can:

- Create short text posts (tweets)
- Upload photos and videos
- Follow and unfollow users
- View a personalized home timeline
- Like, comment, and share tweets
- Search tweets
- View trending topics
- Receive notifications and recommendations

The system must support hundreds of millions of users with low latency and high availability.

---

# 2. Requirement Clarification

## Functional Requirements (FR)

### User Features

- User registration and login
- User profile management
- Follow/unfollow other users

---

### Tweet Features

Users should be able to:

- Create tweets
- Delete tweets
- View tweets

Tweets can contain:

- Text
- Images
- Videos

---

### Timeline Features

The system should generate:

- Home timeline
  - Tweets from users I follow
  - Ordered by relevance and recency

- User timeline
  - Posts created by a specific user

---

### Social Features

Users should be able to:

- Like a tweet
- Comment on a tweet
- Retweet/share a tweet

---

### Extended Features

- Search tweets
- Trending topics
- Personalized recommendations
- Notifications
- Re-engagement for inactive users

---

# 3. Non-Functional Requirements (NFR)

## High Availability

Twitter is a global consumer platform.

The system should continue operating even if individual components fail.

---

## Low Latency

Timeline rendering should be very fast.

Target:

- Timeline read latency: ~100-200ms

---

## Massive Scale

Example assumptions:

- 1 Billion registered users
- 200 Million daily active users
- 100 Million tweets created every day

---

## Read Heavy System

Twitter has far more reads than writes.

Users read timelines many times per day but create relatively few tweets.

Therefore, the architecture is optimized for:

- Fast reads
- Caching
- Pre-computation where possible

---

## Consistency

The system can tolerate eventual consistency.

Examples:

Acceptable:
- Like count delayed by a few seconds
- A new tweet appearing slightly late

Not acceptable:
- Losing a user's tweet

Availability and scalability are prioritized over strong consistency.

---

# 4. Capacity Estimation

## Tweet Storage

Assumptions:

- 100 million tweets/day
- Tweet length = 140 characters
- Unicode = 2 bytes per character
- Metadata = ~30 bytes

Size per tweet:

140 * 2 + 30 = 310 bytes

Daily storage:

100,000,000 * 310 bytes

≈ 30 GB/day

---

## Media Storage

Assume:

- 20% of tweets contain images
- 10% contain videos

Media dominates storage.

Approximation:

- Images: ~4 TB/day
- Videos: ~20 TB/day

Total media storage:

≈ 24 TB/day

---

## Read Traffic Estimation

Assumptions:

- 200M DAU
- Each user visits timeline 2 times/day
- Visits 5 other user profiles/day
- Each page displays 20 tweets

Requests/day:

200M * (2 + 5) * 20

≈ 28 Billion tweet views/day

QPS:

28 Billion / 86400

≈ 325K reads/second

This demonstrates why the system is heavily optimized for reads.

---

# 5. High-Level Architecture

                      Client
                         |
                    Load Balancer
                         |
                    API Gateway
                         |
        ------------------------------------------------
        |                 |               |
        |                 |               |
    User Service     Tweet Service   Timeline Service
        |                 |               |
      MySQL          Cassandra        Redis Cache
                          |
                       Kafka
                          |
       ------------------------------------------------
       |                    |                         |
 Feed Processor      Search Pipeline            Analytics
       |                    |                         |
 Cassandra         Elasticsearch             Hadoop/Spark


---

# 6. User Service

## Responsibilities

Stores:

- User profile
- Authentication information
- Account metadata

Example:

User:
{
    userId,
    name,
    email,
    preferences
}

---

## Database Choice

MySQL Cluster

Reason:

- User data is relational
- Requires transactional consistency
- Supports indexing and replication

---

# 7. Social Graph Service

## Purpose

Stores relationships:

User A follows User B


Example:

User 1001 follows:

[
  2002,
  3003,
  5005
]

---

## Data Model

FollowerId → FollowingId

or

UserId → List of Followers


---

## Database Choices

Possible options:

- Graph database
- Cassandra
- Sharded MySQL

Requirements:

- Very high read throughput
- Fast lookup:

"Who does this user follow?"

This information is heavily used during timeline generation.

---

# 8. Tweet Service

## Responsibilities

Handles:

- Create tweets
- Delete tweets
- Fetch user tweets

---

## Database Choice

Cassandra

Reasons:

- Extremely high write throughput
- Horizontal scaling
- Distributed architecture
- High availability
- Eventual consistency is acceptable

---

## Partition Strategy

Partition Key:

UserId

Clustering Key:

Timestamp

Example:

User 123

10:01 -> Tweet A

10:05 -> Tweet B

10:15 -> Tweet C


Allows efficient retrieval of a user's recent tweets.

---

# 9. Media Service

Tweets containing images/videos are stored separately.

Why?

Media files are very large and would make tweet storage inefficient.

---

## Flow


User Upload
     |
 Asset Service
     |
 Object Storage (S3)
     |
 CDN
     |
 End Users


---

## CDN Benefits

Frequently accessed images/videos remain close to users.

Example:

A celebrity photo may be cached globally.

Old content can be evicted.

If an old image becomes popular again:

CDN miss
    |
Fetch from S3
    |
Store in CDN cache

---

# 10. Event-Driven Architecture

Tweet creation generates an event:

Tweet Created Event
          |
        Kafka
          |
 -------------------------------------------------
 |                    |                          |
Timeline          Search                   Analytics
Processor         Indexing                 Pipeline


---

## Why Kafka?

A background worker could also perform asynchronous processing.

Kafka is preferred because it provides:

- Durable event storage
- Replayability
- Multiple independent consumers
- Backpressure handling
- Consumer-specific offsets
- Decoupling between producer and consumers


Example:

Today:
- Timeline Service
- Search Service

Tomorrow:
- ML Recommendation Service

A new consumer can replay historical tweet events.

---

# 11. Key L6 Trade-Off Talking Points

## Why Cassandra instead of MySQL?

Because tweets require:

- Massive write throughput
- Horizontal scaling
- Global availability

The trade-off is eventual consistency.

---

## Why Redis?

Twitter is read-heavy.

Caching prevents repeatedly generating the same timelines and reduces database load.

---

## Why not store media in Cassandra?

Large binary objects increase storage and replication costs.

Object storage like S3 is optimized for large files.

---

## Why accept eventual consistency?

User experience is more important than strict consistency.

A like count being delayed by a second is acceptable.

A tweet being permanently lost is not.

---

# Part 1 Summary

Core Components:

- User Service → MySQL
- Graph Service → Graph DB / Cassandra / Sharded MySQL
- Tweet Service → Cassandra
- Media Service → S3 + CDN
- Timeline Cache → Redis
- Event Backbone → Kafka
- Search → Elasticsearch
- Analytics → Spark/Hadoop

The foundation of the system is designed around one major principle:

Twitter is a read-heavy system, so we optimize reads using caching, pre-computation, and asynchronous event processing.

# Design Twitter / X - Part 2
# Timeline Generation, Fan-out Strategy, Celebrity Problem, Live Users

---

# 1. The Core Challenge

Twitter is a read-heavy system.

A user opens the home timeline and expects to immediately see:

- Tweets from people they follow
- Recent tweets
- Relevant tweets

The main question is:

When a user creates a tweet, how should it reach their followers?

There are three approaches:

1. Pull Model (Fan-out on Read)
2. Push Model (Fan-out on Write)
3. Hybrid Model

---

# 2. Pull Model (Fan-out on Read)

## Idea

Do not distribute the tweet when it is created.

Instead, when a user opens their timeline, generate the feed at that time.

Example:

User A follows:

- User B
- User C
- User D


When User A opens Twitter:


Timeline Service
       |
       |
Graph Service
(Get users I follow)
       |
       |
Tweet Service
(Get recent tweets from B,C,D)
       |
       |
Merge + Sort by timestamp/relevance
       |
       |
Return timeline


---

## Advantages

- Very cheap writes
- No unnecessary work for inactive users
- Easy to handle users with millions of followers

---

## Problems

### Higher read latency

Every timeline request requires:

- Fetching follower graph
- Fetching tweets
- Merging timelines
- Ranking results

This increases response time.

---

### Empty or repeated refreshes

Many users continuously refresh their feed.

Even if there are no new tweets:

- The server still has to compute the timeline again.
- This creates unnecessary load.

This was one of the issues identified in the notes.

---

# 3. Push Model (Fan-out on Write)

## Idea

When a user creates a tweet, immediately push it to every follower's timeline.

Example:

User B creates a tweet


Tweet Service
       |
      Kafka
       |
Feed Processor
       |
Find followers using Graph Service
       |
Write tweet ID into each follower's timeline cache


Now when a follower opens Twitter:

Timeline Service
       |
      Redis
       |
Return cached timeline


---

## Advantages

Very fast reads.

Timeline generation has already happened.

Redis can directly return:

User Timeline Cache:

User A:

Tweet 101
Tweet 102
Tweet 103


---

## Problems

### Celebrity / Hot Key Problem

Imagine a celebrity with:

100 million followers.


One tweet causes:

100 million writes.

This creates:

- Huge Kafka traffic
- Massive fan-out load
- Large Redis writes
- Backlogs in feed processors


This is the famous "hot celebrity" problem.

---

# 4. Hybrid Approach (Used by Twitter)

The best approach is to combine both models.

---

## Normal Users

Users with a small or medium number of followers:

Use:

Fan-out on Write


Flow:


Tweet Created
      |
    Kafka
      |
Feed Processor
      |
Graph Service
      |
Push tweet IDs to followers' Redis timelines


Why?

Because most users have a reasonable number of followers.

The write amplification is manageable.

The benefit is very low read latency.

---

## Celebrity Users

For users with millions of followers:

Use:

Fan-out on Read


Do NOT push their tweets to every follower.

Instead:

When a follower opens the timeline:

Timeline Service
       |
Get celebrity list from Graph Service
       |
Get latest celebrity tweets from Tweet Service
       |
Merge with user's normal cached timeline
       |
Return result


This avoids millions of writes.

---

# 5. Handling Active vs Passive Users

Another optimization from the notes:

Not all users are equally important.

---

## Active Users

Users currently online:

We can provide near real-time updates.

Possible approaches:

- WebSocket connection
- Long polling


Flow:

New Tweet
     |
 Kafka
     |
 Live User Service
     |
 WebSocket
     |
 Push notification to active client


---

## Passive Users

Users who have not logged in recently.

Do not spend resources continuously updating their timelines.

Their timelines can be generated when they return.

---

# 6. Timeline Cache using Redis

Redis stores recent timeline data.

Example:

Key:

UserId


Value:

[
 TweetId1,
 TweetId2,
 TweetId3
]


Benefits:

- Extremely fast reads
- Reduces Cassandra queries
- Reduces expensive timeline computation


---

# 7. Why Redis TTL?

A timeline changes frequently for active users.

Older timelines become less valuable.

We can:

- Keep recent timeline entries in Redis
- Apply TTL
- Evict old data automatically


This saves memory.

---

# 8. Timeline Archival

Old timeline data does not need to remain in Redis forever.

An archival service can store older timeline data.

Example:

Redis:
- Last few days of feed

Long-term storage:
- Cassandra aggregated timeline tables


When a user scrolls far back:

Timeline Service
       |
Archival Service
       |
Cassandra
       |
Return old posts


---

# 9. Why Store Tweet IDs Instead of Full Tweets in Redis?

Store:

User123 Timeline:

[
  Tweet101,
  Tweet202,
  Tweet303
]


Instead of storing full tweet objects.

Reasons:

- Smaller cache footprint
- Avoid duplicate data
- Tweet details can be fetched separately

---

# 10. L6 Trade-off Discussion

## Why not always use Push?

Because of celebrities.

A user with 100M followers would cause enormous write amplification.

---

## Why not always use Pull?

Because reads dominate writes.

Generating timelines repeatedly causes high latency and compute cost.

---

## Why Hybrid?

It balances:

- Low read latency
- Controlled write amplification
- Efficient handling of celebrities

---

# Key Takeaways

The most important Twitter design decision is timeline generation.

Normal users:
    Fan-out on Write

Celebrity users:
    Fan-out on Read

Active users:
    Real-time updates using WebSockets

Passive users:
    Generate timeline on demand

Recent feeds:
    Redis cache

Historical feeds:
    Cassandra archival storage

Kafka acts as the asynchronous backbone connecting Tweet Service, Feed Processor, and real-time notification systems.

# Design Twitter / X - Part 3
# Likes, Comments, Activity Tracking, User Profiles, and ML Recommendation Pipeline

---

# 1. Social Interaction System

Users can interact with tweets through:

- Likes
- Comments
- Shares / Retweets
- Clicks
- Time spent viewing content

These interactions are extremely valuable because they describe user preferences.

Example:

User 123:
- Likes sports posts
- Watches football videos completely
- Follows sports journalists

The system can use this information to personalize future content.

---

# 2. Like Service

## Responsibilities

Handles:

- Like a tweet
- Remove a like
- Get total likes on a tweet
- Determine whether a user has liked a tweet

---

## Data Model

Like Table:

Composite Key:

(PostId, UserId)

Example:

PostId | UserId
-------|--------
1001   | 501
1001   | 800
1002   | 900


Why this design?

- Prevents duplicate likes
- Efficient lookup:
  "Has this user liked this post?"

---

## Database Choice

Cassandra

Reason:

- Very high write throughput
- Massive number of user interactions
- Horizontal scalability

---

## Like Count Optimization

The exact likes are stored in Cassandra.

However, displaying like counts requires extremely frequent reads.

Example:

A celebrity post may receive millions of reads.

Instead of counting every time:

Cassandra:

(PostId, UserId)


Maintain a Redis cache:

Key:
PostId

Value:
LikeCount


Example:

Post 1001 → 1,500,000 likes


Benefits:

- O(1) reads
- Reduced Cassandra load
- Low latency UI updates

---

# 3. Comment Service

## Responsibilities

Handles:

- Create comments
- Retrieve comments for a post
- Delete comments

---

## Data Model

Comment Table:

Partition Key:
PostId

Clustering Key:
Timestamp

Example:

Post 1001:

10:00 → Comment A
10:02 → Comment B
10:05 → Comment C


Benefits:

- Efficient retrieval of comments sorted by time
- Supports very high write volume

---

# 4. Activity Tracking System

One of the most important systems in Twitter.

Every user action generates an event.

Examples:

- Like
- Comment
- Share
- Follow
- Click
- View
- Watch time


---

# Event Model


Activity Event:

{
    userId,
    actionType,
    contentId,
    timestamp
}


Example:

{
    userId: 123,
    actionType: "LIKE",
    postId: 567,
    timestamp: 123456789
}

---

# Event Streaming Pipeline


User Action
      |
 Like / Comment Service
      |
    Kafka
      |
 Activity Tracker Consumer
      |
 Cassandra


---

## Why Kafka?

The interaction path must remain fast.

We should not block the user request while:

- Updating analytics
- Running ML pipelines
- Generating reports

Kafka allows asynchronous processing.

---

# 5. User Interest Profile Generation

Raw activity data is valuable but not directly usable.

We need to convert actions into user interests.

Example:

User activity:

Likes:
- NBA posts
- Football videos
- Cricket highlights


After ML processing:

User Profile:

{
 Sports: 0.95,
 Technology: 0.30,
 Politics: 0.10
}

---

# 6. Batch Analytics Pipeline

Large-scale analytics can run offline.

Flow:


Kafka
  |
Spark Streaming
  |
Hadoop / Data Lake
  |
ML Jobs


Examples of analysis:

- Most liked topics
- Most shared posts
- User interest classification
- Trending categories
- Similar user identification

---

# 7. Content Classification

Posts can also be classified.

Example:

Tweet:

"Great game between Lakers and Celtics"

ML model tags it:

Sports
Basketball
NBA


This metadata is stored with the content.

---

# 8. Recommendation Engine

The recommendation engine combines:

## User profile

Example:

User likes:

Sports = 0.95

---

## Content profile

Post tags:

Sports = 0.90


---

## Social Graph

Friends with similar interests.

Example:

If many users similar to User A like a post,
we can recommend it to User A.

---

# Graph-Based Recommendation

Your notes mention using graph traversal.

Example:

User A follows User B.

User B follows User C.

User C produces content highly engaged by users similar to A.


Algorithms:

- BFS/DFS traversal
- Graph ranking algorithms
- Embedding-based similarity models


---

# 9. Re-engagement Pipeline

Problem:

Some users become inactive.

Goal:

Bring them back.

---

## Inputs:

- User interests
- Previously liked content
- Trending topics
- Social connections

---

## Pipeline:


Activity Data
       |
    Hadoop
       |
 ML Recommendation Jobs
       |
 Recommendation Service
       |
 Notification Service
       |
 Email / Push Notification


Example:

"Your favorite football team just posted a new video."

---

# 10. Why Separate Online and Offline Processing?

## Online Path

Requirements:

- Low latency
- User facing

Examples:

- Like a post
- Add a comment
- View timeline


---

## Offline Path

Requirements:

- Large computation
- Historical analysis

Examples:

- User profiling
- ML training
- Trend discovery


---

# L6 Trade-Off Discussion

## Why not update recommendation models during every like?

Because it increases user-facing latency.

Instead:

- Record events quickly
- Stream them through Kafka
- Process asynchronously

---

## Why keep activity history?

Allows:

- Personalized recommendations
- Re-engagement campaigns
- Trend analysis
- ML training

---

## Why Redis for counts?

Counting millions of likes by scanning a database is expensive.

Maintain a cached aggregate.

---

# Part 3 Summary

User interactions generate valuable signals.

Real-time path:

User Action
      |
 Service
      |
 Kafka


Asynchronous systems consume events:

- Activity Tracker
- Analytics
- ML Pipelines
- Recommendation Systems


Data stores:

- Cassandra → Likes, comments, activity logs
- Redis → Aggregated counts
- Hadoop/Data Lake → Historical analytics
- Spark → Large-scale processing


The social network is not just a content platform; it is a data and recommendation engine that continuously learns from user behavior.

# Design Twitter / X - Part 4
# Search, Trending Topics, Ranking, and Real-Time Analytics

---

# 1. Search Requirements

Users should be able to search for:

- Tweets
- Users
- Hashtags
- Topics

Examples:

Search:
"NBA Finals"

Results may include:

- Recent tweets
- Popular tweets
- Verified accounts
- Trending discussions

---

# 2. Challenges in Search

Searching billions of tweets by scanning Cassandra is not practical.

Example:

Bad approach:

Search Service
       |
 Cassandra
       |
 Scan billions of tweets


Problems:

- Extremely slow
- High database load
- Poor user experience


We need a dedicated search engine.

---

# 3. Elasticsearch

Use Elasticsearch because it provides:

- Full-text search
- Inverted indexes
- Tokenization
- Ranking algorithms
- Distributed scaling

---

# 4. How Inverted Index Works

Traditional database indexing:

TweetID → Tweet Content


Example:

Tweet 101:

"LeBron wins NBA finals"


A search engine creates the reverse mapping:


Word → Tweet IDs


Example:

"LeBron" → [101, 500, 800]

"NBA" → [101, 200, 600]

"Finals" → [101, 900]


This allows very fast keyword lookup.

---

# 5. Search Indexing Pipeline


Tweet Created
      |
      Kafka
      |
 Search Index Consumer
      |
 Elasticsearch


Why asynchronous indexing?

The user should not wait for search indexing before a tweet is successfully posted.

Tweet creation path:

User
 |
Tweet Service
 |
Cassandra
 |
Return Success


Background path:

Kafka
 |
Search Consumer
 |
Elasticsearch


Trade-off:

A newly created tweet may take a few seconds to appear in search.

Eventual consistency is acceptable.

---

# 6. Search Ranking

Not all matching tweets should appear in chronological order.

The ranking system can consider:

- Text relevance
- Number of likes
- Number of retweets
- Number of comments
- User reputation
- Recency
- User interests


Example:

Query:

"World Cup"

Two matching tweets:

Tweet A:
- Posted 5 seconds ago
- 2 likes

Tweet B:
- Posted 30 minutes ago
- 50,000 likes


The ranking model determines which appears first.

---

# 7. Personalized Search

Search results can vary by user.

Example:

User A:

Interested in sports.

Search:
"Giants"

Results:

NFL New York Giants.


User B:

Interested in technology.

Search:
"Giants"

Results:

Technology companies or different context.


Personalization signals:

- User activity history
- Likes
- Follows
- Previous searches
- Location

---

# 8. Popular Query Caching

Many users search for the same events.

Examples:

- Super Bowl
- Olympics
- Elections
- Oscars


Instead of running the same Elasticsearch query repeatedly:


Search Request
       |
 Redis Cache
       |
    Hit
       |
 Return results


On cache miss:


Search Service
       |
 Elasticsearch
       |
 Redis
       |
 User


---

# 9. Cache Expiration (TTL)

Search results change frequently.

Example:

Query:

"World Cup"

Results are very dynamic during a live match.


Therefore:

- Cache only hot queries.
- Use short TTL values.
- Refresh popular searches frequently.


---

# 10. Trending Topics

Trending is a streaming analytics problem.

Examples:

A hashtag suddenly increases:

#WorldCupFinal

10 mentions/minute
      ↓
50 mentions/minute
      ↓
5000 mentions/minute


The system must detect the spike.

---

# 11. Trending Pipeline


Tweet Events
      |
      Kafka
      |
 Stream Processing (Spark/Flink)
      |
 Count Hashtags
      |
 Detect Growth Rate
      |
 Trending Service
      |
 Redis Cache


---

# 12. Why Kafka?

Kafka allows:

- High-throughput ingestion
- Multiple consumers
- Replayability
- Durability


The same tweet stream can feed:

- Search indexing
- Trending calculations
- Analytics
- ML systems


---

# 13. Real-Time vs Batch Analytics

## Real-Time Streaming

Purpose:

Detect immediate events.

Examples:

- Breaking news
- Viral hashtags
- Live sports discussions


Pipeline:

Kafka
 |
Spark Streaming/Flink
 |
Redis


Latency:

Seconds


---

## Batch Analytics

Purpose:

Historical analysis.

Examples:

- Monthly trends
- User behavior analysis
- Recommendation model training


Pipeline:

Kafka
 |
Data Lake (HDFS/S3)
 |
Spark Batch Jobs


Latency:

Minutes to hours

---

# 14. Data Storage for Analytics

Raw events are valuable.

Store:

- Tweet creation events
- Search events
- Likes
- Shares
- Clicks


Reasons:

- Historical reporting
- Model training
- Trend analysis
- Replay new analytics pipelines


---

# 15. Scaling Elasticsearch

A single Elasticsearch node cannot handle Twitter scale.

Use:

- Sharding
- Replication


Sharding:

Tweet Index

Shard 1:
Tweets A-M

Shard 2:
Tweets N-Z


Replication provides:

- High availability
- Higher read throughput


---

# 16. L6 Trade-Off Discussion

## Why not use Cassandra for search?

Cassandra is optimized for:

- Key-based lookups
- High write throughput


It is not designed for:

- Full-text search
- Relevance ranking
- Token matching


Elasticsearch is specialized for search.

---

## Why not update Elasticsearch synchronously?

Because it increases tweet posting latency.

Instead:

Tweet write path:

Fast and reliable.

Search indexing:

Asynchronous.

Trade-off:

Search is eventually consistent.

---

## Why cache search results?

Because many users search the same topics.

Examples:

- World Cup
- Elections
- Breaking news


Caching reduces:

- Elasticsearch load
- Response latency


---

# Part 4 Summary

Search Architecture:

Tweet Service
       |
    Kafka
       |
 Search Consumer
       |
 Elasticsearch


Trending Architecture:

Tweet Events
       |
    Kafka
       |
 Spark Streaming/Flink
       |
 Trending Service
       |
 Redis


Storage Choices:

- Elasticsearch → Full-text search
- Redis → Hot search results and trending topics
- Kafka → Event backbone
- Spark/Flink → Real-time processing
- Data Lake → Historical analytics


Key idea:

Twitter search is not just keyword matching.

It combines:

- Inverted indexing
- Relevance ranking
- User personalization
- Real-time trend detection
- Large-scale analytics

# Design Twitter / X - Part 5
# Recommendation System, Notifications, Feed Ranking, and End-to-End Architecture

---

# 1. The Evolution of the Timeline

Originally, Twitter timelines were simple:

Timeline = Tweets from people I follow sorted by timestamp.

Example:

User follows:

- Sports Account
- Technology Account
- Celebrity

Feed:

10:01 Sports tweet

10:03 Celebrity tweet

10:05 Technology tweet


However, modern social networks optimize for engagement.

The question becomes:

"What content is most valuable for this specific user?"

Therefore the feed becomes:

Timeline = Ranking(User Profile, Content Features, Social Signals)


---

# 2. User Profile Generation

The system continuously learns user interests.

Signals include:

- Likes
- Comments
- Retweets
- Shares
- Watch time
- Clicks
- Search history
- Follow relationships


Every action generates an event:


User Action
     |
     Kafka
     |
 Activity Tracker
     |
 Data Lake


Examples:

User A:

Likes:
- NBA
- NFL
- Football


Generated User Profile:

{
 Sports: 0.95,
 Technology: 0.20,
 Politics: 0.05
}


This profile is continuously updated.


---

# 3. Content Understanding

Tweets also need a profile.

Example Tweet:

"Lakers defeat Celtics in Game 7"

ML Classification:

{
 Sports: 0.98,
 Basketball: 0.95,
 NBA: 0.99
}


The content metadata is stored with the tweet.


---

# 4. Recommendation Engine


Inputs:

1. User Profile
2. Content Features
3. Social Graph
4. Trending Topics
5. Historical Engagement


Example:

User Profile:

Sports: 0.95


Candidate Tweet:

Sports: 0.90


The recommendation model calculates a score:

Relevance Score =
 User Interest
 + Tweet Popularity
 + Social Connections
 + Freshness


Tweets with higher scores appear earlier in the feed.


---

# 5. Candidate Generation and Ranking

A major challenge:

Twitter contains billions of tweets.

We cannot rank every tweet for every user.

Therefore we divide the problem into two stages.


## Stage 1: Candidate Generation


Retrieve a smaller set of candidate tweets from:

- Followed users
- Trending topics
- Similar users
- Popular tweets


Example:

1 billion tweets

        ↓

10,000 candidate tweets


---

## Stage 2: Ranking


Apply ML ranking models using:

- User preferences
- Likes
- Shares
- Comments
- Time spent viewing
- Relationship strength
- Recency


Example:

10,000 tweets

        ↓

Top 100 personalized tweets


---

# 6. Real-Time Recommendation Updates


Some recommendations need to change quickly.

Examples:

- Breaking news
- Sports events
- Viral content


Pipeline:


Tweet Activity
      |
    Kafka
      |
 Stream Processing
      |
 Update Trending Signals
      |
 Recommendation Service


---

# 7. Notification System


Notifications improve user engagement.


Examples:

- Someone liked your tweet.
- A user you follow posted new content.
- A topic you like is trending.
- A celebrity you follow is live.


---

# 8. Notification Architecture


Events:

- Like Event
- Follow Event
- Tweet Event


Flow:


Application Services
          |
        Kafka
          |
 Notification Service
          |
 User Preference Service
          |
 Notification Queue
          |
 Push / Email / SMS Providers


---

# 9. Why Use Kafka for Notifications?


A direct synchronous approach:


Tweet Service
       |
Notification API
       |
External Push Provider


Problems:

- Increased tweet latency
- Failure of notification provider affects tweeting
- Difficult to support multiple consumers


Event-driven approach:


Tweet Service
      |
    Kafka
      |
 Notification Consumers


Benefits:

- Low latency tweet creation
- Durable message storage
- Retry capability
- Independent scaling
- Multiple notification channels


---

# 10. User Preferences


Users can control:

- Push notifications
- Email notifications
- Muted users
- Blocked users


Before sending a notification:

Notification Service
          |
 Check Preferences
          |
 Send Message


---

# 11. Re-engagement Pipeline


Some users become inactive.

Goal:

Bring them back.


Example:

User has not opened Twitter for 10 days.

System checks:

- Historical interests
- New content from followed accounts
- Trending events


Pipeline:


Activity History
        |
 Data Lake
        |
 ML Recommendation Jobs
        |
 Re-engagement Service
        |
 Notification Service


Example Notification:

"Your favorite team just won a championship."

---

# 12. Online vs Offline Processing


## Online Systems

Requirements:

- Millisecond latency
- User-facing requests


Examples:

- Load timeline
- Like tweet
- Post comment
- Send notifications


Technologies:

- Redis
- Cassandra
- APIs


---

## Offline Systems


Requirements:

- Process enormous historical datasets


Examples:

- Train recommendation models
- Build user profiles
- Discover trends


Technologies:

- Spark
- Hadoop
- Data Lake


---

# 13. End-to-End Twitter Architecture


                     Users
                       |
                 Load Balancer
                       |
                   API Gateway
                       |
 --------------------------------------------------
 |             |             |                    |
User      Tweet Service  Timeline          Social Graph
Service                   Service
 |             |             |
MySQL      Cassandra       Redis
              |
            Kafka
              |
 ----------------------------------------------------------
 |             |              |             |              |
Feed       Search        Analytics     Notification     ML
Processor  Service       Pipeline       Service       Pipeline
 |             |              |             |
Redis      Elasticsearch   Data Lake    Push Provider


---

# 14. Important L6 Trade-Offs


## Why not calculate recommendations during every request?

Because:

- ML ranking is expensive.
- It increases latency.
- It does not scale.

Instead:

- Precompute user profiles.
- Generate candidates.
- Apply fast ranking online.


---

## Why use event-driven architecture?

Because many systems need the same data:

Tweet Created Event

Consumers:

- Timeline generation
- Search indexing
- Analytics
- Recommendations
- Notifications


Kafka allows:

- Independent consumers
- Replayability
- Durability
- Backpressure handling


---

## Why separate online and offline systems?

Online systems optimize for:

- Latency
- Availability


Offline systems optimize for:

- Throughput
- Large-scale computation


---

# Final Twitter Design Summary


Core Databases:

MySQL:
- User accounts
- Profile information


Cassandra:
- Tweets
- Likes
- Comments
- Activity logs


Redis:
- Timeline cache
- Like counts
- Trending topics
- Search cache


Elasticsearch:
- Full-text search


Object Storage + CDN:
- Images and videos


Kafka:
- Event backbone


Spark/Hadoop/Data Lake:
- Analytics and ML


---

# The Most Important Interview Takeaways


Timeline:
- Normal users → Fan-out on Write
- Celebrities → Fan-out on Read
- Redis stores recent feeds
- Cassandra stores historical data


Search:
- Kafka → Elasticsearch
- Inverted indexes
- Ranking and caching


Recommendations:
- User behavior → User Profile
- Content → Feature extraction
- Candidate generation → ML ranking


Notifications:
- Event-driven
- Asynchronous
- User preference aware


The key architectural principle of Twitter is:

"Optimize user-facing paths for low latency, and move heavy computation to asynchronous pipelines using Kafka and large-scale processing systems."

# Social Feed Platform Differences
## Twitter / Facebook / Instagram / LinkedIn Specific Design Considerations

---

# 1. Common Foundation (Applies to All Social Feed Systems)

The following architecture is common across Twitter, Facebook, Instagram, and LinkedIn:

- Content Creation Service
- User / Social Graph Service
- Feed Generation Service
- Fan-out on Write vs Fan-out on Read
- Redis Feed Cache
- Cassandra or distributed storage for large-scale content
- Kafka-based asynchronous pipelines
- Activity Tracking
- Recommendation Engine
- Notifications
- Data Lake + ML pipelines

The first five parts of this design document cover these common concepts.

This document only focuses on platform-specific differences.

---

# 2. Twitter / X Specific Design Considerations

## 2.1 Directed Social Graph

Twitter follows a directed relationship model.


A follows B

A can see B's content.

B does not automatically follow A.


Example:

User A --------> User B


Data Model:

FollowerId → FollowingId


---

## 2.2 Celebrity Problem

Twitter has a very high number of celebrity accounts.

Example:

Celebrity:
100 million followers


Problem:

A single tweet can trigger:

100 million feed updates.


Solution:

Use Hybrid Feed Generation.

Normal users:
- Fan-out on Write

Celebrities:
- Fan-out on Read


---

## 2.3 Search Is a Core Feature

Twitter users frequently search:

- Hashtags
- Breaking news
- Trending events
- Public conversations


Architecture:

Tweet Created
      |
    Kafka
      |
Search Consumer
      |
Elasticsearch


Use:

- Inverted Indexes
- Ranking Algorithms
- Redis caching for hot searches


---

## 2.4 Trending Topics

Twitter emphasizes real-time event discovery.


Pipeline:

Tweet Events
      |
    Kafka
      |
Stream Processing (Spark/Flink)
      |
Trending Service
      |
Redis


Examples:

- World Cup
- Elections
- Breaking News


---

# 3. Facebook Specific Design Considerations


## 3.1 Friendship Graph (Bidirectional)

Unlike Twitter follows, Facebook relationships are usually mutual.


User A <-------> User B


A friendship requires:

- Friend Request
- Pending State
- Acceptance or Rejection


Example:

FriendRequest:

{
    requestId,
    senderId,
    receiverId,
    status,
    timestamp
}


---

## 3.2 Privacy and Access Control (Most Important Difference)

Facebook has complex visibility rules.

A post can have visibility:

- Public
- Friends
- Friends of Friends
- Custom Groups
- Only Me


Feed generation becomes:


Candidate Posts
       |
 ACL / Privacy Service
       |
 Filter Unauthorized Content
       |
 Ranking Service
       |
 User Feed


This is one of the biggest design differences compared with Twitter.


---

## 3.3 Media and Photo Albums

Facebook is more media-centric.

Architecture:


Photo Upload
       |
 Media Service
       |
 Object Storage (S3)
       |
 CDN


Photo metadata may contain:

- PhotoId
- UserId
- AlbumId
- Tags
- Location
- Timestamp


---

## 3.4 Facebook Stories

Stories are temporary content.

Characteristics:

- Available for 24 hours
- High read traffic
- Temporary storage


Implementation:

Redis / Cache
       |
 TTL
       |
 Automatic Expiration


---

# 4. Instagram Specific Design Considerations


Instagram shares many concepts with Facebook but is even more media focused.


## 4.1 Directed Follower Graph


User A --------> User B


---

## 4.2 Media Processing Pipeline


Image/Video Upload
        |
 Media Service
        |
 Transcoding Service
        |
 Multiple Resolutions
        |
 Object Storage
        |
 CDN


Reasons:

- Different device sizes
- Different network speeds
- Optimized streaming


---

## 4.3 Reels Recommendation System

Reels are heavily ML driven.


Ranking signals:

User Features:
- Watch Time
- Likes
- Shares
- Comments
- Following History


Content Features:

- Topic
- Creator
- Popularity
- Engagement Rate


ML Ranking decides which videos appear in the feed.


---

## 4.4 Stories

Same concept as Facebook:

- Ephemeral content
- TTL based expiration
- Cached for low latency


---

# 5. LinkedIn Specific Design Considerations


## 5.1 Professional Social Graph


Relationships are based on:

- Connections
- Companies
- Schools
- Skills
- Professional Interests


---

## 5.2 Feed Ranking

Ranking signals are different from consumer social networks.


Examples:

Professional relevance:
- Same company
- Similar industry
- Shared connections
- Job interests


Engagement:

- Likes
- Comments
- Shares


Recency:

- Recent posts


---

## 5.3 Job Recommendation System


Inputs:

User Profile:
- Skills
- Experience
- Industry


Job Features:
- Required skills
- Location
- Company


ML Matching:

User Profile
       +
Job Features
       +
Historical Engagement
       |
 Recommendation Score


---

## 5.4 Notifications


Examples:

- Someone viewed your profile
- New job matches your skills
- Your connection changed companies
- Someone liked your post


---

# 6. Interview Strategy

Do not memorize four separate architectures.


Think in terms of a common social feed platform:

Core Components:

User
 |
API Gateway
 |
--------------------------------
|              |               |
Post        Feed          Social Graph
Service     Service       Service
 |
Kafka
 |
------------------------------------------------
|          |          |          |             |
Search  Analytics  Notifications  ML      Recommendations


Then apply platform-specific modifications.


---

# Quick Comparison Table

| Feature | Twitter | Facebook | Instagram | LinkedIn |
|---|---|---|---|---|
| Social Graph | Directed Follow | Mutual Friends | Directed Follow | Professional Connections |
| Main Content | Tweets | Posts/Photos | Photos/Videos/Reels | Professional Posts |
| Search Importance | Very High | Medium | Low | Medium |
| Trending Topics | Very High | Medium | Medium | Low |
| Privacy Complexity | Low | Very High | Medium | Medium |
| Media Focus | Medium | High | Very High | Low |
| ML Recommendations | High | Very High | Very High | High |
| Celebrity Problem | Major | Moderate | Major | Low |
| Feed Strategy | Hybrid | Hybrid | Hybrid | Hybrid |

---

# Final L6 Interview Perspective

The interviewer is not looking for "Twitter knowledge" or "Facebook knowledge".

The real evaluation is whether you understand:

- Fan-out on Write vs Fan-out on Read
- Caching strategies
- Event-driven architectures using Kafka
- Storage trade-offs
- Search and indexing
- Activity tracking
- ML-based ranking
- Real-time vs batch processing
- Scalability and failure handling


The platform-specific differences are usually only the last 10–20% of the discussion.

Master the common social feed architecture first, then add these variations.	


	### 3.3 Fraud Monitoring System

	### 3.4 Financial Aggregation Platform (Plaid / QuickBooks)
	
	
# Job Scheduler System Design

# Chapter 1 - Requirements, APIs & High-Level Architecture

---

# Problem Statement

Design a distributed Job Scheduler similar to

- Quartz Scheduler
- Kubernetes CronJob
- AWS EventBridge Scheduler
- Apache Airflow (simplified)
- Google Cloud Scheduler

The system should execute jobs at scheduled times reliably, even in the presence of failures.

Examples

- Send email every day at 9 AM
- Run payroll every Friday
- Generate reports every hour
- Retry failed payments after 5 minutes
- Delete expired sessions every night

---

# Functional Requirements

The system should support

## Job Management

- Create Job
- Update Job
- Delete Job
- Pause Job
- Resume Job

---

## Scheduling

Support

- One-time jobs
- Recurring jobs
- Cron expressions
- Fixed interval jobs

Examples

```
Every 5 minutes

Every day at 9 AM

Every Monday

First day of every month
```

---

## Execution

- Execute jobs
- Retry failed jobs
- Record execution history
- Prevent duplicate execution

---

## Monitoring

Users should be able to see

- Job Status
- Last Run
- Next Run
- Retry Count
- Execution Logs

---

# Non Functional Requirements

## Reliability

Jobs should never be lost.

---

## High Availability

Scheduler should continue working if one server crashes.

---

## Scalability

Support

- Millions of jobs
- Thousands of executions/sec

---

## Fault Tolerance

Recover from

- Scheduler crashes
- Worker crashes
- Database failures

---

## Low Latency

Jobs should execute as close as possible to their scheduled time.

Example

```
Scheduled

9:00:00

Actual

9:00:01
```

---

# Capacity Estimation

Assume

```
10 Million Scheduled Jobs
```

Average frequency

```
Once Per Day
```

Executions/day

```
10 Million
```

Average

```
10,000,000

/

86,400

≈116 jobs/sec
```

Peak traffic

```
5×

≈600 jobs/sec
```

Design for

```
5,000 Jobs/sec
```

to allow future growth.

---

# APIs

---

## Create Job

```
POST /jobs
```

Request

```json
{
   "jobName":"Daily Report",
   "cron":"0 9 * * *",
   "payload":{
      "email":"admin@test.com"
   }
}
```

Response

```json
{
   "jobId":"12345",
   "status":"CREATED"
}
```

---

## Update Job

```
PUT /jobs/{jobId}
```

Allows

- Change cron
- Change payload
- Change retry policy

---

## Pause Job

```
POST /jobs/{jobId}/pause
```

---

## Resume Job

```
POST /jobs/{jobId}/resume
```

---

## Delete Job

```
DELETE /jobs/{jobId}
```

---

## Get Job

```
GET /jobs/{jobId}
```

Returns

```json
{
   "status":"ACTIVE",
   "lastRun":"9:00 AM",
   "nextRun":"Tomorrow 9:00 AM"
}
```

---

# High Level Architecture

```
                     Clients

                        │

                 REST API

                        │

                        ▼

                  Job Service

                        │

                        ▼

                  Job Database

                        ▲

                        │

               Scheduler Service

                        │

                Find Due Jobs

                        │

                        ▼

                  Message Queue

                        │

        ┌───────────────┼───────────────┐

        ▼               ▼               ▼

    Worker 1       Worker 2       Worker 3

        │               │               │

        ▼               ▼               ▼

             Execute Business Logic
```

---

# Components

---

## Client

Responsible for

- Create Job
- Update Job
- Delete Job
- Pause Job

---

## Job Service

Handles

- API requests
- Validation
- Authentication
- Store jobs
- Update jobs

Notice

It **does not execute jobs**.

---

## Job Database

Stores

- Job Definition
- Cron Expression
- Status
- Retry Policy
- Next Run Time

This is the Source of Truth.

---

## Scheduler Service

The Scheduler is the heart of the system.

Every few seconds it asks

```
Which jobs are due now?
```

It does NOT execute jobs.

It only decides

```
What should run?
```

---

## Message Queue

Examples

- Kafka
- RabbitMQ
- SQS

Purpose

Decouple scheduling from execution.

Instead of

```
Scheduler

↓

Worker
```

we have

```
Scheduler

↓

Queue

↓

Workers
```

Benefits

- Retries
- Buffering
- Load balancing

---

## Workers

Workers execute the actual business logic.

Examples

```
Send Email

Generate Report

Cleanup Logs

Retry Payment

Refresh Cache
```

Workers should be stateless.

Multiple workers can run in parallel.

---

# End-to-End Flow

Suppose a user creates

```
Daily Report

9:00 AM
```

### Step 1

Client calls

```
POST /jobs
```

---

### Step 2

Job Service validates the request.

---

### Step 3

Store

```
Cron

Payload

Next Run

Status
```

in the database.

Nothing executes yet.

---

### Step 4

At

```
9:00 AM
```

Scheduler runs

```sql
SELECT *
FROM Jobs
WHERE nextRunTime <= NOW()
AND status='ACTIVE';
```

Returns

```
Job123
```

---

### Step 5

Scheduler publishes

```
Execute Job123
```

to the Queue.

---

### Step 6

Worker receives

```
Job123
```

and executes

```
Generate Daily Report
```

---

### Step 7

Worker reports success.

Database updates

```
Last Run = Today

Next Run = Tomorrow 9 AM
```

Done.

---

# Why Separate Scheduler and Workers?

Many candidates initially design

```
Scheduler

↓

Execute Job
```

This doesn't scale.

Suppose

```
1000 jobs

9:00 AM
```

Scheduler becomes busy executing.

Meanwhile,

new jobs become due.

Scheduling is delayed.

Instead

```
Scheduler

↓

Queue

↓

Workers
```

Scheduler only decides

"What should run?"

Workers perform

"How to execute."

This separation allows each layer to scale independently.

---

# Design Principles

The architecture follows four principles.

### Separation of Concerns

Scheduler decides.

Workers execute.

---

### Durability

Database stores every job definition.

---

### Asynchronous Execution

Queue decouples scheduling from execution.

---

### Horizontal Scalability

Scheduler, Queue, and Workers can all scale independently.

---

# What Comes Next

Now that we understand the overall architecture,

the next chapter focuses on the most important component in the system:

# Chapter 2

**Database Design**

We'll design

- Jobs table
- JobExecution table
- Retry metadata
- Cron storage
- Indexes
- NextRunTime optimization
- Database partitioning

Before moving to the Scheduler algorithm itself.

# Job Scheduler System Design

# Chapter 1 - Requirements, APIs & High-Level Architecture

---

# Problem Statement

Design a distributed Job Scheduler similar to

- Quartz Scheduler
- Kubernetes CronJob
- AWS EventBridge Scheduler
- Apache Airflow (simplified)
- Google Cloud Scheduler

The system should execute jobs at scheduled times reliably, even in the presence of failures.

Examples

- Send email every day at 9 AM
- Run payroll every Friday
- Generate reports every hour
- Retry failed payments after 5 minutes
- Delete expired sessions every night

---

# Functional Requirements

The system should support

## Job Management

- Create Job
- Update Job
- Delete Job
- Pause Job
- Resume Job

---

## Scheduling

Support

- One-time jobs
- Recurring jobs
- Cron expressions
- Fixed interval jobs

Examples

```
Every 5 minutes

Every day at 9 AM

Every Monday

First day of every month
```

---

## Execution

- Execute jobs
- Retry failed jobs
- Record execution history
- Prevent duplicate execution

---

## Monitoring

Users should be able to see

- Job Status
- Last Run
- Next Run
- Retry Count
- Execution Logs

---

# Non Functional Requirements

## Reliability

Jobs should never be lost.

---

## High Availability

Scheduler should continue working if one server crashes.

---

## Scalability

Support

- Millions of jobs
- Thousands of executions/sec

---

## Fault Tolerance

Recover from

- Scheduler crashes
- Worker crashes
- Database failures

---

## Low Latency

Jobs should execute as close as possible to their scheduled time.

Example

```
Scheduled

9:00:00

Actual

9:00:01
```

---

# Capacity Estimation

Assume

```
10 Million Scheduled Jobs
```

Average frequency

```
Once Per Day
```

Executions/day

```
10 Million
```

Average

```
10,000,000

/

86,400

≈116 jobs/sec
```

Peak traffic

```
5×

≈600 jobs/sec
```

Design for

```
5,000 Jobs/sec
```

to allow future growth.

---

# APIs

---

## Create Job

```
POST /jobs
```

Request

```json
{
   "jobName":"Daily Report",
   "cron":"0 9 * * *",
   "payload":{
      "email":"admin@test.com"
   }
}
```

Response

```json
{
   "jobId":"12345",
   "status":"CREATED"
}
```

---

## Update Job

```
PUT /jobs/{jobId}
```

Allows

- Change cron
- Change payload
- Change retry policy

---

## Pause Job

```
POST /jobs/{jobId}/pause
```

---

## Resume Job

```
POST /jobs/{jobId}/resume
```

---

## Delete Job

```
DELETE /jobs/{jobId}
```

---

## Get Job

```
GET /jobs/{jobId}
```

Returns

```json
{
   "status":"ACTIVE",
   "lastRun":"9:00 AM",
   "nextRun":"Tomorrow 9:00 AM"
}
```

---

# High Level Architecture

```
                     Clients

                        │

                 REST API

                        │

                        ▼

                  Job Service

                        │

                        ▼

                  Job Database

                        ▲

                        │

               Scheduler Service

                        │

                Find Due Jobs

                        │

                        ▼

                  Message Queue

                        │

        ┌───────────────┼───────────────┐

        ▼               ▼               ▼

    Worker 1       Worker 2       Worker 3

        │               │               │

        ▼               ▼               ▼

             Execute Business Logic
```

---

# Components

---

## Client

Responsible for

- Create Job
- Update Job
- Delete Job
- Pause Job

---

## Job Service

Handles

- API requests
- Validation
- Authentication
- Store jobs
- Update jobs

Notice

It **does not execute jobs**.

---

## Job Database

Stores

- Job Definition
- Cron Expression
- Status
- Retry Policy
- Next Run Time

This is the Source of Truth.

---

## Scheduler Service

The Scheduler is the heart of the system.

Every few seconds it asks

```
Which jobs are due now?
```

It does NOT execute jobs.

It only decides

```
What should run?
```

---

## Message Queue

Examples

- Kafka
- RabbitMQ
- SQS

Purpose

Decouple scheduling from execution.

Instead of

```
Scheduler

↓

Worker
```

we have

```
Scheduler

↓

Queue

↓

Workers
```

Benefits

- Retries
- Buffering
- Load balancing

---

## Workers

Workers execute the actual business logic.

Examples

```
Send Email

Generate Report

Cleanup Logs

Retry Payment

Refresh Cache
```

Workers should be stateless.

Multiple workers can run in parallel.

---

# End-to-End Flow

Suppose a user creates

```
Daily Report

9:00 AM
```

### Step 1

Client calls

```
POST /jobs
```

---

### Step 2

Job Service validates the request.

---

### Step 3

Store

```
Cron

Payload

Next Run

Status
```

in the database.

Nothing executes yet.

---

### Step 4

At

```
9:00 AM
```

Scheduler runs

```sql
SELECT *
FROM Jobs
WHERE nextRunTime <= NOW()
AND status='ACTIVE';
```

Returns

```
Job123
```

---

### Step 5

Scheduler publishes

```
Execute Job123
```

to the Queue.

---

### Step 6

Worker receives

```
Job123
```

and executes

```
Generate Daily Report
```

---

### Step 7

Worker reports success.

Database updates

```
Last Run = Today

Next Run = Tomorrow 9 AM
```

Done.

---

# Why Separate Scheduler and Workers?

Many candidates initially design

```
Scheduler

↓

Execute Job
```

This doesn't scale.

Suppose

```
1000 jobs

9:00 AM
```

Scheduler becomes busy executing.

Meanwhile,

new jobs become due.

Scheduling is delayed.

Instead

```
Scheduler

↓

Queue

↓

Workers
```

Scheduler only decides

"What should run?"

Workers perform

"How to execute."

This separation allows each layer to scale independently.

---

# Design Principles

The architecture follows four principles.

### Separation of Concerns

Scheduler decides.

Workers execute.

---

### Durability

Database stores every job definition.

---

### Asynchronous Execution

Queue decouples scheduling from execution.

---

### Horizontal Scalability

Scheduler, Queue, and Workers can all scale independently.

---

# What Comes Next

Now that we understand the overall architecture,

the next chapter focuses on the most important component in the system:

# Chapter 2

**Database Design**

We'll design

- Jobs table
- JobExecution table
- Retry metadata
- Cron storage
- Indexes
- NextRunTime optimization
- Database partitioning

Before moving to the Scheduler algorithm itself.

# Job Scheduler System Design

# Chapter 2 - Database Design

---

# Goal

The database is the source of truth.

It stores

- Job definitions
- Schedule
- Retry policy
- Execution history
- Current status

Unlike WhatsApp, the scheduler doesn't store billions of messages.

Instead, it stores

- millions of jobs
- millions of executions

The schema should support

- Fast lookup of due jobs
- Fast updates after execution
- Execution history
- Retry management

---

# Tables

We'll use three primary tables.

```
Jobs

↓

JobExecutions

↓

DeadLetterQueue (optional)
```

---

# 1. Jobs Table

This is the most important table.

Every scheduled job has exactly one row.

| Column | Description |
|---------|-------------|
| jobId | Primary Key |
| jobName | Human readable name |
| cronExpression | Scheduling rule |
| payload | Job data |
| status | ACTIVE / PAUSED |
| nextRunTime | Next scheduled execution |
| retryPolicy | Retry configuration |
| createdAt | Creation time |
| updatedAt | Last update |

Example

| jobId | jobName | cron | nextRunTime | status |
|--------|----------|------|-------------|---------|
|101|Daily Report|0 9 * * *|2026-07-12 09:00|ACTIVE|

---

# Why Store nextRunTime?

Suppose we only stored

```
Cron Expression

0 9 * * *
```

Every second

the scheduler would need to parse

every cron expression

for every job.

Example

```
10 Million Jobs

↓

Parse Cron

↓

Every Second
```

Impossible.

Instead

we precompute

```
Next Run

↓

2026-07-12 09:00
```

Now Scheduler simply asks

```sql
SELECT *
FROM Jobs
WHERE nextRunTime <= NOW()
```

Much faster.

---

# After Execution

Suppose

Daily Report

runs successfully.

Scheduler computes

```
Tomorrow

9:00 AM
```

Updates

```
nextRunTime
```

The cron is parsed only

after execution,

not every second.

Huge optimization.

---

# Payload

Every job needs some input.

Example

Send Email

```json
{
   "to":"admin@test.com",
   "subject":"Daily Report"
}
```

Cleanup Job

```json
{
   "directory":"/logs"
}
```

Retry Payment

```json
{
   "paymentId":12345
}
```

Payload is usually stored as JSON.

---

# Status

Common values

```
ACTIVE

PAUSED

COMPLETED

FAILED
```

Scheduler only executes

```
ACTIVE
```

jobs.

---

# Retry Policy

Different jobs have different retry rules.

Example

```
Retry

3 times

Every 30 seconds
```

or

```
No Retry
```

Store this with the job.

---

# Indexes

The most important index

```
(nextRunTime, status)
```

Why?

Scheduler executes

```sql
SELECT *
FROM Jobs
WHERE status='ACTIVE'
AND nextRunTime <= NOW()
ORDER BY nextRunTime
LIMIT 1000;
```

This query runs every second.

Without an index,

the database scans

every job.

With millions of jobs,

that's unacceptable.

---

# 2. JobExecution Table

Jobs table stores

"What should run."

JobExecution stores

"What actually happened."

---

Schema

| Column | Description |
|---------|-------------|
| executionId | Primary Key |
| jobId | Foreign Key |
| startTime | Execution started |
| endTime | Execution finished |
| status | SUCCESS / FAILED |
| attempt | Retry number |
| workerId | Worker executing |
| errorMessage | Failure reason |

---

Example

| executionId | jobId | status | attempt |
|-------------|--------|----------|----------|
|5001|101|SUCCESS|1|
|5002|102|FAILED|2|

---

Why separate table?

Suppose

Daily Report

runs every day.

We don't overwrite yesterday's execution.

We keep history.

Useful for

- Monitoring
- Auditing
- Debugging

---

# 3. Dead Letter Queue (Optional)

Suppose

Retry Policy

```
3 Attempts
```

All fail.

Now what?

Move the job to

```
Dead Letter Queue
```

Example

| jobId | reason |
|--------|---------|
|105|SMTP Server Down|

Operations team investigates later.

---

# Common Queries

---

## Find Due Jobs

```sql
SELECT *
FROM Jobs
WHERE status='ACTIVE'
AND nextRunTime<=NOW()
ORDER BY nextRunTime
LIMIT 1000;
```

Runs continuously.

This is the most important query.

---

## Job History

```sql
SELECT *
FROM JobExecution
WHERE jobId=101
ORDER BY startTime DESC;
```

---

## Pause Job

```sql
UPDATE Jobs
SET status='PAUSED'
WHERE jobId=101;
```

Scheduler automatically ignores it.

---

## Resume Job

```sql
UPDATE Jobs
SET status='ACTIVE'
WHERE jobId=101;
```

---

# Scaling

Suppose

```
100 Million Jobs
```

One database won't be enough.

Shard

by

```
jobId
```

or

```
TenantId
```

Example

```
Shard1

Jobs 1-10M

Shard2

Jobs 10M-20M
```

Scheduler instances own different shards.

---

# Why Not Shard By nextRunTime?

Looks tempting.

Problem

Every execution updates

```
nextRunTime
```

The row would continuously move between shards.

Very expensive.

---

# Database vs Queue

Important distinction.

Database

stores

```
Job Definition
```

Queue

stores

```
Execution Request
```

Example

Database

```
Run Every Day
```

Queue

```
Run NOW
```

Completely different purposes.

---

# Job Lifecycle

```
Create Job

↓

Jobs Table

↓

Scheduler Finds Due Job

↓

Queue

↓

Worker Executes

↓

JobExecution Table

↓

Update nextRunTime

↓

Wait for next schedule
```

---

# Interview Questions

## Why store nextRunTime?

Avoid parsing millions of cron expressions every second.

---

## Why separate JobExecution?

Maintain execution history without modifying the job definition.

---

## Why keep execution history?

- Debugging
- Auditing
- Monitoring
- SLA reporting

---

## Why not overwrite the last execution?

You lose history.

You can't answer

"Did this job fail yesterday?"

---

## Why use an index on nextRunTime?

Scheduler continuously searches for jobs that are due.

Without an index,

every polling cycle becomes a full table scan.

---

## Why database instead of queue?

The database stores durable job definitions.

The queue stores short-lived execution requests.

---

# Interview Summary

> The database is the durable source of truth for the scheduler. The `Jobs` table stores the job definition, cron expression, payload, retry policy, and the precomputed `nextRunTime`, allowing the Scheduler to efficiently find only jobs that are due. The `JobExecution` table records every execution attempt for monitoring, debugging, and auditing. After each successful execution, the Scheduler computes the next execution time and updates the `nextRunTime` field. The most critical optimization is indexing `(status, nextRunTime)`, which enables efficient retrieval of due jobs without scanning the entire table.


# Chapter 3 - Scheduler Service Deep Dive

---

# Goal

The Scheduler Service is the brain of the system.

It answers one question continuously:

```
Which jobs should execute now?
```

Notice something important.

The Scheduler **does not execute jobs**.

It only decides

```
What should run?
```

Workers execute the jobs.
The queue carries only the jobId (or job ID plus minimal metadata). The Worker treats the database as the source of truth and loads the latest job definition—including the job type, payload, retry policy, and cron expression—before execution. This avoids executing stale job definitions if the job was updated after it was scheduled.

---

# High-Level Flow

```
             Scheduler

                  │

                  ▼

      Find Due Jobs (DB)

                  │

                  ▼

          Publish to Queue

                  │

                  ▼

              Workers

                  │

                  ▼

           Update Next Run
```

---

# Responsibilities

The Scheduler is responsible for

- Finding due jobs
- Preventing duplicate scheduling
- Publishing execution requests
- Computing nextRunTime
- Updating the database

It is **NOT** responsible for

- Sending emails
- Running reports
- Executing business logic

That belongs to Workers.



---

# Simple Scheduler Algorithm

Every second (or configurable interval)

```
while(true){

    Find Due Jobs

    Publish Jobs

    Update Next Run

    Sleep
}
```

That's the entire scheduler at a high level.

Let's understand each step.

---

# Step 1 - Find Due Jobs

Scheduler executes

```sql
SELECT *
FROM Jobs
WHERE status='ACTIVE'
AND nextRunTime<=NOW()
ORDER BY nextRunTime
LIMIT 1000;
```

Suppose current time

```
9:00:01
```

Jobs

| Job | nextRun |
|------|----------|
|A|8:59|
|B|9:00|
|C|9:05|

Scheduler picks

```
A

B
```

---

# Why ORDER BY nextRunTime?

Suppose

```
8:59

9:00

9:01

9:02
```

We should execute

oldest jobs first.

---

# Why LIMIT?

Imagine

```
500,000 jobs

due
```

We don't want one Scheduler iteration to process everything.

Instead

```
LIMIT 1000
```

Process in batches.

Then repeat.

---

# Step 2 - Publish to Queue

Instead of executing directly

Scheduler publishes

```
Execute Job101
```

Queue

↓

Workers

Example

```
Scheduler

↓

Kafka

↓

Worker1

Worker2

Worker3
```

Scheduler becomes lightweight.

---

# Why Queue?

Without Queue

```
Scheduler

↓

Execute Job
```

Scheduler becomes blocked.

New jobs become late.

Queue decouples scheduling from execution.

---

# Step 3 - Compute nextRunTime

Suppose

Cron

```
0 9 * * *
```

Current execution

```
July 10

9 AM
```

Next execution

```
July 11

9 AM
```

Scheduler updates

```sql
UPDATE Jobs
SET nextRunTime='Tomorrow 9 AM'
```

The job is ready for its next cycle.

---

# Polling

The simplest scheduler repeatedly polls the database.

```
Every Second

↓

Find Due Jobs

↓

Sleep
```

Advantages

- Easy
- Reliable
- Simple implementation

---

# Problem With Polling

Suppose

```
Poll

9:00:00
```

Next poll

```
9:00:01
```

Job scheduled at

```
9:00:00.300
```

Will execute

```
9:00:01
```

Delay

```
700 ms
```

Polling interval affects latency.

---

# Reduce Polling Interval?

Suppose

Poll every

```
100 ms
```

Now

Database receives

```
10×

more queries
```

Latency improves.

Database load increases.

Trade-off.

---

# Delay Queue (Alternative)

Instead of polling,

maintain a priority queue ordered by

```
nextRunTime
```

```
Top

↓

9:00

↓

9:01

↓

9:05
```

Scheduler sleeps until

```
Top.nextRunTime
```

Then wakes up.

Benefits

- Lower DB load
- Better latency

Challenges

- Recovery after restart
- Keeping memory synchronized with DB
- Handling multiple scheduler instances

Many production systems still use DB polling because it is simpler and more reliable.

---

# Multiple Scheduler Instances

Suppose

```
Scheduler1

Scheduler2
```

Both execute

```sql
SELECT *
FROM Jobs
WHERE nextRunTime<=NOW();
```

Both find

```
Job101
```

Now

Worker receives

```
Execute Job101

Execute Job101
```

Duplicate execution.

Bad.

---

# Solution 1 - Row Locking

Use

```sql
SELECT ...
FOR UPDATE SKIP LOCKED;
```

Scheduler1 locks

```
Job101
```

Scheduler2 skips it.

No duplicate scheduling.

This works well with relational databases like PostgreSQL.

---

# Solution 2 - Distributed Lock

Before scheduling,

try to acquire a lock.

Example

```
Redis

SET job101 lock NX EX 30
```

Only one scheduler succeeds.

Others skip.

Useful when multiple schedulers share work.

---

# Solution 3 - Leader Election

Instead of allowing every scheduler to scan,

elect one leader.

```
Leader

↓

Find Jobs

↓

Queue
```

Other schedulers stay idle.

If leader crashes,

another scheduler becomes leader.

Leader election can be implemented using

- ZooKeeper
- etcd
- Kubernetes Lease API

---

# Which Approach Is Better?

| Approach | Pros | Cons |
|----------|------|------|
| Row Locking | Simple, DB managed | Higher DB dependency |
| Distributed Lock | Flexible | Extra infrastructure |
| Leader Election | Very efficient | Leader failover complexity |

In interviews,

mentioning **row locking** or **leader election** is usually sufficient.

---

# Scheduler Crash

Suppose

Scheduler crashes

before publishing

```
Job101
```

No problem.

The job still has

```
nextRunTime <= NOW()
```

Next scheduler picks it up.

No job lost.

---

# Crash After Publishing

Suppose

```
Publish

↓

Crash

↓

Before updating nextRunTime
```

Another scheduler may publish again.

Duplicate execution becomes possible.

How do we solve this?

---

# Idempotent Workers

Workers should safely ignore duplicate execution.

OR

Update job state atomically before publishing.

OR

Use a Transactional Outbox pattern.

We'll discuss these in the Fault Tolerance chapter.

---

# Scaling

Suppose

```
100 Million Jobs
```

One scheduler isn't enough.

Partition jobs.

Example

```
Shard1

↓

Scheduler1

Shard2

↓

Scheduler2

Shard3

↓

Scheduler3
```

Each scheduler owns one partition.

No overlap.

Excellent scalability.

---

# Complete Scheduler Flow

```
while(true)

↓

Find Due Jobs

↓

Acquire Lock

↓

Publish To Queue

↓

Compute Next Run

↓

Update Database

↓

Sleep
```

Repeat forever.

---

# Why Scheduler Doesn't Execute Jobs

Imagine

```
Generate Report

30 minutes
```

If Scheduler executes it,

no new jobs are discovered for 30 minutes.

Bad.

Instead

```
Scheduler

↓

Queue

↓

Workers
```

Scheduler remains free.

---

# Design Principles

The Scheduler should

- Be lightweight
- Be stateless
- Never execute business logic
- Publish quickly
- Recover easily
- Scale horizontally

---

# Interview Questions

## Why poll the database?

Simple, reliable, and easy to recover after failures.

---

## Why not execute jobs directly?

Execution blocks scheduling.

Separate scheduling from execution.

---

## Why LIMIT queries?

Avoid overwhelming workers and process jobs in manageable batches.

---

## How do multiple schedulers avoid duplicates?

Options include row-level locking (`FOR UPDATE SKIP LOCKED`), distributed locks, or leader election.

---

## What happens if the scheduler crashes?

No jobs are lost because the database remains the source of truth.

Another scheduler finds jobs whose `nextRunTime` has already passed.

---

## Why compute nextRunTime after execution?

Avoid parsing cron expressions on every polling cycle.

Cron is parsed only once per execution.

---

# Interview Summary

> The Scheduler Service continuously scans the database for active jobs whose `nextRunTime` has arrived. It does not execute the jobs itself; instead, it publishes execution requests to a message queue, allowing stateless workers to perform the actual work. After scheduling a recurring job, it computes and stores the next execution time, enabling future runs without repeatedly parsing cron expressions. In a distributed deployment, duplicate scheduling is prevented using techniques such as row-level locking, distributed locks, or leader election. Because the database remains the source of truth, scheduler failures do not result in lost jobs, and additional scheduler instances can be added to scale horizontally.

# Job Scheduler System Design

# Chapter 4 - Worker Service & Execution Engine

---

# Goal

The Scheduler decides

```
What should execute?
```

The Worker answers

```
How do we execute it?
```

Workers are responsible for

- Executing jobs
- Retrying failures
- Recording execution history
- Reporting success/failure
- Updating job state

Workers should be completely stateless.

---

# High-Level Architecture

```
              Scheduler

                   │

                   ▼

             Message Queue

      ┌────────────┼────────────┐

      ▼            ▼            ▼

  Worker1      Worker2      Worker3

      │            │            │

      ▼            ▼            ▼

 Execute Job   Execute Job   Execute Job

      │

      ▼

 Update Database
```

Notice

Workers never poll the database.

They simply consume execution requests from the queue.

---

# Job Execution Flow

Suppose

```
Daily Report

9:00 AM
```

Scheduler publishes

```
Execute Job101
```

Queue

↓

Worker2 receives

```
Job101
```

Worker performs

```
Load Job

↓

Execute Business Logic

↓

Record Result

↓

Acknowledge Queue
```

Done.

---

# Step 1 - Receive Job

Queue delivers

```json
{
    "jobId":101
}
```

Worker now loads

Job101

from the database.

Why?

Because the database is the source of truth.

The queue contains only

```
Execute Job101
```

---

# Step 2 - Execute Business Logic

Examples

```
Send Email

Generate Invoice

Cleanup Logs

Refresh Cache

Retry Payment
```

Worker calls

```
Job.execute(payload)
```

The Scheduler never knows what the job actually does.

---

# Step 3 - Update Execution History

Insert

JobExecution

| Job | Status |
|------|---------|
|101|SUCCESS|

or

|101|FAILED|

Store

- Start Time
- End Time
- Duration
- Worker ID
- Error Message

---

# Step 4 - Acknowledge Queue

Only after

successful processing

Worker acknowledges

```
ACK
```

Queue removes

```
Job101
```

---

# Why ACK Last?

Suppose

```
ACK

↓

Worker Crashes
```

before execution.

The job disappears forever.

Instead

```
Execute

↓

Store Result

↓

ACK
```

Now crashes are safe.

---

# Worker Crash

Suppose

Worker receives

```
Job101
```

and crashes.

Queue never receives

```
ACK
```

Queue automatically redelivers

```
Job101
```

to another Worker.

Example

```
Worker1

↓

Crash

↓

Worker2

↓

Execute
```

No job lost.

---

# Retry Logic

Suppose

SMTP server

is temporarily unavailable.

Worker fails.

Should we immediately mark the job failed?

No.

Retry.

---

# Retry Policy

Example

```
Attempts = 3

Delay = 30 sec
```

Flow

```
Attempt1

↓

Fail

↓

30 sec

↓

Attempt2

↓

Fail

↓

30 sec

↓

Attempt3

↓

Success
```

---

# Exponential Backoff

Instead of retrying every

```
30 sec
```

Use

```
30 sec

↓

60 sec

↓

120 sec

↓

240 sec
```

Benefits

- Less pressure
- Better recovery
- Avoids retry storms

Very common interview topic.

---

# Permanent Failures

Suppose

Email Address

doesn't exist.

Retrying

100 times

won't help.

Example

```
invalid@email
```

Retries waste resources.

Worker should detect

Permanent Failure

↓

Move to DLQ.

---

# Dead Letter Queue

Dead Letter Queue stores

jobs that exceeded retry limit.

Example

```
Job101

↓

Attempt1

Fail

↓

Attempt2

Fail

↓

Attempt3

Fail

↓

Dead Letter Queue
```

Operations can investigate later.

---

# JobExecution Table

Example

| Attempt | Status |
|----------|---------|
|1|FAILED|
|2|FAILED|
|3|SUCCESS|

History preserved.

---

# Idempotency

Very important interview topic.

Suppose

Worker crashes

after sending email

but before ACK.

Queue redelivers

```
Job101
```

Email sent twice.

Bad.

Workers should be

Idempotent.

---

# Example

Instead of

```
Send Email
```

blindly,

check

```
Already Sent?
```

If yes

Skip.

---

# Example

Payment Job

Bad

```
Charge Card

↓

Charge Again
```

Good

```
PaymentId

Already Processed?

↓

Skip
```

Same result.

---

# At-Least-Once Execution

Most queues provide

```
At Least Once
```

Jobs may execute twice.

Workers must handle duplicates safely.

---

# Exactly Once?

Can we guarantee

```
Exactly Once
```

Not usually.

Instead

We build

Idempotent Workers.

That's the industry standard.

---

# Long Running Jobs

Suppose

Video Processing

takes

```
45 Minutes
```

Worker shouldn't lose track.

Worker periodically updates

```
Heartbeat
```

Database

```
Running

45%

Completed

LastHeartbeat
```

Useful for monitoring.

---

# Worker Scaling

Suppose

```
1000 jobs/sec
```

One worker handles

```
100 jobs/sec
```

Need

```
10 Workers
```

Simply add more.

Queue distributes work automatically.

```
Queue

↓

Worker1

Worker2

Worker3

...

Worker10
```

Horizontal scaling.

---

# Failure Scenarios

## Worker crashes

Queue redelivers.

---

## Queue crashes

Persistent queues recover messages.

---

## Database unavailable

Worker retries later.

---

## External API unavailable

Retry with exponential backoff.

---

## Permanent failure

Move to Dead Letter Queue.

---

# Worker Responsibilities

Workers should

- Execute jobs
- Retry failures
- Record execution history
- Report status
- ACK queue
- Handle duplicate execution

Workers should NOT

- Schedule jobs
- Parse cron
- Find due jobs

Those belong to the Scheduler.

---

# Complete Execution Flow

```
Scheduler

↓

Queue

↓

Worker

↓

Load Job

↓

Execute

↓

Record History

↓

Update Next Run

↓

ACK Queue
```

---

# Design Principles

Workers should be

- Stateless
- Idempotent
- Horizontally scalable
- Failure tolerant

Keep business logic separate from scheduling.

---

# Interview Questions

## Why use workers?

To separate scheduling from execution and allow independent scaling.

---

## Why ACK after execution?

Prevents job loss if a worker crashes.

---

## Why retries?

Many failures are temporary, such as network issues or unavailable services.

---

## Why exponential backoff?

Reduces load on failing dependencies and avoids retry storms.

---

## Why Dead Letter Queue?

Prevents permanently failing jobs from blocking the system while preserving them for investigation.

---

## Why idempotency?

Queues typically provide at-least-once delivery, so duplicate execution is always possible.

Workers must safely handle retries.

---

## Can we guarantee exactly-once execution?

Usually no.

The practical solution is

- At-least-once delivery
- Idempotent workers

This provides effectively-once behavior for most business operations.

---

# Interview Summary

> Workers are stateless execution engines that consume execution requests from a message queue. After receiving a job, a worker loads the latest job definition from the database, executes the business logic, records the execution result, and acknowledges the queue only after successful processing. If a worker crashes before acknowledging the message, the queue redelivers the job to another worker, providing at-least-once execution. To safely handle duplicate deliveries, workers are designed to be idempotent. Temporary failures are retried using exponential backoff, while permanently failing jobs are moved to a Dead Letter Queue for later investigation. This design keeps scheduling and execution independent, enabling reliable and horizontally scalable job processing.

# Job Scheduler System Design

# Chapter 5 - Scaling & High Availability

---

# Goal

A scheduler running on one machine works for small systems.

But what happens when we have

- 100 Million Jobs
- 50,000 Executions/sec
- Multiple Datacenters
- Server Failures

We need a distributed scheduler.

---

# High-Level Architecture

```
                Clients

                   │

                   ▼

              Job Service

                   │

                   ▼

             Job Database

                   ▲

                   │

        ┌──────────┼──────────┐

        ▼          ▼          ▼

   Scheduler1 Scheduler2 Scheduler3

                   │

             Message Queue

        ┌──────────┼──────────┐

        ▼          ▼          ▼

      Worker1   Worker2   Worker3
```

Notice

There are

- Multiple Schedulers
- Multiple Workers

Everything is horizontally scalable.

---

# Why Multiple Schedulers?

Suppose one Scheduler crashes.

```
Scheduler

↓

CRASH
```

No jobs are scheduled.

Bad.

Instead

```
Scheduler1

Scheduler2

Scheduler3
```

If one crashes,

others continue.

---

# The Duplicate Scheduling Problem

Suppose

Scheduler1

and

Scheduler2

execute

```sql
SELECT *
FROM Jobs
WHERE nextRunTime <= NOW();
```

Both receive

```
Job101
```

Both publish

```
Execute Job101
```

Workers execute twice.

Bad.

---

# Solution 1 - Row-Level Locking

PostgreSQL supports

```sql
SELECT *
FROM Jobs
WHERE status='ACTIVE'
AND nextRunTime <= NOW()
FOR UPDATE SKIP LOCKED
LIMIT 1000;
```

What happens?

Scheduler1 locks

```
Job101
```

Scheduler2 skips it.

Only one Scheduler publishes it.

---

# Why SKIP LOCKED?

Without

```
SKIP LOCKED
```

Scheduler2 waits.

Waiting reduces throughput.

Instead

Skip locked rows.

Continue working.

---

# Solution 2 - Leader Election

Instead of

three schedulers polling,

elect

one Leader.

```
Leader Scheduler

↓

Poll Database

↓

Publish Queue
```

Other schedulers

```
Standby
```

If leader crashes,

another Scheduler becomes Leader.

---

# Leader Election Technologies

Common choices

- ZooKeeper
- etcd
- Kubernetes Lease
- Consul

These systems coordinate

```
Who is Leader?
```

---

# Advantages

Very little duplicate work.

Only one Scheduler polls.

---

# Disadvantages

Leader becomes bottleneck.

Needs failover.

---

# Solution 3 - Sharding

Instead of

one Scheduler

polling

100 Million jobs,

divide ownership.

Example

```
Jobs

1-25M

↓

Scheduler1

------------------

25M-50M

↓

Scheduler2

------------------

50M-75M

↓

Scheduler3

------------------

75M-100M

↓

Scheduler4
```

Each Scheduler owns

its partition.

No overlap.

---

# Sharding Strategies

## By JobId

```
JobId % NumberOfShards
```

Simple.

Even distribution.

---

## By Tenant

Large SaaS systems

```
CompanyA

↓

Shard1

CompanyB

↓

Shard2
```

Useful for multi-tenant isolation.

---

# Why Not Shard By nextRunTime?

Looks tempting.

Example

```
Today

↓

Shard1

Tomorrow

↓

Shard2
```

Problem

Every execution changes

```
nextRunTime
```

The row moves

between shards.

Very expensive.

Avoid.

---

# Scaling Workers

Workers are stateless.

Need more throughput?

Just add more.

```
Queue

↓

Worker1

Worker2

Worker3

...

Worker100
```

Queue automatically balances work.

---

# Scaling Queue

One queue may become a bottleneck.

Partition it.

Example

```
Jobs Topic

Partition0

Partition1

Partition2
```

Multiple workers consume

different partitions.

---

# Scaling Database

Eventually

one database isn't enough.

Approaches

### Read Replicas

Used for

Monitoring

Reporting

History

---

### Write Sharding

Split jobs

across databases.

```
Shard1

Shard2

Shard3
```

Each Scheduler owns one shard.

---

# Scheduler Ownership

A common production design.

```
Shard1

↓

Scheduler1

----------------

Shard2

↓

Scheduler2

----------------

Shard3

↓

Scheduler3
```

Each Scheduler

only polls

its own shard.

No locking required.

Very scalable.

---

# Scheduler Crash

Suppose

Scheduler2 crashes.

Who schedules

Shard2?

Answer

Coordinator

reassigns

Shard2

↓

Scheduler1

Now Scheduler1 owns

two shards.

---

# Rebalancing

Suppose

new Scheduler joins.

```
Before

Scheduler1

Scheduler2

Scheduler3

After

Scheduler1

Scheduler2

Scheduler3

Scheduler4
```

Coordinator

redistributes

shards.

Load becomes balanced.

---

# Queue Backpressure

Suppose

Workers

can't keep up.

Queue grows.

```
Scheduler

↓

100,000 Jobs

↓

Queue

↓

Workers

↓

10,000 Jobs/sec
```

Backlog increases.

---

# Solution

Scheduler checks

queue depth.

If queue exceeds threshold,

slow down publishing.

This prevents overwhelming workers.

---

# Multi-Datacenter

Large companies deploy

```
US-East

US-West

Europe

Asia
```

Schedulers typically operate

within their own region.

Cross-region scheduling is used

only for disaster recovery.

---

# Failure Scenarios

## Scheduler crashes

Another Scheduler takes ownership.

---

## Worker crashes

Queue redelivers.

---

## Queue crashes

Persistent queue recovers messages.

---

## Database crashes

Replica promoted.

---

## Entire Region fails

Traffic shifts

to another region.

---

# Scaling Summary

| Component | Scaling Strategy |
|-----------|------------------|
| Scheduler | Multiple instances + locking or shard ownership |
| Workers | Horizontal scaling |
| Queue | Partitioning |
| Database | Sharding + Replicas |
| Job Service | Stateless horizontal scaling |

---

# Design Principles

Every component

scales independently.

Schedulers

↓

Schedule

Workers

↓

Execute

Queue

↓

Buffer

Database

↓

Persist

---

# Interview Questions

## Why multiple Schedulers?

High availability.

No single point of failure.

---

## How do multiple Schedulers avoid duplicates?

Options include

- Row-level locking
- Distributed locks
- Leader election
- Shard ownership

---

## Which approach do you recommend?

For most production systems:

- Small scale: `FOR UPDATE SKIP LOCKED`
- Very large scale: **Shard ownership**

Shard ownership avoids lock contention and scales much better.

---

## Why scale Workers separately?

Scheduling and execution have different workloads.

Scheduling is lightweight.

Execution may take minutes.

---

## Why queue partitioning?

Allows multiple workers to consume in parallel.

---

## What happens if one Scheduler dies?

Another Scheduler takes ownership of its shard.

No jobs are lost because the database is the source of truth.

---

## How do you prevent queue overload?

Monitor queue depth and apply backpressure by reducing the scheduling rate or temporarily limiting the number of jobs published.

---

# Recommended Production Architecture

```
                   Clients

                      │

                 Job Service

                      │

                 Job Database

                      │

      ┌───────────────┼───────────────┐

      ▼               ▼               ▼

 Scheduler1      Scheduler2      Scheduler3

   (Shard1)       (Shard2)       (Shard3)

                      │

                 Kafka / Queue

      ┌───────────────┼───────────────┐

      ▼               ▼               ▼

   Worker1        Worker2        Worker3

                      │

             External Systems

     Email • Payments • Reports • APIs
```

---

# Interview Summary

> To support millions of scheduled jobs, the scheduler must scale horizontally while preventing duplicate scheduling. Multiple Scheduler instances are deployed for high availability, but they must coordinate ownership using techniques such as row-level locking (`FOR UPDATE SKIP LOCKED`), leader election, or—at very large scale—shard ownership, where each Scheduler is responsible for a subset of jobs. Workers remain stateless and scale independently by consuming execution requests from a partitioned message queue. The database is sharded as the system grows, and schedulers are reassigned automatically if one fails. This architecture provides high throughput, fault tolerance, and avoids duplicate execution while allowing each layer to scale independently.

# Job Scheduler System Design

# Chapter 6 - Fault Tolerance & Failure Handling

---

# Goal

Failures are inevitable.

- Servers crash
- Workers die
- Databases become unavailable
- Queues go down
- Networks fail

A production Job Scheduler should

- Never lose jobs
- Minimize duplicate execution
- Recover automatically
- Continue processing

This chapter discusses the most common failure scenarios.

---

# Design Principles

Before discussing failures, remember four rules.

## Rule 1

Database is the Source of Truth.

---

## Rule 2

Workers are Stateless.

---

## Rule 3

Queue provides At-Least-Once delivery.

---

## Rule 4

Workers must be Idempotent.

These four rules solve most failure scenarios.

---

# Failure 1

## Scheduler Crashes Before Publishing

Flow

```
Scheduler

↓

Find Job101

↓

CRASH
```

Notice

The database still contains

```
nextRunTime <= NOW()
```

Nothing has changed.

Another Scheduler starts.

Runs

```sql
SELECT *
FROM Jobs
WHERE nextRunTime <= NOW();
```

Finds

```
Job101
```

Publishes it.

No job is lost.

---

# Failure 2

## Scheduler Crashes After Publishing

Flow

```
Find Job

↓

Publish Queue

↓

CRASH

↓

Before Updating nextRunTime
```

Now another Scheduler sees

```
nextRunTime <= NOW()
```

Publishes again.

Duplicate execution.

---

# Solutions

Option 1

Workers are idempotent.

---

Option 2

Use a Transactional Outbox.

---

Option 3

Update scheduling state atomically before publishing.

---

# Transactional Outbox

Instead of

```
Publish Queue

↓

Update DB
```

Store

```
Job

+

Outbox Event
```

inside one database transaction.

Outbox

| Event | Published |
|--------|-----------|
|ExecuteJob101|No|

Background Publisher

↓

Queue

↓

Published = Yes

No execution requests are lost.

---

# Failure 3

## Worker Crashes Before Execution

Queue

↓

Worker receives

↓

CRASH

No ACK.

Queue waits.

Visibility timeout expires.

Queue redelivers.

No job lost.

---

# Failure 4

## Worker Crashes During Execution

Suppose

```
Charge Credit Card

↓

Worker crashes
```

Queue redelivers.

Another Worker executes again.

Potential duplicate charge.

---

# Solution

Workers must be Idempotent.

Instead of

```
Charge Card
```

blindly,

check

```
Payment already processed?
```

If yes

Skip.

---

# Failure 5

## Worker Crashes After Execution But Before ACK

Flow

```
Execute

↓

Store Success

↓

CRASH

↓

Before ACK
```

Queue thinks

job wasn't processed.

Redelivers.

Duplicate execution.

Again,

Idempotency solves this.

---

# Failure 6

## Queue Failure

Modern queues

Kafka

RabbitMQ

SQS

persist messages.

If one broker crashes,

replicas recover.

Consumers continue.

---

# Failure 7

## Database Failure

Database is the source of truth.

Production setup

```
Primary

↓

Replica1

Replica2
```

Primary crashes.

Replica promoted.

Schedulers reconnect.

Jobs continue.

---

# Failure 8

## Scheduler Clock Drift

Suppose

Scheduler1

```
9:00:00
```

Scheduler2

```
8:59:55
```

Different clocks.

One Scheduler executes

too early.

Another

too late.

---

# Solution

Synchronize clocks using

```
NTP
```

or rely on

database time

instead of local machine time.

Example

```sql
SELECT NOW();
```

instead of

```java
LocalDateTime.now();
```

---

# Failure 9

## Long Running Job

Suppose

Video Processing

takes

```
2 Hours
```

How do we know

Worker is alive?

---

# Heartbeats

Worker periodically updates

```
LastHeartbeat

↓

Every 30 seconds
```

Database

| Job | Status | Heartbeat |
|------|---------|-----------|
|Video|RUNNING|10:15|

If heartbeat stops

Coordinator assumes

Worker failed.

---

# Failure 10

## External Service Failure

Example

SMTP

Payment API

Third-party REST API

temporarily unavailable.

Retry.

Use

Exponential Backoff

```
30 sec

↓

60 sec

↓

120 sec

↓

240 sec
```

Avoid retry storms.

---

# Failure 11

## Permanent Failure

Example

```
Invalid Email

Wrong API Key

Malformed Payload
```

Retrying won't help.

Move to

Dead Letter Queue.

Operations investigates later.

---

# Failure 12

## Duplicate Scheduling

Suppose

Scheduler1

Scheduler2

both find

Job101.

---

# Solution

Use

```
FOR UPDATE SKIP LOCKED
```

or

Leader Election

or

Shard Ownership.

Only one Scheduler owns the job.

---

# Failure 13

## Queue Backlog

Suppose

Workers

process

```
500 Jobs/sec
```

Scheduler publishes

```
5000 Jobs/sec
```

Queue grows indefinitely.

---

# Solution

Backpressure.

Scheduler checks

Queue Depth.

If queue exceeds threshold,

reduce publishing rate.

---

# Failure 14

## Poison Job

Suppose

Job always crashes

```
Attempt1

↓

Fail

↓

Retry

↓

Fail

↓

Retry

↓

Fail
```

Without limits,

the system retries forever.

Bad.

---

# Solution

Retry Policy

```
Max Attempts = 5
```

After that

↓

Dead Letter Queue.

---

# Failure 15

## Scheduler Restart

Scheduler crashes.

Restarts.

Question

How does it know

where to continue?

Simple.

Database.

Scheduler always queries

```
nextRunTime <= NOW()
```

No in-memory recovery required.

---

# At-Least-Once Execution

Most production schedulers guarantee

```
At Least Once
```

because

Worker crashes

can always happen.

Exactly-once execution

across distributed systems

is extremely difficult.

---

# Exactly Once?

Possible only if

- Queue
- Database
- External Systems

all participate in one distributed transaction.

This is impractical.

Industry approach

```
At Least Once

+

Idempotent Workers
```

---

# Monitoring

Track

- Queue Depth
- Scheduler Lag
- Failed Jobs
- Retry Count
- DLQ Size
- Worker Heartbeats

These metrics help detect failures early.

---

# Recovery Summary

| Failure | Recovery |
|----------|----------|
| Scheduler crashes before publish | Another Scheduler republishes |
| Scheduler crashes after publish | Idempotent workers / Outbox |
| Worker crashes before ACK | Queue redelivers |
| Worker crashes after execution | Idempotent execution |
| Queue failure | Replicated queue |
| Database failure | Replica promotion |
| Clock drift | NTP / DB time |
| Long-running job | Heartbeats |
| External API failure | Exponential backoff |
| Permanent failure | Dead Letter Queue |

---

# Design Principles

Reliable schedulers follow these rules

- Database is the source of truth.
- Queue provides durable delivery.
- Workers are stateless.
- Workers are idempotent.
- Retry only transient failures.
- Use DLQ for permanent failures.
- Never rely on in-memory state.

---

# Interview Questions

## What happens if the Scheduler crashes?

Another Scheduler scans the database and finds jobs whose `nextRunTime` has already passed.

---

## What happens if a Worker crashes?

The queue redelivers the job after the visibility timeout expires.

---

## Why are Workers idempotent?

Because queues typically provide at-least-once delivery, duplicate execution is possible.

---

## How do you prevent duplicate scheduling?

Use row-level locking, distributed locks, leader election, or shard ownership.

---

## Why use a Dead Letter Queue?

To isolate permanently failing jobs without blocking normal processing.

---

## Why use heartbeats?

To detect failed workers processing long-running jobs.

---

## Can you guarantee exactly-once execution?

In practice, no.

The common production approach is:

- At-least-once delivery
- Idempotent workers

This provides effectively-once business behavior.

---

# Interview Summary

> A reliable Job Scheduler assumes failures will occur and is designed to recover automatically. The database remains the source of truth for job definitions, while the queue provides durable at-least-once delivery. If a Scheduler crashes, another Scheduler can rediscover due jobs by querying `nextRunTime`. If a Worker crashes before acknowledging a job, the queue redelivers it to another Worker. Because duplicate execution is possible, Workers must be idempotent. Temporary failures are handled through retries with exponential backoff, while permanently failing jobs are moved to a Dead Letter Queue. Long-running jobs use heartbeats to detect failed Workers, and production deployments rely on replicated databases, durable queues, and monitoring to achieve high availability.


# Job Scheduler System Design

# Chapter 7 - Complete Interview Walkthrough (10–15 Minutes)

---

# Interview Strategy

One common mistake candidates make is explaining every component separately.

Instead,

tell the story of **one scheduled job**.

Suppose the user creates

```
Run Daily Report

Every day at 9:00 AM
```

Now explain how this job flows through the system.

Everything naturally appears.

---

# Minute 1

## Clarify Requirements

I'd first clarify the requirements.

### Functional

- Create Job
- Delete Job
- Pause Job
- Resume Job
- One-time Jobs
- Recurring Jobs
- Cron Expressions
- Retry Failed Jobs
- Execution History

### Non Functional

- High Availability
- Scalability
- Reliability
- Low Latency
- No Lost Jobs
- Prevent Duplicate Execution

---

# Minute 2

## APIs

Main APIs

```
POST /jobs

PUT /jobs/{id}

DELETE /jobs/{id}

POST /jobs/{id}/pause

POST /jobs/{id}/resume

GET /jobs/{id}
```

These APIs only manage jobs.

They do not execute jobs.

---

# Minute 3

## High-Level Architecture

```
Client

↓

Job Service

↓

Job Database

↓

Scheduler

↓

Queue

↓

Workers
```

Quick explanation

### Job Service

Stores job definitions.

### Database

Source of truth.

### Scheduler

Finds jobs that should execute.

### Queue

Buffers execution requests.

### Workers

Execute business logic.

---

# Minute 4-6

## Walk Through One Job

Suppose the user creates

```
Daily Report

Every day at 9 AM
```

### Step 1

Client calls

```
POST /jobs
```

Job Service validates

and stores

```
Cron

Payload

Status

NextRunTime
```

in the Jobs table.

Nothing executes yet.

---

### Step 2

At

```
9:00 AM
```

Scheduler polls

```sql
SELECT *
FROM Jobs
WHERE status='ACTIVE'
AND nextRunTime<=NOW()
ORDER BY nextRunTime
LIMIT 1000;
```

Returns

```
Job101
```

---

### Step 3

Scheduler publishes

```
Execute Job101
```

to Kafka (or RabbitMQ/SQS).

It does not execute the job itself.

---

### Step 4

Worker receives

```
Job101
```

Loads

latest job definition

from the database.

Executes

```
Generate Report
```

---

### Step 5

Worker inserts

JobExecution

```
SUCCESS
```

or

```
FAILED
```

history.

---

### Step 6

Scheduler computes

next execution

```
Tomorrow

9 AM
```

Updates

```
nextRunTime
```

Job is ready for the next cycle.

---

# Minute 7

## Database Design

Main tables

```
Jobs

↓

JobExecutions

↓

DeadLetterQueue
```

Important optimization

Store

```
nextRunTime
```

instead of parsing cron every second.

Create an index on

```
(status, nextRunTime)
```

to efficiently find due jobs.

---

# Minute 8

## Scaling

Workers

↓

Stateless

Add more workers.

Scheduler

↓

Multiple instances

Avoid duplicate scheduling using

- Row-level locking (`FOR UPDATE SKIP LOCKED`)
- Leader election
- Shard ownership

Database

↓

Shard by

```
JobId

or

TenantId
```

Queue

↓

Partition for parallel processing.

---

# Minute 9

## Failure Handling

Scheduler crashes

↓

Another Scheduler scans

```
nextRunTime <= NOW
```

Worker crashes

↓

Queue redelivers

No ACK

↓

Retry

Permanent failures

↓

Dead Letter Queue

Workers

↓

Idempotent

---

# Minute 10

## Tradeoffs

Why Queue?

Decouples scheduling from execution.

Why nextRunTime?

Avoid parsing cron every second.

Why Workers?

Execution may take minutes.

Scheduling must remain lightweight.

Why JobExecution table?

Maintain execution history.

Why Database?

Source of truth.

---

# Whiteboard Diagram

```
                 Client

                    │

                    ▼

              Job Service

                    │

                    ▼

              Job Database

                    ▲

                    │

      ┌─────────────┼─────────────┐

      ▼             ▼             ▼

 Scheduler1    Scheduler2    Scheduler3

                    │

             Kafka / RabbitMQ

                    │

      ┌─────────────┼─────────────┐

      ▼             ▼             ▼

    Worker1      Worker2      Worker3

                    │

             External Systems

     Email • Payments • Reports • APIs
```

---

# Design Principles

The architecture follows a few simple principles.

### Separation of Concerns

Scheduler decides

Workers execute

---

### Durability

Database stores

job definitions

execution history

retry metadata

---

### Asynchronous Processing

Queue decouples

Scheduler

and

Workers.

---

### Horizontal Scaling

Schedulers

Workers

Queue

Database

all scale independently.

---

# Common Follow-up Questions

## Why not let Scheduler execute jobs?

Long-running jobs would block scheduling.

Keep Scheduler lightweight.

---

## Why store nextRunTime?

Avoid parsing millions of cron expressions every second.

---

## Why use a Queue?

Provides buffering, retries, and independent scaling.

---

## Why Workers are stateless?

Easy horizontal scaling and recovery.

---

## Why JobExecution table?

Execution history, monitoring, debugging, and auditing.

---

## How do multiple Schedulers avoid duplicate execution?

Small scale

```
FOR UPDATE SKIP LOCKED
```

Large scale

```
Shard Ownership
```

Leader election is another valid approach.

---

## How do you recover from crashes?

Database is the source of truth.

Queue provides durable delivery.

Workers are idempotent.

Schedulers can rediscover overdue jobs.

---

# Final Interview Summary

> I'd design the system with four major components: a Job Service for managing jobs, a Scheduler Service that continuously finds due jobs, a durable queue that decouples scheduling from execution, and stateless Workers that execute the jobs. When a user creates a job, the Job Service stores the job definition, cron expression, payload, and a precomputed `nextRunTime` in the database. The Scheduler periodically queries for active jobs whose `nextRunTime` has arrived, publishes execution requests to the queue, and updates the next scheduled execution time. Workers consume these requests, execute the business logic, record execution history, and acknowledge the queue only after successful processing. Multiple Scheduler instances coordinate using row-level locking, leader election, or shard ownership to prevent duplicate scheduling, while Workers remain idempotent because the queue provides at-least-once delivery. The database serves as the durable source of truth, allowing the system to recover from failures without losing jobs while scaling horizontally to millions of scheduled tasks.	

# Job Scheduler - Advanced Topics

# 1. Production Retry Architecture

In the basic design, retries can be handled by updating `nextRunTime` and letting the Scheduler execute the job again.

However, in a large-scale production system, it is better to separate **recurring scheduling** from **retry processing**.

The Scheduler should only be responsible for scheduling normal job executions.

Workers should own retry logic.

---

## Why separate retries?

A recurring schedule and a retry are fundamentally different.

Example

```
Daily Report

Runs every day at 9:00 AM
```

Suppose today's execution fails.

The next scheduled execution is still

```
Tomorrow 9:00 AM
```

The retry should happen

```
30 seconds later
```

Mixing these two concepts by updating `nextRunTime` makes the design harder to reason about.

Instead, keep

- Scheduled executions
- Retry executions

completely independent.

---

# Execution Flow

```
              Scheduler

                   │

                   ▼

          task_execution Topic

                   │

                   ▼

                 Worker

                   │

          SUCCESS ? ──────────────► Update Execution History

            │

            NO

            ▼

      Is Retryable?

      │             │

     YES            NO

      │             │

      ▼             ▼

 Retry Topic      Dead Letter Queue
```

---

# Step-by-Step Retry Flow

## Step 1

Scheduler finds

```
Job101
```

Publishes

```json
{
   "jobId":101
}
```

to

```
task_execution
```

---

## Step 2

Worker consumes

```
Job101
```

Loads latest job definition from the database

Executes the business logic.

---

## Step 3

Suppose execution fails.

Worker records

```
Attempt = 1

Status = FAILED
```

in

```
TaskExecutionHistory
```

---

## Step 4

Worker checks retry policy.

Example

```
Maximum Retry = 3

Backoff = Exponential
```

If retryable,

Worker publishes

```
Job101
```

to

```
task_retry_30s
```

The Scheduler is NOT involved.

---

## Step 5

Retry Consumer listens to

```
task_retry_30s
```

Waits

```
30 seconds
```

Consumes the message

Executes the job again.

---

## Step 6

If execution fails again,

Worker updates

```
Attempt = 2
```

Publishes to

```
task_retry_5m
```

---

## Step 7

Retry Consumer waits

```
5 minutes
```

Executes again.

---

## Step 8

If all retries fail,

Worker updates

```
Status = FAILED
```

Publishes to

```
task_dlq
```

Operations team can inspect the failed task later.

---

# Why Multiple Retry Topics?

Instead of one retry queue

```
Retry Queue

30 sec

5 min

30 min

2 hours
```

use multiple retry topics

```
task_retry_30s

task_retry_5m

task_retry_30m
```

Benefits

- Different delay policies
- Better scalability
- Long-delay retries do not block short-delay retries
- Independent consumers for each retry stage

---

# Execution History

Jobs Table

Stores

- Job Definition
- Cron
- Payload
- Next Scheduled Run

TaskExecutionHistory

Stores

- ExecutionId
- Attempt Number
- Status
- Start Time
- End Time
- Error Message

Example

| Execution | Attempt | Status |
|------------|----------|---------|
|5001|1|FAILED|
|5001|2|FAILED|
|5001|3|SUCCESS|

This preserves the recurring schedule while tracking every retry attempt.

---

# Production Retry Architecture

```
                    Scheduler

                         │

                         ▼

                 task_execution

                         │

                         ▼

                     Worker Pool

          SUCCESS? ───────────────► COMPLETED

              │

              ▼

        Retryable?

              │

              ▼

       task_retry_30s

              │

        Retry Consumer

              │

        SUCCESS? ───────────────► COMPLETED

              │

              ▼

       task_retry_5m

              │

        Retry Consumer

              │

        SUCCESS? ───────────────► COMPLETED

              │

              ▼

             DLQ
```

---

# Why this design?

Advantages

- Scheduler remains simple.
- Retries are independent of scheduling.
- Supports different retry policies.
- Easy to scale.
- No need to modify recurring schedules during retries.

---

# Interview Answer

> In a production scheduler, I separate recurring scheduling from retry processing. The Scheduler only publishes scheduled executions. If a Worker fails, it evaluates the retry policy and publishes the job to retry topics with increasing delays. Dedicated retry consumers handle subsequent attempts. If all retries are exhausted, the job is moved to a Dead Letter Queue. This keeps the Scheduler simple, prevents retries from affecting recurring schedules, and allows retry infrastructure to scale independently.

---

# 2. Scaling the Scheduler with a Coordinator

As the number of scheduled jobs grows, a single Scheduler becomes a bottleneck.

We deploy multiple Scheduler instances.

The challenge becomes preventing duplicate scheduling.

---

# Small-Scale Solution

All Scheduler instances poll the database.

```
               Jobs DB

                  ▲

       ┌──────────┼──────────┐

       │          │          │

 Scheduler1  Scheduler2  Scheduler3
```

Each executes

```sql
SELECT ...
FOR UPDATE SKIP LOCKED
LIMIT 1000;
```

The database ensures only one Scheduler locks and schedules a given job.

Advantages

- Simple
- Easy to implement

Disadvantages

- Lock contention
- Every Scheduler scans the same table
- Doesn't scale well to very large deployments

---

# Large-Scale Production Solution

Instead of every Scheduler competing for all jobs,

introduce a lightweight Coordinator.

The Coordinator does NOT schedule jobs.

It only manages ownership.

```
             Coordinator

                  │

      ┌───────────┼───────────┐

      ▼           ▼           ▼

 Scheduler1  Scheduler2  Scheduler3

      │           │           │

    Shard1      Shard2      Shard3
```

Each Scheduler owns one or more database shards.

Scheduler1 only queries

```
Shard1
```

Scheduler2 only queries

```
Shard2
```

No overlap.

No duplicate scheduling.

No row locking required.

---

# Coordinator Responsibilities

The Coordinator maintains

| Scheduler | Assigned Shards |
|------------|-----------------|
|Scheduler1|Shard1|
|Scheduler2|Shard2|
|Scheduler3|Shard3|

Responsibilities

- Assign shard ownership
- Receive Scheduler heartbeats
- Detect Scheduler failures
- Reassign shards
- Rebalance when new Schedulers join

The Coordinator never assigns individual jobs.

It only assigns ownership of partitions.

---

# Scheduler Failure

Suppose

```
Scheduler2
```

crashes.

Coordinator detects

```
Heartbeat Missing
```

Reassigns

```
Shard2

↓

Scheduler1
```

Now Scheduler1 owns

```
Shard1

+

Shard2
```

No jobs are lost because the Jobs table remains the source of truth.

---

# New Scheduler Joins

Suppose

```
Scheduler4
```

starts.

Coordinator rebalances ownership.

Before

```
S1 → Shard1

S2 → Shard2

S3 → Shard3
```

After

```
S1 → Shard1

S2 → Shard2

S3 → Half of Shard3

S4 → Half of Shard3
```

(or assigns newly created shards).

---

# Why Assign Shards Instead of Batches?

The Coordinator should **not** assign individual batches of jobs.

Bad

```
Coordinator

↓

Scheduler

↓

Batch

↓

Ask Again
```

Problems

- Coordinator becomes a bottleneck.
- High network overhead.
- Constant coordination.

Instead

Coordinator assigns

```
Ownership
```

Each Scheduler continuously executes

```sql
SELECT *
FROM Jobs_SharedShard
WHERE nextRunTime <= NOW()
LIMIT 1000;
```

The Scheduler decides batching.

The Coordinator only decides ownership.

This is the same model used by Kafka Consumer Groups.

---

# Production Scheduler Architecture

```
                  Job Service

                       │

                  Jobs Database

         Shard1     Shard2     Shard3

            ▲          ▲          ▲

            │          │          │

      Scheduler1 Scheduler2 Scheduler3

                 ▲

                 │

           Coordinator

       (Shard Ownership)

                 │

            Heartbeats

                 ▼

             Kafka Queue

                 ▼

             Worker Pool
```

---

# Interview Answer

> For smaller deployments, multiple Scheduler instances can safely poll the database using `SELECT ... FOR UPDATE SKIP LOCKED`, allowing the database to prevent duplicate scheduling. At larger scale, I would introduce a lightweight Coordinator responsible only for shard ownership. Each Scheduler owns one or more database shards and independently polls only those shards for due jobs. The Coordinator monitors Scheduler heartbeats, detects failures, reassigns shards, and rebalances ownership as new Scheduler instances join. This eliminates lock contention, avoids duplicate scheduling, and allows the Scheduler layer to scale horizontally.


# Job Scheduler - Final Production Design

> This is the final design I would present in a Senior Backend/System Design interview. It is simple, scalable, and keeps each component focused on a single responsibility.

---

# High Level Architecture

```
                    Client

                       │

                Job Service APIs

                       │

                       ▼

                   Jobs Table
             (Recurring Job Definitions)

                       │
             Creates First Execution

                       ▼

              TaskExecution Table
         (Pending Executions to Schedule)

                       ▲
                       │
                Scheduler Polls
                       │
                       ▼

                 Execution Topic
                     (Kafka)

                       │

                 Worker Pool

                       │

        ┌──────────────┴──────────────┐

        ▼                             ▼

   Execution Success           Execution Failed

        │                             │

        ▼                             ▼

Create Next                Retry Allowed?
Recurring Execution              │
                                 │
                       ┌─────────┴──────────┐
                       ▼                    ▼

            Create Retry Execution     Publish to DLQ

```

---

# Responsibilities

## Job Service

Responsible for

- Creating jobs
- Updating jobs
- Deleting jobs
- Pausing jobs
- Resuming jobs

When a new recurring job is created, the Job Service computes the first execution time and inserts the first TaskExecution row.

---

## Scheduler

The Scheduler is intentionally very lightweight.

Its only responsibility is

```
Find pending executions whose scheduled time has arrived.

Publish them to Kafka.
```

Scheduler **does not**

- execute jobs
- compute retries
- calculate exponential backoff
- understand business logic

Scheduler simply runs

```sql
SELECT *
FROM TaskExecution
WHERE status='PENDING'
AND scheduledTime <= NOW()
LIMIT 1000;
```

Marks them

```
IN_PROGRESS
```

Publishes

```
ExecutionId
```

to Kafka.

---

## Worker

Worker owns the entire execution lifecycle.

It

- Loads Job definition
- Executes business logic
- Updates execution status
- Computes retries
- Computes exponential backoff
- Creates retry executions
- Creates next recurring execution after success

Workers understand the retry policy.

Schedulers do not.

---

# Database Design

Only **two business tables** are required.

---

# 1. Jobs Table

Stores recurring job definitions.

| Column | Description |
|---------|-------------|
| JobId | Primary Key |
| JobName | Human-readable name |
| CronExpression | Cron schedule |
| Payload | Input required to execute the job |
| RetryPolicy | Max retries, base delay, backoff strategy |
| Status | ACTIVE / PAUSED / DELETED |

Example

| JobId | JobName | Cron | RetryPolicy |
|-------|----------|------|-------------|
|101|Daily Report|0 9 * * *|3 retries|

The Jobs table is a template.

It does **not** track execution state.

---

# 2. TaskExecution Table

Every execution gets one row.

This includes

- First execution
- Retry executions
- Future recurring executions

| Column | Description |
|---------|-------------|
| ExecutionId | Primary Key |
| JobId | FK to Jobs |
| Attempt | Retry attempt number |
| ScheduledTime | When this execution should run |
| Status | PENDING / IN_PROGRESS / COMPLETED / FAILED |
| StartedAt | Actual execution start time |
| CompletedAt | Completion time |
| ErrorMessage | Failure reason |

Example

| ExecutionId | JobId | Attempt | ScheduledTime | Status |
|-------------|-------|---------|---------------|--------|
|5001|101|1|Today 9:00|PENDING|

The Scheduler polls **only this table**.

---

# Why don't we need TaskExecutionHistory?

Initially it seems useful.

But TaskExecution already stores

- every execution
- every retry
- completion status
- timestamps

Example

| Execution | Attempt | Status |
|------------|----------|--------|
|5001|1|FAILED|
|5002|2|FAILED|
|5003|3|COMPLETED|
|5004|1|PENDING (Tomorrow)|

This already provides execution history.

A separate history table is unnecessary unless the business requires auditing every status transition.

---

# Why don't we store DLQ in the database?

Dead Letter Queue is usually implemented as a Kafka topic.

```
Execution Topic

↓

Worker

↓

FAILED after Max Retries

↓

task_dlq
```

Operations can consume

```
task_dlq
```

to investigate failed executions.

Therefore

DLQ is **not** another database table.

---

# Complete Execution Flow

## Step 1

User creates

```
Daily Report

Every day at 9 AM
```

Insert into

```
Jobs
```

Immediately create the first execution.

```
TaskExecution

Execution5001

ScheduledTime = Today 9 AM

Status = PENDING
```

---

## Step 2

Scheduler polls

```sql
SELECT *
FROM TaskExecution
WHERE status='PENDING'
AND scheduledTime <= NOW();
```

Finds

```
Execution5001
```

Updates

```
Status = IN_PROGRESS
```

Publishes

```
Execution5001
```

to Kafka.

---

## Step 3

Worker receives

```
Execution5001
```

Loads

```
Job101
```

from Jobs table.

Gets

- Cron
- Payload
- Retry Policy

Executes business logic.

---

# Success Flow

Worker updates

```
Execution5001

↓

COMPLETED
```

Worker computes

```
Tomorrow 9 AM
```

Creates another TaskExecution row.

| ExecutionId | JobId | Attempt | ScheduledTime | Status |
|-------------|-------|----------|---------------|--------|
|5002|101|1|Tomorrow 9 AM|PENDING|

Recurring schedule continues.

---

# Failure Flow

Suppose execution fails.

Worker updates

```
Execution5001

↓

FAILED
```

Worker checks

```
Current Attempt = 1

Maximum Retry = 3
```

Retry allowed.

Worker computes exponential delay.

Example

```
30 seconds
```

Creates another execution.

| ExecutionId | JobId | Attempt | ScheduledTime | Status |
|-------------|-------|----------|---------------|--------|
|5003|101|2|Today 9:00:30|PENDING|

The Scheduler does not know this is a retry.

It simply sees another pending execution.

---

# Retry Success

At

```
9:00:30
```

Scheduler polls again.

Finds

```
Execution5003
```

Publishes it.

Worker executes.

Suppose it succeeds.

Worker updates

```
Execution5003

↓

COMPLETED
```

Creates tomorrow's recurring execution.

---

# Retry Exhausted

Suppose

```
Attempt = 3

Maximum Retry = 3
```

Worker updates

```
Execution5003

↓

FAILED
```

Publishes

```
Execution5003
```

to

```
task_dlq
```

Operations team can inspect and replay the message if necessary.

---

# Exponential Backoff

The Worker computes retry delays.

Example

```
delay = BaseDelay × 2^(Attempt-1)
```

Example

| Attempt | Delay |
|----------|--------|
|1|30 sec|
|2|60 sec|
|3|120 sec|

Worker inserts another TaskExecution row using

```
ScheduledTime = NOW + delay
```

Scheduler simply waits until

```
ScheduledTime <= NOW
```

---

# Scaling Scheduler

For small systems

Multiple Scheduler instances poll the same TaskExecution table.

Use

```sql
SELECT ...
FOR UPDATE SKIP LOCKED
LIMIT 1000;
```

to prevent duplicate scheduling.

---

For very large systems

Introduce a lightweight Coordinator.

Coordinator responsibilities

- Assign database shards to Scheduler instances
- Monitor Scheduler heartbeats
- Detect failures
- Reassign shards
- Rebalance ownership

The Coordinator does **not** assign jobs.

It only assigns shard ownership.

Example

```
Coordinator

↓

Scheduler1 → Shard1

Scheduler2 → Shard2

Scheduler3 → Shard3
```

Each Scheduler polls only its own shard.

This eliminates lock contention and scales horizontally.

---

# Kafka Topics

Execution Topic

```
task_execution
```

Worker consumes from here.

Dead Letter Queue

```
task_dlq
```

Failed executions after exhausting retries are published here.

No retry topics are required in this design.

Retries are handled by creating another TaskExecution row.

---

# Component Responsibilities

## Job Service

- Create Job
- Update Job
- Delete Job
- Pause Job
- Create first TaskExecution

---

## Scheduler

- Poll TaskExecution
- Publish due executions
- Mark executions IN_PROGRESS

Nothing else.

---

## Worker

- Execute business logic
- Update execution status
- Compute exponential backoff
- Create retry executions
- Create next recurring execution
- Publish permanently failed executions to DLQ

---

# Why This Design?

Advantages

- Only two business tables.
- Scheduler polls one table.
- Retries and recurring executions use exactly the same pipeline.
- Scheduler remains extremely simple.
- Workers own execution lifecycle.
- Easy to scale horizontally.
- Clean separation of responsibilities.

---

# Interview Summary

> I model the system using two business tables: **Jobs** and **TaskExecution**. The Jobs table stores recurring job definitions such as the cron expression, payload, and retry policy. Every actual execution—whether it is the first scheduled run or a retry—is represented by a row in the TaskExecution table. The Scheduler continuously polls only the TaskExecution table for pending executions whose scheduled time has arrived and publishes their ExecutionIds to Kafka. Workers load the associated Job definition, execute the business logic, update execution status, and either create the next recurring execution on success or create a new retry execution with an exponentially delayed scheduled time on failure. If the retry limit is exceeded, the Worker publishes the execution to a Dead Letter Queue. This keeps the Scheduler simple while allowing retries, recurring scheduling, and horizontal scaling through a single execution pipeline.

# Job Scheduler - Pause and Resume Design

## Overview

A recurring job should be pausable without affecting already completed executions.

When a user pauses a job:

- No future executions should be scheduled.
- Already running executions should be allowed to complete.
- The Scheduler should not have to perform complex joins or additional logic.

Since our final design keeps **only one pending execution per recurring job**, pausing becomes very simple.

---

# Current Design

We have only two business tables.

```
Jobs

↓

TaskExecution
```

Jobs stores

- Cron Expression
- Payload
- Retry Policy
- Job Status

TaskExecution stores

- Next scheduled execution
- Retry executions
- Current execution state

There is only **one pending execution** for a recurring job.

---

# Before Pause

## Jobs

| JobId | Status |
|--------|---------|
|101|ACTIVE|

## TaskExecution

| ExecutionId | JobId | Status | ScheduledTime |
|-------------|-------|---------|---------------|
|5002|101|PENDING|Tomorrow 9 AM|

---

# User Clicks Pause

Both tables are updated in a single transaction.

```sql
BEGIN TRANSACTION;

UPDATE Jobs
SET status='PAUSED'
WHERE jobId=101;

UPDATE TaskExecution
SET status='PAUSED'
WHERE jobId=101
AND status='PENDING';

COMMIT;
```

---

# After Pause

## Jobs

| JobId | Status |
|--------|---------|
|101|PAUSED|

## TaskExecution

| ExecutionId | JobId | Status | ScheduledTime |
|-------------|-------|---------|---------------|
|5002|101|PAUSED|Tomorrow 9 AM|

The pending execution is preserved.

It simply won't be picked by the Scheduler.

---

# Scheduler Query

The Scheduler remains extremely simple.

```sql
SELECT *
FROM TaskExecution
WHERE status='PENDING'
AND scheduledTime <= NOW()
LIMIT 1000;
```

No joins.

No knowledge of the Jobs table.

No additional filtering.

---

# Resume Flow

When the user resumes the job,

both tables are updated.

```sql
BEGIN TRANSACTION;

UPDATE Jobs
SET status='ACTIVE'
WHERE jobId=101;

UPDATE TaskExecution
SET status='PENDING'
WHERE jobId=101
AND status='PAUSED';

COMMIT;
```

The existing pending execution becomes eligible for scheduling again.

No new execution needs to be created.

---

# What if the Job is Already Running?

Suppose the Scheduler has already published the execution.

```
Scheduler

↓

Kafka

↓

Worker
```

Then the user clicks Pause.

The running execution is **not interrupted**.

It continues until completion.

Only future executions are paused.

This is the behavior followed by most enterprise schedulers.

---

# Why Update Both Tables?

Since our design keeps only **one future pending execution**, updating TaskExecution is an O(1) operation.

Advantages

- Scheduler polls only one table.
- No joins required.
- Simpler Scheduler implementation.
- Pause and Resume are very fast.
- Clear separation of responsibilities.

---

# Final Pause Flow

```
User

↓

Pause Job

↓

BEGIN TRANSACTION

↓

Jobs.status = PAUSED

↓

TaskExecution.status = PAUSED

↓

COMMIT

↓

Scheduler ignores paused execution
```

---

# Final Resume Flow

```
User

↓

Resume Job

↓

BEGIN TRANSACTION

↓

Jobs.status = ACTIVE

↓

TaskExecution.status = PENDING

↓

COMMIT

↓

Scheduler schedules execution normally
```

---

# Why This Design?

This design is efficient because:

- Only one pending execution exists per recurring job.
- Both tables are updated atomically.
- Scheduler remains stateless and simple.
- No additional joins are required.
- Running executions are unaffected.
- Future executions are immediately paused or resumed.

This keeps the scheduling pipeline clean while making pause/resume operations extremely inexpensive and easy to reason about during a system design interview.

"The ScheduledTime represents the earliest time the execution is eligible to run, not a guarantee that it starts at that exact instant. Actual execution depends on scheduler polling intervals, queue latency, worker availability, and downstream processing delays. We therefore record both ScheduledTime and StartedAt so we can measure scheduling latency and monitor SLA compliance

# Job Scheduler - Final Database Design & Execution Flow

> This is the final production-ready design for a distributed Job Scheduler. It keeps the Scheduler extremely lightweight while allowing retries, pause/resume, horizontal scaling, and execution history using only **two business tables**.

---

# High-Level Architecture

```text
                    Client

                       │

                 Job Service APIs

                       │

                       ▼

                    Jobs Table
             (Recurring Job Definition)

                       │
          Creates First TaskExecution

                       ▼

               TaskExecution Table
     (Pending / Running / Completed / Failed)

                       ▲

                       │

                  Scheduler

                       │

              task_execution Topic
                    (Kafka)

                       │

                  Worker Pool

                       │

      ┌────────────────┴─────────────────┐

      ▼                                  ▼

 Success                           Failure

      │                                  │

      ▼                                  ▼

Create Next                    Retry Allowed?
Execution                             │
                                      │
                            ┌─────────┴─────────┐
                            ▼                   ▼

                 Create Retry Execution      Publish to DLQ
```

---

# Database Tables

We only need **two business tables**.

---

# 1. Jobs Table

Stores the recurring job definition.

This is the **template** for all future executions.

| Column | Description |
|----------|-------------|
| jobId | Primary Key |
| jobName | Human readable name |
| cronExpression | Cron schedule |
| payload | Input required by Worker |
| retryPolicy | Max retries, base delay, backoff strategy |
| status | ACTIVE / PAUSED / DELETED |
| createdAt | Creation timestamp |
| updatedAt | Last modified timestamp |

Example

| jobId | jobName | cron | status |
|-------|----------|------|--------|
|101|Daily Report|0 9 * * *|ACTIVE|

Notice that this table **does not contain execution state**.

It only defines

- What to execute
- When to execute
- Retry configuration

---

# 2. TaskExecution Table

Every execution gets one row.

This includes

- Initial execution
- Retry executions
- Future recurring executions

| Column | Description |
|----------|-------------|
| executionId | Primary Key |
| jobId | FK to Jobs |
| attempt | Retry attempt number |
| scheduledTime | Earliest time this execution may run |
| status | PENDING / IN_PROGRESS / COMPLETED / FAILED / PAUSED |
| startedAt | Actual execution start time |
| completedAt | Completion timestamp |
| errorMessage | Failure reason |

---

# Why only two tables?

TaskExecution itself already provides execution history.

Example

| ExecutionId | JobId | Attempt | Status | StartedAt | CompletedAt |
|-------------|-------|----------|--------|-----------|-------------|
|5001|101|1|FAILED|9:00:01|9:00:05|
|5002|101|2|COMPLETED|9:00:30|9:00:36|
|5003|101|1|PENDING|NULL|NULL|

This already tells us

- Every retry
- Every execution
- Success/failure
- Timing information

A separate history table is unnecessary unless the business requires auditing every state transition.

---

# Execution Flow

## Step 1 - Create Job

User creates

```
Daily Report

Runs every day at 9 AM
```

Insert into

```
Jobs
```

Immediately create the first execution.

TaskExecution

| ExecutionId | JobId | Attempt | ScheduledTime | Status |
|-------------|-------|----------|---------------|--------|
|5001|101|1|Today 9:00|PENDING|

---

## Step 2 - Scheduler

Every second (or every few seconds)

Scheduler executes

```sql
SELECT *
FROM TaskExecution
WHERE status='PENDING'
AND scheduledTime <= NOW()
LIMIT 1000;
```

Scheduler updates

```
Status = IN_PROGRESS
```

Publishes

```
Execution5001
```

to Kafka.

---

## Step 3 - Worker

Worker receives

```
Execution5001
```

Loads Job101 from Jobs table.

Gets

- Payload
- Cron Expression
- Retry Policy

Executes business logic.

---

# Success Flow

Worker updates

```
Execution5001

↓

COMPLETED
```

Worker computes

```
Tomorrow 9:00 AM
```

Creates another execution.

| ExecutionId | JobId | Attempt | ScheduledTime | Status |
|-------------|-------|----------|---------------|--------|
|5002|101|1|Tomorrow 9:00|PENDING|

Recurring schedule continues.

---

# Failure Flow

Suppose execution fails.

Worker updates

```
Execution5001

↓

FAILED
```

Reads retry policy

```
Max Retry = 3

Base Delay = 30 seconds
```

Current attempt

```
1
```

Computes exponential backoff

```
Delay = 30 seconds
```

Creates another execution.

| ExecutionId | JobId | Attempt | ScheduledTime | Status |
|-------------|-------|----------|---------------|--------|
|5003|101|2|Today 9:00:30|PENDING|

Scheduler treats this exactly like every other execution.

No retry-specific logic is needed.

---

# Retry Success

At

```
9:00:30
```

Scheduler finds

```
Execution5003
```

Publishes it.

Worker executes.

Suppose it succeeds.

Worker updates

```
Execution5003

↓

COMPLETED
```

Creates tomorrow's recurring execution.

---

# Retry Exhausted

Suppose

```
Attempt = 3

Maximum Retry = 3
```

Worker updates

```
Execution5003

↓

FAILED
```

Publishes

```
Execution5003
```

to

```
task_dlq
```

for manual investigation or replay.

---

# Exponential Backoff

Worker calculates retry delay.

Formula

```
Delay = BaseDelay × 2^(Attempt-1)
```

Example

| Attempt | Delay |
|----------|--------|
|1|30 sec|
|2|60 sec|
|3|120 sec|

Worker creates another TaskExecution row using

```
scheduledTime = NOW + Delay
```

Scheduler simply waits until

```
scheduledTime <= NOW
```

---

# Understanding scheduledTime

The Scheduler never guarantees execution at the exact scheduled timestamp.

Instead,

```
scheduledTime
```

means

> **The earliest time this execution is eligible to run.**

Actual execution depends on

- Scheduler polling interval
- Kafka queue latency
- Worker availability
- Network delays

Therefore we store

- scheduledTime
- startedAt
- completedAt

Example

| ExecutionId | ScheduledTime | StartedAt |
|-------------|---------------|-----------|
|5001|9:00:00|9:00:07|

Scheduling Delay

```
StartedAt - ScheduledTime

=

7 seconds
```

This delay is expected in distributed systems.

---

# Pause / Resume

## Pause

User clicks Pause.

Transaction

```sql
BEGIN;

UPDATE Jobs
SET status='PAUSED'
WHERE jobId=101;

UPDATE TaskExecution
SET status='PAUSED'
WHERE jobId=101
AND status='PENDING';

COMMIT;
```

Running executions continue.

Future executions stop.

---

## Resume

```sql
BEGIN;

UPDATE Jobs
SET status='ACTIVE'
WHERE jobId=101;

UPDATE TaskExecution
SET status='PENDING'
WHERE jobId=101
AND status='PAUSED';

COMMIT;
```

Scheduler resumes scheduling normally.

---

# Kafka Topics

Execution Topic

```
task_execution
```

Used for normal executions.

Dead Letter Queue

```
task_dlq
```

Used after all retries are exhausted.

No retry topics are required in this design.

---

# Scheduler Scaling

## Small Scale

Multiple Scheduler instances poll the same TaskExecution table.

Use

```sql
SELECT ...
FOR UPDATE SKIP LOCKED
LIMIT 1000;
```

to prevent duplicate scheduling.

---

## Large Scale

Introduce a Coordinator.

Coordinator responsibilities

- Assign database shards to Scheduler instances
- Receive Scheduler heartbeats
- Detect Scheduler failures
- Reassign shard ownership
- Rebalance when new Scheduler instances join

The Coordinator does **not** assign jobs.

It only assigns shard ownership.

Example

```
Coordinator

↓

Scheduler1 → Shard1

Scheduler2 → Shard2

Scheduler3 → Shard3
```

Each Scheduler polls only its assigned shard.

---

# Final Responsibilities

## Job Service

- Create Job
- Update Job
- Pause Job
- Resume Job
- Delete Job
- Create first TaskExecution

---

## Scheduler

- Poll TaskExecution
- Mark execution IN_PROGRESS
- Publish ExecutionId to Kafka

Scheduler does **not**

- execute jobs
- compute retries
- calculate exponential backoff

---

## Worker

- Load Job definition
- Execute business logic
- Update execution status
- Compute exponential backoff
- Create retry executions
- Create next recurring execution
- Publish permanently failed executions to DLQ

---

# Why This Design?

Advantages

- Only two business tables.
- Scheduler polls only one table.
- No retry-specific scheduling logic.
- Retries and normal executions follow the same pipeline.
- Workers own execution lifecycle.
- Easy to scale horizontally.
- Simple pause/resume implementation.
- Easy to explain during interviews.

---

# Interview Summary

> The system uses two business tables: **Jobs** and **TaskExecution**. The Jobs table stores recurring job definitions including the cron expression, payload, retry policy, and status. Every actual execution—including retries—is represented by a row in the TaskExecution table. The Scheduler continuously polls TaskExecution for pending executions whose scheduled time has arrived, marks them IN_PROGRESS, and publishes them to Kafka. Workers execute the job, update execution status, compute exponential backoff when necessary, create retry executions as new TaskExecution rows, and create the next recurring execution after successful completion. If retries are exhausted, the Worker publishes the execution to a Dead Letter Queue. This keeps the Scheduler lightweight while supporting retries, pause/resume, execution history, and horizontal scalability through a single execution pipeline.

For simplicity and reliability, I'd start with a configurable polling scheduler. A 1-second polling interval is a common choice for enterprise schedulers and provides good precision with minimal database overhead when combined with proper indexing and horizontal scaling. If the system grows significantly or requires sub-second precision, we could evolve to an event-driven or timer-wheel-based scheduler."

Worker inserts

ExecutionId	ScheduledTime
6001	2026-07-12 12:00:00

Again,

the DB stores an absolute timestamp.


# Financial Data Aggregation Platform

# Part 1 - Introduction, Requirements & Capacity Estimation

---

# Problem Statement

Design a platform similar to **Mint, Monarch Money, Rocket Money, or Empower** that allows users to securely connect their bank accounts through financial aggregators (such as Plaid or Yodlee), continuously ingest transaction data, process it in near real-time, categorize spending, and provide financial insights, reports, and analytics.

The system should support millions of users while ensuring correctness, scalability, security, and high availability.

---

# Interview Opening (30 Seconds)

A good opening immediately provides structure to the interviewer.

> "I'll design a platform that connects users to financial institutions through aggregators like Plaid. Transactions are ingested using webhooks and polling, processed through an event-driven pipeline, stored in transactional databases, and exposed through low-latency reporting APIs. Since we're dealing with financial data, correctness, security, and auditability are primary design goals."

This immediately tells the interviewer:

- You understand the domain.
- You know external aggregators are involved.
- You understand event-driven systems.
- You care about correctness.

---

# High-Level Functional Requirements

The platform should support the following capabilities.

## 1. Account Management

Users should be able to

- Connect a bank account
- Connect multiple institutions
- Disconnect accounts
- Reconnect expired accounts
- Refresh account balances

---

## 2. Transaction Synchronization

The system should

- Import historical transactions
- Continuously receive new transactions
- Detect updates
- Detect deleted transactions
- Maintain consistency with the bank

---

## 3. Transaction Processing

Incoming transactions should be

- Validated
- Deduplicated
- Ordered
- Enriched
- Categorized

before becoming available to users.

---

## 4. Search

Users should search transactions using

- Merchant
- Date
- Amount
- Category
- Description

---

## 5. Reports

Generate

- Monthly spending
- Cash flow
- Income vs Expenses
- Category breakdown
- Spending trends
- Net worth

---

## 6. Notifications

Support notifications such as

- Large purchases
- Low balance
- Subscription detection
- Unusual spending

---

## 7. AI Insights (Optional)

Generate insights like

- Spending anomalies
- Budget recommendations
- Personalized financial advice

---

# Non-Functional Requirements

These are more important than functional requirements in system design interviews.

---

## Correctness

Financial data must be accurate.

Duplicate or missing transactions are unacceptable.

The system must support reconciliation with the source of truth.

---

## Availability

Users should be able to view transactions even if downstream systems are temporarily unavailable.

Target

```
99.9%+
```

---

## Scalability

The platform should support

- Millions of users
- Thousands of institutions
- Billions of transactions

without redesign.

---

## Low Latency

New transactions should become visible quickly.

Target

```
30 seconds to 2 minutes
```

depending on aggregator capabilities.

---

## Reliability

Failures should never result in permanent data loss.

Every transaction should either

- be processed
- retried
- sent to a Dead Letter Queue

---

## Security

Financial information is highly sensitive.

Requirements include

- Encryption at rest
- TLS in transit
- OAuth
- Tokenization
- Secret management
- Audit logging

---

## Observability

Every transaction should be traceable through the system.

Support

- Metrics
- Logs
- Tracing
- Alerts
- Correlation IDs

---

# Assumptions

To simplify the design, we make several assumptions.

---

## We do NOT integrate directly with banks.

Instead we use aggregators like

- Plaid
- Yodlee
- MX

These aggregators

- manage authentication
- normalize APIs
- support thousands of institutions
- provide webhooks
- reduce engineering complexity

---

## Banks remain the Source of Truth.

Our database is a cached representation of bank data.

If inconsistencies occur,

reconciliation jobs synchronize our system with the bank.

---

## Aggregators handle credentials.

The platform never stores

- usernames
- passwords
- MFA credentials

Instead we securely store

- access tokens
- refresh tokens

issued by the aggregator.

---

# Design Goals

The system should satisfy five primary goals.

---

## Goal 1

Decouple ingestion from processing.

Reason

External APIs are slow and unreliable.

Processing should continue independently.

---

## Goal 2

Ensure idempotency.

Duplicate events should never create duplicate transactions.

---

## Goal 3

Scale every layer independently.

The following should scale independently

- Ingestion
- Processing
- Storage
- Reporting

---

## Goal 4

Support replay.

If downstream processing fails,

we should replay historical events without asking banks again.

---

## Goal 5

Maintain eventual consistency.

Temporary inconsistencies are acceptable.

Permanent inconsistencies are not.

Nightly reconciliation jobs guarantee correctness.

---

# Capacity Estimation

Interviewers generally expect rough estimates rather than perfect calculations.

Assume

```
5 Million Users
```

Average

```
3 Accounts / User
```

Total Accounts

```
15 Million Accounts
```

Average Transactions

```
5 Transactions / Day / Account
```

Daily Transactions

```
75 Million Transactions / Day
```

Transactions Per Second

```
75,000,000

/

86,400

≈

870 TPS
```

Assume peak traffic is

```
5x Average
```

Peak

```
~4,500 TPS
```

The system should comfortably support

```
5,000–10,000 TPS
```

---

# Storage Estimation

Average transaction size

```
500 Bytes
```

Daily Storage

```
75 Million

×

500 Bytes

≈

37 GB/day
```

Annual Storage

```
≈13 TB/year
```

excluding indexes and replication.

---

# Read vs Write Traffic

Read traffic is significantly larger.

Approximate ratio

```
Reads

:

Writes

=

10

:

1
```

Reason

Users repeatedly

- open dashboards
- refresh transactions
- generate reports

while writes occur only when banks send updates.

---

# High-Level Architecture

The system is organized into three logical layers.

```
                 Ingestion Layer

                         │

                         ▼

                Processing Layer

                         │

                         ▼

             Storage & Serving Layer
```

---

## Ingestion Layer

Responsible for

- Receiving webhooks
- Polling aggregators
- Validating requests
- Publishing events

---

## Processing Layer

Responsible for

- Validation
- Deduplication
- Ordering
- Enrichment
- Categorization

---

## Storage & Serving Layer

Responsible for

- OLTP database
- Analytics
- Reporting APIs
- Search
- Dashboards

---

# Key Design Principles

Throughout the design we follow several important principles.

### Single Responsibility

Every service has one responsibility.

---

### Event-Driven Architecture

Services communicate using Kafka.

This enables

- decoupling
- replay
- scalability

---

### Idempotency

Consumers should safely process duplicate events.

---

### Horizontal Scaling

Every major component should be stateless.

Scaling simply means adding more instances.

---

### Fault Isolation

Failure of one component should not stop the entire pipeline.

---

# Interview Talking Points

These are high-signal statements that interviewers appreciate.

> We use financial aggregators because integrating directly with thousands of banks is operationally expensive and difficult to maintain.

> Since financial systems prioritize correctness over latency, reconciliation is a first-class component of the architecture.

> Kafka decouples ingestion from processing and allows replay without re-fetching data from banks.

> We design consumers to be idempotent because retries and duplicate events are inevitable.

> Banks remain the source of truth, while our platform maintains an eventually consistent representation through continuous synchronization and reconciliation.

---

# Next Chapter

Part 2 - API Design

We will design

- Connect Account APIs
- Disconnect APIs
- Transaction APIs
- Reporting APIs
- Webhook APIs
- Authentication
- API contracts
- Request/Response examples
- Error handling

# Financial Data Aggregation Platform

# Part 2 – API Design

---

# Overview

The API layer is the entry point into the platform.

Responsibilities include

- Authentication
- Authorization
- Request validation
- Rate limiting
- Routing requests to backend services

Clients include

- Mobile applications
- Web applications
- Third-party integrations

The APIs are designed to be RESTful and stateless.

---

# API Gateway

The API Gateway sits between clients and backend services.

```
               Client

                  │

                  ▼

             API Gateway

      Authentication
      Authorization
      Rate Limiting
      Routing
      Request Validation
      Logging

                  │

      ┌───────────┴─────────────┐

      ▼                         ▼

Account Service          Reporting Service
```

---

## Responsibilities

The API Gateway handles

- JWT validation
- OAuth authentication
- Rate limiting
- SSL termination
- Logging
- Metrics
- Request routing
- CORS
- Request validation

It should **NOT**

- Execute business logic
- Access databases
- Call external aggregators

Those responsibilities belong to backend services.

---

# Authentication

Users authenticate using

```
OAuth2 + JWT
```

Flow

```
User Login

↓

Identity Provider

↓

JWT Token

↓

API Gateway validates JWT

↓

Forward request
```

---

# API 1 – Connect Bank Account

This is one of the most important APIs.

The frontend first requests a Link Token.

---

## Request

```
POST /v1/accounts/link-token
```

Request

```json
{
   "userId":"12345"
}
```

---

## Response

```json
{
   "linkToken":"plaid-link-token-xyz"
}
```

---

The frontend opens the Plaid Link SDK using this token.

The user authenticates directly with Plaid.

Our backend **never receives bank credentials**.

---

# API 2 – Exchange Public Token

After successful authentication,

Plaid returns

```
public_token
```

The frontend sends it to our backend.

---

## Request

```
POST /v1/accounts/connect
```

```json
{
   "publicToken":"public-sandbox-xyz",
   "institutionId":"ins_123"
}
```

---

## Backend Flow

```
Frontend

↓

Account Service

↓

Plaid

↓

Exchange public token

↓

Receive access token

↓

Store encrypted access token
```

---

## Response

```json
{
   "accountId":"acc_1001",
   "status":"CONNECTED"
}
```

---

# API 3 – Disconnect Account

```
DELETE /v1/accounts/{accountId}
```

---

Flow

```
Delete mapping

↓

Invalidate token

↓

Stop polling

↓

Delete webhook registration
```

---

Response

```json
{
   "status":"SUCCESS"
}
```

---

# API 4 – Refresh Account

Sometimes the user wants an immediate refresh.

```
POST /v1/accounts/{accountId}/refresh
```

---

Backend

```
Account Service

↓

Plaid Sync API

↓

Fetch latest transactions

↓

Kafka
```

Response

```json
{
    "status":"SYNC_STARTED"
}
```

Notice

Refresh is asynchronous.

We don't wait for all transactions.

---

# API 5 – Get Accounts

```
GET /v1/accounts
```

Response

```json
[
   {
      "accountId":"A1",
      "institution":"Chase",
      "type":"Checking",
      "balance":1200.54
   },
   {
      "accountId":"A2",
      "institution":"Bank of America",
      "type":"Savings",
      "balance":5800
   }
]
```

---

# API 6 – Get Transactions

```
GET /v1/transactions
```

Supports

- pagination
- filtering
- sorting

Example

```
GET /transactions?
accountId=A1
&page=1
&pageSize=50
```

---

Optional Filters

```
merchant

category

amount

startDate

endDate

sort
```

Example

```
GET /transactions?

category=Food

&startDate=2026-01-01

&endDate=2026-01-31
```

---

Response

```json
{
   "transactions":[
      {
         "transactionId":"T1001",
         "merchant":"Starbucks",
         "category":"Food",
         "amount":8.75,
         "date":"2026-01-10"
      }
   ]
}
```

---

# Pagination

Never return all transactions.

Use

```
Cursor Pagination
```

instead of Offset Pagination.

Example

```
GET /transactions

?cursor=abc123

&pageSize=100
```

Advantages

- Better performance
- No skipped records
- Better for large datasets

---

# API 7 – Search Transactions

```
GET /v1/search
```

Filters

```
Merchant

Category

Description

Amount

Date Range
```

Example

```
GET /search?

merchant=Starbucks
```

---

# API 8 – Reports

```
GET /v1/reports/monthly
```

Returns

```json
{
   "income":9000,
   "expenses":6200,
   "cashFlow":2800
}
```

---

Other Reports

```
GET /reports/cashflow

GET /reports/categories

GET /reports/trends

GET /reports/networth
```

---

# API 9 – Dashboard

```
GET /v1/dashboard
```

Returns

- Recent Transactions
- Monthly Spending
- Income
- Savings
- Cash Flow
- Top Categories

Dashboard reads from

```
Redis

+

Analytics Database
```

instead of recalculating every request.

---

# API 10 – Webhook Endpoint

Aggregators call our webhook.

```
POST /v1/webhooks/plaid
```

Example

```json
{
   "webhook_type":"TRANSACTIONS",
   "webhook_code":"SYNC_UPDATES_AVAILABLE"
}
```

The Ingestion Service

- validates signature
- validates schema
- publishes event to Kafka

Response

```
HTTP 200
```

immediately.

Heavy processing happens asynchronously.

---

# API Design Principles

## Idempotency

Some APIs may be retried.

Example

```
POST /accounts/connect
```

should not create duplicate accounts.

Use

```
Idempotency-Key
```

header.

Example

```
Idempotency-Key

9d82cdd4
```

---

## Versioning

Use URI versioning.

```
/v1/accounts

/v1/transactions
```

Future versions

```
/v2/accounts
```

---

## Error Handling

Example

```json
{
   "errorCode":"ACCOUNT_NOT_FOUND",
   "message":"Invalid account."
}
```

Never expose stack traces.

---

## HTTP Status Codes

| Code | Meaning |
|-------|----------|
|200|Success|
|201|Created|
|202|Accepted (Async Request)|
|400|Bad Request|
|401|Unauthorized|
|403|Forbidden|
|404|Not Found|
|409|Duplicate Request|
|429|Rate Limited|
|500|Internal Server Error|

---

# Rate Limiting

Protect backend services.

Example

```
100 requests/minute/user
```

API Gateway returns

```
429

Too Many Requests
```

when exceeded.

---

# Security

Every request should include

```
Authorization

Bearer JWT
```

Sensitive information

- access tokens
- account numbers
- PII

must never be returned by APIs.

---

# Interview Talking Points

> The API Gateway handles cross-cutting concerns such as authentication, routing, and rate limiting, while backend services remain focused on business logic.

> Connecting a bank account is a two-step process: first obtaining a Plaid Link Token, then exchanging the returned Public Token for an Access Token. This ensures bank credentials are never exposed to our backend.

> Long-running operations like account synchronization are asynchronous. The API immediately acknowledges the request and processing continues through Kafka.

> Transaction APIs use cursor-based pagination for better scalability compared to offset pagination.

> Dashboard APIs serve precomputed summaries from Redis and analytics stores to provide low-latency responses without recalculating aggregates on every request.

---

# Next Chapter

**Part 3 – High-Level Architecture**

We will design

- Overall architecture
- Component responsibilities
- Control Plane vs Data Plane
- Ingestion layer
- Processing layer
- Storage layer
- Why Kafka
- Complete architecture diagram
- End-to-end request flow

# Financial Data Aggregation Platform

# Part 3 – High-Level Architecture

---

# Overview

The goal of this chapter is to design the overall architecture of a Financial Data Aggregation Platform.

Our objectives are

- Connect user bank accounts
- Continuously synchronize transactions
- Process transactions reliably
- Generate reports
- Scale horizontally
- Ensure correctness and security

Instead of building a monolithic application, we decompose the system into independent services that communicate asynchronously through Kafka.

---

# High-Level Architecture

```
                           Mobile / Web App

                                   │

                                   ▼

                             API Gateway

                                   │

             ┌─────────────────────┴─────────────────────┐

             ▼                                           ▼

    Account & Aggregation Service                Reporting Service

             │                                           │

             ▼                                           ▼

      Plaid / Yodlee APIs                         Redis / Analytics

             │

             ▼

     Webhooks / Polling

             │

             ▼

      Ingestion Service

             │

             ▼

         Kafka Topics

             │

             ▼

     Processing Pipeline

             │

             ▼

        PostgreSQL

             │

             ▼

    transactions.processed

             │

      ┌──────┼────────┬─────────┐

      ▼      ▼        ▼         ▼

 Reporting Notifications ML Analytics
```

---

# Three Logical Layers

To simplify the architecture, we divide the platform into three layers.

```
Ingestion Layer

↓

Processing Layer

↓

Storage & Serving Layer
```

Each layer has a clear responsibility.

---

# Layer 1 – Ingestion Layer

Purpose

Bring data from external financial institutions into our platform.

Components

- Account & Aggregation Service
- Plaid
- Yodlee
- Webhook Receiver
- Polling Scheduler
- Ingestion Service

Responsibilities

- Authenticate users
- Store access tokens
- Receive webhooks
- Poll aggregators
- Handle retries
- Handle rate limits
- Publish Kafka events

The ingestion layer should perform minimal processing.

Its responsibility is to reliably move data into Kafka.

---

# Layer 2 – Processing Layer

Purpose

Transform raw financial transactions into meaningful business data.

Responsibilities

- Validation
- Deduplication
- Ordering
- Enrichment
- Merchant normalization
- Currency conversion
- Categorization
- Persistence

Processing services communicate using Kafka.

This allows

- replay
- scaling
- fault isolation

---

# Layer 3 – Storage & Serving Layer

Purpose

Serve low-latency APIs.

Contains

- PostgreSQL
- Redis
- Analytics Warehouse
- Reporting Service

This layer never talks directly to Plaid.

It only serves processed data.

---

# Why Use Financial Aggregators?

One of the first architectural decisions is whether to connect directly to banks.

We do **not**.

Instead we use aggregators such as

- Plaid
- Yodlee
- MX

Architecture

```
Application

↓

Plaid

↓

Thousands of Banks
```

instead of

```
Application

↓

Bank A

Bank B

Bank C

...

Bank 10,000
```

---

# Advantages of Aggregators

They provide

- Unified APIs
- OAuth support
- Secure authentication
- Webhooks
- Cursor-based synchronization
- Normalized transaction formats
- Institution metadata

This significantly reduces engineering complexity.

---

# Control Plane vs Data Plane

This distinction is often mentioned by Senior Engineers.

---

## Control Plane

Responsible for managing integrations.

Includes

- User ↔ Account mapping
- Access tokens
- Refresh tokens
- Poll schedules
- Cursor positions
- Webhook registrations
- Sync state

Implemented by

```
Account & Aggregation Service
```

---

Example

```
User

↓

Connect Bank

↓

Store Token

↓

Register Webhook

↓

Start Sync
```

No financial transactions flow through the control plane.

---

## Data Plane

Responsible for processing transaction data.

Includes

```
Webhook

↓

Kafka

↓

Validation

↓

Deduplication

↓

Categorization

↓

Database
```

The data plane is optimized for

- throughput
- scalability
- reliability

---

# Component Responsibilities

---

# API Gateway

Responsibilities

- Authentication
- JWT Validation
- Routing
- Rate Limiting
- Logging

Never

- accesses DB
- calls Plaid
- executes business logic

---

# Account & Aggregation Service

One of the most important services.

Responsibilities

- Create Link Token
- Exchange Public Token
- Store Access Token
- Manage reconnect flow
- Register webhooks
- Manage polling
- Track cursors
- Handle pagination
- Handle rate limits
- Handle token expiration

Think of this as

```
Control Plane
```

---

# Ingestion Service

Purpose

Receive external data.

Responsibilities

- Receive webhook
- Verify signature
- Validate payload
- Normalize schema
- Publish Kafka event

Should perform almost no business logic.

---

# Kafka

Kafka becomes the backbone of the architecture.

```
Webhook

↓

Kafka

↓

Processing Services
```

Kafka provides

- Decoupling
- Replay
- Ordering
- Horizontal scaling

---

# Processing Services

These services transform data.

Pipeline

```
Validation

↓

Deduplication

↓

Ordering

↓

Enrichment

↓

Categorization

↓

Persistence
```

Each service

- owns one responsibility
- scales independently

---

# PostgreSQL

Stores

- Users
- Accounts
- Transactions
- Categories
- Merchants

Supports

- transactional consistency
- user APIs

---

# Redis

Stores

- Dashboard summaries
- Recent transactions
- Frequently accessed reports

Purpose

Reduce database load.

---

# Analytics Warehouse

Examples

- Snowflake
- BigQuery
- Redshift

Used for

- historical reports
- BI
- large aggregations
- ML features

Not used by user-facing APIs.

---

# Reporting Service

Reads

- PostgreSQL
- Redis
- Analytics DB

Serves

```
GET /transactions

GET /dashboard

GET /reports
```

---

# Why Kafka?

Instead of

```
Webhook

↓

Database

↓

Reporting
```

we use

```
Webhook

↓

Kafka

↓

Processing

↓

Database
```

Advantages

---

## Decoupling

Webhook receiver returns immediately.

Processing continues asynchronously.

---

## Replay

Suppose Categorization Service fails.

Kafka retains events.

Consumer restarts.

Reprocesses events.

No need to fetch transactions again.

---

## Independent Scaling

Suppose categorization becomes CPU intensive.

Increase

```
Categorization Consumers
```

No other service changes.

---

## Multiple Consumers

One event can feed multiple systems.

```
transactions.processed

↓

Reporting

Analytics

Notifications

ML
```

without changing the producer.

---

# Event-Driven Architecture

Instead of direct service calls,

services communicate through events.

```
Transaction Arrived

↓

TransactionValidated

↓

TransactionCategorized

↓

TransactionPersisted
```

Each service reacts independently.

This reduces coupling.

---

# Design Principles

Every architectural decision follows a few principles.

---

## Single Responsibility

Each service owns one responsibility.

Examples

- Validation
- Categorization
- Reporting

---

## Stateless Services

Application services should remain stateless.

Any instance can process any request.

Scaling simply means adding more instances.

---

## Idempotency

Duplicate events are expected.

Consumers should safely process duplicates.

---

## Fault Isolation

Failure of one service should not stop the entire pipeline.

Kafka buffers work until downstream services recover.

---

## Eventual Consistency

Reports may lag behind by a few seconds.

Financial correctness is maintained through

- retries
- replay
- reconciliation

---

# Complete End-to-End Flow

```
User

↓

Connect Bank

↓

Plaid

↓

Webhook

↓

Ingestion Service

↓

Kafka

↓

Validation

↓

Deduplication

↓

Ordering

↓

Enrichment

↓

Categorization

↓

PostgreSQL

↓

transactions.processed

↓

Reporting

Analytics

Notifications

ML
```

---

# Interview Talking Points

> I separate the system into Ingestion, Processing, and Storage layers so that each layer can evolve and scale independently.

> We integrate with aggregators like Plaid instead of banks directly because they provide a unified API, secure authentication, and normalized financial data.

> Kafka acts as the event backbone of the system, decoupling ingestion from processing while enabling replay, horizontal scaling, and fault isolation.

> I separate the control plane from the data plane. The Account Service manages integrations, tokens, cursors, and webhooks, while the processing pipeline focuses solely on transaction data.

> Every processing stage has a single responsibility, making the pipeline easier to scale, test, and maintain.

---

# Next Chapter

## Part 4 – Account & Aggregation Service

Topics

- OAuth Flow
- Plaid Link
- Public Token Exchange
- Access Token Management
- Webhook Registration
- Polling Strategy
- Cursor Management
- Incremental Sync
- Pagination
- Rate Limiting
- Retry Strategy
- Token Refresh
- Failure Handling
- Reconnection Flow

This is where we begin interacting with external financial systems.

# Financial Data Aggregation Platform

# Part 4 – Account & Aggregation Service (Control Plane)

---

# Overview

The Account & Aggregation Service is the **Control Plane** of the Financial Data Aggregation Platform.

It is responsible for managing the relationship between users and financial institutions.

Unlike the Processing Pipeline, this service **does not process financial transactions**.

Instead, it manages

- User ↔ Account mappings
- Plaid integration
- OAuth flow
- Access Tokens
- Webhook registration
- Polling schedules
- Cursor management
- Reconnection flows
- Sync orchestration

Think of it as the service responsible for **bringing data into the platform**, while Kafka and downstream services are responsible for **processing** that data.

---

# Responsibilities

The Account Service owns

✓ Connect Bank Account

✓ Disconnect Bank Account

✓ Refresh Account

✓ Token Management

✓ Cursor Management

✓ Poll Scheduling

✓ Webhook Registration

✓ Sync State

✓ Pagination

✓ Retry Policies

✓ Rate Limiting

✓ Reconnection

---

# High-Level Architecture

```
                Mobile App

                     │

                     ▼

             API Gateway

                     │

                     ▼

      Account & Aggregation Service

                     │

         ┌───────────┴───────────┐

         ▼                       ▼

     Plaid APIs            Metadata DB

         │

         ▼

      Financial Institutions
```

---

# Why Financial Aggregators?

Instead of integrating with every bank,

```
Application

↓

Bank A

Bank B

Bank C

...

Bank 10,000
```

we integrate with

```
Application

↓

Plaid

↓

Thousands of Banks
```

Advantages

- Unified API
- OAuth support
- Secure authentication
- Transaction normalization
- Webhooks
- Incremental synchronization
- Institution metadata
- Reduced engineering effort

---

# Connect Account Flow

The complete flow consists of multiple steps.

```
User

↓

Request Link Token

↓

Plaid Link UI

↓

Authenticate

↓

Receive Public Token

↓

Exchange Public Token

↓

Receive Access Token

↓

Store Token

↓

Register Webhook

↓

Start Initial Sync
```

---

# Step 1 – Create Link Token

Frontend calls

```
POST /accounts/link-token
```

Backend

↓

Plaid

↓

Create Link Token

↓

Return to frontend

The Link Token is short-lived.

It is only used to launch Plaid Link.

---

# Step 2 – User Authentication

Plaid displays its hosted authentication UI.

User enters

- Username
- Password
- MFA

Our application never sees these credentials.

This is an important interview point.

> We never store or transmit banking credentials. Authentication is completely managed by the aggregator.

---

# Step 3 – Public Token

After authentication,

Plaid returns

```
public_token
```

This token

- is temporary
- expires quickly
- cannot fetch transactions

---

# Step 4 – Exchange Public Token

Backend calls

```
Plaid

↓

/item/public_token/exchange
```

Plaid returns

```
access_token
```

This token allows

- Fetch transactions
- Refresh balances
- Receive webhooks

---

# Step 5 – Store Access Token

The access token is encrypted before storage.

Never store it in plain text.

Example

```
Accounts

----------------------------

accountId

userId

institutionId

encryptedAccessToken

cursor

status
```

Encryption

- KMS
- Vault
- Secrets Manager

---

# Step 6 – Register Webhook

Backend registers

```
Webhook URL
```

Example

```
https://api.company.com/webhooks/plaid
```

Now Plaid can notify us whenever

- New transaction
- Updated transaction
- Deleted transaction

---

# Initial Synchronization

Immediately after connecting,

the platform performs a historical synchronization.

Typically

```
Last

6–12 Months
```

Flow

```
Plaid

↓

Transactions API

↓

Pagination

↓

Kafka
```

---

# Why Initial Sync?

Users expect to immediately see historical data.

Without this,

only future transactions would appear.

---

# Pagination

Suppose

```
20,000
```

transactions exist.

Do not request all at once.

Instead

```
Page 1

↓

Page 2

↓

Page 3

↓

...
```

Advantages

- Lower memory usage
- Better retry behavior
- Lower timeout probability

---

# Incremental Synchronization

After initial sync,

only fetch changes.

Plaid provides

```
Cursor
```

Example

```
Cursor

↓

abc123
```

Next request

```
Give me changes

after abc123
```

Response

```
Added

Updated

Removed
```

Cursor becomes

```
abc456
```

Store

```
abc456
```

in database.

---

# Why Cursor?

Without cursors

every sync becomes

```
Fetch Everything
```

With cursors

```
Fetch Only Changes
```

This significantly reduces API usage.

---

# Database Example

Accounts

| accountId | cursor |
|------------|---------|
|1001|abc456|

Every successful sync updates

```
cursor
```

---

# Webhook Flow

Whenever new transactions arrive

```
Bank

↓

Plaid

↓

Webhook

↓

Ingestion Service

↓

Kafka
```

The webhook payload is intentionally small.

Example

```json
{
   "webhook_type":"TRANSACTIONS",
   "webhook_code":"SYNC_UPDATES_AVAILABLE"
}
```

Notice

The webhook does **not** contain transactions.

It simply tells us

> New data is available.

The Ingestion Service then calls Plaid.

---

# Polling Strategy

Webhooks are preferred.

However,

they are not sufficient.

Reasons

- Missed webhook
- Network failure
- Provider outage

Therefore

we also poll periodically.

Example

```
Every

30 Minutes
```

Flow

```
Scheduler

↓

Account Service

↓

Plaid

↓

Incremental Sync

↓

Kafka
```

This ensures eventual consistency.

---

# Hybrid Strategy

```
Webhook

↓

Near Real-Time

+

Polling

↓

Reliability
```

Interview line

> Webhooks provide low latency, while polling guarantees eventual consistency.

---

# Rate Limiting

External APIs impose limits.

Example

```
100 requests/minute
```

The Account Service should implement

- Exponential Backoff
- Retry
- Adaptive Throttling

Never continuously retry failed requests.

---

# Retry Strategy

Suppose

```
HTTP 429

Too Many Requests
```

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

This prevents overwhelming Plaid.

---

# Token Expiration

Access tokens may expire.

Flow

```
Plaid

↓

401 Unauthorized

↓

Mark Account

↓

RECONNECT_REQUIRED
```

Notify user

```
Reconnect Bank
```

User repeats

Plaid Link

without losing transaction history.

---

# Account States

An account can be

```
CONNECTED

↓

SYNCING

↓

ACTIVE

↓

ERROR

↓

RECONNECT_REQUIRED

↓

DISCONNECTED
```

This state machine helps operations teams diagnose issues.

---

# Failure Handling

Suppose

Plaid is unavailable.

The Account Service should

- Retry
- Backoff
- Log
- Emit metrics

Do not block user APIs.

---

# Database Schema

Accounts

| Column | Description |
|---------|-------------|
| accountId | PK |
| userId | Owner |
| institutionId | Bank |
| encryptedAccessToken | Secure token |
| cursor | Incremental sync cursor |
| status | Current account state |
| createdAt | Timestamp |

---

# Why is this the Control Plane?

Notice

The Account Service never

- validates transactions
- categorizes transactions
- writes reports
- performs analytics

Its only responsibility is

```
Managing external integrations.
```

Transaction processing belongs to the Data Plane.

---

# Interview Talking Points

> The Account & Aggregation Service acts as the Control Plane for ingestion. It manages user-to-bank mappings, securely stores access tokens, tracks synchronization cursors, registers webhooks, schedules polling, and orchestrates communication with external aggregators.

> We rely on financial aggregators because they provide a unified interface across thousands of banks, handle authentication securely, normalize transaction formats, and significantly reduce engineering complexity.

> We use a hybrid synchronization strategy. Webhooks provide low-latency updates, while scheduled polling ensures reliability in case notifications are delayed or lost.

> Incremental synchronization is cursor-based, allowing us to fetch only changes since the previous successful sync instead of downloading all transactions repeatedly.

> Since external APIs are the least reliable component of the system, the Account Service implements retries, exponential backoff, adaptive throttling, and reconnection workflows to maintain consistency.

---

# Next Chapter

# Part 5 – Kafka Architecture

Topics

- Why Kafka
- Topic Design
- Partitions
- Consumer Groups
- Producer Configuration
- Ordering
- Idempotence
- Schema Registry
- DLQ
- Replay
- Scaling
- Failure Recovery

# Financial Data Aggregation Platform

# Part 5 – Kafka Architecture & Event-Driven Pipeline

---

# Overview

Kafka is the backbone of the Financial Data Aggregation Platform.

Instead of directly coupling the ingestion layer with downstream processing services, Kafka acts as an event bus that decouples producers from consumers.

Without Kafka

```
Webhook

↓

Validation

↓

Categorization

↓

Database

↓

Reporting
```

Every service depends on every other service.

A failure anywhere blocks the entire pipeline.

With Kafka

```
Webhook

↓

Kafka

↓

Validation

↓

Deduplication

↓

Enrichment

↓

Categorization

↓

Persistence
```

Every stage works independently.

---

# Why Kafka?

Kafka solves several problems.

## 1. Decoupling

Instead of

```
Webhook

↓

Validation Service
```

the webhook publishes an event.

```
Webhook

↓

Kafka
```

Consumers process whenever they are ready.

---

## 2. Replay

Suppose Categorization Service crashes.

Without Kafka

```
Transactions Lost
```

With Kafka

```
Kafka retains events

↓

Consumer restarts

↓

Replay
```

No need to call Plaid again.

---

## 3. Independent Scaling

Validation may require

```
2 Consumers
```

Categorization may require

```
20 Consumers
```

Persistence may require

```
5 Consumers
```

Each scales independently.

---

## 4. Fault Isolation

Suppose ML Categorization becomes slow.

```
Validation

↓

Kafka

↓

Categorization (Slow)

↓

Persistence
```

Only Categorization accumulates lag.

Validation continues.

Webhook continues.

No cascading failures.

---

## 5. Multiple Consumers

One event can feed many downstream systems.

```
transactions.processed

↓

Reporting

Analytics

Notifications

Fraud Detection

ML

Audit
```

Producer publishes once.

Many systems consume independently.

---

# High-Level Kafka Architecture

```
               Webhook

                  │

                  ▼

          Ingestion Service

                  │

                  ▼

          transactions.raw

                  │

                  ▼

        Validation Consumer

                  │

                  ▼

      transactions.validated

                  │

                  ▼

      Enrichment Consumer

                  │

                  ▼

      Categorization Consumer

                  │

                  ▼

     transactions.processed

        ┌────────┼────────┬─────────┐

        ▼        ▼        ▼         ▼

 Reporting Notifications ML Analytics
```

Notice

Every stage communicates only through Kafka.

---

# Topic Design

We intentionally separate topics.

---

## transactions.raw

Published by

```
Ingestion Service
```

Contains

Raw transactions received from Plaid.

No processing has occurred yet.

---

## transactions.validated

Produced after

- Schema validation
- Required field validation

Consumers know the event is structurally valid.

---

## transactions.processed

Produced after

- Deduplication
- Enrichment
- Categorization
- Persistence

Used by

- Reporting
- Analytics
- Notifications
- Machine Learning

---

# Why Multiple Topics?

Instead of one giant topic

```
transactions
```

we separate processing stages.

Advantages

- Independent scaling
- Replay specific stage
- Easier debugging
- Better observability

---

# Event Structure

Example

```json
{
   "eventId":"evt-12345",
   "accountId":"A100",
   "transactionId":"T900",
   "timestamp":"2026-07-12T09:00:00Z",
   "amount":125.80,
   "merchant":"STARBUCKS #1234"
}
```

Important fields

```
eventId

transactionId

accountId

timestamp
```

---

# Message Key

The Kafka message key is extremely important.

We use

```
accountId
```

Example

```
Producer

Key = Account123

↓

Kafka Partition
```

---

# Why accountId?

Because all transactions for one account should remain ordered.

Example

```
Account123

Txn1

Txn2

Txn3
```

Kafka guarantees order within a partition.

If we partition randomly,

```
Txn1

↓

Partition1

Txn2

↓

Partition4

Txn3

↓

Partition2
```

Ordering is lost.

---

# Partitioning Strategy

```
hash(accountId)

↓

Partition Number
```

Example

```
Account100

↓

Partition3

-------------------

Account200

↓

Partition7
```

All transactions of the same account always reach the same partition.

---

# Producer Configuration

Producer settings

```
acks = all

enable.idempotence = true

retries = Integer.MAX_VALUE
```

---

## Why acks=all?

Leader waits until followers replicate the message.

Improves durability.

---

## Why enable.idempotence?

Suppose producer retries.

Without idempotence

```
Txn1

Txn1

Txn1
```

Multiple copies.

With idempotence

Only one copy is written.

Important

Producer idempotence **only prevents duplicate writes between Producer and Kafka**.

It does **not** prevent duplicate webhooks from Plaid.

We still perform deduplication in our processing pipeline.

---

# Consumer Groups

Validation Service

```
Consumer Group

validation-group
```

Suppose

```
8 Partitions
```

Consumers

```
Validation1

Validation2

Validation3

Validation4
```

Kafka distributes partitions automatically.

---

# Scaling Consumers

Suppose

```
8 Partitions
```

and

```
4 Consumers
```

Each gets

```
2 Partitions
```

Increase to

```
8 Consumers
```

Each receives

```
1 Partition
```

Easy horizontal scaling.

---

# Ordering Guarantees

Kafka preserves order

within a partition.

Since

```
accountId

↓

Partition
```

All transactions for one account remain ordered.

Example

```
Txn1

Txn2

Txn3
```

Consumers receive

```
Txn1

↓

Txn2

↓

Txn3
```

---

# Out-of-Order Events

External systems may still send

```
Txn3

Txn1

Txn2
```

because of retries.

Kafka preserves producer order,

not real-world event time.

Therefore

the Ordering Service may maintain a small buffer.

Example

```
Buffer

↓

Sort by Event Timestamp

↓

Forward
```

Interview line

> Kafka preserves partition order, but upstream systems may still generate out-of-order events. We use a small reorder buffer when necessary.

---

# Consumer Offset

Kafka tracks

```
Offset
```

Example

```
Partition3

Offset

100

101

102
```

Suppose consumer crashes after

```
101
```

Restart

↓

Resume from

```
102
```

No lost events.

---

# Dead Letter Queue

Suppose validation repeatedly fails.

Instead of infinite retries

```
Retry

↓

Retry

↓

Retry

↓

DLQ
```

Topic

```
transactions.dlq
```

Operations team investigates later.

---

# Replay

Suppose

```
Categorization Service

↓

Bug
```

Fix deployed.

Replay

```
transactions.validated

↓

Categorization

↓

Persistence
```

No need to fetch from Plaid again.

Replay is one of Kafka's biggest advantages.

---

# Schema Registry

Kafka events evolve.

Version 1

```json
{
   "merchant":"Starbucks"
}
```

Version 2

```json
{
   "merchant":"Starbucks",
   "merchantCategory":"Coffee"
}
```

Schema Registry ensures

- compatibility
- version management
- consumer safety

---

# Monitoring Kafka

Important metrics

- Consumer Lag
- Throughput
- Message Size
- Failed Messages
- DLQ Count
- Partition Utilization
- Producer Latency

---

# Failure Scenarios

## Broker Failure

Kafka replicas elect a new leader.

No data loss.

---

## Consumer Failure

Consumer restarts.

Resumes from committed offset.

---

## Producer Failure

Retries automatically.

Producer idempotence prevents duplicate writes.

---

## Processing Failure

Retry

↓

DLQ

↓

Replay

---

# Design Trade-offs

Why Kafka instead of synchronous APIs?

Kafka

Advantages

- Decoupling
- Replay
- Scaling
- Fault tolerance
- Independent consumers

Trade-offs

- Eventual consistency
- More operational complexity
- Consumer lag
- Harder debugging

---

# Interview Talking Points

> Kafka is the event backbone of the system. It decouples ingestion from processing and enables replay without calling external aggregators again.

> We partition topics by accountId to preserve ordering for transactions belonging to the same account while allowing horizontal scaling.

> Producer idempotence prevents duplicate writes caused by producer retries, while application-level idempotency handles duplicate events coming from external systems like webhooks.

> Every processing stage communicates through Kafka, allowing services to evolve and scale independently.

> Downstream systems such as Reporting, Analytics, Notifications, and ML consume processed events independently without impacting the ingestion pipeline.

---

# Next Chapter

# Part 6 – Processing Pipeline (Most Important)

Topics

- Validation
- Deduplication
- Ordering
- Enrichment
- Merchant Normalization
- Currency Conversion
- Categorization
- Persistence
- Idempotency
- Database Upserts
- Publishing Processed Events

This is typically the deepest technical discussion in senior backend interviews.

# Financial Data Aggregation Platform

# Part 6 – Transaction Processing Pipeline (Core of the System)

> This is the heart of the system. Every transaction entering the platform flows through this event-driven pipeline before it is persisted and exposed to users. Each stage has a single responsibility and communicates through Kafka, enabling independent scaling, fault isolation, and replay.

---

# Overview

The Processing Pipeline converts raw financial transaction data into standardized, enriched, categorized, and queryable information.

The pipeline follows these stages:

```
                    Kafka

                      │

                      ▼

              Validation Service

                      │

                      ▼

            Deduplication Service

                      │

                      ▼

               Ordering Service

                      │

                      ▼

              Enrichment Service

                      │

                      ▼

            Categorization Service

                      │

                      ▼

             Persistence Service

                      │

                      ▼

        transactions.processed Topic

          ┌────────┼────────┬──────────┐

          ▼        ▼        ▼          ▼

    Reporting   Analytics  ML   Notifications
```

Each stage is stateless and can scale independently.

---

# Why Split into Multiple Services?

Instead of building one large processing service

```
Validate

↓

Deduplicate

↓

Categorize

↓

Store
```

we separate responsibilities.

Advantages

- Independent deployment
- Independent scaling
- Easier debugging
- Replay individual stages
- Better ownership between teams

---

# Processing Flow

Suppose Plaid sends

```json
{
   "transactionId":"tx123",
   "accountId":"acc100",
   "merchant":"STARBUCKS #234",
   "amount":8.45,
   "currency":"USD",
   "timestamp":"2026-07-12T09:00:00Z"
}
```

The transaction moves through every stage before becoming visible to users.

---

# Stage 1 – Validation Service

Purpose

Ensure incoming events are structurally correct before processing.

---

## Responsibilities

Validate

- Required fields
- Data types
- Currency format
- Timestamp format
- Account existence
- Schema version

Example

```
transactionId

✓

accountId

✓

timestamp

✓

amount

✓
```

---

## Invalid Events

Suppose

```json
{
   "amount":"ABC"
}
```

This event is invalid.

Do not process it.

Instead

```
Validation Failure

↓

transactions.dlq
```

Operations can inspect it later.

---

## Why Validate Early?

If invalid events enter downstream systems,

they create inconsistent data.

Validation should be the first gate.

---

# Stage 2 – Deduplication Service

One of the most important stages.

---

# Why Duplicates Happen

External systems may retry.

Example

```
Webhook

↓

Timeout

↓

Retry

↓

Same Transaction Again
```

Or

Polling

↓

Same transaction fetched again

Duplicates are expected.

---

# Deduplication Key

We use

```
accountId

+

transactionId
```

Example

```
Account100

Transaction500
```

This uniquely identifies a transaction.

---

# Database Constraint

Transactions Table

```sql
UNIQUE(accountId, transactionId)
```

This guarantees correctness.

---

# Upsert

Instead of

```
INSERT
```

use

```
UPSERT
```

Example

```sql
INSERT ...

ON CONFLICT(accountId, transactionId)

DO UPDATE
```

This makes consumers idempotent.

---

# Why Database Constraint?

Cache may fail.

Application may restart.

Only the database guarantees correctness.

Interview Line

> Cache improves performance. Database guarantees correctness.

---

# Stage 3 – Ordering Service

Kafka preserves order

within a partition.

However,

external systems may still send

```
Txn3

Txn1

Txn2
```

due to retries.

---

# Why Ordering Matters

Suppose

```
Balance Update

↓

Refund

↓

Charge
```

Incorrect ordering may produce incorrect balances.

---

# Solution

Maintain a small reorder buffer.

```
Incoming Events

↓

Buffer

↓

Sort by Event Timestamp

↓

Forward
```

Example

Buffer

```
Txn3

Txn1

Txn2
```

↓

Forward

```
Txn1

Txn2

Txn3
```

---

# Why Small Buffer?

Infinite buffering increases latency.

Most systems use

```
Few Seconds
```

Only reorder within this window.

---

# Stage 4 – Enrichment Service

Raw transaction data is inconsistent.

Example

```
STARBUCKS #1234

Starbucks Store

SBX 567

STARBUCKS NYC
```

All represent

```
Starbucks
```

---

# Responsibilities

Enrichment standardizes data.

Tasks

- Merchant normalization
- Currency conversion
- Geo lookup
- Country lookup
- MCC lookup
- Merchant metadata

Result

```
Merchant

↓

Starbucks

Category Hint

↓

Coffee Shop

Country

↓

USA
```

---

# Merchant Normalization

Use

- Rule Engine
- Lookup Table
- External Merchant Database
- ML (optional)

Cache results in Redis.

---

# Currency Conversion

Suppose

```
100 EUR
```

User prefers

```
USD
```

Convert using latest exchange rates.

Store

```
Original Currency

+

Normalized Currency
```

Never overwrite original values.

---

# Stage 5 – Categorization Service

Purpose

Assign business meaning.

Example

```
Starbucks

↓

Food & Dining
```

or

```
Amazon

↓

Shopping
```

---

# Rule-Based Categorization

Fast

Deterministic

Example

```
Merchant contains Starbucks

↓

Food
```

---

# Machine Learning Categorization

Some merchants are ambiguous.

Example

```
ABC Retail
```

ML predicts

```
Shopping
```

Confidence

```
97%
```

---

# Hybrid Strategy

Rule Engine

↓

If no match

↓

ML

↓

Category

Best of both worlds.

---

# Stage 6 – Persistence Service

The final processing stage.

Responsibilities

- Persist transactions
- Update balances
- Update aggregates
- Publish processed events

---

# Database Write

Insert

```
Transactions
```

Update

```
Account Balance
```

Update

```
Monthly Summary
```

Commit.

---

# After Commit

Publish

```
transactions.processed
```

Consumers

- Reporting
- Notifications
- Analytics
- ML

receive this event.

---

# Why Publish After Persistence?

Never publish before database commit.

Otherwise

```
Reporting

↓

Reads Transaction

↓

Transaction not in DB
```

Creates inconsistency.

---

# Idempotency

Every consumer should safely process duplicate events.

Example

Consumer crashes

↓

Kafka retries

↓

Same event again

No duplicate transaction should appear.

Techniques

- Unique DB Constraint
- UPSERT
- Processed Event Table (optional)

---

# Failure Handling

Suppose Enrichment fails.

```
Retry

↓

Retry

↓

Retry

↓

DLQ
```

Do not block Kafka forever.

---

# Retry Strategy

Transient Errors

- Network timeout
- Redis unavailable
- External lookup failed

Retry

Permanent Errors

- Invalid schema
- Missing transactionId

Send directly to DLQ.

---

# Dead Letter Queue

Topic

```
transactions.dlq
```

Contains

- Invalid events
- Events exceeding retry limit
- Corrupted payloads

Operations team can inspect and replay them later.

---

# Event Replay

Suppose

Categorization logic changes.

Instead of fetching data from Plaid again,

Replay

```
transactions.validated

↓

Categorization

↓

Persistence
```

Kafka makes replay simple.

---

# End-to-End Example

Plaid sends

```
STARBUCKS #123

$8.45
```

↓

Validation

✓

↓

Deduplication

✓

↓

Ordering

✓

↓

Merchant Normalization

↓

Starbucks

↓

Categorization

↓

Food & Dining

↓

Persistence

↓

PostgreSQL

↓

transactions.processed

↓

Reporting Dashboard

User sees

```
Starbucks

Food & Dining

$8.45
```

---

# Scaling the Pipeline

Every stage scales independently.

Example

```
Validation

4 Pods

Deduplication

6 Pods

Enrichment

20 Pods

Categorization

30 Pods

Persistence

10 Pods
```

No service blocks another.

---

# Design Principles

Every stage follows

### Single Responsibility

Each service performs one task.

---

### Stateless

Services store no local state.

State lives in Kafka and databases.

---

### Idempotent

Duplicate events produce the same final state.

---

### Horizontally Scalable

Simply add more consumers.

---

### Replayable

Historical events can be reprocessed.

---

### Fault Isolated

Failure of one stage does not stop upstream ingestion.

---

# Interview Talking Points

> The processing pipeline is implemented as a series of independent Kafka consumers, each responsible for a single transformation. This separation improves scalability, replayability, and maintainability.

> Validation ensures only structurally correct events proceed further, while deduplication guarantees idempotency using a unique `(accountId, transactionId)` constraint and UPSERT semantics.

> Kafka preserves ordering within a partition, but upstream systems may still send out-of-order events. We use a small reorder buffer to handle these cases without introducing excessive latency.

> Enrichment standardizes raw transaction data by normalizing merchant names, converting currencies, and attaching metadata, making downstream categorization more accurate.

> Persistence is the final stage. Only after a successful database commit do we publish a `transactions.processed` event, ensuring downstream consumers never observe data that hasn't been durably stored.

---

# Next Chapter

# Part 7 – Database Design

Topics

- Complete ER Diagram
- Users Table
- Accounts Table
- Transactions Table
- Merchants Table
- Categories Table
- Indexes
- Constraints
- Partitioning
- Sharding
- Read vs Write Optimization
- Redis
- Analytics Warehouse

# Financial Data Aggregation Platform

# Part 7 – Database Design & Storage Architecture

---

# Overview

The database layer is responsible for storing processed financial data while supporting

- Fast transactional queries
- Reporting
- Search
- Analytics
- High availability
- Scalability

The platform uses different storage technologies because one database cannot efficiently solve every problem.

We follow the principle

> **Use the right database for the right workload.**

---

# Storage Architecture

```
                    Processing Pipeline

                           │

                           ▼

                     PostgreSQL
                    (Source of Truth)

                           │

        ┌──────────────────┼─────────────────┐

        ▼                  ▼                 ▼

      Redis          Kafka Events      Analytics DB

        │                                    │

        ▼                                    ▼

 Dashboard APIs                  Snowflake / BigQuery

                                          │

                                          ▼

                                    BI Reports
```

---

# Why Multiple Databases?

Every storage system is optimized differently.

| Storage | Purpose |
|----------|----------|
| PostgreSQL | Transactional data |
| Redis | Low latency reads |
| Snowflake / BigQuery | Analytics |
| Kafka | Event propagation |

Trying to use PostgreSQL for analytics or Kafka for querying leads to poor performance.

---

# Primary Database

We use

```
PostgreSQL
```

because

- ACID transactions
- Strong consistency
- Mature indexing
- Rich SQL support

This database serves

- User APIs
- Transaction queries
- Account information

---

# High-Level ER Diagram

```
Users

   │

   ▼

Accounts

   │

   ▼

Transactions

   │

   ▼

Categories

Merchants
```

---

# Users Table

Stores platform users.

```sql
Users
```

| Column | Description |
|---------|-------------|
| userId | Primary Key |
| name | User name |
| email | Login email |
| createdAt | Registration time |

Example

| userId | Name |
|--------|------|
|1|John|

---

# Accounts Table

One user can connect multiple financial accounts.

Examples

- Checking
- Savings
- Credit Card

---

Schema

| Column | Description |
|---------|-------------|
| accountId | Primary Key |
| userId | FK Users |
| institutionId | Bank |
| accountType | Checking/Savings |
| encryptedAccessToken | Plaid Token |
| cursor | Incremental Sync Cursor |
| status | ACTIVE / ERROR / RECONNECT_REQUIRED |
| balance | Current Balance |

---

Relationship

```
User

↓

Accounts

Checking

Savings

Credit Card
```

---

# Transactions Table

This is the largest table.

Every processed transaction is stored here.

---

Schema

| Column | Description |
|---------|-------------|
| transactionId | Provider transaction id |
| accountId | FK Accounts |
| merchantId | FK Merchant |
| categoryId | FK Category |
| amount | Amount |
| currency | Original currency |
| normalizedAmount | Converted amount |
| transactionTime | Bank transaction time |
| description | Raw description |
| createdAt | Insert time |

---

# Primary Key

We can either

Use

```
transactionId
```

or

```
internalTransactionId
```

Many companies generate their own internal ID.

---

# Idempotency Constraint

One of the most important constraints.

```sql
UNIQUE(accountId, transactionId)
```

This guarantees duplicate webhook deliveries never create duplicate transactions.

---

# Merchant Table

Many transactions belong to the same merchant.

Instead of repeating

```
STARBUCKS

STARBUCKS

STARBUCKS
```

Normalize.

---

Schema

| merchantId | normalizedName | categoryHint |
|------------|----------------|--------------|
|101|Starbucks|Coffee|

Transactions reference

```
merchantId
```

instead of storing duplicate merchant names.

---

# Category Table

Stores categories.

| categoryId | name |
|-------------|------|
|1|Food|
|2|Shopping|
|3|Travel|

Transactions simply reference

```
categoryId
```

---

# Relationships

```
Users

   │

   ▼

Accounts

   │

   ▼

Transactions

   │

   ├────────► Merchant

   │

   └────────► Category
```

---

# Why Normalize?

Suppose

```
50 Million
```

transactions belong to Starbucks.

Instead of storing

```
STARBUCKS
```

50 million times,

store

```
merchantId
```

Database becomes

- smaller
- faster
- easier to update

---

# Index Design

Indexes are critical.

---

## Transactions

Create indexes on

```
(accountId, transactionTime)
```

Supports

```
Recent Transactions
```

---

```
categoryId
```

Supports

```
Food

Shopping
```

queries.

---

```
merchantId
```

Supports

```
Merchant Search
```

---

Composite Index

```
(accountId, transactionTime DESC)
```

Supports

```
Latest Transactions
```

without sorting.

---

# Example Query

```
GET /transactions
```

Query

```sql
SELECT *
FROM Transactions
WHERE accountId=100
ORDER BY transactionTime DESC
LIMIT 100;
```

Composite index makes this extremely efficient.

---

# Search

Simple search

```
Merchant

Category

Date
```

uses PostgreSQL indexes.

If requirements evolve into

```
Full Text Search

Description Search

Merchant Similarity
```

Introduce

```
OpenSearch

Elasticsearch
```

---

# Partitioning

Suppose

```
10 Billion
```

transactions.

One table becomes enormous.

Partition

```
Transactions

↓

2026

↓

July

↓

Daily
```

or

```
Hash(accountId)
```

depending on workload.

---

# Why Partition?

Benefits

- Faster scans
- Easier maintenance
- Faster backups
- Better vacuum performance

---

# Sharding

Eventually

one PostgreSQL instance won't scale.

Shard by

```
userId
```

or

```
accountId
```

Example

```
Shard1

Users 1-1M

---------------

Shard2

Users 1M-2M

---------------

Shard3

Users 2M-3M
```

Each shard owns a subset of users.

---

# Read Replicas

Reads dominate writes.

Architecture

```
Primary

↓

Read Replica1

↓

Read Replica2

↓

Read Replica3
```

Writes

↓

Primary

Reads

↓

Replicas

Improves scalability.

---

# Redis

Redis is **not** the source of truth.

Purpose

- Cache
- Dashboard
- Recent Transactions
- Frequently used reports

Example

```
Dashboard

↓

Redis

↓

2 ms
```

instead of

```
Dashboard

↓

Postgres

↓

120 ms
```

---

# What should be Cached?

Good candidates

- Monthly spending
- Dashboard
- Recent transactions
- Category totals
- User profile

Poor candidates

- Financial transactions during synchronization
- Frequently changing balances

---

# Cache Invalidation

Suppose

new transaction arrives.

```
Persistence

↓

Update PostgreSQL

↓

Invalidate Redis

↓

Recompute Dashboard
```

Never update cache before database commit.

---

# Analytics Database

Operational database is not designed for

```
Annual Reports

BI

ML

Large Aggregations
```

Therefore

processed transactions are copied into

```
Snowflake

BigQuery

Redshift
```

---

# Data Flow

```
transactions.processed

↓

Analytics Pipeline

↓

Snowflake
```

Used for

- Business Intelligence
- Trend analysis
- ML training
- Historical reports

---

# OLTP vs Analytics

| OLTP | Analytics |
|-------|-----------|
| PostgreSQL | Snowflake |
| Small queries | Large scans |
| User APIs | BI |
| ACID | Aggregations |

Never run analytical queries on production OLTP.

---

# Backup Strategy

Database

↓

Daily Backup

↓

Point-in-Time Recovery

↓

Cross-region Replication

Financial systems cannot afford data loss.

---

# Archival Strategy

Suppose

```
10 Years
```

of transactions.

Keep

```
Recent 2 Years
```

in PostgreSQL.

Archive older data to

- Data Lake
- Object Storage
- Analytics Warehouse

Improves OLTP performance.

---

# Database Transactions

When persisting a transaction

```
Insert Transaction

↓

Update Balance

↓

Update Monthly Summary

↓

Commit
```

Only after commit

↓

Publish

```
transactions.processed
```

This guarantees consistency.

---

# Scaling Strategy

Initially

```
One PostgreSQL
```

↓

Read Replicas

↓

Partition Tables

↓

Shard Database

↓

Distributed SQL (if needed)
```

Scale gradually.

---

# Design Trade-offs

Normalized Schema

Advantages

- Less storage
- Easier updates
- Better consistency

Trade-off

Requires joins.

---

Denormalized Dashboard

Advantages

- Fast reads

Trade-off

Needs background aggregation.

---

# Interview Talking Points

> PostgreSQL is the source of truth for transactional data because it provides ACID guarantees and strong consistency.

> The Transactions table enforces idempotency using a unique `(accountId, transactionId)` constraint, preventing duplicate webhook deliveries from creating duplicate records.

> We normalize entities like Merchants and Categories to reduce storage and improve consistency, while using Redis to cache precomputed dashboard data for low-latency reads.

> Analytical workloads are separated from transactional workloads. Processed events are streamed into Snowflake or BigQuery, ensuring large reporting queries never impact user-facing APIs.

> The database scales progressively through read replicas, table partitioning, and eventually sharding by `accountId` or `userId` as data volume grows.

---

# Next Chapter

# Part 8 – Reporting & Analytics Layer

Topics

- Reporting Service
- Dashboard APIs
- Aggregation Pipeline
- Redis Cache
- Analytics Database
- Materialized Views
- Scheduled Aggregations
- Monthly Reports
- Cash Flow
- Spending Trends
- Search
- Performance Optimizations

# Financial Data Aggregation Platform

# Part 8 – Reporting & Analytics Layer

> The Reporting Layer is responsible for serving user-facing APIs with low latency while supporting analytical workloads without impacting the transactional database.

---

# Overview

The processing pipeline has already

- Validated transactions
- Removed duplicates
- Categorized spending
- Stored transactions

Now users want answers like

- How much did I spend this month?
- What are my top spending categories?
- How much did I save?
- How has my spending changed over time?

The Reporting Layer is responsible for answering these questions efficiently.

---

# Why Do We Need a Separate Reporting Layer?

Suppose a user opens the dashboard.

Naively we could execute

```sql
SELECT SUM(amount)
FROM Transactions
WHERE category='Food'
AND month='July';
```

every time.

For

```
100 Million Transactions
```

this becomes expensive.

Instead,

we precompute frequently used aggregates.

---

# High-Level Architecture

```
             transactions.processed

                     │

                     ▼

           Aggregation Service

                     │

      ┌──────────────┼──────────────┐

      ▼              ▼              ▼

   PostgreSQL      Redis      Analytics DB

      │

      ▼

Reporting Service

      │

      ▼

Dashboard APIs
```

---

# Reporting Service

The Reporting Service exposes user-facing APIs.

Responsibilities

- Dashboard
- Reports
- Search
- Spending trends
- Monthly summaries
- Cash flow
- Category summaries

It never talks directly to Plaid.

It only reads processed data.

---

# Reporting APIs

Example APIs

```
GET /dashboard

GET /transactions

GET /reports/monthly

GET /reports/categories

GET /reports/cashflow

GET /reports/trends

GET /reports/networth
```

---

# Dashboard API

Example

```
GET /dashboard
```

Response

```json
{
   "monthlyIncome":9000,
   "monthlyExpenses":6200,
   "cashFlow":2800,
   "topCategories":[
      {
         "category":"Food",
         "amount":800
      }
   ],
   "recentTransactions":[]
}
```

Dashboard should return

```
100–300 ms
```

---

# Why Not Calculate Dashboard Every Request?

Suppose

```
50 Million
```

transactions exist.

Running

```
SUM

GROUP BY

COUNT
```

for every dashboard request would overload PostgreSQL.

Instead,

we precompute summaries.

---

# Aggregation Service

The Aggregation Service listens to

```
transactions.processed
```

Whenever a transaction is persisted,

the service updates

- Monthly totals
- Category totals
- Merchant totals
- Cash flow
- Dashboard cache

---

# Flow

```
Transaction Processed

↓

Kafka

↓

Aggregation Service

↓

Redis

↓

Dashboard API
```

---

# Example

Transaction

```
Starbucks

$10
```

Aggregation Service

updates

```
Food

↓

+10
```

Redis now stores

```
Food

$810
```

Dashboard simply reads

```
810
```

instead of recalculating.

---

# Why Event-Driven Aggregation?

Advantages

- Dashboard is always up-to-date
- Very fast reads
- Low database load

---

# Redis Cache

Redis stores

- Dashboard
- Monthly summary
- Recent transactions
- Category totals
- Frequently viewed reports

---

Example

Key

```
dashboard:user123
```

Value

```json
{
   "income":9000,
   "expenses":6200
}
```

---

# Cache Flow

```
Dashboard Request

↓

Redis

↓

Return

2 ms
```

instead of

```
Dashboard Request

↓

PostgreSQL

↓

Complex Aggregation

↓

120 ms
```

---

# Cache Invalidation

Suppose

new transaction

```
Amazon

$120
```

arrives.

Flow

```
Persistence

↓

Database Commit

↓

transactions.processed

↓

Aggregation Service

↓

Redis Update
```

Notice

Database is updated first.

Cache second.

This prevents stale data.

---

# Monthly Summary Table

Instead of calculating monthly spending repeatedly,

maintain

```
MonthlySummary
```

Table

| userId | month | income | expenses | savings |
|---------|-------|---------|-----------|----------|

Example

|100|July|9000|6200|2800|

Dashboard reads directly.

---

# Category Summary Table

Another precomputed table.

| userId | month | category | amount |
|---------|-------|----------|---------|
|100|July|Food|810|
|100|July|Shopping|1200|

Very efficient.

---

# Merchant Summary

Example

| userId | merchant | amount |
|---------|-----------|---------|
|100|Starbucks|450|
|100|Amazon|2200|

Useful for

```
Top Merchants
```

report.

---

# Cash Flow Report

Instead of

```
SELECT

SUM()

GROUP BY
```

every request,

Aggregation Service updates

```
Income

Expenses

Savings
```

incrementally.

---

# Spending Trends

Need

```
Jan

Feb

Mar

Apr

May
```

Store

monthly summaries.

Dashboard simply plots them.

---

# Search

Search by

- Merchant
- Category
- Amount
- Date

Small datasets

↓

PostgreSQL indexes.

Large datasets

↓

OpenSearch.

---

# Analytics Database

Operational reporting

≠

Business Intelligence.

We copy processed events into

```
Snowflake

BigQuery

Redshift
```

Used for

- Yearly reports
- BI dashboards
- Product analytics
- Data science

---

# ETL Pipeline

```
transactions.processed

↓

Kafka

↓

Analytics Loader

↓

Snowflake
```

Completely separate from user APIs.

---

# Materialized Views

Sometimes

PostgreSQL Materialized Views

can precompute

```
Monthly Spending

Category Totals
```

Useful for

smaller deployments.

Large systems

typically use

Aggregation Service

+

Redis.

---

# Read Path

Dashboard

```
Client

↓

API Gateway

↓

Reporting Service

↓

Redis

↓

Return
```

Transaction Search

```
Client

↓

Reporting Service

↓

PostgreSQL
```

Historical Reports

```
Client

↓

Reporting Service

↓

Analytics DB
```

Different workloads use different storage.

---

# Read vs Write Ratio

Typical

```
10 : 1

Reads : Writes
```

Many users refresh dashboards frequently.

Very few transactions arrive every second.

Optimizing reads is critical.

---

# Scaling Reporting

Reporting Service is stateless.

Scale

```
2 Pods

↓

10 Pods

↓

50 Pods
```

behind a load balancer.

Redis also supports clustering.

Analytics database scales independently.

---

# Failure Scenario

Suppose Redis crashes.

Fallback

```
Reporting

↓

PostgreSQL

↓

Return Response
```

Higher latency

but

no outage.

---

# Another Failure

Aggregation Service is down.

Transactions continue

↓

PostgreSQL

Dashboard may temporarily lag

↓

Aggregation Service restarts

↓

Replay

```
transactions.processed
```

↓

Rebuild Redis.

This is another benefit of Kafka.

---

# Design Trade-offs

Calculate on Demand

Advantages

- Simple

Disadvantages

- Slow
- Expensive

---

Precompute

Advantages

- Fast dashboards
- Low latency
- Lower DB load

Trade-off

- More storage
- Eventual consistency

---

# Interview Talking Points

> User-facing dashboards should never perform expensive aggregations over millions of transactions on every request. Instead, an Aggregation Service continuously updates precomputed summaries as transactions are processed.

> Redis stores frequently accessed dashboard data, while PostgreSQL remains the source of truth. If Redis becomes unavailable, the Reporting Service can temporarily fall back to PostgreSQL at the cost of higher latency.

> Analytical workloads are isolated from transactional workloads by streaming processed events into Snowflake or BigQuery, ensuring that business intelligence queries never affect customer-facing APIs.

> Different APIs use different storage systems. Transaction searches read from PostgreSQL, dashboards read from Redis, and historical trend analysis reads from the analytics warehouse.

> Kafka enables rebuilding derived data stores. If Redis or aggregation tables are lost, we can replay `transactions.processed` events to reconstruct them without contacting Plaid again.

---

# Next Chapter

# Part 9 – Reliability, Reconciliation & Distributed Job Scheduler

Topics

- Why reconciliation is required
- Source of Truth
- Missed Webhooks
- Scheduled Reconciliation Jobs
- Distributed Job Scheduler (reuse previous design)
- Retry Strategy
- DLQ
- Replay
- Failure Recovery
- End-to-End Reconciliation Flow

# Financial Data Aggregation Platform

# Part 9 – Reliability, Reconciliation & Distributed Job Scheduler

> Financial systems prioritize **correctness over latency**. Even if webhooks are delayed, messages are duplicated, or external APIs fail temporarily, the system must eventually converge to the correct state. This chapter explains how reliability is achieved using retries, reconciliation jobs, Kafka replay, and the distributed Job Scheduler designed earlier.

---

# Overview

No external system is perfect.

Problems that can occur

- Missed webhooks
- Duplicate webhooks
- Network failures
- API outages
- Rate limiting
- Consumer crashes
- Database failures

Therefore the platform must be designed assuming failures are normal.

---

# Reliability Principles

The platform follows these principles.

✓ Idempotency

✓ Retry

✓ Replay

✓ Dead Letter Queue

✓ Eventual Consistency

✓ Reconciliation

Together these guarantee financial correctness.

---

# Source of Truth

One important design decision is

Who is the source of truth?

```
Bank

↓

Plaid

↓

Our Platform
```

The bank remains the source of truth.

Our platform maintains an eventually consistent copy.

Whenever discrepancies occur,

we synchronize again.

---

# Why Reconciliation?

Suppose

```
User

↓

Bank

↓

New Transaction
```

Plaid attempts webhook delivery.

```
Webhook

↓

Network Failure

↓

Webhook Lost
```

Without reconciliation

Our database never receives the transaction.

User sees incorrect balance.

---

# Solution

Run scheduled reconciliation jobs.

```
Nightly

↓

Fetch Incremental Changes

↓

Compare

↓

Repair Missing Data
```

---

# Reconciliation Architecture

```
                  Job Scheduler

                       │

                       ▼

              Reconciliation Worker

                       │

                       ▼

              Plaid Incremental Sync

                       │

             Compare With PostgreSQL

                       │

          Missing Transactions?

             │               │

            Yes             No

             │               │

             ▼               ▼

 Insert Missing Rows     Finish Job
```

---

# Why Reconciliation Instead of Full Sync?

Suppose

```
20 Million Accounts
```

Fetching all transactions daily would be extremely expensive.

Instead

use

```
Cursor-Based Incremental Sync
```

Only changes since the previous synchronization are fetched.

---

# Reconciliation Frequency

Different schedules may exist.

Example

| Job | Frequency |
|------|-----------|
|Incremental Sync|Every 30 minutes|
|Balance Refresh|Hourly|
|Transaction Reconciliation|Nightly|
|Historical Repair|Weekly|

Frequency depends on business requirements.

---

# Distributed Job Scheduler

Instead of creating another scheduling mechanism,

reuse our existing Job Scheduler.

Architecture

```
Jobs

↓

TaskExecution

↓

Scheduler

↓

Kafka

↓

Worker
```

The reconciliation process becomes just another scheduled job.

---

# Jobs Table

Example

| JobId | JobName | Cron |
|--------|----------|------|
|101|Nightly Reconciliation|0 2 * * *|

Meaning

```
Every day

2:00 AM
```

---

# TaskExecution

Current execution

| ExecutionId | JobId | ScheduledTime | Status |
|-------------|-------|---------------|--------|
|5001|101|2:00 AM|PENDING|

Scheduler polls

```
scheduledTime <= NOW()
```

Publishes execution.

Worker performs reconciliation.

---

# Reconciliation Worker

Responsibilities

- Read execution request
- Load Job definition
- Call Plaid
- Fetch incremental transactions
- Compare with database
- Insert missing records
- Update execution status

---

# Comparison Algorithm

Step 1

Fetch from Plaid

```
Txn100

Txn101

Txn102
```

Step 2

Fetch local transactions

```
Txn100

Txn102
```

Difference

```
Txn101
```

Insert

```
Txn101
```

Database becomes consistent.

---

# Updated Transactions

Sometimes

transactions are modified.

Example

```
Pending

↓

Posted
```

Reconciliation also updates

- Amount
- Merchant
- Status
- Description

---

# Deleted Transactions

Banks occasionally remove transactions.

Plaid reports

```
Removed Transactions
```

Instead of hard delete,

mark

```
Deleted = TRUE
```

This preserves audit history.

---

# Retry Strategy

Suppose

Plaid returns

```
HTTP 503
```

Worker retries.

Example

```
Retry1

30 sec

Retry2

60 sec

Retry3

120 sec
```

Our Job Scheduler creates a new

```
TaskExecution
```

with the delayed

```
scheduledTime
```

Exactly like the scheduler design we built earlier.

---

# Retry Flow

```
Worker

↓

Plaid

↓

503

↓

FAILED

↓

Create Retry TaskExecution

↓

Scheduler

↓

Kafka

↓

Worker
```

Retries are simply future executions.

---

# Permanent Failure

Suppose retries exceed

```
MaxRetry = 3
```

Worker

↓

Marks execution

```
FAILED
```

Publishes

```
reconciliation.dlq
```

Operations team investigates later.

---

# Why Kafka Replay?

Suppose

Categorization Service had a bug.

No need to call Plaid.

Replay

```
transactions.processed

↓

Categorization

↓

Persistence
```

Rebuild everything.

Kafka becomes the recovery mechanism.

---

# Consumer Failure

Suppose

Persistence Consumer crashes.

Kafka retains

```
Offset
```

Restart

↓

Continue

No data loss.

---

# Database Failure

Suppose PostgreSQL is temporarily unavailable.

Persistence consumer

↓

Retry

↓

Retry

↓

Kafka retains events

↓

Database recovers

↓

Resume processing

No events lost.

---

# Idempotency During Recovery

Suppose replay inserts

```
Txn100
```

again.

Unique constraint

```sql
UNIQUE(accountId, transactionId)
```

plus UPSERT

ensures

no duplicate rows.

---

# Monitoring Reliability

Important metrics

- Webhook Success Rate
- Poll Success Rate
- Retry Count
- DLQ Size
- Scheduler Lag
- Kafka Consumer Lag
- Reconciliation Duration
- Missing Transaction Count

Alert if

- DLQ growing
- Scheduler delayed
- Kafka lag increasing
- Reconciliation failing repeatedly

---

# Failure Scenarios

## Scenario 1

Webhook Lost

Solution

```
Polling

+

Nightly Reconciliation
```

---

## Scenario 2

Duplicate Webhook

Solution

```
Idempotent Consumer

+

Database Constraint
```

---

## Scenario 3

Plaid Outage

Solution

```
Retry

↓

Backoff

↓

Reconciliation Later
```

---

## Scenario 4

Consumer Crash

Solution

```
Kafka Offset

↓

Restart

↓

Continue
```

---

## Scenario 5

Database Crash

Solution

```
Kafka Replay

↓

UPSERT

↓

Recover
```

---

# End-to-End Reliability Flow

```
Transaction

↓

Plaid

↓

Webhook

↓

Kafka

↓

Processing

↓

PostgreSQL

↓

Dashboard

↓

Nightly Reconciliation

↓

Compare With Plaid

↓

Repair Missing Transactions

↓

Eventually Consistent
```

---

# Design Trade-offs

Continuous Polling

Advantages

- Faster consistency

Disadvantages

- Higher API cost
- More rate limiting

---

Nightly Reconciliation

Advantages

- Lower API usage
- Guarantees correctness

Trade-off

- Temporary inconsistency

---

Replay

Advantages

- No external API calls
- Easy recovery

Trade-off

- Additional Kafka storage

---

# Interview Talking Points

> Banks are the source of truth, while our platform maintains an eventually consistent copy. Reconciliation jobs ensure long-term correctness even if webhooks are missed.

> We reuse our distributed Job Scheduler for reconciliation instead of building a separate scheduling mechanism. Every reconciliation run is simply another scheduled execution.

> Retries are modeled as future TaskExecution rows with exponentially delayed scheduled times. The Scheduler remains generic because it only executes pending tasks.

> Kafka provides replay capabilities, allowing downstream services to rebuild derived data stores without fetching historical data from Plaid again.

> Idempotent consumers, unique database constraints, retries, DLQs, and scheduled reconciliation together provide a robust reliability strategy suitable for financial systems.

---

# Next Chapter

# Part 10 – Scalability, Security & Observability

Topics

- Horizontal Scaling
- Load Balancing
- Stateless Services
- Database Scaling
- Kafka Scaling
- Redis Clustering
- Security Architecture
- Encryption
- OAuth
- Tokenization
- Secrets Management
- Monitoring
- Logging
- Distributed Tracing
- Alerting
- Production Readiness

# Financial Data Aggregation Platform

# Part 10 – Scalability, Security & Observability

> A production financial platform must not only process transactions correctly but also scale to millions of users while protecting sensitive financial data and providing complete operational visibility. This chapter discusses how the platform is made scalable, secure, and observable.

---

# Overview

At this point our platform consists of

```
Users

↓

API Gateway

↓

Account & Aggregation Service

↓

Plaid

↓

Webhook / Polling

↓

Kafka

↓

Processing Pipeline

↓

PostgreSQL

↓

Redis

↓

Reporting APIs
```

Now we need to ensure it can

- Scale horizontally
- Handle failures
- Protect sensitive data
- Be monitored in production

---

# Horizontal Scaling

One of the fundamental design principles is

> Every service should be stateless.

Since application servers do not store session data,

any instance can process any request.

Architecture

```
                 Load Balancer

                       │

      ┌────────────────┼────────────────┐

      ▼                ▼                ▼

   API-1            API-2            API-3
```

Adding capacity simply means adding more instances.

---

# API Gateway Scaling

Multiple API Gateway instances run behind a Load Balancer.

```
Users

↓

AWS ALB / NGINX

↓

Gateway-1

Gateway-2

Gateway-3
```

Benefits

- High availability
- Horizontal scaling
- No single point of failure

---

# Account Service Scaling

The Account Service is stateless.

Multiple instances can

- Generate Link Tokens
- Exchange Tokens
- Trigger Syncs
- Register Webhooks

```
Load Balancer

↓

Account Service

↓

5 Instances
```

---

# Ingestion Service Scaling

Suppose Plaid sends

```
100,000 Webhooks
```

We simply increase

```
Webhook Receivers
```

All publish into Kafka.

```
Webhook

↓

Ingestion-1

↓

Kafka

------------------

Webhook

↓

Ingestion-2

↓

Kafka
```

---

# Kafka Scaling

Kafka scales using

```
Partitions
```

Suppose

```
transactions.raw

16 Partitions
```

Consumers

```
Validation-1

Validation-2

Validation-3

Validation-4
```

Each consumer owns different partitions.

Need more throughput?

Increase

```
Partitions

+

Consumers
```

---

# Consumer Scaling

Example

```
8 Partitions

↓

4 Consumers

↓

2 Partitions Each
```

Increase to

```
8 Consumers

↓

1 Partition Each
```

Horizontal scaling becomes trivial.

---

# Processing Pipeline Scaling

Each service scales independently.

```
Validation

↓

4 Pods

----------------

Deduplication

↓

8 Pods

----------------

Categorization

↓

20 Pods

----------------

Persistence

↓

10 Pods
```

No service blocks another.

---

# PostgreSQL Scaling

Initially

```
Primary
```

↓

Read Replicas

↓

Partitioning

↓

Sharding

Scale only when necessary.

---

# Read Replicas

Most APIs are read-heavy.

Architecture

```
Primary

↓

Replica-1

Replica-2

Replica-3
```

Writes

↓

Primary

Reads

↓

Replicas

---

# Database Sharding

Eventually

one database becomes insufficient.

Shard by

```
accountId
```

or

```
userId
```

Example

```
Shard-1

Accounts A-M

------------------

Shard-2

Accounts N-Z
```

Every shard manages a subset of users.

---

# Redis Scaling

Redis Cluster

```
Dashboard Cache

↓

Redis Cluster

↓

Shard-1

Shard-2

Shard-3
```

Allows

- More memory
- Higher throughput
- High availability

---

# CDN

Static assets

```
Images

JavaScript

CSS
```

served through

```
CloudFront

Cloudflare
```

Reduces API load.

---

# Auto Scaling

CPU

>

70%

↓

Launch More Pods

Traffic decreases

↓

Terminate Pods

Kubernetes HPA or cloud auto-scaling can handle this automatically.

---

# Security

Financial systems require multiple layers of protection.

Never rely on one security mechanism.

---

# Authentication

Users authenticate using

```
OAuth2

+

JWT
```

Flow

```
Login

↓

Identity Provider

↓

JWT

↓

API Gateway
```

---

# Authorization

Every API validates

```
JWT

↓

User

↓

Account Ownership
```

A user can only access their own accounts.

---

# Plaid Tokens

Never store

```
Username

Password

MFA
```

Instead store

```
Encrypted Access Token
```

using

- AWS KMS
- HashiCorp Vault
- Secrets Manager

---

# Encryption

Data In Transit

```
TLS 1.3
```

Data At Rest

```
AES-256
```

Database backups should also be encrypted.

---

# Field-Level Encryption

Highly sensitive fields

- Account Number
- Routing Number
- SSN (if applicable)

are encrypted individually.

Even database administrators cannot read them directly.

---

# Tokenization

Sensitive identifiers

↓

Replace with

```
Random Token
```

Applications operate on tokens instead of raw PII whenever possible.

---

# Secrets Management

Never store secrets in

```
application.yml

application.properties

GitHub
```

Instead

```
AWS Secrets Manager

HashiCorp Vault

Azure Key Vault
```

---

# Audit Logging

Every security-sensitive action is logged.

Examples

- User Login
- Account Linked
- Account Disconnected
- Token Refreshed
- Report Generated

Audit logs should be immutable.

---

# Rate Limiting

Prevent abuse.

Example

```
100 Requests

Per Minute

Per User
```

Gateway returns

```
429

Too Many Requests
```

---

# Observability

Three pillars

```
Metrics

Logs

Tracing
```

---

# Metrics

Collect

- TPS
- API Latency
- Kafka Lag
- Retry Count
- DLQ Size
- Sync Duration
- Cache Hit Rate
- DB Connections

Prometheus

↓

Grafana

---

# Logging

Use

```
Structured JSON Logs
```

Example

```json
{
   "transactionId":"tx100",
   "accountId":"A101",
   "status":"Processed"
}
```

Avoid free-text logs.

---

# Correlation ID

Every request receives

```
Correlation ID
```

Example

```
API

↓

Kafka

↓

Validation

↓

Persistence

↓

Reporting
```

Same Correlation ID travels through the entire pipeline.

Makes debugging much easier.

---

# Distributed Tracing

Use

```
OpenTelemetry

↓

Jaeger

Zipkin

AWS X-Ray
```

Trace

```
User Request

↓

API Gateway

↓

Kafka

↓

Persistence

↓

Redis

↓

Response
```

Interviewers like hearing this.

---

# Alerts

Important alerts

- Kafka Lag
- DLQ Growth
- API Errors
- DB Replication Lag
- Redis Down
- Plaid API Failures
- Scheduler Delay

Alerts should notify operations teams immediately.

---

# Health Checks

Every service exposes

```
/health

/ready

/live
```

Kubernetes removes unhealthy instances automatically.

---

# Circuit Breaker

Suppose Plaid becomes unavailable.

Instead of continuously retrying

```
Plaid

↓

Failure

↓

Retry

↓

Failure

↓

Retry
```

Use

```
Circuit Breaker

↓

Open

↓

Fail Fast

↓

Half Open

↓

Recover
```

Prevents cascading failures.

---

# Production Readiness

A production-ready platform should include

✓ Auto Scaling

✓ Multi-AZ Deployment

✓ Multi-Region Backups

✓ Disaster Recovery

✓ Immutable Infrastructure

✓ Infrastructure as Code

✓ Continuous Deployment

✓ Feature Flags

---

# Disaster Recovery

Suppose an AWS region fails.

Use

```
Primary Region

↓

Replication

↓

Secondary Region
```

Restore

- PostgreSQL
- Redis
- Kafka

Recovery objectives

```
RPO

≈ Minutes

RTO

≈ Minutes
```

---

# Design Trade-offs

Strong Security

Advantages

- Regulatory compliance
- User trust

Trade-off

- Higher latency
- Operational complexity

---

Caching

Advantages

- Fast dashboards
- Reduced DB load

Trade-off

- Cache invalidation
- Eventual consistency

---

Auto Scaling

Advantages

- Cost optimization
- Handles traffic spikes

Trade-off

- Operational complexity

---

# Interview Talking Points

> Every service in the platform is stateless, allowing horizontal scaling simply by adding more instances behind a load balancer.

> Kafka scales using partitions and consumer groups, while PostgreSQL scales progressively through read replicas, partitioning, and eventually sharding.

> Security is implemented in layers: OAuth for authentication, JWT for authorization, TLS for encryption in transit, AES-256 for encryption at rest, field-level encryption for sensitive PII, and Secrets Manager for credential storage.

> We implement full observability using metrics, structured logging, distributed tracing, and correlation IDs, allowing us to trace a transaction across the entire platform.

> Production systems should automatically detect failures, trigger alerts, and recover through retries, circuit breakers, auto-scaling, and disaster recovery strategies.

---

# Next Chapter

# Part 11 – Complete End-to-End Walkthrough & Interview Discussion

Topics

- End-to-End Transaction Lifecycle
- Complete Sequence Diagram
- Request Walkthrough
- Common Follow-up Questions
- Trade-offs
- Alternative Designs
- Interview Summary
- 5-Minute Answer
- 10-Minute Answer
- Senior-Level Talking Points

# Financial Data Aggregation Platform

# Part 11 – Complete End-to-End Walkthrough, Interview Discussion & Trade-offs

> This chapter ties together every component we've designed and walks through the complete lifecycle of a transaction—from the moment a user links a bank account until they view reports on the dashboard. It also covers common interview questions, trade-offs, and how to present the design confidently.

---

# Complete System Architecture

```
                           Mobile / Web Client

                                   │

                                   ▼

                              API Gateway

                                   │

             ┌─────────────────────┴─────────────────────┐

             ▼                                           ▼

   Account & Aggregation Service               Reporting Service

             │                                           │

             ▼                                           ▼

       Plaid / Yodlee APIs                     Redis / PostgreSQL

             │

      Webhooks / Polling

             │

             ▼

      Ingestion Service

             │

             ▼

        Kafka Topics

             │

             ▼

   Validation Service

             │

             ▼

 Deduplication Service

             │

             ▼

   Ordering Service

             │

             ▼

  Enrichment Service

             │

             ▼

 Categorization Service

             │

             ▼

 Persistence Service

             │

             ▼

       PostgreSQL

             │

             ▼

 transactions.processed

      ┌────────┼─────────┬────────────┐

      ▼        ▼         ▼            ▼

Reporting  Analytics  Notifications   ML
```

---

# End-to-End User Journey

Let's walk through the complete lifecycle.

---

# Step 1 – User Connects Bank

User opens

```
Connect Bank
```

Frontend requests

```
POST /accounts/link-token
```

Backend

↓

Plaid

↓

Returns

```
Link Token
```

Frontend launches

```
Plaid Link SDK
```

---

# Step 2 – User Authenticates

User authenticates with

```
Bank

through

Plaid
```

Our application never receives

- Username
- Password
- MFA

Plaid returns

```
Public Token
```

---

# Step 3 – Exchange Token

Backend calls

```
Plaid

↓

Exchange Public Token

↓

Access Token
```

Access Token is

- encrypted
- stored securely

Account status

```
CONNECTED
```

---

# Step 4 – Register Webhook

Backend registers

```
https://api.company.com/webhooks/plaid
```

Now Plaid notifies us whenever new transactions arrive.

---

# Step 5 – Initial Synchronization

Immediately after linking,

fetch

```
6–12 months
```

of historical transactions.

```
Plaid

↓

Pagination

↓

Kafka
```

Historical data enters the processing pipeline.

---

# Step 6 – Incremental Updates

Later

user swipes card.

```
Bank

↓

Plaid

↓

Webhook
```

Webhook payload

```
New data available
```

Ingestion Service fetches

incremental transactions using

```
Cursor
```

---

# Step 7 – Publish to Kafka

Ingestion Service publishes

```
transactions.raw
```

Example

```json
{
  "transactionId":"TX100",
  "accountId":"ACC101",
  "merchant":"STARBUCKS #4421",
  "amount":8.75
}
```

---

# Step 8 – Validation

Validation Service checks

- Schema
- Required fields
- Timestamp
- Currency
- Account existence

Invalid events

↓

DLQ

Valid events

↓

transactions.validated

---

# Step 9 – Deduplication

Duplicate webhook?

Polling returns same transaction?

Use

```
UNIQUE(accountId, transactionId)
```

and

```
UPSERT
```

Duplicate safely ignored.

---

# Step 10 – Ordering

Kafka preserves partition order.

External systems may still send

```
Txn3

Txn1

Txn2
```

Ordering Service

↓

Small buffer

↓

Timestamp ordering

↓

Forward

---

# Step 11 – Enrichment

Raw Merchant

```
STARBUCKS #4421
```

Normalize

↓

```
Starbucks
```

Additional enrichment

- Country
- Currency
- Merchant metadata

---

# Step 12 – Categorization

Rules

↓

ML

↓

Assign

```
Food & Dining
```

---

# Step 13 – Persistence

Persistence Service

writes

```
Transactions

Accounts

Monthly Summary
```

Commit.

Only after commit

publish

```
transactions.processed
```

---

# Step 14 – Aggregation

Aggregation Service consumes

```
transactions.processed
```

Updates

- Dashboard
- Monthly totals
- Merchant totals
- Category totals

Stores

```
Redis
```

---

# Step 15 – Dashboard

User opens dashboard.

```
Dashboard

↓

Redis

↓

2-10 ms
```

instead of

```
Dashboard

↓

Complex SQL

↓

100+ ms
```

---

# End-to-End Sequence Diagram

```
User

↓

Plaid Link

↓

Account Service

↓

Plaid

↓

Webhook

↓

Ingestion

↓

Kafka

↓

Validation

↓

Deduplication

↓

Ordering

↓

Enrichment

↓

Categorization

↓

Persistence

↓

PostgreSQL

↓

transactions.processed

↓

Aggregation

↓

Redis

↓

Dashboard
```

---

# Failure Scenario 1

Webhook Lost

Solution

```
Polling

+

Nightly Reconciliation
```

Eventually transaction appears.

---

# Failure Scenario 2

Duplicate Webhook

Solution

```
UPSERT

+

Unique Constraint
```

No duplicate transaction.

---

# Failure Scenario 3

Plaid Down

Solution

```
Retry

↓

Backoff

↓

Later Poll

↓

Reconciliation
```

---

# Failure Scenario 4

Consumer Crash

Kafka retains

Offset

↓

Consumer Restarts

↓

Continue Processing

---

# Failure Scenario 5

Redis Down

Dashboard

↓

PostgreSQL

Higher latency

No outage.

---

# Failure Scenario 6

Analytics Database Down

Only

BI Reports

are affected.

User APIs continue working.

---

# Design Decisions

## Why Kafka?

Because

- Decoupling
- Replay
- Horizontal scaling
- Fault isolation

---

## Why PostgreSQL?

Because

- ACID
- Strong consistency
- Financial correctness

---

## Why Redis?

Because dashboards are read-heavy.

Precompute

↓

Cache

↓

Fast reads.

---

## Why Plaid?

Instead of integrating with thousands of banks,

integrate once.

Plaid provides

- OAuth
- Security
- Unified APIs
- Webhooks
- Cursor Sync

---

## Why Event-Driven?

Services become

- Independent
- Scalable
- Replayable

---

## Why Job Scheduler?

Reuse existing scheduler for

- Reconciliation
- Balance refresh
- Polling jobs

Avoid multiple scheduling systems.

---

# Trade-offs

## PostgreSQL

Advantages

- Consistency

Trade-off

- Harder to scale writes

---

## Kafka

Advantages

- Replay
- Decoupling

Trade-off

- Operational complexity

---

## Redis

Advantages

- Extremely fast reads

Trade-off

- Cache invalidation

---

## Eventual Consistency

Advantages

- Better scalability

Trade-off

Dashboard may lag by

```
few seconds
```

---

# Common Interview Questions

---

## Why not call Plaid from Reporting Service?

Because

Reporting should never depend on external APIs.

Always read local processed data.

---

## Why not process transactions synchronously?

Because

Plaid APIs

↓

Network

↓

Categorization

↓

Database

↓

Reporting

would produce high latency.

Kafka decouples everything.

---

## Why not compute reports every request?

Expensive.

Millions of transactions.

Precompute using Aggregation Service.

---

## Why not use MongoDB?

Financial systems require

- ACID
- Joins
- Constraints

PostgreSQL is a better fit.

---

## Why UPSERT?

Consumers may receive duplicate events.

UPSERT makes processing idempotent.

---

## Why Cursor Sync?

Instead of downloading

```
Entire History
```

download

```
Only Changes
```

Reduces

- API cost
- Network traffic
- Processing time

---

# How to Present This Design (5–7 Minutes)

## Step 1

Requirements

```
Functional

Non-functional
```

---

## Step 2

High-Level Architecture

```
Ingestion

↓

Processing

↓

Storage

↓

Reporting
```

---

## Step 3

Explain

Control Plane

↓

Data Plane

---

## Step 4

Explain

Kafka

↓

Processing Pipeline

↓

Persistence

---

## Step 5

Explain

Reporting

↓

Redis

↓

Analytics

---

## Step 6

Explain

Failures

↓

Retries

↓

DLQ

↓

Replay

↓

Reconciliation

---

## Step 7

Explain

Scaling

↓

Security

↓

Observability

---

# Senior-Level Talking Points

These are excellent statements to use naturally during the interview.

> Banks remain the source of truth, while our platform maintains an eventually consistent representation using continuous synchronization and reconciliation.

---

> The Account & Aggregation Service acts as the control plane by managing integrations, tokens, cursors, and synchronization state, while the Kafka-based processing pipeline forms the data plane.

---

> Kafka decouples ingestion from processing, allowing independent scaling, replay, and fault isolation across processing stages.

---

> Consumers are designed to be idempotent using database constraints and UPSERT semantics because duplicate events are inevitable when integrating with external systems.

---

> Reporting APIs never depend on external aggregators. They serve preprocessed and cached data to provide low-latency responses.

---

> We reuse the distributed Job Scheduler for polling, reconciliation, and balance refreshes instead of introducing separate scheduling infrastructure.

---

> We optimize reads using Redis and precomputed summaries while preserving PostgreSQL as the source of truth for transactional data.

---

# Final Interview Summary

> "The platform integrates with financial aggregators like Plaid to connect users with banks. Account management is handled by a Control Plane that securely manages tokens, synchronization state, and webhook registrations. Transactions are ingested via webhooks and polling, published to Kafka, and processed through independent stages for validation, deduplication, ordering, enrichment, categorization, and persistence. PostgreSQL serves as the transactional source of truth, Redis provides low-latency dashboards, and processed events feed downstream analytics, notifications, and machine learning systems. Reliability is achieved through retries, DLQs, Kafka replay, and scheduled reconciliation jobs using our distributed Job Scheduler. The architecture is horizontally scalable, secure, observable, and designed to maintain financial correctness while supporting millions of users."

---

# Congratulations 🎉

This completes the full Financial Data Aggregation System Design.

You now have a complete design covering:

- Requirements
- APIs
- High-Level Architecture
- Account & Aggregation Service
- Kafka
- Processing Pipeline
- Database Design
- Reporting & Analytics
- Reliability & Reconciliation
- Scalability
- Security
- Observability
- End-to-End Flow
- Interview Discussion
- Trade-offs
- Senior-Level Talking Points

This is the level of detail expected in Senior Backend Engineer and Staff Engineer system design interviews.


# Real-Time Fraud Detection System Design

---

# 1. Introduction

## Interview Opening

> "I'll start by clarifying the problem. We're designing a real-time fraud detection system that processes high-volume transactions—on the order of tens of thousands per second—and needs to make decisions within around **100–200 milliseconds**. The goal is to accurately detect fraud while minimizing false positives. I'll assume this system is part of the transaction authorization flow and participates in real-time decisioning."

### Alternative (Fraud-as-a-Service)

If the fraud engine is external:

> "I'll assume this system is integrated as an external fraud service that provides a risk score and recommendation, while the final approval decision is made by the issuing bank or payment processor."

Architecture:

```
Payment Processor
       │
       ▼
 Fraud Detection API
       │
       ▼
Risk Score + Recommendation
       │
       ▼
Payment Processor / Issuing Bank
```

Example:

```
Risk Score = 0.92

Recommendation = DECLINE
```

The issuing bank ultimately decides whether to approve or decline the transaction.

**Interview Line**

> "In this model, we act as a decision-support system rather than the final decision maker."

---

# 2. Requirements

## Functional Requirements

- Detect fraud in real time
- Approve / Decline / Review transactions
- Generate risk score
- Store transaction history
- Support audit trail
- Support model retraining

---

## Non-Functional Requirements

- Ultra-low latency (10–100 ms, target <200 ms)
- High throughput (50K+ TPS)
- High availability
- High accuracy
- Low false positives
- Horizontal scalability
- Strong auditability

**Interview Line**

> "Latency and accuracy are the most critical constraints in this system."

---

# 3. Where the Fraud System Sits

```
Merchant

↓

Payment Gateway

↓

Card Network (Visa / Mastercard)

↓

Issuing Bank

↓

Fraud Detection System

↓

Approve / Decline / Review
```

**Interview Line**

> "The fraud system sits within the issuing bank's authorization flow and must return a decision synchronously before transaction approval."

---
# 4. High-Level Architecture

The fraud decision is part of the payment authorization flow and is highly latency-sensitive. To minimize response time, the decision path remains completely synchronous. Kafka is used **after** the fraud decision for asynchronous processing such as persistence, analytics, notifications, feature engineering, and model training.

```
                         Payment Network

                                │

                                ▼

                          API Gateway

                                │

                                ▼

                    Fraud Decision Service

     ┌──────────────────────────┼──────────────────────────┐

     ▼                          ▼                          ▼

Online Feature Store      Rules Engine              ML Inference
     (Redis)             (Business Rules)          (Fraud Model)

     └──────────────────────────┼──────────────────────────┘

                                ▼

                 Approve / Decline / Review

                                │

              Return Response to Payment Network

                                │

                                ▼

                     Publish Event to Kafka

                                │

                                ▼

=============================================================
                  Asynchronous Processing Pipeline
=============================================================

                                │

        ┌──────────────┬──────────────┬──────────────┐

        ▼              ▼              ▼

 Persistence      Analytics      Notifications

        │              │              │

        └──────────────┴──────────────┘

                       ▼

            Feature Engineering Pipeline

                       ▼

            Offline Feature Store

         (S3 / Data Lake / Snowflake)

                       ▼

               Model Training Pipeline

                       ▼

            Updated ML Models & Features

                       ▼

          Deploy Model + Refresh Redis
```

---

## Why This Architecture?

The payment network is waiting for a fraud decision before completing the transaction authorization. Therefore, the synchronous path should only perform the work required to make that decision.

The Fraud Decision Service retrieves precomputed features from the online feature store, evaluates business rules and the ML model, and immediately returns **Approve**, **Decline**, or **Review**.

After the response is returned, the transaction is published to Kafka for downstream asynchronous processing. This prevents storage, analytics, notifications, and model training from impacting customer-facing latency.

---

**Interview Line**

> "I separate the synchronous authorization path from asynchronous processing. The fraud decision is made immediately using Redis, business rules, and the ML model, while Kafka handles persistence, analytics, notifications, feature engineering, and model training after the response has been returned."

# 5. Request Flow

Transactions arrive synchronously from the payment network through a REST or gRPC API.

The request flows through the API Gateway to the Fraud Decision Service, where the transaction is normalized, enriched with precomputed features, evaluated by the rules engine and ML model, and a decision is returned. After the response is sent back to the payment network, the transaction is published to Kafka for asynchronous processing.

---

## API Gateway Responsibilities

The API Gateway handles cross-cutting concerns before forwarding the request to the Fraud Decision Service.

Responsibilities

- Authentication
- Authorization
- Rate limiting
- Request validation
- Request routing
- Logging and metrics

---

## Fraud Decision Service Responsibilities

The Fraud Decision Service orchestrates the real-time fraud decision.

Responsibilities

- Normalize incoming transaction
- Validate required fields
- Generate internal metadata
- Fetch features from the Online Feature Store (Redis)
- Execute business rules
- Execute ML inference
- Combine rule and ML results
- Return Approve / Decline / Review
- Publish transaction event to Kafka after responding

No heavy processing such as analytics, reporting, or model training occurs on this synchronous path.

---

## Why Normalize?

Different payment processors and card networks send transactions using different payload formats.

Normalization converts these external payloads into a canonical internal format understood by the fraud platform.

Example

External

```json
{
  "amt":"125",
  "merchant_name":"STARBUCKS #123"
}
```

Internal

```json
{
  "transactionId":"txn123",
  "transactionAmount":125,
  "merchant":"Starbucks",
  "currency":"USD",
  "timestamp":"2026-07-12T10:15:30Z"
}
```

Normalization typically includes

- Standardizing field names
- Data type conversion
- Currency normalization
- Timestamp normalization
- Merchant normalization
- Adding internal metadata

---

**Interview Line**

> "The Fraud Decision Service keeps the authorization path lightweight by performing only the work required to generate a fraud decision. Heavy downstream processing is delegated to Kafka after the response has been returned."

# 6. Feature Store

Instead of computing fraud features during every authorization request, we precompute them and store them in dedicated feature stores. This significantly reduces latency and enables the Fraud Decision Service to make decisions within the required SLA.

We maintain two types of feature stores:

- **Online Feature Store** for real-time inference
- **Offline Feature Store** for historical analysis and model training

---

## Online Feature Store

The Online Feature Store is typically implemented using **Redis** because it provides extremely low-latency lookups.

Purpose

- Store the latest behavioral features
- Serve features required during real-time fraud detection
- Support sub-millisecond to low-millisecond access

Key

```
cardId

or

userId

or

accountId
```

Example Features

- Last transaction amount
- Transaction velocity (transactions/minute)
- Last transaction location
- Device ID
- Merchant frequency
- Average spending
- Failed login attempts
- Recent declined transactions

Typical Lookup Latency

```
1–5 ms
```

The Fraud Decision Service retrieves these features before executing the Rules Engine and ML model.

---

**Interview Line**

> "The Online Feature Store provides low-latency access to recent customer behavior, allowing us to make fraud decisions without performing expensive real-time computations."

---

## Offline Feature Store

The Offline Feature Store maintains historical transaction data used for analytics and machine learning.

Typical Technologies

- S3
- Data Lake
- Snowflake
- BigQuery

Stores

- Historical transactions
- Aggregated customer features
- Fraud labels
- Model training datasets

Used For

- Feature engineering
- Model training
- Historical analysis
- Fraud investigations

Typical Flow

```
Kafka

↓

Feature Engineering Pipeline

↓

Offline Feature Store

↓

Model Training

↓

Generate Updated Features

↓

Refresh Online Feature Store (Redis)
```

The offline pipeline continuously computes new features and retrains fraud models using historical transaction data. Once validated, the updated features and models are deployed to production.

---

**Interview Line**

> "We separate online and offline feature stores because real-time inference requires extremely low latency, while feature engineering and model training are compute-intensive batch workloads."

# 7. Decision Engine

The Decision Engine is the core component of the fraud detection platform. It evaluates each transaction using a combination of deterministic business rules and machine learning models to determine whether the transaction should be approved, declined, or sent for manual review.

Using both approaches provides the best balance between explainability, speed, and fraud detection accuracy.

---

## Rules Engine

The Rules Engine performs deterministic checks based on predefined business policies.

Examples

```
Transaction Amount > $10,000

New Country

Blacklisted Merchant

Velocity > 20 Transactions / Minute

Device Not Seen Before

Multiple Failed Login Attempts
```

Advantages

- Explainable
- Fast
- Easy to modify
- Deterministic
- Ideal for regulatory and compliance requirements

Business users can often update these rules without retraining machine learning models.

---

## Machine Learning Model

The Machine Learning model evaluates behavioral patterns that are difficult to capture using static rules.

Input Features

- Transaction amount
- Transaction velocity
- Merchant category
- Device information
- Geographic location
- Historical spending behavior
- Customer risk profile
- Previous fraud history

Output

```
Fraud Score

0.92
```

The fraud score represents the probability that the transaction is fraudulent.

Higher scores indicate higher fraud risk.

---

## Decision Layer

The Decision Layer combines the outputs from the Rules Engine and the ML Model to produce the final fraud decision.

Possible outcomes

```
APPROVE

DECLINE

REVIEW
```

Example

```
Rules

High Amount

+

New Country

+

ML Score = 0.93

↓

DECLINE
```

Another Example

```
Rules

No Violations

+

ML Score = 0.35

↓

APPROVE
```

Borderline Example

```
Rules

Velocity Threshold Exceeded

+

ML Score = 0.68

↓

REVIEW
```

The exact decision thresholds are configurable and may vary depending on customer risk profiles, merchant categories, or business policies.

---

## Why Combine Rules and ML?

Rules provide deterministic and explainable decisions that satisfy compliance and regulatory requirements.

Machine Learning detects complex fraud patterns that are difficult to express as static rules.

Together, they improve fraud detection accuracy while minimizing false positives.

---

**Interview Line**

> "The Rules Engine provides fast, deterministic, and explainable decisions, while the ML model identifies complex behavioral patterns. The Decision Layer combines both to generate the final approve, decline, or review outcome."


# 8. Real-Time Decision Flow (Critical)

The fraud detection system participates in the payment authorization flow and must return a decision within approximately **100–200 ms**. Therefore, the synchronous path is optimized to perform only the work required to generate the fraud decision.

```
Receive Transaction

↓

Normalize Request

↓

Fetch Features from Redis

↓

Execute Rules Engine

↓

Execute ML Model

↓

Combine Results

↓

Approve / Decline / Review

↓

Return Response

↓

Publish Transaction to Kafka
```

---

## Step 1 – Receive Transaction

The payment network sends the authorization request to the Fraud Decision Service through the API Gateway.

The request typically contains

- Transaction ID
- Card ID
- Merchant
- Amount
- Currency
- Timestamp
- Device Information
- Location

---

## Step 2 – Normalize Request

The Fraud Decision Service converts the external request into a canonical internal format.

This includes

- Standardizing field names
- Currency normalization
- Timestamp normalization
- Merchant normalization
- Data validation
- Adding internal metadata

---

## Step 3 – Fetch Features

The Fraud Decision Service retrieves precomputed customer features from the Online Feature Store (Redis).

Examples

- Last transaction amount
- Transaction velocity
- Average spending
- Last transaction location
- Device history
- Merchant frequency

Since these features are already computed, Redis typically returns them within **1–5 ms**.

---

## Step 4 – Execute Rules Engine

The Rules Engine evaluates deterministic business rules.

Examples

- High transaction amount
- Blacklisted merchant
- Velocity exceeded
- New country
- Device not seen before

Rule evaluation is lightweight and completes in a few milliseconds.

---

## Step 5 – Execute ML Model

The Machine Learning model evaluates the transaction using the retrieved features.

The model generates a fraud probability score.

Example

```
Fraud Score = 0.92
```

---

## Step 6 – Combine Results

The Decision Layer combines

- Rule evaluation results
- ML fraud score

to generate the final recommendation.

Possible outcomes

```
APPROVE

DECLINE

REVIEW
```

Business-defined thresholds determine how the final decision is made.

---

## Step 7 – Return Response

Once the fraud decision is generated, the Fraud Decision Service immediately returns the response to the payment network.

At this point, the customer-facing authorization flow is complete.

---

## Step 8 – Publish Event to Kafka

After the response has been returned, the Fraud Decision Service publishes the transaction to Kafka.

This event is consumed asynchronously for

- Transaction persistence
- Audit logging
- Analytics
- Notifications
- Feature engineering
- Model training
- Replay

Since these workloads are asynchronous, they never impact payment authorization latency.

---

## Why Keep Kafka After the Decision?

The payment network is waiting for a fraud decision before completing the authorization.

Adding Kafka before the decision introduces additional publish, queueing, and consumer latency.

Although Kafka itself is very fast, queueing delays under load can increase end-to-end response time.

By publishing to Kafka **after** returning the fraud decision, the authorization path remains fast and predictable while still benefiting from an event-driven architecture.

---

**Interview Line**

> "I keep the synchronous path as short as possible by limiting it to feature retrieval, rule evaluation, ML inference, and decision generation. After returning the response, I publish the transaction to Kafka so persistence, analytics, and model training execute asynchronously without affecting authorization latency."

# 9. Asynchronous Processing

Once the fraud decision has been returned to the payment network, the Fraud Decision Service publishes the transaction event to Kafka.

Moving these workloads out of the synchronous authorization path ensures that heavy processing does not impact customer-facing latency.

```
                Publish Event

                      │

                      ▼

                   Kafka

                      │

      ┌───────────────┼────────────────┬────────────────┐

      ▼               ▼                ▼

 Persistence      Analytics      Notifications

      │               │                │

      └───────────────┼────────────────┘

                      ▼

         Feature Engineering Pipeline

                      ▼

         Offline Feature Store

                      ▼

           Model Training Pipeline

                      ▼

        Deploy Updated Model & Features

                      ▼

        Refresh Online Feature Store
```

---

## Persistence

A Persistence Consumer stores the transaction and fraud decision in the transactional database.

Stored Data

- Transaction details
- Fraud score
- Final decision
- Audit information

This enables

- Transaction history
- Customer support
- Regulatory compliance
- Investigations

---

## Analytics

Analytics consumers process transaction events to generate

- Fraud trends
- Merchant risk analysis
- Customer behavior analysis
- Operational dashboards
- Business reports

Since analytics is asynchronous, large analytical workloads never impact payment authorization.

---

## Notifications

Notification consumers generate alerts for

- Suspicious transactions
- Customer notifications
- Fraud operations teams
- Case management systems

Examples

- SMS
- Email
- Push notifications
- Internal alerting systems

---

## Feature Engineering

Historical transactions are processed to generate new fraud features such as

- Spending patterns
- Transaction velocity
- Merchant frequency
- Device behavior
- Geographic patterns

These features are written to the Offline Feature Store.

---

## Model Training

The Machine Learning pipeline periodically trains new fraud detection models using

- Historical transactions
- Fraud labels
- Engineered features

Once validated, the updated models are deployed and the Online Feature Store is refreshed with newly computed features.

---

## Why Kafka?

Kafka provides several advantages for asynchronous processing.

- Decouples fraud detection from downstream systems
- Enables independent scaling of each consumer
- Supports replay for recovery and debugging
- Provides durable event storage
- Handles temporary downstream failures through buffering
- Allows new consumers to be added without changing the Fraud Decision Service

---

**Interview Line**

> "Kafka allows the Fraud Decision Service to remain focused on low-latency decision making while downstream consumers independently handle persistence, analytics, notifications, feature engineering, and model training. This keeps the authorization path fast while enabling a scalable event-driven architecture."

# 10. Storage

Different storage technologies are used because they serve different purposes and have different performance requirements.

---

## Online Feature Store

Typically

```
Redis
```

Purpose

- Store precomputed fraud features
- Provide low-latency lookups during authorization
- Support real-time ML inference

Example Features

- Transaction velocity
- Average spending
- Last transaction location
- Device history
- Merchant frequency

Typical latency

```
1–5 ms
```

Redis is optimized for fast reads and is **not** the system of record.

---

## OLTP Database

Typically

```
PostgreSQL

MySQL

Aurora
```

Purpose

Store the current transactional state of the system.

Examples

- Transaction details
- Fraud score
- Final fraud decision
- Investigation status
- Case management information

This database supports

- Customer support
- Operational dashboards
- Transaction lookups
- Regulatory reporting

The OLTP database is optimized for transactional reads and writes.

---

## Offline Feature Store

Typically

```
S3

Data Lake

Snowflake

BigQuery
```

Purpose

Store historical transaction data and engineered features used for analytics and machine learning.

Stores

- Historical transactions
- Aggregated customer features
- Fraud labels
- Training datasets

Used for

- Feature engineering
- Model training
- Historical analysis

---

## Kafka

Kafka acts as the event backbone of the system.

Purpose

- Durable event storage
- Decouple producers and consumers
- Replay historical events
- Independent consumer scaling
- Buffer traffic spikes

Kafka is **not** used as the primary database.

Instead, it provides reliable event propagation between services.

---

## Storage Summary

| Component | Technology | Purpose |
|-----------|------------|---------|
| Online Feature Store | Redis | Low-latency feature lookup |
| OLTP Database | PostgreSQL / Aurora | Current transaction state and fraud decisions |
| Offline Feature Store | S3 / Snowflake / BigQuery | Historical data and model training |
| Kafka | Event Streaming Platform | Event propagation, replay, and asynchronous processing |

---

**Interview Line**

> "Each storage technology is optimized for a different workload. Redis provides low-latency feature access for real-time inference, the OLTP database stores the current transactional state, the Offline Feature Store supports analytics and machine learning, and Kafka serves as the durable event backbone that decouples producers from downstream consumers."

# 11. Correctness & Consistency

A fraud detection system must be both highly available and highly accurate. Since distributed systems are inherently unreliable, the platform must handle retries, duplicate requests, out-of-order events, and temporary failures without affecting correctness.

---

## API Idempotency

A client or payment network may retry a request if it does not receive a response due to network failures or timeouts.

To prevent duplicate transactions from entering the system, the Fraud Decision Service performs an API idempotency check before processing the request.

Approach

- The client sends a unique `transactionId` (or `eventId`) with every request.
- The Event Ingestion/Fraud Decision Service checks Redis for the identifier.
- If the request is new, it is processed normally and the identifier is stored with a short TTL.
- If the same request is retried, the service returns the previously cached response without processing or publishing the event again.

This ensures duplicate API requests never create duplicate transactions.

---

## Consumer Idempotency

Kafka provides **at-least-once delivery**, which means a consumer may receive the same event more than once.

To prevent duplicate processing, each event contains a unique `eventId`.

During persistence, the system inserts the event into the immutable **events** table using a unique constraint on `eventId`.

If the event already exists, the transaction is rolled back and the duplicate event is ignored.

This guarantees that each business event is processed only once, even if Kafka redelivers it.

---

## Ordering

Some fraud detection rules depend on transaction order.

Examples

- Transaction velocity
- Geographic anomalies
- Spending patterns

Kafka topics are partitioned using

```
cardId

or

accountId
```

This ensures all events for the same card or account are processed in order by a single consumer within that partition.

---

## Delivery Guarantee

Kafka provides

```
At-Least-Once Delivery
```

Instead of relying on exactly-once delivery guarantees, the system combines

- At-least-once delivery
- Idempotent consumers

to achieve exactly-once business outcomes.

---

## Replay

Kafka retains events for a configurable retention period.

Replay enables

- Recovering from consumer failures
- Rebuilding downstream systems
- Reprocessing historical transactions after bug fixes
- Onboarding new consumers

Replay is one of the key advantages of an event-driven architecture.

---

## Reconciliation

Despite retries and replay, external systems can occasionally miss transactions.

Scheduled reconciliation jobs periodically compare transaction records between the payment network and the fraud platform to detect missing or inconsistent data.

Any missing transactions are safely reprocessed using the platform's idempotency mechanisms.

---

**Interview Line**

> "Rather than relying on exactly-once delivery, I design the platform using at-least-once messaging combined with idempotent processing. Ordering is preserved by partitioning Kafka on the card or account ID, while replay and reconciliation ensure the system can recover safely from failures without introducing duplicate business effects."

# 12. Key Challenges

Building a real-time fraud detection platform requires balancing latency, accuracy, scalability, and operational reliability.

---

## Latency

The fraud decision is part of the payment authorization flow and must typically complete within **100–200 ms**.

Solution

- Online Feature Store (Redis)
- Precomputed features
- Lightweight business rules
- Optimized ML inference
- Asynchronous processing through Kafka

The synchronous path performs only the work required to generate the fraud decision.

---

## False Positives

Incorrectly declining legitimate transactions impacts customer experience and revenue.

To reduce false positives, the platform introduces a third outcome:

```
REVIEW
```

Borderline transactions are routed to manual review instead of being automatically declined.

The final decision combines

- Business rules
- Machine learning predictions

to improve overall accuracy.

---

## Precision vs Recall

### Precision

How many flagged transactions are actually fraudulent?

Higher precision

↓

Lower false positives

↓

May miss some fraudulent transactions

---

### Recall

How many fraudulent transactions are successfully detected?

Higher recall

↓

Catch more fraud

↓

May increase false positives

---

Trade-off

```
Higher Precision

↓

Lower Recall

----------------

Higher Recall

↓

Lower Precision
```

The optimal balance depends on business priorities and risk tolerance.

---

## Model Drift

Fraud patterns continuously evolve over time.

A model trained on historical data may gradually become less accurate as customer behavior and attack patterns change.

Solution

- Continuous model retraining
- Feature engineering pipeline
- Feedback from confirmed fraud cases
- Periodic model evaluation
- Deployment of updated models

---

## Scalability

Transaction volume can increase significantly during peak shopping periods or seasonal events.

Solution

- Stateless services
- Horizontal scaling
- Kafka partitioning
- Consumer groups
- Redis Cluster
- Database partitioning and read replicas

---

## Availability

The fraud platform is part of the payment authorization path, so downtime directly impacts transaction processing.

Solution

- Multiple service instances
- Load balancing
- Kafka replication
- Database replication
- Automatic failover
- Health checks and monitoring

---

**Interview Line**

> "The biggest challenge is balancing low latency with high fraud detection accuracy. I keep the synchronous path lightweight, leverage Redis for fast feature retrieval, use Kafka for asynchronous processing, and continuously retrain ML models to adapt to evolving fraud patterns."

# 13. Scaling

The platform is designed to scale horizontally at every layer.

## Kafka

- Increase partitions
- Scale consumer groups independently

---

## Redis

Use

```
Redis Cluster
```

to support low-latency feature lookups.

---

## Database

- Read replicas
- Partitioning / Sharding (when needed)

---

## Services

All services remain stateless and can be scaled independently behind a load balancer.

---

**Interview Line**

> "Every layer scales independently, allowing the platform to handle increasing transaction volumes without impacting authorization latency."

---

# 14. System Evolution

## Phase 1

- Rule-based fraud detection

---

## Phase 2

- Kafka-based asynchronous processing
- Distributed consumers

---

## Phase 3

- Machine Learning
- Online & Offline Feature Stores
- Continuous model retraining

---

**Interview Line**

> "I'd start with deterministic rules, then evolve to an event-driven architecture, and finally introduce machine learning as sufficient historical data becomes available."

---

# 15. Final Interview Summary

> "I'd design a low-latency fraud detection platform where transactions are processed synchronously through a Fraud Decision Service. The service retrieves precomputed features from Redis, evaluates business rules and ML models, and returns an approve, decline, or review decision within the required latency. After responding, the transaction is published to Kafka for asynchronous processing such as persistence, analytics, notifications, feature engineering, and model training. The platform ensures correctness through idempotency, ordered processing, replay, and reconciliation while scaling horizontally at every layer."

# High Throughput Event Processing Platform

# Part 1 – Introduction, Requirements & Capacity Estimation

---

# Problem Statement

Design a distributed platform capable of processing **50 million events per day** (or higher depending on business growth).

The system should reliably ingest events from multiple producers, process them asynchronously, persist the results, and make processed data available to downstream services.

The platform should support

- High throughput
- Low latency
- Fault tolerance
- Horizontal scalability
- Replay
- Monitoring
- Disaster recovery

The solution should be generic so it can power systems such as

- Post Trade Processing
- Payment Processing
- Order Management
- Notification Platforms
- IoT Platforms
- Fraud Detection
- Audit Pipelines

---

# Interview Opening

A good opening immediately gives structure.

> "I'll assume we're designing a generic high-throughput event processing platform. Events are produced by upstream systems, ingested through an Event Ingestion API, buffered in Kafka, processed asynchronously through independent processing stages, persisted into storage, and exposed to downstream consumers. Since we're processing millions of events, scalability, fault tolerance, and correctness are primary design goals."

Notice

I intentionally say

```
Event Producer
```

instead of

```
Client
```

because producers could be

- Web applications
- Mobile apps
- Internal microservices
- Trading systems
- Payment processors
- IoT devices

The architecture remains the same.

---

# Functional Requirements

The platform should

- Receive events
- Validate events
- Persist events
- Execute business processing
- Publish processed events
- Retry failed processing
- Replay historical events
- Support multiple downstream consumers

---

# Non-Functional Requirements

The platform should provide

## High Throughput

Target

```
50 Million Events / Day
```

with the ability to scale significantly higher.

---

## Low Latency

The ingestion path should acknowledge requests within

```
<100 ms
```

Heavy processing should be asynchronous.

---

## Horizontal Scalability

Every layer should scale independently.

Examples

- API
- Kafka
- Processing
- Database

---

## Reliability

The platform should never lose events.

Failures should be handled through

- Retry
- DLQ
- Replay

---

## Correctness

Duplicate events should never create duplicate side effects.

Consumers must be idempotent.

---

## Availability

Target

```
99.99%
```

No single point of failure.

---

## Observability

Every event should be traceable.

Support

- Metrics
- Logs
- Distributed tracing
- Alerts

---

# Assumptions

We assume

- Event producers communicate through an Event Ingestion API.
- Heavy processing is asynchronous.
- Kafka is the event backbone.
- Services are stateless.
- Storage is strongly consistent.
- Multiple downstream systems consume processed events.

---

# Capacity Estimation

Assume

```
50 Million Events / Day
```

Average throughput

```
50,000,000

/

86,400

≈580 Events/sec
```

Peak traffic

Assume

```
10x
```

Peak throughput

```
≈6,000 Events/sec
```

Always design for peak traffic rather than average traffic.

---

# Event Size

Assume

```
2 KB/Event
```

Daily Storage

```
50M × 2 KB

≈100 GB/day
```

Yearly

```
≈36 TB/year
```

excluding replication and indexes.

---

# Read vs Write Ratio

Depends on domain.

Typical

```
Writes

↓

Processing

↓

Many Readers
```

Approximate

```
Reads

:

Writes

=

5 : 1
```

Some systems may have much higher read traffic.

---

# High-Level Design Principles

Every architectural decision follows these principles.

---

## Event-Driven

Services communicate using events rather than synchronous APIs.

Advantages

- Loose coupling
- Replay
- Independent deployment

---

## Stateless

Application servers store no local state.

Scaling simply means adding more instances.

---

## Idempotent

Duplicate events should produce the same final result.

---

## Horizontally Scalable

Every component scales independently.

---

## Fault Tolerant

Failure of one service should not stop the platform.

---

## Replayable

Historical events should be reprocessed without asking producers to resend them.

---

# High-Level Layers

The platform consists of four logical layers.

```
Event Ingestion

↓

Streaming

↓

Processing

↓

Serving
```

---

## Event Ingestion Layer

Receives events from producers.

Responsible for

- Authentication
- Validation
- Rate limiting
- Idempotency
- Publishing to Kafka

---

## Streaming Layer

Kafka

Provides

- Buffering
- Replay
- Ordering
- Scalability

---

## Processing Layer

Consumes events.

Performs

- Validation
- Business Logic
- Persistence

Publishes processed events.

---

## Serving Layer

Provides

- Query APIs
- Analytics
- Reporting
- Dashboards

---

# Event Producer

Instead of saying

```
Client
```

we use

```
Event Producer
```

because the producer may be

- Web application
- Mobile application
- Internal microservice
- Trading platform
- Payment processor
- IoT gateway

This keeps the design generic.

---

# Interview Talking Points

> I intentionally model the upstream component as an Event Producer instead of a Client because the same architecture applies whether events originate from an external application or an internal enterprise system.

> I separate the platform into four logical layers: Event Ingestion, Streaming, Processing, and Serving. This separation allows every layer to scale and evolve independently.

> Heavy business processing is asynchronous, allowing the ingestion path to remain fast while ensuring the platform can absorb traffic spikes.

> Since this is a distributed event processing platform, reliability, replay, and idempotency are more important than minimizing individual component latency.

---

# Next Chapter

Part 2 – Event Ingestion API & High-Level Architecture

We'll design

- Event API
- Load Balancer
- API Gateway
- Event Ingestion Service
- Authentication
- Idempotency
- Normalization
- Kafka Producer
- High-Level Architecture

# High Throughput Event Processing Platform

# Part 2 – Event Ingestion Layer & High-Level Architecture

---

# Overview

The Event Ingestion Layer is the entry point into the platform.

Its responsibility is **not** to process business logic.

Instead, it is responsible for **accepting events reliably**, performing lightweight validation, and publishing them to Kafka as quickly as possible.

A common mistake is putting business logic in the ingestion layer.

The ingestion layer should remain thin.

---

# High-Level Architecture

```
                    Event Producer

                         │

                         ▼

                  Load Balancer

                         │

                         ▼

                   API Gateway

                         │

                         ▼

              Event Ingestion Service

       Authentication
       Authorization
       Rate Limiting
       Validation
       Idempotency
       Normalization
       Correlation ID

                         │

                         ▼

                  Kafka Producer

                         │

                         ▼

                       Kafka
```

---

# Why use an Event Ingestion API?

Instead of allowing producers to publish directly into Kafka

```
Producer

↓

Kafka
```

we introduce an Event Ingestion Service.

Advantages

- Authentication
- Authorization
- Request validation
- Rate limiting
- Idempotency
- Schema validation
- Centralized logging
- Observability
- Kafka remains internal

This makes the platform easier to evolve without impacting producers.

---

# Who are the Event Producers?

The producer depends on the domain.

Examples

```
Web Application

Mobile App

Payment Gateway

Order Service

Trade Capture Service

OMS

IoT Gateway

Another Microservice
```

The architecture remains identical.

This is why we intentionally use

```
Event Producer
```

instead of

```
Client
```

---

# Load Balancer

The Load Balancer distributes requests across multiple ingestion instances.

```
             Event Producers

                    │

                    ▼

              Load Balancer

      ┌─────────────┼─────────────┐

      ▼             ▼             ▼

  Ingestion1   Ingestion2   Ingestion3
```

Benefits

- High availability
- Horizontal scaling
- Fault tolerance

Examples

- AWS ALB
- NGINX
- HAProxy

---

# API Gateway

The API Gateway sits in front of the ingestion service.

Responsibilities

- Authentication
- Authorization
- TLS termination
- Request validation
- Rate limiting
- Logging
- Metrics
- Routing

The API Gateway should never execute business logic.

---

# Event Ingestion Service

This is the most important component in the ingestion layer.

Responsibilities

✓ Receive events

✓ Validate schema

✓ Authenticate request

✓ Normalize payload

✓ Generate metadata

✓ Publish to Kafka

Return immediately.

---

# What should NOT happen here?

The ingestion service should **not**

- Perform business validation
- Update databases
- Execute workflows
- Call downstream services
- Execute heavy processing

All heavy processing belongs downstream.

---

# Event Flow

```
Producer

↓

POST /events

↓

API Gateway

↓

Event Ingestion Service

↓

Kafka
```

Notice

The ingestion service acknowledges the request quickly.

Heavy processing is asynchronous.

---

# Event API

Example

```
POST /v1/events
```

Example Request

```json
{
   "eventType":"ORDER_CREATED",
   "producerId":"order-service",
   "payload":{
      "orderId":"12345",
      "customerId":"100",
      "amount":250
   }
}
```

---

# Response

We should not wait for processing.

Instead

```
HTTP 202 Accepted
```

Example

```json
{
   "eventId":"evt-1001",
   "status":"ACCEPTED"
}
```

Meaning

The event has been accepted into the platform.

Processing continues asynchronously.

---

# Why HTTP 202?

Returning

```
200 OK
```

could imply

```
Processing Complete
```

which is not true.

Instead

```
202 Accepted
```

means

```
Accepted for Processing
```

---

# Authentication

Every producer should authenticate.

Possible mechanisms

- OAuth
- JWT
- API Keys
- mTLS (internal systems)

The choice depends on the environment.

---

# Authorization

Even authenticated producers may only publish certain event types.

Example

```
Order Service

↓

ORDER_CREATED

✓

-------------------

Inventory Service

↓

ORDER_CREATED

✗
```

---

# Schema Validation

Validate

- Required fields
- Event type
- Payload format
- Version

Example

Required

```
eventId

eventType

producerId

timestamp

payload
```

Invalid requests return

```
HTTP 400
```

---

# Event Normalization

Different producers may send different payload formats.

Example

Producer A

```json
{
   "custId":"100",
   "amt":"250"
}
```

Producer B

```json
{
   "customer":"100",
   "amount":250.0
}
```

Normalize into

```json
{
   "customerId":"100",
   "amount":250,
   "currency":"USD"
}
```

This creates a consistent internal schema.

---

# Metadata Generation

The ingestion service enriches events with internal metadata.

Example

```json
{
   "eventId":"evt-123",
   "receivedTimestamp":"...",
   "correlationId":"abc-xyz",
   "producer":"OMS"
}
```

Benefits

- Tracing
- Debugging
- Monitoring

---

# Idempotency

Network retries happen.

Suppose

```
Producer

↓

Timeout

↓

Retry
```

The same event may arrive twice.

Solution

Use

```
Idempotency-Key
```

or

```
eventId
```

Example

```
eventId

↓

evt-100
```

If already processed,

return previous acknowledgement.

No duplicate Kafka publish.

---

# Rate Limiting

Protect the platform.

Example

```
1000 Requests

Per Second

Per Producer
```

Exceed

↓

Return

```
429 Too Many Requests
```

This prevents one producer from overwhelming the system.

---

# Correlation ID

Every request receives

```
Correlation ID
```

Example

```
Request

↓

API Gateway

↓

Kafka

↓

Consumers

↓

Database
```

The same ID follows the event through every service.

Critical for debugging.

---

# Publishing to Kafka

After lightweight processing

```
Authentication

↓

Validation

↓

Normalization

↓

Metadata

↓

Kafka Producer
```

The ingestion service becomes the Kafka producer.

---

# Producer Configuration

Typical Kafka producer configuration

```
acks = all

enable.idempotence = true

compression = lz4

batch.size

linger.ms
```

These settings improve

- durability
- throughput
- batching

---

# Why keep ingestion lightweight?

Suppose

processing takes

```
500 ms
```

If done synchronously,

throughput drops dramatically.

Instead

```
Accept Event

↓

Publish Kafka

↓

Return

<50 ms
```

Heavy work continues later.

---

# Failure Handling

Suppose Kafka is unavailable.

Possible options

1.

Retry

↓

Kafka available

↓

Publish

---

2.

Persist temporarily

↓

Retry later

---

3.

Return

```
503 Service Unavailable
```

The approach depends on business requirements.

---

# Scaling

The ingestion service is stateless.

Scale

```
2 Pods

↓

10 Pods

↓

100 Pods
```

behind a Load Balancer.

No code changes required.

---

# Design Principles

The ingestion layer follows

## Thin Layer

Only lightweight work.

---

## Stateless

No local state.

---

## Fast

Return quickly.

---

## Durable

Events enter Kafka before acknowledgement.

---

## Secure

Authentication

Authorization

Rate limiting

---

# Interview Talking Points

> I introduce an Event Ingestion Service instead of exposing Kafka directly. This allows us to centralize authentication, validation, rate limiting, schema management, idempotency, and observability while keeping Kafka as an internal implementation detail.

> The ingestion path is intentionally lightweight. Its primary responsibility is to reliably accept events and publish them to Kafka with minimal latency. Heavy business processing is deferred to downstream consumers.

> Every event is normalized into a common internal schema and enriched with metadata such as an Event ID and Correlation ID before entering Kafka. This ensures downstream services operate on a consistent event format regardless of the producer.

> Since the ingestion service is stateless, it can scale horizontally behind a load balancer simply by adding more instances.

"Yes. I would expect the Event Producer to generate a globally unique eventId and include it with every request. The Event Ingestion Service uses that eventId as the idempotency key to detect retries and prevent duplicate events from entering Kafka. The ingestion service may additionally generate a correlationId for distributed tracing, but it should not generate the business eventId because only the producer can consistently identify the same business event across retries."

---

# Next Chapter

# Part 3 – Kafka Architecture & Streaming Layer

Topics

- Why Kafka
- Topic Design
- Partition Strategy
- Consumer Groups
- Ordering
- Replication
- Exactly Once
- Replay
- DLQ
- Retry
- Schema Registry
- Scaling Kafka
- Producer & Consumer Design

# High Throughput Event Processing Platform

# Part 3 – Kafka Architecture & Streaming Layer

> Kafka is the backbone of the event processing platform. It decouples event ingestion from downstream processing, provides durable event storage, enables replay, preserves ordering where required, and allows every processing stage to scale independently.

---

# Why Kafka?

Imagine the Event Ingestion Service directly calling downstream services.

```
Event Producer

↓

Event Ingestion

↓

Validation

↓

Business Logic

↓

Database

↓

Analytics

↓

Notifications
```

Problems

- Tight coupling
- Cascading failures
- No replay
- Difficult scaling
- High latency

Instead, introduce Kafka.

```
Event Producer

↓

Event Ingestion

↓

Kafka

↓

Consumers
```

Kafka acts as a durable buffer between producers and consumers.

---

# Benefits of Kafka

Kafka provides

✓ Durable event storage

✓ Decoupling

✓ Horizontal scalability

✓ Replay

✓ Fault isolation

✓ Consumer independence

✓ Ordering within partitions

---

# High-Level Architecture

```
              Event Producer

                     │

                     ▼

          Event Ingestion Service

                     │

                     ▼

                Kafka Producer

                     │

                     ▼

        ┌────────────────────────┐
        │                        │
        │        Kafka           │
        │                        │
        └────────────────────────┘

          │         │         │

          ▼         ▼         ▼

   Validation   Processing   Analytics

          │

          ▼

      Persistence
```

Notice

The producer knows only Kafka.

Consumers know only Kafka.

Neither side knows about each other.

This loose coupling is one of Kafka's biggest strengths.

---

# Topic Design

Avoid placing all events into one topic.

Bad

```
events
```

Better

```
orders

payments

notifications

shipments

audit
```

or

```
events.raw

events.validated

events.processed

events.failed
```

Topic design should align with business domains or processing stages.

---

# Example Topic Flow

```
events.raw

↓

Validation Consumer

↓

events.validated

↓

Business Consumer

↓

events.processed
```

This allows replay of individual stages without replaying the entire pipeline.

---

# Message Structure

Example

```json
{
  "eventId":"evt-1001",
  "eventType":"ORDER_CREATED",
  "producerId":"order-service",
  "timestamp":"2026-07-12T10:15:30Z",
  "correlationId":"corr-789",
  "payload":{
      "orderId":"12345",
      "customerId":"100",
      "amount":250
  }
}
```

Metadata should be separated from business payload.

---

# Partition Strategy

Choosing the correct partition key is one of the most important Kafka decisions.

Kafka guarantees ordering **within a partition**, not across partitions.

---

## Poor Partition Key

```
Random UUID
```

Same customer's events may land in different partitions.

Ordering is lost.

---

## Better Partition Keys

Choose based on the business requirement.

Examples

```
customerId

accountId

orderId

deviceId

tradeId
```

Example

```
hash(customerId)

↓

Partition 5
```

All events for that customer go to the same partition.

---

# Ordering

Kafka guarantees

```
Ordering

Within

One Partition
```

Example

```
Customer123

↓

Partition 3

↓

Event1

↓

Event2

↓

Event3
```

Events are processed in order.

Across different partitions, there is no ordering guarantee.

---

# How Many Partitions?

Suppose

```
Peak

6,000 Events/sec
```

One consumer can process

```
500 Events/sec
```

Required throughput

```
6000 / 500

≈12 Consumers
```

Create at least

```
12 Partitions
```

Often we provision additional partitions for future growth.

Example

```
24 Partitions
```

---

# Consumer Groups

A Consumer Group allows multiple consumers to process events in parallel.

```
Topic

24 Partitions

↓

Consumer Group

Validation

↓

Validation-1

Validation-2

Validation-3

Validation-4
```

Kafka automatically distributes partitions among consumers.

---

# Scaling Consumers

Example

```
24 Partitions

↓

6 Consumers

↓

4 Partitions Each
```

Increase consumers

```
24 Consumers

↓

1 Partition Each
```

Horizontal scaling becomes simple.

---

# Multiple Consumer Groups

Different services consume the same topic independently.

```
events.processed

        │

 ┌──────┼────────┬─────────┐

 ▼      ▼        ▼         ▼

Billing Reporting Search Analytics
```

Each consumer group maintains its own offset.

One service does not affect another.

---

# Producer Configuration

Typical producer settings

```
acks=all

enable.idempotence=true

compression=lz4

linger.ms=5

batch.size=64KB
```

---

## Why acks=all?

Leader waits for replicas before acknowledging.

Improves durability.

---

## Why Idempotent Producer?

Suppose

```
Producer

↓

Timeout

↓

Retry
```

Without idempotence

```
Duplicate Kafka Messages
```

With idempotence

Kafka stores only one copy.

Important

Producer idempotence only prevents duplicate writes **between the producer and Kafka**.

It does **not** eliminate duplicate business events coming from producers.

Application-level idempotency is still required.

---

# Replication

Each partition has multiple replicas.

Example

```
Partition 3

↓

Leader

↓

Follower

↓

Follower
```

Replication Factor

```
3
```

If the leader fails,

Kafka elects a follower as the new leader.

No data loss (assuming ISR is healthy).

---

# ISR (In-Sync Replicas)

ISR represents replicas that are fully caught up with the leader.

```
Leader

Follower1

Follower2
```

If Follower2 falls behind,

it leaves the ISR.

Kafka only acknowledges writes according to the configured acknowledgment policy.

---

# Offsets

Every event receives an offset.

Example

```
Partition 2

Offset

100

101

102

103
```

Consumers commit offsets after successful processing.

---

# Consumer Failure

Suppose a consumer crashes after processing offset

```
101
```

After restart

Kafka resumes from

```
102
```

No events are lost.

---

# Replay

Replay is one of Kafka's biggest strengths.

Suppose

```
Analytics Service

↓

Bug
```

Fix deployed.

Reset consumer offset.

Replay

```
events.processed
```

No producer involvement is required.

---

# Retry Strategy

Not every failure should immediately go to a DLQ.

Transient failures

- Database unavailable
- Temporary network issue
- External API timeout

Retry

↓

Exponential Backoff

↓

Retry Topic (optional)

↓

Process Again

---

# Dead Letter Queue (DLQ)

Permanent failures should be isolated.

Examples

- Invalid schema
- Corrupt payload
- Unsupported event version

Flow

```
Consumer

↓

Retry

↓

Retry

↓

Retry

↓

events.dlq
```

Operations teams investigate later.

---

# Schema Registry

As events evolve,

schemas change.

Version 1

```json
{
   "customerId":"100"
}
```

Version 2

```json
{
   "customerId":"100",
   "customerTier":"Gold"
}
```

Schema Registry ensures

- Version compatibility
- Backward compatibility
- Forward compatibility
- Producer/Consumer validation

This prevents consumers from breaking when schemas evolve.

---

# Backpressure

Suppose producers send

```
20,000 Events/sec
```

Consumers process

```
8,000 Events/sec
```

Kafka absorbs the burst.

Consumers gradually catch up.

Consumer lag increases temporarily, but no events are lost.

This ability to absorb traffic spikes is a major advantage of Kafka.

---

# Monitoring Kafka

Key metrics

- Producer Throughput
- Consumer Throughput
- Consumer Lag
- Broker Health
- Partition Utilization
- ISR Count
- Replication Lag
- DLQ Size
- Retry Rate
- Failed Publishes

Alert when

- Consumer lag grows rapidly
- Brokers become unavailable
- ISR shrinks
- DLQ increases unexpectedly

---

# Design Trade-offs

## More Partitions

Advantages

- Higher parallelism
- Better scalability

Trade-off

- More broker overhead
- More partition management

---

## Larger Batch Size

Advantages

- Better throughput
- Lower network overhead

Trade-off

- Higher latency

---

## Higher Replication Factor

Advantages

- Better durability
- Higher availability

Trade-off

- More storage
- Slower writes

---

# Interview Talking Points

> Kafka decouples event ingestion from downstream processing, allowing producers and consumers to evolve independently while providing durable storage and replay capabilities.

> Choosing the correct partition key is critical because Kafka guarantees ordering only within a partition. We typically partition by a business identifier such as `customerId`, `accountId`, or `orderId`, depending on the ordering requirements.

> Multiple consumer groups allow independent services such as billing, reporting, analytics, and notifications to process the same event stream without interfering with one another.

> Producer idempotence prevents duplicate writes caused by producer retries, but application-level idempotency is still required because duplicate business events can originate upstream.

> Replay is one of Kafka's strongest capabilities. If a downstream service fails or business logic changes, we can reset consumer offsets and reprocess historical events without asking producers to resend data.

---

# Next Chapter

# Part 4 – Event Processing Pipeline

Topics

- Validation
- Deduplication
- Ordering
- Business Processing
- Enrichment
- Persistence
- Publishing Processed Events
- Retry
- DLQ
- Idempotent Consumers
- Exactly-Once Processing

# High Throughput Event Processing Platform

# Part 4 – Event Processing Pipeline

> The Event Processing Pipeline is the heart of the platform. Once events are durably stored in Kafka, they flow through a series of independent processing stages. Each stage has a single responsibility, communicates through Kafka, and can be scaled independently.

---

# Overview

The goal of the processing pipeline is to transform raw events into processed business events.

Responsibilities include

- Validation
- Deduplication
- Ordering
- Business Processing
- Persistence
- Publishing downstream events

Rather than implementing everything in one large service, we split the pipeline into independent stages.

---

# High-Level Pipeline

```
                    Kafka

                      │

                      ▼

              Validation Service

                      │

                      ▼

            Deduplication Service

                      │

                      ▼

              Business Processing

                      │

                      ▼

             Persistence Service

                      │

                      ▼

             events.processed

        ┌─────────┼─────────┬───────────┐

        ▼         ▼         ▼           ▼

    Reporting  Analytics  Search  Notifications
```

Each stage is independently deployable and scalable.

---

# Why Multiple Processing Stages?

Instead of

```
Consumer

↓

Everything
```

we use

```
Validation

↓

Deduplication

↓

Business Logic

↓

Persistence
```

Advantages

- Easier maintenance
- Better scalability
- Fault isolation
- Independent deployment
- Replay individual stages

---

# Stage 1 – Validation Service

Purpose

Ensure the event is structurally valid before processing.

---

## Responsibilities

Validate

- Required fields
- Event version
- Schema
- Data types
- Mandatory metadata

Example

Required

```
eventId

eventType

timestamp

payload
```

---

## Invalid Event

Example

```json
{
   "eventId":"123",
   "amount":"ABC"
}
```

Invalid amount.

Do not continue processing.

Instead

```
events.dlq
```

---

# Why Validate Early?

Without validation,

invalid events reach downstream systems.

This creates

- inconsistent databases
- failed consumers
- difficult debugging

Validation should always be the first processing stage.

---

# Stage 2 – Deduplication

Duplicate events are inevitable.

Causes

- Producer retries
- Network failures
- Consumer retries
- Manual replay

The platform must safely process duplicates.

---

# Deduplication Key

Choose a business identifier.

Examples

```
eventId

transactionId

orderId

tradeId
```

---

# Example

Incoming

```
evt100

evt100

evt100
```

After deduplication

```
evt100
```

Only one business event is processed.

---

# How to Implement?

Several approaches exist.

## Option 1

Database Constraint

```sql
UNIQUE(eventId)
```

Simple

Reliable

---

## Option 2

Redis Cache

Store recently processed IDs.

Advantages

Very fast

Trade-off

Cache eviction

---

## Option 3

Idempotency Table

```
ProcessedEvents

eventId

processedTime
```

Common in financial systems.

---

# Interview Line

> Consumers should always be idempotent because retries and replay are expected in distributed systems.

---

# Stage 3 – Business Processing

This stage contains

domain-specific logic.

Examples

Payment

```
Calculate Fees
```

Trading

```
Allocate Trades
```

Orders

```
Reserve Inventory
```

Fraud

```
Compute Risk Score
```

Business logic should remain isolated from infrastructure concerns.

---

# External Dependencies

Sometimes business logic calls

- Pricing Service
- Customer Service
- Fraud Service
- Exchange Rates
- ML Model

Always protect external calls.

Use

- Timeouts
- Retries
- Circuit Breakers

Never allow one slow dependency to block the pipeline.

---

# Stage 4 – Persistence

Business processing completed.

Now persist results.

Responsibilities

- Insert records
- Update existing records
- Maintain audit history

---

# Transaction

Typical flow

```
Insert Event

↓

Update Business Tables

↓

Commit
```

Only after commit

↓

Publish downstream event.

---

# Why Publish After Commit?

Incorrect

```
Publish Kafka

↓

Database Commit
```

Consumer may receive an event

before

the database transaction succeeds.

Correct

```
Database Commit

↓

Publish Kafka
```

This avoids inconsistencies.

(We'll improve this further using the Outbox Pattern in a later chapter.)

---

# Publishing Processed Events

After persistence

publish

```
events.processed
```

Consumers

- Reporting
- Analytics
- Notifications
- Search
- ML

subscribe independently.

---

# Event Choreography

Example

```
Order Created

↓

Inventory

↓

Payment

↓

Shipping

↓

Notification
```

No orchestration required.

Each service reacts to events.

---

# Retry Strategy

Not every failure is permanent.

Examples

- Temporary database outage
- External API timeout
- Network issue

Retry

↓

Exponential Backoff

↓

Retry Topic (optional)

↓

Process Again

---

# Exponential Backoff

Example

```
Retry 1

30 sec

Retry 2

60 sec

Retry 3

120 sec
```

Avoid immediate retries.

This reduces pressure on downstream systems.

---

# Dead Letter Queue

Permanent failures

↓

DLQ

Examples

- Invalid schema
- Unsupported version
- Corrupted payload

Flow

```
Processing

↓

Retry

↓

Retry

↓

Retry

↓

events.dlq
```

Operations investigate later.

---

# Idempotent Consumers

Consumers should produce

the same result

whether they process an event

once

or

multiple times.

Example

```
evt100

↓

Insert Row

↓

Replay

↓

No Duplicate Row
```

Implementation

- Unique constraints
- UPSERT
- Idempotency table

---

# Exactly-Once Processing

This is a common interview topic.

There are three delivery guarantees.

---

## At Most Once

```
No Retries

Possible Data Loss
```

---

## At Least Once

```
Retries

Possible Duplicates
```

Most Kafka systems use this model.

---

## Exactly Once

Business effect occurs only once.

Kafka supports

- Idempotent Producers
- Transactions

However,

application-level idempotency is still required.

---

# Ordering

Kafka guarantees ordering

within a partition.

Suppose

```
Customer100

↓

Partition3

↓

Event1

↓

Event2

↓

Event3
```

Business processing preserves order.

---

# Parallel Processing

Different partitions

↓

Different consumers

↓

Parallel execution

```
Partition1

↓

Consumer1

---------------

Partition2

↓

Consumer2

---------------

Partition3

↓

Consumer3
```

Excellent scalability.

---

# Failure Recovery

Suppose

Business Processing crashes.

Kafka

↓

Offset not committed

↓

Restart

↓

Reprocess Event

No data loss.

---

# Replay

Suppose

Business logic changes.

Reset consumer offset.

Replay

```
events.validated
```

No producer involvement required.

---

# Monitoring

Track

- Processing latency
- Consumer lag
- Retry count
- DLQ count
- Processing throughput
- Failed events
- Business errors

Alert when

- DLQ grows
- Retry rate spikes
- Consumer lag increases

---

# Design Principles

Every processing stage follows

## Single Responsibility

Each service performs one job.

---

## Stateless

No local state.

State belongs in Kafka and databases.

---

## Idempotent

Duplicate events produce the same business result.

---

## Replayable

Historical events can be processed again.

---

## Horizontally Scalable

Simply add more consumers.

---

# Design Trade-offs

## Small Services

Advantages

- Independent deployment
- Easier scaling
- Better ownership

Trade-off

- More network hops
- Operational complexity

---

## Retry vs DLQ

Retry

Good for

Temporary failures

DLQ

Good for

Permanent failures

---

## Synchronous Processing

Advantages

Simple

Trade-off

Poor scalability

---

## Event-Driven Processing

Advantages

Highly scalable

Fault tolerant

Replay

Trade-off

Eventual consistency

---

# Interview Talking Points

> The processing pipeline is implemented as a series of independent Kafka consumers, each responsible for a single stage such as validation, deduplication, business processing, or persistence. This separation improves scalability, replayability, and fault isolation.

> Every consumer is designed to be idempotent because retries and replay are expected in distributed systems. Techniques such as unique database constraints, UPSERT operations, or idempotency tables prevent duplicate business effects.

> Business logic is isolated from infrastructure concerns. External dependencies are protected using timeouts, retries, and circuit breakers to prevent cascading failures.

> Processed events are published only after successful database persistence, ensuring downstream consumers never observe data that hasn't been durably committed.

> Kafka's partitioning model allows parallel processing while preserving ordering for related events, providing both scalability and correctness.

---

# Next Chapter

# Part 5 – Database Design, CQRS & Storage Architecture

Topics

- Database Selection
- OLTP vs NoSQL
- CQRS
- Read Models
- Redis
- Snowflake
- Partitioning
- Sharding
- Read Replicas
- Storage Trade-offs

# High Throughput Event Processing Platform

# Part 5 – Database Design, CQRS & Storage Architecture

> The storage layer is responsible for durably persisting processed events while supporting high write throughput, low-latency reads, analytics, replay, and horizontal scalability. A single database is rarely sufficient, so we use different storage technologies for different workloads.

---

# Overview

After an event successfully passes through the processing pipeline, it must be persisted.

The storage layer has several responsibilities:

- Store processed events
- Maintain business state
- Serve read APIs
- Support analytics
- Support replay
- Support auditing

Instead of using one database for everything, we separate storage based on workload.

---

# Storage Architecture

```
                Processing Pipeline

                        │

                        ▼

               OLTP Database
             (PostgreSQL/MySQL)

                        │

        ┌───────────────┼──────────────┐

        ▼               ▼              ▼

      Redis        Read Models     Outbox

        │

        ▼

     Read APIs

                        │

                        ▼

                  Kafka Events

                        │

                        ▼

          Analytics Pipeline

                        │

                        ▼

         Snowflake / BigQuery
```

---

# Why Multiple Storage Systems?

Every database has strengths and weaknesses.

Trying to solve every problem with one database usually results in poor performance.

| Storage | Purpose |
|----------|----------|
| PostgreSQL | Source of Truth |
| Redis | Low latency reads |
| Snowflake | Analytics |
| Kafka | Event propagation |
| Object Storage | Long-term archival |

Each storage system is optimized for a different workload.

---

# Source of Truth

The OLTP database is the authoritative source of business state.

Examples

```
Orders

Trades

Payments

Users

Accounts
```

If Redis is lost,

the database can rebuild it.

If analytics is lost,

the database remains correct.

---

# Database Choice

The choice depends on the workload.

---

## PostgreSQL

Best for

- ACID transactions
- Financial systems
- Strong consistency
- Relational data

Examples

- Payments
- Orders
- Trading
- Banking

---

## Cassandra

Best for

- Massive write throughput
- Time-series events
- Event logs

Trade-off

Eventually consistent.

---

## DynamoDB

Best for

- Key-value access
- Cloud-native workloads
- Automatic scaling

---

## ClickHouse

Best for

- Analytical queries
- Event analytics
- Large aggregations

---

# Example Data Model

Suppose

```
Order Processing
```

Events Table

| Column | Description |
|---------|-------------|
| eventId | Primary Key |
| eventType | Event Type |
| producerId | Producer |
| payload | Event Payload |
| status | Processing Status |
| createdAt | Timestamp |

---

Business Table

Example

Orders

| orderId | customerId | amount | status |

Business tables represent the current state.

Events represent what happened.

---

# Why Store Events?

Even after updating business tables,

store the original event.

Benefits

- Replay
- Audit
- Debugging
- Historical analysis

This is similar to Event Sourcing,

although we're not implementing full Event Sourcing here.

---

# Read vs Write Workloads

Most systems have different read and write patterns.

Example

```
Writes

↓

Processing

↓

Reads

Dashboard

Reporting

Search
```

Optimizing both with one schema is difficult.

---

# CQRS

CQRS stands for

```
Command Query Responsibility Segregation
```

Separate

```
Write Model

and

Read Model
```

---

# Without CQRS

```
API

↓

Database

↓

Reads

Writes
```

Both compete for resources.

---

# With CQRS

```
Commands

↓

Write Database

↓

Kafka

↓

Read Model

↓

Read APIs
```

Write path

↓

Optimized for correctness.

Read path

↓

Optimized for speed.

---

# Write Path

```
Event

↓

Business Processing

↓

PostgreSQL
```

The write model owns business transactions.

---

# Read Path

```
Kafka

↓

Projection Service

↓

Redis

↓

Dashboard
```

Read models are derived from the write model.

---

# Why CQRS?

Advantages

- Independent scaling
- Different schemas
- Faster queries
- Better separation of concerns

Trade-off

Eventual consistency.

---

# Read Models

Instead of querying large transactional tables,

build optimized read models.

Example

Dashboard

```
Customer Summary

Current Balance

Last 10 Events

Recent Orders
```

Designed specifically for read APIs.

---

# Redis

Redis caches

- Dashboard
- Frequently accessed entities
- Read models
- Session data

Example

```
dashboard:customer100
```

Response

```
2–5 ms
```

instead of querying PostgreSQL.

---

# Cache Invalidation

After database commit

```
Update Database

↓

Publish Event

↓

Projection Service

↓

Update Redis
```

Never update Redis before the database commit.

---

# Read Replicas

Reads usually exceed writes.

Architecture

```
Primary

↓

Replica1

Replica2

Replica3
```

Writes

↓

Primary

Reads

↓

Replicas

This significantly improves scalability.

---

# Partitioning

Suppose

```
10 Billion Events
```

A single table becomes inefficient.

Partition by

```
Date

or

Hash(eventId)
```

Benefits

- Faster scans
- Smaller indexes
- Easier maintenance

---

# Sharding

Eventually

one database server is not enough.

Shard by

```
customerId

accountId

tenantId
```

Example

```
Shard1

Customers A–M

------------------

Shard2

Customers N–Z
```

Each shard owns a subset of data.

---

# Archival

Suppose

```
10 Years

of Events
```

Keep

```
Recent Data

↓

PostgreSQL
```

Older events

↓

Object Storage

↓

S3

↓

Data Lake

Supports

- Replay
- Compliance
- Analytics

---

# Analytics Database

Analytical queries should never run on the production database.

Instead

```
events.processed

↓

Kafka

↓

Analytics Pipeline

↓

Snowflake

BigQuery

ClickHouse
```

Used for

- Business Intelligence
- Trend Analysis
- Machine Learning
- Historical Reports

---

# Database Transactions

Example

```
Insert Event

↓

Update Business Table

↓

Commit
```

Only after successful commit

↓

Publish downstream event.

This prevents inconsistencies.

(The Outbox Pattern will improve this further.)

---

# Scaling Strategy

Start simple

```
One Database
```

↓

Read Replicas

↓

Partitioning

↓

Sharding

↓

Distributed Database

Scale incrementally.

---

# Monitoring Storage

Monitor

- Write latency
- Read latency
- Replication lag
- Cache hit ratio
- Slow queries
- Connection pool
- Storage utilization
- Lock contention

Alerts

- Replica lag
- Disk usage
- Failed writes
- High query latency

---

# Design Trade-offs

## Relational Database

Advantages

- ACID
- Constraints
- Transactions

Trade-off

Scaling writes becomes harder.

---

## NoSQL

Advantages

- Massive scalability
- Flexible schema

Trade-off

Weaker consistency.

---

## CQRS

Advantages

- Independent read/write scaling
- Faster queries
- Better performance

Trade-off

Eventual consistency
More operational complexity

---

## Redis

Advantages

- Very low latency
- Reduced database load

Trade-off

Cache invalidation
Additional infrastructure

---

# Interview Talking Points

> The OLTP database serves as the source of truth for business state, while Redis and read models are derived views optimized for query performance.

> I separate read and write workloads using CQRS. Commands update the write database, while events are used to build optimized read models asynchronously.

> Read replicas improve read scalability, partitioning improves large-table performance, and sharding is introduced only when a single database can no longer handle write throughput.

> Analytics workloads are isolated from transactional workloads by streaming processed events into an analytics database such as Snowflake or ClickHouse.

> Every storage technology serves a specific purpose—PostgreSQL for consistency, Redis for speed, Kafka for propagation, and analytical databases for large-scale reporting.

---

# Next Chapter

# Part 6 – Outbox Pattern & Exactly-Once Processing

Topics

- Dual Write Problem
- Outbox Pattern
- Outbox Publisher
- Transaction Boundaries
- Exactly-Once Semantics
- Idempotent Consumers
- Producer Idempotence
- Kafka Transactions
- Trade-offs

# High Throughput Event Processing Platform

# Part 6 – Outbox Pattern & Exactly-Once Processing

> One of the most common failure scenarios in distributed systems is the **Dual Write Problem**. A service often needs to update its database and publish an event to Kafka. If one succeeds and the other fails, the system becomes inconsistent. The Outbox Pattern solves this problem by making database updates and event publication reliable.

---

# The Dual Write Problem

Consider a simple order processing service.

```
Order Created

↓

Update Database

↓

Publish Kafka Event
```

Looks simple.

Unfortunately, failures can happen between these two operations.

---

# Scenario 1

Database succeeds

Kafka publish fails

```
Update Database

✓

↓

Publish Kafka

✗
```

Result

The order exists in the database,

but downstream services never receive the event.

Inventory

Shipping

Notifications

Analytics

remain unaware.

---

# Scenario 2

Kafka succeeds

Database fails

```
Publish Kafka

✓

↓

Database Commit

✗
```

Consumers receive an event

for data that doesn't exist.

This is even worse.

---

# Why is this difficult?

Because

```
Database

and

Kafka
```

are two different distributed systems.

A normal database transaction cannot span both.

---

# Naive Solution

Retry Kafka publishing.

```
DB

↓

Kafka

↓

Retry
```

Problems

- Duplicate events
- Lost events
- Complex error handling

Still not reliable.

---

# The Outbox Pattern

Instead of writing to

```
Database

+

Kafka
```

inside the same code path,

write to

```
Database

+

Outbox Table
```

inside one database transaction.

A separate publisher later sends events to Kafka.

---

# High-Level Architecture

```
Business Service

       │

       ▼

Database Transaction

       │

 ┌─────┴─────────────┐

 ▼                   ▼

Business Table   Outbox Table

                       │

                       ▼

               Outbox Publisher

                       │

                       ▼

                    Kafka
```

Notice

The application never writes directly to Kafka.

---

# What is the Outbox Table?

It is a normal database table.

Example

| Column | Description |
|---------|-------------|
| outboxId | Primary Key |
| eventId | Unique Event ID |
| eventType | Event Type |
| payload | Serialized Event |
| status | NEW / SENT |
| createdAt | Timestamp |

---

# Database Transaction

Suppose an order is created.

Inside one transaction

```
BEGIN

↓

Insert Order

↓

Insert Outbox Row

↓

COMMIT
```

Either both succeed

or both fail.

Atomicity is guaranteed.

---

# Example

```
BEGIN

Insert Order

Insert Outbox Event

COMMIT
```

Result

```
Orders

✓

Outbox

✓
```

No inconsistency.

---

# Outbox Publisher

A separate service continuously scans

```
Outbox Table
```

for new rows.

Flow

```
Outbox

↓

Publisher

↓

Kafka

↓

Mark SENT
```

---

# Publisher Flow

```
Read NEW Rows

↓

Publish Kafka

↓

Success?

      │

 ┌────┴────┐

 ▼         ▼

Yes        No

 │          │

 ▼          ▼

Mark SENT Retry Later
```

---

# Why is this reliable?

Suppose the publisher crashes.

```
Publish

↓

Crash
```

Row still exists in the Outbox.

Publisher restarts.

Reads NEW rows again.

Nothing is lost.

---

# Duplicate Publish

Suppose Kafka receives the event,

but the publisher crashes before updating

```
status = SENT
```

After restart

Publisher republishes.

Duplicate event appears.

Is this okay?

Yes.

Because consumers are idempotent.

Exactly-once effects

are achieved through idempotent consumers,

not by assuming duplicate events never occur.

---

# Cleaning the Outbox

After successful publication,

rows can be

- Deleted
- Archived
- Marked SENT

Many systems periodically archive processed rows.

---

# Polling vs CDC

How does the Outbox Publisher detect new rows?

There are two common approaches.

---

## Option 1 – Polling

```
Every Second

↓

SELECT *

FROM Outbox

WHERE status='NEW'
```

Advantages

- Simple
- Easy to implement

Trade-off

Small polling delay.

---

## Option 2 – Change Data Capture (CDC)

Use tools like

```
Debezium
```

Database changes

↓

Captured automatically

↓

Kafka

No polling required.

Advantages

- Near real-time
- Efficient
- Highly scalable

Trade-off

More operational complexity.

---

# Exactly-Once Processing

Interviewers frequently ask

"What does exactly-once mean?"

Many engineers misunderstand it.

---

# Three Delivery Guarantees

---

## At Most Once

```
No Retry

Possible Data Loss
```

---

## At Least Once

```
Retry

Possible Duplicates
```

Most distributed systems operate here.

---

## Exactly Once

Business effect happens only once.

Example

```
Customer Charged

Exactly Once
```

even if duplicate messages arrive.

---

# Does Kafka Provide Exactly Once?

Kafka provides

- Idempotent Producers
- Transactions

These prevent

producer duplicates

and

coordinate writes across Kafka topics.

However,

Kafka alone cannot guarantee

exactly-once business processing.

Application logic still matters.

---

# Idempotent Consumers

Consumers must safely handle duplicate events.

Example

```
OrderCreated

↓

Replay

↓

Replay

↓

Replay
```

Final database state

```
One Order
```

not three.

---

# How to Build Idempotent Consumers?

Common approaches

## Unique Constraint

```sql
UNIQUE(eventId)
```

---

## UPSERT

```sql
INSERT ...

ON CONFLICT

DO UPDATE
```

---

## Processed Events Table

```
ProcessedEvents

eventId

processedTime
```

Before processing

↓

Check table.

If already processed

↓

Ignore.

---

# Transaction Boundary

Ideal flow

```
Read Kafka

↓

Business Logic

↓

Database Transaction

↓

Commit

↓

Commit Kafka Offset
```

Important

Only commit the Kafka offset

after

the database transaction succeeds.

Otherwise

messages may be lost.

---

# Failure Scenarios

---

## Scenario 1

Database commit fails

```
No Offset Commit

↓

Kafka Redelivers
```

Safe.

---

## Scenario 2

Kafka offset committed first

↓

Database fails

Event lost.

Never commit offsets before the database commit.

---

# Combining Outbox + Kafka

Complete flow

```
Producer

↓

Kafka

↓

Consumer

↓

Database Transaction

    │

    ├── Business Tables

    └── Outbox Table

↓

Commit

↓

Outbox Publisher

↓

Kafka

↓

Downstream Services
```

This pattern is used extensively in

- Banking
- Payments
- Trading
- E-commerce

---

# Monitoring

Monitor

- Outbox size
- Publish latency
- Failed publishes
- Retry count
- Duplicate publishes
- Consumer idempotency failures

Alert when

- Outbox grows continuously
- Publisher stops
- Kafka publish failures increase

---

# Design Trade-offs

## Direct Kafka Publish

Advantages

- Simple
- Low latency

Trade-off

Dual-write problem.

---

## Outbox Pattern

Advantages

- Reliable
- Atomic
- No lost events
- Industry standard

Trade-off

Additional table
Additional publisher
Operational complexity

---

## Polling

Advantages

- Easy

Trade-off

Polling latency

---

## CDC

Advantages

- Near real-time
- Efficient

Trade-off

More infrastructure

---

# Interview Talking Points

> The Outbox Pattern solves the dual-write problem by storing both the business update and the event in the same database transaction. A separate publisher asynchronously delivers outbox events to Kafka.

> We never attempt to update the database and Kafka within the same application transaction because they are independent distributed systems without a shared transaction coordinator.

> The Outbox Publisher can safely retry failed publications because downstream consumers are designed to be idempotent.

> Kafka's exactly-once features prevent duplicate producer writes, but true exactly-once business behavior still requires idempotent consumers and careful transaction boundaries.

> Kafka offsets should only be committed after the corresponding database transaction has successfully committed, ensuring that events are never lost during failures.

---

# Next Chapter

# Part 7 – Reliability, Retry, DLQ & Replay

Topics

- Retry Strategies
- Exponential Backoff
- Retry Topics
- Dead Letter Queue (DLQ)
- Event Replay
- Backpressure
- Circuit Breakers
- Rate Limiting
- Failure Recovery
- Operational Best Practices

# High Throughput Event Processing Platform

# Part 7 – Reliability, Retry, DLQ & Replay

> In distributed systems, failures are inevitable. Services crash, databases become unavailable, networks experience intermittent issues, and downstream systems slow down. A robust event processing platform must be designed assuming failures are normal rather than exceptional. This chapter discusses how the platform maintains reliability through retries, dead-letter queues, replay, backpressure, and failure recovery.

---

# Reliability Principles

The platform follows these principles.

✓ Never lose events

✓ Retry transient failures

✓ Isolate permanent failures

✓ Support replay

✓ Process events idempotently

✓ Prevent cascading failures

---

# Failure Scenarios

Some common failures include

- Database unavailable
- Kafka broker failure
- Consumer crash
- Producer timeout
- Network partition
- External API failure
- Invalid payload
- High traffic spikes

The architecture should continue operating despite these failures.

---

# High-Level Reliability Flow

```
              Kafka

                │

                ▼

           Consumer

                │

       Process Event

                │

         ┌──────┴──────┐

         ▼             ▼

     Success         Failure

         │             │

         ▼             ▼

 Commit Offset     Retry

                         │

                 ┌───────┴────────┐

                 ▼                ▼

          Retry Success      Retry Limit Exceeded

                 │                │

                 ▼                ▼

          Commit Offset        Dead Letter Queue
```

---

# Retry Strategy

Not every failure should immediately move to the DLQ.

Transient failures should be retried.

Examples

- Temporary database outage
- Network timeout
- External API unavailable
- Service restart

---

# Exponential Backoff

Instead of retrying immediately

```
Retry

↓

Retry

↓

Retry
```

increase the delay after each attempt.

Example

```
Attempt 1

30 sec

Attempt 2

60 sec

Attempt 3

120 sec

Attempt 4

240 sec
```

Benefits

- Prevents retry storms
- Gives downstream systems time to recover
- Reduces unnecessary load

---

# Retry Topics

Instead of blocking the main consumer,

publish failed events to retry topics.

```
events.raw

↓

Consumer

↓

Failure

↓

events.retry.1

↓

Retry Consumer

↓

Failure

↓

events.retry.2

↓

Retry Consumer

↓

Success
```

Advantages

- Main consumers continue processing
- Retries do not block healthy traffic
- Different retry delays can be configured

---

# Which Failures Should Be Retried?

Retry

✓ Database timeout

✓ HTTP 503

✓ Connection reset

✓ Kafka temporarily unavailable

Do Not Retry

✗ Invalid schema

✗ Unsupported version

✗ Missing mandatory fields

✗ Business validation failure

Permanent failures belong in the DLQ.

---

# Dead Letter Queue (DLQ)

The DLQ stores events that cannot be processed successfully after all retries are exhausted.

```
Consumer

↓

Retry

↓

Retry

↓

Retry

↓

events.dlq
```

DLQ events are never discarded.

---

# What Goes into the DLQ?

Examples

- Invalid payload
- Corrupted data
- Unknown event type
- Serialization errors
- Max retry exceeded

Operations teams can inspect and replay these events later.

---

# DLQ Message Example

```json
{
  "eventId":"evt-1001",
  "failureReason":"INVALID_SCHEMA",
  "retryCount":3,
  "originalPayload":{
      ...
  }
}
```

Include enough metadata to debug the failure.

---

# Replay

Replay is one of the biggest advantages of Kafka.

Suppose a bug is found in the Processing Service.

Old events

↓

Need reprocessing.

Instead of asking producers to resend data,

reset the consumer offset.

```
Kafka

↓

Replay

↓

Processing Pipeline
```

No data is lost.

---

# Replay Scenarios

Replay is useful when

- Business logic changes
- Bug fixes
- New downstream consumer added
- Analytics needs historical data
- Read models need rebuilding

---

# Consumer Failure

Suppose a consumer crashes while processing an event.

```
Kafka

↓

Consumer

↓

Crash
```

If the offset has not been committed,

Kafka delivers the event again after restart.

No events are lost.

---

# Offset Commit Strategy

Correct flow

```
Read Event

↓

Business Logic

↓

Database Commit

↓

Commit Kafka Offset
```

Never commit the offset before processing succeeds.

---

# Kafka Broker Failure

Kafka replicates partitions.

Example

```
Partition

Leader

Follower

Follower
```

If the leader fails,

Kafka elects a follower.

The producer continues publishing.

---

# Backpressure

Suppose producers generate

```
20,000 Events/sec
```

Consumers process

```
8,000 Events/sec
```

Without buffering,

the system collapses.

With Kafka

```
Producer

↓

Kafka

↓

Consumers
```

Kafka absorbs the burst.

Consumer lag grows temporarily,

then decreases as consumers catch up.

---

# Consumer Lag

Consumer Lag measures

```
Produced Offset

-

Consumed Offset
```

Large lag indicates consumers cannot keep up.

Possible actions

- Add more consumers
- Increase partitions
- Optimize processing
- Increase consumer resources

---

# Bounded Queues

Every service should use bounded queues.

Instead of allowing unlimited memory growth,

configure limits.

```
Queue Full

↓

Reject

or

Throttle
```

This prevents OutOfMemory errors.

---

# Rate Limiting

Suppose one producer sends

```
100,000 Requests/sec
```

while others send

```
500 Requests/sec
```

Without limits,

one producer could starve the platform.

Apply rate limiting at the API Gateway.

Example

```
1000 Requests/sec

Per Producer
```

---

# Circuit Breaker

Suppose an external service becomes unavailable.

Without protection

```
Request

↓

Timeout

↓

Retry

↓

Timeout

↓

Retry
```

Eventually

all worker threads become blocked.

Use a Circuit Breaker.

```
Failure Threshold Reached

↓

Circuit Opens

↓

Fail Fast

↓

Periodic Health Check

↓

Recover
```

Benefits

- Prevents cascading failures
- Protects worker threads
- Allows downstream recovery

---

# Timeouts

Every external dependency should have a timeout.

Never wait indefinitely.

Example

```
Pricing Service

Timeout

500 ms
```

If exceeded,

retry or move to DLQ depending on business rules.

---

# Bulkheads

Separate thread pools for different workloads.

Example

```
Payments

↓

Thread Pool A

-------------------

Notifications

↓

Thread Pool B
```

A slow notification service should never block payment processing.

---

# Monitoring Reliability

Important metrics

- Retry Rate
- DLQ Size
- Consumer Lag
- Failed Events
- Throughput
- Processing Latency
- Timeout Count
- Circuit Breaker State

Alert when

- DLQ grows continuously
- Retry rate spikes
- Consumer lag increases
- Processing latency exceeds SLA

---

# Operational Recovery

If the DLQ grows unexpectedly

Operations should

1. Identify root cause

2. Deploy fix

3. Replay affected events

4. Verify successful processing

This minimizes data loss and manual intervention.

---

# End-to-End Failure Recovery

```
Producer

↓

Kafka

↓

Consumer

↓

Database Timeout

↓

Retry Topic

↓

Retry Consumer

↓

Database Available

↓

Success

↓

Commit Offset
```

If retries fail

```
Retry Limit Exceeded

↓

DLQ

↓

Manual Investigation

↓

Replay
```

---

# Design Trade-offs

## Immediate Retry

Advantages

- Simple

Trade-off

Can overload downstream systems.

---

## Exponential Backoff

Advantages

- Reduces retry storms
- Better system stability

Trade-off

Longer recovery time.

---

## DLQ

Advantages

- Prevents blocking
- Isolates bad events

Trade-off

Requires operational monitoring.

---

## Replay

Advantages

- Recover from bugs
- Rebuild downstream systems

Trade-off

Requires idempotent consumers.

---

# Interview Talking Points

> Failures are expected in distributed systems, so the platform is designed around retries, replay, and idempotent processing rather than assuming every operation succeeds the first time.

> Transient failures are retried using exponential backoff and retry topics, while permanent failures are routed to a Dead Letter Queue for later investigation.

> Kafka's durability allows us to replay historical events whenever business logic changes or downstream systems need to be rebuilt, eliminating the need for producers to resend data.

> Consumer offsets are committed only after successful business processing and database commits, ensuring that events are never lost during failures.

> Reliability is strengthened through circuit breakers, bounded queues, rate limiting, backpressure handling, and comprehensive monitoring, preventing localized failures from cascading across the platform.

---

# Next Chapter

# Part 8 – Scalability, Performance & Distributed Systems

Topics

- Horizontal Scaling
- Auto Scaling
- Load Balancing
- Partition Scaling
- Database Scaling
- Read Replicas
- Sharding
- CAP Theorem
- Consistency Models
- Distributed Locks
- Leader Election
- Consensus
- Clock Skew
- Performance Optimizations

# High Throughput Event Processing Platform

# Part 8 – Scalability, Performance & Distributed Systems

> A high-throughput event processing platform must continue operating efficiently as event volume grows from thousands to millions of events per second. This chapter discusses how the platform scales horizontally, maintains availability, and applies distributed systems principles to achieve high throughput and reliability.

---

# Overview

Scalability should be built into every layer of the platform.

Instead of scaling one large monolithic application,

we scale each component independently.

```
Event Producer

↓

Ingestion

↓

Kafka

↓

Processing

↓

Storage

↓

Serving
```

Every layer has different bottlenecks and therefore different scaling strategies.

---

# Horizontal Scaling

The platform follows one fundamental principle:

> **Every service should be stateless.**

Since services don't store local state,

any instance can process any request.

```
                Load Balancer

                      │

      ┌───────────────┼───────────────┐

      ▼               ▼               ▼

 Ingestion-1     Ingestion-2     Ingestion-3
```

Need more capacity?

Simply add more instances.

---

# Load Balancing

The Load Balancer distributes traffic across healthy instances.

Benefits

- High availability
- Better resource utilization
- No single point of failure

Examples

- AWS ALB
- NGINX
- HAProxy

---

# Auto Scaling

Suppose CPU utilization exceeds

```
70%
```

Auto Scaling

↓

Launch additional instances

When traffic decreases

↓

Terminate unused instances

Common implementations

- Kubernetes HPA
- AWS Auto Scaling Groups

---

# Kafka Scaling

Kafka scales primarily through partitions.

Example

```
Topic

↓

48 Partitions
```

Each partition can be consumed independently.

---

# Scaling Consumers

Suppose

```
48 Partitions
```

Initially

```
12 Consumers

↓

4 Partitions Each
```

Traffic increases.

Increase

```
24 Consumers

↓

2 Partitions Each
```

No application code changes required.

---

# Partition Sizing

Too Few Partitions

- Poor parallelism
- Limited scalability

Too Many Partitions

- Higher broker overhead
- Increased metadata
- Longer leader elections

Choose enough partitions to support expected peak throughput with room for growth.

---

# Stateless Processing

Consumers should not store local state.

State belongs in

- Kafka
- Database
- Redis

Benefits

- Easier scaling
- Easier recovery
- Simpler deployments

---

# Database Scaling

Scaling databases is harder than scaling stateless services.

A common progression is

```
Primary

↓

Read Replicas

↓

Partitioning

↓

Sharding
```

Scale only when required.

---

# Read Replicas

Read-heavy workloads benefit from replicas.

```
Primary

↓

Replica1

Replica2

Replica3
```

Writes

↓

Primary

Reads

↓

Replicas

Improves read throughput significantly.

---

# Partitioning

Large tables become difficult to maintain.

Partition

by

```
Date

or

Hash(eventId)
```

Benefits

- Faster scans
- Smaller indexes
- Better maintenance

---

# Sharding

Eventually

one database instance becomes insufficient.

Shard by

```
customerId

tenantId

accountId
```

Example

```
Shard1

Customers A–M

-------------------

Shard2

Customers N–Z
```

Each shard owns a subset of data.

---

# Redis Scaling

Redis supports

- Replication
- Clustering
- Sharding

Example

```
Redis Cluster

↓

Shard1

Shard2

Shard3
```

Allows both higher throughput and more memory.

---

# Asynchronous Processing

Heavy work should always be asynchronous.

```
Ingestion

↓

Kafka

↓

Processing
```

Instead of

```
Ingestion

↓

Business Logic

↓

Database

↓

Analytics
```

This dramatically improves throughput.

---

# Batching

Instead of writing one event at a time,

batch writes.

Example

```
100 Events

↓

Single Database Write
```

Advantages

- Fewer network calls
- Higher throughput

Trade-off

Slightly higher latency.

---

# Compression

Kafka producers should compress batches.

Examples

```
LZ4

Snappy

ZSTD
```

Benefits

- Reduced network traffic
- Higher throughput

Trade-off

Additional CPU usage.

---

# Backpressure

Suppose

```
Producer

20,000 Events/sec
```

Consumers

```
8,000 Events/sec
```

Kafka buffers the excess.

Consumer lag grows temporarily.

Scale consumers

↓

Lag decreases.

Without buffering,

the system would collapse.

---

# CAP Theorem

A common Staff-level interview topic.

CAP states that during a network partition,

a distributed system can choose only two of

```
Consistency

Availability

Partition Tolerance
```

---

# What Does This Mean?

Suppose a network partition occurs.

You must choose

```
Consistency

or

Availability
```

You cannot guarantee both.

---

# Example

Financial systems

typically prefer

```
Consistency

+

Partition Tolerance
```

Social media

may choose

```
Availability

+

Partition Tolerance
```

depending on business requirements.

---

# Consistency Models

Not every system requires strong consistency.

Common models

---

## Strong Consistency

Every read sees the latest write.

Examples

- Banking
- Trading

---

## Eventual Consistency

Data converges over time.

Examples

- Analytics
- Dashboards
- Notifications

---

## Read-Your-Writes

A user immediately sees their own updates.

Often used in user-facing applications.

---

# Distributed Locks

Sometimes multiple consumers compete for the same resource.

Example

```
Inventory

1 Item Left
```

Only one consumer should reserve it.

Solutions

- Database row locks
- Redis
- ZooKeeper

Use distributed locks sparingly.

They reduce scalability.

---

# Leader Election

Some workloads require only one active worker.

Example

```
Scheduler

Cleanup Job

Partition Rebalancing
```

Leader Election

↓

One instance becomes leader.

Others remain followers.

Technologies

- ZooKeeper
- etcd
- Kubernetes Lease API

---

# Consensus

Consensus ensures distributed nodes agree on shared state.

Algorithms

- Raft
- Paxos

Examples

- Kafka controller election
- etcd
- ZooKeeper

Interview note

You don't implement consensus yourself.

You rely on systems that already provide it.

---

# Clock Skew

Distributed machines do not share perfectly synchronized clocks.

Problem

```
Server A

10:00:01

----------------

Server B

09:59:59
```

Ordering based solely on timestamps becomes unreliable.

Solutions

- NTP
- Logical ordering
- Kafka offsets
- Lamport clocks (advanced systems)

---

# Why Kafka Offsets Matter

Instead of relying solely on timestamps,

Kafka provides

```
Partition

↓

Offset
```

Offsets provide deterministic ordering within a partition.

---

# Network Partitions

Suppose

one data center loses connectivity.

The platform should continue operating where possible.

Use

- Replication
- Leader election
- Automatic failover

to recover.

---

# Performance Optimizations

Common optimizations

- Batch processing
- Connection pooling
- Compression
- Async I/O
- Efficient serialization
- Redis caching
- Prepared SQL statements
- Horizontal scaling

Optimize only after measuring bottlenecks.

---

# Monitoring Scalability

Track

- CPU
- Memory
- Kafka Throughput
- Consumer Lag
- DB Connections
- Query Latency
- Cache Hit Ratio
- Queue Depth

Alert before bottlenecks become outages.

---

# Design Trade-offs

## More Consumers

Advantages

- Better throughput

Trade-off

More infrastructure cost.

---

## More Partitions

Advantages

- Better scalability

Trade-off

Higher Kafka overhead.

---

## Strong Consistency

Advantages

- Correctness

Trade-off

Higher latency
Reduced availability during partitions.

---

## Eventual Consistency

Advantages

- Better scalability
- Better availability

Trade-off

Temporary stale reads.

---

## Distributed Locks

Advantages

- Prevent concurrent updates

Trade-off

Reduced throughput
Potential bottlenecks

Use only when absolutely necessary.

---

# Interview Talking Points

> Every service in the platform is stateless, allowing horizontal scaling simply by adding more instances behind a load balancer.

> Kafka partitions enable parallel processing, while consumer groups allow independent scaling of downstream services without impacting producers.

> Database scaling progresses from read replicas to partitioning and eventually sharding as write throughput increases.

> Different components require different consistency guarantees. Transactional systems typically prioritize strong consistency, while dashboards and analytics can tolerate eventual consistency.

> Distributed systems require careful consideration of CAP, leader election, consensus, clock skew, and backpressure. Rather than implementing these mechanisms ourselves, we leverage mature distributed technologies such as Kafka, Kubernetes, ZooKeeper, and etcd.

---

# Next Chapter

# Part 9 – Observability, Monitoring & Disaster Recovery

Topics

- Metrics
- Logging
- Distributed Tracing
- Correlation IDs
- Health Checks
- Alerting
- Disaster Recovery
- Multi-AZ
- Cross-Region Replication
- RPO/RTO
- Production Readiness

# High Throughput Event Processing Platform

# Part 9 – Observability, Monitoring & Disaster Recovery

> Building a scalable distributed system is only half the challenge. Operating it reliably in production is equally important. Observability enables engineers to understand system behavior, diagnose failures, and recover quickly. Disaster recovery ensures the platform remains available even when infrastructure fails.

---

# Overview

A production-ready platform should answer the following questions at any time:

- Is the system healthy?
- Are events flowing through the pipeline?
- Where is the bottleneck?
- Are consumers keeping up?
- Is any data being lost?
- How quickly can we recover from failures?

To answer these questions, we rely on

- Metrics
- Logging
- Distributed Tracing
- Health Checks
- Alerting
- Disaster Recovery

---

# Three Pillars of Observability

Every production system should implement

```
        Observability

             │

   ┌─────────┼─────────┐

   ▼         ▼         ▼

Metrics    Logs     Traces
```

Each pillar answers different questions.

---

# Metrics

Metrics provide quantitative information about system health.

Examples

```
Requests/sec

Latency

CPU

Memory

Consumer Lag

Throughput

Error Rate
```

Metrics are aggregated over time and displayed on dashboards.

---

# Important Platform Metrics

## API Layer

Monitor

- Request Rate
- Success Rate
- Error Rate
- Response Time
- Authentication Failures
- Rate Limit Violations

---

## Kafka

Monitor

- Producer Throughput
- Consumer Throughput
- Consumer Lag
- Broker Health
- Replication Lag
- ISR Count
- Partition Utilization

---

## Processing Pipeline

Monitor

- Events Processed/sec
- Retry Count
- DLQ Size
- Processing Latency
- Failure Rate

---

## Database

Monitor

- Read Latency
- Write Latency
- Connection Pool Usage
- Slow Queries
- Lock Contention
- Replication Lag

---

## Redis

Monitor

- Cache Hit Ratio
- Cache Miss Ratio
- Memory Usage
- Evictions

---

# Dashboards

Typical production dashboards include

```
System Health

Kafka

Consumers

Database

Redis

Business KPIs
```

Common tools

- Grafana
- Datadog
- New Relic
- CloudWatch

---

# Logging

Metrics tell us **something is wrong**.

Logs tell us **why**.

Always use

```
Structured Logs
```

instead of plain text.

---

# Example

Good

```json
{
  "eventId":"evt100",
  "customerId":"cust123",
  "status":"Processed",
  "latencyMs":18
}
```

Bad

```
Processing completed successfully
```

Structured logs are searchable and machine-readable.

---

# Log Levels

```
DEBUG

INFO

WARN

ERROR

FATAL
```

Use the appropriate level.

Avoid excessive DEBUG logging in production.

---

# Correlation ID

Every request receives a

```
Correlation ID
```

Example

```
Producer

↓

API Gateway

↓

Kafka

↓

Validation

↓

Business Logic

↓

Persistence
```

Every log contains

```
Correlation ID
```

This allows engineers to trace one event through the entire platform.

---

# Distributed Tracing

Modern systems consist of many services.

Tracing connects them.

```
Request

↓

API

↓

Kafka

↓

Processing

↓

Database

↓

Redis
```

Tools

- OpenTelemetry
- Jaeger
- Zipkin
- AWS X-Ray

---

# Trace Example

A trace may show

```
API

12 ms

↓

Kafka Publish

8 ms

↓

Validation

5 ms

↓

Persistence

18 ms

↓

Total

43 ms
```

Easy to identify bottlenecks.

---

# Health Checks

Every service should expose

```
/health

/ready

/live
```

---

## Liveness Probe

Answers

```
Is the process alive?
```

If not,

restart it.

---

## Readiness Probe

Answers

```
Can this instance accept traffic?
```

If not,

remove it from the load balancer.

---

# Alerting

Metrics are useful only if someone notices them.

Configure alerts.

Examples

```
Consumer Lag

>

100,000

↓

Alert
```

```
API Error Rate

>

5%

↓

Alert
```

```
DLQ Size Growing

↓

Alert
```

---

# Common Alerts

- Kafka Broker Down
- Consumer Lag
- DLQ Growth
- Database Replication Lag
- High API Latency
- Redis Unavailable
- Disk Space Low
- CPU > 80%
- Memory > 85%

Alerts should notify

- PagerDuty
- Slack
- Email
- Ops Team

---

# SLOs and SLIs

Production systems typically define

## SLI (Service Level Indicator)

Measured value

Example

```
99.95%

Availability
```

---

## SLO (Service Level Objective)

Target

Example

```
Availability

99.9%
```

Engineering teams monitor whether the SLO is being met.

---

# Disaster Recovery

Infrastructure failures are inevitable.

Plan for them.

---

# Multi-AZ Deployment

Deploy services across multiple Availability Zones.

```
AZ-1

API

Kafka

Consumer

----------------

AZ-2

API

Kafka

Consumer
```

If one AZ fails,

traffic automatically shifts.

---

# Cross-Region Replication

For regional failures,

replicate data to another region.

```
Region A

↓

Replication

↓

Region B
```

Replicate

- Kafka
- Database
- Object Storage

---

# Backup Strategy

Regular backups

```
Daily Full Backup

Hourly Incremental Backup
```

Store backups

offsite

and

encrypted.

---

# Point-in-Time Recovery

Suppose accidental deletion occurs.

Restore database

to

```
10:42 AM
```

instead of yesterday's backup.

Critical for financial systems.

---

# RPO and RTO

Two common interview terms.

---

## RPO

Recovery Point Objective

Maximum acceptable data loss.

Example

```
5 Minutes
```

Meaning

Losing up to five minutes of data is acceptable.

---

## RTO

Recovery Time Objective

Maximum acceptable downtime.

Example

```
15 Minutes
```

Meaning

The system should recover within fifteen minutes.

---

# Failover

Suppose

Primary Database fails.

```
Primary

↓

Replica Promotion

↓

New Primary
```

Applications reconnect automatically.

---

# Rolling Deployments

Avoid downtime during releases.

```
Old Version

↓

New Instance

↓

Health Check

↓

Shift Traffic

↓

Remove Old Instance
```

No downtime.

---

# Blue-Green Deployment

Maintain two environments.

```
Blue

(Current)

Green

(New)
```

Switch traffic after validation.

Rollback becomes simple.

---

# Canary Deployment

Deploy to

```
5%

↓

20%

↓

50%

↓

100%
```

of users gradually.

Detect problems early.

---

# Chaos Engineering

Test failures intentionally.

Examples

- Kill Kafka Broker
- Stop Consumer
- Increase Network Latency
- Disconnect Database

Verify

the platform recovers automatically.

Tools

- Chaos Monkey
- Litmus
- Gremlin

---

# Operational Runbooks

For every major alert,

maintain a runbook.

Example

```
Consumer Lag High

↓

Check Consumer Health

↓

Scale Consumers

↓

Check Broker

↓

Verify Recovery
```

Runbooks reduce incident response time.

---

# Production Readiness Checklist

✓ Metrics

✓ Logs

✓ Tracing

✓ Health Checks

✓ Alerts

✓ Auto Scaling

✓ Backups

✓ Multi-AZ

✓ Disaster Recovery

✓ Runbooks

✓ Load Testing

✓ Security Reviews

---

# Design Trade-offs

## More Metrics

Advantages

- Better visibility

Trade-off

Higher storage cost.

---

## More Logging

Advantages

- Easier debugging

Trade-off

Higher storage and indexing costs.

---

## Multi-Region

Advantages

- Better resilience

Trade-off

Higher operational complexity and cost.

---

## Frequent Backups

Advantages

- Lower RPO

Trade-off

More storage and backup overhead.

---

# Interview Talking Points

> Observability is built on three pillars: metrics, logs, and distributed tracing. Metrics tell us that a problem exists, logs explain why, and traces identify where latency or failures occur across service boundaries.

> Every request receives a Correlation ID that propagates through Kafka, processing services, databases, and downstream systems, allowing end-to-end debugging.

> We continuously monitor consumer lag, retry rates, DLQ growth, database latency, and cache health. These metrics provide early indicators of bottlenecks before they become outages.

> Disaster recovery is achieved through multi-AZ deployments, cross-region replication, automated failover, encrypted backups, and clearly defined RPO and RTO targets.

> Production readiness extends beyond code. Automated deployments, health checks, alerting, chaos testing, and operational runbooks are essential for operating a high-throughput distributed platform at scale.

---

# Next Chapter

# Part 10 – Complete End-to-End Walkthrough & Interview Guide

Topics

- End-to-End Event Flow
- Sequence Diagram
- Failure Scenarios
- Common Interview Questions
- Trade-offs
- Staff Engineer Talking Points
- 5-Minute Interview Answer
- 10-Minute Interview Answer
- Architecture Summary

# High Throughput Event Processing Platform

# Part 10 – Complete End-to-End Walkthrough, Trade-offs & Interview Guide

> This chapter brings together every component of the platform and demonstrates how an event flows through the system from ingestion to downstream consumers. It also covers common interview questions, trade-offs, and how to present the design in a Staff-level interview.

---

# Complete System Architecture

```
                           Event Producer

                                 │

                                 ▼

                          Load Balancer

                                 │

                                 ▼

                           API Gateway

                                 │

                                 ▼

                    Event Ingestion Service

      Authentication
      Authorization
      Schema Validation
      Rate Limiting
      Idempotency
      Normalization
      Correlation ID

                                 │

                                 ▼

                          Kafka Producer

                                 │

                                 ▼

                         Kafka Cluster

                                 │

      ┌──────────────────────────┴─────────────────────────┐

      ▼                                                    ▼

 Validation Consumer                              Other Consumers

      │

      ▼

 Deduplication Consumer

      │

      ▼

 Business Processing

      │

      ▼

 Persistence Service

      │

      ├──────────────┐

      ▼              ▼

 Business DB     Outbox Table

                      │

                      ▼

              Outbox Publisher

                      │

                      ▼

               events.processed

      ┌────────────┬─────────────┬──────────────┬─────────────┐

      ▼            ▼             ▼              ▼

 Reporting     Analytics     Notifications     Search

      │

      ▼

 Redis / Read Models
```

---

# End-to-End Event Flow

Let's follow one event through the platform.

---

# Step 1 – Event Produced

An upstream producer generates an event.

Example

```json
{
  "eventType":"ORDER_CREATED",
  "customerId":"C100",
  "orderId":"O500",
  "amount":250
}
```

The producer could be

- Mobile App
- Order Service
- Payment Gateway
- Trading System
- IoT Device

---

# Step 2 – Event Ingestion

Producer calls

```
POST /events
```

Flow

```
Producer

↓

Load Balancer

↓

API Gateway

↓

Event Ingestion Service
```

The ingestion layer performs

- Authentication
- Authorization
- Schema Validation
- Rate Limiting
- Idempotency
- Normalization
- Correlation ID Generation

---

# Step 3 – Publish to Kafka

After lightweight validation

```
Event

↓

Kafka Producer

↓

Kafka
```

The request returns

```
HTTP 202 Accepted
```

Heavy processing has **not** started yet.

---

# Step 4 – Validation

Validation Consumer

checks

- Schema
- Required fields
- Version
- Metadata

Invalid events

↓

DLQ

Valid events

↓

Next Stage

---

# Step 5 – Deduplication

Duplicate event?

Example

```
evt100

↓

evt100

↓

evt100
```

Deduplication

↓

Single business event

Techniques

- UNIQUE constraint
- UPSERT
- Idempotency table

---

# Step 6 – Business Processing

Business logic executes.

Examples

Payment

↓

Calculate Fees

Trading

↓

Allocate Trade

Order

↓

Reserve Inventory

Fraud

↓

Calculate Risk

External services are protected using

- Timeouts
- Retries
- Circuit Breakers

---

# Step 7 – Persistence

Business state is updated.

Inside one database transaction

```
Insert Business Data

↓

Insert Outbox Event

↓

Commit
```

Business state

and

Outbox

remain consistent.

---

# Step 8 – Outbox Publisher

Publisher continuously scans

```
Outbox
```

Publishes

↓

Kafka

Marks event

↓

SENT

---

# Step 9 – Downstream Consumers

Different consumer groups process

```
events.processed
```

independently.

Examples

```
Reporting

Analytics

Notifications

Search

ML
```

None of these consumers know about each other.

---

# Step 10 – Read Models

Projection Service

updates

```
Redis

↓

Read Database
```

Dashboard APIs become extremely fast.

---

# Complete Sequence Diagram

```
Producer

↓

API Gateway

↓

Event Ingestion

↓

Kafka

↓

Validation

↓

Deduplication

↓

Business Processing

↓

Database

↓

Outbox

↓

Kafka

↓

Reporting

Analytics

Notifications

Search

↓

Redis

↓

Read APIs
```

---

# Failure Scenario 1

Kafka Unavailable

Solution

- Retry publish
- Return 503 if durability cannot be guaranteed
- Producer retries

---

# Failure Scenario 2

Consumer Crash

Kafka

↓

Offset not committed

↓

Restart

↓

Reprocess

No event loss.

---

# Failure Scenario 3

Database Failure

Consumer

↓

Retry

↓

Retry Topic

↓

Database Recovers

↓

Success

---

# Failure Scenario 4

Retry Exhausted

```
Retry

↓

Retry

↓

Retry

↓

DLQ
```

Operations investigate later.

---

# Failure Scenario 5

Business Logic Bug

Deploy Fix

↓

Reset Consumer Offset

↓

Replay Kafka

No producer involvement required.

---

# Failure Scenario 6

Redis Failure

Read APIs

↓

Fallback

↓

Database

Higher latency

No outage.

---

# Failure Scenario 7

Region Failure

Traffic

↓

Secondary Region

↓

Consumers Continue

Cross-region replication minimizes downtime.

---

# Why This Architecture?

## Why Kafka?

- Durable storage
- Replay
- Decoupling
- Parallel processing

---

## Why Event Ingestion Service?

- Authentication
- Validation
- Idempotency
- Schema evolution
- Kafka abstraction

---

## Why CQRS?

Separate

```
Writes

↓

OLTP

Reads

↓

Redis / Read Models
```

Independent scaling.

---

## Why Outbox?

Eliminates the dual-write problem.

Database

and

Kafka

remain consistent.

---

## Why Redis?

Read-heavy APIs

↓

Low latency

↓

Reduced database load.

---

## Why Consumer Groups?

Independent scaling.

Analytics

does not impact

Notifications.

---

## Why Replay?

Allows

- Bug fixes
- Rebuilding projections
- New consumers
- Historical analytics

without requesting producers to resend events.

---

# Design Trade-offs

## API Before Kafka

Advantages

- Validation
- Security
- Governance

Trade-off

One additional network hop.

---

## Kafka

Advantages

- Scalability
- Replay
- Decoupling

Trade-off

Operational complexity.

---

## CQRS

Advantages

- Faster reads
- Independent scaling

Trade-off

Eventual consistency.

---

## Outbox

Advantages

- Reliable event publishing

Trade-off

Additional infrastructure.

---

## Event-Driven Architecture

Advantages

- Loose coupling
- Replay
- Horizontal scaling

Trade-off

Harder debugging
Eventually consistent workflows.

---

# Common Interview Questions

---

## Why not publish directly to Kafka?

Because

the Event Ingestion Service centralizes

- Authentication
- Validation
- Idempotency
- Observability
- Schema management

Kafka remains an internal implementation detail.

---

## Why not process synchronously?

Processing

↓

Database

↓

Notifications

↓

Analytics

would significantly increase latency.

Kafka decouples ingestion from processing.

---

## Why not one database?

Different workloads

- Transactions
- Caching
- Analytics

require different storage technologies.

---

## Why Outbox instead of DB + Kafka?

Direct dual writes are unreliable.

Outbox guarantees atomicity.

---

## Why At-Least-Once instead of Exactly-Once?

Exactly-once delivery across distributed systems is extremely difficult.

Instead

we combine

- At-least-once delivery
- Idempotent consumers

to achieve exactly-once business effects.

---

## Why partition by Customer ID?

Ordering.

All events for one customer remain in the same partition.

---

## How does the system scale?

Every layer scales independently.

- API
- Kafka
- Consumers
- Database
- Redis

---

# Staff Engineer Talking Points

These are concise, high-signal statements suitable for senior interviews.

> I separate event ingestion from business processing so the ingestion path remains lightweight and can acknowledge requests quickly while downstream processing scales independently.

---

> Kafka acts as the durable event backbone, enabling replay, loose coupling, independent consumer scaling, and resilience to downstream failures.

---

> Every consumer is idempotent because retries and replay are fundamental characteristics of distributed systems rather than exceptional cases.

---

> I separate command and query responsibilities using CQRS, allowing the transactional database to optimize writes while Redis and read models optimize query performance.

---

> The Outbox Pattern eliminates the dual-write problem by ensuring database updates and event publication remain consistent without requiring distributed transactions.

---

> Rather than relying on exactly-once delivery guarantees, I design for at-least-once delivery combined with idempotent consumers to achieve exactly-once business outcomes.

---

> Operational excellence is built through observability, automated recovery, replay capabilities, circuit breakers, retries, and disaster recovery rather than assuming infrastructure never fails.

---

# 5-Minute Interview Answer

> "I'd design the platform as an event-driven architecture with four logical layers: Event Ingestion, Streaming, Processing, and Serving. Event Producers submit events through an Event Ingestion API, which performs authentication, validation, idempotency checks, normalization, and publishes events to Kafka. Kafka serves as the durable event backbone, enabling decoupling, replay, and independent scaling. Processing is implemented as a pipeline of stateless consumers responsible for validation, deduplication, business logic, and persistence. The write path persists business data and an Outbox record within the same transaction, while a separate Outbox Publisher reliably publishes downstream events. CQRS separates the write model from optimized read models stored in Redis. Reliability is achieved through retries, DLQs, replay, circuit breakers, and idempotent consumers. The platform scales horizontally at every layer and is monitored using metrics, logs, tracing, and automated alerting."

"The processing pipeline is logically divided into stages such as validation, deduplication, business processing, and persistence, with each stage owning a single responsibility. Depending on throughput and operational complexity, these stages can either be implemented as separate Kafka consumers connected by intermediate topics or combined into fewer services. The key design principle is that the stages remain logically independent, allowing us to scale bottlenecks independently, replay from intermediate stages when needed, isolate failures, and evolve each responsibility without tightly coupling the entire processing pipeline."

---

# Architecture Summary

```
Event Producer

↓

Event Ingestion

↓

Kafka

↓

Validation

↓

Deduplication

↓

Business Processing

↓

Persistence

↓

Outbox

↓

Kafka

↓

Reporting

Analytics

Search

Notifications

↓

Redis / Read Models
```

---

# Complete Topics Covered

This design includes

- Requirements
- Capacity Planning
- Event Ingestion
- Kafka Architecture
- Processing Pipeline
- Database Design
- CQRS
- Outbox Pattern
- Exactly-Once Processing
- Retry
- DLQ
- Replay
- Scalability
- CAP Theorem
- Distributed Systems Concepts
- Observability
- Disaster Recovery
- Trade-offs
- End-to-End Flow
- Staff-Level Talking Points
- Interview Summary

---

# Final Interview Closing

> "The key design principle is to keep event ingestion lightweight, decouple processing through Kafka, make every consumer idempotent, and build the platform around replayability, fault tolerance, and horizontal scalability. Rather than optimizing for perfect delivery guarantees, I optimize for operational correctness through idempotent processing, durable messaging, retries, and observability. This architecture scales from thousands to millions of events while remaining resilient, maintainable, and easy to evolve."


# Master Event-Driven System Architecture

> This document describes the complete high-level architecture of a scalable event-driven platform capable of processing millions of events per day. The design emphasizes reliability, scalability, idempotency, replayability, and fault tolerance.

---

# High-Level Architecture

```text
                                          EVENT PRODUCERS

                    Client Apps | Internal Systems | Microservices | Trading Systems
                                              |
                                              |
                                        HTTP / gRPC
                                              |
                                              ▼
                                   +--------------------+
                                   |   Load Balancer    |
                                   +--------------------+
                                              |
                                              ▼
                                   +--------------------+
                                   |    API Gateway     |
                                   |--------------------|
                                   | • Authentication   |
                                   | • Authorization    |
                                   | • Rate Limiting 	|
								   | • Routing		
                                   | • Logging          |
                                   | • Metrics          |
                                   +--------------------+
                                              |
                                              ▼
                          +-----------------------------------------+
                          |     Event Ingestion Service             |
                          |-----------------------------------------|
                          | • Schema Validation                     |
                          | • Normalize Payload                     |
                          | • Generate CorrelationId                |
                          | • API Idempotency Check                 |
                          +-----------------------------------------+
                                   |                 |
                     Check eventId |                 |
                                   ▼                 |
                        +-------------------+        |
                        | Redis             |        |
                        |-------------------|        |
                        | eventId (TTL)     |        |
                        | API Idempotency   |        |
                        +-------------------+        |
                                   |                 |
                                   +-----------------+
                                              |
                                              ▼
                                    Publish Event
                                              |
                                              ▼
======================================================================================
                                   KAFKA CLUSTER
======================================================================================

Topic : events.raw
Partition Key : customerId / accountId / orderId / tradeId

                                              |
                                              ▼

                         +-------------------------------------------+
                         | Processing Consumer Group                 |
                         |-------------------------------------------|
                         | • Validation                              |
                         | • Deduplication                           |
                         | • Business Logic                          |
                         | • Persistence                             |
                         +-------------------------------------------+
                                              |
                                              ▼

======================================================================================
                           SINGLE DATABASE TRANSACTION
======================================================================================

BEGIN TRANSACTION

    ┌────────────────────────────────────────────────────────────────────────────┐
    │ BUSINESS TABLES                                                           │
    │---------------------------------------------------------------------------│
    │ Orders / Trades / Payments / Accounts                                    │
    │                                                                           │
    │ Stores Current Business State                                             │
    └────────────────────────────────────────────────────────────────────────────┘

                         +

    ┌────────────────────────────────────────────────────────────────────────────┐
    │ processed_events                                                          │
    │---------------------------------------------------------------------------│
    │ eventId (PK)                                                              │
    │ consumerName                                                              │
    │ processedAt                                                               │
    │                                                                           │
    │ Consumer Idempotency                                                      │
    └────────────────────────────────────────────────────────────────────────────┘

                         +

    ┌────────────────────────────────────────────────────────────────────────────┐
    │ outbox                                                                    │
    │---------------------------------------------------------------------------│
    │ outboxId                                                                  │
    │ eventId                                                                   │
    │ payload                                                                   │
    │ status = NEW                                                              │
    │                                                                           │
    │ Reliable Event Publishing                                                 │
    └────────────────────────────────────────────────────────────────────────────┘

COMMIT

======================================================================================

                                              |
                                              ▼

                               Commit Kafka Offset
                        (Only AFTER DB Transaction Commits)

                                              |
                                              ▼

======================================================================================
                                 OUTBOX PUBLISHER
======================================================================================

                   Poll Outbox Table (or CDC using Debezium)

                                              |
                                              ▼

                                      Publish Event

                                              |
                                              ▼

                                    Kafka (events.processed)

                                              |
              ┌──────────────┬──────────────┬──────────────┬──────────────┐
              ▼              ▼              ▼              ▼
         Analytics     Notifications      Search      Reporting

                                              |
                                              ▼

                                  Projection Service

                                              |
                                              ▼

                                 +----------------------+
                                 | Redis Read Cache     |
                                 +----------------------+

                                              |
                                              ▼

                                   Read APIs / Dashboards
```

---

# End-to-End Flow

## Step 1 – Event Producer

A producer generates a business event.

Examples

- Mobile application
- Trading platform
- Payment gateway
- Internal microservice
- External partner

Example

```json
{
  "eventId":"evt-1001",
  "eventType":"ORDER_CREATED",
  "payload":{
      "orderId":"12345",
      "customerId":"100",
      "amount":250
  }
}
```

---

## Step 2 – API Gateway

The API Gateway performs cross-cutting concerns.

Responsibilities

- Authentication
- Authorization
- Rate limiting
- Logging
- Metrics
- Routing

No business logic belongs here.

---

## Step 3 – Event Ingestion Service

The Event Ingestion Service is intentionally lightweight.

Responsibilities

- Schema validation
- Payload normalization
- Generate Correlation ID
- API idempotency
- Publish to Kafka

Business processing is intentionally deferred.

---

## Step 4 – API Idempotency (Redis)

Purpose

Prevent duplicate API requests caused by retries.

Example

```
POST /events

↓

Timeout

↓

Retry

↓

Same eventId
```

The ingestion service checks Redis.

```
SETNX(eventId)
```

If the event already exists

↓

Return the previous response.

Do not publish to Kafka again.

Redis stores

| Key | Value |
|------|-------|
| eventId | ACCEPTED |

TTL

Typically

24–48 hours.

### API Idempotency Response

When the Event Ingestion Service accepts a new event, it stores the `eventId` along with the response in Redis (or an `idempotency_keys` table) and returns:

```json
{
  "eventId": "evt-100",
  "status": "ACCEPTED",
  "acceptedAt": "2026-07-12T18:30:15.123Z"
}
```

If the producer retries with the same `eventId`, the ingestion service detects the duplicate, **does not publish the event to Kafka again**, and returns the **same cached response**. This makes retries transparent while preventing duplicate event ingestion.


---

## Step 5 – Kafka

Kafka acts as the durable event backbone.
decouples ingestion from processing layer.. prevents tight coupling..

Responsibilities

- Durable storage
- Replay
- Decoupling
- Parallel processing
- Backpressure handling

Topic

```
events.raw
```

Partition Key

```
customerId

accountId

orderId

tradeId
```

Ordering is preserved within each partition.

---

## Step 6 – Processing Consumer

The Processing Consumer Group performs all business processing.

Responsibilities

- Validation
- Deduplication
- Business logic
- Persistence

For most systems (including ~50 million events/day), combining these responsibilities into a single consumer service is a practical design choice. The stages remain logically separate and can be split into independent services if throughput or organizational ownership requires it.

---

## Step 7 – Database Transaction

Everything happens within one ACID transaction.

```
BEGIN

↓

Update Business Tables

↓

Insert processed_events

↓

Insert outbox

↓

COMMIT
```

Only after the transaction commits successfully do we commit the Kafka offset.

---

# Business Tables

Examples

- Orders
- Trades
- Payments
- Accounts

Purpose

Store the current business state.

Example

| orderId | customerId | amount | status |
|----------|------------|---------|--------|

---

# processed_events

Purpose

Consumer idempotency.

Prevents duplicate Kafka processing.

Example

| eventId | consumerName | processedAt |
|----------|--------------|-------------|

Before processing

```
SELECT eventId
```

If found

↓

Skip processing.

---

# outbox

Purpose

Reliable Kafka publishing.

Example

| outboxId | eventId | payload | status |
|-----------|----------|----------|--------|

The Outbox Publisher continuously scans this table and publishes new events to Kafka.

---

## Step 8 – Commit Kafka Offset

Only after

- Database transaction succeeds
- Business tables updated
- processed_events inserted
- outbox inserted

do we commit the Kafka offset.

This guarantees that Kafka will redeliver the event if processing fails before the transaction completes.

---

## Step 9 – Outbox Publisher

A dedicated service publishes events from the Outbox table.

Flow

```
Read NEW rows

↓

Publish to Kafka

↓

Mark row SENT
```

This solves the Dual Write Problem.

---

## Step 10 – Downstream Consumers

Independent consumer groups subscribe to

```
events.processed
```

Examples

- Reporting
- Analytics
- Notifications
- Search
- Machine Learning

Each consumer maintains its own Kafka offset.

---

## Step 11 – Projection Service

Projection Services consume processed events and build optimized read models.

Examples

- Customer dashboard
- Search indexes
- Aggregated summaries

---

## Step 12 – Redis Read Cache

Frequently accessed data is cached.

Examples

- Dashboard
- Recent transactions
- Customer summary

Purpose

- Low-latency reads
- Reduced database load

---

# Storage Responsibilities

| Storage | Purpose | Retention |
|----------|----------|-----------|
| Redis (Ingress) | API idempotency | 24–48 hours |
| Kafka | Durable event log & replay | Configurable (e.g., 7–30 days) |
| Business Tables | Current business state | Permanent |
| processed_events | Consumer idempotency | Based on replay window |
| outbox | Reliable event publishing | Until published (then archived/deleted) |
| Redis Read Cache | Fast reads | Short-lived cache |

---

# Why Two Idempotency Mechanisms?

## API Idempotency

Prevents duplicate API requests.

```
Producer Retry

↓

Redis

↓

Skip Duplicate
```

---

## Consumer Idempotency

Prevents duplicate Kafka processing.

```
Kafka Retry

↓

processed_events

↓

Skip Duplicate
```

They solve different problems and both are required.

---

# Key Design Principles

- Keep the ingestion path lightweight.
- Kafka is the system's durable event backbone.
- Business state is stored in the OLTP database.
- Consumers are idempotent.
- Use the Outbox Pattern to eliminate dual writes.
- Commit Kafka offsets only after the database transaction succeeds.
- Scale each layer independently.
- Replay historical events when business logic changes.
- Use Redis only for optimization, never as the system of record.

---

# Staff-Level Summary

> The architecture separates event ingestion from business processing using Kafka as the durable event backbone. The ingestion service performs lightweight validation and API idempotency before publishing to Kafka. A stateless processing consumer performs validation, deduplication, business logic, and persistence within a single ACID transaction that updates business tables, records consumer idempotency, and inserts an outbox event. An Outbox Publisher reliably publishes downstream events to Kafka, allowing independent consumer groups such as reporting, analytics, notifications, and search to process events asynchronously. This design provides high throughput, replayability, fault isolation, horizontal scalability, and operational reliability while avoiding the dual-write problem.

# Idempotency & Database Design (Final Version)

This document explains the final idempotency strategy and database design for our event-driven architecture.

---

# Overview

There are **two different types of idempotency** in the system.

They solve **different problems**.

```
API Request

↓

API Idempotency

↓

Kafka

↓

Consumer

↓

Consumer Idempotency
```

Both are required because duplicates can occur at different stages.

---

# 1. API Idempotency (Ingress Layer)

## Purpose

Prevent duplicate **API requests** from entering Kafka.

Example

```
Producer

↓

POST /events

↓

Network Timeout

↓

Producer Retries

↓

POST /events (same event)
```

Without API idempotency

```
Kafka

↓

Event A

Event A
```

Duplicate events enter Kafka.

---

## Solution

The producer sends a unique

```
eventId
```

Example

```json
{
  "eventId":"evt-100",
  "eventType":"ORDER_CREATED"
}
```

The Event Ingestion Service checks whether this event has already been accepted.

---

## Storage

Preferred implementation

```
Redis
```

Key

```
eventId
```

Value

```
ACCEPTED
```

TTL

```
24–48 Hours
```

Example

```
event:evt-100

↓

ACCEPTED
```

If Redis already contains the key,

the Event Ingestion Service immediately returns the previous response.

The event is **not published to Kafka again.**

---

## Alternative

Instead of Redis,

a database table can be used.

### idempotency_keys

| eventId | status | createdAt | expiresAt |
|----------|---------|-----------|-----------|

This is useful if durable API idempotency is required.

---

# 2. Consumer Idempotency

## Purpose

Prevent duplicate Kafka event processing.

Example

```
Kafka

↓

Consumer

↓

Update Database

↓

Consumer Crashes

↓

Offset NOT Committed

↓

Kafka Redelivers Event
```

Without consumer idempotency,

business logic executes twice.

---

# Our Final Design

Instead of maintaining a separate

```
processed_events
```

table,

we use the

```
events
```

table.

The **events table serves two purposes**:

1. Permanent audit trail
2. Consumer idempotency

---

# Database Tables

## 1. events

Purpose

- Permanent event history
- Consumer idempotency
- Audit
- Replay
- Compliance

Schema

| Column | Description |
|----------|-------------|
| eventId (PK/UNIQUE) | Unique Event ID |
| eventType | Event Type |
| payload | Original JSON Payload |
| source | Producer |
| receivedAt | Timestamp |

Example

| eventId | eventType |
|----------|------------|
| evt100 | ORDER_CREATED |
| evt101 | PAYMENT_COMPLETED |

Notice

```
eventId

is UNIQUE
```

If the same Kafka event is processed twice,

```
INSERT INTO events
```

fails,

which immediately tells us

the event has already been processed.

No duplicate business processing occurs.

---

## 2. Business Tables

Examples

```
Orders

Trades

Payments

Accounts
```

Purpose

Store the **current business state**.

Example

Orders

| orderId | customerId | amount | status |
|----------|------------|---------|--------|
| O100 | C10 | 250 | CREATED |

Notice

Business tables **do not store events**.

They store the latest business state produced by processing those events.

---

## 3. outbox

Purpose

Reliable Kafka publishing.

Schema

| outboxId | eventId | payload | status |
|-----------|----------|----------|--------|

The Outbox Publisher continuously reads

```
status = NEW
```

publishes the event,

then marks it

```
SENT
```

This eliminates the Dual Write Problem.

---

# Final Database Transaction

The Processing Consumer performs

one ACID transaction.

```sql
BEGIN;

INSERT INTO events (...);

UPDATE Orders / Trades / Payments (...);

INSERT INTO outbox (...);

COMMIT;
```

Everything succeeds together,

or everything rolls back.

---

# Why Insert Into events First?

Suppose Kafka redelivers

```
evt-100
```

The transaction starts.

```
INSERT INTO events
```

If

```
eventId

already exists
```

the UNIQUE constraint fails.

The transaction rolls back.

Business logic is skipped.

This makes the consumer idempotent.

---

# Complete Storage Responsibilities

| Storage | Purpose | Retention |
|----------|----------|-----------|
| Redis | API idempotency | 24–48 hours |
| Kafka | Temporary event log, replay | Configurable (7–30+ days) |
| events | Permanent event history + Consumer idempotency | Permanent |
| Business Tables | Current business state | Permanent |
| outbox | Reliable event publishing | Until published |

---

# Complete Flow

```
Producer

↓

POST /events

↓

Event Ingestion Service

↓

Redis
(API Idempotency)

↓

Kafka
(events.raw)

↓

Processing Consumer

↓

BEGIN TRANSACTION

1. INSERT INTO events
   (Audit + Consumer Idempotency)

2. UPDATE Business Tables

3. INSERT INTO outbox

COMMIT

↓

Commit Kafka Offset

↓

Outbox Publisher

↓

Kafka
(events.processed)

↓

Reporting

Analytics

Search

Notifications
```

---

# Why Two Idempotency Layers?

## API Idempotency

Protects against

```
Duplicate HTTP Requests
```

Uses

```
Redis
```

---

## Consumer Idempotency

Protects against

```
Duplicate Kafka Deliveries
```

Uses

```
events

table

(UNIQUE eventId)
```

They solve different problems and are both required.

---

# Why Keep an events Table?

Unlike Kafka,

which has limited retention,

the

```
events
```

table provides

- Permanent audit history
- Regulatory compliance
- Historical investigations
- Replay support
- Consumer idempotency

For financial and trading systems, this is a common and recommended design.

---

# Final Architecture Summary

```
Ingress Layer

↓

Redis
(API Idempotency)

↓

Kafka

↓

Processing Consumer

↓

Database Transaction

    1. events
       (Audit + Consumer Idempotency)

    2. Business Tables
       (Current Business State)

    3. outbox
       (Reliable Event Publishing)

↓

Commit Kafka Offset

↓

Outbox Publisher

↓

Kafka (events.processed)

↓

Downstream Consumers
```

---

# Staff-Level Interview Summary

> The architecture implements idempotency at two independent layers. The Event Ingestion Service prevents duplicate API requests using Redis-backed idempotency keys with a short TTL before publishing events to Kafka. At the processing layer, the immutable **events** table serves as both the permanent audit log and the consumer idempotency mechanism through a unique `eventId` constraint. Each Kafka event is processed within a single ACID transaction that inserts the event into the audit table, updates the business tables, and records an outbox entry. This design guarantees reliable processing, supports replay and compliance, eliminates duplicate business effects, and avoids the dual-write problem through the Outbox Pattern.