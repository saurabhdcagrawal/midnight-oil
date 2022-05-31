package main.java.customDS;

import java.util.*;

class FileSystemInMemory {
    TrieNode root;

    public FileSystemInMemory() {
        root= new TrieNode();
    }

    public List<String> ls(String path) {
        List<String> fileList = new ArrayList<String>();
        TrieNode node =root;
        String[] pathArr = path.split("/");
        for(int i=0;i<pathArr.length;i++){
            //or alternative start path from i=1
            if(pathArr[i]=="")
                continue;
            if(!node.children.containsKey(pathArr[i]))
                return fileList;
            else{
                node= node.children.get(pathArr[i]);
            }
        }

        //curveball 1
        // If path is a file path, returns a list that only contains this file's name.
        if(node.word!=null){
            fileList.add(pathArr[pathArr.length-1]);
        }

        else{
            for (String s: node.children.keySet())
                fileList.add(s);
        }

        //curveball 2
        Collections.sort(fileList);
        return fileList;
    }

  /*
    public void traverseAllPaths(String path, List<String> dirList, TrieNode node) {

        if(node.word!=null)
           dirList.add(node.word);

        if(node.children.isEmpty())
            return;

        for(String child: node.children.keySet())
           traverseAllPaths(path,dirList,node.children.get(child));
    } */

    public TrieNode mkdirAndGetNode(String path){
        TrieNode node= root;
        String[] pathArr= path.split("/");
        for(int i=0;i<pathArr.length;i++){
            if(pathArr[i]=="")
                continue;
            if(!node.children.containsKey(pathArr[i]))
                node.children.put(pathArr[i],new TrieNode());
            node= node.children.get(pathArr[i]);
        }
        //node.word=path;
        return node;
    }

    public void mkdir(String path) {
        mkdirAndGetNode(path);
    }

    public void addContentToFile(String filePath, String content) {
        TrieNode node =mkdirAndGetNode(filePath);
        if(node.word!=null)
            node.word+=content;
        else
            node.word=content;
    }

    public String readContentFromFile(String filePath) {
        TrieNode node= root;
        String[] pathArr= filePath.split("/");
        for(int i=0;i<pathArr.length;i++){
            if(pathArr[i]=="")
                continue;
            if(!node.children.containsKey(pathArr[i]))
                node.children.put(pathArr[i],new TrieNode());
            node= node.children.get(pathArr[i]);
        }
        return node.word;
    }
}

class TrieNode{
    Map<String,TrieNode> children= new HashMap<String,TrieNode>();
    String word=null;

}

/**
 * Your FileSystem object will be instantiated and called as such:
 * FileSystem obj = new FileSystem();
 * List<String> param_1 = obj.ls(path);
 * obj.mkdir(path);
 * obj.addContentToFile(filePath,content);
 * String param_4 = obj.readContentFromFile(filePath);
 */