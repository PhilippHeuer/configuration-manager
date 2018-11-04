package com.github.philippheuer.configurationmanager.storage;

import com.github.philipp.configurationmanager.api.IConfigurationStorageBackend;
import com.github.philipp.configurationmanager.domain.ConfigurationEntry;
import com.github.philipp.configurationmanager.util.JacksonHelper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.Optional;

/**
 * Storage Backend
 */
@Slf4j
public class PostgresDbStorageBackend implements IConfigurationStorageBackend {

    /**
     * Holds the DatabaseName
     */
    private String databaseName;

    /**
     * Holds the TableName
     */
    private String tableName;

    /**
     * Holds the DataSource
     */
    private HikariDataSource dataSource;

    /**
     * Constructor
     *
     * @param connectionString Connection String
     * @param username Username (not required if it's part of the connection string)
     * @param password Password (not required if it's part of the connection string)
     * @param databaseName Database Name
     * @param tableName Collection Name
     */
    public PostgresDbStorageBackend(String connectionString, String username, String password, String databaseName, String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;

        // initialize dataSource
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(connectionString);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(5);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName("springHikariCP");

        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", "true");

        dataSource = new HikariDataSource(hikariConfig);

        // run db migrations if needed
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
            Liquibase liquibase = new Liquibase("postgres-changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("main");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.rollback();
                    conn.close();
                } catch (SQLException e) {
                    // nothing to do
                }
            }
        }
    }

    /**
     * Get Configuration
     *
     * @param environment environment name
     * @param name        config name
     * @return Document configuration
     */
    public Optional<ConfigurationEntry> getConfiguration(String environment, String name) {
        try {
            log.debug(String.format("Will load configuration [%s:%s] from storage backend %s!", environment, name, getClass().getSimpleName()));

            // configuration item
            ConfigurationEntry entry = new ConfigurationEntry();

            // read query
            Statement stmt = dataSource.getConnection().createStatement();
            ResultSet rs = stmt.executeQuery("SELECT environment, name, content FROM " + this.tableName);
            while (rs.next()) {
                String configContent = rs.getString("content");

                // mapping
                ConfigurationEntry configurationEntry = JacksonHelper.getObjectMapper().readValue(configContent, ConfigurationEntry.class);

                // respond
                return Optional.ofNullable(configurationEntry);
            }

        } catch (Exception e) {
            log.error(String.format("Failed to load configuration [%s:%s] from storage backend %s! Error: %s", environment, name, getClass().getSimpleName(), e.getMessage()));
        }

        return Optional.empty();
    }

    /**
     * Store Configuration
     *
     * @param environment environment name
     * @param name        config name
     * @param config      configuration object
     */
    public void storeConfiguration(String environment, String name, ConfigurationEntry config) {
        try {
            log.debug(String.format("Will store configuration [%s:%s] into storage backend %s!", environment, name, getClass().getSimpleName()));

            // write query
            PreparedStatement stmt = dataSource.getConnection().prepareStatement("INSERT INTO " + this.tableName + " (environment, name, content) VALUES (?, ?, ?) ON CONFLICT (environment, name) DO UPDATE SET content=?");
            stmt.setString(1, environment);
            stmt.setString(2, name);
            stmt.setString(3, JacksonHelper.getObjectMapper().writeValueAsString(config));
            stmt.setString(4, JacksonHelper.getObjectMapper().writeValueAsString(config));
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error(String.format("Failed to store configuration [%s:%s] into storage backend %s! Error: %s", environment, name, getClass().getSimpleName(), e.getMessage()));
        }
    }

}
