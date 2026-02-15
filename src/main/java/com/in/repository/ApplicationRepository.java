package com.in.repository;

import com.in.model.Application;
import com.in.model.User;
import com.in.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Kisi specific user ke applications
    List<Application> findByUser(User user);

    // Kisi specific interview ke applications
    List<Application> findByInterview(Interview interview);

    boolean existsByUserAndInterview(User user, Interview interview);
    long countByStatus(String status);
    long countByUser(User user);
    long countByUserAndStatus(User user, String status);
}