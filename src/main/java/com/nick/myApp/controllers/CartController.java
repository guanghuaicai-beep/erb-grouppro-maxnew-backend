package com.nick.myApp.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.nick.myApp.dto.CartRequest;
import com.nick.myApp.models.*;
import com.nick.myApp.repos.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

        private final CartRepo cartRepo;
        private final UsersRepo usersRepo;
        private final CoursesRepo coursesRepo;
        private final OrdersRepo ordersRepo;
        private final OrderItemRepo orderItemRepo;

        // view cart
        @GetMapping
        public ResponseEntity<?> getCart(Authentication authentication) {
                String identifier = authentication.getName();
                Users user = usersRepo.findByEmailIgnoreCase(identifier)
                                .orElseGet(() -> usersRepo.findByMobile(identifier)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "User not found: " + identifier)));

                List<Map<String, Object>> cartItems = cartRepo.findByUser(user).stream()
                                .map(c -> {
                                        Map<String, Object> item = new HashMap<>();
                                        item.put("id", c.getId());
                                        item.put("courseId", c.getCourse().getId());
                                        item.put("courseName", c.getCourse().getCourseName());
                                        item.put("unitPrice", c.getCourse().getPrice());
                                        item.put("quantity", c.getQuantity());
                                        item.put("subTotal", c.getCourse().getPrice() * c.getQuantity());
                                        return item;
                                })
                                .collect(Collectors.toList());

                double total = cartItems.stream()
                                .mapToDouble(i -> ((Number) i.get("subTotal")).doubleValue())
                                .sum();

                return ResponseEntity.ok(Map.of("items", cartItems, "total", total));
        }

        @PostMapping("/add")
        public ResponseEntity<?> addToCart(@RequestBody CartRequest req, Authentication authentication) {
                String identifier = authentication.getName();
                Users user = usersRepo.findByEmailIgnoreCase(identifier)
                                .orElseGet(() -> usersRepo.findByMobile(identifier)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "User not found: " + identifier)));

                Courses course = coursesRepo.findById(req.getCourseId())
                                .orElseThrow(() -> new RuntimeException("Course not found: " + req.getCourseId()));

                Cart existing = cartRepo.findByUserAndCourse(user, course);
                Cart cart;

                if (existing != null) {
                        existing.setQuantity(existing.getQuantity() + req.getQuantity());
                        cart = cartRepo.save(existing);
                } else {
                        cart = new Cart();
                        cart.setUser(user);
                        cart.setCourse(course);
                        cart.setQuantity(req.getQuantity());

                        // 🔥 關鍵修復：自動填入時間
                        cart.setCreatedAt(LocalDateTime.now());

                        cart = cartRepo.save(cart);
                }

                return ResponseEntity.ok(Map.of(
                                "message", "Course added to cart successfully",
                                "cartId", cart.getId(),
                                "courseId", course.getId(),
                                "courseName", course.getCourseName(),
                                "unitPrice", course.getPrice(),
                                "quantity", cart.getQuantity(),
                                "subTotal", course.getPrice() * cart.getQuantity()));
        }

        @PostMapping("/checkout")
        @Transactional
        public ResponseEntity<Map<String, Object>> checkout(Authentication authentication) {
                String identifier = authentication.getName();
                Users user = usersRepo.findByEmailIgnoreCase(identifier)
                                .orElseGet(() -> usersRepo.findByMobile(identifier)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "User not found: " + identifier)));

                List<Cart> cartItems = cartRepo.findByUser(user);
                if (cartItems.isEmpty()) {
                        return ResponseEntity.badRequest().body(Map.of(
                                        "success", false,
                                        "message", "Cart is empty"));
                }

                Orders order = new Orders();
                order.setUser(user);
                order.setStatus("pending");
                order.setCreatedAt(LocalDateTime.now());
                order.setPaymentMethod(PaymentMethod.NA);

                BigDecimal total = BigDecimal.ZERO;
                List<OrderItem> orderItems = new ArrayList<>();
                for (Cart c : cartItems) {
                        OrderItem oi = new OrderItem();
                        oi.setOrder(order);
                        oi.setCourse(c.getCourse());
                        oi.setQuantity(c.getQuantity());
                        oi.setPrice(BigDecimal.valueOf(c.getCourse().getPrice()));
                        total = total.add(BigDecimal.valueOf(c.getCourse().getPrice())
                                        .multiply(BigDecimal.valueOf(c.getQuantity())));
                        orderItems.add(oi);
                }
                order.setTotalAmount(total);

                Orders savedOrder = ordersRepo.save(order);
                orderItems.forEach(orderItemRepo::save);

                return ResponseEntity.ok(Map.of(
                                "success", true,
                                "message", "Order created successfully, please proceed to payment",
                                "orderId", savedOrder.getId(),
                                "orderNo", savedOrder.getOrderNo(),
                                "total", total));
        }

        @PutMapping("/update/{id}")
        public ResponseEntity<?> updateQuantity(@PathVariable Integer id, @RequestParam int quantity) {
                Cart cart = cartRepo.findById(id)
                                .orElseThrow(() -> new RuntimeException("Cart item not found: " + id));

                cart.setQuantity(quantity);
                cartRepo.save(cart);

                return ResponseEntity.ok(Map.of(
                                "message", "Quantity updated",
                                "cartId", cart.getId(),
                                "courseId", cart.getCourse().getId(),
                                "courseName", cart.getCourse().getCourseName(),
                                "unitPrice", cart.getCourse().getPrice(),
                                "quantity", cart.getQuantity(),
                                "subTotal", cart.getCourse().getPrice() * cart.getQuantity()));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteItem(@PathVariable Integer id, Authentication authentication) {
                Cart cart = cartRepo.findById(id)
                                .orElseThrow(() -> new RuntimeException("Cart item not found: " + id));

                cartRepo.delete(cart);

                return ResponseEntity.ok(Map.of("message", "Course removed Successfully"));
        }

        @DeleteMapping("/clear")
        @Transactional
        public ResponseEntity<?> clearCart(Authentication authentication) {
                String identifier = authentication.getName();
                Users user = usersRepo.findByEmailIgnoreCase(identifier)
                                .orElseGet(() -> usersRepo.findByMobile(identifier)
                                                .orElseThrow(() -> new RuntimeException(
                                                                "User not found: " + identifier)));

                cartRepo.deleteByUser(user);

                return ResponseEntity.ok(Map.of("message", "All courses have been removed from cart"));
        }
}