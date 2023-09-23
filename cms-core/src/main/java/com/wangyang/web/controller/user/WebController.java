package com.wangyang.web.controller.user;


import com.google.common.base.Joiner;
import com.wangyang.common.BaseResponse;
import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.annotation.Anonymous;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.ArticleDto;
import com.wangyang.pojo.dto.CategoryContentListDao;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.params.ArticleQuery;
import com.wangyang.service.IArticleService;
import com.wangyang.service.ICategoryService;
import com.wangyang.service.IHtmlService;
import com.wangyang.service.ITemplateService;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.Template;
import com.wangyang.service.base.IContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.wangyang.handle.CrudHandlers;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
//@RestController
public class WebController {

    @Autowired
    IArticleService articleService;
    @Autowired
    ITemplateService templateService;
    @Autowired
    CrudHandlers crudHandlers;


//    @Autowired
//    IContentService contentService;
    @Autowired
    ICategoryService categoryService;
    @Autowired
    IHtmlService htmlService;

    @GetMapping("/registry")
    @Anonymous
    public String registry(HttpServletRequest request){
        Object user = request.getAttribute("user");
        if(user!=null&& ((User)user).getId()!=-1){
            return "redirect:/";
        }
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/registry";
    }
    @GetMapping("/login")
    @Anonymous
    public String login(HttpServletRequest request){
        Object user = request.getAttribute("user");
        if(user!=null&& ((User)user).getId()!=-1){
            return "redirect:/";
        }
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/login";
    }

    @GetMapping("/wxsaoyisao")
    @Anonymous
    public String wxSaoYiSao(){
        return "wxsaoyisao";
    }

    @GetMapping("/")
    @Anonymous
    public String index(HttpServletRequest request){
        return "html/index";
    }
    @GetMapping("/html/{path}/en/{viewName}.html")
    @Anonymous
    public String showENArticle(@PathVariable("path") String path, @PathVariable("viewName") String viewName) {
        return "html" + File.separator+path+File.separator+"en"+File.separator+viewName;
    }
    @GetMapping("/en/{viewName}.html")
    @Anonymous
    public String showEnArticle4( @PathVariable("viewName") String viewName) {
        return "en" +File.separator+viewName;
    }
    @GetMapping("/en/{path}/{viewName}.html")
    @Anonymous
    public String showEnArticle3(@PathVariable("path") String path, @PathVariable("viewName") String viewName) {
        return "en" + File.separator+path+File.separator+viewName;
    }

//    @GetMapping("templates/{path2}/{viewName}.html")
//    @Anonymous
//    public String showArticle4(@PathVariable("path2") String path2, @PathVariable("viewName") String viewName) {
//        return "templates"+ File.separator+path2+File.separator+viewName;
//    }
    @RequestMapping(value = {"/{path:!templates}/{path2}/{viewName}.html"})
    @Anonymous
    public String showArticle3(@PathVariable("path") String path,@PathVariable("path2") String path2, @PathVariable("viewName") String viewName) {
        return "html" + File.separator+path+File.separator+path2+File.separator+viewName;
    }
    @GetMapping("components/{id}/{args}")
    @Anonymous
    public String components1(@PathVariable("id") Integer id, @PathVariable("args") String args) {
        return "html/components" + File.separator+id+File.separator+args;
    }
    @GetMapping("{path}/{viewName}.html")
    @Anonymous
    public String showArticle2(@PathVariable("path") String path, @PathVariable("viewName") String viewName) {
        return "html" + File.separator+path+File.separator+viewName;
    }


    @GetMapping("/html/{path}/{viewName}.html")
    @Anonymous
    public String showArticle(@PathVariable("path") String path, @PathVariable("viewName") String viewName) {
        return "html" + File.separator+path+File.separator+viewName;
    }
//    @GetMapping("/html_{path}_{method}_{arg1}_{arg2}.html")
    public String show(@PathVariable("path") String path, @PathVariable("viewName") String viewName) {
        return "html" + File.separator+path+File.separator+viewName;
    }
    @GetMapping("/html_{path}.html")
    @Anonymous
    public String showArticleFormat(@PathVariable("path") String path) {
        return "html" + File.separator+path;
    }

    @GetMapping("/en_{path}.html")
    @Anonymous
    public String showEnArticleFormat(@PathVariable("path") String path) {
        return "en" + File.separator+path;
    }
//    @GetMapping("/en/{path}.html")
//    @Anonymous
//    public String showEnArticleFormat(@PathVariable("path") String path) {
//        return "html/en" + File.separator+path;
//    }

//    @GetMapping("/html_articleList_{path}.html")
//    @Anonymous
//    public ModelAndView articleList(@PathVariable("path") String path) {
//        Category category = categoryService.findByViewName(path);
//        PageRequest pageRequest = PageRequest.of(0, 10);
//        Page<? extends BaseEntity> baseEntities = crudHandlers.pageBy(CrudType.ARTICLE, pageRequest, "");
//        ModelAndView modelAndView = new ModelAndView("html" +File.separator+ "articleList_"+path);
//        return modelAndView;
//    }

    /**
     * 没有登录用户的文章列表和搜索
     * @param articleQuery
     * @param pageable
     * @param model
     * @return
     */
    @RequestMapping(value = "/articleList")
    public String articleListBySort(ArticleQuery articleQuery, @PageableDefault(sort = {"id"},direction = DESC) Pageable pageable, Model model) {
        Page<Article> articlePage = articleService.pagePublishBy(pageable, articleQuery);
        Page<ArticleDto> articleDtoPage = articleService.convertToSimple(articlePage);
        model.addAttribute("view",articleDtoPage);
        Template template = templateService.findOptionalByEnName(CmsConst.ARTICLE_PAGE);
        return CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
    }

    /**
     * 基于AJax的分页
     * @param categoryId

     * @return
     */
    @GetMapping("/categoryAjax/{categoryId}")
    @ResponseBody
    public Page<ArticleDto> articleListByCategoryAjax(@PathVariable("categoryId") Integer categoryId, Integer page){
        Page<ArticleDto> articles = articleService.pageDtoByCategoryId(categoryId,page);
        return articles;
    }

    @GetMapping("/option/increaseLikeCount/{id}")
    @ResponseBody
    public BaseResponse increaseLikes(@PathVariable("id") int id) {
        int likes = articleService.increaseLikes(id);
        Integer likesNumber = articleService.getLikesNumber(id);
        if(likes!=0&likesNumber!=null){
            return BaseResponse.ok("操作成功",likesNumber);
        }else {
            return BaseResponse.error("操作失败");
        }
    }

    @GetMapping("/option/getLikeCount/{id}")
    @ResponseBody
    public BaseResponse getLikesCount(@PathVariable("id") int id) {
        Integer likesNumber = articleService.getLikesNumber(id);
        return BaseResponse.ok("操作成功",likesNumber);
    }

    @GetMapping("/option/increaseViewCount/{id}")
    @ResponseBody
    @Anonymous
    public BaseResponse increaseVisitsCount(@PathVariable("id") int id) {
        int visits = articleService.increaseVisits(id);
        Integer visitsNumber = articleService.getVisitsNumber(id);
        if(visits!=0&visitsNumber!=null){
            return BaseResponse.ok("操作成功",visitsNumber);
        }else {
            return BaseResponse.error("操作失败");
        }
    }

    @GetMapping("/option/getVisitsCount/{id}")
    @ResponseBody
    @Anonymous
    public BaseResponse getVisitsCount(@PathVariable("id") int id) {
        Integer visitsNumber = articleService.getVisitsNumber(id);
        return BaseResponse.ok("操作成功",visitsNumber);
    }




//    @GetMapping(value = "/html/{path}/{viewName}-{page}-ajaxPage",produces={"text/html;charset=UTF-8;","application/json;"})
//    @Anonymous
//    @ResponseBody
//    public String  contentCategoryPage(@PathVariable("viewName") String viewName,
//                                       @PathVariable("path") String path,
//                                       @PathVariable("page") Integer page){
//        if(page<=0){
//            return "Page is not exist!!";
//        }
//        path = "html/"+path;
//        String viewNameStr = viewName+"-"+page+"-page";
//        if(TemplateUtil.checkFileExist(path,viewNameStr)){
//            return TemplateUtil.openFile(path,viewNameStr);
//        }
//
//
//        Category category = categoryService.findByViewName(viewName);
//        return htmlService.convertArticleListBy(category,page);
//
//
//    }

    @RequestMapping("/admin")
    public String index(){
        return "redirect:admin/index.html";
    }


}
