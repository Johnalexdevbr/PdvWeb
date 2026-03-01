package com.pdv.pdv.controller;

import com.pdv.pdv.model.Caixa;
import com.pdv.pdv.model.Venda;
import com.pdv.pdv.repository.CaixaRepository;
import com.pdv.pdv.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/caixa/api")
public class CaixaController {

    @Autowired
    private CaixaRepository caixaRepository;

    @Autowired
    private VendaRepository vendaRepository;

    // ================= ABRIR CAIXA =================
    @PostMapping("/abrir")
    public ResponseEntity<?> abrirCaixa(@RequestBody Map<String, Double> request) {
        if (caixaRepository.findByStatus("ABERTO").isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um caixa aberto!");
        }

        Double valorAbertura = request.getOrDefault("valorInicial", 0.0);
        Caixa novoCaixa = new Caixa();
        novoCaixa.setValorAbertura(valorAbertura);
        novoCaixa.setDataAbertura(LocalDateTime.now());
        novoCaixa.setStatus("ABERTO");
        novoCaixa.setTotalSangria(0.0);
        caixaRepository.save(novoCaixa);

        return ResponseEntity.ok(Map.of(
                "status", "sucesso",
                "mensagem", "Caixa aberto com sucesso!",
                "valorAbertura", valorAbertura
        ));
    }

    // ================= REGISTRAR SANGRIA =================
    @PostMapping("/sangria")
    public ResponseEntity<?> registrarSangria(@RequestBody Map<String, Double> request) {
        Optional<Caixa> caixaOpt = caixaRepository.findByStatus("ABERTO");
        if (caixaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Não existe caixa aberto para registrar sangria.");
        }

        Double valor = request.get("valor");
        if (valor == null || valor <= 0) {
            return ResponseEntity.badRequest().body("Informe um valor válido para a sangria.");
        }

        Caixa caixa = caixaOpt.get();
        double totalSangria = caixa.getTotalSangria() != null ? caixa.getTotalSangria() : 0.0;
        caixa.setTotalSangria(totalSangria + valor);
        caixaRepository.save(caixa);

        return ResponseEntity.ok(Map.of(
                "status", "sucesso",
                "mensagem", "Sangria registrada com sucesso!",
                "totalSangria", caixa.getTotalSangria()
        ));
    }

    // ================= FECHAR CAIXA =================
    @PostMapping("/fechar")
    public ResponseEntity<?> fecharCaixa() {
        Optional<Caixa> caixaOpt = caixaRepository.findByStatus("ABERTO");
        if (caixaOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Não existe caixa aberto para fechar.");
        }

        try {
            Caixa caixa = caixaOpt.get();
            List<Venda> vendasDoTurno = vendaRepository.findByDataHoraAfter(caixa.getDataAbertura());

            Map<String, Double> porForma = new HashMap<>();
            double totalVendas = 0.0;
            double totalDescontos = 0.0;

            for (Venda v : vendasDoTurno) {
                double valorVenda = v.getTotal() != null ? v.getTotal() : 0.0;
                totalVendas += valorVenda;
                totalDescontos += (v.getDesconto() != null ? v.getDesconto() : 0.0);
                String forma = v.getFormaPagamento() != null ? v.getFormaPagamento() : "NÃO INFORMADO";
                porForma.put(forma, porForma.getOrDefault(forma, 0.0) + valorVenda);
            }

            double valorAbertura = caixa.getValorAbertura() != null ? caixa.getValorAbertura() : 0.0;
            double totalSangria = caixa.getTotalSangria() != null ? caixa.getTotalSangria() : 0.0;
            double saldoFinal = valorAbertura + totalVendas - totalSangria;

            caixa.setStatus("FECHADO");
            caixa.setDataFechamento(LocalDateTime.now());
            caixa.setTotalVendas(totalVendas);
            caixa.setValorFechamento(saldoFinal);
            caixaRepository.save(caixa);

            return ResponseEntity.ok(Map.of(
                    "status", "sucesso",
                    "abertura", valorAbertura,
                    "vendas", totalVendas,
                    "descontos", totalDescontos,
                    "sangrias", totalSangria,
                    "totalGeral", saldoFinal,
                    "detalhePagamento", porForma
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno ao processar valores: " + e.getMessage());
        }
    }
}