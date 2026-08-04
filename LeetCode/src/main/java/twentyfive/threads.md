# Java Engineering Fundamentals
# Part 1 - Thread Safety & Concurrency

---

# 1. What is a Thread?

A thread is an independent execution path within a Java program.

Example

Two HTTP requests arrive simultaneously.

```
Request A
    ↓
Thread A

Request B
    ↓
Thread B
```

Each request is processed by a different thread.

---

# 2. What is a Context Switch?

A CPU executes only one instruction per core at a time.

To run multiple threads, the Operating System rapidly switches between them.

This is called a **Context Switch (Thread Switch).**

Example

```
Thread A

Read count

---------------- CPU switches ----------------

Thread B

Read count
Increment
Write

---------------- CPU switches ----------------

Thread A

Increment
Write
```

The OS saves Thread A's execution state (context), runs Thread B, and later resumes Thread A.

The context includes:

- Program counter
- CPU registers
- Stack
- Local variables

Important:

The JVM does **not** control context switching.

The Operating System scheduler does.

---

# 3. Race Condition

A race condition occurs when multiple threads access shared mutable data and the final result depends on execution timing.

Example

```java
count++;
```

This is NOT one operation.

Internally it becomes

```
Read

↓

Increment

↓

Write
```

If two threads both read the value before either writes it back, one update is lost.

Example

Initial value

```
count = 5
```

Thread A

```
Read 5
```

Thread B

```
Read 5
```

Thread A

```
Write 6
```

Thread B

```
Write 6
```

Final value

```
6
```

Expected

```
7
```

This is called a Lost Update.

---

# 4. synchronized

Purpose

Allow only one thread to execute a critical section at a time.

Example

```java
public synchronized void increment() {
    count++;
}
```

Behavior

```
Thread A

Acquire Lock

↓

Execute

↓

Release Lock
```

Thread B

```
Wait

↓

Acquire Lock

↓

Execute
```

Important

synchronized DOES NOT prevent context switching.

The OS can still switch threads.

However, another thread cannot enter the synchronized block until the current thread releases the lock.

---

# 5. HashMap

HashMap is NOT thread-safe.

Example

```java
HashMap<Long, Game> games = new HashMap<>();
```

Multiple concurrent reads/writes can corrupt the map.

To make it thread-safe

```java
synchronized(games) {

    // read/write

}
```

Problem

This locks the ENTIRE map.

Only one thread may access it at a time.

Poor scalability.

---

# 6. ConcurrentHashMap

Purpose

Provide thread-safe map operations without locking the entire map.

Example

```java
ConcurrentHashMap<Long, Game> games =
        new ConcurrentHashMap<>();
```

Benefits

- Concurrent reads
- Concurrent writes
- High throughput
- Internal fine-grained synchronization

Important

ConcurrentHashMap protects

```
The Map
```

It does NOT protect

```
Objects stored inside the map.
```

---

# 7. Atomic Operations

Examples

```java
put()

get()

remove()

replace()

putIfAbsent()

compute()
```

These operations are individually thread-safe.

Example

```java
games.putIfAbsent(gameId, game);
```

This performs

```
Check

↓

Insert
```

as one atomic operation.

Do NOT write

```java
if (!games.containsKey(id)) {
    games.put(id, game);
}
```

Reason

```
containsKey()

↓

Thread Switch

↓

put()
```

Another thread may insert between these operations.

This is a Check-Then-Act Race Condition.

---

# 8. compute()

Purpose

Perform an atomic Read → Modify → Write operation.

Instead of

```java
Game game = games.get(id);

modify(game);

games.put(id, game);
```

Use

```java
games.compute(id, (key, game) -> {

    return updatedGame;

});
```

Internally Java performs

```
Acquire lock for this key

↓

Get current value

↓

Execute your lambda

↓

Store returned value

↓

Release lock
```

The entire sequence is atomic for that key.

---

# 9. Why compute()?

Without compute()

```
get()

↓

modify()

↓

put()
```

Thread switches can occur between any step.

With compute()

```
Read

↓

Modify

↓

Write
```

becomes one atomic operation.


The operating system may still perform context switches during any of these steps.

However, while the computation is in progress, no other thread can modify the same key through the ConcurrentHashMap.

Therefore, from the application's perspective, the entire read-modify-write sequence behaves as one atomic operation for that key.

---

# 10. Fine-Grained Locking

HashMap + synchronized

```
Entire Map Locked
```

ConcurrentHashMap

```
Game 100

Locked

Game 200

Free

Game 300

Free
```

Only operations on the SAME key block each other.

Different keys can proceed simultaneously.

---

# 11. Mutable Objects

Suppose

```java
Game game = games.get(id);
```

The map returns a reference.

Multiple threads now hold the SAME object.

Example

```
Thread A

↓

Game

↑

Thread B
```

If Thread A modifies

```java
game.setHomeScore(22);
```

Thread B immediately observes that modification.

ConcurrentHashMap cannot prevent this.

Reason

The map only protects map operations.

Not objects already retrieved from the map.

---

# 12. Immutable Objects

Instead of

```java
game.setHomeScore(22);
```

Create

```java
Game updated =
    new Game(...22...20...);

return updated;
```

The map now references a NEW object.

```
Game A

20-18

↓

Game B

22-20
```

Thread B still references Game A.

Future readers receive Game B.

---

# 13. Why Immutable?

Old readers see

```
20-18
```

This is

- stale
- but valid
- consistent

They NEVER observe

```
22-18
```

which would be a partially updated object.

Key idea

Stale data is acceptable.

Inconsistent data is not.

---

# 14. compute() vs Immutable

compute()

Protects

```
Map Entry
```

Immutable Object

Protects

```
Object State
```

These solve DIFFERENT concurrency problems.

---

# 15. ReadWriteLock

Purpose

Optimize read-heavy systems.

Example

```java
ReadWriteLock lock =
        new ReentrantReadWriteLock();
```

Provides

```java
readLock()

writeLock()
```

---

# Read Lock

Many readers

```
Reader A

Reader B

Reader C
```

All execute simultaneously.

---

# Write Lock

Only one writer.

Readers wait.

Other writers wait.

---

# When to Use ReadWriteLock

Use it when

- Many readers
- Few writers
- Shared mutable object
- Multiple related updates

Example

Updating

- Game
- Leaderboard
- Standings

requires coordination beyond what ConcurrentHashMap provides.

---

# 16. Choosing the Right Tool

Need a thread-safe map?

→ ConcurrentHashMap

Need atomic map update?

→ compute(), putIfAbsent()

Need thread-safe shared object?

→ Immutable Object

or

→ ReadWriteLock

Need to coordinate multiple objects?

→ ReadWriteLock

or

→ Transactions

or

→ Event-driven architecture

depending on the use case.

---

# Key Interview Takeaways

1. Context switching is controlled by the Operating System.

2. synchronized prevents concurrent execution, not context switching.

3. ConcurrentHashMap protects the container, not the objects inside it.

4. compute() performs an atomic Read → Modify → Write for a single key.

5. Immutable objects eliminate partially updated state.

6. ReadWriteLock is useful for read-heavy workloads and protecting shared mutable objects.

7. Container thread safety and object thread safety are two different concerns.

# Java Engineering Fundamentals
# Part 3 - compute(), Immutable Objects & ReadWriteLock

---

# 1. Does compute() Prevent Thread Switching?

No.

This is a common misconception.

The Operating System can still context switch at any point.

Example

```
Thread A

Acquire Lock (Key 100)

↓

Read Current Value

↓

******** CPU SWITCH ********
```

Now Thread B executes

```
games.compute(100,...)
```

Can it continue?

No.

It blocks because Thread A owns the lock for key 100.

Eventually

```
CPU switches back

↓

Thread A

Modify Value

↓

Return Updated Object

↓

Release Lock
```

Thread B now proceeds.

Important

compute() does NOT prevent thread switching.

Instead it guarantees that no other thread can modify the SAME key while the computation is in progress.

---

# compute() Internal Flow

Conceptually

```
Acquire lock for key

↓

Read current value

↓

Execute lambda

↓

Store returned value

↓

Release lock
```

The Operating System may context switch during any of these steps.

However, another thread attempting to update the same key waits until the lock is released.

From the application's perspective, the entire read-modify-write sequence behaves as one atomic operation.

---

# 2. Fine-Grained Locking

Suppose the map contains

```
Game 100

Game 200

Game 300
```

Thread A

```
compute(100)
```

Thread B

```
compute(200)
```

Both execute simultaneously.

Only threads operating on the SAME key block each other.

---

# 3. Mutable Objects

Suppose

```java
Game game = games.get(gameId);
```

Now

```
Map

↓

Game Object

↑

Thread A

↑

Thread B
```

Both threads reference the SAME object.

If Thread A modifies

```java
game.setHomeScore(22);
```

Thread B immediately observes that modification.

ConcurrentHashMap cannot prevent this.

Reason

ConcurrentHashMap protects

```
The Map
```

It does NOT protect

```
Objects already retrieved from the map.
```

---

# 4. Inconsistent State

Suppose

```java
game.setHomeScore(22);

game.setAwayScore(20);

game.setStatus(LIVE);
```

CPU switches after

```java
game.setHomeScore(22);
```

Thread B reads

```
Home = 22

Away = 18

Status = SCHEDULED
```

This object never represented a valid game state.

This is a partially updated object.

---

# 5. Immutable Objects

Instead of modifying the existing object

```java
game.setHomeScore(...);
```

Create a new Game.

```java
Game updated =
        new Game(
                id,
                homeTeam,
                awayTeam,
                22,
                20,
                LIVE,
                scheduledStart,
                actualStart,
                actualEnd
        );

return updated;
```

ConcurrentHashMap replaces the map reference.

Old object

```
Game A

20-18
```

New object

```
Game B

22-20
```

Both objects exist simultaneously.

---

# 6. Why Is Immutable Safe?

Suppose Thread B already references

```
Game A

20-18
```

Thread A creates

```
Game B

22-20
```

Thread B still sees

```
20-18
```

This is acceptable because it is

- stale
- but valid
- internally consistent

Thread B never observes

```
22-18
```

because Game A is never modified.

Key Principle

Stale but consistent is acceptable.

Partially updated is not.

---

# 7. compute() vs Immutable Objects

compute()

Protects

```
Map Entry
```

Immutable Objects

Protect

```
Object State
```

These solve different concurrency problems.

---

# 8. ReadWriteLock

Purpose

Protect shared mutable objects.

Example

```java
ReadWriteLock lock =
        new ReentrantReadWriteLock();
```

Provides

```java
readLock()

writeLock()
```

---

# 9. Game Class

```java
public class Game {

    private final Long gameId;

    private final String homeTeam;

    private final String awayTeam;

    private int homeScore;

    private int awayScore;

    private MatchStatus status;

    private final LocalDateTime scheduledStart;

    private LocalDateTime actualStart;

    private LocalDateTime actualEnd;

    // Protects this Game object
    private final ReadWriteLock lock =
            new ReentrantReadWriteLock();

    public Game(
            Long gameId,
            String homeTeam,
            String awayTeam,
            int homeScore,
            int awayScore,
            MatchStatus status,
            LocalDateTime scheduledStart,
            LocalDateTime actualStart,
            LocalDateTime actualEnd) {

        this.gameId = gameId;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
        this.scheduledStart = scheduledStart;
        this.actualStart = actualStart;
        this.actualEnd = actualEnd;
    }

    public ReadWriteLock getLock() {
        return lock;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setHomeScore(int score) {
        this.homeScore = score;
    }

    public void setAwayScore(int score) {
        this.awayScore = score;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }
}
```

Each Game owns its own lock.

Different games do not block each other.

---

# Repository

```java
private final ConcurrentHashMap<Long, Game> games =
        new ConcurrentHashMap<>();
```

ConcurrentHashMap protects the repository.

ReadWriteLock protects the Game object.

---

# addGame()

```java
public Game addGame(CreateGameRequest request) {

    validate(request);

    Game game =
            new Game(
                    request.getGameId(),
                    request.getHomeTeam(),
                    request.getAwayTeam(),
                    0,
                    0,
                    MatchStatus.SCHEDULED,
                    request.getScheduledStart(),
                    null,
                    null);

    Game existing =
            games.putIfAbsent(
                    game.getGameId(),
                    game);

    if(existing != null){
        throw new GameAlreadyExistsException();
    }

    return game;
}
```

ConcurrentHashMap atomically inserts the Game.

---

# getGame()

```java
public Game getGame(Long gameId){

    Game game = games.get(gameId);

    if(game == null){
        throw new GameNotFoundException();
    }

    game.getLock().readLock().lock();

    try{

        return game;

    }finally{

        game.getLock().readLock().unlock();

    }

}
```

Multiple readers may hold the read lock simultaneously.

---

# updateScore()

```java
public Game updateScore(UpdateScoreRequest request){

    Game game = games.get(request.getGameId());

    if(game == null){
        throw new GameNotFoundException();
    }

    game.getLock().writeLock().lock();

    try{

        game.setHomeScore(request.getHomeScore());

        game.setAwayScore(request.getAwayScore());

        game.setStatus(request.getStatus());

        return game;

    }finally{

        game.getLock().writeLock().unlock();

    }

}
```

The write lock guarantees that readers cannot observe a partially updated Game object.

---

# deleteGame()

```java
public void deleteGame(Long gameId){

    Game removed = games.remove(gameId);

    if(removed == null){
        throw new GameNotFoundException();
    }

}
```

ConcurrentHashMap safely removes the entry.

---

# 10. Why Release the Lock?

The lock exists only to protect the object while it is changing.

Timeline

```
Acquire Write Lock

↓

Update Home Score

↓

Update Away Score

↓

Update Status

↓

Release Lock
```

Once the object reaches a consistent state, the lock is no longer needed.

Readers and writers may proceed.

---

# Why finally?

```java
lock.writeLock().lock();

try{

    update();

}finally{

    lock.writeLock().unlock();

}
```

Reason

Even if update() throws an exception,

finally always executes.

Without finally,

the lock may never be released,

causing all future readers and writers to block forever.

---

# ReadWriteLock Behavior

Multiple Readers

```
Reader A

Reader B

Reader C
```

Allowed simultaneously.

---

Reader + Writer

```
Reader

↓

Reading

Writer

↓

Wait
```

Writer waits until all readers finish.

---

Writer + Reader

```
Writer

↓

Updating

Reader

↓

Wait
```

Reader waits until the writer finishes.

---

Writer + Writer

```
Writer A

↓

Updating

Writer B

↓

Wait
```

Only one writer at a time.

---

# ConcurrentHashMap vs ReadWriteLock

ConcurrentHashMap protects

```
Map Operations

get()

put()

remove()

compute()

putIfAbsent()
```

ReadWriteLock protects

```
Mutable State

inside

Game Object
```

They solve different problems.

---

# Immutable vs ReadWriteLock

Immutable Objects

Pros

- Readers never block.
- No partially updated state.
- Simpler reasoning.
- Ideal for read-heavy systems.

Cons

- Creates a new object for each update.

---

ReadWriteLock

Pros

- Reuses the same object.
- Better for very large mutable objects.

Cons

- Readers may block.
- More complex.
- Easier to introduce locking bugs.

---

# Apple Interview Recommendation

For a read-heavy service like Apple Sports

Preferred approach

```
ConcurrentHashMap

+

Immutable Game

+

compute()
```

Alternative

```
ConcurrentHashMap

+

Mutable Game

+

ReadWriteLock
```

Both are valid.

The important part is explaining the trade-offs.

---

# Key Interview Takeaways

1. compute() does NOT prevent context switching.

2. compute() atomically protects updates to a single map entry.

3. ConcurrentHashMap protects the container.

4. ReadWriteLock protects shared mutable objects.

5. Immutable objects eliminate partially updated state.

6. Stale but consistent data is acceptable.

7. Container thread safety and object thread safety are separate concerns.

8. ConcurrentHashMap guarantees thread safety for operations on the map itself—such as inserting, retrieving, replacing, or atomically computing values. It stores references to objects, but it has no knowledge of the internal state of those objects. Once a thread retrieves a mutable Game, protecting its fields becomes the responsibility of the application, typically through immutability or explicit synchronization such as a ReadWriteLock.


# Java Concurrency - Why compute() Alone Is Not Enough

---

# Question

If `ConcurrentHashMap` provides `compute()`, why do we still need:

- Immutable Objects
- ReadWriteLock

Isn't `compute()` enough?

---

# Short Answer

No.

`compute()` and `Immutable Objects` / `ReadWriteLock` solve **different problems**.

- `compute()` protects **the map entry**.
- Immutable Objects / ReadWriteLock protect **the object stored inside the map**.

---

# Responsibility of compute()

`compute()` guarantees that **only one thread can perform a read-modify-write operation on the same map entry at a time.**

Example

```java
games.compute(gameId, (id, game) -> {

    game.setHomeScore(game.getHomeScore() + 2);

    return game;

});
```

Internally Java performs

```
Acquire synchronization for key

↓

Read current value

↓

Execute lambda

↓

Store returned value

↓

Release synchronization
```

While Thread A is executing

```
compute(gameId)
```

another thread executing

```
compute(gameId)
```

must wait.

This prevents **two concurrent writes** from corrupting the same map entry.

---

# What compute() Protects

```
ConcurrentHashMap

↓

Map Entry

↓

Key

↓

Reference
```

It protects operations performed on the map.

Examples

- put()
- remove()
- compute()
- putIfAbsent()
- replace()

---

# What compute() Does NOT Protect

It does NOT protect

```
Game Object

↓

homeScore

awayScore

status
```

Once a thread has a reference to the Game object,

ConcurrentHashMap no longer controls access to that object.

---

# Example - Why compute() Alone Is Not Enough

Suppose the map contains

```
Key 100

↓

Game A

20-18
```

There are two possible scenarios.

---

## Scenario 1 - Thread B Already Has a Reference

Timeline

```
Time 1

Thread B

Game game = games.get(100);

↓

Thread B now references Game A
```

Later

```
Time 2

Thread A

games.compute(100,...)
```

Inside compute()

```java
games.compute(100, (id, game) -> {

    game.setHomeScore(22);

    game.setAwayScore(20);

    game.setStatus(LIVE);

    return game;

});
```

During execution

```
Game A

20-18

↓

22-18
```

CPU switches.

Thread B already has

```java
Game game;
```

Thread B executes

```java
game.getHomeScore();
```

Thread B observes

```
22-18
```

This is an inconsistent state because

```
Away Score

↓

Status
```

have not yet been updated.

---

## Scenario 2 - Thread B Calls get() While compute() Is Running

Suppose Thread A is already executing

```java
games.compute(...)
```

Now Thread B executes

```java
Game game = games.get(100);
```

Can this happen?

Yes.

ConcurrentHashMap allows concurrent reads.

A simple get() does **not** block the same way another compute() does.

If the Game object is mutable,

Thread B may obtain a reference to that same object while Thread A is modifying it.

Again,

Thread B may observe

```
22-18
```

instead of

```
22-20
```

The problem is **not** ConcurrentHashMap.

The problem is that both threads share the same mutable Game object.

---

# Did compute() Fail?

No.

compute() did exactly what it promised.

It guarantees that only one thread performs a read-modify-write operation on the same map entry.

For example

```
Thread A

compute(gameId)
```

and

```
Thread B

compute(gameId)
```

cannot execute simultaneously.

Thread B waits.

However,

compute() does **NOT** protect the internal state of a mutable object.

If another thread already has—or concurrently obtains—a reference to that object,

it may observe the object while it is being modified.

---

# Why Doesn't ConcurrentHashMap Stop Thread B?

ConcurrentHashMap protects

```
The Map

↓

Key

↓

Reference
```

It does NOT protect

```
Game Object

↓

Fields
```

Once a thread has a reference to the Game,

or obtains one through get(),

ConcurrentHashMap no longer controls how that object is accessed.

At that point,

the responsibility shifts to the application.

You have two choices.

1. Make the object immutable.

OR

2. Synchronize access to the object using a ReadWriteLock.

---

# Solution 1 - Immutable Object

Instead of modifying

```java
game.setHomeScore(...);
```

Create a new object.

```java
games.compute(gameId, (id, oldGame) -> {

    return new Game(
            oldGame.getGameId(),
            oldGame.getHomeTeam(),
            oldGame.getAwayTeam(),
            oldGame.getHomeScore() + 2,
            oldGame.getAwayScore(),
            oldGame.getStatus(),
            oldGame.getScheduledStart(),
            oldGame.getActualStart(),
            oldGame.getActualEnd());

});
```

Timeline

Initially

```
Game A

20-18
```

Thread B

```
References Game A
```

Thread A creates

```
Game B

22-18
```

ConcurrentHashMap replaces the map reference.

```
Key 100

↓

Game B
```

Thread B still references

```
Game A

20-18
```

Game A never changes.

Thread B sees

```
20-18
```

which is

- old
- but valid
- internally consistent

Thread B NEVER observes

```
22-18
```

---

# Solution 2 - ReadWriteLock

Keep the Game mutable.

Protect the Game object.

Writer

```java
game.getLock().writeLock().lock();

try {

    game.setHomeScore(22);

    game.setAwayScore(20);

    game.setStatus(LIVE);

} finally {

    game.getLock().writeLock().unlock();

}
```

Reader

```java
game.getLock().readLock().lock();

try {

    return game;

} finally {

    game.getLock().readLock().unlock();

}
```

Now

Thread B cannot read

while

Thread A is updating.

Thread B waits until

```
Home Score

Away Score

Status
```

are all updated.

Then it proceeds.

---

# Why Release the Lock?

The lock exists only while the object is changing.

Timeline

```
Acquire Write Lock

↓

Update Home Score

↓

Update Away Score

↓

Update Status

↓

Release Write Lock
```

Once the object reaches a consistent state,

the lock is released,

allowing readers and writers to continue.

---

# Why unlock() in finally?

```java
lock.writeLock().lock();

try {

    update();

} finally {

    lock.writeLock().unlock();

}
```

If update() throws an exception,

the lock is still released.

Otherwise,

every future reader and writer would wait forever.

---

# compute() vs Immutable vs ReadWriteLock

| Feature | compute() | Immutable Object | ReadWriteLock |
|----------|-----------|------------------|---------------|
| Protects Map Entry | ✅ | ❌ | ❌ |
| Serializes writes for the same map entry | ✅ | ✅ (when used with compute) | ✅ (when writers use the lock) |
| Protects mutable object state | ❌ | ✅ | ✅ |
| Prevents readers seeing partial updates | ❌ | ✅ | ✅ |
| Readers block during update | N/A | ❌ | ✅ |
| Creates new objects | Depends on implementation | ✅ | ❌ |

---

# Mental Model

```
ConcurrentHashMap

↓

Protects

↓

Map Entry

----------------------------

Game Object

↓

Needs Protection

↓

Choose ONE

↓

Immutable Object

OR

ReadWriteLock
```

---

# Apple Interview Answer

**Question**

"If ConcurrentHashMap already provides compute(), why would you still use Immutable Objects or ReadWriteLock?"

**Answer**

> `compute()` guarantees that updates to a map entry are atomic and serialized for a given key. However, it only protects operations performed through the map. If another thread already has—or concurrently obtains—a reference to a mutable Game object, it may observe that object while it is being modified. To prevent readers from seeing partially updated state, I would either make the Game immutable and replace it atomically, or protect the mutable Game with a ReadWriteLock.

---

# Final Mental Model

There are **three different responsibilities**.

```
ConcurrentHashMap

↓

Protects the Map

-------------------------

compute()

↓

Serializes writes for one map entry

-------------------------

Immutable Object
OR
ReadWriteLock

↓

Protects the Game object's internal state
```

A senior Java engineer keeps these three responsibilities separate.