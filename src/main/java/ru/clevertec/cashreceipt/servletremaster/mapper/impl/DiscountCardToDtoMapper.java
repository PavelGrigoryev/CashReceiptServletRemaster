package ru.clevertec.cashreceipt.servletremaster.mapper.impl;

import ru.clevertec.cashreceipt.servletremaster.dto.DiscountCardDto;
import ru.clevertec.cashreceipt.servletremaster.mapper.Mapper;
import ru.clevertec.cashreceipt.servletremaster.model.DiscountCard;

public class DiscountCardToDtoMapper implements Mapper<DiscountCard, DiscountCardDto> {

    @Override
    public DiscountCardDto apply(DiscountCard discountCard) {
        return DiscountCardDto.builder()
                .id(discountCard.getId())
                .discountCardNumber(discountCard.getDiscountCardNumber())
                .discountPercentage(discountCard.getDiscountPercentage())
                .build();
    }

}
