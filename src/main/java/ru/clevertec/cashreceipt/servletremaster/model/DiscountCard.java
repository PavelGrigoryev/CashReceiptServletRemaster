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
public class DiscountCard {

    private Long id;
    private String discountCardNumber;
    private BigDecimal discountPercentage;

}
