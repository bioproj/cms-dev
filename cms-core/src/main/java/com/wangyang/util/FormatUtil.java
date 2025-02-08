package com.wangyang.util;

import com.wangyang.common.CmsConst;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.Sheet;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.vo.*;

import java.io.File;

/**
 * @author wangyang
 * @date 2020/12/15
 */
public class FormatUtil {
    /**
     * 第一页分类文章列表
     * eg. html_articleList_bioinfo.html
     * @param category
     * @return
     */
    public static String categoryListFormat(BaseCategory category){
        return File.separator+category.getPath().replace("html/","")+File.separator+category.getViewName()+".html";
//        if(category.getPath().startsWith("html")){
//            return File.separator+category.getPath().replace("html/","")+File.separator+category.getViewName()+".html";
//        }
//        return File.separator+category.getPath().replace(File.separator,"_")+"_"+category.getViewName()+".html";
    }
    public static String categoryListFormat(BaseCategoryVo category){
        return File.separator+category.getPath().replace("html/","")+File.separator+category.getViewName()+".html";
//        if(category.getPath().startsWith("html")){
//            return File.separator+category.getPath().replace("html/","")+File.separator+category.getViewName()+".html";
//        }
//        return File.separator+category.getPath().replace(File.separator,"_")+"_"+category.getViewName()+".html";
    }
    public static String categoryListFormat(CategoryVO category){
        return File.separator+category.getPath().replace("html/","")+File.separator+category.getViewName()+".html";

//        if(category.getPath().startsWith("html")){
//            return File.separator+category.getPath().replace("html/","")+File.separator+category.getViewName()+".html";
//        }
//        return File.separator+category.getPath().replace(File.separator,"_")+"_"+category.getViewName()+".html";
    }
    /**
     * 第二页分类文章列表
     *  eg. html_articleList_bioinfo_2_page.html
     * @param category
     * @return
     */
    public static String categoryList2Format(Category category) {
        return File.separator+category.getPath().replace(File.separator,"_")+"_"+category.getViewName();
    }
    public static String categoryList2Format(CategoryVO category) {
        return File.separator+category.getPath().replace("html/","")+File.separator+category.getViewName();

//        if(category.getPath().startsWith("html")){
//            return File.separator+category.getPath().replace("html/","")+File.separator+category.getViewName();
//        }
//        return File.separator+category.getPath().replace(File.separator,"_")+"_"+category.getViewName();
    }

    public static String articleList2Format(Article article) {
        return File.separator+article.getPath().replace(File.separator,"_")+"_"+article.getViewName();

//        if(article.getPath().startsWith("html")){
//            return File.separator+article.getPath().replace("html/","")+File.separator+article.getViewName();
//        }
//        return File.separator+article.getPath().replace(File.separator,"_")+"_"+article.getViewName();
    }
    public static String articleListFormat(Article article) {
        if(article!=null && (article.getIsArticleDocLink()!=null && article.getIsArticleDocLink())){
            String path = article.getCategoryPath();
            if(path.startsWith("html")){
                path =  path.replace("html/","");
            }

            return File.separator+path+File.separator+article.getCategoryViewName()+".html#/"+article.getViewName()+".html";
        }
        return File.separator+article.getPath().replace("html/","")+File.separator+article.getViewName()+".html";
//
//        if(article.getPath().startsWith("html")){
//            return File.separator+article.getPath().replace("html/","")+File.separator+article.getViewName()+".html";
//        }
//        return File.separator+article.getPath().replace(File.separator,"_")+"_"+article.getViewName()+".html";
    }
    public static String sheetListFormat(Sheet sheet) {
        return File.separator+sheet.getPath().replace("html/","")+File.separator+sheet.getViewName()+".html";

//        if(sheet.getPath().startsWith("html")){
//            return File.separator+sheet.getPath().replace("html/","")+File.separator+sheet.getViewName()+".html";
//        }
//        return File.separator+sheet.getPath().replace(File.separator,"_")+"_"+sheet.getViewName()+".html";
    }


    public static String articleListFormat(Content content) {
        if(content!=null && (content.getIsArticleDocLink()!=null && content.getIsArticleDocLink())){
            String path = content.getCategoryPath();
            if(path.startsWith("html")){
                path =  path.replace("html/","");
            }

            return File.separator+path+File.separator+content.getCategoryViewName()+".html#/"+content.getViewName()+".html";
        }
        return File.separator+content.getPath().replace("html/","")+File.separator+content.getViewName()+".html";
//
//        if(content.getPath().startsWith("html")){
//            return File.separator+content.getPath().replace("html/","")+File.separator+content.getViewName()+".html";
//        }
//        return File.separator+content.getPath().replace(File.separator,"_")+"_"+content.getViewName()+".html";

    }
    public static String articleFormat(GoodsDetailVO goodsDetailVO) {
//        if(goodsDetailVO!=null && (goodsDetailVO.getIsArticleDocLink()!=null && goodsDetailVO.getIsArticleDocLink())){
//            String path = goodsDetailVO.getCategoryPath();
//            if(path.startsWith("html")){
//                path =  path.replace("html/","");
//            }
//
//            return File.separator+path+File.separator+goodsDetailVO.getCategoryViewName()+".html#/"+goodsDetailVO.getViewName()+".html";
//        }
        return File.separator+goodsDetailVO.getPath().replace("html/","")+File.separator+goodsDetailVO.getViewName()+".html";
//
//        if(articleDetailVO.getPath().startsWith("html")){
//            return File.separator+articleDetailVO.getPath().replace("html/","")+File.separator+articleDetailVO.getViewName()+".html";
//        }
//        return File.separator+articleDetailVO.getPath().replace(File.separator,"_")+"_"+articleDetailVO.getViewName()+".html";
    }
    public static String articleFormat(ArticleDetailVO articleDetailVO) {
        if(articleDetailVO!=null && (articleDetailVO.getIsArticleDocLink()!=null && articleDetailVO.getIsArticleDocLink())){
            String path = articleDetailVO.getCategoryPath();
            if(path.startsWith("html")){
                path =  path.replace("html/","");
            }

            return File.separator+path+File.separator+articleDetailVO.getCategoryViewName()+".html#/"+articleDetailVO.getViewName()+".html";
        }
        return File.separator+articleDetailVO.getPath().replace("html/","")+File.separator+articleDetailVO.getViewName()+".html";
//
//        if(articleDetailVO.getPath().startsWith("html")){
//            return File.separator+articleDetailVO.getPath().replace("html/","")+File.separator+articleDetailVO.getViewName()+".html";
//        }
//        return File.separator+articleDetailVO.getPath().replace(File.separator,"_")+"_"+articleDetailVO.getViewName()+".html";
    }

    public static String articleFormat(SheetVo sheetVo) {
        return File.separator+sheetVo.getPath().replace("html/","")+File.separator+sheetVo.getViewName()+".html";
    }
}
