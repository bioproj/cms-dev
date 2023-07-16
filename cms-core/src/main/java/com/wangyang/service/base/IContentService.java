package com.wangyang.service.base;

import com.wangyang.pojo.dto.CategoryContentList;
import com.wangyang.pojo.dto.CategoryContentListDao;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.common.enums.Lang;
import com.wangyang.pojo.vo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

public interface IContentService<ARTICLE extends Content,ARTICLEDTO,ARTICLEVO>  extends ICrudService<ARTICLE,ARTICLEDTO,ARTICLEVO,Integer>{
    ARTICLE createOrUpdate(ARTICLE article);
    List<ARTICLEVO> listByComponentsId(int componentsId);

    Page<ARTICLE> pageContentByCategoryIds(Set<Integer> ids, Boolean isDesc, PageRequest pageRequest);
    ARTICLE findByViewName(String viewName);
    ARTICLE findByViewName(String viewName, Lang lang);
    List<ARTICLE> listContentByCategoryIds(Set<Integer> ids, Boolean isDesc);

    Page<ARTICLEVO> convertToPageVo(Page<ARTICLE> contentPage);

    void addParentCategory(List<CategoryVO> categoryVOS, Integer parentId);

    List<CategoryContentList> listCategoryContentByComponentsId(int componentsId);

    List<CategoryContentList> listCategoryContentByComponentsId(int componentsId, Integer page);

    List<CategoryContentList> listCategoryContentByComponentsIdSize(int componentsId, Integer size);

    CategoryContentListDao findCategoryContentBy(Category category, Template template, int page);

    CategoryContentListDao findCategoryContentBy(CategoryVO category, Template template, int page);

    List<ARTICLEVO> listVoTree(Integer categoryId);

    List<ARTICLEVO> listVoTree(Set<Integer> ids, Boolean isDesc);

    void updateOrder(Integer id, List<ARTICLEVO> contentVOS);

    List<ARTICLEVO> listArticleVOBy(String viewName);

    ContentDetailVO updateCategory(ARTICLE content, Integer baseCategoryId);

//    ARTICLE previewSave(ARTICLE article);
}
