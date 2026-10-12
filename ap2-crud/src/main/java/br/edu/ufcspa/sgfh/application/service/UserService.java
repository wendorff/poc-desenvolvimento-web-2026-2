package br.edu.ufcspa.sgfh.application.service;

import br.edu.ufcspa.sgfh.application.port.out.PasswordHasher;
import br.edu.ufcspa.sgfh.application.port.out.RoleRepositoryPort;
import br.edu.ufcspa.sgfh.application.port.out.UserRepositoryPort;
import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import br.edu.ufcspa.sgfh.domain.exception.NotFoundException;
import br.edu.ufcspa.sgfh.domain.model.User;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final int MINIMUM_PASSWORD_LENGTH = 6;

    private final UserRepositoryPort users;
    private final RoleRepositoryPort roles;
    private final PasswordHasher passwordHasher;

    public UserService(UserRepositoryPort users, RoleRepositoryPort roles, PasswordHasher passwordHasher) {
        this.users = users;
        this.roles = roles;
        this.passwordHasher = passwordHasher;
    }

    public List<User> findAll() {
        return users.findAll();
    }

    public User findById(String id) {
        return users.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }

    public User create(User user, String plainPassword) {
        String email = normalizeEmail(user.email());
        ensureEmailIsAvailable(email, null);
        ensureRoleExists(user.roleId());

        if (plainPassword == null || plainPassword.isBlank()) {
            throw new BusinessException("Informe uma senha para o novo usuário");
        }
        ensurePasswordIsStrongEnough(plainPassword);

        User toSave = new User(
                null,
                trim(user.name()),
                email,
                passwordHasher.hash(plainPassword),
                user.roleId(),
                user.active(),
                Instant.now());
        return users.save(toSave);
    }

    public User update(String id, User user, String plainPassword) {
        User current = findById(id);
        String email = normalizeEmail(user.email());
        ensureEmailIsAvailable(email, id);
        ensureRoleExists(user.roleId());

        String passwordHash = current.passwordHash();
        if (plainPassword != null && !plainPassword.isBlank()) {
            ensurePasswordIsStrongEnough(plainPassword);
            passwordHash = passwordHasher.hash(plainPassword);
        }

        User toSave = new User(
                id,
                trim(user.name()),
                email,
                passwordHash,
                user.roleId(),
                user.active(),
                current.createdAt());
        return users.save(toSave);
    }

    public void deleteById(String id) {
        findById(id);
        users.deleteById(id);
    }

    private void ensureEmailIsAvailable(String email, String currentId) {
        Optional<User> existing = users.findByEmail(email);
        if (existing.isPresent() && !existing.get().id().equals(currentId)) {
            throw new BusinessException("Já existe um usuário com o e-mail " + email);
        }
    }

    private void ensureRoleExists(String roleId) {
        if (roleId == null || !roles.existsById(roleId)) {
            throw new BusinessException("O papel selecionado não existe");
        }
    }

    private void ensurePasswordIsStrongEnough(String plainPassword) {
        if (plainPassword.length() < MINIMUM_PASSWORD_LENGTH) {
            throw new BusinessException("A senha deve ter ao menos "
                    + MINIMUM_PASSWORD_LENGTH + " caracteres");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
