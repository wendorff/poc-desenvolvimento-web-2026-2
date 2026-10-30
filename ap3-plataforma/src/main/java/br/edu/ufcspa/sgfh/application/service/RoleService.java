package br.edu.ufcspa.sgfh.application.service;

import br.edu.ufcspa.sgfh.application.port.out.RoleRepositoryPort;
import br.edu.ufcspa.sgfh.application.port.out.UserRepositoryPort;
import br.edu.ufcspa.sgfh.domain.exception.BusinessException;
import br.edu.ufcspa.sgfh.domain.exception.NotFoundException;
import br.edu.ufcspa.sgfh.domain.model.Role;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepositoryPort roles;
    private final UserRepositoryPort users;

    public RoleService(RoleRepositoryPort roles, UserRepositoryPort users) {
        this.roles = roles;
        this.users = users;
    }

    public List<Role> findAll() {
        return roles.findAll();
    }

    public Role findById(String id) {
        return roles.findById(id)
                .orElseThrow(() -> new NotFoundException("Papel não encontrado"));
    }

    public Role create(Role role) {
        String name = normalize(role.name());
        ensureNameIsAvailable(name, null);
        Role toSave = new Role(null, name, trim(role.description()), Instant.now());
        return roles.save(toSave);
    }

    public Role update(String id, Role role) {
        Role current = findById(id);
        String name = normalize(role.name());
        if (current.isAdministrator() && !Role.ADMINISTRATOR.equals(name)) {
            throw new BusinessException("O papel " + Role.ADMINISTRATOR
                    + " é usado no controle de acesso e não pode ser renomeado");
        }
        ensureNameIsAvailable(name, id);
        Role toSave = new Role(id, name, trim(role.description()), current.createdAt());
        return roles.save(toSave);
    }

    public void deleteById(String id) {
        Role role = findById(id);
        long inUse = users.countByRoleId(id);
        if (inUse > 0) {
            throw new BusinessException("O papel " + role.name() + " está em uso por "
                    + inUse + " usuário(s) e não pode ser excluído");
        }
        roles.deleteById(id);
    }

    private void ensureNameIsAvailable(String name, String currentId) {
        Optional<Role> existing = roles.findByName(name);
        if (existing.isPresent() && !existing.get().id().equals(currentId)) {
            throw new BusinessException("Já existe um papel com o nome " + name);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
