package com.example;

import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.model.StatusCodes;
import java.util.concurrent.CompletionStage;

public class UserService {

    private static final Integer PAYMENT_MAX_RETRY = 1;

    private UserService() {}

    public static boolean doesUserExist(Integer userId, Http http) {
        return UserService.getUser(userId, http);
    }

    private static Boolean getUser(Integer userId, Http http) {
        Integer timeOut = PAYMENT_MAX_RETRY;

        String url =
                "http://host.docker.internal:8080/users/" + Integer.toString(userId);

        try {
            while (timeOut-- != 0) {
                HttpRequest request = HttpRequest.GET(url);
                CompletionStage<HttpResponse> completion = http.singleRequest(request);
                HttpResponse response = completion.toCompletableFuture().join();
                if (response.status() == StatusCodes.OK) return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}