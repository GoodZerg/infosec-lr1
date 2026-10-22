package ru.itmo.infosec.error;

import java.time.Instant;
import java.util.Map;

public record ApiError(Instant timestamp, int status, String error, Map<String, String> details) {
    public ApiError {
        details = Map.copyOf(details);
    }
}
