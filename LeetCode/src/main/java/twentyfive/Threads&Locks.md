# Thread Safety for Running Mean Service

## Problem

We have a service that receives events for different securities (CUSIPs).

Example:

```
consumeEvent("AAPL", event1)
consumeEvent("MSFT", event2)
consumeEvent("AAPL", event3)
```

Multiple threads may call `consumeEvent()` simultaneously.

The question is:

> How do we make this thread-safe while allowing different CUSIPs to be processed in parallel?

---

# Approach 1 : One Global ReentrantLock (Simple but Poor Scalability)

```java
class MeanService {

    private final ReentrantLock lock = new ReentrantLock();

    private Map<String, List<Event>> events = new HashMap<>();

    public void consumeEvent(String cusip, Event event) {

        lock.lock();

        try {

            if (!events.containsKey(cusip)) {
                events.put(cusip, new ArrayList<>());
            }

            events.get(cusip).add(event);

        } finally {
            lock.unlock();
        }
    }
}
```

### Problem

Suppose three threads arrive.

```
Thread 1 -> AAPL

Thread 2 -> MSFT

Thread 3 -> GOOG
```

Even though they are updating different securities,

all of them wait on **one global lock**.

```
                Global Lock
                     |
Thread 1 ------------ acquires

Thread 2 ------------ waits

Thread 3 ------------ waits
```

This is thread-safe but does **not scale well**.

---

# Better Approach : One Lock Per CUSIP

Instead of locking the whole service,

each security owns its own lock.

## SecurityData

```java
class SecurityData {

    List<Event> events;
    ReentrantReadWriteLock lock;

    public SecurityData() {
        events = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
    }
}
```

---

## Main Service

```java
class MeanService {

    private ConcurrentHashMap<String, SecurityData> securities =
            new ConcurrentHashMap<>();

    public void consumeEvent(String cusip, Event event) {

        SecurityData security;

        if (securities.containsKey(cusip)) {
            security = securities.get(cusip);
        } else {

            security = new SecurityData();
            securities.put(cusip, security);
        }

        security.lock.writeLock().lock();

        try {

            security.events.add(event);

        } finally {

            security.lock.writeLock().unlock();
        }
    }
}
```

---

# Why ConcurrentHashMap?

It makes operations like

```
put()

get()

containsKey()
```

thread-safe.

However,

```
security.events.add(...)
```

is **NOT** thread-safe.

That is why we still need a lock.

---

# Why ReentrantReadWriteLock?

There are two operations.

## consumeEvent()

Modifies data.

Needs

```
Write Lock
```

---

## getMean()

Only reads data.

Needs

```
Read Lock
```

Many readers can execute together.

Only writers block everyone.

---

# Memory Layout

```
ConcurrentHashMap

"AAPL"
      |
      |
      v

+-----------------------------+
| List<Event>                 |
| ReentrantReadWriteLock      |
+-----------------------------+

"MSFT"
      |
      |
      v

+-----------------------------+
| List<Event>                 |
| ReentrantReadWriteLock      |
+-----------------------------+

"GOOG"
      |
      |
      v

+-----------------------------+
| List<Event>                 |
| ReentrantReadWriteLock      |
+-----------------------------+
```

Each security has **its own lock**.

---

# Example

### Thread 1

```
consumeEvent(AAPL)
```

Locks

```
AAPL Lock
```

---

### Thread 2

```
consumeEvent(MSFT)
```

Locks

```
MSFT Lock
```

Both execute simultaneously.

---

### Thread 3

```
consumeEvent(AAPL)
```

Needs

```
AAPL Lock
```

Must wait until Thread 1 finishes.

---

# Time Complexity

Lookup in ConcurrentHashMap

```
O(1)
```

Acquire lock

```
O(1)
```

Insert into ArrayList

```
O(1)
```

Overall

```
O(1)
```

---

# Interview Explanation

> Since multiple threads may call `consumeEvent()` concurrently, I don't want a single global lock because that would serialize updates across all securities.
>
> Instead, I maintain a `ConcurrentHashMap<String, SecurityData>`, where each `SecurityData` object owns its own `ReentrantReadWriteLock`.
>
> When an event arrives, I locate the corresponding `SecurityData`, acquire its **write lock**, update the event list, and release the lock.
>
> Different CUSIPs have different lock objects, so they can be processed concurrently, while updates to the same CUSIP are serialized safely.

# ConcurrentHashMap vs HashMap (Interview Notes)

## My Original Confusion

My question was:

> "If I already have a `ReentrantLock` inside `SecurityData`, why do I even need a `ConcurrentHashMap`? Doesn't the lock already protect the AAPL list?"

The answer is:

**The `ConcurrentHashMap` does NOT protect the AAPL list.**

The `ReentrantLock` protects the AAPL list.

The `ConcurrentHashMap` only protects the **map's internal data structure**.

---

# What does each one protect?

```
ConcurrentHashMap
        │
        ▼
"AAPL"  --->  SecurityData
                    │
                    ▼
             ReentrantLock
                    │
                    ▼
         events, sum, count
```

### ConcurrentHashMap protects

- the mapping from key → value
- internal buckets
- resizing
- insertions
- removals
- lookups while other threads are modifying the map

### ReentrantLock protects

- the mutable state inside one SecurityData object
    - event list
    - running sum
    - count
    - any other mutable fields

---

# Why isn't HashMap enough?

Suppose two threads execute simultaneously.

```java
map.put("AAPL", data1);
```

and

```java
map.put("MSFT", data2);
```

A normal `HashMap` is **not thread-safe**.

Both threads may modify the internal bucket structure at the same time.

Possible consequences include:

- corrupted bucket chains
- lost entries
- inconsistent reads
- undefined behavior

The problem is **inside the HashMap itself**, not your application logic.

---

# What happens after map.get()?

Suppose we already have

```
AAPL  ---> SecurityData A
```

Thread 1

```java
SecurityData data = map.get("AAPL");
```

Thread 2

```java
SecurityData data = map.get("AAPL");
```

Both receive the same object.

Now both try

```java
data.events.add(...)
```

This is where

```java
data.lock.lock();
```

is needed.

The map is no longer involved.

---

# Phone Book Analogy

Think of the map as a phone book.

```
Phone Book

AAPL ---> Room 101
MSFT ---> Room 102
```

The phone book only tells you where the room is.

Once you reach Room 101,

the **door lock** protects the room.

The phone book does not protect what happens inside the room.

```
ConcurrentHashMap
        │
Find Room 101
        │
        ▼
ReentrantLock
        │
Protects the room (events, sum, count)
```

---

# When should I use ConcurrentHashMap?

## Case 1: Single thread

```java
HashMap
```

✅ Perfectly fine.

---

## Case 2: Multiple threads, read-only map

Example:

```
Application starts

↓

Load all CUSIPs

↓

Never modify map again

↓

100 threads only call get()
```

```java
HashMap
```

✅ Safe.

Concurrent reads are fine if the map is never modified after initialization.

---

## Case 3: Multiple threads reading AND writing

Example:

```
Thread 1 -> put(AAPL)

Thread 2 -> get(MSFT)

Thread 3 -> remove(GOOG)
```

Now a normal HashMap is **not safe**.

Use either:

- ConcurrentHashMap
- HashMap protected by synchronization

---

## Case 4: Streaming System

Suppose new securities can arrive dynamically.

```
consume(AAPL)

consume(MSFT)

consume(NVDA)
```

Some threads may perform

```java
map.put(newCusip, new SecurityData());
```

while others perform

```java
map.get(existingCusip);
```

This is exactly where `ConcurrentHashMap` is useful.

---

## Case 5: All securities are preloaded

Suppose during startup

```
Load all securities

↓

Never modify the map again

↓

Only update SecurityData
```

Then

```java
HashMap<String, SecurityData>
```

is sufficient.

The per-CUSIP `ReentrantLock` protects each SecurityData object.

---

# Interview Answer

If asked:

> "Why ConcurrentHashMap?"

A good answer is:

> "Not because my event list needs protection—that's handled by the per-CUSIP `ReentrantLock`. I use `ConcurrentHashMap` so concurrent lookups, insertions, and removals do not corrupt the map's internal structure."

---

# Easy Rule to Remember

| Scenario | HashMap | ConcurrentHashMap |
|----------|---------|-------------------|
| Single thread | ✅ | Not needed |
| Multiple threads, read-only after initialization | ✅ | Optional |
| Multiple threads, concurrent reads + writes | ❌ | ✅ |
| Concurrent put/remove/get | ❌ | ✅ |

---

# Key Takeaway

**ConcurrentHashMap protects the directory.**

```
CUSIP
   │
   ▼
SecurityData
```

**ReentrantLock protects the actual data inside one SecurityData object.**

```
SecurityData
    │
    ├── events
    ├── sum
    └── count
```

They solve **different concurrency problems**, and are often used together.