package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.TintaEntity;
import com.verf_system.verfS.database.repository.ITintaRepository;
import com.verf_system.verfS.dto.TintaDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TintaService {

    private final ITintaRepository tintaRepository;

    public void save(TintaDto tintaDto) {
        TintaEntity tinta = TintaEntity.builder()
                .nome(tintaDto.getNome())
                .numeroHexadecimal(tintaDto.getNumeroHexadecimal())
                .codigo(tintaDto.getCodigo())
                .origemTinta(tintaDto.getOrigem())
                .ativo(true)
                .fornecedorRef(tintaDto.getFornecedorRef())
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();

        tintaRepository.save(tinta);
    }

    public List<TintaEntity> findAll() {
        List<TintaEntity> tintas = tintaRepository.findAll();
        if (tintas == null || tintas.isEmpty()) {
            throw new RuntimeException("Nenhuma Tinta encontrada.");
        }
        return tintas;
    }

    public TintaEntity findById(Integer id) {
        return tintaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tinta não encontrada com o ID: " + id));
    }

    public TintaEntity update(Integer id, TintaDto tintaDto) {
        TintaEntity tintaExistente = findById(id);

        tintaExistente.setNome(tintaDto.getNome());
        tintaExistente.setNumeroHexadecimal(tintaDto.getNumeroHexadecimal());
        tintaExistente.setCodigo(tintaDto.getCodigo());
        tintaExistente.setOrigemTinta(tintaDto.getOrigem());
        tintaExistente.setFornecedorRef(tintaDto.getFornecedorRef());
        tintaExistente.setAtualizadoEm(LocalDateTime.now());

        return tintaRepository.save(tintaExistente);
    }

    public void inativar(Integer id) {
        TintaEntity tintaBuscada = findById(id);
        tintaBuscada.setAtivo(false);
        tintaBuscada.setAtualizadoEm(LocalDateTime.now());
        tintaRepository.save(tintaBuscada);
    }
}