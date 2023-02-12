package com.example.catalogservice.dto.response;

import com.example.catalogservice.doamin.CatalogEntity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class ResponseCatalog {
    private String productId;
    private String productName;
    private Integer unitPrice;
    private Integer stock;
    private Date createAt;

    public static ResponseCatalog from(CatalogEntity entity) {
        return new ResponseCatalog(entity.getProductId(), entity.getProductName(), entity.getUnitPrice(), entity.getStock(), entity.getCreateAt());
    }
}
