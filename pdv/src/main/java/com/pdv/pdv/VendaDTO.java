package com.pdv.pdv.dto;

import lombok.Data;
import java.util.List;

@Data
public class VendaDTO {
    private Double total;
    private List<ItemDTO> itens;
}