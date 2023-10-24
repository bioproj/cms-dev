package com.wangyang.service.base;

import com.wangyang.common.service.ICrudService;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.entity.base.Relation;

public interface IRelationService <RELATION extends Relation,RELATIONDTO,RELATIONVO>  extends ICrudService<RELATION,RELATIONDTO,RELATIONVO,Integer> {
}
