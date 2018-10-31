package com.github.philipp.configurationmanager.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * Jackson Utils
 */
public class JacksonUtils {

    /**
     * Gets a JSON ObjectMapper
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * Gets a YAML Object Mapper
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getYamlObjectMapper() {
        return new ObjectMapper(new YAMLFactory());
    }

}
