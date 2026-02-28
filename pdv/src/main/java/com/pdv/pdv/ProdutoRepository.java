package com.pdv.pdv.repository;

import com.pdv.pdv.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Esta linha permite buscar produtos que contêm o texto digitado (ex: "coca" traz "Coca-Cola")
    // O 'IgnoreCase' garante que não importa se você digitar maiúsculo ou minúsculo.
    List<Produto> findByNomeContainingIgnoreCase(String nome);

}