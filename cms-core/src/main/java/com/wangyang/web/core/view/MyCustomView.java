package com.wangyang.web.core.view;

import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.config.CmsConfig;
import com.wangyang.pojo.authorize.Role;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.authorize.UserDetailDTO;
import com.wangyang.pojo.entity.AuthRedirect;
import com.wangyang.pojo.entity.Components;
import com.wangyang.service.IAuthRedirectService;
import com.wangyang.service.IHtmlService;
import com.wangyang.util.AuthorizationUtil;
import org.apache.el.lang.EvaluationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.Beans;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MyCustomView implements View {

    private ApplicationContext applicationContext;

    private ConversionService mvcConversionService;

    private Boolean isDebug;
    private String viewName;
    private TemplateUtil templateUtil;
    IAuthRedirectService authRedirectService;
    public  MyCustomView(String viewName, Boolean isDebug, ApplicationContext applicationContext, ConversionService mvcConversionService, TemplateUtil templateUtil, IAuthRedirectService authRedirectService){
        this.viewName = viewName;
        this.applicationContext =applicationContext;
        this.mvcConversionService =mvcConversionService;
        this.isDebug =isDebug;
        this.authRedirectService = authRedirectService;
        this.templateUtil = templateUtil;
    }
    @Override
    public void render(Map<String, ?> mapInput, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> map=(Map<String, Object>) mapInput;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        String requestURI = request.getRequestURI();

        Object status = map.get("status");

//        if(status!=null &&status.equals(401)){
//            AuthRedirect authRedirect = authRedirectService.findByCurrentUrl(requestURI);
//            if(authRedirect!=null){
//                response.sendRedirect(authRedirect.getAuthUrl()+"?state="+requestURI);
//            }
//
//        }

        String authUrl = request.getParameter("authUrl");

        if(status!=null &&status.equals(401) && authUrl!=null ){
            response.sendRedirect(authUrl+"?state="+requestURI+"?authUrl="+authUrl);
            return;
        }
        if(status!=null&&status.equals(404) &&map.get("error")!=null){
            map.put("message",map.get("error"));
        }


//        String viewNamePath = viewName.replace("_", File.separator);
//        if(viewName.equals("error")){
//            viewNamePath =CMSUtils.getTemplates()+"error";
//        }
//        String path = CmsConst.WORK_DIR+ File.separator+viewNamePath+".html";
        UserDetailDTO user = AuthorizationUtil.getUser(request);
        if(user!=null){
            map.put("username",user.getUsername());
            map.put("userId",user.getId());
            if(user.getRoles()!=null && user.getRoles().size()>0){
                Set<String> strings = ServiceUtil.fetchProperty(user.getRoles(), Role::getEnName);
                map.put("roles",strings);
            }else {
                map.put("roles",new HashSet<>());
            }
        }

        if (isDebug){
            map.put("debug",true);
        }else {
            map.put("debug",false);
        }

//        https://stackoverflow.com/questions/38518377/thymeleaf-email-template-and-conversionservice
        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(),map);
        final ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(applicationContext, mvcConversionService);
        ctx.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);

        ctx.setVariable("URL",requestURI);
//        ctx.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
//                applicationContext);

//        ITemplateEngine templateEngine = TemplateUtil.getWebEngine();
//        String[] pathArgs = viewName.split("_");
//        if(!Paths.get(path).toFile().exists()&&!invokeGenerateHtml(pathArgs,viewName)){
//            viewNamePath = CMSUtils.getTemplates()+"error";
//            if(!Paths.get(path).toFile().exists()){
//                ctx.setVariable("message","模板不存在："+path);
//            }
//            response.setStatus(HttpStatus.NOT_FOUND.value());
//        }
//
//        if(!Paths.get(path).toFile().exists()){
//            ctx.setVariable("message","模板不存在："+path);
//        }
//        templateEngine.process(viewNamePath,ctx,response.getWriter());
        templateUtil.getHtml(viewName,ctx,request,response);
    }


    @Override
    public String getContentType() {
        //相当于response.setContextType()
        return "text/html;charset=utf-8";
    }
}
