package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@AutoConfiguration
@EnableWebSecurity
@Slf4j
public class OAuth2RestClientAutoConfiguration {

    @Value("${spring.security.oauth2.client.client-identifier:#{null}}")
    private String registrationId;

    @Bean("OAuth2RestClient")
    @ConditionalOnProperty("spring.security.oauth2.client.client-identifier")
    public RestTemplate restTemplate(RestTemplateBuilder builder, OAuth2AuthorizedClientManager authorizedClientManager, ClientRegistrationRepository clientRegistrationRepository) {
        log.info("Configuring authenticated rest client with identifier: {}", registrationId);
        var clientRegistration = clientRegistrationRepository.findByRegistrationId(registrationId);
        return builder
                .additionalInterceptors(new OAuth2ClientCredentialsInterceptor(authorizedClientManager, clientRegistration))
                .setReadTimeout(Duration.ofSeconds(5L))
                .setConnectTimeout(Duration.ofSeconds(1L))
                .build();
    }

    @Bean
    @ConditionalOnProperty("spring.security.oauth2.client.client-identifier")
    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository, OAuth2AuthorizedClientService authorizedClientService) {
        var clientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials(b -> b.clockSkew(Duration.ofSeconds(60L)))
                .build();
        var clientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
        clientManager.setAuthorizedClientProvider(clientProvider);
        return clientManager;
    }
}
