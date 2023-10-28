package com.wangyang.service.impl;

import com.wangyang.common.pojo.BaseVo;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.repository.base.BaseCategoryRepository;
import com.wangyang.service.base.AbstractBaseCategoryServiceImpl;
import com.wangyang.service.base.AbstractContentServiceImpl;
import com.wangyang.service.base.IBaseCategoryService;
import com.wangyang.service.base.IContentService;
import com.wangyang.util.FormatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BaseCategoryServiceImpl extends AbstractBaseCategoryServiceImpl<BaseCategory,BaseCategory, BaseVo> implements IBaseCategoryService<BaseCategory,BaseCategory, BaseVo> {


    private BaseCategoryRepository<BaseCategory> baseCategoryRepository;
    public BaseCategoryServiceImpl(BaseCategoryRepository<BaseCategory> baseCategoryRepository) {
        super(baseCategoryRepository);
        this.baseCategoryRepository =baseCategoryRepository;
    }


}
