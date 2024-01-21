package com.wangyang.service;

import com.wangyang.pojo.dto.TagsDto;
import com.wangyang.pojo.entity.Tags;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ITagsService extends ICrudService<Tags, Tags, BaseVo,Integer> {

    /**
     * list tags and calculate quantity
     * @param pageable
     * @return
     */
    Page<TagsDto> list(Pageable pageable);

    /**
     * add Tags
     * @param tags
     * @return
     */
    Tags add(Tags tags);

    Tags addUniqueByKey(Tags tags);

    /**
     * update tags by Id
     * @param id
     * @return
     */
    Tags update(int id, Tags tagsUpdate);

    /**
     * delete tags by id
     * @param id
     */
    void deleteById(int id);

    /**
     * find tags by id
     * @param id
     * @return
     */
    Tags findById(int id);
    List<Tags> listAll();
    List<TagsDto> listAll1();

    Optional<Tags> findBy(String tagName);

    Optional<Tags> findBySlugName(String slugName);
}
