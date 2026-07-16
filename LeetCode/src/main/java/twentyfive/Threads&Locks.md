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