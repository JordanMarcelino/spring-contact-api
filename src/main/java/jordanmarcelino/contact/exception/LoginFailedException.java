package jordanmarcelino.contact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class LoginFailedException extends ResponseStatusException {

    public LoginFailedException() {
        super(HttpStatus.BAD_REQUEST, "username or password is wrong");
    }

}
