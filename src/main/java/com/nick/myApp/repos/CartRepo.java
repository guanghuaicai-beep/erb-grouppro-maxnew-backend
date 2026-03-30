package com.nick.myApp.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nick.myApp.models.*;


public interface CartRepo extends JpaRepository <Cart , Integer> {
    
    List<Cart> findByUser(Users user);
    Cart findByUserAndCourse(Users user, Courses course);
    void deleteByUser(Users user);
}
