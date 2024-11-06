package jordanmarcelino.contact.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import jordanmarcelino.contact.dto.Token;
import jordanmarcelino.contact.dto.UserLoginRequest;
import jordanmarcelino.contact.dto.UserRegisterRequest;
import jordanmarcelino.contact.dto.UserResponse;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.exception.LoginFailedException;
import jordanmarcelino.contact.exception.UserAlreadyRegisteredException;
import jordanmarcelino.contact.repository.UserRepository;
import jordanmarcelino.contact.util.BCrypt;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

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
    @Transactional
    public Token login(UserLoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            throw new LoginFailedException();
        }

        User user = optionalUser.get();
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new LoginFailedException();
        }

        Token token = new Token(
                UUID.randomUUID().toString(),
                Instant.now().plus(Duration.ofDays(30L)).toEpochMilli()
        );

        user.setToken(token.getToken());
        user.setTokenExpiredAt(token.getExpiredAt());
        userRepository.save(user);

        return token;
    }
}