package jordanmarcelino.contact.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jordanmarcelino.contact.dto.Token;
import jordanmarcelino.contact.dto.UserResponse;
import jordanmarcelino.contact.dto.WebResponse;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.repository.UserRepository;
import jordanmarcelino.contact.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Cookie apiKey;

    @BeforeEach
    void setUp() {
        apiKey = new Cookie("X-API-KEY", "test");
    }

    @Test
    void testGetProfileUnauthorized() throws Exception {
        when(userService.get(any(User.class)))
                .thenReturn(new UserResponse());

        mockMvc.perform(
                get("/api/users/me")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });

        verify(userService, times(0)).get(any(User.class));
    }

    @Test
    void testGetProfileTokenInvalid() throws Exception {
        when(userService.get(any(User.class)))
                .thenReturn(new UserResponse());

        mockMvc.perform(
                get("/api/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });

        verify(userService, times(0)).get(any(User.class));
    }

    @Test
    void testGetProfileSuccess() throws Exception {
        UserResponse wantRes = new UserResponse(1L, "test", "test");

        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setName("test");
        user.setTokenExpiredAt(Instant.now().plus(Duration.ofDays(1L)).toEpochMilli());
        when(userRepository.findByToken(anyString())).thenReturn(Optional.of(user));

        when(userService.get(any(User.class)))
                .thenReturn(wantRes);

        mockMvc.perform(
                get("/api/users/me")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(userService, times(1)).get(any(User.class));
    }
}