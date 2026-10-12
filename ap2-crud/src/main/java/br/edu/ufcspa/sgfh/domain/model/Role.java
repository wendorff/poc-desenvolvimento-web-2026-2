package br.edu.ufcspa.sgfh.domain.model;

import java.time.Instant;

public record Role(
        String id,
        String name,
        String description,
        Instant createdAt
) {
    public Role withId(String newId) {
        return new Role(newId, name, description, createdAt);
    }

    public Role withCreatedAt(Instant newCreatedAt) {
        return new Role(id, name, description, newCreatedAt);
    }
}
