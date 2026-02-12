package com.abhinav.expense_tracker.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ActivityDTO {
    private Long id;
    private String type;
    private String message;

    private Long userId;
    private String userName;

    private Long entityId;
    private String changeDetails;

    private LocalDateTime timestamp;
}
