package me.pieralini.educationbase.service;

import me.pieralini.educationbase.model.Faculdade;
import me.pieralini.educationbase.model.Faculdade.*;
import me.pieralini.educationbase.repository.FaculdadeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class FaculdadeService {

    private static final Logger logger = LoggerFactory.getLogger(FaculdadeService.class);
    private final FaculdadeRepository repository;

    public FaculdadeService() {
        this.repository = FaculdadeRepository.getInstance();
    }

    public Faculdade criar(Faculdade faculdade) {
        validarFaculdade(faculdade);
        return repository.save(faculdade);
    }

    public Optional<Faculdade> atualizar(Integer id, Faculdade faculdadeAtualizada) {
        return repository.findById(id).map(faculdadeExistente -> {
            faculdadeAtualizada.setId(id);
            faculdadeAtualizada.setCreatedAt(faculdadeExistente.getCreatedAt());
            validarFaculdade(faculdadeAtualizada);
            return repository.save(faculdadeAtualizada);
        });
    }

    public Optional<Faculdade> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public List<Faculdade> listarTodas() {
        return repository.findAll();
    }

    public boolean remover(Integer id) {
        return repository.deleteById(id);
    }

    public List<Faculdade> buscarPorTipo(TipoGraduacao tipo) {
        return repository.findByTipoGraduacao(tipo);
    }

    public List<Faculdade> buscarBacharelados() {
        return repository.findBacharelados();
    }

    public List<Faculdade> buscarTecnologos() {
        return repository.findTecnologos();
    }

    public List<Faculdade> pesquisar(String termo) {
        return repository.searchByNomeCurso(termo);
    }

    public long contarTodas() {
        return repository.count();
    }

    private void validarFaculdade(Faculdade faculdade) {
        if (faculdade.getNomeCurso() == null || faculdade.getNomeCurso().isBlank()) {
            throw new IllegalArgumentException("Nome do curso é obrigatório");
        }
        if (faculdade.getTipoGraduacao() == null) {
            throw new IllegalArgumentException("Tipo de graduação é obrigatório");
        }
    }
}
