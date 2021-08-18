package pointsservice.error;

import java.util.NoSuchElementException;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pointsservice.error.model.ErrorResponse;
import pointsservice.error.model.InvalidTransactionException;

@ControllerAdvice
public class PointsExceptionHandler extends ResponseEntityExceptionHandler {

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
  public final ResponseEntity<Object> handleBadRequestError(final Exception exception) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
  }

  @ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
  public ResponseEntity<Object> handleNotFoundError(final Exception exception) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage());
  }

  @ExceptionHandler(InvalidTransactionException.class)
  public ResponseEntity<Object> handleInvalidTransactionException(final InvalidTransactionException exception) {
    return buildErrorResponse(HttpStatus.I_AM_A_TEAPOT, exception.getMessage());
  }


  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleError(final Exception exception) {
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getCause().toString());
  }

  private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(new ErrorResponse(status.value(), message));
  }
}
