package com.in.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kis student ne apply kiya
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Kis interview ke liye apply kiya
    @ManyToOne
    @JoinColumn(name = "interview_id")
    private Interview interview;

    private String status; // APPLIED, APPROVED, REJECTED
}