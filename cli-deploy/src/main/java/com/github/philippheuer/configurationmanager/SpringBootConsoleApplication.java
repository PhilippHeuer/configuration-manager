package com.github.philippheuer.configurationmanager;

import com.github.philipp.configurationmanager.ConfigurationManager;
import com.github.philipp.configurationmanager.ConfigurationManagerBuilder;
import com.github.philipp.configurationmanager.domain.ConfigurationEntry;
import com.github.philipp.configurationmanager.domain.ConfigurationFormat;
import com.github.philipp.configurationmanager.storage.RestStorageBackend;
import com.github.philippheuer.configurationmanager.storage.MongoDbStorageBackend;
import com.github.philippheuer.configurationmanager.storage.MySQLStorageBackend;
import com.github.philippheuer.configurationmanager.storage.PostgresDbStorageBackend;
import lombok.extern.java.Log;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.System.exit;

@SpringBootApplication
@Log
public class SpringBootConsoleApplication implements CommandLineRunner {

    @Autowired
    private Environment env;

    @Option(name="-action", usage="Specify the configuration action (deploy/read)")
    String configAction = "deploy";

    @Option(name="-key", usage="Specify the configuration identifier")
    String configKey = "example01";

    @Option(name="-env", usage="Specify the environment")
    String configEnvironment = "dev";

    @Option(name="-file", usage="Specify the configuration file")
    String configFilename = "example.yaml";

    /**
     * App Entrypoint
     *
     * @param args Args
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringBootConsoleApplication.class, args);
    }

    /**
     * Executed Run Method
     *
     * @param args Args
     */
    @Override
    public void run(String... args) {
        // Parse Aguments
        CmdLineParser parser = new CmdLineParser(this);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            // handling of wrong arguments
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            exit(1);
        }

        // Configuration
        String storageType = env.getProperty("storage.type");

        // Builder
        ConfigurationManager configurationManager = null;
        if (storageType.equalsIgnoreCase("mongo")) {
            configurationManager = ConfigurationManagerBuilder.builder()
                .withStorageBackend(new MongoDbStorageBackend(env.getProperty("storage.server"), env.getProperty("storage.database"), env.getProperty("storage.table")))
                .build();
        }  else if (storageType.equalsIgnoreCase("postgres")) {
            configurationManager = ConfigurationManagerBuilder.builder()
                .withStorageBackend(new PostgresDbStorageBackend(env.getProperty("storage.server"), env.getProperty("storage.username"), env.getProperty("storage.password"), env.getProperty("storage.database"), env.getProperty("storage.table")))
                .build();
        } else if (storageType.equalsIgnoreCase("mysql")) {
            configurationManager = ConfigurationManagerBuilder.builder()
                .withStorageBackend(new MySQLStorageBackend(env.getProperty("storage.server"), env.getProperty("storage.username"), env.getProperty("storage.password"), env.getProperty("storage.database"), env.getProperty("storage.table")))
                .build();
        } else if (storageType.equalsIgnoreCase("rest")) {
            configurationManager = ConfigurationManagerBuilder.builder()
                .withStorageBackend(new RestStorageBackend(env.getProperty("storage.server")))
                .build();
        }

        // YML Mapper
        try {
            // store
            if (configAction.equalsIgnoreCase("deploy")) {
                String fileContent = new String(Files.readAllBytes(Paths.get(configFilename)), StandardCharsets.UTF_8);
                log.info("Loading file [" + Paths.get(configFilename).toAbsolutePath() + "]");

                ConfigurationEntry configItem = new ConfigurationEntry("1", ConfigurationFormat.YAML, fileContent);
                configurationManager.storeConfiguration(configEnvironment, configKey, configItem);
                log.info(String.format("Stored configuration [%s:%s] in storage backend!", configEnvironment, configKey));
            } else if (configAction.equalsIgnoreCase("read")) {
                // read
                Optional<ConfigurationEntry> config = configurationManager.getConfiguration(configEnvironment, configKey);
                log.info(String.format("Config content of [%s:%s] is: %s", configEnvironment, configKey, config.toString()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
