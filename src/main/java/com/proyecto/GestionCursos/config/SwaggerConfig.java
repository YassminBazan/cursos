package com.proyecto.GestionCursos.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI gestionCursosOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Gestión de Cursos")
                        .description("Documentación de la API del sistema de Gestión de Cursos")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Yassmin Bazán")
                                .email("yassmin@example.com")
                                .url("https://github.com/yassminbazan"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Repositorio en GitHub")
                        .url("https://github.com/yassminbazan/gestion-cursos"));
    }
}
