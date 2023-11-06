package com.wangyang.service.base;

import com.wangyang.common.service.ICrudService;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.vo.CategoryVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface
IBaseCategoryService <CATEGORY extends BaseCategory,CATEGORYDTO,CATEGORYVO>  extends ICrudService<CATEGORY,CATEGORYDTO,CATEGORYVO,Integer> {
    List<CategoryDto> covertToListDto(List<CATEGORY> categories);

    CategoryDto covertToDto(CATEGORY category);

    List<CATEGORY> listByIdsOrderComponent(Set<Integer> categoryIds);

    List<CATEGORYVO> listByComponentsId(int componentsId);

    CATEGORY findByViewName(String viewName);

    List<CATEGORY> listByParentId(int i);

    List<CATEGORYVO> addChildFilterRecursive(List<CATEGORYVO> domainvos);

    void addParentCategory(List<CATEGORYVO> categoryVOS, Integer parentId);

    void addTemplatePath(Map<String, Object> map, List< CATEGORYVO> parentCategories , List<Template> templates);

}
