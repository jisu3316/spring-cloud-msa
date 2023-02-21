package com.example.orderservice.dto.kafka;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Payload {
    private String order_id;
    private String user_id;
    private String product_id;
    private int qty;
    private int unit_price;
    private int total_price;

    @Builder
    public Payload(String order_id, String user_id, String product_id, int qty, int unit_price, int total_price) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.product_id = product_id;
        this.qty = qty;
        this.unit_price = unit_price;
        this.total_price = total_price;
    }
}
