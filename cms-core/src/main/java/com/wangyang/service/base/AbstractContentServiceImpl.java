package com.wangyang.service.base;

import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ArticleException;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ImageUtils;
import com.wangyang.common.utils.MarkdownUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.interfaces.IContentAop;
import com.wangyang.pojo.dto.CategoryContentList;
import com.wangyang.pojo.dto.CategoryContentListDao;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.base.BaseCategory;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.common.enums.Lang;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.enums.ArticleList;
import com.wangyang.pojo.enums.ArticleStatus;
import com.wangyang.pojo.enums.ParseType;
import com.wangyang.pojo.params.ArticleQuery;
import com.wangyang.pojo.support.ForceDirectedGraph;
import com.wangyang.pojo.vo.*;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.repository.template.ComponentsArticleRepository;
import com.wangyang.repository.template.ComponentsCategoryRepository;
import com.wangyang.repository.template.ComponentsRepository;
import com.wangyang.repository.TagsRepository;
import com.wangyang.repository.base.ContentRepository;
import com.wangyang.service.ICategoryService;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.util.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

//@Component
@Slf4j
public abstract class AbstractContentServiceImpl<ARTICLE extends Content,CONTENTDETAILVO extends  ContentDetailVO,ARTICLEVO extends ContentVO>  extends AbstractCrudService<ARTICLE,CONTENTDETAILVO,ARTICLEVO,Integer>
        implements IContentService<ARTICLE,CONTENTDETAILVO,ARTICLEVO> {

//    @Autowired
//    IOptionService optionService;
    @Autowired
    ComponentsArticleRepository componentsArticleRepository;

    @Autowired
    ICategoryService categoryService;

    @Autowired
    TagsRepository tagsRepository;

    @Autowired
    IContentAop contentAop;
    @Autowired
    ComponentsRepository componentsRepository;

    @Autowired
    ArticleTagsRepository articleTagsRepository;
    @Autowired
    IUserService userService;
    @Autowired
    IBaseCategoryService<BaseCategory,BaseCategory, BaseCategoryVo> baseCategoryService;

    @Autowired
    ComponentsCategoryRepository componentsCategoryRepository;

    public void injectBeforeCategory(BaseCategory category){
        contentAop.injectContent(category);
    }

    public void injectContent(ARTICLE article,BaseCategory category){
        contentAop.injectContent(article,category);
    }

    @Override
    public List<ContentVO> listContentTopByCategoryId(Integer categoeyId, Boolean desc) {
        return Collections.emptyList();
    }

    @Override
    public List<ContentVO> convertToSimpleListVo(List<Content> contents) {
        return Collections.emptyList();
    }

    @Override
    public ComponentsArticle addComponentsArticle(int articleId, int componentsId){
        Content content = findById(articleId);
        Components components = componentsRepository.findById(componentsId).get();
        ComponentsArticle findComponentsArticle = componentsArticleRepository.findByArticleIdAndComponentId(content.getId(), componentsId);
        if(findComponentsArticle!=null){
            throw new ObjectException(content.getTitle()+"已经在组件"+components.getName()+"中！！！");
        }
        ComponentsArticle componentsArticle = new ComponentsArticle();
        componentsArticle.setArticleId(content.getId());
        componentsArticle.setComponentId(components.getId());
        return  componentsArticleRepository.save(componentsArticle);
    }
    @Override
    public ComponentsArticle addComponentsArticle(String viewName, int componentsId){
        Content content = findByViewName(viewName);
        Components components = componentsRepository.findById(componentsId).get();

        if(content==null){
            throw new ObjectException("要添加的内容不存在！！");
        }
        if(components==null){
            throw new ObjectException("要添加的组件不存在！！");
        }
        if(components.getDataName().equals(CmsConst.ARTICLE_DATA)){
            ComponentsArticle findComponentsArticle = componentsArticleRepository.findByArticleIdAndComponentId(content.getId(), componentsId);
            if(findComponentsArticle!=null){
                throw new ObjectException("["+content.getTitle()+"]已经在组件["+components.getName()+"]中！！！");
            }
            ComponentsArticle componentsArticle = new ComponentsArticle();
            componentsArticle.setArticleId(content.getId());
            componentsArticle.setComponentId(components.getId());
            return  componentsArticleRepository.save(componentsArticle);
        }
        throw new ObjectException("文章["+content.getTitle()+"]不能添加到组件["+components.getName()+"]中");
    }

//    @Autowired
//    ArticleRepository articleRepository;
    private ContentRepository<ARTICLE> contentRepository;
    public AbstractContentServiceImpl(ContentRepository<ARTICLE> contentRepository) {
        super(contentRepository);
        this.contentRepository=contentRepository;
    }

    @Override
    public ARTICLE createOrUpdate(ARTICLE article) {
        if(Objects.isNull(article.getParseType())){
            article.setParseType(ParseType.MARKDOWN);
        }
        if(article.getParseType().equals(ParseType.MARKDOWN)){
            MarkdownUtils.renderHtml(article);
        }else if(article.getParseType().equals(ParseType.COPY)){
            article.setFormatContent(article.getOriginalContent());
        }
        return article;
    }

//    @Override
//    public ARTICLE previewSave(ARTICLE article) {
//
//
//            String[] renderHtml = MarkdownUtils.renderHtml(article.getOriginalContent());
//
//            article.setFormatContent(renderHtml[1]);
//
//            article.setToc(renderHtml[0]);
//
//        return article;
//    }


    @Override
    public Page<ARTICLE> pageContentByCategoryIds(Set<Integer> ids, Boolean isDesc, PageRequest pageRequest) {
        return null;
    }

    @Override
    public List<ARTICLE> listContentByCategoryIds(Set<Integer> ids, Boolean isDesc) {
        return null;
    }
    @Override
    public List<ARTICLE> listContentByCategoryId(Integer categoryId) {
        return contentRepository.findAll(new Specification<ARTICLE>() {
            @Override
            public Predicate toPredicate(Root<ARTICLE> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("categoryId"),categoryId)).getRestriction();
            }
        });
    }

    @Override
    public Page<ARTICLEVO> convertToPageVo(Page<ARTICLE> contentPage) {
        List<ARTICLE> contents = contentPage.getContent();
        //Get article Ids
        Set<Integer> articleIds = ServiceUtil.fetchProperty(contents, Content::getId);

        List<ArticleTags> articleTags = articleTagsRepository.findAllByArticleIdIn(articleIds);

        Set<Integer> tagIds = ServiceUtil.fetchProperty(articleTags, ArticleTags::getRelationId);
        List<Tags> tags = tagsRepository.findAllById(tagIds);
        Map<Integer, Tags> tagsMap = ServiceUtil.convertToMap(tags, Tags::getId);
        Map<Integer,List<Tags>> tagsListMap = new HashMap<>();
        articleTags.forEach(
                articleTag->{
                    tagsListMap.computeIfAbsent(articleTag.getArticleId(),
                                    tagsId->new LinkedList<>())
                            .add(tagsMap.get(articleTag.getRelationId()));
                }

        );
//        Set<Integer> userIds = ServiceUtil.fetchProperty(contents, Content::getUserId);
//        List<User> users = userService.findAllById(userIds);

//        Map<Integer, User> userMap = ServiceUtil.convertToMap(users, User::getId);
        Set<Integer> categories = ServiceUtil.fetchProperty(contents, Content::getCategoryId);
        List<BaseCategory> categoryDtoList = baseCategoryService.listByIds(categories);
//        .stream().map(category -> {
//            CategoryDto categoryDto = new CategoryDto();
//            BeanUtils.copyProperties(category, categoryDto);
//            return categoryDto;
//        }).collect(Collectors.toList());
        List<BaseCategoryVo> categoryVOS = baseCategoryService.convertToListVo(categoryDtoList);
        Map<Integer, BaseCategoryVo> categoryMap = ServiceUtil.convertToMap(categoryVOS, BaseCategoryVo::getId);


        Page<ARTICLEVO> contentVOS = contentPage.map(content -> {
            ARTICLEVO contentVO = getVOInstance();
            BeanUtils.copyProperties(content,contentVO);
//            contentVO.setUser(userMap.get(content.getUserId()));
            if(categoryMap.containsKey(content.getCategoryId())){
                contentVO.setCategory( categoryMap.get(content.getCategoryId()));
            }
            contentVO.setLinkPath(FormatUtil.articleListFormat(content ));
//            articleVO.setLinkPath(FormatUtil.articleListFormat(article));
            contentVO.setTags(Optional.ofNullable(tagsListMap.get(content.getId()))
                    .orElseGet(LinkedList::new)
                    .stream()
                    .filter(Objects::nonNull)
//                    .map(tag->{
//                        TagsDto tagsDto = new TagsDto();
//                        BeanUtils.copyProperties(tag,tagsDto);
//                        return tag;
//                    })
                    .collect(Collectors.toList()));
//            articleVO.setTags(tagsListMap.get(article.getId()));

            return contentVO;
        });
        return contentVOS;
    }




    @Override
    public CategoryContentListDao findCategoryContentBy(BaseCategoryVo category, int page) {
        return null;
    }

    @Override
    public List<ARTICLEVO> listVoTree(Integer categoryId) {
        return null;
    }

    private Specification<ARTICLE> articleSpecification(Set<Integer> ids, Boolean isDesc, ArticleList articleList){
        Specification<ARTICLE> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(criteriaBuilder.in(root.get("categoryId")).value(ids));
            if(articleList.equals(ArticleList.INCLUDE_TOP)){
                predicates.add( criteriaBuilder.isTrue(root.get("top")));
                predicates.add(  criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
                        criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY)));

            }else if(articleList.equals(ArticleList.NO_INCLUDE_TOP)){
                predicates.add( criteriaBuilder.isFalse(root.get("top")));
                predicates.add(  criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
                        criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY)));

            }else if(articleList.equals(ArticleList.ALL_PUBLISH_MODIFY_ARTICLE)){
                predicates.add(  criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
                        criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY)));
            }else if(articleList.equals(ArticleList.ALL_ARTICLE)){

            }
            criteriaQuery.where(predicates.toArray(new Predicate[0]));
            if(isDesc!=null){
                if(isDesc){
//                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("updateDate")));
//                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("order")),criteriaBuilder.desc(root.get("id")));
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
                }else {
                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
//                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("order")),criteriaBuilder.desc(root.get("id")));

//                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("updateDate")));
                }
            }

            return criteriaQuery.getRestriction();
        };
        return specification;
    }
    @Override
    public List<ARTICLEVO> listVoTree(Set<Integer> ids, Boolean isDesc) {

        Specification<ARTICLE> specification =  articleSpecification(ids,isDesc, ArticleList.NO_INCLUDE_TOP);
        List<ARTICLE> contents = contentRepository.findAll(specification);
//                .stream().map(article -> {
//            ArticleVO articleVO = new ArticleVO();
//            BeanUtils.copyProperties(article, articleVO);
//            return articleVO;
//        }).collect(Collectors.toList());
        List<ARTICLEVO> contentVOS = convertToListVo(contents);
        List<ARTICLEVO> contentVOTree = super.listWithTree(contentVOS);
//        List<ArticleDto> listWithTree = listWithTree(articleDtos);
        return contentVOTree;
    }

    @Override
    public void updateOrder(Integer id, List<ARTICLEVO> contentVOS) {
        Category category = categoryService.findById(id);
        updateOrder(category,contentVOS);
//        Set<Integer> ids= new HashSet<>();
//        ids.add(category.getId());
//
//        List<Category> categories = new ArrayList<>();
//        addChildAllIds(categories,category.getId());
//        ids.addAll(ServiceUtil.fetchProperty(categories, Category::getId));
//
//
//        List<Content> contents = listContentByCategoryIds(ids, true);
//
////        List<Article> articles = listArticleByCategoryIds(category.getId());
//        super.updateOrder(contents,contentVOS);
    }

    public void addChildAllIds( List<Category> categoryVOS, Integer id){
        List<Category> categories = categoryService.findByParentId(id);
        if(categories.size()==0){
            return;
        }
        categoryVOS.addAll(categories);
        if(categories.size()!=0){
            for (Category category:categories){
                addChildAllIds(categoryVOS,category.getId());
            }
        }

    }
    @Override
    public void updateOrder(Category category, List<ARTICLEVO> contentVOS) {
        Set<Integer> ids= new HashSet<>();
        ids.add(category.getId());

        List<Category> categories = new ArrayList<>();
        addChildAllIds(categories,category.getId());
        ids.addAll(ServiceUtil.fetchProperty(categories, Category::getId));


        List<ARTICLE> contents = listContentByCategoryIds(ids, true);

//        List<Article> articles = listArticleByCategoryIds(category.getId());
        super.updateOrder(contents,contentVOS);
    }


    @Override
    public List<ARTICLEVO> listArticleVOBy(String viewName) {
        return null;
    }

    @Override
    public ContentDetailVO updateCategory(ARTICLE content, Integer baseCategoryId) {
        return null;
    }


    @Override
    public List<ARTICLEVO> listByComponentsId(int componentsId){
        List<ComponentsArticle> componentsArticles = componentsArticleRepository.findByComponentId(componentsId);
        Set<Integer> articleIds = ServiceUtil.fetchProperty(componentsArticles, ComponentsArticle::getArticleId);
        if(articleIds.size()==0){
            return Collections.emptyList();
        }
//        List<Article> articles = articleRepository.findAllById(articleIds);
        List<ARTICLE> articles = contentRepository.findAll(new Specification<ARTICLE>() {
            @Override
            public Predicate toPredicate(Root<ARTICLE> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(root.get("id").in(articleIds)).getRestriction();
            }
        }, Sort.by(Sort.Direction.DESC,"articleInComponentOrder"));
        return convertToListVo(articles);
    }


    @Override
    public ARTICLE findByViewName(String viewName) {
        List<ARTICLE> contents = contentRepository.findAll(new Specification<ARTICLE>() {
            @Override
            public Predicate toPredicate(Root<ARTICLE> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("viewName"), viewName)).getRestriction();
            }
        });
        if(contents.size()==0){
            throw new ObjectException("查找的内容对象不存在");
        }
        return contents.get(0);
    }

    @Override
    public List<ARTICLEVO> convertToListVo(List<ARTICLE> domains) {
        return domains.stream().map(domain -> {
            ARTICLEVO domainvo = getVOInstance();
            BeanUtils.copyProperties(domain,domainvo);
            domainvo.setLinkPath(FormatUtil.articleListFormat(domain));
            if(domain.getOrder()==null){
                domainvo.setOrder(domain.getId());
            }
            return domainvo;

        }).collect(Collectors.toList());
    }

    @Override
    public ARTICLEVO convertToVo(ARTICLE domain) {
        ARTICLEVO domainvo = getVOInstance();
        BeanUtils.copyProperties(domain,domainvo);
        domainvo.setLinkPath(FormatUtil.articleListFormat(domain));
        if(domain.getCategoryId()!=null &&domain.getCategoryId()!=-1 ){
            BaseCategory baseCategory = baseCategoryService.findById(domain.getCategoryId());
            domainvo.setCategory(baseCategoryService.convertToVo(baseCategory));
        }

        return domainvo;
    }
    @Override
    public ARTICLEVO convertToTagVo(ARTICLEVO domainvo) {
        List<Tags> tags = tagsRepository.findTagsByArticleId(domainvo.getId());
        if(!CollectionUtils.isEmpty(tags)){
            domainvo.setTags(tags);

            domainvo.setTagIds( ServiceUtil.fetchProperty(tags, Tags::getId));
        }

//        domainvo.setLinkPath(FormatUtil.articleListFormat(domain));
        return domainvo;
    }
    @Override
    public ARTICLEVO convertToTagVo(ARTICLE domain) {
        ARTICLEVO domainvo = getVOInstance();
        List<Tags> tags = tagsRepository.findTagsByArticleId(domain.getId());
        if(!CollectionUtils.isEmpty(tags)){
            domainvo.setTags(tags);

            domainvo.setTagIds( ServiceUtil.fetchProperty(tags, Tags::getId));
        }

        BeanUtils.copyProperties(domain,domainvo);
        domainvo.setLinkPath(FormatUtil.articleListFormat(domain));
        return domainvo;
    }
    @Override
    public List<ARTICLEVO> convertToListTagVo(List<ARTICLE> domains) {
        return domains.stream().map(domain -> {
            ARTICLEVO domainvo = getVOInstance();
            List<Tags> tags = tagsRepository.findTagsByArticleId(domain.getId());
            if(!CollectionUtils.isEmpty(tags)){
                domainvo.setTags(tags);

                domainvo.setTagIds( ServiceUtil.fetchProperty(tags, Tags::getId));
            }

            BeanUtils.copyProperties(domain,domainvo);
            domainvo.setLinkPath(FormatUtil.articleListFormat(domain));
            return domainvo;

        }).collect(Collectors.toList());
    }


    @Override
    public List<ARTICLEVO> convertToListCategoryVo(List<ARTICLE> domains) {

        List<ARTICLE> filterDomains = domains.stream().filter(item -> item.getCategoryId() != null).collect(Collectors.toList());
        Set<Integer> categories = ServiceUtil.fetchProperty(filterDomains, ARTICLE::getCategoryId);
//        List<CategoryDto> categoryDtos = baseCategoryService.listByIds(categories).stream().map(category -> {
//            return baseCategoryService.convertToListVo(category);
//        }).collect(Collectors.toList());
        List<BaseCategory> baseCategories = baseCategoryService.listByIds(categories);
        List<BaseCategoryVo> baseCategoryVos = baseCategoryService.convertToListVo(baseCategories);
        Map<Integer, BaseCategoryVo> categoryMap = ServiceUtil.convertToMap(baseCategoryVos, BaseCategoryVo::getId);


        return domains.stream().map(domain -> {
            ARTICLEVO domainvo = getVOInstance();
            if(categoryMap.containsKey(domain.getCategoryId())){
                domainvo.setCategory( categoryMap.get(domain.getCategoryId()));
            }

            BeanUtils.copyProperties(domain,domainvo);
            domainvo.setLinkPath(FormatUtil.articleListFormat(domain));
            return domainvo;

        }).collect(Collectors.toList());
    }

    @Override
    public List<ARTICLEVO> convertToListSimpleVo(List<ARTICLE> domains) {
        return domains.stream().map(domain -> {
            ARTICLEVO domainvo = getVOInstance();
            BeanUtils.copyProperties(domain,domainvo);
            domainvo.setLinkPath(FormatUtil.articleListFormat(domain));
            return domainvo;

        }).collect(Collectors.toList());
    }

    @Override
    public ARTICLE findByViewName(String viewName, Lang lang) {
        List<ARTICLE> contents = contentRepository.findAll(new Specification<ARTICLE>() {
            @Override
            public Predicate toPredicate(Root<ARTICLE> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("viewName"), viewName),
                        criteriaBuilder.equal(root.get("lang"), lang)).getRestriction();
            }
        });
        if(contents.size()==0)return null;
        return contents.get(0);
    }

    public List<CategoryContentList> listCategoryContent( List<Category> categories){
        List<CategoryContentList> categoryArticleLists =  new ArrayList<>();
        for (Category category:categories){
            CategoryContentList<ARTICLEVO> categoryContentList = new CategoryContentList();
            CategoryVO categoryVO = categoryService.convertToVo(category);
            categoryContentList.setCategory(categoryVO);
            ArticleQuery articleQuery = new ArticleQuery();
            articleQuery.setCategoryId(category.getId());
            articleQuery.setDesc(category.getDesc());

            Set<Integer> ids =new HashSet<>();
            List<CategoryVO> categoryVOS = new ArrayList<>();
            ids.add(category.getId());
            categoryService.addChild(categoryVOS,category.getId());
            ids.addAll(ServiceUtil.fetchProperty(categoryVOS, CategoryVO::getId));
            List<ARTICLEVO> contents=listVoTree(ids,category.getIsDesc());

            categoryContentList.setContentVOS(contents);
            categoryArticleLists.add(categoryContentList);
        }
        return categoryArticleLists;
    }
    @Override
    public List<CategoryContentList> listCategoryContentByComponentsId(int componentsId) {
        List<ComponentsCategory> componentsCategories = componentsCategoryRepository.findByComponentId(componentsId);
        Set<Integer> categoryIds = ServiceUtil.fetchProperty(componentsCategories, ComponentsCategory::getCategoryId);
        List<Category>  categories = categoryService.listByIdsOrderComponent(categoryIds);
        return listCategoryContent(categories);
    }

    @Override
    public List<CategoryContentList> listCategoryContentByComponentsId(int componentsId, Integer page) {
        return null;
    }


    @Override
    public List<CategoryContentList> listCategoryContentByComponentsIdSize(int componentsId, Integer size) {
        return null;
    }

    @Override
    public CategoryContentListDao findCategoryContentBy(BaseCategory category, int page) {
        return null;
    }

    public  List<Predicate> listPredicate(ArticleQuery articleQuery, Root<ARTICLE> root, CriteriaBuilder criteriaBuilder, CriteriaQuery<?> query){
        List<Predicate> predicates = new LinkedList<>();



        if (articleQuery.getCategoryId()!=null) {
            predicates.add(criteriaBuilder.equal(root.get("categoryId"),articleQuery.getCategoryId()));
        }

        if (articleQuery.getKeyword() != null) {
            // Format like condition
            String likeCondition = String.format("%%%s%%",articleQuery.getKeyword());

            // Build like predicate
            Predicate titleLike = criteriaBuilder.like(root.get("title"), likeCondition);
            Predicate originalContentLike = criteriaBuilder.like(root.get("originalContent"), likeCondition);

            predicates.add(criteriaBuilder.or(titleLike, originalContentLike));
        }
//            if(articleQuery.getHaveHtml()!=null){
//                predicates.add(criteriaBuilder.equal(root.get("haveHtml"),articleQuery.getHaveHtml()));
//            }
        if(articleQuery.getUserId()!=null){
            predicates.add(criteriaBuilder.equal(root.get("userId"), articleQuery.getUserId()));
        }
        if(articleQuery.getStatus()!=null){
            predicates.add(criteriaBuilder.equal(root.get("status"),articleQuery.getStatus()));
        }
        if(articleQuery.getTagsId()!=null){

            Subquery<Article> subquery = query.subquery(Article.class);
            Root<ArticleTags> subRoot = subquery.from(ArticleTags.class);
            subquery = subquery.select(subRoot.get("articleId")).where(criteriaBuilder.equal(subRoot.get("relationId"),articleQuery.getTagsId()));
            predicates.add(criteriaBuilder.in(root.get("id")).value(subquery));
        }
        if(articleQuery.getTop()!=null){
            if(articleQuery.getTop()){
                predicates.add(criteriaBuilder.isTrue(root.get("top")));
            }else {
                predicates.add(criteriaBuilder.isFalse(root.get("top")));
            }

        }
        return predicates;
    }
    public Specification<ARTICLE> buildPublishByQuery(ArticleQuery articleQuery) {
        return (Specification<ARTICLE>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = listPredicate(articleQuery, root, criteriaBuilder, query);
            predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
                    criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY)));

            query.where(predicates.toArray(new Predicate[0]));
            if(articleQuery.getDesc()!=null){
                if(articleQuery.getDesc()){
                    query.orderBy(criteriaBuilder.desc(root.get("order")),criteriaBuilder.desc(root.get("id")));
                }else {
                    query.orderBy(criteriaBuilder.asc(root.get("order")),criteriaBuilder.desc(root.get("id")));

                }
            }

            return query.getRestriction();
        };
    }

    @Override
    public Page<ARTICLE>  pagePublishBy(Pageable pageable, ArticleQuery articleQuery){
        return  contentRepository.findAll(buildPublishByQuery(articleQuery),pageable);
    }

    @Override
    public void checkContentTemplatePath(ARTICLE content){
        if(content.getCategoryId()!=null){
            Category category = categoryService.findById(content.getCategoryId());
            if(!category.getArticleTemplateName().equals(content.getTemplateName()) || !category.getPath().equals(content.getPath())){
                content.setTemplateName(category.getArticleTemplateName());
                content.setPath(category.getPath());
                save(content);
            }
        }
    }


    @Override
    public ARTICLE update(Integer integer, ARTICLE updateDomain) {
        updateDomain = createOrUpdate(updateDomain);
        return super.update(integer, updateDomain);
    }




//    public static void flattenContentVOTreeToList(List<ContentVO> contentVOS, ForceDirectedGraph forceDirectedGraph) {
//        for (ContentVO content: contentVOS){
//            forceDirectedGraph.addNodes(content.getId(),content.getTitle(),content.getLinkPath());
//
//            if(content.getChildren().size()!=0){
//                for (ContentVO child:content.getChildren()){
//                    forceDirectedGraph.addEdges(content.getId(),child.getId(),60,2);
//                }
//                flattenContentVOTreeToList(content.getChildren(),forceDirectedGraph);
//            }
//        }
//    }
    @Override
    public ForceDirectedGraph graph(List<ContentVO> contents) {
            ForceDirectedGraph forceDirectedGraph = new ForceDirectedGraph();
        contents.forEach(item->{
            forceDirectedGraph.addNodes(String.valueOf(item.getId()),item.getTitle(),item.getLinkPath());
            if(item.getParentId()!=0) {
                forceDirectedGraph.addEdges(String.valueOf(item.getId()),String.valueOf(item.getParentId()),60,2);
            }
        });


        return forceDirectedGraph;
    }

    @Override
    public void generateSummary(ARTICLE article){
//        if(article.getSummary()==null||"".equals(article.getSummary())){
//
//        }
        if(article.getSummary()==""){
            String text = MarkdownUtils.getText(article.getFormatContent());
            String summary ;
            if(text.length()>100){
                summary = text.substring(0,100);
            }else {
                summary = text;
            }
            article.setSummary(summary+"....");
        }
    }
    public ARTICLEVO createOrUpdateArticleVO(ARTICLE article, Set<Integer> tagsIds) {
//        if(article.getUserId()==null){
//            throw new ArticleException("文章用户不能为空!!");
//        }
        if(article.getCategoryId()==null){
            throw new ArticleException("文章类别不能为空!!");
        }
//        if(article.getStatus()!=ArticleStatus.INTIMATE){
//            article.setStatus(ArticleStatus.PUBLISHED);
//        }


        String viewName = article.getViewName();
        if(viewName==null||"".equals(viewName)){
            viewName = CMSUtils.randomViewName();
            log.debug("!!! view name not found, use "+viewName);
            article.setViewName(viewName);
        }
//        article.setHaveHtml(true);

        //设置评论模板
        if(article.getCommentTemplateName()==null){
            article.setCommentTemplateName(CmsConst.DEFAULT_COMMENT_TEMPLATE);
        }
        BaseCategory category = baseCategoryService.findById(article.getCategoryId());


        article.setCategoryPath(category.getPath());
        article.setCategoryViewName(category.getViewName());
        article.setIsArticleDocLink(category.getIsArticleDocLink());

//        if(article.getTemplateName()==null){
//            //由分类管理文章的模板，这样设置可以让文章去维护自己的模板
//
//        }
        article.setTemplateName(category.getArticleTemplateName());
//        if(article.getUseTemplatePath()!=null && article.getUseTemplatePath()){
//            Template template = templateService.findByEnName(category.getTemplateName());
//            article.setPath(template.getPath());
//        }

        if(category.getArticleUseViewName()){
            article.setPath(category.getPath()+ File.separator+category.getViewName());
        }else {
            article.setPath(category.getPath());
        }





//        if(article.getPath()==null || article.getPath().equals("")){
//            article.setPath(CMSUtils.getArticlePath());
//        }

//        article.setPath(CMSUtils.getArticlePath());
//        article.setPath(CMSUtils.getArticlePath());



        article = createOrUpdate(article);
        //图片展示
        if(article.getPicPath()==null|| "".equals(article.getPicPath())){
            String imgSrc = ImageUtils.getImgSrc(article.getOriginalContent());
            article.setPicPath(imgSrc);
        }
//        generateSummary(article);
        generateSummary(article);
//        保存文章
        ARTICLE saveArticle = contentRepository.save(article);
        injectContent(article,category);
        ARTICLEVO voInstance = getVOInstance();
        BeanUtils.copyProperties(saveArticle,voInstance);
        voInstance.setCategory(baseCategoryService.convertToVo(category));
//        ArticleDetailVO articleDetailVO = convert(saveArticle, category, tagsIds);




        return voInstance;
    }
    public CONTENTDETAILVO createOrUpdateArticle(ARTICLE article, Set<Integer> tagsIds) {
//        if(article.getUserId()==null){
//            throw new ArticleException("文章用户不能为空!!");
//        }
        if(article.getCategoryId()==null){
            throw new ArticleException("文章类别不能为空!!");
        }
        if(article.getStatus()!=ArticleStatus.INTIMATE){
            article.setStatus(ArticleStatus.PUBLISHED);
        }


        String viewName = article.getViewName();
        if(viewName==null||"".equals(viewName)){
            viewName = CMSUtils.randomViewName();
            log.debug("!!! view name not found, use "+viewName);
            article.setViewName(viewName);
        }
//        article.setHaveHtml(true);

        //设置评论模板
        if(article.getCommentTemplateName()==null){
            article.setCommentTemplateName(CmsConst.DEFAULT_COMMENT_TEMPLATE);
        }
        BaseCategory category = baseCategoryService.findById(article.getCategoryId());


        article.setCategoryPath(category.getPath());
        article.setCategoryViewName(category.getViewName());
        article.setIsArticleDocLink(category.getIsArticleDocLink());

//        if(article.getTemplateName()==null){
//            //由分类管理文章的模板，这样设置可以让文章去维护自己的模板
//
//        }
        article.setTemplateName(category.getArticleTemplateName());
//        if(article.getUseTemplatePath()!=null && article.getUseTemplatePath()){
//            Template template = templateService.findByEnName(category.getTemplateName());
//            article.setPath(template.getPath());
//        }

        if(category.getArticleUseViewName()){
            article.setPath(category.getPath()+ File.separator+category.getViewName());
        }else {
            article.setPath(category.getPath());
        }





//        if(article.getPath()==null || article.getPath().equals("")){
//            article.setPath(CMSUtils.getArticlePath());
//        }

//        article.setPath(CMSUtils.getArticlePath());
//        article.setPath(CMSUtils.getArticlePath());



        article = createOrUpdate(article);
        //图片展示
        if(article.getPicPath()==null|| "".equals(article.getPicPath())){
            String imgSrc = ImageUtils.getImgSrc(article.getOriginalContent());
            article.setPicPath(imgSrc);
        }
//        generateSummary(article);
        generateSummary(article);
//        保存文章
        ARTICLE saveArticle = contentRepository.save(article);
        injectContent(article,category);
        CONTENTDETAILVO voInstance = getDetailVOInstance();
        BeanUtils.copyProperties(saveArticle,voInstance);
        voInstance.setCategory(baseCategoryService.convertToVo(category));
//        ArticleDetailVO articleDetailVO = convert(saveArticle, category, tagsIds);




        return voInstance;
    }


    @Override
    public ForceDirectedGraph graphByTag(ContentVO content) {
        return null;
    }

    @Override
    public ForceDirectedGraph graphByTag(List<ContentVO> contents) {
        return null;
    }

    @Override
    public ForceDirectedGraph graphTags(List<? extends ContentVO> firstContent) {
        return null;
    }

    @Override
    public ForceDirectedGraph graphTagsCategory(List<? extends ContentVO> firstContent) {
        return null;
    }

    @Override
    public ForceDirectedGraph graphByTag(List<? extends ContentVO> contents, int num) {
        return null;
    }
}
