package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.ItensReceitaEntity;
import com.verf_system.verfS.database.entity.ReceitaEntity;
import com.verf_system.verfS.database.entity.TintaEntity;
import com.verf_system.verfS.database.repository.IItensReceitaRepository;
import com.verf_system.verfS.database.repository.IReceitaRepository;
import com.verf_system.verfS.database.repository.ITintaRepository;
import com.verf_system.verfS.dto.ItensReceitaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItensReceitaService {
    private final IItensReceitaRepository itensReceitaRepository;
    private final IReceitaRepository receitaRepository;
    private final ITintaRepository tintaRepository;

    public void save(ItensReceitaDto itensReceitaDto) {
        ReceitaEntity receita = receitaRepository.findById(itensReceitaDto.getIdReceita()).orElseThrow(()-> new RuntimeException("Nenhuma receita encontrada"));
        TintaEntity tinta = tintaRepository.findById(itensReceitaDto.getIdTintaMateriaPrima()).orElseThrow(()-> new RuntimeException("Nenhum tinta Encontrada"));
        itensReceitaRepository.save(ItensReceitaEntity.builder()
                .receitaRef(receita)
                .tintaMateriaPrimaRef(tinta)
                .proporcaoPercentual(itensReceitaDto.getProporcaoPercentual())
                .build());
    }

    public List<ItensReceitaEntity> findAll() {
        List<ItensReceitaEntity> itens = itensReceitaRepository.findAll();
        if (itens.isEmpty()) {
            throw new RuntimeException("Nenhum Item de Receita encontrado");
        }

        return itens;
    }

    public ItensReceitaEntity findById(Long id) {
        ItensReceitaEntity item = itensReceitaRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhum Item de Receita encontrado"));

        return item;
    }
}
