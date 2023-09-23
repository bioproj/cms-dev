package com.wangyang.interfaces.componentsdata;

import com.wangyang.interfaces.IComponentsData;
import com.wangyang.pojo.entity.Components;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CommentData implements IComponentsData {
    @Override
    public Map<String,Object> getData(Components components) {
        return null;
    }

    @Override
    public Boolean cacheDate(String pathArg) {
        return null;
    }

    @Override
    public String getDataName() {
        return "aaa";
    }
}
