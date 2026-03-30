package com.nick.myApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.twilio.rest.api.v2010.account.call.Payment;

@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @NotBlank(message = "Order number cannot be empty")
    @Size(max = 30, message = "Order number must be at most 30 characters")
    @Column(name = "order_no", nullable = false, length = 30)
    private String orderNo;

    @NotNull(message = "Total amount cannot be null")
    @Digits(integer = 36, fraction = 2, message = "Total amount must be a valid decimal with up to 36 digits and 2 decimals")
    @Column(name = "total_amount", nullable = false, precision = 38, scale = 2)
    private BigDecimal totalAmount;

    @NotBlank(message = "Status cannot be empty")
    @Size(max = 20, message = "Status must be at most 20 characters")
    @Column(name = "status", nullable = false, length = 20)
    private String status = "pending";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", columnDefinition = "VARCHAR(20) DEFAULT 'NA'")
    private PaymentMethod paymentMethod = PaymentMethod.NA;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    @PrePersist
    protected void onCreate() {
        if (this.orderNo == null || this.orderNo.trim().isEmpty()) {
            this.orderNo = "ORD-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void calculateTotal() {
        if (items != null && !items.isEmpty()) {
            this.totalAmount = items.stream()
                    .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.totalAmount = BigDecimal.ZERO;
        }
    }
}
