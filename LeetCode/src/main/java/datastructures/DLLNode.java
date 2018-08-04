package main.java.datastructures;

class DLLNode {
    int key;
    int value;
    DLLNode prev;
    DLLNode next;

    public DLLNode(int key, int value) {
        this.key = key;
        this.value = value;
        prev = null;
        next = null;

    }
}