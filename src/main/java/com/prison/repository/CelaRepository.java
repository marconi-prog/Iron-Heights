package com.prison.repository;

import com.prison.model.Cela;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CelaRepository extends JpaRepository<Cela, Long> {

    Optional<Cela> findByNumero(String numero);
    boolean existsByNumero(String numero);

    List<Cela> findByBloco(String bloco);
    List<Cela> findByStatus(Cela.StatusCela status);

    @Query("""
        SELECT c FROM Cela c
        WHERE c.status NOT IN ('MANUTENCAO')
        AND (SELECT COUNT(d) FROM Detento d WHERE d.cela.id = c.id AND d.status = 'ATIVO') < c.capacidade
    """)
    List<Cela> findCelasComVaga();
}
