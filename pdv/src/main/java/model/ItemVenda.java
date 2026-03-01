package com.pdv.pdv.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "item_venda") // Resolve o erro: Cannot resolve table
@Data
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "venda_id") // Resolve o erro: Cannot resolve column 'venda_id'
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "produto_id") // Resolve o erro: Cannot resolve column 'produto_id'
    private Produto produto;

    private Double quantidade;
    private Double precoUnitario;
}