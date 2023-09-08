package ru.clevertec.cashreceipt.servletremaster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    private Long id;
    private Integer quantity;
    private String name;
    private BigDecimal price;
    private BigDecimal total;
    private Boolean promotion;

}
