package com.verf_system.verfS.database.repository;

import com.verf_system.verfS.database.entity.ReceitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IReceitaRepository extends JpaRepository<ReceitaEntity, Long> {
}
