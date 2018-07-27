package com.datastructures;

public class TreeNode {
  TreeNode left;
  TreeNode right;
  int data;

  public TreeNode(int data){
  this.data=data;
  left=null;
  right=null;
}

static void  insertNode(int data,TreeNode root){
  if(root==null)
   root=new TreeNode(data);
  else if(data<=root.data)
    insertNode(data,root.left);
  else
    insertNode(data,root.right);
  }

 static void inorderTraversal(TreeNode root){
  if(root!=null){
   inorderTraversal(root.left);
   System.out.println(root.data);
   inorderTraversal(root.right);
  }

 }

 static void preorderTraversal(TreeNode root){
        if(root!=null){
            System.out.println(root.data);
            inorderTraversal(root.left);
            inorderTraversal(root.right);
        }

  }


 public static void main(String args[]){
 TreeNode root = new TreeNode(5);
 insertNode(4,root);
 insertNode(6,root);
 inorderTraversal(root);
  }

}
