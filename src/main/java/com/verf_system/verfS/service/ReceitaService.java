package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.entity.ReceitaEntity;
import com.verf_system.verfS.database.entity.TintaEntity;
import com.verf_system.verfS.database.repository.IFuncionarioRepository;
import com.verf_system.verfS.database.repository.IReceitaRepository;
import com.verf_system.verfS.database.repository.ITintaRepository;
import com.verf_system.verfS.dto.request.ReceitaDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceitaService {
    private final IReceitaRepository receitaRepository;
    private final ITintaRepository tintaRepository;
    private final IFuncionarioRepository funcionarioRepository;

    public void save(ReceitaDto receitaDto) {
        TintaEntity tinta = tintaRepository.findById(receitaDto.getIdTintaResultante()).orElseThrow(()-> new NaoEncontradoException("Nenhuma tinta Encontrada"));
        FuncionarioEntity funcionario = funcionarioRepository.findById(receitaDto.getIdCriadoPor()).orElseThrow(()-> new NaoEncontradoException("Nenhum funcionario Encontrado"));

        receitaRepository.save(ReceitaEntity.builder()
                .nome(receitaDto.getNome())
                .tintaResultante(tinta)
                .criadoPor(funcionario)
                .valorPorLitro(receitaDto.getValorPorLitro())
                .build());
    }

    public List<ReceitaEntity> findAll() {
        List<ReceitaEntity> receitas = receitaRepository.findAll();
        if (receitas.isEmpty()) {
            throw new NaoEncontradoException("Nenhuma Receita encontrada");
        }

        return receitas;
    }

    public ReceitaEntity findById(Long id) {
        ReceitaEntity receita = receitaRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhuma Receita encontrada"));

        return receita;
    }
}
