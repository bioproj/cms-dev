package com.wangyang.pojo.vo;

import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.base.Content;
import lombok.Data;

@Data
public class ContentDetailVO{
    private Category category;
    private Content content;
}
