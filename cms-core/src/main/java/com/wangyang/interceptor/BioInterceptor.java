package com.wangyang.interceptor;


import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.authorize.ApiUserDetailDTO;
import com.wangyang.pojo.authorize.Role;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.authorize.UserDetailDTO;
import com.wangyang.service.IAuthRedirectService;
import com.wangyang.service.authorize.IPermissionService;
import com.wangyang.util.AuthorizationException;
import com.wangyang.util.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author wangyang
 * @date 2021/4/24
 */
public class BioInterceptor implements HandlerInterceptor {

    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    IPermissionService permissionService;

    @Value("${cms.isAuth}")
    Boolean isAuth;



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if("OPTIONS".equals(request.getMethod().toString())) {
            return true;
        }
        String uri = request.getRequestURI();

        String method = request.getMethod();
        Set<Role> needsRoles = permissionService.findRolesByResource(method+uri);
        Set<String> needRoleStr = ServiceUtil.fetchProperty(needsRoles, Role::getEnName);

        String token = getToken(request, "Authorization");

        if(needRoleStr.contains("anonymous")){
            User user = new UserDetailDTO();
            user.setId(-1);
            if(token!=null && tokenProvider.validateToken(token)){
                user = tokenProvider.getAuthentication(token);
            }
            request.setAttribute("user",user);
            return true;
        }

        String authorize=null;
        String authorizationSdk = request.getHeader("authorizeSDK");
        String authorizeParam = request.getParameter("authorize");

        if(authorizationSdk!=null)authorize=authorizationSdk;
        if(authorizeParam!=null)authorize=authorizeParam;

        if(authorize!=null){
            ApiUserDetailDTO apiUserDetailDTO = permissionService.findSDKRolesByResource(authorize);
            for(Role needRole : needsRoles){
                if(apiUserDetailDTO.getRoles().contains(needRole)){
                    request.setAttribute("user",apiUserDetailDTO);
                    return true;
                }
            }
        }


        if(token==null | !tokenProvider.validateToken(token)){
            CMSUtils.deleteCooke(request,response);
            throw new AuthorizationException("["+uri+"]需要授权！");
        }

        UserDetailDTO userDetailDTO = tokenProvider.getAuthentication(token);
        for(Role needRole : needsRoles){
            Set<Role> roles = userDetailDTO.getRoles();
            Set<String> roleStr = ServiceUtil.fetchProperty(roles, Role::getEnName);
            if(roleStr.contains(needRole.getEnName())) {
                request.setAttribute("user",userDetailDTO);
                return true;
            }
        }


        String authorities = needRoleStr.stream()
                .collect(Collectors.joining(" | "));
        throw new AuthorizationException("权限不足，["+uri+"]请求角色："+authorities);
    }

    public static  String getToken(HttpServletRequest request,String tokenName){
        String bearerToken = request.getHeader(tokenName);

        String header = request.getHeader("accept");
        String headerAccept =" ";
        if(header!=null){
            headerAccept= header.split(",")[0];
        }

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }else if(headerAccept.equals("text/html")){
            Cookie[] cookies = request.getCookies();
            if(cookies!=null){
                for (int i = 0;i<cookies.length;i++){
                    Cookie cookie = cookies[i];
                    if(cookie.getName().equals(tokenName)){
                        return cookie.getValue();
                    }
                }
            }
        }
        return null;
    }
}
