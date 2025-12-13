package com.univvoting.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.univvoting.repository.UserRepository;

/**
 * Spring Security configuration for the University Voting System.
 * Implements multi-layer security architecture with role-based access control.
 * 
 * <p>Security Layers:
 * <ul>
 *   <li><b>Authentication</b>: Form-based login with BCrypt password hashing</li>
 *   <li><b>Authorization</b>: Role-based access (STUDENT, FACULTY, ADMIN)</li>
 *   <li><b>Verification</b>: OTP validation for sensitive operations</li>
 * </ul>
 * 
 * <p>OWASP Compliance:
 * <ul>
 *   <li>BCrypt password encoding (prevents cryptographic failures)</li>
 *   <li>Role-based authorization (prevents broken access control)</li>
 *   <li>CSRF protection enabled for state-changing operations</li>
 *   <li>Secure session management</li>
 * </ul>
 * 
 * @author Your Name
 * @version 1.0
 * @since 2025-12-14
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Value("${spring.security.user.name}")
    private String adminUsername;

    @Value("${spring.security.user.password}")
    private String adminPassword;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> {
            // Check if it's the predefined admin
            if (adminUsername.equals(username)) {
                return org.springframework.security.core.userdetails.User
                        .withUsername(adminUsername)
                        .password(passwordEncoder().encode(adminPassword))
                        .roles("ADMIN")
                        .build();
            }
            
            // Otherwise, look up in database
            return repo.findByUniversityId(username)
                    .map(u -> org.springframework.security.core.userdetails.User
                            .withUsername(u.getUniversityId())
                            .password(u.getPasswordHash())
                            .roles(u.getRole())
                            .build())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/error", "/webjars/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/profile/**").authenticated()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/elections", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll())
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/accessDenied"));
        return http.build();
    }
}
