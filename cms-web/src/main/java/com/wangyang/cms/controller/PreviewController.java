package com.wangyang.cms.controller;

import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.FileUtils;
import com.wangyang.common.utils.MarkdownUtils;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.data.service.*;
import com.wangyang.model.pojo.dto.CategoryArticleListDao;
import com.wangyang.model.pojo.entity.*;
import com.wangyang.model.pojo.enums.ArticleStatus;
import com.wangyang.model.pojo.vo.ArticleDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/preview")
public class PreviewController {
    @Autowired
    IArticleService articleService;
    @Autowired
    ICategoryService categoryService;
    @Autowired
    ISheetService sheetService;
    @Autowired
    IComponentsService componentsService;

    @Autowired
    ITemplateService templateService;

    @GetMapping("/article/{articleId}")
    @ResponseBody
    public String previewArticle(@PathVariable("articleId")Integer articleId){
        Article article = articleService.findArticleById(articleId);
        if(article.getStatus()!= ArticleStatus.PUBLISHED){
            article = articleService.createOrUpdate(article);
        }
        ArticleDetailVO articleDetailVo;
        if(article.getCategoryId()==null){
            articleDetailVo = articleService.conventToAddTags(article);
        }else {
            articleDetailVo= articleService.convert(article);

        }
//        Optional<Template> templateOptional = templateRepository.findById(articleDetailVo.getTemplateId());
//        if(!templateOptional.isPresent()){
//            throw new TemplateException("Template not found in preview !!");
//        }
        if(articleDetailVo.getCategory()==null&&!articleDetailVo.getStatus().equals(ArticleStatus.PUBLISHED)){
            articleDetailVo.setTemplateName(CmsConst.DEFAULT_ARTICLE_TEMPLATE);
        }
        Template template = templateService.findByEnName(articleDetailVo.getTemplateName());
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("view",articleDetailVo);
//        modelAndView.setViewName(template.getTemplateValue());
        String html = TemplateUtil.convertHtmlAndPreview(articleDetailVo, template);
        String convertHtml = FileUtils.convertByString(html);
        return convertHtml;
    }

    @GetMapping("/save/{articleId}")
    public ModelAndView previewSaveArticle(@PathVariable("articleId")Integer articleId){
        Article article = articleService.findArticleById(articleId);
        article = articleService.previewSave(article);
        ArticleDetailVO articleDetailVo = articleService.convert(article);
//        Optional<Template> templateOptional = templateRepository.findById(articleDetailVo.getTemplateId());
//        if(!templateOptional.isPresent()){
//            throw new TemplateException("Template not found in preview !!");
//        }
        Template template = templateService.findByEnName(articleDetailVo.getTemplateName());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("view",articleDetailVo);
        modelAndView.setViewName(template.getTemplateValue());
        return modelAndView;
    }

    @GetMapping("/category/{id}")
    @ResponseBody
    public String previewCategory(@PathVariable("id") Integer id){
        ModelAndView modelAndView = new ModelAndView();
        Category category = categoryService.findById(id);
        //预览
        CategoryArticleListDao articleListVo = articleService.findCategoryArticleBy(category,0);

        Template template = templateService.findByEnName(category.getTemplateName());
        String html = TemplateUtil.convertHtmlAndPreview(articleListVo, template);
        String convertHtml = FileUtils.convertByString(html);
//        modelAndView.addObject("view", articleListVo);
//        modelAndView.setViewName(template.getTemplateValue());
        return convertHtml;
    }
    @GetMapping("/sheet/{id}")
    public ModelAndView previewSheet(@PathVariable("id") Integer id){
        Sheet sheet = sheetService.findById(id);

//        Template template = templateService.findById(sheetDetailVo.getChannel().getArticleTemplateId());
        Template template = templateService.findByEnName(sheet.getTemplateName());
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("view", sheet);
        modelAndView.setViewName(template.getTemplateValue());
        return modelAndView;
    }

    @GetMapping("/component/{id}")
    @ResponseBody
    public String previewComponent(@PathVariable("id") Integer id){
        Components components = componentsService.findById(id);
        Object o = componentsService.getModel(components);
        String html = TemplateUtil.convertHtmlAndPreview(o, components);
        String convertHtml = FileUtils.convertByString(html);
        return  convertHtml;
    }

    @GetMapping("/pdf/{articleId}")
    @ResponseBody
    public String previewPdf(@PathVariable("articleId")Integer articleId){
        Article article = articleService.findArticleById(articleId);
        article.setFormatContent(MarkdownUtils.renderHtmlOutput(article.getOriginalContent()));
        Template template = templateService.findByEnName(CmsConst.DEFAULT_ARTICLE_PDF_TEMPLATE);
        return TemplateUtil.convertHtmlAndPreview(article,template);
    }



    @GetMapping("/simpleArticle/{articleId}")
    @ResponseBody
    public String previewSimpleArticle(@PathVariable("articleId")Integer articleId){
//        ArticleDetailVO articleDetailVo = articleService.findArticleAOById(articleId);
        Article article = articleService.findArticleById(articleId);
//        Template template = templateService.findByEnName(CmsConst.DEFAULT_ARTICLE_PREVIEW_TEMPLATE);
        return article.getFormatContent();
    }

}
