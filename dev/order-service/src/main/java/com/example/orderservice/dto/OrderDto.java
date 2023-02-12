package com.example.orderservice.dto;

import com.example.orderservice.domain.OrderEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class OrderDto {

    private String productId;
    private Integer qty;
    private Integer unitPrice;
    private Integer totalPrice;

    private String orderId;
    private String userId;

    private Date createAt;

    public static OrderDto of(String productId, Integer qty, Integer unitPrice, String userId) {
        return new OrderDto(productId, qty, unitPrice, null, null, userId, null);
    }
    public static OrderDto from(OrderEntity entity) {
        return new OrderDto(entity.getProductId(), entity.getQty(), entity.getUnitPrice(), entity.getTotalPrice(), entity.getOrderId(), entity.getUserId(), entity.getCreateAt());
    }

    public OrderEntity toEntity() {
        return OrderEntity.builder()
                .productId(this.productId)
                .qty(this.qty)
                .unitPrice(this.unitPrice)
                .orderId(this.orderId)
                .totalPrice(this.totalPrice)
                .userId(this.userId)
                .build();
    }
}
