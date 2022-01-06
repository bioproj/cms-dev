package com.wangyang.web.core.view;

import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.config.CmsConfig;
import com.wangyang.pojo.authorize.User;
import com.wangyang.util.AuthorizationUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.View;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.Map;


public class MyCustomView implements View {

    private String viewName;
    public  MyCustomView(String viewName){
        this.viewName = viewName;
    }
    @Override
    public void render(Map<String, ?> mapInput, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String,Object> map=(Map<String, Object>) mapInput;
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");
        if(viewName.startsWith("redirect:")){
            String redirectPath = viewName.substring("redirect:".length());
            response.sendRedirect(redirectPath);
            return;
        }
        String viewNamePath = viewName.replace("_", File.separator);
        if(viewName.equals("error")){
            viewNamePath = "templates/error";
        }
        String path = CmsConst.WORK_DIR+ File.separator+viewNamePath+".html";
        User user = AuthorizationUtil.getUser(request);
        if(user!=null){
            map.put("username",user.getUsername());
            map.put("userId",user.getId());
        }

        WebContext ctx = new WebContext(request, response, request.getServletContext(), request.getLocale(),map);

        ITemplateEngine templateEngine = TemplateUtil.getWebEngine();
        String[] pathArgs = viewName.split("_");
        if(!Paths.get(path).toFile().exists()&&!invokeGenerateHtml(pathArgs)){
            viewNamePath = "templates/error";
            if(!Paths.get(path).toFile().exists()){
                ctx.setVariable("message","模板不存在："+path);
            }
            response.setStatus(HttpStatus.NOT_FOUND.value());
        }
        templateEngine.process(viewNamePath,ctx,response.getWriter());
    }

    /**
     * 文件不存在，查看GenerateHtml存是否存在生成html的方法，生成之后再用视图名称渲染
     * @see GenerateHtml
     * @param pathArgs
     */
    public boolean invokeGenerateHtml(String[] pathArgs) {
        if(pathArgs.length<2){
            return false;
        }
        try {
            GenerateHtml generateHtml = CmsConfig.getBean(GenerateHtml.class);
            Method[] methods = generateHtml.getClass().getDeclaredMethods();
            for (Method method: methods){
                if(method.getName().equals(pathArgs[pathArgs.length-1])){
                    method.invoke(generateHtml,new Object[]{pathArgs});
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public String getContentType() {
        //相当于response.setContextType()
        return "text/html;charset=utf-8";
    }
}
