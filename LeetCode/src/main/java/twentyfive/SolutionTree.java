package main.java.twentyfive;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/*Level Order Traversal (Tree-Specific BFS):
BFS applied specifically to binary trees.
No need for a visited set because trees don’t have cycles.
Often uses a null marker or queue size tracking to distinguish between levels.
Both approaches perform level order traversal (which is BFS for trees), but the second one is generally preferred since it avoids inserting null markers and reduces unnecessary queue operations by processing nodes in batches using queue size: */
public class SolutionTree {
    public int maxLevelSum(TreeNode root) {
        Queue<TreeNode> q = new LinkedList<>();
        int maxLevelSum = Integer.MIN_VALUE, currentLevel = 1, maxLevel = 0;
        q.add(root);
        while (!q.isEmpty()) {
            int levelSum = 0, size = q.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = q.poll();
                levelSum += node.val;
                if (node.left != null)
                    q.offer(node.left);
                if (node.right != null)
                    q.offer(node.right);
            }
            if (levelSum > maxLevelSum) {
                maxLevelSum = levelSum;
                maxLevel = currentLevel;
            }
            currentLevel++;
        }

        return maxLevel;
    }
    /*public int maxLevelSum(TreeNode root) {
        Queue<TreeNode> q = new LinkedList();
        int levelSum=0;
        int maxLevelSum=Integer.MIN_VALUE;
        int maxLevel=0, currentLevel=0;;
        q.offer(root);
        q.offer(null);
        while(!q.isEmpty()){
            TreeNode node=q.poll();
            if(node==null){
                if(levelSum>maxLevelSum){
                    maxLevelSum=levelSum;
                    maxLevel=currentLevel;
                }
                currentLevel++;
                levelSum=0;
                if(!q.isEmpty())
                    q.offer(null);
            }
            else{
                levelSum+=node.val;
                if(node.left!=null)
                    q.offer(node.left);
                if(root.right!=null)
                    q.offer(node.right);
            }
        }
        return maxLevel;
    }*/


//Space complexity Worst: case.. when the tree is completely unbalanced O(n)
//depth of recursion stack before it reaches base case
//O(5)->o(4)-->o(3)-->O(2)->O(1)
// //if tree is balanced..it reaches base case earlier
 /*At its peak, the deepest recursion level is log₂(N), so the maximum stack depth
 = O(log N)
maxDepth(1)	1 + max(maxDepth(2), maxDepth(3))
maxDepth(2)	1 + max(maxDepth(4), maxDepth(5))
maxDepth(4)	1 + max(maxDepth(null), maxDepth(null))*/

    public int maxDepth(TreeNode root) {
        if (root == null)
            return 0;
        else
            return (1 + Math.max(maxDepth(root.left), maxDepth(root.right)));
    }

    //Time Complexity O(m+n) where m and n are the number of nodes in both trees respectively. Visit each nodes once
    //Space complexity same.. recursion depth could be O(m), o(n)
    //Leaf Lists (leaf1 and leaf2): Stores only leaf nodes, so the space used is O(L1 + L2), where L1 and L2 are the number of leaf nodes in root1 and root2, respectively.
    /*Final Complexity*/
/*Time Complexity: O(N + M)
Space Complexity: O(L1 + L2) + O(H) (worst case O(N + M) for a skewed tree, or O(L1 + L2 + log(N)) for a balanced tree)*/
    public boolean leafSimilar(TreeNode root1, TreeNode root2) {
        List<Integer> leaf1 = new ArrayList<Integer>();
        List<Integer> leaf2 = new ArrayList<Integer>();
        dfs(root1, leaf1);
        dfs(root2, leaf2);
        return leaf1.equals(leaf2);
    }

    public void dfs(TreeNode root, List<Integer> leaf) {
        if (root == null)
            return;
        if (root.left == null && root.right == null)
            leaf.add(root.val);
        dfs(root.left, leaf);
        dfs(root.right, leaf);

    }

}


class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode() {
    }

    TreeNode(int val) {
        this.val = val;
    }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

