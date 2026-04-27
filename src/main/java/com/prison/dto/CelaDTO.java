package com.prison.dto;

import com.prison.model.Cela;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

public class CelaDTO {

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Request {

        @NotBlank(message = "Número da cela é obrigatório")
        @Size(max = 10, message = "Número deve ter no máximo 10 caracteres")
        private String numero;

        @NotBlank(message = "Bloco é obrigatório")
        @Size(max = 10, message = "Bloco deve ter no máximo 10 caracteres")
        private String bloco;

        @NotNull(message = "Capacidade é obrigatória")
        @Min(value = 1, message = "Capacidade mínima é 1")
        @Max(value = 50, message = "Capacidade máxima é 50")
        private Integer capacidade;

        @NotNull(message = "Tipo é obrigatório")
        private Cela.TipoCela tipo;
    }

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class Response {
        private Long id;
        private String numero;
        private String bloco;
        private Integer capacidade;
        private Integer ocupacaoAtual;
        private Cela.StatusCela status;
        private Cela.TipoCela tipo;
        private LocalDateTime criadoEm;
        private LocalDateTime atualizadoEm;
    }
}
