package com.example.userservice.controller;

import com.example.userservice.dto.Greeting;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.request.RequestUser;
import com.example.userservice.dto.response.ResponseUser;
import com.example.userservice.service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/user-service/health-check")
    public String status() {
        return String.format("It`s Working in User Service on PORT %s", env.getProperty("local.server.port"));
    }

    @GetMapping("/user-service/welcome")
    public String welcome() {
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/user-service/users")
    public ResponseEntity<Object> createUser(@RequestBody RequestUser user) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUser.from(userService.createUser(user.toDto())));
    }
}
