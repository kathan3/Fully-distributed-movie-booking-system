package com.example;

import akka.http.javadsl.Http;
import akka.http.javadsl.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class WalletService {

    private static final Integer PAYMENT_MAX_RETRY = 1;

    public static String refund(Integer userId, Integer amount, Http http) {
        return updateWallet(userId, amount, "credit", http);
    }

    public static String payment(Integer userId, Integer amount, Http http) {
        return updateWallet(userId, amount, "debit", http);
    }

    private static String updateWallet(Integer userId, Integer amount, String action, Http http) {
        Integer timeout = PAYMENT_MAX_RETRY;

        String url = "http://host.docker.internal:8082/wallets/" + userId;

        Map<String, Object> data = Map.of("action", action, "amount", amount.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "FAIL";
        }
        try {
            while (timeout-- > 0) {
                HttpRequest request = HttpRequest.PUT(url)
                        .withEntity(ContentTypes.APPLICATION_JSON, jsonData);
                CompletionStage<HttpResponse> completion = http.singleRequest(request);
                HttpResponse response = completion.toCompletableFuture().join();
                if (response.status() == StatusCodes.OK) {
                    return "SUCCESS";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "FAIL";
    }
}