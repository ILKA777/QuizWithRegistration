package ru.ilka.app.TestRega.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ilka.app.TestRega.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}