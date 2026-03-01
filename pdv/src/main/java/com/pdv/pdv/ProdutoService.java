package com.pdv.pdv.service;

import com.pdv.pdv.model.Produto;
import com.pdv.pdv.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public void salvar(Produto produto) {
        produtoRepository.save(produto);
    }

    public void deletar(Long id) {
        produtoRepository.deleteById(id);
    }

    // --- NOVO MÉTODO: Soma a quantidade atual com a nova entrada ---
    public void adicionarEstoque(Long id, Double quantidadeEntrada) {
        Produto produto = buscarPorId(id);
        if (produto != null) {
            // Se a quantidade atual for nula, tratamos como 0 para não dar erro
            double estoqueAtual = (produto.getQuantidade() != null) ? produto.getQuantidade() : 0.0;
            produto.setQuantidade(estoqueAtual + quantidadeEntrada);
            salvar(produto);
        }
    }
}