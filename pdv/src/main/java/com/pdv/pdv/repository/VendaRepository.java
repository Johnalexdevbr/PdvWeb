package com.pdv.pdv.repository;

import com.pdv.pdv.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Mantém o que já funciona para o fechamento
    List<Venda> findByDataHoraAfter(LocalDateTime data);

    List<Venda> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // ADICIONE ESTA LINHA: Busca todas as vendas ordenando pelo ID do maior para o menor
    List<Venda> findAllByOrderByIdDesc();
}