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
//	@Bean
//	CommandLineRunner commandLineRunner(FileDAO repo){
//		return args -> {
//			var x = repo.deleteFileEntity(1);
//			System.out.println(x);
//		};
//	}
}
