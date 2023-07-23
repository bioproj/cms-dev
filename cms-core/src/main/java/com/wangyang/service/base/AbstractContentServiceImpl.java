package com.wangyang.service.base;

import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.common.utils.MarkdownUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.dto.ArticlePageCondition;
import com.wangyang.pojo.dto.CategoryContentList;
import com.wangyang.pojo.dto.CategoryContentListDao;
import com.wangyang.pojo.entity.Category;
import com.wangyang.pojo.entity.ComponentsArticle;
import com.wangyang.pojo.entity.Template;
import com.wangyang.common.pojo.BaseEntity;
import com.wangyang.pojo.entity.base.Content;
import com.wangyang.common.enums.Lang;
import com.wangyang.pojo.vo.CategoryVO;
import com.wangyang.pojo.vo.ContentDetailVO;
import com.wangyang.pojo.vo.ContentVO;
import com.wangyang.repository.ComponentsArticleRepository;
import com.wangyang.repository.base.ContentRepository;
import com.wangyang.util.FormatUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@Component
public abstract class AbstractContentServiceImpl<ARTICLE extends Content,ARTICLEDTO extends BaseEntity,ARTICLEVO extends ContentVO>  extends AbstractCrudService<ARTICLE,ARTICLEDTO,ARTICLEVO,Integer>
        implements IContentService<ARTICLE,ARTICLEDTO,ARTICLEVO> {

//    @Autowired
//    IOptionService optionService;
    @Autowired
    ComponentsArticleRepository componentsArticleRepository;
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
            domainvo.setLinkPath(FormatUtil.articleListFormat(domain,null));
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
}
