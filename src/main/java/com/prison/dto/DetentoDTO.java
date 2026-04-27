package com.prison.dto;

import com.prison.model.Detento;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DetentoDTO {

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres")
        private String nome;

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos")
        private String cpf;

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve ser no passado")
        private LocalDate dataNascimento;

        @NotNull(message = "Data de entrada é obrigatória")
        private LocalDate dataEntrada;

        private LocalDate dataPrevisaoSaida;

        @NotNull(message = "Regime é obrigatório")
        private Detento.Regime regime;

        @Size(max = 200, message = "Crime deve ter no máximo 200 caracteres")
        private String crime;

        @Min(value = 1, message = "Sentença deve ser maior que 0")
        private Integer sentencaAnos;

        private Long celaId;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private String matricula;
        private String nome;
        private String cpf;
        private LocalDate dataNascimento;
        private LocalDate dataEntrada;
        private LocalDate dataPrevisaoSaida;
        private LocalDate dataSaida;
        private Detento.StatusDetento status;
        private Detento.Regime regime;
        private String crime;
        private Integer sentencaAnos;
        private CelaResumoDTO cela;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CelaResumoDTO {
        private Long id;
        private String numero;
        private String bloco;
    }
}
