package com.wangyang.service.authorize;


import com.wangyang.pojo.authorize.UserRole;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;

import java.util.List;

public interface IUserRoleService  extends ICrudService<UserRole, UserRole, BaseVo,Integer> {

    UserRole findBy(Integer userId, Integer roleId);

    List<UserRole> findByUserId(Integer id);

    List<UserRole> findByRoleId(Integer roleId);
}
