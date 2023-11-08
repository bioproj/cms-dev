package com.wangyang.pojo.vo;

import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.base.Content;
import lombok.Data;

import java.util.List;

@Data
public class ContentDetailVO extends ContentVO{
//    private Category category;
//    private Content content;
    private String css;
    private String js;

//    private User user;
}
