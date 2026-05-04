package com.userregistration.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userregistration.dto.RegistrationRequest;
import com.userregistration.dto.RegistrationResponse;
import com.userregistration.error.ErrorResponse;
import com.userregistration.exception.GlobalExceptionHandler;
import com.userregistration.service.RegistrationRuleException;
import com.userregistration.service.RegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegistrationController.class)
@Import(GlobalExceptionHandler.class)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrationService registrationService;

    @Test
    void register_shouldReturnOk_whenRequestIsValid() throws Exception {
        when(registrationService.register(any(RegistrationRequest.class)))
                .thenReturn(new RegistrationResponse("uid-1", "Welcome, alice! Your city is Toronto."));

        MvcResult result = mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username":"alice",
                                            "password":"Secretpass1",
                                            "ipAddress":"99.0.0.1"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andReturn();

        RegistrationResponse body =
                objectMapper.readValue(result.getResponse().getContentAsString(), RegistrationResponse.class);
        assertThat(body.userId()).isEqualTo("uid-1");
        assertThat(body.message()).contains("alice").contains("Toronto");
        verify(registrationService).register(any(RegistrationRequest.class));
    }

    @Test
    void register_shouldReturnBadRequest_whenUsernameIsBlank() throws Exception {
        mockMvc.perform(
                post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"  ","password":"Secretpass1","ipAddress":"99.0.0.1"}
                                """)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturnBadRequest_whenPasswordTooShort() throws Exception {
        mockMvc.perform(
                post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"alice","password":"Short1A","ipAddress":"99.0.0.1"}
                                """)
        ).andExpect(
                status().isBadRequest());
    }

    @Test
    void register_shouldReturnBadRequest_whenNotEligible() throws Exception {
        when(registrationService.register(any(RegistrationRequest.class)))
                .thenThrow(new RegistrationRuleException("ipAddress", "User is not eligible to register (IP is not in Canada)."));

        MvcResult result = mockMvc.perform(
                        post("/api/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {"username":"alice","password":"Secretpass1","ipAddress":"8.8.8.8"}
                                        """))
                .andExpect(status().isBadRequest())
                .andReturn();

        ErrorResponse err = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponse.class);
        assertThat(err.errors()).hasSize(1);
        assertThat(err.errors().getFirst().field()).isEqualTo("ipAddress");
        assertThat(err.errors().getFirst().message()).contains("not eligible");
    }
}
