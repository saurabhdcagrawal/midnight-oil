package com.company;

import com.datastructures.BTreeNode;

public class MainFunc {

  public static void main(String args[]){

      BTreeNode btree = new BTreeNode(8);

      btree.insertNode(btree,4);
      btree.insertNode(btree,12);
      btree.insertNode(btree,5);
      btree.insertNode(btree,6);
      btree.insertNode(btree,14);
      btree.insertNode(btree,16);



  }
}
