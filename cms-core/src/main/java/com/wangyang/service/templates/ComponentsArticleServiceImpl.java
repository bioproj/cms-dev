package com.wangyang.service.templates;

import com.wangyang.pojo.entity.ComponentsArticle;
import com.wangyang.repository.template.ComponentsArticleRepository;
import com.wangyang.repository.template.ComponentsRepository;
import com.wangyang.service.templates.IComponentsArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComponentsArticleServiceImpl implements IComponentsArticleService {



//    @Autowired
//    IComponentsService componentsService;

    @Autowired
    ComponentsRepository componentsRepository;
    @Autowired
    ComponentsArticleRepository componentsArticleRepository;


//    @Autowired
//    @Qualifier("contentServiceImpl")
//    IContentService<Content,Content, ContentVO> contentService;


//    @Override
//    public List<ComponentsArticle> findByComponentsId(Integer id){
//        List<ComponentsArticle> componentsArticleList = componentsArticleRepository.findAll(new Specification<ComponentsArticle>() {
//            @Override
//            public Predicate toPredicate(Root<ComponentsArticle> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                return criteriaQuery.where(criteriaBuilder.equal(root.get("componentId"), id)).getRestriction();
//            }
//        }, Sort.by(Sort.Direction.DESC, "order"));
//        return componentsArticleList;
//    }
    @Override
    public List<ComponentsArticle> findByComponentId(Integer componentId){
        return componentsArticleRepository.findByComponentId(componentId);
    }
    @Override
    public List<ComponentsArticle> findByArticleId(Integer articleId){
        return componentsArticleRepository.findByArticleId(articleId);
    }




    @Override
    public void delete(int id){
        componentsArticleRepository.deleteById(id);
    }

    @Override
    public ComponentsArticle delete(int articleId, int componentsId){
        ComponentsArticle findComponentsArticle = componentsArticleRepository.findByArticleIdAndComponentId(articleId, componentsId);
        if(findComponentsArticle!=null){
            componentsArticleRepository.deleteById(findComponentsArticle.getId());

        }
        return findComponentsArticle;
    }






}
