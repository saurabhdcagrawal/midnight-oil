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

}
