package com.stylit.online.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig implements WebFluxConfigurer {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange ->
                        exchange.pathMatchers("/eureka/**").permitAll()
                                .pathMatchers("/public/**").permitAll()
                                .anyExchange()
                                .authenticated())
                .oauth2ResourceServer(spec -> spec.jwt(Customizer.withDefaults()))
                .cors(cors -> cors.configurationSource(request -> {
                    // Custom CORS configuration
                    var corsConfig = new CorsConfiguration();
                    corsConfig.addAllowedOrigin("*"); // Allow all origins (or specify allowed origins)
                    corsConfig.addAllowedMethod("*"); // Allow all methods (GET, POST, etc.)
                    corsConfig.addAllowedHeader("*"); // Allow all headers
                    return corsConfig;
                }));
        return serverHttpSecurity.build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Customize global CORS configuration if needed
        registry.addMapping("/**")
                .allowedOrigins("*") // Allow all origins (or specify allowed origins)
                .allowedMethods("*") // Allow all methods (GET, POST, etc.)
                .allowedHeaders("*"); // Allow all headers
    }
}
