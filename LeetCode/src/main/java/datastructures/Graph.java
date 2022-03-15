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

import java.util.ArrayList;
import java.util.List;

public class Graph {
//Number of Connected Components in an Undirected Graph
//build adjacency matrix --- go to every edge --O(E)
//vertex matrix to show every visit
//List of vertices
//create boolean matrix for vertex visited
    //Input: n = 5, edges = [[0,1],[1,2],[3,4]]
    public static int countComponents(int n, int[][] edges) {
        List<Integer>[] adjList= new ArrayList[n];
        for(int i=0;i<n;i++)
            adjList[i]= new ArrayList<Integer>();

        for(int i=0;i<edges.length;i++){
            adjList[edges[i][0]].add(edges[i][1]);
            adjList[edges[i][1]].add(edges[i][0]);
        }
      //  [{1},{0,2},{1},{4},{3}]

        int[] visited = new int[n];
        int connectedComponents=0;

        for(int i=0;i<n;i++){
            if (visited[i]==0)
                connectedComponents++;
            dfs(adjList,visited,i);

        }
       return connectedComponents;
    }
//For building adjacency matrix will take O(E) runtime and space
    //Visiting every node once and then for every vertex we run through adjacency list
    //Time O(E+V) //stack dfs takes O(V) space and also to build the visited array
    //Space complexity O(E+V)
    public static void dfs( List<Integer>[] adjList,int[] visited,int node){
        visited[node]=1;
        for(int i=0;i<adjList[node].size();i++){
            if(visited[adjList[node].get(i)]==0) {
                dfs(adjList, visited, adjList[node].get(i));
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

}
