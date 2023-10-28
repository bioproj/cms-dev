package com.wangyang.interfaces;

import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.Content;

public interface IContentAop {


    void injectContent(Content article, BaseCategory category);

    void injectContent(BaseCategory category);
}
