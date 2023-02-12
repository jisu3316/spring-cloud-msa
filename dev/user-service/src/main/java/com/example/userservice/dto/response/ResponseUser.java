package com.example.userservice.dto.response;

import com.example.userservice.domain.UserEntity;
import com.example.userservice.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {
    private final String email;
    private final String name;
    private final String userId;

    List<ResponseOrder> orders;

    public static ResponseUser from(UserDto userDto) {
        return new ResponseUser(userDto.getEmail(), userDto.getName(), userDto.getUserId(), userDto.getOrders());
    }

    public static ResponseUser from(UserEntity userEntity) {
        return new ResponseUser(userEntity.getEmail(), userEntity.getName(), userEntity.getUserId(), null);
    }
}
