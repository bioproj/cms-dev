package com.wangyang.service.relation;


import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.entity.Tags;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.support.ForceDirectedGraph;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.service.ITagsService;
import com.wangyang.service.base.AbstractRelationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleTagsServiceImpl extends AbstractRelationServiceImpl<ArticleTags,ArticleTags, BaseVo> implements IArticleTagsService {

    @Autowired
    ITagsService tagsService;
    ArticleTagsRepository articleTagsRepository;
    public ArticleTagsServiceImpl(ArticleTagsRepository articleTagsRepository) {
        super(articleTagsRepository);
        this.articleTagsRepository = articleTagsRepository;
    }

    @Override
    public ForceDirectedGraph graph(List<ContentVO> contents) {
        ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph();
        contents.forEach(item->{
            forceDirectedGraph.addNodes(item.getId(),item.getTitle(),item.getLinkPath());
        });

        Set<Integer> ids = ServiceUtil.fetchProperty(contents, ContentVO::getId);
        List<ArticleTags> articleTags = articleTagsRepository.findAllByArticleIdIn(ids);
        articleTags.forEach(item->{
            forceDirectedGraph.addEdges(item.getRelationId(),item.getArticleId(),60,2);
        });

        Set<Integer> rIds = ServiceUtil.fetchProperty(articleTags, ArticleTags::getRelationId);
        List<Tags> tags = tagsService.listByIds(rIds);
        tags.forEach(item->{
            forceDirectedGraph.addNodes(item.getId(),item.getName(),"/articleList?tagsId="+item.getId());
        });

        return forceDirectedGraph;
    }


    @Override
    public ForceDirectedGraph graph(List<ContentVO> contents, int num) {
        ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph();

        List<ContentVO> firstContent = contents.subList(0, Math.min(contents.size(), num));
        firstContent.forEach(item->{
            forceDirectedGraph.addNodes(item.getId(),item.getTitle(),item.getLinkPath());
        });

        Set<Integer> ids = ServiceUtil.fetchProperty(firstContent, ContentVO::getId);
        List<ArticleTags> articleTags = articleTagsRepository.findAllByArticleIdIn(ids);

        Set<Integer> rIds = ServiceUtil.fetchProperty(articleTags, ArticleTags::getRelationId);

        List<Tags> tags = tagsService.listByIds(rIds);
        tags.forEach(item->{
            forceDirectedGraph.addNodes(item.getId(),item.getName(),"/articleList?tagsId="+item.getId());
        });
        List<ArticleTags> edges = articleTagsRepository.findAllByRelationIdIn(rIds);

        edges.forEach(item->{
            forceDirectedGraph.addEdges(item.getRelationId(),item.getArticleId(),60,2);
        });

        Set<Integer> articleIds = ServiceUtil.fetchProperty(edges, ArticleTags::getArticleId);
        Set<Integer> articleAddIds = ServiceUtil.fetchProperty(firstContent, ContentVO::getId);

        List<ContentVO> nodes = contents.stream().filter(item -> articleIds.contains(item.getId()) &&  !articleAddIds.contains(item.getId())).collect(Collectors.toList());


        nodes.forEach(item->{
            forceDirectedGraph.addNodes(item.getId(),item.getTitle(),item.getLinkPath());
        });




        return forceDirectedGraph;
    }
}
