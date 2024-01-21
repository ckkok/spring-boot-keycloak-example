package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
class ClientApplicationTests {

  @Autowired
  @Qualifier("OAuth2RestClient")
  private RestTemplate restTemplate;

	@Test
	void givenSpringSecurityOAuth2ClientClientIdentifierConfigurationThenOAuth2RestClientIsAvailable() {
    assertNotNull(restTemplate);
	}

}

