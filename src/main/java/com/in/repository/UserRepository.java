package com.in.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.in.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    long countByRole(String role);

}