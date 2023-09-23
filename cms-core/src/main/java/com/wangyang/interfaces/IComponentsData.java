package com.wangyang.interfaces;

import com.wangyang.pojo.entity.Components;

import java.util.Map;

public interface IComponentsData {
    String getDataName();

    Map<String,Object> getData(Components components);

    Boolean cacheDate(String pathArg);
}
