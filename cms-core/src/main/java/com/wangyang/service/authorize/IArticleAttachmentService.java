package com.wangyang.service.authorize;

import com.wangyang.pojo.entity.ArticleAttachment;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;

public interface IArticleAttachmentService extends ICrudService<ArticleAttachment,ArticleAttachment, BaseVo, Integer> {

    List<ArticleAttachment> findByTemplateId(Integer id);
}
