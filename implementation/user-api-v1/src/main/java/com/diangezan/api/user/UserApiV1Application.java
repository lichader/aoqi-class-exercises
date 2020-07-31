package com.diangezan.api.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class UserApiV1Application {

	public static void main(String[] args) {
		SpringApplication.run(UserApiV1Application.class, args);
	}

	@Bean
	public RestTemplate restTemplateBean(){
		return new RestTemplate();
	}
}
