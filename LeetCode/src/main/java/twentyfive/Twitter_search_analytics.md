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

# Machine Learning Models in Large-Scale Social Media Platforms

## Overview

A common misconception is that platforms like **Twitter/X, Facebook, Instagram, LinkedIn, and YouTube** train **one ML model per user**.

**They do not.**

Instead:

- They train **many different models**, each solving a different business problem.
- Each model is trained using data from **millions or billions of users**.
- During inference, the model receives user-specific features, tweet/post features, query features, or context features to produce a personalized prediction.

**Key Idea**

> **Many models for many business problems, NOT one model per user.**

---

# High-Level Architecture

```text
                    Data Lake
                         |
                         |
              Spark Feature Engineering
                         |
                         |
                  Feature Store
                         |
      +------------------+------------------+
      |                  |                  |
      |                  |                  |
      v                  v                  v
 User Models       Content Models     Search Models
      |                  |                  |
      +------------------+------------------+
                         |
                   Online Inference
                         |
     +----------+---------+----------+----------+
     |          |                    |          |
     v          v                    v          v
 Timeline    Search          Recommendations  Ads
```

The **Feature Store** provides reusable features for multiple models.

---

# 1. User Models

These models predict something **about a user**.

---

## Churn Prediction

### Business Question

Will this user stop using Twitter?

### Input Features

- Login frequency
- Session duration
- Tweets per week
- Likes
- Retweets
- Notifications opened

### Output

```text
Probability(User Churns)
```

### Use Case

Marketing campaigns

Retention emails

Push notifications

---

## User Interest Model

### Business Question

What topics does the user enjoy?

### Input

- Likes
- Retweets
- Search history
- Following list
- Watch history

### Output

```text
Sports = 0.95

Politics = 0.10

Technology = 0.80
```

These interests become part of the user's profile.

---

## Notification Model

### Business Question

Should we notify this user?

### Input

- Previous notification clicks
- Time of day
- Activity history

### Output

```text
Probability(User Opens Notification)
```

---

# 2. Content (Tweet/Post) Models

These models predict something **about the content itself**.

---

## Engagement Prediction

### Business Question

How popular will this tweet become?

### Input

- Author followers
- Tweet text
- Topic
- Time posted
- Hashtags

### Output

```text
Expected Likes

Expected Retweets
```

---

## Spam Detection

### Business Question

Is this tweet spam?

### Input

- Tweet text
- URLs
- Mentions
- User reputation

### Output

```text
Spam Score
```

---

## Toxicity Detection

### Business Question

Does this content contain hate speech or abusive language?

### Output

```text
Toxicity Score
```

---

## Language Detection

### Business Question

What language is this tweet?

### Output

```text
English

Spanish

Hindi

French
```

---

## Fake News Detection

### Input

- Tweet
- Source
- Propagation graph
- Fact-check labels

### Output

```text
Probability(Misinformation)
```

---

# 3. User-Tweet Models

These are the most common recommendation models.

Input

```text
(User,

Tweet)
```

↓

Output

```text
Probability(User Likes Tweet)
```

---

## Timeline Ranking

Business Question

Which tweets should appear first?

Features

User Features

- Interests
- Activity
- Following

Tweet Features

- Likes
- Retweets
- Freshness
- Author Quality

Output

```text
Ranking Score
```

---

# 4. User-User Models

Business Question

Should Alice follow Bob?

Input

```text
(User A,

User B)
```

↓

Output

```text
Follow Probability
```

Used for:

- People You May Know
- Follow recommendations

---

# 5. Tweet-Tweet Models

Business Question

What tweets are similar?

Input

```text
Tweet A

Tweet B
```

↓

Output

```text
Similarity Score
```

Used for:

- Related Tweets
- Similar Content
- Recommendations

---

# 6. Query-Tweet Models

Used by Search.

Input

```text
(Query,

Tweet)
```

↓

Output

```text
Relevance Score
```

Example

```text
football

+

Tweet123

↓

0.94
```

---

# 7. User-Query-Tweet Models

Personalized Search.

Input

```text
(User,

Query,

Tweet)
```

↓

Output

```text
Probability(User Clicks Tweet)
```

Different users receive different rankings.

---

# 8. Advertisement Models

Business Question

Which advertisement should be shown?

Input

```text
(User,

Advertisement)
```

↓

Output

```text
Click Probability
```

---

# 9. Community Detection Models

Business Question

Which communities exist?

Input

```text
Social Graph
```

↓

Output

```text
Sports Community

Technology Community

Politics Community
```

Used for:

- Recommendations
- Feed ranking
- Ads
- Suggested groups

---

# 10. Trending Prediction

Business Question

Will this topic become trending?

Input

- Tweet velocity
- Retweet velocity
- Geographic spread
- Likes per minute

Output

```text
Trending Score
```

---

# 11. Search Ranking Model

Business Question

Which search result should appear first?

Input

```text
(User,

Query,

Tweet)
```

Features

- BM25
- Freshness
- Likes
- Friend affinity
- Author quality
- Previous clicks

Output

```text
Search Ranking Score
```

---

# 12. Recommendation Model

Business Question

Which tweets should appear in the user's timeline?

Input

```text
(User,

Tweet)
```

Output

```text
Recommendation Score
```

---

# Feature Engineering

Raw events are transformed into reusable ML features.

Example:

Raw Event

```text
User123 liked Tweet456
```

Spark computes:

| Feature | Example |
|----------|----------|
| LikesLast7Days | 180 |
| AvgSessionTime | 24 min |
| FavoriteTopic | Sports |
| ActiveHours | 7 PM–10 PM |
| AvgRetweetsReceived | 120 |
| FriendAffinityScore | 0.84 |
| CTR | 0.37 |

These features are stored in a **Feature Store**.

---

# Why Feature Store?

Without a Feature Store:

Every model computes features independently.

With a Feature Store:

```text
            Data Lake
                 |
                 v
      Spark Feature Engineering
                 |
                 v
          Feature Store
                 |
      +----------+----------+
      |          |          |
      v          v          v
 Timeline   Search     Ads Model
   Model     Model
```

All models reuse the same features.

Benefits:

- No duplicate computation
- Consistent features
- Easier maintenance
- Faster model development

---

# Training Pipeline

```text
               Kafka
                  |
                  v
            S3 Data Lake
                  |
                  v
     Spark Feature Engineering
                  |
                  v
           Feature Store
                  |
                  v
          ML Training Jobs
                  |
                  v
          Model Registry
                  |
                  v
         Online Inference Service
                  |
                  +-----------------------------+
                  |             |               |
                  v             v               v
            Timeline      Search Ranking    Recommendations
```

---

# Important Interview Question

## Do we train one model per user?

**No.**

A common misconception is that every user gets their own ML model.

Instead:

- Train one **global model** for each business problem.
- During inference, the model receives:
  - User features
  - Tweet features
  - Query features
  - Context features

Different inputs produce personalized predictions.

Example:

Alice

```text
(User,

Tweet)
```

↓

```text
0.95
```

Bob

```text
(User,

Same Tweet)
```

↓

```text
0.15
```

The **same model** produced different scores because the inputs were different.

---

# Summary

| Model | Input | Output | Business Use Case |
|--------|-------|--------|-------------------|
| Churn Prediction | User | Churn Probability | User retention |
| User Interest | User | Interest Scores | Personalization |
| Notification Model | User | Open Probability | Push notifications |
| Engagement Prediction | Tweet | Expected Likes | Ranking |
| Spam Detection | Tweet | Spam Score | Moderation |
| Toxicity Detection | Tweet | Toxicity Score | Content safety |
| Fake News Detection | Tweet | Misinformation Score | Trust & Safety |
| Timeline Ranking | User + Tweet | Ranking Score | Home feed |
| Follow Recommendation | User + User | Follow Probability | People You May Know |
| Tweet Similarity | Tweet + Tweet | Similarity Score | Related content |
| Search Ranking | User + Query + Tweet | Ranking Score | Search results |
| Ad Ranking | User + Ad | CTR Probability | Advertising |
| Community Detection | Social Graph | Communities | Recommendations |
| Trending Prediction | Tweet Stream | Trending Score | Trending topics |

---

# Key Takeaway

Large-scale platforms do **not** train one model per user.

Instead:

- Train **many task-specific global models**.
- Reuse features through a **Feature Store**.
- Personalization comes from **different input features**, not from separate models for each user.


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

"Autocomplete is usually backed by a dedicated prefix index such as a Trie or FST rather than the inverted index. During indexing, when new posts, hashtags, pages, or users are added, the indexing pipeline extracts candidate terms and updates both the search index and the autocomplete index. The Trie efficiently retrieves all terms matching a prefix, and those candidates are then ranked using popularity, recent trends, and personalization before being returned to the user.

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
