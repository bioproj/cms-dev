package com.wangyang.service.templates;

import com.wangyang.pojo.entity.Template;
import com.wangyang.pojo.entity.TemplateChild;
import com.wangyang.common.enums.Lang;
import com.wangyang.pojo.entity.base.BaseTemplate;
import com.wangyang.pojo.enums.TemplateType;
import com.wangyang.pojo.params.TemplateParam;
import com.wangyang.common.pojo.BaseVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ITemplateService  extends IBaseTemplateService<Template,Template, BaseVo> {
    Optional<Template> findOptionalById(int id);

    List<Template> saveAll(List<Template> templates);

    Template save(Template template);

    Template add(Template template);
    Template update(int id, TemplateParam templateParam);

    Template findDetailsById(int id);

//    List<Template> listAll();
//
//    List<Template> listAll(Lang lang);

    List<Template> findAll();

    Template deleteById(int id);
    Template findById(int id);
    Page<Template> list(Pageable pageable,Lang lang);

    List<Template> listByAndStatusTrue(TemplateType templateType);

    Template findByEnNameReturnNUll(String enName);

    Template findByEnNameNull(String enName);

    Template findByEnName(String enName);

    Template findOptionalByEnName(String enName);

    void deleteAll();
    List<Template> findByTemplateType(TemplateType type);

//    Template setStatus(int id);

    Template addZipFile(MultipartFile file);

    Template tree(int id);

    TemplateChild addChild(Integer id, String enName);

    TemplateChild addChild(Integer id, Integer childId);

    List<Template> findByChild(Integer id);

    TemplateChild removeChildTemplate(Integer templateId,Integer templateChildId);

    Template createTemplateLanguage(@PathVariable("id") Integer id, @RequestParam(defaultValue = "EN") Lang lang);
}
