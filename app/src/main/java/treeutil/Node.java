package treeutil;

/*
 * Copyright (c) 2019. Nikhil Dubbaka from TechNikh.com under GNU AFFERO GENERAL PUBLIC LICENSE
 * Copyright and license notices must be preserved.
 * When a modified version is used to provide a service over a network, the complete source code of the modified version must be made available.
 */

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