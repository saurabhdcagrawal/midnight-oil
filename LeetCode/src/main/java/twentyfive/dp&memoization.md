# Top-Down Dynamic Programming (Memoization) - Interview Pattern

## Step 1: What is my recursive state?

The first question to ask is:

> **What uniquely identifies one recursive call?**

That becomes the dimension of the memoization table.

---

## Example 1: Word Break

```java
boolean wordBreak(int i)
```

Question being answered:

> Can I break the string starting from index `i`?

State:

```
i
```

Memo:

```java
Boolean[] dp = new Boolean[s.length()];
```

---

## Example 2: House Robber

```java
int rob(int i)
```

Question:

> What is the maximum money I can rob starting from house `i`?

State:

```
i
```

Memo:

```java
Integer[] dp = new Integer[nums.length];
```

---

## Example 3: Decode Ways

```java
int decode(int i)
```

Question:

> How many ways can I decode the string starting at index `i`?

State:

```
i
```

Memo:

```java
Integer[] dp = new Integer[s.length()];
```

---

## Example 4: Longest Common Subsequence

```java
int lcs(int i, int j)
```

Question:

> What is the LCS length of the strings starting at `i` and `j`?

State:

```
(i, j)
```

Memo:

```java
Integer[][] dp = new Integer[m][n];
```

---

## Example 5: Edit Distance

```java
int edit(int i, int j)
```

Question:

> Minimum operations to convert the suffixes starting at `i` and `j`?

State:

```
(i, j)
```

Memo:

```java
Integer[][] dp = new Integer[m][n];
```

---

## Example 6: Stock Buy & Sell

```java
int profit(int day, boolean canBuy)
```

Question:

> What is the maximum profit starting from this day and this buying state?

State:

```
(day, canBuy)
```

Memo:

```java
Integer[][] dp = new Integer[n][2];
```

---

# Golden Rule

> **Memo dimensions = Recursive parameters that change.**

Examples

```java
dfs(i)
```

↓

```java
dp[i]
```

---

```java
dfs(i, j)
```

↓

```java
dp[i][j]
```

---

```java
dfs(i, j, k)
```

↓

```java
dp[i][j][k]
```

---

# Step 2: Memoization Pattern

Every recursive DP solution should follow this structure.

```java
ReturnType dfs(State){

    // 1. Base Case
    if(baseCase)
        return baseAnswer;

    // 2. Already solved?
    if(dp[state] != null)
        return dp[state];

    // 3. Compute answer
    ReturnType answer;

    ...

    // 4. Save answer
    dp[state] = answer;

    // 5. Return answer
    return answer;
}
```

Think of it as:

```
Already solved?

↓

No

↓

Compute

↓

Save

↓

Return
```

---

# Step 3: When do I store in the memo table?

Always ask yourself:

> **Have I completely solved this recursive state?**

If the answer is yes:

```java
dp[state] = answer;
return answer;
```

Never memoize before the answer is known.

---

# Word Break Example

Recursive function:

```java
boolean wordBreak(int i)
```

Question being answered:

> Can I break the string starting from index `i`?

After exploring every possible word:

```java
dp[i] = true;
```

or

```java
dp[i] = false;
```

Then return it.

---

# Common Bug (The One We Discussed)

### Incorrect

```java
for(...) {

    if(dictionary.contains(...)){

        boolean result = wordBreak(nextIndex);

        dp[i] = result;

        if(result)
            return true;
    }
}

return false;
```

### Problem

If **no dictionary word matches**, then:

```
if block never executes

↓

dp[i] is never assigned

↓

dp[i] remains null
```

Next time another recursion reaches the same state, the computation happens all over again.

---

### Correct (Flag Version)

```java
boolean flag = false;

for(int j = i; j < s.length(); j++){

    if(dict.contains(s.substring(i, j + 1))){

        flag = wordBreak(j + 1);

        if(flag)
            break;
    }
}

dp[i] = flag;

return flag;
```

Now every state is memoized.

Whether:

- a solution exists
- no solution exists
- no dictionary word matches

the result is always stored.

---

# Why Memoization Works

Think of `dp` as your notebook.

Suppose you've already answered:

> Can I break the string starting at index 7?

If another recursion reaches:

```java
wordBreak(7)
```

Instead of solving it again:

```
Look inside notebook

↓

dp[7]

↓

Return immediately
```

---

# Interview Shortcut

Don't think about DP first.

Instead ask:

> **What question is my recursive function answering?**

Examples:

### Word Break

```java
wordBreak(i)
```

Question:

```
Can I break the string starting from i?
```

Memo:

```java
dp[i]
```

---

### House Robber

```java
rob(i)
```

Question:

```
Maximum money starting from house i?
```

Memo:

```java
dp[i]
```

---

### LCS

```java
lcs(i, j)
```

Question:

```
LCS length of suffixes starting at i and j?
```

Memo:

```java
dp[i][j]
```

---

### Edit Distance

```java
edit(i, j)
```

Question:

```
Minimum edits between suffixes starting at i and j?
```

Memo:

```java
dp[i][j]
```

---

# Final Mental Model

```
1. What question is my recursive function answering?

↓

2. What parameters uniquely identify that question?

↓

3. Those parameters become the memo table dimensions.

↓

4. Solve the state completely.

↓

5. Store the answer.

↓

6. Return the answer.
```

This pattern works for the vast majority of top-down Dynamic Programming interview problems.

# Word Break - Time Complexity Analysis

## Without Memoization (Pure Recursion)

Suppose:

```text
s = "aaaaaaaaab"

Dictionary:
a
aa
aaa
aaaa
```

At index `0`, we have multiple choices:

```text
0

├── a
├── aa
├── aaa
└── aaaa
```

Suppose we choose `"a"`.

Now we are at index `1`.

Again we have multiple choices:

```text
1

├── a
├── aa
├── aaa
└── aaaa
```

This continues recursively.

---

## Recursion Tree

```text
                    0
            /   /   |   \
           1    2   3    4
         / | \ ...
        2 3 4 5
       /...
```

Notice that the same recursive state appears multiple times.

For example:

```text
wordBreak(4)
```

can be reached through many different paths:

```text
0 -> 4

0 -> 1 -> 4

0 -> 2 -> 4

0 -> 1 -> 2 -> 4
```

Every one of these paths recomputes the entire subtree rooted at index `4`.

This repeated work causes an exponential explosion.

---

## Time Complexity

At every recursive level, we may branch into multiple recursive calls.

Conceptually:

```text
2 × 2 × 2 × ...

n levels
```

which results in

```text
O(2^n)
```

Although the exact branching factor depends on the dictionary, interviewers generally accept:

**Time Complexity:**

```text
O(2^n)
```

---

# With Memoization

Suppose we compute:

```java
wordBreak(4)
```

After solving it once, we store:

```java
dp[4] = false;
```

Later another recursion reaches:

```java
wordBreak(4)
```

Instead of recomputing everything:

```java
if(dp[4] != null)
    return dp[4];
```

The answer is returned immediately.

No recursive calls are made.

---

# How Many States Exist?

Our recursive function is:

```java
wordBreak(i)
```

Possible values of `i` are:

```text
0
1
2
3
...
n
```

Therefore there are only:

```text
n + 1
```

unique recursive states.

So,

```text
Number of states = O(n)
```

---

# Work Done Per State

For one state:

```java
wordBreak(i)
```

we execute:

```java
for(int j = i; j < n; j++)
```

In the worst case:

```text
j travels from i to n-1
```

which takes

```text
O(n)
```

time.

---

# Total Runtime

We now have:

- Number of states = **O(n)**
- Work per state = **O(n)**

Therefore,

```text
O(n)

×

O(n)

=

O(n²)
```

---

# Another Way to Derive It (Interview Friendly)

### Step 1

How many unique recursive calls exist?

```text
wordBreak(0)

wordBreak(1)

...

wordBreak(n)
```

Answer:

```text
O(n)
```

---

### Step 2

How much work is done in one call?

Each call executes:

```java
for(int j = i; j < n; j++)
```

Worst case:

```text
O(n)
```

---

### Step 3

Multiply the two

```text
States × Work per State

=

O(n)

×

O(n)

=

O(n²)
```

This is the preferred interview explanation because it works for almost every Top-Down DP problem.

---

# Visual Comparison

### Without Memoization

```text
wordBreak(4)

↓

Computed many times
```

---

### With Memoization

```text
wordBreak(4)

↓

Computed exactly once

↓

Stored in dp[4]

↓

Every future call returns immediately
```

---

# Complexity Summary

| Approach | Time Complexity | Reason |
|-----------|-----------------|--------|
| Pure Recursion | **O(2^n)** | Same recursive states are recomputed many times, leading to exponential branching. |
| Memoization (Top-Down DP) | **O(n²)** | Only `n` unique states exist, and each state performs an `O(n)` loop. |
| Space Complexity | **O(n)** | `dp[]` stores one result per index, and recursion depth is at most `n`. |

---

# Interview Cheat Sheet

Whenever analyzing a Top-Down DP solution:

### Step 1

Ask:

> **How many unique recursive states exist?**

This determines the number of DP entries.

---

### Step 2

Ask:

> **How much work does each state perform?**

Usually this is a loop or a few recursive decisions.

---

### Step 3

Multiply:

```text
Total Runtime

=

(Number of States)

×

(Work Per State)
```

This simple three-step approach works for many DP interview problems such as:

- Word Break
- House Robber
- Decode Ways
- Coin Change
- Longest Common Subsequence
- Edit Distance
- Partition Equal Subset Sum
- Target Sum
- Unique Paths