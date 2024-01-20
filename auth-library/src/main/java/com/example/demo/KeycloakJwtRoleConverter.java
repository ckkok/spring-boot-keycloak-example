package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class KeycloakJwtRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String RESOURCE_IDENTIFIER_KEY = "resource_access";

    private static final String AUTHORITY_PREFIX = "ROLE_";

    private final String resourceIdentifier;

    @Override
    public Collection<GrantedAuthority> convert(Jwt token) {
        Map<String, Map<String, Collection<String>>> resourceAccess = token.getClaim(RESOURCE_IDENTIFIER_KEY);
        if (resourceAccess == null) {
            return Collections.emptySet();
        }
        Collection<GrantedAuthority> mappedRoles = new LinkedHashSet<>();
        for (Map.Entry<String, Map<String, Collection<String>>> entry : resourceAccess.entrySet()) {
            String resource = entry.getKey();
            if (!resourceIdentifier.equals(resource)) continue;
            Map<String, Collection<String>> resourceClaims = entry.getValue();
            if (resourceClaims == null || resourceClaims.isEmpty()) continue;
            Collection<String> roles = resourceClaims.get("roles");
            if (roles == null || roles.isEmpty()) continue;
            if (log.isDebugEnabled()) {
                log.debug("Resource {} :: Roles = {}", resource, roles);
            }
            for (String role : roles) {
                mappedRoles.add(new SimpleGrantedAuthority(AUTHORITY_PREFIX + role));
            }
        }
        return mappedRoles;
    }
}
