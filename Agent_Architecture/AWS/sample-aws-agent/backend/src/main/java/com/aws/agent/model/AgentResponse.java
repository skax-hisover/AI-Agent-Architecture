package com.aws.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentResponse {
    private String response;
    private String sessionId;
    private List<String> citations;
    private Map<String, Object> metadata;
    private String toolUsed;
}
