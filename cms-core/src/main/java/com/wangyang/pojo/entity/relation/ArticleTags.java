package com.wangyang.pojo.entity.relation;

import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.pojo.entity.base.Relation;
import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue(value = "0")
@Data
public class ArticleTags extends Relation {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private int id;


}
