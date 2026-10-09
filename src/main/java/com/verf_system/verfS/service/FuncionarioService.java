package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.repository.IFuncionarioRepository;
import com.verf_system.verfS.dto.request.FuncionarioDto;
import com.verf_system.verfS.dto.request.FuncionarioUpdateDto;
import com.verf_system.verfS.dto.response.FuncionarioResponseDto;
import com.verf_system.verfS.exception.DadoDuplicadoException;
import com.verf_system.verfS.exception.NaoEncontradoException;
import com.verf_system.verfS.exception.RegraDeNegocioException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public FuncionarioResponseDto save(FuncionarioDto funcionarioDto) {
        if(funcionarioRepository.existsByEmail(funcionarioDto.getEmail())){
            throw new DadoDuplicadoException("Funcionario com email " + funcionarioDto.getEmail() + " já cadastrado");
        }
        FuncionarioEntity funcionario = funcionarioRepository.save(FuncionarioEntity.builder()
                .nome(funcionarioDto.getNome())
                .cargo(funcionarioDto.getCargo())
                .email(funcionarioDto.getEmail())
                .senhaHash(passwordEncoder.encode(funcionarioDto.getSenha()))
                .nivelDeAcesso(funcionarioDto.getNivelDeAcesso())
                .build());

        return new FuncionarioResponseDto(funcionario);
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

    public void atualizar(Long id, FuncionarioUpdateDto dados) {
        FuncionarioEntity funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Funcionario encontrado"));
        if(funcionarioRepository.existsByEmailAndIdNot(dados.getEmail(), id)){
            throw new DadoDuplicadoException("Funcionario com email " + dados.getEmail() + " já cadastrado");
        }
        if(isUsuarioLogado(id) && (Boolean.FALSE.equals(dados.getAtivo()) || dados.getNivelDeAcesso() != funcionario.getNivelDeAcesso())){
            throw new RegraDeNegocioException("Você não pode inativar nem alterar o nível de acesso do seu próprio usuário.");
        }
        funcionario.setNome(dados.getNome());
        funcionario.setCargo(dados.getCargo());
        funcionario.setEmail(dados.getEmail());
        funcionario.setNivelDeAcesso(dados.getNivelDeAcesso());
        if(dados.getSenha() != null && !dados.getSenha().isBlank()) {
            funcionario.setSenhaHash(passwordEncoder.encode(dados.getSenha()));
        }
        if(dados.getAtivo() != null) {
            funcionario.setAtivo(dados.getAtivo());
        }
        funcionarioRepository.save(funcionario);
    }

    public void excluir(Long id) {
        FuncionarioEntity funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Funcionario encontrado"));
        if(isUsuarioLogado(id)){
            throw new RegraDeNegocioException("Você não pode excluir o seu próprio usuário.");
        }
        try {
            funcionarioRepository.delete(funcionario);
        } catch (DataIntegrityViolationException e) {
            throw new RegraDeNegocioException("Este usuário possui registros vinculados e não pode ser excluído. Inative-o pela edição.");
        }
    }

    // Compara o id com o funcionário autenticado pelo token
    private boolean isUsuarioLogado(Long id) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal instanceof FuncionarioEntity logado && logado.getId().equals(id);
    }

    public void inativar(Long id) {
        FuncionarioEntity funcionario = funcionarioRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Funcionario encontrado"));
        funcionario.setAtivo(false);

        funcionarioRepository.save(funcionario);
    }
}
