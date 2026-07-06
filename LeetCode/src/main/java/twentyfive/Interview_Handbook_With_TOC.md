# Interview Handbook

## Table of Contents

- [Chapter 1: Interview Framework & Problem Intake](#chapter-1-interview-framework--problem-intake)
- [Chapter 2: Sliding Window & Two Pointer Patterns](#chapter-2-sliding-window--two-pointer-patterns)
- [Chapter 3: Monotonic Stacks](#chapter-3-monotonic-stacks)
- [Chapter 4: Binary Search Patterns](#chapter-4-binary-search-patterns)
- [Chapter 5: Intervals & Meeting Rooms](#chapter-5-intervals--meeting-rooms)
- [Chapter 6: Heaps & Priority Queues](#chapter-6-heaps--priority-queues)
- [Chapter 7: Graphs & Topological Sort](#chapter-7-graphs--topological-sort)
- [Chapter 8: LRU Cache](#chapter-8-lru-cache)
- [Chapter 9: Rate Limiter](#chapter-9-rate-limiter)
- [Chapter 10: Time-Series Analytics System](#chapter-10-time-series-analytics-system)
- [Chapter 11: Java Collections & Interview Cheat Sheet](#chapter-11-java-collections--interview-cheat-sheet)
- [Chapter 12: Pattern Recognition Master Guide](#chapter-12-pattern-recognition-master-guide)
- [Chapter 13: Trees & Binary Search Trees](#chapter-13-trees--binary-search-trees)
- [Chapter 14: Linked Lists](#chapter-14-linked-lists)
- [Chapter 15: Trie & Autocomplete](#chapter-15-trie--autocomplete)
- [Chapter 17: Design a HashMap](#chapter-17-design-a-hashmap)
- [Recognition Cheat Sheet](#recognition-cheat-sheet)
- [Must-Know LeetCode List](#must-know-leetcode-list)
---

# Handbook Navigation

## Core DSA Path

Interview Framework → Sliding Window → Monotonic Stack → Binary Search → Intervals → Heaps → Graphs

## Backend System Design Path

LRU Cache → Rate Limiter → Time-Series Analytics

## Java Collections Cross Reference

- HashMap → Chapter 11 and Chapter 17
- Deque → Chapters 2, 9, 11, 16
- Heap/PriorityQueue → Chapters 5, 6, 11
- TreeMap → Chapters 10 and 11
- Doubly Linked List → Chapters 8 and 14

---

# Chapter 1: Interview Framework & Problem Intake

## 1. Interview Execution Framework

Before writing a single line of code:

### Step 1: Listen & Rephrase

Repeat the problem back using the interviewer's domain.

Example:

> "Just to clarify, we're reconciling overlapping listener playback sessions to calculate peak concurrent listeners across the Audible platform."

**Why?**

* Confirms understanding
* Demonstrates communication
* Shows domain translation skills

---

### Step 2: Fire the Checklist

Ask 2–3 targeted questions before proposing an algorithm.

The goal is not to ask every question.

The goal is to eliminate entire categories of solutions.

---

### Step 3: State Constraints

Example:

> "Since N can reach 10⁶ and the data is static and fully loaded in memory, I'll target O(N log N) or better."

This signals:

* Complexity awareness
* Production mindset
* Scalability thinking

---

### Step 4: Propose the Algorithm

Only now should you start discussing:

* Sliding Window
* Heap
* DFS
* Binary Search
* Graph

---

### Step 5: Code

Keep variable names explicit.

Avoid:

```java
int x;
int y;
```

Prefer:

```java
int maxConcurrentListeners;
int distinctCharacters;
int currentWindowStart;
```

---

### Step 6: Validate Edge Cases

Before finishing:

* Empty input
* Single element
* Duplicate values
* Boundary conditions
* Invalid data

---

## 2. Clarification Question Bank

### 2.1 Maximum Input Size

**Question**

> What is the maximum constraint on the input size (N)?

**Why ask?**

If:

```text
N <= 10³
```

O(N²) may be acceptable.

If:

```text
N <= 10⁶
```

You are effectively forced into:

```text
O(N)
or
O(N log N)
```

This question often eliminates brute force immediately.

---

### 2.2 Data Range

**Question**

> What is the value range of the elements?

**Why ask?**

Negative values break many assumptions.

Examples:

* Sliding Window
* Prefix Sum optimizations
* Counting approaches

If values are tightly bounded, specialized approaches such as Counting Sort may be possible.

---

### 2.3 Static vs Streaming

**Question**

> Is the dataset static and fully loaded in memory, or is it a continuous stream?

**Why ask?**

This determines the entire architecture.

Static:

```java
Arrays.sort(...)
```

Streaming:

* Heap
* Sliding Window
* Deque
* Online processing

---

### 2.4 Event Ordering

**Question**

> Are events guaranteed to arrive in chronological order?

**Why ask?**

If not:

* Buffering
* Sorting
* Watermarks
* Reordering logic

may be required.

---

### 2.5 Empty Input

**Question**

> How should the system behave for null or empty input?

**Why ask?**

Shows defensive programming.

```java
if(nums == null || nums.length == 0)
    return 0;
```

---

### 2.6 Duplicates

**Question**

> Are duplicate IDs or duplicate records permitted?

**Why ask?**

May require:

```java
HashSet
```

or a deduplication stage.

---

## 3. Audible-Specific Clarification Examples

### Listener Concurrency Problem

#### Static vs Streaming

> Is this data static and loaded into memory, or a continuous stream?

Determines:

```text
Sorting
vs
Online Processing
```

---

#### Maximum Number of Logs

> What is the maximum number of session logs?

Eliminates:

```text
O(N²)
```

immediately.

---

#### Is Data Sorted?

> Is the incoming data already sorted?

Justifies:

```java
Arrays.sort(...)
```

---

### Timestamp Edge Cases

#### Inclusive vs Exclusive Overlap

Question:

> If one session ends exactly when another begins, is that considered overlap?

Example:

```text
[1000,5000]
[5000,8000]
```

Need clarification:

* Inclusive overlap
* Exclusive handoff

This changes comparison operators throughout the solution.

---

### Duplicate Logs

Question:

> Can duplicate tracking logs appear because of retries?

May require:

```java
HashSet
```

or a deduplication layer.

---

### Invalid Logs

Question:

> Can leave_time occur before join_time?

Production systems must validate inputs.

---

## 4. String / Sliding Window Clarification Examples

### Character Set

Question:

> Is the input limited to lowercase letters?

Determines:

```java
int[26]
```

vs

```java
int[128]
```

vs

```java
HashMap<Character,Integer>
```

* lowercase letters only → `int[26]`
* full ascii → `int[128]`
* extended ascii latin → `int[256]`
* unicode: characters → `hashmap<character,integer>`

---

### K = 0

Question:

> What should happen when K = 0?

Expected:

```java
return 0;
```

---

### UTF-8 Question

Question:

> Do we need to support Unicode characters or emojis?

Determines:

```java
HashMap<Character,Integer>
```

vs

```java
int[]
```

---

## 5. Interview Sound Bites

### Complexity

> "Since N can reach 10⁶, I'll target O(N) or O(N log N)."

### Clarification

> "Before I write the solution, I'd like to clarify a critical edge case."

### Constraints

> "This immediately eliminates a brute-force O(N²) approach."

### Production Thinking

> "I don't want to assume the data is sorted. I'd like to confirm that first."

### Streaming Systems

> "The architecture changes significantly depending on whether the data is static or unbounded."

### Fault Tolerance

> "Can malformed records occur in production, or are inputs guaranteed to be valid?"


# Chapter 2: Sliding Window & Two Pointer Patterns

## Pattern Recognition

When you hear:

* Longest
* Shortest
* Continuous
* Contiguous
* Subarray
* Substring
* Last K elements
* Rolling metrics
* Moving average
* Stream analytics
* Rate limiting

Immediately think:

```text
Sliding Window
```

---

## Core Mental Model

Maintain a window:

```text
[left .... right]
```

Expand:

```java
right++;
```

Shrink:

```java
left++;
```

Maintain a validity condition.

Examples:

* At most K distinct characters
* Sum <= Target
* Window size = K
* Last N seconds

---

# Longest Substring with K Distinct Characters

## Audible Framing

Audible's metadata gateway ingests a continuous stream of telemetry characters.

The platform must isolate the longest contiguous segment containing at most K distinct metadata types.

Example:

```text
S = "eceba"
K = 2

Answer = "ece"
Length = 3
```

---

## Clarification Questions

### Character Set

Question:

> Is the dataset limited to lowercase alphabets or does it span the full ASCII spectrum?

Why?

Determines:

```java
int[26]
```

vs

```java
int[128]
```

vs

```java
HashMap<Character,Integer>
```

---

### Maximum Input Size

Question:

> What is the maximum constraint on the input size?

Why?

Eliminates:

```text
O(N²)
```

solutions.

Justifies:

```text
O(N)
```

Sliding Window.

---

### K = 0

Question:

> How should the system behave when K = 0?

Expected:

```java
return 0;
```

---

### Unicode Support

Question:

> Do we need to support UTF-8 / Unicode characters?

If yes:

```java
HashMap<Character,Integer>
```

If no:

```java
int[]
```

is sufficient.

---

## Solution

```java
class Solution {

    // What happens when k = 0?
    // At most 0 distinct characters can exist, so return 0.
    //
    // Clarifying questions:
    // - What is the maximum constraint on input size?
    // - Can I assume lowercase alphabets, full ASCII, extended ASCII, or Unicode?
    // - How should the system behave when the input is null?

    // We will use a Sliding Window approach.
    //
    // We start by expanding the right edge of the window while keeping track
    // of the frequency of characters inside the current window.
    //
    // If the number of distinct characters exceeds K, we shrink the window
    // by moving the left edge until the window becomes valid again.
    //
    // We keep track of the maximum window length throughout the traversal.
    //
    // Although the code contains two while loops, every character enters the
    // sliding window exactly once when the right pointer moves and leaves the
    // window exactly once when the left pointer moves.
    //
    // Therefore, each character is processed at most twice.
    // The total number of pointer movements across the entire execution
    // is bounded by 2N.
    //
    // Time Complexity: O(N) (Amortized)
    // Space Complexity: O(1) (Frequency array has fixed size)

    public int lengthOfLongestSubstringKDistinct(String s, int k) {
		
        if (k == 0 || s == null || s.length()==0) { //dont use s.isBlank(), blank string is a valid string
            return 0;
        }

        int[] char_set = new int[128];

        // Example:
        // eceba
        int l = 0, r = 0; //,maxLength=0;
        int distinctChars = 0;
		

        // result[0] = max length
        // result[1] = left index
        // result[2] = right index
        int[] result = new int[3];

        while (r < s.length()) {
			char r_char = s.charAt(r);
			char_set[r_char]++;

            if (char_set[r_char] == 1)
                distinctChars++;

            while (distinctChars > k) {
				char l_char = s.charAt(l);
				char_set[l_char]--;

                if (char_set[l_char] == 0)
                    distinctChars--;
                l++;
            }

            // Update longest window
		    //maxLength=Math.max(maxLength, r-l+1);

            if (r - l + 1 >= result[0]) {
                result[0] = r - l + 1;
                result[1] = l;
                result[2] = r;
            }

            r++;
        }

        // substring is always [left, right + 1)
        System.out.println(s.substring(result[1], result[2] + 1));
		// return maxLength;
        return result[0];
    }
}
```

---

## Complexity

### Time

```text
O(N)
```

Each character:

* enters window once
* leaves window once

---

### Space

```text
O(1)
```

ASCII array.

---

## Interview Talking Point

> Since each character enters and exits the window at most once, the total number of pointer movements is bounded by 2N. Therefore the solution runs in O(N) time.
> Although there is a nested while loop, the left pointer never resets and only moves forward. Every character enters the window once when the right pointer expands the window and leaves the window at most once when the left pointer contracts it. Therefore, the total number of pointer movements across the entire execution is bounded by 2n, giving an amortized time complexity of O(n)

---

# Sliding Window Maximum

## Problem

Given:

```text
nums = [1,3,-1,-3,5,3,6,7]
k = 3
```

Return:

```text
[3,3,5,5,6,7]
```

---

## Audible Analogy

Imagine network throughput metrics arriving continuously.

For every rolling window of size K:

Determine the maximum available throughput.

---

## Key Insight

The window moves:

```text
[1 3 -1]
  [3 -1 -3]
     [-1 -3 5]
```

We need:

* Efficient insertion
* Efficient expiration
* Constant-time max lookup

---

## Why a Deque?

Question:

Do elements expire?

Answer:

Yes.

Old elements leave the window.

Therefore:

```text
Deque
```

instead of:

```text
Stack
```

---

## Monotonic Decreasing Deque

Visualization:

```text
Front -> Back

8 7 5 3
```

Largest value remains at the front.

Smaller values remain toward the back.

---

## Why Remove From Back?

Suppose:

```text
4 3 2
```

New value:

```text
5
```

arrives.

Now:

```text
4
3
2
```

can never become the maximum in any future window.

Remove them immediately.

---

## Solution

```java
class Solution {

    public int[] maxSlidingWindow(int[] nums, int k) {

        int n = nums.length;

        int[] result = new int[n-k+1];

        Deque<Integer> dq = new ArrayDeque<>();

        for(int i=0;i<n;i++) {

            while(!dq.isEmpty() &&
                  dq.peekFirst() <= i-k) {
                dq.pollFirst();
            }

            while(!dq.isEmpty() &&
                  nums[i] > nums[dq.peekLast()]) {
                dq.pollLast();
            }

            dq.offerLast(i);

            if(i >= k-1) {
                result[i-k+1] =
                    nums[dq.peekFirst()];
            }
        }

        return result;
    }
}
```

---

## Complexity

### Time

```text
O(N)
```

---

### Why Not O(N²)?

One insertion may cause multiple removals.

Example:

```text
1 2 3 4 5
```

When:

```text
5
```

arrives

many elements may be removed.

However:

Each element:

* enters once
* leaves once

Therefore:

```text
Total insertions = N
Total removals = N
```

Total operations:

```text
2N
```

---

## Amortized Analysis

Interview Sound Bite:

> I maintain a monotonic decreasing deque of indices. The front always contains the index of the maximum element in the current window. For every new element, I first remove expired indices from the front, then remove smaller elements from the back because they can never become the maximum. 
>  A single iteration may remove multiple elements from the deque. However, every element is inserted once and removed at most once. Across N events, there are at most 2N deque operations. Therefore the amortized complexity is O(1) per operation and O(N) overall. The space complexity is O(k).

---

# Sliding Window Recognition Cheat Sheet

When you hear:

* Longest substring
* Shortest substring
* At most K
* Exactly K
* Continuous
* Contiguous
* Rolling metrics
* Stream analytics
* Last N seconds
* Rate limiting

Think:

```text
Sliding Window
```

before considering any other approach.

## 2 pointer problems
Container With Most Water: https://leetcode.com/problems/container-with-most-water/
Trapping Rain Water: https://leetcode.com/problems/trapping-rain-water/



# Chapter 3: Monotonic Stacks

## Pattern Recognition

When you hear:

* Next Greater Element
* Next Smaller Element
* Previous Greater Element
* Previous Smaller Element
* Nearest Greater
* Nearest Smaller
* First Element to Left
* First Element to Right
* Daily Temperatures
* Stock Span
* Largest Rectangle Histogram
* Trapping Rain Water

Immediately think:

```text
Monotonic Stack
```

---

# Core Mental Model

The stack stores candidates that may answer future queries.

For each element:

1. Remove invalid candidates.
2. The remaining top is the answer.
3. Push current element.

The stack is maintained in a specific order:

* Monotonic Increasing
* Monotonic Decreasing

depending on the problem.

---

# Stack vs Deque Recognition

One of the most common interview mistakes is confusing monotonic stacks and monotonic queues.

## Use a Stack When

You are looking for:

* Next Greater
* Next Smaller
* Previous Greater
* Previous Smaller
* Closest Valid Element

Examples:

```text
Daily Temperatures
Next Greater Element
Stock Span
Largest Rectangle Histogram
```

Question:

> For this element, what is the first valid thing to my left or right?

This usually implies:

```text
Stack
```

---

## Use a Deque When

Elements expire.

Examples:

```text
Sliding Window Maximum
Rate Limiter
Moving Average
Rolling Metrics
```

Question:

> Do old elements leave the system?

If yes:

```text
Deque
```

because you need operations at both ends.

---

# Daily Temperatures

## Problem

Given:

```text
[73,74,75,71,69,72,76,73]
```

Return:

```text
[1,1,4,2,1,1,0,0]
```

Each position stores:

```text
Days until a warmer temperature.
```

---

# Recognition

Question:

For each day:

> What is the next warmer day?

This is:

```text
Next Greater Element
```

Therefore:

```text
Monotonic Stack
```

---

# Key Insight

We process:

```text
Right -> Left
```

because the answer exists in the future.

Future temperatures already exist in the stack.

---

# Solution

```java
class Solution {
    public int[] dailyTemperatures(int[] temperatures) {

        int n = temperatures.length;

        int[] result = new int[n];

        Stack<Integer> st = new Stack<>();

        for(int j = n - 1; j >= 0; j--) {

            while(!st.isEmpty() &&
                  temperatures[j] >= temperatures[st.peek()]) {
                st.pop();
            }

            if(!st.isEmpty()) {
                result[j] = st.peek() - j;
            }

            st.push(j);
        }

        return result;
    }
}
```

---

# Why Do We Pop?

Suppose:

```text
Current = 76
Stack Top = 73
```

Since:

```text
76 >= 73
```

73 can never help any future element.

Remove it.

---

# Why Store Indices Instead of Values?

We need:

```text
Distance
```

not temperature.

Answer requires:

```java
st.peek() - currentIndex
```

Therefore:

```java
Stack<Integer> // stores indices
```

---

# Monotonic Decreasing Stack

Interview wording:

> I maintain a monotonic decreasing stack of temperature indices.

Why?

Because larger temperatures stay deeper in the stack.

Example:

```text
Bottom

80
76
72
69

Top
```

As you move upward:

```text
80 > 76 > 72 > 69
```

Decreasing.

---

# Time Complexity

## Common Interview Concern

One iteration may pop many elements.

Example:

```text
1 2 3 4 5
```

When:

```text
5
```

arrives

many elements are removed.

---

## Amortized Analysis

Each index:

```text
Pushed once
Popped once
```

Therefore:

```text
Total pushes = N
Total pops = N
```

Total work:

```text
2N
```

Complexity:

```text
O(N)
```

---

# Interview Sound Bite

> I recognized this as a Next Greater Element problem. I process temperatures from right to left and maintain a monotonic decreasing stack of temperature indices. Any temperature less than or equal to the current temperature can never serve as the next warmer day for future elements, so I remove it. The remaining stack top gives me the nearest warmer day. Since each index is pushed and popped at most once, the solution runs in O(N) time.

---

# Monotonic Increasing vs Decreasing

## Monotonic Increasing Stack

Example:

```text
1
3
5
8
```

Used for:

```text
Next Smaller Element
```

patterns.

---

## Monotonic Decreasing Stack

Example:

```text
8
5
3
1
```

Used for:

```text
Next Greater Element
```

patterns.

---


# Stock Spanner - Monotonic Decreasing Stack

```java
class StockSpanner {
    Stack<int[]> st;

    public StockSpanner() {
        st = new Stack<>();
    }

    public int next(int price) {

        // Pattern recognition:
        // Previous greater element → Monotonic Stack
        // Maintain a monotonically decreasing stack (bottom high, top low)

        // Stack stores:
        // [price, span]

        // Example evolution:
        // 100:1 => there are 1 elements including itself and to the left that is less than equal to 100
        // 100:1 80:1 60:1
        // 100:1 80:1 70:2  (70 absorbs 60's span) .. if you find a higher element remove the lower element, however encode the information as to how many elements to left of it are less than or equal to the number
        // 100:1 80:1 70:2 60:1
        // 100:1 80:1 75:4  (75 absorbs 60 and 70's spans)
        // 100:1 85:6       (85 absorbs 75 and 80's spans)

        // The span represents the number of consecutive days
        // including today with price <= current price.
        // We reuse previously computed spans instead of scanning left.

        // Time Complexity:
        // Each price is pushed once and popped at most once.
        // Overall: O(N) for N calls
        // Amortized: O(1) per next() call

        // Space Complexity:
        // O(N)

        int count = 1;

        while (!st.isEmpty() && price >= st.peek()[0]) {
            count += st.pop()[1];
        }

        st.push(new int[]{price, count});

        return count;
    }
}
```


# Modern Java Note

Many engineers prefer:

```java
Deque<Integer> stack = new ArrayDeque<>();
```

instead of:

```java
Stack<Integer> stack = new Stack<>();
```

Reason:

```text
Stack is a legacy synchronized class.
```

ArrayDeque is generally preferred.

---

# Monotonic Stack Recognition Cheat Sheet

When you hear:

* Next Greater
* Next Smaller
* Previous Greater
* Previous Smaller
* Nearest
* Closest
* First element on left/right

Think:

```text
Monotonic Stack
```

before considering any other solution.

---

# Interview Quick Memory Trick

Question:

Can I solve this by finding the nearest valid element?

Examples:

```text
Daily Temperatures
Stock Span
Next Greater Element
```

Answer:

```text
Monotonic Stack
```

Question:

Do elements expire?

Examples:

```text
Sliding Window Maximum
Rate Limiter
Moving Average
```

Answer:

```text
Deque
```

This distinction alone solves a large class of interview problems.


# Chapter 4: Binary Search Patterns

## Pattern Recognition

When you hear:

* Minimum
* Maximum
* Smallest
* Largest
* Capacity
* Speed
* Days
* Threshold
* First Valid Answer
* Last Valid Answer
* Peak
* Boundary

Immediately think:

```text
Binary Search on Answer
```

---

# The Two Families of Binary Search

One of the biggest interview insights is that there are actually two different binary search patterns.

---

# Pattern 1: Exact Search

## Recognition

Question:

> Can I know the answer immediately when I see mid?

Examples:

* Binary Search
* Search Insert Position
* Search in Rotated Sorted Array

---

## Template

```java
while(lo <= hi){

    int mid = lo + (hi-lo)/2;

    if(nums[mid] == target)
        return mid;

    else if(nums[mid] < target)
        lo = mid + 1;

    else
        hi = mid - 1;
}
```

---

## Key Characteristic

You can return immediately:

```java
return mid;
```

because:

```text
mid itself may be the answer.
```

---

# Pattern 2: Boundary Search

## Recognition

Question:

> Is mid merely telling me whether I'm on the valid side or invalid side of a boundary?

Examples:

* Koko Eating Bananas
* First Bad Version
* Peak Element
* Capacity To Ship Packages
* Minimum Days To Make Bouquets

---

## Visualization

Imagine:

```text
Speed

1 2 3 4 5 6 7

F F F T T T T
      ^
   Answer
```

We do not know exactly where the boundary occurs.

We only know:

```text
Everything left is invalid.
Everything right is valid.
```

---

## Template

```java
while(lo < hi){

    int mid = lo + (hi-lo)/2;

    if(valid(mid))
        hi = mid;

    else
        lo = mid + 1;
}
```

---

## Key Characteristic

Never:

```java
return mid;
```

Instead:

```java
return lo;
```

after convergence.

---

# Exact Search vs Boundary Search

## Exact Search

Question:

```text
Can mid itself be THE answer?
```

Use:

```java
while(lo <= hi)
```

and:

```java
return mid;
```

---

## Boundary Search

Question:

```text
Am I searching for the first valid answer?
```

Use:

```java
while(lo < hi)
```

and:

```java
return lo;
```

---

# Koko Eating Bananas

## Problem

Given:

```text
piles = [3,6,7,11]
h = 8
```

Find:

```text
Minimum eating speed.
```

---

# Recognition

Keyword:

```text
Minimum
```

Immediately suggests:

```text
Boundary Search
```

---

# Key Insight

If Koko can finish at speed:

```text
k
```

then she can finish at:

```text
k+1
k+2
k+3
```

This produces a monotonic search space.

Example:

```text
Speed

1 2 3 4 5 6 7

F F F T T T T
```

---

# Search Space

## Lower Bound

```java
int lo = 1;
```

Reason:

Speed may be any positive integer.

Not necessarily a pile size.

---

## Upper Bound

```java
int hi = maxPile;
```

Reason:

At maximum pile size:

```text
Each pile takes at most one hour.
```

Guaranteed valid.

---

# Solution

```java
class Solution {

    public int minEatingSpeed(int[] piles, int h) {

        int lo = 1;
        int hi = piles[0];

        for(int i=1;i<piles.length;i++){
            hi = Math.max(hi,piles[i]);
        }

        while(lo < hi){

            int mid = lo + (hi-lo)/2;

            int t = timeElapsed(piles,mid);

            if(t > h){
                lo = mid + 1;
            }
            else{
                hi = mid;
            }
        }

        return lo;
    }

    public int timeElapsed(int[] piles,int speed){

        int timeElapsed = 0;

        for(int i=0;i<piles.length;i++){

            timeElapsed +=
                Math.ceil((double)piles[i]/speed);
        }

        return timeElapsed;
    }
}
```

---

# Complexity

Let:

```text
N = number of piles
M = largest pile
```

Binary Search:

```text
O(log M)
```

Validation:

```text
O(N)
```

Total:

```text
O(N log M)
```

---

# Interview Talking Point

> I recognized this as a binary search on answer problem. The key observation is that if Koko can finish at speed k, she can also finish at any higher speed. This creates a monotonic search space that allows binary search over possible speeds.

---

# Peak Element

## Problem

Find any peak element.

A peak is:

```text
nums[i] > nums[i-1]
nums[i] > nums[i+1]
```

---

# Recognition

Most candidates miss this.

Peak Element is actually:

```text
Boundary Search
```

not Exact Search.

---

# Key Observation

Suppose:

```text
1 3 5 7 6 4 2
```

At:

```text
mid = 5
```

Compare:

```java
nums[mid]
nums[mid+1]
```

---

## Case 1

```java
nums[mid] < nums[mid+1]
```

Example:

```text
1 3 5 7
      ^
```

We are ascending.

A peak must exist:

```text
to the right
```

Move:

```java
lo = mid + 1;
```

---

## Case 2

```java
nums[mid] > nums[mid+1]
```

Example:

```text
7 6 4 2
^
```

We are descending.

A peak must exist:

```text
to the left
```

Move:

```java
hi = mid;
```

---

# Solution

```java
class Solution {

    public int findPeakElement(int[] nums) {

        int lo = 0;
        int hi = nums.length - 1;

        while(lo < hi){

            int mid = lo + (hi-lo)/2;

            if(nums[mid] < nums[mid+1]){
                lo = mid + 1;
            }
            else{
                hi = mid;
            }
        }

        return lo;
    }
}
```

---

# Why Not hi = mid - 1?

Because:

```text
mid itself may be the peak.
```

Therefore:

```java
hi = mid;
```

not:

```java
hi = mid - 1;
```

---

# Peak Element Interview Sound Bite

> I don't need to locate every peak. I only need to find one. If nums[mid] < nums[mid+1], I am on an increasing slope and a peak must exist on the right. Otherwise I am on a decreasing slope and a peak must exist on the left including mid. This allows a logarithmic binary search solution.

---

# First Bad Version Pattern

Recognition:

```text
F F F F T T T T
```

Goal:

```text
Find first T
```

Template:

```java
if(isBad(mid))
    hi = mid;
else
    lo = mid + 1;
```

Return:

```java
lo
```

---

# Binary Search Recognition Cheat Sheet

## Exact Search

Keywords:

* Search
* Find Target
* Locate Element

Think:

```java
while(lo <= hi)
```

and:

```java
return mid;
```

---

## Boundary Search

Keywords:

* Minimum
* Maximum
* First
* Last
* Smallest
* Largest
* Peak
* Capacity
* Speed
* Days

Think:

```java
while(lo < hi)
```

and:

```java
return lo;
```

---

# Audible Interview Trick

Ask yourself:

## Question 1

Can I know the answer when I see mid?

Example:

```text
Search target 7
```

Answer:

```text
Yes
```

Use:

```java
while(lo <= hi)
```

---

## Question 2

Am I searching for a boundary?

Example:

```text
Minimum speed
First bad version
Peak element
Minimum capacity
```

Answer:

```text
Yes
```

Use:

```java
while(lo < hi)
```

Return after convergence.

This single distinction solves most binary search interview questions.

# Chapter 5: Intervals & Meeting Rooms

## Pattern Recognition

When you hear:

* Overlapping intervals
* Scheduling
* Resource allocation
* Concurrent sessions
* Active users
* Meeting rooms
* Listener concurrency
* Time ranges
* Session overlap

Immediately think:

```text
Intervals
```

Then ask:

```text
Do I need:
1. Merge intervals?
2. Count overlap?
3. Track maximum concurrency?
```

---

# Audible Listener Concurrency Problem

## Real World Analogy

Audible collects playback sessions from millions of listeners.

Each session:

```text
[startTime, endTime]
```

represents:

```text
User joins stream
User leaves stream
```

Goal:

Determine:

```text
Maximum number of concurrent listeners
```

at any point in time.

---

# Interview Clarification Questions

## Static vs Streaming

Question:

> Is the data static and already loaded into memory, or is it a continuous stream?

Why?

Static:

```java
Arrays.sort(...)
```

is acceptable.

Streaming:

Requires:

* Heap
* Event processing
* Online algorithm

---

## Maximum Input Size

Question:

> What is the maximum number of session logs?

Why?

Eliminates:

```text
O(N²)
```

solutions.

For:

```text
N = 10⁶
```

target:

```text
O(N log N)
```

or better.

---

## Is Data Sorted?

Question:

> Are the session logs already sorted?

Why?

Determines whether:

```java
Arrays.sort(...)
```

is required.

---

## Inclusive vs Exclusive Boundaries

Question:

> If one session ends exactly when another begins, do we consider that overlap?

Example:

```text
[1000,5000]
[5000,8000]
```

Possible interpretations:

### Inclusive

```text
Overlap exists
```

### Exclusive

```text
No overlap
```

This changes:

```java
>=
```

vs

```java
>
```

throughout the solution.

---

## Duplicate Logs

Question:

> Can duplicate session records appear due to retries?

May require:

```java
HashSet
```

or a deduplication stage.

---

## Invalid Sessions

Question:

> Can endTime occur before startTime?

Production systems should validate malformed data.

---

```java
class Solution {

    public int minMeetingRooms(int[][] intervals) {

        // Audible collects playback sessions from millions of listeners.
        // Each session: [startTime, endTime]
        // Determine the maximum number of concurrent users.

        // Clarifying Questions:
        // - Is the data statically loaded in memory or coming as a stream?
        // - What is the maximum input size (number of session logs)?
        // - Can duplicate intervals exist due to retries or network failures?
        // - Do we need to validate or discard malformed data?
        // - Is every session associated with a different user?
        // - Are session events already sorted chronologically?

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // Example:
        // [0,30] [5,10] [15,20]

        PriorityQueue<Integer> pq = new PriorityQueue<>();

        int maxConcurrentUsers = 0;

        for (int i = 0; i < intervals.length; i++) {

            // Remove all stale (finished) sessions before processing
            // the current session. The heap should contain only active users.
            while (!pq.isEmpty() && pq.peek() <= intervals[i][0]) {
                pq.poll();
            }

            pq.offer(intervals[i][1]);

            // Initially I considered inserting first and then removing stale entries.
            // However, that conceptually allows the current session to participate
            // in the cleanup. It is cleaner to remove completed sessions first,
            // then add the current one.
            //
            // If zero-duration sessions (start == end) are considered invalid,
            // they can simply be skipped using 'continue' before inserting.
            //
            // At any point, the heap size represents the number of active users.
            // Useful for telemetry/debugging:
            // System.out.println(pq.size());

            maxConcurrentUsers = Math.max(maxConcurrentUsers, pq.size());
        }

        return maxConcurrentUsers;
    }
}
```


# Merge Intervals

## Recognition

Goal:

```text
Combine overlapping ranges.
```

Example:

```text
[1,3]
[2,6]
[8,10]
[15,18]
```

Output:

```text
[1,6]
[8,10]
[15,18]
```

---

# Core Idea

Step 1:

Sort by start time.

Step 2:

Compare current interval with previous merged interval.

If overlap exists:

```java
currentEnd =
    Math.max(currentEnd,newEnd);
```

Otherwise:

Start a new interval.

---

# Solution
# Merge Intervals

```java
class Solution {

    public int[][] merge(int[][] intervals) {

        // Clarifying Questions:
        // - Can there be duplicate intervals?
        // - Should we validate faulty input (end < start)?
        // - Is the interval data already sorted?
        // - Is the data statically loaded in memory or coming as a stream?
        //
        // Assumption:
        // - Input is valid and loaded in memory.

         if(intervals == null || intervals.length <= 1)
            return intervals;


        // Sort intervals by start time.
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // LinkedList provides O(1) access to the last merged interval
		//Java's LinkedList is a doubly linked list, so getLast() is O(1).
        // using getLast(), making it convenient to merge intervals.
        LinkedList<int[]> mergedIntervals = new LinkedList<>();

        // Example:
        // [1,4] [2,3]
        // [1,4] [2,10]
        // [1,4] [5,10]

        for (int[] interval : intervals) {
		//in my orig solution, I used while.this is an interview improvement..
            // If this is the first interval or there is no overlap,
            // simply add it.
            if (mergedIntervals.isEmpty() ||
                interval[0] > mergedIntervals.getLast()[1]) {

                mergedIntervals.add(interval);

            } else {

                // Since intervals are sorted by start time,
                // an interval can only overlap with the most recently
                // merged interval.
                mergedIntervals.getLast()[1] =
                        Math.max(mergedIntervals.getLast()[1], interval[1]);
            }
        }

        // Time Complexity:
        // We first sort the intervals by start time in O(n log n).
        // Then we scan the sorted intervals once.
        // Since each interval is processed exactly once,
        // the merge step takes O(n).
        // Therefore, the overall time complexity is O(n log n).
        //
        // Space Complexity:
        // O(n) for the merged intervals.

        return mergedIntervals.toArray(new int[mergedIntervals.size()][2]);
    }
}
```
---

# Complexity

Sorting:

```text
O(N log N)
```

Scan:

```text
O(N)
```

Total:

```text
O(N log N)
```

---

# Interview Sound Bite

> Since intervals are unsorted, I first sort by start time. After sorting, any overlapping intervals become adjacent. I can then perform a single linear scan to merge them.

---

# Meeting Rooms II

## Problem

Given:

```text
[start,end]
```

meeting intervals.

Determine:

```text
Minimum rooms required.
```

Equivalent interpretation:

```text
Maximum concurrent meetings.
```

---

# Audible Analogy

Replace:

```text
Meeting
```

with:

```text
Listener Session
```

Now the question becomes:

```text
Maximum concurrent listeners.
```

Same algorithm.

---

# Core Idea

Sort by:

```text
Start Time
```

Maintain:

```text
Min Heap of End Times
```

Heap represents:

```text
Active sessions
```

---

# Why Min Heap?

Question:

Which active session finishes first?

Answer:

```text
Heap Top
```

---

# Solution

```java
public int minMeetingRooms(
        int[][] intervals) {

    if(intervals == null ||
       intervals.length == 0)
        return 0;

    int maxConcurrentRooms = 0;

    Arrays.sort(
        intervals,
        (a,b) ->
            Integer.compare(a[0],b[0])
    );

    PriorityQueue<Integer> minHeap =
        new PriorityQueue<>();

    for(int i=0;i<intervals.length;i++){

        while(!minHeap.isEmpty() &&
              intervals[i][0] >=
              minHeap.peek()){

            minHeap.poll();
        }

        minHeap.add(intervals[i][1]);

        maxConcurrentRooms =
            Math.max(
                maxConcurrentRooms,
                minHeap.size()
            );
    }

    return maxConcurrentRooms;
}
```

---

# Why Heap Size Equals Active Sessions

Heap stores:

```text
Ending times
```

for sessions currently active.

Example:

```text
Heap

5
8
12
```

means:

```text
3 active sessions
```

Therefore:

```java
minHeap.size()
```

equals:

```text
Current concurrency
```

---

# Global Peak Concurrency

Track:

```java
maxConcurrentRooms
```

during iteration.

This represents:

```text
Highest concurrency observed.
```

Equivalent to:

```text
Peak listeners
Peak meetings
Peak active streams
```

---

# Complexity

Sorting:

```text
O(N log N)
```

Heap Operations:

```text
O(log N)
```

per interval.

Total:

```text
O(N log N)
```

---

# Common Interview Mistake

Returning:

```java
minHeap.size()
```

at the end.

Why?

Because:

```text
Final active sessions
```

is not necessarily:

```text
Maximum active sessions
```

Need:

```java
maxConcurrentRooms
```

instead.

---

# Interview Sound Bite

> I sort sessions by start time and maintain a min heap of ending times. The heap contains only currently active sessions. Whenever a new session begins, I remove every session that has already ended. The heap size therefore represents current concurrency, and the maximum heap size observed during processing represents peak concurrency.

---

# Interval Recognition Cheat Sheet

## Merge Intervals

Keywords:

* Merge
* Consolidate
* Deduplicate ranges

Think:

```text
Sort + Linear Scan
```

---

## Meeting Rooms

Keywords:

* Concurrent
* Overlap count
* Resource allocation
* Active sessions

Think:

```text
Min Heap
```

---

## Audible Mapping

```text
Meeting Room
=
Listener Session

Meeting Overlap
=
Concurrent Listeners

Meeting Scheduler
=
Playback Session Scheduler
```

This translation often makes interval problems easier to reason about during Audible interviews.

# Chapter 6: Heaps & Priority Queues

## Pattern Recognition

When you hear:

* Top K
* K Largest
* K Smallest
* Most Frequent
* Least Frequent
* Ranking
* Leaderboard
* Priority
* Schedule
* Next Available Resource
* Streaming Top K
* Median of a data Stream
Immediately think:

```text
Heap / Priority Queue
```

---

# Core Mental Model

A Heap helps when:

```text
I don't need everything sorted.
I only need the best K items.
```

Instead of:

```text
Sort entire dataset
```

Use:

```text
Maintain only K candidates
```

---

# Why Not Sort?

Suppose:

```text
N = 1,000,000
K = 10
```

Sorting:

```text
O(N log N)
```

Heap:

```text
O(N log K)
```

Since:

```text
log 10 << log 1,000,000
```

Heap is significantly more efficient.

---

# Heap Types

## Min Heap

Smallest element at top.

Java:

```java
PriorityQueue<Integer> minHeap =
    new PriorityQueue<>();
```

---

## Max Heap

Largest element at top.

Java:

```java
PriorityQueue<Integer> maxHeap =
    new PriorityQueue<>(
        (a,b) -> b-a
    );
	
Similar to reverse sort comparator
list.sort((a, b) -> Integer.compare(b[1], a[1]));	
```

---

# Interview Recognition Rule

Question:

> Do I need ALL elements sorted?

If:

```text
Yes
```

Use:

```text
Sorting
```

If:

```text
No, I only need the best K
```

Use:

```text
Heap
```

---

# Top K Frequent Elements

## Problem

Given:

```text
nums = [1,1,1,2,2,3]
k = 2
```

Return:

```text
[1,2]
```

---

# Recognition

Keywords:

```text
Top K
Most Frequent
```

Immediately suggests:

```text
Heap
```

---
# Top K Frequent Elements - Interview Notes

## Clarifying Questions

* What is the maximum input size?
* Is `k` always valid (`1 <= k <= number of unique elements`)?
* Does the output need to be in any specific order?

---

## Key Idea

* Build a frequency map.
* Maintain a **Min Heap** of size at most **K**.
* The heap always contains the **K most frequent elements seen so far**.
* Whenever the heap size exceeds `K`, remove the least frequent element.
* At the end, the heap contains exactly the **K most frequent elements**.
* **Brute-force approach:** Convert the frequency map to a list of `[num, freq]` pairs (e.g., `List<int[]>`), then sort it in **descending order of frequency** using either:
  * `Collections.sort(list, (a, b) -> Integer.compare(b[1], a[1]))`, or
  * `list.sort((a, b) -> Integer.compare(b[1], a[1]))`.
  * Return the first `K` elements from the sorted list.

> **Note:** A heap is **not sorted**.
> Only the **root** is guaranteed to be the minimum frequency.
> The remaining elements (including the most frequent one) can be anywhere in the heap—they are **not necessarily at the bottom**.

---

## Why Min Heap?

* Sorting all unique elements would cost **O(U log U)**.
* Instead, keep only the best `K` candidates.
* Heap size never exceeds `K`.
* `offer()` = **O(log K)**
* `poll()` = **O(log K)**

---

## Understanding the Complexity

Let:

* `N` = total number of elements.
* `U` = number of unique elements.

### Frequency Map

```text
O(N)
```

### Heap

Every unique element is:

* inserted **once** → `U` offers
* removed **at most once** → `U - K` polls (worst case)

Total heap operations:

```text
(U + U - K) × log K
= (2U - K) log K
= O(U log K)
```

Since:

```text
U ≤ N
```

Overall:

```text
O(N) + O(U log K)

Worst Case:

O(N) + O(N log K)
= O(N log K)
```

---

## Space Complexity

* Frequency Map: **O(U)**
* Min Heap: **O(K)**

Overall:

```text
O(U + K)

Worst case: All elements are unique
O(N+K)
```

---

# Interview Sound Bite
> Build a frequency map first. Then maintain a Min Heap of size at most `K`. For every unique element, insert it into the heap. If the heap grows beyond `K`, remove the least frequent element. This guarantees the heap always contains the `K` most frequent elements seen so far. Each unique element is inserted once and removed at most once, so the heap work is `O(U log K)`.

> Since I only need the top K elements, sorting the entire dataset would perform unnecessary work. Instead, I maintain a min heap of size K and discard weaker candidates as I process the input.

---

# K Closest Points To Origin

## Problem

Given:

```text
(x,y)
```

points.

Return:

```text
K closest points
```

---

# Recognition

Keywords:

```text
Closest K
Top K
Nearest K
```

Think:

```text
Heap
```

---

# Distance Formula

Optimization:

Do not compute square root.

Compare:

```text
x² + y²
```

instead.

---
# Comparator Hint

```java
PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> {
    int ax = Math.abs(a - x);
    int bx = Math.abs(b - x);

    if (ax == bx) {
        // If distances are equal, the larger value has lower priority
        // (smaller value is preferred by the problem).
        return Integer.compare(b, a);
    }

    // Max Heap based on distance from x.
    // The farthest element stays at the top and gets removed first.
    return Integer.compare(bx, ax);
});

// Also review the Binary Search solution in this doc for the optimal approach for sorted input.
```
---
# Complexity

```text

O(N log K + K log K)

Since K ≤ N, many people simply write:

O(N log K)

```

---

# LeetCode 3408 - Design Task Manager

---

# Problem Summary

Design a task manager supporting the following operations:

- Add a task
- Edit a task's priority
- Remove a task
- Execute the highest priority task

Rules:

- Every task has
  - `taskId`
  - `userId`
  - `priority`
- `taskId` is globally unique.
- Highest priority executes first.
- If priorities are equal, execute the **largest taskId**.

---

# Questions to Ask the Interviewer

These questions demonstrate senior-level thinking.

## 1. Can multiple tasks have the same priority?

Yes.

If yes,

ask

> **How are ties broken?**

For this problem

```
Higher priority

↓

If equal priority

↓

Larger taskId
```

---

## 2. Is taskId globally unique?

If not,

our map would become

```java
Map<Pair<UserId, TaskId>, Task>
```

instead of

```java
Map<TaskId, Task>
```

Fortunately, the problem guarantees uniqueness.

---

## 3. What are the maximum constraints?

This determines whether

```
O(N)
```

operations are acceptable.

For

```
100 tasks
```

nobody cares.

For

```
10 million tasks
```

linear removal from a heap becomes unacceptable.

---

## 4. Can duplicate requests arrive because of retries?

In distributed systems this happens frequently.

Example

```
Add Task 10

↓

Timeout

↓

Client retries

↓

Add Task 10 again
```

Need clarification.

Possible handling

- Ignore duplicates
- Update existing task
- Throw exception

LeetCode ignores this.

---

## 5. Is this a single server?

Important production question.

Single server

↓

Simple HashMap + Heap

Multiple servers

↓

Need coordination.

We'll discuss this later.

---

## 6. Can I change the data structures?

Usually interviewers only specify

```
Operations
```

They don't tell you

```
HashMap

PriorityQueue
```

Choosing the data structures is part of the interview.

---

# First Design

Initially we considered

```
Map<TaskId, UserTask>

+

PriorityQueue<TaskId>
```

where

```
Map

↓

Stores latest priority

PriorityQueue

↓

Stores taskId only
```

Example

```
Map

10 -> priority 5
```

Heap

```
10
```

Looks good.

---

# Why Doesn't This Work?

Suppose

```
Task 10

Priority = 5
```

Later

```
Edit

Priority = 100
```

Map becomes

```
10 -> priority 100
```

If we push

```
10
```

again,

heap becomes

```
10

10
```

Now both heap entries refer to

```
Map

↓

Priority = 100
```

We've lost the old priority.

Therefore,

we cannot distinguish

```
Old heap entry

vs

New heap entry
```

Lazy deletion becomes impossible.

---

# Solution

The heap must store a **snapshot**.

Instead of

```
PriorityQueue<TaskId>
```

we use

```
PriorityQueue<HeapNode>
```

where

```java
class HeapNode{

    int taskId;

    int priority;
}
```

Now the heap contains

```
(taskId=10, priority=5)

(taskId=10, priority=100)
```

The map contains only

```
10

↓

priority = 100
```

Now stale entries are detectable.

---

# Final Design

```
HashMap

↓

Current Truth

---------------------------

PriorityQueue

↓

Historical Snapshots
```

Map

```java
Map<Integer, UserTask>
```

stores

```java
class UserTask{

    int userId;

    int priority;
}
```

Priority Queue

```java
PriorityQueue<HeapNode>
```

stores

```java
class HeapNode{

    int taskId;

    int priority;
}
```

---

# Why Two Classes?

The two structures have different responsibilities.

## UserTask

Represents

```
Latest version
```

Example

```
Task 10

Priority 100
```

---

## HeapNode

Represents

```
Priority when inserted into heap
```

Example

```
Task 10

Priority 5
```

This separation enables lazy deletion.

---

# Lazy Deletion

Instead of removing stale entries from the heap,

we simply update the map.

Example

Initially

```
Task 10

Priority = 5
```

Map

```
10 -> priority 5
```

Heap

```
(10,5)
```

Edit

```
Priority = 100
```

Map

```
10 -> priority 100
```

Heap

```
(10,5)

(10,100)
```

Notice

we never removed

```
(10,5)
```

Later

during execution

Pop

```
(10,100)
```

Compare

```
Heap Priority

100

↓

Map Priority

100
```

Valid.

Execute.

Later

Pop

```
(10,5)
```

Compare

```
Heap Priority

5

↓

Map Priority

No task

OR

100
```

Mismatch.

Discard.

This is

```
Lazy Deletion
```

---

# Why Not Remove from the Heap?

Initially we considered

```java
pq.remove(taskId);
```

Unfortunately,

Java's PriorityQueue is implemented as an array-backed binary heap.

Removing an arbitrary element requires

```
Linear Search

↓

Heapify
```

Complexity

```
Search

O(N)

+

Heapify

O(logN)

=

O(N)
```

This becomes expensive.

---

# Why Doesn't the Heap Automatically Rebalance?

Suppose

```
Heap

Task 10

Priority 5
```

We update

```java
task.priority = 100;
```

The heap structure has **no idea** the priority changed.

Java's PriorityQueue never reorders itself when external data changes.

Therefore,

after changing priority,

you must either

```
Remove + Reinsert

or

Lazy Delete
```

---

# Final Java Solution

```java
class TaskManager {

    Map<Integer, UserTask> taskMap = new HashMap<>();

    PriorityQueue<HeapNode> pq = new PriorityQueue<>((a, b) -> {

        if (a.priority == b.priority)
            return Integer.compare(b.taskId, a.taskId);

        return Integer.compare(b.priority, a.priority);
    });

    // O(N log N)
    // Insert every task into the heap.

    public TaskManager(List<List<Integer>> tasks) {

        for (List<Integer> task : tasks) {

            add(
                task.get(0),
                task.get(1),
                task.get(2)
            );
        }
    }

    // O(log N)

    public void add(int userId,
                    int taskId,
                    int priority) {

        taskMap.put(
            taskId,
            new UserTask(userId, priority)
        );

        pq.offer(
            new HeapNode(taskId, priority)
        );
    }

    // O(log N)

    public void edit(int taskId,
                     int newPriority) {

        // Do NOT remove from the heap.
        // PriorityQueue.remove(Object) is O(N).

        UserTask editedTask =
            taskMap.get(taskId);

        editedTask.priority = newPriority;

        pq.offer(
            new HeapNode(taskId, newPriority)
        );
    }

    // O(1)

    public void rmv(int taskId) {

        // Lazy deletion.
        // Heap entries are discarded later.

        taskMap.remove(taskId);
    }

    // Amortized O(log N)
	 //remove entries deleted //lazy deletion
            /*while(!taskMap.containsKey(pq.peek().taskId)&& !pq.isEmpty()){
                 pq.poll();
            }*/
            //if task has priority 3 downgraded to 10.. taskid 3, taskId 10.. encounter 3.. dont remove from map 
            //if task has priority 7 upgraded to 2...we would have removed it from map.. in that case the while loop abve
            //should solve it..

    public int execTop() {

        while (!pq.isEmpty()) {

            HeapNode heapEntry = pq.poll();

            UserTask userTask =
                taskMap.get(heapEntry.taskId);

            if (userTask == null)
                continue;

            if (userTask.priority !=
                heapEntry.priority)
                continue;

            taskMap.remove(heapEntry.taskId);

            return userTask.userId;
        }

        return -1;
    }
}

class UserTask {

    int userId;
    int priority;

    UserTask(int userId,
             int priority) {

        this.userId = userId;
        this.priority = priority;
    }
}

class HeapNode {

    int taskId;
    int priority;

    HeapNode(int taskId,
             int priority) {

        this.taskId = taskId;
        this.priority = priority;
    }
}
```

---

# Complexity

| Operation | Complexity |
|------------|------------|
| Constructor | O(N log N) |
| add | O(log N) |
| edit | O(log N) |
| rmv | O(1) |
| execTop | Amortized O(log N) |

---

# Why is execTop() Amortized O(log N)?

At first glance

```java
while(!pq.isEmpty())
```

looks scary.

Could it become

```
O(N)
```

Yes,

a **single** execution might discard many stale entries.

However,

each stale heap entry

```
Inserted once

↓

Discarded once
```

Never again.

Across

```
N operations
```

each heap node is processed exactly once.

Therefore

Total work

```
O(N log N)
```

Average

```
O(log N)
```

per operation.

---

# Production Discussion

## Would I Use Lazy Deletion?

Probably not.

Lazy deletion is excellent for interviews.

Production systems often use an

```
Indexed Priority Queue
```

instead.

---

# What is an Indexed Priority Queue?

Maintain

```
Heap

+

Map<TaskId, HeapIndex>
```

Example

```
Heap

Index 0

Task 20

Priority 100

------------------

HeapIndex Map

20 -> 0
```

Now

editing priority becomes

```
Find heap index

↓

Update priority

↓

Heapify Up

or

Heapify Down
```

No duplicate heap entries.

No stale entries.

True

```
O(log N)
```

update.

This requires implementing your own heap instead of Java's `PriorityQueue`.

---

# Multiple Server Instances

Suppose

```
Server A

Server B
```

Both receive

```
Execute Highest Priority
```

at the same time.

Without coordination

both may execute

```
Task 10
```

This creates duplicate execution.

Solutions

- Distributed lock
- Leader election
- Atomic queue service

---

## ZooKeeper

One common solution is

```
Apache ZooKeeper
```

Servers first acquire a distributed lock.

Only the server holding the lock may execute

```
execTop()
```

Others wait.

---

## Redis

Another approach

```
Redis
```

using

```
SETNX
```

or RedLock.

---

## Better Architecture

Instead of every application server owning its own heap,

use

```
Central Queue

↓

Kafka

RabbitMQ

Redis Sorted Set
```

Application servers consume from the central scheduler.

Only one consumer receives a task.

---

# Coding Style Notes

Using

```java
continue;
```

is perfectly acceptable.

Example

```java
if(userTask == null)
    continue;

if(userTask.priority != heapEntry.priority)
    continue;
```

This is called a

```
Guard Clause
```

or

```
Early Continue
```

It keeps the

```
Happy Path
```

unindented and is common in production code.

---

# Interview Explanation

If the interviewer asks

> **Why didn't you remove from the PriorityQueue?**

A good answer is:

> Java's `PriorityQueue.remove(Object)` is O(N) because it performs a linear search before restoring the heap. To avoid that cost, I use lazy deletion. The HashMap stores the latest state of each task, while the PriorityQueue stores immutable snapshots. During `execTop()`, stale heap entries are discarded until a snapshot matches the current state in the map. This gives amortized O(log N) performance.

---

# Key Design Pattern

This problem demonstrates an important interview pattern.

```
HashMap

↓

Source of Truth

-------------------------

Priority Queue

↓

Immutable Snapshots

↓

Lazy Deletion
```

Whenever

- priorities change,
- arbitrary heap removal is expensive,
- and stale data is acceptable,

consider this pattern.

---

# One Sentence to Remember

> **The HashMap stores the latest version of every task, the PriorityQueue stores immutable snapshots ordered by priority, and lazy deletion eliminates expensive heap removals while maintaining amortized O(log N) performance.**




# Task Scheduler

## Recognition

Keywords:

* Schedule
* Cooldown
* Priority
* Highest frequency first

Think:

```text
Max Heap
```

---

# Core Idea

Always execute:

```text
Most frequent task
```

first.

Reason:

High-frequency tasks are hardest to place.

---

# Audible Analogy

Imagine:

```text
Audiobook recommendation jobs
```

Each job type has:

```text
frequency
```

Goal:

Schedule jobs efficiently while respecting cooldown periods.

---

# Meeting Rooms Revisited

Question:

Why did we use a heap?

Because we needed:

```text
Earliest finishing session
```

at any moment.

Heap Top:

```text
Minimum end time
```

This is exactly what heaps are designed for.

---

# 295. Find Median from Data Stream

## Pattern Recognition

Keywords:

```text
Median
Running Median
Data Stream
Continuous Input
Online Processing
```

Think:

```text
Two Heaps
```

---

## Core Idea

The median splits the dataset into:

```text
Lower Half
Upper Half
```

We maintain:

```text
Max Heap -> Lower Half
Min Heap -> Upper Half
```

The heap peaks always contain the middle element(s).

Example:

```text
1 2 3 4 5

Max Heap = [2,1]
Min Heap = [3,4,5]

Median = 3
```

Example:

```text
1 2 3 4

Max Heap = [2,1]
Min Heap = [3,4]

Median = (2 + 3) / 2
```

---

## Key Invariants

```text
1. maxHeap stores lower half

2. minHeap stores upper half

3. maxHeap.size() >= minHeap.size()

4. Size difference <= 1

5. maxHeap.peek() <= minHeap.peek()
```

---

## Implementation

```java
class MedianFinder {

    PriorityQueue<Integer> minHeap;
    PriorityQueue<Integer> maxHeap;

    public MedianFinder() {

        minHeap = new PriorityQueue<>();

        maxHeap =
            new PriorityQueue<>((a,b) -> Integer.compare(b,a));
    }

    public void addNum(int num) {

        // maxHeap stores lower half
        // minHeap stores upper half

        // top of maxHeap = largest element in lower half
        // top of minHeap = smallest element in upper half

        // median will always be at one or both peaks

        maxHeap.add(num);

        // push largest element of lower half
        // into upper half
        minHeap.add(maxHeap.poll());

        // ensure maxHeap has either:
        // same number of elements
        // OR one extra element

        if(minHeap.size() > maxHeap.size()) {
            maxHeap.add(minHeap.poll());
        }
    }

    public double findMedian() {

        if(maxHeap.size() > minHeap.size())
            return maxHeap.peek();

        return ((double)maxHeap.peek()
                + (double)minHeap.peek()) / 2.0;
    }
}
```

---

## Why This Works

Every inserted value is forced through both heaps:

```text
Insert
   ↓
maxHeap
   ↓
minHeap
   ↓
Rebalance
```

This automatically preserves:

```text
maxHeap.peek() <= minHeap.peek()
```

without needing explicit comparisons.

The median therefore always remains at the heap peaks.

---

## Interview Sound Bite

We maintain two heaps: a max heap for the lower half and a min heap for the upper half. Every inserted value is pushed through both heaps, which naturally preserves the lower-half/upper-half ordering. Rebalancing ensures the heap sizes differ by at most one, allowing O(log N) insertion and O(1) median retrieval.

---

## Complexity

```text
addNum()      O(log N)

findMedian()  O(1)
```

---

## Recognition Cheat Sheet

Keywords:

```text
Median
Running Median
Stream
Continuous Input
Online Processing
```

Think:

```text
Two Heaps

Max Heap -> Lower Half
Min Heap -> Upper Half
```

---

## Related Problems

```text
215. Kth Largest Element in an Array

347. Top K Frequent Elements

23. Merge K Sorted Lists

295. Find Median from Data Stream
```




# Heap Operations Cheat Sheet

## Insert

```java
heap.offer(x);
```

Complexity:

```text
O(log N)
```

---

## Remove Top

```java
heap.poll();
```

Complexity:

```text
O(log N)
```

---

## Peek Top

```java
heap.peek();
```

Complexity:

```text
O(1)
```

---

# Common Interview Mistake

Using:

```java
Collections.sort(...)
```

for a Top K problem.

Ask:

```text
Do I actually need all N elements sorted?
```

If answer is:

```text
No
```

then a heap is usually better.

---

# Streaming Top K

This is extremely common in production systems.

Examples:

```text
Top 10 Audiobooks
Top 100 Customers
Most Active Users
Most Frequent Search Terms
```

Data never stops arriving.

Sorting continuously is expensive.

Heap allows:

```text
Continuous updates
```

while maintaining:

```text
Top K
```

in real time.

---

# Audible Interview Mapping

## Top K Frequent

```text
Most played audiobooks
```

---

## K Closest

```text
Nearest CDN nodes
Nearest service endpoints
```

---

## Task Scheduler

```text
Background processing jobs
Recommendation refresh jobs
Metadata synchronization
```

---

## Meeting Rooms

```text
Concurrent listener sessions
```

---

# Heap Recognition Cheat Sheet

Keywords:

* Top K
* Largest K
* Smallest K
* Most Frequent
* Least Frequent
* Closest K
* Ranking
* Leaderboard
* Priority
* Scheduler

Think:

```text
Heap
```

before considering sorting.

---

# Interview Quick Memory Trick

Question:

Do I need:

```text
Everything sorted?
```

Use:

```text
Sorting
```

Question:

Do I only need:

```text
Best K items?
```

Use:

```text
Heap
```

This distinction alone solves a large class of interview problems.


# Chapter 7: Graphs & Topological Sort

## Pattern Recognition

When you hear:

* Dependency
* Prerequisite
* Build Order
* Boot Sequence
* Service Startup Order
* Task Dependencies
* Directed Graph
* Workflow Pipeline

Immediately think:

```text
Graph
```

Then ask:

```text
Is there a dependency relationship?
```

If yes:

```text
Topological Sort
```

---

# Graph Fundamentals

A graph consists of:

```text
Vertices (Nodes)
Edges (Relationships)
```

Example:

```text
A --> B
A --> C
B --> D
C --> D
```

Interpretation:

```text
A must complete before B
A must complete before C
B must complete before D
C must complete before D
```

---

# Directed vs Undirected Graphs

## Directed Graph

```text
A --> B
```

Meaning:

```text
A depends on B
```

or

```text
A must occur before B
```

Direction matters.

Examples:

* Service dependencies
* Course schedules
* Build systems
* Boot sequences

---

## Undirected Graph

```text
A --- B
```

Relationship is symmetric.

Examples:

* Social network friendship
* Physical network connections

---

# Graph Traversal

Two primary techniques:

```text
DFS
BFS
```

---

# Depth First Search (DFS)

## Mental Model

Go as deep as possible before backtracking.

Example:

```text
A
|
B
|
C
|
D
```

Traversal:

```text
A -> B -> C -> D
```

---

## Recursive DFS Template

```java
public void dfs(int node){

    visited.add(node);

    for(int neighbor : graph.get(node)){

        if(!visited.contains(neighbor)){
            dfs(neighbor);
        }
    }
}
```

---

# Breadth First Search (BFS)

## Mental Model

Explore level by level.

Uses:

```text
Queue
```

instead of:

```text
Stack
```

---

## BFS Template

```java
Queue<Integer> queue =
    new LinkedList<>();

queue.offer(start);

while(!queue.isEmpty()){

    int current = queue.poll();

    for(int neighbor :
        graph.get(current)){

        if(!visited.contains(neighbor)){

            visited.add(neighbor);
            queue.offer(neighbor);
        }
    }
}
```

---

# Complexity

DFS:

```text
O(V + E)
```

BFS:

```text
O(V + E)
```

Where:

```text
V = Vertices
E = Edges
```

---

# Topological Sort

## Recognition

Question:

> Is there an ordering constraint?

Examples:

```text
Course Schedule
Boot Sequence
Build Pipeline
Deployment Workflow
```

Think:

```text
Topological Sort
```

---

# Boot Sequence Problem

One of the strongest examples from your notes.

---

## Problem

Services have dependencies.

Example:

```text
Database

↓

Authentication Service

↓

API Gateway

↓

Frontend
```

Question:

```text
In what order should services start?
```

---

# Graph Representation

Represent:

```text
Dependency → Dependent
```

Example:

```text
Database -> Auth
Auth -> Gateway
Gateway -> Frontend
```

---

# Why DFS Works

A dependency must start:

```text
Before
```

the service that depends on it.

DFS naturally processes:

```text
Dependencies first
Dependents later
```

---

# Post-Order Traversal Insight

Suppose:

```text
A -> B -> C
```

DFS visits:

```text
A
B
C
```

Returns:

```text
C
B
A
```

Reverse:

```text
A
B
C
```

Valid boot order.

---

# White / Gray / Black Coloring

One of the cleanest cycle detection techniques.

---

## WHITE

```text
Unvisited
```

---

## GRAY

```text
Currently being processed
```

Node exists on current DFS path.

---

## BLACK

```text
Fully processed
```

Safe.

---

# Cycle Detection

Suppose:

```text
A -> B
B -> C
C -> A
```

During DFS:

```text
A = GRAY
B = GRAY
C = GRAY
```

Encounter:

```text
C -> A
```

A is already:

```text
GRAY
```

This indicates:

```text
Cycle
```

---

# Key Interview Sound Bite

> Encountering a GRAY node means we have reached a node already on the current DFS path, which implies a cycle.

---

# Boot Sequence Solution Structure

```java
Map<String,List<String>> graph;

Map<String,Color> state;

List<String> bootOrder;
```

Where:

```java
enum Color{
    WHITE,
    GRAY,
    BLACK
}
```

---

# DFS Logic

```java
private boolean dfs(String service){

    if(state.get(service)
            == Color.GRAY){
        return false;
    }

    if(state.get(service)
            == Color.BLACK){
        return true;
    }

    state.put(service, Color.GRAY);

    for(String dependency :
        graph.get(service)){

        if(!dfs(dependency)){
            return false;
        }
    }

    state.put(service, Color.BLACK);

    bootOrder.add(service);

    return true;
}
```

---

# Complexity

Each node:

```text
Visited once
```

Each edge:

```text
Processed once
```

Total:

```text
O(V + E)
```

Space:

```text
O(V)
```

for:

* Recursion stack
* State map
* Result list

---

# Audible Analogy

Imagine:

```text
Metadata Service
Recommendation Service
Playback Service
Notification Service
```

Dependencies:

```text
Metadata
    ↓
Recommendation
    ↓
Notification
```

You cannot start:

```text
Notification
```

before:

```text
Recommendation
```

Topological Sort guarantees:

```text
Correct startup order.
```

---

# Graph Interview Questions

## Clarification Questions

### Directed or Undirected?

Question:

> Is this graph directed or undirected?

Critical because:

```text
Cycle detection differs.
```

---

### Can Cycles Exist?

Question:

> Are cycles allowed?

May eliminate:

```text
Topological Sort
```

entirely.

---

### Graph Size

Question:

> What are the constraints on vertices and edges?

Determines:

```text
Adjacency List
```

vs

```text
Adjacency Matrix
```

---

### Connectivity

Question:

> Is the graph guaranteed to be connected?

If not:

Need:

```java
for(all vertices)
    dfs(vertex);
```

---

# Graph Recognition Cheat Sheet

Keywords:

* Dependency
* Prerequisite
* Build Order
* Course Schedule
* Boot Sequence
* Workflow
* Directed Relationship

Think:

```text
Graph
```

Then ask:

```text
Need ordering?
```

If yes:

```text
Topological Sort
```

---

# Interview Quick Memory Trick

Question:

Can task B occur before task A?

If:

```text
No
```

You likely have:

```text
Dependency Graph
```

Question:

Am I being asked for:

```text
Valid execution order?
```

Think:

```text
Topological Sort
```

Question:

Can cycles break the system?

Think:

```text
DFS + White/Gray/Black
```

This combination solves a large percentage of graph interview problems.

# Chapter 8: LRU Cache

## Pattern Recognition

When you hear:

* Cache
* Eviction
* Recently Used
* Least Recently Used
* O(1) Get
* O(1) Put
* Fixed Capacity Cache

Immediately think:

```text
HashMap + Doubly Linked List
```

---

# Problem Statement

Design:

```text
LRU Cache
```

Support:

```java
get(key)
put(key,value)
```

Requirements:

```text
O(1) get
O(1) put
```

When capacity is reached:

```text
Evict Least Recently Used Item
```

---

# Example

Capacity:

```text
2
```

Operations:

```text
put(1,1)
put(2,2)

get(1)

put(3,3)
```

Question:

Which key should be removed?

Answer:

```text
2
```

Why?

Because:

```text
1 was recently accessed.
2 became least recently used.
```

---

# Interview Clarification Questions

## Capacity

Question:

> Is capacity always greater than zero?

---

## Thread Safety

Question:

> Is the cache expected to be thread-safe?

This often becomes an important follow-up.

---

## Eviction Policy

Question:

> Is the policy strictly LRU?

Possible alternatives:

```text
LRU
LFU
FIFO
TTL Based
```

---

## Read Behavior

Question:

> Does a successful get() update recency?

For LRU:

```text
Yes
```

---

# First Thought Process

Need:

```text
Fast lookup
```

Use:

```java
HashMap
```

Need:

```text
Fast eviction
```

Need:

```text
Fast reordering
```

HashMap alone cannot solve both.

---

# Why Not ArrayList?

Suppose:

```text
1 -> 2 -> 3 -> 4
```

Access:

```text
2
```

Need:

```text
Move 2 to front
```

ArrayList removal:

```text
O(N)
```

Too expensive.

---

# Why Not LinkedList Alone?

Lookup:

```text
Find key 1000
```

Requires:

```text
O(N)
```

search.

Violates requirement.

---

# Final Design

Combine:

```text
HashMap
+
Doubly Linked List
```

---

# LRU Cache Design (O(1) Get and Put)

## Problem Statement

Design a data structure that supports the following operations in **O(1)** average time:

```java
int get(int key)
```

* Return the value associated with the key if it exists.
* Mark the key as the **most recently used** because it was accessed.
* Return `-1` if the key does not exist.

```java
void put(int key, int value)
```

* Insert a new key-value pair.
* If the key already exists, update its value and mark it as most recently used.
* If inserting a new key exceeds the cache capacity, evict the **least recently used (LRU)** item.

---

# High-Level Design

To achieve **O(1)** `get()` and `put()`, combine:

1. **HashMap<Key, DLLNode>**
2. **Doubly Linked List**

The HashMap provides:

```
O(1) lookup of a node by key
```

The Doubly Linked List maintains the recency ordering:

```
front <-> Most Recently Used ... Least Recently Used <-> end
```

* `front.next` → Most Recently Used (MRU) node
* `end.prev` → Least Recently Used (LRU) node

---

# Why not use only a HashMap?

A HashMap can give O(1) access to a value by key, but it does not maintain the order of usage.

We need to know:

```
Which item was used least recently?
```

A HashMap alone cannot answer this efficiently.

---

# Why a Doubly Linked List and not a Singly Linked List?

When a key is accessed, it must be moved to the front.

Example:

```
front <-> A <-> B <-> C <-> end
                 ↑
             Access B
```

After access:

```
front <-> B <-> A <-> C <-> end
```

This requires removing B from the middle.

With a singly linked list:

```
remove(B)
```

requires finding the previous node first:

```
O(N)
```

With a doubly linked list, every node stores a pointer to its previous and next node:

```java
node.prev.next = node.next;
node.next.prev = node.prev;
```

Therefore removal is:

```
O(1)
```

---

# Why use Dummy Head and Tail Nodes?

Dummy nodes remove boundary edge cases.

Without dummy nodes, we would need special handling for:

* Removing the first element
* Removing the last element
* Inserting into an empty list

With:

```
front <-> end
```

as the initial state, every real node always has both a previous and next node.

This simplifies the code and avoids null checks.

---

# Java Implementation

```java
class LRUCache {

    DLLNode front;
    DLLNode end;
    Map<Integer, DLLNode> lruMap;
    int capacity;

    class DLLNode {
        int key;
        int value;
        DLLNode next;
        DLLNode prev;

        public DLLNode() {}

        public DLLNode(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }


    public LRUCache(int capacity) {
        this.capacity = capacity;

        lruMap = new HashMap<>();

        front = new DLLNode();
        end = new DLLNode();

        front.next = end;
        end.prev = front;
    }


    public int get(int key) {

        if (!lruMap.containsKey(key)) {
            return -1;
        }

        DLLNode node = lruMap.get(key);

        // Since this node was accessed,
        // it becomes the most recently used
        bringNodeToFront(node);

        return node.value;
    }


    public void put(int key, int value) {

        if (lruMap.containsKey(key)) {

            DLLNode node = lruMap.get(key);

            // Update existing value
            node.value = value;

            // Move it to the MRU position
            bringNodeToFront(node);

        } else {

            DLLNode node = new DLLNode(key, value);

            lruMap.put(key, node);

            // New nodes are always most recently used
            addNodeToFront(node);

            // Evict LRU if capacity exceeded
            if (lruMap.size() > capacity) {
                evictLRUCache();
            }
        }
    }


    // Add node immediately after front
    // front <-> node <-> oldFirst
    public void addNodeToFront(DLLNode node) {

        DLLNode temp = front.next;

        front.next = node;
        node.prev = front;

        node.next = temp;
        temp.prev = node;
    }


    // Move an existing node to MRU position
    public void bringNodeToFront(DLLNode node) {

        removeNode(node);

        addNodeToFront(node);
    }


    // Remove node from any position
    // A <-> node <-> B
    // becomes
    // A <-> B
    public void removeNode(DLLNode node) {

        node.prev.next = node.next;
        node.next.prev = node.prev;
    }


    // Remove least recently used node
    public void evictLRUCache() {

        DLLNode tailNode = end.prev;

        removeNode(tailNode);

        lruMap.remove(tailNode.key);
    }
}
```

---

# Complexity Analysis

## get(key)

HashMap lookup:

```
O(1)
```

Removing and moving the node in the doubly linked list:

```
O(1)
```

Total:

```
O(1)
```

---

## put(key, value)

HashMap insertion/update:

```
O(1)
```

Adding or removing a DLL node:

```
O(1)
```

Eviction:

```
O(1)
```

Total:

```
O(1)
```

---

## Space Complexity

The cache stores at most:

```
capacity
```

number of nodes.

Each cache entry has:

* One HashMap entry
* One doubly linked list node

Therefore:

```
Space = O(capacity)
```

---

# Thread Safety Discussion

## Is this implementation thread-safe?

No.

Both the HashMap and the Doubly Linked List are mutable data structures.

Example:

Two threads may execute:

```
Thread A:
get(1)
removeNode(node)

Thread B:
put(2)
evictLRUCache()
```

The linked list pointers may become inconsistent, causing corruption.

---

# Simple Solution: Synchronize Operations

Make `get()` and `put()` synchronized:

```java
public synchronized int get(int key) {
    // cache logic
}

public synchronized void put(int key, int value) {
    // cache logic
}
```

This makes the entire cache operation atomic.

---

# Better Approach: ReentrantLock

A `ReentrantLock` gives explicit control over locking.

Example:

```java
private final ReentrantLock lock = new ReentrantLock();

public int get(int key) {

    lock.lock();

    try {
        // Cache operations
    }
    finally {
        lock.unlock();
    }
}
```

---

# Why not just use ConcurrentHashMap?

A `ConcurrentHashMap` only protects the map.

The following operations must happen together atomically:

1. Lookup the node from the map.
2. Remove the node from its current position in the DLL.
3. Move the node to the front.
4. Possibly evict the tail node.

Since the HashMap and DLL represent a single shared state, protecting only the map can still leave the linked list corrupted.

---

# ReentrantLock vs synchronized

## synchronized

* Language-level locking mechanism.
* Simple and easy to use.
* Automatically releases the lock when the block exits.
* Less control over lock behavior.

## ReentrantLock

* Explicit lock API.
* Supports:

  * `tryLock()` to avoid waiting indefinitely.
  * Fairness policies to reduce thread starvation.
  * Interruptible lock acquisition.
  * More flexible lock management.

In high-concurrency systems, `ReentrantLock` often provides better control over thread coordination.

---

# Interview Explanation

A concise senior-level explanation:

> I used a combination of a HashMap and a doubly linked list. The HashMap gives O(1) access to cache nodes by key, while the doubly linked list maintains access order. The head of the list represents the most recently used entry and the tail represents the least recently used entry. Every access moves the node to the front, and when capacity is exceeded, I remove the tail node in O(1). I use a doubly linked list because arbitrary node removal requires a previous pointer, which a singly linked list cannot provide efficiently.

```
```


# Responsibilities

## HashMap

Provides:

```text
O(1)
```

lookup.

Stores:

```java
Map<Integer,Node>
```

---

## Doubly Linked List

Provides:

```text
O(1)
```

insert.

```text
O(1)
```

delete.

```text
O(1)
```

move-to-front.

---

# Mental Model

```text
Head

Most Recently Used

↓

Node
Node
Node

↓

Least Recently Used

Tail
```

---

# Why Doubly Linked List?

Suppose:

```text
A <-> B <-> C
```

Need to remove:

```text
B
```

Using DLL:

```java
B.prev.next = B.next;
B.next.prev = B.prev;
```

Complexity:

```text
O(1)
```

---

# Why Singly Linked List Fails

Need predecessor.

Without predecessor:

```text
O(N)
```

search required.

---

# Dummy Nodes

Common interview technique.

Create:

```text
Head Dummy
Tail Dummy
```

Structure:

```text
HEAD <-> A <-> B <-> C <-> TAIL
```

Benefits:

```text
No edge cases
No null checks
Cleaner code
```

---

# Node Structure

```java
class Node {

    int key;
    int value;

    Node prev;
    Node next;

    Node(int key,int value){
        this.key = key;
        this.value = value;
    }
}
```

---

# Core Operations

## Remove Node

```java
private void remove(Node node){

    node.prev.next =
        node.next;

    node.next.prev =
        node.prev;
}
```

Complexity:

```text
O(1)
```

---

## Insert After Head

```java
private void insert(Node node){

    node.next = head.next;
    node.prev = head;

    head.next.prev = node;
    head.next = node;
}
```

Complexity:

```text
O(1)
```

---

# get(key)

## Scenario 1

Key not present.

```java
return -1;
```

---

## Scenario 2

Key present.

Need:

```text
Move node to front
```

because it became:

```text
Most Recently Used
```

---

# put(key,value)

## Existing Key

Update:

```text
Value
```

Move:

```text
To front
```

---

## New Key

Insert:

```text
At front
```

---

## Capacity Exceeded

Evict:

```text
Tail.prev
```

Reason:

```text
Least Recently Used
```

---

# Full Design

```java
HashMap<Integer,Node> cache;

Node head;
Node tail;
```

Operations:

```text
Lookup -> HashMap
Ordering -> DLL
Eviction -> Tail
```

---

# Complexity

## get()

HashMap lookup:

```text
O(1)
```

DLL update:

```text
O(1)
```

Total:

```text
O(1)
```

---

## put()

HashMap:

```text
O(1)
```

DLL:

```text
O(1)
```

Eviction:

```text
O(1)
```

Total:

```text
O(1)
```

---

# Interview Sound Bite

> The challenge is simultaneously supporting O(1) lookup and O(1) eviction. A HashMap provides constant-time lookup, while a Doubly Linked List provides constant-time insertion, deletion, and recency updates. Together they satisfy all LRU requirements.

---

# Common Follow-Up Questions

## Why Not TreeMap?

Lookup:

```text
O(log N)
```

Not sufficient.

---

## Why Not PriorityQueue?

Removing arbitrary node:

```text
O(N)
```

Not sufficient.

---

## Why Not LinkedHashMap?

Java already provides:

```java
LinkedHashMap
```

which can implement LRU.

Interviewers usually want:

```text
Manual implementation
```

to test understanding.

---

# Thread Safety Discussion

Senior-level interviews often continue here.

Question:

> How would you make this cache thread-safe?

---

## Option 1

```java
synchronized
```

Simple.

But:

```text
Single global lock
```

May reduce throughput.

---

## Option 2

```java
ReentrantLock
```

Provides:

```text
More control
```

including:

```text
tryLock()
fair locking
```

---

## Option 3

```java
ConcurrentHashMap
```

Still insufficient by itself.

Reason:

```text
DLL updates
must also be synchronized.
```

---

# Production Considerations

Additional capabilities:

```text
TTL Expiration
Cache Metrics
Hit Ratio
Miss Ratio
Size Monitoring
Memory Limits
```

---

# Audible Analogy

Imagine:

```text
Most Recently Played Audiobooks
```

Need:

```text
Fast lookup
Fast eviction
Fixed memory footprint
```

Exactly the same problem.

---

# LRU Recognition Cheat Sheet

Keywords:

* Cache
* Recently Used
* Eviction
* Capacity
* O(1)
* Fixed Size

Think:

```text
HashMap + Doubly Linked List
```

before considering anything else.

---

# Interview Quick Memory Trick

Need:

```text
Fast Lookup?
```

Use:

```text
HashMap
```

Need:

```text
Fast Reordering?
```

Use:

```text
DLL
```

Need:

```text
Both?
```

Use:

```text
HashMap + DLL
```

This combination is the standard solution for LRU Cache.

# Chapter 9: Rate Limiter
> Given your extensive experience building high-throughput, low-latency client screening systems, we want to look at a core infrastructure challenge. Your stateless microservice integrates directly with an external compliance vendor API (like Bridger or RDC). This vendor enforces strict, contract-bound rate limits across our enterprise accounts. If we exceed these limits, they will throttle our traffic, causing cascading thread pool exhaustion and breaking our 200–500ms onboarding SLA.To prevent this, we need to implement a local, resilient guardrail within our microservice wrapper.
> Please design and implement a component that tracks and limits inbound traffic on a granular, per-user basis. The system must evaluate requests relative to a moving time horizon, ensuring that no individual customer can abuse our vendor bandwidth.Your component must expose a method allowRequest(String userId, long currentTime).It must return true if the request fits within the allowed budget, or false if the threshold is breached.The sliding window must be mathematically precise down to the millisecond
## Pattern Recognition

When you hear:

* Rate Limiter
* Requests Per Minute
* Requests Per Second
* API Throttling
* Abuse Prevention
* Request Quota
* Traffic Control
* Streaming Events

Immediately think:

```text
Sliding Window
```

Then ask:

```text
Is this:
1. Fixed Window?
2. Sliding Window?
3. Token Bucket?
4. Leaky Bucket?
```

---

# Real World Motivation

Suppose an API allows:

```text
100 requests per minute
```

Without rate limiting:

```text
One client can overwhelm the system.
```

Rate limiting protects:

* APIs
* Databases
* Downstream Services
* Shared Infrastructure

---

# Interview Clarification Questions

## Single Machine or Distributed?

Question:

> Is this rate limiter running on a single instance or multiple servers?

This dramatically changes the design.

---

## Fixed Window or Sliding Window?

Question:

> Do we need precise enforcement or approximate enforcement?

This determines:

```text
Sliding Window
vs
Fixed Window
```

---

## Request Volume

Question:

> What is the expected throughput?

Examples:

```text
100 requests/minute
100,000 requests/second
```

The architecture may differ significantly.

---

## User Identification

Question:

> Is the quota enforced per user, API key, IP address, or account?

Example:

```text
User A -> 100 req/min
User B -> 100 req/min
```

Need separate tracking.

---

# Sliding Window Rate Limiter

## Problem

Allow:

```text
100 requests
```

per:

```text
60 seconds
```

for each user.

---

# Core Idea

Maintain:

```java
Map<UserId, Deque<Timestamp>>
```

Each user owns:

```text
Queue of request timestamps.
```

---

# Visualization

Current Time:

```text
100
```

Window:

```text
40 → 100
```

User Requests:

```text
42
55
67
81
92
```

All requests remain valid.

---

# Expiring Old Requests

Suppose:

Current Time:

```text
120
```

Window:

```text
60 → 120
```

Now:

```text
42
55
```

are expired.

Remove them.

Remaining:

```text
67
81
92
```

---

# Why Deque?

Question:

Do old timestamps expire?

Answer:

```text
Yes
```

Need:

```text
Remove oldest timestamp quickly.
```

Therefore:

```java
Deque<Long>
```

---

# Why Not Stack?

Stack removes:

```text
Newest
```

items.

Rate limiter needs:

```text
Oldest
```

items removed first.

Therefore:

```text
Deque
```

is correct.

---

# Algorithm

For every request:

## Step 1

Retrieve user's deque.

```java
Deque<Long> timestamps =
    userRequests.get(userId);
```

---

## Step 2

Remove expired timestamps.

```java
while(!timestamps.isEmpty() &&
      timestamps.peekFirst()
        <= currentTime - windowSize){

    timestamps.pollFirst();
}
```

---

## Step 3

Check request count.

```java
if(timestamps.size() >= limit)
```

Reject request.

---

## Step 4

Otherwise:

```java
timestamps.offerLast(currentTime);
```

Accept request.

---

# Solution

```java
class StreamRateLimiter {

    private final int maxRequests;

    private final long windowSizeMillis;

    private final Map<String,
            Deque<Long>> requestMap =
                new HashMap<>();

    public StreamRateLimiter(
            int maxRequests,
            long windowSizeMillis){

        this.maxRequests =
            maxRequests;

        this.windowSizeMillis =
            windowSizeMillis;
    }

    public boolean allowRequest(
            String userId,
            long currentTime){

        requestMap.putIfAbsent(
            userId,
            new ArrayDeque<>()
        );

        Deque<Long> timestamps =
            requestMap.get(userId);

        while(!timestamps.isEmpty() &&
              timestamps.peekFirst()
                <= currentTime
                   - windowSizeMillis){

            timestamps.pollFirst();
        }

        if(timestamps.size()
                >= maxRequests){

            return false;
        }

        timestamps.offerLast(
            currentTime
        );

        return true;
    }
}
```

---

# Complexity Discussion

## Common Interview Concern

One request may trigger:

```text
Many removals
```

Example:

```text
100 expired timestamps
```

removed during a single request.

Candidate Concern:

```text
Is this O(N²)?
```

---

# Amortized Analysis

Key Observation:

Every timestamp:

```text
Inserted once
Removed once
```

No timestamp can be removed twice.

Therefore:

```text
Total Inserts = N
Total Removes = N
```

Across:

```text
N requests
```

Total operations:

```text
2N
```

---

# Interview Sound Bite

> A single request may remove M expired timestamps, resulting in a worst-case execution cost of O(M) where M = maxLimit. However, every single timestamp enters the deque exactly once and leaves exactly once. Therefore, the total work across N requests is bounded at O(N), resulting in a precise O(1) amortized cost per request
> The space complexity per unique user is strictly O(M), where M = maxLimit. Because our logic evaluates our budget constraint before allowing entries to persist, the size of each deque is securely bounded by our configuration threshold. Across the entire application, the total memory footprint scales linearly at (O(U *M)), where U is the number of active concurrent users tracking inside our system

---

# Complexity

## Time

Amortized:

```text
O(1)
```

per request.

---
# Rate Limiter - Sliding Window Log

## Clarifying Questions / Assumptions

1. **Single instance vs distributed system**

   * For simplicity, assume the rate limiter runs on a single server instance.
   * For multiple microservice instances, a distributed store like Redis would be required.

2. **Scale constraints**

   * What is the expected number of concurrent users?
   * What is the expected request throughput (requests per second)?

3. **Rate limiting strategy**

   * Are we implementing a fixed window, sliding window counter, or sliding window log?
   * Here, we are implementing a **sliding window log**.

4. **Request ordering**

   * Assume requests arrive in chronological order.

5. **Configuration**

   * `maxLimit`: Maximum number of requests allowed.
   * `windowSize`: Time window in milliseconds.

6. **Window boundary behavior**

   * Should the lower boundary be inclusive or exclusive?
   * Example:

     * Window = 3600 ms
     * Request 1 at `100 ms`
     * Request 2 at `3700 ms`
   * If using:

     ```java
     currentTime - oldestTimestamp >= windowSize
     ```

     then the lower boundary is exclusive:

     ```
     (currentTime - windowSize, currentTime]
     ```

7. **Approach**

   * Maintain a `Map<UserId, Deque<Timestamp>>`.
   * Each user's deque stores request timestamps within the active sliding window.
   * For every new request:

     1. Remove expired timestamps from the front of the deque.
     2. If the remaining count is greater than or equal to `maxLimit`, reject the request.
     3. Otherwise, append the new timestamp and allow the request.

8. **Quota scope**

   * Is the limit applied per user, per API, per client, or globally?
     
9. **Thread safety**

* `HashMap` and `ArrayDeque` are not thread-safe.
* For concurrent requests:
  * Use per-user locking.
  * Use concurrent data structures where appropriate such as concurrentHashMap.

# Audible Analogy

Suppose users stream audiobook previews.

Policy:

```text
Maximum 50 preview requests
per minute.
```

Each preview request:

```text
Timestamp
```

Rate limiter ensures:

```text
Fair usage.
```

---

# Fixed Window Limiter

Alternative approach.

Store:

```java
Map<UserId,Integer>
```

for current minute.

Example:

```text
12:00 → 12:01
```

Counter resets.

---

# Problem With Fixed Window

Example:

```text
59 requests at 12:00:59
59 requests at 12:01:01
```

Total:

```text
118 requests
```

within:

```text
2 seconds
```

Still allowed.

---

# Sliding Window Advantage

Requests are evaluated using:

```text
Actual rolling time window.
```

Produces smoother throttling.

---

# Token Bucket

Frequently asked follow-up.

---

## Idea

Maintain:

```text
Tokens
```

inside a bucket.

Requests consume tokens.

Tokens refill over time.

---

## Benefits

Supports:

```text
Burst traffic
```

while still enforcing:

```text
Average rate.
```

---

# Leaky Bucket

Alternative approach.

Requests enter:

```text
Queue
```

and leave at:

```text
Constant rate.
```

Produces smooth traffic.

---

# Distributed Rate Limiting

Senior-level follow-up.

Single JVM solution fails when:

```text
Multiple application servers exist.
```

Example:

```text
Server A
Server B
Server C
```

Each maintains:

```text
Own counters.
```

Problem:

```text
Limits become inconsistent.
```

---

# Common Production Solutions

Use:

```text
Redis
```

as centralized state.

Store:

```text
User Request History
```

inside Redis.

---

# Redis Advantages

Provides:

```text
Atomic Operations
```

and:

```text
Shared State
```

across servers.

---

# Production Follow-Ups

Common interviewer questions:

### How would you scale this?

Answer:

```text
Redis
Distributed Cache
Sharding
```

---

### How would you persist state?

Answer:

```text
Redis
DynamoDB
Distributed Storage
```

depending on requirements.

---

### How would you handle millions of users?

Answer:

```text
Partition user state
Evict inactive users
Use distributed storage
```

---

### Production Considerations

* If a user becomes inactive, their deque will eventually become empty after stale requests are removed.
* To prevent the map from growing indefinitely, remove the user entry once their deque is empty.

```java
if (timestamps.isEmpty()) {
    requestMap.remove(userId);
}
```

* This is primarily a **production concern** to avoid retaining inactive users in memory.

---

### Why `Deque` instead of `Queue`?

* Conceptually, this solution only requires a **FIFO Queue**:

  * New requests are added at the back.
  * Expired requests are removed from the front.
* Therefore, `Queue<Long>` would be sufficient.

```java
Queue<Long> userRequests = new ArrayDeque<>();
```

* However, `ArrayDeque` implements the `Deque` interface, and `Deque` provides more explicit methods:

```java
offerLast()
peekFirst()
pollFirst()
```

* These method names make it clear that requests are appended at the back and expired requests are removed from the front.
* There is **no algorithmic advantage** to using `Deque` over `Queue` in this problem—it's primarily an API/readability choice.



# Recognition Cheat Sheet

Keywords:

* Rate Limiter
* Requests Per Minute
* Quota
* API Throttling
* Abuse Prevention
* Traffic Shaping

Think:

```text
Sliding Window
```

before considering anything else.

---

# Interview Quick Memory Trick

Question:

Do events expire?

Answer:

```text
Yes
```

Use:

```text
Deque
```

Question:

Need rolling time boundaries?

Answer:

```text
Sliding Window
```

Question:

Need exact enforcement?

Answer:

```text
Sliding Window Rate Limiter
```

Question:

Need burst tolerance?

Answer:

```text
Token Bucket
```

This recognition pattern solves a large class of backend rate-limiting interview problems.

# Hit Counter - Sliding Window Design

## Problem Statement

Design a hit counter that tracks the number of hits received in the past 5 minutes (300 seconds).

Support two operations:

* `hit(timestamp)` → Record a hit at the given timestamp.
* `getHits(timestamp)` → Return the number of hits in the past 300 seconds.

Assumptions:

* Timestamps are received in chronological order.
* Multiple hits may occur at the same timestamp.
* Timestamp granularity is in seconds.

---

# Solution 1: Store Every Hit (No Aggregation)

## Intuition

Store every incoming request as an individual timestamp in a deque.

Example:

```
hit(100)
hit(100)
hit(100)
hit(101)
```

Deque:

```
[100, 100, 100, 101]
```

---

## Code

```java
class HitCounter {

    Deque<Integer> hits;
    int window = 300;

    public HitCounter() {
        hits = new ArrayDeque<>();
    }

    public void hit(int timestamp) {
        hits.offerLast(timestamp);
    }

    public int getHits(int timestamp) {

        // Remove timestamps outside the sliding window
        while (!hits.isEmpty() && hits.peekFirst() <= timestamp - window) {
            hits.pollFirst();
        }

        return hits.size();
    }
}
```

---

## Complexity

### hit()

```
O(1)
```

Append timestamp to the back of the deque.

---

### getHits()

Worst case:

```
O(N)
```

if many expired timestamps are removed.

Amortized:

```
O(1)
```

because each hit:

* Enters the deque exactly once.
* Leaves the deque exactly once.

---

### Space Complexity

```
O(N)
```

where:

```
N = total number of hits in the active 300-second window.
```

Example:

```
1,000,000 hits at timestamp 100
```

Deque:

```
[100, 100, 100, ... 1,000,000 times]
```

The space depends on the number of requests, not the window size.

---

# Optimization: Aggregate Hits by Timestamp

## Key Observation

Multiple requests arriving at the same second can be combined.

Instead of:

```
[100, 100, 100, 101]
```

store:

```
[100,3], [101,1]
```

where:

```
timestamp = 100
count = 3 hits
```

---

# Solution 2: Store Timestamp Buckets

## Data Structure

Use:

```java
Deque<int[]>
```

where:

```
int[0] = timestamp
int[1] = number of hits at that timestamp
```

Maintain a running `total` so `getHits()` does not need to iterate through all buckets.

---

## Code

```java
class HitCounter {

    Deque<int[]> hits;
    int total;
    int window = 300;

    public HitCounter() {
        hits = new ArrayDeque<>();
        total = 0;
    }

    public void hit(int timestamp) {

        // Same timestamp as latest bucket. Check peekLast not peekFirst
        if (!hits.isEmpty() && hits.peekLast()[0] == timestamp) {
            hits.peekLast()[1]++;
        } else {
            hits.offerLast(new int[]{timestamp, 1});
        }

        total++;
    }

    public int getHits(int timestamp) {

        // Remove expired timestamp buckets
        while (!hits.isEmpty() && hits.peekFirst()[0] <= timestamp - window) {
            total -= hits.pollFirst()[1];
        }

        return total;
    }
}
```

---

## Complexity

### hit()

```
O(1)
```

Either increment the latest bucket or create a new one.

---

### getHits()

Worst case:

```
O(U)
```

where:

```
U = number of expired timestamp buckets removed.
```

Amortized:

```
O(1)
```

because each timestamp bucket:

* Is inserted once.
* Is removed once.

---

### Space Complexity

```
O(U)
```

where:

```
U = number of unique timestamps in the active window.
```

Because timestamps have second granularity:

```
Maximum unique timestamps in 5 minutes = 300
```

Therefore:

```
O(U) = O(300) = O(1)
```

---

# Interview Takeaway

### Naive Sliding Window

```
Deque<Integer>
```

Stores every request.

Space:

```
O(number of hits)
```

Simple but does not scale with high traffic.

---

### Optimized Sliding Window with Aggregation

```
Deque<int[]>
```

Stores:

```
[timestamp, count]
```

Space:

```
O(number of unique timestamps)
```

For second-level timestamps and a fixed 5-minute window:

```
O(300) = O(1)
```

This is a common production optimization: **convert individual events into time-based aggregated buckets.**


# Chapter 10: Time-Series Analytics System

## Pattern Recognition

When you hear:

* Event Analytics
* Session Tracking
* User Activity
* Time-Based Queries
* Range Queries
* Metrics Dashboard
* Activity Aggregation
* Event Stream Analytics

Immediately think:

```text
Time-Series Data
```

Then ask:

```text
Do I need:
1. Point lookups?
2. Time-range lookups?
3. Aggregations?
4. Top-K analytics?
```

---

# Real World Motivation

Suppose Audible tracks:

```text
User Session Start
User Session End
Playback Events
Search Events
Download Events
```

Product teams want answers such as:

```text
How many unique users were active?
What were the most popular activities?
How many sessions occurred between two timestamps?
```

---

# Core Operations

System supports:

```java
logSession(...)
getUniqueUserCount(...)
getTopActivities(...)
```

---


# Interview Clarification Questions

## Query Volume

Question:

> How frequently are analytics queried?

Example:

```text
1 million writes/day
100 reads/day
```

Design differs from:

```text
1 million writes/day
1 million reads/day
```

---

## Data Ordering

Question:

> Do events arrive in chronological order?

If not:

```text
Sorting
Buffering
Watermarks
```

may be required.

---

## Retention Period

Question:

> How long should events be retained?

Examples:

```text
24 hours
30 days
1 year
```

This affects storage strategy.

---

## Exact vs Approximate Results

Question:

> Do we require exact analytics?

Approximate counting may allow:

```text
HyperLogLog
Bloom Filters
```

for very large datasets.

---

# Why TreeMap?

Key Requirement:

```text
Efficient Time Range Queries
```

TreeMap stores entries in:

```text
Sorted Order
```

by timestamp.

---

# Real-Time Security & Compliance Log Aggregator

## Problem Statement

In large-scale enterprise environments, audit log streaming and compliance monitoring require high-velocity aggregation.

Upstream event-driven ingestion pipelines (such as Kafka clusters) continuously emit telemetry regarding customer behavior, including:

- Authentication attempts
- Profile changes
- Compliance screening executions
- Operational activities

To turn this raw stream into actionable metrics for internal risk auditors, we need an in-memory aggregation engine that can capture high-throughput event logs and provide low-latency analytical queries over arbitrary time ranges.

---

## Requirements

Implement the following APIs:

```java
void logSession(int userId, int timestamp, String activityType)
```

Logs an activity performed by a user at the given timestamp.

Example:

```
User 101 -> LOGIN at 10:01 AM
User 102 -> PROFILE_UPDATE at 10:02 AM
```

---

```java
int getUniqueUserCount(int startTime, int endTime)
```

Returns the number of distinct users who performed at least one activity in the inclusive time range:

```
[startTime, endTime]
```

---

```java
List<String> getTopActivities(int startTime, int endTime, int k)
```

Returns the top `k` most frequently occurring activities in the given time range.

Sorting requirements:

1. Higher frequency comes first.
2. If two activities have the same frequency, alphabetical order acts as a tie-breaker.

Example:

```
LOGIN   -> 50
SEARCH  -> 50
UPDATE  -> 20
```

Result for `k = 2`:

```
LOGIN, SEARCH
```

because LOGIN comes before SEARCH alphabetically.

---

# Design Decision

## Why TreeMap?

All queries are time-range based.

A HashMap would require scanning all timestamps:

```
O(T)
```

where:

```
T = total number of timestamps stored
```

Instead, TreeMap maintains timestamps in sorted order and supports:

```java
subMap(startTime, true, endTime, true)
```

which allows efficient range retrieval.

Time to locate the time range:

```
O(log T)
```

---

## Data Model

```java
TreeMap<Integer, List<Event>> userEventLog;
```

Structure:

```
Timestamp
    |
    +---- List<Event>
                |
                +---- userId
                +---- activityType
```

Example:

```
1000:
    User 1 -> LOGIN
    User 2 -> SEARCH

1010:
    User 1 -> PURCHASE
```

---

# Java Implementation

```java
class AnalyticsAggregator {

    // Storage complexity:
    // O(E) where E is the total number of events.
    // More precisely O(E + T), but since T <= E, it simplifies to O(E).
    private TreeMap<Integer, List<Event>> userEventLog;

    public AnalyticsAggregator() {
        userEventLog = new TreeMap<>();
    }

    /*
     * Time Complexity: O(log T)
     *
     * TreeMap lookup/insertion takes O(log T)
     * Adding to ArrayList is O(1)
     */
    public void logSession(int userId, int timestamp, String activityType) {

        userEventLog
            .computeIfAbsent(timestamp, t -> new ArrayList<>())
            .add(new Event(userId, activityType));
    }

    /*
     * Time Complexity:
     * O(log T + E)
     *
     * T = total number of distinct timestamps
     * E = total number of events in the queried time window
     *
     * Extra Space:
     * O(U)
     *
     * U = number of unique users in the queried window
     */
    public int getUniqueUserCount(int startTime, int endTime) {

        Map<Integer, List<Event>> window =
            userEventLog.subMap(startTime, true, endTime, true);

        Set<Integer> users = new HashSet<>();

        for (List<Event> events : window.values()) {
            for (Event event : events) {
                users.add(event.userId);
            }
        }

        return users.size();
    }


    /*
     * Time Complexity:
     * O(log T + E + A log K)
     *
     * T = total number of timestamps
     * E = total number of events in the queried window
     * A = number of distinct activities
     *
     * Extra Space:
     * O(A + K)
     */
    public List<String> getTopActivities(
            int startTime,
            int endTime,
            int k) {

        Map<Integer, List<Event>> window =
            userEventLog.subMap(startTime, true, endTime, true);


        // Build frequency map
        Map<String, Integer> activityCount = new HashMap<>();

        for (List<Event> events : window.values()) {
            for (Event event : events) {
                activityCount.put(
                    event.activityType,
                    activityCount.getOrDefault(event.activityType, 0) + 1
                );
            }
        }


        /*
         * Min Heap:
         *
         * Lowest frequency remains on top.
         *
         * For equal frequency:
         * Alphabetically larger string is considered smaller
         * so that it gets removed first.
         *
         * Example:
         *
         * LOGIN: 5
         * SEARCH: 5
         *
         * Heap removes SEARCH first and keeps LOGIN.
         */
        PriorityQueue<String> pq = new PriorityQueue<>(
            (a, b) -> {
                int compare =
                    Integer.compare(
                        activityCount.get(a),
                        activityCount.get(b)
                    );

                if (compare == 0) {
                    return b.compareTo(a);
                }

                return compare;
            }
        );


        // Maintain only top K activities
        for (String activity : activityCount.keySet()) {

            pq.offer(activity);

            if (pq.size() > k) {
                pq.poll();
            }
        }


        /*
         * PriorityQueue does not iterate in sorted order.
         * Therefore, remove using poll().
         */
        List<String> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }


        /*
         * Current order:
         *
         * Lowest frequency -> Highest frequency
         *
         * Reverse to get:
         *
         * Highest frequency -> Lowest frequency
         */
        Collections.reverse(result);

        return result;
    }
}


class Event {

    int userId;
    String activityType;


    public Event(int userId, String activityType) {
        this.userId = userId;
        this.activityType = activityType;
    }
}
```

---

# Complexity Summary

| Operation | Time Complexity | Extra Space |
|-----------|----------------|-------------|
| `logSession()` | `O(log T)` | `O(1)` |
| `getUniqueUserCount()` | `O(log T + E)` | `O(U)` |
| `getTopActivities()` | `O(log T + E + A log K)` | `O(A + K)` |
| Overall Storage | - | `O(E)` |

Where:

- `T` = total number of distinct timestamps
- `E` = total number of events
- `U` = number of unique users in the query range
- `A` = number of unique activities in the query range

---

# Scalability Discussion

This design optimizes **locating the time range** but query performance is still proportional to the amount of data in that window.

For very high query volumes, we can introduce pre-aggregated time buckets.

Example:

```
Hour 10:
    LOGIN -> 50,000
    SEARCH -> 20,000
    PURCHASE -> 5,000

Hour 11:
    LOGIN -> 70,000
    SEARCH -> 30,000
```

Queries spanning multiple hours can combine precomputed aggregates rather than scanning every event.

Possible production optimizations:

- Hourly or minute-level aggregation buckets
- Secondary indexes by user ID
- Distributed storage using Kafka + RocksDB/Cassandra
- Approximate distinct counting using HyperLogLog for large-scale unique user analytics

---

# Interview Talking Points

A strong explanation would be:

> Since every query is time-range based, I chose a TreeMap to maintain timestamps in sorted order and efficiently retrieve a window using `subMap()`. Each timestamp stores a list of events. For unique users, I use a HashSet to deduplicate users in the window. For top activities, I build a frequency map and maintain a size-K min heap to efficiently keep only the most frequent activities. The design provides efficient range lookup while keeping the implementation simple. If query throughput becomes a bottleneck, I would introduce pre-aggregated time buckets or additional indexes depending on the access pattern.

# Data Structure

```java
TreeMap<Long,List<Event>>
```

Where:

```text
Key   = Timestamp
Value = Events at that timestamp
```

Example:

```text
1000 -> [Login, Search]
1500 -> [Play]
2000 -> [Pause]
```

---

# Session Model

```java
class Event {

    String userId;

    String activity;

    long timestamp;
}
```

---

# logSession()

## Goal

Store:

```text
User Activity
```

at:

```text
Timestamp
```

---

## Implementation

```java
events.computeIfAbsent(
    timestamp,
    k -> new ArrayList<>()
).add(event);
```

---

# Complexity

TreeMap insertion:

```text
O(log T)
```

Where:

```text
T = Number of timestamps
```

---

# Unique User Count

## Problem

Determine:

```text
Unique Users
```

between:

```text
startTime
endTime
```

---

# Key TreeMap Feature

Use:

```java
subMap(startTime, true,
       endTime, true)
```

This retrieves:

```text
Only relevant timestamps
```

---

# Algorithm

1. Query range.
2. Iterate events.
3. Add users to HashSet.
4. Return HashSet size.

---

# Implementation

```java
Set<String> uniqueUsers =
    new HashSet<>();

for(List<Event> eventList :
        events.subMap(
            start,
            true,
            end,
            true
        ).values()){

    for(Event e : eventList){
        uniqueUsers.add(
            e.userId
        );
    }
}

return uniqueUsers.size();
```

---

# Complexity

TreeMap range lookup:

```text
O(log T)
```

Event scan:

```text
O(E)
```

Total:

```text
O(log T + E)
```

---

# Top Activities

## Problem

Return:

```text
Top K Activities
```

within:

```text
Time Range
```

---

# Approach

Step 1:

Collect events from:

```java
subMap(...)
```

---

## Step 2

Build frequency map.

```java
Map<String,Integer>
```

---

## Step 3

Use:

```java
PriorityQueue
```

for Top K.

---

# Complexity

Range Query:

```text
O(log T)
```

Frequency Counting:

```text
O(E)
```

Heap:

```text
O(A log K)
```

Where:

```text
A = Number of Activities
```

Total:

```text
O(log T + E + A log K)
```

---

# Why Not HashMap?

HashMap provides:

```text
O(1)
```

lookups.

But:

```text
No ordering
```

for timestamps.

Range queries become:

```text
O(N)
```

full scans.

TreeMap provides:

```text
Sorted keys
```

and:

```java
subMap(...)
```

which is the major advantage.

---

# Audible Analogy

Imagine events:

```text
Play
Pause
Resume
Download
Search
```

Questions:

```text
Most common action?
Most active users?
Activity volume during peak hours?
```

All become:

```text
Time-range analytics queries.
```

---

# Scaling Problem

Suppose:

```text
100 million events/day
```

Scanning raw events becomes expensive.

---

# Pre-Aggregation Strategy

Instead of storing only raw events:

Maintain:

```text
Minute Buckets
Hour Buckets
Day Buckets
```

Example:

```text
12:01 -> 3,000 events
12:02 -> 3,400 events
12:03 -> 2,900 events
```

Now many queries become:

```text
O(Number of Buckets)
```

instead of:

```text
O(Number of Events)
```

---

# Interview Sound Bite

> Raw event scans work well initially, but at scale I would introduce pre-aggregation at the minute or hourly level to reduce query latency and avoid repeatedly scanning historical events.

---

# Streaming Analytics Follow-Up

Question:

> What if events arrive continuously?

Approach:

```text
Kafka
Stream Processor
Aggregation Layer
```

Examples:

```text
Kafka Streams
Flink
Spark Streaming
```

---

# Memory Growth Concern

Question:

> What happens if events grow forever?

Answer:

Introduce:

```text
Retention Policies
Archival
TTL
Cold Storage
```

Example:

```text
Keep 30 days hot
Archive older data
```

---

# Production Considerations

Additional capabilities:

```text
Data Retention
Compression
Archival
Pre-Aggregation
Caching
Fault Tolerance
Replication
```

---

# Interview Follow-Up Questions

### How would you support billions of events?

Answer:

```text
Partition by Time
Partition by User
Pre-Aggregation
Distributed Storage
```

---

### How would you improve Top K performance?

Answer:

```text
Incremental Aggregation
Precomputed Rankings
Caching
```

---

### How would you support real-time dashboards?

Answer:

```text
Kafka
Streaming Aggregation
Materialized Views
```

---

# Recognition Cheat Sheet

Keywords:

* Event Analytics
* Session Analytics
* Time Series
* Metrics
* Dashboard
* Activity Tracking
* User Behavior

Think:

```text
TreeMap
```

when:

```text
Time Range Queries
```

are important.

---

# Interview Quick Memory Trick

Question:

Need:

```text
Ordered timestamps?
```

Use:

```text
TreeMap
```

Question:

Need:

```text
Range queries?
```

Think:

```java
subMap(...)
```

Question:

Need:

```text
Top K analytics?
```

Add:

```text
Heap
```

Question:

Need:

```text
Real-time analytics?
```

Think:

```text
Kafka + Streaming Aggregation
```

This combination solves a large class of analytics and telemetry interview problems.

# Chapter 11: Java Collections & Interview Cheat Sheet

## Why This Chapter Matters

Many interview failures are not algorithm failures.

They are:

```text
Wrong Data Structure Selection
```

Interviewers frequently ask:

> Why HashMap?

> Why TreeMap?

> Why Heap?

> Why Deque?

Being able to answer those questions clearly is often more important than the code itself.

---

# Collection Selection Framework

Before coding ask:

## Question 1

Do I need:

```text
Fast Lookup?
```

Use:

```java
HashMap
HashSet
```

---

## Question 2

Do I need:

```text
Sorted Order?
```

Use:

```java
TreeMap
TreeSet
```

---

## Question 3

Do I need:

```text
Top K?
```

Use:

```java
PriorityQueue
```

---

## Question 4

Do elements expire?

Examples:

```text
Sliding Window
Rate Limiter
Moving Average
```

Use:

```java
Deque
```

---

## Question 5

Need:

```text
Nearest Greater
Nearest Smaller
```

Use:

```java
Stack
```

or

```java
Deque used as Stack
```

---

# HashMap

## Characteristics

```java
Map<K,V> map =
    new HashMap<>();
```

Provides:

```text
O(1) lookup
O(1) insert
O(1) delete
```

Average case.

---

## Use Cases

Examples:

```text
Frequency Counting
Caching
Lookup Tables
Deduplication
```

---

## Interview Sound Bite

> I need constant-time lookup, so HashMap is the most appropriate choice.

---

# HashSet

## Characteristics

```java
Set<Integer> set =
    new HashSet<>();
```

Built on top of:

```text
HashMap
```

---

## Complexity

```text
Contains = O(1)
Add      = O(1)
Remove   = O(1)
```

---

## Use Cases

Examples:

```text
Deduplication
Visited Nodes
Unique Users
```

---

# TreeMap

## Characteristics

```java
TreeMap<K,V>
```

Backed by:

```text
Red-Black Tree
```

---

## Complexity

```text
Put    = O(log N)
Get    = O(log N)
Remove = O(log N)
```

---

## Advantage

Maintains:

```text
Sorted Order
```

---

## Most Important Feature

```java
subMap(...)
```

for:

```text
Range Queries
```

---

## Use Cases

Examples:

```text
Time-Series Analytics
Range Queries
Leaderboards
Ordered Data
```

---

# TreeSet

## Characteristics

```java
TreeSet<Integer>
```

Stores:

```text
Unique Values
```

in:

```text
Sorted Order
```

---

## Complexity

```text
Add      O(log N)
Contains O(log N)
Remove   O(log N)
```

---

## Use Cases

Examples:

```text
Nearest Value
Ordered Unique Elements
Range Queries
```

---

# PriorityQueue (Heap)

## Characteristics

Java:

```java
PriorityQueue<Integer> minHeap =
    new PriorityQueue<>();
```

Default:

```text
Min Heap
```

---

## Complexity

```text
Offer O(log N)
Poll  O(log N)
Peek  O(1)
```

---

## Use Cases

Examples:

```text
Top K
Meeting Rooms
Task Scheduler
K Closest Points
```

---

## Interview Sound Bite

> I only need the best K candidates, not the entire dataset sorted, so a heap is more efficient than sorting.

---

# Deque

## Characteristics

```java
Deque<Integer> dq =
    new ArrayDeque<>();
```

Supports:

```text
Front Operations
Back Operations
```

---

## Complexity

```text
Add First    O(1)
Add Last     O(1)
Poll First   O(1)
Poll Last    O(1)
Peek First   O(1)
Peek Last    O(1)
```

---

## Use Cases

Examples:

```text
Sliding Window Maximum
Rate Limiter
Moving Average
```

---

## Interview Sound Bite

> Old elements expire from the front while new elements arrive at the back, making a deque the natural choice.

---

# Stack

## Characteristics

```java
Stack<Integer>
```

LIFO:

```text
Last In First Out
```

---

## Complexity

```text
Push O(1)
Pop  O(1)
Peek O(1)
```

---

## Common Interview Problems

Examples:

```text
Daily Temperatures
Next Greater Element
Stock Span
```

---

## Modern Java Note

Prefer:

```java
Deque<Integer> stack =
    new ArrayDeque<>();
```

instead of:

```java
Stack<Integer>
```

because:

```text
Stack is a legacy synchronized class.
```

---

# LinkedList

## Characteristics

```java
LinkedList<Integer>
```

Supports:

```text
Front Operations
Back Operations
```

---

## Limitation

Lookup:

```text
O(N)
```

---

## Use Cases

Examples:

```text
Queue
Deque
```

Rarely used directly in interview solutions.

---

# ArrayList

## Characteristics

```java
ArrayList<Integer>
```

---

## Complexity

```text
Get(index) O(1)
Append     O(1) amortized
Insert Mid O(N)
Delete Mid O(N)
```

---

## Use Cases

Examples:

```text
Dynamic Arrays
Result Collections
```

---

# Collection Comparison Table

| Structure | Lookup     | Insert      | Delete   | Sorted  |
| --------- | ---------- | ----------- | -------- | ------- |
| HashMap   | O(1)       | O(1)        | O(1)     | No      |
| HashSet   | O(1)       | O(1)        | O(1)     | No      |
| TreeMap   | O(log N)   | O(log N)    | O(log N) | Yes     |
| TreeSet   | O(log N)   | O(log N)    | O(log N) | Yes     |
| Heap      | O(N)       | O(log N)    | O(log N) | Partial |
| Deque     | O(N)       | O(1)        | O(1)     | No      |
| ArrayList | O(1) Index | O(1) Append | O(N) Mid | No      |

---

# Deep Dive: Understanding Deque

A lot of Java developers get confused by:

```java
Stack
Queue
LinkedList
Deque
ArrayDeque
```

because they are often taught as completely separate data structures.

In reality, they are closely related.

---

# What is a Deque?

Deque stands for:

```text
Double Ended Queue
```

Meaning:

```text
Front <-------> Back
```

You can insert and remove elements from **both ends**.

---

# Normal Queue

A queue supports:

```text
Enqueue at Back
Dequeue at Front
```

Example:

```text
1 2 3
^     ^
Front Back
```

Operations:

```java
offer()
poll()
```

Behavior:

```text
FIFO
First In First Out
```

---

# Stack

A stack supports:

```text
Push
Pop
```

from the same end.

Example:

```java
push(1);
push(2);
push(3);
```

Visual:

```text
3
2
1
```

Behavior:

```text
LIFO
Last In First Out
```

---

# Deque Does Both

```java
Deque<Integer> dq = new ArrayDeque<>();
```

---

## Add At Front

```java
dq.offerFirst(10);
```

Result:

```text
10
```

---

## Add At Back

```java
dq.offerLast(20);
```

Result:

```text
10 20
```

---

## Add At Front Again

```java
dq.offerFirst(5);
```

Result:

```text
5 10 20
```

---

## Remove From Front

```java
dq.pollFirst();
```

Removes:

```text
5
```

Remaining:

```text
10 20
```

---

## Remove From Back

```java
dq.pollLast();
```

Removes:

```text
20
```

Remaining:

```text
10
```

---

# Why Deque Is Perfect For Sliding Windows

Consider a rate limiter.

Window:

```text
3600 seconds
```

User events:

```text
100
200
300
4000
```

Store:

```java
Deque<Integer> timestamps;
```

Initially:

```text
100
200
300
```

New event arrives:

```text
4000
```

Window Start:

```text
4000 - 3600 = 400
```

Everything before:

```text
400
```

must be removed.

Because timestamps arrive in chronological order:

```text
100 200 300
^
Oldest
```

The oldest timestamp is always at the front.

Remove expired events:

```java
while(dq.peekFirst() < 400){
    dq.pollFirst();
}
```

Removes:

```text
100
200
300
```

Now:

```text
(empty)
```

Add new timestamp:

```java
dq.offerLast(4000);
```

Result:

```text
4000
```

Done.

---

# Why Not TreeSet?

A common instinct is:

```java
TreeSet<Integer>
```

because:

```text
Need timestamps
Need ordering
```

Reasonable thought.

However:

```text
100
200
300
4000
5000
6000
```

already arrive in sorted order.

You do not need:

```java
TreeSet
```

to sort something that is already sorted.

A deque preserves insertion order naturally.

---

# Sliding Window Mental Model

Always picture:

```text
|---------------------|
       Window
```

moving through time.

Events:

```text
100
200
300
400
500
600
```

Current timestamp:

```text
700
```

Window:

```text
400 -> 700
```

Everything before:

```text
400
```

gets removed from the front.

New events are added at the back.

This is exactly what a deque is optimized for.

---

# The Four Methods You Actually Need

For most interviews memorize only:

## Add To Back

```java
offerLast(x)
```

---

## Remove From Front

```java
pollFirst()
```

---

## Peek Front

```java
peekFirst()
```

---

## Current Size

```java
size()
```

These four operations are enough to solve:

```text
Sliding Window
Rate Limiter
Moving Average
Recent Events
Hit Counter
```

---

# Rate Limiter Example

```java
Map<Integer, Deque<Integer>> userEvents;
```

When an event arrives:

```java
Deque<Integer> q =
    userEvents.get(userId);
```

Remove expired events:

```java
while(!q.isEmpty() &&
      q.peekFirst()
        <= timestamp-windowDuration){

    q.pollFirst();
}
```

Check limit:

```java
if(q.size() >= maxAllowed)
    return false;
```

Accept request:

```java
q.offerLast(timestamp);

return true;
```

---

# The Most Important Mental Model

Do not think of a deque as:

```text
Another Java Collection
```

Think of it as:

```text
Oldest --------------> Newest
```

Once you visualize data flowing from:

```text
Front -> Back
```

sliding-window problems become much easier to recognize and solve.


# Recognition Cheat Sheet

## Need

```text
Frequency Count
```

Use:

```java
HashMap
```

---

## Need

```text
Unique Values
```

Use:

```java
HashSet
```

---

## Need

```text
Sorted Keys
```

Use:

```java
TreeMap
```

---

## Need

```text
Range Query
```

Use:

```java
TreeMap
```

---

## Need

```text
Top K
```

Use:

```java
PriorityQueue
```

---

## Need

```text
Sliding Window
```

Use:

```java
Deque
```

---

## Need

```text
Next Greater
```

Use:

```java
Monotonic Stack
```

---

## Need

```text
O(1) Cache
```

Use:

```java
HashMap + Doubly Linked List
```

---

# Audible Interview Quick Reference

| Problem Type           | Data Structure |
| ---------------------- | -------------- |
| Listener Concurrency   | Heap           |
| Playback Session Merge | Intervals      |
| Top Audiobooks         | Heap           |
| User Activity Counts   | HashMap        |
| Time Range Analytics   | TreeMap        |
| Sliding Metrics        | Deque          |
| Request Throttling     | Deque          |
| Service Dependencies   | Graph          |
| Cache Design           | HashMap + DLL  |

---

# Final Interview Memory Trick

Before writing code ask:

```text
What operation am I optimizing?
```

Examples:

```text
Lookup?
→ HashMap

Ordering?
→ TreeMap

Top K?
→ Heap

Expiration?
→ Deque

Nearest Greater?
→ Stack

Dependencies?
→ Graph

Time Range?
→ TreeMap
```

Most interview problems become significantly easier once the correct data structure is identified.


# Chapter 12: Pattern Recognition Master Guide

## Why Most Candidates Struggle

Most interview failures happen before coding.

The issue is usually:

```text id="p7aw4x"
Pattern Recognition
```

Strong candidates often identify the pattern within:

```text id="udkng5"
30 seconds
```

and then apply a known template.

This chapter is designed to help answer:

> What data structure or algorithm should I think about first?

---

# Recognition Flowchart

## Question 1

Am I searching for:

```text id="x49vyn"
Target Value?
```

Think:

```text id="mvq0ov"
Binary Search
```

---

## Question 2

Am I looking for:

```text id="20kmpz"
Longest
Shortest
Continuous
Contiguous
```

Think:

```text id="5o4sfx"
Sliding Window
```

---

## Question 3

Am I looking for:

```text id="l6shb4"
Next Greater
Next Smaller
Nearest Greater
Nearest Smaller
```

Think:

```text id="5m3o0n"
Monotonic Stack
```

---

## Question 4

Do I need:

```text id="3pr88h"
Top K
Ranking
Priority
```

Think:

```text id="8p0qdt"
Heap
```

---

## Question 5

Do elements:

```text id="e4yr18"
Expire?
```

Think:

```text id="h6vavj"
Deque
```

---

## Question 6

Do I have:

```text id="w7cnmc"
Dependencies?
Prerequisites?
Build Order?
```

Think:

```text id="3y9f4z"
Graph + Topological Sort
```

---

## Question 7

Do I have:

```text id="g1j37n"
Time Ranges?
Ordered Timestamps?
```

Think:

```text id="0jz4ff"
TreeMap
```

---

## Question 8

Do I have:

```text id="uzyd74"
Intervals?
Overlaps?
Concurrency?
```

Think:

```text id="sy9g2r"
Sort + Intervals
```

or:

```text id="0z4mtr"
Min Heap
```

---

# Sliding Window Recognition

## Keywords

* Longest
* Shortest
* Continuous
* Contiguous
* At Most K
* Exactly K
* Subarray
* Substring
* Rolling Window

---

## Examples

```text id="p50m8o"
Longest Substring K Distinct
Minimum Window Substring
Moving Average
Rate Limiter
```

---

## Mental Model

```text id="qeh7f7"
Expand Right
Shrink Left
Maintain Valid Window
```

---

## Data Structures

Usually:

```text id="a1y5pt"
HashMap
HashSet
Array Counter
```

Sometimes:

```text id="f7v3ho"
Deque
```

---

# Monotonic Stack Recognition

## Keywords

* Next Greater
* Next Smaller
* Previous Greater
* Previous Smaller
* Nearest
* Closest

---

## Examples

```text id="tqm7q2"
Daily Temperatures
Stock Span
Next Greater Element
Largest Rectangle Histogram
```

---

## Mental Model

Question:

> For this element, what is the first valid thing to my left or right?

Think:

```text id="8d7n7g"
Monotonic Stack
```

---
# Monotonic Stack Cheat Sheet

## Step 1: Ask the Question

### Am I looking for a GREATER element or a SMALLER element?

* Looking for **Greater** → Use a **Monotonically Decreasing Stack**

```
Bottom
  100
   80
   60
Top
```

Meaning:

```
100 > 80 > 60
```

* Bigger values are at the bottom.
* Smaller values are near the top.
* The top contains the nearest smaller candidate.

When the current element is larger:

```
Current = 75

75 >= 60 → Pop
75 < 80  → Stop
```

The smaller elements are useless because the current element is a better candidate.

---

### Looking for SMALLER → Use a Monotonically Increasing Stack

```
Bottom
  60
  80
 100
Top
```

Meaning:

```
60 < 80 < 100
```

* Smaller values are at the bottom.
* Larger values are near the top.

---

# Step 2: Which Direction Do I Traverse?

## Looking for the NEXT element (future/right side)

Process from:

```
Right → Left
```

Because the future elements must already be in the stack.

Examples:

* Daily Temperatures
* Next Greater Element
* Next Smaller Element

---

## Looking for the PREVIOUS element (past/left side)

Process from:

```
Left → Right
```

Because previous elements have already been seen.

Examples:

* Stock Span (Previous Greater)
* Previous Greater Element
* Previous Smaller Element

---

# The Complete Pattern Table

| What am I looking for? | Stack Type           | Traversal    |
| ---------------------- | -------------------- | ------------ |
| Next Greater           | Monotonic Decreasing | Right → Left |
| Previous Greater       | Monotonic Decreasing | Left → Right |
| Next Smaller           | Monotonic Increasing | Right → Left |
| Previous Smaller       | Monotonic Increasing | Left → Right |

---

# Quick Memory Rule

```
Greater → Decreasing Stack
Smaller → Increasing Stack

Next (future) → Process Right to Left
Previous (past) → Process Left to Right
```

---

# Why This Works

A monotonic stack keeps only useful candidates.

For a "greater" problem:

```
Stack:
100
80
60 (top)
```

Current = 75:

```
75 >= 60 → Pop (60 can never be the answer again)
75 < 80  → Stop
```

New Stack:

```
100
80
75 (top)
```

The stack remains monotonically decreasing.

---

# Common Confusion

### Monotonic Decreasing Stack

The order is from **Bottom → Top**:

```
100
80
60
```

NOT:

```
60
80
100
```

So:

* Bottom = largest
* Top = smallest

---

### Monotonic Decreasing Deque (Sliding Window Maximum)

The order is from **Front → Back**:

```
Front              Back
100    80    60
```

So:

* Front = largest (current maximum)
* Back = smallest

The direction is different because a deque is read from front to back, while a stack is read from bottom to top.


## Common Mistake

Using:

```text id="r0n8eo"
Deque
```

when no elements expire.

---


# Monotonic Queue Recognition

## Keywords

* Sliding Window Maximum
* Rolling Maximum
* Rolling Minimum

---

## Examples

```text id="lwm8ys"
Sliding Window Maximum
Rate Limiter
Moving Average
```

---

## Mental Model

Question:

> Do old elements leave the system?

If:

```text id="wytnvk"
Yes
```

Think:

```text id="24vzxy"
Deque
```

---

# Binary Search Recognition

## Keywords

* Minimum
* Maximum
* Smallest
* Largest
* Capacity
* Speed
* Threshold

---

## Examples

```text id="u9m3dr"
Koko Eating Bananas
Capacity To Ship Packages
Minimum Days
```

---

# Question

Can I know the answer immediately when I see:

```text id="bxy8qm"
mid
```

?

If:

```text id="m1d8oc"
Yes
```

Use:

```java id="0f1q1q"
while(lo <= hi)
```

---

If:

```text id="6o7vg8"
No
```

and you're searching for a boundary:

Use:

```java id="j8nyg5"
while(lo < hi)
```

---

# Heap Recognition

## Keywords

* Top K
* Most Frequent
* Least Frequent
* K Closest
* Ranking
* Priority

---

## Examples

```text id="a4vhmr"
Top K Frequent
K Closest Points
Meeting Rooms
Task Scheduler
```

---

## Question

Do I need:

```text id="7g8mjj"
Everything sorted?
```

If:

```text id="2vb5aq"
No
```

Think:

```text id="c5dn0s"
Heap
```

---

# Graph Recognition

## Keywords

* Dependency
* Prerequisite
* Build Order
* Course Schedule
* Boot Sequence

---

## Examples

```text id="3e0f0x"
Course Schedule
Topological Sort
Microservice Startup Order
```

---

## Question

Can one task happen before another?

If:

```text id="g2pk7u"
No
```

Think:

```text id="15f3zt"
Directed Graph
```

---

# TreeMap Recognition

## Keywords

* Time Series
* Ordered Data
* Range Query
* Time Window

---

## Examples

```text id="2j6tmi"
Analytics Dashboard
User Activity Tracking
Metrics Aggregation
```

---

## Question

Need:

```text id="i7gf5s"
Ordered Keys?
```

Think:

```text id="vop6q7"
TreeMap
```

---

# Interval Recognition

## Keywords

* Overlap
* Merge
* Scheduling
* Concurrent Sessions
* Meeting Rooms

---

## Examples

```text id="0i5myc"
Merge Intervals
Meeting Rooms II
Listener Concurrency
```

---

## Question

Need:

```text id="yllyqg"
Merged Ranges?
```

Use:

```text id="qav2lu"
Sort + Scan
```

Need:

```text id="l94f42"
Concurrency Count?
```

Use:

```text id="0pwq4v"
Min Heap
```

---

# LRU Cache Recognition

## Keywords

* Cache
* Recently Used
* Eviction
* O(1)

---

## Think

```text id="w6oq8x"
HashMap + Doubly Linked List
```

Immediately.

---

# Rate Limiter Recognition

## Keywords

* Requests Per Minute
* API Throttling
* Quotas
* Traffic Control

---

## Think

```text id="7rlprm"
Sliding Window
```

and:

```text id="km8tzd"
Deque
```

---

# Time-Series Analytics Recognition

## Keywords

* Events
* Metrics
* Dashboards
* Time Ranges

---

## Think

```text id="kpcv6e"
TreeMap
```

plus:

```text id="jlwmhf"
Heap
```

for Top-K queries.

---

# Audible-Specific Pattern Mapping

| Audible Scenario            | Pattern                |
| --------------------------- | ---------------------- |
| Listener Concurrency        | Intervals + Heap       |
| Playback Session Merge      | Intervals              |
| Request Throttling          | Sliding Window + Deque |
| Most Popular Audiobooks     | Heap                   |
| Metadata Dependency Startup | Graph                  |
| Telemetry Analytics         | TreeMap                |
| Session Analytics           | Sliding Window         |
| Cache Design                | HashMap + DLL          |

---

# Senior Engineer Interview Mindset

Before coding ask:

### What operation am I optimizing?

Examples:

```text id="tbw4lw"
Lookup?
Ordering?
Ranking?
Expiration?
Dependency?
Range Query?
```

The answer usually determines:

```text id="j5pylm"
Data Structure
```

which determines:

```text id="h2h2hj"
Algorithm
```

---

# Final Interview Cheat Sheet

| Keywords                        | Think First     |
| ------------------------------- | --------------- |
| Longest / Shortest / Contiguous | Sliding Window  |
| Next Greater / Smaller          | Monotonic Stack |
| Rolling Max / Min               | Monotonic Queue |
| Top K                           | Heap            |
| Minimum / Maximum Answer        | Binary Search   |
| Dependency / Build Order        | Graph           |
| Range Query                     | TreeMap         |
| Overlap / Concurrency           | Intervals       |
| Cache / Eviction                | HashMap + DLL   |
| Rate Limiter                    | Deque           |
| Time-Series Analytics           | TreeMap         |

---

# Ultimate Memory Trick

Don't ask:

```text id="v4n5k8"
What algorithm should I use?
```

Ask:

```text id="s6g2lw"
What operation am I optimizing?
```

The operation usually reveals:

```text id="qv8cjk"
The data structure
```

and the data structure usually reveals:

```text id="a7w2nt"
The algorithm.
```

That single habit dramatically improves interview performance.

# Chapter 13: Trees & Binary Search Trees

## Pattern Recognition

When you hear:

```text
Parent
Child
Ancestor
Descendant
Root
Subtree
Depth
Height
Path
```

Think:

```text
Tree
```

---

# Core Tree Traversals

## Preorder Traversal

Order:

```text
Root
Left
Right
```

LeetCode:

```text
144. Binary Tree Preorder Traversal
```

Use Cases:

```text
Serialization
Tree Copy
```

---

## Inorder Traversal

Order:

```text
Left
Root
Right
```

LeetCode:

```text
94. Binary Tree Inorder Traversal
```

Key Insight:

For BST:

```text
Inorder Traversal Produces Sorted Order
```

---

## Postorder Traversal

Order:

```text
Left
Right
Root
```

LeetCode:

```text
145. Binary Tree Postorder Traversal
```

Use Cases:

```text
Delete Tree
Dependency Processing
```

---

## Level Order Traversal

Order:

```text
Level By Level
```

Think:

```text
BFS + Queue
```

LeetCode:

```text
102. Binary Tree Level Order Traversal
```

Complexity:

```text
Time  O(N)
Space O(N)
```

---

# BFS Must-Know Problems

## BFS Pattern Recognition

Keywords:

```text
Level Order
Minimum Steps
Shortest Path
Nearest
Spread
Infection
Grid
Matrix
```

Think:

```text
Breadth First Search
```

---

# 102. Binary Tree Level Order Traversal

Pattern:

```text
Tree BFS
```

Recognition:

```text
Level By Level Processing
```

Complexity:

```text
Time  O(N)
Space O(N)
```

Interview Sound Bite:

> BFS naturally processes nodes level by level, making it ideal for level-order traversal problems.

---

# 200. Number of Islands

Pattern:

```text
Grid Traversal
Connected Components
```

Recognition:

```text
Grid
Island
Connected Region
```

Think:

```text
BFS or DFS
```

Core Idea:

```text
Every unvisited land cell starts a new island traversal.
```

Complexity:

```text
Time  O(M×N)
Space O(M×N)
```

Interview Sound Bite:

> Every unvisited land cell represents a new connected component. BFS explores the entire island before moving on.

---

# 994. Rotten Oranges

Pattern:

```text
Multi-Source BFS
```

Recognition:

```text
Spread
Infection
Minimum Time
Propagation
```

Core Idea:

Start BFS from:

```text
All Rotten Oranges
```

Each BFS level:

```text
Represents One Minute
```

Complexity:

```text
Time  O(M×N)
Space O(M×N)
```

Interview Sound Bite:

> Since all rotten oranges spread simultaneously, they all enter the queue initially. Each BFS layer corresponds to one minute of spread. We need to add position of all rotting oranges in a queue and start BFS from those positions. The first time an orange is reached, the earliest it can rot. Every level corresponds to unit time of rot thats why
we need level markers in this problem.

Time  O(M*N)
Space O(M*N)
```

---

# 1091. Shortest Path in Binary Matrix

Pattern:

```text
Shortest Path BFS
```

Recognition:

```text
Shortest Path
Minimum Steps
Grid
```

Core Idea:

For an:

```text
Unweighted Graph
```

BFS naturally discovers nodes in shortest-distance order.

Complexity:

```text
Time  O(M×N)
Space O(M×N)
```

Interview Sound Bite:

> Because every move has equal cost, BFS guarantees the shortest path.

---

# 127. Word Ladder

Pattern:

```text
State Space BFS
```

Recognition:

```text
Minimum Transformations
Shortest Sequence
```

Core Idea:

Treat:

```text
Word = Node
Transformation = Edge
```

Use BFS to find:

```text
Minimum Number of Transformations
```

Complexity:

```text
Time O(N × WordLength × 26)
```

depends on implementation.

Interview Sound Bite:

> Word Ladder is a shortest-path problem where words are graph nodes and valid transformations are graph edges.

---

# BFS Master Set

These five problems cover the majority of BFS interview patterns:

```text
102. Binary Tree Level Order Traversal
200. Number of Islands
994. Rotten Oranges
286. Walls and Gates
1091. Shortest Path in Binary Matrix
127. Word Ladder
```

Covered Concepts:

```text
Tree BFS
Grid BFS
Connected Components
Multi-Source BFS
Shortest Path BFS
State Space BFS
```

A note on Walls & Gates
```text
Walls and Gates is a Multi-Source BFS problem.

Instead of running BFS from every room to find the nearest gate,
start BFS from all gates simultaneously.

The first time an empty room is reached is guaranteed to be the shortest distance to any gate.

Time  O(M*N)
Space O(M*N)
```


---

# Binary Search Tree (BST)

## Property

For every node:

```text
Left < Root < Right
```

---

# Validate BST

LeetCode:

```text
98. Validate Binary Search Tree
```

Recognition:

```text
BST Validation
Range Constraints
```

Key Insight:

Pass:

```text
(min,max)
```

bounds during DFS.

Complexity:

```text
Time  O(N)
Space O(H)
```

---

# Kth Smallest Element in BST

LeetCode:

```text
230. Kth Smallest Element in BST
```

Recognition:

```text
BST
Sorted Order
```

Key Insight:

```text
Inorder Traversal = Sorted Order
```

Complexity:

```text
Time  O(N)
Space O(H)
```

---

# Lowest Common Ancestor

LeetCode:

```text
236. Lowest Common Ancestor of Binary Tree
235. Lowest Common Ancestor of BST
```

Recognition:

```text
Ancestor
Common Parent
```

Interview Sound Bite:

> The first node where the paths to p and q diverge is the Lowest Common Ancestor.

---

# Maximum Depth of Binary Tree

LeetCode:

```text
104. Maximum Depth of Binary Tree
```

Recognition:

```text
Height
Depth
Longest Root-To-Leaf Path
```

Pattern:

```text
DFS
```

Complexity:

```text
Time  O(N)
Space O(H)
```

---

# Diameter of Binary Tree

LeetCode:

```text
543. Diameter of Binary Tree
```

Recognition:

```text
Longest Path
```

Key Insight:

```text
Diameter Through Node
=
Left Height + Right Height
```

Complexity:

```text
Time  O(N)
Space O(H)
```

---

# Audible Mapping

Examples:

```text
Content Category Hierarchy
Book Category Tree
Service Dependency Hierarchy
Organization Structure
```

These naturally map to trees.

---

# Tree Recognition Cheat Sheet

Keywords:

```text
Parent
Child
Depth
Height
Ancestor
Subtree
Path
```

Think:

```text
Tree Traversal
```

Keywords:

```text
Level By Level
Minimum Steps
Shortest Path
Nearest
Spread
```

Think:

```text
BFS
```

Keywords:

```text
BST
Sorted
Kth Smallest
```

Think:

```text
Inorder Traversal
```

---

# Must-Know LeetCode List

```text
104. Maximum Depth of Binary Tree
102. Binary Tree Level Order Traversal
98. Validate Binary Search Tree
230. Kth Smallest Element in BST
236. Lowest Common Ancestor
543. Diameter of Binary Tree

200. Number of Islands
994. Rotten Oranges
1091. Shortest Path in Binary Matrix
127. Word Ladder
```

# Chapter 14: Linked Lists

## Pattern Recognition

When you hear:

```text id="r9g4uh"
Reverse
Cycle
Middle
Merge
Reorder
Nth Node
Pointer Manipulation
```

Think:

```text id="n8ql5u"
Linked List
```

---

# Why Linked Lists Matter

Unlike arrays:

```text id="kgp98m"
Array
```

provides:

```text id="ahxyq7"
O(1) Index Access
```

while:

```text id="knkn5x"
Linked List
```

provides:

```text id="q8f1vl"
Efficient Insertions
Efficient Deletions
```

when node references are known.

---

# Core Mental Model

Node Structure:

```java id="6m6l7h"
class ListNode {

    int val;

    ListNode next;
}
```

Visual:

```text id="1nx5zm"
1 -> 2 -> 3 -> 4 -> null
```

Every problem usually comes down to:

```text id="grmz5o"
Moving Pointers
```

---

# Fast & Slow Pointer Pattern

## Recognition

Keywords:

```text id="3c8ymx"
Middle
Cycle
Halfway
Nth Node
```

Think:

```text id="c2vkfc"
Fast + Slow Pointer
```

---

# 876. Middle of the Linked List

## Pattern

Fast & Slow Pointer

---

## Core Idea

```java id="lcf1vr"
slow = slow.next;
fast = fast.next.next;
```

When:

```text id="w9iyb9"
fast reaches end
```

Then:

```text id="rmyfnw"
slow is at middle
```

---

## Complexity

```text id="zpxz4q"
Time  O(N)
Space O(1)
```

---

## Interview Sound Bite

> Moving one pointer twice as fast guarantees the slow pointer reaches the midpoint when the fast pointer reaches the end.

---

# 141. Linked List Cycle

## Pattern

Fast & Slow Pointer

---

## Recognition

Keywords:

```text id="hwrz5z"
Loop
Cycle
Circular
```

---

## Core Idea

If a cycle exists:

```text id="lqf9zm"
Fast and Slow pointers must eventually meet.
```

---

## Complexity

```text id="hgc1hb"
Time  O(N)
Space O(1)
```

---

# 142. Linked List Cycle II

## Pattern

Cycle Detection

---

## Follow-Up

Find:

```text id="s8mkqr"
Cycle Starting Node
```

not just whether a cycle exists.

---

## Interview Sound Bite

> Once fast and slow meet, moving one pointer to the head and advancing both one step at a time leads them to the cycle entry point.

---

# Reverse Linked List

## LeetCode

```text id="dq8v8v"
206. Reverse Linked List
```

---

## Pattern

Pointer Manipulation

---

## Core Variables

```java id="0iw7zj"
prev
curr
next
```

---

## Visualization

Before:

```text id="stz95s"
1 -> 2 -> 3 -> null
```

After:

```text id="yjlwmc"
3 -> 2 -> 1 -> null
```

---

## Complexity

```text id="m3oz4u"
Time  O(N)
Space O(1)
```

---

## Interview Sound Bite

> Reversal is simply redirecting pointers while preserving access to the remaining list.

---

# 92. Reverse Linked List II

## Recognition

Keywords:

```text id="7kh4zt"
Reverse Portion
Reverse Between
```

Think:

```text id="j0hgqe"
Reverse + Reconnect
```

---

# Merge Two Sorted Lists

## LeetCode

```text id="j21r6v"
21. Merge Two Sorted Lists
```

---

## Pattern

Two Pointers

---

## Core Idea

Use:

```text id="qjlwm7"
Dummy Node
```

to simplify edge cases.

---

## Complexity

```text id="hjlwm8"
Time  O(M+N)
Space O(1)
```

---

## Interview Sound Bite

> A dummy node eliminates special handling for the head of the merged list.

---

# 23. Merge K Sorted Lists

## Pattern

Heap

---

## Recognition

Keywords:

```text id="fjlwm9"
Multiple Sorted Lists
K Lists
```

Think:

```text id="pjlwm1"
Priority Queue
```

---

## Complexity

```text id="xjlwm2"
Time O(N log K)
```

---

## Interview Sound Bite

> At any time we only care about the smallest current node among K lists, making a heap ideal.

---

# 143. Reorder List

## Recognition

Keywords:

```text id="djlwm3"
Reorder
Alternate Ends
```

---

## Pattern

Three-Step Problem

```text id="mjlwm4"
Find Middle
Reverse Second Half
Merge
```

---

## Interview Sound Bite

> Most complex linked-list problems are combinations of simpler patterns.

---

# 19. Remove Nth Node From End

## Pattern

Two Pointers

---

## Recognition

Keywords:

```text id="zjlwm5"
Nth From End
```

---

## Core Idea

Maintain:

```text id="tjlwm6"
Gap of N Nodes
```

between pointers.

---

## Complexity

```text id="ujlwm7"
Time  O(N)
Space O(1)
```

---

# 160. Intersection of Two Linked Lists

## Pattern

Pointer Alignment

---

## Recognition

Keywords:

```text id="vjlwm8"
Intersection
Common Node
```

---

## Core Idea

Pointer A:

```text id="wjlwm9"
ListA -> ListB
```

Pointer B:

```text id="xjlwm0"
ListB -> ListA
```

Eventually:

```text id="yjlwm1"
Meet At Intersection
```

---

## Complexity

```text id="zjlwm2"
Time  O(N)
Space O(1)
```

---

# Dummy Node Pattern

One of the most important linked-list techniques.

Used in:

```text id="ajlwm3"
21. Merge Two Sorted Lists
19. Remove Nth Node
92. Reverse Linked List II
```

Benefits:

```text id="bjlwm4"
Cleaner Logic
Fewer Edge Cases
```

---

# Audible Mapping

Examples:

```text id="cjlwm5"
Playback History
Recently Played Books
LRU Cache
Recent Searches
```

Linked Lists frequently appear in:

```text id="djlwm6"
Caching
History Tracking
Pointer-Based Data Structures
```

---

# LRU Connection

Remember:

```text id="ejlwm7"
LRU Cache
=
HashMap
+
Doubly Linked List
```

This chapter directly supports:

```text id="fjlwm8"
Chapter 8: LRU Cache
```

---

# Linked List Recognition Cheat Sheet

Keywords:

```text id="gjlwm9"
Middle
Cycle
Halfway
```

Think:

```text id="hjlwm0"
Fast & Slow Pointer
```

---

Keywords:

```text id="ijlmw1"
Reverse
Reverse Portion
```

Think:

```text id="jjlmw2"
Pointer Manipulation
```

---

Keywords:

```text id="kjlmw3"
Merge Sorted Lists
K Sorted Lists
```

Think:

```text id="ljlmw4"
Two Pointers
or
Heap
```

---

Keywords:

```text id="mjlmw5"
Nth From End
```

Think:

```text id="njlmw6"
Two Pointer Gap
```

---

# Must-Know LeetCode List

```text id="ojlmw7"
876. Middle of the Linked List

141. Linked List Cycle
142. Linked List Cycle II

206. Reverse Linked List
92. Reverse Linked List II

21. Merge Two Sorted Lists
23. Merge K Sorted Lists

143. Reorder List

19. Remove Nth Node From End

160. Intersection of Two Linked Lists
```

---

# Audible Interview Set

If time is limited, prioritize:

```text id="pjlmw8"
206. Reverse Linked List
141. Linked List Cycle
876. Middle of Linked List
21. Merge Two Sorted Lists
143. Reorder List
```

These five cover:

```text id="qjlmw9"
Pointer Manipulation
Fast & Slow Pointer
Reversal
Merge
Multi-Step Linked List Design
```

# Chapter 15: Trie & Autocomplete

## Pattern Recognition

Keywords:

```text
Prefix
Dictionary
Autocomplete
Suggestions
Search
```

Think:

```text
Trie
```

---

# Why Trie?

Suppose:

```text
cat
car
care
card
```

Common Prefix:

```text
ca
```

Trie stores prefixes efficiently.
---

# Trie Node

```java
class TrieNode {

    Map<Character,TrieNode> children = new HashMap<>();
    boolean isWord;
}
```
Using a HashMap stores only existing children. An array of size 26 allocates space for every node regardless of usage. HashMap is more memory efficient and supports arbitrary character sets(support for ASCII,Unicode), while arrays provide slightly faster lookup for fixed alphabets.

---

# Core Operations

## Insert

```java
insert(word)
```

Complexity:

```text
O(length)
```

---

## Search

```java
search(word)
```

Complexity:

```text
O(length)
```

---

## Starts With

```java
startsWith(prefix)
```

Complexity:

```text
O(length)
```

---

# Must-Know Problems

## Implement Trie

LeetCode:

```text
208. Implement Trie
```

---

## Design Add and Search Words

LeetCode:

```text
211. Design Add and Search Words
```

Introduces:

```text
DFS + Wildcards
```

---
# 211. Design Add and Search Words Data Structure

## Pattern Recognition

Keywords:

```text
Dictionary
Wildcard
.
Any Character
Autocomplete
Prefix Search
```

Think:

```text
Trie + DFS
```

---

## Core Idea

Normal Trie search follows a single path.

When we encounter:

```text
.
```

we must try:

```text
ALL possible children
```

from the current Trie node.

This turns the search into a DFS over the Trie.

---

## Implementation

```java
public boolean search(String word) {
    return search(root,word);
}

public boolean search(TrieNode node,String word) {

    if(word!=null && word.length()==0) // b** or word fully consumed
        return node.isEnd;

    for(int i=0;i<word.length();i++) {

        char ch = word.charAt(i);

        if(ch=='.') {

            for(Character k: node.children.keySet()) {

                TrieNode child = node.children.get(k);

                boolean result =
                    search(child,word.substring(i+1));

                if(result)
                    return true;
            }

            return false; // if none of the paths succeeds
        }
        else {

            if(!node.children.containsKey(ch))
                return false;

            node = node.children.get(ch);
        }
    }

    return node.isEnd; // bad or *ad or **d
}
```

---

## Why `return node.isEnd`?

```java
return node.isEnd; // bad or *ad or **d
```

The path existing is not enough.

Example:

```text
cadbury exists
cad does not exist
```

The path:

```text
c -> a -> d
```

exists, but:

```text
d.isEnd = false
```

Therefore:

```java
search("cad")
```

must return:

```text
false
```

while:

```java
startsWith("cad")
```

returns:

```text
true
```

A Trie must distinguish between:

```text
Prefix Exists
```

and

```text
Complete Word Exists
```

---

## Why `return false` after wildcard expansion?

```java
return false; // if none of the paths succeeds
```

Example:

```text
.xy
```

Trie contains:

```text
bad
dad
mad
```

Wildcard expansion tries:

```text
bxy -> false
dxy -> false
mxy -> false
```

Since:

```text
false OR false OR false = false
```

the wildcard search must immediately return:

```text
false
```

Once all wildcard branches fail, there is no valid match remaining.

---

## Interview Sound Bite

A normal Trie search follows a single path. A wildcard introduces multiple possible paths, so we recursively explore every child node and succeed if any branch matches the remaining suffix.

---

## Complexity

Let:

```text
L = word length
```

Normal search:

```text
O(L)
```

Wildcard search worst case:

```text
O(26^L)
```

because each wildcard can branch into multiple child nodes.

## Search Suggestions System

LeetCode:

```text
1268. Search Suggestions System
```

Very Audible-Relevant.

# 1268. Search Suggestions System

## Pattern Recognition

Keywords:

```text
Autocomplete
Search Suggestions
Typeahead
Prefix Search
Dictionary
```

Think:

```text
Trie
```

---

## Core Idea

Naive Approach:

```text
Build Trie

For every typed prefix:

    DFS entire subtree

    Collect matching products

    Sort

    Return first 3
```

Optimized Approach:

```text
1. Sort products once.

2. While inserting into the Trie,
   store up to 3 suggestions at every node.

3. During search,
   simply return the precomputed suggestions.
```

This eliminates:

```text
DFS
Sorting during query
Repeated subtree traversal
```

---

## Key Insight

Sort products first:

```java
Arrays.sort(products);
```

Example:

```text
mobile
moneypot
monitor
mouse
mousepad
```

While inserting products:

```text
Prefix: "mo"

Products arrive in sorted order:

mobile
moneypot
monitor
mouse
mousepad
```

Store only first 3:

```text
mobile
moneypot
monitor
```

Since insertion is already lexicographical:

```text
No DFS needed
No sorting needed during search
```

---

## Implementation

```java
class Solution {
    public static int K=3;
	TrieNode root;

    public Solution(){
        root= new TrieNode();
    }

//O(L) where L is max lenght of product
    public void addProductsToDictionary(String product){
        TrieNode node=root;
        for(char ch :product.toCharArray()){
            if(!node.children.containsKey(ch))
                node.children.put(ch,new TrieNode());
            node= node.children.get(ch);
            if(node.suggestions.size()<K)
                node.suggestions.add(product);    
        }
    }

    public List<List<String>> suggestedProducts(String[] products, String searchWord) {
        //N number of products we have O(Nlog N)
        Arrays.sort(products);
        //O(NL);
        for(String product: products){
            addProductsToDictionary(product);
        }

        List<List<String>> result = new ArrayList();
        TrieNode node = root;
        //O(S)
        //Total : O(NL)+O(S)+O(NlogN)
        for(char ch: searchWord.toCharArray()){
            if(node== null || !node.children.containsKey(ch)){
                result.add(new ArrayList<String>());
                node=null;
            }
            else{
                node= node.children.get(ch);
                result.add(new ArrayList<>(node.suggestions));
            }
        }
        return result;
    }
}


class TrieNode{
    public Map<Character,TrieNode> children = new HashMap<>();
    public List<String> suggestions= new ArrayList();
}
```

---

## Why Store Suggestions at Every Node?

Instead of:

```text
Search Prefix
↓
DFS Subtree
↓
Collect Products
↓
Sort
↓
Take Top 3
```

we do:

```text
Search Prefix
↓
Return node.suggestions
```

Query becomes:

```text
O(S)
```

instead of traversing entire subtrees.

---

## Why Sort Products First?

Because products are inserted in lexicographical order:

```text
mobile
moneypot
monitor
mouse
mousepad
```

the first 3 products reaching a Trie node are automatically:

```text
Top 3 lexicographically smallest suggestions
```

Therefore:

```java
if(node.suggestions.size() < K)
    node.suggestions.add(product);
```

is sufficient.

No additional sorting is required.

---

## Why `node = null`?

Once a prefix fails:

```text
mousez
```

all future prefixes must fail:

```text
mouseza
mousezab
mousezabc
```

Setting:

```java
node = null;
```

allows us to immediately return:

```text
[]
[]
[]
```

for all remaining characters without additional Trie lookups.

---

## Complexity

Let:

```text
N = number of products
L = average/max product length
S = searchWord length
```

Build:

```text
Sort Products: O(N log N)

Build Trie:    O(NL)
```

Query:

```text
O(S)
```

Total:

```text
O(N log N + NL + S)
```

Space:

```text
O(NL)
```

---

## Interview Sound Bite

Sort products first. While inserting into the Trie, store up to three products at every prefix node. Since products are inserted in lexicographical order, the first three products reaching a node are automatically the lexicographically smallest suggestions. During search, simply walk the Trie once and return the precomputed suggestions stored at each node, giving O(S) query time.


---

## Word Search II

LeetCode:

```text
212. Word Search II
```

Combines:

```text
Trie + Backtracking
```

---

# Autocomplete Design

Question:

User types:

```text
ha
```

Need:

```text
harry potter
happy place
half blood prince
```

Quickly.

Recognition:

```text
Prefix Query
```

Think:

```text
Trie
```

---

# Audible Mapping

Examples:

```text
Book Search
Author Search
Narrator Search
Autocomplete Suggestions
```

These are among the most realistic Audible-style applications of Trie.

---

# Interview Sound Bite

> Trie trades memory for speed by storing common prefixes only once, allowing prefix lookups in O(length of prefix).

---

# Must-Know LeetCode List

208. Implement Trie
209. Design Add and Search Words
210. Search Suggestions System
211. Word Search II

```
```
# Chapter 17: Design a HashMap

## Pattern Recognition

Keywords:

```text
Build HashMap
Dictionary
Key Value Store
Lookup
Hash Function
Collision
```

Think:

```text
Hash Table
```

---

# Goal

Implement:

```java
put(key,value)
get(key)
remove(key)
```

Without using:

```java
HashMap
```

---

# Mental Model

HashMap is essentially:

```text
Array of Buckets
```

Example:

```text
Index

0
1
2
3
4
5
...
```

---

# Hash Function

Convert:

```text
Key
```

into:

```text
Bucket Index
```

Example:

```java
index = key % bucketCount;
```

---

# Why Hashing Works

Many keys:

```text
100
200
300
```

Map into:

```text
Fixed Bucket Array
```

Allowing:

```text
Near O(1) Lookup
```

---

# Collision

Problem:

```java
1 % 10 = 1
11 % 10 = 1
21 % 10 = 1
```

All land in:

```text
Bucket 1
```

---

# Collision Resolution

## Chaining

Each bucket stores:

```java
LinkedList<Entry>
```

Example:

```text
Bucket[1]

1 -> 11 -> 21
```

---

# Entry Structure

```java
class Entry {

    int key;
    int value;

    Entry next;
}
```

---

# put()

## Step 1

Find bucket.

```java
index = hash(key);
```

---

## Step 2

Search linked list.

If found:

```text
Update Value
```

Else:

```text
Insert New Entry
```

---

# get()

Find bucket.

Traverse chain.

If found:

```java
return value;
```

Else:

```java
return -1;
```

---

# remove()

Find bucket.

Remove node from linked list.

---

# Complexity

Average Case:

```text
Put O(1)
Get O(1)
Remove O(1)
```

---

# Worst Case

All keys collide.

Bucket:

```text
1 -> 11 -> 21 -> 31 -> ...
```

Complexity:

```text
O(N)
```

---

# Rehashing

Problem:

Too many collisions.

---

## Load Factor

Formula:

```text
entries / buckets
```

Example:

```text
75 entries
100 buckets

Load Factor = 0.75
```

---

## When Load Factor Grows

Create:

```text
Larger Bucket Array
```

Example:

```text
100 -> 200 buckets
```

Reinsert all keys.

This is:

```text
Rehashing
```

---

# Java 8 Improvement

Before Java 8:

```text
Collision Bucket
=
Linked List
```

---

## After Threshold

Convert bucket to:

```text
Balanced Tree
```

Reducing:

```text
O(N)
```

to:

```text
O(log N)
```

for heavy collisions.

---

# Design HashMap LeetCode

```text
706. Design HashMap
```

Must Know.

---

# HashSet Relationship

HashSet internally uses:

```text
HashMap
```

Only keys matter.

Values are ignored.

---

# ConcurrentHashMap Discussion

Senior-Level Follow-Up.

Question:

> How would you make this thread-safe?

Options:

```text
synchronized
ConcurrentHashMap
Lock Striping
```

---

# Interview Sound Bite

> A HashMap trades memory for speed by distributing keys across buckets using a hash function. Good hash distribution keeps bucket sizes small and provides near O(1) lookup, insertion, and deletion.

---

# Audible Mapping

Examples:

```text
User Lookup
Book Metadata Lookup
Session Lookup
Cache Index
```

All naturally map to HashMap.

---

# Recognition Cheat Sheet

Need:

```text
Fast Lookup
Key Value Mapping
Frequency Counting
Caching
```

Think:

```text
HashMap
```

---

# Must-Know LeetCode List

706. Design HashMap
708. Two Sum
709. Group Anagrams
710. Top K Frequent Elements
155. MinStack (Stack problems)
36. Valid Sudoku


```
```

# Subarray Problems: Understanding O(n²) vs O(n³)

Subarray problems
711. Subarray Sum Equals K
Max SubArray..

## The Confusion

Many candidates think:

> "Subarray problems are O(n³) because there are three loops."

This is not always true.

The key question is:

> Am I generating subarrays, or am I recomputing work for every subarray?

---

# Step 1: How Many Subarrays Exist?

Consider:

```text
[1,2,3,4]
```

Possible subarrays:

```text
[1]
[1,2]
[1,2,3]
[1,2,3,4]

[2]
[2,3]
[2,3,4]

[3]
[3,4]

[4]
```

Count:

```text
n + (n-1) + (n-2) + ... + 1

= n(n+1)/2
```

Therefore:

```text
Number of subarrays = O(n²)
```

This is one of the most important interview observations.

---

# Brute Force Pattern #1 (O(n²))

Generate every subarray once.

```java
for(int start=0; start<n; start++) {

    int sum = 0;

    for(int end=start; end<n; end++) {

        sum += nums[end];

        // process current subarray
    }
}
```

Why O(n²)?

Outer loop:

```text
n
```

Inner loop:

```text
n + (n-1) + (n-2) + ... + 1
```

Total:

```text
O(n²)
```

We are visiting every subarray exactly once.

---

# Example: Subarray Sum Equals K

```java
for(int start=0; start<n; start++) {

    int sum = 0;

    for(int end=start; end<n; end++) {

        sum += nums[end];

        if(sum == k)
            count++;
    }
}
```

Time Complexity:

```text
O(n²)
```

Space Complexity:

```text
O(1)
```

---

# Brute Force Pattern #2 (O(n³))

Bad approach:

```java
for(int start=0; start<n; start++) {

    for(int end=start; end<n; end++) {

        int sum = 0;

        for(int k=start; k<=end; k++) {
            sum += nums[k];
        }
    }
}
```

What's happening?

For every subarray:

```text
[start ... end]
```

we scan the entire subarray again.

---

# Why O(n³)?

Loop 1:

```text
start
```

Loop 2:

```text
end
```

Together generate:

```text
O(n²) subarrays
```

Loop 3:

```text
scan subarray
```

Worst case:

```text
O(n)
```

Therefore:

```text
O(n²) * O(n)

= O(n³)
```

---

# Visual Difference

## O(n³)

```text
Generate subarray
        ↓
Scan subarray again
```

Example:

```text
[1,2,3,4]

Generate:
[1,2,3]

Then scan:
1
2
3
```

Repeated over and over.

---

## O(n²)

```text
Generate subarray
        ↓
Maintain running information
```

Example:

```text
sum = 0

[1]
sum = 1

[1,2]
sum = 3

[1,2,3]
sum = 6

[1,2,3,4]
sum = 10
```

No rescanning.

---

# Interview Recognition Rule

Whenever you hear:

```text
Subarray
Contiguous
Range
Window
```

Think:

```text
Brute Force:
start/end loops

O(n²)
```

Then ask:

```text
Can I maintain information incrementally?
```

Examples:

```text
Running Sum
Prefix Sum
Sliding Window
Deque
HashMap
```

---

# Common Examples

## Maximum Subarray Sum

Brute Force:

```text
O(n²)
```

Kadane:

```text
O(n)
```

---

## Subarray Sum Equals K

Brute Force:

```text
O(n²)
```

Prefix Sum + HashMap:

```text
O(n)
```

---

## Longest Subarray With Sum K

Brute Force:

```text
O(n²)
```

Prefix Sum:

```text
O(n)
```

---

## Sliding Window Maximum

Brute Force:

```text
O(nk)
```

Deque:

```text
O(n)
```

---

# Mental Model To Remember

For Subarray Problems:

```text
O(n²)
=
Generate all subarrays
```

```text
O(n³)
=
Generate all subarrays
+
Rescan each subarray
```

This is the most important distinction.

If you can maintain information while expanding the subarray (running sum, running max, running count), you usually reduce O(n³) to O(n²).

If you can avoid generating all subarrays entirely (prefix sums, sliding window, hash maps), you may reach O(n).


# Subarray Sum Equals K - Prefix Sum + HashMap

## Problem

Given an integer array `nums` and an integer `k`, return the total number of continuous subarrays whose sum equals `k`.

---

# Brute Force

```java
class Solution {
    public int subarraySum(int[] nums, int k) {

        int count = 0;

        for(int start = 0; start < nums.length; start++) {

            int sum = 0;

            for(int end = start; end < nums.length; end++) {

                sum += nums[end];

                if(sum == k) {
                    count++;
                }
            }
        }

        return count;
    }
}
```

## Complexity

```text
Time  : O(n²)
Space : O(1)
```

---

# Prefix Sum + HashMap

## Core Intuition

```java
// if current Prefix Sum is S & I see S-K... in prefix sum map... that means
// wherever I saw S-K, index from then on to current index should sum to K
//
// 1,2,3,4
//
// lets say 7...
//
// {0:1,1:1,3:1: ith posn is 1,6:1,10:1}
//
// now iterate...
//
// at position 3, does prefix map contain S-K
//
// yes...
//
// 10-7=3...
//
// at ith position 1...
//
// so from 2+
//
// [3,4] sums to 7
```

The key equation is:

```text
Current Prefix Sum - Previous Prefix Sum = K
```

Therefore:

```text
Previous Prefix Sum = Current Prefix Sum - K
```

So if current prefix sum is:

```text
S
```

then we need to know whether:

```text
S-K
```

has been seen before.

---

# Why Does This Work?

Example:

```text
nums = [1,2,3,4]
k = 7
```

Prefix Sums:

```text
Index      Prefix Sum

-1             0
 0             1
 1             3
 2             6
 3            10
```

At index 3:

```text
Current Prefix Sum = 10
```

Need:

```text
10 - 7 = 3
```

Have we seen prefix sum 3 before?

```text
Yes
```

Prefix sum 3 occurred at index 1.

Therefore:

```text
10 - 3 = 7
```

Subarray:

```text
[3,4]
```

Starts:

```text
1 + 1 = 2
```

Ends:

```text
3
```

---

# Version 1 - Count Subarrays Only

Store:

```java
Map<Integer,Integer>
```

Meaning:

```text
PrefixSum -> Frequency
```

Example:

```text
{
    0 : 1,
    1 : 1,
    3 : 1,
    6 : 1,
    10 : 1
}
```

---

## Why Frequency?

Consider:

```text
nums = [0,0,0]
k = 0
```

Prefix sum 0 appears multiple times.

Every occurrence creates another valid subarray.

Therefore:

```java
count += prefixMap.get(sum-k);
```

instead of:

```java
count++;
```

---

## Code

```java
class Solution {

    public int subarraySumWithoutIndices(int[] nums, int k) {

        // if current Prefix Sum is S & I see S-K... in prefix sum map... that means
        // wherever I saw S-K, index from then on to current index should sum to K
        // 1,2,3,4
        // lets say 7...
        // {0:1,1:1,3:1: ith posn is 1,6:1,10:1}
        // now iterate..
        // at position 3, does prefix map contain S-K yes.. 10-7=3... at ith position 1..
        // so from 2+

        int n = nums.length;
        int count = 0;
        int sum = 0;

        Map<Integer,Integer> prefixMap = new HashMap<>();

        prefixMap.put(0,1);

        for(int i=0;i<n;i++){

            sum += nums[i];

            if(prefixMap.containsKey(sum-k)){
                count += prefixMap.get(sum-k);
            }

            prefixMap.put(
                sum,
                prefixMap.getOrDefault(sum,0)+1
            );
        }

        return count;
    }
}
```

## Complexity

```text
Time  : O(n)
Space : O(n)
```

---

# Version 2 - Return Actual Subarray Indices

Store:

```java
Map<Integer,List<Integer>>
```

Meaning:

```text
PrefixSum -> All Indices
```

Instead of storing:

```text
3 -> 1
```

Store:

```text
3 -> [1]
```

If a prefix sum occurs multiple times:

```text
0 -> [-1, 2, 5]
```

then every stored index can generate a valid subarray.

---

## Why -1?

Before processing the array:

```java
prefixMap.put(0, [-1]);
```

This represents:

```text
Prefix Sum 0 exists before array starts
```

It allows us to detect subarrays that begin at index 0.

---

## Code

```java
class Solution {

    public int subarraySum(int[] nums, int k) {

        // if current Prefix Sum is S & I see S-K... in prefix sum map... that means
        // wherever I saw S-K, index from then on to current index should sum to K
        // 1,2,3,4
        // lets say 7...
        // {0:1,1:1,3:1:ith posn is 1,6:1,10:1}
        // now iterate..
        // at position 3, does prefix map contain S-K yes.. 10-7=3... at ith position 1..
        // so from 2+

        int n = nums.length;
        int count = 0;
        int sum = 0;

        Map<Integer,List<Integer>> prefixMap = new HashMap<>();

        List<Integer> initial = new ArrayList<>();
        initial.add(-1);

        prefixMap.put(0, initial);

        for(int i=0;i<n;i++){

            sum += nums[i];

            if(prefixMap.containsKey(sum-k)){

                count += prefixMap.get(sum-k).size();

                List<Integer> indices =
                    prefixMap.get(sum-k);

                for(int index : indices){

                    System.out.println(
                        "Starting at index "
                        + (index+1)
                        + " and ending at "
                        + i
                    );
                }
            }

            if(!prefixMap.containsKey(sum))
                prefixMap.put(sum,new ArrayList<>());

            List<Integer> indices =
                prefixMap.get(sum);

            indices.add(i);
        }

        return count;
    }
}
```

## Complexity

```text
Time  : O(n + total matching subarrays)
Space : O(n)
```

---

# Interview Follow-Up Variations

## Count Only

```java
Map<Integer,Integer>
```

Store:

```text
PrefixSum -> Frequency
```

---

## Return One Matching Subarray

```java
Map<Integer,Integer>
```

Store:

```text
PrefixSum -> First Index
```

---

## Return All Matching Subarrays

```java
Map<Integer,List<Integer>>
```

Store:

```text
PrefixSum -> All Indices
```

---

# Recognition Pattern

Whenever you hear:

```text
Subarray
Continuous
Range Sum
Sum Equals K
```

Think:

```text
Brute Force O(n²)
        ↓
Prefix Sum
        ↓
Current Prefix Sum = S
Need Previous Prefix Sum = S-K
        ↓
HashMap
```

This pattern appears repeatedly in:

```text
Subarray Sum Equals K
Continuous Subarray Sum
Maximum Size Subarray Sum Equals K
Count of Range Sum
Binary Subarrays With Sum
```
# Insert Delete GetRandom O(1)

## Data Structure Comparison

| Operation        |                   ArrayList | Java `LinkedList` (DLL) | Singly Linked List (Head Only) | Singly Linked List (Head + Tail) |
| ---------------- | --------------------------: | ----------------------: | -----------------------------: | -------------------------------: |
| `get(index)`     |                    **O(1)** |                **O(n)** |                       **O(n)** |                         **O(n)** |
| `addLast()`      |          Amortized **O(1)** |                **O(1)** |                       **O(n)** |                         **O(1)** |
| `removeLast()`   |                    **O(1)** |                **O(1)** |                       **O(n)** |                         **O(n)** |
| `add(index)`     |                    **O(n)** |                **O(n)** |                       **O(n)** |                         **O(n)** |
| `remove(index)`  |                    **O(n)** |                **O(n)** |                       **O(n)** |                         **O(n)** |
| `insert at head` | **O(n)** *(shift elements)* |                **O(1)** |                       **O(1)** |                         **O(1)** |
| `remove head`    | **O(n)** *(shift elements)* |                **O(1)** |                       **O(1)** |                         **O(1)** |
| Random Access    |                  ✅ **O(1)** |              ❌ **O(n)** |                     ❌ **O(n)** |                       ❌ **O(n)** |

```java
// We need random access in O(1), therefore we need an ArrayList.
//
// Otherwise, insert, remove and contains could all be handled by a HashSet.
//
// Caveat:
// Removing an arbitrary element from an ArrayList is O(n)
// because elements need to be shifted.
//
// To avoid shifting, always delete the last element.
// If the element to delete is not the last element,
// swap it with the last element, update its index,
// then remove the last element.
//
// Since we must know the position of every value,
// we use a HashMap<Value, Index> instead of a HashSet.
//
// Improvement:
// Keep Random as a class member instead of creating
// a new Random object for every getRandom() call.

class RandomizedSet {

    Map<Integer, Integer> hmap;
    List<Integer> arrList;
    Random r;

    public RandomizedSet() {
        hmap = new HashMap<>();
        arrList = new ArrayList<>();
        r = new Random();
    }

    // ArrayList.add() at the end is amortized O(1).
    //
    // Native Singly Linked List (head only):
    // Insert at end -> O(n)
    //
    // Java LinkedList is implemented as a Doubly Linked List
    // and supports addLast() in O(1).

    public boolean insert(int val) {

        if (hmap.containsKey(val))
            return false;

        int index = arrList.size();

        hmap.put(val, index);
        arrList.add(val);

        return true;
    }

    // Swap the element to delete with the last element.
    // Update the HashMap with the new index.
    // Remove the last element from the ArrayList in O(1).

    public boolean remove(int val) {

        if (!hmap.containsKey(val))
            return false;

        int indexOfVal = hmap.get(val);

        int lastIndex = arrList.size() - 1;
        int lastValue = arrList.get(lastIndex);

        arrList.set(indexOfVal, lastValue);
        hmap.put(lastValue, indexOfVal);

        arrList.remove(lastIndex);
        hmap.remove(val);

        return true;
    }

    public int getRandom() {

        // nextInt(size) generates [0, size)
        // Upper bound is exclusive (similar to substring()).

        return arrList.get(r.nextInt(arrList.size()));
    }
}

/**
 * Your RandomizedSet object will be instantiated and called as such:
 *
 * RandomizedSet obj = new RandomizedSet();
 * boolean param_1 = obj.insert(val);
 * boolean param_2 = obj.remove(val);
 * int param_3 = obj.getRandom();
 */
```


# Longest Consecutive Sequence

## Problem

Given an unsorted integer array `nums`, return the length of the longest sequence of consecutive integers.

Example:

```text
Input:  [100,4,200,1,3,2]
Output: 4

Sequence: 1,2,3,4
```

**Expected Complexity:**

* Time: **O(n)**
* Space: **O(n)**

---

## Solution (HashSet)

```java
class Solution {
    public int longestConsecutive(int[] nums) {
        int lcs = 0, cs = 0;

        Set<Integer> hset = new HashSet<>();

        // Store all numbers for O(1) lookup
        for (int num : nums) {
            hset.add(num);
        }

        // Iterate over unique numbers
        for (int num : hset) {

            // This small hack:
            // If a smaller value exists, then this number cannot be
            // the beginning of a sequence.
            //
            // So don't even bother counting.
            //
            // Example:
            // Sequence = 1,2,3,4,5
            // When we reach 4, we know 3 exists.
            // The sequence would have already been counted starting from 1.
            int current = num;

            if (hset.contains(current - 1))
                continue;

            cs = 1;

            while (hset.contains(++current))
                cs++;

            lcs = Math.max(lcs, cs);
        }

        return lcs;
    }
}
```

---

## Why is the algorithm O(n)?

At first glance, it looks like **O(n²)** because of the nested `while` loop.

The trick is:

* The **inner `while` loop runs only when the current number is the beginning of a sequence** (`current - 1` is not present).
* Every consecutive sequence has **exactly one beginning**.
* So each sequence is traversed only once.

Example:

```text
1 2 3 4 5
```

Only `1` starts counting:

```text
1 → 2 → 3 → 4 → 5
```

When the outer loop reaches:

```text
2
3
4
5
```

they are immediately skipped because they have predecessors.

Therefore:

* Outer loop = **O(n)**
* Total work done by all `while` loops combined = **O(n)**

Overall:

```text
O(n) + O(n) = O(n)
```

The outer loop iterates over every unique number once. The inner while loop executes only when the current number is the beginning of a sequence (num - 1 doesn't exist). Since every sequence has exactly one beginning, each sequence is traversed only once. Therefore, the total work done by all the inner while loops is O(n). Hence, the overall time complexity is O(n).

**Space Complexity:** `O(n)` (HashSet stores all unique numbers.)



# Asteroid Collision (Nice-to-Have)

> **Category:** Stack Simulation
> **Difficulty:** Medium
> **Priority:** ⭐⭐⭐☆☆ (Nice to have, not a must)

---

## Problem

Given an array of integers representing asteroids:

* Positive value (`+`) → asteroid moving right.
* Negative value (`-`) → asteroid moving left.
* The absolute value represents the asteroid's size.

When two asteroids moving towards each other collide:

* Larger asteroid survives.
* If both are the same size, both explode.

Return the state of the asteroids after all collisions.

---

## Why do we need a Stack?

A collision is only possible when:

```text
Stack Top  ->  →
Current    ->  ←
```

i.e.

```java
stack.peek() > 0 && current < 0
```

The incoming asteroid can only collide with the **most recent right-moving asteroid** that hasn't already been destroyed.

Since we always compare with the latest surviving asteroid first, a **Stack (LIFO)** is the perfect data structure.

---

## Solution

```java
class Solution {
    public int[] asteroidCollision(int[] asteroids) {

        Stack<Integer> st = new Stack<>();

        for (int asteroid : asteroids) {

            int current = asteroid;
            boolean incomingDestroyed = false;

            // Collision is possible only when:
            // stack top is moving right and current is moving left.
            while (!st.isEmpty() && st.peek() > 0 && current < 0) {

                if (st.peek() > Math.abs(current)) {
                    // Stack asteroid survives.
                    incomingDestroyed = true;
                    break;
                } else if (st.peek() == Math.abs(current)) {
                    // Both explode.
                    incomingDestroyed = true;
                    st.pop();
                    break;
                } else {
                    // Current survives.
                    // Pop and continue checking because the current asteroid
                    // may collide with earlier asteroids.
                    st.pop();
                }
            }

            if (!incomingDestroyed)
                st.push(current);
        }

        int[] result = new int[st.size()];

        for (int i = result.length - 1; i >= 0; i--)
            result[i] = st.pop();

        return result;
    }
}
```

---

## Time Complexity

**Time:** `O(n)` (Amortized)

At first glance, it looks like `O(n²)` because of the nested `while` loop.

However:

* The outer loop runs `O(n)` times.
* The inner `while` loop is **not** `O(n)` for every outer iteration.
* Its work is **amortized** across the entire algorithm because **each asteroid is pushed onto the stack at most once and popped from the stack at most once.**

Therefore:

```text
Outer loop                    = O(n)

All executions of while loop  = O(n)

Overall                       = O(n) + O(n) = O(n)
```

### Interview Explanation

> The outer loop runs O(n) times. The inner `while` loop is not O(n) for every outer iteration. Instead, its O(n) work is amortized across the entire algorithm because each asteroid is pushed once and popped at most once. Therefore, the total work is O(n) + O(n) = O(n), not O(n²).

---

## Space Complexity

* **O(n)** (Stack)
---

# Find K Closest Elements (Binary Search)

```java
class Solution {

    public List<Integer> findClosestElements(int[] arr, int k, int x) {

        // Example:
        // arr = [1,2,3,4,5], k = 4, x = 3

        int n = arr.length;

        int lo = 0;
        int hi = n - k;

        // Search space:
        // We are searching for the starting index of the optimal window.
        // Possible starting indices range from 0 to (n - k).
		// between 0 and n-k what is best starting point..
        //Since the array is sorted, the k closest elements to x will always form a contiguous subarray (window). Therefore, instead of selecting individual elements, we only //need to find the correct window of size k.
        //The starting index of this window can range from 0 to N - k, so we perform a binary search over these possible starting positions. Once we determine the correct //starting index, we simply return the k elements in that window. This gives us a time complexity of O(log(N - k) + k).
        //The sophisticated part of the algorithm is deciding whether the optimal window lies to the left or the right of the current midpoint. That's what the comparison logic in Step 2 determines */
        /*"If I slide the window one position to the right, is the new element entering (arr[mid + k]) closer to x than the old element leaving (arr[mid])?"*/
        //"If I slide the window one step to the right, I'm throwing away A and gaining B. Is B a better choice than A?"
        
        //
        // Since the array is sorted, the k closest elements to x will always
        // form a contiguous subarray (window). Therefore, instead of selecting
        // individual elements, we only need to find the correct window of size k.
        //
        // Once we determine the starting index, we simply return the k elements
        // in that window.
        //
        // Time Complexity:
        // O(log(n - k) + k)
        //
        // The sophisticated part of this algorithm is deciding whether the
        // optimal window lies to the left or right of the current midpoint.
        // That's exactly what the comparison logic below determines.

        while (lo < hi) {

            int mid = (lo + hi) / 2;

            // Current Window:
            // [mid ............... mid + k - 1]
            //
            // Next Window:
            // [mid + 1 ........... mid + k]
            //
            // Only two elements differ:
            //
            // Leaving:  arr[mid]      (A)
            // Entering: arr[mid + k]  (B)
            //
            // If I slide the window one position to the right,
            // I'm throwing away A and gaining B.
            // Is B a better choice than A?
            //
            // Common intuition:
            //
            // A -------- x -------- B
            //
            // Compare:
            // Distance from x to A  -> x - A
            // Distance from x to B  -> B - x

            if (x - arr[mid] > arr[mid + k] - x) {
                lo = mid + 1;
            }
            // Standard binary search convergence.
            else {
                hi = mid;
            }
        }

        List<Integer> result = new ArrayList<>();

        for (int i = lo; i < lo + k; i++) {
            result.add(arr[i]);
        }

        return result;
    }
}
```
