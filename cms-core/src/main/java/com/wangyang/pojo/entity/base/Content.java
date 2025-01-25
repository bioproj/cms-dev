package com.wangyang.pojo.entity.base;

import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.pojo.enums.ArticleStatus;
import com.wangyang.pojo.enums.ParseType;
import lombok.Data;

import javax.persistence.*;

@Entity(name = "base_article")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER, columnDefinition = "int default 0")
@Data
public class Content extends BaseEntity {
    private String summary;
    private String picPath;
    private String picThumbPath;
    @Column(name = "status", columnDefinition = "int default 1")
    private ArticleStatus status =ArticleStatus.PUBLISHED;
//    private Integer userId;
    private Boolean useTemplatePath;
    private String title;
    private String viewName;
    @Column(name = "original_content", columnDefinition = "longtext not null")
    private String originalContent;
    @Column(name = "format_content", columnDefinition = "longtext")
    private String formatContent;
    @Column(name = "toc_content", columnDefinition = "longtext")
    private String toc;
    @Column(name = "toc_json", columnDefinition = "longtext")
    private String tocJSON;
    @Column(name = "css_", columnDefinition = "longtext")
    private String css;
    @Column(name = "js_", columnDefinition = "longtext")
    private String js;
    private String templateName;
    private String categoryPath;
    private String categoryViewName;
    private Boolean isArticleDocLink;


    private String commentTemplateName;
    //是否开启评论
    @Column(columnDefinition = "bit(1) default false")
    private Boolean openComment=true;
    private String pdfPath;
    private String path;
    @Column(columnDefinition = "bit(1) default false")
    private Boolean isSource=false;
    private Integer categoryId;
    @Column(name = "article_order", columnDefinition = "int default 0")
    private Integer order;
    @Column(name = "article_top",columnDefinition = "bit(1) default false")
    private Boolean top=false;
    // 父亲id 0是只没有父亲 此时指向category
    private Boolean expanded; // 节点是否展开
    private String direction; //节点的方向
    private String cssClass;

    private Boolean isDivision=false;
    private Integer articleInComponentOrder=0;
//    private Boolean isParseMarkdown;
    @Enumerated(value = EnumType.STRING)
    private ParseType parseType;
}

