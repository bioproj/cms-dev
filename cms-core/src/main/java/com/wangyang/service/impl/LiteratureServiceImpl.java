package com.wangyang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.wangyang.common.CmsConst;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.Collection;
import com.wangyang.common.enums.CrudType;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.enums.RelationType;
import com.wangyang.pojo.vo.CollectionVO;
import com.wangyang.pojo.vo.LiteratureDetailVO;
import com.wangyang.pojo.vo.LiteratureVo;
import com.wangyang.repository.LiteratureRepository;
import com.wangyang.repository.TagsRepository;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.service.*;
import com.wangyang.service.base.AbstractContentServiceImpl;
import com.wangyang.service.relation.IArticleTagsService;
import com.wangyang.service.templates.IComponentsService;
import com.wangyang.service.templates.ITemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class LiteratureServiceImpl  extends AbstractContentServiceImpl<Literature, LiteratureDetailVO, LiteratureVo> implements ILiteratureService {

    private LiteratureRepository literatureRepository;
    private ITaskService taskService;
    private ICollectionService collectionService;
    private ITemplateService templateService;
    private IComponentsService componentsService;
    @Autowired
    private ArticleTagsRepository articleTagsRepository;


    @Autowired
    private IArticleTagsService articleTagsService;

    @Autowired
    IHtmlService htmlService;
    @Autowired
    TagsRepository tagsRepository;

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
    public LiteratureVo update(Integer integer, Literature updateDomain, Set<Integer> tagsIds) {

        updateDomain.setUpdateDate(new Date());

        Literature literature = super.update(integer, updateDomain);
        Collection collection = collectionService.findById(literature.getCategoryId());
        super.injectContent(literature,collection);
        LiteratureVo literatureVo;
        articleTagsRepository.deleteByArticleId(updateDomain.getId());

        if (tagsIds!=null && !CollectionUtils.isEmpty(tagsIds)) {

            literatureVo= new LiteratureVo();
            BeanUtils.copyProperties(literature, literatureVo);

            // Get Article tags
            List<ArticleTags> articleTagsList = tagsIds.stream().map(tagId -> {
                ArticleTags articleTags = new ArticleTags();
                articleTags.setRelationId(tagId);
                articleTags.setRelationType(RelationType.LITERATURE);
                articleTags.setArticleId(updateDomain.getId());
                return articleTags;
            }).collect(Collectors.toList());
            //save article tags
            articleTagsRepository.saveAll(articleTagsList);
            literatureVo.setTagIds(tagsIds);
            List<Tags> tags = tagsRepository.findAllById(tagsIds);
            literatureVo.setTags(tags);

        }else {
            literatureVo = convertToTagVo(literature);
//            List<Tags> tags = tagsRepository.findTagsByArticleId(literatureVo.getId());
//            if(!CollectionUtils.isEmpty(tags)){
//                literatureVo.setTags(tags.stream().map(item->{
//                    TagsDto tagsDto = new TagsDto();
//                    BeanUtils.copyProperties(item,tagsDto);
//                    return  tagsDto;
//                }).collect(Collectors.toList()));
//
//                literatureVo.setTagIds( ServiceUtil.fetchProperty(tags, Tags::getId));
//            }
        }
//        LiteratureVo literatureVo1 = convertToVo(literature1);
//        LiteratureVo literatureVo = convertToTagVo(literatureVo1);
//        htmlService.conventHtml(literatureVo,false);
        CollectionVO collectionVO = collectionService.convertToVo(collection);
        literatureVo.setCategory(collectionVO);
//        literatureVo.setCategory(collection);
        return literatureVo;
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
    public Literature findByKeys(String key) {
//        List<Literature> literatures = new ArrayList<>();


        List<Literature> literatures = literatureRepository.findAll(new Specification<Literature>() {
            @Override
            public Predicate toPredicate(Root<Literature> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("key"),key)).getRestriction();
            }
        });

        if(literatures.size()>0) return literatures.get(0);
        return null;
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
    public void generateHtml(Literature  literatures) {
        LiteratureVo literatureVo1 = convertToVo(literatures);
        LiteratureVo literatureVo = convertToTagVo(literatureVo1);
        htmlService.conventHtml(literatureVo);
    }
    @Override
    public void generateHtml(List<Literature> literatures) {
//        Template template = templateService.findByEnName(CmsConst.DEFAULT_LITERATURE_TEMPLATE);
        for (Literature literature: literatures){
            if(literature.getCategoryId()!=null && literature.getCategoryId()!=-1) {
                LiteratureVo literatureVo1 = convertToVo(literature);
                LiteratureVo literatureVo = convertToTagVo(literatureVo1);
                htmlService.conventHtml(literatureVo);
            }
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
            if(Objects.isNull(literature1.getTemplateName())){
                literature1.setTemplateName(CmsConst.DEFAULT_LITERATURE_TEMPLATE);
                literature1 = save(literature1);
            }
            if(literature1.getCategoryId()!=null && literature1.getCategoryId()!=-1){
                LiteratureVo literatureVo1 = convertToVo(literature1);
                LiteratureVo literatureVo = convertToTagVo(literatureVo1);
                htmlService.conventHtml(literatureVo,false);
            }

        }
//        for (Collection collection : collections){
//            htmlService.conventHtml(collection);
////            Template template = templateService.findByEnName(collection.getTemplateName());
////            List<Literature> subLiterature = literatureList.stream().filter(literature ->
////                    literature.getCategoryId().equals(collection.getId())
////            ).collect(Collectors.toList());
////            Map<String,Object> map = new HashMap<>();
////            map = new HashMap<>();
////            map.put("view",subLiterature);
////            map.put("template",template);
////            String html = TemplateUtil.convertHtmlAndSave(CMSUtils.getLiteraturePath(),collection.getKey(),map, template);
//        }

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
    public ArticleTags addAttachmentTags(Integer literatureId, Integer id) {
        ArticleTags articleTags =  new ArticleTags();
        articleTags.setArticleId(literatureId);
        articleTags.setRelationType(RelationType.LITERATURE_ATTACHMENT);
        articleTags.setRelationId(id);
        articleTagsService.save(articleTags);
        return articleTags;
    }

    @Override
    public boolean supportType(CrudType type) {
        return false;
    }
}
