package pointsservice.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Objects;
import java.util.Set;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pointsservice.model.request.TransactionRequest;
import pointsservice.model.request.UserSpendRequest;
import pointsservice.model.response.PointsBalanceResponse;
import pointsservice.model.response.TransactionResponse;
import pointsservice.model.response.UserSpendResponse;
import pointsservice.service.PointsService;

@RestController
@RequestMapping(
    value = {"/points/user/{userId}", "/points/user"},
    produces = APPLICATION_JSON_VALUE
)
public class PointsController {

  private static final long DEFAULT_USER_ID = 0;

  private final PointsService pointsService;

  public PointsController(final PointsService pointsService) {
    this.pointsService = pointsService;
  }

  @PostMapping
  public TransactionResponse addTransaction(
      @PathVariable(required = false) final Long userId,
      @RequestBody final TransactionRequest transactionRequest
  ) {
    return pointsService.addTransaction(Objects.requireNonNullElse(userId, DEFAULT_USER_ID), transactionRequest);
  }

  @PatchMapping
  public Set<UserSpendResponse> spendPoints(
      @PathVariable(required = false) final Long userId,
      @RequestBody final UserSpendRequest userSpendRequest
  ) {
    return pointsService.spendPoints(Objects.requireNonNullElse(userId, DEFAULT_USER_ID), userSpendRequest);
  }

  @GetMapping
  public PointsBalanceResponse getPointsBalance(@PathVariable(required = false) final Long userId) {
    return pointsService.getPointsBalance(Objects.requireNonNullElse(userId, DEFAULT_USER_ID));
  }
}
