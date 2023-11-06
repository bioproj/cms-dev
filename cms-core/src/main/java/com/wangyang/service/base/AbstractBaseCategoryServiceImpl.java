package com.wangyang.service.base;

import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.entity.ComponentsCategory;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.pojo.vo.BaseCategoryVo;
import com.wangyang.repository.template.ComponentsCategoryRepository;
import com.wangyang.repository.base.BaseCategoryRepository;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.util.FormatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractBaseCategoryServiceImpl <CATEGORY extends BaseCategory,CATEGORYDTO extends BaseEntity,CATEGORYVO extends BaseCategoryVo>  extends AbstractCrudService<CATEGORY,CATEGORYDTO,CATEGORYVO,Integer>
        implements IBaseCategoryService<CATEGORY,CATEGORYDTO,CATEGORYVO>{
    @Autowired
    ComponentsCategoryRepository componentsCategoryRepository;
    BaseCategoryRepository<CATEGORY> baseCategoryRepository;

    @Autowired
    IUserService userService;
    public AbstractBaseCategoryServiceImpl(BaseCategoryRepository<CATEGORY> baseCategoryRepository) {
        super(baseCategoryRepository);
        this.baseCategoryRepository=baseCategoryRepository;
    }
    @Override
    public  List<CategoryDto> covertToListDto(List<CATEGORY> categories) {
        List<CategoryDto> collect = categories.stream().map(category -> {
            return covertToDto(category);
        }).collect(Collectors.toList());
        return collect;
//        CategoryDto categoryDto = new CategoryDto();
//        BeanUtils.copyProperties(category,categoryDto);
//        categoryDto.setLinkPath(FormatUtil.categoryListFormat(category));
//        return categoryDto;
    }
    @Override
    public CategoryDto covertToDto(CATEGORY category) {
        CategoryDto categoryDto = new CategoryDto();
        BeanUtils.copyProperties(category,categoryDto);
        categoryDto.setLinkPath(FormatUtil.categoryListFormat(category));
        return categoryDto;
    }
    @Override
    public CATEGORYVO convertToVo(CATEGORY category){
        CATEGORYVO categoryVO =getVOInstance();
        if(category.getUserId()!=null){
            Integer userId = category.getUserId();
            User user = userService.findUserById(userId);
            categoryVO.setUser(user);
        }

        BeanUtils.copyProperties(category, categoryVO);
        categoryVO.setLinkPath(FormatUtil.categoryListFormat(category));
        categoryVO.setRecommendPath(category.getPath()+ CMSUtils.getArticleRecommendPath()+ File.separator+category.getViewName());
        categoryVO.setRecentPath(category.getPath()+CMSUtils.getArticleRecentPath()+ File.separator+category.getViewName());
        categoryVO.setFirstTitleList(category.getPath()+CMSUtils.getFirstArticleTitleList()+ File.separator+category.getViewName());
        return categoryVO;
    }

    @Override
    public List<CATEGORY> listByIdsOrderComponent(Set<Integer> categoryIds){
        if(categoryIds.size()==0){
            return Collections.emptyList();
        }
        List<CATEGORY>  categories = baseCategoryRepository.findAll(new Specification<CATEGORY>() {
            @Override
            public Predicate toPredicate(Root<CATEGORY> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(root.get("id").in(categoryIds)).getRestriction();
            }
        }, Sort.by(Sort.Direction.DESC,"categoryInComponentOrder"));
        return categories;
    }
    @Override
    public List<CATEGORYVO> listByComponentsId(int componentsId){
        List<ComponentsCategory> componentsCategories = componentsCategoryRepository.findByComponentId(componentsId);
        Set<Integer> categoryIds = ServiceUtil.fetchProperty(componentsCategories, ComponentsCategory::getCategoryId);
        if(categoryIds.size()==0){
            return Collections.emptyList();
        }
//        List<Article> articles = articleRepository.findAllById(articleIds);
        List<CATEGORY>  categories = listByIdsOrderComponent(categoryIds);

        return convertToListVo(categories);
    }

    @Override
    public CATEGORY findByViewName(String viewName){
        return baseCategoryRepository.findByViewName(viewName);
    }

    @Override
    public List<CATEGORY> listByParentId(int i) {
        return baseCategoryRepository.findAll(new Specification<CATEGORY>() {
            @Override
            public Predicate toPredicate(Root<CATEGORY> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("parentId"),i)).getRestriction();
            }
        });
    }

    @Override
    public  List<CATEGORYVO> addChildFilterRecursive(List<CATEGORYVO> domainvos){
        List<CATEGORYVO> saveCategories = new ArrayList<>();
        addChildFilterRecursive(domainvos,saveCategories);
        return saveCategories;
    }


    public  void addChildFilterRecursive(List<CATEGORYVO> domainvos,List<CATEGORYVO> saveCategories ){
        List<CATEGORYVO> filterCategories = domainvos.stream().filter(item -> (item.getIsRecursive() != null && item.getIsRecursive())).collect(Collectors.toList());
        saveCategories.addAll(filterCategories);

        for (CATEGORYVO categoryVO : domainvos){
            if(categoryVO.getChildren()!=null && categoryVO.getChildren().size()!=0){
                addChildFilterRecursive(categoryVO.getChildren(),saveCategories);
            }
        }

    }

    @Override
    public void addParentCategory(List<CATEGORYVO> categoryVOS, Integer parentId){
        if(parentId==0){
            return;
        }
        CATEGORY category = findById(parentId);
        categoryVOS.add(0,convertToVo(category));
        if(category.getParentId()!=0){
            addParentCategory(categoryVOS,category.getParentId());
        }

    }
    @Override
    public void addTemplatePath(Map<String, Object> map, List<CATEGORYVO> parentCategories , List<Template> templates) {
        for (Template template : templates){
            if(parentCategories!=null){
                CATEGORYVO categoryVO;
                if(template.getParentOrder()!=null && template.getParentOrder() > -1){
                    categoryVO = parentCategories.get(template.getParentOrder());
                }else {
                    categoryVO = parentCategories.get(parentCategories.size() - 1);
                }
                map.put(template.getEnName(),categoryVO.getPath()+File.separator+template.getEnName()+File.separator+ categoryVO.getViewName() );

            }
        }
    }
}
