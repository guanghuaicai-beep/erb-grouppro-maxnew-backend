package com.nick.myApp.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nick.myApp.models.WishList;

import java.util.List;
import java.util.Optional;

public interface WishListRepo extends JpaRepository<WishList, Integer> {

    // view user wishlist
    List<WishList> findByUser_Id(int userId);

    // 查某用戶有冇收藏某課程
    Optional<WishList> findByUser_IdAndCourse_Id(int userId, int courseId);

    // delete wishlist
    void deleteByUser_IdAndCourse_Id(int userId, int courseId);

    boolean existsByUser_IdAndCourse_Id(int userId, int courseId);

}
