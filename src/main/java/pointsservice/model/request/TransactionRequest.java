package pointsservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

  @JsonProperty("payer")
  private String payerName;
  @JsonProperty("points")
  private Long transactionPoints;
  private Date timestamp;
}
