package com.wangyang.pojo.entity;

import com.wangyang.pojo.entity.base.BaseCategory;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@DiscriminatorValue(value = "1")
public class Collection extends BaseCategory {
    @Column(name = "collection_key")
    private String key;
    private String parentKey;
    @Column(name = "collection_version")
    private String version;
    private String templateName;
}
