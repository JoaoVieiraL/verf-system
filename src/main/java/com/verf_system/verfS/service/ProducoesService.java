package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FuncionarioEntity;
import com.verf_system.verfS.database.entity.ProducoesEntity;
import com.verf_system.verfS.database.entity.ReceitaEntity;
import com.verf_system.verfS.database.repository.IFuncionarioRepository;
import com.verf_system.verfS.database.repository.IProducoesRepository;
import com.verf_system.verfS.database.repository.IReceitaRepository;
import com.verf_system.verfS.dto.ProducoesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProducoesService {
    private final IProducoesRepository producoesRepository;
    private final IReceitaRepository receitaRepository;
    private final IFuncionarioRepository funcionarioRepository;

    public void save(ProducoesDto producoesDto) {
        ReceitaEntity receita = receitaRepository.findById(producoesDto.getIdReceita()).orElseThrow(()-> new RuntimeException("Nenhuma receita encontrada"));
        FuncionarioEntity funcionario = funcionarioRepository.findById(producoesDto.getIdDuncionario()).orElseThrow(()-> new RuntimeException("Nenhum funcionario encontrado"));

        producoesRepository.save(ProducoesEntity.builder()
                .receitaRef(receita)
                .funcionarioRef(funcionario)
                .volumeProduzido(producoesDto.getVolumeProduzido())
                .dataProducao(producoesDto.getDataProducao())
                .build());
    }

    public List<ProducoesEntity> findAll() {
        List<ProducoesEntity> producoes = producoesRepository.findAll();
        if (producoes.isEmpty()) {
            throw new RuntimeException("Nenhuma Producao encontrada");
        }

        return producoes;
    }

    public ProducoesEntity findById(Long id) {
        ProducoesEntity producao = producoesRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhuma Producao encontrada"));

        return producao;
    }
}
