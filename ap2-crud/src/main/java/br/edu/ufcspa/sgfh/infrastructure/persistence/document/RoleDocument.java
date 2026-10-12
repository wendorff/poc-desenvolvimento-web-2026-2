package br.edu.ufcspa.sgfh.infrastructure.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "roles")
public record RoleDocument(
        @Id String id,
        @Indexed(unique = true) String name,
        String description,
        Instant createdAt
) {
}
