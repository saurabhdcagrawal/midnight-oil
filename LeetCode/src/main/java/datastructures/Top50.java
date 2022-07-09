package main.java.datastructures;

import java.util.*;


public class Top50 {


    private List<List<Integer>> result = new ArrayList<>();
    //  private List<Pair<Integer,Integer>> pairList;

    public int getHeight(BTreeNode root) {
        //depending on this height will vary
        if(root==null)
            return -1;

        int left=getHeight(root.left);
        int right=getHeight(root.right);

        int currentHeight= Math.max(left,right)+1;
        //System.out.println(result.size());
        //when the result size reaches current Height, increment the result
        if(result.size()==currentHeight)
            result.add(currentHeight, new ArrayList<Integer>());
        List<Integer> currLevelList= result.get(currentHeight);
        currLevelList.add(root.data);
        return currentHeight;
    }

    public List<List<Integer>> findLeaves(BTreeNode root){
        getHeight(root);
        return result;
    }

     /* public List<List<Integer>> findLeaves(TreeNode root) {
      //[<0,4>,<0,5>,<1,2><9,3>]
         pairList=new ArrayList();
         getHeight(root);
         Collections.sort(pairList,(a,b)->Integer.compare(a.getKey(),b.getKey()));
         int i=0,height=0,n=pairList.size();
         List<List<Integer>> result= new ArrayList<>();
         while(i<n){
             List<Integer> currList= new ArrayList<Integer>();
             while(i<n && pairList.get(i).getKey()==height){
                currList.add(pairList.get(i).getValue());
                i++;
             }
             height++;
             result.add(currList);
         }
        return result;
    } */

    class SolutionRandomPickWithWeight {
        int [] prefixSum;
        int totalSum=0;
        //[1,3]-->[1,4]
        public SolutionRandomPickWithWeight(int[] w) {
            prefixSum = new int[w.length];
            for(int i=0;i<w.length;i++){
                totalSum+=w[i];
                prefixSum[i]=totalSum;
            }

        }

        public int pickIndex() {
            Random r = new Random();
            double throw_num=Math.random() * totalSum;
            System.out.println(throw_num);
       /* int i=0;
        while(throw_num>prefixSum[i]){
            i++;
        }
        return i; */
            int low=0, high=prefixSum.length;
            while(low<high){
                int mid =low+(high-low)/2;
                if(throw_num>prefixSum[mid])
                    low=mid+1;
                else
                    high=mid;
            }
            return low;
        }

    }
    public int maxScoreSightseeingPair(int[] values) {

        //8,9,14,16,22

        int bestPrevIndex=0;
        //int bestPrevVal=values[0];
        int maxScore=Integer.MIN_VALUE;

        for(int j=1;j<values.length;j++){
            int ans= values[bestPrevIndex]+bestPrevIndex+values[j] -j;
            maxScore=Math.max(maxScore,ans);
            if(values[bestPrevIndex]+bestPrevIndex<values[j]+j)
                bestPrevIndex=j;
        }

        return maxScore;
    }
    public String getHint(String secret, String guess) {

        int[] charset= new int[10];

        for(int i=0;i<secret.length();i++){
            int index=secret.charAt(i)-'0';
            charset[index]++;
        }

        int bulls=0,cows=0;
        for(int i=0;i<guess.length();i++){
            int index= guess.charAt(i)-'0';
            if(guess.charAt(i)==secret.charAt(i)){
                bulls++;
                //if index is 0 and it matches, then cows must have used that number and decremented
                //so decrement cows..to reclaim
                if(charset[index]<=0)
                    cows--;
                charset[index]--;

            }
            else if(charset[index]>0){
                cows++;
                charset[index]--;
            }

        }

        StringBuilder sb = new StringBuilder();
        sb.append(bulls);
        sb.append("A");
        sb.append(cows);
        sb.append("B");
        return sb.toString();
    }
    public long maxPoints(int[][] points) {
        int m=points.length;
        int n=points[0].length;
        long[] pre = new long[n];

        for(int j=0;j<n;j++)
            pre[j]=points[0][j];

        for(int i=1;i<m;i++){
            long[] left= new long[n];
            long[] right= new long[n];
            long[] current= new long[n];
            left[0]=pre[0];
            right[n-1]=pre[n-1];
            for(int j=1;j<n;j++)
                left[j]=Math.max(pre[j],left[j-1]-1);
            for(int j=n-2;j>=0;j--)
                right[j]=Math.max(pre[j],right[j+1]-1);
            for(int j=0;j<n;j++)
                current[j]=Math.max(left[j],right[j])+points[i][j];
            System.out.println(Arrays.toString(pre));
            pre=current;
        }
        System.out.println(Arrays.toString(pre));

        long maxPoints= Integer.MIN_VALUE;
        for(int j=0;j<n;j++){
            maxPoints=Math.max(pre[j],maxPoints);
        }
        return maxPoints;
    }
    public List<String> fullJustify(String[] words, int maxWidth) {
            List<String> result= new ArrayList<String>();
            int i=0,j=0;
            int n=words.length;
            while(i<n) {
                int currentLength=words[i].length();
                j=i+1;
                while(j<n && currentLength+words[j].length()+(j-i-1)<maxWidth){
                    currentLength+=words[j].length();
                    j++;
                }

                if(j-i==1||j>=n)
                    leftJustify(i,j,currentLength,words,maxWidth,result);
                else
                    middleJustify(i,j,currentLength,words,maxWidth,result);
                i=j;
            }
            return result;
        }

        public void leftJustify(int i, int j, int currentLength, String[] words, int maxWidth, List<String> result){
            int maxSpace= maxWidth-currentLength;
            int sections= j-i-1;
            int spaceToRight=maxSpace-sections;
            StringBuffer sb= new StringBuffer(words[i]);
            for(int k=i+1;k<j;k++){
                sb.append(" ");
                sb.append(words[k]);
            }
            sb.append(" ".repeat(spaceToRight));
            result.add(sb.toString());
        }


        public void middleJustify(int i, int j, int currentLength, String[] words, int maxWidth, List<String> result){
            int maxSpace= maxWidth-currentLength;
            int sections= j-i-1;
            int spacePerSection=maxSpace/sections;
            int extraSpace=maxSpace%sections;
            StringBuffer sb= new StringBuffer(words[i]);
            for(int k=i+1;k<j;k++){
                //extra space
                if(extraSpace>0){
                    sb.append(" ");
                    extraSpace--;
                }
                sb.append(" ".repeat(spacePerSection));
                sb.append(words[k]);
            }
            result.add(sb.toString());
        }
    public int countBattleships(char[][] board) {

        int m=board.length;
        int n=board[0].length;
        int count=0;

        for( int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if(board[i][j]=='X' && (i==0||board[i-1][j]!='X') && (j==0||board[i][j-1]!='X'))
                    count++;
            }
        }
        return count;
    }
    public String longestWord(String[] words) {
        Set<String> hset= new HashSet<String>();
        String maxWord="";
        Arrays.sort(words);
        for(String word:words){
            if(word.length()==1||hset.contains(word.substring(0,word.length()-1))){
                maxWord= word.length()>maxWord.length()?word:maxWord;
                hset.add(word);
            }
        }
        return maxWord;
    }/*
    Input: words = ["w","wo","wor","worl","world"]
    Output: "world"*/
    class SolutionLongestDictWord {
        public String longestWord(String[] words) {
            TrieNode root= new TrieNode();
            for(int i=0;i<words.length;i++)
                addWord(root,words[i],i);

            return dfs(root,words);

        }

        public void addWord(TrieNode root, String word, int index){
            TrieNode node=root;
            for(int i=0;i<word.length();i++){
                char ch= word.charAt(i);
                if(!node.children.containsKey(ch)){
                    node.children.put(ch,new TrieNode());
                }
                node= node.children.get(ch);
            }
            node.end=index;
        }

        public String dfs(TrieNode root, String[] words){
            String maxWord="";
            java.util.Stack<TrieNode> st= new java.util.Stack<TrieNode>();
            st.push(root);
            while(!st.isEmpty()){
                TrieNode node =st.pop();
                int nodeIndex=node.end;
                if(nodeIndex>=0||node==root){
                    if(node!=root){
                        String currWord=words[nodeIndex];
                        if(currWord.length()>maxWord.length()||(currWord.length()==maxWord.length() && currWord.compareTo(maxWord)<0))
                            maxWord=currWord;
                    }
                    for(Character ch: node.children.keySet())
                        st.push(node.children.get(ch));
                }

            }
            return maxWord;
        }

    }

    class TrieNode{
        HashMap<Character,TrieNode> children=new HashMap<Character,TrieNode>() ;
        int end=-1;
    }
    public int longestLine(int[][] mat) {
        int m=mat.length;
        int n=mat[0].length;
        int count=0;
        int[][][] dp= new int[m][n][4];
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if(mat[i][j]==1){
                    dp[i][j][0]=j==0?1:1+dp[i][j-1][0];
                    dp[i][j][1]=i==0?1:1+dp[i-1][j][1];
                    dp[i][j][2]=i==0 || j==0?1:1+dp[i-1][j-1][2];
                    dp[i][j][3]=i==0 || j==n-1?1:1+dp[i-1][j+1][3];
                }
                count=Math.max(count,Math.max(dp[i][j][0],Math.max(dp[i][j][1],Math.max(dp[i][j][2],dp[i][j][3]))));
            }
        }

        return count;
    }
    public int findMinDifference(List<String> timePoints) {

        boolean[] counting = new boolean[24*60];

        for(int i=0;i<timePoints.size();i++){
            String currentTimePoint=timePoints.get(i);
            int hrs=Integer.parseInt(currentTimePoint.split(":")[0]);
            int min=Integer.parseInt(currentTimePoint.split(":")[1]);
            int totalMins=hrs*60+min;
            System.out.println(timePoints.get(i)+" "+totalMins);
            if(counting[totalMins])
                return 0;
            counting[totalMins]=true;
        }

        int minDiff=Integer.MAX_VALUE,prev=-1,smallestVal=Integer.MAX_VALUE,largestValue=Integer.MIN_VALUE;

        for(int i=0;i<24*60;i++){
            if(counting[i]){
                if(prev!=-1)
                    minDiff=Math.min(minDiff,i-prev);

                smallestVal=Math.min(smallestVal,i);
                largestValue=Math.max(largestValue,i);

                prev=i;
            }
        }
        System.out.println(minDiff);
        minDiff=Math.min(minDiff,24*60-largestValue+smallestVal);
        return minDiff;
    }
    //routes = [[1,2,7],[3,6,7]], source = 1, target = 6
    public int numBusesToDestination(int[][] routes, int source, int target) {
        if(source==target)
            return 0;

        HashMap<Integer,ArrayList<Integer>> bus_routes= new HashMap<>();
        HashSet<Integer> seen = new HashSet<Integer>();
        Queue<Integer> q= new LinkedList<Integer>();
        //{1->0;2->0;7->0,1;3->1;6->1}
        for(int i=0;i<routes.length;i++){
            for(int j=0;j<routes[i].length;j++){
                //for all stops, get all the buses that go through it
                if(!bus_routes.containsKey(routes[i][j]))
                    bus_routes.put(routes[i][j], new ArrayList<Integer>());
                ArrayList<Integer> li = bus_routes.get(routes[i][j]);
                li.add(i);
            }
        }
        q.add(source);
        int level=0;

        while(!q.isEmpty()){
            level++;
            //get all buses at same level
            int len= q.size();
            for(int i=0;i<len;i++){

                int stop=q.poll();
                //fro stop go to the buses/routes... eg bus 1 /route 1, bus 2 etc
                ArrayList<Integer> busList=bus_routes.get(stop);
                for(Integer bus: busList){
                    //if bus seen go ahead with another
                    if(seen.contains(bus))
                        continue;
                    seen.add(bus);
                    //from bus go to stop again ... 1,2,7
                    for(int j=0;j<routes[bus].length;j++){
                        if(target==routes[bus][j])
                            return level;
                        q.add(routes[bus][j]);

                    }
                }

            }
        }
        return -1;
    }
}
