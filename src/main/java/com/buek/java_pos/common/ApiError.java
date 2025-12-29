package com.buek.java_pos.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public record ApiError(
    @JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    String requestId
) {}
