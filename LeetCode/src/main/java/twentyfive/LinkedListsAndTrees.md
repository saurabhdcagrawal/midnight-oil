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


# LeetCode 355 - Design Twitter

## Pattern

> **Merge K Sorted Lists using a Max Heap**

This is one of the most important design problems because the optimal solution is **not** to collect all tweets and sort them.

The key observation is:

> **Each user's tweets are already stored in sorted order (newest → oldest).**

Instead of processing every tweet, we only process the **current newest remaining tweet** from each followed user.

---

# Heuristic

Whenever you see:

- Multiple users
- Each user's data is already ordered
- Need the global Top K newest/latest

Think:

```
Merge K Sorted Lists
```

NOT

```
Top K Elements
```

---

# Data Structures

```java
Map<Integer, Set<Integer>> userFollowerMap;
```

```
User
    ↓
People the user follows
```

Example

```
1 -> {2,3}

2 -> {4}

3 -> {}
```

---

```java
Map<Integer, Tweet> userTweetMap;
```

Instead of storing a list of tweets, store **only the head** of a linked list.

Example

```
userTweetMap

1 ----------------------+
                        |
                        v
                  Tweet(100)
                        |
                        v
                  Tweet(90)
                        |
                        v
                  Tweet(80)

2 ----------------------+
                        |
                        v
                  Tweet(110)
                        |
                        v
                  Tweet(95)
```

The map stores only one pointer per user.

Every older tweet is reachable through

```java
tweet.next
```

---

# Why Linked List?

Posting a tweet becomes

```java
Tweet lastTweet = userTweetMap.get(userId);

Tweet newTweet = new Tweet(tweetId, clock++);

newTweet.next = lastTweet;

userTweetMap.put(userId, newTweet);
```

which is simply

```
newTweet

↓

old head
```

Insertion is always O(1).

---

# News Feed Logic

## Step 1

Collect

```
All followees

+

The user himself
```

---

## Step 2

Insert only the **head tweet** of every user into a Max Heap.

Example

```
User1

100
 |
90
 |
80

----------------

User2

95
 |
70

----------------

User3

110
 |
60
 |
40
```

Initially the heap contains only

```
110

100

95
```

NOT

```
110

100

95

90

80

70

60

40
```

This is the optimization.

---

## Step 3

Pop

```
110
```

Add to news feed.

Now insert

```
110.next

↓

60
```

Heap becomes

```
100

95

60
```

---

Pop

```
100
```

Insert

```
90
```

Heap

```
95

90

60
```

Continue until

- heap becomes empty OR
- 10 tweets have been returned.

---

# Why does this work?

At any point,

the heap contains only

> **The newest remaining tweet from every user's timeline.**

Whenever one tweet is removed,

we reveal the next older tweet from that user by following

```java
tweet.next
```

Exactly like Merge K Sorted Lists.

---

# Similarity with Merge K Sorted Lists (LeetCode 23)

Merge K Lists

```
1 -> 4 -> 5

1 -> 3 -> 4

2 -> 6
```

Initially insert

```
1

1

2
```

Pop

```
1
```

Insert

```
4
```

Pop

```
1
```

Insert

```
3
```

Repeat.

---

Twitter does the exact same thing.

The only difference is that every user's tweets are stored

```
Newest

↓

Older

↓

Older
```

instead of

```
Smallest

↓

Largest
```

Therefore

Merge K Lists

↓

uses

```
Min Heap
```

Twitter

↓

uses

```
Max Heap
```

because we want the newest tweet first.

---

# Complexity Comparison

Let

```
F = number of followees

T = total tweets across all followees

K = number of tweets requested
```

(K = 10 for this problem.)

---

## Approach 1

### Max Heap containing ALL tweets

Insert every tweet into a Max Heap.

```
Time

Insert all tweets

O(T log T)

+

Pop K tweets

O(K log T)

--------------------------------

O(T log T)
```

Space

```
O(T)
```

---

## Approach 2

### Min Heap of size K (Top K pattern)

Process every tweet.

Maintain

```
Min Heap

size = K
```

For every tweet

```
offer()

if(size > K)

poll()
```

Time

```
Every tweet

↓

O(log K)

Total

↓

O(T log K)
```

Space

```
O(K)
```

Better than Approach 1 because we never keep more than K tweets.

However,

we still scan every tweet.

---

## Approach 3 (Optimal)

### Merge K Sorted Lists

Initially insert only the newest tweet from every followed user.

Heap size

```
F
```

Time

```
Insert F heads

O(F log F)

+

K polls

O(K log F)

+

K offers

O(K log F)

--------------------------------

O((F + K) log F)
```

Since

```
K = 10
```

people usually simplify it to

```
O(F log F)
```

Space

```
O(F)
```

---

# Why is Merge K better?

Suppose

```
100 followees

Each has

10,000 tweets
```

Then

```
F = 100

T = 1,000,000
```

Approach 1

```
Looks at

1,000,000 tweets
```

Approach 2

```
Still scans

1,000,000 tweets
```

Approach 3

Initially looks at only

```
100 tweets
```

Then processes only enough tweets to produce the answer.

This is a massive optimization.

---

# Final Code

```java
public class Twitter {

    Map<Integer,Set<Integer>> userFollowerMap;
    //1->{2,3}
    //1 follows 2 and 3

    //store only heads...
    //Tweet is a LL..
    Map<Integer,Tweet> userTweetMap;
    // 1 tweets 2 and 6

    int clock=1;

    /** Initialize your data structure here. */
    public Twitter() {
        userFollowerMap= new HashMap<>();
        userTweetMap= new HashMap<>();
    }

    /** Compose a new tweet. */
    public void postTweet(int userId, int tweetId) {
        Tweet lastTweet= userTweetMap.get(userId);
        Tweet newTweet= new Tweet(tweetId, clock++);
        newTweet.next=lastTweet;
        userTweetMap.put(userId,newTweet);
    }

    /** Retrieve the 10 most recent tweet ids in the user's news feed. */
    public List<Integer> getNewsFeed(int userId) {

        PriorityQueue<Tweet> maxHeap =
            new PriorityQueue<>((a,b) ->
                Integer.compare(b.timestamp,a.timestamp));

        List<Integer> newsFeed = new ArrayList<>();

        Set<Integer> userFollowersPlusUser = new HashSet<>();

        if(userFollowerMap.containsKey(userId)){
            userFollowersPlusUser.addAll(userFollowerMap.get(userId));
        }

        userFollowersPlusUser.add(userId);

        for(Integer follower : userFollowersPlusUser){

            Tweet tweet = userTweetMap.get(follower);

            if(tweet != null){
                maxHeap.add(tweet);
            }
        }

        while(!maxHeap.isEmpty() && newsFeed.size() < 10){

            Tweet tweet = maxHeap.poll();

            newsFeed.add(tweet.tweetId);

            if(tweet.next != null){
                maxHeap.add(tweet.next);
            }
        }

        return newsFeed;
    }

    /** Follower follows a followee. */
    public void follow(int followerId, int followeeId) {

        if(!userFollowerMap.containsKey(followerId)){
            userFollowerMap.put(followerId,new HashSet<>());
        }

        userFollowerMap.get(followerId).add(followeeId);
    }

    /** Follower unfollows a followee. */
    public void unfollow(int followerId, int followeeId) {

        if(!userFollowerMap.containsKey(followerId))
            return;

        userFollowerMap.get(followerId).remove(followeeId);
    }

    class Tweet{

        int tweetId;
        int timestamp;

        Tweet next;

        public Tweet(int tweetId,int timestamp){
            this.tweetId=tweetId;
            this.timestamp=timestamp;
        }
    }
}
```

---

# Interview Takeaway

Whenever you hear:

> **Multiple sorted streams + return latest/top elements**

Immediately ask yourself:

> **Can I treat this as Merge K Sorted Lists instead of scanning everything?**

That single observation changes the solution from **O(T log T)** to **O((F + K) log F)** and is exactly what interviewers are looking for.


# LeetCode 23 - Merge K Sorted Lists

## Pattern

> **Merge K Sorted Lists using a Min Heap**

This is one of the most important heap patterns.

Whenever you have:

- Multiple sorted streams/lists
- Need to repeatedly pick the smallest (or largest) element

Think:

```
Merge K Sorted Lists
```

---

# Heuristic

Ask yourself:

> "At every step, how many candidates do I need to compare?"

Initially there are **K** candidates (one from each list).

Instead of scanning all K every time, store them in a **Min Heap**.

The heap always gives the smallest node in **O(log K)** time.

---

# Approaches

## Approach 1 - Brute Force

Ignore that the lists are already sorted.

### Steps

1. Traverse every list.
2. Store all nodes (or values) in an array.
3. Sort the array.
4. Rebuild the linked list.

### Complexity

Let

```
N = total number of nodes
```

Time

```
Collect nodes      O(N)

Sort              O(N log N)

Rebuild list      O(N)

--------------------------

Overall           O(N log N)
```

Space

```
O(N)
```

---

## Approach 2 - Linear Search (K-way Merge without Heap)

Maintain one pointer into each list.

Example

```
List1

1 -> 4 -> 7

List2

2 -> 3 -> 8

List3

5 -> 6
```

Initially the pointers are

```
1

2

5
```

To produce the next node,

scan all current pointers

```
1

2

5
```

Choose

```
1
```

Advance only List1.

Pointers become

```
4

2

5
```

Again scan all K pointers.

Repeat until all nodes are processed.

### Why is it O(NK)?

There are

```
N
```

nodes to output.

For **every node**, you scan

```
K
```

current pointers.

Therefore

```
N × K

=

O(NK)
```

Space

```
O(1)
```

---

## Approach 3 - Min Heap (Optimal)

Instead of scanning K pointers every time,

store those K pointers inside a Min Heap.

Initially

```
Heap

1

2

5
```

Pop

```
1
```

Append to answer.

Insert

```
4
```

Heap

```
2

4

5
```

Pop

```
2
```

Insert

```
3
```

Continue until the heap becomes empty.

The heap always contains **only one node from each list**.

---

# Complexity

Let

```
K = number of lists

N = total number of nodes
```

### Initial heap construction

Insert

```
K
```

heads.

```
O(K log K)
```

### Processing nodes

Every node

- enters heap once
- leaves heap once

Each heap operation

```
O(log K)
```

Therefore

```
O(N log K)
```

### Overall

Strictly

```
O(K log K + N log K)

=

O((N + K) log K)
```

Since

```
N >> K
```

we usually write

```
O(N log K)
```

Space

```
O(K)
```

---

# Why does the heap help?

Without a heap

```
Need the smallest node?

↓

Scan all K pointers

↓

O(K)
```

With a heap

```
Need the smallest node?

↓

Heap top

↓

O(log K)
```

The heap replaces a repeated **linear search** with a **logarithmic search**.

---

# Similar Interview Problems

Exactly the same pattern appears in:

- Merge K Sorted Arrays
- Twitter News Feed (Max Heap instead of Min Heap)
- External Merge Sort
- Smallest Range Covering K Lists
- Merge Sorted Files / Log Files

---

# Code

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) {
 *         this.val = val;
 *         this.next = next;
 *     }
 * }
 */
class Solution {

    public ListNode mergeKLists(ListNode[] lists) {

        ListNode dummy = new ListNode(0);
        ListNode result = dummy;

        PriorityQueue<ListNode> pq =
            new PriorityQueue<>((a, b) ->
                Integer.compare(a.val, b.val));

        // Insert the head of every non-empty list.
        // The heap always stores the smallest remaining node
        // from each list.

        for (ListNode list : lists) {
            if (list != null)
                pq.offer(list);
        }

        while (!pq.isEmpty()) {

            ListNode node = pq.poll();

            result.next = node;
            result = result.next;

            // Advance only the list from which
            // the smallest node was removed.

            if (node.next != null)
                pq.offer(node.next);
        }

        return dummy.next;
    }
}
```

# Interview Explanation

> "Since every linked list is already sorted, I don't need to insert all nodes into the heap. I only insert the head of each list. The heap always contains the smallest remaining node from each list. After removing the smallest node, I insert only its next node from the same list. This is exactly the Merge K Sorted Lists pattern and gives an optimal complexity of **O(N log K)** instead of **O(NK)** or **O(N log N)**."

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

**Space:** `O(H1 + H2)`

- `H1` = Height of the main tree.
- `H2` = Height of the subtree.
- Worst Case (Skewed Trees): `O(N + M)`
- Balanced Trees: `O(log N + log M)`


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

# Invert Binary Tree

Invert a binary tree by swapping the left and right subtree of every node.

---

## Java Solution (Postorder)

```java
class Solution {

    public TreeNode invertTree(TreeNode root) {

        if(root == null)
            return root;

        TreeNode leftInvertedTree = invertTree(root.left);
        TreeNode rightInvertedTree = invertTree(root.right);

        root.left = rightInvertedTree;
        root.right = leftInvertedTree;

        return root;
    }
}
```

---

## Example

### Original Tree

```
        4
      /   \
     2     7
    / \   / \
   1   3 6   9
```

---

### Left Subtree After Inversion

```
      2
     / \
    3   1
```

---

### Right Subtree After Inversion

```
      7
     / \
    9   6
```

---

### Final Tree

```
        4
      /   \
     7     2
    / \   / \
   9   6 3   1
```

---

## Why Postorder?

The recursion first inverts the left and right subtrees.

Then the current node swaps the two inverted subtrees.

```
Left

↓

Right

↓

Swap Current Node
```

---

## Complexity

- **Time:** `O(N)`
- **Space:** `O(h)` (Recursive Call Stack)
  - Balanced Tree: `O(log N)`
  - Skewed Tree: `O(N)`

---

## Interview Note

Another equally valid approach is **Preorder**:

1. Swap the left and right child.
2. Recursively invert the left subtree.
3. Recursively invert the right subtree.

Both approaches are correct and run in `O(N)` time.

# Merge Two Binary Trees

Merge two binary trees by summing overlapping nodes. If one node is `null`, use the non-null node directly.

---

## Java Solution

```java
class Solution {

    public TreeNode mergeTrees(TreeNode root1, TreeNode root2) {

        if(root1 == null)
            return root2;

        if(root2 == null)
            return root1;

        root1.val += root2.val;

        root1.left = mergeTrees(root1.left, root2.left);
        root1.right = mergeTrees(root1.right, root2.right);

        return root1;
    }
}
```

---

## Example

### Tree 1

```
      1
     / \
    3   2
   /
  5
```

### Tree 2

```
      2
     / \
    1   3
     \   \
      4   7
```

### Merged Tree

```
        3
      /   \
     4     5
    / \     \
   5   4     7
```

---

## Why do we attach the returned subtree?

The recursive function returns:

```java
TreeNode mergeTrees(TreeNode root1, TreeNode root2)
```

It **returns the root of the merged subtree**.

Suppose we recurse on the left child:

```java
mergeTrees(root1.left, root2.left);
```

Imagine this call returns:

```
    4
```

If we simply call the function without assigning its return value, the merged subtree is discarded.

Instead, we must attach it back to its parent.

```java
root1.left = mergeTrees(root1.left, root2.left);
```

Now the returned subtree becomes the new left child.

The same applies to the right subtree.

```java
root1.right = mergeTrees(root1.right, root2.right);
```

---

## Visual Example

Suppose

### Tree 1

```
    1
   /
 null
```

### Tree 2

```
    2
   /
  4
```

Recursive call:

```java
mergeTrees(root1.left, root2.left)
```

becomes

```java
mergeTrees(null, node(4))
```

The function returns

```
4
```

If we ignore the return value:

```java
mergeTrees(root1.left, root2.left);
```

the node `4` is lost.

Correct:

```java
root1.left = mergeTrees(root1.left, root2.left);
```

Result:

```
    3
   /
  4
```

---

## Complexity

- **Time:** `O(N)`
- **Space:** `O(h)` (Recursive Call Stack)
  - Balanced Tree: `O(log N)`
  - Skewed Tree: `O(N)`

---

# Interview Heuristic

Whenever a recursive function returns a **TreeNode**, ask yourself:

> **Who owns the returned subtree?**

Usually the answer is:

> **The parent node.**

Therefore you'll frequently write:

```java
root.left = helper(root.left);

root.right = helper(root.right);
```

Examples:

- Merge Two Binary Trees
- Trim a BST
- Delete Node in a BST
- Prune Binary Tree
- Remove Leaf Nodes

If you simply write:

```java
helper(root.left);
```

without assigning the result, ask yourself:

> **"Where did the returned subtree go?"**

If the answer is **nowhere**, then you probably forgot to attach it back to the parent.

# Leaf-Similar Trees

Two binary trees are **leaf-similar** if their leaf nodes appear in the **same left-to-right order**.

---

## Java Solution

```java
class Solution {

    public boolean leafSimilar(TreeNode root1, TreeNode root2) {

        List<Integer> leaves1 = new ArrayList<>();
        List<Integer> leaves2 = new ArrayList<>();

        findLeafNodes(root1, leaves1);
        findLeafNodes(root2, leaves2);

        return leaves1.equals(leaves2);
    }

    public void findLeafNodes(TreeNode root, List<Integer> leaves){

        if(root == null)
            return;

        if(root.left == null && root.right == null)
            leaves.add(root.val);

        findLeafNodes(root.left, leaves);
        findLeafNodes(root.right, leaves);
    }
}
```

---

## Example

### Tree 1

```
        3
       / \
      5   1
     / \   \
    6   2   9
       / \
      7   4
```

Leaf sequence:

```
[6, 7, 4, 9]
```

---

### Tree 2

```
        3
       / \
      5   1
     /     \
    6       2
           / \
          7   4
               \
                9
```

Leaf sequence:

```
[6, 7, 4, 9]
```

Since both leaf sequences are identical,

```
true
```

---

## Why Preorder DFS Works

We recursively traverse:

```
Root

↓

Left

↓

Right
```

Whenever we encounter a leaf node,

```java
if(root.left == null && root.right == null)
    leaves.add(root.val);
```

we record its value.

Because recursion visits the **left subtree before the right subtree**, the leaves are naturally collected in **left-to-right order**.

---

## Complexity

Let

- `N` = Number of nodes in Tree 1
- `M` = Number of nodes in Tree 2

**Time:** `O(N + M)`

Each node in both trees is visited exactly once.

**Space:** `O(L1 + L2 + H1 + H2)`

where

- `L1`, `L2` = Number of leaf nodes stored in the lists.
- `H1`, `H2` = Heights of the two trees (recursive call stack).

Worst case:

- Lists: `O(N + M)`
- Recursive Stack: `O(H1 + H2)`

---

## Interview Note

This is a common pattern:

> Traverse the tree using DFS, collect information in a list, and compare the collected results.

The recursive helper performs a simple task:

> **"Collect all leaf nodes from left to right."**

The main function simply compares the two resulting sequences.

# Maximum Depth of Binary Tree

The **maximum depth** (or height using the node-count convention) is the number of nodes on the longest path from the root to a leaf.

---

## Example

```
        1
       / \
      2   3
     /
    4
```

Longest path:

```
1 → 2 → 4
```

Maximum Depth:

```
3
```

---

## Idea

For every node:

1. Compute the maximum depth of the left subtree.
2. Compute the maximum depth of the right subtree.
3. Return the larger depth plus the current node.

This is a classic **bottom-up recursive** problem.

---

## Java Solution

```java
class Solution {

    public int maxDepth(TreeNode root) {

        if(root == null)
            return 0;

        return 1 + Math.max(
                maxDepth(root.left),
                maxDepth(root.right)
        );
    }
}
```

---

## Complexity

**Time:** `O(N)`

Each node is visited exactly once.

**Space:** `O(h)` (Recursive Call Stack)

- Balanced Tree: `O(log N)`
- Skewed Tree: `O(N)`

---

## Interview Note

LeetCode's `maxDepth()` uses the **node-count convention**:

```
height(null) = 0

leaf = 1
```

This is the same convention used throughout the following **Balanced Binary Tree** solution.

# Balanced Binary Tree

A binary tree is **balanced** if, for **every node**, the height difference between its left and right subtrees is **at most 1**.

---

# Why Checking Only the Root is Incorrect

Consider the following tree (LeetCode example):

```
            1
          /   \
         2     2
        /       \
       3         3
      /           \
     4             4
```

Level-order representation:

```
[1,2,2,3,null,null,3,4,null,null,4]
```

---

## Check Only the Root

```
Left Height  = 3

Right Height = 3

Difference = 0
```

Looks perfectly balanced.

If we only checked the root, we would incorrectly return:

```
true
```

---

## But Look at Node 2

```
      2
     /
    3
   /
  4
```

```
Left Height  = 2

Right Height = 0

Difference = 2
```

This subtree is **not balanced**.

The same happens on the right subtree.

Therefore,

**every node** must satisfy the balance condition—not just the root.

---

# Brute Force Solution (Top-Down)

## Idea

For every node:

1. Compute the height of the left subtree.
2. Compute the height of the right subtree.
3. Check if the difference is greater than 1.
4. Recursively verify both subtrees.

The drawback is that subtree heights are recomputed many times.

---

## Java Solution

```java
class Solution {

    public boolean isBalancedBruteForce(TreeNode root) {

        if(root == null)
            return true;

        if(Math.abs(height(root.left) - height(root.right)) > 1)
            return false;

        return isBalancedBruteForce(root.left)
            && isBalancedBruteForce(root.right);
    }

    public int height(TreeNode root){

        if(root == null)
            return 0;

        return 1 + Math.max(height(root.left), height(root.right));
    }
}
```

---

## Complexity

**Time:** `O(N²)`

Reason:

For every node, the height of its subtree may be recomputed.

**Space:** `O(h)`

where `h` is the height of the tree.

- Balanced Tree: `O(log N)`
- Skewed Tree: `O(N)`

---

# Optimized Solution (Bottom-Up)

## Observation

Instead of computing

- Height
- Balance

separately,

compute both together.

Each subtree returns:

- Height
- Is Balanced

The parent combines those results.

---

## Helper Class

```java
class TreeNodeInfo{

    public int height;

    public boolean isBalanced;

    public TreeNodeInfo(int height, boolean isBalanced){
        this.height = height;
        this.isBalanced = isBalanced;
    }
}
```

---

## Java Solution

```java
class Solution {

    public boolean isBalanced(TreeNode root){
        return isBalancedHelper(root).isBalanced;
    }

    public TreeNodeInfo isBalancedHelper(TreeNode root){

        if(root == null)
            return new TreeNodeInfo(0, true);

        TreeNodeInfo left = isBalancedHelper(root.left);

        if(!left.isBalanced)
            return new TreeNodeInfo(-1, false);

        TreeNodeInfo right = isBalancedHelper(root.right);

        if(!right.isBalanced)
            return new TreeNodeInfo(-1, false);

        if(Math.abs(left.height - right.height) > 1)
            return new TreeNodeInfo(-1, false);

        return new TreeNodeInfo(
            1 + Math.max(left.height, right.height),
            true
        );
    }
}

class TreeNodeInfo{

    public int height;

    public boolean isBalanced;

    public TreeNodeInfo(int height, boolean isBalanced){
        this.height = height;
        this.isBalanced = isBalanced;
    }
}
```

---

# Why is the Optimized Solution O(N)?

Each node is visited exactly once.

Each recursive call returns:

- Height
- Balance Status

The parent combines the results in constant time.

No subtree height is recomputed.

---

# Why Use a Helper Object?

Instead of returning only

```java
int
```

return

```java
(height, isBalanced)
```

Now the parent immediately knows:

- Height of left subtree
- Height of right subtree
- Whether left subtree is balanced
- Whether right subtree is balanced

without making additional recursive calls.

---

# Why Return `(-1, false)`?

Our implementation uses

```
height(null) = 0
```

to stay consistent with LeetCode's `maxDepth()` (height measured using node count).

When a subtree is already known to be unbalanced, we return

```java
new TreeNodeInfo(-1, false);
```

The value `-1` is **not treated as a height**.

It is simply a sentinel value indicating failure.

The parent always checks

```java
if(!left.isBalanced)
```

before ever using `left.height`.

---

# Complexity

### Brute Force

- **Time:** `O(N²)`
- **Space:** `O(h)`

---

### Optimized

- **Time:** `O(N)`
- **Space:** `O(h)`

where `h` is the height of the tree.

- Balanced Tree: `O(log N)`
- Skewed Tree: `O(N)`

---

# Interview Heuristics

### Bottom-Up Tree DP

Whenever you find yourself repeatedly computing the same property (such as subtree height), ask:

> **Can the child return everything I need?**

Instead of repeatedly asking for a subtree's height, let each child return:

```
(height, isBalanced)
```

The parent simply combines those results.

---

### Return Multiple Values

If recursion naturally requires multiple pieces of information, return a helper object instead of making multiple recursive calls.

Examples:

- Balanced Binary Tree
- Diameter of Binary Tree
- Maximum Path Sum
- Largest BST in a Binary Tree

---

### Top-Down vs Bottom-Up

**Top-Down (Brute Force)**

For every node:

```
Compute Left Height

↓

Compute Right Height

↓

Recursively check children
```

The same heights are computed repeatedly.

---

**Bottom-Up (Optimized)**

Each subtree answers once:

```
(height, isBalanced)
```

The parent combines the answers in constant time.

This is one of the most common and powerful optimization patterns for recursive tree problems.

# Serialize and Deserialize Binary Tree

Convert a binary tree into a string (**Serialize**) and reconstruct the same tree back from the string (**Deserialize**).

This solution uses **Preorder Traversal** with explicit `null` markers.

---

# Example

## Original Tree

```
        1
      /   \
     2     3
          / \
         4   5
```

---

## Serialized String

```
1,2,null,null,3,4,null,null,5,null,null,
```

Notice that every missing child is explicitly stored as `null`.

Without the `null` markers, we would not be able to uniquely reconstruct the tree.

---

# Java Solution

```java
public class Codec {

    int pointer = 0;

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {

        StringBuilder sb = new StringBuilder();

        serialize(root, sb);

        return sb.toString();
    }

    public void serialize(TreeNode root, StringBuilder sb) {

        if(root == null){
            sb.append("null,");
            return;
        }

        sb.append(root.val);
        sb.append(",");

        serialize(root.left, sb);
        serialize(root.right, sb);
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {

        pointer = 0;

        String[] nodes = data.split(",");

        return deserialize(nodes);
    }

    public TreeNode deserialize(String[] nodes){

        String value = nodes[pointer];
        pointer++;

        if(value.equals("null"))
            return null;

        TreeNode node = new TreeNode(Integer.parseInt(value));

        node.left = deserialize(nodes);
        node.right = deserialize(nodes);

        return node;
    }
}
```

---

# Why Preorder?

Serialization visits nodes in the order

```
Node

↓

Left

↓

Right
```

The serialized string is therefore a preorder traversal with explicit `null` markers.

During deserialization, we read the tokens in exactly the same order.

```
Read Node

↓

Build Left

↓

Build Right
```

The two algorithms are perfect inverses of each other.

---

# Why does Deserialization stop?

Every recursive call consumes **exactly one token**.

```
Read Token

↓

Advance Pointer

↓

Is it "null" ?

        Yes → Return null

        No

↓

Create Node

↓

Build Left

↓

Build Right
```

Whenever we encounter `"null"` we immediately stop building that branch.

Eventually every token has been consumed exactly once.

---

# Walkthrough

Serialized string

```
1,2,null,null,3,null,null
```

Array

```
Index   Value

0       1
1       2
2       null
3       null
4       3
5       null
6       null
```

Execution

```
pointer = 0

Read 1

Create Node(1)

↓

Build Left

Read 2

Create Node(2)

↓

Read null

Return

↓

Read null

Return

↓

Return Node(2)

↓

Build Right

Read 3

↓

Read null

↓

Read null

↓

Return Node(3)

↓

Return Node(1)
```

Every token is consumed exactly once.

---

# Complexity

## Serialize

**Time:** `O(N)`

Every node is visited once.

**Space:**

- Output String: `O(N)`
- Recursive Stack: `O(h)`

---

## Deserialize

**Time:** `O(N)`

Each serialized token is processed exactly once.

**Space:**

- Array created by `split()`: `O(N)`
- Recursive Stack: `O(h)`

where `h` is the height of the tree.

- Balanced Tree: `O(log N)`
- Skewed Tree: `O(N)`

---

# Why use a Class Variable (`pointer`)?

Our recursive helper

```java
deserialize(String[] nodes)
```

needs to know which token should be processed next.

Instead of passing an index through every recursive call, we maintain a single shared pointer.

```
pointer

0

↓

1

↓

2

↓

3

...
```

Every recursive call updates the same pointer.

Before starting deserialization we simply reset it:

```java
pointer = 0;
```

This makes the recursive code much cleaner.

---

# Why NOT use a Class Variable for `StringBuilder`?

Notice that we **do not** write:

```java
StringBuilder sb = new StringBuilder();
```

as a class member.

Instead:

```java
public String serialize(TreeNode root){

    StringBuilder sb = new StringBuilder();

    serialize(root, sb);

    return sb.toString();
}
```

Every call to `serialize()` gets a brand-new `StringBuilder`.

If it were a class variable, multiple calls could accidentally append to previous results unless we remembered to clear it.

---

# Interview Heuristic: When should I use Class Variables?

Use a **class variable** when it represents **shared recursive state**.

Examples:

- `pointer` (Deserialize)
- `prev` (BST Iterator / Recover BST)
- `maxDiameter`
- `maxPathSum`
- `result`

These variables naturally evolve during recursion and are shared by every recursive call.

---

Do **not** use a class variable for objects that belong to **one method invocation**.

Examples:

- `StringBuilder`
- `List`
- `HashMap`
- `HashSet`

Instead, create them once in the public method and pass them down recursively.

---

# Interview Rule

Ask yourself:

> **If I call this method twice on the same object, should this variable remember anything from the previous call?**

If the answer is **No**, it usually should **not** be a class variable.

If the answer is **Yes**, or it represents shared traversal state, then a class variable is often the cleanest solution.

---

# Takeaways

- Serialize using **Preorder + null markers**.
- Deserialize using the **same preorder order**.
- Every recursive call consumes exactly **one token**.
- `pointer` is shared recursive state, making it a good class variable.
- `StringBuilder` belongs to a single serialization call, so keep it local and pass it to the helper.
- Serialization and deserialization are mirror images of each other.


# Why can't we pass `pointer` as a method parameter?

Suppose we try:

```java
TreeNode deserialize(String[] nodes, int pointer)
```

instead of using a class variable.

Consider the serialized tree:

```
1,2,null,null,3,null,null
```

---

## Root Call

```
deserialize(nodes, 0)
```

```
pointer = 0

Read 1

pointer++

pointer = 1
```

Now build the left subtree.

```
deserialize(nodes, 1)
```

---

## Left Subtree

```
pointer = 1

Read 2

pointer++

pointer = 2
```

Build left child.

```
deserialize(nodes, 2)

↓

Read null

↓

Return
```

Build right child.

```
deserialize(nodes, 3)

↓

Read null

↓

Return
```

The left subtree is complete.

```
pointer = 4
```

inside the **left recursive call**.

---

## Return to the Root

Here is the important part.

The left recursive call finished with

```
pointer = 4
```

But the root call still has

```
pointer = 1
```

because Java passed `pointer` **by value**.

The child modified **its own copy**.

The parent never sees those updates.

So the root now executes

```java
node.right = deserialize(nodes, pointer);
```

using

```
pointer = 1
```

again.

It starts reading node `2` a second time.

The traversal is now incorrect.

---

# Visualizing the Problem

Without a shared pointer:

```
Root Call

pointer = 1

           |
           |
           v

Left Call

pointer = 1

↓

2

↓

3

↓

4

(Return)
```

After returning,

the root still has

```
pointer = 1
```

The updates made by the child are lost.

---

# Using a Class Variable

Now consider

```java
int pointer;
```

There is only **one** pointer shared by every recursive call.

```
pointer

0

↓

1

↓

2

↓

3

↓

4

↓

5

↓

6

↓

7
```

When the left subtree finishes,

the pointer is already

```
4
```

The root immediately continues from token `4`, exactly where the left subtree stopped.

Nothing is lost.

---

# Why doesn't `Integer` work?

Passing

```java
Integer pointer
```

does **not** solve the problem.

Although `Integer` is an object, it is **immutable**.

When you write

```java
pointer++;
```

Java creates a **new Integer object**.

The caller still points to the old object.

Therefore the parent still does **not** observe the updated value.

---

# Interview Heuristic

Ask yourself:

> **Do all recursive calls need to share the same traversal state?**

If **yes**, don't pass an `int` or `Integer`.

Use one of these:

- A class variable (recommended here)
- A mutable wrapper object
- A one-element array
- `AtomicInteger`

For tree serialization/deserialization, a **class-level pointer** is the cleanest and most common solution.

# Interview Rule: Mutable vs Immutable Objects in Recursion

## Java Pass-by-Value Rule

Java is **always pass-by-value**.

When you pass an object to a method, Java passes a **copy of the reference**, **not** the object itself.

Whether changes are visible to the caller depends on whether the object is **mutable** or **immutable**.

---

## 1. Mutable Objects ✅

Examples:

- `StringBuilder`
- `List`
- `Map`
- `Set`

Recursive calls modify the **same underlying object**.

Example:

```java
public void serialize(TreeNode root, StringBuilder sb)
```

Every recursive call executes:

```java
sb.append(...);
```

Since all recursive calls point to the **same `StringBuilder`**, every append is visible to every caller.

---

## 2. Immutable Objects ❌

Examples:

- `Integer`
- `String`
- `Long`
- `Double`

Suppose we write:

```java
deserialize(nodes, Integer pointer);
```

Inside the helper:

```java
pointer++;
```

This **does not modify** the existing `Integer`.

Instead, Java creates a **new Integer object**, and the helper's local variable points to that new object.

The caller still points to the old `Integer`, so the updated value is lost when the recursive call returns.

---

## 3. Shared Class Variables ✅

Examples:

```java
int pointer;

TreeNode prev;

int maxDiameter;

int maxPathSum;
```

There is only **one copy** of these variables inside the object.

Every recursive call reads and updates the **same shared state**.

Example:

```java
pointer++;
```

The update is immediately visible to every recursive call.

---

# Interview Heuristic

Ask yourself:

> **Do all recursive calls need to share the same state?**

If **Yes**, use:

- A class variable (preferred in many tree problems)
- A mutable wrapper object
- A one-element array
- `AtomicInteger`

If **No**, simply pass the value as a method parameter.

---

# One Sentence to Remember

> **Java is always pass-by-value. When you pass an object, Java passes a copy of the reference. If the object is mutable (e.g., `StringBuilder`, `List`), everyone modifies the same object. If the object is immutable (e.g., `Integer`, `String`), any "modification" creates a new object, so the caller never sees the change.**

# Understanding Java Pass-by-Value with Mutable and Immutable Objects

One of the most common sources of confusion in Java recursion is understanding **what is actually passed to a method**.

---

# Java is Always Pass-by-Value

When you pass an object to a method, Java **does not pass the original object**.

Instead, Java passes a **copy of the reference** (memory address) to that object.

```
Caller Variable

↓

Reference

↓

Object
```

The caller and callee each have **their own reference variable**, but both initially point to the **same object**.

---

# Case 1: Mutable Object (`StringBuilder`)

Suppose we write:

```java
StringBuilder sb = new StringBuilder();

serialize(root, sb);
```

Initially:

```
Caller

sb
 |
 |
 +----------------------+
                        |
                        v
               +----------------+
               | StringBuilder  |
               | ""             |
               +----------------+
```

When Java calls

```java
serialize(root, sb);
```

it creates a **copy of the reference**.

```
Caller                  Helper

sb                      sb
 |                       |
 |                       |
 +-----------+-----------+
             |
             v
      +----------------+
      | StringBuilder  |
      | ""             |
      +----------------+
```

Notice:

There are now **two reference variables**,

but they both point to **the same object**.

---

Now the helper executes

```java
sb.append("1,");
```

The **StringBuilder object itself changes**.

```
Caller                  Helper

sb                      sb
 |                       |
 +-----------+-----------+
             |
             v
      +----------------+
      | StringBuilder  |
      | "1,"           |
      +----------------+
```

Since both references point to the same object,

the caller also sees

```
"1,"
```

The reference variables never changed.

Only the object changed.

---

# Case 2: Immutable Object (`Integer`)

Suppose we write

```java
Integer pointer = 0;

deserialize(nodes, pointer);
```

Initially:

```
Caller

pointer
   |
   |
   +-------------------+
                       |
                       v
                +-------------+
                | Integer(0)  |
                +-------------+
```

Java copies the reference during the method call.

```
Caller                    Helper

pointer                   pointer
   |                         |
   |                         |
   +------------+------------+
                |
                v
         +-------------+
         | Integer(0)  |
         +-------------+
```

Everything still looks similar.

---

Now the helper executes

```java
pointer++;
```

Many people think this modifies the Integer.

It **does not**.

It is equivalent to

```java
pointer = Integer.valueOf(pointer.intValue() + 1);
```

A **new Integer object** is created.

Now memory looks like this:

```
Caller

pointer
   |
   v
Integer(0)
```

```
Helper

pointer
   |
   v
Integer(1)
```

The helper's reference now points to a **different Integer object**.

The caller is still pointing to the original `Integer(0)`.

When the helper returns,

the updated pointer is lost.

---

# Why does `pointer++` fail?

Because

```java
pointer++;
```

does **not** modify the Integer object.

It changes **only the helper's local reference variable** to point to a newly created Integer.

The caller's reference never changes.

---

# Why does `sb.append()` work?

Because

```java
sb.append(...)
```

does **not** change the reference.

It modifies the **same StringBuilder object** that both the caller and helper reference.

---

# Think of References as Addresses

Imagine:

- Reference = House Address
- Object = House

Initially

```
Caller Address

123 Main Street

↓

House
```

Java copies the address.

```
Caller Address

123 Main Street

↓

House

↑

Helper Address
```

Two address slips.

One house.

---

## Mutable Object

Helper paints the house blue.

```
House

Blue
```

Caller also sees a blue house.

Because everyone is looking at the same house.

---

## Immutable Object

You are not allowed to repaint the house.

Instead,

Helper buys a brand-new house.

```
Caller Address

123 Main Street

↓

House A
```

```
Helper Address

456 Main Street

↓

House B
```

The caller still points to House A.

Only the helper knows about House B.

---

# Why does a Class Variable Work?

In our `Codec` solution,

```java
int pointer;
```

is a **field of the Codec object**.

There is only **one** `pointer`.

Every recursive call updates the same variable.

```
pointer

0

↓

1

↓

2

↓

3

↓

4

↓

5
```

When one recursive call increments the pointer,

every other recursive call immediately sees the updated value.

Nothing is copied.

---

# Interview Takeaway

Remember the distinction:

### Mutable Object

```
Reference stays the same.

Object changes.
```

Example:

```java
StringBuilder
List
Map
Set
```

---

### Immutable Object

```
Object cannot change.

Reference changes to a new object.
```

Example:

```java
Integer
String
Long
Double
```

---

# One Sentence to Remember

> **Java passes a copy of the reference, not the object. If you mutate the object (e.g., `StringBuilder.append()`), everyone sees the change because both references point to the same object. If you reassign the reference (or use an immutable object like `Integer`, where `pointer++` creates a new object), only the local copy of the reference changes, so the caller never sees the update.**

# Why `String` Behaves Like `Integer` (Immutability)

`String` behaves exactly like `Integer` because **both are immutable objects**.

Any "modification" actually creates a **new object** rather than changing the existing one.

---

# Example

```java
public void change(String s) {
    s += " World";
}

public static void main(String[] args) {

    String str = "Hello";

    change(str);

    System.out.println(str);
}
```

Output:

```
Hello
```

**Not**

```
Hello World
```

---

# What Happens in Memory?

Initially

```
Caller

str
 |
 |
 +-------------------+
                     |
                     v
                  "Hello"
```

When Java calls

```java
change(str);
```

it creates a **copy of the reference**.

```
Caller                    Helper

str                       s
 |                         |
 |                         |
 +------------+------------+
              |
              v
           "Hello"
```

Both variables initially point to the same String object.

---

Now the helper executes

```java
s += " World";
```

This is equivalent to

```java
s = s + " World";
```

A **new String object** is created.

Memory now becomes

```
Caller

str
 |
 v
"Hello"
```

```
Helper

s
 |
 v
"Hello World"
```

The caller still points to the original String.

Only the helper knows about the new String.

When the helper returns, the new String becomes unreachable.

---

# Compare with `StringBuilder`

```java
public void change(StringBuilder sb){
    sb.append(" World");
}
```

Initially

```
Caller                    Helper

sb                        sb
 |                         |
 +------------+------------+
              |
              v
     StringBuilder("Hello")
```

Now the helper executes

```java
sb.append(" World");
```

The **same StringBuilder object** is modified.

After the append

```
Caller                    Helper

sb                        sb
 |                         |
 +------------+------------+
              |
              v
StringBuilder("Hello World")
```

The caller immediately sees the change because both references still point to the same object.

---

# Why Does One Work and the Other Doesn't?

### String

```
Reference changes.

Object does NOT change.
```

A new String object is created.

---

### StringBuilder

```
Reference stays the same.

Object changes.
```

The existing object is modified.

---

# Interview Rule

Whenever you pass an object into a recursive helper (or any method), ask yourself:

> **Does this method mutate the existing object or create a new one?**

If it **mutates the existing object**, the caller sees the changes.

Examples:

- `StringBuilder.append()`
- `List.add()`
- `Map.put()`
- `Set.remove()`

---

If it **creates a new object**, the caller does **not** see the changes unless you return the new object.

Examples:

- `String +`
- `Integer++`
- `Long++`
- `Double++`

---

# Quick Reference

| Type | Mutable? | Caller Sees Changes? |
|------|----------|----------------------|
| `String` | ❌ No | ❌ No |
| `Integer` | ❌ No | ❌ No |
| `Long` | ❌ No | ❌ No |
| `Double` | ❌ No | ❌ No |
| `StringBuilder` | ✅ Yes | ✅ Yes |
| `ArrayList` | ✅ Yes | ✅ Yes |
| `HashMap` | ✅ Yes | ✅ Yes |
| `HashSet` | ✅ Yes | ✅ Yes |
| Custom Mutable Class | ✅ Yes | ✅ Yes |

---

# One Sentence to Remember

> **Passing an object to a method does not mean the caller will see changes. The caller only sees changes if the object itself is mutated. If the operation creates a new object (as with `String` or `Integer`), only the local reference changes, and the caller continues pointing to the original object.**



# Java: Object References, Mutability & Pass-by-Value

## Java is always Pass-by-Value

A common misconception is that Java is "pass-by-reference."

Java is **always pass-by-value**.

For objects, the **value being copied is the object reference (memory address)**.

```java
TrieNode root = new TrieNode();

addProductsToTrie(products, root);
```

Memory:

```text
Caller

root
 |
 v
TrieNode A
```

When the method is called:

```text
Caller                    Method

root                      root
 |                         |
 |                         |
 +-------------------------+
            |
            v
        TrieNode A
```

There are **two reference variables**, but both point to the **same object**.

Therefore, modifying the object inside the method is visible to the caller.

---

## Modifying the object

```java
void foo(TrieNode root) {
    root.isEnd = true;
}
```

After the method returns:

```java
root.isEnd == true
```

because the object itself was modified.

---

## Reassigning the reference

```java
void foo(TrieNode root) {
    root = new TrieNode();
}
```

Memory:

```text
Caller

root
 |
 v
TrieNode A


Method

root
 |
 v
TrieNode B
```

Only the **method's local copy** of the reference changes.

After returning:

```text
Caller

root
 |
 v
TrieNode A
```

The caller is completely unaffected.

---

# Why doesn't this happen with String?

```java
void foo(String s) {
    s = s + " World";
}
```

Caller:

```java
String s = "Hello";

foo(s);

System.out.println(s);
```

Output:

```text
Hello
```

### Why?

`String` is **immutable**.

This statement:

```java
s = s + " World";
```

does **not** modify `"Hello"`.

Instead it creates a **new String object**:

```text
Caller

s
 |
 v
"Hello"


Method

s
 |
 v
"Hello World"
```

The caller still points to the original String.

---

# Mutable vs Immutable

The difference is **not** how Java passes the object.

The difference is whether the object itself can be modified.

## Mutable Objects

Changes are visible to the caller.

Examples:

```text
ArrayList
HashMap
HashSet
TreeMap
TreeSet
StringBuilder
TrieNode
TreeNode
Most custom classes
```

Example:

```java
void foo(List<Integer> list) {
    list.add(10);
}
```

The caller's list now contains `10`.

---

## Immutable Objects

Operations create a **new object** instead of modifying the existing one.

Examples:

```text
String
Integer
Long
Double
LocalDate
LocalDateTime
BigInteger
BigDecimal
```

Example:

```java
void foo(String s) {
    s = s + " World";
}
```

The caller's String remains unchanged.

---

# Are Custom Objects Mutable?

**Yes, by default.**

Example:

```java
class Employee {
    String name;
}
```

```java
Employee emp = new Employee();
emp.name = "Mike";

foo(emp);
```

```java
void foo(Employee emp) {
    emp.name = "John";
}
```

After returning:

```text
emp.name == "John"
```

because the same object was modified.

---

# How to Make a Custom Object Immutable

```java
final class Employee {

    private final String name;

    Employee(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
```

Characteristics:

* `final` class (optional but recommended)
* `private final` fields
* No setters
* State cannot be modified after construction

---

# Easy Rule to Remember

If you can write:

```java
obj.someField = value;
```

or

```java
obj.someCollection.add(value);
```

the object is **mutable**.

If every "change" requires:

```java
obj = new SomeObject(...);
```

the object is **immutable**.

---

# Interview Takeaway

> **Java always passes object references by value. If the object is mutable, modifying its state is visible to the caller. If the object is immutable (e.g., `String`), any "modification" creates a new object, so the caller continues to reference the original object.**

# Why does `HashMap<Node, Node>` work?

A common question is:

> **How can `HashMap<Node, Node>` work if we don't know how `Node.hashCode()` is implemented?**

The key idea is that the map is **not comparing the state of the objects** (such as `val` or `neighbors`). It is comparing the **object references (identity)**.

---

## `Node` does not override `equals()` or `hashCode()`

The LeetCode `Node` class is defined as:

```java
class Node {
    public int val;
    public List<Node> neighbors;
}
```

Since it does **not** override `equals()` or `hashCode()`, it inherits both methods from `Object`.

Therefore:

* `equals()` uses **reference equality** (`==`).
* `hashCode()` is identity-based (derived from the object's identity).

---

## Example

```java
Node a = new Node(1);
Node b = new Node(1);
```

Memory:

```text
a -------> Node(val=1)

b -------> Node(val=1)
```

Although both nodes have the same value,

```java
a == b          // false

a.equals(b)     // false
```

because they are two different objects.

The HashMap stores:

```text
Original Node Object        Cloned Node Object

a ----------------------->  a'

b ----------------------->  b'
```

Notice that the key is **not**

```text
1  ---> clone
```

It is

```text
Original Node Object  --->  Cloned Node Object
```

---

## Why not use the node value?

Suppose the graph contains:

```text
Node A (val = 1) ----- Node B (val = 1)
```

These are two distinct graph vertices that happen to have the same value.

If we used:

```java
Map<Integer, Node>
```

both nodes would map to the same key:

```text
1 ---> clone
```

The cloned graph would become incorrect because two different nodes would share the same clone.

Using the original object as the key avoids this problem.

---

## What if two nodes have the same hash code?

Even if two different `Node` objects produce the same hash code, the `HashMap` still works correctly.

Java handles hash collisions by:

1. Computing the hash.
2. Looking in the corresponding bucket.
3. Comparing keys using `equals()`.

Since `equals()` uses reference equality, the map can distinguish between different node objects even if their hash codes collide.

Collisions may slightly affect performance, but **they do not affect correctness**.

---

## Interview Takeaway

> The `HashMap` uses the original `Node` object itself as the key, not the node's value. Since `Node` inherits `Object`'s implementation of `equals()` and `hashCode()`, keys are compared using object identity (reference equality). This ensures every original graph node maps to exactly one cloned node, even when multiple nodes have the same value.



# 105. Construct Binary Tree from Preorder and Inorder Traversal

Given the preorder and inorder traversal of a binary tree, reconstruct the original binary tree.

---

# Why isn't Preorder Alone Enough?

Suppose we only know

```
Preorder

1,2
```

This could represent

```
    1
   /
  2
```

or

```
1
 \
  2
```

Both have the same preorder traversal.

Therefore, **preorder alone cannot uniquely reconstruct a binary tree.**

---

# Why didn't we need Inorder in Serialize/Deserialize?

In Serialize/Deserialize, our preorder looked like:

```
1,2,null,null,3,null,null
```

The `null` markers completely describe the tree structure.

```
Node

↓

Left

↓

Right

↓

null means the subtree ends.
```

Therefore, preorder + null markers are sufficient.

In this problem, however, the input is simply

```
Preorder

3,9,20,15,7
```

There are **no null markers**.

We therefore need another traversal to determine where each subtree begins and ends.

That traversal is **Inorder**.

---

# The Two Traversals Have Different Responsibilities

This is the most important idea in the problem.

## Preorder

Preorder answers

> **Which node should I build next?**

It gives the construction order.

```
3

↓

9

↓

20

↓

15

↓

7
```

We simply consume preorder from left to right.

A single shared pointer is enough.

---

## Inorder

Inorder answers

> **Where does this node belong?**

It tells us how to split the tree into

```
Left Subtree

Root

Right Subtree
```

Example

```
Inorder

9  | 3 |  15 20 7
```

Everything left of **3**

belongs to the left subtree.

Everything right of **3**

belongs to the right subtree.

---

# Key Insight

Think of the two traversals as having different jobs.

```
Preorder

↓

"What node do I build next?"

----------------------------

Inorder

↓

"Where should that node be placed?"
```

This is the entire solution.

---

# Step 1 : Build a HashMap

Instead of searching inorder every time,

store

```
Value

↓

Index
```

Example

```
Inorder

[9,3,15,20,7]
```

Map becomes

```
9  -> 0

3  -> 1

15 -> 2

20 -> 3

7  -> 4
```

Now finding the inorder position takes

```
O(1)
```

instead of

```
O(N)
```

---

# Shared State

We maintain

```java
int preorderIndex;
```

This answers

> **What is the next root?**

Every recursive call shares the same preorder pointer.

```
0

↓

1

↓

2

↓

3

↓

4
```

Exactly like Serialize / Deserialize.

---

# Recursive State

Each recursive call receives

```java
left

right
```

These represent

```
Current Inorder Range
```

Meaning

```
Build the subtree using

inorder[left...right]
```

Notice something important.

We **never traverse the inorder array**.

We only use

```
value

↓

index
```

to determine the subtree boundaries.

---

# Recursive Algorithm

```
Take next preorder value

↓

Create node

↓

Find its inorder position

↓

Everything left belongs to left subtree

↓

Everything right belongs to right subtree

↓

Recursively build both subtrees
```

---

# Dry Run

## Input

```
Preorder

[3,9,20,15,7]
```

```
Inorder

[9,3,15,20,7]
```

Initially

```
preorderIndex = 0
```

---

## Call 1

Current inorder range

```
0...4
```

Read preorder

```
preorder[0]

↓

3
```

```
preorderIndex = 1
```

Create

```
      3
```

Lookup

```
3

↓

Index 1
```

Split

```
Left

0...0

Right

2...4
```

---

## Build Left

Range

```
0...0
```

Read preorder

```
preorder[1]

↓

9
```

```
preorderIndex = 2
```

Create

```
    9
```

Lookup

```
9

↓

Index 0
```

Split

```
Left

0...-1

Right

1...0
```

Both ranges are invalid.

Return

```
9
```

Now tree becomes

```
      3
     /
    9
```

---

## Build Right

Range

```
2...4
```

Read preorder

```
preorder[2]

↓

20
```

```
preorderIndex = 3
```

Create

```
      20
```

Lookup

```
20

↓

Index 3
```

Split

```
Left

2...2

Right

4...4
```

---

## Build Left of 20

Read preorder

```
preorder[3]

↓

15
```

```
preorderIndex = 4
```

Create

```
15
```

Both recursive ranges become invalid.

Return.

---

## Build Right of 20

Read preorder

```
preorder[4]

↓

7
```

```
preorderIndex = 5
```

Create

```
7
```

Return.

---

Final tree

```
        3
      /   \
     9     20
          /  \
         15   7
```

Every preorder element was consumed exactly once.

---

# Java Solution

```java
class Solution {

    int preorderIndex;

    public TreeNode buildTree(int[] preorder, int[] inorder) {

        Map<Integer, Integer> inorderMap = new HashMap<>();

        for(int i = 0; i < inorder.length; i++)
            inorderMap.put(inorder[i], i);

        return helper(
                0,
                inorder.length - 1,
                inorderMap,
                preorder
        );
    }

    public TreeNode helper(
            int left,
            int right,
            Map<Integer,Integer> inorderMap,
            int[] preorder){

        if(left > right)
            return null;

        int value = preorder[preorderIndex++];

        TreeNode root = new TreeNode(value);

        int inorderPosition = inorderMap.get(value);

        root.left = helper(
                left,
                inorderPosition - 1,
                inorderMap,
                preorder);

        root.right = helper(
                inorderPosition + 1,
                right,
                inorderMap,
                preorder);

        return root;
    }
}
```

---

# Why don't we need the inorder array anymore?

Notice that after building the HashMap,

we never actually use

```java
inorder[]
```

again.

We only use

```java
value

↓

index
```

The inorder traversal is now simply a way of determining

```
Left Boundary

Right Boundary
```

---

# Comparison with Serialize / Deserialize

## Serialize / Deserialize

Shared State

```
pointer
```

Recursive State

```
Nothing
```

Why?

Because the `null` markers already tell us exactly where every subtree ends.

```
1

↓

2

↓

null

↓

null

↓

3

↓

null

↓

null
```

The recursion naturally knows when to stop.

---

## Construct Binary Tree

Shared State

```
preorderIndex
```

Recursive State

```
left

right
```

Why?

Because preorder contains **no null markers**.

Instead,

the inorder traversal tells us where the subtree boundaries are.

---

## Compare

| Serialize / Deserialize | Construct Tree |
|-------------------------|----------------|
| Shared Pointer | Shared Preorder Index |
| Preorder + Null Markers | Preorder + Inorder |
| Null marks subtree end | Inorder range marks subtree boundaries |
| Build Left then Right | Build Left then Right |

---

# Complexity

Building HashMap

```
O(N)
```

Recursive Construction

```
O(N)
```

Overall

**Time:** `O(N)`

Every node is processed exactly once.

---

**Space**

HashMap

```
O(N)
```

Recursive Stack

```
O(h)
```

Balanced Tree

```
O(log N)
```

Worst Case

```
O(N)
```

---

# Interview Heuristics

## Heuristic 1

Whenever you see

```
Preorder + Inorder
```

immediately think

```
Shared preorder pointer

+

Recursive inorder boundaries
```

---

## Heuristic 2

Ask yourself

> **What information is shared across every recursive call?**

Here

```
preorderIndex
```

is shared.

---

## Heuristic 3

Ask yourself

> **What information uniquely defines this recursive problem?**

Answer

```
Current inorder range

(left, right)
```

---

## Heuristic 4

Remember the roles

```
Preorder

↓

"What node do I build next?"

------------------------------

Inorder

↓

"Where should I place that node?"
```

This single idea is enough to derive the entire algorithm.

---

# One Sentence to Remember

> **Preorder determines the order in which nodes are created, while inorder determines the boundaries of each subtree. A shared preorder pointer selects the next root, and the inorder range tells the recursion where that root belongs.**

This problem has a beautiful pattern:

Preorder tells us "what node to create next."

Inorder tells us "where that node belongs."

So:

Preorder Index answers: What is the next root?
Inorder Range (left, right) answers: Which subtree am I currently building?

This is the key insight behind the optimal O(N) solution. Once you see those two pieces of state—one shared cursor through preorder and one recursive boundary in inorder—the implementation becomes almost mechanical.

# 106. Construct Binary Tree from Inorder and Postorder Traversal

Given the inorder and postorder traversal of a binary tree, reconstruct the original binary tree.

---

# Why isn't Postorder Alone Enough?

Suppose we only know

```
Postorder

2,1
```

This could represent

```
    1
   /
  2
```

or

```
1
 \
  2
```

Both have the same postorder traversal.

Therefore, **postorder alone cannot uniquely reconstruct a binary tree.**

We need another traversal to determine the subtree boundaries.

That traversal is **Inorder**.

---

# The Two Traversals Have Different Responsibilities

## Postorder

Postorder answers

> **Which node should I build next?**

However, the root is visited **last**.

```
Left

↓

Right

↓

Root
```

Since we reconstruct the tree starting from the root, we process the postorder array **backwards**.

```
Root

↓

Right

↓

Left
```

Therefore,

we maintain a shared pointer starting from the end of the postorder array.

---

## Inorder

Inorder answers

> **Where should this node be placed?**

Example

```
Inorder

9 | 3 | 15 20 7
```

Everything left of **3**

belongs to the left subtree.

Everything right of **3**

belongs to the right subtree.

---

# Key Insight

Think of the traversals as having different responsibilities.

```
Postorder

↓

"What node should I build next?"

-------------------------------

Inorder

↓

"Where should that node be placed?"
```

---

# Step 1 : Build a HashMap

Instead of searching inorder repeatedly,

store

```
Value

↓

Index
```

Example

```
Inorder

[9,3,15,20,7]
```

Map

```
9  -> 0

3  -> 1

15 -> 2

20 -> 3

7  -> 4
```

Now every lookup takes

```
O(1)
```

---

# Shared State

We maintain

```java
int postorderIndex;
```

Initially

```java
postorderIndex = postorder.length - 1;
```

The pointer moves

```
4

↓

3

↓

2

↓

1

↓

0
```

Each recursive call consumes exactly one node from the end of the postorder traversal.

---

# Recursive State

Each recursive call receives

```java
left

right
```

representing

```
Current Inorder Range
```

Meaning

```
Build the subtree represented by

inorder[left...right]
```

Notice that we never traverse the inorder array.

We only use

```
Value

↓

Index
```

to determine subtree boundaries.

---

# Why Build the Right Subtree First?

Postorder traversal is

```
Left

↓

Right

↓

Root
```

Since we process the array **backwards**, we actually encounter

```
Root

↓

Right

↓

Left
```

Therefore,

after creating the root,

we must construct

```
Right Subtree

↓

Left Subtree
```

otherwise the traversal order becomes incorrect.

---

# Dry Run

## Input

```
Inorder

[9,3,15,20,7]
```

```
Postorder

[9,15,7,20,3]
```

Initially

```
postorderIndex = 4
```

---

## Call 1

Current inorder range

```
0...4
```

Read

```
postorder[4]

↓

3
```

```
postorderIndex = 3
```

Create

```
      3
```

Lookup

```
3

↓

Index 1
```

Split

```
Left

0...0

Right

2...4
```

Since we are processing postorder backwards,

we must build

```
Right

↓

Left
```

---

## Build Right

Current range

```
2...4
```

Read

```
postorder[3]

↓

20
```

```
postorderIndex = 2
```

Create

```
      20
```

Lookup

```
20

↓

Index 3
```

Split

```
Left

2...2

Right

4...4
```

Again,

build right first.

---

### Build Right of 20

Read

```
postorder[2]

↓

7
```

Create

```
7
```

Return.

---

### Build Left of 20

Read

```
postorder[1]

↓

15
```

Create

```
15
```

Return.

Now the tree becomes

```
      3
       \
        20
       /  \
      15   7
```

---

## Build Left of Root

Current range

```
0...0
```

Read

```
postorder[0]

↓

9
```

Create

```
9
```

Return.

Final Tree

```
        3
      /   \
     9     20
          /  \
         15   7
```

Every postorder element is consumed exactly once.

---

# Java Solution

```java
class Solution {

    int postorderIndex;

    public TreeNode buildTree(int[] inorder, int[] postorder) {

        Map<Integer,Integer> inorderMap = new HashMap<>();

        for(int i = 0; i < inorder.length; i++)
            inorderMap.put(inorder[i], i);

        postorderIndex = postorder.length - 1;

        return helper(
                0,
                inorder.length - 1,
                inorderMap,
                postorder
        );
    }

    // In postorder traversal, the root is visited last.
    // Since we process the array backwards,
    // we encounter Root → Right → Left.
    // Therefore we build the right subtree first.

    public TreeNode helper(
            int left,
            int right,
            Map<Integer,Integer> inorderMap,
            int[] postorder){

        if(left > right)
            return null;

        int value = postorder[postorderIndex--];

        int inorderPosition = inorderMap.get(value);

        TreeNode root = new TreeNode(value);

        root.right = helper(
                inorderPosition + 1,
                right,
                inorderMap,
                postorder);

        root.left = helper(
                left,
                inorderPosition - 1,
                inorderMap,
                postorder);

        return root;
    }
}
```

---

# Comparison with Preorder + Inorder

| Preorder + Inorder | Postorder + Inorder |
|---------------------|---------------------|
| Pointer starts at beginning | Pointer starts at end |
| `preorderIndex++` | `postorderIndex--` |
| Root → Left → Right | Read backwards: Root → Right → Left |
| Build Left first | Build Right first |
| Shared preorder pointer | Shared postorder pointer |
| Inorder determines subtree boundaries | Inorder determines subtree boundaries |

---

# Complexity

## Time

Building HashMap

```
O(N)
```

Tree Construction

```
O(N)
```

Overall

```
O(N)
```

Every node is processed exactly once.

---

## Space

HashMap

```
O(N)
```

Recursive Stack

```
O(h)
```

Balanced Tree

```
O(log N)
```

Worst Case

```
O(N)
```

---

# Interview Heuristics

## Heuristic 1

Whenever you see

```
Inorder + Postorder
```

immediately think

```
Shared postorder pointer

+

Recursive inorder boundaries
```

---

## Heuristic 2

Remember the traversal order.

Original postorder

```
Left

↓

Right

↓

Root
```

During reconstruction we read backwards

```
Root

↓

Right

↓

Left
```

So always build

```
Right

↓

Left
```

---

## Heuristic 3

Ask yourself

> **What information is shared across every recursive call?**

Answer

```
postorderIndex
```

---

## Heuristic 4

Ask yourself

> **What information uniquely defines each recursive subproblem?**

Answer

```
Current inorder range

(left, right)
```

---

# One Sentence to Remember

> **Postorder determines the order in which roots are created (reading from the end), while inorder determines the boundaries of each subtree. A shared postorder pointer selects the next root, and the inorder range tells the recursion where that root belongs.**

---

# Mirror Pattern

The preorder and postorder construction problems are perfect mirrors of each other.

```
Preorder + Inorder

Pointer starts at beginning

↓

Build Left

↓

Build Right
```

```
Postorder + Inorder

Pointer starts at end

↓

Build Right

↓

Build Left
```

The recursive structure, inorder boundaries, and HashMap optimization remain exactly the same. Only the traversal direction and subtree construction order change.


# 98. Validate Binary Search Tree

Given the root of a binary tree, determine whether it is a valid Binary Search Tree (BST).

A BST satisfies:

- Every node in the left subtree is **strictly smaller** than the current node.
- Every node in the right subtree is **strictly larger** than the current node.
- Both left and right subtrees must themselves be valid BSTs.

---

# Solution 1 : Brute Force

## Idea

For every node:

- Find the **maximum value** in the left subtree.
- Find the **minimum value** in the right subtree.
- Verify

```
Maximum(left)

<

Current

<

Minimum(right)
```

Then recursively validate both subtrees.

---

## Java Solution

```java
class Solution {

    Integer prev = null;

    public boolean isValidBSTPreOrder(TreeNode root) {

        if(root == null)
            return true;

        if(root.left != null && findMaximum(root.left).val >= root.val)
            return false;

        if(root.right != null && findMinimum(root.right).val <= root.val)
            return false;

        return isValidBSTPreOrder(root.left)
                && isValidBSTPreOrder(root.right);
    }

    public TreeNode findMinimum(TreeNode root){

        if(root == null)
            return root;

        if(root.left == null)
            return root;

        return findMinimum(root.left);
    }

    public TreeNode findMaximum(TreeNode root){

        if(root == null)
            return root;

        if(root.right == null)
            return root;

        return findMaximum(root.right);
    }
}
```

---

## Complexity

### Time

```
O(N²)
```

Each node may repeatedly search for the minimum or maximum of its subtree.

---

### Space

Recursive Stack

```
O(h)
```

Balanced Tree

```
O(log N)
```

Worst Case

```
O(N)
```

---

# Solution 2 : Optimal (Inorder Traversal)

## Key Insight

The inorder traversal of a valid BST is always

```
Strictly Increasing
```

Example

```
        5
       / \
      3   8
     / \   \
    2   4   9
```

Inorder traversal

```
2

↓

3

↓

4

↓

5

↓

8

↓

9
```

Every newly visited node must be **greater** than the previously visited node.

---

# Shared State

We maintain

```java
Integer prev = null;
```

`prev` stores the previously visited node during the inorder traversal.

Every recursive call shares this variable.

---

# Java Solution

```java
class Solution {

    Integer prev = null;

    public boolean isValidBST(TreeNode root) {

        if(root == null)
            return true;

        if(!isValidBST(root.left))
            return false;

        if(prev != null && root.val <= prev)
            return false;

        prev = root.val;

        return isValidBST(root.right);
    }
}
```

---

# Dry Run

Tree

```
        5
       / \
      3   8
     / \   \
    2   4   9
```

Traversal

```
Visit 2

prev = null

↓

Valid

prev = 2

↓

Visit 3

3 > 2

Valid

prev = 3

↓

Visit 4

4 > 3

Valid

prev = 4

↓

Visit 5

5 > 4

Valid

prev = 5

↓

Visit 8

8 > 5

Valid

prev = 8

↓

Visit 9

9 > 8

Valid
```

Every node is greater than the previous node.

Therefore,

```
Valid BST
```

---

# Invalid Example

```
      5
     / \
    3   8
       /
      4
```

Inorder traversal

```
3

↓

5

↓

4

↓

8
```

When visiting

```
4
```

we compare

```
4 <= 5
```

which violates the BST property.

Immediately return

```
false
```

---

# Why Does `prev` Need to be Shared?

Suppose we passed

```java
isValidBST(TreeNode root, Integer prev)
```

instead.

The left recursive call might update

```
prev = 5
```

but after returning,

the parent still has

```
prev = 3
```

because `Integer` is immutable and Java passes a **copy of the reference**.

The updated value is lost.

We need **one shared previous value** across the entire inorder traversal.

Therefore,

```java
Integer prev;
```

as a class variable is the correct design.

---

# Should We Reset `prev`?

## LeetCode

Your solution is perfectly fine.

```java
class Solution {

    Integer prev = null;

    public boolean isValidBST(TreeNode root) {
        ...
    }
}
```

LeetCode creates a **new `Solution` object** for every test case.

Therefore,

```
prev

↓

null
```

at the beginning of every execution.

No helper is required.

---

## Production Code / Reusable Library

Suppose someone writes

```java
Solution sol = new Solution();

sol.isValidBST(tree1);

sol.isValidBST(tree2);
```

Now,

after validating the first tree,

```
prev
```

still contains the last visited value.

The second call starts with stale state.

To make the class reusable,

reset the shared state before beginning the recursion.

```java
class Solution {

    Integer prev;

    public boolean isValidBST(TreeNode root) {

        prev = null;

        return validate(root);
    }

    private boolean validate(TreeNode root){

        if(root == null)
            return true;

        if(!validate(root.left))
            return false;

        if(prev != null && root.val <= prev)
            return false;

        prev = root.val;

        return validate(root.right);
    }
}
```

This guarantees every invocation starts with a clean state.

---

# Complexity

## Time

```
O(N)
```

Every node is visited exactly once.

---

## Space

Recursive Stack

```
O(h)
```

Balanced Tree

```
O(log N)
```

Worst Case

```
O(N)
```

---

# Interview Heuristics

## Heuristic 1

Whenever you see

```
BST
```

ask yourself

> **Can inorder traversal simplify the problem?**

Very often,

the answer is **Yes**.

---

## Heuristic 2

Brute Force Thinking

```
Maximum(left)

<

Current

<

Minimum(right)
```

Correct,

but repeatedly recomputes subtree information.

```
O(N²)
```

---

## Heuristic 3

Optimal Thinking

Ask yourself

> **What unique property does a BST have?**

Answer

```
Inorder traversal is strictly increasing.
```

Therefore,

instead of comparing

```
Parent

↓

Child
```

compare

```
Current node

↓

Previously visited inorder node
```

---

# Another Pattern to Recognize

Notice how similar this problem is to several tree problems you've already studied.

| Problem | Shared State |
|----------|--------------|
| Serialize / Deserialize | `pointer` |
| Construct Tree (Preorder + Inorder) | `preorderIndex` |
| Construct Tree (Postorder + Inorder) | `postorderIndex` |
| Validate BST | `prev` |

All four follow the same recursive design pattern.

> **Maintain one piece of shared traversal state that every recursive call must observe and update.**

This is one of the most important recursion patterns in binary tree problems.

---

# One Sentence to Remember

> **A BST's inorder traversal must be strictly increasing. During the traversal, maintain a single shared `prev` variable representing the previously visited node. If the current node is ever less than or equal to `prev`, the tree is not a valid BST.**


# White-Box vs Black-Box Testing

One of the most common misconceptions in unit testing is:

> **"A unit test should test only one method."**

That statement is **partially true**, but there are two different philosophies of testing.

---

# 1. White-Box Testing (State Verification)

White-box testing verifies the **internal implementation** of the class.

You inspect the object's internal state after calling a method.

Example:

```java
class Stack {

    List<Integer> stack = new ArrayList<>();

    public void push(int x){
        stack.add(x);
    }
}
```

Test

```java
@Test
void push() {

    Stack stack = new Stack();

    stack.push(5);

    assertEquals(1, stack.stack.size());

    assertEquals(5, stack.stack.get(0));
}
```

Here we're checking

```
Internal List

↓

size

↓

contents
```

instead of the external behavior.

---

## Advantages

- Easy to write.
- Good for testing low-level data structures.
- Can verify intermediate state.

---

## Disadvantages

Suppose tomorrow we replace

```java
ArrayList
```

with

```java
LinkedList
```

or

```java
ArrayDeque
```

The implementation changes even though the Stack still behaves correctly.

Many white-box tests would now fail.

They are tightly coupled to the implementation.

---

# 2. Black-Box Testing (Behavior Verification)

Black-box testing verifies the **observable behavior** of the class.

It doesn't care how the class is implemented internally.

Instead it asks

> "Did the public API produce the correct result?"

Using the same Stack example

```java
@Test
void push() {

    Stack stack = new Stack();

    stack.push(5);

    assertEquals(5, stack.pop());
}
```

Notice

We never inspect

```
ArrayList

size

indexes
```

We only verify

```
Behavior
```

---

# Twitter Example

Consider our Twitter implementation.

```java
Map<Integer, Set<Integer>> userFollowerMap;

Map<Integer, List<Integer>> userTweetMap;
```

These maps are **implementation details**.

Users of the class never access them directly.

The public API is

```java
postTweet()

follow()

unfollow()

getNewsFeed()
```

---

# White-Box Test

Suppose we write

```java
@Test
void postTweet() {

    Twitter twitter = new Twitter();

    twitter.postTweet(1,5);

    assertEquals(
        1,
        twitter.userTweetMap.get(1).size()
    );
}
```

We're checking

```
HashMap

↓

ArrayList

↓

size
```

instead of testing Twitter's behavior.

---

# Why is this bad?

Suppose we optimize our implementation.

Old

```java
Map<Integer,List<Integer>>
```

↓

New

```java
Map<Integer,Tweet>
```

↓

Or

```
Database
```

Twitter still behaves correctly.

But our white-box test breaks because the implementation changed.

---

# Black-Box Test

Instead we write

```java
@Test
void postTweet_ShouldAppearInOwnFeed() {

    Twitter twitter = new Twitter();

    twitter.postTweet(1,5);

    assertEquals(
        List.of(5),
        twitter.getNewsFeed(1)
    );
}
```

Notice

We're testing the contract

```
After posting,

the tweet appears in the news feed.
```

We don't care how Twitter stores the tweets internally.

---

# Another Example - follow()

Instead of

```java
assertTrue(
    twitter.userFollowerMap
           .get(1)
           .contains(2)
);
```

Test

```java
Twitter twitter = new Twitter();

twitter.postTweet(2,6);

twitter.follow(1,2);

assertEquals(
    List.of(6),
    twitter.getNewsFeed(1)
);
```

We're verifying

```
Behavior

↓

User can now see followee's tweets.
```

---

# Another Example - unfollow()

Instead of

```java
assertFalse(
    twitter.userFollowerMap
           .get(1)
           .contains(2)
);
```

Test

```java
Twitter twitter = new Twitter();

twitter.postTweet(2,6);

twitter.follow(1,2);

twitter.unfollow(1,2);

assertEquals(
    List.of(),
    twitter.getNewsFeed(1)
);
```

Again,

we verify behavior instead of internal state.

---

# "But aren't we testing another method?"

This is where many developers get confused.

Consider

```java
public void postTweet(...)
```

It returns

```java
void
```

How do we verify it worked?

There are only two options.

### Option 1

Inspect internal state

```
userTweetMap
```

(White-box)

### Option 2

Observe the effect using another public method

```
getNewsFeed()
```

(Black-box)

Most teams prefer Option 2 because it tests the public contract rather than the implementation.

---

# Why use another method?

Suppose

```
deposit(100)
```

in a BankAccount class.

How do we verify the deposit succeeded?

Not by checking the private variable

```
balance
```

Instead

```java
account.deposit(100);

assertEquals(
    100,
    account.getBalance()
);
```

We're testing

```
deposit()

↓

observable behavior

↓

getBalance()
```

This is completely normal.

---

# Which one is preferred?

For business applications

```
Twitter

Shopping Cart

Order Service

Bank Account

Library Management
```

Prefer

```
Black-box Testing
```

because tests remain valid even if the implementation changes.

---

For low-level data structures

```
Linked List

HashMap

Binary Heap

LRU Cache Internals
```

White-box testing is acceptable because the internal data structure **is** the thing you're implementing.

---

# Comparison

| White-Box Testing | Black-Box Testing |
|-------------------|-------------------|
| Tests internal implementation | Tests external behavior |
| Verifies internal state | Verifies public contract |
| Coupled to implementation | Independent of implementation |
| Breaks when implementation changes | Continues to pass if behavior is unchanged |
| Good for low-level data structures | Preferred for application/business logic |

---

# Interview Perspective

Suppose the interviewer asks

> **"Write unit tests for Twitter."**

A good answer is

```java
@Test
void follow_ShouldIncludeFolloweeTweets() {

    Twitter twitter = new Twitter();

    twitter.postTweet(1,5);

    twitter.postTweet(2,6);

    twitter.follow(1,2);

    assertEquals(
        List.of(6,5),
        twitter.getNewsFeed(1)
    );
}
```

Instead of

```java
assertTrue(
    twitter.userFollowerMap
           .get(1)
           .contains(2)
);
```

because interviewers usually want to verify **behavior**, not the internal implementation.

---

# Rule of Thumb

Whenever writing a unit test, ask yourself:

> **If I completely rewrite the internals of this class but keep the public API the same, should this test still pass?**

If the answer is **Yes**, you're most likely writing a good **black-box** test.

If the answer is **No**, you're probably testing implementation details and writing a **white-box** test.

# JUnit 5 Syntax & Best Practices

JUnit is the standard testing framework for Java.

A test class usually looks like:

```java
package main.java.java8;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class TwitterTest {

    @Test
    void postTweet() {

        Twitter twitter = new Twitter();

        twitter.postTweet(1, 5);
        twitter.postTweet(2, 6);
        twitter.follow(1, 2);

        List<Integer> tweets = twitter.getNewsFeed(1);

        assertEquals(2, tweets.size());
    }
}
```

---

# Package

```java
package main.java.java8;
```

Specifies the package containing the test.

Usually the test package mirrors the production package.

Example

```
src

 ├── main
 │      └── java
 │             └── twitter
 │                    Twitter.java
 │
 └── test
        └── java
               └── twitter
                      TwitterTest.java
```

---

# Imports

## Static Import

```java
import static org.junit.jupiter.api.Assertions.*;
```

This imports all assertion methods.

Without it

```java
Assertions.assertEquals(2, tweets.size());
```

With static import

```java
assertEquals(2, tweets.size());
```

Much cleaner.

---

## Test Annotation

```java
import org.junit.jupiter.api.Test;
```

Makes

```java
@Test
```

available.

JUnit scans for methods marked with

```java
@Test
```

and executes them.

---

# Test Class

```java
class TwitterTest
```

Convention

```
<ClassName>

+

Test
```

Examples

```
Twitter

↓

TwitterTest

----------------

OrderService

↓

OrderServiceTest

----------------

Calculator

↓

CalculatorTest
```

---

# @Test

```java
@Test
void postTweet() {
}
```

Marks this method as a unit test.

JUnit automatically executes every method annotated with

```
@Test
```

---

# Test Method Name

Instead of

```java
void postTweet()
```

prefer

```java
void postTweet_ShouldAppearInNewsFeed()
```

or

```java
void follow_ShouldIncludeFolloweeTweets()
```

Pattern

```
MethodBeingTested

+

ExpectedBehavior
```

Examples

```
deposit_ShouldIncreaseBalance()

withdraw_ShouldThrowException()

login_ShouldReturnJwt()

follow_ShouldIncludeTweets()
```

These names explain

- what is being tested
- expected result

without opening the method.

---

# Arrange • Act • Assert (AAA Pattern)

Most production code follows this layout.

```java
@Test
void follow_ShouldIncludeTweets() {

    // Arrange
    Twitter twitter = new Twitter();

    twitter.postTweet(1,5);
    twitter.postTweet(2,6);

    // Act
    twitter.follow(1,2);

    List<Integer> tweets =
            twitter.getNewsFeed(1);

    // Assert
    assertEquals(
        List.of(6,5),
        tweets
    );
}
```

---

## Arrange

Create objects.

Prepare data.

Example

```java
Twitter twitter = new Twitter();

twitter.postTweet(1,5);

twitter.postTweet(2,6);
```

---

## Act

Execute the method under test.

```java
twitter.follow(1,2);

List<Integer> tweets =
        twitter.getNewsFeed(1);
```

---

## Assert

Verify expected behavior.

```java
assertEquals(
    List.of(6,5),
    tweets
);
```

---

# Assertions

## assertEquals()

```java
assertEquals(expected, actual);
```

Always remember

```
Expected first

Actual second
```

Correct

```java
assertEquals(2, tweets.size());
```

Wrong

```java
assertEquals(tweets.size(), 2);
```

Why?

If the test fails,

JUnit prints

```
Expected : 2

Actual : 3
```

which is much easier to understand.

---

## assertTrue()

```java
assertTrue(condition);
```

Example

```java
assertTrue(
    tweets.contains(6)
);
```

---

## assertFalse()

```java
assertFalse(condition);
```

Example

```java
assertFalse(
    tweets.isEmpty()
);
```

---

## assertNull()

```java
assertNull(object);
```

---

## assertNotNull()

```java
assertNotNull(object);
```

---

## assertThrows()

Verify exceptions.

```java
assertThrows(
    IllegalArgumentException.class,
    () -> calculator.divide(5,0)
);
```

---

# One Assertion vs Multiple Assertions

Good

```java
assertEquals(
    List.of(6,5),
    tweets
);
```

Instead of

```java
assertEquals(2, tweets.size());

assertEquals(6, tweets.get(0));

assertEquals(5, tweets.get(1));
```

Comparing the whole object is cleaner whenever possible.

---

# One Behavior Per Test

Good

```java
@Test
void follow_ShouldIncludeTweets()
```

Bad

```java
@Test
void everything()
```

Each test should verify one behavior.

---

# Independent Tests

Each test should create its own object.

Good

```java
@Test
void test1(){

    Twitter twitter = new Twitter();

}
```

Another test

```java
@Test
void test2(){

    Twitter twitter = new Twitter();

}
```

Never depend on previous tests.

JUnit may execute tests

- in any order
- in parallel

---

# Common JUnit 5 Annotations

## @BeforeEach

Runs before every test.

```java
@BeforeEach
void setup(){

    twitter = new Twitter();
}
```

Equivalent to creating a fresh object for every test.

---

## @AfterEach

Runs after every test.

Used for cleanup.

---

## @BeforeAll

Runs once before all tests.

Usually for expensive initialization.

---

## @AfterAll

Runs once after all tests.

Cleanup.

---

# Best Practices

## ✅ Good

Descriptive test name

```java
postTweet_ShouldAppearInNewsFeed()
```

---

AAA pattern

```
Arrange

↓

Act

↓

Assert
```

---

One behavior per test.

---

Independent tests.

---

Compare expected before actual.

```java
assertEquals(expected, actual);
```

---

Prefer comparing complete objects.

```java
assertEquals(
    List.of(6,5),
    tweets
);
```

instead of checking every index individually.

---

Test behavior rather than implementation.

Good

```java
assertEquals(
    List.of(6,5),
    twitter.getNewsFeed(1)
);
```

Avoid

```java
assertEquals(
    2,
    twitter.userFollowerMap.get(1).size()
);
```

because that couples the test to internal implementation.

---

# Typical Interview Test

```java
@Test
void follow_ShouldIncludeFolloweeTweets() {

    // Arrange
    Twitter twitter = new Twitter();

    twitter.postTweet(1,5);
    twitter.postTweet(2,6);

    // Act
    twitter.follow(1,2);

    List<Integer> tweets =
            twitter.getNewsFeed(1);

    // Assert
    assertEquals(
        List.of(6,5),
        tweets
    );
}
```

This demonstrates nearly every JUnit best practice:

- Uses `@Test`
- Follows Arrange–Act–Assert
- Creates an independent test
- Tests one behavior
- Verifies observable behavior (black-box testing)
- Uses `assertEquals(expected, actual)`
- Compares the full result rather than individual elements


# Lowest Common Ancestor (Binary Tree)

**LeetCode 236**

---

# Intuition

We solve this problem using **Postorder DFS**.

Instead of asking

> "Is this node the LCA?"

each recursive call answers a simpler question:

> **"What is the best answer for my subtree?"**

That answer can be only one of four things:

| Return Value | Meaning |
|--------------|---------|
| `null` | Neither `p` nor `q` found |
| `p` | Only `p` found |
| `q` | Only `q` found |
| `LCA` | Lowest Common Ancestor already found |

The parent simply combines the answers returned by the left and right subtrees.

---

# Java Solution

```java
/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */

class Solution {

    // Time : O(N)
    // Space: O(H) where H is the tree height

    public TreeNode lowestCommonAncestor(TreeNode root,
                                         TreeNode p,
                                         TreeNode q) {

        // Base Case 1
        if(root == null)
            return null;

        // Base Case 2
        // If we found either p or q,
        // return it immediately.
        if(root == p || root == q)
            return root;

        // Explore both subtrees.
        // Each recursive call returns the best answer
        // for its subtree:
        //
        // null
        // p
        // q
        // LCA
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        // One node found on each side.
        // Therefore current node is the LCA.
        if(left != null && right != null)
            return root;

        // Otherwise propagate whichever subtree
        // returned a valid answer.
        if(right == null)
            return left;

        return right;
    }
}
```

---

# Recursive Invariant ⭐

Every recursive call returns

> **The best answer for its subtree.**

That answer can be

```
null

↓

p

↓

q

↓

Lowest Common Ancestor
```

The parent never needs to know which one it received.

It only asks

- Did left return something?
- Did right return something?

If both returned non-null,

```
Current node is the Lowest Common Ancestor.
```

Otherwise,

return whichever subtree returned something.

---

# Example 1

## p and q are on opposite sides

```
        3
      /   \
     5     1
    / \   / \
   6   2 0   8

p = 6

q = 8
```

Expected Answer

```
3
```

---

### Recursive Calls

```
LCA(3)

    left = LCA(5)

        left = LCA(6)

            return 6

        right = LCA(2)

            return null

        return 6

    right = LCA(1)

        left = LCA(0)

            return null

        right = LCA(8)

            return 8

        return 8
```

Back at node 3

```
left = 6

right = 8
```

Both non-null

```
return 3
```

---

# Example 2

## Both nodes are in the left subtree

```
          3
         /
        5
       / \
      6   2
         / \
        7   4

p = 7

q = 4
```

Expected Answer

```
2
```

---

### Recursive Calls

```
LCA(3)

    left = LCA(5)

        left = LCA(6)

            return null

        right = LCA(2)

            left = LCA(7)

                return 7

            right = LCA(4)

                return 4

            left = 7

            right = 4

            return 2
```

Back at node 5

```
left = null

right = 2

return 2
```

Back at node 3

```
left = 2

right = null

return 2
```

Notice

The recursion already found the LCA.

Node 3 simply propagates it upward.

---

# Example 3

## One node is the ancestor of the other

```
        3
       /
      5
     / \
    6   2
       / \
      7   4

p = 5

q = 4
```

Expected Answer

```
5
```

---

### Recursive Calls

```
LCA(3)

    left = LCA(5)
```

At node 5

```
root == p

return 5
```

Notice

The recursion **does NOT continue below.**

It never visits

```
6

2

7

4
```

Back at node 3

```
left = 5

right = null

return 5
```

---

# Why don't we search below?

Suppose

```
p = 5

q = 4
```

As soon as recursion reaches node **5**

```
if(root == p || root == q)
    return root;
```

The recursion immediately returns.

Why?

Because

- 5 is already one of the target nodes.
- If the other node exists anywhere below,
  then 5 is automatically the Lowest Common Ancestor.

Searching deeper cannot produce a lower ancestor.

---

# Two Different Types of Returns

## 1. Base Case Return

```java
if(root == p || root == q)
    return root;
```

Stops recursion immediately.

Children are never explored.

---

## 2. Propagation Return

```java
if(left != null && right != null)
    return root;

return left != null ? left : right;
```

This simply propagates the best answer already found.

---

# Complexity

## Time

```
O(N)
```

Every node is visited once.

---

## Space

```
O(H)
```

where

```
H = Height of Tree
```

Worst Case

```
O(N)
```

Balanced Tree

```
O(log N)
```

---

# Interview Explanation (30 seconds)

"I solve this using Postorder DFS.

Each recursive call returns the best answer for its subtree, which can be:

- null
- p
- q
- or an LCA already found deeper in the tree.

After exploring both subtrees:

- If both left and right return non-null values, one target was found in each subtree, so the current node is the Lowest Common Ancestor.
- Otherwise, I propagate whichever subtree returned a valid answer upward.

The algorithm visits every node once, giving O(N) time complexity with O(H) recursion stack space."