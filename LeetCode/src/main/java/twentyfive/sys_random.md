# Audible User Library Service Design

## Overview

The **User Library Service** is responsible for maintaining the list of audiobooks that a user owns. It **does not** handle payments, purchases, invoices, or refunds. Its primary responsibility is to provide a fast and scalable way for users to view, search, and download the books they have purchased across all their devices.

The system is separated into three independent services:

- **Purchase Service** – Handles purchases, payments, refunds, and order management.
- **Library Service** – Maintains ownership information for each user.
- **Book Service** – Maintains global book metadata such as title, author, narrator, duration, cover image, and audio location.

This separation follows the **Single Responsibility Principle**, making each service easier to scale, maintain, and deploy independently.

---

# Why Event-Driven Instead of Synchronous?

After a successful payment, the Purchase Service **does not synchronously call the Library Service**. Instead, it publishes a **BookPurchased** event to Kafka (or another event bus).

This design keeps the purchase path lightweight and avoids introducing unnecessary synchronous dependencies.

Advantages include:

- Avoids adding Library Service to the critical purchase path.
- Reduces latency of the purchase request.
- Prevents tight coupling between services.
- Allows Purchase and Library Services to scale independently.
- Enables additional consumers (Analytics, Notifications, Recommendations, etc.) without modifying the Purchase Service.
- Eventual consistency is acceptable because a slight delay before the purchased book appears in the user's library is not business critical.

---

# High-Level Architecture

```text
                         Mobile / Web Client
                                 |
                           API Gateway
                                 |
               -----------------------------------
               |                                 |
        Purchase Service                 Library Service
               |                                 |
       Payment Gateway                    Redis Cache
               |                                 |
       Purchase Database                 Library Database
               |                                 |
               |                          Book Service
               |                                 |
               +------------ Kafka --------------+
                            |
                     BookPurchased Event
                            |
      ------------------------------------------------------
      |                   |               |                 |
Library Service     Analytics Service  Notification   Recommendation
                                         Service         Service
```

---

# Purchase Flow

```text
User
 |
 | POST /purchases
 |
 v
Purchase Service
 |
 | Process Payment
 v
Payment Gateway
 |
 | Payment Successful
 v
Store Purchase Record
(Purchase Database)
 |
 | Publish BookPurchased Event
 v
Kafka / Event Bus
```

### Purchase Database

| purchaseId | userId | bookId | amount | status | purchaseTime |
|------------|--------|--------|--------|---------|--------------|

The Purchase Service is now done.

---

# Event Consumers

The `BookPurchased` event can be consumed by multiple independent services.

```text
BookPurchased
      |
      +-------------------------------+
      |        |        |             |
      |        |        |             |
 Library  Analytics  Notification  Recommendation
 Service    Service      Service        Service
```

Each consumer processes the event independently.

---

# Library Update Flow

The Library Service consumes the `BookPurchased` event.

It inserts an ownership record into the Library Database.

### Library Table

| userId | bookId | purchaseTime |
|---------|--------|--------------|

Notice that this table **only stores ownership**.

It does **not** store:

- title
- author
- narrator
- duration
- cover image

Those belong to the **Book Service**, which acts as the single source of truth for book metadata.

---

# Cache Invalidation

After updating the Library Database, the Library Service invalidates the user's Redis cache.

```text
BookPurchased Event
        |
        v
Update Library Database
        |
        v
DELETE Redis Key

library:user123
```

The next read will rebuild the cache using fresh data.

TTL still exists as a safety mechanism, but cache invalidation is the primary strategy.

---

# Read Flow

```text
User
 |
 | GET /users/123/library
 |
 v
Library Service
 |
 v
Redis
 |
 +------------------------------------+
 |                                    |
 | Cache Hit                          | Cache Miss
 |                                    |
 v                                    v
Return Response                Library Database
                                      |
                                      | Returns Book IDs
                                      v
                               Book Service
                                      |
                                      | Returns Metadata
                                      v
                           Assemble Response
                                      |
                           Store in Redis
                                      |
                                      v
                              Return Response
```

---

# Example Response

```json
[
  {
    "bookId": 10,
    "title": "Harry Potter",
    "author": "J.K. Rowling",
    "coverImage": "...",
    "duration": "8h 15m"
  },
  {
    "bookId": 20,
    "title": "Atomic Habits",
    "author": "James Clear",
    "coverImage": "...",
    "duration": "5h 30m"
  }
]
```

---

# Why Cache the Entire Response?

Instead of caching only:

```text
Book10
Book20
Book35
```

cache the complete assembled response:

```json
[
  {
    "bookId": 10,
    "title": "Harry Potter",
    "author": "J.K. Rowling",
    "coverImage": "..."
  },
  {
    "bookId": 20,
    "title": "Atomic Habits",
    "author": "James Clear",
    "coverImage": "..."
  }
]
```

Benefits:

- No Book Service call on cache hits.
- Lower latency.
- Fewer network calls.
- Better overall throughput.

---

# Design Decisions

| Decision | Reason |
|----------|--------|
| Separate Purchase & Library Services | Different business responsibilities |
| Event-driven communication | Avoid synchronous dependencies and tight coupling |
| Kafka/Event Bus | Allows multiple downstream consumers |
| Library DB stores only ownership | Avoid duplication of metadata |
| Book Service owns metadata | Single source of truth |
| Redis Cache | Reduce database load |
| Explicit cache invalidation | Newly purchased books appear immediately |
| Partition by `userId` | Dominant query is "Show me my library" |
| Eventual consistency | Acceptable for this use case |

---

# Interview Summary

> I would separate purchasing from the library because they represent different business domains. The Purchase Service handles payment processing, creates the purchase record, and publishes a **BookPurchased** event. The Library Service consumes that event, records ownership in the Library Database, and invalidates the user's Redis cache. When the user requests their library, the Library Service first checks Redis. On a cache miss, it loads the user's owned book IDs from the Library Database, retrieves book metadata from the Book Service, assembles the response, caches it, and returns it. This event-driven architecture avoids synchronous dependencies, reduces coupling, enables independent scaling, and allows multiple downstream services such as Analytics, Notifications, and Recommendations to consume the same purchase event. Eventual consistency is acceptable because a small delay before the purchased book appears in the user's library is not business critical.