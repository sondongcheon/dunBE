package com.dnfproject.root.common.staticMethod;

import com.dnfproject.root.common.exception.CustomException;
import com.dnfproject.root.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
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
            //String baseUrl = "https://api.neople.co.kr/df/servers/cain/characters/30e63551f473fcfe366ad62f989beb90/timeline?limit=10&code=201&startDate=20260101T0000&endDate=20260122T0000&apikey=iq3ORt1snK22t1nkSJeo3iZNPeH5REqi";
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<Object> responseEntity = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.GET,
                    entity,
                    Object.class
            );

            return responseEntity.getBody();

        } catch (HttpStatusCodeException exception) {
            throw ApiErrorHandle(exception);
        }
    }

    private static CustomException ApiErrorHandle(HttpStatusCodeException exception) {
        int statusCode = exception.getStatusCode().value();
        log.error(exception.getMessage());
        log.error("statusCode : {}", statusCode);
        return switch (statusCode) {
            case 400 -> new CustomException(ErrorCode.TEST_EXCEPTION);
            case 401 -> new CustomException(ErrorCode.RUNTIME_EXCEPTION);

            default -> throw new RuntimeException(exception);
        };
    }

}
