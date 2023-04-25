package ru.clevertec.cashreceipt.servletremaster.dao.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cashreceipt.servletremaster.config.YamlConfig;
import ru.clevertec.cashreceipt.servletremaster.dao.ProductDao;
import ru.clevertec.cashreceipt.servletremaster.model.Product;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class ProductDAOImpl implements ProductDao {

    private final Connection connection;

    public ProductDAOImpl() throws SQLException {
        Map<String, String> postgresqlMap = new YamlConfig().getYamlMap().get("postgresql");
        String url = postgresqlMap.get("url");
        String user = postgresqlMap.get("user");
        String password = postgresqlMap.get("password");
        try {
            Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            log.error(e.getMessage(), e);
        }
        connection = DriverManager.getConnection(url, user, password);
    }

    @Override
    public List<Product> findAll(Integer pageNumber, Integer pageSize) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product ORDER BY id LIMIT ? OFFSET ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, pageNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = getProductFromResultSet(resultSet);
                    products.add(product);
                }
            }
        }
        return products;
    }

    @Override
    public Optional<Product> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM product WHERE id = ?";
        Optional<Product> product = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    product = Optional.of(getProductFromResultSet(resultSet));
                }
            }
        }
        return product;
    }

    @Override
    public Product save(Product product) throws SQLException {
        String sql = "INSERT INTO product (quantity, name, price, total, promotion) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setProductValuesInStatement(preparedStatement, product);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                product.setId((long) id);
            }
        }
        return product;
    }

    @Override
    public Product update(Product product) throws SQLException {
        String sql = "UPDATE product SET quantity = ?, name = ?, price = ?, total = ?, promotion = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setProductValuesInStatement(preparedStatement, product);
            preparedStatement.setLong(6, product.getId());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                product.setId((long) id);
            }
        }
        return product;
    }

    @Override
    public Optional<Product> delete(Long id) throws SQLException {
        String sql = "DELETE FROM product WHERE id = ?";
        Optional<Product> product = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                product = Optional.of(getProductFromResultSet(resultSet));
            }
        }
        return product;
    }

    private Product getProductFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Integer quantity = resultSet.getInt("quantity");
        String name = resultSet.getString("name");
        BigDecimal price = resultSet.getBigDecimal("price");
        BigDecimal total = resultSet.getBigDecimal("total");
        Boolean promotion = resultSet.getBoolean("promotion");
        return new Product(id, quantity, name, price, total, promotion);
    }

    private void setProductValuesInStatement(PreparedStatement preparedStatement, Product product) throws SQLException {
        preparedStatement.setInt(1, product.getQuantity());
        preparedStatement.setString(2, product.getName());
        preparedStatement.setBigDecimal(3, product.getPrice());
        preparedStatement.setBigDecimal(4, product.getTotal());
        preparedStatement.setBoolean(5, product.getPromotion());
    }

}
