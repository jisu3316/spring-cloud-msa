package com.example.catalogservice.messagequeue;

import com.example.catalogservice.doamin.CatalogEntity;
import com.example.catalogservice.repository.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final CatalogRepository catalogRepository;

    // example-catalog-topic 에 메시지가 전달되면 호출되는 메서드이다.
    @KafkaListener(topics = "example-catalog-topic")
    public void updateQty(String kafkaMessage) {
        log.info("Kafka Message : -> {}", kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        CatalogEntity entity = catalogRepository.findByProductId((String) map.get("productId"));
        if (entity != null) {
            entity.modifyStock(entity.getStock() - (Integer) map.get("qty"));
            catalogRepository.save(entity);
        }
    }
}
