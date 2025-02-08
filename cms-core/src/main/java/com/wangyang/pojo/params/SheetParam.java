package com.wangyang.pojo.params;

import com.wangyang.pojo.enums.ArticleStatus;
import com.wangyang.pojo.enums.ParseType;
import lombok.Data;

@Data
public class SheetParam {
    private String css;
    private String js;
    private ArticleStatus status ;//=ArticleStatus.PUBLISHED;
    private int userId;
    private String title;
    private String viewName;
    private String originalContent;
    private String formatContent;
    private String path;
    private Integer channelId;
    private String templateName;
//    private String cssContent;
//    private String jsContent;
    private Boolean isSource;
    private String cssClass; //节点的方向
    private String picPath;
    private String picThumbPath;
    private ParseType parseType;
}
