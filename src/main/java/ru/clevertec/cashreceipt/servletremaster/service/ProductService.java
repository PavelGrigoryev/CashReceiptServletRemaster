package ru.clevertec.cashreceipt.servletremaster.service;

import ru.clevertec.cashreceipt.servletremaster.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> findAll(Integer pageNumber, Integer pageSize);

    ProductDto findById(Long id);

    ProductDto save(ProductDto productDto);

    ProductDto update(Long id, Integer quantity);

    ProductDto delete(Long id);

}
