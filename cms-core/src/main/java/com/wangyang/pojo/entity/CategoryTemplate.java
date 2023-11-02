package com.wangyang.pojo.entity;

import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.pojo.enums.TemplateType;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@Entity
public class CategoryTemplate extends BaseEntity {

    private Integer categoryId;
    private Integer templateId;

    @Enumerated(EnumType.STRING)
    private TemplateType templateType;

    public CategoryTemplate(){}

    public CategoryTemplate(Integer categoryId, Integer templateId, TemplateType templateType) {
        this.categoryId = categoryId;
        this.templateId = templateId;
        this.templateType = templateType;
    }
}
