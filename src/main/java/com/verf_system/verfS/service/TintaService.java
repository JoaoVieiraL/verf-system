package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.FornecedorEntity;
import com.verf_system.verfS.database.entity.OrigemTinta;
import com.verf_system.verfS.database.entity.TintaEntity;
import com.verf_system.verfS.database.repository.IFornecedorRepository;
import com.verf_system.verfS.database.repository.ITintaRepository;
import com.verf_system.verfS.dto.TintaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TintaService {
    private final ITintaRepository tintaRepository;
    private final IFornecedorRepository fornecedorRepository;
    FornecedorEntity fornecedor = null;

    public void save(TintaDto tintaDto) {
        if(tintaDto.getOrigem() == OrigemTinta.COMPRADA) {
            if(tintaDto.getIdFornecedor() == null) {
                throw new RuntimeException ("Nenhum Fornecedor encontrado");
            }
            fornecedor = fornecedorRepository.findById(tintaDto.getIdFornecedor()).orElseThrow(() -> new RuntimeException("Nenhum Fornecedor encontrado"));

        }else if(tintaDto.getIdFornecedor() != null) {
            throw new RuntimeException ("Tinta Produzida nao deve ter Fornecedor");
        }
        tintaRepository.save(TintaEntity.builder()
                .nome(tintaDto.getNome())
                .numeroHexadecimal(tintaDto.getNumeroHexadecimal())
                .codigo(tintaDto.getCodigo())
                .origemTinta(tintaDto.getOrigem())
                .ativo(tintaDto.isAtivo())
                .fornecedorRef(fornecedor)
                .build());

    }
    public List<TintaEntity> findAll() {
        List<TintaEntity> tintas = tintaRepository.findAll();
        if (tintas.isEmpty()) {
            throw new RuntimeException("Nenhuma Tinta encontrada");
        }

        return tintas;
    }

    public TintaEntity findById(Long id) {
    TintaEntity tintaBuscada = tintaRepository.findById(id).orElseThrow(()-> new RuntimeException("nenhuma tinta Encontrada"));

        return tintaBuscada;
    }

    public void inativar(Long id) {
        TintaEntity tintaBuscada = tintaRepository.findById(id).orElseThrow(()-> new RuntimeException("Nenhuma tinta encontrada"));
        tintaBuscada.setAtivo(false);
        tintaRepository.save(tintaBuscada);
    }

}
