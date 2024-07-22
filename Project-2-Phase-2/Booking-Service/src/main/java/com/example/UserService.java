package com.example;

import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.*;;
public class UserService {
    private UserService() {
    }

    public static boolean isUser(Integer userId, Http http) {
        return UserService.getUser(userId, http);
    }

    public static boolean getUser(Integer user_id, Http http) {
        try {
            HttpRequest request = HttpRequest.GET("http://localhost:8080/users/" + user_id);
            CompletionStage<HttpResponse> completion = http.singleRequest(request);
            HttpResponse response = completion.toCompletableFuture().get(5, TimeUnit.SECONDS);
            return response.status().equals(StatusCodes.OK);
        } catch (Exception e) {
            return false;
        }
    }
}
