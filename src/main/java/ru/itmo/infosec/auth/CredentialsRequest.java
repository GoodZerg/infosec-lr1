package ru.itmo.infosec.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CredentialsRequest(
    @NotBlank
    @Size(min = 3, max = 32)
    @Pattern(regexp = "[A-Za-z0-9_.-]+", message = "допустимы латинские буквы, цифры и символы _.-")
    String username,
    @NotBlank
    @Size(min = 12, max = 72)
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
        message = "пароль должен содержать строчную и заглавную буквы, цифру и специальный символ"
    )
    String password
) {
}

