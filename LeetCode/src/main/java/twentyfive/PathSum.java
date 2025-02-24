package main.java.twentyfive;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 * int val;
 * TreeNode left;
 * TreeNode right;
 * TreeNode() {}
 * TreeNode(int val) { this.val = val; }
 * TreeNode(int val, TreeNode left, TreeNode right) {
 * this.val = val;
 * this.left = left;
 * this.right = right;
 * }
 * }
 */
public class PathSum {
    Map<Long, Integer> prefixMap = new HashMap<>();
    int count = 0;
    int k = 0;

    public int pathSum(TreeNode root, int targetSum) {
        prefixMap.put(0L, 1);
        k = targetSum;
        int sum = 0;
        dfs(root, sum);
        return count;
    }

    /*Each recursive call gets its own copy of sum, so different paths do
    not interfere with each other.
    This ensures:Each path computes the correct prefix sum.*/
    public void dfs(TreeNode root, long sum) {
        if (root == null)
            return;
        sum += root.val;
        if (prefixMap.containsKey(sum - k))
            count += prefixMap.get(sum - k);
        prefixMap.put(sum, prefixMap.getOrDefault(sum, 0) + 1);
        dfs(root.left, sum);
        dfs(root.right, sum);
        /*After visiting a node's left and right subtree, remove its sum
         contribution from prefixMap before returning.
This ensures that when we move to a different subtree, we don't carry over
old prefix sums that are no longer part of the valid path.*/
        prefixMap.put(sum, prefixMap.getOrDefault(sum, 0) - 1);
    }
    //**************another problem*******************/
    //Time Complexity: Preorder traversal, every node has been touched once O(N) where new
    //is the number of nodes
    //Space Complexity: HashMap to store prefix Sums so O(N) , it can store
    //upto N entries in the worst case

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