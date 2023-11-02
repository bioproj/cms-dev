package com.wangyang.web.controller.user;

import com.alibaba.fastjson.JSON;
import com.wangyang.common.CmsConst;
import com.wangyang.common.enums.Lang;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.MarkdownUtils;
import com.wangyang.pojo.annotation.Anonymous;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.CategoryContentListDao;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.enums.NetworkType;
import com.wangyang.pojo.enums.TemplateData;
import com.wangyang.pojo.params.TemplateParam;
import com.wangyang.pojo.support.ForceDirectedGraph;
import com.wangyang.pojo.vo.*;
import com.wangyang.service.*;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.enums.ArticleStatus;
import com.wangyang.service.base.IBaseCategoryService;
import com.wangyang.service.base.IContentService;
import com.wangyang.service.relation.IArticleTagsService;
import com.wangyang.service.templates.IComponentsService;
import com.wangyang.service.templates.ITemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    IArticleTagsService articleTagsService;
    @Autowired
    @Qualifier("contentServiceImpl")
    IContentService<Content,Content, ContentVO> contentService;

    @Autowired
    @Qualifier("baseCategoryServiceImpl")
    IBaseCategoryService<BaseCategory,BaseCategory, BaseCategoryVo> baseCategoryService;

    @PostMapping("/templates/update/{id}")
    public String update(@PathVariable("id") Integer id,TemplateParam templateParam){
        Template template = templateService.update(id, templateParam);
        return CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
    }
    @PostMapping("/templates/add")
    public String add(Template inputTemplate){
        return CmsConst.TEMPLATE_FILE_PREFIX+inputTemplate.getTemplateValue();
    }

    @GetMapping("/baseCategory/{categoryId}")
    public String previewLiterature(@PathVariable("categoryId")Integer categoryId, Model model){
        BaseCategory baseCategory = baseCategoryService.findById(categoryId);
        List<Content> contents = contentService.listContentByCategoryId(baseCategory.getId());
//        List<Literature> literatures = literatureService.listByCollectionId(collectionId);

        Template template = templateService.findByMainCategoryId(baseCategory.getId(), baseCategory.getLang());
        model.addAttribute("contents",contents);
        return CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
    }

    @GetMapping("/content/{articleId}")
    public String previewContent(@PathVariable("articleId")Integer articleId, Model model){
        Content content = contentService.findById(articleId);


        ContentVO contentVO = contentService.convertToTagVo(content);
        if(content instanceof Literature){
            Literature literature = (Literature)content;
            contentVO = literatureService.convertToTagVo(literature);
        }
        BaseCategory baseCategory = baseCategoryService.findById(content.getCategoryId());
        if(baseCategory instanceof  Category){
            Template template = templateService.findByEnName(((Category)baseCategory).getArticleTemplateName());
            model.addAttribute("view",contentVO);

            return CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
        }else {
            Template template = templateService.findByEnName(content.getTemplateName());
            model.addAttribute("view",contentVO);

            return CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
        }

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


//        Template categoryTemplate = templateService.findByMainCategoryId(category.getId());
        CategoryContentListDao categoryArticle = contentService.findCategoryContentBy(category, 0);

        if(category.getTemplateData().equals(TemplateData.ARTICLE_TREE)){
            List<ContentVO> contents = categoryArticle.getContents();
            List<ContentVO> contentVOList = new ArrayList<>();
            CMSUtils.flattenContentVOTreeToList(contents,contentVOList);
            List<ContentVO> contentVOS = contentVOList.stream().filter(item -> item.getIsDivision()==null || (item.getIsDivision()!=null && !item.getIsDivision()) ).collect(Collectors.toList());
            int index = IntStream.range(0, contentVOS.size())
                    .filter(i -> contentVOS.get(i).getId().equals(articleId))
                    .findFirst()
                    .orElse(-1);
            if(index!=-1){
                int size = contentVOS.size();
                if(index>0){
                    ContentVO forwardContentVO = contentVOS.get(index - 1);
                    articleDetailVo.setForwardContentVO(forwardContentVO);
                }
                if(index<(size-1)){
                    ContentVO nextcontentVO = contentVOS.get(index + 1);
                    articleDetailVo.setNextcontentVO(nextcontentVO);
                }
            }
        }

//        List<Category> partnerCategory = categoryService.findByParentId(articleDetailVo.getCategory().getParentId());
//        articleDetailVo.setPartnerCategory(categoryService.convertToListVo(partnerCategory));


//        Template categoryTemplate = templateService.findOptionalByEnName(category.getTemplateName());
//        List<Template> templates = templateService.findByChild(categoryTemplate.getId());
//        for (Template templateChild : templates){
//            model.addAttribute(templateChild.getEnName(),CMSUtils.getCategoryPath()+File.separator+templateChild.getEnName()+File.separator+category.getViewName());
//        }


//        ModelAndView modelAndView = new ModelAndView();
        model.addAttribute("view",articleDetailVo);
//        modelAndView.setViewName(template.getTemplateValue());
//        String html = TemplateUtil.convertHtmlAndPreview(articleDetailVo, template);
//        String convertHtml = FileUtils.convertByString(html);
        return CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
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
        return CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
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
        Template template = templateService.findByMainCategoryId(category.getId(),category.getLang());

        //预览
        CategoryContentListDao articleListVo = contentService.findCategoryContentBy(categoryService.covertToVo(category),0);
//        if(true){
        //是否生成力向图网络
        if(category.getNetworkType()!=null ){
//        if(true){
            if(category.getNetworkType().equals(NetworkType.TAGS_ARTICLE)){
                List<ContentVO> contents = articleListVo.getContents();
                contents = CMSUtils.flattenContentVOTreeToList(contents);
                ForceDirectedGraph forceDirectedGraph = articleTagsService.graph(contents);
                String json = JSON.toJSON(forceDirectedGraph).toString();
                articleListVo.setForceDirectedGraph(json);
            } else if (category.getNetworkType().equals(NetworkType.ARTICLE_ARTICLE)) {
                List<ContentVO> contents = articleListVo.getContents();
                contents = CMSUtils.flattenContentVOTreeToList(contents);
                ForceDirectedGraph forceDirectedGraph = articleService.graph(contents);
                String json = JSON.toJSON(forceDirectedGraph).toString();
                articleListVo.setForceDirectedGraph(json);
            }
        }

        List<Template> templates = templateService.findByChild(template.getId());
        for (Template templateChild : templates){
//            TemplateUtil.convertHtmlAndSave(CMSUtils.getCategoryPath()+File.separator+templateChild.getEnName(),articleListVo.getViewName(),articleListVo, templateChild);
            model.addAttribute(templateChild.getEnName(),category.getPath()+CMSUtils.getCategoryPathList()+File.separator+templateChild.getEnName()+File.separator+articleListVo.getViewName());
        }
//        String html = TemplateUtil.convertHtmlAndPreview(articleListVo, template);
//        String convertHtml = FileUtils.convertByString(html);
        model.addAttribute("view", articleListVo);
        String url = category.getPath()+File.separator+category.getViewName()+"-2-ajaxPage";
        model.addAttribute("url",url);
//        modelAndView.setViewName(template.getTemplateValue());
        return CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
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
        return  CmsConst.TEMPLATE_FILE_PREFIX+components.getTemplateValue();
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


        List<Article> articles = new ArrayList<>();
        Article article = new Article();
        article.setTitle("测试文章题目");
        article.setPath("html/articles");
        article.setViewName("testviewName");
        articles.add(article);


        List<ArticleVO> articleVOS = articleService.convertToListVo(articles);
        model.addAttribute("articleVOS", articleVOS);


        return  CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
    }

    @GetMapping("/pdf/{articleId}")
    @Anonymous
    public String previewPdf(@PathVariable("articleId")Integer articleId,Model model){
        Article article = articleService.findArticleById(articleId);
        article.setFormatContent(MarkdownUtils.renderHtmlOutput(article.getOriginalContent()));
        Template template = templateService.findByEnName(CmsConst.DEFAULT_ARTICLE_PDF_TEMPLATE);
        model.addAttribute("view",article);
        return CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
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
