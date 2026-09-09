package com.verf_system.verfS.database.repository;

import com.verf_system.verfS.database.entity.ItensReceitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IItensReceitaRepository extends JpaRepository<ItensReceitaEntity, Integer> {
}
