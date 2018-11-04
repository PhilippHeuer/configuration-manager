package com.github.philippheuer.configurationmanager.config;

import com.github.philipp.configurationmanager.ConfigurationManager;
import com.github.philipp.configurationmanager.ConfigurationManagerBuilder;
import com.github.philippheuer.configurationmanager.storage.MongoDbStorageBackend;
import com.github.philippheuer.configurationmanager.storage.MySQLStorageBackend;
import com.github.philippheuer.configurationmanager.storage.PostgresDbStorageBackend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class InitializeConfigurationManager {

    @Autowired
    private Environment env;

    /**
     * Build Bean
     *
     * @return ConfigurationManager
     */
    @Bean
    public ConfigurationManager configurationManager() {
        // Configuration
        String storageType = env.getProperty("storage.type");

        // Builder
        ConfigurationManager configurationManager = null;
        if (storageType.equalsIgnoreCase("mongo")) {
            configurationManager = ConfigurationManagerBuilder.builder()
                .withStorageBackend(new MongoDbStorageBackend(env.getProperty("storage.server"), env.getProperty("storage.database"), env.getProperty("storage.table")))
                .build();
        } else if (storageType.equalsIgnoreCase("postgres")) {
            configurationManager = ConfigurationManagerBuilder.builder()
                .withStorageBackend(new PostgresDbStorageBackend(env.getProperty("storage.server"), env.getProperty("storage.username"), env.getProperty("storage.password"), env.getProperty("storage.database"), env.getProperty("storage.table")))
                .build();
        } else if (storageType.equalsIgnoreCase("mysql")) {
            configurationManager = ConfigurationManagerBuilder.builder()
                .withStorageBackend(new MySQLStorageBackend(env.getProperty("storage.server"), env.getProperty("storage.username"), env.getProperty("storage.password"), env.getProperty("storage.database"), env.getProperty("storage.table")))
                .build();
        }

        // unsupported storage type
        if (configurationManager == null) {
            throw new UnsupportedOperationException("Storage Type " + env.getProperty("storage.type") + " is not supported!");
        }

        return configurationManager;
    }

}
