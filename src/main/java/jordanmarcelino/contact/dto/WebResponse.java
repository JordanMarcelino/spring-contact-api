package jordanmarcelino.contact.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> {

    private String message;

    private T data;

    private List<Error> errors;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Error {

        private String field;

        private String message;
    }
}
