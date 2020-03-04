package com.github.lucbui.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.github.lucbui")
public class CalendarFunApplication {
	public static void main(String[] args) {
		SpringApplication.run(CalendarFunApplication.class, args);
	}
}
