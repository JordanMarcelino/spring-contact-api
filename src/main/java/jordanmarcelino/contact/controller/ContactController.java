package jordanmarcelino.contact.controller;

import jordanmarcelino.contact.dto.ContactResponse;
import jordanmarcelino.contact.dto.CreateContactRequest;
import jordanmarcelino.contact.dto.WebResponse;
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
}
