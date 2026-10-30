package br.edu.ufcspa.sgfh.application.port.out;

public interface PasswordHasher {

    String hash(String plainPassword);

    boolean matches(String plainPassword, String passwordHash);
}
