package jordanmarcelino.contact.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.validation.ConstraintViolationException;
import jordanmarcelino.contact.dto.*;
import jordanmarcelino.contact.entity.User;
import jordanmarcelino.contact.exception.NotFoundException;
import jordanmarcelino.contact.repository.AddressRepository;
import jordanmarcelino.contact.repository.UserRepository;
import jordanmarcelino.contact.service.AddressService;
import jordanmarcelino.contact.util.Message;
import org.hibernate.sql.Update;
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
import java.util.ArrayList;
import java.util.List;
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
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

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
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

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
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

            assertEquals(Message.SUCCESS, response.getMessage());
            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(addressService, times(1)).save(any(CreateAddressRequest.class));
    }

    @Test
    void testUpdateAddressUnauthorized() throws Exception {
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCity("Indonesia");
        request.setProvince("Jakarta");
        request.setCountry("Indonesia");
        request.setStreet("Jalan Karet Pedurenan");
        request.setPostalCode("12345");

        mockMvc.perform(
                put("/api/contacts/1/addresses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testUpdateAddressBadRequest() throws Exception {
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCity("");
        request.setProvince("");
        request.setCountry("");
        request.setStreet("");
        request.setPostalCode("");

        when(addressService.update(any(UpdateAddressRequest.class)))
                .thenThrow(ConstraintViolationException.class);

        mockMvc.perform(
                put("/api/contacts/1/addresses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNotNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(addressService, times(1)).update(any(UpdateAddressRequest.class));
    }

    @Test
    void testUpdateAddressNotFound() throws Exception {
        UpdateAddressRequest request = new UpdateAddressRequest();
        request.setCity("Indonesia");
        request.setProvince("Jakarta");
        request.setCountry("Indonesia");
        request.setStreet("Jalan Karet Pedurenan");
        request.setPostalCode("12345");

        when(addressService.update(any(UpdateAddressRequest.class)))
                .thenThrow(new NotFoundException());

        mockMvc.perform(
                put("/api/contacts/1/addresses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );


            assertNull(response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(addressService, times(1)).update(any(UpdateAddressRequest.class));
    }

    @Test
    void testUpdateAddressSuccess() throws Exception {
        UpdateAddressRequest request = new UpdateAddressRequest();
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

        when(addressService.update(any(UpdateAddressRequest.class)))
                .thenReturn(wantRes);

        mockMvc.perform(
                put("/api/contacts/1/addresses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

            assertEquals(Message.SUCCESS, response.getMessage());
            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(addressService, times(1)).update(any(UpdateAddressRequest.class));
    }

    @Test
    void testDeleteAddressUnauthorized() throws Exception {
        mockMvc.perform(
                delete("/api/contacts/1/addresses/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testDeleteAddressNotFound() throws Exception {
        doThrow(new NotFoundException()).when(addressService).delete(any(DeleteAddressRequest.class));

        mockMvc.perform(
                delete("/api/contacts/1/addresses/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testDeleteAddressSuccess() throws Exception {
        doNothing().when(addressService).delete(any(DeleteAddressRequest.class));

        mockMvc.perform(
                delete("/api/contacts/1/addresses/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

            assertEquals(Message.SUCCESS, response.getMessage());
            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetAddressUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/contacts/1/addresses/1")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testGetAddressNotFound() throws Exception {
        when(addressService.get(any(GetAddressRequest.class)))
                .thenThrow(new NotFoundException());

        mockMvc.perform(
                get("/api/contacts/1/addresses/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(addressService, times(1)).get(any(GetAddressRequest.class));
    }

    @Test
    void testGetAddressSuccess() throws Exception {
        AddressResponse wantRes = new AddressResponse();
        wantRes.setId(1L);
        wantRes.setCity("Indonesia");
        wantRes.setProvince("Jakarta");
        wantRes.setCountry("Indonesia");
        wantRes.setStreet("Jalan Karet Pedurenan");
        wantRes.setPostalCode("12345");

        when(addressService.get(any(GetAddressRequest.class)))
                .thenReturn(wantRes);

        mockMvc.perform(
                get("/api/contacts/1/addresses/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<AddressResponse> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<AddressResponse>>() {
                    }
            );

            assertEquals(Message.SUCCESS, response.getMessage());
            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(addressService, times(1)).get(any(GetAddressRequest.class));
    }

    @Test
    void testFindAllAddressUnauthorized() throws Exception {
        mockMvc.perform(
                get("/api/contacts/1/addresses")
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<List<AddressResponse>>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });
    }

    @Test
    void testFindAllAddressNotFound() throws Exception {
        when(addressService.findAll(any(GetAddressRequest.class)))
                .thenThrow(new NotFoundException());

        mockMvc.perform(
                get("/api/contacts/1/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<List<AddressResponse>>>() {
                    }
            );

            assertNull(response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(addressService, times(1)).findAll(any(GetAddressRequest.class));
    }

    @Test
    void testFindAllAddressSuccess() throws Exception {
        List<AddressResponse> wantRes = new ArrayList<>();
        AddressResponse address = new AddressResponse();
        address.setId(1L);
        address.setCity("Indonesia");
        address.setProvince("Jakarta");
        address.setCountry("Indonesia");
        address.setStreet("Jalan Karet Pedurenan");
        address.setPostalCode("12345");

        wantRes.add(address);

        when(addressService.findAll(any(GetAddressRequest.class)))
                .thenReturn(wantRes);

        mockMvc.perform(
                get("/api/contacts/1/addresses")
                        .accept(MediaType.APPLICATION_JSON)
                        .cookie(apiKey)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<List<AddressResponse>> response = objectMapper.readValue(
                    result.getResponse().getContentAsString(),
                    new TypeReference<WebResponse<List<AddressResponse>>>() {
                    }
            );

            assertEquals(Message.SUCCESS, response.getMessage());
            assertEquals(wantRes, response.getData());
            assertNull(response.getErrors());
        });

        verify(userRepository, times(1)).findByToken(anyString());
        verify(addressService, times(1)).findAll(any(GetAddressRequest.class));
    }
}


