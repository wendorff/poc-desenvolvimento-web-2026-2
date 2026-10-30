package br.edu.ufcspa.sgfh.infrastructure.persistence.mapper;

import br.edu.ufcspa.sgfh.domain.model.User;
import br.edu.ufcspa.sgfh.infrastructure.persistence.document.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserDocumentMapper {

    public User toDomain(UserDocument document) {
        return new User(
                document.id(),
                document.name(),
                document.email(),
                document.passwordHash(),
                document.roleId(),
                document.active(),
                document.createdAt());
    }

    public UserDocument toDocument(User user) {
        return new UserDocument(
                user.id(),
                user.name(),
                user.email(),
                user.passwordHash(),
                user.roleId(),
                user.active(),
                user.createdAt());
    }
}
