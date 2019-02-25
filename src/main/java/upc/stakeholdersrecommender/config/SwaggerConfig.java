package upc.stakeholdersrecommender.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    /**
     * API Documentation Generation.
     *
     * @return
     */
   /* public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .pathMapping("/")
                .select()
                .paths(PathSelectors.regex("/upc/stakeholders-recommender/recommed|/upc/stakeholders-recommender/batch_process|/upc/stakeholders-recommender/reject_recommendation"))
                .apis(RequestHandlerSelectors.basePackage("upc.stakeholders-recommender")).paths(PathSelectors.regex("/upc.*"))
                .build().tags(new Tag("Stakeholder Recommender", "REST API to handle stakeholder recommender"));
    }
    /**
     * Informtion that appear in the API Documentation Head.
     *
     * @return
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
/*
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(title).description(description).license(LICENSE_TEXT)
                .contact(new Contact("UPC-GESSI (OPENReq)", "http://openreq.eu/", ""))
                .build();
    }
    */
}