package sk.eastcode.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UploaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploaderApplication.class, args);
	}
}
