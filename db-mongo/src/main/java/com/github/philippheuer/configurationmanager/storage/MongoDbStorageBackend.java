package com.github.philippheuer.configurationmanager.storage;

import com.github.philipp.configurationmanager.api.IConfigurationStorageBackend;
import com.github.philipp.configurationmanager.domain.ConfigurationEntry;
import com.github.philipp.configurationmanager.util.JacksonHelper;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

/**
 * MongoDb Storage Backend
 */
public class MongoDbStorageBackend implements IConfigurationStorageBackend {

    /**
     * Holds the Db Client
     */
    private MongoClient mongoClient;

    /**
     * Holds the Db
     */
    private MongoDatabase mongoDatabase;

    /**
     * Holds the collection
     */
    private MongoCollection<Document> collection;

    /**
     * Constructor
     *
     * @param connectionString Connection String
     * @param databaseName Database Name
     * @param collectionName Collection Name
     */
    public MongoDbStorageBackend(String connectionString, String databaseName, String collectionName) {
        // initialize
        this.mongoClient = MongoClients.create(new ConnectionString(connectionString));
        this.mongoDatabase = mongoClient.getDatabase(databaseName);

        // load collection
        this.collection = mongoDatabase.getCollection(collectionName);
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
            // load document
            Document document = collection.find(and(eq("environment", environment), eq("name", name))).first();

            // mapping
            ConfigurationEntry configurationEntry = JacksonHelper.getObjectMapper().readValue(document.get("configurationBody").toString(), ConfigurationEntry.class);

            // respond
            return Optional.ofNullable(configurationEntry);
        } catch (Exception ex) {
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
        try {
            // initialize document with meta information
            Document document = new Document("name", name)
                .append("environment", environment);
            // map config into the document
            document = document.append("configurationBody", JacksonHelper.getObjectMapper().writeValueAsString(config));

            // store
            Document exists = collection.find(and(eq("environment", environment), eq("name", name))).first();
            if (exists != null) {
                collection.replaceOne(and(eq("environment", environment), eq("name", name)), document);
            } else {
                collection.insertOne(document);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}
