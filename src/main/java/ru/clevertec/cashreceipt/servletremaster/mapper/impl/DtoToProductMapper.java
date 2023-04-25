package ru.clevertec.cashreceipt.servletremaster.mapper.impl;

import ru.clevertec.cashreceipt.servletremaster.dto.ProductDto;
import ru.clevertec.cashreceipt.servletremaster.mapper.Mapper;
import ru.clevertec.cashreceipt.servletremaster.model.Product;

public class DtoToProductMapper implements Mapper<ProductDto, Product> {

    @Override
    public Product apply(ProductDto productDto) {
        return Product.builder()
                .id(productDto.getId())
                .quantity(productDto.getQuantity())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .total(productDto.getTotal())
                .promotion(productDto.getPromotion())
                .build();
    }

}
