package br.edu.ufcspa.sgfh.application.service;

import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import br.edu.ufcspa.sgfh.domain.exception.NotFoundException;
import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.domain.model.User;
import br.edu.ufcspa.sgfh.testdouble.InMemoryRoleRepository;
import br.edu.ufcspa.sgfh.testdouble.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoleServiceTest {

    private InMemoryRoleRepository roles;
    private InMemoryUserRepository users;
    private RoleService service;

    @BeforeEach
    void setUp() {
        roles = new InMemoryRoleRepository();
        users = new InMemoryUserRepository();
        service = new RoleService(roles, users);
    }

    private Role newRole(String name) {
        return new Role(null, name, "descricao", null);
    }

    @Test
    @DisplayName("Cria um papel e normaliza o nome para maiusculo")
    void createsRoleAndUppercasesName() {
        Role created = service.create(newRole("  farmaceutico  "));

        assertEquals("FARMACEUTICO", created.name());
        assertEquals(1, roles.count());
    }

    @Test
    @DisplayName("Recusa papel com nome duplicado")
    void rejectsDuplicateName() {
        service.create(newRole("AUDITOR"));

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.create(newRole("auditor")));

        assertTrue(error.getMessage().contains("Já existe um papel"));
    }

    @Test
    @DisplayName("Atualiza o papel preservando a data de criacao")
    void updateKeepsCreatedAt() {
        Role created = service.create(newRole("TECNICO"));
        Instant originalCreatedAt = created.createdAt();

        Role updated = service.update(created.id(), new Role(null, "TECNICO SENIOR", "nova", null));

        assertEquals("TECNICO SENIOR", updated.name());
        assertEquals(originalCreatedAt, updated.createdAt());
    }

    @Test
    @DisplayName("Recusa exclusao de papel em uso por algum usuario")
    void rejectsDeleteWhenRoleIsInUse() {
        Role role = service.create(newRole("ADMINISTRADOR"));
        users.save(new User(null, "Alguem", "a@b.com", "hash", role.id(), true, Instant.now()));

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.deleteById(role.id()));

        assertTrue(error.getMessage().contains("está em uso"));
        assertEquals(1, roles.count());
    }

    @Test
    @DisplayName("Exclui papel que nao esta em uso")
    void deletesUnusedRole() {
        Role role = service.create(newRole("ESTAGIARIO"));

        service.deleteById(role.id());

        assertEquals(0, roles.count());
    }

    @Test
    @DisplayName("Falha ao buscar papel inexistente")
    void failsWhenRoleDoesNotExist() {
        assertThrows(NotFoundException.class, () -> service.findById("nao-existe"));
    }
}
