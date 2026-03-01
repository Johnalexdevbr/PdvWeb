package com.pdv.pdv.repository;

import com.pdv.pdv.model.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Long> {

    // Método útil para o seu relatório: busca todos os itens de uma venda específica
    List<ItemVenda> findByVendaId(Long vendaId);
}