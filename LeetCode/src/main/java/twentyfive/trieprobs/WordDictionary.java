package main.java.twentyfive.trieprobs;

import java.util.HashMap;
import java.util.Map;
class WordDictionary {
    class TrieNode{
        Map<Character, main.java.twentyfive.trieprobs.WordDictionary.TrieNode> child= new HashMap<>();
        boolean isEnd;
    }
    TrieNode root;
    public WordDictionary() {
        root= new TrieNode();
    }

    public void addWord(String word) {
        TrieNode node= root;
        for(int i=0;i<word.length();i++){
            char c = word.charAt(i);
            if(!node.child.containsKey(c)){
                node.child.put(c,new TrieNode());
            }
            node=node.child.get(c);
        }
        node.isEnd=true;
    }

    //care..

    //c.r
    //heaby on iteration
    public boolean searchPrefix(String prefix, TrieNode node) {
        if(node==null) return false;
        if(prefix==null) return node.isEnd;

        for(int i=0;i<prefix.length();i++){
            char c = prefix.charAt(i);
            if(c=='.'){
                for(char j :node.child.keySet()){
                    boolean result=searchPrefix(prefix.substring(i+1),node.child.get(j));
                    if(result)
                        return true;
                }
                return false;
            }
            else{
                if(!node.child.containsKey(c)){
                    return false;
                }
            }
            node= node.child.get(c);
        }
        return node.isEnd ;
    }

    //heavy on recursion
    public boolean searchPrefixNew(String prefix, TrieNode node) {
        if(node==null || prefix==null) return false;
        if(prefix.isEmpty()) return node.isEnd;
        char c = prefix.charAt(0);
        if(c=='.'){
            for(TrieNode n:node.child.values()){
                boolean result=searchPrefixNew(prefix.substring(1),n);
                if(result)
                    return true;
            }
            return false;
        }
        else{
            if(!node.child.containsKey(c)){
                return false;
            }
            return searchPrefixNew(prefix.substring(1),node.child.get(c));
        }
    }


    //improvised further
    public boolean searchPrefixNewPlus(String prefix, TrieNode node,int index) {
        if(node==null || prefix==null) return false;
        if(prefix.length()==index) return node.isEnd;
        char c = prefix.charAt(index);
        if(c=='.'){
            for(TrieNode n:node.child.values()){
                boolean result=searchPrefixNewPlus(prefix,n,index+1);
                if(result)
                    return true;
            }
            return false;
        }
        else{
            TrieNode n= node.child.get(c);
            if(n==null) return false;

            return searchPrefixNewPlus(prefix,n,index+1);
        }
    }

    public boolean search(String word) {
        //return searchPrefix(word,root);
        // return searchPrefixNew(word,root);
        return searchPrefixNewPlus(word,root,0);
    }
}

/**
 * Your WordDictionary object will be instantiated and called as such:
 * WordDictionary obj = new WordDictionary();
 * obj.addWord(word);
 * boolean param_2 = obj.search(word);
 */

