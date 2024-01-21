package com.wangyang.service.relation;

import com.wangyang.common.pojo.BaseVo;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.support.ForceDirectedGraph;
import com.wangyang.pojo.vo.ArticleVO;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.service.base.IContentService;
import com.wangyang.service.base.IRelationService;

import java.util.List;

public interface IArticleTagsService  extends IRelationService<ArticleTags,ArticleTags, BaseVo> {

    ForceDirectedGraph graph(List<ContentVO> contents);

    ForceDirectedGraph graphTags(List<? extends ContentVO> firstContent);

    ForceDirectedGraph graphTagsCategory(List<? extends ContentVO> firstContent);

    ForceDirectedGraph graph(List<? extends ContentVO> contents, int num);

    ArticleTags findByArticleIdAndRelationId(int articleId, int relationId);
}
