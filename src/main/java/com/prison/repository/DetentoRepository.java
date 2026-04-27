package com.prison.repository;

import com.prison.model.Detento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DetentoRepository extends JpaRepository<Detento, Long> {

    Optional<Detento> findByMatricula(String matricula);
    Optional<Detento> findByCpf(String cpf);
    boolean existsByCpf(String cpf);

    @Query("""
        SELECT d FROM Detento d
        WHERE (:nome IS NULL OR UPPER(d.nome) LIKE UPPER(CONCAT('%', :nome, '%')))
        AND (:status IS NULL OR d.status = :status)
        AND (:regime IS NULL OR d.regime = :regime)
        AND (:celaId IS NULL OR d.cela.id = :celaId)
    """)
    Page<Detento> buscarComFiltros(
            @Param("nome") String nome,
            @Param("status") Detento.StatusDetento status,
            @Param("regime") Detento.Regime regime,
            @Param("celaId") Long celaId,
            Pageable pageable
    );

    @Query("SELECT COUNT(d) FROM Detento d WHERE d.status = 'ATIVO'")
    long countAtivos();

    @Query("SELECT COUNT(d) FROM Detento d WHERE d.cela.id = :celaId AND d.status = 'ATIVO'")
    long countAtivosByCelaId(@Param("celaId") Long celaId);
}
