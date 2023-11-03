package com.wangyang.pojo.vo;

import com.wangyang.common.pojo.BaseVo;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.enums.NetworkType;
import com.wangyang.pojo.enums.TemplateData;
import lombok.Data;

@Data
public class CategoryVO extends BaseCategoryVo {

//    private Integer id;
//    private Integer parentId;
//
    private String name;
    private String icon;
    private String viewName;
    private String path;
    private String linkPath;
    private String recommendPath;
    private String recentPath;
    private String firstTitleList;
    private Integer order;
    private String resource;
    private Boolean haveHtml;
    private Boolean recommend;
    private Boolean existNav;
    private Integer articleNumber;
//    private String templateName;
    private String articleTemplateName;
    private String picPath;
    private String picThumbPath;
    private String description;
    private Boolean isDesc;
    private Integer articleListSize;
    private Integer categoryInComponentOrder;
    private String recommendTemplateName;
    private String originalContent;
    private String formatContent;
    private Boolean parse;
    private String cssClass;

    private User user;
    private Boolean isRecursive;
    private Boolean isDivision;
    private Boolean articleUseViewName;


    private NetworkType networkType;


}
