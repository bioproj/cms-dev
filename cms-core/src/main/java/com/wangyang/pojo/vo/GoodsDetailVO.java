package com.wangyang.pojo.vo;

import com.wangyang.pojo.authorize.User;
import lombok.Data;

@Data
public class GoodsDetailVO extends ContentDetailVO {
    private String css;
    private String js;
    private String bilibili;
    private User user;
    private Double cost;
    private String goodsImg;
    private String goodsQr;
}
