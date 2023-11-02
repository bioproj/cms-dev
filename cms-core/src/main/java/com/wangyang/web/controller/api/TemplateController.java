package com.wangyang.web.controller.api;

import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.FileUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.annotation.Anonymous;
import com.wangyang.pojo.entity.TemplateChild;
import com.wangyang.common.enums.Lang;
import com.wangyang.pojo.enums.TemplateData;
import com.wangyang.service.IHtmlService;
import com.wangyang.service.templates.ITemplateService;
import com.wangyang.pojo.enums.TemplateType;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.params.TemplateParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/template")
public class TemplateController {
    @Autowired
    ITemplateService templateService;

    @Autowired
    IHtmlService htmlService;


    public List<String> listTemplateType(){
        return null;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @RequestPart("file")
    @Anonymous
    public Template upload(@RequestPart("file") MultipartFile file){
        Template template = templateService.addZipFile(file);
        return  template;
    }
    /**
     * 根据template类型获取Template
     * @param type
     * @return
     */
    @GetMapping("/find/{type}")
    public List<Template> findByType(@PathVariable("type") TemplateType type){

        return templateService.findByTemplateType(type);
    }

    @PostMapping("/update/{id}")
    public Template update(@PathVariable("id") Integer id, @RequestBody TemplateParam templateParam){
        return templateService.update(id,templateParam);
    }

    @GetMapping("/findDetailsById/{id}")
    public Template findDetailsById(@PathVariable("id") Integer id){
        return templateService.findDetailsById(id);
    }


    @GetMapping
    public Page<Template> list(@PageableDefault(sort = {"id"},direction = DESC)Pageable pageable,Template template){
//        Page<Template> templatePage = templateService.list(pageable,lang);
        Page<Template> templates = templateService.pageBy(pageable, template);

        return templates;
    }

    @GetMapping("/tree/{id}")
    public Template setStatus(@PathVariable("id") int id){
        Template template = templateService.tree(id);

//        Template template = templateService.setStatus(id);
//        htmlService.generateHome();
        return template;
    }

    @PostMapping
    public Template add(@RequestBody Template templateInput){
        Template template = templateService.findByEnNameReturnNUll(templateInput.getEnName());
        if(template!=null){
            throw new ObjectException(template.getEnName()+"已经存在!!");
        }
        template.setIsSystem(false);
        template = templateService.add(templateInput);
        File file = new File(CMSUtils.getWorkDir()+File.separator+CMSUtils.getTemplates() +template.getTemplateValue()+".html");
        if(!file.exists()){
            FileUtils.saveFile(file,"空模板 for" + templateInput.getTemplateValue());
        }
        return template;
    }

    @GetMapping("/delete/{id}")
    public Template delete(@PathVariable("id") Integer id){

        return templateService.deleteById(id);
    }


    @GetMapping("/updateAllTemplate")
    public List<Template> updateAllTemplate(){
        List<Template> templates = templateService.findAll();
//        for (Template template : templates){
//            if(template.getTemplateType().equals(TemplateType.CATEGORY)){
//                if(template.getTree()){
//                    template.setTemplateData(TemplateData.ARTICLE_TREE);
//                }else {
//                    template.setTemplateData(TemplateData.ARTICLE_PAGE);
//                }
//            }else {
//                template.setTemplateData(TemplateData.OTHER);
//            }
//        }
        List<Template> saveAll = templateService.saveAll(templates);
        return saveAll;
    }
    @GetMapping("/addChildById/{id}")
    public TemplateChild addChildById(@PathVariable("id") Integer id,@RequestParam(required = true)Integer childId){

        TemplateChild templateChild = templateService.addChild(id, childId);
        return templateChild;
    }
    @GetMapping("/addChild/{id}")
    public TemplateChild addChild(@PathVariable("id") Integer id,@RequestParam(required = true)String enName){
        TemplateChild templateChild = templateService.addChild(id, enName);
        return templateChild;
    }
    @GetMapping("/findByChild/{id}")
    public List<Template> findByChild(@PathVariable("id") Integer id){
        List<Template> templates = templateService.findByChild(id);
        return templates;
    }

    @GetMapping("/removeChildTemplate")
    public TemplateChild removeChildTemplate(@RequestParam Integer templateId,@RequestParam Integer templateChildId){
        TemplateChild templateChild = templateService.removeChildTemplate(templateId,templateChildId);
        return templateChild;
    }
    @GetMapping("/fetchTemplates")
    public List<Template> fetchTemplates(@RequestParam(required = false) String path){
        String workDir = CMSUtils.getWorkDir();
        String componentsDir;
        if(path!=null && !path.equals("")){
            componentsDir=workDir+ File.separator+CMSUtils.getTemplates()+path;
        }else{
            componentsDir=workDir+ File.separator+CMSUtils.getTemplates()+"templates";
        }
        Path componentsPath = Paths.get(componentsDir);
        if(!componentsPath.toFile().exists()){
            try {
                Files.createDirectories(componentsPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        List<String> fileNames = FileUtils.getFileNames(componentsDir);
        List<Template> components = templateService.findAll();
        Set<String> templateValue = ServiceUtil.fetchProperty(components, Template::getTemplateValue);
        Set<String> filterFileNames = fileNames.stream().filter(item -> {
            return !templateValue.contains("templates"+File.separator+item.split("\\.")[0]) && !item.endsWith("bak") && !item.contains(" ") && !item.endsWith(".en.html");
        }).collect(Collectors.toSet());

        if(filterFileNames.size()==0){
            throw new ObjectException("模板中没有新的文件！！");
        }

        List<Template> templateList = new ArrayList<>();
        filterFileNames.forEach(item->{
            String name = item.replace("@", "").replace(".html", "").replace(" ","_");
            String viewPath = "templates"+File.separator+item.replace(".html", "");
            templateList.add(new Template(name,name.replace("/","_"),viewPath, TemplateType.ARTICLE,false));

        });

        List<Template> saveAll = templateService.saveAll(templateList);

        return saveAll;
    }

//    @GetMapping("/createAllLanguage")
//    public List<Template> createAllLanguage(@RequestParam(required = false) String path){
//        String workDir = CMSUtils.getWorkDir();
//        String componentsDir;
//        if(path!=null && !path.equals("")){
//            componentsDir=workDir+ File.separator+CMSUtils.getTemplates()+path;
//        }else{
//            componentsDir=workDir+ File.separator+CMSUtils.getTemplates();
//        }
//
//
//        templateService.listAll().forEach(template -> {
//            if(template.getLang()==null){
//                template.setLang(Lang.ZH);
//                templateService.save(template);
//            }
//            if(template.getPath()==null){
//                template.setPath("");
//                templateService.save(template);
//            }
//        });
//        List<Template> templatesZH = templateService.listAll(Lang.ZH);
//        List<Template> templatesEN = templateService.listAll(Lang.EN);
//        Set<String> templateValue = ServiceUtil.fetchProperty(templatesEN, Template::getTemplateValue);
//
//
////        List<Template> templateSet =  new ArrayList<>();
////        for (Template template : templatesZH){
////            if(!templateValue.contains(template.getTemplateValue()+"."+Lang.EN.getSuffix())){
////                templateSet.add(template);
////            }
////        }
//        List<Template> templateSet =  templatesZH.stream().filter(item -> {
//            return !templateValue.contains(item.getTemplateValue()+"."+Lang.EN.getSuffix());
//        }).collect(Collectors.toList());
//
//        if(templateSet.size()==0){
//            throw new ObjectException(templatesZH.size()+"个中文模板创建了"+templatesEN.size()+"个英文模板");
//        }
//
//        List<Template> templateList = new ArrayList<>();
//        for (Template template : templateSet){
//            Template template1 = createTemplate(template, componentsDir);
//            templateList.add(template1);
//        }
//
////
//        List<Template> saveAll = templateService.saveAll(templateList);
//
//        return saveAll;
//    }



    @GetMapping("/createTemplateLanguage/{id}")
    public Template createTemplateLanguage(@PathVariable("id") Integer id, @RequestParam(defaultValue = "EN") Lang lang){
        Template templateLanguage = templateService.createTemplateLanguage(id, lang);
        return templateLanguage;
    }









//    public void deleteById(Integer id){
//        templateService.deleteById(id);
//    }
}
