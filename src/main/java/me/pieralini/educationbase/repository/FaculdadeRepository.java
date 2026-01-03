package me.pieralini.educationbase.repository;

import me.pieralini.educationbase.model.Faculdade;
import me.pieralini.educationbase.model.Faculdade.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class FaculdadeRepository extends BaseRepository<Faculdade, Integer> {

    private static FaculdadeRepository instance;

    private FaculdadeRepository() {
        super();
    }

    public static synchronized FaculdadeRepository getInstance() {
        if (instance == null) {
            instance = new FaculdadeRepository();
        }
        return instance;
    }

    @Override
    protected String getTableName() {
        return "faculdades";
    }

    @Override
    protected Faculdade mapResultSet(ResultSet rs) throws SQLException {
        return Faculdade.builder()
                .id(rs.getInt("id"))
                .nomeCurso(rs.getString("nome_curso"))
                .tipoGraduacao(TipoGraduacao.valueOf(rs.getString("tipo_graduacao")))
                .duracaoSemestres(rs.getObject("duracao_semestres", Integer.class))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Faculdade f) throws SQLException {
        stmt.setString(1, f.getNomeCurso());
        stmt.setString(2, f.getTipoGraduacao().name());
        stmt.setObject(3, f.getDuracaoSemestres());
        stmt.setTimestamp(4, Timestamp.valueOf(f.getCreatedAt()));
        stmt.setTimestamp(5, Timestamp.valueOf(f.getUpdatedAt()));
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Faculdade f) throws SQLException {
        stmt.setString(1, f.getNomeCurso());
        stmt.setString(2, f.getTipoGraduacao().name());
        stmt.setObject(3, f.getDuracaoSemestres());
        stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
        stmt.setInt(5, f.getId());
    }

    @Override
    protected Integer getId(Faculdade entity) {
        return entity.getId();
    }

    public Faculdade save(Faculdade faculdade) {
        if (faculdade.getId() != null && existsById(faculdade.getId())) {
            return update(faculdade);
        }
        return insert(faculdade);
    }

    private Faculdade insert(Faculdade f) {
        String sql = """
            INSERT INTO faculdades (nome_curso, tipo_graduacao, duracao_semestres, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            f.setCreatedAt(LocalDateTime.now());
            f.setUpdatedAt(LocalDateTime.now());
            setInsertParameters(stmt, f);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    f.setId(rs.getInt(1));
                }
            }
            logger.info("Faculdade inserida: {}", f.getId());
        } catch (SQLException e) {
            logger.error("Erro ao inserir faculdade: {}", e.getMessage());
        }
        return f;
    }

    private Faculdade update(Faculdade f) {
        String sql = """
            UPDATE faculdades SET nome_curso=?, tipo_graduacao=?, duracao_semestres=?, updated_at=? WHERE id=?
            """;
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setUpdateParameters(stmt, f);
            stmt.executeUpdate();
            logger.info("Faculdade atualizada: {}", f.getId());
        } catch (SQLException e) {
            logger.error("Erro ao atualizar faculdade: {}", e.getMessage());
        }
        return f;
    }

    public List<Faculdade> findByTipoGraduacao(TipoGraduacao tipo) {
        return executeQuery("SELECT * FROM faculdades WHERE tipo_graduacao = ?", tipo.name());
    }

    public List<Faculdade> findBacharelados() {
        return findByTipoGraduacao(TipoGraduacao.BACHARELADO);
    }

    public List<Faculdade> findTecnologos() {
        return findByTipoGraduacao(TipoGraduacao.TECNOLOGO);
    }

    public List<Faculdade> searchByNomeCurso(String termo) {
        return executeQuery("SELECT * FROM faculdades WHERE nome_curso LIKE ?", "%" + termo + "%");
    }
}
