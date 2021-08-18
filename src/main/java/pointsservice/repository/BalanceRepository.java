package pointsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointsservice.model.entity.BalanceEntity;
import pointsservice.model.entity.BalanceId;

@Repository
public interface BalanceRepository extends JpaRepository<BalanceEntity, BalanceId> {

}