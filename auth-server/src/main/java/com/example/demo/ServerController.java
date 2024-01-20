package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.annotation.security.RolesAllowed;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ServerController {

  @GetMapping("/api")
  @RolesAllowed({"Test Role"})
  public String getString() {
    return "Hello from server!";
  }

}
