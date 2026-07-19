Distributed Job Scheduler - System Design (SDE-3)
# Requirement Clarification

### 1. What types of jobs are we supporting?

- One-time jobs?
- Recurring (Cron) jobs?

**Assumption:** Support both.

---

### 2. Can users manage scheduled jobs?

Should users be able to:

- Create
- Update
- Delete
- Pause
- Resume

jobs?

**Assumption:** Yes.

---

### 3. What execution guarantee do we need?

Do we need:

- At-most-once
- At-least-once
- Exactly-once

**Assumption:** At-least-once. Jobs should be idempotent.

---

### 4. What happens when a job fails?

Should we:

- Retry?
- Configurable retry count?
- Dead Letter Queue (DLQ) after retries?

**Assumption:** Retry with backoff. Send permanently failed jobs to DLQ.

---

### 5. What scale are we designing for?

- Active jobs?
- Executions per day?

**Assumption:**

- ~10 Million active jobs
- ~100 Million executions/day

---

### 6. How much scheduling latency is acceptable?

Should jobs execute:

- Exactly at the scheduled time?
- Or within a few seconds?

**Assumption:** A few seconds of scheduling latency is acceptable.

---

## Assumptions

- Support one-time and recurring cron jobs.
- Users can create, update, pause, resume, and delete jobs.
- At-least-once execution with idempotent jobs.
- Retry failed jobs with backoff and move permanently failed jobs to a DLQ.
- System should handle approximately **10 million active jobs** and **100 million executions per day**.
- A few seconds of scheduling latency is acceptable.

1. Requirements
Functional
Create Job
Update Job
Delete Job
Pause / Resume
One-time Jobs
Cron Jobs
Retry Failed Jobs
View Execution History
Non Functional
10M active jobs
100M executions/day
Highly available
Horizontally scalable
At-least-once execution
Low scheduling latency
---
2. High Level Design
```text
             Client
                |
          API Gateway
                |
      Job Scheduler Service
                |
      +---------+---------+
      |                   |
   Jobs DB         JobExecution DB
                |
          Scheduler Engine
                |
              Kafka
                |
            Worker Pool
```
---
3. APIs
```
POST   /jobs
PUT    /jobs/{id}
DELETE /jobs/{id}
POST   /jobs/{id}/pause
POST   /jobs/{id}/resume
GET    /jobs/{id}
```
---
4. Database
Jobs
jobId
cronExpression
payload
status
retryPolicy
JobExecution
executionId
jobId
scheduledTime
status
retryCount
startTime
endTime
workerId
error
Keep Job definition separate from execution history.
---
5. Scheduler
Poll due jobs:
```sql
SELECT *
FROM JobExecution
WHERE status='SCHEDULED'
AND scheduledTime <= NOW()
LIMIT 1000
FOR UPDATE SKIP LOCKED;
```
Flow
```
Claim Job
    ↓
Update DISPATCHED
    ↓
Publish to Kafka
```
---
6. Worker
```
Receive Message
      ↓
RUNNING
      ↓
Execute
      ↓
SUCCESS / FAILED
      ↓
Commit Kafka Offset
```
Commit offset only after DB update.
---
7. States
```
SCHEDULED
    ↓
DISPATCHED
    ↓
RUNNING
    ↓
SUCCESS / FAILED
```
---
8. Failure Handling
If worker crashes before committing offset:
Kafka redelivers
Worker checks execution status
Skip duplicate execution (Idempotency)
---
9. Recurring Jobs
On success:
```
Execution 1 -> SUCCESS

↓

Insert

Execution 2 -> SCHEDULED
```
Create a new execution entry.
---
10. Retry
```
FAILED

↓

retryCount > 0

↓

Create new JobExecution

↓

scheduledTime = now + backoff
```
Retries exhausted:
```
FAILED_PERMANENTLY

↓

DLQ
```
---
11. Partitioning
Partition JobExecution by scheduled month.
Benefits:
Faster queries
Partition pruning
Easy archival
---
12. Archival
Retention policy:
```
Old Partition

↓

Archive (S3/HDFS)

↓

DROP PARTITION
```
---
13. Scaling
Shard schedulers.
```
Shard0 -> Scheduler1
Shard1 -> Scheduler2
Shard2 -> Scheduler3
```
Each scheduler polls only its shard.
---
14. Coordinator
Coordinator (ZooKeeper / etcd / Kubernetes Lease)
Responsibilities:
Heartbeats
Shard assignment
Detect failures
Reassign shards
```
Scheduler dies

↓

Coordinator detects

↓

Assign shard to another scheduler
```
---
15. Kafka Notes
At-Least-Once delivery
Commit offset after DB update
Idempotent execution
---
16. Monitoring
Queue Length
Scheduling Latency
Retry Count
Failed Jobs
DLQ Size
Worker Utilization
---
Key Interview Points
Separate Job and JobExecution.
Use FOR UPDATE SKIP LOCKED.
Scheduler updates DISPATCHED.
Worker updates RUNNING then SUCCESS/FAILED.
Commit Kafka offset after DB update.
New execution row for recurring jobs.
Retry with backoff.
DLQ after retries exhausted.
Monthly partitioning and archival.
Sharding with coordinator for failover.