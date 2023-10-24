package com.wangyang.web.core.aop;

import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.vo.ArticleDetailVO;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.service.*;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangyang
 * @date 2020/12/16
 */

@Component
@Aspect
@Slf4j
public class ArticleAspectJ {

    @Autowired
    IHtmlService htmlService;

    @Autowired
    ICategoryService categoryService;


    @Autowired
    IComponentsService componentsService;


    @Autowired
    IComponentsCategoryService componentsCategoryService;

    @Autowired
    ICategoryTagsService categoryTagsService;

    @Autowired
    ArticleTagsRepository articleTagsRepository;


    ITemplateService templateService;


    @Autowired
    IArticleService articleService;



    public boolean findArticleInCategory(String categoryViewName,String articleCategoryViewName){
        Category category = categoryService.findByViewName(categoryViewName);
        if(category==null){
            return false;
        }
        List<Category> categories = categoryService.findByParentId(category.getId());
        categories.add(category);
        for (Category c : categories){
            if(c.getViewName().equals(articleCategoryViewName)){
                return true;
            }
        }
        return false;

    }




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

    /**
     * 需要执行删除
     */
    @Around("execution(* com.wangyang.web.controller.api.ArticleController.updateArticleDetailVO(..)) or " +
            "execution(* com.wangyang.web.controller.api.ArticleController.createArticleDetailVO(..)) or " +
            "execution(* com.wangyang.web.controller.api.ArticleController.delete(..)) or " +
            "execution(* com.wangyang.web.controller.api.ArticleController.createArticleDetailVO(..)) or " +
//            "execution(* com.wangyang.web.controller.user.UserArticleController.fastWriteArticle(..)) or " +
            "execution(* com.wangyang.web.controller.api.ArticleController.updateCategory(..))")
    public ArticleDetailVO test(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object o = joinPoint.proceed();
            ArticleDetailVO articleDetailVO = (ArticleDetailVO)o;
//            if(findArticleInCategory("technologyServices",articleDetailVO.getCategory().getViewName())){
//                htmlService.generateMenuListHtml();
//            }
//            if(findArticleInCategory("news",articleDetailVO.getCategory().getViewName())){
//                htmlService.generateHome();
//            }


            generateRecommendArticle(articleDetailVO.getId());
            htmlService.generateComponentsByCategory(articleDetailVO.getCategory().getId(),articleDetailVO.getCategory().getParentId());
            htmlService.generateComponentsByArticle(articleDetailVO.getId());







            deleteTemp(articleDetailVO.getCategory().getName(),articleDetailVO.getCategory().getPath(),articleDetailVO.getCategory().getViewName(),articleDetailVO.getCategory().getParentId());
            if(articleDetailVO.getOldCategory()!=null){
                deleteTemp(articleDetailVO.getOldCategory());
            }
//            htmlService.newArticleListHtml();
            log.info(">>> "+articleDetailVO.getCategory().getName()+"的临时文件");
            return articleDetailVO;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void findAllChildCategoryId(Integer categoryParentId,Set<Category> categoriesInput){
        List<Category> categories = categoryService.findByParentId(categoryParentId);
        categoriesInput.addAll(categories);
        // 没有递归 只是查找一级分类
    }


    /**
     * 需要执行删除
     */
    @Around("execution(* com.wangyang.web.controller.api.CategoryController.update(..)) or " +
            "execution(* com.wangyang.web.controller.api.CategoryController.deleteById(..)) or " +
            "execution(* com.wangyang.web.controller.api.ContentController.updateCategory(..)) or " +
            "execution(* com.wangyang.web.controller.api.CategoryController.haveHtml(..)) or "+
            "execution(* com.wangyang.web.controller.api.CategoryController.generateHtml(..))")
    public Category categoryAop(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object o = joinPoint.proceed();
            Category category = (Category)o;
            if(category!=null){
                deleteTemp(category);
            }

//            htmlService.generateMenuListHtml();
            if(category!=null){
                Set<Integer> categoryIds =  new HashSet<>();
                Set<Category> categories = htmlService.findAllCategoryPatent(category.getParentId());
                Set<Integer> set = ServiceUtil.fetchProperty(categories, Category::getId);
                categoryIds.addAll(set);
                categoryIds.add(category.getId());

                List<ComponentsCategory> componentsCategories = componentsCategoryService.findByCategoryId(categoryIds);
                Set<Integer> componentIds = ServiceUtil.fetchProperty(componentsCategories, ComponentsCategory::getComponentId);
                List<Components> components = componentsService.listByIds(componentIds);
                components.forEach(component -> {
                    Map<String, Object> model = componentsService.getModel(component);
                    TemplateUtil.convertHtmlAndSave(model, component);
                });
            }

            return category;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }




    @Async
    public void deleteTemp(Category category){
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
            Category parentCategory = categoryService.findById(parentId);
            deleteTemp(parentCategory);
        }
//        FileUtils.remove(CmsConst.WORK_DIR+File.separator+ CMSUtils.getCategoryPath()+category.getViewName()+"-");
        //移除临时文章分类
//        FileUtils.remove(CmsConst.WORK_DIR+"/html/articleList/queryTemp");
//        FileUtils.remove(CmsConst.WORK_DIR+"/html/mind/"+category.getId()+".html");
    }
}
