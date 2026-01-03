package me.pieralini.educationbase.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Data
@NoArgsConstructor
public class ConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "config.yml";
    private static ConfigManager instance;

    private Map<String, Object> config;
    private ApplicationConfig applicationConfig;
    private DatabaseConfig databaseConfig;

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
            instance.load();
        }
        return instance;
    }

    public void reload() {
        load();
        logger.info("Configurações recarregadas com sucesso");
    }

    @SuppressWarnings("unchecked")
    private void load() {
        try {
            Yaml yaml = new Yaml(new Constructor(Map.class, new LoaderOptions()));
            
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                if (inputStream != null) {
                    config = yaml.load(inputStream);
                    parseConfig();
                    logger.info("Configurações carregadas do classpath: {}", CONFIG_FILE);
                    return;
                }
            }

            Path configPath = Path.of(CONFIG_FILE);
            if (Files.exists(configPath)) {
                try (InputStream inputStream = Files.newInputStream(configPath)) {
                    config = yaml.load(inputStream);
                    parseConfig();
                    logger.info("Configurações carregadas do arquivo: {}", configPath.toAbsolutePath());
                    return;
                }
            }

            logger.warn("Arquivo de configuração não encontrado, usando valores padrão");
            loadDefaults();

        } catch (IOException e) {
            logger.error("Erro ao carregar configurações: {}", e.getMessage());
            loadDefaults();
        }
    }

    @SuppressWarnings("unchecked")
    private void parseConfig() {
        Map<String, Object> appMap = (Map<String, Object>) config.get("application");
        if (appMap != null) {
            applicationConfig = new ApplicationConfig(
                    (String) appMap.get("name"),
                    (String) appMap.get("version"),
                    (String) appMap.get("description")
            );
        }

        Map<String, Object> dbMap = (Map<String, Object>) config.get("database");
        if (dbMap != null) {
            databaseConfig = new DatabaseConfig(
                    (String) dbMap.get("host"),
                    (Integer) dbMap.get("port"),
                    (String) dbMap.get("user"),
                    (String) dbMap.get("password"),
                    (String) dbMap.get("name")
            );
        }
    }

    private void loadDefaults() {
        applicationConfig = new ApplicationConfig("EducationBase", "1.0.0", "Sistema de gerenciamento educacional");
        databaseConfig = new DatabaseConfig("localhost", 3306, "root", "", "educationbase_db");
    }

    public record ApplicationConfig(String name, String version, String description) {}
    public record DatabaseConfig(String host, int port, String user, String password, String name) {}
}
