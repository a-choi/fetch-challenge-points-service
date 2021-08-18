package pointsservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointsservice.model.entity.PayerEntity;

@Repository
public interface PayerRepository extends JpaRepository<PayerEntity, Long> {

  Optional<PayerEntity> findByPayerNameIgnoreCase(final String payerName);
}
