package com.wangyang.service.relation;


import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.utils.ServiceUtil;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleTagsServiceImpl extends AbstractRelationServiceImpl<ArticleTags,ArticleTags, BaseVo> implements IArticleTagsService {

    @Autowired
    ITagsService tagsService;
    @Autowired
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
    public ArticleTags findByArticleIdAndRelationId(int articleId, int relationId){
        List<ArticleTags> articleTags = articleTagsRepository.findAll(new Specification<ArticleTags>() {
            @Override
            public Predicate toPredicate(Root<ArticleTags> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("articleId"),articleId),
                        criteriaBuilder.equal(root.get("relationId"),relationId)
                        ).getRestriction();
            }
        });
        if(articleTags.size()>0) return articleTags.get(0);
        return null;
    }


    @Override
    public ArticleTags save(ArticleTags articleTagsParams) {
        ArticleTags articleTags = findByArticleIdAndRelationId(articleTagsParams.getArticleId(), articleTagsParams.getRelationId());
        if(articleTags!=null){
            return articleTags;
        }
        return super.save(articleTagsParams);
    }
}
