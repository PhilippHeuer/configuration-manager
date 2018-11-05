package com.github.philipp.configurationmanager.storage;

import com.github.philipp.configurationmanager.api.IConfigurationStorageBackend;
import com.github.philipp.configurationmanager.domain.ConfigurationEntry;
import com.github.philipp.configurationmanager.util.JacksonHelper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.Optional;

/**
 * Storage Backend
 */
@Slf4j
public class RestStorageBackend implements IConfigurationStorageBackend {

    /**
     * Service Url
     */
    private final String serviceUrl;

    /**
     * Http Client
     */
    private final OkHttpClient httpClient = new OkHttpClient();

    /**
     * Constructor
     *
     * @param serviceUrl Service Url
     */
    public RestStorageBackend(String serviceUrl) {
        // initialize
        this.serviceUrl = serviceUrl;
    }

    /**
     * Get Configuration
     *
     * @param environment environment name
     * @param name        config name
     * @return Document configuration
     */
    public Optional<ConfigurationEntry> getConfiguration(String environment, String name) {
        log.debug(String.format("Will load configuration [%s:%s] from storage backend %s!", environment, name, getClass().getSimpleName()));

        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(serviceUrl + "/v1/config").newBuilder();
            urlBuilder.addQueryParameter("environment", environment);
            urlBuilder.addQueryParameter("name", name);

            Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .build();

            Call call = httpClient.newCall(request);
            Response response = call.execute();

            if (response.isSuccessful()) {
                String responseBody = response.body().string();

                // mapping
                ConfigurationEntry configurationEntry = JacksonHelper.getObjectMapper().readValue(responseBody, ConfigurationEntry.class);

                // respond
                return Optional.ofNullable(configurationEntry);
            }
        } catch (Exception ex) {
            log.error(String.format("Failed to load configuration [%s:%s] from storage backend %s! Error: %s", environment, name, getClass().getSimpleName(), ex.getMessage()));
            ex.printStackTrace();
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
        log.debug(String.format("Will store configuration [%s:%s] into storage backend %s!", environment, name, getClass().getSimpleName()));

        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(serviceUrl + "/v1/config").newBuilder();
            urlBuilder.addQueryParameter("environment", environment);
            urlBuilder.addQueryParameter("name", name);

            Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .post(RequestBody.create(MediaType.parse("application/json"), JacksonHelper.getObjectMapper().writeValueAsString(config)))
                .build();

            Call call = httpClient.newCall(request);
            Response response = call.execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("HttpClientError - " + response.code() + ": " + response.body().string());
            }
        } catch (Exception ex) {
            log.error(String.format("Failed to store configuration [%s:%s] into storage backend %s! Error: %s", environment, name, getClass().getSimpleName(), ex.getMessage()));
            throw new RuntimeException(ex);
        }
    }

}
