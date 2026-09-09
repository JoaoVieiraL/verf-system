package com.verf_system.verfS.database.repository;

import com.verf_system.verfS.database.entity.ProducoesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IProducoesRepository extends JpaRepository<ProducoesEntity, Long> {
}
