package com.wangyang.pojo.entity;

import com.alibaba.fastjson.JSON;
import com.wangyang.pojo.entity.base.Content;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("1")
@Data
public class Sheet extends Content {
//    @Column(columnDefinition = "longtext")
//    private String cssContent;
//    @Column(columnDefinition = "longtext")
//    private String jsContent;
    private Integer userId;
    @Column(columnDefinition = "bit(1) default false")
    private Boolean recommend=false;
    @Column(columnDefinition = "bit(1) default false")
    private Boolean existNav=false;
    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
