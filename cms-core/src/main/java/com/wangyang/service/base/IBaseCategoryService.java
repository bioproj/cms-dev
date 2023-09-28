package com.wangyang.service.base;

import com.wangyang.common.service.ICrudService;
import com.wangyang.pojo.entity.base.BaseCategory;

public interface
IBaseCategoryService <CATEGORY extends BaseCategory,CATEGORYDTO,CATEGORYVO>  extends ICrudService<CATEGORY,CATEGORYDTO,CATEGORYVO,Integer> {
}
