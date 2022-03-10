package main.java.datastructures;


import java.util.HashMap;

class TrieNodeII{

    HashMap<Character,TrieNodeII> children;
    boolean word = false;

    public TrieNodeII(){
        children= new HashMap<Character,TrieNodeII>();
    }

}

public class WordDictionary {
    TrieNodeII root;

    public WordDictionary(){
        root= new TrieNodeII();
    }
    //bad
    //b..
    //O(26^M)
    public boolean searchInNode(String word, TrieNodeII node){
        for (int i=0;i<word.length();i++){
            char ch=word.charAt(i);
            if(!node.children.containsKey(ch)){
                if(ch=='.'){
                    for(char x :node.children.keySet()){
                        if(searchInNode(word.substring(i+1),node.children.get(x)))
                            return true;
                    }

                }
                return false;
            }
            node=node.children.get(ch);
        }
        return node.word;
    }

    public boolean search(String word){
       return searchInNode(word,root);
    }

    public void addWord(String word) {
        TrieNodeII node= root;
        for(int i=0;i<word.length();i++){
            if(!node.children.containsKey(word.charAt(i)))
                node.children.put(word.charAt(i),new TrieNodeII());
            node=node.children.get(word.charAt(i));
        }
        node.word=true;
    }
    public static void main(String args[]) {
        WordDictionary dict = new WordDictionary();
        dict.addWord("bad");
        dict.addWord("dad");
        dict.addWord("mad");
        System.out.println(dict.search("pad"));
        System.out.println(dict.search("bad"));
        System.out.println(dict.search(".ad"));
        System.out.println(dict.search("b.."));



    }
}



