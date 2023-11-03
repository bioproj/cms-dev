package com.wangyang.service;

import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.shop.Goods;
import com.wangyang.pojo.vo.ArticleVO;
import com.wangyang.pojo.vo.GoodsVO;
import com.wangyang.service.base.IContentService;


public interface IGoodsService extends IContentService<Goods,Goods, GoodsVO> {
}
