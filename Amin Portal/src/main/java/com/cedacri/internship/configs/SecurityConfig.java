package com.cedacri.internship.configs;

import com.cedacri.internship.entities.user.AdminUser;
import com.cedacri.internship.services.AdminUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

@Configuration
public class SecurityConfig {
    @Bean
    SecurityFilterChain open(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/login.xhtml",
                        "/jakarta.faces.resource/**",
                        "/resources/**").permitAll()
                .anyRequest().authenticated())

                .formLogin(form -> form
                .loginPage("/login.xhtml")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index.xhtml", true)
                .failureUrl("/login.xhtml?error")
                .permitAll())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login.xhtml?logout")
                        .permitAll())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                new LoginUrlAuthenticationEntryPoint("/login.xhtml?denied")));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(AdminUserService service) {
        return (username -> {
            AdminUser adminUser = service.findByUsername(username);
            return User.withUsername(username)
                    .password(adminUser.getPassword())
                    .roles(String.valueOf(adminUser.getRole()))
                    .build();
        });
    }
}