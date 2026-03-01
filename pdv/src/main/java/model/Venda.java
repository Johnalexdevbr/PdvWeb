package com.pdv.pdv.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "vendas")
@Data
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Valor final (já com desconto subtraído)
    private Double total;

    // Valor do desconto aplicado
    private Double desconto;

    // NOVO: Armazena DINHEIRO, PIX, CARTAO, etc.
    private String formaPagamento;

    private LocalDateTime dataHora;

    // Executa automaticamente antes de salvar no banco
    @PrePersist
    protected void onCreate() {
        this.dataHora = LocalDateTime.now();
    }

    public Venda() {
    }

    public Venda(Double total, Double desconto, String formaPagamento) {
        this.total = total;
        this.desconto = desconto;
        this.formaPagamento = formaPagamento;
    }
}