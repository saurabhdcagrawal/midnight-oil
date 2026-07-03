# Linked Lists

---

# Floyd's Cycle Detection Algorithm (Fast & Slow Pointer)

## Problem

Given the head of a singly linked list, determine whether the linked list contains a cycle.

Example:

```
1 -> 2 -> 3 -> 4
          ^     |
          |_____|
```

Return:

```
true
```

If the list terminates normally:

```
1 -> 2 -> 3 -> null
```

Return:

```
false
```

---

# Solution 1 - HashSet

## Idea

Store every visited node inside a HashSet.

If a node is encountered again, a cycle exists.

### Time Complexity

```
O(n)
```

### Space Complexity

```
O(n)
```

```java
/*public boolean hasCycle(ListNode head) {
    HashSet<ListNode> hset= new HashSet<>();
    ListNode temp=head;
    while(temp!=null){
        if(hset.contains(temp))
            return true;
        hset.add(temp);
        temp=temp.next;
    }
    return false;
}*/
```

---

# Solution 2 - Floyd's Cycle Detection (Tortoise & Hare)

## Idea

Maintain two pointers.

- Slow Pointer → moves one step
- Fast Pointer → moves two steps

If a cycle exists, the fast pointer eventually laps the slow pointer and both pointers meet.

If no cycle exists, the fast pointer reaches the end of the list first.

---

## Algorithm

```
Initialize

slow = head
fast = head

while(fast != null && fast.next != null)

    slow = slow.next
    fast = fast.next.next

    if(slow == fast)
        Cycle Exists

If fast reaches null

No Cycle
```

---

# Why Fast Pointer?

One of the most common interview questions is:

> **Why do we only check the fast pointer?**

The **fast pointer** advances two nodes at a time.

If the list does **not** contain a cycle, the fast pointer will always reach the end of the list before the slow pointer.

Therefore, the termination condition should always be based on the fast pointer.

```java
while(fastPointer != null && fastPointer.next != null)
```

instead of

```java
while(slowPointer != null &&
      fastPointer != null &&
      fastPointer.next != null)
```

Although checking `slowPointer != null` is not incorrect, it is unnecessary because the fast pointer reaching the end already guarantees termination.

---

# Why check

```java
if(fastPointer == null || fastPointer.next == null)
```

instead of

```java
if(slowPointer == null || fastPointer == null)
```

Consider:

```
1 -> 2 -> 3 -> null
```

Execution:

```
slow = 1
fast = 1
```

Iteration 1

```
slow = 2
fast = 3
```

Loop condition

```
fast != null       ✓
fast.next != null  ✗
```

The loop exits because the **fast pointer** has reached the end.

Notice:

```
slow != null
fast != null
```

If we checked

```java
if(slowPointer == null || fastPointer == null)
```

the condition would evaluate to **false**, incorrectly suggesting a cycle may exist.

The correct termination condition is therefore

```java
if(fastPointer == null || fastPointer.next == null)
    return false;
```

This accurately indicates that the fast pointer reached the end of the list, meaning no cycle exists.

---

# Why don't we check

```java
slowPointer.next != null
```

Consider a single-node list:

```
1 -> null
```

The while loop condition is

```java
while(fastPointer != null && fastPointer.next != null)
```

Here,

```
fastPointer != null      ✓
fastPointer.next != null ✗
```

The loop never executes.

Therefore,

```java
slowPointer = slowPointer.next;
```

is never reached.

Likewise, for any acyclic list, the fast pointer reaches the end before the slow pointer, so checking the fast pointer alone guarantees that advancing the slow pointer by one step is always safe.

---

# Dry Run

## Example 1 (No Cycle)

```
1 -> 2 -> 3 -> null
```

```
Initial

S = 1
F = 1
```

Iteration 1

```
S = 2
F = 3
```

Next Iteration

```
F.next == null

Exit Loop

Return false
```

---

## Example 2 (Cycle Exists)

```
1 -> 2 -> 3 -> 4
     ^         |
     |_________|
```

```
Initial

S = 1
F = 1
```

Iteration 1

```
S = 2
F = 3
```

Iteration 2

```
S = 3
F = 2
```

Iteration 3

```
S = 4
F = 4
```

Pointers meet.

Return

```
true
```

---

# Java Implementation

```java
/**
 * Definition for singly-linked list.
 * class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
public class Solution {
    /*public boolean hasCycle(ListNode head) {
        HashSet<ListNode> hset= new HashSet<>();
        ListNode temp=head;
        while(temp!=null){
            if(hset.contains(temp))
                return true;
            hset.add(temp);
            temp=temp.next;
        }
        return false;
    }*/

    public boolean hasCycle(ListNode head) {
        if(head==null ||head.next==null)
            return false;

        ListNode slowPointer=head, fastPointer=head;
        while(fastPointer!=null && fastPointer.next!=null){
            slowPointer=slowPointer.next;
            fastPointer=fastPointer.next.next;
            if(slowPointer==fastPointer)
                break;
        }

        //1->2->3->null
        /*We check the fast pointer because it's the one advancing two nodes at a time. In an acyclic list, it reaches the end first, which tells us there is no cycle. The slow pointer cannot reliably indicate the end of the list because it progresses only one node at a time.
        */
        if(fastPointer==null || fastPointer.next==null)
            return false;

      /*  fastPointer=head;
        while(slowPointer!=fastPointer){
            slowPointer=slowPointer.next;
            fastPointer=fastPointer.next;
        }
        System.out.println("Loop is here :"+slowPointer.val); */
        return true;
    }
}
```

---

# Interview Takeaways

- HashSet solution is simple but uses **O(n)** extra space.
- Floyd's Cycle Detection uses **O(1)** extra space.
- The fast pointer determines whether the list terminates.
- Always check `fast != null && fast.next != null` before advancing the fast pointer.
- If the pointers meet, a cycle exists.
- If the fast pointer reaches the end, the list is acyclic.
- The commented second phase in the implementation is used in **Linked List Cycle II (LeetCode 142)** to find the starting node of the cycle after a meeting point has been detected.

# Reverse Linked List (Iterative)

## Problem

Reverse a singly linked list.

```
Input

1 → 2 → 3 → 4 → null
```

```
Output

4 → 3 → 2 → 1 → null
```

---

## Approach

Maintain three pointers:

- **prev** → Previous node
- **curr** → Current node
- **nextNode** → Stores the next node before reversing the link

At each iteration:

1. Save the next node.
2. Reverse the current node's pointer.
3. Move all pointers forward.

---

## Algorithm

```
prev = null
curr = head

while(curr != null)

    nextNode = curr.next
    curr.next = prev

    prev = curr
    curr = nextNode

return prev
```

---

## Dry Run

```
Initial

prev = null

curr = 1 → 2 → 3 → 4
```

### Iteration 1

```
next = 2

1 → null

prev = 1

curr = 2
```

### Iteration 2

```
2 → 1 → null

prev = 2

curr = 3
```

### Iteration 3

```
3 → 2 → 1 → null

prev = 3

curr = 4
```

### Iteration 4

```
4 → 3 → 2 → 1 → null

prev = 4

curr = null
```

Return:

```
prev
```

---

## Java Implementation

```java
class Solution {

    /*public ListNode reverseListIterative(ListNode head) {
        ListNode prev=null,nextNode=null;
        ListNode curr=head;
        while(curr!=null){
            nextNode=curr.next;
            curr.next=prev;
            prev=curr;
            curr=nextNode;
        }
        return prev;
    }*/
}
```

---

## Why Do We Need `nextNode`?

Before reversing:

```
1 → 2 → 3
```

If we execute:

```java
curr.next = prev;
```

the original link to `2` is lost.

Therefore, we first save:

```java
nextNode = curr.next;
```

before changing the pointer.

---

## Complexity

**Time:** `O(n)`

**Space:** `O(1)`

---

## Interview Tips

- Always save `curr.next` before reversing the pointer.
- `prev` becomes the new head after the loop.
- Uses **three pointers**: `prev`, `curr`, and `nextNode`.
- Optimal solution with **O(n)** time and **O(1)** space.

# Palindrome Linked List

## Problem

Determine whether a singly linked list is a palindrome.

Example

```
Input

1 → 2 → 2 → 1
```

```
Output

true
```

---

## Approach

We solve this in **O(n)** time and **O(1)** space.

Steps:

1. Find the middle of the linked list.
2. Reverse the second half.
3. Compare the first half with the reversed second half.
4. Restore the original list by reversing the second half again.

---

## Algorithm

```
Find middle node

↓

Reverse second half

↓

Compare both halves

↓

Restore original list

↓

Return result
```

---

## Dry Run

Input

```
1 → 2 → 2 → 1
```

### Step 1 - Find Middle

```
slow = 2 (third node)
```

```
1 → 2 → 2 → 1
          ↑
       middle
```

---

### Step 2 - Reverse Second Half

```
Original

2 → 1
```

becomes

```
1 → 2
```

Now we have

```
First Half

1 → 2

Second Half

1 → 2
```

---

### Step 3 - Compare

```
1 == 1 ✓

2 == 2 ✓
```

Palindrome.

---

### Step 4 - Restore

Reverse the second half again.

Original list becomes

```
1 → 2 → 2 → 1
```

---

## Java Implementation

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public boolean isPalindrome(ListNode head) {
        if(head==null)
            return true;

        //head 1->2-> 2<-1 SHR
        ListNode middleNode= middleNode(head);
        ListNode secondHalfReversed= reverseListIterative(middleNode);
        ListNode p1=head, p2=secondHalfReversed;
        boolean flag=true;
        while(p2!=null){
            if(p1.val!=p2.val){
                flag=false;
                break;
            }
            p1=p1.next;
            p2=p2.next;
        }
        reverseListIterative(secondHalfReversed);
        return flag;
    }


    public ListNode middleNode(ListNode head) {
        ListNode slowPointer=head;
        ListNode fastPointer=head;

        while(fastPointer!=null && fastPointer.next!=null){
            slowPointer=slowPointer.next;
            fastPointer=fastPointer.next.next;
        }
        return slowPointer;
    }

    public ListNode reverseListIterative(ListNode head) {
        ListNode prev=null,nextNode=null;
        ListNode curr=head;
        while(curr!=null){
            nextNode=curr.next;
            curr.next=prev;
            prev=curr;
            curr=nextNode;
        }
        return prev;
    }
}
```

---

## Complexity

**Time:** `O(n)`

- Find middle → `O(n)`
- Reverse second half → `O(n)`
- Compare → `O(n)`
- Restore → `O(n)`

Overall: **O(n)**

**Space:** `O(1)`

---

## Interview Tips

- Use the **Fast & Slow Pointer** technique to find the middle.
- Reverse only the **second half** of the list.
- Compare node by node.
- Restore the list if the original structure should remain unchanged (good practice in interviews).
- Combines two common linked list patterns:
  - Find Middle Node
  - Reverse Linked List

### Why do we compare until `p2 == null`?

We compare until the second half is exhausted because `p2` traverses the reversed second half of the list.

- **Even-length list:** The reversed second half contains exactly half of the nodes.
- **Odd-length list:** The reversed second half contains the middle node plus the remaining nodes. Comparing the middle node with itself is harmless since it always matches.

Therefore, once `p2` reaches `null`, we have compared every node required to determine whether the list is a palindrome.

**Example (Even Length)**

```
1 → 2 → 2 → 1

First Half:      1 → 2
Second Half:     1 → 2 (after reversal)

Compare:
1 == 1 ✓
2 == 2 ✓

p2 becomes null → Done
```

**Example (Odd Length)**

```
1 → 2 → 3 → 2 → 1

Middle = 3

Reverse second half:

3 → 2 → 1

becomes

1 → 2 → 3

Compare:

1 == 1 ✓
2 == 2 ✓
3 == 3 ✓

p2 becomes null → Done
```

> **Interview Insight:**  
> The stopping condition is **not** because the reversed list ends in `null` (every linked list does). It is because `p2` represents the **entire reversed second half** of the original list. Once we've traversed all nodes in the second half, we've compared every node necessary to determine whether the list is a palindrome.  


# Intersection of Two Linked Lists

## Problem

Given the heads of two singly linked lists, return the node where the two lists intersect.

If no intersection exists, return `null`.

---

## Solution 1 - HashSet

### Approach

- Traverse the first linked list and store every node in a `HashSet`.
- Traverse the second linked list.
- The first node already present in the set is the intersection.

### Complexity

**Time:** `O(m + n)`

**Space:** `O(m)`

---

## Solution 2 - Length Difference (Optimal)

### Approach

1. Find the length of both linked lists.
2. Advance the pointer of the longer list by the difference in lengths.
3. Traverse both lists together.
4. The first common node (`==`) is the intersection.

> **Note:** Compare node references (`==`), **not** node values.

---

## Algorithm

```
Find length of both lists

↓

Advance longer list by |m-n|

↓

Traverse both lists together

↓

If pointers are equal

Return intersection node

↓

Otherwise return null
```

---

## Example

```
List A

1 → 2 → 3 → 4 → 5

Length = 5
```

```
List B

9 → 4 → 5

Length = 3
```

Difference

```
5 - 3 = 2
```

Advance List A by two nodes.

```
A

3 → 4 → 5

B

9 → 4 → 5
```

Move both pointers together.

```
3 != 9

↓

4 == 4

Intersection Found
```

---

## Why Does This Work?

After advancing the longer list by the length difference, both pointers have the **same number of nodes remaining**.

If an intersection exists, both pointers will reach it at the same time.

Otherwise, both pointers will reach `null`.

---

## Java Implementation

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode(int x) {
 *         val = x;
 *         next = null;
 *     }
 * }
 */
public class Solution {
   /* public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        Set<ListNode> hset= new HashSet<>();
        ListNode pA= headA;
        while(pA!=null){
            hset.add(pA);
            pA=pA.next;
        }

        ListNode pB=headB;
        while(pB!=null){
            if(hset.contains(pB))
                return pB;
            pB=pB.next;
        }
        return null;
    }*/

    public ListNode getIntersectionNode(ListNode headA, ListNode headB) {
        int lengthA= length(headA);
        int lengthB=length(headB);
        int diff= Math.abs(lengthA-lengthB);
        ListNode longerLL= lengthA>=lengthB? headA:headB;
        ListNode shorterLL= lengthA<lengthB?headA:headB;

        for(int i=0;i<diff;i++){
            longerLL=longerLL.next;
        }

        while(longerLL!=null && shorterLL!=null){
            if(longerLL==shorterLL)
                return longerLL;

            longerLL=longerLL.next;
            shorterLL=shorterLL.next;
        }

        return null;
    }

    public int length(ListNode head){
        ListNode temp=head;
        int length=0;
        while(temp!=null){
            length++;
            temp=temp.next;
        }
        return length;
    }
}
```

---

## Complexity

### HashSet

- **Time:** `O(m + n)`
- **Space:** `O(m)`

### Length Difference

- **Time:** `O(m + n)`
- **Space:** `O(1)`

---

## Interview Tips

- Compare **node references (`==`)**, not node values.
- Align both lists before traversing.
- After alignment, both pointers have the same distance from the end.
- If no intersection exists, both pointers eventually become `null`.
- This is the optimal solution with **O(m + n)** time and **O(1)** extra space.

# Plus One Linked List

## Problem

A non-negative integer is represented as a singly linked list where each node contains a single digit.

Add one to the number and return the updated linked list.

Example

```
Input

1 → 2 → 9
```

```
Output

1 → 3 → 0
```

---

## Key Observation

The carry propagates through all trailing `9`s.

Instead of adding from the last node, we:

1. Find the **rightmost node that is not 9**.
2. Increment it.
3. Set all nodes after it to `0`.

---

# Solution 1 (Without Dummy Node)

### Approach

- Find the rightmost non-nine node.
- If none exists, create a new head with value `1`.
- Increment the rightmost non-nine node.
- Change all following nodes to `0`.

### Java Implementation

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode plusOne(ListNode head) {
        //9,9,9
        //first rightmost non nine digit
        //increment by one
        //if not there create a new carry and change head..
        ListNode temp=head;
        ListNode rightMostNonNineNode=null;

        while(temp!=null){
            if(temp.val!=9){
                rightMostNonNineNode=temp;
            }
            temp=temp.next;
        }

        if(rightMostNonNineNode==null){
            rightMostNonNineNode= new ListNode(1);
            rightMostNonNineNode.next=head;
            head=rightMostNonNineNode;
        }
        else{
            rightMostNonNineNode.val++;
        }

        temp=rightMostNonNineNode.next;

        while(temp!=null){
            temp.val=0;
            temp=temp.next;
        }

        return head;
    }
}
```

---

# Solution 2 (Using Dummy Node)

### Approach

Introduce a dummy node before the head.

The dummy node naturally handles the edge case where all digits are `9`, eliminating the need for a separate `if` condition.

### Java Implementation

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode plusOne(ListNode head) {
        ListNode dummyNode= new ListNode(0);
        dummyNode.next=head;
        ListNode temp=dummyNode;
        ListNode rightMostNonNineNode=null;

        while(temp!=null){
            if(temp.val!=9){
                rightMostNonNineNode=temp;
            }
            temp=temp.next;
        }

        rightMostNonNineNode.val++;

        temp=rightMostNonNineNode.next;

        while(temp!=null){
            temp.val=0;
            temp=temp.next;
        }

        return dummyNode.val==0 ? dummyNode.next : dummyNode;
    }
}
```

---

## Example 1

```
1 → 2 → 9 → 9
```

Rightmost non-nine:

```
2
```

Increment:

```
1 → 3 → 9 → 9
```

Set remaining digits to zero:

```
1 → 3 → 0 → 0
```

---

## Example 2

```
9 → 9 → 9
```

### Solution 1

```
No non-nine node found

↓

Create new head

↓

1 → 9 → 9 → 9

↓

1 → 0 → 0 → 0
```

### Solution 2

```
Dummy

0 → 9 → 9 → 9

↓

Increment dummy

1 → 9 → 9 → 9

↓

Set remaining digits to zero

1 → 0 → 0 → 0
```

---

## Complexity

**Time:** `O(n)`

**Space:** `O(1)`

---

## Interview Tips

- The carry only propagates until the **rightmost non-nine digit**.
- Every trailing `9` becomes `0`.
- The dummy node solution removes the explicit edge case for an all-9s list and is generally considered the cleaner implementation.
```
# Add Two Numbers

## Problem

Two non-empty linked lists represent two non-negative integers.

- Digits are stored in **reverse order**.
- Each node contains a single digit.
- Return the sum as a linked list.

Example

```
Input

2 → 4 → 3

5 → 6 → 4
```

```
Output

7 → 0 → 8
```

Because

```
342 + 465 = 807
```

---

## Approach

Simulate elementary addition.

For each pair of digits:

- Add both digits and the carry.
- Create a new node with the current digit.
- Carry forward the remaining value.

A **dummy node** is used to simplify linked list construction.

---

## Algorithm

```
Initialize

dummy
curr = dummy
carry = 0

↓

While either list has nodes

    value1 = l1.val (or 0)
    value2 = l2.val (or 0)

    sum = value1 + value2 + carry

    digit = sum % 10
    carry = sum / 10

    Append digit

↓

If carry remains

Append carry

↓

Return dummy.next
```

---

## Example

```
2 → 4 → 3

5 → 6 → 4
```

### Iteration 1

```
2 + 5 + 0 = 7

Digit = 7

Carry = 0

Result

7
```

---

### Iteration 2

```
4 + 6 + 0 = 10

Digit = 0

Carry = 1

Result

7 → 0
```

---

### Iteration 3

```
3 + 4 + 1 = 8

Digit = 8

Carry = 0

Result

7 → 0 → 8
```

---

## Example (Carry Remaining)

```
9 → 9 → 9

1
```

Iterations

```
9 + 1 = 10

↓

0 Carry 1

↓

9 + 0 + 1 = 10

↓

0 Carry 1

↓

9 + 0 + 1 = 10

↓

0 Carry 1
```

Carry still remains.

Append one final node.

Result

```
0 → 0 → 0 → 1
```

---

## Java Implementation

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy= new ListNode(0);
        ListNode l=dummy;
        int val1=0,val2=0,sum=0,digit=0,carry=0;

        // less restrictive
        while(l1!=null || l2!=null){
            val1=l1==null?0:l1.val;
            val2=l2==null?0:l2.val;

            sum=val1+val2+carry;

            digit=sum%10;
            carry=sum/10;

            l.next=new ListNode(digit);
            l=l.next;

            if(l1!=null)
                l1=l1.next;

            if(l2!=null)
                l2=l2.next;
        }

        // Handles cases like 999 + 1
        if(carry!=0)
            l.next=new ListNode(carry);
		//in the while loop you could add (l1!=null||l2!=null||carry!=0), then you wouldnt need this	

        return dummy.next;
    }
}
```

---

## Why Use a Dummy Node?

Without a dummy node, the first node of the result requires special handling.

Using a dummy node lets us use the same logic for every digit.

Pattern:

```java
ListNode dummy = new ListNode(0);
ListNode curr = dummy;

curr.next = new ListNode(value);
curr = curr.next;

return dummy.next;
```

---

## Why Use `||` Instead of `&&`?

We continue as long as **either** list has remaining digits.

Example

```
999

+

1
```

Even after the shorter list ends, we still need to process the remaining digits of the longer list.

Hence:

```java
while(l1 != null || l2 != null)
```

---

## Complexity

**Time:** `O(max(m, n))`

**Space:** `O(max(m, n))` *(output list)*

---

## Interview Tips

- Use a **dummy node** to simplify linked list construction.
- Continue while **either** list has nodes.
- Treat missing digits as `0`.
- Always append a final carry if it exists.
- This is the standard optimal solution with **O(max(m, n))** time.

## Why is the Time Complexity `O(max(m, n))` and not `O(m + n)`?

For this problem, both linked lists are traversed **simultaneously**.

```java
while(l1 != null || l2 != null)
```

During each iteration:

- `l1` moves one step (if not `null`)
- `l2` moves one step (if not `null`)

Therefore, the loop continues until the **longer** linked list is exhausted.

### Example

```
l1

1 → 2 → 3 → 4 → 5

Length = 5
```

```
l2

9 → 8 → 7

Length = 3
```

Iterations:

```
1

2

3

4

5
```

Total iterations = **5**, not **8**.

Hence,

```
Time = O(max(m, n))
```

---

### Why was **Intersection of Two Linked Lists** `O(m + n)`?

In the intersection problem, we first traverse each list independently to compute their lengths.

```
Traverse List A

O(m)

+

Traverse List B

O(n)
```

These are **separate traversals**, so the total work is

```
O(m + n)
```

---

### Rule of Thumb

- **Sequential traversals of two independent linked lists** → `O(m + n)`
- **Simultaneous traversal of two linked lists using OR** → `O(max(m, n))`
- **Simultaneous traversal of two linked lists using AND** → `O(min(m, n))`


> **Note:** `O(max(m, n))` is a tighter bound. Since `max(m, n) ≤ m + n`, writing `O(m + n)` is also technically correct, but `O(max(m, n))` more accurately reflects the actual number of iterations.


# Add Two Numbers II (MSB First)

## Problem

The digits are stored in **forward order (MSB at the head)**.

Return the sum as a linked list.

Example

```
Input

7 → 2 → 4 → 3

5 → 6 → 4
```

Output

```
7 → 8 → 0 → 7
```

---

## Approach

Since addition starts from the least significant digit, push the digits of both linked lists onto stacks.

- Pop digits from both stacks.
- Compute the sum and carry.
- Insert each new digit at the **front** of the result list.

---

## Algorithm

```
Push all digits of l1 into Stack1

↓

Push all digits of l2 into Stack2

↓

While either stack has elements or carry exists

    Pop values

    Add carry

    Create new head node

↓

Return result head
```

---

## Java Implementation

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        Stack<Integer> st1= new Stack<>();
        Stack<Integer> st2= new Stack<>();
        ListNode resultHead=null;
        int val1,val2,sum,digit,carry=0;

        while(l1!=null){
            st1.push(l1.val);
            l1=l1.next;
        }

        while(l2!=null){
            st2.push(l2.val);
            l2=l2.next;
        }

        while(!st1.isEmpty() || !st2.isEmpty() || carry!=0){
            val1=st1.isEmpty()?0:st1.pop();
            val2=st2.isEmpty()?0:st2.pop();

            sum=val1+val2+carry;
            digit=sum%10;
            carry=sum/10;

            ListNode newHead= new ListNode(digit);
            newHead.next=resultHead;
            resultHead=newHead;
        }

        return resultHead;
    }
}
```

---

## Complexity

**Time:** `O(m + n)`

- Push to stacks → `O(m + n)`
- Addition → `O(max(m, n))`
- Build result → `O(max(m, n))`

Overall:

```
O(m + n)
```

**Space:** `O(m + n)`

---

## Interview Tips

- Use stacks because addition starts from the least significant digit.
- Insert each computed digit at the **front** of the result list.
- Continue while **either stack has elements or carry exists**.
- If modifying the input lists is allowed, reversing both lists is another `O(m + n)` solution with `O(1)` extra space.


# Merge Two Sorted Linked Lists

## Problem

Given the heads of two sorted linked lists, merge them into a single sorted linked list.

The merged list should be created by **reusing the existing nodes**.

Example

```
List 1

1 → 2 → 4
```

```
List 2

1 → 3 → 4
```

Output

```
1 → 1 → 2 → 3 → 4 → 4
```

---

## Approach

Use a **dummy node** to simplify construction of the merged list.

Maintain a pointer (`result`) pointing to the last node of the merged list.

- Compare the current nodes of both lists.
- Append the smaller node.
- Move the corresponding pointer forward.
- Continue until one list is exhausted.
- Attach the remaining nodes.

---

## Algorithm

```
Create dummy node

↓

result = dummy

↓

While both lists are not empty

    Compare current nodes

    Append smaller node

    Move corresponding pointer

    Move result

↓

Attach remaining list

↓

Return dummy.next
```

---

## Example

```
List1

1 → 2 → 4
```

```
List2

1 → 3 → 4
```

### Step 1

```
1 <= 1

Append List1 node

Result

1
```

### Step 2

```
1 < 2

Append List2 node

Result

1 → 1
```

### Step 3

```
2 < 3

Result

1 → 1 → 2
```

### Step 4

```
3 < 4

Result

1 → 1 → 2 → 3
```

### Remaining Nodes

```
4 → 4
```

Attach directly.

Final Result

```
1 → 1 → 2 → 3 → 4 → 4
```

---

## Java Implementation

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy= new ListNode(0);
        ListNode result=dummy;

        //dummy->1->1->3->4
        //Reusing the existing nodes. We only update the next pointers
        //to build the merged list. No new nodes are created.

        while(list1!=null && list2!=null){
            if(list1.val<=list2.val){
                result.next=list1;
                list1=list1.next;
            }
            else{
                result.next=list2;
                list2=list2.next;
            }
            result=result.next;
        }

        if(list1!=null){
            result.next=list1;
        }

        if(list2!=null){
            result.next=list2;
        }

        return dummy.next;
    }
}
```

---

## Why Use `&&`?

We continue while **both** lists have nodes remaining.

```java
while(list1 != null && list2 != null)
```

As soon as one list is exhausted, the remaining nodes in the other list are already sorted and can be attached directly.

---

## Complexity

**Time:** `O(m + n)`

Every node from both linked lists is visited exactly once.

**Space:** `O(1)`

The algorithm reuses the existing nodes and only uses a few pointers.

---

## Interview Tips

- Use a **dummy node** to simplify linked list construction.
- Reuse the existing nodes instead of creating new ones.
- Compare **node values** to decide which node to append.
- Attach the remaining list once one list becomes empty.
- This is the standard optimal solution with **O(m + n)** time and **O(1)** extra space.

## Why Reuse the Existing Nodes?

Instead of creating new nodes, we reuse the nodes already present in the input linked lists.

Example

```
List1

1 → 2 → 4
```

```
List2

1 → 3 → 4
```

When we write

```java
result.next = list1;
```

or

```java
result.next = list2;
```

we are **linking an existing node** into the merged list.

Then we advance the corresponding pointer.

```java
list1 = list1.next;
```

or

```java
list2 = list2.next;
```

No new nodes are created.

Only the **`next` pointers are rearranged** to build the merged list.

This is why the algorithm uses only **O(1)** extra space.

> **Note:** Since we reuse the original nodes, the original linked lists are **not preserved** as separate lists after merging.

# Remove Duplicates from an Unsorted Linked List

## Problem

Given an unsorted linked list, remove duplicate nodes while preserving the first occurrence.

Example

```
Input

1 → 4 → 6 → 1 → 6 → 5 → 4
```

Output

```
1 → 4 → 6 → 5
```

---

# Solution 1 - Using HashSet

## Approach

Maintain a `HashSet` of visited values.

- If the next node is already present in the set, remove it.
- Otherwise, add it to the set and continue.

This preserves the first occurrence of every element.

---

## Algorithm

```
Initialize HashSet

↓

Traverse the list

↓

Duplicate?

Yes → Delete node

No → Add to HashSet

↓

Continue
```

---

## Java Implementation

```java
public ListNode removeDups(ListNode head){

    if(head==null)
        return null;

    ListNode temp=head;
    Set<Integer> hset= new HashSet<>();

    hset.add(head.val);

    while(temp.next!=null){

        if(hset.contains(temp.next.val)){
            temp.next=temp.next.next;
        }
        else{
            hset.add(temp.next.val);
            temp=temp.next;
        }
    }

    return head;
}
```

---

## Why don't we move `temp` after deleting?

Suppose

```
1 → 2 → 2 → 2 → 3
```

After deleting the first duplicate

```
1 → 2 → 2 → 3
```

If we moved `temp`, we would skip checking the next `2`.

Therefore,

```java
if(duplicate)
    temp.next=temp.next.next;
else
    temp=temp.next;
```

---

## Complexity

**Time:** `O(n)`

**Space:** `O(n)`

---

# Solution 2 - Runner Technique (Without Extra Space)

## Approach

Use two pointers.

- `current` picks one node.
- `runner` scans all remaining nodes.
- Whenever a duplicate is found, remove it.

Repeat for every node.

---

## Algorithm

```
current = head

↓

runner = current

↓

Compare current with every remaining node

↓

Delete duplicates

↓

Move current
```

---

## Java Implementation

```java
public ListNode removeDupsWithoutSpace(ListNode head){

    if(head==null)
        return null;

    ListNode current=head, runner=null;

    while(current!=null){

        runner=current;

        while(runner.next!=null){

            if(current.val==runner.next.val){
                runner.next=runner.next.next;
            }
            else{
                runner=runner.next;
            }
        }

        current=current.next;
    }

    return head;
}
```

---

## Why does `runner` start from `current`?

Suppose

```
1 → 4 → 1 → 6 → 1
```

```
current = first 1

runner = current
```

The inner loop starts checking from

```
runner.next
```

Comparisons become

```
4

↓

1 ✓ Duplicate

↓

6

↓

1 ✓ Duplicate
```

This avoids comparing `current` with itself while checking every remaining node.

---

## Complexity

### HashSet Solution

- **Time:** `O(n)`
- **Space:** `O(n)`

### Runner Technique

- **Time:** `O(n²)`
- **Space:** `O(1)`

---

## Interview Tips

- If extra space is allowed, use a **HashSet** for an `O(n)` solution.
- Without extra space, use the **Runner Technique** (`O(n²)`).
- After deleting a duplicate, **do not move** the pointer that performed the deletion.
- The runner technique is a common interview pattern for linked list problems where additional memory is not allowed.

## Utility Methods

### Print Linked List

```java
public void printList(ListNode head){
    ListNode temp = head;

    while(temp != null){
        System.out.print(temp.val);

        if(temp.next != null){
            System.out.print(" -> ");
        }

        temp = temp.next;
    }

    System.out.println();
}
```

Example

```
Input

1 → 4 → 6 → 5
```

Output

```
1 -> 4 -> 6 -> 5
```

---

### Insert at End

```java
public void insertAtEnd(ListNode head, int val){
    ListNode temp = head;

    while(temp.next != null){
        temp = temp.next;
    }

    temp.next = new ListNode(val);
}
```

Example

```
Before

1 → 2 → 3
```

```
insertAtEnd(head, 4)
```

```
After

1 → 2 → 3 → 4
```

# Delete Middle Node

## Problem

You are given **only a reference to a node** in a singly linked list.

Delete that node.

> **Constraint:** The given node is **not** the tail (last) node.

Example

```
Input

1 → 2 → 3 → 4 → 5
        ↑
      node
```

Output

```
1 → 2 → 4 → 5
```

---

## Approach

Since we are **not given the head** of the linked list, we cannot access the previous node.

Instead:

1. Copy the value of the next node into the current node.
2. Skip the next node.

---

## Java Implementation

```java
public void deleteMiddleNode(ListNode node){

    if(node == null || node.next == null)
        return;

    ListNode nextNode = node.next;

    node.val = nextNode.val;
    node.next = nextNode.next;
}
```

---

## Why Can't We Delete the Last Node?

Suppose we are given only a reference to the last node:

```
1 → 2 → 3
        ↑
      node
```

If we do:

```java
node = null;
```

we are **only changing the local reference variable**.

The node containing `3` still exists in memory because the previous node (`2`) still points to it.

```
2.next ─────► 3
```

The correct way to delete the last node would be:

```java
previous.next = null;
```

However, we **cannot** do this because we are **not given a reference to the previous node**—only the node to be deleted.

For a **non-tail node**, we can instead modify the node itself:

```java
node.val = node.next.val;
node.next = node.next.next;
```

This works because we have access to the next node, allowing us to copy its value and bypass it.

> **Key Takeaway:**  
> `node = null` only changes the **local reference**. It does **not** modify the linked list because the previous node still points to the original node.

---

## What If the Given Node Is the First Node?

The algorithm still works because the first node is **not** the tail.

Example

```
Before

1 → 2 → 3 → 4
↑
node
```

Copy the next node's value:

```
2 → 2 → 3 → 4
↑
node
```

Skip the next node:

```
2 → 3 → 4
↑
node
```

Notice that the first node itself is **not removed**. Instead:

- Its value changes from `1` to `2`.
- The original second node is bypassed.

From the caller's perspective, it appears as though the head node was deleted.

> **Therefore, this algorithm works for any non-tail node (including the first node), but it can never work for the last node.**

---

## Complexity

**Time:** `O(1)`

**Space:** `O(1)`

---

## Interview Tips

- This problem is really **"Delete a Non-Tail Node"**, not just a middle node.
- The algorithm works for:
  - ✅ First node
  - ✅ Any middle node
- The algorithm does **not** work for:
  - ❌ Last node
- We are **modifying the current node**, not deleting it directly.
- We cannot delete the last node because we have no reference to the previous node.
- Remember the distinction:
  - `node = null` → Changes only the local reference.
  - `node.next = ...` or `node.val = ...` → Modifies the actual linked list.
  - Delete a non tail node given the reference of the node in O(1)


---


# Trees

Trees are one of the most important data structures for coding interviews. Many interview problems involving recursion, DFS, and BSTs are based on trees.

---

# Tree Terminology

- **Root Node** – Top-most node in the tree.
- **Parent Node** – A node having one or more children.
- **Child Node** – A node directly connected below a parent.
- **Leaf Node** – A node with no children.
- **Subtree** – A tree rooted at any node.
- **Height** – Number of edges on the longest path from the node to a leaf.
- **Depth** – Number of edges from the root to a node.

---

# Properties of Trees

- Have a single **root node**.
- Every node can have **zero or more children**.
- There is exactly **one unique path** between any two nodes.
- Trees **cannot contain cycles**.
- Child nodes may or may not maintain a reference to their parent.

---

# N-ary Tree

Each node can have any number of children.

```java
class Node {
    int val;
    List<Node> children;
}
```

Example

```
        1
     /  |  \
    2   3   4
       / \
      5   6
```

---

# Binary Tree

A **Binary Tree** is a tree where every node has **at most two children**.

```java
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;
}
```

Example

```
        10
       /  \
      5    20
     / \
    3   7
```

---

# Perfect Binary Tree

A binary tree where:

- Every internal node has exactly **2 children**.
- All leaf nodes are at the same level.

Example

```
        1
      /   \
     2     3
    / \   / \
   4  5  6   7
```

Properties

- Total Nodes = **2^(h+1) - 1**
- Leaf Nodes = **2^h**

where **h** is the height of the tree.

---

# Complete Binary Tree

A binary tree where:

- Every level is completely filled except possibly the last.
- The last level is filled from **left to right**.

Example

```
        1
      /   \
     2     3
    / \   /
   4  5  6
```

---

# Full Binary Tree

A binary tree where every node has either:

- 0 children, or
- 2 children.

No node has only one child.

---

# Balanced Binary Tree

A binary tree is balanced if, for every node,

```
|Height(left) - Height(right)| <= 1
```

Why is this important?

Balanced trees provide

```
Search

Insert

Delete

↓

O(log N)
```

Examples

- AVL Tree
- Red-Black Tree

---

# Binary Search Tree (BST)

A Binary Search Tree is a **special type of Binary Tree**.

> Every BST is a Binary Tree, but **not every Binary Tree is a BST.**

BST Property

For every node:

```
Maximum value in Left Subtree
        <
Current Node
        <
Minimum value in Right Subtree
```

or equivalently,

```
Left Subtree < Node < Right Subtree
```

Example

```
        8
      /   \
     3     10
    / \      \
   1   6      14
      / \     /
     4   7   13
```

Searching in a balanced BST takes

```
O(log N)
```

---

# Graph vs Tree

## Tree

- No cycles
- Exactly one path between two nodes
- Always connected
- One root node

## Graph

- May contain cycles
- Can be directed or undirected
- May be disconnected
- May have multiple paths between nodes

---

# Tree Traversals

## Depth First Search (DFS)

Explore one branch completely before backtracking.

Recursive traversals:

- Preorder
- Inorder
- Postorder

Uses the **recursive call stack** (or an explicit stack).

---

## Breadth First Search (BFS)

Visit all nodes at the same level before moving to the next level.

Also called

```
Level Order Traversal
```

Uses a **Queue**.

Applications

- Shortest path in unweighted graphs
- Social media friend recommendations
- Level-wise processing

---

# DFS Traversals

## Preorder

```
Root

↓

Left

↓

Right
```

Use Cases

- Serialize trees
- Copy trees
- Prefix expressions

---

## Inorder

```
Left

↓

Root

↓

Right
```

For a BST, Inorder traversal always produces

```
Sorted Order
```

---

## Postorder

```
Left

↓

Right

↓

Root
```

Use Cases

- Delete tree
- Evaluate expression trees

---

# BFS Traversal

```
Level 0

↓

Level 1

↓

Level 2

↓

...
```

Uses a Queue.

Time

```
O(N)
```

Space

```
O(N)
```

---

# Common Interview Problems

## Binary Tree

- Maximum Depth
- Minimum Depth
- Diameter of Binary Tree
- Balanced Binary Tree
- Lowest Common Ancestor (LCA)
- Invert Binary Tree
- Symmetric Tree
- Same Tree
- Path Sum
- Maximum Path Sum
- Level Order Traversal
- Zigzag Traversal
- Vertical Order Traversal

---

## Binary Search Tree

- Search
- Insert
- Delete
- Validate BST
- Lowest Common Ancestor
- Kth Smallest Element
- BST Iterator
- Recover BST
- Convert Sorted Array to BST

---

# Time Complexities

| Operation | Balanced BST | Skewed BST |
|-----------|-------------:|-----------:|
| Search | O(log N) | O(N) |
| Insert | O(log N) | O(N) |
| Delete | O(log N) | O(N) |

---

# Interview Tips

- A **Binary Search Tree** is **not** the same as a Binary Tree.
- A **Balanced BST** gives `O(log N)` operations.
- **Inorder Traversal of a BST is always sorted.**
- DFS generally uses recursion (or a stack).
- BFS always uses a queue.
- Learn both recursive and iterative traversals.
- Diameter, LCA, Balanced Tree, BST Validation, Insert, Delete, and BST Iterator are among the highest-frequency tree interview questions.

---

# Height vs Depth

## Depth

**Depth** tells you how far a node is from the **root**.

- Measured **top → down**
- Root has depth **0**

Example

```
        A
      /   \
     B     C
    / \
   D   E
```

| Node | Depth |
|------|------:|
| A | 0 |
| B | 1 |
| C | 1 |
| D | 2 |
| E | 2 |

> **Depth = Distance from the Root**

---

## Height

**Height** tells you how far a node is from its **deepest leaf**.

- Measured **bottom → up**
- Leaf nodes have height **0**

Example

```
        A
      /   \
     B     C
    / \
   D   E
```

| Node | Height |
|------|-------:|
| D | 0 |
| E | 0 |
| C | 0 |
| B | 1 |
| A | 2 |

> **Height = Longest distance to a Leaf**

---

## Memory Trick

- **Depth** → **Down** from the root.
- **Height** → How **High** the subtree is beneath a node.

---

# Types of Binary Trees

## Perfect Binary Tree

Every internal node has **exactly two children**, and **all leaf nodes are at the same level**.

```
        1
      /   \
     2     3
    / \   / \
   4  5  6   7
```

Properties

- Every non-leaf has exactly **2 children**
- All levels are completely filled
- Total Nodes = **2^(h+1) - 1**
- Leaf Nodes = **2^h**

> **Memory Trick:** Nothing is missing.

---

## Complete Binary Tree

Every level is completely filled **except possibly the last**.

The last level must be filled **from left to right**.

### Valid

```
        1
      /   \
     2     3
    / \   /
   4  5  6
```

Last level:

```
4  5  6
```

No gaps.

---

### Invalid

```
        1
      /   \
     2     3
    /     /
   4     6
```

Last level:

```
4  _  6
```

❌ There is a gap before `6`.

---

### Another Invalid Example

```
        1
      /   \
     2     3
          / \
         6   7
```

❌ The left subtree should have been filled first.

> **Memory Trick:** The last level fills **left → right**, just like people filling seats in a theater.

---

## Full Binary Tree

Every node has either:

- **0 children**, or
- **2 children**

No node has exactly one child.

### Valid

```
      1
     / \
    2   3
       / \
      4   5
```

### Invalid

```
      1
     /
    2
```

`1` has only one child.

---

# Visual Comparison

## Perfect

```
        1
      /   \
     2     3
    / \   / \
   4  5  6   7
```

✔ Full

✔ Complete

✔ Perfect

---

## Complete

```
        1
      /   \
     2     3
    / \   /
   4  5  6
```

✔ Complete

❌ Full

❌ Perfect

---

## Full but NOT Complete

```
        1
      /   \
     2     3
          / \
         6   7
```

✔ Full

❌ Complete

---

# Binary Search Tree Reminder

These tree classifications are **structural properties** and are unrelated to ordering.

A Binary Search Tree satisfies:

```
Left Subtree < Root < Right Subtree
```

A BST may or may not be:

- Complete
- Full
- Perfect
- Balanced

---

# Quick Revision

| Tree Type | Rule |
|------------|------|
| **Perfect** | Every internal node has 2 children and all levels are full |
| **Complete** | Only the last level may be incomplete, but it fills left → right |
| **Full** | Every node has either 0 or 2 children |
| **Balanced** | Height difference ≤ 1 at every node |
| **BST** | Left < Root < Right |

---

# Interview Memory Tricks

- **Perfect** → Nothing is missing.
- **Complete** → Last level fills **left → right**.
- **Full** → 0 or 2 children only.
- **Balanced** → Height difference ≤ 1.
- **BST** → Ordered by value (`Left < Root < Right`).
- Every node has exactly one **depth** (distance from the root).
- **Maximum Depth of a Tree** = Maximum depth among all nodes.
- **Height of a Tree** = Height of the root node.
- Under the standard convention (root depth = 0, leaf height = 0), the **Maximum Depth of the Tree** and the **Height of the Tree** are numerically equal.

# Recursive Tree Traversals

All recursive tree traversals follow the same template.

```java
if(root == null)
    return;
```

The only difference is **where we process the current node**.

---

## Preorder Traversal (Root → Left → Right)

```java
public List<Integer> preorderTraversal(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    preorderTraversal(root, result);
    return result;
}

public void preorderTraversal(TreeNode root, List<Integer> result){
    if(root == null)
        return;

    result.add(root.val);
    preorderTraversal(root.left, result);
    preorderTraversal(root.right, result);
}
```

---

## Inorder Traversal (Left → Root → Right)

```java
public void inorderTraversal(TreeNode root, List<Integer> result){
    if(root == null)
        return;

    inorderTraversal(root.left, result);
    result.add(root.val);
    inorderTraversal(root.right, result);
}
```

---

## Postorder Traversal (Left → Right → Root)

```java
public void postorderTraversal(TreeNode root, List<Integer> result){
    if(root == null)
        return;

    postorderTraversal(root.left, result);
    postorderTraversal(root.right, result);
    result.add(root.val);
}
```

---

## Complexity

- **Time:** `O(n)`
- **Space:** `O(h)` (Recursive Stack), where `h` is the height of the tree.

---

## Interview Tips

- **Preorder:** Root → Left → Right (**NLR**)
- **Inorder:** Left → Root → Right (**LNR**)
- **Postorder:** Left → Right → Root (**LRN**)
- For a **BST**, **Inorder Traversal** always produces the nodes in **sorted order**.

**Space:** `O(h)` (Recursive Call Stack), where `h` is the height of the tree.

- **Balanced Tree:** `O(log N)`
- **Worst Case (Skewed Tree):** `O(N)`

### Why is the Recursive Space Complexity `O(h)`?

The recursive call stack stores **only the current path from the root to the current node**, **not** all the nodes at the same level.

For example, consider the following tree:

```
        1
      /   \
     2     3
    / \   / \
   4  5  6   7
```

During a **Preorder DFS**, the recursive call stack evolves as follows.

Visit `1`

```
1
```

Visit `2`

```
1
2
```

Visit `4`

```
1
2
4
```

Node `4` is a leaf, so we return.

Stack becomes

```
1
2
```

Now visit `5`

```
1
2
5
```

Notice that we **never** have all nodes of the same level on the stack.

For example, the following never happens:

```
4
5
6
7
```

At any point, the stack contains only **one root-to-current-node path**.

Therefore,

```
Maximum Recursive Stack Size = Height of the Tree
```

Hence,

```
Space = O(h)
```

where `h` is the height of the tree.

---

### Balanced Tree

```
        1
      /   \
     2     3
    / \   / \
   4  5  6   7
```

Longest path:

```
1 → 2 → 4
```

Height = `O(log N)`

Recursive Space = `O(log N)`

---

### Skewed Tree

```
1
 \
  2
   \
    3
     \
      4
```

Recursive call stack:

```
1
2
3
4
```

Every node lies on the same path.

Height = `O(N)`

Recursive Space = `O(N)`

---

### DFS vs BFS Space
Space complexity is the max height of the stack in recursive calls
**DFS (Recursion)**

- Stores the **current root-to-node path**
- Space = `O(height)`

**BFS (Queue)**

- Stores **all nodes at the current level**
- Space = `O(width)` (Worst Case `O(N)`)


# Binary Tree Level Order Traversal (BFS)

Uses a **Queue** to process nodes level by level.

## Java Implementation (Null Marker Approach)

```java
class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();

        if(root == null)
            return result;

        Queue<TreeNode> q = new LinkedList<>();
        q.offer(root);
        q.offer(null);

        List<Integer> level = new ArrayList<>();

        while(!q.isEmpty()){

            TreeNode node = q.poll();

            if(node == null){

                result.add(level);
                level = new ArrayList<>();

                if(!q.isEmpty())
                    q.offer(null);
            }
            else{

                level.add(node.val);

                if(node.left != null)
                    q.offer(node.left);

                if(node.right != null)
                    q.offer(node.right);
            }
        }

        return result;
    }
}
```

---

## Complexity

**Time:** `O(N)`

Every node is enqueued and dequeued exactly once.

---

**Space:** `O(W)`

Where **W** is the **maximum width** of the tree (maximum number of nodes at any level).

### Balanced / Complete Binary Tree

```
            1
         /     \
        2       3
      /  \     /  \
     4    5   6    7
```

Queue progression:

```
[1]

↓

[2, 3]

↓

[4, 5, 6, 7]
```

The queue stores an entire level.

For a perfect binary tree,

```
Maximum Width ≈ N/2
```

Since constant factors are ignored,

```
O(N/2) = O(N)
```

So the **worst-case space complexity is `O(N)`**.

---

### Skewed Binary Tree

```
1
 \
  2
   \
    3
     \
      4
```

Queue progression:

```
[1]

↓

[2]

↓

[3]

↓

[4]
```

The queue never contains more than one node.

```
Maximum Width = 1
```

Space complexity:

```
O(1)
```

---

## DFS vs BFS Space

**DFS (Recursion)**

- Stores the current **root-to-node path**
- Space = `O(Height)`

**BFS (Queue)**

- Stores an entire **level**
- Space = `O(Maximum Width)`

---

## Interview Tips

- Level Order Traversal uses **Breadth First Search (BFS)**.
- Queue stores nodes **level by level**, not the entire tree.
- General Space Complexity = **`O(Maximum Width)`**
- Worst Case (Balanced / Complete Tree) = **`O(N)`**
- Best Case (Skewed Tree) = **`O(1)`**

---

## Linked List vs Skewed Binary Tree

A standard linked list is **not** a binary tree because each node has only one pointer (`next`).

```java
class ListNode {
    int val;
    ListNode next;
}
```

However, a linked list can be **represented** as a completely skewed binary tree by treating the `next` pointer as either the left or right child.

Example:

```
Linked List

1 → 2 → 3 → 4
```

Equivalent Right-Skewed Binary Tree

```
1
 \
  2
   \
    3
     \
      4
```

This is a valid binary tree because every node has at most two children, but each node happens to have only one.

> **Interview Note:** A linked list is a special case of a completely skewed binary tree from a structural perspective, although they are different data structures.

# Symmetric Tree

A tree is symmetric if its left and right subtrees are **mirror images** of each other.

---

## Java Solution

```java
class Solution {

    public boolean isSymmetric(TreeNode root) {
        if(root == null)
            return true;

        return isMirror(root.left, root.right);
    }

    public boolean isSameTree(TreeNode root1, TreeNode root2){
        if(root1 == null && root2 == null)
            return true;
        if(root1 == null || root2 == null)
            return false;
        if(root1.val != root2.val)
            return false;

        return isSameTree(root1.left, root2.left)
            && isSameTree(root1.right, root2.right);
    }

    public boolean isMirror(TreeNode root1, TreeNode root2){
        if(root1 == null && root2 == null)
            return true;
        if(root1 == null || root2 == null)
            return false;
        if(root1.val != root2.val)
            return false;

        return isMirror(root1.left, root2.right)
            && isMirror(root1.right, root2.left);
    }
}
```

---

## Why `isSameTree()` Does NOT Work

Consider the following symmetric tree:

```
        1
      /   \
     2     2
    /       \
   3         3
```

The left and right subtrees are **mirror images**, not identical.

### Left Subtree

```
    2
   /
  3
```

### Right Subtree

```
    2
     \
      3
```

---

### What `isSameTree()` Compares

```
            2                  2
           /                    \
          3                      3
```

Comparison sequence:

```
2 == 2 ✔

↓

Compare Left vs Left

3 vs null ✘
```

Since the right subtree has **no left child**, `isSameTree()` immediately returns `false`.

It is checking:

```
Left Child  ↔ Left Child

Right Child ↔ Right Child
```

which is correct for the **Same Tree** problem, but **not** for symmetry.

---

## Correct Comparison for Symmetry

A mirror comparison should check:

```
Left Child  ↔ Right Child

Right Child ↔ Left Child
```

For the same tree:

```
            2                  2
           /                    \
          3                      3
```

Comparison sequence:

```
2 == 2 ✔

↓

3 == 3 ✔

↓

null == null ✔

↓

Tree is Symmetric ✔
```

This is why the recursive calls are:

```java
isMirror(root1.left, root2.right)
&&
isMirror(root1.right, root2.left);
```

instead of

```java
isSameTree(root1.left, root2.left)
&&
isSameTree(root1.right, root2.right);
```

---

## Complexity

- **Time:** `O(N)`
- **Space:** `O(h)` (Recursive Call Stack)

# Subtree of Another Tree

Given two binary trees `root` and `subRoot`, determine whether `subRoot` exists as a subtree of `root`.

---

## Java Solution

```java
class Solution {

    public boolean isSubtree(TreeNode root, TreeNode subRoot) {

        if(root == null && subRoot == null)
            return true;

        if(root == null)
            return false;

        if(subRoot == null)
            return true;
		/*
		if(subRoot==null)
			return true;
		if(root==null)	
			return false */
	
        if(isSameTree(root, subRoot))
            return true;
			

        return isSubtree(root.left, subRoot)
            || isSubtree(root.right, subRoot);
    }

    public boolean isSameTree(TreeNode root1, TreeNode root2){

        if(root1 == null && root2 == null)
            return true;

        if(root1 == null || root2 == null)
            return false;

        if(root1.val != root2.val)
            return false;

        return isSameTree(root1.left, root2.left)
            && isSameTree(root1.right, root2.right);
    }
}
```

---

# Idea

At every node in the main tree, ask:

> **"Is the subtree rooted here exactly the same as `subRoot`?"**

If yes, return `true`.

Otherwise, continue searching in the left or right subtree.

---

## Example

### Main Tree

```
          3
        /   \
       4     5
      / \
     1   2
```

### Subtree

```
      4
     / \
    1   2
```

---

## Execution

Start at node `3`

```
isSameTree(3,4)

↓

false
```

Search left subtree

```
isSubtree(4,4)
```

Now

```
isSameTree(4,4)

↓

true
```

Return

```
true
```

The recursion stops.

---

## Why `||` and NOT `&&`?

The recursive function answers:

> **"Does `subRoot` exist anywhere in the tree rooted at `root`?"**

If it exists in **either** subtree, we have found it.

```
return isSubtree(root.left, subRoot)
    || isSubtree(root.right, subRoot);
```

Using `&&` would incorrectly require the subtree to exist in **both** the left and right subtrees.

---

## Common Mistake

Avoid writing:

```java
if(isSameTree(root, subRoot)
    || isSameTree(root.left, subRoot)
    || isSameTree(root.right, subRoot))
```

This duplicates work because the recursive calls will naturally check `root.left` and `root.right`.

Simply write:

```java
if(isSameTree(root, subRoot))
    return true;

return isSubtree(root.left, subRoot)
    || isSubtree(root.right, subRoot);
```

Trust the recursion.

---

## Complexity

Let

- `N` = Number of nodes in `root`
- `M` = Number of nodes in `subRoot`

**Time:** `O(N × M)`

In the worst case, `isSameTree()` may be called for every node in the main tree.

**Space:** `O(h)`

where `h` is the height of the main tree (recursive call stack).

# Interview Heuristics for Recursive Tree Problems

These are patterns that repeatedly appear in tree interview questions.

---

# 1. First Ask: What is my recursive function answering?

Before writing recursion, complete this sentence:

> **This function answers...**

Example:

```java
boolean isSubtree(root, subRoot)
```

answers

> **"Does `subRoot` exist anywhere in the tree rooted at `root`?"**

Now ask:

- If it's in the left subtree, should I return `true`?
- If it's in the right subtree, should I return `true`?

Immediately tells you:

```java
return isSubtree(root.left, subRoot)
    || isSubtree(root.right, subRoot);
```

Don't memorize `&&` vs `||`. Derive it from the question.

---

# 2. Validation vs Search

This is one of the most useful interview heuristics.

### Validation Problems

Every subtree must satisfy a condition.

Usually use:

```java
&&
```

Examples:

- Validate BST
- Balanced Binary Tree
- Same Tree
- Symmetric Tree (mirror validation)

Example:

```java
return validate(left)
    && validate(right);
```

---

### Search Problems

We are searching for one occurrence.

Usually use:

```java
||
```

Examples:

- Subtree of Another Tree
- Path Sum
- Find Target Node
- Search in BST

Example:

```java
return search(left)
    || search(right);
```

---

# 3. Trust the Recursion

One of the most common mistakes is doing work that recursion will already perform.

Example:

Instead of

```java
if(isSameTree(root, subRoot)
    || isSameTree(root.left, subRoot)
    || isSameTree(root.right, subRoot))
```

write

```java
if(isSameTree(root, subRoot))
    return true;

return isSubtree(root.left, subRoot)
    || isSubtree(root.right, subRoot);
```

The recursive calls will naturally check `root.left` and `root.right`.

Don't duplicate work.

---

# 4. Process Current Node or Delegate?

Many tree problems follow this template.

```java
if(root == null)
    return ...

// Process current node

...

// Delegate to children

return recursive(left) ...
```

Always ask:

> Does the answer depend on the current node?

or

> Should I simply ask my children?

---

# 5. Bottom-Up vs Top-Down

## Top-Down

Parent passes information to children.

Examples:

- Validate BST using Min/Max
- Path Sum
- Root-to-Leaf Problems

---

## Bottom-Up

Children return information to parent.

Examples:

- Height
- Diameter
- Balanced Tree
- Maximum Path Sum

Usually uses **Postorder Traversal**.

---

# 6. Return One Thing or Multiple Things?

If recursion naturally needs multiple values, create a helper object.

Instead of

```java
int
```

return

```java
class TreeInfo {

    int height;

    boolean balanced;
}
```

Examples:

- Balanced Tree
- Diameter
- Largest BST
- House Robber III

---

# 7. Mirror vs Same Tree

## Same Tree

Compare

```
Left  ↔ Left

Right ↔ Right
```

Recursive call

```java
isSameTree(root1.left, root2.left)
&&
isSameTree(root1.right, root2.right)
```

---

## Symmetric Tree

Compare

```
Left  ↔ Right

Right ↔ Left
```

Recursive call

```java
isMirror(root1.left, root2.right)
&&
isMirror(root1.right, root2.left)
```

---

# 8. Recognize the Traversal

Think about **when** you need information.

### Need children before processing current node?

Use **Postorder**

Examples:

- Height
- Diameter
- Balanced Tree

---

### Process current node first?

Use **Preorder**

Examples:

- Clone Tree
- Serialize
- Prefix style problems

---

### Need sorted order in BST?

Use **Inorder**

Produces nodes in ascending order.

---

### Process level by level?

Use **BFS**

Examples:

- Level Order Traversal
- Minimum Depth
- Right Side View

---

# 9. Before Coding, Ask Yourself

1. What is my recursive function returning?
2. What question is it answering?
3. Is this a **Search** problem (`||`) or a **Validation** problem (`&&`)?
4. Am I duplicating work that recursion already performs?
5. Does the parent need information from the children first?
6. Should I use DFS or BFS?

---

# Biggest Lesson

Most implementation mistakes happen because we start coding before clearly defining the recursive contract.

Spend **30 seconds** defining:

> **"This recursive function answers..."**

Once that sentence is clear, the recursive code usually becomes almost mechanical.