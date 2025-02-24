package main.java.twentyfive;
import java.util.Map;
import java.util.HashMap;
/*
A Trie (pronounced "try") or Prefix Tree is a tree data structure used to efficiently retrieve a key from a dataset of strings
*/

public class Trie {
    TrieNode root;
    public Trie() {
        root= new TrieNode();
    }
    /*Time Complexity:O(m), where m is the length of the key. Each operation involves examining or creating a node until the end of the key.
    Space Complexity:O(m). In the worst case, each newly inserted key might require adding m new nodes, resulting inO(m)space usage. */
    public void insert(String word) {
        TrieNode node=root;
        for(int i=0;i<word.length();i++){
            char ch= word.charAt(i);
            if(!node.children.containsKey(ch))
                node.children.put(ch, new TrieNode());
            node=node.children.get(ch);
        }
        node.word=word;
    }
    /* Time Complexity:O(m). Each step involves searching for the next character of the key, requiring m operations
 Space Complexity:O(1).*/
    public TrieNode nodeTraversal(String input){
        TrieNode node=root;
        for(int i=0;i<input.length();i++){
            char ch= input.charAt(i);
            if(!node.children.containsKey(ch))
                return null;
            node=node.children.get(ch);
        }
        return node;
    }

    public boolean search(String word) {
        TrieNode node= nodeTraversal(word);
        return node!=null && node.word!=null && node.word.equals(word);//or isEnd=true
    }

    public boolean startsWith(String prefix) {
        TrieNode node= nodeTraversal(prefix);
        return node!=null; //it may not be a terminal node..
    }
}

class TrieNode{
    String word; //or boolean isEnd.. these will only be present in terminal nodes
    Map<Character,TrieNode> children = new HashMap<Character,TrieNode>();
}

/**
 * Your Trie object will be instantiated and called as such:
 * Trie obj = new Trie();
 * obj.insert(word);
 * boolean param_2 = obj.search(word);
 * boolean param_3 = obj.startsWith(prefix);
 */