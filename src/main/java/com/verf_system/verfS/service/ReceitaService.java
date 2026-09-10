package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.ReceitaEntity;
import com.verf_system.verfS.database.repository.IReceitaRepository;
import com.verf_system.verfS.dto.ReceitaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceitaService {
    private final IReceitaRepository receitaRepository;

    public void save(ReceitaDto receitaDto) {
        receitaRepository.save(ReceitaEntity.builder()
                .nome(receitaDto.getNome())
                .tintaResultante(receitaDto.getTintaResultante())
                .criadoPor(receitaDto.getCriadoPor())
                .valorPorLitro(receitaDto.getValorPorLitro())
                .criadoEm(receitaDto.getCriadoEm())
                .build());
    }

    public List<ReceitaEntity> findAll() {
        List<ReceitaEntity> receitas = receitaRepository.findAll();
        if (receitas.isEmpty()) {
            throw new RuntimeException("Nenhuma Receita encontrada");
        }

        return receitas;
    }

    public ReceitaEntity findById(Long id) {
        ReceitaEntity receita = receitaRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhuma Receita encontrada"));

        return receita;
    }
}
