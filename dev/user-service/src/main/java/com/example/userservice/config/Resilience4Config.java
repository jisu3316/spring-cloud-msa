package com.example.userservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4Config {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> globalCustomConfiguration() {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(4)              // CircuitBreaker를 열지 결정하는 failure rate threshold percentage , default : 50
                .waitDurationInOpenState(Duration.ofMillis(1000))  // CircuitBreaker를 open 한 상태를 유지하는 지속시간을 의미, 이 기간 이후에 half-open 상태 default : 60초
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)  // CircuitBreaker가 닫힐 때 통화 결과를 기록하는데 사용되는 슬라이딩 창의 유형을 구성, 카운트 기반 또는 시간 기반
                .slidingWindowSize(2) // CircuitBreaker가  닫힐 때 호출 결과를 기록하는데 사용되는 슬라이딩 창의 크기를 구성 default : 100
                .build();

        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(4)) // TimeLimiter 는 future supplier 의 time limit을 정하는 API default : 1초  오더 서비스에서 4초동안 응답이 없으면 문제로 간주하고 서킷브레이커를 open 한다.
                .build();

        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .timeLimiterConfig(timeLimiterConfig)
                .circuitBreakerConfig(circuitBreakerConfig)
                .build()
        );
    }
}
