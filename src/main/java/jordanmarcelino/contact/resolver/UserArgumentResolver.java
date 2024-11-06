package jordanmarcelino.contact.resolver;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.exception.UnauthorizedException;
import jordanmarcelino.contact.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Cookie[] cookies = request.getCookies();
        if (Objects.isNull(cookies)) {
            throw new UnauthorizedException();
        }

        Cookie apiKey = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("X-API-KEY")).findFirst().orElse(null);

        if (Objects.isNull(apiKey)) {
            throw new UnauthorizedException();
        }

        String token = apiKey.getValue();
        User user = userRepository.findByToken(token).orElse(null);
        if (Objects.isNull(user)) {
            throw new UnauthorizedException();
        }
        if (user.getTokenExpiredAt() < System.currentTimeMillis()) {
            throw new UnauthorizedException();
        }

        return user;
    }
}
