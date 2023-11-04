package com.wangyang.service;

import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.shop.Goods;
import com.wangyang.pojo.vo.ArticleVO;
import com.wangyang.pojo.vo.GoodsDetailVO;
import com.wangyang.pojo.vo.GoodsVO;
import com.wangyang.service.base.IContentService;

import java.util.Set;


public interface IGoodsService extends IContentService<Goods,GoodsDetailVO, GoodsVO> {
    GoodsDetailVO createGoodsDetailVo(Goods goods, Set<Integer> tagIds);

    GoodsDetailVO updateGoodsDetailVo(Goods goods, Set<Integer> tagIds);

    GoodsDetailVO convert(Goods goods);
}
