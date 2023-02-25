package com.example.orderservice.service;

import com.example.orderservice.domain.OrderEntity;
import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final KafkaProducer kafkaProducer;
    private final OrderProducer orderProducer;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        log.info("Before add orders data");
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        /* snd this order th the kafka*/
//        kafkaProducer.send("example-catalog-topic", orderDto);
//        orderProducer.send("orders", orderDto);
        log.info("Before added orders data");
        return OrderDto.from(orderRepository.save(orderDto.toEntity()));
//        return orderDto;
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        return OrderDto.from(orderRepository.findByOrderId(orderId));
    }

    @Override
    public Iterable<OrderEntity> getOrderByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
