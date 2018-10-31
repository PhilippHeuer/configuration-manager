package com.github.philipp.configurationmanager;

import com.github.philipp.configurationmanager.api.IConfigurationStorageBackend;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.Wither;
import lombok.extern.slf4j.Slf4j;

/**
 * Builder to get a StreamlabsClient Instance by provided various options, to provide the user with a lot of customizable options.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigurationManagerBuilder {

    /**
     * Storage Backend
     */
    @Wither
    private IConfigurationStorageBackend storageBackend = null;

    /**
     * Initialize the builder
     *
     * @return Builder
     */
    public static ConfigurationManagerBuilder builder() {
        return new ConfigurationManagerBuilder();
    }

    /**
     * Initialize
     *
     * @return ConfigurationManager
     */
    public ConfigurationManager build() {
        // Client Builder
        final ConfigurationManager client = new ConfigurationManager(storageBackend);

        // Return new Client Instance
        return client;
    }

}
