package com.wangyang.common.utils;


import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.FileOperationException;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.exception.TemplateException;
import com.wangyang.config.CmsConfig;
import com.wangyang.interfaces.IComponentsData;
import com.wangyang.pojo.entity.Components;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.service.templates.IComponentsService;
import com.wangyang.service.IHtmlService;
import com.wangyang.web.core.view.GenerateHtml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TemplateUtil {

    private static ApplicationContext applicationContext;

    private static ConversionService mvcConversionService;
    @Autowired
    private  IComponentsService componentsService;


    private static IHtmlService htmlService;
    @Autowired
    public  void setApplicationContext(ApplicationContext applicationContext) {
        TemplateUtil.applicationContext = applicationContext;
    }
    @Autowired
    public  void setMvcConversionService(ConversionService mvcConversionService) {
        TemplateUtil.mvcConversionService = mvcConversionService;
    }


    @Autowired
    public static void setHtmlService(IHtmlService htmlService) {
        TemplateUtil.htmlService = htmlService;
    }



    private static String workDir="";


    @Value("${cms.workDir}")
    public TemplateUtil setWorkDir(String workDir) {
        this.workDir = workDir;
        return this;
    }




    public static void deleteTemplateHtml(String oldName, String path){
        if(StringUtils.isEmpty(oldName)){
            return;
        }
        String filePath=workDir;
        if(path!=null){
            filePath=filePath+File.separator+path;
        }
        File file = new File(filePath+File.separator+oldName+".html");
        if(file.exists()){
            file.delete();
            log.info("### delete html file"+file.getPath());
        }
        File pdfFile = new File(filePath+"/"+oldName+".pdf");
        if(pdfFile.exists()){
            pdfFile.delete();
            log.info("### delete pdf file"+pdfFile.getPath());
        }
    }


    /**
     * 直接输入路径和视图名称生成对应的Html
     * @param path
     * @param viewName
     * @param object
     * @param template
     * @return
     */
    public static String convertHtmlAndSave(String path,String viewName,Object object, Template template){
        Assert.notNull(template,"template can't null");
        Map<String,Object> map = new HashMap<>();
        map.put("view",object);
        return convertHtmlAndSave(path,viewName,map,template);
    }

    public static String convertHtmlAndSave(String path,String viewName,Map<String,Object> map, Template template){
        Assert.notNull(template,"template can't null");
        map.put("template",template);
        map.put("isSave",true);
        Context context = new Context();
        context.setVariables(map);
        String html = getHtml(template.getTemplateValue(),context);
        saveFile(path,viewName,html);
        return html;
    }
    public static String convertHtmlAndSave(String path, String templatePath, Context context){

        String html = getHtml(templatePath,context);
        savePathFile(path,html,"html");
        return html;
    }

    public static String savePathFile(String path,String html,String suffix) {
        // 路径 + 视图名称
        path = workDir+File.separator+path;

        Path savePath = Paths.get(path).getParent();
        if (Files.notExists(savePath)){
            try {
                Files.createDirectories(savePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try(FileWriter write = new FileWriter(path+"."+suffix)) {
            write.write(html);
            log.info("### Write file["+path+".html] success!!");
        } catch (IOException e) {
            throw new FileOperationException("Write html error!!");
        }
        return path+".html";
    }


    public static String convertHtmlAndPreview(Object object, BaseTemplate template){
        Assert.notNull(template,"template can't null");
        Map<String,Object> map;
        if(object instanceof  Map){
            map = (Map<String,Object> )object;
        }else {
            map = new HashMap<>();
            map.put("view",object);
            map.put("template",template);
        }
        map.put("isSave",true);
        map.put("isPreview",true);
        Context context = new Context();
        context.setVariables(map);
        return  getHtml(template.getTemplateValue(),context);
//        saveFile(path,viewName,html);
//        return html;
    }

//    public static String convertHtmlAndSave(Map<String,Object> map, BaseTemplate template){
//        map.put("isSave",true);
//        Context context = new Context();
//        context.setVariables(map);
//        return  convertHtml(template,context,map,true);
//    }

    public static String convertHtmlAndSave(Object object, BaseTemplate template){
        Map<String,Object> map;
        if(object instanceof Map){
           map= (Map<String,Object> )object;
        }else {
            map = new HashMap<>();
            map.put("view",object);
            map.put("template",template);
        }
        map.put("isSave",true);
        Context context = new Context();
        context.setVariables(map);
        return  convertHtml(template,context,object,true);

    }


//    public static String convertHtmlAndPreviewByMap(Object object, BaseTemplate template){
//        Context context = new Context();
//        context.setVariable("view",object);
//        return  convertHtml(template,context,object,false);
//    }

    private static String getValue(Object object,String method){
        String viewName = null;
        try {
            Method[] methods = object.getClass().getDeclaredMethods();

            viewName = (String)object.getClass().getMethod(method).invoke(object);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return viewName;
    }



    public static String convertHtml(BaseTemplate baseTemplate, Context context,Object object,boolean isSaveFile) {
        Assert.notNull(baseTemplate,"template can't null");
        String  html;
        if(baseTemplate instanceof Components){
            Components templatePage = (Components) baseTemplate;
            html = getHtml(templatePage.getTemplateValue(),context);

            if(isSaveFile){
                saveFile(templatePage.getPath(),templatePage.getViewName(),html);
            }
        }else{
            Template template = (Template)baseTemplate;
            html = getHtml(template.getTemplateValue(),context);

            if(isSaveFile){
                saveFile(getValue(object,"getPath"),getValue(object,"getViewName"),html);
            }
        }
        return html;

    }

    public static String getHtml(String viewName,Context context) {
        String viewNamePath = CMSUtils.getTemplates()+viewName;

//        if(!templateValue.startsWith("html")){
//
//        }
        Path componentsPath = Paths.get(CMSUtils.getWorkDir()+File.separator+viewNamePath+".html");
        if(!Files.exists(componentsPath)){
            context.setVariable("message","路径["+componentsPath+"]不存在！");
            viewNamePath = errorProcess(viewName, context,  viewNamePath);



//            if(systemViewName.contains(viewName)  ){
//                String jarViewName = CmsConst.SYSTEM_INTERNAL_TEMPLATE_PATH+File.separator+viewName;
//                Path classPathTemplatePath = FileUtils.getJarResources(jarViewName+".html");
//                if( classPathTemplatePath!=null){
//                    viewNamePath = jarViewName;
//                }else {
//                    viewNamePath =CMSUtils.getTemplates()+"error";
//                    Path errorPath = Paths.get(CmsConst.WORK_DIR+ File.separator+viewNamePath+".html");
//                    if(!Files.exists(errorPath)){
//                        String jarErrorViewName = CmsConst.SYSTEM_INTERNAL_TEMPLATE_PATH+File.separator+"error";
//                        Path classPathErrorTemplatePath = FileUtils.getJarResources(jarErrorViewName+".html");
//                        if(classPathErrorTemplatePath!=null) {
//                            viewNamePath =jarErrorViewName;
//                        }else {
//                            String error = "["+jarViewName+"]不存在!";
//                            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+jarViewName+"不存在！");
//                            return error;
//                        }
//                    }
//                }
//
//            }else {
//                viewNamePath =CMSUtils.getTemplates()+"error";
//                Path errorPath = Paths.get(CmsConst.WORK_DIR+ File.separator+viewNamePath+".html");
//                if(!Files.exists(errorPath)){
//                    String jarErrorViewName = CmsConst.SYSTEM_INTERNAL_TEMPLATE_PATH+File.separator+"error";
//                    Path classPathErrorTemplatePath = FileUtils.getJarResources(jarErrorViewName+".html");
//                    if(classPathErrorTemplatePath!=null) {
//                        viewNamePath =jarErrorViewName;
//                    }else {
//                        String error = "["+componentsPath+"]不存在!";
//                        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+componentsPath+"不存在！");
//                        return error;
//                    }
//                }
//            }
        }

        if(viewNamePath==null||"".equals(viewNamePath)){
            throw new TemplateException("Template value can't empty!!");
        }
        final ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(applicationContext, mvcConversionService);
        context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);
        context.setVariable("isWeb",false);
        String html = getFileEngine().process(viewNamePath, context);
        return html;
    }

    public static String errorPath(){
        String viewNamePath =CMSUtils.getTemplates()+"error";
        Path errorPath = Paths.get(CmsConst.WORK_DIR+ File.separator+viewNamePath+".html");


        if(!Files.exists(errorPath)){
            String jarErrorViewName = CmsConst.SYSTEM_INTERNAL_TEMPLATE_PATH+File.separator+"error";
            Path classPathErrorTemplatePath = FileUtils.getJarResources(jarErrorViewName+".html");
            if(classPathErrorTemplatePath!=null) {
                viewNamePath =jarErrorViewName;
                return viewNamePath;
            }else {
                String error = "["+viewNamePath+"]不存在!["+jarErrorViewName+"]也不存在! ";
//                writer.write(error);
               throw new ObjectException(error);
            }
        }else {
            return viewNamePath;
        }
    }

    public  static String   errorProcess(String viewName, IContext ctx, String viewNamePath){

//        Set<String> systemViewName = ServiceUtil.fetchProperty(SystemTemplates.components(), Components::getTemplateValue);
//        systemViewName.addAll(ServiceUtil.fetchProperty(SystemTemplates.templates(), Template::getTemplateValue));
//        if(systemViewName.contains(viewName) ){
//
//
//        }else {
//
//
//        }
        String jarViewName = CmsConst.SYSTEM_INTERNAL_TEMPLATE_PATH+File.separator+viewName;
        Path classPathTemplatePath = FileUtils.getJarResources(jarViewName+".html");
        if(classPathTemplatePath!=null){
            log.info("使用jar内部模板："+jarViewName);
            viewNamePath = jarViewName;
        }else {
            if(ctx instanceof WebContext){
                ((WebContext)ctx).setVariable("message","路径["+jarViewName+"]不存在！");
            }else if (ctx instanceof Context){
                ((Context)ctx).setVariable("message","路径["+jarViewName+"]不存在！");
            }

            viewNamePath = errorPath();
        }


//        if(!viewName.equals("error")){
//            String message = "路径["+viewName+"]不存在！";
//            if(ctx.getVariable("message")!=null){
//                message = ctx.getVariable("message")+message;
//            }
//            if(ctx instanceof WebContext){
//                ((WebContext)ctx).setVariable("message",message);
//            }else if (ctx instanceof Context){
//                ((Context)ctx).setVariable("message",message);
//            }
//            viewNamePath = errorPath();
//        }else {
//
//        }
        return viewNamePath;
    }
    public  void getHtml(String viewName, WebContext ctx, HttpServletRequest request, HttpServletResponse response) {
//        PrintWriter writer=null; // = response.getWriter();
        try(PrintWriter writer = response.getWriter()) {
            try {

                String viewNamePath =viewName ;
                ITemplateEngine templateEngine = getWebEngine();
                if(viewName.startsWith("redirect:")){
                    String redirectPath = viewName.substring("redirect:".length());
                    String servername =request.getServerName();

                    if(request.getRequestURL().toString().toLowerCase().startsWith(CMSUtils.getProxyUrl())){
                        response.sendRedirect("https://"+servername+"/"+redirectPath);
                    }else {
                        response.sendRedirect(redirectPath);
                    }

                    return;
                }else if(viewName.startsWith(CmsConst.TEMPLATE_FILE_PREFIX)){
                    viewName = viewName.replace(CmsConst.TEMPLATE_FILE_PREFIX,"");
                    viewNamePath = CMSUtils.getTemplates()+viewName;
//                    templateEngine = getFileEngine();
                }else if(viewName.startsWith(CmsConst.TEMPLATE_FILE_FILE_ENGIN_PREFIX)){
                    viewName = viewName.replace(CmsConst.TEMPLATE_FILE_FILE_ENGIN_PREFIX,"");
                    viewNamePath = CMSUtils.getTemplates()+viewName;
                    templateEngine = getFileEngine();
                }
//        (!viewName.startsWith("html") && !viewName.startsWith("en") ){
//
//        }


//                viewNamePath = viewNamePath.replace("_", File.separator);
            if(viewNamePath.equals("error")){
                viewNamePath =CMSUtils.getTemplates()+"error";
            }
//                String[] pathArgs = viewNamePath.split("_");
            Path path = Paths.get(CmsConst.WORK_DIR+ File.separator+viewNamePath+".html");
            if(!Files.exists(path) && !invokeGenerateHtml(viewName)){

                    viewNamePath = errorProcess(viewName, ctx,  viewNamePath);

//                    Set<String> systemViewName = ServiceUtil.fetchProperty(SystemTemplates.components(), Components::getTemplateValue);
//                    systemViewName.addAll(ServiceUtil.fetchProperty(SystemTemplates.templates(), Template::getTemplateValue));
//                    String jarViewName = CmsConst.SYSTEM_INTERNAL_TEMPLATE_PATH+File.separator+viewName;
//
//                    // 只有系统模板文件在classpath查找
//                    if(systemViewName.contains(viewName) ){
//                        Path classPathTemplatePath = FileUtils.getJarResources(jarViewName+".html");
//                        if(classPathTemplatePath!=null){
//                            log.info("使用jar内部模板："+jarViewName);
//                            viewNamePath = jarViewName;
//                        }else {
//                            ctx.setVariable("message","路径["+jarViewName+"]不存在！");
//                            viewNamePath = errorPath();
//                        }
//
//                    }else {
//                        if(!viewName.equals("error")){
//                            String message = "路径["+path+"]不存在！";
//                            if(ctx.getVariable("message")!=null){
//                                message = ctx.getVariable("message")+message;
//                            }
//                            ctx.setVariable("message",message);
//                        }
//
//                        viewNamePath = errorPath();
//
//                    }
                }

//            String[] pathArgs = viewNamePath.split("_");
//            if(!path.toFile().exists()&&!invokeGenerateHtml(pathArgs,viewName)){
//                viewNamePath = errorPath();
////
//            }
//
//            if(!Paths.get(path).toFile().exists()){
//                ctx.setVariable("message","模板不存在："+path);
//            }
                ctx.setVariable("isWeb",true);
                templateEngine.process(viewNamePath,ctx,writer);
            } catch (Exception e) {
//            throw new RuntimeException(e);
                e.printStackTrace();
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                e.printStackTrace(writer);

            }
        }catch (IOException ioe){
            ioe.printStackTrace();

        }


//        if(!templateValue.startsWith("html")){
//            templateValue = CMSUtils.getTemplates()+templateValue;
//        }
//        Path componentsPath = Paths.get(CMSUtils.getWorkDir()+File.separator+templateValue+".html");
//        if(!Files.exists(componentsPath)){
//            log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+componentsPath+"不存在！");
//            return ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+componentsPath+"不存在！";
//        }
//
//        if(templateValue==null||"".equals(templateValue)){
//            throw new TemplateException("Template value can't empty!!");
//        }
//        final ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(applicationContext, mvcConversionService);
//        context.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext);
//
//        String html = getFileEngine().process(templateValue, context);
//        return html;
    }

    /**
     * 文件不存在，查看GenerateHtml存是否存在生成html的方法，生成之后再用视图名称渲染
     * @see GenerateHtml
//     * @param pathArgs
     */

    public  boolean invokeGenerateHtml(String viewName) throws InvocationTargetException, IllegalAccessException {
//        if(pathArgs.length==1 && pathArgs[0].contains("index")){
//            htmlService.generateHome();
//            htmlService.generateHtmlByViewName();
//        }
//        if(pathArgs.length==1
//                && viewName.contains("/")){
//            int pos = viewName.lastIndexOf("/");
//            String args1 = viewName.substring(0,pos);
//            String args2 = viewName.substring(pos+1);
//            pathArgs = new String[]{args1,args2};
//
//
//
//
////            if(!viewName.contains("article")
////                    && !viewName.contains("articleList")
////                    && !viewName.contains("sheet")){
////
////                try{
////                    htmlService.generateComponentsByViewName(args1, args2);
////                    return true;
////                }catch (ObjectException e){
////                    e.printStackTrace();
////                    return false;
////                }
////            }
//        }
        if(viewName.contains("/")){
            String[] pathArgs = viewName.split("/");
            if(pathArgs.length<2){
                return false;
            }
            pathArgs = pathArgs[pathArgs.length-1].split("-");
            Map<String, IComponentsData> componentsDataMap = componentsService.getComponentsDataMap();
            if(componentsDataMap.containsKey(pathArgs[pathArgs.length-1])){
                IComponentsData componentsData = componentsDataMap.get(pathArgs[pathArgs.length - 1]);
                //生成html
                return componentsData.cacheDate(pathArgs[0]);
            }else {
                GenerateHtml generateHtml = CmsConfig.getBean(GenerateHtml.class);
                Method[] methods = generateHtml.getClass().getDeclaredMethods();
                for (Method method: methods){
                    if(method.getName().equals(pathArgs[pathArgs.length-1])){
                        method.invoke(generateHtml,new Object[]{pathArgs});
                        return true;
                    }
                }
            }



        }

//        if(!pathArgs[1].contains("-")){
//            try{
//                htmlService.generateHtmlByViewName(pathArgs[0],pathArgs[1]);
//                return true;
//            }catch (ObjectException e){
//                e.printStackTrace();
//                return false;
//            }
//
//        }else {
//            pathArgs = pathArgs[1].split("-");
//
//            try {
//                GenerateHtml generateHtml = CmsConfig.getBean(GenerateHtml.class);
//                Method[] methods = generateHtml.getClass().getDeclaredMethods();
//                for (Method method: methods){
//                    if(method.getName().equals(pathArgs[pathArgs.length-1])){
//                        method.invoke(generateHtml,new Object[]{pathArgs});
//                        return true;
//                    }
//                }
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();
//            }
//        }

        return false;
    }


    /**
     *
//     * @see com.wangyang.common.thymeleaf.IncludeElementTagProcessor#doProcess(ITemplateContext, IProcessableElementTag, IElementTagStructureHandler)
     * @return
     */
    public static ITemplateEngine
    getWebEngine() {
        TemplateEngine templateEngine = HtmlTemplateEngine.getWebInstance(workDir, ".html",CMSUtils.getTemplates());
        return templateEngine;
    }
    public static Path getTemplatePath(String viewName){
        return  Paths.get(workDir,CMSUtils.getTemplates(),viewName+".html");
    }
    /**
     *
//     * @param needInclude 是否需要引入header
//     * @see com.wangyang.common.thymeleaf.IncludeElementTagProcessor#doProcess(ITemplateContext, IProcessableElementTag, IElementTagStructureHandler)
     * @return
     */
    public static ITemplateEngine getFileEngine() {
        TemplateEngine templateEngine = HtmlTemplateEngine.getFileInstance(workDir, ".html",CMSUtils.getTemplates());

        return templateEngine;
    }
    public static String saveFile(String path,String viewName,String html) {
        return saveFile(path,viewName,html,"html");
    }
    public static Boolean checkFileExist(String path,String viewName) {
        path = workDir+File.separator+path+File.separator+viewName+".html";
        return Paths.get(path).toFile().exists();
    }
    public static String openFile(String path,String viewName) {
        path = workDir+File.separator+path+File.separator+viewName+".html";
        return FileUtils.openFile(path);
    }
    public static void deleteFile(String path) {
        path = workDir+File.separator+path;
        log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>删除文件:"+path);
        FileUtils.remove(path);
    }
    public static String saveFile(String path,String viewName,String html,String suffix) {
        // 路径 + 视图名称
        path = workDir+File.separator+path;
        if(viewName==null||"".equals(viewName)){
            throw new TemplateException("Template  view name can't null in template page!!");
        }
        Path savePath = Paths.get(path);
        if (Files.notExists(savePath)){
            try {
                Files.createDirectories(savePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try(FileWriter write = new FileWriter(path+"/"+viewName+"."+suffix)) {
            write.write(html);
            log.info("### Write file["+path+"/"+viewName+".html] success!!");
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileOperationException("Write html error!!");
        }
        return viewName+".html";
    }

    public static boolean componentsExist(String viewName){
        String path = CmsConst.WORK_DIR+File.separator+CMSUtils.getComponentsPath()+File.separator+viewName+".html";
        File file = new File(path);
        return file.exists();
    }

}
