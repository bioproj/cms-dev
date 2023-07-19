package com.wangyang.pojo.vo;

import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.ArticleDto;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.dto.TagsDto;
import com.wangyang.pojo.enums.ArticleStatus;
import lombok.Data;

import javax.persistence.Column;
import java.io.File;
import java.util.Date;
import java.util.List;

@Data
public class ArticleVO extends ContentVO {
//    private List<TagsDto> tags;
//    private CategoryDto category;
//    private String cssClass;
//    private Integer id;
//    private Date createDate;
//    private Date updateDate;
//    //    private Integer templateId;
//    private String templateName;
////    private Integer parentId;
//    private ArticleStatus status;
//    private Integer likes;
//    private Integer visits;
//    private Integer userId;
//    private Integer commentNum;
//    //    private Boolean haveHtml=false;
//    private String summary;
//    private String title;
//    private String viewName;
//    private String  path;
//    private String picPath;
//    private String picThumbPath;
//    private String pdfPath;
//    private String toc;
//    private User user;
//    private Integer categoryId;
//    private String tocJSON;
//    private String commentTemplateName;
//    //是否开启评论
//    private Boolean openComment;
//    private Integer articleListSize;
//    private Boolean isDesc;
//    private Integer order;
//    // 路径格式
//    private String linkPath ;
//    private Boolean top;
//    private String formatContent;
    private Integer articleInComponentOrder;
    private String bilibili;
    private String youtube;
    private String video;

//    public String getLinkPath() {
//      return File.separator+this.getPath().replace(File.separator,"_")+"_"+this.getViewName()+".html";
//    }
}
