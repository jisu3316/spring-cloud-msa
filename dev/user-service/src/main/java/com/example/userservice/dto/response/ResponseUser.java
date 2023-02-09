package com.example.userservice.dto.response;

import com.example.userservice.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseUser {
    private final String email;
    private final String name;
    private final String userId;

    public static ResponseUser from(UserDto userDto) {
        return new ResponseUser(userDto.getEmail(), userDto.getName(), userDto.getUserId());
    }
}
