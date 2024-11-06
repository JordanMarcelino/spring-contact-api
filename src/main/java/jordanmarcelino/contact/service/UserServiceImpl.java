package jordanmarcelino.contact.service;

import jdk.jfr.TransitionTo;
import jordanmarcelino.contact.dto.UpdateUserRequest;
import jordanmarcelino.contact.dto.UserResponse;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.repository.UserRepository;
import jordanmarcelino.contact.util.BCrypt;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ValidationService validationService;

    @Override
    public UserResponse get(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

    @Override
    @Transactional
    public UserResponse update(User user, UpdateUserRequest request) {
        validationService.validate(request);

        if (Objects.nonNull(request.getName())) {
            user.setName(request.getName());
        }
        if (Objects.nonNull(request.getPassword())) {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }
        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }
}
