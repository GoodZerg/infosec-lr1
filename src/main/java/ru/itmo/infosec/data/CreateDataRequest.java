package ru.itmo.infosec.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDataRequest(
    @NotBlank @Size(max = 80) String title,
    @NotBlank @Size(max = 500) String content
) {
}

