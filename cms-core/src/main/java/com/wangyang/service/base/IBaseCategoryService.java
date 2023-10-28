package com.wangyang.service.base;

import com.wangyang.common.service.ICrudService;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.entity.base.BaseCategory;

import java.util.List;
import java.util.Set;

public interface
IBaseCategoryService <CATEGORY extends BaseCategory,CATEGORYDTO,CATEGORYVO>  extends ICrudService<CATEGORY,CATEGORYDTO,CATEGORYVO,Integer> {
    List<CategoryDto> covertToListDto(List<CATEGORY> categories);

    CategoryDto covertToDto(CATEGORY category);

    List<CATEGORY> listByIdsOrderComponent(Set<Integer> categoryIds);

    List<CATEGORYVO> listByComponentsId(int componentsId);

    CATEGORY findByViewName(String viewName);

    List<CATEGORY> listByParentId(int i);
}
