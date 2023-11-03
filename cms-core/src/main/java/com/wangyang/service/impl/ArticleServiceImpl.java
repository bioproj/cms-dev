package com.wangyang.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ArticleException;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ImageUtils;
import com.wangyang.common.utils.MarkdownUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.dto.*;
import com.wangyang.pojo.entity.*;
import com.wangyang.pojo.entity.relation.ArticleTags;
import com.wangyang.pojo.enums.ArticleStatus;
import com.wangyang.common.enums.CrudType;
import com.wangyang.pojo.enums.RelationType;
import com.wangyang.pojo.params.ArticleQuery;
import com.wangyang.pojo.vo.ArticleDetailVO;
import com.wangyang.pojo.vo.ArticleVO;
import com.wangyang.pojo.vo.BaseCategoryVo;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.repository.*;
import com.wangyang.repository.relation.ArticleTagsRepository;
import com.wangyang.repository.template.ComponentsArticleRepository;
import com.wangyang.repository.template.ComponentsCategoryRepository;
import com.wangyang.service.templates.IComponentsArticleService;
import com.wangyang.service.templates.ITemplateService;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.service.IArticleService;
import com.wangyang.service.ICategoryService;
import com.wangyang.service.base.AbstractContentServiceImpl;
import com.wangyang.util.FormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


/**
 * ArticleServiceImpl
 *
 * @author wangyang
 */
@Service
@Transactional
@Slf4j
public class ArticleServiceImpl extends AbstractContentServiceImpl<Article,Article,ArticleVO> implements IArticleService {

    enum ArticleList{
        INCLUDE_TOP,
        NO_INCLUDE_TOP,
        ALL_PUBLISH_MODIFY_ARTICLE,
        ALL_ARTICLE
    }



    @Autowired
    TagsRepository tagsRepository;
    @Autowired
    ArticleTagsRepository articleTagsRepository;
    @Autowired
    ICategoryService categoryService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ComponentsArticleRepository componentsArticleRepository;
    @Autowired
    IUserService userService;
    @Autowired
    IComponentsArticleService componentsArticleService;



    @Autowired
    ITemplateService templateService;

    @Autowired
    ComponentsCategoryRepository componentsCategoryRepository;

    private  ArticleRepository articleRepository;
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        super(articleRepository);
        this.articleRepository = articleRepository;
    }



    private Specification<Article> buildAllByQuery(ArticleQuery articleQuery) {
        return (Specification<Article>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = listPredicate(articleQuery, root, criteriaBuilder, query);
            if (articleQuery.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), articleQuery.getStatus()));
            }
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
    private Specification<Article> articleSpecification(int  categoryId,ArticleList articleList){
//        Category category =new Category();
//        category.setId(categoryId);
//        category.setDesc(true);
        Set<Integer> ids = new HashSet<>();
        ids.add(categoryId);
        return articleSpecification(ids,true,articleList);
    }
    private Specification<Article> articleSpecification(Set<Integer> ids,Boolean isDesc,ArticleList articleList){
        Specification<Article> specification = (root, criteriaQuery, criteriaBuilder) -> {
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


//    /**
//     * @param categoryId
//     * @return
//     */
//    private Specification<Article> queryByCategory(int categoryId){
//        Specification<Article> specification = new Specification<Article>() {
//            @Override
//            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//
//                criteriaQuery.where(criteriaBuilder.equal(root.get("categoryId"),categoryId),
//                                criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
//                                        criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY))
//                );
//                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("order")),criteriaBuilder.desc(root.get("id")));
//                return  criteriaQuery.getRestriction();
//            }
//        };
//        return specification;
//    }
//    /**
//     * @param category
//     * @return
//     */
//    private Specification<Article> queryByCategory(Category category){
//        Specification<Article> specification = new Specification<Article>() {
//            @Override
//            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//
//                criteriaQuery.where(criteriaBuilder.equal(root.get("categoryId"),category.getId()),
//                        criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
//                                criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY))
////                        criteriaBuilder.notEqual(root.get("status"),ArticleStatus.RECYCLE)
//                );
//                if(category.getDesc()){
//                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get("order")),criteriaBuilder.desc(root.get("id")));
//                }else {
//                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get("order")),criteriaBuilder.desc(root.get("id")));
//
//                }
//                return  criteriaQuery.getRestriction();
//            }
//        };
//        return specification;
//    }
//
//
//
//    /**
//     * 除去置顶文章查询条件
//     * @param category
//     * @return
//     */
//    private Specification<Article> queryArticleDtoNoTopByCategory(Category category ){
//        Specification<Article> specification = new Specification<Article>() {
//            @Override
//            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//
//                criteriaQuery.where(criteriaBuilder.equal(root.get("categoryId"),category.getId()),
//                        criteriaBuilder.isFalse(root.get("top")),
//                        criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
//                                criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY))
//
//                );
//                if(category.getDesc()){
//                    criteriaQuery.orderBy(criteriaBuilder.desc(root.get("order")),criteriaBuilder.desc(root.get("id")));
//                }else {
//                    criteriaQuery.orderBy(criteriaBuilder.asc(root.get("order")),criteriaBuilder.desc(root.get("id")));
//
//                }
//
//                return  criteriaQuery.getRestriction();
//            }
//        };
//        return specification;
//    }
//
//    /**
//     * 置顶文章查询条件
//     * @param categoryId
//     * @return
//     */
//    private Specification<Article> queryListByCategory(int categoryId){
//        Specification<Article> specification = new Specification<Article>() {
//            @Override
//            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//
//                criteriaQuery.where(criteriaBuilder.equal(root.get("categoryId"),categoryId),
////                        ,criteriaBuilder.isTrue(root.get("haveHtml")),
//                        criteriaBuilder.isTrue(root.get("top"))
//                );
//                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("order")),criteriaBuilder.desc(root.get("id")));
//                return  criteriaQuery.getRestriction();
//            }
//        };
//        return specification;
//    }
//


    /**
     * create article
     * @param tagsIds
     * @return
     */
    @Override
    public ArticleDetailVO createArticleDetailVo(Article article, Set<Integer> tagsIds) {
        if(article.getOrder()==null){
            int count = articleRepository.countBycategoryId(article.getCategoryId());
            article.setOrder(count+1);
        }
        if(article.getParentId()==null){
            article.setParentId(0);
        }
        if(article.getDirection()==null){
            article.setDirection("right");// 脑图方向向左
        }
        if(article.getExpanded()== null){
            article.setExpanded(false);// 脑图默认不展开
        }

//        article.setStatus(ArticleStatus.PUBLISHED);
//        article.setHaveHtml(true);
        article.setTop(false);
        ArticleDetailVO articleDetailVO = createOrUpdateArticle(article, tagsIds);
        return articleDetailVO;
    }



    @Override
    public ArticleDetailVO updateArticleDetailVo(Article article,  Set<Integer> tagsIds) {
        if(article.getOrder()==null){
            int count = articleRepository.countBycategoryId(article.getCategoryId());
            article.setOrder(count+1);
        }
        article.setPdfPath(null);
//        article.setStatus(ArticleStatus.PUBLISHED);
//        article.setHaveHtml(true);
        article.setUpdateDate(new Date());


        //TODO temp delete all tags and category before update
        articleTagsRepository.deleteByArticleId(article.getId());

        ArticleDetailVO articleDetailVO = createOrUpdateArticle(article, tagsIds);
        return articleDetailVO;
    }


    @Override
    public ArticleDetailVO updateArticleDetailVo(Article article) {
        article.setPdfPath(null);
//        article.setStatus(ArticleStatus.PUBLISHED);
        // 文章发布默认生成HTML
//        if(article.getHaveHtml()==null){
//            article.setHaveHtml(true);
//        }
        ArticleDetailVO articleDetailVO = createOrUpdateArticle(article, null);
        return articleDetailVO;
    }


    @Override
    public Article save(Article article){
        return  articleRepository.save(article);
    }

    /**
     * 保存或者更新文章草稿
     * @param article
     * @return
     */
    @Override
    public Article updateArticleDraft(Article article,boolean more){

        if(article.getUserId()==null){
            throw new ArticleException("文章用户不能为空!!");
        }

        String viewName = article.getViewName();
        if(viewName==null||"".equals(viewName)){
            viewName = CMSUtils.randomViewName();
            log.debug("!!! view name not found, use "+viewName);
            article.setViewName(viewName);
        }
//        article.setStatus(ArticleStatus.DRAFT);
        if(more){
            article=super.createOrUpdate(article);
        }
        return  articleRepository.save(article);
    }

    @Override
    public Article saveArticleDraft(Article article,boolean more){




        if(article.getCategoryId()==null){
            throw new ArticleException("文章类别不能为空!!");
        }
        if(article.getParentId()==null){
            article.setParentId(0);
        }
        if(article.getDirection()==null){
            article.setDirection("right");// 脑图方向向左
        }
        if(article.getExpanded()== null){
            article.setExpanded(false);// 脑图默认不展开
        }

        if(article.getUserId()==null){
            throw new ArticleException("文章用户不能为空!!");
        }
        article.setTop(false);

        String viewName = article.getViewName();
        if(viewName==null||"".equals(viewName)){
            viewName = CMSUtils.randomViewName();
            log.debug("!!! view name not found, use "+viewName);
            article.setViewName(viewName);
        }
        article.setStatus(ArticleStatus.DRAFT);
        if(more){
            article=super.createOrUpdate(article);
        }
        return  articleRepository.save(article);
    }

    @Override
    public Article recycle(int id) {
        Article article = findArticleById(id);
        Article findArticle = new Article();
        BeanUtils.copyProperties(article,findArticle);
        article.setStatus(ArticleStatus.RECYCLE);
        article.setTop(false);
        article.setOpenComment(false);
        articleRepository.save(article);
        return findArticle;
    }

    @Override
    public Article deleteByArticleId(int id) {
        Article article = findArticleById(id);
        List<Article> articles = findByParentId(article.getId());
        if(articles.size()!=0){
            throw new ObjectException("文章存在子类不能删除！");
        }

        log.debug(">>> delete comment");
//        commentRepository.deleteByArticleId(id);
        log.debug(">>> delete article tags");
        articleTagsRepository.deleteByArticleId(id);
        log.debug("delete article");
        articleRepository.deleteById(id);
        Category category = categoryService.findById(article.getCategoryId());
        super.injectContent(article,category);
        return article;
    }

//    @Override
//    public ArticleDetailVO createOrUpdateArticleVo(Article article, Set<Integer> tagsIds) {
//        ArticleDetailVO saveArticle = createOrUpdateArticle(article, tagsIds);
//        // crate value object
////        ArticleDetailVO articleVO = convert(saveArticle,tagsIds);
//        return saveArticle;
//    }

    public ArticleDetailVO createOrUpdateArticle(Article article, Set<Integer> tagsIds) {
//        if(article.getUserId()==null){
//            throw new ArticleException("文章用户不能为空!!");
//        }
//        if(article.getCategoryId()==null){
//            throw new ArticleException("文章类别不能为空!!");
//        }
//        if(article.getStatus()!=ArticleStatus.INTIMATE){
//            article.setStatus(ArticleStatus.PUBLISHED);
//        }
//
//
//        String viewName = article.getViewName();
//        if(viewName==null||"".equals(viewName)){
//            viewName = CMSUtils.randomViewName();
//            log.debug("!!! view name not found, use "+viewName);
//            article.setViewName(viewName);
//        }
////        article.setHaveHtml(true);
//
//        //设置评论模板
//        if(article.getCommentTemplateName()==null){
//            article.setCommentTemplateName(CmsConst.DEFAULT_COMMENT_TEMPLATE);
//        }
//        Category category = categoryService.findById(article.getCategoryId());
//
//
//        article.setCategoryPath(category.getPath());
//        article.setCategoryViewName(category.getViewName());
//        article.setIsArticleDocLink(category.getIsArticleDocLink());
//
////        if(article.getTemplateName()==null){
////            //由分类管理文章的模板，这样设置可以让文章去维护自己的模板
////
////        }
//        article.setTemplateName(category.getArticleTemplateName());
////        if(article.getUseTemplatePath()!=null && article.getUseTemplatePath()){
////            Template template = templateService.findByEnName(category.getTemplateName());
////            article.setPath(template.getPath());
////        }
//
//        if(category.getArticleUseViewName()){
//            article.setPath(category.getPath()+File.separator+category.getViewName());
//        }else {
//            article.setPath(category.getPath());
//        }
//
//
//
//
//
////        if(article.getPath()==null || article.getPath().equals("")){
////            article.setPath(CMSUtils.getArticlePath());
////        }
//
////        article.setPath(CMSUtils.getArticlePath());
////        article.setPath(CMSUtils.getArticlePath());
//
//
//
//        article = super.createOrUpdate(article);
//        //图片展示
//        if(article.getPicPath()==null|| "".equals(article.getPicPath())){
//            String imgSrc = ImageUtils.getImgSrc(article.getOriginalContent());
//            article.setPicPath(imgSrc);
//        }
//        generateSummary(article);
//
////        保存文章
//        Article saveArticle = articleRepository.save(article);
//        super.injectContent(article,category);

        ArticleVO articleVO = super.createOrUpdateArticle(article, tagsIds);
        ArticleDetailVO articleDetailVO = convert(articleVO, tagsIds);



//        articleDetailVO.setCategory(categoryService.covertToVo(category));
////        articleDetailVO.setUpdateChannelFirstName(true);
//        BeanUtils.copyProperties(saveArticle,articleDetailVO);
//        // 添加标签
//        if (!CollectionUtils.isEmpty(tagsIds)) {
//            // Get Article tags
//            List<ArticleTags> articleTagsList = tagsIds.stream().map(tagId -> {
//                ArticleTags articleTags = new ArticleTags();
//                articleTags.setTagsId(tagId);
//                articleTags.setArticleId(saveArticle.getId());
//                return articleTags;
//            }).collect(Collectors.toList());
//            //save article tags
//            articleTagsRepository.saveAll(articleTagsList);
//            articleDetailVO.setTagIds(tagsIds);
//            List<Tags> tags = tagsRepository.findAllById(tagsIds);
//            articleDetailVO.setTags(tags);
//
//        }
//        //添加用户
//        User user = userService.findById(article.getUserId());
//        articleDetailVO.setUser(user);
//        articleDetailVO.setCommentPath( article.getPath()+ CMSUtils.getComment()+ File.separator +article.getViewName());
        return articleDetailVO;
    }


    private ArticleDetailVO convert(Article saveArticle, Set<Integer> tagsIds) {
        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        BeanUtils.copyProperties(saveArticle,articleDetailVO);

        //find tags
        if(!CollectionUtils.isEmpty(tagsIds)){
            articleDetailVO.setTagIds(tagsIds);
            List<Tags> tags = tagsRepository.findAllById(tagsIds);
            articleDetailVO.setTags(tags);
        }

        if(saveArticle.getCategoryId()!=null){
            Category category = categoryService.findById(saveArticle.getCategoryId());
            CategoryVO categoryVO = categoryService.convertToVo(category);

            articleDetailVO.setCategory(categoryVO);
        }
        articleDetailVO.setLinkPath( FormatUtil.articleFormat(articleDetailVO));
        return articleDetailVO;
    }

    /**
     * 为文章只添加标签
     * @param article
     * @return
     */
    @Override
    public ArticleDetailVO conventToAddTags(Article article){
        ArticleDetailVO articleDetailVo = new ArticleDetailVO();
        BeanUtils.copyProperties(article,articleDetailVo);
        //find tags
        List<Tags> tags = tagsRepository.findTagsByArticleId(article.getId());
        if(!CollectionUtils.isEmpty(tags)){
            articleDetailVo.setTags(tags);
            articleDetailVo.setTagIds( ServiceUtil.fetchProperty(tags, Tags::getId));
        }
        //添加用户
        User user = userService.findById(article.getUserId());
        articleDetailVo.setUser(user);
        articleDetailVo.setLinkPath( FormatUtil.articleFormat(articleDetailVo));
        return articleDetailVo;
    }

    /**
     * 为文章添加类别和标签
     * @param article
     * @return
     */
    @Override
    public ArticleDetailVO convert(Article article) {
        Category category = categoryService.findById(article.getCategoryId());
        ArticleVO articleVO = convertToVo(article);
        return convert(articleVO,null);
    }
    public ArticleDetailVO convert(ArticleVO articleVO,Set<Integer> tagsIds) {
//        ArticleDetailVO articleDetailVo = new ArticleDetailVO();
//        BeanUtils.copyProperties(article,articleDetailVo);
//
//        //find tags

        if(articleVO.getCategoryId()==null){
            throw  new ArticleException("文章["+articleVO.getTitle()+"]的没有指定类别!!");
        }
//
//        User user = userService.findById(article.getUserId());
//        articleDetailVo.setUser(user);
//        Optional<Category> optionalCategory = categoryService.findOptionalById(article.getCategoryId());
//        if(optionalCategory.isPresent()){
////            throw new ObjectException("文章为名称："+article.getTitle()+" 文章为Id："+article.getId()+"分类没有找到！");
//            if(articleDetailVo.getTemplateName()==null){
//                articleDetailVo.setTemplateName(optionalCategory.get().getArticleTemplateName());
//            }
//            articleDetailVo.setCategory(categoryService.covertToVo(optionalCategory.get()));
//        }
        BaseCategoryVo category = articleVO.getCategory();
        ArticleDetailVO articleDetailVO = new ArticleDetailVO();
        articleDetailVO.setCategory(category);
//        articleDetailVO.setUpdateChannelFirstName(true);
        BeanUtils.copyProperties(articleVO,articleDetailVO);
        // 添加标签
        if (tagsIds!=null && !CollectionUtils.isEmpty(tagsIds)) {
            // Get Article tags
            List<ArticleTags> articleTagsList = tagsIds.stream().map(tagId -> {
                ArticleTags articleTags = new ArticleTags();
                articleTags.setRelationId(tagId);
                articleTags.setRelationType(RelationType.ARTICLE);
                articleTags.setArticleId(articleVO.getId());
                return articleTags;
            }).collect(Collectors.toList());
            //save article tags
            articleTagsRepository.saveAll(articleTagsList);
            articleDetailVO.setTagIds(tagsIds);
            List<Tags> tags = tagsRepository.findAllById(tagsIds);
            articleDetailVO.setTags(tags);

        }else {
            List<Tags> tags = tagsRepository.findTagsByArticleId(articleVO.getId());
            if(!CollectionUtils.isEmpty(tags)){
                articleDetailVO.setTags(tags);
                articleDetailVO.setTagIds( ServiceUtil.fetchProperty(tags, Tags::getId));
            }
        }



        //添加用户
        User user = userService.findById(articleVO.getUserId());
        articleDetailVO.setUser(user);
        articleDetailVO.setCommentPath( articleVO.getPath()+ CMSUtils.getComment()+ File.separator +articleVO.getViewName());
        articleDetailVO.setLinkPath( FormatUtil.articleFormat(articleDetailVO));

        if(articleVO.getToc()!=null){
            List<Toc> toc = MarkdownUtils.getToc(articleVO.getToc());
            articleDetailVO.setTocList(toc);
        }

        return articleDetailVO;
    }




    @Override
    public  List<Article>  listHaveHtml(){
        List<Article> articles = articleRepository.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
                        criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY));
            }
        });
        return articles;
    }

    @Override
    public ArticleDetailVO findArticleAOById(int id) {

        return conventToAddTags(findArticleById(id));
//        return convert(findArticleById(id));
    }

    @Override
    public Article findArticleById(int id) {
        Optional<Article> optionalArticle = articleRepository.findById(id);
        if(!optionalArticle.isPresent()){
            throw new ObjectException("Article is not found");
        }
        return optionalArticle.get();
    }




    @Override
    public Article findByIdAndUserId(int id, int userId){
        Article article = articleRepository.findByIdAndUserId(id, userId);
        if(article==null){
            throw new ObjectException("用户为"+userId+"的文章不存在！");
        }
        return article;
    }



    @Override
    public Page<ArticleDto> convertToSimple(Page<Article> articlePage) {
        List<Article> articles = articlePage.getContent();
        Set<Integer> userIds = ServiceUtil.fetchProperty(articles, Article::getUserId);
        List<User> users = userService.findAllById(userIds);

        Map<Integer, User> userMap = ServiceUtil.convertToMap(users, User::getId);

        return  articlePage.map(article -> {
            ArticleDto articleDto = new ArticleDto();
            articleDto.setUser(userMap.get(article.getUserId()));
            BeanUtils.copyProperties(article,articleDto);
            articleDto.setLinkPath(FormatUtil.articleListFormat(article));
            return articleDto;
        });

    }




    /**
     * 添加为分裂数据添加category
     * @param articlePage
     * @return
     */
//    @Override
//    public Page<ArticleVO> convertToAddCategory(Page<Article> articlePage) {
//        List<Article> articles = articlePage.getContent();
//        Set<Integer> categories = ServiceUtil.fetchProperty(articles, Article::getCategoryId);
//        List<CategoryDto> categoryDtos = categoryService.findAllById(categories).stream().map(category -> {
//            CategoryDto categoryDto = new CategoryDto();
//            BeanUtils.copyProperties(category, categoryDto);
//            return categoryDto;
//        }).collect(Collectors.toList());
//        Map<Integer, CategoryDto> categoryMap = ServiceUtil.convertToMap(categoryDtos, CategoryDto::getId);
//        Page<ArticleVO> articleVOS = articlePage.map(article -> {
//            ArticleVO articleVO = new ArticleVO();
//            BeanUtils.copyProperties(article,articleVO);
//            if(categoryMap.containsKey(article.getCategoryId())){
//                articleVO.setCategory( categoryMap.get(article.getCategoryId()));
//            }
//            return articleVO;
//        });
//        return articleVOS;
//    }

    @Override
    public Page<ArticleVO> convertToPageVo(Page<Article> articlePage) {
        List<Article> articles = articlePage.getContent();
        //Get article Ids
        Set<Integer> articleIds = ServiceUtil.fetchProperty(articles, Article::getId);

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
        Set<Integer> userIds = ServiceUtil.fetchProperty(articles, Article::getUserId);
        List<User> users = userService.findAllById(userIds);

        Map<Integer, User> userMap = ServiceUtil.convertToMap(users, User::getId);
//        Set<Integer> categories = ServiceUtil.fetchProperty(articles, Article::getCategoryId);
//        List<CategoryDto> categoryDtos = categoryService.findAllById(categories).stream().map(category -> {
//            CategoryDto categoryDto = new CategoryDto();
//            BeanUtils.copyProperties(category, categoryDto);
//            return categoryDto;
//        }).collect(Collectors.toList());
//        Map<Integer, CategoryDto> categoryMap = ServiceUtil.convertToMap(categoryDtos, CategoryDto::getId);


        Page<ArticleVO> articleVOS = articlePage.map(article -> {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(article,articleVO);
            articleVO.setUser(userMap.get(article.getUserId()));
//            if(categoryMap.containsKey(article.getCategoryId())){
//                articleVO.setCategory( categoryMap.get(article.getCategoryId()));
//
//            }
//            articleVO.setLinkPath(FormatUtil.articleListFormat(article));
            articleVO.setTags(Optional.ofNullable(tagsListMap.get(article.getId()))
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
            articleVO.setLinkPath(FormatUtil.articleListFormat(article));
            return articleVO;
        });
        return articleVOS;
    }
    @Override
    public List<ArticleVO> convertToListVo(List<Article> articles) {
//        List<Article> articles = articlePage.getContent();
        //Get article Ids
        Set<Integer> articleIds = ServiceUtil.fetchProperty(articles, Article::getId);

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
        Set<Integer> userIds = ServiceUtil.fetchProperty(articles, Article::getUserId);
        List<User> users = userService.findAllById(userIds);

        Map<Integer, User> userMap = ServiceUtil.convertToMap(users, User::getId);
        Set<Integer> categories = ServiceUtil.fetchProperty(articles, Article::getCategoryId);
        List<Category> categoryList = categoryService.findAllById(categories);
        List<CategoryVO> categoryVOS = categoryService.convertToListVo(categoryList);
//        .stream().map(category -> {
//            CategoryDto categoryDto = new CategoryDto();
//            BeanUtils.copyProperties(category, categoryDto);
//            return categoryDto;
//        }).collect(Collectors.toList());

        Map<Integer, CategoryVO> categoryMap = ServiceUtil.convertToMap(categoryVOS, CategoryVO::getId);


        List<ArticleVO> articleVOS = articles.stream().map(article -> {
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(article,articleVO);
            articleVO.setUser(userMap.get(article.getUserId()));
            if(categoryMap.containsKey(article.getCategoryId())){
                articleVO.setCategory( categoryMap.get(article.getCategoryId()));

            }
//            articleVO.setLinkPath(FormatUtil.articleListFormat(article));
            articleVO.setTags(Optional.ofNullable(tagsListMap.get(article.getId()))
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
            articleVO.setLinkPath(FormatUtil.articleListFormat(article));
            return articleVO;
        }).collect(Collectors.toList());
        return articleVOS;
    }


//    @Override
//    public Page<ArticleDto> articleShow(Specification<Article> specification, Pageable pageable){
//        Page<Article> articles = articleRepository.findAll(specification, pageable);
//        return convertArticle2ArticleDto(articles);
//    }
//    @Override
//    public List<ArticleDto> articleShow(Specification<Article> specification,Sort sort){
//        List<Article> articles = articleRepository.findAll(specification,sort);
//        return articles.stream().map(article -> {
//            ArticleDto articleDto = new ArticleDto();
//            BeanUtils.copyProperties(article,articleDto);
//            return articleDto;
//        }).collect(Collectors.toList());
//    }

    @Override
    public int increaseLikes(int id) {
        int affectedRows = articleRepository.updateLikes(id);
        return affectedRows;
    }

    @Override
    public Integer getLikesNumber(int id){
        Integer likesNumber = articleRepository.getLikesNumber(id);

        return likesNumber;
    }
    @Override
    public int increaseVisits(int id) {
        int affectedRows = articleRepository.updateVisits(id);
        return affectedRows;
    }

    @Override
    public Integer getVisitsNumber(int id){
        Integer likesNumber = articleRepository.getVisitsNumber(id);

        return likesNumber;
    }



//
//    @Override
//    public Article haveHtml(int id){
//        Article article = findArticleById(id);
//        if(article.getCategoryId()==null){
//            throw new ArticleException("文章别为空不能生成文章，请编辑添加类别！");
//        }
//        if(article.getStatus()==ArticleStatus.DRAFT){
//            //草稿文章要生成Html,将文章状态改为发布
//            article.setStatus(ArticleStatus.PUBLISHED);
//            article =super.createOrUpdate(article);
//            article.setUpdateDate(new Date());
//            Category category = categoryService.findById(article.getCategoryId());
//            article.setPath(category.getPath());
//            article.setTemplateName(category.getArticleTemplateName());
//            // 生成摘要
//            generateSummary(article);
//        }
//        if(article.getHaveHtml()){
//            article.setHaveHtml(false);
//        }else {
//            article.setHaveHtml(true);
//        }
//        articleRepository.save(article);
//        return article;
//    }


    /**
     * 打开或者关闭评论
     * @param id
     * @return
     */
    @Override
    public Article openComment(int id){
        Article article = findArticleById(id);
        if(article.getOpenComment()==null|| article.getCommentTemplateName()==null){
            article.setOpenComment(true);
            article.setCommentTemplateName(CmsConst.DEFAULT_COMMENT_TEMPLATE);
            return  articleRepository.save(article);
        }

        if(article.getOpenComment()){
            article.setOpenComment(false);
        }else {
            article.setOpenComment(true);
        }
        articleRepository.save(article);
        return article;
    }




    /**
     * 更改文章类别
     * @param article
     * @param categoryId
     * @return
     */
    @Override
    public ArticleDetailVO updateArticleCategory(Article article, int categoryId){
        if(article.getUserId()==null){
            throw new ArticleException("文章用户不能为空!!");
        }
        if(article.getCategoryId()==null){
            throw new ArticleException("文章类别不能为空!!");
        }
        Category oldCategory = categoryService.findById(article.getCategoryId());
        super.injectBeforeCategory(oldCategory);
//        if(article.getStatus()!=ArticleStatus.PUBLISHED){
//            throw new ArticleException("文章没有发布不能更改类别!!");
//        }

//        article.setTitle(updateArticle.getTitle());
//        article.setOriginalContent(updateArticle.getOriginalContent());
//        article.setUserId(updateArticle.getUserId());
        Category category = categoryService.findById(categoryId);
        //文章路径
//        article.setPath();
        article.setTemplateName(category.getArticleTemplateName());

//        if(baseCategory instanceof  Channel) {
//            article.setPath(baseCategory.getPath() + "/" + baseCategory.getName());
//            article.setTemplateName(((Channel) baseCategory).getArticleTemplateName());
//        }else {
//            article.setPath("article");
//            article.setTemplateName(CmsConst.DEFAULT_ARTICLE_TEMPLATE);
//        }
        article.setCategoryId(categoryId);
        article.setParentId(0);
        Article saveArticle = articleRepository.save(article);
        ArticleDetailVO articleDetailVO = conventToAddTags(saveArticle);
        articleDetailVO.setCategory(categoryService.convertToVo(category));
        injectContent(article,category);
        return articleDetailVO;
    }


//    @Override
//    public CategoryArticleListDao findCategoryArticleBy(Category category) {
//        CategoryArticleListDao articleListVo = new CategoryArticleListDao();
//
////        Page<Article> articles = articleRepository.findAll(articleSpecification(category, ArticleList.NO_INCLUDE_TOP),PageRequest.of(page,category.getArticleListSize()));
////        Page<ArticleVO> articleVOS = convertToListVo(articles);
//        List<ArticleVO> articleVOS = listVoTree(category.getId());
//        articleListVo.setList(articleVOS);
//        articleListVo.setCategory(category);
//        articleListVo.setViewName(category.getViewName());
//        articleListVo.setPath(category.getPath());
//        /**
//         * 分页路径的格式生成
//         */
//        articleListVo.setLinkPath(FormatUtil.categoryList2Format(category));
//        return articleListVo;
//    }

    /**
     * 动态分页使用
     * @param category
     * @return
     */

    @Override
    public CategoryArticleListDao findCategoryArticleBy(Category category, Template template,int page){
        return findCategoryArticleBy(categoryService.convertToVo(category),template,page);
    }

    @Override
    public void addParentCategory(List<CategoryVO> categoryVOS, Integer parentId){
        if(parentId==0){
            return;
        }
        Category category = categoryService.findById(parentId);
        categoryVOS.add(0,categoryService.convertToVo(category));
        if(category.getParentId()!=0){
            addParentCategory(categoryVOS,category.getParentId());
        }

    }


    public void addChildIds( List<CategoryVO> categoryVOS, Integer id){
        List<Category> categories = categoryService.findByParentId(id);
        if(categories.size()==0){
            return;
        }
        categoryVOS.addAll(categoryService.convertToListVo(categories));
        if(categories.size()!=0){
            for (Category category:categories){
                addChildIds(categoryVOS,category.getId());
            }
        }

    }

    @Override
    public Page<Article> pageArticleByCategoryIds(Set<Integer> ids, Boolean isDesc, PageRequest pageRequest){
        Page<Article> articles = articleRepository.findAll(articleSpecification(ids,isDesc, ArticleList.NO_INCLUDE_TOP),pageRequest);
        return articles;
    }
    @Override
    public CategoryArticleListDao findCategoryArticleBy(CategoryVO category, Template template, int page){
        CategoryArticleListDao articleListVo = new CategoryArticleListDao();

        /**
         * 根据一组id查找article
         * **/
        Set<Integer> ids =new HashSet<>();
        List<CategoryVO> categoryVOS = new ArrayList<>();
        ids.add(category.getId());
        addChildIds(categoryVOS,category.getId());


        List<Category> categoryPartner = categoryService.findByParentId(category.getParentId());
        articleListVo.setPartner(categoryService.convertToListVo(categoryPartner));

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


        List<ArticleVO> contents;


        if(!template.getTree()){
            Page<Article> articles = articleRepository.findAll(articleSpecification(ids,category.getIsDesc(), ArticleList.NO_INCLUDE_TOP),PageRequest.of(page,category.getArticleListSize()));
            Page<ArticleVO> articleVOS = convertToPageVo(articles);
            int totalPages = articleVOS.getTotalPages();
            int size = articleVOS.getSize();
            long totalElements = articleVOS.getTotalElements();
            articleListVo.setTotalPages(totalPages);
            articleListVo.setSize(size);
            articleListVo.setTotalElements(totalElements);
            contents = articleVOS.getContent();
        }else {
            contents=listVoTree(ids,category.getIsDesc());
        }

        articleListVo.setContents(contents);
        articleListVo.setCategory(category);
        articleListVo.setViewName(category.getViewName());
        articleListVo.setPath(category.getPath());
        /**
         * 分页路径的格式生成
         */
        articleListVo.setLinkPath(FormatUtil.categoryListFormat(category));
        return articleListVo;
    }





    private List<ArticleDto> convertArticle2ArticleDto(List<Article> articles){
        return articles.stream().map(article -> {
            ArticleDto articleDto = new ArticleDto();
            BeanUtils.copyProperties(article,articleDto);
            articleDto.setLinkPath(FormatUtil.articleListFormat(article));
            return articleDto;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<ArticleDto> convertArticle2ArticleDto(Page<Article> articles){
        return articles.map(article -> {
            ArticleDto articleDto = new ArticleDto();
            BeanUtils.copyProperties(article, articleDto);
            articleDto.setLinkPath(FormatUtil.articleListFormat(article));
            return articleDto;
        });
    }


    @Override
    public List<Article> listArticleDtoBy(int categoryId){
        return articleRepository.findAll(articleSpecification(categoryId, ArticleList.ALL_ARTICLE));
    }

    @Override
    public List<Article> listArticleBy(int categoryId){
//        Specification<Content> specification =  articleSpecification(ids,isDesc, ArticleServiceImpl.ArticleList.NO_INCLUDE_TOP);

        return  articleRepository.findAll(articleSpecification(categoryId, ArticleList.NO_INCLUDE_TOP), Sort.by(Sort.Direction.ASC,"order"));
    }







    @Override
    public Page<Article> pageAllBy(Pageable pageable,ArticleQuery articleQuery){
        return  articleRepository.findAll(buildAllByQuery(articleQuery),pageable);
    }
    @Override
    public Page<Article>  pageByUserId(int userId, Pageable pageable,ArticleQuery articleQuery){
        articleQuery.setUserId(userId);
        return  pageAllBy(pageable,articleQuery);
    }


    @Override
    public ArticlePageCondition pagePublishBy(Integer componentsId, Set<String> sortStr, String order, Integer page, Integer size){
        List<ComponentsCategory> componentsCategories = componentsCategoryRepository.findByComponentId(componentsId);
        Set<Integer> categoryIds = ServiceUtil.fetchProperty(componentsCategories, ComponentsCategory::getCategoryId);
        Set<Integer> ids = new HashSet<>();
        ids.addAll(categoryIds);
        for (Integer id : categoryIds){
            List<CategoryVO> categoryVOS = categoryService.getAllChild(id);
            Set<Integer> set = ServiceUtil.fetchProperty(categoryVOS, CategoryVO::getId);
            ids.addAll(set);
        }
        return pagePublishBy(ids,sortStr,order,page,size);


    }


    @Override
    public ArticlePageCondition pagePublishBy(Set<Integer> ids,  Set<String> sortStr,String order, Integer page, Integer size){
        ArticlePageCondition articlePageCategoryIds = new ArticlePageCondition();
        if(ids.size()==0){
            return articlePageCategoryIds;
        }


        Sort.Direction direction = Sort.Direction.valueOf(order);
        Sort sort= Sort.by(direction,sortStr.toArray(new String[]{}));

        Page<Article> articlePage = articleRepository.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.in(root.get("categoryId")).value(ids), criteriaBuilder.or(criteriaBuilder.equal(root.get("status"), ArticleStatus.PUBLISHED),
                        criteriaBuilder.equal(root.get("status"), ArticleStatus.MODIFY))).getRestriction();
            }
        }, PageRequest.of(page, size, sort));
        articlePageCategoryIds.setArticles(articlePage);
        articlePageCategoryIds.setIds(ids);
        articlePageCategoryIds.setSortStr(sortStr);
        articlePageCategoryIds.setOrder(order);
        articlePageCategoryIds.setPage(page);
        articlePageCategoryIds.setSize(size);
        articlePageCategoryIds.setTotalPage(articlePage.getTotalPages());

        return articlePageCategoryIds;

    }



//    public Page<ArticleDto>  pageDtoBy(Pageable pageable,ArticleQuery articleQuery){
//        return  convertToSimple(pageUserBy(pageable,articleQuery));
//    }
    /**
     * 查找分类第一页的文章,用于该分类下文章的静态化
     * @param category
     * @param pageable
     * @return
     */
    @Override
    public Page<ArticleDto> pageDtoByCategory(Category category, Pageable pageable) {
        ArticleQuery articleQuery = new ArticleQuery();
        articleQuery.setCategoryId(category.getId());
        articleQuery.setDesc(category.getDesc());
        Page<Article> articles = pagePublishBy(pageable, articleQuery);
        return  convertToSimple(articles);
    }
    /**
     * 分类页文章展示设置,可以通过Option动态设置分页大小, 排序
     * @param categoryId
     * @param page
     * @return
     */
    @Override
    public Page<ArticleDto> pageDtoByCategoryId(int categoryId, int page) {
        Category category = categoryService.findById(categoryId);
        return pageDtoByCategory(category, PageRequest.of(page, category.getArticleListSize()));
    }











    @Override
    public Article updateOrder(int articleId, int order){
        Article article = findArticleById(articleId);
        article.setOrder(order);
        Article saveArticle = articleRepository.save(article);
        return saveArticle;
    }






    @Override
    public Page<ArticleDto> pageByTagId(int tagId, int size){
        return pageByTagId(tagId,PageRequest.of(0,size,Sort.by(Sort.Order.desc("createDate"))));
    }

    @Override
    public Page<ArticleDto> pageByTagId(int tagId, Pageable pageable){

        Specification<Article> specification = new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                Subquery<Article> subquery = criteriaQuery.subquery(Article.class);
                Root<ArticleTags> subRoot = subquery.from(ArticleTags.class);
                subquery = subquery.select(subRoot.get("articleId")).where(criteriaBuilder.equal(subRoot.get("tagsId"),tagId));
                return criteriaQuery.where(criteriaBuilder.in(root.get("id")).value(subquery)).getRestriction();
            }
        };
        Page<Article> articles = articleRepository.findAll(specification, pageable);
        return   convertArticle2ArticleDto(articles);
    }




    @Override
    public List<ArticleDto> listByTitle(String title){
        Specification<Article> specification = new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                String likeCondition = String.format("%%%s%%",title);
                return criteriaQuery.where(criteriaBuilder.like(root.get("title"),likeCondition)).getRestriction();
            }
        };
        return articleRepository.findAll(specification).stream().map(article -> {
            ArticleDto articleDto = new ArticleDto();
            BeanUtils.copyProperties(article, articleDto);
            return articleDto;
        }).collect(Collectors.toList());
    }





    /**
     * 根据id查找文章
     * @return
     */
//    @Override
//    public List<Article> listByIds(Set<Integer> ids){
//        List<Article> articles = articleRepository.findAllById(ids);
//        return articles;
//    }

    @Override
    public List<ArticleVO> listByComponentsId(int componentsId){
        List<ComponentsArticle> componentsArticles = componentsArticleRepository.findByComponentId(componentsId);
        Set<Integer> articleIds = ServiceUtil.fetchProperty(componentsArticles, ComponentsArticle::getArticleId);
//        List<Article> articles = articleRepository.findAllById(articleIds);
        List<Article> articles = articleRepository.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(root.get("id").in(articleIds)).getRestriction();
            }
        },Sort.by(Sort.Direction.DESC,"articleInComponentOrder"));
        return convertToListVo(articles);
    }

    @Override
    public List<Article>listByUserId(int userId){
        ArticleQuery articleQuery = new ArticleQuery();
        articleQuery.setUserId(userId);
        return articleRepository.findAll(buildAllByQuery(articleQuery));
    }
    @Override
    public Article findByViewName(String viewName){
        return articleRepository.findByViewName(viewName);
    }

    @Override
    public ArticleAndCategoryMindDto listArticleMindDto(String viewName) {
        Category category = categoryService.findByViewName(viewName);

        ArticleAndCategoryMindDto articleAndCategoryMindDto = new ArticleAndCategoryMindDto();
        List<Article> articles = listArticleBy(category.getId());
        List<ArticleMindDto> articleMindDtoList =articles.stream().map(article -> {
            ArticleMindDto articleMindDto = new ArticleMindDto();
            BeanUtils.copyProperties(article, articleMindDto);
            return articleMindDto;
        }).collect(Collectors.toList());

        articleAndCategoryMindDto.setCategory(category);
        articleAndCategoryMindDto.setList(articleMindDtoList);
        articleAndCategoryMindDto.setLinkPath(FormatUtil.categoryList2Format(category));
        return  articleAndCategoryMindDto;
    }



    @Override
    public ArticleAndCategoryMindDto listArticleMindDto(int categoryId){

        ArticleAndCategoryMindDto articleAndCategoryMindDto = new ArticleAndCategoryMindDto();
        List<ArticleMindDto> articleMindDtoList = listArticleBy(categoryId).stream().map(article -> {
            ArticleMindDto articleMindDto = new ArticleMindDto();
            BeanUtils.copyProperties(article, articleMindDto);
            return articleMindDto;
        }).collect(Collectors.toList());
        Category category = categoryService.findById(categoryId);

        articleAndCategoryMindDto.setCategory(category);
        articleAndCategoryMindDto.setList(articleMindDtoList);
        articleAndCategoryMindDto.setLinkPath(FormatUtil.categoryList2Format(category));
        return  articleAndCategoryMindDto;
    }


    @Override
    public String jsMindFormat( ArticleAndCategoryMindDto articleAndCategoryMindDto ){
        JSONArray root = new JSONArray();
        Category category = articleAndCategoryMindDto.getCategory();
        JSONObject item = new JSONObject();
        item.put("id",String.valueOf(0));
        item.put("topic",category.getName());
        item.put("isroot",true);
        root.add(item);
        for (ArticleMindDto articleMindDto:articleAndCategoryMindDto.getList()){
            item = new JSONObject();
            item.put("id",String.valueOf(articleMindDto.getId()));
            item.put("topic",articleMindDto.getTitle());
            item.put("direction",articleMindDto.getDirection());
            item.put("expanded",articleMindDto.getExpanded());
            item.put("parentid",String.valueOf(articleMindDto.getParentId()));
            item.put("data","/"+articleMindDto.getPath()+"/"+articleMindDto.getViewName()+".html");
            root.add(item);
        }

//        articleMindDtoList.add(new ArticleMindDto(0,category.getName(),category.getViewName(),
//                category.getPath(),-1));
        return  root.toString();
    }


    @Override
    public List<ArticleDto> listTopByCategoryId(Category category) {
        Set<Integer> ids = new HashSet<>();
        ids.add(category.getId());
        List<Article> articles = articleRepository.findAll(articleSpecification(
                ids,category.getDesc(), ArticleList.INCLUDE_TOP));
        return convertArticle2ArticleDto(articles);
    }

    @Override
    public Article sendOrCancelTop(int id) {
        Article article = findArticleById(id);
        if(article.getStatus().equals(ArticleStatus.DRAFT)||article.getStatus().equals(ArticleStatus.RECYCLE)){
            throw new ArticleException("该文章没有发布不能置顶！");
        }
        if(article.getTop()){
            article.setTop(false);
        }else {
            article.setTop(true);
        }
        return  save(article);
    }

    @Override
    public Article divisionOkOrCancel(int id) {
        Article article = findArticleById(id);
        if(article.getIsDivision()!=null){
            if(article.getIsDivision()){
                article.setIsDivision(false);
            }else {
                article.setIsDivision(true);
            }
        }else {
            article.setIsDivision(true);
        }

        return  save(article);
    }

    @Override
    public List<ArticleVO> listVoTree(Integer categoryId) {
        Category category = categoryService.findById(categoryId);
        Set<Integer> ids  = new HashSet<>();
        ids.add(category.getId());
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
    public List<ArticleVO> listVoTree(Set<Integer> ids,Boolean isDesc) {

//        ArticleQuery articleQuery = new ArticleQuery();
//        articleQuery.setCategoryId(category.getId());
//        articleQuery.setDesc(category.getDesc());



        Specification<Article> specification =  articleSpecification(ids,isDesc, ArticleList.NO_INCLUDE_TOP);
        List<Article> articles = articleRepository.findAll(specification);
//                .stream().map(article -> {
//            ArticleVO articleVO = new ArticleVO();
//            BeanUtils.copyProperties(article, articleVO);
//            return articleVO;
//        }).collect(Collectors.toList());
        List<ArticleVO> articleVOS = convertToListVo(articles);
        List<ArticleVO> articleVOTree = super.listWithTree(articleVOS);
//        List<ArticleDto> listWithTree = listWithTree(articleDtos);
        return articleVOTree;
    }

    public List<CategoryArticleList> listCategoryChild(String viewName){
        Category parentCategory = categoryService.findByViewName(viewName);
        if(parentCategory==null){
            return null;
        }
        List<Category> categories = categoryService.findByParentId(parentCategory.getId());

        List<CategoryArticleList> categoryArticleLists =  new ArrayList<>();
        for (Category category:categories){
            CategoryArticleList categoryArticleList = new CategoryArticleList();
            CategoryVO categoryVO = categoryService.convertToVo(category);
            categoryArticleList.setCategory(categoryVO);
            ArticleQuery articleQuery = new ArticleQuery();
            articleQuery.setCategoryId(category.getId());
            articleQuery.setDesc(category.getDesc());
            Specification<Article> specification = buildPublishByQuery(articleQuery);
            List<Article> articles = articleRepository.findAll(specification);
            List<ArticleVO> articleVOS = convertToListVo(articles);
            List<ArticleVO> articleVOTree = super.listWithTree(articleVOS);
            categoryArticleList.setArticleVOS(articleVOTree);
            categoryArticleLists.add(categoryArticleList);
        }
        return categoryArticleLists;
    }


    public List<CategoryArticleList> listCategoryChild(Integer categoryId){
        List<Category> categories = categoryService.findByParentId(categoryId);
        List<CategoryArticleList> categoryArticleLists =  new ArrayList<>();
        for (Category category:categories){
            CategoryArticleList categoryArticleList = new CategoryArticleList();
            CategoryVO categoryVO = categoryService.convertToVo(category);
            categoryArticleList.setCategory(categoryVO);
            ArticleQuery articleQuery = new ArticleQuery();
            articleQuery.setCategoryId(category.getId());
            articleQuery.setDesc(category.getDesc());
            Specification<Article> specification = buildPublishByQuery(articleQuery);
            List<Article> articles = articleRepository.findAll(specification);
            List<ArticleVO> articleVOS = convertToListVo(articles);
            List<ArticleVO> articleVOTree = super.listWithTree(articleVOS);
            categoryArticleList.setArticleVOS(articleVOTree);
            categoryArticleLists.add(categoryArticleList);
        }
        return categoryArticleLists;
    }

    @Override
    public List<ArticleVO> listVoTreeByCategoryViewName(String viewName) {
        Category category = categoryService.findByViewName(viewName);
//        Category category = categoryService.findById(categoryId);
        ArticleQuery articleQuery = new ArticleQuery();
        articleQuery.setCategoryId(category.getId());
        articleQuery.setDesc(category.getDesc());
        Specification<Article> specification = buildPublishByQuery(articleQuery);
        List<Article> articles = articleRepository.findAll(specification);
//                .stream().map(article -> {
//            ArticleVO articleVO = new ArticleVO();
//            BeanUtils.copyProperties(article, articleVO);
//            return articleVO;
//        }).collect(Collectors.toList());
        List<ArticleVO> articleVOS = convertToListVo(articles);
        List<ArticleVO> articleVOTree = super.listWithTree(articleVOS);
//        List<ArticleDto> listWithTree = listWithTree(articleDtos);
        return articleVOTree;
    }

    @Override
    public List<ArticleVO> listVoByCategoryViewName(String viewName,Integer size) {
        Category category = categoryService.findByViewName(viewName);
        if(category==null){
            return null;
        }
        List<CategoryVO> categoryVOS = new ArrayList<>();
        addChildIds(categoryVOS,category.getId());
        categoryVOS.add(categoryService.convertToVo(category));
        Set<Integer> ids = ServiceUtil.fetchProperty(categoryVOS, CategoryVO::getId);
        Page<Article> articles = articleRepository.findAll(articleSpecification(ids,category.getIsDesc(), ArticleList.NO_INCLUDE_TOP),PageRequest.of(0,size));
        Page<ArticleVO> articleVOS = convertToPageVo(articles);

//        articleVOS
        //        Category category = categoryService.findById(categoryId);
//        ArticleQuery articleQuery = new ArticleQuery();
//        articleQuery.setCategoryId(category.getId());
//        articleQuery.setDesc(category.getDesc());
//        Specification<Article> specification = buildPublishByQuery(articleQuery);
//        List<Article> articles = articleRepository.findAll(specification);
//                .stream().map(article -> {
//            ArticleVO articleVO = new ArticleVO();
//            BeanUtils.copyProperties(article, articleVO);
//            return articleVO;
//        }).collect(Collectors.toList());
//        List<ArticleVO> articleVOS = convertToListVo(articles);
//        List<ArticleDto> listWithTree = listWithTree(articleDtos);
        return articleVOS.getContent();
    }

    @Override
    public List<ArticleVO> listVoByCategoryViewName(String viewName) {
        Category category = categoryService.findByViewName(viewName);
        if(category==null){
            return null;
        }
        List<CategoryVO> categoryVOS = new ArrayList<>();
        addChildIds(categoryVOS,category.getId());
        categoryVOS.add(categoryService.convertToVo(category));
        Set<Integer> ids = ServiceUtil.fetchProperty(categoryVOS, CategoryVO::getId);
        List<Article> articles = articleRepository.findAll(articleSpecification(ids, category.getIsDesc(), ArticleList.NO_INCLUDE_TOP));
        List<ArticleVO> articleVOS = convertToListVo(articles);
//        Page<ArticleVO> articleVOS = convertToPageVo(articles);

//        articleVOS
        //        Category category = categoryService.findById(categoryId);
//        ArticleQuery articleQuery = new ArticleQuery();
//        articleQuery.setCategoryId(category.getId());
//        articleQuery.setDesc(category.getDesc());
//        Specification<Article> specification = buildPublishByQuery(articleQuery);
//        List<Article> articles = articleRepository.findAll(specification);
//                .stream().map(article -> {
//            ArticleVO articleVO = new ArticleVO();
//            BeanUtils.copyProperties(article, articleVO);
//            return articleVO;
//        }).collect(Collectors.toList());
//        List<ArticleVO> articleVOS = convertToListVo(articles);
//        List<ArticleDto> listWithTree = listWithTree(articleDtos);
        return articleVOS;
    }


    //    public List<ArticleDto> listWithTree(List<ArticleDto> list) {
//        // 1. 先查出所有数据
////        List<ProjectLeader> list = projectLeaderService.list(Condition.getLikeQueryWrapper(projectLeader));
//        List<ArticleDto> collect = list.stream()
//                // 2. 找出所有顶级（规定 0 为顶级）
//                .filter(o -> o.getParentId().equals(0))
//                // 3.给当前父级的 childList 设置子
//                .peek(o -> o.setChildren(getChildList(o, list)))
//                .sorted(Comparator.comparing(ArticleDto::getOrder))
//                // 4.收集
//                .collect(Collectors.toList());
//        return collect;
//    }
//
//    // 根据当前父类 找出子类， 并通过递归找出子类的子类
//    private List<ArticleDto> getChildList(ArticleDto articleDto, List<ArticleDto> list) {
//        return list.stream()
//                //筛选出父节点id == parentId 的所有对象 => list
//                .filter(o -> o.getParentId().equals(articleDto.getId()))
//                .peek(o -> o.setChildren(getChildList(o, list)))
//                .sorted(Comparator.comparing(ArticleDto::getOrder))
//                .collect(Collectors.toList());
//    }


    @Override
    public void updateOrder(Integer id, List<ArticleVO> articleVOS) {
        Category category = categoryService.findById(id);
        List<Article> articles = listArticleBy(category.getId());
        super.updateOrder(articles,articleVOS);
    }

    @Override
    public  Integer getCommentNum(int id){
        return  articleRepository.getCommentNum(id);
    }

    @Override
    public void updateCommentNum(int id, int num){
        articleRepository.updateCommentNum(id,num);
    }






    public Page<ArticleVO> listByCategoryViewName(String viewName,Integer size){
        Category category = categoryService.findByViewName(viewName);
        if(category==null){
            return null;
        }
        Page<Article> articles = articleRepository.findAll(new Specification<Article>() {
            @Override
            public Predicate toPredicate(Root<Article> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaQuery.where(criteriaBuilder.equal(root.get("categoryId"), category.getId())).getRestriction();
            }
        }, PageRequest.of(0, size, Sort.by(Sort.Direction.DESC,"createDate")));
//        , PageRequest.of(0, size, Sort.by(Sort.Direction.DESC))

        return articles.map(article -> {
            ArticleVO articleVO=new ArticleVO();
            BeanUtils.copyProperties(article,articleVO);
            return articleVO;
        });
    }

    @Override
    public boolean supportType(CrudType type) {
        return type.equals(CrudType.ARTICLE);
    }
}
