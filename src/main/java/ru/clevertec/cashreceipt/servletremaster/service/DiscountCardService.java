package ru.clevertec.cashreceipt.servletremaster.service;

import ru.clevertec.cashreceipt.servletremaster.dto.DiscountCardDto;

import java.util.List;

public interface DiscountCardService {

    List<DiscountCardDto> findAll(Integer pageNumber, Integer pageSize);

    DiscountCardDto findById(Long id);

    DiscountCardDto save(DiscountCardDto discountCardDto);

    DiscountCardDto findByDiscountCardNumber(String discountCardNumber);

    DiscountCardDto delete(Long id);

}
