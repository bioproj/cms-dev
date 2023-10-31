package com.wangyang.web.controller.api;

import com.wangyang.common.CmsConst;
import com.wangyang.common.enums.Lang;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ImageUtils;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.vo.ArticleDetailVO;
import com.wangyang.pojo.vo.BaseCategoryVo;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.service.*;
import com.wangyang.service.base.IBaseCategoryService;
import com.wangyang.service.templates.IBaseTemplateService;
import com.wangyang.service.base.IContentService;
import com.wangyang.service.templates.IComponentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/option")
public class OptionController {

    @Autowired
    IOptionService optionService;

    @Autowired
    IArticleService articleService;

    @Autowired
    IComponentsService componentsService;

    @Autowired
    IHtmlService htmlService;

    @Autowired
    ICategoryService categoryService;


    @Autowired
    ISheetService sheetService;


    @Autowired
    IContentService<Content,Content, ContentVO> contentService;

    @Autowired
    IBaseCategoryService<BaseCategory,BaseCategory,BaseCategoryVo> baseCategoryService;


    @Autowired
    IBaseTemplateService<BaseTemplate,BaseTemplate, BaseVo> baseTemplateService;

    @PostMapping
    public List<Option> addOption(@RequestBody List<Option> options){
        return optionService.saveUpdateOptionList(options);
    }
    @GetMapping
    public List<Option> list(){
        return optionService.list();
    }
    @GetMapping("/updateLanguage")
    public String updateLanguage(){

        List<Content> contents = contentService.listAll();
        contents.forEach(content -> {
            if(content.getLang()==null){
                content.setLang(Lang.ZH);
                contentService.save(content);
            }
        });

        List<BaseCategory> baseCategories = baseCategoryService.listAll();
        baseCategories.forEach(baseCategory -> {
            if(baseCategory.getLang()==null){
                baseCategory.setLang(Lang.ZH);
                baseCategoryService.save(baseCategory);
            }
        });

        List<BaseTemplate> baseTemplates = baseTemplateService.listAll();
        baseTemplates.forEach(baseTemplate -> {
            if(baseTemplate.getLang()==null){
                baseTemplate.setLang(Lang.ZH);
                baseTemplateService.save(baseTemplate);
            }
        });

        return "success!";
    }
    @GetMapping("/initialize")
    public String initialize(){
        // 初始化组件
        List<Components> components = componentsService.listAll();
        components.forEach(component -> {
            Object o = componentsService.getModel(component);
            TemplateUtil.convertHtmlAndSave(o, component);
        });

        List<Category> categories = categoryService.listAll();
        categories.forEach(category -> {
            if(true){
                if(category.getTemplateName()==null){
                    category.setTemplateName(CmsConst.DEFAULT_CATEGORY_TEMPLATE);
                }
                if(category.getArticleTemplateName()==null){
                    category.setArticleTemplateName(CmsConst.DEFAULT_ARTICLE_TEMPLATE);
                }
                if(category.getDesc()==null){
                    category.setDesc(true);
                }
                if(category.getArticleListSize()==null){
                    category.setArticleListSize(10);
                }
                category.setPath(CMSUtils.getCategoryPath());
                categoryService.save(category);
            }
            htmlService.articleTopListByCategoryId(category.getId());
            htmlService.convertArticleListBy(category);
        });

        List<Article> articles = articleService.listHaveHtml();
        articles.forEach(article->{
            if(true){
                article = articleService.createOrUpdate(article);
                articleService.generateSummary(article);
                if(article.getLikes()==null){
                    article.setLikes(0);
                }
                if(article.getVisits()==null){
                    article.setVisits(0);
                }
                if(article.getCommentNum()==null){
                    article.setCommentNum(0);
                }
                if(article.getOpenComment()==null){
                    article.setOpenComment(false);
                }
                if(article.getPicPath()==null){
                    article.setPicPath(ImageUtils.getImgSrc(article.getFormatContent()));
                }
                if(article.getParentId()==null){
                    article.setParentId(0);
                }
                if(article.getOrder()==null){
                    article.setOrder(0);
                }
                if(article.getDirection()==null){
                    article.setDirection("right");
                }
                if(article.getTop()==null){
                    article.setTop(false);
                }
                Category category = categoryService.findById(article.getCategoryId());
                article.setPath(CMSUtils.getArticlePath());
                article.setTemplateName(category.getArticleTemplateName());

                articleService.save(article);
//                log.info("更新["+article.getTitle()+"]内容!!!");
            }
            ArticleDetailVO articleDetailVO = articleService.convert(article);
            htmlService.conventHtml(articleDetailVO);
        });

        List<Sheet> sheets = sheetService.listAll();
        sheets.forEach(sheet -> {
            htmlService.convertArticleListBy(sheet);
        });
        return "success!!";
    }


}
