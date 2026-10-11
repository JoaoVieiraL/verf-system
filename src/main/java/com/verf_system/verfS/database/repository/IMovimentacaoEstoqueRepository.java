package com.verf_system.verfS.database.repository;

import com.verf_system.verfS.database.entity.MovimentacaoEstoqueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoqueEntity, Long> {
    List<MovimentacaoEstoqueEntity> findAllByOrderByDataMovimentacaoDesc();
}
