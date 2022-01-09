package main.java.datastructures;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Queue;
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

public class BST {

    public static class Node {
        Node left;
        Node right;
        int value;
        Node parent;

        Node(int value, Node parent) {
            this.value = value;
            this.parent = parent;
        }

        Node(int value) {
            this.value = value;

        }
    }

    public void insert(Node node, int value) {
        if (value < node.value) {
            if (node.left != null) {
                insert(node.left, value);
            } else {
                node.left = new Node(value, node);

            }
        } else {
            if (node.right != null) {
                insert(node.right, value);
            } else {
                node.right = new Node(value, node);

            }
        }

    }

    /*
     * follow a chain in which p and q are on the same side. That is, if p and q
     * are both on the left of the node, branch left to look for the common
     * ancestor. When p and q are no longer on the same side, you must have
     * found the first common ancestor
     */

    public Node commonAncestor(Node root, Node p, Node q) {
        if (covers(root.left, p) && covers(root.left, q))
            return commonAncestor(root.left, p, q);

        if (covers(root.right, p) && covers(root.right, q))
            return commonAncestor(root.right, p, q);
        return root;

    }

    // if p lies in any subtree below root
    public boolean covers(Node root, Node p) {
        if (root == null)
            return false;

        if (root == p)
            return true;

        return (covers(root.left, p) || covers(root.right, p));
    }

    // check if -23 lies in right or left of 11 ie in any of right or left
    // subtree of 11

    // Single element for hold
    // Sufficient rootnode or parent Node
    public void printInOrder(Node node) {

        if (node != null) {
            printInOrder(node.left);
            System.out.println("Node Traversed" + node.value);
            printInOrder(node.right);
        }

    }



    /*
     * Visit the root. Traverse the left subtree. Traverse the right subtree.
     */
    public void printPreOrder(Node node) {

        if (node != null) {
            System.out.println("Node Traversed" + node.value);
            printInOrder(node.left);
            printInOrder(node.right);
        }

    }

    /*
     * Traverse the left subtree. Visit the root. Traverse the right subtree.
     */
    public void printOrder(Node node) {

        if (node != null) {
            printInOrder(node.left);
            System.out.println("Node Traversed" + node.value);
            printInOrder(node.right);
        }

    }

    /*
     * Traverse the left subtree. Traverse the right subtree. Visit the root.
     */
    public void printPostOrder(Node node) {

        if (node != null) {
            printInOrder(node.left);
            printInOrder(node.right);
            System.out.println("Node Traversed :" + node.value);

        }

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
     */public Node findInOrderSuccessor(Node e) {

        Node p = null;
        if (e.parent == null || e.right != null)
            p = leftMostNode(e.right);

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

    }

    // works for even left most node
    public Node leftMostNode(Node node) {
        if (node == null)
            return null;
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public boolean checkSum(Node n, int sum) {

        if (n == null)
            return (sum == 0);

        else {
            int subsum = sum - n.value;
            return (checkSum(n.left, subsum) || checkSum(n.right, subsum));// cover
            // all
            // paths
        }

    }

    // post order//Void return ,so use if(node==null) return null
    public void deleteAllNodes(Node root) {
        Node temp = root;
        if (temp == null)
            return;

        deleteAllNodes(temp.left);
        deleteAllNodes(temp.right);

        System.out.println("Deleting root :" + root.value);
        root = null;

    }

    // postOrder//not working
    public Node getMirrorOfBinaryTree(Node root) {
        Node n = root;
        /*
         * if(temp!=null) {
         */

        if (n != null) {
            getMirrorOfBinaryTree(n.left);
            getMirrorOfBinaryTree(n.right);

            Node temp = n.left;
            n.left = n.right;
            n.right = temp;
        }
        return root;

    }

    public boolean isMirror(Node root1, Node root2) {

        if (root1 == null && root2 == null)
            return true;

        if (root1 == null || root2 == null)
            return false;

        if (root1.value != root2.value)
            return false;

        else
            return (isMirror(root1.left, root2.right) && isMirror(root1.right,
                    root2.left));
    }

    // construct Binary Tree

    // 1)Obtain first element in the preOrder traversal list.Increment a
    // preoerder index variable to
    // obtain next element in the next recursive call.
    // 2)Create a new Tree node with data equal to the value of the element
    // 3)Find the position of this element in Inorder Traversal.Let it be called
    // index
    // 4)make a recursive call to buildBinaryTree for all elements to left of
    // the index and
    // make the built tree as the left subtree of the new Node
    // 5)make a recursive call to the elements to right of subtree and make the
    // builtTreee as the right
    // subTree of the new Node
    // 6)returnNewNode

    public Node buildBinaryTree(int preOrder[], int inOrder[], int inStart,
                                int inEnd) {

        int preIndex = 0;
        // if(inStart>inEnd)
        // return null
        // Node newNode=new Node(preOrder[preIndex]);
        // preIndex++
        // if(inStart==inEnd)
        // return newNode //the node has no children
        return null;

        // int index=search(inOrder,preOrder,inStart,inEnd)
        // node.left(buildBinaryTree(preOrder,inOrder,inStart,index-1)
        // node.right(buildBinaryTree(preOrder,inOrder,index+1,inEnd)
        // return newNode

    }

    // print all ancestors

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

    public Node deleteBSTNode(Node root, Node z) {
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
        Node y = null;
        Node x = null;
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

    void findSum(Node head, int sum) {

        ArrayList<Integer> buffer = new ArrayList<Integer>();
        findSum(head, sum, buffer, 0);

    }

    void findSum(Node head, int sum, ArrayList<Integer> buffer, int level) {
        if (head == null)
            return;
        int tmp = sum;
        buffer.add(head.value);
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

    // for every node,check if the maximum value in left sub tree is greater
    // than node's value
    // and minimum value in right sub tree is greater than the node's value
    // O(n2)
    public boolean isBST(Node root) {

        if (root == null)
            return true;

        if (root.left != null && (findMaximum(root.left)).value > root.value)
            return false;

        if (root.right != null && (findMinimum(root.right)).value < root.value)
            return false;

        if (!isBST(root.left) && !isBST(root.right))
            return false;

        return true;
    }

    //

    public boolean isBSTInOrder(Node root) {
        return isBSTInOrder(root, Integer.MIN_VALUE);

    }

    public boolean isBSTInOrder(Node root, int prev) {
        if (root == null)
            return true;

        if (!isBSTInOrder(root.left, prev))
            return false;

        if (root.value < prev)
            return false;

        prev = root.value;

        if (!isBSTInOrder(root.right, prev))
            return false;
        return true;

    }

    // Do an Inorder traversal and keep track of the number of elements

    // Do an Inorder traversal and keep track of the number of elements
    // kth smallest element

    /*
     * public boolean kSmallestElement(Node root, int k) { if(root==null) return
     * true;
     *
     * if(kSmallestElement(root.left,k)!=null) return true;
     *
     *
     *
     *
     *
     * }
     */

    public int findLeaf(Node root) {
        if (root == null)
            return 0;
        else if (root.left == null && root.right == null)
            return 1;

        else
            return (findLeaf(root.left) + findLeaf(root.right));

    }

    public int findHalfNode(Node root) {
        if (root == null)
            return 0;
        else if ((root.left == null && root.right != null)
                || (root.right == null && root.left != null))
            return 1;

        else
            return (findHalfNode(root.left) + findHalfNode(root.right));

    }

    public boolean hasPathSum(Node root, int sum) {
        if (root == null)
            return (sum == 0);
        else {
            int subsum = sum - root.value;
            return (hasPathSum(root.left, subsum) || hasPathSum(root.right,
                    subsum));
        }

    }

    public boolean findElementInBTree(Node root, int elem) {
        if (root == null)
            return false;

        if (root.value == elem)
            return true;

        else
            return (findElementInBTree(root.left, elem) || findElementInBTree(
                    root.right, elem));
    }

    public void run() {

        Node rootnode = new Node(25, null);
        insert(rootnode, 11);
        insert(rootnode, 15);
        insert(rootnode, 20);
        insert(rootnode, 6);
        insert(rootnode, 16);
        insert(rootnode, -16);
        insert(rootnode, 23);
        insert(rootnode, 79);
        insert(rootnode, -23);
        Node temp = findNode(rootnode, 23);
        Node temp2 = findNode(rootnode, -23);
        Node temp3 = findNode(rootnode, 20);
        System.out.println(temp.value);
        System.out.println("My successor value"
                + findInOrderSuccessor(temp).value);

        System.out.println("Common ancestor is"
                + commonAncestor(rootnode, temp, temp2).value);

        System.out.println("Checksum is " + checkSum(rootnode, 106));
        findSum(rootnode, -10);
        System.out.println("Size" + size(rootnode));
        System.out.println("Value found" + findBSTNode(rootnode, -23).value);
        System.out.println("Value found BT:"
                + findBTNodeRecursion(rootnode, -45));
        deleteAllNodes(rootnode);
        System.out.println("findElementInBTree"
                + findElementInBTree(rootnode, -22));

        System.out.println("Has path sum" + hasPathSum(rootnode, 103));
        /*
         * Node rootAfterDel = deleteBSTNode(rootnode, temp3);
         * System.out.println("Printing order"); printInOrder c (rootAfterDel);
         */

        /*
         * Node rootnode = new Node(1,null); insert(rootnode, 2);
         * insert(rootnode, 3); insert(rootnode, 4); insert(rootnode, 5);
         * insert(rootnode, 6); insert(rootnode, 7); insert(rootnode, 8);
         * insert(rootnode, 9); insert(rootnode, 10);
         */

        /*
         * int array[]={1,2,3,4,5,6,7,8,9}; Node
         * treeNode=createBSTMinimalLength(array);
         */
        // printOrder(treeNode);
        // System works on reference,root node se leke sab traverse kar sakte
        // hain
        System.out.println("Max Depth is " + maxDepth(rootnode));
        System.out.println("Min Depth is " + minDepth(rootnode));

        ArrayList<LinkedList<Node>> result = returnLevelLinkedList(rootnode);
        for (int i = 0; i < result.size(); i++) {
            LinkedList<Node> list = result.get(i);
            System.out.println("*************");
            for (Node node : list) {
                System.out.println(node.value);
            }

        }
        System.out.println("NEW ONE");
        ArrayList<LinkedList<Node>> result1 = findLevelLinkList(rootnode);
        /*
         * System.out.println("Maximum Value is"); findMaximumValue(rootnode);
         */
        for (int i = 0; i < result1.size(); i++) {
            LinkedList<Node> list = result1.get(i);
            System.out.println("*************");
            for (Node node : list) {
                System.out.println(node.value);
            }

        }

        printInOrder(rootnode);
        System.out.println("Number of leaf nodes" + findHalfNode(rootnode));

        Node Mirror = getMirrorOfBinaryTree(rootnode);
        System.out.println("Printing Original");
        printInOrder(rootnode);
        System.out.println("Printing Mirror");
        printInOrder(Mirror);
        System.out.println(isMirror(rootnode, Mirror));

        // printPostOrder(rootnode);
        /*
         * findMinimum(rootnode); System.out.println("Printed value is" +
         * findMaximum(rootnode).value); System.out.println("Max Depth is " +
         * maxDepth(rootnode)); System.out.println("Min Depth is " +
         * minDepth(rootnode)); System.out.println("Maximum Value is");
         * findMaximumValue(rootnode);
         */

        // DFSWithRecursion(rootnode);
        BFSWithOutRecursion(rootnode);
        System.out.println(findNode(rootnode, 16) == null);

        System.out.println("Paths are");
        //printPath(rootnode);

        printAllAncestors(rootnode,temp2);

    }

    // Find BTree Node

    // Find minimum
    public void findMinimumValue(Node node) {
        if (node.left == null) {
            System.out.println("Node minimum" + node.value);
            if (node.parent != null) {
                System.out.println("My parent is" + node.parent.value);
            } else {
                System.out.println("No parent");
            }
        } else
            findMinimum(node.left);

    }

    // find minimum
    public Node findMinimum(Node node) {
        if (node.left == null) {
            return node;

        } else
            return (findMinimum(node.left));

    }

    // Find Maximum
    public void findMaximumValue(Node node) {
        if (node.right == null) {
            System.out.println("Node maximum value" + node.value);
            System.out.println("My parent is" + node.parent.value);
        } else
            findMaximumValue(node.right);

    }

    public Node findMaximum(Node node) {
        if (node.right == null) {
            return node;

        } else
            return (findMaximum(node.right));

    }

    // Structurally similar
    public boolean areStructurallySimilar(Node node1, Node node2) {

        if (node1 == null && node2 == null)
            return true;

        if (node1 == null || node2 == null)
            return false;

        else
            return ((node1.value == node2.value)
                    && (areStructurallySimilar(node1.left, node1.left)) && (areStructurallySimilar(
                    node2.left, node2.right)));
    }

    // Max Depth or max height

    public int maxDepth(Node node) {
        if (node == null)
            return 0;

        else
            return 1 + Math.max(maxDepth(node.left), maxDepth(node.right));
    }

    // MAX DIAMeter--largest number of nodes between 2 leaf nodes
    // does not necessarily pass through root
    public int Diameter(Node node) {
        if (node == null)
            return 0;

        else
            return 1 + Math.max(maxDepth(node.left), maxDepth(node.right));
    }

    public int minDepth(Node node) {
        if (node == null)
            return 0;

        else
            return 1 + Math.min(minDepth(node.left), minDepth(node.right));
    }

    public int size(Node node) {
        if (node == null)
            return 0;
        else
            return 1 + size(node.left) + size(node.right);

    }

    // Section 4.4
    public Node createBSTMinimalLength(int[] array, int start, int end) {

        if (end < start)
            return null;

        int mid = (start + end) / 2;

        Node treeNode = new Node(array[mid], null);
        treeNode.left = createBSTMinimalLength(array, start, mid - 1);
        treeNode.right = createBSTMinimalLength(array, mid + 1, end);

        return treeNode;

    }

    public Node createBSTMinimalLength(int[] array) {
        return (createBSTMinimalLength(array, 0, array.length - 1));
    }

    // BFS TRAVERSAL
    public ArrayList<LinkedList<Node>> returnLevelLinkedList(Node node) {
        ArrayList<LinkedList<Node>> result = new ArrayList<LinkedList<Node>>();
        LinkedList<Node> list = new LinkedList<Node>();
        int level = 0;
        list.add(node);
        result.add(level, list);
        while (true) {
            list = new LinkedList<Node>();
            for (int i = 0; i < result.get(level).size(); i++) {
                Node nodeIn = result.get(level).get(i);
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

    ArrayList<LinkedList<Node>> findLevelLinkList(Node root) {
        int level = 0;
        ArrayList<LinkedList<Node>> result = new ArrayList<LinkedList<Node>>();
        LinkedList<Node> list = new LinkedList<Node>();
        list.add(root);
        result.add(level, list);
        while (true) {
            list = new LinkedList<Node>();
            for (int i = 0; i < result.get(level).size(); i++) {
                Node n = (Node) result.get(level).get(i);
                if (n != null) {
                    if (n.left != null)
                        list.add(n.left);
                    if (n.right != null)
                        list.add(n.right);
                }
            }
            if (list.size() > 0) {
                result.add(level + 1, list);
            } else {
                break;
            }
            level++;
        }
        return result;
    }

    // Delete

    public static void main(String args[]) {
        new BST().run();

    }

    public void DFSWithOutRecursion(Node root) {

        Stack<Node> stack = new Stack<Node>();

        stack.push(root);

        Node current = null;

        while (!stack.isEmpty()) {
            current = stack.pop();
            System.out.println("Popping node :" + current.value);

            // do the processing on a node
            // process(current);
            if (current.right != null)
                stack.push(current.right);
            if (current.left != null)
                stack.push(current.left);

        }

    }

    public void BFSWithOutRecursion(Node root) {

        Queue<Node> queue = new LinkedList<Node>();

        queue.add(root);

        Node current = null;

        while (!queue.isEmpty()) {
            current = queue.poll();
            System.out.println("Popping node :" + current.value);

            // do the processing on a node
            // process(current);
            if (current.right != null)
                queue.add(current.right);
            if (current.left != null)
                queue.add(current.left);
            // may delete queue at end of while loop
        }

    }

    // for a

    public Node findBSTNode(Node root, int data) {
        if (root == null)
            return null;

        else if (root.value == data)
            return root;

        else if (root.value < data)
            return (findBSTNode(root.right, data));
        else
            return (findBSTNode(root.left, data));

    }

    // IS BTREE A BST

    /*
     * public ArrayList<ArrayList<Integer>> printAllPaths(Node n) {
     *
     * ArrayList<ArrayList<Integer>> paths = new
     * ArrayList<ArrayList<Integer>>(); ArrayList<Integer> pathList= new
     * ArrayList<Integer>(); pathList.add(n.value);
     *
     * printAllPaths(n.left);
     *
     *
     *
     * }
     */
    // Not for a BST
    public Node findNode(Node root, int value) {

        Queue<Node> queue = new LinkedList<Node>();

        queue.add(root);

        Node current = null;

        while (!queue.isEmpty()) {
            current = queue.poll();

            if (current.value == value)
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

    // General algo find node by recursion
    public boolean findBTNodeRecursion(Node root, int value) {

        if (root == null)
            return false;

        if (root.value == value)
            return true;

        else
            return (findBTNodeRecursion(root.left, value) || findBTNodeRecursion(
                    root.right, value));

    }

    public void DFSWithRecursion(Node node) {
        Stack<Node> stack = new Stack<Node>();
        stack.push(node);
        DFSWithRecursion(stack);
    }

    public void DFSWithRecursion(Stack stack) {

        if (stack.isEmpty())
            return;
        Node current = (Node) stack.pop();
        System.out.println("Popping node :" + current.value);

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

    public void printPath(Node root) {

        int[] path = new int[256];
        printPath(root, path, 0);
    }

    public void printPath(Node root, int[] path, int pathlen) {
        if (root == null)
            return;

        path[pathlen] = root.value;
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

    public boolean printAllAncestors(Node root,Node findAncestor)
    {
        if(root==null)
            return false;

        if(root.left==findAncestor|| root.right==findAncestor||printAllAncestors(root.left,findAncestor)||printAllAncestors(root.right,findAncestor))
        {
            System.out.println(root.value);
            return true;
        }
        return false;

    }
}
