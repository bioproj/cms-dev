package com.wangyang.repository;

import com.wangyang.pojo.entity.Tags;
import com.wangyang.common.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagsRepository extends BaseRepository<Tags,Integer> {

    @Query("select o from Tags o where o.id in (select a.relationId from ArticleTags a where a.articleId=?1)")
    List<Tags> findTagsByArticleId(int aid);

    Tags findTagsByName(String name);
    Tags findTagsBySlugName(String name);

    @Query("select a.relationId from ArticleTags a where a.id=?1")
//    @Query("select a.tagsId from ArticleTags a where a.articleId = ?1")
    int test1(int id);

    @Query("select o.name from Tags o where o.id = ?1")
    String test2(int id);

    @Query(nativeQuery = true,value = "SELECT * FROM tags where id = ?1")
    Tags test3(int id);
}
