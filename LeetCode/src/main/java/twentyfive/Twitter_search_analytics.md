# Real-Time Search vs Streaming Analytics vs Batch Analytics

## Overview

Large-scale social media platforms (Twitter/X, Facebook, LinkedIn, YouTube, Instagram) don't have just a **Search System**.

They typically have **three independent data processing pipelines**, each solving a different problem.

1. **Search Pipeline**
   - Makes tweets/posts searchable.
   - Optimized for low-latency keyword search.

2. **Streaming Analytics Pipeline**
   - Computes real-time metrics.
   - Trending hashtags
   - Trending searches
   - Live dashboards
   - Breaking news
   - Live engagement

3. **Batch Analytics Pipeline**
   - Computes historical insights.
   - Weekly newsletters
   - Top creators
   - Most engaging users
   - Recommendation features
   - ML model training
   - Business analytics

The key idea is:

> **Search retrieves documents. Streaming computes live insights. Batch computes historical insights.**

---

# High-Level Architecture

```text
                    Tweet Created
                          |
                          v
                    Tweet Service
                          |
                          v
                        Kafka
              +-----------+------------+
              |                        |
              |                        |
              v                        v
   Search Index Consumer      Flink / Spark Streaming
              |                        |
              v                        v
      Elasticsearch         Trending Service
                                       |
                                       v
                                     Redis

              |
              |
              v
       Data Lake (S3/HDFS)
              |
              v
     Spark / Hadoop Batch Jobs
              |
      +-------+--------+--------+
      |                |        |
      v                v        v
Weekly Reports  Top Creators  ML Training
      |
      v
Dashboards / Newsletters / Recommendations
```

Notice that the same Kafka event is consumed by multiple independent systems.

---

# Search Pipeline

Purpose:

Make tweets searchable.

Architecture:

```text
Tweet Created
      |
      v
Tweet Service
      |
      v
Kafka
      |
      v
Search Index Consumer
      |
      v
Elasticsearch
```

## Step 1 — Tweet Service

When a user creates a tweet,

the Tweet Service:

- Validates request
- Stores tweet in the primary database
- Publishes a TweetCreated event to Kafka

The database remains the **Source of Truth**.

---

## Step 2 — Kafka

Kafka acts as the **event backbone**.

Instead of Tweet Service calling:

- Search
- Analytics
- Notifications
- Recommendation

directly,

it simply publishes one event.

Example:

```text
TweetCreated

{
    TweetId:12345
}
```

Multiple consumers independently subscribe.

Benefits:

- Loose coupling
- Independent scaling
- Retry support
- Replay capability

---

## Step 3 — Search Index Consumer

Consumes TweetCreated events.

For each tweet:

- Fetch tweet
- Tokenize
- Normalize
- Remove stop words
- Stem words (optional)
- Update Elasticsearch

Example

Tweet:

```
I love football
```

Indexed terms:

```
love

football
```

---

## Step 4 — Elasticsearch

Responsible for:

- Full-text search
- Inverted index
- Phrase search
- Filtering
- Ranking

It **does not compute analytics or trending topics**.

It is optimized for:

```
Find tweets containing:

football
```

---

# Streaming Analytics Pipeline

Searching answers:

```
Find tweets about football.
```

Streaming answers:

```
What is everyone talking about right now?
```

Architecture:

```text
Tweet Events
      |
      v
Kafka
      |
      v
Spark Streaming / Flink
      |
      v
Trending Service
      |
      v
Redis
```

---

# Why Spark Streaming / Flink?

Suppose Twitter receives:

```
600,000 tweets/minute
```

We want:

```
Top hashtags

Last 5 minutes
```

Scanning Elasticsearch or the database every second would be extremely expensive.

Instead,

every incoming tweet updates counters immediately.

Example:

```
#worldcup
```

↓

Increment counter

```
#football
```

↓

Increment counter

The streaming engine continuously maintains aggregates.

---

# Spark Streaming vs Flink

### Spark Streaming

- Micro-batch processing
- Slightly higher latency
- Excellent for analytics

---

### Flink

- True event-by-event processing
- Millisecond latency
- Better for real-time systems

Today,

many companies prefer Flink for low-latency streaming.

---

# Sliding Windows

Interview Question

Why doesn't

```
#COVID
```

stay trending forever?

Trending is based on recent activity.

Example windows:

- Last 5 minutes
- Last 15 minutes
- Last 1 hour

As events fall outside the window,

they automatically expire.

Example:

```
12:00

#football
```

```
12:02

#football
```

```
12:10

#football
```

Using a 5-minute window,

only tweets between

```
12:05–12:10
```

are counted.

This keeps trends fresh.

---

# Trending Service

Streaming engines compute aggregates.

Trending Service exposes APIs.

Responsibilities:

- Rank hashtags
- Regional trends
- Country trends
- Trending searches
- Mobile API

---

# Why Redis?

Trending data is read millions of times.

Redis stores:

- Top hashtags
- Trending searches
- Popular topics

Reads become:

```
< 1 ms
```

instead of querying Spark every time.

---

# Batch Analytics Pipeline

Streaming computes **live** metrics.

Batch computes **historical** metrics.

Architecture:

```text
Tweet Events
      |
      v
Kafka
      |
      v
Data Lake (S3/HDFS)
      |
      v
Spark / Hadoop Batch Jobs
      |
      v
Analytics DB
      |
      v
Dashboards
Recommendations
Newsletters
```

---

# Why Data Lake?

Streaming engines usually maintain only recent state.

Historical events are stored in:

- Amazon S3
- HDFS
- Cloud Storage

Benefits:

- Historical analytics
- Compliance
- Machine Learning
- Reporting
- Replay

---

# Example — Weekly Top Creators

Every tweet stores:

- AuthorId
- Likes
- Retweets
- Replies
- Timestamp

Weekly Spark Job:

```text
Read Tweets

↓

GROUP BY AuthorId

↓

Compute Likes

↓

Compute Retweets

↓

Compute Replies

↓

Rank Authors

↓

Top 100 Creators
```

Store results in:

- Redis
- Analytics DB

Now the application instantly shows:

```
Top Creators This Week
```

---

# Example — Weekly Newsletter

Every Sunday,

a scheduled Spark job computes:

- Most liked tweet
- Most retweeted tweet
- Most engaging topic
- New followers
- Weekly impressions

Stores:

```
User Weekly Summary
```

Email Service later sends:

```
Your Weekly Highlights

✓ Top Tweet

✓ +200 Followers

✓ 1.2M Impressions
```

---

# Recommendation Features

Recommendation systems need historical behavior.

Spark batch jobs compute:

- Favorite topics
- Follow patterns
- User interests
- Engagement score
- Long-term activity

These become ML features.

---

# ML Training

Historical events are used to train recommendation models.

Example:

```
Last 6 Months

↓

Likes

↓

Retweets

↓

Clicks

↓

Train Recommendation Model
```

Training is typically offline because it is computationally expensive.

---

# Scheduled Batch Jobs

Typical schedules:

### Every Hour

- Spam reports
- Trending summaries

### Every Day

- Daily engagement reports
- Content quality metrics

### Every Week

- Top creators
- Weekly newsletters
- Weekly recommendations

### Every Month

- Business reports
- Growth analytics
- ML retraining

Production systems usually use:

- Apache Airflow
- Argo Workflows
- Dagster

instead of plain cron.

---

# Streaming vs Batch

| Feature | Streaming | Batch |
|----------|-----------|-------|
| Latency | Milliseconds–Seconds | Minutes–Hours |
| Data | Live Events | Historical Data |
| Trigger | Every Event | Scheduled |
| Storage | Recent State | Data Lake |
| Use Cases | Trending, Live Dashboards | Reports, Newsletters, ML |

---

# Storage Choices

| Technology | Why Used |
|------------|----------|
| **Primary Database** | Source of truth for tweets |
| **Kafka** | Durable event backbone connecting producers and consumers |
| **Elasticsearch** | Full-text search using inverted indexes |
| **Spark Streaming / Flink** | Real-time event processing and streaming analytics |
| **Redis** | Trending topics, hot search results, counters |
| **Data Lake (S3/HDFS)** | Historical analytics, ML training, business intelligence |

---

# Why Not Use Elasticsearch for Trending?

Interview Question:

Why not simply run:

```sql
SELECT hashtag, COUNT(*)
GROUP BY hashtag
```

on Elasticsearch?

Answer:

Elasticsearch is optimized for document retrieval.

Running expensive aggregations continuously over billions of tweets would be inefficient.

Streaming engines continuously maintain aggregates as events arrive.

Instead of:

```
Query

↓

Count
```

they perform:

```
Tweet Arrives

↓

Increment Counter
```

This is much cheaper.

---

# Complete Data Platform

```text
                     Tweet Created
                           |
                           v
                    Tweet Service
                           |
                           v
                         Kafka
          +----------------+----------------+
          |                                 |
          |                                 |
          v                                 v
 Search Index Consumer          Spark Streaming / Flink
          |                                 |
          v                                 v
   Elasticsearch              Trending Service
                                             |
                                             v
                                           Redis

          |
          |
          v
   Data Lake (S3/HDFS)
          |
          v
 Spark / Hadoop Batch Jobs
          |
  +-------+--------+----------------+
  |                |                |
  v                v                v
Weekly Reports  Top Creators  ML Training
  |                |                |
  +----------------+----------------+
                   |
                   v
 Dashboards / Recommendations / Newsletters
```

---

# Interview Takeaways

Search systems in large social media platforms are **not just search engines**.

They consist of multiple independent pipelines:

### Search Pipeline

Purpose:

- Full-text search
- Low latency retrieval
- Ranking

Technology:

- Elasticsearch

---

### Streaming Pipeline

Purpose:

- Trending topics
- Live engagement
- Real-time analytics

Technology:

- Kafka
- Spark Streaming / Flink
- Redis

---

### Batch Pipeline

Purpose:

- Weekly reports
- Top creators
- Newsletters
- Recommendation features
- ML training

Technology:

- Data Lake
- Spark
- Hadoop
- Airflow

---

# Key Design Principle

A production-scale social media platform separates concerns:

- **Search** → Retrieve documents.
- **Streaming** → Compute live insights.
- **Batch** → Compute historical insights.

Kafka acts as the central event backbone, allowing all three pipelines to process the same events independently without coupling them to the Tweet Service.