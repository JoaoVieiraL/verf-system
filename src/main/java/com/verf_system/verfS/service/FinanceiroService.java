package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FinanceiroEntity;
import com.verf_system.verfS.database.repository.IFinanceiroRepository;
import com.verf_system.verfS.dto.FinanceiroDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceiroService {
    private final IFinanceiroRepository financeiroRepository;

    public void save(FinanceiroDto financeiroDto) {
        financeiroRepository.save(FinanceiroEntity.builder()
                .receita_total(financeiroDto.getReceita_total())
                .compras(financeiroDto.getCompras())
                .perdas(financeiroDto.getPerdas())
                .saldoLiquido(financeiroDto.getSaldoLiquido())
                .dataReferencia(financeiroDto.getDataReferencia())
                .build());
    }

    public List<FinanceiroEntity> findAll() {
        List<FinanceiroEntity> registros = financeiroRepository.findAll();
        if (registros.isEmpty()) {
            throw new RuntimeException("Nenhum registro Financeiro encontrado");
        }

        return registros;
    }

    public FinanceiroEntity findById(Long id) {
        FinanceiroEntity financeiro = financeiroRepository.findById(id).orElseThrow(() -> new RuntimeException("Nenhum registro Financeiro encontrado"));

        return financeiro;
    }
}
