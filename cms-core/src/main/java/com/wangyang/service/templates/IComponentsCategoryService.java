package com.wangyang.service.templates;

import com.wangyang.pojo.entity.ComponentsCategory;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;
import java.util.Set;

public interface IComponentsCategoryService extends ICrudService<ComponentsCategory, ComponentsCategory, BaseVo,Integer> {
    List<ComponentsCategory> findByCategoryId(Integer categoryId);

    ComponentsCategory add(String viewName, int componentsId);
    void delete(int id);

    ComponentsCategory delete(Integer categoryId, Integer componentId);

    List<ComponentsCategory> findByCategoryId(Set<Integer> categoryIds);

    List<ComponentsCategory> addAllParentCategory(Integer componentId);
}
