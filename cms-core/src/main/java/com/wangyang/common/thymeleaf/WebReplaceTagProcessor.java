package com.wangyang.common.thymeleaf;

import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.FilenameUtils;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.config.ApplicationBean;
import com.wangyang.pojo.entity.Components;
import com.wangyang.pojo.entity.Sheet;
import com.wangyang.service.*;
import com.wangyang.service.impl.*;
import com.wangyang.service.templates.ComponentsServiceImpl;
import com.wangyang.service.templates.IComponentsService;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.processor.AbstractStandardFragmentInsertionTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebReplaceTagProcessor extends AbstractStandardFragmentInsertionTagProcessor {
    private static final String ATTR_NAME  = "replace";
    private static final int PRECEDENCE = 10000;
    private static String varPattern2 = "\\~\\{(.*)}";
    private static Pattern rv = Pattern.compile(varPattern2);
    private IComponentsService componentsService= ApplicationBean.getBean(ComponentsServiceImpl.class);
    private ISheetService sheetService= ApplicationBean.getBean(SheetServiceImpl.class);
    private IHtmlService htmlService= ApplicationBean.getBean(IHtmlService.class);
    public WebReplaceTagProcessor(TemplateMode templateMode, String dialectPrefix) {
//        super(
//                templateMode, // This processor will apply only to HTML mode
//                dialectPrefix,     // Prefix to be applied to name for matching
//                null,              // No tag name: match any tag name
//                false,             // No prefix to be applied to tag name
//                ATTR_NAME,         // Name of the attribute that will be matched
//                true,              // Apply dialect prefix to attribute name
//                PRECEDENCE,        // Precedence (inside dialect's precedence)
//                true);             // Remove the matched attribute afterwards
////        this.componentsService =componentsService;
        super(templateMode, dialectPrefix, "replace", 100, true);
    }


    public  void  processHtml(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler){
        try{
            super.doProcess(context,tag,attributeName,attributeValue,structureHandler);
        }catch (Exception e){
            e.printStackTrace();
            structureHandler.setAttribute("cms:if","${debug}");
            structureHandler.setBody(e.getMessage(),false);
        }
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {
        String pathStrOrigin=attributeValue;
        if(pathStrOrigin.startsWith("~{")){
            Matcher matcher = rv.matcher(attributeValue);
            while (matcher.find()){
                pathStrOrigin = matcher.group(1);
//                pathStr = attr.replace(":: #fragment","");

            }
        }
        String pathStr=pathStrOrigin;
        if(pathStrOrigin.contains("::") && pathStrOrigin.contains("#") ){
            pathStr= pathStrOrigin.split("::")[0].replace(" ","");
//            htmlId = attributeValue.split("::")[1].replace(" ","").substring(1);
        }


        Path path = Paths.get(CmsConst.WORK_DIR + File.separator + pathStr + ".html");
        if(!path.toFile().exists()){
            if(path.toString().contains("components")){
                String viewName = FilenameUtils.getBasename(path.getFileName().toString());
                String parentPath = Paths.get(pathStr).getParent().toString().substring(1);
                Components components = componentsService.findByViewName(parentPath, viewName);
                if(components!=null){
                    Object data = componentsService.getModel(components);
                    TemplateUtil.convertHtmlAndSave(data,components);
                    processHtml(context,tag,attributeName,attributeValue,structureHandler);
                }else {

                    structureHandler.setAttribute("cms:if","${debug}");
                    structureHandler.setBody("components 文件["+path+"]不存在！",false);
                }
            } else if (path.toString().contains("sheet")){
                String viewName = FilenameUtils.getBasename(path.getFileName().toString());
                Sheet sheet = sheetService.findByViewName(viewName);
                if(sheet!=null){
                    htmlService.convertArticleListBy(sheet);
                    processHtml(context,tag,attributeName,attributeValue,structureHandler);
                }else {
                    structureHandler.setAttribute("cms:if","${debug}");
                    structureHandler.setBody("sheet 文件["+path+"]不存在！",false);
                }
            }else {
//                structureHandler.setAttribute("cms:if","${debug}");
//                structureHandler.setBody("其他文件["+path+"]不存在！",false);
                processHtml(context,tag,attributeName,attributeValue,structureHandler);
            }
        }else {
            processHtml(context,tag,attributeName,attributeValue,structureHandler);

        }
    }
}
