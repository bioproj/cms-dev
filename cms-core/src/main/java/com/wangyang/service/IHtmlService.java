package com.wangyang.service;

import com.wangyang.pojo.dto.ArticleDto;
import com.wangyang.pojo.dto.CategoryArticleListDao;
import com.wangyang.pojo.dto.CategoryContentListDao;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.vo.ArticleDetailVO;
import com.wangyang.pojo.vo.CategoryVO;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public interface IHtmlService {


    /**
     * 生成文章的html
     * @param articleVO
     */
    void conventHtml(ArticleDetailVO articleVO);

    Set<Category> findAllCategoryPatent(Integer categoryParentId);

    /**
     * 生成该栏目下所有文章的列表, 用于动态添加到文章详情的旁边
     * @param channel
     * @return
     */
//    ChannelVo conventHtml(Channel channel);

    void generateComponentsByCategory(Integer categoryId, Integer categoryParentId);

    void generateComponentsByArticle(Integer articleId);

    /**
     * 生成该栏目下文章列表, 只展示文章列表
     * @param category
     */
    CategoryContentListDao convertArticleListBy(Category category);

//    void generateCategoryArticleListByCategory(Category category);

    Components generateHome();

//    void updateCategoryPage(Integer oldCategoryId);

//    void deleteTempFileByCategory(Category category);

    @Async //异步执行
    void conventHtml(ArticleDetailVO articleVO, Boolean isCategory);

    @Async
    void generateRecommendArticle(List<Category> categories);

    String articlePageCondition(Integer componentsId, Set<String> sortStr, String order, Integer page, Integer size);

    String articlePageCondition(Integer componentId, Set<Integer> ids, Set<String> sortStr, String order, Integer page, Integer size);

    @Async
        //异步执行
    void conventHtmlNoCategoryList(ArticleDetailVO articleVO);

//    void addOrRemoveArticleToCategoryListByCategoryId(int baseCategoryId);

//    void generateCategoryArticleListByCategory(Integer id);

    /**
     * 生成分类列表的html, 用于首页显示
     */
    void generateCategoryListHtml();


    /**
     * 生成菜单的html
     */
    void generateMenuListHtml();



    void commonTemplate(String option);

    CategoryContentListDao convertArticleListBy(CategoryVO category);


    String convertArticleListBy(Category category, int page);

    /**
     * 生成最新文章
     */
    void newArticleListHtml();

    String convertArticlePageBy(HttpServletRequest request, Page<ArticleDto> articleDtoPage, String viewName);


    String previewArticlePageBy(HttpServletRequest request, Page<ArticleDto> articleDtoPage);

    CategoryContentListDao convertArticleListBy(int categoryId);

    void convertArticleListBy(Sheet sheet);


//    void generateSheetListByChannelId(int id);


    void generateCommentHtmlByArticleId(int articleId);


    void generateCommentHtmlByArticleId(Article article);

    void generateComponentsByViewName(String path, String viewName);

    void generateHtmlByViewName(String type, String viewName);

    void articleTopListByCategoryId(int id);
}
