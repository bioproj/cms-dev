package com.wangyang.service.templates;

import com.wangyang.common.service.ICrudService;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.pojo.entity.base.Relation;
import com.wangyang.pojo.enums.TemplateType;

import java.util.List;

public interface IBaseTemplateService <BASETEMPLATE extends BaseTemplate,BASETEMPLATEDTO,BASETEMPLATEVO>  extends ICrudService<BASETEMPLATE,BASETEMPLATEDTO,BASETEMPLATEVO,Integer> {
    List<BASETEMPLATE> findByCategoryId(Integer id);

    BASETEMPLATE findByMainCategoryId(Integer id, TemplateType templateType);

    BASETEMPLATE findByMainCategoryId(Integer id);
}
