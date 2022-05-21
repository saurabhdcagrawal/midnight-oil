package main.java.datastructures;
import java.util.*;
import java.util.Stack;
//q offer, q add
//diameter,rootToLeaf,deleteSuccesorBST,VerticalSum,zigzag,minimalBST
//A binary tree is similar to linked list but instead of simply pointing to next node
//In a binary tree each node,points to several nodes
//non linear data structure
//order of elements is not important
//if you need order use linked list,stack or queue
//set of nodes at a given level is called depth of a tree
//set of all nodes at given depth is called level of a tree
//root node is level 0
//full binary tree--> Each node
//complete binary tree --> every node except node at last level has 2 child nodes
//size of node is the number of descendants a node has including itself
//If every node has only one child we call it as a skew tree
//if every node left child call left skew tree
//if every node right child,call right skew tree
//full binary tree all leaf nodes same level,exactly two children
//no of nodes at any level 2^n for full binary tree
//if N no nodes at terminal level,height is logN
//Total no of nodes is 2^n+1 for full binary node
//what is complete?wsted pointers?,
//The process of visiting all nodes of a tree is called tree traversal
//In link libst,stack,queue only one way of traversing list(linear data structures) but tree structures there are many ways
//trress have several ways
//pre-order traversal,each node is processed before its subtree
//even though each node processed first,
//still requires that root information must be maintained before moving down the tree
//obvious data type is stack...hence recursion
//BST insertion,deletion,search takes logn time
//visit the root
//traverse left subtree in preorder
//traverse right subtree in preorder
//time and space complexity O(n)
//Inorder traversal root is visited between the subtree
//traverse left subtree in inorder
//Visit the root
//traverse right subtree in inorder
//time and space O(n)
//level order traversal,set of all nodes
//starts at vertex,visit all nodes at one level,visit all nodes at 2nd level
//BFS--Uses queues
//DFS works like preorder traversal and hence uses stack
//BFS
//visit root
//while traversing level l,keep all elements at l+1 in queue
//Go to next level and visit all nodes at that level
//Repeat until all levels are completed
//Breadth First Search: BFS involves searching a node and its siblings
//before going on to any children.
////Depth First Search: DFS involves searching a node and all its children
//before proceeding to its siblings.
/*  25
 /  \
 11      79
 / \
 6     15
 /      \
 -16      20
 /       /    \
 -23      16   23
 */
//25
//79 11        25
//79 15 6      11
//79 15 -16    6
//79 15 -23    -16
//79 15        -23
//79  20        15
//79 23 16      20
//              16
//              23
//              79

/*Queue
 * [25]
 * [79 11]          25
 * [11]             79
 * [15,6]           11
 * [6,20]           15
 * [20,-16]         6
 * [-16,23,16]      20
 * [23,16,-23]     -16
 * [16,-23]         23
 * [-23]            16
 []              -23
 */
//GRAPH IMPLEMENTATION
//Depth First Search: DFS involves searching a node and all its children before proceeding to its siblings.
//Breadth First Search: BFS involves searching a node and its siblings before going on to any children.
/*In computer science, a binary tree is a tree data structure in which each node
 has at most two children (referred to as the left child and the right child).
 In a binary tree, the degree of each node can be at most two. */
/*BST is a node-based binary tree data structure which has the following properties:[1]
 The left subtree of a node contains only nodes with keys less than the node's key.
 The right subtree of a node contains only nodes with keys greater than the node's key.
 The left and right subtree each must also be a binary search tree.

 The major advantage of binary search trees over other data structures is that the
 related sorting algorithms and search algorithms such as in-order traversal can be
 very efficient.*/
/*The depth of a node is the length of the unique path from the root to the node.*/
/*A node of degree zero is called a terminal node or leaf node*/
/*The height of a node in a tree is the length of a longest path from the node to a leaf.
 The height of a tree is the height of its root.*/
/*A balanced tree is a tree which is balanced - it has roughly the same height on each
 of its sub-nodes. a balanced binary search tree will have equal heights (plus or minus
 one) on the left and right sub-trees of each node.
 If the tree is balanced on both sides, only then while searching anything -
 we can divide tree into halves during every operation. Otherwise, dividing tree
 into 2 halves for every lookup does not hold valid. This ensures that operations
 on the tree always are guaranteed to have O(lg n) time, rather than the O(n) time
 that they might have in an unbalanced tree*/
//Heap is a nearly complete binary tree,all nodes except last node level has children
//Max Heap,Node Heap
/*This problem can be solved by just simple graph traversal, such as depth first search
 or breadth first search. We start with one of the two nodes and, during traversal,
 check if the other node is found. We should mark any node found in the course of
 the algorithm as ‘already visited’ to avoid cycles and repetition of the nodes.
 public enum State {
 Unvisited, Visited, Visiting;
 }

 public static boolean search(Graph g, Node start, Node end) {
 LinkedList<Node> q = new LinkedList<Node>(); // operates as Stack
 for (Node u : g.getNodes()) {
 u.state = State.Unvisited;
 }
 start.state = State.Visiting;
 q.add(start);
 Node u;
 while(!q.isEmpty()) {
 u = q.removeFirst(); // i.e., pop()
 if (u != null) {
 for (Node v : u.getAdjacent()) {
 if (v.state == State.Unvisited) {
 if (v == end) {
 return true;
 } else {
 v.state = State.Visiting;
 q.add(v);
 }
 }
 }
 u.state = State.Visited;
 }
 }
 return false;*/
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
    //Same as invert Binary Tree
    public static BTreeNode getMirrorOfBinaryTree(BTreeNode root){
     if(root==null)
         return null;
     getMirrorOfBinaryTree(root.left);
     getMirrorOfBinaryTree(root.right);
     BTreeNode temp=root.left;
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
    //for zigzag traversal, add q elements to stack and empty the stack later
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
    public BTreeNode findMaximum(BTreeNode node) {
        if (node.right == null) {
            return node;

        } else
            return (findMaximum(node.right));

    }
    public BTreeNode findMinimum(BTreeNode node) {
        if (node.left == null) {
            return node;

        } else
            return (findMinimum(node.left));

    }
    //there should be a false condition
    public boolean findElementInBTree(BTreeNode root, int elem) {
        if (root == null)
            return false;

        if (root.data == elem)
            return true;

        else
            return (findElementInBTree(root.left, elem) || findElementInBTree(
                    root.right, elem));
    }
//Standard BFS traversal
    public BTreeNode findNode(BTreeNode root, int value) {
        Queue<BTreeNode> queue = new LinkedList<BTreeNode>();

        queue.add(root);

        BTreeNode current = null;

        while (!queue.isEmpty()) {
            current = queue.poll();

            if (current.data == value)
                return current;

            // do the processing on a node
            // process(current);
            if (current.right != null)
                queue.add(current.right);
            if (current.left != null)
                queue.add(current.left);

        }
        return null;

    }

    //O(logN)
    public BTreeNode findBSTNode(BTreeNode root, int data) {
        if (root == null)
            return null;

        else if (root.data == data)
            return root;

        else if (root.data < data)
            return (findBSTNode(root.right, data));
        else
            return (findBSTNode(root.left, data));

    }

    //preorder traversal //O(n2) space is O(n)
    public static boolean validateIsBST(BTreeNode root){
     if (root==null)
         return true;

     if(root.left!=null && findMaximumElement(root.left)> root.data)
         return false;
     if(root.right!=null && findMinimumElement(root.right)<root.data)
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
    //Symmetric means left and right half are mirror of each other
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

//Preorder DFS... Printing data
//1,2,null,null,3,4,null,null,5,null,null,
    public static String serialize(BTreeNode root){
            if(root==null)
                return "null,";

            String str=root.data+",";
            str+=serialize(root.left);
            str+=serialize(root.right);
            System.out.println(str);
            return str;

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
            BTreeNode root=new BTreeNode(Integer.valueOf(data.get(0)));
            data.remove(0);
            root.left=deserialize(data);
            root.right=deserialize(data);
            return root;
    }

//if one traversal is inorder then the binary tree can be created
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

    public BTreeNode createBSTMinimalLength(int[] array, int start, int end) {

        if (start>end)
            return null;

        int mid = (start + end) / 2;

        BTreeNode treeNode = new BTreeNode(array[mid]);
        treeNode.left = createBSTMinimalLength(array, start, mid - 1);
        treeNode.right = createBSTMinimalLength(array, mid + 1, end);

        return treeNode;

    }

    public BTreeNode createBSTMinimalLength(int[] array) {
        return (createBSTMinimalLength(array, 0, array.length - 1));
    }


    public int countLeafNodes(BTreeNode root) {
        if (root == null)
            return 0;
        else if (root.left == null && root.right == null)
            return 1;

        else
            return (countLeafNodes(root.left) + countLeafNodes(root.right));

    }

    public int countHalfNodes(BTreeNode root) {
        if (root == null)
            return 0;
        else if ((root.left == null && root.right != null)
                || (root.right == null && root.left != null))
            return 1;

        else
            return (countHalfNodes(root.left) + countHalfNodes(root.right));

    }
    class SolutionBTreeToCircular {
        BTreeNode first;
        BTreeNode last;

        public void helper(BTreeNode node){
            if(node!=null){
                helper(node);
                if(last!=null){
                    last.right=node;
                    node.left=last;
                }
                last=node;
                if(first==null){
                    first=node;
                }
                helper(node.right);

            }
        }

        public BTreeNode treeToDoublyList(BTreeNode root) {
            if(root==null)
                return null;
            helper(root);
            last.right=first;
            first.left=last;
            return first;

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

    public int maxDepth(BTreeNode node) {
        if (node == null)
            return 0;

        else
            return 1 + Math.max(maxDepth(node.left), maxDepth(node.right));
    }

    public int minDepth(BTreeNode node) {
        if (node == null)
            return 0;

        else
            return 1 + Math.min(minDepth(node.left), minDepth(node.right));
    }
    public int size(BTreeNode node) {
        if (node == null)
            return 0;
        else
            return 1 + size(node.left) + size(node.right);

    }


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
    //LCA is also the shortest path between 2 nodes
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



    //Revisit********************************************************************
/*
 * follow a chain in which p and q are on the same side. That is, if p and q
 * are both on the left of the node, branch left to look for the common
 * ancestor. When p and q are no longer on the same side, you must have
 * found the first common ancestor
 */
    public BTreeNode commonAncestor(BTreeNode root, BTreeNode p, BTreeNode q) {
        if (covers(root.left, p) && covers(root.left, q))
            return commonAncestor(root.left, p, q);

        if (covers(root.right, p) && covers(root.right, q))
            return commonAncestor(root.right, p, q);
        return root;

    }

    // if p lies in any subtree below root
    public boolean covers(BTreeNode root, BTreeNode p) {
        if (root == null)
            return false;

        if (root == p)
            return true;

        return (covers(root.left, p) || covers(root.right, p));
    }

    /*
     * 1) If right subtree of node is not NULL or parent is NULL, then succ lies
     * in right subtree. Do following. Go to right subtree and return the node
     * with minimum key value in right subtree. 2) If right sbtree of node is
     * NULL, then succ is one of the ancestors. Do following. Travel up using
     * the parent pointer until you see a node which is left child of it’s
     * parent. The parent of such a node is the succ.
     */

    /*
     * A binary tree is either an empty pointer or a node that consists of an
     * integer value and two sub-trees. A binary tree T is given. A node of that
     * tree containing value V is described as visible if the path from the root
     * of the tree to that node does not contain a node with any value exceeding
     * V. In particular, the root is always visible and nodes with values lower
     * than that of the root are never visible. Assume that the following
     * declarations are given:
     */
    /*public BTreeNode findInOrderSuccessor(BTreeNode e) {

        BTreeNode p = null;
        if (e.parent == null || e.right != null)
            p = findMinimum(e.right);

        else {
            while (e.parent != null) {
                p = e.parent;
                if (p.left == e)// Travel up until we find a node which is left
                    // child of parent
                    break;
                e = p; // To continue the while loop
            }

        }
        return p;

    }*/


    // count (k-1) left *count(n-k) right nodes from k=1 to n
    public int numberOfTrees(int n) {

        if (n == 0 || n == 1)
            return 1;

        else {
            int sum = 0;
            for (int k = 1; k <= n; k++) {
                int left = numberOfTrees(k - 1);
                int right = numberOfTrees(n - k);
                sum = sum + left + right;
            }

            return sum;
        }
    }
        // if node to be deleted has no children,determine if left node or right
        // node of its parent
        // update pointers accordingly
        // deleteNode.parent.left = null;
        // deleteNode.parent.right = null;
        // if node to be deleted has a single child,(either left or right
        // irrespective)
        // 2 pointer update
        // parent of child,parent of node to be updated
        // 2nd pointer
        //
        // if node to be deleted is left node of its parent,make the child node
        // of deleted node left node of parentt
        // determine if its a left node or right node or root node and update
        // accordingly
        // in case of 2 children find in order successor
        // do the same process,find children x,update paremt amd all,make left
        // or right child
        // at end make node's key to be deleted as the inorder succers;r'key
        /*
        public BTreeNode deleteBSTNode(BTreeNode root, BTreeNode z) {
        BTreeNode y = null;
        BTreeNode x = null;
        if (z.left == null || z.right == null)
            y = z;
        else
            y = findInOrderSuccessor(z);
//find tthe single child,x will contain
        if (y.left != null)
            x = y.left;
        else
            x = y.right;
//update one side parent pointers

        if (x != null)
            x.parent = y.parent;

        if (y.parent == null)
            root = x;

        else {

            if (y.parent.left == y)
                y.parent.left = x;
            else
                y.parent.right = x;

            if (y != z)
                z.value = y.value;
        }
        return root;

    }
*/
        public void printPath(BTreeNode root) {

            int[] path = new int[256];
            printPath(root, path, 0);
        }

    public void printPath(BTreeNode root, int[] path, int pathlen) {
        if (root == null)
            return;

        path[pathlen] = root.data;
        pathlen++;

        if (root.left == null && root.right == null) {

            printArray(path, pathlen);
        }

        else

        {

            printPath(root.left, path, pathlen);
            printPath(root.right, path, pathlen);

        }

    }

    public void printArray(int[] path, int pathlen) {

        for (int i = 0; i < pathlen; i++)

        {
            System.out.print(path[i] + ",");
        }
        System.out.println();
    }
    void findSum(BTreeNode head, int sum) {

        ArrayList<Integer> buffer = new ArrayList<Integer>();
        findSum(head, sum, buffer, 0);

    }

    void findSum(BTreeNode head, int sum, ArrayList<Integer> buffer, int level) {
        if (head == null)
            return;
        int tmp = sum;
        buffer.add(head.data);
        for (int i = level; i > -1; i--) {
            tmp -= buffer.get(i);
            if (tmp == 0) {
                System.out.println("Buffer" + buffer.toString());
                System.out.println(i);
                System.out.println(level);
                print(buffer, i, level);
            }
        }

        ArrayList<Integer> c1 = (ArrayList<Integer>) buffer.clone();
        System.out.println(c1.toString());
        ArrayList<Integer> c2 = (ArrayList<Integer>) buffer.clone();
        System.out.println(c2.toString());
        findSum(head.left, sum, c1, level + 1);
        findSum(head.right, sum, c2, level + 1);
    }

    void print(ArrayList<Integer> buffer, int level, int i2) {
        for (int i = level; i <= i2; i++) {
            System.out.print(buffer.get(i) + "");
        }
        System.out.println();
    }

    public void DFSWithOutRecursion(BTreeNode root) {
            Stack<BTreeNode> stack = new Stack<BTreeNode>();
            stack.push(root);
            BTreeNode current = null;

        while (!stack.isEmpty()) {
            current = stack.pop();
            System.out.println("Popping node :" + current.data);

            // do the processing on a node
            // process(current);
            if (current.right != null)
                stack.push(current.right);
            if (current.left != null)
                stack.push(current.left);

        }

    }

    public void DFSWithRecursion(BTreeNode node) {
        Stack<BTreeNode> stack = new Stack<BTreeNode>();
        stack.push(node);
        DFSWithRecursion(stack);
    }

    public void DFSWithRecursion(Stack stack) {
        if (stack.isEmpty())
            return;
        BTreeNode current = (BTreeNode) stack.pop();
        System.out.println("Popping node :" + current.data);

        // do the processing on a node
        // process(current);
        if (current.right != null)
            stack.push(current.right);
        if (current.left != null)
            stack.push(current.left);
        System.out.println("Stack at this point"
                + Arrays.toString(stack.toArray()));
        DFSWithRecursion(stack);
    }
    public ArrayList<LinkedList<BTreeNode>> returnLevelLinkedList(BTreeNode node) {
        ArrayList<LinkedList<BTreeNode>> result = new ArrayList<LinkedList<BTreeNode>>();
        LinkedList<BTreeNode> list = new LinkedList<BTreeNode>();
        int level = 0;
        list.add(node);
        result.add(level, list);
        while (true) {
            list = new LinkedList<BTreeNode>();
            for (int i = 0; i < result.get(level).size(); i++) {
                BTreeNode nodeIn = result.get(level).get(i);
                if (nodeIn.left != null)
                    list.add(nodeIn.left);
                if (nodeIn.right != null)
                    list.add(nodeIn.right);
            }

            if (list.size() == 0)
                break;
            else {
                level = level + 1;
                result.add(level, list);
            }
        }
        return result;

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

    //Standard preorder
    //https://leetcode.com/problems/merge-two-binary-trees/
    public BTreeNode mergeTrees(BTreeNode root1, BTreeNode root2) {
        if(root1==null && root2==null)
            return null;
        else if(root1==null)
            return root2;
        else if(root2==null)
            return root1;
        root1.data+=root2.data;

        //how to propogate when not boolean
        root1.left=mergeTrees(root1.left,root2.left);
        root1.right=mergeTrees(root1.right,root2.right);

        return root1;
    }

    //Trim a B Tree
    public BTreeNode trimBST(BTreeNode root, int low, int high) {

        if(root==null)
            return null;

        if(root.data>high)
            return trimBST(root.left,low,high);

        if(root.data<low)
            return trimBST(root.right,low,high);

        root.left=trimBST(root.left,low,high);

        root.right=trimBST(root.right,low,high);

        return root;

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
       getMirrorOfBinaryTree(root);
       System.out.println("Mirrored B tree") ;
       inOrderTraversal(root);

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