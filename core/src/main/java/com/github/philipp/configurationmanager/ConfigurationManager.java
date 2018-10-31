package com.github.philipp.configurationmanager;

import com.github.philipp.configurationmanager.api.IConfigurationStorageBackend;
import com.github.philipp.configurationmanager.domain.ConfigurationItem;

import java.util.Optional;

public class ConfigurationManager {

    /**
     * Storage Backend
     */
    private IConfigurationStorageBackend storageBackend;

    /**
     * Constructor
     *
     * @param storageBackend Storage Backend
     */
    public ConfigurationManager(IConfigurationStorageBackend storageBackend) {
        this.storageBackend = storageBackend;
    }

    /**
     * Get Configuration
     *
     * @param environment environment name
     * @param name        config name
     * @return Document configuration
     */
    public Optional<ConfigurationItem> getConfiguration(String environment, String name) {
        return storageBackend.getConfiguration(environment, name);
    }

    /**
     * Store Configuration
     *
     * @param environment environment name
     * @param name        config name
     * @param config      config body
     */
    public void storeConfiguration(String environment, String name, ConfigurationItem config) {
        storageBackend.storeConfiguration(environment, name, config);
    }

}
