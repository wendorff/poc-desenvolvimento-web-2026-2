package br.edu.ufcspa.sgfh.application.port.out;

import br.edu.ufcspa.sgfh.domain.model.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepositoryPort {

    List<Role> findAll();

    Optional<Role> findById(String id);

    Optional<Role> findByName(String name);

    boolean existsById(String id);

    long count();

    Role save(Role role);

    void deleteById(String id);
}
