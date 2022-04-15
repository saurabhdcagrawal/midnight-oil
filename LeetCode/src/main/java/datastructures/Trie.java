package main.java.datastructures;
//All time complexities O(M) and space complexities O(M) where M is the length of the word

/*
  Root
  |
 / \
A
|
P
|
P (end)
| links[] one of 26
L
|
E (end)
all links null
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class TrieNode{
    String word; // Can instead use boolean word to denote end of word
    HashMap<Character,TrieNode> children;

    public TrieNode(){
        children= new HashMap<Character,TrieNode>();
    }
}


class Trie {
    TrieNode root;
    public Trie() {
        root = new TrieNode();

    }

    public void insert(String word) {
        TrieNode node=root;
        for(int i=0;i<word.length();i++){
            char ch= word.charAt(i);
            if(!node.children.containsKey(ch))
                node.children.put(ch,new TrieNode());
            node=node.children.get(ch);
        }
        node.word=word;
    }

    public TrieNode searchPrefix(String word) {
        TrieNode node=root;
        for(int i=0;i<word.length();i++){
            char ch= word.charAt(i);
            if(node.children.containsKey(ch))
                node=node.children.get(ch);
            else
                return null;
        }
        return node;
    }

    public boolean search(String word) {
        TrieNode node=searchPrefix(word);
        return (node!=null && node.word!=null &&!node.word.isEmpty());
    }
    public boolean startsWith(String prefix) {
        TrieNode node=searchPrefix(prefix);
        return node!=null;
    }
    public boolean searchInNode(String word, TrieNode node){
        for (int i=0;i<word.length();i++){
            char ch=word.charAt(i);
            if(!node.children.containsKey(ch)){
                if(ch=='.'){
                    for(char x :node.children.keySet()){
                        boolean result=searchInNode(word.substring(i+1),node.children.get(x));
                        if(result)
                            return true;
                    }
                   /* return false; */
                }
                return false;
                /*else
                    return false;*/
            }
            node=node.children.get(ch);
        }
        return node.word!=null;
    }

    public boolean searchInNode(String word){
        return searchInNode(word,root);
    }

    public static void main(String args[]){
        Trie obj = new Trie();
        obj.insert("apple");
        System.out.println(obj.search("apple"));
        System.out.println(obj.search("app"));
        System.out.println(obj.startsWith("app"));
        obj.insert("app");
        System.out.println(obj.search("app"));

        //Word Dictionary part
        obj.insert("bad");
        obj.insert("dad");
        obj.insert("mad");
        System.out.println(obj.searchInNode("pad"));
        System.out.println(obj.searchInNode("bad"));
        System.out.println(obj.searchInNode(".ad"));
        System.out.println(obj.searchInNode("b.."));

    }

}

/**
 * Your Trie object will be instantiated and called as such:
 * Trie obj = new Trie();
 * obj.insert(word);
 * boolean param_2 = obj.search(word);
 * boolean param_3 = obj.startsWith(prefix);
 */