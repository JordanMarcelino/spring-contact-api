package jordanmarcelino.contact.controller;

import jordanmarcelino.contact.dto.*;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.service.AddressService;
import jordanmarcelino.contact.util.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/contacts/{contactId}/addresses")
@AllArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<AddressResponse> save(
            User user,
            @RequestBody CreateAddressRequest request,
            @PathVariable("contactId") Long contactId
    ) {
        request.setUser(user);
        request.setContactId(contactId);

        AddressResponse response = addressService.save(request);

        return WebResponse.<AddressResponse>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }

    @PutMapping(
            path = "/{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<AddressResponse> update(
            User user,
            @RequestBody UpdateAddressRequest request,
            @PathVariable("contactId") Long contactId,
            @PathVariable("addressId") Long addressId
    ) {
        request.setUser(user);
        request.setContactId(contactId);
        request.setId(addressId);

        AddressResponse response = addressService.update(request);

        return WebResponse.<AddressResponse>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }

    @DeleteMapping(
            path = "{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<AddressResponse> delete(
            User user,
            @PathVariable("contactId") Long contactId,
            @PathVariable("addressId") Long addressId
    ) {
        addressService.delete(new DeleteAddressRequest(user, addressId, contactId));

        return WebResponse.<AddressResponse>builder()
                .message(Message.SUCCESS)
                .build();
    }
}
