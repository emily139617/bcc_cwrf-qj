package com.itstyle.bcc_cwrf.common.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket userApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("bcc_cwrf").apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("com.itstyle.bcc_cwrf.web")).paths(PathSelectors.any()).build();
	}
	// 预览地址:swagger-ui.html
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Spring 中使用Swagger2构建文档").termsOfServiceUrl("https://baidu.com")
				.contact(new Contact("bcc_cwrf接口文档 ", "https://baidu.com/", "1520969995@qq.com")).version("1" +
						".1").build();
	}
}
