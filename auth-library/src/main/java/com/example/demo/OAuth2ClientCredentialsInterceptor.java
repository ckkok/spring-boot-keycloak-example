package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class OAuth2ClientCredentialsInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final ClientRegistration clientRegistration;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("Authorizing request for client id: {}", clientRegistration.getClientId());
        }
        var authRequest = OAuth2AuthorizeRequest.withClientRegistrationId(clientRegistration.getRegistrationId())
                .principal(clientRegistration.getClientId())
                .build();
        var authorizedClient = authorizedClientManager.authorize(authRequest);
        if (authorizedClient == null) {
            throw new IllegalStateException("authorizedClient is null");
        }
        var accessToken = authorizedClient.getAccessToken();
        if (log.isDebugEnabled()) {
            log.debug("Access token: {}", accessToken.getTokenValue());
        }
        request.getHeaders().setBearerAuth(accessToken.getTokenValue());
        return execution.execute(request, body);
    }
}
