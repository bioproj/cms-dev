package com.wangyang.service.impl;

import com.wangyang.common.enums.Lang;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.pojo.entity.CategoryTemplate;
import com.wangyang.pojo.enums.TemplateType;
import com.wangyang.repository.CategoryTemplateRepository;
import com.wangyang.service.ICategoryTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
@Slf4j
public class CategoryTemplateServiceImpl extends AbstractCrudService<CategoryTemplate, CategoryTemplate, BaseVo,Integer> implements ICategoryTemplateService {


    CategoryTemplateRepository categoryTemplateRepository;
    public CategoryTemplateServiceImpl(CategoryTemplateRepository categoryTemplateRepository) {
        super(categoryTemplateRepository);
        this.categoryTemplateRepository = categoryTemplateRepository;
    }
    @Override
    public CategoryTemplate  findByCategoryIdAndTemplateType(int categoryId, TemplateType templateType, Lang lang){
        List<CategoryTemplate> categoryTemplates = categoryTemplateRepository.findAll(new Specification<CategoryTemplate>() {
            @Override
            public Predicate toPredicate(Root<CategoryTemplate> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("categoryId"),categoryId),
                        criteriaBuilder.equal(root.get("templateType"),templateType),
                        criteriaBuilder.equal(root.get("lang"),lang)).getRestriction();
            }
        });
        if(categoryTemplates.size()==1){
            return categoryTemplates.get(0);
        } else if (categoryTemplates.size()>1) {
            categoryTemplates.forEach(item->{
                log.info(item.getId()+"");
            });
            throw new ObjectException("找到两个个Category！！");
        } else {
            return null;
        }
    }

    @Override
    public CategoryTemplate findByCategoryIdAndTemplateType(int categoryId, Lang lang){
        CategoryTemplate categoryTemplate = findByCategoryIdAndTemplateType(categoryId, TemplateType.CATEGORY,lang);
        return categoryTemplate;
    }
    @Override
    public CategoryTemplate findByCategoryIdAndTemplateType(int categoryId){
        return findByCategoryIdAndTemplateType(categoryId,Lang.ZH);
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

    @Override
    public List<CategoryTemplate> listByTemplateId(Integer id) {
        List<CategoryTemplate> categoryTemplates = categoryTemplateRepository.findAll(new Specification<CategoryTemplate>() {
            @Override
            public Predicate toPredicate(Root<CategoryTemplate> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("templateId"),id)).getRestriction();
            }
        });
        return categoryTemplates;
    }
}
