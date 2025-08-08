package com.usman.auth.user_module_springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UserModuleSpringbootApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserModuleSpringbootApplication.class, args);
	}

}
