package com.verf_system.verfS.database.repository;

import com.verf_system.verfS.database.entity.ItensReceitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IItensReceitaRepository extends JpaRepository<ItensReceitaEntity, Long> {
    List<ItensReceitaEntity> findByReceitaRefId(Long idReceita);
}
