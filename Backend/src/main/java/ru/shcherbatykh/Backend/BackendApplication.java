package ru.shcherbatykh.Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BackendApplication.class, args);
//		UserService userService = context.getBean(UserService.class);
//		userService.addUser(new User(2L, "Ренат", "Фарахутдинов", "Абуханович", "renat@mail.ru", "ren"));
	}

}
