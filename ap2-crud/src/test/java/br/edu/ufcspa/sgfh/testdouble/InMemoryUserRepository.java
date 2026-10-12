package br.edu.ufcspa.sgfh.testdouble;

import br.edu.ufcspa.sgfh.application.port.out.UserRepositoryPort;
import br.edu.ufcspa.sgfh.domain.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class InMemoryUserRepository implements UserRepositoryPort {

    private final Map<String, User> storage = new LinkedHashMap<>();

    @Override
    public List<User> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return storage.values().stream()
                .filter(user -> user.email().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public long countByRoleId(String roleId) {
        return storage.values().stream()
                .filter(user -> roleId.equals(user.roleId()))
                .count();
    }

    @Override
    public User save(User user) {
        String id = user.id() == null ? UUID.randomUUID().toString() : user.id();
        User stored = new User(id, user.name(), user.email(), user.passwordHash(),
                user.roleId(), user.active(), user.createdAt());
        storage.put(id, stored);
        return stored;
    }

    @Override
    public void deleteById(String id) {
        storage.remove(id);
    }
}
