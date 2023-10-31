package com.wangyang.repository.template;

import com.wangyang.pojo.entity.ComponentsCategory;
import com.wangyang.common.repository.BaseRepository;

import java.util.List;

public interface ComponentsCategoryRepository extends BaseRepository<ComponentsCategory,Integer> {

    List<ComponentsCategory> findByComponentId(Integer componentId);
    List<ComponentsCategory> findByCategoryId(Integer categoryId);

    ComponentsCategory findByCategoryIdAndComponentId(int categoryId, int componentId);
}
