# Largest Production System

**Priority:** ⭐⭐⭐⭐⭐
**Interview Frequency:** ⭐⭐⭐⭐⭐
**Study Time:** 15-20 minutes

---

# Purpose

This is usually the first architecture question.

Examples:

- Tell me about the largest production system you've built.
- Walk me through a system you designed.
- Describe a complex backend architecture you've worked on.

The goal is **not** to explain every component.

The goal is to provide enough context for the interviewer to start asking deeper questions.

---

# 90-Second Interview Answer

The largest production system I worked on was Morgan Stanley's real-time client screening platform.
Whenever a new institutional client is onboarded or an existing client's KYC profile undergoes a significant change, upstream business applications invoke our screening platform to determine whether the client matches sanctions lists, PEP databases, or adverse media. The screening decision is then used to determine whether onboarding or further processing can proceed.

The key business requirement was balancing two competing goals.

First, we needed to return a screening decision within a few hundred milliseconds so onboarding workflows could continue without introducing unnecessary latency.

Second, because screening is a regulatory process, every screening request, vendor response, decision, and downstream action had to be fully traceable for auditing and compliance.

To achieve this, upstream systems invoke our API Gateway.

The gateway authenticates requests, applies rate limiting, and routes traffic to stateless Screening Service instances.

The Screening Service validates the request, transforms our canonical request model into the vendor-specific format required by the external screening provider such as Bridger, invokes the provider, and receives the screening result.

Once the vendor responds, we normalize the vendor-specific response into our canonical internal response model and apply our decisioning logic to classify the result as HIT, REVIEW, or NO HIT.

For latency-sensitive onboarding workflows, we immediately return this decision to the calling application.

After the response is returned, we publish the normalized screening event to Kafka.

Kafka acts as our distribution layer for audit persistence, Actimize alert generation, reconciliation, analytics, and other downstream consumers.

By separating synchronous decisioning from asynchronous distribution, we kept onboarding latency low while allowing downstream systems to scale independently without impacting the customer experience.

**STOP HERE**

Wait for the interviewer to ask the next question.

---

# Important
Banks are legally required to perform ongoing customer due diligence (CDD), not just screening at account opening.

This ensures they can identify new risks if:

Watchlists change.
Customer information changes.
Regulatory requirements change

Screening doesn't happen only once.

Banks continuously monitor customers because regulations require ongoing due diligence.


Examples of profile changes include:
Customer changes legal name.
Company merges or changes ownership.
Address changes.
Date of birth correction.
New beneficial owner is added.
Risk classification/ranking changes.
Periodic KYC refresh.

Any significant change may require the client to be screened again




# Why This Introduction Works

This introduction explains:

✔ What the system does

✔ Why the business needs it

✔ The primary architectural challenge

✔ The overall architecture

✔ The major design decision

without getting into implementation details.

---

# Architecture at a Glance

```
                    Upstream Systems
                           │
                           ▼
                     API Gateway
                           │
                           ▼
                 Screening Service
                           │
             ┌─────────────┴─────────────┐
             ▼                           ▼
     External Screening          Kafka Distribution
      Provider (Bridger)                 │
             │                           │
             ▼                           ▼
     HIT / REVIEW / NO HIT      Audit / Actimize /
                                Analytics / Reporting
```

---

# Senior Engineering Signals

This answer demonstrates:

- Starts with the business problem.
- Explains functional and non-functional requirements.
- Separates synchronous and asynchronous workloads.
- Explains architecture before technology.
- Introduces the biggest architectural decision naturally.
- Uses business terminology instead of technology buzzwords.

---

# Common Mistakes

❌ Starting with Kafka.

❌ Listing technologies.

❌ Explaining every component.

❌ Talking for five minutes without pausing.

❌ Explaining Kafka internals before explaining the business problem.

---

# Likely Audible Follow-up Questions

After this introduction, interviewers typically ask one of these:

1. Walk me through one request end-to-end.
2. Why did you choose synchronous APIs?
3. Why Kafka?
4. Why canonical request and response models?
5. How did you scale the system?
6. What happens if Bridger is slow?
7. Why stateless services?
8. How do you avoid duplicate events?
9. How do you monitor the platform?
10. If traffic doubled tomorrow, what would you change?

# Walk Me Through One Request

**Priority:** ⭐⭐⭐⭐⭐
**Interview Frequency:** ⭐⭐⭐⭐⭐
**Study Time:** 20 minutes

---

# Purpose

After introducing the architecture, interviewers almost always ask:

- Walk me through one request.
- What happens when a client calls your API?
- Explain the request lifecycle.
- What exactly does your service do?

The goal is to explain ONE request from start to finish.

Don't jump into scaling, Kafka internals, or distributed systems unless asked.

---

# 2-Minute Interview Answer

Let's take the example of a new institutional client being onboarded.

### Step 1 – Client Request

An upstream onboarding application submits a screening request through our API Gateway.

The request contains customer information such as name, date of birth, address, country, and other identifying attributes.

---

### Step 2 – API Gateway

The API Gateway performs cross-cutting concerns before the request reaches our business service.

Responsibilities include:

- Authentication
- Authorization
- Rate limiting
- Request routing
- Request logging

After validation, the request is routed to one of the available Screening Service instances.

---

### Step 3 – Screening Service

This is the service my team owned.

The service first validates the request and ensures all mandatory fields are present.

Next, it transforms our canonical internal request model into the vendor-specific format expected by the external screening provider.

One of the reasons we introduced canonical request models was to isolate upstream systems from vendor-specific APIs. This allowed us to migrate between providers such as RDC and Bridger without requiring changes in consuming applications.

---

### Step 4 – External Screening Provider

The Screening Service invokes the external screening provider.

The provider performs fuzzy matching against sanctions lists, PEP databases, and adverse media datasets using attributes such as:

- Customer Name
- Address
- Date of Birth

The provider returns potential matches along with confidence scores and other screening details.

---

### Step 5 – Response Normalization

Because every screening provider has a different response format, we normalize the vendor-specific response into our canonical internal response model.

This ensures that downstream applications remain completely vendor agnostic.

---

### Step 6 – Decision Engine

Once normalized, our decision engine applies business rules and classifies the screening outcome into one of three categories:

- HIT
- REVIEW
- NO HIT

For onboarding workflows, this decision is returned synchronously to the calling application so the onboarding process can continue immediately.

---

### Step 7 – Kafka Distribution

After the synchronous response is returned, the normalized screening event is published to Kafka.

Kafka is intentionally outside the critical request path.

It acts as the distribution layer for:

- Audit Storage
- Actimize Alert Generation
- Reconciliation
- Analytics
- Reporting
- Other downstream consumers

This separation keeps customer-facing latency low while allowing downstream consumers to process events independently.

---

# Visual Flow

```

Client

↓

API Gateway

↓

Authentication

↓

Screening Service

↓

Validation

↓

Canonical Request

↓

Vendor Adapter

↓

Bridger

↓

Response Normalization

↓

Decision Engine

↓

Return Response

↓

Kafka

↓

Audit

↓

Actimize

↓

Analytics

```

---

# Why This Design?

Every major component exists for a reason.

| Component | Why |
|-----------|-----|
| API Gateway | Centralized authentication, routing, and rate limiting |
| Canonical Request | Vendor abstraction |
| Vendor Adapter | Isolate provider-specific APIs |
| Response Normalizer | Common internal response model |
| Decision Engine | Consistent business logic |
| Kafka | Decouple downstream processing |

---

# Common Mistakes

❌ Explaining Kafka partitions.

❌ Explaining retries.

❌ Talking about scaling.

❌ Jumping into production incidents.

This question is only asking:

"What happens to one request?"

Stay focused.

---

# Likely Audible Follow-up Questions

The interviewer will usually branch into one of these areas:

- Why canonical request models?
- Why normalize responses?
- Why synchronous APIs?
- Why Kafka?
- Why not write directly to the database?
- What happens if Bridger is unavailable?
- How do you handle retries?
- How do you monitor this request?
- What metrics do you collect?

Don't answer these yet.

Wait until they ask.

---

# Senior Engineering Signals

✔ Explains the request in business terms.

✔ Separates business logic from infrastructure.

✔ Shows vendor abstraction.

✔ Explains why each component exists.

✔ Keeps Kafka outside the critical path.

✔ Avoids implementation details unless asked

# 03-Why-Did-You-Choose-This-Architecture.md

**Priority:** ⭐⭐⭐⭐⭐

**Interview Frequency:** ⭐⭐⭐⭐⭐

**Study Time:** 20 minutes

---

# Purpose

After understanding the request flow, interviewers almost always ask:

- Why did you design it this way?
- Why not make everything synchronous?
- Why Kafka?
- Why separate APIs and Kafka?
- What alternatives did you consider?

This chapter explains the architectural decisions behind the system.

Remember:

Interviewers aren't evaluating whether you know Kafka.

They're evaluating whether you make good engineering decisions.

---

# 2-Minute Interview Answer

The biggest architectural decision we made was separating **decisioning** from **distribution**.

The onboarding application only needs one thing immediately—a screening decision.

Everything else, such as audit persistence, Actimize alert generation, reconciliation, reporting, and analytics, does not need to happen before the user receives a response.

If we performed all of those operations synchronously, the onboarding latency would increase significantly, and failures in downstream systems could directly impact customer onboarding.

Instead, once the Screening Service returns the screening decision, it publishes the normalized event to Kafka.

Kafka becomes the distribution layer, allowing downstream consumers to process the event independently.

This gives us three major benefits:

- Low latency for customer-facing workflows.
- Independent scaling of downstream systems.
- Better resilience because failures in audit or analytics do not block onboarding.

This separation of concerns became the foundation for the rest of the architecture.

---

# Why APIs?

Real-time onboarding requires an immediate compliance decision.

Business users cannot continue onboarding until screening completes.

Because of that, synchronous APIs are the right integration pattern.

Benefits:

- Immediate response.
- Simple request/response model.
- Better user experience.
- Easier error handling.

Tradeoff:

The API path depends on external vendor latency.

To mitigate this we implemented:

- Timeouts
- Retries
- Circuit breakers
- Controlled concurrency

---

# Why Kafka?

Kafka was introduced for a different problem.

Kafka was **not** intended to make screening faster.

The external screening provider remained the bottleneck.

Kafka solved a scalability and decoupling problem.

Instead of forcing the onboarding request to wait for audit storage, Actimize, reconciliation, analytics, and reporting, we publish the screening event once and allow independent consumers to process it asynchronously.

Benefits:

- Decouples producers and consumers.
- Buffers traffic spikes.
- Supports replay.
- Allows independent consumer scaling.
- Prevents downstream failures from impacting onboarding.

Tradeoff:

- Eventual consistency.
- Additional operational complexity.
- Consumers must be idempotent.

---

# Why Support Kafka-Based Ingestion?

As Wealth Management workloads increased, scheduled batch processing became increasingly difficult to scale.

Instead of processing large periodic batches, we introduced Kafka-based ingestion.

This allowed producers to publish continuously while consumers processed requests at a controlled rate.

The same Screening Service and Decision Engine were reused.

Only the ingestion mechanism changed.

Benefits:

- Continuous processing.
- Better throughput.
- Traffic smoothing.
- Independent scaling.
- Reduced batch windows.

---

# Why Canonical Request and Response Models?

External screening vendors expose different APIs and response formats.

We wanted upstream systems to remain completely independent of vendor-specific schemas.

So we introduced canonical request and response contracts.

The Screening Service is responsible for translating between the canonical model and the vendor-specific model.

Benefits:

- Vendor abstraction.
- Easier migration.
- Support multiple providers.
- No upstream changes during vendor replacement.

Tradeoff:

Additional transformation layer.

---

# Why Stateless Services?

The Screening Service stores no session state.

Every request contains all information needed for processing.

Benefits:

- Horizontal scaling.
- Easier deployments.
- Better fault tolerance.
- Simpler load balancing.

---

# Why Separate API and Consumer Deployments?

We maintained a single implementation of the business logic.

Both deployments share:

- Screening Orchestrator
- Decision Engine
- Vendor Adapters
- Response Normalization

The difference is only the entry point.

API Deployment

- REST Controllers enabled
- Kafka listeners disabled

Consumer Deployment

- Kafka listeners enabled
- REST Controllers disabled

This allows each workload to scale independently while avoiding duplicate business logic.

---

# Architectural Principles

Every design decision followed one of these principles.

| Principle | Architecture Decision |
|------------|-----------------------|
| Low latency | Synchronous APIs |
| High throughput | Kafka ingestion |
| Decoupling | Kafka distribution |
| Vendor flexibility | Canonical models |
| Scalability | Stateless services |
| Reliability | Circuit breakers, retries |
| Loose coupling | Independent consumers |
| Reusability | Shared orchestration layer |

---

# Tradeoffs

| Decision | Benefit | Cost |
|-----------|---------|------|
| API | Immediate response | Vendor latency |
| Kafka | Decoupling | Eventual consistency |
| Canonical Model | Vendor independence | Transformation layer |
| Stateless Services | Horizontal scaling | Externalized state |
| Separate Deployments | Independent scaling | Operational complexity |

---

# Common Mistakes

❌ Saying "Kafka makes everything faster."

Kafka doesn't make Bridger faster.

Kafka allows the platform to absorb bursts, decouple producers from consumers, and isolate downstream processing.

---

❌ Explaining Kafka internals before explaining why Kafka exists.

Always explain the business reason first.

---

# Senior Engineering Signals

✔ Architecture driven by business requirements.

✔ Separated decisioning from distribution.

✔ Chose APIs and Kafka based on workload characteristics.

✔ Optimized independently for latency and throughput.

✔ Introduced vendor abstraction.

✔ Reused business logic across multiple ingestion patterns.

---

# Likely Audible Follow-up Questions

- Why not make onboarding asynchronous?
- Why Kafka instead of RabbitMQ?
- Why not write directly to the database?
- Why not expose Bridger directly?
- What happens if Kafka is unavailable?
- Why separate deployments?
- What if the vendor becomes the bottleneck?
- How do you replay events?
- How do you prevent duplicate alerts?


# Screening Service

**Priority:** ⭐⭐⭐⭐⭐

**Interview Frequency:** ⭐⭐⭐⭐⭐

**Study Time:** 30-40 minutes

---

# Purpose

The Screening Service is the core of the platform.

This is the service my team designed and owned.

It orchestrates the entire screening workflow while insulating upstream applications from vendor-specific implementations.

Think of it as the brain of the platform.

---

# Responsibilities

The Screening Service is responsible for:

- Validating incoming requests
- Request enrichment
- Transforming canonical requests into vendor-specific formats
- Selecting the appropriate screening provider
- Invoking external screening APIs
- Normalizing vendor responses
- Applying business decision logic
- Publishing screening events to Kafka
- Returning screening decisions

Everything else revolves around this service.

---

# High-Level Architecture

```text
                    API Gateway
                         │
                         ▼
                Screening Service
                         │
         ┌───────────────┼────────────────┐
         ▼               ▼                ▼
   Validation      Vendor Adapter    Decision Engine
         │               │                │
         └───────────────┼────────────────┘
                         ▼
                   Bridger / RDC
                         │
                         ▼
              Response Normalizer
                         │
         ┌───────────────┼────────────────┐
         ▼                                ▼
  Return Response                 Kafka Producer
```

---

# Internal Flow

Step 1

Receive canonical request.

↓

Validate request.

↓

Perform request enrichment if needed.

↓

Transform canonical model into vendor format.

↓

Invoke screening provider.

↓

Receive vendor response.

↓

Normalize vendor response.

↓

Run decision engine.

↓

Return response.

↓

Publish event to Kafka.

---

# Why One Orchestrator?

One important design decision was centralizing orchestration inside the Screening Service.

Instead of allowing upstream systems to call vendors directly, every request flows through the same orchestration layer.

Benefits:

- Single business workflow
- Centralized validation
- Vendor abstraction
- Easier monitoring
- Consistent auditing
- Easier provider migration

---

# Why Canonical Models?

Every vendor exposes different APIs.

Rather than exposing those APIs to upstream applications, we introduced canonical request and response contracts.

Example

Canonical Request

Customer Name

DOB

Address

↓

Vendor Adapter

↓

Bridger Request

or

RDC Request

The same happens on the response side.

Benefits

- Vendor independence
- Easier migration
- Support multiple vendors
- Cleaner business logic

Tradeoff

Additional mapping layer.

---

# Decision Engine

After response normalization, the Screening Service applies internal business rules.

Possible outcomes

- HIT
- REVIEW
- NO HIT

The decision engine is isolated from vendor-specific logic.

This makes business rules consistent across providers.

---

# Vendor Adapter

The Screening Service never calls Bridger directly.

Instead, it communicates through a Vendor Adapter.

Responsibilities

- Build vendor request
- Authentication
- Invoke API
- Handle retries
- Parse response
- Error translation

Benefits

- Loose coupling
- Vendor migration
- Unit testing
- Multiple provider support

---

# Why Stateless?

Every request contains all information required for processing.

The service stores no conversational state.

Benefits

- Horizontal scaling
- Easier deployments
- Better resiliency
- Simpler load balancing

---

# Scaling

Scaling the Screening Service is straightforward.

Because it's stateless:

API Gateway

↓

Load Balancer

↓

Pod 1

Pod 2

Pod 3

Pod 4

Each instance processes requests independently.

---

# Reliability

Since Bridger is outside our control, we protect the Screening Service using:

- Timeouts
- Retries
- Circuit Breakers
- Controlled Concurrency
- Backpressure

These mechanisms prevent downstream failures from cascading through the platform.

---

# Observability

Important metrics include:

Business

- Requests/sec
- Screening volume
- HIT rate
- REVIEW rate

Application

- API latency
- P95 latency
- P99 latency
- Error rate

Infrastructure

- CPU
- Memory
- Thread Pool Utilization
- Connection Pool Utilization

External

- Bridger latency
- Timeout count
- Retry count
- Circuit breaker open rate

Kafka

- Publish failures
- Consumer lag
- Queue depth

---

# Tradeoffs

| Decision | Benefit | Tradeoff |
|----------|----------|----------|
| Stateless | Horizontal scaling | Externalized state |
| Canonical models | Vendor abstraction | Mapping overhead |
| Vendor Adapter | Loose coupling | Extra abstraction |
| Kafka producer | Decoupled downstream | Eventual consistency |

---

# Senior Engineering Signals

✔ Single orchestration layer

✔ Vendor abstraction

✔ Canonical contracts

✔ Stateless services

✔ Separation of concerns

✔ Centralized business rules

✔ Horizontal scaling

✔ Resiliency patterns

---

# Common Mistakes

❌ Explaining Spring Boot annotations.

❌ Talking about REST Controllers.

❌ Explaining HTTP.

Interviewers care about architecture.

Not framework details.

---

# Likely Audible Follow-up Questions

- Why not let upstream systems call Bridger directly?
- How do you choose between RDC and Bridger?
- What if Bridger becomes unavailable?
- What happens if response normalization fails?
- Why canonical models?
- How would you support another vendor?
- Where would you cache?
- Would you make the Decision Engine configurable?
- How do you deploy this service?

# Why Kafka?

**Priority:** ⭐⭐⭐⭐⭐

**Interview Frequency:** ⭐⭐⭐⭐⭐

**Study Time:** 30 minutes

---

# Purpose

Interviewers are rarely asking:

"Explain Kafka."

They're actually asking:

"What engineering problem were you solving?"

This answer should always begin with the problem—not the technology.

---

# 30-Second Interview Answer

Kafka wasn't introduced to make screening faster.

The external screening provider remained the primary bottleneck.

We introduced Kafka to separate low-latency decisioning from downstream processing.

The onboarding application only needs the screening decision immediately.

Audit persistence, Actimize alert generation, reconciliation, analytics, and reporting can all happen asynchronously.

Kafka allowed us to decouple those workloads, absorb traffic bursts, replay events when necessary, and scale downstream consumers independently without increasing customer-facing latency.

STOP.

---

# 2-Minute Answer

Initially, every request followed a synchronous request-response model.

That worked well for onboarding because the business needed an immediate screening decision.

However, after a decision was returned, several independent systems also needed the screening result.

For example:

- Audit Storage
- Actimize
- Analytics
- Reporting
- Reconciliation

Those systems had very different performance characteristics.

If we executed all of those synchronously, every downstream dependency would increase API latency.

Instead, once the Screening Service produced the decision, we published a normalized event to Kafka.

Kafka became our distribution layer.

Each downstream system consumed the event independently.

This architecture improved scalability, resilience, and operational isolation.

---

# What Problem Did Kafka Solve?

NOT

More throughput.

NOT

Faster vendor calls.

Instead

It solved four architectural problems.

---

## Problem 1

Downstream systems shouldn't increase onboarding latency.

Solution

Return the screening decision first.

Publish the event afterwards.

---

## Problem 2

Every downstream consumer has different scaling requirements.

Audit

↓

Actimize

↓

Analytics

↓

Reporting

Instead of tightly coupling them together, Kafka lets each consumer scale independently.

---

## Problem 3

Traffic arrives in bursts.

Without Kafka

Producer

↓

Screening Service

↓

Vendor

↓

Audit

↓

Actimize

Everything must complete before the producer finishes.

With Kafka

Producer

↓

Kafka

↓

Consumer

↓

Vendor

↓

Audit

↓

Actimize

Kafka absorbs bursts and smooths traffic.

---

## Problem 4

Need replay.

Suppose

Actimize

↓

Down

We don't want to lose screening events.

Kafka retains events.

Consumers replay them after recovery.

---

# Why Not RabbitMQ?

RabbitMQ is excellent for traditional work queues.

Our use case looked more like an event distribution platform.

We had multiple independent downstream consumers processing the same screening result.

Kafka provides:

- Durable event storage
- Replay
- Independent consumer groups
- High throughput
- Long retention

Those characteristics aligned better with our requirements.

---

# Why Not Database Polling?

A database would become tightly coupled to downstream systems.

Polling also introduces:

- Higher database load
- Increased latency
- Poor scalability
- Difficult replay

Kafka naturally supports event-driven architectures.

---

# Why Not Make Everything Asynchronous?

Because onboarding is latency sensitive.

Business users cannot continue until screening completes.

Returning immediately would violate business requirements.

Therefore

Decisioning

↓

Synchronous

Distribution

↓

Asynchronous

---

# Architecture

                 API

                  │

                  ▼

          Screening Service

                  │

      Return Screening Decision

                  │

                  ▼

         Publish to Kafka

                  │

      ┌───────────┼─────────────┐

      ▼           ▼             ▼

   Audit     Actimize     Analytics

---

# Tradeoffs

Benefits

- Decoupling
- Replay
- Independent scaling
- Buffering
- Durability
- Event streaming

Tradeoffs

- Eventual consistency
- Operational complexity
- Consumer management
- Idempotency
- Monitoring consumer lag

---

# Distributed Systems Concepts

Kafka naturally introduced several distributed systems concepts.

These are topics interviewers often explore.

- At-least-once delivery
- Idempotent consumers
- Event replay
- Consumer groups
- Partitioning
- Ordering guarantees
- Backpressure

Don't explain these unless asked.

---

# Common Mistakes

❌ Kafka makes Bridger faster.

False.

Bridger remains the bottleneck.

---

❌ Kafka increases vendor throughput.

False.

Kafka buffers demand.

It doesn't increase external provider capacity.

---

❌ Kafka improves latency.

Partially true.

Kafka improves perceived latency by removing downstream work from the critical path.

It doesn't reduce vendor response time.

---

# Senior Engineering Signals

✔ Started with the business problem.

✔ Distinguished latency from throughput.

✔ Explained decoupling.

✔ Explained workload isolation.

✔ Mentioned replay.

✔ Mentioned independent scaling.

✔ Mentioned tradeoffs.

---

# Likely Audible Follow-ups

- Why partition by Client ID?
- Why not SQS?
- Why not RabbitMQ?
- What if Kafka goes down?
- How do you replay events?
- How do you avoid duplicate events?
- What delivery guarantee did you use?
- How many partitions would you create?
- How do you monitor Kafka?


# 06-How-Did-You-Scale-the-System.md

**Priority:** ⭐⭐⭐⭐⭐

**Interview Frequency:** ⭐⭐⭐⭐⭐

**Study Time:** 30 minutes

---

# Purpose

This is one of the most common architecture follow-up questions.

Examples:

- How did you scale the platform?
- Suppose traffic doubles tomorrow.
- What became your bottleneck?
- How did Kafka help?
- How did you support both onboarding and batch workloads?

Interviewers want to understand your scaling strategy, not just hear "we added more servers."

---

# 2-Minute Interview Answer

When we thought about scaling the platform, we looked at it from multiple dimensions rather than assuming every bottleneck could be solved the same way.

First, the Screening Service itself is stateless, so scaling the application layer is straightforward. We deploy multiple instances behind a load balancer, allowing requests to be distributed evenly across instances.

Second, we recognized that not every workload had the same latency requirements. Customer onboarding requires an immediate screening decision, so those requests continue to use synchronous REST APIs.

However, as our Wealth Management screening workloads grew, scheduled batch processing became increasingly difficult to manage because processing happened in large spikes during fixed execution windows.

To solve this, we introduced Kafka-based ingestion for high-volume workloads. Producers publish requests continuously, and Kafka consumers process them at a controlled rate using the same Screening Service and Decision Engine.

Finally, the external screening provider remained our primary bottleneck. We couldn't simply increase concurrency indefinitely because doing so would overwhelm the vendor.

Instead, we implemented controlled concurrency, rate limiting, retries, circuit breakers, and backpressure so we could maximize throughput while protecting downstream dependencies.

---

# Scaling Strategy

Scaling wasn't a single solution.

We scaled each layer independently.

```

                 Client
                    │
                    ▼
             Load Balancer
                    │
      ┌─────────────┼─────────────┐
      ▼             ▼             ▼
 Screening      Screening      Screening
 Service         Service         Service

                    │

          External Screening

                    │

                 Kafka

      ┌─────────────┼─────────────┐
      ▼             ▼             ▼
   Audit        Actimize      Analytics

```

---

# Layer 1 — API Layer

Problem

More onboarding requests.

Solution

- Stateless services
- Horizontal scaling
- Load balancer

Benefit

Increase API throughput without changing business logic.

---

# Layer 2 — Screening Service

Problem

Growing request volume.

Solution

Multiple Screening Service instances.

Because the service stores no session state, every request can be processed by any instance.

Benefits

- Horizontal scaling
- High availability
- Rolling deployments

---

# Layer 3 — API vs Kafka

This was one of the biggest architectural decisions.

Real-time onboarding

↓

REST API

High-volume workloads

↓

Kafka

Both entry points reuse the same orchestration layer.

Benefits

- Independent scaling
- No duplicate business logic
- Better resource utilization

---

# Layer 4 — Kafka

Kafka solved several scaling problems.

Instead of producers waiting for downstream systems, they only wait until the event is accepted by Kafka.

Benefits

- Traffic buffering
- Burst absorption
- Replay
- Independent consumer scaling

Kafka consumers process requests continuously instead of large scheduled batches.

---

# Layer 5 — Vendor Bottleneck

The external screening provider was ultimately the limiting factor.

Adding more application servers doesn't increase vendor capacity.

Instead, we focused on controlled demand.

Techniques

- Controlled concurrency
- Rate limiting
- Circuit breakers
- Exponential backoff
- Backpressure

Goal

Maximize throughput without overwhelming the provider.

---

# Layer 6 — Consumer Scaling

Different downstream consumers process events independently.

Example

Audit

↓

5 Consumers

Actimize

↓

2 Consumers

Analytics

↓

10 Consumers

Each consumer group scales independently.

---

# Layer 7 — Database

Audit storage and downstream systems scale independently of onboarding.

Because writes occur asynchronously, customer-facing latency remains unaffected.

---

# Independent Scaling

One important design decision was separating API deployments from Kafka consumer deployments.

```

Single Codebase

↓

API Deployment

↓

REST Enabled

Kafka Disabled

--------------------

Consumer Deployment

↓

Kafka Enabled

REST Disabled

```

Both deployments share:

- Screening Orchestrator
- Decision Engine
- Vendor Adapters
- Response Normalization

Only the entry point changes.

Benefits

- One implementation
- Independent scaling
- Easier maintenance

---

# Traffic Spike Example

Suppose traffic increases from 300 requests/sec to 900 requests/sec.

Application Layer

Increase Screening Service instances.

Kafka

Increase consumer instances.

Vendor

Keep concurrency limits unchanged.

Audit

Scale consumers independently.

This prevents one bottleneck from impacting the rest of the system.

---

# Metrics

Business

- Requests/sec
- Screenings/day
- Average onboarding latency

Application

- P95 latency
- P99 latency
- Error rate
- Thread pool utilization

Kafka

- Consumer lag
- Publish failures
- Queue depth

Vendor

- Average response time
- Timeout rate
- Retry count
- Circuit breaker open percentage

Infrastructure

- CPU
- Memory
- Connection pool utilization

---

# Tradeoffs

| Decision | Benefit | Tradeoff |
|----------|----------|----------|
| Stateless Services | Easy scaling | Externalized state |
| Kafka Ingestion | High throughput | Eventual consistency |
| Separate Deployments | Independent scaling | Operational complexity |
| Controlled Concurrency | Protect vendor | Throughput limited by vendor |
| Shared Codebase | No duplication | Configuration complexity |

---

# Senior Engineering Signals

✔ Identified bottlenecks independently.

✔ Didn't assume more servers solve every problem.

✔ Distinguished latency from throughput.

✔ Protected external dependencies.

✔ Reused business logic.

✔ Supported multiple ingestion patterns.

✔ Discussed scaling at every layer.

---

# Common Mistakes

❌ "Kafka made the vendor faster."

False.

Kafka improved demand management.

---

❌ "We scaled by adding servers."

Too simplistic.

Good scaling identifies each bottleneck separately.

---

❌ "Everything scaled equally."

False.

Each layer has different scaling characteristics.

---

# Likely Audible Follow-up Questions

- What was your biggest bottleneck?
- How did you determine the vendor concurrency limit?
- Why not increase Kafka partitions indefinitely?
- How do you prevent hot partitions?
- How does backpressure work?
- What happens if consumers can't keep up?
- How would you scale to 10× traffic?
- When would you add caching?


# How Does Your System Handle Failures?

**Priority:** ⭐⭐⭐⭐⭐

**Interview Frequency:** ⭐⭐⭐⭐⭐

**Study Time:** 30 minutes

---

# Purpose

After understanding the architecture, interviewers almost always ask:

- What happens if Bridger is down?
- What if Kafka goes down?
- What if downstream systems are slow?
- How do you avoid cascading failures?
- How do you make the platform resilient?

This chapter explains the resilience strategy of the platform.

Remember:

A production system isn't judged by how it behaves when everything works.

It's judged by how it behaves when things fail.

---

# 2-Minute Interview Answer

The biggest reliability challenge was that our platform depended on external screening providers such as Bridger.

Since we don't control those systems, we designed the platform assuming they would occasionally become slow or unavailable.

We protected the platform using multiple layers of resilience rather than relying on a single mechanism.

At the application layer we configured request timeouts so requests wouldn't wait indefinitely.

For transient failures we used retries with exponential backoff.

To prevent retry storms and cascading failures we wrapped vendor calls with circuit breakers.

We also implemented controlled concurrency and backpressure so we never overwhelmed downstream providers.

Finally, because downstream processing occurs asynchronously through Kafka, temporary failures in audit or analytics never impact customer onboarding.

---

# Failure Strategy

Think about failures one layer at a time.

```

             Client

               │

               ▼

        API Gateway

               │

               ▼

      Screening Service

               │

               ▼

     External Provider

               │

               ▼

            Kafka

               │

               ▼

     Downstream Consumers

```

Each layer can fail independently.

Each layer has a different recovery strategy.

---

# Scenario 1

## Bridger Becomes Slow

Problem

Response latency increases.

Risk

Application threads become blocked.

Customer latency increases.

Possible timeout.

Solution

- Request timeout
- Retry with exponential backoff
- Circuit breaker
- Controlled concurrency

Tradeoff

Some requests fail faster instead of waiting indefinitely.

---

# Scenario 2

## Bridger Becomes Unavailable

Problem

External dependency is down.

Solution

Circuit breaker opens.

New requests fail immediately instead of waiting for repeated timeouts.

Benefits

- Protect application threads.
- Faster recovery.
- Prevent cascading failures.

---

# Scenario 3

## Kafka Unavailable

Problem

Unable to publish events.

Business impact

Customer already received screening decision.

Audit event cannot be distributed.

Possible approaches

- Retry publishing
- Persist locally for replay
- Alert operations
- Fail request only if compliance requires guaranteed audit persistence

Discuss the tradeoff explicitly.

---

# Scenario 4

## Consumer Failure

Problem

Actimize consumer crashes.

Solution

Kafka retains events.

Consumer restarts.

Resumes processing from committed offsets.

No producer changes required.

---

# Scenario 5

## Retry Storm

Problem

External vendor slows down.

Every request retries immediately.

Result

Traffic multiplies.

Vendor becomes overloaded.

Solution

- Exponential backoff
- Retry limits
- Jitter
- Circuit breaker

---

# Scenario 6

## Traffic Spike

Problem

Sudden increase in requests.

Solution

Kafka buffers requests.

Consumers process at a controlled rate.

Backpressure prevents overwhelming the screening provider.

---

# Scenario 7

## Duplicate Messages

Kafka guarantees at-least-once delivery.

Consumers must therefore be idempotent.

For downstream alert generation we use business keys before creating Actimize alerts to avoid duplicate alerts.

---

# Monitoring During Failures

Metrics

Application

- Error rate
- P95 latency
- P99 latency

Vendor

- Response time
- Timeout rate
- Retry count

Kafka

- Consumer lag
- Publish failures
- DLQ size (if used)

Infrastructure

- CPU
- Memory
- Thread pools

---

# Reliability Patterns

| Pattern | Why |
|----------|-----|
| Timeout | Prevent blocked threads |
| Retry | Recover transient failures |
| Exponential Backoff | Avoid retry storms |
| Circuit Breaker | Prevent cascading failures |
| Controlled Concurrency | Protect vendor |
| Backpressure | Smooth traffic |
| Kafka Replay | Recover consumers |
| Idempotency | Prevent duplicate alerts |

---

# Tradeoffs

Retries

✔ Better reliability

✖ Increased latency

---

Circuit Breakers

✔ Protect application

✖ Some requests fail immediately

---

Kafka

✔ Replay

✔ Durability

✖ Eventual consistency

---

# Senior Engineering Signals

✔ Assumes failures will happen.

✔ Protects downstream systems.

✔ Prevents cascading failures.

✔ Discusses recovery.

✔ Understands resilience patterns.

✔ Balances reliability with latency.

---

# Common Mistakes

❌ "Retries solve everything."

Retries can make outages worse.

---

❌ "Circuit breakers prevent failures."

They don't.

They prevent failures from spreading.

---

❌ "Kafka guarantees exactly once."

Not by default.

The application still needs idempotent consumers.

---

# Likely Audible Follow-up Questions

- How long are your timeouts?
- How many retries?
- Why exponential backoff?
- Why jitter?
- What opens the circuit breaker?
- How do you recover after recovery?
- What happens if Kafka is down for hours?
- How do you replay events?
- How do you avoid duplicate alerts?


# 08-Tell-Me-About-a-Production-Incident.md

**Priority:** ⭐⭐⭐⭐⭐

**Interview Frequency:** ⭐⭐⭐⭐⭐

**Study Time:** 30-40 minutes

---

# Purpose

Almost every senior backend interview eventually asks some variation of:

- Tell me about a production incident.
- Describe a critical outage.
- Tell me about a Sev-1 issue.
- Walk me through your debugging process.
- Describe the hardest production problem you've solved.

The interviewer isn't testing debugging.

They're testing engineering maturity.

---

# The Story

One production incident involved increased latency from our external screening provider.

Because every onboarding request depended on this provider, the slowdown immediately affected customer onboarding latency.

Fortunately, the platform was designed to isolate failures, so the impact was limited and we were able to recover without causing a complete outage.

---

# Step 1 — Detection

We first noticed the issue through production monitoring.

The alerts showed:

- Increased API latency
- Higher timeout rate
- Rising retry count
- Increased error percentage

At the same time, business teams reported slower onboarding.

---

# Step 2 — Initial Investigation

Our first goal was to determine whether the problem was inside our application or an external dependency.

We checked:

Application Metrics

- CPU
- Memory
- GC
- Thread pools
- Database connections

Everything looked healthy.

That suggested our infrastructure wasn't the bottleneck.

---

# Step 3 — External Dependency

Next we examined vendor-specific dashboards.

The metrics showed:

- Vendor response time increasing significantly
- Retry count increasing
- Circuit breaker opening more frequently

At this point we concluded the slowdown originated from the external screening provider.

---

# Step 4 — Mitigation

Because the platform already had resilience mechanisms, we focused on limiting customer impact.

Our mitigation included:

- Request timeouts
- Controlled retries
- Circuit breakers
- Controlled concurrency

These prevented application threads from becoming exhausted while still allowing transient failures to recover automatically.

Meanwhile Kafka continued processing downstream work independently, so audit and analytics remained decoupled from onboarding.

---

# Step 5 — Resolution

Once the vendor stabilized, the circuit breakers gradually closed and request latency returned to normal.

Because downstream processing was asynchronous, there was no need for large-scale recovery or replay.

The platform returned to normal operation without requiring application changes.

---

# Timeline

09:15

Monitoring alerts triggered.

↓

09:18

Business users reported slower onboarding.

↓

09:20

Checked application health.

↓

09:25

Verified infrastructure healthy.

↓

09:30

Identified external vendor latency.

↓

09:35

Circuit breakers opened.

↓

09:50

Vendor recovered.

↓

10:00

Latency returned to normal.

---

# Metrics

The exact values will vary, but this is the type of information interviewers want.

Before

P95 Latency

≈ 200 ms

Vendor Response

≈ 150 ms

Retry Rate

<1%

Error Rate

<0.5%

Incident

Vendor Response

≈ 2-4 seconds

Retry Rate

10-20%

Circuit Breaker Opens

Increasing

API Latency

Several seconds

Recovery

Metrics gradually returned to baseline.

---

# How We Knew It Wasn't Our System

Application

✅ Healthy

CPU

Normal

Memory

Normal

GC

Normal

Database

Normal

Thread Pools

Healthy

Only vendor latency increased.

This helped isolate the problem quickly.

---

# What Went Well

The architecture behaved exactly as designed.

- Circuit breakers prevented cascading failures.
- Retries handled transient errors.
- Controlled concurrency protected the vendor.
- Kafka isolated downstream processing.
- Monitoring detected the issue quickly.

---

# What Could Be Improved

Every incident should end with improvements.

Examples:

- Better alert thresholds.
- More detailed vendor dashboards.
- Faster anomaly detection.
- Additional synthetic monitoring.
- Improved runbooks.

Interviewers love hearing continuous improvement.

---

# Lessons Learned

The biggest lesson was that external dependencies should always be treated as unreliable.

Rather than assuming downstream services are always available, production systems should be designed to fail gracefully using timeouts, retries, circuit breakers, monitoring, and operational visibility.

---

# Senior Engineering Signals

✔ Used metrics before making assumptions.

✔ Eliminated possible causes systematically.

✔ Distinguished internal vs external failures.

✔ Used observability.

✔ Discussed resilience patterns.

✔ Focused on customer impact.

✔ Ended with lessons learned.

---

# Common Mistakes

❌ "The vendor was slow."

Too shallow.

Explain how you proved it.

---

❌ Jump straight to the solution.

Always explain the investigation first.

---

❌ Forget metrics.

Senior engineers use data.

---

❌ Forget customer impact.

Architecture exists to support the business.

---

# Likely Audible Follow-up Questions

- What dashboards did you check first?
- How did you know it wasn't your code?
- What metrics alerted you?
- Would you retry every request?
- How would you avoid retry storms?
- What would happen if the vendor stayed down for hours?
- Would you fail open or fail closed?
- How would you improve this architecture?

# Hardest Distributed Systems Problem

**Priority:** ⭐⭐⭐⭐⭐

**Interview Frequency:** ⭐⭐⭐⭐⭐

**Study Time:** 30 minutes

---

# Purpose

Common interview questions:

- What was the hardest distributed systems problem you've solved?
- Tell me about a challenging consistency problem.
- How did you handle duplicate events?
- How do you guarantee correctness in an event-driven system?
- What's the hardest reliability problem you've encountered?

The interviewer is NOT looking for buzzwords.

They're evaluating whether you understand the challenges that naturally arise in distributed systems.

---

# 2-Minute Interview Answer

One of the more interesting distributed systems challenges wasn't making the external screening call itself—it was ensuring correctness across multiple asynchronous downstream systems.

Once a screening decision is made, that result is published to Kafka and consumed independently by systems such as Actimize, audit storage, reconciliation, and analytics.

Because Kafka provides at-least-once delivery, consumers must be prepared to receive duplicate messages.

For screening itself, duplicate processing is generally acceptable because screening is a read-oriented operation.

However, duplicate downstream alerts are not acceptable.

To solve that, we designed downstream consumers to be idempotent by using business keys before creating alerts. If an event was replayed or redelivered, the consumer recognized that the alert had already been processed and skipped creating a duplicate.

That allowed us to safely retry, recover consumers, and replay historical events without generating duplicate compliance alerts.

---

# Why Was This a Distributed Systems Problem?

Several independent services consume the same event.

Each service has its own:

- Processing speed
- Failure modes
- Retry strategy
- Deployment schedule

There is no global transaction across all consumers.

Instead, every service progresses independently.

That means the architecture must tolerate:

- Duplicate delivery
- Delayed delivery
- Consumer failures
- Replay
- Temporary inconsistency

---

# Problem 1 — Duplicate Messages

Kafka provides at-least-once delivery.

This means a consumer may receive the same event more than once.

Example:

Screening Event

↓

Consumer processes

↓

Crash before offset commit

↓

Kafka redelivers

Without protection:

❌ Duplicate alert

❌ Duplicate notification

Solution:

Idempotent consumers.

---

# Problem 2 — Consumer Recovery

Suppose the Actimize consumer is unavailable for 30 minutes.

Producer

↓

Kafka

↓

Actimize Consumer ❌

Events remain safely stored in Kafka.

When the consumer recovers, it resumes processing from its last committed offset.

No producer changes are required.

---

# Problem 3 — Replay

Sometimes downstream systems need historical events.

Examples:

- Bug fixes
- New consumer
- Data recovery
- Backfill

Kafka retention allows consumers to replay events safely.

Replay is only practical if consumers are idempotent.

---

# Problem 4 — Ordering

Ordering matters for screening events belonging to the same client.

For example:

Client Updated

↓

Client Rescreened

↓

Alert Generated

Processing these events out of order can lead to inconsistent results.

To preserve ordering, we partition Kafka topics by Client ID so all events for the same client are processed sequentially within a partition while still allowing parallelism across different clients.

---

# Problem 5 — Eventual Consistency

The onboarding application receives its screening decision immediately.

However:

Audit

↓

Analytics

↓

Reporting

↓

Actimize

may complete later.

This is an intentional design decision.

Business correctness depends only on the screening decision.

Downstream systems can become consistent shortly afterwards.

---

# Why We Didn't Use Distributed Transactions

Using a distributed transaction across:

- Screening
- Kafka
- Audit
- Actimize
- Analytics

would significantly increase complexity and reduce availability.

Instead, we accepted eventual consistency for downstream processing while keeping the customer-facing decision synchronous.

This was a conscious architectural tradeoff.

---

# Distributed Systems Concepts Demonstrated

This architecture naturally uses:

✓ At-least-once delivery

✓ Idempotent consumers

✓ Event replay

✓ Eventual consistency

✓ Independent failure domains

✓ Partition-based ordering

✓ Loose coupling

These are all real distributed systems concepts applied to a production system.

---

# Tradeoffs

| Decision | Benefit | Tradeoff |
|-----------|----------|----------|
| At-least-once delivery | Reliability | Duplicate messages |
| Idempotent consumers | Safe retries | Additional logic |
| Replay | Recovery | Consumers must tolerate duplicates |
| Eventual consistency | Better scalability | Temporary inconsistency |
| Partition by Client ID | Ordering | Risk of hot partitions |

---

# Common Mistakes

❌ Saying "Kafka guarantees exactly-once."

Exactly-once semantics are more nuanced and require support from producers, brokers, and consumers. Our application still relied on idempotent consumers for correctness.

---

❌ Saying "duplicates never happen."

Duplicates are expected in distributed systems.

The application should handle them safely.

---

❌ Confusing replay with retry.

Retries handle transient failures.

Replay reprocesses historical events.

---

# Senior Engineering Signals

✔ Understands delivery guarantees.

✔ Understands idempotency.

✔ Understands replay.

✔ Understands eventual consistency.

✔ Explains tradeoffs instead of chasing perfect consistency.

✔ Designs for failure rather than assuming success.

---

# Likely Audible Follow-up Questions

- Why at-least-once instead of exactly-once?
- How do you implement idempotency?
- What business key would you use?
- Why partition by Client ID?
- What happens if one partition becomes hot?
- How do consumers recover?
- What happens during consumer rebalance?
- How would you replay one day's worth of events?

# Observability and Monitoring

**Priority:** ⭐⭐⭐⭐⭐

**Interview Frequency:** ⭐⭐⭐⭐⭐

**Study Time:** 20-25 minutes

---

# Purpose

Almost every senior backend interview eventually asks questions like:

- How did you monitor the system?
- How did you detect production issues?
- What metrics mattered?
- How did you know the vendor was slow?
- What dashboards did you use?
- What wakes you up at 2 AM?

Interviewers are evaluating whether you can operate a production system—not just build one.

---

# 2-Minute Interview Answer

Observability was a key part of our production architecture because our platform depended on external screening providers.

Whenever an issue occurred, our goal was to quickly determine whether the problem was within our own platform or an external dependency.

To accomplish that, we monitored the platform across four dimensions:

- Business Metrics
- Application Metrics
- Infrastructure Metrics
- External Dependency Metrics

This allowed us to isolate failures quickly and reduce Mean Time to Detection (MTTD) and Mean Time to Recovery (MTTR).

---

# Monitoring Stack

We primarily used:

- Prometheus
- Grafana
- AppDynamics
- Application Logs

Each tool served a different purpose.

Prometheus collected metrics.

Grafana visualized dashboards.

AppDynamics provided application performance monitoring and transaction tracing.

Application logs were used for detailed troubleshooting.

---

# 1. Business Metrics

Business metrics answer:

"Is the platform delivering business value?"

Examples

- Requests per second
- Screenings per day
- HIT rate
- REVIEW rate
- NO HIT rate
- Average onboarding latency
- Vendor distribution (Bridger vs RDC)

These metrics are useful for capacity planning and identifying unusual traffic patterns.

---

# 2. Application Metrics

These metrics indicate application health.

Examples

- Request Count
- Success Rate
- Error Rate
- Average Response Time
- P95 Latency
- P99 Latency
- Timeout Rate
- Retry Count
- Circuit Breaker Opens

If P99 latency suddenly increased while CPU remained low, that often indicated an external dependency rather than an application bottleneck.

---

# 3. Infrastructure Metrics

Infrastructure metrics ensure the application itself remains healthy.

Examples

- CPU Utilization
- Memory Usage
- JVM Heap
- Garbage Collection
- Thread Pool Utilization
- Connection Pool Usage
- Disk Utilization
- Network I/O

These help determine whether problems originate from the application or infrastructure.

---

# 4. Kafka Metrics

Kafka introduces additional operational metrics.

Examples

Producer

- Publish Success Rate
- Publish Latency

Consumer

- Consumer Lag
- Consumption Rate
- Processing Time

Cluster

- Partition Health
- Broker Availability

Consumer lag was one of our most important operational indicators because it showed whether downstream consumers were keeping up with incoming traffic.

---

# 5. External Dependency Metrics

Since Bridger was outside our control, we monitored it separately.

Important metrics included:

- Vendor Response Time
- Vendor Timeout Rate
- Vendor Error Rate
- Retry Count
- Circuit Breaker Status

If vendor latency increased while our infrastructure remained healthy, we could quickly isolate the issue to the external provider.

---

# Logging Strategy

Every request included a correlation ID.

This allowed us to trace the request across:

Client

↓

API Gateway

↓

Screening Service

↓

Vendor Adapter

↓

Kafka

↓

Downstream Consumers

Structured logging made debugging significantly easier.

---

# Alerting Strategy

Alerts were configured for situations such as:

Application

- High error rate
- High P99 latency
- JVM memory pressure

Kafka

- High consumer lag
- Publish failures

Vendor

- High timeout rate
- Increased response time
- Circuit breaker opens

Infrastructure

- CPU saturation
- Thread pool exhaustion

Alerts were designed to identify customer-impacting issues before business users reported them.

---

# Dashboards

Typical dashboard sections included:

Business

- Screening volume
- Request rate
- Decision distribution

Application

- Response time
- Error percentage
- Retry count

Vendor

- Response time
- Timeouts

Kafka

- Consumer lag
- Topic throughput

Infrastructure

- CPU
- Memory
- Thread pools

---

# Production Investigation Example

Suppose API latency suddenly increases.

Step 1

Check P95 and P99 latency.

↓

Step 2

Check CPU, Memory, JVM.

↓

Healthy?

↓

Yes

↓

Check Vendor Response Time.

↓

Vendor Latency High?

↓

Yes

↓

Problem isolated.

This systematic approach prevents guessing.

---

# Tradeoffs

| Monitoring | Benefit | Cost |
|------------|----------|------|
| Detailed Metrics | Faster debugging | More storage |
| Distributed Tracing | End-to-end visibility | Performance overhead |
| Structured Logs | Easier debugging | Log volume |
| Aggressive Alerting | Faster detection | Alert fatigue |

---

# Senior Engineering Signals

✔ Thinks in metrics.

✔ Uses dashboards before logs.

✔ Separates business metrics from infrastructure metrics.

✔ Understands customer impact.

✔ Uses observability to drive decisions.

---

# Common Mistakes

❌ "We monitored Grafana."

Grafana is a dashboard.

The important question is:

What metrics did you monitor?

---

❌ Looking at logs first.

Metrics identify the problem.

Logs explain it.

---

❌ Alerting on everything.

Too many alerts create noise.

Focus on customer-impacting metrics.

---

# Likely Audible Follow-up Questions

- Which metric do you check first?
- What wakes you up at 2 AM?
- How do you know it's not your code?
- How do you monitor Kafka?
- What metrics indicate backpressure?
- How do you detect retry storms?
- Do you use tracing?
- How would you reduce alert fatigue?

# Audible Architecture Mock Interview

---

# Round 1 — Introduction

Q1

Tell me about the largest production system you've built.

(Expected: Largest Production System.md)

---

Q2

Walk me through one request.

(Expected: Walk Me Through One Request.md)

---

Q3

Why did you choose this architecture?

(Expected: Why Architecture.md)

---

# Round 2 — Architecture

Q4

Why synchronous APIs?

Q5

Why Kafka?

Q6

Why not RabbitMQ?

Q7

Why canonical request models?

Q8

Why canonical response models?

Q9

Why vendor abstraction?

Q10

Why stateless services?

Q11

Why separate deployments?

Q12

What would you cache?

Q13

Would you use Redis?

Q14

Where would you put a database?

Q15

Would you redesign anything today?

---

# Round 3 — Scaling

Q16

How did you scale?

Q17

What was your biggest bottleneck?

Q18

Suppose traffic doubles tomorrow.

Q19

Suppose traffic becomes 10×.

Q20

Why not increase Kafka partitions forever?

Q21

How do you avoid hot partitions?

Q22

How many consumers?

Q23

How many partitions?

Q24

How do you autoscale?

---

# Round 4 — Failures

Q25

What if Bridger is slow?

Expected Answer

Timeout → Retry → Circuit Breaker → Controlled Concurrency → Backpressure.

Q26

What if Bridger becomes slow?

Q27

What if Kafka is unavailable?

Q28

What if Actimize fails?

Q29

How do you recover consumers?

Q30

How do you replay?

Q31

How do you avoid retry storms?

Q32

How do you avoid cascading failures?

Q33

What if Kafka contains duplicates?

Q34

How do you prevent duplicate alerts?

---

# Round 5 — Distributed Systems

Q35

What delivery guarantee?

Q36

Why at-least-once?

Expected Answer

Because reliability was more important than avoiding duplicate delivery. We handled duplicates using idempotent consumers.

Q37

Why not exactly-once?

Q38

How do you implement idempotency?

Q39

Why eventual consistency?

Q40

How do you preserve ordering?

Q41

What happens during consumer rebalance?

Q42

How do you replay one day's events?

Q43

Why not distributed transactions?

Q44

What consistency guarantees do you provide?

---

# Round 6 — Operations

Q45

What metrics matter?

Q46

How do you monitor Kafka?

Q47

How do you detect failures?

Q48

What dashboards do you use?

Q49

Tell me about a production incident.

Q50

What did you learn?

---

# Round 7 — Design

Q51

Redesign this for 100 million events/day.

Q52

How would AWS change your design?

Q53

Would you choose SQS today?

Q54

Would you use EventBridge?

Q55

Would you use DynamoDB?

Q56

Would you use Kubernetes?

Q57

How would you make it multi-region?

Q58

How would you make it active-active?

Q59

What would you improve today?

Q60

If you had six more months, what would you build?

# 15-Numbers-and-Metrics.md

**Priority:** ⭐⭐⭐⭐⭐

**Interview Frequency:** ⭐⭐⭐⭐⭐

**Study Time:** 15 minutes

---

# Purpose

One of the biggest differences between good engineers and senior engineers is that senior engineers naturally quantify their systems.

Instead of saying:

❌ "The system scaled well."

They say:

✅ "The platform processed over 10 million client records, maintained a 200–500 ms response SLA for synchronous onboarding, and reduced processing time by around 40% after introducing Kafka-based event-driven processing."

Numbers make your architecture believable.

---

# Business Scale

Mention these naturally during the conversation.

- Supports institutional client onboarding.
- Screens against sanctions, PEP, and adverse media datasets.
- Processes **10M+ client records** for high-volume Wealth Management workflows.
- Improved processing time by **~40%** after moving workloads from scheduled batch processing to Kafka-based ingestion.

---

# API Metrics

These are the metrics that matter for customer-facing workflows.

Examples:

- API Response Time
- Average Latency
- P95 Latency
- P99 Latency
- Error Rate
- Request Rate (Requests/sec)
- Success Rate

Interview Example

> For onboarding, our focus was keeping API latency low. We monitored average response time along with P95 and P99 latency because tail latency has the biggest impact on user experience.

---

# Vendor Metrics

The external screening provider was our primary dependency.

We monitored:

- Vendor Response Time
- Timeout Rate
- Retry Count
- Error Rate
- Circuit Breaker Open Count

Interview Example

> When API latency increased, one of the first things we checked was vendor response time. If our infrastructure remained healthy but vendor latency increased, we knew the bottleneck was external.

---

# Kafka Metrics

Kafka introduced its own operational metrics.

Producer

- Publish Success Rate
- Publish Latency

Consumer

- Consumer Lag
- Processing Rate
- Processing Time

Broker

- Partition Health
- Broker Availability

Interview Example

> Consumer lag was one of our most important operational metrics because it immediately showed whether downstream consumers were keeping up with incoming traffic.

---

# Infrastructure Metrics

Examples

- CPU Utilization
- Memory Usage
- JVM Heap
- Garbage Collection
- Thread Pool Utilization
- Connection Pool Usage
- Disk I/O
- Network I/O

Interview Example

> During production incidents, we first checked infrastructure metrics to determine whether the bottleneck was inside our application or an external dependency.

---

# Business Metrics

Examples

- Screenings per Day
- Requests per Second
- HIT Rate
- REVIEW Rate
- NO HIT Rate
- Average Onboarding Time

These metrics measure business health rather than system health.

---

# Reliability Metrics

Examples

- Retry Rate
- Timeout Rate
- Circuit Breaker Opens
- Failed Requests
- Kafka Publish Failures
- Consumer Recovery Time

---

# Useful Numbers to Mention

These are numbers you can naturally mention because they come from your project.

| Metric | Value |
|----------|--------|
| Client Records | **10M+** |
| Processing Improvement | **~40% faster** |
| API SLA | **200–500 ms** |
| Architecture | API + Kafka |
| Service Type | Stateless |
| Scaling | Horizontal |
| Delivery Model | At-least-once |
| Consumer Type | Idempotent |
| Processing | Near real-time |

---

# Numbers That Sound Good (Only If You Can Defend Them)

These are examples of the types of numbers interviewers appreciate.

Don't invent them.

Instead, use the actual values if you know them.

Examples:

- Requests/sec
- Peak TPS
- Kafka Partitions
- Consumer Groups
- Service Instances
- Pods
- Availability
- P95
- P99
- Queue Depth
- Consumer Lag

---

# During Every Answer Ask Yourself

Can I naturally add a number?

Instead of

❌ "Latency was low."

Say

✅ "Our target SLA for onboarding was around **200–500 ms**."

---

Instead of

❌ "The system handled a lot of traffic."

Say

✅ "The Kafka-based architecture supported processing **over 10 million client records** while improving overall processing time by around **40%**."

---

Instead of

❌ "We monitored the application."

Say

✅ "We monitored request rate, P95/P99 latency, vendor response time, retry count, Kafka consumer lag, and thread pool utilization."

---

# Numbers to Avoid

Don't guess.

Avoid statements like:

❌ 500 requests/sec

❌ 30 Kafka partitions

❌ 99.999% uptime

❌ 5TB database

Unless you genuinely know those numbers.

Interviewers may ask:

"How did you arrive at that number?"

Always be able to defend every metric you mention.

---

# Senior Engineering Signals

✔ Quantifies architecture.

✔ Talks in SLAs instead of adjectives.

✔ Uses latency percentiles.

✔ Differentiates business metrics from system metrics.

✔ Discusses operational metrics.

✔ Supports architecture decisions with measurable outcomes.

---

# Common Mistakes

❌ Saying "The system was fast."

Say:

"We targeted a 200–500 ms response time for synchronous onboarding."

---

❌ Saying "Kafka improved performance."

Say:

"Kafka reduced downstream processing on the critical path and improved processing time by around 40% for migrated Wealth Management workloads."

---

❌ Saying "We monitored Grafana."

Grafana is the dashboard.

The important part is **what** you monitored.

---

# Audible Interview Tip

Interviewers don't expect you to remember every number.

They expect you to naturally quantify the important parts of your system.

If you consistently mention:

- 10M+ client records
- 200–500 ms API SLA
- ~40% processing improvement
- P95/P99 latency
- Consumer lag
- Vendor response time
- Retry count

your architecture immediately sounds like a production system rather than a classroom design.