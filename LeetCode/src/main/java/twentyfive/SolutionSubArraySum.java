package main.java.twentyfive;


/*Input: nums = [1,2,3], k = 3
Output: 2


[0,1,3,6]
Create an array containing cumulative sum of length nums+1.. initialize sums[0]=0;
then sum[j+1] -sum[i] will give you
sum of elements between j and i
*/


import java.util.HashMap;
import java.util.Map;

public class SolutionSubArraySum {
/*Brute force.. start pointer, end pointer=start+1 until nums.length
third loop for start<end...sub arrays between start and end*/

    /*public int subarraySum(int[] nums, int k) {
        int count=0;
        //[1,2,3,4,5]
        //start fixed at 1, end fixed at 4:arrays,//
        //1,{1,2}//{1,2,3}//{1,2,3,4}
        for(int start=0;start<nums.length;start++){
            for(int end=start+1;end<=nums.length;end++){
                int sum=0;
                for(int i=start;i<end;i++)
                    sum+=nums[i];
                if(sum==k)
                    count++;

            }
        }
        return count;
    }*/

    /*[0,1,3,6]
    Create an array containing cumulative sum of length nums+1.. initialize sums[0]=0;
    then sum[j+1] -sum[i] will give you
    sum of elements between j and i
    */
   /* public int subarraySum(int[] nums, int k) {
        int[] sum = new int[nums.length+1];
        sum[0]=0;
        int count=0;
        for(int i=1;i<=nums.length;i++)
            sum[i]=sum[i-1]+nums[i-1];

        for(int start=0;start<nums.length;start++){
            for(int end=start+1;end<=nums.length;end++){
                if(sum[end]-sum[start]==k)
                    count++;
            }
        }
        return count;
    } */
    //if the difference in the cumulative sum of the array at position i and j is k
    //then the sum of elements between i and j must be k
    public int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixMap = new HashMap<>();
        int count = 0;
        prefixMap.put(0, 1);
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (prefixMap.containsKey(sum - k))
                count += prefixMap.get(sum - k);
            prefixMap.put(sum, prefixMap.getOrDefault(sum, 0) + 1);
        }
        return count;
    }
    //Time Complexity: O(n) where n is number of elements in the array
    //We go through the array once

    //Space Complexity: HashMap to store prefix Sums so O(N), hashmap can store upto n
    //prefix entries in the worst case

    /**
     * Definition for a binary tree node.
     * public class TreeNode {
     *     int val;
     *     TreeNode left;
     *     TreeNode right;
     *     TreeNode(int x) { val = x; }
     * }
     */

        public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {

            if (root==null)
                return null;

            if(root==p||root==q)
                return root;

            TreeNode left = lowestCommonAncestor(root.left,p,q);
            TreeNode right = lowestCommonAncestor(root.right,p,q);

            if(left!=null && right!=null)
                return root;
        /*If both left and right subtrees return non-null values, it means p and q are found in different subtrees. In this case, the current node (root) is the Lowest Common Ancestor (LCA) because it's the first node where the paths to p and q diverge.

If one side is null, it means both p and q are located on the same side of the tree. The recursive calls would have already found the LCA in that subtree, so we simply return the non-null result.*/

            else
                return left==null?right:left;

        }
    }
