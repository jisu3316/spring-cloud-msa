package com.example.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Greeting {

    @Value("${greeting.message}")
    private String message;
}
