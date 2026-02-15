package com.in.repository;

import com.in.model.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    Page<Interview> findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(
            String title,
            String company,
            Pageable pageable
    );
    long countByInterviewDateAfter(LocalDate date);

}