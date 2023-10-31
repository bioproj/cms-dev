package com.wangyang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//@EnableJms
@ComponentScan(basePackages = {"com.wangyang"})
@EnableJpaRepositories(basePackages = {"com.wangyang"})
@EntityScan(basePackages = {"com.wangyang"})
@EnableCaching
@EnableAsync
@EnableSwagger2
public class CmsApplication {



	public static void main(String[] args) {
		// Customize the spring config location
		System.setProperty("spring.config.additional-location", "optional:file:${user.home}/cms/application.yml");
//		System.setProperty("spring.config.additional-location",
//				"optional:file:${user.home}/cms/");
		SpringApplication.run(CmsApplication.class, args);
	}

}
