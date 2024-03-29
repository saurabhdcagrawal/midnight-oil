package main.java.datastructures;

//To store Graph,we need to store number of vertices,number of edges and their interconnections
//We would need a matrix with size V * V where V is number of vertices and the matrix is boolean
//(or corresponding to each vertex have a list)
//This is called an adjacency matrix
//Adj[u,u] will be stored 1 ,if there is an edge from u to v Adj[u,v} and so will be Adj[v,u]
//O(V2) for storage and O(V2) time for initialization
///Depth First Search: DFS involves searching a node and all its children before
// proceeding to its siblings.
////Breadth First Search: BFS involves searching a node and its
// siblings before going on to any children.
///

//use BFS - when you want to find the shortest path from a certain source node to
// a certain destination ,if the tree is too deep.
/*
DFS is more space-efficient than BFS, but may go to unnecessary depths.
 Their names are revealing: if there's a big breadth (i.e. big branching factor),
    but very limited depth (e.g. limited number of "moves"), then DFS can be more preferrable to BFS.
*/
//BFS is storing child pointers at each level
//DFS has less memory requirements than BFS
//when you want to exhaust all possibilities,
// and check which one is the best/count the number of all possible way

//DFS uses stack

/*public  void dfsUsingStack(Node node)
        {
        Stack<Node> stack=new  Stack<Node>();
        stack.add(node);
        node.visited=true;
        while (!stack.isEmpty())
        {
        Node element=stack.pop();
        System.out.print(element.data + " ");

        List<Node> neighbours=element.getNeighbours();
        for (int i = 0; i < neighbours.size(); i++) {
        Node n=neighbours.get(i);
        if(n!=null && !n.visited)
        {
        stack.add(n);
        n.visited=true;

        }
        }
        }
        }*/

/*public void bfs(int adjacency_matrix[][], Node node)
        {
        queue.add(node);
        node.visited=true;
        while (!queue.isEmpty())
        {

        Node element=queue.remove();
        System.out.print(element.data + "t");
        ArrayList<Node> neighbours=findNeighbours(adjacency_matrix,element);
        for (int i = 0; i < neighbours.size(); i++) {
        Node n=neighbours.get(i);
        if(n!=null && !n.visited)
        {
        queue.add(n);
        n.visited=true;

        }
        }

        }
        }*/

import java.util.*;

public class Graph {
//Number of Connected Components in an Undirected Graph
//build adjacency matrix --- go to every edge --O(E)
//vertex matrix to show every visit
//List of vertices
//create boolean matrix for vertex visited
    //Input: n = 5, edges = [[0,1],[1,2],[3,4]]
    //Array of adjacency lists
    //int[] similarly List<Integer>[]
    /*Time complexity: {O}(E + V)O(E+V).
    Space complexity: {O}(E + V)O(E+V).
    Building the adjacency list will take {O}(E)O(E) space.
    To keep track of visited vertices, an array of size {O}(V)O(V) is required.
    Also, the run-time stack for DFS will use {O}(V)O(V) space.*/
    public static int countComponents(int n, int[][] edges) {
        List<Integer>[] adjList= new ArrayList[n];
        for(int i=0;i<n;i++)
            adjList[i]= new ArrayList<Integer>();

        for(int i=0;i<edges.length;i++){
            adjList[edges[i][0]].add(edges[i][1]);
            adjList[edges[i][1]].add(edges[i][0]);
        }
      //  [{1},{0,2},{1},{4},{3}]

        boolean[] visited = new boolean[n];
        int connectedComponents=0;

        for(int i=0;i<n;i++){
            if (!visited[i]) {
                connectedComponents++;
                dfs(adjList, visited, i);
            }
        }
       return connectedComponents;
    }
//For building adjacency matrix will take O(E) runtime and space
    //Visiting every node once and then for every vertex we run through adjacency list
    //Time O(E+V) //stack dfs takes O(V) space and also to build the visited array
    //Space complexity O(E+V)
    public static void dfs( List<Integer>[] adjList,boolean[] visited,int node){
        if (visited[node])
                return ;
        visited[node]=true;
        for(int i=0;i<adjList[node].size();i++){
            int neighbor=adjList[node].get(i);
            if(!visited[neighbor]) {
                dfs(adjList, visited, neighbor);
            }
        }
   }
    //A graph is a tree if no cycles
    //no disconnected vertex
    // [[0,1],[1,2],[2,3],[1,3],[1,4]]

    /* 0 ----1\------4
            /  \
           3----2
        */
    //Adj list*/
    //0 visited (0,-1)--->(1,0)
    //1 visited (2,1)
    //2 visited (2,3)
    //3 visited (1,3)
    //return false
    //[{1},{0,2,3,4},{1,3},{1,2},{1}]
    //Time complexity O(E+V) Space complexity O(E+V)
    public boolean validTree(int n, int[][] edges) {

        List<Integer>[] adjList= new ArrayList[n];

        for(int i=0;i<n;i++)
            adjList[i]= new ArrayList<Integer>();

        for(int i=0;i<edges.length;i++){
            adjList[edges[i][0]].add(edges[i][1]);
            adjList[edges[i][1]].add(edges[i][0]);
        }
        //[{1},{0,2,3,4},{1,3},{1,2},{1}]
        boolean[] visited= new boolean[n];

        return (checkNoCyclesDFS(adjList,visited,0,-1) && checkFullyConnected(visited));
    }
    //calls (0,-1) 0 seen (1,0)
    //0 parent 1 seen (0,1) ->wont call--> calls (2,1),(3,1),(4,1)
    //2 seen (2,1)---wont call-->calls (2,3)
    //3 seen (3,2)--> wont call -->calls->(3,1)
    // 1 seen again --loop detected
    public boolean checkNoCyclesDFS(List<Integer>[] adjList, boolean[]visited, int node, int parentNode){
        if(visited[node])
            return false;
        visited[node]=true;
        for(int i=0;i<adjList[node].size();i++){
            int neighbor=adjList[node].get(i);
            if(neighbor!=parentNode){
                //understand this pattern of recursion
                boolean result=checkNoCyclesDFS(adjList,visited,neighbor,node);
                if(!result)
                    return false;
            }
        }
        return true;
    }

    public boolean checkFullyConnected(boolean[]visited){
        for(int i=0;i<visited.length;i++){
            if(!visited[i])
                return false;
        }
        System.out.println();
        return true;
    }
//O (C)
    //O(E+V)
    //E cant be more than V^2 both and 2 relations can lead to N-1 edges
    //smaller than both
    //O(V+min(V^2,N-1)
    //O(min(V^2,N-1))+O(C) .. so O(C+min(V^2,N-1))=O(C+min(V^2,N))
    //Space complexity is 1 if 26 letters ..if others then O(V+min(V^2,N))..
    public String alienOrder(String[] words) {
        //draw inferences and relations from characters
        //put it in an adjacency list
        //find incoming nodes.. if no incoming nodes indegree 0
        //place those elements in o/p first and BFS queue first
        //place only those elements in o/p whose indegree is 0
        //when you place those elements in o/p decrease indegree for others
        //queue will be eventually empty

        Map<Character,List<Character>> adjListMap= new HashMap<>();
        Map<Character,Integer> inDegreeMap= new HashMap<Character,Integer>();

        for(String word: words){
            for(char c: word.toCharArray()){
                adjListMap.put(c,new ArrayList<Character>());
                inDegreeMap.put(c,0);
            }
        }

        for(int i=1;i<words.length;i++){
            String word1= words[i-1];
            String word2= words[i];
            //abcdef abc ? no relation can be determined
            if(word1.length()>word2.length() && word1.startsWith(word2))
                return "";
            int upperBound=Math.min(word1.length(),word2.length());
            int j=0;
            while(j<upperBound && word1.charAt(j)==word2.charAt(j))
                j++;
            //"t","tacfeiea" will yield no relation
            if(j<upperBound){
                List<Character> adjList = adjListMap.get(word1.charAt(j));
                adjList.add(word2.charAt(j));
                inDegreeMap.put(word2.charAt(j),inDegreeMap.get(word2.charAt(j))+1);
            }
        }
        System.out.println(adjListMap);
        System.out.println(inDegreeMap);

        Queue<Character> q = new LinkedList<Character>();
        StringBuilder sb = new StringBuilder();
        for(Character c: inDegreeMap.keySet())
            if(inDegreeMap.get(c)==0)
                q.add(c);
        while(!q.isEmpty()){
            Character c= q.poll();
            sb.append(c);
            List<Character> adjList=adjListMap.get(c);
            for(Character k: adjList){
                inDegreeMap.put(k,inDegreeMap.get(k)-1);
                if(inDegreeMap.get(k)==0)
                    q.add(k);
            }
        }
        //cant determine enough relations?
        if(sb.length()<inDegreeMap.size())
            return("");

        return sb.toString();
    }
    public boolean isBipartite(int[][] graph) {
        //Input: graph = [[1,2,3],[0,2],[0,1,3],[0,2]]
        //
        int n= graph.length;
        int[] color= new int[n];
        Arrays.fill(color,-1);
        for(int i=0;i<n;i++){
            if(color[i]==-1){
                java.util.Stack<Integer> st = new java.util.Stack<Integer>();
                st.push(i);
                color[0]=0;
                while(!st.isEmpty()){
                    Integer node=st.pop();
                    for(Integer neigh: graph[node]){
                        if(color[neigh]==-1){
                            st.push(neigh);
                            color[neigh]=color[node]^1;
                        }
                        else if(color[neigh]==color[node])
                            return false;
                    }
                }

            }
        }
        return true;
    }

    public static void main(String args[]){
       int n = 5;
       int[][] edges = {{0,1},{1,2},{3,4}};
       System.out.println(countComponents(n,edges));
   }
    class TopSortCourseOrder {
        //there are 3 modes.. while during recursion if node is encountered again then its a cycle

        //if node is encountered after recursion ends then its not a cycle
        //during recursion node will be gray but not black;


        static int WHITE=0;
        static int GRAY=1;
        static int BLACK=2;

        public int[] findOrder(int numCourses, int[][] prerequisites) {

            List<Integer>[] adjList= new ArrayList[numCourses];
            for(int i=0; i<numCourses;i++){
                adjList[i]= new ArrayList<Integer>();
            }

            for(int i=0;i<prerequisites.length;i++)
                adjList[prerequisites[i][0]].add(prerequisites[i][1]);

            Map<Integer,Integer> visited= new HashMap<Integer,Integer>();

            for(int i=0;i<numCourses;i++)
                visited.put(i,WHITE);

            List<Integer> result= new ArrayList<Integer>();
            //calling when white
            for(int i=0;i<numCourses;i++){
                if(visited.get(i)==WHITE){
                    boolean flag= dfs(adjList,visited,result,i);
                    if(!flag)
                        return new int[]{};
                }
            }

            int[] res_array=new int[result.size()];

            for(int i=0;i<result.size();i++)
                res_array[i]=result.get(i);

            return res_array;
        }
        public boolean dfs( List<Integer>[] adjList,Map<Integer,Integer> visited,List<Integer> result, int node){

            visited.put(node,GRAY);
            boolean flag=true;
            //checking gray inside for loop
            for(int i=0;i<adjList[node].size();i++){
                int neighbor=adjList[node].get(i);
                if(visited.get(neighbor)==GRAY)
                    return false;
                else if(visited.get(neighbor)==WHITE){
                    flag=dfs(adjList,visited,result,neighbor);
                    if(!flag)
                        return false;

                }
            }
            visited.put(node,BLACK);
            result.add(node);
            return true;
        }



    }
    //finding a cycle in a DAG
   /* In directed graphs, we often detect cycles by using graph coloring. All nodes start as white, and then once
    they're first visited they become grey, and then once all their outgoing nodes have been fully explored, they become black.
    We know there is a cycle if we enter a node that is currently grey (it works because all nodes that are currently on the
            stack are grey*/
    //there are 3 modes.. while during recursion if node is encountered whose recursion is not complete
    //again then its a cycle

    //if node is encountered after recursion ends for that node then its not a cycle
    //during recursion node will be gray but not black;

    class TopSortCourseDependency {
        static int WHITE=0;
        static int GRAY=1;
        static int BLACK=2;

        public boolean canFinish(int numCourses, int[][] prerequisites) {

            List<Integer>[] adjList= new ArrayList[numCourses];

            for(int i=0;i<numCourses;i++)
                adjList[i]= new ArrayList<Integer>();

            for(int i=0;i<prerequisites.length;i++)
                adjList[prerequisites[i][0]].add(prerequisites[i][1]);

            HashMap<Integer,Integer> visited= new HashMap<Integer,Integer>();

            for(int i=0;i<numCourses;i++){
                visited.put(i,WHITE);
            }

            for(int i=0;i<numCourses;i++){
                if(visited.get(i)==WHITE){
                    boolean flag=dfs(adjList,visited,i);
                    if(!flag)
                        return false;
                }
            }
            return true;
        }
        public boolean dfs(List<Integer>[] adjList,HashMap<Integer,Integer> visited, int node){

            visited.put(node,GRAY);

            for(int i=0;i<adjList[node].size();i++){
                int neighbor=adjList[node].get(i);
                if(visited.get(neighbor)==GRAY)
                    return false;
                else if (visited.get(neighbor)==WHITE){
                    boolean flag=dfs(adjList,visited,neighbor);
                    if(!flag)
                        return false;
                }
            }
            visited.put(node,BLACK);
            return true;
        }
    }

}

/*
// Definition for a Node.
class Node {
    public int val;
    public List<Node> neighbors;
    public Node() {
        val = 0;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val) {
        val = _val;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val, ArrayList<Node> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
}
*/
//Deep clone of a graph
class Solution {

    HashMap<Node,Node> visited= new HashMap<Node,Node>();
    public Node cloneGraph(Node node) {
        if(node==null)
            return null;

        if(visited.containsKey(node))
            return visited.get(node);


        Node clonedNode = new Node(node.val, new ArrayList<Node>());
        visited.put(node, clonedNode);

        for(int i=0;i<node.neighbors.size();i++)
            clonedNode.neighbors.add(cloneGraph(node.neighbors.get(i)));


        return clonedNode;


    }

    public Node cloneGraphBFS(Node node) {
        if(node==null)
            return null;

        Node clonedNode=new Node(node.val, new ArrayList<Node>());
        visited.put(node,clonedNode);
        Queue<Node> q = new LinkedList<Node>();
        q.offer(node);

        while(!q.isEmpty()){
            Node n = (Node) q.poll();
            for(Node neighbor: n.neighbors){
                if(!visited.containsKey(neighbor)){
                    Node clonedNeighbor= new Node(neighbor.val, new ArrayList<Node>());
                    //still be visited for linking but not cloned again
                    visited.put(neighbor,clonedNeighbor);
                    q.offer(neighbor);
                }
                //still be visisted for linking but not cloned again
                visited.get(n).neighbors.add(visited.get(neighbor));
            }
        }
        return clonedNode;
    }
//EulerianPath
    public List<String> findItinerary(List<List<String>> tickets) {

        Map<String,LinkedList<String>> flightMap = new HashMap<>();
        LinkedList<String> result = new LinkedList<String>();

        for(int i=0;i<tickets.size();i++){
            List<String> singleTick=tickets.get(i);
            String origin=singleTick.get(0);
            String dest=singleTick.get(1);
            if(!flightMap.containsKey(origin))
                flightMap.put(origin, new LinkedList<String>());
            LinkedList<String> destList=flightMap.get(origin);
            destList.addLast(dest);
        }

        System.out.println(flightMap);

        for(String origin: flightMap.keySet()){
            LinkedList<String> destList=flightMap.get(origin);
            Collections.sort(destList);
        }

        System.out.println(flightMap);
        postOrderDFS("JFK",flightMap,result);
        return result;

    }
    public void postOrderDFS(String source,Map<String,LinkedList<String>> flightMap,LinkedList<String> result){

        if(flightMap.containsKey(source)){
            LinkedList<String> dest = flightMap.get(source);
            while(dest!=null && !dest.isEmpty()){
                String newSource= dest.pollFirst();
                postOrderDFS(newSource,flightMap,result);
            }
        }
        result.addFirst(source);
    }
    class Node {
        public int val;
        public List<Node> neighbors;
        public Node() {
            val = 0;
            neighbors = new ArrayList<Node>();
        }
        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<Node>();
        }
        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }

//Serialize and Deseralize an N'ary tree
    class Codec {
        class Node {
            public int val;
            public List<Node> children;

            public Node() {}

            public Node(int _val) {
                val = _val;
            }

            public Node(int _val, List<Node> _children) {
                val = _val;
                children = _children;
            }
        }
        // Encodes a tree to a single string.
        public final String DELIM=",";
        public final String SENTINEL="X";

    public String serialize(Node root) {
        StringBuilder sb= new StringBuilder();
        if(root==null)
            return "X";
        encode(root,sb);
        int size=sb.length();
        sb.deleteCharAt(sb.length()-1);
        System.out.println(sb);
        return sb.toString();

    }

    public void encode(Node n, StringBuilder sb){
        sb.append(n.val);
        sb.append(DELIM);
        for(Node children: n.children){
            encode(children,sb);
        }
        sb.append(SENTINEL);
        sb.append(DELIM);
    }
//    //1,2,X,3,6,X,7,11,14,X,X,X,X,4,8,12,X,X,X,5,9,13,X,X,10,X,X,X
        // Decodes your encoded data to tree.
//when child is null, we make that node and travel upwards
//first leaf nodes are created
    //thats why null is important
        public Node deserialize(String data) {
            if(data.isEmpty())
                return null;

            String[] dataArr= data.split(",");
            List<String> dataList= new LinkedList<String>(Arrays.asList(dataArr));
            return decode(dataList);
        }

        public Node decode(List<String>dataList){
            if(dataList.get(0).equals("X")){
                dataList.remove(0);
                return null;
            }

            String val= dataList.get(0);
            dataList.remove(0);
            List<Node> children= new ArrayList<Node>();
            while(!dataList.isEmpty()){
                Node child=decode(dataList);
                if(child!=null)
                    children.add(child);
                else
                    break;
            }
            Node n = new Node(Integer.parseInt(val), children);
            return n;

        }
    }
//Maze
    public boolean hasPath(int[][] maze, int[] start, int[] destination) {

        int m= maze.length;
        int n=maze[0].length;

        boolean[][] visited = new boolean[m][n];

        Queue<int[]> q = new LinkedList<int[]>();
        q.add(start);
        visited[start[0]][start[1]]=true;
        return bfs(q,maze,visited,m,n,destination);
    }

    public boolean bfs(Queue<int[]>q, int[][] maze,boolean[][] visited,int m, int n,int[] destination){
        while(!q.isEmpty()){
            int[] point=q.poll();
            int i= point[0];
            int j=point[1];
            if(i==destination[0] && j==destination[1])
                return true;
            int[] x_dir= {-1,0,1,0};
            int[] y_dir= {0,1,0,-1};
            for(int k=0;k<x_dir.length;k++){
                int new_i=i+x_dir[k];
                int new_j=j+y_dir[k];
                //propogating in same direction//keep rolling//if gate is hanging it will keep rolling
                while(new_i>=0 && new_i<m && new_j>=0 && new_j<n && maze[new_i][new_j]==0){
                    new_i+=x_dir[k];
                    new_j+=y_dir[k];
                }
                //hit a wall
                //the posn just before it hit a wall
                //queue will have all positions before it hit a wall
                if(!visited[new_i-x_dir[k]][new_j-y_dir[k]]){
                    visited[new_i-x_dir[k]][new_j-y_dir[k]]=true;
                    q.add(new int[]{new_i-x_dir[k],new_j-y_dir[k]});
                }
            }
        }
        return false;
    }

}
