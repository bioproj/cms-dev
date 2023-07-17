package com.wangyang.util;

import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.authorize.UserDetailDTO;

import javax.servlet.http.HttpServletRequest;

public class AuthorizationUtil {

    public static Integer getUserId(HttpServletRequest request){
        Object obj = request.getAttribute("user");
        if(obj!=null){
            return ((UserDetailDTO)obj).getId();
        }
        throw new AuthorizationException("用户id不存在！");

    }
    public static UserDetailDTO getUser(HttpServletRequest request){
        Object obj = request.getAttribute("user");
        if(obj!=null){
            return ((UserDetailDTO)obj);
        }
        return null;
    }

    public static UserDetailDTO getUserNotNUll(HttpServletRequest request) {
        Object obj = request.getAttribute("user");
        if (obj != null) {
            return ((UserDetailDTO) obj);
        }
        throw new AuthorizationException("用户不存在！");
    }
}
