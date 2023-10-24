package com.wangyang.service.base;

import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.repository.BaseRepository;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.entity.base.Relation;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.repository.base.ContentRepository;
import com.wangyang.repository.base.RelationRepository;

public abstract class AbstractRelationServiceImpl <RELATION extends Relation,RELATIONDTO extends BaseEntity,RELATIONVO extends BaseVo>  extends AbstractCrudService<RELATION,RELATIONDTO,RELATIONVO,Integer>
        implements IRelationService<RELATION,RELATIONDTO,RELATIONVO>{

    private RelationRepository<RELATION> relationRepository;
    public AbstractRelationServiceImpl(RelationRepository<RELATION> relationRepository) {
        super(relationRepository);
        this.relationRepository = relationRepository;

    }
}
