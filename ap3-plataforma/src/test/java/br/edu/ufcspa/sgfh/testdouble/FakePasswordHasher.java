package br.edu.ufcspa.sgfh.testdouble;

import br.edu.ufcspa.sgfh.application.port.out.PasswordHasher;

public class FakePasswordHasher implements PasswordHasher {

    @Override
    public String hash(String plainPassword) {
        return "hashed:" + plainPassword;
    }

    @Override
    public boolean matches(String plainPassword, String passwordHash) {
        return hash(plainPassword).equals(passwordHash);
    }
}
