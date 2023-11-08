package com.wangyang.pojo.entity;

import com.wangyang.pojo.entity.base.Content;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@DiscriminatorValue(value = "2")
@Data
public class Literature extends Content {
    @Column(name = "literature_key")
    private String key;
    private Integer userId;
    private String zoteroKey;
    private String author;
    @Column(name = "url_")
    private String url;
//    private Integer categoryId;
    @Column(name = "publish_data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishDate;

    private String itemType;


}
