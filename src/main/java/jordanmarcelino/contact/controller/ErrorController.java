package jordanmarcelino.contact.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jordanmarcelino.contact.dto.WebResponse;
import jordanmarcelino.contact.util.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<WebResponse<Object>> handleConstraintViolationException(ConstraintViolationException exception) {
        Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
        Map<String, String> fieldErrorMessages = new HashMap<>();

        if (violations != null) {
            for (ConstraintViolation<?> violation : violations) {
                String field = violation.getPropertyPath().toString();
                String message = violation.getMessage();

                fieldErrorMessages.merge(field, message, (existing, newMsg) -> existing + "; " + newMsg);
            }
        }

        List<WebResponse.Error> errors = fieldErrorMessages.entrySet().stream()
                .map(entry -> WebResponse.Error.builder()
                        .field(entry.getKey())
                        .message(entry.getValue())
                        .build())
                .collect(Collectors.toList());

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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<WebResponse<Object>> handleException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(WebResponse.builder().message(Message.INTERNAL_SERVER_ERROR).build());
    }
}

