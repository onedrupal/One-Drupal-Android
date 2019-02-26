package treeutil;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public List<Node> children = new ArrayList<Node>();
    public Node parent;
    public MyObject associatedObject;
    public Node(){

    }
    public Node(MyObject associatedObject) {
        this.associatedObject = associatedObject;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public MyObject getAssociatedObject() {
        return associatedObject;
    }

    public void setAssociatedObject(MyObject associatedObject) {
        this.associatedObject = associatedObject;
    }

}