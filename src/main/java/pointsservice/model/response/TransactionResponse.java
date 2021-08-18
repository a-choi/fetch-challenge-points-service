package pointsservice.model.response;

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
public class TransactionResponse {

  @JsonProperty("payer")
  private String payerName;
  private Long transactionPoints;
  private Long totalPoints;
  private Date timestamp;
}
