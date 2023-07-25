package com.wangyang.service.base;

import com.wangyang.common.service.ICrudService;
import com.wangyang.pojo.authorize.BaseAuthorize;
import com.wangyang.common.pojo.BaseVo;

public interface IAuthorizeService<AUTHORIZE> extends ICrudService<AUTHORIZE, BaseAuthorize, BaseVo,Integer> {
}
