package ru.clevertec.cashreceipt.servletremaster.mapper.impl;

import ru.clevertec.cashreceipt.servletremaster.dto.ProductDto;
import ru.clevertec.cashreceipt.servletremaster.mapper.Mapper;
import ru.clevertec.cashreceipt.servletremaster.model.Product;

public class ProductToDtoMapper implements Mapper<Product, ProductDto> {

    @Override
    public ProductDto apply(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .quantity(product.getQuantity())
                .name(product.getName())
                .price(product.getPrice())
                .total(product.getTotal())
                .promotion(product.getPromotion())
                .build();
    }

}
