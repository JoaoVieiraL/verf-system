package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FornecedorEntity;
import com.verf_system.verfS.database.repository.IFornecedorRepository;
import com.verf_system.verfS.dto.request.FornecedorDto;
import com.verf_system.verfS.dto.response.FornecedorResponseDto;
import com.verf_system.verfS.exception.DadoDuplicadoException;
import com.verf_system.verfS.exception.NaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FornecedorService {
    private final IFornecedorRepository fornecedorrepository;


    public List<FornecedorResponseDto> findAll() {
        List<FornecedorEntity> fornecedores = fornecedorrepository.findAll();
        List<FornecedorResponseDto> fornecedorResponses = new ArrayList<>();

        for (FornecedorEntity fornecedor : fornecedores) {

            fornecedorResponses.add(new FornecedorResponseDto(fornecedor));
        }
        if(fornecedores.isEmpty()){
            throw new NaoEncontradoException("Nenhum Fornecedor encontrado");
        }

        return fornecedorResponses;
    }

    public FornecedorResponseDto findById(Long id) {
        FornecedorEntity fornecedor = fornecedorrepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Fornecedor encontrado"));
        FornecedorResponseDto responseDto = new FornecedorResponseDto(fornecedor);
        return responseDto;
    }

    public void save(FornecedorDto fornecedor) {
        if(fornecedorrepository.existsByCnpj(fornecedor.getCnpj())){
            throw new DadoDuplicadoException("Fornecedor com CNPJ " + fornecedor.getCnpj() + " já cadastrado");
        }
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
