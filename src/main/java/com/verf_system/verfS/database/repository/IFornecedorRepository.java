package com.verf_system.verfS.database.repository;

import com.verf_system.verfS.database.entity.FornecedorEntity;
import com.verf_system.verfS.dto.FornecedorDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFornecedorRepository extends JpaRepository<FornecedorEntity, Integer> {

    public List<FornecedorEntity> findAll();
    public FornecedorEntity findById(int id);
}
