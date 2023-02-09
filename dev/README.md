# Users Microservice

## Features
-  신규 회원 등록
- 회원 로그인
- 상세 정보 확인
- 회원 정보 수정/삭제
- 상품 주문
- 주문 내역 확인

## APIs

| 기능                   | URI(API Gate way 사용시)          | URI(API Gate way 미사용 시) | HTTP  <br/>Method |
|----------------------|--------------------------------|------------------------|-------------------|
| 사용자 정보 등록            | /user-service/users            | /users                 | POST              |
| 전체 사용자 조회            | /user-service/users            | /users                 | GET               |
| 사용자 정보, 주문<br/>내역 조회 | /user-service/users/{user_id}  | /users/{user_id}       | GET               |
| 작동 상태 확인             | /user-service/users/health_check | /users/health_check    | GET               |
| 환영 메세지               | /user-service/users/welcome    | /users/welcome         | GET               |

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

