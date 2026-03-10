package com.univvoting;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UnivVotingApplication {

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        dotenv.entries().forEach(e -> {
            if (e.getKey() != null && !e.getKey().isBlank()) {
                System.setProperty(e.getKey(), e.getValue());
            }
        });

        SpringApplication.run(UnivVotingApplication.class, args);
    }
}