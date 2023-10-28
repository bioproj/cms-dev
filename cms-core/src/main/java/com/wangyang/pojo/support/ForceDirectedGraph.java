package com.wangyang.pojo.support;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ForceDirectedGraph {




    private  List<Node> nodes;
    private  List<Edge> edges;
    public ForceDirectedGraph(){
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addNodes(String index,String title,String url){
        Node node = new Node(index,title,url);
        nodes.add(node);
    }
    public void addNodes(String index,String title,String url,double radius){
        Node node = new Node(index,title,url,radius);
        nodes.add(node);
    }
    public void addEdges(String source,String target,double distance,double weight){
        Edge edge = new Edge(source,target,distance,weight);
        edges.add(edge);
    }




}
