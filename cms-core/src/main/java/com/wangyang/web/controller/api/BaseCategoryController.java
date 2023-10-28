package com.wangyang.web.controller.api;

import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.vo.BaseCategoryVo;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.service.base.IBaseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/baseCategory")
@Slf4j
public class BaseCategoryController {
    @Autowired
    IBaseCategoryService<BaseCategory, BaseCategory, BaseCategoryVo> baseCategoryService;


    @GetMapping("/listByComponentsId/{componentsId}")
    public List<BaseCategoryVo> listByComponentsId(@PathVariable("componentsId") Integer componentsId){
        return  baseCategoryService.listByComponentsId(componentsId);
    }
}
