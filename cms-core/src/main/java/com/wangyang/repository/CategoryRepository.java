package com.wangyang.repository;

import com.wangyang.pojo.entity.Category;
import com.wangyang.repository.base.BaseCategoryRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends BaseCategoryRepository<Category> {

    /**
     * 根据文章id找到所有的分类
     * @return
     */
//    @Query("select o from Category o where o.id in (select a.categoryId from ArticleCategory a where a.articleId=?1)")
//    List<Category> findCategoryByArticleId(int aid);

    @Query(value = "select o.id from Category o")
    List<Integer> findAllId();


}
