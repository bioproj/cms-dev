package com.wangyang.service.templates;

import com.wangyang.common.service.ICrudService;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.pojo.entity.base.Relation;

public interface IBaseTemplateService <BASETEMPLATE extends BaseTemplate,BASETEMPLATEDTO,BASETEMPLATEVO>  extends ICrudService<BASETEMPLATE,BASETEMPLATEDTO,BASETEMPLATEVO,Integer> {
}
