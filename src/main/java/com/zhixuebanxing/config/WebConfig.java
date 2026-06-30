package com.zhixuebanxing.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class WebConfig {

    private final AppConfig appConfig;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        String origins = appConfig.getCors().getAllowedOrigins();
        if ("*".equals(origins)) {
            // 当允许所有来源时，使用setAllowedOriginPatterns避免与allowCredentials冲突
            configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        } else {
            configuration.setAllowedOrigins(Arrays.asList(origins.split(",")));
        }
        configuration.setAllowedMethods(Arrays.asList(appConfig.getCors().getAllowedMethods().split(",")));
        configuration.setAllowedHeaders(Arrays.asList(appConfig.getCors().getAllowedHeaders().split(",")));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
