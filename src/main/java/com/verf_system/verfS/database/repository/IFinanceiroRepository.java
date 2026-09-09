package com.verf_system.verfS.database.repository;

import com.verf_system.verfS.database.entity.FinanceiroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFinanceiroRepository extends JpaRepository<FinanceiroEntity, Long> {
}
