package com.wangyang.listener;

import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.pojo.entity.Components;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.enums.TemplateType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SystemTemplates {

    private static final List<Template> templates;
    private static final List<Components> components;
    static {
        templates = Arrays.asList(
                new Template("默认的文章模板", CmsConst.DEFAULT_ARTICLE_TEMPLATE,"@article", TemplateType.ARTICLE,1),
                new Template("文章教程模板",CmsConst.DEFAULT_COURSE_TEMPLATE,"@course", TemplateType.ARTICLE,1),
                new Template("Tree的文章模板",CmsConst.DEFAULT_TREE_ARTICLE_TEMPLATE,"@articleTree", TemplateType.ARTICLE,1),
                new Template("默认的文章栏目模板",CmsConst.DEFAULT_ARTICLE_CHANNEL_TEMPLATE, "@articleChannel", TemplateType.ARTICLE,2),
                new Template("默认的图片文章模板",CmsConst.DEFAULT_ARTICLE_PICTURE_TEMPLATE, "@articlePicture", TemplateType.ARTICLE,3),
                new Template("默认的pdf导出文章预览模板",CmsConst.DEFAULT_ARTICLE_PDF_TEMPLATE, "@articlePDF", TemplateType.ARTICLE,3),
                new Template("默认的pdf导出Sheet预览模板",CmsConst.DEFAULT_SHEET_PDF_TEMPLATE, "@sheetPDF", TemplateType.SHEET,3),

                //new Template("文章预览模板",CmsConst.DEFAULT_ARTICLE_PREVIEW_TEMPLATE, "@articlePreview", TemplateType.ARTICLE,3),
                new Template("默认的文献模板",CmsConst.DEFAULT_LITERATURE_TEMPLATE,"@literature", TemplateType.Literature,1),


                new Template("默认的分类模板",CmsConst.DEFAULT_CATEGORY_TEMPLATE,"@category", TemplateType.CATEGORY,4),

                new Template("分类文档模板",CmsConst.DEFAULT_CATEGORY_DOC_TEMPLATE,"@categoryDoc", TemplateType.CATEGORY,4),
                new Template("文章文档模板",CmsConst.DEFAULT_ARTICLE_DOC_TEMPLATE, "@articleDoc", TemplateType.ARTICLE,3),



                new Template("列表显示文章",CmsConst.CATEGORY_ARTICLE,"@categoryArticle", TemplateType.CATEGORY,4),
                new Template("分类模显示子分类",CmsConst.DEFAULT_CATEGORY_CHILD,"@categoryChild", TemplateType.CATEGORY,4),
                new Template("分类模板标题列表",CmsConst.CATEGORY_TITLE,"@categoryTitle", TemplateType.CATEGORY,4),
                new Template("Tree分类模板",CmsConst.DEFAULT_CATEGORY_TREE_TEMPLATE,"@categoryTree", TemplateType.CATEGORY,4,true),
                new Template("默认的栏目模板",CmsConst.DEFAULT_CHANNEL_TEMPLATE, "@channel", TemplateType.CATEGORY,5),
                new Template("默认的图片分类模板",CmsConst.DEFAULT_PICTURE_TEMPLATE, "@picture", TemplateType.CATEGORY,6),
                new Template("默认的幻灯片列表模板",CmsConst.DEFAULT_REVEAL_TEMPLATE, "@reveal", TemplateType.CATEGORY,7),

                new Template("Email模板",CmsConst.DEFAULT_EMAIL, "@email", TemplateType.EMAIL,7),
                new Template("Email模板",CmsConst.FOR_CUSTOMER, "@forCustomer", TemplateType.EMAIL,7),



                new Template("标签模板",CmsConst.TAGS, "@tags", TemplateType.TAGS,7),


                new Template("默认分类列表",CmsConst.DEFAULT_CATEGORY_LIST, "@categoryList", TemplateType.CATEGORY_LIST,7),

                new Template("默认的页面模板",CmsConst.DEFAULT_SHEET_TEMPLATE, "sheet/@sheet", TemplateType.SHEET,8),
                new Template("公司介绍",CmsConst.COMPANY_INTRODUCTION, "sheet/@company", TemplateType.SHEET,8),
                new Template("自定义页面模板",CmsConst.CUSTOM_SHEET_TEMPLATE, "sheet/@customSheet", TemplateType.SHEET,8),
                new Template("空白页面模板",CmsConst.EMPTY_SHEET, "sheet/@emptySheet", TemplateType.SHEET,8),


                new Template("默认的评论模板",CmsConst.DEFAULT_COMMENT_TEMPLATE, "@comment", TemplateType.COMMENT,9),

                new Template("基于AJAX分页的分类模板","CATEGORY_PAGE","@categoryPage", TemplateType.CATEGORY,10),
                new Template("文章幻灯片模板","REVEAL","@articleReveal", TemplateType.ARTICLE,11),

                new Template("文章列表(热门文章)",CmsConst.ARTICLE_LIST,"@articleList", TemplateType.ARTICLE_LIST,12),


                new Template("文章推荐列表",CmsConst.ARTICLE_RECOMMEND_LIST,"@articleRecommendList", TemplateType.ARTICLE_LIST,12),
                new Template("分类下的最新列表",CmsConst.ARTICLE_RECENT_LIST,"@articleRecentList", TemplateType.ARTICLE_LIST,12),
                new Template("TOP_SIZE文章列表",CmsConst.ARTICLE_TOP,"articleList/@topArticles", TemplateType.ARTICLE_LIST,12),


                new Template("文章置顶列表",CmsConst.ARTICLE_TOP_LIST,"@articleTopList", TemplateType.ARTICLE_LIST,12),
                new Template("更多文章和文章搜索",CmsConst.ARTICLE_PAGE,"@articleMore", TemplateType.ARTICLE_LIST,12),
                new Template("文章思维导图jsMind",CmsConst.ARTICLE_JS_MIND,"@jsMind", TemplateType.ARTICLE_MIND,12),
                new Template("手机验证码页面", CmsConst.LOGIN_PHONE_AUTH,"@loginPhoneAuth", TemplateType.SUBMIT_PAGE,1),
                new Template("确认页面", CmsConst.LOGIN_CONFIRM,"@loginConfirm", TemplateType.SUBMIT_PAGE,1),
                new Template("手机验证码直接登陆", CmsConst.PHONE_AUTH_PAGE,"@phoneAuth", TemplateType.SUBMIT_PAGE,1),
                new Template("登陆后操作完成页面", CmsConst.AUTH_OTHER_PAGE,"@resultPage", TemplateType.OTHER_PAGE,1)


//                new Template("手机验证码登陆页面",CmsConst.PHONE_AUTH_PAGE,"@phoneAuth", TemplateType.SUBMIT_PAGE,12)

        );

        components = new ArrayList<>();
        components.add( new Components("Carousel", CMSUtils.getComponentsPath(), "components/@carousel","carousel",CmsConst.ARTICLE_DATA,"",true));
        components.add( new Components("标签列表", CMSUtils.getComponentsPath(), "components/@tagList","tagList",CmsConst.TAG_DATA,"",true));
        components.add( new Components("Form",CMSUtils.getComponentsPath(), "components/@Form","form",CmsConst.ARTICLE_DATA,"",true));
        components.add( new Components("categoryComponent",CMSUtils.getComponentsPath(), "components/@categoryComponent","categoryComponent",CmsConst.CATEGORY_DATA,"",true));
        components.add( new Components("Company",CMSUtils.getComponentsPath(), "components/@Company","Company",CmsConst.ARTICLE_DATA,"",true));
        components.add( new Components("myArticle",CMSUtils.getComponentsPath(), "components/@myArticle","myArticle",CmsConst.ARTICLE_DATA,"",true));
        components.add( new Components("点赞最多", CMSUtils.getComponentsPath(), "components/@articleList","likeArticle",CmsConst.ARTICLE_DATA_SORT+"likes,DESC","",true));
        components.add( new Components("热门文章", CMSUtils.getComponentsPath(), "components/@newArticleIndex","hotArticle",CmsConst.ARTICLE_DATA_SORT_SIZE+"size_20,sort_visits,order_DESC","",true));
        components.add( new Components("当下流行", CMSUtils.getComponentsPath(), "components/@articleList","keyWordArticle",CmsConst.ARTICLE_DATA_KEYWORD+"R语言","",true));
        components.add( new Components("最新文章", CMSUtils.getComponentsPath(), "components/@newArticleIndex","newArticleIndex",CmsConst.ARTICLE_DATA_SORT_SIZE+"size_20,sort_createDate,order_DESC","",true));
        components.add( new Components("首页分类展示", CMSUtils.getComponentsPath(), "components/@categoryChild","categoryChild",CmsConst.CATEGORY_CHILD_DATA,"",true));


    }
    public static List<Template> templates(){
        return templates;
    }

    public static  List<Components> components(){
        return components;
    }
}
