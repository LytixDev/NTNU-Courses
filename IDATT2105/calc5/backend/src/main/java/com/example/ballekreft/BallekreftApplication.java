package com.example.ballekreft;

import com.example.ballekreft.model.Role;
import com.example.ballekreft.model._User;
import com.example.ballekreft.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BallekreftApplication {

    // https://www.youtube.com/watch?v=KxqlJblhzfI 1:41:07
    public static void main(String[] args) {
        SpringApplication.run(BallekreftApplication.class, args);
    }


}
