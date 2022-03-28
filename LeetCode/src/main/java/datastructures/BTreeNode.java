package main.java.datastructures;
import java.util.*;
import java.util.Stack;
//most algorithms fit into pre-order traversals
//delete b tree
//create mirror --postorder
//kths smallest and other things into in-order
//BFS is queue (level order) if you want level, you put null in a queue
//DFS techniques are pre-order,in-order and postorder
/*Time complexity: O(N), where N is number of nodes, since we visit each node not more than 2 times.
        Space complexity: O(H), where H is a tree height, to keep the recursion stack.
        In the average case of balanced tree, the tree height H = log N in the worst case of skewed tree, H = N*/
public class BTreeNode{
 int data;
 BTreeNode left;
 BTreeNode right;

 public BTreeNode(int data){
 this.data=data;
 left=null;
 right=null;
 }
//1.Visit the leftsubtree in orderManner 2 Visit the current Node 3. Visit the right sub tree in Inorder Traversal fashion
 public static void inOrderTraversal(BTreeNode root){
   if(root!=null){
    inOrderTraversal(root.left);
    System.out.println(root.data);
    inOrderTraversal(root.right);
   }
 }

 public static void inorderTraversalIterative(BTreeNode root){
     //While with true and break later
     //Stack keep pushing root
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
    public static BTreeNode getMirrorOfBinaryTree(BTreeNode root){
     if(root==null)
         return null;
     getMirrorOfBinaryTree(root.left);
     getMirrorOfBinaryTree(root.right);
     BTreeNode temp=root;
     root.left=root.right;
     root.right=temp;
     return root;
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
//when you reach null add null
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

    //preorder traversal
    public static boolean validateIsBST(BTreeNode root){
     if (root==null)
         return true;

     if(root.left!=null && findMaximumElement(root.left)< root.data)
         return false;
     if(root.right!=null && findMinimumElement(root.right)>root.data)
         return false;

     return (validateIsBST(root.left) && validateIsBST(root.right));

    }


//preorder traversal
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
  //pre order traversal
        public static boolean isMirrorOfEachOther(BTreeNode root1 ,BTreeNode root2){
        if (root1==null&& root2==null)
            return true;
        else if(root1==null ||root2==null)
            return false;
        else if(root1.data!=root2.data)
            return false;
        return (isMirrorOfEachOther(root1.left,root2.right) &&
                isMirrorOfEachOther(root1.right,root2.left));

    }
    public boolean isSymmetric(BTreeNode root) {
        if(root==null) return true;
        return isMirrorOfEachOther(root.left,root.right);

    }

   //add of paths leads to a given sum?
   //when you reach the leaf node ,addition of the path should be null
  public static boolean hasSum(BTreeNode root ,int sum){
     if(root==null)
         return(sum==0);

     return (hasSum(root.left,sum- root.data)||hasSum(root.right,sum- root.data));

 }
//leetcode style when you want the empty root with 0 input combo as false
    /*public boolean hasPathSum(BTreeNode root, int targetSum) {
        if(root==null)
            return false;
        if(root.left==null && root.right==null)
            return(targetSum-root.data==0);
        return (hasPathSum(root.left,targetSum-root.data)||hasPathSum(root.right,targetSum-root.data));
    }*/

//preorder traversal
    public static int heightBTree(BTreeNode root){
     if(root==null)return 0;
     else
         return(1+Math.max(heightBTree(root.left),heightBTree(root.right)));

    }

    //for every node of the tree, check if the subtree starting that node is identical to given subtree in a pre-order fashion
    //Maximium do it for everyNode m and do n comparisons if sub root has n nodes making it total O(mn)
    public static boolean isSubtree(BTreeNode root, BTreeNode subRoot) {
        if(root==null)
            return false;
        if(isStructurallyIdentical(root,subRoot))
            return true;
        return (isSubtree(root.left,subRoot)||isSubtree(root.right,subRoot));
    }

    public static String serialize(BTreeNode root){
        return serialize(root,"");
    }
    public static String serialize(BTreeNode root,String str){
        if(root==null){
            str+="null,";
            return str;
        }
        else{
            str+=String.valueOf(root.data)+",";
            str=serialize(root.left,str);
            str=serialize(root.right,str);
            return str;
        }
    }

    public static BTreeNode deserialize(String data){
        String[] dataArr=data.split(",");
        List<String> dataList= new LinkedList<>(Arrays.asList(dataArr));
        return deserialize(dataList);
    }
    public static BTreeNode deserialize(List<String> data){
        if(data.get(0).equals("null")){
            data.remove(0);
            return null;
        }
        else{
            BTreeNode root=new BTreeNode(Integer.valueOf(data.get(0)));
            data.remove(0);
            root.left=deserialize(data);
            root.right=deserialize(data);
            return root;
        }
    }


   //W Construct Binary Tree from Preorder and Inorder Traversal
   // go on with preorder index
  /* Start from not inorder traversal, usually it's preorder or postorder one, and use the traversal picture above to define
  the strategy to pick the nodes. For example, for preorder traversal the first value is a root, then its left child,
  then its right child, etc. For postorder traversal the last value is a root, then its right child, then its left child, etc.
  The value picked from preorder/postorder traversal splits the inorder traversal into left and right subtrees.
  The only information one needs from inorder - if the current subtree is empty (= return None) or not (= continue to
  construct the subtree).
  For postorder we get the root nodes from right... the last node is the root.. the node after that is the root of right subtree
  //so we build trees from right to left
*/   class SolutionBTreefromPreOrderInOrder {
       int preorderIndex;
       Map<Integer,Integer> inOrderMap= new HashMap<Integer,Integer>();
       public BTreeNode buildTree(int[] preorder, int[] inorder) {
           preorderIndex=0;
          // postorderIndex=postorder.length-1;
           for(int i=0;i<inorder.length;i++)
               inOrderMap.put(inorder[i],i);

           return buildTree(preorder,0,preorder.length-1);

       }

       public BTreeNode buildTree(int[] preorder,int left,int right){
           if(left>right)
               return null;
           int currentRootVal=preorder[preorderIndex];
           preorderIndex++;
           BTreeNode root= new BTreeNode(currentRootVal);
           root.left=buildTree(preorder,left,inOrderMap.get(currentRootVal)-1);
           root.right=buildTree(preorder,inOrderMap.get(currentRootVal)+1,right);
/*
           postorderIndex--;
           TreeNode root= new TreeNode(currentRootVal);
           root.right=buildTree(postorder,inOrderMap.get(currentRootVal)+1,right);
           root.left=buildTree(postorder,left,inOrderMap.get(currentRootVal)-1);
*/

           return root;
       }
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

    class Solution {
        Integer prev;
        public boolean isValidBST(BTreeNode root) {
            prev=null;
            return isValidBSTUsingInOrderT(root);
        }
        public boolean isValidBSTUsingInOrderT(BTreeNode root){
            if(root==null)
                return true;
            if(!isValidBSTUsingInOrderT(root.left))
                return false;
            if(prev!=null && root.data<=prev)
                return false;
            prev=root.data;
            return isValidBSTUsingInOrderT(root.right);
        }
    }


    public int kthSmallest(BTreeNode root, int k) {
        Stack s = new Stack();
        while(true){
            while(root!=null){
                s.push(root);
                root=root.left;
            }
            if(s.isEmpty())
                break;
            root=(BTreeNode)s.pop();
            if(--k==0)
                return root.data;
            root=root.right;
        }
        return -1;
    }

    public int kthSmallestRecursive(BTreeNode root, int k) {
        return findKthSmallestFromInorderTraversal(root,k);
    }

    public void inOrderTraversal(BTreeNode root,List<Integer> nodes){
        if(root==null)
            return ;
        inOrderTraversal(root.left,nodes);
        nodes.add(root.data);
        inOrderTraversal(root.right,nodes);
    }

    public int findKthSmallestFromInorderTraversal(BTreeNode root,int k){
        List<Integer> nodes = new ArrayList<Integer>();
        inOrderTraversal(root,nodes);
        return nodes.get(k-1);
    }
    //LCA BST //T=O(N)  S= O(N) skewed
    public BTreeNode lowestCommonAncestorBST(BTreeNode root, BTreeNode p, BTreeNode q) {
        if(root==null)
            return null;

        if(p.data<root.data && q.data<root.data)
            return lowestCommonAncestorBST(root.left,p,q);
        else if(p.data>root.data && q.data>root.data)
            return lowestCommonAncestorBST(root.right,p,q);
        else
            return root;
    }
    //LCA BST //T=O(N)  S= O(1)
    public BTreeNode lowestCommonAncestorBSTIter(BTreeNode root, BTreeNode p, BTreeNode q) {
        while(root!=null){
            if(p.data<root.data && q.data<root.data)
                root=root.left;
            else if(p.data>root.data && q.data>root.data)
                root=root.right;
            else
                return root;
        }
        return null;
    }
    public BTreeNode lowestCommonAncestor(BTreeNode root, BTreeNode p, BTreeNode q) {
        if(root==null)
            return null;
        //base case
        if(root==p || root==q)
            return root;

        BTreeNode left=lowestCommonAncestor(root.left,p,q);
        BTreeNode right=lowestCommonAncestor(root.right,p,q);

        if(left!=null && right!=null)
            return root;
        else
            return left!=null?left:right;

    }
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
     //Binary Tree Maximum Path Sum
    class SolutionMaxPathSum {
        //max node is node value + 0 or 1 of its subtrees can add
        // then path sum can be obtained as node value+ max from left subtree+max from right subtree
        int max_sum = Integer.MIN_VALUE;

        public int maxGain(BTreeNode root) {
            if (root == null)
                return 0;
            int leftGain = Math.max(maxGain(root.left), 0);
            int rightGain = Math.max(maxGain(root.right), 0);
            int updatedSum = root.data + leftGain + rightGain;
            max_sum = Math.max(updatedSum, max_sum);
            return root.data + Math.max(leftGain, rightGain);
        }

        public int maxPathSum(BTreeNode root) {
            maxGain(root);
            return max_sum;
        }
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
       System.out.println("Serializing and deserializing B tree");
       System.out.println(serialize(temp));
       BTreeNode node=deserialize(serialize(temp));
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