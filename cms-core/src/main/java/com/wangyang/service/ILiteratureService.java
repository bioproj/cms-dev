package com.wangyang.service;

import com.wangyang.pojo.entity.Literature;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.pojo.vo.LiteratureDetailVO;
import com.wangyang.pojo.vo.LiteratureVo;
import com.wangyang.service.base.IContentService;

import java.util.List;
import java.util.Set;

public interface ILiteratureService  extends IContentService<Literature, LiteratureDetailVO, LiteratureVo> {
    LiteratureVo update(Integer integer, Literature updateDomain, Set<Integer> tagIds);

    List<Literature> listByKeys(Set<String> literatureStrIds);

    List<Literature> listByCollectionId(Integer collectionId);

    void generateHtml(List<Literature> literatures);

    void generateListHtml(int userId);
}
