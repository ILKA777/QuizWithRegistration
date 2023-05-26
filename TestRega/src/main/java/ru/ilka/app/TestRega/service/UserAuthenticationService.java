package ru.ilka.app.TestRega.service;

import org.springframework.stereotype.Service;
import ru.ilka.app.TestRega.entity.User;

@Service
public class UserAuthenticationService {

    private final UserService userService;

    public UserAuthenticationService(UserService userService) {
        this.userService = userService;
    }

    public User authenticateUser(String email, String password) {
        User userDto = userService.findUserByEmail(email);

        if (userDto != null && userDto.getPassword().equals(password)) {
            return userDto;
        }

        return null;
    }
}
