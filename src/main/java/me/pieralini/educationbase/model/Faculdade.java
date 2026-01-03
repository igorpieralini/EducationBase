package me.pieralini.educationbase.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Faculdade {

    private Integer id;
    private String nomeCurso;
    private TipoGraduacao tipoGraduacao;
    private Integer duracaoSemestres;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum TipoGraduacao {
        BACHARELADO, TECNOLOGO, LICENCIATURA
    }

    public boolean isTecnologo() {
        return this.tipoGraduacao == TipoGraduacao.TECNOLOGO;
    }

    public boolean isBacharelado() {
        return this.tipoGraduacao == TipoGraduacao.BACHARELADO;
    }
}
