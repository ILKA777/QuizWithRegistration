package ru.ilka.app.TestRega.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ilka.app.TestRega.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}