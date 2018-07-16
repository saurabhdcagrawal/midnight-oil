package com.datastructures;

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

    }






}
