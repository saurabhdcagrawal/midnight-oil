# Senior Backend System Design

## Purpose

This handbook is focused on:

* Senior Backend Interviews
* Staff/Lead Engineering Interviews
* System Design Rounds
* AI System Design Discussions
* Architecture Discussions
* Technical Leadership Discussions

This handbook intentionally excludes DSA and coding interview preparation.

---

# TABLE OF CONTENTS

# Personal Learning Note: My Biggest Bottleneck Is Retrieval, Not Knowledge

One of the most important realizations from my interview preparation journey is that my biggest challenge is usually not a lack of knowledge.

My challenge is retrieving and structuring knowledge under pressure.

## The Difference Between Learning and Retrieval

There are two very different activities:

### Knowledge Acquisition

Examples:

* Reading articles
* Watching videos
* Building notes
* Expanding handbooks
* Organizing chapters
* Improving diagrams

This operates mostly in recognition mode.

I see information and think:

"I understand this."

This feels productive and relatively comfortable.

---

### Knowledge Retrieval

Examples:

* Explaining a system design from scratch
* Answering an interviewer
* Walking through architecture
* Explaining tradeoffs
* Discussing failures and scaling

This requires:

* Recall
* Structure
* Sequencing
* Communication
* Prioritization

All in real time.

This creates significantly more cognitive load.

---

## My Natural Tendency

When something feels difficult, I often default to:

* Improving notes
* Adding another chapter
* Refining documentation
* Finding another resource

These activities are useful.

However, they can also become a form of avoidance.

The uncomfortable part is not learning.

The uncomfortable part is retrieval.

---

## The Four Stages Of Learning

### Stage 1

I don't know it.

### Stage 2

I recognize it when I see it.

### Stage 3

I can solve it myself.

### Stage 4

I can explain it clearly under pressure.

Most interview preparation stops at Stage 3.

Most offers are won at Stage 4.

---

## My Current Observation

My knowledge level is often higher than my interview performance level.

Many times I already know:

* Kafka
* Fraud Detection
* AML Screening
* Spring Boot
* AI Systems
* RAG
* Feature Stores
* Distributed Systems

The challenge is organizing those thoughts quickly and presenting them clearly.

The issue is often packaging, not knowledge.

---

## What Interviewers Actually See

Interviewers cannot directly measure what I know.

They can only measure what I communicate.

Therefore:

Knowledge × Communication = Interview Performance

Strong knowledge with weak retrieval can appear weaker than it actually is.

---

## The Highest ROI Activity Going Forward

Less:

* Building new chapters
* Collecting more resources
* Endless note refinement

More:

* Speaking
* Retrieval
* Whiteboarding
* Verbal explanations

### Practice Method

1. Open a chapter.
2. Read for two minutes.
3. Close the handbook.
4. Explain the topic for five minutes.
5. Record myself if possible.
6. Compare against the handbook.
7. Repeat.

Examples:

Monday:
Financial Aggregation Platform

Tuesday:
Fraud Detection System

Wednesday:
RAG

Thursday:
SQL Modernization Story

Friday:
Feature Store

The goal is not perfect answers.

The goal is building retrieval pathways.

---

## Important Reminder

Discomfort during explanation is not evidence that I don't know the material.

It is evidence that I am exercising retrieval.

Retrieval is a skill.

Like coding, design, or public speaking, it improves through repetition.

---

## Final Reminder

My next level of interview performance will not come from learning dramatically more content.

It will come from becoming faster and more structured at retrieving and communicating the knowledge I already possess.

The handbook is the foundation.

Verbal explanation is the multiplier.


## PART I - SYSTEM DESIGN INTERVIEW FRAMEWORK

### Chapter 1 - System Design Interview Framework

#### Requirement Gathering

Functional Requirements

Non Functional Requirements

#### Capacity Planning

Users

TPS

QPS

Storage

Bandwidth

Latency

#### API Design

REST

gRPC

WebSockets

#### Data Modeling

Entities

Relationships

Indexes

Partition Keys

#### High Level Architecture

Component Design

Data Flow

Request Flow

#### Bottlenecks

#### Scaling

#### Reliability

#### Security

#### Tradeoffs

---

### Chapter 2 - Core Building Blocks

#### Kafka

Topics

Partitions

Consumer Groups

Ordering

Replay

Dead Letter Queues

Backpressure

Retention

#### Redis

Caching

Rate Limiting

Session Management

Distributed Locks

Hot Keys

#### Databases

SQL

NoSQL

Replication

Sharding

Partitioning

#### Reliability

Retries

Exponential Backoff

Circuit Breakers

Bulkheads

DLQ

#### Consistency

Strong Consistency

Eventual Consistency

At Least Once

Exactly Once

Idempotency

Reconciliation

---

## PART II - DISTRIBUTED SYSTEMS

### Chapter 3 - Event Driven Architecture

#### Architecture

Producer
↓
Kafka
↓
Consumer
↓
Storage

#### Design Principles

Event Ownership

Schema Evolution

Replayability

Ordering

Partitioning

Idempotency

#### Interview Sound Bites

"Kafka is for propagation, not querying."

"We design for retries rather than trying to prevent them."

"Ordering is guaranteed within a partition."

---

### Chapter 4 - Distributed Cache Design

Cache Aside

Write Through

Write Back

Redis Cluster

Eviction

TTL

Hot Key Mitigation

Consistency Considerations

---

### Chapter 5 - Rate Limiter Design

Fixed Window

Sliding Window

Token Bucket

Leaky Bucket

Distributed Rate Limiting

Redis Based Designs

Interview Discussion

Tradeoffs

---

### Chapter 6 - Feature Store Design

#### Why Feature Stores Exist

#### Online Feature Store

Redis

Low Latency

#### Offline Feature Store

Data Lake

Snowflake

S3

#### Feature Freshness

#### Point In Time Correctness

#### Training Serving Skew

---

### Chapter 7 - Observability

Metrics

Logs

Distributed Tracing

SLIs

SLOs

Alerting

Prometheus

Grafana

OpenTelemetry

---

## PART III - SYSTEM DESIGN CASE STUDIES

### Chapter 8 - Financial Aggregation Platform

(QuickBooks / Mint Style System)

---

#### Requirements

Aggregate Financial Data

Account Synchronization

Transaction Synchronization

Categorization

Reporting

Analytics

Financial Insights

---

#### High Level Architecture

External Banks
↓
Plaid / MX / Yodlee
↓
Aggregation Layer
↓
Kafka
↓
Processing Layer
↓
Storage Layer
↓
Analytics Layer

---

#### Data Flow

Financial Institution
↓
Plaid
↓
Aggregation Service
↓
Kafka Topic
↓
Transaction Processor
↓
Storage

---

#### Core Components

Aggregation Service

Account Service

Transaction Service

Categorization Service

Insights Service

Analytics Service

---

#### Categorization

Rules Engine

ML Models

AI Categorization Layer

Merchant Normalization

---

#### Data Quality

Deduplication

Validation

Reconciliation

Missing Transaction Handling

Backfill Support

---

#### Scalability

External APIs are the bottleneck

Everything downstream scales horizontally

Kafka decouples ingestion from processing

Partitioning by Account ID

---

#### Security

Encryption In Transit

Encryption At Rest

Tokenization

PII Protection

Secrets Management

Audit Logging

---

#### Reliability

Retries

Circuit Breakers

Dead Letter Queues

Reconciliation Jobs

Replay Support

---

#### Interview Sound Bites

"External APIs are usually the largest bottleneck."

"Everything after ingestion can scale horizontally."

"Kafka provides decoupling between ingestion and processing."

"We reconcile because exactly-once guarantees are rarely practical across institutions."

---

#### Two Minute Interview Version

(Reserved)

---

#### Ten Minute Deep Dive

(Reserved)

---

### Chapter 9 - Real Time Fraud Detection System

---

#### Requirements

Real Time Decisions

Sub 100ms Latency

High Accuracy

Low False Positives

Explainability

Auditability

---

#### High Level Architecture

Transaction
↓
Feature Store
↓
Rules Engine
↓
ML Layer
↓
Decision Engine
↓
Approve / Decline

---

#### Feature Store

Velocity Features

Behavioral Features

Geographic Features

Device Features

Merchant Features

Historical Features

---

#### Rules Engine

Velocity Rules

Country Rules

Risk Rules

Sanctions Rules

Compliance Rules

---

#### Machine Learning Layer

Risk Scoring

Behavioral Detection

Anomaly Detection

Pattern Recognition

---

#### Decision Engine

Approve

Decline

Review

Risk Scoring

Explainability

---

#### Human Review Workflow

Transaction
↓
Risk Engine
↓
Review Queue
↓
Analyst
↓
Decision

---

#### Training Pipeline

Historical Data

Feature Generation

Model Training

Validation

Deployment

Monitoring

---

#### Model Drift

Detection

Retraining

Feedback Loops

Evaluation

---

#### Reliability

Fallback Rules

Feature Availability

Graceful Degradation

Model Rollback

---

#### Interview Sound Bites

"Rules provide explainability. Models improve accuracy."

"Feature freshness is often more important than model complexity."

"We optimize for precision and recall while controlling false positives."

---

#### Two Minute Interview Version

(Reserved)

---

#### Ten Minute Deep Dive

(Reserved)

---

### Chapter 10 - Notification Platform

Email

SMS

Push Notifications

User Preferences

Retries

DLQ

Delivery Tracking

Observability

---

### Chapter 11 - API Gateway Design

Authentication

Authorization

Routing

Rate Limiting

Request Validation

Circuit Breaking

Observability

---

### Chapter 12 - URL Shortener

API Design

Base62 Encoding

Storage

Caching

Scaling

---

### Chapter 13 - Event Analytics Platform

Event Collection

Kafka

Stream Processing

Aggregation

Dashboards

Time Series Storage

Reporting

---


