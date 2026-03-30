package com.nick.myApp.controllers;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.WebDataBinder;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.nick.myApp.models.*;
import com.nick.myApp.repos.*;

import lombok.RequiredArgsConstructor;
import com.nick.myApp.dto.OrderDTO;
import com.nick.myApp.dto.OrderItemDTO;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrdersController {

    @Autowired
    private OrdersRepo ordersRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetails(@PathVariable Long id, Authentication authentication) {
        String identifier = authentication.getName();
        Users user = usersRepo.findByEmailIgnoreCase(identifier)
                .orElseGet(() -> usersRepo.findByMobile(identifier)
                        .orElseThrow(() -> new RuntimeException("User not found: " + identifier)));

        Orders order = ordersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        // 確保訂單屬於該 user
        if (order.getUser() == null || order.getUser().getId() != user.getId()) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }
        List<Map<String, Object>> items = orderItemRepo.findByOrder(order).stream()
                .map(oi -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("courseName", oi.getCourse().getCourseName());
                    map.put("quantity", oi.getQuantity());
                    map.put("price", oi.getPrice());
                    map.put("subTotal", oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));
                    return map;
                })
                .toList();

        return ResponseEntity.ok(Map.of(
                "id", order.getId(),
                "status", order.getStatus(),
                "paymentMethod", order.getPaymentMethod(),
                "totalAmount", order.getTotalAmount(),
                "items", items));
    }

    @GetMapping("/history/{identifier:.+}")
    public ResponseEntity<List<OrderDTO>> getOrderHistory(@PathVariable String identifier) {
        Users user = identifier.contains("@")
                ? usersRepo.findByEmailIgnoreCase(identifier).orElseThrow(() -> new RuntimeException("User not found"))
                : usersRepo.findByMobile(identifier).orElseThrow(() -> new RuntimeException("User not found"));

        List<OrderDTO> orders = ordersRepo.findByUser(user).stream().map(order -> {
            List<OrderItemDTO> items = orderItemRepo.findByOrder(order).stream().map(oi -> new OrderItemDTO(
                    oi.getCourse().getCourseName(),
                    oi.getQuantity(),
                    oi.getPrice(),
                    oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())))).toList();

            return new OrderDTO(
                    order.getId(),
                    order.getOrderNo(),
                    order.getTotalAmount(),
                    order.getStatus(),
                    order.getPaymentMethod().getValue(),
                    order.getCreatedAt(),
                    order.getPaidAt(),
                    items);
        }).toList();

        return ResponseEntity.ok(orders);
    }

    @PostMapping("/payment-success/{id}")
    @Transactional
    public ResponseEntity<?> paymentSuccess(@PathVariable Long id,
            @RequestParam(required = false) String method,
            Authentication authentication) {

        // 取出 user
        String identifier = authentication.getName();
        Users user = usersRepo.findByEmailIgnoreCase(identifier)
                .orElseGet(() -> usersRepo.findByMobile(identifier)
                        .orElseThrow(() -> new RuntimeException("User not found: " + identifier)));

        // 取出 order
        Orders order = ordersRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        // 檢查 order 屬於邊個 user
        if (order.getUser() == null || order.getUser().getId() != user.getId()) {
            return ResponseEntity.status(403).body(Map.of("error", "Unauthorized access"));
        }

        // 更新狀態
        order.setStatus("paid");
        order.setPaidAt(LocalDateTime.now());

        // 設定 paymentMethod
        PaymentMethod chosenMethod = PaymentMethod.fromValue(method);
        order.setPaymentMethod(chosenMethod);
        /*
         * try {
         * PaymentMethod chosenMethod = PaymentMethod.fromValue(method);
         * order.setPaymentMethod(chosenMethod);
         * } catch (IllegalArgumentException e) {
         * order.setPaymentMethod(PaymentMethod.NA); // fallback
         * }
         */
        ordersRepo.save(order);
        cartRepo.deleteByUser(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment successful, order already paid and cart cleared",
                "orderId", order.getId(),
                "status", order.getStatus(),
                "paymentMethod", order.getPaymentMethod().getValue()));
    }

}
