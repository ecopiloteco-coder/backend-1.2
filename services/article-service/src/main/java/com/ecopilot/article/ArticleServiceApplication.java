package com.ecopilot.article;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.boot.context.ApplicationPidFileWriter;

@SpringBootApplication
@EnableDiscoveryClient
public class ArticleServiceApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ArticleServiceApplication.class);
		app.addListeners(new ApplicationPidFileWriter());
		app.run(args);
	}

	@Bean
	public org.springframework.boot.CommandLineRunner commandLineRunner(org.springframework.context.ApplicationContext ctx) {
		return args -> {
			System.out.println("Let's inspect the beans provided by Spring Boot:");
			String[] beanNames = ctx.getBeanDefinitionNames();
			java.util.Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				if (beanName.contains("Controller")) {
					System.out.println("BEAN: " + beanName);
				}
			}
		};
	}

	       // Removed duplicate CORS configuration. CORS is now configured in WebConfig.java only.
}
