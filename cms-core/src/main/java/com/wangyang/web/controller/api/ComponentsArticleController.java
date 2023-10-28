package com.wangyang.web.controller.api;

import com.wangyang.common.BaseResponse;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.service.IComponentsArticleService;
import com.wangyang.pojo.entity.ComponentsArticle;
import com.wangyang.service.base.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/componentsArticle")
public class ComponentsArticleController {

    @Autowired
    IComponentsArticleService componentsArticleService;

    @Autowired
    IContentService<Content,Content,ContentVO> contentService;

    @GetMapping("/add")
    public ComponentsArticle add(Integer articleId,Integer componentsId){
        return  contentService.addComponentsArticle(articleId,componentsId);
    }

    @GetMapping("/addByArticleViewName/{componentId}")
    public ComponentsArticle addByArticleViewName(@PathVariable("componentId") Integer componentId,String viewName){
        return  contentService.addComponentsArticle(viewName,componentId);
    }
    @GetMapping("/delete/{id}")
    public BaseResponse delete(@PathVariable("id") Integer id){
        componentsArticleService.delete(id);
        return BaseResponse.ok("成功删除");
    }

    @GetMapping("/deleteByComponentIdAndArticleId/{componentId}")
    public ComponentsArticle delete(@PathVariable("componentId") Integer componentId,Integer articleId){
        return  componentsArticleService.delete(articleId,componentId);
    }


}
