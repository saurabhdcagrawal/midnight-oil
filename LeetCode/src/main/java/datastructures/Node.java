package main.java.datastructures;

import main.java.datastructures.assist.Result;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import java.util.HashMap;

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
        while (n!= null) {
            size++;
            n=n.next;
        }
        return size;
    }

    public static void printNode(Node head){
        Node n =head;
        while(n.next!=null){
            System.out.print(n.data+"->");
            n=n.next;
        }
        System.out.print(n.data);
        System.out.println("");
    }

    public void printNode(){
        Node n=this;
        while(n.next!=null){
            System.out.print(n.data+"->");
            n=n.next;
        }
        System.out.print(n.data);
        System.out.println("");
    }

    public void appendNode( int data) {
        Node n = this;
        while (n.next != null)
            n = n.next;
        Node end = new Node(data);
        n.next = end;
    }

    public static Node deleteNode(Node head, int data) {
        if (head.data == data)
            return head.next;

        Node n = head;
        while (n!=null || n.next != null) {
            if (n.next.data == data) {
                n.next = n.next.next;
                return head;
            }
            n = n.next;
        }
       return head;
    }

    public static Node removeDuplicates(Node head){
    if(head==null) return null;
    Hashtable t = new Hashtable();
    Node n=head;
    Node prev=null;
    while(n!=null){
      if(t.containsKey(n.data)){
      prev.next=n.next;
     }
     else{
      t.put(n.data,n.data);
      prev=n;
     }
      n=n.next;
    }
    return head;
    //cannot be till the penultimate node ,because even last node
    //can be duplicate
   /* while(n.next!=null){
    if(t.containsKey(n.next.data))
        n.next=n.next.next;
     else
       t.put(n.data,n.data);
     n=n.next;
     }*/
    }
   //5->7->9->11
    public Node insertInSortedList(Node head ,int val){
        Node newNode = new Node(val);
        Node current=head;Node temp=head;
        if(head==null) return newNode;
        while(current!=null && current.data<=val){
        temp=current;
        current=current.next;
        }
        temp.next=newNode;
        newNode.next=current;
        return head;
        }
          //5->7->9->11->12
        public void displayLinkedListFromEnd(Node head){
        if(head==null)
        return;
        else{
            displayLinkedListFromEnd(head.next);
            System.out.print(head.data);
          }
        }



   /* public static Node removeDuplicatesLinkedList(Node head){

        Hashtable t = new Hashtable();
        Node n =head;
        llMap.put(n.data,n.data);
        while(n!=null|| n.next!=null){
        if (llMap.containsKey(n.next.data))
            n.next=n.next.next;
        else
            llMap.put(n.next.data,n.next.data);
        n=n.next;
        }

        return head;
    }*/
      public static boolean removeMiddleNode(Node c){
        //edge cases

       if (c.next==null||c.next==null)
       return false;
       Node d=c.next;
       c.data=d.data;
       c.next=d.next;
       return true;
      }

      //O(max(m,n))
      public static Node addLL(Node number1 ,Node number2){
      Node dummy = new Node(0);
      Node p=number1;
      Node q=number2;
      Node sum=dummy;
      int carryover=0,val1=0,val2=0,sum_result=0;

      while(p!=null ||q!=null){

        val1=p!=null?p.data:0;
        val2=q!=null?q.data:0;
        sum_result=val1+val2+carryover;
        sum.next=new Node(sum_result%10);
        carryover=sum_result/10;
        p=p.next;q=q.next;
        sum=sum.next;
      }
      if(carryover>0)
      sum.next=new Node(carryover);

      return dummy.next;
      }



     //1->2->3->4->5//reduces to remove the L-n+1
      public static void removeNthNodeFromList(Node head,int n){

      Node p1=head,p2=head;
      int count=0;
      while(count<n+1){
          p1=p1.next;
          count++;
      }
      }

     //Determine loop linked list
    //at the point of meet ,they are k nodes away from the mstart of the loop
   public Node getBeginning(Node head){

    if(head==null)
        return null;

          Node fastPointer=head;
          Node slowPointer=head;
//add condn
    while (fastPointer!=null && fastPointer.next!=null){
           slowPointer=slowPointer.next;
           fastPointer=fastPointer.next.next;
           if(slowPointer==fastPointer)
           break;
    }
    //check for no null
    if(fastPointer==null || fastPointer.next==null)
    return null;

    slowPointer=head;
    while(slowPointer!=fastPointer){
    slowPointer=slowPointer.next;
    fastPointer=fastPointer.next;
    }

    return fastPointer;
    }

    public Node determineLoopHashTable(Node head){
        if(head==null)
            return null;
        Node n= head;
        java.util.Hashtable t = new java.util.Hashtable();
        while(n!=null){
         if(t.containsKey(n)){
          return  n;
         }
         else{
          t.put(n,n);
         }
          n=n.next;
         }
         return  null;
      }

     // 5->7->8->10->12


    //3
   //5->7->8->9->1
   public Node insertNodePositionGiven(Node head ,int position,int newVal){
          Node n =head;
    for(int i=0;i<position-1;i++){
     n=n.next;
    }
    Node newNode = new Node(newVal);
    Node temp=n.next;
    n.next=newNode;
    newNode.next=temp;
    return head;
   }

    public Node getIntersectionNode(Node headA, Node headB) {
        int aLength=0,bLength=0;
        Node p1=headA ;Node p2=headB;
        while(p1!=null && p1.next!=null){
            aLength++;
            p1=p1.next;
        }
        aLength++;

        while(p2!=null && p2.next!= null){
            bLength++;
            p2=p2.next;
        }
        bLength++;

        if(p1!=p2)
            return null;

        int lengthDiff=Math.abs(aLength-bLength);
        Node longerNode=aLength>=bLength?headA:headB;
        Node shorterNode= longerNode==headA?headB:headA;

        for(int i=0;i<lengthDiff;i++)
            longerNode=longerNode.next;

        while(longerNode!=shorterNode){
            longerNode=longerNode.next;
            shorterNode=shorterNode.next;
        }
        return shorterNode;
    }




    //reverse linked list



      //Or put in a hashtable
     public static Node findIntersectingNode(Node n1 ,Node n2) {
          if(n1==null||n2==null)
              return null;
         Result res1=getTailAndLength(n1);
         Result res2=getTailAndLength(n2);
         Node n1_last_node=res1.getTail();
         int length1=res1.getLength();
         Node n2_last_node=res2.getTail();
         int length2=res2.getLength();
         if(n1_last_node!=n2_last_node)
          return null;
         int delta=0;
         Node longer=null,shorter=null;
         delta=Math.abs(length1-length2);
         if(length1>length2){
              longer=n1;shorter=n2;
         }
         else{
             longer=n2;shorter=n1;
         }


         for(int i=0;i<delta;i++){
             longer=longer.next;
         }

         while(shorter!=longer){
             longer=longer.next;
             shorter=shorter.next;
             }
         return longer;
     }
     public static Result getTailAndLength(Node head){
             if(head==null) return null;
             Node n=head;
             int length=0;
             while (n.next != null) {
             n = n.next;
             length++;
         }
            length++;
            Result result= new Result();
            result.setLength(length);
            result.setTail(n);
            return result;
         }
         //          | |
         //1->4->5->7->9
         //n=4
    //p2.next ,comes to the last node but does not go in while loop
    //p2==null goes to all node ,so print last data ,when coming out refremce
    //points to null
    public Node getNthNodeFromEnd(Node head ,int n){
          if(head==null || n==1) return null;
          Node p1=head ,p2=head;
          //p2 advances by n-1
          for(int i=1;i<=n;i++){
              //length of list is less than n
              if(p2==null)
                  return null;
              p2=p2.next;
          }
          while(p2!=null){
          p1=p1.next;
          p2=p2.next;
          }
          return  p1;
         }
         //L+N-1;

    public Node getNthNodeFromEndHashTable(Node head ,int n){
        HashMap <Integer,Node> nodeMap= new HashMap<Integer, Node>();
        Node p=head;int count=1;
        while(p!=null){
         nodeMap.put(count,p);
         count++;
         p=p.next;
         }
         int length=count;
         return nodeMap.get(length+1-n);
    }

     public static Node findMiddleNode(Node head){
         if (head==null ||head.next==null) return head;
          Node p1 =head,p2=head;
         //1->2->3->4->5->6
         //1->2->3
         while (p2!=null&& p2.next!=null&&p2.next.next!=null) {
             p1 = p1.next;
             p2 = p2.next.next;
         }
          return p1;
         }

          //5->4->3->2->1
          public static Node reverseLinkedList(Node head){
          //3 pointers
          Node current=head, previous=null,nextNode= null;
          while(current!=null){
           nextNode=current.next;
           current.next=previous;
           previous=current;
           current=nextNode;
           }
           head=previous;
           return  head;
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
*/       public static boolean isPalindromeLinkedList(Node head){
          if(head==null) return true;
          System.out.println("Printing original Node");
          printNode(head);
          Node middleNode=findMiddleNode(head);
          System.out.println("Printing middle node " +middleNode.data);
          Node secondHead=reverseLinkedList(middleNode.next);
          middleNode.next=null;
          System.out.println("Second half");
          printNode(secondHead);
          System.out.println("First half");
          printNode(head);
          Node p1=head,p2=secondHead;
          Boolean result=true;
          while(p2!=null){
          if (p1.data!=p2.data)
              result= false;
          p1=p1.next;
          p2=p2.next;
          }
           //boolean result=isLinkedListIdentical(head,secondHead);
           Node secondHalf=reverseLinkedList(secondHead);
           System.out.println("Getting back Original Second Half");
           printNode(secondHalf);
           middleNode.next=secondHalf;
           System.out.println("Printing linked list again");
           printNode(head);
           return result;
         }
       //concise
     /*  public boolean isPalindrome(ListNode head) {
           if(head==null) return true;
           ListNode middleNode=findMiddleNode(head);
           ListNode secondHead=reverseLinkedList(middleNode.next);
           middleNode.next=null;
           ListNode p1=head ,p2=secondHead;
           boolean result=true;
           while(p2!=null){
               if(p1.val!=p2.val)
                   result=false;
               p1=p1.next;
               p2=p2.next;
           }
           ListNode secondHalf=reverseLinkedList(secondHead);
           middleNode.next=secondHalf;
           return result;

       }

    public ListNode findMiddleNode(ListNode head){
        if(head==null || head.next==null) return head;
        ListNode p1=head,p2=head;
        while(p2!=null &&p2.next!=null && p2.next.next!=null){
            p1=p1.next;
            p2=p2.next.next;
        }
        return p1;
    }

    public ListNode reverseLinkedList(ListNode head){
        ListNode current=head ,nextNode=null,previous=null;
        while(current!=null){
            nextNode=current.next;
            current.next=previous;
            previous=current;
            current=nextNode;
        }

        head=previous;
        return head;
    }*/
       public boolean isLinkedListIdentical(Node n1 ,Node n2){
       if(n1==null && n2 ==null) return true;
           while (n1 != null && n2 != null) {
            if(n1.data!=n2.data)
               return false;
            n1=n1.next;n2=n2.next;
            }
            //Are both null at this point?
            if(n1==null&& n2==null)
             return true;

         return false;
       }


    public static Node mergeTwoLists(Node l1, Node l2) {
        Node dummy=new Node(0);
        Node result=dummy;
        while(l1!=null && l2!=null){
            if(l1.data<=l2.data){
                result.next=l1;
                l1=l1.next;
            }
            else{
                result.next=l2;
                l2=l2.next;
            }
            result=result.next;
        }
        if(l1!=null)
            result.next=l1;
        else if (l2!=null)
           result.next=l2;
         return dummy.next;
    }

    public static void main (String args[]){
         Node n = new Node (1);
         n.appendNode(2);
         n.appendNode(3);
         //n.appendNode(4);
         //n.appendNode(5);
          // n.appendNode(6);
         n.printNode(n);
         Node middle=n.findMiddleNode(n);
         System.out.println("Middle Node "+middle.data);
        System.out.println("Starting Palindrome");
           System.out.println("Even Length Linked List");
        Node palin= new Node(5);
        palin.appendNode(7);
        palin.appendNode(9);
        palin.appendNode(9);
        palin.appendNode(7);
        palin.appendNode(5);
        System.out.println(isPalindromeLinkedList(palin)==true?"True":"False");
           System.out.println("Odd Length Linked List");
           Node palin_odd= new Node(5);
           palin_odd.appendNode(7);
           palin_odd.appendNode(9);
           palin_odd.appendNode(7);
           palin_odd.appendNode(5);
           System.out.println(isPalindromeLinkedList(palin_odd)==true?"True":"False");

        System.out.println("Merge node starts");
           Node l1=new Node(1);
       l1.appendNode(2);
       l1.appendNode(4);
       l1.appendNode(9);
       System.out.println("Printing first node");
       printNode(l1);
       Node l2= new Node(1);
       l2.appendNode(3);
       l2.appendNode(4);
       System.out.println("Printing second node");
       printNode(l2);
       Node result =mergeTwoLists(l1,l2);
        System.out.println("Printing result");
        printNode(result);

       }


}
