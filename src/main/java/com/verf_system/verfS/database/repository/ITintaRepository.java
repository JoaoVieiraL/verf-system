package com.verf_system.verfS.database.repository;

import com.verf_system.verfS.database.entity.TintaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITintaRepository extends JpaRepository<TintaEntity, Long> {
    public boolean existsByCodigo(String codigo);
    public boolean existsByNumeroHexadecimal(String numeroHexadecimal);

}
