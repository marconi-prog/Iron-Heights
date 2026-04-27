package com.prison.service;

import com.prison.dto.CelaDTO;
import com.prison.exception.BusinessException;
import com.prison.exception.ResourceNotFoundException;
import com.prison.model.Cela;
import com.prison.repository.CelaRepository;
import com.prison.repository.DetentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CelaService {

    private final CelaRepository celaRepository;
    private final DetentoRepository detentoRepository;

    @Transactional
    public CelaDTO.Response criar(CelaDTO.Request request) {
        if (celaRepository.existsByNumero(request.getNumero())) {
            throw new BusinessException("Já existe uma cela com o número: " + request.getNumero());
        }
        Cela cela = Cela.builder()
                .numero(request.getNumero())
                .bloco(request.getBloco())
                .capacidade(request.getCapacidade())
                .tipo(request.getTipo())
                .build();
        return toResponse(celaRepository.save(cela));
    }

    @Transactional(readOnly = true)
    public Page<CelaDTO.Response> listar(Pageable pageable) {
        return celaRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public CelaDTO.Response buscarPorId(Long id) {
        return toResponse(findCelaById(id));
    }

    @Transactional(readOnly = true)
    public List<CelaDTO.Response> buscarComVaga() {
        return celaRepository.findCelasComVaga().stream().map(this::toResponse).toList();
    }

    @Transactional
    public CelaDTO.Response atualizar(Long id, CelaDTO.Request request) {
        Cela cela = findCelaById(id);
        if (!cela.getNumero().equals(request.getNumero()) && celaRepository.existsByNumero(request.getNumero())) {
            throw new BusinessException("Já existe outra cela com o número: " + request.getNumero());
        }
        long ocupacao = detentoRepository.countAtivosByCelaId(id);
        if (request.getCapacidade() < ocupacao) {
            throw new BusinessException("Nova capacidade (" + request.getCapacidade() + ") menor que ocupação atual (" + ocupacao + ").");
        }
        cela.setNumero(request.getNumero());
        cela.setBloco(request.getBloco());
        cela.setCapacidade(request.getCapacidade());
        cela.setTipo(request.getTipo());
        return toResponse(celaRepository.save(cela));
    }

    @Transactional
    public void colocarEmManutencao(Long id) {
        Cela cela = findCelaById(id);
        long ocupacao = detentoRepository.countAtivosByCelaId(id);
        if (ocupacao > 0) {
            throw new BusinessException("Não é possível colocar em manutenção. Cela possui " + ocupacao + " detentos ativos.");
        }
        cela.setStatus(Cela.StatusCela.MANUTENCAO);
        celaRepository.save(cela);
    }

    private Cela findCelaById(Long id) {
        return celaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cela não encontrada. ID: " + id));
    }

    private CelaDTO.Response toResponse(Cela c) {
        long ocupacao = detentoRepository.countAtivosByCelaId(c.getId());
        return CelaDTO.Response.builder()
                .id(c.getId())
                .numero(c.getNumero())
                .bloco(c.getBloco())
                .capacidade(c.getCapacidade())
                .ocupacaoAtual((int) ocupacao)
                .status(c.getStatus())
                .tipo(c.getTipo())
                .criadoEm(c.getCriadoEm())
                .atualizadoEm(c.getAtualizadoEm())
                .build();
    }
}
