package com.wangyang.pojo.support;

import lombok.Data;

@Data
public class Node {
    private Integer id;
    private String title;
    private String url;

    public Node(Integer id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }
}
