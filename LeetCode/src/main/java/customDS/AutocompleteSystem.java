package main.java.customDS;

import java.util.*;

class AutocompleteSystem {


    TrieNodeAutocomplete root;
    StringBuffer sb= new StringBuffer();
    HashMap<String,Integer> countMap= new HashMap<String,Integer>();

    public AutocompleteSystem(String[] sentences, int[] times) {
        root=new TrieNodeAutocomplete();
        for(int i=0;i<sentences.length;i++)
            addUpdateWords(sentences[i],times[i]);
    }

    public List<String> input(char c) {

        if(c=='#'){
            addUpdateWords(sb.toString(),1);
            sb=new StringBuffer();
            return new ArrayList<String>();
        }
        sb.append(c);

        TrieNodeAutocomplete node=root;
        for(int i=0;i<sb.length();i++){
            char ch=sb.charAt(i);
            if(!node.children.containsKey(ch))
                return new ArrayList<String>();
            node=node.children.get(ch);
        }

        HashMap<String,Integer> hmap= node.countMap;
        PriorityQueue<String> pq = new PriorityQueue<String>((a, b)->hmap.get(a)==hmap.get(b)?b.compareTo(a):Integer.compare(hmap.get(a),hmap.get(b)));

        for(String str:hmap.keySet()){
            pq.add(str);
            if(pq.size()>3)
                pq.poll();
        }

        int k=pq.size();
        String[]results = new String[k];
        // System.out.println("------------");
        for(int i=k-1;i>=0;i--)
            results[i]=pq.poll();

        return Arrays.asList(results);
    }


    public void addUpdateWords(String word, int time){
        TrieNodeAutocomplete node=root;
        for(int i=0;i<word.length();i++){
            char ch= word.charAt(i);
            if(!node.children.containsKey(ch))
                node.children.put(ch, new TrieNodeAutocomplete());
            node= node.children.get(ch);
            node.countMap.put(word,node.countMap.getOrDefault(word,0)+time);
        }
    }

}
class TrieNodeAutocomplete {

    HashMap<Character, TrieNodeAutocomplete> children= new HashMap<Character, TrieNodeAutocomplete>();
    HashMap<String,Integer> countMap= new HashMap<String,Integer>();

}
/**
 * Your AutocompleteSystem object will be instantiated and called as such:
 * AutocompleteSystem obj = new AutocompleteSystem(sentences, times);
 * List<String> param_1 = obj.input(c);
 */


