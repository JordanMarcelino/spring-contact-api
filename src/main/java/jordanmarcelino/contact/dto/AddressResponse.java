package jordanmarcelino.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressResponse {

    private Long id;

    private String country;

    private String province;

    private String city;

    private String street;

    private String postalCode;

}
