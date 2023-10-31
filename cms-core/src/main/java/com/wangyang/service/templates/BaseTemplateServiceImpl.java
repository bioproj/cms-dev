package com.wangyang.service.templates;

import com.wangyang.common.pojo.BaseVo;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.repository.template.BaseTemplateRepository;
import com.wangyang.service.base.AbstractBaseTemplateServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BaseTemplateServiceImpl  extends AbstractBaseTemplateServiceImpl<BaseTemplate,BaseTemplate, BaseVo> implements IBaseTemplateService<BaseTemplate,BaseTemplate, BaseVo> {

    BaseTemplateRepository<BaseTemplate> baseTemplateRepository;
    public BaseTemplateServiceImpl(BaseTemplateRepository<BaseTemplate> baseTemplateRepository) {
        super(baseTemplateRepository);
        this.baseTemplateRepository = baseTemplateRepository;
    }
}
