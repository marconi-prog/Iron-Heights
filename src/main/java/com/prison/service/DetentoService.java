package com.prison.service;

import com.prison.dto.DetentoDTO;
import com.prison.exception.BusinessException;
import com.prison.exception.ResourceNotFoundException;
import com.prison.model.Cela;
import com.prison.model.Detento;
import com.prison.repository.CelaRepository;
import com.prison.repository.DetentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class DetentoService {

    private final DetentoRepository detentoRepository;
    private final CelaRepository celaRepository;

    private static final AtomicInteger sequencial = new AtomicInteger(1);

    @Transactional
    public DetentoDTO.Response criar(DetentoDTO.Request request) {
        log.info("Criando detento: CPF={}", request.getCpf());

        if (detentoRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("Já existe um detento cadastrado com este CPF.");
        }

        Detento detento = new Detento();
        detento.setMatricula(gerarMatricula());
        detento.setNome(request.getNome());
        detento.setCpf(request.getCpf());
        detento.setDataNascimento(request.getDataNascimento());
        detento.setDataEntrada(request.getDataEntrada());
        detento.setDataPrevisaoSaida(request.getDataPrevisaoSaida());
        detento.setRegime(request.getRegime());
        detento.setCrime(request.getCrime());
        detento.setSentencaAnos(request.getSentencaAnos());
        detento.setStatus(Detento.StatusDetento.ATIVO);

        if (request.getCelaId() != null) {
            Cela cela = buscarCelaComVaga(request.getCelaId());
            detento.setCela(cela);
            atualizarStatusCela(cela);
        }

        detento = detentoRepository.save(detento);
        log.info("Detento criado com sucesso. ID={}, Matricula={}", detento.getId(), detento.getMatricula());
        return toResponse(detento);
    }

    @Transactional(readOnly = true)
    public Page<DetentoDTO.Response> listar(String nome, Detento.StatusDetento status,
                                             Detento.Regime regime, Long celaId, Pageable pageable) {
        return detentoRepository.buscarComFiltros(nome, status, regime, celaId, pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public DetentoDTO.Response buscarPorId(Long id) {
        return toResponse(findDetentoById(id));
    }

    @Transactional(readOnly = true)
    public DetentoDTO.Response buscarPorMatricula(String matricula) {
        return toResponse(detentoRepository.findByMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("Detento não encontrado com matrícula: " + matricula)));
    }

    @Transactional
    public DetentoDTO.Response atualizar(Long id, DetentoDTO.Request request) {
        log.info("Atualizando detento ID={}", id);
        Detento detento = findDetentoById(id);

        if (!detento.getCpf().equals(request.getCpf()) && detentoRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("Já existe outro detento cadastrado com este CPF.");
        }

        detento.setNome(request.getNome());
        detento.setCpf(request.getCpf());
        detento.setDataNascimento(request.getDataNascimento());
        detento.setDataEntrada(request.getDataEntrada());
        detento.setDataPrevisaoSaida(request.getDataPrevisaoSaida());
        detento.setRegime(request.getRegime());
        detento.setCrime(request.getCrime());
        detento.setSentencaAnos(request.getSentencaAnos());

        if (request.getCelaId() != null) {
            Cela novaCela = buscarCelaComVaga(request.getCelaId());
            Cela celaAnterior = detento.getCela();
            detento.setCela(novaCela);
            atualizarStatusCela(novaCela);
            if (celaAnterior != null && !celaAnterior.getId().equals(novaCela.getId())) {
                atualizarStatusCela(celaAnterior);
            }
        } else {
            if (detento.getCela() != null) {
                Cela celaAnterior = detento.getCela();
                detento.setCela(null);
                atualizarStatusCela(celaAnterior);
            }
        }

        return toResponse(detentoRepository.save(detento));
    }

    @Transactional
    public void liberar(Long id) {
        log.info("Liberando detento ID={}", id);
        Detento detento = findDetentoById(id);
        if (detento.getStatus() != Detento.StatusDetento.ATIVO) {
            throw new BusinessException("Apenas detentos com status ATIVO podem ser liberados.");
        }
        detento.setStatus(Detento.StatusDetento.LIBERADO);
        detento.setDataSaida(LocalDate.now());
        Cela cela = detento.getCela();
        detento.setCela(null);
        detentoRepository.save(detento);
        if (cela != null) atualizarStatusCela(cela);
    }

    @Transactional
    public void transferir(Long id, Long novaCelaId) {
        log.info("Transferindo detento ID={} para cela ID={}", id, novaCelaId);
        Detento detento = findDetentoById(id);
        if (detento.getStatus() != Detento.StatusDetento.ATIVO) {
            throw new BusinessException("Apenas detentos ATIVOS podem ser transferidos.");
        }
        Cela novaCela = buscarCelaComVaga(novaCelaId);
        Cela celaAnterior = detento.getCela();
        detento.setCela(novaCela);
        detento.setStatus(Detento.StatusDetento.TRANSFERIDO);
        detentoRepository.save(detento);
        atualizarStatusCela(novaCela);
        if (celaAnterior != null) atualizarStatusCela(celaAnterior);
    }

    // ---- helpers ----

    private Detento findDetentoById(Long id) {
        return detentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detento não encontrado. ID: " + id));
    }

    private Cela buscarCelaComVaga(Long celaId) {
        Cela cela = celaRepository.findById(celaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cela não encontrada. ID: " + celaId));
        if (cela.getStatus() == Cela.StatusCela.MANUTENCAO) {
            throw new BusinessException("Cela " + cela.getNumero() + " está em manutenção.");
        }
        long ocupacao = detentoRepository.countAtivosByCelaId(celaId);
        if (ocupacao >= cela.getCapacidade()) {
            throw new BusinessException("Cela " + cela.getNumero() + " não tem vagas disponíveis.");
        }
        return cela;
    }

    private void atualizarStatusCela(Cela cela) {
        long ocupacao = detentoRepository.countAtivosByCelaId(cela.getId());
        if (ocupacao == 0) {
            cela.setStatus(Cela.StatusCela.DISPONIVEL);
        } else if (ocupacao >= cela.getCapacidade()) {
            cela.setStatus(Cela.StatusCela.LOTADA);
        } else {
            cela.setStatus(Cela.StatusCela.OCUPADA);
        }
        celaRepository.save(cela);
    }

    private String gerarMatricula() {
        String ano = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"));
        return "DET" + ano + String.format("%05d", sequencial.getAndIncrement());
    }

    private DetentoDTO.Response toResponse(Detento d) {
        DetentoDTO.CelaResumoDTO celaResumo = null;
        if (d.getCela() != null) {
            celaResumo = DetentoDTO.CelaResumoDTO.builder()
                    .id(d.getCela().getId())
                    .numero(d.getCela().getNumero())
                    .bloco(d.getCela().getBloco())
                    .build();
        }
        return DetentoDTO.Response.builder()
                .id(d.getId())
                .matricula(d.getMatricula())
                .nome(d.getNome())
                .cpf(d.getCpf())
                .dataNascimento(d.getDataNascimento())
                .dataEntrada(d.getDataEntrada())
                .dataPrevisaoSaida(d.getDataPrevisaoSaida())
                .dataSaida(d.getDataSaida())
                .status(d.getStatus())
                .regime(d.getRegime())
                .crime(d.getCrime())
                .sentencaAnos(d.getSentencaAnos())
                .cela(celaResumo)
                .criadoEm(d.getCriadoEm())
                .atualizadoEm(d.getAtualizadoEm())
                .build();
    }
}
