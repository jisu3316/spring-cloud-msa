package com.example.userservice.dto;

import com.example.userservice.domain.UserEntity;
import com.example.userservice.dto.response.ResponseOrder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String email;

    private String name;

    private String pwd;

    private String userId;

    private LocalDate createdAt;

    private String encryptedPwd;

    private List<ResponseOrder> orders;

    public UserDto(String email, String name, String pwd) {
        this.email = email;
        this.name = name;
        this.pwd = pwd;
    }

    public UserDto(Long id, String email, String name, String userId) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userId = userId;
    }

    public static UserDto from(UserEntity user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getUserId()
        );
    }
    public UserEntity toEntity() {
        return UserEntity.builder()
                .email(this.email)
                .name(this.name)
                .build();
    }
}
