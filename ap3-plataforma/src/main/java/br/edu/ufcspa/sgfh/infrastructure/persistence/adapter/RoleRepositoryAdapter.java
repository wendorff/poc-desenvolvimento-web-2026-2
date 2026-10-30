package br.edu.ufcspa.sgfh.infrastructure.persistence.adapter;

import br.edu.ufcspa.sgfh.application.port.out.RoleRepositoryPort;
import br.edu.ufcspa.sgfh.domain.model.Role;
import br.edu.ufcspa.sgfh.infrastructure.persistence.mapper.RoleDocumentMapper;
import br.edu.ufcspa.sgfh.infrastructure.persistence.repository.RoleMongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleMongoRepository repository;
    private final RoleDocumentMapper mapper;

    public RoleRepositoryAdapter(RoleMongoRepository repository, RoleDocumentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<Role> findAll() {
        return repository.findAllByOrderByNameAsc().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Role> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return repository.findByNameIgnoreCase(name).map(mapper::toDomain);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public Role save(Role role) {
        return mapper.toDomain(repository.save(mapper.toDocument(role)));
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
