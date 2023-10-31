package com.wangyang.repository.template;

import com.wangyang.pojo.entity.ComponentsArticle;
import com.wangyang.common.repository.BaseRepository;

import java.util.List;

public interface ComponentsArticleRepository extends BaseRepository< ComponentsArticle,Integer> {

    List<ComponentsArticle> findByComponentId(Integer componentId);
    List<ComponentsArticle> findByArticleId(Integer articleId);

    ComponentsArticle findByArticleIdAndComponentId(int articleId, int componentId);
}
