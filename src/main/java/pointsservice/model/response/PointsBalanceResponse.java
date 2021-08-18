package pointsservice.model.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Schema(example = "{ \"payer\": \"balance\"}")
public class PointsBalanceResponse {

  private Map<String, Long> payerBalances;

  @JsonAnyGetter
  public Map<String, Long> getPayerBalances() {
    return this.payerBalances;
  }
}
