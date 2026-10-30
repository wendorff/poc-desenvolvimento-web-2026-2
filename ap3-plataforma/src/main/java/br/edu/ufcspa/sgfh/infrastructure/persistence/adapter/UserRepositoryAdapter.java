package br.edu.ufcspa.sgfh.infrastructure.persistence.adapter;

import br.edu.ufcspa.sgfh.application.port.out.UserRepositoryPort;
import br.edu.ufcspa.sgfh.domain.model.User;
import br.edu.ufcspa.sgfh.infrastructure.persistence.mapper.UserDocumentMapper;
import br.edu.ufcspa.sgfh.infrastructure.persistence.repository.UserMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserMongoRepository repository;
    private final UserDocumentMapper mapper;

    public UserRepositoryAdapter(UserMongoRepository repository, UserDocumentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<User> findAll() {
        return repository.findAllByOrderByNameAsc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(mapper::toDomain);
    }

    @Override
    public long countByRoleId(String roleId) {
        return repository.countByRoleId(roleId);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(repository.save(mapper.toDocument(user)));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
