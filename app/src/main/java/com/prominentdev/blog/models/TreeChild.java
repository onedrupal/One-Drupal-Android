package com.prominentdev.blog.models;

import java.io.Serializable;

public class TreeChild implements Serializable {
    String name;
    String id;
    String parentId;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "name='" + name + ", parentId='" + parentId + '\n';

    }
}
