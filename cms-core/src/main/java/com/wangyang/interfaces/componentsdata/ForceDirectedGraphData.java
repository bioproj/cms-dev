package com.wangyang.interfaces.componentsdata;

import com.alibaba.fastjson.JSON;
import com.wangyang.interfaces.IComponentsData;
import com.wangyang.pojo.entity.Components;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.support.ForceDirectedGraph;
import com.wangyang.pojo.vo.ContentDetailVO;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.service.base.IContentService;
import com.wangyang.service.relation.IArticleTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ForceDirectedGraphData implements IComponentsData {


    public final static String NAME = "@ForceDirectedGraph";


    @Autowired
    IContentService<Content, ContentDetailVO, ContentVO> contentService;

    @Autowired
    IArticleTagsService articleTagsService;


    @Override
    public String getDataName() {
        return NAME;
    }

    @Override
    public Map<String, Object> getData(Components components) {
        String dataArgs = components.getDataArgs();
        Integer day = 20;
        if(dataArgs!=null){
            day = Integer.parseInt(dataArgs);
        }
        List<Content> contents = contentService.sortList(day, Sort.Direction.DESC,"updateDate","id");
        List<ContentVO> contentVOS = contentService.convertToListCategoryVo(contents);
        ForceDirectedGraph forceDirectedGraph = contentService.graphTagsCategory(contentVOS);
        String json = JSON.toJSON(forceDirectedGraph).toString();

        Map<String,Object> map = new HashMap<>();
        map.put("forceDirectedGraph",json);
        return map;
    }

    @Override
    public Boolean cacheDate(String pathArg) {
        return null;
    }
}
