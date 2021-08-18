package pointsservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
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

@DataJpaTest
@ActiveProfiles({"test"})
class PointsServiceIntegrationTest {

  @Autowired
  private TestEntityManager testEntityManager;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PayerRepository payerRepository;
  @Autowired
  private BalanceRepository balanceRepository;
  @Autowired
  private TransactionRepository transactionRepository;

  private PointsService pointsService;

  @BeforeEach
  void setUp() {
    pointsService = new PointsService(userRepository, payerRepository, balanceRepository, transactionRepository);
  }

  @Test
  void addTransaction_ExistingBalance_UpdatesBalanceAndAddsNewTransaction() {
    final Date timestamp = Date.from(Instant.now());
    final String payerName = "payerName";
    final long transactionPoints = 999;
    final long originalBalance = 1;

    final UserEntity user = UserEntity.builder().userName("userName").build();
    final PayerEntity payer = PayerEntity.builder().payerName(payerName).build();
    final BalanceEntity balance = new BalanceEntity(originalBalance, user, payer);
    user.getBalances().add(balance);

    testEntityManager.persist(user);
    testEntityManager.persist(payer);
    testEntityManager.persist(balance);

    assertThat(userRepository.getById(user.getUserId()).getBalances().size()).isEqualTo(1);
    assertThat(userRepository.getById(user.getUserId()).getTransactions()).isEmpty();
    assertThat(transactionRepository.count()).isZero();

    final var response = pointsService.addTransaction(
        user.getUserId(),
        new TransactionRequest(payer.getPayerName(), transactionPoints, timestamp)
    );
    final var expectedResponse = TransactionResponse.builder()
        .payerName(payerName)
        .transactionPoints(transactionPoints)
        .totalPoints(originalBalance + transactionPoints)
        .timestamp(timestamp)
        .build();
    final var expectedBalance = new BalanceEntity(
        originalBalance + transactionPoints,
        user, payer
    );
    assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
    assertThat(balanceRepository.getById(new BalanceId(user.getUserId(), payer.getPayerId()))).usingRecursiveComparison().isEqualTo(expectedBalance);
    assertThat(userRepository.getById(user.getUserId()).getBalances().size()).isEqualTo(1);
    assertThat(userRepository.getById(user.getUserId()).getTransactions().size()).isEqualTo(1);
    assertThat(transactionRepository.count()).isEqualTo(1);
  }

  @Test
  void addTransaction_NoBalanceFound_SavesNewBalance() {
    final Date timestamp = Date.from(Instant.now());
    final String payerName = "payerName";
    final long transactionPoints = 999;

    final UserEntity user = UserEntity.builder().userName("userName").build();
    final PayerEntity payer = PayerEntity.builder().payerName(payerName).build();

    testEntityManager.persist(user);
    testEntityManager.persist(payer);

    assertThat(userRepository.getById(user.getUserId()).getBalances()).isEmpty();

    final TransactionResponse response = pointsService.addTransaction(
        user.getUserId(),
        new TransactionRequest(payer.getPayerName(), transactionPoints, timestamp)
    );
    final TransactionResponse expectedResponse = TransactionResponse.builder()
        .payerName(payerName)
        .transactionPoints(transactionPoints)
        .totalPoints(transactionPoints)
        .timestamp(timestamp)
        .build();
    final BalanceEntity expectedBalance = new BalanceEntity(transactionPoints, user, payer);
    assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
    assertThat(balanceRepository.getById(new BalanceId(user.getUserId(), payer.getPayerId()))).usingRecursiveComparison().isEqualTo(expectedBalance);
    assertThat(userRepository.getById(user.getUserId()).getBalances()).usingRecursiveFieldByFieldElementComparator().containsExactly(expectedBalance);
  }

  @Test
  void spendPoints_ValidRequest_UsesBalancePointsOrderedByTransactionTimestamp() {
    final UserEntity user = UserEntity.builder().build();
    final PayerEntity payer1 = PayerEntity.builder().payerName("payerName1").build();
    final PayerEntity payer2 = PayerEntity.builder().payerName("payerName2").build();
    final PayerEntity payer3 = PayerEntity.builder().payerName("payerName3").build();
    final BalanceEntity balance1 = BalanceEntity.builder().user(user).payer(payer1).pointBalance(1100L).build();
    final BalanceEntity balance2 = BalanceEntity.builder().user(user).payer(payer2).pointBalance(200L).build();
    final BalanceEntity balance3 = BalanceEntity.builder().user(user).payer(payer3).pointBalance(10000L).build();
    final TransactionEntity transaction1 = TransactionEntity.builder().transactionPoints(1000L).timestamp(Date.from(Instant.parse("2020-11-02T14:00:00Z"))).balance(balance1).build();
    final TransactionEntity transaction2 = TransactionEntity.builder().transactionPoints(200L).timestamp(Date.from(Instant.parse("2020-10-31T11:00:00Z"))).balance(balance2).build();
    final TransactionEntity transaction3 = TransactionEntity.builder().transactionPoints(-200L).timestamp(Date.from(Instant.parse("2020-10-31T15:00:00Z"))).balance(balance1).build();
    final TransactionEntity transaction4 = TransactionEntity.builder().transactionPoints(10000L).timestamp(Date.from(Instant.parse("2020-11-01T14:00:00Z"))).balance(balance3).build();
    final TransactionEntity transaction5 = TransactionEntity.builder().transactionPoints(300L).timestamp(Date.from(Instant.parse("2020-10-31T10:00:00Z"))).balance(balance1).build();

    user.setBalances(Set.of(balance1, balance2, balance3));
    user.setTransactions(Arrays.asList(transaction1, transaction2, transaction3, transaction4, transaction5));

    testEntityManager.persist(user);
    testEntityManager.persist(payer1);
    testEntityManager.persist(payer2);
    testEntityManager.persist(payer3);
    testEntityManager.persist(balance1);
    testEntityManager.persist(balance2);
    testEntityManager.persist(balance3);
    testEntityManager.persist(transaction1);
    testEntityManager.persist(transaction2);
    testEntityManager.persist(transaction3);
    testEntityManager.persist(transaction4);
    testEntityManager.persist(transaction5);

    final Set<UserSpendResponse> response = pointsService.spendPoints(user.getUserId(), new UserSpendRequest(5000L));

    final var expectedUserSpend1 = new UserSpendResponse(payer1.getPayerName(), -100L);
    final var expectedUserSpend2 = new UserSpendResponse(payer2.getPayerName(), -200L);
    final var expectedUserSpend3 = new UserSpendResponse(payer3.getPayerName(), -4700L);

    assertThat(response).containsExactlyInAnyOrder(expectedUserSpend1, expectedUserSpend2, expectedUserSpend3);
    assertThat(balanceRepository.getById(new BalanceId(user.getUserId(), payer1.getPayerId())).getPointBalance()).isEqualTo(1000);
    assertThat(balanceRepository.getById(new BalanceId(user.getUserId(), payer2.getPayerId())).getPointBalance()).isZero();
    assertThat(balanceRepository.getById(new BalanceId(user.getUserId(), payer3.getPayerId())).getPointBalance()).isEqualTo(5300);
  }

  @Test
  void spendPoints_TransactionExceedsBalance_SkipsTransaction() {
    final long pointsToSpend = 500;
    final UserEntity user = UserEntity.builder().build();
    final PayerEntity payer = PayerEntity.builder().payerName("payerName").build();
    final PayerEntity payerToSkip = PayerEntity.builder().payerName("payerToSkipName").build();
    final BalanceEntity balance = BalanceEntity.builder().user(user).payer(payer).pointBalance(1100L).build();
    final BalanceEntity balanceToSkip = BalanceEntity.builder().user(user).payer(payerToSkip).pointBalance(199L).build();
    final TransactionEntity transaction = TransactionEntity.builder().transactionPoints(pointsToSpend).timestamp(Date.from(Instant.parse("2020-11-02T14:00:00Z"))).balance(balance).build();
    final TransactionEntity transactionToSkip = TransactionEntity.builder().transactionPoints(200L).timestamp(Date.from(Instant.parse("2020-10-31T11:00:00Z"))).balance(balanceToSkip).build();
    user.setBalances(Set.of(balance, balanceToSkip));
    user.setTransactions(Arrays.asList(transaction, transactionToSkip));

    testEntityManager.persist(user);
    testEntityManager.persist(payer);
    testEntityManager.persist(payerToSkip);
    testEntityManager.persist(balance);
    testEntityManager.persist(balanceToSkip);
    testEntityManager.persist(transaction);
    testEntityManager.persist(transactionToSkip);


    final Set<UserSpendResponse> response = pointsService.spendPoints(user.getUserId(), new UserSpendRequest(pointsToSpend));
    final UserSpendResponse expected = new UserSpendResponse(payer.getPayerName(), -pointsToSpend);
    assertThat(response).containsExactly(expected);
    assertThat(balanceRepository.getById(new BalanceId(user.getUserId(), payer.getPayerId())).getPointBalance())
        .isEqualTo(600);
    assertThat(balanceRepository.getById(new BalanceId(user.getUserId(), payerToSkip.getPayerId())).getPointBalance())
        .isEqualTo(199);
  }

  @Test
  void spendPoints_PointsRemainingFromDeductions_ThrowsInsufficientBalanceException() {
    final UserEntity user = UserEntity.builder().build();
    final PayerEntity payer = PayerEntity.builder().payerName("payerName").build();
    final PayerEntity payer2 = PayerEntity.builder().payerName("payer2").build();
    final BalanceEntity balance = BalanceEntity.builder().user(user).payer(payer).pointBalance(6L).build();
    final BalanceEntity balance2 = BalanceEntity.builder().user(user).payer(payer2).pointBalance(3L).build();
    final TransactionEntity transaction = TransactionEntity.builder()
        .transactionPoints(6L)
        .timestamp(Date.from(Instant.parse("2020-10-01T12:00:00Z")))
        .balance(balance).build();
    final TransactionEntity transactionToSkip = TransactionEntity.builder()
        .transactionPoints(4L)
        .timestamp(Date.from(Instant.parse("2020-11-01T12:00:00Z"))).balance(balance2).build();
    user.setBalances(Set.of(balance, balance2));
    user.setTransactions(Arrays.asList(transaction, transactionToSkip));

    testEntityManager.persist(user);
    testEntityManager.persist(payer);
    testEntityManager.persist(payer2);
    testEntityManager.persist(balance);
    testEntityManager.persist(balance2);
    testEntityManager.persist(transaction);
    testEntityManager.persist(transactionToSkip);

    assertThatThrownBy(() -> pointsService.spendPoints(user.getUserId(), new UserSpendRequest(10L)))
        .isInstanceOf(InsufficientBalanceException.class)
        .hasMessageContaining(String.valueOf(10L));
  }

  @Test
  void getPointsBalance_UserExists_ReturnsPointsBalanceResponse() {
    final UserEntity user = UserEntity.builder().build();
    final PayerEntity payer1 = PayerEntity.builder().payerName("payerName1").build();
    final PayerEntity payer2 = PayerEntity.builder().payerName("payerName2").build();
    final PayerEntity payer3 = PayerEntity.builder().payerName("payerName3").build();
    final BalanceEntity balance1 = BalanceEntity.builder()
        .user(user)
        .payer(payer1)
        .pointBalance(1100L)
        .build();
    final BalanceEntity balance2 = BalanceEntity.builder()
        .user(user)
        .payer(payer2)
        .pointBalance(200L)
        .build();
    final BalanceEntity balance3 = BalanceEntity.builder()
        .user(user)
        .payer(payer3)
        .pointBalance(10000L)
        .build();
    user.setBalances(Set.of(balance1, balance2, balance3));
    testEntityManager.persist(user);
    testEntityManager.persist(payer1);
    testEntityManager.persist(payer2);
    testEntityManager.persist(payer3);
    testEntityManager.persist(balance1);
    testEntityManager.persist(balance2);
    testEntityManager.persist(balance3);

    final PointsBalanceResponse response = pointsService.getPointsBalance(user.getUserId());
    final PointsBalanceResponse expectedResponse = new PointsBalanceResponse(Map.of(
        payer1.getPayerName(), balance1.getPointBalance(),
        payer2.getPayerName(), balance2.getPointBalance(),
        payer3.getPayerName(), balance3.getPointBalance()
    ));
    assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);
  }
}
