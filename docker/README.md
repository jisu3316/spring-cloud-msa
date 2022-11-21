# Dokerfile을 통해 local에서 서버배포하기
로컬에서 도커 컨테이너를 이용해서 배포를 하며 정리를 해보겠습니다.  
이렇게 배포를 해두면 나중에 다른 환경, 다른 클라우드에 배포를 하더라도 도커이미지를 그대로 컨태이너화 시키면 배포를 할 수 있습니다.  
<br/> <br/>
# 배포할 컨테이너
- ### User Microservice
- ### Catalogs Microservice
- ### Orders Microservice
- ### Eureka
- ### API Gateway
- ### Configuration
- ### RabbitMQ - 다양한 마이크로서비스에 Configuration에 변화된 사항을 동시에 알려준다.
- ### MySQL 
- ### Kafka - 메세징기반 서비스를 통해 데이터를 보내준다.
- ### Zipkin - 마이크로서비스에서 분산 트래킹, 어떻게 서비스가 호출이 되었고 사용이 되었고 다음 서비스로써 어떤것이 사용되는것인지 확일할 수 있는 서비스
- ### Prometheus - 마이크로서비스 다양한 서버들의 리소스, 상태들을 확인할 수 있는 모니터링 서비스
- ### Grafana - 마이크로서비스 다양한 서버들의 리소스, 상태들을 확인할 수 있는 모니터링 서비스

<br/><br/>
# Create Bridge Network

- ## Bridge network
    - 아무런 설정없이 그냥 사용할 수 있는 것이 브릿지 네트워크 이다. 호스트 PC와 별도의 가상 네트워크를 만들고 가상의 네트워크에서 우리가 만들어 쓰고 있는 컨테이너들을 배치해놓고 쓰는 방식을 브릿지 네트워크라고 생각하면 된다. (우리가 만들었던 서비스만 사용할 수 있는 가상의 네트워크)
    - $ docker network create --driver bridge [브릿지 이름]
- ## Host network
    - 네트워크를 호스트로 설정하면 호스트의 네트워크 환경을 그대로사용
    - 포트 포워딩 없이 내부 어플리케이션 사용
- ## Non network
    - 네트워크를 사용하지 않음
    - IO 네트워크만 사용, 외부와 단절
<br/><br/>
## $ docker network create ecommerce-network - 도커 네트워크를 만든다.  

## $ docker network ls - 도커 네트워크 목록을 확인한다. 

## $ docker network create --gateway 172.18.0.1 --subnet 172.18.0.0/16 ecommerce-network   
여기서 gateway와 subnetmask를 지정하지 않고 만들수도 있지만 그랬을 경우에 나중에 자동으로 컨테이너의 IP가 할당되게 하는 옵션말고 IP Address를 지정해서 컨테이너를 띄울 수 있습니다. 그럴때 오류가 발생할 수 있습니다. 그래서 가급적이면 컨테이너에서 IP를 수동으로 할당할것을 대비해서 gateway, subnetmask를 같이 지정해주도록 합니다.

## $ docker network inspect ecommerce-network
네트워크의 상세정보를 확인할 수 있다. 
```
[
    {
        "Name": "ecommerce-network",
        "Id": "7db95049e212c325ce998ba9cd8a3613c88fb9acaa66d52857cfa9341d0c96aa",
        "Created": "2023-02-26T07:13:58.731339883Z",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "172.18.0.0/16",
                    "Gateway": "172.18.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {},
        "Options": {},
        "Labels": {}
    }
]
```
확인해보면 위에서 Subnet과 Gateway를 확인할 수 있다.  
Containers 는 이 네트워크에 추가되어진 컨테이너를 확인할 수 있다.  
이렇게 컨테이너에서 사용할 네트워크를 직접 할당하게되면 좋은점은 일반적인 컨테이너는 하나의 Guest OS라고 생각하면 된다.  
각각의 Guest OS마다 고유의 IP Address가 할당이 됩니다. 컨테이너들간의 이런 아이피주소를 이용해서 통신을 하게 되는데 만약 같은 네트워크에 포함된 컨테이너들끼리는 아이피주소외에도 컨테이너 아이디, 컨테이너 이름을 이용해서 통신을 할 수도 있습니다.   
이게 어떠한 장점이 되냐면 전체 어플리케이션을 구성하는데 위에서 말한듯이 12개의 서비스를 구성하게 됩니다.  
그러면 각각의 서비스마다 IP Address를 할당 받게 되는데 예를 들어서 Kafka가 172.17.0.2라고 가정하고 순차적으로 할당된다고 가정하면 마지막은 172.17.0.13가 할당되게 됩니다.
이러면 Kafka에서 마지막 서비스에 통신하는데  172.17.0.13 로 하면 문제가 없습니다. 근데 여기서 고려해야할 사항이 도커에서 컨테이너로 배포하다보면 IP가 순차적으로 비어 있는 IP로 할당되게 됩니다.  
만에 하나 우리가 설정했던 서버의 Kafka의 IP Address가 172.17.0.2이지만 어떤 환경에서는 다른 아이디로 할당되게 되면 외부에서 접속하는 다른 서비스들이 변경된 아이피에따라 설정을 해주어야 합니다.  
여기서 같은 네트워크를 사용하도록 설정을 해놓으면 Kafka를 호출함에 있어서 IP가 아니라 hostname, CONTAINER ID, CONTAINER NAMES을 가지고 호출할 수 있습니다.  
그런데 IP가 변경되었다는 것은 컨테이너 아이디가 새롭게 발부되었다는 얘기이기 때문에 CONTAINER ID, CONTAINER NAMES등으로 찾지 못하게 됩니다. 그렇기 때문에 컨테이너를 생성할때 --name 옵션을 통해 CONTAINER NAMES을 할당해 IP가 변경되어진다 하더라도 다른쪽에 있는 마이크로서비스라던가 서버들에서도 우리가 필요했던 특정한 서비스,서버에 접속하기 위해서 name만 가지고 접속하게되면 바로 연결할수 있게 됩니다.

<br/><br/><br/>
# RabbitMQ
Configuration server의 변경된 사항을 모든 마이크로서비스에 동시에 업데이트 시켜주기 위해서 스프링 클라우드 버스라는 기능을 사용했습니다. 스프링 클라우드 버스에서 사용할 수 있는 MQ서버로서 RabbitMQ를 사용했습니다.
<br/><br/>
## Docker Container 
https://registry.hub.docker.com/_/rabbitmq/tags
<br/><br/>
## Docker 명령어 
docker run -d --name rabbitmq --network ecommerce-network \

-p 15672:15672 -p 5672:5672 -p 15671:15671 -p 5671:5671 -p 4369:4369 \

-e RABBITMQ_DEFAULT_USER=guest \

-e RABBITMQ_DEFAULT_PASS=guest rabbitmq:management  
<br/>

- ### -d : 백 그라운드 모드로 실행

- ### --name rabbitmq
    - 네트워크에서 서로 통신 가능한 고유한 이름을 부여한다.(컨테이너의 고유한 이름)
<br/><br/>
- ### --network ecommerce-network
  - 위에서 만든 도커 네트워크를 지정해주는 항목이다. 이 네트워크 항목을 지정해주지 않으면 기본적으로 도커가 가지고 있는 브릿지 네트워크를 가져와서 사용하게 됩니다. 이렇게 되면 다른 네트워크에 저장되면서 통신할 수 있는 방법이 없어지므로 네트워크를 지정해주도록 합니다.
<br/><br/>
- ### -p 15672:15672 -p 5672:5672 -p 15671:15671 -p 5671:5671 -p 4369:4369 \ 
  - 포트 포워딩에 관련된 항목이다. RabbitMQ에서 사용하고 있는 항목들이 콜론뒤에 오는 컨테이너 포트이고, 실제 우리가 사용하고 있는 호스트 PC에서 사용할 포트를 포워딩을 걸어줌으로써 호스트 PC뿐만 아니라 다른 쪽에서도 사용할 때도 크게 무리없이 해당하는 포트에 접속할 수 있도록 연결해놓습니다.
<br/><br/>
- ### -e RABBITMQ_DEFAULT_USER=guest \ -e RABBITMQ_DEFAULT_PASS=guest  
    - 접속을 위한 아이디와 패스워드이다.
<br/><br/>
- ### rabbitmq:management 
    - 실제로 호출하려는 이미지의 이름이다.

위의 명령어를 치고 docker network inspect ecommerce-network 확인해보면
```
[
    {
        "Name": "ecommerce-network",
        "Id": "7db95049e212c325ce998ba9cd8a3613c88fb9acaa66d52857cfa9341d0c96aa",
        "Created": "2023-02-26T07:13:58.731339883Z",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "172.18.0.0/16",
                    "Gateway": "172.18.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "c686fb2478dbba01967a0ef0b7c948af0c9163d825829baf2fe624f53421ff25": {
                "Name": "rabbitmq",
                "EndpointID": "bd0c40032a9392056f3590245f021fdb621903bad52097bdd5385880a01430eb",
                "MacAddress": "02:42:ac:12:00:02",
                "IPv4Address": "172.18.0.2/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```
Contianers 에 'IPv4Address": "172.18.0.2/16" 가 2번이 할당된것을 확인할 수 있습니다.

<br/><br/>
# Configuration Service

## Dockerfile
```
FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY apiEncryptionKey.jks apiEncryptionKey.jks
COPY target/config-service-1.0.jar ConfigServer.jar
ENTRYPOINT ["java","-jar","ConfigServer.jar"]
```
VOLUME은 가상의 디렉토리이다. 컨테이에 사용할 tmp라는 디렉토리이다.  
COPY apiEncryptionKey.jks apiEncryptionKey.jks 는 DB 정보 및 JWT키를 암호화 시키기 위한 키입니다. 현재 루트경로애 있는 apiEncryptionKey.jks 를 컨테이너의 루트 디렉토리에 복사해달라는 뜻입니다.  
COPY target/config-service-1.0.jar ConfigServer.jar 이거또한 target 밑에 있는 jar파일을 Configserver.jar라는 이름으로 복사한다는 뜻이다.  
ENTRYPOINT ["java","-jar","ConfigServer.jar"] 도커 컨테이가 실행할 마지막 커맨드이다. 컨테이너안에 있는 ConfigServer.jar 파일을 실행한다는 의미이다.  

## pom.xml
```
<version>1.0</version>
```
 mvn clean compile package -DskipTests=true   명령어로 빌드를 해줍니다.  

 그리고 현재 스프링 부트 어플리케이션 루트경로로 이동해준 후 다음 명령어로 도커이미지를 만들어 줍니다.

 ```
 docker build -t jisu3268/config-service:1.0 .
 ```
 위에서 . 은 현재 위치에 있는 도커파일을 실행한다는 의미입니다.  


## 도커 컨테이너 만들고 실행하기
 ```
 docker run -d -p 8888:8888 --network ecommerce-network -e "spring.rabbitmq.host=rabbitmq" -e "spring.profiles.active=default" --name config-service jisu3268/config-service:1.0
 ```

### docker run -d -p 8888:8888  
- 지금까지 컨피그 서비스의 포트 번호는 8888이였습니다. 그렇기 때문에 다른호스트에서도 사용할때도 8888로 포트포워딩을 걸어줍니다.  
### --network ecommerce-network  
- 네트워크 설정을 해줍니다. 이렇게 해야 위에서 만든 RabbitMQ와 같은 네트워크에서 실행 되게 됩니다.

### -e "spring.rabbitmq.host=rabbitmq" -e "spring.profiles.active=default"
도커에서 -e 옵션은 environment의 약자로 컨테이너 내에서 사용할 환경변수 설정을 해주는 옵션입니다.  
기존의 컨피그서비스의 application.yml을 살펴보겠습니다.

```
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
```
위와같이 RabbiMQ 설정이 되어 있는데 여기서 127.0.0.1 은 로컬호스트입니다. 이 호스트는 컨피그서비스 자체의 IP Adrress가 됩니다. 위에서 도커파일로 빌드한 rabbitMQ와 다른 IP를 가지게 됩니다. 127.0.0.1로 하게 되면 config-service내에 rabbitMQ가 있어야 한다는 의미이지만 두 서비스는 완전 별개의 시스템입니다.  
그래서 172.18.0.2로 넣어줄수도있지만 이 IP는 변경 될 수 있습니다. 도커는 기본적으로 172로 시작하지만 다른 컨테이너 가상화 기술을 사용한다면 172로 시작 안 할수도있는 문제점이 있습니다.  
위에서 같은 도커 네트워크를 사용하게 된다면 CONTAINER ID, CONTAINER NAMES 로 호출 할 수 있다는것을 배웠습니다. 그러므로 환경변수에 rabbitmq로 설정해줌으로써 IP대신 CONTAINER NAMES로  나중에 변경 사항 없이 편하게 사용할 수 있습니다.  

## --name config-service 
위와 같이 다른 서비스에서 config-service를 이용할때 IP대신 이름을 사용할 수 있도록 이름을 지정해줍니다.

## jisu3268/config-service:1.0
가장 중요한 컨테이너의 이미지 값, 태그 이름 입니다.

# 도커 네트워크 확인하기

## docker network inspect ecommerce-network

```
[
    {
        "Name": "ecommerce-network",
        "Id": "7db95049e212c325ce998ba9cd8a3613c88fb9acaa66d52857cfa9341d0c96aa",
        "Created": "2023-02-26T07:13:58.731339883Z",
        "Scope": "local",
        "Driver": "bridge",
        "EnableIPv6": false,
        "IPAM": {
            "Driver": "default",
            "Options": {},
            "Config": [
                {
                    "Subnet": "172.18.0.0/16",
                    "Gateway": "172.18.0.1"
                }
            ]
        },
        "Internal": false,
        "Attachable": false,
        "Ingress": false,
        "ConfigFrom": {
            "Network": ""
        },
        "ConfigOnly": false,
        "Containers": {
            "b2aff4dbc7c718f5077525dd7e7dcd4d9771c2ea4ea956d4d81deb0939385953": {
                "Name": "config-service",
                "EndpointID": "f50a3e18bb4fbcb1a619f954f8cddb1f93461175c76c55bafebf1eaae8fcebdd",
                "MacAddress": "02:42:ac:12:00:03",
                "IPv4Address": "172.18.0.3/16",
                "IPv6Address": ""
            },
            "c686fb2478dbba01967a0ef0b7c948af0c9163d825829baf2fe624f53421ff25": {
                "Name": "rabbitmq",
                "EndpointID": "bd0c40032a9392056f3590245f021fdb621903bad52097bdd5385880a01430eb",
                "MacAddress": "02:42:ac:12:00:02",
                "IPv4Address": "172.18.0.2/16",
                "IPv6Address": ""
            }
        },
        "Options": {},
        "Labels": {}
    }
]
```
위에서 먼저 만든 rabbitmq가 178.18.0.2, config-service가 172.18.0.3 으로 IP가 할당된것을 확인할 수 있습니다.