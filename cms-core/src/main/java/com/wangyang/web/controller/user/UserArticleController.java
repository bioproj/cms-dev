package com.wangyang.web.controller.user;


import cn.hutool.core.bean.BeanUtil;
import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.FileUtils;
import com.wangyang.pojo.annotation.Anonymous;
import com.wangyang.pojo.annotation.CommentRole;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.entity.shop.Goods;
import com.wangyang.pojo.vo.*;
import com.wangyang.service.*;
import com.wangyang.service.authorize.ICustomerService;
import com.wangyang.service.authorize.ISubscribeService;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.pojo.dto.ArticleAndCategoryMindDto;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.dto.UserDto;
import com.wangyang.pojo.params.ArticleQuery;
import com.wangyang.service.base.IContentService;
import com.wangyang.service.templates.IComponentsService;
import com.wangyang.service.templates.ITemplateService;
import com.wangyang.util.FormatUtil;
import com.wangyang.util.AuthorizationUtil;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Controller
@RequestMapping("/user")
public class UserArticleController {
    @Autowired
    IUserService userService;

    @Autowired
    IArticleService articleService;

    @Autowired
    ICategoryService categoryService;
    @Autowired
    ILiteratureService literatureService;
    @Autowired
    IHtmlService htmlService;

    @Autowired
    ITemplateService templateService;

    @Autowired
    ISheetService sheetService;

    @Autowired
    ICustomerService customerService;


    @Autowired
    IGoodsService goodsService;

    @Autowired
    IComponentsService componentsService;

    @Autowired
    ISubscribeService subscribeService;

    @Autowired
    @Qualifier("contentServiceImpl")
    IContentService<Content, ContentDetailVO, ContentVO> contentService;

    @GetMapping("/write")
    public String writeArticle(){
//        int userId = AuthorizationUtil.getUserId(request);

//        Page<Article> articlePage = articleService.pageByUserId(userId, pageable);
//        model.addAttribute("view",articlePage);
//        System.out.println(userId);
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/write";
    }
    @GetMapping("/customer")
    public String customer(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable,Model model){
        Page<Customer> customers = customerService.pageBy(pageable);
        model.addAttribute("view",customers);
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/customer";
    }

    @GetMapping("/subscribe")
    public String subscribe(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable,Model model){
        Page<Subscribe> subscribes = subscribeService.pageBy(pageable);
        model.addAttribute("view",subscribes);
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/subscribe";
    }
    @GetMapping("/mailList")
    public String mailList(Model model){
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/mailList";
    }
    @GetMapping("/pushMail")
    public String pushMail(Model model){
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/pushMail";
    }
    @GetMapping("/editV1/{id}")
    public String editArticle2(HttpServletRequest request,Model model,@PathVariable("id") Integer id){
        int userId = AuthorizationUtil.getUserId(request);//在授权时将userId存入request
        Article article = articleService.findByIdAndUserId(id, userId);
        ArticleDetailVO articleDetailVO = articleService.conventToAddTags(article);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        model.addAttribute("view",articleDetailVO);
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/write";
    }

    @GetMapping("/edit/{id}")
    public String editArticle(HttpServletRequest request,Model model,@PathVariable("id") Integer id){
        int userId = AuthorizationUtil.getUserId(request);//在授权时将userId存入request
        Article article = articleService.findByIdAndUserId(id, userId);
        ArticleDetailVO articleDetailVO = articleService.conventToAddTags(article);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
//        ArticleDetailVO articleDetailVOSimple= BeanUtil.copyProperties(articleDetailVO,ArticleDetailVO.class,"originalContent","formatContent");
        model.addAttribute("view",articleDetailVO);
//        model.addAttribute("originalContent",articleDetailVO.getOriginalContent());
//        model.addAttribute("formatContent",articleDetailVO.getFormatContent());
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/write";
    }

    @GetMapping("/editContent/{id}")
    public String editContent(HttpServletRequest request,Model model,@PathVariable("id") Integer id){
        int userId = AuthorizationUtil.getUserId(request);//在授权时将userId存入request
        Content content = contentService.findById(id);
        ContentVO contentVO = contentService.convertToTagVo(content);

//        ArticleDetailVO articleDetailVO = articleService.conventToAddTags(article);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        model.addAttribute("view",contentVO);
        model.addAttribute("originalContent",content.getOriginalContent());
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/editContent";
    }
    @GetMapping("/editComponents/{id}")
    public String editComponents(HttpServletRequest request,Model model,@PathVariable("id") Integer id){
        int userId = AuthorizationUtil.getUserId(request);//在授权时将userId存入request
        Components components = componentsService.findDetailsById(id);

//        ArticleDetailVO articleDetailVO = articleService.conventToAddTags(article);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        model.addAttribute("view",components);
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/editComponents";
    }
    @GetMapping("/editCategory/{id}")
    public String editCategory(HttpServletRequest request,Model model,@PathVariable("id") Integer id){
        int userId = AuthorizationUtil.getUserId(request);//在授权时将userId存入request
        Category category = categoryService.findById(id);
        CategoryVO categoryVO = categoryService.convertToVo(category);
//        ArticleDetailVO articleDetailVO = articleService.conventToAddTags(article);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        model.addAttribute("view",categoryVO);
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/editCategory";
    }
    @GetMapping("/editSheet/{id}")
    public String editSheet(HttpServletRequest request,Model model,@PathVariable("id") Integer id){
        int userId = AuthorizationUtil.getUserId(request);//在授权时将userId存入request
        Sheet sheet = sheetService.findById(id);
//        Article article = articleService.findByIdAndUserId(id, userId);
//        ArticleDetailVO articleDetailVO = articleService.conventToAddTags(article);
//        ArticleDetailVO articleDetailVO = articleService.convert(article);
        model.addAttribute("view",sheet);
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/writeSheet";
    }

    @GetMapping("/literature")
    @Anonymous
    public String searchLiterature(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable, String keywords, Model model){
        Template template = templateService.findByEnName(CmsConst.DEFAULT_LITERATURE_CATEGORY_TEMPLATE);
        Set<String> filed = new HashSet<>();
        filed.add("title");
        Page<Literature> literature = literatureService.pageBy(pageable, keywords,filed);
        model.addAttribute("contents",literature);
        return CmsConst.TEMPLATE_FILE_PREFIX+template.getTemplateValue();
    }

    /**
     * 快速创建类别的文章
     * @param request
     * @return
     */
    @GetMapping("/write/{categoryId}")
    public String fastWriteArticle(@RequestParam(required = true) String title,HttpServletRequest request,@PathVariable("categoryId") Integer categoryId,Model model){
        if(title==null||title.equals("")){
            return "error";
        }
        int userId = AuthorizationUtil.getUserId(request);
        ArticleDetailVO articleDetailVO = fastWriteArticleHtml(categoryId, title, userId);
//        htmlService.generateComponentsByCategory(articleDetailVO.getCategory().getId(),articleDetailVO.getCategory().getParentId());
//        model.addAttribute("view",articleDetailVO);
        return "redirect:"+ FormatUtil.categoryListFormat(articleDetailVO.getCategory());
    }



    public ArticleDetailVO fastWriteArticleHtml(int categoryId,String title,int userId){
        Article article = new Article();
        article.setCategoryId(categoryId);
        article.setTitle(title);
        article.setOriginalContent("# 开始你的创作:"+title);
//        article.setUserId(userId);
        ArticleDetailVO articleDetailVO = articleService.createArticleDetailVo(userId,article,null);

        htmlService.conventHtml(articleDetailVO);
//        FileUtils.remove(CmsConst.WORK_DIR+ File.separator+articleDetailVO.getCategory().getPath()+File.separator+articleDetailVO.getCategory().getViewName());

        return articleDetailVO;
    }

    // http://localhost:8080/user/goods/write/2?title=789

    @GetMapping("/goods/write/{categoryId}")
    public String fastGoodsArticle(@RequestParam(required = true) String title, HttpServletRequest request, @PathVariable("categoryId") Integer categoryId, Model model){
        if(title==null||title.equals("")){
            return "error";
        }
        int userId = AuthorizationUtil.getUserId(request);
        GoodsDetailVO goodsDetailVO = fastWriteGoodsHtml(categoryId, title, userId);
//        htmlService.generateComponentsByCategory(articleDetailVO.getCategory().getId(),articleDetailVO.getCategory().getParentId());
//        model.addAttribute("view",articleDetailVO);
        return "redirect:"+ FormatUtil.categoryListFormat(goodsDetailVO.getCategory());
    }



    public GoodsDetailVO fastWriteGoodsHtml(int categoryId,String title,int userId){
        Category category = categoryService.findById(categoryId);
        Goods goods = new Goods();
        goods.setCategoryId(category.getId());
        goods.setTitle(title);
        goods.setOriginalContent("# 开始你的创作:"+title);
        goods.setUserId(userId);
        GoodsDetailVO goodsDetailVO = goodsService.createGoodsDetailVo(goods,null);

        htmlService.conventHtml(goodsDetailVO);
        FileUtils.remove(CmsConst.WORK_DIR+ File.separator+goodsDetailVO.getCategory().getPath()+File.separator+goodsDetailVO.getCategory().getViewName());

        return goodsDetailVO;
    }

    @GetMapping("/writeDraft/{categoryId}")
    public String fastWriteDraftArticleHtml(@RequestParam(required = true) String title,HttpServletRequest request,@PathVariable("categoryId") Integer categoryId,Model model){
        if(title==null||title.equals("")){
            return "error";
        }
        int userId =AuthorizationUtil.getUserId(request);
        Article article= fastWriteDraftArticleHtml(categoryId, title, userId);

//        model.addAttribute("view",articleDetailVO);
        return "redirect:/user/articleList?categoryId="+categoryId;
    }
    public Article fastWriteDraftArticleHtml(int categoryId,String title,int userId){
        Article article = new Article();
        article.setCategoryId(categoryId);
        article.setTitle(title);
        article.setOriginalContent("# 开始你的创作:"+title);
//        article.setUserId(userId);
        Article saveArticle = articleService.saveArticleDraft(userId,article,false);


        return saveArticle;
    }






    @GetMapping("/info")
    @CommentRole
    public String info(HttpServletRequest  request,Model model){
        int userId = AuthorizationUtil.getUserId(request);
        UserDto userDto = userService.findUserDaoById(userId);
        model.addAttribute("view",userDto);
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/info";
    }


    @GetMapping("/mindJs/{categoryId}")
    public String mindJs(@PathVariable("categoryId") int categoryId,Model model){
        ArticleAndCategoryMindDto articleAndCategoryMindDto = articleService.listArticleMindDto(categoryId);
        Category category = articleAndCategoryMindDto.getCategory();
        String mindFormat = articleService.jsMindFormat(articleAndCategoryMindDto);
        model.addAttribute("mind",mindFormat);
        model.addAttribute("category",category);
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/mindJs";
    }


    @GetMapping("/delete/{id}")
    public String deleteArticle(HttpServletRequest request,@PathVariable("id") Integer id){
        int userId = AuthorizationUtil.getUserId(request);
        return "redirect:/user/articleList";
    }

    /**
     * 显示用户文章，包含草稿
     * @param request
     * @param model
     * @param articleQuery
     * @param pageable
     * @return
     */
    @GetMapping("/articleList")
    public String articleList(HttpServletRequest request, Model model, ArticleQuery articleQuery, @PageableDefault(sort = {"id"},direction = DESC) Pageable pageable){
        int userId = AuthorizationUtil.getUserId(request);

        Page<Article> articlePage = articleService.pageByUserId(userId, pageable,articleQuery);
//        model.addAttribute("view",articleService.convertToAddCategory(articlePage));
        model.addAttribute("view",articlePage);
        List<CategoryDto> categories = categoryService.listAllDto();
        model.addAttribute("categories",categories);
        if(articleQuery.getCategoryId()!=null){
            model.addAttribute("categoryId",articleQuery.getCategoryId());
        }


//        System.out.println(userId);
        return CmsConst.TEMPLATE_FILE_PREFIX+"user/articleList";
    }

}
