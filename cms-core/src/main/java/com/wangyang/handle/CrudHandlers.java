package com.wangyang.handle;

import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.common.enums.CrudType;

import com.wangyang.common.service.ICrudService;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;

@Component
public class CrudHandlers {
    private final Collection<ICrudService> fileTermMappingHandlers= new LinkedList<>();
    private CrudHandlers(ApplicationContext applicationContext) {
        // Add all file handler
        addTermMappingHandlers(applicationContext.getBeansOfType(ICrudService.class).values());
    }
    private CrudHandlers addTermMappingHandlers(@Nullable Collection<ICrudService> termMappingHandlers) {
        if (!CollectionUtils.isEmpty(termMappingHandlers)) {
            this.fileTermMappingHandlers.addAll(termMappingHandlers);
        }
        return this;
    }

    public Page<? extends BaseEntity> pageBy(CrudType crudType, Pageable pageable, String keywords) {
        Assert.notNull(crudType, "crudType  must not be null");

        for (ICrudService crudService : fileTermMappingHandlers) {
            if (crudService.supportType(crudType)) {
                return crudService.pageBy(pageable);
            }
        }
        throw new ObjectException("不能找到Crud！！");
    }

    public ICrudService getCrudService(CrudType crudEnum){
        for (ICrudService crudService : fileTermMappingHandlers) {
            if (crudService.supportType(crudEnum)) {
                return crudService;
            }
        }
        throw new ObjectException("不能找到Crud！！");
    }
}
