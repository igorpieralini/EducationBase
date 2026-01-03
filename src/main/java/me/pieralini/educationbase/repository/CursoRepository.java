package me.pieralini.educationbase.repository;

import me.pieralini.educationbase.model.Curso;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

public class CursoRepository extends BaseRepository<Curso, Integer> {

    private static CursoRepository instance;

    private CursoRepository() {
        super();
    }

    public static synchronized CursoRepository getInstance() {
        if (instance == null) {
            instance = new CursoRepository();
        }
        return instance;
    }

    @Override
    protected String getTableName() {
        return "cursos";
    }

    @Override
    protected Curso mapResultSet(ResultSet rs) throws SQLException {
        return Curso.builder()
                .id(rs.getInt("id"))
                .nome(rs.getString("nome"))
                .descricao(rs.getString("descricao"))
                .plataforma(rs.getString("plataforma"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    @Override
    protected void setInsertParameters(PreparedStatement stmt, Curso curso) throws SQLException {
        stmt.setString(1, curso.getNome());
        stmt.setString(2, curso.getDescricao());
        stmt.setString(3, curso.getPlataforma());
        stmt.setTimestamp(4, Timestamp.valueOf(curso.getCreatedAt()));
        stmt.setTimestamp(5, Timestamp.valueOf(curso.getUpdatedAt()));
    }

    @Override
    protected void setUpdateParameters(PreparedStatement stmt, Curso curso) throws SQLException {
        stmt.setString(1, curso.getNome());
        stmt.setString(2, curso.getDescricao());
        stmt.setString(3, curso.getPlataforma());
        stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
        stmt.setInt(5, curso.getId());
    }

    @Override
    protected Integer getId(Curso entity) {
        return entity.getId();
    }

    public Curso save(Curso curso) {
        if (curso.getId() != null && existsById(curso.getId())) {
            return update(curso);
        }
        return insert(curso);
    }

    private Curso insert(Curso curso) {
        String sql = "INSERT INTO cursos (nome, descricao, plataforma, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            curso.setCreatedAt(LocalDateTime.now());
            curso.setUpdatedAt(LocalDateTime.now());
            setInsertParameters(stmt, curso);
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    curso.setId(rs.getInt(1));
                }
            }
            logger.info("Curso inserido: {}", curso.getId());
        } catch (SQLException e) {
            logger.error("Erro ao inserir curso: {}", e.getMessage());
        }
        return curso;
    }

    private Curso update(Curso curso) {
        String sql = "UPDATE cursos SET nome=?, descricao=?, plataforma=?, updated_at=? WHERE id=?";
        try (Connection conn = databaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setUpdateParameters(stmt, curso);
            stmt.executeUpdate();
            logger.info("Curso atualizado: {}", curso.getId());
        } catch (SQLException e) {
            logger.error("Erro ao atualizar curso: {}", e.getMessage());
        }
        return curso;
    }

    public List<Curso> findByPlataforma(String plataforma) {
        return executeQuery("SELECT * FROM cursos WHERE plataforma = ?", plataforma);
    }

    public List<Curso> searchByNome(String termo) {
        return executeQuery("SELECT * FROM cursos WHERE nome LIKE ?", "%" + termo + "%");
    }
}
