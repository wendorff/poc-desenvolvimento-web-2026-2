package br.edu.ufcspa.sgfh.infrastructure.persistence.repository;

import br.edu.ufcspa.sgfh.infrastructure.persistence.document.RoleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoleMongoRepository extends MongoRepository<RoleDocument, String> {

    Optional<RoleDocument> findByNameIgnoreCase(String name);

    List<RoleDocument> findAllByOrderByNameAsc();
}
