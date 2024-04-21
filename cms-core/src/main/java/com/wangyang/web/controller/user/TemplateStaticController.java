package com.wangyang.web.controller.user;

import com.wangyang.common.CmsConst;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.vo.ArticleDetailVO;
import com.wangyang.pojo.vo.ContentDetailVO;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.service.IArticleService;
import com.wangyang.service.base.IContentService;
import com.wangyang.util.AuthorizationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping
public class TemplateStaticController {
//    @Autowired
//    IArticleService articleService;
    @Autowired
    @Qualifier("contentServiceImpl")
    IContentService<Content, ContentDetailVO, ContentVO> contentService;
//    @GetMapping("/md/{id}")
//    public String editArticleMd(HttpServletRequest request, Model model, @PathVariable("id") Integer id){
//        int userId = AuthorizationUtil.getUserId(request);//在授权时将userId存入request
//        Article article = articleService.findByIdAndUserId(id, userId);
//        ArticleDetailVO articleDetailVO = articleService.conventToAddTags(article);
////        ArticleDetailVO articleDetailVO = articleService.convert(article);
//        model.addAttribute("view",articleDetailVO);
//        return CmsConst.TEMPLATE_FILE_PREFIX+"md/index";
//    }
    @GetMapping("/md/{id}.do")
    public String editContentMd(HttpServletRequest request,Model model,@PathVariable("id") Integer id){
        int userId = AuthorizationUtil.getUserId(request);//在授权时将userId存入request
        Content content = contentService.findById(id);
        ContentVO contentVO = contentService.convertToTagVo(content);

//        ArticleDetailVO articleDetailVO = articleService.conventToAddTags(article);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        model.addAttribute("view",contentVO);
        model.addAttribute("originalContent",content.getOriginalContent());
        return CmsConst.TEMPLATE_FILE_PREFIX+"md/index";
    }
}
