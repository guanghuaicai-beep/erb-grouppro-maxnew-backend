package com.nick.myApp.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;
import com.nick.myApp.config.JwtAuthenticationFilter; // 🔥 這行必須加！
import org.springframework.context.annotation.Lazy;

import com.nick.myApp.models.Users;
import com.nick.myApp.repos.UsersRepo;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UsersRepo usersRepo;
    private final JwtAuthenticationFilter jwtFilter;

    // 🔥 加上 @Lazy 才能打破循環依賴
    public SecurityConfig(UsersRepo usersRepo, @Lazy JwtAuthenticationFilter jwtFilter) {
        this.usersRepo = usersRepo;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register", "/login", "/logout", "/forget_password",
                                "/reset_password")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/courses/**", "/categories/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/wishlist/**").authenticated()
                        .requestMatchers("/cart/**").authenticated()
                        .requestMatchers("/orders/**").authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // ✅ 修正這行
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            Optional<Users> userOpt = usersRepo.findByEmailIgnoreCase(identifier);
            if (userOpt.isEmpty()) {
                userOpt = usersRepo.findByMobile(identifier);
            }
            Users user = userOpt.orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
            return new org.springframework.security.core.userdetails.User(
                    identifier,
                    user.getPassword(),
                    java.util.Collections.emptyList());
        };
    }
}