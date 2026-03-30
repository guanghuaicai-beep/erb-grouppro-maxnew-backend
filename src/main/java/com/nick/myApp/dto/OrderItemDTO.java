
package com.nick.myApp.dto;

import java.math.BigDecimal;

public record OrderItemDTO(
        String courseName,
        int quantity,
        BigDecimal price,
        BigDecimal subTotal) {
}
