package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class ClientController {

  @Autowired
  @Qualifier("OAuth2RestClient")
  private RestTemplate restTemplate;

  @GetMapping("/api")
  public String getResult() {
    return restTemplate.getForObject("http://localhost:9190/api", String.class);
  }

}
