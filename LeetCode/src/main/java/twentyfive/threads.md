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

# Is `allOf()` Chaining?

## Short Answer

**No.**

`CompletableFuture.allOf()` is **not chaining**.

It is a **coordination/synchronization mechanism** that waits for multiple independent futures to complete.

---

# What is Chaining?

Chaining means

> Each operation depends on the result of the previous operation.

Example

```java
CompletableFuture.supplyAsync(() ->
        playerService.getPlayer(playerId))
    .thenApply(player ->
        player.getName())
    .thenCompose(name ->
        teamService.getTeamAsync(name))
    .thenApply(team ->
        team.getCoach());
```

Flow

```
Player

↓

thenApply()

↓

Player Name

↓

thenCompose()

↓

Team

↓

thenApply()

↓

Coach
```

Notice

Every step depends on the previous step.

This is called **chaining**.

---

# What is `allOf()`?

Suppose we have three completely independent tasks.

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));

CompletableFuture<Odds> oddsFuture =
        CompletableFuture.supplyAsync(() ->
                oddsService.getOdds(matchId));

CompletableFuture<Stats> statsFuture =
        CompletableFuture.supplyAsync(() ->
                statsService.getStats(leagueId));
```

All three begin immediately.

```
Player Future

||

Odds Future

||

Stats Future
```

Now we simply wait.

```java
CompletableFuture.allOf(
        playerFuture,
        oddsFuture,
        statsFuture)
        .join();
```

Flow

```
Player Future

||

Odds Future

||

Stats Future

↓

Wait for ALL

↓

Continue
```

Notice

None of these futures depends on another.

---

# Key Difference

## Chaining

```
Future A

↓

Future B

↓

Future C
```

Each stage depends on the previous stage.

---

## allOf()

```
Future A

||

Future B

||

Future C

↓

Synchronization

↓

Continue
```

No dependency exists between the futures.

---

# Interview Definition

**Chaining**

> A sequence of dependent asynchronous operations where each stage uses the result of the previous stage.

Examples

- `thenApply()`
- `thenCompose()`
- `thenCombine()`

---

**allOf()**

> A coordination/synchronization mechanism that waits until multiple independent `CompletableFuture`s complete before continuing.

It is **not chaining**.

---

# Apple Interview Answer

**Question**

Is `CompletableFuture.allOf()` considered chaining?

**Answer**

> No. Chaining means each asynchronous stage depends on the result of the previous stage, such as with `thenApply()` or `thenCompose()`. `CompletableFuture.allOf()` does not create dependencies between futures. Instead, it synchronizes multiple independent futures and waits until all of them complete before continuing.


# CompletableFuture - thenApply(), thenCompose(), allOf() (Complete Guide)

---

# Scenario

Suppose we are building a Sports application.

A user opens

```
GET /match/123
```

We need to fetch

- Player
- Team
- Odds
- Stats

Some operations depend on others, while others are completely independent.

---

# Domain Objects

```java
public class Player {

    private Long playerId;
    private String name;
    private Long teamId;

    public Long getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public Long getTeamId() {
        return teamId;
    }
}
```

```java
public class Team {

    private Long teamId;
    private String teamName;
    private String coach;

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getCoach() {
        return coach;
    }
}
```

---

# Services

## PlayerService

```java
@Service
public class PlayerService {

    public Player getPlayer(Long playerId) {

        // Database/API call

        return playerRepository.findById(playerId);

    }

}
```

---

## TeamService

Notice

This is NOT a Java built-in method.

This is OUR method.

Internally it uses

```
CompletableFuture.supplyAsync()
```

```java
@Service
public class TeamService {

    public CompletableFuture<Team> getTeamAsync(Long teamId) {

        return CompletableFuture.supplyAsync(() -> {

            // Database/API call

            return teamRepository.findById(teamId);

        });

    }

}
```

---

# Step 1 - Create First Future

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));
```

Initially

```
Future<Player>

↓

PENDING
```

Later

```
Future<Player>

↓

Player
```

---

# thenApply()

Question

Suppose we only need the player's name.

No new async call is needed.

We simply transform the Player.

```java
CompletableFuture<String> playerNameFuture =
        playerFuture.thenApply(player ->
                player.getName());
```

Flow

```
Future<Player>

↓

Player

↓

getName()

↓

Future<String>
```

Notice

No new asynchronous operation starts.

---

# How do we get the String?

Just like any CompletableFuture.

```java
String playerName = playerNameFuture.join();

System.out.println(playerName);
```

Question

Why is it still a Future?

Because

```
thenApply()

↓

returns

↓

CompletableFuture<String>
```

The transformation is also asynchronous.

---

# Another thenApply() Example

Need player's team id.

```java
CompletableFuture<Long> teamIdFuture =
        playerFuture.thenApply(player ->
                player.getTeamId());
```

Flow

```
Future<Player>

↓

Player

↓

getTeamId()

↓

Future<Long>
```

Again

No new async operation.

Only extracting data.

---

# thenCompose()

Suppose after getting the Player

we need to fetch the Team.

Question

How?

Call another asynchronous service.

```java
CompletableFuture<Team> teamFuture =
        playerFuture.thenCompose(player ->
                teamService.getTeamAsync(
                        player.getTeamId()));
```

Notice

```
player

↓

teamService.getTeamAsync(...)
```

returns

```
CompletableFuture<Team>
```

This starts another asynchronous operation.

Flow

```
Future<Player>

↓

Player

↓

Async Team Call

↓

Future<Team>
```

Notice

No nested Future.

```
thenCompose()

↓

flattens

↓

Future<Team>
```

---

# Extract Team

Later

```java
Team team = teamFuture.join();

System.out.println(team.getTeamName());

System.out.println(team.getCoach());
```

---

# Chaining Multiple Operations

Now let's build a complete chain.

```java
CompletableFuture<String> coachFuture =

        CompletableFuture

                .supplyAsync(() ->
                        playerService.getPlayer(playerId))

                .thenApply(player ->
                        player.getTeamId())

                .thenCompose(teamId ->
                        teamService.getTeamAsync(teamId))

                .thenApply(team ->
                        team.getCoach());
```

Flow

```
Future<Player>

↓

Player

↓

TeamId

↓

Async Team Call

↓

Future<Team>

↓

Coach

↓

Future<String>
```

Finally

```java
String coach = coachFuture.join();

System.out.println(coach);
```

---

# allOf()

Now suppose we need

- Player
- Odds
- Stats

These are completely independent.

Start all three immediately.

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));

CompletableFuture<Odds> oddsFuture =
        CompletableFuture.supplyAsync(() ->
                oddsService.getOdds(matchId));

CompletableFuture<Stats> statsFuture =
        CompletableFuture.supplyAsync(() ->
                statsService.getStats(matchId));
```

All begin simultaneously.

```
Player Future

||

Odds Future

||

Stats Future
```

Now wait.

```java
CompletableFuture.allOf(
        playerFuture,
        oddsFuture,
        statsFuture)
        .join();
```

Notice

```
allOf()

↓

Wait for ALL

↓

Continue
```

---

# Extract Objects

Once

```
allOf().join()
```

returns,

every Future has already completed.

Now

```java
Player player = playerFuture.join();

Odds odds = oddsFuture.join();

Stats stats = statsFuture.join();
```

These

```
join()
```

calls return immediately.

No waiting occurs here.

---

# Build Response

```java
MatchResponse response =
        new MatchResponse(
                player,
                odds,
                stats);

return response;
```

---

# Visual Summary

## thenApply()

```
Future<Player>

↓

Player

↓

Extract Property

↓

Future<String>
```

No new async call.

---

## thenCompose()

```
Future<Player>

↓

Player

↓

Call Async Service

↓

Future<Team>
```

Starts another asynchronous operation.

---

## allOf()

```
Future<Player>

||

Future<Odds>

||

Future<Stats>

↓

Wait for ALL

↓

Extract Objects

↓

Build Response
```

---

# Rule of Thumb

## thenApply()

Use when

```
Already have object

↓

Need another value
```

Examples

```java
player.getName()

player.getTeamId()

team.getCoach()

team.getTeamName()
```

---

## thenCompose()

Use when

```
Already have object

↓

Need another asynchronous call
```

Examples

```java
teamService.getTeamAsync(...)

oddsService.getOddsAsync(...)

statsService.getStatsAsync(...)
```

---

## allOf()

Use when

```
Multiple independent Futures

↓

Wait for ALL

↓

Combine Results
```

---

# Apple Interview Answer

**Question**

When do you use `thenApply()`, `thenCompose()`, and `allOf()`?

**Answer**

> I use `thenApply()` when I already have the result of a completed asynchronous operation and simply want to transform it, such as extracting a player's name or team ID. I use `thenCompose()` when the next step requires another asynchronous operation, such as fetching a team after retrieving the player's team ID. Since the second method returns a `CompletableFuture`, `thenCompose()` flattens the nested futures into a single future. I use `CompletableFuture.allOf()` when I have multiple independent asynchronous operations, such as fetching player, odds, and statistics concurrently, and I want to wait until all of them complete before building the final response.		


# CompletableFuture - `thenApply()`, `thenCompose()`, and `allOf()` (Complete Guide)

---

# Goal

Understand

- `thenApply()`
- `thenCompose()`
- `CompletableFuture.allOf()`

by comparing **synchronous code** with **asynchronous code**.

The easiest way to understand CompletableFuture is to first ask:

> **"What would I write if everything was synchronous?"**

Then convert it to asynchronous code.

---

# Domain Objects

## Player

```java
public class Player {

    private Long playerId;
    private String name;
    private Long teamId;

    public Long getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public Long getTeamId() {
        return teamId;
    }
}
```

---

## Team

```java
public class Team {

    private Long teamId;
    private String teamName;
    private String coach;

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getCoach() {
        return coach;
    }
}
```

---

# TeamService - Synchronous

Normally our service looks like this.

```java
@Service
public class TeamService {

    public Team getTeam(Long teamId) {

        // Database/API call

        return teamRepository.findById(teamId);

    }

}
```

Notice

```
Returns

↓

Team
```

The caller waits until the Team is returned.

---

# TeamService - Asynchronous

Suppose fetching the Team is slow.

We make it asynchronous.

```java
@Service
public class TeamService {

    public CompletableFuture<Team> getTeamAsync(Long teamId) {

        return CompletableFuture.supplyAsync(() -> {

            // Database/API call

            return teamRepository.findById(teamId);

        });

    }

}
```

Notice

Instead of returning

```
Team
```

it returns

```
CompletableFuture<Team>
```

Think of this as

```
A Promise

that a Team

will be available later.
```

---

# Synchronous vs Asynchronous Service

| Synchronous | Asynchronous |
|--------------|--------------|
| `Team getTeam()` | `CompletableFuture<Team> getTeamAsync()` |
| Returns Team | Returns Promise<Team> |
| Caller waits | Caller continues |
| Same thread | Worker thread |

---

# thenApply()

## Synchronous Version

Suppose we need the player's name.

```java
Player player =
        playerService.getPlayer(playerId);

String playerName =
        player.getName();
```

Flow

```
Player

↓

Extract Name

↓

String
```

Nothing asynchronous happens.

We simply transform one object into another.

---

# Asynchronous Version

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));

CompletableFuture<String> playerNameFuture =
        playerFuture.thenApply(player ->
                player.getName());
```

Flow

```
Future<Player>

↓

Player

↓

Extract Name

↓

Future<String>
```

Notice

No new asynchronous call starts.

We are simply transforming

```
Player

↓

String
```

---

# How do we get the player's name?

Just like any other CompletableFuture.

```java
String playerName =
        playerNameFuture.join();

System.out.println(playerName);
```

Question

Why is it still a Future?

Because

```
thenApply()

↓

returns

↓

CompletableFuture<String>
```

The transformation itself is part of the asynchronous pipeline.

---

# Another thenApply() Example

Need the player's team id.

```java
CompletableFuture<Long> teamIdFuture =
        playerFuture.thenApply(player ->
                player.getTeamId());

Long teamId =
        teamIdFuture.join();
```

Again

```
Player

↓

Team Id

↓

Future<Long>
```

No new async operation.

---

# thenCompose()

Now suppose we need the Team.

---

# Synchronous Version

```java
Player player =
        playerService.getPlayer(playerId);

Team team =
        teamService.getTeam(player.getTeamId());

String coach =
        team.getCoach();
```

Flow

```
Player

↓

Get Team

↓

Team

↓

Coach
```

---

# Asynchronous Version

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));

CompletableFuture<Team> teamFuture =
        playerFuture.thenCompose(player ->
                teamService.getTeamAsync(
                        player.getTeamId()));

CompletableFuture<String> coachFuture =
        teamFuture.thenApply(team ->
                team.getCoach());

String coach =
        coachFuture.join();

System.out.println(coach);
```

Flow

```
Future<Player>

↓

Player

↓

Async Team Call

↓

Future<Team>

↓

Extract Coach

↓

Future<String>
```

Notice

Fetching the Team is another asynchronous operation.

---

# Why do we need thenCompose()?

Suppose we accidentally wrote

```java
CompletableFuture<CompletableFuture<Team>> teamFuture =

        playerFuture.thenApply(player ->
                teamService.getTeamAsync(
                        player.getTeamId()));
```

Question

What does

```java
teamService.getTeamAsync(...)
```

return?

Answer

```java
CompletableFuture<Team>
```

Since

```
thenApply()
```

automatically wraps the returned value,

we get

```
CompletableFuture<
        CompletableFuture<Team>>
```

A Future inside another Future.

This is almost never what we want.

---

# thenCompose()

Instead

```java
CompletableFuture<Team> teamFuture =

        playerFuture.thenCompose(player ->
                teamService.getTeamAsync(
                        player.getTeamId()));
```

Flow

```
Future<Player>

↓

Player

↓

Async Team Call

↓

Future<Team>
```

Notice

No nested Future.

`thenCompose()` automatically flattens it.

---

# Complete Chaining Example

```java
CompletableFuture<String> coachFuture =

        CompletableFuture

                .supplyAsync(() ->
                        playerService.getPlayer(playerId))

                .thenApply(player ->
                        player.getTeamId())

                .thenCompose(teamId ->
                        teamService.getTeamAsync(teamId))

                .thenApply(team ->
                        team.getCoach());

String coach =
        coachFuture.join();

System.out.println(coach);
```

Flow

```
Future<Player>

↓

Player

↓

Team Id

↓

Async Team Call

↓

Future<Team>

↓

Coach

↓

Future<String>
```

---

# allOf()

Now suppose we need

- Player
- Odds
- Stats

These are completely independent.

---

# Start All Futures

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));

CompletableFuture<Odds> oddsFuture =
        CompletableFuture.supplyAsync(() ->
                oddsService.getOdds(matchId));

CompletableFuture<Stats> statsFuture =
        CompletableFuture.supplyAsync(() ->
                statsService.getStats(matchId));
```

All begin immediately.

```
Player Future

||

Odds Future

||

Stats Future
```

---

# Wait for All

```java
CompletableFuture.allOf(
        playerFuture,
        oddsFuture,
        statsFuture)
        .join();
```

Flow

```
Player Future

||

Odds Future

||

Stats Future

↓

Wait for ALL

↓

Continue
```

---

# Extract Objects

After

```java
allOf().join();
```

every Future has already completed.

```java
Player player = playerFuture.join();

Odds odds = oddsFuture.join();

Stats stats = statsFuture.join();
```

These

```
join()
```

calls return immediately.

---

# Build Response

```java
MatchResponse response =
        new MatchResponse(
                player,
                odds,
                stats);

return response;
```

---

# Visual Summary

## thenApply()

```
Future<Player>

↓

Player

↓

Extract Property

↓

Future<String>
```

Examples

```java
player.getName()

player.getTeamId()

team.getCoach()
```

No new async call.

---

## thenCompose()

```
Future<Player>

↓

Player

↓

Call Async Service

↓

Future<Team>
```

Examples

```java
teamService.getTeamAsync(...)

oddsService.getOddsAsync(...)

statsService.getStatsAsync(...)
```

Starts another asynchronous operation.

---

## allOf()

```
Future<Player>

||

Future<Odds>

||

Future<Stats>

↓

Wait for ALL

↓

Extract Results

↓

Build Response
```

Used for independent asynchronous operations.

---

# Decision Table

| Lambda Returns | Use |
|----------------|-----|
| `String` | `thenApply()` |
| `Integer` | `thenApply()` |
| `Player` | `thenApply()` |
| `Team` | `thenApply()` |
| `CompletableFuture<Player>` | `thenCompose()` |
| `CompletableFuture<Team>` | `thenCompose()` |
| `CompletableFuture<Odds>` | `thenCompose()` |

---

# Rule of Thumb

Don't memorize method names.

Instead, look at the **return type**.

If your lambda returns

```
Normal Object
```

Use

```
thenApply()
```

---

If your lambda returns

```
CompletableFuture
```

Use

```
thenCompose()
```

---

If you have multiple independent Futures

```
Future A

||

Future B

||

Future C
```

Use

```
CompletableFuture.allOf()
```

to wait for all of them before continuing.

---

# Apple / Audible Interview Answer

**Question**

When would you use `thenApply()`, `thenCompose()`, and `allOf()`?

**Answer**

> I use `thenApply()` when I already have the result of a completed asynchronous operation and simply want to transform it into another value, such as extracting a player's name or team ID. I use `thenCompose()` when the next step requires another asynchronous operation that returns a `CompletableFuture`, such as fetching a Team after retrieving the player's team ID. `thenCompose()` flattens the nested futures into a single future. Finally, I use `CompletableFuture.allOf()` when I have multiple independent asynchronous operations, such as fetching player, odds, and statistics concurrently, and I want to wait until all of them complete before building the final response.

# CompletableFuture - Additional Interview Notes

This document captures the follow-up questions and deeper understanding after learning:

- `thenApply()`
- `thenCompose()`
- `allOf()`

---

# Are PlayerService and TeamService in the Same Microservice?

Our examples assumed they are.

```
                Match Service (Spring Boot)

            ┌──────────────────────────────┐

            │ PlayerService                │

            │ TeamService                  │

            │ OddsService                  │

            │ StatsService                 │

            └──────────────────────────────┘
```

Here

```java
@Service
public class PlayerService { }

@Service
public class TeamService { }
```

They are simply Spring beans living inside the same application.

---

# Could They Be Different Microservices?

Absolutely.

This is actually more common in a distributed system.

```
                  API Gateway

                        │

                  Match Service

        ┌───────────────┼────────────────┐

        │               │                │

 Player Service    Odds Service    Team Service
```

Now

```java
playerService.getPlayerAsync(...)
```

might internally call

- REST
- gRPC

to another microservice.

The caller doesn't care.

It still receives

```java
CompletableFuture<Player>
```

---

# Could They Be the Same Microservice But Different Databases?

Yes.

Example

```
                Match Service

        ┌────────────────────────┐

        │ PlayerService          │

        │ TeamService            │

        │ OddsService            │

        └────────────────────────┘

            │       │       │

        PlayerDB TeamDB OddsDB
```

Each service accesses a different database.

Still one microservice.

---

# Could They Be the Same Microservice and Same Database?

Yes.

```
               Match Service

      ┌────────────────────────┐

      │ PlayerService          │

      │ TeamService            │

      └────────────────────────┘

               │

          PostgreSQL
```

Very common.

---

# Which Architecture Is Most Common?

All are valid.

| Architecture | Common? |
|--------------|----------|
| Same Microservice + Same DB | ✅ |
| Same Microservice + Multiple DBs | ✅ |
| Multiple Microservices + Separate DBs | ✅ (Most common in distributed systems) |

For our Sports system,

the third option is the most realistic.

```
                Match Service

                     │

       ┌─────────────┼─────────────┐

       │             │             │

 Player MS      Odds MS      Team MS
```

---

# Synchronous vs Asynchronous Service

## Traditional Service

```java
@Service
public class TeamService {

    public Team getTeam(Long teamId) {

        return teamRepository.findById(teamId);

    }

}
```

Returns

```
Team
```

Caller waits.

---

## Async Service

```java
@Service
public class TeamService {

    public CompletableFuture<Team> getTeamAsync(Long teamId) {

        return CompletableFuture.supplyAsync(() -> {

            return teamRepository.findById(teamId);

        });

    }

}
```

Returns

```
CompletableFuture<Team>
```

Instead of returning the Team immediately,

it returns a promise that the Team will arrive later.

---

# Synchronous vs Asynchronous Flow

## Synchronous

```java
Player player =
        playerService.getPlayer(playerId);

Team team =
        teamService.getTeam(player.getTeamId());

String coach =
        team.getCoach();
```

Timeline

```
Request Thread

↓

Get Player

↓

WAIT

↓

Player

↓

Get Team

↓

WAIT

↓

Team

↓

Coach
```

Everything executes sequentially.

---

## Asynchronous

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));

CompletableFuture<Team> teamFuture =
        playerFuture.thenCompose(player ->
                teamService.getTeamAsync(
                        player.getTeamId()));

CompletableFuture<String> coachFuture =
        teamFuture.thenApply(team ->
                team.getCoach());

String coach =
        coachFuture.join();
```

Timeline

```
Future<Player>

↓

Player

↓

Async Team Call

↓

Future<Team>

↓

Extract Coach

↓

Future<String>

↓

join()

↓

Coach
```

---

# Why Does thenCompose() Need getTeamAsync()?

Suppose our service looked like this.

```java
public Team getTeam(Long teamId)
```

It returns

```
Team
```

NOT

```
CompletableFuture<Team>
```

Then we should write

```java
playerFuture.thenApply(player ->
        teamService.getTeam(
                player.getTeamId()));
```

because the lambda returns

```
Team
```

---

Suppose instead our service is

```java
public CompletableFuture<Team> getTeamAsync(Long teamId)
```

Now the lambda returns

```
CompletableFuture<Team>
```

Therefore we write

```java
playerFuture.thenCompose(player ->
        teamService.getTeamAsync(
                player.getTeamId()));
```

---

# The Rule

Don't look at the method name.

Look at the return type.

If it returns

```
Team
```

↓

Use

```
thenApply()
```

If it returns

```
CompletableFuture<Team>
```

↓

Use

```
thenCompose()
```

---

# Is allOf() Chaining?

No.

`CompletableFuture.allOf()` is **not chaining**.

It is a synchronization mechanism.

---

## Chaining

```
Future<Player>

↓

thenApply()

↓

Future<String>

↓

thenCompose()

↓

Future<Team>

↓

thenApply()

↓

Future<String>
```

Every stage depends on the previous stage.

---

## allOf()

```
Future<Player>

||

Future<Odds>

||

Future<Stats>

↓

Wait For All

↓

Continue
```

The futures are completely independent.

---

# Reactive Programming (High-Level)

Apple mentioned **Reactive Patterns**.

We are intentionally postponing a deep dive until after CompletableFuture.

For now, understand the difference.

---

## Traditional Spring MVC

```java
Player player =
        playerService.getPlayer(id);
```

Thread waits.

```
Request Thread

↓

Call DB

↓

WAIT

↓

Response
```

---

## CompletableFuture

```java
CompletableFuture<Player> future =
        playerService.getPlayerAsync(id);
```

Worker thread performs the work.

```
Request Thread

↓

Start Worker Thread

↓

Wait Later
```

---

## Reactive

Instead of returning

```java
Player
```

or

```java
CompletableFuture<Player>
```

a reactive application returns

```java
Mono<Player>
```

Example

```java
public Mono<Player> getPlayer(Long id) {

    return webClient
            .get()
            .uri("/players/" + id)
            .retrieve()
            .bodyToMono(Player.class);

}
```

Notice

No worker thread is blocked waiting for the response.

Instead,

the framework registers a callback.

When the response arrives,

processing continues.

---

# Reactive Timeline

Traditional

```
Request Thread

↓

Call API

↓

WAIT

↓

Response
```

---

CompletableFuture

```
Request Thread

↓

Worker Thread

↓

Call API

↓

WAIT
```

---

Reactive

```
Request Thread

↓

Register Callback

↓

Release Thread

↓

Response Arrives

↓

Resume Processing
```

---

# Interview Summary

## thenApply()

Use when

```
Lambda returns

↓

Normal Object
```

Examples

```
String

Player

Team

Long
```

---

## thenCompose()

Use when

```
Lambda returns

↓

CompletableFuture
```

Examples

```
CompletableFuture<Player>

CompletableFuture<Team>

CompletableFuture<Odds>
```

---

## allOf()

Use when

```
Multiple Independent Futures

↓

Wait For All

↓

Continue
```

---

# Apple Interview Answer

**Question**

Can these services be part of the same application or different microservices?

**Answer**

> They can be either. In a simple application, they may just be Spring services within the same microservice. In a distributed architecture, they are often separate microservices communicating over REST or gRPC. The orchestration code using `CompletableFuture` remains the same—the only difference is whether the service implementation calls a local repository or a remote service.

---

# Next Topic

The next topic is

**Custom Executors / ThreadPoolTaskExecutor**

This answers one of the most common senior interview questions:

> **"Where do the worker threads used by CompletableFuture actually come from?"**

After that, we'll move into **Reactive Programming (Mono, Flux, WebClient, Event Loops, and Reactive Patterns)** in depth.

# Complete Comparison - Synchronous vs Asynchronous

---

# Synchronous Version

## Architecture

```
HTTP Request

↓

Controller

↓

PlayerService

↓

PlayerRepository (DAO)

↓

Database
```

---

## Repository (DAO)

```java
@Repository
public class PlayerRepository {

    public Player findById(Long playerId) {

        // SQL Query

        return jdbcTemplate.queryForObject(...);

    }

}
```

---

## Service

```java
@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public Player getPlayer(Long playerId) {

        return playerRepository.findById(playerId);

    }

}
```

---

## Controller

```java
@RestController
public class MatchController {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/players/{id}")
    public Player getPlayer(@PathVariable Long id) {

        return playerService.getPlayer(id);

    }

}
```

---

## Flow

```
HTTP Request

↓

Controller

↓

PlayerService

↓

PlayerRepository

↓

Database

↓

Player

↓

PlayerService

↓

Controller

↓

HTTP Response
```

Everything happens synchronously.

The request thread waits until the database returns.

---

# Asynchronous Version

The repository usually stays exactly the same.

## Repository (DAO)

```java
@Repository
public class TeamRepository {

    public Team findById(Long teamId) {

        return jdbcTemplate.queryForObject(...);

    }

}
```

Notice

Nothing changed.

Repositories are normally synchronous.

---

## Service

Now we expose an asynchronous method.

```java
@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public CompletableFuture<Team> getTeamAsync(Long teamId) {

        return CompletableFuture.supplyAsync(() -> {

            return teamRepository.findById(teamId);

        });

    }

}
```

Notice

Instead of returning

```java
Team
```

we return

```java
CompletableFuture<Team>
```

Internally

```
supplyAsync()
```

executes

```
teamRepository.findById(...)
```

on a worker thread.

---

## Calling Service

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));

CompletableFuture<Team> teamFuture =
        playerFuture.thenCompose(player ->
                teamService.getTeamAsync(
                        player.getTeamId()));

CompletableFuture<String> coachFuture =
        teamFuture.thenApply(team ->
                team.getCoach());

String coach =
        coachFuture.join();
```

---

# Flow

```
Request Thread

↓

Create Future<Player>

↓

Worker Thread

↓

PlayerRepository

↓

Database

↓

Player

↓

thenCompose()

↓

Create Future<Team>

↓

Worker Thread

↓

TeamRepository

↓

Database

↓

Team

↓

thenApply()

↓

Coach

↓

join()

↓

Return Response
```

---

# What Changed?

| Synchronous | Asynchronous |
|--------------|--------------|
| Repository returns `Player` | Repository still returns `Player` |
| Service returns `Player` | Service returns `CompletableFuture<Player>` |
| Caller waits | Caller receives a promise |
| Sequential execution | Can compose multiple async operations |

---

# Important Interview Point

Notice that **the repository did not change**.

Only the **service layer** changed.

```
Repository

↓

Still synchronous

-----------------------

Service

↓

Wrapped in CompletableFuture
```

This is very common in Spring Boot applications.

---

# Could These Services Be Different Microservices?

Yes.

Instead of

```
PlayerService

↓

PlayerRepository

↓

Database
```

you could have

```
Match Service

↓

HTTP/gRPC

↓

Player Microservice

↓

PlayerRepository

↓

Database
```

The calling code **does not change**.

It still receives

```java
CompletableFuture<Player>
```

Whether the data comes from

- a local repository,
- another database,
- another microservice,

is an implementation detail hidden inside the service.

---

# Interview Takeaway

The caller should not care where the data comes from.

The service contract remains the same.

```java
CompletableFuture<Player> playerFuture =
        playerService.getPlayerAsync(playerId);
```

Internally, that service may:

- Query a local database.
- Call another microservice.
- Read from Redis.
- Call a third-party REST API.

The caller simply waits on a `CompletableFuture<Player>`.


# CompletableFuture - Thread Pools and Executors (Complete Interview Notes)

---

# The Big Question

Earlier we wrote

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(() ->
                playerService.getPlayer(playerId));
```

Question

**Who executes**

```java
playerService.getPlayer(playerId)
```

Certainly not

```
Request Thread
```

So who executes it?

---

# Default Behavior

When we don't specify an executor,

```java
CompletableFuture.supplyAsync(...)
```

internally behaves approximately like

```java
CompletableFuture.supplyAsync(
        supplier,
        ForkJoinPool.commonPool());
```

Java automatically uses

```
ForkJoinPool.commonPool()
```

---

# Execution Flow

```
HTTP Request

↓

Request Thread

↓

Create CompletableFuture

↓

ForkJoinPool.commonPool()

↓

Worker Thread

↓

Execute playerService.getPlayer()

↓

Complete Future
```

Notice

The request thread **does not execute the task**.

It submits the task to the thread pool.

---

# What is ForkJoinPool?

Think of it as

```
A pool of reusable worker threads.
```

Instead of creating a brand-new thread for every task,

Java maintains a pool of worker threads.

```
Worker 1

Worker 2

Worker 3

Worker 4

Worker 5
```

Whenever a task arrives,

one available worker thread executes it.

---

# Why Do We Need Thread Pools?

Creating threads is expensive.

Imagine

```
1000 Requests

↓

Create 1000 Threads
```

Problems

- High memory usage
- Context switching
- Thread creation overhead
- Poor performance

Instead

```
1000 Requests

↓

20 Worker Threads

↓

Reuse Existing Threads
```

Thread pools reuse existing threads.

Much faster.

---

# Why Doesn't Java Create a New Thread Every Time?

Creating a thread involves

- allocating memory
- creating a stack
- registering with the operating system
- scheduling

This is expensive.

Thread pools avoid this cost by reusing existing threads.

---

# Why is ForkJoinPool Not Ideal for Production?

The default pool is

```
ForkJoinPool.commonPool()
```

This pool is

```
Shared by the entire JVM.
```

Imagine

```
Player API

Odds API

Notification Service

Email Service

Image Processing
```

Everything uses

```
ForkJoinPool.commonPool()
```

Suppose

```
Weather API

↓

Network Timeout

↓

Worker Thread Busy
```

Then

```
Odds API

↓

Worker Thread Busy
```

Eventually

all worker threads become occupied.

Now

```
Player API

↓

Waiting for Free Worker
```

This is called

```
Thread Starvation
```

Unrelated tasks become slow because they all share the same thread pool.

---

# Apple Interview Question

**Question**

Why don't you use the default ForkJoinPool?

**Answer**

> The default `ForkJoinPool.commonPool()` is shared across the JVM. If blocking I/O operations consume its worker threads, unrelated tasks may be delayed, leading to thread starvation. In production applications, I prefer dedicated executors for different workloads so one slow subsystem doesn't affect others.

---

# Production Architecture

Instead of one shared pool

```
ForkJoinPool
```

Create dedicated thread pools.

```
Player Executor

↓

10 Threads
```

```
Odds Executor

↓

20 Threads
```

```
Notification Executor

↓

5 Threads
```

Now

```
Player Service
```

cannot block

```
Notification Service
```

Each workload has its own resources.

---

# ExecutorService

Instead of relying on the common pool,

we create our own.

```java
ExecutorService executor =
        Executors.newFixedThreadPool(10);
```

Now

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(

                () -> playerService.getPlayer(playerId),

                executor
        );
```

Notice

Instead of

```
ForkJoinPool.commonPool()
```

we explicitly specify

```
executor
```

---

# Execution Flow

```
HTTP Request

↓

Request Thread

↓

ExecutorService

↓

Worker Thread

↓

playerService.getPlayer()

↓

Complete Future
```

---

# Spring Boot Approach

Spring Boot usually doesn't use

```java
Executors.newFixedThreadPool(...)
```

Instead

it uses

```
ThreadPoolTaskExecutor
```

Spring manages

- thread creation
- lifecycle
- shutdown
- monitoring

---

# ThreadPoolTaskExecutor Example

```java
@Configuration
public class ExecutorConfig {

    @Bean
    public ThreadPoolTaskExecutor playerExecutor() {

        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10);

        executor.setMaxPoolSize(20);

        executor.setQueueCapacity(100);

        executor.setThreadNamePrefix("player-");

        executor.initialize();

        return executor;
    }

}
```

Now use it.

```java
@Autowired
private ThreadPoolTaskExecutor playerExecutor;
```

```java
CompletableFuture<Player> playerFuture =
        CompletableFuture.supplyAsync(

                () -> playerService.getPlayer(playerId),

                playerExecutor
        );
```

Now the Player workload uses its own dedicated thread pool.

---

# Why Spring Prefers ThreadPoolTaskExecutor

Spring Boot can

- manage the executor as a bean
- inject it anywhere
- configure it externally
- monitor it
- gracefully shut it down

This integrates much better with Spring applications than manually creating thread pools.

---

# Comparison

| Default | Production |
|----------|------------|
| ForkJoinPool.commonPool() | Dedicated ThreadPoolTaskExecutor |
| Shared by JVM | Dedicated to one workload |
| Limited control | Full control |
| Good for demos | Recommended for production |

---

# Summary

## Default

```
Request

↓

CompletableFuture

↓

ForkJoinPool.commonPool()

↓

Worker Thread
```

---

## Production

```
Request

↓

CompletableFuture

↓

Dedicated Executor

↓

Worker Thread
```

---

# Key Takeaways

### ForkJoinPool

- Default executor
- Shared across JVM
- Automatically used by `supplyAsync()`
- Good for simple applications
- Not ideal for blocking I/O workloads

---

### ExecutorService

- Custom thread pool
- More control
- Can isolate workloads
- Better for production

---

### ThreadPoolTaskExecutor

- Spring Boot's preferred executor
- Managed as a Spring bean
- Easy to configure
- Integrates with the Spring lifecycle

---

# Apple Interview Answer

**Question**

Where do the worker threads used by `CompletableFuture` come from?

**Answer**

> By default, `CompletableFuture.supplyAsync()` submits the task to Java's `ForkJoinPool.commonPool()`, which provides a shared pool of reusable worker threads. In production Spring Boot applications, I generally avoid relying on the common pool for blocking I/O. Instead, I configure dedicated `ThreadPoolTaskExecutor` beans for different workloads, such as player data, odds, or notifications. This isolates resources, prevents thread starvation, and gives better control over concurrency and performance.

---

# Next Topic

The next step is understanding how to size a thread pool.

We'll cover:

- Core Pool Size
- Maximum Pool Size
- Queue Capacity
- What happens when 1000 requests arrive
- RejectedExecutionHandler
- CPU-bound vs I/O-bound thread pools

These are very common senior backend interview topics.

# ThreadPoolTaskExecutor - Task Execution Order (Core Pool, Queue, Max Pool)

One of the most confusing parts of `ThreadPoolTaskExecutor` is understanding **what happens when many requests arrive at the same time**.

A common misconception is:

> "If the queue fills up and new threads are created, won't newer requests execute before older queued requests?"

Let's understand exactly what happens.

---

# Example Configuration

```java
@Bean
public ThreadPoolTaskExecutor playerExecutor() {

    ThreadPoolTaskExecutor executor =
            new ThreadPoolTaskExecutor();

    executor.setCorePoolSize(2);

    executor.setMaxPoolSize(4);

    executor.setQueueCapacity(3);

    executor.initialize();

    return executor;
}
```

Configuration

```
Core Threads = 2

Maximum Threads = 4

Queue Capacity = 3
```

---

# Incoming Requests

Suppose eight requests arrive almost simultaneously.

```
R1

R2

R3

R4

R5

R6

R7

R8
```

---

# Step 1 - Fill Core Threads

The executor first creates the core threads.

```
Thread 1

↓

R1
```

```
Thread 2

↓

R2
```

Current state

```
Threads

T1 → R1

T2 → R2
```

Core pool is now full.

---

# Step 2 - Fill the Queue

The next requests are NOT assigned new threads.

They are placed into the queue.

```
Queue

↓

R3

R4

R5
```

Current state

```
Threads

T1 → R1

T2 → R2

------------------

Queue

R3

R4

R5
```

Queue is now full.

---

# Step 3 - Create Additional Threads

Now

```
R6
```

arrives.

Since

- Core threads are busy
- Queue is full

the executor creates another thread.

```
Thread 3

↓

R6
```

Next

```
R7
```

arrives.

```
Thread 4

↓

R7
```

Current state

```
Threads

T1 → R1

T2 → R2

T3 → R6

T4 → R7

------------------

Queue

R3

R4

R5
```

Maximum pool size has now been reached.

---

# Question

At first glance it looks like

```
R6

R7
```

started executing before

```
R3

R4

R5
```

Isn't that unfair?

---

# What Actually Happens?

Suppose

```
Thread 1
```

finishes processing

```
R1
```

It immediately checks the queue.

The oldest queued request is

```
R3
```

So

```
Thread 1

↓

R3
```

---

Next

```
Thread 2
```

finishes

```
R2
```

It takes

```
R4
```

from the queue.

```
Thread 2

↓

R4
```

---

Next

```
Thread 3
```

finishes

```
R6
```

The queue still contains

```
R5
```

So

```
Thread 3

↓

R5
```

---

# Important Observation

The queued requests

```
R3

R4

R5
```

are processed in

```
FIFO

(First In, First Out)
```

They are **not skipped** or **reordered**.

---

# Then Why Create More Threads?

Imagine Java never created additional threads.

State

```
Core Threads

↓

Busy

Queue

↓

Full
```

Now

```
R6
```

arrives.

Where should it go?

There is

- No available thread
- No queue space

The only remaining choices are

- Reject the request
- Create another worker thread

Java chooses to create another worker thread (up to `maxPoolSize`) to increase throughput.

---

# The Goal

The goal is NOT

```
Allow newer requests to jump ahead.
```

The goal is

```
Increase processing capacity

↓

Handle traffic spikes

↓

Avoid rejecting requests immediately.
```

---

# Complete Timeline

Time T0

```
Threads

T1 → R1

T2 → R2

Queue

R3

R4

R5
```

Queue becomes full.

---

Time T1

```
R6 arrives

↓

Create Thread 3

↓

T3 → R6
```

---

Time T2

```
R7 arrives

↓

Create Thread 4

↓

T4 → R7
```

---

Time T3

```
T1 finishes

↓

Processes R3
```

---

Time T4

```
T2 finishes

↓

Processes R4
```

---

Time T5

```
T3 finishes

↓

Processes R5
```

Notice

The queued requests are still processed in FIFO order.

---

# Why Doesn't Java Create More Threads First?

The executor follows this order.

```
Incoming Task

↓

Core Threads

↓

Queue

↓

Additional Threads

↓

Reject
```

NOT

```
Core Threads

↓

Additional Threads

↓

Queue
```

Why?

Because

```
Creating threads

↓

Expensive
```

A queue is cheaper.

Java assumes that many traffic spikes are temporary.

Instead of creating lots of new threads immediately,

it first buffers requests in the queue.

Only when the queue becomes full does it create additional worker threads.

---

# Benefits

Using a queue first

- Reduces thread creation
- Improves resource utilization
- Handles temporary traffic bursts efficiently

Creating extra threads only when needed

- Improves throughput during sustained load
- Delays request rejection
- Prevents unnecessary thread creation

---

# Complete Execution Flow

```
Incoming Requests

        │

        ▼

Core Threads

        │

        ▼

Queue

        │

        ▼

Additional Threads
(up to maxPoolSize)

        │

        ▼

RejectedExecutionHandler
```

This is the execution order used by Java's `ThreadPoolExecutor`, which underlies Spring's `ThreadPoolTaskExecutor`.

---

# Apple Interview Question

**Question**

Why does the executor place requests into the queue before creating additional threads?

**Answer**

> Creating threads is relatively expensive. The executor assumes that short bursts of traffic can be absorbed by the queue without increasing the number of threads. Only when the queue becomes full does it create additional worker threads, up to `maxPoolSize`, to increase throughput. This strategy balances resource usage with responsiveness while avoiding unnecessary thread creation.

---

# Key Takeaways

- Core threads are created first.
- After the core pool is full, incoming tasks are placed in the queue.
- Only when the queue is full are additional threads created (up to `maxPoolSize`).
- Queued tasks remain in FIFO order.
- Newly created threads increase overall throughput; they do not reorder or remove tasks already waiting in the queue.
- Once both the queue and maximum thread count are exhausted, the `RejectedExecutionHandler` determines how new tasks are handled.

# RejectedExecutionHandler (High Level)

When

- Core threads are busy
- Queue is full
- Maximum threads are busy

the executor cannot accept more tasks.

It uses a **RejectedExecutionHandler** to decide what to do.

---

| Policy | What it Does | Typical Use |
|---------|--------------|-------------|
| **AbortPolicy** (Default) | Rejects the task and throws `RejectedExecutionException`. | Critical systems where losing work is unacceptable. |
| **CallerRunsPolicy** | The calling thread executes the task instead of a worker thread. | Applies backpressure by slowing incoming requests. |
| **DiscardPolicy** | Silently drops the new task. | Non-critical work such as logging or metrics. |
| **DiscardOldestPolicy** | Removes the oldest queued task and accepts the new one. | Scenarios where the latest data is more valuable than older data. |

---

# Interview Takeaway

For most backend interviews, it's enough to know:

- **AbortPolicy** → Fail fast (default).
- **CallerRunsPolicy** → Slow down the caller (backpressure).
- **DiscardPolicy** → Drop the new task.
- **DiscardOldestPolicy** → Drop the oldest queued task and accept the new one.


# CPU-bound vs I/O-bound Workloads

Choosing the right thread pool size depends on the type of workload.

---

## CPU-bound

The CPU spends most of its time performing computations.

### Examples

- Image processing
- Video encoding
- Encryption
- Compression
- Mathematical calculations

### Characteristics

- CPU is the bottleneck.
- Threads are actively using the CPU.
- Too many threads increase context switching and reduce performance.

### Thread Pool Guideline

```
Number of Threads ≈ Number of CPU Cores
```

---

## I/O-bound

The thread spends most of its time waiting for external resources.

### Examples

- Database queries
- REST API calls
- gRPC calls
- Redis
- Kafka
- File reads/writes

### Characteristics

- CPU is mostly idle while waiting.
- Network or disk is the bottleneck.
- More threads can improve throughput because many threads are waiting rather than computing.

### Thread Pool Guideline

```
Use a larger thread pool than CPU cores.

Exact size depends on:
- Database capacity
- API latency
- System load
- Load testing
```

---

# Quick Comparison

| CPU-bound | I/O-bound |
|-----------|-----------|
| CPU performs computation | Thread waits for external systems |
| CPU is the bottleneck | Network/Disk/Database is the bottleneck |
| Threads ≈ CPU cores | Larger thread pool is usually beneficial |
| Examples: Encryption, Compression | Examples: Database, REST, Redis, Kafka |

---

# Sports System Example

Most operations in our sports system are **I/O-bound**, such as:

- Calling Player Service
- Calling Odds Service
- Reading from Redis
- Reading/Writing Cassandra
- Publishing to Kafka
- Calling external provider APIs

Therefore, a **larger thread pool** is generally appropriate because worker threads spend much of their time waiting for external systems to respond.

---

# Apple Interview Answer

> CPU-bound tasks spend most of their time performing computations, so I size the thread pool close to the number of CPU cores. I/O-bound tasks spend most of their time waiting for databases, caches, or network calls, so I typically use a larger thread pool to keep the CPU utilized while other threads wait on I/O.

# CompletableFuture vs Reactive - What Happens to Request Threads?

One of the biggest advantages of `CompletableFuture` is that it **frees the Tomcat request thread**.

However, it is important to understand **what is actually freed** and **what is still blocked**.

---

# Traditional Spring MVC

Suppose Tomcat is configured with

```properties
server.tomcat.threads.max=200
```

Now imagine

```
10,000 HTTP Requests
```

arrive.

Tomcat does **NOT** create 10,000 threads.

Instead

```
10,000 Requests

↓

Tomcat

↓

200 Request Threads

↓

Only 200 requests execute immediately

↓

Remaining requests wait in the server's connection queue
```

The maximum number of simultaneously executing requests is limited by the Tomcat thread pool.

---

# Traditional Spring MVC Flow

```java
@GetMapping("/match")
public MatchResponse getMatch() {

    Player player = playerService.getPlayer(id);

    return new MatchResponse(player);
}
```

Timeline

```
Tomcat Thread

↓

Controller

↓

Service

↓

Database/API Call

↓

WAIT

↓

Response

↓

Tomcat Thread Released
```

Notice

The Tomcat request thread is blocked while waiting for the database or REST API.

---

# CompletableFuture (Incorrect Usage)

Suppose we write

```java
@GetMapping("/match")
public MatchResponse getMatch() {

    CompletableFuture<Player> future =
            CompletableFuture.supplyAsync(
                    () -> playerService.getPlayer(id));

    Player player = future.join();

    return new MatchResponse(player);
}
```

Timeline

```
Tomcat Thread

↓

Create Future

↓

Worker Thread

↓

Database/API Call

↓

WAIT

↓

Tomcat Thread blocks on join()

↓

Response
```

Although the work runs on another thread,

the request thread still waits at

```java
future.join()
```

This provides little benefit.

---

# CompletableFuture (Proper Async Usage)

Spring MVC allows a controller to return a `CompletableFuture`.

```java
@GetMapping("/match")
public CompletableFuture<MatchResponse> getMatch() {

    return CompletableFuture

            .supplyAsync(() ->
                    playerService.getPlayer(id))

            .thenApply(player ->
                    new MatchResponse(player));
}
```

Timeline

```
Tomcat Thread

↓

Create CompletableFuture

↓

Submit Work

↓

Worker Thread

↓

Database/API Call

↓

WAIT

↓

Tomcat Thread Released

↓

Worker Thread Completes

↓

Spring Sends Response
```

Notice

The Tomcat thread is released almost immediately.

It can now process another HTTP request.

---

# What Happens to the Worker Thread?

The worker thread is still waiting.

```
Worker Thread

↓

Database/API Call

↓

WAIT

↓

Response
```

So blocking has **not disappeared**.

It has simply moved from

```
Tomcat Thread

↓

to

↓

Worker Thread
```

---

# Why Is This Better?

Suppose

```
Tomcat Threads = 200

Worker Threads = 100
```

Request 1

```
Tomcat Thread #1

↓

Submit Async Task

↓

Worker Thread #12

↓

Tomcat Thread Released
```

Now

```
Request 2
```

can immediately reuse

```
Tomcat Thread #1
```

even though

```
Worker Thread #12
```

is still waiting for the database.

This improves the throughput of the web server because request threads become available much sooner.

---

# But There Is Still a Limit

Suppose all worker threads are busy.

```
100 Worker Threads

↓

Waiting on Database/API
```

The next asynchronous task must wait until a worker thread becomes available.

The bottleneck has moved from

```
Tomcat Thread Pool
```

to

```
Worker Thread Pool
```

---

# Reactive Programming

Reactive takes the next step.

Instead of blocking either thread,

it performs non-blocking I/O.

Timeline

```
Request Thread

↓

Start Database/API Call

↓

Register Callback

↓

Release Thread Immediately

↓

Response Arrives

↓

Resume Processing

↓

Send Response
```

Notice

No worker thread sits idle waiting for the database or REST API.

---

# Comparison

| Traditional Spring MVC | CompletableFuture | Reactive |
|------------------------|-------------------|----------|
| Tomcat thread blocks | Worker thread blocks | No thread blocks during I/O |
| Request thread waits for DB/API | Request thread is released early | Request thread is released immediately |
| Limited by Tomcat thread pool | Limited by worker thread pool | Can support many more concurrent I/O operations |

---

# Key Takeaways

### Traditional Spring MVC

- One request thread handles the request.
- The request thread waits for every database or REST call.

---

### CompletableFuture

- Frees the Tomcat request thread.
- Uses a worker thread for the asynchronous work.
- The worker thread still blocks while waiting for I/O.

---

### Reactive

- Uses non-blocking I/O.
- Neither the request thread nor a worker thread waits for external I/O.
- Enables much higher concurrency with fewer threads.

---

# Apple Interview Answer

**Question**

How does `CompletableFuture` improve scalability compared to traditional Spring MVC?

**Answer**

> In traditional Spring MVC, the request thread blocks while waiting for databases or downstream services. With `CompletableFuture`, the request thread submits the work to a worker thread and is released back to Tomcat, allowing it to handle additional incoming requests. The worker thread still blocks while waiting for I/O, so the bottleneck shifts from the request thread pool to the worker thread pool. Reactive programming goes one step further by using non-blocking I/O, so no thread remains blocked while waiting for external operations.


# Reactive Programming - Understanding the Motivation

This section explains **why Reactive Programming exists** and how it differs from traditional Spring MVC and `CompletableFuture`.

---

# Traditional Spring MVC

Suppose Tomcat is configured as

```properties
server.tomcat.threads.max=200
```

Now imagine

```
10,000 HTTP Requests
```

arrive.

Tomcat **does not** create 10,000 threads.

Instead

```
10,000 Requests

↓

Tomcat

↓

200 Request Threads

↓

Remaining requests wait in the server's connection queue
```

Only 200 requests can be processed simultaneously.

---

# Traditional Spring MVC Flow

```java
@GetMapping("/player/{id}")
public Player getPlayer(Long id) {

    return playerService.getPlayer(id);
}
```

Timeline

```
Tomcat Thread

↓

Controller

↓

Service

↓

Database Call

↓

WAIT

↓

Response

↓

Thread Released
```

The Tomcat thread is blocked while waiting for the database.

---

# CompletableFuture

Instead of letting the Tomcat thread wait,

we move the work to a worker thread.

```java
@GetMapping("/player/{id}")
public CompletableFuture<Player> getPlayer(Long id) {

    return CompletableFuture.supplyAsync(
            () -> playerService.getPlayer(id));
}
```

Timeline

```
Tomcat Thread

↓

Submit Task

↓

Worker Thread

↓

Database Call

↓

WAIT

↓

Response

↓

Tomcat Thread Already Released
```

Notice

The Tomcat thread is released quickly and can process another incoming request.

---

# Does CompletableFuture Eliminate Waiting?

No.

It simply moves the waiting.

Instead of

```
Tomcat Thread

↓

WAIT
```

we now have

```
Worker Thread

↓

WAIT
```

Blocking still exists.

It has simply moved to another thread.

---

# Proper CompletableFuture Flow

Suppose

```
Tomcat Threads = 200

Worker Threads = 100
```

Request 1

```
Tomcat Thread #1

↓

Submit Async Task

↓

Worker Thread #12

↓

Tomcat Thread Released
```

Now

```
Request 2
```

can immediately reuse

```
Tomcat Thread #1
```

even though

```
Worker Thread #12
```

is still waiting for the database.

This increases throughput.

---

# But There Is Still a Limit

Eventually

```
100 Worker Threads

↓

WAITING on Database/API
```

Now new asynchronous work must wait for an available worker thread.

The bottleneck has moved from

```
Tomcat Thread Pool
```

to

```
Worker Thread Pool
```

---

# Reactive Programming

Reactive asks

> "Why should any Java thread wait?"

Instead

```
Request Thread

↓

Send Database/API Request

↓

Register Callback

↓

Release Thread Immediately
```

Now

```
Database

↓

Processes Request
```

No Java thread is waiting.

---

# What Happens When the Database Responds?

Question

If no thread was waiting,

who processes the response?

Answer

An available **Event Loop Thread**.

Timeline

```
Database

↓

Network Socket

↓

Response Arrives

↓

Event Loop Thread Picks It Up

↓

Continue Processing

↓

Send HTTP Response
```

Notice

The thread that resumes processing does **not** have to be the same thread that started the request.

---

# Traditional vs CompletableFuture vs Reactive

## Spring MVC

```
Tomcat Thread

↓

Database

↓

WAIT

↓

Response
```

---

## CompletableFuture

```
Tomcat Thread

↓

Submit Work

↓

Worker Thread

↓

WAIT

↓

Response
```

---

## Reactive

```
Event Loop Thread

↓

Start Database Call

↓

Release Thread

-------------------------

Database Responds

↓

Available Event Loop Thread

↓

Continue Processing

↓

Response
```

No Java thread waits while the database is processing the request.

---

# Restaurant Analogy

## Traditional

```
Waiter

↓

Stands Outside Kitchen

↓

Waits

↓

Food Ready
```

---

## CompletableFuture

```
You

↓

Friend Waits

↓

Friend Brings Food
```

You are free,

but someone is still waiting.

---

## Reactive

```
Order Food

↓

Kitchen Works

↓

Bell Rings

↓

Available Waiter Delivers Food
```

Nobody stands waiting outside the kitchen.

---

# Event Loop Threads vs Tomcat Threads

This depends on the server.

---

## Spring MVC

Typically uses

```
Tomcat
```

which provides

```
Tomcat Request Threads
```

Example

```
Tomcat

↓

200 Request Threads
```

Each active request occupies one request thread.

---

## Spring WebFlux

Typically uses

```
Netty
```

which provides

```
Event Loop Threads
```

Example

```
Netty

↓

8-16 Event Loop Threads
```

These are **not** Tomcat request threads.

Instead of assigning one thread per request,

event-loop threads process work only when there is something to do.

---

# Thread Ownership

## CompletableFuture

Worker thread

```
Worker Thread

↓

Call Database

↓

WAIT

↓

Continue Processing
```

The worker thread is occupied while waiting.

---

## Reactive

Event-loop thread

```
Event Loop Thread

↓

Send Request

↓

Release Thread

--------------------

Database Responds

↓

Available Event Loop Thread

↓

Continue Processing
```

The thread is borrowed only when useful work exists.

---

# Where Is the Execution State Stored?

A common question is

> If Thread A starts the request and Thread B finishes it, where is the state stored?

The answer is

**The state is stored in heap objects, not in the thread.**

Threads execute code.

Objects store state.

---

# Traditional Java

```java
Player player =
        repository.findById(id);
```

Stack

```
playerRef  --------->

id = 10
```

Heap

```
Player Object
```

Local variables live on the thread stack.

Objects live on the heap.

---

# CompletableFuture

```java
CompletableFuture<Player> future =
        CompletableFuture.supplyAsync(...);
```

Stack

```
futureRef --------->
```

Heap

```
CompletableFuture

↓

Result

↓

Callbacks
```

While the worker thread executes,

it has a normal stack frame.

When the method completes,

the result is stored inside the `CompletableFuture` object.

The worker thread becomes free.

---

# Reactive

```java
Mono<Player> mono =
        playerService.getPlayer(id);
```

Stack

```
monoRef ---------->
```

Heap

```
Mono Pipeline

↓

Operators

↓

Callbacks

↓

State
```

When the request is waiting for the database,

there is **no waiting thread**.

The information needed to continue processing is stored in the reactive pipeline on the heap.

Later,

an available event-loop thread reads that state and continues processing.

---

# Stack vs Heap

General JVM memory model

```
                 JVM Memory

        +-------------------------+
        |         Heap            |
        |-------------------------|
        | Player Object           |
        | Team Object             |
        | CompletableFuture       |
        | Mono                    |
        | Repository              |
        | String                  |
        +-------------------------+

                 ▲
                 │
          References point here
                 │

        +-------------------------+
        | Thread Stack            |
        |-------------------------|
        | playerRef ------------->|
        | futureRef ------------->|
        | monoRef   ------------->|
        | id = 10                 |
        +-------------------------+
```

---

# Important Note

While a thread is actively executing,

it still has a normal Java stack.

For example,

during

```java
playerService.getPlayer(id)
```

the executing thread contains

```
Stack Frame

id

playerRef

repositoryRef
```

The difference is what happens during I/O.

### CompletableFuture

```
Worker Thread

↓

Keeps the stack

↓

WAIT

↓

Continue
```

### Reactive

```
Event Loop Thread

↓

Send Request

↓

Stack Disappears

↓

Thread Released

---------------------

Later

↓

Another Event Loop Thread

↓

New Stack

↓

Read Pipeline State

↓

Continue Processing
```

The execution state survives because it is stored in heap objects, not in the thread stack.

---

# Interview Takeaways

### Traditional Spring MVC

- Tomcat request thread blocks during I/O.

### CompletableFuture

- Frees the Tomcat request thread.
- Uses a worker thread.
- Worker thread still blocks during I/O.

### Reactive

- Uses non-blocking I/O.
- No Java thread waits during I/O.
- Execution state is stored in heap objects.
- Any available event-loop thread can resume processing when the response arrives.

---

# Apple Interview Answer

**Question**

Why is Reactive Programming more scalable than CompletableFuture?

**Answer**

> CompletableFuture improves scalability by freeing the Tomcat request thread, but a worker thread still blocks while waiting for I/O. Reactive programming uses non-blocking I/O, so neither the request thread nor a worker thread waits during external operations. The execution state is stored in heap objects, allowing any available event-loop thread to resume processing when data arrives, which enables much higher concurrency with fewer threads.


# Reactive Programming - Introduction to Mono

Now that we understand **why Reactive Programming exists**, let's learn its first building block: **Mono**.

---

# What is Mono?

`Mono<T>` represents

> **0 or 1 asynchronous result.**

Think of it as the Reactive equivalent of `CompletableFuture<T>`.

---

# Comparison

| Traditional | CompletableFuture | Reactive |
|-------------|-------------------|-----------|
| `Player` | `CompletableFuture<Player>` | `Mono<Player>` |

---

## Traditional

```java
Player player =
        playerService.getPlayer(playerId);
```

Returns

```
Player
```

---

## CompletableFuture

```java
CompletableFuture<Player> playerFuture =
        playerService.getPlayerAsync(playerId);
```

Returns

```
CompletableFuture<Player>
```

Meaning

> "The Player will be available later."

---

## Reactive

```java
Mono<Player> playerMono =
        playerService.getPlayer(playerId);
```

Returns

```
Mono<Player>
```

Meaning

> "The Player will be available later using non-blocking I/O."

---

# Where Does Mono Come From?

Suppose Player Service is another microservice.

Traditional (Blocking)

```java
Player player =
        restTemplate.getForObject(
                "/players/10",
                Player.class);
```

Timeline

```
REST Call

↓

WAIT

↓

Player
```

The thread waits.

---

Reactive (Non-Blocking)

```java
Mono<Player> playerMono =

        webClient
                .get()
                .uri("/players/10")
                .retrieve()
                .bodyToMono(Player.class);
```

Timeline

```
Send HTTP Request

↓

Return Mono<Player>

↓

Thread Released

↓

Response Arrives Later

↓

Mono Completes
```

Notice

No Java thread waits for the HTTP response.

---

# RestTemplate vs WebClient

| RestTemplate | WebClient |
|--------------|-----------|
| Blocking | Non-blocking |
| Waits for HTTP response | Returns immediately |
| Returns `Player` | Returns `Mono<Player>` |

---

# Mono as a Container

Think of `Mono<Player>` as a container.

Initially

```
┌──────────────┐
│ Mono<Player> │
└──────────────┘
```

Later

```
┌──────────────┐
│ Player       │
└──────────────┘
```

The framework fills the container when the response arrives.

---

# Transforming the Result

Suppose we want the player's name.

---

## CompletableFuture

```java
CompletableFuture<Player> playerFuture =
        playerService.getPlayerAsync(playerId);

CompletableFuture<String> nameFuture =
        playerFuture.thenApply(player ->
                player.getName());
```

Flow

```
CompletableFuture<Player>

↓

thenApply()

↓

CompletableFuture<String>
```

---

## Reactive

```java
Mono<Player> playerMono =
        playerService.getPlayer(playerId);

Mono<String> nameMono =
        playerMono.map(player ->
                player.getName());
```

Flow

```
Mono<Player>

↓

map()

↓

Mono<String>
```

Notice

`map()` is the Reactive equivalent of `thenApply()`.

Both transform the value inside the asynchronous container.

---

# How Do We Get the Value?

## CompletableFuture

Eventually we retrieve the value.

```java
String name = nameFuture.join();
```

Flow

```
CompletableFuture<String>

↓

join()

↓

String
```

---

## Reactive

Reactive also has a method.

```java
String name = nameMono.block();
```

Flow

```
Mono<String>

↓

block()

↓

String
```

However,

**in a reactive application we generally avoid calling `block()`** because it blocks the event-loop thread and defeats the purpose of non-blocking programming.

---

# The Preferred Reactive Approach

Instead of extracting the value,

return the `Mono`.

```java
@GetMapping("/name")
public Mono<String> getPlayerName() {

    return playerService.getPlayer(playerId)

            .map(player ->
                    player.getName());

}
```

Spring WebFlux

- subscribes to the `Mono`
- waits asynchronously
- writes the HTTP response automatically when the value arrives

The application code never calls `block()`.

---

# Key Mapping

| CompletableFuture | Reactive |
|-------------------|----------|
| `CompletableFuture<Player>` | `Mono<Player>` |
| `thenApply()` | `map()` |
| `join()` | `block()` *(available but generally avoided in WebFlux)* |

---

# Interview Takeaways

- `Mono<T>` represents **zero or one asynchronous value**.
- `Mono<Player>` is conceptually similar to `CompletableFuture<Player>`.
- `map()` is equivalent to `thenApply()`.
- `block()` is equivalent to `join()`, but should generally be avoided in reactive server applications.
- In Spring WebFlux, you typically return the `Mono` directly and let the framework handle subscription and sending the response.

---

# Apple Interview Answer

**Question**

What is a `Mono`?

**Answer**

> `Mono<T>` is the basic reactive type that represents zero or one asynchronous value. It is conceptually similar to `CompletableFuture<T>`, but instead of relying on a worker thread that blocks while waiting for I/O, it uses non-blocking I/O. In Spring WebFlux, controllers typically return `Mono<T>` directly, allowing the framework to send the HTTP response asynchronously when the value becomes available.


# Reactive Programming - `flatMap()`

Now that we understand `Mono` and `map()`, let's learn `flatMap()`.

If you understand `CompletableFuture.thenCompose()`, then `flatMap()` is almost identical.

---

# Problem Statement

Suppose we first fetch a Player.

The Player contains

```java
player.getTeamId();
```

Now we need to call another service to retrieve the Team.

Architecture

```
Player Service

↓

Player

↓

teamId

↓

Team Service

↓

Team
```

This is an asynchronous call that depends on the result of another asynchronous call.

---

# CompletableFuture Version

```java
CompletableFuture<Player> playerFuture =
        playerService.getPlayerAsync(playerId);

CompletableFuture<Team> teamFuture =

        playerFuture.thenCompose(player ->

                teamService.getTeamAsync(
                        player.getTeamId()));
```

Why `thenCompose()`?

Because

```java
teamService.getTeamAsync(...)
```

returns

```java
CompletableFuture<Team>
```

---

# Reactive Version

```java
Mono<Player> playerMono =
        playerService.getPlayer(playerId);

Mono<Team> teamMono =

        playerMono.flatMap(player ->

                teamService.getTeam(
                        player.getTeamId()));
```

Why `flatMap()`?

Because

```java
teamService.getTeam(...)
```

returns

```java
Mono<Team>
```

---

# Underlying Service Implementations

## PlayerService

```java
@Service
public class PlayerService {

    private final WebClient webClient;

    public PlayerService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Player> getPlayer(Long playerId) {

        return webClient

                .get()

                .uri("/players/" + playerId)

                .retrieve()

                .bodyToMono(Player.class);
    }

}
```

Notice

The service returns

```java
Mono<Player>
```

instead of

```java
Player
```

---

## TeamService

```java
@Service
public class TeamService {

    private final WebClient webClient;

    public TeamService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Team> getTeam(Long teamId) {

        return webClient

                .get()

                .uri("/teams/" + teamId)

                .retrieve()

                .bodyToMono(Team.class);
    }

}
```

Again,

the service returns

```java
Mono<Team>
```

---

# Complete Flow

```java
Mono<Player> playerMono =
        playerService.getPlayer(playerId);

Mono<Team> teamMono =

        playerMono.flatMap(player ->

                teamService.getTeam(
                        player.getTeamId()));
```

Execution Flow

```
Mono<Player>

↓

Player Arrives

↓

Extract teamId

↓

Call Team Service

↓

Mono<Team>

↓

flatMap()

↓

Mono<Team>
```

Notice

The second service call starts **only after** the Player has been received because it depends on `player.getTeamId()`.

---

# Why Not map()?

Suppose we wrote

```java
Mono<Mono<Team>> teamMono =

        playerMono.map(player ->

                teamService.getTeam(
                        player.getTeamId()));
```

What does

```java
teamService.getTeam(...)
```

return?

```
Mono<Team>
```

Therefore

```
map()

↓

Mono<Mono<Team>>
```

We now have a nested `Mono`.

This is usually not what we want.

---

# What Does flatMap() Do?

`flatMap()` automatically flattens

```
Mono<Mono<Team>>

↓

Mono<Team>
```

Exactly like

```
CompletableFuture<CompletableFuture<Team>>

↓

CompletableFuture<Team>
```

is flattened by

```java
thenCompose()
```

---

# Rule

## Use `map()`

When the lambda returns a **normal object**.

Example

```java
Mono<String> nameMono =

        playerMono.map(player ->
                player.getName());
```

Flow

```
Mono<Player>

↓

Player

↓

getName()

↓

String

↓

Mono<String>
```

---

## Use `flatMap()`

When the lambda returns another **Mono**.

Example

```java
Mono<Team> teamMono =

        playerMono.flatMap(player ->

                teamService.getTeam(
                        player.getTeamId()));
```

Flow

```
Mono<Player>

↓

Player

↓

Mono<Team>

↓

flatMap()

↓

Mono<Team>
```

---

# Sports System Example

Suppose our Match Service needs

1. Player
2. Team
3. Coach

Each call depends on the previous one.

```java
Mono<Coach> coachMono =

        playerService.getPlayer(playerId)

                .flatMap(player ->

                        teamService.getTeam(
                                player.getTeamId()))

                .flatMap(team ->

                        coachService.getCoach(
                                team.getCoachId()));
```

Execution

```
Player Service

↓

Player

↓

Team Service

↓

Team

↓

Coach Service

↓

Coach
```

Each service returns

```java
Mono<T>
```

Therefore

each stage uses

```java
flatMap()
```

---

# Comparison

| CompletableFuture | Reactive |
|-------------------|----------|
| `thenApply()` | `map()` |
| `thenCompose()` | `flatMap()` |
| `CompletableFuture<Player>` | `Mono<Player>` |
| `CompletableFuture<Team>` | `Mono<Team>` |

---

# Interview Takeaways

- Use **`map()`** when the lambda returns a normal object.
- Use **`flatMap()`** when the lambda returns another `Mono`.
- `flatMap()` prevents nested types such as `Mono<Mono<Team>>`.
- `flatMap()` is conceptually the same as `thenCompose()` in `CompletableFuture`.

---

# Apple Interview Answer

**Question**

When would you use `flatMap()` instead of `map()`?

**Answer**

> I use `map()` when I'm transforming the value already inside a `Mono`, such as extracting a player's name from a `Player`. I use `flatMap()` when the transformation performs another asynchronous operation that returns a `Mono`, such as calling a Team Service using the player's `teamId`. It's the Reactive equivalent of `thenCompose()` in `CompletableFuture` because it flattens nested reactive types.