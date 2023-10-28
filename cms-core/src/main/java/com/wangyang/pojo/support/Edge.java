package com.wangyang.pojo.support;

import lombok.Data;

@Data
public class Edge {

    private String source;
    private String target;
    private double distance;
    private  double weight;

    public Edge(String source, String target, double distance, double weight) {
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.weight = weight;
    }
}
