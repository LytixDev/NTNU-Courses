package com.example.ballekreft.controller;

import com.example.ballekreft.model.Equation;
import com.example.ballekreft.model.Expr;
import com.example.ballekreft.model._User;
import com.example.ballekreft.repository.UserRepository;
import com.example.ballekreft.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class ExprController {

    private static final Logger LOG = LoggerFactory.getLogger(ExprController.class);

    private final UserRepository userRepository;

    private final JwtService jwtService;

    @PostMapping(value = "/calculate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> calculate(@RequestBody Expr expr, HttpServletRequest request) {
        LOG.info("Received:" + expr);
        if (expr == null) {
            LOG.error("expr is null");
            return ResponseEntity.noContent().build();
        }

        _User user = userRepository.findByUsername(jwtService.extractUsername(request.getHeader("Authorization").substring(7))).orElseThrow();
        Equation equation = new Equation();
        equation.addExpr(expr);
        user.addEquation(equation);
        userRepository.save(user);

        String result = expr.eval();
        LOG.info("Sending: " + result);
        return ResponseEntity.ok(result);
    }
}
