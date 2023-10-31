package com.wangyang.web.controller.api;

import com.wangyang.common.BaseResponse;
import com.wangyang.pojo.entity.ComponentsCategory;
import com.wangyang.service.templates.IComponentsCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/componentsCategory")
public class ComponentsCategoryController {

    @Autowired
    IComponentsCategoryService componentsCategoryService;
    @GetMapping("/addByCategoryViewName/{componentId}")
    public ComponentsCategory addByCategoryViewName(@PathVariable("componentId") Integer componentId, String viewName){
        return  componentsCategoryService.add(viewName,componentId);
    }
    @GetMapping("/delete/{id}")
    public BaseResponse delete(@PathVariable("id") Integer id){
        componentsCategoryService.delete(id);
        return BaseResponse.ok("成功删除");
    }

    @GetMapping("/deleteByComponentIdAndCategoryId/{componentId}")
    public ComponentsCategory delete(@PathVariable("componentId") Integer componentId,Integer categoryId){
        return  componentsCategoryService.delete(categoryId,componentId);
    }

    @GetMapping("/addAllCategory/{componentId}")
    public List<ComponentsCategory> addAllCategory(@PathVariable("componentId") Integer componentId){

        return  componentsCategoryService.addAllParentCategory(componentId);
    }


}
