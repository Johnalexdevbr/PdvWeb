package com.pdv.pdv.controller;

import com.pdv.pdv.model.Venda;
import com.pdv.pdv.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/venda/api")
public class VendaController {

    @Autowired
    private VendaRepository vendaRepository;

    // ================= FINALIZAR VENDA =================
    @PostMapping("/finalizar")
    public ResponseEntity<?> finalizarVenda(@RequestBody Map<String, Object> request) {
        try {
            // Pegando dados do front-end
            Double total = Double.parseDouble(request.get("total").toString());
            Double desconto = request.get("desconto") != null ? Double.parseDouble(request.get("desconto").toString()) : 0.0;
            String formaPagamento = request.get("formaPagamento") != null ? request.get("formaPagamento").toString() : "DINHEIRO";

            // Criando venda
            Venda venda = new Venda();
            venda.setTotal(total);
            venda.setDesconto(desconto);
            venda.setFormaPagamento(formaPagamento);
            venda.setDataHora(LocalDateTime.now());

            vendaRepository.save(venda);

            return ResponseEntity.ok(Map.of(
                    "status", "sucesso",
                    "mensagem", "Venda registrada com sucesso!",
                    "idVenda", venda.getId()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro ao registrar venda: " + e.getMessage());
        }
    }
}