package com.example.ballekreft.controller;

import com.example.ballekreft.model.Expr;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin("*")
public class ExprController {

    private static final Logger LOG = LoggerFactory.getLogger(ExprController.class);

    @PostMapping("/calculate")
    @ResponseBody
    public ResponseEntity<String> calculate(@RequestBody Expr expr) {
        LOG.info("Received: " + expr);
        if (expr == null) {
            LOG.error("expr is null");
            return ResponseEntity.noContent().build();
        }

        String result = expr.eval();
        LOG.info("Sending: " + result);
        return ResponseEntity.ok(result);
    }

}
