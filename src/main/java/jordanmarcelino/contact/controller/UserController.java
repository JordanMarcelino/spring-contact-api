package jordanmarcelino.contact.controller;

import jordanmarcelino.contact.dto.UpdateUserRequest;
import jordanmarcelino.contact.dto.UserResponse;
import jordanmarcelino.contact.dto.WebResponse;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.service.UserService;
import jordanmarcelino.contact.util.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping(
            path = "/me",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<UserResponse> get(User user) {
        UserResponse response = userService.get(user);

        return WebResponse.<UserResponse>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }

    @PutMapping(
            path = "/me",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<UserResponse> update(User user, @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.update(user, request);

        return WebResponse.<UserResponse>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }

}
