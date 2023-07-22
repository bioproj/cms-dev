package com.wangyang.common.utils;

import com.wangyang.common.CmsConst;
import com.wangyang.pojo.vo.ContentVO;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CMSUtils {

    public static String randomViewName(){
        Calendar date = Calendar.getInstance();
        int year = date.get(Calendar.YEAR);
        int month =date.get(Calendar.MONTH);
        int second = date.get(Calendar.SECOND);
        int day = date.get(Calendar.DAY_OF_MONTH);
        int millisecond = date.get(Calendar.MILLISECOND);
        String viewName = String.valueOf(year)+String.valueOf(month)+String.valueOf(day)+String.valueOf(second)+String.valueOf(millisecond);
//        return UUID.randomUUID().toString();
        return viewName;
    }
    public static String phoneAuth(){
        return CmsConst.PHONE_AUTH;
    }
    public static String getWorkDir(){
        return CmsConst.WORK_DIR;
    }
    public static String getArticlePath(){
        return CmsConst.ARTICLE_DETAIL_PATH;
    }
    public static String getTemplates(){
        return CmsConst.TEMPLATES+ File.separator;
    }
    public static String getProxyUrl(){
        return CmsConst.PROXY_URL;
    }
    public static String getLiteraturePath(){
        return CmsConst.LITERATURE_DETAIL_PATH;
    }

//    public static String getCategoryPath(){
//        return CmsConst.CATEGORY_LIST_PATH;
//    }
    public static String getFirstArticleList(){
        return CmsConst.FIRST_ARTICLE_LIST;
    }
    public static String getComponentFragment(){
        return CmsConst.COMPONENT_FRAGMENT;
    }
    public static String getCategoryChildren(){
        return CmsConst.CATEGORY_CHILDREN;
    }
    public static String getFirstArticleTitleList(){
        return CmsConst.FIRST_ARTICLE_TITLE_LIST;
    }
    public static String getComment(){
        return CmsConst.COMMENT_PATH;
    }
    public static String getCommentJSON(){
        return CmsConst.COMMENT_PATH_JSON;
    }
    public static String getCategoryPath(){
        return CmsConst.CATEGORY_PATH;
    }
    public static String getCategoryPathList(){
        return CmsConst.CATEGORY_PATH_LIST;
    }
    public static String getArticleRecommendPath(){
        return CmsConst.ARTICLE_RECOMMEND_LIST_PATH;
    }
    public static String getArticleRecentPath(){
        return CmsConst.ARTICLE_RECENT_LIST_PATH;
    }
    public static String getTagPath(){
        return CmsConst.TAG_PATH;
    }
    public static String getComponentsPath(){
        return CmsConst.COMPONENT_PATH;
    }
    public static String getArticleListJs(){
        return CmsConst.ARTICLE_LIST_JS_PATH;
    }

    public static String getSheetPath(){
        return CmsConst.SHEET_PATH;
    }

    public static String getWxRole(){
        return CmsConst.WX_ROLE;
    }
    public static String getEmailRole(){
        return CmsConst.EMAIL_ROLE;
    }
    public static String getPhoneRole(){
        return CmsConst.PHONE_ROLE;
    }


    public static String randomTime(){
        return String.valueOf(System.currentTimeMillis());
    }
    public static String getHostAddress(){
        /**
        try {
            InetAddress address = InetAddress.getLocalHost();
            return String.format("http://%s:%s",address.getHostAddress(),"8080");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
         **/
        return String.format("http://%s:%s", "127.0.0.1", "8080");
    }
    public static String[] getNullPropertyNames (Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    public static String generateVerificationCode() {
        // 生成随机六位数验证码
        SecureRandom random = new SecureRandom();
        int code = random.nextInt(900000) + 100000;
        return Integer.toString(code);
    }

    private static final int EXPIRATION_MINUTES = 1; // 验证码有效期（分钟）
    public static LocalDateTime getExpirationTime() {
        return LocalDateTime.now().plus(EXPIRATION_MINUTES, ChronoUnit.MINUTES);
    }
    public static void deleteCooke(HttpServletRequest request, HttpServletResponse response){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("Authorization")){
                    cookie.setValue(null);
                    cookie.setMaxAge(0);// 立即销毁cookie
                    cookie.setPath("/");
//                    System.out.println("被删除的cookie名字为:"+cookie.getName());
                    response.addCookie(cookie);
                    break;
                }
            }
        }
    }
    public static void flattenContentVOTreeToList(List<ContentVO> contentVOS, List<ContentVO> contentVOList) {
        for (ContentVO content: contentVOS){
            contentVOList.add(content);
            if(content.getChildren().size()!=0){
                flattenContentVOTreeToList(content.getChildren(),contentVOList);
            }
        }
    }


}
