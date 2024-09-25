package edu.duan.app.docs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DocsAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocsAppApplication.class, args);
    }

}
