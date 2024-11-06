package jordanmarcelino.contact.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jordanmarcelino.contact.dto.UserRegisterRequest;
import jordanmarcelino.contact.dto.UserResponse;
import jordanmarcelino.contact.dto.WebResponse;
import jordanmarcelino.contact.exception.UserAlreadyRegisteredException;
import jordanmarcelino.contact.service.UserService;
import jordanmarcelino.contact.util.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void testRegisterSuccess() throws Exception {
        UserResponse wantRes = new UserResponse(1L, "test", "test");
        when(userService.register(any(UserRegisterRequest.class)))
                .thenReturn(wantRes);

        UserRegisterRequest request = new UserRegisterRequest("test", "test", "supersecret");
        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });

            assertEquals(Message.SUCCESS, response.getMessage());
            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });

        verify(userService, times(1)).register(request);
    }

    @Test
    void testRegisterBadRequest() throws Exception {
        when(userService.register(any(UserRegisterRequest.class)))
                .thenThrow(ConstraintViolationException.class);

        UserRegisterRequest request = new UserRegisterRequest("", "", "");
        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<Object> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });

            assertEquals(Message.BAD_REQUEST, response.getMessage());
            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });

        verify(userService, times(1)).register(request);
    }

    @Test
    void testRegisterAlreadyRegistered() throws Exception {
        when(userService.register(any(UserRegisterRequest.class)))
                .thenThrow(new UserAlreadyRegisteredException());

        UserRegisterRequest request = new UserRegisterRequest("test", "test", "supersecret");
        mockMvc.perform(
                post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<Object> response = objectMapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<>() {
                    });

            assertNotNull(response.getMessage());
            assertNull(response.getData());
            assertNull(response.getErrors());
        });

        verify(userService, times(1)).register(request);
    }
}