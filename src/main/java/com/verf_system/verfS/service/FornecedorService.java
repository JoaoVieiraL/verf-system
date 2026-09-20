package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FornecedorEntity;
import com.verf_system.verfS.database.repository.IFornecedorRepository;
import com.verf_system.verfS.dto.FornecedorDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FornecedorService {
    private final IFornecedorRepository fornecedorrepository;


    public List<FornecedorEntity> findAll() {
        List<FornecedorEntity> fornecedores = fornecedorrepository.findAll();
        if(fornecedores.isEmpty()){
            throw new NaoEncontradoException("Nenhum Fornecedor encontrado");
        }

        return fornecedores;
    }

    public FornecedorEntity findById(Long id) {
        FornecedorEntity fornecedor = fornecedorrepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Fornecedor encontrado"));

        return fornecedor;
    }

    public void save(FornecedorDto fornecedor) {
        fornecedorrepository.save(FornecedorEntity.builder()
                .cnpj(fornecedor.getCnpj())
                .nome(fornecedor.getNome())
                .telefone(fornecedor.getTelefone())
                .email(fornecedor.getEmail())
                .build());
    }

    public void inativar(Long id) {
        FornecedorEntity fornecedor = fornecedorrepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Fornecedor encontrado"));
        fornecedor.setAtivo(false);
        fornecedorrepository.save(fornecedor);
    }
}
