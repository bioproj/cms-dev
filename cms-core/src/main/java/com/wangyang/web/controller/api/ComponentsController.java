package com.wangyang.web.controller.api;

import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.FileUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.common.enums.Lang;
import com.wangyang.service.IComponentsService;
import com.wangyang.pojo.entity.Components;
import com.wangyang.pojo.params.ComponentsParam;
import com.wangyang.common.BaseResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/templatePage")
public class ComponentsController {

    @Autowired
    IComponentsService componentsService;

    @GetMapping
    public Page<Components> list(@PageableDefault(sort = {"name"},direction = DESC) Pageable pageable,@RequestParam(required = false) Lang lang){
        return  componentsService.page(pageable, lang);
    }

    @RequestMapping("/find/{id}")
    public Components findById(@PathVariable("id") Integer id){
        return componentsService.findById(id);
    }

    @PostMapping
    public Components add(@RequestBody  ComponentsParam componentsParam){
        return componentsService.add(componentsParam);
    }





    @PostMapping("/update/{id}")
    public Components update(@PathVariable("id") Integer id,@RequestBody  ComponentsParam componentsParam){
        return componentsService.update(id,componentsParam);
    }
    @PostMapping("/save/{id}")
    public Components saveUpdate(@PathVariable("id") Integer id,@RequestBody  ComponentsParam componentsParam){
        return componentsService.saveUpdate(id,componentsParam);
    }




    @RequestMapping("/delete/{id}")
    public Components delete(@PathVariable("id") Integer id){
        Components components = componentsService.delete(id);
        TemplateUtil.deleteTemplateHtml(components.getViewName(),components.getPath());
        return components;
    }


    @GetMapping("/generate/{id}")
    public BaseResponse generateHtml(@PathVariable("id") Integer id){
        Components components = componentsService.findById(id);
        Object o = componentsService.getModel(components);
        return BaseResponse.ok(TemplateUtil.convertHtmlAndSave(o, components));
    }

    @GetMapping("/fetchComponents")
    public List<Components> fetchComponents(@RequestParam(required = false) String path){
        String workDir = CMSUtils.getWorkDir();
        String componentsDir;
        if(path!=null && !path.equals("")){
            componentsDir=workDir+ File.separator+CMSUtils.getTemplates()+path;
        }else{
            componentsDir=workDir+ File.separator+CMSUtils.getTemplates()+"components";
        }
        List<String> fileNames = FileUtils.getFileNames(componentsDir);
        List<Components> components = componentsService.listAll();
        Set<String> templateValue = ServiceUtil.fetchProperty(components, Components::getTemplateValue);
        Set<String> filterFileNames = fileNames.stream().filter(item -> {
            return !templateValue.contains("components"+File.separator+item.split("\\.")[0]) && !item.endsWith("bak") && !item.contains(" ") && !item.endsWith(".en.html");
        }).collect(Collectors.toSet());

        if(filterFileNames.size()==0){
            throw new ObjectException("模板中没有新的文件！！");
        }

        List<Components> componentsList = new ArrayList<>();
        filterFileNames.forEach(item->{
            String name = item.replace("@", "").replace(".html", "").replace(" ","_");
            String viewPath = "components"+File.separator+item.replace(".html", "");
            componentsList.add( new Components(name, CMSUtils.getComponentsPath(), viewPath,name,CmsConst.ARTICLE_DATA,"",true,false));

        });

        List<Components> saveAll = componentsService.saveAll(componentsList);

        return saveAll;
    }


    @GetMapping("/installLanguage")
    public List<Components> installLanguage(@RequestParam(required = false) String path){
        String workDir = CMSUtils.getWorkDir();
        String componentsDir;
        if(path!=null && !path.equals("")){
            componentsDir=workDir+ File.separator+CMSUtils.getTemplates()+path;
        }else{
            componentsDir=workDir+ File.separator+CMSUtils.getTemplates()+"components";
        }
        List<String> fileNames = FileUtils.getFileNames(componentsDir);
        List<Components> components = componentsService.listAll();

        Set<String> templateValue = ServiceUtil.fetchProperty(components, Components::getTemplateValue);

        Set<String> filterFileNames = fileNames.stream().filter(item -> {
            return !templateValue.contains("components"+File.separator+item.replace(".html","")) && !item.endsWith("bak") && !item.contains(" ");
        }).collect(Collectors.toSet());

        List<Components> componentsList = new ArrayList<>();
        for (Components component : components){
            String en = component.getTemplateValue().replace("components/","") + "."+Lang.EN.getSuffix()+".html";
            if(filterFileNames.contains(en)){
                Components components1 = createComponents(component, null);
                componentsList.add(components1);
            }
        }



        if(componentsList.size()==0){
            throw new ObjectException("模板中没有新的文件！！");
        }



        List<Components> saveAll = componentsService.saveAll(componentsList);

        return saveAll;
    }

    public Components createComponents(Components component,String componentsDir){
        if(componentsDir!=null){
//            List<String> fileNames = FileUtils.getFileNames(componentsDir);
            Path path = Paths.get(componentsDir, component.getTemplateValue() + ".html");
            Path targetPath = Paths.get(componentsDir, component.getTemplateValue() + "." + Lang.EN.getSuffix() + ".html");
            if(path.toFile().exists() && !targetPath.toFile().exists()){
                try {
                    FileUtils.copyFolder(path,targetPath);
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }

        Components newComponents = new Components();
        BeanUtils.copyProperties(component,newComponents,"id");

        newComponents.setLangSource(component.getId());
        newComponents.setId(null);
        newComponents.setPath(component.getPath().replace("html",Lang.EN.getSuffix()));
        newComponents.setViewName(component.getViewName());
        newComponents.setTemplateValue(component.getTemplateValue()+"."+Lang.EN.getSuffix());
        newComponents.setName(component.getName()+"."+Lang.EN.getSuffix());
        newComponents.setIsSystem(false);
        newComponents.setLang(Lang.EN);
        return newComponents;

    }




    @GetMapping("/createComponentsLanguage/{id}")
    public Components createComponentsLanguage(@PathVariable("id") Integer id, @RequestParam(defaultValue = "EN") Lang lang){
        String workDir = CMSUtils.getWorkDir();
        String componentsDir=workDir+ File.separator+CMSUtils.getTemplates();;

        Components components = componentsService.findById(id);

        if(components.getLang()==null){
            components.setLang(Lang.ZH);
            componentsService.save(components);
        }
        if(components.getLang().equals(lang)){
            throw new ObjectException(components.getName()+"该组件已经是"+lang.getSuffix()+"的了！！！");
        }
        Components langComponents = componentsService.findByLang(components.getId(), lang);

        if(langComponents!=null){
            throw new ObjectException(langComponents.getName()+"已经创建了英文！！！");
        }

        Components newComponents = createComponents(components, componentsDir);
        Components save = componentsService.save(newComponents);
        return save;
    }



    @GetMapping("/createAllLanguage")
    public List<Components> createAllLanguage(@RequestParam(required = false) String path){

        String workDir = CMSUtils.getWorkDir();
        String componentsDir;
        if(path!=null && !path.equals("")){
            componentsDir=workDir+ File.separator+CMSUtils.getTemplates()+path;
        }else{
            componentsDir=workDir+ File.separator+CMSUtils.getTemplates();
        }

        componentsService.listAll().forEach(comp -> {
            if(comp.getLang()==null){
                comp.setLang(Lang.ZH);
                componentsService.save(comp);
            }
        });


        List<Components> componentsZH = componentsService.listAll(Lang.ZH);
        List<Components> componentsEN = componentsService.listAll(Lang.EN);
        Set<String> templateValue = ServiceUtil.fetchProperty(componentsEN, Components::getTemplateValue);

        List<Components> componentsSet = componentsZH.stream().filter(item -> {
            return !templateValue.contains(item.getTemplateValue()+"."+Lang.EN.getSuffix());
        }).collect(Collectors.toList());

        if(componentsSet.size()==0){
            throw new ObjectException(componentsZH.size()+"个中文模板创建了"+componentsEN.size()+"个英文模板");
        }

        List<Components> componentsList = new ArrayList<>();
        for (Components comp : componentsSet){
            Components components = createComponents(comp, componentsDir);
            componentsList.add(components);
        }


        List<Components> saveAll = componentsService.saveAll(componentsList);

        return saveAll;
    }

    @GetMapping("/listNeedArticle")
    public List<Components> listNeedArticle(){
        return componentsService.listNeedArticle();
    }


    @GetMapping("/findDetailsById/{id}")
    public Components findDetailsById(@PathVariable("id") Integer id){
        return componentsService.findDetailsById(id);
    }
}
