package ru.ilka.app.TestRega.service;

import ru.ilka.app.TestRega.dto.UserDto;
import ru.ilka.app.TestRega.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();

    UserDto authenticateUser(String email, String password);
}