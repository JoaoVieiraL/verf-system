package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FornecedorEntity;
import com.verf_system.verfS.database.repository.IFornecedorRepository;
import com.verf_system.verfS.database.repository.ITintaRepository;
import com.verf_system.verfS.dto.request.FornecedorDto;
import com.verf_system.verfS.dto.response.FornecedorResponseDto;
import com.verf_system.verfS.exception.DadoDuplicadoException;
import com.verf_system.verfS.exception.NaoEncontradoException;
import com.verf_system.verfS.exception.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FornecedorService {
    private final IFornecedorRepository fornecedorRepository;
    private final ITintaRepository tintaRepository;


    public List<FornecedorResponseDto> findAll() {
        List<FornecedorEntity> fornecedores = fornecedorRepository.findAll();
        List<FornecedorResponseDto> fornecedorResponses = new ArrayList<>();

        for (FornecedorEntity fornecedor : fornecedores) {

            fornecedorResponses.add(new FornecedorResponseDto(fornecedor));
        }


        return fornecedorResponses;
    }

    public FornecedorResponseDto findById(Long id) {
        FornecedorEntity fornecedor = fornecedorRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Fornecedor encontrado"));
        FornecedorResponseDto responseDto = new FornecedorResponseDto(fornecedor);
        return responseDto;
    }

    public FornecedorResponseDto save(FornecedorDto fornecedor) {
        if(fornecedorRepository.existsByCnpj(fornecedor.getCnpj())){
            throw new DadoDuplicadoException("Fornecedor com CNPJ " + fornecedor.getCnpj() + " já cadastrado");
        }
        FornecedorEntity fornecedorCriado = fornecedorRepository.save(FornecedorEntity.builder()
                .cnpj(fornecedor.getCnpj())
                .nome(fornecedor.getNome())
                .telefone(fornecedor.getTelefone())
                .email(fornecedor.getEmail())
                .build());
        return new FornecedorResponseDto(fornecedorCriado);
    }

    public void atualizar(Long id, FornecedorDto dados) {
        FornecedorEntity fornecedor = fornecedorRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Fornecedor encontrado"));
        if(fornecedorRepository.existsByCnpjAndIdNot(dados.getCnpj(), id)){
            throw new DadoDuplicadoException("Fornecedor com CNPJ " + dados.getCnpj() + " já cadastrado");
        }
        fornecedor.setCnpj(dados.getCnpj());
        fornecedor.setNome(dados.getNome());
        fornecedor.setTelefone(dados.getTelefone());
        fornecedor.setEmail(dados.getEmail());
        if(dados.getAtivo() != null) {
            fornecedor.setAtivo(dados.getAtivo());
        }
        fornecedorRepository.save(fornecedor);
    }

    public void excluir(Long id) {
        FornecedorEntity fornecedor = fornecedorRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Fornecedor encontrado"));
        if(tintaRepository.existsByFornecedorRefId(id)){
            throw new RegraDeNegocioException("Este fornecedor possui tintas vinculadas e não pode ser excluído. Inative-o pela edição.");
        }
        fornecedorRepository.delete(fornecedor);
    }

    public void inativar(Long id) {
        FornecedorEntity fornecedor = fornecedorRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Fornecedor encontrado"));
        fornecedor.setAtivo(false);
        fornecedorRepository.save(fornecedor);
    }
}
