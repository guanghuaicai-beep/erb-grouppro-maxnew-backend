package com.nick.myApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // 允許帶 token（Authorization）
        config.setAllowCredentials(true);

        // ✅ 允許的前端來源（不要用 "*"）
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "https://*.ngrok-free.dev",
                "https://erb-group-project-ngrok.netlify.app"));

        // 允許所有 header（包含 Authorization）
        config.setAllowedHeaders(List.of("*"));

        // 允許所有 HTTP 方法
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 讓前端可以讀到 Authorization
        config.setExposedHeaders(List.of("Authorization"));

        // （可選）預檢請求快取 1 小時
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}