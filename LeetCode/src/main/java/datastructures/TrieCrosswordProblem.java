package main.java.datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrieCrosswordProblem {
        public List<String> findWords(char[][] board, String[] words) {
            //create a Trie first and then backtracking
            TrieNodeIII root= new TrieNodeIII();
            List<String> result= new ArrayList<String>();
            for(int i=0;i<words.length;i++)
                addWord(root,words[i]);

            for(int i=0;i<board.length;i++){
                for(int j=0;j<board[0].length;j++){
                    if(root.children.containsKey(board[i][j]))
                        backtrack(i,j,root,board,result);
                }
            }


            return result;
        }

        public void backtrack(int i, int j, TrieNodeIII root,char[][] board,List<String> result){
            char ch= board[i][j];
            TrieNodeIII node=root.children.get(ch);
            if(node.word!=null){
                result.add(node.word);
                node.word=null;
            }
            board[i][j] = '#';
            int[] rowOffset = {-1, 0, 1, 0};
            int[] colOffset = {0, 1, 0, -1};
            for(int k=0; k<4;k++){
                i+=rowOffset[k];
                j+=colOffset[k];
                if(i>=0 && j>=0 && i<board.length && j<board[0].length){
                    ch= board[i][j];
                    if(node.children.containsKey(ch))
                        backtrack(i,j,node,board,result);
                }
            }
           // End of EXPLORATION, restore the original letter in the board.
            board[i][j] =ch;
        }

        public void addWord(TrieNodeIII root,String word){
            TrieNodeIII node = root;
            for(int i=0; i<word.length();i++){
                char ch = word.charAt(i);
                if(!node.children.containsKey(ch))
                    node.children.put(ch, new TrieNodeIII());
                node=node.children.get(ch);
            }
            node.word=word;
        }


    public static void main(String args[]) {
        TrieCrosswordProblem prob = new TrieCrosswordProblem();
        char [][] board = {{'o','a','a','n'},{'e','t','a','e'},{'i','h','k','r'},{'i','f','l','v'}};
        String[] words={"oath","pea","eat","rain"};
        System.out.println(prob.findWords(board,words));

    }
}
    class TrieNodeIII{
        HashMap<Character,TrieNodeIII> children;
        String word;
        public TrieNodeIII(){
            children= new HashMap<Character,TrieNodeIII>();
        }
    }


