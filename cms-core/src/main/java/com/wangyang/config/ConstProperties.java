package com.wangyang.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstProperties {

    @Getter
    @Value("${cms.template-web-prefix:t}")
    public String templateWebPrefix;
}
