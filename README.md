# Demo for Spring Boot OAuth2 Keycloak Client and Resource Server AutoConfiguration and Enforcement

This repository demonstrates a minimal configuration for bootstrapping OAuth2 RestTemplate and JWT security using Keycloak as the identity provider. Much of the heavy lifting is done by Spring Boot's autoconfiguration, with only the following custom implementations.

1. A client credentials interceptor for injection into RestTemplates. This is not needed if the project uses Spring's WebClient instead of RestTemplate as WebClients can be configured with Spring's native ServerOAuth2AuthorizedClientExchangeFilterFunction for the same purpose.
2. A JWT converter to extract roles from Keycloak's access tokens. This is not needed if security is scope-based as Spring's default JWT converter already extracts scopes automatically.

As default behaviour, Spring Security validates the `iss`, `exp` and `nbf` claims. When the `spring.security.oauth2.resourceserver.jwt.audiences` property is configured, the `aud` claim is also validated against it.

By further making use of Spring Boot's AutoConfiguration infrastructure, these configurations become opt-out the moment the auth-library module is on the classpath, rather than requiring Import annotations. As examples, the client can be seen to exclude the resource server autoconfiguration (and vice versa).

## Running the Example

1. Bring up a Keycloak instance. A sample Docker compose.yaml file has been provided, e.g. `docker compose up -d`. Create a confidential client in Keycloak, i.e. one capable of using the client credentials flow from OAuth2. Make a note of its realm, client id, and client secret. You will also need the realm's OIDC configuration discovery url. Create a role for the client and associate it with the client, e.g. "Test Role". If you use a different role from "Test Role", update the @RolesAllowed annotation under the server's ServerController class accordingly.
2. Replace the sample values in the auth-client and auth-server modules' application.yml respectively.
3. Run `mvn clean package` in the project's root directory.
4. Run `java -jar auth-server/target/auth-server-1.0.0-SNAPSHOT.jar` to start the server
5. Run `java -jar auth-client/target/auth-client-1.0.0-SNAPSHOT.jar` to start the client

Observe the respective client and server configuration logs indicating that the necessary OAuth2 rest client and resource server configurations have been completed on startup.

Go to `http://localhost:9190/api` in your browser. This is the secured resource server and you should see a 401 Unauthorized response.

Instead, go to `http://localhost:9191/api` and observe the client requesting and validating the access token from Keycloak before calling the resource server and forwarding its response.

Shut down Keycloak and re-issue the request immediately. If the token is still valid, determined by its exp value, no further token validation request is made to Keycloak and the request succeeds. If token introspection is required, Spring Security can be configured to validate the tokens as opaque tokens, sending them to an introspection endpoint automatically.

Re-issue the request after the token expires - check via jwt.io, the default clock skew is 60 seconds, i.e. a token that expires in the next 60 seconds is considered expired. Observe that if Keycloak is unavailable, the request will fail.

## Things to Address Before Bringing to Production

- Failure to obtain an access token from Keycloak for any reason will throw an org.springframework.security.oauth2.client.ClientAuthorizationException. This should be wrapped in a relevant exception that applications can handle, perhaps via retries. For example, Spring's HttpClientException.
- Appropriate exception translation policies on the secured server

## References
- [Spring Security Documentation on Resource Server Configuration](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#_specifying_the_authorization_server)
