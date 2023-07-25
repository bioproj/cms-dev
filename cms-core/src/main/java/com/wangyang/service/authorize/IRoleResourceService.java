package com.wangyang.service.authorize;


import com.wangyang.pojo.authorize.RoleResource;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;
import java.util.Set;

public interface IRoleResourceService  extends ICrudService<RoleResource, RoleResource, BaseVo,Integer> {

    RoleResource findBy(Integer resourceId, Integer roleId);

    List<RoleResource> findByRoleId(int roleId);

    List<RoleResource> findByResourceId(int resourceId);

    List<RoleResource> findByResourceId(Set<Integer> ids);

    void init();

//    List<RoleResourceDTO> findDTOByRoleId(int roleId);
//
//    List<RoleResourceDTO> findDTOByResourceId(int resourceId);
//
//    List<Resource> findResourceWithoutRoleId(int roleId);
}
