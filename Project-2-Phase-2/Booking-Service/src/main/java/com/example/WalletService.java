package com.example;

import akka.http.javadsl.Http;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.CompletionStage;

public class WalletService {
    public WalletService(){}
    public static boolean walletOperation(Integer user_id, Integer amount, String action, Http http){
        Map<String, Object> data = Map.of("action", action, "amount", amount.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
        try {
                HttpRequest request = HttpRequest.PUT("http://localhost:8082/wallets/"+user_id)
                        .withEntity(ContentTypes.APPLICATION_JSON,jsonData);
                CompletionStage<HttpResponse> completion = http.singleRequest(request);
                HttpResponse response = completion.toCompletableFuture().join();
                if (response.status() == StatusCodes.OK) {
                    return true;
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
