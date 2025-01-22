package com.wangyang.service.templates;


import com.alibaba.fastjson.JSONObject;
import com.wangyang.common.CmsConst;
import com.wangyang.common.enums.CrudType;
import com.wangyang.common.enums.Lang;
import com.wangyang.common.exception.FileOperationException;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.FileUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.dto.FileDTO;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.enums.*;
import com.wangyang.pojo.params.TemplateParam;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.repository.template.TemplateChildRepository;
import com.wangyang.service.ICategoryTemplateService;
import com.wangyang.service.authorize.IArticleAttachmentService;
import com.wangyang.repository.template.TemplateRepository;
import com.wangyang.service.IAttachmentService;
import com.wangyang.service.base.AbstractBaseTemplateServiceImpl;
import com.wangyang.util.ZipHelper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TemplateServiceImpl  extends AbstractBaseTemplateServiceImpl<Template,Template, BaseVo> implements ITemplateService {




    @Autowired
    IAttachmentService attachmentService;

    @Autowired
    IArticleAttachmentService articleAttachmentService;

    @Autowired
    TemplateChildRepository templateChildRepository;

    @Autowired
    ICategoryTemplateService categoryTemplateService;

    @Value("${cms.workDir}")
    private String workDir;

    private static String varPattern2 = "(.*)";
    private static Pattern rv = Pattern.compile(varPattern2);

    private final static  String INSTALL_TEMPLATE_PATH = "templates/install";
    private final static  Pattern css = Pattern.compile("href=\"(.*)\\.css");
    private final static  Pattern js = Pattern.compile("src=\"(.*)\\.js");
    @Autowired
    TemplateRepository templateRepository;
    public TemplateServiceImpl(TemplateRepository templateRepository) {
        super(templateRepository);
        this.templateRepository =templateRepository;
    }

    public List<String> getLink(Pattern pattern,String content,String suffix){
        List<String> links = new ArrayList<>();
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()){
            String attr = matcher.group(1);
            links.add(attr+suffix);
        }
        return links;
    }

    public Template addCssAndJs(Template template){
        if(template.getTemplateContent()!=null){
            List<String> cssSet = getLink(css, template.getTemplateContent(),".css");
            List<String> jsSet = getLink(js, template.getTemplateContent(),".js");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("css",cssSet);
            jsonObject.put("js",jsSet);

            template.setResource(jsonObject.toJSONString());
            Document html = Jsoup.parse(template.getTemplateContent());
//         <div  th:utext="${view.formatContent}">
            Elements formatContent = html.body().getElementsByAttributeValueContaining("th:utext", "formatContent");
            String formatContentHtml = formatContent.html();
            template.setBase(formatContentHtml);
        }

        return template;
    }

    @Override
    public Template save(Template template){

        addCssAndJs(template);
        return templateRepository.save(template);
    }






    @Override
    public Template add(Template template) {
//        convert(template,template);
        createFile(template);
        return save(template);
    }






    @Override
    public Optional<Template> findOptionalById(int id){
        return templateRepository.findById(id);
    }
    @Override
    public List<Template> saveAll(List<Template> templates) {
        templates.forEach(template -> addCssAndJs(template));
        return templateRepository.saveAll(templates);
    }

    @Override
    public Template update(int id, TemplateParam templateParam) {
        Template template = findById(id);
        BeanUtils.copyProperties(templateParam,template);
//        convert(template,templateParam);
        if(template.getTemplateContent()!=null){
            createFile(template);
        }
        Template saveTemplate  = save(template);
        List<CategoryTemplate> categoryTemplates = categoryTemplateService.listByTemplateId(template.getId());
        categoryTemplates.forEach(categoryTemplate -> {
            categoryTemplate.setTemplateType(saveTemplate.getTemplateType());
            categoryTemplateService.save(categoryTemplate);
        });

        return saveTemplate;
    }
    private void createFile(Template template) {
        File file = new File(workDir+"/"+template.getTemplateValue()+".html");

        FileUtils.saveFile(file,template.getTemplateContent());
    }
//    private void convert(Template template, TemplateParam templateParam){
//        BeanUtils.copyProperties(templateParam,template,"templateValue");
//        String templateValue =templateParam.getTemplateValue();
//        //判断是文件还是内容
//        if(templateValue.startsWith("templates")){
//            String templateValueName = templateValue.split("\n")[0];
//            String path = CmsConst.WORK_DIR+"/"+templateValueName+".html";
//            File file = new File(path);
//            String fileTemplateValue = templateParam.getTemplateValue();
//            template.setTemplateValue(templateValueName);
//            String replaceFileTemplateValue = fileTemplateValue.replace(templateValueName+"\n", "");
//            FileUtils.saveFile(file,replaceFileTemplateValue);
//        }else {
//            template.setTemplateValue(templateParam.getTemplateValue());
//        }
//    }

    @Override
    public Template findDetailsById(int id){
        Template template = findById(id);
        String templateValue = template.getTemplateValue();
        String path = CmsConst.WORK_DIR+"/"+templateValue+".html";
        File file = new File(path);
        if(file.exists()){
            String openFile = FileUtils.openFile(file);
            template.setTemplateContent(openFile);
        }
        return template;
    }


    @Override
    public List<Template> listAll() {
        return templateRepository.findAll();
    }

    @Override
    public List<Template> listAll(Lang lang){
        List<Template> components = templateRepository.findAll(new Specification<Template>() {
            @Override
            public Predicate toPredicate(Root<Template> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("lang"),lang)).getRestriction();
            }
        });
        return components;
    }
    @Override
    public List<Template> findAll(){
        return templateRepository.findAll();
    }

    @Override
    public Template deleteById(int id) {
        Template template = findById(id);
        if(template.getIsSystem()!=null && template.getIsSystem()){
            throw new ObjectException("系统模板不能删除！");
        }
        templateRepository.deleteById(id);
        List<ArticleAttachment> articleAttachments = articleAttachmentService.findByTemplateId(template.getId());
        for (ArticleAttachment articleAttachment : articleAttachments){
            File file = new File(articleAttachment.getPath());
            if(file.exists()) ZipHelper.deleteFile(file);
        }
        articleAttachmentService.deleteAll(articleAttachments);


        List<CategoryTemplate> categoryTemplates = categoryTemplateService.listByTemplateId(template.getId());
        categoryTemplateService.deleteAll(categoryTemplates);
        return template;
    }

    @Override
    public Template findById(int id) {
        Optional<Template> templateOptional = templateRepository.findById(id);
        if(!templateOptional.isPresent()){
            throw  new ObjectException("Not found template");
        }
        return templateOptional.get();
    }

    @Override
    public Page<Template> list(Pageable pageable,Lang lang) {
        if(lang!=null && !lang.equals("")){
            return templateRepository.findAll(new Specification<Template>() {
                @Override
                public Predicate toPredicate(Root<Template> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                    return query.where(criteriaBuilder.equal(root.get("lang"),lang)).getRestriction();
                }
            }, pageable);
        }else {
            return templateRepository.findAll(pageable);
        }


    }


    @Override
    public List<Template> findByTemplateType(TemplateType templateType) {
        Specification<Template> specification = new Specification<Template>() {
            @Override
            public Predicate toPredicate(Root<Template> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(
                        criteriaBuilder.equal(root.get("templateType"),templateType)).getRestriction();
            }
        };
        return templateRepository.findAll(specification, Sort.by(Sort.Order.asc("order")));

    }


    @Override
    public List<Template> listByAndStatusTrue(TemplateType templateType){
        Specification<Template> specification = new Specification<Template>() {
            @Override
            public Predicate toPredicate(Root<Template> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.isTrue(root.get("status")),
                                        criteriaBuilder.equal(root.get("templateType"),templateType)).getRestriction();
            }
        };
        return templateRepository.findAll(specification, Sort.by(Sort.Order.asc("order")));
    }
    @Override
    public Template findByEnNameReturnNUll(String enName){
        List<Template> templates = templateRepository.findAll(new Specification<Template>() {
            @Override
            public Predicate toPredicate(Root<Template> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("enName"),enName)).getRestriction();
            }
        });
        if(templates.size()==0){
           return null;
        }
        return templates.get(0);
    }
    @Override
    public Template findByEnNameNull(String enName) {

        List<Template> templates = templateRepository.findAll(new Specification<Template>() {
            @Override
            public Predicate toPredicate(Root<Template> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("enName"),enName)).getRestriction();
            }
        });
        if(templates.size()==0)return null;
        return templates.get(0);
    }
    @Override
    public Template findByEnName(String enName){

        List<Template> templates = templateRepository.findAll(new Specification<Template>() {
            @Override
            public Predicate toPredicate(Root<Template> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("enName"),enName)).getRestriction();
            }
        });
        if(templates.size()==0){
            throw new ObjectException(enName+"Template模板没有找到!!!");
        }
        return templates.get(0);
    }

    @Override
    public Template findOptionalByEnName(String enName){
        List<Template> templates = templateRepository.findAll(new Specification<Template>() {
            @Override
            public Predicate toPredicate(Root<Template> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("enName"),enName)).getRestriction();
            }
        });
        if(templates.size()==0){
            throw new ObjectException(enName+"不存在！！！");
        }
        return templates.get(0);
    }



    @Override
    public void deleteAll() {
        templateRepository.deleteAll();
    }

    @Override
    public boolean supportType(CrudType type) {
        return false;
    }


//    @Override
//    public Template setStatus(int id){
//        Template template = findById(id);
//        if(template.getStatus()){
//            template.setStatus(false);
//        }else {
//            List<CategoryDto> categoryDtos = categoryService.listBy(template.getEnName());
//            if(categoryDtos.size()==0){
//                throw new OptionException("不能启用"+template.getName()+"在首页,因为该模板下没有分类!");
//            }
//            template.setStatus(true);
//        }
//        return templateRepository.save(template);
//    }

    @Override
    public Template addZipFile(MultipartFile uploadFile) {
        String path = "testUpload/"+uploadFile.getOriginalFilename();
        Attachment attachment = attachmentService.upload(uploadFile, path, FileWriteType.COVER, AttachmentStoreType.LOCAL);
        String filePath = attachment.getPath();
        // 上传压缩文件文件
        File file = new File(workDir+File.separator+filePath);
        if(!file.exists())throw new FileOperationException("文件不存在:"+file.getName());

        String destDirPath = file.getAbsolutePath().replace(".zip", "");
        ZipHelper.zipUncompress(file,destDirPath);

        //配置文件
        String baseTemplateInstall = workDir+File.separator+INSTALL_TEMPLATE_PATH;
        File propertiesFile = new File(destDirPath+File.separator+"template.properties");
        if(!propertiesFile.exists()){
            throw new FileOperationException("template.properties不存在！");
        }
        Properties properties = ZipHelper.getProperties(propertiesFile);
        String enName = properties.getProperty("enName");
        String name = properties.getProperty("name");
        String templateTypeStr = properties.getProperty("templateType");


        Template template = templateRepository.findByEnName(enName);
        if(template==null){
            template = new Template();
        }
        String templateName = INSTALL_TEMPLATE_PATH+File.separator+name+File.separator+"index";
        template.setTemplateValue(templateName);
        template.setEnName(enName);
        template.setName(name);
        TemplateType templateType = TemplateType.valueOf(templateTypeStr);
        template.setTemplateType(templateType);

        File templateFile = new File(destDirPath+File.separator+"index.html");
        //修改链接路径
        if(templateFile.exists()){
            String openFile = FileUtils.openFile(templateFile);
            openFile = openFile.replace("css/","/"+INSTALL_TEMPLATE_PATH+"/css/");
            openFile = openFile.replace("js/","/"+INSTALL_TEMPLATE_PATH+"/js/");
            openFile = openFile.replace("fonts/","/"+INSTALL_TEMPLATE_PATH+"/fonts/");
            openFile = openFile.replace("images/","/"+INSTALL_TEMPLATE_PATH+"/images/");
            template.setTemplateContent(openFile);
            FileUtils.saveFile(templateFile,template.getTemplateContent());
        }
        template = save(template);



        //解压文件
        List<FileDTO> listPath = ZipHelper.listPath(destDirPath);
        List<ArticleAttachment> articleAttachmentList = new ArrayList<>();
        for(FileDTO fileDTO : listPath){
            File fromFile = new File(fileDTO.getAbsolutePath());
            String toPath;
            if(fileDTO.getAbsolutePath().contains("css")){
                toPath=baseTemplateInstall+File.separator+"css"+ File.separator+fromFile.getName();
            }else if(fileDTO.getAbsolutePath().contains("js")){
                toPath=baseTemplateInstall+File.separator+"js"+ File.separator+fromFile.getName();
            }else if(fileDTO.getAbsolutePath().contains("fonts")){
                toPath=baseTemplateInstall+File.separator+"fonts"+ File.separator+fromFile.getName();
            }else if(fileDTO.getAbsolutePath().contains("images")){
                toPath=baseTemplateInstall+File.separator+"images"+ File.separator+fromFile.getName();
            }else {
                toPath=baseTemplateInstall+File.separator+name+File.separator+fromFile.getName();
            }
            File toFile=new File(toPath);

            try {
                Files.createDirectories(toFile.getParentFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            fromFile.renameTo(toFile);

            ArticleAttachment articleAttachment = new ArticleAttachment();
            articleAttachment.setPath(toFile.getAbsolutePath());
            articleAttachment.setTemplateId(template.getId());
            articleAttachmentList.add(articleAttachment);
        }
        List<ArticleAttachment> articleAttachments = articleAttachmentService.findByTemplateId(template.getId());
        articleAttachmentService.deleteAll(articleAttachments);
        articleAttachmentService.saveAll(articleAttachmentList);

        ZipHelper.deleteFile(file);
        ZipHelper.deleteFile(new File(destDirPath));
        return template;
    }

    @Override
    public Template tree(int id) {
        Template template = findById(id);
        if(template.getTree()){
            template.setTree(false);
        }else {
            template.setTree(true);
        }
        Template save = templateRepository.save(template);
        return save;
    }

    @Override
    public TemplateChild addChild(Integer id, String enName) {
        Template template = findById(id);
        Template template2= findByEnName(enName);
        TemplateChild findTemplateChild = templateChildRepository.findByTemplateIdAndTemplateChildId(template.getId(), template2.getId());
        if(findTemplateChild!=null){
            throw new ObjectException(template2.getName()+"已经是"+template.getName()+"的子类了！！");
        }
        TemplateChild templateChild = new TemplateChild();
        templateChild.setTemplateId(template.getId());
        templateChild.setTemplateChildId(template2.getId());
        templateChildRepository.save(templateChild);
        return templateChild;
    }

    @Override
    public TemplateChild addChild(Integer id, Integer childId) {
        Template template = findById(id);
        Template template2= findById(childId);
        TemplateChild findTemplateChild = templateChildRepository.findByTemplateIdAndTemplateChildId(template.getId(), template2.getId());
        if(findTemplateChild!=null){
            throw new ObjectException(template2.getName()+"已经是"+template.getName()+"的子类了！！");
        }
        TemplateChild templateChild = new TemplateChild();
        templateChild.setTemplateId(template.getId());
        templateChild.setTemplateChildId(template2.getId());
        templateChildRepository.save(templateChild);
        return templateChild;
    }


    @Override
    public List<Template> findByChild(Integer id) {
        List<TemplateChild> childList = templateChildRepository.findByTemplateId(id);
        Set<Integer> ids = ServiceUtil.fetchProperty(childList, TemplateChild::getTemplateChildId);
        List<Template> templates = templateRepository.findAllById(ids);
        return templates;
    }

    @Override
    public TemplateChild removeChildTemplate(Integer templateId,Integer templateChildId) {
        TemplateChild templateChild = templateChildRepository.findByTemplateIdAndTemplateChildId(templateId, templateChildId);
        if(templateChild==null){
            throw new ObjectException("要删除的对象不存在！！");
        }

        templateChildRepository.delete(templateChild);
        return templateChild;
    }

    private Template createTemplate(Template template, String componentsDir) {
        if(componentsDir!=null){
//            List<String> fileNames = FileUtils.getFileNames(componentsDir);
            Path path = Paths.get(componentsDir, template.getTemplateValue() + ".html");
            Path targetPath = Paths.get(componentsDir, template.getTemplateValue() + "." + Lang.EN.getSuffix() + ".html");
            if(path.toFile().exists() && !targetPath.toFile().exists()){
                try {
                    FileUtils.copyFolder(path,targetPath);
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }

        Template newTemplate = new Template();
        BeanUtils.copyProperties(template,newTemplate,"id");

        newTemplate.setLangSource(template.getId());
        newTemplate.setId(null);
        if(template.getPath()!=""){
            newTemplate.setPath(template.getPath()+File.separator+Lang.EN.getSuffix());
        }else {
            newTemplate.setPath(Lang.EN.getSuffix());
        }

        newTemplate.setTemplateValue(template.getTemplateValue()+"."+Lang.EN.getSuffix());
        newTemplate.setName(template.getName()+"."+Lang.EN.getSuffix());
        newTemplate.setIsSystem(false);
        newTemplate.setLang(Lang.EN);
        newTemplate.setEnName(template.getEnName()+"."+Lang.EN.getSuffix());
        return newTemplate;
    }

    @Override
    public Template createTemplateLanguage(@PathVariable("id") Integer id, @RequestParam(defaultValue = "EN") Lang lang){
        String workDir = CMSUtils.getWorkDir();
        String componentsDir=workDir+ File.separator+CMSUtils.getTemplates();;

        Template template = findById(id);
        if(template.getLang()==null){
            template.setLang(Lang.ZH);
            save(template);
        }
        if(template.getLang().equals(lang)){
            throw new ObjectException(template.getName()+"该组件已经是"+lang.getSuffix()+"的了！！！");
        }
        Template langTemplate = findByLang(template.getId(), lang);

        if(langTemplate!=null){
            throw new ObjectException(langTemplate.getName()+"已经创建了英文！！！");
        }

        Template byEnName = findByEnNameNull(template.getEnName());
        if(byEnName!=null){
            throw new ObjectException(byEnName.getName()+"已经使用了enName"+byEnName.getEnName());
        }
        Template template1 = createTemplate(template, componentsDir);



        Template save = save(template1);
        return save;
    }

}
