package com.nick.myApp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,
        String orderNo,
        BigDecimal totalAmount,
        String status,
        String paymentMethod,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        List<OrderItemDTO> items) {
}
