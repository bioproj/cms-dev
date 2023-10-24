package com.wangyang.pojo.entity.relation;

import com.wangyang.pojo.entity.base.Relation;
import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "1")
@Data
public class ArticleRelation extends Relation {
}
