package com.verf_system.verfS.service;

import com.verf_system.verfS.database.entity.ItensReceitaEntity;
import com.verf_system.verfS.database.entity.ReceitaEntity;
import com.verf_system.verfS.database.entity.TintaEntity;
import com.verf_system.verfS.database.repository.IItensReceitaRepository;
import com.verf_system.verfS.database.repository.IProducoesRepository;
import com.verf_system.verfS.database.repository.IReceitaRepository;
import com.verf_system.verfS.database.repository.ITintaRepository;
import com.verf_system.verfS.dto.request.ItensReceitaDto;
import com.verf_system.verfS.dto.response.ItensReceitaResponseDto;
import com.verf_system.verfS.exception.DadoDuplicadoException;
import com.verf_system.verfS.exception.NaoEncontradoException;
import com.verf_system.verfS.exception.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItensReceitaService {
    private final IItensReceitaRepository itensReceitaRepository;
    private final IReceitaRepository receitaRepository;
    private final ITintaRepository tintaRepository;
    private final IProducoesRepository producoesRepository;

    private static final BigDecimal CEM = new BigDecimal("100");

    public void save(ItensReceitaDto itensReceitaDto) {
        ReceitaEntity receita = receitaRepository.findById(itensReceitaDto.getIdReceita()).orElseThrow(()-> new NaoEncontradoException("Nenhuma receita encontrada"));
        TintaEntity tinta = tintaRepository.findById(itensReceitaDto.getIdTintaMateriaPrima()).orElseThrow(()-> new NaoEncontradoException("Nenhuma tinta Encontrada"));

        if (producoesRepository.existsByReceitaRefId(receita.getId())) {
            throw new RegraDeNegocioException("Receita já usada em produção não pode ser alterada; crie uma nova receita");
        }
        if (tinta.getId().equals(receita.getTintaResultante().getId())) {
            throw new RegraDeNegocioException("A tinta resultante não pode ser ingrediente da própria receita");
        }
        if (itensReceitaRepository.existsByReceitaRefIdAndTintaMateriaPrimaRefId(receita.getId(), tinta.getId())) {
            throw new DadoDuplicadoException("Essa tinta já faz parte da receita");
        }

        BigDecimal somaAtual = BigDecimal.ZERO;

        List<ItensReceitaEntity> itens = itensReceitaRepository
                .findByReceitaRefId(receita.getId());

        for (ItensReceitaEntity item : itens) {
            somaAtual = somaAtual.add(item.getProporcaoPercentual());
        }
        BigDecimal novaSoma = somaAtual.add(itensReceitaDto.getProporcaoPercentual());

        if (novaSoma.compareTo(CEM) > 0) {
            throw new RegraDeNegocioException("A soma das proporções passaria de 100%");
        }


            itensReceitaRepository.save(ItensReceitaEntity.builder()
                .receitaRef(receita)
                .tintaMateriaPrimaRef(tinta)
                .proporcaoPercentual(itensReceitaDto.getProporcaoPercentual())
                .build());
    }

    public List<ItensReceitaResponseDto> findAll() {
        List<ItensReceitaEntity> itens = itensReceitaRepository.findAll();


        List<ItensReceitaResponseDto> itemResponses = new ArrayList<>();
        for (ItensReceitaEntity item : itens) {
            itemResponses.add(new ItensReceitaResponseDto(item));
        }

        return itemResponses;
    }

    public ItensReceitaResponseDto findById(Long id) {
        ItensReceitaEntity item = itensReceitaRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Nenhum Item de Receita encontrado"));

        return new ItensReceitaResponseDto(item);
    }
}
