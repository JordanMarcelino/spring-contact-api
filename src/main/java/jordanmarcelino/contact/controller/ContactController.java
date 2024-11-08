package jordanmarcelino.contact.controller;

import jordanmarcelino.contact.dto.*;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.service.ContactService;
import jordanmarcelino.contact.util.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contacts")
@AllArgsConstructor
public class ContactController {

    private ContactService contactService;

    @PostMapping(
            path = "",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<ContactResponse> save(User user, @RequestBody CreateContactRequest request) {
        request.setUser(user);
        ContactResponse response = contactService.save(request);

        return WebResponse.<ContactResponse>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }

    @GetMapping(
            path = "/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<ContactResponse> get(User user, @PathVariable("contactId") Long contactId) {
        ContactResponse response = contactService.get(new GetContactRequest(user, contactId));

        return WebResponse.<ContactResponse>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }

    @PutMapping(
            path = "/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<ContactResponse> update(
            User user,
            @RequestBody UpdateContactRequest request,
            @PathVariable("contactId") Long contactId
    ) {
        request.setUser(user);
        request.setId(contactId);
        ContactResponse response = contactService.update(request);

        return WebResponse.<ContactResponse>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }

    @DeleteMapping(
            path = "/{contactId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<ContactResponse> delete(
            User user,
            @PathVariable("contactId") Long contactId
    ) {
        contactService.delete(new DeleteContactRequest(user, contactId));

        return WebResponse.<ContactResponse>builder()
                .message(Message.SUCCESS)
                .build();
    }
}
