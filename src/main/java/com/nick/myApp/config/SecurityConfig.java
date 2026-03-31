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

import com.nick.myApp.models.Users;
import com.nick.myApp.repos.UsersRepo;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 正确注入
    private final UsersRepo usersRepo;
    private final JwtAuthenticationFilter jwtFilter;

    // 正确构造函数
    public SecurityConfig(UsersRepo usersRepo, JwtAuthenticationFilter jwtFilter) {
        this.usersRepo = usersRepo;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // 🔥 关键：让 Spring 使用你写的 CorsConfig
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
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
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 🔥 关键：引入你的 CORS 配置
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfig().corsConfigurationSource();
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