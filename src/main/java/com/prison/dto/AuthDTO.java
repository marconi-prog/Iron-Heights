package com.prison.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class AuthDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {

        @NotBlank
        private String username;

        @NotBlank
        private String password;

        @NotBlank
        private String nomeCompleto;

        @NotBlank
        private String email;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LoginResponse {
        private String token;
        private String tipo;
        private String username;
        private String role;
        private Long expiracaoMs;
    }
}