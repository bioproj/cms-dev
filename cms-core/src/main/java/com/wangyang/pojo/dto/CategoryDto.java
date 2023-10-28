package com.wangyang.pojo.dto;


import lombok.Data;

@Data
public class CategoryDto {

    private Integer id;
    private String name;
    private Integer parentId;
    private String viewName;
    private String path;
    private String linkPath;
    private Integer order;
    private String resource;
    private String icon;
//    private String articleTemplateName;
    private Boolean isArticleDocLink;
    private Boolean isRecursive;
    private Boolean isDivision;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryDto myObject = (CategoryDto) o;
        return id.equals(myObject.id);
    }
    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
