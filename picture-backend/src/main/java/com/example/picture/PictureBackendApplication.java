package com.example.picture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PictureBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PictureBackendApplication.class, args);
	}

}
