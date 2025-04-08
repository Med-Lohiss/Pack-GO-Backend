package com.packandgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PackAndGoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PackAndGoBackendApplication.class, args);
	}

}
