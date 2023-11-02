package com.wangyang.pojo.entity.base;

import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.pojo.enums.TemplateData;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "t_category")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER, columnDefinition = "int default 0")
@Data
public class BaseCategory extends BaseEntity {
    private String name;
    private String cssClass; //节点的方向
    private Boolean useTemplatePath;
//    private String templateName;
    private String path;
    private String viewName;
    private Integer categoryInComponentOrder=0;
    @Column(name = "template_data_name")
    private TemplateData templateData;
}
