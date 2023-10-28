package com.wangyang.service.impl;

import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.dto.TagsDto;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.Collection;
import com.wangyang.common.enums.CrudType;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.enums.RelationType;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.pojo.vo.LiteratureVo;
import com.wangyang.repository.LiteratureRepository;
import com.wangyang.repository.TagsRepository;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.service.*;
import com.wangyang.service.base.AbstractContentServiceImpl;
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
public class LiteratureServiceImpl  extends AbstractContentServiceImpl<Literature, Literature, LiteratureVo> implements ILiteratureService {

    private LiteratureRepository literatureRepository;
    private ITaskService taskService;
    private ICollectionService collectionService;
    private ITemplateService templateService;
    private IComponentsService componentsService;
    @Autowired
    ArticleTagsRepository articleTagsRepository;

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
            literatureVo.setTags(tags.stream().map(item->{
                TagsDto tagsDto = new TagsDto();
                BeanUtils.copyProperties(item,tagsDto);
                return  tagsDto;
            }).collect(Collectors.toList()));

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
            LiteratureVo literatureVo = convertToTagVo(literature);
            htmlService.conventHtml(literatureVo);
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
            LiteratureVo literatureVo = convertToTagVo(literature1);
            htmlService.conventHtml(literatureVo);
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
