package com.example.userservice.service;

import com.example.userservice.domain.UserEntity;
import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {

//        ModelMapper mapper = new ModelMapper();
//        userDto.setUserId(UUID.randomUUID().toString());
//        //완전하게 맞아 떨어져야지 데이터 변환이 일어난다.
//        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        UserEntity userEntity = userDto.toEntity();
        userEntity.createUserId(UUID.randomUUID().toString());
        userEntity.encryptedPwd("encrypted_password");

        return UserDto.from(userRepository.save(userEntity));
    }
}
