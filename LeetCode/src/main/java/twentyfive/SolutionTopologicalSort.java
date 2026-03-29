package main.java.twentyfive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class SolutionToplogicalSort {
    private static int WHITE=0;
    private static int GREY=1;
    private static int BLACK=2;

    public int[] findOrder(int numCourses, int[][] prerequisites) {

        int[] color = new int[numCourses];
        List<Integer>[] adjList= new List[numCourses];
        List<Integer> result = new ArrayList<>(numCourses);

        for(int i=0;i<numCourses;i++)
            adjList[i]= new ArrayList<>();

        for(int[] p:prerequisites){
            int a =  p[0];
            int b =  p[1];
            adjList[a].add(b);
        }

        for(int i=0;i<numCourses;i++){
            if(color[i]==WHITE){
                boolean flag = dfs(i,color,adjList,result);
                if(!flag)
                    return new int[]{};
            }
        }

        int[] result_arr = new int[numCourses];
        for(int i=0;i<numCourses;i++){
            result_arr[i]=result.get(i);
        }
        return result_arr;
    }

    public boolean dfs(int course, int[] color, List<Integer>[]adjList,List<Integer> result){
        if(color[course]==GREY)
            return false;
        if(color[course]==BLACK)
            return true;
        color[course]=GREY;
        for(int i=0;i<adjList[course].size();i++){
            int neighbor=adjList[course].get(i);
            boolean flag = dfs(neighbor,color,adjList,result);
            if(!flag)
                return false;
        }
        color[course]=BLACK;
        result.add(course);
        return true;
    }

/**************************************************************************/
    //Every edge and vertex is vsited once,, we use a visited array to mark
    //a vertex once it is visited
    //Building the adjacency list takes O(E+V) space
    //Also the stack can take O(V) space
    public int countComponents(int n, int[][] edges) {
        //for undirected graph, you need 2 states visited not visited
        //adjList points both ways
        boolean[] visited= new boolean[n];
        int connectedComponents=0;
        List<Integer>[] adjList= new List[n];
        for(int i=0;i<n;i++)
            adjList[i]= new ArrayList<>();

        for(int i=0;i<edges.length;i++){
            adjList[edges[i][0]].add(edges[i][1]);
            adjList[edges[i][1]].add(edges[i][0]);
        }
        for(int i=0;i<n;i++){
            if(!visited[i]){
                connectedComponents++;
                dfs(i,adjList,visited);
            }
        }
        return connectedComponents;
    }

    public void dfs(int index, List<Integer>[] adjList,boolean[] visited){
        if(visited[index])
            return;
        visited[index]=true;
        for(int i=0;i<adjList[index].size();i++){
            int neighbor=adjList[index].get(i);
            //so for {1},{0,2}
            dfs(neighbor,adjList,visited);
        }
    }
/*****************************************************************/
    //a graph is a tree if its connected and there exists no cycle
    public boolean validTree(int n, int[][] edges) {
        boolean[] visited= new boolean[n];
        List<Integer>[] adjList= new List[n];
        for(int i=0;i<n;i++)
            adjList[i]= new ArrayList<Integer>();

        for(int i=0;i<edges.length;i++){
            adjList[edges[i][0]].add(edges[i][1]);
            adjList[edges[i][1]].add(edges[i][0]);
        }

        //just start from 0, no need to start forloop for all;
        //if graph is tree from, one vertice, you should be able to visit all and
        //there should be no cycle
        //
        return dfs(0,-1,adjList,visited) && isGraphFullyConnected(visited);
    }
    /*
        0->{1}
        1->{0,2,3,4}
        2->{1,3}
        3->{1,2}
        4->{1}

        */
    public boolean dfs(int index, int parent, List<Integer>[]adjList,boolean[] visited){
        if(visited[index])
            return false;
        visited[index]=true;
        for(int i=0;i<adjList[index].size();i++){
            int neighbor= adjList[index].get(i);
            if(neighbor!=parent){
                boolean flag=dfs(neighbor,index,adjList,visited);
                if(!flag)
                    return false;
            }
        }
        return true;
    }

    public boolean isGraphFullyConnected(boolean[]  visited){
        for(int i=0;i<visited.length;i++){
            if(!visited[i])
                return false;
        }
        return true;
    }
   /******************Extra room and keys **************************************************/
/*
    Input: rooms = [[1],[2],[3],[]]
    Output: true
    Explanation:
    We visit room 0 and pick up key 1.
    We then visit room 1 and pick up key 2.
    We then visit room 2 and pick up key 3.
    We then visit room 3.
    Since we were able to visit every room, we return true.
    Example 2:

    Input: rooms = [[1,3],[3,0,1],[2],[0]]
    Output: false
    Explanation: We can not enter room number 2 since the only key that unlocks it is in that room.*/

    //// This problem can be viewed as a graph traversal where:
    // - Rooms represent vertices (nodes).
    // - Keys act as edges connecting these vertices.
    // Then the problem reduces to starting from vertex 0 can we traverse the entire graph and visit every node, i.e. if the graph is fully connected (i.e., not disjoint).
    //Let N be the number of rooms and you can specify that E is the total number of keys (edges) across all rooms.
    //Time Complexity is O(N+E) we visited every room and key once
    //Space Complexity O(N) as recursion stack can grow up to N rooms..if the rooms
    //form a linear chain
    //Also visited array needs O(N) space


    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        int n = rooms.size();
        boolean[] visited= new boolean[n];

        dfs(0,rooms,visited);
        for(boolean v:visited){
            if(!v)
                return false;
        }
        return true;
    }

    public void dfs(int index, List<List<Integer>> rooms, boolean[]visited){
        if(visited[index])
            return;

        visited[index]=true;

        for(int i=0;i<rooms.get(index).size();i++){
            int key= rooms.get(index).get(i);
            if(!visited[key])
                dfs(key,rooms,visited);
        }
    }
}