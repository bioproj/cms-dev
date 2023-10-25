package com.wangyang.pojo.support;

import lombok.Data;

@Data
public class Edge {

    private Integer source;
    private Integer target;
    private double distance;
    private  double weight;

    public Edge(Integer source, Integer target, double distance, double weight) {
        this.source = source;
        this.target = target;
        this.distance = distance;
        this.weight = weight;
    }
}
