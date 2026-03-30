package com.nick.myApp.repos;

import com.nick.myApp.models.OrderItem;
import com.nick.myApp.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderItemRepo extends JpaRepository<OrderItem, Integer> {

    // 根據 order 查返所有 items
    List<OrderItem> findByOrder(Orders order);

    List<OrderItem> findByOrderId(int orderId);
}
