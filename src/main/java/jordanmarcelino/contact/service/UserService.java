package jordanmarcelino.contact.service;

import jordanmarcelino.contact.dto.UpdateUserRequest;
import jordanmarcelino.contact.dto.UserResponse;
import jordanmarcelino.contact.entity.User;

public interface UserService {

    UserResponse get(User user);

    UserResponse update(User user, UpdateUserRequest request);
}
