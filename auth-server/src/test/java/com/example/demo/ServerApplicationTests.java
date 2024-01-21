package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
class ServerApplicationTests {

  @Autowired
  private ServerController controller;

	@Test
  @WithMockUser(roles = "Test Role")
	void givenCorrectRoleWhenGetStringThenReturnResponse() {
    String response = controller.getString();
    assertEquals("Hello from server!", response);
	}

  @Test
  @WithMockUser(roles = "Some Role")
  void givenIncorrectRoleWhenGetStringThenThrowAccessDeniedException() {
    var exception = assertThrows(AccessDeniedException.class, () -> controller.getString());
    assertEquals("Access Denied", exception.getMessage());
  }

  @Test
  void givenNoPrincipalWhenGetStringThenThrowAuthenticationCredentialsNotFoundException() {
    var exception = assertThrows(AuthenticationCredentialsNotFoundException.class, () -> controller.getString());
    assertEquals("An Authentication object was not found in the SecurityContext", exception.getMessage());
  }
}

