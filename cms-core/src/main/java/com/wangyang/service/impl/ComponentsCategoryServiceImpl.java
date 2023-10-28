package com.wangyang.service.impl;

import com.wangyang.common.exception.ObjectException;
import com.wangyang.pojo.entity.*;
import com.wangyang.common.enums.CrudType;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.vo.BaseCategoryVo;
import com.wangyang.repository.ComponentsCategoryRepository;
import com.wangyang.service.ICategoryService;
import com.wangyang.service.IComponentsCategoryService;
import com.wangyang.service.IComponentsService;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.service.base.IBaseCategoryService;
import org.quartz.impl.calendar.BaseCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

@Service
public class ComponentsCategoryServiceImpl extends AbstractCrudService<ComponentsCategory,ComponentsCategory, BaseVo,Integer>
        implements IComponentsCategoryService  {

    @Autowired
    IComponentsService componentsService;

//    @Autowired
//    ICategoryService categoryService;


    @Autowired
    IBaseCategoryService<BaseCategory, BaseCategory, BaseCategoryVo> baseCategoryService;

    ComponentsCategoryRepository componentsCategoryRepository;
    public ComponentsCategoryServiceImpl( ComponentsCategoryRepository componentsCategoryRepository) {
        super(componentsCategoryRepository);
        this.componentsCategoryRepository = componentsCategoryRepository;
    }



    @Override
    public List<ComponentsCategory> findByCategoryId(Integer categoryId){
        return componentsCategoryRepository.findByCategoryId(categoryId);
    }

    @Override
    public ComponentsCategory add(String viewName, int componentsId) {
        BaseCategory category = baseCategoryService.findByViewName(viewName);
        Components components = componentsService.findById(componentsId);

        if(category==null){
            throw new ObjectException("要添加的文章不存在！！");
        }
        if(components==null){
            throw new ObjectException("要添加的组件不存在！！");
        }
        ComponentsCategory componentsCategory = componentsCategoryRepository.findByCategoryIdAndComponentId(category.getId(), componentsId);
        if(componentsCategory!=null){
            throw new ObjectException("["+category.getName()+"]已经在组件["+components.getName()+"]中！！！");
        }
        ComponentsCategory componentsArticle = new ComponentsCategory();
        componentsArticle.setCategoryId(category.getId());
        componentsArticle.setComponentId(components.getId());
        return  componentsCategoryRepository.save(componentsArticle);
//        if(components.getDataName().equals(CmsConst.CATEGORY_DATA) ||
//                components.getDataName().equals(CmsConst.CATEGORY_ARTICLE_DATA) ||
//                components.getDataName().equals(CmsConst.CATEGORY_ARTICLE_PAGE_DATA) ||
//                components.getDataName().equals(CmsConst.CATEGORY_ARTICLE_SIZE_DATA) ||
//                components.getDataName().equals(CmsConst.CATEGORY_CHILD_DATA) ||
//                components.getDataName().equals(CmsConst.CATEGORY_CHILD_DATA)
//                ){
//
//        }
//        throw new ObjectException("文章["+category.getName()+"]不能添加到组件["+components.getName()+"]中");
    }

    @Override
    public void delete(int id) {
        componentsCategoryRepository.deleteById(id);
    }

    @Override
    public ComponentsCategory delete(Integer categoryId, Integer componentId) {
        ComponentsCategory componentsCategory = componentsCategoryRepository.findByCategoryIdAndComponentId(categoryId, componentId);
        if(componentsCategory!=null){
            componentsCategoryRepository.deleteById(componentsCategory.getId());

        }
        return componentsCategory;
    }


    @Override
    public List<ComponentsCategory> findByCategoryId(Set<Integer> categoryIds){
        List<ComponentsCategory> componentsCategories = componentsCategoryRepository.findAll(new Specification<ComponentsCategory>() {
            @Override
            public Predicate toPredicate(Root<ComponentsCategory> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.in(root.get("categoryId")).value(categoryIds)).getRestriction();
//                return criteriaQuery.where(criteriaBuilder.in(root.get("categoryId")).value(categoryIds), criteriaBuilder.isTrue(root.get("hasArticle"))).getRestriction();
            }
        });
        return componentsCategories;
    }


    @Override
    public boolean supportType(CrudType type) {
        return false;
    }
}
