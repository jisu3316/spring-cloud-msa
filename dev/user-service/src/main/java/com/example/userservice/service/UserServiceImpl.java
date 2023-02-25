package com.example.userservice.service;

import com.example.userservice.feign.client.OrderServiceClient;
import com.example.userservice.domain.UserEntity;
import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.response.ResponseOrder;
import com.example.userservice.repository.UserRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final OrderServiceClient orderServiceClient;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException(username);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptedPwd(),
                true, true, true, true,
                new ArrayList<>());
    }

    @Override
    public UserDto createUser(UserDto userDto) {

//        ModelMapper mapper = new ModelMapper();
//        userDto.setUserId(UUID.randomUUID().toString());
//        //완전하게 맞아 떨어져야지 데이터 변환이 일어난다.
//        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
//        UserEntity userEntity = mapper.map(userDto, UserEntity.class);
        UserEntity userEntity = userDto.toEntity();
        userEntity.createUserId(UUID.randomUUID().toString());
        userEntity.encryptedPwd(passwordEncoder.encode(userDto.getPwd()));

        return UserDto.from(userRepository.save(userEntity));
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }

        UserDto userDto = UserDto.from(userEntity);


//        List<ResponseOrder> orders = null;
//        try {
//            orders = orderServiceClient.getOrders(userId);
//        } catch (FeignException e) {
//            log.error(e.getMessage());
//        }

        /* Using a feign client */
        /* Feign exception handling */
        /* ErrorDecoder */
//        List<ResponseOrder> orders = orderServiceClient.getOrders(userId);

        /* CircuitBreaker*/
        log.info("Before call orders microservice");
        CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
        List<ResponseOrder> orders = circuitbreaker.run(() -> orderServiceClient.getOrders(userId),
                throwable -> new ArrayList<>());
        log.info("After call orders microservice");
        userDto.setOrders(orders);

        return userDto;
    }

    @Override
    public Iterable<UserEntity> getUserByAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity entity = userRepository.findByEmail(email);
        if (entity == null) {
            throw new UsernameNotFoundException(email);
        }
        return UserDto.from(entity);
    }
}
