package com.gcp.agent.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Cloud Functions / Cloud Run 도구를 모킹한 간단한 Tools 서비스
 */
@Service
public class ToolService {

    public enum Tool {
        CALCULATOR("calculator", "계산기 - 수학 연산 수행"),
        WEATHER("weather", "날씨 조회 - 모킹 데이터"),
        TIME("time", "현재 시간 조회"),
        NONE("none", "도구 사용 안 함");

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

    public Tool determineTool(String message) {
        String lower = message.toLowerCase();

        if (lower.matches(".*\\d+\\s*[+\\-*/]\\s*\\d+.*") ||
                lower.contains("계산") || lower.contains("더하기") ||
                lower.contains("빼기") || lower.contains("곱하기") ||
                lower.contains("나누기")) {
            return Tool.CALCULATOR;
        }
        if (lower.contains("날씨") || lower.contains("기온")) {
            return Tool.WEATHER;
        }
        if (lower.contains("시간") || lower.contains("몇 시")) {
            return Tool.TIME;
        }
        return Tool.NONE;
    }

    public Map<String, Object> executeCalculator(String message) {
        Map<String, Object> result = new HashMap<>();
        Pattern pattern = Pattern.compile("(\\d+)\\s*([+\\-*/])\\s*(\\d+)");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            try {
                double n1 = Double.parseDouble(matcher.group(1));
                String op = matcher.group(2);
                double n2 = Double.parseDouble(matcher.group(3));
                double out = 0;

                switch (op) {
                    case "+" -> out = n1 + n2;
                    case "-" -> out = n1 - n2;
                    case "*" -> out = n1 * n2;
                    case "/" -> {
                        if (n2 == 0) {
                            result.put("error", "0으로 나눌 수 없습니다");
                            return result;
                        }
                        out = n1 / n2;
                    }
                }

                result.put("result", out);
                result.put("expression", n1 + " " + op + " " + n2);
            } catch (Exception e) {
                result.put("error", "계산 중 오류가 발생했습니다: " + e.getMessage());
            }
        } else {
            result.put("error", "계산식을 찾을 수 없습니다. 예: '5 + 3'");
        }
        return result;
    }

    public Map<String, Object> executeWeather(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("location", "서울 (샘플)");
        result.put("temperature", "20°C");
        result.put("condition", "맑음");
        result.put("note", "실제 GCP API 대신 모킹된 데이터입니다.");
        return result;
    }

    public Map<String, Object> executeTime(String message) {
        Map<String, Object> result = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        result.put("currentTime", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        result.put("timezone", "Asia/Seoul");
        return result;
    }
}

