package br.edu.ufcspa.sgfh.infrastructure.security;

import br.edu.ufcspa.sgfh.application.port.out.PasswordHasher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public String hash(String plainPassword) {
        return encoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plainPassword, String passwordHash) {
        return encoder.matches(plainPassword, passwordHash);
    }
}
