package main.java.datastructures;

import java.util.HashMap;

public class DataStructures {

    public static void main (String args[]){
     Node ll= new Node (10);
     ll.appendNode(10);
     ll.appendNode(10);
     Node.printNode(ll);
     //ll.deleteNode(ll,30);

        //Node dupRemovedNode=Node.removeDuplicatesLinkedList(ll);
        //Node.printNode(dupRemovedNode);

        Node number1= new Node(2);
        number1.appendNode(4);
        number1.appendNode(3);
        Node.printNode(number1);



        Node number2= new Node(5);
        number2.appendNode(6);
        number2.appendNode(4);
        Node.printNode(number2);
        Node sum=Node.addLL(number1,number2);
        Node.printNode(sum);
        HashMap h= new HashMap();
        System.out.println("Duplicates removed code");
       Node n= new Node(5);
       n.appendNode(6);
       n.appendNode(7);
       n.appendNode(8);
       n.appendNode(10);
       n.appendNode(10);
       n.printNode();
       Node dups_removed=Node.removeDuplicates(n);
       dups_removed.printNode();

       System.out.println("Reversed linked list");
        Node toBeRev= new Node(2);
        toBeRev.appendNode(4);
        toBeRev.appendNode(3);
        toBeRev.appendNode(10);
        Node.printNode(toBeRev);
        Node reversed=Node.reverseLinkedList(toBeRev);
        Node.printNode(reversed);
    }






}
