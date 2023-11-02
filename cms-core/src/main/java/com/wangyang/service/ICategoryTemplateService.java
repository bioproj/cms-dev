package com.wangyang.service;


import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;
import com.wangyang.pojo.entity.CategoryTags;
import com.wangyang.pojo.entity.CategoryTemplate;

import java.util.List;

public interface ICategoryTemplateService  extends ICrudService<CategoryTemplate, CategoryTemplate, BaseVo,Integer> {

    CategoryTemplate findByCategoryIdAndTemplateType(int categoryId);

    List<CategoryTemplate> listByCategoryId(Integer id);
}
