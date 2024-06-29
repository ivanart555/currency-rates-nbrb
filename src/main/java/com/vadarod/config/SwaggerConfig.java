package com.vadarod.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Currency Rates API",
                version = "1.0",
                description = "API for managing and retrieving currency rates",
                contact = @Contact(
                        name = "Ivan Artsiushyn",
                        email = "ivanart555@gmail.com"
                )
        )
)
public class SwaggerConfig {
}
