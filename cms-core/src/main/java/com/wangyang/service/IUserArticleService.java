package com.wangyang.service;


import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;
import com.wangyang.pojo.entity.CategoryTemplate;
import com.wangyang.pojo.entity.UserArticle;

import java.util.List;

public interface IUserArticleService  extends ICrudService<UserArticle, UserArticle, BaseVo,Integer> {
    List<UserArticle> listByArticleId(Integer articleId);

    UserArticle findByUserIdAndArticleId(Integer userId,Integer articleId);
}
