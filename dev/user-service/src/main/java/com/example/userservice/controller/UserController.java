package com.example.userservice.controller;

import com.example.userservice.domain.UserEntity;
import com.example.userservice.dto.Greeting;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.request.RequestUser;
import com.example.userservice.dto.response.ResponseUser;
import com.example.userservice.service.UserService;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    private final Environment env;
    private final Greeting greeting;
    private final UserService userService;

    public UserController(Environment env, Greeting greeting, UserService userService) {
        this.env = env;
        this.greeting = greeting;
        this.userService = userService;
    }

    @GetMapping("/health-check")
    public String status() {
        return "It`s Working in User Service"
                + ", PORT(local.server.port)= " + env.getProperty("local.server.port")
                + ", PORT(server.port)= " + env.getProperty("server.port")
                + ", token secret=" + env.getProperty("token.secret")
                + ", token expiration time=" + env.getProperty("token.expiration_time");
    }

    @GetMapping("/welcome")
    public String welcome() {
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@RequestBody RequestUser user) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUser.from(userService.createUser(user.toDto())));
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(ResponseUser.from(v));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        return ResponseEntity.status(HttpStatus.OK).body(ResponseUser.from(userService.getUserByUserId(userId)));
    }
}
