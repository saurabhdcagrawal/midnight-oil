package main.java.datastructures;

import java.util.ArrayList;
import java.util.List;

//first cell ..we can go 4 directions from next 3... 4*3^L-1.. total M cells O(M4*3^l-1)

public class TrieCrosswordProblem {

    public List<String> findWords(char[][] board, String[] words) {
        int m = board.length;
        int n = board[0].length;

        List<String> result = new ArrayList<String>();

        TrieNode root = new TrieNode();
        for (String word : words)
            addWord(word, root);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                backtrack(i, j, board, m, n, root, result);
            }
        }

        return result;


    }
    public void backtrack(int i, int j, char[][] board, int m, int n, TrieNode root, List<String> result){

        if(i<0||i>=m||j<0||j>=n||!root.children.containsKey(board[i][j]))
            return;

        char ch=board[i][j];

        TrieNode node=root.children.get(ch);

        if(node.word!=null){
            result.add(node.word);
            //to prevent being tracked and added again
            node.word=null;
            // dont return as  there could be more words down the node.. eg oath..oathify
            // return;
        }

        board[i][j]='#';

        int[] rowOffset = {-1, 0, 1, 0};
        int[] colOffset = {0, 1, 0, -1};

        for(int k=0;k<rowOffset.length;k++){
            int new_i=i+rowOffset[k];
            int new_j=j+colOffset[k];
            backtrack(new_i, new_j,board,m,n,node,result);
        }

        board[i][j]= ch;

        //Optimization
        //pruning
        // //if the child is empty , we must have traveled all paths so far and added any word..so we can
        //go to parent
        if(node.children.isEmpty())
            root.children.remove(ch);

    }

    public void addWord(String word, TrieNode root) {
        TrieNode node = root;
        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (!node.children.containsKey(ch))
                node.children.put(ch, new TrieNode());
            node = node.children.get(ch);
        }
        node.word = word;
    }

    public static void main(String args[]){
        TrieCrosswordProblem prob = new TrieCrosswordProblem();
        char [][] board = {{'o','a','a','n'},{'e','t','a','e'},{'i','h','k','r'},{'i','f','l','v'}};
        String[] words={"oath","pea","eat","rain"};
        System.out.println(prob.findWords(board,words));
    }
}




