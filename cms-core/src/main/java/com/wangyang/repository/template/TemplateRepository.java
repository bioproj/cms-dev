package com.wangyang.repository.template;

import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.enums.TemplateType;
import com.wangyang.common.repository.BaseRepository;

import java.util.List;

public interface TemplateRepository  extends BaseTemplateRepository<Template> {
    List<Template> findByTemplateType(TemplateType type);

    Template findByEnName(String enName);

}
