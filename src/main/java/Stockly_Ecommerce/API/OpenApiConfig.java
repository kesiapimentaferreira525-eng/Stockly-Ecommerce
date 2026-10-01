package Stockly_Ecommerce.API;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI stocklyOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stockly Ecommerce API")
                        .version("1.0.0")
                        .description("API de catálogo, estoque, produtos e finalização de pedidos."));
    }
}
