package com.wangyang.pojo.support;

import lombok.Data;

@Data
public class Node {
    private String id;
    private String title;
    private String url;
    private double radius=8;

    public Node(String id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }
    public Node(String id, String title, String url,double radius) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.radius=radius;
    }
}
