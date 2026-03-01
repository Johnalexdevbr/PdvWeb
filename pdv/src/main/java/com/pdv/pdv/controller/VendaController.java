package com.pdv.pdv.controller;

import com.pdv.pdv.model.Venda;
import com.pdv.pdv.model.Produto;
import com.pdv.pdv.model.ItemVenda; // Importe a entidade ItemVenda
import com.pdv.pdv.repository.VendaRepository;
import com.pdv.pdv.repository.ProdutoRepository;
import com.pdv.pdv.repository.ItemVendaRepository; // Importe o repository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

    @RestController
    @RequestMapping("/venda/api")
    public class VendaController {

        @Autowired
        private VendaRepository vendaRepository;

        @Autowired
        private ProdutoRepository produtoRepository;

        // CORRIGIDO: Nome exato que o Java vai reconhecer lá embaixo
        @Autowired
        private ItemVendaRepository itemVendaRepository;

    @PostMapping("/finalizar")
    @Transactional
    public ResponseEntity<?> finalizarVenda(@RequestBody Map<String, Object> request) {
        try {
            Double total = Double.parseDouble(request.get("total").toString());
            // Captura forma de pagamento se houver no seu front-end
            String formaPagamento = request.get("formaPagamento") != null ? request.get("formaPagamento").toString() : "DINHEIRO";

            // 1. Salva a Venda (O "Cabeçalho")
            Venda venda = new Venda();
            venda.setTotal(total);
            venda.setFormaPagamento(formaPagamento);
            venda.setDataHora(LocalDateTime.now());
            vendaRepository.save(venda);

            if (request.containsKey("itens")) {
                List<Map<String, Object>> itens = (List<Map<String, Object>>) request.get("itens");

                for (Map<String, Object> itemMap : itens) {
                    Long produtoId = Long.parseLong(itemMap.get("id").toString());
                    int qtdVendida = Double.valueOf(itemMap.get("quantidade").toString()).intValue();

                    Produto produto = produtoRepository.findById(produtoId)
                            .orElseThrow(() -> new RuntimeException("Produto não encontrado ID: " + produtoId));

                    // 2. Baixa o Estoque
                    int estoqueAtual = (produto.getEstoque() != null) ? produto.getEstoque() : 0;
                    if (estoqueAtual < qtdVendida) {
                        throw new RuntimeException("Estoque insuficiente para: " + produto.getNome());
                    }
                    produto.setEstoque(estoqueAtual - qtdVendida);
                    produtoRepository.save(produto);

                    // 3. SALVA O ITEM DA VENDA (Isso alimenta o seu relatório profissional)
                    ItemVenda iv = new ItemVenda();
                    iv.setVenda(venda); // Liga o item à venda que criamos acima
                    iv.setProduto(produto); // Liga o item ao produto
                    iv.setQuantidade((double) qtdVendida); // Converte para o Double da sua entidade
                    iv.setPrecoUnitario(produto.getPreco()); // Registra o preço praticado no momento
                    itemVendaRepository.save(iv);
                }
            }

            // ... (fim do seu código de salvar itemVenda)
            return ResponseEntity.ok(Map.of("status", "sucesso", "idVenda", venda.getId()));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    } // FECHA O MÉTODO finalizarVenda
    } // FECHA A CLASSE VendaController