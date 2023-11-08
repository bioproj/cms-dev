package com.wangyang.pojo.entity;

import com.wangyang.common.pojo.BaseEntity;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class UserArticle extends BaseEntity {

    private Integer userId;
    private Integer articleId;

    public UserArticle(Integer userId, Integer articleId) {
        this.userId = userId;
        this.articleId = articleId;
    }

    public UserArticle() {
    }
}
