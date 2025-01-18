package com.wangyang.common.utils;

import com.wangyang.common.thymeleaf.CmsFileDialect;
import com.wangyang.common.thymeleaf.CmsWebDialect;
import com.wangyang.common.thymeleaf.TestStandardDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author wangyang
 * @date 2020/12/14
 */
public class HtmlTemplateEngine {

    private static TemplateEngine templateWebEngine ;
    private static TemplateEngine templateEngineFile ;
    /**
     * https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#chaining-template-resolvers
     * @param prefix
     * @param suffix
     * @return templateEngine
     */
    public static TemplateEngine getFileInstance(String prefix, String suffix,String templatePath){
        if(templateEngineFile==null){
            templateEngineFile = new SpringTemplateEngine();
            templateEngineFile.addDialect(new CmsFileDialect());

            FileTemplateResolver fileTemplateResolver2 = new FileTemplateResolver();
            fileTemplateResolver2.setOrder(Integer.valueOf(1));
            fileTemplateResolver2.setCacheable(false);
            fileTemplateResolver2.setPrefix(prefix+File.separator+ templatePath);
            fileTemplateResolver2.setSuffix(suffix);
            fileTemplateResolver2.getResolvablePatternSpec().addPattern("fragment/*");


            templateEngineFile.addTemplateResolver(fileTemplateResolver2);


            FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
//            templateEngineFile.addDialect(new CmsDialect(null));
            fileTemplateResolver.setOrder(Integer.valueOf(2));
            fileTemplateResolver.setCacheable(false);
            fileTemplateResolver.setPrefix(prefix+ File.separator);
            fileTemplateResolver.setSuffix(suffix);

            ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
//            classLoaderTemplateResolver.setPrefix("mytemplates/");
            classLoaderTemplateResolver.setCacheable(false);
            classLoaderTemplateResolver.setSuffix(".html");
//            classLoaderTemplateResolver.setTemplateMode("HTML5");
            classLoaderTemplateResolver.setCharacterEncoding("UTF-8");
            classLoaderTemplateResolver.setOrder(Integer.valueOf(1));
            classLoaderTemplateResolver.setCacheable(false);
            classLoaderTemplateResolver.getResolvablePatternSpec().addPattern("internal_template/*");
            templateEngineFile.addTemplateResolver(classLoaderTemplateResolver);


//            fileTemplateResolver.getResolvablePatternSpec().addPattern("templates/*");
//            fileTemplateResolver.getResolvablePatternSpec().addPattern("html/*");
//            StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();

//            stringTemplateResolver.setOrder(Integer.valueOf(2));
//            stringTemplateResolver.getResolvablePatternSpec().addPattern("str:");
            // 添加字符串模板
//            templateEngineFile.addTemplateResolver(stringTemplateResolver);
            templateEngineFile.addTemplateResolver(fileTemplateResolver);
        }
        return templateEngineFile;
    }

    public static TemplateEngine getWebInstance(String prefix, String suffix,String templatePath){
        if(templateWebEngine==null){
            templateWebEngine = new SpringTemplateEngine();
//            templateWebEngine.addDialect(new CmsWebDialect());
//            templateWebEngine.addDialect(new TestStandardDialect());
            templateWebEngine.setDialect(new TestStandardDialect());
            templateWebEngine.setDialect(new StandardDialect());
//            StandardDialect standardDialect = new StandardDialect();
//            TestStandardDialect testStandardDialect = new TestStandardDialect();
//            Map<String,IDialect> map =new HashMap<>();
//            map.put("cms",testStandardDialect );
//            map.put("th",standardDialect);
//            templateWebEngine.setDialectsByPrefix(map);
//            Set<IDialect> set = new HashSet<>();
//            set.add(testStandardDialect);
//            templateWebEngine.setAdditionalDialects(set);

//            Set<IDialect> dialects = templateWebEngine.getDialects();


//            templateWebEngine.addDialect(new CmsDialect(prefix));
//            templateWebEngine.setDialect(new CmsWebDialect());



            FileTemplateResolver fileTemplateResolver2 = new FileTemplateResolver();
//            templateEngineFile.addDialect(new CmsDialect(null));
            fileTemplateResolver2.setOrder(Integer.valueOf(1));
            fileTemplateResolver2.setCacheable(false);
            fileTemplateResolver2.setPrefix(prefix+File.separator+ templatePath);
            fileTemplateResolver2.setSuffix(suffix);
            fileTemplateResolver2.getResolvablePatternSpec().addPattern("fragment/*");



            FileTemplateResolver fileTemplateResolver = new FileTemplateResolver();
            fileTemplateResolver.setOrder(Integer.valueOf(2));
            fileTemplateResolver.setCacheable(false);
            fileTemplateResolver.setPrefix(prefix+ File.separator);
            fileTemplateResolver.setSuffix(suffix);

            templateWebEngine.addTemplateResolver(fileTemplateResolver2);

            ClassLoaderTemplateResolver classLoaderTemplateResolver = new ClassLoaderTemplateResolver();
//            classLoaderTemplateResolver.setPrefix("mytemplates/");
            classLoaderTemplateResolver.setCacheable(false);
            classLoaderTemplateResolver.setSuffix(".html");
//            classLoaderTemplateResolver.setTemplateMode("HTML5");
            classLoaderTemplateResolver.setCharacterEncoding("UTF-8");
            classLoaderTemplateResolver.setOrder(Integer.valueOf(1));
            classLoaderTemplateResolver.setCacheable(false);
            classLoaderTemplateResolver.getResolvablePatternSpec().addPattern("internal_template/*");
            templateWebEngine.addTemplateResolver(classLoaderTemplateResolver);





//            fileTemplateResolver.getResolvablePatternSpec().addPattern("html/*");
//            StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
//
//            stringTemplateResolver.setOrder(Integer.valueOf(2));
//            stringTemplateResolver.getResolvablePatternSpec().addPattern("str:");
            // 添加字符串模板
//            templateWebEngine.addTemplateResolver(stringTemplateResolver);
            templateWebEngine.addTemplateResolver(fileTemplateResolver);
        }
        return templateWebEngine;
    }
}
