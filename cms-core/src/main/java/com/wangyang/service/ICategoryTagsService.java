package com.wangyang.service;

import com.wangyang.pojo.entity.CategoryTags;
import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;
import java.util.Set;

public interface ICategoryTagsService extends ICrudService<CategoryTags, BaseEntity, BaseVo,Integer> {
    List<CategoryTags> listByTagIds(Set<Integer> ids);
}
