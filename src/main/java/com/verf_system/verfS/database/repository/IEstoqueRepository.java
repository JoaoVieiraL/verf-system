package com.verf_system.verfS.database.repository;


import com.verf_system.verfS.database.entity.EstoqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEstoqueRepository extends JpaRepository<EstoqueEntity, Long> {
    Optional<EstoqueEntity> findByTintaId(Long idTinta);
}
