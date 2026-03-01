package com.pdv.pdv.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaixaRepository extends JpaRepository<com.pdv.pdv.model.Caixa, Long> {

    // Retorna o caixa pelo status (ABERTO ou FECHADO)
    Optional<com.pdv.pdv.model.Caixa> findByStatus(String status);

    // Retorna todos os caixas pelo status
    List<com.pdv.pdv.model.Caixa> findAllByStatus(String status);

    List<com.pdv.pdv.model.Caixa> findAllByStatusOrderByDataFechamentoDesc(String status);

    // Retorna o último caixa aberto (caso queira histórico futuramente)
    Optional<com.pdv.pdv.model.Caixa> findTopByStatusOrderByDataAberturaDesc(String status);
}