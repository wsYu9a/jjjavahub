package com.wsyu9a.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class LoginResponseDTO {
    private String token;
    private String role;
    private String username;
} 