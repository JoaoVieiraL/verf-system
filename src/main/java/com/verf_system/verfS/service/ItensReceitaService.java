package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.ItensReceitaEntity;
import com.verf_system.verfS.database.repository.IItensReceitaRepository;
import com.verf_system.verfS.dto.ItensReceitaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItensReceitaService {
    private final IItensReceitaRepository itensReceitaRepository;

    public void save(ItensReceitaDto itensReceitaDto) {
        itensReceitaRepository.save(ItensReceitaEntity.builder()
                .receitaRef(itensReceitaDto.getReceitaRef())
                .tintaMateriaPrimaRef(itensReceitaDto.getTintaMateriaPrimaRef())
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
