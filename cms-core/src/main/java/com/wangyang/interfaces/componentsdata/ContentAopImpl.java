package com.wangyang.interfaces.componentsdata;


import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.interfaces.IContentAop;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.vo.BaseCategoryVo;
import com.wangyang.repository.CategoryTagsRepository;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.service.*;
import com.wangyang.service.base.IBaseCategoryService;
import com.wangyang.service.templates.IComponentsArticleService;
import com.wangyang.service.templates.IComponentsCategoryService;
import com.wangyang.service.templates.IComponentsService;
import com.wangyang.service.templates.ITemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ContentAopImpl implements IContentAop {

    @Autowired
    @Lazy
    IHtmlService htmlService;
    @Autowired
    @Lazy
    IComponentsArticleService componentsArticleService;

    @Autowired
    @Lazy
    ICategoryService categoryService;

    @Autowired
    @Lazy
    IComponentsCategoryService componentsCategoryService;

    @Autowired
    @Lazy
    ICategoryTagsService categoryTagsService;

    @Autowired
    @Lazy
    ArticleTagsRepository articleTagsRepository;

    @Autowired
    @Lazy
    ITemplateService templateService;
    @Autowired
    @Lazy
    CategoryTagsRepository categoryTagsRepository;
    @Autowired
    @Lazy
    IBaseCategoryService<BaseCategory,BaseCategory, BaseCategoryVo> baseCategoryService;
    @Autowired
    @Lazy
    IComponentsService componentsService;

    public void generateRecommendArticle(Integer articleId){
        List<ArticleTags> articleTags = articleTagsRepository.findByArticleId(articleId);
        if(articleTags.size()==0){
            return;
        }
        Set<Integer> tagIds = ServiceUtil.fetchProperty(articleTags, ArticleTags::getRelationId);
        List<CategoryTags> categoryTags = categoryTagsService.listByTagIds(tagIds);
        if(categoryTags.size()==0){
            return;
        }

        Set<Integer> categoryIds = ServiceUtil.fetchProperty(categoryTags, CategoryTags::getCategoryId);

        List<Category> categories = categoryService.findAllById(categoryIds);

        htmlService.generateRecommendArticle(categories);
    }

    @Override
    @Async
    public void injectContent(Content article, BaseCategory category) {
        generateRecommendArticle(article.getId());
        htmlService.generateComponentsByCategory(category.getId(),category.getParentId());
        htmlService.generateComponentsByArticle(article.getId());
        deleteTemp(category.getName(),category.getPath(),category.getViewName(),category.getParentId());

    }

    @Async
    public void deleteTemp(String title,String path,String viewName,Integer parentId){




        File dir = new File(CmsConst.WORK_DIR+File.separator+ path);
        File[] files = dir.listFiles();
        for(File file : files){
            String name = file.getName();
            Pattern pattern = Pattern.compile(viewName+"-(.*).html");
            Matcher matcher = pattern.matcher(name);
            if(matcher.find()){
                file.delete();
                log.info("删除分类[{}]文件[{}]",title,file.getAbsolutePath());
            }
        }
        if(parentId!=0){
            BaseCategory parentCategory = baseCategoryService.findById(parentId);
            deleteTemp(parentCategory);
        }
//        FileUtils.remove(CmsConst.WORK_DIR+File.separator+ CMSUtils.getCategoryPath()+category.getViewName()+"-");
        //移除临时文章分类
//        FileUtils.remove(CmsConst.WORK_DIR+"/html/articleList/queryTemp");
//        FileUtils.remove(CmsConst.WORK_DIR+"/html/mind/"+category.getId()+".html");
    }
    @Async
    public void deleteTemp(BaseCategory category){
        deleteTemp(category.getName(),category.getPath(),category.getViewName(),category.getParentId());
    }

    @Override
    public void injectContent(BaseCategory category) {
        deleteTemp(category);
    }
}
