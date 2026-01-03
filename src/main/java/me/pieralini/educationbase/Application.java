package me.pieralini.educationbase;

import me.pieralini.educationbase.config.ConfigManager;
import me.pieralini.educationbase.config.DatabaseManager;
import me.pieralini.educationbase.loader.DataLoader;
import me.pieralini.educationbase.service.CursoService;
import me.pieralini.educationbase.service.FaculdadeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("=".repeat(60));
        logger.info("Iniciando EducationBase");
        logger.info("=".repeat(60));

        try {
            ConfigManager config = ConfigManager.getInstance();
            logger.info("Aplicação: {} v{}", config.getApplicationConfig().name(), config.getApplicationConfig().version());

            DatabaseManager dbManager = DatabaseManager.getInstance();
            dbManager.initialize(config);

            DataLoader dataLoader = new DataLoader();
            dataLoader.loadAll();

            CursoService cursoService = new CursoService();
            FaculdadeService faculdadeService = new FaculdadeService();

            logger.info("=".repeat(40));
            logger.info("Resumo dos dados:");
            logger.info("  Cursos: {}", cursoService.contarTodos());
            logger.info("  Faculdades: {}", faculdadeService.contarTodas());
            logger.info("=".repeat(40));

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Encerrando aplicação...");
                dbManager.close();
            }));

            logger.info("=".repeat(60));
            logger.info("EducationBase iniciado com sucesso!");
            logger.info("=".repeat(60));

        } catch (Exception e) {
            logger.error("Erro ao executar aplicação: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
