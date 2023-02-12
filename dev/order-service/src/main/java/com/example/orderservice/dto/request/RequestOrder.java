package com.example.orderservice.dto.request;

import com.example.orderservice.dto.OrderDto;
import lombok.Data;

@Data
public class RequestOrder {
    private String productId;
    private Integer qty;
    private Integer unitPrice;

    public OrderDto toDto(String userId) {
        return OrderDto.of(this.productId, this.qty, this.unitPrice, userId);
    }
}
