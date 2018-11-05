package com.github.philippheuer.configurationmanager.controller;

import com.github.philipp.configurationmanager.ConfigurationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.github.philipp.configurationmanager.domain.ConfigurationEntry;

import java.util.*;

@RestController
@RequestMapping("/v1")
@Slf4j
public class ConfigurationController {

    /**
     * ConfigurationManager
     */
    @Autowired
    private ConfigurationManager configurationManager;

    /**
     * Method: Get Configuration
     *
     * @param environment environment name
     * @param configId config name
     * @return ConfigurationEntry
     */
    @RequestMapping(value = "/config", method = RequestMethod.GET)
    public ResponseEntity<ConfigurationEntry> getConfig(
        @RequestParam(value = "environment", required = true) String environment,
        @RequestParam(value = "name", required = true) String configId
    ) {
        // load configuration
        Optional<ConfigurationEntry> configurationEntry = configurationManager.getConfiguration(environment, configId);

        // success
        return new ResponseEntity<>(configurationEntry.get(), HttpStatus.OK);
    }

    /**
     * Get Store Configuration
     *
     * @param environment environment name
     * @param configId config name
     * @return nothing
     */
    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public ResponseEntity<Object> storeConfig(
            @RequestParam(value = "environment", required = true) String environment,
            @RequestParam(value = "name", required = true) String configId,
            @RequestBody ConfigurationEntry body
    		) {
        // store configuration
        configurationManager.storeConfiguration(environment, configId, body);

        // success
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
