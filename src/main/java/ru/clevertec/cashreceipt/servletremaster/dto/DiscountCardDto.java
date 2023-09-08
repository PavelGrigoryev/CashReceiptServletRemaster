package ru.clevertec.cashreceipt.servletremaster.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCardDto {

    private Long id;
    private String discountCardNumber;
    private BigDecimal discountPercentage;

}
