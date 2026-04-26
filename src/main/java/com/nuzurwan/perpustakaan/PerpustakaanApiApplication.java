package com.nuzurwan.perpustakaan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // WAJIB: Tanpa ini, @Scheduled tidak akan jalan
public class PerpustakaanApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PerpustakaanApiApplication.class, args);
    }

}
