package com.wangyang.service.impl;

import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.Collection;
import com.wangyang.common.enums.CrudType;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.repository.LiteratureRepository;
import com.wangyang.service.*;
import com.wangyang.service.base.AbstractContentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LiteratureServiceImpl  extends AbstractContentServiceImpl<Literature, Literature, ContentVO> implements ILiteratureService {

    private LiteratureRepository literatureRepository;
    private ITaskService taskService;
    private ICollectionService collectionService;
    private ITemplateService templateService;
    private IComponentsService componentsService;
    @Autowired
    IHtmlService htmlService;

    public LiteratureServiceImpl(LiteratureRepository literatureRepository,
                                 ITaskService taskService,
                                 ICollectionService collectionService,
                                 ITemplateService templateService,
                                 IComponentsService componentsService) {
        super(literatureRepository);
        this.literatureRepository = literatureRepository;
        this.taskService =taskService;
        this.collectionService = collectionService;
        this.templateService = templateService;
        this.componentsService =componentsService;
    }

    @Override
    public List<Literature> listByKeys(Set<String> literatureStrIds) {
//        List<Literature> literatures = new ArrayList<>();

        if(literatureStrIds.size()==0){
            return new ArrayList<>();
        }
        List<Literature> literatures = literatureRepository.findAll(new Specification<Literature>() {
            @Override
            public Predicate toPredicate(Root<Literature> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.in(root.get("key")).value(literatureStrIds)).getRestriction();
            }
        });


        return literatures;
    }


    @Override
    public List<Literature> listByCollectionId(Integer collectionId) {
        return literatureRepository.findAll(new Specification<Literature>() {
            @Override
            public Predicate toPredicate(Root<Literature> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("categoryId"),collectionId)).getRestriction();
            }
        });
    }
    @Override
    public void generateHtml(List<Literature> literatures) {
//        Template template = templateService.findByEnName(CmsConst.DEFAULT_LITERATURE_TEMPLATE);
        for (Literature literature: literatures){
            htmlService.conventHtml(literature);
//            Map<String,Object> map = new HashMap<>();
//            map = new HashMap<>();
//            map.put("view",literature);
//            map.put("template",template);
//            String html = TemplateUtil.convertHtmlAndSave(CMSUtils.getLiteraturePath(),literature.getKey(),map, template);
        }
    }
    @Override
    public void generateListHtml(int userId) {
        List<Collection> collections = collectionService.listAll();
        List<Literature> literature = listAll();
        for (Literature literature1 : literature){
            htmlService.conventHtml(literature1);
        }
        for (Collection collection : collections){
            htmlService.conventHtml(collection);
//            Template template = templateService.findByEnName(collection.getTemplateName());
//            List<Literature> subLiterature = literatureList.stream().filter(literature ->
//                    literature.getCategoryId().equals(collection.getId())
//            ).collect(Collectors.toList());
//            Map<String,Object> map = new HashMap<>();
//            map = new HashMap<>();
//            map.put("view",subLiterature);
//            map.put("template",template);
//            String html = TemplateUtil.convertHtmlAndSave(CMSUtils.getLiteraturePath(),collection.getKey(),map, template);
        }

        Components components = componentsService.findByViewName("collectionTree");
        Object o = componentsService.getModel(components);
        TemplateUtil.convertHtmlAndSave(o, components);
//        for (Collection collection:collections){
//            List<Literature> subLiterature = literatureList.stream().filter(literature ->
//                    literature.getCategoryId().equals(collection.getId())
//            ).collect(Collectors.toList());
//            Map<String,Object> map = new HashMap<>();
//            map = new HashMap<>();
//            map.put("view",subLiterature);
//            map.put("template",template);
//            String html = TemplateUtil.convertHtmlAndSave(CMSUtils.getLiteraturePath(),collection.getKey(),map, template);
//        }
    }

    @Override
    public boolean supportType(CrudType type) {
        return false;
    }
}
