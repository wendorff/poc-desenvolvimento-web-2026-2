package br.edu.ufcspa.sgfh.web.security;

import br.edu.ufcspa.sgfh.application.port.out.PasswordHasher;
import br.edu.ufcspa.sgfh.domain.model.Role;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

@Configuration
public class SecurityConfig {

    static final String HX_REQUEST = "HX-Request";
    static final String HX_BOOSTED = "HX-Boosted";
    static final String HX_REDIRECT = "HX-Redirect";

    /**
     * Todo script, estilo e fonte vem do próprio servidor (bundle do Vite), sem nada inline,
     * então a política pode recusar qualquer código injetado na página.
     */
    static final String CONTENT_SECURITY_POLICY = String.join("; ",
            "default-src 'self'",
            "script-src 'self'",
            "style-src 'self'",
            "img-src 'self' data:",
            "font-src 'self'",
            "connect-src 'self'",
            "object-src 'none'",
            "base-uri 'self'",
            "form-action 'self'",
            "frame-ancestors 'none'");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/error", "/assets/**").permitAll()
                        .requestMatchers("/users/**", "/roles/**").hasRole(Role.ADMINISTRATOR)
                        .requestMatchers("/integrations/**").hasAnyRole(Role.ADMINISTRATOR, Role.PHARMACIST)
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout"))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(DelegatingAuthenticationEntryPoint.builder()
                                .addEntryPointFor(htmxLoginRedirect(), new RequestHeaderRequestMatcher(HX_REQUEST))
                                .defaultEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                                .build()))
                .requestCache(cache -> cache
                        .requestCache(pageRequestCache()))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(CONTENT_SECURITY_POLICY)));
        return http.build();
    }

    /**
     * Sessão expirada numa requisição do htmx: em vez de encaixar a tela de login dentro da
     * página, o cabeçalho HX-Redirect faz o navegador ir para o login.
     */
    private static AuthenticationEntryPoint htmxLoginRedirect() {
        return (request, response, exception) -> {
            response.setHeader(HX_REDIRECT, request.getContextPath() + "/login");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        };
    }

    /**
     * Guarda para depois do login apenas navegação de página inteira. Um fragmento do htmx
     * (ex.: resultado de consulta) abriria sozinho, sem layout.
     */
    private static RequestCache pageRequestCache() {
        HttpSessionRequestCache cache = new HttpSessionRequestCache();
        cache.setRequestMatcher(request -> HttpMethod.GET.matches(request.getMethod())
                && !request.getRequestURI().endsWith("/favicon.ico")
                && (request.getHeader(HX_REQUEST) == null || request.getHeader(HX_BOOSTED) != null));
        return cache;
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
