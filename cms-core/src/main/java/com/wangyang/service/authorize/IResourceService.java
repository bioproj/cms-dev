package com.wangyang.service.authorize;


import com.wangyang.pojo.authorize.Resource;
import com.wangyang.pojo.authorize.ResourceVO;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.ICrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author wangyang
 * @date 2021/5/5
 */
public interface IResourceService  extends ICrudService<Resource, Resource, BaseVo,Integer> {
    Resource findRoleById(int id);
    Resource delResource(int id);
    Page<Resource> pageResource(Pageable pageable);
    Resource listByUri(String Uri);

    List<Resource> findByIds(Iterable<Integer> inputIds);

    List<Resource> findByWithoutIds(Iterable<Integer> inputIds);

    //
    List<ResourceVO> findByRoleId(Integer id);

    List<Resource> findByWithoutRoleId(Integer id);


//    List<Resource> findByIds(Iterable<Integer> inputIds);
//
//    List<ResourceVO> findByRoleId(Integer id);
//
//    List<Resource> findByWithoutRoleId(Integer id);
}
