package com.azure.agent.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AgentRequest {

    @NotBlank(message = "메시지는 필수입니다")
    private String message;

    private String sessionId;
}

