package com.aws.agent.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 도구(Tools) 서비스 - 실제 Action Groups 역할
 */
@Service
public class ToolService {
    
    /**
     * 사용 가능한 도구 목록
     */
    public enum Tool {
        CALCULATOR("calculator", "계산기 - 수학 연산 수행"),
        WEATHER("weather", "날씨 조회 - 현재 날씨 정보 제공"),
        TIME("time", "현재 시간 조회"),
        NONE("none", "도구 사용 없음");
        
        private final String name;
        private final String description;
        
        Tool(String name, String description) {
            this.name = name;
            this.description = description;
        }
        
        public String getName() {
            return name;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 메시지에서 사용할 도구 결정 (간단한 의도 파악)
     */
    public Tool determineTool(String message) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("계산") || lowerMessage.contains("더하기") || 
            lowerMessage.contains("빼기") || lowerMessage.contains("곱하기") || 
            lowerMessage.contains("나누기") || lowerMessage.matches(".*\\d+\\s*[+\\-*/]\\s*\\d+.*")) {
            return Tool.CALCULATOR;
        }
        
        if (lowerMessage.contains("날씨") || lowerMessage.contains("기온")) {
            return Tool.WEATHER;
        }
        
        if (lowerMessage.contains("시간") || lowerMessage.contains("몇 시")) {
            return Tool.TIME;
        }
        
        return Tool.NONE;
    }
    
    /**
     * 계산기 도구 실행
     */
    public Map<String, Object> executeCalculator(String message) {
        Map<String, Object> result = new HashMap<>();
        
        // 간단한 수식 추출 (예: "5 + 3", "10 - 2" 등)
        Pattern pattern = Pattern.compile("(\\d+)\\s*([+\\-*/])\\s*(\\d+)");
        Matcher matcher = pattern.matcher(message);
        
        if (matcher.find()) {
            try {
                double num1 = Double.parseDouble(matcher.group(1));
                String operator = matcher.group(2);
                double num2 = Double.parseDouble(matcher.group(3));
                double calculationResult = 0;
                
                switch (operator) {
                    case "+":
                        calculationResult = num1 + num2;
                        break;
                    case "-":
                        calculationResult = num1 - num2;
                        break;
                    case "*":
                        calculationResult = num1 * num2;
                        break;
                    case "/":
                        if (num2 == 0) {
                            result.put("error", "0으로 나눌 수 없습니다");
                            return result;
                        }
                        calculationResult = num1 / num2;
                        break;
                }
                
                result.put("result", calculationResult);
                result.put("expression", num1 + " " + operator + " " + num2);
            } catch (Exception e) {
                result.put("error", "계산 중 오류가 발생했습니다: " + e.getMessage());
            }
        } else {
            result.put("error", "계산식을 찾을 수 없습니다. 예: '5 + 3'");
        }
        
        return result;
    }
    
    /**
     * 날씨 도구 실행 (모킹)
     */
    public Map<String, Object> executeWeather(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("location", "서울");
        result.put("temperature", "15°C");
        result.put("condition", "맑음");
        result.put("humidity", "65%");
        result.put("note", "이것은 샘플 데이터입니다");
        return result;
    }
    
    /**
     * 시간 도구 실행
     */
    public Map<String, Object> executeTime(String message) {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        result.put("currentTime", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.put("timezone", "Asia/Seoul");
        return result;
    }
}
