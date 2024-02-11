package com.wangyang.pojo.vo;

import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.entity.shop.Goods;
import lombok.Data;

@Data
public class GoodsVO extends ContentVO {
    private User user;
    private Integer userId;
    private Double cost;
    private String goodsImg;
    private String goodsQr;
    private String costUrl;
}
