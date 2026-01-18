package com.abhinav.expense_tracker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf().disable()
            .authorizeHttpRequests(auth->auth
                .requestMatchers("/auth/**","/health","/h2-console/**","/error","/v3/api-docs/**","/swagger-ui/**","/ws/**").permitAll()
                .anyRequest().authenticated()
            )
            .cors(cors->cors.configurationSource(request->{
                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                corsConfiguration.setAllowedOrigins(java.util.List.of("http://localhost:4200", "https://your-production-frontend.com"));
                corsConfiguration.setAllowedMethods(java.util.List.of("GET","POST","PUT","DELETE","OPTIONS"));
                corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                corsConfiguration.setAllowCredentials(true);
                return corsConfiguration;
            }));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
    @Bean public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception{ return config.getAuthenticationManager();}
}
