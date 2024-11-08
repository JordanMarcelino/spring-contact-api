package jordanmarcelino.contact.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.validation.ConstraintViolationException;
import jordanmarcelino.contact.dto.*;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.exception.NotFoundException;
import jordanmarcelino.contact.repository.UserRepository;
import jordanmarcelino.contact.service.ContactService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContactService contactService;

    @MockBean
    private UserRepository userRepository;

    private Cookie apiKey;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setName("test");
        user.setTokenExpiredAt(Instant.now().plus(Duration.ofDays(1L)).toEpochMilli());

        when(userRepository.findByToken(anyString()))
                .thenReturn(Optional.of(user));
        apiKey = new Cookie("X-API-KEY", "test");
    }

    @Test
    void testCreateContactUnauthorized() throws Exception {
        mockMvc.perform(
                post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testCreateContactFailedBadRequest() throws Exception {
        when(contactService.save(any(CreateContactRequest.class)))
                .thenThrow(ConstraintViolationException.class);

        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("");
        request.setPhone("");

        mockMvc.perform(
                post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
    }

    @Test
    void testCreateContactSuccess() throws Exception {
        CreateContactRequest request = new CreateContactRequest();
        request.setFirstName("test");
        request.setLastName("test");
        request.setEmail("test@gmail.com");
        request.setPhone("0123456789");
        ContactResponse wantRes = new ContactResponse(1L, request.getFirstName(), request.getLastName(), request.getEmail(), request.getPhone());
        when(contactService.save(any(CreateContactRequest.class)))
                .thenReturn(wantRes);
        mockMvc.perform(
                post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );
            assertEquals(Message.SUCCESS, response.getMessage());
            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });
        verify(userRepository, times(1)).findByToken(anyString());
        verify(contactService, times(1)).save(any(CreateContactRequest.class));
    }

    @Test
    void testGetContactUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/contacts/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetContactNotFound() throws Exception {
        when(contactService.get(any(GetContactRequest.class)))
                .thenThrow(new NotFoundException());

        mockMvc.perform(
                get("/api/contacts/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(contactService, times(1)).get(any(GetContactRequest.class));
    }

    @Test
    void testGetContactSuccess() throws Exception {
        ContactResponse wantRes = new ContactResponse();
        wantRes.setId(1L);
        wantRes.setFirstName("test");
        wantRes.setLastName("test");
        wantRes.setEmail("test@gmail.com");
        wantRes.setPhone("0123456789");

        when(contactService.get(any(GetContactRequest.class)))
                .thenReturn(wantRes);

        mockMvc.perform(
                get("/api/contacts/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertEquals(Message.SUCCESS, response.getMessage());
            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(contactService, times(1)).get(any(GetContactRequest.class));
    }

    @Test
    void testUpdateContactUnauthorized() throws Exception {
        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("test");
        request.setLastName("test");
        request.setEmail("test@gmail.com");
        request.setPhone("0123456789");

        mockMvc.perform(
                put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testUpdateContactBadRequest() throws Exception {
        when(contactService.update(any(UpdateContactRequest.class)))
                .thenThrow(ConstraintViolationException.class);

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setEmail("");
        request.setPhone("");

        mockMvc.perform(
                put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(contactService, times(1)).update(any(UpdateContactRequest.class));
    }

    @Test
    void testUpdateContactNotFound() throws Exception {
        when(contactService.update(any(UpdateContactRequest.class)))
                .thenThrow(new NotFoundException());

        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("test");
        request.setLastName("test");
        request.setEmail("test@gmail.com");
        request.setPhone("0123456789");

        mockMvc.perform(
                put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(contactService, times(1)).update(any(UpdateContactRequest.class));
    }

    @Test
    void testUpdateContactSuccess() throws Exception {
        UpdateContactRequest request = new UpdateContactRequest();
        request.setFirstName("test");
        request.setLastName("test");
        request.setEmail("test@gmail.com");
        request.setPhone("0123456789");

        ContactResponse wantRes = new ContactResponse();
        wantRes.setId(1L);
        wantRes.setFirstName(request.getFirstName());
        wantRes.setLastName(request.getLastName());
        wantRes.setEmail(request.getEmail());
        wantRes.setPhone(request.getPhone());

        when(contactService.update(any(UpdateContactRequest.class)))
                .thenReturn(wantRes);

        mockMvc.perform(
                put("/api/contacts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertEquals(Message.SUCCESS, response.getMessage());
            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(contactService, times(1)).update(any(UpdateContactRequest.class));
    }

    @Test
    void testDeleteContactUnauthorized() throws Exception {
        mockMvc.perform(
                delete("/api/contacts/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testDeleteContactNotFound() throws Exception {
        doThrow(new NotFoundException()).when(contactService).delete(any(DeleteContactRequest.class));

        mockMvc.perform(
                delete("/api/contacts/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(contactService, times(1)).delete(any(DeleteContactRequest.class));
    }

    @Test
    void testDeleteContactSuccess() throws Exception {
        doNothing().when(contactService).delete(any(DeleteContactRequest.class));

        mockMvc.perform(
                delete("/api/contacts/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<ContactResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<ContactResponse>>() {
                    }
            );

            assertEquals(Message.SUCCESS, response.getMessage());
            assertNull(response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(contactService, times(1)).delete(any(DeleteContactRequest.class));
    }
}