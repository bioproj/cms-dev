package com.wangyang.service.impl;

import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.shop.Goods;
import com.wangyang.pojo.vo.ArticleVO;
import com.wangyang.pojo.vo.GoodsVO;
import com.wangyang.repository.GoodsRepository;
import com.wangyang.repository.base.ContentRepository;
import com.wangyang.service.IArticleService;
import com.wangyang.service.IGoodsService;
import com.wangyang.service.base.AbstractContentServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class GoodsServiceImpl extends AbstractContentServiceImpl<Goods,Goods, GoodsVO> implements IGoodsService {


    GoodsRepository goodsRepository;
    public GoodsServiceImpl(GoodsRepository goodsRepository) {
        super(goodsRepository);
    }
}
