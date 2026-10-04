package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.repository.IFuncionarioRepository;
import com.verf_system.verfS.dto.request.FuncionarioDto;
import com.verf_system.verfS.dto.response.FuncionarioResponseDto;
import com.verf_system.verfS.exception.DadoDuplicadoException;
import com.verf_system.verfS.exception.NaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FuncionarioService {
    private final IFuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(FuncionarioDto funcionarioDto) {
        if(funcionarioRepository.existsByEmail(funcionarioDto.getEmail())){
            throw new DadoDuplicadoException("Funcionario com email " + funcionarioDto.getEmail() + " já cadastrado");
        }
        funcionarioRepository.save(FuncionarioEntity.builder()
                .nome(funcionarioDto.getNome())
                .cargo(funcionarioDto.getCargo())
                .email(funcionarioDto.getEmail())
                .senhaHash(passwordEncoder.encode(funcionarioDto.getSenha()))
                .nivelDeAcesso(funcionarioDto.getNivelDeAcesso())
                .build());
    }

    public List<FuncionarioResponseDto> findAll() {
        List<FuncionarioEntity> funcionarios = funcionarioRepository.findAll();
        List<FuncionarioResponseDto> funcionarioResponseDtos = new ArrayList<>();
        for (FuncionarioEntity funcionarioEntity : funcionarios) {
            funcionarioResponseDtos.add(new  FuncionarioResponseDto(funcionarioEntity));
        }


        return funcionarioResponseDtos;
    }

    public FuncionarioResponseDto findById(Long id) {
        FuncionarioEntity funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Funcionario encontrado"));
        FuncionarioResponseDto funcionarioResponseDto = new  FuncionarioResponseDto(funcionario);
        return funcionarioResponseDto;
    }

    public void inativar(Long id) {
        FuncionarioEntity funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Funcionario encontrado"));
        funcionario.setAtivo(false);

        funcionarioRepository.save(funcionario);
    }
}
