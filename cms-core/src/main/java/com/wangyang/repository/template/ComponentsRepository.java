package com.wangyang.repository.template;

import com.wangyang.pojo.entity.Components;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.pojo.entity.Template;

public interface ComponentsRepository  extends BaseTemplateRepository<Components> {

    Components findByViewName(String viewName);
    Components findByEnName(String enName);
}
