package main.java;

public class ReverseLL {

    public NodeInt reverseLL(NodeInt ll){
     // a->b->c->d->e
        NodeInt current= ll;
        NodeInt prev=null;
        while(current!=null) { //current =e
            NodeInt nextNodeInLine = current.next; // nextNodeInLine=null
            current.next = prev; // NULL<-a<-b<-c<-d<-e
            prev = current;//prev=e
            current = nextNodeInLine;//current=c
        }
       return prev;
    }
    public static void main(String args[]){
        NodeInt n= new NodeInt(1);
            n.addNode(2);
            n.addNode(3);
            n.print();
        ReverseLL sample= new ReverseLL();
        NodeInt j=sample.reverseLL(n);
        j.print();


    }


}

class NodeInt{

    int value;
    NodeInt next=null;
    public NodeInt(int value){
        this.value=value;
    }
    public void addNode(int val){
        NodeInt n = this;
        while(n.next!=null) {
            n = n.next;
        }
        n.next= new NodeInt(val);
    }
    public void print(){
        NodeInt n= this;
        while(n!=null){
            System.out.print(n.value+"->");
            n=n.next;
        }
        System.out.println("");
    }
}