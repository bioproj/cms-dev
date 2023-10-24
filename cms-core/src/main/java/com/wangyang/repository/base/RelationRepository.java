package com.wangyang.repository.base;

import com.wangyang.common.repository.BaseRepository;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.entity.base.Relation;
import com.wangyang.pojo.entity.relation.ArticleTags;

import java.util.Collection;
import java.util.List;

public interface RelationRepository<T extends Relation>  extends BaseRepository<T, Integer> {


    List<T> findAllByArticleIdIn(Collection<Integer> articleIds);
    List<T> findAllByRelationIdIn(Collection<Integer> relationIds);
    List<T> findByArticleId(int articleId);
    List<T> findByRelationId(int relationId);

    List<T> deleteByArticleId(int id);
}
