package com.taj.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonPropertyOrder({"status", "message", "timestamp"})
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
