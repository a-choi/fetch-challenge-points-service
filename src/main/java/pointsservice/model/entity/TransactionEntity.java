package pointsservice.model.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long transactionId;
  private Long transactionPoints;
  private Date timestamp;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  @JoinColumn(name = "payer_id", referencedColumnName = "payer_id")
  private BalanceEntity balance;
}
