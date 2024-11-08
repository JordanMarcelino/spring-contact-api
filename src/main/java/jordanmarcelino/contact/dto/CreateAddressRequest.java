package jordanmarcelino.contact.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
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
public class CreateAddressRequest {

    @JsonIgnore
    private User user;

    @JsonIgnore
    private Long contactId;

    @NotBlank
    @Size(max = 100)
    private String country;

    @Size(max = 100)
    private String province;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String street;

    @Size(max = 100)
    private String postalCode;
}
