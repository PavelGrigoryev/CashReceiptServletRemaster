package ru.clevertec.cashreceipt.servletremaster.dao;

import ru.clevertec.cashreceipt.servletremaster.model.DiscountCard;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DiscountCardDao {

    List<DiscountCard> findAll(Integer pageNumber, Integer pageSize) throws SQLException;

    Optional<DiscountCard> findById(Long id) throws SQLException;

    DiscountCard save(DiscountCard discountCard) throws SQLException;

    Optional<DiscountCard> findByDiscountCardNumber(String discountCardNumber) throws SQLException;

    Optional<DiscountCard> delete(Long id) throws SQLException;

}
