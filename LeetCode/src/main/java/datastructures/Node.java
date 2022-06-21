package main.java.datastructures;

import main.java.datastructures.assist.Result;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

//inside same LLnode then reference head using this
/* Base condition , head condition
2 ways prev pointer and n.next
condition n.null && n.next ! =null
 */
/*
        while(diff>0){
        longerNode=longerNode.next;
        diff--;
        }*/
public class Node {

    int data;
    Node next;

    public Node(int data) {
        this.data = data;
        this.next = null;
    }

    public static int size(Node head) {

        int size = 0;
        Node n = head;
        while (n != null) {
            size++;
            n = n.next;
        }
        return size;
    }

    public static void printNode(Node head) {
        Node n = head;
        while (n.next != null) {
            System.out.print(n.data + "->");
            n = n.next;
        }
        System.out.print(n.data);
        System.out.println("");
    }

    public void printNode() {
        Node n = this;
        while (n.next != null) {
            System.out.print(n.data + "->");
            n = n.next;
        }
        System.out.print(n.data);
        System.out.println("");
    }

    public void appendNode(int data) {
        Node n = this;
        while (n.next != null)
            n = n.next;
        Node end = new Node(data);
        n.next = end;
    }
//Note that it returns head
    public static Node deleteNode(Node head, int data) {
        if (head.data == data)
            return head.next;

        Node n = head;
        while (n != null && n.next != null) {
            if (n.next.data == data) {
                n.next = n.next.next;
                return head;
            }
            n = n.next;
        }
        return head;
        /*alternate way?
        while(n!=null && n.next!=null && n.next.data==data)
            n = n.next;
        if(n!=null && n.next!=null)
            n.next=n.next.next;
        return head;*/


    }
//Note that it returns head
   // 1->2->3->2->2 reason why prev to be incremented only in the put section
    public static Node removeDuplicates(Node head) {
        if (head == null) return null;
        HashSet t = new HashSet();
        Node n = head;
        Node prev = null;
        while (n != null) {
            if (!t.contains(n.data)) {
                prev = n;
                t.add(n.data);
            } else {
                prev.next = n.next;
            }
            n = n.next;
        }
        return head;
    }

    //5->7->9->11
    public Node insertInSortedList(Node head, int val) {

        Node newNode = new Node(val);
        //always think of edge case for start of the node
        if (head == null) return newNode;

        if (val < head.data) {
            newNode.next = head;
            head = newNode;
            return head;
        }
        Node current = head;
        Node prev = null;
        while (current != null && current.data <= val) {
            prev = current;
            current = current.next;
        }
        prev.next = newNode;
        newNode.next = current;
        return head;
    }

    //Linked list recursion
    //5->7->9->11->12
    public void displayLinkedListFromEnd(Node head) {
        if (head == null)
            return;

        displayLinkedListFromEnd(head.next);
        System.out.print(head.data);

    }


    /* Delete Middle Node: Implement an algorithm to delete a node in the middle (i.e., any node but
the first and last node, not necessarily the exact middle) of a singly linked list, given only access to
that node.*/
    public static boolean removeMiddleNode(Node c) {
        //edge cases
        if (c == null)
            return false;
        //if last node, mark it as null;
        if (c.next == null) {
            c = null;
            return true;
        }
        Node d = c.next;
        c.data = d.data;
        c.next = d.next;
        d = null;
        return true;
    }

    //O(max(m,n))
 /*   You are given two non-empty linked lists representing two non-negative integers.
    The digits are stored in reverse order, and each of their nodes contains a single digit.
    Add the two numbers and return the sum as a linked list.*/
    public static Node addTwoNumbers(Node l1, Node l2) {

        Node n1=l1;
        Node n2=l2;
        Node dummyHead= new Node(0);
        Node n3=dummyHead;
        int sumDigit=0,carryOver=0;
        //or condition because we have to continue even if one is not empty
        //last node because we have to add last digit, hence last node is not null
        //always evaluate with worst cases
        while (n1!=null || n2!=null){
            int digitOne=n1==null?0:n1.data;
            int digitTwo=n2==null?0:n2.data;
            sumDigit= (carryOver+ digitOne+digitTwo)%10;
            carryOver=(carryOver+digitOne+digitTwo)/10;
            n3.next= new Node(sumDigit);
            if(n1!=null)
                n1=n1.next;
            if(n2!=null)
                n2=n2.next;
            n3=n3.next;

        }//Ump step
        if (carryOver!=0)
            n3.next= new Node(carryOver);
        return dummyHead.next;

    }

    //1->2->3->4->5//reduces to remove the L-n+1
    //Testcases [1,2] 2
    //[1]1
    public Node removeNthFromEnd(Node head, int n) {
        Node l =head;
        int size=0;
        while(l!=null){
            size++;
            l=l.next;
        }
        // dummy->1->2->3->4->5
        //3
        int diff= size-n;//to remove Node at position L-n+1;
        Node dummy = new Node(0);
        dummy.next=head;
        l =dummy;
        while(diff>0){
            diff--;
            l=l.next;
        }
        l.next=l.next.next;
        return dummy.next;
    }

    public Node removeNthFromEndSinglePass(Node head, int n) {
//takes care of edge cases like [1] and n=1
        Node dummy = new Node(0);
        dummy.next=head;
        Node p1 =dummy;
        Node p2 =dummy;
        //if n=2, diff is 3
        int diff=n+1;
        while(diff>0){
            diff--;
            p2=p2.next;
        }

        while(p2!=null){
            p1=p1.next;
            p2=p2.next;
        }
        p1.next=p1.next.next;

        return dummy.next;
    }

    //3
    //dummy->5->7->8->9->1
    public Node insertNodePositionGiven(Node head, int position, int newVal) {
        if(head==null)
            return null;
        Node dummy = new Node(0);
        Node n= dummy;
        int diff=position-1;
        while(diff>0){
            diff--;
            if(n==null)
                return null;
            n=n.next;
        }
        Node newNode= new Node(newVal);
        newNode.next=n.next;
        n.next=newNode;
        return dummy.next;
    }

    public Node determineLoop(Node head){
        if(head==null)
            return null;
        HashMap<Node, Node> t = new HashMap<>();
        Node l=head;
        while(l!=null){
            if(t.containsKey(l))
                return l;
            else
                t.put(l,l);
            l=l.next;
        }
        return null;
    }

    //Determine loop linked list
    //at the point of meet ,they are k nodes away from the mstart of the loop
    public Node getBeginning(Node head) {

        if (head == null)
            return null;

        Node fastPointer = head;
        Node slowPointer = head;
//add condn
        //since we incrementing fastPointer2 times, thats why the 2nd condition in while
        while (fastPointer != null && fastPointer.next != null) {
            slowPointer = slowPointer.next;
            fastPointer = fastPointer.next.next;
            if (slowPointer == fastPointer)
                break;
        }
        //check for no null
        if (fastPointer == null || fastPointer.next == null)
            return null;
//     both meet exactly k nodes before start of head;
        slowPointer = head;
        while (slowPointer != fastPointer) {
            slowPointer = slowPointer.next;
            fastPointer = fastPointer.next;
        }

        return fastPointer;
    }

    public Node determineLoopHashTable(Node head) {
        if (head == null)
            return null;
        Node n = head;
        java.util.Hashtable t = new java.util.Hashtable();
        while (n != null) {
            if (t.containsKey(n)) {
                return n;
            } else {
                t.put(n, n);
            }

            n = n.next;
        }
        return null;
    }

    // 5->7->8->10->12



    public static Node getIntersectionNode(Node headA, Node headB) {
        int aLength = 0, bLength = 0;
        Node p1 = headA;
        Node p2 = headB;
        while (p1.next != null) {
            aLength++;
            p1 = p1.next;
        }

        while (p2.next != null) {
            bLength++;
            p2 = p2.next;
        }
        //incrementing length coz we stopped at last
        aLength++;
        bLength++;

     // if both LL are intersecting then the last node should be the same, if different then it means they are non-intersecting
        if (p1 != p2)
            return null;

        int lengthDiff = Math.abs(aLength - bLength);
        Node longerNode = aLength >= bLength ? headA : headB;
        Node shorterNode = longerNode == headA ? headB : headA;

        for (int i = 0; i < lengthDiff; i++)
            longerNode = longerNode.next;
       /* while(lengthDiff>0){
            lengthDiff--;
            longerNode=longerNode.next;
        }
        */

        while (longerNode != shorterNode) {
            longerNode = longerNode.next;
            shorterNode = shorterNode.next;
        }
        return shorterNode;
    }

    public Node getIntersectionNodeOptimum(Node headA, Node headB) {
        Node pA=headA;
        Node pB=headB;
        while(pA!=pB){
            pA=pA==null?headB:pA.next;
            pB=pB==null?headA:pB.next;
        }
        return pA;
    }
    //reverse linked list



    public static Node findMiddleNode(Node head) {
        if (head == null || head.next == null) return head;
        Node p1 = head, p2 = head;
        //1->2->3->4->5->6
        //1->2->3
        while (p2 != null && p2.next != null) {
            p1 = p1.next;
            p2 = p2.next.next;
        }
        return p1;
    }

    //5->4->3->2->1
    public static Node reverseLinkedList(Node head) {
        //3 pointers
        Node current = head, previous = null, nextNode = null;
        while (current != null) {
            nextNode = current.next;
            current.next = previous;
            previous = current;
            current = nextNode;
        }
        head = previous;
        return head;
    }
   //    1->2<-3<-4<-5
   //               |
   //   head|reverseLL
    public Node reverseListRecursive(Node head) {

        if(head==null||head.next==null)
            return head;

        Node reversedLL=reverseListRecursive(head.next);
        head.next.next=head;
        head.next=null;
        return reversedLL;
    }
    //2 nodes con and tail
    public Node reverseBetween(Node head, int left, int right) {
        Node prev=null;
        Node current=head;
        Node nextNode=null;
        for(int i=1;i<left;i++){
            prev=current;
            current=current.next;
        }
        Node con=prev;
        Node tail=current;
        while(right>=left && current!=null){
            right--;
            nextNode=current.next;
            current.next=prev;
            prev=current;
            current=nextNode;
        }
        //after current will point to lastNode, next will point to node after
        //if the first node is included in left, then new head will be prev
        if(con!=null)
            con.next=prev;
        else
            head=prev;

        tail.next=current;
        return head;
    }

    // 5->7->9->22->33
    // 5->7->9->sh7->5
    // 5->7->9->
    //     5->7

    /*      1) Get the middle of the linked list.
            2) Reverse the second half of the linked list.
            3) Check if the first half and second half are identical.
            4) Construct the original linked list by reversing the second half again
            and attaching it back to the first half
  */
    public static boolean isPalindromeLinkedList(Node head) {
        if (head == null) return true;
        /*System.out.println("Printing original Node");
        printNode(head);*/
        Node middleNode = findMiddleNode(head);
       // System.out.println("Printing middle node " + middleNode.data);
        Node secondHead = reverseLinkedList(middleNode);
        //reversing the second LL will create a null for second LL so use the pointer on second Node
       // Wrong approach middleNode.next = null;
       /* System.out.println("Second half");
        printNode(secondHead);
        System.out.println("First half");
        printNode(head);
       */
        Node p1 = head, p2 = secondHead;
        Boolean result = true;
        while (p2 != null) {
            if (p1.data != p2.data)
                result = false;
            p1 = p1.next;
            p2 = p2.next;
        }
        Node secondHalf = reverseLinkedList(secondHead);
        return result;
        //System.out.println("Getting back Original Second Half");
        //printNode(secondHalf);
       // Wrong technique middleNode.next = secondHalf;
       // System.out.println("Printing linked list again");
       // printNode(head);
        }
    /*You are given the head of a singly linked-list. The list can be represented as:
    L0 → L1 → … → Ln - 1 → Ln
    Reorder the list to be on the following form:
    L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …*/

   /* Find a middle node of the linked list. If there are two middle nodes, return the second middle node.
    //go to middle node
    Example: for the list 1->2->3->4->5->6, the middle element is 4.

    Once a middle node has been found, reverse the second part of the list.
        Example: convert 1->2->3->4->5->6 into 1->2->3->4 and 6->5->4.

    Now merge the two sorted lists. Example: merge 1->2->3->4 and 6->5->4 into 1->6->2->5->3->4.*/
    public void reorderList(Node head) {

        if(head==null)
            return ;

        Node middleNode=findMiddleNode(head);
        Node reversedSecondHalf=reverseLinkedList(middleNode);
        mergeSortedLinkedListsDiff(head,reversedSecondHalf);
    }
    public void mergeSortedLinkedListsDiff(Node head1, Node head2) {
        Node first = head1;
        Node second = head2;
        while (second.next != null) {
            Node firstNext = first.next;
            Node secondNext = second.next;
            first.next = second;
            second.next = firstNext;
            first = firstNext;
            second = secondNext;
        }

    }
    public boolean isLinkedListIdentical(Node n1, Node n2) {
        if (n1 == null && n2 == null) return true;
        if (n1 == null || n2 == null) return false;
        while (n1 != null && n2 != null) {
            if (n1.data != n2.data)
                return false;
            n1 = n1.next;
            n2 = n2.next;
        }
        //Are both null at this point?
        if (n1 != null || n2 != null)
            return false;


        return true;
    }


    public static Node mergeTwoSortedLists(Node l1, Node l2) {
        Node dummy = new Node(0);
        Node result = dummy;
        while (l1 != null && l2 != null) {
            if (l1.data <= l2.data) {
                result.next = l1;
                l1 = l1.next;
            } else {
                result.next = l2;
                l2 = l2.next;
            }
            result = result.next;
        }
        if (l1 != null)
            result.next = l1;
        else if (l2 != null)
            result.next = l2;
        return dummy.next;
    }
    // Find the right most Non nine node , increment it and set the folowing nodes to 0
    //Use sentinel node for cases such as 9->9->9
    //Given a non-negative integer represented as a linked list of digits, plus one to the integer.
    public Node plusOneLL(Node head) {
        Node sentinelNode= new Node(0);
        sentinelNode.next=head;
        Node rightMostNonNineNode=sentinelNode;
        Node n =head;
        while(n!=null){
            if(n.data!=9)
                rightMostNonNineNode=n;
            n=n.next;
        }

        rightMostNonNineNode.data+=1;

        n = rightMostNonNineNode.next;

        while(n!=null) {
            n.data=0;
            n=n.next;
        }
        return sentinelNode.data==0?head:sentinelNode;
    }
    //1) Brute force approach collect all lists in Array/ArrayList, sort and then recreate new linkedList with the sorted values
//Time Complexity O(NlogN)
//Space Complexity O(N)
//(2) Merge all sorted lists with K pointers...this will take O(NK) time complexity and O(N) space complexity
//(3) Using min Heap of size k -> O(NlogK) and O(N+K) space
//(4) Merge 2 lists at a time takes O(N) space, perform K-1 times...O(NK) ..space complexity O(1)
    //O (NlogK)  size O(N) + O(K)
    public Node mergeKLists(Node[] lists) {
        PriorityQueue<Node> pq = new PriorityQueue<Node>((a, b)->Integer.compare(a.data,b.data));
        //Adding only heads?
        // Pq has K nodes at a time ..making this would take O(K) time worst case O(KLogK)
        for(Node listHead: lists){
            if(listHead!=null)
                pq.add(listHead);
        }
        Node dummy= new Node(0);
        Node result=dummy;
        //size of heap is maintained K nodes at a time
        while(!pq.isEmpty()){
            result.next=pq.poll();
            result=result.next;
            Node remainderLLNode=result.next;
            if(remainderLLNode!=null)
                pq.add(remainderLLNode);
        }
        return dummy.next;

    }

    public static void main(String args[]) {
        Node n = new Node(1);
        n.appendNode(2);
        n.appendNode(3);
        //n.appendNode(4);
        //n.appendNode(5);
        // n.appendNode(6);
        n.printNode(n);
        Node middle = n.findMiddleNode(n);
        System.out.println("Middle Node " + middle.data);
        System.out.println("Starting Palindrome");
        System.out.println("Even Length Linked List");
        Node palin = new Node(5);
        palin.appendNode(7);
        palin.appendNode(9);
        palin.appendNode(9);
        palin.appendNode(7);
        palin.appendNode(5);
        System.out.println(isPalindromeLinkedList(palin) == true ? "True" : "False");
        System.out.println("Odd Length Linked List");
        Node palin_odd = new Node(5);
        palin_odd.appendNode(7);
        palin_odd.appendNode(9);
        palin_odd.appendNode(7);
        palin_odd.appendNode(5);
        System.out.println(isPalindromeLinkedList(palin_odd) == true ? "True" : "False");

        System.out.println("Merge node starts");
        Node l1 = new Node(1);
        l1.appendNode(2);
        l1.appendNode(4);
        l1.appendNode(9);
        System.out.println("Printing first node");
        printNode(l1);
        Node l2 = new Node(1);
        l2.appendNode(3);
        l2.appendNode(4);
        System.out.println("Printing second node");
        printNode(l2);
        Node result = mergeTwoSortedLists(l1, l2);
        System.out.println("Printing result");
        printNode(result);
        Node revisited = new Node(7);
        revisited.appendNode(10);
        revisited.appendNode(11);
        revisited.printNode();
        Node newOrder = revisited.insertInSortedList(revisited, 5);
        //5->7->10->11
        newOrder.printNode();
        System.out.println("Intersection Problem -------");
        l2.printNode();
        revisited.printNode();
        Node intersectionNode= getIntersectionNode(l2,revisited);
        System.out.println(intersectionNode);

    }


}
