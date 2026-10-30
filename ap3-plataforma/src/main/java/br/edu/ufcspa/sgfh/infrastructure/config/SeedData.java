package br.edu.ufcspa.sgfh.infrastructure.config;

import br.edu.ufcspa.sgfh.application.port.out.RoleRepositoryPort;
import br.edu.ufcspa.sgfh.application.port.out.UserRepositoryPort;
import br.edu.ufcspa.sgfh.application.service.UserService;
import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.domain.model.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class SeedData implements CommandLineRunner {

    private final RoleRepositoryPort roles;
    private final UserRepositoryPort users;
    private final UserService userService;

    public SeedData(RoleRepositoryPort roles, UserRepositoryPort users, UserService userService) {
        this.roles = roles;
        this.users = users;
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        seedRoles();
        seedDemoUsers();
    }

    private void seedRoles() {
        if (roles.count() > 0) {
            return;
        }
        List.of(
                new Role(null, Role.ADMINISTRATOR, "Acesso total, gerencia usuários e papéis", Instant.now()),
                new Role(null, Role.PHARMACIST, "Dispensa medicamentos e gerencia o estoque", Instant.now()),
                new Role(null, Role.TECHNICIAN, "Registra entradas e transferências de estoque", Instant.now()),
                new Role(null, Role.AUDITOR, "Acesso somente leitura aos registros e à auditoria", Instant.now())
        ).forEach(roles::save);
    }

    private void seedDemoUsers() {
        if (users.count() > 0) {
            return;
        }
        createDemoUser("Administrador Demo", "admin@sgfh.local", "admin123", Role.ADMINISTRATOR);
        createDemoUser("Farmacêutica Demo", "farmaceutico@sgfh.local", "farma123", Role.PHARMACIST);
        createDemoUser("Técnico Demo", "tecnico@sgfh.local", "tecnico123", Role.TECHNICIAN);
        createDemoUser("Auditora Demo", "auditor@sgfh.local", "auditor123", Role.AUDITOR);
    }

    private void createDemoUser(String name, String email, String password, String roleName) {
        roles.findByName(roleName).ifPresent(role -> userService.create(
                new User(null, name, email, null, role.id(), true, null), password));
    }
}
