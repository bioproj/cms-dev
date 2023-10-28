package com.wangyang.service.base;

import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.repository.base.BaseCategoryRepository;
import com.wangyang.util.FormatUtil;
import org.springframework.beans.BeanUtils;

public abstract class AbstractBaseCategoryServiceImpl <CATEGORY extends BaseCategory,CATEGORYDTO extends BaseEntity,CATEGORYVO extends BaseVo>  extends AbstractCrudService<CATEGORY,CATEGORYDTO,CATEGORYVO,Integer>
        implements IBaseCategoryService<CATEGORY,CATEGORYDTO,CATEGORYVO>{

    BaseCategoryRepository<CATEGORY> baseCategoryRepository;
    public AbstractBaseCategoryServiceImpl(BaseCategoryRepository<CATEGORY> baseCategoryRepository) {
        super(baseCategoryRepository);
        this.baseCategoryRepository=baseCategoryRepository;
    }

    @Override
    public CategoryDto covertToDto(CATEGORY category) {
        CategoryDto categoryDto = new CategoryDto();
        BeanUtils.copyProperties(category,categoryDto);
        categoryDto.setLinkPath(FormatUtil.categoryListFormat(category));
        return categoryDto;
    }
}
