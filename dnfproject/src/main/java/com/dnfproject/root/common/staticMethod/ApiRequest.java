package com.dnfproject.root.common.staticMethod;

import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class ApiRequest {

    private static ValueAutoWired valueAutoWired;

    @Autowired
    public ApiRequest(ValueAutoWired valueAutoWired) {
        ApiRequest.valueAutoWired = valueAutoWired; // static 필드에 주입
    }

    public static Object requestGetAPI(String url) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("accept", "application/json");
            headers.set("APIKey", valueAutoWired.getApiKey());
            String baseUrl = "https://api.neople.co.kr/df" + url;
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Object> responseEntity = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            return responseEntity.getBody();

        } catch (RestClientResponseException exception) {
            throw apiErrorHandle(exception);
        }
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static CustomException apiErrorHandle(RestClientResponseException exception) {
        int statusCode = exception.getStatusCode().value();
        String responseBody = exception.getResponseBodyAsString();
        log.error("External API error: {} - statusCode: {}, responseBody: {}", exception.getMessage(), statusCode, responseBody);

        String dnfCode = parseDnfCodeFromResponse(responseBody);
        ErrorCode errorCode = dnfCode != null ? ErrorCode.fromDnfApiCode(dnfCode) : null;

        if (errorCode != null) {
            return new CustomException(errorCode);
        }
        throw new RuntimeException(exception);
    }

    /** 응답 body에서 {"error":{"code":"DNF006", ...}} 형태의 code 추출 */
    private static String parseDnfCodeFromResponse(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode error = root.path("error");
            if (error.isMissingNode()) {
                return null;
            }
            JsonNode codeNode = error.path("code");
            return codeNode.isMissingNode() ? null : codeNode.asText();
        } catch (Exception e) {
            log.debug("Failed to parse API error response body: {}", e.getMessage());
            return null;
        }
    }

}
