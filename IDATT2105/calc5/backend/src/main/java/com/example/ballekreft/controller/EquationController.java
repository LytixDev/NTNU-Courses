package com.example.ballekreft.controller;

import com.example.ballekreft.model.Equation;
import com.example.ballekreft.model._User;
import com.example.ballekreft.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.ballekreft.service.JwtService;

import java.util.List;

@RestController
@CrossOrigin("*")
public class EquationController {

    private static final Logger LOG = LoggerFactory.getLogger(ExprController.class);

    private final UserRepository userRepository;

    private final JwtService jwtService;

    public EquationController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @GetMapping(value = "/fetch-calculations")
    @ResponseBody
    public ResponseEntity<List<Equation>> calculate(HttpServletRequest request) {
        LOG.info("fetch calculations request");
        _User user = userRepository.findByUsername(jwtService.extractUsername(request.getHeader("Authorization").substring(7))).orElseThrow();
        return ResponseEntity.ok(user.getEquations());
    }

}
