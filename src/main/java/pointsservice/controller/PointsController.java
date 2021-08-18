package pointsservice.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Objects;
import java.util.Set;
import javax.validation.Valid;
import lombok.SneakyThrows;
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

@Tag(name = "API")
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
      @Parameter(in = ParameterIn.PATH, example = "0")
      @PathVariable(required = false) final Long userId,
      @Valid @RequestBody final TransactionRequest transactionRequest
  ) {
    return pointsService.addTransaction(Objects.requireNonNullElse(userId, DEFAULT_USER_ID), transactionRequest);
  }

  @SneakyThrows
  @PatchMapping
  public Set<UserSpendResponse> spendPoints(
      @Parameter(in = ParameterIn.PATH, example = "0")
      @PathVariable(required = false) final Long userId,
      @Valid @RequestBody final UserSpendRequest userSpendRequest
  ) {
    return pointsService.spendPoints(Objects.requireNonNullElse(userId, DEFAULT_USER_ID), userSpendRequest);
  }

  @GetMapping
  public PointsBalanceResponse getPointsBalance(@Parameter(in = ParameterIn.PATH, example = "0") @PathVariable(required = false) final Long userId) {
    return pointsService.getPointsBalance(Objects.requireNonNullElse(userId, DEFAULT_USER_ID));
  }
}
