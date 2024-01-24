package com.zeyad.securefileaccess;

import com.zeyad.securefileaccess.dao.FileDAO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SecureFileAccessApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureFileAccessApplication.class, args);
	}
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	@Bean
	CommandLineRunner commandLineRunner(FileDAO repo){
		return args -> {
			repo.getAllFilesForUser("d8173183-0c24-4f62-b1ae-6eb635c2440c", 1);
		};
	}
}
