package com.wangyang.pojo.enums;

import com.wangyang.common.enums.ValueEnum;
import com.wangyang.pojo.support.TemplateTypeList;

import java.util.ArrayList;
import java.util.List;

public enum  TemplateType implements ValueEnum<Integer> {
    ARTICLE(0,"文章模板"), //文章模板
    CATEGORY(1,"分类模板"), //分类模板
    SHEET(2,""), //单页模板
//    CHANNEL(3), //栏目模板
//    ARTICLE_CHANNEL(4),// 文章栏目模板
    COMMENT(3,"评论模板"),
    TEMPLATE_CHILD(4,"分类名称列表"),
    ARTICLE_LIST(5,"文章组件列表"),
    ARTICLE_MIND(6,"文章思维导图"),
    EMAIL(7,"EMAIL"),
    Literature(8,"文献模板"),
    TAGS(8,"标签"),
    SURVEY(8,"标签"),
    SUBMIT_PAGE(8,"标签"),
    OTHER_PAGE(8,"标签"),
    REPORT(8,"标签"),
    TAGS_LIST(9,"标签");

    private final int value;
    private final String name;
    TemplateType(int value,String name) {
        this.value = value;
        this.name = name;
    }
    List<TemplateTypeList> list =null;
    public List<TemplateTypeList> getList(){
        if(list!=null){
            return list;
        }
        list = new ArrayList<TemplateTypeList>();
        for (TemplateType templateType : TemplateType.values()){
            TemplateTypeList templateTypeList = new TemplateTypeList();
            templateTypeList.setId(templateType.getValue());
            templateTypeList.setName(templateType.getName());
            list.add(templateTypeList);
        }
        return list;
    }

    @Override
    public Integer getValue() {
        return value;
    }
    public String getName() {
        return name;
    }
}
