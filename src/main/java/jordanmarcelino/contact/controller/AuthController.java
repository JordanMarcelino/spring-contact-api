package jordanmarcelino.contact.controller;

import jordanmarcelino.contact.dto.UserRegisterRequest;
import jordanmarcelino.contact.dto.UserResponse;
import jordanmarcelino.contact.dto.WebResponse;
import jordanmarcelino.contact.service.UserService;
import jordanmarcelino.contact.util.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
@AllArgsConstructor
public class AuthController {

    private UserService userService;

    @PostMapping(
            path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<UserResponse> register(@RequestBody UserRegisterRequest request) {
        UserResponse response = userService.register(request);

        return WebResponse.<UserResponse>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }
}
