package jordanmarcelino.contact.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {

    @JsonIgnore
    private Long id;

    @Size(max = 100)
    private String name;

    @Size(min = 8, max = 100)
    private String password;
}
