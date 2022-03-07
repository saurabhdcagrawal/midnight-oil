package main.java.datastructures;

public class Trie {

    private TrieNode root;

    public Trie() {
        root= new TrieNode();

    }

    public void insert(String word) {
        TrieNode node = root;
        for(int i=0;i<word.length();i++){
            if(!node.contains(word.charAt(i))){
                node.put(word.charAt(i), new TrieNode());
            }
            else
                node=root.getNode(word.charAt(i));
        }
        node.setEnd();


    }

    public TrieNode searchPrefix(String word) {

        for(int i=0;i<word.length();i++){
            if(root.contains(word.charAt(i)))
                root=root.getNode(word.charAt(i));
            else
                return null;
        }
        return root;
    }

    public boolean search(String word) {
        TrieNode node=searchPrefix(word);
        return (node!=null && node.isEnd());

    }
    //cod..cannot be coc
    public boolean startsWith(String prefix) {
        TrieNode node=searchPrefix(prefix);
        return (node!=null);

    }
}

class TrieNode{
    private TrieNode[] links;
    private final int R=26;
    private boolean isEnd;
    //maximum r roots to children

    public TrieNode(){
        links = new TrieNode[26];
    }

    public boolean contains (char ch){
        return links[ch-'a']!=null;
    }

    public void put (char ch, TrieNode node){
        links[ch-'a']=node;
    }

    public TrieNode getNode (char ch){
        return links[ch-'a'];
    }

    public void setEnd(){
        isEnd=true;
    }

    public boolean isEnd(){
        return isEnd;
    }
}

/**
 * Your Trie object will be instantiated and called as such:
 * Trie obj = new Trie();
 * obj.insert(word);
 * boolean param_2 = obj.search(word);
 * boolean param_3 = obj.startsWith(prefix);
 */