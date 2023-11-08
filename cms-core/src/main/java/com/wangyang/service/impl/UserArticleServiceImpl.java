package com.wangyang.service.impl;

import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.pojo.entity.UserArticle;
import com.wangyang.repository.UserArticleRepository;
import com.wangyang.service.IUserArticleService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Service
public class UserArticleServiceImpl  extends AbstractCrudService<UserArticle, UserArticle, BaseVo,Integer> implements IUserArticleService {

    UserArticleRepository userArticleRepository;
    public UserArticleServiceImpl(UserArticleRepository userArticleRepository) {
        super(userArticleRepository);
        this.userArticleRepository =userArticleRepository;
    }

    @Override
    public List<UserArticle> listByArticleId(Integer articleId){
        List<UserArticle> userArticles = userArticleRepository.findAll(new Specification<UserArticle>() {

            @Override
            public Predicate toPredicate(Root<UserArticle> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("articleId"), articleId)).getRestriction();
            }
        });
        return userArticles;

    }

    @Override
    public UserArticle findByUserIdAndArticleId(Integer userId, Integer articleId) {
        List<UserArticle> userArticles = userArticleRepository.findAll(new Specification<UserArticle>() {
            @Override
            public Predicate toPredicate(Root<UserArticle> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return query.where(criteriaBuilder.equal(root.get("userId"), userId),
                        criteriaBuilder.equal(root.get("articleId"), articleId)).getRestriction();
            }
        });
        if(userArticles.size()==0){
            return null;
        }
        return userArticles.get(0);
    }

}
