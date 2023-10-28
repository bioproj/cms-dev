package com.wangyang.service;

import com.wangyang.pojo.entity.ComponentsArticle;

import java.util.List;

public interface IComponentsArticleService {
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
    List<ComponentsArticle> findByComponentId(Integer componentId);

    List<ComponentsArticle> findByArticleId(Integer articleId);

//    ComponentsArticle add(int articleId, int componentsId);
//
//    ComponentsArticle add(String viewName, int componentsId);

    void delete(int id);

    ComponentsArticle delete(int articleId, int componentsId);
}
