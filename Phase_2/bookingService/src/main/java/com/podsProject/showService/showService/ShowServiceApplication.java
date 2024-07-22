package com.podsProject.showService.showService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;

@SpringBootApplication
@EnableTransactionManagement
public class ShowServiceApplication {
	@Bean
	public RestTemplate restTemplat(){
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(ShowServiceApplication.class, args);
	}

}
