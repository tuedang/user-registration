package com.userregistration.geo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class IpGeolocationClient {

    private static final String FIELDS = "status,message,countryCode,city";

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public IpGeolocationClient(RestTemplate restTemplate, @Value("${ip-api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public IpApiResponse lookup(String ipAddress) {
        URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/json/{query}")
                .queryParam("fields", FIELDS)
                .buildAndExpand(ipAddress)
                .encode()
                .toUri();
        try {
            IpApiResponse body = restTemplate.getForObject(uri, IpApiResponse.class);
            if (body == null) {
                return new IpApiResponse("fail", "empty response", null, null);
            }
            return body;
        } catch (RestClientException ex) {
            return new IpApiResponse("fail", "request failed", null, null);
        }
    }
}
