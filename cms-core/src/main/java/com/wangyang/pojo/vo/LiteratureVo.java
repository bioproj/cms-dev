package com.wangyang.pojo.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
public class LiteratureVo extends ContentVO{

    private String key;
    private String zoteroKey;
    private String author;

    private String url;

    private Date publishDate;

    @Override
    public String toString(){
        return JSON.toJSONString(this);
    }

}
