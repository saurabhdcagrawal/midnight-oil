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

# Word Break Solution: Complexity Analysis

The total time complexity of your solution is **$O(N^3 + M)$**, where **$N$** is the length of the string `s`, and **$M$** is the total number of characters across all words in `wordDict`.


## Detailed Breakdown

### 1. Initial Setup: $O(M)$
Before triggering the recursive method, your code converts the dictionary into a `HashSet`.
* Adding a word to a `HashSet` requires hashing its characters, taking time proportional to the word's length.
* Iterating through all words in `wordDict` and processing them takes **$O(M)$** time.

### 2. Number of Memoized States: $O(N)$
Your `dp` array tracking cache states has a maximum size of `s.length() + 1`.
* The state is entirely defined by the index pointer `i`.
* Because of the guard check `if(dp[i]!=null) return dp[i];`, the code block inside the function runs **at most once** for each unique index `i` from `0` to `N`.
* Any duplicate future call to an index `i` that has already been solved instantly short-circuits and runs in **$O(1)$** time.

### 3. Work Done Inside Each State: $O(N^2)$
For every unique index `i` that hasn't been cached yet, the code runs a `for` loop:
* **The Loop Steps**: The pointer `j` scans from `i` to `s.length()`. In the worst-case scenario, this loop runs up to **$N$** times.
* **The Substring Operation**: Inside the loop, `s.substring(i, j+1)` creates a brand-new string slice. Creating and allocating a substring in Java takes time proportional to the substring's length, which takes up to **$O(N)$** time.
* **The HashSet Lookup**: `hset.contains(...)` computes the hash code of the newly generated slice. Hashing a string requires iterating over its characters, which also takes up to **$O(N)$** time.

Combining the loop bounds and the inner string mechanics: 
$$\text{N iterations} \times O(N) \text{ string work} = O(N^2) \text{ total work per state}$$

### 4. Total Calculation
* **Recursion Engine**: $\text{Total Unique States } O(N) \times \text{Work Per State } O(N^2) = O(N^3)$
* **Combined with HashSet Setup**: **$O(N^3 + M)$**


# Word Break Solution: Space Complexity Analysis

Yes, you are **exactly right**. The total space complexity of your code is **$O(M + N)$**. 

---

## Detailed Breakdown

### 1. HashSet Storage: $O(M)$
* Your `hset` stores every unique word from `wordDict`.
* The memory usage scales directly with **$M$** (the total number of characters across all words in the dictionary).

### 2. Memoization Array: $O(N)$
* The `dp` array is initialized with a size of `s.length() + 1`.
* This requires **$O(N)$** space to store the cached `Boolean` results for each index.

### 3. Recursive Call Stack: $O(N)$
* In the worst-case scenario (e.g., `s = "aaaa"` and `wordDict = ["a"]`), the recursion moves forward one character at a time.
* This creates a maximum recursive call stack depth of **$N$**.

---

## Total Space Calculation

Combining all allocated memory:

$$\text{HashSet } O(M) + \text{Memo Array } O(N) + \text{Call Stack } O(N) = O(M + 2N)$$

In Big-O notation, we drop the constant multiplier ($2$), which simplifies perfectly to:

$$\mathbf{O(M + N)}$$



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



# House Robber (LeetCode 198)

## Variants

1. **Pure Recursion**
   - Time: `O(2^n)`
   - Space: `O(n)` (Recursion Stack)

2. **Recursion + Memoization (Top-Down DP)**
   - Time: `O(n)`
   - Space: `O(n)`

3. **Bottom-Up DP (DP Array)**
   - Time: `O(n)`
   - Space: `O(n)`

4. **Bottom-Up DP (Space Optimized)** ✅
   - Time: `O(n)`
   - Space: `O(1)`

---

# DP Recurrence

At every house, we have two choices:

- **Rob current house**
  - `nums[i] + dp[i+2]`
- **Skip current house**
  - `dp[i+1]`

So,

```text
dp[i] = max(nums[i] + dp[i+2], dp[i+1])
```

The answer is always `dp[0]`.

---

# Space Optimized DP

Instead of storing the entire DP array, we only keep the last two DP states.

During every iteration:

```text
robNextPlusOne = dp[i+2]

robNext = dp[i+1]

current = dp[i]
```

After computing `current`, we shift the variables:

```java
robNextPlusOne = robNext;
robNext = current;
```

Now they represent

```text
robNextPlusOne = dp[i+1]

robNext = dp[i]
```

The variables keep sliding left until we reach `dp[0]`.

---

# Why return `robNext` and not `current`?

At each iteration:

- `current` computes `dp[i]`.
- After computing it, we shift the variables so that `robNext` now represents `dp[i]`.

When the loop finishes (`i = 0`),

```text
robNext = dp[0]
```

which is the final answer.

`current` happens to contain the same value at the end, but it is only a temporary variable. `robNext` consistently represents the latest DP state, so returning it makes the intent clearer.

---

# Interview Flow

Most interviewers like seeing the progression:

```
Recursion
      ↓
Memoization (Top-Down DP)
      ↓
Bottom-Up DP
      ↓
Bottom-Up DP (Space Optimized)
```

---

# Code

```java
class Solution {

    /*"At each iteration, current computes dp[i]. After computing it, I shift my variables so that robNext now represents dp[i] for the next iteration. When the loop finishes at i = 0, robNext has become dp[0], which is the answer. current happens to have the same value at the end, but it's just a temporary variable, whereas robNext consistently represents the latest DP state."*/
    public int rob(int[] nums) {
    //improve further no need to store all
    //4 variants.. recursion, recursion with memoization, bottom up DP, bottom UP DP without memory
        int n= nums.length;
        //Integer[] dp= new Integer[n+1];
        int robNextPlusOne=0; //last value =0
        int robNext= nums[n-1]; //second last value is last element.. if we have one element we only get
        //one option
        int current=0;
        for(int i=n-2;i>=0;i--){
            current= Math.max(robNextPlusOne+nums[i],robNext);
            robNextPlusOne=robNext;
            robNext=current;
        }

        return robNext;
    }

    //now improve further


    /*public int rob(int[] nums) {
    //math.max
    //so DP is bottom up in this case...
    //many times problems become O(2^n) to O(n)
        int n= nums.length;
        Integer[] dp= new Integer[n+1];
        dp[n]=0; //last value =0
        dp[n-1]=nums[n-1]; //second last value is last element.. if we have one element we only get
        //one option
        for(int i=n-2;i>=0;i--){
            dp[i]= Math.max(dp[i+2]+nums[i],dp[i+1]);
        }

        return dp[0];
    }*/


    /* Recursion with memoization
    public int rob(int[] nums) {
     //math.max
        Integer[] dp= new Integer[nums.length+1];
        return rob(nums,0,dp);

    }

    public int rob(int [] nums, int i, Integer[] dp){
        if(i>=nums.length)
            return 0;
        if(dp[i]!=null)
            return dp[i];
        dp[i]= Math.max(nums[i]+rob(nums,i+2,dp),rob(nums,i+1,dp));
        return dp[i];
    }*/


    //Recursion pure
    /*public int rob(int[] nums) {
     //math.max

        return rob(nums,0);

    }

    public int rob(int [] nums, int i){
        if(i>=nums.length)
            return 0;

        return Math.max(nums[i]+rob(nums,i+2),rob(nums,i+1));

    }*/

}
```

## Key Interview Takeaways

- Start with **Pure Recursion** to derive the recurrence.
- Add **Memoization** to avoid recomputing overlapping subproblems.
- Convert it to **Bottom-Up DP** by filling from right to left.
- Observe that each state only depends on the next **two** states, allowing **O(1)** space optimization.


# House Robber II (LeetCode 213)

## Difference from House Robber I

In House Robber I, houses are arranged in a **straight line**.

```text
1 --- 2 --- 3 --- 4
```

In House Robber II, houses are arranged in a **circle**.

```text
      1
   /     \
  2       4
    \   /
      3
```

Since the **first and last houses are adjacent**, they **cannot both be robbed**.

---

## Key Observation

There are only **two possible cases**:

### Case 1: Rob from House[0] to House[n-2]

Exclude the last house.

```text
[2,3,2]

Take

[2,3]
```

---

### Case 2: Rob from House[1] to House[n-1]

Exclude the first house.

```text
[2,3,2]

Take

[3,2]
```

Now both cases become the original **House Robber I** problem.

Compute both and return the maximum.

```text
answer =
max(
    rob(0...n-2),
    rob(1...n-1)
)
```

---

## Edge Case

If there is only one house,

```java
if(nums.length == 1)
    return nums[0];
```

---

## Complexity

- Time: **O(n)**
- Space:
  - **O(n)** (copying arrays)
  - Can be optimized to **O(1)** extra space by passing start/end indices instead of creating new arrays.

---

## Code

```java
public int rob(int[] nums) {
    /*    1
        1    2
          3   */
    /*Since House[1] and House[n] are adjacent, they cannot be robbed together.
      Therefore, the problem becomes to rob either House[1]-House[n-1]
      or House[2]-House[n], depending on which choice offers more money.
      Now the problem has degenerated to the House Robber,
      which is already been solved.
    */

    if(nums.length==1)
        return nums[0];

    int [] nums1= new int[nums.length-1];
    int [] nums2= new int[nums.length-1];

    copyArrayElems(nums1,0,nums);
    copyArrayElems(nums2,1,nums);

    return Math.max(robHouses(nums1),robHouses(nums2));
}

//LCS
//edit distance
//coins..
public void copyArrayElems(int [] arr, int copyIndex,int[] nums){

    for(int i=0;i<arr.length;i++){
          arr[i]= nums[copyIndex++];
    }

}
```

## Interview Explanation

> "Because the first and last houses are adjacent, they cannot both be robbed. So there are only two valid possibilities: either exclude the last house or exclude the first house. Each case becomes the original House Robber problem. I solve both independently and return the maximum result."
```java
public int rob(int[] nums) {

    if(nums.length == 1)
        return nums[0];

    return Math.max(
        robHouses(nums, 0, nums.length - 2),
        robHouses(nums, 1, nums.length - 1)
    );
}
//Now we tell it which part of the array to process.
private int robHouses(int[] nums, int start, int end) {

    int robNextPlusOne = 0;
    int robNext = nums[end];
    int current = 0;

    for(int i = end - 1; i >= start; i--) {

        current = Math.max(
                nums[i] + robNextPlusOne,
                robNext
        );

        robNextPlusOne = robNext;
        robNext = current;
    }

    return robNext;
}

```

# Longest Common Subsequence (LeetCode 1143)

## Approach: Bottom-Up Dynamic Programming

### State Definition

`dp[i][j]` represents the length of the **Longest Common Subsequence (LCS)** between:

- First `i` characters of `text1`
- First `j` characters of `text2`

So,

```text
dp[i][j] = LCS(text1[0...i-1], text2[0...j-1])
```

We create a DP table of size `(m+1) x (n+1)`.

The extra row and column represent an **empty string**, whose LCS is always `0`.

---

## DP Transition

### Case 1: Characters Match

If

```java
text1.charAt(i-1) == text2.charAt(j-1)
```

then we include that character in the LCS.

```text
dp[i][j] = 1 + dp[i-1][j-1]
```

---

### Case 2: Characters Do Not Match

One of the current characters cannot be part of the optimal subsequence.

So we try:

- Skip character from `text1`
- Skip character from `text2`

```text
dp[i][j] =
max(dp[i-1][j],
    dp[i][j-1])
```

---

## Complexity

### Time Complexity

There are

```text
(m+1) × (n+1)
```

states.

Each state takes **O(1)** time.

Therefore,

```text
Time = O(m × n)
```

---

### Space Complexity

The DP table contains

```text
(m+1) × (n+1)
```

cells.

Therefore,

```text
Space = O(m × n)
```

---

## Example

```text
text1 = "abcde"

text2 = "ace"
```

### DP Table

|     | "" | a | c | e |
|-----|---:|---:|---:|---:|
| ""  | 0 | 0 | 0 | 0 |
| a   | 0 | 1 | 1 | 1 |
| b   | 0 | 1 | 1 | 1 |
| c   | 0 | 1 | 2 | 2 |
| d   | 0 | 1 | 2 | 2 |
| e   | 0 | 1 | 2 | 3 |

Final Answer:

```text
dp[5][3] = 3
```

LCS:

```text
ace
```

---

## Interview Explanation

> "`dp[i][j]` stores the length of the Longest Common Subsequence between the first `i` characters of `text1` and the first `j` characters of `text2`. If the current characters match, I extend the previous subsequence using `1 + dp[i-1][j-1]`. Otherwise, I try skipping one character from either string and take the maximum. After filling the table from top-left to bottom-right, the answer is stored in `dp[m][n]`."

---

## Code

```java
class Solution {
    public int longestCommonSubsequence(String text1, String text2) {
        int m= text1.length();
        int n= text2.length();

        // dp[i][j] means the length of the Longest Common Subsequence
        // between the first i characters of text1 and
        // the first j characters of text2.
        int[][] dp = new int[m+1][n+1];

        for(int i=1;i<=m;i++){
            for(int j=1;j<=n;j++){

                dp[i][j] =
                    text1.charAt(i-1)==text2.charAt(j-1)
                    ? 1 + dp[i-1][j-1]
                    : Math.max(dp[i-1][j], dp[i][j-1]);

            }
        }

        return dp[m][n];
    }
}
```

# Edit Distance (LeetCode 72)

## Approach: Bottom-Up Dynamic Programming

### State Definition

`dp[i][j]` represents the **minimum number of operations** required to convert:

- First `i` characters of `word1`
- Into the first `j` characters of `word2`

```text
dp[i][j] = Min operations to convert

           word1[0...i-1]

           into

           word2[0...j-1]
```

We build a DP table of size `(m+1) x (n+1)`.

---

# Base Cases

Unlike **Longest Common Subsequence**, the first row and first column are **not zero**.

### First Row

Convert an empty string to `"ros"`.

```text
"" → "ros"

Insert r
Insert o
Insert s

Cost = 3
```

Therefore,

```text
dp[0][j] = j
```

---

### First Column

Convert `"horse"` to an empty string.

```text
horse → ""

Delete h
Delete o
Delete r
Delete s
Delete e

Cost = 5
```

Therefore,

```text
dp[i][0] = i
```

This is why we initialize

```java
if(i==0 || j==0)
    dp[i][j] = i + j;
```

---

# DP Transition

## Case 1 : Characters Match

If

```java
word1.charAt(i-1) == word2.charAt(j-1)
```

No operation is required.

```text
dp[i][j] = dp[i-1][j-1]
```

Move diagonally.

---

## Case 2 : Characters Don't Match

We have **three choices**.

### Insert

Insert a character into `word1`.

```text
dp[i][j-1] + 1
```

Move left.

---

### Delete

Delete the current character from `word1`.

```text
dp[i-1][j] + 1
```

Move up.

---

### Replace

Replace the current character.

```text
dp[i-1][j-1] + 1
```

Move diagonally.

---

Take the minimum.

```text
dp[i][j] =
1 + min(
    dp[i-1][j],      // Delete
    dp[i][j-1],      // Insert
    dp[i-1][j-1]     // Replace
)
```

---

# DP Matrix Example

```text
word1 = "horse"
word2 = "ros"
```

|      | "" | r | o | s |
|------|---:|---:|---:|---:|
| **""** | 0 | 1 | 2 | 3 |
| **h**  | 1 | 1 | 2 | 3 |
| **ho** | 2 | 2 | 1 | 2 |
| **hor**| 3 | 2 | 2 | 2 |
| **hors**|4 | 3 | 3 | 2 |
| **horse**|5|4|4|3|

Final Answer:

```text
dp[5][3] = 3
```

One optimal sequence is

```text
horse
↓ Replace h → r

rorse
↓ Delete r

rose
↓ Delete e

ros
```

Total operations = **3**

---

# Complexity

### Time Complexity

There are

```text
(m+1) × (n+1)
```

states.

Each state takes **O(1)** time.

```text
Time = O(m × n)
```

---

### Space Complexity

The DP table contains

```text
(m+1) × (n+1)
```

cells.

```text
Space = O(m × n)
```

---

# Interview Explanation

> "`dp[i][j]` represents the minimum number of operations needed to convert the first `i` characters of `word1` into the first `j` characters of `word2`. If the current characters match, no operation is needed, so I copy the diagonal value. Otherwise, I consider the three possible operations: insert, delete, and replace. Each operation costs one, so I take the minimum of the left, top, and diagonal neighbors plus one. The first row and first column represent converting to or from an empty string, so they are initialized with the number of insertions or deletions required."

---

# Visual Mnemonic

```text
                word2
             ""  r  o  s

          ↖ Match / Replace

word1    ↑ Delete

          ← Insert
```

- **Left (`dp[i][j-1]`)** → Insert
- **Up (`dp[i-1][j]`)** → Delete
- **Diagonal (`dp[i-1][j-1]`)** → Replace (or Match)

---

# Code

```java
class Solution {

    //the word you want to convert to put in top right
    //the word to be converted put in top left
    //everywhere you have 3 choices
    ///insert replace or delete
    //horizontal insert
    //vertical delete
    //diagonal replace
    //take minimum
    //if character same take diagonal value
    //unlike LCS here 0th row and 0th col have values

    public int minDistance(String word1, String word2) {

        int m= word1.length();
        int n= word2.length();
        int[][] dp = new int[m+1][n+1];

        for(int i=0;i<=m;i++){
            for(int j=0;j<=n;j++){
                if(i==0||j==0)
                    dp[i][j]=i+j;
                else
                    dp[i][j] =word1.charAt(i-1)==word2.charAt(j-1)
                        ? dp[i-1][j-1]
                        : Math.min(
                            Math.min(dp[i-1][j], dp[i][j-1]),
                            dp[i-1][j-1]
                          )+1;

            }
        }

        return dp[m][n];
    }

}
```