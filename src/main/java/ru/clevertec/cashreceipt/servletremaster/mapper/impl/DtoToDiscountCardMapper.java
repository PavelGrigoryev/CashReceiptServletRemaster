package ru.clevertec.cashreceipt.servletremaster.mapper.impl;

import ru.clevertec.cashreceipt.servletremaster.dto.DiscountCardDto;
import ru.clevertec.cashreceipt.servletremaster.mapper.Mapper;
import ru.clevertec.cashreceipt.servletremaster.model.DiscountCard;

public class DtoToDiscountCardMapper implements Mapper<DiscountCardDto, DiscountCard> {

    @Override
    public DiscountCard apply(DiscountCardDto discountCardDto) {
        return DiscountCard.builder()
                .id(discountCardDto.getId())
                .discountCardNumber(discountCardDto.getDiscountCardNumber())
                .discountPercentage(discountCardDto.getDiscountPercentage())
                .build();
    }

}
