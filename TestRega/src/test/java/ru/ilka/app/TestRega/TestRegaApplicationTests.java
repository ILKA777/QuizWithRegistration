package ru.ilka.app.TestRega;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import ru.ilka.app.TestRega.controller.AuthController;
import ru.ilka.app.TestRega.dto.UserDto;
import ru.ilka.app.TestRega.service.UserService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest

class TestRegaApplicationTests {
	@Mock
	private UserService userService;
	@Mock
	private RestTemplate restTemplate;


	// Тест проверяет, что метод home() возвращает "index".
	@Test
	public void testHome() {
		AuthController controller = new AuthController(userService, restTemplate);
		String viewName = controller.home();
		assertEquals("index", viewName);
	}

	// Тест проверяет, что метод showRegistrationForm()
	// возвращает "register" и что модель содержит атрибут "user",
	// а также проверяет, что значение атрибута "user" не равно null.
	@Test
	public void testShowRegistrationForm() {
		AuthController controller = new AuthController(userService, restTemplate);
		Model model = new ExtendedModelMap();
		String viewName = controller.showRegistrationForm(model);
		assertEquals("register", viewName);
		assertTrue(model.containsAttribute("user"));
		UserDto userDto = (UserDto) model.getAttribute("user");
		assertNotNull(userDto);
	}


	// Тест проверяет, что метод registration()
	// при передаче корректных данных пользователя
	// переадрессовывает на "/register?success",
	// и что объект BindingResult не содержит ошибок,
	// а также saveUser() userService вызывается с переданным userDto.

	@Test
	public void testRegistration_ValidData() {
		AuthController controller = new AuthController(userService, restTemplate);
		UserDto userDto = new UserDto();
		userDto.setEmail("test@example.com");
		userDto.setPassword("password");

		BindingResult result = new MapBindingResult(Collections.emptyMap(), "");

		Model model = new ExtendedModelMap();

		String viewName = controller.registration(userDto, result, model);

		assertEquals("redirect:/register?success", viewName);
		assertFalse(result.hasErrors());
		verify(userService).saveUser(userDto);
	}


	// Тест проверяет, что метод authenticateUser()
	// при передаче правильных учетных данных пользователя
	// переадрессовывает на "/startGame"
	// и что redirectAttributes не содержит атрибут "error".
	@Test
	public void testAuthenticateUser_ValidCredentials() {
		AuthController controller = new AuthController(userService, restTemplate);
		UserDto userDto = new UserDto();
		userDto.setEmail("test@example.com");
		userDto.setPassword("password");

		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

		when(userService.authenticateUser(userDto.getEmail(), userDto.getPassword())).thenReturn(userDto);

		String viewName = controller.authenticateUser(userDto, redirectAttributes);

		assertEquals("redirect:/startGame", viewName);
		assertFalse(redirectAttributes.containsAttribute("error"));
	}


	@Test
	public void testAuthenticateUser_InvalidCredentials() {
		AuthController controller = new AuthController(userService, restTemplate);
		UserDto userDto = new UserDto();
		userDto.setEmail("test@example.ru");
		userDto.setPassword("password");

		RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

		when(userService.authenticateUser(userDto.getEmail(), userDto.getPassword())).thenReturn(null);

		String viewName = controller.authenticateUser(userDto, redirectAttributes);

		assertEquals("redirect:/login", viewName);
		assertNotNull(redirectAttributes.getFlashAttributes().get("error"));
	}



}
