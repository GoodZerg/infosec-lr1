package ru.itmo.infosec.data;

import java.time.Instant;

public record DataResponse(Long id, String title, String content, Instant createdAt) {
}

