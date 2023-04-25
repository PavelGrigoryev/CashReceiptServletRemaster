package ru.clevertec.cashreceipt.servletremaster.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;
import ru.clevertec.cashreceipt.servletremaster.config.YamlConfig;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
@WebListener
public class LiquibaseInitializer implements ServletContextListener {

    private Liquibase liquibase;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (JdbcConnection jdbcConnection = new JdbcConnection(getDatabaseConnection());
             ClassLoaderResourceAccessor classLoaderResourceAccessor = new ClassLoaderResourceAccessor()) {
            liquibase = new Liquibase("db.changelog/changelog.yaml", classLoaderResourceAccessor,
                    DatabaseFactory.getInstance().findCorrectDatabaseImplementation(jdbcConnection));
            liquibase.update(new Contexts());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (liquibase != null) {
            try {
                liquibase.getDatabase().getConnection().close();
            } catch (DatabaseException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private Connection getDatabaseConnection() throws SQLException {
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
        return DriverManager.getConnection(url, user, password);
    }

}
