package com.wangyang.pojo.vo;

import com.alibaba.fastjson.JSON;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.SheetDto;
import lombok.Data;

@Data
public class SheetVo extends ContentVO {
    private String css;
    private User user;
    private Integer userId;
    private String js;
//    private String cssContent;
//
//    private String jsContent;
    private Integer categoryId;
    private String cssClass;
    private String picPath;
    private String picThumbPath;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
