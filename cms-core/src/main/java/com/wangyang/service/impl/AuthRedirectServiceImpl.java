package com.wangyang.service.impl;

import com.wangyang.common.CmsConst;
import com.wangyang.common.pojo.BaseVo;
import com.wangyang.common.service.AbstractCrudService;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.common.utils.ServiceUtil;
import com.wangyang.common.utils.TemplateUtil;
import com.wangyang.pojo.entity.AuthRedirect;
import com.wangyang.pojo.entity.Template;
import com.wangyang.repository.AuthRedirectRepository;
import com.wangyang.service.IAuthRedirectService;
import com.wangyang.service.templates.ITemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthRedirectServiceImpl extends AbstractCrudService<AuthRedirect,AuthRedirect, BaseVo,Integer>
    implements IAuthRedirectService {
    AuthRedirectRepository authRedirectRepository;


    ITemplateService templateService;
    public AuthRedirectServiceImpl(AuthRedirectRepository authRedirectRepository,
                                   ITemplateService templateService) {
        super(authRedirectRepository);
        this.authRedirectRepository = authRedirectRepository;
        this.templateService = templateService;
    }

    @Override
    public List<AuthRedirect> listAll() {
        return authRedirectRepository.findAll();
    }

    public void htmlGenerate(AuthRedirect authRedirect){

        if(authRedirect.getTemplateName()==null){
            authRedirect.setTemplateName(CmsConst.LOGIN_CONFIRM);
        }
        if(authRedirect.getOtherTemplateName()==null){
            authRedirect.setOtherTemplateName(CmsConst.AUTH_OTHER_PAGE);
        }
        if(authRedirect.getViewName()==null){
            authRedirect.setViewName(CMSUtils.randomViewName());
        }
        if(authRedirect.getOtherViewName()==null || (authRedirect.getOtherViewName()!=null && authRedirect.getOtherViewName().equals(authRedirect.getViewName()) )){
            authRedirect.setOtherViewName(CMSUtils.randomViewName());
        }
        if(authRedirect.getPath()==null){
            authRedirect.setPath("html/loginPage");
        }


        Template template = templateService.findByEnName(authRedirect.getTemplateName());
        TemplateUtil.convertHtmlAndSave(authRedirect.getPath(),authRedirect.getViewName(),authRedirect,template);
        String loginPage = authRedirect.getPath()+ File.separator + authRedirect.getViewName() ;
        authRedirect.setLoginPage(loginPage);

        Template otherTemplate = templateService.findByEnName(authRedirect.getOtherTemplateName());
        TemplateUtil.convertHtmlAndSave(authRedirect.getPath(),authRedirect.getOtherViewName(),authRedirect,otherTemplate);
        String otherPage = authRedirect.getPath()+ File.separator + authRedirect.getOtherViewName() ;
        authRedirect.setOtherPage(otherPage);

    }



    @Override
    public AuthRedirect update(Integer integer, AuthRedirect authRedirectInput) {
        AuthRedirect authRedirect = findById(integer);
        authRedirectInput.setId(null);
        BeanUtils.copyProperties(authRedirectInput, authRedirect,CMSUtils.getNullPropertyNames(authRedirectInput));

        htmlGenerate(authRedirect);
        return super.update(integer, authRedirect);
    }
    @Override
    public AuthRedirect addUniqueCurrentUrl(AuthRedirect authRedirectInput) {
        AuthRedirect authRedirect = findByCurrentUrl(authRedirectInput.getCurrentUrl());
        if(authRedirect==null){
            authRedirect = new AuthRedirect();
        }
        authRedirectInput.setId(null);
        BeanUtils.copyProperties(authRedirectInput, authRedirect,CMSUtils.getNullPropertyNames(authRedirectInput));


//        Template template = templateService.findByEnName(authRedirect.getTemplateName());
//        TemplateUtil.convertHtmlAndSave(authRedirect.getPath(),authRedirect.getViewName(),authRedirect,template);
//        String loginPage = authRedirect.getPath()+ File.separator + authRedirect.getViewName();
//        authRedirect.setLoginPage(loginPage);
        htmlGenerate(authRedirect);
        return super.add(authRedirect);
    }
    @Override
    public AuthRedirect add(AuthRedirect authRedirect) {


//        Template template = templateService.findByEnName(authRedirect.getTemplateName());
//        TemplateUtil.convertHtmlAndSave(authRedirect.getPath(),authRedirect.getViewName(),authRedirect,template);
//        String loginPage = authRedirect.getPath()+ File.separator + authRedirect.getViewName() + ".html";
//        authRedirect.setLoginPage(loginPage);
        htmlGenerate(authRedirect);
        return super.add(authRedirect);
    }

    @Override
    public AuthRedirect findByCurrentUrl(String currentUrl) {

        List<AuthRedirect> authRedirects = this.listAll();
        if(!authRedirects.isEmpty()){
            Map<String, AuthRedirect> authRedirectMap = ServiceUtil.convertToMap(authRedirects, AuthRedirect::getCurrentUrl);
            List<String> list = authRedirectMap.keySet().stream().filter(item -> item.startsWith(currentUrl)).collect(Collectors.toList());
            if(list.size()>0){
                return authRedirectMap.get( list.get(0));
            }
        }

        return null;
    }
}
