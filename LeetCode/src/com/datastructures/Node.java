package com.datastructures;

import com.datastructures.assist.Result;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import java.util.HashMap;

public class Node {

    int data;
    Node next;

    public Node(int data) {
        this.data = data;
        this.next = null;
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

    public static int size(Node head) {

        int size = 0;
        Node n = head;
        while (n!= null) {
            size++;
            n=n.next;
        }
        return size;
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


}
