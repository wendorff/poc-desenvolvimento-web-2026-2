package br.edu.ufcspa.sgfh.infrastructure.config;

import br.edu.ufcspa.sgfh.application.port.out.RoleRepositoryPort;
import br.edu.ufcspa.sgfh.domain.model.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class SeedData implements CommandLineRunner {

    private final RoleRepositoryPort roles;

    public SeedData(RoleRepositoryPort roles) {
        this.roles = roles;
    }

    @Override
    public void run(String... args) {
        if (roles.count() > 0) {
            return;
        }
        List.of(
                new Role(null, "ADMINISTRADOR", "Acesso total, gerencia usuários e papéis", Instant.now()),
                new Role(null, "FARMACEUTICO", "Dispensa medicamentos e gerencia o estoque", Instant.now()),
                new Role(null, "TECNICO", "Registra entradas e transferências de estoque", Instant.now()),
                new Role(null, "AUDITOR", "Acesso somente leitura aos registros e à auditoria", Instant.now())
        ).forEach(roles::save);
    }
}
