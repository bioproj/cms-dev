package com.wangyang.web.controller.api;

import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.FileUtils;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.shop.Goods;
import com.wangyang.pojo.enums.ArticleStatus;
import com.wangyang.pojo.params.ArticleParams;
import com.wangyang.pojo.params.GoodsParams;
import com.wangyang.pojo.vo.ArticleDetailVO;
import com.wangyang.pojo.vo.ArticleVO;
import com.wangyang.pojo.vo.GoodsDetailVO;
import com.wangyang.pojo.vo.GoodsVO;
import com.wangyang.service.ICategoryService;
import com.wangyang.service.IGoodsService;
import com.wangyang.service.IHtmlService;
import com.wangyang.util.AuthorizationUtil;
import com.wangyang.util.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/goods")
//@CrossOrigin
@Slf4j
public class GoodsController {

    @Autowired
    IGoodsService goodsService;

    @Autowired
    IHtmlService htmlService;

    @Autowired
    ICategoryService categoryService;

    @GetMapping
    public Page<? extends GoodsVO> articleList(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable,
                                               @RequestParam(value = "more", defaultValue = "true") Boolean more,
                                               Goods goods, String keyword){
        Page<Goods> goodsPage = goodsService.pageBy(pageable,goods,keyword,new HashSet<>( Arrays.asList("title")));
//        if(more){
//
//        }

        return goodsService.convertToPageVo(goodsPage);
//        return articleService.convertToSimple(articles);
    }

//    @PostMapping
//    public ArticleDetailVO createArticleDetailVO(@RequestBody @Valid ArticleParams articleParams, HttpServletRequest request){
//        int userId = AuthorizationUtil.getUserId(request);
//        Article article = new Article();
//        BeanUtils.copyProperties(articleParams,article,getNullPropertyNames(articleParams));
//        article.setUserId(userId);
//        ArticleDetailVO articleDetailVO = articleService.createArticleDetailVo(article, articleParams.getTagIds());
//        articleDetailVO.setIsPublisher(true);
//        htmlService.conventHtml(articleDetailVO);
//        return articleDetailVO;
//    }
    @PostMapping
    public GoodsDetailVO createArticleDetailVO(@RequestBody @Valid GoodsParams goodsParams, HttpServletRequest request){
        int userId = AuthorizationUtil.getUserId(request);
        Goods goods = new Goods();
        BeanUtils.copyProperties(goodsParams,goods, CMSUtils.getNullPropertyNames(goodsParams));
        goods.setUserId(userId);
        GoodsDetailVO goodsDetailVO = goodsService.createGoodsDetailVo(goods, goodsParams.getTagIds());
        goodsDetailVO.setIsPublisher(true);
        htmlService.conventHtml(goodsDetailVO);
        return goodsDetailVO;
    }
    @PostMapping("/update/{articleId}")
    public GoodsDetailVO updateArticleDetailVO(@Valid @RequestBody ArticleParams articleParams,
                                                 @PathVariable("articleId") Integer articleId,HttpServletRequest request){
        int userId = AuthorizationUtil.getUserId(request);
        Goods goods = goodsService.findById(articleId);
//        checkUser(userId,article);

        Integer  oldCategoryId = goods.getCategoryId();

        BeanUtils.copyProperties(articleParams,goods,CMSUtils.getNullPropertyNames(articleParams));



        GoodsDetailVO goodsDetailVO = goodsService.updateGoodsDetailVo( goods, articleParams.getTagIds());
        //有可能更新文章的视图名称
//        TemplateUtil.deleteTemplateHtml(article.getViewName(),article.getPath());

        //更新文章分类, 还需要重新生成老的分类
        if(!articleParams.getCategoryId().equals(oldCategoryId) && oldCategoryId!=null){
            Category oldCategory = categoryService.findById(oldCategoryId);
//            goodsDetailVO.setOldCategory(oldCategory);
            htmlService.convertArticleListBy(oldCategory);
        }
//        if(articleDetailVO.getHaveHtml()){
//
////            producerService.sendMessage(articleDetailVO);
//        }
        goodsDetailVO.setIsPublisher(true);
        htmlService.conventHtml(goodsDetailVO);
        log.info(goods.getTitle()+"--->更新成功！");
        return goodsDetailVO;
    }
    @GetMapping("/delete/{id}")
    public Goods delete(@PathVariable("id") Integer id){
        Goods goods = goodsService.delBy(id);
        //删除文章
        TemplateUtil.deleteTemplateHtml(goods.getViewName(),goods.getPath());
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        if(goods.getStatus().equals(ArticleStatus.PUBLISHED)||goods.getStatus().equals(ArticleStatus.MODIFY)){
            Category category = categoryService.findById(goods.getCategoryId());
            //重新生成文章列表
            htmlService.convertArticleListBy(category);
            // 删除分页的文章列表
//            FileUtils.removeCategoryPageTemp(category);
//            FileUtils.remove(CmsConst.WORK_DIR+"/html/articleList/queryTemp");
            if(goods.getTop()){
                htmlService.articleTopListByCategoryId(category.getId());
            }
        }

        return  goods;
    }

    @GetMapping("/generateHtml/{id}")
    public GoodsDetailVO generateHtml(@PathVariable("id") Integer id){

//        TestStatic.test();
        Goods goods = goodsService.findById(id);
        GoodsDetailVO goodsDetailVO = goodsService.convert(goods);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        htmlService.conventHtml(goodsDetailVO);
        return goodsDetailVO;
    }



}
