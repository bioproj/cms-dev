package com.wangyang.web.controller.api;

import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.shop.Goods;
import com.wangyang.pojo.params.ArticleParams;
import com.wangyang.pojo.vo.ArticleDetailVO;
import com.wangyang.pojo.vo.ArticleVO;
import com.wangyang.pojo.vo.GoodsVO;
import com.wangyang.service.IGoodsService;
import com.wangyang.util.AuthorizationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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

}
