package com.wangyang.service.base;

import com.wangyang.common.service.ICrudService;
import com.wangyang.pojo.dto.CategoryContentList;
import com.wangyang.pojo.dto.CategoryContentListDao;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.ComponentsArticle;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.common.enums.Lang;
import com.wangyang.pojo.params.ArticleQuery;
import com.wangyang.pojo.support.ForceDirectedGraph;
import com.wangyang.pojo.vo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface IContentService<ARTICLE extends Content,CONTENTDETAILVO extends  ContentDetailVO,ARTICLEVO>  extends ICrudService<ARTICLE,CONTENTDETAILVO,ARTICLEVO,Integer> {
    ComponentsArticle addComponentsArticle(int articleId, int componentsId);

    ComponentsArticle addComponentsArticle(String viewName, int componentsId);

    ARTICLE createOrUpdate(ARTICLE article);
    List<ARTICLEVO> listByComponentsId(int componentsId);

    Page<ARTICLE> pageContentByCategoryIds(Set<Integer> ids, Boolean isDesc, PageRequest pageRequest);
    ARTICLE findByViewName(String viewName);

    ARTICLEVO convertToTagVo(ARTICLEVO domainvo);

    ARTICLEVO convertToTagVo(ARTICLE domain);

    List<ARTICLEVO> convertToListTagVo(List<ARTICLE> domains);
    List<ARTICLEVO> convertToListCategoryVo(List<ARTICLE> domains);

    List<ARTICLEVO> convertToListSimpleVo(List<ARTICLE> domains);

    ARTICLE findByViewName(String viewName, Lang lang);
    List<ARTICLE> listContentByCategoryIds(Set<Integer> ids, Boolean isDesc);

    List<ARTICLE> listContentByCategoryId(Integer categoryId);

    Page<ARTICLEVO> convertToPageVo(Page<ARTICLE> contentPage);

//    void addParentCategory(List<? extends BaseCategoryVo> categoryVOS, Integer parentId);

    List<ContentVO> convertToSimpleListVo(List<Content> contents);

    List<CategoryContentList> listCategoryContentByComponentsId(int componentsId);

    List<CategoryContentList> listCategoryContentByComponentsId(int componentsId, Integer page);

    List<CategoryContentList> listCategoryContentByComponentsIdSize(int componentsId, Integer size);

    CategoryContentListDao findCategoryContentBy(BaseCategory category, int page);

    CategoryContentListDao findCategoryContentBy(BaseCategoryVo category,  int page);

    List<ContentVO> listContentTopByCategoryId(Integer categoeyId, Boolean desc);

    List<ARTICLEVO> listVoTree(Integer categoryId);

    List<ARTICLEVO> listVoTree(Set<Integer> ids, Boolean isDesc);

    void updateOrder(Integer id, List<ARTICLEVO> contentVOS);

    void updateOrder(Category category, List<ARTICLEVO> contentVOS);

    List<ARTICLEVO> listArticleVOBy(String viewName);

    ContentDetailVO updateCategory(ARTICLE content, Integer baseCategoryId);

    Page<ARTICLE>  pagePublishBy(Pageable pageable, ArticleQuery articleQuery);

    void checkContentTemplatePath(ARTICLE content);

    ForceDirectedGraph graph(List<ContentVO> contents);

    void generateSummary(ARTICLE article);


//    ARTICLE previewSave(ARTICLE article);
}
