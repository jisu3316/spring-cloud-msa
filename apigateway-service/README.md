# API Gateway Service

API Gateway Service는 사용자가 설정한 라우팅 설정에 따라서 각각의 엔드 포인트로 클라이언트 대신해서 요청하고
응답을 받으면 클라이언트한테 전달해 주는 프락시 역할을 합니다. 시스템의 내부 구조는 숨기고 외부의 요청에 대해서 적절한 형태로
가공해서 응답할 수 있다는 장점을 가지고 있습니다.

마이크로 서비스 자체가 독립적으로 빌드 되고 배포되는 장점이 있습니다.  
하지만 클라이언트 사이드에서는 문제가 있습니다.  
클라이언트 사이드에서 마이크로 서비스의 endpoint를 직접적으로 호출할 경우 클라이언트 애플리케이션도 수정 후 빌드, 배포되어야 합니다.  
그래서 단일 진입 전을 가지고 있는 형태로서 개발하는 게 필요하게 되었습니다.

이러한 이유로 서버단(백엔드)에서 게이트웨이 역할을 해줄 수 있는 일종의 진입 전(API Gateway)를 두게 됩니다.  
각각의 마이크로 서비스에 요청되는 모든 정보에 대해서 일괄적으로 처리할 수 있게 됩니다.  
모바일, Client SPA Web app(React, Vue), MVC Web app 등 어떤 식의 앱이든 마이크로 서비스를 직접 호출하지 않고
게이트웨이만 상대하게 됩니다.  
따라서 내가 필요한 정보들을 게이트웨이에서 변경하고 갱신하는데 훨씬 수월해집니다.

## API Gateway 를 통해 할 수 있는 일
- 인증 및 권한 부여
- 서비스 검색 통합
- 응답 캐싱
- 정책, 회로 차단기 및 QoS 다시 시도
- 속도 제한 
- 부하 분산
- 로깅, 추적, 상관 관계
- 헤더, 쿼리 문자열 및 청구 변환
- IP 허용 목록애 추가

# Dependencies
- Eureka Discovery Client
- Gateway

# Filter 적용하기
### 1. 기본 라우팅 기능
### 2. 커스텀 필터 적용하기
### 3. 글로벌 필터 적용하기
### 4. 로깅 필터 적용하기

# 1. 기본 라우팅 기능

### java 코드를 통한 라우팅

```java
package com.example.apigatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    /**
     * /first-service/** 로 요청이 들어오면 http://localhost:8081 로 간다.
     * request header 에 키밸류 추가,  response header 에 키 벨류추가
     */
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/first-service/**")
                        .filters(f -> f.addRequestHeader("first-request", "first-request-header")
                                       .addResponseHeader("first-response", "first-response-header"))
                        .uri("http://localhost:8081"))
                .route(r -> r.path("/second-service/**")
                        .filters(f -> f.addRequestHeader("second-request", "second-request-header")
                                .addResponseHeader("second-response", "second-response-header"))
                        .uri("http://localhost:8082"))
                .build();
    }
}

```

### application.yml 을 통한 라우팅
```yaml
server:
  port: 8000

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka

spring:
  application:
    name: apigateway-service
  cloud: # 리스트 형태로 여러개의 라우터 객체를 등록할 수 있다.
    gateway:
      routes:
        - id: first-service # 아이디
          uri: http://localhost:8081/ # 어디로 포워딩 될 것인지
          predicates:
            - Path=/first-service/** # 조건절 사용자가 입력한 path 정보가 /first-service/** 어떤 요청 정보가 와도 위의 uri로 포워딩된다.
          filters:
            - AddRequestHeader=first-request, first-request-header2
            - AddResponseHeader=first-response, first-response-header2
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
             - AddRequestHeader=second-request, second-request-header2
             - AddResponseHeader=second-response, second-response-header2

```

# 2. 커스텀 필터 적용하기

### CustomFilter

```java
package com.example.apigatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class CustomFilter extends AbstractGatewayFilterFactory<CustomFilter.Config> {

    public CustomFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        // Custom Pre Filter
        return (exchange, chain) -> {
            // rxjava 의 ServerHttpRequest, ServerHttpResponse import 받아야 함.
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Custom PRE filter: request id -> {}", request.getId());

            // Custom Post Filter
            // Mono = WebFlex 의 단일 객체 리턴시 사용
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                log.info("Custom POST filter: response code -> {}", response.getStatusCode());
            }));
        };
    }

    public static class Config {
        // Put the configuration properties
    }
}

```
### application.yml

```yaml
server:
  port: 8000

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka

spring:
  application:
    name: apigateway-service
  cloud: # 리스트 형태로 여러개의 라우터 객체를 등록할 수 있다.
    gateway:
      routes:
        - id: first-service # 아이디
          uri: http://localhost:8081/ # 어디로 포워딩 될 것인지
          predicates:
            - Path=/first-service/** # 조건절 사용자가 입력한 path 정보가 /first-service/** 어떤 요청 정보가 와도 위의 uri로 포워딩된다.
          filters:
#            - AddRequestHeader=first-request, first-request-header2
#            - AddResponseHeader=first-response, first-response-header2
            - CustomFilter
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
#             - AddRequestHeader=second-request, second-request-header2
#             - AddResponseHeader=second-response, second-response-header2
            - CustomFilter
```
filters 를 주석 한 후 depth를 맞춰 - CustomFilter 를 작성해주면 잘 작동한다.  
여기서 내가 만든
커스텀 필터의 클래스 이름을 적어 줘야한다.

# 3. 글로벌 필터 적용하기

## GlobalFilter

```java
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
```
## application.yml

```yaml
server:
  port: 8000

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://localhost:8761/eureka

spring:
  application:
    name: apigateway-service
  cloud: # 리스트 형태로 여러개의 라우터 객체를 등록할 수 있다.
    gateway:
      default-filters:
        - name: GlobalFilter
          args:
            baseMessage: Spring Cloud Gateway Global Filter
            preLogger: true
            postLogger: true
      routes:
        - id: first-service # 아이디
          uri: http://localhost:8081/ # 어디로 포워딩 될 것인지
          predicates:
            - Path=/first-service/** # 조건절 사용자가 입력한 path 정보가 /first-service/** 어떤 요청 정보가 와도 위의 uri로 포워딩된다.
          filters:
#            - AddRequestHeader=first-request, first-request-header2
#            - AddResponseHeader=first-response, first-response-header2
            - CustomFilter
        - id: second-service
          uri: http://localhost:8082/
          predicates:
            - Path=/second-service/**
          filters:
#             - AddRequestHeader=second-request, second-request-header2
#             - AddResponseHeader=second-response, second-response-header2
            - CustomFilter

```
spring.cloud.gateway.default-filters:
- name : 내가 만든 글로벌 필터 클래스 이름을 적어준다.
- args : GlobalFilter의 값 들은 내가 작성한 static class의 변수들로 매핑된다.  
GlobalFilter.Config의 변수에 적용할 값들을 넣어준다. 추후의 이런 값들은 spring-cloud-config를 통해 외부에서 가져올 수 있게 된다.

# 4. 로깅 필터 적용하기
## LoginFilter
```java
package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {

    public LoggingFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter baseMessage: -> {}", config.getBaseMessage());

            if (config.getPreLogger()) {
                log.info("Logging PRE Filter Start: request id {}", request.getId());
            }

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.getPostLogger()) {
                    log.info("Logging Post Filter end: response code -> {}", response.getStatusCode());
                }
            }));
        }, Ordered.LOWEST_PRECEDENCE);

        return filter;
    }

    @Data
    public static class Config {
        // Put the configuration properties
        private String baseMessage;
        private Boolean preLogger;
        private Boolean postLogger;
    }
}
```
GatewayFilter 반환값의 apply 메서드를 재 정의하여 필터가 할 일을 정해 놓으면 이 내용이 스프링 클라우드 게이트웨이로 전송되는 모든 클라이언트 들의 공통적인 작업을 할 수 있습니다.  
기존의 GlobalFilter는 람다 함수를 사용하여 필터를 정의하였습니다.  
이 필터는 OrderGatewayFilter 객체를 구현해서 반환해 주면 됩니다.

OrderGatewayFilter
```java
public class OrderedGatewayFilter implements GatewayFilter, Ordered {
    private final GatewayFilter delegate;
    private final int order;

    public OrderedGatewayFilter(GatewayFilter delegate, int order) {
        this.delegate = delegate;
        this.order = order;
    }

    public GatewayFilter getDelegate() {
        return this.delegate;
    }

    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return this.delegate.filter(exchange, chain);
    }

    public int getOrder() {
        return this.order;
    }

    public String toString() {
        return "[" + this.delegate + ", order = " + this.order + "]";
    }
}
```
OrderedGatewayFilter 이 필터를 살펴보겠습니다.  
GatewayFilter와 Ordered를 상속받은 것을 볼 수 있습니다.  
우리가 구현해야 할 내용은 생성자입니다.  
OrderedGatewayFilter는 생성자에 GatewayFilter 와 order(순서)를 받고 있습니다.  
중요한 것은 filter라는 메서드입니다.  
filter라는 메서드가 재정의됨으로써 filter가 해야 할 역할을 여기에 재정의하는 것입니다.  
filter라는 메서드는 ServerWebExchange와 GatewayFilterChain을 받고 싶습니다.  
현재 spring-gateway 이 프로젝트는 Spring-WebFlux를 사용하고 있습니다.  
기존의 spring-mvc를 통해 개발을 할 때는 HttpServletRequest, HttpServletResponse를 사용했지만
Spring-WebFlux를 사용하게 되면 HttpServletRequest, HttpServletResponse을 지원하지 않습니다.  
ServerRequest, ServerResponse를 가지고 사용해야 합니다.  
ServerRequest, ServerResponse 을 사용할 수 있도록 도와주는 객체가 ServerWebExchange입니다.  
이 ServerWebExchange 객체로부터 ServerRequest, ServerResponse를 얻어올 수 있습니다.  
GatewayFilterChain은 PRE, POST Filter들을 연결시켜서 작업할 수 있도록 합니다.  

그리고 order는 Ordered.HIGHEST_PRECEDENCE 와 Ordered.LOWEST_PRECEDENCE 두개가 있습니다.
최상위, 최하위를 적용할 수 있습니다.  
여기서 구현할 필터의 순서는 Global (PRE) -> Custom (PRE) -> Logging (PRE) -> Logging (POST) -> Custom (POST) -> Global (POST)
 이와 같이 적용을 할 것이기 때문에 Ordered.LOWEST_PRECEDENCE 을 통해 제일 마지막에 로깅을 찍을 수 있도록 합니다.
 