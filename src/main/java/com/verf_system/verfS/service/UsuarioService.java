package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.entity.UsuarioEntity;
import com.verf_system.verfS.database.repository.IFuncionarioRepository;
import com.verf_system.verfS.database.repository.IUsuarioRepository;
import com.verf_system.verfS.dto.UsuarioDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final IUsuarioRepository usuarioRepository;
    private final IFuncionarioRepository funcionarioRepository;

    public void save(UsuarioDto usuarioDto) {
        FuncionarioEntity funcionario = funcionarioRepository.findById(usuarioDto.getIdFuncionario()).orElseThrow(()-> new RuntimeException("Nenhum funcionario Encontrado"));
        usuarioRepository.save(UsuarioEntity.builder()
                .funcionarioRef(funcionario)
                .ultimoAcesso(usuarioDto.getUltimoAcesso())
                .ativo(usuarioDto.isAtivo())
                .build());
    }

    public List<UsuarioEntity> findAll() {
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            throw new RuntimeException("Nenhum Usuario encontrado");
        }

        return usuarios;
    }

    public UsuarioEntity findById(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhum Usuario encontrado"));

        return usuario;
    }

    public void inativar(Long id) {
        UsuarioEntity usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhum Usuario encontrado"));
        usuario.setAtivo(false);

        usuarioRepository.save(usuario);
    }
}
