package jordanmarcelino.contact.controller;

import jordanmarcelino.contact.dto.*;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.service.AddressService;
import jordanmarcelino.contact.util.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/contacts/{contactId}/addresses")
@AllArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<List<AddressResponse>> get(
            User user,
            @PathVariable("contactId") Long contactId
    ) {
        GetAddressRequest request = new GetAddressRequest();
        request.setUser(user);
        request.setContactId(contactId);
        List<AddressResponse> response = addressService.findAll(request);

        return WebResponse.<List<AddressResponse>>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }

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

    @GetMapping(
            path = "{addressId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<AddressResponse> get(
            User user,
            @PathVariable("contactId") Long contactId,
            @PathVariable("addressId") Long addressId
    ) {
        AddressResponse response = addressService.get(new GetAddressRequest(user, addressId, contactId));

        return WebResponse.<AddressResponse>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }
}
