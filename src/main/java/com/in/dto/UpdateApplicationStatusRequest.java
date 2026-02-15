package com.in.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateApplicationStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;   // APPROVED or REJECTED
}