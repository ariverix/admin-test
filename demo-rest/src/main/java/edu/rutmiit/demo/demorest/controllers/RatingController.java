package edu.rutmiit.demo.demorest.controllers;

import edu.rutmiit.demo.demorest.config.RabbitMQConfig;
import edu.rutmiit.demo.events.UserRatedEvent;
import grpc.demo.AnalyticsServiceGrpc;
import grpc.demo.UserRatingRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatingController {

    @GrpcClient("analytics-service")
    private AnalyticsServiceGrpc.AnalyticsServiceBlockingStub analyticsStub;

    private final RabbitTemplate rabbitTemplate;

    public RatingController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping("/api/users/{id}/rate")
    public String rateUser(@PathVariable Long id) {
        // Вызов gRPC
        var request = UserRatingRequest.newBuilder().setUserId(id).setCategory("General").build();
        var gRpcResponse = analyticsStub.calculateUserRating(request);

        // Отправка события в Fanout
        var event = new UserRatedEvent(gRpcResponse.getUserId(), gRpcResponse.getRatingScore(), gRpcResponse.getVerdict());

        // Для Fanout routingKey не важен, оставляем пустым ""
        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", event);

        return "Rating calculated: " + gRpcResponse.getRatingScore();
    }
}

