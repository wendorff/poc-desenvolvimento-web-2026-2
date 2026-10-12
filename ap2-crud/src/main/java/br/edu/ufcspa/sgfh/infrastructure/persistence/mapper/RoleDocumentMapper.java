package br.edu.ufcspa.sgfh.infrastructure.persistence.mapper;

import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.infrastructure.persistence.document.RoleDocument;
import org.springframework.stereotype.Component;

@Component
public class RoleDocumentMapper {

    public Role toDomain(RoleDocument document) {
        return new Role(
                document.id(),
                document.name(),
                document.description(),
                document.createdAt());
    }

    public RoleDocument toDocument(Role role) {
        return new RoleDocument(
                role.id(),
                role.name(),
                role.description(),
                role.createdAt());
    }
}
