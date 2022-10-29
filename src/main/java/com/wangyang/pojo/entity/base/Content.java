package com.wangyang.pojo.entity.base;

import com.wangyang.pojo.enums.ArticleStatus;
import lombok.Data;

import javax.persistence.*;

@Entity(name = "base_article")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER, columnDefinition = "int default 0")
@Data
public class Content extends BaseEntity{


    @Column(name = "status", columnDefinition = "int default 1")
    private ArticleStatus status =ArticleStatus.PUBLISHED;
    private Integer userId;
    private String title;
    private String viewName;
    @Column(name = "original_content", columnDefinition = "longtext not null")
    private String originalContent;
    @Column(name = "format_content", columnDefinition = "longtext")
    private String formatContent;
    @Column(name = "toc_content", columnDefinition = "longtext")
    private String toc;
    private String templateName;
    private String commentTemplateName;
    //是否开启评论
    @Column(columnDefinition = "bit(1) default false")
    private Boolean openComment=false;
    private String pdfPath;
    private String path;
    @Column(columnDefinition = "bit(1) default false")
    private Boolean isSource=false;


}

