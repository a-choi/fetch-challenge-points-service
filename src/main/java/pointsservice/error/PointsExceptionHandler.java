package pointsservice.error;

import java.util.NoSuchElementException;
import java.util.Objects;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import pointsservice.error.model.ErrorResponse;
import pointsservice.error.model.InsufficientBalanceException;

@ControllerAdvice
public class PointsExceptionHandler {

  @ExceptionHandler({
      BindException.class,
      ConstraintViolationException.class,
      HttpMessageNotReadableException.class,
      MethodArgumentNotValidException.class,
      MethodArgumentTypeMismatchException.class,
      MissingServletRequestParameterException.class,
      MissingServletRequestPartException.class,
      ServletRequestBindingException.class,
      TypeMismatchException.class,
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public final ResponseEntity<ErrorResponse> handleBadRequestError(final Exception exception) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler({
      EntityNotFoundException.class,
      JpaObjectRetrievalFailureException.class,
      NoSuchElementException.class,
  })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> handleNotFoundError(final Exception exception) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(InsufficientBalanceException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTransactionException(final InsufficientBalanceException exception) {
    return buildErrorResponse(HttpStatus.I_AM_A_TEAPOT, exception.getMessage());
  }


  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleError(final Exception exception) {
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        Objects.requireNonNullElse(exception.getCause(), new Throwable("Internal Server Error")).toString()
    );
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(new ErrorResponse(status.value(), message));
  }
}
