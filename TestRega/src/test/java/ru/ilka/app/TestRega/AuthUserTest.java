package ru.ilka.app.TestRega;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import ru.ilka.app.TestRega.controller.AuthController;
import ru.ilka.app.TestRega.dto.UserDto;
import ru.ilka.app.TestRega.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AuthUserTest {
    @Mock
    private UserService userService;
    @Mock
    private RestTemplate restTemplate;

    // Тест проверяет, что метод authenticateUser()
    // при передаче неправильных данных пользователя
    // переадрессовывает на "/login" и что
    // redirectAttributes содержит атрибут "error".
    @Test
    public void testAuthenticateUser_InvalidCredentials() {
        AuthController controller = new AuthController(userService, restTemplate);
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.ru");
        userDto.setPassword("password");

        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap();

        when(userService.authenticateUser(userDto.getEmail(), userDto.getPassword())).thenReturn(null);

        String viewName = controller.authenticateUser(userDto, redirectAttributes);

        assertAll("Authentication",
                () -> assertEquals("redirect:/login", viewName),
                () -> assertTrue(redirectAttributes.getFlashAttributes().containsKey("error"))
        );
    }
}
