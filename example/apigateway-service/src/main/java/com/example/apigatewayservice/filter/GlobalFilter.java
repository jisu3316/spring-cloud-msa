package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    public GlobalFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return (exchange, chain) -> {
            // rxjava 의 ServerHttpRequest, ServerHttpResponse import 받아야 함.
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Global baseMessage: -> {}", config.getBaseMessage());

            if (config.getPreLogger()) {
                log.info("Global Filter Start: request id {}", request.getId());
            }

            // Custom Post Filter
            // Mono = WebFlex 의 단일 객체 리턴시 사용
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.getPostLogger()) {
                    log.info("Global Filter end: response code -> {}", response.getStatusCode());
                }
            }));
        };
    }

    @Data
    public static class Config {
        // Put the configuration properties
        private String baseMessage;
        private Boolean preLogger;
        private Boolean postLogger;
    }
}
