package jordanmarcelino.contact.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jordanmarcelino.contact.dto.WebResponse;
import jordanmarcelino.contact.util.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<Object>> handleConstraintViolationException(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        List<WebResponse.Error> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : violations) {
            errors.add(
                    WebResponse.Error.builder()
                            .field(violation.getPropertyPath().toString())
                            .message(violation.getMessage())
                            .build()
            );
        }

        return ResponseEntity
                .badRequest()
                .body(
                        WebResponse.builder()
                                .message(Message.BAD_REQUEST)
                                .errors(errors)
                                .build()
                );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<WebResponse<Object>> handleResponseStatusException(ResponseStatusException exception) {
        return ResponseEntity
                .status(exception.getStatusCode())
                .body(WebResponse.builder().message(exception.getMessage()).build());
    }
}
