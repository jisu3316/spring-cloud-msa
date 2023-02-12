package com.example.orderservice.dto.response;

import com.example.orderservice.domain.OrderEntity;
import com.example.orderservice.dto.OrderDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ResponseOrder {
    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;
    private Date createAt;

    private String orderId;

    public static ResponseOrder from(OrderDto dto) {
        return new ResponseOrder(dto.getProductId(), dto.getQty(), dto.getUnitPrice(), dto.getTotalPrice(), dto.getCreateAt(), dto.getOrderId());
    }

    public static ResponseOrder from(OrderEntity entity) {
        return new ResponseOrder(entity.getProductId(), entity.getQty(), entity.getUnitPrice(), entity.getTotalPrice(), entity.getCreateAt(), entity.getOrderId());
    }
}
