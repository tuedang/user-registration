package com.userregistration.service;

import com.userregistration.dto.RegistrationRequest;
import com.userregistration.dto.RegistrationResponse;
import com.userregistration.geo.IpApiResponse;
import com.userregistration.geo.IpGeolocationClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrationService {

    private final IpGeolocationClient ipGeolocationClient;

    public RegistrationService(IpGeolocationClient ipGeolocationClient) {
        this.ipGeolocationClient = ipGeolocationClient;
    }

    public RegistrationResponse register(RegistrationRequest request) {
        IpApiResponse geo = ipGeolocationClient.lookup(request.ipAddress());
        if (!"success".equalsIgnoreCase(geo.status())) {
            String detail = geo.message() != null && !geo.message().isBlank()
                    ? geo.message()
                    : "Unable to resolve geolocation for the provided IP address.";
            throw new RegistrationRuleException("ipAddress", detail);
        }
        if (!"CA".equalsIgnoreCase(geo.countryCode())) {
            throw new RegistrationRuleException("ipAddress", "User is not eligible to register (IP is not in Canada).");
        }
        String city = geo.city() != null && !geo.city().isBlank() ? geo.city() : "Unknown";
        UUID userId = UUID.randomUUID();
        String message = String.format("Welcome, %s! Your city is %s.", request.username(), city);
        return new RegistrationResponse(userId.toString(), message);
    }
}
