package com.wangyang.service.base;

import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.ComponentsCategory;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.pojo.vo.BaseCategoryVo;
import com.wangyang.pojo.vo.CategoryVO;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
}
