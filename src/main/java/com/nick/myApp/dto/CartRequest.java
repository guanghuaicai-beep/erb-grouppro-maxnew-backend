package com.nick.myApp.dto;

import lombok.Data;

@Data
public class CartRequest {
    private Integer courseId;
    private Integer quantity;
}

