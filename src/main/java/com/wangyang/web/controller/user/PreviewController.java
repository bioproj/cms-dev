package com.wangyang.web.controller.user;

import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.MarkdownUtils;
import com.wangyang.pojo.annotation.Anonymous;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.CategoryContentListDao;
import com.wangyang.pojo.params.TemplateParam;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.pojo.vo.CommentVo;
import com.wangyang.service.*;
import com.wangyang.pojo.dto.CategoryArticleListDao;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.enums.ArticleStatus;
import com.wangyang.pojo.vo.ArticleDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    ILiteratureService literatureService;

    @Autowired
    ICollectionService collectionService;

    @Autowired
    IHtmlService htmlService;

    @Autowired
    IContentServiceEntity contentService;


    @PostMapping("/templates/update/{id}")
    public String update(@PathVariable("id") Integer id,TemplateParam templateParam){
        Template template = templateService.update(id, templateParam);
        return template.getTemplateValue();
    }
    @PostMapping("/templates/add")
    public String add(Template inputTemplate){
        return inputTemplate.getTemplateValue();
    }

    @GetMapping("/literatureList/{collectionId}")
    public String previewLiterature(@PathVariable("collectionId")Integer collectionId, Model model){
        List<Literature> literatures = literatureService.listByCollectionId(collectionId);
        Template template = templateService.findByEnName(CmsConst.DEFAULT_LITERATURE_TEMPLATE);
        model.addAttribute("view",literatures);
        return template.getTemplateValue();
    }


//    @ResponseBody
    /**
     * 使用自定义的公共头部引用语句
     */
    @GetMapping("/article/{articleId}")
    public String previewArticle(@PathVariable("articleId")Integer articleId, Model model){
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
        Category category = categoryService.findById(article.getCategoryId());
        Template template = templateService.findByEnName(category.getArticleTemplateName());
//        htmlService.addParentCategory(articleDetailVo);
        List<CategoryVO> categoryVOS =new ArrayList<>();
        articleService.addParentCategory(categoryVOS,articleDetailVo.getCategory().getParentId());

        articleDetailVo.setParentCategory(categoryVOS);
        List<Category> partnerCategory = categoryService.findByParentId(articleDetailVo.getCategory().getParentId());
        articleDetailVo.setPartnerCategory(categoryService.convertToListVo(partnerCategory));



//        ModelAndView modelAndView = new ModelAndView();
        model.addAttribute("view",articleDetailVo);
//        modelAndView.setViewName(template.getTemplateValue());
//        String html = TemplateUtil.convertHtmlAndPreview(articleDetailVo, template);
//        String convertHtml = FileUtils.convertByString(html);
        return template.getTemplateValue();
    }

    @GetMapping("/sheet/{id}")
    @Anonymous
    public String previewSheet(@PathVariable("id") Integer id,Model model){
        Sheet sheet = sheetService.findById(id);
        if(sheet.getStatus()!= ArticleStatus.PUBLISHED){
            if(sheet.getIsSource()){
                sheet.setFormatContent(sheet.getOriginalContent());
            }else {
                sheet = sheetService.createOrUpdate(sheet);
            }
        }
//        Template template = templateService.findById(sheetDetailVo.getChannel().getArticleTemplateId());
        Template template = templateService.findByEnName(sheet.getTemplateName());
//        ModelAndView modelAndView = new ModelAndView();
//
//        modelAndView.addObject("view", sheet);
//        modelAndView.setViewName(template.getTemplateValue());
        model.addAttribute("view",sheet);
//        String html = TemplateUtil.convertHtmlAndPreview(sheet, template);
//        String convertHtml = FileUtils.convertByString(html);
        return template.getTemplateValue();
    }

    @GetMapping("/save/{articleId}")
    @Deprecated
    //未使用
    public ModelAndView previewSaveArticle(@PathVariable("articleId")Integer articleId){
        Article article = articleService.findArticleById(articleId);
        article = articleService.createOrUpdate(article);
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
//    @ResponseBody
    public String previewCategory(@PathVariable("id") Integer id,Model model){
        Category category = categoryService.findById(id);
        Template template = templateService.findByEnName(category.getTemplateName());

        //预览
        CategoryContentListDao articleListVo = contentService.findCategoryContentBy(categoryService.covertToVo(category),template,0);

//        String html = TemplateUtil.convertHtmlAndPreview(articleListVo, template);
//        String convertHtml = FileUtils.convertByString(html);
        model.addAttribute("view", articleListVo);
//        modelAndView.setViewName(template.getTemplateValue());
        return template.getTemplateValue();
    }
//    @GetMapping("/sheet/{id}")
//    public ModelAndView previewSheet(@PathVariable("id") Integer id){
//        Sheet sheet = sheetService.findById(id);
//
////        Template template = templateService.findById(sheetDetailVo.getChannel().getArticleTemplateId());
//        Template template = templateService.findByEnName(sheet.getTemplateName());
//        ModelAndView modelAndView = new ModelAndView();
//
//        modelAndView.addObject("view", sheet);
//        modelAndView.setViewName(template.getTemplateValue());
//        return modelAndView;
//    }

    @GetMapping("/component/{id}")
    public String previewComponent(@PathVariable("id") Integer id,Model model){
        Components components = componentsService.findById(id);
        Map<String,Object> o = componentsService.getModel(components);
//        String html = TemplateUtil.convertHtmlAndPreview(o, components);
////        String convertHtml = FileUtils.convertByString(html);
        model.addAllAttributes(o);
        return  components.getTemplateValue();
    }

    @GetMapping("/template/{id}")
    public String previewTemplate(@PathVariable("id") Integer id,Model model){
        Template template = templateService.findById(id);
        List<CommentVo> commentVos = new ArrayList<>();
        CommentVo commentVo = new CommentVo("1111111111111111111");
        User user = new User();
        user.setUsername("wangyang");
        user.setAvatar("http://wangyang-bucket.oss-cn-beijing.aliyuncs.com/cms/image/admin.jpg");
        commentVo.setUser(user);
        commentVos.add(commentVo);
        model.addAttribute("comments",commentVos);
        model.addAttribute("viewName","1111111111111111111");
        model.addAttribute("articleId",0);
        model.addAttribute("proxyUrl", CMSUtils.getProxyUrl());

        Customer customer = new Customer();
        customer.setUsername("Edward");
        model.addAttribute("customer", customer);

//        Map<String,Object> o = componentsService.getModel(components);
////        String html = TemplateUtil.convertHtmlAndPreview(o, components);
//////        String convertHtml = FileUtils.convertByString(html);
//        model.addAllAttributes(o);
        return  template.getTemplateValue();
    }

    @GetMapping("/pdf/{articleId}")
    @Anonymous
    public String previewPdf(@PathVariable("articleId")Integer articleId,Model model){
        Article article = articleService.findArticleById(articleId);
        article.setFormatContent(MarkdownUtils.renderHtmlOutput(article.getOriginalContent()));
        Template template = templateService.findByEnName(CmsConst.DEFAULT_ARTICLE_PDF_TEMPLATE);
        model.addAttribute("view",article);
        return template.getTemplateValue();
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
