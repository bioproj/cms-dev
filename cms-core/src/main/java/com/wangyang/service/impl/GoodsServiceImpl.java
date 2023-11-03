package com.wangyang.service.impl;

import com.wangyang.common.exception.ArticleException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.MarkdownUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.Toc;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.Tags;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.entity.shop.Goods;
import com.wangyang.pojo.enums.RelationType;
import com.wangyang.pojo.vo.*;
import com.wangyang.repository.GoodsRepository;
import com.wangyang.repository.TagsRepository;
import com.wangyang.repository.base.ContentRepository;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.service.IArticleService;
import com.wangyang.service.IGoodsService;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.service.base.AbstractContentServiceImpl;
import com.wangyang.util.FormatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GoodsServiceImpl extends AbstractContentServiceImpl<Goods,Goods, GoodsVO> implements IGoodsService {
    @Autowired
    ArticleTagsRepository articleTagsRepository;
    @Autowired
    TagsRepository tagsRepository;
    GoodsRepository goodsRepository;
    @Autowired
    IUserService userService;
    public GoodsServiceImpl(GoodsRepository goodsRepository) {
        super(goodsRepository);
    }


    @Override
    public GoodsDetailVO createGoodsDetailVo(Goods goods, Set<Integer> tagIds) {
//        if(goods.getOrder()==null){
//            int count = goodsRepository.countBycategoryId(goods.getCategoryId());
//            goods.setOrder(count+1);
//        }
        if(goods.getParentId()==null){
            goods.setParentId(0);
        }
        if(goods.getDirection()==null){
            goods.setDirection("right");// 脑图方向向左
        }
        if(goods.getExpanded()== null){
            goods.setExpanded(false);// 脑图默认不展开
        }

//        article.setStatus(ArticleStatus.PUBLISHED);
//        article.setHaveHtml(true);
        goods.setTop(false);
        GoodsDetailVO goodsDetailVO = createOrUpdateGoods(goods, tagIds);
        return goodsDetailVO;

    }

    @Override
    public GoodsDetailVO updateGoodsDetailVo(Goods goods, Set<Integer> tagIds) {
//        if(goods.getOrder()==null){
//            int count = articleRepository.countBycategoryId(article.getCategoryId());
//            goods.setOrder(count+1);
//        }
        goods.setPdfPath(null);
//        article.setStatus(ArticleStatus.PUBLISHED);
//        article.setHaveHtml(true);
        goods.setUpdateDate(new Date());


        //TODO temp delete all tags and category before update
        articleTagsRepository.deleteByArticleId(goods.getId());

        GoodsDetailVO goodsDetailVO = createOrUpdateGoods(goods, tagIds);
        return goodsDetailVO;
    }
    public GoodsDetailVO createOrUpdateGoods(Goods goods, Set<Integer> tagsIds) {
        GoodsVO goodsVO = super.createOrUpdateArticle(goods, tagsIds);
        GoodsDetailVO goodsDetailVO = convert(goodsVO, tagsIds);
        return goodsDetailVO;
    }
    public GoodsDetailVO convert(GoodsVO goodsVO,Set<Integer> tagsIds) {
//        ArticleDetailVO articleDetailVo = new ArticleDetailVO();
//        BeanUtils.copyProperties(article,articleDetailVo);
//
//        //find tags

        if(goodsVO.getCategoryId()==null){
            throw  new ArticleException("文章["+goodsVO.getTitle()+"]的没有指定类别!!");
        }
//
//        User user = userService.findById(article.getUserId());
//        articleDetailVo.setUser(user);
//        Optional<Category> optionalCategory = categoryService.findOptionalById(article.getCategoryId());
//        if(optionalCategory.isPresent()){
////            throw new ObjectException("文章为名称："+article.getTitle()+" 文章为Id："+article.getId()+"分类没有找到！");
//            if(articleDetailVo.getTemplateName()==null){
//                articleDetailVo.setTemplateName(optionalCategory.get().getArticleTemplateName());
//            }
//            articleDetailVo.setCategory(categoryService.covertToVo(optionalCategory.get()));
//        }
        BaseCategoryVo category = goodsVO.getCategory();
        GoodsDetailVO goodsDetailVO = new GoodsDetailVO();
        goodsDetailVO.setCategory(category);
//        articleDetailVO.setUpdateChannelFirstName(true);
        BeanUtils.copyProperties(goodsVO,goodsDetailVO);
        // 添加标签
        if (tagsIds!=null && !CollectionUtils.isEmpty(tagsIds)) {
            // Get Article tags
            List<ArticleTags> articleTagsList = tagsIds.stream().map(tagId -> {
                ArticleTags articleTags = new ArticleTags();
                articleTags.setRelationId(tagId);
                articleTags.setRelationType(RelationType.ARTICLE);
                articleTags.setArticleId(goodsVO.getId());
                return articleTags;
            }).collect(Collectors.toList());
            //save article tags
            articleTagsRepository.saveAll(articleTagsList);
            goodsDetailVO.setTagIds(tagsIds);
            List<Tags> tags = tagsRepository.findAllById(tagsIds);
            goodsDetailVO.setTags(tags);

        }else {
            List<Tags> tags = tagsRepository.findTagsByArticleId(goodsVO.getId());
            if(!CollectionUtils.isEmpty(tags)){
                goodsDetailVO.setTags(tags);
                goodsDetailVO.setTagIds( ServiceUtil.fetchProperty(tags, Tags::getId));
            }
        }



        //添加用户
        User user = userService.findById(goodsVO.getUserId());
        goodsDetailVO.setUser(user);
//        goodsDetailVO.setCommentPath( goodsVO.getPath()+ CMSUtils.getComment()+ File.separator +goodsVO.getViewName());
        goodsDetailVO.setLinkPath( FormatUtil.articleFormat(goodsDetailVO));

        if(goodsVO.getToc()!=null){
            List<Toc> toc = MarkdownUtils.getToc(goodsVO.getToc());
            goodsDetailVO.setTocList(toc);
        }

        return goodsDetailVO;
    }


}
