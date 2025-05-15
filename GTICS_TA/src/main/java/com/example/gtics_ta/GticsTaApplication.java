package com.example.gtics_ta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class GticsTaApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(GticsTaApplication.class, args);
		String[] beans = context.getBeanDefinitionNames();

		for (String bean : beans) {
			if (bean.toLowerCase().contains("usuario")) {
				System.out.println("🟢 BEAN CARGADO: " + bean);
			}
		}
	}
}
