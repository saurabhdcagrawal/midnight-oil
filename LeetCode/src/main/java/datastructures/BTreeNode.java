package main.java.datastructures;
import java.util.*;
import java.util.Stack;

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

// Creating Mirror of tree will also use post Order Traversal

    public static void getMirrorOfBinaryTree(BTreeNode root){
     if(root==null)
         return;
     getMirrorOfBinaryTree(root.left);
     getMirrorOfBinaryTree(root.right);
     BTreeNode temp=root;
     root.left=root.right;
     root.right=temp;
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
//in reality q.add and q.remove
    //temp will be the last node ,it gives the deepest node
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

  //leetcode version

        public List<List<Integer>> levelOrder(BTreeNode root) {
            List<List<Integer>> levelNodes = new ArrayList<>();
            if(root==null)
                return levelNodes;
            Queue<BTreeNode> q = new LinkedList<BTreeNode>();
            q.add(root);
            q.add(null);
            List<Integer> level = new ArrayList<Integer>();

            while(!q.isEmpty()) {
                root= (BTreeNode) q.remove();
                if(root==null){
                    levelNodes.add(level);
                    level= new ArrayList<Integer>();
                    if(!q.isEmpty())
                        q.add(null);

                } else {
                    level.add(root.data);
                    if (root.left!=null)
                        q.add(root.left);
                    if(root.right!=null)
                        q.add(root.right);
                }

            }
            q=null;
            return levelNodes;
        }

    //Level with maximum sum
    public int maxLevelSum(BTreeNode root) {
        int sub_sum=0, max_sum=Integer.MIN_VALUE, level=1 ,max_level=0;
        Queue q= new LinkedList<BTreeNode>();
        q.add(root);
        q.add(null);

        while(!q.isEmpty()){
            root=(BTreeNode)q.remove();
            if (root==null){
                if(!q.isEmpty())
                    q.add(null);
                if(sub_sum>max_sum){
                    max_sum=sub_sum;
                    max_level=level;
                }
                sub_sum=0;
                level++;
            }else {
                sub_sum+=root.data;
                if (root.left!=null)
                    q.add(root.left);
                if (root.right!=null)
                    q.add(root.right);
            }
        }
        q=null;
        return max_level;
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

     if(root.left!=null && findMaximumElement(root.left)< root.data)
         return false;
     if(root.right!=null && findMinimumElement(root.right)>root.data)
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

   //add of paths leads to a given sum?
   //when you reach the leaf node ,addition of the path should be null
  public static boolean hasSum(BTreeNode root ,int sum){
     if(root==null)
         return(sum==0);
     else{
          int subsum=sum-root.data;
          return (hasSum(root.left,subsum)||hasSum(root.right,subsum));

     }

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

    public static int heightBTree(BTreeNode root){
     if(root==null)return 0;
     else
         return(1+Math.max(heightBTree(root.left),heightBTree(root.right)));

    }
      public static int getDiameter(BTreeNode root){
          int diameter=new DiameterBTree().getDiameter(root);
          return diameter;
      }

    //diameter of a tree
    //while finding the depth of the tree,left node and the right node for every node
    //you come to a point where you do L+R+1 for every node
    //save this and find the point where you can get max
    public static class DiameterBTree {
        int diameter;

        public  int getDiameter(BTreeNode root) {
            diameter = 0;
            getHeightForDiameter(root);
          return diameter;
        }

        public  int getHeightForDiameter(BTreeNode root) {
            if (root == null)
                return 0;
            else {
                int left = getHeightForDiameter(root.left);
                int right = getHeightForDiameter(root.right);
                diameter = Math.max(diameter, (left + right));
                return (1 + Math.max(left, right));
            }
        }
    }


//height or depth without recursion



    public static int depthNonRecursive(BTreeNode root){
     if (root==null) return 0;
     int level=0;
     BTreeNode temp=null;
     Queue q =new LinkedList();
     q.add(root);
     q.add(null);
     while (!q.isEmpty()){
     temp=(BTreeNode)q.remove();
     ///when level l is complete ,we have already enqueued all nodes of l+1;
     //so time to mark end of next level
     if (temp==null) {
         if (!q.isEmpty())
             q.add(null);
             level = level + 1;

     } else{
             if(temp.left!=null)
                 q.add(temp.left);
             if(temp.right!=null)
                 q.add(temp.right);
           }
      }
      q=null;
      return  level;
     }

    public boolean printAllAncestors(BTreeNode root ,BTreeNode node){

     if (root==null)
         return false;

     if (root.left==node || root.right==node||
             printAllAncestors(root.left,node)|| printAllAncestors(root.right,node)){
         System.out.println(root.data);
          return true;
     }
      return false;
    }

    //give prev=Integer.MIN_VALUE
    public boolean inOrderTraversalBST(BTreeNode root,int prev){
     if (root==null)
         return true;
     if(!inOrderTraversalBST(root.left,prev))
         return false;
      if (root.data<prev)
          return false;
      prev=root.data;
      return inOrderTraversalBST(root.right,prev);

    }

    //

    public BTreeNode kthSmallestElementBST(BTreeNode root,int k ,int count){
     if (root==null)
         return null;
     BTreeNode left=kthSmallestElementBST(root.left,k,count);
     if (left!=null) return left;
     if (++k==count)
         return root;
     return kthSmallestElementBST(root.right,k,count);

    }

    public boolean isSymmetric(BTreeNode root) {
        if(root==null) return true;
        return isMirror(root.left,root.right);

    }


    public boolean isMirror(BTreeNode root1 ,BTreeNode root2){

        if(root1==null && root2==null)
            return true;
        if (root1 ==null || root2==null )
            return false;
        if(root1.data!=root2.data)
            return false;

        return (isMirror(root1.left,root2.right) && isMirror(root1.right,root2.left));


    }

   /* public boolean findLCA(BTreeNode root1 ,BTreeNode root2){

        if (root==null)
            return false;

        if (root.left==node || root.right==node||
                printAllAncestors(root.left,node)|| printAllAncestors(root.right,node)){
            System.out.println(root.data);
            return true;
        }
        return false;
    }*/
//8 null 5 11 null 1 6 null
       //     8
       //   5    11
       // 1   6

  /************************************************Revamp***************/
 /* Binary Tree Inorder Traversal */
  public List<Integer> inorderTraversalRec(BTreeNode root) {
             List<Integer> nodes= new ArrayList<Integer>();
             inorderTraversalRec(root, nodes);
             return nodes;
         }
        public void inorderTraversalRec(BTreeNode root, List<Integer> nodes) {
            if (root != null) {
                inorderTraversalRec(root.left, nodes);
                nodes.add(root.data);
                inorderTraversalRec(root.right, nodes);
            }
        }
        public List<Integer> inorderTraversalIter(BTreeNode root){
            List<Integer> nodes = new ArrayList<Integer> ();
            Stack<BTreeNode> s = new Stack<BTreeNode>();

            while(true) {
                while (root!=null){
                    s.push(root);
                    root=root.left;
                }

                if(s.isEmpty())
                    break;

                BTreeNode node= (BTreeNode)s.pop();
                nodes.add(node.data);

                root=node.right;

            }
            return nodes;
        }


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
      System.out.println("Height of tree " + heightBTree(root));
       System.out.println("Non recursive Height of tree " + depthNonRecursive(root));
       System.out.println("Diameter is "+getDiameter(root));
       //getMirrorOfBinaryTree(root);
       //System.out.println("Mirrored B tree") ;
       //inOrderTraversal(root);

         System.out.println("Sum is 19?"+hasSum(root,19));
         System.out.println("Sum is 18?"+hasSum(root,18));
      System.out.println("Deleting B tree");
      deleteBtree(root);
     }


}
//seperate class solution
class SolutionDiameter {
    int diameter;
    public int diameterOfBinaryTree(BTreeNode root) {
        diameter=0;
        getHeight(root);
        return diameter;
    }

    public int getHeight(BTreeNode root){
        if (root==null)
            return 0;

        else{
            int left= getHeight(root.left);
            int right=getHeight(root.right);
            diameter= Math.max(diameter,left+right);
            return (1+Math.max(left,right));
        }
    }

    class SolutionisBST {
        BTreeNode prev;


        public boolean isValidBST(BTreeNode root) {
            if (root != null)
            {
                if (!isValidBST(root.left))
                    return false;

                // allows only distinct values node
                if (prev != null && root.data <= prev.data)
                    return false;
                prev = root;
                return isValidBST(root.right);
            }
            return true;

        }



    }

}

//remaining problems ,print root to leaf paths
//construct b tree
//