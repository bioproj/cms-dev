package com.wangyang.service.templates;

import com.wangyang.interfaces.IComponentsData;
import com.wangyang.pojo.entity.Components;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.pojo.params.ComponentsParam;
import com.wangyang.common.pojo.BaseVo;

import java.util.List;
import java.util.Map;

public interface IComponentsService  extends IBaseTemplateService<Components,Components, BaseVo> {


    Map<String, IComponentsData> getComponentsDataMap();


    List<Components> listNeedArticle();

    /**
     * 查找所有组件
     * @return
     */
//    List<Components> listAll();
//
//    List<Components> listAll(Lang lang);

    Components add(ComponentsParam componentsParam);


    List<Components> addAll(List<Components> templatePages);
    Components update(int id, ComponentsParam templatePageParam);

    Components findById(int id);

    Components findDetailsById(int id);

    Components delete(int id);
    void deleteAll();


//    Map<String ,Object> getModelPageSize(Components components, Integer page, Integer size,String order);

    Map<String ,Object> getModel(Components components);

    Components findByDataName(String dataName);


    Components findByViewName(String path, String viewName);

    Components findByViewName(String viewName);
    Components findByEnName(String viewName);

    Components saveUpdate(Integer id, ComponentsParam componentsParam);
}
