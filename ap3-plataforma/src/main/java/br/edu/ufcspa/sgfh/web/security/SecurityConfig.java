package br.edu.ufcspa.sgfh.web.security;

import br.edu.ufcspa.sgfh.application.port.out.PasswordHasher;
import br.edu.ufcspa.sgfh.domain.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/style.css", "/htmx.min.js", "/app.js").permitAll()
                        .requestMatchers("/users/**", "/roles/**").hasRole(Role.ADMINISTRATOR)
                        .requestMatchers("/integrations/**").hasAnyRole(Role.ADMINISTRATOR, Role.PHARMACIST)
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(PasswordHasher hasher) {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return hasher.hash(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return hasher.matches(rawPassword.toString(), encodedPassword);
            }
        };
    }
}
