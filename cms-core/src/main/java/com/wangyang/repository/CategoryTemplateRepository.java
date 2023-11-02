package com.wangyang.repository;


import com.wangyang.common.repository.BaseRepository;
import com.wangyang.pojo.entity.CategoryTags;
import com.wangyang.pojo.entity.CategoryTemplate;
import com.wangyang.pojo.entity.ComponentsArticle;
import com.wangyang.pojo.enums.TemplateType;

import java.util.List;


public interface CategoryTemplateRepository  extends BaseRepository<CategoryTemplate,Integer> {

    List<CategoryTemplate> deleteByCategoryId(int id);
    CategoryTemplate findByCategoryIdAndTemplateId(int categoryId, int templateId);
    CategoryTemplate findByCategoryIdAndTemplateType(int categoryId, TemplateType templateType);

}
