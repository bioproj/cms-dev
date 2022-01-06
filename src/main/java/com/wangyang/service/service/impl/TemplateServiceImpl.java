package com.wangyang.service.service.impl;


import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.FileOperationException;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.exception.OptionException;
import com.wangyang.common.utils.FileUtils;
import com.wangyang.pojo.dto.CategoryDto;
import com.wangyang.pojo.dto.FileDTO;
import com.wangyang.pojo.entity.ArticleAttachment;
import com.wangyang.pojo.entity.Attachment;
import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.enums.AttachmentType;
import com.wangyang.pojo.enums.FileWriteType;
import com.wangyang.pojo.enums.TemplateType;
import com.wangyang.pojo.params.TemplateParam;
import com.wangyang.service.IArticleAttachmentService;
import com.wangyang.service.repository.TemplateRepository;
import com.wangyang.service.service.IAttachmentService;
import com.wangyang.service.service.ICategoryService;
import com.wangyang.service.service.ITemplateService;
import com.wangyang.util.ZipHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Service
@Slf4j
public class TemplateServiceImpl implements ITemplateService {

    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    ICategoryService categoryService;

    @Autowired
    IAttachmentService attachmentService;

    @Autowired
    IArticleAttachmentService articleAttachmentService;

    @Value("${cms.workDir}")
    private String workDir;

    private final static  String INSTALL_TEMPLATE_PATH = "templates/install";

    @Override
    public Template add(Template template) {
//        convert(template,template);
        createFile(template);
        return templateRepository.save(template);
    }




    @Override
    public Optional<Template> findOptionalById(int id){
        return templateRepository.findById(id);
    }
    @Override
    public List<Template> saveAll(List<Template> templates) {
        return templateRepository.saveAll(templates);
    }

    @Override
    public Template update(int id, TemplateParam templateParam) {
        Template template = findById(id);
        BeanUtils.copyProperties(templateParam,template);
//        convert(template,templateParam);
        createFile(template);
        return templateRepository.save(template);
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
    public List<Template> findAll(){
        return templateRepository.findAll();
    }

    @Override
    public Template deleteById(int id) {
        Template template = findById(id);
        templateRepository.deleteById(id);
        List<ArticleAttachment> articleAttachments = articleAttachmentService.findByTemplateId(template.getId());
        for (ArticleAttachment articleAttachment : articleAttachments){
            File file = new File(articleAttachment.getPath());
            if(file.exists()) ZipHelper.deleteFile(file);
        }
        articleAttachmentService.deleteAll(articleAttachments);
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
    public Page<Template> list(Pageable pageable) {
        return templateRepository.findAll(pageable);
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
    public Template findByEnName(String enName){
        Template template = templateRepository.findByEnName(enName);
        if(template==null){
            throw new ObjectException(enName+"Template模板没有找到!!!");
        }
        return template;
    }

    @Override
    public Optional<Template> findOptionalByEnName(String enName){
        Template template = templateRepository.findByEnName(enName);
        return Optional.ofNullable(template);
    }



    @Override
    public void deleteAll() {
        templateRepository.deleteAll();
    }



    @Override
    public Template setStatus(int id){
        Template template = findById(id);
        if(template.getStatus()){
            template.setStatus(false);
        }else {
            List<CategoryDto> categoryDtos = categoryService.listBy(template.getEnName());
            if(categoryDtos.size()==0){
                throw new OptionException("不能启用"+template.getName()+"在首页,因为该模板下没有分类!");
            }
            template.setStatus(true);
        }
        return templateRepository.save(template);
    }

    @Override
    public Template addZipFile(MultipartFile uploadFile) {
        String path = "testUpload/"+uploadFile.getOriginalFilename();
        Attachment attachment = attachmentService.upload(uploadFile, path, FileWriteType.COVER, AttachmentType.LOCAL);
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
        template = templateRepository.save(template);



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
}
