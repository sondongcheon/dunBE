package com.dnfproject.root.controller;

import com.dnfproject.root.common.staticMethod.ApiRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello, Spring Boot!";
    }

    @GetMapping("/health")
    public Object health() {
        return ApiRequest.requestGetAPI();
    }
}
