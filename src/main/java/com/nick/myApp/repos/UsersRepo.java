package com.nick.myApp.repos;

import java.util.Optional;

import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties.Apiversion.Use;
import org.springframework.data.jpa.repository.JpaRepository;

import com.nick.myApp.models.Users;

public interface UsersRepo extends JpaRepository<Users, Integer> {

    Optional<Users> findByEmailIgnoreCase(String email);

    Optional<Users> findByMobile(String mobile);

    Optional<Users> findByUsernameIgnoreCase(String username);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);
}
