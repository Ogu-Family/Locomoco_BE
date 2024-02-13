package org.prgms.locomocoserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LocomocoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocomocoServerApplication.class, args);
    }

}
