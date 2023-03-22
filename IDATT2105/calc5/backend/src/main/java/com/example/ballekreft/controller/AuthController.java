package com.example.ballekreft.controller;

import com.example.ballekreft.dto.AuthenticationRequest;
import com.example.ballekreft.dto.AuthenticationResponse;
import com.example.ballekreft.dto.RegisterRequest;
import com.example.ballekreft.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authenticationService;

    private static final Logger LOG = LoggerFactory.getLogger(ExprController.class);

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        try {
            return ResponseEntity.ok(authenticationService.register(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) throws Exception {
        LOG.info("Received: " + request);
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

}
