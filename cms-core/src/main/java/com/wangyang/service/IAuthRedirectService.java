package com.wangyang.service;

import com.wangyang.common.pojo.BaseVo;
import com.wangyang.pojo.entity.AuthRedirect;
import com.wangyang.common.service.ICrudService;

public interface IAuthRedirectService extends ICrudService<AuthRedirect,AuthRedirect, BaseVo,Integer> {
    AuthRedirect addUniqueCurrentUrl(AuthRedirect authRedirectInput);

    AuthRedirect findByCurrentUrl(String currentUrl);

}
