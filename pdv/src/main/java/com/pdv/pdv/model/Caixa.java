package com.pdv.pdv.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "caixa")
public class Caixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;

    private Double valorAbertura;
    private Double valorFechamento;
    private Double totalVendas;
    private Double totalSangria = 0.0;
    private String status; // "ABERTO" ou "FECHADO"

    @Column(columnDefinition = "TEXT") // Para armazenar JSON das sangrias
    private String sangriasJson;

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // --- NOVOS MÉTODOS PARA SANGRIA ---
    public Double getTotalSangria() {
        return totalSangria != null ? totalSangria : 0.0;
    }

    public void setTotalSangria(Double totalSangria) {
        this.totalSangria = totalSangria;
    }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDateTime getDataFechamento() { return dataFechamento; }
    public void setDataFechamento(LocalDateTime dataFechamento) { this.dataFechamento = dataFechamento; }

    public Double getValorAbertura() { return valorAbertura; }
    public void setValorAbertura(Double valorAbertura) { this.valorAbertura = valorAbertura; }

    public Double getValorFechamento() { return valorFechamento; }
    public void setValorFechamento(Double valorFechamento) { this.valorFechamento = valorFechamento; }

    public Double getTotalVendas() { return totalVendas; }
    public void setTotalVendas(Double totalVendas) { this.totalVendas = totalVendas; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSangriasJson() { return sangriasJson; }
    public void setSangriasJson(String sangriasJson) { this.sangriasJson = sangriasJson; }
}