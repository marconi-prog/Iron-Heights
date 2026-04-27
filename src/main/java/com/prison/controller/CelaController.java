package com.prison.controller;

import com.prison.dto.CelaDTO;
import com.prison.service.CelaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/celas")
@RequiredArgsConstructor
public class CelaController {

    private final CelaService celaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<CelaDTO.Response> criar(@Valid @RequestBody CelaDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(celaService.criar(request));
    }

    @GetMapping
    public ResponseEntity<Page<CelaDTO.Response>> listar(
            @PageableDefault(size = 20, sort = "numero") Pageable pageable) {
        return ResponseEntity.ok(celaService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CelaDTO.Response> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(celaService.buscarPorId(id));
    }

    @GetMapping("/com-vaga")
    public ResponseEntity<List<CelaDTO.Response>> buscarComVaga() {
        return ResponseEntity.ok(celaService.buscarComVaga());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','GESTOR')")
    public ResponseEntity<CelaDTO.Response> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CelaDTO.Request request) {
        return ResponseEntity.ok(celaService.atualizar(id, request));
    }

    @PatchMapping("/{id}/manutencao")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> colocarEmManutencao(@PathVariable Long id) {
        celaService.colocarEmManutencao(id);
        return ResponseEntity.noContent().build();
    }
}
