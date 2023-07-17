package com.wangyang.pojo.params;

import com.wangyang.pojo.dto.InputConverter;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.Category;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

@Data
public class CategoryParam implements InputConverter<Category> {

    private String description;
    @NotBlank(message = "Category name can't empty!!")
    private String name;
//    @NotBlank(message = "parentId  can't empty!!")
    private Integer parentId;
    private String templateName;
    private String viewName;
    private Boolean haveHtml;
    private String picPath;
    private String picThumbPath;
    private String path;
    private Integer order;
    private Boolean recommend=false;
    private String articleTemplateName;
    private String recommendTemplateName;
    private Integer articleListSize=10;
    private Boolean isDesc=true;
    private String icon;
    private Set<Integer> tagIds;
    private String originalContent;
    private Boolean parse;
    private Integer userId;
    private String cssClass; //节点的方向
    private Boolean articleUseViewName=false;
}
