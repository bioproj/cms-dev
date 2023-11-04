package com.wangyang.service.relation;


import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.CategoryTags;
import com.wangyang.pojo.entity.Tags;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.support.ForceDirectedGraph;
import com.wangyang.pojo.vo.BaseCategoryVo;
import com.wangyang.pojo.vo.ContentDetailVO;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.service.ICategoryTagsService;
import com.wangyang.service.ITagsService;
import com.wangyang.service.base.AbstractRelationServiceImpl;
import com.wangyang.service.base.IBaseCategoryService;
import com.wangyang.service.base.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleTagsServiceImpl extends AbstractRelationServiceImpl<ArticleTags,ArticleTags, BaseVo> implements IArticleTagsService {

    @Autowired
    ITagsService tagsService;
    ArticleTagsRepository articleTagsRepository;
    @Autowired
    ICategoryTagsService categoryTagsService;
    @Autowired
    IContentService<Content, ContentDetailVO,ContentVO> contentService;
    @Autowired
    IBaseCategoryService<BaseCategory,BaseCategory, BaseCategoryVo> baseCategoryService;

    public ArticleTagsServiceImpl(ArticleTagsRepository articleTagsRepository) {
        super(articleTagsRepository);
        this.articleTagsRepository = articleTagsRepository;
    }

    @Override
    public ForceDirectedGraph graph(List<ContentVO> contents) {
        ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph();
        contents.forEach(item->{
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getTitle(),item.getLinkPath());
        });

        Set<Integer> ids = ServiceUtil.fetchProperty(contents, ContentVO::getId);
        List<ArticleTags> articleTags = articleTagsRepository.findAllByArticleIdIn(ids);
        articleTags.forEach(item->{
            forceDirectedGraph.addEdges(String.valueOf(item.getRelationId()),String.valueOf(item.getArticleId()),60,2);
        });

        Set<Integer> rIds = ServiceUtil.fetchProperty(articleTags, ArticleTags::getRelationId);
        List<Tags> tags = tagsService.listByIds(rIds);
        tags.forEach(item->{
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getName(),"/articleList?tagsId="+item.getId());
        });

        return forceDirectedGraph;
    }

    @Override
    public ForceDirectedGraph graphTags(List<? extends ContentVO> firstContent) {
        ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph();


        int r =8+firstContent.size();
        for (ContentVO item:firstContent){
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getTitle(),item.getLinkPath(),r);
            r=r-1;
        }

        Set<Integer> ids = ServiceUtil.fetchProperty(firstContent, ContentVO::getId);
        List<ArticleTags> articleTags = articleTagsRepository.findAllByArticleIdIn(ids);

        Set<Integer> rIds = ServiceUtil.fetchProperty(articleTags, ArticleTags::getRelationId);

        List<Tags> tags = tagsService.listByIds(rIds);
        tags.forEach(item->{
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getName(),"/articleList?tagsId="+item.getId(),8);
        });
        List<ArticleTags> edges = articleTagsRepository.findAllByRelationIdIn(rIds);

        edges.forEach(item->{
            forceDirectedGraph.addEdges(String.valueOf(item.getRelationId()),String.valueOf(item.getArticleId()),60,2);
        });

        Set<Integer> articleIds = ServiceUtil.fetchProperty(edges, ArticleTags::getArticleId);
        Set<Integer> articleAddIds = ServiceUtil.fetchProperty(firstContent, ContentVO::getId);

        List<Content> contentsDb = contentService.listByIds(articleIds);
        List<ContentVO> contentVOS = contentService.convertToListTagVo(contentsDb);
        List<ContentVO> nodes = contentVOS.stream().filter(item -> articleIds.contains(item.getId()) &&  !articleAddIds.contains(item.getId())).collect(Collectors.toList());


        nodes.forEach(item->{
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getTitle(),item.getLinkPath(),8);
        });




        return forceDirectedGraph;
    }

    @Override
    public ForceDirectedGraph graphTagsCategory(List<? extends ContentVO> firstContent) {
        ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph();
        Set<BaseCategoryVo> firstCategory = ServiceUtil.fetchProperty(firstContent, ContentVO::getCategory);



        int r =8+firstContent.size();
        for (ContentVO item:firstContent){
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getTitle(),item.getLinkPath(),r);
            if(item.getCategory()!=null){
                forceDirectedGraph.addEdges(String.valueOf(item.getId()),"c-"+String.valueOf(item.getCategoryId()),300,2);

            }
//            forceDirectedGraph.addEdges(item.getId(),item.getCategoryId(),60,2);

            r=r-1;
        }


        Set<Integer> ids = ServiceUtil.fetchProperty(firstContent, ContentVO::getId);
        List<ArticleTags> articleTags = articleTagsRepository.findAllByArticleIdIn(ids);

        Set<Integer> rIds = ServiceUtil.fetchProperty(articleTags, ArticleTags::getRelationId);

        List<Tags> tags = tagsService.listByIds(rIds);
        tags.forEach(item->{
            forceDirectedGraph.addNodes("t-"+String.valueOf(item.getId()),item.getName(),"/articleList?tagsId="+item.getId(),8);
        });
        List<ArticleTags> edges = articleTagsRepository.findAllByRelationIdIn(rIds);

        edges.forEach(item->{
            forceDirectedGraph.addEdges("t-"+String.valueOf(item.getRelationId()),String.valueOf(item.getArticleId()),300,2);
        });

        Set<Integer> articleIds = ServiceUtil.fetchProperty(edges, ArticleTags::getArticleId);
        Set<Integer> articleAddIds = ServiceUtil.fetchProperty(firstContent, ContentVO::getId);

        List<Content> contentsDb = contentService.listByIds(articleIds);
        List<ContentVO> contentVOS = contentService.convertToListCategoryVo(contentsDb);



        List<ContentVO> nodes = contentVOS.stream().filter(item -> articleIds.contains(item.getId()) &&  !articleAddIds.contains(item.getId())).collect(Collectors.toList());


        nodes.forEach(item->{
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getTitle(),item.getLinkPath(),8);
            if(item.getCategory()!=null){
                forceDirectedGraph.addEdges(String.valueOf(item.getId()),"c-"+String.valueOf(item.getCategoryId()),300,2);

            }
//            forceDirectedGraph.addEdges(item.getId(),item.getCategoryId(),60,2);
        });


        List<CategoryTags> categoryTags = categoryTagsService.listByTagIds(rIds);
        if(categoryTags.size()!=0){
            Set<Integer> categoryIds = ServiceUtil.fetchProperty(categoryTags, CategoryTags::getCategoryId);
            List<BaseCategory> categories = baseCategoryService.listByIds(categoryIds);
            List<BaseCategoryVo> categoryDtoList = baseCategoryService.convertToListVo(categories);
            firstCategory.addAll(categoryDtoList);
            categoryTags.forEach(item->{
                forceDirectedGraph.addEdges("t-"+String.valueOf(item.getTagsId()),"c-"+String.valueOf(item.getCategoryId()),300,2);
            });
        }




        Set<BaseCategoryVo> otherCategory = ServiceUtil.fetchProperty(contentVOS, ContentVO::getCategory);
        firstCategory.addAll(otherCategory);

        firstCategory.forEach(item->{
            if(item!=null){
                forceDirectedGraph.addNodes("c-"+String.valueOf(item.getId()),item.getName(),item.getLinkPath(),8);
            }
        });





        return forceDirectedGraph;
    }

    @Override
    public ForceDirectedGraph graph(List<? extends ContentVO> contents, int num) {
        ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph();

        List<? extends ContentVO> firstContent = contents.subList(0, Math.min(contents.size(), num));

        int r =8+num;
        for (ContentVO item:firstContent){
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getTitle(),item.getLinkPath(),r);
            r=r-1;
        }

        Set<Integer> ids = ServiceUtil.fetchProperty(firstContent, ContentVO::getId);
        List<ArticleTags> articleTags = articleTagsRepository.findAllByArticleIdIn(ids);

        Set<Integer> rIds = ServiceUtil.fetchProperty(articleTags, ArticleTags::getRelationId);

        List<Tags> tags = tagsService.listByIds(rIds);
        tags.forEach(item->{
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getName(),"/articleList?tagsId="+item.getId(),8);
        });
        List<ArticleTags> edges = articleTagsRepository.findAllByRelationIdIn(rIds);

        edges.forEach(item->{
            forceDirectedGraph.addEdges(String.valueOf(item.getRelationId()),String.valueOf(item.getArticleId()),60,2);
        });

        Set<Integer> articleIds = ServiceUtil.fetchProperty(edges, ArticleTags::getArticleId);
        Set<Integer> articleAddIds = ServiceUtil.fetchProperty(firstContent, ContentVO::getId);

        List<Content> contentsDb = contentService.listByIds(articleIds);
        List<ContentVO> contentVOS = contentService.convertToListTagVo(contentsDb);
        List<ContentVO> nodes = contentVOS.stream().filter(item -> articleIds.contains(item.getId()) &&  !articleAddIds.contains(item.getId())).collect(Collectors.toList());


        nodes.forEach(item->{
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getTitle(),item.getLinkPath(),8);
        });




        return forceDirectedGraph;
    }
}
