# Senior Backend System Design & AI Handbook

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

## PART IV - AI SYSTEMS DESIGN

### Chapter 14 - AI Fundamentals

What Is An LLM?

Tokens

Context Window

Temperature

Latency

Cost

Hallucinations

Deterministic vs Probabilistic Systems

Interview Sound Bite:

"I treat LLMs as probabilistic services."

---

### Chapter 15 - AI Architecture Patterns

AI As Enrichment Layer

AI + Rules

Human In The Loop

AI Summaries

AI Categorization

AI Search

AI Recommendations

Interview Sound Bite:

"AI should enrich systems rather than own correctness-sensitive decisions."

---

### Chapter 16 - Retrieval Augmented Generation (RAG)

#### Problem

Hallucinations

Outdated Knowledge

Enterprise Data Access

Customer Specific Context

---

#### Architecture

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
Response

---

#### Concepts

Chunking

Embeddings

Vector Databases

Retrieval

Grounding

Evaluation

---

#### RAG vs Fine Tuning

RAG = Knowledge

Fine Tuning = Behavior

---

#### Interview Sound Bite

"RAG grounds the model with enterprise knowledge."

---

### Chapter 17 - Prompt Engineering

Role Prompting

Few Shot Prompting

Context Injection

Structured Outputs

Prompt Evaluation

Prompt Lifecycle

Production Prompt Design

Interview Sound Bite:

"Prompts are the control plane for LLM behavior."

---

### Chapter 18 - AI Reliability

Hallucination Reduction

Grounding

Validation

Confidence Thresholds

Fallbacks

Human Review

Evaluation Pipelines

Monitoring

Interview Sound Bite:

"We design assuming AI can be wrong."

---

### Chapter 19 - AI Agents

Agent Architecture

Tool Calling

Planning

Memory

Multi Agent Systems

MCP

Approval Gates

Agent Reliability

---

### Chapter 20 - AI In Compliance, AML and Screening

Adverse Media Summaries

Risk Summaries

Alert Prioritization

Entity Resolution

Name Matching

Case Investigation Assistants

Compliance Guardrails

Interview Sound Bite:

"AI assists analysts. It does not replace regulatory controls."

---

### Chapter 21 - AI Assisted SQL Modernization

#### Problem

Legacy SQL

Undocumented Business Logic

Complex Inclusion / Exclusion Rules

Knowledge Silos

---

#### Architecture

SQL
↓
Semantic Extraction
↓
Knowledge Layer
↓
AI Agent

---

#### Semantic Extraction

Table Lineage

Business Rules

Relationship Discovery

Rule Extraction

---

#### Guardrails

Validation

Structured Outputs

Restricted Scope

Verification Against Source SQL

---

#### Impact

Reduced Manual Analysis

Improved Transparency

Faster Knowledge Transfer

Business Rule Discovery

---

#### 90 Second Interview Story

Problem

Solution

Architecture

Validation

Impact

---

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
