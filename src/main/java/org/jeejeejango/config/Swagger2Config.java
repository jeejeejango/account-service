package org.jeejeejango.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * @author jeejeejango
 * @since 20/11/2018 10:25 PM
 */
@Configuration
@EnableSwagger2
@Import(springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class)
public class Swagger2Config {

    @Bean
    public Docket petApi() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("org.jeejeejango"))
            .paths(PathSelectors.ant("/api/**/*"))
            .build()
            .apiInfo(apiInfo());
    }


    private ApiInfo apiInfo() {
        return new ApiInfo(
            "Account REST API",
            "The Account APIs are typical REST APIs that handles the account information.",
            "v1",
            "Terms of service",
            new Contact("jeejeejango", "https://github.com/jeejeejango", "admin@jeejeejango.com"),
            "Apache 2.0",
            "https://www.apache.org/licenses/LICENSE-2.0",
            Collections.emptyList());
    }


}
