package com.univvoting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.univvoting.repository.UserRepository;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByUniversityId(username)
                .map(u -> org.springframework.security.core.userdetails.User
                        .withUsername(u.getUniversityId())
                        .password(u.getPasswordHash())
                        .roles(u.getRole())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                    .ignoringRequestMatchers("/api/auth/**", "/api/user/login", "/api/user/register")
                    .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .maximumSessions(1)
                        .expiredSessionStrategy(event -> {
                            event.getResponse().setStatus(401);
                            event.getResponse().getWriter().write("{\"error\":\"Session expired\"}");
                        })
                        .and()
                        .sessionFixation().migrateSession())
                .authorizeHttpRequests(auth -> auth
                        // Allow access to API endpoints for login and registration
                        .requestMatchers("/api/user/login", "/api/user/register", "/api/auth/**", "/error").permitAll()
                        // Allow access to static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        // Allow access to the React app - include all static resources
                        .requestMatchers("/", "/index.html", "/*.js", "/*.css", "/assets/**", "/*.json", "/*.ico", "/static/**").permitAll()
                        // API endpoints access control
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/public/**").permitAll() // Public access endpoints
                        .requestMatchers("/api/public/election-list").permitAll() // New public election list endpoint
                        .requestMatchers("/api/debug/**").permitAll() // Debug endpoints
                        .requestMatchers("/api/elections").permitAll() // Public access to view elections
                        .requestMatchers("/api/**").authenticated()
                        // React router paths - these should be forwarded to index.html
                        .requestMatchers("/login", "/register", "/profile", "/elections", "/vote", "/admin/**").permitAll()
                        .anyRequest().authenticated())
                // Keep form login for backward compatibility during transition
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/elections", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        // Add a special API endpoint for logging out
                        .addLogoutHandler((request, response, authentication) -> {
                            // Handle any special cleanup needed
                        })
                        .logoutUrl("/logout") // The default logout URL
                        .logoutSuccessHandler((request, response, authentication) -> {
                            // For API calls, return 200 OK
                            if (request.getHeader("Accept") != null && 
                                request.getHeader("Accept").contains("application/json")) {
                                response.setStatus(200);
                                response.getWriter().write("{\"success\":true}");
                                response.setContentType("application/json");
                            } else {
                                // For browser calls, redirect
                                response.sendRedirect("/login?logout=true");
                            }
                        })
                        .permitAll())
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (request.getHeader("Accept") != null &&
                                request.getHeader("Accept").contains("application/json")) {
                                response.setStatus(403);
                                response.getWriter().write("{\"error\":\"Access denied\"}");
                                response.setContentType("application/json");
                            } else {
                                response.sendRedirect("/accessDenied");
                            }
                        }));
        return http.build();
    }
}
