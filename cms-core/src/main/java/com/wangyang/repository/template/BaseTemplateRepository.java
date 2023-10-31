package com.wangyang.repository.template;

import com.wangyang.common.repository.BaseRepository;
import com.wangyang.pojo.entity.base.BaseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseTemplateRepository<T extends BaseTemplate> extends BaseRepository<T,Integer> {
}
