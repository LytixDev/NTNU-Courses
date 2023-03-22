package com.example.ballekreft.controller;

import com.example.ballekreft.model._User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin("*")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(ExprController.class);

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> calculate(@RequestBody _User user) {
        LOG.info("Received: " + user);
        if (user == null) {
            LOG.error("expr is null");
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok("");
    }

}
