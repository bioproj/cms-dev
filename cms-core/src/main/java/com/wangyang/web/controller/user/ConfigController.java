package com.wangyang.web.controller.user;

import com.wangyang.config.ConstProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

@Configuration
public class ConfigController {


    @Autowired
    private  ConstProperties constProperties;

    @Autowired
    public void setHandlerMapping(RequestMappingHandlerMapping mapping,
                                 WebController controller) throws NoSuchMethodException {
        RequestMappingInfo info = RequestMappingInfo
                .paths("/"+constProperties.templateWebPrefix+"/{viewName}.html").methods(RequestMethod.GET).build();
        Method method = WebController.class.getMethod("template",String.class);
        mapping.registerMapping(info, controller, method);
    }

}
