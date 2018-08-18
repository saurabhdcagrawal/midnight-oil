package main.java.datastructures;

//To store Graph,we need to store number of vertices ,number of edges and ther interconnections
//We would need a matrix with size V *V where V is number of vertices and the matrix is boolean
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

public class Graph {



}
