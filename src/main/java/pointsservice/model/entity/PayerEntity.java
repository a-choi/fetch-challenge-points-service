package pointsservice.model.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long payerId;
  private String payerName;
}
