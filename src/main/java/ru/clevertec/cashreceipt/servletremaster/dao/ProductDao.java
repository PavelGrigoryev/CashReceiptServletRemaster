package ru.clevertec.cashreceipt.servletremaster.dao;

import ru.clevertec.cashreceipt.servletremaster.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProductDao {

    List<Product> findAll(Integer pageNumber, Integer pageSize) throws SQLException;

    Optional<Product> findById(Long id) throws SQLException;

    Product save(Product product) throws SQLException;

    Product update(Product product) throws SQLException;

    Optional<Product> delete(Long id) throws SQLException;

}
