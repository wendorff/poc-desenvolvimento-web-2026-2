package br.edu.ufcspa.sgfh.application.service;

import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.domain.model.User;
import br.edu.ufcspa.sgfh.testdouble.FakePasswordHasher;
import br.edu.ufcspa.sgfh.testdouble.InMemoryRoleRepository;
import br.edu.ufcspa.sgfh.testdouble.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    private InMemoryUserRepository users;
    private InMemoryRoleRepository roles;
    private UserService service;
    private String roleId;

    @BeforeEach
    void setUp() {
        users = new InMemoryUserRepository();
        roles = new InMemoryRoleRepository();
        service = new UserService(users, roles, new FakePasswordHasher());
        roleId = roles.save(new Role(null, "FARMACEUTICO", "desc", null)).id();
    }

    private User newUser(String email) {
        return new User(null, "Maria Silva", email, null, roleId, true, null);
    }

    @Test
    @DisplayName("Cria usuario, normaliza e-mail e guarda apenas o hash da senha")
    void createsUserWithHashedPassword() {
        User created = service.create(newUser("  Maria.Silva@UFCSPA.edu.br "), "senha123");

        assertEquals("maria.silva@ufcspa.edu.br", created.email());
        assertEquals("hashed:senha123", created.passwordHash());
        assertNotEquals("senha123", created.passwordHash());
    }

    @Test
    @DisplayName("Recusa usuario sem senha")
    void rejectsUserWithoutPassword() {
        BusinessException error = assertThrows(BusinessException.class,
                () -> service.create(newUser("a@b.com"), "  "));

        assertTrue(error.getMessage().contains("Informe uma senha"));
    }

    @Test
    @DisplayName("Recusa senha com menos de 6 caracteres")
    void rejectsShortPassword() {
        BusinessException error = assertThrows(BusinessException.class,
                () -> service.create(newUser("a@b.com"), "123"));

        assertTrue(error.getMessage().contains("ao menos 6"));
    }

    @Test
    @DisplayName("Recusa e-mail duplicado")
    void rejectsDuplicateEmail() {
        service.create(newUser("maria@ufcspa.edu.br"), "senha123");

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.create(newUser("MARIA@ufcspa.edu.br"), "senha123"));

        assertTrue(error.getMessage().contains("Já existe um usuário"));
    }

    @Test
    @DisplayName("Recusa papel inexistente")
    void rejectsUnknownRole() {
        User user = new User(null, "Maria", "m@b.com", null, "papel-invalido", true, null);

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.create(user, "senha123"));

        assertTrue(error.getMessage().contains("papel selecionado não existe"));
    }

    @Test
    @DisplayName("Atualizar sem informar senha mantem o hash anterior")
    void updateWithoutPasswordKeepsHash() {
        User created = service.create(newUser("maria@ufcspa.edu.br"), "senha123");

        User updated = service.update(created.id(),
                new User(null, "Maria Souza", "maria@ufcspa.edu.br", null, roleId, true, null), null);

        assertEquals("Maria Souza", updated.name());
        assertEquals("hashed:senha123", updated.passwordHash());
        assertEquals(created.createdAt(), updated.createdAt());
    }

    @Test
    @DisplayName("Atualizar com nova senha troca o hash")
    void updateWithPasswordChangesHash() {
        User created = service.create(newUser("maria@ufcspa.edu.br"), "senha123");

        User updated = service.update(created.id(),
                new User(null, "Maria", "maria@ufcspa.edu.br", null, roleId, true, null), "novaSenha");

        assertEquals("hashed:novaSenha", updated.passwordHash());
    }

    @Test
    @DisplayName("Exclui usuario")
    void deletesUser() {
        User created = service.create(newUser("maria@ufcspa.edu.br"), "senha123");

        service.deleteById(created.id());

        assertEquals(0, users.findAll().size());
    }
}
