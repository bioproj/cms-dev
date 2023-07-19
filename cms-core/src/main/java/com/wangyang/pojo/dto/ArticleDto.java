package com.wangyang.pojo.dto;

import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.enums.ArticleStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ArticleDto  implements Serializable {

    private String tocJSON;
    private Integer id;
    private Date createDate;
    private Date updateDate;
    //    private Integer templateId;
    private String templateName;
    private Integer parentId;
    private ArticleStatus status;
    private Integer likes;
    private Integer visits;
    private Integer userId;
    private Integer commentNum;
    //    private Boolean haveHtml=false;
    private String summary;
    private String title;
    private String viewName;
    private String  path;
    private String picPath;
    private String picThumbPath;
    private String pdfPath;
    private String toc;
    private User user;
    private Integer categoryId;

    private String commentTemplateName;
    //是否开启评论
    private Boolean openComment;
    private Integer articleListSize;
    private Boolean isDesc;
    private Integer order;
    // 路径格式
    private String linkPath ;
    private Boolean top;
    private String bilibili;
    private String youtube;
    private String video;



}
