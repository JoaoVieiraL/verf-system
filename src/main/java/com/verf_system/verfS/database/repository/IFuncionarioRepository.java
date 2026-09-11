package com.verf_system.verfS.database.repository;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFuncionarioRepository extends JpaRepository<FuncionarioEntity, Long> {
}
