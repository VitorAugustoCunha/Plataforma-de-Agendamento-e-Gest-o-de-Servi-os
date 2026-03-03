package com.agenda.plataform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Plataforma de Agendamento e Gestão de Serviços")
                        .description("API para agendamento de serviços com profissionais, " +
                                "gerenciamento de disponibilidade, pagamentos e notificações")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sem nome")
                                .email("email@exemplo.com")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Token JWT para autenticação")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
