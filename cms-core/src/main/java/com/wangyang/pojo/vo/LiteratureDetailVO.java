package com.wangyang.pojo.vo;

import lombok.Data;

import java.util.Date;

@Data
public class LiteratureDetailVO extends ContentDetailVO{

    private String key;
    private String zoteroKey;
    private String author;

    private String url;

    private Date publishDate;
}
