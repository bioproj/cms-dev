package com.wangyang.interfaces.componentsdata;

import com.wangyang.interfaces.IComponentsData;
import com.wangyang.pojo.entity.Comment;
import com.wangyang.pojo.entity.Components;
import com.wangyang.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommentData implements IComponentsData {
    public final static String NAME = "@Comment";


    @Autowired
    ICommentService commentService;

    @Override
    public Map<String,Object> getData(Components components) {
        List<Comment> comments = commentService.listAll();
        Map<String,Object> map = new HashMap<>();
        map.put("view",comments);
        return map;
    }

    @Override
    public Boolean cacheDate(String pathArg) {
        return null;
    }

    @Override
    public String getDataName() {
        return NAME;
    }
}
