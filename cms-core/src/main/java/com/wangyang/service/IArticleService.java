package com.wangyang.service;

import com.wangyang.pojo.dto.ArticleAndCategoryMindDto;
import com.wangyang.pojo.dto.ArticleDto;
import com.wangyang.pojo.dto.ArticlePageCondition;
import com.wangyang.pojo.dto.CategoryArticleListDao;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.params.ArticleQuery;
import com.wangyang.pojo.support.TemplateOption;
import com.wangyang.pojo.vo.ArticleDetailVO;
import com.wangyang.pojo.vo.ArticleVO;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.service.base.IContentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;


@TemplateOption
public interface IArticleService extends IContentService<Article,ArticleDetailVO,ArticleVO> {

    /**
     * 为文章添加类别和标签
     * @param article
     * @return
     */
    ArticleDetailVO convert(Article article);
    /**
     * 为文章只添加标签
     * @param article
     * @return
     */
    ArticleDetailVO conventToAddTags(Article article);
    /**
     * Find article vo  by id
     * @param id article id
     * @return
     */
    ArticleDetailVO findArticleAOById(int id);
    ArticleDetailVO updateArticleDetailVo(Integer userId,Article article, Set<Integer> tagsIds);
    ArticleDetailVO createArticleDetailVo(Integer userId,Article article, Set<Integer> tagsIds);
//    ArticleDetailVO updateArticleDetailVo(Article article);
    ArticleDetailVO updateArticleCategory(Integer userId,Article article, int categoryId);


    /**
     * 编辑文章时判断，该文章是否属于该用户
     * @param id 文章id
     * @param userId 用户 id
     * @return
     */
    Article findByIdAndUserId(int id, int userId);
    Article findArticleById(int id);
    Article save(Article article);
    /**
     * 保存或者更新文章草稿,只是保存文章内容
     * @param article
     * @return
     */
    Article updateArticleDraft(Integer userId,Article article,boolean more);
    Article saveArticleDraft(Integer userId,Article article,boolean more);

    Article recycle(int id);

    Article deleteByArticleId(int id);
//    Article haveHtml(int id);
    Article openComment(int id);
    Article updateOrder(int articleId, int order);

    /**
     * 发送或取消置顶
     * @param id
     * @return
     */
   Article sendOrCancelTop(int id);


    /**
     *
     * @param id
     * @return
     */
    List<ArticleDto> listTopByCategoryId(Category category);
    /**
     * 同过标题查找
     * @param title
     * @return
     */
    List<ArticleDto> listByTitle(String title);


    /**
     * 查找自定义组件里的article
     * @param componentsId
     * @return
     */

    /**
     * 根据user查找article
     * @param userId
     * @return
     */
    List<Article>listByUserId(int userId);

//    void addParentCategory(List<CategoryVO> categoryVOS, Integer parentId);

    Page<Article> pageArticleByCategoryIds(Set<Integer> ids, Boolean isDesc,  PageRequest pageRequest);

    CategoryArticleListDao findCategoryArticleBy(CategoryVO category, Template template, int page);

    Page<ArticleDto> convertArticle2ArticleDto(Page<Article> articles);

    /**
     *
     * @param categoryId
     * @return
     */
    List<Article> listArticleDtoBy(int categoryId);
    /**
     * 更新本地所有文章html
     */
    List<Article>  listHaveHtml();

    /**
     * 根据category查找article
     * 不涉及
     * @param categoryId
     * @return
     */
    List<Article> listArticleBy(int categoryId);


//    ArticlePageCondition pagePublishBy(Integer componentsId, Pageable pageable);

    ArticlePageCondition pagePublishBy(Integer componentsId, Set<String> sortStr, String order, Integer page, Integer size);


    ArticlePageCondition pagePublishBy(Set<Integer> ids, Set<String> sortStr, String order, Integer page, Integer size);

    //    Page<ArticleDto> pageHtmlDtoByCategory(Category category, int page);
    Page<ArticleDto> pageDtoByCategory(Category category, Pageable pageable);
    Page<ArticleDto> pageDtoByCategoryId(int categoryId, int page);
    /**
     * 不包含DRAFT,RECYCLE
     * @param pageable
     * @param articleQuery
     * @return
     */
//    Page<Article>  pagePublishBy(Pageable pageable, ArticleQuery articleQuery);
    /**
     * 包含DRAFT,RECYCLE
     * @param pageable
     * @param articleQuery
     * @return
     */
    Page<Article>  pageByUserId(int userId, Pageable pageable, ArticleQuery articleQuery);
    /**
     * 包含DRAFT,RECYCLE
     * @param pageable
     * @param articleQuery
     * @return
     */
    Page<Article>  pageAllBy(Pageable pageable, ArticleQuery articleQuery);
//    Page<ArticleDto> pageDtoBy(Pageable pageable, ArticleQuery articleQuery);
//    Page<Article> articleList(ArticleQuery articleQuery, Pageable pageable);

    Page<ArticleDto> convertToSimple(Page<Article> articlePage);
//    Page<ArticleVO> convertToAddCategory(Page<Article> articlePage);
    Page<ArticleVO> convertToPageVo(Page<Article> articlePage);
//    Page<ArticleDto> articleShow(Specification<Article> specification, Pageable pageable);
//    Page<ArticleDto> pageDtoBy(Category category, int page);


    Page<ArticleDto> pageByTagId(int tagId, int size);
    Page<ArticleDto> pageByTagId(int tagId, Pageable pageable);


    List<ArticleVO> convertToListVo(List<Article> articles);

    /**
     * Increase article like
     * @param id
     */
    int increaseLikes(int id);
    Integer getLikesNumber(int id);
    int increaseVisits(int id);
    Integer getVisitsNumber(int id);
//    void generateSummary(Article article);

//    List<ArticleVO> listVoTree(Integer categoryId);

    Integer getCommentNum(int id);
    /**
     * 动态分页使用
     * @param category
     * @param page
     * @return
     */
//    CategoryArticleListDao findCategoryArticleBy(Category category);
    CategoryArticleListDao findCategoryArticleBy(Category category, Template template ,int page);
    void updateCommentNum(int id, int num);
    ArticleAndCategoryMindDto listArticleMindDto(int categoryId);
    ArticleAndCategoryMindDto listArticleMindDto(String viewName);
    String jsMindFormat(ArticleAndCategoryMindDto articleAndCategoryMindDto);

//    List<ArticleVO> listVoTree(Set<Integer> ids,Boolean isDesc);

    List<ArticleVO> listVoTreeByCategoryViewName(String viewName);

    List<ArticleVO> listVoByCategoryViewName(String viewName,Integer size);

    List<ArticleVO> listVoByCategoryViewName(String viewName);

    Article divisionOkOrCancel(int id);

//    void updateOrder(Integer id, List<ArticleVO> articleVOS);


//    List<ArticleDto> listDtoTree(Integer categoryId);

//    void updateOrder(List<ArticleDto> articleDtos);
}