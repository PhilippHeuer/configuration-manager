package com.github.philipp.configurationmanager.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration Item
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationItem {

    /**
     * Version
     */
    private String version;

    /**
     * Format
     */
    private ConfigurationFormat format;

    /**
     * Body
     */
    private Object body;

}
