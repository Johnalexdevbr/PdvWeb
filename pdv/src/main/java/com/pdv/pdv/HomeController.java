package com.pdv.pdv.controller;

import com.pdv.pdv.model.Caixa;
import com.pdv.pdv.model.Venda;
import com.pdv.pdv.repository.CaixaRepository;
import com.pdv.pdv.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CaixaRepository caixaRepository;

    @Autowired
    private VendaRepository vendaRepository;

    // =========================
    // NAVEGAÇÃO PRINCIPAL
    // =========================
    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/")
    public String index() { return "redirect:/home"; }

    @GetMapping("/home")
    public String home(Model model) {
        boolean aberto = caixaRepository.findTopByStatusOrderByDataAberturaDesc("ABERTO").isPresent();
        model.addAttribute("caixaAberto", aberto);

        if (aberto) {
            Caixa caixaAberto = caixaRepository.findTopByStatusOrderByDataAberturaDesc("ABERTO").get();
            model.addAttribute("valorAbertura", caixaAberto.getValorAbertura());
            model.addAttribute("totalVendas", caixaAberto.getTotalVendas() != null ? caixaAberto.getTotalVendas() : 0.0);
            model.addAttribute("totalSangria", caixaAberto.getTotalSangria() != null ? caixaAberto.getTotalSangria() : 0.0);
        }

        return "home";
    }

    @GetMapping("/venda")
    public String venda() { return "venda"; }

    @GetMapping("/relatorios")
    public String relatorios(@RequestParam(value = "data", required = false) String data, Model model) {
        List<Venda> vendas;
        try {
            if (data != null && !data.isEmpty()) {
                LocalDate localDate = LocalDate.parse(data);
                vendas = vendaRepository.findByDataHoraBetween(localDate.atStartOfDay(), localDate.atTime(23, 59, 59));
            } else {
                vendas = vendaRepository.findAllByOrderByIdDesc();
            }
        } catch (Exception e) {
            vendas = vendaRepository.findAllByOrderByIdDesc();
        }

        double totalFaturado = vendas.stream().mapToDouble(v -> v.getTotal() != null ? v.getTotal() : 0.0).sum();
        double totalDescontos = vendas.stream().mapToDouble(v -> v.getDesconto() != null ? v.getDesconto() : 0.0).sum();

        model.addAttribute("listaVendas", vendas);
        model.addAttribute("totalFaturado", totalFaturado);
        model.addAttribute("totalDescontos", totalDescontos);
        model.addAttribute("vendasHoje", vendas.size());

        return "relatorios";
    }
}