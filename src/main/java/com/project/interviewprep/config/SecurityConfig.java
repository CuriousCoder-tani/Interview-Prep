package com.project.interviewprep.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.project.interviewprep.service.UserService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserService userDetailsService() {
        return new UserService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        return authManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityfilterchain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/css/**","/js/**","/images/**","/register/**","/error/**","/login","/submit").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        
                        .loginProcessingUrl("/submit")
                        .successHandler((request, response, authentication) -> {
                            String role = authentication.getAuthorities().stream()
                            .map(authority -> authority.getAuthority())
                            .findFirst()
                            .orElse(null);

                            if(role.equals("ADMIN")) {
                                response.sendRedirect("/admin");
                            } else if(role.equals("USER")) {
                                response.sendRedirect("/home");
                            } else {
                                response.sendRedirect("/login?error=invalid_role");
                            }

                        })                        
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }

}
