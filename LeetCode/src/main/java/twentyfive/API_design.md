# API DESIGN & MICROSERVICES HANDBOOK

---

# TABLE OF CONTENTS

## PART I - API FUNDAMENTALS

### Page 1

1. Statelessness
2. Stateful vs Stateless APIs
3. Sessions
4. JWT and Stateless Authentication
5. Business Impact of Stateless Design

---

## PART II - AUTHENTICATION & AUTHORIZATION

### Page 2

6. Authentication vs Authorization
7. OAuth 2.0
8. JWT Structure
9. Public & Private Keys
10. OAuth Flow
11. JWT Validation Flow

---

## PART III - API SECURITY

### Page 3

12. HTTPS & TLS
13. Request Validation
14. Bean Validation
15. JSON Schema Validation
16. Input Sanitization
17. Rate Limiting
18. Security Best Practices

---

## PART IV - API VERSIONING

### Page 4

19. Why Versioning Matters
20. Backward Compatibility
21. URI Versioning
22. Deprecation Strategy

---

## PART V - API DOCUMENTATION

### Page 5

23. OpenAPI
24. Swagger
25. Request Examples
26. Response Examples
27. Sandbox Environments

---

## PART VI - OBSERVABILITY

### Page 6

28. Logging
29. Metrics
30. Prometheus
31. Grafana
32. Alerting
33. Traceability

---

## PART VII - ERROR HANDLING

### Page 7

34. HTTP Status Codes
35. Structured Error Responses
36. Trace IDs
37. Error Design Best Practices

---

## PART VIII - PERFORMANCE & SCALABILITY

### Page 8

38. Pagination
39. Cursor vs Offset Pagination
40. Redis Caching
41. Cache Strategies
42. Rate Limiting
43. Horizontal Scaling
44. Asynchronous Processing

---

## PART IX - CONSISTENCY & RESOURCE MODELING

### Page 9

45. REST Principles
46. Resource Naming
47. URI Design
48. Query Parameters
49. Resource Relationships

---

## PART X - IDEMPOTENCY

### Page 10

50. Idempotency Fundamentals
51. HTTP Method Semantics
52. Idempotency Keys
53. Payment Processing Examples

---

## PART XI - EVENT DRIVEN APIS

### Page 11

54. Kafka Event Bus
55. Asynchronous Messaging
56. Real Time Analytics
57. Event Driven Architecture
58. Replayability

---

## PART XII - WEBHOOKS

### Page 12

59. Webhook Fundamentals
60. Registration Flow
61. Delivery Flow
62. Retry Strategies
63. HMAC Signatures
64. Webhook Best Practices

---

## PART XIII - GLOBAL FINANCIAL ANALYTICS API DESIGN

### Page 13

65. Requirements
66. Architecture Overview
67. Authentication Flow
68. API Gateway
69. Redis Layer
70. Distributed Database
71. Security
72. Observability
73. Rate Limiting
74. End To End Request Flow

---

## PART XIV - INTERVIEW DISCUSSIONS

### Page 14

75. Staff Engineer Discussion Questions
76. Architecture Tradeoffs
77. Scalability Discussion
78. Security Discussion
79. Reliability Discussion
80. Executive Narrative

# PART I - API FUNDAMENTALS

# PAGE 1

---

# 1. What Is An API?

## Definition

API stands for:

```text
Application Programming Interface
```

An API is a contract that allows one system to communicate with another system in a standardized way.

---

## Example

```text
Mobile App
      ↓
REST API
      ↓
Backend Service
      ↓
Database
```

The client does not need to know:

* Database structure
* Internal business logic
* Infrastructure details

It only needs to know the API contract.

---

## Real World Example

When a user logs into an application:

```http
POST /login
```

the client sends credentials to the API.

The API:

* Validates credentials
* Generates tokens
* Returns a response

without exposing internal implementation details.

---

# 2. Why Do APIs Exist?

APIs provide:

```text
Abstraction
Standardization
Reusability
Security
Scalability
```

---

## Without APIs

```text
Frontend
      ↓
Direct Database Access
```

Problems:

```text
Security Risks
Tight Coupling
Maintenance Challenges
```

---

## With APIs

```text
Frontend
      ↓
API Layer
      ↓
Database
```

Benefits:

```text
Loose Coupling
Security
Versioning
Independent Development
```

---

# 3. What Is REST?

REST stands for:

```text
Representational State Transfer
```

REST is an architectural style for designing web services.

---

## Core Principles

```text
Client Server Architecture

Statelessness

Cacheability

Uniform Interface

Layered System
```

---

## REST Example

Retrieve user:

```http
GET /users/123
```

Create user:

```http
POST /users
```

Update user:

```http
PUT /users/123
```

Delete user:

```http
DELETE /users/123
```

---

# 4. What Does Stateless Mean?

Stateless means:

```text
Every Request Contains
Everything Required
To Process That Request
```

The server does not store client session information between requests.

---

## Example

Request 1:

```http
GET /accounts
Authorization: Bearer JWT
```

Request 2:

```http
GET /transactions
Authorization: Bearer JWT
```

Each request contains all required authentication information.

---

## Key Characteristic

Server does not remember:

```text
Previous Request

Previous Response

Client Session State
```

---

# 5. Stateful vs Stateless APIs

## Stateful

Server stores session information.

Example:

```text
Login
      ↓
Session Created
      ↓
Session Stored On Server
```

---

### Problems

```text
Memory Consumption

Sticky Sessions

Horizontal Scaling Challenges
```

---

## Stateless

No session storage on server.

Each request contains:

```text
Authentication

Authorization

Context
```

---

### Benefits

```text
Scalable

Simple

Cloud Friendly

Load Balancer Friendly
```

---

# 6. Why Is Statelessness Important?

Stateless services can scale horizontally very easily.

---

## Example

```text
Load Balancer
     ↓
 ┌─────┬─────┬─────┐
 │API1 │API2 │API3 │
 └─────┴─────┴─────┘
```

Any request can be served by any instance.

---

## Result

```text
High Availability

Elastic Scaling

Fault Tolerance
```

---

# 7. Session Based Authentication

Traditional web applications commonly use sessions.

---

## Flow

```text
User Login
      ↓
Server Creates Session
      ↓
Session Stored
      ↓
Session ID Returned
      ↓
Browser Sends Session ID
```

---

## Example

```http
Cookie: SESSIONID=ABC123
```

---

## Drawbacks

```text
Session Replication

Sticky Sessions

Server Memory Usage
```

---

# 8. JWT Based Authentication

JWT stands for:

```text
JSON Web Token
```

JWT enables stateless authentication.

---

## Flow

```text
Login
      ↓
JWT Generated
      ↓
JWT Returned To Client
      ↓
Client Sends JWT
On Every Request
```

---

Example:

```http
Authorization: Bearer eyJhbGci...
```

---

## Benefits

```text
Stateless

Scalable

Distributed Friendly

Cloud Native
```

---

# 9. Business Impact Of Stateless Design

Stateless architecture enables:

```text
Cloud Deployments

Kubernetes

Auto Scaling

High Availability

Global Load Balancing
```

---

## Example

A financial institution processing millions of API calls per day cannot rely on server-side sessions because:

```text
Servers Fail

Containers Restart

Traffic Spikes
```

Stateless APIs allow traffic to be routed to any healthy instance.

---

# 10. Interview Discussion

## Question

Why do modern microservices prefer stateless APIs?

---

## Strong Answer

Stateless APIs eliminate server-side session management and allow requests to be processed by any instance. This simplifies horizontal scaling, improves fault tolerance, supports cloud-native deployments, and reduces operational complexity.

---

## Staff Engineer Discussion

Statelessness is one of the most important architectural decisions in distributed systems because it directly impacts:

```text
Scalability

Availability

Resilience

Operational Simplicity
```

Modern microservice architectures heavily favor stateless designs for these reasons.

---

# Quick Revision Sheet

```text
API
=
Contract Between Systems

REST
=
Architectural Style

Stateless
=
No Server Session Storage

Stateful
=
Server Stores Session

JWT
=
Stateless Authentication

Session
=
Server Side Authentication State

Major Benefit
=
Horizontal Scalability
```

---

# End Of Page 1

## Next Page

### Page 2 – Authentication & Authorization

# PART II - AUTHENTICATION & AUTHORIZATION

# PAGE 2

---

# 11. Authentication vs Authorization

One of the most commonly asked API interview questions.

---

## Authentication

Authentication answers:

```text
Who Are You?
```

Examples:

* Username & Password
* JWT
* OAuth Login
* MFA

---

### Example

User logs in:

```http
POST /login
```

Credentials:

```json
{
  "username":"saurabh",
  "password":"********"
}
```

System verifies identity.

---

## Authorization

Authorization answers:

```text
What Are You Allowed To Do?
```

Examples:

* View Accounts
* Create Transactions
* Admin Access
* Read Only Access

---

### Example

User authenticated successfully.

Attempts:

```http
DELETE /users/123
```

System checks:

```text
Does User Have Admin Rights?
```

---

## Quick Interview Answer

```text
Authentication
=
Identity Verification

Authorization
=
Permission Verification
```

---

# 12. Authentication Flow

Typical API Authentication Flow:

```text
User
  ↓
Login API
  ↓
Credential Validation
  ↓
Token Generated
  ↓
Token Returned
  ↓
Subsequent API Requests
```

---

## Example

Login:

```http
POST /login
```

Response:

```json
{
  "token":"JWT_TOKEN"
}
```

Subsequent Requests:

```http
GET /accounts

Authorization: Bearer JWT_TOKEN
```

---

# 13. What Is OAuth 2.0?

OAuth is:

```text
Authorization Framework
```

Used to grant access without sharing credentials.

---

## Real Example

Login using:

```text
Google
Microsoft
GitHub
LinkedIn
```

---

Instead of:

```text
Sharing Passwords
```

---

## Benefit

Applications never see the user's actual password.

---

# 14. OAuth Actors

OAuth typically involves:

```text
Resource Owner

Client Application

Authorization Server

Resource Server
```

---

## Example

```text
User
    ↓
Google Login
    ↓
Authorization Server
    ↓
Access Token
    ↓
Application
```

---

# 15. OAuth Authorization Code Flow

Most common OAuth flow.

---

## Flow

```text
User
  ↓
Application
  ↓
Authorization Server
  ↓
Login
  ↓
Authorization Code
  ↓
Access Token
  ↓
API Access
```

---

### Why Use Authorization Code?

Most secure flow for web applications.

---

# 16. Access Token vs Refresh Token

## Access Token

Used for API access.

Typically short lived.

Example:

```text
15 Minutes
30 Minutes
1 Hour
```

---

## Refresh Token

Used to obtain new access tokens.

Typically long lived.

Example:

```text
30 Days
60 Days
90 Days
```

---

## Flow

```text
Access Token Expires
       ↓
Refresh Token Used
       ↓
New Access Token Issued
```

---

# 17. What Is JWT?

JWT stands for:

```text
JSON Web Token
```

JWT is a compact token format used for stateless authentication.

---

## Structure

```text
Header.Payload.Signature
```

Example:

```text
xxxxx.yyyyy.zzzzz
```

---

# 18. JWT Header

Contains metadata.

Example:

```json
{
  "alg":"RS256",
  "typ":"JWT"
}
```

---

## Purpose

Defines:

```text
Signing Algorithm

Token Type
```

---

# 19. JWT Payload

Contains claims.

Example:

```json
{
  "sub":"123",
  "username":"saurabh",
  "role":"ADMIN"
}
```

---

## Common Claims

```text
sub
iss
aud
exp
iat
```

---

### Important

Never store sensitive information in JWT payloads.

Payloads are encoded.

Not encrypted.

---

# 20. JWT Signature

Used to verify token integrity.

---

Generated using:

```text
Header
+
Payload
+
Private Key
```

---

Purpose:

```text
Prevent Tampering
```

---

# 21. Public Key vs Private Key

Most enterprise APIs use:

```text
RS256
```

---

## Private Key

Used to:

```text
Sign JWT
```

Must remain secret.

---

## Public Key

Used to:

```text
Validate JWT
```

Can be distributed safely.

---

## Benefit

Services can validate tokens without sharing secrets.

---

# 22. JWT Validation Flow

Incoming Request:

```http
Authorization: Bearer JWT
```

---

Validation Steps:

```text
Verify Signature

Verify Expiration

Verify Issuer

Verify Audience

Extract Claims
```

---

If valid:

```text
Request Continues
```

Otherwise:

```text
401 Unauthorized
```

---

# 23. Common JWT Claims

## Subject

```json
{
 "sub":"12345"
}
```

Represents user identifier.

---

## Issuer

```json
{
 "iss":"auth.company.com"
}
```

---

## Audience

```json
{
 "aud":"api.company.com"
}
```

---

## Expiration

```json
{
 "exp":1234567890
}
```

---

# 24. Common Security Mistakes

## Storing Sensitive Data

Bad:

```json
{
 "password":"secret"
}
```

---

## Long Expiration Times

Bad:

```text
1 Year Token
```

---

## Missing Signature Validation

Critical vulnerability.

---

## Trusting Unsigned Tokens

Never acceptable.

---

# 25. Enterprise Authentication Architecture

Typical Architecture:

```text
User
  ↓
API Gateway
  ↓
Identity Provider
  ↓
JWT Issued
  ↓
Microservices
```

---

Examples:

```text
Okta

Keycloak

Azure AD

Ping Identity
```

---

# 26. Authentication In Microservices

Question:

Should every service validate JWT?

---

Answer:

Usually yes.

---

Benefits:

```text
Independent Services

Decentralized Security

Reduced Coupling
```

---

# 27. Staff Engineer Discussion

## Why JWT Instead Of Sessions?

JWT provides:

```text
Statelessness

Scalability

Cloud Native Architecture

Cross Service Authentication
```

---

## Why RS256 Instead Of HS256?

RS256 provides:

```text
Public Key Validation

Better Key Management

Microservice Friendly Security
```

---

# Quick Revision Sheet

```text
Authentication
=
Who Are You

Authorization
=
What Can You Do

OAuth
=
Authorization Framework

JWT
=
Stateless Authentication

Access Token
=
Short Lived

Refresh Token
=
Long Lived

Header.Payload.Signature

RS256
=
Public / Private Key

JWT Validation
=
Signature + Claims Validation
```

---

# End Of Page 2

## Next Page

### Page 3 – API Security
# PART III - API SECURITY

# PAGE 3

---

# 28. Why API Security Matters

APIs expose business functionality and data.

Without proper security:

```text
Data Breaches

Unauthorized Access

Account Takeovers

Data Manipulation

Financial Loss
```

can occur.

---

## Real World Examples

```text
Banking APIs

Trading APIs

Payment APIs

Healthcare APIs
```

all require strong security controls.

---

# 29. HTTPS & TLS

## What Is HTTPS?

HTTPS stands for:

```text
HyperText Transfer Protocol Secure
```

HTTPS encrypts communication between:

```text
Client
    ↔
Server
```

---

## Why Not HTTP?

HTTP sends data in plain text.

Example:

```http
POST /login

username=saurabh
password=password123
```

Anyone intercepting traffic can read it.

---

## HTTPS Solution

```text
Client
     ↓
TLS Encryption
     ↓
Server
```

Data is encrypted during transmission.

---

# 30. TLS Handshake

TLS establishes a secure communication channel.

---

## Simplified Flow

```text
Client
    ↓
Server Certificate
    ↓
Public Key Exchange
    ↓
Session Key Creation
    ↓
Encrypted Communication
```

---

## Result

Provides:

```text
Confidentiality

Integrity

Authentication
```

---

# 31. Authentication vs Security

Authentication is only one part of security.

---

Authentication answers:

```text
Who Are You?
```

---

Security additionally includes:

```text
Encryption

Input Validation

Authorization

Rate Limiting

Monitoring

Audit Logging
```

---

# 32. Request Validation

Never trust client input.

---

## Example

Incoming Request:

```json
{
   "age": -50
}
```

---

Without validation:

```text
Bad Data
Corrupt Data
Unexpected Failures
```

---

With validation:

```text
Reject Invalid Requests
```

---

# 33. Bean Validation

Spring Boot commonly uses:

```java
@NotNull

@NotBlank

@Size

@Min

@Max

@Pattern
```

---

## Example

```java
public class UserRequest {

    @NotBlank
    private String username;

    @Min(18)
    private Integer age;

}
```

---

Benefits:

```text
Consistent Validation

Cleaner Code

Centralized Rules
```

---

# 34. JSON Schema Validation

Useful when APIs support dynamic payloads.

---

Example:

```json
{
  "type":"object",
  "required":["username"]
}
```

---

Benefits:

```text
Schema Enforcement

Contract Validation

Better API Governance
```

---

# 35. Input Sanitization

Validation verifies correctness.

Sanitization removes dangerous content.

---

Example:

```html
<script>alert("hack")</script>
```

---

Without sanitization:

```text
XSS Attacks
```

may occur.

---

# 36. SQL Injection

One of the most famous security vulnerabilities.

---

## Bad Example

```java
String sql =
"SELECT * FROM users WHERE id="
+ userInput;
```

---

Input:

```text
1 OR 1=1
```

---

Result:

```text
Entire Table Returned
```

---

## Solution

Use:

```java
PreparedStatement

JPA

Hibernate
```

---

# 37. Cross Site Scripting (XSS)

Occurs when malicious scripts are rendered in browsers.

---

Example:

```html
<script>
stealCookies()
</script>
```

---

Mitigation:

```text
Input Sanitization

Output Encoding

Content Security Policies
```

---

# 38. Cross Site Request Forgery (CSRF)

Victim is authenticated.

Attacker tricks victim into sending requests.

---

Example:

```text
Transfer Money

Delete User

Change Password
```

---

Mitigation:

```text
CSRF Tokens

SameSite Cookies

JWT Authentication
```

---

# 39. API Rate Limiting

Protects APIs from abuse.

---

Example:

```text
100 Requests Per Minute
```

---

Without rate limiting:

```text
Abuse

DDoS

Credential Stuffing

Resource Exhaustion
```

---

# 40. Rate Limiting Algorithms

## Fixed Window

Example:

```text
100 Requests
Per Minute
```

Simple but can create bursts.

---

## Sliding Window

Provides smoother rate limiting.

---

## Token Bucket

Widely used.

Allows bursts while enforcing average limits.

---

# 41. Replay Attacks

Attacker captures a valid request.

Replays it multiple times.

---

Example:

```text
Payment Request
```

sent repeatedly.

---

## Mitigation

```text
Nonces

Timestamps

Idempotency Keys

JWT Expiration
```

---

# 42. API Keys

Simple authentication mechanism.

---

Example:

```http
X-API-KEY: abc123
```

---

Commonly used for:

```text
Internal Services

Partner Integrations

Developer Portals
```

---

Limitations:

```text
No User Context

Limited Security
```

---

# 43. Secrets Management

Never hardcode:

```java
String password =
"admin123";
```

---

Use:

```text
AWS Secrets Manager

HashiCorp Vault

Azure Key Vault

Kubernetes Secrets
```

---

# 44. Security Headers

Common HTTP Security Headers:

```text
Content-Security-Policy

Strict-Transport-Security

X-Frame-Options

X-Content-Type-Options
```

---

Benefits:

```text
Reduced Browser Attacks

Improved Security Posture
```

---

# 45. Audit Logging

Security-sensitive systems require audit trails.

---

Log:

```text
User

Action

Timestamp

IP Address

Outcome
```

---

Examples:

```text
Login

Logout

Password Reset

Role Changes

Money Transfers
```

---

# 46. Security Architecture

Typical Flow:

```text
Client
   ↓
API Gateway
   ↓
Authentication
   ↓
Authorization
   ↓
Validation
   ↓
Business Logic
   ↓
Database
```

---

Security should exist at multiple layers.

Never rely on a single control.

---

# 47. Staff Engineer Discussion

## What Is The Most Common Security Mistake?

Answer:

```text
Trusting Client Input
```

---

## What Is Defense In Depth?

Applying multiple security controls.

Example:

```text
TLS

Authentication

Authorization

Validation

Rate Limiting

Monitoring
```

---

## Why Is HTTPS Mandatory?

Because credentials and sensitive data must never travel over the network unencrypted.

---

# Quick Revision Sheet

```text
HTTPS
=
Encrypted Communication

TLS
=
Transport Security

Validation
=
Check Correctness

Sanitization
=
Remove Dangerous Input

SQL Injection
=
Database Attack

XSS
=
Browser Attack

CSRF
=
Forged Requests

Rate Limiting
=
Abuse Protection

Secrets Manager
=
Credential Storage

Audit Logging
=
Traceability

Defense In Depth
=
Multiple Security Layers
```

---

# End Of Page 3

## Next Page

### Page 4 – API Versioning
# PART IV - API VERSIONING

# PAGE 4

---

# 48. Why API Versioning Matters

APIs evolve over time.

Business requirements change.

New features are introduced.

Data models evolve.

---

Without versioning:

```text
Existing Clients Break
```

---

With versioning:

```text
Old Clients Continue Working

New Clients Can Adopt New Features
```

---

## Goal

Allow APIs to evolve without disrupting consumers.

---

# 49. What Is Backward Compatibility?

A change is backward compatible if:

```text
Existing Clients Continue To Work
Without Modification
```

---

## Example

Original Response:

```json
{
  "id":123,
  "name":"John"
}
```

---

Updated Response:

```json
{
  "id":123,
  "name":"John",
  "email":"john@test.com"
}
```

---

This is usually:

```text
Backward Compatible
```

because existing consumers ignore new fields.

---

# 50. Breaking Changes

Breaking changes require consumers to modify their code.

---

## Examples

Removing Fields

```json
{
  "id":123
}
```

removing:

```json
{
  "name":"John"
}
```

---

Changing Data Types

Bad:

```json
{
  "id":123
}
```

Changed To:

```json
{
  "id":"ABC"
}
```

---

Changing Required Request Fields

Old Request:

```json
{
  "name":"John"
}
```

New Request:

```json
{
  "name":"John",
  "country":"US"
}
```

---

Result:

```text
Client Failures
```

---

# 51. API Versioning Strategies

Common approaches:

```text
URI Versioning

Header Versioning

Query Parameter Versioning

Media Type Versioning
```

---

# 52. URI Versioning

Most common approach.

---

Example:

```http
/api/v1/users

/api/v2/users
```

---

Benefits:

```text
Easy To Understand

Easy To Test

Widely Adopted
```

---

Drawbacks:

```text
Multiple Endpoints
```

must be maintained.

---

# 53. Header Versioning

Version supplied in request headers.

---

Example:

```http
GET /users

API-Version: 2
```

---

Benefits:

```text
Clean URLs

Flexible
```

---

Drawbacks:

```text
Less Discoverable
```

---

# 54. Query Parameter Versioning

Example:

```http
GET /users?version=2
```

---

Benefits:

```text
Simple
```

---

Drawbacks:

```text
Less Common

Can Become Messy
```

---

# 55. Media Type Versioning

Version embedded in content type.

---

Example:

```http
Accept:
application/vnd.company.v2+json
```

---

Benefits:

```text
REST Purists Prefer It
```

---

Drawbacks:

```text
Harder To Understand

Harder To Debug
```

---

# 56. Which Versioning Strategy Should You Use?

Most enterprise APIs use:

```text
URI Versioning
```

---

Example:

```http
/api/v1/accounts

/api/v2/accounts
```

---

Reason:

```text
Simple

Explicit

Consumer Friendly
```

---

# 57. Versioning In Microservices

Versioning becomes critical when:

```text
Multiple Teams

Multiple Clients

External Consumers
```

exist.

---

Examples:

```text
Mobile Apps

Web Applications

Partner Integrations

Internal Services
```

---

# 58. Deprecation Strategy

Old versions should not be removed immediately.

---

Recommended approach:

```text
Announce Deprecation

Publish Timeline

Monitor Usage

Remove After Migration
```

---

## Example

```text
v1 Active

v2 Released

6 Month Migration Window

v1 Retired
```

---

# 59. Consumer Driven APIs

Good API design considers consumers first.

---

Question:

```text
How Many Clients Use This API?
```

before making changes.

---

Bad approach:

```text
Change API

Break Clients

Inform Later
```

---

Good approach:

```text
Understand Consumers

Plan Migration

Communicate Early
```

---

# 60. API Evolution Example

Version 1:

```json
{
  "id":123,
  "name":"John"
}
```

---

Business Requirement:

Need customer tier.

---

Version 2:

```json
{
  "id":123,
  "name":"John",
  "tier":"GOLD"
}
```

---

Migration:

```text
v1 Continues Running

v2 Introduced

Clients Migrate Gradually
```

---

# 61. Backward Compatibility Best Practices

Prefer:

```text
Additive Changes
```

instead of:

```text
Destructive Changes
```

---

Good Examples:

```text
Adding Optional Fields

Adding Endpoints

Adding Headers
```

---

Bad Examples:

```text
Removing Fields

Changing Types

Changing Semantics
```

---

# 62. API Contract Management

An API contract defines:

```text
Request Structure

Response Structure

Headers

Status Codes

Validation Rules
```

---

Changing contracts requires:

```text
Impact Analysis
```

---

Tools:

```text
OpenAPI

Swagger

Contract Testing
```

---

# 63. Staff Engineer Discussion

## Why Is Versioning Important?

Because APIs are long-lived contracts.

Consumers may depend on them for years.

---

## What Is The Biggest Versioning Mistake?

Making breaking changes without a migration strategy.

---

## When Should A New Version Be Created?

Typically when:

```text
Breaking Changes
```

are introduced.

---

## When Is A New Version NOT Required?

For:

```text
Bug Fixes

Performance Improvements

Optional Fields
```

---

# 64. Real World Example

Financial institutions often expose APIs to:

```text
Trading Systems

Risk Platforms

Compliance Systems

Partner Applications
```

Breaking one API can impact dozens of downstream systems.

---

Therefore:

```text
Backward Compatibility
```

is often treated as a critical architectural requirement.

---

# Quick Revision Sheet

```text
Backward Compatible
=
Old Clients Still Work

Breaking Change
=
Client Code Must Change

Most Common Strategy
=
URI Versioning

/api/v1
/api/v2

Good Change
=
Add Field

Bad Change
=
Remove Field

Deprecation
=
Gradual Retirement

Contract
=
API Agreement

New Version
=
Breaking Change
```

---

# End Of Page 4

## Next Page

### Page 5 – API Documentation & OpenAPI
# PART V - API DOCUMENTATION & OPENAPI

# PAGE 5

---

# 65. Why API Documentation Matters

APIs are contracts between systems.

Without proper documentation:

```text
Developers Guess

Integration Errors Increase

Support Tickets Increase

Adoption Decreases
```

---

## Goal

Good API documentation should answer:

```text
What Does This API Do?

How Do I Call It?

What Inputs Are Required?

What Responses Can I Expect?

What Errors Can Occur?
```

---

# 66. What Is OpenAPI?

OpenAPI is the industry standard specification for describing REST APIs.

---

## Purpose

Provides a machine-readable API contract.

---

Example:

```yaml
openapi: 3.0.0

paths:
  /users:
    get:
      summary: Get Users
```

---

Benefits:

```text
Standardization

Code Generation

Documentation Generation

Contract Validation
```

---

# 67. What Is Swagger?

Swagger is a set of tools built around the OpenAPI specification.

---

Common Components:

```text
Swagger UI

Swagger Editor

Swagger Codegen
```

---

## Most Popular Usage

Interactive API documentation.

---

Example:

```text
Developer
     ↓
Swagger UI
     ↓
Try API Directly
```

---

# 68. OpenAPI vs Swagger

## OpenAPI

Specification.

---

## Swagger

Tooling Ecosystem.

---

Simple Interview Answer:

```text
OpenAPI
=
Standard

Swagger
=
Implementation Tools
```

---

# 69. Why Swagger UI Is Popular

Swagger UI provides:

```text
Live Documentation

Request Examples

Response Examples

Interactive Testing
```

---

Benefits:

```text
Faster Onboarding

Fewer Integration Issues

Better Developer Experience
```

---

# 70. Basic OpenAPI Example

```yaml
paths:

  /users/{id}:

    get:

      summary: Get User

      parameters:

        - name: id
          in: path
          required: true

      responses:

        200:
          description: Success
```

---

What It Defines:

```text
Endpoint

Request Parameters

Response Structure

Status Codes
```

---

# 71. Request Documentation

Every API should document:

```text
Method

Endpoint

Headers

Authentication

Parameters

Request Body
```

---

Example:

```http
POST /users
```

---

Headers:

```http
Authorization: Bearer JWT
```

---

Request:

```json
{
  "name":"John",
  "email":"john@test.com"
}
```

---

# 72. Response Documentation

Document successful responses.

---

Example:

```json
{
  "id":123,
  "name":"John",
  "email":"john@test.com"
}
```

---

Benefits:

```text
Consumer Clarity

Faster Development

Reduced Questions
```

---

# 73. Error Documentation

Many APIs document success responses but ignore errors.

---

Document:

```text
400 Bad Request

401 Unauthorized

403 Forbidden

404 Not Found

500 Internal Server Error
```

---

Example:

```json
{
  "errorCode":"USER_NOT_FOUND",
  "message":"User does not exist"
}
```

---

# 74. API Examples

Examples dramatically improve adoption.

---

Bad Documentation:

```text
POST /users
```

---

Good Documentation:

```http
POST /users
```

Request:

```json
{
  "name":"John"
}
```

Response:

```json
{
  "id":123,
  "name":"John"
}
```

---

# 75. Consumer Experience

Think from the perspective of API consumers.

---

Questions consumers ask:

```text
How Do I Authenticate?

What Fields Are Required?

What Errors Can Occur?

How Do I Retry?
```

---

Good documentation answers these questions proactively.

---

# 76. API Contracts

Documentation should reflect the actual API contract.

---

Mismatch between:

```text
Documentation

Implementation
```

creates major problems.

---

## Common Issue

```text
Documentation Says One Thing

API Returns Another
```

---

Result:

```text
Consumer Frustration
```

---

# 77. Keeping Documentation Current

Documentation should evolve with the API.

---

Best Practice:

```text
Code Changes

↓

OpenAPI Updated

↓

Documentation Published
```

---

Avoid:

```text
Documentation Updated Months Later
```

---

# 78. Documentation Automation

Modern organizations generate documentation automatically.

---

Example:

```text
Spring Boot
       ↓
OpenAPI Annotations
       ↓
Swagger UI
```

---

Benefits:

```text
Less Manual Work

More Accurate Docs

Reduced Drift
```

---

# 79. Sandbox Environments

Consumers should have access to:

```text
Development Environment

Sandbox Environment

Mock APIs
```

---

Benefits:

```text
Safe Testing

Faster Integration

Reduced Production Risk
```

---

# 80. API Discoverability

Developers should easily discover:

```text
Available APIs

Authentication Requirements

Version Information

Usage Examples
```

---

Good documentation acts as:

```text
Developer Portal
```

rather than a simple PDF.

---

# 81. Documentation Best Practices

Always Document:

```text
Purpose

Authentication

Headers

Requests

Responses

Errors

Examples

Rate Limits
```

---

Avoid:

```text
Incomplete Examples

Outdated Docs

Missing Error Information
```

---

# 82. Staff Engineer Discussion

## Why Is Documentation Important?

Documentation reduces onboarding time and integration failures.

---

## What Is Better?

```text
Code First
```

or

```text
Documentation First
```

---

Most modern organizations prefer:

```text
API Contract First
```

using OpenAPI.

---

Benefits:

```text
Shared Understanding

Consumer Alignment

Earlier Feedback
```

---

# 83. Real World Example

A global analytics platform may expose APIs to:

```text
Internal Teams

Partner Systems

Mobile Apps

External Clients
```

Without strong documentation:

```text
Support Costs Increase

Integration Timelines Increase

Adoption Drops
```

---

# Quick Revision Sheet

```text
OpenAPI
=
API Specification

Swagger
=
Tooling Ecosystem

Swagger UI
=
Interactive Documentation

Document:
=
Requests
Responses
Errors
Examples

Contract First
=
Preferred Approach

Sandbox
=
Safe Testing Environment

Goal
=
Developer Experience
```

---

# End Of Page 5

## Next Page

### Page 6 – API Observability & Monitoring
# PART VI - API OBSERVABILITY & MONITORING

# PAGE 6

---

# 84. What Is Observability?

Observability is the ability to understand the internal state of a system by examining its outputs.

---

## Traditional Monitoring

Monitoring tells you:

```text
Something Is Wrong
```

---

## Observability

Observability tells you:

```text
What Is Wrong

Why It Is Wrong

Where It Is Wrong
```

---

## Goal

When production issues occur, engineers should be able to answer:

```text
What Failed?

When Did It Fail?

Why Did It Fail?

Which Services Were Impacted?
```

---

# 85. Three Pillars Of Observability

Modern observability is built on:

```text
Logs

Metrics

Traces
```

---

## Simple Visualization

```text
Logs
   +
Metrics
   +
Traces
   =
Observability
```

---

# 86. Logging

Logs capture detailed events occurring within applications.

---

## Example

```text
2026-01-01 10:00:00

User 123 Logged In
```

---

## Purpose

Logs help answer:

```text
What Happened?
```

---

## Common Log Data

```text
Timestamp

Request Id

User Id

API Endpoint

Status Code

Error Details
```

---

# 87. Structured Logging

Avoid:

```text
Something Failed
```

---

Prefer:

```json
{
  "timestamp":"2026-01-01T10:00:00",
  "requestId":"abc123",
  "userId":"123",
  "endpoint":"/accounts",
  "status":"FAILED"
}
```

---

Benefits:

```text
Searchable

Machine Readable

Easier Debugging
```

---

# 88. Log Levels

## DEBUG

Detailed development information.

---

## INFO

Normal application events.

---

## WARN

Potential issues.

---

## ERROR

Failures requiring investigation.

---

Example:

```text
DEBUG

INFO

WARN

ERROR
```

---

# 89. Metrics

Metrics are numerical measurements collected over time.

---

Examples:

```text
Requests Per Second

Latency

CPU Usage

Memory Usage

Error Rate
```

---

Metrics answer:

```text
How Healthy Is The System?
```

---

# 90. Golden Signals

Google's Site Reliability Engineering (SRE) model identifies four key metrics.

---

## Latency

How long requests take.

---

## Traffic

How much demand exists.

---

## Errors

How many requests fail.

---

## Saturation

How close the system is to capacity.

---

### Remember

```text
Latency

Traffic

Errors

Saturation
```

---

# 91. Application Metrics

Common API metrics:

```text
Request Count

Success Count

Failure Count

Response Time

Throughput
```

---

Example:

```text
500 Requests Per Second

99.9% Success Rate
```

---

# 92. Prometheus

Prometheus is the most popular open-source monitoring system.

---

## Purpose

Collects metrics from applications.

---

Example Metrics:

```text
http_requests_total

jvm_memory_usage

api_response_time
```

---

## Flow

```text
Application
      ↓
Prometheus
      ↓
Grafana
```

---

# 93. Grafana

Grafana visualizes metrics.

---

Examples:

```text
Latency Dashboard

Error Dashboard

Infrastructure Dashboard
```

---

Benefits:

```text
Real Time Visibility

Alerting

Trend Analysis
```

---

# 94. Distributed Tracing

Modern systems often involve multiple services.

---

Example:

```text
API Gateway
      ↓
User Service
      ↓
Account Service
      ↓
Database
```

---

Question:

```text
Where Did The Request Slow Down?
```

---

Tracing answers this.

---

# 95. Trace IDs

Every request receives a unique identifier.

---

Example:

```text
TRACE-123456
```

---

Flow:

```text
Gateway
   ↓
Service A
   ↓
Service B
   ↓
Database
```

All logs contain:

```text
TRACE-123456
```

---

Benefits:

```text
End To End Visibility
```

---

# 96. Correlation IDs vs Trace IDs

## Trace ID

Tracks a request across systems.

---

## Correlation ID

Groups related business operations.

---

Example:

```text
Customer Onboarding
```

may involve:

```text
10 API Calls

3 Databases

4 Services
```

All linked via correlation ID.

---

# 97. OpenTelemetry

Industry standard for observability.

---

Supports:

```text
Logs

Metrics

Tracing
```

---

Benefits:

```text
Vendor Neutral

Cloud Friendly

Standardized
```

---

# 98. Alerting

Monitoring without alerts is incomplete.

---

Examples:

```text
Error Rate > 5%

Latency > 500ms

CPU > 90%

Database Down
```

---

Alert Flow:

```text
Application
      ↓
Prometheus
      ↓
Alert Manager
      ↓
Email / Slack / PagerDuty
```

---

# 99. SLIs, SLOs & SLAs

## SLI

Service Level Indicator.

Example:

```text
Response Time
```

---

## SLO

Service Level Objective.

Example:

```text
99.9% Availability
```

---

## SLA

Service Level Agreement.

Contractual commitment.

Example:

```text
99.95% Availability
```

---

# 100. Error Budget

Error budget represents acceptable failure.

---

Example:

```text
99.9% Availability
```

allows:

```text
0.1% Failure
```

---

Purpose:

```text
Balance Reliability

And

Feature Delivery
```

---

# 101. Health Checks

Applications expose health endpoints.

---

Example:

```http
GET /actuator/health
```

---

Response:

```json
{
  "status":"UP"
}
```

---

Used by:

```text
Load Balancers

Kubernetes

Monitoring Systems
```

---

# 102. API Monitoring Dashboard

Typical Dashboard Includes:

```text
Request Volume

Latency

Error Rate

Success Rate

Throughput

Top Endpoints
```

---

# 103. Real World Example

Global Analytics API:

```text
Millions Of Requests Daily
```

Monitoring tracks:

```text
Authentication Failures

Latency

Database Response Time

Cache Hit Ratio

Error Rates
```

---

# 104. Staff Engineer Discussion

## Why Are Logs Alone Not Enough?

Logs explain individual events.

Metrics show trends.

Tracing shows request flow.

All three are needed.

---

## Most Important Metric?

Depends on business goals, but common answers:

```text
Latency

Availability

Error Rate
```

---

## Why Use Distributed Tracing?

Microservices introduce complexity.

Tracing identifies bottlenecks quickly.

---

# Quick Revision Sheet

```text
Observability
=
Understand System Behavior

Three Pillars
=
Logs
Metrics
Traces

Prometheus
=
Metrics Collection

Grafana
=
Visualization

Trace ID
=
Request Tracking

OpenTelemetry
=
Observability Standard

SLI
=
Measurement

SLO
=
Target

SLA
=
Contract

Health Check
=
Service Status
```

---

# End Of Page 6

## Next Page

### Page 7 – API Error Handling & Resilience
# PART VII - API ERROR HANDLING & RESILIENCE

# PAGE 7

---

# 105. Why Error Handling Matters

APIs do not operate in perfect environments.

Failures occur due to:

```text
Invalid Requests

Network Failures

Database Issues

Dependency Outages

Unexpected Bugs
```

---

## Goal

Error handling should:

```text
Prevent System Failures

Provide Useful Feedback

Improve Reliability

Simplify Troubleshooting
```

---

# 106. Characteristics Of Good Error Handling

Good error handling is:

```text
Consistent

Predictable

Actionable

Traceable
```

---

Bad Example:

```json
{
  "error":"Something went wrong"
}
```

---

Good Example:

```json
{
  "errorCode":"USER_NOT_FOUND",
  "message":"User does not exist",
  "traceId":"abc123"
}
```

---

# 107. HTTP Status Codes Overview

HTTP status codes communicate request outcomes.

---

## Categories

```text
2xx = Success

3xx = Redirect

4xx = Client Error

5xx = Server Error
```

---

# 108. Common Success Codes

## 200 OK

Request succeeded.

---

Example:

```http
GET /users/123
```

---

## 201 Created

Resource successfully created.

---

Example:

```http
POST /users
```

---

## 204 No Content

Success with no response body.

---

Example:

```http
DELETE /users/123
```

---

# 109. Common Client Error Codes

## 400 Bad Request

Invalid request.

---

Example:

```json
{
  "age": -10
}
```

---

## 401 Unauthorized

Authentication missing or invalid.

---

Example:

```text
Expired JWT
```

---

## 403 Forbidden

Authenticated but insufficient permissions.

---

Example:

```text
User Attempting Admin Action
```

---

## 404 Not Found

Requested resource does not exist.

---

Example:

```http
GET /users/99999
```

---

## 409 Conflict

Conflict with current state.

---

Example:

```text
Duplicate Username
```

---

## 429 Too Many Requests

Rate limit exceeded.

---

# 110. Common Server Error Codes

## 500 Internal Server Error

Unexpected application failure.

---

## 502 Bad Gateway

Upstream service returned invalid response.

---

## 503 Service Unavailable

Service temporarily unavailable.

---

Examples:

```text
Maintenance

Dependency Failure

Capacity Issues
```

---

## 504 Gateway Timeout

Dependency did not respond in time.

---

# 111. Standard Error Response Design

Recommended structure:

```json
{
  "timestamp":"2026-01-01T10:00:00Z",
  "status":404,
  "errorCode":"USER_NOT_FOUND",
  "message":"User does not exist",
  "traceId":"abc123"
}
```

---

Benefits:

```text
Consistency

Searchability

Debuggability
```

---

# 112. Error Codes vs Error Messages

## Error Code

Machine-readable.

Example:

```text
USER_NOT_FOUND
```

---

## Error Message

Human-readable.

Example:

```text
User does not exist
```

---

Best practice:

```text
Expose Both
```

---

# 113. Global Exception Handling

Avoid:

```java
try {
}
catch(Exception e){
}
```

in every controller.

---

Spring Boot provides:

```java
@ControllerAdvice
```

---

Example:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

---

Benefits:

```text
Centralized Logic

Consistency

Less Boilerplate
```

---

# 114. Validation Errors

Example Request:

```json
{
  "age": -5
}
```

---

Response:

```json
{
  "errorCode":"VALIDATION_ERROR",
  "message":"Age must be greater than 0"
}
```

---

Benefits:

```text
Consumer Friendly

Easy To Debug
```

---

# 115. Dependency Failures

Modern APIs often call:

```text
Databases

Caches

External APIs

Message Brokers
```

---

Any dependency can fail.

---

Example:

```text
API
 ↓
Payment Service
 ↓
Timeout
```

---

Response:

```http
503 Service Unavailable
```

---

# 116. Timeouts

Never wait forever.

---

Bad:

```text
Infinite Wait
```

---

Good:

```text
2 Seconds

5 Seconds

10 Seconds
```

depending on use case.

---

Benefits:

```text
Prevent Resource Exhaustion

Improve Stability
```

---

# 117. Retries

Retries help recover from temporary failures.

---

Example:

```text
Call Service

Fail

Retry

Success
```

---

Useful for:

```text
Network Glitches

Temporary Dependency Issues
```

---

Avoid retrying:

```text
Validation Errors

Authentication Failures
```

---

# 118. Exponential Backoff

Bad:

```text
Retry Every 1 Second
```

---

Good:

```text
1 Second

2 Seconds

4 Seconds

8 Seconds
```

---

Benefits:

```text
Reduces Load

Improves Stability
```

---

# 119. Circuit Breaker Pattern

Protects systems from cascading failures.

---

Without Circuit Breaker:

```text
Service A
 ↓
Service B Down
 ↓
Infinite Failures
```

---

With Circuit Breaker:

```text
Service A
 ↓
Circuit Open
 ↓
Fail Fast
```

---

Popular Implementation:

```text
Resilience4j
```

---

# 120. Circuit Breaker States

## Closed

Normal operation.

---

## Open

Failures exceed threshold.

Requests blocked.

---

## Half Open

Testing recovery.

Limited requests allowed.

---

Flow:

```text
Closed
 ↓
Open
 ↓
Half Open
 ↓
Closed
```

---

# 121. Bulkhead Pattern

Inspired by ship compartments.

---

Goal:

```text
Isolate Failures
```

---

Example:

```text
User APIs

Payment APIs

Admin APIs
```

operate independently.

---

Failure in one area should not impact all traffic.

---

# 122. Fallback Responses

Sometimes degraded service is acceptable.

---

Example:

```text
Recommendation Service Down
```

Return:

```text
Popular Products
```

instead.

---

Benefits:

```text
Better User Experience
```

---

# 123. Idempotent Retries

Retries can create duplicate operations.

---

Example:

```text
Charge Credit Card
```

Retrying may charge twice.

---

Solution:

```text
Idempotency Keys
```

---

Covered in detail on:

```text
Page 10
```

---

# 124. Traceability During Failures

Always include:

```text
Trace ID

Correlation ID
```

in logs and responses.

---

Benefits:

```text
Faster Troubleshooting

Better Root Cause Analysis
```

---

# 125. Real World Example

Global Analytics API:

```text
Client
 ↓
API Gateway
 ↓
Analytics Service
 ↓
Redis
 ↓
Database
```

Potential failures:

```text
Redis Down

Database Timeout

Gateway Failure

Network Partition
```

Resilience mechanisms:

```text
Retries

Circuit Breakers

Timeouts

Fallbacks
```

---

# 126. Staff Engineer Discussion

## What Is The Most Common Reliability Mistake?

Answer:

```text
Missing Timeouts
```

---

## Why Are Retries Dangerous?

Retries can amplify failures if not controlled.

---

## Why Use Circuit Breakers?

Prevent cascading failures and preserve system stability.

---

## What Is Better?

```text
Fast Failure
```

or

```text
Slow Failure
```

Usually:

```text
Fast Failure
```

because resources are protected.

---

# Quick Revision Sheet

```text
400
=
Bad Request

401
=
Unauthorized

403
=
Forbidden

404
=
Not Found

429
=
Rate Limited

500
=
Internal Error

503
=
Unavailable

Timeout
=
Bound Waiting

Retry
=
Recover Temporary Failure

Circuit Breaker
=
Prevent Cascading Failure

Bulkhead
=
Failure Isolation

Fallback
=
Graceful Degradation

Trace ID
=
Troubleshooting
```

---

# End Of Page 7

## Next Page

### Page 8 – API Performance & Scalability
# PART VII - API ERROR HANDLING & RESILIENCE

# PAGE 7

---

# 105. Why Error Handling Matters

APIs do not operate in perfect environments.

Failures occur due to:

```text
Invalid Requests

Network Failures

Database Issues

Dependency Outages

Unexpected Bugs
```

---

## Goal

Error handling should:

```text
Prevent System Failures

Provide Useful Feedback

Improve Reliability

Simplify Troubleshooting
```

---

# 106. Characteristics Of Good Error Handling

Good error handling is:

```text
Consistent

Predictable

Actionable

Traceable
```

---

Bad Example:

```json
{
  "error":"Something went wrong"
}
```

---

Good Example:

```json
{
  "errorCode":"USER_NOT_FOUND",
  "message":"User does not exist",
  "traceId":"abc123"
}
```

---

# 107. HTTP Status Codes Overview

HTTP status codes communicate request outcomes.

---

## Categories

```text
2xx = Success

3xx = Redirect

4xx = Client Error

5xx = Server Error
```

---

# 108. Common Success Codes

## 200 OK

Request succeeded.

---

Example:

```http
GET /users/123
```

---

## 201 Created

Resource successfully created.

---

Example:

```http
POST /users
```

---

## 204 No Content

Success with no response body.

---

Example:

```http
DELETE /users/123
```

---

# 109. Common Client Error Codes

## 400 Bad Request

Invalid request.

---

Example:

```json
{
  "age": -10
}
```

---

## 401 Unauthorized

Authentication missing or invalid.

---

Example:

```text
Expired JWT
```

---

## 403 Forbidden

Authenticated but insufficient permissions.

---

Example:

```text
User Attempting Admin Action
```

---

## 404 Not Found

Requested resource does not exist.

---

Example:

```http
GET /users/99999
```

---

## 409 Conflict

Conflict with current state.

---

Example:

```text
Duplicate Username
```

---

## 429 Too Many Requests

Rate limit exceeded.

---

# 110. Common Server Error Codes

## 500 Internal Server Error

Unexpected application failure.

---

## 502 Bad Gateway

Upstream service returned invalid response.

---

## 503 Service Unavailable

Service temporarily unavailable.

---

Examples:

```text
Maintenance

Dependency Failure

Capacity Issues
```

---

## 504 Gateway Timeout

Dependency did not respond in time.

---

# 111. Standard Error Response Design

Recommended structure:

```json
{
  "timestamp":"2026-01-01T10:00:00Z",
  "status":404,
  "errorCode":"USER_NOT_FOUND",
  "message":"User does not exist",
  "traceId":"abc123"
}
```

---

Benefits:

```text
Consistency

Searchability

Debuggability
```

---

# 112. Error Codes vs Error Messages

## Error Code

Machine-readable.

Example:

```text
USER_NOT_FOUND
```

---

## Error Message

Human-readable.

Example:

```text
User does not exist
```

---

Best practice:

```text
Expose Both
```

---

# 113. Global Exception Handling

Avoid:

```java
try {
}
catch(Exception e){
}
```

in every controller.

---

Spring Boot provides:

```java
@ControllerAdvice
```

---

Example:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
}
```

---

Benefits:

```text
Centralized Logic

Consistency

Less Boilerplate
```

---

# 114. Validation Errors

Example Request:

```json
{
  "age": -5
}
```

---

Response:

```json
{
  "errorCode":"VALIDATION_ERROR",
  "message":"Age must be greater than 0"
}
```

---

Benefits:

```text
Consumer Friendly

Easy To Debug
```

---

# 115. Dependency Failures

Modern APIs often call:

```text
Databases

Caches

External APIs

Message Brokers
```

---

Any dependency can fail.

---

Example:

```text
API
 ↓
Payment Service
 ↓
Timeout
```

---

Response:

```http
503 Service Unavailable
```

---

# 116. Timeouts

Never wait forever.

---

Bad:

```text
Infinite Wait
```

---

Good:

```text
2 Seconds

5 Seconds

10 Seconds
```

depending on use case.

---

Benefits:

```text
Prevent Resource Exhaustion

Improve Stability
```

---

# 117. Retries

Retries help recover from temporary failures.

---

Example:

```text
Call Service

Fail

Retry

Success
```

---

Useful for:

```text
Network Glitches

Temporary Dependency Issues
```

---

Avoid retrying:

```text
Validation Errors

Authentication Failures
```

---

# 118. Exponential Backoff

Bad:

```text
Retry Every 1 Second
```

---

Good:

```text
1 Second

2 Seconds

4 Seconds

8 Seconds
```

---

Benefits:

```text
Reduces Load

Improves Stability
```

---

# 119. Circuit Breaker Pattern

Protects systems from cascading failures.

---

Without Circuit Breaker:

```text
Service A
 ↓
Service B Down
 ↓
Infinite Failures
```

---

With Circuit Breaker:

```text
Service A
 ↓
Circuit Open
 ↓
Fail Fast
```

---

Popular Implementation:

```text
Resilience4j
```

---

# 120. Circuit Breaker States

## Closed

Normal operation.

---

## Open

Failures exceed threshold.

Requests blocked.

---

## Half Open

Testing recovery.

Limited requests allowed.

---

Flow:

```text
Closed
 ↓
Open
 ↓
Half Open
 ↓
Closed
```

---

# 121. Bulkhead Pattern

Inspired by ship compartments.

---

Goal:

```text
Isolate Failures
```

---

Example:

```text
User APIs

Payment APIs

Admin APIs
```

operate independently.

---

Failure in one area should not impact all traffic.

---

# 122. Fallback Responses

Sometimes degraded service is acceptable.

---

Example:

```text
Recommendation Service Down
```

Return:

```text
Popular Products
```

instead.

---

Benefits:

```text
Better User Experience
```

---

# 123. Idempotent Retries

Retries can create duplicate operations.

---

Example:

```text
Charge Credit Card
```

Retrying may charge twice.

---

Solution:

```text
Idempotency Keys
```

---

Covered in detail on:

```text
Page 10
```

---

# 124. Traceability During Failures

Always include:

```text
Trace ID

Correlation ID
```

in logs and responses.

---

Benefits:

```text
Faster Troubleshooting

Better Root Cause Analysis
```

---

# 125. Real World Example

Global Analytics API:

```text
Client
 ↓
API Gateway
 ↓
Analytics Service
 ↓
Redis
 ↓
Database
```

Potential failures:

```text
Redis Down

Database Timeout

Gateway Failure

Network Partition
```

Resilience mechanisms:

```text
Retries

Circuit Breakers

Timeouts

Fallbacks
```

---

# 126. Staff Engineer Discussion

## What Is The Most Common Reliability Mistake?

Answer:

```text
Missing Timeouts
```

---

## Why Are Retries Dangerous?

Retries can amplify failures if not controlled.

---

## Why Use Circuit Breakers?

Prevent cascading failures and preserve system stability.

---

## What Is Better?

```text
Fast Failure
```

or

```text
Slow Failure
```

Usually:

```text
Fast Failure
```

because resources are protected.

---

# Quick Revision Sheet

```text
400
=
Bad Request

401
=
Unauthorized

403
=
Forbidden

404
=
Not Found

429
=
Rate Limited

500
=
Internal Error

503
=
Unavailable

Timeout
=
Bound Waiting

Retry
=
Recover Temporary Failure

Circuit Breaker
=
Prevent Cascading Failure

Bulkhead
=
Failure Isolation

Fallback
=
Graceful Degradation

Trace ID
=
Troubleshooting
```

---

# End Of Page 7

## Next Page

### Page 8 – API Performance & Scalability
# PART IX - CONSISTENCY & RESOURCE MODELING

# PAGE 9

---

# 151. What Is Resource Modeling?

Resource modeling is the process of designing APIs around business entities rather than database tables.

---

## Goal

Expose business concepts in a clean and intuitive manner.

---

Examples:

```text
Users

Accounts

Orders

Transactions

Products
```

---

Good APIs model:

```text
Business Resources
```

not:

```text
Database Structure
```

---

# 152. What Is A Resource?

A resource represents a business object.

---

Examples:

```text
Customer

Account

Payment

Invoice

Trade
```

---

Resource Example:

```http
GET /customers/123
```

returns:

```json
{
  "id":123,
  "name":"John Doe"
}
```

---

# 153. REST Resource Principles

REST APIs should be resource-oriented.

---

Good:

```http
GET /users

GET /users/123

GET /accounts/456
```

---

Bad:

```http
GET /getUser

GET /fetchAccount

GET /retrieveCustomer
```

---

Reason:

```text
Resources
Not Actions
```

---

# 154. Resource Naming Best Practices

Use:

```text
Plural Nouns
```

---

Examples:

```http
/users

/accounts

/orders

/products
```

---

Avoid:

```http
/user

/getUsers

/userList
```

---

Benefits:

```text
Consistency

Predictability

Developer Experience
```

---

# 155. URI Design Principles

URIs should be:

```text
Simple

Consistent

Readable
```

---

Examples:

```http
/users

/users/123

/users/123/accounts
```

---

Avoid:

```http
/doUserLookup

/getCustomerData
```

---

# 156. Hierarchical Resources

Resources often have relationships.

---

Example:

```text
Customer
   ↓
Accounts
```

---

API Design:

```http
GET /customers/123/accounts
```

---

Another Example:

```text
Order
   ↓
Items
```

---

API:

```http
GET /orders/456/items
```

---

# 157. Query Parameters

Used for:

```text
Filtering

Sorting

Searching

Pagination
```

---

Examples:

```http
GET /users?status=ACTIVE
```

---

```http
GET /users?country=US
```

---

```http
GET /users?sort=name
```

---

# 158. Filtering

Example:

```http
GET /transactions?status=PENDING
```

---

Benefits:

```text
Reduced Payload Size

Improved Flexibility
```

---

# 159. Sorting

Example:

```http
GET /users?sort=lastName
```

---

Descending:

```http
GET /users?sort=-lastName
```

---

Benefits:

```text
Consumer Control

Reduced Client Logic
```

---

# 160. Searching

Example:

```http
GET /customers?search=john
```

---

Benefits:

```text
Flexible Retrieval

Improved UX
```

---

# 161. Resource Relationships

Example:

```text
Customer
   ↓
Accounts
   ↓
Transactions
```

---

Possible Endpoints:

```http
GET /customers/123

GET /customers/123/accounts

GET /accounts/456/transactions
```

---

# 162. Consistency In API Design

Consistency is more important than perfection.

---

Example:

If one endpoint uses:

```http
/users/{id}
```

then avoid:

```http
/customer/{customerId}
```

for a similar pattern.

---

Use consistent conventions.

---

Benefits:

```text
Lower Learning Curve

Fewer Errors

Better Maintainability
```

---

# 163. Resource Identifiers

Every resource should have a stable identifier.

---

Examples:

```text
User ID

Account ID

Order ID

Transaction ID
```

---

Example:

```http
GET /users/123
```

---

# 164. Composite Resources

Sometimes APIs aggregate data from multiple services.

---

Example:

```http
GET /customer-summary/123
```

---

Response:

```json
{
  "customer":{},
  "accounts":[],
  "transactions":[]
}
```

---

Benefits:

```text
Reduced Network Calls

Improved Consumer Experience
```

---

Tradeoff:

```text
Higher Backend Complexity
```

---

# 165. Resource Expansion

Consumers may request additional information.

---

Example:

```http
GET /users/123?expand=accounts
```

---

Benefits:

```text
Flexible Responses

Reduced Round Trips
```

---

# 166. Consistency Models

In distributed systems:

```text
Consistency

Availability

Partition Tolerance
```

must be balanced.

---

Common Models:

```text
Strong Consistency

Eventual Consistency
```

---

# 167. Strong Consistency

Every read returns the latest write.

---

Example:

```text
Bank Balance
```

---

Benefits:

```text
Accuracy

Predictability
```

---

Tradeoffs:

```text
Higher Latency

Lower Availability
```

---

# 168. Eventual Consistency

Updates propagate over time.

---

Example:

```text
Analytics Dashboard

Recommendation Systems

Social Media Feeds
```

---

Benefits:

```text
Scalability

Availability
```

---

Tradeoffs:

```text
Temporary Stale Data
```

---

# 169. Read-Your-Writes Consistency

A useful middle ground.

---

Guarantee:

```text
User Sees Their Own Updates
```

---

Example:

```text
Update Profile

Refresh Page

See Updated Profile
```

---

# 170. Idempotent Resource Updates

Resource design should support safe retries.

---

Example:

```http
PUT /users/123
```

---

Multiple executions:

```text
Same Result
```

---

Supports:

```text
Reliable APIs

Retry Mechanisms
```

---

# 171. Real World Example

Financial APIs commonly expose:

```text
Customers

Accounts

Positions

Transactions

Alerts
```

---

Good Design:

```http
GET /accounts/{id}

GET /accounts/{id}/transactions

GET /customers/{id}/accounts
```

---

Bad Design:

```http
GET /getTransactions

GET /accountLookup

GET /findCustomer
```

---

# 172. Staff Engineer Discussion

## Resource-Oriented vs Action-Oriented APIs?

Prefer:

```text
Resource-Oriented
```

for REST APIs.

---

## Why Is Consistency Important?

Because developers interact with dozens or hundreds of APIs.

Consistency reduces cognitive load.

---

## Strong Consistency Or Eventual Consistency?

Depends on business requirements.

---

Examples:

```text
Payments
=
Strong Consistency

Analytics
=
Eventual Consistency
```

---

# Quick Revision Sheet

```text
Resource
=
Business Entity

REST
=
Resource Oriented

Plural Nouns
=
Best Practice

Filtering
=
?status=ACTIVE

Sorting
=
?sort=name

Strong Consistency
=
Latest Data

Eventual Consistency
=
Eventually Correct

Read Your Writes
=
User Sees Own Update

Resource Modeling
=
Business First Design
```

---

# End Of Page 9

## Next Page

### Page 10 – Idempotency & Reliable API Design
# PART X - IDEMPOTENCY & RELIABLE API DESIGN

# PAGE 10

---

# 173. What Is Idempotency?

An operation is idempotent if executing it multiple times produces the same result as executing it once.

---

## Simple Definition

```text
Same Request

Executed Multiple Times

Same Final Outcome
```

---

## Example

```http
PUT /users/123
```

Request:

```json
{
  "name":"John"
}
```

---

Execute:

```text
1 Time

5 Times

100 Times
```

---

Final State:

```text
User Name = John
```

---

Result:

```text
Idempotent
```

---

# 174. Why Idempotency Matters

Distributed systems are unreliable.

Failures occur because of:

```text
Network Issues

Timeouts

Server Crashes

Load Balancer Failures

Client Retries
```

---

Question:

```text
What Happens If The Client
Doesn't Know Whether
The Request Succeeded?
```

---

Most clients retry.

Without idempotency:

```text
Duplicate Operations
```

may occur.

---

# 175. Real World Example

Imagine:

```http
POST /payments
```

Request:

```json
{
  "amount":100
}
```

---

Timeline:

```text
Payment Processed

Response Lost

Client Retries
```

---

Without protection:

```text
Customer Charged Twice
```

---

This is why idempotency is critical.

---

# 176. HTTP Method Semantics

Not all HTTP methods are equally idempotent.

---

## GET

```http
GET /users/123
```

---

Characteristics:

```text
Safe

Read Only

Idempotent
```

---

## PUT

```http
PUT /users/123
```

---

Characteristics:

```text
Idempotent
```

---

Repeated requests produce the same final state.

---

## DELETE

```http
DELETE /users/123
```

---

Deleting multiple times:

```text
Resource Remains Deleted
```

---

Therefore:

```text
Idempotent
```

---

## POST

```http
POST /payments
```

---

Characteristics:

```text
Usually Not Idempotent
```

---

Multiple executions often create:

```text
Duplicate Records
```

---

# 177. Safe vs Idempotent

Common interview question.

---

## Safe

Does not modify state.

Examples:

```text
GET

HEAD
```

---

## Idempotent

May modify state but repeated executions produce same outcome.

Examples:

```text
PUT

DELETE
```

---

Summary:

```text
Safe
≠
Idempotent

Idempotent
≠
Safe
```

---

# 178. Why Retries Need Idempotency

Typical Flow:

```text
Client
  ↓
API
  ↓
Timeout
  ↓
Client Retries
```

---

Without idempotency:

```text
Duplicate Orders

Duplicate Payments

Duplicate Trades
```

---

With idempotency:

```text
Single Business Operation
```

---

# 179. Idempotency Keys

Most common enterprise solution.

---

Client generates:

```text
Unique Request Identifier
```

---

Example:

```http
POST /payments

Idempotency-Key:
PAY-123456
```

---

Server stores:

```text
Idempotency Key

Request Result
```

---

# 180. Idempotency Key Flow

Initial Request:

```text
POST Payment

Key = ABC123
```

---

Server:

```text
Process Payment

Store Result
```

---

Retry:

```text
POST Payment

Key = ABC123
```

---

Server:

```text
Existing Key Found

Return Previous Result
```

---

No duplicate payment created.

---

# 181. Typical Architecture

```text
Client
   ↓
API Gateway
   ↓
Payment Service
   ↓
Idempotency Store
   ↓
Database
```

---

Idempotency Store may use:

```text
Redis

Database

Distributed Cache
```

---

# 182. Using Redis For Idempotency

Common enterprise pattern.

---

Flow:

```text
Receive Request

Check Redis

Key Exists?
```

---

If:

```text
No
```

Process request.

Store result.

---

If:

```text
Yes
```

Return existing response.

---

Benefits:

```text
Fast

Distributed

Scalable
```

---

# 183. Duplicate Request Protection

Protects against:

```text
Client Retries

Network Failures

Browser Refreshes

Load Balancer Retries
```

---

Without idempotency:

```text
Duplicate Business Events
```

---

# 184. Event Driven Systems

Idempotency is equally important in asynchronous systems.

---

Example:

```text
Kafka Consumer
```

receives:

```text
Payment Event
```

twice.

---

Consumer must ensure:

```text
Process Once

Ignore Duplicate
```

---

# 185. Exactly Once Processing

Frequently misunderstood concept.

---

Reality:

```text
Exactly Once
Is Extremely Difficult
```

---

Most systems implement:

```text
At Least Once Delivery
```

combined with:

```text
Idempotent Processing
```

---

Result:

```text
Effectively Once
```

---

# 186. Idempotent Consumer Pattern

Consumer receives:

```text
Event ID
```

---

Stores:

```text
Processed Event IDs
```

---

If event already processed:

```text
Ignore
```

---

Benefits:

```text
Reliable Event Processing
```

---

# 187. Database Constraints

Another protection mechanism.

---

Example:

```sql
UNIQUE(transaction_id)
```

---

Benefits:

```text
Database Enforced Protection
```

---

Limitations:

```text
Does Not Replace
Application Idempotency
```

---

# 188. Financial Systems Example

Consider:

```text
Wire Transfer

ACH Transfer

Stock Trade

Payment
```

---

Duplicate execution could cause:

```text
Financial Loss

Compliance Issues

Customer Impact
```

---

Therefore:

```text
Idempotency
=
Critical Requirement
```

---

# 189. API Design Best Practices

For critical operations:

```text
Support Idempotency Keys

Implement Retries

Use Timeouts

Log Request IDs

Track Trace IDs
```

---

# 190. Common Interview Question

Question:

```text
How Would You Prevent
Duplicate Payments?
```

---

Strong Answer:

```text
Use Idempotency Keys

Store Request Results

Return Existing Result
For Duplicate Keys
```

---

# 191. Staff Engineer Discussion

## Why Is Idempotency Important?

Because distributed systems experience retries and duplicate delivery.

---

## Is POST Idempotent?

Usually:

```text
No
```

---

But can be made idempotent using:

```text
Idempotency Keys
```

---

## Is PUT Idempotent?

```text
Yes
```

because repeated updates produce the same final state.

---

## How Do Payment Systems Prevent Double Charges?

```text
Idempotency Keys

Unique Transaction IDs

Database Constraints

Audit Logging
```

---

# 192. Real World Example

Global Analytics Platform:

```text
Client Requests Report
```

---

Request:

```text
Generate Report
```

---

Network timeout occurs.

---

Client retries.

---

Using idempotency:

```text
Same Report Request

Same Report Result

No Duplicate Processing
```

---

# Quick Revision Sheet

```text
Idempotency
=
Same Result After Retries

GET
=
Safe + Idempotent

PUT
=
Idempotent

DELETE
=
Idempotent

POST
=
Usually Not Idempotent

Idempotency Key
=
Duplicate Protection

Redis
=
Common Storage

At Least Once
+
Idempotent Consumer
=
Effectively Once

Payment Systems
=
Require Idempotency
```

---

# End Of Page 10

## Next Page

### Page 11 – Event Driven APIs & Kafka Integration
# PART XI - EVENT DRIVEN APIS & KAFKA INTEGRATION

# PAGE 11

---

# 193. Why Event Driven Architecture?

Traditional APIs are typically synchronous.

---

Example:

```text
Client
   ↓
API
   ↓
Database
   ↓
Response
```

---

Problem:

The client must wait for processing to complete.

---

As systems grow:

```text
More Services

More Dependencies

Longer Processing Times
```

---

This creates:

```text
Higher Latency

Tighter Coupling

Lower Scalability
```

---

# 194. What Is Event Driven Architecture?

Event Driven Architecture (EDA) is a design pattern where systems communicate through events.

---

Example:

```text
Producer
   ↓
Event
   ↓
Event Bus
   ↓
Consumers
```

---

Benefits:

```text
Loose Coupling

Scalability

Resilience

Asynchronous Processing
```

---

# 195. What Is An Event?

An event represents:

```text
Something That Happened
```

---

Examples:

```text
UserCreated

OrderPlaced

PaymentCompleted

AccountOpened

TradeExecuted
```

---

Event Example:

```json
{
  "eventType":"OrderPlaced",
  "orderId":"12345",
  "amount":100
}
```

---

# 196. Synchronous vs Asynchronous APIs

## Synchronous

```text
Client
  ↓
API
  ↓
Response
```

---

Characteristics:

```text
Immediate Response

Blocking

Simple
```

---

## Asynchronous

```text
Client
  ↓
Event Published
  ↓
Response Returned
  ↓
Processing Continues
```

---

Characteristics:

```text
Non Blocking

Scalable

Eventually Consistent
```

---

# 197. Why Kafka?

Kafka is one of the most widely used event streaming platforms.

---

Kafka provides:

```text
High Throughput

Durability

Scalability

Replayability

Fault Tolerance
```

---

Common Use Cases:

```text
Microservices

Analytics

Fraud Detection

Monitoring

Financial Systems
```

---

# 198. Kafka Architecture Overview

```text
Producer
    ↓
Kafka Topic
    ↓
Partitions
    ↓
Consumers
```

---

Example:

```text
Order Service
     ↓
orders Topic
     ↓
Payment Service

Inventory Service

Analytics Service
```

---

# 199. API To Kafka Flow

Traditional Flow:

```text
Client
   ↓
Order Service
   ↓
Database
```

---

Event Driven Flow:

```text
Client
   ↓
Order Service
   ↓
Database
   ↓
Kafka Event
```

---

Benefits:

```text
Multiple Consumers

Decoupling

Scalability
```

---

# 200. Event Publishing Pattern

Example:

```http
POST /orders
```

---

Order Created:

```text
Save Order
```

---

Then publish:

```json
{
  "eventType":"OrderCreated",
  "orderId":"123"
}
```

---

Consumers react independently.

---

# 201. Event Consumers

One event can have multiple consumers.

---

Example:

```text
OrderCreated
      ↓
Payment Service
      ↓
Inventory Service
      ↓
Notification Service
      ↓
Analytics Service
```

---

Benefits:

```text
Loose Coupling

Independent Scaling
```

---

# 202. Eventual Consistency

In event-driven systems:

```text
Updates Propagate Over Time
```

---

Example:

```text
Order Created
```

Immediately:

```text
Order Service Updated
```

A few milliseconds later:

```text
Inventory Updated

Analytics Updated

Notifications Sent
```

---

System becomes:

```text
Eventually Consistent
```

---

# 203. Event Schema Design

Events should contain:

```text
Event Type

Timestamp

Identifier

Business Data
```

---

Example:

```json
{
  "eventId":"123",
  "eventType":"PaymentCompleted",
  "timestamp":"2026-01-01T10:00:00Z",
  "paymentId":"456"
}
```

---

# 204. Event Versioning

Events evolve over time.

---

Versioning example:

```json
{
  "version":"2"
}
```

---

Benefits:

```text
Backward Compatibility

Consumer Stability
```

---

# 205. Delivery Guarantees

## At Most Once

```text
No Duplicates

Possible Data Loss
```

---

## At Least Once

```text
No Data Loss

Possible Duplicates
```

---

## Exactly Once

```text
Very Difficult

Usually Expensive
```

---

Most systems use:

```text
At Least Once
```

combined with:

```text
Idempotent Consumers
```

---

# 206. Event Replay

Major Kafka advantage.

---

Replay allows:

```text
Reprocessing

Recovery

Backfills

Auditing
```

---

Example:

```text
Historical Events
      ↓
New Consumer Group
      ↓
Replay Processing
```

---

# 207. Dead Letter Queues (DLQ)

Some events cannot be processed.

---

Examples:

```text
Invalid Payload

Missing Data

Schema Mismatch
```

---

Flow:

```text
Main Topic
     ↓
Retry
     ↓
Retry
     ↓
DLQ
```

---

Benefits:

```text
Fault Isolation

Operational Recovery
```

---

# 208. API Gateway + Kafka Pattern

Modern Architecture:

```text
Client
   ↓
API Gateway
   ↓
Microservice
   ↓
Kafka
   ↓
Consumers
```

---

Benefits:

```text
Scalable

Decoupled

Event Driven
```

---

# 209. Outbox Pattern

Common reliability pattern.

---

Problem:

```text
Database Updated

Kafka Publish Failed
```

---

Result:

```text
Data Inconsistency
```

---

Solution:

```text
Database Transaction
       ↓
Outbox Table
       ↓
Publisher
       ↓
Kafka
```

---

Benefits:

```text
Reliable Event Publication
```

---

# 210. Real World Example

Financial analytics platform:

```text
Trade Executed
      ↓
Kafka Event
      ↓
Risk Service

Compliance Service

Reporting Service

Analytics Service
```

---

Benefits:

```text
Independent Processing

Scalability

Replayability
```

---

# 211. Staff Engineer Discussion

## Why Use Kafka Instead Of Direct API Calls?

Kafka provides:

```text
Decoupling

Replayability

Fault Tolerance

Independent Scaling
```

---

## Why Are Idempotent Consumers Important?

Because duplicate events are expected in distributed systems.

---

## Why Is Replay Valuable?

Allows systems to recover and reprocess historical data without rebuilding source systems.

---

## What Is The Biggest Advantage Of Event Driven Systems?

```text
Loose Coupling
```

between producers and consumers.

---

# Quick Revision Sheet

```text
Event
=
Something Happened

EDA
=
Event Driven Architecture

Producer
=
Publishes Event

Consumer
=
Processes Event

Kafka
=
Event Streaming Platform

Replay
=
Reprocess Events

DLQ
=
Failed Events

Outbox Pattern
=
Reliable Publishing

At Least Once
=
Most Common

Idempotent Consumer
=
Duplicate Protection
```

---

# End Of Page 11

## Next Page

### Page 12 – Webhooks & External Integrations
# PART XII - WEBHOOKS & EXTERNAL INTEGRATIONS

# PAGE 12

---

# 212. What Is A Webhook?

A webhook is a mechanism that allows one system to notify another system when an event occurs.

---

Instead of:

```text
Consumer Constantly Polling
```

the producer pushes information automatically.

---

## Simple Flow

```text
Event Occurs
      ↓
Webhook Sent
      ↓
Consumer Receives Event
```

---

## Example

```text
Payment Completed
```

Webhook:

```json
{
  "event":"payment.completed",
  "paymentId":"12345"
}
```

---

# 213. Polling vs Webhooks

## Polling

Consumer repeatedly asks:

```text
Any Updates?
```

---

Example:

```http
GET /payments/status
```

every few seconds.

---

Problems:

```text
Wasteful

Higher Costs

Unnecessary Requests
```

---

## Webhooks

Producer notifies consumer.

---

Example:

```text
Payment Completed
      ↓
Webhook Sent
```

---

Benefits:

```text
Real Time

Efficient

Scalable
```

---

# 214. Why Use Webhooks?

Webhooks are ideal when:

```text
Events Are Infrequent

Consumers Need Real Time Updates

Polling Is Expensive
```

---

Common Examples:

```text
Stripe Payments

GitHub Events

Slack Notifications

Shopify Orders

Twilio Messages
```

---

# 215. Webhook Registration

Before receiving webhooks, consumers register a callback URL.

---

Example:

```json
{
  "url":"https://company.com/webhooks"
}
```

---

Producer stores:

```text
Webhook URL

Subscription Details

Event Types
```

---

# 216. Webhook Delivery Flow

```text
Event Occurs
      ↓
Webhook Producer
      ↓
HTTP POST
      ↓
Consumer Endpoint
      ↓
200 OK
```

---

Example:

```http
POST /webhooks
```

Payload:

```json
{
  "event":"order.created",
  "orderId":"123"
}
```

---

# 217. Webhook Payload Design

Good payloads contain:

```text
Event Type

Timestamp

Identifier

Business Data
```

---

Example:

```json
{
  "eventId":"evt-123",
  "eventType":"order.created",
  "timestamp":"2026-01-01T10:00:00Z",
  "orderId":"12345"
}
```

---

# 218. Why Webhooks Need Reliability

Consumers may be:

```text
Down

Slow

Unavailable
```

---

Example:

```text
Webhook Sent
      ↓
Consumer Down
      ↓
Delivery Failed
```

---

Need recovery mechanisms.

---

# 219. Retry Strategy

Most webhook providers retry failed deliveries.

---

Example:

```text
Attempt 1

Fail

Attempt 2

Fail

Attempt 3

Success
```

---

# 220. Exponential Backoff

Avoid:

```text
Retry Every Second
```

---

Prefer:

```text
1 Minute

2 Minutes

4 Minutes

8 Minutes
```

---

Benefits:

```text
Reduces Pressure

Improves Recovery
```

---

# 221. Dead Letter Queue For Webhooks

After repeated failures:

```text
Webhook
      ↓
Retry
      ↓
Retry
      ↓
DLQ
```

---

Benefits:

```text
No Data Loss

Operational Recovery
```

---

# 222. Security Challenges

Webhooks originate from external systems.

---

Questions:

```text
Who Sent This Request?

Was It Modified?

Can It Be Trusted?
```

---

Need:

```text
Authentication

Integrity Validation
```

---

# 223. HMAC Signatures

Most common webhook security mechanism.

---

Producer:

```text
Payload
      +
Secret Key
      ↓
HMAC Signature
```

---

Consumer:

```text
Recalculate Signature

Compare Values
```

---

If mismatch:

```text
Reject Request
```

---

# 224. Example HMAC Header

```http
X-Signature:
abc123xyz
```

---

Validation:

```text
Received Payload
      ↓
Generate Signature
      ↓
Compare
```

---

Benefits:

```text
Tamper Detection
```

---

# 225. Replay Attack Protection

Attacker may capture:

```text
Valid Webhook
```

and resend it later.

---

Example:

```text
Payment Completed Event
```

sent repeatedly.

---

Potential impact:

```text
Duplicate Processing

Duplicate Emails

Duplicate Payments
```

---

# 226. Replay Prevention

Common Techniques:

```text
Timestamp Validation

Nonce Validation

Event IDs

Idempotency
```

---

Example:

```json
{
  "eventId":"evt-123"
}
```

---

Consumer stores processed event IDs.

---

# 227. Idempotent Webhook Processing

Webhook systems must assume:

```text
Duplicate Delivery
```

---

Reason:

```text
Retries

Network Failures

Unknown Delivery Status
```

---

Consumer Logic:

```text
Event Already Processed?
```

If:

```text
Yes
```

Ignore.

---

If:

```text
No
```

Process.

---

# 228. Ordering Challenges

Webhook systems typically do not guarantee ordering.

---

Example:

```text
OrderUpdated

OrderCreated
```

may arrive:

```text
OrderUpdated First
```

---

Consumers should not assume:

```text
Strict Ordering
```

---

# 229. Webhooks vs Kafka

## Webhooks

```text
External Communication

HTTP Based

Internet Facing
```

---

## Kafka

```text
Internal Communication

Event Streaming

High Throughput
```

---

Common Architecture:

```text
Kafka
   ↓
Webhook Publisher
   ↓
External Consumers
```

---

# 230. Webhook Gateway Pattern

Large platforms often use:

```text
Kafka
   ↓
Webhook Service
   ↓
External Customers
```

---

Benefits:

```text
Isolation

Independent Scaling

Failure Handling
```

---

# 231. Monitoring Webhooks

Track:

```text
Success Rate

Failure Rate

Retry Count

Latency
```

---

Questions:

```text
Which Webhooks Failed?

How Long Is Delivery Taking?

Which Consumers Are Slow?
```

---

# 232. Real World Example

Payment Platform:

```text
Payment Completed
      ↓
Kafka Event
      ↓
Webhook Service
      ↓
Merchant Endpoint
```

---

Benefits:

```text
Real Time Notifications

Reliable Delivery

Scalable Architecture
```

---

# 233. Staff Engineer Discussion

## Why Use Webhooks Instead Of Polling?

Webhooks reduce unnecessary traffic and provide near real-time updates.

---

## Why Are Retries Required?

Consumers may be unavailable when events occur.

---

## Why Is Idempotency Critical?

Duplicate webhook deliveries are expected.

---

## Why Use HMAC?

To verify authenticity and detect tampering.

---

## Biggest Design Challenge?

```text
Reliable Delivery
```

to systems outside your control.

---

# Quick Revision Sheet

```text
Webhook
=
Push Notification Between Systems

Polling
=
Consumer Pulls Data

Webhook
=
Producer Pushes Data

HMAC
=
Integrity Validation

Retries
=
Reliability

DLQ
=
Failed Deliveries

Event ID
=
Duplicate Detection

Idempotency
=
Safe Retries

Kafka
=
Internal Events

Webhooks
=
External Events
```

---

# End Of Page 12

## Next Page

### Page 13 – Global Financial Analytics API Design
	