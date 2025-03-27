package com.wangyang.service;

import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.Collection;
import com.wangyang.pojo.entity.Task;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.pojo.vo.CollectionVO;
import com.wangyang.service.base.IBaseCategoryService;

import java.util.List;

public interface ICollectionService extends IBaseCategoryService<Collection,Collection, CollectionVO> {
    List<CollectionVO> listTree();

    void generateListHtml();
}
