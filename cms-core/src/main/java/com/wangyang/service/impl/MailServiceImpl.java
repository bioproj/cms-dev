package com.wangyang.service.impl;

import com.wangyang.common.CmsConst;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.authorize.BaseAuthorize;
import com.wangyang.pojo.authorize.Role;
import com.wangyang.pojo.authorize.User;
import com.wangyang.pojo.authorize.UserRole;
import com.wangyang.pojo.entity.*;
import com.wangyang.service.ICommentService;
import com.wangyang.service.templates.ITemplateService;
import com.wangyang.service.MailService;
import com.wangyang.service.authorize.IRoleService;
import com.wangyang.service.authorize.IUserRoleService;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.service.base.IAuthorizeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;
import java.util.Set;

/**
* @author Likaifeng
* @date 2021/6/22
* 简单文本邮件
* 参考地址  https://blog.csdn.net/sinat_26342009/article/details/89425836
*/
@Service
public class MailServiceImpl implements MailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.mail.username}")
    private String from ;            //邮件发送者,配置文件中配置
    @Value("${cms.url}")
    private String hostname ;
    @Autowired
    private JavaMailSender mailSender;

//    @Resource
//    private TemplateEngine templateEngine;

    @Autowired
    ITemplateService templateService;

    @Autowired
    IUserService userService;

    @Autowired
    IUserRoleService userRoleService;


    @Autowired
    @Qualifier("authorizeServiceImpl")
    IAuthorizeService<BaseAuthorize> authorizeService;


    @Autowired
    IRoleService roleService;
    @Autowired
    ICommentService commentService;

    /**
     * 简单文本邮件
     * @param to      接收者邮件
     * @param subject 邮件主题
     * @param content 邮件内容
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom(from);
        try {
            mailSender.send(message);
        }catch (MailException e){
            e.getMessage();
        }
    }

    /**
     * HTML 文本邮件
     * @param to      接收者邮件
     * @param subject 邮件主题
     * @param content HTML内容
     * @throws MessagingException
     */
    @Override
    public void sendHtmlMail(String to, String subject, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        helper.setFrom(from);

        mailSender.send(message);
    }

    /**
     * 附件邮件
     * @param to       接收者邮件
     * @param subject  邮件主题
     * @param content  HTML内容
     * @param filePath 附件路径
     * @throws MessagingException
     */
    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);
        helper.setFrom(from);

        FileSystemResource file = new FileSystemResource(new File(filePath));
        String fileName = file.getFilename();
        helper.addAttachment(fileName, file);

        mailSender.send(message);
    }

    /**
     * 图片邮件
     * @param to      接收者邮件
     * @param subject 邮件主题
     * @param content HTML内容
     * @param rscPath 图片路径
     * @param rscId   图片ID
     * @throws MessagingException
     */
    @Override
    public void sendInlinkResourceMail(String to, String subject, String content,
                                       String rscPath, String rscId) throws MessagingException {
        logger.info("发送静态邮件开始: {},{},{},{},{}", to, subject, content, rscPath, rscId);

        MimeMessage message = mailSender.createMimeMessage();
        message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(from));
        MimeMessageHelper helper = null;

        try {

            helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom(from);

            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);
            mailSender.send(message);
            logger.info("发送静态邮件成功!");

        } catch (MessagingException e) {
            logger.info("发送静态邮件失败: ", e);
        }
    }


    @Async("taskExecutor")
    @Override
    public void sendEmail(User currentUser, Comment comment, Article article){
        // 文章作者
        Context context = new Context();
        context.setVariable("article",article);
        context.setVariable("user",currentUser);
        context.setVariable("comment",comment);
        context.setVariable("hostname",hostname);

        Template template = templateService.findByEnName(CmsConst.DEFAULT_EMAIL);
        String emailContent = TemplateUtil.getHtml(template.getTemplateValue(), context);
//        String emailContent = templateEngine.process("emailTeplate", context);
        //TODO
//        if(!currentUser.getId().equals(article.getUserId())){
//            User userArticle = userService.findById(article.getUserId());
//            sendSimpleMail(userArticle.getEmail(),currentUser.getUsername()+"在文章["+article.getTitle()+"]中回复:",emailContent);
//        }
//        if(comment.getParentId()!=0){
//            Comment replyComment = commentService.findById(comment.getParentId());
//            Integer replyUserId = replyComment.getUserId();
//            if(replyUserId!=comment.getUserId() && article.getUserId()!=replyUserId){
//                User replyUser = userService.findById(replyUserId);
//                sendSimpleMail(replyUser.getEmail(),currentUser.getUsername()+"在文章["+article.getTitle()+"]中回复:",emailContent);
//            }
//        }
    }


    @Override
    @Async
    public void sendEmail(Customer customer) {

        Context context = new Context();
        context.setVariable("customer",customer);
        context.setVariable("proxyUrl",CMSUtils.getProxyUrl());

        Template template = templateService.findByEnName(CmsConst.FOR_CUSTOMER);
        String emailContent = TemplateUtil.getHtml(template.getTemplateValue(), context);


        Role role = roleService.findByEnName("ADMIN");
        List<UserRole> userRoles = userRoleService.findByRoleId(role.getId());
        Set<Integer> userIds = ServiceUtil.fetchProperty(userRoles, UserRole::getUserId);
        List<User> users = userService.findAllById(userIds);
        sendSimpleMail(customer.getEmail(),"老师感谢您的咨询",emailContent);
        for (User user:users){
            if(user.getEmail()==null){
                throw new ObjectException("管理员"+user.getUsername()+"没有配置邮箱！！");
            }
            sendSimpleMail(user.getEmail(),"客户["+customer.getUsername()+"]发来需求，请尽快回复！",customer.getContent());
        }
    }

    @Override
    public void sendEmail(Subscribe subscribe) {
        sendSimpleMail(subscribe.getEmail(),"老师感谢您的订阅！","老师感谢您的订阅");
    }

    @Override
    public List<BaseAuthorize> sendEmail(Mail mailInput) {
        List<BaseAuthorize> baseAuthorizes = authorizeService.listAll();
        Set<String> strings = ServiceUtil.fetchProperty(baseAuthorizes, BaseAuthorize::getEmail);
        for (String mail:strings){
            sendSimpleMail(mail,mailInput.getTitle(),mailInput.getContent());
        }
        return baseAuthorizes;
    }
}
