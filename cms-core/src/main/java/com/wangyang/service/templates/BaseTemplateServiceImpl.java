package com.wangyang.service.templates;

import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.entity.CategoryTemplate;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.repository.CategoryTemplateRepository;
import com.wangyang.repository.template.BaseTemplateRepository;
import com.wangyang.service.base.AbstractBaseTemplateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class BaseTemplateServiceImpl  extends AbstractBaseTemplateServiceImpl<BaseTemplate,BaseTemplate, BaseVo> implements IBaseTemplateService<BaseTemplate,BaseTemplate, BaseVo> {

    BaseTemplateRepository<BaseTemplate> baseTemplateRepository;
    public BaseTemplateServiceImpl(BaseTemplateRepository<BaseTemplate> baseTemplateRepository) {
        super(baseTemplateRepository);
        this.baseTemplateRepository = baseTemplateRepository;
    }



}
