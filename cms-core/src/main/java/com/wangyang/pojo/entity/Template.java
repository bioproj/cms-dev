package com.wangyang.pojo.entity;

import com.wangyang.common.annotation.QueryField;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.pojo.enums.TemplateData;
import com.wangyang.pojo.enums.TemplateType;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@DiscriminatorValue(value = "0")
@Data
public class Template extends BaseTemplate implements Serializable {



//    @Column(name = "template_type", columnDefinition = "int")
    @QueryField
    @Enumerated(EnumType.STRING)
    @Column(name = "template_type_name")
    private TemplateType templateType;
//    @Column(name = "template_type_name")
    @Column(name = "template_type")
    private Integer templateTypeTmp;
    @QueryField
    @Enumerated(EnumType.STRING)
    @Column(name = "template_data_name")
    private TemplateData templateData;
    @Column(name = "template_data")
    private Integer templateDateTmp;
    private Integer parentOrder=-2;

    public Template(){}

    public Template(String name,String enName,String templateValue,TemplateType templateType,Integer order){
        super.setName(name);
        super.setEnName(enName);
        super.setTemplateValue(templateValue);
        this.templateType = templateType;
        this.setStatus(false);
        this.setOrder(order);
        this.setIsSystem(true);
        this.setTemplateData(TemplateData.OTHER);
    }
    public Template(String name,String enName,String templateValue,TemplateType templateType,Boolean isSystem){
        super.setName(name);
        super.setEnName(enName);
        super.setTemplateValue(templateValue);
        this.templateType = templateType;
        this.setStatus(false);
        this.setIsSystem(isSystem);
        this.setTemplateData(TemplateData.OTHER);
    }
    public Template(String name,String enName,String templateValue,TemplateType templateType,Integer order,Boolean tree){
        super.setName(name);
        super.setEnName(enName);
        super.setTemplateValue(templateValue);
        this.templateType = templateType;
        this.setStatus(false);
        this.setOrder(order);
        super.setTree(tree);
        this.setIsSystem(true);
        this.setTemplateData(TemplateData.OTHER);
    }
    public Template(String templateValue) {
        super.setTemplateValue(templateValue);
    }

}
