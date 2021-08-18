package pointsservice.model.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@IdClass(BalanceId.class)
@Table(name = "balances")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceEntity {

  private Long pointBalance;

  @Id
  @ManyToOne
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Id
  @ManyToOne
  @JoinColumn(name = "payer_id")
  private PayerEntity payer;
}
