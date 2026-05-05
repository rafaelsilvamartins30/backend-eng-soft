package com.backend.api.descarteeletronico.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Descarte Eletronico API")
                .description("API backend para gerenciamento do projeto de descarte eletrônico.")
                .version("v1"));
  }
}
