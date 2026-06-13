# Design Twitter / X - Part 1
## Requirements, Capacity Estimation, High-Level Architecture, Core Data Services

---

# 1. Problem Statement

Design a Twitter-like social networking system where users can:

- Create short text posts (tweets)
- Upload photos and videos
- Follow and unfollow users
- View a personalized home timeline
- Like, comment, and share tweets
- Search tweets
- View trending topics
- Receive notifications and recommendations

The system must support hundreds of millions of users with low latency and high availability.

---

# 2. Requirement Clarification

## Functional Requirements (FR)

### User Features

- User registration and login
- User profile management
- Follow/unfollow other users

---

### Tweet Features

Users should be able to:

- Create tweets
- Delete tweets
- View tweets

Tweets can contain:

- Text
- Images
- Videos

---

### Timeline Features

The system should generate:

- Home timeline
  - Tweets from users I follow
  - Ordered by relevance and recency

- User timeline
  - Posts created by a specific user

---

### Social Features

Users should be able to:

- Like a tweet
- Comment on a tweet
- Retweet/share a tweet

---

### Extended Features

- Search tweets
- Trending topics
- Personalized recommendations
- Notifications
- Re-engagement for inactive users

---

# 3. Non-Functional Requirements (NFR)

## High Availability

Twitter is a global consumer platform.

The system should continue operating even if individual components fail.

---

## Low Latency

Timeline rendering should be very fast.

Target:

- Timeline read latency: ~100-200ms

---

## Massive Scale

Example assumptions:

- 1 Billion registered users
- 200 Million daily active users
- 100 Million tweets created every day

---

## Read Heavy System

Twitter has far more reads than writes.

Users read timelines many times per day but create relatively few tweets.

Therefore, the architecture is optimized for:

- Fast reads
- Caching
- Pre-computation where possible

---

## Consistency

The system can tolerate eventual consistency.

Examples:

Acceptable:
- Like count delayed by a few seconds
- A new tweet appearing slightly late

Not acceptable:
- Losing a user's tweet

Availability and scalability are prioritized over strong consistency.

---

# 4. Capacity Estimation

## Tweet Storage

Assumptions:

- 100 million tweets/day
- Tweet length = 140 characters
- Unicode = 2 bytes per character
- Metadata = ~30 bytes

Size per tweet:

140 * 2 + 30 = 310 bytes

Daily storage:

100,000,000 * 310 bytes

≈ 30 GB/day

---

## Media Storage

Assume:

- 20% of tweets contain images
- 10% contain videos

Media dominates storage.

Approximation:

- Images: ~4 TB/day
- Videos: ~20 TB/day

Total media storage:

≈ 24 TB/day

---

## Read Traffic Estimation

Assumptions:

- 200M DAU
- Each user visits timeline 2 times/day
- Visits 5 other user profiles/day
- Each page displays 20 tweets

Requests/day:

200M * (2 + 5) * 20

≈ 28 Billion tweet views/day

QPS:

28 Billion / 86400

≈ 325K reads/second

This demonstrates why the system is heavily optimized for reads.

---

# 5. High-Level Architecture

                      Client
                         |
                    Load Balancer
                         |
                    API Gateway
                         |
        ------------------------------------------------
        |                 |               |
        |                 |               |
    User Service     Tweet Service   Timeline Service
        |                 |               |
      MySQL          Cassandra        Redis Cache
                          |
                       Kafka
                          |
       ------------------------------------------------
       |                    |                         |
 Feed Processor      Search Pipeline            Analytics
       |                    |                         |
 Cassandra         Elasticsearch             Hadoop/Spark


---

# 6. User Service

## Responsibilities

Stores:

- User profile
- Authentication information
- Account metadata

Example:

User:
{
    userId,
    name,
    email,
    preferences
}

---

## Database Choice

MySQL Cluster

Reason:

- User data is relational
- Requires transactional consistency
- Supports indexing and replication

---

# 7. Social Graph Service

## Purpose

Stores relationships:

User A follows User B


Example:

User 1001 follows:

[
  2002,
  3003,
  5005
]

---

## Data Model

FollowerId → FollowingId

or

UserId → List of Followers


---

## Database Choices

Possible options:

- Graph database
- Cassandra
- Sharded MySQL

Requirements:

- Very high read throughput
- Fast lookup:

"Who does this user follow?"

This information is heavily used during timeline generation.

---

# 8. Tweet Service

## Responsibilities

Handles:

- Create tweets
- Delete tweets
- Fetch user tweets

---

## Database Choice

Cassandra

Reasons:

- Extremely high write throughput
- Horizontal scaling
- Distributed architecture
- High availability
- Eventual consistency is acceptable

---

## Partition Strategy

Partition Key:

UserId

Clustering Key:

Timestamp

Example:

User 123

10:01 -> Tweet A

10:05 -> Tweet B

10:15 -> Tweet C


Allows efficient retrieval of a user's recent tweets.

---

# 9. Media Service

Tweets containing images/videos are stored separately.

Why?

Media files are very large and would make tweet storage inefficient.

---

## Flow


User Upload
     |
 Asset Service
     |
 Object Storage (S3)
     |
 CDN
     |
 End Users


---

## CDN Benefits

Frequently accessed images/videos remain close to users.

Example:

A celebrity photo may be cached globally.

Old content can be evicted.

If an old image becomes popular again:

CDN miss
    |
Fetch from S3
    |
Store in CDN cache

---

# 10. Event-Driven Architecture

Tweet creation generates an event:

Tweet Created Event
          |
        Kafka
          |
 -------------------------------------------------
 |                    |                          |
Timeline          Search                   Analytics
Processor         Indexing                 Pipeline


---

## Why Kafka?

A background worker could also perform asynchronous processing.

Kafka is preferred because it provides:

- Durable event storage
- Replayability
- Multiple independent consumers
- Backpressure handling
- Consumer-specific offsets
- Decoupling between producer and consumers


Example:

Today:
- Timeline Service
- Search Service

Tomorrow:
- ML Recommendation Service

A new consumer can replay historical tweet events.

---

# 11. Key L6 Trade-Off Talking Points

## Why Cassandra instead of MySQL?

Because tweets require:

- Massive write throughput
- Horizontal scaling
- Global availability

The trade-off is eventual consistency.

---

## Why Redis?

Twitter is read-heavy.

Caching prevents repeatedly generating the same timelines and reduces database load.

---

## Why not store media in Cassandra?

Large binary objects increase storage and replication costs.

Object storage like S3 is optimized for large files.

---

## Why accept eventual consistency?

User experience is more important than strict consistency.

A like count being delayed by a second is acceptable.

A tweet being permanently lost is not.

---

# Part 1 Summary

Core Components:

- User Service → MySQL
- Graph Service → Graph DB / Cassandra / Sharded MySQL
- Tweet Service → Cassandra
- Media Service → S3 + CDN
- Timeline Cache → Redis
- Event Backbone → Kafka
- Search → Elasticsearch
- Analytics → Spark/Hadoop

The foundation of the system is designed around one major principle:

Twitter is a read-heavy system, so we optimize reads using caching, pre-computation, and asynchronous event processing.

# Design Twitter / X - Part 2
# Timeline Generation, Fan-out Strategy, Celebrity Problem, Live Users

---

# 1. The Core Challenge

Twitter is a read-heavy system.

A user opens the home timeline and expects to immediately see:

- Tweets from people they follow
- Recent tweets
- Relevant tweets

The main question is:

When a user creates a tweet, how should it reach their followers?

There are three approaches:

1. Pull Model (Fan-out on Read)
2. Push Model (Fan-out on Write)
3. Hybrid Model

---

# 2. Pull Model (Fan-out on Read)

## Idea

Do not distribute the tweet when it is created.

Instead, when a user opens their timeline, generate the feed at that time.

Example:

User A follows:

- User B
- User C
- User D


When User A opens Twitter:


Timeline Service
       |
       |
Graph Service
(Get users I follow)
       |
       |
Tweet Service
(Get recent tweets from B,C,D)
       |
       |
Merge + Sort by timestamp/relevance
       |
       |
Return timeline


---

## Advantages

- Very cheap writes
- No unnecessary work for inactive users
- Easy to handle users with millions of followers

---

## Problems

### Higher read latency

Every timeline request requires:

- Fetching follower graph
- Fetching tweets
- Merging timelines
- Ranking results

This increases response time.

---

### Empty or repeated refreshes

Many users continuously refresh their feed.

Even if there are no new tweets:

- The server still has to compute the timeline again.
- This creates unnecessary load.

This was one of the issues identified in the notes.

---

# 3. Push Model (Fan-out on Write)

## Idea

When a user creates a tweet, immediately push it to every follower's timeline.

Example:

User B creates a tweet


Tweet Service
       |
      Kafka
       |
Feed Processor
       |
Find followers using Graph Service
       |
Write tweet ID into each follower's timeline cache


Now when a follower opens Twitter:

Timeline Service
       |
      Redis
       |
Return cached timeline


---

## Advantages

Very fast reads.

Timeline generation has already happened.

Redis can directly return:

User Timeline Cache:

User A:

Tweet 101
Tweet 102
Tweet 103


---

## Problems

### Celebrity / Hot Key Problem

Imagine a celebrity with:

100 million followers.


One tweet causes:

100 million writes.

This creates:

- Huge Kafka traffic
- Massive fan-out load
- Large Redis writes
- Backlogs in feed processors


This is the famous "hot celebrity" problem.

---

# 4. Hybrid Approach (Used by Twitter)

The best approach is to combine both models.

---

## Normal Users

Users with a small or medium number of followers:

Use:

Fan-out on Write


Flow:


Tweet Created
      |
    Kafka
      |
Feed Processor
      |
Graph Service
      |
Push tweet IDs to followers' Redis timelines


Why?

Because most users have a reasonable number of followers.

The write amplification is manageable.

The benefit is very low read latency.

---

## Celebrity Users

For users with millions of followers:

Use:

Fan-out on Read


Do NOT push their tweets to every follower.

Instead:

When a follower opens the timeline:

Timeline Service
       |
Get celebrity list from Graph Service
       |
Get latest celebrity tweets from Tweet Service
       |
Merge with user's normal cached timeline
       |
Return result


This avoids millions of writes.

---

# 5. Handling Active vs Passive Users

Another optimization from the notes:

Not all users are equally important.

---

## Active Users

Users currently online:

We can provide near real-time updates.

Possible approaches:

- WebSocket connection
- Long polling


Flow:

New Tweet
     |
 Kafka
     |
 Live User Service
     |
 WebSocket
     |
 Push notification to active client


---

## Passive Users

Users who have not logged in recently.

Do not spend resources continuously updating their timelines.

Their timelines can be generated when they return.

---

# 6. Timeline Cache using Redis

Redis stores recent timeline data.

Example:

Key:

UserId


Value:

[
 TweetId1,
 TweetId2,
 TweetId3
]


Benefits:

- Extremely fast reads
- Reduces Cassandra queries
- Reduces expensive timeline computation


---

# 7. Why Redis TTL?

A timeline changes frequently for active users.

Older timelines become less valuable.

We can:

- Keep recent timeline entries in Redis
- Apply TTL
- Evict old data automatically


This saves memory.

---

# 8. Timeline Archival

Old timeline data does not need to remain in Redis forever.

An archival service can store older timeline data.

Example:

Redis:
- Last few days of feed

Long-term storage:
- Cassandra aggregated timeline tables


When a user scrolls far back:

Timeline Service
       |
Archival Service
       |
Cassandra
       |
Return old posts


---

# 9. Why Store Tweet IDs Instead of Full Tweets in Redis?

Store:

User123 Timeline:

[
  Tweet101,
  Tweet202,
  Tweet303
]


Instead of storing full tweet objects.

Reasons:

- Smaller cache footprint
- Avoid duplicate data
- Tweet details can be fetched separately

---

# 10. L6 Trade-off Discussion

## Why not always use Push?

Because of celebrities.

A user with 100M followers would cause enormous write amplification.

---

## Why not always use Pull?

Because reads dominate writes.

Generating timelines repeatedly causes high latency and compute cost.

---

## Why Hybrid?

It balances:

- Low read latency
- Controlled write amplification
- Efficient handling of celebrities

---

# Key Takeaways

The most important Twitter design decision is timeline generation.

Normal users:
    Fan-out on Write

Celebrity users:
    Fan-out on Read

Active users:
    Real-time updates using WebSockets

Passive users:
    Generate timeline on demand

Recent feeds:
    Redis cache

Historical feeds:
    Cassandra archival storage

Kafka acts as the asynchronous backbone connecting Tweet Service, Feed Processor, and real-time notification systems.

# Design Twitter / X - Part 3
# Likes, Comments, Activity Tracking, User Profiles, and ML Recommendation Pipeline

---

# 1. Social Interaction System

Users can interact with tweets through:

- Likes
- Comments
- Shares / Retweets
- Clicks
- Time spent viewing content

These interactions are extremely valuable because they describe user preferences.

Example:

User 123:
- Likes sports posts
- Watches football videos completely
- Follows sports journalists

The system can use this information to personalize future content.

---

# 2. Like Service

## Responsibilities

Handles:

- Like a tweet
- Remove a like
- Get total likes on a tweet
- Determine whether a user has liked a tweet

---

## Data Model

Like Table:

Composite Key:

(PostId, UserId)

Example:

PostId | UserId
-------|--------
1001   | 501
1001   | 800
1002   | 900


Why this design?

- Prevents duplicate likes
- Efficient lookup:
  "Has this user liked this post?"

---

## Database Choice

Cassandra

Reason:

- Very high write throughput
- Massive number of user interactions
- Horizontal scalability

---

## Like Count Optimization

The exact likes are stored in Cassandra.

However, displaying like counts requires extremely frequent reads.

Example:

A celebrity post may receive millions of reads.

Instead of counting every time:

Cassandra:

(PostId, UserId)


Maintain a Redis cache:

Key:
PostId

Value:
LikeCount


Example:

Post 1001 → 1,500,000 likes


Benefits:

- O(1) reads
- Reduced Cassandra load
- Low latency UI updates

---

# 3. Comment Service

## Responsibilities

Handles:

- Create comments
- Retrieve comments for a post
- Delete comments

---

## Data Model

Comment Table:

Partition Key:
PostId

Clustering Key:
Timestamp

Example:

Post 1001:

10:00 → Comment A
10:02 → Comment B
10:05 → Comment C


Benefits:

- Efficient retrieval of comments sorted by time
- Supports very high write volume

---

# 4. Activity Tracking System

One of the most important systems in Twitter.

Every user action generates an event.

Examples:

- Like
- Comment
- Share
- Follow
- Click
- View
- Watch time


---

# Event Model


Activity Event:

{
    userId,
    actionType,
    contentId,
    timestamp
}


Example:

{
    userId: 123,
    actionType: "LIKE",
    postId: 567,
    timestamp: 123456789
}

---

# Event Streaming Pipeline


User Action
      |
 Like / Comment Service
      |
    Kafka
      |
 Activity Tracker Consumer
      |
 Cassandra


---

## Why Kafka?

The interaction path must remain fast.

We should not block the user request while:

- Updating analytics
- Running ML pipelines
- Generating reports

Kafka allows asynchronous processing.

---

# 5. User Interest Profile Generation

Raw activity data is valuable but not directly usable.

We need to convert actions into user interests.

Example:

User activity:

Likes:
- NBA posts
- Football videos
- Cricket highlights


After ML processing:

User Profile:

{
 Sports: 0.95,
 Technology: 0.30,
 Politics: 0.10
}

---

# 6. Batch Analytics Pipeline

Large-scale analytics can run offline.

Flow:


Kafka
  |
Spark Streaming
  |
Hadoop / Data Lake
  |
ML Jobs


Examples of analysis:

- Most liked topics
- Most shared posts
- User interest classification
- Trending categories
- Similar user identification

---

# 7. Content Classification

Posts can also be classified.

Example:

Tweet:

"Great game between Lakers and Celtics"

ML model tags it:

Sports
Basketball
NBA


This metadata is stored with the content.

---

# 8. Recommendation Engine

The recommendation engine combines:

## User profile

Example:

User likes:

Sports = 0.95

---

## Content profile

Post tags:

Sports = 0.90


---

## Social Graph

Friends with similar interests.

Example:

If many users similar to User A like a post,
we can recommend it to User A.

---

# Graph-Based Recommendation

Your notes mention using graph traversal.

Example:

User A follows User B.

User B follows User C.

User C produces content highly engaged by users similar to A.


Algorithms:

- BFS/DFS traversal
- Graph ranking algorithms
- Embedding-based similarity models


---

# 9. Re-engagement Pipeline

Problem:

Some users become inactive.

Goal:

Bring them back.

---

## Inputs:

- User interests
- Previously liked content
- Trending topics
- Social connections

---

## Pipeline:


Activity Data
       |
    Hadoop
       |
 ML Recommendation Jobs
       |
 Recommendation Service
       |
 Notification Service
       |
 Email / Push Notification


Example:

"Your favorite football team just posted a new video."

---

# 10. Why Separate Online and Offline Processing?

## Online Path

Requirements:

- Low latency
- User facing

Examples:

- Like a post
- Add a comment
- View timeline


---

## Offline Path

Requirements:

- Large computation
- Historical analysis

Examples:

- User profiling
- ML training
- Trend discovery


---

# L6 Trade-Off Discussion

## Why not update recommendation models during every like?

Because it increases user-facing latency.

Instead:

- Record events quickly
- Stream them through Kafka
- Process asynchronously

---

## Why keep activity history?

Allows:

- Personalized recommendations
- Re-engagement campaigns
- Trend analysis
- ML training

---

## Why Redis for counts?

Counting millions of likes by scanning a database is expensive.

Maintain a cached aggregate.

---

# Part 3 Summary

User interactions generate valuable signals.

Real-time path:

User Action
      |
 Service
      |
 Kafka


Asynchronous systems consume events:

- Activity Tracker
- Analytics
- ML Pipelines
- Recommendation Systems


Data stores:

- Cassandra → Likes, comments, activity logs
- Redis → Aggregated counts
- Hadoop/Data Lake → Historical analytics
- Spark → Large-scale processing


The social network is not just a content platform; it is a data and recommendation engine that continuously learns from user behavior.

# Design Twitter / X - Part 4
# Search, Trending Topics, Ranking, and Real-Time Analytics

---

# 1. Search Requirements

Users should be able to search for:

- Tweets
- Users
- Hashtags
- Topics

Examples:

Search:
"NBA Finals"

Results may include:

- Recent tweets
- Popular tweets
- Verified accounts
- Trending discussions

---

# 2. Challenges in Search

Searching billions of tweets by scanning Cassandra is not practical.

Example:

Bad approach:

Search Service
       |
 Cassandra
       |
 Scan billions of tweets


Problems:

- Extremely slow
- High database load
- Poor user experience


We need a dedicated search engine.

---

# 3. Elasticsearch

Use Elasticsearch because it provides:

- Full-text search
- Inverted indexes
- Tokenization
- Ranking algorithms
- Distributed scaling

---

# 4. How Inverted Index Works

Traditional database indexing:

TweetID → Tweet Content


Example:

Tweet 101:

"LeBron wins NBA finals"


A search engine creates the reverse mapping:


Word → Tweet IDs


Example:

"LeBron" → [101, 500, 800]

"NBA" → [101, 200, 600]

"Finals" → [101, 900]


This allows very fast keyword lookup.

---

# 5. Search Indexing Pipeline


Tweet Created
      |
      Kafka
      |
 Search Index Consumer
      |
 Elasticsearch


Why asynchronous indexing?

The user should not wait for search indexing before a tweet is successfully posted.

Tweet creation path:

User
 |
Tweet Service
 |
Cassandra
 |
Return Success


Background path:

Kafka
 |
Search Consumer
 |
Elasticsearch


Trade-off:

A newly created tweet may take a few seconds to appear in search.

Eventual consistency is acceptable.

---

# 6. Search Ranking

Not all matching tweets should appear in chronological order.

The ranking system can consider:

- Text relevance
- Number of likes
- Number of retweets
- Number of comments
- User reputation
- Recency
- User interests


Example:

Query:

"World Cup"

Two matching tweets:

Tweet A:
- Posted 5 seconds ago
- 2 likes

Tweet B:
- Posted 30 minutes ago
- 50,000 likes


The ranking model determines which appears first.

---

# 7. Personalized Search

Search results can vary by user.

Example:

User A:

Interested in sports.

Search:
"Giants"

Results:

NFL New York Giants.


User B:

Interested in technology.

Search:
"Giants"

Results:

Technology companies or different context.


Personalization signals:

- User activity history
- Likes
- Follows
- Previous searches
- Location

---

# 8. Popular Query Caching

Many users search for the same events.

Examples:

- Super Bowl
- Olympics
- Elections
- Oscars


Instead of running the same Elasticsearch query repeatedly:


Search Request
       |
 Redis Cache
       |
    Hit
       |
 Return results


On cache miss:


Search Service
       |
 Elasticsearch
       |
 Redis
       |
 User


---

# 9. Cache Expiration (TTL)

Search results change frequently.

Example:

Query:

"World Cup"

Results are very dynamic during a live match.


Therefore:

- Cache only hot queries.
- Use short TTL values.
- Refresh popular searches frequently.


---

# 10. Trending Topics

Trending is a streaming analytics problem.

Examples:

A hashtag suddenly increases:

#WorldCupFinal

10 mentions/minute
      ↓
50 mentions/minute
      ↓
5000 mentions/minute


The system must detect the spike.

---

# 11. Trending Pipeline


Tweet Events
      |
      Kafka
      |
 Stream Processing (Spark/Flink)
      |
 Count Hashtags
      |
 Detect Growth Rate
      |
 Trending Service
      |
 Redis Cache


---

# 12. Why Kafka?

Kafka allows:

- High-throughput ingestion
- Multiple consumers
- Replayability
- Durability


The same tweet stream can feed:

- Search indexing
- Trending calculations
- Analytics
- ML systems


---

# 13. Real-Time vs Batch Analytics

## Real-Time Streaming

Purpose:

Detect immediate events.

Examples:

- Breaking news
- Viral hashtags
- Live sports discussions


Pipeline:

Kafka
 |
Spark Streaming/Flink
 |
Redis


Latency:

Seconds


---

## Batch Analytics

Purpose:

Historical analysis.

Examples:

- Monthly trends
- User behavior analysis
- Recommendation model training


Pipeline:

Kafka
 |
Data Lake (HDFS/S3)
 |
Spark Batch Jobs


Latency:

Minutes to hours

---

# 14. Data Storage for Analytics

Raw events are valuable.

Store:

- Tweet creation events
- Search events
- Likes
- Shares
- Clicks


Reasons:

- Historical reporting
- Model training
- Trend analysis
- Replay new analytics pipelines


---

# 15. Scaling Elasticsearch

A single Elasticsearch node cannot handle Twitter scale.

Use:

- Sharding
- Replication


Sharding:

Tweet Index

Shard 1:
Tweets A-M

Shard 2:
Tweets N-Z


Replication provides:

- High availability
- Higher read throughput


---

# 16. L6 Trade-Off Discussion

## Why not use Cassandra for search?

Cassandra is optimized for:

- Key-based lookups
- High write throughput


It is not designed for:

- Full-text search
- Relevance ranking
- Token matching


Elasticsearch is specialized for search.

---

## Why not update Elasticsearch synchronously?

Because it increases tweet posting latency.

Instead:

Tweet write path:

Fast and reliable.

Search indexing:

Asynchronous.

Trade-off:

Search is eventually consistent.

---

## Why cache search results?

Because many users search the same topics.

Examples:

- World Cup
- Elections
- Breaking news


Caching reduces:

- Elasticsearch load
- Response latency


---

# Part 4 Summary

Search Architecture:

Tweet Service
       |
    Kafka
       |
 Search Consumer
       |
 Elasticsearch


Trending Architecture:

Tweet Events
       |
    Kafka
       |
 Spark Streaming/Flink
       |
 Trending Service
       |
 Redis


Storage Choices:

- Elasticsearch → Full-text search
- Redis → Hot search results and trending topics
- Kafka → Event backbone
- Spark/Flink → Real-time processing
- Data Lake → Historical analytics


Key idea:

Twitter search is not just keyword matching.

It combines:

- Inverted indexing
- Relevance ranking
- User personalization
- Real-time trend detection
- Large-scale analytics

# Design Twitter / X - Part 5
# Recommendation System, Notifications, Feed Ranking, and End-to-End Architecture

---

# 1. The Evolution of the Timeline

Originally, Twitter timelines were simple:

Timeline = Tweets from people I follow sorted by timestamp.

Example:

User follows:

- Sports Account
- Technology Account
- Celebrity

Feed:

10:01 Sports tweet

10:03 Celebrity tweet

10:05 Technology tweet


However, modern social networks optimize for engagement.

The question becomes:

"What content is most valuable for this specific user?"

Therefore the feed becomes:

Timeline = Ranking(User Profile, Content Features, Social Signals)


---

# 2. User Profile Generation

The system continuously learns user interests.

Signals include:

- Likes
- Comments
- Retweets
- Shares
- Watch time
- Clicks
- Search history
- Follow relationships


Every action generates an event:


User Action
     |
     Kafka
     |
 Activity Tracker
     |
 Data Lake


Examples:

User A:

Likes:
- NBA
- NFL
- Football


Generated User Profile:

{
 Sports: 0.95,
 Technology: 0.20,
 Politics: 0.05
}


This profile is continuously updated.


---

# 3. Content Understanding

Tweets also need a profile.

Example Tweet:

"Lakers defeat Celtics in Game 7"

ML Classification:

{
 Sports: 0.98,
 Basketball: 0.95,
 NBA: 0.99
}


The content metadata is stored with the tweet.


---

# 4. Recommendation Engine


Inputs:

1. User Profile
2. Content Features
3. Social Graph
4. Trending Topics
5. Historical Engagement


Example:

User Profile:

Sports: 0.95


Candidate Tweet:

Sports: 0.90


The recommendation model calculates a score:

Relevance Score =
 User Interest
 + Tweet Popularity
 + Social Connections
 + Freshness


Tweets with higher scores appear earlier in the feed.


---

# 5. Candidate Generation and Ranking

A major challenge:

Twitter contains billions of tweets.

We cannot rank every tweet for every user.

Therefore we divide the problem into two stages.


## Stage 1: Candidate Generation


Retrieve a smaller set of candidate tweets from:

- Followed users
- Trending topics
- Similar users
- Popular tweets


Example:

1 billion tweets

        ↓

10,000 candidate tweets


---

## Stage 2: Ranking


Apply ML ranking models using:

- User preferences
- Likes
- Shares
- Comments
- Time spent viewing
- Relationship strength
- Recency


Example:

10,000 tweets

        ↓

Top 100 personalized tweets


---

# 6. Real-Time Recommendation Updates


Some recommendations need to change quickly.

Examples:

- Breaking news
- Sports events
- Viral content


Pipeline:


Tweet Activity
      |
    Kafka
      |
 Stream Processing
      |
 Update Trending Signals
      |
 Recommendation Service


---

# 7. Notification System


Notifications improve user engagement.


Examples:

- Someone liked your tweet.
- A user you follow posted new content.
- A topic you like is trending.
- A celebrity you follow is live.


---

# 8. Notification Architecture


Events:

- Like Event
- Follow Event
- Tweet Event


Flow:


Application Services
          |
        Kafka
          |
 Notification Service
          |
 User Preference Service
          |
 Notification Queue
          |
 Push / Email / SMS Providers


---

# 9. Why Use Kafka for Notifications?


A direct synchronous approach:


Tweet Service
       |
Notification API
       |
External Push Provider


Problems:

- Increased tweet latency
- Failure of notification provider affects tweeting
- Difficult to support multiple consumers


Event-driven approach:


Tweet Service
      |
    Kafka
      |
 Notification Consumers


Benefits:

- Low latency tweet creation
- Durable message storage
- Retry capability
- Independent scaling
- Multiple notification channels


---

# 10. User Preferences


Users can control:

- Push notifications
- Email notifications
- Muted users
- Blocked users


Before sending a notification:

Notification Service
          |
 Check Preferences
          |
 Send Message


---

# 11. Re-engagement Pipeline


Some users become inactive.

Goal:

Bring them back.


Example:

User has not opened Twitter for 10 days.

System checks:

- Historical interests
- New content from followed accounts
- Trending events


Pipeline:


Activity History
        |
 Data Lake
        |
 ML Recommendation Jobs
        |
 Re-engagement Service
        |
 Notification Service


Example Notification:

"Your favorite team just won a championship."

---

# 12. Online vs Offline Processing


## Online Systems

Requirements:

- Millisecond latency
- User-facing requests


Examples:

- Load timeline
- Like tweet
- Post comment
- Send notifications


Technologies:

- Redis
- Cassandra
- APIs


---

## Offline Systems


Requirements:

- Process enormous historical datasets


Examples:

- Train recommendation models
- Build user profiles
- Discover trends


Technologies:

- Spark
- Hadoop
- Data Lake


---

# 13. End-to-End Twitter Architecture


                     Users
                       |
                 Load Balancer
                       |
                   API Gateway
                       |
 --------------------------------------------------
 |             |             |                    |
User      Tweet Service  Timeline          Social Graph
Service                   Service
 |             |             |
MySQL      Cassandra       Redis
              |
            Kafka
              |
 ----------------------------------------------------------
 |             |              |             |              |
Feed       Search        Analytics     Notification     ML
Processor  Service       Pipeline       Service       Pipeline
 |             |              |             |
Redis      Elasticsearch   Data Lake    Push Provider


---

# 14. Important L6 Trade-Offs


## Why not calculate recommendations during every request?

Because:

- ML ranking is expensive.
- It increases latency.
- It does not scale.

Instead:

- Precompute user profiles.
- Generate candidates.
- Apply fast ranking online.


---

## Why use event-driven architecture?

Because many systems need the same data:

Tweet Created Event

Consumers:

- Timeline generation
- Search indexing
- Analytics
- Recommendations
- Notifications


Kafka allows:

- Independent consumers
- Replayability
- Durability
- Backpressure handling


---

## Why separate online and offline systems?

Online systems optimize for:

- Latency
- Availability


Offline systems optimize for:

- Throughput
- Large-scale computation


---

# Final Twitter Design Summary


Core Databases:

MySQL:
- User accounts
- Profile information


Cassandra:
- Tweets
- Likes
- Comments
- Activity logs


Redis:
- Timeline cache
- Like counts
- Trending topics
- Search cache


Elasticsearch:
- Full-text search


Object Storage + CDN:
- Images and videos


Kafka:
- Event backbone


Spark/Hadoop/Data Lake:
- Analytics and ML


---

# The Most Important Interview Takeaways


Timeline:
- Normal users → Fan-out on Write
- Celebrities → Fan-out on Read
- Redis stores recent feeds
- Cassandra stores historical data


Search:
- Kafka → Elasticsearch
- Inverted indexes
- Ranking and caching


Recommendations:
- User behavior → User Profile
- Content → Feature extraction
- Candidate generation → ML ranking


Notifications:
- Event-driven
- Asynchronous
- User preference aware


The key architectural principle of Twitter is:

"Optimize user-facing paths for low latency, and move heavy computation to asynchronous pipelines using Kafka and large-scale processing systems."

# Social Feed Platform Differences
## Twitter / Facebook / Instagram / LinkedIn Specific Design Considerations

---

# 1. Common Foundation (Applies to All Social Feed Systems)

The following architecture is common across Twitter, Facebook, Instagram, and LinkedIn:

- Content Creation Service
- User / Social Graph Service
- Feed Generation Service
- Fan-out on Write vs Fan-out on Read
- Redis Feed Cache
- Cassandra or distributed storage for large-scale content
- Kafka-based asynchronous pipelines
- Activity Tracking
- Recommendation Engine
- Notifications
- Data Lake + ML pipelines

The first five parts of this design document cover these common concepts.

This document only focuses on platform-specific differences.

---

# 2. Twitter / X Specific Design Considerations

## 2.1 Directed Social Graph

Twitter follows a directed relationship model.


A follows B

A can see B's content.

B does not automatically follow A.


Example:

User A --------> User B


Data Model:

FollowerId → FollowingId


---

## 2.2 Celebrity Problem

Twitter has a very high number of celebrity accounts.

Example:

Celebrity:
100 million followers


Problem:

A single tweet can trigger:

100 million feed updates.


Solution:

Use Hybrid Feed Generation.

Normal users:
- Fan-out on Write

Celebrities:
- Fan-out on Read


---

## 2.3 Search Is a Core Feature

Twitter users frequently search:

- Hashtags
- Breaking news
- Trending events
- Public conversations


Architecture:

Tweet Created
      |
    Kafka
      |
Search Consumer
      |
Elasticsearch


Use:

- Inverted Indexes
- Ranking Algorithms
- Redis caching for hot searches


---

## 2.4 Trending Topics

Twitter emphasizes real-time event discovery.


Pipeline:

Tweet Events
      |
    Kafka
      |
Stream Processing (Spark/Flink)
      |
Trending Service
      |
Redis


Examples:

- World Cup
- Elections
- Breaking News


---

# 3. Facebook Specific Design Considerations


## 3.1 Friendship Graph (Bidirectional)

Unlike Twitter follows, Facebook relationships are usually mutual.


User A <-------> User B


A friendship requires:

- Friend Request
- Pending State
- Acceptance or Rejection


Example:

FriendRequest:

{
    requestId,
    senderId,
    receiverId,
    status,
    timestamp
}


---

## 3.2 Privacy and Access Control (Most Important Difference)

Facebook has complex visibility rules.

A post can have visibility:

- Public
- Friends
- Friends of Friends
- Custom Groups
- Only Me


Feed generation becomes:


Candidate Posts
       |
 ACL / Privacy Service
       |
 Filter Unauthorized Content
       |
 Ranking Service
       |
 User Feed


This is one of the biggest design differences compared with Twitter.


---

## 3.3 Media and Photo Albums

Facebook is more media-centric.

Architecture:


Photo Upload
       |
 Media Service
       |
 Object Storage (S3)
       |
 CDN


Photo metadata may contain:

- PhotoId
- UserId
- AlbumId
- Tags
- Location
- Timestamp


---

## 3.4 Facebook Stories

Stories are temporary content.

Characteristics:

- Available for 24 hours
- High read traffic
- Temporary storage


Implementation:

Redis / Cache
       |
 TTL
       |
 Automatic Expiration


---

# 4. Instagram Specific Design Considerations


Instagram shares many concepts with Facebook but is even more media focused.


## 4.1 Directed Follower Graph


User A --------> User B


---

## 4.2 Media Processing Pipeline


Image/Video Upload
        |
 Media Service
        |
 Transcoding Service
        |
 Multiple Resolutions
        |
 Object Storage
        |
 CDN


Reasons:

- Different device sizes
- Different network speeds
- Optimized streaming


---

## 4.3 Reels Recommendation System

Reels are heavily ML driven.


Ranking signals:

User Features:
- Watch Time
- Likes
- Shares
- Comments
- Following History


Content Features:

- Topic
- Creator
- Popularity
- Engagement Rate


ML Ranking decides which videos appear in the feed.


---

## 4.4 Stories

Same concept as Facebook:

- Ephemeral content
- TTL based expiration
- Cached for low latency


---

# 5. LinkedIn Specific Design Considerations


## 5.1 Professional Social Graph


Relationships are based on:

- Connections
- Companies
- Schools
- Skills
- Professional Interests


---

## 5.2 Feed Ranking

Ranking signals are different from consumer social networks.


Examples:

Professional relevance:
- Same company
- Similar industry
- Shared connections
- Job interests


Engagement:

- Likes
- Comments
- Shares


Recency:

- Recent posts


---

## 5.3 Job Recommendation System


Inputs:

User Profile:
- Skills
- Experience
- Industry


Job Features:
- Required skills
- Location
- Company


ML Matching:

User Profile
       +
Job Features
       +
Historical Engagement
       |
 Recommendation Score


---

## 5.4 Notifications


Examples:

- Someone viewed your profile
- New job matches your skills
- Your connection changed companies
- Someone liked your post


---

# 6. Interview Strategy

Do not memorize four separate architectures.


Think in terms of a common social feed platform:

Core Components:

User
 |
API Gateway
 |
--------------------------------
|              |               |
Post        Feed          Social Graph
Service     Service       Service
 |
Kafka
 |
------------------------------------------------
|          |          |          |             |
Search  Analytics  Notifications  ML      Recommendations


Then apply platform-specific modifications.


---

# Quick Comparison Table

| Feature | Twitter | Facebook | Instagram | LinkedIn |
|---|---|---|---|---|
| Social Graph | Directed Follow | Mutual Friends | Directed Follow | Professional Connections |
| Main Content | Tweets | Posts/Photos | Photos/Videos/Reels | Professional Posts |
| Search Importance | Very High | Medium | Low | Medium |
| Trending Topics | Very High | Medium | Medium | Low |
| Privacy Complexity | Low | Very High | Medium | Medium |
| Media Focus | Medium | High | Very High | Low |
| ML Recommendations | High | Very High | Very High | High |
| Celebrity Problem | Major | Moderate | Major | Low |
| Feed Strategy | Hybrid | Hybrid | Hybrid | Hybrid |

---

# Final L6 Interview Perspective

The interviewer is not looking for "Twitter knowledge" or "Facebook knowledge".

The real evaluation is whether you understand:

- Fan-out on Write vs Fan-out on Read
- Caching strategies
- Event-driven architectures using Kafka
- Storage trade-offs
- Search and indexing
- Activity tracking
- ML-based ranking
- Real-time vs batch processing
- Scalability and failure handling


The platform-specific differences are usually only the last 10–20% of the discussion.

Master the common social feed architecture first, then add these variations.