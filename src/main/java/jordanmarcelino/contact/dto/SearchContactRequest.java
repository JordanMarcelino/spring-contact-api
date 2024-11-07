package jordanmarcelino.contact.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jordanmarcelino.contact.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchContactRequest {

    @JsonIgnore
    private User user;

    @Size(max = 200)
    private String name;

    @Size(max = 255)
    private String email;

    @Size(max = 100)
    private String phone;

    @Min(value = 0)
    private Integer page;

    @Min(value = 1)
    private Integer size;
}
