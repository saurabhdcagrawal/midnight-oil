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

# ML Model & Feature Store – Common Interview Questions

## Why do we need an Offline Feature Store?

The Offline Feature Store stores historical transaction data used for:

- Feature engineering
- Model training
- Historical analysis

It is **not** used during real-time fraud detection because querying historical data would be too slow.

---

## Why do we need Redis?

The ML model requires input features such as:

- Average spending
- Transaction velocity
- Merchant frequency
- Device history

Computing these features from millions of historical transactions during every authorization request would be too expensive.

Instead, these features are **precomputed** and stored in Redis for fast (1–5 ms) lookups.

**Redis stores features, not the ML model.**

---

## How does the ML model use Redis?

During authorization:

```
Transaction

↓

Fetch Features from Redis

↓

ML Model

↓

Fraud Score
```

The Fraud Decision Service combines:

- Current transaction
- Precomputed features from Redis

and passes them to the deployed ML model.

---

## What does the Offline Pipeline produce?

The offline pipeline has **two outputs**:

### 1. Updated ML Model

```
Historical Transactions

↓

Model Training

↓

New ML Model

↓

Deploy to Fraud Decision Service
```

The deployed model is then used for real-time inference.

---

### 2. Updated Features

```
Historical Transactions

↓

Feature Engineering

↓

Redis
```

Feature engineering computes historical features and refreshes Redis.

---

## How often is Redis updated?

It depends on the feature.

### Batch Updates (Every few hours or daily)

Examples

- Average spending
- Merchant frequency
- Monthly spending

```
Historical Transactions

↓

Feature Engineering

↓

Redis
```

### Real-Time Updates

Examples

- Last transaction
- Transaction velocity
- Last merchant
- Last country

```
Transaction

↓

Kafka

↓

Feature Update Consumer

↓

Redis
```

---

## How often is the ML model retrained?

The model is trained **offline**, not during every transaction.

Typical frequencies

- Daily
- Weekly
- On demand (if performance degrades)

After training, the new model is validated and deployed only if it performs better than the current production model.

---

## End-to-End Flow

```
Historical Transactions

        │

        ├──────────────┐

        ▼              ▼

Feature Engineering   Model Training

        │              │

        ▼              ▼

      Redis       New ML Model

        │              │

        └──────┬───────┘

               ▼

      Fraud Decision Service

               ▲

        New Transaction
```

---

## Interview Summary

> The Offline Feature Store serves two purposes: it trains improved ML models and computes historical features. The trained model is deployed to the Fraud Decision Service, while the precomputed features are loaded into Redis. During authorization, the Fraud Decision Service retrieves these features from Redis and combines them with the current transaction to generate a fraud score with the deployed ML model.


# Understanding the Offline Feature Store

## What is the Offline Feature Store?

The Offline Feature Store is **not** a collection of ML models.

It is a repository of **historical transaction data and engineered features** used for analytics, feature engineering, and model training.

Typical technologies include:

- S3
- Data Lake
- Snowflake
- BigQuery

---

## What does it store?

### Historical Transactions

| transactionId | customerId | merchant | amount | fraudLabel |
|--------------|------------|----------|--------|------------|
| T1 | C101 | Starbucks | 8 | No |
| T2 | C101 | Apple | 999 | No |
| T3 | C102 | Unknown | 4500 | Yes |

---

### Engineered Features

These features are computed from historical transactions.

| Customer | Avg Spend | Merchant Frequency | Fraud Count | Last 90-Day Spend |
|----------|-----------:|-------------------:|------------:|------------------:|
| C101 | 420 | 18 | 0 | 3250 |
| C102 | 980 | 45 | 2 | 8900 |

These features are primarily used for training ML models.

---

## Why don't we query it during authorization?

The Offline Feature Store contains millions or billions of historical records.

Querying it during every payment authorization would be too slow.

Instead, we periodically compute the important features and load them into the Online Feature Store (Redis).

---

## What is stored in Redis?

Redis stores only the latest precomputed features needed during real-time fraud detection.

Examples

- Average spending
- Merchant frequency
- Transaction velocity
- Device history
- Last transaction location

Redis provides these features in **1–5 ms**, enabling low-latency fraud decisions.

---

## Relationship Between the Offline Feature Store, Redis, and ML Model

The offline pipeline has **two outputs**.

### 1. Train a New ML Model

```
Historical Transactions

↓

Model Training

↓

New ML Model

↓

Deploy to Fraud Decision Service
```

The deployed model is then used for real-time inference.

---

### 2. Compute New Features

```
Historical Transactions

↓

Feature Engineering

↓

Redis
```

Feature engineering computes historical features and refreshes Redis.

---

## Real-Time Fraud Detection Flow

```
Transaction

↓

Fetch Features from Redis

↓

Deployed ML Model

↓

Fraud Score
```

The Fraud Decision Service combines

- Current transaction
- Features from Redis

and passes them to the deployed ML model to generate the fraud score.

---

## Mental Model

```
Offline Feature Store

↓

Historical Data + Engineered Features

        │

        ├──────────────┐

        ▼              ▼

Train Model     Compute Features

        │              │

        ▼              ▼

 Deploy Model      Refresh Redis

        └──────┬───────┘

               ▼

      Fraud Decision Service
```

---

## Interview Summary

> The Offline Feature Store is a repository of historical transactions and engineered features used for analytics and model training. It is not queried during real-time authorization. Instead, the offline pipeline periodically trains new ML models and computes updated features. The trained model is deployed to the Fraud Decision Service, while the computed features are loaded into Redis for low-latency inference during fraud detection.


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


# Asynchronous Processing Pipeline

Once the Fraud Decision Service returns the fraud decision to the payment network, it publishes the transaction event to Kafka.

```text
                         Fraud Decision Service

                                   │

                        Publish Transaction Event

                                   │

                                   ▼

==========================================================================================
                               Kafka
                     Topic : fraud.transactions
==========================================================================================

                                   │

      ┌────────────────────────────┼─────────────────────────────┐

      ▼                            ▼                             ▼

+------------------+      +------------------+      +----------------------+
| Storage          |      | Analytics        |      | Notification         |
| Consumer         |      | Consumer         |      | Consumer             |
+------------------+      +------------------+      +----------------------+

      │                            │                             │

      ▼                            ▼                             ▼

+----------------------+    +-----------------------------+   SMS / Email
| OLTP Database        |    | Offline Feature Store       |   Push Notifications
|                      |    | (S3 / Data Lake /           |   Fraud Operations
|                      |    |  Snowflake / BigQuery)      |
+----------------------+    +-----------------------------+

      │                            │
      │                            │
      ▼                            ▼

+-------------------------------------------------------+
| transactions                                           |
+-------------------------------------------------------+
| transactionId (PK)                                    |
| customerId                                             |
| cardId                                                 |
| merchant                                               |
| amount                                                 |
| currency                                               |
| fraudScore                                             |
| decision (APPROVE / DECLINE / REVIEW)                  |
| transactionTime                                        |
| createdAt                                              |
+-------------------------------------------------------+

      +

+-------------------------------------------------------+
| events                                                 |
+-------------------------------------------------------+
| eventId (PK)                                           |
| transactionId                                          |
| eventType                                              |
| payload (JSON)                                         |
| source                                                 |
| receivedAt                                             |
+-------------------------------------------------------+

      +

+-------------------------------------------------------+
| outbox                                                 |
+-------------------------------------------------------+
| outboxId (PK)                                          |
| eventId                                                |
| payload (JSON)                                         |
| status (NEW / SENT)                                    |
| createdAt                                              |
+-------------------------------------------------------+

                                   │

                                   ▼

                          Outbox Publisher

                                   │

                           Poll NEW Records

                                   │

                                   ▼

==========================================================================================
                            Kafka
                     Topic : fraud.processed
==========================================================================================

                                   │

         ┌─────────────┬──────────────┬──────────────┬──────────────────────┐

         ▼             ▼              ▼              ▼

    Reporting      Search Service   Real-Time      ML Pipeline
                                   Feature Service

                                         │
                                         ▼

                             +----------------------+
                             | Redis               |
                             | Online Feature Store|
                             +----------------------+

                             Updates Features Like

                             • Transaction Velocity
                             • Last Transaction Time
                             • Last Merchant
                             • Device History
                             • Last Country

                                         ▲
                                         │
                              (Periodic Refresh)

                                         │

                             +----------------------+
                             | Batch Feature        |
                             | Refresh Job          |
                             +----------------------+

                                         ▲

                                         │

                             +----------------------+
                             | Feature Engineering  |
                             +----------------------+

                                         ▲

                                         │

                             +-----------------------------+
                             | Offline Feature Store       |
                             | (S3 / Data Lake /           |
                             |  Snowflake / BigQuery)      |
                             +-----------------------------+

                                         │

                                         ▼

                               Model Training

                                         │

                                         ▼

                              Model Validation

                                         │

                                         ▼

                            Deploy New ML Model

                                         │

                                         ▼

                          Fraud Decision Service
```

---

# Component Responsibilities

## 1. Storage Consumer

Stores transaction data in the OLTP database.

### transactions

Stores the current transaction state.

| Field | Description |
|--------|-------------|
| transactionId | Primary Key |
| customerId | Customer |
| cardId | Card |
| merchant | Merchant |
| amount | Transaction Amount |
| currency | Currency |
| fraudScore | ML Score |
| decision | APPROVE / DECLINE / REVIEW |
| transactionTime | Event Time |
| createdAt | Created Timestamp |

---

### events

Stores the immutable audit trail.

| Field | Description |
|--------|-------------|
| eventId | Primary Key |
| transactionId | Business Transaction |
| eventType | Transaction Event |
| payload | Original JSON |
| source | Payment Network |
| receivedAt | Event Time |

---

### outbox

Supports the Outbox Pattern.

| Field | Description |
|--------|-------------|
| outboxId | Primary Key |
| eventId | Related Event |
| payload | JSON Payload |
| status | NEW / SENT |
| createdAt | Created Time |

The Outbox Publisher polls this table and publishes events reliably to downstream Kafka topics.

---

## 2. Analytics Consumer

Writes historical transaction data into the Offline Feature Store.

Used for

- Historical reporting
- Dashboards
- Fraud analytics
- Regulatory reporting
- Feature engineering
- Model training

---

## 3. Notification Consumer

Generates

- SMS
- Email
- Push notifications
- Fraud operations alerts
- Case management notifications

---

## 4. Real-Time Feature Service

Consumes processed Kafka events and immediately updates Redis.

Examples

- Transaction velocity
- Last transaction timestamp
- Last merchant
- Device history
- Country history

This ensures the next fraud decision uses the latest customer behavior.

---

## 5. Batch Feature Refresh Job

Runs periodically (for example, every few hours or nightly).

Reads historical data from the Offline Feature Store and refreshes long-term customer features in Redis.

Examples

- Average spend (30 days)
- Merchant frequency
- Monthly spending
- Customer lifetime value

---

## 6. Feature Engineering

Reads historical transactions from the Offline Feature Store.

Computes

- Aggregated customer features
- Fraud features
- Training datasets

Produces two outputs:

1. Updated features for Redis.
2. Training data for the ML pipeline.

---

## 7. ML Pipeline

Uses historical data and engineered features to train new fraud detection models.

Flow

```
Offline Feature Store

↓

Feature Engineering

↓

Model Training

↓

Model Validation

↓

Deploy New ML Model

↓

Fraud Decision Service
```

The deployed model replaces the previous version used for real-time inference.

---

# Interview Summary

> After the Fraud Decision Service returns the fraud decision, the transaction is published to Kafka. Independent consumers handle storage, analytics, notifications, and feature updates. Historical transactions are written to the Offline Feature Store, where Feature Engineering computes long-term customer features and training datasets. These features are periodically refreshed into Redis, while the ML Pipeline trains and validates improved fraud models that are deployed to the Fraud Decision Service. Meanwhile, a Real-Time Feature Service continuously updates Redis with the latest behavioral features, ensuring every authorization decision uses both fresh real-time context and long-term historical insights.


# Why Do We Need Both an OLTP Database and an Offline Feature Store?

Although both store transaction data, they serve **different purposes**.

The Storage Consumer persists transactions into the **OLTP database** for operational workloads, while the Analytics Consumer stores the same Kafka events in the **Offline Feature Store** for analytical and machine learning workloads.

```
                Kafka (fraud.transactions)

                     │
        ┌────────────┴────────────┐

        ▼                         ▼

 Storage Consumer         Analytics Consumer

        │                         │

        ▼                         ▼

  OLTP Database        Offline Feature Store
                    (S3 / Data Lake / Snowflake)
```

---

## OLTP Database

The OLTP database is the operational database used by the application.

Purpose

- Store the current transaction state
- Store fraud decisions
- Support customer lookups
- Support operational APIs
- Support investigations and case management

Typical queries

```sql
SELECT *
FROM transactions
WHERE transactionId = 'TX123';
```

OLTP databases are optimized for

- Fast inserts
- Fast updates
- Low-latency point lookups
- High transaction throughput

---

## Offline Feature Store

The Offline Feature Store stores the same transaction events, but is optimized for analytics and machine learning.

Purpose

- Historical transaction storage
- Feature engineering
- Model training
- Fraud analytics
- Historical reporting

Typical queries

- Average customer spend over the last 90 days
- Merchant fraud trends
- Customer behavior analysis
- Generate ML training datasets

These queries may scan millions or billions of transactions, making them unsuitable for an OLTP database.

---

## Why Store the Same Data Twice?

Both systems receive the same Kafka events but optimize for different workloads.

- **OLTP Database** → Operational workloads (fast reads and writes)
- **Offline Feature Store** → Analytical workloads (large scans and aggregations)

Separating these workloads prevents expensive analytical queries from impacting real-time fraud detection.

---

## Interview Summary

> The Storage Consumer writes transaction events to the OLTP database, which serves as the operational system of record for the application. In parallel, the Analytics Consumer writes the same Kafka events to the Offline Feature Store, where they are used for feature engineering, model training, analytics, and historical reporting. Although both contain transaction data, they are optimized for completely different workloads.



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