package com.nick.myApp.utils;

import java.util.List;
import com.nick.myApp.models.Cart;

public class PriceUtils {
    public static double calculateTotal(List<Cart> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> {
                    double price = item.getCourse().getPrice(); // ✅ 已經係 Double
                    return price * item.getQuantity();
                })
                .sum();
    }
}
