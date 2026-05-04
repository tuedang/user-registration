package com.userregistration.service;

import com.userregistration.dto.RegistrationRequest;
import com.userregistration.dto.RegistrationResponse;
import com.userregistration.geo.IpApiResponse;
import com.userregistration.geo.IpGeolocationClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private IpGeolocationClient ipGeolocationClient;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void register_shouldReturnUserIdAndMessage_whenIpIsCanadian() {
        when(ipGeolocationClient.lookup("1.1.1.1"))
                .thenReturn(new IpApiResponse("success", null, "CA", "Montreal"));

        RegistrationRequest request =
                new RegistrationRequest("bob", "Validpass9", "1.1.1.1");

        RegistrationResponse response = registrationService.register(request);

        assertThat(response.userId()).isNotBlank();
        assertThat(response.message())
                .contains("bob")
                .contains("Montreal")
                .startsWith("Welcome, bob!");
    }

    @Test
    void register_shouldThrow_whenIpIsNotInCanada() {
        when(ipGeolocationClient.lookup("8.8.8.8"))
                .thenReturn(new IpApiResponse("success", null, "US", "Mountain View"));

        RegistrationRequest request =
                new RegistrationRequest("bob", "Validpass9", "8.8.8.8");

        RegistrationRuleException ex =
                catchThrowableOfType(() -> registrationService.register(request), RegistrationRuleException.class);
        assertThat(ex).isNotNull();
        assertThat(ex.getField()).isEqualTo("ipAddress");
        assertThat(ex.getMessage()).contains("not eligible");
    }

    @Test
    void register_shouldThrow_whenGeoLookupFails() {
        when(ipGeolocationClient.lookup("0.0.0.0"))
                .thenReturn(new IpApiResponse("fail", "invalid query", null, null));

        RegistrationRequest request =
                new RegistrationRequest("bob", "Validpass9", "0.0.0.0");

        RegistrationRuleException ex =
                catchThrowableOfType(() -> registrationService.register(request), RegistrationRuleException.class);
        assertThat(ex).isNotNull();
        assertThat(ex.getField()).isEqualTo("ipAddress");
        assertThat(ex.getMessage()).contains("invalid query");
    }
}
