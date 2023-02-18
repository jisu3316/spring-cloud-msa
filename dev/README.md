# Users Microservice

## Features
-  신규 회원 등록
- 회원 로그인
- 상세 정보 확인
- 회원 정보 수정/삭제
- 상품 주문
- 주문 내역 확인

## Environment
- mac OS
- Spring boot 2.7.8
- Maven

## Dependencies
- Spring Web
- Eureka Discovery Client
- H2 Database
- Spring Data JPA
- Spring Boot DevTools
- Lombok
- Jwt

## APIs

| 프로젝트 이름         | 기능                   | URI(API Gate way 사용시)            | URI(API Gate way 미사용 시) | HTTP  <br/>Method |
|-----------------|----------------------|----------------------------------|-------------------------|-------------------|
| user-service    | 사용자 정보 등록            | /user-service/users              | /users                  | POST              |
| user-service    | 전체 사용자 조회            | /user-service/users              | /users                  | GET               |
| user-service    | 사용자 정보, 주문<br/>내역 조회 | /user-service/users/{userId}     | /users/{userId}         | GET               |
| user-service    | 작동 상태 확인             | /user-service/users/health-check | /users/health-check     | GET               |
| user-service    | 환영 메세지               | /user-service/users/welcome      | /users/welcome          | GET               |
| catalog-service | 상품 목록 조회             | /catalog-service/catalogs        | /catalogs               | GET               |
| order-service   | 사용자 별 상품 주문          | /order-service/{userId}/orders   | /{userId}/orders        | POST              |
| order-service   | 사용자 별 주문 내역 조회       | /order-service/{userId}/orders   | /{userId}/orders        | GET               |


## API-Gateway-Service

application.yml
```yaml
server:
  port: 8000

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
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
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/user-service/login
            - Method=POST
          filters:
            - RemoveRequestHeader=Cookie
            - RewritePath=/user-service/(?<segment>.*), /$\{segment}
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/user-service/users
            - Method=POST
          filters:
            - RemoveRequestHeader=Cookie
            - RewritePath=/user-service/(?<segment>.*), /$\{segment}
        - id: user-service
          uri: lb://USER-SERVICE
          predicates:
            - Path=/user-service/**
            - Method=GET
          filters:
            - RemoveRequestHeader=Cookie
            - RewritePath=/user-service/(?<segment>.*), /$\{segment}
            - AuthorizationHeaderFilter
        - id: catalog-service
          uri: lb://CATALOG-SERVICE
          predicates:
            - Path=/catalog-service/**
        - id: order-service
          uri: lb://ORDER-SERVICE
          predicates:
            - Path=/order-service/**

token:
  secret: spring_boot_application_com.example.userservice.kim_ji_su_2023.my_first_micro_service_secret_key
```
### spring.cloud.gateway.routes - predicates : 다음 값들이 만족할때 정상 호출 된다.    
### spring.cloud.gateway.routes.filters - RemoveRequestHeader=Cookie : 헤더 값을 초기화한다.  
### spring.cloud.gateway.routes.filters - RewritePath=/user-service/(?<segment>.*), /$\{segment} : 사용자가 /user-service/login 으로 요청하면 URI를 /login으로 바꾼다는 의미이다.  
### 클라이언트쪽에서 호출을 /user-service/**로 한다면 RewritePath로 인하여 user-service의 URL을 /user-serivce 를 제외하고 /users 만 선언해줘도 된다.
### 만약 인증처리가 불 필요하다고 생각하는 URI에 대해서는 filters에서 AuthorizationHeaderFilter를 제외시키면 된다.


# Spring Cloud Config
- 분산 시스템에서 서버, 클라이언트 구성에 필요한 설정 정보(application.yml)를 외부 시스템에서 관리
- 하나의 중앙화 된 저장소에서 구성요소 관리 기능
- 각 서비스를 다시 빌드하지 않고, 바로 적용 가능
- 애플리케이션 배포 파이프라인을 통해 DEV, PROD 환경에 맞는 구성정보 사용

## application.yml
```yaml
spring:
  config:
    import:
      - classpath:/bootstrap.yml
```
위의 코드를 추가해야 bootstrap.yml을 읽을 수 있다.

## bootstrap.yml
```yaml
spring:
  cloud:
    config:
      uri: http://127.0.0.1:8888
      name: user-service
  profiles:
    active: dev
```

bootstrap.yml 은 application.yml에서 생성한다.  
가장 최상위 설정 파일이므로 application.yml 보다 먼저 인식 된다.  
spring.cloud.config.uri = spring-cloud-config-server 의 uri이다.  
spring.cloud.config.name = spring-cloud-config-server의 yml파일의 이름을 작성해준다.  
spring.profiles.active = spring-cloud-config-server의 yml 환경을 작성해주면된다.  

위와 같이 작성하게 되면 스프링 서버 기동시 bootstrap.yml 파일을 통해 user-servie.yml을 읽어오게된다.  
그래서 공통으로 사용하는 정보들을 user-service.yml에 작성하면 공통코드를 줄일 수 있다.

