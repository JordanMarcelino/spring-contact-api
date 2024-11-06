package jordanmarcelino.contact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyRegisteredException extends ResponseStatusException {

    public UserAlreadyRegisteredException() {
        super(HttpStatus.BAD_REQUEST, "user already registered");
    }

}
