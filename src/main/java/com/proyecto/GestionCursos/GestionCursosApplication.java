package com.proyecto.GestionCursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GestionCursosApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionCursosApplication.class, args);
	}

}
