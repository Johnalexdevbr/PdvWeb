package com.pdv.pdv.controller;

import com.pdv.pdv.model.Produto;
import com.pdv.pdv.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // --- NOVO MÉTODO: Pesquisa produtos por parte do nome ---
    @GetMapping("/buscar")
    @ResponseBody
    public List<Produto> buscarPorNome(@RequestParam("nome") String nome) {
        // Este método usa a linha que adicionamos no ProdutoRepository
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    // 1. Lista todos os produtos na tela principal
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        return "produtos";
    }

    // 2. API para a Frente de Caixa: Retorna JSON por ID
    @GetMapping("/{id}")
    @ResponseBody
    public Optional<Produto> buscarPorId(@PathVariable Long id) {
        return produtoRepository.findById(id);
    }

    // 3. Abre a tela para cadastrar um novo produto
    @GetMapping("/novo")
    public String exibirFormularioCadastro(Model model) {
        model.addAttribute("produto", new Produto());
        return "cadastro";
    }

    // 4. Salva tanto novos produtos quanto edições
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("produto") Produto produto) {
        produtoRepository.save(produto);
        return "redirect:/produtos";
    }

    // 5. Abre a tela de edição
    @GetMapping("/editar/{id}")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        model.addAttribute("produto", produto);
        return "cadastro";
    }

    // 6. Botão de entrada rápida de estoque
    @PostMapping("/entrada-estoque")
    public String entradaEstoque(@RequestParam("id") Long id, @RequestParam("quantidade") Double quantidade) {
        Produto produto = produtoRepository.findById(id).orElseThrow();
        Double estoqueAtual = (produto.getQuantidade() != null) ? produto.getQuantidade() : 0.0;
        produto.setQuantidade(estoqueAtual + quantidade);
        produtoRepository.save(produto);
        return "redirect:/produtos";
    }

    // 7. Remove o produto do sistema
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Long id) {
        produtoRepository.deleteById(id);
        return "redirect:/produtos";
    }
}