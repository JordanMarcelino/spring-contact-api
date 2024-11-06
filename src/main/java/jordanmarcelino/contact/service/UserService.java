package jordanmarcelino.contact.service;

import jordanmarcelino.contact.dto.UserLoginRequest;
import jordanmarcelino.contact.dto.UserRegisterRequest;
import jordanmarcelino.contact.dto.UserResponse;

public interface UserService {

    UserResponse register(UserRegisterRequest request);

    void login(UserLoginRequest request);
}
