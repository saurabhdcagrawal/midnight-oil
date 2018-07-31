package main.java.datastructures;

public class BTreeNode {
    int data;
    BTreeNode left;
    BTreeNode right;

   public BTreeNode(int data){
   this.data=data;
   left=null;
   right=null;
  }

  public  void insertNode(BTreeNode root,int data) {
      if (root == null)
          root = new BTreeNode(data);
      else if (data < root.data)
          insertNode(root.left, data);
      else
          insertNode(root.right, data);
  }

  public void inorderTraversal(BTreeNode root){
   if(root==null)
   return;
   inorderTraversal(root.left);
   System.out.println("Visiting current node"+root.data);
   inorderTraversal(root.right);
   }

    public void preorderTraversal(BTreeNode root){
       if(root==null)
            return;
        System.out.println("Visiting current node"+root.data);
        preorderTraversal(root.left);
        preorderTraversal(root.right);
    }

    public void postorderTraversal(BTreeNode root){
        if(root==null)
            return;
        postorderTraversal(root.left);
        postorderTraversal(root.right);
        System.out.println("Visiting current node"+root.data);
        }

  public int getDepth(BTreeNode root){
       if(root==null)
        return 1;
       else return
               (Math.max(getDepth(root.left),getDepth(root.right))+1);
  }

}