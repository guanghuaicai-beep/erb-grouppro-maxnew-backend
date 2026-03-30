package com.nick.myApp.repos;

import com.nick.myApp.models.Orders;
import com.nick.myApp.models.Users;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepo extends JpaRepository<Orders, Long> {

    Optional<Orders> findById(Long id);

    List<Orders> findByUser(Users user);

}
