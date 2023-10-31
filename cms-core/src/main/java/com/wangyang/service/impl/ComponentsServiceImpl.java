package com.wangyang.service.impl;

import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.exception.TemplateException;
import com.wangyang.common.utils.*;
import com.wangyang.interfaces.IComponentsData;
import com.wangyang.pojo.dto.ArticleDto;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.common.enums.Lang;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.params.ArticleQuery;
import com.wangyang.pojo.params.ComponentsParam;
import com.wangyang.config.ApplicationBean;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.pojo.vo.BaseCategoryVo;
import com.wangyang.pojo.vo.ComponentsVO;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.repository.ComponentsRepository;
import com.wangyang.service.IArticleService;
import com.wangyang.service.ICategoryService;
import com.wangyang.service.IComponentsService;
import com.wangyang.service.ITagsService;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.service.base.IBaseCategoryService;
import com.wangyang.service.base.IContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ComponentsServiceImpl extends AbstractCrudService<Components, Components, BaseVo,Integer> implements IComponentsService {


    @Autowired
    IArticleService articleService;

    @Autowired
    @Qualifier("contentServiceImpl")
    IContentService<Content,Content, ContentVO> contentService;


    @Autowired
    ICategoryService categoryService;
    @Autowired
    ITagsService tagsService;
    ComponentsRepository componentsRepository;
    @Autowired
    ArticleTagsRepository articleTagsRepository;

    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    IBaseCategoryService<BaseCategory,BaseCategory, BaseCategoryVo> baseCategoryService;
    private final Map<String, IComponentsData> componentsDataMap = new HashMap<>();

    @PostConstruct
    public void init() {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(IComponentsData.class);
        for (String beanName : beanNamesForType){
            IComponentsData componentsData = (IComponentsData)applicationContext.getBean(beanName);
            this.componentsDataMap.put(componentsData.getDataName(),componentsData);
        }
    }

    @Override
    public Map<String, IComponentsData> getComponentsDataMap() {
        return componentsDataMap;
    }


    public ComponentsServiceImpl( ComponentsRepository componentsRepository) {
        super(componentsRepository);
        this.componentsRepository=componentsRepository;

    }







    @Override
    public List<Components> listNeedArticle(){
        Specification<Components> specification = new Specification<Components>() {
            @Override
            public Predicate toPredicate(Root<Components> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("dataName"),"@Article")).getRestriction();
            }
        };
        return componentsRepository.findAll(specification);
    }

    @Override
    public List<Components> listAll() {
        return componentsRepository.findAll();
    }

    @Override
    public List<Components> listAll(Lang lang){
        List<Components> components = componentsRepository.findAll(new Specification<Components>() {
            @Override
            public Predicate toPredicate(Root<Components> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("lang"),lang)).getRestriction();
            }
        });
        return components;
    }

    @Override
    public Components add(ComponentsParam componentsParam){
        Components components = new Components();
        components.setIsSystem(false);
        BeanUtils.copyProperties(componentsParam,components,CMSUtils.getNullPropertyNames(componentsParam));
        Components saveComponents = componentsRepository.save(components);
        ComponentsVO componentsVO = new ComponentsVO();
        BeanUtils.copyProperties(saveComponents,componentsVO);

        convert(componentsVO,true);
        return componentsVO;
    }

    @Override
    public Components saveUpdate(Integer id, ComponentsParam componentsParam) {
        Components components = findById(id);
//        convert(components,componentsParam);
        BeanUtils.copyProperties(componentsParam,components,CMSUtils.getNullPropertyNames(componentsParam));
        Components saveComponents = componentsRepository.save(components);
        ComponentsVO componentsVO = new ComponentsVO();
        BeanUtils.copyProperties(saveComponents,componentsVO);

        convert(componentsVO,false);
        return componentsVO;
    }

    @Override
    public Components update(int id, ComponentsParam componentsParam){
        Components components = findById(id);

        BeanUtils.copyProperties(componentsParam,components,CMSUtils.getNullPropertyNames(componentsParam));
        Components saveComponents = componentsRepository.save(components);
        ComponentsVO componentsVO = new ComponentsVO();
        BeanUtils.copyProperties(saveComponents,componentsVO);

        convert(componentsVO,true);
        return componentsVO;
    }

    private void convert(ComponentsVO componentsVO,Boolean isFile){

        String htmlContent = componentsVO.getOriginalContent();
        if(htmlContent!=null){
//            if(componentsVO.getParse()!=null && componentsVO.getParse()){
//                htmlContent = MarkdownUtils.renderHtml(htmlContent);
//            }
            componentsVO.setHtmlFile(htmlContent);

            if(isFile){
                String templateValue =componentsVO.getTemplateValue();
                String path = CmsConst.WORK_DIR+File.separator+CMSUtils.getTemplates()+File.separator+templateValue+".html";
                File file = new File(path);
                FileUtils.saveFile(file,htmlContent);
            }
        }


    }

    @Override
    public List<Components> addAll(List<Components> templatePages) {
        return componentsRepository.saveAll(templatePages);
    }

    @Override
    public Components findById(int id){
        Optional<Components> templatePageOptional = componentsRepository.findById(id);
        if(!templatePageOptional.isPresent()){
            throw new TemplateException("add template did't exist!!");
        }
        return templatePageOptional.get();
    }


    @Override
    public ComponentsVO findDetailsById(int id){
        Components components = findById(id);
        ComponentsVO componentsVO = new ComponentsVO();
        BeanUtils.copyProperties(components,componentsVO);
//        if(components.getParse()!=null && components.getParse() && components.getOriginalContent()!=null && components.getOriginalContent()!=""){
//            convert(componentsVO,false);
//        }else {

            String templateValue = components.getTemplateValue();
            String path = CmsConst.WORK_DIR+File.separator+CMSUtils.getTemplates()+File.separator+templateValue+".html";
            File file = new File(path);
            if(file.exists()){
                String openFile = FileUtils.openFile(file);
                componentsVO.setHtmlFile(openFile);
                componentsVO.setOriginalContent(openFile);
            }
//        }

        return componentsVO;
    }

    @Override
    public Components delete(int id){
        Components components = findById(id);
        if(components.getIsSystem()){
            throw new ObjectException("系统内置模板不能删除");
        }
        componentsRepository.deleteById(id);
        return components;
    }

    @Override
    public void deleteAll() {
        componentsRepository.deleteAll();
    }



    @Override
    public Map<String ,Object> getModel(Components components) {
        Map<String,Object> map = new HashMap<>();
        map.put("component",components);
        try {
            if(componentsDataMap.containsKey(components.getDataName())){
                IComponentsData componentsData = componentsDataMap.get(components.getDataName());
                map.putAll( componentsData.getData(components));
                return map;
            } else if(components.getDataName().equals(CmsConst.ARTICLE_DATA)){

                map.put("view",contentService.listByComponentsId(components.getId()));
                return  map;


//                Set<Integer> ids = Arrays.asList(args).stream().map(a -> Integer.parseInt(a)).collect(Collectors.toSet());
//                String[] names = components.getDataName().substring(1).split("\\.");
//                String className = names[0];
//                String methodName = names[1];
//                Object bean = ApplicationBean.getBean(className);
//                Method method = bean.getClass().getMethod(methodName,Set.class);
//                Object o = method.invoke(bean,ids);
//                return o;
            }else if(components.getDataName().equals(CmsConst.CATEGORY_DATA)){

//                Map<String,Object> map = new HashMap<>();
                map.put("view",baseCategoryService.listByComponentsId(components.getId()));
                return  map;
            }else if(components.getDataName().equals(CmsConst.CATEGORY_CHILD_DATA)){

//                Map<String,Object> map = new HashMap<>();
                map.put("view",categoryService.listChildByComponentsId(components.getId()));
                return  map;
            }else if(components.getDataName().equals(CmsConst.CATEGORY_ARTICLE_DATA)){

//                Map<String,Object> map = new HashMap<>();
                map.put("view",contentService.listCategoryContentByComponentsId(components.getId()));
                return  map;
            }else if(components.getDataName().startsWith(CmsConst.CATEGORY_ARTICLE_PAGE_DATA)){
                if(!components.getDataName().contains("_")){
                    throw new ObjectException("数据中必须包含[@CategoryArticlePage_5]格式");
                }
                String[] split = components.getDataName().split("_");
                int page = Integer.parseInt(split[1]);
//                Map<String,Object> map = new HashMap<>();
                map.put("view",contentService.listCategoryContentByComponentsId(components.getId(),page));
                return  map;
            } else if(components.getDataName().startsWith(CmsConst.CATEGORY_ARTICLE_SIZE_DATA)){
                if(!components.getDataName().contains("_")){
                    throw new ObjectException("数据中必须包含[@CategoryArticleSize_5]格式");
                }
                String[] split = components.getDataName().split("_");
                int size = Integer.parseInt(split[1]);
//                Map<String,Object> map = new HashMap<>();
                map.put("view",contentService.listCategoryContentByComponentsIdSize(components.getId(),size));
                return  map;
            }  else if (components.getDataName().startsWith("articleJob")){
                String[] names = components.getDataName().split("\\.");
                String className = names[0];
                String methodName = names[1];
                Object bean = ApplicationBean.getBean(className);
                Method method = bean.getClass().getMethod(methodName);
                Object o = method.invoke(bean);
                map.putAll((Map<String,Object>)o);
                return map;

            }
//            else if(components.getDataName().startsWith(CmsConst.ARTICLE_DATA_SORT)){
//                String args = components.getDataName().substring(CmsConst.ARTICLE_DATA_SORT.length());
//                Sort sort;
//                if(args!=null||!"".equals(args)){
//                    String[] argsArray = args.split(",");
//                    String directionStr = argsArray[argsArray.length-1];
//                    if(directionStr.equals("DESC")||directionStr.equals("ASC")){
//                        Sort.Direction direction = Sort.Direction.valueOf(directionStr);
//                        sort = Sort.by(direction, Arrays.copyOf(argsArray,argsArray.length-1));
//                    }else {
//                        sort = Sort.by( argsArray);
//                    }
//                }else {
//                    sort = Sort.by(Sort.Order.desc("id"));
//                }
//                ArticlePageCondition articlePageCategoryIds = articleService.pagePublishBy(components.getId(),0, 5, "DESC");
//                Page<Article> articles =articlePageCategoryIds.getArticles();
//                Page<ArticleVO> articleVOS = articleService.convertToPageVo(articles);
////                Map<String,Object> map = new HashMap<>();
//                map.put("view",articleVOS);
//                map.put("showUrl","/articleList?sort="+args); //likes,DESC
//                map.put("name",components.getName());
//                return map;
////                Template template = templateService.findByEnName(CmsConst.ARTICLE_LIST);
////                TemplateUtil.convertHtmlAndSave();
//            }
            else if(components.getDataName().startsWith(CmsConst.ARTICLE_DATA_KEYWORD)){

                String args = components.getDataName().substring(CmsConst.ARTICLE_DATA_KEYWORD.length());
                ArticleQuery articleQuery = new ArticleQuery();
                articleQuery.setKeyword(args);
//                articleQuery.setHaveHtml(true);
                Page<Article> articles = articleService.pagePublishBy(PageRequest.of(0, 5, Sort.by(Sort.Order.desc("updateDate"))), articleQuery);
                Page<ArticleDto> pageDto = articleService.convertArticle2ArticleDto(articles);
//                Map<String,Object> map = new HashMap<>();
                map.put("view",pageDto);
                map.put("showUrl","/articleList?keyword="+args); //
                map.put("name",components.getName());
                return map;
            }else if(components.getDataName().startsWith(CmsConst.ARTICLE_DATA_TAGS)) {
                String args = components.getDataName().substring(CmsConst.ARTICLE_DATA_TAGS.length());
                Optional<Tags> tags = tagsService.findBy(args);
                if(tags.isPresent()){
                    Page<ArticleDto> articleDtos = articleService.pageByTagId(tags.get().getId(), 5);
//                    Map<String,Object> map = new HashMap<>();
                    map.put("view",articleDtos);
                    map.put("showUrl","/articleList?tagsId="+tags.get().getId());
                    map.put("name",components.getName());
                    return map;
                }
            }
//            else if(components.getDataName().startsWith(CmsConst.ARTICLE_DATA_SORT_SIZE)){
//                map = getModelPageSize(components,0,5,"DESC");
//                return map;
//            }
            else if (components.getDataName().startsWith(CmsConst.TAG_DATA)) {
                List<Tags> tags = tagsService.listAll();
                Map<Tags,Integer> mapTags =  new HashMap<>();
                for (Tags tag : tags){
                    List<ArticleTags> articleTags = articleTagsRepository.findByRelationId(tag.getId());
                    int size = articleTags.stream().filter(articleTag -> {
                        boolean b = articleTag.getRelationId() == tag.getId();
                        return b;
                    }).collect(Collectors.toSet()).size();
                    mapTags.put(tag,size);
                }
                Map<Tags, Integer> sortMap = ServiceUtil.sortDescend(mapTags);
                Set<Map.Entry<Tags, Integer>> entries = sortMap.entrySet();

                List<Tags> topTags = new ArrayList<>();

                int i=0;
                for(Map.Entry<Tags, Integer>  item : entries){
                    if(i>10){
                        break;
                    }
                    topTags.add(item.getKey());
                }

//                Map<String,Object> map = new HashMap<>();
                map.put("view",topTags);
                return map;
            } else {

            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new ObjectException("组件数据不存在！");

    }


    @Override
    public Components findByDataName(String dataName){
        List<Components> templatePages = componentsRepository.findAll(new Specification<Components>() {
            @Override
            public Predicate toPredicate(Root<Components> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("dataName"), dataName)
                        ,criteriaBuilder.isTrue(root.get("status"))).getRestriction();
            }
        });

        if(CollectionUtils.isEmpty(templatePages)){
            throw new TemplateException("Template Not found!!");
        }

        return templatePages.get(0);
    }



    @Override
    public Components findByViewName(String path, String viewName){
        List<Components> components = componentsRepository.findAll(new Specification<Components>() {
            @Override
            public Predicate toPredicate(Root<Components> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("viewName"),viewName),criteriaBuilder.equal(root.get("path"),path)).getRestriction();
            }
        });
        if(components.size()==0)return null;
        return components.get(0);
    }
    @Override
    public Components findByViewName(String viewName){
        List<Components> components = componentsRepository.findAll(new Specification<Components>() {
            @Override
            public Predicate toPredicate(Root<Components> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("viewName"),viewName)).getRestriction();
            }
        });
        if(components.size()==0)return null;
        return components.get(0);
    }

    @Override
    public Components findByEnName(String enName) {
        return componentsRepository.findByEnName(enName);
    }
}
