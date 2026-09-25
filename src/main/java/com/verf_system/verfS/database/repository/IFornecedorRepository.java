package com.verf_system.verfS.database.repository;

import com.verf_system.verfS.database.entity.FornecedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IFornecedorRepository extends JpaRepository<FornecedorEntity, Long> {

    public boolean existsByCnpj(String cnpj);

}
