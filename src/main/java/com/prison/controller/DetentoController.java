package com.prison.controller;

import com.prison.dto.DetentoDTO;
import com.prison.model.Detento;
import com.prison.service.DetentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/detentos")
@RequiredArgsConstructor
public class DetentoController {

    private final DetentoService detentoService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<DetentoDTO.Response> criar(@Valid @RequestBody DetentoDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detentoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<Page<DetentoDTO.Response>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Detento.StatusDetento status,
            @RequestParam(required = false) Detento.Regime regime,
            @RequestParam(required = false) Long celaId,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(detentoService.listar(nome, status, regime, celaId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetentoDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(detentoService.buscarPorId(id));
    }

    @GetMapping("/matricula/{matricula}")
    public ResponseEntity<DetentoDTO.Response> buscarPorMatricula(@PathVariable String matricula) {
        return ResponseEntity.ok(detentoService.buscarPorMatricula(matricula));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<DetentoDTO.Response> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DetentoDTO.Request request) {
        return ResponseEntity.ok(detentoService.atualizar(id, request));
    }

    @PatchMapping("/{id}/liberar")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Void> liberar(@PathVariable Long id) {
        detentoService.liberar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/transferir/{novaCelaId}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<Void> transferir(@PathVariable Long id, @PathVariable Long novaCelaId) {
        detentoService.transferir(id, novaCelaId);
        return ResponseEntity.noContent().build();
    }
}
