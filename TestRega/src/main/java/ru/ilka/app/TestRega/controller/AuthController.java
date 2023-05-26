package ru.ilka.app.TestRega.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.ilka.app.TestRega.dto.UserDto;
import ru.ilka.app.TestRega.entity.User;
import ru.ilka.app.TestRega.service.UserService;
import org.springframework.http.ResponseEntity;


import java.util.List;

@Controller
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final RestTemplate restTemplate;

    @Autowired
    public AuthController(UserService userService, RestTemplate restTemplate) {
        this.userService = userService;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/index")
    public String home() {
        logger.info("Запрос главной страницы");
        return "index";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        logger.info("Запрос формы регистрации");
        UserDto user = new UserDto();
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserDto userDto,
                               BindingResult result,
                               Model model) {
        logger.info("Получен запрос на регистрацию");

        User existingUser = userService.findUserByEmail(userDto.getEmail());

        if (existingUser != null && existingUser.getEmail() != null && !existingUser.getEmail().isEmpty()) {
            result.rejectValue("email",
                    null, "Учетная запись с таким электронным адресом уже зарегистрирована");
        }

        if (result.hasErrors()) {
            logger.warn("Ошибка введенных данных");
            model.addAttribute("user", userDto);
            return "register";
        }

        userService.saveUser(userDto);
        logger.info("Регистрация завершена успешно");

        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String users(Model model) {
        logger.info("Запрос страница пользователей");
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/login")
    public String login(Model model) {
        logger.info("Запрос страницы входа");
        model.addAttribute("user", new UserDto());
        return "login";
    }

    @PostMapping("/login")
    public String authenticateUser(@ModelAttribute("user") UserDto userDto, RedirectAttributes redirectAttributes) {
        logger.info("Запрос на аутентификацию пользователя");

        UserDto authenticatedUser = userService.authenticateUser(userDto.getEmail(), userDto.getPassword());

        if (authenticatedUser == null) {
            logger.warn("Неверный адрес электронной почты или пароль");
            redirectAttributes.addFlashAttribute("error", "Неверный адрес электронной почты или пароль");
            return "redirect:/login";
        }

        logger.info("Пользователь успешно аутентифицирован");
        return "redirect:/startGame";
    }

    @GetMapping("/startGame")
    public String startGame(Model model) {
        logger.info("Запрос страницы начала игры");

        String url = "http://jservice.io/api/random";
        ResponseEntity<JsonNode> responseEntity = restTemplate.getForEntity(url, JsonNode.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            JsonNode question = responseEntity.getBody();
            if (question != null) {
                model.addAttribute("question", question);
                return "game";
            }
        }

        logger.error("Не удалось получить вопрос от API");
        return "error";
    }
}
