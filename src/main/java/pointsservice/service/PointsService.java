package pointsservice.service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import pointsservice.error.model.InsufficientBalanceException;
import pointsservice.model.entity.BalanceEntity;
import pointsservice.model.entity.BalanceId;
import pointsservice.model.entity.PayerEntity;
import pointsservice.model.entity.TransactionEntity;
import pointsservice.model.entity.UserEntity;
import pointsservice.model.request.TransactionRequest;
import pointsservice.model.request.UserSpendRequest;
import pointsservice.model.response.PointsBalanceResponse;
import pointsservice.model.response.TransactionResponse;
import pointsservice.model.response.UserSpendResponse;
import pointsservice.repository.BalanceRepository;
import pointsservice.repository.PayerRepository;
import pointsservice.repository.TransactionRepository;
import pointsservice.repository.UserRepository;

@Service
public class PointsService {

  private final UserRepository userRepository;
  private final PayerRepository payerRepository;
  private final BalanceRepository balanceRepository;
  private final TransactionRepository transactionRepository;

  public PointsService(
      final UserRepository userRepository,
      final PayerRepository payerRepository,
      final BalanceRepository balanceRepository,
      final TransactionRepository transactionRepository
  ) {
    this.userRepository = userRepository;
    this.payerRepository = payerRepository;
    this.balanceRepository = balanceRepository;
    this.transactionRepository = transactionRepository;
  }

  public TransactionResponse addTransaction(
      final Long userId,
      final TransactionRequest transactionRequest
  ) {
    final UserEntity user = userRepository.getById(userId);
    final PayerEntity payer = payerRepository
        .findByPayerNameIgnoreCase(transactionRequest.getPayerName().toUpperCase())
        .orElseThrow();
    final BalanceEntity balance = balanceRepository
        .findById(new BalanceId(user.getUserId(), payer.getPayerId()))
        .orElse(new BalanceEntity(0L, user, payer));

    balance.setPointBalance(balance.getPointBalance() + transactionRequest.getTransactionPoints());
    balanceRepository.save(balance);

    final TransactionEntity transaction = transactionRepository.save(TransactionEntity.builder()
        .transactionPoints(transactionRequest.getTransactionPoints())
        .timestamp(transactionRequest.getTimestamp())
        .balance(balance)
        .build()
    );

    user.getTransactions().add(transaction);
    user.getBalances().add(balance);
    userRepository.save(user);

    return TransactionResponse.builder()
        .payerName(payer.getPayerName())
        .transactionPoints(transaction.getTransactionPoints())
        .totalPoints(balance.getPointBalance())
        .timestamp(transaction.getTimestamp())
        .build();
  }

  @SneakyThrows
  public Set<UserSpendResponse> spendPoints(final Long userId, final UserSpendRequest userSpendRequest) {
    final List<TransactionEntity> transactions = userRepository.getById(userId)
        .getTransactions()
        .stream()
        .sorted(Comparator
            .comparing(TransactionEntity::getTimestamp)
            .thenComparing(TransactionEntity::getTransactionPoints)
            .thenComparing(transactionEntity -> transactionEntity.getBalance().getPointBalance(), Comparator.reverseOrder())
        ).collect(Collectors.toList());

    final Map<String, Long> payerDeductions = new HashMap<>();
    final Set<BalanceEntity> balancesToUpdate = new HashSet<>();
    long pointsRemaining = userSpendRequest.getPoints();

    for (var transaction : transactions) {
      BalanceEntity balance = transaction.getBalance();
      if (transaction.getTransactionPoints() <= balance.getPointBalance()) {
        final long deductionAmount = Math.min(
            transaction.getTransactionPoints(),
            pointsRemaining
        );
        pointsRemaining -= deductionAmount;
        balance.setPointBalance(balance.getPointBalance() - deductionAmount);
        balancesToUpdate.add(balance);
        payerDeductions.put(
            balance.getPayer().getPayerName(),
            payerDeductions.getOrDefault(balance.getPayer().getPayerName(), 0L) - deductionAmount
        );
        if (pointsRemaining == 0) {
          break;
        }
      }
    }
    if (pointsRemaining > 0) {
      throw new InsufficientBalanceException(String.format("Insufficient funds for requested %d points", userSpendRequest.getPoints()));
    }
    balanceRepository.saveAll(balancesToUpdate);
    return payerDeductions.entrySet().stream()
        .map(entry -> new UserSpendResponse(entry.getKey(), entry.getValue()))
        .collect(Collectors.toSet());
  }

  public PointsBalanceResponse getPointsBalance(final Long userId) {
    return new PointsBalanceResponse(
        userRepository.getById(userId).getBalances().stream()
            .collect(Collectors.toMap(
                balance -> balance.getPayer().getPayerName(),
                BalanceEntity::getPointBalance
            ))
    );
  }
}
