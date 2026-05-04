package com.userregistration.geo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class IpGeolocationClientTest {

    private MockRestServiceServer mockServer;
    private RestTemplate restTemplate;
    private IpGeolocationClient client;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        client = new IpGeolocationClient(restTemplate, "http://localhost");
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    void lookup_shouldReturnSuccess_whenApiReturnsSuccess() {
        String ip = "99.0.0.1";
        URI expectedUri =
                UriComponentsBuilder.fromUriString("http://localhost")
                        .path("/json/{query}")
                        .queryParam("fields", "status,message,countryCode,city")
                        .buildAndExpand(ip)
                        .encode()
                        .toUri();

        mockServer.expect(requestTo(expectedUri)).andRespond(
                withSuccess(
                        """
                                {"status":"success","message":"","countryCode":"CA","city":"Ottawa"}
                                """,
                        MediaType.APPLICATION_JSON));

        IpApiResponse response = client.lookup(ip);

        assertThat(response.status()).isEqualTo("success");
        assertThat(response.countryCode()).isEqualTo("CA");
        assertThat(response.city()).isEqualTo("Ottawa");
    }

    @Test
    void lookup_shouldReturnFail_whenApiReturnsFail() {
        String ip = "10.0.0.1";
        URI expectedUri =
                UriComponentsBuilder.fromUriString("http://localhost")
                        .path("/json/{query}")
                        .queryParam("fields", "status,message,countryCode,city")
                        .buildAndExpand(ip)
                        .encode()
                        .toUri();

        mockServer.expect(requestTo(expectedUri)).andRespond(
                withSuccess(
                        """
                                {"status":"fail","message":"private range"}
                                """,
                        MediaType.APPLICATION_JSON));

        IpApiResponse response = client.lookup(ip);

        assertThat(response.status()).isEqualTo("fail");
        assertThat(response.message()).contains("private");
    }
}
