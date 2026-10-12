package br.edu.ufcspa.sgfh.testdouble;

import br.edu.ufcspa.sgfh.application.port.out.RoleRepositoryPort;
import br.edu.ufcspa.sgfh.domain.model.Role;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryRoleRepository implements RoleRepositoryPort {

    private final Map<String, Role> storage = new LinkedHashMap<>();

    @Override
    public List<Role> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<Role> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Role> findByName(String name) {
        return storage.values().stream()
                .filter(role -> role.name().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    @Override
    public long count() {
        return storage.size();
    }

    @Override
    public Role save(Role role) {
        String id = role.id() == null ? UUID.randomUUID().toString() : role.id();
        Role stored = role.withId(id);
        storage.put(id, stored);
        return stored;
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }
}
