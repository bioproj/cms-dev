package com.wangyang.repository.template;

import com.wangyang.pojo.entity.TemplateChild;
import com.wangyang.common.repository.BaseRepository;

import java.util.List;

public interface TemplateChildRepository extends BaseRepository<TemplateChild,Integer> {
    TemplateChild findByTemplateIdAndTemplateChildId(int templateId, int templateChildId);
    List<TemplateChild> findByTemplateId(int templateId);
}
