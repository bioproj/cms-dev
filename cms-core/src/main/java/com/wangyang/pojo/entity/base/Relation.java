package com.wangyang.pojo.entity.base;

import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.pojo.enums.RelationType;
import lombok.Data;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;

@Entity(name = "t_relation")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER, columnDefinition = "int default 0")
@Data
public class Relation extends BaseEntity {
    private int articleId;
    private int relationId;

    private RelationType relationType;

}
