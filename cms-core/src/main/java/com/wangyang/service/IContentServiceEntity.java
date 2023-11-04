package com.wangyang.service;

import com.wangyang.pojo.dto.CategoryArticleListDao;
import com.wangyang.pojo.dto.CategoryContentListDao;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.vo.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Set;

public interface IContentServiceEntity extends com.wangyang.service.base.IContentService<Content,ContentDetailVO, ContentVO> {
    Page<Content> pageContentByCategoryIds(Set<Integer> ids, Boolean isDesc, PageRequest pageRequest);
    List<Content> listContentByCategoryIds(Set<Integer> ids, Boolean isDesc);

    Page<ContentVO> convertToPageVo(Page<Content> contentPage);

    void addParentCategory(List<CategoryVO> categoryVOS, Integer parentId);

    CategoryContentListDao findCategoryContentBy(CategoryVO category, Template template, int page);

    List<ContentVO> listVoTree(Integer categoryId);

    List<ContentVO> listVoTree(Set<Integer> ids, Boolean isDesc);

    void updateOrder(Integer id, List<ContentVO> contentVOS);

    List<ContentVO> listArticleVOBy(String viewName);

    ContentDetailVO updateCategory(Content content, Integer baseCategoryId);
}
