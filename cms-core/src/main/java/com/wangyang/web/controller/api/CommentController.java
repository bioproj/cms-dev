package com.wangyang.web.controller.api;

import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.MarkdownUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.pojo.annotation.CommentRole;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.entity.Article;
import com.wangyang.pojo.entity.UserArticle;
import com.wangyang.service.*;
import com.wangyang.pojo.entity.Comment;
import com.wangyang.pojo.params.CommentLoginUserParam;
import com.wangyang.pojo.params.CommentParam;
import com.wangyang.pojo.vo.CommentVo;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.util.AuthorizationUtil;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;
import java.util.Set;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Autowired
    ICommentService commentService;

    @Autowired
    IHtmlService htmlService;

    @Autowired
    IUserService userService;
    @Autowired
    private MailService mailService;

    @Autowired
    IArticleService articleService;

    @Autowired
    IUserArticleService userArticleService;

    @PostMapping
    @CommentRole
    public Comment add(@RequestBody @Valid CommentParam commentParam, HttpServletRequest request){
        Comment comment = new Comment();
        int userId = AuthorizationUtil.getUserId(request);
        User user = userService.findById(userId);
        BeanUtils.copyProperties(commentParam,comment);
        comment.setUserId(user.getId());
        if(comment.getParentId()==null){
            comment.setParentId(0);
        }

        String originalContent = MarkdownUtils.renderHtml(comment.getOriginalContent());
        comment.setFormatContent(originalContent);
        Comment saveComment = commentService.add(comment);


        Article article = articleService.findArticleById(saveComment.getArticleId());

        mailService.sendEmail(user,saveComment,article);
        /**
         * 根据一个评论生成单个文章下的评论列表
         */
        htmlService.generateCommentHtmlByArticleId(article);




        return saveComment;
    }



    @PostMapping("/addByLoginUser")
    public Comment addByLoginUser(@RequestBody @Valid CommentLoginUserParam commentLoginUserParam){
        Comment comment = new Comment();

        BeanUtils.copyProperties(commentLoginUserParam,comment);
        Comment saveComment = commentService.add(comment);
        /**
         * 根据一个评论生成单个文章下的评论列表
         */
        htmlService.generateCommentHtmlByArticleId(comment.getArticleId());
        return saveComment;
    }



    @GetMapping("/listByArticleId/{id}")
    public Page<CommentVo> listByArticleId(@PathVariable("id") Integer id, @PageableDefault(sort = {"id"},direction = DESC) Pageable pageable){

        Page<CommentVo> commentDtos = commentService.pageVoBy(id,pageable);
        return  commentDtos;
    }

    @DeleteMapping("/deleteById/{id}")
    @CommentRole
    public Comment deleteById(@PathVariable("id") Integer id, HttpServletRequest request){
        Comment comment = commentService.findById(id);
        int userId = AuthorizationUtil.getUserId(request);
        User user = userService.findById(userId);
        if(comment.getUserId()!=user.getId()){
            Article article = articleService.findById(comment.getArticleId());
            List<UserArticle> userArticles = userArticleService.listByArticleId(article.getId());
            Set<Integer> userIds = ServiceUtil.fetchProperty(userArticles, UserArticle::getUserId);
            if(userIds.contains(user.getId())){
                throw new ObjectException("Can't delete, not your!!");
            }
        }
        List<Comment> childComment = commentService.findByParentId(comment.getId());
        if(childComment.size()>0){
            throw new ObjectException("Can't delete, comment has child!!");
        }
        commentService.deleteById(comment.getId());
        htmlService.generateCommentHtmlByArticleId(comment.getArticleId());
        return comment;
    }

}
