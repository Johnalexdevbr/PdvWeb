package com.pdv.pdv.dto;

import lombok.Data; // Se você usa Lombok, senão use Getters/Setters

@Data
public class ItemDTO {
    private Long produtoId;
    private Double quantidade;
}