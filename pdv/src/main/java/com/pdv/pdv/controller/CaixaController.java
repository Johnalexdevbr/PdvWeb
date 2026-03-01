package com.pdv.pdv.controller;

import com.pdv.pdv.model.Caixa;
import com.pdv.pdv.model.ItemVenda;
import com.pdv.pdv.model.Venda;
import com.pdv.pdv.repository.CaixaRepository;
import com.pdv.pdv.repository.ItemVendaRepository;
import com.pdv.pdv.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/caixa/api")
public class CaixaController {

    @Autowired
    private CaixaRepository caixaRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ItemVendaRepository itemVendaRepository;

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
            // Busca as vendas desde a abertura do caixa atual
            List<Venda> vendasDoTurno = vendaRepository.findByDataHoraAfter(caixa.getDataAbertura());

            double totalVendas = 0.0;
            double totalDescontos = 0.0;
            List<Map<String, Object>> vendasDetalhadas = new ArrayList<>();

            for (Venda v : vendasDoTurno) {
                double valorVenda = v.getTotal() != null ? v.getTotal() : 0.0;
                totalVendas += valorVenda;
                totalDescontos += (v.getDesconto() != null ? v.getDesconto() : 0.0);

                // Objeto de venda para o relatório do JS
                Map<String, Object> vMap = new HashMap<>();
                vMap.put("id", v.getId());
                vMap.put("pagamento", v.getFormaPagamento() != null ? v.getFormaPagamento() : "N/I");
                vMap.put("valor", valorVenda);

                // Busca os itens desta venda usando o ID
                List<Map<String, Object>> itensMap = itemVendaRepository.findByVendaId(v.getId()).stream().map(item -> {
                    Map<String, Object> pMap = new HashMap<>();
                    pMap.put("nome", item.getProduto().getNome());
                    pMap.put("quantidade", item.getQuantidade());
                    pMap.put("preco", item.getPrecoUnitario());
                    return pMap;
                }).collect(Collectors.toList());

                vMap.put("produtos", itensMap);
                vendasDetalhadas.add(vMap);
            }

            double valorAbertura = caixa.getValorAbertura() != null ? caixa.getValorAbertura() : 0.0;
            double totalSangria = caixa.getTotalSangria() != null ? caixa.getTotalSangria() : 0.0;
            double saldoFinal = valorAbertura + totalVendas - totalSangria;

            // Atualiza o status do caixa
            caixa.setStatus("FECHADO");
            caixa.setDataFechamento(LocalDateTime.now());
            caixa.setTotalVendas(totalVendas);
            caixa.setValorFechamento(saldoFinal);
            caixaRepository.save(caixa);

            // Resposta unificada para o JavaScript
            return ResponseEntity.ok(Map.of(
                    "status", "sucesso",
                    "abertura", valorAbertura,
                    "vendas", totalVendas,
                    "descontos", totalDescontos,
                    "sangrias", totalSangria,
                    "totalGeral", saldoFinal,
                    "vendasDetalhadas", vendasDetalhadas
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro interno ao fechar caixa: " + e.getMessage());
        }
    }
}