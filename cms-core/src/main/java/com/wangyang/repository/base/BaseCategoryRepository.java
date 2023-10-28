package com.wangyang.repository.base;

import com.wangyang.common.repository.BaseRepository;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.base.BaseCategory;
import org.springframework.data.repository.NoRepositoryBean;


public interface BaseCategoryRepository<T extends BaseCategory>  extends BaseRepository<T,Integer> {
    T findByViewName(String viewName);
}
