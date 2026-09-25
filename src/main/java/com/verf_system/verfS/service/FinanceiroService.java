package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FinanceiroEntity;
import com.verf_system.verfS.database.repository.IFinanceiroRepository;
import com.verf_system.verfS.dto.request.FinanceiroDto;
import com.verf_system.verfS.exception.NaoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceiroService {
    private final IFinanceiroRepository financeiroRepository;

    public void save(FinanceiroDto financeiroDto) {
        financeiroRepository.save(FinanceiroEntity.builder()
                .receitaTotal(financeiroDto.getReceitaTotal())
                .compras(financeiroDto.getCompras())
                .perdas(financeiroDto.getPerdas())
                .saldoLiquido(financeiroDto.getReceitaTotal()
                        .subtract(financeiroDto.getCompras())
                        .subtract(financeiroDto.getPerdas()))
                .dataReferencia(financeiroDto.getDataReferencia())
                .build());
    }

    public List<FinanceiroEntity> findAll() {
        List<FinanceiroEntity> registros = financeiroRepository.findAll();
        if (registros.isEmpty()) {
            throw new NaoEncontradoException("Nenhum registro Financeiro encontrado");
        }

        return registros;
    }

    public FinanceiroEntity findById(Long id) {
        FinanceiroEntity financeiro = financeiroRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum registro Financeiro encontrado"));

        return financeiro;
    }
}
