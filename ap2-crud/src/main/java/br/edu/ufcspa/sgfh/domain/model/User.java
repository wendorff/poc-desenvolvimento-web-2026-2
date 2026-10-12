package br.edu.ufcspa.sgfh.domain.model;

import java.time.Instant;

public record User(
        String id,
        String name,
        String email,
        String passwordHash,
        String roleId,
        boolean active,
        Instant createdAt
) {
    public User withPasswordHash(String newPasswordHash) {
        return new User(id, name, email, newPasswordHash, roleId, active, createdAt);
    }

    public User withCreatedAt(Instant newCreatedAt) {
        return new User(id, name, email, passwordHash, roleId, active, newCreatedAt);
    }
}
