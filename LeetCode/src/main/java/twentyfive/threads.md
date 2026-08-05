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

# Java CompletableFuture - Senior Interview Notes

---

# Why CompletableFuture?

Suppose we need to fetch a Player.

Traditional code

```java
Player player = playerService.getPlayer(playerId);
```

The request thread waits until the Player is returned.

```
Request Thread

↓

Call API

↓

WAIT

↓

Player Returned

↓

Continue
```

This blocks the thread.

---

# CompletableFuture

Instead we can do

```java
CompletableFuture<Player> future =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));
```

Now

```
Request Thread

↓

Create CompletableFuture

↓

Worker Thread Fetches Player

↓

Request Thread Continues
```

The request thread is no longer blocked.

---

# What does CompletableFuture contain?

Initially it does NOT contain a Player.

It contains a promise that

```
A Player

WILL

be available later.
```

Initially

```
CompletableFuture<Player>

↓

PENDING
```

Later

```
CompletableFuture<Player>

↓

COMPLETED

↓

Player
```

or

```
CompletableFuture<Player>

↓

FAILED

↓

Exception
```

So a CompletableFuture has three states.

```
PENDING

↓

COMPLETED

or

FAILED
```

---

# supplyAsync()

```java
CompletableFuture<Player> future =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));
```

Question

What thread executes

```java
playerService.getPlayer(...)
```

Answer

A worker thread from the ForkJoinPool
(or a custom Executor if supplied).

The request thread immediately continues.

---

# How do we get the result?

Two methods.

```
future.get()

future.join()
```

---

# future.get()

```java
Player player = future.get();
```

Behavior

- waits until completion
- returns Player

Throws checked exceptions

```java
InterruptedException

ExecutionException
```

So Java forces us to write

```java
try {

    Player player = future.get();

} catch (InterruptedException e) {

} catch (ExecutionException e) {

}
```

---

# future.join()

```java
Player player = future.join();
```

Also waits.

Also returns Player.

But throws

```java
CompletionException
```

which is an unchecked exception.

So

```java
Player player = future.join();
```

needs no try/catch.

---

# get() vs join()

| Feature | get() | join() |
|----------|--------|---------|
| Waits | ✅ | ✅ |
| Returns Result | ✅ | ✅ |
| Checked Exception | ✅ | ❌ |
| Runtime Exception | ❌ | ✅ |
| Preferred with CompletableFuture | ❌ | ✅ |

---

# Chaining Futures

Suppose

```java
Player player =
        playerService.getPlayer(id);

Team team =
        teamService.getTeam(player.getTeamId());
```

Notice

```
Player

↓

Team
```

Team depends on Player.

---

# thenApply()

Suppose after getting Player we simply need

```java
player.getName()
```

```java
CompletableFuture<String> future =
        CompletableFuture
                .supplyAsync(() ->
                        playerService.getPlayer(id))
                .thenApply(player ->
                        player.getName());
```

Input

```
Player
```

Output

```
String
```

No asynchronous call.

Only transformation.

Think of

```
Stream.map()
```

```
Player

↓

String
```

---

# thenCompose()

Suppose instead

```java
player ->
teamService.getTeamAsync(player.getTeamId())
```

returns

```java
CompletableFuture<Team>
```

NOT

```java
Team
```

If we write

```java
.thenApply(player ->
        teamService.getTeamAsync(player.getTeamId()))
```

the result becomes

```java
CompletableFuture<
        CompletableFuture<Team>>
```

Future inside another Future.

Almost never desired.

Instead use

```java
.thenCompose(player ->
        teamService.getTeamAsync(player.getTeamId()))
```

Java automatically flattens

```
Future

↓

Future

↓

Team
```

into

```
Future

↓

Team
```

---

# Rule

If lambda returns

```java
String

Integer

Player

Game
```

Use

```java
thenApply()
```

If lambda returns

```java
CompletableFuture<Team>

CompletableFuture<Player>

CompletableFuture<Game>
```

Use

```java
thenCompose()
```

---

# thenApply vs thenCompose

| thenApply | thenCompose |
|------------|-------------|
| Synchronous transformation | Starts another async operation |
| Returns normal object | Returns CompletableFuture |
| Similar to Stream.map() | Similar to Stream.flatMap() |

---

# Running Independent Tasks

Suppose

```java
Player player =
        playerService.getPlayer(id);

Stats stats =
        statsService.getStats(id);
```

Neither depends on the other.

They can execute in parallel.

---

# Parallel Execution

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(id));

CompletableFuture<Stats> statsFuture =
        CompletableFuture.supplyAsync(() ->
                statsService.getStats(id));
```

Both start immediately.

```
Player Thread

↓

Player API

--------------------

Stats Thread

↓

Stats API
```

---

# thenCombine()

After BOTH complete

build

```java
PlayerResponse
```

```java
CompletableFuture<PlayerResponse> responseFuture =
        playerFuture.thenCombine(
                statsFuture,
                (player, stats) ->
                        new PlayerResponse(player, stats));
```

Timeline

```
Player Future

↓

Player

---------------

Stats Future

↓

Stats

↓

thenCombine()

↓

PlayerResponse
```

---

# Why not just join()?

This works

```java
Player player = playerFuture.join();

Stats stats = statsFuture.join();

return new PlayerResponse(player, stats);
```

Because both futures already started.

However

```
join()
```

blocks the current thread.

```
thenCombine()
```

keeps everything inside the asynchronous pipeline.

This is the preferred CompletableFuture style.

---

# Mental Model

thenApply()

```
Player

↓

String
```

Simple transformation.

---

thenCompose()

```
Player

↓

Future<Team>
```

Chain another async call.

---

thenCombine()

```
Player Future

AND

Stats Future

↓

Combine

↓

Response
```

Used when tasks are independent.

---

# Sports Backend Example

```java
CompletableFuture<Game> gameFuture =
        gameService.getGameAsync(gameId);

CompletableFuture<Odds> oddsFuture =
        oddsService.getOddsAsync(gameId);

CompletableFuture<Standings> standingsFuture =
        standingsService.getStandingsAsync(leagueId);
```

All execute simultaneously.

Then

```java
gameFuture
        .thenCombine(oddsFuture,
                GameWithOdds::new)
        .thenCombine(standingsFuture,
                (gameOdds, standings) ->
                        new MatchResponse(
                                gameOdds,
                                standings));
```

---

# Summary Table

| Situation | Method |
|-----------|--------|
| Start async task | supplyAsync() |
| Wait for result | join() |
| Wait with checked exceptions | get() |
| Transform object | thenApply() |
| Chain another async API | thenCompose() |
| Combine two independent async tasks | thenCombine() |

---

# Apple / Audible Interview Answer

Question

"When would you use thenApply(), thenCompose(), and thenCombine()?"

Answer

> I use `thenApply()` when I want to synchronously transform the result of a completed future. I use `thenCompose()` when the transformation itself is asynchronous and returns another CompletableFuture, because it avoids nested futures by flattening them. I use `thenCombine()` when I have two independent asynchronous operations running in parallel and want to combine both results once they complete.

# CompletableFuture vs Kafka - When to Use Which?

One of the most common misconceptions is thinking that **CompletableFuture and Kafka solve the same problem.**

They don't.

They solve **completely different architectural problems.**

---

# The Wrong Question

Many engineers think

```
CompletableFuture

vs

Kafka
```

This is the wrong comparison.

Instead ask

```
Am I solving

Request Parallelism

OR

Event Distribution?
```

---

# Problem 1 - User is Waiting (HTTP Request)

Suppose a user opens the ESPN app.

```
GET /match/123
```

The request reaches

```
Game Service
```

To build the response we need

- Redis Cache
- Odds Service
- Standings Service
- Player Service

Architecture

```
                Game Service

                    │

        ┌───────────┼───────────┐

        │           │           │

     Redis      Odds API     Player API

                    │

            Standings API
```

Notice

All these operations are

- independent
- required for ONE response

The user is waiting.

---

# Should we use Kafka?

Imagine

```
User

↓

GET /match/123

↓

Publish Kafka Event
```

Now

```
Kafka Consumer

↓

Fetch Redis

↓

Fetch Odds

↓

Fetch Player

↓

Publish Another Event

↓

Eventually Return Response
```

This would be terrible.

Why?

Because Kafka is not designed for request-response communication.

The user wants the response immediately.

---

# Correct Solution

Run everything in parallel using CompletableFuture.

```java
CompletableFuture<Game> gameFuture =
        CompletableFuture.supplyAsync(() ->
                gameService.getGame(gameId));

CompletableFuture<Odds> oddsFuture =
        CompletableFuture.supplyAsync(() ->
                oddsService.getOdds(gameId));

CompletableFuture<PlayerStats> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayerStats(gameId));

CompletableFuture<Standings> standingsFuture =
        CompletableFuture.supplyAsync(() ->
                standingsService.getStandings(leagueId));
```

Wait for all

```java
CompletableFuture.allOf(
        gameFuture,
        oddsFuture,
        playerFuture,
        standingsFuture).join();
```

Build response

```java
return new MatchResponse(
        gameFuture.join(),
        oddsFuture.join(),
        playerFuture.join(),
        standingsFuture.join());
```

---

# Timeline

```
User Request

↓

Game Service

↓

Redis ----------150ms

Odds ----------250ms

Player---------200ms

Standings------300ms

↓

allOf()

↓

300ms

↓

Return Response
```

Without parallelism

```
150

+

250

+

200

+

300

=

900 ms
```

With CompletableFuture

```
300 ms
```

Huge improvement.

---

# Problem 2 - Provider Sends Webhook

Now imagine a completely different scenario.

```
Sports Provider

↓

Webhook

↓

Game Service
```

A touchdown occurred.

Need to

- Update Redis
- Update Cassandra
- Update Analytics
- Notify Users

Architecture

```
Webhook

↓

Game Service
```

---

# Could we use CompletableFuture?

Technically yes.

```java
CompletableFuture.runAsync(() -> updateRedis());

CompletableFuture.runAsync(() -> updateCassandra());

CompletableFuture.runAsync(() -> sendNotification());
```

Would it work?

Yes.

Would it be the best architecture?

Usually NO.

---

# Why Kafka is Better

Instead

```
Webhook

↓

Validate

↓

Persist Event

↓

Publish Kafka Event
```

Now

```
                 Kafka

         ┌────────┼────────┐

         │        │        │

Redis   Analytics  Notification

Consumer Consumer    Consumer

         │        │        │

Update   Store    Send Push
```

Notice

Each service

- works independently
- retries independently
- scales independently
- can replay messages
- is loosely coupled

Kafka provides

- durability
- retries
- ordering
- buffering
- replay
- decoupling

All things CompletableFuture cannot provide.

---

# The Difference

## CompletableFuture

```
One Request

↓

Need Several Results

↓

Run in Parallel

↓

Return One Response
```

Used

Inside ONE JVM

Inside ONE Service

Inside ONE Request

---

## Kafka

```
One Event Happened

↓

Many Services Need It

↓

Distribute Event

↓

Each Service Processes Independently
```

Used

Between Microservices

Across Multiple Services

Event Driven Architecture

---

# Sports Backend Examples

## Example 1 - User Opens Match

```
GET /match/123
```

Need

- Redis
- Odds
- Player Stats
- Standings

Use

```
CompletableFuture
```

Because

Only THIS request needs the data.

---

## Example 2 - Score Update Arrives

```
Provider

↓

Webhook
```

Need

- Redis
- Cassandra
- Analytics
- Notifications

Use

```
Kafka
```

Because

Many downstream services need the event.

---

# Mental Model

## CompletableFuture

```
HTTP Request

↓

One Service

↓

Parallel API Calls

↓

Return Response
```

---

## Kafka

```
Event

↓

Kafka

↓

Service A

Service B

Service C

↓

Each Processes Independently
```

---

# Simple Rule

## Is a user waiting?

```
YES

↓

CompletableFuture
```

---

## Is an event being distributed?

```
YES

↓

Kafka
```

---

# CompletableFuture vs Kafka

| CompletableFuture | Kafka |
|-------------------|-------|
| Inside one service | Between services |
| Parallelizes work | Distributes events |
| Request-response | Event-driven |
| One JVM | Multiple microservices |
| Low latency | Reliable asynchronous processing |
| Used when client is waiting | Used when no client is waiting |

---

# Apple / Audible Interview Answer

**Question**

Why didn't you use Kafka instead of CompletableFuture?

**Answer**

> Kafka and CompletableFuture solve different problems. CompletableFuture is used within a single service to execute multiple independent tasks concurrently while building a response for a client request. Kafka is used for asynchronous communication between microservices where durability, retries, ordering, replay, and loose coupling are required. In my Sports backend, I would use CompletableFuture to fetch Redis, odds, standings, and player statistics in parallel for a GET request. For provider webhook events, I would publish the event to Kafka so downstream services like Redis, Cassandra, Analytics, and Notification services can process it independently.

---

# Interview Mental Model

```
User Waiting?

↓

YES

↓

CompletableFuture

-------------------------

Need to Notify Multiple Services?

↓

YES

↓

Kafka
```

# CompletableFuture - Complete Production Example

---

# Problem

Suppose a user opens the Sports application.

```
GET /match/123
```

To build the response we need data from multiple independent services.

- Player Service
- Odds Service
- Stats Service

Each service is independent and can execute in parallel.

---

# Complete Example

```java
public MatchResponse getMatchDetails(Long matchId,
                                     Long playerId,
                                     Long leagueId) {

    // Start all asynchronous tasks

    CompletableFuture<Player> playerFuture =
            CompletableFuture.supplyAsync(() ->
                    playerService.getPlayer(playerId));

    CompletableFuture<Odds> oddsFuture =
            CompletableFuture.supplyAsync(() ->
                    oddsService.getOdds(matchId));

    CompletableFuture<Stats> statsFuture =
            CompletableFuture.supplyAsync(() ->
                    statsService.getStats(leagueId));

    // Wait until ALL tasks complete

    CompletableFuture.allOf(
            playerFuture,
            oddsFuture,
            statsFuture)
            .join();

    // Retrieve results
    // (returns immediately because all futures have already completed)

    Player player = playerFuture.join();

    Odds odds = oddsFuture.join();

    Stats stats = statsFuture.join();

    // Build final response

    return new MatchResponse(
            player,
            odds,
            stats
    );
}
```

---

# Step-by-Step Execution

## Step 1

Client sends

```
GET /match/123
```

Spring creates a request thread.

```
Request Thread
```

---

## Step 2

The request thread starts all asynchronous tasks.

```
Request Thread

↓

playerFuture

↓

Worker Thread A
```

```
Request Thread

↓

oddsFuture

↓

Worker Thread B
```

```
Request Thread

↓

statsFuture

↓

Worker Thread C
```

Notice

All three tasks begin immediately.

---

## Step 3

The request thread reaches

```java
CompletableFuture.allOf(
        playerFuture,
        oddsFuture,
        statsFuture)
        .join();
```

The request thread now waits.

Meanwhile

```
Worker Thread A

↓

Player Service
```

```
Worker Thread B

↓

Odds Service
```

```
Worker Thread C

↓

Stats Service
```

All three services execute simultaneously.

---

## Step 4

Suppose the services take

```
Player Service

300 ms
```

```
Odds Service

200 ms
```

```
Stats Service

250 ms
```

Timeline

```
Player ----------------------300 ms

Odds ----------------200 ms

Stats --------------250 ms

↓

allOf()

↓

300 ms

↓

Continue
```

The request waits only for the slowest task.

Without parallel execution

```
300

+

200

+

250

=

750 ms
```

With CompletableFuture

```
300 ms
```

---

## Step 5

Now the request thread executes

```java
Player player = playerFuture.join();

Odds odds = oddsFuture.join();

Stats stats = statsFuture.join();
```

Question

Why don't these block?

Because

```
allOf().join()
```

has already waited until every future completed.

So each

```java
future.join()
```

returns immediately.

---

## Step 6

Finally

```java
return new MatchResponse(
        player,
        odds,
        stats);
```

Spring Boot converts

```
MatchResponse
```

into JSON and returns it to the client.

---

# Visual Flow

```
                     Request Thread

                           │

        ┌──────────────────┼──────────────────┐

        │                  │                  │

Player Future        Odds Future        Stats Future

        │                  │                  │

 Worker Thread A     Worker Thread B    Worker Thread C

        │                  │                  │

 Player Service      Odds Service       Stats Service

        │                  │                  │

        └──────────────────┼──────────────────┘

                  CompletableFuture.allOf()

                           │

                         join()

                           │

                 All Futures Completed

                           │

             playerFuture.join()

             oddsFuture.join()

             statsFuture.join()

                           │

                  Build MatchResponse

                           │

                  Return HTTP Response
```

---

# Why not call join() immediately?

❌ Incorrect

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));

Player player = playerFuture.join();
```

Timeline

```
Create Future

↓

Immediately Wait

↓

Player Returned

↓

Continue
```

Very little benefit.

---

# Correct Pattern

```
Start Future A

↓

Start Future B

↓

Start Future C

↓

Let them all execute in parallel

↓

Wait once

↓

Retrieve all results

↓

Build response
```

---

# Rule

Never do this

```
Start Future

↓

Immediately join()
```

Instead

```
Start ALL futures

↓

Wait once

↓

Collect all results
```

---

# Interview Explanation

> When a request arrives, I immediately start all independent I/O operations using `CompletableFuture.supplyAsync()`. Each task runs concurrently on a worker thread. Instead of blocking after every API call, I allow all tasks to execute in parallel and wait only once using `CompletableFuture.allOf().join()`. After all tasks complete, each individual `join()` returns immediately because the results are already available. Finally, I combine the results into a single response object. This reduces the response time from the sum of all API latencies to approximately the duration of the slowest API call.

# CompletableFuture Exception Handling - Complete Interview Notes

---

# The Problem

Suppose we have two asynchronous operations.

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));

CompletableFuture<Odds> oddsFuture =
        CompletableFuture.supplyAsync(() ->
                oddsService.getOdds(matchId));
```

Everything works fine until one service fails.

For example

```
Odds Service

↓

500 Internal Server Error
```

or

```
Odds Service

↓

Network Timeout
```

Question

**What happens now?**

---

# Default Behavior (No Exception Handling)

Suppose we wait for both futures.

```java
CompletableFuture.allOf(
        playerFuture,
        oddsFuture)
        .join();
```

Timeline

```
Player Future

↓

SUCCESS

-------------------------

Odds Future

↓

EXCEPTION
```

Result

```
CompletableFuture.allOf()

↓

EXCEPTION
```

The request immediately fails.

Even though

```
Player Future
```

completed successfully,

the overall request fails because one future completed exceptionally.

---

# Why?

`CompletableFuture.allOf()` succeeds only if **every future succeeds**.

If even one future fails,

```
allOf().join()
```

throws an exception.

---

# Three Ways to Handle Exceptions

Java provides three methods.

```
exceptionally()

handle()

whenComplete()
```

Each has a different purpose.

---

# 1. exceptionally()

Think of it as

```
try

↓

Exception?

↓

Return Default Value

↓

Continue
```

Example

```java
CompletableFuture<Odds> oddsFuture =
        CompletableFuture
                .supplyAsync(() ->
                        oddsService.getOdds(matchId))
                .exceptionally(ex -> {

                    System.out.println(ex);

                    return Odds.empty();

                });
```

Timeline

```
Odds Service

↓

Exception

↓

exceptionally()

↓

Odds.empty()

↓

Future Completes Successfully
```

Instead of failing,

the future returns a fallback value.

---

# Sports Backend Example

Suppose the Odds provider is temporarily unavailable.

Without exception handling

```
HTTP 500
```

The entire request fails.

With

```java
.exceptionally(...)
```

Return

```
Odds.empty()
```

The response still contains

- Match Details
- Player Statistics
- Standings

Only

```
Odds

↓

Unavailable
```

Much better user experience.

---

# When should I use exceptionally()?

Whenever I want to recover from an exception and provide a default value.

---

# 2. handle()

Think of it as

```
Success

OR

Failure

↓

Always Execute
```

Method signature

```java
.handle((result, ex) -> ...)
```

Notice

It receives BOTH

```
Result

AND

Exception
```

Example

```java
CompletableFuture<Odds> oddsFuture =
        CompletableFuture
                .supplyAsync(() ->
                        oddsService.getOdds(matchId))
                .handle((odds, ex) -> {

                    if (ex != null) {

                        return Odds.empty();

                    }

                    return odds;

                });
```

Timeline

Success

```
Odds

↓

handle()

↓

Odds
```

Failure

```
Exception

↓

handle()

↓

Odds.empty()
```

Unlike

```
exceptionally()
```

this method always executes.

---

# When should I use handle()?

When I want to process BOTH

- successful results
- failed results

inside one method.

---

# Difference Between exceptionally() and handle()

## exceptionally()

Only executes

```
ON FAILURE
```

---

## handle()

Executes

```
ON SUCCESS

AND

ON FAILURE
```

---

# 3. whenComplete()

Think of it as

```
finally
```

It is mainly used for

- Logging
- Metrics
- Auditing
- Monitoring

Example

```java
CompletableFuture<Odds> oddsFuture =
        CompletableFuture
                .supplyAsync(() ->
                        oddsService.getOdds(matchId))
                .whenComplete((odds, ex) -> {

                    if (ex == null) {

                        log.info("Odds retrieved successfully");

                    } else {

                        log.error("Odds retrieval failed", ex);

                    }

                });
```

Notice

It does NOT recover from the exception.

It simply observes the outcome.

Timeline

```
Odds Service

↓

Exception

↓

whenComplete()

↓

Log Error

↓

Future Still Fails
```

The exception continues to propagate.

---

# When should I use whenComplete()?

Whenever I only want to

- log
- audit
- publish metrics

without changing the result.

---

# Summary

## exceptionally()

```
Failure

↓

Recover

↓

Return Default Value
```

---

## handle()

```
Success

↓

Transform

OR

Failure

↓

Recover
```

---

## whenComplete()

```
Success

↓

Log

---------------------

Failure

↓

Log

↓

Continue Original Result
```

---

# Real Sports Backend Example

Suppose

```
GET /match/123
```

Needs

```
Redis

Odds

Player

Standings
```

Suppose

```
Odds Service

↓

Timeout
```

Without exception handling

```
HTTP 500
```

With

```java
.exceptionally(ex -> Odds.empty())
```

Flow

```
Redis

↓

Player

↓

Standings

↓

Odds.empty()

↓

Build MatchResponse

↓

HTTP 200
```

The user still receives useful data.

Only the odds section is unavailable.

---

# Which One Should I Use?

## Need fallback data?

Use

```java
exceptionally()
```

---

## Need to process both success and failure?

Use

```java
handle()
```

---

## Need only logging or metrics?

Use

```java
whenComplete()
```

---

# Comparison Table

| Feature | exceptionally() | handle() | whenComplete() |
|----------|-----------------|----------|----------------|
| Executes on Success | ❌ | ✅ | ✅ |
| Executes on Failure | ✅ | ✅ | ✅ |
| Can Recover from Failure | ✅ | ✅ | ❌ |
| Can Return Different Value | ✅ | ✅ | ❌ |
| Best Use Case | Fallback Value | Transform + Recovery | Logging / Metrics |

---

# Timeline Comparison

## exceptionally()

```
Future

↓

Exception

↓

Fallback Value

↓

Continue
```

---

## handle()

```
Future

↓

Success

↓

Transform

OR

↓

Failure

↓

Fallback

↓

Continue
```

---

## whenComplete()

```
Future

↓

Success / Failure

↓

Log

↓

Original Result Continues
```

---

# Apple / Audible Interview Question

**Question**

What happens if one CompletableFuture fails?

**Answer**

> By default, if one CompletableFuture completes exceptionally, `CompletableFuture.allOf().join()` also completes exceptionally and the overall request fails. If I want to tolerate partial failures, such as the Odds service being temporarily unavailable, I can use `exceptionally()` or `handle()` to return a fallback object like `Odds.empty()`. If I simply want to log the outcome without changing the result, I use `whenComplete()`. This allows the application to degrade gracefully while still returning useful data to the client.