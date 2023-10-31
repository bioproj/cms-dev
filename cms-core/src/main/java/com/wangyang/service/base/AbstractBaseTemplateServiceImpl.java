package com.wangyang.service.base;

import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.repository.template.BaseTemplateRepository;
import com.wangyang.service.templates.IBaseTemplateService;


public abstract class AbstractBaseTemplateServiceImpl<BASETEMPLATE extends BaseTemplate,BASETEMPLATEDTO extends BaseTemplate,BASETEMPLATEVO extends BaseVo>
        extends AbstractCrudService<BASETEMPLATE,BASETEMPLATEDTO,BASETEMPLATEVO,Integer>
        implements IBaseTemplateService<BASETEMPLATE,BASETEMPLATEDTO,BASETEMPLATEVO> {


    BaseTemplateRepository<BASETEMPLATE> baseTemplateRepository;
    public AbstractBaseTemplateServiceImpl(BaseTemplateRepository<BASETEMPLATE> baseTemplateRepository) {
        super(baseTemplateRepository);
        this.baseTemplateRepository = baseTemplateRepository;
    }


}
