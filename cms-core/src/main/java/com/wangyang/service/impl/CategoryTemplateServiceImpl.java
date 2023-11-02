package com.wangyang.service.impl;

import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.pojo.entity.CategoryTemplate;
import com.wangyang.pojo.enums.TemplateType;
import com.wangyang.repository.CategoryTemplateRepository;
import com.wangyang.service.ICategoryTemplateService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class CategoryTemplateServiceImpl extends AbstractCrudService<CategoryTemplate, CategoryTemplate, BaseVo,Integer> implements ICategoryTemplateService {


    CategoryTemplateRepository categoryTemplateRepository;
    public CategoryTemplateServiceImpl(CategoryTemplateRepository categoryTemplateRepository) {
        super(categoryTemplateRepository);
        this.categoryTemplateRepository = categoryTemplateRepository;
    }


    @Override
    public CategoryTemplate findByCategoryIdAndTemplateType(int categoryId){
        CategoryTemplate categoryTemplate = categoryTemplateRepository.findByCategoryIdAndTemplateType(categoryId, TemplateType.CATEGORY);
        return categoryTemplate;
    }

    @Override
    public List<CategoryTemplate> listByCategoryId(Integer id) {
        List<CategoryTemplate> categoryTemplates = categoryTemplateRepository.findAll(new Specification<CategoryTemplate>() {
            @Override
            public Predicate toPredicate(Root<CategoryTemplate> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("categoryId"),id)).getRestriction();
            }
        });
        return categoryTemplates;
    }
}
