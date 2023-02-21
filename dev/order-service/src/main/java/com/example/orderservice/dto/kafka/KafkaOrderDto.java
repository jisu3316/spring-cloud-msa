package com.example.orderservice.dto.kafka;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KafkaOrderDto {
    private Schema schema;
    private Payload payload;
}
