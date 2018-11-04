package com.github.philipp.configurationmanager.api;

import com.github.philipp.configurationmanager.domain.ConfigurationEntry;

import java.util.Optional;

/**
 * The Configuration Storage Backend
 */
public interface IConfigurationStorageBackend {

    /**
     * Get Configuration
     *
     * @param environment environment name
     * @param name        config name
     * @return Document configuration
     */
    Optional<ConfigurationEntry> getConfiguration(String environment, String name);

    /**
     * Store Configuration
     *
     * @param environment environment name
     * @param name        config name
     * @param config      config body
     */
    void storeConfiguration(String environment, String name, ConfigurationEntry config);

}
