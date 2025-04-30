package com.aymengabsi.ecommerce.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("🛒 Product Service API")
                        .version("1.0.0")
                        .description("""
                            This service manages the product catalog, including:
                            - Product creation and updates
                            - Inventory and pricing
                            - Purchase endpoints

                            **Base path:** `/api/v1/products`
                            """)
                        .termsOfService("https://example.com/terms")
                        .contact(new Contact()
                                .name("Aymen Gabsi")
                                .url("https://aymengabsi.dev")
                                .email("aymengabsi.se@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html"))
                );
    }
}