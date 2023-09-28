package com.wangyang.service;

import com.wangyang.pojo.entity.Literature;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.service.base.IContentService;

import java.util.List;
import java.util.Set;

public interface ILiteratureService  extends IContentService<Literature,Literature, ContentVO> {
    List<Literature> listByKeys(Set<String> literatureStrIds);

    List<Literature> listByCollectionId(Integer collectionId);

    void generateHtml(List<Literature> literatures);

    void generateListHtml(int userId);
}
