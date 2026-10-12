package br.edu.ufcspa.sgfh.application.port.out;

import br.edu.ufcspa.sgfh.domain.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {

    List<User> findAll();

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    long countByRoleId(String roleId);

    User save(User user);

    void deleteById(String id);
}
