package main.java.customDS;

import java.util.HashMap;
import java.util.Map;

class FileSystem {

    Map<String,Integer> validPaths=null;

    public FileSystem() {
        validPaths= new HashMap<String,Integer>();
    }

    public boolean createPath(String path,int value) {
        if(path==null||path.isEmpty()||path.equals("")||path.equals("/")||validPaths.containsKey(path))
            return false;

        String getParentFromSub=path.substring(0,path.lastIndexOf("/"));
        //in case of firstpath, this value be empty
        if(getParentFromSub.length()>1 && !validPaths.containsKey(getParentFromSub))
            return false;

        validPaths.put(path,value);
        return true;
    }

    public int get(String path) {
        if (!validPaths.containsKey(path))
            return -1;
        return validPaths.get(path);
    }
}

/**
 * Your FileSystem object will be instantiated and called as such:
 * FileSystem obj = new FileSystem();
 * boolean param_1 = obj.createPath(path,value);
 * int param_2 = obj.get(path);
 */