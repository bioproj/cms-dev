package com.wangyang.service.base;

import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.common.utils.MarkdownUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.dto.CategoryContentList;
import com.wangyang.pojo.dto.CategoryContentListDao;
import com.wangyang.pojo.dto.TagsDto;
import com.wangyang.pojo.entity.*;
import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.common.enums.Lang;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.enums.ArticleStatus;
import com.wangyang.pojo.params.ArticleQuery;
import com.wangyang.pojo.support.ForceDirectedGraph;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.pojo.vo.ContentDetailVO;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.repository.ComponentsArticleRepository;
import com.wangyang.repository.TagsRepository;
import com.wangyang.repository.base.ContentRepository;
import com.wangyang.service.ICategoryService;
import com.wangyang.util.FormatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

//@Component
public abstract class AbstractContentServiceImpl<ARTICLE extends Content,ARTICLEDTO extends BaseEntity,ARTICLEVO extends ContentVO>  extends AbstractCrudService<ARTICLE,ARTICLEDTO,ARTICLEVO,Integer>
        implements IContentService<ARTICLE,ARTICLEDTO,ARTICLEVO> {

//    @Autowired
//    IOptionService optionService;
    @Autowired
    ComponentsArticleRepository componentsArticleRepository;

    @Autowired
    ICategoryService categoryService;

    @Autowired
    TagsRepository tagsRepository;

//    @Autowired
//    ArticleRepository articleRepository;
    private ContentRepository<ARTICLE> contentRepository;
    public AbstractContentServiceImpl(ContentRepository<ARTICLE> contentRepository) {
        super(contentRepository);
        this.contentRepository=contentRepository;
    }

    @Override
    public ARTICLE createOrUpdate(ARTICLE article) {

        MarkdownUtils.renderHtml(article);
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
        return null;
    }

    @Override
    public void addParentCategory(List<CategoryVO> categoryVOS, Integer parentId) {

    }

    @Override
    public CategoryContentListDao findCategoryContentBy(CategoryVO category, Template template, int page) {
        return null;
    }

    @Override
    public List<ARTICLEVO> listVoTree(Integer categoryId) {
        return null;
    }

    @Override
    public List<ARTICLEVO> listVoTree(Set<Integer> ids, Boolean isDesc) {
        return null;
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
    public ARTICLEVO convertToTagVo(ARTICLE domain) {
        ARTICLEVO domainvo = getVOInstance();
        List<Tags> tags = tagsRepository.findTagsByArticleId(domain.getId());
        if(!CollectionUtils.isEmpty(tags)){
            domainvo.setTags(tags.stream().map(item->{
                TagsDto tagsDto = new TagsDto();
                BeanUtils.copyProperties(item,tagsDto);
                return  tagsDto;
            }).collect(Collectors.toList()));

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
                domainvo.setTags(tags.stream().map(item->{
                    TagsDto tagsDto = new TagsDto();
                    BeanUtils.copyProperties(item,tagsDto);
                    return  tagsDto;
                }).collect(Collectors.toList()));

                domainvo.setTagIds( ServiceUtil.fetchProperty(tags, Tags::getId));
            }

            BeanUtils.copyProperties(domain,domainvo);
            domainvo.setLinkPath(FormatUtil.articleListFormat(domain));
            return domainvo;

        }).collect(Collectors.toList());
    }

    @Override
    public List<ARTICLEVO> convertToListVo(List<ARTICLE> domains) {
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

    @Override
    public List<CategoryContentList> listCategoryContentByComponentsId(int componentsId) {
        return null;
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
    public CategoryContentListDao findCategoryContentBy(Category category, Template template, int page) {
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
            forceDirectedGraph.addNodes(item.getId(),item.getTitle(),item.getLinkPath());
            if(item.getParentId()!=0) {
                forceDirectedGraph.addEdges(item.getId(),item.getParentId(),60,2);
            }
        });


        return forceDirectedGraph;
    }
}
