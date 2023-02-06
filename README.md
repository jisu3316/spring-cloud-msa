# Monolith Architecture 란?
- 모든 업무 로직이 하나의 애플리케이션 형태로 패키지 되어 서비스
- 애플리케이션에서 사용하는 데이터가 한곳에 모여 참조되어 서비스되는 형태
- 하나의 시스템에 애플리케이션을 구성하는 모든 서비스와 요소들이 패키징되어 하나의 서비스가 되어야 하기 때문에 시스템의 일부만 수정된다 하더라도 애플리케이션을 다시 빌드하고 패키징해야한다는 단점이 있습니다.


# Microservice 란?  
마이크로서비스의 창시자인 Martin Fowler가 정의한 내용입니다.  
마이크로서비스들이 가져야할 특징들은 최소한의 중앙집중식 관리가 되어야 하고 서로 다른 프로그래밍 언어와 서로 다른 데이터 저장 기술을 사용할 수 있다.
기존의 모놀리스와 마이크로서비스의 가장 큰 차이점은 하나의 서비스를 구성하고 있는 크기 입니다. 그 서비스의 크기가 도메인의 특성을 고려해서
경계를 구분해야하고 구분된 서비스들은 독립적인 언어와 독립적인 데이터베이스를 사용할 수 있다라고 했습니다.
   
2010년 이후에서부터 Resilient(탄력적인) / Anti-Fragile (Fragile 깨지기 쉬운의 반대말), Cloud Native 로 시스템이 구축되고 있었습니다.
시스템은 클라우드로 변경되었고 지속적인 개선 및 변경 사항이 있어도 탄력적으로 시스템이 운영될 수 있게 되었습니다.

# Antifragile
Anti-Fragile 의 핵심이 되는 4가지의 특성이 있습니다.
1. Auto scaling : 자동 확장성을 갖는다는 특징입니다.  
예를 들어 쇼핑몰은 가정의 달 5월이나 연말, 크리스마스 등 특수한 이벤트가 있는 날에는 서버의 운영 갯수를 늘리고 아닌 날에는 줄이고 이러한 행동을 관리자에 의해서가 아닌 데이터베이스의 사용량이나 서버의 부하에 따라 자동으로 처리될 수 있는 개념입니다. 
2. Microservices : 전체 서비스를 구축하고 있는 개별적인 모듈이나 기능을 배포하고 운영할 수 있도록 세분화된 서비스라고 할 수 있다.
3. chaos engineering : 시스템이 예측하지 못한 상황에서도 버틸 수 있고 신뢰성을 쌓기 위해 운영 중인 소프트웨어의 실행하는 방법이나 규칙을 의미한다.
4. Continuous deployments : 지속적인 통합, 지속적인 배포라는 뜻의 CI/CD를 수백수천 개의 마이크로 서비스에 적용할 수 있습니다. 


# Cloud Native Architecture  
### - 확장 가능한 아키텍처
  - 시스템의 수평적 확장에 유연
  - 확장된 서버로 시스템의 부하 분산, 가용성보장
  - 시스템또는, 서비스 애플리케이션 단위의 패키지(컨테이너 기반 패키지)
  - 모니터링
### - 탄력적 아키텍처
  - 서비스 생성, 통합, 배포, 비즈니스 환경 변화에 대응 시간 단축
  - 분할된 서비스 구조
  - 무상태 통신 프로토콜
  - 서비스의 추가와 삭제 자동으로 감지
  - 변경된 서비스 요청에 따라 사용자 요청 처리(동적 처리)
### - 장애 격리(Fault isolation)
  - 특정 서비스에 오류가 발생해도 다른 서비스에 영향을 주지 않음

# Cloud Native Application
## 1. CI/CD
### - 지속적인 통합 CI(Continuous Integration)
  - 통합 서버, 소스관리(SCM), 빌드 도구, 테스트 도구 
  - ex)Jenkins, Team CI, Travis CI
### - 지속적인 배포
  - Continuous Delivery    (수동 반영)
  - Continuous Deployment  (자동 반영)
  - Pipe line
  - 배포 전략에는 지정한 서버 또는 특정한 user에게만 배포했다가 정상적이면 전체를 배포하는 카나리 배포 전략이 있고  
신 버전을 배포하고 일제히 전환하여 모든 연결을 신 버전을 바라보게 하는 전략이다. 구 버전, 신 버전 서버를 동시에 나란히 구성하여 배포 시점에 트래픽이 일제히 전환된다.

## 2. DevOps
Development 와 Operations 가 합쳐진 용어이다. 개발 조직과 운영 조직을 통합한 의미이다.

## 3. Container 가상화
Cloud Native Application 을 구성하는 가장 큰 특징입니다.  
기존 로컬 환경에서 운영하고 개발하던 시스템을 클라우드 환경으로 이전해서 적은 비용으로 탄력성있는 시스템을 구축할 수 있는 배경이 컨테이너 가상화 기술이라고 할 수 있다.  
하드웨어 가상화, 서버 가상화에 비해서 적은 리소스를 사용하여 가상화 서비스를 구축할 수 있다.  
공통적인 라이브러리나 리소스들을 공유해서 사용한다.  
각자 필요한 부분에 대해서만 독립적인 영역에다가 실행할 수 있는 구조이다.  
그러므로 가볍고 빠르게 운영할 수 있다.

# 12 Factors(https://12factor.net)
클라우드 어플리케이션을 개발하거나 운영할때 고려할 사항에대해서 정리한 헤로쿠에서 제시한 12가지 항목입니다.
1. BASE CODE : 자체 레포지토리에 저장된 자체 마이크로서비스에 대해서 코드 베이스를 뜻합니다.코드의 형상 관리가 중효하기때문에 가장 중요한 항목으로 꼽습니다.
2. DEPENDENCY ISOLATION : 마이크로 서비스는 자체 종속성을 가지고 패키지 되어 있어서 자체 시스템에 영향을 안주고 변경하고 수정될 수 있어야 한다는 뜻입니다.
3. CONFIGURATION : 구성정보 , 코드 외부에서 구성 관리 도구를 통해서 마이크로 서비스에 필요한 어떠한 작업들을 제어하는것을 의미합니다.
4. LINKABLE BACKING SERVICES : 서비스 지원, 데이터베이스, 메세지 서비스 브로커등 마이크로 서비스가 가져야할 기능을 제공한다. 
5. STAGES OF CREATION : 빌드, 릴리즈, 실행환경을 각각 분리하는것을 말한다.
6. STATELESS PROCESSES : 각 각의 마이크로서비스는 실행중인 다른 서비스와 분리된 채 각 각의 프로세스에서 운영될 수 있어야한다.
7. PORT BINDING : 각각의 마이크로서비스는 자체포트에서 노출되는 인터페이스 및 기능과 함께 자체 포함되어 있는 기능이 있어야한다. 이렇게 하면 다른 마이크로서비스와 격리가 가능하기 때문이다.
8. CONCURRENCY : 동시성, 마이크로 서비스는 아주 많은 수의 인스턴스를 확장해 나갑니다. 하나의 서비스가 여러가지 인스턴스에 동일한 형태로 복사가 되어서 운영됨으로써 부하 분산을 이뤄낼수있고 따라서 동일한 그 서비스가 여러 인스턴스에 나눠서 실행되기 때문에 동시성이 있어야한다.
9. DISPOSABILITY : 서비스 인스턴스 자체가 삭제가 가능해야한다. 
10. DEVELOPMENT & PRODUCTION PARITY : 개발 단계와 프로덕션 단계를 구분할수 있어야한다. 
11. LOGS : 마이크로서비스에 의해서 생성된 로그를 이벤트 스트림으로 처리해야한다. 즉, 하나의 시스템 안에서 구성된 로그를 서비스와 분리시켜 어플리케이션이 실행되지 않는 상태라하여도 로그를 정상적으로 작동 되어야 한다.
12. ADMIN PROCESSES FOR EVENTUAL PROCESSES : 현재 운영되고 있는 모든 마이크로서비스들을 어떤 상태로 사용되고 있으며, 리소스가 어떻게 쓰고 있는지 파악하기 위한 관리 도구가 있어야한다.

최근에는 위의 12가지 외에 고려사항이 3가지가 추가되었다한다.
1. API first
2. Telemetry
3. Authentication and authorization 

# Spring Cloud 란?
https://spring.io/projects/spring-cloud  스프링 클라우드가 지원하는 서비스들을 확인할 수 있다.  
Spring Cloud는 마이크로서비스의 개발, 배포, 운영에 필요한 아키텍처를 쉽게 구성할 수 있도록 지원하는
Spring Boot기반의 프레임워크입니다. 다시 말해 MSA구성을 지원하는 Springboot기반 Framework 입니다.  

프로젝트 구성
- Spring Cloud Config  
- Spring Cloud Netflix  
- Spring Cloud Security  
- Spring Cloud Sleuth  
- Spring Cloud Starters  
- Spring Cloud GateWay  
- Spring Cloud OpenFeign  

### Centralized configuration management
#### Spring Cloud Config Server 
Config Server 를 통하여 다양한 마이크로서비스에서 사용할 수 있는 어떤 정보들을 클라우드 컨피그라는 외부 저장소 환경설정 정보를 저장할 수 있습니다. 게이트 웨이 IP, 서버에 대한 토큰 정보 등 이런것들을 한곳의 저장소에 몰아넣고 나머지 마이크로 서비스에서 해당 값들을 참조해서 사용하는 방식이다.

### Location transparency 
#### Naming Server(Netflix Eureka)
서비스에 등록과 위치정보 확인, 검색과 같은 서비스를 위해서 Netflix Eureka 를 사용한다.

### Load Distribution (Load Balancing) 
##### Ribbon(Client Side), Spring Cloud GateWay
서버의 들어왔던 요청정보를 분산하기위한 용도로서 로드발란서이라던가 게이트웨이기능으로서 위 두가지를 사용한다.
기존에는 Netflix zuul 이나 Ribbon 을 사용했지만 최근들어서는 Spring Cloud GateWay 를 사용하는 추세이므로 Spring Cloud GateWay를 사용하겠습니다.

### Easier REST Clients
#### Feign Client  
각각의 마이크로서비스가 통신을 위해서는 Rest Template 나 Feign Client 를 이용해서 데이터 통신을 하게 됩니다.

### Visibility and monitoring (시각화와 모니터링)
#### Zipkin Distributed Tracing, Netflix API gateway

### Fault Tolerance
#### Hystrix
장애가 났을경우 빠르게 복구할수있는 회복성패턴이라는것이 있는데 넷플릭스에 있는 Hystrix 를 사용하겠습니다. 