package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.repository.IFuncionarioRepository;
import com.verf_system.verfS.dto.FuncionarioDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioService {
    private final IFuncionarioRepository funcionarioRepository;

    public void save(FuncionarioDto funcionarioDto) {
        funcionarioRepository.save(FuncionarioEntity.builder()
                .nome(funcionarioDto.getNome())
                .cargo(funcionarioDto.getCargo())
                .email(funcionarioDto.getEmail())
                .senhaHash(funcionarioDto.getSenhaHash())
                .nivelDeAcesso(funcionarioDto.getNivelDeAcesso())
                .build());
    }

    public List<FuncionarioEntity> findAll() {
        List<FuncionarioEntity> funcionarios = funcionarioRepository.findAll();
        if (funcionarios.isEmpty()) {
            throw new NaoEncontradoException("Nenhum Funcionario encontrado");
        }

        return funcionarios;
    }

    public FuncionarioEntity findById(Long id) {
        FuncionarioEntity funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Funcionario encontrado"));

        return funcionario;
    }

    public void inativar(Long id) {
        FuncionarioEntity funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Funcionario encontrado"));
        funcionario.setAtivo(false);

        funcionarioRepository.save(funcionario);
    }
}
