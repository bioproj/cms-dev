package com.wangyang.service.authorize;


import com.wangyang.pojo.authorize.Role;
import com.wangyang.pojo.authorize.RoleParam;
import com.wangyang.pojo.authorize.RoleVO;
import com.wangyang.pojo.dto.RoleDto;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author wangyang
 * @date 2021/5/5
 */
public interface IRoleService  extends ICrudService<Role, Role, BaseVo,Integer> {
    Role addRole(Role role);
    Role findRoleById(int id);
    Role delRole(int id);
    Page<RoleDto> pageRole(Pageable pageable);

    Role findByEnName(String name);

    List<RoleVO> findByUserId(Integer id);
    List<Role> findByUser(Integer id);

    List<Role> findByWithoutUserId(Integer id);

    List<RoleVO> findByRoleId(Integer id);

    List<Role> findByWithoutRoleId(Integer id);

    Role addRole(RoleParam roleParam);

    Role updateRole(Integer id, RoleParam roleParam);


}
