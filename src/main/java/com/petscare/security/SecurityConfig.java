package com.petscare.security;

import com.petscare.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private AuthSuccessHandler authSuccessHandler;
        @Autowired
        private AuthFailureHandler authFailureHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity in migration
                                .authorizeHttpRequests(auth -> auth
                                                // Public Endpoints
                                                .requestMatchers("/", "/index.html", "/aboutus.html", "/login.html", "/register.html",
                                                                "/shop.html", "/cart.html", "/adoption.html",
                                                                "/track.html",
                                                                "/style.css", "/css/**", "/js/**", "/images/**")
                                                .permitAll()
                                                .requestMatchers("/api/auth/**", "/login", "/api/emergency/**",
                                                                "/api/shop/**", "/api/adoption/**", "/api/track",
                                                                "/api/payment/**", "/api/donation/**")
                                                .permitAll()
                                                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/admin/doctor", "/api/admin/rescue_list")
                                                .permitAll()
                                                .requestMatchers("/thank_you.html")
                                                .permitAll()

                                                // Admin Endpoints
                                                .requestMatchers("/api/admin/stats").permitAll()
                                                .requestMatchers("/admin.html", "/api/admin/**", "/api/shop/**")
                                                .hasRole("ADMIN")

                                                // Doctor Endpoints
                                                .requestMatchers("/doctor.html", "/api/doctor/**")
                                                .hasAnyRole("DOCTOR", "ADMIN", "RESCUE")

                                                // Rescue Endpoints
                                                .requestMatchers("/rescue.html", "/api/rescue/**").hasAnyRole("RESCUE", "ADMIN", "DOCTOR")

                                                // Customer Endpoints
                                                .requestMatchers("/client.html", "/api/customer/**")
                                                .hasAnyRole("CUSTOMER", "ADMIN")

                                                // Default: Authenticated
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login.html")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/index.html")
                                                .invalidateHttpSession(true)
                                                .deleteCookies("JSESSIONID")
                                                .permitAll());
                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(CustomUserDetailsService userDetailsService) {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                provider.setUserDetailsService(userDetailsService);
                return new ProviderManager(provider);
        }
}
