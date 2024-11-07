package jordanmarcelino.contact.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jordanmarcelino.contact.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteContactRequest {

    @JsonIgnore
    private User user;

    @JsonIgnore
    private Long id;
}
