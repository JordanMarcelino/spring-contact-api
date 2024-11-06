package jordanmarcelino.contact.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jordanmarcelino.contact.dto.*;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.service.AuthService;
import jordanmarcelino.contact.util.Message;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/auth")
@AllArgsConstructor
public class AuthController {

    private AuthService authService;

    @PostMapping(
            path = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<UserResponse> register(@RequestBody UserRegisterRequest request) {
        UserResponse response = authService.register(request);

        return WebResponse.<UserResponse>builder()
                .message(Message.SUCCESS)
                .data(response)
                .build();
    }

    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<Object> register(@RequestBody UserLoginRequest request, HttpServletResponse response) {
        Token token = authService.login(request);
        Cookie cookie = new Cookie("X-API-KEY", token.getToken());
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return WebResponse.builder()
                .message(Message.SUCCESS)
                .build();
    }

    @PostMapping(
            path = "/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.OK)
    public WebResponse<Object> logout(User user) {
        authService.logout(user);

        return WebResponse.builder()
                .message(Message.SUCCESS)
                .build();
    }
}
