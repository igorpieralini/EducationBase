package me.pieralini.educationbase.service;

import me.pieralini.educationbase.model.Curso;
import me.pieralini.educationbase.repository.CursoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class CursoService {

    private static final Logger logger = LoggerFactory.getLogger(CursoService.class);
    private final CursoRepository repository;

    public CursoService() {
        this.repository = CursoRepository.getInstance();
    }

    public Curso criar(Curso curso) {
        validarCurso(curso);
        return repository.save(curso);
    }

    public Optional<Curso> atualizar(Integer id, Curso cursoAtualizado) {
        return repository.findById(id).map(cursoExistente -> {
            cursoAtualizado.setId(id);
            cursoAtualizado.setCreatedAt(cursoExistente.getCreatedAt());
            validarCurso(cursoAtualizado);
            return repository.save(cursoAtualizado);
        });
    }

    public Optional<Curso> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public List<Curso> listarTodos() {
        return repository.findAll();
    }

    public boolean remover(Integer id) {
        return repository.deleteById(id);
    }

    public List<Curso> buscarPorPlataforma(String plataforma) {
        return repository.findByPlataforma(plataforma);
    }

    public List<Curso> pesquisar(String termo) {
        return repository.searchByNome(termo);
    }

    public long contarTodos() {
        return repository.count();
    }

    private void validarCurso(Curso curso) {
        if (curso.getNome() == null || curso.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do curso é obrigatório");
        }
        if (curso.getPlataforma() == null || curso.getPlataforma().isBlank()) {
            throw new IllegalArgumentException("Plataforma é obrigatória");
        }
    }
}
