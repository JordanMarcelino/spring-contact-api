package jordanmarcelino.contact.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jordanmarcelino.contact.dto.UserLoginRequest;
import jordanmarcelino.contact.dto.UserRegisterRequest;
import jordanmarcelino.contact.dto.UserResponse;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.exception.UserAlreadyRegisteredException;
import jordanmarcelino.contact.repository.UserRepository;
import jordanmarcelino.contact.util.BCrypt;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final Validator validator;


    @Override
    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        Set<ConstraintViolation<UserRegisterRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        User user = new User();
        user.setUsername(request.getUsername());

        if (userRepository.findOne(Example.of(user)).isPresent()) {
            throw new UserAlreadyRegisteredException();
        }

        user.setName(request.getName());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUsername(), user.getName());
    }

    @Override
    public void login(UserLoginRequest request) {

    }
}
