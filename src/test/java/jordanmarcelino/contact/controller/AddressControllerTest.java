package jordanmarcelino.contact.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.validation.ConstraintViolationException;
import jordanmarcelino.contact.dto.AddressResponse;
import jordanmarcelino.contact.dto.CreateAddressRequest;
import jordanmarcelino.contact.dto.CreateContactRequest;
import jordanmarcelino.contact.dto.WebResponse;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.repository.AddressRepository;
import jordanmarcelino.contact.repository.UserRepository;
import jordanmarcelino.contact.service.AddressService;
import jordanmarcelino.contact.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrdererContext;
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

import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@AutoConfigureMockMvc
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AddressService addressService;

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
    void testCreateAddressUnauthorized() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCity("Indonesia");
        request.setProvince("Jakarta");
        request.setCountry("Indonesia");
        request.setStreet("Jalan Karet Pedurenan");
        request.setPostalCode("12345");

        mockMvc.perform(
                post("/api/contacts/1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {}
            ) ;

            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testCreateAddressBadRequest() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCity("");
        request.setProvince("");
        request.setCountry("");
        request.setStreet("");
        request.setPostalCode("");

        when(addressService.save(any(CreateAddressRequest.class)))
                .thenThrow(ConstraintViolationException.class);

        mockMvc.perform(
                post("/api/contacts/1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {}
            ) ;

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(addressService, times(1)).save(any(CreateAddressRequest.class));
    }

    @Test
    void testCreateAddressSuccess() throws Exception {
        CreateAddressRequest request = new CreateAddressRequest();
        request.setCity("Indonesia");
        request.setProvince("Jakarta");
        request.setCountry("Indonesia");
        request.setStreet("Jalan Karet Pedurenan");
        request.setPostalCode("12345");

        AddressResponse wantRes = new AddressResponse();
        wantRes.setId(1L);
        wantRes.setCity(request.getCity());
        wantRes.setProvince(request.getProvince());
        wantRes.setCountry(request.getCountry());
        wantRes.setStreet(request.getStreet());
        wantRes.setPostalCode(request.getPostalCode());

        when(addressService.save(any(CreateAddressRequest.class)))
                .thenReturn(wantRes);

        mockMvc.perform(
                post("/api/contacts/1/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {}
            ) ;

            assertEquals(Message.SUCCESS, response.getMessage());
            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(addressService, times(1)).save(any(CreateAddressRequest.class));
    }
}