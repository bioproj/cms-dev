package com.wangyang.pojo.entity;

import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.enums.NetworkType;
import lombok.Data;

import javax.persistence.*;
@Entity
@DiscriminatorValue(value = "0")
@Data
//@Table(name = "t_category")
public class Category extends BaseCategory{

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;

//    @Column(columnDefinition = "int default 0")
//    private Integer parentId;
    @Column(columnDefinition = "longtext")
    private String description;
    @Column(name = "original_content", columnDefinition = "longtext")
    private String originalContent;
    @Column(name = "format_content", columnDefinition = "longtext")
    private String formatContent;
    @Column(columnDefinition = "int default 0")
    private Integer articleNumber;
//    private Integer templateId;

    @Column(columnDefinition = "bit(1) default true")
    private Boolean haveHtml=true;






    private String viewName;
//    @Column(columnDefinition = "bit(1) default true")
//    private Boolean status=true;
    private String picPath;
    private String picThumbPath;

//    @Column(name = "category_order",columnDefinition = "int default 1")
//    private Integer order;
    @Column(columnDefinition = "bit(1) default false")
    private Boolean recommend=false;
    @Column(columnDefinition = "bit(1) default false")
    private Boolean existNav=false;

    private String recommendTemplateName;
    private String icon;
    // 每页显示文章的数量
    private Integer articleListSize=10;
//    private Integer articleListPage=0;
    private Boolean isDesc=true;

    @Column(name = "parse_")
    private Boolean parse=true;




    private Boolean isRecursive=false;
    private Boolean isDivision=false;

    private NetworkType networkType=NetworkType.NONE;
    private String templateName;
    public Boolean getDesc() {
        return isDesc;
    }

    public void setDesc(Boolean desc) {
        isDesc = desc;
    }
}
