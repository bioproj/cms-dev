package com.wangyang.service.base;

import com.wangyang.common.enums.Lang;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.entity.CategoryTemplate;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.pojo.enums.TemplateType;
import com.wangyang.repository.CategoryTemplateRepository;
import com.wangyang.repository.template.BaseTemplateRepository;
import com.wangyang.service.ICategoryTemplateService;
import com.wangyang.service.templates.IBaseTemplateService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;


public abstract class AbstractBaseTemplateServiceImpl<BASETEMPLATE extends BaseTemplate,BASETEMPLATEDTO extends BaseTemplate,BASETEMPLATEVO extends BaseVo>
        extends AbstractCrudService<BASETEMPLATE,BASETEMPLATEDTO,BASETEMPLATEVO,Integer>
        implements IBaseTemplateService<BASETEMPLATE,BASETEMPLATEDTO,BASETEMPLATEVO> {
    @Autowired
    ICategoryTemplateService categoryTemplateService;

    @Autowired
    CategoryTemplateRepository categoryTemplateRepository;

    BaseTemplateRepository<BASETEMPLATE> baseTemplateRepository;
    public AbstractBaseTemplateServiceImpl(BaseTemplateRepository<BASETEMPLATE> baseTemplateRepository) {
        super(baseTemplateRepository);
        this.baseTemplateRepository = baseTemplateRepository;
    }
    @Override
    public List<BASETEMPLATE> findByCategoryId(Integer id) {
        List<CategoryTemplate> categoryTemplates = categoryTemplateService.listByCategoryId(id);
        Set<Integer> templateIds = ServiceUtil.fetchProperty(categoryTemplates, CategoryTemplate::getTemplateId);
        List<BASETEMPLATE> baseTemplates = listByIds(templateIds);
        return baseTemplates;
    }
    @Override
    public BASETEMPLATE findByMainCategoryId(Integer id, TemplateType templateType, Lang lang) {
        CategoryTemplate categoryTemplate = categoryTemplateService.findByCategoryIdAndTemplateType(id, templateType,lang);
        if(categoryTemplate==null){
            throw new ObjectException("categoryTemplate"+templateType.getName()+"不存在！");
        }
        BASETEMPLATE basetemplate = findById(categoryTemplate.getTemplateId());
        return basetemplate;
    }
    @Override
    public BASETEMPLATE findByMainCategoryId(Integer id, Lang lang) {

        return findByMainCategoryId(id,TemplateType.CATEGORY,lang);
    }
}
