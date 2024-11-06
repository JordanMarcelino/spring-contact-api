package jordanmarcelino.contact.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.time.Instant;

@Component
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    private final String LATENCY_ATTRIBUTE = "latency";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(LATENCY_ATTRIBUTE, Instant.now());
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String addr = request.getRemoteAddr();
        String path = request.getRequestURI();
        String method = request.getMethod();
        int statusCode = response.getStatus();
        Duration latency = Duration.between(
                (Instant) request.getAttribute(LATENCY_ATTRIBUTE),
                Instant.now()
        );

        log.info("Incoming Request Path={} Method={} ClientIP={} StatusCode={} Latency={}ms", path, method, addr, statusCode, latency.toMillis());
    }
}
