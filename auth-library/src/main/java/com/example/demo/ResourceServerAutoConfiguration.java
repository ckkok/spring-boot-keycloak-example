package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;

@Slf4j
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@AutoConfiguration
public class ResourceServerAutoConfiguration {

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(@Value("${spring.security.oauth2.resourceserver.jwt.resource-identifier:#{null}}") String resourceIdentifier) {
        if (resourceIdentifier == null) {
            log.info("No resource identifier configured. Only scopes will be extracted from JWTs.");
        } else {
            log.info("Resource identifier for Keycloak JWTs: {}", resourceIdentifier);
        }
        Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = resourceIdentifier == null ?
                new JwtGrantedAuthoritiesConverter() :
                new DelegatingJwtGrantedAuthoritiesConverter(
                        new JwtGrantedAuthoritiesConverter(),
                        new KeycloakJwtRoleConverter(resourceIdentifier)
                );
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

}