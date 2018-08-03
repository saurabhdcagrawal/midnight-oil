package main.java.datastructures;
import java.util.LinkedList;
import  java.util.Stack;
import java.util.Queue;
public class BTreeNode{
 int data;
 BTreeNode left;
 BTreeNode right;

 public BTreeNode(int data){
 this.data=data;
 left=null;
 right=null;
 }

 public static void inOrderTraversal(BTreeNode root){
   if(root!=null){
    inOrderTraversal(root.left);
    System.out.println(root.data);
    inOrderTraversal(root.right);
   }
 }

 public static void inorderTraversalIterative(BTreeNode root){
 Stack s = new Stack();
     while(true){
         while(root!=null){
             //For preorder visit current node first ,hence we will print node here and visit
             s.push(root);
         root=root.left;
         }

         if(s.isEmpty())
             break;

         root=(BTreeNode)s.pop();
         System.out.println(root.data);
         root=root.right;
     }
 }


    public static void preOrderTraversal(BTreeNode root){
        if(root!=null){
            System.out.println(root.data);
            preOrderTraversal(root.left);
            preOrderTraversal(root.right);
        }
    }

    public static void postOrderTraversal(BTreeNode root){
        if(root!=null){
            inOrderTraversal(root.left);
            inOrderTraversal(root.right);
            System.out.println(root.data);
        }
    }
   //Delete uses  post order traversal
    public static void deleteBtree(BTreeNode root){
        if(root==null) return;
        deleteBtree(root.left);
        deleteBtree(root.right);
        System.out.print("Deleting current Node "+root.data);
        root=null;

    }
 //no linkages
 public static void insertNode(BTreeNode root, int data) {
      if (data<root.data){
       if(root.left!=null)
        insertNode(root.left,data);
      else
       root.left=new BTreeNode(data);
      }
   else{
        if(root.right!=null)
         insertNode(root.right,data);
        else
         root.right=new BTreeNode(data);
        }

   }

   public static void levelOrderTraversal(BTreeNode root){
     if (root==null) return;
     BTreeNode temp;
     Queue q =new LinkedList();
     q.add(root);
     while(!q.isEmpty()){
     temp=(BTreeNode)q.remove();
     System.out.println(temp.data);
     if(temp.left!=null)
        q.add(temp.left);
     if(temp.right!=null)
        q.add(temp.right);
     }
      q=null;
     }

     public static int findMinimumElement(BTreeNode root) {
     if (root==null) return -1;
         while (root.left != null) {
             root = root.left;
         }
         return root.data;
     }
    public static int findMaximumElement(BTreeNode root) {
        if (root==null) return -1;
        while (root.right != null) {
            root = root.right;
        }
        return root.data;
    }

    public static boolean validateIsBST(BTreeNode root){
     if (root==null)
         return true;

     if(root.left!=null && findMaximumElement(root.left)> root.data)
         return false;
     if(root.right!=null && findMinimumElement(root.right)<root.data)
         return false;

     return (validateIsBST(root.left) && validateIsBST(root.right));

    }



    public static boolean isStructurallyIdentical(BTreeNode root1 ,BTreeNode root2){
        if (root1==null&& root2==null)
            return true;
        else if(root1==null ||root2==null)
            return false;
        else if(root1.data!=root2.data)
            return false;
        return (isStructurallyIdentical(root1.left,root2.left) &&
                isStructurallyIdentical(root1.right,root2.right));

        }

    public static boolean isMirrorOfEachOther(BTreeNode root1 ,BTreeNode root2){
        if (root1==null&& root2==null)
            return true;
        else if(root1==null ||root2==null)
            return false;
        else if(root1.data!=root2.data)
            return false;
        return (isStructurallyIdentical(root1.left,root2.right) &&
                isStructurallyIdentical(root1.right,root2.left));

    }

       //     8
       //   5    11
       // 1   6
     public  static void main(String args[]){
      BTreeNode root= new BTreeNode(8);
      BTreeNode.insertNode(root,11);
      BTreeNode.insertNode(root,5);
      BTreeNode.insertNode(root,1);
      BTreeNode.insertNode(root,6);
      System.out.println("Inorder Traversal");
      inOrderTraversal(root);
      System.out.println("Inorder Traversal Iterative");
      inorderTraversalIterative(root);
      System.out.println("Level Order Traversal");
      levelOrderTraversal(root);
      System.out.println("Minimum element "+findMinimumElement(root));
      System.out.println("Maximum element "+findMaximumElement(root));
      System.out.println("Validate BST "+ validateIsBST(root));
      BTreeNode temp =new BTreeNode(10);
      temp.left=new BTreeNode(15);
      temp.right=new BTreeNode(12);
      System.out.println("Validate BST "+ validateIsBST(temp));
      System.out.println("Deleting B tree");
      deleteBtree(root);
     }





}