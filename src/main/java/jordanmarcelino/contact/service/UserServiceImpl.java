package jordanmarcelino.contact.service;

import jordanmarcelino.contact.dto.UserResponse;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse get(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

}
