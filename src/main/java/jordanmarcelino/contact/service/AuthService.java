package jordanmarcelino.contact.service;

import jordanmarcelino.contact.dto.Token;
import jordanmarcelino.contact.dto.UserLoginRequest;
import jordanmarcelino.contact.dto.UserRegisterRequest;
import jordanmarcelino.contact.dto.UserResponse;

public interface AuthService {

    UserResponse register(UserRegisterRequest request);

    Token login(UserLoginRequest request);
}
