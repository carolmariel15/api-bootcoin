package com.nttdata.api.bootcoin.router;

import com.nttdata.api.bootcoin.handler.BootCoinMovementHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class BootCoinMovementRouter {

    @Bean
    public RouterFunction<ServerResponse> clientRouterFunc(BootCoinMovementHandler bootCoinMovementHandler) {
        return RouterFunctions.route(GET("/bootcoin").and(accept(MediaType.TEXT_EVENT_STREAM)), bootCoinMovementHandler::getAllBMovements)
                .andRoute(GET("/bootcoin/{id}").and(accept(MediaType.TEXT_EVENT_STREAM)), bootCoinMovementHandler::getBMovement)
                .andRoute(POST("/bootcoin").and(accept(MediaType.TEXT_EVENT_STREAM)), bootCoinMovementHandler::create)
                .andRoute(PUT("/bootcoin").and(accept(MediaType.TEXT_EVENT_STREAM)), bootCoinMovementHandler::edit)
                .andRoute(DELETE("/bootcoin/{id}").and(accept(MediaType.TEXT_EVENT_STREAM)), bootCoinMovementHandler::delete)
                .andRoute(GET("/bootcoin/{accountNumber}").and(accept(MediaType.TEXT_EVENT_STREAM)), bootCoinMovementHandler::listReport);
    }

}
