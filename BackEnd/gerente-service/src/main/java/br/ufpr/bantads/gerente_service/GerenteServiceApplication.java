package br.ufpr.bantads.gerente_service;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableRabbit
@SpringBootApplication
public class GerenteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GerenteServiceApplication.class, args);
	}

}
