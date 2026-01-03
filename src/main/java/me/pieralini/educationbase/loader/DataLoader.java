package me.pieralini.educationbase.loader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.pieralini.educationbase.model.Curso;
import me.pieralini.educationbase.model.Faculdade;
import me.pieralini.educationbase.repository.CursoRepository;
import me.pieralini.educationbase.repository.FaculdadeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private final ObjectMapper objectMapper;
    private final CursoRepository cursoRepository;
    private final FaculdadeRepository faculdadeRepository;

    public DataLoader() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.cursoRepository = CursoRepository.getInstance();
        this.faculdadeRepository = FaculdadeRepository.getInstance();
    }

    public void loadAll() {
        logger.info("Iniciando carregamento de dados JSON...");
        loadCursos();
        loadFaculdades();
        logger.info("Carregamento de dados concluído!");
    }

    public void loadCursos() {
        try {
            List<Curso> cursos = loadFromJson("data/cursos.json", new TypeReference<>() {});
            if (cursos != null) {
                int count = 0;
                for (Curso c : cursos) {
                    if (!cursoRepository.existsById(c.getId())) {
                        cursoRepository.save(c);
                        count++;
                    }
                }
                logger.info("Cursos carregados: {} novos de {} total", count, cursos.size());
            }
        } catch (Exception e) {
            logger.error("Erro ao carregar cursos: {}", e.getMessage());
        }
    }

    public void loadFaculdades() {
        try {
            List<Faculdade> faculdades = loadFromJson("data/faculdades.json", new TypeReference<>() {});
            if (faculdades != null) {
                int count = 0;
                for (Faculdade f : faculdades) {
                    if (!faculdadeRepository.existsById(f.getId())) {
                        faculdadeRepository.save(f);
                        count++;
                    }
                }
                logger.info("Faculdades carregadas: {} novas de {} total", count, faculdades.size());
            }
        } catch (Exception e) {
            logger.error("Erro ao carregar faculdades: {}", e.getMessage());
        }
    }

    private <T> T loadFromJson(String resourcePath, TypeReference<T> typeReference) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is != null) {
                return objectMapper.readValue(is, typeReference);
            }
        } catch (IOException e) {
            logger.debug("Arquivo não encontrado no classpath: {}", resourcePath);
        }

        Path filePath = Path.of(resourcePath);
        if (Files.exists(filePath)) {
            try {
                return objectMapper.readValue(filePath.toFile(), typeReference);
            } catch (IOException e) {
                logger.error("Erro ao ler arquivo {}: {}", filePath, e.getMessage());
            }
        }

        logger.warn("Arquivo JSON não encontrado: {}", resourcePath);
        return null;
    }

    public void exportToJson() {
        try {
            Path dataDir = Path.of("data");
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }

            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            List<Curso> cursos = cursoRepository.findAll();
            objectMapper.writeValue(dataDir.resolve("cursos.json").toFile(), cursos);
            logger.info("Exportados {} cursos", cursos.size());

            List<Faculdade> faculdades = faculdadeRepository.findAll();
            objectMapper.writeValue(dataDir.resolve("faculdades.json").toFile(), faculdades);
            logger.info("Exportadas {} faculdades", faculdades.size());

            logger.info("Exportação concluída para pasta data/");
        } catch (IOException e) {
            logger.error("Erro ao exportar dados: {}", e.getMessage());
        }
    }
}
