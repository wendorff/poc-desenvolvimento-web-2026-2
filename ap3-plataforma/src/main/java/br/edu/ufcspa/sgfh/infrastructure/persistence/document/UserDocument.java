package br.edu.ufcspa.sgfh.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
public record UserDocument(
        @Id String id,
        String name,
        @Indexed(unique = true) String email,
        String passwordHash,
        String roleId,
        boolean active,
        Instant createdAt
) {
}
