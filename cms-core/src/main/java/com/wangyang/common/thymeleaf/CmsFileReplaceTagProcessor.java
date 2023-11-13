package com.wangyang.common.thymeleaf;

import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.FilenameUtils;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.config.ApplicationBean;
import com.wangyang.pojo.entity.Components;
import com.wangyang.pojo.entity.Sheet;
import com.wangyang.service.templates.IComponentsService;
import com.wangyang.service.IHtmlService;
import com.wangyang.service.ISheetService;
import com.wangyang.service.templates.ComponentsServiceImpl;
import com.wangyang.service.impl.SheetServiceImpl;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangyang
 * @date 2020/12/17
 */

public class CmsFileReplaceTagProcessor extends AbstractAttributeTagProcessor {
    private static final String ATTR_NAME  = "replace";
    private static final int PRECEDENCE = 10000;
    private static String varPattern2 = "\\~\\{(.*)}";
    private static Pattern rv = Pattern.compile(varPattern2);
    private IComponentsService componentsService= ApplicationBean.getBean(ComponentsServiceImpl.class);
    private ISheetService sheetService= ApplicationBean.getBean(SheetServiceImpl.class);
    private IHtmlService htmlService= ApplicationBean.getBean(IHtmlService.class);
    public CmsFileReplaceTagProcessor(TemplateMode templateMode, String dialectPrefix) {
        super(
                templateMode, // This processor will apply only to HTML mode
                dialectPrefix,     // Prefix to be applied to name for matching
                null,              // No tag name: match any tag name
                false,             // No prefix to be applied to tag name
                ATTR_NAME,         // Name of the attribute that will be matched
                true,              // Apply dialect prefix to attribute name
                PRECEDENCE,        // Precedence (inside dialect's precedence)
                true);             // Remove the matched attribute afterwards
//        this.componentsService =componentsService;
    }

    @Override
    protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName, String attributeValue, IElementTagStructureHandler structureHandler) {

        if(tag.hasAttribute("parser")){
            final IEngineConfiguration configuration = context.getConfiguration();
            final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
            if(attributeValue.startsWith("~")){
                Matcher matcher = rv.matcher(attributeValue);
                while (matcher.find()){
                    String attr = matcher.group(1);
                    attr = attr.replace(":: #fragment","");
                    final IStandardExpression expression = parser.parseExpression(context, attr);
                    Object execute = expression.execute(context);
                    if(execute!=null){
                        String path ;
                        if(!attributeValue.contains("#fragment")){
                            path= "~{"+execute.toString()+"} ?:_";
                        }else {
                            path= "~{"+execute.toString()+" :: #fragment} ?:_";
                        }
                        structureHandler.setAttribute("cms:replace",path);
                    }

                }
            }else {
                final IStandardExpression expression = parser.parseExpression(context, attributeValue);
                Object execute = expression.execute(context);
                structureHandler.setAttribute("cms:replace",execute.toString());
            }

       }else {

            String pathStr=attributeValue;
            if(attributeValue.contains("::")){
                pathStr= attributeValue.split("::")[0].replace(" ","");
            }


            Path path = Paths.get(CmsConst.WORK_DIR + File.separator + pathStr + ".html");
            if(!path.toFile().exists() && !pathStr.startsWith("fragment")){
                if(path.toString().contains("components")){
                    String viewName = FilenameUtils.getBasename(path.getFileName().toString());
                    String parentPath = Paths.get(pathStr).getParent().toString().substring(1);
                    Components components = componentsService.findByViewName(parentPath,viewName);
                    if(components!=null){
                        Object data = componentsService.getModel(components);
                        TemplateUtil.convertHtmlAndSave(data,components);
                        structureHandler.setAttribute("cms:replace",attributeValue);

                    }else {
                        structureHandler.setAttribute("cms:if","${debug}");
                        structureHandler.setBody("components 文件["+path+"]不存在！",false);
                    }
                }else if (path.toString().contains("sheet")){
                    String viewName = FilenameUtils.getBasename(path.getFileName().toString());
                    Sheet sheet = sheetService.findByViewName(viewName);
                    if(sheet!=null){
                        htmlService.convertArticleListBy(sheet);
                        structureHandler.setAttribute("cms:replace",attributeValue);
                    }else {
                        structureHandler.setAttribute("cms:if","${debug}");
                        structureHandler.setBody("sheet 文件["+path+"]不存在！",false);
                    }
                }else {
                    structureHandler.setAttribute("cms:if","${debug}");
                    structureHandler.setBody("其他文件["+path+"]不存在！",false);
                }
            }else {
                structureHandler.setAttribute("cms:replace",attributeValue);

            }

       }
    }
}
