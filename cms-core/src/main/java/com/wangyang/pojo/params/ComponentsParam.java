package com.wangyang.pojo.params;


import lombok.Data;

@Data
public class ComponentsParam {
    private String name;
    private String description;
    private String templateValue;
    private String viewName;
    private String enName;
    private String dataName;
    private String dataArgs;
    private String path;
    private String originalContent;

    private Boolean parse=false;

}
