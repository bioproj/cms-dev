package com.wangyang.common;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class CmsConst {



    @Value("${cms.workDir}")
    private void setWorkDir(String workDir) {
        this.WORK_DIR = workDir;
    }
    public static  String WORK_DIR;


    @Value("${cms.templates}")
    private void setTemplates(String templates) {
        this.TEMPLATES = templates;
    }
    public static  String TEMPLATES;

    @Value("${cms.isDebug}")
    private  void setsDebug(Boolean isDebug) {
        this.IS_DEBUG = isDebug;
    }
    public static  Boolean IS_DEBUG ;

    @Value("${cms.proxy_url}")
    private  void setsProxyUrl(String PROXY_URL ) {
        this.PROXY_URL = PROXY_URL;
    }
    public static  String PROXY_URL ;

    public static final String SYSTEM_TEMPLATE_PATH="templates";
    public static final String SYSTEM_INTERNAL_TEMPLATE_PATH="internal_template"+File.separator+"default";
    public static final String SYSTEM_HTML_PATH="html";
    public static final String TEMPLATE_PATH="templates";
    public static final String STATIC_HTML_PATH="html";
    public static final String CONFIGURATION= "application.yml";
    public final static String UPLOAD_SUB_DIR = "upload/";
    public final static String INIT_STATUS = "INIT_STATUS";
    public final static String COMMENT_PATH =  File.separator+"comment";
    public final static String COMMENT_PATH_JSON =COMMENT_PATH+File.separator+"json";

    public final static String CATEGORY_PATH = "html"+ File.separator+"articleList";
    public final static String CATEGORY_PATH_LIST = File.separator+"articleList";
    public final static String FIRST_ARTICLE_LIST = File.separator+"firstArticleList";
    public final static String ARTICLE_RECOMMEND_LIST_PATH =  File.separator+"articleRecommendList";
    public final static String ARTICLE_RECENT_LIST_PATH =  File.separator+"articleRecentList";
    public final static String CATEGORY_CHILDREN =  File.separator+"categoryChildren";
    public final static String FIRST_ARTICLE_TITLE_LIST =  File.separator+"firstTitleList";
    public final static String ARTICLE_LIST_JS_PATH = File.separator+"articleListJs";



    public final static String TAG_PATH = "html"+ File.separator+"tag";


    public final static String COMPONENT_PATH = "html"+ File.separator+"components";
    public final static String COMPONENT_FRAGMENT = "html"+ File.separator+"components"+File.separator+"fragment";
    public final static String CATEGORY_MENU = "categoryMenu";

//    public final static String CATEGORY_LIST_PATH = "html"+File.separator+"articleList";
    public final static String ARTICLE_DETAIL_PATH = "html"+File.separator+"article";
    public final static String LITERATURE_DETAIL_PATH = "html"+File.separator+"literature";
    public final static String SHEET_PATH = "html"+File.separator+"sheet";


    public final static String DEFAULT_ARTICLE_PDF_TEMPLATE = "DEFAULT_ARTICLE_PDF_TEMPLATE";
    public final static String DEFAULT_SHEET_PDF_TEMPLATE = "DEFAULT_SHEET_PDF_TEMPLATE";
//    public final static String DEFAULT_ARTICLE_PREVIEW_TEMPLATE = "DEFAULT_ARTICLE_PREVIEW_TEMPLATE";


    public final static String DEFAULT_ARTICLE_TEMPLATE = "DEFAULT_ARTICLE";
    public final static String DEFAULT_COURSE_TEMPLATE = "DEFAULT_COURSE_TEMPLATE";
    public final static String DEFAULT_LITERATURE_TEMPLATE = "DEFAULT_LITERATURE_TEMPLATE";
    public final static String DEFAULT_TREE_ARTICLE_TEMPLATE = "DEFAULT_TREE_ARTICLE_TEMPLATE";
    public final static String DEFAULT_ARTICLE_CHANNEL_TEMPLATE = "DEFAULT_ARTICLE_CHANNEL";
    public final static String DEFAULT_ARTICLE_PICTURE_TEMPLATE = "DEFAULT_ARTICLE_PICTURE";

    public final static String DEFAULT_CHANNEL_TEMPLATE = "DEFAULT_CHANNEL";//分类下的文章列表
    public final static String DEFAULT_PICTURE_TEMPLATE = "DEFAULT_PICTURE";
    public final static String DEFAULT_CATEGORY_TEMPLATE = "DEFAULT_CATEGORY";
    public final static String DEFAULT_CATEGORY_DOC_TEMPLATE = "DEFAULT_CATEGORY_DOC_TEMPLATE";
    public final static String CATEGORY_ARTICLE = "CATEGORY_ARTICLE";
    public final static String DEFAULT_CATEGORY_CHILD = "DEFAULT_CATEGORY_CHILD";
    public final static String CATEGORY_TITLE = "CATEGORY_TITLE";
    public final static String DEFAULT_CATEGORY_TREE_TEMPLATE = "DEFAULT_CATEGORY_TREE_TEMPLATE";
    public final static String DEFAULT_REVEAL_TEMPLATE = "DEFAULT_REVEAL";
    public final static String DEFAULT_EMAIL = "DEFAULT_EMAIL";
    public static final String FOR_CUSTOMER = "FOR_CUSTOMER";

    public final static String DEFAULT_SHEET_TEMPLATE = "DEFAULT_SHEET";
    public final static String COMPANY_INTRODUCTION  = "COMPANY_INTRODUCTION";
    public final static String CUSTOM_SHEET_TEMPLATE = "CUSTOM_SHEET";
    public final static String EMPTY_SHEET = "EMPTY_SHEET";

    public final static String DEFAULT_COMMENT_TEMPLATE = "DEFAULT_COMMENT";//评论

    // 依据模板类型分类列表
    public final static String DEFAULT_CATEGORY_LIST = "DEFAULT_CATEGORY_LIST";//分类列表



    public final static String ARTICLE_LIST = "ARTICLE_LIST";//分类列表
    public final static String ARTICLE_RECOMMEND_LIST = "ARTICLE_RECOMMEND_LIST";//分类列表
    public final static String ARTICLE_RECENT_LIST = "ARTICLE_RECENT_LIST";//分类列表
    public final static String ARTICLE_TOP = "ARTICLE_TOP";//分类列表
    public final static String TAGS = "TAGS";//分类列表


    public final static String ARTICLE_TOP_LIST = "ARTICLE_TOP_LIST";//分类列表
    public final static String ARTICLE_PAGE = "ARTICLE_PAGE";//分类列表


    public final static String ARTICLE_JS_MIND = "ARTICLE_JS_MIND";//文章思维导图
    public final static String LOGIN_PHONE_AUTH = "LOGIN_PHONE_AUTH";//文章思维导图
    public final static String LOGIN_CONFIRM = "LOGIN_CONFIRM";//文章思维导图
    public final static String AUTH_OTHER_PAGE = "AUTH_OTHER_PAGE";//文章思维导图
    public final static String PHONE_AUTH_PAGE = "PHONE_AUTH_PAGE";//文章思维导图

    public final static String TAGS_INFORMATION = "资讯";//分类列表
    public final static String TAGS_RECOMMEND = "推荐";//分类列表


    public final static String ARTICLE_DATA = "@Article";//文章的Components标识
    public final static String CATEGORY_DATA = "@Category";//文章的Components标识
    public final static String CATEGORY_CHILD_DATA = "@CategoryChild";//文章的Components标识
    public final static String CATEGORY_ARTICLE_DATA= "@CategoryArticle";//文章的Components标识
    public final static String CATEGORY_ARTICLE_PAGE_DATA = "@CategoryArticlePage";//文章的Components标识
    public final static String CATEGORY_ARTICLE_SIZE_DATA = "@CategoryArticleSize";//文章的Components标识
    public final static String ARTICLE_DATA_SORT = "@SortArticle:";//文章的Components标识
    public final static String ARTICLE_DATA_KEYWORD = "@KeywordArticle:";//文章的Components标识
    public final static String ARTICLE_DATA_TAGS = "@TagsArticle:";//文章的Components标识
    public final static String ARTICLE_DATA_SORT_SIZE = "@SizeSortArticle:";//文章的Components标识
    public final static String TAG_DATA = "@Tag";//文章的Components标识
    public final static String ZOTERO_LITERATURE = "ZOTERO_LITERATURE";
    public final static String ZOTERO_COLLECTION = "ZOTERO_COLLECTION";


    public static final String MARKDOWN_REVEAL_START = "<p>@=";
    public static final String MARKDOWN_REVEAL_END = "=@</p>";
    public static final String LABEL_SECTION_START = "<section>";
    public static final String LABEL_SECTION_END = "</section>";


    public final static String PHONE_AUTH = "html/phoneAuth";
    public final static String WX_ROLE = "WX_ROLE";
    public final static String EMAIL_ROLE = "EMAIL_ROLE";
    public final static String PHONE_ROLE = "PHONE_ROLE";
    public final static String TEMPLATE_FILE_PREFIX = "tf:";
}
