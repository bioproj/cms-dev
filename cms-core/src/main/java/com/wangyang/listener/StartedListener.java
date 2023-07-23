package com.wangyang.listener;

import com.wangyang.common.CmsConst;
import com.wangyang.interfaces.ITemplateInit;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.FileUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.entity.Components;
import com.wangyang.pojo.entity.Option;
import com.wangyang.pojo.entity.Tags;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.enums.PropertyEnum;
import com.wangyang.pojo.support.TemplateOption;
import com.wangyang.pojo.support.TemplateOptionMethod;
import com.wangyang.service.authorize.IPermissionService;
import com.wangyang.cache.StringCacheStore;
import com.wangyang.repository.ComponentsRepository;
import com.wangyang.repository.TemplateRepository;
import com.wangyang.service.IComponentsService;
import com.wangyang.service.IOptionService;
import com.wangyang.service.ITagsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

;

@Slf4j
@Configuration
public class StartedListener implements ApplicationListener<ApplicationStartedEvent> {

    @Value("${cms.workDir}")
    private String workDir;

//    @Value("${spring.resources.static-locations}")
//    private String staticResourceLocations;DEFAULT_ARTICLE_PDF_TEMPLATE

    @Autowired
    ComponentsRepository componentsRepository;

    @Autowired
    IComponentsService componentsService;
    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    ITagsService tagsService;

    @Autowired
    IOptionService optionService;
//    @Autowired
//    IUserService userService;
    @Autowired
    StringCacheStore stringCacheStore;


    @Autowired
    IPermissionService permissionService;


    @Autowired(required = false)
    ITemplateInit templateInit;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
//        initCms();
        initDatabase(applicationStartedEvent);
        initRun();
        permissionService.init();
//        if(!isInit()){
//            log.info("### init database!!!");
//            initDatabase(applicationStartedEvent);
//        }else {
//            log.info("### database already init!!!");
//        }

    }
    private  void initRun(){
        // 生成首页
//        List<Components> components = componentsRepository.findAll();
//        components.forEach(component -> {
//            Object o = componentsService.getModel(component);
//            TemplateUtil.convertHtmlAndSave(o, component);
//        });
//        log.info("加载用户权限！！");
//        List<PermissionDto> permissionDtos = permissionService.listAll();
//        permissionDtos.forEach(permissionDto -> {
//            log.info(permissionDto.getUrl()+"需要权限"+permissionDto.getEnName());
//        });
    }
    @Deprecated
    private boolean isInit() {
        String value = Optional.ofNullable(optionService.getValue(CmsConst.INIT_STATUS))
                .orElse("false");
        if(value.equals("true")){
            return true;
        }
        return false;
    }

    private void initDatabase(ApplicationStartedEvent applicationStartedEvent) {

        List<Template> templates = new ArrayList<>();
        if(templateInit!=null){
            List<Template> templatesList = templateInit.templates();
            templates.addAll(SystemTemplates.templates());
            templates.addAll(templatesList);

        }else {
            templates =SystemTemplates.templates();
        }

       List<Tags> tags = Arrays.asList(new Tags(CmsConst.TAGS_INFORMATION,CmsConst.TAGS_INFORMATION),new Tags(CmsConst.TAGS_RECOMMEND,CmsConst.TAGS_RECOMMEND));
       tags.forEach(tag->{
           Tags saveTag = tagsService.add(tag);
       });
//        log.info(">>> init user wangyang");
//        User user = new User();
//        user.setUsername("wangyang");
//        user.setPassword("123456");
//        userService.save(user);
        List<Option> options = new ArrayList<>();

//        ITemplateInit templateInit = applicationStartedEvent.getApplicationContext().getBean(ITemplateInit.class);




         List<Template> findTemplates = templateRepository.findAll();
        Set<String> findTemplateName = ServiceUtil.fetchProperty(findTemplates, Template::getEnName);

        Set<String> templateNames = ServiceUtil.fetchProperty(templates, Template::getEnName);
        Map<String, Template> templateMap = ServiceUtil.convertToMap(templates, Template::getEnName);
        templateNames.removeAll(findTemplateName);
        if(!CollectionUtils.isEmpty(templateNames)){
            templateNames.forEach(name->{
                Template template = templateRepository.save(templateMap.get(name));
//                if(template.getName().equals("DEFAULT_ARTICLE")){
//                    options.add(new Option(PropertyEnum.DEFAULT_ARTICLE_TEMPLATE_ID.getValue(),String.valueOf(template.getId())));
//                }
                File file = new File(workDir+"/"+template.getTemplateValue()+".html");
                if(!file.exists()){
//                    FileUtils.saveFile(file,"空模板");
                    log.info("创建文件:"+file.getName());
                }
                log.info("添加 Template ["+name+"] ");
            });
        }

        List<Components> componentsList = SystemTemplates.components();




        //        componentsList.add( new Components("推荐标签", CMSUtils.getComponentsPath(),"components/@articleListAndVisit","recommendArticle",CmsConst.ARTICLE_DATA_TAGS+"推荐","",true));
//        componentsList.add( new Components("自定义组件","components","自定义HTML内容","myHtml","","",true));

        Map<String,Object> beans = applicationStartedEvent.getApplicationContext().getBeansWithAnnotation(TemplateOption.class);
        beans.forEach((k,v)->{
            Class<?> targetClass = AopUtils.getTargetClass(v);
            Method[] methods = targetClass.getDeclaredMethods();
            for (Method method: methods){
                if(method.isAnnotationPresent(TemplateOptionMethod.class)){
                    Annotation[] annotations = method.getDeclaredAnnotations();
                    for (Annotation annotation: annotations){
                        if(annotation instanceof TemplateOptionMethod){
                            TemplateOptionMethod tm = (TemplateOptionMethod) annotation;
                            String dataName = k+"."+method.getName();
                            String path;
                            if(tm.path().equals("null")){
                                path = CMSUtils.getComponentsPath();
                            }else {
                                path = tm.path();
                            }
                            Components components = new Components(tm.name(), path,tm.templateValue(), tm.viewName(), dataName, tm.event(), tm.status());
                            componentsList.add(components);
//                            componentsService.add(components);
                        }
                    }

                }
            }
        });
        List<Components> findComponents = componentsRepository.findAll();
        Set<String> findName = ServiceUtil.fetchProperty(findComponents, Components::getName);

        Set<String> componentsName = ServiceUtil.fetchProperty(componentsList, Components::getName);
        Map<String, Components> componentsMap = ServiceUtil.convertToMap(componentsList, Components::getName);
        componentsName.removeAll(findName);
        if(!CollectionUtils.isEmpty(componentsName)){
            componentsName.forEach(name->{
                Components components = componentsMap.get(name);
                componentsRepository.save(components);
                log.info("添加 Components ["+name+"] ");

            });
        }


        List<Option> findOption = optionService.list();
        findOption.forEach(option -> {
            stringCacheStore.setValue(option.getKey(),option.getValue());
        });
        Set<String> findOptionName = ServiceUtil.fetchProperty(findOption, Option::getKey);
        Set<String> optionsName = new HashSet<>();
        for (PropertyEnum propertyEnum: PropertyEnum.values()){
            optionsName.add(propertyEnum.name());
        }
        optionsName.removeAll(findOptionName);
        if(!CollectionUtils.isEmpty(optionsName)){
            optionsName.forEach(name->{
                options.add(new Option(name,PropertyEnum.valueOf(name).getDefaultValue(),PropertyEnum.valueOf(name).getName(),PropertyEnum.valueOf(name).getGroupId()));
            });

        }

        if(!CollectionUtils.isEmpty(options)){
            options.forEach(option -> {
                optionService.save(option);
                stringCacheStore.setValue(option.getKey(),option.getValue());
                log.info("添加 option key:"+option.getKey()+",value:"+option.getValue());
            });

        }


//        optionService.save(new Option(CmsConst.INIT_STATUS,"true"));
//        log.info("###! all template init success!!");
    }

    private void initCms(){
        log.info("### WorkDir:"+workDir);
//        log.info("### Static Resource Locations"+staticResourceLocations);
        log.info("### Template Resource Locations"+workDir+"/"+CMSUtils.getTemplates()+"/");
        try {
            // 拷贝配置文件
            Path cmsDir = Paths.get(workDir);
            if(Files.notExists(cmsDir)){
                Files.createDirectories(cmsDir);
                log.info(">>> Not exist work directory, Create template directory "+cmsDir.toString());
                Path propertiesFile = new File(workDir+ File.separator+CmsConst.CONFIGURATION).toPath();

                Path source = FileUtils.getJarResources(CmsConst.CONFIGURATION);
                Files.copy(source,propertiesFile);
                log.info(">>> copy configuration file ["+source.toString()+"] to ["+cmsDir.toString()+"]");
            }

            // 拷贝templates目录
            Path templatePath = Paths.get(workDir + "/" + CMSUtils.getTemplates());
            if(Files.notExists(templatePath)){
                Files.createDirectories(templatePath);
                log.info(">>> Not exist template directory, Create template directory "+templatePath.toString());
                Path source = FileUtils.getJarResources("template");
                FileUtils.copyFolder(source, templatePath);
                log.info(">>> copy ["+source.toString()+"] to ["+templatePath.toString()+"]");
            }

            Path htmlPath = Paths.get(workDir + "/" + CmsConst.STATIC_HTML_PATH);
            if(Files.notExists(htmlPath)){
                Files.createDirectories(htmlPath);
                log.info(">>> Not exits html directory, create html directory");
                Path source = FileUtils.getJarResources(CmsConst.SYSTEM_HTML_PATH);
                FileUtils.copyFolder(source, htmlPath);
                log.info(">>> copy ["+source.toString()+"] to ["+templatePath.toString()+"]");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
