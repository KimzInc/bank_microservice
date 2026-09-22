package com.bankapp.employeeservice.client;

import com.bankapp.employeeservice.exception.RoleAssignmentException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Set;

/**
 * The actual service-to-service call: employee-service asking auth-service to
 * grant a role. "http://auth-service/..." is NOT a real hostname - the
 * @LoadBalanced RestTemplate (see RestTemplateConfig) intercepts it and asks
 * Eureka to resolve "auth-service" to a real instance address before the
 * request goes out. This is genuinely different from routing through the API
 * Gateway: the Gateway handles traffic FROM external clients INTO a service;
 * this is one internal service calling another directly.
 *
 * The caller's own bearer token is forwarded as-is, not replaced with some
 * separate service credential - auth-service re-validates it independently
 * (same as it would for any other request) and checks ROLE_ADMIN on whoever
 * it belongs to. If that token doesn't carry ROLE_ADMIN, auth-service returns
 * 403 and this call fails accordingly - the admin check happens THERE, not here.
 */
@Component
public class AuthServiceClient {

    private final RestTemplate restTemplate;

    public AuthServiceClient(RestTemplate loadBalancedRestTemplate) {
        this.restTemplate = loadBalancedRestTemplate;
    }

    @SuppressWarnings("unchecked")
    public Set<String> assignRole(String username, String role, String bearerToken) {
        String url = "http://auth-service/api/auth/internal/users/{username}/roles";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, bearerToken);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(Map.of("role", role), headers);

        try {
            var response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map.class, username);
            Map<String, Object> body = response.getBody();
            if (body == null || !(body.get("roles") instanceof java.util.List<?> roles)) {
                throw new RoleAssignmentException("auth-service returned an unexpected response");
            }
            return roles.stream().map(String::valueOf).collect(java.util.stream.Collectors.toSet());
        } catch (RestClientResponseException ex) {
            // auth-service reached and responded, but rejected the request - surface why,
            // rather than a generic failure (e.g. 404 unknown username, 403 caller isn't
            // ROLE_ADMIN, 400 invalid role name).
            throw new RoleAssignmentException(
                    "auth-service rejected the role assignment (HTTP " + ex.getStatusCode().value() + "): "
                            + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            // auth-service unreachable entirely (down, not registered, network issue).
            throw new RoleAssignmentException("Could not reach auth-service to assign the role", ex);
        }
    }
}
