# MongoDB Notes (Interview & Architecture Focus)

# 1. MongoDB Overview

MongoDB is a **NoSQL document database** that stores data in **BSON (Binary JSON)** documents instead of rows and columns. It is designed for:

* Flexible schema
* Horizontal scalability
* High availability
* Rich JSON documents
* Modern cloud-native applications
* AI/RAG workloads through vector search

---

# 2. Logical Hierarchy

```
Database
    ├── Collection
    │      ├── Document
    │      ├── Document
    │      ├── Document
```

## Document

A **Document** is the smallest/basic unit of storage in MongoDB.

Equivalent to a **row** in a relational database.

Example:

```json
{
    "_id": ObjectId("..."),
    "name": {
        "first": "John",
        "last": "Doe"
    },
    "age": 30,
    "interests": ["Reading", "Travel"]
}
```

---

## Collection

A **Collection** is analogous to a **table** in an RDBMS.

* Contains one or more documents
* Documents within the same collection do NOT need identical schemas

---

## Database

A database contains one or more collections.

Example:

```
banking_db

Customers
Accounts
Loans
Transactions
Payments
```

---

# 3. Physical Deployment Hierarchy

```
MongoDB Deployment

        Cluster
       /   |    \
   Node  Node  Node
```

## Node

A Node is a single MongoDB instance running on a VM or server.

Each node has its own:

* CPU
* Memory
* Storage
* Compute resources

Nodes do **not** share hardware resources.

---

## Cluster

A Cluster is a group of MongoDB nodes.

Types:

* Replica Set Cluster
* Sharded Cluster

---

# 4. Replica Sets (High Availability)

Replica Sets provide:

* High Availability
* Fault Tolerance
* Automatic Failover

Example:

```
          Primary

         /       \

 Secondary     Secondary
```

## Primary Node

Handles:

* Writes
* Reads (default)

---

## Secondary Nodes

Maintain copies of the data.

Can also serve reads if configured.

---

## Automatic Replication

Every write on the Primary is automatically replicated to the Secondary nodes.

MongoDB has built-in replication support.

---

## Automatic Failover

If the Primary fails:

1. Election process starts
2. One Secondary becomes the new Primary
3. Clients reconnect automatically

No manual intervention required.

---

## Multi Data Center Deployment

Replica Sets can span multiple data centers.

Benefits:

* Disaster Recovery
* High Availability
* Geographic redundancy

---

# 5. Sharding (Horizontal Scaling)

Sharding allows MongoDB to distribute data across multiple machines.

```
           MongoS
         (Router)

      /      |      \
  Shard1  Shard2  Shard3
```

## MongoS

MongoS is the MongoDB Router.

Responsibilities:

* Routes queries
* Routes writes
* Hides shard complexity from applications

Unlike traditional databases where developers implement sharding manually, MongoDB provides built-in sharding support.

---

## Sharding Strategies

### Hash Sharding

* Even data distribution
* Prevents hotspots

---

### Range Sharding

Data distributed based on value ranges.

Example:

```
Customer IDs

1-1000
1001-2000
2001-3000
```

---

## Benefits

* Horizontal Scaling
* Auto Scaling
* Data Locality
* Geographic Data Residency

---

# 6. MongoDB Atlas

MongoDB Atlas is MongoDB's cloud Database-as-a-Service (DBaaS).

Features:

* Runs on AWS, Azure, GCP
* Automated Backups
* Auto Scaling
* Replica Sets
* Sharding
* Monitoring
* Security
* Global Deployment

Alternative:

Self-managed MongoDB deployment.

---

# 7. BSON Document Model

MongoDB stores data as BSON (Binary JSON).

Supports rich data types.

Example:

```json
{
    "_id": "...",
    "name": {
        "first": "John",
        "last": "Doe"
    },
    "age": 32,
    "isActive": true,
    "balance": 10234.67,
    "interests": [
        "Travel",
        "Music"
    ]
}
```

Supports:

* Strings
* Numbers
* Boolean
* Dates
* Arrays
* Nested Objects
* Binary
* ObjectId

---

# 8. Flexible Schema

Unlike relational databases:

MongoDB does NOT require every document to have the same schema.

Example:

Customer A

```json
{
"name":"John"
}
```

Customer B

```json
{
"name":"Alice",
"preferredLanguage":"Spanish"
}
```

Both are valid.

---

## Benefits

* Easy schema evolution
* Add new fields without migrations
* No ORM boilerplate
* Application changes naturally flow into the database model

Schema validation can still be enforced when required.

---

# 9. Documents & Collections

MongoDB models business entities as documents.

Examples:

```
Customer
Account
Loan
Payment
Transaction
Mortgage
Credit Card
```

Each entity becomes a document inside a collection.

---

## Polymorphic Collections

A collection can contain multiple document structures.

Example:

financial_products

```
Mortgage

Credit Card

Auto Loan
```

Each document has different attributes.

Example:

Mortgage

```
loanAmount
interestRate
propertyAddress
```

Credit Card

```
creditLimit
rewardType
annualFee
```

Useful in Banking & Financial Services.

---

# 10. Data Modeling Philosophy

MongoDB philosophy:

> **Data accessed together should be stored together.**

This minimizes joins and improves read performance.

Unlike relational databases where normalization is preferred, MongoDB often favors denormalization for read-heavy workloads.

---

# 11. Rich Document Model

Example:

```json
{
    "_id": "...",
    "name": {
        "first": "John",
        "last": "Doe"
    },
    "interests": [
        "Reading",
        "Travel"
    ]
}
```

Notice:

* Nested objects
* Arrays
* Flexible attributes

---

# 12. Data Relationships

MongoDB supports:

* One-to-One
* One-to-Many
* Many-to-Many

Two modeling approaches exist.

---

# 13. Embedding

Store related data inside the same document.

Example:

```
Customer

    Contact

    Addresses

    Preferences
```

Example JSON

```json
{
    "customerId": 1,
    "addresses": [
        ...
    ]
}
```

### Best For

* Read-heavy workloads
* Small bounded collections
* Data accessed together

Example:

Customer + 5 addresses

Good candidate for embedding.

---

# 14. Referencing

Store child documents separately.

Parent stores only the reference.

Example:

```
Customer

Transaction IDs

Transaction Collection
```

Suitable for:

* Thousands of transactions
* Independent lifecycle
* Frequently updated child records

---

## When to Embed

Use Embedding when:

* Read together
* Small bounded arrays
* Low update frequency

Example:

Customer

* Address
* Contact
* Preferences

---

## When to Reference

Use Referencing when:

* Large collections
* Independent updates
* High write volume
* Large history

Example:

Customer

↓

Transactions

↓

Payments

---

# 15. Unbounded Arrays (Anti-Pattern)

Avoid storing huge arrays inside documents.

Example:

```
Customer

Transactions[]

500000 elements
```

Problems:

* Large document size
* Expensive updates
* Performance degradation

Use referencing instead.

---

# 16. Document Size Limit

Maximum document size:

```
16 MB
```

Best Practice:

Keep documents as small as possible while preserving locality of access.

---

# 17. Full Text Search

MongoDB supports Full Text Search using Apache Lucene.

Features:

* Text indexing
* Relevance scoring
* Phrase search
* Autocomplete

---

# 18. Vector Search & AI

MongoDB supports vector search.

Capabilities:

* Store vector embeddings
* Semantic Search
* RAG applications
* Agentic AI workloads

Supports integration with embedding providers.

Examples:

* Voyage AI
* Native embedding integrations

MongoDB acts as both:

* Embedding storage layer
* Retrieval layer

---

# 19. Banking Examples

MongoDB is commonly used for:

* Fraud Detection
* AML
* KYC
* Payments
* Digital Lending
* Customer 360
* Financial Products

Example:

Customer 360 stores:

* Accounts
* Loans
* Mortgages
* Credit Cards
* Investments

inside a unified customer view.

---

# 20. MongoDB Best Practices

✔ Store data accessed together together

✔ Embed small bounded relationships

✔ Reference very large child collections

✔ Avoid unbounded arrays

✔ Keep documents reasonably small

✔ Use Replica Sets for High Availability

✔ Use Sharding for Horizontal Scaling

✔ Let schema evolve naturally

✔ Take advantage of flexible JSON documents

✔ Use Atlas for managed cloud deployments

---

# Interview Questions

### Why MongoDB over Relational DB?

* Flexible schema
* Horizontal scaling
* Rich JSON documents
* High availability
* Easy schema evolution
* Better for hierarchical data

---

### Difference between Collection and Table?

Collection:

* Flexible schema
* JSON documents

Table:

* Fixed schema
* Rows & columns

---

### When would you embed vs reference?

Embed:

* Small bounded related data
* Read together

Reference:

* Large child collections
* Independent lifecycle
* Frequent updates

---

### Why are unbounded arrays an anti-pattern?

Because:

* Document size grows continuously
* Updates become slower
* Maximum document size is 16 MB

---

### Explain Replica Set

* Primary node
* Secondary nodes
* Automatic replication
* Automatic failover
* Election process

---

### Explain Sharding

MongoDB distributes data across multiple shards.

MongoS routes requests.

Supports:

* Hash sharding
* Range sharding

Provides horizontal scalability.
