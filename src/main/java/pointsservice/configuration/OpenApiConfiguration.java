package pointsservice.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.Generated;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class OpenApiConfiguration {

  @Bean
  @Generated
  public OpenAPI openApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Points Service (Fetch Challenge)")
            .description("Default valid userId's: 0,1,2,3,4,5 ----- Default valid payer names: DANNON, UNILEVER, MILLER_COORS")
        );
  }
}
