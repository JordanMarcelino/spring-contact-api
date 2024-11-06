package jordanmarcelino.contact.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.validation.ConstraintViolationException;
import jordanmarcelino.contact.dto.UpdateUserRequest;
import jordanmarcelino.contact.dto.UserResponse;
import jordanmarcelino.contact.dto.WebResponse;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.repository.UserRepository;
import jordanmarcelino.contact.service.UserService;
import jordanmarcelino.contact.util.Message;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void testUpdateProfileUnauthorized() throws Exception {
        when(userService.update(any(User.class), any(UpdateUserRequest.class)))
                .thenReturn(new UserResponse());

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("test");
        request.setPassword("supersecret");
        mockMvc.perform(
                put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
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

        verify(userService, times(0)).update(any(User.class), any(UpdateUserRequest.class));
    }

    @Test
    void testUpdateProfileBadRequest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setName("test");
        user.setTokenExpiredAt(Instant.now().plus(Duration.ofDays(1L)).toEpochMilli());

        when(userRepository.findByToken(anyString()))
                .thenReturn(Optional.of(user));
        when(userService.update(any(User.class), any(UpdateUserRequest.class)))
                .thenThrow(ConstraintViolationException.class);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("");
        request.setPassword("");
        mockMvc.perform(
                put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertEquals(Message.BAD_REQUEST, response.getMessage());
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(userService, times(1)).update(any(User.class), any(UpdateUserRequest.class));
    }

    @Test
    void testUpdateProfileSuccess() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setName("test");
        user.setTokenExpiredAt(Instant.now().plus(Duration.ofDays(1L)).toEpochMilli());

        UserResponse wantRes = new UserResponse(1L, "test", "new test");
        when(userRepository.findByToken(anyString()))
                .thenReturn(Optional.of(user));
        when(userService.update(any(User.class), any(UpdateUserRequest.class)))
                .thenReturn(wantRes);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("new test");
        request.setPassword("newsupersecret");
        mockMvc.perform(
                put("/api/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    }
            );

            assertEquals(Message.SUCCESS, response.getMessage());
            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(userService, times(1)).update(any(User.class), any(UpdateUserRequest.class));
    }
}