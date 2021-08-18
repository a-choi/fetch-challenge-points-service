package pointsservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

  @NotBlank
  @JsonProperty("payer")
  private String payerName;

  @NotNull
  @JsonProperty("points")
  private Long transactionPoints;

  @NotNull
  private Date timestamp;
}
