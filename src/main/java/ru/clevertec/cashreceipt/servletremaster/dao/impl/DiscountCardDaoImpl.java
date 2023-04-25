package ru.clevertec.cashreceipt.servletremaster.dao.impl;

import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cashreceipt.servletremaster.config.YamlConfig;
import ru.clevertec.cashreceipt.servletremaster.dao.DiscountCardDao;
import ru.clevertec.cashreceipt.servletremaster.model.DiscountCard;

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
public class DiscountCardDaoImpl implements DiscountCardDao {

    private final Connection connection;

    public DiscountCardDaoImpl() throws SQLException {
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
    public List<DiscountCard> findAll(Integer pageNumber, Integer pageSize) throws SQLException {
        List<DiscountCard> discountCards = new ArrayList<>();
        String sql = "SELECT * FROM discount_card ORDER BY id LIMIT ? OFFSET ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, pageSize);
            preparedStatement.setInt(2, pageNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    DiscountCard discountCard = getDiscountCardFromResultSet(resultSet);
                    discountCards.add(discountCard);
                }
            }
        }
        return discountCards;
    }

    @Override
    public Optional<DiscountCard> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM discount_card WHERE id = ?";
        Optional<DiscountCard> discountCard = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    discountCard = Optional.of(getDiscountCardFromResultSet(resultSet));
                }
            }
        }
        return discountCard;
    }

    @Override
    public DiscountCard save(DiscountCard discountCard) throws SQLException {
        String sql = "INSERT INTO discount_card (discount_card_number, discount_percentage) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setDiscountCardValuesInStatement(preparedStatement, discountCard);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                discountCard.setId((long) id);
            }
        }
        return discountCard;
    }

    @Override
    public Optional<DiscountCard> findByDiscountCardNumber(String discountCardNumber) throws SQLException {
        String sql = "SELECT * FROM discount_card WHERE discount_card_number = ?";
        Optional<DiscountCard> discountCard = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, discountCardNumber);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    discountCard = Optional.of(getDiscountCardFromResultSet(resultSet));
                }
            }
        }
        return discountCard;
    }

    @Override
    public Optional<DiscountCard> delete(Long id) throws SQLException {
        String sql = "DELETE FROM discount_card WHERE id = ?";
        Optional<DiscountCard> discountCard = Optional.empty();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                discountCard = Optional.of(getDiscountCardFromResultSet(resultSet));
            }
        }
        return discountCard;
    }

    private DiscountCard getDiscountCardFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String discountCardNumber = resultSet.getString("discount_card_number");
        BigDecimal discountPercentage = resultSet.getBigDecimal("discount_percentage");
        return new DiscountCard(id, discountCardNumber, discountPercentage);
    }

    private void setDiscountCardValuesInStatement(PreparedStatement preparedStatement,
                                                  DiscountCard discountCard) throws SQLException {
        preparedStatement.setString(1, discountCard.getDiscountCardNumber());
        preparedStatement.setBigDecimal(2, discountCard.getDiscountPercentage());
    }

}
