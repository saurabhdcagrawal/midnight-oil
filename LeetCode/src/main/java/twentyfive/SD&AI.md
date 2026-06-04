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

# AI Systems Design Handbook - Expanded Version

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
