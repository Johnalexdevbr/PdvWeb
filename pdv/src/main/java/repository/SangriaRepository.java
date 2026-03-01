package com.pdv.pdv.repository;

import com.pdv.pdv.model.Caixa;
import com.pdv.pdv.model.Sangria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SangriaRepository extends JpaRepository<Sangria, Long> {
    List<Sangria> findByCaixa(Caixa caixa);
}