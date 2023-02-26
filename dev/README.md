# E-commerce Microservice

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
- Java 11

## Dependencies
- Spring Web
- Eureka Discovery Client
- H2 Database
- Spring Data JPA
- Spring Boot DevTools
- Lombok
- Jwt
- spring-boot-starter-actuator
- spring-cloud-starter-bus-amqp
- spring-cloud-starter-openfeign
- spring-cloud-starter-circuitbreaker-resilience4j
- spring-cloud-starter-sleuth
- spring-cloud-starter-zipkin

## APIs

| 프로젝트 이름         | 기능                   | URI(API Gate way 사용시)            | URI(API Gate way 미사용 시) | HTTP  <br/>Method |
|-----------------|----------------------|----------------------------------|-------------------------|-------------------|
| user-service    | 사용자 정보 등록            | /user-service/users              | /users                  | POST              |
| user-service    | 전체 사용자 조회            | /user-service/users              | /users                  | GET               |
| user-service    | 사용자 정보, 주문<br/>내역 조회 | /user-service/users/{userId}     | /users/{userId}         | GET               |
| user-service    | 작동 상태 확인             | /user-service/users/health-check | /users/health-check     | GET               |
| user-service    | 환영 메세지               | /user-service/users/welcome      | /users/welcome          | GET               |
| user-service    | 로그인                  | /user-service/login              | /login                  | POST              |
| catalog-service | 상품 목록 조회             | /catalog-service/catalogs        | /catalogs               | GET               |
| order-service   | 사용자 별 상품 주문 생성       | /order-service/{userId}/orders   | /{userId}/orders        | POST              |
| order-service   | 사용자 별 주문 내역 조회       | /order-service/{userId}/orders   | /{userId}/orders        | GET               |
| config-service  | 암호화                  | /config-service/encrypt          | /encrypt                | GET               |
| config-service  | 복호화                  | /config-service/decrypt          | /decrypt                | GET               |
| config-service  | 값 변경 적용              | /actuator/refresh                | /actuator/refresh       | POST              |
| config-service  | 값 변경 적용              | /actuator/busrefresh             | /actuator/busrefresh    | POST              |



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


# Spring - Cloud- Bus
- 분산 시스템의 노드를 경량 메시지 브로커(Rabbit MQ) 연결
- 상태 및 구성에 대한 변경 사항을 연결된 노드에게 전달(Broadcast)  

위에서 cofing-service를 만들면서 공통적으로 사용하는 값 등을 설정하게 되었다. 이 값들이 변경되면 서버를 재기동하거나, Actuator refresh를 통해 
변경된 값을 다시 가져올 수 있다. 하지만 마이크로서비스가 한 두개면 괜찮을지 몰라도 여러개, 수십, 수백개 되면 이 서버들을 하나씩 refresh를 해줘야한다는 일이 발생한다.
이런걸 개선하기위한 방법이 spring cloud bus입니다. Config Server와 연결하여 변경된 정보가 있다면 각각의 마이크로 서비스에게 직접 데이터를 Push updates 해주는 방식으로 변경해보겠습니다.  
이때 데이터가 갱신되었다는것을 각각의 마이크로 서비스에게 알려줄때 AMQP(Advanced Message Queuing Protocol)을 이용해서 알려주도록하겠습니다. 

## AMQP (Advanced Message Queuing Protocol) 메세지 지향 미들웨어를 위한 개방형 표준 응용 계층 프로토콜
- ### 메세지 지향
- ### Erlang, RabbitMQ에서 사용
- ### 메시지 브로커
- ### 초당 20+ 메시지를 소비자에게 전달
- ### 메시지 전달 보장, 시스템 간 메시지 전달
- ### 브로커, 소비자 중심

## Kafka 프로젝트
- ### Apache Software Founation이 Scalar 언어로 개발한 오픈 소스 메시지 브로커 프로젝트
- ### 분산형 스트리밍 플랫폼
- ### 대요량의 데이터를 처리 가능한 메시징 시스템
- ### 초당 100ㅏ+ 이상의 이벤트 처리
- ### Pub/Sub, Topic에 메시지 전달
- ### Ack를 기다리지 않고 전달 가능
- ### 생상자 중심


|                                | Kafka                | Pulsar                | RabbitMQ(Mirrored)          |
|--------------------------------|----------------------|-----------------------|-----------------------------|
| Peak Throughput(최고 처리량) (MB/s) | 605 MB/s             | 305 MB/s              | 38 MB/s                     |
| p99 Latency (ms)               | 5 ms (200 MB/s load) | 25 ms (200 MB/s load) | 1 ms (reduced 30 MB/s load) |
## Actuator bus-refresh Endpoint
Spring Cloug Bus는 위에서 말한듯이 연결된 모드 노드에게 전달 되기 때문에 Cloud Bus에 연결된 서비스에서 /busrefresh 를 호출하게되면 자기하고 연결된 
클라우드 버스에게 알려주고 클라우드 버스가 또 다른 노드들(마이크로서비스들)하테 전달하게됩니다.

## Dependencies 추가
```xml
<dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
 </dependency>

<dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```
## application.yml
```yaml
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
management:
  endpoints:
    web:
      exposure:
        include: refresh, health, beans, httptrace, busrefresh
```
위와 같이 설정한후 컨피그 서버의 정보를 변경하고 /busrefresh 로 요청하면 변경된 정볼르 가져오는것을 확인 할 수 있다.


현재 각각의 마이크로서비스는 각각의 데이터베이스를 사용하고 있다. 그렇기 때문에 데이터의 동기화가 안되고 있다는 문제점이 있다.
예를 들어 order-service 의 서버를 A,B 두대를 사용하고있다고 치자.  
사용자 A가 주문을 두번 생성할때 주문생성 api를 두번 요청하면 api-gateway를 통해 로드밸렁싱이 된다.  
그러므로 첫번째 요청은 order-service A의 DB에 저장, 두번째 요청은 order-service B의 DB에 저장되기떄문에 
각각의 DB에 저장하게 되면 A라는 유저는 주문을 두번 했지만 조회를 하면 한번만 주문한것으로 나오게 된다.   
이러한 데이터 동기화가 이루어지지 않는 문제가 있다. 이러한 문제를 kafka를 통해 해결하고자 한다.

# Apache kafka

- ### Apache Software Foundation 의 Scalar 언어로 된 오픈 소스 메시지 브로커 프로젝트
  ####  - Open Source Message Broker Project
- ### 링크드인(Linked-in)에서 개발, 2011년 오픈 소스화
  #### - 2014 년 11월 링크드인에서 Kafka를 개발하던 엔지니어들이 Kafka개발에 집중하기 위해 Confluent라는 회사 창립
- ### 실시간 데이터 피드를 관리하기 위해 통일된 높은 처리량, 높은 처리량, 낮은 지연 시간을 지닌 플랫폼 제공
- ### Apple, Netflix, Yelp, Kakao, New York Times, Cisco, Paypal, Hyperledger Fabric, Uber, Salesfoce.com 등이 사용

### End-to-End 연결 방식의 아키텍처의 단점
- 데이터 연동의 복잡성 증가(HW, 운영체제, 장애 등)
- 서로 다른 데이터 Pipeline 연결 구조
- 확장이 어려운 구조  
###  Apache kafka 장점
- 모든 시스템으로 실시간으로 전송하여 처리할 수 있는 시스템
- 데이터가 많아지더라도 확장이 용이한 시스템
- Producer(메세지를 보내는 쪽)/Consumer(메세지를 받는쪽) 분리
- 메세지를 여러 Consumer에게 허용
- 높은 처리량을 위한 메시지 최적화
- Scale-out 가능
- Eco-system

## kafka Broker
- ### 실행 된 Kafka 애플리케이션 서버
- ### 3대 이상의 Broker Cluster 구성
- ### Zookeeper 연동
    #### - 역할: 메타데이터(Broker ID, Controller ID 등) 저장
    #### - Controller 정보 저장
- ### n개 Broker 중 1대는 Controller 기능 수행
    #### - Controller 역할
    - #####  각 Broker에게 담당 파티셔 할당 수행
    - #####  Broker 정상 동작 모니터링 관리

# kafka 설치 
https://kafka.apache.org/downloads  
kafka_2.13-3.4.0.tgz 다운 및 압축 해제 (tar xvf 파일명)  
config 폴더에는 zookeeper를 실행 시킬 수 있는 zookeeper.properties 파일이 있고 Apache kafka를 실행 시킬 수 있는 server.properties 가 있다.  
bin 폴더에는 주키퍼를 실행, 종료 할 수 있는 zookeeper-server-start.sh, zookeeper-server-stop.sh 파일이 있고
카프카를 실행, 종료 시킬 수 있는 kafka-server-start.sh, kafka-server-stop.sh 가 있다.  
카프카를 다운 받으면 윈도우와 맥 같은 파일을 다운받는데 윈도우는 bin/windows 폴더에 위와 같은 .bat 파일들이 있다.

이 프로젝트에서의 카프카는 pub/sub 기능 구현과, kafka에 메세지를 보냄에 있어서 자바 라이브러리를 통해 데이터를 받을 수 있는(kafka client)와 데이터베이스의 값들이
insert, update, delete 됐을때(변경사항이 생겼을때) 데이터베이스로부터 카프카가 변경된 데이터에 메세지를 가지고 그 값을 다른쪽에 있는 데이터베이스,
서비스에 전달하는 기능(kafka connect)에 대해서 알아보겠습다.

# Kafka Client

Kafka와 데이터를 주고 받기 위해 사용하는 Java Library
- https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients  
Producer, Consumer, Admin, Stream 등 Kafka 관련 API 제공
다양한 3rd party library 존쟤: C/C++, Node.js, Python, .NET 등
- https://cwiki.apache.org/confluence/display/KAFKA/Clients

# Kafka 서버 기동
Zookeeper 및 Kafka 서버 구동
- $KAFKA_HOME/bin/zookeeper-server-start.sh  
- $KAFKA_HOME/config/zookeeper.properties
- $KAFKA_HOME/bin/kafka-server-start.sh  
- $KAFKA_HOME/config/server.properties
- ./bin/zookeeper-server-start.sh ./config/zookeeper.properties  주키퍼 실행  
- ./bin/kafka-server-start.sh ./config/server.properties 카프카 서버 실  


Topic 생성
- $KAFKA_HOME/bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092  --partitions 1

Topic 목록 확인
- $KAFKA_HOME/bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

Topic 정보 확인
- $KAFKA_HOME/bin/kafka-topics.sh --describe --topic quickstart-events --bootstrap-server localhost:9092

# Kafka Producer/Consumer 테스트
메세지 생산
- $KAFKA_HOME/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic quickstart-events

메세지 소비
- $KAFKA_HOME/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic quickstart-events --from-beginning  

위의 명령어에서 --from-beginning 옵션은 내가 참여하기전의 데이터까지 불러올 수 있다.

# Kafka Connect
- Kafka Connect를 통해 Data를 Import/Export 가능
- 코드 없이 Configuration으로 데이터를 이동
- Standalone mode, Distribution mode 지원
  - RESTful API 통해 지원
  - Stream 또는 Batch 형태로 데이터 전송 가능
  - 커스텀 Connector를 통한 다양한 Plugin 제공(File, S3, Hive, Mysql, etc...)

# Kafka Connect 설치
### - curl -O http://packages.confluent.io/archive/5.5/confluent-community-5.5.2-2.12.tar.gz

### - curl -O http://packages.confluent.io/archive/6.1/confluent-community-6.1.0.tar.gz
이게 최신 버전
### - tar xvf confluent-community-6.1.0.tar.gz

### - cd  $KAFKA_CONNECT_HOME

## Kafka Connect 설정(기본으로 설정)
### - $KAFKA_CONNECT_HOME/config/connect-distributed.properties

## Kafka Connect 실행
### - ./bin/connect-distributed ./etc/kafka/connect-distributed.properties

## Topic 목록 확인
### - ./bin/kafka-topics.sh --bootstrap-server localhost:9092 --list

## JDBC Connector 설정
### - https://docs.confluent.io/5.5.1/connect/kafka-connect-jdbc/index.html
  - Download and extract the ZIP file -> confluentinc-kafka-connect-jdbc-10.0.0.1zip 다운로드
  - confluentinc-kafka-connect-jdbc-10.0.1.zip 

### etc/kafka/connect-distributed.properties 파일 마지막에 아래 plugin 정보 추가
- plugin.path=[confluentinc-kafka-connect-jdbc-10.0.1 폴더]

### JdbcSourceConnector에서 mysql 사용하기 위해 mysql 드라이버 복사
-  ./share/java/kafka/ 폴더에 mysql-java-client-2.7.2.jar  파일 복사


# pom.xml
```yaml
   <!-- kafka     -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>
    </dependencies>
```

# KafkaConsumerConfig
```java
package com.example.catalogservice.messagequeue;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092"); //카프카 서버
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumerGroupId");      //카프카 토픽에 쌓여있는 토픽들을 그룹화할수있다.
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(properties);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();
        kafkaListenerContainerFactory.setConsumerFactory(consumerFactory());

        return kafkaListenerContainerFactory;
    }
}

```

# KafkaConsumer
```java
package com.example.catalogservice.messagequeue;

import com.example.catalogservice.doamin.CatalogEntity;
import com.example.catalogservice.repository.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CatalogRepository catalogRepository;

    // example-catalog-topic 에 메시지가 전달되면 호출되는 메서드이다.
    @KafkaListener(topics = "example-catalog-topic")
    public void updateQty(String kafkaMessage) {
        log.info("Kafka Message : -> {}", kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        CatalogEntity entity = catalogRepository.findByProductId((String) map.get("productId"));
        if (entity != null) {
            entity.modifyStock(entity.getStock() - (Integer) map.get("qty"));
            catalogRepository.save(entity);
        }
    }
}

```


# KafkaProducerConfig

```java
package com.example.orderservice.messagequeue;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092"); //카프카 서버
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

```

# KafkaProducer

```java
package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderDto send(String topic, OrderDto orderDto) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(orderDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        log.info("Kafka Producer sent data from the Order microservice: {}", orderDto);

        return orderDto;
    }
}

```

# Microservice 통신시 연쇄 오류 
마이크로서비스는 각각의 서버를 가지고있다. (이 프로젝트에서는 유저서비스와 오더서비스처럼 나뉘어져있다.)  
그렇기 때문에 유저정보를 조회시 주문 정보를 가져오게 되는데 이때 오더 서비스 서버가 죽어있다면 java.net.UnknownHostException: order-service 같은 에러가 발생하게 된다.  
하지만 이렇게 되면 유저서비스 마저도 오류때문에 정상작동이 일어나지 않게 됩니다.  
그렇기 때문에 마이서비스 통신시 연쇄 오류가 발생하게 됩니다. 이때 오더서비스는 죽어있더라도 유저정보만 정상적인 흐름으로 작동하고 오류가 발생한 흐름은
막아주고 문제가 생긴 서비스를 다시 재사용할 수 있는 상태로 복구가 된다고 하면 이전에 사용했던것처럼 정상적인 흐름으로 바꿔주는 장치를 CircuitBreaker 라고 합니다.

# CircuitBreaker
- 장애가 발생하는 서비스에 반복적인 호출이 되지 못하게 차단
- 특정 서비스가 정상적으로 동작하지 않을 경우 다른 기능으로 대체 수행 -> 장애 회피

## Spring Cloud Netflix Hystrix
```yaml
<dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>


@EnableCircuitBreaker

feign:
  hystrix:
    enabled: true
```
2019년 이전에는 위와 같은 설정으로 넷플릭스에서 만든 Hystrix를 사용했지만 그 이후 부터는 더 이상 개발하지 않고 유지보수도 최근에는 더 이상 진행하지 않는다고 합니다.

그래서 저는 Resilience4j 를 사용하기로 했습니다.

# Resilience4j

- resilience4j-circuitbreaker: Circuit breaking
- resilience4j-ratelimiter: Rate limiting
- resilience4j-bulkhead: Bulkheading
- resilience4j-retry: Automatic retrying (sync and async)
- resilience4j-timelimiter: Timeout handling
- resilience4j-cache: Result caching
- https://resilience4j.readme.io/docs/getting-started
- https://github.com/resilience4j/resilience4j  
 
위와 같은 라이브러리를 제공합니다.

pom.xml
```yaml
<!-- rersilience4j   -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
        </dependency>
```


UserServiceImpl
```java
private final CircuitBreakerFactory circuitBreakerFactory;

        /* Using a feign client */
        /* Feign exception handling */
        /* ErrorDecoder */
        List<ResponseOrder> orders = orderServiceClient.getOrders(userId);

        /* CircuitBreaker*/
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orders = circuitbreaker.run(() -> orderServiceClient.getOrders(userId),
        throwable -> new ArrayList<>());

        userDto.setOrders(orders);
```


Resilience4j.config
```java
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


```

userServiceImpl 에서는 기본 서킷브레이커 빈을 주입 받았지만 위와 같이 커스텀하게 만들어 빈을 등록하면 커스텀한 빈을 주입 받을 수 있다. 이 빈은 여러개 만들고 만들때  
return factory -> factory.configure(builder -> builder.circuitBreakerConfig(circuitBreakerConfig)
.timeLimiterConfig(timeLimiterConfig).build(),
"circuitBreaker2");   
이렇게 만들고 주입 받는 곳에서 circuitBreakerFactory.create("circuitbreaker2"); 을 통해 커스텀하게 사용 할 수 있다.


# Microservice 분산 추적 - Zipkin, Spring  Cloud Sleuth
## Zipkin
위에서는 서킷 브레이커를 통해 연쇄적인 마이크로서비스의 오류를 예방할 수 있었습니다.  
이번에는 마이크로서비스가 자체적인 독립 서비스만 작동하는것이 아니라 연쇄적으로 여러개의 서비스가 실행되기 때문에 그 과정에서 해당하는 요청 정보가 어떻게 실행이되고 
어느단계를 거쳐서 어떤 마이크로 서비스로 이동이 되는지 이런것들을 추적할 수 있는 방법에 대해서 알아보겠습니다. 이러한 내용을 마이크로서비스의 분산 추적이라고 합니다.
- https://zipkin.io/
- Twitter에서 사용하는 분산 환경의 Timing 데이터 수집, 추적 시스템(오픈 소스)
- Google Drapper에서 발전하였으며, 분산환경에서의 시스템 병목 현상 파악
- Controller, Query Service, Databasem WebUI로 구성

### Span
- 하나의 요청에 사용되는 작업의 단위
- 64 bit unique ID

### Trace
- 트리 구조로 이뤄진 Span 셋
- 하나의 요청에 대한 같은 Trace ID 발급

하나의 요청에 대해서 Trace ID 는 같다. Span ID 는 각 마이크로서비스와 통신할때 마다 다른 ID가 발급 된다.

## Spring  Cloud Sleuth
  - #### servlet filter
  - #### rest template
  - #### schedule actions
  - #### message channels
  - #### feign client
- ### 위와 같은 스프링 부트 어플리케이션과 연동해서 가지고 있는 로그 데이터 값을 Zipkin과 서버측에다가 전달해주는 용도로 사용한다.
- ### 요청 값에 따른 Trace ID, Span ID 부여
- ### Trace와 Span Ids를 로그에 추가 가능

# Zipkin server 설치
## Docker
docker run -d -p 9411:9411 openzipkin/zipkin

## Java
curl -sSl https://zipkin.io/quickstart.sh | bash -s
java -jar zipkin.jar

기본 포트 번호는 9411 이다.

# Dependencies
```xml
        <!--  zipkin      -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-sleuth</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-starter-zipkin -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
            <version>2.2.8.RELEASE</version>
        </dependency>
```

# application.yml
```yaml
spring:
  application:
    name: user-service
  zipkin:
    base-url: http://127.0.0.1:9411
    enabled: true
  sleuth:
    sampler:
      probability: 1.0
```
log가 찍히는데 [order-service TraceID SpanID] TraceID를 가지고 localhost:9411 에 오른쪽 상단에 붙여넣으면 요청에 대해 추적할 수 있다.

# Micrometer
어플리케이션을 모니터링하기 위해서 각종 자료들을 수집하는 용도로 사용됩니다. 우리가 모니터링을 한다는 의미는 현재 CPU가 사용량이 얼마가 되었고, 메소드가 사용량이
얼마나 되었고 네트워크 트래픽이 발생했고, 어느정도 사용량이 있는지, 사용자의 요청이 몇번 호출되었는지 이런 수치화 되어있는 좌표를 도식화시켜서 표현해줄수있는것들을
모니터링도구라고 합니다. 모니터링 도구를 연동해줌으로써 현재 운영하고 있는 서버라던가, 시스템들의 가지고있는 부하라던가, 문제가 생겼던 시점등을 파악할 수 있습니다.
Spring을 통해 마이크로서비스를 개발하고 있는데 마이크로서비스라는것 자체가 하나의 어플리케이션이 아니라 분산되어있는 여러개 독립적인 소프트웨어로 구성 되어 있기 때문에
각종 서비스, 각종 서버들의 기능이 잘 작동되고 있는지 문제가 생긴곳이 있는지 병목현상이 있는지 잘 파악을 해서 그때그떄 자원을 할당해주는것이 필요합니다. 
그래서 zipkin과 연동해 확인을 했었고 이제는 모니터링 시스템을 구축해야 합니다. 엔드포인트 Spring Framework5, Spring Boot 2 버전 부터는 스프링에서 발생하는 
다양한 지표들을 Metics를 Micometer로 서비스를 제공하고 있습니다. 따른 추가적인 작업을 하지 않더라고 엔드포인트에서 발생했던 작업에 대해서 자동으로 수치를 기록돼서
가지고 올 수 있습니다. 이러한 수집된 정보는 Prometheus 라던가 다양한 모니터링 시스템하고 연동되어서 시각화할때 유용하게 사용할 수 있습니다.
- ### https://micrometer.io/
- ### JVM 기반의 애플리케이션의 Metrics 제공
- ### Spring Framework5, Spring Boot 2부터 Spring의 Metrics처리
- ### Prometheus 등의 다양한 모니터링 시스템 지원

# Timer
- ### 짧은 지연 시간, 이벤트의 사용 빈도를 측정
- ### 시계열로 이벤트의 시간, 호출 빈도 등을 제공
- ### @Timed 제공

```xml
        <!-- Micrometer      -->
        <dependency>
            <groupId>io.micrometer</groupId>
            <artifactId>micrometer-registry-prometheus</artifactId>
        </dependency>
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: refresh, health, beans, busrefresh, info, metrics, prometheus
```
actuator를 사용하게 되면 스프링에서 제공하는 다양한 지표, 어떤 데이터를 활용할 수 있습니다.  
기존의 health, info, busrefresh등을 사용하고 있었습니다.
이번에는 prometheus, metrics를 추가해줌으로써 현재 서비스가 사용할 수 있는 다양한 지표들이 어떤것들이 있는지 확인해보겠습니다.

확인하고 싶은 엔드포인트에 @Timed(value="이름", longTask = true) 를 추가해줍니다.  
/actuator/metrics 로 접속하면 위에 정의한 value="이름" 이 나오면서 지표로 사용할 수 있다고 확인 할 수 있습니다.  
이제 사용자가 호출한 시점부터 호출된 정보가 이 마이크로미터에 기록되어지고 기록된 정보는 추후에 연결된 prometheus에서 자동으로 사용할 수 있게 됩니다.

/actuator/prometheus 라는 엔드포인트로 접속하게 되면 해당 메소드가 몇번 호출되었는지 확인 할 수 있는 지표를 확인 할 수 있습니다.

# Prometheus 
- ### Metrics 를 수집하고 모니터링 및 알람에 사용되는 오픈소스 애플리케이션
- ### 2016년부터 CNCF에서 관리되는 2번째 공식 프로젝트
  - ### Level DB -> Time Series Database(TSDB)
- ### Pull 방식의 구조와 다양한 Metric Exporter 제공
- ### 시계열 DB에 Metircs 저장 -> 조회 가능 (Query)

# Grafana
- ### 데이터 시각화, 모니터링 및 분석을 위한 오픈소스 애플리케이션
- ### 시계열 데이터를 시각화 하기 위한 대시보드 제공

# Prometheus Grafana 설치
- https://prometheus.io/download/
- https://grafana.com/grafana/download

## prometheus.yml
### targer을 지정해줘야합니다. 정보를 수집하고자하는 액츄에이터의 주소를 입력해주면 됩니다.
```yaml
# my global config
global:
  scrape_interval: 15s # Set the scrape interval to every 15 seconds. Default is every 1 minute.
  evaluation_interval: 15s # Evaluate rules every 15 seconds. The default is every 1 minute.
  # scrape_timeout is set to the global default (10s).

# Alertmanager configuration
alerting:
  alertmanagers:
    - static_configs:
        - targets:
          # - alertmanager:9093

# Load rules once and periodically evaluate them according to the global 'evaluation_interval'.
rule_files:
# - "first_rules.yml"
# - "second_rules.yml"

# A scrape configuration containing exactly one endpoint to scrape:
# Here it's Prometheus itself.
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: "prometheus"

    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.

    static_configs:
      - targets: ["localhost:9090"]
  - job_name: 'user-service'
    scrape_interval: 15s
    metrics_path: '/user-service/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8000']
  - job_name: 'order-service'
    scrape_interval: 15s
    metrics_path: '/order-service/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8000']
  - job_name: 'apigateway-service'
    scrape_interval: 15s
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8000']


```
타겟을 지정하는 방법은 다음과 같습니다. 다운 받은 prometheus.yml 을 열어 scrape_configs: 하위에 위와 같이 작성해주면 됩니다.  
- job_name: 'user-service'  
  scrape_interval: 15s  
  metrics_path: '/user-service/actuator/prometheus'  (여기 정보를 수집한다.)
  static_configs:
  - targets: ['localhost:8080']  (spring-gateway 도메인)

## prometheus 실행 방법  
./prometheus --config.file=prometheus.yml

## grafana 실행 방법  
./bin/grafana-server

# Grafana Dashboard
- JVM(Micrometer)
- Prometheus
- Spring Cloud Gateway

127.0.0.1:3000 번으로 접속하면 로그인 화면이 나온다. 기본 값 admin, admin으로 로그인한다.  
DATA SOURCES -> Prometheus -> URL : 127.0.0.1:9000 입력 -> Dashboards 클릭 -> import 클릭  
-> JVM(11892), Prometheus 2.0 Overview(3662), Spring Cloud Gateway(11506) 아이디 추가 -> Browse 클릭 후 확인  

### Spring Cloud Gateway 클릭후 
#### Total Requests Served -> Edit 버튼 클릭 후
sum(spring_cloud_gateway_requests_seconds_count{job=~"apigateway-service"}) 이렇게 수정 한다.  
spring_cloud_gateway_requests_seconds_count == /actuator/prometheus 에서 확인할 수 있는 지표 이름이다.
#### Total Successful Requests Served
sum(spring_cloud_gateway_requests_seconds_count{outcome="SUCCESSFUL", job=~"apigateway-service"})

#### Total UnSuccessful Requests Served
sum(spring_cloud_gateway_requests_seconds_count{outcome!="SUCCESSFUL", job=~"apigateway-service"})