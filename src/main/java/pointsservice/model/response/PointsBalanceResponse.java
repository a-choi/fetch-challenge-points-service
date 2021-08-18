package pointsservice.model.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PointsBalanceResponse {

  private Map<String, Long> payerBalances;

  @JsonAnyGetter
  public Map<String, Long> getPayerBalances() {
    return this.payerBalances;
  }
}
