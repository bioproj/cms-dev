package com.wangyang.service.impl;

import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.*;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.enums.ArticleStatus;
import com.wangyang.common.enums.CrudType;
import com.wangyang.pojo.enums.TemplateData;
import com.wangyang.pojo.params.ArticleQuery;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.pojo.vo.ContentDetailVO;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.repository.ComponentsCategoryRepository;
import com.wangyang.repository.TagsRepository;
import com.wangyang.repository.base.ContentRepository;
import com.wangyang.service.ICategoryService;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.service.base.AbstractContentServiceImpl;
import com.wangyang.service.base.IContentService;
import com.wangyang.util.FormatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContentServiceImpl extends AbstractContentServiceImpl<Content,Content, ContentVO>  implements IContentService<Content,Content, ContentVO>  {
    @Autowired
    TagsRepository tagsRepository;
    @Autowired
    ArticleTagsRepository articleTagsRepository;
    @Autowired
    ComponentsCategoryRepository componentsCategoryRepository;


    @Autowired
    IUserService userService;
    @Autowired
    ICategoryService categoryService;
    private ContentRepository<Content> contentRepository;
    public ContentServiceImpl(ContentRepository<Content> contentRepository) {
        super(contentRepository);
        this.contentRepository=contentRepository;
    }

    @Override
    public boolean supportType(CrudType type) {
        return false;
    }
    private List<Content> listByCategory(){
        List<Content> contents = contentRepository.findAll();
        return contents;
    }
    private Specification<Content> articleSpecification(Set<Integer> ids, Boolean isDesc, ArticleServiceImpl.ArticleList articleList){
        Specification<Content> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(criteriaBuilder.in(root.get("categoryId")).value(ids));
            if(articleList.equals(ArticleServiceImpl.ArticleList.INCLUDE_TOP)){
                predicates.add( criteriaBuilder.isTrue(root.get("top")));
                predicates.add(  criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
                        criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY)));

            }else if(articleList.equals(ArticleServiceImpl.ArticleList.NO_INCLUDE_TOP)){
                predicates.add( criteriaBuilder.isFalse(root.get("top")));
                predicates.add(  criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
                        criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY)));

            }else if(articleList.equals(ArticleServiceImpl.ArticleList.ALL_PUBLISH_MODIFY_ARTICLE)){
                predicates.add(  criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
                        criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY)));
            }else if(articleList.equals(ArticleServiceImpl.ArticleList.ALL_ARTICLE)){

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
    public Page<Content> pageContentByCategoryIds(Set<Integer> ids, Boolean isDesc, PageRequest pageRequest){
        Page<Content> contents = contentRepository.findAll(articleSpecification(ids,isDesc, ArticleServiceImpl.ArticleList.NO_INCLUDE_TOP),pageRequest);
        return contents;
    }

    @Override
    public List<Content> listContentByCategoryIds(Set<Integer> ids, Boolean isDesc) {
        return contentRepository.findAll(articleSpecification(ids,isDesc, ArticleServiceImpl.ArticleList.NO_INCLUDE_TOP));
    }


    @Override
    public List<ContentVO> convertToListVo(List<Content> contents) {
//        List<Article> articles = articlePage.getContent();
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
        Set<Integer> userIds = ServiceUtil.fetchProperty(contents, Content::getUserId);
        List<User> users = userService.findAllById(userIds);

        Map<Integer, User> userMap = ServiceUtil.convertToMap(users, User::getId);
//        Set<Integer> categories = ServiceUtil.fetchProperty(articles, Article::getCategoryId);
//        List<CategoryDto> categoryDtos = categoryService.findAllById(categories).stream().map(category -> {
//            CategoryDto categoryDto = new CategoryDto();
//            BeanUtils.copyProperties(category, categoryDto);
//            return categoryDto;
//        }).collect(Collectors.toList());
//        Map<Integer, CategoryDto> categoryMap = ServiceUtil.convertToMap(categoryDtos, CategoryDto::getId);


        List<ContentVO> contentVOS  = contents.stream().map(content -> {
            ContentVO contentVO = new ContentVO();
            BeanUtils.copyProperties(content,contentVO);
            contentVO.setUser(userMap.get(content.getUserId()));

            if(content.getOrder()==null){
                contentVO.setOrder(0);
            }

//            if(categoryMap.containsKey(article.getCategoryId())){
//                articleVO.setCategory( categoryMap.get(article.getCategoryId()));
//
//            }
//            articleVO.setLinkPath(FormatUtil.articleListFormat(article));
            contentVO.setTags(Optional.ofNullable(tagsListMap.get(content.getId()))
                    .orElseGet(LinkedList::new)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(tag->{
                        TagsDto tagsDto = new TagsDto();
                        BeanUtils.copyProperties(tag,tagsDto);
                        return tagsDto;
                    })
                    .collect(Collectors.toList()));
//            articleVO.setTags(tagsListMap.get(article.getId()));
            contentVO.setLinkPath(FormatUtil.articleListFormat(content));

            return contentVO;
        }).collect(Collectors.toList());
        return contentVOS;
    }


    @Override
    public Page<ContentVO> convertToPageVo(Page<Content> contentPage) {
        List<Content> contents = contentPage.getContent();
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
        Set<Integer> userIds = ServiceUtil.fetchProperty(contents, Content::getUserId);
        List<User> users = userService.findAllById(userIds);

        Map<Integer, User> userMap = ServiceUtil.convertToMap(users, User::getId);
        Set<Integer> categories = ServiceUtil.fetchProperty(contents, Content::getCategoryId);
        List<CategoryDto> categoryDtos = categoryService.findAllById(categories).stream().map(category -> {
            CategoryDto categoryDto = new CategoryDto();
            BeanUtils.copyProperties(category, categoryDto);
            return categoryDto;
        }).collect(Collectors.toList());
        Map<Integer, CategoryDto> categoryMap = ServiceUtil.convertToMap(categoryDtos, CategoryDto::getId);


        Page<ContentVO> contentVOS = contentPage.map(content -> {
            ContentVO contentVO = new ContentVO();
            BeanUtils.copyProperties(content,contentVO);
            contentVO.setUser(userMap.get(content.getUserId()));
            if(categoryMap.containsKey(content.getCategoryId())){
                contentVO.setCategory( categoryMap.get(content.getCategoryId()));
            }
            contentVO.setLinkPath(FormatUtil.articleListFormat(content ));
//            articleVO.setLinkPath(FormatUtil.articleListFormat(article));
            contentVO.setTags(Optional.ofNullable(tagsListMap.get(content.getId()))
                    .orElseGet(LinkedList::new)
                    .stream()
                    .filter(Objects::nonNull)
                    .map(tag->{
                        TagsDto tagsDto = new TagsDto();
                        BeanUtils.copyProperties(tag,tagsDto);
                        return tagsDto;
                    })
                    .collect(Collectors.toList()));
//            articleVO.setTags(tagsListMap.get(article.getId()));

            return contentVO;
        });
        return contentVOS;
    }




    @Override
    public void addParentCategory(List<CategoryVO> categoryVOS, Integer parentId){
        if(parentId==0){
            return;
        }
        Category category = categoryService.findById(parentId);
        categoryVOS.add(0,categoryService.covertToVo(category));
        if(category.getParentId()!=0){
            addParentCategory(categoryVOS,category.getParentId());
        }

    }
    public List<CategoryContentList> listCategoryChild(String viewName,Integer page){
        Category parentCategory = categoryService.findByViewName(viewName);
        if(parentCategory==null){
            return null;
        }

        return listCategoryChild(parentCategory.getId(),page);
/*
        List<Category> categories = categoryService.findByParentId(parentCategory.getId());

        List<CategoryContentList> categoryArticleLists =  new ArrayList<>();
        for (Category category:categories){
            CategoryContentList categoryContentList = new CategoryContentList();
            CategoryVO categoryVO = categoryService.covertToVo(category);
            categoryContentList.setCategory(categoryVO);
            ArticleQuery articleQuery = new ArticleQuery();
            articleQuery.setCategoryId(category.getId());
            articleQuery.setDesc(category.getDesc());
            Specification<Content> specification = buildPublishByQuery(articleQuery);
            List<Content> articles = contentRepository.findAll(specification);
            List<ContentVO> articleVOS = convertToListVo(articles);
            List<ContentVO> articleVOTree = super.listWithTree(articleVOS);
            categoryContentList.setContentVOS(articleVOTree);
            categoryArticleLists.add(categoryContentList);
        }
        return categoryArticleLists;
*/
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
        List<ComponentsCategory> componentsCategories = componentsCategoryRepository.findByComponentId(componentsId);
        Set<Integer> categoryIds = ServiceUtil.fetchProperty(componentsCategories, ComponentsCategory::getCategoryId);
        List<Category>  categories = categoryService.listByIdsOrderComponent(categoryIds);
        return listCategoryContent(categories,page);
    }
    @Override
    public List<CategoryContentList> listCategoryContentByComponentsIdSize(int componentsId, Integer size) {
        List<ComponentsCategory> componentsCategories = componentsCategoryRepository.findByComponentId(componentsId);
        Set<Integer> categoryIds = ServiceUtil.fetchProperty(componentsCategories, ComponentsCategory::getCategoryId);
        List<Category>  categories = categoryService.listByIdsOrderComponent(categoryIds);
        return listCategoryConetntSize(categories,size);
    }
    public List<CategoryContentList> listCategoryContent(List<Category> categories,int page){
        List<CategoryContentList> categoryArticleLists =  new ArrayList<>();
        for (Category category:categories){
            CategoryContentList categoryContentList = new CategoryContentList();
            CategoryVO categoryVO = categoryService.covertToVo(category);
            categoryContentList.setCategory(categoryVO);
            ArticleQuery articleQuery = new ArticleQuery();
            articleQuery.setCategoryId(category.getId());
            articleQuery.setDesc(category.getDesc());

            Set<Integer> ids =new HashSet<>();
            List<CategoryVO> categoryVOS = new ArrayList<>();
            ids.add(category.getId());
            categoryService.addChild(categoryVOS,category.getId());
            ids.addAll(ServiceUtil.fetchProperty(categoryVOS, CategoryVO::getId));

            Page<Content> contentsPage = pageContentByCategoryIds(ids, category.getIsDesc(), PageRequest.of(page, category.getArticleListSize()));
            Page<ContentVO> contentVOS = convertToPageVo(contentsPage);

//            Specification<Content> specification = buildPublishByQuery(articleQuery);
//            List<Content> articles = contentRepository.findAll(specification);
//            List<ContentVO> articleVOS = convertToListVo(articles);

//            List<ContentVO> articleVOTree = super.listWithTree(contentVOS);
            List<ContentVO> contentVOList = contentVOS.getContent();
            categoryContentList.setContentVOS(contentVOList);
            categoryArticleLists.add(categoryContentList);
        }
        return categoryArticleLists;
    }
    public List<CategoryContentList> listCategoryChild(Integer id,int page){
        List<Category> categories = categoryService.findByParentId(id);
        return listCategoryContent(categories,page);

    }
    public List<CategoryContentList> listCategoryChild(String viewName) {
        Category parentCategory = categoryService.findByViewName(viewName);
        if (parentCategory == null) {
            return null;
        }
        return listCategoryChild(parentCategory.getId());
    }

    //TUDO
    public List<CategoryContentList> listCategoryConetntSize( List<Category> categories, Integer size){
        List<CategoryContentList> categoryArticleLists =  new ArrayList<>();
        for (Category category:categories){
            CategoryContentList categoryContentList = new CategoryContentList();
            CategoryVO categoryVO = categoryService.covertToVo(category);
            categoryContentList.setCategory(categoryVO);
            ArticleQuery articleQuery = new ArticleQuery();
            articleQuery.setCategoryId(category.getId());
            articleQuery.setDesc(category.getDesc());

            Set<Integer> ids =new HashSet<>();
            List<CategoryVO> categoryVOS = new ArrayList<>();
            ids.add(category.getId());
            categoryService.addChild(categoryVOS,category.getId());
            ids.addAll(ServiceUtil.fetchProperty(categoryVOS, CategoryVO::getId));
            List<ContentVO> contents=listVoTree(ids,category.getIsDesc());
            if(size>contents.size()){
                size=contents.size();
            }
            contents = contents.subList(0, size);
            categoryContentList.setContentVOS(contents);
            categoryArticleLists.add(categoryContentList);
        }
        return categoryArticleLists;
    }
    public List<CategoryContentList> listCategoryContent( List<Category> categories){
        List<CategoryContentList> categoryArticleLists =  new ArrayList<>();
        for (Category category:categories){
            CategoryContentList categoryContentList = new CategoryContentList();
            CategoryVO categoryVO = categoryService.covertToVo(category);
            categoryContentList.setCategory(categoryVO);
            ArticleQuery articleQuery = new ArticleQuery();
            articleQuery.setCategoryId(category.getId());
            articleQuery.setDesc(category.getDesc());

            Set<Integer> ids =new HashSet<>();
            List<CategoryVO> categoryVOS = new ArrayList<>();
            ids.add(category.getId());
            categoryService.addChild(categoryVOS,category.getId());
            ids.addAll(ServiceUtil.fetchProperty(categoryVOS, CategoryVO::getId));
            List<ContentVO> contents=listVoTree(ids,category.getIsDesc());
            categoryContentList.setContentVOS(contents);
            categoryArticleLists.add(categoryContentList);
        }
        return categoryArticleLists;
    }
    public List<CategoryContentList> listCategoryChild(Integer id){
        List<Category> categories = categoryService.findByParentId(id);
        return listCategoryContent(categories);
    }
    @Override
    public CategoryContentListDao findCategoryContentBy(Category category, Template template, int page){
        CategoryVO categoryVO = categoryService.covertToVo(category);
        return findCategoryContentBy(categoryVO, template, page);
    }
    @Override
    public CategoryContentListDao findCategoryContentBy(CategoryVO category, Template template, int page){
        CategoryContentListDao articleListVo = new CategoryContentListDao();

        /**
         * 根据一组id查找article
         * **/
        Set<Integer> ids =new HashSet<>();
        List<CategoryVO> categoryVOS = new ArrayList<>();
        ids.add(category.getId());
        categoryService.addChildFilterRecursive(categoryVOS,category.getId());


//        List<Category> categoryPartner = categoryService.findByParentId(category.getParentId());
//        articleListVo.setPartner(categoryService.convertToListVo(categoryPartner));

        if(categoryVOS.size()!=0){
            ids.addAll(ServiceUtil.fetchProperty(categoryVOS, CategoryVO::getId));
            List<CategoryVO> categoryVOSTree = categoryService.listWithTree(categoryVOS,category.getId());
            articleListVo.setChildren(categoryVOSTree);
        }
        if(category.getParentId()!=0){
            // add forward parent
//            Category parentCategory = categoryService.findById(category.getParentId());
//            CategoryVO parentCategoryVO = categoryService.covertToVo(parentCategory);
//            articleListVo.setParentCategory(parentCategoryVO);
//
//
//            List<CategoryVO> categoryVOSParent = new ArrayList<>();
//            categoryVOSParent.add(parentCategoryVO);
//            addParentCategory(categoryVOSParent,parentCategoryVO.getParentId());

            // add first parent
            List<CategoryVO> categoryVOSParent = new ArrayList<>();
            addParentCategory(categoryVOSParent,category.getParentId());
            CategoryVO categoryVO = categoryVOSParent.get(0);
            articleListVo.setParentCategory(categoryVO);
            articleListVo.setParentCategories(categoryVOSParent);

        }


        if(template.getTemplateData().equals(TemplateData.ARTICLE_TREE)){
            List<ContentVO> contents=listVoTree(ids,category.getIsDesc());
            articleListVo.setContents(contents);
        }else if(template.getTemplateData().equals(TemplateData.CATEGORY_CHILD_PAGE)){
            List<CategoryContentList> categoryContentLists = listCategoryChild(category.getId(), page);
            articleListVo.setCategoryContentLists(categoryContentLists);
            List<ContentVO> allVos = new ArrayList<>();
            for(CategoryContentList categoryContentList: categoryContentLists){
                List<ContentVO> contentVOS = categoryContentList.getContentVOS();
                allVos.addAll(contentVOS);
            }
            articleListVo.setContents(allVos);
        }else if(template.getTemplateData().equals(TemplateData.CATEGORY_CHILD_TREE)){
            List<CategoryContentList> categoryContentLists = listCategoryChild(category.getId());
            articleListVo.setCategoryContentLists(categoryContentLists);
            List<ContentVO> allVos = new ArrayList<>();
            for(CategoryContentList categoryContentList: categoryContentLists){
                List<ContentVO> contentVOS = categoryContentList.getContentVOS();
                allVos.addAll(contentVOS);
            }
            articleListVo.setContents(allVos);
        }else {
//            Page<Article> articles = pageArticleByCategoryIds(articleSpecification(ids,category.getIsDesc(), ArticleServiceImpl.ArticleList.NO_INCLUDE_TOP),PageRequest.of(page,category.getArticleListSize()));
            Page<Content> contentsPage = pageContentByCategoryIds(ids, category.getIsDesc(), PageRequest.of(page, category.getArticleListSize()));
            Page<ContentVO> contentVOS = convertToPageVo(contentsPage);
            int totalPages = contentVOS.getTotalPages();
            int size = contentVOS.getSize();
            long totalElements = contentVOS.getTotalElements();
            articleListVo.setTotalPages(totalPages);
            articleListVo.setSize(size);
            articleListVo.setTotalElements(totalElements);
            List<ContentVO> contents = contentVOS.getContent();
            articleListVo.setContents(contents);
        }


        articleListVo.setCategory(category);
        articleListVo.setViewName(category.getViewName());
        articleListVo.setPath(category.getPath());
        articleListVo.setPage(page);
        /**
         * 分页路径的格式生成
         */
        articleListVo.setLinkPath(FormatUtil.categoryListFormat(category));
        return articleListVo;
    }



    @Override
    public List<ContentVO> listVoTree(Integer categoryId) {
        Category category = categoryService.findById(categoryId);
        Set<Integer> ids =new HashSet<>();
        ids.add(category.getId());

        List<Category> categories = new ArrayList<>();
        addChildAllIds(categories,category.getId());
        ids.addAll(ServiceUtil.fetchProperty(categories, Category::getId));


        return listVoTree(ids,category.getDesc());
//        ArticleQuery articleQuery = new ArticleQuery();
//        articleQuery.setCategoryId(category.getId());
//        articleQuery.setDesc(category.getDesc());
//        Specification<Article> specification = buildPublishByQuery(articleQuery);
//        List<Article> articles = articleRepository.findAll(specification);
////                .stream().map(article -> {
////            ArticleVO articleVO = new ArticleVO();
////            BeanUtils.copyProperties(article, articleVO);
////            return articleVO;
////        }).collect(Collectors.toList());
//        List<ArticleVO> articleVOS = convertToListVo(articles);
//        List<ArticleVO> articleVOTree = super.listWithTree(articleVOS);
////        List<ArticleDto> listWithTree = listWithTree(articleDtos);
//        return articleVOTree;
    }
    @Override
    public List<ContentVO> listVoTree(Set<Integer> ids, Boolean isDesc) {

//        ArticleQuery articleQuery = new ArticleQuery();
//        articleQuery.setCategoryId(category.getId());
//        articleQuery.setDesc(category.getDesc());



        Specification<Content> specification =  articleSpecification(ids,isDesc, ArticleServiceImpl.ArticleList.NO_INCLUDE_TOP);
        List<Content> contents = contentRepository.findAll(specification);
//                .stream().map(article -> {
//            ArticleVO articleVO = new ArticleVO();
//            BeanUtils.copyProperties(article, articleVO);
//            return articleVO;
//        }).collect(Collectors.toList());
        List<ContentVO> contentVOS = convertToListVo(contents);
        List<ContentVO> contentVOTree = super.listWithTree(contentVOS);
//        List<ArticleDto> listWithTree = listWithTree(articleDtos);
        return contentVOTree;
    }



    @Override
    public List<ContentVO> listArticleVOBy(String viewName){
        Category category = categoryService.findByViewName(viewName);
        if(category==null){
            return null;
        }
        Set<Integer> ids = new HashSet<>();
        ids.add(category.getId());
        List<Content> contents = contentRepository.findAll(articleSpecification(ids, true, ArticleServiceImpl.ArticleList.NO_INCLUDE_TOP), Sort.by("order"));
        List<ContentVO> contentVOS = convertToListVo(contents);
        return contentVOS;
    }


    @Override
    public ContentDetailVO updateCategory(Content content, Integer baseCategoryId) {
        Optional<Category> category = categoryService.findOptionalById(baseCategoryId);
        ContentDetailVO contentDetailVO = new ContentDetailVO();
        contentDetailVO.setContent(content);
        content.setParentId(0);
        if(category.isPresent()){
            content.setPath(category.get().getPath());
            content.setCategoryId(category.get().getId());
            contentDetailVO.setCategory(category.get());
            content.setTemplateName(category.get().getArticleTemplateName());

        }else {
            content.setCategoryId(0);
        }
        contentRepository.save(content);
        return contentDetailVO;


    }





}
